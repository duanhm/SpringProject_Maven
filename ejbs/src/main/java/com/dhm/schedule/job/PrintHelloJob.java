package com.dhm.schedule.job;

import com.dhm.schedule.core.BaseJob;

public class PrintHelloJob extends BaseJob {
	
	@Override
	public void execute(){
		System.out.println("-------------hello-------------");
	}
}
