package vip.wangjc.lock.aop;

import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import vip.wangjc.lock.annotation.LockSingle;

/**
 * 普通锁的aop通知
 * @author wangjc
 * @title: LockSingleAnnotationAdvisor
 * @projectName wangjc-vip
 * @date 2020/12/13 - 15:55
 */
public class LockSingleAnnotationAdvisor extends AbstractPointcutAdvisor implements BeanFactoryAware {

    private final Advice advice;

    private final Pointcut pointcut = AnnotationMatchingPointcut.forMethodAnnotation(LockSingle.class);

    public LockSingleAnnotationAdvisor(LockSingleInterceptor interceptor, int order){
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
