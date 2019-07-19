package com.aimir.service.mvm.impl;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aimir.util.DecimalUtil;
import com.aimir.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.DataSVC;
import com.aimir.constants.CommonConstants.DisplayType;
import com.aimir.dao.mvm.ChannelConfigDao;
import com.aimir.dao.mvm.DisplayChannelDao;
import com.aimir.dao.system.MeterConfigDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.model.device.Meter;
import com.aimir.model.mvm.ChannelConfig;
import com.aimir.model.mvm.DisplayChannel;
import com.aimir.model.system.DeviceModel;
import com.aimir.model.system.MeterConfig;
import com.aimir.model.system.Supplier;
import com.aimir.service.mvm.ChannelConfigManager;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;

@Service(value = "channelConfigManager")
@Transactional(readOnly = false)
public class ChannelConfigManagerImpl implements ChannelConfigManager {

	@Autowired ChannelConfigDao channelConfigDao;
	@Autowired DisplayChannelDao displayChannelDao;
	@Autowired MeterConfigDao meterConfigDao;
	@Autowired SupplierDao supplierDao;
	
	@Override
	public DisplayChannel getDisplayChannel(
			Integer channelId) {
		DisplayChannel displaychannel = new DisplayChannel();
		
		displaychannel = displayChannelDao.get(channelId);
		return displaychannel;
	}
	 /*
		 *channel conofig에 등록된 channel의 정보를 삭제한다.*/
    @Transactional
    @Override
	public Boolean deleteChannelConfig(Integer meterConfigId,
			List<Object> channelList){
		
		Boolean result	= true;		

		List<ChannelConfig> channalconfigalllist = new ArrayList<ChannelConfig>();
		Set<Condition> set = new HashSet<Condition>();
        set.add(new Condition("meterConfigId",new Object[]{meterConfigId},null,Restriction.EQ));
         
        channalconfigalllist = channelConfigDao.findByConditions(set);
        
        for(ChannelConfig channelConfig : channalconfigalllist){
        	Map<String, Object> channel = new HashMap<String, Object>();
        	boolean check = true;
        	for(int i=0; i<channelList.size();i++){
        		channel = (Map<String, Object>) channelList.get(i);
        		Integer comchannelId = Integer.parseInt(StringUtil.nullToZero(channel.get("id"))); 
        		if((channelConfig.getId()).equals(comchannelId)){
        			check = false;
        		}
        	}
        	if(check){
        		channelConfigDao.delete(channelConfig);
        	}
        }
       
        
		return result;
	}
	 /*
		 *channel conofig에 channel의 정보를 등록한다.*/
    @Transactional
    @Override
	public Boolean addChannelConfig(Map<String, Object> condition){
		
		Boolean result	= true;		
		Integer displaychannelId  = (Integer) condition.get("id");	
		Integer channelIndex  = (Integer) condition.get("index");
		String serviceType   = (String)condition.get("dataType");
		Integer meterconfigId   = (Integer)condition.get("meterconfigId");
		
		DisplayChannel displaychannel = displayChannelDao.findByCondition("id", displaychannelId);
		
		String dateType="EnergyMeter";
		if("EnergyMeter".equals(serviceType)){
			dateType = CommonConstants.MeterType.EnergyMeter.getLpClassName();
		}else if("GasMeter".equals(serviceType)){
			dateType = CommonConstants.MeterType.GasMeter.getLpClassName();
		}else if("WaterMeter".equals(serviceType)){
			dateType = CommonConstants.MeterType.WaterMeter.getLpClassName();
		}else if("HeatMeter".equals(serviceType)){
			dateType = CommonConstants.MeterType.HeatMeter.getLpClassName();
		}else if("SolarPowerMeter".equals(serviceType)){
			dateType = CommonConstants.MeterType.SolarPowerMeter.getLpClassName();
		}else if("VolumeCorrector".equals(serviceType)){
			dateType = CommonConstants.MeterType.VolumeCorrector.getLpClassName();
		}
		
		MeterConfig meterconfig = meterConfigDao.get(meterconfigId);
		ChannelConfig channelconfig = new ChannelConfig();
	
			channelconfig.setChannel(displaychannel);
			channelconfig.setChannelIndex(channelIndex);
			channelconfig.setDataType(dateType);
			channelconfig.setDisplayType(CommonConstants.DisplayType.SaveAndDisplay.toString());
			channelconfig.setMeterConfig(meterconfig);
			channelConfigDao.add(channelconfig);
	
		return result;
	}
    
    /*
	 *channel conofig에 channel index의 정보를 수정한다.*/
    @Transactional
    @Override
	public Boolean updateChannelConfig(Map<String, Object> condition){
			
			Boolean result	= true;		
			Integer id  = (Integer) condition.get("id");	
			Integer channelIndex  = (Integer) condition.get("channelindex");	
			String displayType =(String)condition.get("displayType");
			ChannelConfig channelconfig = channelConfigDao.findByCondition("id", id);
			
			channelconfig.setChannelIndex(channelIndex);
			channelconfig.setDisplayType(displayType);
			channelConfigDao.update(channelconfig);
			
			return result;
	}
	
