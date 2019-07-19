package com.aimir.fep.tool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.constants.CommonConstants.TR_STATE;
import com.aimir.dao.device.AsyncCommandLogDao;
import com.aimir.dao.device.AsyncCommandResultDao;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.device.MMIUDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.protocol.smsp.SMSConstants;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.model.device.AsyncCommandLog;
import com.aimir.model.device.AsyncCommandResult;
import com.aimir.model.device.MCU;
import com.aimir.model.device.MMIU;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.model.system.Code;
import com.aimir.model.system.Location;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service
public class UpdateMeterFWVersion {
    private static Log log = LogFactory.getLog(UpdateMeterFWVersion.class);
    
    @Autowired
    MCUDao mcuDao;
    
    @Autowired
    ModemDao modemDao;
    
    @Autowired
    MeterDao meterDao;

    @Autowired
    SupplierDao supplierDao;
    
	@Autowired
	LocationDao locationDao;
	
	@Autowired
	CodeDao	codeDao;
	
	@Autowired
	MMIUDao mmiuDao;
	
	@Autowired
	AsyncCommandLogDao asyncCommandLogDao;
	
	@Autowired
	AsyncCommandResultDao resultDao;
	
    @Resource(name="transactionManager")
    JpaTransactionManager txmanager;	
    
    private int  		_maxThreadWorker = 10;
    private int 		_timeout = 3600;
    private String		_location = "";
    private String		_msa = "";
    private String		_mds_id = "";
    private int			_testmode = 0;
	Map<String,JSONObject> fileMap = new HashMap<String,JSONObject>();
    
