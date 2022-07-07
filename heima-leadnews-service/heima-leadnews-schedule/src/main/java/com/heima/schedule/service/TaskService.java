package com.heima.schedule.service;

import com.heima.model.schedule.pojos.Task;

/**
 * @description: TODO 对外访问接口
 * @ClassName: TaskService
 * @author: Zle
 * @date: 2022-06-30 22:52
 * @version 1.0
*/
public interface TaskService {

    /**
     * @description: 添加任务
     * @author Zle
     * @date 2022/6/30 22:58
     * @param task 任务对象
     * @return long 任务id
     */
    public long addTask(Task task);

    /**
     * @description: 取消任务
     * @author Zle
     * @date 2022/7/1 17:59
     * @param taskId 任务id
     * @return boolean 取消结果
     */
    public boolean cancelTask(long taskId);

    /**
     * @description: 按照类型和优先级拉取任务
     * @author Zle
     * @date 2022/7/1 18:18
     * @param type 类型
     * @param priority 优先级
     * @return Task
     */
    public Task poll(int type, int priority);

}
