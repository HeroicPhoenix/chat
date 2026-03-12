package com.lvwyh.chat.entity;

import java.util.Date;

/**
 * 附件实体
 */
public class ChatAttachment {

    /**
     * 附件ID
     */
    private Long id;

    /**
     * 所属消息ID
     */
    private Long messageId;

    /**
     * NAS 上实际存储文件名
     */
    private String storedName;

    /**
     * 原始文件名密文
     */
    private String originalNameCiphertext;

    /**
     * 原始文件名加密 IV
     */
    private String originalNameIv;

    /**
     * 文件路径密文
     */
    private String filePathCiphertext;

    /**
     * 文件路径加密 IV
     */
    private String filePathIv;

    /**
     * 文件 MIME 类型
     */
    private String mimeType;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 文件内容加密使用的 IV
     */
    private String fileIv;

    /**
     * 文件 SHA-256 摘要
     */
    private String fileSha256;

    /**
     * 创建时间
     */
    private Date createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public String getStoredName() {
        return storedName;
    }

    public void setStoredName(String storedName) {
        this.storedName = storedName;
    }

    public String getOriginalNameCiphertext() {
        return originalNameCiphertext;
    }

    public void setOriginalNameCiphertext(String originalNameCiphertext) {
        this.originalNameCiphertext = originalNameCiphertext;
    }

    public String getOriginalNameIv() {
        return originalNameIv;
    }

    public void setOriginalNameIv(String originalNameIv) {
        this.originalNameIv = originalNameIv;
    }

    public String getFilePathCiphertext() {
        return filePathCiphertext;
    }

    public void setFilePathCiphertext(String filePathCiphertext) {
        this.filePathCiphertext = filePathCiphertext;
    }

    public String getFilePathIv() {
        return filePathIv;
    }

    public void setFilePathIv(String filePathIv) {
        this.filePathIv = filePathIv;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileIv() {
        return fileIv;
    }

    public void setFileIv(String fileIv) {
        this.fileIv = fileIv;
    }

    public String getFileSha256() {
        return fileSha256;
    }

    public void setFileSha256(String fileSha256) {
        this.fileSha256 = fileSha256;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}