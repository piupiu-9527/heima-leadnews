package com.heima.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heima.model.article.pojos.ApArticleContent;
import org.apache.ibatis.annotations.Mapper;

import javax.annotation.ManagedBean;

/**
* @description:
* @ClassName ApArticleContentMapper
* @author Zle
* @date 2022-06-25 00:17
* @version 1.0
*/
@Mapper
public interface ApArticleContentMapper extends BaseMapper<ApArticleContent> {
}
