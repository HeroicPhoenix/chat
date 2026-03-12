package com.lvwyh.chat.entity;

import java.util.Date;

/**
 * 会话成员实体
 *
 * 对应数据库表：chat_conversation_member
 *
 * 作用：
 * 1. 记录某个用户是否属于某个会话
 * 2. 控制谁可以看到该会话
 * 3. 控制谁可以发送消息到该会话
 */
public class ChatConversationMember {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 会话ID
     * 对应 chat_conversation.id
     */
    private Long conversationId;

    /**
     * 用户ID
     * 对应 sys_user.id
     */
    private Long userId;

    /**
     * 加入时间
     */
    private Date joinedAt;

    public ChatConversationMember() {
    }

    public ChatConversationMember(Long conversationId, Long userId) {
        this.conversationId = conversationId;
        this.userId = userId;
    }

    /**
     * 获取主键ID
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置主键ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取会话ID
     */
    public Long getConversationId() {
        return conversationId;
    }

    /**
     * 设置会话ID
     */
    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }

    /**
     * 获取用户ID
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * 设置用户ID
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * 获取加入时间
     */
    public Date getJoinedAt() {
        return joinedAt;
    }

    /**
     * 设置加入时间
     */
    public void setJoinedAt(Date joinedAt) {
        this.joinedAt = joinedAt;
    }
}