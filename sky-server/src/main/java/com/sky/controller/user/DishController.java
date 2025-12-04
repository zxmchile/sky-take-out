package com.sky.controller.user;

import com.sky.constant.StatusConstant;
import com.sky.entity.Dish;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * projectName: sky-take-out
 *
 * @author: 张轩鸣
 * description:
 */
@RestController("UserDishController")
@RequestMapping("/user/dish")
@Api("菜品管理")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<DishVO>> list(Long categoryId) {
        log.info("查询分类id：{}的菜品", categoryId);
        // 构造key：dish_id
        String key = "dish_" + categoryId;
        // 查询redis中是否存在菜品数据
        List<DishVO> list = (List<DishVO>) redisTemplate.opsForValue().get(key);
        // 存在，直接返回
        if (list != null && list.size() > 0) {
            return Result.success(list);
        }
        // 不存在，需要查询数据库，将数据缓存到redis中
        // 创建查询条件
        Dish dish = new Dish();
        dish.setCategoryId(categoryId);
        dish.setStatus(StatusConstant.ENABLE);
        // 查询
        list= dishService.listWithFlavor(dish);
        // 载入缓存
        redisTemplate.opsForValue().set(key, list);
        return Result.success(list);
    }
}
