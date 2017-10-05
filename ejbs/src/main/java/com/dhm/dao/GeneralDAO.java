package com.dhm.dao;

import java.util.List;
import java.util.Map;


public interface GeneralDAO {
	
	/**
	 * SERVICE_ID <br>
	 */
	String SERVICE_ID="dao.generalDAO";
	
	Object insert(String sqlId,Object param);
	
	int update(String sqlId,Object param);
	
	Object queryForObject(String sqlId, Object param);
	
	List<Map<String, Object>> queryForListMap(String sqlId, Object param);
	
	List<?> queryForList(String sqlId, Object param);
	
	<T> Integer batchInsert(String sqlId, List<T> list);
	
	<T> Integer batchUpdate(String sqlId, List<T> list);
}
