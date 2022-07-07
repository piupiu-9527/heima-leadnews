package com.heima.behavior.controller.v1;

import com.heima.behavior.service.ReadService;
import com.heima.model.behavior.dtos.ReadBehaviorDto;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description:   
 * @ClassName: ReadController
 * @author: Zle
 * @date: 2022-07-04 10:54
 * @version 1.0
*/
@RestController
@RequestMapping("/api/v1/read_behavior")
public class ReadController {

    @Autowired
    private ReadService readService;

    @PostMapping
    public ResponseResult readBehavior(@RequestBody ReadBehaviorDto dto){
        return readService.readBehavior(dto);
    }
}
