package com.Peter.Impl;

import com.Peter.ArticleService;
import com.Peter.dao.ArticleDao;
import com.Peter.dto.ArticleDetailInfoDto;
import com.Peter.dto.DeleteArticleInfoDto;
import com.Peter.dto.PostArticleInfoDto;
import com.Peter.dto.QueryArticleInfoDto;
import com.Peter.entity.Article;
import com.Peter.entity.ArticleExample;
import com.Peter.mapper.ArticleMapper;
import com.Peter.utils.DateUtils;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleDao articleDao;

    @Autowired
    private ArticleMapper articleMapper;
    @Override
    public int PostArticle(PostArticleInfoDto postArticleInfoDto) {
        try {
            log.info("发布文章-PostArticle-service-入参：{}", JSON.toJSONString(postArticleInfoDto));
            if(postArticleInfoDto.getId()!= null){
              Article article= articleDao.selectByPrimaryKey(Long.valueOf(postArticleInfoDto.getId()));
              article.setContent(postArticleInfoDto.getContent());
              article.setTitle(postArticleInfoDto.getTitle());
              article.setCategoryId(Long.valueOf(postArticleInfoDto.getCategoryId()));
              int updateCount=articleDao.updateByPrimaryKeySelective(article);
              log.info("发布文章-PostArticle-service-出参：{}", updateCount);
              return updateCount;
                //修改
            }else{
                //新增
             Article article=new Article();
             article.setModule(postArticleInfoDto.getModule());
             article.setTitle(postArticleInfoDto.getTitle());
             article.setType(postArticleInfoDto.getType().byteValue());
             article.setCategoryId(Long.valueOf(postArticleInfoDto.getCategoryId()));
             article.setContent(postArticleInfoDto.getContent());
             article.setUserId(Long.valueOf(postArticleInfoDto.getUserId()));
             article.setStatus(postArticleInfoDto.getStatus().byteValue());
             int insertCount=articleDao.insertSelective(article);
             log.info("发布文章-新增-PostArticle-service-出参：{}", insertCount);
             return insertCount;
            }
        }catch (Exception e){
            log.error("发布文章-PostArticle-service-异常：", e);
            return -1;
        }
    }
    @Override
    public int DeleteArticle(DeleteArticleInfoDto deleteArticleInfoDto){
        try {
            log.info("删除文章-DeleteArticle-service-入参：{}", JSON.toJSONString(deleteArticleInfoDto));
            Article article=articleDao.selectByPrimaryKey(deleteArticleInfoDto.getId());
            if(article== null){
                log.error("无对应文章:{}", deleteArticleInfoDto.getId());
                return -1;
            }
            if(article.getIsDelete().equals((byte)1)){
                log.error("文章已被删除:{}", deleteArticleInfoDto.getId());
                return -1;
            }
            if(!article.getUserId().equals(deleteArticleInfoDto.getUserId())){
                log.error("用户不匹配，无法删除该文章:{}", deleteArticleInfoDto.getId());
                return -1;
            }
            article.setIsDelete(( byte)1);
            int deleteCount = articleDao.updateByPrimaryKey(article);
            log.info("删除文章-DeleteArticle-service-出参：{}", deleteCount);
            return deleteCount;
        }catch (Exception e){
            log.error("删除文章-DeleteArticle-service-异常：", e);
            return -1;
        }
    }
    @Override
    public List<ArticleDetailInfoDto> QueryArticleList(QueryArticleInfoDto queryArticleInfoDto){
        try {
            log.info("查询文章列表-QueryArticleList-service-入参：{}", JSON.toJSONString(queryArticleInfoDto));
            //构建查询条件
            ArticleExample articleExample=buildArticleExample(queryArticleInfoDto);
            List<Article> articleList=articleDao.selectByExampleWithBLOBs(articleExample);//查询所有对象，包括content
            if(CollectionUtils.isEmpty(articleList)){
                return new ArrayList<>();
            }
            //构建结果集
            List<ArticleDetailInfoDto> targetList=new ArrayList<>();
            for(int i=0;i<articleList.size();i++){
                Article article=articleList.get(i);
                ArticleDetailInfoDto detailInfoDto=new ArticleDetailInfoDto();
                detailInfoDto.setId(article.getId());
                detailInfoDto.setUserId(article.getUserId());
                detailInfoDto.setContent(article.getContent());
                detailInfoDto.setTitle(article.getTitle());
                detailInfoDto.setCategoryId(article.getCategoryId());
                detailInfoDto.setType(article.getType().intValue());
                detailInfoDto.setCommentsCount(article.getCommentsCount());
                detailInfoDto.setModule(article.getModule());
                detailInfoDto.setLikes(article.getLikes());
                detailInfoDto.setViews(article.getViews());
                detailInfoDto.setStatus(article.getStatus().intValue());
                detailInfoDto.setCreateTime(DateUtils.date2Str(article.getCreateTime(), DateUtils.DATE_FORMAT));
                detailInfoDto.setUpdateTime(DateUtils.date2Str(article.getUpdateTime(), DateUtils.DATE_FORMAT));
                targetList.add(detailInfoDto);
            }


            log.info("查询文章列表-QueryArticleList-service-出参：{}", JSON.toJSONString(targetList));
            return targetList;
        }
        catch (Exception e){
            log.error("查询文章列表-QueryArticleList-service-异常：", e);
            return new ArrayList<>();
        }
    }
    @Override
    public int addLikesCount(Long  id){
        return articleMapper.addLikesCount(id);
    }
    @Override
    public int subtractLikesCount(Long  id){
        return articleMapper.subtractLikesCount(id);
    }

    private ArticleExample buildArticleExample(QueryArticleInfoDto queryArticleInfoDto) {
        if(queryArticleInfoDto== null){
            return null;
        }
        ArticleExample articleExample=new ArticleExample();
        ArticleExample.Criteria criteria=articleExample.createCriteria();
        criteria.andUserIdEqualTo(Long.valueOf(queryArticleInfoDto.getUserId()));
        criteria.andIsDeleteEqualTo((byte)0);
        articleExample.setLimit(1000);
        if(queryArticleInfoDto.getModule()!= null){
            criteria.andModuleEqualTo(queryArticleInfoDto.getModule());
        }
        if(queryArticleInfoDto.getType()!= null){
            criteria.andTypeEqualTo(queryArticleInfoDto.getType().byteValue());
        }
        if(queryArticleInfoDto.getId()!=null){
            criteria.andIdEqualTo(queryArticleInfoDto.getId());
        }
        if(queryArticleInfoDto.getCategoryId()!=null){
            criteria.andCategoryIdEqualTo(queryArticleInfoDto.getCategoryId());
        }
        if(queryArticleInfoDto.getStatus()!=null){
            criteria.andStatusEqualTo(queryArticleInfoDto.getStatus().byteValue());
        }
        return null;
    }


}
