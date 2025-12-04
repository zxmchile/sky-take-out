package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.ShoppingCart;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * projectName: sky-take-out
 *
 * @author: 张轩鸣
 * description:
 */
@Mapper
public interface ShoppingCartMapper {

    /**
     * 查询购物车
     * @param shoppingCart
     * @return
     */
    List<ShoppingCart> list(ShoppingCart shoppingCart);

    /**
     * 修改购物车
     * @param onlyShoppingCart
     */
    void update(ShoppingCart onlyShoppingCart);

    /***
     * 插入购物车
     * @param shoppingCart
     */
    void insert(ShoppingCart shoppingCart);

    /**
     * 查看购物车
     * @param shoppingCart
     * @return
     */
    List<ShoppingCart> showShoppingCart(ShoppingCart shoppingCart);

    /**
     * 根据用户id删除购物车
     * @param currentId
     */
    @Delete("delete from shopping_cart where user_id = #{currentId}")
    void deleteByUserId(Long currentId);

    /**
     * 删除单个购物车
     * @param shoppingCart
     */
    void delete(ShoppingCart shoppingCart);
}