    public static void main(String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{"/config/spring-fep-schedule.xml"}); 
        DataUtil.setApplicationContext(ctx);
        UpdateMeterFWVersion task = ctx.getBean(UpdateMeterFWVersion.class);
        log.info("======================== UpdateMeterFWVersion start. ========================");
        task.execute(args);
        log.info("======================== UpdateMeterFWVersion end. ========================");
        System.exit(0);
    }
 
    public class McuDeviceList {
    	Integer mcuId;
    	List<String> mdsIdList;
    }
    
    public class ResultJsonType {
    	String MdsId;
    	FwData FwDatas;
    	public ResultJsonType(){
        }
    	public ResultJsonType(String mds, String ver, String stat, String dt) {
    		this.MdsId = mds;
    		this.FwDatas = new FwData();
    		this.FwDatas.Version = ver;
    		this.FwDatas.Status = stat;
    		this.FwDatas.DateTime = dt;
    	}
    }
    
    public class FwData {
    	String Version;
    	String Status;
    	String DateTime;
    }    
 
    public void execute(String[] args) {

		byte paramFlg = 0;
    	        
		log.info("ARG_0[" + args[0] + "] ARG_1[" + args[1] + "] ARG_2[" + args[2] + "] ARG_3[" + args[3] + 
				"] ARG_4[" + args[4] +"] ARG_5[" + args[5] +"]");
 
		// Location
        if (args[0].length() > 0) {
        	_location = args[0];
        	paramFlg = 0x01;
        }
        // MSA
        if (args[1].length() > 0) {
        	_msa = args[1];
        	paramFlg += 0x02;
        }
        else {
        	_msa = "NULL";
        	paramFlg += 0x02;
        }        
        // MDS_ID
        if (args[2].length() > 0) {
        	_mds_id = args[2];
        	paramFlg += 0x04;
        }
        if(!(((paramFlg & 0x01) == 0x01 && (paramFlg & 0x02) == 0x02) || ((paramFlg & 0x04) == 0x04))) {
        	return;
        }
        if(((paramFlg & 0x01) == 0x01) && ((paramFlg & 0x02) == 0x02) && ((paramFlg & 0x04) == 0x04)) {
        	return;
        }
        // MAX_THREAD_WORKER
        if (args[3].length() > 0) {
            _maxThreadWorker = Integer.parseInt(args[3]);        	
        }
        // TIMEOUT
        if (args[4].length() > 0) {
            _timeout = Integer.parseInt(args[4]);        	
        }
        
        if (args[5].length() > 0) {
            _testmode = Integer.parseInt(args[5]);        	
        }
        
        log.info("Start UpdateMeterFWVersion maxThreadWorker[" + _maxThreadWorker + "]");

    	List<McuDeviceList> mculist = new ArrayList<McuDeviceList>();    	
    	List<McuDeviceList> exceptMculist = new ArrayList<McuDeviceList>();

        if(((paramFlg & 0x01) == 0x01 && (paramFlg & 0x02) == 0x02)) {
            try {
            	List<Meter> list = getMeter(_location, _msa);     	
            	if(!list.isEmpty()){
            		fileMap = createFileMap();
            		List<Meter> targetList = new ArrayList<Meter>();
            		targetList = createMeterList(list);
            		List<List<McuDeviceList>> resultList = new ArrayList<List<McuDeviceList>>();
            		resultList = getMeterList(targetList);
            		mculist = resultList.get(0);
            		exceptMculist = resultList.get(1);
            	}
            	else{
            		return;
            	}
            	
            	// in the case of dcu
            	int cnt = 0;

            	ExecutorService pool = Executors.newFixedThreadPool(_maxThreadWorker);
            	UpdateMeterFWVersionThread threads[] = new UpdateMeterFWVersionThread[mculist.size()];
            
        		int i = 0;
            	for (McuDeviceList mcuMeter : mculist) {
            		log.info(cnt++ + ": MCU[" + mcuMeter.mcuId + "] Update Meter FW Version");

            		threads[i] = new UpdateMeterFWVersionThread(mcuMeter);
            		pool.execute(threads[i]);
            		i++;
            	}

                log.info("ExecutorService for mcu shutdown.");
                pool.shutdown();
                log.info("ExecutorService for mcu awaitTermination. [" + _timeout + "]sec");
                pool.awaitTermination(_timeout, TimeUnit.SECONDS);

                // in the case of except dcu
                pool = Executors.newFixedThreadPool(_maxThreadWorker);
                UpdateMmiuMeterFWVersionThread threads2[] = new UpdateMmiuMeterFWVersionThread[exceptMculist.get(0).mdsIdList.size()];
            
        		i = 0;
            	for ( String mdsId : exceptMculist.get(0).mdsIdList) {
            		log.info(cnt++ + ": MMIU Meter [" + mdsId + "] Update Meter FW Version");

            		threads2[i] = new UpdateMmiuMeterFWVersionThread(mdsId);
            		pool.execute(threads2[i]);
            		Thread.sleep(1000);
            		i++;
            	}
                log.info("ExecutorService for mcu shutdown.");
                pool.shutdown();
                log.info("ExecutorService for mcu awaitTermination. [" + _timeout + "]sec");
                pool.awaitTermination(_timeout, TimeUnit.SECONDS);
            }
            catch (Exception e) {
            	log.debug(e.getMessage());
            }
            finally {
            }
        }
        else{
        	try {
        		boolean mdsIdExist = getMeterFromMdsId(_mds_id);
        		if(mdsIdExist){
        			updateMeterFWVersionFromMdsId(_mds_id);
        		}
			} catch (Exception e) {
			}   
        }
    }  

    private List<List<McuDeviceList>> getMeterList(List<Meter> meters) {
    	/*
    	 * Output List<McuModemList> is list of McuModemList. 
    	 * Output meters is list of meter which doesn't have a mcu.
    	 */
    	try{
	    	log.debug("getMeterList(meters[" + meters.size() + "])");

    		List<McuDeviceList> exceptMcuList = new ArrayList<McuDeviceList>();
	    	List<McuDeviceList> mcuList = new ArrayList<McuDeviceList>();
	    	List<List<McuDeviceList>> result = new ArrayList<List<McuDeviceList>>();

	    	for (Meter m : meters) {

	        	Modem modem = m.getModem();
	        	if (modem == null) {
					log.debug("Meter[" + m.getMdsId() + "] . Modem is null .");
	        		continue;
	        	}
				MCU mcu = modem.getMcu();

				if (mcu != null) {
					log.debug("MCU[" + mcu.getId() + "] . Meter[" + m.getMdsId() + "] . Modem[" + modem.getDeviceSerial() + "] .");
					int i;
					for (i = 0; i < mcuList.size(); i++) {	// Search the same mcu.
						if ( mcuList.get(i).mcuId.compareTo(mcu.getId())==0 ) { 
							break;
						}
					}
					if (i < mcuList.size()) {	// find!
						log.debug("MCU[" + mcu.getId() + "] has been already listed." );
						mcuList.get(i).mdsIdList.add(m.getMdsId());
						continue;
					}
					McuDeviceList mml = new McuDeviceList();
					mml.mcuId = mcu.getId();
					mml.mdsIdList = new ArrayList<String>();
					mml.mdsIdList.add(m.getMdsId());
					mcuList.add(mml);
				} else {
					log.debug("MCU is null. Meter[" + m.getMdsId() + "] . Modem[" + modem.getDeviceSerial() + "] .");		
					if(exceptMcuList.size() == 0){
						McuDeviceList mml = new McuDeviceList();
						mml.mdsIdList = new ArrayList<String>();
						mml.mdsIdList.add(m.getMdsId());
						exceptMcuList.add(mml);
					}
					else{
						exceptMcuList.get(0).mdsIdList.add(m.getMdsId());
					}
				}
	        }
	        result.add(mcuList);
	        result.add(exceptMcuList);
	        return result;
    	}
        catch (Exception e) {
        	log.debug(e, e);
        	return null;
        }   	
    } 

    protected class UpdateMeterFWVersionThread extends Thread {
    	McuDeviceList mcuDeviceList;
    	MCU mcu = null;
    	CommandGW commandGw = DataUtil.getBean(CommandGW.class);

    	public UpdateMeterFWVersionThread(McuDeviceList list) {
    		try {
	    		this.mcuDeviceList = list;
	    		mcu = mcuDao.get(list.mcuId);
            }
            catch (Exception ee) {}
    	}
    	public void run() {
    		Map<String,Object> result = new HashMap<String,Object>();

            if (mcu != null) {
            	log.info("ThreadID[" + Thread.currentThread().getId() + "] UpdateMeterFWVersionThread() thread start. MCU[" + mcuDeviceList.mcuId + "]");

            	try {
            		List<String> mdsList = mcuDeviceList.mdsIdList;
            		String fwVersion = "";
            		String cmdResult = "";
            		String dateStr = "";
            		String status = "";
        			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
            		JSONArray resultArray = new JSONArray();
            		for (int i = 0; i < mdsList.size(); i++) {
            			JSONObject itemsJson = new JSONObject();
            			JSONObject itemJson = new JSONObject();
                		FwData fwData = new FwData();
                		ResultJsonType rjt = new ResultJsonType();	
            			result = commandGw.cmdGetMeterFWVersion(mdsList.get(i));
            			if(result.equals(null)) {
                			fwVersion = "";
                			status = "0";			
            			}
            			else {
                			fwVersion = String.valueOf(result.get("resultValue"));
                			cmdResult = String.valueOf(result.get("result"));
//                			if(cmdResult.equals("false")) {
                			if ((cmdResult.equals("false")) || 
                					(fwVersion.equals(null)) ||
                					(fwVersion.equals("null")) ||
                					(fwVersion.isEmpty())) {
                    			fwVersion = "";
                    			status = "0";		
                			}
                			else {
                    			status = "1";	
                			}
            			}
            			Date date = new Date();
            			dateStr = sdf.format(date).toString();
            			rjt.MdsId = mdsList.get(i).toString();
            			fwData.Version = fwVersion;
            			fwData.Status = status;
            			fwData.DateTime = dateStr;
            			rjt.FwDatas = fwData;
            			itemJson.put("Version", fwData.Version);
            			itemJson.put("Status", fwData.Status);
            			itemJson.put("DateTime", fwData.DateTime);
            			itemsJson.put(rjt.MdsId, itemJson);
            			resultArray.add(itemsJson);
            		}
            		updateResultFile(resultArray);
            	} catch (Exception e){
            		log.info("ThreadID[" + Thread.currentThread().getId() + "] UpdateMeterFWVersionThread() thread end. MCU[" + mcu.getId() + "] is failed.");
            	}	
            	log.info("ThreadID[" + Thread.currentThread().getId() + "] UpdateMeterFWVersionThread() thread end. MCU[" + mcu.getId() + "]");            	
            }
            else {
            	log.debug("MCU[" + mcu.getId() + "] is null.");
            }
    	}
    }
    
    protected class UpdateMmiuMeterFWVersionThread extends Thread {
    	String mdsId;
    	MCU mcu = null;
    	CommandGW commandGw = DataUtil.getBean(CommandGW.class);

    	public UpdateMmiuMeterFWVersionThread(String mdsId) {
    		try {
    			this.mdsId = mdsId;
            }
            catch (Exception e) {}
		}
    	public void run() {
    		Map<String, Object> resultMap = new HashMap<String, Object>();
            try {
        		String fwVersion = "";
        		String cmdResult = "";
        		String dateStr = "";
        		String status = "";
    			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
                Meter meter = meterDao.get(mdsId);
                Modem modem = meter.getModem();
        		JSONArray resultArray = new JSONArray();		
        		FwData fwData = new FwData();
        		ResultJsonType rjt = new ResultJsonType();
    			JSONObject itemsJson = new JSONObject();
    			JSONObject itemJson = new JSONObject();
                log.debug("UpdateMeterFWVersion() MeterID[" + meter.getMdsId() + "] ModemType[" + modem.getModemType().name() + 
        				"] diff[" + meter.getTimeDiff() + "]");

                CommandGW commandGw = DataUtil.getBean(CommandGW.class);                        
                
                if((modem.getModemType() == ModemType.MMIU) && (modem.getProtocolType() == Protocol.SMS)) {
                	// MBB
                	// for AutoTimeSync Task
           			String cmd = "cmdGetMeterFWVersion";
    				String rtnString = "";
           			Map<String, String> paramMap = new HashMap<String, String>();
        			//paramSet	
        			paramMap.put("meterId", meter.getMdsId());

            		MMIU mmiuModem = mmiuDao.get(modem.getId());           				
       				List<String> paramListForSMS  = new ArrayList<String>();

       		        paramListForSMS.add(FMPProperty.getProperty("smpp.hes.fep.server", ""));
       		        paramListForSMS.add(FMPProperty.getProperty("soria.modem.tls.port", ""));
       		        paramListForSMS.add(FMPProperty.getProperty("smpp.auth.port", ""));
               		        
       		        String cmdMap = null;
       		        ObjectMapper om = new ObjectMapper();
       		        if(paramMap != null)
       		        	cmdMap = om.writeValueAsString(paramMap);
               		                  		    	
       		    	rtnString = commandGw.sendSMS(
       		    			cmd, 
       		    			SMSConstants.MESSAGE_TYPE.REQ_NON_ACK.getTypeCode(), 
       		    			mmiuModem.getPhoneNumber(), 
       		    			modem.getDeviceSerial(),
       		    			SMSConstants.COMMAND_TYPE.NI.getTypeCode(), 
       		    			paramListForSMS, cmdMap);
       		    	if (rtnString.equals("F") || rtnString.equals("CF")) { // Fail
       					log.debug(rtnString);
       					return;
       				} else {
       				    int loopCount = 0;
       	                Integer lastStatus = null;
       	                while(loopCount < 9) {
//       	                    lastStatus = asyncCommandLogDao.getCmdStatus(modem.getDeviceSerial(), cmd);
       	                	lastStatus = getAsyncCommandLogStatus(modem.getDeviceSerial(), Long.parseLong(rtnString));
       	                	log.debug("CmdStatus [" + lastStatus + "]");
       	                	if ((lastStatus != null) && (TR_STATE.Success.getCode() == lastStatus)) {
       	                        break;
       	                    }
       	                    loopCount++;
       	                    Thread.sleep(10000);
       	                }
       					if (TR_STATE.Success.getCode() != lastStatus) {
       						log.debug("FAIL : Communication Error but Send SMS Success.  " + cmd);
       						return;
       					} else {
       						ObjectMapper mapper = new ObjectMapper();
       						List<AsyncCommandResult> asyncResult = resultDao.getCmdResults(modem.getDeviceSerial(), Long.parseLong(rtnString), cmd); //ASYNC_COMMAND_RESULT에서 결과 값을 가져옴
                			Date date = new Date();
                			dateStr = sdf.format(date).toString();
                			rjt.MdsId = mdsId;
       						if (asyncResult == null || asyncResult.size() <= 0) {
       							log.debug("FAIL : Send SMS but fail to execute " + cmd);
           						// Fail
                    			fwData.Version = "";
                    			fwData.Status = "0";
                    			fwData.DateTime = dateStr;
                    			rjt.FwDatas = fwData;
                    			itemJson.put("Version", fwData.Version);
                    			itemJson.put("Status", fwData.Status);
                    			itemJson.put("DateTime", fwData.DateTime);
                    			itemsJson.put(rjt.MdsId, itemJson);
                    			resultArray.add(itemsJson);
       						} else { // Success
       							String resultStr = "";
       							for (int j = 0; j < asyncResult.size(); j++) {
       								resultStr += asyncResult.get(j).getResultValue();
       							}
       							log.debug("Async result string[" + resultStr + "]");
       							Map<String, String> map = mapper.readValue(resultStr, new TypeReference<Map<String, String>>() {
       							});
       							fwVersion = map.get("resultValue");
       		        			cmdResult = String.valueOf(resultMap.get("result"));
       		        			if ((cmdResult.equals("false")) || (fwVersion.isEmpty())) {
       		            			fwVersion = "";
       		            			status = "0";		
       		        			}
       		        			else {
       		            			status = "1";	
       		        			}
                    			fwData.Version = fwVersion;
                    			fwData.Status = status;
                    			fwData.DateTime = dateStr;
                    			rjt.FwDatas = fwData;
                    			itemJson.put("Version", fwData.Version);
                    			itemJson.put("Status", fwData.Status);
                    			itemJson.put("DateTime", fwData.DateTime);
                    			itemsJson.put(rjt.MdsId, itemJson);
                    			resultArray.add(itemsJson);
       							log.debug("Success get result");
       						}
       					}
       				}
                }
                else if((modem.getModemType() == ModemType.MMIU) && (modem.getProtocolType() == Protocol.IP || modem.getProtocolType() == Protocol.GPRS)) {  
        			fwData = new FwData();
        			resultMap = commandGw.cmdGetMeterFWVersion(mdsId);
        			fwVersion = String.valueOf(resultMap.get("resultValue"));
        			cmdResult = String.valueOf(resultMap.get("result"));
        			if ((cmdResult.equals("false")) || (fwVersion.isEmpty())) {
            			fwVersion = "";
            			status = "0";		
        			}
        			else {
            			status = "1";	
        			}
        			Date date = new Date();
        			dateStr = sdf.format(date).toString();
        			rjt.MdsId = mdsId;
        			fwData.Version = fwVersion;
        			fwData.Status = status;
        			fwData.DateTime = dateStr;
        			rjt.FwDatas = fwData;
        			itemJson.put("Version", fwData.Version);
        			itemJson.put("Status", fwData.Status);
        			itemJson.put("DateTime", fwData.DateTime);
        			itemsJson.put(rjt.MdsId, itemJson);
        			resultArray.add(itemsJson);
                }
                updateResultFile(resultArray);
            }
            catch (Exception e) {
            }
    	}
    }

    private List<Meter> getMeter(String name, String msa) throws Exception {
    	List<Meter> list = new ArrayList<Meter>();
    	List<Meter> tmpList = new ArrayList<Meter>();
        try {
        	log.debug("getMeter(" + name + "," + msa + ")");
        	Set<Condition> conditionList = new HashSet<Condition>();    
			Location location = locationDao.getLocationByName(name).get(0);
			Code code = codeDao.getCodeByName("MeterType");
			List<Code> codeList = codeDao.getChildCodes(code.getCode());
			int energyMeterCode = 0;
			for (int i=0; i < codeList.size(); i++) {
				Code tmpCode = codeList.get(i);
				if (tmpCode.getName().equals("EnergyMeter")) {
					energyMeterCode = tmpCode.getId();
					break;
				}
			}
			
			if((location != null) && (code != null)) {
	        	log.debug("location.id = " + location.getId() + ", meterType.id = " + energyMeterCode);
				conditionList.add(new Condition("location.id",
						new Object[] { location.getId()}, null, Restriction.EQ));
				conditionList.add(new Condition("meterType.id",
						new Object[] { energyMeterCode}, null, Restriction.EQ));
				
				if (msa.equals("NULL")) {
					conditionList.add(new Condition("msa",
							null, null, Restriction.NULL));
				}
				else {
					conditionList.add(new Condition("msa",
							new Object[] { msa }, null, Restriction.EQ));
				}
				tmpList = meterDao.findByConditions(conditionList);
				list.addAll(tmpList);
			}
			else {
	        	log.debug("code or location is null");
	        	return null;
			}
				
        	log.debug("Meter count = " + list.size());
			return list;
        }
        catch (Exception e) {
        	log.debug(e.getMessage());
        	throw e;
        }
        finally {
        }
    }

    synchronized private Integer getAsyncCommandLogStatus(String deviceId, long trId) throws Exception {
    	TransactionStatus txstatus = null;
    	List<AsyncCommandLog> list = new ArrayList<AsyncCommandLog>();
    	
        try {
        	txstatus = txmanager.getTransaction(null);
        	
        	log.debug("getAsyncCommandLogStatus(" + deviceId + "," + trId + ")");
//        	Set<Condition> conditionList = new HashSet<Condition>();    
//			conditionList.add(new Condition("deviceId",
//					new Object[] { deviceId}, null, Restriction.EQ));
//			conditionList.add(new Condition("id.trId",
//					new Object[] { trId}, null, Restriction.EQ));
//			list = asyncCommandLogDao.findByConditions(conditionList);
//			if (list.isEmpty()) {
//				return null;
//			}
        	Integer lastStatus = asyncCommandLogDao.getCmdStatusByTrId(deviceId, trId);

			txmanager.commit(txstatus);
//			return list.get(0).getState();
			
			return lastStatus;
        }
        catch (Exception e) {
        	log.debug(e,e);
    		if (txstatus != null&& !txstatus.isCompleted())
    			txmanager.rollback(txstatus);        	
        }
		return null;
    }    
    
    
    private boolean getMeterFromMdsId(String mdsId) throws Exception {
    	List<Meter> list = new ArrayList<Meter>();
        try {
        	Set<Condition> conditionList = new HashSet<Condition>();    
			conditionList.add(new Condition("mdsId",
						new Object[] { mdsId }, null, Restriction.EQ));
			list = meterDao.findByConditions(conditionList);
			if(list.size() == 0){
            	return false;
			}
        }
        catch (Exception e) {
        	log.debug(e.getMessage());
        	throw e;
        }
        finally {
        }
        return true;
    }
    private void updateMeterFWVersionFromMdsId(String mdsId){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
            Meter meter = meterDao.get(mdsId);
            Modem modem = meter.getModem();
            log.debug("MeterID[" + meter.getMdsId() + "] ModemType[" + modem.getModemType().name() + 
    				"] diff[" + meter.getTimeDiff() + "]");

            CommandGW commandGw = DataUtil.getBean(CommandGW.class);                        
            
            if((modem.getModemType() == ModemType.MMIU) && (modem.getProtocolType() == Protocol.SMS)) {
            	// MBB
            	// for AutoTimeSync Task
       			String cmd = "cmdGetMeterFWVersion";
				String rtnString = "";
       			Map<String, String> paramMap = new HashMap<String, String>();
    			//paramSet	
    			paramMap.put("meterId", meter.getMdsId());

        		MMIU mmiuModem = mmiuDao.get(modem.getId());           				
   				List<String> paramListForSMS  = new ArrayList<String>();

   		        paramListForSMS.add(FMPProperty.getProperty("smpp.hes.fep.server", ""));
   		        paramListForSMS.add(FMPProperty.getProperty("soria.modem.tls.port", ""));
   		        paramListForSMS.add(FMPProperty.getProperty("smpp.auth.port", ""));
           		        
   		        String cmdMap = null;
   		        ObjectMapper om = new ObjectMapper();
   		        if(paramMap != null)
   		        	cmdMap = om.writeValueAsString(paramMap);
           		                  		    	
  					log.debug("sendSMS modem[" + modem.getDeviceSerial()+  "] cmd[" + cmd + "]");
   		    	rtnString = commandGw.sendSMS(
   		    			cmd, 
   		    			SMSConstants.MESSAGE_TYPE.REQ_NON_ACK.getTypeCode(), 
   		    			mmiuModem.getPhoneNumber(), 
   		    			modem.getDeviceSerial(),
   		    			SMSConstants.COMMAND_TYPE.NI.getTypeCode(), 
   		    			paramListForSMS, cmdMap);
   		    	if (rtnString.equals("F") || rtnString.equals("CF")) { // Fail
   					log.debug(rtnString);
   					return;
   				} else {
   				    int loopCount = 0;
   	                Integer lastStatus = null;
   	                while(loopCount < 9) {
   	   					log.debug("asyncCommandLogDao.getCmdStatus　rtnString[" + rtnString + "]");
//   	                    lastStatus = asyncCommandLogDao.getCmdStatus(modem.getDeviceSerial(), cmd);
   	                	lastStatus = getAsyncCommandLogStatus(modem.getDeviceSerial(), Long.parseLong(rtnString));
   	   					log.debug("CmdStatus [" + lastStatus + "]");
   	                    if ((lastStatus != null) && (TR_STATE.Success.getCode() == lastStatus)) {
   	                        break;
   	                    }
   	                    loopCount++;
   	                    Thread.sleep(10000);
   	                }
   					if (TR_STATE.Success.getCode() != lastStatus) {
   						log.debug("FAIL : Communication Error but Send SMS Success.  " + cmd);
   						return;
   					} else {
   						ObjectMapper mapper = new ObjectMapper();
   						List<AsyncCommandResult> asyncResult = resultDao.getCmdResults(modem.getDeviceSerial(), Long.parseLong(rtnString), cmd); //ASYNC_COMMAND_RESULT에서 결과 값을 가져옴
   						if (asyncResult == null || asyncResult.size() <= 0) {
   	   						log.debug("FAIL : asyncResult is none. ");
   							return;
   						} else { // Success
   							String resultStr = "";
   							for (int j = 0; j < asyncResult.size(); j++) {
   								resultStr += asyncResult.get(j).getResultValue();
   							}
   							Map<String, String> map = mapper.readValue(resultStr, new TypeReference<Map<String, String>>() {
   							});
   							
   							log.debug("SUCCESS: " + resultStr);
   						}
   					}
   				}
            }
            else if((modem.getModemType() == ModemType.MMIU) && (modem.getProtocolType() == Protocol.IP || modem.getProtocolType() == Protocol.GPRS)) {           		
    			resultMap = commandGw.cmdGetMeterFWVersion(mdsId);
            }
		}catch(Exception e){
			log.error(e);
		}
    }
	
    synchronized private List<Meter> createMeterList(List<Meter> list){
    	List<Meter> updateList  = new ArrayList<Meter>();
    	int count = 0;
    	
    	if(fileMap.size() > 0){
    		try{
    			for(int i = 0; i < list.size();i++){
        			String mdsIdStr = list.get(i).getMdsId();
        			if (fileMap.containsKey(mdsIdStr)) {
        				String statusStr = fileMap.get(mdsIdStr).get("Status").toString();
    		    		if(!statusStr.equals("1")){
    		    			log.debug("MDS_ID[" + mdsIdStr +"] is target.");
    		    			updateList.add(count, list.get(i));
    		    			count++;
    		    		}
    		    		else {
    		    			log.debug("MDS_ID[" + mdsIdStr +"] Already updated.");
    		    		}
        			} else {
    	    			log.debug("MDS_ID[" + mdsIdStr +"] is target. First time.");
    	    			updateList.add(count, list.get(i));
    	    			count++;		    				
        			}
        		}
    		}catch(Exception e){
    			log.error(e);
    		}
    	}
		else{
			return list;
		}
    	return updateList;
    }
    
    private Map<String,JSONObject> createFileMap() throws IOException{
		String fileName = "./result/" + _location + "_" +_msa + ".txt";
		File file = new File(fileName);
		log.debug("createFileMap() File[" + file.getAbsolutePath() + "]");
    	Map<String,JSONObject> items = new HashMap<String,JSONObject>();

		try{
			if(file.exists()){
				FileReader fr = new FileReader(fileName);
		    	BufferedReader br = new BufferedReader(fr);
		    	StringBuilder sb = new StringBuilder();
		    	String b;
		    	while((b = br.readLine()) != null){
		    		sb.append(b);
		    	}
		    	br.close();
		        JSONArray array = JSONArray.fromObject(sb.toString());
		        
		        if(array != null){
		        	for(int j = 0; j < array.size();j++){
						JSONObject jobj = array.getJSONObject(j);
						Iterator<String> iterator = jobj.keys();
						while(iterator.hasNext()){
							String mds_id = iterator.next();
							JSONObject item = jobj.getJSONObject(mds_id);
				   		 	items.put(mds_id, item);	
						}
			    	}	
		        }
			}
			else{
				file.createNewFile();
			}
        }catch(Exception e){
            log.error(e);
        }
		
    	return items;
    }
    
    synchronized private void updateResultFile(JSONArray list){
		try{
			log.debug("updateResultFile() list coount = " + list.size());
			String fileName = "./result/" + _location + "_" +_msa + ".txt";
	    	Map<String,JSONObject> fileMapCopy = new HashMap<String,JSONObject>();
	    	Map<String,JSONObject> exeMap = new HashMap<String,JSONObject>();
	    	Map<String,JSONObject> allMap = new HashMap<String,JSONObject>();
	    	
	    	for(int j = 0; j < list.size();j++){
				JSONObject jobj = list.getJSONObject(j);
				Iterator<String> iterator = jobj.keys();
				while(iterator.hasNext()){
					String mds_id = iterator.next();
					JSONObject item = jobj.getJSONObject(mds_id);
					exeMap.put(mds_id, item);
				}
	    	}
	    	if(fileMap.size() > 0){
		    	fileMapCopy.putAll(fileMap);
		    	for(Map.Entry<String,JSONObject> bar : fileMap.entrySet()){
		    		if(exeMap.containsKey(bar.getKey())){
		    			fileMapCopy.remove(bar.getKey());
		    		}
		    	}	
	    	}
	    	allMap.putAll(exeMap);
	    	if(fileMapCopy.size() > 0){
	    		allMap.putAll(fileMapCopy);
	    	}

	        OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(fileName));
	    	JSONArray jsonArray = JSONArray.fromObject( allMap );
	    	osw.write("[" + jsonArray.toArray()[0].toString() + "]");
	        osw.close();
	        fileMap.putAll(allMap);
	        
	        if (_testmode > 0) {
				String resultLog = "./result/" + _location + "_" +_msa + ".log";
		        OutputStreamWriter osw2 = new OutputStreamWriter(new FileOutputStream(resultLog, true));
		        Iterator<Map.Entry<String,JSONObject>> it = exeMap.entrySet().iterator();
		        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		        Date date = new Date();
				String dateStr = sdf.format(date).toString();
				osw2.write( "----------------------------------\n" );
		        osw2.write( dateStr + "\n");
		        osw2.write( "----------------------------------\n" );
		        while( it.hasNext() ){
			        Map.Entry<String, JSONObject> e = it.next();
			        osw2.write( e.getKey() + ":" + e.getValue() + "\n");
		        }
		        osw2.write( "----------------------------------\n" );
		        osw2.close();
	        }

        }catch(Exception e){
            log.error(e);
        }
	}
    
}
