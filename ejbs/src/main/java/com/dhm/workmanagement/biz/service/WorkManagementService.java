package com.dhm.workmanagement.biz.service;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;

import com.dhm.workmanagement.exception.WorkManagementException;


/**
 * 
 * @author EX-DUANHONGMEI001
 *
 */
public interface WorkManagementService {

    public void submitWork(String workExecutorName, Runnable task)
            throws RejectedExecutionException,WorkManagementException;

    public Future<?> submitFutureWork(String workExecutorName, Callable<?> task)
            throws RejectedExecutionException,WorkManagementException;

}
