package org.nb.pethome.controller;


import org.nb.pethome.net.NetCode;
import org.nb.pethome.net.NetResult;
import org.nb.pethome.service.impl.RedisService;
import org.nb.pethome.service.impl.UserService;
import org.nb.pethome.utils.RegexUtil;
import org.nb.pethome.utils.ResultGenerator;
import org.nb.pethome.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {
    private RedisService redisService;
    private UserService userService;

    @Autowired
    public  LoginController(RedisService redisService,UserService userService){
        this.redisService = redisService;
        this.userService = userService;
    }

    //登录
    @GetMapping("/getverifycode")
    public NetResult sendVerifyCode(@RequestParam String phone){
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

        String expiredV = redisService.getValue(phone+phone);
        System.out.println(expiredV);
        if (StringUtil.isNullOrNullStr(expiredV)) {
            return ResultGenerator.genFailResult("验证码过期");
        } else {
            if (expiredV.equals(code)) {
                return ResultGenerator.genSuccessResult("验证码正常");
            } else {
                return ResultGenerator.genFailResult("验证码错误");
            }
        }
    }

    //注册
    public void register(){

    }

}
