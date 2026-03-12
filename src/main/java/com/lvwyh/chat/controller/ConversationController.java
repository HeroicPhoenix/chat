package com.lvwyh.chat.controller;

import com.lvwyh.chat.ao.CreateConversationAO;
import com.lvwyh.chat.common.ApiResponse;
import com.lvwyh.chat.service.ConversationService;
import com.lvwyh.chat.util.SecurityUtil;
import com.lvwyh.chat.vo.ConversationVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 会话控制器
 */
@RestController
@RequestMapping("/api/conversation")
public class ConversationController {

    private final ConversationService conversationService;

    public ConversationController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @Operation(summary = "创建会话", description = "创建一个新的可见范围会话")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/create")
    public ApiResponse<ConversationVO> create(@Valid @RequestBody CreateConversationAO ao) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        ConversationVO vo = conversationService.createConversation(currentUserId, ao);
        return ApiResponse.success("创建成功", vo);
    }

    @Operation(summary = "查询我的会话列表")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/my")
    public ApiResponse<List<ConversationVO>> myList() {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        List<ConversationVO> list = conversationService.listMyConversations(currentUserId);
        return ApiResponse.success(list);
    }
}