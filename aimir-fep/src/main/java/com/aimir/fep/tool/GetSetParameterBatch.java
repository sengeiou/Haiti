/**
 * 
 */
package com.aimir.fep.tool;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.springframework.stereotype.Component;
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

@Component
public class GetSetParameterBatch {
    private static Log log = LogFactory.getLog(GetSetParameterBatch.class);

    @Autowired
    private MeterDao meterDao;	

    @Autowired
    private MCUDao mcuDao;	

	@Autowired
	private MMIUDao mmiuDao;

	@Autowired
	private AsyncCommandLogDao asyncCommandLogDao;
	
	@Autowired
	private AsyncCommandResultDao resultDao;
	
    @Resource(name="transactionManager")
    JpaTransactionManager txmanager;		
	
    final int MSG_TIMEOUT = 30;
    final int TUNNEL_TIMEOUT = 0;	

    String _mdsId;
    String _type;
    // obisCode+"|"+classId+"|"+attributeNo+"|"+accessRight+"|"+dataType+"|"+value
    String _params;
    String _file;
    private int  		_maxThreadWorker = 10;
    private int 		_timeout = 3600;
    private String		_execTime = "";
    
    public class McuDeviceList {
    	Integer mcuId;
    	List<String> mdsIdList;
    }    
    
