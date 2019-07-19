package com.aimir.test.fep.pattern.event;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.protocol.fmp.client.lan.LANClient;
import com.aimir.fep.protocol.fmp.common.LANTarget;
import com.aimir.fep.protocol.fmp.datatype.OID;
import com.aimir.fep.protocol.fmp.datatype.SMIValue;
import com.aimir.fep.protocol.fmp.datatype.TIMESTAMP;
import com.aimir.fep.protocol.fmp.frame.ServiceDataConstants;
import com.aimir.fep.protocol.fmp.frame.service.EventData;
import com.aimir.fep.util.DataUtil;

import com.aimir.test.fep.util.TimeUtil;

public class TestDCUInstall implements Runnable {

	private static Log log = LogFactory.getLog(TestDCUInstall.class);
	
    protected String MCUID = null;
    protected int nodeCount = 1;
    protected String targetIp = null;
    protected int targetPort = 8000;
	
	private boolean isRandomId = false;
	private long randomCount = 0L;
	
	private String from = null;
	private String to = null;
	
    public static void main(String[] args)
    {
		int dcuCount = 1000;
		int nodeCount = 300;
		String ip = "";
		int port = 8000;

        for (int i=0; i < args.length; i+=2) {

            String nextArg = args[i];
            if (nextArg.startsWith("-dcuCount")) {
            	dcuCount = Integer.parseInt(args[i+1]);
            }
            else if (nextArg.startsWith("-nodeCount")) {
            	nodeCount = Integer.parseInt(args[i+1]);
            }
            else if (nextArg.startsWith("-fepIp")) {
            	ip = args[i+1];
            }
            else if (nextArg.startsWith("-fepPort")) {
            	port = Integer.parseInt(args[i+1]);
            }
        }

    	String start = TimeUtil.getCurrentTimeMilli();    	
		ExecutorService pool = Executors.newFixedThreadPool(1000);
		int i = 0;
		int START_DCUID = 10000;
        try
        {
        	for(; i < dcuCount; i++){
        		
            	TestDCUInstall test = new TestDCUInstall();
            	test.setNodeCount(nodeCount);    
            	test.setTargetIp(ip);
            	test.setTargetPort(port);
            	
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
	
    
    public EventData makeEventData() throws Exception {
    	
		EventData event = new EventData();
		event.setCode(new OID("200.1.0"));
		event.setSrcType(ServiceDataConstants.E_SRCTYPE_MCU);
		event.setTimeStamp(new TIMESTAMP(TimeUtil.getCurrentTime()));
		SMIValue smiValue = null;
		smiValue = (SMIValue) DataUtil.getFMPVariable("","sysPhoneNumber",
            "010000"+MCUID); 
		event.append(smiValue);
		smiValue = (SMIValue) DataUtil.getFMPVariable("","ethIpAddr", 
            "128.0.0.1"); 
		event.append(smiValue);
		smiValue = (SMIValue) DataUtil.getFMPVariable("","sysType","0"); 
		event.append(smiValue);
		smiValue = (SMIValue) DataUtil.getFMPVariable("","sysMobileType","0"); 
		event.append(smiValue);
		smiValue = (SMIValue) DataUtil.getFMPVariable("","sysMobileMode","2"); 
		event.append(smiValue);
		smiValue = (SMIValue) DataUtil.getFMPVariable("","sysLocalPort","8000"); 
		event.append(smiValue);
		
		return event;
    }
    /*
    public void test_EquipmentRegistration(String ip, int port, int dcuCount) {
		
    	try{

    		int START_DCUID = 10000;
            		
        	for(int i = 0; i < dcuCount; i++){
        		
        		//String dcuid = ValueGenerator.randombcdString(i, 5);
        		String dcuid = String.valueOf((START_DCUID+i));
        		LANTarget target = new LANTarget(ip,port);
        		target.setTargetId(dcuid);
        		LANClient client = new LANClient(target);  

        		EventData event = new EventData();
        		event.setCode(new OID("200.1.0"));
        		event.setSrcType(ServiceDataConstants.E_SRCTYPE_MCU);
        		event.setTimeStamp(new TIMESTAMP(TimeUtil.getCurrentTime()));
        		SMIValue smiValue = null;
        		smiValue = DataUtil.getSMIValue("sysPhoneNumber",
                    "01191807327"); 
        		event.append(smiValue);
        		smiValue = DataUtil.getSMIValue("ethIpAddr", 
                    "187.1.5.235"); 
        		event.append(smiValue);
        		smiValue = DataUtil.getSMIValue("sysType","0"); 
        		event.append(smiValue);
        		smiValue = DataUtil.getSMIValue("sysMobileType","0"); 
        		event.append(smiValue);
        		smiValue = DataUtil.getSMIValue("sysMobileMode","2"); 
        		event.append(smiValue);
        		smiValue = DataUtil.getSMIValue("sysLocalPort","8000"); 
        		event.append(smiValue);
        		client.sendEvent(event);
        	}
        }catch(Exception ex) { ex.printStackTrace(); }

    	
    	/*
		String dcuid = "79960";
		LANTarget target = new LANTarget("187.1.10.28",8000);
		target.setTargetId(dcuid);
		LANClient client;
		try {
			client = new LANClient(target);
			EventData event = new EventData();
			event.setCode(new OID("200.1.0"));
			event.setSrcType(ServiceDataConstants.E_SRCTYPE_MCU);
			event.setTimeStamp(new TIMESTAMP(TimeUtil.getCurrentTime()));
			SMIValue smiValue = null;
			smiValue = DataUtil.getSMIValue("sysPhoneNumber",
	            "01191807327"); 
			event.append(smiValue);
			smiValue = DataUtil.getSMIValue("ethIpAddr", 
	            "187.1.5.235"); 
			event.append(smiValue);
			smiValue = DataUtil.getSMIValue("sysType","0"); 
			event.append(smiValue);
			smiValue = DataUtil.getSMIValue("sysMobileType","0"); 
			event.append(smiValue);
			smiValue = DataUtil.getSMIValue("sysMobileMode","2"); 
			event.append(smiValue);
			smiValue = DataUtil.getSMIValue("sysLocalPort","8000"); 
			event.append(smiValue);
			client.sendEvent(event);
		}  catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		*/

    public String getMCUID() {
		return MCUID;
	}

	public void setMCUID(String mCUID) {
		MCUID = mCUID;
	}
	

	public int getNodeCount() {
		return nodeCount;
	}

	public void setNodeCount(int nodeCount) {
		this.nodeCount = nodeCount;
	}

	public String getTargetIp() {
		return targetIp;
	}

	public void setTargetIp(String targetIp) {
		this.targetIp = targetIp;
	}

	public int getTargetPort() {
		return targetPort;
	}

	public void setTargetPort(int targetPort) {
		this.targetPort = targetPort;
	}

	public boolean isRandomId() {
		return isRandomId;
	}

	public void setRandomId(boolean isRandomId) {
		this.isRandomId = isRandomId;
	}

	public long getRandomCount() {
		return randomCount;
	}

	public void setRandomCount(long randomCount) {
		this.randomCount = randomCount;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	@Override
	public void run() {
        try {
            log.info("targetIp="+targetIp + ", port=" + targetPort);
            LANTarget target = new LANTarget(targetIp,targetPort);
            target.setTargetId(MCUID);
            LANClient client = new LANClient(target);            

            client.sendEvent(makeEventData());
            client.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
		
	}

}
