package com.heima.wemedia.controller.v1;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.wemedia.service.WmChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description: 自媒体文章
 * @ClassName: WmchannelController
 * @author: Zle
 * @date: 2022-06-25 23:27
 * @version 1.0
*/
@RestController
@RequestMapping("/api/v1/channel")
public class WmchannelController {
    
    @Autowired
    private WmChannelService wmChannelService;
    
    /**
     * @description: 查询所有频道
     * @param: []
     * @return: com.heima.model.common.dtos.ResponseResult
     * @author Zle
     * @date: 2022-06-25 23:35
    */
    @GetMapping("/channels")
    public ResponseResult findAll(){
        return ResponseResult.okResult(wmChannelService.list());
    }
}
