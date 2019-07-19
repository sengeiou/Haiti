package com.aimir.mars.integration.bulkreading.model;

import java.io.Serializable;

import com.aimir.constants.CommonConstants.DeviceType;

public class MDMBillingDayEMPK implements Serializable {
	
	private String yyyymmdd;
	private String hhmmss;
    private String mdevId;
    private DeviceType mdevType;
    
    public MDMBillingDayEMPK() {}

    public MDMBillingDayEMPK(String yyyymmdd, String hhmmss, String mdevId, DeviceType mdevType) {
        this.yyyymmdd = yyyymmdd;
        this.hhmmss = hhmmss;
        this.mdevId = mdevId;
        this.mdevType = mdevType;
    }
}
