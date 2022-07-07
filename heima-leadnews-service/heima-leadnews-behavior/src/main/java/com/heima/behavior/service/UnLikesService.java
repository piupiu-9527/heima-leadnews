package com.heima.behavior.service;

import com.heima.model.behavior.dtos.UnLikesBehaviorDto;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @description:
 * @ClassName: UnLikeService
 * @author: Zle
 * @date: 2022-07-04 14:23
 * @version 1.0
*/
public interface UnLikesService {

    public ResponseResult Unlike( UnLikesBehaviorDto dto);
}
