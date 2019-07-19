package com.aimir.service.system.impl.demandResponseMgmt;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.net.URL;
import javax.xml.bind.JAXBElement;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Holder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.HomeDeviceCategoryType;
import com.aimir.constants.CommonConstants.ResultStatus;
import com.aimir.dao.device.EndDeviceDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.DemandResponseEventLogDao;
import com.aimir.dao.system.GroupMemberDao;
import com.aimir.dao.system.HomeDeviceDrLevelDao;
import com.aimir.dao.system.HomeGroupDao;
import com.aimir.model.device.EndDevice;
import com.aimir.model.system.Code;
import com.aimir.model.system.DemandResponseEventLog;
import com.aimir.schedule.command.CmdOperationUtil;
import com.aimir.service.system.demandResponseMgmt.DemandResponseMgmtManager;
import com.aimir.service.system.impl.demandResponseMgmt.eventstate.EventState;
import com.aimir.service.system.impl.demandResponseMgmt.eventstate.EventStateConfirmation;
import com.aimir.service.system.impl.demandResponseMgmt.eventstate.ListOfEventStates;
import com.aimir.service.system.impl.demandResponseMgmt.eventstate.ObjectFactory;
import com.aimir.service.system.impl.demandResponseMgmt.soap.DRASClientSOAP;
import com.aimir.service.system.impl.demandResponseMgmt.soap.DRASClientSOAP_Service;
import com.aimir.util.CalendarUtil;
import com.aimir.util.TimeLocaleUtil;

@Service(value="DemandResponseMgmtManager")
@Transactional(readOnly=false)
public class DemandResponseMgmtManagerImpl implements DemandResponseMgmtManager {

    @Autowired
    EndDeviceDao endDeviceDao;
    
    @Autowired
    CodeDao codeDao;
    
    @Autowired
    HomeGroupDao homeGroupDao;

    @Autowired    
    GroupMemberDao groupMemberDao;
    
    @Autowired    
    HomeDeviceDrLevelDao homeDeviceDrLevelDao;

    @Autowired    
    DemandResponseEventLogDao demandResponseEventLogDao;
    
    @Autowired
    CmdOperationUtil cmdOperationUtil;

	public List<Map<String, Object>> getHomeDeviceDRMgmtInfo(String groupId, String homeDeviceGroupName, String homeDeviceCategory) {

		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();

		List<Map<String, Object>> list = endDeviceDao.getHomeDeviceInfoForDrMgmt(groupId, homeDeviceGroupName, homeDeviceCategory);
    	for(Map<String,Object> tmp:list){

    		// 일반 가전일 경우, 맵핑된 스마트 콘센트 정보를 취득한다.
    		int categoryId = codeDao.getCodeIdByCode(HomeDeviceCategoryType.GENERAL_APPLIANCE.getCode());
    		if(categoryId == (Integer)tmp.get("CATEGORYID") && tmp.get("MODEMID") != null) {
        		List<EndDevice> result = endDeviceDao.getMappingHomeDeviceForDrMgmt((Integer)tmp.get("MODEMID"), codeDao.getCodeIdByCode(HomeDeviceCategoryType.SMART_CONCENT.getCode()));
               	tmp.put("MAPPINGID", result.get(0).getId());
        		tmp.put("MAPPINGFRIENDLYNAME", result.get(0).getFriendlyName());
            	tmp.put("MAPPINGIMGURL", result.get(0).getHomeDeviceImgFilename());
            	tmp.put("MAPPINGDRPROGRAMMANDATORY", result.get(0).getDrProgramMandatory());
            	tmp.put("MAPPINGDRLEVEL", result.get(0).getDrLevel());
            	List<Map<String, Object>> drName = homeDeviceDrLevelDao.getHomeDeviceDrLevelByCondition(result.get(0).getCategoryCode().getId(), result.get(0).getDrLevel().toString());
            	tmp.put("MAPPINGDRNAME", drName.get(0).get("DRNAME")); 
            	tmp.put("MAPPINGINSTALLSTATUSCODE", result.get(0).getInstallStatus().getCode());
            	tmp.put("MAPPINGCATEGORYID", result.get(0).getCategoryCode().getId());
        		resultList.add(tmp);
    		}
    	}
		return resultList;
	}

	public void updateDrProgramMandatoryInfo(int id, String drProgramMandatory){
		endDeviceDao.updateDrProgramMandatoryInfo(id, drProgramMandatory);
	}

