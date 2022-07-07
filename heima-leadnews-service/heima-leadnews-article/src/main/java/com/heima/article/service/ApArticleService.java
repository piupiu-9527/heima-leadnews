package com.heima.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.article.dtos.ArticleInfoDto;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.common.dtos.ResponseResult;

/**
* @description:
* @ClassName ApArticleService
* @author Zle
* @date 2022-06-23 18:36
* @version 1.0
*/
public interface ApArticleService extends IService<ApArticle> {

    /**
    * @description: 根据参数查询文章列表
    * @param: [loadtype(1,加载更多，2，加载最新), dto]
    * @return: com.heima.model.common.dtos.ResponseResult
    * @author Zle
    * @date: 2022-06-23 19:13
    */
    ResponseResult load(Short loadtype, ArticleHomeDto dto);

    /**
     * @description: 保存APP端相关文章
     * @author Zle
     * @date 2022/6/27 16:45
     * @param dto
     * @return ResponseResult
     */
    ResponseResult saveArticle(ArticleDto dto);

    /**
     * @description: 数据回显
     * @author Zle
     * @date 2022/7/4 21:21
     * @param dto
     * @return ResponseResult
     */
    public ResponseResult loadArticleBehavior(ArticleInfoDto dto);

    /**
     * 根据参数加载文章列表  v2
     * @param loadtype  1 加载更多   2 加载最新
     * @param dto
     * @param firstPage  是否是首页
     * @return
     */
    public ResponseResult load2(Short loadtype, ArticleHomeDto dto,boolean firstPage);
}
