package com.heima.wemedia.service;
/**
 * @description:
 * @ClassName: WmNewsAutoScanService
 * @author: Zle
 * @date: 2022-06-27 21:23
 * @version 1.0
*/
public interface WmNewsAutoScanService {

    /**
     * @description: 自媒体文章审核
     * @author Zle
     * @date 2022/6/27 21:24
     * @param wmNewsId 自媒体文章id
     */
    void autoScanWmNews(Integer wmNewsId); //wm_news.wmNewsId
}
