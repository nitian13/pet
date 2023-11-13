package org.nb.pethome.controller;




import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.nb.pethome.common.Constants;
import org.nb.pethome.entity.Employee;
import org.nb.pethome.entity.Result;
import org.nb.pethome.entity.Users;
import org.nb.pethome.interceptor.TokenInterceptor;
import org.nb.pethome.net.NetCode;
import org.nb.pethome.net.NetResult;
import org.nb.pethome.net.param.LoginParam;
import org.nb.pethome.net.param.RegisterParam;
import org.nb.pethome.service.IEmployeeService;
import org.nb.pethome.service.impl.RedisService;
import org.nb.pethome.service.impl.UserService;
import org.nb.pethome.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@RestController
public class LoginController {
    private RedisService redisService;
    private UserService userService;
    private IEmployeeService iEmployeeService;
    private RedisTemplate redisTemplate;
    private Logger logger = LoggerFactory.getLogger(TokenInterceptor.class);
    private GetCode getCode;

    @Autowired
    public LoginController(RedisService redisService, UserService userService,RedisTemplate redisTemplate,GetCode getCode) {
        this.redisService = redisService;
        this.userService = userService;
        this.redisTemplate=redisTemplate;
        this.getCode=getCode;

    }

    @PostMapping(value = "/login" ,produces = {"application/json", "application/xml"})
    public NetResult adminLogin(@RequestBody LoginParam loginParam){
        String expiredV = (String) redisTemplate.opsForValue().get(loginParam.getPhone());
        String code = loginParam.getCode();
        if (!code.equals(expiredV)){
            return ResultGenerator.genFailResult("验证码错误/过期");
        }
        if (loginParam.getType() == 0){
            try {
                NetResult netResult = userService.login(loginParam);
                return netResult;
            }catch (Exception e){
                return ResultGenerator.genFailResult("未知异常"+e.getMessage());
            }
        }else {
            try {
                NetResult netResult = userService.adminLogin(loginParam);
                return netResult;
            }catch (Exception e){
                return ResultGenerator.genFailResult("未知异常"+e.getMessage());
            }
        }
    }


    //登录
    @GetMapping("/getverifycode")
    public NetResult sendVerifyCode(String phone) {
        return userService.sendRegisterCode(phone);
    }


    @GetMapping("/sendcode")
    public NetResult SendCode(@RequestParam String phone) throws Exception {

        /**
         * 检查手机号是否空
         */
        if (StringUtil.isEmpty(phone)) {
            return ResultGenerator.genErrorResult(NetCode.PHONE_INVALID,Constants.PHONE_IS_NULL);
        }
        /**
         * 检查手机号格式
         */
        if (!RegexUtil.isPhoneValid(phone)) {
            return ResultGenerator.genErrorResult(NetCode.PHONE_INVALID,"手机号格式不正确");
        }
        String host = "https://dfsns.market.alicloudapi.com";
        String path = "/data/send_sms";
        String method = "GET";
        String appcode = "2dad1dbc5d334d179bae6ad2fb2fb853";
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        //根据API的要求，定义相对应的Content-Type
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        Map<String, String> querys = new HashMap<String, String>();
        Map<String, String> bodys = new HashMap<String, String>();
        String code = getCode.sendCode();
        bodys.put("content", "code:"+code);
        bodys.put("template_id", "CST_ptdie100");
        bodys.put("phone_number", phone);
        //HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
        try {
            HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
            String result = EntityUtils.toString(response.getEntity());
            redisTemplate.opsForValue().set(phone, code, 300, TimeUnit.SECONDS);
            return ResultGenerator.genSuccessResult(result);
        }catch (Exception e){
            e.printStackTrace();
        }


        return ResultGenerator.genFailResult("发送验证码失败");

    }

    //处理流异常的状态
    private String convertStreamToString(InputStream is) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        } catch (IOException e) {
            // 处理异常
            return null;
        }
    }


    //验证
    @GetMapping("/verifycode")
    public NetResult verifyCode(@RequestParam String phone,@RequestParam String code){
        if (StringUtil.isEmpty(phone)){
            return ResultGenerator.genErrorResult(NetCode.PHONE_INVALID, Constants.PHONE_IS_NULL);
        }
        if(!RegexUtil.isPhoneValid(phone)){
            return ResultGenerator.genErrorResult(NetCode.PHONE_INVALID,"手机号不合法");
        }

        String expiredV= (String) redisTemplate.opsForValue().get(phone);
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
    @PostMapping("/register")
    public NetResult register(@RequestBody RegisterParam registerParam){
        System.out.println();
        try {
            NetResult netResult = userService.register(registerParam);
            return netResult;
        }catch (Exception e){
            return ResultGenerator.genFailResult("未知异常"+e.getMessage());
        }
    }

}
