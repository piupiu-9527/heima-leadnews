package com.heima.behavior.service.impl;

import com.alibaba.fastjson.JSON;
import com.heima.behavior.service.LikesService;
import com.heima.behavior.service.UnLikesService;
import com.heima.common.constans.BehaviorConstants;
import com.heima.common.constans.HotArticleConstants;
import com.heima.common.redis.CacheService;
import com.heima.model.behavior.dtos.LikesBehaviorDto;
import com.heima.model.behavior.dtos.UnLikesBehaviorDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.mess.UpdateArticleMess;
import com.heima.model.user.pojos.ApUser;
import com.heima.utils.thread.AppThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @description: TODO
 * @ClassName: LikesServiceImpl
 * @author: Zle
 * @date: 2022-07-03 10:29
 * @version 1.0
*/
@Service
@Slf4j
@Transactional
public class UnLikesServiceImpl implements UnLikesService {


    @Autowired
    private CacheService cacheService;

    @Autowired
    private KafkaTemplate<String,String> kafkaTemplate;


    @Override
    public ResponseResult Unlike(UnLikesBehaviorDto dto) {

        //判断用户登录
        ApUser user = AppThreadLocalUtil.getUser();
        if (user == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }
        //判断文章

        if (dto == null || dto.getArticleId() == null ) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST);
        }

        UpdateArticleMess mess = new UpdateArticleMess();
        if (dto.getType() == 0){
            log.info("保存当前的key：{}，{}，{}",dto.getArticleId(),user.getId(),dto);
            cacheService.hPut(BehaviorConstants.UN_LIKE_BEHAVIOR + dto.getArticleId().toString(), user.getId().toString(),JSON.toJSONString(dto));
        }else {
            log.info("删除当前的key：{}，{}，{}",dto.getArticleId(),user.getId(),dto);
            cacheService.hDelete(BehaviorConstants.UN_LIKE_BEHAVIOR + dto.getArticleId().toString(), user.getId().toString());
        }
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
}
