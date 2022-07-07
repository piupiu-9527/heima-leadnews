package com.heima.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.model.article.pojos.ApArticleConfig;

import java.util.Map;

/**
 * @description:   
 * @ClassName: ApArticleConfigService
 * @author: Zle
 * @date: 2022-07-02 19:05
 * @version 1.0
*/
public interface ApArticleConfigService extends IService<ApArticleConfig> {

    /**
     * 修改文章配置
     * @param map
     */
    public void updateByMap(Map map);
}
