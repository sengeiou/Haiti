package com.aimir.fep.schedule.task;

import java.io.BufferedReader;
import java.io.File;
import java.util.Date;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.io.InputStream;
import java.util.Properties;
import java.util.Scanner;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Service;
import com.aimir.constants.CommonConstants.OTAType;
import org.springframework.transaction.TransactionStatus;
import com.aimir.fep.protocol.smsp.SMSConstants;
import com.aimir.fep.protocol.smsp.SMSConstants.COMMAND_TYPE;
import com.aimir.fep.protocol.smsp.command.frame.sms.ResponseFrame;

import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.model.device.MCU;
import com.aimir.util.CalendarUtil;
import com.aimir.constants.CommonConstants.ResultStatus;
import com.aimir.constants.CommonConstants.Protocol;

/**
 * @author tatsumi
 *
 */
@Service
public class GetSORIAMeterKey 
{
    private static Log log = LogFactory.getLog(GetSORIAMeterKey.class);
    
    @Autowired
    MCUDao mcuDao;
    
    @Autowired
    ModemDao modemDao;
    
    @Autowired
    MeterDao meterDao;

    @Autowired
    SupplierDao supplierDao;
    
    @Resource(name="transactionManager")
    JpaTransactionManager txmanager;

    int		supplierId;    
    private String _filepath = "SORIAGetMeterKey_list.txt";
    private String		_obisaction = "set";
    private String		_obisparam;
    private final String commandType = "SORIAGetMeterKey";
    
