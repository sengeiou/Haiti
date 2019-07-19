package com.aimir.dao.device.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.device.MeterInstallImgDao;
import com.aimir.model.device.MeterInstallImg;

@Repository(value = "meterinstallimgDao")
public class MeterInstallImgDaoImpl  extends AbstractHibernateGenericDao<MeterInstallImg, Long> implements MeterInstallImgDao {

    Log logger = LogFactory.getLog(MeterInstallImgDaoImpl.class);
    
	@Autowired
	protected MeterInstallImgDaoImpl(SessionFactory sessionFactory) {
		super(MeterInstallImg.class);
		super.setSessionFactory(sessionFactory);
	}
	
	
	@SuppressWarnings("unchecked")
	public List<Object> getMeterInstallImgList(Integer meterId){
		
		List<Object> result      = new ArrayList<Object>();
		StringBuffer sbQuery = new StringBuffer();
		
	    sbQuery.append(" SELECT id, CURRENTTIMEMILLISNAME, ORGINALNAME ");
	    sbQuery.append("   FROM MeterInstallImg  ");
	    sbQuery.append("  WHERE METER_ID = :meterId     ");
	    sbQuery.append("  ORDER BY id            ");
	    
		SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
		query.setInteger("meterId", meterId);
		
		// List 조회
		List dataList = null;
		dataList =  query.list();
		
		
		int dataListLen = 0;
		if(dataList != null)
			dataListLen = dataList.size();
		
		for(int i=0 ; i < dataListLen ; i++){
			
			HashMap installImgMap = new HashMap();
			Object[] resultData = (Object[]) dataList.get(i);
			
			installImgMap.put("id", resultData[0]);    			
			installImgMap.put("saveFileName", resultData[1]);
			installImgMap.put("orgFileName", resultData[2]);
			
			result.add(installImgMap);
		}
		
		return result;
	}
	
		
}
