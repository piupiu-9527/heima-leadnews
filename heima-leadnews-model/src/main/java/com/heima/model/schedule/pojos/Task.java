package com.heima.model.schedule.pojos;

import lombok.Data;

import java.io.Serializable;

/**
 * @description:
 * @ClassName: Task
 * @author: Zle
 * @date: 2022-06-30 22:56
 * @version 1.0
*/
@Data
public class Task implements Serializable {

    /**
     * 任务id
     */
    private Long taskId;
    /**
     * 类型
     */
    private Integer taskType;

    /**
     * 优先级
     */
    private Integer priority;

    /**
     * 执行时间:毫秒数
     */
    private long executeTime;

    /**
     * task参数：longblob
     */
    private byte[] parameters;
}
