package com.aimir.fep.protocol.fmp.frame.service.entry;

import com.aimir.fep.protocol.fmp.datatype.BYTE;
import com.aimir.fep.protocol.fmp.datatype.INT;
import com.aimir.fep.protocol.fmp.datatype.TIMESTAMP;
import com.aimir.fep.protocol.fmp.frame.service.Entry;

/**
 * trHistoryEntry
 * generated by MIB Tool, Do not modify
 *
 * @author J.S Park (elevas@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2009-04-16 10:21:15 +0900 $,
 */
public class trHistoryEntry extends Entry {
    public TIMESTAMP trhTime = new TIMESTAMP(7);
    public BYTE trhState = new BYTE(1);
    public INT trhError = new INT();

    public String toString()
    {
        StringBuffer sb = new StringBuffer();

		sb.append("CLASS["+this.getClass().getName()+"]\n");
		sb.append("trhTime: " + trhTime + "\n");
		sb.append("trhState: " + trhState + "\n");
		sb.append("trhError: " + trhError + "\n");

        return sb.toString();
    }
}
