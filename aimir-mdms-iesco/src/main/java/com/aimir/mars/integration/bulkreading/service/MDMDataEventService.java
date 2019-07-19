package com.aimir.mars.integration.bulkreading.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.mars.integration.bulkreading.dao.MDMBatchDataDao;
import com.aimir.mars.integration.bulkreading.dao.MDMBatchLogDao;
import com.aimir.mars.integration.bulkreading.dao.MDMMeterEventLogDao;
import com.aimir.mars.integration.bulkreading.model.MDMBatchLog;
import com.aimir.mars.integration.bulkreading.model.MDMMeterEventLog;
import com.aimir.mars.integration.bulkreading.xml.cim.MeterReadingsType;
import com.aimir.mars.integration.bulkreading.xml.cim.header.MessageHeaderType;
import com.aimir.mars.integration.bulkreading.xml.data.MOEConstants;

import ch.iec.tc57._2011.enddeviceevents.EndDeviceEvent;
import ch.iec.tc57._2011.enddeviceevents.EndDeviceEvent.EndDeviceEventType;
import ch.iec.tc57._2011.enddeviceeventsmessage.EndDeviceEventsEventMessageType;
import ch.iec.tc57._2011.enddeviceevents.EndDeviceEvents;
import ch.iec.tc57._2011.schema.message.EventMessageType;
import ch.iec.tc57._2011.schema.message.HeaderType;
import ch.iec.tc57._2011.schema.message.Noun;
 
@Service
@Transactional
public class MDMDataEventService extends MDMDataService { 
	
	private static final Logger log = LoggerFactory.getLogger(MDMDataEventService.class);
	
	@Autowired
	MDMBatchDataDao mdmBatchDataDao;
	
	@Autowired
	MDMBatchLogDao mdmBatchLogDao;
	
	@Autowired
	MDMMeterEventLogDao mdmMeterEventLogDao;
	
	List<MDMMeterEventLog> errorList = null; 
	List<Map<String, Object>> mdmDatas = null; 
	EndDeviceEvents endDeviceEvents = null;
	
	private String mdmType = MOEConstants.MDMType.Event.name();
	
	public MDMDataEventService() {
		mdmsHelper = MDMSHelper.getInstance();
		endDeviceEvents = new EndDeviceEvents();
	}
	
	@Override
	public void getMessage() {
		
		HeaderType _header = new HeaderType();
		_header.setNoun("EndDeviceEvents");
		
		EndDeviceEventsEventMessageType message = mdmsHelper.setEndDeviceEventsEventMessage(_header, endDeviceEvents);
		
		String strEventMessage = mdmsHelper.getMessageString(message);
		
		log.debug("## [getMessage] {}", strEventMessage);
		
		saveEventFile(strEventMessage, mdmType);		
	}
	
	@Override
	public void execute() {
		
		log.info("================ MDMDataEVENTService.execute START ============== ");
		
		getBatchData();
		
		ftpUpload(mdmType);
		
		log.info("================ MDMDataEVENTService.execute END ============== ");
	}
	
	@Override
	public void getBatchData() {

		try {			
			Map<String, Object> condition = new HashMap<String, Object>();
	        
	        condition.put("batchStatus", BATCH_STATUS_READY);
	        condition.put("batchType", "METEREVENT_LOG");
	        condition.put("maxResult", MDM_BATCH_MAXRESULT);
	        
	        List<MDMBatchLog> batchs = mdmBatchLogDao.selectBatchList(condition);
	        
	        for(MDMBatchLog batch : batchs) {
	        	
	        	errorList = new ArrayList<MDMMeterEventLog>();
	        	
	        	Map<String, Object> map = new HashMap<String, Object>();
	        	map.put("BATCH_ID", batch.getBatch_id());
	        	map.put("BATCH_STATUS", BATCH_STATUS_COMPLETE);
	        	
	        	mdmDatas = mdmMeterEventLogDao.select(map);
	        		        	
		        if(mdmDatas != null) {
		        	makeDataList(); 
		        }
		        
	        	// batch log 상태 변경
	        	mdmBatchLogDao.updateBatchStatus(map);
	        	
	        	// 전송일자 update
	        	mdmMeterEventLogDao.updateTransferDate(batch.getBatch_id());
				
				// 오류 업데이트
	        	mdmMeterEventLogDao.updateInitTransferDate(errorList);				
	        }
	        
		} catch (Exception ex) {        	
            log.error("[getData ERROR]", ex);
        }
	}	
	
	
	public void getBatchDataTest() {

		try {			
//			Map<String, Object> condition = new HashMap<String, Object>();
//	        
//	        condition.put("batchStatus", BATCH_STATUS_READY);
//	        condition.put("batchType", "METEREVENT_LOG");
//	        condition.put("maxResult", MDM_BATCH_MAXRESULT);
//	        
//	        List<MDMBatchLog> batchs = mdmBatchLogDao.selectBatchList(condition);
//	        
//	        for(MDMBatchLog batch : batchs) {
	        	
	        	errorList = new ArrayList<MDMMeterEventLog>();
	        	
	        	Map<String, Object> map = new HashMap<String, Object>();
	        	map.put("BATCH_ID", 1664);
//	        	map.put("BATCH_STATUS", BATCH_STATUS_COMPLETE);
	        	
	        	mdmDatas = mdmMeterEventLogDao.select(map);
	        		        	
		        if(mdmDatas != null) {
		        	makeDataList(); 
		        }
		        
	        	// batch log 상태 변경
//	        	mdmBatchLogDao.updateBatchStatus(map);
	        	
//	        	// 전송일자 update
//	        	mdmMeterEventLogDao.updateTransferDate(batch.getBatch_id());
//				
//				// 오류 업데이트
//	        	mdmMeterEventLogDao.updateInitTransferDate(errorList);				
//	        }
//	        
		} catch (Exception ex) {        	
            log.error("[getData ERROR]", ex);
        }
	}
	
	@Override
	public void makeDataList() {
		
		endDeviceEvents = new EndDeviceEvents();
		
		try {
		
			for(Map<String, Object> dat : mdmDatas) {
				
				String meterEventId = (String)dat.get("meterevent_id");
	        	String meterId = (String)dat.get("activator_id");	        	
	        	String eventDate = (String)dat.get("open_time");
	        
				mdmsHelper.setEndDeviceEventList(endDeviceEvents, meterId, meterEventId, eventDate);
			}
			
			getMessage();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}