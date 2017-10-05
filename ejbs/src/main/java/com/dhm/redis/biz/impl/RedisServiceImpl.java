package com.dhm.redis.biz.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.dhm.common.redis.RedisUtils;
import com.dhm.redis.biz.RedisService;

@Service(RedisService.SERVICE_ID)
public class RedisServiceImpl implements RedisService {

	protected static Log logger = LogFactory.getLog(RedisServiceImpl.class);

	@Override
	public String putObJToRedis(String key, Object obj) {
		String resultCode = "0";
		JedisPool pool = null;
		Jedis jedis = null;
		try {
			pool = RedisUtils.getRedisPoolObject();
			jedis = pool.getResource();

			RedisUtils.setRedis(jedis, key, obj);
		} catch (Exception e) {
			logger.error("RedisPojoService.putObJToRedis 失败" + e);
			resultCode = "1";
		} finally {
			RedisUtils.close(pool, jedis);
		}
		return resultCode;
	}

	@Override
	public Object getObJForRedis(String key) throws Exception {
		Object objInfo = new Object();

		JedisPool pool = null;
		Jedis jedis = null;

		try {
			pool = RedisUtils.getRedisPoolObject();
			jedis = pool.getResource();

			if (RedisUtils.isExists(jedis, key)) {
				objInfo =  RedisUtils.getRedis(jedis, key);
			}
		} catch (Exception e) {
			logger.error("RedisPojoService.getObJForRedis  失败" + e);
			throw new Exception(e);
		} finally {
			RedisUtils.close(pool, jedis);
		}
		return objInfo;
	}

	@Override
	public void removeDataFromRedis(String key) throws Exception {
		JedisPool pool = null;
		Jedis jedis = null;

		try {
			pool = RedisUtils.getRedisPoolObject();
			jedis = pool.getResource();

			if (RedisUtils.isExists(jedis, key)) {
				RedisUtils.removeRedis(jedis, key);
			}

		} catch (Exception e) {
			logger.error("RedisPojoService.removeDataFromRedis  失败" + e);
			throw new Exception(e);
		} finally {
			RedisUtils.close(pool, jedis);
		}
	}

	@Override
	public boolean isExisDataFromRedis(String key) throws Exception {
		boolean flag = false;
		JedisPool pool = null;
		Jedis jedis = null;

		try {
			pool = RedisUtils.getRedisPoolObject();
			jedis = pool.getResource();

			if (RedisUtils.isExists(jedis, key)) {
				flag = true;
			}

		} catch (Exception e) {
			logger.error("RedisPojoService.isExisDataFromRedis  失败"+e);
			throw new Exception(e);
		} finally {
			RedisUtils.close(pool, jedis);
		}
		return flag;
	}

	@Override
	public void setKeyDieFoRedis(String key, int seconds) throws Exception {
		JedisPool pool = null;
		Jedis jedis = null;

		try {
			pool = RedisUtils.getRedisPoolObject();
			jedis = pool.getResource();
			RedisUtils.setRedisKeyDie(jedis, key, seconds);
			
		} catch (Exception e) {
			logger.error("RedisPojoService.setKeyDieFoRedis  失败"+e);
			throw new Exception(e);
		} finally {
			RedisUtils.close(pool, jedis);
		}
	}
}
