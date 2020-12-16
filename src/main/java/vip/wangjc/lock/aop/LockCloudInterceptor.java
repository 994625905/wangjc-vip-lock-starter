package vip.wangjc.lock.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import vip.wangjc.lock.LockTemplate;
import vip.wangjc.lock.annotation.LockCloud;
import vip.wangjc.lock.entity.LockCloudEntity;

/**
 * 分布式锁的aop切面处理器
 * @author wangjc
 * @title: LockCloudInterceptor
 * @projectName wangjc-vip
 * @date 2020/12/13 - 15:52
 */
public class LockCloudInterceptor implements MethodInterceptor {

    private final LockTemplate lockTemplate;

    public LockCloudInterceptor(LockTemplate lockTemplate){
        this.lockTemplate = lockTemplate;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        LockCloud lockCloud = null;
        LockCloudEntity lockCloudEntity = null;

        try {
            lockCloud = invocation.getMethod().getAnnotation(LockCloud.class);
            lockCloudEntity = lockTemplate.lockCloud(invocation,lockCloud);
            if(lockCloudEntity != null){
                return invocation.proceed();
            }
            return null;
        }finally {
            lockTemplate.releaseLockCloud(lockCloudEntity,lockCloud);
        }
    }
}
