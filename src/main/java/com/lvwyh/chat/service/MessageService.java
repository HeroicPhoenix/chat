package com.lvwyh.chat.service;

import com.lvwyh.chat.ao.SendTextMessageAO;
import com.lvwyh.chat.vo.FileMessageVO;
import com.lvwyh.chat.vo.MessageVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MessageService {

    MessageVO sendTextMessage(Long currentUserId, SendTextMessageAO ao);

    List<MessageVO> listRecentMessages(Long currentUserId, Long conversationId, Integer limit);

    FileMessageVO sendFileMessage(Long currentUserId, Long conversationId, MultipartFile file);
}