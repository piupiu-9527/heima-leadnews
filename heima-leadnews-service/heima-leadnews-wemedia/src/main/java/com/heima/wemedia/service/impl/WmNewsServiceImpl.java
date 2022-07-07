package com.heima.wemedia.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.common.constans.WemediaConstants;
import com.heima.common.constans.WmNewsMessageConstants;
import com.heima.common.exception.CustomException;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.WmNewsDto;
import com.heima.model.wemedia.dtos.WmNewsPageReqDto;
import com.heima.model.wemedia.pojos.*;
import com.heima.utils.thread.WmThreadLocalUtils;
import com.heima.wemedia.mapper.WmMaterialMapper;
import com.heima.wemedia.mapper.WmNewsMapper;
import com.heima.wemedia.mapper.WmNewsMaterialMapper;
import com.heima.wemedia.service.WmNewsAutoScanService;
import com.heima.wemedia.service.WmNewsService;
import com.heima.wemedia.service.WmNewsTaskService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @description:
 * @ClassName: WmNewsService
 * @author: Zle
 * @date: 2022-06-25 23:42
 * @version 1.0
*/
@Service
@Slf4j
public class WmNewsServiceImpl extends ServiceImpl<WmNewsMapper, WmNews> implements WmNewsService {

    @Autowired
    private WmNewsMaterialMapper wmNewsMaterialMapper;

    @Autowired
    private WmMaterialMapper wmMaterialMapper;

    @Autowired
    private WmNewsAutoScanService wmNewsAutoScanService;

    @Autowired
    private WmNewsTaskService wmNewsTaskService;

    @Autowired
    private KafkaTemplate kafkaTemplate;


