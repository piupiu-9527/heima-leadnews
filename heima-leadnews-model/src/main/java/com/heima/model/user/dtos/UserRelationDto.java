package com.heima.model.user.dtos;

import lombok.Data;

/**
 * @description:
 * @ClassName: UserRelationDto
 * @author: Zle
 * @date: 2022-07-04 15:46
 * @version 1.0
*/
@Data
public class UserRelationDto {
    // 文章作者ID
    Integer authorId;

    // 文章id
    Long articleId;
    /**
     * 操作方式
     * 0  关注
     * 1  取消
     */
    Short operation;
}
