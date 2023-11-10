package org.nb.pethome.service.impl;

import org.nb.pethome.service.IRedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class RedisService implements IRedisService {


    /**
     * slf4j日志
     */
    private  final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * 自定义 key 三种
     * String key:String value         普通key:value
     * String key:Set<String> set      key:set集合
     * String key:List<String> list    key:list集合
     */
    private static final String KEY_PREFIX_KEY = "info:bear:key";
    private static final String KEY_PREFIX_SET = "info:bear:set";
    private static final String KEY_PREFIX_LIST = "info:bear:list";

    private final RedisTemplate<String, String> redisTemplate;

    /**
     * 注入
     * @param redisTemplate
     */
    @Autowired
    public RedisService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean cacheValue(String k, String v, long time) {
        try {
            String key = KEY_PREFIX_KEY + k;
            ValueOperations<String,String> ops = redisTemplate.opsForValue();
            ops.set(key,v);
            if(time>0){
                redisTemplate.expire(key,time, TimeUnit.SECONDS);
            }
            return  true;
        }catch (Throwable e){
            log.error("缓存存入失败key:[{}] value:[{}]", k, v);
        }
        return false;
    }

    @Override
    public boolean cacheValue(String key, String value) {
        return cacheValue(key,value,-1);
    }

    @Override
    public boolean containsValueKey(String key) {
        return containsKey(KEY_PREFIX_KEY+key);
    }

    @Override
    public boolean containsSetKey(String key) {
        return containsKey(KEY_PREFIX_SET+key);
    }

    @Override
    public boolean containsListKey(String key) {
        return containsKey(KEY_PREFIX_LIST+key);
    }


    @Override
    public boolean containsKey(String key) {
        try {
            return  redisTemplate.hasKey(key);
        }catch (Throwable e){
            log.error("判断缓存存在失败key:[" + key + "],错误信息 Codeor[{}]", e);
        }
        return false;
    }

    @Override
    public String getValue(String key) {
        try {
            ValueOperations<String,String> ops = redisTemplate.opsForValue();
            return  ops.get(KEY_PREFIX_KEY+key);
        }catch (Throwable e){
            log.error("根据 key 获取缓存失败，当前key:[{}],失败原因 Codeor:[{}]", key, e);
        }
        return null;
    }

    @Override
    public boolean removeValue(String key) {
        return remove(KEY_PREFIX_KEY+key);
    }

    @Override
    public boolean removeSet(String key) {
        return remove(KEY_PREFIX_SET+key);
    }

    @Override
    public boolean removeList(String key) {
        return remove(KEY_PREFIX_LIST+key);
    }

    @Override
    public boolean cacheSet(String k, String v, long time) {
        try {
            String key = KEY_PREFIX_SET+k;
            SetOperations<String,String> opsForSet = redisTemplate.opsForSet();
            opsForSet.add(key,v);
            if (time>0){
                redisTemplate.expire(key,time,TimeUnit.SECONDS);
                return  true;
            }
        }catch (Throwable e){
            log.error("缓存 set 失败 当前 key:[{}] 失败原因 [{}]", k, e);
        }
        return false;
    }

    @Override
    public boolean cacheSet(String key, String value) {
        return cacheSet(key,value,-1);
    }

    @Override
    public boolean cacheSet(String k, Set<String> v, long time) {
        try {
            String key = KEY_PREFIX_SET+k;
            SetOperations<String,String> opsForSet = redisTemplate.opsForSet();
            opsForSet.add(key,v.toArray(new String[v.size()]));
            if (time>0){
                redisTemplate.expire(key,time,TimeUnit.SECONDS);
            }
            return true;
        }catch (Throwable e){
            log.error("缓存 set 失败 当前 key:[{}],失败原因 [{}]", k, e);
        }
        return false;
    }

    @Override
    public boolean cacheSet(String k, Set<String> v) {
        return cacheSet(k, v,-1);
    }

    @Override
    public Set<String> getSet(String k) {
        try {
            String key = KEY_PREFIX_SET+k;
            SetOperations<String,String> opsForSet = redisTemplate.opsForSet();
            return opsForSet.members(key);
        }catch (Throwable e){
            log.error("获取缓存set失败 当前 key:[{}],失败原因 [{}]", k, e);
        }
        return null;
    }

    @Override
    public boolean cacheList(String k, String v, long time) {
        try {
            String key = KEY_PREFIX_LIST+k;
            ListOperations<String, String> opsForList = redisTemplate.opsForList();
            opsForList.rightPush(key,v);
            if (time>0){
                redisTemplate.expire(key,time,TimeUnit.SECONDS);
            }
            return true;
        }catch (Throwable e){
            log.error("缓存list失败 当前 key:[{}],失败原因 [{}]", k, e);
        }
        return false;
    }

    @Override
    public boolean cacheList(String k, String v) {
        return cacheList(k,v,-1);
    }

    @Override
    public boolean cacheList(String k, List<String> v, long time) {
        try {
            String key = KEY_PREFIX_LIST+k;
            ListOperations<String, String> opsForList = redisTemplate.opsForList();
            opsForList.rightPushAll(key,v);
            if (time>0){
                redisTemplate.expire(key,time,TimeUnit.SECONDS);
            }
            return true;
        }catch (Throwable e){
            log.error("缓存list失败 当前 key:[{}],失败原因 [{}]", k, e);
        }
        return false;
    }

    @Override
    public boolean cacheList(String k, List<String> v) {
        return cacheList(k,v,-1);
    }

    @Override
    public List<String> getList(String k, long start, long end) {
        try {
            String key = KEY_PREFIX_LIST+k;
            ListOperations<String, String> opsForList = redisTemplate.opsForList();
            return opsForList.range(key,start,end);
        }catch (Throwable e){
            log.error("获取list缓存失败 当前 key:[{}],失败原因 [{}]", k, e);
        }
        return null;
    }

    @Override
    public long getListSize(String key) {
        try {
            ListOperations<String,String> opsForList = redisTemplate.opsForList();
            return  opsForList.size(KEY_PREFIX_LIST+key);
        }catch (Throwable e){
            log.error("获取list长度失败key[" + KEY_PREFIX_LIST + key + "], Codeor[" + e + "]");
        }
        return 0;
    }

    @Override
    public long getListSize(ListOperations<String, String> listOps, String k) {
        try {
            return  listOps.size(k);
        }catch (Throwable e){
            log.error("获取list长度失败key[" + KEY_PREFIX_LIST + k + "], Codeor[" + e + "]");
        }
        return 0;
    }

    @Override
    public boolean removeOneOfList(String k) {
        try {
            String key = KEY_PREFIX_LIST+k;
            ListOperations<String,String> opsForList = redisTemplate.opsForList();
            opsForList.rightPop(key);
            return  true;
        }catch (Throwable e){
            log.error("移除list缓存失败 key[" + KEY_PREFIX_LIST + k + "], Codeor[" + e + "]");
        }
        return false;
    }


    private boolean remove(String key) {
        try {
            redisTemplate.delete(key);
            return true;
        } catch (Throwable e) {
            log.error("移除缓存失败 key:[{}] 失败原因 [{}]", key, e);
        }
        return false;
    }
}

