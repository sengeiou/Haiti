package com.aimir.fep.meter.parser.plc;

import com.aimir.fep.util.DataUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * 변압기 감시 Configuration Set
 * @author kaze
 * 2009. 6. 23.
 */
public class NDataRequest extends PLCData
{
    private static Log log = LogFactory.getLog(MDataRequest.class);
    public String tId;//변압기 전산화 번호
	public int capacity;//상별 용량(kVA)
	public int overVol;//과전압 임계치
	public int lowVol;//저전압 임계치
	public int overLoad;//과부하 단위
	public int volUnit;//전압 경보 단위
	public int period;//측정 주기(분)

    /**
     * @param sof
     * @param protocolDirection
     * @param protocolVersion
     * @param dId
     * @param sId
     * @param length
     * @param data(tId+capacity+overVol+lowVol+overLoad+volUnit+period)
     */
    public NDataRequest(byte sof, byte protocolDirection, byte protocolVersion, String dId, String sId, int length, String tId, int capacity, int overVol, int lowVol, int overLoad, int volUnit, int period) {
    	super(sof, protocolDirection, protocolVersion, dId, sId, length, PLCDataConstants.COMMAND_N,
    			DataUtil.append(DataUtil.getFixedLengthByte(tId, 9), DataUtil.append(DataUtil.get2ByteToInt(!PLCDataConstants.isConvert, capacity),
    			DataUtil.append(DataUtil.get2ByteToInt(!PLCDataConstants.isConvert, overVol), DataUtil.append(DataUtil.get2ByteToInt(!PLCDataConstants.isConvert, lowVol),
    	    	DataUtil.append(new byte[] {DataUtil.getByteToInt(overLoad)}, DataUtil.append(new byte[] {DataUtil.getByteToInt(volUnit)}, new byte[] {DataUtil.getByteToInt(period)})))))));
	}

    /* (non-Javadoc)
	 * @see nuri.aimir.moa.protocol.fmp.frame.plc.PLCData#getServiceType()
	 */
	@Override
	public Integer getServiceType() {
		return new Integer(1);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("NDataRequest [capacity=");
		builder.append(capacity);
		builder.append(", \\n lowVol=");
		builder.append(lowVol);
		builder.append(", \\n overLoad=");
		builder.append(overLoad);
		builder.append(", \\n overVol=");
		builder.append(overVol);
		builder.append(", \\n period=");
		builder.append(period);
		builder.append(", \\n tId=");
		builder.append(tId);
		builder.append(", \\n volUnit=");
		builder.append(volUnit);
		builder.append(']');
		return builder.toString();
	}


}
