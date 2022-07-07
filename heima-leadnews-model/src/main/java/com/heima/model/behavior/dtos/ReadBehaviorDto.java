package com.heima.model.behavior.dtos;

import lombok.Data;

/**
 * @description:
 * @ClassName: ReadBehaviorDto
 * @author: Zle
 * @date: 2022-07-04 10:56
 * @version 1.0
*/
@Data
public class ReadBehaviorDto {

    /**
     * 文章id
     */
    private Long articleId;

    /**
     * 阅读次数 , 默认加一
     */
    private Integer count;
}
