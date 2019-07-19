/**
 * SP-975
 * SP-1055
 */
package com.aimir.fep.tool;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.constants.CommonConstants.ModemIFType;
import com.aimir.constants.CommonConstants.OperatorType;
import com.aimir.constants.CommonConstants.TR_OPTION;
import com.aimir.constants.CommonConstants.TR_STATE;
import com.aimir.dao.device.AsyncCommandLogDao;
import com.aimir.dao.device.AsyncCommandParamDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.meter.AbstractMDSaver;
import com.aimir.fep.meter.data.MeterData;
import com.aimir.fep.meter.parser.DLMSKaifaTable.DLMSVARIABLE.DLMS_CLASS;
import com.aimir.fep.meter.parser.DLMSKaifaTable.DLMSVARIABLE.DLMS_CLASS_ATTR;
import com.aimir.fep.meter.parser.DLMSKaifaTable.DLMSVARIABLE.OBIS;
import com.aimir.fep.protocol.smsp.SMSConstants;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Util;
import com.aimir.model.device.AsyncCommandLog;
import com.aimir.model.device.AsyncCommandParam;
import com.aimir.model.device.Meter;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeUtil;


@Component
public class RecollectMeteringSoriaDWH {
	private static Logger logger= LoggerFactory.getLogger(RecollectMeteringSoriaDWH.class);

	@PersistenceContext
	protected EntityManager em;

	@Autowired
	private MeterDao meterDao;	

	@Resource(name="transactionManager")
	JpaTransactionManager txmanager;		

	public enum RecollecStatus{
		SUCCESS("0"),      
		FAILED("1"),   
		NO_CONNECT ("2"), 
		STARTED("3"),       
		ALREADY_RECEIVED("4"),
		COMPLETED ("5"),
		PARTLY ("6");       

		private String code;

		RecollecStatus(String code) {
			this.code = code;
		}

		public String getCode() {
			return this.code;
		}
	}
	final static String DB_TIMESTAMP_TZ_FMT = "'YYYYMMDDHH24MISSTZHTZM'";
	final static String DB_DATE_FMT         = "'YYYYMMDDHH24MISS'";
	final int MSG_TIMEOUT = 30;
	final int TUNNEL_TIMEOUT = 0;	

	private String		_dwhView = null;
	private boolean		_usePriority = false;
	private int 		_maxSubgigaThreadWorker = 20;
	private int			_maxMmiuGprsThreadWorker = 20;
	
	private int 		_timeout = 60 * 23; //min
	
	private int 		_maxReadRecordNum = 2000;
	
	private int			_smsJoinMin = 24 * 60 ;

	private String		_lastLinkTime =null;;
	
	private String		_dso = null;
	
	private String 		_priority = null;
	
	CommonConstants.DeviceType _deviceType;
	private String		_modemType = "ALL";   
	
	private int			_beforeHour = 7;
	//String timeFormat = "yyyyMMddHHmmssZ";	//YYYYMMDDhhmmss+XXXX
	String timeFormat = "yyyyMMddHHmmss";
	

	///// for Execute Result Num
	private final int successIndex = 0;
	private final int failIndex = 1;
	private final int notExecuteIndex = 2;
	private final int alreadySendedIndex = 3;
	private final int timeOverIndex = 4;
	private AtomicInteger[]   _subGigaResult = new AtomicInteger[5];
	private AtomicInteger[]   _mmiuGprsResult = new AtomicInteger[5];
	
	private AtomicInteger subGigaAllRec = new AtomicInteger(0);
	
	private final int asyncRecNumIndex = 0;
	//private final int faildIndex = 1; // meter has multiple records ,  not joined. => RECOLLECT_STATUS = FAIL
	private final int joindRecNumIndex = 2;
	//private final int alreadySendedIndex = 3;
	private AtomicInteger[]   _mmiuSmsResult = new AtomicInteger[4];
	
	///// GRPS RECOLLECT
	String[] _meters = null;
	String _fromDate = null;
	String _toDate = null;
	int  _gprsforce = 0;
	
	private String getCurrentTime()
	{
		return DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss");
	}
	
	private String getCurrentUtcTime()
	{
		SimpleDateFormat utcdatestr = new SimpleDateFormat("yyyyMMddHHmmss");
		utcdatestr.setTimeZone(TimeZone.getTimeZone("UTC"));
		return  utcdatestr.format(new Date());
	}
	
