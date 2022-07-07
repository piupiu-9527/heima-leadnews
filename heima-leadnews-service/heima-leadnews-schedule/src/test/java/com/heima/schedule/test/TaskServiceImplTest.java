package com.heima.schedule.test;

import com.heima.model.schedule.pojos.Task;
import com.heima.schedule.ScheduleApplication;
import com.heima.schedule.service.TaskService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

/**
 * @description:
 * @ClassName: TaskServiceImplTest
 * @author: Zle
 * @date: 2022-07-01 15:54
 * @version 1.0
*/
@SpringBootTest(classes = ScheduleApplication.class)
@RunWith(SpringRunner.class)
public class TaskServiceImplTest {
    @Autowired
    private TaskService taskService;

    /**
     * @description: 添加redis测试
     * @author Zle
     * @date 2022/7/1 18:13
     */
    @Test
    public void addTask() {
        Task task = new Task();
        task.setTaskType(100);
        task.setPriority(50);
        task.setParameters("task test".getBytes());
        task.setExecuteTime(new Date().getTime() + 500);
        //设置未来5分钟执行
        task.setExecuteTime(new Date().getTime() + 5 * 60 * 1000);

        long taskId = taskService.addTask(task);
        System.out.println(taskId);
    }

    /**
     * @description: 删除redis测试
     * @author Zle
     * @date 2022/7/1 18:14
     */
    @Test
    public void cancelTest() {
        taskService.cancelTask(1542795766877011970L);
    }

    /**
     * @description: 测试拉取任务
     * @author Zle
     * @date 2022/7/1 18:28
     */
    @Test
    public void testPoll() {
        Task task = taskService.poll(100, 50);
        System.out.println(task);
    }

}
