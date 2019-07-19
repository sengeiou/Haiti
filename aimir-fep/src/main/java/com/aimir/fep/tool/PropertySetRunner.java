package com.aimir.fep.tool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.mcu.data.McuPropertyResult;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;

public class PropertySetRunner {
	private static Logger log = LoggerFactory.getLogger(PropertySetRunner.class);

	private McuPropertyResult mcuPropertyResult;
	
	private List<String> mcuList = new ArrayList<>();
	private String mcuId;
	private String[] key;
	private String[] keyValue;
	
	public PropertySetRunner() {}
	public PropertySetRunner(List<String> mcuList, String[] key, String[] keyValue) {
		this.mcuList = mcuList;
		this.key = key;
		this.keyValue = keyValue;
	}
	
	public List<McuPropertyResult> startRun(List<String>mcuList, String[] key, String[] keyValue){
		CommandGW gw = DataUtil.getBean(CommandGW.class);
		
		int maxPoolSize = Integer.parseInt(FMPProperty.getProperty("executor.max.pool.size","100"));
		ExecutorService executorService = Executors.newFixedThreadPool(maxPoolSize);
		log.debug("Thread Start~!");
		
		PropertySetResult proSetResult = new PropertySetResult();
		
		for(int i = 0; i<mcuList.size() ; i++) {
			mcuId = mcuList.get(i);
			log.info("1-for loop mcuId: " + mcuId);
			mcuPropertyResult = new McuPropertyResult();
			
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					mcuPropertyResult.setSysId(mcuId);
					log.info("Setting MCUID===================>"+mcuId);
					
					try {
						log.info("2-for loop run: " + mcuId);	
						
						Map<String, Object> result = new HashMap<String, Object>();
						result = gw.cmdMcuSetProperty(mcuId, key, keyValue);
						
						if(result != null && result.size() != 0) {
							mcuPropertyResult.setResult(result.get("cmdResult").toString());
						}else {
							mcuPropertyResult.setResult("FAIL");
						}
						
					} catch (Exception e) {
						log.info("99-for loop exception");
						log.debug(e.toString());
						mcuPropertyResult.setResult("FAIL("+e.toString()+")");
					}
				}
			};	//Runnable End
			
			Future<McuPropertyResult> future = executorService.submit(runnable, mcuPropertyResult);
			
			try {
				mcuPropertyResult = future.get();
				proSetResult.setResult(mcuPropertyResult);
				log.info("future result3 = > " + proSetResult.schList);
			}catch (Exception e) {
				log.debug(e.getMessage()+"#####Exception#####");
			}
		}//for End
		executorService.shutdown();
		return proSetResult.schList;
	} 
	
}

class PropertySetResult{
	List<McuPropertyResult> schList = new ArrayList<McuPropertyResult>();
	synchronized void setResult(McuPropertyResult mpr) {
		schList.add(mpr);
	}
}
