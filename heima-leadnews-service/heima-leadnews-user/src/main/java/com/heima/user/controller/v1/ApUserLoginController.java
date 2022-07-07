package com.heima.user.controller.v1;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.user.dtos.LoginDto;
import com.heima.user.service.ApUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
* @description:  用户登录
* @ClassName ApUserLoginController
* @author Zle
* @date 2022-06-22 19:00
* @version 1.0
*/
@RestController
@RequestMapping("/api/v1/login")
@Api(value = "app端用户", tags = "ap_user", description = "app端用户API")
public class ApUserLoginController {

    @Autowired
    private ApUserService apUserService;

    @PostMapping("/login_auth")
    @ApiOperation("用户登录")
    public ResponseResult login(@RequestBody LoginDto dto){
        return apUserService.login(dto);
    }

}
