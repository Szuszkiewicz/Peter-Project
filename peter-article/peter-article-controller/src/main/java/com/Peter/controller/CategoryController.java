package com.Peter.controller;

import com.Peter.CategoryService;
import com.Peter.Param.CategoryParam;
import com.Peter.dto.CategoryInfoDto;
import com.Peter.entity.Category;
import com.Peter.Param.BaseResult;
import com.Peter.utils.BaseResultUtils;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {
 @Autowired
 private CategoryService categoryService;
    @GetMapping("/selectPage")
    public BaseResult<PageInfo<Category>> selectPage(CategoryParam categoryParam ,
                                                     @RequestParam(defaultValue = "1")Integer pageNum,
                                                     @RequestParam(defaultValue = "10")Integer pageSize)
    {
        CategoryInfoDto categoryInfoDto = new CategoryInfoDto();
        BeanUtils.copyProperties(categoryParam,categoryInfoDto);

        PageInfo<Category> categoryPageInfo=categoryService.selectPage(categoryInfoDto,pageNum,pageSize);

        return BaseResultUtils.generateSuccess(categoryPageInfo);
    }
    @GetMapping("/selectAll")
    public BaseResult<List<CategoryInfoDto>> selectAll()
    {
       List<CategoryInfoDto> categoryInfoDtoList = categoryService.selectAll();
       return BaseResultUtils.generateSuccess(categoryInfoDtoList);
    }
    @GetMapping("/selectById/{id}")
    public BaseResult<CategoryInfoDto> selectById(@PathVariable Integer id)
    {
        CategoryInfoDto categoryInfoDtoList = categoryService.selectById(id);
        return BaseResultUtils.generateSuccess(categoryInfoDtoList);
    }
    @PostMapping("/add")
    public BaseResult add(@RequestBody CategoryParam categoryParam )
    {
       CategoryInfoDto categoryInfoDto = new CategoryInfoDto();
       categoryInfoDto.setName(categoryParam.getName());
       categoryService.add(categoryInfoDto);

       return BaseResultUtils.success();
    }
    @PutMapping("/update")
    public BaseResult update(@RequestBody CategoryParam categoryParam )
    {
        CategoryInfoDto categoryInfoDto = new CategoryInfoDto();
        BeanUtils.copyProperties(categoryParam,categoryInfoDto);
        categoryService.updateById(categoryInfoDto);
        return BaseResultUtils.success();
    }

    @DeleteMapping("/delete/{id}")
    public BaseResult deleteById(@PathVariable Integer id)
    {
        categoryService.deleteById(id);
        return BaseResultUtils.success();
    }

    @DeleteMapping("/delete/batch")
    public BaseResult deleteBatch(@RequestBody List<Integer> ids)
    {
        categoryService.deleteBatch(ids);
        return BaseResultUtils.success();
    }
}
