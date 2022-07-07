package com.heima.behavior.controller.v1;

import com.heima.behavior.service.LikesService;
import com.heima.behavior.service.UnLikesService;
import com.heima.model.behavior.dtos.LikesBehaviorDto;
import com.heima.model.behavior.dtos.UnLikesBehaviorDto;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description:    点赞
 * @ClassName: LikesController
 * @author: Zle
 * @date: 2022-07-03 10:22
 * @version 1.0
*/
@RestController
@RequestMapping("/api/v1/un_likes_behavior")
public class UnLikesController {

    @Autowired
    private UnLikesService unLikesService;

    @PostMapping
    public ResponseResult Unlike(@RequestBody UnLikesBehaviorDto dto){
        return unLikesService.Unlike(dto);
    }
}
