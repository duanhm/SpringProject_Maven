package com.dhm.service.biz.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.dhm.common.SQLID;
import com.dhm.dao.GeneralDAO;
import com.dhm.service.biz.InterfaceProcessService;
import com.dhm.service.util.InterfaceConstantUtil;

/**
 * 用来操作接口表（请求信息日志表和接口处理日志表）
 * @author EX-DUANHONGMEI001
 *
 */
@Component("interfaceProcessService")
public class InterfaceProcessServiceImpl implements InterfaceProcessService {

	private static Log logger=LogFactory.getLog(InterfaceProcessService.class);
	
	@Resource(name=GeneralDAO.SERVICE_ID)
	private GeneralDAO generalDAO;
	
	
	/**
	 * 记录处理请求异常信息
	 * @param applyNo
	 * @param trxType
	 * @param errorCode
	 * @param errorMsg
	 * @param dataInfo
	 * @param status
	 * @param trxCode
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void writeInterfaceProcessLog(String applyNo, String trxType,
			String errorCode, String errorMsg, String dataInfo) {
		Map params=new HashMap();
		params.put("businessNo", applyNo);
		params.put("trxType",trxType);
		params.put("errorCode", errorCode);
		params.put("dataInfo", dataInfo);
		params.put("errorMsg", errorMsg);
		params.put("sysId", "yxd-cbp");
		try {
			generalDAO.insert(SQLID.WRITE_PROCESS_LOG, params);
		} catch (Exception e) {
			logger.error("日志记录出错:"+e.getMessage(),e);
		}
	}

	/**
	 * 接口处理失败时，需要记录日志
	 * @param data
	 * @param trxType
	 * @param resultMap
	 * @throws Exception
	 */
	@Override
	public void writeInterfaceProcessLog(Map reqData, String trxType,
			Map resultMap) throws Exception {
		
		String sysCode = (String) reqData.get(InterfaceConstantUtil.REQ_SYS);
		String reqSer = (String) reqData.get(InterfaceConstantUtil.REQ_NO);
		sysCode = StringUtils.isEmpty(sysCode) ? "" : sysCode;
		reqSer = StringUtils.isEmpty(reqSer) ? "" : reqSer;
		
		writeInterfaceProcessLog(
				sysCode + "@" + reqSer,
				trxType, InterfaceConstantUtil.RETURN_CODE_9999,
				(String) resultMap.get(InterfaceConstantUtil.ERROR_MSG),
				JSON.toJSONString(reqData));
	}
}
