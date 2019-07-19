package com.aimir.dao.device.impl;

import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.device.HeadendCtrlDao;
import com.aimir.model.device.HeadendCtrl;

@Repository(value="headendCtrlDao")
public class HeadendCtrlDaoImpl extends AbstractHibernateGenericDao<HeadendCtrl, Integer> implements HeadendCtrlDao {

	@Autowired
	protected HeadendCtrlDaoImpl(SessionFactory sessionFactory) {
		super(HeadendCtrl.class);
		super.setSessionFactory(sessionFactory);
	}
	
	public List<HeadendCtrl> getHeadendCtrlLastData(Map<String, Object> conditionMap) {

		String ctrlId=conditionMap.get("ctrlId").toString();
		String writeDate=conditionMap.get("writeDate").toString();
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("     SELECT h  \n");
		sb.append("    	FROM HeadendCtrl  h\n");
		sb.append("		WHERE");
		sb.append("    	h.id.ctrlId=:ctrlId AND h.id.writeDate=:writeDate");
    	
    	Query query = getSession().createQuery(sb.toString());
		query.setString("ctrlId", ctrlId);
		query.setString("writeDate", writeDate);

		return query.list();
		
	}
	
	public void insert(HeadendCtrl headendCtrl) {
		StringBuffer sq = new StringBuffer();
		sq.append("    INSERT INTO  \n");
    	sq.append("    MDIS_HEADEND_CTRL \n");
    	sq.append("		(CTRLID, WRITE_DATE, PARAM1, PARAM2, STATUS)");
    	sq.append("    VALUES \n");
    	sq.append("		(:ctrlId, :writeDate, :timeout, :retry, :status)");
    	
    	SQLQuery query = getSession().createSQLQuery(sq.toString());
		query = getSession().createSQLQuery(sq.toString());
		
		query.setString("ctrlId", headendCtrl.id.getCtrlId());
		query.setString("writeDate", headendCtrl.id.getWriteDate());
		query.setString("timeout", headendCtrl.getParam1());
		query.setString("retry", headendCtrl.getParam2());
		query.setInteger("status", headendCtrl.getStatus());
		
		query.executeUpdate();
		
		getSession().flush();
	}
	
}
