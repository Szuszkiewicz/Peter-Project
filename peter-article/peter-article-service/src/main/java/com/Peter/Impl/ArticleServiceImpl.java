package com.Peter.Impl;

import com.Peter.ArticlePostService;
import com.Peter.ArticleService;
import com.Peter.dao.ArticleDao;
import com.Peter.dto.ArticleDetailInfoDto;
import com.Peter.dto.DeleteArticleInfoDto;
import com.Peter.dto.PostArticleInfoDto;
import com.Peter.dto.QueryArticleInfoDto;
import com.Peter.entity.Article;
import com.Peter.entity.ArticleExample;
import com.Peter.enums.ArticleOperationTypeEnums;
import com.Peter.factory.ArticleFactory;
import com.Peter.mapper.ArticleMapper;
import com.Peter.utils.DateUtils;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


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
            //修改为策略模式
            ArticlePostService articlePostService=ArticleFactory.fetchArticleService(postArticleInfoDto.getId()!=null? ArticleOperationTypeEnums.UPDATE:ArticleOperationTypeEnums.ADD);
            return articlePostService.doAction(postArticleInfoDto);
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
                ArticleDetailInfoDto detailInfoDto = getArticleDetailInfoDto(article);
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

    public  ArticleDetailInfoDto getArticleDetailInfoDto(Article article) {
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
        detailInfoDto.setCover(article.getCover());
        detailInfoDto.setTags(article.getTags());
        detailInfoDto.setDesc(article.getDesc());
        detailInfoDto.setStatus(article.getStatus().intValue());
        detailInfoDto.setCreateTime(article.getCreateTime() != null ? DateUtils.date2Str(article.getCreateTime(), DateUtils.DATE_FORMAT) : null);
        detailInfoDto.setUpdateTime(article.getUpdateTime()!= null ? DateUtils.date2Str(article.getUpdateTime(), DateUtils.DATE_FORMAT) : null);
        return detailInfoDto;
    }

    @Override
    public  ArticleDetailInfoDto queryArticleById(Long id) {
        try {
            log.info("查询文章列表-queryArticleById-service-入参：{}", id);
            Article article = articleDao.selectByPrimaryKey(id);
            if(article==null){
                log.error("无法查询不存在的文章:{}", id);
                return null;
            }
            ArticleDetailInfoDto detailInfoDto = new ArticleDetailInfoDto();
            if (article.getIsDelete().equals((byte) 1)) {
                log.error("无法查询已被删除的文章:{}", id);
                return null;
            }
            detailInfoDto.setId(article.getId());
            detailInfoDto.setUserId(article.getUserId());
            detailInfoDto.setCover(article.getCover());
            detailInfoDto.setDesc(article.getDesc());
            detailInfoDto.setTags(article.getTags());
            detailInfoDto.setContent(article.getContent());
            detailInfoDto.setTitle(article.getTitle());
            detailInfoDto.setCategoryId(article.getCategoryId());
            detailInfoDto.setType(article.getType().intValue());
            detailInfoDto.setCommentsCount(article.getCommentsCount());
            detailInfoDto.setModule(article.getModule());
            detailInfoDto.setLikes(article.getLikes());
            detailInfoDto.setCollects(article.getCollects());
            detailInfoDto.setViews(article.getViews());
            detailInfoDto.setStatus(article.getStatus().intValue());
            detailInfoDto.setCreateTime(DateUtils.date2Str(article.getCreateTime(), DateUtils.DATE_FORMAT));
            detailInfoDto.setUpdateTime(DateUtils.date2Str(article.getUpdateTime(), DateUtils.DATE_FORMAT));
            return detailInfoDto;
        } catch (Exception e) {
            log.error("根据id查询文章-queryArticleById-service-异常：", e);
            return null;
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

    @Override
    public int addCollectsCount(Long id){
        return articleMapper.addCollectsCount(id);
    }
    @Override
    public int subtractCollectsCount(Long id){
        return articleMapper.subtractCollectsCount(id);
    }
    @Override
    public int addViewsCount(Long id){
        try {
            log.info("增加文章浏览数-addViewsCount-service-入参：{}", id);
            Article article = articleDao.selectByPrimaryKey(id);
            if (article == null) {
                log.error("该文章不存在:{}", id);
                return -1;
            }
            if (article.getIsDelete().equals((byte) 1)) {
                log.error("该文章已被删除:{}", id);
                return -1;
            }
            int count = articleMapper.addViewsCount(id);
            log.info("增加文章浏览数-addViewsCount-service-出参：{}", count);
            return count;
        } catch (Exception e) {
            log.error("增加文章浏览数-addViewsCount-service-异常:{}", e.getMessage());
            return -1;
        }
    }
    @Override
    public int countArticle(QueryArticleInfoDto queryArticleInfoDto){
        try {
            log.info("查询文章总数-countArticle-service-入参：{}", JSON.toJSONString(queryArticleInfoDto));
            ArticleExample articleExample=buildArticleExample(queryArticleInfoDto);
            int count = Math.toIntExact(articleDao.countByExample(articleExample));
            log.info("查询文章总数-countArticle-service-出参：{}", count);
            return count;
        }catch (Exception e){
            log.error("查询文章总数-countArticle-service-异常：", e);
            return -1;
        }
    }
    /**
     * 榜单推荐逻辑：浏览量，取前20条
     */
    @Override
    public List<ArticleDetailInfoDto> selectRank(){
      List<Article> articleList =articleMapper.selectAll(null,true);

      articleList.stream().sorted((o1, o2)->o2.getViews().compareTo(o1.getViews()))
              .limit(20)
              .collect(Collectors.toList());

      List<ArticleDetailInfoDto> articleDetailInfoDtoList= getArticleDetailInfoDtos(articleList);
      return articleDetailInfoDtoList;
    }

    private List<ArticleDetailInfoDto> getArticleDetailInfoDtos(List<Article> articleList) {
        if(CollectionUtils.isEmpty(articleList)){
            return new ArrayList<>();
        }
        List<ArticleDetailInfoDto> articleDetailInfoDtoList=new ArrayList<>();
        for(int i=0;i<articleList.size();i++){
            Article article=articleList.get(i);
            ArticleDetailInfoDto articleDetailInfoDto=getArticleDetailInfoDto(article);
            articleDetailInfoDtoList.add(articleDetailInfoDto);
        }
        return articleDetailInfoDtoList;
    }
    @Override
    public Set<ArticleDetailInfoDto> selectRecommend(Long id){
        log.info("开始查询文章推荐，文章ID: {}", id);
        //根据标签推荐
        ArticleDetailInfoDto articleDetailInfoDto=this.queryArticleById(id);
        if(articleDetailInfoDto == null){
            log.warn("文章不存在，无法获取推荐: {}", id);
            return new HashSet<>();
        }

        String tags=articleDetailInfoDto.getTags();
        log.info("当前文章的标签: {}", tags);

        Set<Article> articleSet=new HashSet<>();
        List<Article> articleList=articleMapper.selectAll(null, true);
        log.info("数据库中共有 {} 篇文章", articleList.size());

        if(!StringUtils.isEmpty(tags)){
            try {
                JSONArray tagsArr = JSONArray.parseArray(tags);
                log.info("解析出 {} 个标签(JSON格式)", tagsArr.size());
                for (Object tag : tagsArr) {
                    String tagStr = tag.toString();
                    log.info("正在匹配标签: {}", tagStr);
                    Set<Article> collect = articleList.stream()
                            .filter(b -> b.getTags() != null && b.getTags().contains(tagStr))
                            .filter(b -> !b.getId().equals(id))
                            .collect(Collectors.toSet());
                    log.info("标签 '{}' 匹配到 {} 篇文章", tagStr, collect.size());
                    articleSet.addAll(collect);
                }
            } catch (Exception e) {
                log.warn("tags字段不是JSON格式，按普通文本处理: {}", tags);
                String[] tagArray = tags.split(",");
                log.info("拆分出 {} 个标签", tagArray.length);
                for (String tag : tagArray) {
                    String trimmedTag = tag.trim();
                    if (!StringUtils.isEmpty(trimmedTag)) {
                        log.info("正在匹配标签: {}", trimmedTag);
                        Set<Article> collect = articleList.stream()
                                .filter(b -> b.getTags() != null && b.getTags().contains(trimmedTag))
                                .filter(b -> !b.getId().equals(id))
                                .collect(Collectors.toSet());
                        log.info("标签 '{}' 匹配到 {} 篇文章", trimmedTag, collect.size());
                        articleSet.addAll(collect);
                    }
                }
            }
        } else {
            log.info("当前文章没有标签，将返回热门文章");
        }

        log.info("去重后共匹配到 {} 篇文章", articleSet.size());

        // 如果基于标签的推荐结果为空，或当前文章没有标签，返回热门文章
        if(articleSet.isEmpty()){
            log.info("标签推荐结果为空，返回热门文章作为补充");
            if(articleList != null && !articleList.isEmpty()) {
                List<Article> hotArticles = articleList.stream()
                        .filter(a -> !a.getId().equals(id))
                        .filter(a -> a.getIsDelete() != null && a.getIsDelete() == 0)
                        .sorted((a1, a2) -> {
                            Integer views1 = a1.getViews() != null ? a1.getViews() : 0;
                            Integer views2 = a2.getViews() != null ? a2.getViews() : 0;
                            return views2.compareTo(views1);
                        })
                        .limit(5)
                        .collect(Collectors.toList());
                articleSet.addAll(hotArticles);
                log.info("返回 {} 篇热门文章", hotArticles.size());
            }
        }

        //从全集里筛选出五个
        articleSet = articleSet.stream().limit(5).collect(Collectors.toSet());
        Set<ArticleDetailInfoDto> resultSet=buildArticleDetailIntoDtoSet(articleSet);
        log.info("推荐结果: {} 篇文章", resultSet.size());

        return resultSet;
    }

    private Set<ArticleDetailInfoDto> buildArticleDetailIntoDtoSet(Set<Article> articleSet) {
        if(CollectionUtils.isEmpty(articleSet)){
            return new HashSet<>();
        }
        Set<ArticleDetailInfoDto> resultSet=new HashSet<>();
        for(Article article:articleSet){
            resultSet.add(getArticleDetailInfoDto(article));
        }
        return  resultSet;
    }


    private  ArticleExample buildArticleExample(QueryArticleInfoDto queryArticleInfoDto) {
        if(queryArticleInfoDto== null){
            return null;
        }
        ArticleExample articleExample=new ArticleExample();
        ArticleExample.Criteria criteria=articleExample.createCriteria();
        if(queryArticleInfoDto.getId()!=null) {
            criteria.andUserIdEqualTo(queryArticleInfoDto.getUserId());
        }
        criteria.andIsDeleteEqualTo((byte)0);//判断文章是不是被删除的
        articleExample.setOffset(buildOffset(queryArticleInfoDto.getPageNum(),queryArticleInfoDto.getPageSize()));
        articleExample.setLimit(queryArticleInfoDto.getPageSize());
        articleExample.setOrderByClause("id desc");
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
       return articleExample;
    }

    /**
     * 计算分页偏移量
     * @param pageNum
     * @param pageSize
     * @return
     */
    private Long buildOffset(Integer pageNum, Integer pageSize) {
        Integer offset = (pageNum - 1) * pageSize;
        return (long)Math.max(0, offset);
    }


}
