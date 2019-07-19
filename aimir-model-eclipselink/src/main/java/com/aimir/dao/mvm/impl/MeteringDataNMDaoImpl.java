package com.aimir.dao.mvm.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.mvm.MeteringDataNMDao;
import com.aimir.model.mvm.MeteringDataNM;
import com.aimir.util.Condition;

@Repository(value = "meteringdatanmDao")
public class MeteringDataNMDaoImpl extends AbstractJpaDao<MeteringDataNM, Integer> implements MeteringDataNMDao {

    private static Log logger = LogFactory.getLog(MeteringDataDaoImpl.class);

    public MeteringDataNMDaoImpl() {
        super(MeteringDataNM.class);
    }

    @Override
    public Class<MeteringDataNM> getPersistentClass() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getSumFieldByCondition(Set<Condition> conditions,
            String field, String... groupBy) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getMeteringData(Map<String, Object> conditionMap, boolean isTotal) {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public List<Map<String, Object>> getMeteringDataDetail(Map<String, Object> conditionMap) {
        return null;
    }
    
    public List<Map<String, Object>> getMeteringSuccessCountListPerLocation(Map<String, Object> conditionMap){
        return null;
    }
}
