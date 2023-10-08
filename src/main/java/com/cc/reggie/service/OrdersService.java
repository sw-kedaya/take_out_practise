package com.cc.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cc.reggie.entity.Orders;

import javax.servlet.http.HttpSession;

public interface OrdersService extends IService<Orders> {
    void submit(Orders orders, HttpSession session);
}
