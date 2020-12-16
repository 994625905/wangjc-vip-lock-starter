package vip.wangjc.lock.executor.service.impl;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import vip.wangjc.lock.entity.LockEntity;
import vip.wangjc.lock.executor.service.ILockExecutorService;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 分布式锁的公平锁（Redisson支持）
 * @author wangjc
 * @title: RedissonFairLockExecutorServiceImpl
 * @projectName wangjc-vip
 * @date 2020/12/13 - 15:05
 */
public class RedissonFairLockExecutorServiceImpl implements ILockExecutorService {

    private RLock lock;

    private final RedissonClient redissonClient;

    public RedissonFairLockExecutorServiceImpl(RedissonClient redissonClient){
        this.redissonClient = redissonClient;
    }

    @Override
    public boolean acquire(String key, String value, Long timeout, Long expire) {
        try {
            lock = redissonClient.getFairLock(key);
            return lock.tryLock(timeout, expire, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            return false;
        }
    }

    @Override
    public boolean release(LockEntity lockEntity) {
        if(lock.isHeldByCurrentThread() && lockEntity != null){
            try {
                return lock.forceUnlockAsync().get();
            } catch (InterruptedException | ExecutionException e) {
                return false;
            }
        }
        return false;
    }
}
