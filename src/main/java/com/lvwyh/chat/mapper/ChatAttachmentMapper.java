package com.lvwyh.chat.mapper;

import com.lvwyh.chat.entity.ChatAttachment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ChatAttachmentMapper {

    int insert(ChatAttachment attachment);

    ChatAttachment selectByMessageId(@Param("messageId") Long messageId);

    ChatAttachment selectById(@Param("id") Long id);
}