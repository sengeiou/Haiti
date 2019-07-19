package com.aimir.dao.mvm.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.mvm.MeteringMonthDao;
import com.aimir.model.mvm.MeteringMonth;
import com.aimir.util.Condition;

@Repository(value = "meteringmonthDao")
@SuppressWarnings("unchecked")
public class MeteringMonthDaoImpl extends AbstractJpaDao<MeteringMonth, Integer> implements MeteringMonthDao {

//  private static Log logger = LogFactory.getLog(MeteringMonthDaoImpl.class);
    
    public MeteringMonthDaoImpl() {
        super(MeteringMonth.class);
    }

    @Override
    @Deprecated
    public List<Object> getConsumptionRanking(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    @Deprecated
    public List<Object> getConsumptionRankingList(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getConsumptionRankingDataList(
            Map<String, Object> conditionMap, boolean isCount) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getConsumptionRankingDataList(
            Map<String, Object> conditionMap, boolean isCount, boolean isAll) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    @Deprecated
    public List<Object> getDetailMonthSearchData(
            HashMap<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    @Deprecated
    public List<Object> getDetailMonthMaxMinAvgSumData(
            HashMap<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getOverlayChartMonthlyData(
            Map<String, Object> condition, Integer contractId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getDetailDailySearchData(
            Map<String, Object> condition, boolean isSum) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getDetailMonthlySearchData(
            Map<String, Object> condition, boolean isSum) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getMeteringDataMonthlyData(
            Map<String, Object> conditionMap, boolean isTotal) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getMeteringDataMonthlyChannel2Data(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getMeteringDataMonthlyData(
            Map<String, Object> conditionMap, boolean isTotal, boolean isPrev) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getMeteringDataYearlyData(
            Map<String, Object> conditionMap, boolean isTotal) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getMeteringDataYearlyData(
            Map<String, Object> conditionMap, boolean isTotal, boolean isPrev) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getMeteringDataDetailMonthlyData(
            Map<String, Object> conditionMap, boolean isSum) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<MeteringMonth> getPersistentClass() {
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