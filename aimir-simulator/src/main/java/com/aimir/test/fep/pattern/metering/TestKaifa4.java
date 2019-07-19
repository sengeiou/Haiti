package com.aimir.test.fep.pattern.metering;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.protocol.fmp.client.lan.LANClient;
import com.aimir.fep.protocol.fmp.common.LANTarget;
import com.aimir.fep.protocol.fmp.datatype.WORD;
import com.aimir.fep.protocol.fmp.frame.GeneralDataFrame;
import com.aimir.fep.protocol.fmp.frame.ServiceDataFrame;
import com.aimir.fep.protocol.fmp.frame.service.MDData;
import com.aimir.fep.protocol.fmp.frame.service.ServiceData;
import com.aimir.fep.protocol.fmp.log.MDLogger;
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
public class TestKaifa4 extends TestMetering {

    private static Log log = LogFactory.getLog(TestKaifa4.class);
	private Random rand = new Random(System.currentTimeMillis());
    
    public static void main(String[] args){
    	log.info("##### TestKaifa4 Start #####");
    	
		int dcuCount = 1;
		int nodeCount = 1;
		String ip = "";
		int port = 8000;
		int threadCount = 1;
		int lpCount = 15;
		int lpPeriod = 60;
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
            	TestKaifa4 test = new TestKaifa4();
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
    	
    	METERDATA.append(makeMeterMeteringData());
    	METERDATA.append(makeMeterEvent(currentTime));
    	METERDATA.append(makePowerQuality(currentTime));
        
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
    	String pqIntervalTime = Util.getQuaterYymmddhhmm(currentTime, lpPeriod).substring(0,10)+String.valueOf(60-lpPeriod)+"00";
                
        //int randomV = rand.nextInt(200);
        int randomV = getRandomRange(250000,300000);
        log.debug("threadId:"+Thread.currentThread().getId() +", pqIntervalTime="+pqIntervalTime +", randomV:"+randomV);
    	for(int i = 0; i < lpCnt; i++){
    		
            byte[] year  = DataUtil.get2ByteToInt(Integer.parseInt(pqIntervalTime.substring(0,4)));
            byte[] month = new byte[]{DataUtil.getByteToInt(Integer.parseInt(pqIntervalTime.substring(4,6)))};
            byte[] day   = new byte[]{DataUtil.getByteToInt(Integer.parseInt(pqIntervalTime.substring(6,8)))};
            byte[] hour  = new byte[]{DataUtil.getByteToInt(Integer.parseInt(pqIntervalTime.substring(8,10)))};
            byte[] min   = new byte[]{DataUtil.getByteToInt(Integer.parseInt(pqIntervalTime.substring(10,12)))};
            byte[] week  = new byte[]{(byte)0xFF};
                        
            String lpTimeHex = Hex.decode(year)
            		         + Hex.decode(month)
            		         + Hex.decode(day)
            		         + Hex.decode(week)
            		         + Hex.decode(hour)
            		         + Hex.decode(min);

            pqIntervalTime = Util.addMinYymmdd(pqIntervalTime, -lpPeriod);
            
    		lpData.append("02");// STRUCTURE[02]    		
    		lpData.append(Hex.decode(new byte[]{(byte) (channelCount+1+1)}));// LENGTH[06] (channel + lptime + status)
  		
    		randomV -= getRandomRange(100,400);
    		
    		//log.debug("pqIntervalTime["+pqIntervalTime+"] randomV["+randomV+"] bytes["+Hex.decode(DataUtil.get4ByteToInt(randomV))+"]");
    		log.debug("threadId["+Thread.currentThread().getId()+"] pqIntervalTime["+pqIntervalTime+"] " + " lpCnt ["+i+"/"+lpCnt+"] "+ "randomV["+randomV+"] bytes["+Hex.decode(DataUtil.get4ByteToInt(randomV))+"]");
    		
            lpData.append("090C") ;//LP TIME HEADER (type, length)
    		lpData.append(lpTimeHex); //LP TIME
            lpData.append("00FF800000"); //LP TIME END
    		lpData.append("1100"); //LP STATUS  
    		lpData.append("06").append(Hex.decode(DataUtil.get4ByteToInt(randomV))); //ActiveEnergyImport
    		lpData.append("0600000000"); //ActiveEnergyExport
    		lpData.append("0600000000"); //ReactiveEnergyImport
    		lpData.append("0600000000"); //ReactiveEnergyExport
    	}
    	
    	//LOAD_PROFILE
        buf.append("0100630100FF"); //OBIS
        buf.append("0007"); //CLASS
        buf.append("02"); //ATTR        
        buf.append(Hex.decode((DataUtil.get2ByteToInt(Hex.encode(lpData.toString()).length + 2))));//length (tag data length) (lpdata length + 2)
		buf.append("01");//ARRAY[01]
        buf.append(Hex.decode(new byte[]{(byte) lpCnt})); //DATA(00A5) TOTAL LP COUNT
        buf.append(lpData);
        
        log.debug("## buf // ["+buf+"]");
    	return buf;
    }

