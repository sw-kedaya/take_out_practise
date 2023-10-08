package com.cc.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cc.reggie.dto.SetmealDto;
import com.cc.reggie.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {

    void saveWithDish(SetmealDto setmealDto);

    void removeWithDish(List<Long> ids);

    SetmealDto getByIdWithDish(Long id);

    void updateWithDish(SetmealDto setmealDto);

    void updateStatus(List<Long> ids, int status);
}
