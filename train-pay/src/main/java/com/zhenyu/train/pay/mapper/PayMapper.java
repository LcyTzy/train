package com.zhenyu.train.pay.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhenyu.train.pay.entity.Pay;
import org.apache.ibatis.annotations.Mapper;

/**
 * 支付Mapper
 */
@Mapper
public interface PayMapper extends BaseMapper<Pay> {
}
