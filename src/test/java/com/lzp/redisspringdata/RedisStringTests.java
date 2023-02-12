package com.lzp.redisspringdata;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lzp.redisspringdata.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest
class RedisStringTests {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    void testString() {
        stringRedisTemplate.opsForValue().set("stu_name","戴诗娴121号");
        Object stu_name = stringRedisTemplate.opsForValue().get("stu_name");
        System.out.println("stu_name="+stu_name);
    }

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    void testUser() throws JsonProcessingException {
        // 创建对象
        User user = new User("戴诗娴131号",18);

        // 手动序列化将User转为Json
        String json = MAPPER.writeValueAsString(user);
        // 写入数据
        stringRedisTemplate.opsForValue().set("user:131",json);

        // 获取数据
        String jsonUser = stringRedisTemplate.opsForValue().get("user:131");
        // 手动反序列化将Json转为User
        User res = MAPPER.readValue(jsonUser, User.class);
        System.out.println("user="+res);
    }

}
