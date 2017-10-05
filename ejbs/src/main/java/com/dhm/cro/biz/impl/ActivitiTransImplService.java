package com.dhm.cro.biz.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.dhm.cro.biz.ActivitiTransService;
import com.dhm.dao.GeneralDAO;
/**
 * spring声明式事务操作
 * 
 * 1. 不开启事务（没有@Transactional）
 *    直到失败操作之前的所有成功操作会计入数据库(包括一个batchInsert部分成功的List)
 * 
 * 2. 开启事务（@Transactional）不catch异常
 *    如果其中一个数据库操作失败(抛出了RuntimeException)，则全部不成功，会回滚
 * 
 * 3. 开启事务（@Transactional）catch异常不抛出
 *    如果catch异常不抛出，则直到失败操作之前的所有成功操作会计入数据库(包括一个batchInsert部分成功的List)
 *    事务回滚默认只会响应RuntimeException抛出后
 * 
 * 4. 开启事务（@Transactional）部分catch异常不抛出
 *    如果部分catch异常不抛出，只有部分失败操作不计入数据库,失败之前和之后的成功操作都会计入数据库(包括一个batchInsert部分成功的List)
 *    事务回滚默认只会响应RuntimeException抛出后
 * 
 * 5. 开启事务（@Transactional）catch异常不抛出, TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
 *    如果其中一个数据库操作失败(可抛出任何异常)，则全部不成功，会回滚。
 *    TransactionAspectSupport.currentTransactionStatus()获取当前事务的status
 *    TransactionAspectSupport为底层TransactionInterceptor委托的事务织入类
 *
 * 6. 开启事务（@Transactional）catch异常后抛出Exception
 *    事务回滚默认只会响应RuntimeException抛出后，所以直到失败操作之前的所有成功操作会计入数据库(包括一个batchInsert部分成功的List)
 * 
 * 7. 开启事务（@Transactional）catch异常后抛出Exception, 显式回滚Exception
 *    改变事务回滚响应Exception抛出后，所以会回滚
 * 
 * @author EX-DUANHONGMEI001
 *
 */
@Service(ActivitiTransService.SERVICE_ID)
public class ActivitiTransImplService implements ActivitiTransService {
	
	@Resource(name=GeneralDAO.SERVICE_ID)
	private GeneralDAO generalDAO;

	@SuppressWarnings("rawtypes")
	@Override
	public List<Map<String, Object>> queryActHiTaskInst(Map param) {
		return generalDAO.queryForListMap("queryActHiTaskInst.sql", param);
	}
	
