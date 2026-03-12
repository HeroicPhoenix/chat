package com.lvwyh.chat.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * 当前登录用户信息
 *
 * 说明：
 * 1. 这里实现 UserDetails，便于接入 Spring Security
 * 2. 当前版本先不做角色权限，所以 authorities 返回空集合
 * 3. 后续如果要扩展角色、权限，可以在这里增加 roleCode、permissionList 等字段
 */
public class LoginUser implements UserDetails {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 用户状态：1-正常，0-禁用
     */
    private Integer status;

    public LoginUser() {
    }

    public LoginUser(Long userId, String username, String nickname, Integer status) {
        this.userId = userId;
        this.username = username;
        this.nickname = nickname;
        this.status = status;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setUsernameValue(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * 当前版本不使用角色权限，返回空集合
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    /**
     * JWT 场景下，这里密码通常不参与认证流程
     * 返回 null 即可
     */
    @Override
    public String getPassword() {
        return null;
    }

    /**
     * 返回用户名
     */
    @Override
    public String getUsername() {
        return username;
    }

    /**
     * 账号是否未过期
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 账号是否未锁定
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 凭证是否未过期
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 账号是否启用
     */
    @Override
    public boolean isEnabled() {
        return status != null && status == 1;
    }
}