	public void updateEndDeviceDrLevel(int endDeviceId, int categoryCode, int drLevel) {
		endDeviceDao.updateEndDeviceDrLevel(endDeviceId, categoryCode, drLevel);
	}

//	public List<Map<String, Object>> pollDrWebService() {
//
//		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
//		Map<String, Object> map = new HashMap<String, Object>();
//
//		DemandResponseEventLog demandResponseEventLog = new DemandResponseEventLog();
//		demandResponseEventLog.setOptOutStatus(CommonConstants.DemandResponseEventOptOutStatus.Initialization.getDrEventOptOutStatus()); // 진행중
//		demandResponseEventLog.setEventStatus("FAR"); //// FAR, NEAR, ACTIVE
//		demandResponseEventLog.setDrasClientId("nuritelecom.hems_001");
//		List<DemandResponseEventLog> list = demandResponseEventLogDao.getDemandResponseEventLogs(demandResponseEventLog);
//
//		for(DemandResponseEventLog drEventLog : list) {
//			map.put("identifier", drEventLog.getEventIdentifier());
//			map.put("programName", drEventLog.getProgramName());
////				map.put("RemainingTime", "0Day");
//			map.put("notificationTime", drEventLog.getNotificationTime());
//			map.put("operationModeValue", drEventLog.getOperationModeValue());
//			map.put("startTime", drEventLog.getStartTime());
//			map.put("endTime", drEventLog.getEndTime());
//			result.add(map);
//		}
//	
//		return result;
//	}

