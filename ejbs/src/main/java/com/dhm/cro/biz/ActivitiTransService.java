package com.dhm.cro.biz;

import java.util.List;
import java.util.Map;


public interface ActivitiTransService {
	/**
     * SERVICE_ID <br>
     */
	String SERVICE_ID="cro.biz.activitiTransService";
	
	@SuppressWarnings("rawtypes")
	List<Map<String, Object>> queryActHiTaskInst(Map param);
	
	/**
	 * 没有开启事务
	 * @param param
	 */
	@SuppressWarnings("rawtypes")
	public void saveActHiDetailNoTrans(Map param);
	
	/**
	 * 开启事务，不catch异常
	 * @param param
	 */
	@SuppressWarnings("rawtypes")
	public void saveActHiDetailInTrans(Map param);
	
	/**
	 * 开启事务，catch异常不抛出
	 * @param param
	 */
	@SuppressWarnings("rawtypes")
	public void saveActHiDetailInTransCatchException1(Map param);
	
	/**
	 * 开启事务，部分catch异常不抛出
	 * @param param
	 */
	@SuppressWarnings("rawtypes")
	public void saveActHiDetailInTransCatchException2(Map param);
	
	/**
	 *开启事务，catch异常不抛出并获取当前事务TransactionStatus, 并设置setRollbackOnly
	 * @param param
	 */
	@SuppressWarnings("rawtypes")
	public void saveActHiDetailInTransCatchException3Rollback(Map param);
	
	/**
	 * 开启事务，catch异常后抛出Exception
	 * @param param
	 */
	@SuppressWarnings("rawtypes")
	public void saveActHiDetailInTransException1(Map param)  throws Exception;
	
	/**
	 * 开启事务，catch异常后抛出Exception,显式回滚Exception
	 * @param param
	 */
	@SuppressWarnings("rawtypes")
	public void saveActHiDetailInTransException2(Map param)  throws Exception;
}
