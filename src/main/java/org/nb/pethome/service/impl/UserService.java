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

    //管理员登录
    @Override
    public NetResult adminLogin(LoginParam loginParam) {
        System.out.println(loginParam);
        //判断手机号为不为空
        if (StringUtil.isEmpty(loginParam.getPhone())) {
            return ResultGenerator.genErrorResult(NetCode.PHONE_INVALID, "手机号不能为空");
        }
        //判断密码为不为空
        if (StringUtil.isEmpty(loginParam.getPassword())) {
            return ResultGenerator.genErrorResult(NetCode.USERNAME_INVALID, "密码不能为空");
        }
        //密码md5加密
        loginParam.setPassword(MD5Util.MD5Encode(loginParam.getPassword(), "utf-8"));
        //根据手机号和密码从数据库找这个人
        Users users = iUsersService.getAdmin(loginParam.getPhone(),loginParam.getPassword());
        //判断是否有这个人
        if (users != null) {
            //如果有 加个token
            String token = UUID.randomUUID().toString();
            //把token给他
            users.setToken(token);
            //密码设置为空
            users.setPassword(null);
            //设置token过期时间
            redisTemplate.opsForValue().set(token, users, 30, TimeUnit.MINUTES);
//            logger.info("token__"+token);
            return ResultGenerator.genSuccessResult(users);
        }
        //如果没有找到，则没有这个管理员
        return ResultGenerator.genFailResult("你不是管理员");

    }


    //用户登录
    @Override
    public NetResult login(LoginParam loginParam) {
        /**
         * 检查手机号是否为空
         */
        if (StringUtil.isEmpty(loginParam.getPhone())) {
            return ResultGenerator.genErrorResult(NetCode.PHONE_INVALID,Constants.PHONE_IS_NULL);
        }
        /**
         * 检查手机号格式是否正确
         */
        if (!RegexUtil.isPhoneValid(loginParam.getPhone())) {
            return ResultGenerator.genErrorResult(NetCode.PHONE_ERROE,"手机号格式不正确");
        }
        //把用户的密码进行加密
        loginParam.setPassword(MD5Util.MD5Encode(loginParam.getPassword(), "utf-8"));

        //通过用户的手机号和密码去查询用户
        Users users = iUsersService.getUser(loginParam.getPhone(),loginParam.getPassword());

        //如果数据库能够查询到
        if(users != null){
            //随机生成一个token
            String token = UUID.randomUUID().toString();
            //把这个值给用户
            users.setToken(token);
            //把用户的密码设为空
            users.setPassword(null);
            //设置token过期时间
            redisTemplate.opsForValue().set(token, users, 30, TimeUnit.MINUTES);
            return ResultGenerator.genSuccessResult(users);
        }
        //如果没找到，则没有找到这个用户
        return ResultGenerator.genFailResult("账号密码错误");
    }


    //注册
    @Override
    public NetResult  register(RegisterParam registerParam) {
        //Users users = registerParam.getUsers();
        //获取输入的验证码
        String code = registerParam.getCode();
        //看看验证码过期没，通过电话号从redis里获得验证码
        String expiredV = (String) redisTemplate.opsForValue().get(registerParam.getPhone());
//        System.out.println("----");
        System.out.println(expiredV);
        System.out.println(code);
//        System.out.println("----");
        //比对验证码，如果比对不上，验证码错误
        if (!code.equals(expiredV)){
            return ResultGenerator.genFailResult("验证码错误/过期");
        }
        //判断账号是不是空
        if (StringUtil.isEmpty(registerParam.getUsername())) {
            return ResultGenerator.genErrorResult(NetCode.PHONE_INVALID, "用户名不能为空");
        }

        //没密码给个123456
        if (StringUtil.isEmpty(registerParam.getPassword())) {
            registerParam.setPassword("123456");
        }
        //给密码进行加密处理
        registerParam.setPassword(MD5Util.MD5Encode(registerParam.getPassword(), "utf-8"));
        //通过电话号去查询这个人
        Users users1 = iUsersService.selectPhone(registerParam.getPhone());
        //能找到这个人，已经注册了
        if(users1!=null){
            return ResultGenerator.genErrorResult(NetCode.PHONE_INVALID,Constants.PHONE_OCCUPATION);
        }

        //设置当前时间
        registerParam.setRegisterTime(System.currentTimeMillis());
        try {
            //添加这个人
            iUsersService.add(registerParam);
            return ResultGenerator.genSuccessResult("注册成功");
        }catch (Exception e){
            //添加失败
            return ResultGenerator.genFailResult("注册失败"+e.getMessage());
        }
    }
}
