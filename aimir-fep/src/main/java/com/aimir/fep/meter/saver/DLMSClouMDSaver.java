package com.aimir.fep.meter.saver;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.MeterStatus;
import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.constants.CommonConstants.MeteringType;
import com.aimir.fep.command.conf.DLMSMeta.LOAD_CONTROL_STATUS;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.command.mbean.CommandGW.OnDemandOption;
import com.aimir.fep.meter.AbstractMDSaver;
import com.aimir.fep.meter.data.LPData;
import com.aimir.fep.meter.entry.IMeasurementData;
import com.aimir.fep.meter.parser.DLMSClou;
import com.aimir.fep.meter.parser.DLMSClouTable.DLMSVARIABLE.RELAY_STATUS_CLOU;
import com.aimir.fep.util.DataUtil;
import com.aimir.model.device.EnergyMeter;
import com.aimir.model.device.Meter;
import com.aimir.model.system.Code;
import com.aimir.model.system.Contract;
import com.aimir.util.DateTimeUtil;

@Service
public class DLMSClouMDSaver extends AbstractMDSaver {
	private static Log log = LogFactory.getLog(DLMSClouMDSaver.class);
	@Override
	protected boolean save(IMeasurementData md) throws Exception {
		DLMSClou parser = (DLMSClou) md.getMeterDataParser();
		
		saveMeterInfomation(parser);
		
		saveLpClouUsingLPTime(md, parser);
		
		saveMeteringDataWithChannel(md,parser);
		
		saveEventLog(parser);

        savePowerQualityKaifa(parser);
        
		return true;
	}

	private void saveMeterInfomation(DLMSClou parser) throws Exception {
		boolean updateflg = false;
		Long ct_ratio = parser.getCtRatio();
		Long vt_ratio = parser.getVtRatio();
		Integer lpInterval = parser.getLpInterval();
		String fwVer = parser.getFwVersion();
		LinkedHashMap<String, Object> relayStatus = parser.getRelayStatus();
		
		try {
			EnergyMeter eMeter = null;
			switch (MeterType.valueOf(parser.getMeter().getMeterType().getName())) {
			case EnergyMeter:
				eMeter = (EnergyMeter)parser.getMeter();
				break;
			}
			
			if(eMeter == null)
				return ;
			
			// set Ct Ratio
			if ( ct_ratio != 0L ){
				if ( (eMeter.getCt() == null && ct_ratio != null )
						|| ((eMeter.getCt() != null && ct_ratio != null) && (eMeter.getCt() != ct_ratio.doubleValue()) )) {
					eMeter.setCt(ct_ratio.doubleValue());
					log.debug("MDevId[" + parser.getMDevId() + "] set ct_ratio[" + ct_ratio.doubleValue() + "]");
					updateflg = true;
				}
			}

			// set Vt Ratio
			if ( vt_ratio != 0L ){
				if ( (eMeter.getVt() == null && vt_ratio != null )
						|| ((eMeter.getVt() != null && vt_ratio != null) && (eMeter.getVt() != vt_ratio.doubleValue()) )) {
					eMeter.setVt(vt_ratio.doubleValue());
					log.debug("MDevId[" + parser.getMDevId() + "] set vt_ratio[" + vt_ratio.doubleValue() + "]");
					updateflg = true;
				}
			}
			
			//set FW Version 
			if ( fwVer.length() != 0 ) {
				if (eMeter.getSwVersion() == null ||
						(eMeter.getSwVersion() != null && !eMeter.getSwVersion().equals(fwVer))) {
					eMeter.setSwVersion(fwVer);
					log.debug("MDevId[" + parser.getMDevId() + "] set Swversion[" + fwVer + "]");
					updateflg = true;
				}
			}
			
			//set lpInterval
			if(eMeter.getLpInterval() == null || !eMeter.getLpInterval().equals(lpInterval)) {
				eMeter.setLpInterval(lpInterval);
				log.debug("MDevId[" + parser.getMDevId() + "] set lpInterval[" + eMeter.getLpInterval() + "]");
				updateflg = true;
			}
			
			// set Relay Status
			if((eMeter.getSwitchActivateStatus() != null && relayStatus != null) && eMeter.getSwitchActivateStatus() != relayStatus.get("Relay Status")) {
				eMeter.setSwitchActivateStatus(Integer.parseInt((String) relayStatus.get("Relay Status")));
				log.debug("MDevId[" + parser.getMDevId() + "] set SwitchActivateStatus[" + relayStatus.get("Relay Status") + "]");
				updateflg = true;
			}
			
			// set Meter model
			/*
			 * 
			 */
			
			// set Threshold
			/*
			 * 
			 */
			
			// set min Threshold
			/*
			 * 
			 */
			
			if(updateflg) {
				meterDao.update(eMeter);
			}
		}catch(Exception e) {
			log.error(e,e);
			throw e;
		}finally {
			log.debug("MDevId[" + parser.getMDevId() + "] saveMeterInfomation finish");
		}
	}
	
