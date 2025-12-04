package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * projectName: sky-take-out
 *
 * @author: 张轩鸣
 * description:
 */
@Mapper
public interface SetmealDishMapper {

    /**
     * 根据套餐id查询套餐菜品
     * @param setmealId
     * @return
     */
    List<SetmealDish> getBySetmealId(Long setmealId);

    /**
     * 批量插入套餐菜品数据
     * @param setmealDishes
     */
    void insertBatch(List<SetmealDish> setmealDishes);

    /**
     * 批量删除套餐菜品数据
     * @param ids
     */
    void deleteBySetmealIds(List<Long> ids);

}
