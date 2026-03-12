package com.lvwyh.chat.security;

import com.lvwyh.chat.entity.SysUser;
import com.lvwyh.chat.mapper.SysUserMapper;
import com.lvwyh.chat.util.JwtTokenUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT认证过滤器
 *
 * 主要职责：
 * 1. 从请求头 Authorization 中提取 Bearer Token
 * 2. 校验 Token 是否有效
 * 3. 根据 Token 中的 userId / username 构造登录态
 * 4. 将登录态写入 SecurityContextHolder
 *
 * 说明：
 * 1. 该过滤器每个请求只执行一次
 * 2. 对登录、注册、swagger 等白名单接口直接放行
 * 3. 如果 token 无效，不在这里直接抛异常，而是清空上下文后继续走后续流程
 *    最终由 Spring Security 的认证机制决定是否拦截
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /**
     * 请求路径匹配器
     */
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    /**
     * JWT工具类
     */
    private final JwtTokenUtil jwtTokenUtil;

    /**
     * 用户Mapper
     */
    private final SysUserMapper sysUserMapper;

    /**
     * 白名单路径
     */
    private static final String[] WHITE_LIST = {
            "/api/auth/login",
            "/api/auth/register",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/webjars/**",
            "/error"
    };

    public JwtAuthenticationFilter(JwtTokenUtil jwtTokenUtil, SysUserMapper sysUserMapper) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.sysUserMapper = sysUserMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestUri = request.getRequestURI();

        // 白名单请求直接放行
        if (isWhiteList(requestUri)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 如果当前上下文已经有认证信息，则直接放行
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = getTokenFromRequest(request);

        // 没有token，直接放行，后续由 Spring Security 判断是否需要拦截
        if (!StringUtils.hasText(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // token无效则不写入认证信息
            if (!jwtTokenUtil.validateToken(token)) {
                SecurityContextHolder.clearContext();
                filterChain.doFilter(request, response);
                return;
            }

            Long userId = jwtTokenUtil.getUserId(token);
            String username = jwtTokenUtil.getUsername(token);

            if (userId == null || !StringUtils.hasText(username)) {
                SecurityContextHolder.clearContext();
                filterChain.doFilter(request, response);
                return;
            }

            // 从数据库查询当前用户，避免 token 中信息过期或用户被禁用后仍可访问
            SysUser user = sysUserMapper.selectById(userId);
            if (user == null) {
                SecurityContextHolder.clearContext();
                filterChain.doFilter(request, response);
                return;
            }

            // 用户被禁用，直接清理上下文
            if (user.getStatus() == null || user.getStatus() != 1) {
                SecurityContextHolder.clearContext();
                filterChain.doFilter(request, response);
                return;
            }

            LoginUser loginUser = new LoginUser(
                    user.getId(),
                    user.getUsername(),
                    user.getNickname(),
                    user.getStatus()
            );

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(
                            loginUser,
                            null,
                            loginUser.getAuthorities()
                    );

            authenticationToken.setDetails(loginUser);

            // 写入 Spring Security 上下文
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        } catch (Exception e) {
            // token 解析异常或数据库异常时，清空上下文，继续后续流程
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 判断当前请求是否命中白名单
     *
     * @param requestUri 请求路径
     * @return true-白名单，false-非白名单
     */
    private boolean isWhiteList(String requestUri) {
        for (String pattern : WHITE_LIST) {
            if (antPathMatcher.match(pattern, requestUri)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 从请求头中提取 Bearer Token
     *
     * @param request 请求对象
     * @return token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (!StringUtils.hasText(authorization)) {
            return null;
        }

        if (!authorization.startsWith("Bearer ")) {
            return null;
        }

        return authorization.substring(7);
    }
}