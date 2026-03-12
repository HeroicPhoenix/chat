package com.lvwyh.chat.mapper;

import com.lvwyh.chat.entity.ChatConversationMember;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 会话成员 Mapper
 */
@Mapper
public interface ChatConversationMemberMapper {

    /**
     * 批量新增成员
     *
     * @param members 成员列表
     * @return 影响行数
     */
    int batchInsert(@Param("members") List<ChatConversationMember> members);

    /**
     * 根据会话ID查询成员ID列表
     *
     * @param conversationId 会话ID
     * @return 成员ID列表
     */
    List<Long> selectUserIdsByConversationId(@Param("conversationId") Long conversationId);

    /**
     * 判断用户是否属于某会话
     *
     * @param conversationId 会话ID
     * @param userId 用户ID
     * @return 数量
     */
    int countByConversationIdAndUserId(@Param("conversationId") Long conversationId,
                                       @Param("userId") Long userId);
}