package vip.wangjc.lock.entity;

import vip.wangjc.lock.executor.service.ILockExecutorService;

/**
 * 分布式锁的实体类，加强版，过期时间和尝试获取次数
 * @author wangjc
 * @title: LockCloudEntity
 * @projectName wangjc-vip
 * @date 2020/12/14 - 10:34
 */
public class LockCloudEntity extends LockEntity{

    public LockCloudEntity(String key, String value, Long acquireTimeout, ILockExecutorService lockExecutor,Long expire, int acquireCount) {
        super(key, value, acquireTimeout, lockExecutor);
        this.expire = expire;
        this.acquireCount = acquireCount;
    }

    /**
     * 锁的过期时间
     */
    private Long expire;

    /**
     * 尝试获取锁的次数
     */
    private int acquireCount;

    public Long getExpire() {
        return expire;
    }

    public void setExpire(Long expire) {
        this.expire = expire;
    }

    public int getAcquireCount() {
        return acquireCount;
    }

    public void setAcquireCount(int acquireCount) {
        this.acquireCount = acquireCount;
    }
}
