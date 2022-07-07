package com.heima.model.behavior.dtos;

import lombok.Data;

/**
 * @description:  点赞或取消
 * @ClassName: LikesBehaviorDto
 * @author: Zle
 * @date: 2022-07-03 10:17
 * @version 1.0
*/
@Data
public class LikesBehaviorDto {

    private Long articleId;

    private short operation;

    private short type;
}
