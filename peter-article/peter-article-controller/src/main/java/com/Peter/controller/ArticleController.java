package com.Peter.controller;

import cn.hutool.core.lang.Assert;
import com.Peter.ArticleService;
import com.Peter.Param.ArticleDetailInfoRes;
import com.Peter.Param.ArticleParam;
import com.Peter.Param.BaseResult;
import com.Peter.Param.DeleteArticleParam;
import com.Peter.dto.ArticleDetailInfoDto;
import com.Peter.dto.DeleteArticleInfoDto;
import com.Peter.dto.PostArticleInfoDto;
import com.Peter.dto.QueryArticleInfoDto;
import com.Peter.utils.BaseResultUtils;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/article")
public class ArticleController {
    @Autowired
    private ArticleService articleService;
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
        Assert.isTrue(articleParam!=null, "参数不能为空");
        Assert.isTrue(articleParam.getUserId()!=null, "用户ID不能为空");
        Assert.isTrue(articleParam.getModule()!=null, "类型不能为空");
        Assert.isTrue(articleParam.getTitle()!=null, "类型不能为空");
        Assert.isTrue(articleParam.getTitle()!=null, "标题不能为空");
        Assert.isTrue(articleParam.getContent()!=null, "内容不能为空");
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
    @PostMapping("/query/list")
    public BaseResult<List<ArticleDetailInfoRes>> queryArticleList(@RequestBody ArticleParam articleParam){
        try {
            log.info("查询文章列表-入参：{}", JSON.toJSON(articleParam));
            //参数校验
            Assert.isTrue(articleParam!= null, "参数不能为空");
            Assert.isTrue(articleParam.getUserId() != null, "用户ID不能为空");

            //查询
            QueryArticleInfoDto queryArticleInfoDto = new QueryArticleInfoDto();
            BeanUtils.copyProperties(articleParam, queryArticleInfoDto);
           if(StringUtils.isNotBlank(articleParam.getId())){
               queryArticleInfoDto.setId(Long.valueOf(articleParam.getId()));
           }

            List<ArticleDetailInfoDto> articleDetailInfoDtos = articleService.QueryArticleList(queryArticleInfoDto);
            List<ArticleDetailInfoRes> targetList=buildArticleList(articleDetailInfoDtos);
            BeanUtils.copyProperties(articleDetailInfoDtos,targetList);
            log.info("查询文章列表-出参：{}",targetList);
           //返回结果集
            return BaseResultUtils.generateSuccess(targetList);
        } catch (Exception e) {
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
