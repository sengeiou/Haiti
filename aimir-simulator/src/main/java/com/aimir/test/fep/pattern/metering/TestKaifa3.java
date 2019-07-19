package com.aimir.test.fep.pattern.metering;

import java.util.ArrayList;
import java.util.Calendar;
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
import com.aimir.fep.util.Hex;
import com.aimir.fep.util.Util;
import com.aimir.test.fep.util.DateTimeUtil;
import com.aimir.test.fep.util.TimeUtil;

/**
 * Simulator for SP-1000
 * @see SP-1000
 * @author wll27471297 ( Jiwoong Park )
 */
public class TestKaifa3 extends TestMetering {

    private static Log log = LogFactory.getLog(TestKaifa3.class);
	
    public static void main(String[] args){
    	log.info("##### TestKaifa3 Start #####");
    	
		int dcuCount = 1;
		int nodeCount = 1000;
		String ip = "";
		int port = 8000;
		int threadCount = 1000;
		int lpCount = 96;
		int lpPeriod = 15;
		int threadSleep = 300;
		int startDcuId = 10000;
		log.info("Default Value : dcuCount="+dcuCount+", nodeCount="+nodeCount+", ip="+ip+", port="+port+", threadCount="+threadCount+", lpCount="+lpCount+", threadSleep="+threadSleep+", startDcuId="+startDcuId );
		
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
            else if (nextArg.startsWith("-lpPeriod")) {
            	lpPeriod = Integer.parseInt(args[i+1]);
            }
            else if (nextArg.startsWith("-threadSleep")) {
            	threadSleep = Integer.parseInt(args[i+1]);
            }
            else if (nextArg.startsWith("-startDcuId")) {
            	startDcuId = Integer.parseInt(args[i+1]);
            }
        }
		log.info("Set Value : dcuCount="+dcuCount+", nodeCount="+nodeCount+", ip="+ip+", port="+port+", threadCount="+threadCount+", lpCount="+lpCount+", threadSleep="+threadSleep+", startDcuId="+startDcuId );
        
