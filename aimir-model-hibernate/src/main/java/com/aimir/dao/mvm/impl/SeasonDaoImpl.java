package com.aimir.dao.mvm.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.mvm.SeasonDao;
import com.aimir.model.mvm.Season;
import com.aimir.util.StringUtil;

@Repository(value = "seasonDao")
public class SeasonDaoImpl extends AbstractHibernateGenericDao<Season, Integer> implements SeasonDao {

	private static Log log = LogFactory.getLog(SeasonDaoImpl.class);
    
	@Autowired
	protected SeasonDaoImpl(SessionFactory sessionFactory) {
		super(Season.class);
		super.setSessionFactory(sessionFactory);
	}
    
	@SuppressWarnings("unchecked")
    public List<Season> getSeasons(){
	    StringBuffer queryBuffer = new StringBuffer();	    
	    queryBuffer.append(" FROM Season ");
	    queryBuffer.append(" ORDER BY id ");
        
	    Query query = getSession().createQuery(queryBuffer.toString());
        return query.list();
    }
	
	@SuppressWarnings("unchecked")
    public List<Season> getSeasonBySmonth(String smonth){
        StringBuffer queryBuffer = new StringBuffer();        
        queryBuffer.append(" FROM Season ");
        queryBuffer.append(" WHERE smonth = :smonth ");
        
        Query query = getSession().createQuery(queryBuffer.toString());
        query.setString("smonth", smonth);
        return query.list();
    }
	
	public List<Season> getSeasonIdByYMD(String ymd){

		StringBuffer queryBuffer = new StringBuffer();	    

		queryBuffer.append(" FROM Season ");
		Query query = getSession().createQuery(queryBuffer.toString());
		
	    List<Season> results = query.list();
	    List<Season> ret = new ArrayList<Season>();
		
		for(int i = 0, j = results.size() ; i < j ; i++) {
			
			Season season = (Season)results.get(i);

			String smon = season.getSmonth();
			String sday = season.getSday();
			
			String emon = season.getEmonth();
			String eday = season.getEday();
			
			String cmmdd = ymd.substring(4);
			
			if(cmmdd.compareTo(smon+sday) >= 0 && cmmdd.compareTo(emon+eday) <= 0){
				ret.add(season);
			}
		}
		return ret;
	}
	
    public Season getSeasonByMonth(String month){
    	 List<Season> seasonList = getSeasonsBySyearIsNull();
    	 int inMonth = Integer.parseInt(month);
    	 Season season = null;
    	 for(Season tmpSeason : seasonList){
    		if(Integer.parseInt(tmpSeason.getSmonth()) <= Integer.parseInt(tmpSeason.getEmonth())){
    			if(Integer.parseInt(tmpSeason.getSmonth())<=inMonth && Integer.parseInt(tmpSeason.getEmonth())>=inMonth){
    				season = tmpSeason;
    				break;
    			}
    		}else{
    			if(Integer.parseInt(tmpSeason.getSmonth())<=inMonth || Integer.parseInt(tmpSeason.getEmonth())>=inMonth){
    				season = tmpSeason;
    				break;
    			}
    		}
    	 }
        
        return season;
    }
	
	@SuppressWarnings("unchecked")
    public List<Season> getSeasonsBySyearIsNull(){
	    StringBuffer queryBuffer = new StringBuffer();	    
	    queryBuffer.append(" FROM Season ");
	    queryBuffer.append(" WHERE syear is null ");
	    queryBuffer.append(" ORDER BY smonth ");
        Query query = getSession().createQuery(queryBuffer.toString());
        return query.list();
    }
	
	@SuppressWarnings("unchecked")
    public List<Season> getSeasonsBySyear(String startYear){
	    StringBuffer queryBuffer = new StringBuffer();	    
	    queryBuffer.append(" FROM Season ");
	    queryBuffer.append(" WHERE syear = :startYear ");
	    queryBuffer.append(" ORDER BY smonth ");
        
	    Query query = getSession().createQuery(queryBuffer.toString());
	    query.setString("startYear", startYear);
        return query.list();
    }

	@SuppressWarnings("unchecked")
    public List<Object> getSeasonsDateBySyearId(Map<String,Object> condition){
		String year	  	= (String)condition.get("year");
		Integer seasonId = (Integer)condition.get("seasonId");
		SQLQuery query = null;
		try {
	    StringBuffer sb = new StringBuffer();
	    sb.append(" SELECT SYEAR,SMONTH,SDAY, EYEAR,EMONTH,EDAY");
	    sb.append(" FROM AIMIRSeason ");
	    sb.append(" WHERE syear = :year ");
	    sb.append(" AND  id = :seasonId");	
        sb.append(" ORDER BY id ");
        
        query = getSession().createSQLQuery(sb.toString());
		query.setString("year", year);
		query.setInteger("seasonId", seasonId);
		
		}catch(Exception e){
			e.printStackTrace();
		}
        List<Object> list =  query.list();
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
			
			Object[] obj = new Object[2];
			obj[0] = StringUtil.nullToBlank(syear)+StringUtil.nullToBlank(smonth)+StringUtil.nullToBlank(sday);
			obj[1] = StringUtil.nullToBlank(eyear)+StringUtil.nullToBlank(emonth)+StringUtil.nullToBlank(eday);
			result.add(obj);
		}
		
		return result;		
    }
	
	@SuppressWarnings("unchecked")
    public List<Object> getSeasonsDateBySyearNullId(Map<String,Object> condition){
		Integer seasonId = (Integer)condition.get("seasonId");
        StringBuffer sb = new StringBuffer();
	    sb.append(" SELECT  SMONTH, EMONTH, NAME");
	    sb.append(" FROM AIMIRSeason ");
	    sb.append(" WHERE id = :seasonId ");
        sb.append(" ORDER BY id ");
        
        Query query = getSession().createQuery(sb.toString())        				
        				.setInteger("seasonId", seasonId);
        return query.list();
    }
	
	@SuppressWarnings("unchecked")
	public List<Season> getSeasonByName(String name) {
        StringBuffer sb = new StringBuffer();
	    sb.append("FROM Season WHERE name = :name");
        
        Query query = getSession().createQuery(sb.toString())        				
        				.setString("name", name);
        return query.list();
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public Season getSeasonByYyyyMMdd(String yyyymmdd) {
		Season ret = null;
		String month = yyyymmdd.substring(4, 6);
		Criteria criteria = getSession().createCriteria(Season.class);
		criteria.add(Restrictions.ge("smonth", month));
		criteria.add(Restrictions.le("emonth", month));
		List<Season> seasons = criteria.list();
		
		if ( seasons.size() > 0 ) {
			ret = seasons.get(0);
		} else {
			ret = get(4);
		}
		return ret;
	}
}