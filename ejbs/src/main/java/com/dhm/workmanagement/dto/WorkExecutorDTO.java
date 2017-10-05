package com.dhm.workmanagement.dto;

/**
 * 
 * @author EX-DUANHONGMEI001
 *
 */
public class WorkExecutorDTO {

	private String workExecutorName;
    private int nThreads;
    private int corePoolSize;
    private int maximumPoolSize;
    private int maximumWorkQueueSize;
    private String systemName;

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public String getWorkExecutorName() {
        return workExecutorName;
    }

    public void setWorkExecutorName(String workExecutorName) {
        this.workExecutorName = workExecutorName;
    }

    public int getNThreads() {
        return nThreads;
    }

    public void setNThreads(int nThreads) {
        this.nThreads = nThreads;
    }

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public int getMaximumPoolSize() {
        return maximumPoolSize;
    }

    public void setMaximumPoolSize(int maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
    }

    public int getMaximumWorkQueueSize() {
        return maximumWorkQueueSize;
    }

    public void setMaximumWorkQueueSize(int maximumWorkQueueSize) {
        this.maximumWorkQueueSize = maximumWorkQueueSize;
    }
}
