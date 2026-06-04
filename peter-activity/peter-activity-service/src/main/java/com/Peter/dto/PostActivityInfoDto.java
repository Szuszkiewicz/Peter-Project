package com.Peter.dto;

import lombok.Data;

import java.util.Date;
@Data
public class PostActivityInfoDto {
    /**
     * 主键id
     */
    private Long id;

    /**
     * 活动名字
     */
    private String name;

    /**
     * 活动简介
     */
    private String desc;

    /**
     * 活动时间
     */
    private Date time;

    /**
     * 活动地点
     */
    private String location;

    /**
     * 创建者的id
     */
    private String creatorId;

}
