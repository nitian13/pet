package org.nb.pethome.controller;


import org.nb.pethome.interceptor.TokenInterceptor;
import org.nb.pethome.net.NetCode;
import org.nb.pethome.net.NetResult;
import org.nb.pethome.net.param.LoginParam;
import org.nb.pethome.service.IEmployeeService;
import org.nb.pethome.service.impl.RedisService;
import org.nb.pethome.service.impl.UserService;
import org.nb.pethome.utils.RegexUtil;
import org.nb.pethome.utils.ResultGenerator;
import org.nb.pethome.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;



@RestController
public class LoginController {
    private RedisService redisService;
    private UserService userService;
    private IEmployeeService iEmployeeService;
    private RedisTemplate redisTemplate;
    private Logger logger = LoggerFactory.getLogger(TokenInterceptor.class);

    @Autowired
    public  LoginController(RedisService redisService,UserService userService,IEmployeeService iEmployeeService,RedisTemplate redisTemplate){
        this.redisService = redisService;
        this.userService = userService;
        this.iEmployeeService =iEmployeeService;
        this.redisTemplate = redisTemplate;
    }

    @PostMapping(value = "/login" ,produces = {"application/json", "application/xml"})
    public NetResult login(@RequestBody LoginParam loginParam){

        System.out.println(loginParam);
        try {
            NetResult netResult = userService.adminLogin(loginParam);
            return netResult;
        }catch (Exception e){
            return ResultGenerator.genFailResult("未知异常"+e.getMessage());
        }


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

//
        if(StringUtil.isNullOrNullStr(expiredV)){
            return  ResultGenerator.genFailResult("验证码过期");
        }else {
            if (expiredV.equals(code)){
                return  ResultGenerator.genSuccessResult("验证码正常");
            }else {
                return ResultGenerator.genFailResult("验证码不存在");
            }
        }
    }

    //注册
    public void register(){

    }

}
