package com.heima.app.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
* @description:
* @ClassName AppGatewayApplication
* @author Zle
* @date 2022-06-22 21:06
* @version 1.0
*/
@SpringBootApplication
@EnableDiscoveryClient  //开启注册中心
public class AppGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(AppGatewayApplication.class,args);
    }
}
