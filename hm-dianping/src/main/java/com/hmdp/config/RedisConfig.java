package com.hmdp.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sun.security.krb5.KrbException;

/**
 * @author lzp
 * @date 2023.03.06 12:30:30
 */
@Configuration
public class RedisConfig {

    @Bean
    public RedissonClient redissonClient() throws KrbException {
        // 配置类
        Config config = new Config();
        // 添加redis地址，可以单点，也可以集群
        config.useSingleServer().setAddress("redis://192.168.65.130:6379").setPassword("635241Xyz");
        // 创建客户端
        return Redisson.create(config);
    }

}
