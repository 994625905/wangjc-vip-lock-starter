package vip.wangjc.lock.executor.service.impl;

import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import vip.wangjc.lock.entity.LockEntity;
import vip.wangjc.lock.executor.service.ILockExecutorService;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 分布式锁的写锁（Redisson支持）
 * @author wangjc
 * @title: RedissonWriteLockExecutorServiceImpl
 * @projectName wangjc-vip
 * @date 2020/12/13 - 14:58
 */
public class RedissonWriteLockExecutorServiceImpl implements ILockExecutorService {

    private RReadWriteLock lock;

    private final RedissonClient redissonClient;

    public RedissonWriteLockExecutorServiceImpl(RedissonClient redissonClient){
        this.redissonClient = redissonClient;
    }

    @Override
    public boolean acquire(String key, String value, Long timeout, Long expire) {
        try {
            lock = this.redissonClient.getReadWriteLock(key);
            return lock.writeLock().tryLock(timeout, expire, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            return false;
        }
    }

    @Override
    public boolean release(LockEntity lockEntity) {
        if(lock.writeLock().isHeldByCurrentThread() && lockEntity != null){
            try {
                return lock.writeLock().forceUnlockAsync().get();
            } catch (InterruptedException | ExecutionException e) {
                return false;
            }
        }
        return false;
    }
}
