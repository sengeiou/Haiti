package com.aimir.test.fep.pattern.metering;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.parser.DLMS;
import com.aimir.fep.protocol.fmp.client.lan.LANClient;
import com.aimir.fep.protocol.fmp.common.LANTarget;
import com.aimir.fep.protocol.fmp.datatype.WORD;
import com.aimir.fep.protocol.fmp.frame.service.MDData;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;
import com.aimir.fep.util.Util;
import com.aimir.test.fep.generator.ValueGenerator;
import com.aimir.test.fep.util.DateTimeUtil;
import com.aimir.test.fep.util.TimeUtil;

public class TestNamjunOrg extends TestMetering {

    private static Log log = LogFactory.getLog(TestNamjunOrg.class);
	
    public static void main(String[] args)
    {    	
		int dcuCount = 1;
		int nodeCount = 1;
		String ip = "";
		int port = 8000;
		int threadCount = 1000;
		int lpCount = 12;
		int threadSleep = 300;
		
        for (int i=0; i < args.length; i+=2) {

            String nextArg = args[i];
            if (nextArg.startsWith("-dcuCount")) {
            	dcuCount = Integer.parseInt(args[i+1]);
            }
            else if (nextArg.startsWith("-nodeCount")) {
            	nodeCount = Integer.parseInt(args[i+1]);
            }
            else if (nextArg.startsWith("-threadCount")) {
            	threadCount = Integer.parseInt(args[i+1]);
            }
            else if (nextArg.startsWith("-fepIp")) {
            	ip = args[i+1];
            }
            else if (nextArg.startsWith("-fepPort")) {
            	port = Integer.parseInt(args[i+1]);
            }
            else if (nextArg.startsWith("-lpCount")) {
            	lpCount = Integer.parseInt(args[i+1]);
            }
            else if (nextArg.startsWith("-threadSleep")) {
            	threadSleep = Integer.parseInt(args[i+1]);
            }
        }        

        /*
    	TestNamjun test = new TestNamjun();
    	test.setNodeCount(nodeCount);    
    	test.setTargetIp(ip);
    	test.setTargetPort(port);
    	*/

    	String start = TimeUtil.getCurrentTimeMilli();    	
		ExecutorService pool = Executors.newFixedThreadPool(threadCount);
		int i = 0;
		int START_DCUID = 10000;
        try
        {
        	for(; i < dcuCount; i++){
        		
            	TestNamjunOrg test = new TestNamjunOrg();
            	test.setNodeCount(nodeCount);  
            	test.setLpCnt(lpCount);
            	test.setTargetIp(ip);
            	test.setTargetPort(port);
            	
        		//String dcuid = ValueGenerator.randombcdString(i, 5);
        		String dcuid = String.valueOf((START_DCUID+i));
        		test.setMCUID(dcuid);
        		pool.execute(test);
        		Thread.sleep(threadSleep);
        	}

    		pool.shutdown();
    		
    		if(pool.isShutdown()){
            	String end = TimeUtil.getCurrentTimeMilli();
            	log.info("dcuCount=["+dcuCount+"] nodeCount=["+nodeCount+"] Start["+start+"], End["+end+"]" + i);
    		}
        	
        } catch (Exception ex)
        {
        	pool.shutdown();
            log.error("failed ", ex);
        }   
    }
    
