package com.zj.blog.vo;


import com.zj.blog.cms.entity.CmsSubject;
import com.zj.blog.marking.entity.SmsCoupon;
import com.zj.blog.marking.entity.SmsHomeAdvertise;
import com.zj.blog.marking.entity.SmsRedPacket;
import com.zj.blog.pms.entity.PmsProductAttributeCategory;
import lombok.Data;

import java.util.List;

/**
 * Created by Administrator on 2017/10/18 0018.
 */
@Data
public class IndexData {
    private List<TArticleDO> module_list;
    private List<SmsHomeAdvertise> banner_list;
    private List<TArticleDO> nav_icon_list;
    private List<PmsProductAttributeCategory> cat_list;
    private int cat_goods_cols;
    private List<TArticleDO> block_list;
    private List<SmsCoupon> coupon_list;
    private List<CmsSubject> subjectList;

    private List<SmsRedPacket> redPacketList;


}
