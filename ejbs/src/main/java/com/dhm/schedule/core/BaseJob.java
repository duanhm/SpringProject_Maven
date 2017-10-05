package com.dhm.schedule.core;

import java.net.InetAddress;
import java.net.UnknownHostException;

public abstract class BaseJob implements LockedJob {
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

    /**
     * lockMins <br>
     * 锁超时时间（分钟）
     */
    private Double lockMins;
    
    static {
    	try {
    		//获取当前机器名
			machineId = InetAddress.getLocalHost().getHostName().toUpperCase();
//			machineId = System.getProperty("weblogic.Name");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
    }
   
    public Double getLockMins() {
        return lockMins;
    }

    public void setLockMins(Double lockMins) {
        this.lockMins = lockMins;
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
}
