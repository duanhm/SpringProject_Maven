package com.dhm.ejb.biz;

import java.rmi.RemoteException;

import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;

public class HelloWorldBean implements SessionBean {

    /**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -5822535492791212079L;

	public String SayHello(String name) throws RemoteException {
        return name +"说：你好!";
    }

	public void ejbCreate() {
		System.out.println("HelloWorldBean: ejbCreate() is called!");
	}


	public void setSessionContext(SessionContext sessionContext) throws EJBException, RemoteException {

	}

	public void ejbRemove() throws EJBException, RemoteException {

	}

	public void ejbActivate() throws EJBException, RemoteException {

	}

	public void ejbPassivate() throws EJBException, RemoteException {

	}
}