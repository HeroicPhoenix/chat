package com.lvwyh.chat.ao;

import javax.validation.constraints.Size;
import java.util.List;

/**
 * 创建会话请求参数
 */
public class CreateConversationAO {

    /**
     * 会话名称，可为空
     */
    @Size(max = 128, message = "会话名称长度不能超过128")
    private String name;

    /**
     * 除自己之外的成员ID列表，可为空
     */
    private List<Long> memberIds;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Long> getMemberIds() {
        return memberIds;
    }

    public void setMemberIds(List<Long> memberIds) {
        this.memberIds = memberIds;
    }
}