	/**
	 * 1. 不开启事务（没有@Transactional）
	 *    直到失败操作之前的所有成功操作会计入数据库(包括一个batchInsert部分成功的List)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void saveActHiDetailNoTrans(Map param) {
	    if (param.get("actHiProcInst")!=null){
	    	generalDAO.insert("saveActHiProcInst.sql", param.get("actHiProcInst"));
	    }
	    
		if (param.get("actHiActInstList")!=null){
			generalDAO.batchInsert("saveActHiActInst.sql", (List<Map<String, Object>>)param.get("actHiActInstList"));
		}
		
		if (param.get("actHiTaskInstList")!=null){
			generalDAO.batchInsert("saveActHiTaskInst.sql", (List<Map<String, Object>>)param.get("actHiTaskInstList"));
		}
	}

	/**
	 * 2. 开启事务（@Transactional）不catch异常
	 *    如果其中一个数据库操作失败(抛出了RuntimeException)，则全部不成功，会回滚
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	@Transactional
	public void saveActHiDetailInTrans(Map param) {
		if (param.get("actHiProcInst")!=null){
			generalDAO.insert("saveActHiProcInst.sql", param.get("actHiProcInst"));
		}
		
		if (param.get("actHiActInstList")!=null){
			generalDAO.batchInsert("saveActHiActInst.sql", (List<Map<String, Object>>)param.get("actHiActInstList"));
		}
		
		if (param.get("actHiTaskInstList")!=null){
			generalDAO.batchInsert("saveActHiTaskInst.sql", (List<Map<String, Object>>)param.get("actHiTaskInstList"));
		}
	}
	
	/**
	 * 3. 开启事务（@Transactional）catch异常不抛出
	 *    如果catch异常不抛出，则直到失败操作之前的所有成功操作会计入数据库(包括一个batchInsert部分成功的List)
	 *    事务回滚默认只会响应RuntimeException抛出后
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	@Transactional
	public void saveActHiDetailInTransCatchException1(Map param) {
		try {
			if (param.get("actHiProcInst")!=null){
				generalDAO.insert("saveActHiProcInst.sql", param.get("actHiProcInst"));
			}
			
			if (param.get("actHiActInstList")!=null){
				generalDAO.batchInsert("saveActHiActInst.sql", (List<Map<String, Object>>)param.get("actHiActInstList"));
			}
			
			if (param.get("actHiTaskInstList")!=null){
				generalDAO.batchInsert("saveActHiTaskInst.sql", (List<Map<String, Object>>)param.get("actHiTaskInstList"));
			}
		} catch (Exception e) {
			System.out.println("catch异常了，但是没有抛出");
		}
	}
	
	/**
	 * 4. 开启事务（@Transactional）部分catch异常不抛出
	 *    如果部分catch异常不抛出，只有部分失败操作不计入数据库,失败之前和之后的成功操作都会计入数据库(包括一个batchInsert部分成功的List)
	 *    事务回滚默认只会响应RuntimeException抛出后
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	@Transactional
	public void saveActHiDetailInTransCatchException2(Map param) {
		if (param.get("actHiProcInst")!=null){
			generalDAO.insert("saveActHiProcInst.sql", param.get("actHiProcInst"));
		}
		
		try {
			if (param.get("actHiActInstList")!=null){
				generalDAO.batchInsert("saveActHiActInst.sql", (List<Map<String, Object>>)param.get("actHiActInstList"));
			}
		} catch (Exception e) {
			System.out.println("部分操作catch异常了，但是没有抛出");
		}
		
		if (param.get("actHiTaskInstList")!=null){
			generalDAO.batchInsert("saveActHiTaskInst.sql", (List<Map<String, Object>>)param.get("actHiTaskInstList"));
		}
	}
	
	/**
	 * 5. 开启事务（@Transactional）catch异常不抛出, TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
     *    如果其中一个数据库操作失败(可抛出任何异常)，则全部不成功，会回滚。
     *    TransactionAspectSupport.currentTransactionStatus()获取当前事务的status
     *    TransactionAspectSupport为底层TransactionInterceptor委托的事务织入类
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	@Transactional
	public void saveActHiDetailInTransCatchException3Rollback(Map param) {
		try {
			if (param.get("actHiProcInst")!=null){
				generalDAO.insert("saveActHiProcInst.sql", param.get("actHiProcInst"));
			}
			
			if (param.get("actHiActInstList")!=null){
				generalDAO.batchInsert("saveActHiActInst.sql", (List<Map<String, Object>>)param.get("actHiActInstList"));
			}
			
			if (param.get("actHiTaskInstList")!=null){
				generalDAO.batchInsert("saveActHiTaskInst.sql", (List<Map<String, Object>>)param.get("actHiTaskInstList"));
			}
		} catch (Exception e) {
			System.out.println("catch异常了，但是没有抛出, TransactionAspectSupport.currentTransactionStatus().setRollbackOnly()");
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		}
	}
	
	/**
	 * 6. 开启事务（@Transactional）catch异常后抛出Exception
	 *    事务回滚默认只会响应RuntimeException抛出后，所以直到失败操作之前的所有成功操作会计入数据库(包括一个batchInsert部分成功的List)
	 * 
	 * @throws Exception 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	@Transactional
	public void saveActHiDetailInTransException1(Map param) throws Exception {
		try {
			if (param.get("actHiProcInst")!=null){
				generalDAO.insert("saveActHiProcInst.sql", param.get("actHiProcInst"));
			}
			
			if (param.get("actHiActInstList")!=null){
				generalDAO.batchInsert("saveActHiActInst.sql", (List<Map<String, Object>>)param.get("actHiActInstList"));
			}
			
			if (param.get("actHiTaskInstList")!=null){
				generalDAO.batchInsert("saveActHiTaskInst.sql", (List<Map<String, Object>>)param.get("actHiTaskInstList"));
			}
		} catch (Exception e) {
			System.out.println("catch异常了，然后又抛出了异常Exception");
			throw new Exception();
		}
	}
	
	/**
	 * 7. 开启事务（@Transactional）catch异常后抛出Exception, 显式回滚Exception
	 *    改变事务回滚响应Exception抛出后，所以会回滚
	 * 
	 * @throws Exception 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	@Transactional(rollbackFor={Exception.class})
	public void saveActHiDetailInTransException2(Map param) throws Exception {
		try {
			if (param.get("actHiProcInst")!=null){
				generalDAO.insert("saveActHiProcInst.sql", param.get("actHiProcInst"));
			}
			
			if (param.get("actHiActInstList")!=null){
				generalDAO.batchInsert("saveActHiActInst.sql", (List<Map<String, Object>>)param.get("actHiActInstList"));
			}
			
			if (param.get("actHiTaskInstList")!=null){
				generalDAO.batchInsert("saveActHiTaskInst.sql", (List<Map<String, Object>>)param.get("actHiTaskInstList"));
			}
		} catch (Exception e) {
			System.out.println("catch异常了，然后又抛出了异常Exception, 显式回滚Exception");
			throw new Exception();
		}
	}
}
