package com.aimir.fep.meter.parser.plc;

import com.aimir.fep.util.DataUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Meter Status Request
 * @author kaze
 * 2009. 6. 23.
 */
public class IDataRequest extends PLCData
{
    private static Log log = LogFactory.getLog(IDataRequest.class);
    public String meterId;

    /**
     * @param sof
     * @param protocolDirection
     * @param protocolVersion
     * @param dId
     * @param sId
     * @param length
     * @param meterId : "" mean all meter
     */
    public IDataRequest(byte sof, byte protocolDirection, byte protocolVersion, String dId, String sId, int length, String meterId) {
    	super(sof, protocolDirection, protocolVersion, dId, sId, length, PLCDataConstants.COMMAND_I, DataUtil.getFixedLengthByte(meterId, PLCDataConstants.METER_ID_LENGTH));
	}

    /* (non-Javadoc)
	 * @see nuri.aimir.moa.protocol.fmp.frame.plc.PLCData#getServiceType()
	 */
	@Override
	public Integer getServiceType() {
		return new Integer(1);
	}
}
