package com.aimir.mars.integration.bulkreading.model;

import java.io.Serializable;

public class MDMLpEMPK implements Serializable {
	
	protected String mdevId;
    protected String yyyymmddhhmmss;
    protected String mdevType;
    protected Integer dst;
    
    public MDMLpEMPK() {}

    public MDMLpEMPK(String mdevId, String yyyymmddhhmmss, String mdevType, Integer dst) {    	
    	this.mdevId = mdevId;
    	this.yyyymmddhhmmss = yyyymmddhhmmss;
        this.mdevType = mdevType;
        this.dst = dst;
    }
}