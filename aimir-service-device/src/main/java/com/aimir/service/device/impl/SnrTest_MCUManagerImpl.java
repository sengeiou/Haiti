package com.aimir.service.device.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aimir.dao.device.ModemDao;
import com.aimir.dao.device.SNRLogDao;
import com.aimir.model.device.Modem;
import com.aimir.service.device.SnrTest_MCUManager;
import com.aimir.util.TimeUtil;

@Service(value = "snrTest_MCUManager")
public class SnrTest_MCUManagerImpl implements SnrTest_MCUManager {
	
	@Autowired
    ModemDao modemDao;
    
    @Autowired
    SNRLogDao snrDao;
    
    
	 /**
     * method Desc : 집중기에 연결된 모뎀들의 SNR 목록을 출력한다. (마지막값,평균,최대,최소)
     * @param condition
     * @return
     */
    @SuppressWarnings("unchecked")
	public Map<String,Map<String,Object>> getMcuSnrList (Map<String,Object> condition) {
    	
    	Map<String,Map<String, Object>> result = new HashMap<String,Map<String, Object>>();
    	List<Object> snrList = null;
    	List<Object> statList = null;
    	String baseSysId = condition.get("sysId").toString();
    	
    	//MCU.ID를 조건으로 해당하는 모뎀 리스트 조회
    	HashMap<String,Object> modemCondition = new HashMap<String,Object>();
    	modemCondition.put("mcu.id", condition.get("mcuId"));
    	List<Object> modList = modemDao.getDeviceSerialByMcu(baseSysId);    
    	//List<Modem> modList = modemDao.getModemMapDataWithoutGpio(modemCondition);    	    
    	
    	//조회한 모뎀 리스트의 device serial을 result에 입력
    	Map<String,Object> mergeList = null;
    	if(modList.isEmpty()) return null;
    	int modSize = modList.size();
    	for(int i=0; i<modSize; i++){
    		String mdSid = modList.get(i).toString();
    		mergeList = new HashMap<String,Object>();    		
    		mergeList.put("device_id", mdSid);
    		//snr_log와 modem의 mcu_id가 다를수도 있어서 저장 후 아래에서 비교함.    		
    		result.put(mdSid, mergeList);    		
    	}
    	
    	//SNR_LOG 조회 (Period, Last)
    	if(condition.get("isLatest").toString().equals("period")){
        	snrList = snrDao.getLastSnrByMcu(condition);
        	statList = snrDao.getStatisticsByMcu(condition);
        	
        	// 기존 모뎀 리스트 필요없음
        	result = new HashMap<String,Map<String, Object>>();
        	// result에 기본정보 입력
        	if(snrList.isEmpty()) return null;
        	int listSize = snrList.size();
        	for(int i=0; i<listSize; i++){
        		// snrList.Object[] = 0.SID 1.Date 2.SysId 3.SNR
        		Object[] listA = (Object[])snrList.get(i);
        		String Aid = listA[0].toString().trim();
        		mergeList = new HashMap<String,Object>();
        		mergeList.put("device_id", Aid);
        		mergeList.put("date", TimeUtil.formatDateTime(listA[1].toString()));
    			mergeList.put("mcuId", listA[2].toString());
    			mergeList.put("slast", listA[3]);
    			result.put(Aid, mergeList);
        	}
        	
        	//result에 통계정보 입력(평균, 최대, 최소)
        	listSize = statList.size();
        	for(int q=0; q<listSize; q++){
        		// statList.Object[] = 0.SID 1.AVG 2.MAX 3.MIN
        		Object[] listB = (Object[])statList.get(q);
        		String Bid = listB[0].toString().trim();
        		if(result.containsKey(Bid)){
        			mergeList = result.get(Bid);
        			mergeList.put("savg", listB[1]);
        			mergeList.put("smax", listB[2]);
        			mergeList.put("smin", listB[3]);
        		}else ;
        			// 위에서 추가되지 않은 device serial에 대한 값은 버림
        	}
        	
    	}else{ 
        	snrList = snrDao.getFinalSnrByMcu(condition);
        	//statList = snrDao.getFinalStatisticsByMcu(condition);
        	statList = getFinalStatisticsByMcu(condition);
        	
        	//result에 기본정보 입력함 
        	if(snrList.isEmpty()) return null;
        	int listSize = snrList.size();
        	for(int i=0; i<listSize; i++){
        		// snrList.Object[] = 0.SID 1.Date 2.SysId 3.SNR
        		Object[] listA = (Object[])snrList.get(i);
        		String Aid = listA[0].toString().trim();
        		if(result.containsKey(Aid)){
        			mergeList = result.get(Aid);        			
        			mergeList.put("date", TimeUtil.formatDateTime(listA[1].toString()));
        			mergeList.put("mcuId", listA[2].toString());
        			mergeList.put("slast", listA[3]);
        		}else{
        			// SNR_LOG에는 선택된 집중기로 올렸지만, 지금은 다른 집중기에 붙어 있는 모뎀
        			mergeList = new HashMap<String,Object>();
        			mergeList.put("device_id", Aid);
        			mergeList.put("date", TimeUtil.formatDateTime(listA[1].toString()));
        			// 선택된 집중기 Real Path에 지금 붙어 있는 다른 집중기 sysId도 출력.
        			Modem oModem = modemDao.get(Aid);        			
	        			if(oModem==null || oModem.getMcu()==null) {
	        				mergeList.put("mcuId", listA[2].toString()); 
	        			}else{
	        				String cmi = listA[2].toString().concat(" (");
	        				cmi = cmi.concat(oModem.getMcu().getSysID());
	        				mergeList.put("mcuId", cmi.concat(")"));
	        			}
        			//mergeList.put("mcuId", listA[2].toString());
        			mergeList.put("slast", listA[3]);
        			result.put(Aid, mergeList);
        		}        		
        	}
        	
        	//result에 통계정보 입력(평균, 최대, 최소)
        	listSize = statList.size();
        	for(int q=0; q<listSize; q++){
        		// statList.Object[] = 0.SID 1.AVG 2.MAX 3.MIN
        		Object[] listB = (Object[])statList.get(q);
        		Object[] listC = (Object[]) listB[0];
        		String Bid = listC[0].toString().trim();
        		if(result.containsKey(Bid)){
        			mergeList = result.get(Bid);
        			mergeList.put("savg", listC[2]);
        			mergeList.put("smax", listC[2]);
        			mergeList.put("smin", listC[3]);
        		}else ;
        			// 위에서 추가되지 않은 device serial에 대한 값은 버림
        	}
    	}    // --LAST

    	
    	return result;
    }
    
