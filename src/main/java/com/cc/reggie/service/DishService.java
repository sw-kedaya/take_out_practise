package com.cc.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cc.reggie.common.R;
import com.cc.reggie.dto.DishDto;
import com.cc.reggie.entity.Dish;

public interface DishService extends IService<Dish> {
    void saveWithFlavor(DishDto dishDto);

    DishDto getByIdWithFlavor(Long id);

    void updateWithFlavor(DishDto dishDto);

    R<String> removeWithFlavor(Long[] ids);

    void updateStatus(Long[] ids, int status);
}
