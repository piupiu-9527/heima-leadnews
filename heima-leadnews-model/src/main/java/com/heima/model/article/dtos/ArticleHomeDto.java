package com.heima.model.article.dtos;

import lombok.Data;

import java.util.Date;

/**
* @description:
* @ClassName ArticleHomeDto
* @author Zle
* @date 2022-06-23 10:58
* @version 1.0
*/
@Data
public class ArticleHomeDto {

    // 最大时间
    Date maxBehotTime;
    // 最小时间
    Date minBehotTime;
    // 分页size
    Integer size;
    // 频道ID
    String tag;

}
