/** 
 * @(#)MT113.java       1.0 06/12/14 *
 * 
 * Line-Side diagnostics/Power Quality Data Table Class.
 * Copyright (c) 2006-2007 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */

package com.aimir.fep.meter.parser.SM110Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.Instrument;
import com.aimir.fep.util.DataFormat;

/**
 * @author Park YeonKyoung goodjob@nuritelecom.com
 */
public class MT113 implements java.io.Serializable {

	private static final long serialVersionUID = -5999754099256533299L;
	public static final int OFS_RMS_VOLTAGE_PHA       = 0;
	public static final int OFS_RMS_VOLTAGE_PHC       = 2;
	public static final int OFS_MOMENTARY_INTERVAL_PF = 4;
	
	public static final int LEN_RMS_VOLTAGE_PHA       = 2;
	public static final int LEN_RMS_VOLTAGE_PHC       = 2;
	public static final int LEN_MOMENTARY_INTERVAL_PF = 1;

	
	private byte[] data;
    private static Log log = LogFactory.getLog(MT113.class);
    
    public MT113() {}

	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public MT113(byte[] data) {
		this.data = data;
	}

	/**
	 * Momentary interval phase A Voltage
	 * @return
	 * @throws Exception
	 */
	public double getRMS_VOLTAGE_PHA() throws Exception {
		return DataFormat.hex2dec(
			DataFormat.LSB2MSB(
				DataFormat.select(
					data,OFS_RMS_VOLTAGE_PHA,LEN_RMS_VOLTAGE_PHA)))*0.1;
	}
	
	/**
	 * Momentary interval phase C Voltage
	 * @return
	 * @throws Exception
	 */
	public double getRMS_VOLTAGE_PHC() throws Exception {
		return DataFormat.hex2dec(
			DataFormat.LSB2MSB(
				DataFormat.select(
					data,OFS_RMS_VOLTAGE_PHC,LEN_RMS_VOLTAGE_PHC)))*0.1;
	}	

	/**
	 * Momentary interval power factor in percent
	 * (e.g. a reading of 70 corresponds to a power factor of 0.70)
	 * @return
	 * @throws Exception
	 */
	public double getMOMENTARY_INTERVAL_PF() throws Exception {
		return  DataFormat.hex2unsigned8(data[OFS_MOMENTARY_INTERVAL_PF]);
	}
    
    public Instrument[] getInstrument() {
        
        try{
            Instrument[] instruments = new Instrument[1];
            Instrument inst = new Instrument();
            inst.setVOL_A(getRMS_VOLTAGE_PHA());
            inst.setVOL_C(getRMS_VOLTAGE_PHC());
            instruments[0] = inst;
            return instruments;
        }catch(Exception e){
            log.warn(e);
        }
        return null;
    }
}
