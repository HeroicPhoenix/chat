package com.lvwyh.chat.controller;

import com.lvwyh.chat.ao.SendTextMessageAO;
import com.lvwyh.chat.common.ApiResponse;
import com.lvwyh.chat.service.MessageService;
import com.lvwyh.chat.util.SecurityUtil;
import com.lvwyh.chat.vo.FileMessageVO;
import com.lvwyh.chat.vo.MessageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * 消息控制器
 */
@RestController
@RequestMapping("/api/message")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @Operation(summary = "发送文本消息")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/text/send")
    public ApiResponse<MessageVO> sendText(@Valid @RequestBody SendTextMessageAO ao) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        MessageVO vo = messageService.sendTextMessage(currentUserId, ao);
        return ApiResponse.success("发送成功", vo);
    }

    @Operation(summary = "查询最近N条消息")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/recent")
    public ApiResponse<List<MessageVO>> recent(@RequestParam Long conversationId,
                                               @RequestParam(defaultValue = "20") Integer limit) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        List<MessageVO> list = messageService.listRecentMessages(currentUserId, conversationId, limit);
        return ApiResponse.success(list);
    }

    @Operation(summary = "发送附件消息")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/file/send")
    public ApiResponse<FileMessageVO> sendFile(@RequestParam Long conversationId,
                                               @RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        FileMessageVO vo = messageService.sendFileMessage(currentUserId, conversationId, file);
        return ApiResponse.success("发送成功", vo);
    }
}