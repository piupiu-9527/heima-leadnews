package com.heima.model.article.vo;

import com.heima.model.article.pojos.ApArticle;
import lombok.Data;

/**
 * @description: TODO
 * @ClassName: HotArticleVo
 * @author: Zle
 * @date: 2022-07-05 20:03
 * @version 1.0
*/
@Data
public class HotArticleVo extends ApArticle {
    /**
     * 分值
     */
    private Integer score;
}
