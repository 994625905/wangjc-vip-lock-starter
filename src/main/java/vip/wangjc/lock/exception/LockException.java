package vip.wangjc.lock.exception;

/**
 * 自定义锁的异常
 * @author wangjc
 * @title: LockException
 * @projectName wangjc-vip
 * @date 2020/12/12 - 15:31
 */
public class LockException extends RuntimeException{

    public LockException(){
        super();
    }

    public LockException(String message){
        super(message);
    }

}
