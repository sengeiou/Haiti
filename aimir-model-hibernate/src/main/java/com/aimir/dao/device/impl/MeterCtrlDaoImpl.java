package com.aimir.dao.device.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.device.MeterCtrlDao;
import com.aimir.model.device.MeterCtrl;
import com.aimir.util.SQLWrapper;


@Repository(value = "meterCtrlDao")
public class MeterCtrlDaoImpl extends AbstractHibernateGenericDao<MeterCtrl, Integer> implements MeterCtrlDao {

    Log logger = LogFactory.getLog(MeterCtrlDaoImpl.class);
    
	@Autowired
	protected MeterCtrlDaoImpl(SessionFactory sessionFactory) {
		super(MeterCtrl.class);
		super.setSessionFactory(sessionFactory);
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

        sb.append("\nFROM MeterCtrl m ");
        sb.append("\nWHERE m.id.ctrlId  = :ctrlId ");
        sb.append("\nAND   m.id.meter.id  = :meterId ");
        sb.append("\nAND   m.id.writeDate  = :writeDate ");

        Query query = getSession().createQuery(sb.toString());

        query.setString("ctrlId", ctrlId);
        query.setInteger("meterId", meterId);
        query.setString("writeDate", writeDate);

        return query.list();
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

        sb.append("\nFROM MeterCtrl m ");
        sb.append("\nWHERE m.id.meter.id  = :meterId ");
        sb.append("\nAND   m.status IN (0, 1) ");      // 0: Initialize , 1: Send command
        sb.append("\nORDER BY m.id.writeDate DESC ");

        Query query = getSession().createQuery(sb.toString());

        query.setInteger("meterId", meterId);

        query.setFirstResult(0);
        query.setMaxResults(1);
        result = query.list();

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

        Query query = getSession().createQuery(sb.toString());
        query.setString("ctrlId", ctrlId);

        return query.list();
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
        sb.append("\nFROM meterctrl mc, ");
        sb.append("\n     ( ");
        sb.append("\n        SELECT MAX(ml.write_date) AS write_date, ");
        sb.append("\n               ml.meter_id ");
        sb.append("\n        FROM meterctrl ml ");
        sb.append("\n        WHERE ml.meter_id in (:meterIdList) ");
        sb.append("\n        AND   ml.status IN (0, 1) ");  // 0: Initialize , 1: Send command
        sb.append("\n        GROUP BY ml.meter_id ");
        sb.append("\n     ) ml ");
        sb.append("\nWHERE mc.meter_id = ml.meter_id ");
        sb.append("\nAND   mc.write_date = ml.write_date ");

        SQLQuery query = getSession().createSQLQuery(new SQLWrapper().getQuery(sb.toString()));
        query.setParameterList("meterIdList", meterIdList);

        count = ((Number)query.uniqueResult()).intValue();
        return count;
    }
}