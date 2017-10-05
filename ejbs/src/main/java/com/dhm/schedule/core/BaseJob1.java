package com.dhm.schedule.core;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.dhm.common.SQLID;
import com.dhm.dao.GeneralDAO;

public abstract class BaseJob1 implements LockedJob {
	
	private static Log logger=LogFactory.getLog(BaseJob1.class); 
	
	@Resource(name = GeneralDAO.SERVICE_ID)
	private GeneralDAO generalDAO;

	@Resource(name = "txManager")
	private PlatformTransactionManager transactionManager;
	
	/**
     * 服务器识别号 ip <br>
     */
    static String machineId;

    /**
     * targetType <br>
     * 目标类型
     */
    private String targetType;

    /**
     * targetId <br>
     * 目标锁唯一标识
     */
    private String targetId;

    static {
    	try {
    		//获取当前机器名
			machineId = InetAddress.getLocalHost().getHostName().toUpperCase();
//			machineId = System.getProperty("weblogic.Name");
    	} catch (UnknownHostException e) {
			e.printStackTrace();
		}
    }
   
    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getMachineId(){
        return machineId;
    }
    
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
  	
    public abstract void doWork() throws Exception;

	@Override
	public void execute() throws Exception {
		// 记录JOB运行轨迹
		Map<String, Object> logMap = new HashMap<String, Object>();
		logMap.put("instance_name", machineId);
		logMap.put("job_name", targetId);
		logMap.put("state", jobRunStates.start.getValue());

		logger.trace("aroundJobExecute [insName:"+ machineId + 
					",job:" + targetType + "-" + targetId + "]开始加锁---------");
		
		// job配置文件基本参数校验
		if (StringUtils.isEmpty(targetType)) {
			throw new IllegalArgumentException("非法Job Type配置");
		}
		if (StringUtils.isEmpty(targetId)) {
			throw new IllegalArgumentException("非法Job Id配置");
		}
		
		// JOB开始执行的时间
		Date jobStart = new Date();
		
		//记录日志
		String logId = recordJobExecuteLog(logMap);
		logMap.put("log_id", logId);
		String jobId = lock();
		if (StringUtils.isNotEmpty(jobId)) {
			logger.trace("aroundJobExecute [insName:"+ machineId + 
					",job:" + targetType + "-" + targetId+ "]加锁成功---------");
			try {
				doWork();
				logMap.put("state", jobRunStates.end.getValue());
				logMap.put("state_desc", targetId + "正常结束");
			} catch (Throwable e) {
				logger.error("aroundJobExecute 目标方法执行异常",e);
				
				logMap.put("state", jobRunStates.error.getValue());
				logMap.put("state_desc", "目标方法执行异常");
				logMap.put("error_msg", e.toString());
			} finally {
				// 释放锁
				boolean unlockSuccess = unLock(jobId);
				if (unlockSuccess) {
					logger.trace("aroundJobExecute [insName:"+ machineId + 
							",job:" + targetType + "-" + targetId+ "]释放锁成功---------");
				} else {
					logger.trace("aroundJobExecute [insName:"+ machineId + 
							",job:" + targetType + "-" + targetId+ "]释放锁失败---------");
				}
			}
		} else {
			logger.trace("aroundJobExecute [insName:"+ machineId + 
					",job:" + targetType + "-" + targetId+ "]加锁失败---------");
			
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
	private String lock() {
		TransactionStatus transactionStatus = transactionManager
			       .getTransaction(new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRES_NEW));
		
		String jobId=null;
		final Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("targetType", targetType);
		paramsMap.put("targetId", targetId);
		paramsMap.put("lockBy", machineId);
		paramsMap.put("isRunning", "0"); //暂时用isRunning作为job是否在运行的标志   0 :未运行 1: 运行中
		
		try {
			//用select for update nowait 的方式将job配置表记录锁住，并返回jobId。
			jobId=(String)generalDAO.queryForObject(SQLID.LOCK_SINGLE_JOB_CONFIG, paramsMap);
			
			if (jobId == null || "".equals(jobId)) {
				return null;
			}
			
			paramsMap.put("jobId", jobId);
			paramsMap.put("isRunning", "1");
			
			generalDAO.update(SQLID.UPDATE_JOB_CONFIG_BY_ID, paramsMap);
			
		} catch (Exception e) {
			logger.error("lock, 数据库错误", e);
			transactionStatus.setRollbackOnly();
		} finally {
			transactionManager.commit(transactionStatus);
	    }
		
		return jobId;
	}

	/**
	 * 解锁 JOB
	 * 
	 * @param job
	 * @return
	 */
	private boolean unLock(String jobId) {
		TransactionStatus transactionStatus = transactionManager
			       .getTransaction(new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRES_NEW));
		
		boolean unlockSuccess = false;
		final Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("jobId", jobId);
		paramsMap.put("isRunning", "0");
		try {
			int row = generalDAO.update(SQLID.UPDATE_JOB_CONFIG_BY_ID, paramsMap);
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
