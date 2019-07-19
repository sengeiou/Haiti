package com.aimir.fep.tool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.mcu.data.McuScheduleResult;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;

public class ScheduleSetRunner {
	private static Logger log = LoggerFactory.getLogger(ScheduleGetRunner.class);
	
	private McuScheduleResult mcuScheduleResult;

	private List<String> mcuList = new ArrayList<>();
	private String mcuId;
	private String[][] args;
	private String result;
	
	public ScheduleSetRunner() {}
	
	public ScheduleSetRunner(List<String> mcuList, String[][] args) {
		this.mcuList = mcuList;
		this.args = args;
	}

	public List<McuScheduleResult> startRun(List<String> mcuList, String[][] args){
		CommandGW gw = DataUtil.getBean(CommandGW.class);
		
		int maxPoolSize = Integer.parseInt(FMPProperty.getProperty("executor.max.pool.size","100"));
		ExecutorService executorService = Executors.newFixedThreadPool(maxPoolSize);
	
		log.debug("Thread Start~!");
		ScheduleSetResult schSetResult = new ScheduleSetResult();
		
		for(int i = 0; i<mcuList.size() ; i++) {
			mcuId = mcuList.get(i);
			log.info("1-for loop mcuId: " + mcuId);
			
			mcuScheduleResult = new McuScheduleResult();

			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					
					mcuScheduleResult.setSysId(mcuId);
					log.info("Setting MCUID===================>"+mcuId);
					
					try {
						log.info("2-for loop run: " + mcuId);	
						result = gw.cmdMcuGroupSetSchedule_(mcuId, args);
						
						if(result != null && result.length() != 0) {
							mcuScheduleResult.setResult(result);
						}else {
							mcuScheduleResult.setResult("FAIL");
						}
						
					} catch (Exception e) {
						log.info("99-for loop exception");
						log.debug(e.toString());
						mcuScheduleResult.setResult("FAIL("+e.toString()+")");
					}
				}
			};	//Runnable End
			
			Future<McuScheduleResult> future = executorService.submit(runnable, mcuScheduleResult);
			
			try {
				mcuScheduleResult = future.get();
				schSetResult.setResult(mcuScheduleResult);
				log.info("future result3 = > " + schSetResult.schList);
			}catch (Exception e) {
				log.debug(e.getMessage()+"#####Exception#####");
			}
		}//for End
		executorService.shutdown();
		return schSetResult.schList;
	}
}

class ScheduleSetResult{
	List<McuScheduleResult> schList = new ArrayList<McuScheduleResult>();
	synchronized void setResult(McuScheduleResult msr) {
		schList.add(msr);
	}
}

