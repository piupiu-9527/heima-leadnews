package com.heima.article.test;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.article.ArticleApplication;
import com.heima.article.mapper.ApArticleContentMapper;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.article.service.ApArticleService;
import com.heima.file.service.FileStorageService;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.pojos.ApArticleContent;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;

/**
* @description:
* @ClassName ActicleFreemarkerTest
* @author Zle
* @date 2022-06-25 00:10
* @version 1.0
*/
@SpringBootTest(classes = ArticleApplication.class)
@RunWith(SpringRunner.class)
public class ActicleFreemarkerTest {

    @Autowired
    private ApArticleContentMapper apArticleContentMapper;

    @Autowired
    private ApArticleMapper apArticleMapper;

    @Autowired
    private Configuration configuration;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private ApArticleService apArticleService;
    @Test
    public void createStaticUrlTest() throws Exception {
        //已知文章ID，通过文章id获取文章内容
        //4.1 获取文章内容  根据文章id不是文章内容表的id  article_id
        ApArticleContent apArticleContent = apArticleContentMapper.selectOne(Wrappers.<ApArticleContent>lambdaQuery().eq(ApArticleContent::getArticleId, "1302865474094120961L"));
        if (apArticleContent !=null && StringUtils.isNotBlank(apArticleContent.getContent())){
            //4.2 文章内容通过freemarker生成html文件
            Template template = configuration.getTemplate("article.ftl");

            //数据模型
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("content", JSONArray.parseArray(apArticleContent.getContent()));
            StringWriter out = new StringWriter();

            //合成
            template.process(params,out);

            InputStream in = new ByteArrayInputStream(out.toString().getBytes());

            //4.3 把html文件上传到minio中
            String path = fileStorageService.uploadHtmlFile("", apArticleContent.getArticleId() + ".html", in);

            //4.4 修改ap_article表，保存static_url字段
            ApArticle article = new ApArticle();
            article.setId(apArticleContent.getArticleId());
            article.setStaticUrl(path);
            apArticleMapper.updateById(article);

        }

    }
}
