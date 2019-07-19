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
import com.aimir.fep.mcu.data.ScheduleData;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;

public class ScheduleGetRunner{
	private static Logger log = LoggerFactory.getLogger(ScheduleGetRunner.class);
	
	private ScheduleData scheduleData;
	private Map<String, Object> singleResult;
	
	private List<String> mcuList = new ArrayList<>();
	private String mcuId;
	
	public ScheduleGetRunner() {}

	public ScheduleGetRunner(List<String> mcuList) {
		this.mcuList = mcuList;
	}
	
	public List<ScheduleData> startRun(List<String> mcuList){
		CommandGW gw = DataUtil.getBean(CommandGW.class);
		
		int maxPoolSize = Integer.parseInt(FMPProperty.getProperty("executor.max.pool.size","100"));
		ExecutorService executorService = Executors.newFixedThreadPool(maxPoolSize);
		log.debug("Thread Start~!");
		ScheduleGetResult schGetResult = new ScheduleGetResult();
		
		for(int i = 0; i<mcuList.size() ; i++) {
			mcuId = mcuList.get(i);
			log.info("1-for loop mcuId: " + mcuId);
			scheduleData = new ScheduleData();
			
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					scheduleData.setSysId(mcuId);
					log.info("Setting MCUID===================>"+mcuId);
					
					try {
						log.info("2-for loop run: " + mcuId);
						
						singleResult = new HashMap<String, Object>();
						singleResult= gw.cmdMcuGetSchedule_(mcuId, null);
						
						if(singleResult != null && singleResult.size() != 0) {
							singleResult.put("status","SUCCESS");
							scheduleData.setDataToMap(singleResult);
						}else {
							singleResult.put("status","FAIL");
							scheduleData.setDataToMap(singleResult);
						}
						
					} catch (Exception e) {
						log.info("99-for loop exception");
						log.error(e.toString());
						singleResult.put("status","FAIL");
						scheduleData.setDataToMap(singleResult);
					}
				}
			};	//Runnable End
			
			Future<ScheduleData> future = executorService.submit(runnable, scheduleData);
			
			try {
				scheduleData = future.get();
				schGetResult.setResult(scheduleData);
				
				log.info("future result3 = > " + schGetResult.schList.toString());
				
			}catch (Exception e) {
				log.debug(e.getMessage()+"#####Exception#####");
			}
		}//for End
		log.info("3-for Finish ");
		executorService.shutdown();
	
		return schGetResult.schList;
	}
}

class ScheduleGetResult{
	List<ScheduleData> schList = new ArrayList<ScheduleData>();
	synchronized void setResult(ScheduleData sd) {
		schList.add(sd);
	}
}


