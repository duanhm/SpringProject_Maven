package com.dhm.ejb.biz;

import java.rmi.RemoteException;

import javax.ejb.EJBObject;


public interface HelloWorldRemote extends EJBObject {
	public String SayHello(String name) throws RemoteException;
}
