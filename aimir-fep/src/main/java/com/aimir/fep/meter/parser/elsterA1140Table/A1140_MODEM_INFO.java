package com.aimir.fep.meter.parser.elsterA1140Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Hex;

/**
 * 
 * @author choiEJ
 *
 */
public class A1140_MODEM_INFO {    
	private Log log = LogFactory.getLog(A1140_MODEM_INFO.class);
    
    public static final int OFS_FW_VERSION    = 0;
    public static final int OFS_FW_BUILD      = 1;
    public static final int OFS_HW_VERSION 	  = 2;
    public static final int OFS_SIM_NUMBER 	  = 3;
    public static final int OFS_MODULE_TIME   = 23;
    public static final int OFS_MODEM_RSSI    = 30;
    public static final int OFS_MODEM_BER     = 31;
    public static final int OFS_MODEM_STATUS  = 32;
    
    public static final int LEN_FW_VERSION 	  = 1;
    public static final int LEN_FW_BUILD      = 1;
    public static final int LEN_HW_VERSION 	  = 1;
    public static final int LEN_SIM_IMSI 	  = 20;
    public static final int LEN_MODULE_TIME   = 7;
    public static final int LEN_MODEM_CSQ     = 1;
    public static final int LEN_MODEM_STATUS  = 1;
    
	private byte[] rawData = null;
	
	private String fwVersion; 
	
	private String fwBuild;
	
	private String hwVersion;
    
	private String simNumber;
	
	private String moduleTime;
	
	private int rssi;
	
	private int ber;
	
	private int modemStatus;
	
	/**
	 * Constructor
	 */
	public A1140_MODEM_INFO(byte[] rawData) {
        this.rawData = rawData;
        
        try {
            parse();
        }
        catch (Exception e) {
            log.error(e, e);
        }
	}
	
	private void parse() throws Exception {
	    fwVersion = Hex.decode(DataFormat.select(rawData, OFS_FW_VERSION, LEN_FW_VERSION));
        fwVersion = Double.parseDouble(fwVersion) * 0.1 + "";
        log.debug("FW_VERSION=[" + fwVersion + "]");
        
        fwBuild = Hex.decode(DataFormat.select(rawData, OFS_FW_BUILD, LEN_FW_BUILD));
        log.debug("FW_BUILD=[" + fwBuild + "]");
        
        hwVersion = Hex.decode(DataFormat.select(rawData, OFS_HW_VERSION, LEN_HW_VERSION));
        hwVersion = Double.parseDouble(hwVersion) * 0.1 + "";
        log.debug("HW_VERSION=[" + hwVersion + "]");
        
        simNumber = Hex.decode(DataFormat.select(rawData, OFS_SIM_NUMBER, LEN_SIM_IMSI));
        log.debug("SIM_NUMBER=[" + simNumber + "]");
        
        byte[] time = DataFormat.select(rawData, OFS_MODULE_TIME, LEN_MODULE_TIME);

        int year  = DataFormat.getIntToBytes(DataFormat.select(time, 0, 2));
        int month = DataFormat.getIntToByte(time[2]);
        int day   = DataFormat.getIntToByte(time[3]);
        int hour  = DataFormat.getIntToByte(time[4]);
        int min   = DataFormat.getIntToByte(time[5]);
        int sec   = DataFormat.getIntToByte(time[6]);
        
        moduleTime = year + "" + 
                           (month < 10? "0"+month : month) + 
                           (day < 10? "0"+day : day) +
                           (hour < 10? "0"+hour : hour) +
                           (min < 10? "0"+min : min) +
                           (sec < 10? "0"+sec : sec);
        
        log.debug("MODULE_TIME=[" + moduleTime + "]");
        
        rssi = DataFormat.getIntToBytes(DataFormat.select(rawData, OFS_MODEM_RSSI, LEN_MODEM_CSQ));
        log.debug("RSSI=[" + rssi + "]");
        
        ber = DataFormat.getIntToBytes(DataFormat.select(rawData, OFS_MODEM_BER, LEN_MODEM_CSQ));
        log.debug("BER=[" + ber + "]");
        
        modemStatus = DataFormat.getIntToBytes(DataFormat.select(rawData, OFS_MODEM_STATUS, LEN_MODEM_STATUS));
        
        if (modemStatus == 0xF0) {
            modemStatus = 6;            // Power Fail = 0xF0
        }
        
        log.debug("MODEM_STATUS=[" + modemStatus + "]");
        
	}
	
	public static void main(String args[]) {
		A1140_TEST_DATA testData = new A1140_TEST_DATA();
		A1140_MODEM_INFO elster = new A1140_MODEM_INFO(testData.getTestData_modem());
    	System.out.println(elster.toString());
	}
	
    public String getFwVerion() throws Exception {
		return fwVersion;
	}
    
    public String getFwBuild() throws Exception {
		return fwBuild;
	}

	public String getHwVersion() throws Exception {
		return hwVersion;
	}

//	public String getModuleSerial() throws Exception {
//		String moduleSerial = Hex.decode(DataFormat.select(rawData, OFS_MODULE_SERIAL, LEN_MODULE_SERIAL));
//		moduleSerial = moduleSerial.substring(1);	// 모뎀에서 123456789012345 -> 0123456789012345 로 보냄
//		log.debug("MODULE_SERIAL=[" + moduleSerial + "]");
//		
//		return moduleSerial;
//	}

	public String getSimIMSI() throws Exception {
		return simNumber;
	}

	public String getModuleTime() throws Exception {
		return moduleTime;
	}

	public int getRSSI() throws Exception {
		return rssi;
	}
	
	public int getBER() throws Exception {
		return ber;
	}
	
	public int getModemStatus() throws Exception {
		return modemStatus;
	}

	public String toString() {
        StringBuffer sb = new StringBuffer();
        
        try {
            sb.append("A1140_MODEM_INFO[\n")
              .append("  (FW_VERSION="   ).append(getFwVerion()).append("),\n")
              .append("  (FW_BUILD="     ).append(getFwBuild()).append("),\n")
              .append("  (HW_VERSION="   ).append(getHwVersion()).append("),\n")
//              .append("  (MODULE_SERIAL=").append(getModuleSerial()).append("),\n")
              .append("  (SIM_NUMBER="   ).append(getSimIMSI()).append("),\n")
              .append("  (MODULE_TIME="  ).append(getModuleTime()).append("),\n")
              .append("  (MODEM_RSSI="   ).append(getRSSI()).append("),\n")
              .append("  (MODEM_BER="    ).append(getBER()).append("),\n")
              .append("  (MODEM_STATUS=" ).append(getModemStatus()).append(")\n")
              .append("]\n");
        } catch (Exception e) {
            log.error("A1700_MODEM_INFO ERR => " + e.getMessage(),e);
        }
        return sb.toString();
    }
}
