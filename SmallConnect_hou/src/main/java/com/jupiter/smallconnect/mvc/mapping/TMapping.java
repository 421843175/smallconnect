package com.jupiter.smallconnect.mvc.mapping;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jupiter.smallconnect.mvc.pojo.TValue;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TMapping extends BaseMapper<TValue> {

}
