package com.cc.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cc.reggie.entity.Dish;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