	private boolean checkExecuteTime(String recollectValidEndStr)
	{
		boolean ret = false;
		SimpleDateFormat timestamp_sdf = new SimpleDateFormat("yyyyMMddHHmmssZ");
		try {
			Date endDate = timestamp_sdf.parse(recollectValidEndStr);
			Date now = new Date();
			if (now.after(endDate)) {
				ret = false;
			}
			else {
				ret = true;
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
	
	private boolean checkSlaRawdata(String meterId,  String startDate, String endDate, String  lpIntervalStr ) {
		boolean ret = false;
		
		int lpInterval = 0;
		try {
			if ( lpIntervalStr != null ) 
				lpInterval = Integer.parseInt(lpIntervalStr);
		}
		catch (Exception e) {
			logger.error(e.getMessage() + "can't convert to int:" + lpIntervalStr);
		}
		if ( lpInterval <= 0 ) {
			return ret;
		}
		
		StringBuffer sb = new StringBuffer();
		ArrayList<Object> sqlParams = new ArrayList<Object>();
		
		sb.append("select sum(CURRENT_MV_COUNT)  from sla_rawdata ");
		sb.append(" where METER_ID = ? and ? <= YYYYMMDD and YYYYMMDD <= ? ") ;
		sqlParams.add(meterId);
		sqlParams.add(startDate.substring(0, 8));
		sqlParams.add(endDate.substring(0, 8));
		Query query = em.createNativeQuery(sb.toString());
		for ( int i = 0; i < sqlParams.size(); i++) {
			query.setParameter(i+1,  sqlParams.get(i));
		}
		
		
		BigDecimal  cntObj = (BigDecimal) query.getSingleResult();
		int sendedCnt = 0;
		if ( cntObj != null ) {
			sendedCnt = cntObj.intValue();
		}
		if ( sendedCnt <= 0 ) {
			return ret;
		}
		Date start = null;
		Date end = null;
		try {
			SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
			start = fmt.parse(startDate.substring(0,8));
			end = fmt.parse(endDate.substring(0,8));
		} catch (Exception e) {
		}
		if ( start != null && end != null ) {
			long days = ( end.getTime() - start.getTime()) / (1000 * 60 * 60 * 24) + 1;
			if ( ((60 * 24 ) / lpInterval) * days <= sendedCnt  ) {
				ret = true;
			}
		}
		return ret;
	}
	
	private String[]  getPriorityList(Map<String,Object> params ) {
		TransactionStatus txStatus = null;
		String lastLinkTime = StringUtil.nullToBlank(params.get("lastLinkTime"));
		String modemType = StringUtil.nullToBlank(params.get("modemType"));
		String protocolType = StringUtil.nullToBlank(params.get("protocolType"));
		String[] dsoList = (String[])params.get("dsoList");
		String recollectType = StringUtil.nullToBlank(params.get("recollectType"));
		String sysId = StringUtil.nullToBlank(params.get("sysId"));
		
		ArrayList<String> prioList = new ArrayList<String>();
		List resultList = new ArrayList<Object[]>();
		ArrayList<Object> sqlParams = new ArrayList<Object>();
		try {
//			txStatus = txmanager.getTransaction(null);
			StringBuffer sb = new StringBuffer();
			
			sb.append("select REC.PRIORITY " + 
					"  from "+ _dwhView + " REC " + 
					"  join METER on METER.MDS_ID = REC.METERID" + 
					"  join MODEM on METER.MODEM_ID = MODEM.id ");
			if ( dsoList != null ) {
					sb.append("  join LOCATION LOC on METER.LOCATION_ID = LOC.id ");
			}
			//sb.append(" where RECOLLECT_VALID_START <= SYSTIMESTAMP AND SYSTIMESTAMP < RECOLLECT_VALID_END ");
			sb.append(" where  SYSTIMESTAMP between RECOLLECT_VALID_START  AND RECOLLECT_VALID_END ");
			sb.append(" AND  RECOLLECT_COMPLETED is null ");
			if ( !"".equals(modemType) ) {
				sb.append("  AND MODEM.MODEM_TYPE = ? " );
				sqlParams.add(modemType);
			}
			if ( !"".equals(protocolType) ) {
				sb.append("  AND MODEM.PROTOCOL_TYPE = ? " );
				sqlParams.add(protocolType);
			}
			if ( !"".equals(lastLinkTime) ) {
				sb.append("  AND MODEM.LAST_LINK_TIME >= ? " );
				sqlParams.add(lastLinkTime);
			}
			if ( !"".equals(sysId)) {
				sb.append(" and MCU.SYS_ID = ? ");
				sqlParams.add(sysId);
			}
			if ( dsoList != null ) {
				sb.append(" and LOC.NAME in (");
				for ( int i = 0; i <  dsoList.length; i++ ) {
					sb.append("?");
					sqlParams.add(dsoList[i]);
					if ( i < dsoList.length - 1 ) {
						sb.append(",");
					}
				}
				sb.append(") ");
			}
			sb.append("group by REC.PRIORITY ");
			sb.append("order by REC.PRIORITY ");
			
			Query query = em.createNativeQuery(sb.toString());
			
//			logger.debug(sb.toString());
			for ( int i = 0; i < sqlParams.size(); i++) {
				query.setParameter(i+1,  sqlParams.get(i));
			}

			resultList = query.getResultList();
			
			for ( Object result : resultList) {
				if ( result != null && !"".equals(result.toString())) {
					prioList.add(result.toString());
				}
			}
			//Collections.sort(prioList);
//			txmanager.commit(txStatus);
		}  catch (Exception ex) {
			logger.error("Get target list error - " + ex, ex);
//			if(txStatus != null && !txStatus.isCompleted()) {
//				txmanager.rollback(txStatus);
//			} 
		} finally {
//			if (em != null) {
//				em.close();
//			}
		}
		return prioList.toArray(new String[prioList.size()]);
	}
	
	private List<String> getMcuListFromDWHView(String[] dsoList, String _lastLinkTime, String priority) {
		//String dso = StringUtil.nullToBlank(_dso);
		String lastLinkTime = StringUtil.nullToBlank(_lastLinkTime);
		ArrayList<Object> params = new ArrayList<Object>();
		List<String> targetList = new ArrayList<String>();
		try {
			
			StringBuffer sb = new StringBuffer();
			sb.append("select  mcu.sys_id , count(*) cnt from "+ _dwhView + " rec join meter on meter.mds_id = rec.meterid " +
					"  join location loc on meter.location_id = loc.id " + 
					"  join modem on meter.modem_id = modem.id " + 
					"  join mcu on modem.mcu_id = mcu.id ");
			sb.append(" where modem.MODEM_TYPE='SubGiga' ");
			sb.append(" and  SYSTIMESTAMP between RECOLLECT_VALID_START  AND RECOLLECT_VALID_END ");
			sb.append(" and  RECOLLECT_COMPLETED is null ");
			//sb.append(" and  RECOLLECT_VALID_START <= SYSTIMESTAMP - 4 AND SYSTIMESTAMP - 4  <= RECOLLECT_VALID_END ");
			if ( !"".equals(lastLinkTime)) {
				sb.append(" and modem.last_link_time >= ? ");
				params.add(lastLinkTime);
			}
			if ( priority != null ) {
				sb.append(" and rec.priority = ? ");
				params.add(priority);
			}
			if ( dsoList != null ) {
				sb.append(" and loc.name in (");
				for ( int i = 0; i <  dsoList.length; i++ ) {
					sb.append("?");
					params.add(dsoList[i]);
					if ( i < dsoList.length - 1 ) {
						sb.append(",");
					}
				}
				sb.append(")");
			}
			sb.append("   group by  mcu.sys_id order by cnt desc");
			
			Query query = em.createNativeQuery(sb.toString());

			for ( int  i = 0; i < params.size(); i++ ) {
				query.setParameter(i+1, params.get(i));
			}

			List<Object[]> list = query.getResultList();
			logger.debug("Found target List size = {}", (list == null ? "Null~!" : list.size()));
			for (Object object : list) {
				Object[] arrObject = (Object[]) object;
				targetList.add((String) arrObject[0]);
			}
		} catch (Exception ex) {
			logger.error("Get target list error - " + ex, ex);
		} finally {
//			if (em != null) {
//				em.close();
//			}
		}
		return targetList;
	}
	
	private List<Map<String,String>> getRecollectsByMCU(String _sysId, String[] _dsoList, String _lastLinkTime, String priority ) {

		//String dso = StringUtil.nullToBlank(_dso);
		String sysId = StringUtil.nullToBlank(_sysId);
		//String lastLinkTime = StringUtil.nullToBlank(_lastLinkTime);
		
		ArrayList<Map<String,String>> resultList = new ArrayList<Map<String,String>>();
		HashMap<String,Object> params = new HashMap<String,Object>();
		
		try {
			params.put("modemType", "SubGiga");
			params.put("lastLinkTime", _lastLinkTime);
			params.put("dsoList", _dsoList);
			params.put("sysId", sysId);
			String[] priorityList = new String[1];
			params.put("priority", priority);
			params.put("orderBy", " order by RANGE_START_DATETIME ");

			List<Object[]>  arrList = getRecollectList(params, false);
			
			logger.debug("MCU[{}] Recollect Record Size = Found target List size = {}", 
					_sysId, (arrList == null ? "Null~!" : arrList.size()));
			
			for (Object[] rec  : arrList) {
				try {
					HashMap<String,String> entry = new HashMap<String,String>();
					entry.put("modemId", (String)rec[0]);
					entry.put("fwVer", (String)rec[1]);
					// 2 modemType, 3 modemProtocol
					entry.put("meterId", (String)rec[4]);
					if ( rec[5] == null )
						entry.put("modemPort", "0");
					else
						entry.put("modemPort", String.valueOf( rec[5]));
					//SimpleDateFormat timestamp_sdf = new SimpleDateFormat(timeFormat);
					//Date startDate = timestamp_sdf.parse(new String((String) rec[6]));
					//Date endDate   = timestamp_sdf.parse(new String((String) rec[7]));
					entry.put("fromDate", (String)rec[6]);
					entry.put("toDate", (String)rec[7]);
					entry.put("lastLinkTime", (String)rec[8]);
					entry.put("dso", (String)rec[9]);
					entry.put("rec_type", (String)rec[10]);
					entry.put("rec_prio",(String)rec[11]);
					entry.put("recValidStartStr",  (String) rec[12]);
					entry.put("recValidEndStr",  (String) rec[13]);
					
					entry.put("fromDateStr",  (String) rec[14]);
					entry.put("toDateStr",  (String) rec[15]);
					entry.put("lpInterval", String.valueOf(rec[16]));
					entry.put("id", (String)rec[17]);
					// check already sended 
					if ( checkSlaRawdata((String)entry.get("meterId"), (String)entry.get("fromDate"), (String)entry.get("toDate"), (String)entry.get("lpInterval"))) {
						_subGigaResult[alreadySendedIndex].getAndIncrement();
						entry.put("started", getCurrentUtcTime());
						entry.put("completed", getCurrentUtcTime());
						entry.put("status", RecollecStatus.ALREADY_RECEIVED .name());
						 updateRecollectTable(entry,(String)entry.get("id"),
								 (String)entry.get("started"), (String)entry.get("completed"), (String)entry.get("status"),"SubGiga");
						 continue;
					}
					resultList.add(entry);
				}
				catch (Exception e) {
					logger.error("Exception - " + e, e);
					logger.error("READ RECORD RESULT[Fail]  MCU[{}] modemId[{}],meterId[{}] startDate[{}] endDate[{}] ", 
							_sysId,(String)rec[0], (String)rec[4], (String)rec[6], (String)rec[7]);
				}
			}
		} catch (Exception ex) {
			logger.error("Get target list error - " + ex, ex);
		} finally {
//			if (em != null) {
//				em.close();
//			}
		}
		
		return resultList;
	}

	
	private Map<String,String> getEntryMap(Object[] arr) 
	{
		Map<String,String> ret = new HashMap<String,String>();
		SimpleDateFormat timestamp_sdf = new SimpleDateFormat(timeFormat);
		try {
			ret.put("modemId", (String)arr[0]);
			ret.put("fwVer", (String)arr[1]);
			ret.put("modemType", (String)arr[2]);
			ret.put("modemProtocol", (String)arr[3]);
			ret.put("meterId", (String)arr[4]);
			ret.put("modemPort", String.valueOf(arr[5]));
			Date startDate = timestamp_sdf.parse((String) arr[6]);
			Date endDate   = timestamp_sdf.parse((String) arr[7]);
			ret.put("fromDate",  DateTimeUtil.getDateString(startDate));
			ret.put("toDate",  DateTimeUtil.getDateString(endDate));
			ret.put("lastLinkTime", (String)arr[8]);
			ret.put("dso", (String)arr[9]);
		}catch(Exception e) {
			logger.error(e.getMessage(),e);
		}
		return ret;
		
	}
	
	private boolean checkRecollectToDate(Date fromDate, Date toDate)
	{
		
		if ( ( toDate.getTime() - fromDate.getTime())/(1000*60) < _smsJoinMin  )
			return true;
		else 
			return false;
		
	}
	
	private ArrayList<ArrayList<HashMap<String,String>>> getMeterEntryMapForGprs(List<Object[]> arrList)
	{
		HashMap<String,Object> meterHash = new HashMap<String,Object>();
		ArrayList<ArrayList<HashMap<String,String>>> ret = new ArrayList<ArrayList<HashMap<String,String>>>();
		HashMap<String,String> entry = null;
		String currMeter = null;
		//Date meterStartDate = null ; 

		//SimpleDateFormat timestamp_sdf = new SimpleDateFormat(timeFormat);
	//	HashMap<String,ArrayList<HashMap<String,String>>> meterEntry  = null;
		ArrayList<HashMap<String,String>> meterRecList = null;
		
		for( Object[] arr: arrList) {
			String meterId = (String)arr[4];
			try {

				entry = new HashMap<String,String>();
				entry.put("modemId", (String)arr[0]); 
				entry.put("fwVer", (String)arr[1]);
				entry.put("modemType", (String)arr[2]);
				entry.put("modemProtocol", (String)arr[3]);				
				entry.put("meterId", (String)arr[4]); 
				entry.put("modemPort",String.valueOf(arr[5]));
//				Date startDate = timestamp_sdf.parse((String) arr[6]);
//				Date endDate   = timestamp_sdf.parse((String) arr[7]);
//				entry.put("fromDate",  DateTimeUtil.getDateString(startDate));
//				entry.put("toDate",  DateTimeUtil.getDateString(endDate));
				entry.put("fromDate", (String)arr[6]);
				entry.put("toDate", (String)arr[7]);
				entry.put("lastLinkTime", (String)arr[8]);
				entry.put("dso", (String)arr[9]);
				entry.put("rec_type", (String)arr[10]);
				entry.put("rec_prio",(String)arr[11]);
				entry.put("recValidStartStr",  (String) arr[12]);
				entry.put("recValidEndStr",  (String) arr[13]);
				entry.put("fromDateStr",  (String) arr[14]);
				entry.put("toDateStr",  (String) arr[15]);
				entry.put("lpInterval", String.valueOf(arr[16]));
				entry.put("id", (String) arr[17]);
				
				// check already sended 
				if ( checkSlaRawdata(entry.get("meterId"), entry.get("fromDate"), entry.get("toDate"),entry.get("lpInterval") )) {
					_mmiuGprsResult[alreadySendedIndex].getAndIncrement();
					entry.put("started", getCurrentUtcTime());
					entry.put("completed", getCurrentUtcTime());
					entry.put("status", RecollecStatus.ALREADY_RECEIVED .name());
					 updateRecollectTable(entry,(String)entry.get("id"),
							 (String)entry.get("started"), (String)entry.get("completed"), (String)entry.get("status"),"GPRS");
					 continue;
				}
				//if ( currMeter == null || !currMeter.equals(meterId)) {
				//	meterRecList = new ArrayList<HashMap<String,String>>();
				//	ret.add(meterRecList);
				//}
				if ( meterHash.get(entry.get("meterId")) == null ) {
					meterRecList = new ArrayList<HashMap<String,String>>();
					ret.add(meterRecList);
					meterRecList.add(entry);
					meterHash.put(entry.get("meterId"),meterRecList);
				}
				else {
					meterRecList =  (ArrayList<HashMap<String,String>> )meterHash.get(entry.get("meterId"));
					meterRecList.add(entry);
				}
				currMeter  = meterId;
			}catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		return ret;
	}
	
	private ArrayList<ArrayList<HashMap<String,Object>>> getMeterEntryMapForSMS(List<Object[]> arrList)
	{
		ArrayList<ArrayList<HashMap<String,Object>>> ret = new ArrayList<ArrayList<HashMap<String,Object>>>();
		HashMap<String,Object> entry = null;
		String currMeter = null;
		Date startDate = null ; 
		Date endDate = null;
		Date meterStartDate = null ; 
		ArrayList<HashMap<String,Object>> meterRecList = null;
		
		SimpleDateFormat timestamp_sdf = new SimpleDateFormat(timeFormat);
		for( Object[] arr: arrList) {
			try {
				String meterId = (String)arr[4];	

				entry = new HashMap<String,Object>();
				entry.put("deviceSerial", (String)arr[0]);
				entry.put("fwVer", (String)arr[1]);
				entry.put("modemType", (String)arr[2]);
				entry.put("modemProtocol", (String)arr[3]);
				entry.put("mdsId", (String)arr[4]);
				entry.put("modemPort", arr[5]);
				//startDate = timestamp_sdf.parse((String) arr[6]);
				//endDate   = timestamp_sdf.parse((String) arr[7]);
				entry.put("fromDate", (String) arr[6]);
				entry.put("toDate",  (String) arr[7]);
				entry.put("lastLinkTime", (String)arr[8]);
				entry.put("dso", (String)arr[9]);
				entry.put("rec_type", (String)arr[10]);
				entry.put("rec_prio",(String)arr[11]);
				entry.put("recValidStartStr",  (String) arr[12]);
				entry.put("recValidEndStr",  (String) arr[13]);
				entry.put("fromDateStr",  (String) arr[14]);
				entry.put("toDateStr",  (String) arr[15]);
				entry.put("lpInterval", String.valueOf(arr[16]));
				entry.put("id", (String)arr[17]);
				
				// check already sended 
				if ( checkSlaRawdata((String)entry.get("mdsId"), (String)entry.get("fromDate"), (String)entry.get("toDate"), (String)entry.get("lpInterval"))) {
					_mmiuSmsResult[alreadySendedIndex].getAndIncrement();
					entry.put("started", getCurrentUtcTime());
					entry.put("completed", getCurrentUtcTime());
					entry.put("status", RecollecStatus.ALREADY_RECEIVED .name());
					 updateRecollectTable(entry,(String)entry.get("id"),
							 (String)entry.get("started"), (String)entry.get("completed"), (String)entry.get("status"),"SMS");
					 continue;
				}
				if ( currMeter == null || !currMeter.equals(meterId)) {
					meterRecList = new ArrayList<HashMap<String,Object>>();
					ret.add( meterRecList);
					meterRecList.add(entry);
					meterStartDate = timestamp_sdf.parse((String) arr[6]);
				}
				else {
					// check end date 
					Date meterEndDate = timestamp_sdf.parse((String) arr[7]);
					if ( checkRecollectToDate(meterStartDate, meterEndDate)) {
						// join start-end term in asyncCommand
						//_mmiuSmsResult[joindRecNumIndex].getAndIncrement();
						meterRecList.add(entry);
					}
					else {
						// can't execute => RECOLLECT_STATUS = FAILED 
						_mmiuSmsResult[failIndex].getAndIncrement();
						entry.put("started", getCurrentUtcTime());
						entry.put("completed", getCurrentUtcTime());
						entry.put("status", RecollecStatus.FAILED.name());
						 updateRecollectTable(entry,(String)entry.get("id"),
								 (String)entry.get("started"), (String)entry.get("completed"), (String)entry.get("status"),"SMS");
					}
				}
				currMeter  = meterId;
			}catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		return ret;
	}
	
	private List  getRecollectList(Map<String,Object> params, boolean count ) {
		TransactionStatus txStatus = null;
		//String dso = StringUtil.nullToBlank(params.get("dso"));
		String sysId = StringUtil.nullToBlank(params.get("sysId"));
		String lastLinkTime = StringUtil.nullToBlank(params.get("lastLinkTime"));
		String modemType = StringUtil.nullToBlank(params.get("modemType"));
		String protocolType = StringUtil.nullToBlank(params.get("protocolType"));
		String[] protocolTypeList =   (String[])params.get("protocolTypeList");
		String orderBy = StringUtil.nullToBlank(params.get("orderBy"));
		Integer  firstResult = (Integer)params.get("firstResult");
		Integer  maxResults = (Integer)params.get("maxResults");
		String[] dsoList = (String[])params.get("dsoList");
		String priority = (String)params.get("priority");
		String[] priorityList = (String[])params.get("priorityList");
		
		List resultList = new ArrayList<Object[]>();
		ArrayList<Object> sqlParams = new ArrayList<Object>();
		try {
//			txStatus = txmanager.getTransaction(null);
			StringBuffer sb = new StringBuffer();

			if ( count ) {
				sb.append("select count(*) from (");
			}
			sb.append("select  MODEM.DEVICE_SERIAL, MODEM.FW_VER , MODEM.MODEM_TYPE, MODEM.PROTOCOL_TYPE, \n"
					+ "METER.MDS_ID, METER.MODEM_PORT, \n" + 
					"  TO_CHAR(SYS_EXTRACT_UTC(REC.RANGE_START_DATETIME)," + DB_DATE_FMT + "), \n" + 
					"  TO_CHAR(SYS_EXTRACT_UTC(REC.RANGE_END_DATETIME), " + DB_DATE_FMT + "), \n" + 
					"  MODEM.LAST_LINK_TIME, " + 
					"  LOC.name, " +
					"  REC.RECOLLECT_TYPE, " + 
					"  REC.PRIORITY, \n" + 
					"  TO_CHAR(REC.RECOLLECT_VALID_START, " + DB_TIMESTAMP_TZ_FMT + "), \n" + 
					"  TO_CHAR(REC.RECOLLECT_VALID_END, " + DB_TIMESTAMP_TZ_FMT + "), \n" + 
					"  TO_CHAR(REC.RANGE_START_DATETIME, "+ DB_TIMESTAMP_TZ_FMT + "), \n" + 
					"  TO_CHAR(REC.RANGE_END_DATETIME,  " + DB_TIMESTAMP_TZ_FMT + "), \n" + 
					"  METER.LP_INTERVAL, " +
					"  RAWTOHEX (REC.ID) " +
					"  from "+ _dwhView + " REC " + 
					"  join METER on METER.MDS_ID = REC.METERID" + 
					"  join MODEM on METER.MODEM_ID = MODEM.id ");
			if ( !"".equals(modemType) ) {
				sb.append("  AND MODEM.MODEM_TYPE = ? \n" );
				sqlParams.add(modemType);
			}
			
			if ( !"".equals(protocolType) ) {
				sb.append("  AND MODEM.PROTOCOL_TYPE = ? \n" );
				sqlParams.add(protocolType);
			}
			else if (protocolTypeList != null ) {
				sb.append(" AND  MODEM.PROTOCOL_TYPE  in (");
				for ( int i = 0; i <  protocolTypeList.length; i++ ) {
					sb.append("?");
					sqlParams.add(protocolTypeList[i]);
					if ( i < protocolTypeList.length - 1 ) {
						sb.append(",");
					}
				}
				sb.append(") \n");
			}
			sb.append("  join LOCATION LOC on METER.LOCATION_ID = LOC.id ");
			if ( dsoList != null ) {
				sb.append(" and LOC.NAME in (");
				for ( int i = 0; i <  dsoList.length; i++ ) {
					sb.append("?");
					sqlParams.add(dsoList[i]);
					if ( i < dsoList.length - 1 ) {
						sb.append(",");
					}
				}
				sb.append(") \n");
			}
			
			if ( ! "".equals(sysId)) {
				sb.append("  join MCU on MODEM.MCU_ID = MCU.id and MCU.SYS_ID = ? \n" );
				sqlParams.add(sysId);
			}
			sb.append(" where SYSTIMESTAMP BETWEEN  RECOLLECT_VALID_START AND RECOLLECT_VALID_END \n");
			//sb.append(" where RECOLLECT_VALID_START <= SYSTIMESTAMP - 4 AND SYSTIMESTAMP -4 <= RECOLLECT_VALID_END ");
			sb.append(" and RECOLLECT_COMPLETED is null \n");

			if ( !"".equals(lastLinkTime) ) {
				sb.append("  AND MODEM.LAST_LINK_TIME >= ? " );
				sqlParams.add(lastLinkTime);
			}

//			if ( !"".equals(dso)) {
//				sb.append(" and REC.DSO = ? ");
//				sqlParams.add(dso);
//			}

			
			if ( priority != null ) {
				sb.append(" and REC.PRIORITY = ? ");
				sqlParams.add(priority);
			}
			else if (priorityList != null  ){
				sb.append(" and REC.PRIORITY in (");
				for ( int i = 0; i <  priorityList.length; i++ ) {
					sb.append("?");
					sqlParams.add(priorityList[i]);
					if ( i < priorityList.length - 1 ) {
						sb.append(",");
					}
				}
				sb.append(") ");
			}
			if ( !"".equals("orderBy")) {
				sb.append(" " + orderBy + " ");
			}
			if ( count ) {
				sb.append(" ) ");
			}
			
			Query query = em.createNativeQuery(sb.toString());
			
			if ( firstResult != null ) {
				query.setFirstResult(firstResult);
			}
			
			if ( maxResults != null ) {
				query.setMaxResults(maxResults);
			}
//			logger.debug(sb.toString());
			for ( int i = 0; i < sqlParams.size(); i++) {
				query.setParameter(i+1,  sqlParams.get(i));
//				int a = i+1;
//				logger.debug("param[{}]={}", a ,sqlParams.get(i));
			}

			resultList = query.getResultList();
//			txmanager.commit(txStatus);
		}  catch (Exception ex) {
			logger.error("Get target list error - " + ex, ex);
//			if(txStatus != null && !txStatus.isCompleted()) {
//				txmanager.rollback(txStatus);
//			} 
		} finally {
//			if (em != null) {
//				em.close();
//			}
		}
		return resultList;
	}

	public static void main(String[] args) {
		String dev =  "";
		String[] contextFiles = new String[] { "/config/spring-fep-schedule2.xml" };
		
		for (int i = 0; i < args.length; i += 2) {
			String nextArg = args[i];
			if (nextArg.startsWith("-contextFile")) {
				if ( !"${contextFile}".equals(args[i + 1]))
					contextFiles = new String[] { args[i + 1] };
				}
			}

		ApplicationContext ctx = new ClassPathXmlApplicationContext(contextFiles); 
		DataUtil.setApplicationContext(ctx);
		RecollectMeteringSoriaDWH task = ctx.getBean(RecollectMeteringSoriaDWH.class);
		logger.info("======================== RecollectMeteringSoriaDWH start. ========================");
		task.execute(args);
		logger.info("======================== RecollectMeteringSoriaDWH end. ========================");
		
		System.exit(0);
	}

	public void execute(String[] args) {

		String deviceType = null;
		String smsJoinMin = null; 
		String strMaxThreadSubGiga = null;
		String strMaxThreadMmiuGprs = null;
		_deviceType = DeviceType.Meter;
//		////////for test
//
//		String data = null;
//		String deviceSerial= "000B160010000075";
		String mdsId = null;
//		////////for test
		try {
			for (int i = 0; i < args.length; i += 2) {
				String nextArg = args[i];
				
//				logger.debug("arg[i]=" + args[i] + "arg[i+1]=" + args[i+1]);
	
				if (nextArg.startsWith("-deviceType")) {
					if ( !"${deviceType}".equals(args[i + 1]))
						deviceType =args[i + 1];
					if ( deviceType.equals(DeviceType.Modem.name())) {
						_deviceType = DeviceType.Modem;
					}
				}
				else if ( nextArg.startsWith("-dso")){
					if ( !"${dso}".equals(args[i + 1]))
						_dso  = args[i + 1];
				}
				else if ( nextArg.startsWith("-priority")) {
					if ( !"${priority}".equals(args[i + 1]))
						_priority  = args[i + 1];
				}
//				else if ( nextArg.startsWith("-meterId")){
//					if ( !"${meterId}".equals(args[i + 1]))
//						_mdsId = args[i + 1];
//				}
				else if ( nextArg.startsWith("-smsJoinMin")){
					if ( !"${smsJoinMin}".equals(args[i + 1]))
						smsJoinMin = args[i + 1];
				}
				else if ( nextArg.startsWith("-maxThreadSubGiga")){
					if ( !"${maxThreadSubGiga}".equals(args[i + 1]))
						strMaxThreadSubGiga = args[i + 1];
				}
				else if ( nextArg.startsWith("-maxThreadMmiuGprs")){
					if ( !"${maxThreadMmiuGprs}".equals(args[i + 1])){
						strMaxThreadMmiuGprs = args[i+1];
					}
				}
				else if ( nextArg.startsWith("-modemType")) {
					if ( "SubGiga".equalsIgnoreCase(args[i+1])) {
						_modemType = "SubGiga";
					} else if ( "MMIUGPRS".equalsIgnoreCase(args[i+1])) {
						_modemType = "MMIUGPRS";
					} else if ( "MMIUSMS".equalsIgnoreCase(args[i+1])) {
						_modemType = "MMIUSMS";
					}
				}
//////////////////// GPRS FORCE RECOLLECT
//				else if ( nextArg.startsWith("-mdsId")) {
//					mdsId = args[i + 1];
//					logger.debug("***{}***", mdsId);
//					if (mdsId.trim().length() > 0 &&
//							(!"${mdsId}".equals(args[i + 1]))){
//						String ids = args[i + 1];
//						_meters = ids.split(",");
//					}
//				}
//				else if ( nextArg.startsWith("-fromDate")) {
//					if ( args[i + 1].trim().length() > 0 &&
//							!"${fromDate}".equals(args[i + 1])){
//						_fromDate = args[i + 1];
//					}
//				}
//				else if ( nextArg.startsWith("-toDate")) {
//					if ( args[i + 1].trim().length() > 0 && 
//							!"${fromDate}".equals(args[i + 1])){
//						_toDate = args[i + 1];
//					}
//				}
//////////////////GPRS FORCE RECOLLECT
			}
			for ( int i = 0; i < _subGigaResult.length; i++) {
				_subGigaResult[i] = new AtomicInteger(0);
			}
			for ( int i = 0; i < _mmiuGprsResult.length; i++) {
				_mmiuGprsResult[i] = new AtomicInteger(0);
			}
			for ( int i = 0; i < _mmiuSmsResult.length; i++) {
				_mmiuSmsResult[i] = new AtomicInteger(0);
			}
			loadProperties();
			
			_lastLinkTime = null;
			
			if ( strMaxThreadSubGiga != null) {
				_maxSubgigaThreadWorker = Integer.parseInt(strMaxThreadSubGiga);
			}
			if ( strMaxThreadMmiuGprs != null) {
				_maxMmiuGprsThreadWorker = Integer.parseInt(strMaxThreadMmiuGprs);
			}
			if ( smsJoinMin != null) {
				_smsJoinMin = Integer.parseInt(smsJoinMin);
			}
			logger.info(" Parameter Settings : deviceType[{}] DSO[{}] Priority[{}] modemType[{}] maxSubgigaThreadWorker[{}] maxMmiuGprsThreadWorker[{}] smsJoinMin[{}] maxReadRecordNum[{}] lastLinkTime[{}] timeout(min)[{}]",
					_deviceType.name(), _dso,_modemType, _priority,
					_maxSubgigaThreadWorker, _maxMmiuGprsThreadWorker, _smsJoinMin, _maxReadRecordNum, _lastLinkTime, _timeout );
			logger.debug("****mdsId[{}] size[{}], fromDate[{}], toDate[{}]", mdsId, _meters != null ? _meters.length : 0, _fromDate, _toDate);
			final ExecutorService executor = Executors.newCachedThreadPool();
			List<Future<Long>> list = new ArrayList<Future<Long>>();
			Future<Long> future = null;
			if ( _modemType.equals("ALL") ||  _modemType.equals("SubGiga") ) {
				future = (Future<Long>) executor.submit(new SubGigaRecollectStartThread2(_dso, null, _priority ));
				list.add(future);
			}
			if ( _modemType.equals("ALL") ||  _modemType.equals("MMIUGPRS") ) {
				future = (Future<Long>) executor.submit(new MmiuGprsRecollectStartThread2(_dso, null, _priority));
				list.add(future);
			}
			if ( _modemType.equals("ALL") ||  _modemType.equals("MMIUSMS") ) {
				future = (Future<Long>) executor.submit(new MmiuSmsRecollectStartThread2(_dso, null, _priority));
				list.add(future);
			}
			executor.shutdown();
			executor.awaitTermination(_timeout, TimeUnit.MINUTES);
			for (Future<Long> future2 : list) {
				future2.get();
			}
		}
		catch (Exception e) {
			logger.error(e.getMessage(), e);

		}

	}


	// DeviceType == METER
	// MdemType.SubGiga ||
	//  ( ModemType.MMIU && (  Protocol.GPRS or  Protocol.IP ))
	private void onDemandMeterBypass(String mcuId, Map<String,String> entry)  throws Exception {
		logger.info("MCU[{}] meterId[{}] modemId[{}] fromDate[{}] toDate[{}]",
				mcuId, entry.get("meterId"), entry.get("modemId"), entry.get("fromDate"), entry.get("toDate"));

		Class clazz = null;
		try {
			Meter meter = meterDao.get(entry.get("meterId"));
			clazz = Class.forName(meter.getModel().getDeviceConfig().getSaverName());
		}
		catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw e;
		}
		AbstractMDSaver saver = (AbstractMDSaver)DataUtil.getBean(clazz);

		MeterData md = saver.onDemandMeterBypass(mcuId,entry.get("meterId"), entry.get("modemId"),"", entry.get("fromDate"), entry.get("toDate"));
		//Thread.sleep(1000); // for debug
		if (mcuId == null ) {
			_mmiuGprsResult[successIndex].getAndIncrement();
		}
		else {
			_subGigaResult[successIndex].getAndIncrement();
		}
		logger.info("RESULT[{}] ID[{}] MCU[{}] METERID[{}] DSO[{}] fromDate[{}] toDate[{}] TOTAL[S:{},F:{},NE:{},AR:{},TO:{}] Type[{}] Priority[{}]",
				"Success",
				entry.get("id"),
				mcuId,
				entry.get("meterId"),
				entry.get("dso"),
				entry.get("fromDate"),
				entry.get("toDate"),
				mcuId == null ? _mmiuGprsResult[successIndex].get()        : _subGigaResult[successIndex].get(),
				mcuId == null ? _mmiuGprsResult[failIndex].get()           : _subGigaResult[failIndex].get(),
				mcuId == null ? _mmiuGprsResult[notExecuteIndex].get()     : _subGigaResult[notExecuteIndex].get(),
				mcuId == null ? _mmiuGprsResult[alreadySendedIndex].get()  : _subGigaResult[alreadySendedIndex].get(),
				mcuId == null ? _mmiuGprsResult[timeOverIndex].get()  : _subGigaResult[timeOverIndex].get(),
				entry.get("rec_type"),
				entry.get("rec_prio"));
		entry.put("completed",  getCurrentUtcTime());
		entry.put("status", RecollecStatus.SUCCESS.name());
	}
	
	// DeviceType == MODEM 
	// ModemType.SubGiga || 
	private void cmdDmdNiGetRomRead(String mcuId, Map<String,String> entry)  throws Exception {
		logger.info("MCU[{}] meterId[{}] modemId[{}]  fwVer[{}]  fromDate[{}] toDate[{}]",
				mcuId, entry.get("meterId"), entry.get("modemId"), entry.get("fwVer"), entry.get("fromDate"), entry.get("toDate"));

		CommandGW commandGw = DataUtil.getBean(CommandGW.class);

		int pollType = 2;
		if (entry.get("fwVer").compareTo("1.2") >= 0) {
			pollType = 3;
		}
		MeterData emd = commandGw.cmdDmdNiGetRomRead(mcuId, entry.get("meterId"), entry.get("modemId"), entry.get("fromDate"), entry.get("toDate"), pollType);
		_subGigaResult[successIndex].getAndIncrement();
		logger.info("RESULT[{}] ID[{}] MCU[{}] METERID[{}] DSO[{}] fromDate[{}] toDate[{}] TOTAL[S:{},F:{},NE:{},AR:{},TO:{}] Type[{}] Priority[{}]",
				"Success",
				entry.get("id"),
				mcuId,
				entry.get("meterId"),
				entry.get("dso"),
				entry.get("fromDate"),
				entry.get("toDate"),
				_subGigaResult[successIndex].get(),_subGigaResult[failIndex].get(),
				_subGigaResult[notExecuteIndex].get(),_subGigaResult[alreadySendedIndex].get(),_subGigaResult[timeOverIndex].get(),
				entry.get("rec_type"),
				entry.get("rec_prio"));
		entry.put("completed",  getCurrentUtcTime());
		entry.put("status", RecollecStatus.SUCCESS.name());
	}
	
	// DeviceType == MODEM 
	//  ModemType.MMIU && (  Protocol.GPRS or  Protocol.IP )
	private void cmdGetROMRead(String mcuId, Map<String,String> entry)  throws Exception {
		logger.info("meterId[{}] modemId[{}]  fwVer[{}]  fromDate[{}] toDate[{}]",
				entry.get("meterId"), entry.get("modemId"), entry.get("fwVer"), entry.get("fromDate"), entry.get("toDate"));

		CommandGW commandGw = DataUtil.getBean(CommandGW.class);

		MeterData emd = commandGw.cmdGetROMRead(mcuId, entry.get("meterId"), entry.get("modemId"), "", entry.get("fromDate"), entry.get("toDate"));
		_mmiuGprsResult[successIndex].getAndIncrement();
		logger.info("RESULT[{}] ID[{}] METERID[{}] DSO[{}] fromDate[{}] toDate[{}] TOTAL[S:{},F:{},NE:{},AR:{},TO:{}] Type[{}] Priority[{}]",
				"Success",
				entry.get("id"),
				entry.get("meterId"),
				entry.get("dso"),
				entry.get("fromDate"),
				entry.get("toDate"),
				_mmiuGprsResult[successIndex].get(),
				_mmiuGprsResult[failIndex].get(),
				_mmiuGprsResult[notExecuteIndex].get(),
				_mmiuGprsResult[alreadySendedIndex].get(),
				_mmiuGprsResult[timeOverIndex].get(),
				entry.get("rec_type"),
				entry.get("rec_prio"));
		entry.put("completed",  getCurrentUtcTime());
		entry.put("status", RecollecStatus.SUCCESS.name());
	}
	
	// DeviceType == MODEM or METER
	// ModemType.MMIU  &&  Protocol.SMS
	private void executeUseAsyncMode(CommonConstants.DeviceType deviceType, ArrayList<ArrayList<HashMap<String,Object>>> targetList) throws Exception {

		String commandName = null;
		String commandCode = SMSConstants.COMMAND_TYPE.NI.getTypeCode();

		List<Map<String, Object>> asyncTargetList = new ArrayList<>();;

		for (ArrayList<HashMap<String,Object>> meterEntries :targetList) {
			@SuppressWarnings("unchecked")
			Map<String,Object> entry = meterEntries.get(0);

			HashMap<String,Object> targetMap = (HashMap<String, Object>) ((HashMap<String, Object>) entry).clone();
			if ( meterEntries.size() > 1 ) {
				Map<String,Object> lastEntry = meterEntries.get(meterEntries.size()-1);
				targetMap.put("toDate", meterEntries.get(meterEntries.size()-1).get("toDate"));
			}
			
			
			String meterId =  (String) targetMap.get("mdsId");
//			String deviceSerial = (targetMap.get("deviceSerial") != null ? (String) targetMap.get("deviceSerial") : null);
//			String modemType = (targetMap.get("modemType") != null ? (String) targetMap.get("modemType") : null);
//			String protocolType = (targetMap.get("protocolType") != null ? (String) targetMap.get("protocolType") : null);

			int modemPort = 0;
			if ( entry.get("modemPort") != null ) {
			    if (  entry.get("modemPort") instanceof BigDecimal  ) {
			        modemPort = ((BigDecimal)entry.get("modemPort")).intValue();
			    }
			    else {
			        modemPort =  (Integer) entry.get("modemPort");
			    }
			}
			String  fromDate = (String)entry.get("fromDate");
			String	toDate = (String)entry.get("toDate");

			Map<String, String> paramMap = null;
			if ( deviceType == CommonConstants.DeviceType.Meter){
				commandName = "cmdMeterParamGet";

				Meter meter = meterDao.get(meterId);
				if ( modemPort > 5){
					logger.error("[{}] ModemPort: {} is not Support", meterId, modemPort);
					throw new Exception("ModemPort:" + modemPort + " is not Support");
				}

				paramMap = getOnDemandMeterBypassMBBParam(meterId, modemPort, fromDate, toDate);
				paramMap.put("CommandCode", commandCode);
			}
			else if ( deviceType == CommonConstants.DeviceType.Modem){
				commandName = "cmdGetROMRead";

				paramMap = new HashMap<String,String>();
				paramMap.put("CommandCode", commandCode);
				paramMap.put("fromDate", fromDate);
				paramMap.put("toDate", toDate);
				paramMap.put("meterId", meterId);
			}

			targetMap.put("asycParams", paramMap);
			targetMap.put("targetRecords", meterEntries);
			asyncTargetList.add(targetMap);

		}
		
		saveAsyncCommandByUseAsyncOption(asyncTargetList, commandName);	
	}
	
	private Map<String, String> getOnDemandMeterBypassMBBParam(String mdsId, int modemPort, String fromDate, String toDate) throws Exception{
		Map<String,String> paramMap = new HashMap<String,String>();
		if (modemPort==0) {
			logger.debug("cmdGetLoadProfile ["+ mdsId + "][" + modemPort +  "]["  +  fromDate + "][" +toDate +"]");

			String obisCode = DataUtil.convertObis(OBIS.ENERGY_LOAD_PROFILE.getCode());
			int classId = DLMS_CLASS.PROFILE_GENERIC.getClazz();
			int attrId = DLMS_CLASS_ATTR.PROFILE_GENERIC_ATTR02.getAttr();

			Map<String,String> valueMap = Util.getParamValueByRange(fromDate,toDate);
			String value = CommandGW.meterParamMapToJSON(valueMap);

			logger.debug("[{}] ObisCode=> {}, classID => {}, attributeId => {}", mdsId, obisCode,classId,attrId);
			//paramGet
			paramMap.put("paramGet", obisCode+"|"+classId+"|"+attrId+"|null|null|"+value);
		}
		else {
	    	logger.debug("cmdGetLoadProfile ["+ mdsId + "][" + modemPort +  "]["  +  fromDate + "][" +toDate +"]");

	    	String obisCode = DataUtil.convertObis(OBIS.MBUSMASTER_LOAD_PROFILE.getCode());
			int classId = DLMS_CLASS.PROFILE_GENERIC.getClazz();
			int attrId = DLMS_CLASS_ATTR.PROFILE_GENERIC_ATTR02.getAttr();

			Map<String,String> valueMap = Util.getParamValueByRange(fromDate,toDate);
			String value = CommandGW.meterParamMapToJSON(valueMap);

			logger.debug("[{}] ObisCode=> {}, classID => {}, attributeId => ", mdsId, obisCode, classId, attrId);
			//paramGet
			paramMap.put("paramGet", obisCode+"|"+classId+"|"+attrId+"|null|null|"+value);
		}
		paramMap.put("option", "ondemandmbb");

		return paramMap;
	}
	
	@SuppressWarnings("unchecked")
	private void  saveAsyncCommandByUseAsyncOption(List<Map<String, Object>> targetList, String commandName){
		TransactionStatus txStatus = null;
		int successCnt = 0;
		try {
			String currentTime = TimeUtil.getCurrentTime();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmssSSS");
			int count = 0;

			logger.info("============== Async Command save start ======================");
			for(Map<String, Object> target : targetList) {
				try {
					txStatus = txmanager.getTransaction(null);
	
					Calendar calendar = Calendar.getInstance();
					Thread.sleep(10);
					String sequence = dateFormat.format(calendar.getTime());
	
					AsyncCommandLogDao asyncCommandLogDao = DataUtil.getBean(AsyncCommandLogDao.class);
					AsyncCommandLog asyncCommandLog = new AsyncCommandLog();
					asyncCommandLog.setTrId(Long.parseLong(sequence));
					asyncCommandLog.setMcuId((String)target.get("deviceSerial"));
					asyncCommandLog.setDeviceType(ModemIFType.MBB.name());
					asyncCommandLog.setDeviceId((String)target.get("deviceSerial"));
					asyncCommandLog.setCommand(commandName);
					asyncCommandLog.setTrOption(TR_OPTION.ASYNC_OPT_RETURN_DATA_SAVE.getCode());
					asyncCommandLog.setState(TR_STATE.Waiting.getCode());
					asyncCommandLog.setOperator(OperatorType.OPERATOR.name());
					asyncCommandLog.setCreateTime(currentTime);
					asyncCommandLog.setRequestTime(currentTime);
					asyncCommandLog.setLastTime(null);
					asyncCommandLogDao.add(asyncCommandLog);
					logger.debug("asyncCommandLog ==> " + asyncCommandLog.toJSONString());
	
					int num = 0;
					if (target.get("asycParams") != null && ((Map<String, String>)target.get("asycParams")).size() > 0) {
						AsyncCommandParamDao asyncCommandParamDao = DataUtil.getBean(AsyncCommandParamDao.class);
						Map<String, String> asycParams = (Map<String, String>) target.get("asycParams");
	
						Iterator<String> iter = asycParams.keySet().iterator();
						while (iter.hasNext()) {
							String key = iter.next();
	
							AsyncCommandParam asyncCommandParam = new AsyncCommandParam();
							asyncCommandParam.setMcuId(asyncCommandLog.getMcuId());
							asyncCommandParam.setNum(num++);
							asyncCommandParam.setParamType(key);
							asyncCommandParam.setParamValue(asycParams.get(key));
							asyncCommandParam.setTrId(asyncCommandLog.getTrId());
							if(key.equals("CommandCode")) {
								asyncCommandParam.setTrType("CommandCode");
							}else {
								asyncCommandParam.setTrType("CMD");
							}
	
							asyncCommandParamDao.add(asyncCommandParam);
							logger.debug("asyncCommandParam ==> " + asyncCommandParam.toJSONString());
						}
					}
	
					
					txmanager.commit(txStatus);
					
					logger.info("AsyncCommand Save TID[{}] METERID[{}] DSO[{}] fromDate[{}] toDate[{}] TOTAL[S:{},F:{},J:{}] Type[{}] Priority[{}]",
							sequence,
							(String)target.get("mdsId"),
							(String)target.get("dso"),
							(String)target.get("fromDate"),
							(String)target.get("toDate"),
							_mmiuSmsResult[asyncRecNumIndex].get(),
							_mmiuSmsResult[failIndex].get(),
							_mmiuSmsResult[joindRecNumIndex].get(),
							(String)target.get("rec_type"),
							(String)target.get("rec_prio"));
					
					ArrayList<HashMap<String,Object>> entryList  = (ArrayList<HashMap<String,Object>>) target.get("targetRecords");
					
					for (  HashMap<String,Object> entry : entryList ) {
						_mmiuSmsResult[asyncRecNumIndex].getAndIncrement();
						entry.put("started", getCurrentUtcTime());
						entry.put("completed", getCurrentUtcTime());
						entry.put("status", RecollecStatus.SUCCESS.name());
						 updateRecollectTable(entry,(String)entry.get("id"),
								 (String)entry.get("started"), (String)entry.get("completed"), (String)entry.get("status"),"SMS");
					}
					successCnt++;

				}catch (Exception ex) { 
					logger.error("Save AsyncLog Error - " + ex.getMessage(), ex);

					if(txStatus != null && !txStatus.isCompleted()) {
						txmanager.rollback(txStatus);
					}
					ArrayList<HashMap<String,Object>> entryList  = (ArrayList<HashMap<String,Object>>) target.get("targetRecords");
					for (  HashMap<String,Object> entry : entryList ) {
						_mmiuSmsResult[failIndex].getAndIncrement();
						entry.put("started", getCurrentUtcTime());
						entry.put("completed", getCurrentUtcTime());
						entry.put("status", RecollecStatus.FAILED.name());
						 updateRecollectTable(entry,(String)entry.get("id"),
								 (String)entry.get("started"), (String)entry.get("completed"), (String)entry.get("status"),"SMS");
					}
				}
			}
		}
		catch (Exception e ){
		}
		logger.info("============== Async Command save finished ======================");

	}


	private String getLastLinkTimeLimit(int beforeHour) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		
		calendar.add(Calendar.HOUR, (beforeHour*-1));	

		Date limit = calendar.getTime();
		return DateTimeUtil.getDateString(limit);
	}
	
	/**
	 * SubGiga Meter Recollect Start Thread
	 *
	 */
	/* Old Version
	class SubGigaRecollectStartThread extends Thread {
		private String dso = "";
		private String lastLinkTime = "";
		
		SubGigaRecollectStartThread(String dso, String lastLinkTime){
			if ( dso != null )
				this.dso = dso;
			if ( lastLinkTime != null)
				this.lastLinkTime =  lastLinkTime;
		}
		
		public void run(){
			logger.info("*****     Start Meter SubGiga Recollect     *****");
			int cnt = 0;

			try {	
				List<String> mcuList  = getMcuListFromDWHView(dso, lastLinkTime);
				
				ExecutorService pool1 = Executors.newFixedThreadPool(_maxSubgigaThreadWorker);
				
				SubGigaRecollectThread threads1[] = new SubGigaRecollectThread[mcuList.size()];
				int i = 0;

				for (String mcu : mcuList) {
					logger.info(cnt++ + ": MCU[" + mcu + "] Recollect Start ");

					threads1[i] = new SubGigaRecollectThread(mcu);
					
					pool1.execute(threads1[i]);
					i++;
				}

				logger.info("ExecutorService for mcu shutdown.");
				pool1.shutdown();
				logger.info("ExecutorService for mcu awaitTermination. [" + _timeout + "]min");
				pool1.awaitTermination(_timeout, TimeUnit.MINUTES);

				cnt = 0;
				i = 0;   
			}catch (Exception e){
				logger.error(e.getMessage(),e);
			}
		}
	}

	class SubGigaRecollectThread extends Thread {
		String mcu;

		SubGigaRecollectThread(String mcu)  {
			this.mcu = mcu;
		}
		public void run() {
			long threadId = Thread.currentThread().getId();

			
			List<Map<String,String>>recList = getRecollectsByMCU(mcu, _dso, _lastLinkTime);
			logger.info("ThreadID[{}] MCU[{}] Meter(SubGiga) collect[{}] Metering thread start.",
					threadId, mcu, recList.size());
			try {
				boolean errorExit = false;
				HashMap<String, String> errMeters = new HashMap<String, String>();
				for ( Map<String,String> entry : recList) {
						
					if ( errMeters.get(entry.get("modemId")) != null ) {
						logger.error ("RESULT[Fail] MCU[{}] METERID [{}] fromDate [{}] toDate[{}]",
								mcu,
								entry.get("meterId"),
								entry.get("fromDate"),
								entry.get("toDate"));
						continue;
					}
					try {
						if ( _deviceType == CommonConstants.DeviceType.Meter ) {
							onDemandMeterBypass(mcu,entry);
						}
						else {
							cmdDmdNiGetRomRead(mcu,entry);
						}
					}
					catch (Exception e) {
						errMeters.put(entry.get("modemId"), "");
						logger.error ("RESULT[Fail] MCU[{}] METERID [{}] fromDate [{}] toDate[{}]",
								mcu,
								entry.get("meterId"),
								entry.get("fromDate"),
								entry.get("toDate"));
					}
				}
				
			} catch (Exception ex) {
				logger.info("ThreadID[{}] Mcu Recollect Metering thread end. MCU[{}] is  failed.", 
						threadId,mcu);	
			} 
			logger.info("ThreadID[{}] MCU[{}] Meter(SubGiga) collect[{}] Metering thread end.",
					threadId, mcu, recList.size());
		}
	}
	*/
	/**
	 * MMIU GPRS Meter Recollect Start Thread
	 *
	 */
	/* Old Version
	class MmiuGprsRecollectStartThread extends Thread {
		private String dso = null;
		private String lastLinkTime  = null;
		MmiuGprsRecollectStartThread(String dso, String lastLinkTime){
			this.dso = dso;
			this.lastLinkTime = lastLinkTime;
		}

		public void run(){
			logger.info("*****     Start MMIU GPRS Recollect     *****");
			int cnt = 0;
			Date startDate = new Date();
			long startTime = startDate.getTime();
			
			try {
				HashMap<String, Object> params = new HashMap<String, Object>();
				if ( dso != null ) {
					params.put("dso", dso);
				}
				if ( lastLinkTime != null ) {
					params.put("lastLinkTime", lastLinkTime);
				}
				params.put("modemType", "MMIU");
				params.put("protocolType", "GPRS");
				params.put("orderBy", "order by REC.RANGE_START_DATETIME, REC.METERID ");

				List<BigDecimal>  ret =  getRecollectList(params, true );
				
//				if ( _gprsforce > 0  ) { //  GPRS FORCE RECOLLECT
//					 ret =  getRecollectListForDevTest(params, true );
//				}
				if ( ret.size() !=  1 ) {
					logger.error("Can't get Record Count of MMIU GPRS Meter !");
					throw new Exception("Can't get Record Count of MMIU GPRS Meter !");
				}
				int allRecNum = ret.get(0).intValue();
				logger.info(" MMIU GPRS Target Record Count = {} ", allRecNum);
				
				//_maxReadRecordNum = 5; // for Debug
				//_maxMmiuGprsThreadWorker = 2; // for Debug
				ExecutorService executorService = Executors.newFixedThreadPool(_maxMmiuGprsThreadWorker);

				CompletionService<Integer> completionService
				= new ExecutorCompletionService<Integer>(executorService); 

				int readNum = (allRecNum / _maxReadRecordNum ) + 1 ;
				int finNum = 0;
				int startNum = 0;
				boolean finish = false;
				for ( int readCnt = 0; readCnt < readNum; readCnt ++ ) {
					if ( finish )
						break;
					params.put("firstResult", readCnt *_maxReadRecordNum );
					params.put("maxResults", _maxReadRecordNum );
					List<Object[]>  targetList =  getRecollectList(params, false );
//					if ( _gprsforce > 0 ) {// GPRS FORCE RECOLLECT
//						targetList =  getListForDevTest(); 
//					}
					
					int targetNum = targetList.size();

					for (int i = 0; i < targetNum; i++ ) {
						completionService.submit(new MmiuGprsRecollectThread(_deviceType, targetList.get(i)));
						startNum ++;
					}
					// Wait until free thread
					while ( finNum <= startNum  - _maxMmiuGprsThreadWorker)  { 
						Date now = new Date();
						long nowTime = now.getTime();
						if ( (nowTime - startTime) / (1000 * 60) >  _timeout ) {
							finish = true;
							break;
						}
						Future<Integer> future = completionService.take(); 
						Integer result = future.get(); 
						finNum += 1;
					}
				}
				executorService.shutdown(); 
				// Wait All job are finished.
				while (  !finish &&  finNum < startNum )  {
					Date now = new Date();
					long nowTime = now.getTime();
					if ( (nowTime - startTime) / (1000 * 60) >  _timeout ) {
						finish = true;
						break;
					}
					Future<Integer> future = completionService.take(); 
					Integer result = future.get(); 
					finNum += 1;
				}
				cnt = 0;   
			}catch (Exception e){
				logger.error(e.getMessage(),e);
			}
		}
	}
	class MmiuGprsRecollectThread implements Callable<Integer> {

		Map<String,String> map = null;
		CommonConstants.DeviceType deviceType;

		public MmiuGprsRecollectThread(CommonConstants.DeviceType deviceType, Object[] arr) { 
			this.map = getEntryMap(arr);
			this.deviceType = deviceType;
		}

		public Integer call() throws Exception {
			Integer ret = 0;
			if (map.get("fromDate") == null || map.get("toDate") == null)
				return ret;

			try { 
				if ( deviceType == DeviceType.Meter ) {
					onDemandMeterBypass(null, map);
				}
				else {
					cmdGetROMRead(null, map);
				} 
			}catch ( Exception e ){
				ret = 1;
				logger.error ("RESULT[{}] METERID [{}] fromDate [{}] toDate[{}]",
						"Fail",
						map.get("meterId"),
						map.get("fromDate"),
						map.get("toDate"));
			} finally { 

			} 
			//Thread.sleep(60*1000); // for Debug
			return ret; 
		}
	}
*/
	/**
	 * MMIU SMS Meter Recollect Start Thread
	 *
	 */
	/* Old Version 
	class MmiuSmsRecollectStartThread extends Thread {
		private String dso = null;
		private String lastLinkTime  = null;
		MmiuSmsRecollectStartThread(String dso, String lastLinkTime){
			this.dso = dso;
			this.lastLinkTime = lastLinkTime;
		}

		public void run(){
			logger.info("*****     Start MMIU SMS Recollect     *****");

			try {
				HashMap<String, Object> params = new HashMap<String, Object>();
				if ( dso != null ) {
					params.put("dso", dso);
				}
				if ( lastLinkTime != null ) {
					params.put("lastLinkTime", lastLinkTime);
				}
				params.put("modemType", "MMIU");
				params.put("protocolType", "SMS");
				params.put("orderBy", "order by REC.METERID, REC.RANGE_START_DATETIME");

				List<BigDecimal>  ret =  getRecollectList(params, true );
				if ( ret.size() !=  1 ) {
					logger.error("Can't get Record Count of MMIU SMS Meter !");
					throw new Exception("Can't get Record Count of MMIU GPRS Meter !");
				}

				int allRecNum = ret.get(0).intValue();
				logger.info(" MMIU SMS Target Record Count = {} ", allRecNum);
				
				//_maxReadRecordNum = 10; // for debug;
				int readNum = (allRecNum / _maxReadRecordNum ) + 1 ;
				Map<String,Object> preLastMeter = null;
				for ( int readCnt = 0; readCnt < readNum; readCnt ++ ) {
					params.put("firstResult", readCnt *_maxReadRecordNum );
					params.put("maxResults", _maxReadRecordNum );
					List<Object[]>  targetList =  getRecollectList(params, false );
					List<Map<String,Object>> meterEntryList = getMeterEntryMap(targetList);
					int targetNum = meterEntryList.size();
					
					if ( preLastMeter != null ) {
						Map<String,Object> firstMeter = meterEntryList.get(0);
						String fistMeterId = (String)firstMeter.get("mdsId");
						String preLastMeterId = (String)preLastMeter.get("mdsId");
						if ( preLastMeterId.equals(fistMeterId)){
							if ( checkRecollectToDate(
									DateTimeUtil.getDateFromYYYYMMDDHHMMSS((String)preLastMeter.get("fromDate")),
									 DateTimeUtil.getDateFromYYYYMMDDHHMMSS((String)firstMeter.get("toDate")))){
								firstMeter.put("fromDate", preLastMeter.get("fromDate"));
							}
							else {
								firstMeter.put("fromDate", preLastMeter.get("fromDate"));
								firstMeter.put("toDate", preLastMeter.get("toDate"));
							}
						}
						else {
							meterEntryList.add(0,preLastMeter);
						}
					}
					if ( readCnt + 1 < readNum ) {
						preLastMeter = meterEntryList.get(targetNum - 1);
						meterEntryList.remove(targetNum - 1);
					}
					executeUseAsyncMode(_deviceType,meterEntryList);
				}
			}catch (Exception e){
				logger.error(e.getMessage(),e);
			}
		}
	}
*/
	
	private void loadProperties(){
		Properties prop = new Properties();
		try{
			prop.load(getClass().getClassLoader().getResourceAsStream("config/RecollectMeteringSoriaDWH.properties"));
		}catch(Exception e){
			logger.error("Can't not read property file. -" + e,e);
		}

		_maxSubgigaThreadWorker = Integer.parseInt(prop.getProperty("recollect.subgiga.maxworker", "20"));
		_maxMmiuGprsThreadWorker = Integer.parseInt(prop.getProperty("recollect.mmiu.gprs.maxworker", "20"));
		
		_maxReadRecordNum = Integer.parseInt(prop.getProperty("recollect.record.maxread", "2000"));
		_smsJoinMin =  Integer.parseInt(prop.getProperty("recollect.smsJoin.min", "1440")); // 60*24
		_beforeHour = Integer.parseInt(prop.getProperty("recollect.lastLinkTime.beforeHour", "24"));
		_timeout = Integer.parseInt(prop.getProperty("recollect.ondemand.timeout", "1380")); // 60 * 23 min
		_dwhView = prop.getProperty("recollect.dwhViewName", "DWH.MDS$RECOLLECT@PDBDWH.VALIDER.NO");
		_usePriority = Boolean.parseBoolean( prop.getProperty("recollect.use.priority", "false"));
		
		//Calendar calendar = Calendar.getInstance();
		//calendar.setTime(new Date());
		//calendar.add(Calendar.HOUR, (beforeHour*-1));
		//Date lastLinkDate = calendar.getTime();
		//_lastLinkTime = DateTimeUtil.getDateString(lastLinkDate); // Delete SP-1055
		
		/// GPRS FORCE RECOLLECT
		//_gprsforce = Integer.parseInt(prop.getProperty("recollect.gprs.force", "0"));
		
	}
	/**
	 * SubGiga Meter Recollect With Priority Start Thread
	 *
	 */
	class SubGigaRecollectStartThread2 extends Thread {
		private String dsos = null;
		private String[] dsoList = null;
		private String lastLinkTime = "";
		private String priorities = null;
		private String[] priorityList = null;
		
		SubGigaRecollectStartThread2(String dso, String lastLinkTime, String priority){
			this.dsos = dso;
			if ( dso != null )
				this.dsoList = dso.split(",");
			if ( lastLinkTime != null)
				this.lastLinkTime =  lastLinkTime;
			if ( priority != null) {
				this.priorities = priority;
				this.priorityList= priority.split(",");
			}
		}
		
		public void run(){
			String dsoNames = dsos == null ? "" : dsos.replaceAll(",", " ");
			String prio= priorities == null ? "" : priorities.replaceAll(",", " ");
			
			logger.info("*****     Start SubGiga Recollect DSO[{}] Priority[{}]   *****", dsoNames, prio);
			int cnt = 0;

			try {
				
				if (priorityList == null  ) {
					//HashMap<String,Object> params = new HashMap<String,Object>();
					//params.put("modemType", "SubGiga");
					//params.put("lastLinkTime", _lastLinkTime);
					//params.put("dsoList", dsoList);
					//priorityList = getPriorityList(params);
					priorityList = "1,2,3,4,5".split(",");
				}
				for ( String priority : priorityList ) {
					logger.info("-----     Start SubGiga Recollect DSO[{}] Priority[{}]   -----", dsoNames, priority);
					List<String> mcuList  = getMcuListFromDWHView(dsoList, null, priority);

					ExecutorService pool1 = Executors.newFixedThreadPool(_maxSubgigaThreadWorker);

					SubGigaRecollectThread threads1[] = new SubGigaRecollectThread[mcuList.size()];
					int i = 0;
					cnt = 0;
					for (String mcu : mcuList) {
						logger.info(cnt++ + ": MCU[" + mcu + "] Recollect Start ");

						threads1[i] = new SubGigaRecollectThread(mcu, dsoList,priority);

						pool1.execute(threads1[i]);
						i++;
					}

					logger.info("ExecutorService for mcu shutdown.");
					pool1.shutdown();
					logger.info("ExecutorService for mcu awaitTermination. [" + _timeout + "]min");
					pool1.awaitTermination(_timeout, TimeUnit.MINUTES);
					logger.info("-----     Finish SubGiga Recollect DSO[{}] Priority[{}]  RESULT[{},{},{},{},{}] -----",
							dsoNames, priority,
							subGigaAllRec.get() + _subGigaResult[alreadySendedIndex].get(),
							_subGigaResult[successIndex].get(),
							_subGigaResult[failIndex].get(),
							_subGigaResult[notExecuteIndex].get(),
							_subGigaResult[alreadySendedIndex].get());
				}
			}catch (Exception e){
				logger.error(e.getMessage(),e);
			}
			logger.info("*****     Finish SubGiga Recollect  Dso[{}]  RESULT[ALL:{},Success{},Failed:{},NotExecuted:{},AlreadyReceived:{}]  *****",
					dsoNames,
					subGigaAllRec.get()  + _subGigaResult[alreadySendedIndex].get(),
					_subGigaResult[successIndex].get(),
					_subGigaResult[failIndex].get(),
					_subGigaResult[notExecuteIndex].get(),
					_subGigaResult[alreadySendedIndex].get());

		}
	}

	class SubGigaRecollectThread extends Thread {
		String mcu = null;
		String[] dsoList = null;
		String priority = null;
		
		SubGigaRecollectThread(String mcu, String[] dsoList, String priority )  {
			this.mcu = mcu;
			this.dsoList = dsoList;
			this.priority = priority;
		}
		public void run() {
			long threadId = Thread.currentThread().getId();

			
			List<Map<String,String>>recList = getRecollectsByMCU(mcu, dsoList, _lastLinkTime, priority);
			logger.info("ThreadID[{}] MCU[{}] Meter(SubGiga) Priority[{}] collect[{}] Metering thread start.",
					threadId, mcu, priority, recList.size());
			subGigaAllRec.addAndGet(recList.size());
			
			try {
				boolean errorExit = false;
				HashMap<String, String> errMeters = new HashMap<String, String>();
				for ( Map<String,String> entry : recList) {
					
					// Check current time < RECOLLECT_VALID_END
					if ( !checkExecuteTime(entry.get("recValidEndStr"))) {
						logger.info ("TimeOver RECOLLECT_VALID_END[{}] MCU[{}] METERID[{}] DSO[{}] fromDate[{}] toDate[{}] TOTAL[S:{},F:{},NE:{},AR:{}] Type[{}] Priority[{}]",
								entry.get("recValidEndStr"),
								mcu,
								entry.get("meterId"),
								entry.get("dso"),
								entry.get("fromDate"),
								entry.get("toDate"),
								_subGigaResult[successIndex].get(),_subGigaResult[failIndex].get(),_subGigaResult[notExecuteIndex].get(),_subGigaResult[alreadySendedIndex].get(),
								entry.get("rec_type"),
								entry.get("rec_prio"));
						continue;
					}
					entry.put("started",  getCurrentUtcTime());
					entry.put("status", RecollecStatus.STARTED.name());
					 updateRecollectTable((HashMap<String, ?>) entry,(String)entry.get("id"),
							(String)entry.get("started"), null,(String)entry.get("status"),"SMS");
					 
					boolean execute = false;
					try {
						// check error meters
						if ( errMeters.get(entry.get("modemId")) != null ) {
							entry.put("completed",  getCurrentUtcTime());
							entry.put("status", errMeters.get(entry.get("modemId")));
							throw new Exception("SKIP:LastExecution");
						}

						// Check Modem Last_Link_Time 
						Calendar calendar = Calendar.getInstance();
						calendar.setTime(new Date());
						calendar.add(Calendar.HOUR, (_beforeHour*-1));
						String lastLinkBefore = DateTimeUtil.getDateString(calendar.getTime());
						
						if( lastLinkBefore.compareTo(entry.get("lastLinkTime")) > 0 ) {
							entry.put("completed",  getCurrentUtcTime());
							entry.put("status", RecollecStatus.NO_CONNECT.name());
							throw new Exception("SKIP:LastLinkTime");
						}

						
						if ( _deviceType == CommonConstants.DeviceType.Meter ) {
							execute = true;
							onDemandMeterBypass(mcu,entry);
						}
						else {
							execute = true;
							cmdDmdNiGetRomRead(mcu,entry);
						}
						 updateRecollectTable((HashMap<String, ?>) entry,(String)entry.get("id"),
								 null, (String)entry.get("completed"), (String)entry.get("status"),"SubGiga");
					}
					catch (Exception e) {
						
						if ( execute ) {
							_subGigaResult[failIndex].getAndIncrement();
							if ( errMeters.get(entry.get("modemId")) == null )
									errMeters.put(entry.get("modemId"), RecollecStatus.FAILED.name());
						}
						else { 
							_subGigaResult[notExecuteIndex].getAndIncrement();
							if ( errMeters.get(entry.get("modemId")) == null )
								errMeters.put(entry.get("modemId"), RecollecStatus.NO_CONNECT.name());
						}
						if ( entry.get("completed") == null ) {
							entry.put("status", RecollecStatus.FAILED.name());
							entry.put("completed",  getCurrentUtcTime());
						}
						logger.error ("RESULT[Fail] ID[{}] MCU[{}] METERID [{}] DSO[{}] fromDate [{}] toDate[{}] TOTAL[S:{},F:{},NE:{},AR:{}] Type[{}] Priority[{}] Exception[{}]",
								entry.get("id"),
								mcu,
								entry.get("meterId"),
								entry.get("dso"),
								entry.get("fromDate"),
								entry.get("toDate"),
								_subGigaResult[successIndex].get(),_subGigaResult[failIndex].get(),_subGigaResult[notExecuteIndex].get(),_subGigaResult[alreadySendedIndex].get(),
								entry.get("rec_type"),
								entry.get("rec_prio"),
								e.getMessage());
						 updateRecollectTable((HashMap<String, ?>) entry,(String)entry.get("id"),
								 null, (String)entry.get("completed"), (String)entry.get("status"),"SubGiga");
					}
				}
				
			} catch (Exception ex) {
				logger.info("ThreadID[{}] Mcu Recollect Metering thread end. MCU[{}] is  failed.", 
						threadId,mcu);	
			} 
			logger.info("ThreadID[{}] MCU[{}] Meter(SubGiga) collect[{}] Metering thread end.",
					threadId, mcu, recList.size());
		}
	}
	
	/**
	 * MMIU GPRS Meter Recollect Start Thread With Priority
	 *
	 */
	class MmiuGprsRecollectStartThread2 extends Thread {
		private String dsos = null;
		private String[] dsoList = null;
		private String lastLinkTime  = null;
		private String priorities = null;
		private String[] priorityList = null;
		
		MmiuGprsRecollectStartThread2(String dso, String lastLinkTime, String priority){
			this.dsos = dso;
			if ( dso != null )
				this.dsoList = dso.split(",");
			this.lastLinkTime = lastLinkTime;
			if ( priority != null) {
				this.priorities = priority;
				this.priorityList= priority.split(",");
			}
		}

		public void run(){
			String dsoNames = dsos == null ? "" : dsos.replaceAll(",", " ");
			String prio= priorities == null ? "" : priorities.replaceAll(",", " ");
			
			logger.info("*****     Start MMIU GPRS Recollect DSO[{}] Priority[{}]   *****", dsoNames, prio);

			int cnt = 0;
			Date startDate = new Date();
			long startTime = startDate.getTime();
			int allRecNum = 0;

			try {
				HashMap<String, Object> params = new HashMap<String, Object>();

				if ( dsoList != null) {
					params.put("dsoList", dsoList);
				}
				if ( lastLinkTime != null ) {
					params.put("lastLinkTime", lastLinkTime);
				}
				if ( priorityList != null ) {
					params.put("priorityList", priorityList);
				}
				params.put("modemType", "MMIU");
				//params.put("protocolType", "GPRS");
				params.put("protocolTypeList", "GPRS,IP".split(","));
				
				List<BigDecimal>  ret =  getRecollectList(params, true );

				if ( ret.size() !=  1 ) {
					logger.error("Can't get Record Count of MMIU GPRS Meter !");
					throw new Exception("Can't get Record Count of MMIU GPRS Meter !");
				}

				
				allRecNum = ret.get(0).intValue();
				logger.info(" MMIU GPRS Target Record Count = {} ", allRecNum);

				if ( allRecNum > 0 ) {
					params.put("orderBy", "order by REC.PRIORITY, REC.RANGE_START_DATETIME ");
					//_maxReadRecordNum = 5; // for Debug
					//_maxMmiuGprsThreadWorker = 2; // for Debug
					ExecutorService executorService = Executors.newFixedThreadPool(_maxMmiuGprsThreadWorker);
	
					CompletionService<Integer> completionService
					= new ExecutorCompletionService<Integer>(executorService); 
					
					//int readNum = (allRecNum / _maxReadRecordNum ) + ( (allRecNum % _maxReadRecordNum) == 0  ? 0 : 1 ) ;
					int finNum = 0;
					int startNum = 0;
					boolean finish = false;
					ArrayList<HashMap<String,String>> preLastMeter = null;
					while ( !finish ) {
//						params.put("firstResult", readCnt *_maxReadRecordNum );
						params.put("maxResults", _maxReadRecordNum );
						List<Object[]>  targetRecList =  getRecollectList(params, false );
						if ( targetRecList.size() < _maxReadRecordNum) {
							finish = true;
						}
						
						logger.debug("@@@@@ Read {} Records ", targetRecList.size()  );
						
						List<ArrayList<HashMap<String, String>>> meterEntryList = getMeterEntryMapForGprs(targetRecList);
						
						int targetMeterNum = meterEntryList.size();

//						if ( preLastMeter != null ) {
//							ArrayList<HashMap<String, String>> firstMeter = meterEntryList.get(0);
//							String fistMeterId = (String)(firstMeter.get(0).get("meterId"));
//							String preLastMeterId = (String)(preLastMeter.get(0).get("meterId"));
//							if ( preLastMeterId.equals(fistMeterId)){
//								firstMeter.addAll(0,preLastMeter);
//							}
//							else {
//								meterEntryList.add(0,preLastMeter);
//							}
//						}
						if ( !finish  ) { //Since  meter may be straddling the reading boundary, the last meter will not run
							preLastMeter = meterEntryList.get(meterEntryList.size() - 1);
							meterEntryList.remove(meterEntryList.size() - 1);
						}

						targetMeterNum = meterEntryList.size();
	
						for (int i = 0; i < targetMeterNum; i++ ) {
							completionService.submit(new MmiuGprsRecollectThread2(_deviceType, meterEntryList.get(i)));
							startNum ++;
						}
						//Wait for all threads to finish
						while ( finNum < startNum )  { 
							Date now = new Date();
							long nowTime = now.getTime();
							if ( (nowTime - startTime) / (1000 * 60) >  _timeout ) {
								finish = true;
								break;
							}
							Future<Integer> future = completionService.take(); 
							Integer result = future.get(); 
							finNum += 1;
						}
						logger.debug("@@@@@ finishThreadNum {} , startThreadNum {}", finNum, startNum  );
					}
					
					// Wait All job are finished.
					while (  !finish &&  finNum < startNum )  {
						Date now = new Date();
						long nowTime = now.getTime();
						if ( (nowTime - startTime) / (1000 * 60) >  _timeout ) {
							finish = true;
							break;
						}
						Future<Integer> future = completionService.take(); 
						Integer result = future.get(); 
						finNum += 1;
					}
					executorService.shutdown(); 
					cnt = 0;   
				}
			}catch (Exception e){
				logger.error(e.getMessage(),e);
			}
			logger.info("*****     Finish MMIU GPRS Recollect  Dso[{}] RESULT[ALL:{},Success:{},Failed:{},NotExecuted:{},AlreadyReceived:{},TimeOver:{}]  *****",
					dsoNames,
					allRecNum, 
					_mmiuGprsResult[successIndex].get(),
					_mmiuGprsResult[failIndex].get(),
					_mmiuGprsResult[notExecuteIndex].get(),
					_mmiuGprsResult[alreadySendedIndex].get(),
					_mmiuGprsResult[timeOverIndex].get());
		}


	}
	class MmiuGprsRecollectThread2 implements Callable<Integer> {

		ArrayList<HashMap<String,String>> meterEntry = null;
		CommonConstants.DeviceType deviceType;

		public MmiuGprsRecollectThread2(CommonConstants.DeviceType deviceType, ArrayList<HashMap<String,String>> meterEntry) { 
			this.meterEntry = meterEntry;
			this.deviceType = deviceType;
		}

		public Integer call() throws Exception {
			Integer ret = 0;
			boolean executeSuccess = true;
			for ( HashMap<String,String> map : meterEntry ) {
				boolean execute = false;
				
				if ( !checkExecuteTime(map.get("recValidEndStr"))) {
					logger.error ("TimeOver RECOLLECT_VALID_END[{}] METERID[{}] DSO[{}] fromDate[{}] toDate[{}] Type[{}] Priority[{}] Exception[{}]",
							map.get("recValidEndStr"),
							map.get("meterId"),
							map.get("dso"),
							map.get("fromDate"),
							map.get("toDate"),
							map.get("rec_type"),
							map.get("rec_prio"));
					_mmiuGprsResult[timeOverIndex].getAndIncrement();
					continue;
				}
				
				map.put("started",  getCurrentUtcTime());
				map.put("status", RecollecStatus.STARTED.name() );
				updateRecollectTable(map,(String)map.get("id"),
						 (String)map.get("started"), null, (String)map.get("status"),"GPRS");
				try {

					Calendar calendar = Calendar.getInstance();
					calendar.setTime(new Date());
					calendar.add(Calendar.HOUR, (_beforeHour*-1));
					String lastLinkBefore = DateTimeUtil.getDateString(calendar.getTime());
					
					if( lastLinkBefore.compareTo(map.get("lastLinkTime")) > 0 ) {
						map.put("completed",  getCurrentUtcTime());
						map.put("status", RecollecStatus.NO_CONNECT.name());
						throw new Exception("SKIP:LastLinkTime");
					}
					if ( !executeSuccess ) {
						map.put("completed",  getCurrentUtcTime());
						map.put("status", RecollecStatus.FAILED.name());
						throw new Exception("SKIP:LastExecution");
					}
					if ( deviceType == DeviceType.Meter ) {
						execute=true;
						onDemandMeterBypass(null, map);
					}
					else {
						execute=true;
						cmdGetROMRead(null, map);
					} 
				}catch ( Exception e ){
					if ( execute ) 
						_mmiuGprsResult[failIndex].getAndIncrement();
					else 
						_mmiuGprsResult[notExecuteIndex].getAndIncrement();
					logger.error ("RESULT[{}] ID[{}] METERID[{}] DSO[{}] fromDate[{}] toDate[{}] TOTAL[S:{},F:{},NE:{},AR:{},TO:{}] Type[{}] Priority[{}] Exception[{}]",
							"Fail",
							map.get("id"),
							map.get("meterId"),
							map.get("dso"),
							map.get("fromDate"),
							map.get("toDate"),
							_mmiuGprsResult[successIndex].get(),
							_mmiuGprsResult[failIndex].get(),
							_mmiuGprsResult[notExecuteIndex].get(),
							_mmiuGprsResult[alreadySendedIndex].get(),
							_mmiuGprsResult[timeOverIndex].get(),
							map.get("rec_type"),
							map.get("rec_prio"),
							e.getMessage());
					if ( map.get("completed") == null ) {
						map.put("completed",  getCurrentUtcTime());
						map.put("status", RecollecStatus.FAILED.name());
					}
					executeSuccess = false;
				} finally { 
					updateRecollectTable(map,(String)map.get("id"),
							 null, (String)map.get("completed"), (String)map.get("status"),"GPRS");
					ret ++;
				} 
			}
			//Thread.sleep(60*1000); // for Debug
			logger.debug("finish {} records", ret);
			return ret; 
		}
	}
	
	/*
	 * MMIU SMS Meter Recollect With Priority Start Thread
	 *
	 */
	class MmiuSmsRecollectStartThread2 extends Thread {
		private String dsos = null;
		private String[] dsoList = null;
		private String lastLinkTime  = null;
		private String priorities = null;
		private String[] priorityList = null;
		
		MmiuSmsRecollectStartThread2(String dso, String lastLinkTime, String priority){
			this.dsos = dso;
			this.lastLinkTime = lastLinkTime;
			if ( dso != null )
				this.dsoList = dso.split(",");
			this.lastLinkTime = lastLinkTime;
			if ( priority != null) {
				this.priorities = priority;
				this.priorityList= priority.split(",");
			}
		}

		public void run(){
			String dsoNames = dsos == null ? "" : dsos.replaceAll(",", " ");
			String prio= priorities == null ? "" : priorities.replaceAll(",", " ");
			int allRecNum = 0;
			logger.info("*****     Start MMIU SMS Recollect DSO[{}] Priority[{}]   *****", dsoNames, prio);
			try {
				HashMap<String, Object> params = new HashMap<String, Object>();
				if ( dsoList != null ) {
					params.put("dsoList", dsoList);
				}
				if ( lastLinkTime != null ) {
					params.put("lastLinkTime", lastLinkTime);
				}
				params.put("modemType", "MMIU");
				params.put("protocolType", "SMS");
				params.put("priorityList", priorityList);
			

				List<BigDecimal>  ret =  getRecollectList(params, true );
				if ( ret.size() !=  1 ) {
					logger.error("Can't get Record Count of MMIU SMS Meter !");
					throw new Exception("Can't get Record Count of MMIU GPRS Meter !");
				}

				allRecNum = ret.get(0).intValue();
				logger.info(" MMIU SMS Target Record Count = {} ", allRecNum);
				
				params.put("orderBy", "order by REC.PRIORITY, REC.METERID, REC.RANGE_START_DATETIME");
				//_maxReadRecordNum = 10; // for debug;
				//int readNum = (allRecNum / _maxReadRecordNum ) + (allRecNum % _maxReadRecordNum) == 0  ? 0 : 1 ;
				ArrayList<HashMap<String,Object>> preLastMeter = null;
				boolean finish = false;
				int readCnt = 0;
				while ( !finish ) {
					//params.put("firstResult", readCnt *_maxReadRecordNum );
					params.put("maxResults", _maxReadRecordNum );
					List<Object[]>  targetList =  getRecollectList(params, false );
					
					if ( targetList.size() < _maxReadRecordNum) {
						finish = true;
					}
					 ArrayList<ArrayList<HashMap<String,Object>>> meterEntryList = getMeterEntryMapForSMS(targetList);
					int targetNum = meterEntryList.size();

//					if ( preLastMeter != null ) {
//						ArrayList<HashMap<String,Object>> firstMeter = meterEntryList.get(0);
//						String fistMeterId = (String)firstMeter.get(0).get("mdsId");
//						String preLastMeterId = (String)preLastMeter.get(0).get("mdsId");
//						if ( preLastMeterId.equals(fistMeterId)){
//							Date meterFromDate = DateTimeUtil.getDateFromYYYYMMDDHHMMSS((String)preLastMeter.get(0).get("fromDate"));
//							for ( HashMap<String,Object> rec : firstMeter) {
//								
//								if ( checkRecollectToDate(meterFromDate,  DateTimeUtil.getDateFromYYYYMMDDHHMMSS((String)(rec.get("toDate"))))){
//									preLastMeter.add(rec);
//								}
//							}
//						}
//						meterEntryList.add(0,preLastMeter);
//					}
					if ( !finish ) {
						preLastMeter = meterEntryList.get(targetNum - 1);
						meterEntryList.remove(targetNum - 1);
					}
					
					executeUseAsyncMode(_deviceType,meterEntryList);
				}
				
				
			}catch (Exception e){
				logger.error(e.getMessage(),e);
			}
			logger.info("*****     Finish MMIU SMS Recollect  Dso[{}] RESULT[ALL:{},Success:{},Failed:{},AreadyReceived:{}]  *****",
					dsoNames,
					allRecNum, 
					_mmiuSmsResult[asyncRecNumIndex].get(),
					_mmiuSmsResult[failIndex].get(),
//					_mmiuSmsResult[joindRecNumIndex].get(),
					_mmiuSmsResult[alreadySendedIndex].get());
		}
	}

	private void updateRecollectTable(HashMap<String,?> entry ,
			String id, 
			String recollectStarted, String recollectCompletedDate, String recollectStatus, 
			String protocolType) {
		
		String	meterId = "SMS".equals(protocolType) ? (String)entry.get("mdsId") :  (String)entry.get("meterId") ;
//		String rangeStartDateTime =  "TO_TIMESTAMP_TZ('" + (String)entry.get("fromDateStr") + "', " + DB_TIMESTAMP_TZ_FMT  +")";
//		String rangeEndDateTime = "TO_TIMESTAMP_TZ('" + (String)entry.get("toDateStr") + "', " + DB_TIMESTAMP_TZ_FMT  +")";
//		String recollectValidStart = "TO_TIMESTAMP_TZ('" + (String)entry.get("recValidStartStr") + "', " +  DB_TIMESTAMP_TZ_FMT  + ")";
//		String recollectValidEnd = "TO_TIMESTAMP_TZ('" + (String)entry.get("recValidEndStr") + "', " +  DB_TIMESTAMP_TZ_FMT  +")";
		
		String started  = null;
		if (  recollectStarted  != null ) {
			started = "TO_DATE('" + recollectStarted + "', 'YYYYMMDDHH24MISS')";
		}
		String completed = null;
		if (recollectCompletedDate!= null ) {
			completed = "TO_DATE('" + recollectCompletedDate+ "', 'YYYYMMDDHH24MISS')";
		}
		String status = null;
		if ( recollectStatus != null ) {
			status = "'" + recollectStatus + "'";
		}
		String idRaw = "HEXTORAW('" + id+ "')";
		
		TransactionStatus txStatus = null;
		int updateCnt = 0;
		try {
			txStatus = txmanager.getTransaction(null);
			StringBuffer sb = new StringBuffer();
			ArrayList<Object> sqlParams = new ArrayList<Object>();
			

			
//			sb.append("select PRIORITY, METERID " + 
//					"  from "+ _dwhView +  
			/*
			sb.append( "update " + _dwhView + " set RECOLLECT_STARTED=" + started + ", RECOLLECT_COMPLETED=" + completed + ", RECOLLECT_STATUS =" +  status );
			sb.append(" where ");
			sb.append(" METERID = '" + meterId + "'");
			sqlParams.add(meterId);
			
			sb.append(" and RANGE_START_DATETIME =  "  + rangeStartDateTime );
			sqlParams.add(rangeStartDateTime);
			
			sb.append(" and RANGE_END_DATETIME = " + rangeEndDateTime) ;
			sqlParams.add(rangeEndDateTime);
			
			sb.append(" and RECOLLECT_VALID_START = " + recollectValidStart);
			sqlParams.add(recollectValidStart);
			
			sb.append(" and RECOLLECT_VALID_END = " + recollectValidEnd);
			sqlParams.add(recollectValidEnd);
			*/
			
			sb.append("MERGE INTO " + _dwhView + " d  USING ( SELECT ");
			if ( completed != null && status != null ) {
				sb.append(completed + " RECOLLECT_COMPLETED, ");
			}
			if ( started != null ) {
				sb.append( started + " RECOLLECT_STARTED, ");
			}
			if ( status != null ) {
				sb.append(status + " RECOLLECT_STATUS, ");
			}
			sb.append(idRaw + " ID FROM DUAL ) s \n" );
			sb.append( " ON ( d.ID = s.ID ) ");
			sb.append("WHEN MATCHED THEN \n");
			sb.append("     UPDATE SET ");
			if ( completed != null ) {
				sb.append("        d.RECOLLECT_COMPLETED  = s.RECOLLECT_COMPLETED, \n");
			}
			if ( started != null) {
				sb.append("        d.RECOLLECT_STARTED  = s.RECOLLECT_STARTED, \n");
			}
			if ( status != null ) {
				sb.append("        d.RECOLLECT_STATUS     = s.RECOLLECT_STATUS \n");
			}
			Query query = em.createNativeQuery(sb.toString());
			
			updateCnt  = query.executeUpdate();
			txmanager.commit(txStatus);
		}
		catch (Exception e) {
			logger.error("Update RECOLLECT TABLE error - " + e, e);
			if(txStatus != null && !txStatus.isCompleted()) {
				txmanager.rollback(txStatus);
			}
		}
		finally {
			logger.info("UPDATE CNT[{}] ID[{}] METERID[{}], from-to[{}-{}] ,RANGE[{} - {}],RECOLLECT_VALID[{}-{}], RECOLLECT_STARTED[{}],RECOLLECT_COMPLETED[{}],STATUS[{}],DSO[{}],Priority[{}]",
					updateCnt,
					(String)entry.get("id"),
					meterId,(String)entry.get("fromDate"),(String)entry.get("toDate"),
					(String)entry.get("fromDateStr"),(String)entry.get("toDateStr"),
					(String)entry.get("recValidStartStr"),(String)entry.get("recValidEndStr"),
					(String)entry.get("started"),(String)entry.get("completed"),status,
					(String)entry.get("dso"), (String)entry.get("rec_prio"));
		}
	}
	
	private List  getRecollectListForDevTest(Map<String,Object> params, boolean count ) {
		
		List<BigDecimal> ret = new ArrayList<BigDecimal>();
		int meternum = 10;
		int datenum = 5;
		if ( _meters != null) {
			meternum = _meters.length;
		}
		if ( _fromDate != null ){
			datenum = 1;
		}
		logger.debug("meter_num[{}] date_num[{}]", meternum,datenum);
		ret.add(new BigDecimal(meternum*datenum));

		return ret;
	}
	
	private List<Object[]> getListForDevTest() 
	{
		List<Object[]> ret = new ArrayList<Object[]>();
		//String[] meters = {"5100000000000012","6970631400000035","6970631400000066","6970631401147593"};
		String[] meters = {
				"6970631400900694",
				"6970631400900670",
				"6970631400900960",
				"6970631400899905",
				"6970631404701358",
				"6970631400900540",
				"6970631400900748",
				"6970631400900199",
				"6970631400900113",
				"6970631400165512"
		};
		//String[] modems = {"000B12000000093A","000B1200000BD653","000B12000001FA1B","000B120000001615"};
		String[] modems = {
		"000B1200000449AF",
		"000B12000002F44C",
		"000B120000037EB5",
		"000B120000059902",
		"000B12000007F34F",
		"000B12000004612D",
		"000B12000007F35B",
		"000B12000007F311",
		"000B1200000451AE",
		"000B120000005A8A"
		};
		String[] fromDate = {
				"20180701000000+0000",
				"20180708000000+0000",
				"20180715000000+0000",
				"20180722000000+0000",
				"20180729000000+0000"
		};
		String[] toDate = {
				"20180707235959+0000",
				"20180714235959+0000",
				"20180721235959+0000",
				"20180728235959+0000",
				"20180803235959+0000"
		};
		
		
		if ( _meters !=  null ) {
			meters = _meters;
			modems = new String[_meters.length];
			for ( int i = 0; i < _meters.length; i++) {
				Meter meter = meterDao.get(_meters[i]);
				modems[i] = meter.getModem().getDeviceSerial();
			}
		}
				
		
		if ( _fromDate != null && _toDate != null) {
			fromDate = new String[1];
			fromDate[0] = _fromDate + "+0000";
			toDate = new String[1];
			toDate[0] = _toDate + "+0000";
		}
		try {
			for ( int j = 0; j < fromDate.length; j++) {
				for ( int i = 0; i < meters.length; i++ ) {
					Object[] arr = new Object[8];
					arr[0] = modems[i];
					arr[1] = "1.51";
					arr[2] = "MMIU";
					arr[3] = "GPRS";
					arr[4] = meters[i];
					arr[5] = new BigDecimal(0);
					arr[6] = fromDate[j];
					arr[7] = toDate[j];
					ret.add(arr);
				}
			}
		}catch(Exception e) {
			logger.error(e.getMessage(),e);
		}
		return ret;
		
	}    

}