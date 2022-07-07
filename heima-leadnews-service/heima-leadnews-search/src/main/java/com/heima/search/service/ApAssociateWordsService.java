package com.heima.search.service;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.search.dtos.UserSearchDto;

/**
 * @description:  联想词表 服务类
 * @ClassName: ApAssociateWordsService
 * @author: Zle
 * @date: 2022-07-03 20:17
 * @version 1.0
*/
public interface ApAssociateWordsService {

    /**
     联想词
     @param userSearchDto
     @return
     */
    ResponseResult findAssociate(UserSearchDto userSearchDto);

}
