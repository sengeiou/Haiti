package com.aimir.dao.mvm.impl;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.mvm.DisplayChannelDao;
import com.aimir.model.mvm.DisplayChannel;

@Repository(value = "displaychannelDao")
public class DisplayChannelDaoImpl extends AbstractHibernateGenericDao<DisplayChannel, Integer> implements DisplayChannelDao {

	private static Log logger = LogFactory.getLog(DisplayChannelDaoImpl.class);
    
	@Autowired
	protected DisplayChannelDaoImpl(SessionFactory sessionFactory) {
		super(DisplayChannel.class);
		super.setSessionFactory(sessionFactory);
	}
	
	@SuppressWarnings("unchecked")
    public List<DisplayChannel> getFromTableNameByList(HashMap<String, Object> conditions) {
		
		logger.info("===================condition=====>\n " + conditions);
		
		String tlbType  = (String) conditions.get("tlbType");
		
		StringBuffer sb = new StringBuffer();
		sb.append("\n SELECT ID, LOCAL_NAME, NAME, SERVICE_TYPE, UNIT  ");
		sb.append("\n FROM CHANNEL_CONFIG ");
		sb.append("\n WHERE CHANNEL_ID = :tlbType ");
		
		Query query = getSession().createQuery(sb.toString()).setString("tlbType", tlbType);
		
		List<DisplayChannel> result = (List<DisplayChannel>) query.list();
		return result;
	}
}