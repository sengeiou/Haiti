package com.aimir.fep.meter.parser.plc;

import com.aimir.fep.util.DataUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Trap Info.(IR 통신 오류 Trap)
 * @author kaze
 * 2009. 6. 19.
 */
public class LData extends PLCData {
	/**
	 * @return the mt
	 */
	public int getMt() {
		return mt;
	}

	/**
	 * @param mt the mt to set
	 */
	public void setMt(int mt) {
		this.mt = mt;
	}

	/**
	 * @return the mId
	 */
	public String getmId() {
		return mId;
	}

	/**
	 * @param mId the mId to set
	 */
	public void setmId(String mId) {
		this.mId = mId;
	}

	/**
	 * @return the errorCode
	 */
	public int getErrorCode() {
		return errorCode;
	}

	/**
	 * @param errorCode the errorCode to set
	 */
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * @return the errCodeStr
	 */
	public String getErrCodeStr() {
		return errCodeStr;
	}

	/**
	 * @param errCodeStr the errCodeStr to set
	 */
	public void setErrCodeStr(String errCodeStr) {
		this.errCodeStr = errCodeStr;
	}

	private static Log log = LogFactory.getLog(LData.class);
	private int mt;
	private String mId;
	private int errorCode;
	private String errCodeStr="Unknown";

	public LData(PLCDataFrame pdf) {
		super(pdf);
		try {
			byte[] rawData = pdf.getData();
			//Check Length
			if(rawData.length!=PLCDataConstants.LDATA_TOTAL_LEN){
				throw new Exception("LData LENGTH["+rawData.length+"] IS INVALID!");
			}
			byte[] MT = new byte[1];
			byte[] MID = new byte[20];
			byte[] ERRORCODE = new byte[1];
			int pos = 0;
			pos=DataUtil.copyBytes(PLCDataConstants.isConvert, pos, rawData, MT);
			mt=DataUtil.getIntToBytes(MT);

			pos=DataUtil.copyBytes(PLCDataConstants.isConvert, pos, rawData, MID);
			mId=DataUtil.getString(MID).trim();

			pos=DataUtil.copyBytes(PLCDataConstants.isConvert, pos, rawData, ERRORCODE);
			errorCode=DataUtil.getIntToBytes(ERRORCODE);
			if(errorCode==0x01) {
				errCodeStr="PMU PSU CRC Error";
			}else if(errorCode==0x02) {
				errCodeStr="Meter Response Time Out";
			}
		}catch (Exception e) {
			log.error("LDATA PARSING ERROR! - "+e.getMessage());
			e.printStackTrace();
		}
	}

    /* (non-Javadoc)
	 * @see nuri.aimir.moa.protocol.fmp.frame.plc.PLCData#getServiceType()
	 */
	@Override
	public Integer getServiceType() {
		return new Integer(4);
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

	    retValue.append("LData ( ")
	        .append(super.toString()).append(TAB)
	        .append("mt = ").append(this.mt).append(TAB)
	        .append("mId = ").append(this.mId).append(TAB)
	        .append("errorCode = ").append(this.errorCode).append(TAB)
	        .append("errCodeStr = ").append(this.errCodeStr).append(TAB)
	        .append(" )");

	    return retValue.toString();
	}
}
