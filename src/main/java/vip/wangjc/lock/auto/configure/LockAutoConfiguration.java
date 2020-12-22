package vip.wangjc.lock.auto.configure;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import vip.wangjc.lock.LockTemplate;
import vip.wangjc.lock.aop.LockCloudAnnotationAdvisor;
import vip.wangjc.lock.aop.LockCloudInterceptor;
import vip.wangjc.lock.aop.LockSingleAnnotationAdvisor;
import vip.wangjc.lock.aop.LockSingleInterceptor;
import vip.wangjc.lock.executor.LockCloudExecutorFactory;
import vip.wangjc.lock.executor.LockSingleExecutorFactory;

/**
 * 锁的自动配置器
 * @author wangjc
 * @title: LockAutoConfiguration
 * @projectName wangjc-vip
 * @date 2020/12/13 - 16:26
 */
@Configuration
@Order(99)
public class LockAutoConfiguration {

    /**
     * @ConditionalOnMissingBean注解 如果当前容器中已经存在该bean，则不注入，如果不存在就注入
     * @ConditionalOnClass注解 某个class位于类路径上，才会实例化一个Bean，声明类
     */

    /**
     * 注入普通锁执行器的构建工厂
     * @return
     */
    @Bean
    public LockSingleExecutorFactory lockSingleExecutorFactory(){
        return new LockSingleExecutorFactory();
    }

    /**
     * 配置分布式锁执行器，前提必须引入了对应的jar包
     */
    @Configuration
    @ConditionalOnClass({Redisson.class, RedisOperations.class})
    static class LockCloudExecutorFactoryConfigure{
        /**
         * 分布式锁执行器的构建工厂
         * @param redisTemplate
         * @param redissonClient
         * @return
         */
        @Bean
        public LockCloudExecutorFactory lockCloudExecutorFactory(RedisTemplate redisTemplate, RedissonClient redissonClient){
            return new LockCloudExecutorFactory(redisTemplate,redissonClient);
        }
    }

    /**
     * 注入锁的模板方法
     * @return
     */
    @Bean
    public LockTemplate lockTemplate(){
        return new LockTemplate();
    }

    /**
     * 注入普通锁的aop切面处理器
     * @param lockTemplate
     * @return
     */
    @Bean
    @ConditionalOnBean(LockTemplate.class)
    public LockSingleInterceptor lockSingleInterceptor(LockTemplate lockTemplate){
        return new LockSingleInterceptor(lockTemplate);
    }

    /**
     * 注入分布式锁的aop切面处理器
     * @param lockTemplate
     * @return
     */
    @Bean
    @ConditionalOnBean(LockTemplate.class)
    public LockCloudInterceptor lockCloudInterceptor(LockTemplate lockTemplate){
        return new LockCloudInterceptor(lockTemplate);
    }

    /**
     * 注入普通锁的aop通知
     * @param lockSingleInterceptor
     * @return
     */
    @Bean
    @ConditionalOnBean(LockSingleInterceptor.class)
    public LockSingleAnnotationAdvisor lockSingleAnnotationAdvisor(LockSingleInterceptor lockSingleInterceptor){
        return new LockSingleAnnotationAdvisor(lockSingleInterceptor, Ordered.HIGHEST_PRECEDENCE);
    }

    /**
     * 注入分布式锁的aop通知
     * @param lockCloudInterceptor
     * @return
     */
    @Bean
    @ConditionalOnBean(LockCloudInterceptor.class)
    public LockCloudAnnotationAdvisor lockCloudAnnotationAdvisor(LockCloudInterceptor lockCloudInterceptor){
        return new LockCloudAnnotationAdvisor(lockCloudInterceptor,Ordered.HIGHEST_PRECEDENCE+1);
    }

}
