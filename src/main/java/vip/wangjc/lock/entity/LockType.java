package vip.wangjc.lock.entity;

/**
 * @author wangjc
 * @title: LockType
 * @projectName wangjc-vip
 * @date 2020/12/12 - 14:15
 */
public enum LockType {

    /**
     * 读写锁
     */
    READ(1,"read lock"),

    /**
     * 写锁
     */
    WRITE(2,"write lock"),

    /**
     * 公平锁
     */
    FAIR(3,"fair lock"),

    /**
     * 可重入锁
     */
    REENTRANT(4,"reentrant lock");

    private Integer type;
    private String msg;

    private LockType(Integer type, String msg) {
        this.type = type;
        this.msg = msg;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
