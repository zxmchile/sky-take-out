package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.WorkSpaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * projectName: sky-take-out
 *
 * @author: 张轩鸣
 * description: 工作台
 */
@RestController
@RequestMapping("/admin/workspace")
@Slf4j
@Api(tags = "工作台接口")
public class WorkSpaceController {

    @Autowired
    private WorkSpaceService workSpaceService;

    @GetMapping("/businessData")
    @ApiOperation("今日数据")
    public Result<BusinessDataVO> getBusinessData() {
        BusinessDataVO businessDataVO = workSpaceService.getBusinessData();
        return Result.success(businessDataVO);
    }

    @GetMapping("/overviewOrders")
    @ApiOperation("订单统计")
    public Result<OrderOverViewVO> getOrderOverView() {
        OrderOverViewVO orderOverViewVO = workSpaceService.getOrderOverView();
        return Result.success(orderOverViewVO);
    }

    @GetMapping("/overviewSetmeals")
    @ApiOperation("套餐统计")
    public Result<SetmealOverViewVO> getSetmealOverView() {
        SetmealOverViewVO setmealOverViewVO = workSpaceService.getSetmealOverView();
        return Result.success(setmealOverViewVO);
    }

    @GetMapping("/overviewDishes")
    @ApiOperation("菜品统计")
    public Result<DishOverViewVO> getDishOverView() {
        DishOverViewVO dishOverViewVO = workSpaceService.getDishOverView();
        return Result.success(dishOverViewVO);
    }
}
