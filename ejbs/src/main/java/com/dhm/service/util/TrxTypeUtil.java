package com.dhm.service.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 接口类型工具类
 * @author EX-DUANHONGMEI001
 *
 */
public class TrxTypeUtil{

	/** helloWorld服务 */
	public static final String HELLO_WORLD_PROCESSOR = "helloWorldProcessor";

	/**
	 * 外调内同步队列
	 */
	public static Map<String, String> interFaceWorkExecutorMap=new HashMap<String, String>();
	
	static {
		interFaceWorkExecutorMap.put(HELLO_WORLD_PROCESSOR, "helloWorldIWE");
	}
}
