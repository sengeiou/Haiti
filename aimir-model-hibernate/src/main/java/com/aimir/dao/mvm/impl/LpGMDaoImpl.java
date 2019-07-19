package com.aimir.dao.mvm.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import com.aimir.dao.mvm.LpGMDao;
import com.aimir.model.mvm.LpGM;
import com.aimir.util.Condition;
import com.aimir.util.SearchCondition;

@Repository(value="lpgmDao")
public class LpGMDaoImpl extends AbstractHibernateGenericDao<LpGM, Integer> implements LpGMDao{
	private static Log log = LogFactory.getLog(LpGMDaoImpl.class);
	
	@Autowired
	protected LpGMDaoImpl(SessionFactory sessionFactory) {
		super(LpGM.class);
		super.setSessionFactory(sessionFactory);
	}

    
    public List<LpGM> getLpGMsByListCondition(Set<Condition> set) {         
        
        return findByConditions(set);
    }
    
    public List<Object> getLpGMsCountByListCondition(Set<Condition> set) {         
        
        return findTotalCountByConditions(set);
    }
    
	@SuppressWarnings("unchecked")
	public List<Object> getLpGMsMaxMinSumAvg(Set<Condition> conditions, String div) {
		Criteria criteria = getSession().createCriteria(LpGM.class);
		
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
    public List<Object> getLpGMsByNoSended() {
        StringBuffer sbQuery = new StringBuffer();
        sbQuery.append("SELECT A.CHANNEL, A.YYYYMMDDHH, A.DST, A.MDEV_ID, A.MDEV_TYPE, A.VALUE, A.VALUE_00, A.VALUE_15, A.VALUE_30, A.VALUE_45 ")
            .append(", B.VALUE_00 AS VALUE_00_RESULT , B.VALUE_15 AS VALUE_15_RESULT, B.VALUE_30 AS VALUE_30_RESULT, B.VALUE_45 AS VALUE_45_RESULT ")
            .append("FROM LP_GM A ")
            .append("JOIN (SELECT * FROM LP_GM ")
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
    public List<Object> getLpGMsByNoSended(String mdevType) {
        StringBuffer sbQuery = new StringBuffer();
        sbQuery.append("SELECT A.CHANNEL, A.YYYYMMDDHH, A.DST, A.MDEV_ID, A.MDEV_TYPE, A.VALUE, A.VALUE_00, A.VALUE_15, A.VALUE_30, A.VALUE_45 ")
            .append(", B.VALUE_00 AS VALUE_00_RESULT , B.VALUE_15 AS VALUE_15_RESULT, B.VALUE_30 AS VALUE_30_RESULT, B.VALUE_45 AS VALUE_45_RESULT ")
            .append("FROM LP_GM A ")
            .append("JOIN (SELECT * FROM LP_GM ")
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


    public void updateSendedResult(LpGM lpgm) {
        StringBuffer sbQuery = new StringBuffer();
        sbQuery.append("UPDATE LpGM SET ")
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
        query.setParameter(1, lpgm.getValue_00());
        query.setParameter(2, lpgm.getValue_15());
        query.setParameter(3, lpgm.getValue_30());
        query.setParameter(4, lpgm.getValue_45());
        query.setParameter(5, lpgm.getId().getYyyymmddhh());
        query.setParameter(6, lpgm.getId().getChannel());
        query.setParameter(7, lpgm.getId().getMDevType());
        query.setParameter(8, lpgm.getId().getMDevId());
        query.setParameter(9, lpgm.getId().getDst());
        query.executeUpdate();
        // bulkUpdate 때문에 주석처리
        /*this.getSession().bulkUpdate(sbQuery.toString(),
            new Object[] {lpgm.getValue_00(), lpgm.getValue_15(), lpgm.getValue_30(), lpgm.getValue_45()
            , lpgm.getId().getYyyymmddhh(), lpgm.getId().getChannel(), lpgm.getId().getMDevType(), lpgm.getId().getMDevId(), lpgm.getId().getDst()} );*/
        
    }   
    
    public List<Object> getConsumptionGmCo2LpValuesParentId(Map<String, Object> condition) {

		log.info("최상위 위치별 총합\n==== conditions ====\n" + condition);

		@SuppressWarnings("unused")
		Integer locationId = (Integer) condition.get("locationId");
		Integer channel = (Integer) condition.get("channel");
		// 탄소일 경우만 0 ,
		// 수도/온도/습도의
		// 사용량일때는 1
		
		String startDate = (String) condition.get("startDate");
		//String hh0 = (String) condition.get("hh0");

		StringBuffer sb = new StringBuffer();
		
		sb.append("\n 	SELECT SUM(VALUE_00) AS SUMVALUE");		
		sb.append("\n 	FROM LP_GM LP INNER JOIN (SELECT ID FROM LOCATION WHERE PARENT_ID=:parentId) L ");
		sb.append("\n 	ON LP.LOCATION_ID = L.ID ");
		sb.append("\n 	WHERE LP.CHANNEL=:channel AND YYYYMMDDHH =:startDate ");
	
		SQLQuery query = getSession().createSQLQuery(sb.toString());

		query.setInteger("parentId", locationId);
		query.setInteger("channel", channel);

		query.setString("startDate", startDate);

		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
				.list();
	}
    
    public int getLpInterval( String mdevId ) {
		/*
		 * select * from meter m
		 * inner join ( select lp.meter_id from lp_em lp where lp.mdev_id = 
		 */
		
		//주기 ( lpInterval ) 구해오기
		StringBuffer buffer = new StringBuffer();		
		buffer.append("\n SELECT DISTINCT M.LP_INTERVAL FROM METER M ");		
		buffer.append("\n INNER JOIN (SELECT METER_ID FROM LP_EM WHERE MDEV_ID=:mdevId) LP ");
		buffer.append("\n ON M.ID = LP.METER_ID ");
		
		
		SQLQuery query = getSession().createSQLQuery(buffer.toString());
		query.setString("mdevId", mdevId);
		
		List<Object> result = query.list();
		
		return Integer.parseInt(String.valueOf(result.get(0)));
	}
}
