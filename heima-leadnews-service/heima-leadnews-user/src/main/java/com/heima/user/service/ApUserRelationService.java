package com.heima.user.service;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.user.dtos.UserRelationDto;

/**
 * @description: TODO
 * @ClassName: ApUserRelationService
 * @author: Zle
 * @date: 2022-07-04 15:43
 * @version 1.0
*/
public interface ApUserRelationService {
    /**
     * 用户关注/取消关注
     * @param dto
     * @return
     */
    public ResponseResult follow(UserRelationDto dto);
}
