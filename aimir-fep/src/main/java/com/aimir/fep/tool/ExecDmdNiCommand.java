package com.aimir.fep.tool;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.dao.device.ModemDao;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.util.DataUtil;
import com.aimir.model.device.MMIU;
import com.aimir.model.device.Modem;


/**
 * Running Execute DmdNiGet or DmdNiSet
 * @author 
 * arg[0]=={modemId} arg[1]=={requestType} arg[2]=={attributeId} arg[3]=={param}
 */
@Service
public class ExecDmdNiCommand 
{
	private static Log log = LogFactory.getLog(ExecDmdNiCommand.class);

    @Autowired
    ModemDao modemDao;	

	public static void main(String[] args) {
//		log.info("ARG_0[" + args[0] + "] ARG_1[" + args[1] + 
//				"] ARG_2[" + args[2] + "] ARG_3[" + args[3] + 
//				"] ARG_4[" + args[4] + "] ARG_5[" + args[5] + "]");		
//				
//		String modemId = args[0];
//		String requestType = args[1];
//		String attrId = args[2];
//		String attrParam = args[3];
		String dev =  args[4];
//		String direct = args[5];
		
//		ExecDmdNiCommand forJob = new ExecDmdNiCommand();
//		forJob.execCommand(modemId, requestType, attrId, attrParam, dev, direct);
//		System.exit(0);	
		
		if (dev.equals("DEV")) {
			ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[] { "/config/spring-fep-schedule-dev.xml" });
			DataUtil.setApplicationContext(ctx);
			ExecDmdNiCommand task = ctx.getBean(ExecDmdNiCommand.class);
	        log.info("======================== ExecDmdNiCommand start. ========================");
	        task.execute(args);
	        log.info("======================== ExecDmdNiCommand end. ========================");
		} else {
			ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[] { "/config/spring-fep-schedule.xml" });
			DataUtil.setApplicationContext(ctx);
			ExecDmdNiCommand task = ctx.getBean(ExecDmdNiCommand.class);
	        log.info("======================== ExecDmdNiCommand start. ========================");
	        task.execute(args);
	        log.info("======================== ExecDmdNiCommand end. ========================");
		}
		
        System.exit(0);
        
	}		

    public void execute(String[] args) {
    	
		log.info("ARG_0[" + args[0] + "] ARG_1[" + args[1] + 
				"] ARG_2[" + args[2] + "] ARG_3[" + args[3] + 
				"] ARG_4[" + args[4] + "] ARG_5[" + args[5] + "]");		
				
		String modemId = args[0];
		String requestType = args[1];
		String attrId = args[2];
		String attrParam = args[3];
		String dev =  args[4];
		String direct = args[5];
		
		CommandGW gw = DataUtil.getBean(CommandGW.class);
		
		log.info("Execute start...");

		try {
			Map<String, String> result = new HashMap<String, String>();
			
			if (modemDao == null) {
				log.error("modemDao is null. ");
				return;
			}
			Modem modem = modemDao.get(modemId);
			//Modem modem = modemDao.get(Integer.parseInt(modemId));
			if (modem == null) {
				log.error("Modem is null. [" + modemId + "]");
				return;
			}
			// MBB(SMS)
			if( ((modem instanceof MMIU) && (modem.getProtocolType() == Protocol.SMS)) ){			
			    // Ethernet
			}else if(((modem instanceof MMIU) && (modem.getProtocolType() == Protocol.IP || modem.getProtocolType() == Protocol.GPRS)) || 
					 (direct.equals("DIRECT"))
					){
				result = gw.cmdGeneralNiCommand(String.valueOf(modem.getId()), requestType, attrId, attrParam);
			    // RF
			}else{			
				result = gw.cmdExecDmdNiCommand(String.valueOf(modem.getId()),requestType,attrId, attrParam);
			}
	        for (Map.Entry<String, String> e : result.entrySet()) {
	            log.debug("[MODEM ID:" + modemId + "] REQUEST TYPE: " + requestType + "] key["+e.getKey()+"], value["+ e.getValue()+"]");
	        }		
		}catch(Exception e){
			log.error(e,e);
		}		

    }
    
//    public void execCommand(String modemId, String requestType, String attrId, String param, String dev, String direct) {
//
//		if (dev.equals("DEV")) {
//			ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[] { "/config/spring-fep-schedule-dev.xml" });
//			DataUtil.setApplicationContext(ctx);
//		} else {
//			ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[] { "/config/spring-fep-schedule.xml" });
//			DataUtil.setApplicationContext(ctx);
//		}
//		
//		CommandGW gw = DataUtil.getBean(CommandGW.class);
//					
//		log.info("Execute start...");
//
//		try {
//			Map<String, String> result = new HashMap<String, String>();
//			
//			if (modemDao == null) {
//				log.error("modemDao is null. ");
//				return;
//			}
//			Modem modem = modemDao.get(modemId);
//			//Modem modem = modemDao.get(Integer.parseInt(modemId));
//			if (modem == null) {
//				log.error("Modem is null. [" + modemId + "]");
//				return;
//			}
//			// MBB(SMS)
//			if( ((modem instanceof MMIU) && (modem.getProtocolType() == Protocol.SMS)) ){			
//			    // Ethernet
//			}else if(((modem instanceof MMIU) && (modem.getProtocolType() == Protocol.IP)) || 
//					 (direct.equals("DIRECT"))
//					){
//				result = gw.cmdGeneralNiCommand(String.valueOf(modem.getId()), requestType, attrId, param);
//			    // RF
//			}else{			
//				result = gw.cmdExecDmdNiCommand(String.valueOf(modem.getId()),requestType,attrId, param);
//			}
//	        for (Map.Entry<String, String> e : result.entrySet()) {
//	            log.debug("[MODEM ID:" + modemId + "] REQUEST TYPE: " + requestType + "] key["+e.getKey()+"], value["+ e.getValue()+"]");
//	        }		
//		}catch(Exception e){
//			log.error(e,e);
//		}		
//		
//	}	
	
}
