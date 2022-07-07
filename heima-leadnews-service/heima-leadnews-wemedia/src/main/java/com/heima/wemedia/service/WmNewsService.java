package com.heima.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmNewsDto;
import com.heima.model.wemedia.dtos.WmNewsPageReqDto;
import com.heima.model.wemedia.pojos.WmNews;

/**
 * @description:
 * @ClassName: WmNewsService
 * @author: Zle
 * @date: 2022-06-25 23:41
 * @version 1.0
*/
public interface WmNewsService extends IService<WmNews> {
    
    /**
     * @description: 查询文章
     * @param: [dto]
     * @return: com.heima.model.common.dtos.ResponseResult
     * @author Zle
     * @date: 2022-06-26 20:47
    */
    ResponseResult findList(WmNewsPageReqDto dto);

    /**
     * @description: 发布文章或保存草稿
     * @author Zle
     * @date 2022/6/27 10:03
     * @param dto
     * @return ResponseResult
     */
    ResponseResult submitNews(WmNewsDto dto);

    /**
     * @description: 文章的上下架
     * @author Zle
     * @date 2022/7/2 18:25
     * @param dto
     * @return ResponseResult
     */
    public ResponseResult downOrUp(WmNewsDto dto);
}
