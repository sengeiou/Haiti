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
 * Include ERROR_STATUS. <p>
 */
public class Class1 {
	
	public final static int OFS_ERROR_STATUS  = 16;
	public final static int LEN_ERROR_STATUS  = 4;
	
	private byte[] data;
	
	/**
	 * Constructor . <p>
	 * @param data
	 */
	public Class1(byte[] data){
		this.data = data;	
	}

	/**
	 * Get Error Status Field.<p>
	 * @return
	 */
	public byte[] parseErrorStatus() throws Exception {
		return DataFormat.select(data,OFS_ERROR_STATUS,LEN_ERROR_STATUS);
	}
}
