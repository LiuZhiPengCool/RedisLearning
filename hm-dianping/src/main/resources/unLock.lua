-- 锁的key
-- 当前线程标识
-- 获取锁中的线程标识 get key
local id = redis.call('GET', KEYS[1])
-- 比较线程标识与锁中标识是否一致
if(id == ARGV[1])
then
    -- 释放锁 del key
    return redis.call('DEL', KEYS[1])
end
return 0