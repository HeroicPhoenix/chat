package com.lvwyh.chat.vo;

/**
 * 附件返回对象
 */
public class AttachmentVO {

    /**
     * 附件ID
     */
    private Long id;

    /**
     * 原始文件名
     */
    private String originalName;

    /**
     * MIME 类型
     */
    private String mimeType;

    /**
     * 文件大小
     */
    private Long fileSize;

    public Long getId() {
        return id;
    }

    public String getOriginalName() {
        return originalName;
    }

    public String getMimeType() {
        return mimeType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
}