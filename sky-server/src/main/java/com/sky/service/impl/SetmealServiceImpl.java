package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * projectName: sky-take-out
 *
 * @author: 张轩鸣
 * description:
 */
@Service
@Slf4j
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Autowired
    private DishMapper dishMapper;

    /**
     * 获取套餐列表
     * @param setmeal
     * @return
     */
    @Override
    public List<Setmeal> list(Setmeal setmeal) {
        log.info("查询套餐：{}", setmeal);
        List<Setmeal> list = setmealMapper.list(setmeal);
        return list;
    }

    /**
     * 获取套餐详情
     * @param id
     * @return
     */
    @Override
    public List<DishItemVO> getDishItemById(Long id) {
        return setmealMapper.getDishesBySetmealId(id);
    }

    /**
     * 新增套餐
     * TODO：这里因为BeanUtils.copyProperties()方法，没能将id赋给setmealDish，需要修改，等你来处理
     * @param setmealDTO
     */
    @Override
    public void saveWithDish(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        // 向套餐表插入数据
        setmealMapper.insert(setmeal);
        // 获取id
        Long setmealId = setmeal.getId();
        System.out.println("setmealId = " + setmealId);
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if (setmealDishes != null && setmealDishes.size() > 0) {
            for (SetmealDish setmealDish : setmealDishes) {
                setmealDish.setSetmealId(setmealId);
            }
            // 向套餐菜品关系表插入数据
            setmealDishMapper.insertBatch(setmealDishes);
        }
    }

    /**
     * 套餐分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    @Override
    public PageResult page(SetmealPageQueryDTO setmealPageQueryDTO) {
        // 开启分页
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        // 调用分页查询方法
        Page<SetmealVO> page = setmealMapper.page(setmealPageQueryDTO);
        // 获取总记录数
        long total = page.getTotal();
        // 获取分页结果
        List<SetmealVO> result = page.getResult();
        // 将结果封装成PageResult并返回
        return new PageResult(total,result);
    }

    /**
     * 批量删除套餐
     * @param ids
     */
    @Override
    public void deleteBatch(List<Long> ids) {
        for (Long id : ids) {
            Setmeal setmeal = setmealMapper.getById(id);
            if (setmeal.getStatus() == 1) {
                throw new DeletionNotAllowedException("起售中的套餐不能删除");
            }
        }
        setmealMapper.deleteBatch(ids);
        setmealDishMapper.deleteBySetmealIds(ids);
    }

    /**
     * 修改套餐
     * @param setmealDTO
     */
    @Override
    public void update(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        // 修改套餐表
        setmealMapper.update(setmeal);
        // 删除套餐和菜品的关联关系
        setmealDishMapper.deleteBySetmealIds(new ArrayList<Long>() {{
            add(setmeal.getId());
        }});
        // 重新插入新的关联关系
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if (setmealDishes != null && !setmealDishes.isEmpty()) {
            // 关键步骤：为每个菜品设置套餐ID
            for (SetmealDish dish : setmealDishes) {
                dish.setSetmealId(setmeal.getId());
            }
            setmealDishMapper.insertBatch(setmealDishes);
        }
    }

    /**
     * 根据id查询套餐数据
     * @param id
     * @return
     */
    @Override
    public SetmealVO getByIdWithDish(Long id) {
        // 根据id查询套餐数据
        Setmeal setmeal = setmealMapper.getById(id);
        List<SetmealDish> setmealDishes = setmealDishMapper.getBySetmealId(id);
        // 将数据封装成SetmealVO
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal, setmealVO);
        setmealVO.setSetmealDishes(setmealDishes);
        // 返回
        return setmealVO;
    }

    @Override
    public void startOrStop(Integer status, Long id) {
        Setmeal setmeal = Setmeal.builder()
                .id(id)
                .status(status)
                .build();
        setmealMapper.update(setmeal);
    }
}
