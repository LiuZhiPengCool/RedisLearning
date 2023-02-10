package com.lzp.jedis.util;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author lzp
 * @date 2023.01.17 18:24:24
 */
public class JedisConnectionFactory {

    private static final JedisPool jedisPool;

    static {
        // 配置连接池
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(8);
        config.setMaxIdle(8);
        config.setMinIdle(0);
        config.setMaxWaitMillis(1000);
        // 创建连接池对象
        jedisPool = new JedisPool(config,"192.168.65.130",
                6379, 1000,"635241Xyz");
    }

    public static Jedis getJedis() {
        return jedisPool.getResource();
    }
}
