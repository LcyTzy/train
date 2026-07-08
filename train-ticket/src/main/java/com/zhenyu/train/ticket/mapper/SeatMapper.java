package com.zhenyu.train.ticket.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhenyu.train.ticket.entity.Seat;
import org.apache.ibatis.annotations.Mapper;

/**
 * 座位Mapper
 */
@Mapper
public interface SeatMapper extends BaseMapper<Seat> {
}
