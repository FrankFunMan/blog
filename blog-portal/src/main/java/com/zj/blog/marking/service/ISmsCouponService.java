package com.zj.blog.marking.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zj.blog.marking.entity.SmsCoupon;
import com.zj.blog.marking.entity.SmsCouponHistory;
import com.zj.blog.marking.vo.SmsCouponHistoryDetail;
import com.zj.blog.oms.vo.CartPromotionItem;
import com.zj.blog.utils.CommonResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 优惠卷表 服务类
 * </p>
 *
 * @author zscat
 * @since 2019-04-19
 */
public interface ISmsCouponService extends IService<SmsCoupon> {

    /**
     * 会员添加优惠券
     */
    @Transactional
    CommonResult add(Long couponId);

    /**
     * 获取优惠券列表
     *
     * @param useStatus 优惠券的使用状态
     */
    List<SmsCouponHistory> list(Integer useStatus);

    /**
     * 根据购物车信息获取可用优惠券
     */
    List<SmsCouponHistoryDetail> listCart(List<CartPromotionItem> cartItemList, Integer type);


    List<SmsCoupon> selectNotRecive(Long memberId);
    List<SmsCoupon> selectRecive(Long memberId);

    List<SmsCoupon> selectNotRecive();
}