    public static void main(String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{"/config/spring-fep-schedule.xml"}); 
        DataUtil.setApplicationContext(ctx);
        GetSORIAMeterKey task = ctx.getBean(GetSORIAMeterKey.class);
        log.info("======================== GetSORIAMeterKey start. ========================");
		writeResult("start--------------------------------------------------");
        task.execute(args);
        log.info("======================== GetSORIAMeterKey end. ========================");
		writeResult("end  --------------------------------------------------");
        System.exit(0);
    }

	public static void writeResult(String resultStr){
		String fileName = "./log/GetSORIAMeterKey.txt";
		
        try{
            File file = new File(fileName) ;
            FileWriter fw = new FileWriter(file, true) ;
            fw.write(resultStr +"\n");
            fw.flush();
            fw.close(); 
        }catch(Exception e){
            log.error(e);
        }
	}
	
	private List<String> getTargetList() {
		List<String> targetList = null;
		InputStream fileInputStream = getClass().getClassLoader().getResourceAsStream(_filepath);
		if (fileInputStream != null) {
			targetList = new LinkedList<String>();
			log.info("Target List size ===> " + targetList.size());

			@SuppressWarnings("resource")
			Scanner scanner = new Scanner(fileInputStream);
			while (scanner.hasNextLine()) {
				String target = scanner.nextLine().trim();
				if (!target.equals("")) {
					targetList.add(target);
					log.info("Target => " + target);
				}
			}
		} else {
			log.warn("[" + _filepath + "] file not found" );
		}

		return targetList;
	}
	

    public void execute(String[] args) {
        long _timeout = 3600;

        _filepath = FMPProperty.getProperty("soria.cmd.obis.target.file", "SORIAGetMeterKey_list.txt");
        
        log.debug("target.file => " + _filepath);
		Date startDate = new Date();
		long startTime = startDate.getTime();

		log.info("########### START Command[SORIAGetMeterKey] - " + CalendarUtil.getDatetimeString(startDate, "yyyy-MM-dd HH:mm:ss") + " ###############");

		List<String> targetList = getTargetList();

		try {
			if(targetList != null && 0 < targetList.size()){
				for(String meterSerial : targetList){
					commandStart(meterSerial);
				}			
			}else{
				log.warn("Cannot find target list.");
			}

			long endTime = System.currentTimeMillis();
			log.info("FINISHED Command[SORIAGetMeterKey] - Elapse Time : " + (endTime - startTime) / 1000.0f + "s");

			log.info("########### END CommandBatch Task[SORIAGetMeterKey] ############");
        }
        catch (Exception ee) {}
        finally {
        }
    }
    
	private void commandStart(String meterSerial) {
		Modem modem = null;
		OTAType targetType;
		
		/*
		 * 1. Init
		 */
		TransactionStatus txstatus = null;
		txstatus = txmanager.getTransaction(null);

		try {
			Meter meter = meterDao.findByCondition("mdsId", meterSerial);
			if (meter == null) {
				throw new Exception("Meter[" + meterSerial + "] is not exists.");
			}

			modem = meter.getModem();
			if (modem == null) {
				throw new Exception("Meter[" + meterSerial + "] have no modem.");
			}

			if (modem.getModemType() == ModemType.MMIU && (modem.getProtocolType() == Protocol.SMS)) { // MBB Modem
				targetType = OTAType.METER_MBB;
			} else if (modem.getModemType() == ModemType.MMIU && (modem.getProtocolType() == Protocol.IP
					|| modem.getProtocolType() == Protocol.GPRS) ) { // Ethernet Modem
				targetType = OTAType.METER_ETHERNET;
			} else if (modem.getModemType() == ModemType.SubGiga && modem.getProtocolType() == Protocol.IP) { // RF Modem
				targetType = OTAType.METER_RF;
			} else {
				throw new Exception("Unknown Modem type or Protocol type.");
			}

			log.info("###  Meter = ["+meterSerial+"], Modem = ["+modem.getDeviceSerial()+"], Target Type = ["+targetType+"] ###" );
		} catch (Exception e) {
			if (txstatus != null) {
				txmanager.rollback(txstatus);
			}
			log.error("Task Excute transaction error - " + e, e);
			return;
		}
		if (txstatus != null) {
			txmanager.commit(txstatus);
		}
		
		/*
		 * 2. Command execute.
		 */
		try {
			Map<String, Object> result = new HashMap<>();
			CommandGW commandGw = DataUtil.getBean(CommandGW.class);

			// ETHERNET, RF인경우
			if (targetType == OTAType.METER_ETHERNET || targetType == OTAType.METER_RF) {
				try {
					result = commandGw.cmdSORIAGetMeterKey(meterSerial);
				} catch (Exception e) {
					log.error("Command Excute Exception - Target type = [" + targetType + "] Meter = [" + meterSerial + "] Modem = [" + modem.getDeviceSerial() + "]", e);
				}
			}
			// MBB 인 경우
			else if (targetType == OTAType.METER_MBB) {
				try {
//					Map<String, String> asyncModemMBBParamMap = new HashMap<String, String>();
//					asyncModemMBBParamMap.put("meterId", meterSerial);

//					Map<String, String> mbbMeterResult = sendSmsForCmdServer(modem, SMSConstants.MESSAGE_TYPE.REQ_NON_ACK.getTypeCode(), SMSConstants.COMMAND_TYPE.NI.getTypeCode(), commandType, asyncModemMBBParamMap);
//					if (mbbMeterResult != null && 0 < mbbMeterResult.size()) {
//						result.putAll(mbbMeterResult);
//					}
					log.info("Meter = [" + meterSerial + "] Modem = [" + modem.getDeviceSerial() + "] skip");
				} catch (Exception e) {
					log.error("Command Excute Exception - Target type = [" + targetType + "] Meter = [" + meterSerial + "] Modem = [" + modem.getDeviceSerial() + "]", e);
				}

			} else {
				log.error("Unknown target type.");
			}

			log.debug("[" + targetType + "] cmdSORIAGetMeterKey Result = ["+result == null ? "null" : result.toString()+"]");

			/*
			 * result
			 */
			ResultStatus status = ResultStatus.SUCCESS;
			String rtnMessage = "";
			if (result != null && 0 < result.size()) {
				if (!Boolean.valueOf((boolean) result.get("result"))) {
					status = ResultStatus.FAIL;
					rtnMessage = String.valueOf(result.get("resultValue"));
				}else{
					rtnMessage = String.valueOf(result.get("resultValue"));	
				}
				
				log.debug("cmdSORIAGetMeterKey returnValue =>> " + rtnMessage);
			} else {
				status = ResultStatus.FAIL;
				rtnMessage = "FAIL : result receive fail.";
				log.debug("FAIL : result receive fail.");
			}			
			
			
			if(status == ResultStatus.SUCCESS){
				log.info(rtnMessage);	
			}else{
				log.info("------ fail -----");
			}
		} catch (Exception e) {
			//logger.error("FAIL : Command Fail - [{}][{}] - {}", targetType, meterSerial, e);
			log.error("FAIL : Command Fail - [" + targetType + "][" + meterSerial + "] - " + e, e);
		}
	}

}
