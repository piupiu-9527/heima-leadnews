package com.heima.model.article.dtos;

import com.heima.model.article.pojos.ApArticle;
import lombok.Data;

/**
 * @description:
 * @ClassName: ArticleDto
 * @author: Zle
 * @date: 2022-06-27 16:37
 * @version 1.0
*/
@Data
public class ArticleDto extends ApArticle {

    /**
     * 文章内容
     */
    private String content;
}
