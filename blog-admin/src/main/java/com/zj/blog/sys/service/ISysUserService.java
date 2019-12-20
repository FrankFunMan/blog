package com.zj.blog.sys.service;

import com.zj.blog.sys.entity.SysPermission;
import com.zj.blog.sys.entity.SysRole;
import com.zj.blog.sys.entity.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 后台用户表 服务类
 * </p>
 *
 * @author zscat
 * @since 2019-04-14
 */
public interface ISysUserService extends IService<SysUser> {

    String refreshToken(String token);

    String login(String username, String password);

    int updateUserRole(Long adminId, List<Long> roleIds);

    List<SysRole> getRoleListByUserId(Long adminId);

    int updatePermissionByUserId(Long adminId, List<Long> permissionIds);

    List<SysPermission> getPermissionListByUserId(Long adminId);

    boolean saves(SysUser entity);
    boolean updates(Long id, SysUser admin);

    List<SysPermission> listUserPerms(Long id);

    void removePermissRedis(Long id);
}
