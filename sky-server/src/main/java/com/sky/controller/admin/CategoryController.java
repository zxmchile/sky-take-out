package com.sky.controller.admin;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * projectName: sky-take-out
 *
 * @author: 张轩鸣
 * description: 分类功能控制层
 */
@RestController
@RequestMapping("/admin/category")
@Slf4j
@Api(tags = "分类相关接口")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/list")
    @ApiOperation("菜品分类列表：供选择")
    public Result<List<Category>> list(Integer type) {
        log.info("分类列表查询");
        List<Category> list = categoryService.list(type);
        return Result.success(list);
    }

    @PostMapping
    @ApiOperation("新增分类")
    public Result save(@RequestBody CategoryDTO categoryDTO) {
        log.info("新增分类:{}", categoryDTO);
        categoryService.save(categoryDTO);
        return Result.success();
    }

    @GetMapping("/page")
    @ApiOperation("分类分页查询")
    public Result<PageResult> page(CategoryPageQueryDTO categoryPageQueryDTO) {
        log.info("分类分页查询:{}", categoryPageQueryDTO);
        PageResult pageResult = categoryService.page(categoryPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 根据id删除分类，通过请求参数获取id
     * @param id
     * @return
     */
    @DeleteMapping
    @ApiOperation("根据id删除分类")
    public Result deleteById(@RequestParam Long id) {
        log.info("根据id删除分类:{}", id);
        categoryService.deleteById(id);
        return Result.success();
    }

    /**
     * 修改分类
     * @param categoryDTO
     */
    @PutMapping
    @ApiOperation("修改分类")
    public Result update(@RequestBody CategoryDTO categoryDTO) {
        log.info("修改分类:{}", categoryDTO);
        categoryService.update(categoryDTO);
        return Result.success();
    }

    @PostMapping("/status/{status}")
    @ApiOperation("修改分类状态")
    public Result startOrStop(@PathVariable Integer status,@RequestParam Long id) {
        log.info("修改分类状态:{}", status);
        categoryService.startOrStop(status, id);
        return Result.success();
    }

//    @GetMapping("/page")
//    @ApiOperation("根据类型查询分类")
//    public Result<PageResult> page(@RequestParam Integer page, @RequestParam Integer pageSize, @RequestParam Integer type) {
//        log.info("根据类型查询分类");
//        CategoryPageQueryDTO categoryPageQueryDTO = new CategoryPageQueryDTO();
//        categoryPageQueryDTO.setType(type);
//        categoryPageQueryDTO.setPage(page);
//        categoryPageQueryDTO.setPageSize(pageSize);
//        PageResult pageResult = categoryService.page(categoryPageQueryDTO);
//        return Result.success(pageResult);
//    }
}
