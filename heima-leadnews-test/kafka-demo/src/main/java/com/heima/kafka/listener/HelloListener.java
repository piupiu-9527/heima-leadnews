package com.heima.kafka.listener;

import com.alibaba.fastjson.JSON;
import com.heima.kafka.pojo.User;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import java.time.chrono.IsoChronology;

/**
 * @description: TODO 消息消费者
 * @ClassName: HelloListener
 * @author: Zle
 * @date: 2022-07-02 16:38
 * @version 1.0
*/
@Component
public class HelloListener {

    @KafkaListener(topics = "itcast-topic")
    public void onMessage(String message){
        if (!StringUtils.isEmpty(message)){
            System.out.println(message);
        }
    }

    @KafkaListener(topics = "user-topic")
    public void onMessage1(String message){
        if (!StringUtils.isEmpty(message)){
            User user = JSON.parseObject(message, User.class);
            System.out.println(user);
        }
    }
}
