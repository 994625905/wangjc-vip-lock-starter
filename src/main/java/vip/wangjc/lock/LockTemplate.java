package vip.wangjc.lock;

import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import vip.wangjc.lock.annotation.LockCloud;
import vip.wangjc.lock.annotation.LockSingle;
import vip.wangjc.lock.entity.LockCloudEntity;
import vip.wangjc.lock.entity.LockEntity;
import vip.wangjc.lock.executor.LockExecutorFactory;
import vip.wangjc.lock.executor.service.ILockExecutorService;
import vip.wangjc.lock.util.LockUtil;


/**
 * 锁的模板方法
 * @author wangjc
 * @title: LockTemplate
 * @projectName wangjc-vip
 * @date 2020/12/12 - 18:25
 */
public class LockTemplate {

    private static final Logger logger = LoggerFactory.getLogger(LockTemplate.class);

    private static final String PROCESS_ID = LockUtil.getLocalMac() + LockUtil.getJVMProcessId();

    /** 锁的执行器构建工厂 */
    private final LockExecutorFactory lockExecutorFactory;

    public LockTemplate(LockExecutorFactory lockExecutorFactory){
        this.lockExecutorFactory = lockExecutorFactory;
    }

    /**
     * 加锁，普通锁
     * @param methodInvocation
     * @param lock
     * @return
     * @throws Exception
     */
    public LockEntity lockSingle(MethodInvocation methodInvocation, LockSingle lock) throws Exception{
        Assert.isTrue(lock.acquireTimeout() > 0,"tryTimeout must more than 0");

        /** 构建锁名称key-->反射调用机制 */
        String key = lock.keyBuilder().newInstance().buildKey(methodInvocation, lock.keys());

        /** 构建锁值 */
        String value = PROCESS_ID + Thread.currentThread().getName();

        /** 当前锁的核心处理器 */
        ILockExecutorService lockExecutorService = this.lockExecutorFactory.buildSingleExecutor(lock);

        /** 尝试获取锁 */
        boolean result = lockExecutorService.acquire(key,value,lock.acquireTimeout(),null);
        if(result){
            return new LockEntity(key,value,lock.acquireTimeout(),lockExecutorService);
        }
        logger.debug("lock failed, key[{}], acquireTimeout [{}] ms",key,lock.acquireTimeout());
        /** 获取锁的失败策略-->，单节点普通锁无需失败策略 */
        return null;
    }

    /**
     * 释放锁，普通锁
     * @param lockEntity
     * @return
     */
    public boolean releaseLockSingle(LockEntity lockEntity,LockSingle lock) throws Exception{
        boolean release = lockEntity.getLockExecutor().release(lockEntity);
        if(release){
            return true;
        }
        /** 释放锁的失败策略-->继承失败策略，可自定义 */
        lock.lockFailureStrategy().newInstance().releaseFailure(lockEntity);
        return false;
    }

    /**
     * 加锁，分布式锁
     * @param methodInvocation
     * @param lock
     * @return
     * @throws Exception
     */
    public LockCloudEntity lockCloud(MethodInvocation methodInvocation, LockCloud lock) throws Exception{
        Assert.isTrue(lock.acquireTimeout() > 0,"tryTimeout must more than 0");

        /** 构建锁名称key-->反射调用机制 */
        String key = lock.keyBuilder().newInstance().buildKey(methodInvocation, lock.keys());

        /** 构建锁值 */
        String value = PROCESS_ID + Thread.currentThread().getName();

        /** 当前锁的核心处理器 */
        ILockExecutorService lockExecutorService = this.lockExecutorFactory.buildCloudExecutor(lock);

        /** 重试次数，自加 */
        int acquireCount = 0;

        /** 到达重试结构前的时间，毫秒级别 */
        long startTime = System.currentTimeMillis();

        while (System.currentTimeMillis() - startTime < lock.acquireTimeout()){
            acquireCount++;

            /** 重试获取锁 */
            boolean result = lockExecutorService.acquire(key,value,lock.acquireTimeout(),lock.expire());
            if(result){
                return new LockCloudEntity(key,value,lock.acquireTimeout(),lockExecutorService,lock.expire(),acquireCount);
            }
            Thread.sleep(100);//延时0.1秒再重试
        }
        logger.info("lock failed, key[{}], acquireTimeout [{}] ms, try [{}] times",key,lock.acquireTimeout(),acquireCount);

        /** 获取锁的失败策略-->，继承失败策略，可自定义 */
        lock.lockFailureStrategy().newInstance().lockFailure(lock.acquireTimeout(), acquireCount);

        return null;
    }

    /**
     * 释放锁，分布式锁
     * @param lockCloudEntity
     * @return
     */
    public boolean releaseLockCloud(LockCloudEntity lockCloudEntity, LockCloud lock) throws Exception{
        boolean release = lockCloudEntity.getLockExecutor().release(lockCloudEntity);
        if(release){
            return true;
        }
        /** 释放锁的失败策略-->继承失败策略，可自定义 */
        lock.lockFailureStrategy().newInstance().releaseFailure(lockCloudEntity);
        return false;
    }
}
