package com.Peter.dto;

import lombok.Data;

@Data
public class QueryArticleInfoDto {
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
     * 用户ID
     */
    private Long userId;

    /**
     * 分类ID
     */
    private Long categoryId;
   /**
     * 页码
     */
    private Integer pageNum;
   /**
     * 每页数量
     */
    private Integer pageSize;
    /**
     * 是否已删除
     */
    private Byte isDeleted;
}
