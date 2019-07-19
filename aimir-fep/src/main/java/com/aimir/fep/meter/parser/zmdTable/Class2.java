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
 * Include DEMAND FACTOR.       <p>
 */
public class Class2 {
	
	public final static int OFS_DEMAND_FACTOR = 15;
	public final static int LEN_DEMAND_FACTOR = 2;
	
	private byte[] data;
	
	/**
	 * Constructor. <p>
	 * @param data
	 */
	public Class2(byte[] data){
		this.data = data;	
	}

	/**
	 * Get Demand Factor Field.<p>
	 * @return
	 */
	public byte[] parseDemandFactor() throws Exception {
		return DataFormat.select(data,OFS_DEMAND_FACTOR,LEN_DEMAND_FACTOR);
	}
}
