package com.zj.blog;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 应用启动入口
 * https://github.com/shenzhuan/mallplus on 2018/4/26.
 */
@SpringBootApplication
@MapperScan("com.zj.blog.*.mapper")
@EnableTransactionManagement
public class BlogAdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(BlogAdminApplication.class, args);
    }
}
