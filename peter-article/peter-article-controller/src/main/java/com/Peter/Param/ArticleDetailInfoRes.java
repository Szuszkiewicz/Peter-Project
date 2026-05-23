package com.Peter.Param;

import lombok.Data;

@Data
public class ArticleDetailInfoRes {
    private Long id;

    /**
     * 模块
     */
    private Integer module;

    /**
     * 1:文章，2：视频
     */
    private Integer type;

    /**
     * 1:未发布,2:已发布
     */
    private Integer status;


    /**
     * 标题
     */
    private String title;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 浏览量
     */
    private Integer views;

    /**
     * 点赞数
     */
    private Integer likes;
    /**
     * 收藏数
     */
    private Integer collects;

    /**
     * 评论数
     */
    private Integer commentsCount;

    /**
     * 创建时间
     * yyyy-MM-dd hh:mm:ss
     */
    private String createTime;

    /**
     * 更新时间
     */
    private String updateTime;
    /**
     * 封面
     */
    private String cover;

    /**
     * 简介
     */
    private String desc;

    /**
     * 标签
     */
    private String tags;

    /**
     * 内容
     */
    private String content;
}
