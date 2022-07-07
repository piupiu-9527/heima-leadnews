package com.heima.article.controller.v1;

import com.heima.article.service.ApArticleService;
import com.heima.model.article.dtos.ArticleInfoDto;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description:   
 * @ClassName: ArticleInfoController
 * @author: Zle
 * @date: 2022-07-04 21:12
 * @version 1.0
*/
@RestController
@RequestMapping("/api/v1/article")
public class ArticleInfoController {

    @Autowired
    private ApArticleService apArticleService;

    @PostMapping("/load_article_behavior")
    public ResponseResult loadArticleBehavior(@RequestBody ArticleInfoDto dto){
        return ResponseResult.okResult(apArticleService.loadArticleBehavior(dto));
    }
}
