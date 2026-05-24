package com.Peter.mapper;

import com.Peter.entity.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


import java.util.List;

@Mapper
public interface ArticleMapper {
    int addLikesCount(Long  id);

    int subtractLikesCount(Long  id);

    int addCollectsCount(Long  id);

    int subtractCollectsCount(Long  id);

    int addViewsCount(Long  id);

    List<Article> selectAll(@Param("article") Article article, @Param("distinct") Boolean distinct);
}