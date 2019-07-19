package com.aimir.service.mvm.impl;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.ChangeMeterTypeName;
import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.mvm.DayEMDao;
import com.aimir.dao.mvm.DayGMDao;
import com.aimir.dao.mvm.DayHMDao;
import com.aimir.dao.mvm.DaySPMDao;
import com.aimir.dao.mvm.DayWMDao;
import com.aimir.dao.mvm.LpEMDao;
import com.aimir.dao.mvm.LpGMDao;
import com.aimir.dao.mvm.LpHMDao;
import com.aimir.dao.mvm.LpSPMDao;
import com.aimir.dao.mvm.LpWMDao;
import com.aimir.dao.mvm.MeteringDayDao;
import com.aimir.dao.mvm.MeteringLpDao;
import com.aimir.dao.mvm.MeteringMonthDao;
import com.aimir.dao.mvm.MonthEMDao;
import com.aimir.dao.mvm.MonthGMDao;
import com.aimir.dao.mvm.MonthHMDao;
import com.aimir.dao.mvm.MonthSPMDao;
import com.aimir.dao.mvm.MonthWMDao;
import com.aimir.dao.mvm.SeasonDao;
import com.aimir.dao.system.Co2FormulaDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.dao.system.CustomerDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.dao.system.TariffTypeDao;
import com.aimir.model.device.Meter;
import com.aimir.model.mvm.MeteringDay;
import com.aimir.model.mvm.MeteringLP;
import com.aimir.model.mvm.MeteringMonth;
import com.aimir.model.mvm.Season;
import com.aimir.model.system.Co2Formula;
import com.aimir.model.system.Contract;
import com.aimir.model.system.Customer;
import com.aimir.model.system.Location;
import com.aimir.model.system.Supplier;
import com.aimir.model.system.TariffType;
import com.aimir.service.mvm.SearchMeteringDataManager;
import com.aimir.service.mvm.SeasonManager;
import com.aimir.service.mvm.bean.MeteringListData;
import com.aimir.util.CalendarUtil;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;
import com.aimir.util.DecimalUtil;
import com.aimir.util.SearchCalendarUtil;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeLocaleUtil;
import com.aimir.util.TimeUtil;

@Service(value = "searchMeteringDataManager")
@SuppressWarnings("unchecked")
public class SearchMeteringDataManagerImpl implements SearchMeteringDataManager {

    Log logger = LogFactory.getLog(SearchMeteringDataManagerImpl.class);
    
    @Autowired
    LpEMDao lpEMDao;
    
    @Autowired
    LpGMDao lpGMDao;
    
    @Autowired
    LpHMDao lpHMDao;
    
    @Autowired
    LpWMDao lpWMDao;
    
    @Autowired
    LpSPMDao lpSPMDao;
    
    @Autowired
    DayEMDao dayEMDao;
    
    @Autowired
    DayWMDao dayWMDao;
    
    @Autowired
    DayHMDao dayHMDao;
    
    @Autowired
    DaySPMDao daySPMDao;  
    
    @Autowired
    DayGMDao dayGMDao;
    
    @Autowired
    MonthEMDao monthEMDao;
    
    @Autowired
    MonthGMDao monthGMDao;
    
    @Autowired
    MonthHMDao monthHMDao;
    
    @Autowired
    MonthWMDao monthWMDao;
    
    @Autowired
    MonthSPMDao monthSPMDao;
    
    @Autowired
    CustomerDao customerDao;
    
    @Autowired
    ContractDao contractDao;
    
    @Autowired
    MeterDao meterDao;
    
    @Autowired
    LocationDao locationDao;
    
    @Autowired
    SeasonDao seasonDao;
    
    @Autowired
    TariffTypeDao tariffTypeDao;
    
    @Autowired
    Co2FormulaDao co2formulaDao;

    @Autowired
    MeteringLpDao meteringLpDao;

    @Autowired
    MeteringDayDao meteringdayDao;

    @Autowired
    MeteringMonthDao meteringMonthDao;

    @Autowired
    CodeDao codeDao;
    
    @Autowired
    SupplierDao supplierDao;

    @Autowired
    SeasonManager seasonManager;

    /*
     * 시간별 검색데이타 전체 건수
     */
    @SuppressWarnings("rawtypes")
    public Map<String,String>  getMeteringDataHourTotal(String[] values,String type, String supplierId){
        
        Map result = new HashMap();
        result.put("total", "0");
        Set<Condition> set = new HashSet<Condition>();
        
        for(String value : values) {
            if(value != null){
                Condition ele = getSearchHourCondition(value, supplierId);
                if(ele != null){
                    if(ele.getValue()==null){
                        return result;
                    }
                    set.add(ele);                
                }                
            }
        }
        Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));
        
        set.add(new Condition("supplier",new Object[]{supplier},null,Restriction.EQ));
        set.add(new Condition("id.channel",new Object[]{1},null,Restriction.EQ));
        set.add(new Condition("meter",new Object[]{"m"},null,Restriction.ALIAS));
        
        if ("EM".equals(type)) {
            result.put("total", lpEMDao.getLpEMsCountByListCondition(set));
        } else if ("GM".equals(type)) {
            result.put("total", lpGMDao.getLpGMsCountByListCondition(set));
        } else if ("WM".equals(type)) {
            result.put("total", lpWMDao.getLpWMsCountByListCondition(set));
        } else if ("HM".equals(type)) {
            result.put("total", lpHMDao.getLpHMsCountByListCondition(set));
        }
        
        return result;
    }
    
    /*
     * 일별 검색데이타 전체 건수
     */
    @SuppressWarnings("rawtypes")
    public Map<String,String>  getMeteringDataDayTotal(String[] values,String type, String supplierId){
        Map result = new HashMap();
        result.put("total", "0");
        Set<Condition> set = new HashSet<Condition>();
        
        for(String value : values){
            if(value != null){
                Condition ele = getSearchCondition(value, supplierId);
                if(ele != null){
                    if(ele.getValue()==null){
                        return result;
                    }
                    set.add(ele);                
                }                
            }
        }
        
        set.add(new Condition("id.channel",new Object[]{1},null,Restriction.EQ));
        set.add(new Condition("meter",new Object[]{"m"},null,Restriction.ALIAS));
        
        if ("EM".equals(type)) {
            result.put("total", dayEMDao.getDayEMsCountByListCondition(set));
        } else if ("GM".equals(type)) {
            result.put("total", dayGMDao.getDayGMsCountByListCondition(set));
        } else if ("WM".equals(type)) {
            result.put("total", dayWMDao.getDayWMsCountByListCondition(set));
        } else if ("HM".equals(type)) {
            result.put("total", dayHMDao.getDayHMsCountByListCondition(set));
        }
   
        return result;
    }
    
    /*
     * 요일별 검색데이타 전체 건수
     */
    @SuppressWarnings("rawtypes")
    public Map<String,String>  getMeteringDataDayWeekTotal(String[] values,String type, String supplierId){
       
        Map result = new HashMap();
        result.put("total","0");
        Set<Condition> set = new HashSet<Condition>();
        
        for(String value : values){
            if(value != null){
                Condition ele = getSearchCondition(value, supplierId);
                if(ele != null){
                    if(ele.getValue()==null){
                        return result;
                    }
                    set.add(ele);                
                }                
            }
        }
        
        set.add(new Condition("id.channel",new Object[]{1},null,Restriction.EQ));
        if ("EM".equals(type)) {
            result.put("total", dayEMDao.getTotalGroupByListCondition(set));
        } else if ("GM".equals(type)) {
            result.put("total", dayGMDao.getTotalGroupByListCondition(set));
        } else if ("WM".equals(type)) {
            result.put("total", dayWMDao.getTotalGroupByListCondition(set));
        } else if ("HM".equals(type)) {
            result.put("total", dayHMDao.getTotalGroupByListCondition(set));
        } 
        
        return result;
        
    }
    
    /*
     * 주별 검색데이타 전체 건수
     */
    @SuppressWarnings("rawtypes")
    public Map<String,String>  getMeteringDataWeekTotal(String[] values,String type, String supplierId){
        Map result = new HashMap();
        result.put("total","0");
        int totCount = 0;
        HashMap<String, Object> hm = new HashMap<String, Object>();
        hm = getSearchWeekCondition(values);
        hm.put("channel",1);
        
        
        if ("EM".equals(type)) {
            hm.put("meterType","EM");
            totCount = getWeekEMTotTalCount(hm);
        } else if ("GM".equals(type)) {
            hm.put("meterType","GM");
            totCount = getWeekEMTotTalCount(hm);
        } else if ("WM".equals(type)) {
            hm.put("meterType","WM");
            totCount = getWeekEMTotTalCount(hm);
        } else if ("HM".equals(type)) {
            hm.put("meterType","HM");
            totCount = getWeekEMTotTalCount(hm);
        }
        result.put("total", totCount);
        return result;
    }
    
    /*
     * 월별 검색데이타 전체 건수
     */
    @SuppressWarnings("rawtypes")
    public Map<String,String>  getMeteringDataMonthTotal(String[] values, String type, String supplierId){
        Map result = new HashMap();
        result.put("total","0");
        Set<Condition> set = new HashSet<Condition>();
        
        for(String value : values){
            if(value != null){
                Condition ele = getSearchMonthCondition(value, supplierId);
                if(ele != null){
                    if(ele.getValue()==null){
                        return result;
                    }
                    set.add(ele);                
                }                
            }
        }
        
        set.add(new Condition("id.channel",new Object[]{1},null,Restriction.EQ));
        if ("EM".equals(type)) {
            result.put("total", monthEMDao.getMonthEMsCountByListCondition(set));
        } else if ("GM".equals(type)) {
            result.put("total", monthGMDao.getMonthGMsCountByListCondition(set));
        } else if ("WM".equals(type)) {
            result.put("total", monthWMDao.getMonthWMsCountByListCondition(set));
        } else if ("HM".equals(type)) {
            result.put("total", monthHMDao.getMonthHMsCountByListCondition(set));
        }
   
        return result;
    }
    
    /*
     * 계절별 검색데이타 전체 건수
     */
    @SuppressWarnings("rawtypes")
    public Map<String,String>  getMeteringDataSeasonTotal(String[] values,String type, String supplierId){
        Map result = new HashMap();
        result.put("total","0");
        HashMap<String, Object> hm = new HashMap<String, Object>();
        hm  = getSearchSeasonCondition(values);
        hm.put("channel",1);

        int totCount =0;
        
        if ("EM".equals(type)) {
            hm.put("meterType","EM");
            totCount = getWeekEMTotTalCount(hm);
        } else if ("GM".equals(type)) {
            hm.put("meterType","GM");
            totCount = getWeekEMTotTalCount(hm);
        } else if ("WM".equals(type)) {
            hm.put("meterType","WM");
            totCount = getWeekEMTotTalCount(hm);
        } else if ("HM".equals(type)) {
            hm.put("meterType","HM");
            totCount = getWeekEMTotTalCount(hm);
        }
        result.put("total", totCount);
        return result;
        
    }
    
    /*
     * 시간별 검색데이타
     */
    public List<MeteringListData> getMeteringDataHour(String[] values,String type, String supplierId) {
//        Set<Condition> set = new HashSet<Condition>();
        Set<Condition> set = new LinkedHashSet<Condition>();
        List<MeteringListData> result = new ArrayList<MeteringListData>();
        int firstRow =0;
        
        for(String value : values){
            if(value != null){
                Condition ele = getSearchHourCondition(value, supplierId);
                if(ele != null){
                    if(ele.getValue()==null){
                        return result;
                    }
                    if(ele.getRestriction()==Restriction.FIRST){
                        firstRow = Integer.parseInt(ele.getValue()[0].toString());                        
                    }
                    set.add(ele);                
                }                
            }
        }

        HashMap<String, Object> hm = new HashMap<String, Object>();
        hm.put("firstRow", firstRow);
        hm.put("type",type);
        hm.put("supplierId",supplierId);
        set.add(new Condition("id.channel",new Object[]{1},null,Restriction.EQ));
        set.add(new Condition("meter",new Object[]{"m"},null,Restriction.ALIAS));
        
        result = getHourList(hm,set);

        return result;        
    }
    
    /*
     * 시간별 엑셀 검색데이타
     */
    public List<MeteringListData> getMeteringDataHourExcel(String[] values,String type, String supplierId) {
        Set<Condition> set = new LinkedHashSet<Condition>();
        List<MeteringListData> result = new ArrayList<MeteringListData>();
        int firstRow =0;
        
        for(String value : values){
            if(value != null){
                Condition ele = getSearchHourCondition(value, supplierId);
                if(ele != null){
                    if(ele.getValue()==null){
                        return result;
                    }
                    if(ele.getRestriction()==Restriction.FIRST){
                        continue;                 
                    }
                    if(ele.getRestriction()==Restriction.MAX){
                        continue;                 
                    }
                    set.add(ele);                
                }                
            }
        }

        HashMap<String, Object> hm = new HashMap<String, Object>();
        hm.put("firstRow", firstRow);
        hm.put("type",type);
        hm.put("supplierId",supplierId);
        set.add(new Condition("id.channel",new Object[]{1},null,Restriction.EQ));
        result = getHourList(hm,set);

        return result;        
    }
    /*
     * 일별 검색데이타
     */
    public List<MeteringListData> getMeteringDataDay(String[] values,String type, String supplierId) {
        
        Set<Condition> set = new HashSet<Condition>();
        List<MeteringListData> result = new ArrayList<MeteringListData>();
        int firstRow =0;
        for(String value : values){
            if(value != null){
                Condition ele = getSearchCondition(value, supplierId);
                if(ele != null){
                    if(ele.getValue()==null){
                        return result;
                    }
                    if(ele.getRestriction()==Restriction.FIRST){
                        firstRow = Integer.parseInt(ele.getValue()[0].toString());                        
                    }
                    set.add(ele);                
                }                
            }
        }
        
        HashMap<String, Object> hm = new HashMap<String, Object>();
        hm.put("firstRow", firstRow);
        hm.put("type",type);
        hm.put("supplierId",supplierId);
        set.add(new Condition("id.channel",new Object[]{1},null,Restriction.EQ));
        set.add(new Condition("meter",new Object[]{"m"},null,Restriction.ALIAS));
        result = getDayList(hm,set);

        return result;        
    }
    
    /*
     * 일별 엑셀 검색데이타
     */
    public List<MeteringListData> getMeteringDataDayExcel(String[] values,String type, String supplierId) {
        
        Set<Condition> set = new HashSet<Condition>();
        List<MeteringListData> result = new ArrayList<MeteringListData>();
        int firstRow =0;
        for(String value : values){
            if(value != null){
                Condition ele = getSearchCondition(value, supplierId);
                if(ele != null){
                    if(ele.getValue()==null){
                        return result;
                    }
                    if(ele.getRestriction()==Restriction.FIRST){
                        continue;                 
                    }
                    if(ele.getRestriction()==Restriction.MAX){
                        continue;                 
                    }
                    set.add(ele);                
                }                
            }
        }
        
        HashMap<String, Object> hm = new HashMap<String, Object>();
        hm.put("firstRow", firstRow);
        hm.put("type",type);
        hm.put("supplierId",supplierId);
        set.add(new Condition("id.channel",new Object[]{1},null,Restriction.EQ));
        result = getDayList(hm,set);

        return result;        
    }
    
    /*
     * 요일별 검색데이타
     */      
    public  List<?> getMeteringDataDayWeek(String[] values,String type, String supplierId){
        Set<Condition> set = new HashSet<Condition>();
        List<MeteringListData> result = new ArrayList<MeteringListData>();
        int firstRow =0;
        for(String value : values){
            if(value != null){
                Condition ele = getSearchCondition(value, supplierId);
                if(ele != null){
                    if(ele.getValue()==null){
                        return result;
                    }
                    if(ele.getRestriction()==Restriction.FIRST){
                        firstRow = Integer.parseInt(ele.getValue()[0].toString());                        
                    }
                    
                    set.add(ele);                
                }                
            }
        }
        
        HashMap<String, Object> hm = new HashMap<String, Object>();
        hm.put("firstRow", firstRow);
        hm.put("type",type);
        hm.put("supplierId",supplierId);
        set.add(new Condition("id.channel",new Object[]{1},null,Restriction.EQ));
        result = getDayWeekList(hm,set);

        return result;             
    }
    
    /*
     * 요일별 엑셀 검색데이타
     */      
    public  List<?> getMeteringDataDayWeekExcel(String[] values,String type, String supplierId){
        Set<Condition> set = new HashSet<Condition>();
        List<MeteringListData> result = new ArrayList<MeteringListData>();
        int firstRow =0;
        for(String value : values){
            if(value != null){
                Condition ele = getSearchCondition(value, supplierId);
                if(ele != null){
                    if(ele.getValue()==null){
                        return result;
                    }
                    if(ele.getRestriction()==Restriction.FIRST){
                        continue;                 
                    }
                    if(ele.getRestriction()==Restriction.MAX){
                        continue;                 
                    }
                    set.add(ele);                
                }                
            }
        }
        
        HashMap<String, Object> hm = new HashMap<String, Object>();
        hm.put("firstRow", firstRow);
        hm.put("type",type);
        hm.put("supplierId",supplierId);
        set.add(new Condition("id.channel",new Object[]{1},null,Restriction.EQ));
        result = getDayWeekList(hm,set);

        return result;             
    }
    
    /*
     * 주별 검색데이타
     */
    public  List<?> getMeteringDataWeek(String[] values,String type, String supplierId){
        List<MeteringListData> result = new ArrayList<MeteringListData>();
        HashMap<String, Object> hm = new HashMap<String, Object>();
        hm = getSearchWeekCondition(values);
        
        hm.put("channel",1);
        hm.put("supplierId",supplierId);
        if ("EM".equals(type)) {
            hm.put("meterType","EM");
            result = getWeekEMList(type,hm); 
        } else if ("GM".equals(type)) {
            hm.put("meterType","GM");
            result = getWeekEMList(type,hm);  
        } else if ("WM".equals(type)) {
            hm.put("meterType","WM");
            result = getWeekEMList(type,hm); 
        } else if ("HM".equals(type)) {
            hm.put("meterType","HM");
            result = getWeekEMList(type,hm);
        } else {
        }
        
        return result;       
    }
    
    /*
     * 주별 엑셀 검색데이타
     */
    public  List<?> getMeteringDataWeekExcel(String[] values,String type, String supplierId){
        List<MeteringListData> result = new ArrayList<MeteringListData>();
        HashMap<String, Object> hm = new HashMap<String, Object>();
        hm = getSearchWeekCondition(values);
        //패이징 처리 안함
        hm.put("excel", 1);
        hm.remove("first");
        hm.put("first", 0);
        hm.remove("max");
        
        hm.put("channel",1);
        hm.put("supplierId",supplierId);
        if ("EM".equals(type)) {
            hm.put("meterType","EM");
            result = getWeekEMList(type,hm); 
        } else if ("GM".equals(type)) {
            hm.put("meterType","GM");
            result = getWeekEMList(type,hm);  
        } else if ("WM".equals(type)) {
            hm.put("meterType","WM");
            result = getWeekEMList(type,hm); 
        } else if ("HM".equals(type)) {
            hm.put("meterType","HM");
            result = getWeekEMList(type,hm);
        } else {
        }
        
        return result;       
    }
    /*
     * 월별 검색데이타
     */
    public  List<?> getMeteringDataMonth(String[] values,String type, String supplierId){
        Set<Condition> set = new HashSet<Condition>();
        List<MeteringListData> result = new ArrayList<MeteringListData>();
        int firstRow =0;
        for(String value : values){ // 조건셋팅
            if(value != null){
                Condition ele = getSearchMonthCondition(value, supplierId);
                if(ele != null){
                    if(ele.getValue()==null){
                        return result;
                    }
                    if(ele.getRestriction()==Restriction.FIRST){
                        firstRow = Integer.parseInt(ele.getValue()[0].toString());                        
                    }
                    set.add(ele);                
                }                
            }
        }
        HashMap<String, Object> hm = new HashMap<String, Object>();
        hm.put("firstRow", firstRow);
        hm.put("type",type);
        hm.put("supplierId",supplierId);
        set.add(new Condition("id.channel",new Object[]{1},null,Restriction.EQ));
        result = getMonthList(hm, set);
        
        return result;        
    }
    /*
     * 월별 엑셀 검색데이타
     */
    public  List<?> getMeteringDataMonthExcel(String[] values,String type, String supplierId){
        Set<Condition> set = new HashSet<Condition>();
        List<MeteringListData> result = new ArrayList<MeteringListData>();
        int firstRow =0;
        for(String value : values){ // 조건셋팅
            if(value != null){
                Condition ele = getSearchMonthCondition(value, supplierId);
                if(ele != null){
                    if(ele.getValue()==null){
                        return result;
                    }
                    if(ele.getRestriction()==Restriction.FIRST){
                        continue;                 
                    }
                    if(ele.getRestriction()==Restriction.MAX){
                        continue;                 
                    }
                    set.add(ele);                
                }                
            }
        }
        HashMap<String, Object> hm = new HashMap<String, Object>();
        hm.put("firstRow", firstRow);
        hm.put("type",type);
        hm.put("supplierId",supplierId);
        set.add(new Condition("id.channel",new Object[]{1},null,Restriction.EQ));
        result = getMonthList(hm, set);
        
        return result;        
    }
    
    /*
     * 계절별 검색데이타
     */
    public  List<?>getMeteringDataSeason(String[] values,String type, String supplierId){
        List<MeteringListData> result = new ArrayList<MeteringListData>();
        int firstRow =0;        
        HashMap<String, Object> hm = new HashMap<String, Object>();
        hm = getSearchSeasonCondition(values);
        
        hm.put("channel",1);
        hm.put("supplierId",supplierId);
        if ("EM".equals(type)) {
            hm.put("meterType","EM");
            result = getSeasonEMList(firstRow,type,hm); 
        } else if ("GM".equals(type)) {
            hm.put("meterType","GM");
            result = getSeasonEMList(firstRow,type,hm);  
        } else if ("WM".equals(type)) {
            hm.put("meterType","WM");
            result = getSeasonEMList(firstRow,type,hm); 
        } else if ("HM".equals(type)) {
            hm.put("meterType","HM");
            result = getSeasonEMList(firstRow,type,hm);
        } else {
        }
        
        return result;        
    }
    
    /*
     * 계절별 엑셀 검색데이타
     */
    public  List<?>getMeteringDataSeasonExcel(String[] values,String type, String supplierId){
        List<MeteringListData> result = new ArrayList<MeteringListData>();
        int firstRow =0;        
        HashMap<String, Object> hm = new HashMap<String, Object>();
        hm = getSearchSeasonCondition(values);
        
      //패이징 처리 안함
        hm.put("excel", 1);
        hm.remove("first");
        hm.put("first", 0);
        hm.remove("max");
        
        hm.put("channel",1);
        hm.put("supplierId",supplierId);
        if ("EM".equals(type)) {
            hm.put("meterType","EM");
            result = getSeasonEMList(firstRow,type,hm); 
        } else if ("GM".equals(type)) {
            hm.put("meterType","GM");
            result = getSeasonEMList(firstRow,type,hm);  
        } else if ("WM".equals(type)) {
            hm.put("meterType","WM");
            result = getSeasonEMList(firstRow,type,hm); 
        } else if ("HM".equals(type)) {
            hm.put("meterType","HM");
            result = getSeasonEMList(firstRow,type,hm);
        } else {
        }
        
        return result;        
    }
    /*
     * 시간별데이터 추출
     */
    @SuppressWarnings({"unused", "rawtypes"})
    private List<MeteringListData> getHourList(HashMap<String, Object> hm,Set<Condition> set){        

        Iterator itr = null;

        List<MeteringListData> result = new ArrayList<MeteringListData>();
        int firstRow = (Integer)hm.get("firstRow");
        String type = (String)hm.get("type");
        String supplierId = (String) hm.get("supplierId");
        Double co2StdValue =getTypeToCo2(type);

        Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));
        DecimalFormat df = DecimalUtil.getDecimalFormat(supplier.getMd());

        String lang = supplier.getLang().getCode_2letter();
        String country = supplier.getCountry().getCode_2letter();

        set.add(new Condition("supplier",new Object[]{supplier},null,Restriction.EQ));

        // 정렬
        set.add(new Condition("id.yyyymmddhh",new Object[]{""},null,Restriction.ORDERBYDESC));

        if ("EM".equals(type)) {
            itr = lpEMDao.getLpEMsByListCondition(set).iterator();
        } else if ("GM".equals(type)) {
            itr = lpGMDao.getLpGMsByListCondition(set).iterator();
        } else if ("WM".equals(type)) {
            itr = lpWMDao.getLpWMsByListCondition(set).iterator();
        } else if ("HM".equals(type)) {
            itr = lpHMDao.getLpHMsByListCondition(set).iterator();
        }

        List<MeteringLP> mtlp = new ArrayList<MeteringLP>();
        while (itr.hasNext()) {
            MeteringLP mlp = (MeteringLP) itr.next();
            mtlp.add(mlp);
        }

