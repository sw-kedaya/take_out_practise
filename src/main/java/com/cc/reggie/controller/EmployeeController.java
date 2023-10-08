package com.cc.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cc.reggie.common.R;
import com.cc.reggie.entity.Employee;
import com.cc.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录功能
     *
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {

        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        LambdaQueryWrapper<Employee> query = new LambdaQueryWrapper<>();
        query.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(query);

        if (emp == null) {
            return R.error("登陆失败");
        }

        if (!emp.getPassword().equals(password)) {
            return R.error("登陆失败");
        }

        if (emp.getStatus() == 0) {
            return R.error("该账号已被封禁");
        }

        request.getSession().setAttribute("employee", emp.getId());

        return R.success(emp);
    }

    /**
     * 员工退出
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        //清掉存在session的员工id
        request.getSession().removeAttribute("employee");

        return R.success("退出成功");
    }

    /**
     * 添加员工
     *
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee) {

        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//
//        Long id = (Long) request.getSession().getAttribute("employee");
//        employee.setCreateUser(id);
//        employee.setUpdateUser(id);

        boolean save = employeeService.save(employee);
        if (!save) {
            return R.error("添加失败");
        }

        return R.success("添加成功");
    }

    /**
     * 分页查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        log.info("page={},pageSize={},name={} ", page, pageSize, name);

        Page<Employee> pageInfo = new Page<>(page, pageSize);

        LambdaQueryWrapper<Employee> query = new LambdaQueryWrapper<>();
        query.like(StringUtils.isNotEmpty(name), Employee::getName, name);
        query.orderByDesc(Employee::getUpdateTime);

        employeeService.page(pageInfo, query);

        return R.success(pageInfo);
    }

    /**
     * 修改员工
     *
     * @param request
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee) {

//        Long adminId = (Long) request.getSession().getAttribute("employee");
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser(adminId);

        boolean update = employeeService.updateById(employee);

        if (!update) return R.error("修改失败");

        return R.success("修改成功");
    }

    @GetMapping("/{id}")
    public R<Employee> getByID(@PathVariable Long id) {
        Employee emp = employeeService.getById(id);
        if (emp != null) {
            return R.success(emp);
        }
        return R.error("没有查找到数据");
    }
}
