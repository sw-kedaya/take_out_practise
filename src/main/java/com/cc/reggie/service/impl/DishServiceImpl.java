package com.cc.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cc.reggie.common.R;
import com.cc.reggie.dto.DishDto;
import com.cc.reggie.entity.Dish;
import com.cc.reggie.entity.DishFlavor;
import com.cc.reggie.mapper.DishMapper;
import com.cc.reggie.service.DishFlavorService;
import com.cc.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 新增功能，对flavor的dishId做添加
     *
     * @param dishDto
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        save(dishDto);

        Long dish_id = dishDto.getId();

        List<DishFlavor> flavors = dishDto.getFlavors();
        for (DishFlavor flavor : flavors) {
            flavor.setDishId(dish_id);
        }

        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 修改的操作的数据回响
     *
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        DishDto dishDto = new DishDto();

        Dish dish = getById(id);
        BeanUtils.copyProperties(dish, dishDto);

        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dish.getId());
        dishDto.setFlavors(dishFlavorService.list(queryWrapper));

        return dishDto;
    }

    /**
     * 修改操作
     *
     * @param dishDto
     */
    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        this.updateById(dishDto);

        //先查出来这个菜品有多少种口味，就算是0个也无所谓
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dishDto.getId());
        List<DishFlavor> list = dishFlavorService.list(queryWrapper);
        //再拿出dishId用于添加新标签的使用，把flavors提前拿出来用于修改里面的dishID
        List<DishFlavor> flavors = dishDto.getFlavors();
        Long dish_id = dishDto.getId();
        //不管list是不是为空，都先把dishId的值附上，以防万一
        for (DishFlavor flavor : flavors) {
            flavor.setDishId(dish_id);
        }
        //然后看哪个口味id不在传进来的参数里就删掉哪个
        boolean flag;
        for (DishFlavor dishFlavor : list) { //这里是查询出来已有的口味
            flag = false;
            for (DishFlavor flavor : flavors) { //这里是在页面修改过后的口味
                if (dishFlavor.getId().equals(flavor.getId())) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                dishFlavorService.removeById(dishFlavor);
            }
        }

        dishFlavorService.saveOrUpdateBatch(flavors);

    }

    /**
     * 删除菜品以及其对应的口味数据
     * @param ids
     */
    @Override
    @Transactional
    public R<String> removeWithFlavor(Long[] ids) {
        //只要发现想有启售状态的菜品，就返回不能删除正在售卖的菜品
        List<Dish> dishes = this.listByIds(Arrays.asList(ids));
        for (Dish dish : dishes) {
            if(dish.getStatus()==1){
                return R.error("无法删除正在售卖的菜品");
            }
        }

        //删除对应的菜品的口味
        for (Long id : ids) {
            LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(id != null, DishFlavor::getDishId,id);
            dishFlavorService.remove(queryWrapper);
        }

        //删除菜品
        this.removeByIds(Arrays.asList(ids));

        return R.success("删除成功");
    }

    /**
     * 修改菜品的状态
     * @param ids
     */
    @Override
    public void updateStatus(Long[] ids, int status) {
        Dish dish = new Dish();
        dish.setStatus(status);
//        for (Long id : ids) {
//            LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
//            queryWrapper.eq(id != null, Dish::getId, id);
//            this.update(dish, queryWrapper);
//        }
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ids != null, Dish::getId, ids);
        this.update(dish, queryWrapper);
    }

}
