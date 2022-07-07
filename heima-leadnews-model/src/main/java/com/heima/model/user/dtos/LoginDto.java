package com.heima.model.user.dtos;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
* @description:
* @ClassName LoginDto
* @author Zle
* @date 2022-06-22 19:11
* @version 1.0
*/
@Data
@ApiModel("用户登录DTO")
public class LoginDto {

    /**
     * 手机号
     */
    @ApiModelProperty(value="手机号",required = true)
    private String phone;

    /**
     * 密码
     */
    @ApiModelProperty(value="密码",required = true)
    private String password;
}
