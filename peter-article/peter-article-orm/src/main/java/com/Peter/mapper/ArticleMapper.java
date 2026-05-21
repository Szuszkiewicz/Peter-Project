package com.Peter.mapper;

import org.apache.ibatis.annotations.Mapper;


import java.util.List;

@Mapper
public interface ArticleMapper {
    int addLikesCount(Long  id);

    int subtractLikesCount(Long  id);
}