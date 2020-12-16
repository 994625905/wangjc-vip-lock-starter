package vip.wangjc.lock.aop;

import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import vip.wangjc.lock.annotation.LockCloud;

/**
 * 分布式锁的aop通知
 * @author wangjc
 * @title: LockCloudAnnotationAdvisor
 * @projectName wangjc-vip
 * @date 2020/12/13 - 16:01
 */
public class LockCloudAnnotationAdvisor extends AbstractPointcutAdvisor implements BeanFactoryAware {

    private final Advice advice;

    private final Pointcut pointcut = AnnotationMatchingPointcut.forMethodAnnotation(LockCloud.class);

    public LockCloudAnnotationAdvisor(LockCloudInterceptor interceptor, int order){
        this.advice = interceptor;
        setOrder(order);
    }

    @Override
    public Pointcut getPointcut() {
        return this.pointcut;
    }

    @Override
    public Advice getAdvice() {
        return this.advice;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        if(this.advice instanceof BeanFactoryAware){
            ((BeanFactoryAware) this.advice).setBeanFactory(beanFactory);
        }
    }
}
