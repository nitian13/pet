package org.nb.pethome.controller;


import org.nb.pethome.entity.Employee;
import org.nb.pethome.entity.Users;
import org.nb.pethome.net.NetCode;
import org.nb.pethome.net.NetResult;
import org.nb.pethome.service.IUsersService;
import org.nb.pethome.service.impl.RedisService;
import org.nb.pethome.service.impl.UserService;
import org.nb.pethome.utils.RegexUtil;
import org.nb.pethome.utils.ResultGenerator;
import org.nb.pethome.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@RestController
public class LoginController {
    private RedisService redisService;
    private UserService userService;

    @Autowired
    public LoginController(RedisService redisService, UserService userService) {
        this.redisService = redisService;
        this.userService = userService;

    }

    @PostMapping(value = "/login" ,produces = {"application/json", "application/xml"})
    public NetResult login(@RequestBody Employee employee) {
        try {
            NetResult netResult = userService.adminLogin(employee);
            return netResult;
        } catch (Exception e) {
            return ResultGenerator.genFailResult("未知的异常"+e.getMessage());
        }

    }



    //登录
    @GetMapping("/getverifycode")
    public NetResult sendVerifyCode(String phone) {
        return userService.sendRegisterCode(phone);
    }

    //验证
    @GetMapping("/verifyCode")
    public NetResult verifyCode(String phone, String code) {
        if (StringUtil.isEmpty(phone)) {
            return ResultGenerator.genErrorResult(NetCode.PHONE_INVALID, "手机号不能为空");
        }
        if (!RegexUtil.isPhoneValid(phone)) {
            return ResultGenerator.genErrorResult(NetCode.PHONE_INVALID, "不是合法的手机号");
        }
        Set<String> expiredV = redisService.getSet(phone + phone);
        if(expiredV==null) {
            return ResultGenerator.genFailResult("验证码过期");
        }
        else {
            List<String> expiredList=new ArrayList<String>(expiredV);
            if(expiredList.isEmpty()){
                return ResultGenerator.genFailResult("验证码过期");
            }
            else {
                String expiredValue=expiredList.get(0);
                if (StringUtil.isNullOrNullStr(expiredValue)) {
                    return ResultGenerator.genFailResult("验证码过期");
                } else {
                    if (expiredV.equals(code)) {
                        return ResultGenerator.genSuccessResult("验证码正常");
                    } else {
                        return ResultGenerator.genFailResult("验证码错误");
                    }
                }
            }
        }

    }

    @PostMapping("/userlogin")
    public NetResult userLogin(@RequestBody Employee employee){
        System.out.println(employee);
        try {
            NetResult netResult = userService.login(employee);
            return netResult;
        }catch (Exception e){
            return ResultGenerator.genFailResult("未知异常"+e.getMessage());
        }
    }

    //注册
    @PostMapping("/register")
    public NetResult register(Users users){
        System.out.println();
        try {
            NetResult netResult = userService.register(users);
            return netResult;
        }catch (Exception e){
            return ResultGenerator.genFailResult("未知异常"+e.getMessage());
        }
    }

}
