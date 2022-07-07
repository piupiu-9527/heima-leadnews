package com.heima.common.constans;
/**
* @description:
* @ClassName ArticleConstants
* @author Zle
* @date 2022-06-23 18:41
* @version 1.0
*/
public class ArticleConstants {
    public static final Short LOADTYPE_LOAD_MORE = 1; //1为加载更多
    public static final Short LOADTYPE_LOAD_NEW = 2; //2为加载最新
    public static final String DEFAULT_TAG = "__all__"; //all为所有

    public static final Integer HOT_ARTICLE_READ_WEIGHT = 1;
    public static final Integer HOT_ARTICLE_LIKE_WEIGHT = 3;
    public static final Integer HOT_ARTICLE_COMMENT_WEIGHT = 5;
    public static final Integer HOT_ARTICLE_COLLECTION_WEIGHT = 8;

    //新增同步数据的topic
    public static final String ARTICLE_ES_SYNC_TOPIC = "article.es.sync.topic";

    //新增常量
    public static final String HOT_ARTICLE_FIRST_PAGE = "hot_article_first_page_";
}
