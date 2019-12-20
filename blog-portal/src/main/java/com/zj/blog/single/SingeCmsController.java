package com.zj.blog.single;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zj.blog.annotation.IgnoreAuth;
import com.zj.blog.annotation.SysLog;
import com.zj.blog.cms.entity.CmsSubject;
import com.zj.blog.cms.entity.CmsSubjectCategory;
import com.zj.blog.cms.entity.CmsSubjectComment;
import com.zj.blog.ums.entity.UmsMember;
import com.zj.blog.ums.entity.UmsMemberLevel;
import com.zj.blog.ums.service.IUmsMemberLevelService;
import com.zj.blog.utils.CommonResult;
import com.zj.blog.cms.service.ICmsSubjectCategoryService;
import com.zj.blog.cms.service.ICmsSubjectCommentService;
import com.zj.blog.cms.service.ICmsSubjectService;
import com.zj.blog.marking.service.ISmsGroupService;
import com.zj.blog.pms.service.IPmsProductAttributeCategoryService;
import com.zj.blog.pms.service.IPmsProductCategoryService;
import com.zj.blog.pms.service.IPmsProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Auther: shenzhuan
 * @Date: 2019/4/2 15:02
 * @Description:
 */
@RestController
@Api(tags = "CmsController", description = "内容关系管理")
@RequestMapping("/api/single/cms")
public class SingeCmsController extends ApiBaseAction {

    @Resource
    private ISmsGroupService groupService;
    @Resource
    private IUmsMemberLevelService memberLevelService;
    @Resource
    private IPmsProductService pmsProductService;
    @Resource
    private IPmsProductAttributeCategoryService productAttributeCategoryService;
    @Resource
    private IPmsProductCategoryService productCategoryService;


    @Resource
    private ICmsSubjectCategoryService subjectCategoryService;
    @Resource
    private ICmsSubjectService subjectService;
    @Resource
    private ICmsSubjectCommentService commentService;

    @IgnoreAuth
    @SysLog(MODULE = "cms", REMARK = "查询文章列表")
    @ApiOperation(value = "查询文章列表")
    @GetMapping(value = "/subject/list")
    public Object subjectList(CmsSubject subject,
                              @RequestParam(value = "pageSize", required = false, defaultValue = "5") Integer pageSize,
                              @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum) {
        return new CommonResult().success(subjectService.page(new Page<CmsSubject>(pageNum, pageSize), new QueryWrapper<>(subject)));
    }

    @SysLog(MODULE = "cms", REMARK = "查询文章分类列表")
    @IgnoreAuth
    @ApiOperation(value = "查询文章分类列表")
    @GetMapping(value = "/subjectCategory/list")
    public Object cateList(CmsSubjectCategory subjectCategory,
                           @RequestParam(value = "pageSize", required = false, defaultValue = "5") Integer pageSize,
                           @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum) {
        return new CommonResult().success(subjectCategoryService.page(new Page<CmsSubjectCategory>(pageNum, pageSize), new QueryWrapper<>(subjectCategory)));
    }

    @SysLog(MODULE = "cms", REMARK = "查询文章评论列表")
    @IgnoreAuth
    @ApiOperation(value = "查询文章评论列表")
    @GetMapping(value = "/subjectComment/list")
    public Object subjectList(CmsSubjectComment subjectComment,
                              @RequestParam(value = "pageSize", required = false, defaultValue = "5") Integer pageSize,
                              @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum) {
        return new CommonResult().success(commentService.page(new Page<CmsSubjectComment>(pageNum, pageSize), new QueryWrapper<>(subjectComment)));
    }

    @SysLog(MODULE = "cms", REMARK = "创建文章")
    @ApiOperation(value = "创建文章")
    @PostMapping(value = "/createSubject")
    public Object createSubject(CmsSubject subject, BindingResult result) {
        CommonResult commonResult;
        UmsMember member = this.getCurrentMember();
        if (member.getMemberLevelId()>0){
            UmsMemberLevel memberLevel = memberLevelService.getById(member.getMemberLevelId());
            CmsSubject newSubject = new CmsSubject();
            newSubject.setMemberId(member.getId());
            List<CmsSubject> subjects = subjectService.list(new QueryWrapper<>(newSubject));
            if (subjects!=null && subjects.size()>memberLevel.getArticlecount()){
                commonResult = new CommonResult().failed("你今天已经有发"+memberLevel.getArticlecount()+"篇文章");
                return commonResult;
            }
        }
        subject.setMemberId(member.getId());
        boolean count = subjectService.save(subject);
        if (count) {
            commonResult = new CommonResult().success(count);
        } else {
            commonResult = new CommonResult().failed();
        }
        return commonResult;
    }


}