    public StringBuffer makeMeterEvent(String currentTime) throws Exception {
    	StringBuffer buf = new StringBuffer();
    	
    	boolean f = rand.nextBoolean();
    	
    	Calendar cal = Calendar.getInstance();
    	cal.add(Calendar.HOUR, -1);
    	currentTime = DateTimeUtil.getDateString(cal.getTime());
    	String eventIntervalTime = Util.getQuaterYymmddhhmm(currentTime, lpPeriod).substring(0,10)+String.valueOf(60-lpPeriod)+"00";
    	
    	byte[] year  = DataUtil.get2ByteToInt(Integer.parseInt(eventIntervalTime.substring(0,4)));
        byte[] month = new byte[]{DataUtil.getByteToInt(Integer.parseInt(eventIntervalTime.substring(4,6)))};
        byte[] day   = new byte[]{DataUtil.getByteToInt(Integer.parseInt(eventIntervalTime.substring(6,8)))};
        byte[] hour  = new byte[]{DataUtil.getByteToInt(Integer.parseInt(eventIntervalTime.substring(8,10)))};
        byte[] min   = new byte[]{DataUtil.getByteToInt(Integer.parseInt(eventIntervalTime.substring(10,12)))};
        byte[] week  = new byte[]{(byte)0xFF};
                    
        String lpTimeHex = Hex.decode(year)
        		         + Hex.decode(month)
        		         + Hex.decode(day)
        		         + Hex.decode(week)
        		         + Hex.decode(hour)
        		         + Hex.decode(min);
        
    	if(f) {//STANDARD_EVENT
            buf.append("0000636200FF"); //OBIS
            buf.append("0007"); //CLASS
            buf.append("02"); //ATTR       
            buf.append("0015"); //length
            buf.append("0101"); //array + array length
            buf.append("0202"); //struct + struct item length
            buf.append("090C").append(lpTimeHex).append("00FF800000"); //time
            buf.append("120004"); //eventCode
    	} else {//TAMPER_EVENT
            buf.append("0000636200FF"); //OBIS
            buf.append("0007"); //CLASS
            buf.append("02"); //ATTR    
            buf.append("0015"); //length 
            buf.append("0101"); //array + array length
            buf.append("0202"); //struct + struct item length
            buf.append("090C").append(lpTimeHex).append("00FF800000"); //time
            buf.append("120003"); //eventCode
    	}
    	
    	return buf;
    }
    
    public StringBuffer makeMeterMeteringData() {
    	StringBuffer buf = new StringBuffer();
    	
    	//CUMULATIVE_ACTIVEENERGY_IMPORT
    	buf.append("0100010800FF");
    	buf.append("0003");
    	buf.append("02");
    	buf.append("0005");
    	buf.append("06").append(Hex.decode(DataUtil.get4ByteToInt(rand.nextInt(200))));
    	
    	//CUMULATIVE_ACTIVEENERGY_EXPORT
    	buf.append("0100020800FF");
    	buf.append("0003");
    	buf.append("02");
    	buf.append("0005");
    	buf.append("0600000000");
    	
    	//CUMULATIVE_REACTIVEENERGY_IMPORT
    	buf.append("0100030800FF");
    	buf.append("0003");
    	buf.append("02");
    	buf.append("0005");
    	buf.append("06").append(Hex.decode(DataUtil.get4ByteToInt(rand.nextInt(100))));
    	
    	//CUMULATIVE_REACTIVEENERGY_EXPORT
    	buf.append("0100040800FF");
    	buf.append("0003");
    	buf.append("02");
    	buf.append("0005");
    	buf.append("0600000000");
    	
    	return buf;
    }
    
    public StringBuffer makePowerQuality(String currentTime) throws Exception {
    	StringBuffer buf = new StringBuffer();
    	
    	buf.append("0100630200FF"); //OBIS
        buf.append("0007"); //CLASS
        buf.append("02"); //ATTR      
    	        
        String lpIntervalTime = Util.getQuaterYymmddhhmm(currentTime, lpPeriod).substring(0,10)+String.valueOf(60-lpPeriod)+"00";
        StringBuffer pqData = new StringBuffer();        
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
            int maxVoltage = getRandomRange(2100, 2300);
            
            pqData.append("02");// STRUCTURE[02]    		
            pqData.append("04");// STRUCTURE-VALUE[02]
            
            pqData.append("090C") ;//PQ TIME HEADER (type, length)
            pqData.append(lpTimeHex).append("00FF800000"); //PQ TIME
            pqData.append("06").append(Hex.decode(DataUtil.get4ByteToInt(maxVoltage))); //L1MaxVoltage
            log.debug("maxVoltage:"+maxVoltage+" // decode:"+Hex.decode(DataUtil.get4ByteToInt(maxVoltage)));
            pqData.append("06").append(Hex.decode(DataUtil.get4ByteToInt(maxVoltage - 12))); //L1MinVoltage
            pqData.append("06").append(Hex.decode(DataUtil.get4ByteToInt(maxVoltage - 6))); //L1AvgVoltage
        }
    	buf.append(Hex.decode(DataUtil.get2ByteToInt(Hex.encode(pqData.toString()).length + 2))); ////length
    	buf.append("01"); //array
    	buf.append(Hex.decode(new byte[]{(byte) lpCnt})); //array length
    	buf.append(pqData.toString());
    	
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
                                         
                    ServiceDataFrame sdf = client.sendMDWithCompress_(mdData);
                    writeFile(sdf);
                                   
                    //client.sendMDWithCompress(mdData);
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
    
    private int getRandomRange(int min, int max) {
    	return ThreadLocalRandom.current().nextInt(min, max+1);
    }
    
    public void writeFile(Object message) {
    	try {   		
    		GeneralDataFrame frame = (GeneralDataFrame) message;
			if (frame instanceof ServiceDataFrame) {
				if (frame.getSvc() != 'C') {
					ServiceDataFrame sdf = (ServiceDataFrame)frame;
					ServiceData sdata = ServiceData.decode("SP", sdf, "");
					if (sdata instanceof MDData) {
						MDLogger mdlog = new MDLogger();
						//String filename = mdlog.writeObject(sdata, "/home/aimir/aimiramm/aimir-fep-exec/FEP1/db/md");
						String filename = mdlog.writeObject(sdata, "D:\\logs");
					}
				} 
			}
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    }
}
