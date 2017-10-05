package com.dhm.schedule.core;

import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.dhm.common.SQLID;
import com.dhm.dao.GeneralDAO;

/**
 * quartz job切面类，主要用于对JOB进行加锁和解锁
 * @author EX-DUANHONGMEI001
 *
 */
@Component
@Aspect
public class LockedJobAspect {

	private static Log logger=LogFactory.getLog(LockedJobAspect.class); 
	
	@Resource(name = GeneralDAO.SERVICE_ID)
	private GeneralDAO generalDAO;

	@Resource(name = "txManager")
	private PlatformTransactionManager transactionManager;
	
	//job运行状态
	private enum jobRunStates{ 

		start("0") , //开始
		end("1") , //正常结束
		error("2") ; //异常

		private final String value;
		
		jobRunStates(String value){
			this.value=value;
		}
		
		public String getValue() {            
			return value;        
		}
		
	}

	/**
	 * Around切面 <br>
	 * 切入点:BaseJob及所有子类的execute()方法
	 * 
	 * @param  proceedingJoinPoint
	 * @throws Exception
	 * @throws UnknownHostException
	 */
	@Around("execution(* com.dhm.schedule.core.BaseJob.execute())")
	public void aroundJobExecute(ProceedingJoinPoint proceedingJoinPoint)
			throws Exception {

		BaseJob job = (BaseJob) proceedingJoinPoint.getTarget();
		
		// 记录JOB运行轨迹
		Map<String, Object> logMap = new HashMap<String, Object>();
		logMap.put("instance_name", job.getMachineId());
		logMap.put("job_name", job.getTargetId());
		logMap.put("state", jobRunStates.start.getValue());

		logger.trace("aroundJobExecute [insName:"+ job.getMachineId() + 
					",job:" + job.getTargetType() + "-" + job.getTargetId() + "]开始加锁---------");
		
		// job配置文件基本参数校验
		if (StringUtils.isEmpty(job.getTargetType())) {
			throw new IllegalArgumentException("非法Job Type配置");
		}
		if (StringUtils.isEmpty(job.getTargetId())) {
			throw new IllegalArgumentException("非法Job Id配置");
		}
		if (null == job.getLockMins() || 0 >= job.getLockMins()) {
			throw new IllegalArgumentException("非法Job Lock Mins配置");
		}

		// JOB开始执行的时间
		Date jobStart = new Date();
		
		//记录日志
		String logId = recordJobExecuteLog(logMap);
		logMap.put("log_id", logId);
		boolean lockSuccess = lock(job);
		if (lockSuccess) {
			logger.trace("aroundJobExecute [insName:"+ job.getMachineId() + 
					",job:" + job.
					getTargetType() + "-" + job.getTargetId() + "]加锁成功---------");
			try {
				proceedingJoinPoint.proceed();
				logMap.put("state", jobRunStates.end.getValue());
				logMap.put("state_desc", job.getTargetId() + "正常结束");
			} catch (Throwable e) {
				logger.error("aroundJobExecute 目标方法执行异常",e);
				
				logMap.put("state", jobRunStates.error.getValue());
				logMap.put("state_desc", "目标方法执行异常");
				logMap.put("error_msg", e.toString());
			} finally {
				// 释放锁
				boolean unlockSuccess = unLock(job);
				if (unlockSuccess) {
					logger.trace("aroundJobExecute [insName:"+ job.getMachineId() + 
							",job:" + job.getTargetType() + "-" + job.getTargetId() + "]释放锁成功---------");
				} else {
					logger.trace("aroundJobExecute [insName:"+ job.getMachineId() + 
							",job:" + job.getTargetType() + "-" + job.getTargetId() + "]释放锁失败---------");
				}
			}
		} else {
			logger.trace("aroundJobExecute [insName:"+ job.getMachineId() + 
					",job:" + job.getTargetType() + "-" + job.getTargetId() + "]加锁失败---------");
			
			logMap.put("state", jobRunStates.error.getValue());
			logMap.put("state_desc", "加锁失败");
		}
		//JOB执行结束时间
		Date jobEnd = new Date();
		//更新日志
		logMap.put("execute_time", String.valueOf(jobEnd.getTime()-jobStart.getTime()));
		updateJobExecuteLog(logMap);
	}

	/**
	 * 对JOB进行加锁
	 * 
	 * @param job
	 * @return
	 */
	private boolean lock(BaseJob job) {
		boolean lockSuccess = false;
		final Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("targetType", job.getTargetType());
		paramsMap.put("targetId", job.getTargetId());
		paramsMap.put("lockMins", job.getLockMins());
		paramsMap.put("lockBy", job.getMachineId());
		TransactionStatus transactionStatus = transactionManager
				.getTransaction(new DefaultTransactionDefinition(
						TransactionDefinition.PROPAGATION_REQUIRES_NEW));
		try {
			int rows = generalDAO.update(SQLID.UPDATE_QUARTZLOCK_SQL,
					paramsMap);
			lockSuccess = rows > 0;
		} catch (Exception e) {
			logger.error("lock, 数据库错误", e);
			transactionStatus.setRollbackOnly();
		} finally {
			transactionManager.commit(transactionStatus);
		}
		return lockSuccess;
	}

	/**
	 * 解锁 JOB
	 * 
	 * @param job
	 * @return
	 */
	private boolean unLock(BaseJob job) {
		boolean unlockSuccess = false;
		final Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("targetId", job.getTargetId());
		paramsMap.put("targetType", job.getTargetType());
		paramsMap.put("lockBy", job.getMachineId());
		TransactionStatus transactionStatus = transactionManager
			       .getTransaction(new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRES_NEW));
		try {
			int row = generalDAO.update(SQLID.UPDATE_QUARTZUNLOCK_SQL,
					paramsMap);
			unlockSuccess = row > 0;
		} catch (Exception e) {
			logger.error("unLock, 数据库错误", e);
			transactionStatus.setRollbackOnly();
		} finally {
			transactionManager.commit(transactionStatus);
		}
		return unlockSuccess;
	}

	/**
	 * 记录JOB运行轨迹
	 * @param logMap
	 * @return
	 */
	private String recordJobExecuteLog(Map<String, Object> logMap) {
		TransactionStatus transactionStatus = transactionManager
				       .getTransaction(new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRES_NEW));
		String logId = null;
		try {
			logId = (String) generalDAO.insert(SQLID.INSERT_RECORD_JOB_EXECUTE_LOG, logMap);
		} catch (Exception e) {
			logger.error("recordJobExecuteLog, 记录JOB运行轨迹异常", e);
			transactionStatus.setRollbackOnly();
		}
		transactionManager.commit(transactionStatus);
		return logId;
	}
	/**
	 * 更新JOB运行轨迹
	 * @param logMap
	 */
	private void updateJobExecuteLog(Map<String, Object> logMap) {
		TransactionStatus transactionStatus = transactionManager
			       .getTransaction(new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRES_NEW));
		try {
			generalDAO.update(SQLID.UPDATE_JOB_EXECUTE_LOG, logMap);
		} catch (Exception e) {
			logger.error("recordJobExecuteLog, 更新JOB运行轨迹异常", e);
			transactionStatus.setRollbackOnly();
		}
		transactionManager.commit(transactionStatus);
	}
}
