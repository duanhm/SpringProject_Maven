package com.dhm.common.redis;

public class RedisConfiguration {

    private String host;

    private String port;

    private String pwd;

    private String maxActive;

    private String maxIdle;

    private String maxWait;

    private String testOnBorrow;

    private String testOnReturn;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getMaxActive() {
        return maxActive;
    }

    public void setMaxActive(String maxactive) {
        this.maxActive = maxactive;
    }

    public String getMaxIdle() {
        return maxIdle;
    }

    public void setMaxIdle(String maxidle) {
        this.maxIdle = maxidle;
    }

    public String getMaxWait() {
        return maxWait;
    }

    public void setMaxWait(String maxwait) {
        this.maxWait = maxwait;
    }

    public String getTestOnBorrow() {
        return testOnBorrow;
    }

    public void setTestOnBorrow(String testonborrow) {
        this.testOnBorrow = testonborrow;
    }

    public String getTestOnReturn() {
        return testOnReturn;
    }

    public void setTestOnReturn(String testonreturn) {
        this.testOnReturn = testonreturn;
    }
    
    
}
