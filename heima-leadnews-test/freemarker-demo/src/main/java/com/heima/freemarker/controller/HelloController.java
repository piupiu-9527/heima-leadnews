package com.heima.freemarker.controller;

import com.heima.freemarker.entity.Student;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
* @description:
* @ClassName HelloController
* @author Zle
* @date 2022-06-23 23:28
* @version 1.0
*/
@Controller
public class HelloController {
    @GetMapping("/basic")
    public String test(Model model) {

        //纯文本形式的参数
        model.addAttribute("name","freemarker");

        //实体类相关的参数
        Student student = new Student();
        student.setName("张三");
        student.setAge(18);
        model.addAttribute("stu",student);
        return "01-basic";
    }
    
    @GetMapping("list")
    public String list(Model model){

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
        model.addAttribute("stus",stus);

        //-------------------------------

        //创建map数据

         HashMap<String, Student> map = new HashMap<>();
         map.put("stu1",stu1);
         map.put("stu2",stu2);


        // 3.1 向model中存放Map数据
        model.addAttribute("map",map);

          return "02-list";
          
         
    }
}
