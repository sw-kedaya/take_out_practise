package com.cc.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cc.reggie.common.R;
import com.cc.reggie.entity.User;
import com.cc.reggie.service.UserService;
import com.cc.reggie.utils.SMSUtils;
import com.cc.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 发送验证码
     *
     * @param user
     * @param session
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMessage(@RequestBody User user, HttpSession session) {
        String phone = user.getPhone();

        if (StringUtils.isNotEmpty(phone)) {
            //生成四位的验证码(这是自定义的工具类)
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("验证码为：{}", code);

            //发送短信的功能其中参数是：签名名称，模板code，接收的手机号，验证码
            //查看response.getMessage()发现提示：测试专用签名和模板必须结合使用
            //根据这个返回值可知还是能发送的，只是签名和模板不匹配，不想花钱，这样注释了就行了
//            SMSUtils.sendMessage("瑞吉外卖", "SMS_269520668", phone, code);

            //存储验证码用于与用户输入的验证码进行判断验证
            session.setAttribute(phone, code);

            return R.success("短信发送成功");
        }

        return R.error("短信发送失败");
    }

    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session) {
        String phone = map.get("phone").toString();
        String code = map.get("code").toString();

        Object coedInSession = session.getAttribute(phone);

        if (coedInSession != null && coedInSession.equals(code)) {
            //判断该号码是否在数据库，没有则添加
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone, phone);
            User user = userService.getOne(queryWrapper);
            if (user == null){
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            //登录成功且数据库有数据则保存到session用于过滤器放行页面
            session.setAttribute("user", user.getId());

            //返回用户信息用于登录后的回响
            return R.success(user);
        }
        return R.error("登陆失败");
    }

    /**
     * 退出登录
     * @param session
     * @return
     */
    @PostMapping("/loginout")
    public R<String> loginout(HttpSession session){
        session.removeAttribute("user");
        return R.success("退出登录成功");
    }
}
