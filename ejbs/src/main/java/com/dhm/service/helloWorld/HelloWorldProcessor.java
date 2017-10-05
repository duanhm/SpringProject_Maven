package com.dhm.service.helloWorld;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.dhm.service.biz.InterfaceDataDist;
import com.dhm.service.util.InterfaceConstantUtil;
import com.dhm.service.util.TrxTypeUtil;

@Component(TrxTypeUtil.HELLO_WORLD_PROCESSOR)
public class HelloWorldProcessor implements InterfaceDataDist {

	@Override
	public Object dealRequest(Map params) {
		Map<String, Object> infoContentMap=(Map<String, Object>) params.get(InterfaceConstantUtil.INFO_CONTENT);
				
		Map<String, Object> resultMap=new HashMap<String, Object>();
		Map<String, Object> resultInfoContent=new HashMap<String, Object>();
		resultInfoContent.put("resp", "hello"+infoContentMap.get("name"));
		
		resultMap.put(InterfaceConstantUtil.RET_CODE, InterfaceConstantUtil.RETURN_CODE_0000);
		resultMap.put(InterfaceConstantUtil.RET_MSG, "helloWorld服务调用成功");
		resultMap.put(InterfaceConstantUtil.INFO_CONTENT, resultInfoContent);
		
		return resultMap;
	}
}