    	String start = TimeUtil.getCurrentTimeMilli();
    	log.debug("Current Time : "+start);
		ExecutorService pool = Executors.newFixedThreadPool(threadCount);
		int i = 0;
		int START_DCUID = startDcuId;
        try{
        	for(; i < dcuCount; i++){
            	TestKaifa3 test = new TestKaifa3();
            	test.setNodeCount(nodeCount);  
            	test.setLpCnt(lpCount);
            	test.setLpPeriod(lpPeriod);
            	test.setTargetIp(ip);
            	test.setTargetPort(port);

        		String dcuid = String.valueOf((START_DCUID+i));
        		test.setMCUID(dcuid);
        		pool.execute(test);
        		Thread.sleep(threadSleep);
        	}

    		pool.shutdown();
    		
    		while(!pool.isTerminated()){
    		    Thread.sleep(threadSleep);
    		}
    		String end = TimeUtil.getCurrentTimeMilli();
            log.info("dcuCount=["+dcuCount+"] nodeCount=["+nodeCount+"] Start["+start+"], End["+end+"]" + i);
        	
        } catch (Exception ex) {
        	pool.shutdown();
            log.error("failed ", ex);
        }   
        return;
    }
    
    
    public MDData[] makeFrames() throws Exception {
    	log.debug("Make Frames lpPeriod="+lpPeriod+", lpCount=."+lpCnt+" ");
    	
    	int MAX_NODE_COUNT = 1;
    	ArrayList<MDData> list = new ArrayList<MDData>();
    	
    	int cnt = nodeCount/MAX_NODE_COUNT;
    	int res = nodeCount % MAX_NODE_COUNT;
    	
    	for(int k=0; k < cnt; k++){
    		list.add(makeFrame(MAX_NODE_COUNT, 10000+(MAX_NODE_COUNT*k), 10000+(MAX_NODE_COUNT*k)));
    	}
    	
    	if(res > 0){    		
    		list.add(makeFrame(res, 10000+(MAX_NODE_COUNT*cnt), 10000+(MAX_NODE_COUNT*cnt)));
    	}
    	log.debug("Make Frames list["+list.size()+"]");
    	return list.toArray(new MDData[0]);
    	
    }
    
    public MDData makeFrame(int nodecnt, int euiStart, int meterStart) throws Exception{

        MDData mdData = new MDData();
        mdData.setCnt(new WORD(nodecnt));
        Calendar cal = Calendar.getInstance();
        String timestamp = DateTimeUtil.getDateString(cal.getTime());
        //log.debug("Frame Time : " + timestamp);
        byte[] year2 = DataUtil.get2ByteToInt(Integer.parseInt(timestamp.substring(0, 4)));
        DataUtil.convertEndian(year2);
        String hYear2 = Hex.decode(year2);
        String hMonth = Hex.decode(new byte[]{DataUtil.getByteToInt(Integer.parseInt(timestamp.substring(4, 6)))});
        String hDay = Hex.decode(new byte[]{DataUtil.getByteToInt(Integer.parseInt(timestamp.substring(6, 8)))});
        String hHour = Hex.decode(new byte[]{DataUtil.getByteToInt(Integer.parseInt(timestamp.substring(8, 10)))});
        String hMin = Hex.decode(new byte[]{DataUtil.getByteToInt(Integer.parseInt(timestamp.substring(10, 12)))});
        String hSec = Hex.decode(new byte[]{DataUtil.getByteToInt(Integer.parseInt(timestamp.substring(12, 14)))});
        
        StringBuffer mdBuf = new StringBuffer();  
        StringBuffer kaifaData = null;
		try {
			kaifaData = makeKaifaMeterData(timestamp);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Timestamp.length(7)+Data.length
		int MD_LENGTH = 7 + Hex.encode(kaifaData.toString()).length;
        
        String sensorType = "65"; // subgiga
        String serviceType = "01";
        String vendor = "2D"; // 0x2D kaifa meter
        String dataCount = "0100";
        String length = Hex.decode(DataFormat.LSB2MSB(DataUtil.get2ByteToInt(MD_LENGTH))); // convert to little endian
        
        for(int i = 0; i < nodecnt; i++){

        	String eui = "000B12"+MCUID+(euiStart+i);
            mdBuf.append(eui);            
            String value = MCUID+"000"+(meterStart+i);
            byte[] code = value.getBytes();
            String meterid = Hex.decode(code)+"00000000000000";
            log.info("MODEM_ID["+eui+"], METER_ID["+value+"]");
            
            mdBuf.append(meterid);
            mdBuf.append(sensorType);
            mdBuf.append(serviceType);
            mdBuf.append(vendor);
            mdBuf.append(dataCount);
            mdBuf.append(length);
            mdBuf.append(hYear2 + hMonth + hDay + hHour + hMin + hSec); // md data timestamp
            
            mdBuf.append(kaifaData);
        }
        
        byte[] md = Hex.encode(mdBuf.toString());
        mdData.setMdData(md);
        mdData.setMcuId(MCUID);
        
        return mdData;
    }    
    
    
    private StringBuffer makeKaifaMeterData(String currentTime) throws Exception {
    	
    	StringBuffer METERDATA = new StringBuffer();
		
    	METERDATA.append(makeBasicInfo(currentTime));
    	METERDATA.append(makeLpData(currentTime));
        
		return METERDATA;    	
    } 
    
    private StringBuffer makeBasicInfo(String currentTime) {    	
        
//    	log.debug("CurrentTime="+currentTime);
        byte[] year  = DataUtil.get2ByteToInt(Integer.parseInt(currentTime.substring(0,4)));
        byte[] month = new byte[]{DataUtil.getByteToInt(Integer.parseInt(currentTime.substring(4,6)))};
        byte[] day   = new byte[]{DataUtil.getByteToInt(Integer.parseInt(currentTime.substring(6,8)))};
        byte[] hour  = new byte[]{DataUtil.getByteToInt(Integer.parseInt(currentTime.substring(8,10)))};
        byte[] min   = new byte[]{DataUtil.getByteToInt(Integer.parseInt(currentTime.substring(10,12)))};
        byte[] sec   = new byte[]{DataUtil.getByteToInt(Integer.parseInt(currentTime.substring(12,14)))};
		byte[] xx  = new byte[]{(byte)0x00};
        byte[] week  = new byte[]{(byte)0xFF};
        
        String currTimeHex = Hex.decode(year)
        		         + Hex.decode(month)
        		         + Hex.decode(day)
        		         + Hex.decode(week)
        		         + Hex.decode(hour)
        		         + Hex.decode(min)
        		         + Hex.decode(sec)
						 + Hex.decode(xx);


    	StringBuffer buf = new StringBuffer();

        buf.append("0000010000FF");// --> meter time
        buf.append("000802000E");
        buf.append("090C");	
        buf.append(currTimeHex); //meter time 07 D0 02 0D 07 11 11 05 ff  800000 	
        buf.append("800000");        
        
        byte[] lpIntervalSec  = DataUtil.get2ByteToInt(lpPeriod*60); //sec
    	//LOAD_PROFILE interval
        buf.append("0100630100FF"); //OBIS
        buf.append("0007"); //CLASS
        buf.append("04"); //ATTR  
        buf.append("0005060000");
        buf.append(Hex.decode(lpIntervalSec));

        return buf;
    	
    }

    private StringBuffer makeLpData(String currentTime) throws Exception {
    	
    	int channelCount = 4;
    	
    	StringBuffer buf = new StringBuffer();   
    	StringBuffer lpData = new StringBuffer();
        
    	Calendar cal = Calendar.getInstance();
    	cal.add(Calendar.HOUR, -1);
    	currentTime = DateTimeUtil.getDateString(cal.getTime());
//    	String lpIntervalTime = Util.getQuaterYymmddhhmm(currentTime, lpPeriod);
    	String lpIntervalTime = Util.getQuaterYymmddhhmm(currentTime, lpPeriod).substring(0,10)+String.valueOf(60-lpPeriod)+"00";
        log.debug("lpIntervalTime="+lpIntervalTime);
    	for(int i = 0; i < lpCnt; i++){
    		
            byte[] year  = DataUtil.get2ByteToInt(Integer.parseInt(lpIntervalTime.substring(0,4)));
            byte[] month = new byte[]{DataUtil.getByteToInt(Integer.parseInt(lpIntervalTime.substring(4,6)))};
            byte[] day   = new byte[]{DataUtil.getByteToInt(Integer.parseInt(lpIntervalTime.substring(6,8)))};
            byte[] hour  = new byte[]{DataUtil.getByteToInt(Integer.parseInt(lpIntervalTime.substring(8,10)))};
            byte[] min   = new byte[]{DataUtil.getByteToInt(Integer.parseInt(lpIntervalTime.substring(10,12)))};
            byte[] week  = new byte[]{(byte)0xFF};
            
            String lpTimeHex = Hex.decode(year)
            		         + Hex.decode(month)
            		         + Hex.decode(day)
            		         + Hex.decode(week)
            		         + Hex.decode(hour)
            		         + Hex.decode(min);

            lpIntervalTime = Util.addMinYymmdd(lpIntervalTime, -lpPeriod);
            
    		lpData.append("02");// STRUCTURE[02]    		
    		lpData.append(Hex.decode(new byte[]{(byte) (channelCount+1+1)}));// LENGTH[06] (channel + lptime + status)
  		
            lpData.append("090C") ;//LP TIME HEADER (type, length)
    		lpData.append(lpTimeHex); //LP TIME
            lpData.append("00FF800000"); //LP TIME END
    		lpData.append("11000600000000060000000006000000000600000000"); //LP STATUS  and channel
    	}
    	
    	//LOAD_PROFILE
        buf.append("0100630100FF"); //OBIS
        buf.append("0007"); //CLASS
        buf.append("02"); //ATTR        
        buf.append(Hex.decode((DataUtil.get2ByteToInt(Hex.encode(lpData.toString()).length + 2))));//length (tag data length) (lpdata length + 2)
		buf.append("01");//ARRAY[01]
        buf.append(Hex.decode(new byte[]{(byte) lpCnt})); //DATA(00A5) TOTAL LP COUNT
        buf.append(lpData);
    	return buf;
    }

    public void run() {
        try {
            log.info("DCU["+MCUID+"] SEND START.. targetIp="+targetIp + ", port=" + targetPort);
            LANTarget target = new LANTarget(targetIp,targetPort);
            target.setTargetId(MCUID);
            target.setNameSpace("SP");
            MDData[] frames = makeFrames();
            for(int i = 0; i < frames.length; i++) {
                LANClient client = new LANClient(target);
            	try {
            		MDData mdData = frames[i];
                    log.debug("DCU["+MCUID+"] MDDATA ENTRY["+i+"] ");
                    client.sendMDWithCompress(mdData);
            	}catch (Exception e) {
            		log.error("Error on DCU["+MCUID+"] i["+i+"] : "+e.getMessage(),e);
					i--;
				}finally {
                    Thread.sleep(1000);
                    client.close(true);
				}
            }

            log.info("DCU["+MCUID+"] SEND END.. targetIp="+targetIp + ", port=" + targetPort + ", data_cnt[" + frames.length + "]");
        }
        catch (Exception e) {
            log.error(e,e);
        }
        return;
    }
}
