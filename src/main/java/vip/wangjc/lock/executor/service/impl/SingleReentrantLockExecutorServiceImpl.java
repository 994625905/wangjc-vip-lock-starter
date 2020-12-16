package vip.wangjc.lock.executor.service.impl;

import vip.wangjc.lock.entity.LockEntity;
import vip.wangjc.lock.executor.pool.LockSinglePool;
import vip.wangjc.lock.executor.service.ILockExecutorService;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 单节点的可重入锁执行器
 * @author wangjc
 * @title: SingleReentrantLockExecutorServiceImpl
 * @projectName wangjc-vip
 * @date 2020/12/12 - 17:00
 */
public class SingleReentrantLockExecutorServiceImpl implements ILockExecutorService {

    /**
     * 可重入锁：一个线程中可以多次获取同一把锁。粒度小，灵活度高
     * synchronized 其实也是可重入锁
     * @param key 锁名称，标识
     * @param value 锁值
     * @param timeout 尝试获取锁的超时时间
     * @param expire 锁的有效时间，single的情况下，有效时间无效，必须手动释放
     * @return
     */
    @Override
    public boolean acquire(String key, String value, Long timeout, Long expire) {
        try {
            ReentrantLock reentrantLock = LockSinglePool.getLock(key,ReentrantLock.class);
            return reentrantLock.tryLock(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            return false;
        }
    }

    @Override
    public boolean release(LockEntity lockEntity) {
        if(lockEntity != null){
            LockSinglePool.getLock(lockEntity.getKey(),ReentrantLock.class).unlock();
            return true;
        }
        return false;
    }
}
