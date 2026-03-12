package com.lvwyh.chat.service;

import com.lvwyh.chat.ao.LoginAO;
import com.lvwyh.chat.ao.RegisterAO;
import com.lvwyh.chat.vo.LoginVO;
import com.lvwyh.chat.vo.UserVO;

/**
 * 认证服务接口
 */
public interface AuthService {

    /**
     * 注册
     *
     * @param ao 注册参数
     */
    void register(RegisterAO ao);

    /**
     * 登录
     *
     * @param ao 登录参数
     * @return 登录结果
     */
    LoginVO login(LoginAO ao);

    /**
     * 获取当前登录用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    UserVO getCurrentUser(Long userId);

    /**
     * 注销
     */
    void logout();
}