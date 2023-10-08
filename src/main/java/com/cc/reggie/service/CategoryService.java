package com.cc.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cc.reggie.entity.Category;

public interface CategoryService extends IService<Category> {
    boolean remove(Long id);
}
