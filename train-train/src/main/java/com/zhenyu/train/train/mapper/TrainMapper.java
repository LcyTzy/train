package com.zhenyu.train.train.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhenyu.train.train.entity.Train;
import org.apache.ibatis.annotations.Mapper;

/**
 * 车次Mapper
 */
@Mapper
public interface TrainMapper extends BaseMapper<Train> {
}
