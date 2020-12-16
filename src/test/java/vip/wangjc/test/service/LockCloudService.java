package vip.wangjc.test.service;

import org.springframework.stereotype.Service;
import vip.wangjc.lock.annotation.LockCloud;
import vip.wangjc.lock.entity.LockType;
import vip.wangjc.test.entity.User;

/**
 * @author wangjc
 * @title: LockSingleService
 * @projectName wangjc-vip
 * @date 2020/12/13 - 17:02
 */
@Service
public class LockCloudService {

    private int counter = 1;

    @LockCloud(type = LockType.READ)
    public void read(){
        System.out.println("分布式锁：读锁，当前线程："+Thread.currentThread().getName()+"，counter："+ counter++);
    }

    @LockCloud(type = LockType.WRITE,keys = "#key")
    public void write(String key){
        System.out.println("分布式锁：写锁，当前线程："+Thread.currentThread().getName()+"，counter："+ counter++);
    }

    @LockCloud(type = LockType.FAIR,keys = "#user.name")
    public void fair(User user){
        System.out.println("分布式锁：公平锁，当前线程："+Thread.currentThread().getName()+"，counter："+ counter++);
    }

    /**
     * 设置获取锁超时时间 和过期失效时间
     * @param user
     */
    @LockCloud(keys = {"#user.name","#user.id"},acquireTimeout = 4000,expire = 4000)
    public void reentrant(User user){
        System.out.println("分布式锁：可重入锁，当前线程："+Thread.currentThread().getName()+"，counter："+ counter++);
        try {
            /** 模拟占用 */
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
