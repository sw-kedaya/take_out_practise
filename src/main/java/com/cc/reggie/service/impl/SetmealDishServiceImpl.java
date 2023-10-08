package com.cc.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cc.reggie.entity.Setmeal;
import com.cc.reggie.entity.SetmealDish;
import com.cc.reggie.mapper.SetmealDishMapper;
import com.cc.reggie.mapper.SetmealMapper;
import com.cc.reggie.service.SetmealDishService;
import com.cc.reggie.service.SetmealService;
import org.springframework.stereotype.Service;

@Service
public class SetmealDishServiceImpl extends ServiceImpl<SetmealDishMapper, SetmealDish> implements SetmealDishService {
}
