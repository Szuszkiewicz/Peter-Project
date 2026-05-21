package com.Peter.dao;

import com.Peter.entity.ArticleLikes;
import com.Peter.entity.ArticleLikesExample;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
@Mapper
public interface ArticleLikesDao {
    long countByExample(ArticleLikesExample example);

    int deleteByExample(ArticleLikesExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(ArticleLikes record);

    int insertSelective(ArticleLikes record);

    List<ArticleLikes> selectByExample(ArticleLikesExample example);

    ArticleLikes selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") ArticleLikes record, @Param("example") ArticleLikesExample example);

    int updateByExample(@Param("record") ArticleLikes record, @Param("example") ArticleLikesExample example);

    int updateByPrimaryKeySelective(ArticleLikes record);

    int updateByPrimaryKey(ArticleLikes record);
}