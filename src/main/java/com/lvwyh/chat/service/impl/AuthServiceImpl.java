package com.lvwyh.chat.service.impl;

import com.lvwyh.chat.ao.LoginAO;
import com.lvwyh.chat.ao.RegisterAO;
import com.lvwyh.chat.common.BusinessException;
import com.lvwyh.chat.entity.SysUser;
import com.lvwyh.chat.mapper.SysUserMapper;
import com.lvwyh.chat.service.AuthService;
import com.lvwyh.chat.util.JwtTokenUtil;
import com.lvwyh.chat.vo.LoginVO;
import com.lvwyh.chat.vo.UserVO;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 认证服务实现
 */
@Service
public class AuthServiceImpl implements AuthService {

    private final SysUserMapper sysUserMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;

    public AuthServiceImpl(SysUserMapper sysUserMapper,
                           BCryptPasswordEncoder passwordEncoder,
                           JwtTokenUtil jwtTokenUtil) {
        this.sysUserMapper = sysUserMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public void register(RegisterAO ao) {
        if (ao == null) {
            throw new BusinessException("注册参数不能为空");
        }

        String username = ao.getUsername();
        String password = ao.getPassword();
        String nickname = ao.getNickname();

        if (!StringUtils.hasText(username)) {
            throw new BusinessException("用户名不能为空");
        }
        if (!StringUtils.hasText(password)) {
            throw new BusinessException("密码不能为空");
        }
        if (!StringUtils.hasText(nickname)) {
            throw new BusinessException("昵称不能为空");
        }

        SysUser existUser = sysUserMapper.selectByUsername(username);
        if (existUser != null) {
            throw new BusinessException("用户名已存在");
        }

        SysUser user = new SysUser();
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setNickname(nickname);
        user.setStatus(1);

        int rows = sysUserMapper.insert(user);
        if (rows <= 0) {
            throw new BusinessException("注册失败");
        }
    }

    @Override
    public LoginVO login(LoginAO ao) {
        if (ao == null) {
            throw new BusinessException("登录参数不能为空");
        }

        String username = ao.getUsername();
        String password = ao.getPassword();

        SysUser user = sysUserMapper.selectByUsername(username);
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }

        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new BusinessException("用户已被禁用");
        }

        boolean matched = passwordEncoder.matches(password, user.getPasswordHash());
        if (!matched) {
            throw new BusinessException("用户名或密码错误");
        }

        String token = jwtTokenUtil.generateToken(user.getId(), user.getUsername());

        UserVO userVO = new UserVO();
        userVO.setId(user.getId());
        userVO.setUsername(user.getUsername());
        userVO.setNickname(user.getNickname());

        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setUserInfo(userVO);

        return loginVO;
    }

    @Override
    public UserVO getCurrentUser(Long userId) {
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }

        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        UserVO userVO = new UserVO();
        userVO.setId(user.getId());
        userVO.setUsername(user.getUsername());
        userVO.setNickname(user.getNickname());

        return userVO;
    }

    @Override
    public void logout() {
        // 第一版JWT注销只做前端删除token
        // 如果后续需要服务端强制失效，可增加token黑名单表或Redis
    }
}