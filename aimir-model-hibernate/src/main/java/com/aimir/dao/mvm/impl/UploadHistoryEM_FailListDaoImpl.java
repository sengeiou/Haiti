package com.aimir.dao.mvm.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.mvm.UploadHistoryEM_FailListDao;
import com.aimir.model.mvm.UploadHistoryEM_FailList;

@Repository(value = "uploadhistoryem_faillistDao")
public class UploadHistoryEM_FailListDaoImpl extends AbstractHibernateGenericDao<UploadHistoryEM_FailList, Integer> implements UploadHistoryEM_FailListDao {
	
	private static Log log = LogFactory.getLog(UploadHistoryEM_FailListDaoImpl.class);
	
	@Autowired
	protected UploadHistoryEM_FailListDaoImpl(SessionFactory sessionFactory) {
		super(UploadHistoryEM_FailList.class);
		super.setSessionFactory(sessionFactory);
	}

	// 주어진 업로드 이력 아이디에 해당하는 실패 리스트 반환
	public List<UploadHistoryEM_FailList> getUploadFailHistory(String _uid) {
		final String qstr = "FROM UploadHistoryEM_FailList "
				+ "WHERE uploadId = :uploadId "
				+ "ORDER BY rowLine ASC ";
		
		Query query = getSession().createQuery(qstr);
		query.setString("uploadId", _uid);				
		
		List<UploadHistoryEM_FailList> result = query.list();		
		if(result == null || result.size() == 0) return null;
		return result;
	}
	
	
}