	/*
	 *channel config에 등록할 diplaychannel List들을 조회한다.*/
	@Override
	public List<Object> getDisplayChannelList(Integer channelid, Integer supplierId) {
		
		List<Object> resultList = new ArrayList<Object>();
		List<DisplayChannel> displaychannellist = new ArrayList<DisplayChannel>();
		
		if(channelid==null){
			displaychannellist = displayChannelDao.getAll();
		}else{
			Set<Condition> set = new HashSet<Condition>();
	        set.add(new Condition("id",new Object[]{channelid},null,Restriction.EQ));
	         
			displaychannellist = displayChannelDao.findByConditions(set);
		}
		
		HashMap<String,Object> resultMap = null;
		int count=0;
		
		Supplier supplier = supplierDao.get(supplierId);
		DecimalFormat dfMd = DecimalUtil.getMDStyle(supplier.getMd());

		for(DisplayChannel displaychannel : displaychannellist){
			resultMap = new HashMap<String,Object>();
			resultMap.put("id", displaychannel.getId());
			resultMap.put("no", dfMd.format(count+1));
			resultMap.put("name", displaychannel.getName());
			resultMap.put("serviceType", displaychannel.getServiceType());
			resultMap.put("unit", displaychannel.getUnit());
			resultMap.put("chmethod", displaychannel.getChMethod());
			resultList.add(count, resultMap);		
			
			count++;
		}
		
		return resultList;
		
	}
	
	/*
	 *channelconfig에 등록된 diplaychannel들을 제외한 channel List들을 조회한다.*/
	@Override
	public List<Object> getExceptDisplayChannelList(Integer id, Integer supplierId) {
		
		List<Object> resultList = new ArrayList<Object>();
		List<ChannelConfig> channalconfiglist = new ArrayList<ChannelConfig>();
		List<DisplayChannel> displaychannellist = new ArrayList<DisplayChannel>();
	//	List<Integer> existId = new ArrayList<Integer>();
		
		Set<Condition> set = new HashSet<Condition>();
        set.add(new Condition("meterConfigId",new Object[]{id},null,Restriction.EQ));
         
        channalconfiglist = channelConfigDao.findByConditions(set);

		displaychannellist = displayChannelDao.getAll();
		
		HashMap<String,Object> resultMap = null;
		int count=0;
		
		Supplier supplier = supplierDao.get(supplierId);
		DecimalFormat dfMd = DecimalUtil.getMDStyle(supplier.getMd());

		for(DisplayChannel displaychannel : displaychannellist){
			
			boolean exists = true;
			for(int j=0; j<channalconfiglist.size();j++){
				if(channalconfiglist.get(j).getChannelId().equals(displaychannel.getId())){
					exists = false;
				}
			}
			if(exists){
			resultMap = new HashMap<String,Object>();
			resultMap.put("id", displaychannel.getId());
			resultMap.put("no", dfMd.format(count+1));
			resultMap.put("name", displaychannel.getName());
			resultMap.put("serviceType", displaychannel.getServiceType());
			resultMap.put("unit", displaychannel.getUnit());
			resultMap.put("chmethod", displaychannel.getChMethod());
			resultList.add(count, resultMap);		
			
			count++;
			}
		}

		return resultList;
		
	}
	
	/*
	 * channel conofig에 channel의 정보를 등록및 수정한다.
	 */
	@Transactional
	@Override
	public Boolean saveOrUpdateChannelConfig(Integer meterConfigId,
			List<Object> channelList) {

		Boolean result = true;

		Map<String, Object> channel = new HashMap<String, Object>();
		
		deleteChannelConfig(meterConfigId, channelList);
		
		for (int i = 0; i < channelList.size(); i++) {
			channel = (Map<String, Object>) channelList.get(i);
			Integer displaychannelId = Integer.parseInt((String) channel
					.get("displayid"));
			Integer channelconfiglId = Integer.parseInt(StringUtil.nullToZero(channel
					.get("id")));
			Integer channelIndex = Integer.parseInt((String) channel
					.get("channelIndex"));
			String displayType = (String) channel.get("displayType");
			String serviceType = (String) channel.get("serviceType");
			DisplayChannel displaychannel = displayChannelDao.findByCondition(
					"id", displaychannelId);

			String dateType = "";
			if ("EnergyMeter".equals(serviceType)) {
				dateType = CommonConstants.MeterType.EnergyMeter
						.getLpClassName();
			} else if ("GasMeter".equals(serviceType)) {
				dateType = CommonConstants.MeterType.GasMeter.getLpClassName();
			} else if ("WaterMeter".equals(serviceType)) {
				dateType = CommonConstants.MeterType.WaterMeter
						.getLpClassName();
			} else if ("HeatMeter".equals(serviceType)) {
				dateType = CommonConstants.MeterType.HeatMeter.getLpClassName();
			} else if ("SolarPowerMeter".equals(serviceType)) {
				dateType = CommonConstants.MeterType.SolarPowerMeter
						.getLpClassName();
			} else if ("VolumeCorrector".equals(serviceType)) {
				dateType = CommonConstants.MeterType.VolumeCorrector
						.getLpClassName();
			}

			MeterConfig meterconfig = meterConfigDao.get(meterConfigId);

	        ChannelConfig channelconfig = null;
			if(channelconfiglId==null || channelconfiglId==0){
				channelconfig = new ChannelConfig();
			}else{
				channelconfig = channelConfigDao.get(channelconfiglId);
			}
		
			channelconfig.setChannel(displaychannel);
			channelconfig.setChannelIndex(channelIndex);
			channelconfig.setDataType(dateType);
			channelconfig.setDisplayType(displayType);
			channelconfig.setMeterConfig(meterconfig);
			channelConfigDao.saveOrUpdate(channelconfig);
		}

		return result;
	}


}
