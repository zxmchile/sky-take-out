package com.sky.controller.admin;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * projectName: sky-take-out
 *
 * @author: 张轩鸣
 * description:
 */
@RestController
@RequestMapping("/admin/order")
@Api("订单管理")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/conditionSearch")
    @ApiOperation("订单条件搜索")
    public Result<PageResult> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        log.info("订单条件搜索");
        PageResult pageResult = orderService.conditionSearch(ordersPageQueryDTO);
        return Result.success(pageResult);
    }

    @GetMapping("/statistics")
    @ApiOperation("统计订单数据")
    public Result<OrderStatisticsVO> statistics() {
        log.info("统计订单数据");
        OrderStatisticsVO orderStatisticsVO = orderService.statistics();
        return Result.success(orderStatisticsVO);
    }

    @GetMapping("/details/{id}")
    @ApiOperation("订单详情")
    public Result<OrderVO> details(@PathVariable Long id) {
        log.info("订单详情");
        OrderVO orderVO = orderService.orderDetails(id);
        return Result.success(orderVO);
    }

    @PutMapping("/confirm")
    @ApiOperation("接单")
    public Result confirm(@RequestBody OrdersConfirmDTO ordersConfirmDTO) {
        log.info("接单");
        orderService.confirm(ordersConfirmDTO);
        return Result.success();
    }

    @PutMapping("/rejection")
    @ApiOperation("取消订单")
    public Result rejection(@RequestBody OrdersRejectionDTO ordersRejectionDTO) throws Exception{
        log.info("取消订单");
        orderService.rejection(ordersRejectionDTO);
        return Result.success();
    }

    @PutMapping("/cancel")
    @ApiOperation("取消订单")
    public Result cancel(@RequestBody OrdersCancelDTO ordersCancelDTO) throws Exception{
        log.info("取消订单");
        orderService.cancelOrder(ordersCancelDTO);
        return Result.success();
    }

    @PutMapping("/delivery/{id}")
    @ApiOperation("派送订单")
    public Result delivery(@PathVariable Long id) throws Exception {
        log.info("派送订单");
        orderService.delivery(id);
        return Result.success();
    }

    @PutMapping("/complete/{id}")
    @ApiOperation("完成订单")
    public Result complete(@PathVariable Long id) throws Exception{
        log.info("完成订单");
        orderService.complete(id);
        return Result.success();
    }
}
