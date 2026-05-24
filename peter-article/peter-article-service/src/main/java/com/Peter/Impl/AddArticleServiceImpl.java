package com.Peter.Impl;

import com.Peter.ArticlePostService;
import com.Peter.dao.ArticleDao;
import com.Peter.dto.PostArticleInfoDto;
import com.Peter.entity.Article;
import com.Peter.enums.ArticleOperationTypeEnums;
import com.Peter.factory.ArticleFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 新增文章
 */
@Service
@Slf4j
public class AddArticleServiceImpl implements ArticlePostService, InitializingBean {
    @Autowired
    private ArticleDao articleDao;
    @Override
    public int doAction(PostArticleInfoDto postArticleInfoDto){
        //新增
        Article article=new Article();
        article.setModule(postArticleInfoDto.getModule());
        article.setTitle(postArticleInfoDto.getTitle());
        article.setType(postArticleInfoDto.getType().byteValue());
        article.setCategoryId(Long.valueOf(postArticleInfoDto.getCategoryId()));
        article.setContent(postArticleInfoDto.getContent());
        article.setUserId(Long.valueOf(postArticleInfoDto.getUserId()));
        article.setStatus(postArticleInfoDto.getStatus().byteValue());
        article.setTags(postArticleInfoDto.getTags());
        article.setCover(postArticleInfoDto.getCover());
        article.setDesc(postArticleInfoDto.getDesc());
        article.setViews(0);
        if(StringUtils.isEmpty(postArticleInfoDto.getTags())){
            article.setTags("");
        }
        int insertCount=articleDao.insertSelective(article);
        if(insertCount>0){
            postArticleInfoDto.setId(article.getId()+"");
        }
        log.info("发布文章-新增-PostArticle-service-出参：{}", insertCount);
        return insertCount;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ArticleFactory.init(ArticleOperationTypeEnums.ADD, this);
    }
}
