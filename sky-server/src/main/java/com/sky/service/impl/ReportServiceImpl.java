package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkSpaceService;
import com.sky.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 * projectName: sky-take-out
 *
 * @author: 张轩鸣
 * description:
 */
@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WorkSpaceService workSpaceService;

    /**
     * 营业额统计——统计某个时间区间内的营业额数据
     * @param begin
     * @param end
     * @return
     */
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateTimeList = new ArrayList<>();
        dateTimeList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateTimeList.add(begin);
        }
        List<Double> turnoverList = new ArrayList<>();
        for (LocalDate date : dateTimeList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN); // 00:00:00
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX); // 23:59:59
            Map map = new HashMap();
            map.put("begin", beginTime);
            map.put("end", endTime);
            map.put("status", Orders.COMPLETED);
            Double turnover = orderMapper.sumByStatusAndOrderTime(map);
            if (turnover == null) {
                turnover = 0.0;
            }
            turnoverList.add( turnover);
        }
        String stringDateTimeList = StringUtils.join(dateTimeList, ",");
        String stringTurnoverList = StringUtils.join(turnoverList, ",");

        TurnoverReportVO turnoverReportVO = TurnoverReportVO.builder()
                .dateList(stringDateTimeList)
                .turnoverList(stringTurnoverList)
                .build();
        return turnoverReportVO;
    }

    /**
     * 用户统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateTimeList = new ArrayList<>(); // 日期集合
        List<Integer> newUserList = new ArrayList<>(); // 新增用户数
        List<Integer> totalUserList = new ArrayList<>(); // 总用户数
        // 存放每天日期
        dateTimeList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateTimeList.add(begin);
        }
//        // 存放每天的新增用户数：SELECT COUNT(id) FROM user WHERE create_time >= ? AND create_time < ?
//        for (LocalDate date : dateTimeList) {
//            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
//            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
//            Integer newUser = userMapper.countByCreateTime(beginTime, endTime);
//            newUserList.add(newUser);
//        }
//        // 存放每天总用户数：SELECT COUNT(id) FROM user
//        for (LocalDate date : dateTimeList) {
//            Integer totalUser = userMapper.count();
//            totalUserList.add(totalUser);
//        }
        for (LocalDate date : dateTimeList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map map = new HashMap();

            map.put("begin", beginTime);
            Integer newUser = userMapper.countByMap(map);

            map.put("end", endTime);
            Integer totalUser = userMapper.countByMap(map);

            newUserList.add(newUser);
            totalUserList.add(totalUser);
        }
        // 将日期、新增用户数、总用户数拼接成字符串
        String stringDateTimeList = StringUtils.join(dateTimeList, ",");
        String stringNewUserList = StringUtils.join(newUserList, ",");
        String stringTotalUserList = StringUtils.join(totalUserList, ",");
        // 封装VO
        UserReportVO userReportVO = UserReportVO.builder()
                .dateList(stringDateTimeList)
                .newUserList(stringNewUserList)
                .totalUserList(stringTotalUserList)
                .build();
        return userReportVO;
    }

    /**
     * 订单统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        // 存放日期
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        // 存放订单总数与有效订单完成数
        List<Integer> orderCountList = new ArrayList<>();
        List<Integer> validOrderCountList = new ArrayList<>();
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map map = new HashMap();
            map.put("begin", beginTime);
            map.put("end", endTime);
            // 订单总数 SELECT COUNT(id) FROM orders WHERE order_time >= ? AND order_time < ?
            Integer totalOrderCount = orderMapper.countByMap(map);
            orderCountList.add(totalOrderCount);
            // 有效订单完成数：SELECT COUNT(id) FROM orders WHERE order_time >= ? AND order_time < ? AND status = 5
            map.put("status", Orders.COMPLETED);
            Integer validOrderCount = orderMapper.countByMap(map);
            validOrderCountList.add(validOrderCount);
        }
        // 遍历订单总数列表+订单完成数列表，计算订单完成率
        Integer totalOrderCount = orderCountList.stream().reduce(Integer::sum).get();
        Integer validOrderCount = validOrderCountList.stream().reduce(Integer::sum).get();
        Double orderCompletionRate = 0.0;
        if (totalOrderCount != 0) {
            orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount;
        }
        OrderReportVO orderReportVO = OrderReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .orderCountList(StringUtils.join(orderCountList, ","))
                .validOrderCountList(StringUtils.join(validOrderCountList, ","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
        return orderReportVO;
    }

    /**
     * 统计指定时间区间内，销售量top10
     * @param begin
     * @param end
     * @return
     */
    @Override
    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        List<GoodsSalesDTO> goodsSalesDTOList = orderMapper.getSalesTop10(beginTime, endTime);
        String nameList = StringUtils.join(goodsSalesDTOList.stream().map(GoodsSalesDTO::getName).toArray(), ",");
        String numberList = StringUtils.join(goodsSalesDTOList.stream().map(GoodsSalesDTO::getNumber).toArray(), ",");
        SalesTop10ReportVO salesTop10ReportVO = SalesTop10ReportVO.builder()
                .nameList(nameList)
                .numberList(numberList)
                .build();
        return salesTop10ReportVO;
    }

    /**
     * 导出营业数据
     * @param response
     */
    @Override
    public void exportBusinessData(HttpServletResponse response) {
        LocalDate begin = LocalDate.now().minusDays(30); // 开始时间
        LocalDate end = LocalDate.now().minusDays(1); // 结束时间
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN); // 开始时间转为LocalDateTime
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX); // 结束时间转为LocalDateTime
        // 1.查询数据
        BusinessDataVO businessDataForDays = workSpaceService.getBusinessDataForDays(beginTime, endTime);
        // 2.通过Apache POI将数据写到Excel中
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("templates/Template.xlsx"); // 获取模板输入流
        try {
            // 基于模板创建Excel表格对象
            XSSFWorkbook sheets = new XSSFWorkbook(inputStream);
            // 获取表格对象
            XSSFSheet sheet_1 = sheets.getSheet("Sheet1");
            // 设置第二行数据：写入时间段
            sheet_1.getRow(1).getCell(1).setCellValue(begin + "至" + end + "营业数据");
            // 获取第四行
            XSSFRow row_4 = sheet_1.getRow(3);
            row_4.getCell(2).setCellValue(businessDataForDays.getTurnover());
            row_4.getCell(4).setCellValue(businessDataForDays.getOrderCompletionRate());
            row_4.getCell(6).setCellValue(businessDataForDays.getNewUsers());
            // 获取第五行
            XSSFRow row_5 = sheet_1.getRow(4);
            row_5.getCell(2).setCellValue(businessDataForDays.getValidOrderCount());
            row_5.getCell(4).setCellValue(businessDataForDays.getUnitPrice());

            LocalDate date = null;
            for (int i = 0; i < 30; i++) {
                date = begin.plusDays(i);
                LocalDateTime beginDateTime = LocalDateTime.of(date, LocalTime.MIN);
                LocalDateTime endDateTime = LocalDateTime.of(date, LocalTime.MAX);
                BusinessDataVO data = workSpaceService.getBusinessDataForDays(beginDateTime, endDateTime);
                // 获取第8行
                XSSFRow row_8 = sheet_1.getRow(7 + i);
                row_8.getCell(1).setCellValue(date.toString());
                row_8.getCell(2).setCellValue(data.getTurnover());
                row_8.getCell(3).setCellValue(data.getValidOrderCount());
                row_8.getCell(4).setCellValue(data.getOrderCompletionRate());
                row_8.getCell(5).setCellValue(data.getUnitPrice());
                row_8.getCell(6).setCellValue(data.getNewUsers());
            }
            // 3.通过输出流将Excel下载到客户端/浏览器
            ServletOutputStream outputStream = response.getOutputStream(); // 获取输出流
            sheets.write(outputStream); // 将创建好的Excel文件写入到输出流中

            // 4.关闭流
            outputStream.close();
            sheets.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
