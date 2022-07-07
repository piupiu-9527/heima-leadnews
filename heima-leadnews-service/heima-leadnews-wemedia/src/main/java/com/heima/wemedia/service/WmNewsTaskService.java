package com.heima.wemedia.service;

import com.heima.model.wemedia.pojos.WmNews;

import java.util.Date;

/**
 * @description:
 * @ClassName: WmNewsTaskService
 * @author: Zle
 * @date: 2022-07-01 22:21
 * @version 1.0
*/
public interface WmNewsTaskService {

    /**
     * 添加任务到延迟队列中
     * @param
     */
    public void addNewsToTask(Integer wmNewsId,Date publishTime);

    /**
     * 消费延迟队列数据
     */
    public void scanNewsByTask();
}
