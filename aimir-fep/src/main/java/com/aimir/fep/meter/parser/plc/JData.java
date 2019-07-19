package com.aimir.fep.meter.parser.plc;

import com.aimir.fep.util.DataUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Response(변압기 감시 데이터)
 * @author kaze
 * 2009. 6. 19.
 */
public class JData extends PLCData {
	private static Log log = LogFactory.getLog(JData.class);
	private int dumpCnt;
	private JDataDump[] jDataDump;

	public JData(PLCDataFrame pdf) {
		super(pdf);
		try {
			byte[] rawData = pdf.getData();
			int pos = 0;
			byte[] COUNT = new byte[2];
			byte[] DATADUMP = new byte[PLCDataConstants.JDATA_DUMP_TOTAL_LEN];
			pos=DataUtil.copyBytes(!PLCDataConstants.isConvert, pos, rawData, COUNT);
			dumpCnt=DataUtil.getIntToBytes(COUNT);
			//Check Length
			if(rawData.length!=(COUNT.length+dumpCnt*PLCDataConstants.JDATA_DUMP_TOTAL_LEN)){
				throw new Exception("JData LENGTH["+rawData.length+"] IS INVALID, CORRECT LENGTH["+(COUNT.length+dumpCnt*PLCDataConstants.JDATA_DUMP_TOTAL_LEN)+"]!");
			}
			jDataDump=new JDataDump[dumpCnt];
			for(int i=0;i<dumpCnt;i++) {
				pos=DataUtil.copyBytes(PLCDataConstants.isConvert, pos, rawData, DATADUMP);
				jDataDump[i] = new JDataDump(DATADUMP);
			}
		}catch (Exception e) {
			log.error("JDATA PARSING ERROR! ",e);
		}
	}

    /* (non-Javadoc)
	 * @see nuri.aimir.moa.protocol.fmp.frame.plc.PLCData#getServiceType()
	 */
	@Override
	public Integer getServiceType() {
		return new Integer(1);
	}



	/**
	 * @return the dumpCnt
	 */
	public int getDumpCnt() {
		return dumpCnt;
	}

	/**
	 * @param dumpCnt the dumpCnt to set
	 */
	public void setDumpCnt(int dumpCnt) {
		this.dumpCnt = dumpCnt;
	}

	/**
	 * @return the jDataDump
	 */
	public JDataDump[] getJDataDump() {
		return jDataDump;
	}

	/**
	 * @param jDataDump the jDataDump to set
	 */
	public void setJDataDump(JDataDump[] jDataDump) {
		this.jDataDump = jDataDump;
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

	    retValue.append("JData ( ")
	        .append(super.toString()).append(TAB)
	        .append("dumpCnt = ").append(this.dumpCnt).append(TAB);
	    for(int i=0;i<dumpCnt;i++) {
	        retValue.append("jDataDump = ").append(this.jDataDump[i]).append(TAB);
	    }
	        retValue.append(" )");

	    return retValue.toString();
	}
}
