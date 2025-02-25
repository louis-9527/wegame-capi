package com.wegame.capi.api.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
public class DynamicOperationEncryptionUtil {
    private RSAPublicKey rsaPublicKey;
    private SecretKeySpec aesSecretKey;
    private IvParameterSpec aesIvParameterSpec;

    private static final String RSA_ALGORITHM = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";
    private static final String AES_ALGORITHM = "AES/CBC/PKCS5Padding";

    @Value("${api.sport.cp.privateKey}")
    String cpPrivateKey;
    @Value("${api.sport.cp.privateIv}")
    String cpPrivateIv;
    @Value("${dynamic.operation.public_key}")
    String publicKeyStr;


    /**
     * 启动时初始化 RSA 公钥、AES 密钥和 IV
     */
    @PostConstruct
    public void init() {
        try {
            // 初始化 RSA 公钥
            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyStr);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            this.rsaPublicKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);
            // 初始化 AES 密钥和 IV
            byte[] key = Base64.getDecoder().decode(cpPrivateKey);
            byte[] iv = Base64.getDecoder().decode(cpPrivateIv);
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
            throw new RuntimeException("RSA encryption failed", e);
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
}
