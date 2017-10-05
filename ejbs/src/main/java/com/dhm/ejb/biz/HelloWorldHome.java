package com.dhm.ejb.biz;

import java.rmi.RemoteException;

import javax.ejb.CreateException;
import javax.ejb.EJBHome;


public interface HelloWorldHome extends EJBHome {
	public HelloWorldRemote create() throws CreateException, RemoteException;
}
