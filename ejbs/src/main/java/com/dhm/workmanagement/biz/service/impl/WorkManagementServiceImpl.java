package com.dhm.workmanagement.biz.service.impl;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;

import org.springframework.stereotype.Component;

import com.dhm.workmanagement.biz.service.WorkManagementService;
import com.dhm.workmanagement.exception.WorkManagementException;

/**
 * 
 * @author EX-DUANHONGMEI001
 *
 */
@Component("workManagementService" )
public class WorkManagementServiceImpl implements WorkManagementService {

    @Override
    public void submitWork(String workExecutorName, Runnable task) throws RejectedExecutionException,WorkManagementException {
        ExecutorService exec = WorkExecutorFactory.getWorkExecutor(workExecutorName);
        if (exec == null) {
            throw new WorkManagementException("指定的工作执行队列不存在 : 队列名：" + workExecutorName);
        }
        exec.execute(task);
    }
    
    @Override
    public Future<?> submitFutureWork(String workExecutorName, Callable<?> task) throws RejectedExecutionException,WorkManagementException {
        ExecutorService exec = WorkExecutorFactory.getWorkExecutor(workExecutorName);
        if (exec == null) {
            throw new WorkManagementException("指定的工作执行队列不存在 : 队列名：" + workExecutorName);
        }
        Future<?> future = exec.submit(task);
        return future;
    }
}
