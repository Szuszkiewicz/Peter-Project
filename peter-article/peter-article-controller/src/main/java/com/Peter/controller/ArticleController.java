package com.Peter.controller;

import cn.hutool.core.lang.Assert;
import com.Peter.ArticleService;
import com.Peter.Param.*;
import com.Peter.dao.ArticleDao;
import com.Peter.dto.ArticleDetailInfoDto;
import com.Peter.dto.DeleteArticleInfoDto;
import com.Peter.dto.PostArticleInfoDto;
import com.Peter.dto.QueryArticleInfoDto;
import com.Peter.entity.Article;
import com.Peter.enums.ArticleTypeEnum;
import com.Peter.enums.ModuleTypeEnum;
import com.Peter.utils.BaseResultUtils;
import com.Peter.utils.TokenUtils;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/article")
public class ArticleController {
    @Autowired
    private ArticleService articleService;
    @Autowired
    private ArticleDao articleDao;

    /**
     * 发表文章(新增或修改)
     * @param articleParam
     * @return
     */
    @PostMapping("/post")
    public BaseResult<Long> postArticle(@RequestBody ArticleParam articleParam){
        log.info("发布文章-入参：{}", JSON.toJSON(articleParam));
        //参数校验
        checkPostArticleParam(articleParam);
        //发布
        PostArticleInfoDto postArticleInfoDto=new PostArticleInfoDto();
        BeanUtils.copyProperties(articleParam,postArticleInfoDto);
        int count=articleService.PostArticle(postArticleInfoDto);
        //返回结果
        log.info("发布文章-出参：{},文章id:{}", count>0,postArticleInfoDto.getId());
        return BaseResultUtils.generateSuccess( Long.valueOf(postArticleInfoDto.getId()));
    }

    private void checkPostArticleParam(ArticleParam articleParam) {
        boolean isUpdate = StringUtils.isNotBlank(articleParam.getId());
        if(isUpdate){
            Assert.isTrue(StringUtils.isNotBlank(articleParam.getId()), "文章ID不能为空");
            //判断id是否存在
             Article article =articleDao.selectByPrimaryKey(Long.valueOf(articleParam.getId()));
             Assert.isTrue(article != null, "文章不存在");
             Assert.isTrue(article.getIsDelete()==0, "文章已被删除");
        }else {
            Assert.isTrue(articleParam != null, "参数不能为空");
            Assert.isTrue(articleParam.getUserId() != null, "用户ID不能为空");
            Assert.isTrue(articleParam.getModule() != null, "模块不能为空");
            Assert.isTrue(articleParam.getType() != null, "类型不能为空");
            Assert.isTrue(articleParam.getTitle() != null, "标题不能为空");
            Assert.isTrue(articleParam.getContent() != null, "内容不能为空");
        }
    }
    /**
     * 删除文章
     * @param deleteArticleParam
     * @return
     */
    @PostMapping("/delete")
    public BaseResult< Boolean> deleteArticle(@RequestBody DeleteArticleParam deleteArticleParam){
        try {
            //参数校验
            Assert.isTrue(deleteArticleParam != null, "参数不能为空");
            Assert.isTrue(deleteArticleParam.getUserId() != null, "用户ID不能为空");
            Assert.isTrue(deleteArticleParam.getId() != null, "ID不能为空");
            //删除
            DeleteArticleInfoDto deleteArticleInfoDto = new DeleteArticleInfoDto();
            deleteArticleInfoDto.setId(Long.valueOf(deleteArticleParam.getId()));
            deleteArticleInfoDto.setUserId(deleteArticleParam.getUserId());
            int deleteCount = articleService.DeleteArticle(deleteArticleInfoDto);

            //返回结果
            log.info("删除文章-出参：{}", deleteCount);

            return BaseResultUtils.generateSuccess(deleteCount > 0);
        } catch (Exception e) {
            return BaseResultUtils.generateError(e.getMessage());
        }

    }
    /**
     * 查询文章列表
     *
     */
    @RequestMapping("/query/list")
    public PageResultWrapper<ArticleDetailInfoRes> queryArticleList(@RequestBody ArticleParam articleParam) {
        try {
            log.info("查询文章列表-入参：{}", JSON.toJSON(articleParam));
            //参数校验
            Assert.isTrue(articleParam != null, "参数不能为空");
            Assert.isTrue(articleParam.getModule() != null, "文章模块不能为空");

          //构建查询条件
            QueryArticleInfoDto queryArticleInfoDto = buildQueryArticleInfoDto(articleParam);
            //查询总数
            int total=articleService.countArticle(queryArticleInfoDto);
            List<ArticleDetailInfoDto> articleDetailInfoDto = articleService.QueryArticleList(queryArticleInfoDto);
            List<ArticleDetailInfoRes> targetList = buildArticleList(articleDetailInfoDto);
            log.info("查询文章列表-出参：{}",JSON.toJSONString(targetList));
            //返回结果集
            return PageResultWrapper.page(targetList,total,queryArticleInfoDto.getPageNum(),queryArticleInfoDto.getPageSize());
        } catch (Exception e) {
            log.error("查询文章列表-异常：{}", e.getMessage());
            return PageResultWrapper.fail("-1","查询文章列表异常");
        }
    }

