package vip.wangjc.lock.executor;

import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import vip.wangjc.lock.annotation.LockCloud;
import vip.wangjc.lock.executor.service.ILockExecutorService;
import vip.wangjc.lock.executor.service.impl.*;

/**
 * 分布式锁执行器的构建工厂
 * @author wangjc
 * @title: LockCloudExecutorFactory
 * @projectName wangjc-vip
 * @date 2020/12/12 - 15:54
 */
public class LockCloudExecutorFactory {

    private final RedisTemplate redisTemplate;

    private final RedissonClient redissonClient;

    public LockCloudExecutorFactory(RedisTemplate redisTemplate, RedissonClient redissonClient){
        this.redisTemplate = redisTemplate;
        this.redissonClient = redissonClient;
    }

    /**
     * 构建分布式锁的执行器
     * @param lock
     * @return
     */
    public ILockExecutorService buildCloudExecutor(LockCloud lock){
        switch (lock.client()){
            case REDIS_TEMPLATE:
                return new RedisTemplateLockServiceImpl(this.redisTemplate);
            case REDISSON:
                switch (lock.type()){
                    case REENTRANT:
                        return new RedissonReentrantLockExecutorServiceImpl(this.redissonClient);
                    case READ:
                        return new RedissonReadLockExecutorServiceImpl(this.redissonClient);
                    case WRITE:
                        return new RedissonWriteLockExecutorServiceImpl(this.redissonClient);
                    case FAIR:
                        return new RedissonFairLockExecutorServiceImpl(this.redissonClient);
                    default:
                        throw new IllegalArgumentException("error lockCloud type argument");
                }
            default:
                throw new IllegalArgumentException("error lockCloud client argument");
        }
    }
}
