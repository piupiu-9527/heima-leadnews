package com.heima.behavior.service.impl;

import com.alibaba.fastjson.JSON;
import com.heima.behavior.service.LikesService;
import com.heima.common.constans.BehaviorConstants;
import com.heima.common.constans.HotArticleConstants;
import com.heima.common.redis.CacheService;
import com.heima.model.behavior.dtos.LikesBehaviorDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.mess.UpdateArticleMess;
import com.heima.model.user.pojos.ApUser;
import com.heima.utils.thread.AppThreadLocalUtil;
import com.heima.utils.thread.WmThreadLocalUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

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
public class LikesServiceImpl implements LikesService {


    @Autowired
    private CacheService cacheService;

    @Autowired
    private KafkaTemplate<String,String> kafkaTemplate;

    /**
     * @description: 点赞或取消
     * @author Zle
     * @date 2022/7/3 10:30
     * @param dto
     * @return ResponseResult
     */
    @Override
    public ResponseResult likeOrUnLike(LikesBehaviorDto dto) {


        //判断用户登录
        ApUser user = AppThreadLocalUtil.getUser();
        if (user == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }
        //判断文章

        if (dto == null || dto.getArticleId() == null || checkParam(dto)) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST);
        }

        UpdateArticleMess mess = new UpdateArticleMess();
        mess.setArticleId(dto.getArticleId());
        mess.setType(UpdateArticleMess.UpdateArticleType.LIKES);

        if (dto.getOperation() == 0){
            Object obj = cacheService.hGet(BehaviorConstants.LIKE_BEHAVIOR + dto.getArticleId().toString(), user.getId().toString());
            if (obj != null) {
                return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID,"已点赞");
            }
            log.info("保存当前的key：{}，{}，{}",dto.getArticleId(),user.getId(),dto);
            cacheService.hPut(BehaviorConstants.LIKE_BEHAVIOR + dto.getArticleId().toString(), user.getId().toString(),JSON.toJSONString(dto));
            mess.setAdd(1);
        }else {
            //删除当前key
            log.info("删除当前的key：{}，{}，{}",dto.getArticleId(),user.getId(),dto);
            cacheService.hDelete(BehaviorConstants.LIKE_BEHAVIOR + dto.getArticleId().toString(), user.getId().toString());
        }

        kafkaTemplate.send(HotArticleConstants.HOT_ARTICLE_SCORE_TOPIC,JSON.toJSONString(mess));



        /*HashMap<String, Map<String, String>> hashMap = new HashMap();
        HashMap<String, String> hashValue = new HashMap<String, String>();


        String hashKey = BehaviorConstants.LIKE_BEHAVIOR + apArticle.getId().toString();
        String userId = user.getId().toString();

        hashValue.put(userId, JSON.toJSONString(dto));
        hashMap.put(hashKey,hashValue);

        HashSet hashSet = new HashSet();


        return ResponseResult.okResult(hashMap);*/
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    private boolean checkParam(LikesBehaviorDto dto) {

        if (dto.getType() > 2 || dto.getType() < 0 || dto.getOperation() > 1 || dto.getOperation() < 0){
            return true;
        }
        return false;
    }
}
