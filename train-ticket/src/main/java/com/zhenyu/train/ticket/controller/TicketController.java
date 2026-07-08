package com.zhenyu.train.ticket.controller;

import com.zhenyu.train.common.result.Result;
import com.zhenyu.train.ticket.dto.TicketStockQueryRequest;
import com.zhenyu.train.ticket.dto.TicketStockResponse;
import com.zhenyu.train.ticket.service.TicketStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 余票控制器
 */
@RestController
@RequestMapping("/ticket")
@RequiredArgsConstructor
public class TicketController {

    private final TicketStockService ticketStockService;

    /**
     * 查询余票
     */
    @GetMapping("/stock")
    public Result<List<TicketStockResponse>> queryStock(
            @RequestParam("trainId") Long trainId,
            @RequestParam("trainDate") LocalDate trainDate,
            @RequestParam("startStationId") Long startStationId,
            @RequestParam("endStationId") Long endStationId) {

        TicketStockQueryRequest request = new TicketStockQueryRequest();
        request.setTrainId(trainId);
        request.setTrainDate(trainDate);
        request.setStartStationId(startStationId);
        request.setEndStationId(endStationId);

        List<TicketStockResponse> list = ticketStockService.queryStock(request);
        return Result.success(list);
    }

    /**
     * 缓存预热
     */
    @PostMapping("/preheat")
    public Result<Void> preheatCache(@RequestParam Long trainId, @RequestParam LocalDate trainDate) {
        ticketStockService.preheatCache(trainId, trainDate);
        return Result.success();
    }

    /**
     * 扣减库存（供Feign调用）
     */
    @PostMapping("/deduct")
    public boolean deductStock(@RequestParam Long trainId,
                               @RequestParam String trainDate,
                               @RequestParam Integer carriageNo,
                               @RequestParam Integer seatType,
                               @RequestParam Integer count) {
        return ticketStockService.deductStock(trainId, LocalDate.parse(trainDate), carriageNo, seatType, count);
    }

    /**
     * 恢复库存（供Feign调用）
     */
    @PostMapping("/restore")
    public Result<Void> restoreStock(@RequestParam Long trainId,
                                     @RequestParam String trainDate,
                                     @RequestParam Integer carriageNo,
                                     @RequestParam Integer seatType,
                                     @RequestParam Integer count) {
        ticketStockService.restoreStock(trainId, LocalDate.parse(trainDate), carriageNo, seatType, count);
        return Result.success();
    }
}
