package com.aimir.fep.trap.mbean;

public interface EventRuleMBean {

    public void startActiveMQ() throws Exception;

    public void startExgineMQ() throws Exception;

    public void start() throws Exception;

    public void stop() throws Exception;
    
    public void close() throws Exception;
}