//        String yyyymmddhh = "";
        for (MeteringLP lp : mtlp) {
            firstRow++;
            MeteringListData mld = new MeteringListData();

            mld.setMeterType(type);

            Set<Condition> conditionList = new HashSet<Condition>();
            mld.setNo(firstRow);

//            yyyymmddhh = lp.getYyyymmddhh();
//            mld.setMeteringTime(yyyymmddhh);
            mld.setMeteringTime(TimeLocaleUtil.getLocaleDateHour(lp.getYyyymmddhh(), lang, country));
            
            Double currHourTotal = getTotalValue(lp);
//            if (lp == null) {
//                mld.setMeteringData("");                
//                mld.setCo2("");
//            } else {
            mld.setMeteringData(df.format(currHourTotal));
            mld.setCo2(df.format(co2StdValue * currHourTotal));
//            }

            Customer customer = null;
            Contract contract = null;
            Meter meter = null;
        
            if (lp.getContract() != null) {
                contract = lp.getContract();
                mld.setcontractNo(contract.getContractNumber());
                if (contract.getCustomer() != null) {
                     customer = lp.getContract().getCustomer();
                     mld.setCustomerNo(customer.getCustomerNo());
                     mld.setCustomerName(customer.getName()); 
                } else {
                    mld.setCustomerNo("");
                    mld.setCustomerName("");
                }
            } else {
                mld.setcontractNo("");
                mld.setCustomerNo("");
                mld.setCustomerName("");
            }

            if (lp.getMeter() != null) {
                meter = lp.getMeter();

                mld.setMeterNo(meter.getMdsId());

//                if (meter.getModem() != null) {
//                    mld.setDeviceSerial(meter.getModem().getDeviceSerial());

//                    if (meter.getModem().getMcu() != null) {
//                        mld.setMcuNo(meter.getModem().getMcu().getSysID());
//                    } else {
//                        mld.setMcuNo("");
//                    }
//                } else {
//                    mld.setMcuNo("");
//                    mld.setDeviceSerial("");
//                }

                if (meter.getModem() == null || meter.getModem().getMcu() == null) {
                    mld.setMcuNo("");
                } else {
                    mld.setMcuNo(meter.getModem().getMcu().getSysID());
                }
                conditionList.add(new Condition("meter.id",new Object[]{meter.getId()},null,Restriction.EQ));

                mld.setFriendlyName(lp.getMeter().getFriendlyName());
                if (meter.getIsManualMeter() != null && meter.getIsManualMeter() > 0) {
                    mld.setIsManual(true);
                } else {
                    mld.setIsManual(false);
                }               
            } else {
                mld.setMeterNo("");
                mld.setMcuNo("");
                mld.setFriendlyName("");
                mld.setIsManual(false);
            }

            conditionList.add(new Condition("id.channel",new Object[]{1},null,Restriction.EQ));
            conditionList.add(new Condition("id.yyyymmddhh",new Object[]{getBeforeHour(lp.getId().getYyyymmddhh())},null,Restriction.EQ));

            Double prevHourTotal = 0d;
            if ("EM".equals(type)) {
                if( (lpEMDao.getLpEMsByListCondition(conditionList).size() > 0) &&
                        (lpEMDao.getLpEMsByListCondition(conditionList).get(0) != null)) {
                    mld.setBeforData(df.format(getTotalValue(lpEMDao.getLpEMsByListCondition(conditionList).get(0))));
                }
                else {
                     mld.setBeforData("");
                }
            } else if ("GM".equals(type)) {
                if( (lpGMDao.getLpGMsByListCondition(conditionList).size() > 0) &&
                        (lpGMDao.getLpGMsByListCondition(conditionList).get(0).getValue() != null)) {
                    mld.setBeforData(df.format(getTotalValue(lpGMDao.getLpGMsByListCondition(conditionList).get(0))));
                }
                else {
                     mld.setBeforData("");
                }
            } else if ("WM".equals(type)) {
                if( (lpWMDao.getLpWMsByListCondition(conditionList).size() > 0) &&
                        (lpWMDao.getLpWMsByListCondition(conditionList).get(0).getValue() != null)) {
                    mld.setBeforData(df.format(getTotalValue(lpWMDao.getLpWMsByListCondition(conditionList).get(0))));
                }
                else {
                     mld.setBeforData("");
                }
            } else if ("HM".equals(type)) {
                if( (lpWMDao.getLpWMsByListCondition(conditionList).size() > 0) &&
                        (lpWMDao.getLpWMsByListCondition(conditionList).get(0).getValue() != null)) {
                    mld.setBeforData(df.format(getTotalValue(lpHMDao.getLpHMsByListCondition(conditionList).get(0))));
                }
                else {
                     mld.setBeforData("");
                }
            }


            if(lp.getLocation()==null){
                mld.setLocationName("");
            }else{
                mld.setLocationName(lp.getLocation().getName());
            }

            mld.setDetailView("");
            mld.setChecked("");
            result.add(mld);
        }

        return result;
    }

    private Double getTotalValue(MeteringLP lp){
        
        if(lp != null){
            
            return 
              StringUtil.nullToDoubleZero(lp.getValue_00())
            + StringUtil.nullToDoubleZero(lp.getValue_01()) 
            + StringUtil.nullToDoubleZero(lp.getValue_02()) 
            + StringUtil.nullToDoubleZero(lp.getValue_03()) 
            + StringUtil.nullToDoubleZero(lp.getValue_04()) 
            + StringUtil.nullToDoubleZero(lp.getValue_05()) 
            + StringUtil.nullToDoubleZero(lp.getValue_06()) 
            + StringUtil.nullToDoubleZero(lp.getValue_07()) 
            + StringUtil.nullToDoubleZero(lp.getValue_08()) 
            + StringUtil.nullToDoubleZero(lp.getValue_09()) 
            + StringUtil.nullToDoubleZero(lp.getValue_10()) 
            + StringUtil.nullToDoubleZero(lp.getValue_11()) 
            + StringUtil.nullToDoubleZero(lp.getValue_12()) 
            + StringUtil.nullToDoubleZero(lp.getValue_13()) 
            + StringUtil.nullToDoubleZero(lp.getValue_14()) 
            + StringUtil.nullToDoubleZero(lp.getValue_15()) 
            + StringUtil.nullToDoubleZero(lp.getValue_16()) 
            + StringUtil.nullToDoubleZero(lp.getValue_17()) 
            + StringUtil.nullToDoubleZero(lp.getValue_18()) 
            + StringUtil.nullToDoubleZero(lp.getValue_19()) 
            + StringUtil.nullToDoubleZero(lp.getValue_20()) 
            + StringUtil.nullToDoubleZero(lp.getValue_21()) 
            + StringUtil.nullToDoubleZero(lp.getValue_22()) 
            + StringUtil.nullToDoubleZero(lp.getValue_23()) 
            + StringUtil.nullToDoubleZero(lp.getValue_24()) 
            + StringUtil.nullToDoubleZero(lp.getValue_25()) 
            + StringUtil.nullToDoubleZero(lp.getValue_26()) 
            + StringUtil.nullToDoubleZero(lp.getValue_27()) 
            + StringUtil.nullToDoubleZero(lp.getValue_28()) 
            + StringUtil.nullToDoubleZero(lp.getValue_29()) 
            + StringUtil.nullToDoubleZero(lp.getValue_30()) 
            + StringUtil.nullToDoubleZero(lp.getValue_31()) 
            + StringUtil.nullToDoubleZero(lp.getValue_32()) 
            + StringUtil.nullToDoubleZero(lp.getValue_33()) 
            + StringUtil.nullToDoubleZero(lp.getValue_34()) 
            + StringUtil.nullToDoubleZero(lp.getValue_35()) 
            + StringUtil.nullToDoubleZero(lp.getValue_36()) 
            + StringUtil.nullToDoubleZero(lp.getValue_37()) 
            + StringUtil.nullToDoubleZero(lp.getValue_38()) 
            + StringUtil.nullToDoubleZero(lp.getValue_39()) 
            + StringUtil.nullToDoubleZero(lp.getValue_40())
            + StringUtil.nullToDoubleZero(lp.getValue_41()) 
            + StringUtil.nullToDoubleZero(lp.getValue_42()) 
            + StringUtil.nullToDoubleZero(lp.getValue_43()) 
            + StringUtil.nullToDoubleZero(lp.getValue_44()) 
            + StringUtil.nullToDoubleZero(lp.getValue_45()) 
            + StringUtil.nullToDoubleZero(lp.getValue_46()) 
            + StringUtil.nullToDoubleZero(lp.getValue_47()) 
            + StringUtil.nullToDoubleZero(lp.getValue_48()) 
            + StringUtil.nullToDoubleZero(lp.getValue_49()) 
            + StringUtil.nullToDoubleZero(lp.getValue_50()) 
            + StringUtil.nullToDoubleZero(lp.getValue_51()) 
            + StringUtil.nullToDoubleZero(lp.getValue_52()) 
            + StringUtil.nullToDoubleZero(lp.getValue_53()) 
            + StringUtil.nullToDoubleZero(lp.getValue_54()) 
            + StringUtil.nullToDoubleZero(lp.getValue_55()) 
            + StringUtil.nullToDoubleZero(lp.getValue_56()) 
            + StringUtil.nullToDoubleZero(lp.getValue_57()) 
            + StringUtil.nullToDoubleZero(lp.getValue_58()) 
            + StringUtil.nullToDoubleZero(lp.getValue_59());
        }else{
            return 0d;
        }
    }
    
    
    /*
     * 일별 데이터
     */
    @SuppressWarnings("rawtypes")
    private List<MeteringListData> getDayList(HashMap<String, Object> hm,Set<Condition> set){
        
        List<MeteringListData> result = new ArrayList<MeteringListData>();
        int firstRow = (Integer)hm.get("firstRow");
        String type = (String)hm.get("type");
        String supplierId = (String) hm.get("supplierId");
        Double co2StdValue =getTypeToCo2(type);
        
        Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));
        DecimalFormat df = DecimalUtil.getDecimalFormat(supplier.getMd());

        String lang = supplier.getLang().getCode_2letter();
        String country = supplier.getCountry().getCode_2letter();
        set.add(new Condition("supplier",new Object[]{supplier},null,Restriction.EQ));
        
     // 정렬
        set.add(new Condition("id.yyyymmdd",new Object[]{""},null,Restriction.ORDERBYDESC));
        
        Iterator itr = null;
        if ("EM".equals(type)) {
            itr = dayEMDao.getDayEMsByListCondition(set).iterator();
        } else if ("GM".equals(type)) {
            itr = dayGMDao.getDayGMsByListCondition(set).iterator();
        } else if ("WM".equals(type)) {
            itr = dayWMDao.getDayWMsByListCondition(set).iterator();
        } else if ("HM".equals(type)) {
            itr = dayHMDao.getDayHMsByListCondition(set).iterator();
        } else if ("SPM".equals(type)) {
            itr = daySPMDao.getDaySPMsByListCondition(set).iterator();
        } 
        
        
        List<MeteringDay> mtDay = new ArrayList<MeteringDay>();
        while (itr.hasNext()) {
            MeteringDay mDay = (MeteringDay) itr.next();
            mtDay.add(mDay);
        }
        
//        String yyyymmdd = "";
        for(MeteringDay day : mtDay){
            firstRow++;
            MeteringListData mld = new MeteringListData();
            
            Double bv = day.getBaseValue();
            if(bv != null) {
                mld.setBaseValue(df.format(day.getBaseValue()));
            }
            else {
                mld.setBaseValue("");
            }
            
            mld.setMeterType(type);
            
            Set<Condition> conditionList = new HashSet<Condition>();
            mld.setNo(firstRow);
            Meter meter = null;
            
            if(day.getContract() != null) {
                Contract contract = day.getContract();
                if(contract.getContractNumber() != null) {
                    mld.setcontractNo(day.getContract().getContractNumber());
                }
                else {
                    mld.setcontractNo("");
                }
                
                if(contract.getCustomer() !=null) {
                    Customer customer = contract.getCustomer();
                    if(customer.getCustomerNo() != null) {
                        mld.setCustomerNo(customer.getCustomerNo());
                    }
                    else {
                        mld.setCustomerNo("");
                    }
                    
                    if(customer.getName() != null ) {
                        mld.setCustomerName(customer.getName());
                    }
                    else {
                        mld.setCustomerName("");
                    }
                    
                }
                else {
                    mld.setCustomerNo("");
                    mld.setCustomerName("");
                }
            }
            else {
                mld.setcontractNo("");
                mld.setCustomerNo("");
                mld.setCustomerName("");
            }
            
//            yyyymmdd = day.getYyyymmdd();
//            mld.setMeteringTime(yyyymmdd);
            mld.setMeteringTime(TimeLocaleUtil.getLocaleDate(day.getYyyymmdd(), lang, country));
            if(day.getTotal()==null){
                mld.setMeteringData("");                
                mld.setCo2("");
            }else{
                mld.setMeteringData(df.format(day.getTotal()));
                mld.setCo2( df.format(co2StdValue * day.getTotal()));
            }
            
            if(day.getMeter() == null){
                mld.setMeterNo("");
                mld.setMcuNo("");
                mld.setFriendlyName("");
                mld.setIsManual(false);
            }
            else {
                meter = day.getMeter();
                conditionList.add(new Condition("meter.id",new Object[]{day.getMeter().getId()},null,Restriction.EQ));
                mld.setMeterNo(meter.getMdsId());
                if(meter.getModem() == null || meter.getModem().getMcu() == null) {
                    mld.setMcuNo("");
                }
                else {
                    mld.setMcuNo(meter.getModem().getMcu().getSysID());                    
                }
                
                mld.setFriendlyName(meter.getFriendlyName());
                if(meter.getIsManualMeter() != null && meter.getIsManualMeter() > 0) {
                    mld.setIsManual(true);
                }
                else {
                    mld.setIsManual(false);
                }
            }
            
            
            conditionList.add(new Condition("id.channel",new Object[]{1},null,Restriction.EQ));
            conditionList.add(new Condition("id.yyyymmdd",new Object[]{getBeforeDay(day.getId().getYyyymmdd())},null,Restriction.EQ));
            if ("EM".equals(type)) {
                if( (dayEMDao.getDayEMsByListCondition(conditionList).size() > 0) &&
                        (dayEMDao.getDayEMsByListCondition(conditionList).get(0).getTotal() != null)) {
                    mld.setBeforData(df.format(dayEMDao.getDayEMsByListCondition(conditionList).get(0).getTotal()));
                }
                else {
                     mld.setBeforData("");
                }
            } else if ("GM".equals(type)) {
                if( (dayGMDao.getDayGMsByListCondition(conditionList).size() > 0) &&
                        (dayGMDao.getDayGMsByListCondition(conditionList).get(0).getTotal() != null)) {
                    mld.setBeforData(df.format(dayGMDao.getDayGMsByListCondition(conditionList).get(0).getTotal()));
                }
                else {
                     mld.setBeforData("");
                }
            } else if ("WM".equals(type)) {
                if( (dayWMDao.getDayWMsByListCondition(conditionList).size() > 0) &&
                        (dayWMDao.getDayWMsByListCondition(conditionList).get(0).getTotal() != null)) {
                    mld.setBeforData(df.format(dayWMDao.getDayWMsByListCondition(conditionList).get(0).getTotal()));
                }
                else {
                     mld.setBeforData("");
                }
            } else if ("HM".equals(type)) {
                if( (dayHMDao.getDayHMsByListCondition(conditionList).size() > 0) &&
                        (dayHMDao.getDayHMsByListCondition(conditionList).get(0).getTotal() != null)) {
                    mld.setBeforData(df.format(dayHMDao.getDayHMsByListCondition(conditionList).get(0).getTotal()));
                }
                else {
                     mld.setBeforData("");
                }
            } 
            
            if(day.getLocation()==null){
                mld.setLocationName("");                
            }else{
                mld.setLocationName(day.getLocation().getName());                
            }
            
            mld.setChecked("");
            mld.setDetailView("");
            result.add(mld);
        }

        return result;
    }

    /*
     * 요일별 데이터
     */
    @SuppressWarnings("rawtypes")
    private List<MeteringListData> getDayWeekList(HashMap<String, Object> hm, Set<Condition> set) {

        List<MeteringListData> result = new ArrayList<MeteringListData>();
        int firstRow = (Integer) hm.get("firstRow");
        String type = (String) hm.get("type");
        String supplierId = (String) hm.get("supplierId");
        Double co2StdValue = getTypeToCo2(type);

        // SearchCalendarUtil scu = new SearchCalendarUtil();

        Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));
        DecimalFormat df = DecimalUtil.getDecimalFormat(supplier.getMd());

        String lang = supplier.getLang().getCode_2letter();
        String country = supplier.getCountry().getCode_2letter();

//        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyyMMdd");
//        SimpleDateFormat sdfDayFormat = null;
//        StringBuilder sbPattern = new StringBuilder();

