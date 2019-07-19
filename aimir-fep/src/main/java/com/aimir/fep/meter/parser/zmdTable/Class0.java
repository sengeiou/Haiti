/*
 * Created on 2004. 12. 27.
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.aimir.fep.meter.parser.zmdTable;

import com.aimir.fep.util.DataFormat;

/**
 * @author Park YeonKyoung yeonkyoung@hanmail.net
 *
 * LandisGyr+ ZMD4 meter Class. <p> 
 * Include Meter Serial Information. <p>
 */
public class Class0 {
	
	public final static int OFS_SERIAL      = 19;	// offset meter serial number.
	public final static int LEN_SERIAL      = 8;	// length meter serial number.

	private byte[] data;

	/**
	 * Constructor 
	 * 
	 * @param data - read data
	 */
	public Class0(byte[] data) {
		this.data = data;
	}
	
	/**
	 * Get Meter Serial Number. <p>
	 * @return
	 */
	public byte[] parseSerial() throws Exception {		
		return DataFormat.select(data,OFS_SERIAL,LEN_SERIAL);
	}


}
