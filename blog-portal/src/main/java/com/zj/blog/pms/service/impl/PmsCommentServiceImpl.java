package com.zj.blog.pms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zj.blog.pms.entity.PmsComment;
import com.zj.blog.pms.mapper.PmsCommentMapper;
import com.zj.blog.pms.service.IPmsCommentService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 商品评价表 服务实现类
 * </p>
 *
 * @author zscat
 * @since 2019-04-19
 */
@Service
public class PmsCommentServiceImpl extends ServiceImpl<PmsCommentMapper, PmsComment> implements IPmsCommentService {

}
