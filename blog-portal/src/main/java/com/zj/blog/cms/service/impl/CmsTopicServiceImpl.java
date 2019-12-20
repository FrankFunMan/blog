package com.zj.blog.cms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zj.blog.cms.entity.CmsTopic;
import com.zj.blog.cms.mapper.CmsTopicMapper;
import com.zj.blog.cms.service.ICmsTopicService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 话题表 服务实现类
 * </p>
 *
 * @author zscat
 * @since 2019-04-17
 */
@Service
public class CmsTopicServiceImpl extends ServiceImpl<CmsTopicMapper, CmsTopic> implements ICmsTopicService {

}
