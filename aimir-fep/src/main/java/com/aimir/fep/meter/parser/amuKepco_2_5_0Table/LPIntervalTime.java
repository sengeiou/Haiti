package com.aimir.fep.meter.parser.amuKepco_2_5_0Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Util;

/**
 * KEPCO v2.5.0 LP Interval Time
 *
 * @author  : taeho Park 
 * @version : $REV $1, 2010. 4. 12. 오후 5:15:52$
 */
public class LPIntervalTime {
	
	private Log log = LogFactory.getLog(LPIntervalTime.class);
	
	public static final int LP_TIME_YEAR = 0x00000FFF;
    public static final int LP_TIME_MON  = 0x0000F000;
    public static final int LP_TIME_DATE = 0x001F0000;
    public static final int LP_TIME_HOUR = 0x03E00000;
    public static final int LP_TIME_MIN  = 0xFC000000;
    
	private byte[] data;
   
	
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public LPIntervalTime(byte[] data) {
		this.data = data;
	}
	
    /**
     * LINE_MISSING
     */
    public String getIntervalTime() throws Exception  {
        int dataValue = (int)DataFormat.hex2dec(data);
        int year 	= (int) (dataValue&LP_TIME_YEAR);
        int month 	= ((int)(dataValue&LP_TIME_MON)) >>> 12;
        int date 	= ((int)(dataValue&LP_TIME_DATE) >>> 16);
        int hour 	= ((int)(dataValue&LP_TIME_HOUR) >>> 21);
        int min 	= ((int)(dataValue&LP_TIME_MIN)  >>> 26);

        StringBuffer ret = new StringBuffer();
        
        ret.append(Util.frontAppendNStr('0',Integer.toString(year),4));
        ret.append(Util.frontAppendNStr('0',Integer.toString(month),2));
        ret.append(Util.frontAppendNStr('0',Integer.toString(date),2));
        ret.append(Util.frontAppendNStr('0',Integer.toString(hour),2));
        ret.append(Util.frontAppendNStr('0',Integer.toString(min),2));
        
        return ret.toString();
    }
	    
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        try{
            sb.append("LPIntervalTime DATA[");        
            sb.append("(IntervalTime=").append(""+getIntervalTime()).append(')');
            sb.append("]\n");
        }catch(Exception e){
            log.warn("LPIntervalTime TO STRING ERR=>"+e.getMessage());
        }

        return sb.toString();
    }
}


