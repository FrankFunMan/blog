/**
 * 
 */
package com.zj.blog.sys.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author 951449465@qq.com
 * @Time 2017年9月6日
 * @description
 * 
 */
@Service
public interface GeneratorService {
	Page page(Page page);

	byte[] generatorCode(String[] tableNames);
}
