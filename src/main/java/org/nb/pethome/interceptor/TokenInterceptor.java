package org.nb.pethome.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.nb.pethome.common.Constants;
import org.nb.pethome.net.NetCode;
import org.nb.pethome.net.NetResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

@Component
public class TokenInterceptor implements HandlerInterceptor {
    private Logger logger = LoggerFactory.getLogger(TokenInterceptor.class);

    private RedisTemplate redisTemplate;

    public TokenInterceptor(RedisTemplate redisTemplate) {
        this.redisTemplate  =redisTemplate;
    }


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        logger.info("请求来了"+request.getRequestURL().toString());
        //要求接口里面的请求头必须有token
        String token = request.getHeader("token");
        if(!StringUtils.isEmpty(token)){
            //如果token有,那么就去redis拿到token 对应的用户信息
            Object obj = redisTemplate.opsForValue().get(token);
            if (obj==null){
                writeRes(response, NetCode.TOKEN_INVALID, Constants.INVALID_TOKEN,null);
                return  false;
            }else {
                //更新token
                redisTemplate.opsForValue().set(token,obj,30, TimeUnit.MINUTES);
                return  true;
            }
        }
        writeRes(response,NetCode.TOKEN_NOT_EXIST,Constants.INVALID_REQUEST,null);
        return false;
    }

    private void writeRes(HttpServletResponse response,int code,String message,Object data) throws IOException {
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json; charset=UTF-8");
        PrintWriter writer = response.getWriter();
        NetResult netResult = new NetResult();
        netResult.setResultCode(code);
        netResult.setMessage(message);
        if(data!=null){
            netResult.setData(data);
        }
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(netResult);
        writer.write(json);
        writer.close();
    }
}
