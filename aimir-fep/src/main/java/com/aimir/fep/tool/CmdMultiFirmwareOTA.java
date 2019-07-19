package com.aimir.fep.tool;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants.OTATargetType;
import com.aimir.dao.device.MeterDao;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.command.ws.client.ResponseMap;
import com.aimir.fep.meter.data.MeterData;
import com.aimir.fep.util.DataUtil;
import com.aimir.model.device.MCU;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;


/**
 * Running Execute DmdNiGet or DmdNiSet
 * @author 
 * arg[0]=={modemId} arg[1]=={requestType} arg[2]=={attributeId} arg[3]=={param}
 */

public class CmdMultiFirmwareOTA 
{
	private static Log log = LogFactory.getLog(CmdMultiFirmwareOTA.class);
	
	
	
	private void execCommand(String targetTypeName, String deviceId, String takeOver, boolean useNullBypass, String firmwareId, String optVersion, String optModel, String optTime) {
		ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[] { "/config/spring-fep-schedule.xml" });
		DataUtil.setApplicationContext(ctx);
		
		CommandGW gw = DataUtil.getBean(CommandGW.class);
					
		log.info("Execute start...");

		try {	
			OTATargetType targetType = OTATargetType.getItem(targetTypeName.toUpperCase());
			List<String> deviceList = new ArrayList<String>(Arrays.asList(deviceId.split(",")));
			
			Map<String, Object> result = new HashMap<String, Object>();
			result = gw.cmdMultiFirmwareOTA(targetType, deviceList, takeOver, useNullBypass, firmwareId, optVersion, optModel, optTime);
	
	        for (Map.Entry<String, Object> e : result.entrySet()) {
	            log.debug("key["+e.getKey()+"], value["+ e.getValue()+"]");
	        }		
		}catch(Exception e){
			log.error(e,e);
		}		
		log.info("All job is finish");
		
	}
	

	
	public static void main(String[] args) {
		log.info("ARG_0[" + args[0] + "] ARG_1[" + args[1] + "] ARG_2[" + args[2] + 
				"] ARG_3[" + args[3] + "] ARG_4[" + args[4] +
				"] ARG_5[" + args[5] + "] ARG_6[" + args[6] + "] ARG_7[" + args[7] +
				"]");
				
		
		String targetTypeName = args[0];
		String deviceId = args[1];
		String takeOver = args[2];
		String useNullBypass = args[3];        
		String firmwareId = args[4];
		String optVersion = args[5];
		String optModel = args[6];
		String optTime = args[7];
	
		CmdMultiFirmwareOTA forJob = new CmdMultiFirmwareOTA();
		forJob.execCommand(targetTypeName, 
				deviceId,
				takeOver, 
				Boolean.parseBoolean(useNullBypass),
				firmwareId, 
				optVersion,
				optModel,
				optTime);
		
		System.exit(0);						
	}		
	
}
