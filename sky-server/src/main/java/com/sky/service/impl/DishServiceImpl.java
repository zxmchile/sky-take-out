package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;

/**
 * projectName: sky-take-out
 *
 * @author: 张轩鸣
 * description: 实现类
 */
@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 新增菜品和口味，涉及多表操作，保证数据一致性，需要使用事务注解
     * @param dishDTO
     */
    @Override
    @Transactional
    public void saveWithFlavor(@RequestBody DishDTO dishDTO) {
        // 向【菜品表】插入一条数据
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);//属性拷贝
        dishMapper.insert(dish);
        // 向【口味表】插入多条数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            for (DishFlavor flavor : flavors) {
                flavor.setDishId(dish.getId());
            }
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult page(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());

        Page<Dish> page = dishMapper.pageQuery(dishPageQueryDTO);

        long total = page.getTotal();
        List<Dish> records = page.getResult();

        return new PageResult(total, records);
    }

    /**
     * 删除菜品
     * @param ids
     */
    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {
        // 1. 判断当前菜品是否能够删除--删除的菜品状态是起售状态
        for (Long id : ids) {
            Dish dish = dishMapper.getById(id);
            // 判断是否处于起售中
            if (dish.getStatus() == StatusConstant.ENABLE) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        // 2. 判断当前菜品是否能够删除--删除的菜品关联了套餐
        List<Long> setmealIds = setmealMapper.getSetmealIdsByDishIds(ids);
        if (setmealIds != null && setmealIds.size() > 0) {
            // 当前菜品有关联的套餐
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        // 3. 删除菜品表中的数据
        for (Long id : ids) {
            dishMapper.deleteById(id);
            dishFlavorMapper.deleteByDishId(id);
        }

    }

    /**
     * 根据id查询菜品和口味
     * @param id
     * @return
     */
    @Override
    public DishVO getByIdWithFlavor(Long id) {
        // 先查询菜品数据
        Dish dish = dishMapper.getById(id);
        // 再查询口味数据
        List<DishFlavor> dishFlavors = dishFlavorMapper.getByDishId(id);
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish,dishVO);
        dishVO.setFlavors(dishFlavors);
        return dishVO;
    }

    /**
     * 修改菜品
     * @param dishDTO
     */
    @Override
    @Transactional
    public void update(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        // 先修改菜品表的信息
        dishMapper.update(dish);
        // 再删除原有口味数据
        dishFlavorMapper.deleteByDishId(dishDTO.getId());
        // 再插入新的口味数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            for (DishFlavor flavor : flavors) {
                flavor.setDishId(dishDTO.getId());
            }
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 获取菜品列表
     * @param dish
     * @return
     */
    @Override
    public List<DishVO> listWithFlavor(Dish dish) {
        List<Dish> dishList = dishMapper.list(dish);
        List<DishVO> dishVOList = new ArrayList<>();
        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);
            dishVO.setFlavors(dishFlavorMapper.getByDishId(d.getId()));
            dishVOList.add(dishVO);
        }
        return dishVOList;
    }

    /**
     * 获取指定分类下的菜品列表
     * @param categoryId
     * @return
     */
    @Override
    public List<Dish> list(Long categoryId) {
        Dish dish = Dish.builder().categoryId(categoryId).status(StatusConstant.ENABLE).build();
        List<Dish> dishList = dishMapper.list(dish);
        return dishList;
    }

    /**
     * 菜品起售停售
     * @param dish
     */
    @Override
    public void startOrStop(Dish dish) {
        dishMapper.update(dish);
    }


}
