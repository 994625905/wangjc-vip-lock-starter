package vip.wangjc.lock.executor.service.impl;

import vip.wangjc.lock.entity.LockEntity;
import vip.wangjc.lock.executor.pool.LockSinglePool;
import vip.wangjc.lock.executor.service.ILockExecutorService;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 单节点的公平锁执行器（防止线程饿死）
 * @author wangjc
 * @title: SingleFairLockExecutorServiceImpl
 * @projectName wangjc-vip
 * @date 2020/12/12 - 17:11
 */
public class SingleFairLockExecutorServiceImpl implements ILockExecutorService {

    @Override
    public boolean acquire(String key, String value, Long timeout, Long expire) {
        try {
            ReentrantLock fairLock = LockSinglePool.getFairLock(key);
            return fairLock.tryLock(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean release(LockEntity lockEntity) {
        if(lockEntity != null){
            LockSinglePool.getFairLock(lockEntity.getKey()).unlock();
            return true;
        }
        return false;
    }
}
