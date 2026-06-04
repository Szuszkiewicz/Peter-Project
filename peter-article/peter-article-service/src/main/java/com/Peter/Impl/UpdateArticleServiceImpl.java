package com.Peter.Impl;

import com.Peter.ArticlePostService;
import com.Peter.dao.ArticleDao;
import com.Peter.dto.PostArticleInfoDto;
import com.Peter.entity.Article;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 修改文章
 */
@Slf4j
@Service
public class UpdateArticleServiceImpl implements ArticlePostService {

    @Autowired
    private ArticleDao articleDao;
    @Override
    public int doAction(PostArticleInfoDto postArticleInfoDto) {
        log.info("发布文章-PostArticle-service-入参：{}", postArticleInfoDto);
        Article article= articleDao.selectByPrimaryKey(Long.valueOf(postArticleInfoDto.getId()));
        article.setContent(postArticleInfoDto.getContent());
        article.setTitle(postArticleInfoDto.getTitle());
        article.setCategoryId(Long.valueOf(postArticleInfoDto.getCategoryId()));
        int updateCount=articleDao.updateByPrimaryKeySelective(article);
        log.info("发布文章-PostArticle-service-出参：{}", updateCount);
        return updateCount;
    }
}
