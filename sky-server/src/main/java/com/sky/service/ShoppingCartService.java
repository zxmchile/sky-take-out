package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

/**
 * projectName: sky-take-out
 *
 * @author: 张轩鸣
 * description:
 */
public interface ShoppingCartService {

    /**
     * 添加购物车
     */
    void addShoppingCart(ShoppingCartDTO shoppingCartDTO);

    /**
     * 获取购物车
     * @return
     */
    List<ShoppingCart> showShoppingCart();

    /**
     * 删除购物车
     * @param shoppingCartDTO
     */
    void sub(ShoppingCartDTO shoppingCartDTO);

    /**
     * 清空购物车
     */
    void cleanShoppingCart();
}
