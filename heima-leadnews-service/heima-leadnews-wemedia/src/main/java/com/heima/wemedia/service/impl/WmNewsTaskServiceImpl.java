package com.heima.wemedia.service.impl;

import com.alibaba.fastjson.JSON;
import com.heima.apis.schedule.IScheduleClient;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.common.enums.TaskTypeEnum;
import com.heima.model.schedule.pojos.Task;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.utils.common.ProtostuffUtil;
import com.heima.wemedia.service.WmNewsAutoScanService;
import com.heima.wemedia.service.WmNewsTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @description:
 * @ClassName: WmNewsTaskServiceImpl
 * @author: Zle
 * @date: 2022-07-01 22:35
 * @version 1.0
*/
@Service
@Slf4j
public class WmNewsTaskServiceImpl implements WmNewsTaskService {

    @Autowired
    private IScheduleClient scheduleClient;

    @Autowired
    private WmNewsAutoScanService wmNewsAutoScanService;

    /**
     * 添加任务到延迟队列中
     * @param
     */
    @Async
    @Override
    public void addNewsToTask(Integer wmNewsId, Date publishTime) {
        log.info("---------添加任务到延迟队列中------begin--------");

        Task task = new Task();
        task.setExecuteTime(publishTime.getTime());
        task.setTaskType(TaskTypeEnum.NEWS_SCAN_TIME.getTaskType());
        task.setPriority(TaskTypeEnum.NEWS_SCAN_TIME.getPriority());
        //二进制大对象：将Java对象序列化成二进制数组
        WmNews wmNews = new WmNews();
        wmNews.setId(wmNewsId);

        task.setParameters(ProtostuffUtil.serialize(wmNews));

        scheduleClient.addTask(task);
        log.info("----------添加任务到延迟队列中------end-------------");
    }

    /**
     * 消费延迟队列数据
     */
    @Scheduled(fixedRate = 1000) //每隔一秒执行一次
    @Override
    public void scanNewsByTask() {
        log.info("消费任务，审核文章");
        //自媒体微服务 -》 远程调用 -》延迟微服务 -> Redis的List集合中拉取任务
        ResponseResult responseResult = scheduleClient.poll(
                TaskTypeEnum.NEWS_SCAN_TIME.getTaskType(), TaskTypeEnum.NEWS_SCAN_TIME.getPriority());
        if(responseResult.getCode().equals(AppHttpCodeEnum.SUCCESS.getCode())
                && responseResult.getData() != null){
            Task task = JSON.parseObject(JSON.toJSONString(responseResult.getData()), Task.class);
            //将数据库中存储的二进制文章内容反序列化成Java对象
            WmNews wmNews = ProtostuffUtil.deserialize(task.getParameters(), WmNews.class);
            log.info("执行任务ID：" + task.getTaskId() + ", 审核文章ID：" + wmNews.getId());
            //自媒体微服务 -》 远程调用 -》文章微服务 -》 完成文章审核（文本+图片阿里云审核）
            wmNewsAutoScanService.autoScanWmNews(wmNews.getId());
        }
    }
}
