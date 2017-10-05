package com.dhm.workmanagement.exception;


/**
 * 
 * @author EX-DUANHONGMEI001
 *
 */
public class WorkManagementException extends Exception {

    /**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -8519416710858825087L;

	public WorkManagementException(String s) {
        super(s);
    }

    public WorkManagementException(String s, Throwable throwable) {
        super(s, throwable);
    }
}

