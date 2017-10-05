package com.dhm.service.biz;

import java.util.Map;

/**
 * 用来操作接口表（请求信息日志表和接口处理日志表）
 * @author EX-DUANHONGMEI001
 *
 */
public interface InterfaceProcessService {

	/**
	 * 日志信息记入日志表
	 * @param applyNo
	 * @param trxType
	 * @param errorCode
	 * @param errorMsg
	 * @param dataInfo
	 * @param status
	 * @param trxCode
	 * @param processRequstResultMap
	 */
	public void writeInterfaceProcessLog(String applyNo, String trxType,
			String errorCode, String errorMsg, String dataInfo) throws Exception;
	
	/**
	 * 接口处理失败时，需要记录日志
	 * @param reqData
	 * @param trxType
	 * @param resultMap
	 * @throws BusinessServiceException
	 */
	public void writeInterfaceProcessLog(Map reqData, String trxType, Map resultMap) throws Exception;
}
