package com.zhenyu.train.ticket.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhenyu.train.ticket.entity.TicketStock;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 余票库存Mapper
 */
@Mapper
public interface TicketStockMapper extends BaseMapper<TicketStock> {

    /**
     * 扣减库存(乐观锁)
     */
    @Update("UPDATE t_ticket_stock SET available_count = available_count - #{count}, " +
            "sold_count = sold_count + #{count}, update_time = NOW() " +
            "WHERE train_id = #{trainId} AND train_date = #{trainDate} " +
            "AND carriage_no = #{carriageNo} AND seat_type = #{seatType} " +
            "AND available_count >= #{count}")
    int deductStock(@Param("trainId") Long trainId,
                    @Param("trainDate") String trainDate,
                    @Param("carriageNo") Integer carriageNo,
                    @Param("seatType") Integer seatType,
                    @Param("count") Integer count);

    /**
     * 恢复库存
     */
    @Update("UPDATE t_ticket_stock SET available_count = available_count + #{count}, " +
            "sold_count = sold_count - #{count}, update_time = NOW() " +
            "WHERE train_id = #{trainId} AND train_date = #{trainDate} " +
            "AND carriage_no = #{carriageNo} AND seat_type = #{seatType}")
    int restoreStock(@Param("trainId") Long trainId,
                      @Param("trainDate") String trainDate,
                      @Param("carriageNo") Integer carriageNo,
                      @Param("seatType") Integer seatType,
                      @Param("count") Integer count);
}
