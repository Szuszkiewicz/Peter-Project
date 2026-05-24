package com.Peter;

import com.Peter.dto.ArticleDetailInfoDto;
import com.Peter.dto.DeleteArticleInfoDto;
import com.Peter.dto.PostArticleInfoDto;
import com.Peter.dto.QueryArticleInfoDto;

import java.util.List;
import java.util.Set;

public interface ArticleService {
    /**
     * 添加文章(新增或修改)
     * @param postArticleInfoDto
     * @return
     */
    int PostArticle(PostArticleInfoDto postArticleInfoDto);

    /**
     * 删除文章
     */
    int DeleteArticle(DeleteArticleInfoDto deleteArticleInfoDto);
    /**
     * 获取文章列表
     */
    List<ArticleDetailInfoDto> QueryArticleList(QueryArticleInfoDto queryArticleInfoDto);
    /**
     * 根据id查询文章
     */
    ArticleDetailInfoDto queryArticleById(Long id);
    /**
     * 添加文章点赞数
     * @param id
     * @  return
     */
    int addLikesCount(Long id);
    /**
     *  减少文章点赞数
     *  @param id
     *  @return
     */
    int subtractLikesCount(Long id);
    /**
     * 添加文章收藏数
     * @param id
     * @return
     */
    int addCollectsCount(Long id);
    /**
     * 减少文章收藏数
     * @param id
     * @return
     */
    int subtractCollectsCount(Long id);
    /**
     * 统计文章数量
     */
    int countArticle(QueryArticleInfoDto queryArticleInfoDto);
    /**
     * 增加文章浏览量
     */
    int addViewsCount(Long id);
    /**
     * 文章排行
     *
     */
    List<ArticleDetailInfoDto> selectRank();

    Set<ArticleDetailInfoDto> selectRecommend(Long id);
}
