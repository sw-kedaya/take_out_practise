package com.cc.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cc.reggie.common.R;
import com.cc.reggie.dto.DishDto;
import com.cc.reggie.entity.Category;
import com.cc.reggie.entity.Dish;
import com.cc.reggie.entity.DishFlavor;
import com.cc.reggie.service.CategoryService;
import com.cc.reggie.service.DishFlavorService;
import com.cc.reggie.service.DishService;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增菜品
     *
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        dishService.saveWithFlavor(dishDto);

        return R.success("新增菜品成功");
    }

    /**
     * 菜品分页功能
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page<DishDto>> page(int page, int pageSize, String name) {

        Page<Dish> dishPage = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>(page, pageSize);

        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(name), Dish::getName, name)
                .orderByDesc(Dish::getUpdateTime);

        dishService.page(dishPage, queryWrapper);

        BeanUtils.copyProperties(dishPage, dishDtoPage, "records");

        List<DishDto> list = dishPage.getRecords().stream().map((item) -> {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item, dishDto);

            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                dishDto.setCategoryName(category.getName());
            }

            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }

    /**
     * 点击修改时的回响数据功能
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public R<DishDto> get(@PathVariable Long id) {
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    /**
     * 修改菜品
     *
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        dishService.updateWithFlavor(dishDto);
        return R.success("修改菜品成功");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping()
    public R<String> delete(Long[] ids) {
        return dishService.removeWithFlavor(ids);
    }

    /**
     * 修改菜品停售和启售
     *
     * @param ids
     * @param status
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> updateStatus(Long[] ids, @PathVariable int status) {
        dishService.updateStatus(ids, status);
        String statusStr = status == 0 ? "停售" : "启售";
        return R.success("批量" + statusStr + "成功");
    }

    /**
     * 根据条件查询菜品数据
     *
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish) {
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId())
                .eq(Dish::getStatus, 1) //只查启售的
                .orderByAsc(Dish::getSort)
                .orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(queryWrapper);

        List<DishDto> dishDtoList = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item,dishDto);

            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                dishDto.setCategoryName(category.getName());
            }

            LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
            dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId,item.getId());
            List<DishFlavor> flavorList = dishFlavorService.list(dishFlavorLambdaQueryWrapper);

            dishDto.setFlavors(flavorList);

            return dishDto;
        }).collect(Collectors.toList());


        return R.success(dishDtoList);
    }
}
