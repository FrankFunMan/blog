package com.zj.blog.marking.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zj.blog.cms.entity.CmsSubject;
import com.zj.blog.marking.entity.SmsHomeAdvertise;
import com.zj.blog.oms.vo.HomeContentResult;
import com.zj.blog.pms.entity.PmsBrand;
import com.zj.blog.pms.entity.PmsProduct;

import java.util.List;

/**
 * <p>
 * 首页轮播广告表 服务类
 * </p>
 *
 * @author zscat
 * @since 2019-04-19
 */
public interface ISmsHomeAdvertiseService extends IService<SmsHomeAdvertise> {

    HomeContentResult singelContent();

     List<PmsBrand> getRecommendBrandList(int pageNum, int pageSize) ;
     List<PmsProduct> getNewProductList(int pageNum, int pageSize) ;
     List<PmsProduct> getHotProductList(int pageNum, int pageSize) ;
     List<CmsSubject> getRecommendSubjectList(int pageNum, int pageSize) ;

     List<SmsHomeAdvertise> getHomeAdvertiseList() ;
}
