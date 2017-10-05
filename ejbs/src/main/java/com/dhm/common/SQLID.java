package com.dhm.common;


public class SQLID {
	/**锁机制 加锁*/
	public static final String UPDATE_QUARTZLOCK_SQL = "update.quartzLock.sql";
	
	/**锁机制 释放锁*/
	public static final String UPDATE_QUARTZUNLOCK_SQL = "update.quartzUnLock.sql";
	
	/**记录JOB运行轨迹*/
	public static final String INSERT_RECORD_JOB_EXECUTE_LOG = "insert.recordJobExecuteLog.sql";
	
	/**更新JOB运行轨迹*/
	public static final String UPDATE_JOB_EXECUTE_LOG = "update.updateJobExecuteLog.sql";
	
	
	
	public static final String LOCK_SINGLE_JOB_CONFIG = "lockSingleJobConfig.sql";
	public static final String UPDATE_JOB_CONFIG_BY_ID = "updateJobConfigById.sql";
	
	
	/**获取线程池配置*/
	public static final String SEARCH_WORK_EXECUTOR_DEFINE = "search.work.executor.define";
	
	public static final String WRITE_PROCESS_LOG="writeProcessLog";
}
