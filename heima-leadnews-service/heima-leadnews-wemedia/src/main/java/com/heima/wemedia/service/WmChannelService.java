package com.heima.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.pojos.WmChannel;

/**
 * @description:
 * @ClassName: WmChannelService
 * @author: Zle
 * @date: 2022-06-25 23:29
 * @version 1.0
*/
public interface WmChannelService extends IService<WmChannel> {

    ResponseResult findAll();
}
