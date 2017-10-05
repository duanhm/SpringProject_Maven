package com.dhm.service.biz.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.dhm.common.util.Base64Util;
import com.dhm.common.util.DESEncrptionUtil;
import com.dhm.common.util.SpringContextUtil;
import com.dhm.service.biz.CommonInterfaceService;
import com.dhm.service.biz.InterfaceDispatcher;
import com.dhm.service.biz.InterfaceProcessService;
import com.dhm.service.util.InterfaceConstantUtil;

@Service(CommonInterfaceService.SERVICE_ID)
public class CommonInterfaceServiceImpl implements CommonInterfaceService {

	private static Log logger=LogFactory.getLog(CommonInterfaceService.class);
	
	@Value("${common.service.keyStoreFileName}")
	private String keyStoreFileName;
	
	@Value("${common.service.keyStorePassword}")
	private String keyStorePassword;
	
	@Value("${common.service.sCharset}")
	private String sCharset;
	
	@Resource
	private InterfaceProcessService interfaceProcessService;
	
	@Override
	public String process(String reqData) {
		logger.info("加密后的请求报文 ["+reqData+"]");
		
		Map<String,Object> contentDataMap=new HashMap<String, Object>();
		try {
			Map<String,Object> decodedReqDataMap=JSON.parseObject(Base64Util.decode(reqData),Map.class);
			contentDataMap=(Map<String, Object>) decodedReqDataMap.get(InterfaceConstantUtil.CONTENT_DATA);
			String validateStr=validateReqData(contentDataMap);
			if (StringUtils.isNotEmpty(validateStr)){
				return generateResult(InterfaceConstantUtil.RETURN_CODE_9999, validateStr, contentDataMap, null);
			}
		} catch (Exception e) {
			logger.error("请求参数Json格式错误", e);
			return generateResult(InterfaceConstantUtil.RETURN_CODE_9999,"请求参数Json格式错误", null, null);
		}
		
		String interfaceId=contentDataMap.get(InterfaceConstantUtil.INTERFACE_ID).toString();
		String interfaceDesc=InterfaceConstantUtil.getInterfaceDescById(interfaceId);
		String processorType=InterfaceConstantUtil.getInterfaceProcessorByInterfaceId(interfaceId);
		logger.info("接口ID:[" + interfaceId + "] 接口名称:["+interfaceDesc + "] 解析后的请求参数为:[" + JSON.toJSONString(contentDataMap)+"]");
		
		Map<String,Object> resultMap=new HashMap<String, Object>();
		try {
			// 1. 调用通用的处理接口请求的方法
			InterfaceDispatcher interfaceDispatcher = (InterfaceDispatcher)SpringContextUtil.getBean("interfaceDispatcher");
			
			resultMap = (Map<String,Object>)interfaceDispatcher.handleInterfaceRequest(processorType, contentDataMap);
			
			// 如果队列执行过程有异常,如超时等,同样记录异常信息
			if(resultMap != null && resultMap.get(InterfaceConstantUtil.HANDLE_REQUEST_EXCEPTION_IND) != null) {
				interfaceProcessService.writeInterfaceProcessLog(contentDataMap, processorType, resultMap);
				resultMap.put(InterfaceConstantUtil.ERROR_MSG, "系统队列执行异常");
			}
			
			
			return generateResult((String)resultMap.get(InterfaceConstantUtil.RET_CODE),
					(String)resultMap.get(InterfaceConstantUtil.RET_MSG), contentDataMap, resultMap);
		} catch (Exception e) {
			logger.error("处理请求发生异常,请求结束",e);
			return generateResult(InterfaceConstantUtil.RETURN_CODE_9999, "系统内部异常", contentDataMap, resultMap);
		}
	}
	
