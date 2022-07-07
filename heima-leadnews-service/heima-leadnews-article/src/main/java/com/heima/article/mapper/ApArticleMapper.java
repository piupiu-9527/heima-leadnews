package com.heima.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.article.pojos.ApArticle;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;

import java.util.Date;
import java.util.List;

/**
* @description:
* @ClassName ApArticleMapper
* @author Zle
* @date 2022-06-23 18:35
* @version 1.0
*/
@Mapper
public interface ApArticleMapper extends BaseMapper<ApArticle> {
    public List<ApArticle> loadApArticleList(@Param("dto") ArticleHomeDto dto, @Param("type") Short type);


    //2021/11/25 14:21 -> 2021/11/20 14:21
    //where publish_time >= '2021/11/20 14:21'
    public List<ApArticle> findArticleListByLast5days(@Param("dayParam") Date dayParam);
}