//        sbPattern.append(TimeLocaleUtil.getDateFormat(8, lang, country).replace('m', 'M'));
//        sbPattern.append(" E");
//        sdfDayFormat = new SimpleDateFormat(sbPattern.toString(), new Locale(lang, country));

        // String[] strDayWeek = {"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};
        set.add(new Condition("supplier",new Object[]{supplier},null,Restriction.EQ));
        Iterator itr = null;
        if ("EM".equals(type)) {
            itr = dayEMDao.getDayEMsByListCondition(set).iterator();
        } else if ("GM".equals(type)) {
            itr = dayGMDao.getDayGMsByListCondition(set).iterator();
        } else if ("WM".equals(type)) {
            itr = dayWMDao.getDayWMsByListCondition(set).iterator();
        } else if ("HM".equals(type)) {
            itr = dayHMDao.getDayHMsByListCondition(set).iterator();
        }

        List<MeteringDay> mtDay = new ArrayList<MeteringDay>();
        while (itr.hasNext()) {
            MeteringDay mDay = (MeteringDay) itr.next();
            mtDay.add(mDay);
        }

        // String yyyymmdd = "";
        for (MeteringDay day : mtDay) {
            firstRow++;
            MeteringListData mld = new MeteringListData();
            mld.setNo(firstRow);
            Meter meter = day.getMeter();
            Contract contract = day.getContract();
            if (contract != null) {
                mld.setcontractNo(contract.getContractNumber());
            } else {
                mld.setcontractNo("");
            }
            Customer customer = day.getContract() == null ? null : day.getContract().getCustomer();

            if (customer != null) {
                mld.setCustomerNo(customer.getCustomerNo());
                mld.setCustomerName(customer.getName());
            } else {
                mld.setCustomerNo("");
                mld.setCustomerName("");
            }

            // yyyymmdd = day.getYyyymmdd();
            // mld.setMeteringTime( yyyymmdd.substring(0,4) + "/" + yyyymmdd.substring(4,6) + "/" + yyyymmdd.substring(6,8)
            // +" "+ strDayWeek[(scu.getDateTodayWeekNum(yyyymmdd) - 1)].toString());
            // mld.setMeteringTime( yyyymmdd + " " + strDayWeek[(scu.getDateTodayWeekNum(yyyymmdd) - 1)].toString());
            // mld.setMeteringTime(sdfDayFormat.format(sdfDate.parse(day.getYyyymmdd())));
            mld.setMeteringTime(TimeLocaleUtil.getLocaleWeekDay(day.getYyyymmdd(), lang, country));

            if (day.getTotal() == null) {
                mld.setMeteringData("");
                mld.setCo2("");
            } else {
                mld.setMeteringData(df.format(day.getTotal()));
                mld.setCo2(df.format(co2StdValue * day.getTotal()));
            }
            Set<Condition> conditionList = new HashSet<Condition>();
            conditionList.add(new Condition("id.channel", new Object[] { 1 }, null, Restriction.EQ));

            if (day.getMeter() != null) {
                conditionList.add(new Condition("meter.id", new Object[] { day.getMeter().getId() }, null, Restriction.EQ));
            }
            conditionList.add(new Condition("id.yyyymmdd", new Object[] { getBeforeDay(day.getId().getYyyymmdd()) }, null,
                    Restriction.EQ));
            if ("EM".equals(type)) {
                if ((dayEMDao.getDayEMsByListCondition(conditionList).size() > 0)
                        && (dayEMDao.getDayEMsByListCondition(conditionList).get(0).getTotal() != null)) {
                    mld.setBeforData(df.format(dayEMDao.getDayEMsByListCondition(conditionList).get(0).getTotal()));
                } else {
                    mld.setBeforData("");
                }
            } else if ("GM".equals(type)) {
                if ((dayGMDao.getDayGMsByListCondition(conditionList).size() > 0)
                        && (dayGMDao.getDayGMsByListCondition(conditionList).get(0).getTotal() != null)) {
                    mld.setBeforData(df.format(dayGMDao.getDayGMsByListCondition(conditionList).get(0).getTotal()));
                } else {
                    mld.setBeforData("");
                }
            } else if ("WM".equals(type)) {
                if ((dayWMDao.getDayWMsByListCondition(conditionList).size() > 0)
                        && (dayWMDao.getDayWMsByListCondition(conditionList).get(0).getTotal() != null)) {
                    mld.setBeforData(df.format(dayWMDao.getDayWMsByListCondition(conditionList).get(0).getTotal()));
                } else {
                    mld.setBeforData("");
                }
            } else if ("HM".equals(type)) {
                if ((dayHMDao.getDayHMsByListCondition(conditionList).size() > 0)
                        && (dayHMDao.getDayHMsByListCondition(conditionList).get(0).getTotal() != null)) {
                    mld.setBeforData(df.format(dayHMDao.getDayHMsByListCondition(conditionList).get(0).getTotal()));
                } else {
                    mld.setBeforData("");
                }
            }

            if (meter == null) {
                mld.setMeterNo("");
                mld.setMcuNo("");
            } else {
                mld.setMeterNo(meter.getMdsId());
                if (meter.getModem() == null || meter.getModem().getMcu() == null) {
                    mld.setMcuNo("");
                } else {
                    mld.setMcuNo(meter.getModem().getMcu().getSysID());
                }
            }

            if (day.getLocation() == null) {
                mld.setLocationName("");
            } else {
                mld.setLocationName(day.getLocation().getName());
            }
            mld.setChecked("");
            mld.setDetailView("");
            result.add(mld);
        }

        return result;
    }
    
    // 주별 데이터 조회
    private List<MeteringListData> getWeekEMList(String type, HashMap<String, Object> hm){
        List<MeteringListData> result = new ArrayList<MeteringListData>();
        List<Object> days = meteringdayDao.getMeteringDayMaxGadgetWeekDataList(hm);   
        Double co2StdValue =getTypeToCo2(type);
        
        String yyyymmdd = (String)hm.get("startDate");
        int firstRow = (Integer)hm.get("first");        
        int channel =1;         
        Double beforeData = 0D;
        
        String supplierId = (String)hm.get("supplierId");
        Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));
        DecimalFormat df = DecimalUtil.getDecimalFormat(supplier.getMd());
        
        for(Object day : days){
            MeteringListData mld = new MeteringListData();
            
            Object[] obj         = (Object[]) day;
            
            int meteringTime    = Integer.parseInt(obj[0].toString());
            String contractNo       = StringUtil.nullToBlank(obj[1]);
            String customerName     = StringUtil.nullToBlank(obj[2]);
            
            String mddata = obj[3]==null?"0.0":obj[3].toString();
            Double meteringData     = Double.valueOf( mddata);
            
            String mcuNo            = StringUtil.nullToBlank(obj[4]);
            String meterNo          = StringUtil.nullToBlank(obj[5]);
            String locationName     = StringUtil.nullToBlank(obj[6]);
            
            firstRow++;
            mld.setNo(firstRow);
            mld.setMeteringTime(meteringTime+" Week");
            mld.setcontractNo(StringUtil.nullToBlank(contractNo));
            mld.setCustomerName(StringUtil.nullToBlank(customerName));
            mld.setMeteringData(df.format(meteringData));
            mld.setCo2(df.format(co2StdValue * meteringData));
            mld.setMeterNo(StringUtil.nullToBlank(meterNo));
            mld.setMcuNo(StringUtil.nullToBlank(mcuNo));
            mld.setLocationName(StringUtil.nullToBlank(locationName));
            mld.setMeterType(type);
            mld.setCustomerNo("");
            
            // 첫주의 이전 데이터는 존재하지 않음
            if (meteringTime > 1) {
                beforeData = getBeforeMeteringData(yyyymmdd, channel, String.valueOf(meteringTime -1), contractNo, type, "W");
                if(beforeData != null && beforeData > 0) {
                    mld.setBeforData(df.format(beforeData));
                }
                else {
                    mld.setBeforData("");
                }
            }
            else {
                mld.setBeforData("");
            }
            mld.setDetailView("");
            mld.setChecked("");
            result.add(mld);
        }

        return result;
    }
    // 주별/계절별  이전 데이터 추출
    public double getBeforeMeteringData (String yyyymmdd, Integer channel, String WeekNum, String contractNo,String type, String div) {
        Double result = 0D;
        SearchCalendarUtil scu = new SearchCalendarUtil();
        int intNum = Integer.parseInt(WeekNum);
        
        String [] compDate = new String[2];
        if ("W".equals(div)) {
            String yyyymm = yyyymmdd.substring(0, 6);
            List<String> dateList = scu.getWeekNumToDateList(yyyymm, String.valueOf(intNum));
            for (int i = 0; i < dateList.size(); i++) {
                compDate[i] = dateList.get(i);
            }
        }
        else {
            String yyyy = yyyymmdd.substring(0, 4);
            compDate = getBeforeSeasonDate(yyyy, intNum);
        }
        
        Set<Condition> set = new HashSet<Condition>();
        set.add(new Condition("contractNumber",new Object[]{contractNo},null,Restriction.EQ));
        List<Contract> contractList = contractDao.getContractByListCondition(set);
        if(contractList != null && contractList.size() > 0) {
        
            Set<Condition> conditionList = new HashSet<Condition>();
            conditionList.add(new Condition("id.channel",new Object[]{channel},null,Restriction.EQ));
            conditionList.add(new Condition("contract.id",new Object[]{contractList.get(0).getId()},null,Restriction.EQ));
            conditionList.add(new Condition("id.yyyymmdd",compDate,null,Restriction.BETWEEN));
            
            if("EM".equals(type)) {
                result = (Double)dayEMDao.getDayEMsMaxMinAvgSum(conditionList,"sum").get(0);
            }
            else if("WM".equals(type)) {
                if(dayWMDao.getDayWMsMaxMinAvgSum(conditionList,"sum") != null && dayWMDao.getDayWMsMaxMinAvgSum(conditionList,"sum").size() > 0) {
                    result = (Double)dayWMDao.getDayWMsMaxMinAvgSum(conditionList,"sum").get(0);
                }
            }
            else if("GM".equals(type)) {
                if(dayGMDao.getDayGMsMaxMinAvgSum(conditionList,"sum") != null && dayGMDao.getDayGMsMaxMinAvgSum(conditionList,"sum").size() > 0) {
                    result = (Double)dayGMDao.getDayGMsMaxMinAvgSum(conditionList,"sum").get(0);
                }
            }
            else if("HM".equals(type)) {
                if(dayHMDao.getDayHMsMaxMinAvgSum(conditionList,"sum") != null && dayHMDao.getDayHMsMaxMinAvgSum(conditionList,"sum").size() > 0) {
                    result = (Double)dayHMDao.getDayHMsMaxMinAvgSum(conditionList,"sum").get(0);
                }
            }
        }
        if (result == null) {
            result = 0D;
        }
        
        return result;
    }
    
 // 주별  총 건수 조회
    private Integer getWeekEMTotTalCount(HashMap<String, Object> hm){
        hm.put("totalFlag","Y");
        List<Object> days = meteringdayDao.getMeteringDayMaxGadgetWeekDataList(hm);   
        int result = days.size();
        return result;
    }
    
    // 연별  총 건수 조회
    private Integer getYearEMTotTalCount(HashMap<String, Object> hm){
        hm.put("totalFlag","Y");
        List<Object> years = meteringdayDao.getMeteringDayMaxGadgetYearDataList(hm);   
        int result = years.size();
        return result;
    }
    
    // 월별데이터
    @SuppressWarnings("rawtypes")
    private List<MeteringListData> getMonthList(HashMap<String, Object> hm, Set<Condition> set) {

        List<MeteringListData> result = new ArrayList<MeteringListData>();
        int firstRow = (Integer) hm.get("firstRow");
        String type = (String) hm.get("type");
        String supplierId = (String) hm.get("supplierId");

        Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));
        DecimalFormat df = DecimalUtil.getDecimalFormat(supplier.getMd());

        String lang = supplier.getLang().getCode_2letter();
        String country = supplier.getCountry().getCode_2letter();
        set.add(new Condition("supplier",new Object[]{supplier},null,Restriction.EQ));
        Double co2StdValue = getTypeToCo2(type);
        Iterator itr = null;
        if ("EM".equals(type)) {
            itr = monthEMDao.getMonthEMsByListCondition(set).iterator();
        } else if ("GM".equals(type)) {
            itr = monthGMDao.getMonthGMsByListCondition(set).iterator();
        } else if ("WM".equals(type)) {
            itr = monthWMDao.getMonthWMsByListCondition(set).iterator();
        } else if ("HM".equals(type)) {
            itr = monthHMDao.getMonthHMsByListCondition(set).iterator();
        }
        List<MeteringMonth> mtMonth = new ArrayList<MeteringMonth>();
        while (itr.hasNext()) {
            MeteringMonth mMonth = (MeteringMonth) itr.next();
            mtMonth.add(mMonth);
        }
