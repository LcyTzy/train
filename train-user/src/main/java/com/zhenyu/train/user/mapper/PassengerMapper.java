package com.zhenyu.train.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhenyu.train.user.entity.Passenger;
import org.apache.ibatis.annotations.Mapper;

/**
 * 乘客Mapper
 */
@Mapper
public interface PassengerMapper extends BaseMapper<Passenger> {
}
