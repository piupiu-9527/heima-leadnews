package com.heima.wemedia.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.gson.JsonArray;
import com.heima.apis.article.IArticleClient;
import com.heima.common.aliyun.GreenImageScan;
import com.heima.common.aliyun.GreenTextScan;
import com.heima.common.tess4j.Tess4jClient;
import com.heima.file.service.FileStorageService;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.pojos.WmChannel;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.model.wemedia.pojos.WmSensitive;
import com.heima.model.wemedia.pojos.WmUser;
import com.heima.utils.common.SensitiveWordUtil;
import com.heima.wemedia.mapper.WmChannelMapper;
import com.heima.wemedia.mapper.WmNewsMapper;
import com.heima.wemedia.mapper.WmSensitiveMapper;
import com.heima.wemedia.mapper.WmUserMapper;
import com.heima.wemedia.service.WmNewsAutoScanService;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.TesseractException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @description:
 * @ClassName: WmNewsAutoScanServiceImpl
 * @author: Zle
 * @date: 2022-06-27 21:26
 * @version 1.0
*/
@Service
@Slf4j
@Transactional
public class WmNewsAutoScanServiceImpl implements WmNewsAutoScanService {

    @Autowired
    private WmNewsMapper wmNewsMapper;

    @Autowired
    private GreenTextScan greenTextScan;

    @Autowired
    private GreenImageScan greenImageScan;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private IArticleClient articleClient;

    @Autowired
    private WmChannelMapper wmChannelMapper;

    @Autowired
    private WmUserMapper wmUserMapper;

    @Autowired
    private WmSensitiveMapper wmSensitiveMapper;

    @Autowired
    private Tess4jClient tess4jClient;

    /**
     * @description: 自媒体文章审核
     * @author Zle
     * @date 2022/6/27 21:27
     * @param wmNewsId 自媒体文章id
     */
    @Override
    @Async   //表明当前方法是一个异步方法 想让那个方法异步，就在那个方法上加上@Async这个注解
    public void autoScanWmNews(Integer wmNewsId) {



        //1 查询自媒体文章
        WmNews wmNews = wmNewsMapper.selectById(wmNewsId);
        if (wmNews == null) {
            throw new RuntimeException("WmNewsAutoScanServiceImpl - 文章不存在");
        }
        //状态必须是待审核的状态
        if (wmNews.getStatus().equals(WmNews.Status.SUBMIT.getCode())) {
            // 从内容中提取纯文本内容和图片
            Map<String, Object> testAndImages = handleTextAndImages(wmNews);

            //自管理的敏感词过滤
            boolean isSensitive = handleSensitiveScan((String) testAndImages.get("content"),wmNews);
            if (!isSensitive){
                return;
            }

            //2 审核文本内容 阿里云接口
            boolean isTextScan = handleTestScan((String) testAndImages.get("content"),wmNews);
            if (!isTextScan){
                return;
            }
            //3 审核图片 阿里云接口
            boolean isImageScan = handleImageScan((List<String>) testAndImages.get("images"),wmNews);
            if (!isImageScan){
                return;
            }
            //4 审核成功 保存app端的相关文章数据
            ResponseResult responseResult = saveAppArticle(wmNews);

            if (!responseResult.getCode().equals(200)){
                throw new RuntimeException("WmNewsAutoScanServiceImpl - 文章审核，保存app端数据失败");
            }

            //回填Article_id
            wmNews.setArticleId((Long) responseResult.getData());

            updateWmNews(wmNews, (short) 9,"审核成功");
        }

    }


    /**
     * @description: 从自媒体文章的内容中提取文本和图片  提取文章的封面图片
     * @author Zle
     * @date 2022/6/27 21:41
     * @param wmNews
     * @return Map<Object>
     */
    private Map<String, Object> handleTextAndImages(WmNews wmNews) {

        //存储纯文本内容
        StringBuilder stringBuilder = new StringBuilder();
        //存储文章中的图片和封面图片
        List<String> images = new ArrayList<>();

        String titleStr =null;

        //从自媒体文章的内容中提取文本和图片
        if (wmNews.getTitle() != null) {
            titleStr = wmNews.getTitle();
        }
        if (StringUtils.isNotBlank(wmNews.getContent())){
            List<Map> maps = JSONArray.parseArray(wmNews.getContent(), Map.class);

            for (Map map : maps) {
                if (map.get("type").equals("text")){
                    stringBuilder.append(map.get("value"));
                }
                if (map.get("type").equals("image")){
                    images.add((String) map.get("value"));
                }
            }
        }
        //2.提取文章的封面图片
        if (StringUtils.isNotBlank(wmNews.getImages())) {
            String[] split = wmNews.getImages().split(",");
            images.addAll(Arrays.asList(split));
        }

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("content",stringBuilder.toString()+titleStr);
        resultMap.put("images",images);
        return resultMap;
    }