	private String validateReqData(Map<String,Object> contentDataMap){
		String validateStr="";
		
		if (StringUtils.isEmpty((String)contentDataMap.get(InterfaceConstantUtil.REQ_SYS))) {
			validateStr="请求系统: req_sys不能为空! ";
			return validateStr;
		}
		
		if (StringUtils.isEmpty((String)contentDataMap.get(InterfaceConstantUtil.REQ_NO))) {
			validateStr="请求流水号: req_no不能为空! ";
			return validateStr;
		}
		
		String interfaceId = (String) contentDataMap.get(InterfaceConstantUtil.INTERFACE_ID);
		if (StringUtils.isEmpty(interfaceId)) {
			validateStr="接口编号: interface_id不能为空! ";
			return validateStr;
		}
		
		String processorType = InterfaceConstantUtil.getInterfaceProcessorByInterfaceId(interfaceId);
		if(StringUtils.isEmpty(processorType)) {
			validateStr = "请求接口编号无效! ";
			return validateStr;
		}
		
		if (StringUtils.isEmpty((String)contentDataMap.get(InterfaceConstantUtil.INFO_CONTENT))) {
			validateStr="请求业务参数: info_content不能为空! ";
			return validateStr;
		}
		
		if (StringUtils.isEmpty((String)contentDataMap.get(InterfaceConstantUtil.SIGN))) {
			validateStr="签名参数: sign不能为空! ";
			return validateStr;
		}
		
		// 解密验签
		validateStr=decryptAndVerifySign(contentDataMap);
		
		return validateStr;
	}
	
	private String decryptAndVerifySign(Map<String,Object> contentDataMap){
		String validateStr="";
		
		String infoContent=contentDataMap.get(InterfaceConstantUtil.INFO_CONTENT).toString();
		String sign=contentDataMap.get(InterfaceConstantUtil.SIGN).toString();
		
		try {
			//解密
			String decryptInfoContent=DESEncrptionUtil.decrypt(infoContent, sCharset, keyStorePassword, keyStoreFileName);
			
			//验签
			if (!DigestUtils.md5Hex(decryptInfoContent).equals(sign)){
				validateStr="验签失败";
				return validateStr;
			}
			contentDataMap.put(InterfaceConstantUtil.INFO_CONTENT, JSON.parseObject(decryptInfoContent, Map.class));
		} catch (Exception e) {
			validateStr="解密或验签异常";
			logger.error("解密或验签异常",e);
		}
		return validateStr;
	}
	
	private String generateResult(String ret_code, String ret_msg, Map<String,Object> requestMap, Map<String,Object> resultMap){
		Map<String,Object> jsonMap = new HashMap<String,Object>();

		ret_code = ret_code==null ? InterfaceConstantUtil.RETURN_CODE_9999 : ret_code;
		ret_msg = ret_msg==null ? (String)resultMap.get(InterfaceConstantUtil.ERROR_MSG) : ret_msg;
		jsonMap.put(InterfaceConstantUtil.RET_CODE, ret_code);
		jsonMap.put(InterfaceConstantUtil.RET_MSG, ret_msg);
		
		String interfaceId = requestMap==null ? "" : (String)requestMap.get(InterfaceConstantUtil.INTERFACE_ID);
		String interfaceDesc=InterfaceConstantUtil.getInterfaceDescById(interfaceId);
		
		jsonMap.put(InterfaceConstantUtil.REQ_SYS, requestMap==null ? "" : requestMap.get(InterfaceConstantUtil.REQ_SYS));
		jsonMap.put(InterfaceConstantUtil.REQ_NO, requestMap==null ? "" : requestMap.get(InterfaceConstantUtil.REQ_NO));
		jsonMap.put(InterfaceConstantUtil.INTERFACE_ID, requestMap==null ? "" : requestMap.get(InterfaceConstantUtil.INTERFACE_ID));
		jsonMap.put(InterfaceConstantUtil.INFO_CONTENT, resultMap==null ? "" : resultMap.get(InterfaceConstantUtil.INFO_CONTENT));
		
		String jsonResult=JSON.toJSONString(jsonMap);
		logger.info("接口ID:[" + interfaceId + "] 接口名称:["+interfaceDesc + "] 处理请求后的返回结果为:[" + jsonResult+"]");
		
		return jsonResult;
	}
}
