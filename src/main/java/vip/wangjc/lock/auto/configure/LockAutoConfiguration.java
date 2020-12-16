package vip.wangjc.lock.auto.configure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import vip.wangjc.lock.LockTemplate;
import vip.wangjc.lock.aop.LockCloudAnnotationAdvisor;
import vip.wangjc.lock.aop.LockCloudInterceptor;
import vip.wangjc.lock.aop.LockSingleAnnotationAdvisor;
import vip.wangjc.lock.aop.LockSingleInterceptor;
import vip.wangjc.lock.executor.LockExecutorFactory;

/**
 * 锁的自动配置器
 * @author wangjc
 * @title: LockAutoConfiguration
 * @projectName wangjc-vip
 * @date 2020/12/13 - 16:26
 */
@Configuration
public class LockAutoConfiguration {

    /** @ConditionalOnMissingBean注解 备用选项，如果当前容器中已经存在该项bean，则不注入备用选项，如果不存在就注入  */

    /**
     * 注入锁执行器的构建工厂
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public LockExecutorFactory lockExecutorFactory(){
        return new LockExecutorFactory();
    }

    /**
     * 注入锁的模板方法
     * @param lockExecutorFactory
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public LockTemplate lockTemplate(LockExecutorFactory lockExecutorFactory){
        return new LockTemplate(lockExecutorFactory);
    }

    /**
     * 注入普通锁的aop切面处理器
     * @param lockTemplate
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public LockSingleInterceptor lockSingleInterceptor(LockTemplate lockTemplate){
        return new LockSingleInterceptor(lockTemplate);
    }

    /**
     * 注入分布式锁的aop切面处理器
     * @param lockTemplate
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public LockCloudInterceptor lockCloudInterceptor(LockTemplate lockTemplate){
        return new LockCloudInterceptor(lockTemplate);
    }

    /**
     * 注入普通锁的aop通知
     * @param lockSingleInterceptor
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public LockSingleAnnotationAdvisor lockSingleAnnotationAdvisor(LockSingleInterceptor lockSingleInterceptor){
        return new LockSingleAnnotationAdvisor(lockSingleInterceptor, Ordered.HIGHEST_PRECEDENCE);
    }

    /**
     * 注入分布式锁的aop通知
     * @param lockCloudInterceptor
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public LockCloudAnnotationAdvisor lockCloudAnnotationAdvisor(LockCloudInterceptor lockCloudInterceptor){
        return new LockCloudAnnotationAdvisor(lockCloudInterceptor,Ordered.HIGHEST_PRECEDENCE+1);
    }

}
