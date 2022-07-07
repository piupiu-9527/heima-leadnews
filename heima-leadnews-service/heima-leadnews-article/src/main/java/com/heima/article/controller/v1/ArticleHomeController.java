package com.heima.article.controller.v1;

import com.heima.article.service.ApArticleService;
import com.heima.common.constans.ArticleConstants;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
* @description: app端文章
* @ClassName ArticleHomeController
* @author Zle
* @date 2022-06-23 18:29
* @version 1.0
*/
@RestController
@RequestMapping("/api/v1/article")
public class ArticleHomeController {

    @Autowired
    private ApArticleService apArticleService;

    /**
    * @description: 当前页
    * @param: [dto]
    * @return: com.heima.model.common.dtos.ResponseResult
    * @author Zle
    * @date: 2022-06-23 18:33
    */
    @PostMapping("load")
    public ResponseResult load(@RequestBody ArticleHomeDto dto){
        //return apArticleService.load(ArticleConstants.LOADTYPE_LOAD_MORE,dto);
        return apArticleService.load2(ArticleConstants.LOADTYPE_LOAD_MORE,dto,true);
    }

    /**
     * @description: 加载更多（下一页）
     * @param: [dto]
     * @return: com.heima.model.common.dtos.ResponseResult
     * @author Zle
     * @date: 2022-06-23 18:33
     */
    @PostMapping("loadmore")
    public ResponseResult loadmore(@RequestBody ArticleHomeDto dto){
        return apArticleService.load(ArticleConstants.LOADTYPE_LOAD_MORE,dto);
    }

    /**
     * @description: 加载最新 （上一页）
     * @param: [dto]
     * @return: com.heima.model.common.dtos.ResponseResult
     * @author Zle
     * @date: 2022-06-23 18:33
     */
    @PostMapping("loadnew")
    public ResponseResult loadnew(@RequestBody ArticleHomeDto dto){
        return apArticleService.load(ArticleConstants.LOADTYPE_LOAD_NEW,dto);
    }


}
