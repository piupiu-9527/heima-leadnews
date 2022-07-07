package com.heima.wemedia.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heima.model.wemedia.pojos.WmNewsMaterial;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @description: 发布文章
 * @ClassName: WmNewsMaterialMapper
 * @author: Zle
 * @date: 2022-06-27 09:31
 * @version 1.0
*/
@Mapper
public interface WmNewsMaterialMapper extends BaseMapper<WmNewsMaterial> {
    void saveRelations(@Param("materialIds") List<Integer> materialIds,
                       @Param("newsId") Integer newsId, @Param("type") Short type);
}