	private void saveLpClouUsingLPTime(IMeasurementData md, DLMSClou parser)  throws Exception {
		try {
			LPData[] lplist = parser.getLpData();
			if (lplist == null || lplist.length == 0) {
				log.warn("LP size is 0!!");
				return;
			}
			log.debug("saveLpClouUsingLPTime Start Total LPSIZE => " + lplist.length);
			
			log.debug("Parser Info : "+parser.toString());
			Double meteringValue =  parser.getMeteringValue() == null ? 0d : parser.getMeteringValue();
			Meter meter = parser.getMeter();
	        String dsttime = DateTimeUtil.getDST(null, md.getTimeStamp());
	        String meterTime =  parser.getMeterTime();
	        log.debug("MDevId[" + parser.getMDevId() + "] DSTTime["+dsttime+"]");

	        if (meterTime != null && !"".equals(meterTime))
	            meter.setLastReadDate(meterTime);
	        else
	            meter.setLastReadDate(dsttime);
	        
	        meter.setLastMeteringValue(meteringValue);
	        log.debug("MDevId[" + parser.getMDevId() + "] DSTTime["+dsttime+"] LASTREADDate[" + meter.getLastReadDate()+"]");
	        
	        Code normalStatus = CommonConstants.getMeterStatusByName(MeterStatus.Normal.name());
	        log.debug("MDevId[" + parser.getMDevId() + "] METER_STATUS[" + (meter.getMeterStatus() == null ? "NULL" : meter.getMeterStatus()) + "]");
	        if (meter.getMeterStatus() == null || 
	                (meter.getMeterStatus() != null && 
	                !meter.getMeterStatus().getName().equals("CutOff") && 
	                !meter.getMeterStatus().getName().equals("Delete"))){
	            meter.setMeterStatus(normalStatus);
	            log.debug("MDevId[" + parser.getMDevId() + "] METER_CHANGED_STATUS[" + meter.getMeterStatus() + "]");
	        }
	        
	        if (meterTime != null && !"".equals(meterTime)) {
	            try {
	                long diff = DateTimeUtil.getDateFromYYYYMMDDHHMMSS(md.getTimeStamp()).getTime() - 
	                        DateTimeUtil.getDateFromYYYYMMDDHHMMSS(meterTime).getTime();
	                meter.setTimeDiff(diff / 1000);
    				log.debug("MDevId[" + parser.getMDevId() + "] Update timeDiff. diff=[" + meter.getTimeDiff() + "]"); // INSERT SP-406
	            }
	            catch (ParseException e) {
	                log.warn("MDevId[" + parser.getMDevId() + "] Check MeterTime[" + meterTime + "] and MeteringTime[" + md.getTimeStamp() + "]");
	            }
	        }     
	        
	        lpSaveUsingLPTime(md, lplist, parser);
			
		}catch(Exception e) {
			log.error(e,e);
			throw e;
		}finally {
			log.debug("MDevId[" + parser.getMDevId() + "] saveLpClouUsingLPTime finish");
		}
	}

