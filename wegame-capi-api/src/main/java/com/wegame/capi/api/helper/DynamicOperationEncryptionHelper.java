package com.wegame.capi.api.helper;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.capi.infrastructure.config.properties.GameApiProperty;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JceOpenSSLPKCS8DecryptorProviderBuilder;
import org.bouncycastle.operator.InputDecryptorProvider;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.Security;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class DynamicOperationEncryptionHelper {
    private static final String RSA_ALGORITHM = "RSA/ECB/OAEPWITHSHA-1ANDMGF1PADDING";
    private static final String AES_ALGORITHM = "AES/CBC/PKCS5Padding";

    @Autowired
    GameApiProperty gameApiProperty;

    private Map<String, AesSecretKey> aesSecretmap = new HashMap<>();

    /**
     * 启动时初始化 RSA 公钥、AES 密钥和 IV
     */
    @PostConstruct
    public void init() {
        try {
            List<GameApiProperty.PrivateInfo> privateInfoList = gameApiProperty.supportDosPrivateInfoList();
            for (GameApiProperty.PrivateInfo info : privateInfoList) {
                if (!info.isDosSupport()) {
                    continue;
                }
                byte[] key = hexStringToByteArray(info.getPrivateKey());
                byte[] iv = hexStringToByteArray(info.getPrivateIv());
                SecretKeySpec aesSecretKey = new SecretKeySpec(key, "AES");
                IvParameterSpec aesIvParameterSpec = new IvParameterSpec(iv);
                AesSecretKey secretKey = AesSecretKey.builder()
                        .aesSecretKey(aesSecretKey)
                        .aesIvParameterSpec(aesIvParameterSpec)
                        .build();
                // 加载rsa
                if (StringUtils.isNotEmpty(info.getDosPublicKey()) && StringUtils.isNotEmpty(info.getDosPrivateKey())) {
                    Security.addProvider(new BouncyCastleProvider());
                    RSAPublicKey rsaPublicKey = this.loadPublicKey(info.getDosPublicKey());
                    RSAPrivateKey rsaPrivateKey = this.loadPrivateKey(info.getDosPrivateKey(), info.getDosPassword());
                    secretKey.setRsaPublicKey(rsaPublicKey);
                    secretKey.setRsaPrivateKey(rsaPrivateKey);
                }
                aesSecretmap.put(info.getChannelId(), secretKey);
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize encryption keys", e);
        }
    }

    /**
     * 使用 RSA 公钥加密
     */
    public String encryptWithPublicKey(String channelId, String message) {
        try {
            AesSecretKey aesSecretKey = aesSecretmap.get(channelId);
            // log.info("encryptWithPublicKey,aesSecretKey.getRsaPublicKey(),channelId:{},publicKey:{},message:{}",channelId,aesSecretKey.getRsaPublicKey(),message);
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, aesSecretKey.getRsaPublicKey());
            byte[] encryptedBytes = cipher.doFinal(message.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to initialize RSA keys", e);
        }
    }

    /**
     * 解密数据
     */
    public String decrypt(String channelId, String encryptedData) {
        try {
            AesSecretKey aesSecretKey = aesSecretmap.get(channelId);
            // log.info("encryptWithPublicKey,decrypt.getRsaPrivateKey(),channelId:{},privateKey:{},encryptedData:{}",channelId,aesSecretKey.getRsaPrivateKey(),encryptedData);
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, aesSecretKey.getRsaPrivateKey());
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Throwable e) {
            throw new IllegalStateException("DynamicOperationCallBackRsaHelper decrypt error", e);
        }

    }

    /**
     * 使用 AES-256-CBC 加密
     */
    public String encryptWithAES256CBC(String channelId, String plaintext) {
        try {
            AesSecretKey aesSecret = aesSecretmap.get(channelId);
            if (aesSecret == null) {
                throw new BusinessException(ErrorCode.ERR_INTER);
            }
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);

            cipher.init(Cipher.ENCRYPT_MODE, aesSecret.aesSecretKey, aesSecret.aesIvParameterSpec);
            byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
            // 转换为 HEX
            StringBuilder encrypted = new StringBuilder();
            for (byte b : encryptedBytes) {
                encrypted.append(String.format("%02x", b));
            }
            return encrypted.toString();
        } catch (Exception e) {
            throw new RuntimeException("AES encryption failed", e);
        }
    }


    /**
     * 使用 AES-256-CBC 解密
     */
    public String decryptWithAES256CBC(String encryptedHex, String privateKey, String ivKey) {
        try {

            // 初始化 AES 密钥和 IV
            byte[] key = hexStringToByteArray(privateKey);
            byte[] iv = hexStringToByteArray(ivKey);
            SecretKeySpec aesSecretKey = new SecretKeySpec(key, "AES");
            IvParameterSpec aesIvParameterSpec = new IvParameterSpec(iv);

            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, aesSecretKey, aesIvParameterSpec);

            // 将 HEX 转换回字节数组
            byte[] encryptedBytes = hexStringToByteArray(encryptedHex);

            // 执行 AES 解密
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

            // 返回解密后的字符串
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("AES decryption failed", e);
        }
    }


    private byte[] hexStringToByteArray(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }


    private RSAPublicKey loadPublicKey(String publicKeyStr) throws Exception {
        PEMParser pemParser = new PEMParser(new StringReader(publicKeyStr));
        Object object = pemParser.readObject();
        pemParser.close();
        if (object instanceof SubjectPublicKeyInfo) {
            SubjectPublicKeyInfo publicKeyInfo = (SubjectPublicKeyInfo) object;
            return (RSAPublicKey) (new JcaPEMKeyConverter().getPublicKey(publicKeyInfo));
        } else {
            throw new IllegalArgumentException("Unsupported public key format");
        }
    }

    private RSAPrivateKey loadPrivateKey(String privateKeyStr, String password) throws Exception {
        PEMParser pemParser = new PEMParser(new StringReader(privateKeyStr));
        Object object = pemParser.readObject();
        pemParser.close();

        // 如果私钥需要密码进行解密，使用 PKCS8EncryptedPrivateKeyInfo
        if (object instanceof PKCS8EncryptedPrivateKeyInfo) {
            PKCS8EncryptedPrivateKeyInfo encryptedKeyInfo = (PKCS8EncryptedPrivateKeyInfo) object;
            // 提供密码解密
            char[] passwords = password.toCharArray();

            InputDecryptorProvider decryptorProvider = new JceOpenSSLPKCS8DecryptorProviderBuilder()
                    .setProvider("BC")
                    .build(passwords);

            JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");
            PrivateKey privateKey = converter.getPrivateKey(encryptedKeyInfo.decryptPrivateKeyInfo(decryptorProvider));

            return (RSAPrivateKey) privateKey;
        } else {
            throw new IllegalArgumentException("Unsupported private key format");
        }
    }

    @Data
    @Builder
    static class AesSecretKey {
        private SecretKeySpec aesSecretKey;
        private IvParameterSpec aesIvParameterSpec;
        private RSAPublicKey rsaPublicKey;
        private RSAPrivateKey rsaPrivateKey;
    }
}
