package com.lvwyh.chat.mapper;

import com.lvwyh.chat.entity.ChatMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 消息 Mapper
 */
@Mapper
public interface ChatMessageMapper {

    /**
     * 新增消息
     *
     * @param message 消息对象
     * @return 影响行数
     */
    int insert(ChatMessage message);

    /**
     * 查询某会话最近 N 条消息
     *
     * @param conversationId 会话ID
     * @param limit          数量限制
     * @return 消息列表（按时间倒序）
     */
    List<ChatMessage> selectRecentMessages(@Param("conversationId") Long conversationId,
                                           @Param("limit") Integer limit);
}