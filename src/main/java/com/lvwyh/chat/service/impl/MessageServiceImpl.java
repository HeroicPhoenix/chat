package com.lvwyh.chat.service.impl;

import com.lvwyh.chat.ao.SendTextMessageAO;
import com.lvwyh.chat.common.BusinessException;
import com.lvwyh.chat.config.CryptoConfig;
import com.lvwyh.chat.config.StorageConfig;
import com.lvwyh.chat.entity.ChatAttachment;
import com.lvwyh.chat.entity.ChatConversation;
import com.lvwyh.chat.entity.ChatMessage;
import com.lvwyh.chat.entity.SysUser;
import com.lvwyh.chat.mapper.ChatAttachmentMapper;
import com.lvwyh.chat.mapper.ChatConversationMapper;
import com.lvwyh.chat.mapper.ChatConversationMemberMapper;
import com.lvwyh.chat.mapper.ChatMessageMapper;
import com.lvwyh.chat.mapper.SysUserMapper;
import com.lvwyh.chat.service.MessageService;
import com.lvwyh.chat.util.AesGcmUtil;
import com.lvwyh.chat.util.DigestUtil;
import com.lvwyh.chat.util.FileCryptoUtil;
import com.lvwyh.chat.vo.AttachmentVO;
import com.lvwyh.chat.vo.FileMessageVO;
import com.lvwyh.chat.vo.MessageVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * 消息服务实现
 */
@Service
public class MessageServiceImpl implements MessageService {

    /**
     * 单文件最大限制：100MB
     * 可按需改成从配置文件读取
     */
    private static final long MAX_FILE_SIZE = 100L * 1024L * 1024L;

    private final ChatMessageMapper chatMessageMapper;
    private final ChatConversationMapper chatConversationMapper;
    private final ChatConversationMemberMapper chatConversationMemberMapper;
    private final ChatAttachmentMapper chatAttachmentMapper;
    private final StorageConfig storageConfig;
    private final SysUserMapper sysUserMapper;
    private final CryptoConfig cryptoConfig;

