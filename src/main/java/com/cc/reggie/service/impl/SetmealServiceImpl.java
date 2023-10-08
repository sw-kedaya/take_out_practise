package com.cc.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cc.reggie.common.CustomException;
import com.cc.reggie.dto.SetmealDto;
import com.cc.reggie.entity.Setmeal;
import com.cc.reggie.entity.SetmealDish;
import com.cc.reggie.mapper.SetmealMapper;
import com.cc.reggie.service.SetmealDishService;
import com.cc.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 添加套餐并给套餐的菜品表添加关联id
     *
     * @param setmealDto
     */
    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        this.save(setmealDto);

        //设置setmealDto里setmealDishes确实的setmealId
        Long setmealID = setmealDto.getId();
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealID);
            return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 删除套餐的逻辑
     *
     * @param ids
     */
    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {
        //检查是否该套餐正在售卖
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId, ids)
                .eq(Setmeal::getStatus, 1);
        int count = this.count(queryWrapper);
        if (count > 0) throw new CustomException("套餐正在售卖，无法删除！");

        //删除套餐的菜品表
        LambdaQueryWrapper<SetmealDish> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.in(SetmealDish::getSetmealId, ids);
        setmealDishService.remove(queryWrapper1);

        //删除套餐表
        this.removeByIds(ids);
    }

    /**
     * 根据id查询套餐和对应的菜品
     *
     * @param id
     * @return
     */
    @Override
    public SetmealDto getByIdWithDish(Long id) {
        SetmealDto setmealDto = new SetmealDto();
        Setmeal setmeal = this.getById(id);
        BeanUtils.copyProperties(setmeal, setmealDto);

        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getId() != null, SetmealDish::getSetmealId, setmeal.getId());
        setmealDto.setSetmealDishes(setmealDishService.list(queryWrapper));
        return setmealDto;
    }

    /**
     * 修改套餐
     *
     * @param setmealDto
     */
    @Override
    @Transactional
    public void updateWithDish(SetmealDto setmealDto) {
        this.updateById(setmealDto);

        //查出该套餐有哪些菜品
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmealDto.getId() != null, SetmealDish::getSetmealId, setmealDto.getId());
        List<SetmealDish> list = setmealDishService.list(queryWrapper);
        //取出修改后传过来的新菜品
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        boolean flag = false;
        //不管菜品是新增的还是修改的,都给它赋上与setmeal表关联的id值
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmealDto.getId());
        }
        //若list的菜品有与修改后存在的菜品不匹配的id,则判断它被移除而删除掉
        for (SetmealDish setmealDish : list) {
            flag = false;
            for (SetmealDish newSetmealDish : setmealDishes) {
                if (setmealDish.getId().equals(newSetmealDish.getId())) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                setmealDishService.removeById(setmealDish.getId());
            }
        }

        setmealDishService.saveOrUpdateBatch(setmealDishes);
    }

    /**
     * 根据页面传递的id值修改setmeal表的status值
     * @param ids
     * @param status
     */
    @Override
    public void updateStatus(List<Long> ids, int status) {
        Setmeal setmeal = new Setmeal();
        setmeal.setStatus(status);

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ids != null, Setmeal::getId, ids);
        this.update(setmeal,queryWrapper);
    }
}
