package com.aimir.fep.meter.parser.plc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataUtil;


/**
 * Not Acknowledge (FEP <--> IRM 공통)
 * @author kaze
 * 2009. 6. 23.
 */
public class AData extends PLCData
{
    private static Log log = LogFactory.getLog(AData.class);
    private int errorCode;
	private String errCodeStr="Unknown";

	/**
	 * @param sof
	 * @param protocolDirection
	 * @param protocolVersion
	 * @param dId
	 * @param sId
	 * @param length
	 * @param errCode
	 */
	public AData(byte sof, byte protocolDirection, byte protocolVersion, String dId, String sId, int length, byte[] errCode) {
    	super(sof, protocolDirection, protocolVersion, dId, sId, length, PLCDataConstants.COMMAND_a, errCode);
	}

	/**
	 * @param rawData
	 */
	public AData(PLCDataFrame pdf) {
		super(pdf);
		try {
			byte[] rawData = pdf.getData();
			//Check Length
			if(rawData.length!=PLCDataConstants.ADATA_TOTAL_LEN){
				throw new Exception("AData LENGTH["+rawData.length+"] IS INVALID!");
			}
			byte[] ERRORCODE = new byte[1];
			int pos = 0;
			pos=DataUtil.copyBytes(PLCDataConstants.isConvert, pos, rawData, ERRORCODE);
			errorCode=DataUtil.getIntToBytes(ERRORCODE);
			if(errorCode==PLCDataConstants.ERR_CODE_CRC) {
				errCodeStr="CRC16 Error";
			}else if(errorCode==PLCDataConstants.ERR_CODE_FRAME) {
				errCodeStr="Frame Error";
			}
		}catch (Exception e) {
			log.error("ADATA PARSING ERROR! - "+e.getMessage());
			e.printStackTrace();
		}
	}

    /* (non-Javadoc)
	 * @see nuri.aimir.moa.protocol.fmp.frame.plc.PLCData#getServiceType()
	 */
	@Override
	public Integer getServiceType() {
		return new Integer(1);
	}
}
