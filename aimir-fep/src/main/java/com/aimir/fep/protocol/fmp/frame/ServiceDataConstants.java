package com.aimir.fep.protocol.fmp.frame;

import com.aimir.fep.protocol.fmp.datatype.BYTE;

/**
 * ServiceData Constants
 * 
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
public class ServiceDataConstants
{
    public static byte SVC_CMD=(byte)'C';
    public static byte SVC_MMD=(byte)'M';
    public static byte SVC_NMD=(byte)'N';
    public static byte SVC_ALM=(byte)'A';
    public static byte SVC_EVT=(byte)'E';
    public static byte SVC_EVT2=(byte)'B';
    public static byte SVC_FTS=(byte)'F';
    public static byte SVC_TEL=(byte)'T';

    public static int HEADER_LEN = 4;

    // Command Service Constants
    public static BYTE C_ATTR_REQUEST = new BYTE((byte)0x81);
    public static BYTE C_ATTR_RESPONSE = new BYTE((byte)0x00);

    // Event Service Constants
    public static BYTE E_SRCTYPE_UNKNOWN = new BYTE((byte)0x00);  // 0 : 알 수 없음
    public static BYTE E_SRCTYPE_FEP = new BYTE((byte)0x01);   // 1 : FEP
    public static BYTE E_SRCTYPE_MCU = new BYTE((byte)0x02);   // 2 : Data Concentrator
    public static BYTE E_SRCTYPE_OAMPC = new BYTE((byte)0x03); // 3 : OAM PC (SERIAL)
    public static BYTE E_SRCTYPE_OAMPDA = new BYTE((byte)0x04); // 4 : OAM PDA (RF)
    public static BYTE E_SRCTYPE_MOBILE = new BYTE((byte)0x05);  // 5 : Mobile unit (GSM/CDMA)
    public static BYTE E_SRCTYPE_MASTER_MODEM = new BYTE((byte)0x06);  // 6: Master modem
    public static BYTE E_SRCTYPE_ZRU = new BYTE((byte)0x07);  // 7 : RF - Router modem
    public static BYTE E_SRCTYPE_ZMU = new BYTE((byte)0x08);  // 8 : RF - End modem
    public static BYTE E_SRCTYPE_ZEU = new BYTE((byte)0x09);  // 9 : RF - Expansion unit 
    public static BYTE E_SRCTYPE_UNIT = new BYTE((byte)0x0A); // 10 : 
    public static BYTE E_SRCTYPE_MMIU = new BYTE((byte)0x0B);  // 11 : Iraq, vietnam MMIU 고압모뎀
    public static BYTE E_SRCTYPE_CODI = new BYTE((byte)0x0C);  // 12 
    public static BYTE E_SRCTYPE_IEIU = new BYTE((byte)0x0D);  // 13   
    public static BYTE E_SRCTYPE_PLC = new BYTE((byte)0x0E);   // 14 
    public static BYTE E_SRCTYPE_MMIU2 = new BYTE((byte)0x0F);  // 15 : Ghana GPRS 모뎀
    public static BYTE E_SRCTYPE_SERIAL = new BYTE((byte)0x10); // 16
    public static BYTE E_SRCTYPE_CONVERTER = new BYTE((byte)0x13);  // 19
    public static BYTE E_SRCTYPE_SUBGIGA = new BYTE((byte)0x65); // 101

    /**
     * constructor
     */
    public ServiceDataConstants()
    {
    }
    
    public enum EventSRCType {
        UnKnown(0),
        FEP(1),
        Data_Concentrator(2),
        OAM_PC_SERIAL(3),
        OAM_PC_RF(4),
        Mobile_Unit_GSM_CDMA(5),
        Master_Modem(6),
        RF_Router_modem(7),
        RF_End_modem(8),
        RF_Expansion_unit(9),
        UNIT(10),
        MMIU(11),
        CODI(12),
        IEIU(13),
        PLC_modem(14),
        MMIU2(15),
        Serial_modem_RS232_RS485(16),
        CONVERTER(19),
        SUBGIGA(101);    
        
        private int code;
        
        private EventSRCType(int code) {
            this.code = code;
        }
        
        public static EventSRCType getCode(int value) {
        	for(EventSRCType type : EventSRCType.values()) {
        		if(type.code == value) {
        			return type;
        		}
        	}
        	return null;
        }
    }
}
