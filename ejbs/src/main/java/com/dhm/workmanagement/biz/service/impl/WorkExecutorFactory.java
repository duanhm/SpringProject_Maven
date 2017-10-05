package com.dhm.workmanagement.biz.service.impl;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dhm.workmanagement.biz.dao.WorkManagementDAO;
import com.dhm.workmanagement.dto.WorkExecutorDTO;
import com.dhm.workmanagement.util.WorkExecutorRepository;

/**
 * 
 * @author EX-DUANHONGMEI001
 *
 */
@Component("workExecutorFactory")
public class WorkExecutorFactory {

    @Autowired
    private WorkManagementDAO workManagementDAO;

    private final static WorkExecutorRepository wr = WorkExecutorRepository.getInstance();

    @PostConstruct
    private void initWorkExecutors() {
        List<WorkExecutorDTO> workExecutorProfile = workManagementDAO.getWorkManagementProfile();
        for (WorkExecutorDTO workExecutorDTO : workExecutorProfile) {
            ThreadPoolExecutor tpe = initThreadPool(workExecutorDTO);
            wr.bind(workExecutorDTO.getWorkExecutorName(), tpe);
        }
    }

    private ThreadPoolExecutor initThreadPool(WorkExecutorDTO workExecutorDTO) {
        if (workExecutorDTO.getMaximumWorkQueueSize() > 0) {
            return new TrackingThreadPool(workExecutorDTO.getCorePoolSize(), workExecutorDTO.getMaximumPoolSize(), 0L,
                    TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(workExecutorDTO.getMaximumWorkQueueSize()));
        } else if (workExecutorDTO.getMaximumWorkQueueSize() == 0) {
            return new TrackingThreadPool(0, workExecutorDTO.getMaximumPoolSize(), 60L, TimeUnit.SECONDS,
                    new SynchronousQueue<Runnable>());
        } else if (workExecutorDTO.getMaximumWorkQueueSize() < 0) {
            return new TrackingThreadPool(workExecutorDTO.getCorePoolSize(), workExecutorDTO.getMaximumPoolSize(), 0L,
                    TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        } else {
            throw new IllegalArgumentException("IllegalArgumentException" + workExecutorDTO.getWorkExecutorName());
        }
    }

    public static ExecutorService getWorkExecutor(String workExecutorName) {
        return wr.lookup(workExecutorName);
    }
}
