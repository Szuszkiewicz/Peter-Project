package com.Peter.dto;

import com.Peter.enums.ArticleTypeEnum;
import lombok.Data;

@Data
public class CollectsInfoDto {
    /**
     * 收藏主键id
     */
    private Integer id;
    /**
     * 目标id
     */
    private Long targetId;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * @see ArticleTypeEnum
     * 模块
     */
    private Integer module;
}
