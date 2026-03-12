package com.lvwyh.chat.entity;

import java.util.Date;

/**
 * 聊天消息实体
 */
public class ChatMessage {

    /**
     * 消息ID
     */
    private Long id;

    /**
     * 会话ID
     */
    private Long conversationId;

    /**
     * 发送人ID
     */
    private Long senderId;

    /**
     * 消息类型：TEXT / FILE / IMAGE / VIDEO
     */
    private String messageType;

    /**
     * 消息密文内容
     */
    private String contentCiphertext;

    /**
     * 加密使用的IV，Base64字符串
     */
    private String contentIv;

    /**
     * 加密版本号
     */
    private String contentVersion;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 逻辑删除标志：0-未删除，1-已删除
     */
    private Integer deleted;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getConversationId() {
        return conversationId;
    }

    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getContentCiphertext() {
        return contentCiphertext;
    }

    public void setContentCiphertext(String contentCiphertext) {
        this.contentCiphertext = contentCiphertext;
    }

    public String getContentIv() {
        return contentIv;
    }

    public void setContentIv(String contentIv) {
        this.contentIv = contentIv;
    }

    public String getContentVersion() {
        return contentVersion;
    }

    public void setContentVersion(String contentVersion) {
        this.contentVersion = contentVersion;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }
}