package com.lvwyh.chat.mapper;

import com.lvwyh.chat.entity.ChatConversation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 会话 Mapper
 */
@Mapper
public interface ChatConversationMapper {

    /**
     * 新增会话
     *
     * @param conversation 会话对象
     * @return 影响行数
     */
    int insert(ChatConversation conversation);

    /**
     * 根据ID查询会话
     *
     * @param id 会话ID
     * @return 会话
     */
    ChatConversation selectById(@Param("id") Long id);

    /**
     * 查询某个用户可见的会话列表
     *
     * @param userId 用户ID
     * @return 会话列表
     */
    List<ChatConversation> selectByUserId(@Param("userId") Long userId);
}