package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * projectName: sky-take-out
 *
 * @author: 张轩鸣
 * description:
 */
@Mapper
public interface SetmealMapper {

    /**
     * 根据菜品id查询套餐数量
     * @param dishId
     * @return
     */
    @Select("select count(id) from setmeal where dish_id = #{dishId}")
    Integer countByDishId(Long dishId);

    /**
     * 根据菜品id查询套餐id
     * @param ids
     * @return
     */
    List<Long> getSetmealIdsByDishIds(List<Long> ids);
}
