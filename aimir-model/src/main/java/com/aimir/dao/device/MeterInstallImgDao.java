package com.aimir.dao.device;

import java.util.List;

import com.aimir.dao.GenericDao;
import com.aimir.model.device.MeterInstallImg;

public interface MeterInstallImgDao extends GenericDao<MeterInstallImg, Long> {
	
	public List<Object> getMeterInstallImgList(Integer meterId);
	
}
