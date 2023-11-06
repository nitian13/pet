package org.nb.pethome.service.impl;

import org.nb.pethome.common.Constants;
import org.nb.pethome.net.NetCode;
import org.nb.pethome.net.NetResult;
import org.nb.pethome.service.IUserService;
import org.nb.pethome.service.IRedisService;
import org.nb.pethome.utils.RegexUtil;
import org.nb.pethome.utils.ResultGenerator;
import org.nb.pethome.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
public class UserService implements IUserService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private IRedisService redisService;
    @Autowired
    public UserService(RedisService redisService){
        this.redisService = redisService;
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
            redisService.cacheSet(phone+phone,code,60);
//            CodeResBeam
            return  ResultGenerator.genSuccessResult(code);
        }else {
            return  ResultGenerator.genSuccessResult(expiredV);
        }

    }
}
