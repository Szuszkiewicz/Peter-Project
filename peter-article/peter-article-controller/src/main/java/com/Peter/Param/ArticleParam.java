package com.Peter.Param;

import lombok.Data;

@Data
public class ArticleParam {

    private String id;
    /**
     * 模块
     */
    private Integer module;
    /**
     *  类型
     */
    private Integer type;
    /**
     * 状态
     * 1：草稿（未发布），2：已发布
     */
    private Integer status;
    /**
     * 标题
     */
    private String title;
    /**
     * 内容
     */
    private String content;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 分类id
     */
    private Integer categoryId;


}
