package org.nb.pethome.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTest {

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 存入缓存键 key：value
     * first ：siwei
     * second：siweiWu （30秒过期时间）
     */
    @Test
    public void setRedis() {
        //缓存中最常用的方法
        redisTemplate.opsForValue().set("first", "siwei");

        //设置缓存过期时间为30   单位：秒　　　　
        //关于TimeUnit下面有部分源码截图
        redisTemplate.opsForValue().set("second", "siweiWu", 30, TimeUnit.SECONDS);
        System.out.println("存入缓存成功");
    }


    /**
     * 根据key 获取 value
     */
    @Test
    public void getRedis() {
        String first = redisTemplate.opsForValue().get("first");
        String second = redisTemplate.opsForValue().get("second");

        System.out.println("取出缓存中first的数据是:" + first);
        System.out.println("取出缓存中second的数据是:" + second);

    }


    /**
     * 根据key 删除缓存
     */
    @Test
    public void delRedis() {
        //根据key删除缓存
        Boolean first = redisTemplate.delete("first");

        System.out.println("是否删除成功:" + first);
    }
}
