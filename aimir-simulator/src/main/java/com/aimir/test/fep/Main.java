package com.aimir.test.fep;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.test.fep.pattern.metering.TestKamstrup;
import com.aimir.test.fep.pattern.metering.TestMetering;
import com.aimir.test.fep.pattern.metering.TestSM110;
import com.aimir.test.fep.util.TimeUtil;

public class Main {

    private static Log log = LogFactory.getLog(Main.class);
    
    public static void main(String[] args)
    {
    	
		int dcuCount = 1000;
		int nodeCount = 300;
		int threadCount = 1000;
		String modelType = "Kamstrup";
    	
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
            else if(nextArg.startsWith("-modelType")){
            	modelType = args[i+1];
            }
        }        
        
    	TestMetering test = null;
    	
    	if(modelType.equals("Kamstrup")){
    		test = new TestKamstrup();
    	}else if(modelType.equals("SM110")){
    		test = new TestSM110();
    	}else{
    		return;
    	}
    	test.setMCUID("11010");
    	test.setNodeCount(nodeCount);    	

    	String start = TimeUtil.getCurrentTimeMilli();
    	
		ExecutorService pool = Executors.newFixedThreadPool(threadCount);
		int i = 0;
        try
        {

        	for(; i < dcuCount; i++){
        		pool.execute(test);
        		Thread.sleep(250);
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
}