    /**
     * @description: 审核纯文本内容
     * @author Zle
     * @date 2022/6/27 22:46
     * @param content
     * @param wmNews
     * @return boolean
     */
    private boolean handleTestScan(String content, WmNews wmNews) {

        boolean flag =true;


        if (content.length() == 0){
            flag=false;
        }
        try {
            Map map = greenTextScan.greeTextScan(content);
            if (map != null) {
                //审核失败
                if (map.get("suggestion").equals("block")){
                    flag = false;
                    updateWmNews(wmNews, (short) 2, "当前文章中存在违规内容");
                }

                //不确定信息，需要人工审核
                if (map.get("suggestion").equals("review")){
                    flag = false;
                    updateWmNews(wmNews, (short) 3, "当前文章中存在不确定内容");
                }

            }
        } catch (Exception e) {
            flag = false;
            e.printStackTrace();
        }
        return flag;
    }

    /** 
     * @description: 文章未通过审核,修改文章内容
     * @author Zle
     * @date 2022/6/27 22:54
     * @param wmNews 
     * @param status
     * @param reason
     */
    private void updateWmNews(WmNews wmNews, short status, String reason) {
        wmNews.setStatus(status);
        wmNews.setReason(reason);
        wmNewsMapper.updateById(wmNews);
    }

    /**
     * @description:
     * @author Zle
     * @date 2022/6/27 23:04
     * @param images
     * @param wmNews
     * @return boolean
     */
    private boolean handleImageScan(List<String> images, WmNews wmNews) {

        boolean flag = true;

        if (images == null || images.size() == 0) {
            return flag;
        }

        //下载图片minio
        //图片去重
        images = images.stream().distinct().collect(Collectors.toList());

        List<byte[]> imagesList=new ArrayList<>();

        try {
            //识别图片并审核
            for (String image : images) {
                //从MinIO下载图片
                byte[] bytes = fileStorageService.downLoadFile(image);

                //图片识别文字审核 --start--
                //从byte[]转换为bufferedImage
                ByteArrayInputStream in = new ByteArrayInputStream(bytes);
                BufferedImage imageFile = ImageIO.read(in);

                //识别图片中的文字
                String result = tess4jClient.doOCR(imageFile);

                //审核是否包含自管理的敏感词
                boolean isSensitive = handleSensitiveScan(result, wmNews);
                if (!isSensitive){
                    return false;
                }
                //图片识别文字审核 --end--
                imagesList.add(bytes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //审核图片
        try {
            Map map = greenImageScan.imageScan(imagesList);

            if (map != null) {
                //审核失败
                if (map.get("suggestion").equals("block")){
                    flag = false;
                    updateWmNews(wmNews, (short) 2, "当前图片中存在违规内容");
                }

                //不确定信息，需要人工审核
                if (map.get("suggestion").equals("review")){
                    flag = false;
                    updateWmNews(wmNews, (short) 3, "当前图片中存在不确定内容");
                }

            }

        } catch (Exception e) {
            flag = false;
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * @description: 保存app端的文章数据
     * @author Zle
     * @date 2022/6/27 23:21
     * @param wmNews
     * @return ResponseResult
     */
    private ResponseResult saveAppArticle(WmNews wmNews) {

        ArticleDto dto = new ArticleDto();
        //属性拷贝
        BeanUtils.copyProperties(wmNews,dto);
        //文章布局
        dto.setLayout(wmNews.getType());
        //频道
        WmChannel wmChannel = wmChannelMapper.selectById(wmNews.getChannelId());
        if (wmChannel != null) {
            dto.setChannelName(wmChannel.getName());
        }
        //作者
        dto.setAuthorId(wmNews.getUserId().longValue());
        WmUser wmUser = wmUserMapper.selectById(wmNews.getUserId());
        if (wmUser != null) {
            dto.setAuthorName(wmUser.getName());
        }
        //设置文章id
        if (wmNews.getArticleId() != null) {
            dto.setId(wmNews.getArticleId());
        }
        dto.setCreatedTime(new Date());
        ResponseResult responseResult = articleClient.saveArticle(dto);
        return responseResult;
    }

    /**
     * @description: 自管理的敏感词审核
     * @author Zle
     * @date 2022/6/28 20:08
     * @param content
     * @param wmNews
     * @return boolean
     */
    private boolean handleSensitiveScan(String content, WmNews wmNews) {

        boolean flag = true;

        //获取所有的敏感词
        List<WmSensitive> wmSensitives = wmSensitiveMapper.selectList(Wrappers.<WmSensitive>lambdaQuery().select(WmSensitive::getSensitives));

        List<String> sensitiveList = wmSensitives.stream().map(WmSensitive::getSensitives).collect(Collectors.toList());

        //初始化敏感词词库
        SensitiveWordUtil.initMap(sensitiveList);

        //查看文章中是否包含敏感词
        Map<String, Integer> map = SensitiveWordUtil.matchWords(content);

        if (map.size() > 0){
            updateWmNews(wmNews, (short) 2,"当前文章中存在违规内容"+map);
            flag = false;
        }
        return flag;
    }
}
