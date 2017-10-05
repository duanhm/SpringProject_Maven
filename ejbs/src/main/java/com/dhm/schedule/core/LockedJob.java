package com.dhm.schedule.core;

/**
 * quartz锁接口，业务job需要实现execute()方法
 * @author EX-DUANHONGMEI001
 *
 */
public interface LockedJob {

   /**
    * 业务方法 
    * @throws Exception
    */
    public void execute() throws Exception;
    
}
