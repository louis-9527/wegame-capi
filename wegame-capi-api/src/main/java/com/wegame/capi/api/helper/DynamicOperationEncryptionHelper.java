package com.wegame.capi.api.helper;

import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JceOpenSSLPKCS8DecryptorProviderBuilder;
import org.bouncycastle.operator.InputDecryptorProvider;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import org.springframework.beans.factory.annotation.Value;
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

@Component
public class DynamicOperationEncryptionHelper {
    private static final String RSA_ALGORITHM = "RSA/ECB/OAEPWITHSHA-1ANDMGF1PADDING";
    private static final String AES_ALGORITHM = "AES/CBC/PKCS5Padding";

    @Value("${api.game.cp.privateKey}")
    String cpPrivateKey;
    @Value("${api.game.cp.privateIv}")
    String cpPrivateIv;
    @Value("${dynamic.operation.rsa.public_key}")
    private String publicKeyStr;
    @Value("${dynamic.operation.rsa.private_key}")
    private String privateKeyStr;
    @Value("${dynamic.operation.rsa.password}")
    private String privateKeyPassword;

    private RSAPublicKey rsaPublicKey;
    private RSAPrivateKey privateKey;
    private SecretKeySpec aesSecretKey;
    private IvParameterSpec aesIvParameterSpec;

    /**
     * 启动时初始化 RSA 公钥、AES 密钥和 IV
     */
    @PostConstruct
    public void init() {
        try {
            Security.addProvider(new BouncyCastleProvider());
            this.rsaPublicKey = loadPublicKey(publicKeyStr);
            this.privateKey = loadPrivateKey(privateKeyStr, privateKeyPassword);
            // 初始化 AES 密钥和 IV
            byte[] key = hexStringToByteArray(cpPrivateKey);
            byte[] iv = hexStringToByteArray(cpPrivateIv);
            this.aesSecretKey = new SecretKeySpec(key, "AES");
            this.aesIvParameterSpec = new IvParameterSpec(iv);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize encryption keys", e);
        }
    }

    /**
     * 使用 RSA 公钥加密
     */
    public String encryptWithPublicKey(String message) {
        try {
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, rsaPublicKey);
            byte[] encryptedBytes = cipher.doFinal(message.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to initialize RSA keys", e);
        }
    }

    /**
     * 解密数据
     */
    public String decrypt(String encryptedData) {
        try {
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Throwable e) {
            throw new IllegalStateException("DynamicOperationCallBackRsaHelper decrypt error", e);
        }

    }

    /**
     * 使用 AES-256-CBC 加密
     */
    public String encryptWithAES256CBC(String plaintext) {
        try {
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, aesSecretKey, aesIvParameterSpec);
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

    /**
     * 使用 AES-256-CBC 解密
     */
    public String decryptWithAES256CBC(String encryptedHex) {
        try {
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


    private RSAPublicKey loadPublicKey(String key) throws Exception {
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

    private RSAPrivateKey loadPrivateKey(String key, String password) throws Exception {
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
}
