package com.heima.freemarker.entity;

import lombok.Data;

import java.util.Date;

/**
* @description:
* @ClassName Student
* @author Zle
* @date 2022-06-23 20:07
* @version 1.0
*/
@Data
public class Student {
    private String name;//姓名
    private int age;//年龄
    private Date birthday;//生日
    private Float money;//钱包
}
