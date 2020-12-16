package vip.wangjc.lock.exception;

/**
 * 失败策略的异常类
 * @author wangjc
 * @title: LockFailureException
 * @projectName wangjc-vip
 * @date 2020/12/12 - 15:34
 */
public class LockFailureException extends LockException{

    public LockFailureException(String message){
        super(message);
    }
}
