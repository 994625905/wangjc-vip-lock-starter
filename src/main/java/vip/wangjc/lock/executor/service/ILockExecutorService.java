package vip.wangjc.lock.executor.service;

import vip.wangjc.lock.entity.LockEntity;

/**
 * 定义锁的核心处理器接口
 * @author wangjc
 * @title: LockExecute
 * @projectName wangjc-vip
 * @date 2020/12/12 - 14:06
 */
public interface ILockExecutorService {

    /**
     * 获取锁
     * @param key 锁名称，标识
     * @param value 锁值
     * @param timeout 尝试获取锁的超时时间
     * @param expire 锁的有效时间，single的情况下，有效时间无效，必须手动释放
     * @return
     */
    boolean acquire(String key, String value, Long timeout, Long expire);

    /**
     * 释放锁
     * 释放锁时需要校验锁的value！
     * 原因就在于业务代码的执行时间超过了锁的自动过期时间，此时在手动释放锁的话，可能把其他线程的锁给误释放了，造成数据不安全
     * @param lockEntity
     * @return
     */
    boolean release(LockEntity lockEntity);
}
