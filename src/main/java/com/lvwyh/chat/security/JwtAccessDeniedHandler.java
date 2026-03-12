package com.lvwyh.chat.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lvwyh.chat.common.ApiResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 已登录但无权限访问时的统一处理器
 *
 * 当前版本虽然还没有细粒度权限控制，
 * 但建议提前接入，后续扩展角色权限时可直接复用。
 */
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    /**
     * JSON 序列化工具
     */
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        ApiResponse<Object> result =
                new ApiResponse<Object>(403, false, "无权限访问该资源", null);

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("application/json;charset=UTF-8");

        response.getWriter().write(objectMapper.writeValueAsString(result));
        response.getWriter().flush();
    }
}