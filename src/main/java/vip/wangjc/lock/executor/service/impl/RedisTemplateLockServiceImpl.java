package vip.wangjc.lock.executor.service.impl;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import vip.wangjc.lock.entity.LockEntity;
import vip.wangjc.lock.executor.service.ILockExecutorService;

import java.util.Collections;

/**
 * 分布式锁原生RedisTemplate处理器
 * @author wangjc
 * @title: RedisTemplateLockServiceImpl
 * @projectName wangjc-vip
 * @date 2020/12/13 - 15:11
 */
public class RedisTemplateLockServiceImpl implements ILockExecutorService {

    /**
     * 加锁脚本
     */
    private static final RedisScript<String> SCRIPT_LOCK = new DefaultRedisScript<String>("return redis.call('set',KEYS[1],ARGV[1],'NX','PX',ARGV[2])", String.class);
    private static final String LOCK_SUCCESS = "OK";

    /**
     * 解锁脚本
     */
    private static final RedisScript<String> SCRIPT_UNLOCK = new DefaultRedisScript<String>("if redis.call('get',KEYS[1]) == ARGV[1] then return tostring(redis.call('del', KEYS[1])==1) else return 'false' end", String.class);

    private final RedisTemplate redisTemplate;

    public RedisTemplateLockServiceImpl(RedisTemplate redisTemplate){
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean acquire(String key, String value, Long timeout, Long expire) {
        Object lockResult = this.redisTemplate.execute(SCRIPT_LOCK,
                this.redisTemplate.getStringSerializer(), this.redisTemplate.getStringSerializer(), Collections.singletonList(key), value, String.valueOf(expire));
        return LOCK_SUCCESS.equals(lockResult);
    }

    @Override
    public boolean release(LockEntity lockEntity) {
        if(lockEntity != null){
            Object unLockResult = this.redisTemplate.execute(SCRIPT_UNLOCK,
                    this.redisTemplate.getStringSerializer(), this.redisTemplate.getStringSerializer(), Collections.singletonList(lockEntity.getKey()), lockEntity.getValue());
            return Boolean.parseBoolean(unLockResult.toString());
        }
        return false;
    }
}
