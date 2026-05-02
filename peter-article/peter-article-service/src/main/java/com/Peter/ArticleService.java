package com.Peter;

import com.Peter.dto.DeleteArticleInfoDto;
import com.Peter.dto.PostArticleInfoDto;

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

}
