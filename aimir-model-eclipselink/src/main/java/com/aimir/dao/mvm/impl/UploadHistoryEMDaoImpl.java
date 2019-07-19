package com.aimir.dao.mvm.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.mvm.UploadHistoryEMDao;
import com.aimir.model.mvm.UploadHistoryEM;
import com.aimir.util.Condition;

@Repository(value = "uploadhistoryemDao")
public class UploadHistoryEMDaoImpl extends AbstractJpaDao<UploadHistoryEM, String> implements UploadHistoryEMDao {

	private static Log log = LogFactory.getLog(UploadHistoryEMDaoImpl.class);
	
	public UploadHistoryEMDaoImpl() {
	    super(UploadHistoryEM.class);
	}


	// 주어진 아이디에 해당하는 이력 조회
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	public UploadHistoryEM getUploadHistory(String _id) {
				
		return findByCondition("id",_id);
	}

	// 미터아이디, 로그인아이디, 업로드 기간에 따른 이력 리스트 조회
	public List<UploadHistoryEM> getUploadHistory_List(Map<String, Object> condition) {
		String qstr = "FROM UploadHistoryEM "
				+ " WHERE uploadDate >= :startDate AND uploadDate <= :endDate ";
		
		if(!condition.get("meterId").toString().equals("")){
			qstr = qstr.concat("AND mdsId = :meterId ");
		}
		if(!condition.get("loginId").toString().equals("")){
			qstr = qstr.concat("AND loginId = :loginId ");
		}
		
		qstr = qstr.concat("ORDER BY uploadDate DESC");
		Query query = getEntityManager().createQuery(qstr);
		query.setParameter("startDate", condition.get("startDate").toString());
		query.setParameter("endDate", condition.get("endDate").toString());
		if(!condition.get("meterId").toString().equals("")){
			query.setParameter("meterId", condition.get("meterId").toString());
		}
		if(!condition.get("loginId").toString().equals("")){
			query.setParameter("loginId", condition.get("loginId").toString());
		}
		
		
		
		List<UploadHistoryEM> result = query.getResultList();
		if(result == null || result.size() == 0 ) return null;
		return result;
	}


    @Override
    public Class<UploadHistoryEM> getPersistentClass() {
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
