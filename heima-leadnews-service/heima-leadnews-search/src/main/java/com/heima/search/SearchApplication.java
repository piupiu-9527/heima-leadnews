/*package com.heima.search;

import com.heima.common.redis.CacheService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.scheduling.annotation.EnableAsync;

//@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, CacheService.class})
@SpringBootApplication
@EnableDiscoveryClient
@EnableAsync
@ComponentScan(excludeFilters  = {@ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE, classes = {DataSourceAutoConfiguration.class, CacheService.class})})
public class SearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(SearchApplication.class,args);;
    }
}*/

package com.heima.search;

import com.heima.common.redis.CacheService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
//@SpringBootApplication(exclude =
//        {DataSourceAutoConfiguration.class, CacheService.class})
@EnableDiscoveryClient
@EnableAsync
//@ComponentScan(excludeFilters  = {@ComponentScan.Filter(
//        type = FilterType.ASSIGNABLE_TYPE,
//        classes = {DataSourceAutoConfiguration.class, CacheService.class})})
public class SearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(SearchApplication.class,args);
    }
}