	private boolean lpSaveUsingLPTime(IMeasurementData md, LPData[] validlplist, DLMSClou parser) throws Exception {
		log.info("#########save mdevId:"+parser.getMDevId());
		
		ArrayList<String> dupdateList = new ArrayList<String>();
		ArrayList<String> dateList = new ArrayList<String>();
		
		for(LPData lp: validlplist) {
			dupdateList.add(lp.getDatetime().substring(0, 8));
		}
		
		HashSet hs = new HashSet(dupdateList);
		Iterator it = hs.iterator();
		while(it.hasNext()){
			dateList.add((String)it.next());
		}
		
		String[] dayList = dateList.toArray(new String[0]);
		Arrays.sort(dayList);
		log.debug("dateList:"+ArrayUtils.toString(dayList ,"-"));

		//split by date SP-890
		for(String day : dayList) {

			ArrayList<String> timeList = new ArrayList<String>();
			ArrayList<Integer> flagList = new ArrayList<Integer>();
			ArrayList<Double> pfList = new ArrayList<Double>();
			ArrayList<LPData> lpValueList = new ArrayList<LPData>();
			
			for(LPData lpdata : validlplist) {
				if(lpdata.getDatetime().substring(0, 8).equals(day)){
					timeList.add(lpdata.getDatetime());
					flagList.add(lpdata.getFlag());
					pfList.add(lpdata.getPF());
					lpValueList.add(lpdata);
				}
			}
			
			String[] timelist = timeList.toArray(new String[0]);
			int[] flaglist = ArrayUtils.toPrimitive(flagList.toArray(new Integer[0]));
			double[] pflist = ArrayUtils.toPrimitive(pfList.toArray(new Double[0]));
			
			LPData[] splitlplist = lpValueList.toArray(new LPData[0]);
			double[][] lpValues = new double[splitlplist[0].getCh().length][splitlplist.length];
			
	        double[] _baseValue = new double[lpValues.length];
	        _baseValue[0] = validlplist[0].getLpValue();;
	        for (int i = 1; i < lpValues.length; i++) {
	            _baseValue[i] = 0;
	        }
			
			//test debugging
			log.debug("date="+day+","+ArrayUtils.toString(timelist ,"-"));
			log.debug("date="+day+","+ArrayUtils.toString(flaglist ,"-"));
			log.debug("date="+day+","+ArrayUtils.toString(pflist ,"-"));
			log.debug("date="+day+","+ArrayUtils.toString(_baseValue ,"-"));

			for (int ch = 0; ch < lpValues.length; ch++) {
				for (int lpcnt = 0; lpcnt < lpValues[ch].length; lpcnt++) {
					lpValues[ch][lpcnt] = splitlplist[lpcnt].getCh()[ch];					
					log.debug(lpValues[ch][lpcnt]);
				}
			}

			try {
				saveLPDataUsingLPTime(MeteringType.Normal, timelist, lpValues, flaglist,
						_baseValue, parser.getMeter(), parser.getDeviceType(),
						parser.getDeviceId(), parser.getMDevType(), parser
								.getMDevId());
			}catch(Exception e) {
				log.warn(e,e);;
			}
		}
		
		return true;
	}
	
	
	private void saveMeteringDataWithChannel(IMeasurementData md, DLMSClou parser) throws Exception {
		
	}
	
	private void saveEventLog(DLMSClou parser) throws Exception {
		
	}
	
	private void savePowerQualityKaifa(DLMSClou parser) throws Exception {
		
	}
	
	@Override
	public String relayValveOn(String mcuId, String meterId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		TransactionStatus txstatus = null;
		try {
			txstatus = txmanager.getTransaction(null);
			Meter meter = meterDao.get(meterId);

			txmanager.commit(txstatus);

			CommandGW commandGw = DataUtil.getBean(CommandGW.class);

			resultMap = commandGw.cmdOnDemandMeter(mcuId, meterId, OnDemandOption.WRITE_OPTION_RELAYON.getCode());

			if (resultMap != null && resultMap.get("LoadControlStatus") != null) {

				LOAD_CONTROL_STATUS ctrlStatus = (LOAD_CONTROL_STATUS) resultMap.get("LoadControlStatus");

				if (ctrlStatus.getCode() == LOAD_CONTROL_STATUS.CLOSE.getCode()) {
					meter.setMeterStatus(CommonConstants.getMeterStatusByName(MeterStatus.Normal.name()));

					Contract contract = meter.getContract();
					if (contract != null && (contract.getStatus() == null|| contract.getStatus().getCode().equals(CommonConstants.ContractStatus.PAUSE.getCode()))) {
						Code normalCode = codeDao.getCodeIdByCodeObject(CommonConstants.ContractStatus.NORMAL.getCode());
						contract.setStatus(normalCode);
					}
				}
			}
		} catch (Exception e) {
			if (txstatus != null && !txstatus.isCompleted())
				txmanager.rollback(txstatus);
			resultMap.put("failReason", e.getMessage());
		}

		return MapToJSON(resultMap);
	}

