package com.heima.behavior.service;

import com.heima.model.behavior.dtos.LikesBehaviorDto;
import com.heima.model.common.dtos.ResponseResult;

/**
 * @description: TODO
 * @ClassName: LikesService
 * @author: Zle
 * @date: 2022-07-03 10:28
 * @version 1.0
*/
public interface LikesService {

    /**
     * @description: 点赞
     * @author Zle
     * @date 2022/7/3 10:30
     * @param dto
     * @return ResponseResult
     */
    ResponseResult likeOrUnLike(LikesBehaviorDto dto);
}
