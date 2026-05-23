package com.Peter.Impl;

import com.Peter.CategoryService;
import com.Peter.dao.CategoryDao;
import com.Peter.dto.CategoryInfoDto;
import com.Peter.entity.Category;
import com.Peter.mapper.CategoryMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private CategoryDao categoryDao;

    @Override
    public PageInfo<Category> selectPage(CategoryInfoDto categoryInfoDto, Integer pageNum, Integer pageSize){
        //查询总数
        PageHelper.startPage(pageNum,pageSize);
        List<Category> categoryList= categoryMapper.selectAll(categoryInfoDto.getName());
        return PageInfo.of(categoryList);
    }
    @Override
    public void add(CategoryInfoDto categoryInfoDto){
        Category category=new Category();
        category.setName(categoryInfoDto.getName());
        int insert=categoryDao.insert(category);
    }
    @Override
    public void updateById(CategoryInfoDto categoryInfoDto){
        Category category=new Category();
        category.setId(categoryInfoDto.getId());
        category.setName(categoryInfoDto.getName());
        categoryDao.updateByPrimaryKey(category);
    }
    @Override
    public void deleteById(Integer id){
        categoryDao.deleteByPrimaryKey(id);
    }
    @Override
    public void deleteBatch(List<Integer> ids){
        if(CollectionUtils.isEmpty(ids)){
            return;
        }
        for(Integer id:ids){
            categoryDao.deleteByPrimaryKey(id);//一个一个删除
        }
    }
    @Override
    public List<CategoryInfoDto> selectAll(){
        List<Category> categories=categoryMapper.selectAll( null);
        if(CollectionUtils.isEmpty(categories)){
            return Collections.emptyList();
        }
        List<CategoryInfoDto> categoryInfoDtos =new ArrayList<>();
        for(int i=0;i<categories.size();i++){
            Category category=categories.get(i);
            CategoryInfoDto categoryInfoDto=new CategoryInfoDto();
            categoryInfoDto.setId(category.getId());
            categoryInfoDto.setName(category.getName());
            categoryInfoDtos.add(categoryInfoDto);
        }
        return categoryInfoDtos;
    }//先从mapper中拿出列表
     //再把列表用同类型一个一个取出来
     //再把同类型转换为Dto类型
     //再把Dto类型放到新的列表中
    @Override
    public CategoryInfoDto selectById(Integer id){
        Category category=categoryDao.selectByPrimaryKey(id);
        CategoryInfoDto categoryInfoDto=new CategoryInfoDto();
        categoryInfoDto.setId(category.getId());
        categoryInfoDto.setName(category.getName());
        return categoryInfoDto;
    }
}
