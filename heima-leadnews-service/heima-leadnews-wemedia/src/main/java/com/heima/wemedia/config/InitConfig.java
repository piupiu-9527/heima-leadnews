package com.heima.wemedia.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @description:  扫描降级代码类的包
 * @ClassName: InitConfig
 * @author: Zle
 * @date: 2022-06-28 18:48
 * @version 1.0
*/
@Configuration
@ComponentScan("com.heima.apis.article.fallback")
public class InitConfig {
}
