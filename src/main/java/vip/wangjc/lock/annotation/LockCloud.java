package vip.wangjc.lock.annotation;

import vip.wangjc.lock.builder.service.ILockKeyBuilderService;
import vip.wangjc.lock.builder.service.impl.DefaultLockKeyBuilderServiceImpl;
import vip.wangjc.lock.entity.LockCloudClientType;
import vip.wangjc.lock.entity.LockEntity;
import vip.wangjc.lock.entity.LockType;
import vip.wangjc.lock.failureStrategy.service.ILockFailureStrategyService;
import vip.wangjc.lock.failureStrategy.service.impl.DefaultLockFailureStrategyServiceImpl;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 分布式锁的注解（Redis && Zookeeper）
 * @author wangjc
 * @title: LockCloud
 * @projectName wangjc-vip
 * @date 2020/12/12 - 15:45
 */
@Target({ElementType.METHOD}) // 只作用于方法
@Retention(RetentionPolicy.RUNTIME)
public @interface LockCloud {

    /**
     * 分布式锁的客户端支持，
     * @return 默认是redisson
     */
    LockCloudClientType client() default LockCloudClientType.REDISSON;

    /**
     * 锁类型：可重入锁，读锁，写锁，公平锁
     * @return 默认为可重入锁，目前就redisson支持
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
     * 锁的过期自动释放时间 单位：毫秒 （分布式锁必须设置自动释放时间，为0无效）
     * @return 默认为30，过期时间一定是要长于业务的执行时间，
     */
    long expire() default 30000;

    /**
     * 尝试获取锁的超时时间 单位：毫秒 （分布式锁必须设置自旋获取锁的超时时间，为0无效）
     * @return 默认为3，结合业务，建议该时间不宜设置过长，特别在并发高的情况下.
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
