package com.heima.article.test;

import com.heima.article.ArticleApplication;
import com.heima.article.service.HotArticleService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @description:   
 * @ClassName: HotArticleServiceTest
 * @author: Zle
 * @date: 2022-07-05 20:37
 * @version 1.0
*/
@SpringBootTest(classes = ArticleApplication.class)
@RunWith(SpringRunner.class)
public class HotArticleServiceTest {

    @Autowired
    private HotArticleService hotArticleService;

    @Test
    public void testComputeHotArticle(){
        hotArticleService.computeHotArticle();
    }
}
