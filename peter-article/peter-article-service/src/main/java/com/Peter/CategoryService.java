package com.Peter;
import com.Peter.dto.CategoryInfoDto;
import com.Peter.entity.Category;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface CategoryService {
    /**
     * 分页查询类目
     * @param categoryInfoDto
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageInfo<Category> selectPage(CategoryInfoDto categoryInfoDto,Integer pageNum,Integer pageSize);
    /**
     * 查询所有类目
     * @return
     */
    List<CategoryInfoDto> selectAll();
    /**
     * 根据id查询类目
     * @param id
     * @return
     */
    CategoryInfoDto selectById(Integer id);
    /**
     * 新增类目
     * @param category
     * @return
     */
    void add(CategoryInfoDto  category);
    /**
     * 修改类目
     * @param categoryInfoDto
     * @return
     */
    void updateById(CategoryInfoDto categoryInfoDto);
    /**
     * 根据id删除类目
     * @param id
     * @return
     */
    void deleteById(Integer id);
    /**
     * 批量删除类目
     * @param ids
     * @return
     */
    void deleteBatch(List< Integer> ids);

}