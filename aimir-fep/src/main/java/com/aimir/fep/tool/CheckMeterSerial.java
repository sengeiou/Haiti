package com.aimir.fep.tool;

import java.io.BufferedReader;
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

import net.sf.json.JSONObject;

@Service
public class CheckMeterSerial {
    private static Log log = LogFactory.getLog(CheckMeterSerial.class);

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
    private int 		_retry = 5;
    private String		_location = "CheckMeterSerial";
    private String		_filepath = "TargetMeterList.txt";
	private List<Meter> _meterList = new ArrayList<Meter>();
	private List<McuDeviceList> _mcuList = new ArrayList<McuDeviceList>();
	private List<McuDeviceList> _exceptMcuList = new ArrayList<McuDeviceList>();
    private String		_obisparam;
	Map<String,JSONObject> fileMap = new HashMap<String,JSONObject>();

    public static void main(String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{"/config/spring-fep-schedule.xml"});
        DataUtil.setApplicationContext(ctx);
        CheckMeterSerial task = ctx.getBean(CheckMeterSerial.class);
        log.info("======================== CheckMeterSerial start. ========================");
        task.execute(args);
        log.info("======================== CheckMeterSerial end. ========================");
        System.exit(0);
    }

    public class McuDeviceList {
    	Integer mcuId;
    	List<String> mdsIdList;
    	// ++++
    	List<String> deviceIdList;
    	List<String> 	meterMdsIdList;

    	public boolean equals(Object obj){
    		McuDeviceList t = (McuDeviceList)obj;
    		if (this.mcuId.equals(t.mcuId)) return true;
    		else return false;
    	}
    	// ++++
    }


