package com.sky.controller.user;

import com.sky.constant.StatusConstant;
import com.sky.entity.Setmeal;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.CacheNamespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * projectName: sky-take-out
 *
 * @author: 张轩鸣
 * description:
 */
@RestController("UserSetmealController")
@RequestMapping("/user/setmeal")
@Api("套餐接口")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @GetMapping("/list")
    @ApiOperation("查询套餐")
    @Cacheable(cacheNames = "setmealCache",key = "#categoryId") // 缓存套餐数据：查询redis中缓存的套餐数据，若redis中不存在就查询数据库
    public Result<List<Setmeal>> list(Long categoryId) {
        log.info("查询分类id：{}的套餐", categoryId);
        Setmeal setmeal = Setmeal.builder()
                .categoryId(categoryId)
                .status(StatusConstant.ENABLE)
                .build();
        List<Setmeal> list = setmealService.list(setmeal);
        return Result.success(list);
    }

    @GetMapping("/dish/{id}")
    @ApiOperation("查询套餐和菜品")
    public Result<List<DishItemVO>> dishList(@PathVariable Long id) {
        List<DishItemVO> dishItemVOList = setmealService.getDishItemById(id);
        return Result.success(dishItemVOList);
    }
}
