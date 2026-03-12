package com.lvwyh.chat.service.impl;

import com.lvwyh.chat.ao.CreateConversationAO;
import com.lvwyh.chat.common.BusinessException;
import com.lvwyh.chat.entity.ChatConversation;
import com.lvwyh.chat.entity.ChatConversationMember;
import com.lvwyh.chat.entity.SysUser;
import com.lvwyh.chat.mapper.ChatConversationMapper;
import com.lvwyh.chat.mapper.ChatConversationMemberMapper;
import com.lvwyh.chat.mapper.SysUserMapper;
import com.lvwyh.chat.service.ConversationService;
import com.lvwyh.chat.vo.ConversationVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 会话服务实现
 */
@Service
public class ConversationServiceImpl implements ConversationService {

    private final ChatConversationMapper chatConversationMapper;
    private final ChatConversationMemberMapper chatConversationMemberMapper;
    private final SysUserMapper sysUserMapper;

    public ConversationServiceImpl(ChatConversationMapper chatConversationMapper,
                                   ChatConversationMemberMapper chatConversationMemberMapper,
                                   SysUserMapper sysUserMapper) {
        this.chatConversationMapper = chatConversationMapper;
        this.chatConversationMemberMapper = chatConversationMemberMapper;
        this.sysUserMapper = sysUserMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ConversationVO createConversation(Long currentUserId, CreateConversationAO ao) {
        if (currentUserId == null) {
            throw new BusinessException(401, "用户未登录");
        }
        if (ao == null) {
            throw new BusinessException("创建会话参数不能为空");
        }

        Set<Long> memberIdSet = new LinkedHashSet<Long>();

        // 当前用户自己必须在会话里
        memberIdSet.add(currentUserId);

        if (!CollectionUtils.isEmpty(ao.getMemberIds())) {
            for (Long memberId : ao.getMemberIds()) {
                if (memberId == null) {
                    continue;
                }
                memberIdSet.add(memberId);
            }
        }

        // 校验成员是否存在
        for (Long memberId : memberIdSet) {
            SysUser user = sysUserMapper.selectById(memberId);
            if (user == null) {
                throw new BusinessException("成员不存在，userId=" + memberId);
            }
            if (user.getStatus() == null || user.getStatus() != 1) {
                throw new BusinessException("成员不可用，userId=" + memberId);
            }
        }

        ChatConversation conversation = new ChatConversation();
        conversation.setName(ao.getName());
        conversation.setCreatedBy(currentUserId);

        int rows = chatConversationMapper.insert(conversation);
        if (rows <= 0 || conversation.getId() == null) {
            throw new BusinessException("创建会话失败");
        }

        List<ChatConversationMember> members = new ArrayList<ChatConversationMember>();
        for (Long memberId : memberIdSet) {
            ChatConversationMember member = new ChatConversationMember();
            member.setConversationId(conversation.getId());
            member.setUserId(memberId);
            members.add(member);
        }

        if (!members.isEmpty()) {
            chatConversationMemberMapper.batchInsert(members);
        }

        ConversationVO vo = new ConversationVO();
        vo.setId(conversation.getId());
        vo.setName(conversation.getName());
        vo.setCreatedBy(conversation.getCreatedBy());
        vo.setCreatedAt(conversation.getCreatedAt());
        vo.setMemberIds(new ArrayList<Long>(memberIdSet));

        return vo;
    }

    @Override
    public List<ConversationVO> listMyConversations(Long currentUserId) {
        if (currentUserId == null) {
            throw new BusinessException(401, "用户未登录");
        }

        List<ChatConversation> conversations = chatConversationMapper.selectByUserId(currentUserId);
        List<ConversationVO> result = new ArrayList<ConversationVO>();

        if (CollectionUtils.isEmpty(conversations)) {
            return result;
        }

        for (ChatConversation conversation : conversations) {
            ConversationVO vo = new ConversationVO();
            vo.setId(conversation.getId());
            vo.setName(conversation.getName());
            vo.setCreatedBy(conversation.getCreatedBy());
            vo.setCreatedAt(conversation.getCreatedAt());
            vo.setMemberIds(
                    chatConversationMemberMapper.selectUserIdsByConversationId(conversation.getId())
            );
            result.add(vo);
        }

        return result;
    }
}