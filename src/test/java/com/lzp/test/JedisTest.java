package com.lzp.test;

import com.lzp.jedis.util.JedisConnectionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisFactory;

import java.util.Map;

/**
 * @author lzp
 * @date 2023.01.17 16:00:00
 */
public class JedisTest {

    private Jedis jedis;

    // 连接redis
    @BeforeEach
    void setup() {
        // 1.建立连接
//        jedis = new Jedis("192.168.65.130",6379);

        // 通过连接池连接Jedis
        jedis = JedisConnectionFactory.getJedis();
        // 2.设置密码
        jedis.auth("635241Xyz");
        // 3.选择库
        jedis.select(0);

    }

    // redis string数据类型
    @Test
    void test() {
        // 存入数据
        String result = jedis.set("name", "虎哥");
        System.out.println("result == "+ result);
        // 获取数据
        String value = jedis.get("name");
        System.out.println("value == "+value);
    }

    // redis hash数据类型
    @Test
    void hashTest() {
        // 插入hash数据
        jedis.hset("user:1","name","裴雨涵");
        jedis.hset("user:1","age","26");
        Map<String, String> result = jedis.hgetAll("user:1");
        System.out.println(result);
    }

    // 关闭redis
    @AfterEach
    void tearDown() {
        if(jedis != null) {
            jedis.close();
        }
    }
}
