package com.aimir.mars.integration.bulkreading.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.fep.meter.parser.DLMSKaifaTable.DLMSVARIABLE;
import com.aimir.mars.integration.bulkreading.dao.MDMBatchDataDao;
import com.aimir.mars.integration.bulkreading.dao.MDMBatchLogDao;
import com.aimir.mars.integration.bulkreading.dao.MDMLpEMDao;
import com.aimir.mars.integration.bulkreading.model.MDMBatchLog;
import com.aimir.mars.integration.bulkreading.model.MDMLpEM;
import com.aimir.mars.integration.bulkreading.xml.cim.MeterReadingsType;
import com.aimir.mars.integration.bulkreading.xml.cim.header.MessageHeaderType;
import com.aimir.mars.integration.bulkreading.xml.cim.meterreading.DeviceListType;
import com.aimir.mars.integration.bulkreading.xml.cim.meterreading.DeviceListType.Device.InitialMeasurementDataList.InitialMeasurementData.PreVEE.Msrs.ML;
import com.aimir.mars.integration.bulkreading.xml.data.MOEConstants;
import com.aimir.model.device.MCU;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;

import ch.iec.tc57._2011.schema.message.Noun;

@Service
@Transactional
public class MDMDataLPService extends MDMDataService {
	
	private static final Logger log = LoggerFactory.getLogger(MDMDataLPService.class);
	
	@Autowired
	MDMBatchDataDao mdmBatchDataDao;
	
	@Autowired
	MDMBatchLogDao mdmBatchLogDao;
	
	@Autowired
	MDMLpEMDao mdmLpEMDao;
	
	List<MDMLpEM> errorList = null; 
	List<MDMLpEM> mdmDatas = null; 
	DeviceListType deviceList = null;
	
	private String mdmType = MOEConstants.MDMType.LP.name();
	
	public MDMDataLPService() {
		mdmsHelper = MDMSHelper.getInstance();
		deviceList = new DeviceListType();
	}
	
	@Override
	public void getMessage() {
		
		MessageHeaderType header = new MessageHeaderType();
		header.setNoun(Noun.deviceList.getDescription());
		
		MeterReadingsType message = mdmsHelper.setMeterReadingsMessage(header, deviceList);
		
		String strEventMessage = mdmsHelper.getMessageString(message);
		
		log.debug("## [getMessage] {}", strEventMessage);
		
		saveLPFile(strEventMessage, mdmType);		
	}
	
	@Override
	public void execute() {
		
		log.info("================ MDMDataLPService.execute START ============== ");
		
		getBatchData();
		        
    	ftpUpload(mdmType);
		
		log.info("================ MDMDataLPService.execute END ============== ");
	}
	
