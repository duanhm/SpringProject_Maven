package com.dhm.service.util;

import java.util.HashMap;
import java.util.Map;


/**
 *  接口常量定义
 * @author EX-DUANHONGMEI001
 *
 */
public class InterfaceConstantUtil {

	public static final String CONTENT_DATA="content_data";
	
	/** 请求系统 */
	public static final String REQ_SYS="req_sys";
	/** 请求流水号 */
	public static final String REQ_NO="req_no";
	/** 接口编号 */
	public static final String INTERFACE_ID="interface_id";
	/** 请求业务参数 */
	public static final String INFO_CONTENT="info_content";
	/** 签名参数 */
	public static final String SIGN="sign";
	/** 返回码 */
	public static final String RET_CODE="ret_code";
	/** 返回信息 */
	public static final String RET_MSG="ret_msg";
	/** 失败返回码 */
	public static final String RETURN_CODE_9999="9999";
	/** 成功返回码 */
	public static final String RETURN_CODE_0000="0000";
	/** 错误信息Key */
	public static final String ERROR_MSG = "error_msg";
	/** 处理状态Key */
	public static final String PROCESS_STATUS = "status";
	/** 队列分发处理异常标识 */
	public static final String HANDLE_REQUEST_EXCEPTION_IND = "handleRequestExceptionInd";
	public static final String COMMON_Y = "Y";
	
	/** 接口ID */
	public static final String _0001 = "0001"; // helloWorld
	
	/** 接口ID编码与对应处理类 Processor的Map对象 */
	public static final Map<String, String> INTERFACE_PROCESSOR_MAP=new HashMap<String, String>();
	
	/** 接口ID描述 */
	public static final Map<String, String> INTERFACE_DESC_MAP=new HashMap<String, String>();
	
	static {
		INTERFACE_PROCESSOR_MAP.put(_0001, TrxTypeUtil.HELLO_WORLD_PROCESSOR);
	}
	
	static {
		INTERFACE_DESC_MAP.put(_0001, "helloWorld服务");
	}
	
	public static String getInterfaceProcessorByInterfaceId(String interfaceId) {
		return INTERFACE_PROCESSOR_MAP.get(interfaceId);
	}
	
	public static String getInterfaceDescById(String interfaceId) {
		return INTERFACE_DESC_MAP.get(interfaceId);
	}
}
