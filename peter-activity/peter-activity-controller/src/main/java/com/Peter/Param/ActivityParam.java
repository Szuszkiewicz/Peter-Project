package com.Peter.Param;

import lombok.Data;

@Data
public class ActivityParam {
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
    private String time;

    /**
     * 活动地点
     */
    private String location;

    /**
     * 创建者的id
     */
    private String creatorId;

}