	public List<Map<String, Object>> pollDrWebService() {

		List<Map<String, Object>> result = null;
		try{

//			String endPoint = "http://cdp.openadr.com/SOAPClientWS/nossl/soap2";
			String endPoint = "https://cdp.openadr.com/SOAPClientWS/soap2";
         	Authenticator.setDefault( new SimpleAuthenticator() );
         	
        	DRASClientSOAP service = 
        		new DRASClientSOAP_Service(new URL(endPoint + "?wsdl"),
        		new QName("http://www.openadr.org/DRAS/DRASClientSOAP/", "SOAPWS2Service")).getDRASClientSOAPPort();
  
			final BindingProvider bp = (BindingProvider) service;
			bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, 
			  endPoint);

			// get the EventState
        	Holder<String> holderRV = new Holder<String>();
        	Holder<ListOfEventStates> eventStates = new Holder<ListOfEventStates>();
			service.getEventStates("", holderRV, eventStates);
			
	    	JAXBElement<ListOfEventStates> 
    		wsEventStatesElement = (new ObjectFactory()).createListOfEventState(eventStates.value);

			String eventIdentifier = wsEventStatesElement.getValue().getEventStates().get(0).getEventIdentifier();
			if (ResultStatus.SUCCESS.name().equals(holderRV.value) && eventIdentifier.length() != 0) {
				result = getDRInfo((ListOfEventStates)wsEventStatesElement.getValue());
			}

			EventStateConfirmation confirm = new EventStateConfirmation();
			String rv = service.setEventStateConfirmation(confirm);

		}catch(Exception e){
			e.printStackTrace();

		}
		return result;
	}

	private List<Map<String, Object>> getDRInfo(ListOfEventStates list) {
		System.out.println("Get Dr Info!!!!!!");
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		List<EventState> test = list.getEventStates();
		DemandResponseEventLog demandResponseEventLog = null;

		for (EventState e : test) {
            demandResponseEventLog = demandResponseEventLogDao.findByCondition("eventIdentifier", e.getEventIdentifier());
            if( demandResponseEventLog == null){
            	demandResponseEventLog = new DemandResponseEventLog();
            	demandResponseEventLog.setOptOutStatus(CommonConstants.DemandResponseEventOptOutStatus.Initialization.getDrEventOptOutStatus()); // 초기값 설정
            }
			demandResponseEventLog.setDrasClientId(e.getDrasClientID());
			demandResponseEventLog.setEndTime(getDatetimeString(e.getDrEventData().getValue().getEndTime(), ("yyyyMMddHHmmss")));
			//demandResponseEventLog.setEndTime(getDatetimeString(e.getDrEventData().getValue().getEndTime(), ("yyyyMMddHHmmss")));
			demandResponseEventLog.setEventIdentifier(e.getEventIdentifier());
			demandResponseEventLog.setEventInfoName(e.getDrEventData().getValue().getEventInfoInstances().get(0).getEventInfoName());
			demandResponseEventLog.setEventInfoTypeID(e.getDrEventData().getValue().getEventInfoInstances().get(0).getEventInfoTypeID().getValue());
//			demandResponseEventLog.setEventInfoValues(e.getDrEventData().getValue().getEventInfoInstances().get(0).getEventInfoValues());
			demandResponseEventLog.setEventModNumber(Long.toString(e.getEventModNumber()));
			demandResponseEventLog.setEventStateId(Long.toString(e.getEventStateID()));
			demandResponseEventLog.setNotificationTime(getDatetimeString(e.getDrEventData().getValue().getNotificationTime(), ("yyyyMMddHHmmss")));
			demandResponseEventLog.setOperationModeValue(e.getSimpleDRModeData().getOperationModeValue());
			demandResponseEventLog.setProgramName(e.getProgramName());
			demandResponseEventLog.setStartTime(getDatetimeString(e.getDrEventData().getValue().getStartTime(), ("yyyyMMddHHmmss")));
			demandResponseEventLog.setYyyymmdd(getDatetimeString(e.getDrEventData().getValue().getStartTime(), "yyyyMMdd"));

			// EventStatus
			e.getSimpleDRModeData().getEventStatus(); // FAR, NEAR, ACTIVE
			demandResponseEventLogDao.saveOrUpdate(demandResponseEventLog);

			// 초기 상태일때만 화면에 표시한다.
			if(demandResponseEventLog.getOptOutStatus() == CommonConstants.DemandResponseEventOptOutStatus.Initialization.getDrEventOptOutStatus()) {
				map.put("identifier", e.getEventIdentifier());
				map.put("programName", e.getProgramName());
	//			map.put("RemainingTime", "0Day");
				map.put("notificationTime", getLocaleDatetimeString(e.getDrEventData().getValue().getNotificationTime(), ("yyyyMMddHHmmss")));
				map.put("operationModeValue", e.getSimpleDRModeData().getOperationModeValue());
				map.put("startTime", getLocaleDatetimeString(e.getDrEventData().getValue().getStartTime(), ("yyyyMMddHHmmss")));
				map.put("endTime", getLocaleDatetimeString(e.getDrEventData().getValue().getEndTime(), ("yyyyMMddHHmmss")));

				result.add(map);
			}
		}
		return result;
	}

	private String getDatetimeString(XMLGregorianCalendar xmlDate, String format) {
		return CalendarUtil.getDatetimeString(xmlDate.toGregorianCalendar().getTime(), format);
	}
	
	private String getLocaleDatetimeString(XMLGregorianCalendar xmlDate, String format) {
		return TimeLocaleUtil.getLocaleDate(CalendarUtil.getDatetimeString(xmlDate.toGregorianCalendar().getTime(), format), "en","en");
	}

	static class SimpleAuthenticator extends Authenticator {
		public PasswordAuthentication getPasswordAuthentication()
		{
//			System.out.println("Authenticating");
			return new PasswordAuthentication("nuritelecom.hems_001", "Hems_1234"
				.toCharArray());
		}
	}

	public List<Map<String, Object>> getDemandResponseHistory(String userId, String contractNumber, int page, int limit, String fromDate, String toDate) {
		return demandResponseEventLogDao.getDemandResponseHistory(userId, contractNumber, page, limit, fromDate, toDate);
	}

	public String getDemandResponseHistoryTotalCount(String userId, String contractNumber, String fromDate, String toDate) {
		return demandResponseEventLogDao.getDemandResponseHistoryTotalCount(userId, contractNumber, fromDate, toDate);
	}
	
	public void deleteDemandResponseEventLog(int id) {
		demandResponseEventLogDao.deleteById(id);
	}

	/* (non-Javadoc)
	 * @see com.aimir.service.system.demandResponseMgmt.DemandResponseMgmtManager#setOptDREventOptOutStatus(int, java.lang.String)
	 */
	public void setOptDREventOptOutStatus(int optOutStatus, String eventIdentifier, String mcuId, String sensorId) {
		DemandResponseEventLog demandResponseEventLog = demandResponseEventLogDao.findByCondition("eventIdentifier", eventIdentifier);
        if( demandResponseEventLog != null){
        	demandResponseEventLog.setOptOutStatus(optOutStatus); // DR Event 참여 상태를 갱신한다.
        	demandResponseEventLogDao.saveOrUpdate(demandResponseEventLog);

        	// ACD Control Off for ATC Demo ADD start by eunmiae
			//1=RelayOn, 15=RelayOff
			final int SET_ENERGYLEVEL = 15;
			try{
				if(optOutStatus == CommonConstants.DemandResponseEventOptOutStatus.Participated.getDrEventOptOutStatus()) {
					//ACD Contrl off/on
					cmdOperationUtil.cmdSetEnergyLevel(mcuId, sensorId, SET_ENERGYLEVEL);
					//cmdOperationUtil.cmdDigitalInOut(mcuId, sensorId, (byte)1, (byte)0xff, (byte)0x01);

					final String RELAYOFF = "RelayOff";
					Code code = CommonConstants.getMeterStatusByName(RELAYOFF);
					
					// EndDevice를 device serial로 검색한다.
					EndDevice endDevice = endDeviceDao.findByCondition("serialNumber", sensorId);
	
					if(endDevice != null) {
						// EndDevice의 DR레벨을 갱신한다.	
						endDevice.setDrLevel(SET_ENERGYLEVEL);
						endDeviceDao.saveOrUpdate(endDevice);
					}

	//				operationLogManager.saveOperationLogByCustomer(supplier, modem.getMcu().getMcuType(), modem.getDeviceSerial(),
	//				loginId, codeManager.getCodeByCode("8.2.10"), status.getCode(), rtnStr, description.toString(), contract.getContractNumber());
					
					// for ATC Demo
					demandResponseEventLog.setOptOutStatus(CommonConstants.DemandResponseEventOptOutStatus.Completed.getDrEventOptOutStatus()); // DR Event 참여 상태를 갱신한다.
		        	demandResponseEventLogDao.saveOrUpdate(demandResponseEventLog);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			
//			meter.setMeterStatus(code);
//			rtnStr=String.format("Status : %s", code.getName());
//			status = ResultStatus.SUCCESS;
			// ACD Control Off for ATC Demo ADD end by eunmiae
        }
	}
}
