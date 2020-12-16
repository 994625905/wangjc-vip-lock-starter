package vip.wangjc.lock.failureStrategy.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vip.wangjc.lock.entity.LockEntity;
import vip.wangjc.lock.exception.LockFailureException;
import vip.wangjc.lock.failureStrategy.service.ILockFailureStrategyService;

/**
 * 默认的失败策略
 * @author wangjc
 * @title: DefaultLockFailureStrategyServiceImpl
 * @projectName wangjc-vip
 * @date 2020/12/12 - 15:26
 */
public class DefaultLockFailureStrategyServiceImpl implements ILockFailureStrategyService {

    private static final Logger logger = LoggerFactory.getLogger(DefaultLockFailureStrategyServiceImpl.class);

    @Override
    public void lockFailure(Long timeout, Integer acquireCount) {
        logger.debug("acquire lock fail,timeout:[{}] acquireCount:[{}]",timeout,acquireCount);
        throw new LockFailureException("acquire lock failed,please retry it.");
    }

    @Override
    public void releaseFailure(LockEntity lockEntity) {
        logger.debug("release lock fail，key[{}]，value[{}]",lockEntity.getKey(),lockEntity.getValue());
        throw new LockFailureException("release lock failed,please check it.");
    }
}
