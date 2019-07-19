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
        StringBuffer sbQuery = new StringBuffer();
        sbQuery.append("UPDATE LpHM SET ")
            .append(" VALUE_00 = ?, ")
            .append(" VALUE_15 = ?, ")
            .append(" VALUE_30 = ?, ")
            .append(" VALUE_45 = ? ")
            .append(" WHERE id.yyyymmddhh = ? ")
            .append(" AND id.channel = ? ")
            .append(" AND id.mdevType = ? ")
            .append(" AND id.mdevId = ? ")
            .append(" AND id.dst = ? ");
    
        //HQL문을 이용한 CUD를 할 경우에는 getHibernateTemplate().bulkUpdate() 메소드를 사용한다.      
        Query query = getSession().createQuery(sbQuery.toString());
        query.setParameter(1, lphm.getValue_00());
        query.setParameter(2, lphm.getValue_15());
        query.setParameter(3, lphm.getValue_30());
        query.setParameter(4, lphm.getValue_45());
        query.setParameter(5, lphm.getId().getYyyymmddhh());
        query.setParameter(6, lphm.getId().getChannel());
        query.setParameter(7, lphm.getId().getMDevType());
        query.setParameter(8, lphm.getId().getMDevId());
        query.setParameter(9, lphm.getId().getDst());
        query.executeUpdate();
        // bulkUpdate 때문에 주석처리
        /*this.getSession().bulkUpdate(sbQuery.toString(),
            new Object[] {lphm.getValue_00(), lphm.getValue_15(), lphm.getValue_30(), lphm.getValue_45()
            , lphm.getId().getYyyymmddhh(), lphm.getId().getChannel(), lphm.getId().getMDevType(), lphm.getId().getMDevId(), lphm.getId().getDst()} );*/
        
    }
}