    public static void main(String[] args) {
		String dev =  "";
        if (args[6].length() > 0) {
        	dev = args[6];
        }
        
		if (dev.equals("DEV")) {
			ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[] { "/config/spring-fep-schedule-dev.xml" });
	        DataUtil.setApplicationContext(ctx);
	        GetSetParameterBatch task = ctx.getBean(GetSetParameterBatch.class);
	        log.info("======================== GetSetParameterBatch start. ========================");
	        task.execute(args);
	        log.info("======================== GetSetParameterBatch end. ========================");
		} else {
	        ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{"/config/spring-fep-schedule.xml"}); 
	        DataUtil.setApplicationContext(ctx);
	        GetSetParameterBatch task = ctx.getBean(GetSetParameterBatch.class);
	        log.info("======================== GetSetParameterBatch start. ========================");
	        task.execute(args);
	        log.info("======================== GetSetParameterBatch end. ========================");
		}        
		    	
        System.exit(0);
    }
    
    public void execute(String[] args) {
		log.info("ARG_0[" + args[0] + "] ARG_1[" + args[1] + "] ARG_2[" + args[2] + "] ARG_3[" + args[3] + 
				"] ARG_4[" + args[4] +"] ARG_5[" + args[5] + "]");

		_mdsId = "";
		_type = "";
		_params = "";
		_file = "";

        if (args[0].length() > 0) { _mdsId = args[0]; }		
        if (args[1].length() > 0) { _type = args[1]; }		
        if (args[2].length() > 0) { _params = args[2]; }		
        if (args[3].length() > 0) { _file = args[3]; }		
        // MAX_THREAD_WORKER
        if (args[4].length() > 0) {
            _maxThreadWorker = Integer.parseInt(args[4]);        	
        }
        // TIMEOUT
        if (args[5].length() > 0) {
            _timeout = Integer.parseInt(args[5]);        	
        }
        
        if (_mdsId.length() == 0 && _file.length()==0) {
        	log.error("Invalid parameters.");
        	return;
        }        
        
        if (_mdsId.length() > 0) {
	        Meter meter = meterDao.get(_mdsId);
	        Modem modem = meter.getModem();
	        String modemType = modem.getModemType().name();
	        log.debug("Modem Type = " + modemType);
	 
			if (_type.equals("get")) {
		        cmdGetParameter(_mdsId, _params);
			}
			else if (_type.equals("set")) {
		        cmdSetParameter(_mdsId, _params);
			}
			else if (_type.equals("act")) {
		        cmdActParameter(_mdsId, _params);
			} 
			else {
		        log.debug("type = [" + _type + "] is invalid.");			
			}
        } else if (_file.length() > 0) {
        	
            try {
    	        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    	        Date date = new Date();
            	_execTime=sdf.format(date).toString();            	
            	
            	List<McuDeviceList> mculist = new ArrayList<McuDeviceList>();    	
            	List<McuDeviceList> exceptMculist = new ArrayList<McuDeviceList>();

            	List<Meter> list = getMeterListfromFile(_file);     	
            	if(!list.isEmpty()){
            		List<List<McuDeviceList>> resultList = new ArrayList<List<McuDeviceList>>();
            		resultList = getMcuList(list);
            		mculist = resultList.get(0);
            		exceptMculist = resultList.get(1);
            	} else {	log.info("Target meter is none.");
    				return;
    			}

    			log.info("Total MCU to need set ["+ mculist.size() + "]");
   			
            	// Execute Command
	            ExecutorService pool = Executors.newFixedThreadPool(_maxThreadWorker);
	    		if (mculist.size() > 0) {
	                GetSetParameterThread threads[] = new GetSetParameterThread[mculist.size()];
	            	int	i = 0;
	        		int cnt = 0;
	            
	            	for (McuDeviceList mcu : mculist) {
	            		log.info(cnt++ + ": MCU[" + mcu.mcuId + "] Set ");
	
	            		threads[i] = new GetSetParameterThread(mcu,_type, _params);
	            		pool.execute(threads[i]);
	            		i++;
	            	}
	
	                log.info("ExecutorService for mcu shutdown.");
	                pool.shutdown();
	                log.info("ExecutorService for mcu awaitTermination. [" + _timeout + "]sec");
	                pool.awaitTermination(_timeout, TimeUnit.SECONDS);
    			}
    			
                // in the case of except dcu
                if (exceptMculist.size() > 0) {
	                pool = Executors.newFixedThreadPool(_maxThreadWorker);
	                GetSetParameterMmiuThread threads2[] = new GetSetParameterMmiuThread[exceptMculist.get(0).mdsIdList.size()];
	        		int i = 0;
	        		int cnt = 0;
	        		
	            	for ( String mdsId : exceptMculist.get(0).mdsIdList) {
	            		log.info(cnt++ + ": MMIU Meter [" + mdsId + "] ");
	
	            		threads2[i] = new GetSetParameterMmiuThread(mdsId,_type, _params);
	            		pool.execute(threads2[i]);
	            		Thread.sleep(1000);
	            		i++;
	            	}
	                log.info("ExecutorService for mcu shutdown.");
	                pool.shutdown();
	                log.info("ExecutorService for mcu awaitTermination. [" + _timeout + "]sec");
	                pool.awaitTermination(_timeout, TimeUnit.SECONDS);                
                }
                
            }
            catch (Exception e) {
            	log.debug(e, e);
           }
            finally {
            }        	
        }
    }

    private List<Meter> getMeterListfromFile(String filePath) {
    	try{
    		List<String > inputList = new ArrayList<String>();
    	    FileReader fr = null;
    	    BufferedReader br = null;
    	    
    	    log.debug("current dir" + new File(".").getAbsolutePath());
    	    
	        try {
		        fr = new FileReader(filePath);		        
	    	    br = new BufferedReader(fr);
	    	    
		        String line;
		        while ((line = br.readLine()) != null) {
		        	if (line.length() > 0) {
		        		inputList.add(line);
		        	}
		        }    		    		
   
	        } catch (Exception e) {
	        	log.debug(e.getMessage());
	        } finally {
	            try {
	                br.close();
	                fr.close();
	            } catch (Exception e) {
	            	log.debug(e.getMessage());
	            }
	        }	        
	        
	    	log.debug("getMeterListfromFile(Meter Serial List[" + inputList.size() + "])");
	    	List<Meter> resultList = new ArrayList<Meter>();

	    	int idx = 0;
	        for (idx=0; idx < inputList.size(); idx++) {
	        	Meter meter = meterDao.get(inputList.get(idx));
				if (meter == null) {
					log.debug("Meter[" + inputList.get(idx) + "] . Meter is null .");
	        		continue;
	        	}
				resultList.add(meter);
	        }
	        return resultList;
    	}
        catch (Exception e) {
        	log.debug(e, e);
        	return null;
        }   	
    }       
    
    private List<List<McuDeviceList>> getMcuList(List<Meter> meters) {
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

    protected class GetSetParameterThread extends Thread {
    	McuDeviceList mcuDeviceList;
    	MCU mcu = null;
    	String type;
    	String params;

    	public GetSetParameterThread(McuDeviceList list, String argType, String argParams) {
    		try {
	    		this.mcuDeviceList = list;
	    		mcu = mcuDao.get(list.mcuId);
	    		type = argType;
	    		params = argParams;
            }
            catch (Exception ee) {}
    	}
    	public void run() {
            if (mcu != null) {
            	log.info("ThreadID[" + Thread.currentThread().getId() + "] GetSetParameterThread() thread start. MCU[" + mcuDeviceList.mcuId + "]");

            	try {
            		List<String> mdsList = mcuDeviceList.mdsIdList;
            		for (int i = 0; i < mdsList.size(); i++) {
            			if (type.equals("get")) {
            		        cmdGetParameter(mdsList.get(i), params);
            			}
            			else if (type.equals("set")) {
            		        cmdSetParameter(mdsList.get(i), params);
            			}
            			else if (type.equals("act")) {
            		        cmdActParameter(mdsList.get(i), params);
            			} 
            			else {
            		        log.debug("type = [" + type + "] is invalid.");
            		        break;
            			}            		
            		}
            	} catch (Exception e){
            		log.info("ThreadID[" + Thread.currentThread().getId() + "] GetSetParameterThread() thread end. MCU[" + mcu.getId() + "] is failed.");
            	}	
            	log.info("ThreadID[" + Thread.currentThread().getId() + "] GetSetParameterThread() thread end. MCU[" + mcu.getId() + "]");            	
            }
            else {
            	log.debug("MCU[" + mcu.getId() + "] is null.");
            }
    	}
    }
    
    protected class GetSetParameterMmiuThread extends Thread {
    	String mdsId;
    	MCU mcu = null;
    	String type;
    	String params;

    	public GetSetParameterMmiuThread(String mdsId, String argType, String argParams) {
    		try {
    			this.mdsId = mdsId;
	    		type = argType;
	    		params = argParams;
	    	}
            catch (Exception e) {}
		}
    	public void run() {
            try {
                Meter meter = meterDao.get(mdsId);
                Modem modem = meter.getModem();
                log.debug("GetSetParameter() MeterID[" + meter.getMdsId() + "] ModemType[" + modem.getModemType().name() + "]");

                CommandGW commandGw = DataUtil.getBean(CommandGW.class);                        
                
                if((modem.getModemType() == ModemType.MMIU) && (modem.getProtocolType() == Protocol.SMS)) {
                	// MBB
           			String cmd = "";
    				String rtnString = "";
           			Map<String, String> paramMap = new HashMap<String, String>();
        			if (type.equals("get")) {
        				cmd = "cmdMeterParamGet";
            			paramMap.put("paramGet", params);
        			}
        			else if (type.equals("set")) {
        				cmd = "cmdMeterParamSet";
            			paramMap.put("paramSet", params);
        			}
        			else if (type.equals("act")) {
        				cmd = "cmdMeterParamAct";
            			paramMap.put("paramAct", params);
        			} 
        			else {
        		        log.debug("type = [" + type + "] is invalid.");
        		        return;
        			}         			
        			
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
       					makeResultFile(mdsId, rtnString);
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
       						makeResultFile(mdsId, "FAIL : Communication Error but Send SMS Success.  ");
       						return;
       					} else {
       						ObjectMapper mapper = new ObjectMapper();
       						List<AsyncCommandResult> asyncResult = resultDao.getCmdResults(modem.getDeviceSerial(), Long.parseLong(rtnString), cmd); //ASYNC_COMMAND_RESULT에서 결과 값을 가져옴
       						if (asyncResult == null || asyncResult.size() <= 0) {
       							log.debug("FAIL : Send SMS but fail to execute " + cmd);
           						makeResultFile(mdsId, "FAIL : Send SMS but fail to execute ");
           						// Fail
       						} else { // Success
       							String resultStr = "";
       							for (int j = 0; j < asyncResult.size(); j++) {
       								resultStr += asyncResult.get(j).getResultValue();
       							}
       							log.debug("Async result string[" + resultStr + "]");
       							Map<String, String> map = mapper.readValue(resultStr, new TypeReference<Map<String, String>>() {
       							});
       							log.debug("Success get result");
       							
       							String rtnStr = "";
       							for(Map.Entry<String, String> entry : map.entrySet()){
       								rtnStr += "[" + entry.getKey() + ":" + entry.getValue() + "] ";
       							}			
       							makeResultFile(mdsId, rtnStr);       						}
       					}
       				}
                }
                else if((modem.getModemType() == ModemType.MMIU) && (modem.getProtocolType() == Protocol.IP || modem.getProtocolType() == Protocol.GPRS)) {  
        			if (type.equals("get")) {
        		        cmdGetParameter(mdsId, params);
        			}
        			else if (type.equals("set")) {
        		        cmdSetParameter(mdsId, params);
        			}
        			else if (type.equals("act")) {
        		        cmdActParameter(mdsId, params);
        			} 
        			else {
        		        log.debug("type = [" + type + "] is invalid.");
        		        return;
        			}            		
                }
            }
            catch (Exception e) {
            }
    	}
    }

    synchronized private Integer getAsyncCommandLogStatus(String deviceId, long trId) throws Exception {
    	TransactionStatus txstatus = null;
    	List<AsyncCommandLog> list = new ArrayList<AsyncCommandLog>();
    	
        try {
        	txstatus = txmanager.getTransaction(null);
        	
        	log.debug("getAsyncCommandLogStatus(" + deviceId + "," + trId + ")");

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
    
    private void cmdGetParameter(String mdsId, String params) {
		Meter meter = meterDao.get(mdsId);
		Modem modem = meter.getModem();
				
		CommandGW commandGw = DataUtil.getBean(CommandGW.class);		

		String rtnStr="";
		try {
			Map<String, Object> resultTable = new HashMap<String, Object>();
			resultTable = commandGw.cmdMeterParamGet(modem.getDeviceSerial(), params);
			for(Map.Entry<String, Object> entry : resultTable.entrySet()){
				rtnStr += "[" + entry.getKey() + ":" + (String)entry.getValue() + "] ";
			}			
			makeResultFile(mdsId, rtnStr);
		}
		catch(Exception e)
		{
			log.debug("Data Connect Error["+e.getMessage()+"]");
			return;
		}

		log.debug("=================================");
		log.debug(rtnStr);
		log.debug("=================================");    	
    }

    private void cmdSetParameter(String mdsId, String params) {
		Meter meter = meterDao.get(mdsId);
		Modem modem = meter.getModem();
				
		CommandGW commandGw = DataUtil.getBean(CommandGW.class);		

		String rtnStr="";
		try {
			Map<String, Object> resultTable = new HashMap<String, Object>();
			resultTable = commandGw.cmdMeterParamSet(modem.getDeviceSerial(), params);
			for(Map.Entry<String, Object> entry : resultTable.entrySet()){
				rtnStr += "[" + entry.getKey() + ":" + (String)entry.getValue() + "] ";
			}			
			makeResultFile(mdsId, rtnStr);
		}
		catch(Exception e)
		{
			log.debug("Data Connect Error["+e.getMessage()+"]");
			return;
		}

		log.debug("=================================");
		log.debug(rtnStr);
		log.debug("=================================");    	
    }
    
    private void cmdActParameter(String mdsId, String params) {
		Meter meter = meterDao.get(mdsId);
		Modem modem = meter.getModem();
				
		CommandGW commandGw = DataUtil.getBean(CommandGW.class);		

		String rtnStr="";
		try {
			Map<String, Object> resultTable = new HashMap<String, Object>();
			resultTable = commandGw.cmdMeterParamAct(modem.getDeviceSerial(), params);
			for(Map.Entry<String, Object> entry : resultTable.entrySet()){
				rtnStr += "[" + entry.getKey() + ":" + (String)entry.getValue() + "] ";
			}			
			makeResultFile(mdsId, rtnStr);
		}
		catch(Exception e)
		{
			log.debug("Data Connect Error["+e.getMessage()+"]");
			return;
		}

		log.debug("=================================");
		log.debug(rtnStr);
		log.debug("=================================");    	
    }

	synchronized private void makeResultFile(String mdsId, String result){
		String fileName1 = getPreffix(_file) + "_" + _execTime + "_result.csv";
		
        try{
        	File file = new File(fileName1) ;
        	boolean init = false;
        	if (file.createNewFile()) {
        		// not exists. create new.
        		init = true;
        	}
        	FileOutputStream fos = new FileOutputStream(fileName1, true);
        	OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
        	BufferedWriter bw = new BufferedWriter(osw);
        	        
        	if (init) {
            	StringBuffer buffer = new StringBuffer();
            	buffer.append("==========================" + "\n");
            	buffer.append(_type + "\n");
            	buffer.append(_params + "\n");
            	buffer.append("==========================");
                bw.write(buffer.toString());
            	bw.newLine();        	
        	}
        	
        	// Data
    		StringBuffer dataBuffer = new StringBuffer();

    		dataBuffer.append("\"" + mdsId + "\"" + ",");
    		dataBuffer.append("\"" + result + "\"" );
    		
            bw.write(dataBuffer.toString());
        	bw.newLine();        	
        	bw.close();
        }catch(Exception e){
            log.error("makeResultFile error - " + e, e);
        }
	}    

	public static String getPreffix(String fileName) {
	    if (fileName == null)
	        return null;
	    int point = fileName.lastIndexOf(".");
	    if (point != -1) {
	        return fileName.substring(0, point);
	    } 
	    return fileName;
	}	    

}