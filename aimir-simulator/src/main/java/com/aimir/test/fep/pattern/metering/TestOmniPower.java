package com.aimir.test.fep.pattern.metering;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.protocol.fmp.client.lan.LANClient;
import com.aimir.fep.protocol.fmp.common.LANTarget;
import com.aimir.fep.protocol.fmp.datatype.WORD;
import com.aimir.fep.protocol.fmp.frame.service.MDData;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.DataUtil;
import com.aimir.test.fep.generator.ValueGenerator;
import com.aimir.test.fep.util.DateTimeUtil;
import com.aimir.test.fep.util.Hex;
import com.aimir.test.fep.util.TimeUtil;
import com.aimir.test.fep.util.Util;

public class TestOmniPower extends TestMetering {

    private static Log log = LogFactory.getLog(TestOmniPower.class);
	
    public static void main(String[] args)
    {    	
		int dcuCount = 1000;
		int nodeCount = 300;
		String ip = "";
		int port = 8000;
		int threadCount = 1000;

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

        }        

    	TestOmniPower test = new TestOmniPower();
    	test.setNodeCount(nodeCount);    
    	test.setTargetIp(ip);
    	test.setTargetPort(port);

    	String start = TimeUtil.getCurrentTimeMilli();    	
		ExecutorService pool = Executors.newFixedThreadPool(threadCount);
		int i = 0;
		int START_DCUID = 10000;
        try
        {
        	for(; i < dcuCount; i++){
        		
        		//String dcuid = ValueGenerator.randombcdString(i, 5);
        		String dcuid = String.valueOf((START_DCUID+i));
        		test.setMCUID(dcuid);
        		pool.execute(test);
        		Thread.sleep(100);
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
        StringBuffer omniData = null;
		try {
			omniData = makeOmniPowerMeterData(timestamp);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//Timestamp.length(7)+Data.length
		int MD_LENGTH = 7 + Hex.encode(omniData.toString()).length;
        
        String sensorType = "01";
        String serviceType = "01";
        String vendor = "29";//0x29 kamstrup new version
        String dataCount = "0100";
        String length = Hex.decode(DataFormat.LSB2MSB(DataUtil.get2ByteToInt(MD_LENGTH)));//convert to little endian
        
        for(int i = 0; i < nodeCount; i++){

        	String eui = "000B12"+MCUID+ValueGenerator.randomTextString(i, 5);
            mdBuf.append(eui);            
            String value = MCUID+ValueGenerator.randombcdString(i, 8);
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
            mdBuf.append(omniData);
        }
        
        //log.info("mdBuf.length()="+mdBuf.length());
        byte[] md = Hex.encode(mdBuf.toString());
        //log.info("mdBuf="+Hex.decode(md));
        mdData.setMdData(md);
        mdData.setMcuId(MCUID);
        
        return mdData;
    }
    
    private StringBuffer makeOmniPowerMeterData(String currentTime) throws Exception {
    	
    	StringBuffer METERDATA = new StringBuffer();
    	int lpcnt = 24;
    	int lpInterval = 15;
    	int channelCount = 4;
    	String period = Hex.decode(new byte[]{(byte) (60/lpInterval)});//00: metering fail, 01: 1hour, 02:30 min, 04: 15 min
    	String channelMask = Hex.decode(new byte[]{0x0F});
    	String lpTimeFormat = "480500";
    	String channelFormat = "0204420204420E04420E0442";
    	String lastLpIndex = "00000696";
    	String cnt = Hex.decode(DataUtil.get2ByteToInt(lpcnt));
    	
    	METERDATA.append(period);
    	METERDATA.append(channelMask);
    	METERDATA.append(lpTimeFormat);
    	METERDATA.append(channelFormat);
    	METERDATA.append(lastLpIndex);
    	
        int tfUnit = DataUtil.getIntToByte(Hex.encode(lpTimeFormat)[0]);
        int tfLen = DataUtil.getIntToByte(Hex.encode(lpTimeFormat)[1]);
        byte[] LT = new byte[tfLen];
        
        String lpIntervalTime = currentTime.substring(0,10)+"0000";
        lpIntervalTime = Util.addMinYymmdd(lpIntervalTime, -(lpInterval*lpcnt));  
        
    	for(int i = 0; i < lpcnt; i++){
    		
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            Calendar cal = Calendar.getInstance();
            cal.setTime(sdf.parse(lpIntervalTime));
            long s = cal.getTimeInMillis();
            byte[] lt = DataUtil.get8ByteToInt(s);       

            String lpTime = Hex.decode(lt).substring(6);

            long[] lpChannel = new long[]{0,0,0,0};

            String lpValue = Hex.decode(DataUtil.get4ByteToInt(lpChannel[0]))
            		       + Hex.decode(DataUtil.get4ByteToInt(lpChannel[1]))
            		       + Hex.decode(DataUtil.get4ByteToInt(lpChannel[2]))
            		       + Hex.decode(DataUtil.get4ByteToInt(lpChannel[3]));
            
            METERDATA.append(lpTime);
            METERDATA.append(lpValue);            		
            lpIntervalTime = Util.addMinYymmdd(lpIntervalTime, lpInterval);  
    	}

    	//add KMP register data
		METERDATA.append("0A403F015A000D013B330D");
		METERDATA.append("0F403F1003E9330400010E33C2A7450D");
		METERDATA.append("2A403F1000010204420000000000020204420000000000030E04420000000000040E04420000000083E60D");
		METERDATA.append("36403F10041E21020000E3041F210200000004202102000000043422044200000000043522044200000000043622044200000000C6040D");
		METERDATA.append("1C403F1003EC2E04000000028904173508000201310F0D0C050E9B4A0D");
		
		return METERDATA;    	
    }    

    public void run() {
        try {
            log.info("targetIp="+targetIp + ", port=" + targetPort);
            LANTarget target = new LANTarget(targetIp,targetPort);
            //LANTarget target = new LANTarget("187.1.10.28",8000);
            target.setTargetId(MCUID);
            LANClient client = new LANClient(target);            

            client.sendMDWithCompress(makeFrame());
            client.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
