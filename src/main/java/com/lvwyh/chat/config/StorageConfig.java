package com.lvwyh.chat.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 存储配置
 */
@Configuration
public class StorageConfig {

    @Value("${chat.storage.base-path}")
    private String basePath;

    public String getBasePath() {
        return basePath;
    }
}