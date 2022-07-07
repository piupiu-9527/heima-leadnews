package com.heima.search.service;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.search.dtos.UserSearchDto;

import java.io.IOException;

/**
 * @description:
 * @ClassName: ArticleSearchService
 * @author: Zle
 * @date: 2022-07-02 20:07
 * @version 1.0
*/
public interface ArticleSearchService {
    /**
     ES文章分页搜索
     @return
     */
    ResponseResult search(UserSearchDto userSearchDto) ;
}
