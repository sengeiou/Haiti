package com.aimir.dao.mvm.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.mvm.VEELogDao;
import com.aimir.model.mvm.VEELog;
import com.aimir.util.Condition;

@Repository(value = "veelogDao")
public class VEELogDaoImpl extends AbstractHibernateGenericDao<VEELog, Integer> implements VEELogDao {
	
//	private static Log logger = LogFactory.getLog(VEELogDaoImpl.class);
	    
	@Autowired
	protected VEELogDaoImpl(SessionFactory sessionFactory) {
		super(VEELog.class);
		super.setSessionFactory(sessionFactory);
	}
	
	public List<VEELog> getVEELogByListCondition(Set<Condition> set) {
		return findByConditions(set);
	}
	
	public List<VEELog> getVEELogByListCondition(Set<Condition> set, int startRow, int pageSize) {
		return findByConditions(set);
	}
	
	//VeeLog 데이터 추출(검침데이터 VEE history max가젯용
	@SuppressWarnings("unchecked")
    public List<Object> getVeeLogByDataList(HashMap<String, Object> condition) {
        Query query = null;
        try {

            String startDate = (String) condition.get("startDate");
            String endDate = (String) condition.get("endDate");
            String tableName = (String) condition.get("tableName");// 'LpEM'
            String contractNo = (String) condition.get("contractNo");// 669810230
            String operator = (String) condition.get("userId");// 1

            StringBuffer sb = new StringBuffer();
            sb.append("\n SELECT ");
            sb.append("\n         A.HH ");// 시각
            sb.append("\n         ,B.CONTRACT_NUMBER ");// 계약번호
            sb.append("\n         ,'' ");// 미터번호
            sb.append("\n         ,D.ADDRESS ");// 소비지역
            sb.append("\n         ,A.EDIT_ITEM ");// 데이터유형
            sb.append("\n         ,A.ATTR_NAME ");// 속성
            sb.append("\n         ,A.AFTER_VALUE ");// 이후값
            sb.append("\n         ,A.BEFORE_VALUE ");// 이전값
            sb.append("\n         ,A.WRITE_DATE ");//
            sb.append("\n         ,A.OPERATOR ");// 사용자
            sb.append("\n         ,A.DESCR ");// 설명
            sb.append("\n         ,A.ID ");
            sb.append("\n         ,A.CONTRACT_ID ");
            sb.append("\n         ,A.MDEV_TYPE ");
            sb.append("\n         ,A.MDEV_ID ");
            sb.append("\n         ,A.DST ");
            sb.append("\n         ,A.RESULT ");
            sb.append("\n         ,A.YYYYMMDD ");// 검침일자
            sb.append("\n         ,A.OPERATOR_TYPE ");
            sb.append("\n         ,D.ID ");
            sb.append("\n         ,A.LOCATION_ID ");// 지역번호
            sb.append("\n         ,A.CHANNEL ");// channel id.
            sb.append("\n         ,A.TABLE_NAME ");// table class name
            sb.append("\n         ,A.SUPPLIER_ID ");// SUPPLIER_ID

            sb.append("\n FROM VEE_LOG A LEFT OUTER JOIN CONTRACT B ON A.CONTRACT_ID = B.ID ");
            sb.append("\n                LEFT OUTER JOIN SUPPLIER D ON A.SUPPLIER_ID = D.ID ");
            sb.append("\n WHERE A.YYYYMMDD >=:startDate ");
            sb.append("\n   AND A.YYYYMMDD <=:endDate ");
            if (tableName.length() > 0) {
                sb.append("\n AND     A.TABLE_NAME = :tableName ");
            }
            if (contractNo != null && contractNo.length() > 0) {
                sb.append("\n AND B.CONTRACT_NUMBER =:contractNo ");
            }

            if (operator != null && operator.length() > 0) {
                sb.append("\n   AND A.OPERATOR=:operator ");
            }
            sb.append("\n ORDER BY   A.HH , B.CONTRACT_NUMBER ");

            query = getSession().createSQLQuery(sb.toString());
            query.setString("startDate", startDate);
            query.setString("endDate", endDate);

            if (tableName.length() > 0) {
                query.setString("tableName", tableName);
            }

            if (contractNo != null && contractNo.length() > 0) {
                query.setString("contractNo", contractNo);
            }
            if (operator != null && operator.length() > 0) {
                query.setString("operator", operator);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return query.list();
    }

	// VEE 미니가젯용
	@SuppressWarnings("unchecked")
    public List<Object> getVeeLogByCountList(HashMap<String, Object> condition) {
		Query query = null;
    	try {
    		
//		String meterType	= (String)condition.get("meterType");
		String startDate	= (String)condition.get("startDate");
		String endDate		= (String)condition.get("endDate");
		String tableName    = (String)condition.get("tableName");
		

//		logger.info("\n====conditions====\n"+condition);

        StringBuffer sb = new StringBuffer();
      
        sb.append("\n SELECT TLB.editItem, COUNT(TLB.id) 	");
        sb.append("\n FROM   VEELog ").append(" TLB");
        sb.append("\n WHERE     1=1          						");
        sb.append("\n AND     TLB.yyyymmdd >= :startDate			");
        sb.append("\n AND     TLB.yyyymmdd <= :endDate				");
        if(tableName.length() > 0) {
        	sb.append("\n AND     TLB.tableName = :tableName		");
        }
        sb.append("\n GROUP BY   TLB.editItem            			");
        sb.append("\n ORDER BY   TLB.editItem            			");

        query = getSession().createQuery(sb.toString());
		query.setString("startDate", startDate);
		query.setString("endDate", endDate);
		if(tableName.length() > 0) {
			query.setString("tableName", tableName);
        }
		

		}catch(Exception e){
			e.printStackTrace();
		}
		
		return query.list(); 
	}
		
}
