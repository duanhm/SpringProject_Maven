package com.dhm.common.redis;

import redis.clients.jedis.Jedis;

/**
 * Redis回调接口，*RedisDAO实现类需创建本接口的匿名callback对象
 * @author EX-DUANHONGMEI001
 *
 * @param <T>
 */
public interface RedisCallback<T> {

	/**
	 * 具体回调方法
	 * @param jedis
	 * @return
	 */
    public T doInRedis(Jedis jedis);

}
