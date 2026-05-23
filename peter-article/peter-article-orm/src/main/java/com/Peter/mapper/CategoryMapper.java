package com.Peter.mapper;

import com.Peter.entity.Category;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryMapper {
    List<Category> selectAll(String  name);
}
