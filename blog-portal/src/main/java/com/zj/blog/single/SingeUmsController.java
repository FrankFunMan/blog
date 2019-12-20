package com.zj.blog.single;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zj.blog.annotation.IgnoreAuth;
import com.zj.blog.annotation.SysLog;
import com.zj.blog.sys.entity.SysArea;
import com.zj.blog.sys.entity.SysSchool;
import com.zj.blog.ums.entity.UmsMember;
import com.zj.blog.ums.entity.UmsMemberMemberTagRelation;
import com.zj.blog.ums.service.IUmsMemberMemberTagRelationService;
import com.zj.blog.ums.service.IUmsMemberService;
import com.zj.blog.util.UserUtils;
import com.zj.blog.utils.CommonResult;
import com.zj.blog.utils.ValidatorUtils;
import com.zj.blog.cms.service.ISysAreaService;
import com.zj.blog.cms.service.ISysSchoolService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: shenzhuan
 * @Date: 2019/4/2 15:02
 * @Description:
 */
@RestController
@Api(tags = "UmsController", description = "会员关系管理")
@RequestMapping("/api/single/user")
public class SingeUmsController extends ApiBaseAction{

    @Resource
    private ISysSchoolService schoolService;
    @Resource
    private IUmsMemberService memberService;
    @Resource
    private ISysAreaService areaService;
    @Resource
    private IUmsMemberMemberTagRelationService memberTagService;
    @IgnoreAuth
    @ApiOperation(value = "查询学校列表")
    @GetMapping(value = "/school/list")
    @SysLog(MODULE = "ums", REMARK = "查询学校列表")
    public Object subjectList(SysSchool entity,
                              @RequestParam(value = "pageSize", required = false, defaultValue = "5") Integer pageSize,
                              @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum) {
        return new CommonResult().success(schoolService.page(new Page<SysSchool>(pageNum, pageSize), new QueryWrapper<>(entity)));
    }
    @IgnoreAuth
    @SysLog(MODULE = "ums", REMARK = "根据pid查询区域")
    @ApiOperation("根据pid查询区域")
    @RequestMapping(value = "/getAreaByPid", method = RequestMethod.GET)
    public Object getAreaByPid(@RequestParam(value = "pid", required = false, defaultValue = "0") Long pid) {
        SysArea queryPid = new SysArea();
        queryPid.setPid(pid);
        List<SysArea> list = areaService.list(new QueryWrapper<SysArea>(queryPid));
        return new CommonResult().success(list);
    }

    @ApiOperation(value = "会员绑定学校")
    @PostMapping(value = "/bindSchool")
    @SysLog(MODULE = "ums", REMARK = "会员绑定学校")
    public Object bindSchool(@RequestParam(value = "schoolId", required = true) Long schoolId) {
        try {
            UmsMember member = UserUtils.getCurrentMember();
            member.setSchoolId(schoolId);
            memberService.updateById(member);
            return new CommonResult().success("绑定学校成功");
        }catch (Exception e){
            e.printStackTrace();
            return new CommonResult().failed("绑定学校失败");
        }
    }

    @ApiOperation(value = "会员绑定区域")
    @PostMapping(value = "/bindArea")
    @SysLog(MODULE = "ums", REMARK = "会员绑定区域")
    public Object bindArea(@RequestParam(value = "areaIds", required = true) String  areaIds) {
        try {
            if (ValidatorUtils.empty(areaIds)){
                return new CommonResult().failed("请选择区域");
            }
            UmsMember member = UserUtils.getCurrentMember();
            String[] areIdList = areaIds.split(",");
            List<UmsMemberMemberTagRelation> list = new ArrayList<>();
            for (String id : areIdList){
                UmsMemberMemberTagRelation tag = new UmsMemberMemberTagRelation();
                tag.setMemberId(member.getId());
                tag.setTagId(Long.valueOf(id));
                list.add(tag);
            }
            if (list!=null && list.size()>0){
                memberTagService.saveBatch(list);
            }
            return new CommonResult().success("绑定区域成功");
        }catch (Exception e){
            e.printStackTrace();
            return new CommonResult().failed("绑定区域失败");
        }
    }
}
