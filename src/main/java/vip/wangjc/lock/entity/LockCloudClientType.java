package vip.wangjc.lock.entity;

/**
 * 分布式锁的客户端类型
 * @author wangjc
 * @title: LockClientTye
 * @projectName wangjc-vip
 * @date 2020/12/12 - 14:26
 */
public enum LockCloudClientType {

    /**
     * spring提供的Redis template
     */
    REDIS_TEMPLATE(1,"spring redis template"),

    /**
     * 官方提供的redisson框架支持
     */
    REDISSON(2,"Redisson - Redis Java client");

    private Integer type;
    private String msg;

    private LockCloudClientType(Integer type, String msg) {
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
