package com.zhenyu.train.ticket.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zhenyu.train.common.constant.RedisKeyConstant;
import com.zhenyu.train.common.exception.BusinessException;
import com.zhenyu.train.common.result.ResultCode;
import com.zhenyu.train.ticket.dto.TicketStockQueryRequest;
import com.zhenyu.train.ticket.dto.TicketStockResponse;
import com.zhenyu.train.ticket.entity.TicketStock;
import com.zhenyu.train.ticket.mapper.TicketStockMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 余票服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TicketStockService {

    private final TicketStockMapper ticketStockMapper;
    private final StringRedisTemplate redisTemplate;

    /**
     * Lua脚本: 原子性扣减库存
     */
    private static final String DEDUCT_STOCK_LUA =
            "local stock = tonumber(redis.call('get', KEYS[1])) " +
            "if stock == nil then return -1 end " +
            "local count = tonumber(ARGV[1]) " +
            "if stock >= count then " +
            "    redis.call('decrby', KEYS[1], count) " +
            "    return 1 " +
            "else " +
            "    return 0 " +
            "end";

    /**
     * 查询余票
     */
    public List<TicketStockResponse> queryStock(TicketStockQueryRequest request) {
        // 先查Redis缓存
        String cacheKey = buildCacheKey(request.getTrainId(), request.getTrainDate());
        List<Object> cached = redisTemplate.opsForHash().values(cacheKey).stream()
                .collect(Collectors.toList());

        if (!cached.isEmpty()) {
            // 从缓存返回
            return buildResponseFromCache(cached);
        }

        // 缓存未命中，查数据库
        List<TicketStock> stocks = ticketStockMapper.selectList(
                new LambdaQueryWrapper<TicketStock>()
                        .eq(TicketStock::getTrainId, request.getTrainId())
                        .eq(TicketStock::getTrainDate, request.getTrainDate()));

        return stocks.stream()
                .map(this::buildResponse)
                .collect(Collectors.toList());
    }

    /**
     * 扣减库存(Redis Lua原子操作)
     */
    public boolean deductStock(Long trainId, LocalDate trainDate, Integer carriageNo,
                               Integer seatType, Integer count) {
        String key = RedisKeyConstant.TICKET_STOCK_PREFIX +
                trainId + ":" + trainDate + ":" + carriageNo + ":" + seatType;

        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptText(DEDUCT_STOCK_LUA);
        script.setResultType(Long.class);

        Long result = redisTemplate.execute(script,
                Collections.singletonList(key),
                count.toString());

        if (result == null || result == -1) {
            log.warn("库存不存在: {}", key);
            return false;
        }

        return result == 1;
    }

    /**
     * 恢复库存
     */
    public void restoreStock(Long trainId, LocalDate trainDate, Integer carriageNo,
                              Integer seatType, Integer count) {
        String key = RedisKeyConstant.TICKET_STOCK_PREFIX +
                trainId + ":" + trainDate + ":" + carriageNo + ":" + seatType;
        redisTemplate.opsForValue().increment(key, count);

        // 同步到数据库
        ticketStockMapper.restoreStock(trainId, trainDate.toString(), carriageNo, seatType, count);
    }

    /**
     * 缓存预热
     */
    public void preheatCache(Long trainId, LocalDate trainDate) {
        List<TicketStock> stocks = ticketStockMapper.selectList(
                new LambdaQueryWrapper<TicketStock>()
                        .eq(TicketStock::getTrainId, trainId)
                        .eq(TicketStock::getTrainDate, trainDate));

        String cacheKey = buildCacheKey(trainId, trainDate);
        for (TicketStock stock : stocks) {
            String fieldKey = stock.getCarriageNo() + ":" + stock.getSeatType();
            redisTemplate.opsForHash().put(cacheKey, fieldKey, stock.getAvailableCount().toString());
        }

        log.info("缓存预热完成: trainId={}, date={}", trainId, trainDate);
    }

    private String buildCacheKey(Long trainId, LocalDate trainDate) {
        return RedisKeyConstant.TICKET_STOCK_PREFIX + trainId + ":" + trainDate;
    }

    private TicketStockResponse buildResponse(TicketStock stock) {
        TicketStockResponse response = new TicketStockResponse();
        response.setSeatType(stock.getSeatType());
        response.setSeatName(com.zhenyu.train.common.enums.SeatTypeEnum.getDescByCode(stock.getSeatType()));
        response.setAvailableCount(stock.getAvailableCount());
        return response;
    }

    private List<TicketStockResponse> buildResponseFromCache(List<Object> cached) {
        List<TicketStockResponse> responses = new ArrayList<>();
        for (Object obj : cached) {
            if (obj instanceof String) {
                String[] parts = ((String) obj).split(":");
                if (parts.length >= 2) {
                    TicketStockResponse response = new TicketStockResponse();
                    response.setSeatType(Integer.parseInt(parts[0]));
                    response.setSeatName(com.zhenyu.train.common.enums.SeatTypeEnum.getDescByCode(Integer.parseInt(parts[0])));
                    response.setAvailableCount(Integer.parseInt(parts[1]));
                    responses.add(response);
                }
            }
        }
        return responses;
    }
}
