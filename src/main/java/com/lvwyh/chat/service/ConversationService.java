package com.lvwyh.chat.service;

import com.lvwyh.chat.ao.CreateConversationAO;
import com.lvwyh.chat.vo.ConversationVO;

import java.util.List;

/**
 * 会话服务
 */
public interface ConversationService {

    /**
     * 创建会话
     *
     * @param currentUserId 当前登录用户ID
     * @param ao 创建参数
     * @return 会话信息
     */
    ConversationVO createConversation(Long currentUserId, CreateConversationAO ao);

    /**
     * 查询我的会话列表
     *
     * @param currentUserId 当前登录用户ID
     * @return 会话列表
     */
    List<ConversationVO> listMyConversations(Long currentUserId);
}