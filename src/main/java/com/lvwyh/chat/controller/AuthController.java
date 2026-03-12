package com.lvwyh.chat.controller;

import com.lvwyh.chat.ao.LoginAO;
import com.lvwyh.chat.ao.RegisterAO;
import com.lvwyh.chat.common.ApiResponse;
import com.lvwyh.chat.service.AuthService;
import com.lvwyh.chat.util.SecurityUtil;
import com.lvwyh.chat.vo.LoginVO;
import com.lvwyh.chat.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "注册")
    @PostMapping("/register")
    public ApiResponse<String> register(@Valid @RequestBody RegisterAO ao) {
        authService.register(ao);
        return ApiResponse.success("注册成功", null);
    }

    @Operation(summary = "登录")
    @PostMapping("/login")
    public ApiResponse<LoginVO> login(@Valid @RequestBody LoginAO ao) {
        LoginVO loginVO = authService.login(ao);
        return ApiResponse.success("登录成功", loginVO);
    }

    @Operation(summary = "注销")
    @PostMapping("/logout")
    public ApiResponse<String> logout() {
        authService.logout();
        return ApiResponse.success("注销成功", null);
    }

    @Operation(summary = "获取当前登录用户")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/me")
    public ApiResponse<UserVO> me() {
        Long userId = SecurityUtil.getCurrentUserId();
        if (userId == null) {
            return ApiResponse.fail("未登录");
        }

        UserVO userVO = authService.getCurrentUser(userId);
        return ApiResponse.success(userVO);
    }
}