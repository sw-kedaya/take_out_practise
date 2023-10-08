package com.cc.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cc.reggie.common.CustomException;
import com.cc.reggie.entity.Category;
import com.cc.reggie.entity.Dish;
import com.cc.reggie.entity.Setmeal;
import com.cc.reggie.mapper.CategoryMapper;
import com.cc.reggie.service.CategoryService;
import com.cc.reggie.service.DishService;
import com.cc.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    /**
     * 删除业务逻辑
     * @param id
     * @return
     */
    @Override
    public boolean remove(Long id) {

        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, id);
        int dishCount = dishService.count(dishLambdaQueryWrapper);
        if (dishCount > 0){
            throw new CustomException("该菜品里存在菜谱，无法删除");
        }

        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, id);
        int setmealCount = setmealService.count(setmealLambdaQueryWrapper);
        if (setmealCount > 0){
            throw new CustomException("该菜品里存在套餐，无法删除");
        }

        return super.removeById(id);
    }
}
