package com.heima.search.service;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.search.dtos.HistorySearchDto;

/**
 * @description: TODO
 * @ClassName: ApUserSearchService
 * @author: Zle
 * @date: 2022-07-03 17:01
 * @version 1.0
*/
public interface ApUserSearchService {

    /**
     * 保存用户搜索历史记录
     * @param keyword
     * @param userId
     */
    public void insert(String keyword, Integer userId);

    /**
     查询搜索历史
     @return
     */
    ResponseResult findUserSearch();

    /**
     删除搜索历史
     @param historySearchDto
     @return
     */
    ResponseResult delUserSearch(HistorySearchDto historySearchDto);
}
