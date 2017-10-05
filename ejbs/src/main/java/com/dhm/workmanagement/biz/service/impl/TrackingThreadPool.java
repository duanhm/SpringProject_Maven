package com.dhm.workmanagement.biz.service.impl;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * 
 * @author EX-DUANHONGMEI001
 *
 */
public class TrackingThreadPool extends ThreadPoolExecutor {

    public TrackingThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
            BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    private final Map<Runnable, Boolean> inProgress = new ConcurrentHashMap<Runnable, Boolean>();

    private final ThreadLocal<Long> startTime = new ThreadLocal<Long>();

    private long totalTime;

    private int totalTask;

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
        startTime.set(new Long(System.currentTimeMillis()));
        inProgress.put(r, Boolean.TRUE);
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        long time = System.currentTimeMillis() - startTime.get().longValue();
        synchronized (this) {
            totalTime += time;
            ++totalTask;
        }
        super.afterExecute(r, t);
        inProgress.remove(r);
    }

    public Set<Runnable> getInProgressTasks() {
        return Collections.unmodifiableSet(inProgress.keySet());
    }

    public synchronized int getInProgressTaskAmount() {
        return inProgress.keySet().size();
    }

    public synchronized int getTotalTasks() {
        return totalTask;
    }

    public synchronized long getTotalTaskTime() {
        return totalTime;
    }

    public synchronized double getAverageTaskTime() {
        return (totalTask == 0) ? 0 : totalTime / totalTask;
    }

    public synchronized int getQueueSize() {
        return getQueue().size();
    }

}
