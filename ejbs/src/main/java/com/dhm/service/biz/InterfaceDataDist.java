package com.dhm.service.biz;

import java.util.Map;
/**
 * 使用队列处理请求的接口
 * @author EX-DUANHONGMEI001
 *
 */
public interface InterfaceDataDist {
	
	/**
	 * 处理请求
	 * @param params
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Object dealRequest(Map params);
}
