package com.aimir.service.system.impl.homeDeviceMgmt;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants.DefaultChannel;
import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.constants.CommonConstants.HomeDeviceCategoryType;
import com.aimir.dao.device.EndDeviceDao;
import com.aimir.dao.mvm.DayEMDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.dao.system.GroupDao;
import com.aimir.dao.system.GroupMemberDao;
import com.aimir.dao.system.HomeDeviceDrLevelDao;
import com.aimir.dao.system.HomeGroupDao;
import com.aimir.model.device.EndDevice;
import com.aimir.model.device.MCU;
import com.aimir.model.mvm.DayEM;
import com.aimir.model.system.Contract;
import com.aimir.model.system.HomeDeviceDrLevel;
import com.aimir.model.system.HomeGroup;
import com.aimir.service.system.homeDeviceMgmt.HomeDeviceMgmtManager;
import com.aimir.util.BillDateUtil;
import com.aimir.util.DecimalUtil;

@Service(value="HomeDeviceMgmtManager")
@Transactional(readOnly=false)
public class HomeDeviceMgmtManagerImpl implements HomeDeviceMgmtManager {

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
    DayEMDao dayEMDao;
    
    @Autowired      
    ContractDao contractDao;
    
    @Autowired      
    GroupDao groupDao;
	public List<Map<String, Object>> getHomeDeviceInfo(String groupId, String homeDeviceGroupName, String homeDeviceCategory) {

		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();

		List<Map<String, Object>> list = endDeviceDao.getHomeDeviceInfo(groupId, homeDeviceGroupName, homeDeviceCategory);
 
    	for(Map<String,Object> tmp:list){

    		// 일반 가전일 경우, 맵핑된 스마트 콘센트 정보를 취득한다.
    		int categoryId = codeDao.getCodeIdByCode(HomeDeviceCategoryType.GENERAL_APPLIANCE.getCode());
    		if(categoryId == (Integer)tmp.get("CATEGORYID") && tmp.get("MODEMID") != null) {
        		List<EndDevice> result = endDeviceDao.getMappingHomeDevice((Integer)tmp.get("MODEMID"), codeDao.getCodeIdByCode(HomeDeviceCategoryType.SMART_CONCENT.getCode()));
               	// 이번달 전기 사용량을 취득한다.
        		String usage = this.getEndDeviceEnergyUsage(result.get(0), DeviceType.Modem.name(), Integer.parseInt(groupId));
      
        		tmp.put("MAPPINGID", result.get(0).getId());
        		tmp.put("MAPPINGFRIENDLYNAME", result.get(0).getFriendlyName());
            	tmp.put("MAPPINGIMGURL", result.get(0).getHomeDeviceImgFilename());
            	List<Map<String, Object>> drName = homeDeviceDrLevelDao.getHomeDeviceDrLevelByCondition(result.get(0).getCategoryCode().getId(), result.get(0).getDrLevel().toString());
            	tmp.put("MAPPINGDRNAME", drName.get(0).get("DRNAME"));
            	tmp.put("USAGE", usage);
    		}else {
    			tmp.put("MAPPINGID", "");
        		tmp.put("MAPPINGFRIENDLYNAME", "");
        		tmp.put("MAPPINGIMGURL", "");
        		tmp.put("MAPPINGDRNAME", "");
        		tmp.put("USAGE", "");
    		}
    		resultList.add(tmp);
    	}

		return resultList;
	}

	private String getEndDeviceEnergyUsage(EndDevice endDevice, String mDevType, int homeGroupId) {

		Double usage = 0d;
		// 과금일 정보 취득 위해 계약정보 취득
		Contract contract = contractDao.findByCondition("contractNumber", homeGroupDao.get(homeGroupId).getName());
		DecimalFormat dfMd = DecimalUtil.getDecimalFormat(contract.getSupplier().getMd());
		try{
			DayEM dayEM = new DayEM();	        
	        //String billDate = BillDateUtil.getBillDate(contract, TimeUtil.getCurrentDay(), 0);
	        String billDate = BillDateUtil.getBillDate(contract, "20110401", 0);
	        dayEM.setChannel(DefaultChannel.Usage.getCode());
	        dayEM.setYyyymmdd(billDate);
	        dayEM.setContract(contract);
	        dayEM.setMDevType(mDevType); // Modem 또는 EndDevice

	        List<DayEM> dayEMs = dayEMDao.getDayEMs(dayEM);

			for (DayEM result : dayEMs) {

				usage += (null == result.getTotal() ? 0.0 : result.getTotal());
			}
		}catch(Exception e){
			e.printStackTrace();
		}
        return dfMd.format(usage);    
	}

