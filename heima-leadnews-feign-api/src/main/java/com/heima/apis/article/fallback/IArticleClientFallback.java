package com.heima.apis.article.fallback;

import com.aliyuncs.IAcsClient;
import com.heima.apis.article.IArticleClient;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @description:    服务降级处理逻辑  feign失败配置
 * @ClassName: IArticleClientFallback
 * @author: Zle
 * @date: 2022-06-28 18:43
 * @version 1.0
*/
@Component
@Slf4j
public class IArticleClientFallback implements IArticleClient {
    @Override
    public ResponseResult saveArticle(ArticleDto dto) {
        log.error("远程调用文章微服务失败，服务降级！");
        //Redis，微信，短信 ——》 推送错误消息给运维人员，尽快检查服务器
        return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR,"获取数据失败");
    }
}