	@Override
	public String relayValveOff(String mcuId, String meterId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		TransactionStatus txstatus = null;
		try {
			txstatus = txmanager.getTransaction(null);
			Meter meter = meterDao.get(meterId);

			txmanager.commit(txstatus);

			CommandGW commandGw = DataUtil.getBean(CommandGW.class);

			resultMap = commandGw.cmdOnDemandMeter(mcuId, meterId, OnDemandOption.WRITE_OPTION_RELAYOFF.getCode());

			if (resultMap != null && resultMap.get("LoadControlStatus") != null) {

				LOAD_CONTROL_STATUS ctrlStatus = (LOAD_CONTROL_STATUS) resultMap.get("LoadControlStatus");

				if (ctrlStatus.getCode() == LOAD_CONTROL_STATUS.OPEN.getCode()) {
					meter.setMeterStatus(CommonConstants.getMeterStatusByName(MeterStatus.CutOff.name()));
					Contract contract = meter.getContract();
					if (contract != null && (contract.getStatus() == null || contract.getStatus().getCode().equals(CommonConstants.ContractStatus.NORMAL.getCode()))) {
						Code pauseCode = codeDao.getCodeIdByCodeObject(CommonConstants.ContractStatus.PAUSE.getCode());
						contract.setStatus(pauseCode);
					}
				}
			}
		} catch (Exception e) {
			if (txstatus != null && !txstatus.isCompleted())
				txmanager.rollback(txstatus);
			resultMap.put("failReason", e.getMessage());
		}

		return MapToJSON(resultMap);
	}
	
	@Override
	public String relayValveStatus(String mcuId, String meterId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		TransactionStatus txstatus = null;
		try {
			txstatus = txmanager.getTransaction(null);
			Meter meter = meterDao.get(meterId);

			txmanager.commit(txstatus);

			CommandGW commandGw = DataUtil.getBean(CommandGW.class);

			int nOption = OnDemandOption.READ_OPTION_RELAY.getCode(); // read
																		// table
			resultMap = commandGw.cmdOnDemandMeter(mcuId, meterId, nOption);

			if (resultMap != null) {

				log.debug(resultMap.toString());

				if (resultMap.get("LoadControlStatus") != null) {
					if (((LOAD_CONTROL_STATUS) resultMap.get("LoadControlStatus")).getCode() == LOAD_CONTROL_STATUS.CLOSE.getCode()) {
						meter.setMeterStatus(CommonConstants.getMeterStatusByName(MeterStatus.Normal.name()));

						Contract contract = meter.getContract();
						if (contract != null && (contract.getStatus() == null || contract.getStatus().getCode().equals(CommonConstants.ContractStatus.PAUSE.getCode()))) {
							Code normalCode = codeDao.getCodeIdByCodeObject(CommonConstants.ContractStatus.NORMAL.getCode());
							contract.setStatus(normalCode);
						}
					} else if (((LOAD_CONTROL_STATUS) resultMap.get("LoadControlStatus")).getCode() == LOAD_CONTROL_STATUS.OPEN.getCode()) {
						meter.setMeterStatus(CommonConstants.getMeterStatusByName(MeterStatus.CutOff.name()));
						Contract contract = meter.getContract();
						if (contract != null && (contract.getStatus() == null || contract.getStatus().getCode().equals(CommonConstants.ContractStatus.NORMAL.getCode()))) {
							Code pauseCode = codeDao.getCodeIdByCodeObject(CommonConstants.ContractStatus.PAUSE.getCode());
							contract.setStatus(pauseCode);
						}
					}
				} else if (resultMap.get("SendSMSStatus") != null) {
					// Iraq MOE GPRS Modem의 경우 해당.
				}
			}
		} catch (Exception e) {
			if (txstatus != null && !txstatus.isCompleted())
				txmanager.rollback(txstatus);
			log.error(e, e);
			resultMap.put("failReason", e.getMessage());
		}

		return MapToJSON(resultMap);
	}
}