    public List<Object> getFinalStatisticsByMcu (Map<String,Object> condition){
    	List<Object> result = new ArrayList<Object>();
    	String mcuId = condition.get("sysId").toString();
    	Boolean isPoor = false;
    	if(condition.get("isPoor").equals("poor")){
    		isPoor = true;
    	}
    	// MCU에 연결된 각 모뎀의 최신 업로드 일자를 조회
    	List<Object> fModemList = snrDao.getFinalDayByMcu(mcuId, isPoor);
    	if(fModemList.isEmpty()) return null;
    	
    	// 각 모뎀별로 통계를 구하여 하나로 묶음.
    	int listSize = fModemList.size();
    	for(int f=0; f<listSize; f++){
    		Object[] fModem = (Object[])fModemList.get(f);
    		result.add(snrDao.getStatisticsByModem(mcuId, isPoor, fModem[0].toString(), fModem[1].toString()).toArray());
    		
    	}
    	
    	return result;
    }
    
    
    /**
     * method Desc : 선택된 모뎀의 시간별 SNR 데이터를 조회한다. (차트,그리드 생성)
     * @param condition
     * @return
     */
    public List<Map<String,Object>> getModemSnrList (Map<String,Object> condition){
    	
    	List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();    	
    	List<Object> mdSnrList = null;
    	    	
    	if(condition.get("isLatest").toString().equals("period")){
    		mdSnrList = snrDao.getSnrChartByModem(condition);
    	}else{
    		mdSnrList = snrDao.getFinalSnrChartByModem(condition);
    	}
    	    	
    	if(mdSnrList.isEmpty()) return null;
    	Map<String,Object> snrList = null;
    	int listSize = mdSnrList.size();
    	for(int i=0; i<listSize; i++){
    		Object[] listA = (Object[])mdSnrList.get(i);
    		snrList = new HashMap<String,Object>();
    		snrList.put("date", TimeUtil.formatDateTime(listA[0].toString()));
    		snrList.put("snr", listA[1]);
    		snrList.put("mcuId", listA[2]);
    		result.add(snrList);
    	}
    	//result.put("result", mdSnrList);
    	
    	return result;
    }
}
