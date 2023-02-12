package com.lzp.redisspringdata;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
class RedisSpringdataApplicationTests {

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Test
    void testString() {
        redisTemplate.opsForValue().set("stu_name","戴诗娴");
        Object stu_name = redisTemplate.opsForValue().get("stu_name");
        System.out.println("stu_name="+stu_name);
    }

}
