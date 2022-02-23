package com.wu.config.security;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Mybatis分页配置
 * @author cxj
 */
@Configuration
public class MybatisPulsConfig {

    @Bean
    public PaginationInterceptor PaginationInterceptor(){
        return  new PaginationInterceptor();
    }
}
