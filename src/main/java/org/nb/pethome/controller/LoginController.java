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

    //登录，用户/管理员
    @PostMapping(value = "/login" ,produces = {"application/json", "application/xml"})
    public NetResult adminLogin(@RequestBody LoginParam loginParam){
        //根据手机号从redis里获取验证码
        String expiredV = (String) redisTemplate.opsForValue().get(loginParam.getPhone());
        //获得输入的验证码
        String code = loginParam.getCode();
        //输入的验证码和从redis获取的验证码不同
        if (!code.equals(expiredV)){
            //验证码错误
            return ResultGenerator.genFailResult("验证码错误/过期");
        }
        //如果type=0则为普通用户，type=1则为管理员
        if (loginParam.getType() == 0){
            try {
                //用户登录
                NetResult netResult = userService.login(loginParam);
                return netResult;
            }catch (Exception e){
                //失败则会报异常
                return ResultGenerator.genFailResult("未知异常"+e.getMessage());
            }
        }else {
            try {
                //管理员登录
                NetResult netResult = userService.adminLogin(loginParam);
                return netResult;
            }catch (Exception e){
                //失败则会报异常
                return ResultGenerator.genFailResult("未知异常"+e.getMessage());
            }
        }
    }


    //登录，验证码发送
    @GetMapping("/getverifycode")
    public NetResult sendVerifyCode(String phone) {
        return userService.sendRegisterCode(phone);
    }


    //第三方应用阿里云，根据手机号发送验证码
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
        /*  host：请求的主机地址
            path：请求的路径
            method：请求的方法（GET/POST等）
            headers：请求头信息
            querys：请求参数
            bodys：请求体信息*/
        //HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
        try {
            //使用HttpUtils.doPost方法发送POST请求
            HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
            //将响应结果转化为字符串类型的数据，使用EntityUtils.toString方法将HttpResponse对象中的Entity转换为字符串类型的数据。
            String result = EntityUtils.toString(response.getEntity());
            //设置验证码过期时间
            redisTemplate.opsForValue().set(phone, code, 300, TimeUnit.SECONDS);
            //返回数据
            return ResultGenerator.genSuccessResult(result);
        }catch (Exception e){
            e.printStackTrace();
        }

        return ResultGenerator.genFailResult("发送验证码失败");

    }

    //处理流异常的状态
    private String convertStreamToString(InputStream is) {
        //高效字符流读取
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            //一行一行的读取
            String line;
            while ((line = reader.readLine()) != null) {
                //进行拼接
                sb.append(line).append("\n");
            }
            //以字符串的方式返回拼接的数据
            return sb.toString();
        } catch (IOException e) {
            // 处理异常
            return null;
        }
    }


    //验证
    @GetMapping("/verifycode")
    public NetResult verifyCode(@RequestParam String phone,@RequestParam String code){
        //手机号不能为空
        if (StringUtil.isEmpty(phone)){
            return ResultGenerator.genErrorResult(NetCode.PHONE_INVALID, Constants.PHONE_IS_NULL);
        }
        //手机号格式要正确
        if(!RegexUtil.isPhoneValid(phone)){
            return ResultGenerator.genErrorResult(NetCode.PHONE_INVALID,"手机号不合法");
        }
        //通过手机号获取验证码
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
            //调用register方法
            NetResult netResult = userService.register(registerParam);
            return netResult;
        }catch (Exception e){
            return ResultGenerator.genFailResult("未知异常"+e.getMessage());
        }
    }

}
