package com.dhm.common.redis;

/**
 * Redis客户端
 * @author EX-DUANHONGMEI001
 *
 */
public interface RedisClient {

    /**
     * SERVICE_ID <br>
     */
    String SERVICE_ID = "redis.redisClient";

    /**
     *  模板方法，dao调用此方法并传入回调对象即可
     * @param action
     * @return
     */
    public <T> T excute(RedisCallback<T> action);

}
