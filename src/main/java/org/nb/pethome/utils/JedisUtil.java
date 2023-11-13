package org.nb.pethome.utils;

import lombok.val;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;


public enum  JedisUtil {
    INSTANCE;

    private static JedisPool pool;

    static {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(2);
        config.setMaxTotal(10);
        config.setMaxWaitMillis(2 * 1000);
        config.setTestOnBorrow(true);
        pool = new JedisPool(config, "127.0.0.1", 6379, 2 * 1000);
    }

    //获取
    public Jedis getResource() {
        return pool.getResource();
    }

    public void release(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }

    public void set(String key, String val) {
        Jedis jedis = null;
        try {
            jedis = this.getResource();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.release(jedis);
        }
    }

    public void set(String key, String val, int seconds) {
        Jedis jedis = null;
        try {
            jedis = this.getResource();
            val seconds1 = seconds;
            jedis.setex(key, seconds, val);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.release(jedis);
        }
    }

    public String get(String key) {
        Jedis jedis = null;
        try {
            jedis = this.getResource();
            return jedis.get(key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.release(jedis);
        }
        return null;
    }
}
