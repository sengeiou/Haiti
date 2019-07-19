package com.aimir.fep.meter.parser.amuKmpMc601Table;
/**
 * Units Table
 *
 * @author  : taeho Park 
 * @version : $REV $1, 2010. 3. 18. 오전 11:01:08$
 */
public class UnitsTable {
	
	public final static int Wh 			= 1;
    public final static int kWh 		= 2;
    public final static int MWh 		= 3;
    public final static int Gj 			= 8;
    public final static int Gcal 		= 12;
    public final static int kW 			= 22;
    public final static int MW 			= 23;
    public final static int C 			= 37;
    public final static int K 			= 38;
    public final static int l 			= 39;
    public final static int m3 			= 40;
    public final static int l_h 		= 41;
    public final static int m3_h 		= 42;
    public final static int m3xC 		= 43;
    public final static int ton 		= 44;
    public final static int ton_h 		= 45;
    public final static int h 			= 46;
    public final static int clock 		= 47;
    public final static int date1 		= 48;
    public final static int date3 		= 50;
    public final static int number 		= 51;
    public final static int bar 		= 52;

    public static String getUnitName(int unit){
    	
    	switch(unit){
    	case Wh: 		return "Wh";
    	case kWh: 		return "kWh";
    	case MWh: 		return "MWh";
    	case Gj: 		return "Gj";
    	case Gcal: 		return "Gcal";
    	case kW: 		return "kW";
    	case MW: 		return "MW";
    	case C: 		return "C";
    	case K: 		return "K";
    	case l: 		return "l";
    	case m3: 		return "m3";
    	case l_h: 		return "l/h";
    	case m3_h: 		return "m3/h";
    	case m3xC: 		return "m3xC";
    	case ton: 		return "ton";
    	case ton_h: 	return "ton/h";
    	case h: 		return "h";
    	case clock: 	return "hh:mm:ss";
    	case date1: 	return "yy:mm:dd";
    	case date3: 	return "mm:dd";
    	case number: 	return "number";
    	case bar:  		return "bar";
    	}
    	return "Unknown ["+unit+"]";
    }
}


