package vip.wangjc.lock.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vip.wangjc.lock.LockTemplate;
import vip.wangjc.lock.annotation.LockSingle;
import vip.wangjc.lock.entity.LockEntity;

/**
 * 普通锁的aop切面处理器
 * @author wangjc
 * @title: LockInterceptor
 * @projectName wangjc-vip
 * @date 2020/12/13 - 15:35
 */
public class LockSingleInterceptor implements MethodInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(LockSingleInterceptor.class);
    private final LockTemplate lockTemplate;

    public LockSingleInterceptor(LockTemplate lockTemplate){
        this.lockTemplate = lockTemplate;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        LockEntity lockEntity = null;
        LockSingle lockSingle = null;
        try {
            lockSingle = invocation.getMethod().getAnnotation(LockSingle.class);
            lockEntity = lockTemplate.lockSingle(invocation,lockSingle);
            if(lockEntity != null){
                return invocation.proceed();
            }
            return null;
        }catch (Exception e){
            logger.error("LockSingleInterceptor invoke error reason[{}]",e.getMessage());
            throw e;
        }finally {
            lockTemplate.releaseLockSingle(lockEntity,lockSingle);
        }
    }
}
