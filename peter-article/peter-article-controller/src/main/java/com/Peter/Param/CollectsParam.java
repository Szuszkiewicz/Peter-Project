package com.Peter.Param;

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
     * @see com.Peter.enums.ModuleTypeEnums
     */
    private Integer module;

}
