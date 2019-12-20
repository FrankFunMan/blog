package com.zj.blog.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zj.blog.bo.Tree;
import com.zj.blog.sys.entity.SysPermission;
import com.zj.blog.sys.entity.SysPermissionNode;

import java.util.List;

/**
 * <p>
 * 后台用户权限表 服务类
 * </p>
 *
 * @author zscat
 * @since 2019-04-14
 */
public interface ISysPermissionService extends IService<SysPermission> {

    List<Tree<SysPermission>> getPermissionsByUserId(Long id);

    List<SysPermissionNode> treeList();
}
