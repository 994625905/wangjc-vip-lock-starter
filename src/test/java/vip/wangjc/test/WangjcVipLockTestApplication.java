package vip.wangjc.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import vip.wangjc.test.entity.User;
import vip.wangjc.test.service.LockSingleService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author wangjc
 * @title: WangjcVipLockTestApplication
 * @projectName wangjc-vip-lock-starter
 * @date 2020/12/16 - 17:22
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = WangjcVipLockTestApplication.class)
@SpringBootApplication
public class WangjcVipLockTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(WangjcVipLockTestApplication.class, args);
    }


    @Autowired
    private LockSingleService lockSingleService;

    @Test
    public void singleWrite(){
        ExecutorService threadPool = Executors.newFixedThreadPool(10);
        Runnable task = new Runnable() {
            @Override
            public void run() {
                lockSingleService.write("wangjc");
            }
        };
        for(int i=0;i<20;i++){
            threadPool.submit(task);
        }
    }

    @Test
    public void singleFair(){
        ExecutorService threadPool = Executors.newFixedThreadPool(10);
        Runnable task = new Runnable() {
            @Override
            public void run() {
                lockSingleService.fair(new User());
            }
        };
        for(int i=0;i<20;i++){
            threadPool.submit(task);
        }
    }

}
