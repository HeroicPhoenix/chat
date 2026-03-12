package com.lvwyh.chat.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lvwyh.chat.common.ApiResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * JWT 认证失败入口
 *
 * 作用：
 * 1. 当用户未登录访问受保护资源时触发
 * 2. 当 token 无效、token 过期且未通过认证时触发
 * 3. 统一返回 JSON，而不是默认的 HTML 或空白 401 页面
 */
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * JSON 序列化工具
     */
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        ApiResponse<Object> result =
                new ApiResponse<Object>(401, false, "未登录或token已失效", null);

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("application/json;charset=UTF-8");

        response.getWriter().write(objectMapper.writeValueAsString(result));
        response.getWriter().flush();
    }
}