package com.lvwyh.chat.util;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 文件加密工具类
 */
public class FileCryptoUtil {

    private static final String AES = "AES";
    private static final String AES_GCM_NO_PADDING = "AES/GCM/NoPadding";
    private static final int IV_LENGTH = 12;
    private static final int TAG_LENGTH_BIT = 128;

    private FileCryptoUtil() {
    }

    public static class EncryptFileResult {
        private byte[] encryptedBytes;
        private String ivBase64;

        public byte[] getEncryptedBytes() {
            return encryptedBytes;
        }

        public String getIvBase64() {
            return ivBase64;
        }

        public void setEncryptedBytes(byte[] encryptedBytes) {
            this.encryptedBytes = encryptedBytes;
        }

        public void setIvBase64(String ivBase64) {
            this.ivBase64 = ivBase64;
        }
    }

    public static EncryptFileResult encrypt(byte[] fileBytes, String key) {
        try {
            byte[] keyBytes = buildAesKey(key);
            byte[] iv = new byte[IV_LENGTH];
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(AES_GCM_NO_PADDING);
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, AES);
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, gcmParameterSpec);

            byte[] encryptedBytes = cipher.doFinal(fileBytes);

            EncryptFileResult result = new EncryptFileResult();
            result.setEncryptedBytes(encryptedBytes);
            result.setIvBase64(Base64.getEncoder().encodeToString(iv));
            return result;
        } catch (Exception e) {
            throw new RuntimeException("文件加密失败", e);
        }
    }

    public static byte[] decrypt(byte[] encryptedBytes, String ivBase64, String key) {
        try {
            byte[] keyBytes = buildAesKey(key);
            byte[] iv = Base64.getDecoder().decode(ivBase64);

            Cipher cipher = Cipher.getInstance(AES_GCM_NO_PADDING);
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, AES);
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, gcmParameterSpec);

            return cipher.doFinal(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("文件解密失败", e);
        }
    }

    private static byte[] buildAesKey(String key) {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("AES密钥不能为空，请检查配置 chat.crypto.aes-key");
        }
        byte[] source = key.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        if (source.length < 16) {
            throw new IllegalArgumentException("AES密钥长度不能少于16字节，请检查配置 chat.crypto.aes-key");
        }
        byte[] result = new byte[16];
        System.arraycopy(source, 0, result, 0, 16);
        return result;
    }
}