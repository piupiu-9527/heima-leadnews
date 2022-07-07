package com.heima.common.constans;
/**
 * @description:
 * @ClassName: WemediaConstants
 * @author: Zle
 * @date: 2022-06-27 10:07
 * @version 1.0
*/
public class WemediaConstants {

    public static final Short COLLECT_MATERIAL = 1;//收藏

    public static final Short CANCEL_COLLECT_MATERIAL = 0;//取消收藏

    public static final String WM_NEWS_TYPE_IMAGE = "image";

    //封面-》无图
    public static final Short WM_NEWS_NONE_IMAGE = 0;
    //封面-》单图
    public static final Short WM_NEWS_SINGLE_IMAGE = 1;
    //封面-》三图
    public static final Short WM_NEWS_MANY_IMAGE = 3;
    //封面-》自动（使用文章内容的图片做为封面）
    public static final Short WM_NEWS_TYPE_AUTO = -1;

    public static final Short WM_CONTENT_REFERENCE = 0;
    public static final Short WM_COVER_REFERENCE = 1;
}
