package com.lvwyh.chat.vo;

/**
 * 登录返回结果
 */
public class LoginVO {

    /**
     * JWT令牌
     */
    private String token;

    /**
     * 用户信息
     */
    private UserVO userInfo;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserVO getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserVO userInfo) {
        this.userInfo = userInfo;
    }
}