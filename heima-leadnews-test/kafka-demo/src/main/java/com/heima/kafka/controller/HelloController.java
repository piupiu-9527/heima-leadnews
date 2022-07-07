package com.heima.kafka.controller;

import com.alibaba.fastjson.JSON;
import com.heima.kafka.pojo.User;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description: 消息生产者
 * @ClassName: HelloController
 * @author: Zle
 * @date: 2022-07-02 16:38
 * @version 1.0
*/
@RestController
public class HelloController {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @GetMapping("/hello")
    public String hello(){
        kafkaTemplate.send("itcast-topic","黑马程序员");
        return "ok";
    }

    @GetMapping("helloenity")
    public String helloEnity(){
        User user = new User();
        user.setName("zhangsan");
        user.setAge(18);

        kafkaTemplate.send("user-topic", JSON.toJSONString(user));
        return "ok";

    }
}
