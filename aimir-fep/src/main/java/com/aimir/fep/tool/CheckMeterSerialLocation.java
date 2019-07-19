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

import org.apache.commons.beanutils.BeanUtils;
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
import com.aimir.dao.device.MeterAttrDao;
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
import com.aimir.model.device.MeterAttr;
import com.aimir.model.device.Modem;
import com.aimir.model.system.Code;
import com.aimir.model.system.Location;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service
public class CheckMeterSerialLocation {
	private static Log log = LogFactory.getLog(CheckMeterSerialLocation.class);

	@Autowired
	MCUDao mcuDao;

	@Autowired
	ModemDao modemDao;

	@Autowired
	MeterDao meterDao;

	@Autowired
	MeterAttrDao meterAttrDao;

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

	private int	  _maxThreadWorkerMcu  = 10;
	private int	  _maxThreadWorkerMmiu = 5;
	private int	  _timeoutMcu		  = 3600;
	private int	  _timeoutMmiu		 = 3600;
    private int     _retry = 5;
	private String   _location			= "";
	private String   _msa				 = "";
	private String   _mds_id			  = "";
	private int	  _testmode			= 0;
	Map<String,JSONObject> fileMap = new HashMap<String,JSONObject>();

	public static void main(String[] args) {
		ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{"/config/spring-fep-schedule.xml"});
		DataUtil.setApplicationContext(ctx);
		CheckMeterSerialLocation task = ctx.getBean(CheckMeterSerialLocation.class);
		log.info("======================== CheckMeterSerialLocation start. ========================");
		task.execute(args);
		log.info("======================== CheckMeterSerialLocation end. ========================");
		System.exit(0);
	}

	public class McuDeviceList {
		Integer mcuId;
		List<String> mdsIdList;
		// ++++
		List<String> deviceIdList;
		List<String> meterMdsIdList;

		public boolean equals(Object obj){
			McuDeviceList t = (McuDeviceList)obj;
			if (this.mcuId.equals(t.mcuId)) return true;
			else return false;
		}
		// ++++
	}

	public class ResultJsonType {
		String MeterId;
		FwData FwDatas;
		public ResultJsonType(){
		}
		public ResultJsonType(String mtr, String mac, String d1, String d2, String stat, String dt, String m_id) {
			this.MeterId = mtr;
			this.FwDatas = new FwData();
			this.FwDatas.MacAddress = mac;
			this.FwDatas.D1 = d1;
			this.FwDatas.D2 = d2;
			this.FwDatas.Status = stat;
			this.FwDatas.DateTime = dt;
			this.FwDatas.Meter_id = m_id;
		}
	}

	public class FwData {
		String MacAddress;
		String D1;
		String D2;
		String Status;
		String DateTime;
		String Meter_id;
	}

	public void execute(String[] args) {

		byte paramFlg = 0;

		log.info(
				  "ARG_0[" + args[0] + "] ARG_1[" + args[1] +
				"] ARG_2[" + args[2] + "] ARG_3[" + args[3] +
				"] ARG_4[" + args[4] + "] ARG_5[" + args[5] +
				"] ARG_6[" + args[6] + "] ARG_7[" + args[7] + "]");

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

		// MAX_THREAD_WORKER(MCU)
		if (args[3].length() > 0) {
			_maxThreadWorkerMcu = Integer.parseInt(args[3]);
		}

		// MAX_THREAD_WORKER(MMIU)
		if (args[4].length() > 0) {
			_maxThreadWorkerMmiu = Integer.parseInt(args[4]);
		}

		// TIMEOUT(MCU)
		if (args[5].length() > 0) {
			_timeoutMcu = Integer.parseInt(args[5]);
		}

		// TIMEOUT(MMIU)
		if (args[6].length() > 0) {
			_timeoutMmiu = Integer.parseInt(args[6]);
		}

		if (args[7].length() > 0) {
			_retry = Integer.parseInt(args[7]);
		}

		if (args[8].length() > 0) {
			_testmode = Integer.parseInt(args[8]);
		}

		log.info("Start CheckMeterSerialLocation maxThreadWorker MCU[" +
				 _maxThreadWorkerMcu + "] MMIU[" + _maxThreadWorkerMmiu + "]");

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

				log.info("CheckMeterSerialLocation MCU[" + String.valueOf(mculist.size()) + "]");
				ExecutorService pool = Executors.newFixedThreadPool(_maxThreadWorkerMcu);
				CheckMeterSerialLocationThread threads[] = new CheckMeterSerialLocationThread[mculist.size()];

				int i = 0;
				for (McuDeviceList mcuMeter : mculist) {
					log.info(cnt++ + ": MCU[" + mcuMeter.mcuId + "] Check Meter Serial(D1,D2)");

					threads[i] = new CheckMeterSerialLocationThread(mcuMeter);
					pool.execute(threads[i]);
					i++;
				}

				log.info("ExecutorService for mcu shutdown.");
				pool.shutdown();
				log.info("ExecutorService for mcu awaitTermination. [" + _timeoutMcu + "]sec");
				pool.awaitTermination(_timeoutMcu, TimeUnit.SECONDS);

				// in the case of except dcu
				log.info("CheckMeterSerialLocation MMIU[" + String.valueOf(exceptMculist.get(0).mdsIdList.size()) + "]");
				pool = Executors.newFixedThreadPool(_maxThreadWorkerMmiu);
				CheckMmiuMeterSerialLocationThread threads2[] = new CheckMmiuMeterSerialLocationThread[exceptMculist.get(0).mdsIdList.size()];

				i = 0;
				for ( String mdsId : exceptMculist.get(0).mdsIdList) {
					log.info(cnt++ + ": MMIU Meter [" + mdsId + "] Check Meter Serial(D1,D2)");

					threads2[i] = new CheckMmiuMeterSerialLocationThread(mdsId);
					pool.execute(threads2[i]);
					Thread.sleep(1000);
					i++;
				}
				log.info("ExecutorService for mmiu shutdown.");
				pool.shutdown();
				log.info("ExecutorService for mmiu awaitTermination. [" + _timeoutMmiu + "]sec");
				pool.awaitTermination(_timeoutMmiu, TimeUnit.SECONDS);
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
						mcuList.get(i).deviceIdList.add(modem.getDeviceSerial());
						continue;
					}
					McuDeviceList mml = new McuDeviceList();
					mml.mcuId = mcu.getId();
					mml.mdsIdList = new ArrayList<String>();
					mml.mdsIdList.add(m.getMdsId());
					mml.deviceIdList = new ArrayList<String>();
					mml.deviceIdList.add(modem.getDeviceSerial());
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

	protected class CheckMeterSerialLocationThread extends Thread {
		McuDeviceList mcuDeviceList;
		MCU mcu = null;
		CommandGW commandGw = DataUtil.getBean(CommandGW.class);

		public CheckMeterSerialLocationThread(McuDeviceList list) {
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
				log.info("ThreadID[" + Thread.currentThread().getId() + "] CheckMeterSerialLocationThread() thread start. MCU[" + mcuDeviceList.mcuId + "]");

				try {
					List<String> modemList = mcuDeviceList.deviceIdList;
					List<String> meterList = mcuDeviceList.mdsIdList;
					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
            		JSONArray resultArray = new JSONArray();

					for (int i = 0; i < modemList.size(); i++) {
            			JSONObject itemsJson = new JSONObject();
            			JSONObject itemJson  = new JSONObject();
                		FwData fwData = new FwData();
                		ResultJsonType rjt = new ResultJsonType();
						String     strRtnVal = "";
						String     strMeterId = meterList.get(i);
						String     strModemId = modemList.get(i);
						String     strSerial1 = "failed";
						String     strSerial2 = "failed";
						Integer    iCmp = 0;
						String     strDate	= "";
						Object     obj = null;
						Meter meter = meterDao.get(strMeterId);

						// E-meter serial number
						if( checkSreialNByFileMap(strMeterId,"D1") != 0 ) {
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
								log.debug("Failed get result[0.0.96.1.0.255]");
							}
							strRtnVal = "";
							sleep(500);
						}
						else {
							log.debug("[" + strMeterId + "] Skip get result[0.0.96.1.0.255]");
						}

						// E-meter equipment identifier
						if( checkSreialNByFileMap(strMeterId,"D2") != 0 ) {
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
								log.debug("Failed get result[0.0.96.1.1.255]");
							}
						}
						else {
							log.debug("[" + strMeterId + "] Skip get result[0.0.96.1.1.255]");
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

						rjt.MeterId = strMeterId;
						fwData.MacAddress = strModemId;
						fwData.D1 = strSerial1;
						fwData.D2 = strSerial2;
						fwData.Status = iCmp.toString();
						fwData.DateTime = strDate;
						fwData.Meter_id = meter.getId().toString();

						rjt.FwDatas = fwData;
						itemJson.put("MacAddress", fwData.MacAddress);
						itemJson.put("D1", fwData.D1);
						itemJson.put("D2", fwData.D2);
						itemJson.put("Status", fwData.Status);
						itemJson.put("DateTime", fwData.DateTime);
						itemJson.put("Meter_id", fwData.Meter_id);
						itemsJson.put(rjt.MeterId, itemJson);
						resultArray.add(itemsJson);
					}
					updateResultFile(resultArray);

				} catch (Exception e){
					log.info("ThreadID[" + Thread.currentThread().getId() + "] CheckMeterSerialLocationThread() thread end. MCU[" + mcu.getId() + "] is failed.");
				}
				log.info("ThreadID[" + Thread.currentThread().getId() + "] CheckMeterSerialLocationThread() thread end. MCU[" + mcu.getId() + "]");
			}
			else {
				log.debug("MCU[" + mcu.getId() + "] is null.");
			}
		}
	}

	protected class CheckMmiuMeterSerialLocationThread extends Thread {
		String mdsId;
		MCU mcu = null;
		CommandGW commandGw = DataUtil.getBean(CommandGW.class);

		public CheckMmiuMeterSerialLocationThread(String mdsId) {
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

        		JSONArray resultArray = new JSONArray();
        		FwData fwData = new FwData();
        		ResultJsonType rjt = new ResultJsonType();
    			JSONObject itemsJson = new JSONObject();
    			JSONObject itemJson = new JSONObject();

				log.debug("CheckMmiuMeterSerialLocationThread() MeterID[" + meter.getMdsId() + "] ModemType[" + modem.getModemType().name() +
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

					if( checkSreialNByFileMap(mdsId,"D1") != 0 ) {
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
					}
					else {
						log.debug("[" + mdsId + "] Skip get result[0.0.96.1.0.255]");
					}

					if( checkSreialNByFileMap(mdsId,"D2") != 0 ) {
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
					}
					else {
						log.debug("[" + mdsId + "] Skip get result[0.0.96.1.1.255]");
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
//					updateResultFile(mdsId,strModemId,strSerial1,strSerial2,iCmp.toString(),strDate);

					rjt.MeterId = mdsId;
					fwData.MacAddress = strModemId;
					fwData.D1 = strSerial1;
					fwData.D2 = strSerial2;
					fwData.Status = iCmp.toString();
					fwData.DateTime = strDate;
					fwData.Meter_id = meter.getId().toString();

					rjt.FwDatas = fwData;
					itemJson.put("MacAddress", fwData.MacAddress);
					itemJson.put("D1", fwData.D1);
					itemJson.put("D2", fwData.D2);
					itemJson.put("Status", fwData.Status);
					itemJson.put("DateTime", fwData.DateTime);
					itemJson.put("Meter_id", fwData.Meter_id);
					itemsJson.put(rjt.MeterId, itemJson);
					resultArray.add(itemsJson);
				}
				else if((modem.getModemType() == ModemType.MMIU) && (modem.getProtocolType() == Protocol.IP || modem.getProtocolType() == Protocol.GPRS)) {
					Map<String, Object> resultMap1 = new HashMap<String, Object>();
					Map<String, Object> resultMap2 = new HashMap<String, Object>();
					String strRtnVal = "";
					strModemId = modem.getDeviceSerial();

					if( checkSreialNByFileMap(mdsId,"12") != 0 ) {
						for( int rty=0; rty < _retry ; rty++ ) {
							resultMap1 = commandGw.cmdMeterParamGet(mdsId,"0.0.96.1.0.255|1|2|RO|octet-string|");
							strRtnVal = String.valueOf(resultMap1.get("RESULT_VALUE"));
							if( strRtnVal.equals("Success") ) {
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
					}
					else {
						log.debug("[" + mdsId + "] Skip get result[0.0.96.1.0.255]");
					}

					if( checkSreialNByFileMap(mdsId,"D2") != 0 ) {
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
					}
					else {
						log.debug("[" + mdsId + "] Skip get result[0.0.96.1.1.255]");
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
//					updateResultFile(mdsId,strModemId,strSerial1,strSerial2,iCmp.toString(),strDate);

					rjt.MeterId = mdsId;
					fwData.MacAddress = strModemId;
					fwData.D1 = strSerial1;
					fwData.D2 = strSerial2;
					fwData.Status = iCmp.toString();
					fwData.DateTime = strDate;
					fwData.Meter_id = meter.getId().toString();

					rjt.FwDatas = fwData;
					itemJson.put("MacAddress", fwData.MacAddress);
					itemJson.put("D1", fwData.D1);
					itemJson.put("D2", fwData.D2);
					itemJson.put("Status", fwData.Status);
					itemJson.put("DateTime", fwData.DateTime);
					itemJson.put("Meter_id", fwData.Meter_id);
					itemsJson.put(rjt.MeterId, itemJson);
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
//			Set<Condition> conditionList = new HashSet<Condition>();
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
//   						lastStatus = asyncCommandLogDao.getCmdStatus(modem.getDeviceSerial(), cmd);
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
		// -> INSERT START 2018/03/16 #SP-898
		List<Meter> updateListTmp  = new ArrayList<Meter>();
		int countTmp = 0;
		// <- INSERT END   2018/03/16 #SP-898

		if(fileMap.size() > 0){
			try{
				for(int i = 0; i < list.size();i++){
					String mdsIdStr = list.get(i).getMdsId();
					if (fileMap.containsKey(mdsIdStr)) {
						String statusStr = fileMap.get(mdsIdStr).get("Status").toString();
						if(!statusStr.equals("0")){
							log.debug("MDS_ID[" + mdsIdStr +"] is target.");
							// -> UPDATE START 2018/03/16 #SP-898
//							updateList.add(count, list.get(i));
//							count++;
							updateListTmp.add(countTmp, list.get(i));
							countTmp++;
							// <- UPDATE END   2018/03/16 #SP-898
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

				// -> INSERT START 2018/03/16 #SP-898
				for(int i = 0; i < updateListTmp.size();i++){
					updateList.add(count, updateListTmp.get(i));
					countTmp++;
				}
				// <- INSERT END   2018/03/16 #SP-898

			}catch(Exception e){
				log.error(e);
			}
		}
		else{
			return list;
		}
		return updateList;
	}

	synchronized private int checkSreialNByFileMap(String mdsIdStr, String key){
		int iRet = 0;

		if(fileMap.size() > 0){
			try{
				if (fileMap.containsKey(mdsIdStr)) {
					String strResult = fileMap.get(mdsIdStr).get(key).toString();
					if(!strResult.equals("failed")){
						log.debug("MDS_ID[" + mdsIdStr +"] Already get.");
						iRet = 0;
					}
					else {
						log.debug("MDS_ID[" + mdsIdStr +"] is target.");
						iRet = 1;
					}
				} else {
					log.debug("MDS_ID[" + mdsIdStr +"] is target. First time.");
					iRet = 1;
				}
			}catch(Exception e){
				log.error(e);
				iRet = 1;
			}
		}

		return iRet;
	}

	private Map<String,JSONObject> createFileMap() throws IOException{
		String fileName = "./result/CheckMeterSerialLocation_" + _location + "_" +_msa + ".txt";
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
			log.debug("updateResultFile() list count = " + list.size());
			String fileName = "./result/CheckMeterSerialLocation_" + _location + "_" +_msa + ".txt";
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

			// update MeterAttr Table
			{
				Iterator<Map.Entry<String,JSONObject>> it = exeMap.entrySet().iterator();

				while( it.hasNext() ){
					Map.Entry<String, JSONObject> e = it.next();
					Map<String,Object> clm = e.getValue();
					String strMeterId = e.getKey();
					String strMeter_id = (String)(clm.get("Meter_id")  );

					String textAttrs[] = new String[10];
					textAttrs[0] = strMeterId;
					textAttrs[1] = (String)(clm.get("MacAddress"));
					textAttrs[2] = (String)(clm.get("D1")        );
					textAttrs[3] = (String)(clm.get("D2")        );
					textAttrs[4] = (String)(clm.get("Status")    );
					textAttrs[5] = (String)(clm.get("DateTime")  );
					textAttrs[6] = "";
					textAttrs[7] = "";
					textAttrs[8] = "";
					textAttrs[9] = "";
					log.debug(
							"Attr[0] = "  + textAttrs[0] +
							" Attr[1] = " + textAttrs[1] +
							" Attr[2] = " + textAttrs[2] +
							" Attr[3] = " + textAttrs[3] +
							" Attr[4] = " + textAttrs[4] +
							" Attr[5] = " + textAttrs[5] +
							" Attr[6] = " + textAttrs[6] +
							" Attr[7] = " + textAttrs[7] +
							" Attr[8] = " + textAttrs[8] +
							" Attr[9] = " + textAttrs[9] );
					setMeterAttr(null,Integer.valueOf(strMeter_id),textAttrs);
				}
			}

			if (_testmode == 1 ) {
				String resultLog = "./result/CheckMeterSerialLocation_" + _location + "_" +_msa + ".csv";
				OutputStreamWriter osw2 = new OutputStreamWriter(new FileOutputStream(resultLog, true));
				Iterator<Map.Entry<String,JSONObject>> it = exeMap.entrySet().iterator();

				while( it.hasNext() ){
					Map.Entry<String, JSONObject> e = it.next();
					Map<String,Object> clm = e.getValue();
					osw2.write( e.getKey() + "," +
							(String)(clm.get("MacAddress")) + "," +
							(String)(clm.get("D1")        ) + "," +
							(String)(clm.get("D2")        ) + "," +
							(String)(clm.get("Status")    ) + "," +
							(String)(clm.get("DateTime")  ) + "\n");
				}
				osw2.close();
			}
			else if( _testmode > 1 ) {
				String resultLog = "./result/CheckMeterSerialLocation_" + _location + "_" +_msa + ".log";
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

	private MeterAttr getMeterAttr(Integer meter_id){
		return meterAttrDao.getByMeterId(meter_id);
	}

	private MeterAttr getMeterAttrByMdsId(String mds_id){
		return meterAttrDao.getByMdsId(mds_id);
	}

	private Map<String, Object> 	 setMeterAttr(Integer id, Integer meter_id, String textAttrs[])
	{
		Map<String, Object> result = new HashMap<String, Object>();
		MeterAttr meterAttr = null;
		int iSaveOrUpdate  = 0; // 0:save 1:update 2:skip

		try {
			if ( id != null ){
				meterAttr = meterAttrDao.get(id.longValue());
			}
			else if ( meter_id != null){
				meterAttr = meterAttrDao.getByMeterId(meter_id);
			}
			if ( meterAttr != null){
				if( meterAttr.getTextAttr04().equals("0")) {
					log.debug("[" + meterAttr.getTextAttr00() + "] has already been checked.");
					iSaveOrUpdate = 2;
				}
				else {
					iSaveOrUpdate = 1;
				}
				log.debug("this record exist.");
				for ( int i = 0; i < textAttrs.length; i++ ){
					 String xx = String.format("%02d", i);
					 if ( textAttrs[i] != null ){
						 BeanUtils.copyProperty(meterAttr,
	                           "textAttr" + xx, textAttrs[i]);
					 }
				}
			}
			else {
				log.debug("this record not found.");
				if ( meter_id != null ){
					meterAttr = new MeterAttr();
					meterAttr.setMeterId(meter_id);
					for ( int i = 0; i < textAttrs.length; i++ ){
						 String xx = String.format("%02d", i);
						 if ( textAttrs[i] != null ){
							 BeanUtils.copyProperty(meterAttr,
									 "textAttr" + xx, textAttrs[i]);
						 }
					}
				}
			}
			if ( meterAttr != null) {
				if( iSaveOrUpdate == 0 ) {
					meterAttrDao.add(meterAttr);
				}
				else if( iSaveOrUpdate == 1 ) {
					meterAttrDao.update(meterAttr);
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		if ( meterAttr != null )
			result.put("id", meterAttr.getId());
		return result;
	}

}
