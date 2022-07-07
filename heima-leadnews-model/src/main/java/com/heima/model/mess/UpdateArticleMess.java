package com.heima.model.mess;

import lombok.Data;

/**
 * @description: TODO
 * @ClassName: UpdateArticleMess
 * @author: Zle
 * @date: 2022-07-03 10:10
 * @version 1.0
*/
@Data
public class UpdateArticleMess {
    /**
     * 修改文章的字段类型
     */
    private UpdateArticleType type;
    /**
     * 文章ID
     */
    private Long articleId;
    /**
     * 修改数据的增量，可为正负
     */
    private Integer add;

    public enum UpdateArticleType{
        COLLECTION,COMMENT,LIKES,VIEWS;
    }
}
