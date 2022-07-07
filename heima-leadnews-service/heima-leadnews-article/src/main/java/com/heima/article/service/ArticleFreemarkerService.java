package com.heima.article.service;

import com.heima.model.article.pojos.ApArticle;

/**
 * @description: TODO 生成静态文件上传到minio中
 * @ClassName: ArticleFreemarkerService
 * @author: Zle
 * @date: 2022-06-29 19:24
 * @version 1.0
*/
public interface ArticleFreemarkerService {

    /**
     * @description: 生成静态文件上传到minIO中
     * @author Zle
     * @date 2022/6/29 19:26
     * @param apArticle
     * @param content
     */
    void buildArticleToMinIO(ApArticle apArticle, String content);
}
