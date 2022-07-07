package com.heima.article.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.article.mapper.ApArticleConfigMapper;
import com.heima.article.mapper.ApArticleContentMapper;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.article.service.ApArticleService;
import com.heima.article.service.ArticleFreemarkerService;
import com.heima.common.constans.ArticleConstants;
import com.heima.common.constans.BehaviorConstants;
import com.heima.common.redis.CacheService;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.article.dtos.ArticleInfoDto;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.pojos.ApArticleConfig;
import com.heima.model.article.pojos.ApArticleContent;
import com.heima.model.article.vo.HotArticleVo;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.pojos.ApUser;
import com.heima.utils.thread.AppThreadLocalUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @description:  ApArticle业务实现
* @ClassName ApArticleServiceImpl
* @author Zle
* @date 2022-06-23 18:36
* @version 1.0
*/
@Service
public class ApArticleServiceImpl extends ServiceImpl<ApArticleMapper, ApArticle> implements ApArticleService {
    // 单页最大加载的数字
    private final static short MAX_PAGE_SIZE = 50;

    @Autowired
    private ApArticleMapper apArticleMapper;

    @Autowired
    private ApArticleConfigMapper apArticleConfigMapper;

    @Autowired
    private ApArticleContentMapper apArticleContentMapper;

    @Autowired
    private ArticleFreemarkerService articleFreemarkerService;



    /**
     * @param loadtype 1, 加载更多 ， 2 ， 加载最新
     * @param dto
     * @description: 根据参数查询文章列表
     * @return: com.heima.model.common.dtos.ResponseResult
     * @author Zle
     * @date: 2022-06-23 19:13
     */
    @Override
    public ResponseResult load(Short loadtype, ArticleHomeDto dto) {
        // 校验参数 参数是否为空，为空设默认值10，与单页加载最大值比对，取最小值
        Integer size = dto.getSize();
        if (size == null || size ==0){
            size=10;
        }
        dto.setSize(Math.min(size,MAX_PAGE_SIZE));
        // 类型参数检验 与设置的枚举类型做比对，不满足则设为加载更多
        if (!loadtype.equals(ArticleConstants.LOADTYPE_LOAD_MORE) &&
                !loadtype.equals(ArticleConstants.LOADTYPE_LOAD_NEW) ){
            loadtype=ArticleConstants.LOADTYPE_LOAD_MORE;
        }
        //文章频道校验
        if (StringUtils.isEmpty(dto.getTag())){
            dto.setTag(ArticleConstants.DEFAULT_TAG);
        }
        //时间校验
        if (dto.getMinBehotTime()==null){
            dto.setMinBehotTime(new Date());
        }
        if (dto.getMaxBehotTime()==null){
            dto.setMaxBehotTime(new Date());
        }
        //2.查询数据
        List<ApArticle> apArticles = apArticleMapper.loadApArticleList(dto, loadtype);
        //3.结果封装
        ResponseResult responseResult = ResponseResult.okResult(apArticles);
        return responseResult;
    }

    @Override
    public ResponseResult saveArticle(ArticleDto dto) {

        //测试服务熔断降级
        /*try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        //1.检查参数
        if (dto == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        ApArticle apArticle = new ApArticle();
        BeanUtils.copyProperties(dto,apArticle);
        //数据合法性默认处理
        if (apArticle.getPublishTime() == null) {
            apArticle.setPublishTime(new Date());
        }

        //2.判断是否岑仔id
        if (dto.getId() == null) {
            //2.1 不存在id 保存 文章 文章配置 文章内容
            //保存文章
            save(apArticle);
            //保存配置
            /*ApArticleConfig apArticleConfig = new ApArticleConfig();
            apArticleConfig.setArticleId(apArticle.getId());*/
            ApArticleConfig apArticleConfig = new ApArticleConfig(apArticle.getId());
            apArticleConfigMapper.insert(apArticleConfig);

            //保存文章内容
            ApArticleContent apArticleContent = new ApArticleContent();
            apArticleContent.setArticleId(apArticle.getId());
            apArticleContent.setContent(dto.getContent());
            apArticleContentMapper.insert(apArticleContent);
        }else {
            //2.2 存在id 修改 文章 文章内容
            //修改文章
            updateById(apArticle);
            ApArticleContent apArticleContent = apArticleContentMapper.selectOne(Wrappers.<ApArticleContent>lambdaQuery().eq(ApArticleContent::getArticleId, dto.getId()));
            apArticleContent.setContent(dto.getContent());
            apArticleContentMapper.updateById(apArticleContent);
        }

        //异步调用 生成静态文件上传到minio中
        articleFreemarkerService.buildArticleToMinIO(apArticle,dto.getContent());

        //3 结果返回 返回文章id
        return ResponseResult.okResult(apArticle.getId());
    }

    @Autowired
    private CacheService cacheService;

    @Override
    public ResponseResult loadArticleBehavior(ArticleInfoDto dto) {
        //判断用户登录

        //判断文章
        if (dto == null || dto.getArticleId() == null || dto.getAuthorId() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST);
        }

        //{ "isfollow": true, "islike": true,"isunlike": false,"iscollection": true }
        boolean isfollow = false, islike = false, isunlike = false, iscollection = false;

        ApUser user = AppThreadLocalUtil.getUser();
        if(user != null){
            //喜欢行为
            String likeBehaviorJson = (String) cacheService.hGet(BehaviorConstants.LIKE_BEHAVIOR + dto.getArticleId().toString(), user.getId().toString());
            if(StringUtils.isNotBlank(likeBehaviorJson)){
                islike = true;
            }
            //不喜欢的行为
            String unLikeBehaviorJson = (String) cacheService.hGet(BehaviorConstants.UN_LIKE_BEHAVIOR + dto.getArticleId().toString(), user.getId().toString());
            if(StringUtils.isNotBlank(unLikeBehaviorJson)){
                isunlike = true;
            }
            //是否收藏
            String collctionJson = (String) cacheService.hGet(BehaviorConstants.COLLECTION_BEHAVIOR+user.getId(),dto.getArticleId().toString());
            if(StringUtils.isNotBlank(collctionJson)){
                iscollection = true;
            }

            //是否关注
            Double score = cacheService.zScore(BehaviorConstants.APUSER_FOLLOW_RELATION + user.getId(), dto.getAuthorId().toString());
            System.out.println(score);
            if(score != null){
                isfollow = true;
            }

        }

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("isfollow", isfollow);
        resultMap.put("islike", islike);
        resultMap.put("isunlike", isunlike);
        resultMap.put("iscollection", iscollection);

        return ResponseResult.okResult(resultMap);
    }

    /**
     * 根据参数加载文章列表  v2
     * @param loadType  1 加载更多   2 加载最新
     * @param dto
     * @param firstPage  是否是首页
     * @return
     */
    @Override
    public ResponseResult load2(Short loadType, ArticleHomeDto dto, boolean firstPage) {
        if (firstPage) {
            String jsonStr = cacheService.get(ArticleConstants.HOT_ARTICLE_FIRST_PAGE + dto.getTag());
            if (StringUtils.isNotBlank(jsonStr)) {
                List<HotArticleVo> hotArticleVoList = JSON.parseArray(jsonStr, HotArticleVo.class);
                ResponseResult responseResult = ResponseResult.okResult(hotArticleVoList);
                return responseResult;
            }
        }
        return load(loadType,dto);
    }
}
