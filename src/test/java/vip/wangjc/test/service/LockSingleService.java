package vip.wangjc.test.service;

import org.springframework.stereotype.Service;
import vip.wangjc.lock.annotation.LockSingle;
import vip.wangjc.lock.entity.LockType;
import vip.wangjc.test.entity.User;

/**
 * @author wangjc
 * @title: LockSingleService
 * @projectName wangjc-vip
 * @date 2020/12/13 - 17:02
 */
@Service
public class LockSingleService {

    private int counter = 1;

    @LockSingle(type = LockType.READ)
    public void read(){
        System.out.println("普通锁：读锁，当前线程："+Thread.currentThread().getName()+"，counter："+ counter++);
    }

    @LockSingle(type = LockType.WRITE,keys = "#key")
    public void write(String key){
        System.out.println("普通锁：写锁，当前线程："+Thread.currentThread().getName()+"，counter："+ counter++);
    }

    @LockSingle(type = LockType.FAIR,keys = "#user.name")
    public void fair(User user){
        System.out.println("普通锁：公平锁，当前线程："+Thread.currentThread().getName()+"，counter："+ counter++);
    }

    @LockSingle(keys = {"#user.name","#user.id"},acquireTimeout = 8000)
    public void reentrant(User user){
        System.out.println("普通锁：可重入锁，当前线程："+Thread.currentThread().getName()+"，counter："+ counter++);
        try {
            /** 模拟占用时间 */
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
