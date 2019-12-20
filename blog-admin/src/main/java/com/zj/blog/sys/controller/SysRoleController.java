package com.zj.blog.sys.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.ApiController;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zj.blog.annotation.SysLog;
import com.zj.blog.sys.entity.SysRole;
import com.zj.blog.sys.entity.SysRolePermission;
import com.zj.blog.sys.service.ISysRoleService;
import com.zj.blog.utils.CommonResult;
import com.zj.blog.utils.ValidatorUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 后台角色表 前端控制器
 * </p>
 *
 * @author zscat
 * @since 2019-04-14
 */
@Slf4j
@Api(value = "角色管理", description = "", tags = {"角色管理"})
@RestController
@RequestMapping("/sys/sysRole")
public class SysRoleController extends ApiController {

    @Resource
    private ISysRoleService sysRoleService;

    @SysLog(MODULE = "sys", REMARK = "根据条件查询所有角色列表")
    @ApiOperation("根据条件查询所有角色列表")
    @GetMapping(value = "/list")
    @PreAuthorize("hasAuthority('sys:role:read')")
    public Object getRoleByPage(String keyword,
                                @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize
    ) {
        try {
            return new CommonResult().success(sysRoleService.page(new Page<SysRole>(pageNum, pageSize), new QueryWrapper<SysRole>().and(ValidatorUtils.notEmpty(keyword), wrapper -> wrapper.like("id", keyword).or().like("name", keyword).or().like("description", keyword))));
        } catch (Exception e) {
            log.error("根据条件查询所有角色列表：%s", e.getMessage(), e);
        }
        return new CommonResult().failed();
    }

    @SysLog(MODULE = "sys", REMARK = "保存角色")
    @ApiOperation("保存角色")
    @PostMapping(value = "/create")
    @PreAuthorize("hasAuthority('sys:role:read')")
    public Object saveRole(@RequestBody SysRole entity) {
        try {
            if (sysRoleService.saves(entity)) {
                return new CommonResult().success();
            }
        } catch (Exception e) {
            log.error("保存角色：%s", e.getMessage(), e);
            return new CommonResult().failed();
        }
        return new CommonResult().failed();
    }

    @SysLog(MODULE = "sys", REMARK = "更新角色")
    @ApiOperation("更新角色")
    @PostMapping(value = "/update/{id}")
    @PreAuthorize("hasAuthority('sys:role:read')")
    public Object updateRole(@RequestBody SysRole entity) {
        try {
            if (sysRoleService.updates(entity)) {
                return new CommonResult().success();
            }
        } catch (Exception e) {
            log.error("更新角色：%s", e.getMessage(), e);
            return new CommonResult().failed();
        }
        return new CommonResult().failed();
    }

    @SysLog(MODULE = "sys", REMARK = "更新角色状态")
    @ApiOperation("更新角色状态")
    @PostMapping(value = "/update/batch/status")
    @PreAuthorize("hasAuthority('sys:role:read')")
    public Object updateBatchStatus(@RequestParam("ids") List<Long> ids, @RequestParam("status") Integer status) {
        try {
            if (ValidatorUtils.notEmpty(ids)) {
                List<SysRole> sysRoles = new ArrayList<>();
                for (Long id : ids) {
                    SysRole sysRole = new SysRole();
                    sysRole.setId(id);
                    sysRole.setStatus(status);
                    sysRoles.add(sysRole);
                }
                if (sysRoleService.updateBatchById(sysRoles)) {
                    return new CommonResult().success();
                }
            }
        } catch (Exception e) {
            log.error("更新角色：%s", e.getMessage(), e);
            return new CommonResult().failed();
        }
        return new CommonResult().failed();
    }

    @SysLog(MODULE = "sys", REMARK = "批量更新角色状态")
    @ApiOperation("批量更新角色状态")
    @PostMapping(value = "/update/showStatus")
    @PreAuthorize("hasAuthority('sys:role:read')")
    public Object showStatus(SysRole entity) {
        try {
            if (sysRoleService.updateById(entity)) {
                return new CommonResult().success();
            }
        } catch (Exception e) {
            log.error("更新角色：%s", e.getMessage(), e);
            return new CommonResult().failed();
        }
        return new CommonResult().failed();
    }

    @SysLog(MODULE = "sys", REMARK = "删除角色")
    @ApiOperation("删除角色")
    @DeleteMapping(value = "/delete/{id}")
    @PreAuthorize("hasAuthority('sys:role:read')")
    public Object deleteRole(@ApiParam("角色id") @PathVariable Long id) {
        try {
            if (ValidatorUtils.empty(id)) {
                return new CommonResult().paramFailed("角色id");
            }
            if (sysRoleService.removeById(id)) {
                return new CommonResult().success();
            }
        } catch (Exception e) {
            log.error("删除角色：%s", e.getMessage(), e);
            return new CommonResult().failed();
        }
        return new CommonResult().failed();
    }

    @SysLog(MODULE = "sys", REMARK = "给角色分配角色")
    @ApiOperation("查询角色明细")
    @GetMapping(value = "/{id}")
    @PreAuthorize("hasAuthority('sys:role:read')")
    public Object getRoleById(@ApiParam("角色id") @PathVariable Long id) {
        try {
            if (ValidatorUtils.empty(id)) {
                return new CommonResult().paramFailed("角色id");
            }
            SysRole coupon = sysRoleService.getById(id);
            return new CommonResult().success(coupon);
        } catch (Exception e) {
            log.error("查询角色明细：%s", e.getMessage(), e);
            return new CommonResult().failed();
        }

    }

    @ApiOperation(value = "批量删除角色")
    @RequestMapping(value = "/deleteBatch", method = RequestMethod.POST)
    @ResponseBody
    @SysLog(MODULE = "sys", REMARK = "批量删除角色")
    @PreAuthorize("hasAuthority('sys:role:read')")
    public Object deleteBatch(@RequestParam("ids") List<Long> ids) {
        boolean count = sysRoleService.removeByIds(ids);
        if (count) {
            return new CommonResult().success(count);
        } else {
            return new CommonResult().failed();
        }
    }

    @SysLog(MODULE = "sys", REMARK = "获取相应角色权限")
    @ApiOperation("获取相应角色权限")
    @RequestMapping(value = "/permission/{roleId}", method = RequestMethod.GET)
    @ResponseBody
    public Object getPermissionList(@PathVariable Long roleId) {
        List<SysRolePermission> permissionList = sysRoleService.getRolePermission(roleId);
        return new CommonResult().success(permissionList);
    }

    @SysLog(MODULE = "sys", REMARK = "获取相应角色权限-单表")
    @ApiOperation("获取相应角色权限-单表")
    @RequestMapping(value = "/rolePermission/{roleId}", method = RequestMethod.GET)
    @ResponseBody
    public Object rolePermission(@PathVariable Long roleId) {
        List<SysRolePermission> rolePermission = sysRoleService.getRolePermission(roleId);
        return new CommonResult().success(rolePermission);
    }
}

