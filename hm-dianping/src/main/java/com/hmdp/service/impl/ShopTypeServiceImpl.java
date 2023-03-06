package com.hmdp.service.impl;

import cn.hutool.json.JSONUtil;
import com.hmdp.dto.Result;
import com.hmdp.entity.ShopType;
import com.hmdp.mapper.ShopTypeMapper;
import com.hmdp.service.IShopTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

import static com.hmdp.utils.RedisConstants.TYPE_SHOP_KEY;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements IShopTypeService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result queryTypeList() {

        // 1.到redis中查询商铺类型
        String key = TYPE_SHOP_KEY;
        String types = stringRedisTemplate.opsForValue().get(key);

        // 2.如果有，直接返回
        List<ShopType> shopTypes = JSONUtil.toList(types, ShopType.class);
        if(!shopTypes.isEmpty()) {
            return Result.ok(shopTypes);
        }

        // 3.没有，从数据库查找
        List<ShopType> typeList = query().orderByAsc("sort").list();

        // 4.将数据存入redis
        String json = JSONUtil.toJsonStr(typeList);
        stringRedisTemplate.opsForValue().set(key, json);

        return Result.ok(typeList);
    }
}
