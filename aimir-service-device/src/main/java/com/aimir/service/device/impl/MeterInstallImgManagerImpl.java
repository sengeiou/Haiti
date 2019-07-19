package com.aimir.service.device.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.dao.device.DeviceDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.MeterInstallImgDao;
import com.aimir.model.device.MeterInstallImg;
import com.aimir.service.device.MeterInstallImgManager;
import com.aimir.util.StringUtil;

@Service(value="meterInstallImgManager")
@Transactional(readOnly=false)
public class MeterInstallImgManagerImpl implements MeterInstallImgManager {

    @Autowired
    DeviceDao deviceDao;
    
    @Autowired
    MeterDao meterDao;
    
    @Autowired
    MeterInstallImgDao meterInstallImgDao;
    
    
    public List<Object> getMeterInstallImgList(Integer meterId){
    	
    	return meterInstallImgDao.getMeterInstallImgList(meterId);
    }

	public void insertMeterInstallImg(Map<String, Object> condition) {
		
		Integer meterId     = Integer.parseInt(StringUtil.nullToBlank(condition.get("meterId")));
		String orgFileName  = StringUtil.nullToBlank(condition.get("orgFileName"));
		String saveFileName	= StringUtil.nullToBlank(condition.get("saveFileName"));
		
		MeterInstallImg meterInstallImg = new MeterInstallImg();
		meterInstallImg.setCurrentTimeMillisName(saveFileName);
		meterInstallImg.setOrginalName(orgFileName);
		meterInstallImg.setMeterId(meterDao.get(meterId));
		
		meterInstallImgDao.add(meterInstallImg);
		
	}

	public String deleteMeterInstallImg(Integer meterInstallImgId) {
		
		MeterInstallImg meterInstallImg = meterInstallImgDao.get((long) meterInstallImgId);
		
		// 삭제
		meterInstallImgDao.deleteById((long) meterInstallImgId);
		
		return meterInstallImg.getCurrentTimeMillisName();
	}
	
	@SuppressWarnings("unchecked")
	public List<Object> deleteMeterInstallAllImg(Integer meterId){
		List<Object> rtnList = meterInstallImgDao.getMeterInstallImgList(meterId);
		
		
		// 삭제
		int rtnListSize = rtnList.size();
		
		for(int i=0 ; i < rtnListSize ; i++){
			HashMap saveFile = (HashMap) rtnList.get(i);
			String id = saveFile.get("id").toString();
			
			meterInstallImgDao.deleteById(Long.parseLong(id));
		}
		
		
		
		return rtnList;
	}
	
}
