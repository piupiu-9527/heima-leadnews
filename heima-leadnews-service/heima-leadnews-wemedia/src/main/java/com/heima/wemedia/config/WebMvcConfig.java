package com.heima.wemedia.config;

import com.heima.wemedia.interceptor.WmTokenInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @description:  配置使拦截器生效，拦截所有请求
 * @ClassName: WebMvcConfig
 * @author: Zle
 * @date: 2022-06-25 20:00
 * @version 1.0
*/
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //等同于<mvc:mapping path="/**"/>
        registry.addInterceptor(new WmTokenInterceptor()).addPathPatterns("/**");
    }
}
