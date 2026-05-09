package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 统计指定时间区间内的营业额数据
     * @param begin
     * @param end
     * @return
     */
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {

        //当前集合用于存放从begin到end范围内的每天的日期
        List<LocalDate> dateList=new ArrayList<>();

        dateList.add(begin);
        while (!begin.equals(end)){
            //日期计算，计算指定日期的后一天对应的日期
            begin=begin.plusDays(1);
            dateList.add(begin);
        }

        //存放每天的营业额
        List<Double> turnOverList=new ArrayList<>();
        for (LocalDate localDate : dateList) {
            //查询date日期对应的营业额数据，状态为“已完成”的订单金额合计
            LocalDateTime beginTime = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(localDate,LocalTime.MAX);

            Double turnOver=orderMapper.sumByMap(beginTime,endTime,Orders.COMPLETED);
            turnOver = turnOver == null ? 0.0 : turnOver;
            turnOverList.add(turnOver);
        }

        return TurnoverReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .turnoverList(StringUtils.join(turnOverList,","))
                .build();
    }
}
