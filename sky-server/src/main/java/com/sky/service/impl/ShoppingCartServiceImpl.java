package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * projectName: sky-take-out
 *
 * @author: 张轩鸣
 * description:
 */
@Service
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 添加购物车
     * @param shoppingCartDTO
     */
    @Override
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        // 判断当前菜品是否在购物车表中
        // 若存在，则数量加1
        if (list != null && list.size() > 0) {
            ShoppingCart onlyShoppingCart = list.get(0);
            onlyShoppingCart.setNumber(onlyShoppingCart.getNumber() + 1);
            onlyShoppingCart.setUserId(BaseContext.getCurrentId());
            shoppingCartMapper.update(onlyShoppingCart);
        } else {
            // 若不存在，则添加到购物车表中一条数据
            // 判断当前添加的是菜品还是套餐
            Long dishId = shoppingCart.getDishId();
            Long setmealId = shoppingCart.getSetmealId();

            // 设置数量
            shoppingCart.setNumber(1);
            // 设置用户id
            shoppingCart.setUserId(BaseContext.getCurrentId());
            // 设置创建时间
            shoppingCart.setCreateTime(LocalDateTime.now());

            if (dishId != null) {
                // 设置菜品id
                shoppingCart.setDishId(dishMapper.getById(dishId).getId());
                // 设置菜品名称
                shoppingCart.setName(dishMapper.getById(dishId).getName());
                // 获取菜品价格
                shoppingCart.setAmount(dishMapper.getById(dishId).getPrice());
                // 获取菜品图片
                shoppingCart.setImage(dishMapper.getById(dishId).getImage());
            }else if (setmealId != null) {
                shoppingCart.setSetmealId(setmealMapper.getById(setmealId).getId());
                shoppingCart.setName(setmealMapper.getById(setmealId).getName());
                shoppingCart.setAmount(setmealMapper.getById(setmealId).getPrice());
                shoppingCart.setImage(setmealMapper.getById(setmealId).getImage());
            }
            // 插入对应的表
            shoppingCartMapper.insert(shoppingCart);
            log.info("shoppingCart = {}", shoppingCart);
        }
    }

    /**
     * 查看购物车
     * @return
     */
    @Override
    public List<ShoppingCart> showShoppingCart() {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(BaseContext.getCurrentId());
        List<ShoppingCart> list = shoppingCartMapper.showShoppingCart(shoppingCart);
        return list;
    }

    /**
     * 删除购物车
     * @param shoppingCartDTO
     */
    @Override
    public void sub(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());
        shoppingCartMapper.delete(shoppingCart);
    }

    /**
     * 清空购物车
     */
    @Override
    public void cleanShoppingCart() {
        shoppingCartMapper.deleteByUserId(BaseContext.getCurrentId());
    }

}
