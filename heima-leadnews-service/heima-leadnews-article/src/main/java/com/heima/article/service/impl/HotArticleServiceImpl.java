package com.heima.article.service.impl;

import com.alibaba.fastjson.JSON;
import com.heima.apis.wemedia.IWemediaClient;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.article.service.HotArticleService;
import com.heima.common.constans.ArticleConstants;
import com.heima.common.redis.CacheService;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.vo.HotArticleVo;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.pojos.WmChannel;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @description:   
 * @ClassName: HotArticleServiceImpl
 * @author: Zle
 * @date: 2022-07-05 19:58
 * @version 1.0
*/
@Service
@Slf4j
public class HotArticleServiceImpl implements HotArticleService {

    @Autowired
    private ApArticleMapper apArticleMapper;

    @Autowired
    private IWemediaClient wemediaClient;

    @Autowired
    private CacheService cacheService;



    /**
     * 计算热点文章
     */
    @Override
    public void computeHotArticle() {
        //1.查询前5天的文章数据
        //Date dateParam = DateTime.now().minusDays(5).toDate();
        Date dateParam = DateTime.parse("2020-05-05T07:19:21Z").toDate();
        List<ApArticle> apArticleList = apArticleMapper.findArticleListByLast5days(dateParam);

        //2.计算文章的分值
        List<HotArticleVo> hotArticleVoList = computeHotArticle(apArticleList);

        //3.为每个频道缓存30条分值较高的文章
        cacheTagToRedis(hotArticleVoList);
    }

    /**
     * 计算指定文章的具体分值
     */
    private Integer computeScore(ApArticle apArticle) {
        Integer scere = 0;
        if (apArticle.getLikes() != null) {
            scere += apArticle.getLikes() * ArticleConstants.HOT_ARTICLE_LIKE_WEIGHT;
        }
        if (apArticle.getViews() != null) {
            scere += apArticle.getViews() * ArticleConstants.HOT_ARTICLE_READ_WEIGHT;
        }
        if (apArticle.getComment() != null) {
            scere += apArticle.getComment() * ArticleConstants.HOT_ARTICLE_COMMENT_WEIGHT;
        }
        if (apArticle.getCollection() != null) {
            scere += apArticle.getCollection() * ArticleConstants.HOT_ARTICLE_COLLECTION_WEIGHT;
        }

        return scere;
    }

    /**
     * 计算文章分值
     */
    private List<HotArticleVo> computeHotArticle(List<ApArticle> apArticleList) {
        List<HotArticleVo> hotArticleVoList = new ArrayList<>();

        if (apArticleList != null && apArticleList.size() > 0) {
            for (ApArticle apArticle : apArticleList) {
                HotArticleVo hot = new HotArticleVo();
                BeanUtils.copyProperties(apArticle, hot);
                Integer score = computeScore(apArticle);
                hot.setScore(score);
                hotArticleVoList.add(hot);
            }
        }
        return hotArticleVoList;
    }

    /**
     * 为每个频道缓存30条分值较高的文章
     */
    private void cacheTagToRedis(List<HotArticleVo> hotArticleVoList) {
        //1.远程调用自媒体微服务查询所有的频道
        ResponseResult responseResult = wemediaClient.getChannels();
        if (responseResult.getCode().equals(200)) {
            String channelJson = JSON.toJSONString(responseResult.getData());
            List<WmChannel> wmChannels = JSON.parseArray(channelJson, WmChannel.class);
            //2.检索出每个频道的文章
            if (wmChannels != null && wmChannels.size() > 0) {
                for (WmChannel wmChannel : wmChannels) {
                    //本次循环为java频道
                    List<HotArticleVo> hotArticleVos =
                            hotArticleVoList.stream().filter(hotArticleVo -> {
                                if (hotArticleVo.getChannelId() == null) {
                                    return false; //没有频道信息，舍弃数据
                                }
                                return hotArticleVo.getChannelId().equals(wmChannel.getId());
                            }).collect(Collectors.toList());
                    //3.给文章进行排序，取每个频道中30条分值较高的文章存入redis
                    //key：频道id，value：30条分值较高的文章
                    sortAndCache(hotArticleVos,
                            ArticleConstants.HOT_ARTICLE_FIRST_PAGE + wmChannel.getId());
                }
            }
        }

        //4.设置推荐数据
        //对所有频道中的文章进行排序，取30条分值较高的文章存入redis
        sortAndCache(hotArticleVoList,
                ArticleConstants.HOT_ARTICLE_FIRST_PAGE + ArticleConstants.DEFAULT_TAG);
    }

    /**
     * 排序并且缓存数据
     *
     * @param hotArticleVos
     * @param key
     */
    private void sortAndCache(List<HotArticleVo> hotArticleVos, String key) {
        hotArticleVos = hotArticleVos.stream()
                .sorted(Comparator.comparing(HotArticleVo::getScore).reversed()) //反转
                .collect(Collectors.toList());
        if (hotArticleVos.size() > 30) {
            hotArticleVos = hotArticleVos.subList(0, 30);
        }
        cacheService.set(key, JSON.toJSONString(hotArticleVos));
    }
}
