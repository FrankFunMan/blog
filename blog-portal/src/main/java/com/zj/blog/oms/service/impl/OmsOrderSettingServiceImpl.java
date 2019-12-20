package com.zj.blog.oms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zj.blog.oms.entity.OmsOrderSetting;
import com.zj.blog.oms.mapper.OmsOrderSettingMapper;
import com.zj.blog.oms.service.IOmsOrderSettingService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单设置表 服务实现类
 * </p>
 *
 * @author zscat
 * @since 2019-04-17
 */
@Service
public class OmsOrderSettingServiceImpl extends ServiceImpl<OmsOrderSettingMapper, OmsOrderSetting> implements IOmsOrderSettingService {

}
