package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * projectName: sky-take-out
 *
 * @author: 张轩鸣
 * description:
 */
@Component
@Slf4j
public class OrderTask {

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 处理超时订单
     */
    @Scheduled(cron = "0 * * * * ? ") // 每分钟执行一次
    public void processTimeoutOrder(){
        log.info("开始处理订单：{}", LocalDateTime.now());
        // SELECT * FROM sky_take_out.orders WHERE status = 1 AND order_time < ?(15min)
        LocalDateTime orderTime = LocalDateTime.now().minusMinutes(15); // 当前时间减去15分钟，最终比较哪些订单小于当前时间的15分钟之前
        List<Orders> list = orderMapper.getByStatusAndOrderTimeLT(Orders.PENDING_PAYMENT,orderTime);
        if (list!=null && list.size()>0) {
            for (Orders orders : list) {
//                log.info("处理订单：{}", orders);
//                orderMapper.updateStatus(Orders.CANCELLED,Orders.UN_PAID,LocalDateTime.now(),orders.getNumber());
//                log.info("取消订单：{}", orders);
                orders.setStatus(Orders.CANCELLED);
                orders.setCancelReason("支付超时");
                orders.setCancelTime(LocalDateTime.now());
                orderMapper.update(orders);
            }
        }
    }

    /**
     * 处理一直派送订单
     */
    @Scheduled(cron = "0 0 1 * * ? ") // 每天凌晨1点执行一次
    public void processDeliveryOrder(){
        log.info("处理一直派送订单：{}", LocalDateTime.now());
        LocalDateTime orderTime = LocalDateTime.now().minusHours(1); // 当前时间减去1小时，最终比较哪些订单小于当前时间的1小时之前
        List<Orders> list = orderMapper.getByStatusAndOrderTimeLT(Orders.DELIVERY_IN_PROGRESS,orderTime);
        if (list!=null && list.size()>0) {
            for (Orders orders : list) {
                log.info("处理订单：{}", orders);
                orders.setStatus(Orders.COMPLETED);
                orders.setCancelReason("订单自动完成");
                orders.setCancelTime(LocalDateTime.now());
                orderMapper.update(orders);
            }
        }
    }
}