//        String yyyymm = "";

        for (MeteringMonth month : mtMonth) {
            firstRow++;
            MeteringListData mld = new MeteringListData();
            mld.setNo(firstRow);
            Contract contract = month.getContract();
            if (contract != null) {
                mld.setcontractNo(contract.getContractNumber());
            } else {
                mld.setcontractNo("");
            }
            Customer customer = month.getContract() == null ? null : month.getContract().getCustomer();
            Meter meter = month.getMeter();
            if (customer != null) {
                mld.setCustomerNo(customer.getCustomerNo());
                mld.setCustomerName(customer.getName());
            } else {
                mld.setCustomerNo("");
                mld.setCustomerName("");
            }
//            yyyymm = month.getYyyymm();
//            mld.setMeteringTime(yyyymm);
            mld.setMeteringTime(TimeLocaleUtil.getLocaleYearMonth(month.getYyyymm(), lang, country));

            if (month.getTotal() == null) {
                mld.setMeteringData("");
                mld.setCo2("");
            } else {
                mld.setMeteringData(df.format(month.getTotal()));
                mld.setCo2(df.format(co2StdValue * month.getTotal()));
            }
            Set<Condition> conditionList = new HashSet<Condition>();

            conditionList.add(new Condition("id.channel", new Object[] { 1 }, null, Restriction.EQ));

            if (month.getMeter() != null) {
                conditionList.add(new Condition("meter.id", new Object[] { month.getMeter().getId() }, null, Restriction.EQ));
            }
            conditionList.add(new Condition("id.yyyymm", new Object[] { getBeforeMonth(month.getId().getYyyymm()) }, null,
                    Restriction.EQ));

            if ("EM".equals(type)) {
                if ((monthEMDao.getMonthEMsByListCondition(conditionList).size() > 0)
                        && (monthEMDao.getMonthEMsByListCondition(conditionList).get(0).getTotal() != null)) {
                    mld.setBeforData(df.format(monthEMDao.getMonthEMsByListCondition(conditionList).get(0).getTotal()));
                } else {
                    mld.setBeforData("");
                }
            } else if ("GM".equals(type)) {
                if ((monthGMDao.getMonthGMsByListCondition(conditionList).size() > 0)
                        && (monthGMDao.getMonthGMsByListCondition(conditionList).get(0).getTotal() != null)) {
                    mld.setBeforData(df.format(monthGMDao.getMonthGMsByListCondition(conditionList).get(0).getTotal()));
                } else {
                    mld.setBeforData("");
                }
            } else if ("WM".equals(type)) {
                if ((monthWMDao.getMonthWMsByListCondition(conditionList).size() > 0)
                        && (monthWMDao.getMonthWMsByListCondition(conditionList).get(0).getTotal() != null)) {
                    mld.setBeforData(df.format(monthWMDao.getMonthWMsByListCondition(conditionList).get(0).getTotal()));
                } else {
                    mld.setBeforData("");
                }
            } else if ("HM".equals(type)) {
                if ((monthHMDao.getMonthHMsByListCondition(conditionList).size() > 0)
                        && (monthHMDao.getMonthHMsByListCondition(conditionList).get(0).getTotal() != null)) {
                    mld.setBeforData(df.format(monthHMDao.getMonthHMsByListCondition(conditionList).get(0).getTotal()));
                } else {
                    mld.setBeforData("");
                }
            }

            if (meter == null) {
                mld.setMeterNo("");
                mld.setMcuNo("");
            } else {
                mld.setMeterNo(meter.getMdsId());
                if (meter.getModem() == null || meter.getModem().getMcu() == null) {
                    mld.setMcuNo("");
                } else {
                    mld.setMcuNo(meter.getModem().getMcu().getSysID());
                }
            }

            if (month.getLocation() == null) {
                mld.setLocationName("");
            } else {
                mld.setLocationName(month.getLocation().getName());
            }
            mld.setChecked("");
            mld.setDetailView("");
            result.add(mld);
        }

        return result;
    }
    
    // 계절별 데이터
    private List<MeteringListData> getSeasonEMList(int firstRow,String type, HashMap<String, Object> hm){

        List<MeteringListData> result = new ArrayList<MeteringListData>();
        
        String supplierId = (String)hm.get("supplierId");
        int first = Integer.parseInt( hm.get("first").toString() );
        Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));
        
        // 공급지역 검색 조건 시 - 최하위 노드 값 조회 및 설정
        if(hm.get("locationId") != null && !(String.valueOf(hm.get("locationId"))).trim().equals("")) {
            List<Integer> locations = locationDao.getLeafLocationId(Integer.parseInt(String.valueOf(hm.get("locationId"))), Integer.parseInt(String.valueOf(hm.get("supplierId"))));            
            hm.put("sLocations", locations);
        }
        
        
        List<Object> days = meteringdayDao.getMeteringDayMaxGadgetWeekDataList(hm);   
        Double co2StdValue =getTypeToCo2(type);
        String[] seasonName = {"Spring","Summer","Autumn","Winter"};
        
        String yyyymmdd = (String)hm.get("startDate");
        int channel =1;
        Double beforeData = 0D;
        
        
        
        DecimalFormat df = DecimalUtil.getDecimalFormat(supplier.getMd());
        
        for(Object day : days){
            MeteringListData mld = new MeteringListData();
            Object[] obj         = (Object[]) day;
            
            int meteringTime    = Integer.parseInt(obj[0].toString());
            String contractNo       = (String)obj[1];
            String customerName     = (String)obj[2];
            Double meteringData     = Double.valueOf(obj[3].toString());
            String mcuNo            = (String)obj[4];
            String meterNo          = (String)obj[5];
            String locationName         = (String)obj[6];
            firstRow++;
            
            mld.setNo(first + firstRow);
            mld.setMeteringTime(seasonName[meteringTime -1]);
            mld.setcontractNo(StringUtil.nullToBlank(contractNo));
            mld.setCustomerName(StringUtil.nullToBlank(customerName));
            mld.setMeteringData(df.format(meteringData));
            mld.setCo2(df.format(co2StdValue * meteringData));
            mld.setMeterNo(StringUtil.nullToBlank(meterNo));
            mld.setMcuNo(StringUtil.nullToBlank(mcuNo));
            mld.setLocationName(StringUtil.nullToBlank(locationName));
            mld.setCustomerNo("");
            
            // 첫 데이터의 이전데이터는 존재하지 않음
            if (meteringTime > 1) {
                beforeData = getBeforeMeteringData(yyyymmdd, channel, String.valueOf(meteringTime -1), contractNo, type,"");
                if(beforeData != null && beforeData > 0) {
                    mld.setBeforData(df.format(beforeData));
                }
                else {
                    mld.setBeforData("");
                }
            }
            else {
                mld.setBeforData("");
            }
            mld.setDetailView("");
            
            result.add(mld);
        }

        return result;
    }
    
 // Year Data
    @SuppressWarnings("unused")
    private List<MeteringListData> getYearEMList(int firstRow,String type, HashMap<String, Object> hm){

        List<MeteringListData> result = new ArrayList<MeteringListData>();
        
        String supplierId = (String)hm.get("supplierId");
        int first = Integer.parseInt( hm.get("first").toString() );
        Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));
        
        // 공급지역 검색 조건 시 - 최하위 노드 값 조회 및 설정
        if(hm.get("locationId") != null && !(String.valueOf(hm.get("locationId"))).trim().equals("")) {
            List<Integer> locations = locationDao.getLeafLocationId(Integer.parseInt(String.valueOf(hm.get("locationId"))), Integer.parseInt(String.valueOf(hm.get("supplierId"))));            
            hm.put("sLocations", locations);
        }
        
        
        List<Object> days = meteringdayDao.getMeteringDayMaxGadgetYearDataList(hm);   
        Double co2StdValue =getTypeToCo2(type);
        
        String yyyymmdd = (String)hm.get("startDate");
        int channel =1;
        Double beforeData = 0D;
        
        
        
        DecimalFormat df = DecimalUtil.getDecimalFormat(supplier.getMd());
        
        for(Object day : days){
            MeteringListData mld = new MeteringListData();
            Object[] obj         = (Object[]) day;
            
//            int meteringTime  = Integer.parseInt(obj[0].toString());
            String contractNo       = (String)obj[0];
            String customerName     = (String)obj[1];
            Double meteringData     = Double.valueOf(obj[2].toString());
            String mcuNo            = (String)obj[3];
            String meterNo          = (String)obj[4];
            String locationName         = (String)obj[5];
            firstRow++;
            
            mld.setNo(first + firstRow);
            mld.setMeteringTime(yyyymmdd.substring(0, 4));
            mld.setcontractNo(StringUtil.nullToBlank(contractNo));
            mld.setCustomerName(StringUtil.nullToBlank(customerName));
            mld.setMeteringData(df.format(meteringData));
            mld.setCo2(df.format(co2StdValue * meteringData));
            mld.setMeterNo(StringUtil.nullToBlank(meterNo));
            mld.setMcuNo(StringUtil.nullToBlank(mcuNo));
            mld.setLocationName(StringUtil.nullToBlank(locationName));
            mld.setCustomerNo("");
            
            // 첫 데이터의 이전데이터는 존재하지 않음
//            if (meteringTime > 1) {
//              beforeData = getBeforeMeteringData(yyyymmdd, channel, String.valueOf(meteringTime -1), contractNo, type,"");
//              if(beforeData != null && beforeData > 0) {
//                  mld.setBeforData(df.format(beforeData));
//              }
//              else {
//                  mld.setBeforData("");
//              }
//            }
//            else {
//              mld.setBeforData("");
//            }
            mld.setDetailView("");
            
            result.add(mld);
        }

        return result;
    }
    
    private Condition getSearchHourCondition(String conditions, String supplierId){
        Condition condition = new Condition();
        String[] stCondition = conditions.split(":");
        String field = null;
        Object[] value = new Object[1];
       
        Restriction restrict = null;
        
        if(stCondition.length < 2 || stCondition[1].trim().equals("")){
            return null;
        }
        
        if(stCondition[0].equals("customer_number")){
            restrict = Restriction.IN;
            field = "contract.id";            
            List<Object> list = contractDao.getContractIdByContractNoLike(stCondition[1]);
            
            int i=0;
            value = new Object[list.size()];
            
            for(Object con:list){
                value[i] = ((Contract)con).getId();
                i++;
            }
            
            if(list.size()==0){
                condition.setValue(null);
                                
            }else{
                condition.setValue(value);
            }
            
        }else if(stCondition[0].equals("customer_name")){
            restrict = Restriction.IN;
            field = "contract.id";            
            List<Object> list = contractDao.getContractIdByCustomerName(stCondition[1]);
            
            int i=0;
            value = new Object[list.size()];
            
            for(Object con:list){
                value[i] = con;
                i++;
            }
            
            if(list.size()==0){
                condition.setValue(null);
                                
            }else{
                condition.setValue(value);
            }
        }else if(stCondition[0].equals("contractGroup")){
            restrict = Restriction.IN;
            field = "contract.id";            
            List<Object> list = contractDao.getContractIdByGroup(stCondition[1]);
            
            int i=0;
            value = new Object[list.size()];
            
            for(Object con:list){
                value[i] = con;
                i++;
            }
            
            if(list.size()==0){
                condition.setValue(null);
                                
            }else{
                condition.setValue(value);
            }
        }else if(stCondition[0].equals("meteringSF")){
            if(stCondition[1].equals("s")){                    
                restrict = Restriction.NOTNULL;
            }else{
                restrict = Restriction.NULL;                
            }
            field = "value";
            value[0]="";
            condition.setValue(value);
        }
        else if(stCondition[0].equals("location")){
            restrict = Restriction.IN;
            field = "location.id";
            
            List<Integer> list = locationDao.getLeafLocationId(Integer.parseInt(stCondition[1]), Integer.parseInt(supplierId));
            
            int i=0;
            value = new Object[list.size()];
            
            for(Object con:list){
                value[i] = con;
                i++;
            }
            
            if(list.size()==0){
                condition.setValue(null);
                                
            }else{
                condition.setValue(value);
            }
        }else if(stCondition[0].equals("customer_type")){            
            restrict = Restriction.IN;
            field = "contract.id";            
            List<Object> list = contractDao.getContractIdByTariffIndex(Integer.parseInt(stCondition[1]));
            
            int i=0;
            value = new Object[list.size()];
            
            for(Object con:list){
                value[i] = con;
                i++;
            }
            
            if(list.size()==0){
                condition.setValue(null);
                                
            }else{
                condition.setValue(value);
            }
        }else if(stCondition[0].equals("mcu_id")){
            restrict = Restriction.IN;
            field = "meter.id";            
            List<Object> list = meterDao.getMetersByMcuName(stCondition[1]);
            
            int i=0;
            value = new Object[list.size()];
            
            for(Object con:list){
                value[i] = con;
                i++;
            }
            
            if(list.size()==0){
                condition.setValue(null);
                                
            }else{
                condition.setValue(value);
            }           
            
        }
        else if(stCondition[0].equals("search_from")){
            String[] searchDate = stCondition[1].split("@");
            restrict = Restriction.BETWEEN;
            field = "id.yyyymmddhh";
            if(searchDate.length != 0) {
                condition.setValue(searchDate);
            } else {
                SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
                String[] hourlyDate = {df.format(new Date()) + "00", df.format(new Date()) + "23"};
                searchDate = hourlyDate;
                condition.setValue(searchDate);
            }
            
        }
        else if(stCondition[0].equals("is_manual_metering")){
            restrict = Restriction.EQ;
            field = "m.isManualMeter";            
            value[0] = Integer.valueOf(stCondition[1]);
            condition.setValue(value);
        }
        
        else if(stCondition[0].equals("meter_id")){
            restrict = Restriction.IN;
            field = "m.id";            
            Meter meter = meterDao.findByCondition("mdsId", stCondition[1]);
            
            if(meter == null) {
                condition.setValue(null);
            }
            else {
                value[0] = meter.getId();
                condition.setValue(value);
            }
        }
        else if(stCondition[0].equals("friendly_name")){
            restrict = Restriction.EQ;
            field = "m.friendlyName";            
            value[0] = stCondition[1].toString();
            condition.setValue(value);
        }
        else if(stCondition[0].equals("first")){
            restrict = Restriction.FIRST;
            field = "";
            value[0] = Integer.parseInt(stCondition[1]);
            condition.setValue(value);
        }else if(stCondition[0].equals("max")){
            restrict = Restriction.MAX;
            field = "";
            value[0] = Integer.parseInt(stCondition[1]);
            condition.setValue(value);
        }else if(stCondition[0].equals("device_type")){
            restrict = Restriction.EQ;
            field = "id.mdevType";
            value[0] = CommonConstants.DeviceType.valueOf(stCondition[1]);
            condition.setValue(value);
        }else if(stCondition[0].equals("mdev_id")){
            restrict = Restriction.EQ;
            field = "id.mdevId";
            value[0] = stCondition[1].toString();
            condition.setValue(value);
        }else if(stCondition[0].equals("customType")){      // SIC(산업분류코드)
            restrict = Restriction.IN;
            field = "contract.id";

            List<Object> contracts = contractDao.getContractBySicId(Integer.parseInt(stCondition[1]));

            if (contracts == null || contracts.size() <= 0) {
                condition.setValue(null);
            } else {
                Set<Integer> set = new HashSet<Integer>();

                for (Object obj : contracts) {
                    set.add(((Contract)obj).getId());
                }

                value = new Object[set.size()];
                set.toArray(value);
                condition.setValue(value);
            }
        }else {
            condition.setValue(null);
        }
        
        condition.setRestrict(restrict);
        condition.setField(field);
        
        return condition;
    }
    
    private Condition getSearchCondition(String conditions, String supplierId){
        Condition condition = new Condition();
        String[] stCondition = conditions.split(":");
        String field = null;
        Object[] value = new Object[1];
        Restriction restrict = null;
        
        if(stCondition.length < 2 || stCondition[1].trim().equals("")){
            return null;
        }
        
        if(stCondition[0].equals("customer_number")){
            restrict = Restriction.IN;
            field = "contract.id";            
            List<Object> list = contractDao.getContractIdByContractNoLike(stCondition[1]);
            
            int i=0;
            value = new Object[list.size()];
            
            for(Object con:list){
                value[i] = ((Contract)con).getId();
                i++;
            }
            
            if(list.size()==0){
                condition.setValue(null);
                                
            }else{
                condition.setValue(value);
            }
            
        }else if(stCondition[0].equals("customer_name")){
            restrict = Restriction.IN;
            field = "contract.id";            
            List<Object> list = contractDao.getContractIdByCustomerName(stCondition[1]);
            
            int i=0;
            value = new Object[list.size()];
            
            for(Object con:list){
                value[i] = con;
                i++;
            }
            
            if(list.size()==0){
                condition.setValue(null);
                                
            }else{
                condition.setValue(value);
            }
        }else if(stCondition[0].equals("contractGroup")){
            restrict = Restriction.IN;
            field = "contract.id";            
            List<Object> list = contractDao.getContractIdByGroup(stCondition[1]);
            
            int i=0;
            value = new Object[list.size()];
            
            for(Object con:list){
                value[i] = con;
                i++;
            }
            
            if(list.size()==0){
                condition.setValue(null);
                                
            }else{
                condition.setValue(value);
            }
        }else if(stCondition[0].equals("meteringSF")){
            if(stCondition[1].equals("s")){                    
                restrict = Restriction.NOTNULL;
            }else{
                restrict = Restriction.NULL;                
            }
            field = "total";
            value[0]="";
            condition.setValue(value);
        }else if(stCondition[0].equals("search_from")){
            String[] searchDate = stCondition[1].split("@");
            restrict = Restriction.BETWEEN;
            field = "id.yyyymmdd";
            condition.setValue(searchDate);
        }else if(stCondition[0].equals("location")){
            restrict = Restriction.IN;
            field = "location.id";
            
            List<Integer> list = locationDao.getLeafLocationId(Integer.parseInt(stCondition[1]), Integer.parseInt(supplierId));
            
            int i=0;
            value = new Object[list.size()];
            
            for(Object con:list){
                value[i] = con;
                i++;
            }
            
            if(list.size()==0){
                condition.setValue(null);
                                
            }else{
                condition.setValue(value);
            }
        }else if(stCondition[0].equals("customer_type")){
            restrict = Restriction.IN;
            field = "contract.id";            
            List<Object> list = contractDao.getContractIdByTariffIndex(Integer.parseInt(stCondition[1]));
            
            int i=0;
            value = new Object[list.size()];
            
            for(Object con:list){
                value[i] = con;
                i++;
            }
            
            if(list.size()==0){
                condition.setValue(null);
                                
            }else{
                condition.setValue(value);
            }
        }else if(stCondition[0].equals("mcu_id")){
            restrict = Restriction.IN;
            field = "meter.id";            
            List<Object> list = meterDao.getMetersByMcuName(stCondition[1]);
            
            int i=0;
            value = new Object[list.size()];
            
            for(Object con:list){
                value[i] = con;
                i++;
            }
            
            if(list.size()==0){
                condition.setValue(null);
                                
            }else{
                condition.setValue(value);
            }           
            
        }
        else if(stCondition[0].equals("is_manual_metering")){
            restrict = Restriction.EQ;
            field = "m.isManualMeter";            
            value[0] = Integer.valueOf(stCondition[1]);
            condition.setValue(value);
        }
        
        else if(stCondition[0].equals("meter_id")){
            restrict = Restriction.IN;
            field = "m.id";            
            Meter meter = meterDao.findByCondition("mdsId", stCondition[1]);
            
            if(meter == null) {
                condition.setValue(null);
            }
            else {
                value[0] = meter.getId();
                condition.setValue(value);
            }
        }
        else if(stCondition[0].equals("friendly_name")){
            restrict = Restriction.EQ;
            field = "m.friendlyName";            
            value[0] = stCondition[1].toString();
            condition.setValue(value);
        }
        else if(stCondition[0].equals("first")){
            restrict = Restriction.FIRST;
            field = "";
            value[0] = Integer.parseInt(stCondition[1]);
            condition.setValue(value);
        }else if(stCondition[0].equals("max")){
            restrict = Restriction.MAX;
            field = "";
            value[0] = Integer.parseInt(stCondition[1]);
            condition.setValue(value);
        }else if(stCondition[0].equals("device_type")){
            restrict = Restriction.EQ;
            field = "id.mdevType";
            value[0] = CommonConstants.DeviceType.valueOf(stCondition[1]);
            condition.setValue(value);

        }else if(stCondition[0].equals("mdev_id")){
            restrict = Restriction.EQ;
            field = "id.mdevId";
            value[0] = stCondition[1].toString();
            condition.setValue(value);
        }else if(stCondition[0].equals("customType")){      // SIC(산업분류코드)
            restrict = Restriction.IN;
            field = "contract.id";

            List<Object> contracts = contractDao.getContractBySicId(Integer.parseInt(stCondition[1]));

            if (contracts == null || contracts.size() <= 0) {
                condition.setValue(null);
            } else {
                Set<Integer> set = new HashSet<Integer>();

                for (Object obj : contracts) {
                    set.add(((Contract)obj).getId());
                }

                value = new Object[set.size()];
                set.toArray(value);
                condition.setValue(value);
            }
        }
        
        condition.setRestrict(restrict);
        condition.setField(field);
        
        return condition;
    }

   
    // 주별데이터 추출
    public HashMap<String, Object> getSearchWeekCondition(String[] values){
        HashMap<String, Object> hm = new HashMap<String, Object>();
        
        for(String value : values){
            if(value != null){
                
                String[] stCondition = value.split(":");
                if(stCondition.length < 2 || stCondition[1].trim().equals("")){
                    continue;
                }
                else {
                    
                
                    if(stCondition[0].equals("customer_number")){
                        hm.put("contractNumber", StringUtil.nullToBlank(stCondition[1]));
                    }else if(stCondition[0].equals("customer_name")){
                        hm.put("customer_name", StringUtil.nullToBlank(stCondition[1]));
                    }
                    
                    else if(stCondition[0].equals("contractGroup")){
                        hm.put("contractGroup", StringUtil.nullToBlank(stCondition[1]));
                    }
                    
                    else if(stCondition[0].equals("meteringSF")){
                        hm.put("meteringSF", StringUtil.nullToBlank(stCondition[1]));
                    }else if(stCondition[0].equals("customer_type")){
                        hm.put("tariffType", Integer.parseInt(StringUtil.nullCheck(stCondition[1],"0")));
                    }else if(stCondition[0].equals("search_from")){
                        String[] searchDate = stCondition[1].split("@");
                        hm.put("startDate", searchDate[0]);
                        String endDate = CalendarUtil.getMonthLastDate( searchDate[0].substring(0, 4), searchDate[0].substring(4, 6));
                        hm.put("endDate",  endDate);
                        String[] dateStartEnd = getYyMmDdToWeekDate(searchDate[0]);
                        hm.put("arrStdEndDate", dateStartEnd);
                    }else if(stCondition[0].equals("location")){
                        hm.put("locationId", Integer.parseInt(StringUtil.nullCheck(stCondition[1],"0")));
                    }else if(stCondition[0].equals("mcu_id")){
                        hm.put("mcuId", stCondition[1]);
                    }else if(stCondition[0].equals("meter_id")){
                        Meter meter = meterDao.findByCondition("mdsId", stCondition[1]);
                        
                        if(meter == null){
                            hm.put("mdsId", null);
                                            
                        }else{
                            hm.put("mdsId", value);
                        }
                    }else if(stCondition[0].equals("first")){
                        hm.put("first", Integer.parseInt(StringUtil.nullCheck(stCondition[1],"0")));
                    }else if(stCondition[0].equals("max")){
                        hm.put("max", Integer.parseInt(StringUtil.nullCheck(stCondition[1],"0")));
                    }else if(stCondition[0].equals("device_type")){
                        hm.put("deviceType", StringUtil.nullCheck(stCondition[1],""));
                    }else if(stCondition[0].equals("mdev_id")){
                        hm.put("mdevId", StringUtil.nullCheck(stCondition[1],""));
                    }else if(stCondition[0].equals("customType")){
                        hm.put("customType", StringUtil.nullCheck(stCondition[1],""));
                    }
                
                }
                
            }
        }
        
        return hm;
    }
    
    private Condition getSearchMonthCondition(String conditions, String supplierId){
        Condition condition = new Condition();
        String[] stCondition = conditions.split(":");
        String field = null;
        Object[] value = new Object[1];
        Restriction restrict = null;
        
        if(stCondition.length < 2 || stCondition[1].trim().equals("")){
            return null;
        }
        
        if(stCondition[0].equals("customer_number")){
            restrict = Restriction.IN;
            field = "contract.id";            
            List<Object> list = contractDao.getContractIdByContractNoLike(stCondition[1]);
            
            int i=0;
            value = new Object[list.size()];
            
            for(Object con:list){
                value[i] = ((Contract)con).getId();
                i++;
            }
            
            if(list.size()==0){
                condition.setValue(null);
                                
            }else{
                condition.setValue(value);
            }
            
        }else if(stCondition[0].equals("customer_name")){
            restrict = Restriction.IN;
            field = "contract.id";            
            List<Object> list = contractDao.getContractIdByCustomerName(stCondition[1]);
            
            int i=0;
            value = new Object[list.size()];
            
            for(Object con:list){
                value[i] = con;
                i++;
            }
            
            if(list.size()==0){
                condition.setValue(null);
                                
            }else{
                condition.setValue(value);
            }
        }else if(stCondition[0].equals("contractGroup")){
            restrict = Restriction.IN;
            field = "contract.id";            
            List<Object> list = contractDao.getContractIdByGroup(stCondition[1]);
            
            int i=0;
            value = new Object[list.size()];
            
            for(Object con:list){
                value[i] = con;
                i++;
            }
            
            if(list.size()==0){
                condition.setValue(null);
                                
            }else{
                condition.setValue(value);
            }
        }else if(stCondition[0].equals("meteringSF")){
            if(stCondition[1].equals("s")){                    
                restrict = Restriction.NOTNULL;
            }else{
                restrict = Restriction.NULL;                
            }
            field = "total";
            value[0]="";
            condition.setValue(value);
        }else if(stCondition[0].equals("search_from")){
            String[] tmpDate = stCondition[1].split("@");
            String[] searchDate =new String[2];
            searchDate[0] = tmpDate[0].substring(0, 6);
            searchDate[1] = tmpDate[1].substring(0, 6);
            restrict = Restriction.BETWEEN;
            field = "id.yyyymm";
            condition.setValue(searchDate);
            
        }else if(stCondition[0].equals("location")){
            restrict = Restriction.IN;
            field = "location.id";
            
            List<Integer> list = locationDao.getLeafLocationId(Integer.parseInt(stCondition[1]), Integer.parseInt(supplierId));
            
            int i=0;
            value = new Object[list.size()];
            
            for(Object con:list){
                value[i] = con;
                i++;
            }
            
            if(list.size()==0){
                condition.setValue(null);
                                
            }else{
                condition.setValue(value);
            }
        }else if(stCondition[0].equals("customer_type")){
            restrict = Restriction.EQ;
            field = "contract.tariffIndex.id";
            value[0] = Integer.parseInt(stCondition[1]);
            condition.setValue(value);
        }else if(stCondition[0].equals("mcu_id")){
            restrict = Restriction.IN;
            field = "meter.id";            
            List<Object> list = meterDao.getMetersByMcuName(stCondition[1]);
            
            int i=0;
            value = new Object[list.size()];
            
            for(Object con:list){
                value[i] = con;
                i++;
            }
            
            if(list.size()==0){
                condition.setValue(null);
                                
            }else{
                condition.setValue(value);
            }           
            
        }else if(stCondition[0].equals("meter_id")){
            restrict = Restriction.IN;
            field = "meter.id";            
            Meter meter = meterDao.findByCondition("mdsId", stCondition[1]);
            
            if(meter == null){
                condition.setValue(null);
                                
            }else{
                condition.setValue(value);
            }
        }else if(stCondition[0].equals("first")){
            restrict = Restriction.FIRST;
            field = "";
            value[0] = Integer.parseInt(stCondition[1]);
            condition.setValue(value);
        }else if(stCondition[0].equals("max")){
            restrict = Restriction.MAX;
            field = "";
            value[0] = Integer.parseInt(stCondition[1]);
            condition.setValue(value);
        }else if(stCondition[0].equals("device_type")){
            restrict = Restriction.EQ;
            field = "id.mdevType";
            value[0] = CommonConstants.DeviceType.valueOf(stCondition[1]);
            condition.setValue(value);

        }else if(stCondition[0].equals("mdev_id")){
            restrict = Restriction.EQ;
            field = "id.mdevId";
            value[0] = stCondition[1].toString();
            condition.setValue(value);
        }else if(stCondition[0].equals("customType")){      // SIC(산업분류코드)
            restrict = Restriction.IN;
            field = "contract.id";

            List<Object> contracts = contractDao.getContractBySicId(Integer.parseInt(stCondition[1]));

            if (contracts == null || contracts.size() <= 0) {
                condition.setValue(null);
            } else {
                Set<Integer> set = new HashSet<Integer>();

                for (Object obj : contracts) {
                    set.add(((Contract)obj).getId());
                }

                value = new Object[set.size()];
                set.toArray(value);
                condition.setValue(value);
            }
        }
        
        condition.setRestrict(restrict);
        condition.setField(field);
        
        return condition;
    }
    
    @SuppressWarnings("unused")
    private HashMap<String, Object> getSearchSeasonCondition(String[] values){
        HashMap<String, Object> hm = new HashMap<String, Object>();
        
        for(String value : values){
            if(value != null){
                
                String[] stCondition = value.split(":");
                if(stCondition.length < 2 || stCondition[1].trim().equals("")){
                    continue;
                }
                else {
                    
                
                    if(stCondition[0].equals("customer_number")){
                        hm.put("contractNumber", StringUtil.nullToBlank(stCondition[1]));
                    }else if(stCondition[0].equals("customer_name")){
                        hm.put("customerName", StringUtil.nullToBlank(stCondition[1]));
                    }else if(stCondition[0].equals("contractGroup")){
                        hm.put("contractGroup", StringUtil.nullToBlank(stCondition[1]));
                    }else if(stCondition[0].equals("meteringSF")){
                        hm.put("meteringSF", StringUtil.nullToBlank(stCondition[1]));
                    }else if(stCondition[0].equals("customer_type")){
                        hm.put("tariffType", Integer.parseInt(StringUtil.nullCheck(stCondition[1],"0")));
                    }else if(stCondition[0].equals("search_from")){
                        String[] searchDate = stCondition[1].split("@");
                        String[] dateStartEnd = new String[4];
                        HashMap<String, Object> dateHm = getSeasonDate (searchDate[0].substring(0, 4));
                        
                        dateStartEnd[0] = (String)dateHm.get("Spring");
                        dateStartEnd[1] = (String)dateHm.get("Summer");
                        dateStartEnd[2] = (String)dateHm.get("Autumn");
                        dateStartEnd[3] = (String)dateHm.get("Winter");
                        
                        String[] arrStartDate   = dateStartEnd[0].split("@");
                        hm.put("startDate", searchDate[0]);
                        String[] arrEndDate     = dateStartEnd[3].split("@");
                        hm.put("endDate",  searchDate[1]);
                        
                        hm.put("arrStdEndDate", dateStartEnd);
                    }else if(stCondition[0].equals("location")){
                        hm.put("locationId", StringUtil.nullCheck(stCondition[1],"0"));
                    }else if(stCondition[0].equals("mcu_id")){
                        hm.put("mcuId", stCondition[1]);
                    }else if(stCondition[0].equals("meter_id")){
                        Meter meter = meterDao.findByCondition("mdsId", stCondition[1]);
                        
                        if(meter == null){
                            hm.put("mdsId", null);
                                            
                        }else{
                            hm.put("mdsId", value);
                        }
                    }else if(stCondition[0].equals("first")){
                        hm.put("first", Integer.parseInt(StringUtil.nullCheck(stCondition[1],"0")));
                    }else if(stCondition[0].equals("max")){
                        hm.put("max", Integer.parseInt(StringUtil.nullCheck(stCondition[1],"0")));
                    }else if(stCondition[0].equals("device_type")){
                        hm.put("deviceType", StringUtil.nullCheck(stCondition[1],""));
                    }else if(stCondition[0].equals("mdev_id")){
                        hm.put("mdevId", StringUtil.nullCheck(stCondition[1],""));
                    }else if(stCondition[0].equals("customType")){
                        hm.put("customType", StringUtil.nullCheck(stCondition[1],""));
                    }
                
                }
                
            }
        }
        
        return hm;

    }
    
    /*
     * 연별 검색 조건 메소드
     */
    private HashMap<String, Object> getSearchYearCondition(String[] values){
        HashMap<String, Object> hm = new HashMap<String, Object>();
        
        for(String value : values){
            if(value != null){
                
                String[] stCondition = value.split(":");
                if(stCondition.length < 2 || stCondition[1].trim().equals("")){
                    continue;
                }
                else {
                    if(stCondition[0].equals("customer_number")){
                        hm.put("contractNumber", StringUtil.nullToBlank(stCondition[1]));
                    }else if(stCondition[0].equals("customer_name")){
                        hm.put("customerName", StringUtil.nullToBlank(stCondition[1]));
                    }else if(stCondition[0].equals("contractGroup")){
                        hm.put("contractGroup", StringUtil.nullToBlank(stCondition[1]));
                    }else if(stCondition[0].equals("meteringSF")){
                        hm.put("meteringSF", StringUtil.nullToBlank(stCondition[1]));
                    }else if(stCondition[0].equals("customer_type")){
                        hm.put("tariffType", Integer.parseInt(StringUtil.nullCheck(stCondition[1],"0")));
                    }else if(stCondition[0].equals("search_from")){
                        String[] searchDate = stCondition[1].split("@");                        
                        hm.put("startDate", searchDate[0]);
                        hm.put("endDate",  searchDate[1]);
                    }else if(stCondition[0].equals("location")){
                        hm.put("locationId", StringUtil.nullCheck(stCondition[1],"0"));
                    }else if(stCondition[0].equals("mcu_id")){
                        hm.put("mcuId", stCondition[1]);
                    }else if(stCondition[0].equals("meter_id")){
                        Meter meter = meterDao.findByCondition("mdsId", stCondition[1]);
                        
                        if(meter == null){
                            hm.put("mdsId", null);
                                            
                        }else{
                            hm.put("mdsId", value);
                        }
                    }else if(stCondition[0].equals("first")){
                        hm.put("first", Integer.parseInt(StringUtil.nullCheck(stCondition[1],"0")));
                    }else if(stCondition[0].equals("max")){
                        hm.put("max", Integer.parseInt(StringUtil.nullCheck(stCondition[1],"0")));
                    }else if(stCondition[0].equals("device_type")){
                        hm.put("deviceType", StringUtil.nullCheck(stCondition[1],""));
                    }else if(stCondition[0].equals("mdev_id")){
                        hm.put("mdevId", StringUtil.nullCheck(stCondition[1],""));
                    }else if(stCondition[0].equals("customType")){
                        hm.put("customType", StringUtil.nullCheck(stCondition[1],""));
                    }
                
                }
                
            }
        }
        
        return hm;

    }
    
    private String getBeforeHour(String yyyymmddhh){
        String before = "";
        String yyyymmdd = yyyymmddhh.substring(0,8);
        int hh = Integer.parseInt(yyyymmddhh.substring(8, 10)) - 1;
        if(hh < 0){
            before = getBeforeDay(yyyymmdd) + "23";
        }else if(hh < 10 ){
            before = yyyymmdd + "0" + Integer.toString(hh); 
        }else{
            before = yyyymmdd + Integer.toString(hh);
        }

        return before;
    }
    
    private String getBeforeDay(String yyyymmdd){
        String before = "";
        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyyMMdd");
        Calendar calendar= Calendar.getInstance();
        calendar.clear();
        try {
            calendar.setTime(sdFormat.parse(yyyymmdd));
        } catch (ParseException e) {           
            e.printStackTrace();
        }
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        
        before = sdFormat.format(calendar.getTime());

        return before;
    }
    
    private String getBeforeMonth(String yyyymm){
        String before = "";
        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyyMM");
        Calendar calendar= Calendar.getInstance();
        calendar.clear();
        try {
            calendar.setTime(sdFormat.parse(yyyymm));
        } catch (ParseException e) {           
            e.printStackTrace();
        }
        calendar.add(Calendar.MONTH, -1);
        
        before = sdFormat.format(calendar.getTime());

        return before;
    }

    
   
    /*
     * Season의 이전계절별 시작일, 종료일 가져오기
     */
    public String[] getBeforeSeasonDate (String year, int SeasonNum) {
        
        Iterator<Season> it = null;
        String[] arrSeasonName = {"Spring","Summer","Autumn","Winter"};
        String seasonName = arrSeasonName[(SeasonNum-1)];
        String[] searchDate = new String[2];
        
        List<Season> searchSeasonList = seasonDao.getSeasonsBySyear(year);
        if (searchSeasonList.size() > 0 && searchSeasonList != null) {
            it = searchSeasonList.iterator();
            while (it.hasNext()) {
                Season retSeason = (Season) it.next();
                
                if(seasonName.equals(retSeason.getName())) {
                    searchDate[0] = retSeason.getSyear()+retSeason.getSmonth()+retSeason.getSday();
                    searchDate[1] = retSeason.getEyear()+retSeason.getEmonth()+retSeason.getEday();
                    
                    break;
                }
                else {
                    continue;
                }
                
            }
        }
        else {
            List<Season> seasonList = seasonDao.getSeasonsBySyearIsNull();
            it = seasonList.iterator();
            
            while (it.hasNext()) {
                Season retSeason = (Season) it.next();
                
                if(seasonName.equals(retSeason.getName())) {
                    searchDate[0] = year+retSeason.getSmonth()+"01";
                    searchDate[1] = year+retSeason.getEmonth()+"31";
                    break;
                }
                else {
                    continue;
                }
                
            }
        }
        
        return searchDate;
    }
    
    /*
     *  해당월의 주에 속한 시작일과 종료일을 추출
     */
    public String[] getYyMmDdToWeekDate(String yyyymmdd){
        
        SearchCalendarUtil sCaldUtil = new SearchCalendarUtil();
        
        String yyMM = yyyymmdd.substring(0, 6);
        
        List<String> dateList = sCaldUtil.getMonthToBeginDateEndDate(yyMM);
        String[] resultOb = new String[dateList.size()];
        for(int i=0;i<dateList.size();i++) {
            String stdDate = dateList.get(i).substring(0, 8);
            String endDate = dateList.get(i).substring(8);
            resultOb[i] = stdDate+"@"+endDate;
        }
        
        return resultOb;
    }
    
    
    
    /*
     * Season의 계절별 시작일, 종료일 가져오기
     */
    public HashMap<String, Object> getSeasonDate (String year) {
        
        HashMap<String, Object> hm = new HashMap<String, Object>();
        Iterator<Season> it = null;
        
        List<Season> searchSeasonList = seasonDao.getSeasonsBySyear(year);
        if (searchSeasonList.size() > 0 && searchSeasonList != null) {
            it = searchSeasonList.iterator();
            while (it.hasNext()) {
                Season retSeason = (Season) it.next();
                String[] searchDate = new String[2];
                
                if("Spring".equals(retSeason.getName())) {
                    searchDate[0] = retSeason.getSyear()+retSeason.getSmonth()+retSeason.getSday();
                    searchDate[1] = retSeason.getEyear()+retSeason.getEmonth()+retSeason.getEday();
                    hm.put("Spring", searchDate[0]+"@"+searchDate[1]);
                }
                else if("Summer".equals(retSeason.getName())) {
                    searchDate[0] = retSeason.getSyear()+retSeason.getSmonth()+retSeason.getSday();
                    searchDate[1] = retSeason.getEyear()+retSeason.getEmonth()+retSeason.getEday();
                    hm.put("Summer", searchDate[0]+"@"+searchDate[1]);
                }
                else if("Autumn".equals(retSeason.getName())) {
                    searchDate[0] = retSeason.getSyear()+retSeason.getSmonth()+retSeason.getSday();
                    searchDate[1] = retSeason.getEyear()+retSeason.getEmonth()+retSeason.getEday(); 
                    hm.put("Autumn", searchDate[0]+"@"+searchDate[1]);
                }
                else { 
                    searchDate[0] = retSeason.getSyear()+retSeason.getSmonth()+retSeason.getSday();
                    searchDate[1] = retSeason.getEyear()+retSeason.getEmonth()+retSeason.getEday();
                    hm.put("Winter", searchDate[0]+"@"+searchDate[1]);
                }
            }
        }
        else {
            List<Season> seasonList = seasonDao.getSeasonsBySyearIsNull();
            it = seasonList.iterator();
            
            while (it.hasNext()) {
                Season retSeason = (Season) it.next();
                String[] searchDate = new String[2];
                if("Spring".equals(retSeason.getName())) {
                    searchDate[0] = year+retSeason.getSmonth()+"01";
                    searchDate[1] = year+retSeason.getEmonth()+"31";
                    hm.put("Spring",  searchDate[0]+"@"+searchDate[1]);
                }
                else if("Summer".equals(retSeason.getName())) {
                    searchDate[0] = year+retSeason.getSmonth()+"01";
                    searchDate[1] = year+retSeason.getEmonth()+"31";
                    hm.put("Summer",  searchDate[0]+"@"+searchDate[1]);
                }
                else if("Autumn".equals(retSeason.getName())) {
                    searchDate[0] = year+retSeason.getSmonth()+"01";
                    searchDate[1] = year+retSeason.getEmonth()+"31";
                    hm.put("Autumn",  searchDate[0]+"@"+searchDate[1]);
                }
                else { 
                    searchDate[0] = year+retSeason.getSmonth()+"01";
                    searchDate[1] = (Integer.parseInt(year)+1)+retSeason.getEmonth()+"31";// 겨울의 종료일은 다음해로 넘어감
                    hm.put("Winter",  searchDate[0]+"@"+searchDate[1]);
                }
            }
        }
        
        return hm;
    }
    
    
    @SuppressWarnings("rawtypes")
    public Map<String,String> getLocationList() {
        
        List<Location> loList = locationDao.getLocations();
        Map map = new LinkedHashMap();
        
        map.put("", "All"); 
                    
        for(Location lo:loList){
            map.put(lo.getId(), lo.getName());
        }
        return map;        
    }
    
    @SuppressWarnings("rawtypes")
    public Map<String,String> getTariffTypeList() {
        
        Set<Condition> set = new HashSet<Condition>();
        set.add(new Condition("name",null,null,Restriction.ORDERBY));
        List<TariffType> tariffType = tariffTypeDao.findByConditions(set);
     
        Map map = new LinkedHashMap();
        
        map.put("", "All"); 
                    
        for(TariffType tt:tariffType){
            map.put(tt.getId(), tt.getName());
        }
        return map;        
    }
    
    /*
     * supplyTypecode를 가지고 Co2계산기준값을 가져옴
     */
    public Double getTypeToCo2(String type) {
        
        String meterType = ChangeMeterTypeName.valueOf(type).getCode();
        int typeId = codeDao.getCodeIdByCode(MeterType.valueOf(meterType).getServiceType());
        
        Co2Formula co2Formula = co2formulaDao.getCo2FormulaBySupplyType(typeId);

        return  co2Formula.getCo2factor();
    }
