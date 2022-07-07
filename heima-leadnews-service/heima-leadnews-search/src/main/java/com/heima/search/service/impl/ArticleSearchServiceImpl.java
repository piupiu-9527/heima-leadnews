package com.heima.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.search.dtos.UserSearchDto;
import com.heima.model.user.pojos.ApUser;
import com.heima.search.service.ApUserSearchService;
import com.heima.search.service.ArticleSearchService;
import com.heima.utils.thread.AppThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @description: TODO
 * @ClassName: ArticleSearchServiceImpl
 * @author: Zle
 * @date: 2022-07-02 20:08
 * @version 1.0
*/
@Service
@Slf4j
public class ArticleSearchServiceImpl implements ArticleSearchService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private ApUserSearchService apUserSearchService;

    /**
     * @description: 文章分页搜索
     * @author Zle
     * @date 2022/7/2 20:09
     * @param userSearchDto
     * @return ResponseResult
     */
    @Override
    public ResponseResult search(UserSearchDto userSearchDto) {
        String searchWords = userSearchDto.getSearchWords();
        //1 参数检查
        if (userSearchDto == null || StringUtils.isBlank(searchWords)) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_REQUIRE);
        }

        //浏览器发送请求到达Tomcat，会自动创建一个新的线程来执行代码
        //主线程：拦截器-》controller-》service
        ApUser user = AppThreadLocalUtil.getUser();

        //异步调用 保存搜索记录
        //apUserSearchService.insert(userSearchDto.getSearchWords(), user.getId());
        //添加一个判断，如果是游客不保存搜索记录
        if(user != null && user.getId() != 0 && userSearchDto.getFromIndex() == 0) {
            //@Async就会创建子线程，因此在这个方法内部无法获取到用户ID
            apUserSearchService.insert(userSearchDto.getSearchWords(), user.getId());

            //new Thread(new Runnable() {
            //    @Override
            //    public void run() {
            //        apUserSearchService.insert(dto.getSearchWords(), user.getId());
            //    }
            //}).start();
        }

        //2 构建查询执行条件
        SearchRequest searchRequest = new SearchRequest("app_info_article");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        // 构建查询条件和过滤等
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        // 关键词查询
        QueryStringQueryBuilder queryStringQueryBuilder = QueryBuilders.queryStringQuery(searchWords)
                .field("title")
                .field("content")
                .defaultOperator(Operator.OR);
        boolQueryBuilder.must(queryStringQueryBuilder);

        //分页
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(userSearchDto.getPageSize());

        // 按照发布时间倒序
        searchSourceBuilder.sort("publishTime", SortOrder.DESC);
        // 高亮 三要素
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title");
        highlightBuilder.preTags("<font style='color: red; font-size: inherit;'>");
        highlightBuilder.postTags("</font>");
        searchSourceBuilder.highlighter(highlightBuilder);

        searchSourceBuilder.query(boolQueryBuilder);

        searchRequest.source(searchSourceBuilder);
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            //3 解析结果 封装结果
            SearchHits searchHits = searchResponse.getHits();
            List<Map> resultList = new ArrayList<>();
            // 总记录数
            long total = searchHits.getTotalHits().value;
            log.info("search result total:{}",total);
            SearchHit[] hits = searchHits.getHits();
            for (SearchHit hit : hits) {
                String jsonString = hit.getSourceAsString();
                Map apArticle = JSON.parseObject(jsonString, Map.class);
                Text[] titles = new Text[0];

                if (hit.getHighlightFields() != null && hit.getHighlightFields().size()>0) {
                    // 获取高亮结果集
                    titles = hit.getHighlightFields().get("title").getFragments();
                    //[“标题前边”，“标题后边”...]
                    String title = StringUtils.join(titles);
                    //高亮标题
                    apArticle.put("title", title);
                }else {
                    // 保留原始标题
                    apArticle.put("title", apArticle.get("title"));
                }
                //["123", "456"] -> 123456
                String title = StringUtils.join(titles);
                if (StringUtils.isNotBlank(title)) {
                    apArticle.put("h_title", title);
                } else {
                    apArticle.put("h_title", apArticle.get("title"));
                }

                resultList.add(apArticle);
            }

            return ResponseResult.okResult(resultList);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("search result error:{}",e);
        }

        return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR);
    }

}
