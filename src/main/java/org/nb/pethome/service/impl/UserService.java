package org.nb.pethome.service.impl;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.nb.pethome.common.Constants;
import org.nb.pethome.entity.CodeResBean;
import org.nb.pethome.entity.Employee;
import org.nb.pethome.entity.Users;
import org.nb.pethome.mapper.EmployeeMapper;
import org.nb.pethome.net.NetCode;
import org.nb.pethome.net.NetResult;
import org.nb.pethome.net.param.LoginParam;
import org.nb.pethome.net.param.RegisterParam;
import org.nb.pethome.service.IEmployeeService;
import org.nb.pethome.service.IUserService;
import org.nb.pethome.service.IUsersService;
import org.nb.pethome.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Service
@Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
public class UserService implements IUserService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private IEmployeeService iEmployeeService;

    private RedisService redisService;

    private RedisTemplate redisTemplate;

    private IUsersService iUsersService;

    private GetCode getCode;
    @Autowired
    public UserService(RedisService redisService, IEmployeeService iEmployeeService, RedisTemplate redisTemplate,IUsersService iUsersService,GetCode getCode) {
        this.redisService = redisService;
        this.iEmployeeService = iEmployeeService;
        this.redisTemplate = redisTemplate;
        this.iUsersService = iUsersService;
        this.getCode=getCode;
    }

    @Override
    public NetResult sendRegisterCode(String phone) {
        if (StringUtil.isEmpty(phone)) {
            return ResultGenerator.genErrorResult(NetCode.PHONE_INVALID, Constants.PHONE_IS_NULL);
        }
        if (!RegexUtil.isPhoneValid(phone)) {
            return ResultGenerator.genErrorResult(NetCode.PHONE_INVALID, "手机号不合法");
        }

        //已被注册
        if (iUsersService.selectPhone(phone)!=null) {
            return ResultGenerator.genErrorResult(NetCode.PHONE_INVALID, "手机号已注册");
        }
        Long lastSandTime = 0L;
        try {
            lastSandTime = Long.parseLong(this.redisService.getValue(phone));
        } catch (NumberFormatException e) {
            logger.error(e.getMessage());
            lastSandTime = 0l;
            redisService.cacheValue(phone, System.currentTimeMillis() + "", 60);
        }

        //在不在1分钟内
        if (System.currentTimeMillis() - lastSandTime < 1 * 60 * 1000) {
            return ResultGenerator.genErrorResult(NetCode.PHONE_INVALID, "调用频率过多");
        }

        String expiredV = redisService.getValue(phone + phone);
        if (StringUtil.isNullOrNullStr(expiredV)) {
            String code = "123456" ; //+ System.currentTimeMillis()
            redisService.cacheValue(phone + phone, code, 60);
//            CodeResBeam
            return ResultGenerator.genSuccessResult(code);
        } else {
            return ResultGenerator.genSuccessResult(expiredV);
        }

    }

    @Override
    public NetResult adminLogin(LoginParam loginParam) {
        System.out.println(loginParam);
        //判断账号密码是不是空
        if (StringUtil.isEmpty(loginParam.getPhone())) {
            return ResultGenerator.genErrorResult(NetCode.PHONE_INVALID, "手机号不能为空");
        }
        if (StringUtil.isEmpty(loginParam.getPassword())) {
            return ResultGenerator.genErrorResult(NetCode.USERNAME_INVALID, "密码不能为空");
        }
        //密码md5加密
        loginParam.setPassword(MD5Util.MD5Encode(loginParam.getPassword(), "utf-8"));
        //从数据库找这个人
        Users users = iUsersService.getAdmin(loginParam.getPhone(),loginParam.getPassword());
        if (users != null) {
            //如果有 加个token
            String token = UUID.randomUUID().toString();
            users.setToken(token);
            users.setPassword(null);
            redisTemplate.opsForValue().set(token, users, 30, TimeUnit.MINUTES);
//            logger.info("token__"+token);
            return ResultGenerator.genSuccessResult(users);
        }
        return ResultGenerator.genFailResult("你不是管理员");

    }

    @Override
    public NetResult login(LoginParam loginParam) throws Exception {
        /**
         * 检查手机号是否空
         */
        if (StringUtil.isEmpty(loginParam.getPhone())) {
            return ResultGenerator.genErrorResult(NetCode.PHONE_INVALID,Constants.PHONE_IS_NULL);
        }
        /**
         * 检查手机号格式
         */
        if (!RegexUtil.isPhoneValid(loginParam.getPhone())) {
            return ResultGenerator.genErrorResult(NetCode.PHONE_INVALID,"手机号格式不正确");
        }
        String host = "https://dfsns.market.alicloudapi.com";
        String path = "/data/send_sms";
        String method = "POST";
        String appcode = "2dad1dbc5d334d179bae6ad2fb2fb853";
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        //根据API的要求，定义相对应的Content-Type
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        Map<String, String> querys = new HashMap<String, String>();
        Map<String, String> bodys = new HashMap<String, String>();
        String code = getCode.sendCode();
        bodys.put("content", "code:1234");
        bodys.put("template_id", "CST_ptdie100");
        bodys.put("phone_number", "156*****140");

        try {
            HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
            String result = EntityUtils.toString(response.getEntity());
            return ResultGenerator.genSuccessResult(result);
        }catch (Exception e){
            e.printStackTrace();
        }

        return ResultGenerator.genFailResult("发送验证码失败");
    }


    //注册
    @Override
    public NetResult  register(RegisterParam registerParam) {
        //看看验证码过期没
        String expiredV = redisService.getValue(registerParam.getPhone());
        if (StringUtil.isNullOrNullStr(expiredV)){
            return ResultGenerator.genFailResult("验证码过期");
        }
        //判断账号是不是空
        if (StringUtil.isEmpty(registerParam.getUsername())) {
            return ResultGenerator.genErrorResult(NetCode.PHONE_INVALID, "用户名不能为空");
        }
        //没密码给个123456
        if (StringUtil.isEmpty(registerParam.getPassword())) {
            registerParam.setPassword("123456");
        }
        registerParam.setPassword(MD5Util.MD5Encode(registerParam.getPassword(), "utf-8"));

        //面向成人的产品，不让未成年注册
        if (registerParam.getAge()<18){
            return ResultGenerator.genErrorResult(NetCode.AGE_INVALID, "未成年不能注册");
        }

        //设置当前时间
        registerParam.setRegistertime(System.currentTimeMillis());
        try {
            iUsersService.add(registerParam);
            return ResultGenerator.genSuccessResult("注册成功");
        }catch (Exception e){
            return ResultGenerator.genFailResult("注册失败"+e.getMessage());
        }
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
}
