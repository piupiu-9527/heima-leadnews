package com.heima.schedule.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.common.constans.ScheduleConstants;
import com.heima.model.schedule.pojos.Task;
import com.heima.model.schedule.pojos.Taskinfo;
import com.heima.model.schedule.pojos.TaskinfoLogs;
import com.heima.schedule.mapper.TaskinfoLogsMapper;
import com.heima.schedule.mapper.TaskinfoMapper;
import com.heima.common.redis.CacheService;
import com.heima.schedule.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @description:
 * @ClassName: TaskServiceImpl
 * @author: Zle
 * @date: 2022-06-30 23:00
 * @version 1.0
*/
@Service
@Slf4j
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskinfoMapper taskinfoMapper;

    @Autowired
    private TaskinfoLogsMapper taskinfoLogsMapper;

    @Autowired
    private CacheService cacheService;

    /**
     * @description: 保存任务到数据库中
     * @author Zle
     * @date 2022/6/30 23:02
     * @param task 任务对象
     * @return long 任务id
     */
    public Boolean addTaskToDb(Task task) {

        boolean flag = false;

        try {
            //保存任务
            Taskinfo taskinfo = new Taskinfo();
            BeanUtils.copyProperties(task,taskinfo);
            taskinfo.setExecuteTime(new Date(task.getExecuteTime()));
            taskinfoMapper.insert(taskinfo);

            //设置任务id
            task.setTaskId(taskinfo.getTaskId());

            //记录任务的日志
            TaskinfoLogs taskinfoLogs = new TaskinfoLogs();
            BeanUtils.copyProperties(taskinfo,taskinfoLogs);
            taskinfoLogs.setVersion(1);
            taskinfoLogs.setStatus(ScheduleConstants.SCHEDULED);
            taskinfoLogsMapper.insert(taskinfoLogs);
            flag = true;
        } catch (BeansException e) {
            e.printStackTrace();
        }
        //添加任务到redis中
        //2.1 如果任务的执行时间小于当前时间，存入list中
        //2.2 如果任务的执行时间大于当前时间，小于等于预设时间（未来5分钟）存入zset中

        return flag;
    }

    /**
     * @description: 添加任务到缓存中
     * @author Zle
     * @date 2022/6/30 23:41
     * @param task
     */
    private void addTaskCache(Task task){
        String key = task.getTaskType()+"_"+task.getPriority();

        //获取5分钟之后的时间  毫秒值
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE,5);
        long nextScheduleTime = calendar.getTimeInMillis();

        //使用joda time简化时间操作
        //long nextScheduleTime = DateTime.now().plusMinutes(5).getMillis();

        //2.1  如果任务的执行时间小于等于当前时间  存入list
        if(task.getExecuteTime() <= System.currentTimeMillis()){
            cacheService.lLeftPush("topic_"+key, JSON.toJSONString(task));
        }else if(task.getExecuteTime() <= nextScheduleTime){
            //2.2  如果任务的执行时间大于当前时间，小于等于预设时间（未来5分钟）存入zset中
            cacheService.zAdd("future_"+key,JSON.toJSONString(task),task.getExecuteTime());
        }
    }


    /**
     * @description: 添加任务
     * @author Zle
     * @date 2022/7/1 15:52
     * @param task 任务对象
     * @return long
     */
    @Override
    public long addTask(Task task) {
        //1.添加任务到数据库中
        boolean success = addTaskToDb(task);

        //2.添加任务到redis中
        if(success){
            addTaskCache(task);
        }

        return task.getTaskId();
    }

    /**
     * @description: 取消任务
     * @author Zle
     * @date 2022/7/1 18:01
     * @param taskId
     * @return boolean
     */
    @Override
    public boolean cancelTask(long taskId) {

        boolean flag = false;

        //删除任务，更新日志
        Task task = updateDb(taskId, ScheduleConstants.CANCELLED);

        //删除redis的数据
        if(task != null){
            removeTaskFromCache(task);
            flag = true;
        }

        return false;
    }

    /**
     * @description: 按照类型和优先级拉取任务
     * @author Zle
     * @date 2022/7/1 18:19
     * @param type
     * @param priority
     * @return Task
     */
    @Override
    @Transactional
    public Task poll(int type, int priority) {

        Task task = null;

        try {
            String key = type+"_"+priority;

            //pop = get + delete
            String task_json = cacheService.lRightPop(ScheduleConstants.TOPIC + key);

            if (StringUtils.isNotBlank(task_json)){
                task = JSON.parseObject(task_json,Task.class);
                //更新数据库信息
                updateDb(task.getTaskId(),ScheduleConstants.EXECUTED);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("poll task exception");
        }

        return task;
    }

    /**
     * @description: 删除redis中的数据
     * @author Zle
     * @date 2022/7/1 18:10
     * @param task
     */
    private void removeTaskFromCache(Task task) {
        String key = task.getTaskType() + "_" + task.getPriority();

        if (task.getExecuteTime() <= System.currentTimeMillis()){
            //从list中删除
            cacheService.lRemove(ScheduleConstants.TOPIC + key, 0, JSON.toJSONString(task));
        } else {
            //从zset中删除
            cacheService.zRemove(ScheduleConstants.FUTURE + key, JSON.toJSONString(task));
        }
    }

    /**
     * @description: 删除任务taskinfo   使用乐观锁更新任务日志taskinfo_logs
     * @author Zle
     * @date 2022/7/1 18:03
     * @param taskId
     * @param status
     * @return Task
     */
    private Task updateDb(long taskId, int status) {
        Task task = null;

        try {
            //删除任务
            taskinfoMapper.deleteById(taskId);

            //更新任务日志
            //使用乐观锁，必须先进行查询处理
            TaskinfoLogs taskinfoLogs = taskinfoLogsMapper.selectById(taskId);
            taskinfoLogs.setStatus(status);

            //修改时，必须保证把原有的version传过去
            taskinfoLogsMapper.updateById(taskinfoLogs);

            //从Redis中删除数据，不能把整个key删除，因此里边保存了很多任务，
            //删除需要根据两个内容：1.key，2.value
            task = new Task();
            BeanUtils.copyProperties(taskinfoLogs,task);
            task.setExecuteTime(taskinfoLogs.getExecuteTime().getTime());
        } catch (BeansException e) {
            e.printStackTrace();
        }


        return task;
    }

    /**
     * @description: 定时任务  解决抢占251 251行
     * @author Zle
     * @date 2022/7/1 21:22
     */
    @Scheduled(cron = "0 */1 * * * ?") //每分钟执行一次
    public void refresh(){
        log.info("未来数据定时刷新---定时任务");

        //解决集群下的方法抢占执行
        String token = cacheService.tryLock("FUTURE_TASK_SYNC", 1000 * 30);
        if(StringUtils.isNotBlank(token)){
            //获取所有未来数据的集合key
            Set<String> futureKeys = cacheService.scan(ScheduleConstants.FUTURE + "*");
            for (String futureKey : futureKeys) { //future_100_50
                //按照key和分数查询符合条件的数据
                Set<String> tasks = cacheService.zRangeByScore(futureKey, 0, System.currentTimeMillis());
                //同步数据
                if (!tasks.isEmpty()){
                    //获取当前数据的key：topic开头
                    String topicKey = futureKey.replace(ScheduleConstants.FUTURE, ScheduleConstants.TOPIC);

                    cacheService.refreshWithPipeline(futureKey, topicKey, tasks);
                    log.info("成功的将" + futureKey + "刷新到了" + topicKey);
                }
            }
        }

    }



    /**
     * @description: 数据库同步到redis
     * @author Zle
     * @date 2022/7/1 21:43
     */
    public void reloadData(){
        // 清除缓存中原有的数据
        clearCache();

        log.info("数据库数据同步到缓存");

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 5);

        //查看小于未来5分钟的所有任务
        List<Taskinfo> allTasks = taskinfoMapper.selectList(
                Wrappers.<Taskinfo>lambdaQuery().lt(Taskinfo::getExecuteTime, calendar.getTime()));
        if (allTasks != null && allTasks.size() > 0) {
            for (Taskinfo taskinfo : allTasks) {
                Task task = new Task();
                BeanUtils.copyProperties(taskinfo, task);
                task.setExecuteTime(taskinfo.getExecuteTime().getTime());
                addTaskCache(task);
            }
        }
    }

    /**
     * 清理缓存中的数据
     */
    private void clearCache() {
        // 删除缓存中未来数据集合和当前消费者队列的所有key
        // topic_
        Set<String> topickeys = cacheService.scan(ScheduleConstants.TOPIC + "*");
        // future_
        Set<String> futurekeys = cacheService.scan(ScheduleConstants.FUTURE + "*");

        cacheService.delete(futurekeys);
        cacheService.delete(topickeys);
    }
}
