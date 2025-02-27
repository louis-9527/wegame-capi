package com.wegame.capi.api.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
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

    private   byte[] hexStringToByteArray(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }

    public static String encryptWithPublicKey(String publicKeyStr, String message) throws Exception {
        // Convert the public key string into a PublicKey object
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyStr);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        RSAPublicKey publicKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);

        // Create Cipher object for RSA encryption with OAEP padding
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        // Encrypt the message
        byte[] encryptedBytes = cipher.doFinal(message.getBytes());

        // Return the encrypted message as a base64 encoded string
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static void main(String[] args) throws Exception {

        String publicKeyPEM =
               "-----BEGIN PUBLIC KEY-----\n" +
                       "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAuFfxoFw0IRd2QFopOOx2\n" +
                       "6udxe0DaSkVdqT64AZCZFdNbT1dh9TldrePcRMNtzEhZFLjx+v3Yn4T91vTkl4iU\n" +
                       "EHQiHsNTARh2OYuhaS/xzqiqcb4uX88Nuj8C++sNDcVI7sSb8ihRm3EBVWiBFgVm\n" +
                       "M88osuMspLaMhD/VKqafotmOyjI1X5fJcaiYXY6Zqwlpul3Opg2/Q6wmcO27JyJS\n" +
                       "gMUs2hRz3JDiW1RVjBlLSyPq6/K3wbUTXZBTVgGeRsEWrcKe2Qg9JOdqtzGoBg2u\n" +
                       "XU2miEtKban8PAcYtCn33r2BTA+hR8ey53SQjnJp7JHmdDPwvn4TKuRya1yTwE/y\n" +
                       "CQIDAQAB\n" +
                       "-----END PUBLIC KEY-----";
        // 3. 去掉公钥字符串中的头尾信息，并进行 Base64 解码
        String publicKey = publicKeyPEM.replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        String ss = encryptWithPublicKey(publicKey, "da0e808e659078811ebfdc2f14375c124fa047906ca53bc7aba99632372c29af");
        String dd = encryptWithPublicKey(publicKey, "8aa403321950bfa49b9cc13f31a0558f");
        System.out.println(ss);
        System.out.println(dd);

    }

}
