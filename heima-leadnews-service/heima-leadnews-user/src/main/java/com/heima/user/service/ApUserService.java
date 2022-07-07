package com.heima.user.service;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.user.dtos.LoginDto;

/**
* @description:
* @ClassName ApUserService
* @author Zle
* @date 2022-06-22 19:14
* @version 1.0
*/
public interface ApUserService {
    
    /**
    * @description: app端登录
    * @param: [dto]
    * @return: com.heima.model.common.dtos.ResponseResult
    * @author Zle
    * @date: 2022-06-22 19:17
    */
    public ResponseResult login(LoginDto dto);
}