	@Override
	public void getBatchData() {

		try {			
			Map<String, Object> condition = new HashMap<String, Object>();
	        
	        condition.put("batchStatus", BATCH_STATUS_READY);
	        condition.put("batchType", "LP_EM");
	        condition.put("maxResult", MDM_BATCH_MAXRESULT);
	        
	        log.debug("mdmBatchLogDao : " + mdmBatchLogDao);
	        
	        List<MDMBatchLog> batchs = mdmBatchLogDao.selectBatchList(condition);
	        
	        for(MDMBatchLog batch : batchs) {
	        	
	        	errorList = new ArrayList<MDMLpEM>();
	        	
	        	Set<Condition> conditions = new HashSet<Condition>();
	        	conditions.add(new Condition("batchId", new Object[]{ batch.getBatch_id() }, null, Restriction.EQ));
	        	conditions.add(new Condition("id.mdevId", null, null, Restriction.ORDERBY));
	        	conditions.add(new Condition("id.yyyymmddhhmmss", null, null, Restriction.ORDERBY));
	        	List<MDMLpEM> list = mdmLpEMDao.findByConditions(conditions);
	        	
	        	if(list != null) {
	        		
	        		log.debug("batch.getBatch_id() : " + batch.getBatch_id() + "  count : " + list.size());
	        		
	        		mdmDatas = list;
	        		makeDataList();
	        		
	        		Map<String, Object> map = new HashMap<String, Object>();
		        	map.put("BATCH_ID", batch.getBatch_id());
		        	map.put("BATCH_STATUS", BATCH_STATUS_COMPLETE);
		        	
		        	// batch log 상태 변경
		        	mdmBatchLogDao.updateBatchStatus(map);		        	
		        	
		        	// 전송일자 update
		        	mdmLpEMDao.updateTransferDate(batch.getBatch_id());
					
					// 오류 업데이트
		        	mdmLpEMDao.updateInitTransferDate(errorList);
	        	}	        	
	        }
	        
		} catch (Exception ex) {        	
            log.error("[getData ERROR]", ex);
        }
	}	
	
	
	public void getBatchDataTest(int batch_id) {

		try {			
			Map<String, Object> condition = new HashMap<String, Object>();
	        
	        	
	        	errorList = new ArrayList<MDMLpEM>();
	        	
	        	Set<Condition> conditions = new HashSet<Condition>();
	        	conditions.add(new Condition("batchId", new Object[]{ batch_id}, null, Restriction.EQ));
	        	conditions.add(new Condition("id.mdevId", null, null, Restriction.ORDERBY));
	        	conditions.add(new Condition("id.yyyymmddhhmmss", null, null, Restriction.ORDERBY));
	        	List<MDMLpEM> list = mdmLpEMDao.findByConditions(conditions);
	        	
	        	if(list != null) {
	        		
	        		log.debug("batch.getBatch_id() : " + batch_id + "  count : " + list.size());
	        		
	        		mdmDatas = list;
	        		makeDataList();
	        		
	        		Map<String, Object> map = new HashMap<String, Object>();
		        	map.put("BATCH_ID", batch_id);
		        	map.put("BATCH_STATUS", BATCH_STATUS_COMPLETE);
		        	
		        	List<?> logs =  mdmBatchLogDao.getAll();
		        	log.debug("logs : " + logs.size());
		        	
		        	// batch log 상태 변경
		        	mdmBatchLogDao.updateBatchStatus(map);		        	
		        	
//		        	// 전송일자 update
		        	mdmLpEMDao.updateTransferDate(batch_id);
//					
//					// 오류 업데이트
		        	mdmLpEMDao.updateInitTransferDate(errorList);
	        	}	        	
	       
		} catch (Exception ex) {        	
            log.error("[getData ERROR]", ex);
        }
	}	
	
	
	@Override
	public void makeDataList() {
		
		deviceList = new DeviceListType();
		
		try {
		
			for(MDMLpEM dat : mdmDatas) {
				
				String mdevId = dat.getMdevId();
				String yyyymmddhhmmss = dat.getYyyymmddhhmmss();			
				String ts = yyyymmddhhmmss.substring(8, 10) + "." + yyyymmddhhmmss.substring(10, 12) + "." + "00";
				String status = dat.getInstallProperty();
				
				Meter meter = dat.getMeter();
				String deviceModelName = "";
				if(meter != null) {
					deviceModelName = meter.getModel().getName();
				}
				
				Modem modem = meter.getModem();
				MCU mcu = null;
				if(modem == null) log.debug("modem is null");
				if(modem != null) mcu = modem.getMcu();
							
				int lp_interval = meter.getLpInterval(); 
				
				String stDt = yyyymmddhhmmss;
				String enDt = mdmsHelper.getEDateTime(yyyymmddhhmmss, lp_interval);
				String yyyymmdd = yyyymmddhhmmss.substring(0, 8); 
								
				DeviceListType.Device device = null;
				DeviceListType.Device.InitialMeasurementDataList mDataList = null;				
				
				for(DeviceListType.Device _device : deviceList.getDevice()) {
					// 동일한 device 가 있는지 체크
					if(mdevId.equals(_device.getDeviceIdentifierNumber())) {
						device = _device;		
						break;
					}
				}
				
				if(device == null) {
					
					mDataList = new DeviceListType.Device.InitialMeasurementDataList(); // InitialMeasurementDataList
					
					device = new DeviceListType.Device();
					device.setHeadEndExternalId(HEAD_END_ID);
					device.setDeviceIdentifierNumber(mdevId);
					device.setInitialMeasurementDataList(mDataList);
					deviceList.getDevice().add(device);
				}
				
				mDataList = device.getInitialMeasurementDataList();
				
				for(int ch=0; ch < 6; ch++) {
					
					Class<?> clazz = dat.getClass();
					Object obj = clazz.getDeclaredMethod("getCh" + (ch+1)).invoke(dat);
					int midx = -1;
					if(obj != null) {
						
						String mcIdN = MOEConstants.getOBISCode(deviceModelName, ch);
							
						int l = 0;
						for(DeviceListType.Device.InitialMeasurementDataList.InitialMeasurementData _initialMeasurementData : mDataList.getInitialMeasurementData()) {
							String _mcIdn = _initialMeasurementData.getPreVEE().getMcIdN();
							String _stDt = _initialMeasurementData.getPreVEE().getStDt().toString().replace("-", "");
							if(_mcIdn.equals(mcIdN) && _stDt.substring(0, 8).equals(yyyymmdd)) {
								midx = l; 
								break;
							}
							l++;
						}
						
						if(midx < 0) { // 없으면 생성
							midx = mDataList.getInitialMeasurementData().size();
							mdmsHelper.setInitialMeasurementData(mDataList, mcIdN, stDt, enDt, midx);
						}
					
						ML ml = new ML();
						ml.setTs(ts);
						ml.setMeterDt(mdmsHelper.getTimestamp(stDt));
						ml.setCaptureDt(mdmsHelper.getTimestamp(stDt));
						
						if(mcu != null) {
							ml.setCaptureDeviceID(mcu.getSysID());
							ml.setCaptureDeviceType("DCU");
						} else if (modem != null) {
							ml.setCaptureDeviceID(modem.getDeviceSerial());
							ml.setCaptureDeviceType("Modem");
						}
						
						ml.setQ(_floor(Double.parseDouble(String.valueOf(obj))));
						ml.setFc(getFcStatus(status));
						
						
						mdmsHelper.addMLData(mDataList, ml, enDt, midx);
					}
					
				} // end for
			}
			
			getMessage();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private String getFcStatus(String status) {
		
		String fc = MOEConstants.LPStatus.Regular.getCode();
		
		if (status == null || "".equals(status)) {
			fc = MOEConstants.LPStatus.Regular.getCode();			
		} else {
			if (status.indexOf(DLMSVARIABLE.LP_STATUS_BIT[0]) >= 0) {
				fc = MOEConstants.LPStatus.NoReadOutage.getCode();
			} else if (status.indexOf(DLMSVARIABLE.LP_STATUS_BIT[2]) >= 0) {				
				fc = MOEConstants.LPStatus.TimeResetOccurred.getCode();
			} else if (status.indexOf(DLMSVARIABLE.LP_STATUS_BIT[4]) >= 0) {				
				fc = MOEConstants.LPStatus.DST.getCode();
			} else if (status.indexOf(DLMSVARIABLE.LP_STATUS_BIT[5]) >= 0) {				
				fc = MOEConstants.LPStatus.BadAMIData.getCode();
			} else if (status.indexOf(DLMSVARIABLE.LP_STATUS_BIT[6]) >= 0) {				
				fc = MOEConstants.LPStatus.ClockError.getCode();
			} else if (status.indexOf(DLMSVARIABLE.LP_STATUS_BIT[7]) >= 0) {				
				fc = MOEConstants.LPStatus.DeviceFailure.getCode();
			}
		}
		
		return fc;
	}
	
}