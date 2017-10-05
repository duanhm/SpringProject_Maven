package com.dhm.cro.biz;

import java.util.Map;


public interface ActivitiService {
	
	/**
     * SERVICE_ID <br>
     */
	String SERVICE_ID="cro.biz.activitiService";
	
	@SuppressWarnings("rawtypes")
	public void saveActHiDetail1(Map param);
	
	@SuppressWarnings("rawtypes")
	public void saveActHiDetail2(Map param);
}
