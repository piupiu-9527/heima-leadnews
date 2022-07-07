package com.heima.user;

import com.heima.common.redis.CacheService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
* @description: user引导类
* @ClassName UserApplication
* @author Zle
* @date 2022-06-22 18:42
* @version 1.0
*/
@SpringBootApplication(exclude = {KafkaAutoConfiguration.class})
@EnableDiscoveryClient   //集成当前注册中心   注册中心：eureka, nacos
//@EnableTransactionManagement
@MapperScan("com.heima.user.mapper")
public class UserApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class,args);
    }
}
