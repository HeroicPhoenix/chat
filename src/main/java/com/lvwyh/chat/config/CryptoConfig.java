package com.lvwyh.chat.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 加密配置
 */
@Configuration
public class CryptoConfig {

    /**
     * AES密钥
     */
    @Value("${chat.crypto.aes-key}")
    private String aesKey;

    /**
     * 加密版本号
     */
    @Value("${chat.crypto.version:v1}")
    private String version;

    public String getAesKey() {
        return aesKey;
    }

    public String getVersion() {
        return version;
    }
}