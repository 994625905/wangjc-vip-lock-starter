package vip.wangjc.lock.builder.service;

import org.aopalliance.intercept.MethodInvocation;

/**
 * 锁的名称key生成器
 * @author wangjc
 * @title: LockKeyBuilder
 * @projectName wangjc-vip
 * @date 2020/12/12 - 14:41
 */
public interface ILockKeyBuilderService {

    /**
     * 构建key
     * @param invocation：拦截的invocation
     * @param definitionKeys：定义
     * @return
     */
    String buildKey(MethodInvocation invocation, String[] definitionKeys);
}