   private QueryArticleInfoDto buildQueryArticleInfoDto(ArticleParam articleParam) {
       QueryArticleInfoDto queryArticleInfoDto = new QueryArticleInfoDto();
       BeanUtils.copyProperties(articleParam, queryArticleInfoDto);
       if (StringUtils.isNotBlank(articleParam.getId())) {
           queryArticleInfoDto.setId(Long.valueOf(articleParam.getId()));
       }
//       if (!CollectionUtils.isEmpty(articleParam.getIds())) {
//           List<Long> ids = articleParam.getIds().stream().map(x -> {
//               return Long.valueOf(x);
//           }).collect(Collectors.toList());
//           QueryArticleInfoDto.setIds(ids);
//       }
       if (queryArticleInfoDto.getPageNum() == null || queryArticleInfoDto.getPageSize() == null) {
           queryArticleInfoDto.setPageNum(1);
           queryArticleInfoDto.setPageSize(10);
       }
       return queryArticleInfoDto;
    }

    /**
         * 根据id查询文章
         * @param
         * @return
         */
        @RequestMapping("/query")
        public BaseResult<ArticleDetailInfoRes> queryArticleById(@RequestParam("id") String id){
        log.info("查询文章-ById-入参：{}", id);
        //参数校验
            Assert.isTrue(StringUtils.isNotBlank(id), "id不能为空");
        //根据id查询文章
        ArticleDetailInfoDto articleDetailInfoDto=articleService.queryArticleById(Long.valueOf(id));
        ArticleDetailInfoRes articleDetailInfoRes = new ArticleDetailInfoRes();
        BeanUtils.copyProperties(articleDetailInfoDto,articleDetailInfoRes);
        articleService.addViewsCount(Long.valueOf(id));
        return BaseResultUtils.generateSuccess(articleDetailInfoRes);
    }
    /**
     * 分页查询
     * @param
     * @return
     */
    @GetMapping("/selectPage")
    public PageResultWrapper<ArticleDetailInfoRes> selectPage(ArticleParam articleParam,
                                                              @RequestParam(defaultValue="1")Integer pageNum,
                                                              @RequestParam(defaultValue="10")Integer pageSize){
        articleParam.setPageNum(pageNum);
        articleParam.setPageSize(pageSize);
        articleParam.setModule(ModuleTypeEnum.WEBSITE.getCode());
        articleParam.setType(ArticleTypeEnum.ARTICLE.getCode());
        return this.queryArticleList(articleParam);
    }

    /**
     * 查询文章榜单
     *
     */
    @GetMapping("/selectRank")
    public BaseResult<List<ArticleDetailInfoDto>> selectRank(){
        List<ArticleDetailInfoDto> list=articleService.selectRank();
        return BaseResultUtils.generateSuccess(list);
    }
    /**
     * 分页查询当前用户的文章列表
     */
    @GetMapping("/selectUser")
    public PageResultWrapper<ArticleDetailInfoRes> selectUser(ArticleParam articleParam,
                                                            @RequestParam(defaultValue="1")Integer pageNum,
                                                            @RequestParam(defaultValue="10")Integer pageSize){
        articleParam.setPageNum(pageNum);
        articleParam.setPageSize(pageSize);
        articleParam.setModule(ModuleTypeEnum.WEBSITE.getCode());
        PageResultWrapper<ArticleDetailInfoRes> articleDetailInfoDtoPageResultWrapper=this.queryArticleList(articleParam);
        if(!articleDetailInfoDtoPageResultWrapper.isSuccess()||CollectionUtils.isEmpty(articleDetailInfoDtoPageResultWrapper.getData())){
            return PageResultWrapper.absent();
        }
        List<ArticleDetailInfoRes> data=articleDetailInfoDtoPageResultWrapper.getData();
        for(int i=0;i<data.size();i++){
            ArticleDetailInfoRes articleDetailInfoRes=data.get(i);
            articleDetailInfoRes.setUserName(TokenUtils.getUsername());
            articleDetailInfoRes.setAvatar(TokenUtils.getAvatar());
        }
        return  PageResultWrapper.page( data, articleDetailInfoDtoPageResultWrapper.getTotal(),pageNum,pageSize);
    }
     /**
     * 文章推荐
     * @return
     */
    @GetMapping("/selectRecommend/{id}")
    public BaseResult<Set<ArticleDetailInfoDto>> selectRecommend(@PathVariable Long id){
        Set<ArticleDetailInfoDto> detailInfoDtoSet =articleService.selectRecommend(id);
        return BaseResultUtils.generateSuccess(detailInfoDtoSet);
    }
    @PostMapping("/addViews")
    public BaseResult<Boolean> addViews(@RequestParam Long  id){
        try {
            log.info("添加文章浏览量-入参：{}", id);
            Assert.isTrue(id != null, "id不能为空");
            int count=articleService.addViewsCount(id);
            log.info("添加文章浏览量-出参：{}", count);
            return BaseResultUtils.generateSuccess(count>0);
        }catch (Exception e){
            return BaseResultUtils.generateError(e.getMessage());
        }
    }
    private List<ArticleDetailInfoRes> buildArticleList(List<ArticleDetailInfoDto> articleDetailInfoDtos) {
        if(CollectionUtils.isEmpty(articleDetailInfoDtos)){
            return new ArrayList<>();
        }
        List<ArticleDetailInfoRes> targetList=new ArrayList<>();
        for(int i=0;i<articleDetailInfoDtos.size();i++){
            ArticleDetailInfoDto detailInfoDto = articleDetailInfoDtos.get(i);
            ArticleDetailInfoRes target = new ArticleDetailInfoRes();
            BeanUtils.copyProperties(detailInfoDto,target);
            targetList.add(target);
        }
        return targetList;
    }
}