    /**
     * @description: 查询文章
     * @param: [dto]
     * @return: com.heima.model.common.dtos.ResponseResult
     * @author Zle
     * @date: 2022-06-26 19:11
    */
    @Override
    public ResponseResult findList(WmNewsPageReqDto dto) {
        //1.检查参数 dto为空，返回PARAM_INVALID
        if (dto == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //分页参数检查
        dto.checkParam();
        //获取当前登录人的信息
        WmUser user = WmThreadLocalUtils.getUser();
        if (user == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }
        //2.分页条件查询
        IPage page=new Page(dto.getPage(),dto.getSize());
        LambdaQueryWrapper<WmNews> lambdaQueryWrapper = new LambdaQueryWrapper<WmNews>();
        //状态精确查询
        if (dto.getStatus() != null) {
            lambdaQueryWrapper.eq(WmNews::getStatus,dto.getStatus());
        }

        //频道精确查询
        if (dto.getChannelId() != null) {
            lambdaQueryWrapper.eq(WmNews::getChannelId,dto.getChannelId());
        }
        //时间范围查询
        if (dto.getBeginPubDate() != null && dto.getEndPubDate() != null) {
            lambdaQueryWrapper.between(WmNews::getPublishTime,dto.getBeginPubDate(),dto.getEndPubDate());
        }
        //关键字模糊查询
        if (dto.getKeyword() != null) {
            lambdaQueryWrapper.like(WmNews::getTitle,dto.getKeyword());
        }
        //发布时间倒序查询
        lambdaQueryWrapper.orderByDesc(WmNews::getCreatedTime);
        page(page,lambdaQueryWrapper);
        //3.结果返回   TODO
        PageResponseResult responseResult = new PageResponseResult(dto.getPage(),dto.getSize(), (int) page.getTotal());
        //把查询出来的数据集合    放入到data里
        responseResult.setData(page.getRecords());
        return responseResult;
    }

    /**
     * @description: 发布文章或保存草稿
     * @author Zle
     * @date 2022/6/27 10:04
     * @param dto
     * @return ResponseResult
     */
    @Override
    public ResponseResult submitNews(WmNewsDto dto) {
        //1.保存或修改文章 -》 wm_news
        if (dto == null || dto.getContent() == null) {
            return  ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        //对发布时间进行校验
        if (dto.getPublishTime() == null) {
            dto.setPublishTime(new Date());
        }

        WmNews wmNews = new WmNews();
        //属性拷贝
        BeanUtils.copyProperties(dto,wmNews);
        // 将封面图片的类由list转成string
        if (dto.getImages() != null && dto.getImages().size()> 0) {
            String ImageStr = StringUtils.join(dto.getImages(), ",");
            wmNews.setImages(ImageStr);
        }
        //封面类型为自动 type=-1
        if (dto.getType().equals(WemediaConstants.WM_NEWS_TYPE_AUTO)){
            wmNews.setType(null);
        }
        //保存或修改文章
        saveOrUpdateWmNews(wmNews);

        //2.判断是否为草稿(status == 0)  如果为草稿不保存文章与素材关系直接结束当前方法: return success
        if (dto.getStatus().equals(WmNews.Status.NORMAL.getCode())) {
            return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
        }
        //获取到文章内容中的图片信息
        List<String> materials = ectractUrlInfo(dto.getContent());

        //3.不是草稿，保存文章内容图片与素材的关系 -> wm_news_material
        saveRelativeInfoForContent(materials, wmNews.getId());
        //4.不是草稿，保存文章封面图片与素材的关系，如果当前布局是自动，需要匹配封面图片->wm_news_material
        saveRelativeInfoForCover(dto,wmNews,materials);

        //新增文章后审核调用 自动审核文章

        //wmNewsAutoScanService.autoScanWmNews(wmNews.getId());
        wmNewsTaskService.addNewsToTask(wmNews.getId(),wmNews.getPublishTime());
        //wmNewsTaskService.addNewsToTask(wmNews);
        //异步  与WmNewsAutoScanServiceImpl中autoScanWmNews方法上的@Async注解作用相同
        /*new Thread(new Runnable() {
            @Override
            public void run() {
                wmNewsAutoScanService.autoScanWmNews(wmNews.getId());
            }
        }).start();*/


        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    /**
     * @description: 文章的上下架
     * @author Zle
     * @date 2022/7/2 18:26
     * @param dto
     * @return ResponseResult
     */
    @Override
    public ResponseResult downOrUp(WmNewsDto dto) {

        //1. 检查参数
        if (dto.getId() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        //2. 查询文章
        WmNews wmNews = getById(dto.getId());
        if (wmNews == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST,"文章不存在");
        }

        //判断文章是否发布
        if(!wmNews.getStatus().equals(WmNews.Status.PUBLISHED.getCode())){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID,"当前文章不是发布状态，不能上下架");
        }

        //4.修改文章enable
        if(dto.getEnable() != null && dto.getEnable() > -1 && dto.getEnable() < 2){
            update(Wrappers.<WmNews>lambdaUpdate().set(WmNews::getEnable,dto.getEnable())
                    .eq(WmNews::getId,wmNews.getId()));
        }

        if(wmNews.getArticleId() != null){
            Map<String,Object> map = new HashMap<>();
            map.put("articleId",wmNews.getArticleId());
            map.put("enable",dto.getEnable());
            kafkaTemplate.send(WmNewsMessageConstants.WM_NEWS_UP_OR_DOWN_TOPIC,JSON.toJSONString(map));
        }

        //偷懒实现方式
        //boolean result = this.lambdaUpdate().set(WmNews::getEnable, dto.getEnable())
        //        .eq(WmNews::getId, dto.getId())
        //        .eq(WmNews::getStatus, WmNews.Status.PUBLISHED.getCode())
        //        .update();
        //if (result) { //更新成功
        //    //发消息
        //} else { //更新失败
        //    return ResponseResult.errorResult(AppHttpCodeEnum.UNKNOWN, "非法操作");
        //}
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);

    }


    /**
     * @description: 保存或修改文章
     * @author Zle
     * @date 2022/6/27 10:40
     * @param wmNews
     */
    private void saveOrUpdateWmNews(WmNews wmNews) {
        //补全属性 userid 时间 上架
        wmNews.setUserId(WmThreadLocalUtils.getUser().getId());
        wmNews.setCreatedTime(new Date());
        wmNews.setPublishTime(new Date());
        wmNews.setSubmitedTime(new Date());
        wmNews.setEnable((short) 1);

        //没有id直接保存（修改），有id修改
        if (wmNews.getId() == null) {
            save(wmNews);
        } else {
            //1.删除文章图片和素材的关系
            wmNewsMaterialMapper.delete(
                    Wrappers.<WmNewsMaterial>lambdaQuery().eq(WmNewsMaterial::getNewsId,wmNews.getId()));
            //2.更新
            updateById(wmNews);
        }
    }


    /**
     * @description: 文章内容中的图片信息
     * @author Zle
     * @date 2022/6/27 11:11
     * @param content
     * @return List<String>
     */
    private List<String> ectractUrlInfo(String content) {
        List<String> materials = new ArrayList<>();
        List<ContentData> maps = JSON.parseArray(content, ContentData.class);
        for (ContentData map : maps) {
            if (map.getType().equals("images")){
                materials.add(map.getValue());
            }
        }

        return materials;
    }


    /**
     * @description: 处理文章内容图片与素材的关系
     * @author Zle
     * @date 2022/6/27 14:35
     * @param materials
     * @param newsId
     */
    private void saveRelativeInfoForContent(List<String> materials, Integer newsId) {
        saveRelativeInfo(materials,newsId,WemediaConstants.WM_CONTENT_REFERENCE);
    }

    /**
     * @description: 保存文章图片与素材的关系到数据库中
     * @author Zle
     * @date 2022/6/27 14:35
     * @param materials
     * @param newsId
     * @param type
     */
    private void saveRelativeInfo(List<String> materials, Integer newsId, Short type) {
        //校验是否包含图片
        if (CollectionUtils.isEmpty(materials)) {
            return;
        }
        //根据路径查id
        List<WmMaterial> dbMaterials = wmMaterialMapper.selectList(Wrappers.<WmMaterial>lambdaQuery()
                .in(WmMaterial::getUrl, materials));
        //判断素材是否与有效
        if (CollectionUtils.isEmpty(dbMaterials) || materials.size() != dbMaterials.size()){
            throw new CustomException(AppHttpCodeEnum.MATERIASL_REFERENCE_FAIL);
        }
        //素材id
        List<Integer> idList = dbMaterials.stream().map(WmMaterial::getId).collect(Collectors.toList());
        //批量保存
        wmNewsMaterialMapper.saveRelations(idList,newsId,type);
    }

    private void saveRelativeInfoForCover(WmNewsDto dto, WmNews wmNews, List<String> materials) {
        List<String> coverImages = dto.getImages();
        //如果当前封面类型为自动，则从内容中的图片作为封面图片
        if (dto.getType().equals(WemediaConstants.WM_NEWS_TYPE_AUTO)) {
            //materials：保存的是内容中的图片
            if (materials.size() >=3){
                coverImages = materials.stream().limit(3).collect(Collectors.toList());
            }else if (materials.size() >=0){
                coverImages = materials.stream().limit(1).collect(Collectors.toList());
            }else {
                wmNews.setType(WemediaConstants.WM_NEWS_NONE_IMAGE);
            }

            //修改文章
            if (coverImages != null && coverImages.size() > 0) {
                wmNews.setImages(StringUtils.join(coverImages, ","));
            }
            updateById(wmNews);

            //第二个功能 保存文章与素材的关系
            if (coverImages != null && coverImages.size() > 0) {
                saveRelativeInfo(coverImages, wmNews.getId(), WemediaConstants.WM_COVER_REFERENCE);
            }
        }
    }


}
