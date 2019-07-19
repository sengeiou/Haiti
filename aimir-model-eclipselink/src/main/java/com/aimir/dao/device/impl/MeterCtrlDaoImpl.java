package com.aimir.dao.device.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.MeterCtrlDao;
import com.aimir.model.device.MeterCtrl;
import com.aimir.util.Condition;
import com.aimir.util.SQLWrapper;


@Repository(value = "meterCtrlDao")
public class MeterCtrlDaoImpl extends AbstractJpaDao<MeterCtrl, Integer> implements MeterCtrlDao {

    Log logger = LogFactory.getLog(MeterCtrlDaoImpl.class);
    
    public MeterCtrlDaoImpl() {
		super(MeterCtrl.class);
	}

    /**
     * method name : getMeterCommandResultData
     * method Desc : Meter Command 실행 결과 값을 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<MeterCtrl> getMeterCommandResultData(Map<String, Object> conditionMap) {
        String ctrlId = (String)conditionMap.get("ctrlId");
        Integer meterId = (Integer)conditionMap.get("meterId");
        String writeDate = (String)conditionMap.get("writeDate");

        StringBuilder sb = new StringBuilder();

        sb.append("select m ");
        sb.append("\nFROM MeterCtrl m ");
        sb.append("\nWHERE m.id.ctrlId  = :ctrlId ");
        sb.append("\nAND   m.id.meter.id  = :meterId ");
        sb.append("\nAND   m.id.writeDate  = :writeDate ");

        Query query = em.createQuery(sb.toString(), MeterCtrl.class);

        query.setParameter("ctrlId", ctrlId);
        query.setParameter("meterId", meterId);
        query.setParameter("writeDate", writeDate);

        return query.getResultList();
    }

    /**
     * method name : getMeterCommandRunCheck
     * method Desc : MDIS - Meter Command 실행 여부를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<MeterCtrl> getMeterCommandRunCheck(Map<String, Object> conditionMap) {
        List<MeterCtrl> result;

        Integer meterId = (Integer)conditionMap.get("meterId");

        StringBuilder sb = new StringBuilder();

        sb.append("select m ");
        sb.append("\nFROM MeterCtrl m ");
        sb.append("\nWHERE m.id.meter.id  = :meterId ");
        sb.append("\nAND   m.status IN (0, 1) ");      // 0: Initialize , 1: Send command
        sb.append("\nORDER BY m.id.writeDate DESC ");

        Query query = em.createQuery(sb.toString());

        query.setParameter("meterId", meterId);

        query.setFirstResult(0);
        query.setMaxResults(1);
        result = query.getResultList();

        return result;
    }

    /**
     * method name : getBulkMeterCommandResultData
     * method Desc : MDIS - Bulk Meter Command 실행 결과 값을 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<MeterCtrl> getBulkMeterCommandResultData(Map<String, Object> conditionMap) {
        String ctrlId = (String)conditionMap.get("ctrlId");
        List<Integer> meterIdList = (List<Integer>)conditionMap.get("meterIdList");
        List<String> writeDateList = (List<String>)conditionMap.get("writeDateList");

        StringBuilder sb = new StringBuilder();

        sb.append("select m ");
        sb.append("\nFROM MeterCtrl m ");
        sb.append("\nWHERE m.id.ctrlId = :ctrlId ");
        
        int len = meterIdList.size();

        for (int i = 0 ; i < len ; i++) {
            if (i == 0) {
                sb.append("\nAND   (");
            } else {
                sb.append("\n    OR ");
            }

            sb.append("(m.id.meter.id = ").append(meterIdList.get(i)).append(" AND m.id.writeDate = '").append(writeDateList.get(i)).append("') ");

            if (i == (len-1)) {
                sb.append(") ");
            }
        }

        Query query = em.createQuery(sb.toString(), MeterCtrl.class);
        query.setParameter("ctrlId", ctrlId);

        return query.getResultList();
    }

    /**
     * method name : getBulkMeterCommandRunCheck
     * method Desc : MDIS - Bulk Meter Command 실행 여부를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @SuppressWarnings("unchecked")
    public Integer getBulkMeterCommandRunCheck(Map<String, Object> conditionMap) {
        Integer count = 0;
        List<Integer> meterIdList = (List<Integer>)conditionMap.get("meterIdList");

        StringBuilder sb = new StringBuilder();

        sb.append("\nSELECT COUNT(*) AS cnt ");
        sb.append("\nFROM MeterCtrl mc, ");
        sb.append("\n     ( ");
        sb.append("\n        SELECT MAX(ml.writeDate) AS write_date, ");
        sb.append("\n               ml.meter.id ");
        sb.append("\n        FROM MeterCtrl ml ");
        sb.append("\n        WHERE ml.meter.id in (:meterIdList) ");
        sb.append("\n        AND   ml.status IN (0, 1) ");  // 0: Initialize , 1: Send command
        sb.append("\n        GROUP BY ml.meter.id ");
        sb.append("\n     ) ml ");
        sb.append("\nWHERE mc.meter.id = ml.meter.id ");
        sb.append("\nAND   mc.writeDate = ml.writeDate ");

        Query query = em.createQuery(new SQLWrapper().getQuery(sb.toString()));
        query.setParameter("meterIdList", meterIdList);

        count = ((Number)query.getSingleResult()).intValue();
        return count;
    }

    @Override
    public Class<MeterCtrl> getPersistentClass() {
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