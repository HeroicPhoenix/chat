package com.lvwyh.chat.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

/**
 * JWT工具类
 */
public class JwtTokenUtil {

    /**
     * JWT密钥
     */
    private final String secret;

    /**
     * token过期时间，单位秒
     */
    private final long expireSeconds;

    public JwtTokenUtil(String secret, long expireSeconds) {
        this.secret = secret;
        this.expireSeconds = expireSeconds;
    }

    /**
     * 生成token
     *
     * @param userId 用户ID
     * @param username 用户名
     * @return token字符串
     */
    public String generateToken(Long userId, String username) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + expireSeconds * 1000);

        return Jwts.builder()
                .claim("userId", userId)
                .claim("username", username)
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    /**
     * 解析token
     *
     * @param token token
     * @return claims
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 校验token是否有效
     *
     * @param token token
     * @return true-有效，false-无效
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = parseToken(token);
            Date expiration = claims.getExpiration();
            return expiration != null && expiration.after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取用户ID
     *
     * @param token token
     * @return 用户ID
     */
    public Long getUserId(String token) {
        Claims claims = parseToken(token);
        Object value = claims.get("userId");
        return value == null ? null : Long.valueOf(String.valueOf(value));
    }

    /**
     * 获取用户名
     *
     * @param token token
     * @return 用户名
     */
    public String getUsername(String token) {
        Claims claims = parseToken(token);
        Object value = claims.get("username");
        return value == null ? null : String.valueOf(value);
    }
}