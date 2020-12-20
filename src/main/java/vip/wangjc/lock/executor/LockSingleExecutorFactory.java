package vip.wangjc.lock.executor;

import vip.wangjc.lock.annotation.LockSingle;
import vip.wangjc.lock.executor.service.ILockExecutorService;
import vip.wangjc.lock.executor.service.impl.SingleFairLockExecutorServiceImpl;
import vip.wangjc.lock.executor.service.impl.SingleReadLockExecutorServiceImpl;
import vip.wangjc.lock.executor.service.impl.SingleReentrantLockExecutorServiceImpl;
import vip.wangjc.lock.executor.service.impl.SingleWriteLockExecutorServiceImpl;

/**
 * 单节点锁执行器的构建工厂
 * @author wangjc
 * @title: LockExecutorFactory
 * @projectName wangjc-vip
 * @date 2020/12/12 - 15:54
 */
public class LockSingleExecutorFactory {

    /**
     * 构建普通锁的执行器
     * @param lock
     * @return
     */
    public ILockExecutorService buildSingleExecutor(LockSingle lock){
        switch (lock.type()){
            case READ:
                return new SingleReadLockExecutorServiceImpl();
            case WRITE:
                return new SingleWriteLockExecutorServiceImpl();
            case FAIR:
                return new SingleFairLockExecutorServiceImpl();
            case REENTRANT:
                return new SingleReentrantLockExecutorServiceImpl();
            default:
                throw new IllegalArgumentException("error lockSingle type argument");
        }
    }
}
