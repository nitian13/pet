package org.nb.pethome.service.impl;

import org.nb.pethome.common.Constants;
import org.nb.pethome.entity.CodeResBean;
import org.nb.pethome.entity.Employee;
import org.nb.pethome.entity.Users;
import org.nb.pethome.mapper.EmployeeMapper;
import org.nb.pethome.net.NetCode;
import org.nb.pethome.net.NetResult;
import org.nb.pethome.service.IUserService;
import org.nb.pethome.service.IUsersService;
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

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Service
@Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
public class UserService implements IUserService {

    private Logger logger= (Logger) LoggerFactory.getLogger(this.getClass());

    private RedisService redisService;
    private EmployeeMapper employeeMapper;
    private RedisTemplate redisTemplate;
    private IUsersService iUsersService;
    @Autowired
    public UserService(RedisService redisService,EmployeeMapper employeeMapper,RedisTemplate redisTemplate,IUsersService iUsersService) {
        this.redisService = redisService;
        this.employeeMapper=employeeMapper;
        this.redisTemplate=redisTemplate;
        this.iUsersService=iUsersService;
    }

    @Override
    public NetResult sendRegisterCode(String phone) {
        if (StringUtil.isEmpty(phone)) {
            return ResultGenerator.genErrorResult(NetCode.PHONE_INVALID, "手机号不能为空");
        }
        if (!RegexUtil.isPhoneValid(phone)) {
            return ResultGenerator.genErrorResult(NetCode.PHONE_INVALID, "不是合法的手机号");
        }
       /* User u =userMapper.findByPhone(phone);
        if (u!=null) {
            return ResultGenerator.genErrorResult(NetCode.PHONE_INVALID, "用户已经被注册");
        }*/
        Long lastSendTime = 0L;
        try {
            lastSendTime = Long.parseLong(redisService.getValue(phone));
        } catch (NumberFormatException e) {
            logger.error(e.getMessage());
            lastSendTime = 0l;
            redisService.cacheValue(phone, System.currentTimeMillis() + "", 60);
        }

        /**
         * 在一分钟之内
         */
        if (System.currentTimeMillis() - lastSendTime < 1 * 60 * 1000) {
            return ResultGenerator.genErrorResult(NetCode.PHONE_INVALID, "调用频率过多");
        }
        //过来一分钟，发验证码
        String expiredV = redisService.getValue(phone + phone);
        if (StringUtil.isNullOrNullStr(expiredV)) {
            Random random=new Random();
            int number=random.nextInt(1000000);
            String code =String.format("%06d",number);
            redisService.cacheSet(phone + phone, code, 60);
            CodeResBean<String> resBean = new CodeResBean<>();
            resBean.code = code;
            return ResultGenerator.genSuccessResult(resBean);
        } else {
            return ResultGenerator.genSuccessResult(expiredV);
        }


    }

    @Override
    public NetResult adminLogin(Employee employee) {
        if (StringUtil.isEmpty(employee.getUsername())) {
            return ResultGenerator.genErrorResult(NetCode.USERNAME_INVALID, "用户名不能为空");
        }
        if (StringUtil.isEmpty(employee.getPassword())) {
            return ResultGenerator.genErrorResult(NetCode.PASSWORD_INVALID, "密码不能为空");
        }
        employee.setPassword(MD5Util.MD5Encode(employee.getPassword(), "utf-8"));
        Employee e=employeeMapper.login(employee);
        if(e==null){
            return ResultGenerator.genFailResult("账号或密码错误");
        }
        else {
            //生成一个token
            String token= UUID.randomUUID().toString();
            logger.info("token__"+token);
            e.setToken(token);
            e.setPassword(null);
            //30分钟token过期
            redisTemplate.opsForValue().set(token,e,30, TimeUnit.MINUTES);
            return ResultGenerator.genSuccessResult(e);
        }
    }

    @Override
    public NetResult login(Employee employee) {
        //判断账号密码是不是空
        if (StringUtils.isEmpty(employee.getPhone())) {
            return ResultGenerator.genErrorResult(NetCode.PHONE_INVALID, "手机号不能为空");
        }
        if (StringUtils.isEmpty(employee.getPassword())) {
            return ResultGenerator.genErrorResult(NetCode.USERNAME_INVALID, "密码不能为空");
        }
        //密码md5加密
        employee.setPassword(MD5Util.MD5Encode(employee.getPassword(), "utf-8"));
        Users users = iUsersService.getUser(employee.getPhone(),employee.getPassword());
        if (users != null) {
            //如果有 加个token
            String token = UUID.randomUUID().toString();
            users.setToken(token);
            users.setPassword(null);
            redisTemplate.opsForValue().set(token, users, 30, TimeUnit.MINUTES);
//            logger.info("token__"+token);
            return ResultGenerator.genSuccessResult(users);
        }
        return ResultGenerator.genFailResult("密码错误");
    }

    @Override
    public NetResult register(Users users) {
        //看看验证码过期没
        String expiredV = redisService.getValue(users.getPhone()+users.getPhone());
        if (StringUtil.isNullOrNullStr(expiredV)){
            return ResultGenerator.genFailResult("验证码过期");
        }
        //判断账号是不是空
        if (StringUtils.isEmpty(users.getUsername())) {
            return ResultGenerator.genErrorResult(NetCode.PHONE_INVALID, "用户名不能为空");
        }
        //没密码给个123456
        if (StringUtils.isEmpty(users.getPassword())) {
            users.setPassword("123456");
        }
        users.setPassword(MD5Util.MD5Encode(users.getPassword(), "utf-8"));

        //面向成人的产品，不让未成年注册
        if (users.getAge()<18){
            return ResultGenerator.genErrorResult(NetCode.AGE_INVALID, "未成年不能注册");
        }

        //设置当前时间
        users.setRegisterTime(System.currentTimeMillis());
        try {
            iUsersService.add(users);
            return ResultGenerator.genSuccessResult("注册成功");
        }catch (Exception e){
            return ResultGenerator.genFailResult("注册失败"+e.getMessage());
        }
    }
}
