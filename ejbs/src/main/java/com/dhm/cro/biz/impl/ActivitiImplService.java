package com.dhm.cro.biz.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import com.dhm.cro.biz.ActivitiService;
import com.dhm.dao.GeneralDAO;
/**
 * spring编程式事务操作
 * 1. PlatformTransactionManager
 * 	      DataSourceTransactionManager：适用于JDBC和iBatis中数据持久化操作。
 *	      HibernateTransactionManager：适用于Hibernate中数据持久化操作。
 *	      JpaTransactionManager：适用于JPA中数据持久化操作。
 *	          其他还有JtaTransactionManager 、JdoTransactionManager、JmsTransactionManager等等。
 *    TransactionDefinition (事务隔离级别，传播属性，超时，只读)
 *    TransactionStatus
 * 
 * 2. TransactionTemplate(推荐)
 *    transactionTemplate.execute(TransactionCallback action)通过回调实现
 *    	  TransactionCallback的doInTransaction(TransactionStatus status)方法，有返回结果
 *    	  TransactionCallbackWithoutResult(TransactionCallback子类)的doInTransactionWithoutResult(TransactionStatus status)方法，没有返回结果
 *    可以显示的使用status.setRollbackOnly()方法将事务标识为回滚
 * 
 * @author EX-DUANHONGMEI001
 *
 */
@Service(ActivitiService.SERVICE_ID)
public class ActivitiImplService implements ActivitiService {
	
	@Resource(name=GeneralDAO.SERVICE_ID)
	private GeneralDAO generalDAO;
	
	@Resource(name = "txManager")
	private PlatformTransactionManager transactionManager;
	
	@Resource(name = "txTemplate")
	private TransactionTemplate transactionTemplate;

	@SuppressWarnings("rawtypes")
	@Override
	public void saveActHiDetail1(Map param) {
		TransactionDefinition definition=new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		TransactionStatus status=transactionManager.getTransaction(definition);
		
		try {
			saveActHiProcInst(param);
			saveActHiActInst(param);
			saveActHiTaskInst(param);
		} catch (Exception e) {
			System.out.println("catch异常了，设置回滚status.setRollbackOnly");
//			status.setRollbackOnly();
		} finally {
			System.out.println("finally");
//			transactionManager.commit(status);
		}
	}
	
	/**
	 * TransactionTemplate
	 * 不能catch异常,除非catch了又抛出RuntimeException类型的异常或者status.setRollbackOnly(),否则不会回滚
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public void saveActHiDetail2(final Map param) {
		//TransactionCallback 有返回结果
		/*transactionTemplate.execute(new TransactionCallback<Void>() {
			@Override
			public Void doInTransaction(TransactionStatus status) {
				try {
					saveActHiProcInst(param);
					saveActHiActInst(param);
					saveActHiTaskInst(param);
				} catch (Exception e) {
					status.setRollbackOnly();
//					throw new RuntimeException();
				}
				
				return null;
			}
		});*/
		
		//TransactionCallbackWithoutResult 没有返回结果
		transactionTemplate.execute(new TransactionCallbackWithoutResult() {
			
			@Override
			protected void doInTransactionWithoutResult(
					TransactionStatus status) {
				try {
					saveActHiProcInst(param);
					saveActHiActInst(param);
					saveActHiTaskInst(param);
				} catch (Exception e) {
					status.setRollbackOnly();
//					throw new RuntimeException();
				}
			}
		});
	}
	
	@SuppressWarnings({ "rawtypes" })
	private void saveActHiProcInst(Map param) {
		generalDAO.insert("saveActHiProcInst.sql", param.get("actHiProcInst"));
	}
	
	@SuppressWarnings({ "rawtypes" })
	private void saveActHiActInst(Map param) {
		generalDAO.batchInsert("saveActHiActInst.sql", (List<Map<String, Object>>)param.get("actHiActInstList"));
		
	}
	
	@SuppressWarnings({ "rawtypes" })
	private void saveActHiTaskInst(Map param) {
		generalDAO.batchInsert("saveActHiTaskInst.sql", (List<Map<String, Object>>)param.get("actHiTaskInstList"));
		
	}
}
