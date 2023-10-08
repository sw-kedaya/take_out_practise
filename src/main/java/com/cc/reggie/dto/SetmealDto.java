package com.cc.reggie.dto;

import com.cc.reggie.entity.Setmeal;
import com.cc.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