    public MessageServiceImpl(ChatMessageMapper chatMessageMapper,
                              ChatConversationMapper chatConversationMapper,
                              ChatConversationMemberMapper chatConversationMemberMapper,
                              SysUserMapper sysUserMapper,
                              CryptoConfig cryptoConfig,
                              ChatAttachmentMapper chatAttachmentMapper,
                              StorageConfig storageConfig) {
        this.chatMessageMapper = chatMessageMapper;
        this.chatConversationMapper = chatConversationMapper;
        this.chatConversationMemberMapper = chatConversationMemberMapper;
        this.sysUserMapper = sysUserMapper;
        this.cryptoConfig = cryptoConfig;
        this.chatAttachmentMapper = chatAttachmentMapper;
        this.storageConfig = storageConfig;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MessageVO sendTextMessage(Long currentUserId, SendTextMessageAO ao) {
        if (currentUserId == null) {
            throw new BusinessException(401, "用户未登录");
        }
        if (ao == null) {
            throw new BusinessException("发送消息参数不能为空");
        }
        if (ao.getConversationId() == null) {
            throw new BusinessException("会话ID不能为空");
        }
        if (!StringUtils.hasText(ao.getContent())) {
            throw new BusinessException("消息内容不能为空");
        }

        ChatConversation conversation = chatConversationMapper.selectById(ao.getConversationId());
        if (conversation == null) {
            throw new BusinessException("会话不存在");
        }

        int memberCount = chatConversationMemberMapper.countByConversationIdAndUserId(
                ao.getConversationId(),
                currentUserId
        );
        if (memberCount <= 0) {
            throw new BusinessException(403, "你无权向该会话发送消息");
        }

        AesGcmUtil.EncryptResult encryptResult = AesGcmUtil.encrypt(
                ao.getContent(),
                cryptoConfig.getAesKey()
        );

        ChatMessage message = new ChatMessage();
        message.setConversationId(ao.getConversationId());
        message.setSenderId(currentUserId);
        message.setMessageType("TEXT");
        message.setContentCiphertext(encryptResult.getCiphertextBase64());
        message.setContentIv(encryptResult.getIvBase64());
        message.setContentVersion(cryptoConfig.getVersion());
        message.setDeleted(0);

        int rows = chatMessageMapper.insert(message);
        if (rows <= 0 || message.getId() == null) {
            throw new BusinessException("发送消息失败");
        }

        SysUser sender = sysUserMapper.selectById(currentUserId);

        MessageVO vo = new MessageVO();
        vo.setId(message.getId());
        vo.setConversationId(message.getConversationId());
        vo.setSenderId(message.getSenderId());
        vo.setSenderUsername(sender == null ? null : sender.getUsername());
        vo.setSenderNickname(sender == null ? null : sender.getNickname());
        vo.setMessageType(message.getMessageType());
        vo.setContent(ao.getContent());
        vo.setCreatedAt(message.getCreatedAt());

        return vo;
    }

    @Override
    public List<MessageVO> listRecentMessages(Long currentUserId, Long conversationId, Integer limit) {
        if (currentUserId == null) {
            throw new BusinessException(401, "用户未登录");
        }
        if (conversationId == null) {
            throw new BusinessException("会话ID不能为空");
        }

        if (limit == null || limit <= 0) {
            limit = 20;
        }
        if (limit > 100) {
            limit = 100;
        }

        ChatConversation conversation = chatConversationMapper.selectById(conversationId);
        if (conversation == null) {
            throw new BusinessException("会话不存在");
        }

        int memberCount = chatConversationMemberMapper.countByConversationIdAndUserId(
                conversationId,
                currentUserId
        );
        if (memberCount <= 0) {
            throw new BusinessException(403, "你无权查看该会话消息");
        }

        List<ChatMessage> messages = chatMessageMapper.selectRecentMessages(conversationId, limit);
        if (CollectionUtils.isEmpty(messages)) {
            return new ArrayList<>();
        }

        List<MessageVO> result = new ArrayList<>(messages.size());

        for (ChatMessage message : messages) {
            MessageVO vo = new MessageVO();
            vo.setId(message.getId());
            vo.setConversationId(message.getConversationId());
            vo.setSenderId(message.getSenderId());
            vo.setMessageType(message.getMessageType());
            vo.setCreatedAt(message.getCreatedAt());

            SysUser sender = sysUserMapper.selectById(message.getSenderId());
            if (sender != null) {
                vo.setSenderUsername(sender.getUsername());
                vo.setSenderNickname(sender.getNickname());
            }

            if ("TEXT".equals(message.getMessageType())) {
                if (StringUtils.hasText(message.getContentCiphertext())
                        && StringUtils.hasText(message.getContentIv())) {
                    String content = AesGcmUtil.decrypt(
                            message.getContentCiphertext(),
                            message.getContentIv(),
                            cryptoConfig.getAesKey()
                    );
                    vo.setContent(content);
                }
            } else {
                ChatAttachment attachment = chatAttachmentMapper.selectByMessageId(message.getId());
                if (attachment != null) {
                    AttachmentVO attachmentVO = new AttachmentVO();
                    attachmentVO.setId(attachment.getId());
                    attachmentVO.setMimeType(attachment.getMimeType());
                    attachmentVO.setFileSize(attachment.getFileSize());

                    String originalName = null;
                    if (StringUtils.hasText(attachment.getOriginalNameCiphertext())
                            && StringUtils.hasText(attachment.getOriginalNameIv())) {
                        originalName = AesGcmUtil.decrypt(
                                attachment.getOriginalNameCiphertext(),
                                attachment.getOriginalNameIv(),
                                cryptoConfig.getAesKey()
                        );
                    }

                    attachmentVO.setOriginalName(originalName);
                    vo.setAttachment(attachmentVO);
                }
            }

            result.add(vo);
        }

        // SQL 通常按时间倒序取最近消息，这里转成前端更常用的正序
        Collections.reverse(result);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileMessageVO sendFileMessage(Long currentUserId, Long conversationId, MultipartFile file) {
        if (currentUserId == null) {
            throw new BusinessException(401, "用户未登录");
        }
        if (conversationId == null) {
            throw new BusinessException("会话ID不能为空");
        }
        if (file == null || file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }

        ChatConversation conversation = chatConversationMapper.selectById(conversationId);
        if (conversation == null) {
            throw new BusinessException("会话不存在");
        }

        int memberCount = chatConversationMemberMapper.countByConversationIdAndUserId(conversationId, currentUserId);
        if (memberCount <= 0) {
            throw new BusinessException(403, "你无权向该会话发送附件");
        }

        if (!StringUtils.hasText(storageConfig.getBasePath())) {
            throw new BusinessException("附件存储路径未配置");
        }

        long fileSize = file.getSize();
        if (fileSize <= 0) {
            throw new BusinessException("上传文件不能为空");
        }
        if (fileSize > MAX_FILE_SIZE) {
            throw new BusinessException("文件过大，超过限制：100MB");
        }

        String originalFilename = StringUtils.hasText(file.getOriginalFilename())
                ? file.getOriginalFilename()
                : "unknown";
        String mimeType = file.getContentType();
        String messageType = resolveMessageType(mimeType);

        File targetFile = null;

        try {
            byte[] sourceBytes = file.getBytes();

            FileCryptoUtil.EncryptFileResult encryptResult =
                    FileCryptoUtil.encrypt(sourceBytes, cryptoConfig.getAesKey());

            String storedName = UUID.randomUUID().toString().replace("-", "") + ".bin";
            String relativePath = buildRelativePath(storedName);

            targetFile = new File(storageConfig.getBasePath(), relativePath);
            File parentDir = targetFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                boolean mkdirs = parentDir.mkdirs();
                if (!mkdirs && !parentDir.exists()) {
                    throw new BusinessException("创建文件目录失败");
                }
            }

            Files.write(targetFile.toPath(), encryptResult.getEncryptedBytes());

            AesGcmUtil.EncryptResult fileNameEncryptResult =
                    AesGcmUtil.encrypt(originalFilename, cryptoConfig.getAesKey());

            AesGcmUtil.EncryptResult pathEncryptResult =
                    AesGcmUtil.encrypt(relativePath, cryptoConfig.getAesKey());

            ChatMessage message = new ChatMessage();
            message.setConversationId(conversationId);
            message.setSenderId(currentUserId);
            message.setMessageType(messageType);
            message.setContentCiphertext(null);
            message.setContentIv(null);
            message.setContentVersion(cryptoConfig.getVersion());
            message.setDeleted(0);

            int rows = chatMessageMapper.insert(message);
            if (rows <= 0 || message.getId() == null) {
                throw new BusinessException("创建附件消息失败");
            }

            ChatAttachment attachment = new ChatAttachment();
            attachment.setMessageId(message.getId());
            attachment.setStoredName(storedName);
            attachment.setOriginalNameCiphertext(fileNameEncryptResult.getCiphertextBase64());
            attachment.setOriginalNameIv(fileNameEncryptResult.getIvBase64());
            attachment.setFilePathCiphertext(pathEncryptResult.getCiphertextBase64());
            attachment.setFilePathIv(pathEncryptResult.getIvBase64());
            attachment.setMimeType(mimeType);
            attachment.setFileSize(fileSize);
            attachment.setFileIv(encryptResult.getIvBase64());
            attachment.setFileSha256(DigestUtil.sha256Hex(sourceBytes));

            int attachmentRows = chatAttachmentMapper.insert(attachment);
            if (attachmentRows <= 0 || attachment.getId() == null) {
                throw new BusinessException("保存附件信息失败");
            }

            SysUser sender = sysUserMapper.selectById(currentUserId);

            AttachmentVO attachmentVO = new AttachmentVO();
            attachmentVO.setId(attachment.getId());
            attachmentVO.setOriginalName(originalFilename);
            attachmentVO.setMimeType(mimeType);
            attachmentVO.setFileSize(fileSize);

            FileMessageVO vo = new FileMessageVO();
            vo.setId(message.getId());
            vo.setConversationId(conversationId);
            vo.setSenderId(currentUserId);
            vo.setSenderUsername(sender == null ? null : sender.getUsername());
            vo.setSenderNickname(sender == null ? null : sender.getNickname());
            vo.setMessageType(messageType);
            vo.setCreatedAt(message.getCreatedAt());
            vo.setAttachment(attachmentVO);

            return vo;
        } catch (BusinessException e) {
            deleteQuietly(targetFile);
            throw e;
        } catch (Exception e) {
            deleteQuietly(targetFile);
            throw new BusinessException("发送附件消息失败：" + e.getMessage());
        }
    }

    /**
     * 根据 MIME 类型判断消息类型
     */
    private String resolveMessageType(String mimeType) {
        if (!StringUtils.hasText(mimeType)) {
            return "FILE";
        }
        if (mimeType.startsWith("image/")) {
            return "IMAGE";
        }
        if (mimeType.startsWith("video/")) {
            return "VIDEO";
        }
        return "FILE";
    }

    /**
     * 生成按日期分层的相对路径
     */
    private String buildRelativePath(String storedName) {
        LocalDate now = LocalDate.now();
        return now.getYear() + "/" + now.getMonthValue() + "/" + now.getDayOfMonth() + "/" + storedName;
    }

    /**
     * 失败时尽量删除已落盘文件，避免脏数据
     */
    private void deleteQuietly(File file) {
        if (file == null || !file.exists()) {
            return;
        }
        try {
            Files.deleteIfExists(file.toPath());
        } catch (Exception ignored) {
        }
    }
}