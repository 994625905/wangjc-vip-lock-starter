package vip.wangjc.lock.executor.service.impl;

import vip.wangjc.lock.entity.LockEntity;
import vip.wangjc.lock.executor.pool.LockSinglePool;
import vip.wangjc.lock.executor.service.ILockExecutorService;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 单节点的读锁执行器
 * @author wangjc
 * @title: NormalLockExecutorServiceImpl
 * @projectName wangjc-vip
 * @date 2020/12/12 - 16:04
 */
public class SingleReadLockExecutorServiceImpl implements ILockExecutorService {

    /**
     *  读写锁特点特点：读锁是共享锁，写锁是排他锁，两者不能同时存在，
     *  ReentrantReadWriteLock适合于读多写少的场合，可以提高并发效率，而ReentrantLock适合普通场合
     * @param key 锁名称，标识
     * @param value 锁值
     * @param timeout 尝试获取锁的超时时间
     * @param expire 锁的有效时间，single的情况下，有效时间无效，必须手动释放
     * @return
     */
    @Override
    public boolean acquire(String key, String value, Long timeout, Long expire) {
        try {
            /**
             * tryLock，一进来就尝试获取锁，获取不到就返回
             * lock，排队阻塞（非公平的lock首先会尝试去抢占，抢占不到就执行公平锁的逻辑）
             */
            ReentrantReadWriteLock.ReadLock readLock = LockSinglePool.getReadLock(key);
            return readLock.tryLock(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean release(LockEntity lockEntity) {
        if(lockEntity != null){
            LockSinglePool.getLock(lockEntity.getKey(),ReentrantReadWriteLock.ReadLock.class).unlock();
            return true;
        }
        return false;
    }
}
