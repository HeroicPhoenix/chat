-- 创建数据库
-- 说明：
-- 1. 数据库名为 chat
-- 2. 使用 utf8mb4，便于支持 emoji、特殊字符、多语言内容
-- 3. 排序规则使用 utf8mb4_general_ci，兼容性较好
CREATE DATABASE IF NOT EXISTS chat
DEFAULT CHARACTER SET utf8mb4
DEFAULT COLLATE utf8mb4_general_ci;

-- 切换到 chat 数据库
USE chat;

DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
`id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户主键ID',
`username` varchar(64) NOT NULL COMMENT '用户名，唯一，用于登录',
`password_hash` varchar(255) NOT NULL COMMENT '密码哈希值，推荐使用 BCrypt',
`nickname` varchar(64) DEFAULT NULL COMMENT '用户昵称，展示用',
`status` tinyint(4) NOT NULL DEFAULT 1 COMMENT '用户状态：1-正常，0-禁用',
`created_at` datetime NOT NULL DEFAULT current_timestamp() COMMENT '创建时间',
`updated_at` datetime NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新时间',
PRIMARY KEY (`id`),
UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户表';

DROP TABLE IF EXISTS `chat_message`;
CREATE TABLE `chat_message` (
`id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '消息主键ID',
`conversation_id` bigint(20) NOT NULL COMMENT '所属会话ID',
`sender_id` bigint(20) NOT NULL COMMENT '发送人用户ID',
`message_type` varchar(20) NOT NULL COMMENT '消息类型：TEXT/FILE/IMAGE/VIDEO',
`content_ciphertext` longtext DEFAULT NULL COMMENT '消息密文内容，文本消息时必填，建议存 Base64 密文',
`content_iv` varchar(64) DEFAULT NULL COMMENT '文本消息加密使用的IV，建议存 Base64 编码值',
`content_version` varchar(16) DEFAULT NULL COMMENT '加密版本号，例如 v1，用于密钥轮换',
`created_at` datetime NOT NULL DEFAULT current_timestamp() COMMENT '消息发送时间',
`deleted` tinyint(4) NOT NULL DEFAULT 0 COMMENT '逻辑删除标志：0-未删除，1-已删除',
PRIMARY KEY (`id`),
KEY `idx_conv_time` (`conversation_id`,`created_at`),
KEY `idx_sender_id` (`sender_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='聊天消息表';

DROP TABLE IF EXISTS `chat_conversation_member`;
CREATE TABLE `chat_conversation_member` (
            `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '会话成员记录主键ID',
            `conversation_id` bigint(20) NOT NULL COMMENT '所属会话ID',
            `user_id` bigint(20) NOT NULL COMMENT '成员用户ID',
            `joined_at` datetime NOT NULL DEFAULT current_timestamp() COMMENT '加入时间',
            PRIMARY KEY (`id`),
            UNIQUE KEY `uk_conversation_user` (`conversation_id`,`user_id`),
            KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='会话成员表';

DROP TABLE IF EXISTS `chat_conversation`;
CREATE TABLE `chat_conversation` (
     `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '会话主键ID',
     `name` varchar(128) DEFAULT NULL COMMENT '会话名称，单聊可为空，群聊可设置名称',
     `created_by` bigint(20) NOT NULL COMMENT '创建人用户ID',
     `created_at` datetime NOT NULL DEFAULT current_timestamp() COMMENT '创建时间',
     PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='聊天会话表';

DROP TABLE IF EXISTS `chat_attachment`;
CREATE TABLE `chat_attachment` (
   `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '附件主键ID',
   `message_id` bigint(20) NOT NULL COMMENT '所属消息ID',
   `stored_name` varchar(128) NOT NULL COMMENT 'NAS上实际存储文件名，建议使用UUID',
   `original_name_ciphertext` longtext NOT NULL COMMENT '原始文件名密文',
   `original_name_iv` varchar(64) DEFAULT NULL COMMENT '原始文件名加密IV',
   `file_path_ciphertext` longtext NOT NULL COMMENT '文件存储路径密文',
   `file_path_iv` varchar(64) DEFAULT NULL COMMENT '文件路径加密IV',
   `mime_type` varchar(128) DEFAULT NULL COMMENT '文件MIME类型，例如 image/png、video/mp4',
   `file_size` bigint(20) NOT NULL COMMENT '文件大小，单位字节',
   `file_iv` varchar(64) DEFAULT NULL COMMENT '附件内容加密使用的IV，建议存 Base64 编码值',
   `file_sha256` varchar(128) DEFAULT NULL COMMENT '文件SHA-256摘要，用于完整性校验',
   `created_at` datetime NOT NULL DEFAULT current_timestamp() COMMENT '创建时间',
   PRIMARY KEY (`id`),
   KEY `idx_message_id` (`message_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='聊天附件表';