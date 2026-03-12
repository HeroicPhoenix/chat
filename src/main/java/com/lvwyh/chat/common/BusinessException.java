package com.lvwyh.chat.common;

/**
 * 业务异常
 *
 * 用于主动抛出可预期的业务错误，
 * 比如用户名已存在、用户不存在、参数非法等。
 */
public class BusinessException extends RuntimeException {

    /**
     * 业务码
     */
    private Integer code;

    public BusinessException(String message) {
        super(message);
        this.code = 500;
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}