    public MDData makeFrame() throws Exception{

        MDData mdData = new MDData();
        mdData.setCnt(new WORD(nodeCount));
        String timestamp = DateTimeUtil.getDateString(new Date());
        byte[] year2 = DataUtil.get2ByteToInt(Integer.parseInt(timestamp.substring(0, 4)));
        DataUtil.convertEndian(year2);
        String hYear2 = Hex.decode(year2);
        String hYear1 = Hex.decode(new byte[]{DataUtil.getByteToInt(Integer.parseInt(timestamp.substring(2, 4)))});
        String hMonth = Hex.decode(new byte[]{DataUtil.getByteToInt(Integer.parseInt(timestamp.substring(4, 6)))});
        String hDay = Hex.decode(new byte[]{DataUtil.getByteToInt(Integer.parseInt(timestamp.substring(6, 8)))});
        String hHour = Hex.decode(new byte[]{DataUtil.getByteToInt(Integer.parseInt(timestamp.substring(8, 10)))});
        String hMin = Hex.decode(new byte[]{DataUtil.getByteToInt(Integer.parseInt(timestamp.substring(10, 12)))});
        String hSec = Hex.decode(new byte[]{DataUtil.getByteToInt(Integer.parseInt(timestamp.substring(12, 14)))});
        
        StringBuffer mdBuf = new StringBuffer();  
        StringBuffer namjunData = null;
		try {
			namjunData = makeNamjunMeterData(timestamp);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//Timestamp.length(7)+Data.length
		int MD_LENGTH = 7 + Hex.encode(namjunData.toString()).length;
        
        String sensorType = "01";
        String serviceType = "01";
        String vendor = "0C";//0x0C kepco namjunsa meter
        String dataCount = "0100";
        String length = Hex.decode(DataFormat.LSB2MSB(DataUtil.get2ByteToInt(MD_LENGTH)));//convert to little endian
        
        int euiStart = 10000;
        int meterStart = 10000;
        
        for(int i = 0; i < nodeCount; i++){

        	String eui = "000B12"+MCUID+(euiStart+i);
            mdBuf.append(eui);            
            String value = MCUID+"000"+(meterStart+i);
            byte[] code = value.getBytes();
            String meterid = Hex.decode(code)+"00000000000000";
            
            mdBuf.append(meterid);
            mdBuf.append(sensorType);
            mdBuf.append(serviceType);
            mdBuf.append(vendor);
            mdBuf.append(dataCount);
            mdBuf.append(length);
            mdBuf.append(hYear2 + hMonth + hDay + hHour + hMin + hSec); // md data timestamp
            
            // start metering data 
            mdBuf.append(namjunData);
        }
        
        //log.info("mdBuf.length()="+mdBuf.length());
        byte[] md = Hex.encode(mdBuf.toString());
        //log.info("mdBuf="+Hex.decode(md));
        mdData.setMdData(md);
        mdData.setMcuId(MCUID);
        
        return mdData;
    }    
    
    private StringBuffer makeNamjunMeterData(String currentTime) throws Exception {
    	
    	StringBuffer METERDATA = new StringBuffer();

        // DLMS Header OBIS(6), CLASS(1), ATTR(1), LENGTH(2)
        // DLMS Tag Tag(1), DATA or LEN/DATA (*)      		
    	METERDATA.append(makeBasicInfo(currentTime));
    	METERDATA.append(makeBilling(currentTime));
    	METERDATA.append(makeLpData(currentTime));
		
        //DLMS dlms = new DLMS();
        //dlms.parse(Hex.encode(METERDATA.toString()));
        
		return METERDATA;    	
    } 
    
    private StringBuffer makeBasicInfo(String currentTime) {    	
        
        byte[] year  = DataUtil.get2ByteToInt(Integer.parseInt(currentTime.substring(0,4)));
        byte[] month = new byte[]{DataUtil.getByteToInt(Integer.parseInt(currentTime.substring(4,6)))};
        byte[] day   = new byte[]{DataUtil.getByteToInt(Integer.parseInt(currentTime.substring(6,8)))};
        byte[] hour  = new byte[]{DataUtil.getByteToInt(Integer.parseInt(currentTime.substring(8,10)))};
        byte[] min   = new byte[]{DataUtil.getByteToInt(Integer.parseInt(currentTime.substring(10,12)))};
        byte[] sec   = new byte[]{DataUtil.getByteToInt(Integer.parseInt(currentTime.substring(12,14)))};
        byte[] week  = new byte[]{(byte)0xFF};
        
        String currTimeHex = Hex.decode(year)
        		         + Hex.decode(month)
        		         + Hex.decode(day)
        		         + Hex.decode(hour)
        		         + Hex.decode(min)
        		         + Hex.decode(sec)
        		         + Hex.decode(week); 
        
    	StringBuffer buf = new StringBuffer();

        buf.append("0180808081FF");//METER_INFO
        buf.append("07");
        buf.append("02");
        buf.append("4500");
        buf.append("0101");
        buf.append("020D");
        buf.append("090131");//meter kind
        buf.append("090108");  //vendor
        buf.append("09083030383439313733"); //meter id        
        buf.append("0908"); //meter time tag, length        
        buf.append(currTimeHex); //meter time
        buf.append("040100"); //meter error status
        buf.append("040101"); //meter caution status
        buf.append("1207D0"); //meter pulse constant
        buf.append("120001"); //pt ratio
        buf.append("120001"); //ct ratio
        buf.append("1101");//regular read date
        buf.append("110F"); //lp profile interval
        buf.append("0906"); //recent read load profile date
        buf.append("07DC050B1100"); //last read information
        buf.append("090A000007D0010100010B06");    //re  
		
        buf.append("0101000300FF");//METER_CONSTANT_ACTIVE
        buf.append("03");
        buf.append("02");
        buf.append("0600");
        buf.append("090443FA0000");

        buf.append("0101000301FF");//METER_CONSTANT_REACTIVE
        buf.append("03");
        buf.append("02");
        buf.append("0600");
        buf.append("090443FA0000");
        
        return buf;
    	
    }
    
    private StringBuffer makeBilling(String currentTime){
    	
    	StringBuffer buf = new StringBuffer();
    	
        byte[] year  = DataUtil.get2ByteToInt(Integer.parseInt(currentTime.substring(0,4)));
        byte[] month = new byte[]{DataUtil.getByteToInt(Integer.parseInt(currentTime.substring(4,6)))};
        byte[] day   = new byte[]{DataUtil.getByteToInt(Integer.parseInt(currentTime.substring(6,8)))};
        byte[] week  = new byte[]{(byte)0xFF};
        byte[] hour  = new byte[]{DataUtil.getByteToInt(Integer.parseInt(currentTime.substring(8,10)))};
        byte[] min   = new byte[]{DataUtil.getByteToInt(Integer.parseInt(currentTime.substring(10,12)))};
        
        String demandTimeHex = Hex.decode(year)
        		         + Hex.decode(month)
        		         + Hex.decode(day)
        		         + Hex.decode(week)
        		         + Hex.decode(hour)
        		         + Hex.decode(min); 

        buf.append("018080808064");//KEPCO_CURRENT_MAX_DEMAND
        buf.append("07");
        buf.append("02");
        buf.append("6400");
        buf.append("0101020F");
        buf.append("0600000003");
        buf.append("0600000000");
        buf.append("120004");
        buf.append("0600000004");
        buf.append("090C");
        buf.append("07DC0504FF0C0000FF800000");
        buf.append("0600000000");
        buf.append("0600000000");
        buf.append("120000");
        buf.append("0600000000");
        buf.append("090C");
        buf.append("07D00101FF000100FF800000");
        buf.append("0600000000");
        buf.append("0600000000");
        buf.append("120000");
        buf.append("0600000000");
        buf.append("090C07D00101FF000100FF800000");            		
        		
        buf.append("018080808065");//KEPCO_PREVIOUS_MAX_DEMAND
        buf.append("07");
        buf.append("02");
        buf.append("6400");
        buf.append("0101020F");
        buf.append("0600000000");
        buf.append("0600000000");
        buf.append("120000");
        buf.append("0600000000");
        buf.append("090C07D00101FF000100FF800000");
        buf.append("0600000000");
        buf.append("0600000000");
        buf.append("120000");
        buf.append("0600000000");
        buf.append("090C07D00101FF000100FF800000");
        buf.append("0600000000");
        buf.append("0600000000");
        buf.append("120000");
        buf.append("0600000000");
        buf.append("090C07D00101FF000100FF800000");            		
        		
        buf.append("0000620101FF");//MONTHLY_ENERGY_PROFILE
        buf.append("07");
        buf.append("02");
        buf.append("4400");
        buf.append("0101020C");
        buf.append("0600000003");
        buf.append("0600000000");
        buf.append("09043F800000");
        buf.append("0600000003");
        buf.append("0600000000");
        buf.append("09043F800000");
        buf.append("0600000000");
        buf.append("0600000000");
        buf.append("09043F800000");
        buf.append("0600000000");
        buf.append("0600000000");
        buf.append("09043F800000");            
        
        buf.append("0000620102FF");//MONTHLY_DEMAND_PROFILE
        buf.append("07");
        buf.append("02");
        buf.append("B400");
        buf.append("01010218");
        buf.append("120004");
        //set active demand date time
        buf.append("090C"); //demand time tag, length        
        buf.append(demandTimeHex); //demand time : 07DC0504FF0C00
        buf.append("00FF800000"); //date time end
        buf.append("0600000004");
        buf.append("120000");
        buf.append("090C07D00101FF000100FF800000");
        buf.append("0600000000");
        buf.append("120004");
        buf.append("090C07DC0504FF0C0000FF800000");
        buf.append("0600000004");
        buf.append("120000");
        buf.append("090C07D00101FF000100FF800000");
        buf.append("0600000000");
        buf.append("120000");
        buf.append("090C07D00101FF000100FF800000");
        buf.append("0600000000");
        buf.append("120000");
        buf.append("090C07D00101FF000100FF800000");
        buf.append("0600000000");
        buf.append("120000");
        buf.append("090C07D00101FF000100FF800000");
        buf.append("0600000000");
        buf.append("120000");
        buf.append("090C07D00101FF000100FF800000");
        buf.append("0600000000");
        
        return buf;
    }    
    
    private StringBuffer makeLpData(String currentTime) throws Exception {
    	
    	//int lpcnt = 12;
    	int lpInterval = 15;
    	int channelCount = 4;
    	
    	StringBuffer buf = new StringBuffer();   
    	StringBuffer lpData = new StringBuffer();
        
        String lpIntervalTime = currentTime.substring(0,10)+"0000";
        lpIntervalTime = Util.addMinYymmdd(lpIntervalTime, -(lpInterval*lpCnt));  
        
    	for(int i = 0; i < lpCnt; i++){
    		
            byte[] year  = DataUtil.get2ByteToInt(Integer.parseInt(lpIntervalTime.substring(0,4)));
            byte[] month = new byte[]{DataUtil.getByteToInt(Integer.parseInt(lpIntervalTime.substring(4,6)))};
            byte[] day   = new byte[]{DataUtil.getByteToInt(Integer.parseInt(lpIntervalTime.substring(6,8)))};
            byte[] week  = new byte[]{(byte)0xFF};
            byte[] hour  = new byte[]{DataUtil.getByteToInt(Integer.parseInt(lpIntervalTime.substring(8,10)))};
            byte[] min   = new byte[]{DataUtil.getByteToInt(Integer.parseInt(lpIntervalTime.substring(10,12)))};
            
            String lpTimeHex = Hex.decode(year)
            		         + Hex.decode(month)
            		         + Hex.decode(day)
            		         + Hex.decode(week)
            		         + Hex.decode(hour)
            		         + Hex.decode(min);            
            lpIntervalTime = Util.addMinYymmdd(lpIntervalTime, lpInterval);
            
    		lpData.append("02");// STRUCTURE[02]    		
    		lpData.append(Hex.decode(new byte[]{(byte) (channelCount+1+1)}));// LENGTH[06] (channel + lptime + status)
    		
    		//LP n(4) CHANNEL
    		for(int k = 0; k < channelCount; k++){
    			lpData.append("12");//TAG (UINT16) 0x12
    			lpData.append("0000"); //VALUE 2 byte
    		}
    		
            lpData.append("090C") ;//LP TIME HEADER (type, length)
    		lpData.append(lpTimeHex); //LP TIME
            lpData.append("00FF800000"); //LP TIME END
    		lpData.append("040100"); //LP STATUS  
    	}
    	
    	//LOAD_PROFILE
        buf.append("0100630100FF"); //OBIS
        buf.append("07"); //CLASS
        buf.append("02"); //ATTR          
        buf.append(Hex.decode(DataFormat.LSB2MSB((DataUtil.get2ByteToInt(Hex.encode(lpData.toString()).length + 4)))));//length (tag data length) (lpdata length + 4)
        buf.append("0182");//ARRAY[01], LENGTH[82] (2bytes)
        buf.append(Hex.decode(DataUtil.get2ByteToInt(lpCnt))); //DATA(00A5) TOTAL LP COUNT
        buf.append(lpData);
    	return buf;
    }

    public void run() {
        try {
            log.info("DCU["+MCUID+"] SEND START.. targetIp="+targetIp + ", port=" + targetPort);
            LANTarget target = new LANTarget(targetIp,targetPort);
            target.setTargetId(MCUID);
            LANClient client = new LANClient(target);            

            client.sendMDWithCompress(makeFrame());
            client.close();
            log.info("DCU["+MCUID+"] SEND END.. targetIp="+targetIp + ", port=" + targetPort);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
