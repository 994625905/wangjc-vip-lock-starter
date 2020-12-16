package vip.wangjc.lock.annotation;

import vip.wangjc.lock.builder.service.ILockKeyBuilderService;
import vip.wangjc.lock.builder.service.impl.DefaultLockKeyBuilderServiceImpl;
import vip.wangjc.lock.entity.LockEntity;
import vip.wangjc.lock.entity.LockType;
import vip.wangjc.lock.failureStrategy.service.ILockFailureStrategyService;
import vip.wangjc.lock.failureStrategy.service.impl.DefaultLockFailureStrategyServiceImpl;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 普通锁的注解（API自带的Lock体系，single）
 * @author wangjc
 * @title: Lock
 * @projectName wangjc-vip
 * @date 2020/12/12 - 14:33
 */
@Target({ElementType.METHOD}) // 只作用于方法
@Retention(RetentionPolicy.RUNTIME)
public @interface LockSingle {

    /**
     * 锁类型：可重入锁，读锁，写锁，公平锁
     * @return 默认为可重入锁
     */
    LockType type() default LockType.REENTRANT;

    /**
     * 锁名称
     * @return 默认为包名+方法名
     */
    String[] keys() default "";

    /**
     * 锁名称的构建器
     * @return
     */
    Class<? extends ILockKeyBuilderService> keyBuilder() default DefaultLockKeyBuilderServiceImpl.class;

    /**
     * 尝试获取锁的超时时间 单位：毫秒
     * @return 默认为3，
     */
    long acquireTimeout() default 3000;

    /**
     * 失败策略，默认是抛出异常
     * 获取锁：{@link DefaultLockFailureStrategyServiceImpl#lockFailure(Long, Integer)}
     * 释放锁：{@link DefaultLockFailureStrategyServiceImpl#releaseFailure(LockEntity)}
     * @return
     */
    Class<? extends ILockFailureStrategyService> lockFailureStrategy() default DefaultLockFailureStrategyServiceImpl.class;
}
