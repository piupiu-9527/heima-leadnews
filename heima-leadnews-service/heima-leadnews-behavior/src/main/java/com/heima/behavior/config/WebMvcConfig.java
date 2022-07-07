package com.heima.behavior.config;


import com.heima.behavior.interceptor.AppTokenInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @description: TODO
 * @ClassName: WebMvcConfig
 * @author: Zle
 * @date: 2022-07-04 15:06
 * @version 1.0
*/
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AppTokenInterceptor()).addPathPatterns("/**");
    }
}
