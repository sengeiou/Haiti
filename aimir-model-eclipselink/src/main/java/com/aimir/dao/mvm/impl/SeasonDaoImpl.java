package com.aimir.dao.mvm.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.mvm.SeasonDao;
import com.aimir.model.mvm.Season;
import com.aimir.util.Condition;

@Repository(value = "seasonDao")
public class SeasonDaoImpl extends AbstractJpaDao<Season, Integer> implements SeasonDao {

	private static Log logger = LogFactory.getLog(SeasonDaoImpl.class);
    
	public SeasonDaoImpl() {
		super(Season.class);
	}
    
	@SuppressWarnings("unchecked")
    public List<Season> getSeasons(){
	    return getAll();
    }
	
	@SuppressWarnings("unchecked")
    public List<Season> getSeasonBySmonth(String smonth){
        StringBuffer sql = new StringBuffer();     
        sql.append(" select s ");
        sql.append(" FROM Season s ");
        sql.append(" WHERE smonth = :smonth ");
        
        Query query = em.createQuery(sql.toString(), Season.class);
        query.setParameter("smonth", smonth);
        
        return query.getResultList();
    }

    @Override
    public List<Season> getSeasonsBySyearIsNull() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Season> getSeasonsBySyear(String startYear) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getSeasonsDateBySyearId(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getSeasonsDateBySyearNullId(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Season getSeasonByMonth(String month) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Season> getSeasonIdByYMD(String ymd) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Season> getSeasonByName(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Season getSeasonByYyyyMMdd(String yyyymmdd) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<Season> getPersistentClass() {
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