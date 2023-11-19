package org.nb.pethome.service.impl;


import org.nb.pethome.common.Constants;
import org.nb.pethome.entity.Employee;
import org.nb.pethome.entity.Users;
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


    @Autowired
    public UserService(RedisService redisService, IEmployeeService iEmployeeService, RedisTemplate redisTemplate,IUsersService iUsersService) {
        this.redisService = redisService;
        this.iEmployeeService = iEmployeeService;
        this.redisTemplate = redisTemplate;
        this.iUsersService = iUsersService;

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
//            redisTemplate.opsForValue().set(phone, code, 300, TimeUnit.SECONDS);
            redisService.cacheValue(phone + phone, code, 60);
//            CodeResBeam
            return ResultGenerator.genSuccessResult(code);
        } else {
            return ResultGenerator.genSuccessResult(expiredV);
        }

    }

    @Override
    public NetResult login(LoginParam loginParam){
        //如果type=0则为普通用户，type=1则为管理员
        loginParam.setPassword(MD5Util.MD5Encode(loginParam.getPassword(), "utf-8"));

        String token = UUID.randomUUID().toString();
        if (loginParam.getType() == 0){
            Users user = iUsersService.getUser(loginParam.getPhone(),loginParam.getPassword());
            if(user== null){
                return ResultGenerator.genFailResult("用户登录失败");
            }
            user.setPassword(null);
            user.setToken(token);
            redisTemplate.opsForValue().set(token, user, 30, TimeUnit.MINUTES);
            return ResultGenerator.genSuccessResult(user);
        }else if(loginParam.getType()==1){
            Employee employee=iEmployeeService.select(loginParam.getPhone(),loginParam.getPassword());
            if(employee== null){
                return ResultGenerator.genFailResult("管理员登录失败");
            }
            employee.setPassword(null);
            employee.setToken(token);
            redisTemplate.opsForValue().set(token, employee, 30, TimeUnit.MINUTES);
            return ResultGenerator.genSuccessResult(employee);
        }else{
            return ResultGenerator.genErrorResult(NetCode.TYPE_INVALID, "用户类型错误");
        }

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
