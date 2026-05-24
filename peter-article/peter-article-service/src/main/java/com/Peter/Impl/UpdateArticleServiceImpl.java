package com.Peter.Impl;

import com.Peter.ArticlePostService;
import com.Peter.dao.ArticleDao;
import com.Peter.dto.PostArticleInfoDto;
import com.Peter.entity.Article;
import com.Peter.enums.ArticleOperationTypeEnums;
import com.Peter.factory.ArticleFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 修改文章
 */
@Slf4j
@Service
public class UpdateArticleServiceImpl implements ArticlePostService, InitializingBean {
    @Autowired
    private ArticleDao articleDao;
    @Override
    public int doAction(PostArticleInfoDto postArticleInfoDto){
        //修改
        Article article= articleDao.selectByPrimaryKey(Long.valueOf(postArticleInfoDto.getId()));
        article.setContent(postArticleInfoDto.getContent());
        article.setTitle(postArticleInfoDto.getTitle());
        article.setCategoryId(Long.valueOf(postArticleInfoDto.getCategoryId()));
        article.setTags(postArticleInfoDto.getTags());
        article.setCover(postArticleInfoDto.getCover());
        article.setDesc(postArticleInfoDto.getDesc());
        int updateCount=articleDao.updateByPrimaryKeySelective(article);
        log.info("发布文章-PostArticle-service-出参：{}", updateCount);
        return updateCount;

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ArticleFactory.init(ArticleOperationTypeEnums.UPDATE, this);
    }
}
