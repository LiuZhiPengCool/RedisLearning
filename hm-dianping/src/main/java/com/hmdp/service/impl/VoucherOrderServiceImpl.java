package com.hmdp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.dto.Result;
import com.hmdp.entity.VoucherOrder;
import com.hmdp.mapper.VoucherOrderMapper;
import com.hmdp.service.ISeckillVoucherService;
import com.hmdp.service.IVoucherOrderService;
import com.hmdp.utils.RedisIdWorker;
import com.hmdp.utils.UserHolder;
import org.redisson.api.RedissonClient;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collections;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class VoucherOrderServiceImpl extends ServiceImpl<VoucherOrderMapper, VoucherOrder> implements IVoucherOrderService {

    @Resource
    private ISeckillVoucherService seckillVoucherService;

    @Resource
    private RedisIdWorker redisIdWorker;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RedissonClient redissonClient;

    private static final DefaultRedisScript<Long> SECKILL_SCRIPT;
    static {
        SECKILL_SCRIPT = new DefaultRedisScript<>();
        SECKILL_SCRIPT.setLocation(new ClassPathResource("seckill.lua"));
        SECKILL_SCRIPT.setResultType(Long.class);
    }

    @Override
    public Result seckillVoucher(Long voucherId) {
        // 获取用户
        Long userId = UserHolder.getUser().getId();
        // 1.执行lua脚本
        Long result = stringRedisTemplate.execute(
                SECKILL_SCRIPT,
                Collections.emptyList(),
                voucherId.toString(), userId.toString());
        // 2.判断结果是0
        assert result != null;
        int r = result.intValue();
        if(r != 0) {
            // 2.1.不为0，代表没有购买资格
            return Result.fail(r == 1 ? "库存不足" : "不能重复下单");
        }
            long orderId = redisIdWorker.nextId("order");
        // 2.2.为0，有购买资格，把下单消息保存到阻塞队列
        // TODO 保存阻塞队列
        // 3.返回订单id
        return Result.ok(orderId);
    }

/*    @Override
    public Result seckillVoucher(Long voucherId) {
        // 1.查询优惠卷
        SeckillVoucher voucher = seckillVoucherService.getById(voucherId);
        // 2.判断秒杀是否开始
        if (voucher.getBeginTime().isAfter(LocalDateTime.now())) {
            // 尚未开始
            return Result.fail("秒杀尚未开始!");
        }
        // 3.判断秒杀是否已经结束
        if (voucher.getEndTime().isBefore(LocalDateTime.now())) {
            // 已经结束
            return Result.fail("秒杀已经结束!");
        }
        // 4.判断库存是否充足
        if (voucher.getStock() < 1) {
            // 库存不足
            return Result.fail("库存不足!");
        }
        Long userId = UserHolder.getUser().getId();

        // 获取锁对象
        // SimpleRedisLock redisLock = new SimpleRedisLock("order:" + userId, stringRedisTemplate);
        RLock lock = redissonClient.getLock("lock:order" + userId);

        // boolean isLock = redisLock.tryLock(2000);
        boolean isLock = lock.tryLock();
        // 判断是否获取锁成功
        if (!isLock) {
            return Result.fail("你抢你妈呢！");
        }
        try {
            // 获取代理对象
            IVoucherOrderService proxy = (IVoucherOrderService) AopContext.currentProxy();
            // 7.返回订单ID
            return proxy.createVoucherOrder(voucherId);
        } finally {
            // redisLock.unLock();
            lock.unlock();
        }

    }*/

    @Transactional
    public Result createVoucherOrder(Long voucherId) {
        // 4.一人一单
        Long userId = UserHolder.getUser().getId();

        // 4.1.查询订单
        int count = query().eq("user_id", userId).eq("voucher_id", voucherId).count();
        // 4.2.判断是否存在
        if (count > 0) {
            // 用户购买过
            return Result.fail("仅能购买一次");
        }

        // 5.扣减库存
        boolean success = seckillVoucherService.update()
                .setSql("stock = stock-1")
                .eq("voucher_id", voucherId).gt("stock", 0)
                .update();

        if (!success) {
            // 扣减失败
            return Result.fail("库存不足!");
        }

        // 6.创建订单
        VoucherOrder voucherOrder = new VoucherOrder();
        // 6.1.订单id
        long orderId = redisIdWorker.nextId("order");
        voucherOrder.setId(orderId);
        // 6.2.用户id
        voucherOrder.setUserId(userId);
        // 6.3.代金卷id
        voucherOrder.setVoucherId(voucherId);
        save(voucherOrder);
        return Result.ok(orderId);
    }
}
