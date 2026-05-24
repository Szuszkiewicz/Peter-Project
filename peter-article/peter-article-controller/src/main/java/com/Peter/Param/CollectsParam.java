package com.Peter.Param;

import com.Peter.enums.ArticleTypeEnum;
import lombok.Data;

@Data
public class CollectsParam {
    /**
     * 收藏主键id
     */
    private Integer id;
    /**
     * 目标id
     */
    private  Long targetId;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * @see ArticleTypeEnum
     */
    private Integer module;

}
