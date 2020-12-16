package vip.wangjc.lock.failureStrategy.service;

import vip.wangjc.lock.entity.LockEntity;

/**
 * 获取锁/释放锁的失败策略
 * @author wangjc
 * @title: ILockFailureStrategyService
 * @projectName wangjc-vip
 * @date 2020/12/12 - 15:24
 */
public interface ILockFailureStrategyService {

    /**
     * 加锁的失败策略
     * @param timeout ：超时
     * @param acquireCount ：尝试获取锁次数
     */
    void lockFailure(Long timeout, Integer acquireCount);

    /**
     * 释放锁的失败策略
     * @param lockEntity
     */
    void releaseFailure(LockEntity lockEntity);
}
