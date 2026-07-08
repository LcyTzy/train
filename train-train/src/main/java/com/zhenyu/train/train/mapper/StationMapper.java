package com.zhenyu.train.train.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhenyu.train.train.entity.Station;
import org.apache.ibatis.annotations.Mapper;

/**
 * 车站Mapper
 */
@Mapper
public interface StationMapper extends BaseMapper<Station> {
}
