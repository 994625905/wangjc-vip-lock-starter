package vip.wangjc.lock.executor.pool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 单节点锁的锁存放池，
 * @author wangjc
 * @title: LockSinglePool
 * @projectName wangjc-vip
 * @date 2020/12/14 - 11:29
 */
public class LockSinglePool {

    private static final Logger logger = LoggerFactory.getLogger(LockSinglePool.class);

    /**
     * 下文get方法使用synchronized的话，此处是可以把ConcurrentHashMap替换成HashMap的，
     */
    private static Map<String, Lock> lockPool = new ConcurrentHashMap<>();

    /**
     * 获取锁实例
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    public synchronized static <T extends Lock> T getLock(String key,Class<T> clazz){
        if(lockPool.containsKey(key)){
            return (T) lockPool.get(key);
        }
        try {
            T lock = clazz.newInstance();
            lockPool.put(key,lock);
            return lock;
        } catch (InstantiationException | IllegalAccessException e) {
            logger.error("获取锁实例失败，key：[{}]",key);
            return null;
        }
    }

    /**
     * 获取读锁实例，无法通过反射newInstance实例化
     * @param key
     * @return
     */
    public synchronized static ReentrantReadWriteLock.ReadLock getReadLock(String key){
        if(lockPool.containsKey(key)){
            return (ReentrantReadWriteLock.ReadLock) lockPool.get(key);
        }
        ReentrantReadWriteLock.ReadLock readLock = new ReentrantReadWriteLock().readLock();
        lockPool.put(key,readLock);
        return readLock;
    }

    /**
     * 获取写锁实例，无法通过反射newInstance实例化
     * @param key
     * @return
     */
    public synchronized static ReentrantReadWriteLock.WriteLock getWriteLock(String key){
        if(lockPool.containsKey(key)){
            return (ReentrantReadWriteLock.WriteLock) lockPool.get(key);
        }
        ReentrantReadWriteLock.WriteLock writeLock = new ReentrantReadWriteLock().writeLock();
        lockPool.put(key,writeLock);
        return writeLock;
    }

    /**
     * 获取公平锁
     * 可通过构造方法参数，来指定是否为公平锁，部分源码注释如下：
     * Fair version of tryAcquire.  Don't grant access unless recursive call or no waiters or is first.
     * @param key
     * @return
     */
    public synchronized static ReentrantLock getFairLock(String key){
        if(lockPool.containsKey(key)){
            return (ReentrantLock) lockPool.get(key);
        }
        ReentrantLock fairLock = new ReentrantLock(true);
        lockPool.put(key,fairLock);
        return fairLock;
    }

}
