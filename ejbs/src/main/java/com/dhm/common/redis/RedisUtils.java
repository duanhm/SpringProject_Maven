package com.dhm.common.redis;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import com.dhm.common.util.SpringContextUtil;

/**
 * redis工具类
 * 
 */
public class RedisUtils {

	private static JedisPool jedisPool = null;

	private static Log logger = LogFactory.getLog(RedisUtils.class);

	/**
	 * 获取redis对象
	 * 
	 * @return
	 * @throws Exception
	 */
	public static JedisPool getRedisPoolObject() {

			jedisPool = initData(jedisPool);
			return jedisPool;

	}

	/**
	 * 初始化数据
	 * 
	 * @param flag
	 * @param jedisPool
	 */
	@SuppressWarnings("rawtypes")
	public static JedisPool initData(JedisPool jedisPool) {
		if (jedisPool == null) {
			try {
				Map map = (Map)SpringContextUtil.getBean("redisConfig");

				String redisHost = (String) map.get("redisHost");
				String redisPort = (String) map.get("redisPort");
				String redisPwd = (String) map.get("redisPwd");
				String maxActive = (String) map.get("redisMaxActive");
				String maxIdle = (String) map.get("redisMaxIdle");
				String maxWait = (String) map.get("redisMaxWait");
				String testOnBorrow = (String) map.get("redisTestOnBorrow");
				String testOnReturn = (String) map.get("redisTestOnReturn");

				if (StringUtils.isEmpty(redisHost)
					|| StringUtils.isEmpty(redisPort)
					|| StringUtils.isEmpty(redisPwd)
					|| StringUtils.isEmpty(maxActive)
					|| StringUtils.isEmpty(maxIdle)
					|| StringUtils.isEmpty(maxWait)
					|| StringUtils.isEmpty(testOnBorrow)
					|| StringUtils.isEmpty(testOnReturn)) {
					throw new Exception("redis配置项不能为空");
				}

				logger.info("获取的配置参数.....");
				logger.info(" redisHost:" + redisHost + " redisPort:"
						+ redisPort + " redisPwd:" + redisPwd + " maxActive:"
						+ maxActive + " maxIdle:" + maxIdle + " maxWait:"
						+ maxWait + " testOnBorrow:" + testOnBorrow
						+ " testOnReturn:" + testOnReturn);

				// 创建jedis池配置实例
				JedisPoolConfig poolConfig = new JedisPoolConfig();
				poolConfig.setMaxActive(Integer.parseInt(maxActive));
				poolConfig.setMaxIdle(Integer.parseInt(maxIdle));
				poolConfig.setMaxWait(Long.parseLong(maxWait));
				poolConfig.setTestOnBorrow(Boolean.parseBoolean(testOnBorrow));
				poolConfig.setTestOnReturn(Boolean.parseBoolean(testOnReturn));

				// 根据配置实例化jedis池
				jedisPool = new JedisPool(poolConfig, redisHost,Integer.parseInt(redisPort), 1000, redisPwd);
			} catch (Exception e) {
				logger.info("获取redis pool对象异常：", e);
			}
		}
		return jedisPool;
	}

	/**
	 * 释放jedis
	 * 
	 * @param pool
	 * @param redis
	 */
	public static void returnResource(JedisPool pool, Jedis jedis) {
		if (jedis != null) {
			try {
				pool.returnResource(jedis);
			} catch (Exception e) {
				logger.info("释放redis pool对象异常：", e);
			}
		}
	}

	/**
	 * 根据jedis对象判断是否存在: true:存在 false:不存在
	 * 
	 * @param jedis
	 * @param key
	 * @return
	 */
	public static boolean isExists(Jedis jedis, String key) {
		return jedis.exists(key);
	}

	/**
	 * 设置jedis值
	 * 
	 * @param jedis
	 * @param key
	 */
	public static void setRedis(Jedis jedis, String key, String value) {
		jedis.set(key, value);
	}

	/**
	 * 设置 redis 的失效时间
	 * @param jedis
	 * @param key
	 * @param value
	 */
	public static void setRedisKeyDie(Jedis jedis, String key, int seconds){
		jedis.expire(key, seconds);
	}
	
	/**
	 * 关闭资源
	 * @param pool
	 * @param jedis
	 */
	public static void close(JedisPool pool, Jedis jedis) {
		if (pool != null && jedis != null) {
			returnResource(pool, jedis);
		}
	}
	
	/**
	 * 清除jedis值
	 * 
	 * @param jedis
	 * @param key
	 */
	public static void removeRedis(Jedis jedis, String key) {
		jedis.del(key);
	}

	/**
	 * 设置jedis值
	 * 
	 * @param jedis
	 * @param key
	 */
	public static void setRedis(Jedis jedis, String key, Object value) {

		jedis.set(key.getBytes(), ObjectsTranscoder.serialize(value));

	}

	/**
	 * 设置jedis值
	 * 
	 * @param jedis
	 * @param key
	 */
	public static Object getRedis(Jedis jedis, String key) {

		byte[] in = jedis.get(key.getBytes());
		Object obj = ObjectsTranscoder.deserialize(in);
		return obj;
	}

	public static void close(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (Exception e) {
				logger.info("Unable to close %s"+ e);
			}
		}
	}


	static class ObjectsTranscoder {
		public static byte[] serialize(Object value) {
			if (value == null) {
				throw new NullPointerException("Can't serialize null");
			}
			byte[] rv = null;
			ByteArrayOutputStream bos = null;
			ObjectOutputStream os = null;
			try {
				bos = new ByteArrayOutputStream();
				os = new ObjectOutputStream(bos);
				os.writeObject(value);
				os.close();
				bos.close();
				rv = bos.toByteArray();
			} catch (IOException e) {
				throw new IllegalArgumentException("Non-serializable object", e);
			} finally {
				close(os);
				close(bos);
			}
			return rv;
		}

		public static Object deserialize(byte[] in) {
			Object rv = null;
			ByteArrayInputStream bis = null;
			ObjectInputStream is = null;
			try {
				if (in != null) {
					bis = new ByteArrayInputStream(in);
					is = new ObjectInputStream(bis);
					rv = is.readObject();
					is.close();
					bis.close();
				}
			} catch (IOException e) {
				logger.info("Caught IOException decoding %d bytes of data"+e);
			} catch (ClassNotFoundException e) {
				logger.info("Caught CNFE decoding %d bytes of data"+e);
			} 
			finally {
				close(is);
				close(bis);
			}
			return rv;
		}
	}
}
