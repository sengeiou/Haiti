package com.aimir.fep.meter.parser.plc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataUtil;


/**
 * IRM  Configuration Set
 * @author kaze
 * 2009. 6. 23.
 */
public class CDataRequest extends PLCData
{
    private static Log log = LogFactory.getLog(CDataRequest.class);
    public String fepIp;
    public int fepPort;
    public String iTime;

    /**
     * @param sof
     * @param protocolDirection
     * @param protocolVersion
     * @param dId
     * @param sId
     * @param length
     * @param fepIp
     * @param fepPort
     * @param iTime
     */
    public CDataRequest(byte sof, byte protocolDirection, byte protocolVersion, String dId, String sId, int length, String fepIp, int fepPort , String iTime) {
    	super(sof, protocolDirection, protocolVersion, dId, sId, length, PLCDataConstants.COMMAND_C, DataUtil.append(DataUtil.append(DataUtil.getPLCIpByte(fepIp), DataUtil.getPLCPortByte(!PLCDataConstants.isConvert, fepPort)), DataUtil.getPLCDateByte(iTime)));
	}

    /* (non-Javadoc)
	 * @see nuri.aimir.moa.protocol.fmp.frame.plc.PLCData#getServiceType()
	 */
	@Override
	public Integer getServiceType() {
		return new Integer(1);
	}
}