	public HomeGroup getHomeGroupById(int id){
		return homeGroupDao.get(id);
	}

	public HomeGroup getHomeGroupByMember(String member){
		return homeGroupDao.findByCondition("name", member);
	}
	
	public List<Object> getHomeDeviceGroupSelected(String groupId){
		return endDeviceDao.getHomeDeviceGroupSelected(groupId);
	}
	
	public List<Object> getHomeDeviceCategorySelected(String groupId){
		return endDeviceDao.getHomeDeviceCategorySelected(groupId);
	}
	
	public void updateMappingInfo(int id, int modemId){
		endDeviceDao.updateMappingInfo(id, modemId);
	}

	public int updateGroupMember(int id, String member) throws Exception{
		return groupMemberDao.updateGroupMember(id, member);
	}
	
	public void delete(int memberId) {
		groupMemberDao.deleteById(memberId);
	}
	
	public void updateEndDeviceInfo(int id, String homeDeviceGroupName, String friendlyName){
		endDeviceDao.updateEndDeviceInfo(id, homeDeviceGroupName, friendlyName);
	}
	
	public void resetMappingInfo(int modemId, int categoryId){
		endDeviceDao.resetMappingInfo(modemId, categoryId);
	}

	public List<Map<String, Object>> getHomeDeviceGroupCnt(String groupId){
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		Map<String, Object> resultMap = null;
		int smartConcent = codeDao.getCodeIdByCode(HomeDeviceCategoryType.SMART_CONCENT.getCode());
		int generalAppliance = codeDao.getCodeIdByCode(HomeDeviceCategoryType.GENERAL_APPLIANCE.getCode());

		List<Map<String, Object>> list = endDeviceDao.getHomeDeviceGroupCnt(groupId, smartConcent, generalAppliance);
		String beforeGroupName = (String)list.get(0).get("HOMEDEVICEGROUPNAME");
		int beforeCnt = (Integer)list.get(0).get("CNT");

		for(int i = 1; i<list.size(); i++){
			Map<String, Object> tmp = list.get(i);
			if(!beforeGroupName.equals((String)tmp.get("HOMEDEVICEGROUPNAME"))){
				resultMap = new HashMap<String, Object>();
				resultMap.put("HOMEDEVICEGROUPNAME", beforeGroupName);
				resultMap.put("CNT", beforeCnt);
				resultList.add(resultMap);
			}
			beforeGroupName = (String)tmp.get("HOMEDEVICEGROUPNAME");
			beforeCnt = (Integer)tmp.get("CNT");
		}

		resultMap = new HashMap<String, Object>();
		resultMap.put("HOMEDEVICEGROUPNAME", beforeGroupName);
		resultMap.put("CNT", beforeCnt);
		resultList.add(resultMap);

		return resultList;
	}
	
	public List<Map<String, Object>> getHomeDeviceDrLevelByCondition(int categoryId, String drLevel) {
		return homeDeviceDrLevelDao.getHomeDeviceDrLevelByCondition(categoryId, drLevel);
	}

	public HomeDeviceDrLevel getHomeDeviceDrLevelByDrLevel(String drLevel) {
		return homeDeviceDrLevelDao.findByCondition("drLevel", drLevel);
	}
	
	public List<HomeDeviceDrLevel> getHomeDeviceDrLevel() {
		return homeDeviceDrLevelDao.getAll();
	}
	
	public void resetMappingInfo(int endDeviceId) {
		endDeviceDao.resetMappingInfo(endDeviceId);
	}
	
	public MCU getHomeGroupMcuByGroupId(Integer groupId){
		return homeGroupDao.getHomeGroupMcuByGroupId(groupId);
	}
}
