package com.cc.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cc.reggie.common.BaseContext;
import com.cc.reggie.common.R;
import com.cc.reggie.entity.AddressBook;
import com.cc.reggie.entity.User;
import com.cc.reggie.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/addressBook")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    /**
     * 新增地址
     *
     * @param addressBook
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody AddressBook addressBook) {
        //存入id，这样才知道这个地址是谁的
        addressBook.setUserId(BaseContext.getCurrentUserId());

        addressBookService.save(addressBook);

        return R.success("新增地址成功");
    }

    /**
     * 查询该用户的所有地址
     *
     * @return
     */
    @GetMapping("/list")
    public R<List<AddressBook>> list() {
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId, BaseContext.getCurrentUserId())
                .orderByDesc(AddressBook::getUpdateTime);
        List<AddressBook> list = addressBookService.list(queryWrapper);
        return R.success(list);
    }

    /**
     * 回响需修改的地址信息
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<AddressBook> get(@PathVariable Long id) {
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getId, id)
                .eq(AddressBook::getUserId, BaseContext.getCurrentUserId());
        AddressBook addressBook = addressBookService.getOne(queryWrapper);
        return R.success(addressBook);
    }

    /**
     * 修改地址信息
     *
     * @param addressBook
     * @return
     */
    @PutMapping
    public R<String> getById(@RequestBody AddressBook addressBook) {
        addressBookService.updateById(addressBook);
        return R.success("修改地址成功");
    }

    /**
     * 设置默认地址
     *
     * @param addressBook
     * @return
     */
    @PutMapping("/default")
    public R<String> setDefault(@RequestBody AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentUserId());

        //把当前用户的之前设置的默认地址取消(设置成0)
        AddressBook defaultToFalse = new AddressBook();
        defaultToFalse.setUserId(BaseContext.getCurrentUserId());
        defaultToFalse.setIsDefault(0);
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId, addressBook.getUserId())
                .eq(AddressBook::getIsDefault, 1);
        addressBookService.update(defaultToFalse, queryWrapper);

        //把需要设置成默认地址的开启(改成1)
        addressBook.setIsDefault(1);
        addressBookService.updateById(addressBook);

        return R.success("设置默认地址成功");
    }

    //先注释，不知道哪里用到了它
    /**
     * 查询默认地址的信息
     * @return
     */
    @GetMapping("/default")
    public R<AddressBook> getByDefault() {
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId, BaseContext.getCurrentUserId())
                .eq(AddressBook::getIsDefault, 1);
        AddressBook addressBook = addressBookService.getOne(queryWrapper);
        return R.success(addressBook);
    }


}
