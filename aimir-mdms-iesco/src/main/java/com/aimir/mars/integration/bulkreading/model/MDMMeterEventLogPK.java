package com.aimir.mars.integration.bulkreading.model;

import java.io.Serializable;

public class MDMMeterEventLogPK implements Serializable {
	
	protected String activator_id;
    protected String meterevent_id;
    protected String open_time;
    protected String yyyymmdd;
    
    public MDMMeterEventLogPK() {}

    public MDMMeterEventLogPK(String activator_id, String meterevent_id, String open_time, String yyyymmdd) {
        this.activator_id = activator_id;
        this.meterevent_id = meterevent_id;
        this.open_time = open_time;
        this.yyyymmdd = yyyymmdd;
    }
}
