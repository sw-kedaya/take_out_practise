package com.cc.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cc.reggie.common.BaseContext;
import com.cc.reggie.common.R;
import com.cc.reggie.entity.Orders;
import com.cc.reggie.entity.ShoppingCart;
import com.cc.reggie.service.OrdersService;
import com.cc.reggie.service.ShoppingCartService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/order")
public class OrdersController {

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders, HttpSession session) {
        ordersService.submit(orders, session);
        return R.success("支付成功");
    }

    /**
     * 手机端订单查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/userPage")
    public R<Page<Orders>> userPage(int page, int pageSize, String name) {
        Page<Orders> pageInfo = new Page<>(page, pageSize);
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId, BaseContext.getCurrentUserId())
                .orderByDesc(Orders::getOrderTime);
        ordersService.page(pageInfo, queryWrapper);

        return R.success(pageInfo);
    }

    /**
     * 电脑端订单查询
     *
     * @param page
     * @param pageSize
     * @param number
     * @param beginTime
     * @param endTime
     * @return
     */
    @GetMapping("/page")
    public R<Page<Orders>> page(int page, int pageSize, String number,
                                String beginTime, String endTime) {
        Page<Orders> pageInfo = new Page<>(page, pageSize);
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotEmpty(number), Orders::getNumber, number)
                .ge(beginTime != null, Orders::getOrderTime, beginTime) //大于等于开始时间
                .le(endTime != null, Orders::getOrderTime, endTime); //小于等于结束时间
        ordersService.page(pageInfo, queryWrapper);

        return R.success(pageInfo);
    }

    /**
     * 修改订单状态(派送，完成)
     *
     * @param orders
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Orders orders) {
        ordersService.updateById(orders);
        return R.success("修改成功");
    }

    @PostMapping("/again")
    public R<String> again(@RequestBody Orders orders, HttpSession session) {
        List<ShoppingCart> list = (List<ShoppingCart>) session.getAttribute(orders.getId().toString());
        shoppingCartService.saveBatch(list);
        return R.success("再来一单！");
    }
}
