package vip.wangjc.lock.entity;

import vip.wangjc.lock.executor.service.ILockExecutorService;

import java.io.Serializable;

/**
 * 锁的实体类
 * @author wangjc
 * @title: LockEntity
 * @projectName wangjc-vip
 * @date 2020/12/12 - 13:59
 */
public class LockEntity implements Serializable {

    private static final long serialVersionUID = -4294962094263189454L;

    public LockEntity(String key, String value, Long acquireTimeout, ILockExecutorService lockExecutor) {
        this.key = key;
        this.value = value;
        this.acquireTimeout = acquireTimeout;
        this.lockExecutor = lockExecutor;
    }

    /**
     * 锁名称
     */
    private String key;

    /**
     * 锁值
     */
    private String value;

    /**
     * 获取锁的超时时间
     */
    private Long acquireTimeout;

    /**
     * 锁的核心执行器
     */
    private ILockExecutorService lockExecutor;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Long getAcquireTimeout() {
        return acquireTimeout;
    }

    public void setAcquireTimeout(Long acquireTimeout) {
        this.acquireTimeout = acquireTimeout;
    }

    public ILockExecutorService getLockExecutor() {
        return lockExecutor;
    }

    public void setLockExecutor(ILockExecutorService lockExecutor) {
        this.lockExecutor = lockExecutor;
    }
}
