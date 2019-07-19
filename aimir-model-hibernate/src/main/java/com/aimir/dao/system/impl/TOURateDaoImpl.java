package com.aimir.dao.system.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.PeakType;
import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.system.TOURateDao;
import com.aimir.model.system.TOURate;
import com.aimir.util.Condition;
import com.aimir.util.StringUtil;

@Repository(value = "tourateDao")
public class TOURateDaoImpl extends AbstractHibernateGenericDao<TOURate, Integer> implements TOURateDao {
			
    Log logger = LogFactory.getLog(TOURateDaoImpl.class);
    
    @Autowired
    protected TOURateDaoImpl(SessionFactory sessionFactory) {
        super(TOURate.class);
        super.setSessionFactory(sessionFactory);
    }

	@SuppressWarnings("unchecked")
	public Map<Integer, Object> getPeakTimeZone(Map<String, Object> condition) {
		int tariffType  = Integer.parseInt(String.valueOf(condition.get("tariffType")));
		String dateType = (String)condition.get("dateType");
		int season   = Integer.parseInt(String.valueOf(condition.get("season")));
		String month    = condition.get("startDate").toString().substring(4, 6);
		
		StringBuffer sb = new StringBuffer();
		sb.append("\n SELECT	t.tariffType.id as tariffType, ");
		sb.append("\n       	t.peakType as peakType,       ");
		sb.append("\n       	MIN(t.startTime) as startTime, ");
		sb.append("\n       	MAX(t.endTime) as endTime      ");
		sb.append("\n FROM      TOURate t ");
		sb.append("\n WHERE     t.season IN ( ");
		sb.append("\n               SELECT s.id ");  
		sb.append("\n               FROM   Season s ");
		sb.append("\n               WHERE  s.syear IS NULL ");
		if(dateType.equals(CommonConstants.DateType.SEASONAL.getCode())){
			sb.append("\n               AND    s.name = :season ");
		}else{
			sb.append("\n               AND   ((smonth < emonth  AND  smonth <= :month AND emonth >= :month) ");
			sb.append("\n               OR     (smonth > emonth  AND (smonth <= :month OR  emonth >= :month))) ");
		}
		sb.append("\n           ) ");
		if( tariffType > 0 ){
			sb.append("\n AND       t.tariffType.id = :tariffType ");
		}
		sb.append("\n GROUP BY t.peakType, t.tariffType.id ");
		sb.append("\n ORDER BY t.tariffType.id, t.peakType ");

		Query query = getSession().createQuery(sb.toString());
			     				  
		if( tariffType > 0 ){
			query.setInteger("tariffType", tariffType);
		}
		if(dateType.equals(CommonConstants.DateType.SEASONAL.getCode())){
			query.setString("season", CommonConstants.Season.values()[season].getSeason());
		}else{
			query.setString("month", month);
		}

		List<Object> result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

		Map<Integer, Object> returnData = new HashMap<Integer, Object>();
		List<Object> temp = null;
		
		Integer tariffIndex = 0;
		Map<String, Object> row = null;
		for(Object obj:result){
			row = (Map<String, Object>)obj;

			if(tariffIndex.compareTo((Integer)row.get("tariffType")) != 0){
				tariffIndex = (Integer)row.get("tariffType");
				temp = new ArrayList<Object>();
				returnData.put(tariffIndex, temp);
			}
			else{
				temp = (List<Object>)returnData.get(tariffIndex);
			}
			temp.add(row); 
		}
		
		return returnData;
    }
	
	 public List<TOURate> getTOURateByListCondition(Set<Condition> set) {         
        
        return findByConditions(set);
    }
	
