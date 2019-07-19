package com.aimir.dao.mvm.impl;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.mvm.UploadHistoryEM_FailListDao;
import com.aimir.model.mvm.UploadHistoryEM_FailList;
import com.aimir.util.Condition;

@Repository(value = "uploadhistoryem_faillistDao")
public class UploadHistoryEM_FailListDaoImpl extends AbstractJpaDao<UploadHistoryEM_FailList, Integer> implements UploadHistoryEM_FailListDao {
	
	private static Log log = LogFactory.getLog(UploadHistoryEM_FailListDaoImpl.class);
	
	public UploadHistoryEM_FailListDaoImpl() {
	    super(UploadHistoryEM_FailList.class);
	}

	// 주어진 업로드 이력 아이디에 해당하는 실패 리스트 반환
	public List<UploadHistoryEM_FailList> getUploadFailHistory(String _uid) {
		final String qstr = "FROM UploadHistoryEM_FailList "
				+ "WHERE uploadId = :uploadId "
				+ "ORDER BY rowLine ASC ";
		
		Query query = getEntityManager().createQuery(qstr);
		query.setParameter("uploadId", _uid);				
		
		List<UploadHistoryEM_FailList> result = query.getResultList();		
		if(result == null || result.size() == 0) return null;
		return result;
	}

    @Override
    public Class<UploadHistoryEM_FailList> getPersistentClass() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getSumFieldByCondition(Set<Condition> conditions,
            String field, String... groupBy) {
        // TODO Auto-generated method stub
        return null;
    }

}