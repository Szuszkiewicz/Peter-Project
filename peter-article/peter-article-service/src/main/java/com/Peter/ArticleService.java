package com.Peter;

import com.Peter.dto.ArticleDetailInfoDto;
import com.Peter.dto.DeleteArticleInfoDto;
import com.Peter.dto.PostArticleInfoDto;
import com.Peter.dto.QueryArticleInfoDto;

import java.util.List;

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

}
