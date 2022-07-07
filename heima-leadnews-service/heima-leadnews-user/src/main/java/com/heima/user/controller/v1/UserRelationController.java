package com.heima.user.controller.v1;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.user.dtos.UserRelationDto;
import com.heima.user.service.ApUserRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description: TODO
 * @ClassName: UserRelationController
 * @author: Zle
 * @date: 2022-07-04 15:42
 * @version 1.0
*/
@RestController
@RequestMapping("/api/v1/user")
public class UserRelationController {
    @Autowired
    private ApUserRelationService apUserRelationService;

    @PostMapping("/user_follow")
    public ResponseResult follow(@RequestBody UserRelationDto dto){
        return apUserRelationService.follow(dto);
    }
}
