package com.aimir.dao.mvm.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.mvm.LpHMDao;
import com.aimir.model.mvm.LpHM;
import com.aimir.util.Condition;
import com.aimir.util.SearchCondition;

@Repository(value="lphmDao")
public class LpHMDaoImpl extends AbstractHibernateGenericDao<LpHM, Integer> implements LpHMDao{

	@Autowired
	protected LpHMDaoImpl(SessionFactory sessionFactory) {
		super(LpHM.class);
		super.setSessionFactory(sessionFactory);
	}

    
    public List<LpHM> getLpHMsByListCondition(Set<Condition> set) {         
        
        return findByConditions(set);
    }
    
    public List<Object> getLpHMsCountByListCondition(Set<Condition> set) {         
        
        return findTotalCountByConditions(set);
    }
    
	@SuppressWarnings("unchecked")
	public List<Object> getLpHMsMaxMinSumAvg(Set<Condition> conditions, String div) {
		Criteria criteria = getSession().createCriteria(LpHM.class);
		
		 if(conditions != null) {                                
	            Iterator it = conditions.iterator();
	            while(it.hasNext()){
	                Condition condition = (Condition)it.next();
	                Criterion addCriterion = SearchCondition.getCriterion(condition);
	                
	                if(addCriterion != null){
	                    criteria.add(addCriterion);
	                }
	            }
	        }
	        
			ProjectionList pjl = Projections.projectionList();
			
			if("max".equals(div)) {
				pjl.add(Projections.max("value_00"));
			}
			else if("min".equals(div)) {
				pjl.add(Projections.min("value_00"));
			}
			else if("avg".equals(div)) {
				pjl.add(Projections.avg("value_00"));
			}
			else if("sum".equals(div)) {
				pjl.add(Projections.sum("value_00"));
			}
			
			criteria.setProjection(pjl);
			return criteria.list();
	}

	@SuppressWarnings("unchecked")
    public List<Object> getLpHMsByNoSended() {
        StringBuffer sbQuery = new StringBuffer();
        sbQuery.append("SELECT A.CHANNEL, A.YYYYMMDDHH, A.DST, A.MDEV_ID, A.MDEV_TYPE, A.VALUE, A.VALUE_00, A.VALUE_15, A.VALUE_30, A.VALUE_45 ")
            .append(", B.VALUE_00 AS VALUE_00_RESULT , B.VALUE_15 AS VALUE_15_RESULT, B.VALUE_30 AS VALUE_30_RESULT, B.VALUE_45 AS VALUE_45_RESULT ")
            .append("FROM LP_HM A ")
            .append("JOIN (SELECT * FROM LP_HM ")
            .append("WHERE CHANNEL = 98 ")
            .append("AND (VALUE_00 = 0 OR VALUE_15 = 0 OR VALUE_30 = 0 OR VALUE_45 = 0 )) B ")
            .append("ON A.YYYYMMDDHH = B.YYYYMMDDHH ")
            .append("AND A.DST = B.DST ")
            .append("AND A.MDEV_TYPE = B.MDEV_TYPE ")
            .append("AND A.MDEV_ID = B.MDEV_ID ")
            .append("AND A.CHANNEL = 1 ")
            .append("AND A.MDEV_TYPE = ?");
        
        SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
                
        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }
    
    @SuppressWarnings("unchecked")
    public List<Object> getLpHMsByNoSended(String mdevType) {
        StringBuffer sbQuery = new StringBuffer();
        sbQuery.append("SELECT A.CHANNEL, A.YYYYMMDDHH, A.DST, A.MDEV_ID, A.MDEV_TYPE, A.VALUE, A.VALUE_00, A.VALUE_15, A.VALUE_30, A.VALUE_45 ")
            .append(", B.VALUE_00 AS VALUE_00_RESULT , B.VALUE_15 AS VALUE_15_RESULT, B.VALUE_30 AS VALUE_30_RESULT, B.VALUE_45 AS VALUE_45_RESULT ")
            .append("FROM LP_HM A ")
            .append("JOIN (SELECT * FROM LP_HM ")
            .append("WHERE CHANNEL = 98 ")
            .append("AND (VALUE_00 = 0 OR VALUE_15 = 0 OR VALUE_30 = 0 OR VALUE_45 = 0 )) B ")
            .append("ON A.YYYYMMDDHH = B.YYYYMMDDHH ")
            .append("AND A.DST = B.DST ")
            .append("AND A.MDEV_TYPE = B.MDEV_TYPE ")
            .append("AND A.MDEV_ID = B.MDEV_ID ")
            .append("AND A.CHANNEL = 1 ")
            .append("AND A.MDEV_TYPE = ?");
        
        SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
        query.setString(0, mdevType);
                
        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }


    public void updateSendedResult(LpHM lphm) {
    	//OPF-610 정규화 관련 처리로 내용 삭제. 호출 하는 함수 없음
    }
}
