package com.cc.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cc.reggie.common.R;
import com.cc.reggie.entity.Category;
import com.cc.reggie.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增菜品分类
     *
     * @param category
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category) {
        boolean save = categoryService.save(category);
        return save ? R.success("新建菜品分类成功") : R.error("新建菜品分类失败");
    }

    /**
     * 分页查询
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize) {

        Page<Category> categoryPage = new Page<>(page, pageSize);

        LambdaQueryWrapper<Category> query = new LambdaQueryWrapper<>();

        query.orderByAsc(Category::getSort);

        categoryService.page(categoryPage, query);

        return R.success(categoryPage);
    }

    /**
     * 删除菜品
     *
     * @param id
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long id) {

        categoryService.remove(id);

        return R.success("删除成功");
    }

    /**
     * 修改菜品
     *
     * @param category
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Category category) {
        boolean b = categoryService.updateById(category);
        if (!b) return R.error("修改失败");
        return R.success("修改成功");
    }

    @GetMapping("/list")
    public R<List<Category>> list(Category category) {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(category.getType() != null, Category::getType, category.getType());

        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

        List<Category> list = categoryService.list(queryWrapper);

        return R.success(list);
    }

}
