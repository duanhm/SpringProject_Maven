package com.dhm.schedule.job;

import com.dhm.schedule.core.BaseJob1;

public class PrintHelloJob1 extends BaseJob1 {

	@Override
	public void doWork() throws Exception {
		System.out.println("-------------hello1-------------");
	}
}
