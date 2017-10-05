package com.dhm.common.redis;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * RedisClient 实现类
 * @author EX-DUANHONGMEI001
 *
 */
@Component(RedisClient.SERVICE_ID)
public class RedisClientImpl implements RedisClient,InitializingBean {
	private static Log logger=LogFactory.getLog(RedisClientImpl.class);

	@Autowired
	private RedisConfiguration redisConfiguration;
	
    private JedisPool jedisPool = null;
    
    @Override
	public void afterPropertiesSet() throws Exception {
    	try {
        	String redisHost = redisConfiguration.getHost();
            String redisPort = redisConfiguration.getPort();
            String redisPwd = redisConfiguration.getPwd();
            String maxActive = redisConfiguration.getMaxActive();
            String maxIdle = redisConfiguration.getMaxIdle();
            String maxWait = redisConfiguration.getMaxWait();
            String testOnBorrow = redisConfiguration.getTestOnBorrow();
            String testOnReturn = redisConfiguration.getTestOnReturn();
            
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

            // 创建jedis池配置实例
		    JedisPoolConfig poolConfig = new JedisPoolConfig();
			poolConfig.setMaxActive(Integer.parseInt(maxActive));
			poolConfig.setMaxIdle(Integer.parseInt(maxIdle));
			poolConfig.setMaxWait(Long.parseLong(maxWait));
			poolConfig.setTestOnBorrow(Boolean.parseBoolean(testOnBorrow));
			poolConfig.setTestOnReturn(Boolean.parseBoolean(testOnReturn));

            // 根据配置实例化jedis池
            jedisPool = new JedisPool(poolConfig, redisHost, Integer.parseInt(redisPort), 1000, redisPwd);
        }
        catch (Exception e) {
        	logger.error("getRedisPoolObject, 获取redis pool对象异常", e);
        }
	}
    /**
     * 模板方法，dao调用此方法并传入回调对象即可
     *  
     * @author LUOLEI839<br>
     * @taskId <br>
     * @param jedis回调对象
     * @return <br>
     */ 
    @Override
    public <T> T excute(RedisCallback<T> action) {
        Jedis jedis = jedisPool.getResource();
        T result = null;            
        if(jedis != null){
            result =  action.doInRedis(jedis);
            jedisPool.returnResource(jedis);
        }
        return result;
    }

    
    
    public static void main(String[] args) {
        RedisClient redisClient3 = new RedisClientImpl();
        /*redisClient3.excute(new RedisCallback<Object>() {

            @Override
            public Object doInRedis(Jedis jedis) {
                //jedis.flushDB();
                System.out.println(jedis.get("theme:LIUTIEGANG499"));
                //Set<String> set = jedis.keys("*");
//                Set<String> set = jedis.smembers("invalidUrl:LIUTIEGANG499");
//                System.out.println(set.size());
//                for(String str : set){
//                    System.out.println(str);
//                    //System.out.println(jedis.type(str));
//                }
                return null;
            }
        });*/
        
        /*
        Long startTime_flush = System.currentTimeMillis();
        redisClient3.excute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(Jedis jedis) {
                Long startTime_realFlush = System.currentTimeMillis();
                jedis.flushDB();
                Long endTime_realFlush = System.currentTimeMillis();
                System.out.println("realFlushCost:"+(endTime_realFlush -startTime_realFlush));
                return null;
            }
        });
        Long endTime_flush = System.currentTimeMillis();
        System.out.println("outCost:"+(endTime_flush - startTime_flush));
         */
       
        /*
        Long startTime_callback = System.currentTimeMillis();
        RedisClient redisClient = new RedisClientImpl();
        for(int i = 0;i<10000;i++){
            final int tempNum = i;
            redisClient.excute(new RedisCallback<Boolean>() {

                @Override
                public Boolean doInRedis(Jedis jedis) {
                    jedis.set("test1:"+tempNum, String.valueOf(System.currentTimeMillis()));
                    return null;
                }
                
            });
        }
        Long endTime_callBack = System.currentTimeMillis();
        System.out.println(endTime_callBack - startTime_callback);
        
        
        Long startTime_callback2 = System.currentTimeMillis();
        RedisClient redisClient2 = new RedisClientImpl();
        redisClient2.excute(new RedisCallback<Boolean>() {

            @Override
            public Boolean doInRedis(Jedis jedis) {
                for(int i = 0;i<10000;i++){
                    jedis.set("test2:"+i, String.valueOf(System.currentTimeMillis()));
                }
                return null;
            }
            
        });
        Long endTime_callBack2 = System.currentTimeMillis();
        System.out.println(endTime_callBack2 - startTime_callback2);
        */
    }
}
