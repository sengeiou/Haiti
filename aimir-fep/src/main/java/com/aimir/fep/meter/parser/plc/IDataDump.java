package com.aimir.fep.meter.parser.plc;

import com.aimir.fep.util.DataUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Meter Status Response/Meter Trap
 * @author kaze
 * 2009. 6. 19.
 */
public class IDataDump implements java.io.Serializable{
	private static Log log = LogFactory.getLog(IDataDump.class);
	private String id;
	private String psmMac;

	public IDataDump(byte[] rawData) throws Exception {
		try {
			//Check Length
			if(rawData.length!=PLCDataConstants.IDATA_DUMP_TOTAL_LEN) {
				throw new Exception("IDataDUMP LENGTH["+rawData.length+"] IS INVALID!");
			}
			int pos = 0;
			byte[] METERID = new byte[20];//Meter ID
			byte[] PSMMAC = new byte[6];//Meter PLC MAC Address

			pos=DataUtil.copyBytes(PLCDataConstants.isConvert, pos, rawData, METERID);
			id=DataUtil.getString(METERID).trim();

			pos=DataUtil.copyBytes(PLCDataConstants.isConvert, pos, rawData, PSMMAC);
			psmMac=DataUtil.getPLCMacAddr(PSMMAC).trim();
		}catch (Exception e) {
			log.error("IDATA DUMP PARSING ERROR! - "+e.getMessage());
			e.printStackTrace();
		}
	}


	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}


	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}


	/**
	 * @return the psmMac
	 */
	public String getPsmMac() {
		return psmMac;
	}


	/**
	 * @param psmMac the psmMac to set
	 */
	public void setPsmMac(String psmMac) {
		this.psmMac = psmMac;
	}


	/**
	 * Constructs a <code>String</code> with all attributes
	 * in name = value format.
	 *
	 * @return a <code>String</code> representation
	 * of this object.
	 */
	public String toString()
	{
	    final String TAB = "\n";

	    StringBuffer retValue = new StringBuffer();

	    retValue.append("IDataDump ( ")
	        .append(super.toString()).append(TAB)
	        .append("id = ").append(this.id).append(TAB)
	        .append("psmMac = ").append(this.psmMac).append(TAB)
	        .append(" )");

	    return retValue.toString();
	}
}
