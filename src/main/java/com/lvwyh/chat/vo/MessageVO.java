package com.lvwyh.chat.vo;

import java.util.Date;

/**
 * 消息返回对象
 */
public class MessageVO {

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
     * 发送人用户名
     */
    private String senderUsername;

    /**
     * 发送人昵称
     */
    private String senderNickname;

    /**
     * 消息类型
     */
    private String messageType;

    /**
     * 解密后的消息内容
     */
    private String content;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 附件信息
     */
    private AttachmentVO attachment;

    public Long getId() {
        return id;
    }

    public Long getConversationId() {
        return conversationId;
    }

    public Long getSenderId() {
        return senderId;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public String getSenderNickname() {
        return senderNickname;
    }

    public String getMessageType() {
        return messageType;
    }

    public String getContent() {
        return content;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }

    public void setSenderNickname(String senderNickname) {
        this.senderNickname = senderNickname;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public AttachmentVO getAttachment() {
        return attachment;
    }

    public void setAttachment(AttachmentVO attachment) {
        this.attachment = attachment;
    }
}