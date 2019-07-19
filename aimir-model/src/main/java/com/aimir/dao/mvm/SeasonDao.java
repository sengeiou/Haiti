package com.aimir.dao.mvm;

import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.mvm.Season;



public interface SeasonDao extends GenericDao<Season, Integer>{
    
    public List<Season> getSeasons();   
    public List<Season> getSeasonBySmonth(String smonth);
    public List<Season> getSeasonsBySyearIsNull();
    public List<Season> getSeasonsBySyear(String startYear);
    //public List<Integer> getSeasonsIdBySyearNull(Map<String,Object> condition);
    //public List<Integer> getSeasonsIdBySyear(Map<String,Object> condition);
    public List<Object> getSeasonsDateBySyearId(Map<String,Object> condition);
    public List<Object> getSeasonsDateBySyearNullId(Map<String,Object> condition);
    public Season getSeasonByMonth(String month);
    public List<Season> getSeasonIdByYMD(String ymd);
    public List<Season> getSeasonByName(String name);
    public Season getSeasonByYyyyMMdd(String yyyymmdd);    
}