	@SuppressWarnings("unchecked")
    public List<Object> getTOURateWithSeasonsBySyear(Map<String,Object> condition){
//		logger.info("\n====conditions====\n"+condition);
		SQLQuery query = null;
		String year	  	= (String)condition.get("year");
		String stdDate	= (String)condition.get("stdDate");
		String endDate	= (String)condition.get("endDate");
		int tariffTypeId  = (Integer)condition.get("tariffTypeId");
		
	    StringBuffer sb = new StringBuffer();
	    
	    sb.append("\n SELECT A.SYEAR,A.SMONTH,A.SDAY,  A.EYEAR,A.EMONTH,A.EDAY, B.LOCAL_NAME, B.START_TIME, B.END_TIME ");
	    //sb.append("       , A.id,A.NAME, B.ID, B.SEASON_ID, B.TARIFFTYPE_ID                                             ");
	    sb.append("\n FROM AIMIRSeason  A, TOU_RATE B                                                                           ");
	    sb.append("\n WHERE A.syear = :year                                                                               ");
	    sb.append("\n AND B.TARIFFTYPE_ID = :tariffTypeId                                                                              ");
	    sb.append("\n AND  A.id = B.SEASON_ID                                                                              ");
	    sb.append("\n ORDER BY A.SYEAR,A.SMONTH,A.SDAY, B.start_time                                                                                        ");
	    // order by 절에 start_time를 해야 off데이터가 제일 마지막에 나와서 in 데이터를 구한 나머지 컬럼을 구할수 있음 (수정하면 안됨)
        query = getSession().createSQLQuery(sb.toString()); 
        query.setString("year", year);
        query.setInteger("tariffTypeId", tariffTypeId);
        query.setString("stdDate", stdDate);
        query.setString("endDate", endDate);
        
        List<Object> list = query.list();
        
        List<Object> result = new ArrayList<Object>();
        
        String syear = "";
        String smonth = "";
        String sday = "";
        String eyear = "";
        String emonth = "";
        String eday = "";
                
		for(int i = 0, j = list.size() ; i < j ; i++) {
			
			Object[] objVal = (Object[])list.get(i);
			syear = (String)objVal[0];
			smonth = (String)objVal[1];
			sday = (String)objVal[2];
			eyear = (String)objVal[3];
			emonth = (String)objVal[4];
			eday = (String)objVal[5];
			
			Object[] obj = new Object[5];
			obj[0] = StringUtil.nullToBlank(syear)+StringUtil.nullToBlank(smonth)+StringUtil.nullToBlank(sday);
			obj[1] = StringUtil.nullToBlank(eyear)+StringUtil.nullToBlank(emonth)+StringUtil.nullToBlank(eday);
			obj[2] = (String)objVal[6];
			obj[3] = (String)objVal[7];
			obj[4] = (String)objVal[8];
			result.add(obj);
		}
		
        return result;
        
    }
	
	@SuppressWarnings("unchecked")
    public List<Object> getTOURateWithSeasonsBySyearNull(Map<String,Object> condition){
//		logger.info("\n====conditions====\n"+condition);
		SQLQuery query = null;
//		String year	  	= (String)condition.get("year");
		String stdDate	= (String)condition.get("stdDate");
		String endDate	= (String)condition.get("endDate");
		int tariffTypeId  = (Integer)condition.get("tariffTypeId");
		
	    StringBuffer sb = new StringBuffer();
	    
	    sb.append("SELECT DISTINCT A.SMONTH,  A.EMONTH, B.LOCAL_NAME, B.START_TIME, B.END_TIME ");
	    //sb.append("       , A.id,A.NAME, B.ID, B.SEASON_ID, B.TARIFFTYPE_ID                                             ");
	    sb.append("FROM AIMIRSeason  A, TOU_RATE B                                                                           ");
	    sb.append("WHERE A.syear is null                                                                               ");
	    sb.append("AND B.TARIFFTYPE_ID = :tariffTypeId                                                                              ");
	    sb.append("AND  A.id = B.SEASON_ID                                                                              ");
	    sb.append("AND (                                                                                                ");
	    sb.append("     ( A.SMONTH <= :stdDate AND A.EMONTH >=:stdDate)        ");
	    sb.append("	    OR                                                                                              ");
	    sb.append("	    ( A.SMONTH <= :endDate AND A.EMONTH >=:endDate )          ");
	    sb.append("	    OR                                                                                              ");
	    sb.append("	    ( A.SMONTH > :stdDate AND A.EMONTH <:endDate )            ");
	    sb.append("	)                                                                                                   ");
	    sb.append("ORDER BY A.SMONTH, B.start_time                                                                                        ");
	    // order by 절에 start_time를 해야 off데이터가 제일 마지막에 나와서 in 데이터를 구한 나머지 컬럼을 구할수 있음 (수정하면 안됨)
        query = getSession().createSQLQuery(sb.toString()); 
        //query.setString("year", year);
        query.setInteger("tariffTypeId", tariffTypeId);
        query.setString("stdDate", stdDate);
        query.setString("endDate", endDate);
        return query.list();
    }

	@SuppressWarnings("unchecked")
    public TOURate getTOURate(Integer tariffTypeId, Integer seasonId, PeakType peakType) {
	    TOURate touRate = null;
		Criteria criteria = getSession().createCriteria(TOURate.class);
		criteria.add(Restrictions.eq("tariffType.id", tariffTypeId));
		criteria.add(Restrictions.eq("season.id", seasonId));
		criteria.add(Restrictions.eq("peakType", peakType));

        List<Object> list = criteria.list();

		if (list != null && list.size() > 0) {
		    touRate = (TOURate)list.get(0);
		}
		return touRate;
	}
	
	public int touDeleteByCondition(Map<String, Object> condition) {
		Query query = null;
		StringBuffer sb = new StringBuffer();
		int result = 0;
		try {
			Integer tariffTypeId = (Integer) condition.get("tariffTypeId");
			
			sb.append("\nDELETE FROM TOURate em WHERE 1=1 ");

			if(tariffTypeId != null) {
				sb.append("\nAND em.tariffTypeId = :tariffTypeId");
			}
			
			query = getSession().createQuery(sb.toString());
			
			if(tariffTypeId != null) {
				query.setInteger("tariffTypeId", tariffTypeId);
			}
			
			result = query.executeUpdate();
			
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e,e);
		}
		return result;
	}
}
