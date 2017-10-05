package com.dhm.service.web.controller;

import com.dhm.service.biz.CommonInterfaceService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
public class CommonInterfaceController {
	private static Log logger=LogFactory.getLog(CommonInterfaceService.class);
	
	@Resource(name=CommonInterfaceService.SERVICE_ID)
	private CommonInterfaceService commonInterfaceService;
	
	@RequestMapping(value="/processRequest", method=RequestMethod.POST)
	@ResponseBody
	public String processRequest(@RequestBody String reqData){
		//返回处理结果
		String result = "";
		try {
			result=commonInterfaceService.process(reqData);
		} catch (Exception e) {
			logger.error("处理请求异常", e);
		}
		
		return result;
	}
}
