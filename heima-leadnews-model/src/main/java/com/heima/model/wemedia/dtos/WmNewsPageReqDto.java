package com.heima.model.wemedia.dtos;

import com.heima.model.common.dtos.PageRequestDto;
import lombok.Data;

import java.util.Date;

/**
 * @description:
 * @ClassName: WmNewsPageReqDto
 * @author: Zle
 * @date: 2022-06-25 23:38
 * @version 1.0
*/
@Data
public class WmNewsPageReqDto extends PageRequestDto {
    /**
     * 状态
     */
    private Short status;
    /**
     * 开始时间
     */
    private Date beginPubDate;
    /**
     * 结束时间
     */
    private Date endPubDate;
    /**
     * 所属频道ID
     */
    private Integer channelId;
    /**
     * 关键字
     */
    private String keyword;

    /**
     * 用户ID
     */
    private String userId;
}
