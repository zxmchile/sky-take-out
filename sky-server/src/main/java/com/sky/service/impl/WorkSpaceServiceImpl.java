package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.DishMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.WorkSpaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;


/**
 * projectName: sky-take-out
 *
 * @author: 张轩鸣
 * description: 实现类
 */
@Service
public class WorkSpaceServiceImpl implements WorkSpaceService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private DishMapper dishMapper;

    /**
     * 今日数据
     * @return
     */
    @Override
    public BusinessDataVO getBusinessData() {
        // 获取今日的开始时间以及结束时间
        LocalDateTime begin = LocalDateTime.now().with(LocalTime.MIN);
        LocalDateTime end = LocalDateTime.now().with(LocalTime.MAX);
        Map map = new HashMap();
        map.put("status", 5);
        map.put("begin", begin);
        map.put("end", end);
        // 1.获取营业额
        Double turnover = orderMapper.sumByMap(map);
        turnover = turnover == null ? 0.0 : turnover;
        // 2.获取有效订单数
        Integer validOrderCount = orderMapper.countByMap(map);
        validOrderCount = validOrderCount == null ? 0 : validOrderCount;
        // 3.获取订单完成数，订单完成率
        map.put("status", 4);
        Integer completedOrderCount = orderMapper.countByMap(map);
        completedOrderCount = completedOrderCount == null ? 0 : completedOrderCount;
        Double orderCompletionRate = 0.0; // 避免除0
        if (validOrderCount > 0) {
            orderCompletionRate = completedOrderCount.doubleValue() / validOrderCount;
        }
        // 4.获取新增用户数
        Integer newUsers = userMapper.countByCreateTime(begin, end);
        newUsers = newUsers == null ? 0 : newUsers;
        // 5.获取平均客单价
        map.put("status", 5);
        Double unitPrice = 0.0;
        if (completedOrderCount > 0) {
            unitPrice = orderMapper.sumByMap(map) / completedOrderCount;
        }
        unitPrice = unitPrice == null ? 0.0 : unitPrice;
        // 6.封装VO并返回
        BusinessDataVO businessDataVO = BusinessDataVO.builder()
                .turnover(turnover)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .unitPrice(unitPrice)
                .newUsers(newUsers)
                .build();
        return businessDataVO;
    }

    /**
     * 订单统计
     * @return
     */
    @Override
    public OrderOverViewVO getOrderOverView() {
        Map map = new HashMap();
        // 待接单
        map.put("status", Orders.TO_BE_CONFIRMED);
        Integer waitingOrders = orderMapper.countByMap(map);
        // 待派送
        map.put("status", Orders.CONFIRMED);
        Integer deliveredOrders = orderMapper.countByMap(map);
        // 已完成
        map.put("status", Orders.COMPLETED);
        Integer completedOrders = orderMapper.countByMap(map);
        // 已取消
        map.put("status", Orders.CANCELLED);
        Integer cancelledOrders = orderMapper.countByMap(map);
        // 全部订单
        map.put("status", null);
        Integer allOrders = orderMapper.countByMap(map);
        // 封装VO并返回
        OrderOverViewVO orderOverViewVO = OrderOverViewVO.builder()
                .waitingOrders(waitingOrders)
                .deliveredOrders(deliveredOrders)
                .completedOrders(completedOrders)
                .cancelledOrders(cancelledOrders)
                .allOrders(allOrders)
                .build();
        return orderOverViewVO;
    }

    /**
     * 套餐统计
     * @return
     */
    @Override
    public SetmealOverViewVO getSetmealOverView() {
        Map map = new HashMap();
        map.put("status", 1);
        Integer sold = setmealMapper.countByMap(map);
        map.put("status", 0);
        Integer discontinued = setmealMapper.countByMap(map);
        // 封装VO并返回
        SetmealOverViewVO setmealOverViewVO = SetmealOverViewVO.builder()
                .sold(sold)
                .discontinued(discontinued)
                .build();
        return setmealOverViewVO;
    }

    /**
     * 菜品统计
     * @return
     */
    @Override
    public DishOverViewVO getDishOverView() {
        Map map = new HashMap();
        map.put("status", 1);
        Integer sold = dishMapper.countByMap(map);
        map.put("status", 0);
        Integer discontinued = dishMapper.countByMap(map);
        // 封装VO并返回
        DishOverViewVO dishOverViewVO = DishOverViewVO.builder()
                .sold(sold)
                .discontinued(discontinued)
                .build();
        return dishOverViewVO;
    }

    /**
     * 根据天获取营业数据
     * @return
     */
    @Override
    public BusinessDataVO getBusinessDataForDays(LocalDateTime begin, LocalDateTime end) {
        Map map = new HashMap();
        map.put("status", 5);
        map.put("begin", begin);
        map.put("end", end);
        // 1.获取营业额
        Double turnover = orderMapper.sumByMap(map);
        turnover = turnover == null ? 0.0 : turnover;
        // 2.获取有效订单数
        Integer validOrderCount = orderMapper.countByMap(map);
        validOrderCount = validOrderCount == null ? 0 : validOrderCount;
        // 3.获取订单完成数，订单完成率
        map.put("status", 4);
        Integer completedOrderCount = orderMapper.countByMap(map);
        completedOrderCount = completedOrderCount == null ? 0 : completedOrderCount;
        Double orderCompletionRate = 0.0; // 避免除0
        if (validOrderCount > 0) {
            orderCompletionRate = completedOrderCount.doubleValue() / validOrderCount;
        }
        // 4.获取新增用户数
        Integer newUsers = userMapper.countByCreateTime(begin, end);
        newUsers = newUsers == null ? 0 : newUsers;
        // 5.获取平均客单价
        map.put("status", 5);
        Double unitPrice = 0.0;
        if (completedOrderCount > 0) {
            unitPrice = orderMapper.sumByMap(map) / completedOrderCount;
        }
        unitPrice = unitPrice == null ? 0.0 : unitPrice;
        // 6.封装VO并返回
        BusinessDataVO businessDataVO = BusinessDataVO.builder()
                .turnover(turnover)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .unitPrice(unitPrice)
                .newUsers(newUsers)
                .build();
        return businessDataVO;
    }
}
