package com.heima.freemarker.test;

import com.heima.freemarker.FreemarkerDemoApplication;
import com.heima.freemarker.entity.Student;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
* @description:  静态文件生成
* @ClassName FreemarkerTest
* @author Zle
* @date 2022-06-24 18:16
* @version 1.0
*/
@SpringBootTest(classes = FreemarkerDemoApplication.class)
@RunWith(SpringRunner.class)
public class FreemarkerTest {

    @Autowired
    private Configuration configuration;

    @Test
    public void test() throws IOException, TemplateException {
        Template template = configuration.getTemplate("02-list.ftl");

        /**
         * 合成方法
         *
         * 两个参数
         * 第一个参数：模型数据
         * 第二个参数：输出流
         */
        template.process(getData(),new FileWriter("D:/template/list.html"));
    }

    private Map getData(){

        Map<String, Object> map = new HashMap<String, Object>();

        Student stu1 = new Student();
        stu1.setName("小强");
        stu1.setAge(18);
        stu1.setMoney(10086.86f);
        stu1.setBirthday(new Date());

        Student stu2 = new Student();
        stu2.setName("小红");
        stu2.setMoney(154.15f);
        stu2.setAge(16);

        ArrayList<Student> stus = new ArrayList<Student>();
        stus.add(stu2);
        stus.add(stu1);

        //向model中存放List集合数据
        map.put("stus",stus);
        //-------------------------------

        //创建map数据

        HashMap<String, Student> stuMap = new HashMap<>();
        stuMap.put("stu1",stu1);
        stuMap.put("stu2",stu2);


        // 3.1 向model中存放Map数据
        map.put("stuMap",stuMap);

        return map;


    }
}
