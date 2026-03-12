package com.lvwyh.chat.util;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * AES-GCM 工具类
 *
 * 说明：
 * 1. 采用 AES/GCM/NoPadding
 * 2. 每次加密都会生成随机 IV
 * 3. 返回 Base64 编码后的密文和 IV
 */
public class AesGcmUtil {

    /**
     * AES算法
     */
    private static final String AES = "AES";

    /**
     * 加密算法模式
     */
    private static final String AES_GCM_NO_PADDING = "AES/GCM/NoPadding";

    /**
     * GCM 推荐 IV 长度 12 字节
     */
    private static final int IV_LENGTH = 12;

    /**
     * GCM 认证标签长度 128 bit
     */
    private static final int TAG_LENGTH_BIT = 128;

    private AesGcmUtil() {
    }

    /**
     * 加密结果对象
     */
    public static class EncryptResult {

        private String ciphertextBase64;
        private String ivBase64;

        public String getCiphertextBase64() {
            return ciphertextBase64;
        }

        public void setCiphertextBase64(String ciphertextBase64) {
            this.ciphertextBase64 = ciphertextBase64;
        }

        public String getIvBase64() {
            return ivBase64;
        }

        public void setIvBase64(String ivBase64) {
            this.ivBase64 = ivBase64;
        }
    }

    /**
     * 文本加密
     *
     * @param plaintext 明文
     * @param key       AES密钥字符串
     * @return 加密结果
     */
    public static EncryptResult encrypt(String plaintext, String key) {
        try {
            byte[] keyBytes = buildAesKey(key);
            byte[] iv = new byte[IV_LENGTH];
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(AES_GCM_NO_PADDING);
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, AES);
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, gcmParameterSpec);

            byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

            EncryptResult result = new EncryptResult();
            result.setCiphertextBase64(Base64.getEncoder().encodeToString(encryptedBytes));
            result.setIvBase64(Base64.getEncoder().encodeToString(iv));
            return result;
        } catch (Exception e) {
            throw new RuntimeException("消息加密失败", e);
        }
    }

    /**
     * 文本解密
     *
     * @param ciphertextBase64 密文Base64
     * @param ivBase64         IV的Base64
     * @param key              AES密钥字符串
     * @return 明文
     */
    public static String decrypt(String ciphertextBase64, String ivBase64, String key) {
        try {
            byte[] keyBytes = buildAesKey(key);
            byte[] encryptedBytes = Base64.getDecoder().decode(ciphertextBase64);
            byte[] iv = Base64.getDecoder().decode(ivBase64);

            Cipher cipher = Cipher.getInstance(AES_GCM_NO_PADDING);
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, AES);
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, gcmParameterSpec);

            byte[] plainBytes = cipher.doFinal(encryptedBytes);
            return new String(plainBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("消息解密失败", e);
        }
    }

    /**
     * 构造 AES 密钥
     *
     * 说明：
     * 1. 这里要求 key 长度至少 16 字节
     * 2. 截取前 16 字节作为 AES-128 key
     *
     * @param key 原始密钥字符串
     * @return 16字节密钥
     */
    private static byte[] buildAesKey(String key) {
        if (key == null) {
            throw new IllegalArgumentException("AES密钥不能为空");
        }

        byte[] source = key.getBytes(StandardCharsets.UTF_8);
        if (source.length < 16) {
            throw new IllegalArgumentException("AES密钥长度不能少于16字节");
        }

        byte[] result = new byte[16];
        System.arraycopy(source, 0, result, 0, 16);
        return result;
    }
}