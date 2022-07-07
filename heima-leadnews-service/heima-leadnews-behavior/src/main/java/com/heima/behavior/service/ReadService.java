package com.heima.behavior.service;

import com.heima.model.behavior.dtos.ReadBehaviorDto;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @description: TODO
 * @ClassName: ReadService
 * @author: Zle
 * @date: 2022-07-04 11:20
 * @version 1.0
*/
public interface ReadService {

    ResponseResult readBehavior(ReadBehaviorDto dto);
}
