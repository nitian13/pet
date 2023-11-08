package org.nb.pethome.service.impl;

import org.nb.pethome.common.Constants;
import org.nb.pethome.entity.Employee;
import org.nb.pethome.net.NetCode;
import org.nb.pethome.net.NetResult;
import org.nb.pethome.net.param.LoginParam;
import org.nb.pethome.service.IEmployeeService;
import org.nb.pethome.service.IUserService;
import org.nb.pethome.service.IRedisService;
import org.nb.pethome.utils.MD5Util;
import org.nb.pethome.utils.RegexUtil;
import org.nb.pethome.utils.ResultGenerator;
import org.nb.pethome.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
public class UserService implements IUserService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private IEmployeeService iEmployeeService;

    private IRedisService redisService;
    private RedisTemplate redisTemplate;


    @Autowired
    public UserService(RedisService redisService,IEmployeeService iEmployeeService,RedisTemplate redisTemplate){
        this.redisService = redisService;
        this.iEmployeeService =iEmployeeService;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public NetResult sendRegisterCode(String phone) {
        if (StringUtils.isEmpty(phone)){
            return ResultGenerator.genErrorResult(NetCode.PHONE_INVALID, Constants.PHONE_IS_NULL);
        }
        if(!RegexUtil.isPhoneValid(phone)){
            return ResultGenerator.genErrorResult(NetCode.PHONE_INVALID,"手机号不合法");
        }

        //已被注册
        if(!RegexUtil.isPhoneValid(phone)){
            return ResultGenerator.genErrorResult(NetCode.PHONE_INVALID,"手机号已注册");
        }
        Long lastSandTime = 0L;
        try {
            lastSandTime = Long.parseLong(this.redisService.getValue(phone));
        }catch (NumberFormatException e){
            logger.error(e.getMessage());
            lastSandTime = 0l;
            redisService.cacheValue(phone,System.currentTimeMillis()+"",60);
        }

        //在不在1分钟内
        if(System.currentTimeMillis() - lastSandTime < 1*60*1000){
            return  ResultGenerator.genErrorResult(NetCode.PHONE_INVALID,"调用频率过多");
        }

        String expiredV = redisService.getValue(phone+phone);
        if(StringUtil.isNullOrNullStr(expiredV)){
            String code = "123456"+System.currentTimeMillis();
            redisService.cacheValue(phone+phone,code,60);
//            CodeResBeam
            return  ResultGenerator.genSuccessResult(code);
        }else {
            return  ResultGenerator.genSuccessResult(expiredV);
        }

    }

    @Override
    public NetResult adminLogin(LoginParam loginParam) {
        if (StringUtils.isEmpty(loginParam.getUsername())){
            return ResultGenerator.genErrorResult(NetCode.PHONE_INVALID,"手机号不能为空");
        }
        if (StringUtils.isEmpty(loginParam.getPassword())){
            return ResultGenerator.genErrorResult(NetCode.USERNAME_INVALID,"密码不能为空");
        }
        loginParam.setPassword(MD5Util.MD5Encode(loginParam.getPassword(),"utf-8"));
        Employee employee =  iEmployeeService.login(loginParam);
        if(employee!=null){
            String token = UUID.randomUUID().toString();
            employee.setToken(token);
            employee.setPassword(null);
            redisTemplate.opsForValue().set(token,employee,30, TimeUnit.MINUTES);
//            logger.info("token__"+token);
            return ResultGenerator.genSuccessResult(employee);
        }
        return ResultGenerator.genFailResult("账号或密码错误");

    }
}
