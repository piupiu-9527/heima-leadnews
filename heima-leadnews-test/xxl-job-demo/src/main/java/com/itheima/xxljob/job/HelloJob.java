package com.itheima.xxljob.job;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @description: TODO
 * @ClassName: HelloJob
 * @author: Zle
 * @date: 2022-07-05 18:39
 * @version 1.0
*/
@Component
public class HelloJob {

    @Value("${server.port}")
    private String appPort;

/*    @XxlJob("testDemoJobHandler")
    public void helloJob() {
        //System.out.println("简单任务执行了。。。。" + port);
        System.out.println(new Date() + "简单任务执行了。。。。" );
    }*/

    @XxlJob("shardingJobHandler")
    public void shardingJobHandler() {
        //分片的参数
        //1.当前分片序号(从0开始)，执行器集群列表中当前执行器的序号；
        int shardIndex = XxlJobHelper.getShardIndex();
        //2.总分片数，执行器集群的总机器数量；
        int shardTotal = XxlJobHelper.getShardTotal();

        //业务逻辑
        List<Integer> list = getList();
        for (Integer integer : list) {
            if (integer % shardTotal == shardIndex) {
                System.out.println("当前第" + shardIndex + "分片执行了，任务项为：" + integer);
            }
        }
    }

    public List<Integer> getList() {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            list.add(i);
        }
        return list;
    }
}
