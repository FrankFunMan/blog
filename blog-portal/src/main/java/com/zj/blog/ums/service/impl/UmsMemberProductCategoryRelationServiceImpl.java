package com.zj.blog.ums.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zj.blog.ums.entity.UmsMemberProductCategoryRelation;
import com.zj.blog.ums.mapper.UmsMemberProductCategoryRelationMapper;
import com.zj.blog.ums.service.IUmsMemberProductCategoryRelationService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 会员与产品分类关系表（用户喜欢的分类） 服务实现类
 * </p>
 *
 * @author zscat
 * @since 2019-04-19
 */
@Service
public class UmsMemberProductCategoryRelationServiceImpl extends ServiceImpl<UmsMemberProductCategoryRelationMapper, UmsMemberProductCategoryRelation> implements IUmsMemberProductCategoryRelationService {

}
