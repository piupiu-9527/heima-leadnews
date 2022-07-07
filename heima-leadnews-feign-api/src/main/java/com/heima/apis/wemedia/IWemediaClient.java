package com.heima.apis.wemedia;

import com.heima.model.common.dtos.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @description: TODO
 * @ClassName: IWemediaClient
 * @author: Zle
 * @date: 2022-07-05 20:12
 * @version 1.0
*/
@FeignClient("leadnews-wemedia")
public interface IWemediaClient {

    @GetMapping("/api/v1/channel/list")
    public ResponseResult getChannels();
}
