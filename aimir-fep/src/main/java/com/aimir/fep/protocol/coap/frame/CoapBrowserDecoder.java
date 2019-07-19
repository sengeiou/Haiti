package com.aimir.fep.protocol.coap.frame;

import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;

public class CoapBrowserDecoder {
	 public static String decode(String uri, String rtnStr) throws Exception{
		 byte[] data;
		 data = Hex.encode(rtnStr);
		 String result="";
		 
		 switch(uri){
		 	case "/3/u_t":
		 	case "/time/utc_time":
		 	case "/4/l_up_t":
		 	case "/4/l_comm_t":
		 	case "/4/c_a_power_t":
		     	 result="Time: " + String.format("%04d%02d%02d%02d%02d%02d", 
		     		   DataUtil.getIntTo2Byte(new byte[]{data[0], data[1]}),
	                   DataUtil.getIntToByte(data[2]),
	                   DataUtil.getIntToByte(data[3]),
	                   DataUtil.getIntToByte(data[4]),
	                   DataUtil.getIntToByte(data[5]),
	                   DataUtil.getIntToByte(data[6]));
		 		break;
		 	case "/3/t_z":
		 	case "/time/time_zone": 
		 		result = "Time Zone: "+DataUtil.getIntToByte(data[0]);
		 		break;
		 	case "/4/m_ser":
		 		result ="Meter Serial Number: " + DataUtil.getString(data);
		 		break;
		 	case "/4/m_m": 
		 		result ="Meter Manufacture Number: " + DataUtil.getString(data);
		 		break;
		 	case "/4/c_n": 
		 		result ="Customer Number: " + DataUtil.getString(data);
		 		break;
		 	case "/4/m_n":
		 		result ="Model Name: " + DataUtil.getString(data);
		 		break;
		 	case "/4/hw_ver": 
		 		result ="HW Version: " + DataUtil.getString(data);
		 		break;
		 	case "/4/sw_ver":
		 		result ="SW Version: " + rtnStr;
		 		result ="OBIS Tag: " + rtnStr.substring(0,2) + "\n" +
				 		"SW Version: " + rtnStr.substring(2,8);
		 		break;
		 	case "/4/m_s":  
		 		String meterStatus="";
		 		String bit0 =  DataUtil.getBit(data[1]);
		 		String bit1 =  DataUtil.getBit(data[2]);
		 		String bit2 =  DataUtil.getBit(data[3]);
		 		
		 	    if(bit0.substring(0,1).equals("1"))
		 	    	meterStatus += "Clock invalid\n";
		 	    if(bit0.substring(1,2).equals("1"))
		 	    	meterStatus += "Replace battery\n";
		 	    if(bit0.substring(2,3).equals("1"))
		 	    	meterStatus += "Power up\n";
		 	    if(bit0.substring(3,4).equals("1"))
		 	    	meterStatus += "L1 error\n";
		 	    if(bit0.substring(4,5).equals("1"))
		 	    	meterStatus += "L2 error\n";
		 	    if(bit0.substring(5,6).equals("1"))
		 	    	meterStatus += "L3 error\n";
		 	    
		 	    if(bit1.substring(0,1).equals("1"))
		 	    	meterStatus += "Program memory error\n";
		 	    if(bit1.substring(1,2).equals("1"))
		 	    	meterStatus += "RAM error\n";
		 	    if(bit1.substring(2,3).equals("1"))
		 	    	meterStatus += "NV memory error\n";
		 	    if(bit1.substring(3,4).equals("1"))
		 	    	meterStatus += "Watchdog error\n";
		 	    if(bit1.substring(4,5).equals("1"))
		 	    	meterStatus += "Fraud attempt\n";
		 	    
		 	   if(bit2.substring(0,1).equals("1"))
		 	    	meterStatus += "Communication error M-BUS\n";
		 	    if(bit2.substring(1,2).equals("1"))
		 	    	meterStatus += "New M-BUS device discovered\n";
		 	    
		 		result += "OBIS Tag: " + rtnStr.substring(0,2) + "\n"
		 		+ "Meter Status: \n" +meterStatus;
		 		break;
		 	case "/4/lp_ch_cnt": 
		 		result ="LP Channel Count: " + DataUtil.getIntToByte(data[0]);
		 		break;
		 	case "/4/lp_in":
		 		result ="OBIS Tag: " + rtnStr.substring(0,2) + "\n" +
		 		"LP Interval: " + DataUtil.getIntTo4Byte(new byte[]{data[1], data[2], data[3], data[4]}) + "(Seconds)";
		 		break;
		 	case "/4/c_a_power": 
		 		result ="OBIS Tag: " + rtnStr.substring(0,2) + "\n" +
		 		"Cumulative Active Power: " + DataUtil.getIntTo4Byte(new byte[]{data[1], data[2], data[3], data[4]});
		 		break;
		 	case "/5/ct": 
		 		result ="OBIS Tag: " + rtnStr.substring(0,2) + "\n" +
		 		"CT: " + DataUtil.getIntTo4Byte(new byte[]{data[1], data[2], data[3], data[4]});
		 		break;
		 	case "/5/pt": 
		 		result ="OBIS Tag: " + rtnStr.substring(0,2) + "\n" +
		 		"PT: " + DataUtil.getIntTo4Byte(new byte[]{data[1], data[2], data[3], data[4]});
		 		break;
		 	case "/5/t_r": 
		 		result ="OBIS Tag: " + rtnStr.substring(0,2) + "\n" +
		 		"Transformer Ratio: " + DataUtil.getIntTo4Byte(new byte[]{data[1], data[2], data[3], data[4]});
		 		break;
		 	case "/5/p_c": 
		 		// 미터에서 지원 X
		 		result ="Phase configuration: " + "Not supported by meter";
		 		break;
		 	case "/5/s_s": 
		 		int switchStatus = DataUtil.getIntToByte(data[1]);
		 		if(switchStatus == 0){
		 			result = "OBIS Tag: " + rtnStr.substring(0,2) + "\n" +
		 			"Switch Status: " + "Switch Off";
		 		}else{
		 			result = "OBIS Tag: " + rtnStr.substring(0,2) + "\n" +
				 			"Switch Status: " + "Switch On";
		 		}
		 		break;
		 	case "/5/fre":   //이거 고쳐야됭
		 		result = "OBIS Tag: " + rtnStr.substring(0,2) + "\n" +
		 		"Frequency: " + DataUtil.getIntTo4Byte(new byte[]{data[1], data[2], data[3], data[4]}); 
		 		break;
		 	case "/5/va_sf": 
		 		result = "OBIS Tag: " + rtnStr.substring(0,2) + "\n" +
		 		"VA_SF: " + DataUtil.getIntTo4Byte(new byte[]{data[1], data[2], data[3], data[4]});
		 		break;
		 	case "/5/vah_sf": 
		 		result ="OBIS Tag: " + rtnStr.substring(0,2) + "\n" +
		 		"VAH_SF: " + DataUtil.getIntTo4Byte(new byte[]{data[1], data[2], data[3], data[4]});
		 		break;
		 	case "/5/disp_s": 
		 		result ="Display Scalar: " + DataUtil.getIntToByte(data[0]);
		 		break;
		 	case "/5/disp_m": 
		 		result ="Display Multiplier : " + DataUtil.getIntToByte(data[0]);
		 		break;
		 	case "/6/p_p_t": 
		 		int pst = DataUtil.getIntToByte(data[0]);
		 		String ptype ="";
		 		if(pst == 0)
		 			ptype = "Unknown";
		 		else if(pst == 1)
		 			ptype = "Electric";
		 		else if(pst == 2)
		 			ptype = "Battery";
		 		else if(pst == 3)
		 			ptype = "Solar";
		 		else if(pst == 4)
		 			ptype = "SuperCap";
		 		result ="Primary Power Source Type : " + pst + "("+ptype+")";
		 		break;
		 	case "/6/s_p_t": 
		 		int sst = DataUtil.getIntToByte(data[0]);
		 		String stype ="";
		 		if(sst == 0)
		 			stype = "Unknown";
		 		else if(sst == 1)
		 			stype = "Electric";
		 		else if(sst == 2)
		 			stype = "Battery";
		 		else if(sst == 3)
		 			stype = "Solar";
		 		else if(sst == 4)
		 			stype = "SuperCap";
		 		result ="Secondary Power Source Type : " + sst + "("+stype+")";
		 		break;
		 	case "/6/r_cnt": 
		 		result ="Reset Count : " + DataUtil.getIntTo2Byte(data);
		 		break;
		 	case "/6/r_r": 
		 		String reason ="";
		 		if(rtnStr.substring(0,1).equals("1"))
		 			reason = "Other";
		 		else if(rtnStr.substring(1,2).equals("1"))
		 			reason = "Low power";
		 		else if(rtnStr.substring(2,3).equals("1"))
		 			reason = "WWDOG";
		 		else if(rtnStr.substring(3,4).equals("1"))
		 			reason = "IWDOG";
		 		else if(rtnStr.substring(4,5).equals("1"))
		 			reason = "Software";
		 		else if(rtnStr.substring(5,6).equals("1"))
		 			reason = "POR/PDR";
		 		else if(rtnStr.substring(6,7).equals("1"))
		 			reason = "Pin";
		 		else if(rtnStr.substring(7,8).equals("1"))
		 			reason = "POR/PDR or BOR";
		 		result = "Reason: " + rtnStr + "(" + reason  + ")";
		 		break;
		 	case "/6/o_t": 
		 		result ="Operation Time: " + DataUtil.getIntTo4Byte(data) + "(seconds)";
		 		break;
		 	case "/6/r_sch": 
		 		int hours = DataUtil.getIntToByte(data[0]);
		 		if(hours != 255)
		 			result ="Reset Schedule: " + hours + "(hour)";
		 		else
		 			result ="Reset Schedule: " +"(None)";
		 		break;
		 	case "/6/n_m_s":
		 		if(data.length == 7){
		 			result += "CPU usage: " + DataUtil.getIntToByte(data[0])+"% \n";
		 			result += "Memory Usage: " + DataUtil.getIntToByte(data[1])+"% \n";
		 			result += "Total TX Size: " + DataUtil.getIntTo4Byte(new byte[]{data[2], data[3], data[4], data[5]})+"(Bytes) \n";
		 			int network = DataUtil.getIntToByte(data[6]);
		 			if(network == 0)
		 				result += "Network: " + network + " (GSM / 2G)" ;
		 			else if(network == 2)
		 				result += "Network: " + network + " (UTRAN / 3G)" ;
		 			else if(network == 3)
		 				result += "Network: " + network + " (GSM w/EGPRS / 2G)" ;
		 			else if(network == 4)
		 				result += "Network: " + network + " (UTRAN w/HSDPA / 3G)" ;
		 			else if(network == 5)
		 				result += "Network: " + network + " (UTRAN w/HSUPA / 3G)" ;
		 			else if(network == 6)
		 				result += "Network: " + network + " (UTRAN w/HSDPA and HSUPA / 3G)" ;
		 			else if(network == 7)
		 				result += "Network: " + network + " (E-UTRAN / 4G)" ;
		 		}else if(data.length == 6){
		 			result += "CPU usage: " + DataUtil.getIntToByte(data[0])+"% \n";
		 			result += "Memory Usage: " + DataUtil.getIntToByte(data[1])+"% \n";
		 			result += "Total TX Size: " + DataUtil.getIntTo4Byte(new byte[]{data[2], data[3], data[4], data[5]})+"(Bytes) \n";
		 		}else if(data.length == 18){
		 			result += "Parent Node ID: " + rtnStr.substring(0,16) + "\n";
		 			 if(DataUtil.getIntToByte(data[8]) > 127)
		    	        	result += "RSSI: " + (256 - DataUtil.getIntToByte(data[8])) * -1 + "\n";
		    	        else
		    	        	result += "RSSI: " +  DataUtil.getIntToByte(data[8]) + "\n";
		 			 
		 			result += "LQI: " + DataUtil.getIntToByte(data[9])+"% \n";
		 			
		 			if(DataUtil.getIntTo2Byte(new byte[]{data[10], data[11]}) >= 100)
		 				result += "ETX: " +  "0x" + DataUtil.getIntTo2Byte(new byte[]{data[10], data[11]}) + "\n";
		 			else
		 				result += "ETX: " + DataUtil.getIntTo2Byte(new byte[]{data[10], data[11]}) + "\n";
		 			result += "CPU usage: " + DataUtil.getIntToByte(data[12])+"% \n";
		 			result += "Memory Usage: " + DataUtil.getIntToByte(data[13])+"% \n";
		 			result += "Total TX Size: " + DataUtil.getIntTo4Byte(new byte[]{data[14], data[15], data[16], data[17]})+"(Bytes) \n";
		 		}
		 		break;
		 	case "/dcu_info/model_name": 
		 		result ="Model Name: " + DataUtil.getString(data);
		 		break;
		 	case "/dcu_info/hw_version": 
		 		result ="HW Version: " + DataUtil.getString(data);
		 		break;
		 	case "/dcu_info/sw_version": 
		 		result ="OBIS Tag: " + rtnStr.substring(0,2) + "\n" +
				 		"SW Version: " + rtnStr.substring(2,8);
		 		break;
		 	case "/dcu_info/id": 
		 		result ="DCU ID: " + DataUtil.getString(data);
		 		break;
		 	case "/comm_interface/main_type": 
		 		int type = DataUtil.getIntToByte(data[0]);
		 		if(type == 0)
		 			result ="Type: " + type + "(Ethernet)";
		 		else if(type == 1)
		 			result ="Type: " + type + "(Mobile)";
		 		else 
		 			result ="Type: " + type + "(Other)";
		 		break;
		 	case "/comm_interface/sub_type": 
		 		int subType = DataUtil.getIntToByte(data[0]);
		 		if(subType == 0)
		 			result ="Type: " + subType + "(ZigBee)";
		 		else if(subType == 1)
		 			result ="Type: " + subType + "(PLC)";
		 		else if(subType == 2)
		 			result ="Type: " + subType + "(SubGiga)";
		 		else 
		 			result ="Type: " + subType + "(Other)";
		 		break;
		 	case "/ether_interface/main_ip_address" :
		 	case "/9/m_ip_addr" :  //이것도
		 	    if(data.length == 4){
		 	    	result ="Main IP Address: " + 
			 		DataUtil.getIntToByte(data[0]) + "." +
			 		DataUtil.getIntToByte(data[1]) + "." +
			 		DataUtil.getIntToByte(data[2]) + "." +
			 		DataUtil.getIntToByte(data[3]);
		 	    }else if(data.length == 16){
		 	    	result ="Main IP Address: " + rtnStr.substring(0,4) + ":"
					 		+ rtnStr.substring(4,8) + ":" 
					 		+ rtnStr.substring(8,12) + ":" 
					 		+ rtnStr.substring(12,16) + ":" 
					 		+ rtnStr.substring(16,20) + ":" 
					 		+ rtnStr.substring(20,24) + ":" 
					 		+ rtnStr.substring(24,28) + ":" 
					 		+ rtnStr.substring(28,32);
		 	    }else{
		 	    	result = DataUtil.getString(data);
		 	    }
		 		break;
		 	case "/ether_interface/main_port_number": 
		 	case "/9/m_port_num":
		 		result ="Main Port Number: " + DataUtil.getIntTo2Byte(data);
		 		break;
		 	case "/mobile_interface/mobile_type":
		 	case "/10/m_t":
		 		int mType = DataUtil.getIntToByte(data[0]);
		 		if(mType == 0)
		 			result ="Mobile Type: " + mType + "(GSM)";
		 		else if(mType == 1)
		 			result ="Mobile Type: " + mType + "(GSM Compact)";
		 		else if(mType == 2)
		 			result ="Mobile Type: " + mType + "(UTRAN)";
		 		else if(mType == 3)
		 			result ="Mobile Type: " + mType + "(GSM w/EGPRS)";
		 		else if(mType == 4)
		 			result ="Mobile Type: " + mType + "(UTRAN w/HSDPA)";
		 		else if(mType == 5)
		 			result ="Mobile Type: " + mType + "(UTRAN w/HSUPA)";
		 		else if(mType == 6)
		 			result ="Mobile Type: " + mType + "(UTRAN w/HSDPA and HSUPA)";
		 		else if(mType == 7)
		 			result ="Mobile Type: " + mType + "(E-UTRAN)";
		 		break;
		 	case "/mobile_interface/mobile_id":
		 	case "/10/m_i":
		 		result ="IMEI: " + DataUtil.getString(data);
		 		break;
		 	case "/mobile_interface/imsi":
		 	case "/8/imsi":
		 		result ="IMSI: " + DataUtil.getString(data);
		 		break;
		 	case "/mobile_interface/mobile_number":
		 	case "/10/m_n":
		 		result ="MSISDN: " + DataUtil.getString(data);
		 		break;
		 	case "/mobile_interface/mobile_mode":
		 	case "/10/m_m":	
		 		int mode = DataUtil.getIntToByte(data[0]);
		 		if(mode == 0)
		 			result ="Mobile Mode: " + mode + "(CSD)";
		 		else if(mode == 1)
		 			result ="Mobile Mode: " + mode + "(Packet)";
		 		else if(mode == 2)
		 			result ="Mobile Mode: " + mode + "(Always On)";
		 		else
		 			result ="Mobile Mode: " + mode + "(Unkown)";
		 		break;
		 	case "/mobile_interface/apn":
		 	case "/10/apn":
		 		result ="APN: " + DataUtil.getString(data); 
		 		break;
		 	case "/mobile_interface/id":
		 	case "/10/id":
		 		result ="APN ID: " + DataUtil.getString(data); 
		 		break;
		 	case "/mobile_interface/password":
		 	case "/10/pwd":
		 		result ="APN password: " + DataUtil.getString(data); 
		 		break;
		 	case "/mobile_interface/ip_address": 
		 	case "/10/ip_addr": 
		 		if(data.length == 4){
		 	    	result ="IP Address: " + 
			 		DataUtil.getIntToByte(data[0]) + "." +
			 		DataUtil.getIntToByte(data[1]) + "." +
			 		DataUtil.getIntToByte(data[2]) + "." +
			 		DataUtil.getIntToByte(data[3]);
		 	    }else{
		 	    	result = DataUtil.getString(data);
		 	    }
		 		break;
		 	case "/mobile_interface/read_current_network_status":
		 	case "/10/r_c_n_s":
		 		result ="Read Current Network Status: " + DataUtil.getString(data);
		 		break;
		 	case "/mobile_interface/connection_status":
		 	case "/10/c_s":
		 		int status = DataUtil.getIntToByte(data[0]);
		 		if(status == 0)
		 			result ="Connection Status: " + status + "(disconnected)" ;
		 		else if(status == 1)
		 			result ="Connection Status: " + status + "(connected)" ;
		 		else
		 			result ="Connection Status: " + status + "(Unkown)" ;
		 		break;
		 	case "/11/i_i6_addr": //해봐야함
		 		if(data.length == 16){
			 		result ="DCU IP: " + rtnStr.substring(0,4) + ":"
			 		+ rtnStr.substring(4,8) + ":" 
			 		+ rtnStr.substring(8,12) + ":" 
			 		+ rtnStr.substring(12,16) + ":" 
			 		+ rtnStr.substring(16,20) + ":" 
			 		+ rtnStr.substring(20,24) + ":" 
			 		+ rtnStr.substring(24,28) + ":" 
			 		+ rtnStr.substring(28,32);
		 		}else
		 			result ="DCU IP: " + DataUtil.getString(data);
		 		break;
		 	case "/11/i6_addr": //해봐야함
		 		if(data.length == 16){
			 		result ="IPv6 address: " + rtnStr.substring(0,4) + ":"
			 		+ rtnStr.substring(4,8) + ":" 
			 		+ rtnStr.substring(8,12) + ":" 
			 		+ rtnStr.substring(12,16) + ":" 
			 		+ rtnStr.substring(16,20) + ":" 
			 		+ rtnStr.substring(20,24) + ":" 
			 		+ rtnStr.substring(24,28) + ":" 
			 		+ rtnStr.substring(28,32);
		 		}else
		 			result ="IPv6 address: " + DataUtil.getString(data);
		 		break;
		 	case "/11/i_l_port": 
		 		result ="Interface Listen Port: " + DataUtil.getIntTo2Byte(data);
		 		break;
		 	case "/11/n_l_port": 
		 		result ="Network Listen Port: " + DataUtil.getIntTo2Byte(data);
		 		break;
		 	case "/11/fre": 
		 		result ="Start Frequency: " + 
		 	    DataUtil.getIntTo2Byte(new byte[]{data[0], data[1]}) + "."+
		 	    DataUtil.getIntToByte(data[2]) + "MHz\n"+
		 	   "End Frequency: " + 
		 	    DataUtil.getIntTo2Byte(new byte[]{data[3], data[4]}) + "."+
		 	    DataUtil.getIntToByte(data[5]) + "MHz\n";
		 		break;
		 	case "/11/band": 
		 		result ="Bandwidth: " + DataUtil.getIntTo2Byte(data) + "kHz";
		 		break;
		 	case "/11/b_s_addr":
		 		if(data.length == 16){
			 		result ="Base Station Address: " + rtnStr.substring(0,4) + ":"
			 		+ rtnStr.substring(4,8) + ":" 
			 		+ rtnStr.substring(8,12) + ":" 
			 		+ rtnStr.substring(12,16) + ":" 
			 		+ rtnStr.substring(16,20) + ":" 
			 		+ rtnStr.substring(20,24) + ":" 
			 		+ rtnStr.substring(24,28) + ":" 
			 		+ rtnStr.substring(28,32);
		 		}else
		 			result ="Base Station Address: " + DataUtil.getString(data);
		 		break;
		 	case "/11/a_key":
		 		result ="APP Key: " + rtnStr;
		 		break;
		 	case "/11/h_t_b_s":
		 		if(rtnStr.substring(3,4).equals("1"))
		 			result ="Hops to Base Station: " + rtnStr + "(Coordinator)";
		 		else if(rtnStr.substring(1,2).equals("1"))
		 			result ="Hops to Base Station: " + rtnStr + "(1hope node)";
		 		else if(rtnStr.substring(1,2).equals("2"))
		 			result ="Hops to Base Station: " + rtnStr + "(2hope node)";
		 		else if(rtnStr.substring(1,2).equals("3"))
		 			result ="Hops to Base Station: " + rtnStr + "(3hope node)";
		 		else
		 			result ="Hops to Base Station: " + rtnStr;
		 		break;
		 	case "/11/e_64": 
		 		StringBuilder sb = new StringBuilder();
		 		for(final byte a: data)
		            sb.append(String.format("%02X", a&0xff));
		 		result ="EUI 64: " + sb.toString();
		 		break;
		 	case "/11/l_port":
		 		result ="Listen Port: " + DataUtil.getIntTo2Byte(data);
		 		break;
		 	case "/11/m_h":
		 		result ="Max Hop: " + DataUtil.getIntToByte(data[0]);
		 		break;
		 	case "/12/metering":
		 		result ="Metering Schedule: " + DataUtil.getIntTo2Byte(data) + "(Seconds)";
		 		break;
		 	case "/12/lp_upload":
		 		result ="LP Upload Schedule: " + DataUtil.getIntTo4Byte(data) + "(Seconds)";
		 		break;
		 	case "/event/type":
		 	case "/13/type":
		 		result ="Please refer to 'Comannd List' page ";
		 		break;
		 }
		 return result;
	 }
}
