package com.zhenyu.train.train.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhenyu.train.train.entity.Price;
import org.apache.ibatis.annotations.Mapper;

/**
 * 票价Mapper
 */
@Mapper
public interface PriceMapper extends BaseMapper<Price> {
}
