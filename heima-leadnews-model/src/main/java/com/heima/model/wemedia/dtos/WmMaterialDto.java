package com.heima.model.wemedia.dtos;

import com.heima.model.common.dtos.PageRequestDto;
import lombok.Data;

/**
 * @description:
 * @ClassName: WmMaterialDto
 * @author: Zle
 * @date: 2022-06-25 20:58
 * @version 1.0
*/
@Data
public class WmMaterialDto extends PageRequestDto {

    /**
     * 1 收藏
     * 0 未收藏
     */
    private Short isCollection;

    private int userId; //为了让黑马头条管理员使用的

    //page, size 继承来的属性
}
