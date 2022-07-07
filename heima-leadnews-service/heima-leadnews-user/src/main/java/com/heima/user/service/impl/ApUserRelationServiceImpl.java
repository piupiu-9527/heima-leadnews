package com.heima.user.service.impl;

import com.heima.common.constans.BehaviorConstants;
import com.heima.common.redis.CacheService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.dtos.UserRelationDto;
import com.heima.model.user.pojos.ApUser;
import com.heima.user.service.ApUserRelationService;
import com.heima.utils.thread.AppThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @description: TODO
 * @ClassName: ApUserRelationServiceImpl
 * @author: Zle
 * @date: 2022-07-04 15:44
 * @version 1.0
*/
@Service
@Slf4j
public class ApUserRelationServiceImpl implements ApUserRelationService {

    @Autowired
    private CacheService cacheService;

    @Override
    public ResponseResult follow(UserRelationDto dto) {

        //判断用户登录
        ApUser user = AppThreadLocalUtil.getUser();
        if (user == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }
        //判断文章

        if (dto == null || dto.getArticleId() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST);
        }

        if (dto.getOperation() == 0) {
            cacheService.zAdd(BehaviorConstants.APUSER_FANS_RELATION + user.getId(), dto.getAuthorId().toString(), System.currentTimeMillis());
            // 将我写入对方的粉丝中
            cacheService.zAdd(BehaviorConstants.APUSER_FANS_RELATION + dto.getAuthorId(), user.getId().toString(), System.currentTimeMillis());

        } else {
            // 取消关注
            cacheService.zRemove(BehaviorConstants.APUSER_FOLLOW_RELATION + user.getId(), dto.getAuthorId().toString());
            cacheService.zRemove(BehaviorConstants.APUSER_FANS_RELATION + dto.getAuthorId(), user.getId().toString());
        }

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);

    }
}