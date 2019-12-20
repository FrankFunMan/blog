package com.zj.blog.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zj.blog.sys.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 后台用户表 Mapper 接口
 * </p>
 *
 * @author zscat
 * @since 2019-04-14
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

}
