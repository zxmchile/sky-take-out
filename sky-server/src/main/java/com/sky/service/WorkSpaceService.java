package com.sky.service;

import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;

import java.time.LocalDateTime;

/**
 * projectName: sky-take-out
 *
 * @author: 张轩鸣
 * description:
 */
public interface WorkSpaceService {

    /**
     * 今日数据
     * @return
     */
    BusinessDataVO getBusinessData();

    /**
     * 订单统计
     * @return
     */
    OrderOverViewVO getOrderOverView();

    /**
     * 套餐统计
     * @return
     */
    SetmealOverViewVO getSetmealOverView();

    /**
     * 菜品统计
     * @return
     */
    DishOverViewVO getDishOverView();

    /**
     * 根据时间段查询营业额
     * @return
     */
    BusinessDataVO getBusinessDataForDays(LocalDateTime beginTime, LocalDateTime endTime);
}
