package com.dhm.redis.biz;


public interface RedisService {
	
	/**
     * SERVICE_ID <br>
     */
    String SERVICE_ID = "redis.biz.redisService";
    
	/**
	 * 向REDIS缓存 放入数据
	 * 
	 * @param key
	 * @param map
	 * @return 返回 ：0 put成功 返回 ：1 put 失败
	 */
	public String putObJToRedis(String key, Object obj);

	/**
	 * 从REDIS缓存中获取信息
	 * 
	 * @return
	 * @throws Exception
	 */
	public Object getObJForRedis(String key) throws Exception;


	/**
	 * 清除REDIS 缓存中的数据
	 * @param requestType
	 * @throws Exception
	 */
	public void removeDataFromRedis(String key) throws Exception;
	
	/**
	 * 查看redis是否有该键值的数据
	 * @param requestType
	 * @return
	 * @throws Exception
	 */
	public boolean isExisDataFromRedis(String key) throws Exception;
	
	/**
	 * 设置相应的 KEY的失效时间
	 * @param key
	 * @param seconds
	 */
	public void setKeyDieFoRedis(String key,int seconds) throws Exception;
	
}