////////////////////////////////////////////////////////////////// year MeteringData
    /*
     * 연별 검색 데이터
     */
    public List<?> getMeteringDataYear(String[] values, String type, String supplierId) {
        List<MeteringListData> result = new ArrayList<MeteringListData>();
        int firstRow =0;        
        HashMap<String, Object> hm = new HashMap<String, Object>();
        hm = getSearchYearCondition(values); 
        
        hm.put("channel",1);
        hm.put("supplierId",supplierId);
        if ("EM".equals(type)) {
            hm.put("meterType","EM");
            result = getYearEMList(firstRow,type,hm);
        } else if ("GM".equals(type)) {
            hm.put("meterType","GM");
            result = getYearEMList(firstRow,type,hm);
        } else if ("WM".equals(type)) {
            hm.put("meterType","WM");
            result = getYearEMList(firstRow,type,hm);
        } else if ("HM".equals(type)) {
            hm.put("meterType","HM");
            result = getYearEMList(firstRow,type,hm);
        } else {
        }
        
        return result;        
    }
    /*
     * 연별 엑셀 검색 데이터
     */
    public List<?> getMeteringDataYearExcel(String[] values, String type, String supplierId) {
        List<MeteringListData> result = new ArrayList<MeteringListData>();
        int firstRow =0;        
        HashMap<String, Object> hm = new HashMap<String, Object>();
        hm = getSearchYearCondition(values); 
        
      //패이징 처리 안함
        hm.put("excel", 1);
        hm.remove("first");
        hm.put("first", 0);
        hm.remove("max");
        
        hm.put("channel",1);
        hm.put("supplierId",supplierId);
        if ("EM".equals(type)) {
            hm.put("meterType","EM");
            result = getYearEMList(firstRow,type,hm);
        } else if ("GM".equals(type)) {
            hm.put("meterType","GM");
            result = getYearEMList(firstRow,type,hm);
        } else if ("WM".equals(type)) {
            hm.put("meterType","WM");
            result = getYearEMList(firstRow,type,hm);
        } else if ("HM".equals(type)) {
            hm.put("meterType","HM");
            result = getYearEMList(firstRow,type,hm);
        } else {
        }
        
        return result;        
    }

    @SuppressWarnings("rawtypes")
    public Map<String, String> getMeteringDataYearTotal(String[] values, String type, String supplierId) {
        Map result = new HashMap();
        result.put("total","0");
        HashMap<String, Object> hm = new HashMap<String, Object>();
        hm  = getSearchYearCondition(values);
        hm.put("channel",1);
        hm.put("supplierId",supplierId);
        int totCount =0;
        
        if ("EM".equals(type)) {
            hm.put("meterType","EM");
            totCount = getYearEMTotTalCount(hm);
        } else if ("GM".equals(type)) {
            hm.put("meterType","GM");
            totCount = getYearEMTotTalCount(hm);
        } else if ("WM".equals(type)) {
            hm.put("meterType","WM");
            totCount = getYearEMTotTalCount(hm);
        } else if ("HM".equals(type)) {
            hm.put("meterType","HM");
            totCount = getYearEMTotTalCount(hm);
        }
        result.put("total", totCount);
        return result;
    }

    /**
     * method name : getMeteringDataHourlyData<b/>
     * method Desc : Metering Data 맥스가젯에서 시간별 검침데이터를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getMeteringDataHourlyData(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        Integer page = (Integer)conditionMap.get("page");
        Integer limit = (Integer)conditionMap.get("limit");
        Integer locationId = (Integer)conditionMap.get("locationId");
        Integer permitLocationId = (Integer)conditionMap.get("permitLocationId");
        List<Integer> locationIdList = null;

        if (locationId != null) {
            locationIdList = locationDao.getChildLocationId(locationId);
            locationIdList.add(locationId);
            conditionMap.put("locationIdList", locationIdList);
        } else if (permitLocationId != null) {
            locationIdList = locationDao.getChildLocationId(permitLocationId);
            locationIdList.add(permitLocationId);
            conditionMap.put("locationIdList", locationIdList);
        }

        String searchStartDate = (String)conditionMap.get("searchStartDate");
        String searchStartHour = (String)conditionMap.get("searchStartHour");
        String searchEndDate = (String)conditionMap.get("searchEndDate");
        String searchEndHour = (String)conditionMap.get("searchEndHour");
        String searchPrevStartDate = null;
        String searchPrevStartHour = null;
        String searchPrevStartDateHour = null;
        Integer preStartHour = null;

        if (page != null && limit != null) {        // paging
            conditionMap.put("startDate", searchStartDate + searchStartHour);
        } else {        // all
            try {
                if (searchStartHour.equals("00")) {
                    searchPrevStartDate = TimeUtil.getPreDay(searchStartDate).substring(0, 8);
                    searchPrevStartHour = "23";
                } else {
                    searchPrevStartDate = searchStartDate;
                    preStartHour = new Integer(Integer.parseInt(searchStartHour) - 1);
                    searchPrevStartHour = StringUtil.frontAppendNStr('0', preStartHour.toString(), 2);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            searchPrevStartDateHour = searchPrevStartDate + searchPrevStartHour;
            conditionMap.put("startDate", searchPrevStartDateHour);
        }

        conditionMap.put("endDate", searchEndDate + searchEndHour);

        List<Map<String, Object>> list = meteringLpDao.getMeteringDataHourlyData(conditionMap, false);
        List<Map<String, Object>> prevList = new ArrayList<Map<String, Object>>();
        Map<String, Object> listMap = new HashMap<String, Object>();
        Map<String, Object> map = null;
        Supplier supplier = supplierDao.get(supplierId);
        String country = supplier.getCountry().getCode_2letter();
        String lang    = supplier.getLang().getCode_2letter();
        DecimalFormat mdf = DecimalUtil.getDecimalFormat(supplier.getMd());
        Double prevValue = null;
        Set<String> meterNoList = new HashSet<String>();

        if (page != null && limit != null) {        // paging
            int cnt = 0;
            String prevStartDate = null;
            String prevEndDate = null;
            Map<String, Object> fstMap = null;
            Map<String, Object> lstMap = null;

            if (list != null && list.size() > 0) {
                for (Map<String, Object> obj : list) {
                    meterNoList.add((String)obj.get("METER_NO"));
                }
                conditionMap.put("meterNoList", meterNoList);

                cnt = list.size();
                fstMap = list.get(0);
                lstMap = list.get(cnt-1);
                if (((String)fstMap.get("HH")).equals("00")) {
                    try {
                        prevStartDate = TimeUtil.getPreDay((String)fstMap.get("YYYYMMDD")).substring(0, 8) + "23";
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    prevStartDate = (String)fstMap.get("YYYYMMDD") + StringUtil.frontAppendNStr('0', new Integer(Integer.parseInt((String)fstMap.get("HH")) - 1).toString(), 2);
                }
                prevEndDate = (String)lstMap.get("YYYYMMDDHH");

                conditionMap.put("prevStartDate", prevStartDate);
                conditionMap.put("prevEndDate", prevEndDate);

                prevList = meteringLpDao.getMeteringDataHourlyData(conditionMap, false, true);

                for (Map<String, Object> obj : prevList) {
                    listMap.put((String)obj.get("YYYYMMDDHH") + "_" + (String)obj.get("METER_NO"), obj.get("VALUE"));
                }
            }
        } else {        // all
            for (Map<String, Object> obj : list) {
                listMap.put((String)obj.get("YYYYMMDDHH") + "_" + (String)obj.get("METER_NO"), obj.get("VALUE"));
            }

            if ("대성에너지".equals(supplier.getName())) {
                conditionMap.put("startDate", searchStartDate);
                conditionMap.put("endDate", searchEndDate);
                conditionMap.put("startDetailDate", searchStartHour);
                conditionMap.put("endDetailDate", searchEndHour);

                List<Map<String, Object>> accumulateList = meteringdayDao.getMeteringDataHourlyChannel2Data(conditionMap);

                try {
                    for (Map<String, Object> obj : accumulateList) {
                        searchStartDate = (String)obj.get("YYYYMMDD");
                        searchEndDate = (String)obj.get("YYYYMMDD");
                        for (int j = 23; j >= 0; j--) {
                            listMap.put((String)obj.get("YYYYMMDD") + StringUtil.frontAppendNStr('0', new Integer(j).toString(), 2)+ "_" + (String)obj.get("METER_NO")+"_ACCUMULATE", obj.get("VALUE_"+StringUtil.frontAppendNStr('0', new Integer(j).toString(), 2)));
                        }
                    }
                } catch (Exception e) {
                    logger.error(e,e);
                }
            }
        }

        Integer prevHour = 0;
        int num = 0;

        if (page != null && limit != null) {
            num = ((page - 1) * limit) + 1;
        } else {
            num = 1;
        }

        for (Map<String, Object> obj : list) {
            if ((page == null || limit == null) && ((String)obj.get("YYYYMMDDHH")).compareTo(searchPrevStartDateHour) == 0) {
                continue;
            }

            map = new HashMap<String, Object>();

            map.put("num", num++);
            map.put("contractNumber", (String)obj.get("CONTRACT_NUMBER"));
            map.put("friendlyName", (String)obj.get("FRIENDLY_NAME"));
            map.put("customerName", (String)obj.get("CUSTOMER_NAME"));
            map.put("meteringTime", TimeLocaleUtil.getLocaleDateHour((String)obj.get("YYYYMMDDHH"), lang, country));

            map.put("meterNo", (String)obj.get("METER_NO"));
            map.put("modemId", (String)obj.get("MODEM_ID"));
            map.put("sicName", (String)obj.get("SIC_NAME"));
            map.put("value", mdf.format(DecimalUtil.ConvertNumberToDouble(obj.get("VALUE"))));
            map.put("channel_1", mdf.format(DecimalUtil.ConvertNumberToDouble(obj.get("CHANNEL_1"))));
            map.put("channel_2", mdf.format(DecimalUtil.ConvertNumberToDouble(obj.get("CHANNEL_2"))));
            map.put("channel_3", mdf.format(DecimalUtil.ConvertNumberToDouble(obj.get("CHANNEL_3"))));
            map.put("channel_4", mdf.format(DecimalUtil.ConvertNumberToDouble(obj.get("CHANNEL_4"))));

            Double accumulateValue = null;

            if (((String)obj.get("HH")).equals("00")) {
                try {
                    prevValue = DecimalUtil.ConvertNumberToDouble(listMap.get(TimeUtil.getPreDay((String)obj.get("YYYYMMDD")).substring(0, 8) + "23_" + (String)obj.get("METER_NO")));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                prevHour = new Integer(Integer.parseInt((String)obj.get("HH")) - 1);
                prevValue = DecimalUtil.ConvertNumberToDouble(listMap.get((String)obj.get("YYYYMMDD") + StringUtil.frontAppendNStr('0', prevHour.toString(), 2) + "_" + (String)obj.get("METER_NO")));
            }

            if("대성에너지".equals(supplier.getName())) {
                try {
                    accumulateValue = DecimalUtil.ConvertNumberToDouble(listMap.get((String)obj.get("YYYYMMDDHH")+"_"+(String)obj.get("METER_NO")+"_ACCUMULATE"));
                } catch (Exception e) {
                    logger.error(e,e);
                }
            }

            map.put("prevValue", (prevValue == null) ? "" : mdf.format(prevValue));
            if("대성에너지".equals(supplier.getName()))
                map.put("accumulateValue", (accumulateValue == null) ? "" : mdf.format(accumulateValue));
            result.add(map);
        }

        return result;
    }

    /**
     * method name : getMeteringDataHourlyDataTotalCount<b/>
     * method Desc : Metering Data 맥스가젯에서 시간별 검침데이터의 total count 를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public Integer getMeteringDataHourlyDataTotalCount(Map<String, Object> conditionMap) {
        Integer locationId = (Integer)conditionMap.get("locationId");
        Integer permitLocationId = (Integer)conditionMap.get("permitLocationId");
        String searchStartDate = (String)conditionMap.get("searchStartDate");
        String searchStartHour = (String)conditionMap.get("searchStartHour");
        String searchEndDate = (String)conditionMap.get("searchEndDate");
        String searchEndHour = (String)conditionMap.get("searchEndHour");
        List<Integer> locationIdList = null;

        if (locationId != null) {
            locationIdList = locationDao.getChildLocationId(locationId);
            locationIdList.add(locationId);
            conditionMap.put("locationIdList", locationIdList);
        } else if (permitLocationId != null) {
            locationIdList = locationDao.getChildLocationId(permitLocationId);
            locationIdList.add(permitLocationId);
            conditionMap.put("locationIdList", locationIdList);
        }

        conditionMap.put("meterNoList", null);
        conditionMap.put("startDate", searchStartDate + searchStartHour);
        conditionMap.put("endDate", searchEndDate + searchEndHour);

        List<Map<String, Object>> result = meteringLpDao.getMeteringDataHourlyData(conditionMap, true);
        return (Integer)(result.get(0).get("total"));
    }
    
    /**
     * method name : getMeteringValueDataHourlyData<b/>
     * method Desc : Metering Data 맥스가젯에서 시간별 지침값을 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getMeteringValueDataHourlyData(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        Integer page = (Integer)conditionMap.get("page");
        Integer limit = (Integer)conditionMap.get("limit");
        Integer locationId = (Integer)conditionMap.get("locationId");
        Integer permitLocationId = (Integer)conditionMap.get("permitLocationId");

        List<Integer> locationIdList = null;

        if (locationId != null) {
            locationIdList = locationDao.getChildLocationId(locationId);
            locationIdList.add(locationId);
            conditionMap.put("locationIdList", locationIdList);
        } else if (permitLocationId != null) {
            locationIdList = locationDao.getChildLocationId(permitLocationId);
            locationIdList.add(permitLocationId);
            conditionMap.put("locationIdList", locationIdList);
        }

        String searchStartDate = (String)conditionMap.get("searchStartDate");
        String searchEndDate = (String)conditionMap.get("searchEndDate");
        String searchPrevStartDate = null;

        if (page != null && limit != null) {        // paging
            conditionMap.put("startDate", searchStartDate);
        } else {        // all
            try {
                searchPrevStartDate = TimeUtil.getPreDay(searchStartDate).substring(0, 8);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            conditionMap.put("startDate", searchPrevStartDate);
        }

        conditionMap.put("endDate", searchEndDate);

        List<Map<String, Object>> list = meteringdayDao.getMeteringDataDailyData(conditionMap, false);
        List<Map<String, Object>> prevList = new ArrayList<Map<String, Object>>();
        Map<String, Object> listMap = new HashMap<String, Object>();
        Map<String, Object> map = null;

        Supplier supplier = supplierDao.get(supplierId);
        String country = supplier.getCountry().getCode_2letter();
        String lang    = supplier.getLang().getCode_2letter();
        DecimalFormat mdf = DecimalUtil.getDecimalFormat(supplier.getMd());
        Double prevValue = null;
        Double afterValue = null;

        if (page != null && limit != null) {        // paging
            int cnt = 0;
            String prevStartDate = null;
            String prevEndDate = null;
            Map<String, Object> fstMap = null;
            Map<String, Object> lstMap = null;

            if (list != null && list.size() > 0) {
                cnt = list.size();
                fstMap = list.get(0);
                lstMap = list.get(cnt-1);
                try {
                    prevStartDate = TimeUtil.getPreDay((String)fstMap.get("YYYYMMDD")).substring(0, 8);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                
                try {
                    prevEndDate = TimeUtil.getAddedDay((String)lstMap.get("YYYYMMDD"), 3).substring(0,8);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                conditionMap.put("prevStartDate", prevStartDate);
                conditionMap.put("prevEndDate", prevEndDate);

                prevList = meteringdayDao.getMeteringDataDailyData(conditionMap, false, true);

                for (Map<String, Object> obj : prevList) {
                    listMap.put((String)obj.get("YYYYMMDD") + "_" + (String)obj.get("METER_NO"), obj.get("BASEVALUE"));
                }
            }
        } else {        // all
            int cnt = 0;
            String prevStartDate = null;
            String prevEndDate = null;
            Map<String, Object> fstMap = null;
            Map<String, Object> lstMap = null;

            if (list != null && list.size() > 0) {
                cnt = list.size();
                fstMap = list.get(0);
                lstMap = list.get(cnt-1);
                try {
                    prevStartDate = TimeUtil.getPreDay((String)fstMap.get("YYYYMMDD")).substring(0, 8);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                
                try {
                    prevEndDate = TimeUtil.getAddedDay((String)lstMap.get("YYYYMMDD"), 3).substring(0,8);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                conditionMap.put("prevStartDate", prevStartDate);
                conditionMap.put("prevEndDate", prevEndDate);

                prevList = meteringdayDao.getMeteringDataDailyData(conditionMap, false, true);

                for (Map<String, Object> obj : prevList) {
                    listMap.put((String)obj.get("YYYYMMDD") + "_" + (String)obj.get("METER_NO"), obj.get("BASEVALUE"));
                }
            }

            if ("대성에너지".equals(supplier.getName())) {
                conditionMap.put("startDate", searchStartDate.substring(0, 6));
                conditionMap.put("endDate", searchEndDate.substring(0, 6));
                conditionMap.put("startDetailDate", searchStartDate.substring(6, 8));
                conditionMap.put("endDetailDate", searchEndDate.substring(6, 8));

                List<Map<String, Object>> accumulateList = meteringMonthDao.getMeteringDataMonthlyChannel2Data(conditionMap);

                for (Map<String, Object> obj : accumulateList) {
                    listMap.put((String)obj.get("METER_NO")+"_ACCUMULATE", obj.get("ACCUMULATEVALUE"));
                }
            }
        }

        int num = 0;

        if (page != null && limit != null) {
            num = ((page - 1) * limit) + 1;
        } else {
            num = 1;
        }

        for (Map<String, Object> obj : list) {
            // 전체조회일 경우 이전일자 데이터는 skip
            if ((page == null || limit == null) && ((String)obj.get("YYYYMMDD")).compareTo(searchPrevStartDate) == 0) {
                continue;
            }

            map = new HashMap<String, Object>();

            map.put("num", num++);
            map.put("contractNumber", (String)obj.get("CONTRACT_NUMBER"));
            map.put("customerName", (String)obj.get("CUSTOMER_NAME"));
            map.put("friendlyName", (String)obj.get("FRIENDLY_NAME"));
            map.put("meteringTime", TimeLocaleUtil.getLocaleDate(((String)obj.get("YYYYMMDD"))+"000000", lang, country));

            map.put("meterNo", (String)obj.get("METER_NO"));
            map.put("modemId", (String)obj.get("MODEM_ID"));
            map.put("sicName", (String)obj.get("SIC_NAME"));
            String value = obj.get("BASEVALUE") == null ? null : mdf.format(DecimalUtil.ConvertNumberToDouble(obj.get("BASEVALUE")));
            map.put("value", value);

            Double accumulate = null;

            try {
                prevValue = DecimalUtil.ConvertNumberToDouble(listMap.get(TimeUtil.getPreDay((String)obj.get("YYYYMMDD")).substring(0, 8) + "_" + (String)obj.get("METER_NO")));
                afterValue = DecimalUtil.ConvertNumberToDouble(listMap.get(TimeUtil.getAddedDay((String)obj.get("YYYYMMDD"),1).substring(0, 8) + "_" + (String)obj.get("METER_NO")));
                if ("대성에너지".equals(supplier.getName()))
                    accumulate = DecimalUtil.ConvertNumberToDouble(listMap.get((String)obj.get("METER_NO")+"_ACCUMULATE"));
            } catch (ParseException e) {
                logger.error(e,e);
            }
            String prevValueStr = (prevValue == null) ? "" : mdf.format(prevValue);

            map.put("prevValue",  prevValueStr);
            map.put("usage", (value == null ||afterValue == null) ? "" : mdf.format(afterValue - DecimalUtil.ConvertNumberToDouble(obj.get("BASEVALUE"))));
            if ("대성에너지".equals(supplier.getName()))
                map.put("accumulateValue", (accumulate == null) ? "" : mdf.format(accumulate));
            result.add(map);
        }

        return result;
    }
    
    /**
     * method name : getMeteringValueDataHourlyDataTotalCount<b/>
     * method Desc : Metering Data 맥스가젯에서 시간별 지침값의 total count 를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public Integer getMeteringValueDataHourlyDataTotalCount(Map<String, Object> conditionMap) {
        Integer locationId = (Integer)conditionMap.get("locationId");
        Integer permitLocationId = (Integer)conditionMap.get("permitLocationId");
        List<Integer> locationIdList = null;

        if (locationId != null) {
            locationIdList = locationDao.getChildLocationId(locationId);
            locationIdList.add(locationId);
            conditionMap.put("locationIdList", locationIdList);
        } else if (permitLocationId != null) {
            locationIdList = locationDao.getChildLocationId(permitLocationId);
            locationIdList.add(permitLocationId);
            conditionMap.put("locationIdList", locationIdList);
        }

        conditionMap.put("startDate", (String)conditionMap.get("searchStartDate"));
        conditionMap.put("endDate", (String)conditionMap.get("searchEndDate"));

        List<Map<String, Object>> result = meteringdayDao.getMeteringDataDailyData(conditionMap, true);
        return (Integer)(result.get(0).get("total"));
    }

    /**
     * method name : getMeteringDataDailyData<b/>
     * method Desc : Metering Data 맥스가젯에서 일별 검침데이터를 조회한다.
     * 추후 삭제 예정(16-10-03)
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getMeteringDataDailyData(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        Integer page = (Integer)conditionMap.get("page");
        Integer limit = (Integer)conditionMap.get("limit");
        Integer locationId = (Integer)conditionMap.get("locationId");
        Integer permitLocationId = (Integer)conditionMap.get("permitLocationId");

        List<Integer> locationIdList = null;

        if (locationId != null) {
            locationIdList = locationDao.getChildLocationId(locationId);
            locationIdList.add(locationId);
            conditionMap.put("locationIdList", locationIdList);
        } else if (permitLocationId != null) {
            locationIdList = locationDao.getChildLocationId(permitLocationId);
            locationIdList.add(permitLocationId);
            conditionMap.put("locationIdList", locationIdList);
        }

        String searchStartDate = (String)conditionMap.get("searchStartDate");
        String searchEndDate = (String)conditionMap.get("searchEndDate");
        String searchPrevStartDate = null;

        if (page != null && limit != null) {        // paging
            conditionMap.put("startDate", searchStartDate);
        } else {        // all
            try {
                searchPrevStartDate = TimeUtil.getPreDay(searchStartDate).substring(0, 8);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            conditionMap.put("startDate", searchPrevStartDate);
        }

        conditionMap.put("endDate", searchEndDate);

        List<Map<String, Object>> list = meteringdayDao.getMeteringDataDailyData(conditionMap, false);
        List<Map<String, Object>> prevList = new ArrayList<Map<String, Object>>();
        Map<String, Object> listMap = new HashMap<String, Object>();
        Map<String, Object> map = null;

        Supplier supplier = supplierDao.get(supplierId);
        String country = supplier.getCountry().getCode_2letter();
        String lang    = supplier.getLang().getCode_2letter();
        DecimalFormat mdf = DecimalUtil.getDecimalFormat(supplier.getMd());
        Double prevValue = null;

        if (page != null && limit != null) {        // paging
            int cnt = 0;
            String prevStartDate = null;
            String prevEndDate = null;
            Map<String, Object> fstMap = null;
            Map<String, Object> lstMap = null;

            if (list != null && list.size() > 0) {
                cnt = list.size();
                fstMap = list.get(0);
                lstMap = list.get(cnt-1);
                try {
                    prevStartDate = TimeUtil.getPreDay((String)fstMap.get("YYYYMMDD")).substring(0, 8);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                prevEndDate = (String)lstMap.get("YYYYMMDD");

                conditionMap.put("prevStartDate", prevStartDate);
                conditionMap.put("prevEndDate", prevEndDate);

                prevList = meteringdayDao.getMeteringDataDailyData(conditionMap, false, true);

                for (Map<String, Object> obj : prevList) {
                    listMap.put((String)obj.get("YYYYMMDD") + "_" + (String)obj.get("METER_NO"), obj.get("VALUE"));
                }
            }
        } else {        // all
            for (Map<String, Object> obj : list) {
                listMap.put((String)obj.get("YYYYMMDD") + "_" + (String)obj.get("METER_NO"), obj.get("VALUE"));
            }

            if ("대성에너지".equals(supplier.getName())) {
                conditionMap.put("startDate", searchStartDate.substring(0, 6));
                conditionMap.put("endDate", searchEndDate.substring(0, 6));
                conditionMap.put("startDetailDate", searchStartDate.substring(6, 8));
                conditionMap.put("endDetailDate", searchEndDate.substring(6, 8));

                List<Map<String, Object>> accumulateList = meteringMonthDao.getMeteringDataMonthlyChannel2Data(conditionMap);

                for (Map<String, Object> obj : accumulateList) {
                    listMap.put((String)obj.get("METER_NO")+"_ACCUMULATE", obj.get("ACCUMULATEVALUE"));
                }
            }
        }

        int num = 0;

        if (page != null && limit != null) {
            num = ((page - 1) * limit) + 1;
        } else {
            num = 1;
        }

        for (Map<String, Object> obj : list) {
            // 전체조회일 경우 이전일자 데이터는 skip
            if ((page == null || limit == null) && ((String)obj.get("YYYYMMDD")).compareTo(searchPrevStartDate) == 0) {
                continue;
            }

            map = new HashMap<String, Object>();

            map.put("num", num++);
            map.put("contractNumber", (String)obj.get("CONTRACT_NUMBER"));
            map.put("customerName", (String)obj.get("CUSTOMER_NAME"));
            map.put("friendlyName", (String)obj.get("FRIENDLY_NAME"));
            map.put("meteringTime", TimeLocaleUtil.getLocaleDate((String)obj.get("YYYYMMDD"), lang, country));

            map.put("meterNo", (String)obj.get("METER_NO"));
            map.put("modemId", (String)obj.get("MODEM_ID"));
            map.put("sicName", (String)obj.get("SIC_NAME"));
            map.put("value", mdf.format(DecimalUtil.ConvertNumberToDouble(obj.get("VALUE"))));

            Double accumulate = null;

            try {
                prevValue = DecimalUtil.ConvertNumberToDouble(listMap.get(TimeUtil.getPreDay((String)obj.get("YYYYMMDD")).substring(0, 8) + "_" + (String)obj.get("METER_NO")));
                if ("대성에너지".equals(supplier.getName()))
                    accumulate = DecimalUtil.ConvertNumberToDouble(listMap.get((String)obj.get("METER_NO")+"_ACCUMULATE"));
            } catch (ParseException e) {
                logger.error(e,e);
            }

            map.put("prevValue", (prevValue == null) ? "" : mdf.format(prevValue));
            if ("대성에너지".equals(supplier.getName()))
                map.put("accumulateValue", (accumulate == null) ? "" : mdf.format(accumulate));
            result.add(map);
        }

        return result;
    }

    /**
     * A reform version of "getMeteringDataDailyData"
     * method name : getMeteringDataDailyData2
     * method Desc : Metering Data 맥스가젯에서 일별 검침데이터를 조회한다.
     * @param conditionMap
     */
    public List<Map<String, Object>> getMeteringDataDailyData2(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        Integer page = (Integer)conditionMap.get("page");
        Integer limit = (Integer)conditionMap.get("limit");
        Integer locationId = (Integer)conditionMap.get("locationId");
        Integer permitLocationId = (Integer)conditionMap.get("permitLocationId");

        List<Integer> locationIdList = null;

        if (locationId != null) {
            locationIdList = locationDao.getChildLocationId(locationId);
            locationIdList.add(locationId);
            conditionMap.put("locationIdList", locationIdList);
        } else if (permitLocationId != null) {
            locationIdList = locationDao.getChildLocationId(permitLocationId);
            locationIdList.add(permitLocationId);
            conditionMap.put("locationIdList", locationIdList);
        }

        String searchStartDate = (String)conditionMap.get("searchStartDate");
        String searchEndDate = (String)conditionMap.get("searchEndDate");
        String searchPrevStartDate = null;

        try {
            if (page != null && limit != null) {        // paging
                conditionMap.put("startDate", searchStartDate);
                searchPrevStartDate = TimeUtil.getPreDay(searchStartDate).substring(0, 8);
                conditionMap.put("prevStartDate", searchPrevStartDate);

            }else{    // all
                searchPrevStartDate = TimeUtil.getPreDay(searchStartDate).substring(0, 8);
                conditionMap.put("startDate", searchPrevStartDate);

            }
        } catch (ParseException e) {
            logger.error(e,e);
        }

        conditionMap.put("endDate", searchEndDate);

        // Replace to the reformed function for reduce the processing time.
        List<Map<String, Object>> list = meteringdayDao.getMeteringDataDailyData2(conditionMap, false);
        Map<String, Object> listMap = new HashMap<String, Object>();
        Map<String, Object> map = null;

        Supplier supplier = supplierDao.get(supplierId);
        String country = supplier.getCountry().getCode_2letter();
        String lang    = supplier.getLang().getCode_2letter();
        DecimalFormat mdf = DecimalUtil.getDecimalFormat(supplier.getMd());

        if (page != null && limit != null) {        // paging

        } else {        // all
            for (Map<String, Object> obj : list) {
                listMap.put((String)obj.get("YYYYMMDD") + "_" + (String)obj.get("METER_NO"), obj.get("VALUE"));
            }
        }

        int num = 0;

        if (page != null && limit != null) {
            num = ((page - 1) * limit) + 1;
        } else {
            num = 1;
        }

        for (Map<String, Object> obj : list) {
            // 전체조회일 경우 이전일자 데이터는 skip
            if ((page == null || limit == null) && ((String)obj.get("YYYYMMDD")).compareTo(searchPrevStartDate) == 0) {
                continue;
            }

            map = new HashMap<String, Object>();

            map.put("num", num++);
            map.put("contractNumber", (String)obj.get("CONTRACT_NUMBER"));
            map.put("customerName", (String)obj.get("CUSTOMER_NAME"));
            map.put("friendlyName", (String)obj.get("FRIENDLY_NAME"));
            map.put("meteringTime", TimeLocaleUtil.getLocaleDate((String)obj.get("YYYYMMDD"), lang, country));

            map.put("meterNo", (String)obj.get("METER_NO"));
            map.put("modemId", (String)obj.get("MODEM_ID"));
            map.put("sicName", (String)obj.get("SIC_NAME"));
            map.put("value", mdf.format(DecimalUtil.ConvertNumberToDouble(obj.get("VALUE"))));

            if(!StringUtil.nullToBlank(obj.get("PRETOTAL")).isEmpty()){
                map.put("prevValue", mdf.format(DecimalUtil.ConvertNumberToDouble(obj.get("PRETOTAL"))));
            }

            result.add(map);
        }

        return result;
    }

    /**
     * method name : getMeteringDataDailyDataTotalCount<b/>
     * method Desc : Metering Data 맥스가젯에서 일별 검침데이터의 total count 를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public Integer getMeteringDataDailyDataTotalCount(Map<String, Object> conditionMap) {
        Integer locationId = (Integer)conditionMap.get("locationId");
        Integer permitLocationId = (Integer)conditionMap.get("permitLocationId");
        List<Integer> locationIdList = null;

        if (locationId != null) {
            locationIdList = locationDao.getChildLocationId(locationId);
            locationIdList.add(locationId);
            conditionMap.put("locationIdList", locationIdList);
        } else if (permitLocationId != null) {
            locationIdList = locationDao.getChildLocationId(permitLocationId);
            locationIdList.add(permitLocationId);
            conditionMap.put("locationIdList", locationIdList);
        }

        conditionMap.put("startDate", (String)conditionMap.get("searchStartDate"));
        conditionMap.put("endDate", (String)conditionMap.get("searchEndDate"));

        List<Map<String, Object>> result = meteringdayDao.getMeteringDataDailyData(conditionMap, true);
        return (Integer)(result.get(0).get("total"));
    }
    /**
     * Improvement Version 16-10-03
     * method name : getMeteringDataDailyDataTotalCount<b/>
     * method Desc : Metering Data 맥스가젯에서 일별 검침데이터의 total count 를 조회한다.
     * @param conditionMap
     */
    public Integer getMeteringDataDailyDataTotalCount2(Map<String, Object> conditionMap) {
        Integer locationId = (Integer)conditionMap.get("locationId");
        Integer permitLocationId = (Integer)conditionMap.get("permitLocationId");
        List<Integer> locationIdList = null;

        if (locationId != null) {
            locationIdList = locationDao.getChildLocationId(locationId);
            locationIdList.add(locationId);
            conditionMap.put("locationIdList", locationIdList);
        } else if (permitLocationId != null) {
            locationIdList = locationDao.getChildLocationId(permitLocationId);
            locationIdList.add(permitLocationId);
            conditionMap.put("locationIdList", locationIdList);
        }

        conditionMap.put("startDate", (String)conditionMap.get("searchStartDate"));
        conditionMap.put("endDate", (String)conditionMap.get("searchEndDate"));

        List<Map<String, Object>> result = meteringdayDao.getMeteringDataDailyData2(conditionMap, true);
        return (Integer)(result.get(0).get("total"));
    }

    /**
     * method name : getMeteringDataWeeklyData<b/>
     * method Desc : Metering Data 맥스가젯에서 주별 검침데이터를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getMeteringDataWeeklyData(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        Integer page = (Integer)conditionMap.get("page");
        Integer limit = (Integer)conditionMap.get("limit");
        Integer locationId = (Integer)conditionMap.get("locationId");
        Integer permitLocationId = (Integer)conditionMap.get("permitLocationId");

        List<Integer> locationIdList = null;

        if (locationId != null) {
            locationIdList = locationDao.getChildLocationId(locationId);
            locationIdList.add(locationId);
            conditionMap.put("locationIdList", locationIdList);
        } else if (permitLocationId != null) {
            locationIdList = locationDao.getChildLocationId(permitLocationId);
            locationIdList.add(permitLocationId);
            conditionMap.put("locationIdList", locationIdList);
        }

        String searchStartDate = (String)conditionMap.get("searchStartDate");
        String searchEndDate = (String)conditionMap.get("searchEndDate");
        String searchWeek = (String)conditionMap.get("searchWeek");

        conditionMap.put("startDate", searchStartDate);
        conditionMap.put("endDate", searchEndDate);

        List<Map<String, Object>> list = meteringdayDao.getMeteringDataWeeklyData(conditionMap, false);
        List<Map<String, Object>> prevList = new ArrayList<Map<String, Object>>();
        Map<String, Object> listMap = new HashMap<String, Object>();
        Map<String, Object> map = null;

        Supplier supplier = supplierDao.get(supplierId);
        DecimalFormat mdf = DecimalUtil.getDecimalFormat(supplier.getMd());
        Double prevValue = null;
        Set<String> meterNoList = new HashSet<String>();

        if (list != null && list.size() > 0) {
            if (page != null && limit != null) {        // paging
                for (Map<String, Object> obj : list) {
                    meterNoList.add((String)obj.get("METER_NO"));
                }
                conditionMap.put("meterNoList", meterNoList);
            }

            String prevDate = null; 
            Map<String, String> prevWeek = null;

            try {
                // 조회시작일자 이전일자
                prevDate = TimeUtil.getPreDay(searchStartDate).substring(0, 8);

                // 이전일자 주차 구하기
                int weekNum = CalendarUtil.getWeekOfMonth(prevDate);

                // 주차에 해당하는 from to
                prevWeek = CalendarUtil.getDateWeekOfMonth(prevDate.substring(0, 4), prevDate.substring(4, 6), weekNum+"");
            } catch (ParseException e) {
                e.printStackTrace();
            }

            conditionMap.put("prevStartDate", prevWeek.get("startDate"));
            conditionMap.put("prevEndDate", prevWeek.get("endDate"));

            prevList = meteringdayDao.getMeteringDataWeeklyData(conditionMap, false, true);

            for (Map<String, Object> obj : prevList) {
                listMap.put((String)obj.get("METER_NO"), obj.get("VALUE"));
            }

            if("대성에너지".equals(supplier.getName())) {
                conditionMap.put("startDate", searchStartDate.substring(0, 6));
                conditionMap.put("endDate", searchEndDate.substring(0, 6));
                conditionMap.put("startDetailDate", searchStartDate.substring(6, 8));
                conditionMap.put("endDetailDate", searchEndDate.substring(6, 8));
            
                List<Map<String, Object>> accumulateList = meteringMonthDao.getMeteringDataMonthlyChannel2Data(conditionMap);
            
                for (Map<String, Object> obj : accumulateList) {
                    listMap.put((String)obj.get("METER_NO")+"_ACCUMULATE", obj.get("ACCUMULATEVALUE"));
                }
            }
        }

        int num = 0;

        if (page != null && limit != null) {
            num = ((page - 1) * limit) + 1;
        } else {
            num = 1;
        }

        for (Map<String, Object> obj : list) {
            map = new HashMap<String, Object>();
            Double accumulate = null;
            
            map.put("num", num++);
            map.put("contractNumber", (String)obj.get("CONTRACT_NUMBER"));
            map.put("customerName", (String)obj.get("CUSTOMER_NAME"));
            map.put("meteringTime", searchWeek + " Week");
            map.put("meterNo", (String)obj.get("METER_NO"));
            map.put("modemId", (String)obj.get("MODEM_ID"));
            map.put("sicName", (String)obj.get("SIC_NAME"));
            map.put("value", mdf.format(DecimalUtil.ConvertNumberToDouble(obj.get("VALUE"))));

            prevValue = DecimalUtil.ConvertNumberToDouble(listMap.get((String)obj.get("METER_NO")));
            if("대성에너지".equals(supplier.getName())) 
                accumulate = DecimalUtil.ConvertNumberToDouble(listMap.get((String)obj.get("METER_NO")+"_ACCUMULATE"));
            
            map.put("prevValue", (prevValue == null) ? "" : mdf.format(prevValue));
            if("대성에너지".equals(supplier.getName())) 
                map.put("accumulateValue", (accumulate == null) ? "" : mdf.format(accumulate));
            result.add(map);
        }

        return result;
    }

    /**
     * method name : getMeteringDataWeeklyDataTotalCount<b/>
     * method Desc : Metering Data 맥스가젯에서 주별 검침데이터의 total count 를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public Integer getMeteringDataWeeklyDataTotalCount(Map<String, Object> conditionMap) {
        Integer locationId = (Integer)conditionMap.get("locationId");
        Integer permitLocationId = (Integer)conditionMap.get("permitLocationId");
        List<Integer> locationIdList = null;

        if (locationId != null) {
            locationIdList = locationDao.getChildLocationId(locationId);
            locationIdList.add(locationId);
            conditionMap.put("locationIdList", locationIdList);
        } else if (permitLocationId != null) {
            locationIdList = locationDao.getChildLocationId(permitLocationId);
            locationIdList.add(permitLocationId);
            conditionMap.put("locationIdList", locationIdList);
        }

        conditionMap.put("meterNoList", null);
        conditionMap.put("startDate", (String)conditionMap.get("searchStartDate"));
        conditionMap.put("endDate", (String)conditionMap.get("searchEndDate"));

        List<Map<String, Object>> result = meteringdayDao.getMeteringDataWeeklyData(conditionMap, true);
        return (Integer)(result.get(0).get("total"));
    }

    /**
     * method name : getMeteringDataWeekDailyData<b/>
     * method Desc : Metering Data 맥스가젯에서 요일별 검침데이터를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getMeteringDataWeekDailyData(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        Integer page = (Integer)conditionMap.get("page");
        Integer limit = (Integer)conditionMap.get("limit");
        Integer locationId = (Integer)conditionMap.get("locationId");
        Integer permitLocationId = (Integer)conditionMap.get("permitLocationId");

        List<Integer> locationIdList = null;

        if (locationId != null) {
            locationIdList = locationDao.getChildLocationId(locationId);
            locationIdList.add(locationId);
            conditionMap.put("locationIdList", locationIdList);
        } else if (permitLocationId != null) {
            locationIdList = locationDao.getChildLocationId(permitLocationId);
            locationIdList.add(permitLocationId);
            conditionMap.put("locationIdList", locationIdList);
        }

        String searchStartDate = (String)conditionMap.get("searchStartDate");
        String searchEndDate = (String)conditionMap.get("searchEndDate");
        String searchPrevStartDate = null;

        if (page != null && limit != null) {        // paging
            conditionMap.put("startDate", searchStartDate);
        } else {        // all
            try {
                searchPrevStartDate = TimeUtil.getPreDay(searchStartDate).substring(0, 8);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            conditionMap.put("startDate", searchPrevStartDate);
        }

        conditionMap.put("endDate", searchEndDate);

        List<Map<String, Object>> list = meteringdayDao.getMeteringDataDailyData(conditionMap, false);
        List<Map<String, Object>> prevList = new ArrayList<Map<String, Object>>();
        Map<String, Object> listMap = new HashMap<String, Object>();
        Map<String, Object> map = null;

        Supplier supplier = supplierDao.get(supplierId);
        String country = supplier.getCountry().getCode_2letter();
        String lang    = supplier.getLang().getCode_2letter();
        DecimalFormat mdf = DecimalUtil.getDecimalFormat(supplier.getMd());
        Double prevValue = null;
        int num = 0;

        if (page != null && limit != null) {        // paging
            int cnt = 0;
            String prevStartDate = null;
            String prevEndDate = null;
            Map<String, Object> fstMap = null;
            Map<String, Object> lstMap = null;

            if (list != null && list.size() > 0) {
                cnt = list.size();
                fstMap = list.get(0);
                lstMap = list.get(cnt-1);
                try {
                    prevStartDate = TimeUtil.getPreDay((String)fstMap.get("YYYYMMDD")).substring(0, 8);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                prevEndDate = (String)lstMap.get("YYYYMMDD");

                conditionMap.put("prevStartDate", prevStartDate);
                conditionMap.put("prevEndDate", prevEndDate);

                prevList = meteringdayDao.getMeteringDataDailyData(conditionMap, false, true);

                for (Map<String, Object> obj : prevList) {
                    listMap.put((String)obj.get("YYYYMMDD") + "_" + (String)obj.get("METER_NO"), obj.get("VALUE"));
                }
            }
        } else {        // all
            for (Map<String, Object> obj : list) {
                listMap.put((String)obj.get("YYYYMMDD") + "_" + (String)obj.get("METER_NO"), obj.get("VALUE"));
            }
        }

        if (page != null && limit != null) {
            num = ((page - 1) * limit) + 1;
        } else {
            num = 1;
        }

        for (Map<String, Object> obj : list) {
            // 전체조회일 경우 이전일자 데이터는 skip
            if ((page == null || limit == null) && ((String)obj.get("YYYYMMDD")).compareTo(searchPrevStartDate) == 0) {
                continue;
            }

            map = new HashMap<String, Object>();

            map.put("num", num++);
            map.put("contractNumber", (String)obj.get("CONTRACT_NUMBER"));
            map.put("customerName", (String)obj.get("CUSTOMER_NAME"));
            map.put("meteringTime", TimeLocaleUtil.getLocaleWeekDay((String)obj.get("YYYYMMDD"), lang, country));
            map.put("meterNo", (String)obj.get("METER_NO"));
            map.put("modemId", (String)obj.get("MODEM_ID"));
            map.put("sicName", (String)obj.get("SIC_NAME"));       
            map.put("value", mdf.format(DecimalUtil.ConvertNumberToDouble(obj.get("VALUE"))));

            try {
                prevValue = DecimalUtil.ConvertNumberToDouble(listMap.get(TimeUtil.getPreDay((String)obj.get("YYYYMMDD")).substring(0, 8) + "_" + (String)obj.get("METER_NO")));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            map.put("prevValue", (prevValue == null) ? "" : mdf.format(prevValue));
            result.add(map);
        }

        return result;
    }

    /**
     * method name : getMeteringDataWeekDailyDataTotalCount<b/>
     * method Desc : Metering Data 맥스가젯에서 요일별 검침데이터의 total count 를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public Integer getMeteringDataWeekDailyDataTotalCount(Map<String, Object> conditionMap) {
        Integer locationId = (Integer)conditionMap.get("locationId");
        Integer permitLocationId = (Integer)conditionMap.get("permitLocationId");
        List<Integer> locationIdList = null;

        if (locationId != null) {
            locationIdList = locationDao.getChildLocationId(locationId);
            locationIdList.add(locationId);
            conditionMap.put("locationIdList", locationIdList);
        } else if (permitLocationId != null) {
            locationIdList = locationDao.getChildLocationId(permitLocationId);
            locationIdList.add(permitLocationId);
            conditionMap.put("locationIdList", locationIdList);
        }

        conditionMap.put("startDate", (String)conditionMap.get("searchStartDate"));
        conditionMap.put("endDate", (String)conditionMap.get("searchEndDate"));

        List<Map<String, Object>> result = meteringdayDao.getMeteringDataDailyData(conditionMap, true);
        return (Integer)(result.get(0).get("total"));
    }
    
    /**
     * method name : getMeteringValueMonthlyData<b/>
     * method Desc : Metering Data 맥스가젯에서 LastDay 테이블을 이용해 월별 지침값을 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getMeteringValueMonthlyData(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        Integer page = (Integer)conditionMap.get("page");
        Integer limit = (Integer)conditionMap.get("limit");
        Integer locationId = (Integer)conditionMap.get("locationId");
        Integer permitLocationId = (Integer)conditionMap.get("permitLocationId");

        List<Integer> locationIdList = null;

        if (locationId != null) {
            locationIdList = locationDao.getChildLocationId(locationId);
            locationIdList.add(locationId);
            conditionMap.put("locationIdList", locationIdList);
        } else if (permitLocationId != null) {
            locationIdList = locationDao.getChildLocationId(permitLocationId);
            locationIdList.add(permitLocationId);
            conditionMap.put("locationIdList", locationIdList);
        }

        String searchStartDate = (String)conditionMap.get("searchStartDate");
        String searchEndDate = (String)conditionMap.get("searchEndDate");
        
        conditionMap.put("startDate", searchStartDate.substring(0, 6));
        conditionMap.put("endDate", searchEndDate.substring(0, 6));

        //당월 데이터 조회
        List<Map<String, Object>> list = meteringdayDao.getMeteringValueMonthlyData(conditionMap, false);
        List<Map<String, Object>> prevList = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> prev2List = new ArrayList<Map<String, Object>>();
        Map<String, Object> pevListMap = new HashMap<String, Object>();
        Map<String, Object> pev2ListMap = new HashMap<String, Object>();
        Map<String, Object> map = null;

        Supplier supplier = supplierDao.get(supplierId);
        String country = supplier.getCountry().getCode_2letter();
        String lang    = supplier.getLang().getCode_2letter();
        DecimalFormat mdf = DecimalUtil.getDecimalFormat(supplier.getMd());
        Double prevValue = null;
        Double prev2Value = null;

        int cnt = 0;
        String subStartDate = null;
        String subvEndDate = null;

        if (list != null && list.size() > 0) {
            cnt = list.size();
            try {
                // 이전 달에 대한 데이터
                subStartDate = TimeUtil.getPreMonth(searchStartDate).substring(0, 6);
                subvEndDate = TimeUtil.getPreMonth(searchEndDate).substring(0, 6);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            
            conditionMap.put("subStartDate", subStartDate);
            conditionMap.put("subEndDate", subvEndDate);

            prevList = meteringdayDao.getMeteringValueMonthlyData(conditionMap, false, true);
            
            try {
                subStartDate = TimeUtil.getAddedMonth(searchStartDate, 1).substring(0, 6);
                subvEndDate = TimeUtil.getAddedMonth(searchEndDate,1).substring(0, 6);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            
            conditionMap.put("subStartDate", subStartDate);
            conditionMap.put("subEndDate", subvEndDate);
            
            //이이전 데이터 조회
            prev2List = meteringdayDao.getMeteringValueMonthlyData(conditionMap, false, true);
            for (Map<String, Object> obj : prevList) {
                pevListMap.put((String)obj.get("METER_NO"), obj.get("BASEVALUE"));
            }
            
            for (Map<String, Object> obj : prev2List) {
                pev2ListMap.put(((String)obj.get("METER_NO")), obj.get("BASEVALUE"));
            }
        }

        int num = 0;
        
        if (page != null && limit != null) {
            num = ((page - 1) * limit) + 1;
        } else {
            num = 1;
        }

        for (Map<String, Object> obj : list) {
            map = new HashMap<String, Object>();

            map.put("num", num++);
            map.put("contractNumber", (String)obj.get("CONTRACT_NUMBER"));
            map.put("customerName", (String)obj.get("CUSTOMER_NAME"));
            map.put("friendlyName", (String)obj.get("FRIENDLY_NAME"));
            map.put("meteringTime", TimeLocaleUtil.getLocaleDate(((String)obj.get("YYYYMMDD")+"000000"), lang, country));

            map.put("meterNo", (String)obj.get("METER_NO"));
            map.put("modemId", (String)obj.get("MODEM_ID"));
            map.put("sicName", (String)obj.get("SIC_NAME"));
            //당월 마지막 검침일의 baseValue
            String value = obj.get("BASEVALUE") == null ? null : mdf.format(DecimalUtil.ConvertNumberToDouble(obj.get("BASEVALUE")));
            map.put("value", value);
            
            Double accumulate = null;
            try {
                prevValue = pevListMap.get((String)obj.get("METER_NO")) == null ? null : DecimalUtil.ConvertNumberToDouble(pevListMap.get((String)obj.get("METER_NO")));
                prev2Value = pev2ListMap.get((String)obj.get("METER_NO")) == null ? null : DecimalUtil.ConvertNumberToDouble(pev2ListMap.get((String)obj.get("METER_NO")));
            } catch (Exception e) {
                logger.error(e,e);
            }

            map.put("prevValue", (prevValue == null) ? "" : mdf.format(prevValue));
            map.put("prevUsage", (prev2Value == null ||prevValue == null) ? "" : mdf.format(prevValue - prev2Value));
            map.put("usage", (value == null ||prevValue == null) ? "" : mdf.format(DecimalUtil.ConvertNumberToDouble(obj.get("BASEVALUE")) - prevValue));
            if("대성에너지".equals(supplier.getName())) 
                map.put("accumulateValue", (accumulate == null) ? "" : mdf.format(accumulate));
            result.add(map);
        }

        return result;        
    }
    
    /**
     * method name : getMeteringValueMonthlyDataTotalCount<b/>
     * method Desc : Metering Data 맥스가젯에서 월별 검침데이터의 total count 를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public Integer getMeteringValueMonthlyDataTotalCount(Map<String, Object> conditionMap) {
        Integer locationId = (Integer)conditionMap.get("locationId");
        Integer permitLocationId = (Integer)conditionMap.get("permitLocationId");
        String searchStartDate = (String)conditionMap.get("searchStartDate");
        String searchEndDate = (String)conditionMap.get("searchEndDate");
        List<Integer> locationIdList = null;

        if (locationId != null) {
            locationIdList = locationDao.getChildLocationId(locationId);
            locationIdList.add(locationId);
            conditionMap.put("locationIdList", locationIdList);
        } else if (permitLocationId != null) {
            locationIdList = locationDao.getChildLocationId(permitLocationId);
            locationIdList.add(permitLocationId);
            conditionMap.put("locationIdList", locationIdList);
        }

        conditionMap.put("startDate", searchStartDate.substring(0, 6));
        conditionMap.put("endDate", searchEndDate.substring(0, 6));

        List<Map<String, Object>> result = meteringMonthDao.getMeteringDataMonthlyData(conditionMap, true);
        return (Integer)(result.get(0).get("total"));
    }

    /**
     * method name : getMeteringDataMonthlyData<b/>
     * method Desc : Metering Data 맥스가젯에서 월별 검침데이터를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getMeteringDataMonthlyData(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        Integer page = (Integer)conditionMap.get("page");
        Integer limit = (Integer)conditionMap.get("limit");
        Integer locationId = (Integer)conditionMap.get("locationId");
        Integer permitLocationId = (Integer)conditionMap.get("permitLocationId");

        List<Integer> locationIdList = null;

        if (locationId != null) {
            locationIdList = locationDao.getChildLocationId(locationId);
            locationIdList.add(locationId);
            conditionMap.put("locationIdList", locationIdList);
        } else if (permitLocationId != null) {
            locationIdList = locationDao.getChildLocationId(permitLocationId);
            locationIdList.add(permitLocationId);
            conditionMap.put("locationIdList", locationIdList);
        }

        String searchStartDate = (String)conditionMap.get("searchStartDate");
        String searchEndDate = (String)conditionMap.get("searchEndDate");
        String searchPrevStartDate = null;

        if (page != null && limit != null) {        // paging
            conditionMap.put("startDate", searchStartDate.substring(0, 6));
        } else {        // all
            try {
                searchPrevStartDate = TimeUtil.getPreMonth(searchStartDate).substring(0, 6);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            conditionMap.put("startDate", searchPrevStartDate);
        }

        conditionMap.put("endDate", searchEndDate.substring(0, 6));

        List<Map<String, Object>> list = meteringMonthDao.getMeteringDataMonthlyData(conditionMap, false);
        List<Map<String, Object>> prevList = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> accumulateList = new ArrayList<Map<String, Object>>();
        Map<String, Object> listMap = new HashMap<String, Object>();
        Map<String, Object> map = null;

        Supplier supplier = supplierDao.get(supplierId);
        String country = supplier.getCountry().getCode_2letter();
        String lang    = supplier.getLang().getCode_2letter();
        DecimalFormat mdf = DecimalUtil.getDecimalFormat(supplier.getMd());
        Double prevValue = null;

        if (page != null && limit != null) {        // paging
            int cnt = 0;
            String prevStartDate = null;
            String prevEndDate = null;
            Map<String, Object> fstMap = null;
            Map<String, Object> lstMap = null;

            if (list != null && list.size() > 0) {
                cnt = list.size();
                fstMap = list.get(0);
                lstMap = list.get(cnt-1);
                try {
                    prevStartDate = TimeUtil.getPreMonth((String)fstMap.get("YYYYMM") + "01").substring(0, 6);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                prevEndDate = (String)lstMap.get("YYYYMM");

                conditionMap.put("prevStartDate", prevStartDate);
                conditionMap.put("prevEndDate", prevEndDate);

                prevList = meteringMonthDao.getMeteringDataMonthlyData(conditionMap, false, true);

                for (Map<String, Object> obj : prevList) {
                    listMap.put((String)obj.get("YYYYMM") + "_" + (String)obj.get("METER_NO"), obj.get("VALUE"));
                }
            }
        } else {        // all
            for (Map<String, Object> obj : list) {
                listMap.put((String)obj.get("YYYYMM") + "_" + (String)obj.get("METER_NO"), obj.get("VALUE"));
            }
            
            //채널 2번(누적유효전력량) 추가
            if("대성에너지".equals(supplier.getName())) {
                conditionMap.put("startDate", searchStartDate.substring(0,6));
                conditionMap.put("endDate", searchEndDate.substring(0,6));
                conditionMap.put("startDetailDate", searchStartDate.substring(6,8));
                conditionMap.put("endDetailDate", searchEndDate.substring(6,8));
            
                accumulateList = meteringMonthDao.getMeteringDataMonthlyChannel2Data(conditionMap);
            
                for (Map<String, Object> obj : accumulateList) {
                    listMap.put((String)obj.get("METER_NO")+"_ACCUMULATE", obj.get("ACCUMULATEVALUE"));
                }
            }
        }

        int num = 0;
        
        if (page != null && limit != null) {
            num = ((page - 1) * limit) + 1;
        } else {
            num = 1;
        }

        for (Map<String, Object> obj : list) {
            // 전체조회일 경우 이전일자 데이터는 skip
            if ((page == null || limit == null) && ((String)obj.get("YYYYMM")).compareTo(searchPrevStartDate) == 0) {
                continue;
            }

            map = new HashMap<String, Object>();

            map.put("num", num++);
            map.put("contractNumber", (String)obj.get("CONTRACT_NUMBER"));
            map.put("customerName", (String)obj.get("CUSTOMER_NAME"));
            map.put("friendlyName", (String)obj.get("FRIENDLY_NAME"));
            map.put("meteringTime", TimeLocaleUtil.getLocaleYearMonth((String)obj.get("YYYYMM"), lang, country));

            map.put("meterNo", (String)obj.get("METER_NO"));
            map.put("modemId", (String)obj.get("MODEM_ID"));
            map.put("sicName", (String)obj.get("SIC_NAME"));
            map.put("value", mdf.format(DecimalUtil.ConvertNumberToDouble(obj.get("VALUE"))));
            String lastMeteringValue = obj.get("LAST_METERING_VALUE") == null ? null : mdf.format(DecimalUtil.ConvertNumberToDouble(obj.get("LAST_METERING_VALUE")));
            map.put("meterValue", lastMeteringValue);
            //Monthly로 겁색할때 표시하는 값이며 해당월의 baseValue + total 값으로 이루어져 있다.
            map.put("meteringValue", obj.get("METERING_VALUE") == null ? null : mdf.format(obj.get("METERING_VALUE")));

            Double accumulate = null;
            try {
                prevValue = DecimalUtil.ConvertNumberToDouble(listMap.get(TimeUtil.getPreMonth((String)obj.get("YYYYMM") + "01").substring(0, 6) + "_" + (String)obj.get("METER_NO")));
                if("대성에너지".equals(supplier.getName())) 
                    accumulate = DecimalUtil.ConvertNumberToDouble(listMap.get((String)obj.get("METER_NO")+"_ACCUMULATE"));
            } catch (ParseException e) {
                logger.error(e,e);
            }

            map.put("prevValue", (prevValue == null) ? "" : mdf.format(prevValue));
            if("대성에너지".equals(supplier.getName())) 
                map.put("accumulateValue", (accumulate == null) ? "" : mdf.format(accumulate));
            result.add(map);
        }

        return result;        
    }

    /**
     * method name : getMeteringDataMonthlyDataTotalCount<b/>
     * method Desc : Metering Data 맥스가젯에서 월별 검침데이터의 total count 를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public Integer getMeteringDataMonthlyDataTotalCount(Map<String, Object> conditionMap) {
        Integer locationId = (Integer)conditionMap.get("locationId");
        Integer permitLocationId = (Integer)conditionMap.get("permitLocationId");
        String searchStartDate = (String)conditionMap.get("searchStartDate");
        String searchEndDate = (String)conditionMap.get("searchEndDate");
        List<Integer> locationIdList = null;

        if (locationId != null) {
            locationIdList = locationDao.getChildLocationId(locationId);
            locationIdList.add(locationId);
            conditionMap.put("locationIdList", locationIdList);
        } else if (permitLocationId != null) {
            locationIdList = locationDao.getChildLocationId(permitLocationId);
            locationIdList.add(permitLocationId);
            conditionMap.put("locationIdList", locationIdList);
        }

        conditionMap.put("startDate", searchStartDate.substring(0, 6));
        conditionMap.put("endDate", searchEndDate.substring(0, 6));

        List<Map<String, Object>> result = meteringMonthDao.getMeteringDataMonthlyData(conditionMap, true);
        return (Integer)(result.get(0).get("total"));
    }

    /**
     * method name : getMeteringDataSeasonalData<b/>
     * method Desc : Metering Data 맥스가젯에서 계절별 검침데이터를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getMeteringDataSeasonalData(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        Integer page = (Integer)conditionMap.get("page");
        Integer limit = (Integer)conditionMap.get("limit");
        Integer locationId = (Integer)conditionMap.get("locationId");
        Integer permitLocationId = (Integer)conditionMap.get("permitLocationId");
        List<Integer> locationIdList = null;

        if (locationId != null) {
            locationIdList = locationDao.getChildLocationId(locationId);
            locationIdList.add(locationId);
            conditionMap.put("locationIdList", locationIdList);
        } else if (permitLocationId != null) {
            locationIdList = locationDao.getChildLocationId(permitLocationId);
            locationIdList.add(permitLocationId);
            conditionMap.put("locationIdList", locationIdList);
        }

        String searchStartDate = (String)conditionMap.get("searchStartDate");
        String searchEndDate = (String)conditionMap.get("searchEndDate");
        String searchSeason = null;
        Boolean hasDay = false;     // 계절데이터에 일자 포함 여부. 있으면 일자로 조회, 없으면 월로 조회.
        Map<String, String> seasonMap = seasonManager.getSeasonPeriodByDate(searchStartDate);

        if (seasonMap != null) {
            searchSeason = seasonMap.get("seasonName");
            hasDay = Boolean.valueOf(seasonMap.get("hasDay"));
        }
        
        List<Map<String, Object>> list = null;
        
        if (hasDay) {   // 계절기간에 일자가 포함되어있을 경우 일자로 조회
            conditionMap.put("startDate", searchStartDate);
            conditionMap.put("endDate", searchEndDate);
            list = meteringdayDao.getMeteringDataWeeklyData(conditionMap, false);   // weekly 와 조회 query 가 동일하므로 재사용
        } else {        // 계절기간에 일자가 포함안되어있을 경우 월로 조회
            conditionMap.put("startDate", searchStartDate.substring(0, 6));
            conditionMap.put("endDate", searchEndDate.substring(0, 6));
            list = meteringMonthDao.getMeteringDataYearlyData(conditionMap, false); // yearly 와 조회 query 가 동일하므로 재사용
        }

        List<Map<String, Object>> prevList = new ArrayList<Map<String, Object>>();
        Map<String, Object> listMap = new HashMap<String, Object>();
        Map<String, Object> map = null;

        Supplier supplier = supplierDao.get(supplierId);
        DecimalFormat mdf = DecimalUtil.getDecimalFormat(supplier.getMd());
        Double prevValue = null;
        Set<String> meterNoList = new HashSet<String>();

        if (list != null && list.size() > 0) {
            if (page != null && limit != null) {        // paging
                for (Map<String, Object> obj : list) {
                    meterNoList.add((String)obj.get("METER_NO"));
                }
                conditionMap.put("meterNoList", meterNoList);
            }

            String prevDate = null; 
            Map<String, String> prevSeasonMap = null;

            try {
                // 조회시작일자 이전일자
                prevDate = TimeUtil.getPreDay(searchStartDate).substring(0, 8);

                // 이전일자 계절주기 구하기
                prevSeasonMap = seasonManager.getSeasonPeriodByDate(prevDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (hasDay) {   // 계절기간에 일자가 포함되어있을 경우 일자로 조회
                conditionMap.put("prevStartDate", prevSeasonMap.get("startDate"));
                conditionMap.put("prevEndDate", prevSeasonMap.get("endDate"));
                prevList = meteringdayDao.getMeteringDataWeeklyData(conditionMap, false, true);     // weekly 와 조회 query 가 동일하므로 재사용
            } else {        // 계절기간에 일자가 포함안되어있을 경우 월로 조회
                conditionMap.put("prevStartDate", prevSeasonMap.get("startDate").substring(0, 6));
                conditionMap.put("prevEndDate", prevSeasonMap.get("endDate").substring(0, 6));
                prevList = meteringMonthDao.getMeteringDataYearlyData(conditionMap, false, true);   // yearly 와 조회 query 가 동일하므로 재사용
            }

            for (Map<String, Object> obj : prevList) {
                listMap.put((String)obj.get("METER_NO"), obj.get("VALUE"));
            }
        }

        int num = 0;
        
        if (page != null && limit != null) {
            num = ((page - 1) * limit) + 1;
        } else {
            num = 1;
        }

        for (Map<String, Object> obj : list) {
            map = new HashMap<String, Object>();

            map.put("num", num++);
            map.put("contractNumber", (String)obj.get("CONTRACT_NUMBER"));
            map.put("customerName", (String)obj.get("CUSTOMER_NAME"));
            map.put("meteringTime", searchSeason);
            map.put("meterNo", (String)obj.get("METER_NO"));
            map.put("modemId", (String)obj.get("MODEM_ID"));
            map.put("sicName", (String)obj.get("SIC_NAME"));       
            map.put("value", mdf.format(DecimalUtil.ConvertNumberToDouble(obj.get("VALUE"))));

            prevValue = DecimalUtil.ConvertNumberToDouble(listMap.get((String)obj.get("METER_NO")));

            map.put("prevValue", (prevValue == null) ? "" : mdf.format(prevValue));
            result.add(map);
        }

        return result;
    }

    /**
     * method name : getMeteringDataSeasonalDataTotalCount<b/>
     * method Desc : Metering Data 맥스가젯에서 계절별 검침데이터의 total count 를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public Integer getMeteringDataSeasonalDataTotalCount(Map<String, Object> conditionMap) {
        Integer locationId = (Integer)conditionMap.get("locationId");
        Integer permitLocationId = (Integer)conditionMap.get("permitLocationId");
        List<Integer> locationIdList = null;
        String searchStartDate = (String)conditionMap.get("searchStartDate");
        String searchEndDate = (String)conditionMap.get("searchEndDate");

        if (locationId != null) {
            locationIdList = locationDao.getChildLocationId(locationId);
            locationIdList.add(locationId);
            conditionMap.put("locationIdList", locationIdList);
        } else if (permitLocationId != null) {
            locationIdList = locationDao.getChildLocationId(permitLocationId);
            locationIdList.add(permitLocationId);
            conditionMap.put("locationIdList", locationIdList);
        }

        conditionMap.put("meterNoList", null);
        Boolean hasDay = false;     // 계절데이터에 일자 포함 여부. 있으면 일자로 조회, 없으면 월로 조회.
        Map<String, String> seasonMap = seasonManager.getSeasonPeriodByDate(searchStartDate);
        List<Map<String, Object>> result = null;

        if (seasonMap != null) {
            hasDay = Boolean.valueOf(seasonMap.get("hasDay"));
        }

        if (hasDay) {   // 계절기간에 일자가 포함되어있을 경우 일자로 조회
            conditionMap.put("startDate", searchStartDate);
            conditionMap.put("endDate", searchEndDate);
            result = meteringdayDao.getMeteringDataWeeklyData(conditionMap, true);   // weekly 와 조회 query 가 동일하므로 재사용
        } else {        // 계절기간에 일자가 포함안되어있을 경우 월로 조회
            conditionMap.put("startDate", searchStartDate.substring(0, 6));
            conditionMap.put("endDate", searchEndDate.substring(0, 6));
            result = meteringMonthDao.getMeteringDataYearlyData(conditionMap, true); // yearly 와 조회 query 가 동일하므로 재사용
        }

        return (Integer)(result.get(0).get("total"));
    }

    
    /**
     * method name : getMeteringDataYearlyData<b/>
     * method Desc : Metering Data 맥스가젯에서 연간 검침데이터를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getMeteringDataYearlyData(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        Integer page = (Integer)conditionMap.get("page");
        Integer limit = (Integer)conditionMap.get("limit");
        Integer locationId = (Integer)conditionMap.get("locationId");
        Integer permitLocationId = (Integer)conditionMap.get("permitLocationId");

        List<Integer> locationIdList = null;

        if (locationId != null) {
            locationIdList = locationDao.getChildLocationId(locationId);
            locationIdList.add(locationId);
            conditionMap.put("locationIdList", locationIdList);
        } else if (permitLocationId != null) {
            locationIdList = locationDao.getChildLocationId(permitLocationId);
            locationIdList.add(permitLocationId);
            conditionMap.put("locationIdList", locationIdList);
        }

        String searchStartDate = (String)conditionMap.get("searchStartDate");
        String searchEndDate = (String)conditionMap.get("searchEndDate");
        String searchYear = searchStartDate.substring(0, 4);

        conditionMap.put("startDate", searchStartDate.substring(0, 6));
        conditionMap.put("endDate", searchEndDate.substring(0, 6));

        List<Map<String, Object>> list = meteringMonthDao.getMeteringDataYearlyData(conditionMap, false);
        List<Map<String, Object>> prevList = new ArrayList<Map<String, Object>>();
        Map<String, Object> listMap = new HashMap<String, Object>();
        Map<String, Object> map = null;

        Supplier supplier = supplierDao.get(supplierId);
        DecimalFormat mdf = DecimalUtil.getDecimalFormat(supplier.getMd());
        Double value = null;
        Double prevValue = null;
        Set<String> meterNoList = new HashSet<String>();

        if (list != null && list.size() > 0) {
            if (page != null && limit != null) {        // paging
                for (Map<String, Object> obj : list) {
                    meterNoList.add((String)obj.get("METER_NO"));
                }
                conditionMap.put("meterNoList", meterNoList);
            }
            String prevYear = null; 

            try {
                // 조회이전 년도
                prevYear = TimeUtil.getPreMonth(searchStartDate).substring(0, 4);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            conditionMap.put("prevStartDate", prevYear + "01");
            conditionMap.put("prevEndDate", prevYear + "12");

            prevList = meteringMonthDao.getMeteringDataYearlyData(conditionMap, false, true);

            for (Map<String, Object> obj : prevList) {
                listMap.put((String)obj.get("METER_NO"), obj.get("VALUE"));
            }
        }

        int num = 0;
        
        if (page != null && limit != null) {
            num = ((page - 1) * limit) + 1;
        } else {
            num = 1;
        }

        for (Map<String, Object> obj : list) {
            map = new HashMap<String, Object>();

            map.put("num", num++);
            map.put("contractNumber", (String)obj.get("CONTRACT_NUMBER"));
            map.put("customerName", (String)obj.get("CUSTOMER_NAME"));
            map.put("meteringTime", searchYear);
            map.put("meterNo", (String)obj.get("METER_NO"));
            map.put("modemId", (String)obj.get("MODEM_ID"));
            map.put("sicName", (String)obj.get("SIC_NAME"));       
            
            value = DecimalUtil.ConvertNumberToDouble(obj.get("VALUE"));
            map.put("value", (value == null) ? "" : mdf.format(value));

            prevValue = DecimalUtil.ConvertNumberToDouble(listMap.get((String)obj.get("METER_NO")));
            map.put("prevValue", (prevValue == null) ? "" : mdf.format(prevValue));
            result.add(map);
        }

        return result;        
    }

    /**
     * method name : getMeteringDataYearlyDataTotalCount<b/>
     * method Desc : Metering Data 맥스가젯에서 연간 검침데이터의 total count 를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public Integer getMeteringDataYearlyDataTotalCount(Map<String, Object> conditionMap) {
        Integer locationId = (Integer)conditionMap.get("locationId");
        Integer permitLocationId = (Integer)conditionMap.get("permitLocationId");
        List<Integer> locationIdList = null;
        String searchStartDate = (String)conditionMap.get("searchStartDate");
        String searchEndDate = (String)conditionMap.get("searchEndDate");

        if (locationId != null) {
            locationIdList = locationDao.getChildLocationId(locationId);
            locationIdList.add(locationId);
            conditionMap.put("locationIdList", locationIdList);
        } else if (permitLocationId != null) {
            locationIdList = locationDao.getChildLocationId(permitLocationId);
            locationIdList.add(permitLocationId);
            conditionMap.put("locationIdList", locationIdList);
        }

        conditionMap.put("meterNoList", null);
        conditionMap.put("startDate", searchStartDate.substring(0, 6));
        conditionMap.put("endDate", searchEndDate.substring(0, 6));

        List<Map<String, Object>> result = meteringMonthDao.getMeteringDataYearlyData(conditionMap, true);
        return (Integer)(result.get(0).get("total"));
    }

	public List<Map<String, Object>> getRealTimeMeterValues(Map<String, Object> conditionMap) {
		return meteringdayDao.getRealTimeMeterValues(conditionMap, false);
	}

	public Integer getRealTimeMeterValuesTotalCount(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result = meteringdayDao.getRealTimeMeterValues(conditionMap, true);
        
        return (Integer)(result.get(0).get("total"));
	}
}