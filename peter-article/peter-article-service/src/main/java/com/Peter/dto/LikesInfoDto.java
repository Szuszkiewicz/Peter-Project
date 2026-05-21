package com.Peter.dto;

import lombok.Data;

@Data
public class LikesInfoDto {
    /**
     * 点赞主键id
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
     * @see com.Peter.enums.ModuleTypeEnums
     * 模块
     */
    private Integer module;
}
