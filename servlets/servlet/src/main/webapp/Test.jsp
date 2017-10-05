<%@page import="javax.rmi.PortableRemoteObject"%>
<%@page import="com.dhm.ejb.biz.HelloWorldHome"%>
<%@page import="com.dhm.ejb.biz.HelloWorldRemote"%>
<%@page contentType="text/html; charset=GBK"%>
<%@page import="com.dhm.ejb.*, javax.naming.*"%>
<%
 try {  
      out.println("hello world");
      InitialContext ctx = new InitialContext();
      Object remoteObj = ctx.lookup("ejb/helloWorldService");
      HelloWorldHome home= (HelloWorldHome)PortableRemoteObject.narrow(remoteObj, HelloWorldHome.class);
      HelloWorldRemote remote=home.create();
      out.println(remote.SayHello("test"));
 } catch (Exception e) {
  out.println(e.getMessage());
 }
%>