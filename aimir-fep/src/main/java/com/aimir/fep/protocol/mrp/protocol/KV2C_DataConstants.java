package com.aimir.fep.protocol.mrp.protocol;

/**
 * RequestData Constants
 * 
 * @author Yeon Kyoung Park
 * @version $Rev: 1 $, $Date: 2008-01-05 15:59:15 +0900 $,
 */
public class KV2C_DataConstants
{
    public static final int CMD_ALL = 0;
    public static final int CMD_LCB = 1;//lp&current billing
    public static final int CMD_LPB = 2;//lp&previous billing
    public static final int CMD_EVT = 10;//event
    public static final int CMD_CHANNELINFO = 12;//lp channel info
    /**
     * Packet Definition
     * <stp><ctrl><seq_nbr><length><data><crc>
     */
    public static final char READ_MT_00  = 0x0800;  //GE Device table
    public static final char READ_MT_13  = 0x080D;  //Phase Angle Multiplies
    public static final char READ_MT_64  = 0x0840;  //meter configuration constants table
    public static final char READ_MT_65  = 0x0841;  //instrumentation
    public static final char READ_MT_85  = 0x0855;  //meter status table
    public static final char READ_MT_67  = 0x0843;  //meter program constants 2 table
    public static final char READ_MT_70  = 0x0846;  //display configuration table
    public static final char READ_MT_72  = 0x0848;  //power quality data table instrument, monitor count
    public static final char READ_MT_75  = 0x084B;  //instrumentation scale factor
    public static final char READ_MT_78  = 0x084E;  //Security log (cum power outage)
    public static final char READ_MT_86  = 0x0856;  //meter service
    public static final char READ_MT_87  = 0x0857;  //meter element
    public static final char READ_MT_110 = 0x086E;  //Present Register Data Table
    public static final char READ_MT_111 = 0x086F;  //Voltage Event Monitor Configuration
    public static final char READ_MT_112 = 0x0870;  //Voltage Event Monitor Log
    
    public static final int LEN_MT_00  = 59;
    public static final int LEN_MT_13  = 12;
    public static final int LEN_MT_64  = 73;
    public static final int LEN_MT_65  = 32;
    public static final int LEN_MT_72  = 37;
    public static final int LEN_MT_78  = 113;
    public static final int LEN_MT_85  = 4;
    public static final int LEN_MT_87  = 9;
    public static final int LEN_MT_110 = 167;
    public static final int LEN_MT_111 = 5;
    public static final int LEN_MT_112 = 4615;//max 4615

    public static long SLEEPTIME = 1 * 1000;

    /**
     * constructor
     */
    public KV2C_DataConstants()
    {
    }
}
