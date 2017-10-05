package com.dhm.service.util;


public class ExceptionUtils {
	
    public static Throwable getRootCauseOrSelf(Throwable throwable) {
    	Throwable[] ta = org.apache.commons.lang.exception.ExceptionUtils
				.getThrowables(throwable);
		return (ta.length < 1 ? throwable : ta[ta.length - 1]);
    }

    public static String getRootCauseStackTrace(Throwable throwable) {
    	Throwable t = getRootCauseOrSelf(throwable);
    	return org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(t);
    }    
    
    public static String getExceptionMsg(Throwable throwable) {
    	Throwable[] ta = org.apache.commons.lang.exception.ExceptionUtils
				.getThrowables(throwable);
    	StringBuffer exceptionMsgBf = new StringBuffer(throwable.getMessage());
    	if(ta.length > 1){
    		exceptionMsgBf.append(ta[ta.length - 1].getMessage());
    	}
    	return exceptionMsgBf.toString();
    }
    
    public static String getExceptionString(Throwable throwable) {
    	Throwable[] ta = org.apache.commons.lang.exception.ExceptionUtils
				.getThrowables(throwable);
    	StringBuffer exceptionMsgBf = new StringBuffer(toSimpleString(throwable));
    	if(ta.length > 1){
    		exceptionMsgBf.append(toSimpleString(ta[ta.length - 1]));
    	}
    	return exceptionMsgBf.toString();
    }
    public static String toSimpleString(Throwable throwable) {
        String s = throwable.getClass().getSimpleName();
        String message = throwable.getLocalizedMessage();
        return (message != null) ? (s + ": " + message) : s;
    }
    /**
	 * 异常处理
	 * @param errorMsgCode
	 * @param errorMsgCodeDT
	 * @param e
	 * @return
	 */
	public static String getErrorMsg(String errorMsgCode,Exception e){
		StringBuffer processBf = new StringBuffer();
		String exception=null;
		processBf.append(errorMsgCode);
		if(e != null){
			processBf.append(ExceptionUtils.getRootCauseStackTrace(e));
			exception=processBf.toString();
		}
		// 这里不进行字符串的截取.
		/*if(processBf.length()>=4000)
		{
			exception=processBf.substring(0, 3999);
		}*/
		return exception;
	}
}
