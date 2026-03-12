package com.lvwyh.chat.mapper;

import com.lvwyh.chat.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户Mapper
 */
@Mapper
public interface SysUserMapper {

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    SysUser selectByUsername(@Param("username") String username);

    /**
     * 根据ID查询用户
     *
     * @param id 用户ID
     * @return 用户信息
     */
    SysUser selectById(@Param("id") Long id);

    /**
     * 新增用户
     *
     * @param user 用户实体
     * @return 影响行数
     */
    int insert(SysUser user);
}