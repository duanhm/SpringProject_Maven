package com.dhm.service.biz;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dhm.service.util.ExceptionUtils;
import com.dhm.service.util.InterfaceConstantUtil;
import com.dhm.service.util.TrxTypeUtil;
import com.dhm.workmanagement.biz.service.WorkManagementService;
import com.dhm.workmanagement.exception.WorkManagementException;

/**
 * 将不同接口的请求交由相应的Processor类进行处理
 * @author EX-DUANHONGMEI001
 *
 */
@Component("interfaceDispatcher")
public class InterfaceDispatcher {

	private static Log logger=LogFactory.getLog(InterfaceDispatcher.class);
	
	@Value("${common.service.queue.timeout}")
	private String timeout;
	
	@Autowired
	private Map<String, InterfaceDataDist> processorMap;
	
	@Autowired
	private WorkManagementService workManagementService;
	
	/**
	 * 
	 * @param processorType
	 * @param data
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unused" })
	public Object handleInterfaceRequest(String processorType, Map data){	
		Object obj=null;
		Future future=null;
		Map exceptionMap=new HashMap();
		InterfaceDataDist interfaceDataDist=null;
		try {
			interfaceDataDist = (InterfaceDataDist)processorMap.get(processorType);
			logger.info("handleInterfaceRequest, @@队列开始执行:" + data.get("req_no"));
			
			future = workManagementService.submitFutureWork(TrxTypeUtil.interFaceWorkExecutorMap.get(processorType), new ProcessRequestWorker(interfaceDataDist, data));
			
			logger.info("handleInterfaceRequest, @@队列开始执行超时时间为:" + timeout);
			
			obj = future.get(Long.valueOf(timeout), TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			String errMsg = processorType + " - 接口公共方法一个正在执行的线程被中断出错:"+e.getMessage();
			logger.error(errMsg, e);
			exceptionMap.put(InterfaceConstantUtil.ERROR_MSG, ExceptionUtils.getErrorMsg(errMsg, e));
			exceptionMap.put(InterfaceConstantUtil.HANDLE_REQUEST_EXCEPTION_IND, InterfaceConstantUtil.COMMON_Y);
			obj = exceptionMap;
		} catch (ExecutionException e) {
			String errMsg = processorType + " -接口公共方法ExecutionException出错:"+e.getMessage();
			logger.error(errMsg,e);
			exceptionMap.put(InterfaceConstantUtil.ERROR_MSG, ExceptionUtils.getErrorMsg(errMsg, e));
			exceptionMap.put(InterfaceConstantUtil.HANDLE_REQUEST_EXCEPTION_IND, InterfaceConstantUtil.COMMON_Y);
			obj=exceptionMap;
		} catch (TimeoutException e) {
			String errMsg = processorType + " -接口公共方法超时出错:"+e.getMessage();
			logger.error(errMsg, e);
			exceptionMap.put(InterfaceConstantUtil.ERROR_MSG, ExceptionUtils.getErrorMsg(errMsg, e));
			exceptionMap.put(InterfaceConstantUtil.HANDLE_REQUEST_EXCEPTION_IND, InterfaceConstantUtil.COMMON_Y);
			obj = exceptionMap;
		}catch(WorkManagementException e){
			String errMsg = "接口:"+processorType+"加入线程池失败,同步执行";
			logger.error(errMsg, e);
			exceptionMap.put(InterfaceConstantUtil.ERROR_MSG, ExceptionUtils.getErrorMsg(errMsg + e.getMessage(), e));
			exceptionMap.put(InterfaceConstantUtil.HANDLE_REQUEST_EXCEPTION_IND, InterfaceConstantUtil.COMMON_Y);
			obj = exceptionMap;
		}catch (RejectedExecutionException e) {
			String errMsg = "接口:"+processorType+"超出队列或者线程数限制";
			logger.error(errMsg, e);
			exceptionMap.put(InterfaceConstantUtil.ERROR_MSG, ExceptionUtils.getErrorMsg(errMsg + e.getMessage(), e));
			exceptionMap.put(InterfaceConstantUtil.HANDLE_REQUEST_EXCEPTION_IND, InterfaceConstantUtil.COMMON_Y);
			obj = exceptionMap;
		} catch (Exception e) {
			String errMsg = processorType + " -接口公共方法未知错误:"+e.getMessage();
			logger.error(errMsg, e);
			exceptionMap.put(InterfaceConstantUtil.ERROR_MSG, ExceptionUtils.getErrorMsg(errMsg, e));
			exceptionMap.put(InterfaceConstantUtil.HANDLE_REQUEST_EXCEPTION_IND, InterfaceConstantUtil.COMMON_Y);
			obj = exceptionMap;
		}
		//线程没有初始化成功
		if(obj == null)
		{
			logger.error("接口:"+processorType+"处理结果为空："+data.toString());
		}
		logger.info("handleInterfaceRequest, @@队列结束执行:" + data.get("req_no"));
		return obj;
	}
	
	/**
	 * 实际分发请求
	 * @author EX-DUANHONGMEI001
	 *
	 * @param <V>
	 */
	private class ProcessRequestWorker implements Callable<Object> {

        private InterfaceDataDist ex;

        @SuppressWarnings("rawtypes")
		private Map data;

        @SuppressWarnings("rawtypes")
		public ProcessRequestWorker(InterfaceDataDist ex, Map data) {
            this.ex = ex;
            this.data = data;
        }

        @Override
        public Object call() throws Exception {
            return ex.dealRequest(data);
        }
    }
	
	/**
	 * 实际分发请求
	 * @author EX-DUANHONGMEI001
	 *
	 */
	/*private class TrxProcessWorker implements Runnable {
		
		private InterfaceDataDist interfaceDataDist;
		
		@SuppressWarnings("rawtypes")
		private Map data;
		
		@SuppressWarnings("rawtypes")
		public TrxProcessWorker(InterfaceDataDist interfaceDataDist, Map data) {
		    this.interfaceDataDist = interfaceDataDist;
		    this.data = data;
		}
		
		@Override
		public void run() {
			interfaceDataDist.dealRequest(data);
		}
	}*/
}
