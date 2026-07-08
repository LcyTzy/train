package com.zhenyu.train.ticket.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zhenyu.train.common.constant.CommonConstant;
import com.zhenyu.train.common.constant.RedisKeyConstant;
import com.zhenyu.train.ticket.entity.Seat;
import com.zhenyu.train.ticket.mapper.SeatMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 座位服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SeatService {

    private final SeatMapper seatMapper;
    private final RedissonClient redissonClient;

    /**
     * 锁定座位(分布式锁)
     */
    public boolean lockSeat(Long trainId, LocalDate trainDate, Integer carriageNo, String seatNo) {
        String lockKey = RedisKeyConstant.SEAT_LOCK_PREFIX +
                trainId + ":" + trainDate + ":" + carriageNo + ":" + seatNo;

        RLock lock = redissonClient.getLock(lockKey);
        try {
            // 尝试获取锁，等待3秒，锁定10分钟
            boolean acquired = lock.tryLock(3, CommonConstant.SEAT_LOCK_SECONDS, TimeUnit.SECONDS);
            if (!acquired) {
                log.warn("获取座位锁失败: {}", lockKey);
                return false;
            }

            // 更新座位状态为锁定
            Seat seat = seatMapper.selectOne(
                    new LambdaQueryWrapper<Seat>()
                            .eq(Seat::getTrainId, trainId)
                            .eq(Seat::getTrainDate, trainDate)
                            .eq(Seat::getCarriageNo, carriageNo)
                            .eq(Seat::getSeatNo, seatNo));

            if (seat == null || seat.getStatus() != 0) {
                lock.unlock();
                return false;
            }

            seat.setStatus(2); // 锁定
            seatMapper.updateById(seat);

            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * 释放座位锁
     */
    public void unlockSeat(Long trainId, LocalDate trainDate, Integer carriageNo, String seatNo) {
        String lockKey = RedisKeyConstant.SEAT_LOCK_PREFIX +
                trainId + ":" + trainDate + ":" + carriageNo + ":" + seatNo;

        RLock lock = redissonClient.getLock(lockKey);
        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
        }

        // 更新座位状态为可售
        Seat seat = seatMapper.selectOne(
                new LambdaQueryWrapper<Seat>()
                        .eq(Seat::getTrainId, trainId)
                        .eq(Seat::getTrainDate, trainDate)
                        .eq(Seat::getCarriageNo, carriageNo)
                        .eq(Seat::getSeatNo, seatNo));

        if (seat != null && seat.getStatus() == 2) {
            seat.setStatus(0); // 可售
            seatMapper.updateById(seat);
        }
    }

    /**
     * 确认出票(座位状态改为已售)
     */
    public void confirmSeat(Long trainId, LocalDate trainDate, Integer carriageNo, String seatNo) {
        Seat seat = seatMapper.selectOne(
                new LambdaQueryWrapper<Seat>()
                        .eq(Seat::getTrainId, trainId)
                        .eq(Seat::getTrainDate, trainDate)
                        .eq(Seat::getCarriageNo, carriageNo)
                        .eq(Seat::getSeatNo, seatNo));

        if (seat != null) {
            seat.setStatus(1); // 已售
            seatMapper.updateById(seat);
        }

        // 释放锁
        unlockSeat(trainId, trainDate, carriageNo, seatNo);
    }

    /**
     * 获取可用座位
     */
    public List<Seat> getAvailableSeats(Long trainId, LocalDate trainDate,
                                        Integer carriageNo, Integer seatType) {
        return seatMapper.selectList(
                new LambdaQueryWrapper<Seat>()
                        .eq(Seat::getTrainId, trainId)
                        .eq(Seat::getTrainDate, trainDate)
                        .eq(Seat::getCarriageNo, carriageNo)
                        .eq(Seat::getSeatType, seatType)
                        .eq(Seat::getStatus, 0)); // 可售
    }
}
