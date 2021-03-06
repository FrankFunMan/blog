package com.zj.blog.pms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zj.blog.pms.entity.PmsProductLadder;
import com.zj.blog.pms.mapper.PmsProductLadderMapper;
import com.zj.blog.pms.service.IPmsProductLadderService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 产品阶梯价格表(只针对同商品) 服务实现类
 * </p>
 *
 * @author zscat
 * @since 2019-04-19
 */
@Service
public class PmsProductLadderServiceImpl extends ServiceImpl<PmsProductLadderMapper, PmsProductLadder> implements IPmsProductLadderService {

}