//    private List<McuDeviceList> getMcuListfromFile() {
    private void getListfromFile() {
    	/*
    	 * Output List<McuMeterList> is list of McuMeterList.
    	 * Output meters is list of meter which doesn't have a mcu.
    	 */
    	try{
    		List<String > mdsIDList = new ArrayList<String>();
    	    FileReader fr = null;
    	    BufferedReader br = null;

    	    log.debug("current dir" + new File(".").getAbsolutePath());

	        try {
		        fr = new FileReader(_filepath);

	    	    br = new BufferedReader(fr);

		        String line;
		        while ((line = br.readLine()) != null) {
		        	mdsIDList.add(line);
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

	    	log.debug("getMcuListfromFile(mdsIDList[" + mdsIDList.size() + "])");
	    	List<McuDeviceList> mcuList = new ArrayList<McuDeviceList>();

	    	int idx = 0;
	        for (idx=0; idx < mdsIDList.size(); idx++) {
				Meter meter = meterDao.get(mdsIDList.get(idx));
				if (meter == null) {
					log.debug("Meter[" + mdsIDList.get(idx) + "] . Meter is null .");
					continue;
				}
				Modem modem = meter.getModem();
				if (modem == null) {
					log.debug("Meter[" + mdsIDList.get(idx) + "] . Modem is null .");
					continue;
				}
				_meterList.add(meter);
				MCU mcu = modem.getMcu();

				if (mcu != null) {
					log.debug("MCU[" + mcu.getId() + "] . Meter[" + meter.getMdsId() + "] . Modem[" + modem.getDeviceSerial() + "] .");
					int i;
					for (i = 0; i < mcuList.size(); i++) {	// Search the same mcu.
						if ( mcuList.get(i).mcuId.compareTo(mcu.getId())==0 ) {
							break;
						}
					}
					if (i < mcuList.size()) {	// find!
						log.debug("MCU[" + mcu.getId() + "] has been already listed. Meter[" + meter.getMdsId() + "],Modem[" + modem.getDeviceSerial() + " is added.");
						mcuList.get(i).meterMdsIdList.add(mdsIDList.get(idx));
						mcuList.get(i).deviceIdList.add(modem.getDeviceSerial());
						continue;
					}
					McuDeviceList mml = new McuDeviceList();
					mml.mcuId = mcu.getId();
					mml.deviceIdList = new ArrayList<String>();
					mml.deviceIdList.add(modem.getDeviceSerial());
					mml.meterMdsIdList = new ArrayList<String>();
					mml.meterMdsIdList.add(mdsIDList.get(idx));
					_mcuList.add(mml);
				} else {
					log.debug("MCU is null. Meter[" + meter.getMdsId() + "] . Modem[" + modem.getDeviceSerial() + "] .");
					if(_exceptMcuList.size() == 0){
						McuDeviceList mml = new McuDeviceList();
						mml.mdsIdList = new ArrayList<String>();
						mml.mdsIdList.add(meter.getMdsId());
						_exceptMcuList.add(mml);
					}
					else{
						_exceptMcuList.get(0).mdsIdList.add(meter.getMdsId());
					}
				}
			}
//	        return mcuList;
	        return;
    	}
        catch (Exception e) {
        	log.debug(e.getMessage());
//        	return null;
        	return;
        }
    }

    public void execute(String[] args) {

		byte paramFlg = 0;

		_obisparam = FMPProperty.getProperty("soria.cmd.obis.param", "");

		log.info("ARG_0[" + args[0] + "]" );

		// Target File
		if (args[0].length() > 0) {
			_filepath = args[0];
			paramFlg = 0x01;
		}
		else {
			return;
		}
		// MAX_THREAD_WORKER
		if (args[1].length() > 0) {
			_maxThreadWorker = Integer.parseInt(args[1]);
		}
		// TIMEOUT
		if (args[2].length() > 0) {
			_timeout = Integer.parseInt(args[2]);
		}
		// RETRY
		if (args[3].length() > 0) {
			_retry = Integer.parseInt(args[3]);
		}

		log.info("Start CheckMeterSerial TargetFile[" + _filepath + "]");
		log.info("Start CheckMeterSerial maxThreadWorker[" + _maxThreadWorker + "]");

		if( (paramFlg & 0x01) == 0x01 ) {
			try {
				getListfromFile();
				if( (_mcuList.size() == 0) && (_exceptMcuList.size() == 0) ) {
					log.info("Target meter is none.");
					return;
				}

            	// in the case of dcu
            	int cnt = 0;

            	ExecutorService pool = Executors.newFixedThreadPool(_maxThreadWorker);
            	CheckMeterSerialThread threads[] = new CheckMeterSerialThread[_mcuList.size()];

        		int i = 0;
            	for (McuDeviceList mcuMeter : _mcuList) {
            		log.info(cnt++ + ": MCU[" + mcuMeter.mcuId + "] Check Meter Serial");

            		threads[i] = new CheckMeterSerialThread(mcuMeter);
            		pool.execute(threads[i]);
            		i++;
            	}

                log.info("ExecutorService for mcu shutdown.");
                pool.shutdown();
                log.info("ExecutorService for mcu awaitTermination. [" + _timeout + "]sec");
                pool.awaitTermination(_timeout, TimeUnit.SECONDS);

                // in the case of except dcu
                pool = Executors.newFixedThreadPool(_maxThreadWorker);
                CheckMmiuMeterSerialThread threads2[] = new CheckMmiuMeterSerialThread[_exceptMcuList.get(0).mdsIdList.size()];

        		i = 0;
            	for ( String mdsId : _exceptMcuList.get(0).mdsIdList) {
            		log.info(cnt++ + ": MMIU Meter [" + mdsId + "] Update Meter FW Version");

            		threads2[i] = new CheckMmiuMeterSerialThread(mdsId);
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
    }

    protected class CheckMeterSerialThread extends Thread {
    	McuDeviceList mcuDeviceList;
    	MCU mcu = null;
    	CommandGW commandGw = DataUtil.getBean(CommandGW.class);

    	public CheckMeterSerialThread(McuDeviceList list) {
    		try {
	    		this.mcuDeviceList = list;
	    		mcu = mcuDao.get(list.mcuId);
            }
            catch (Exception ee) {}
    	}
    	public void run() {
    		Map<String,Object> result1 = new HashMap<String,Object>();
    		Map<String,Object> result2 = new HashMap<String,Object>();

            if (mcu != null) {
				log.info("ThreadID[" + Thread.currentThread().getId() + "] CheckMeterSerialThread() thread start. MCU[" + mcuDeviceList.mcuId + "]");

				try {
					List<String> modemList = mcuDeviceList.deviceIdList;
					List<String> meterList = mcuDeviceList.meterMdsIdList;
					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");

					for (int i = 0; i < modemList.size(); i++) {
						String strRtnVal = "";
						String strMeterId  = meterList.get(i);
						String strModemId  = modemList.get(i);
						String strSerial1 = "failed";
						String strSerial2 = "failed";
						Integer iCmp = 0;
						String strDate    = "";
						Object obj = null;

						// E-meter serial number
						for( int j=0; j < _retry ; j++ ) {
							result1  = commandGw.cmdMeterParamGet(strModemId, "0.0.96.1.0.255|1|2|RO|octet-string|");
							obj = result1.get("RESULT_VALUE");
							if (obj instanceof String) {
								strRtnVal = result1.get("RESULT_VALUE").toString();
							}
							if( strRtnVal == "Success" ) {
								strSerial1 = result1.get("value").toString();
								log.debug("OBIS[0.0.96.1.0.255] Serial1[" + strSerial1 + "]");
								break;
							}
							else {
								log.debug("Retry[0.0.96.1.0.255] count[" + Integer.toString(j+1) + "/" + Integer.toString(_retry) + "]");
								sleep(1000);
							}
						}
						if( strRtnVal != "Success" ) {
							log.debug("Failed get result[0.0.96.1.0.255");
						}
						strRtnVal = "";

						sleep(500);
						// E-meter equipment identifier
						for( int j=0; j < _retry ; j++ ) {
							result2 = commandGw.cmdMeterParamGet(strModemId, "0.0.96.1.1.255|1|2|RO|octet-string|");
							obj = null;
							obj = result2.get("RESULT_VALUE");
							if (obj instanceof String) {
								strRtnVal = result2.get("RESULT_VALUE").toString();
							}
							if( strRtnVal == "Success" ) {
								strSerial2 = result2.get("value").toString();
								log.debug("OBIS[0.0.96.1.1.255] Serial2[" + strSerial2 + "]");
								break;
							}
							else {
								log.debug("Retry[0.0.96.1.1.255] count[" + Integer.toString(j+1) + "/" + Integer.toString(_retry) + "]");
								sleep(1000);
							}
						}
						if( strRtnVal != "Success" ) {
							log.debug("Failed get result[0.0.96.1.1.255");
						}

						log.debug("MeterId[" + strMeterId + "] Serial1[" + strSerial1 + "] Serial2[" + strSerial2 + "]");
						if( (strSerial1.compareTo("failed") != 0) && (strSerial2.compareTo("failed") != 0) ) {
							if( strMeterId.compareTo(strSerial1.toString()) != 0 ) {
								iCmp = 1;
							}
							if( strMeterId.compareTo(strSerial2.toString()) != 0 ) {
								iCmp += 2;
							}
						}
						else {
							iCmp = -1;
						}

						Date date = new Date();
						strDate = sdf.format(date).toString();
						updateResultFile(strMeterId,strModemId,strSerial1,strSerial2,iCmp.toString(),strDate);
					}
				} catch (Exception e){
					log.info("ThreadID[" + Thread.currentThread().getId() + "] CheckMeterSerialThread() thread end. MCU[" + mcu.getId() + "] is failed.");
				}
				log.info("ThreadID[" + Thread.currentThread().getId() + "] CheckMeterSerialThread() thread end. MCU[" + mcu.getId() + "]");
			}
			else {
				log.debug("MCU[" + mcu.getId() + "] is null.");
			}
		}
	}

    protected class CheckMmiuMeterSerialThread extends Thread {
    	String mdsId;
    	MCU mcu = null;
    	CommandGW commandGw = DataUtil.getBean(CommandGW.class);

    	public CheckMmiuMeterSerialThread(String mdsId) {
    		try {
    			this.mdsId = mdsId;
           }
            catch (Exception e) {}
		}
		public void run() {
			try {
				String strModemId = "";
				String strSerial1 = "failed";
				String strSerial2 = "failed";
				Integer iCmp = 0;
				String strDate = "";
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
				Meter meter = meterDao.get(mdsId);
				Modem modem = meter.getModem();
				strModemId = modem.getDeviceSerial();
				log.debug("CheckMmiuMeterSerialThread() MeterID[" + meter.getMdsId() + "] ModemType[" + modem.getModemType().name() +
						"] diff[" + meter.getTimeDiff() + "]");

				CommandGW commandGw = DataUtil.getBean(CommandGW.class);

				if((modem.getModemType() == ModemType.MMIU) && (modem.getProtocolType() == Protocol.SMS)) {
					// MBB
					// for AutoTimeSync Task
					String cmd = "cmdMeterParamGet";
					String rtnString1 = "";
					String rtnString2 = "";
					Map<String, String> paramMap1 = new HashMap<String, String>();
					Map<String, String> paramMap2 = new HashMap<String, String>();

					//paramSet
					paramMap1.put("meterId", meter.getMdsId());
					paramMap1.put("obis", "0.0.96.1.0.255|1|2|RO|octet-string|");
					paramMap2.put("meterId", meter.getMdsId());
					paramMap2.put("obis", "0.0.96.1.1.255|1|2|RO|octet-string|");

					MMIU mmiuModem = mmiuDao.get(modem.getId());
					List<String> paramListForSMS  = new ArrayList<String>();

					paramListForSMS.add(FMPProperty.getProperty("smpp.hes.fep.server", ""));
					paramListForSMS.add(FMPProperty.getProperty("soria.modem.tls.port", ""));
					paramListForSMS.add(FMPProperty.getProperty("smpp.auth.port", ""));

					String cmdMap1 = null;
					ObjectMapper om1 = new ObjectMapper();
					if(paramMap1 != null)
						cmdMap1 = om1.writeValueAsString(paramMap1);

					String cmdMap2 = null;
					ObjectMapper om2 = new ObjectMapper();
					if(paramMap2 != null)
						cmdMap2 = om2.writeValueAsString(paramMap2);

					for( int rty=0; rty < _retry ; rty++ ) {
						rtnString1 = commandGw.sendSMS(
								cmd,
								SMSConstants.MESSAGE_TYPE.REQ_NON_ACK.getTypeCode(),
								mmiuModem.getPhoneNumber(),
								modem.getDeviceSerial(),
								SMSConstants.COMMAND_TYPE.NI.getTypeCode(),
								paramListForSMS, cmdMap1);

						if (rtnString1.equals("F") || rtnString1.equals("CF")) {
							// Fail
							log.debug(rtnString1);
							log.debug("Retry[0.0.96.1.0.255] count[" + Integer.toString(rty+1) + "/" + Integer.toString(_retry) + "]");
							sleep(1000);
							continue;
						}

						int loopCount = 0;
						Integer lastStatus = null;
						while(loopCount < 9) {
							lastStatus = getAsyncCommandLogStatus(modem.getDeviceSerial(), Long.parseLong(rtnString1));
							log.debug("CmdStatus [" + lastStatus + "]");
							if ((lastStatus != null) && (TR_STATE.Success.getCode() == lastStatus)) {
								break;
							}
							loopCount++;
							Thread.sleep(10000);
						}
						if (TR_STATE.Success.getCode() != lastStatus) {
							log.debug("FAIL : Communication Error but Send SMS Success.  " + cmd);
							log.debug("Retry[0.0.96.1.0.255] count[" + Integer.toString(rty+1) + "/" + Integer.toString(_retry) + "]");
							sleep(1000);
							continue;
						}

						ObjectMapper mapper = new ObjectMapper();
						//ASYNC_COMMAND_RESULT
						List<AsyncCommandResult> asyncResult = resultDao.getCmdResults(
								modem.getDeviceSerial(),
								Long.parseLong(rtnString1),
								cmd);
						//Date date = new Date();
						//strDate = sdf.format(date).toString();
						if (asyncResult == null || asyncResult.size() <= 0) {
							// Fail
							log.debug("FAIL : Send SMS but fail to execute " + cmd);
							log.debug("Retry[0.0.96.1.0.255] count[" + Integer.toString(rty+1) + "/" + Integer.toString(_retry) + "]");
							sleep(1000);
							continue;
						}

						String resultStr = "";
						String strRtnVal = "";
						for (int j = 0; j < asyncResult.size(); j++) {
							resultStr += asyncResult.get(j).getResultValue();
						}
						log.debug("Async result string[" + resultStr + "]");
						Map<String, String> map = mapper.readValue(resultStr, new TypeReference<Map<String, String>>() {});
						strRtnVal = map.get("RESULT_VALUE");
						if( strRtnVal == "Success" ) {
							strSerial1 = map.get("value");
							log.debug("Success get result");
							break;
						}
						else {
							log.debug("Failed get result");
							log.debug("Retry[0.0.96.1.0.255] count[" + Integer.toString(rty+1) + "/" + Integer.toString(_retry) + "]");
							sleep(1000);
							continue;
						}
					}

					sleep(1000);
					for( int rty=0; rty < _retry ; rty++ ) {
						rtnString2 = commandGw.sendSMS(
								cmd,
								SMSConstants.MESSAGE_TYPE.REQ_NON_ACK.getTypeCode(),
								mmiuModem.getPhoneNumber(),
								modem.getDeviceSerial(),
								SMSConstants.COMMAND_TYPE.NI.getTypeCode(),
								paramListForSMS, cmdMap2);

						if (rtnString2.equals("F") || rtnString1.equals("CF")) {
							// Fail
							log.debug(rtnString2);
							log.debug("Retry[0.0.96.1.1.255] count[" + Integer.toString(rty+1) + "/" + Integer.toString(_retry) + "]");
							sleep(1000);
							continue;
						}

						int loopCount = 0;
						Integer lastStatus = null;
						while(loopCount < 9) {
							lastStatus = getAsyncCommandLogStatus(modem.getDeviceSerial(), Long.parseLong(rtnString2));
							log.debug("CmdStatus [" + lastStatus + "]");
							if ((lastStatus != null) && (TR_STATE.Success.getCode() == lastStatus)) {
								break;
							}
							loopCount++;
							Thread.sleep(10000);
						}
						if (TR_STATE.Success.getCode() != lastStatus) {
							log.debug("FAIL : Communication Error but Send SMS Success.  " + cmd);
							log.debug("Retry[0.0.96.1.1.255] count[" + Integer.toString(rty+1) + "/" + Integer.toString(_retry) + "]");
							sleep(1000);
							continue;
						}

						ObjectMapper mapper = new ObjectMapper();
						//ASYNC_COMMAND_RESULT
						List<AsyncCommandResult> asyncResult = resultDao.getCmdResults(
								modem.getDeviceSerial(),
								Long.parseLong(rtnString2),
								cmd);
						//Date date = new Date();
						//strDate = sdf.format(date).toString();
						if (asyncResult == null || asyncResult.size() <= 0) {
							// Fail
							log.debug("FAIL : Send SMS but fail to execute " + cmd);
							log.debug("Retry[0.0.96.1.1.255] count[" + Integer.toString(rty+1) + "/" + Integer.toString(_retry) + "]");
							sleep(1000);
							continue;
						}

						String resultStr = "";
						String strRtnVal = "";
						for (int j = 0; j < asyncResult.size(); j++) {
							resultStr += asyncResult.get(j).getResultValue();
						}
						log.debug("Async result string[" + resultStr + "]");
						Map<String, String> map = mapper.readValue(resultStr, new TypeReference<Map<String, String>>() {});
						strRtnVal = map.get("RESULT_VALUE");
						if( strRtnVal == "Success" ) {
							strSerial2 = map.get("value");
							log.debug("Success get result");
							break;
						}
						else {
							log.debug("Failed get result");
							log.debug("Retry[0.0.96.1.1.255] count[" + Integer.toString(rty+1) + "/" + Integer.toString(_retry) + "]");
							sleep(1000);
							continue;
						}
					}

					log.debug("MeterId[" + mdsId + "] Serial1[" + strSerial1 + "] Serial2[" + strSerial2 + "]");
					if( (strSerial1.compareTo("failed") != 0) && (strSerial2.compareTo("failed") != 0) ) {
						if( mdsId.compareTo(strSerial1.toString()) != 0 ) {
							iCmp = 1;
						}
						if( mdsId.compareTo(strSerial2.toString()) != 0 ) {
							iCmp += 2;
						}
					}
					else {
						iCmp = -1;
					}

					Date date = new Date();
					strDate = sdf.format(date).toString();
					updateResultFile(mdsId,strModemId,strSerial1,strSerial2,iCmp.toString(),strDate);
				}
				else if((modem.getModemType() == ModemType.MMIU) && (modem.getProtocolType() == Protocol.IP || modem.getProtocolType() == Protocol.GPRS)) {
					Map<String, Object> resultMap1 = new HashMap<String, Object>();
					Map<String, Object> resultMap2 = new HashMap<String, Object>();
					String strRtnVal = "";
					strModemId = modem.getDeviceSerial();

					for( int rty=0; rty < _retry ; rty++ ) {
						resultMap1 = commandGw.cmdMeterParamGet(mdsId,"0.0.96.1.0.255|1|2|RO|octet-string|");
						strRtnVal = String.valueOf(resultMap1.get("RESULT_VALUE"));
						if( strRtnVal.equals("Success")) {
							strSerial1 = String.valueOf(resultMap1.get("value"));
							log.debug("Success get result");
							break;
						}
						else {
							log.debug("Retry[0.0.96.1.0.255] count[" + Integer.toString(rty+1) + "/" + Integer.toString(_retry) + "]");
							sleep(1000);
						}
					}
					if( strRtnVal != "Success" ) {
						log.debug("Failed get result[0.0.96.1.0.255");
					}
					strRtnVal = "";

					sleep(1000);
					for( int rty=0; rty < _retry ; rty++ ) {
						resultMap2 = commandGw.cmdMeterParamGet(mdsId,"0.0.96.1.1.255|1|2|RO|octet-string|");
						strRtnVal = String.valueOf(resultMap2.get("RESULT_VALUE"));
						if( strRtnVal == "Success" ) {
							strSerial2 = String.valueOf(resultMap2.get("value"));
							log.debug("Success get result");
						}
						else {
							log.debug("Retry[0.0.96.1.1.255] count[" + Integer.toString(rty+1) + "/" + Integer.toString(_retry) + "]");
							sleep(1000);
						}
					}
					if( strRtnVal != "Success" ) {
						log.debug("Failed get result[0.0.96.1.1.255");
					}

					log.debug("MeterId[" + mdsId + "] Serial1[" + strSerial1 + "] Serial2[" + strSerial2 + "]");
					if( (strSerial1.compareTo("failed") != 0) && (strSerial2.compareTo("failed") != 0) ) {
						if( mdsId.compareTo(strSerial1.toString()) != 0 ) {
							iCmp = 1;
						}
						if( mdsId.compareTo(strSerial2.toString()) != 0 ) {
							iCmp += 2;
						}
					}
					else {
						iCmp = -1;
					}

					Date date = new Date();
					strDate = sdf.format(date).toString();
					updateResultFile(mdsId,strModemId,strSerial1,strSerial2,iCmp.toString(),strDate);
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

			return lastStatus;
        }
        catch (Exception e) {
        	log.debug(e,e);
    		if (txstatus != null&& !txstatus.isCompleted())
    			txmanager.rollback(txstatus);
        }
		return null;
    }

    synchronized private void updateResultFile(String strMeterId,String strModemId,String strSerial1,String strSerial2,String strCmp,String strDate){
		try{
			String resultLog = "./result/" + _location + ".csv";
	        OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(resultLog, true));
	        osw.write( strMeterId + "," + strModemId + "," + strSerial1 + "," + strSerial2 + "," + strCmp + "," + strDate + "\n");
	        osw.close();

        }catch(Exception e){
            log.error(e);
        }
	}

}
