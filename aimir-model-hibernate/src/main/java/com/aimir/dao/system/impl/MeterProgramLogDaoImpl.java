package com.aimir.dao.system.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.constants.CommonConstants.DefaultCmdResult;
import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.system.MeterProgramLogDao;
import com.aimir.model.device.Meter;
import com.aimir.model.system.MeterProgramLog;

@Repository(value = "meterProgramLogDao")
public class MeterProgramLogDaoImpl extends AbstractHibernateGenericDao<MeterProgramLog, Integer> implements
MeterProgramLogDao {

    @Autowired
    protected MeterProgramLogDaoImpl(SessionFactory sessionFactory) {
        super(MeterProgramLog.class);
        super.setSessionFactory(sessionFactory);
    }

    @Override
    public List<MeterProgramLog> findbyMeter(Meter meter) {
        return findbyMeterId(meter.getId());
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<MeterProgramLog> findbyMeterId(Integer meterId) {
        final String queryString = "FROM MeterProgramLog WHERE id in (SELECT MAX(id) FROM MeterProgramLog mpl WHERE mpl.meter.id=:ID AND result IS NULL GROUP BY mpl.meter.id, mpl.meterProgram.id)";

        Query query = getSession().createQuery(queryString);
        query.setInteger("ID", meterId);

        List<MeterProgramLog> list = query.list();

        return list;
    }

    /**
     * method name : getMeterProgramList<b/>
     * method Desc : Vendor Model 맥스가젯의 Meter Program 탭에서 Meter Program Log 리스트를 조회한다.
     *
     * @param conditionMap
     * @param isCount total count 여부
     * @return
     */
    @SuppressWarnings("unchecked")
    @Deprecated
    public List<Map<String, Object>> getMeterProgramLogList(Map<String, Object> conditionMap, boolean isCount) {
        List<Map<String, Object>> result;
        Integer configId = (Integer)conditionMap.get("configId");
        Integer page = (Integer)conditionMap.get("page");
        Integer pageSize = (Integer)conditionMap.get("pageSize");

        StringBuilder sb = new StringBuilder();

        if (isCount) {
            sb.append("\nSELECT COUNT(*) FROM ( ");
        }
        sb.append("\nSELECT mp.last_modified_date AS LAST_MODIFIED_DATE, ");
        sb.append("\n       mp.id AS METERPROGRAM_ID, ");
        sb.append("\n       mp.kind AS METERPROGRAM_KIND, ");
        sb.append("\n       SUM(CASE WHEN ml.result = '").append(DefaultCmdResult.SUCCESS.getCode()).append("' THEN 1 ELSE 0 END) AS SUCCESS_COUNT, ");
        sb.append("\n       SUM(CASE WHEN ml.result = '").append(DefaultCmdResult.FAILURE.getCode()).append("' THEN 1 ELSE 0 END) AS FAILURE_COUNT, ");
        sb.append("\n       SUM(CASE WHEN ml.id IS NOT NULL AND ml.result IS NULL THEN 1 ELSE 0 END) AS TRY_COUNT ");
        sb.append("\nFROM meterprogram mp ");
        sb.append("\n     LEFT OUTER JOIN ");
        sb.append("\n     meterprogramlog ml ");
        sb.append("\n     ON ml.meterprogram_id = mp.id ");
        sb.append("\nWHERE mp.meterconfig_id = :configId ");
        sb.append("\nGROUP BY mp.last_modified_date, mp.id, mp.kind ");

        if (isCount) {
            sb.append("\n) cnt ");
        } else {
            sb.append("\nORDER BY mp.last_modified_date DESC ");
        }

        Query query = getSession().createSQLQuery(sb.toString());
        query.setInteger("configId", configId);

        if (isCount) {
            Map<String, Object> map = new HashMap<String, Object>();
            int cnt = ((Number)query.uniqueResult()).intValue();
            map.put("total", cnt);
            result = new ArrayList<Map<String, Object>>();
            result.add(map);
        } else {
            query.setFirstResult(page * pageSize);
            query.setMaxResults(pageSize);
            result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        }

        return result;
    }

    /**
     * method name : getMeterProgramListRenew<b/>
     * method Desc : Vendor Model 맥스가젯의 Meter Program 탭에서 Meter Program Log 리스트를 조회한다.
     *
     * @param conditionMap
     * @param isCount total count 여부
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getMeterProgramLogListRenew(Map<String, Object> conditionMap, boolean isCount) {
        List<Map<String, Object>> result;
        Integer configId = (Integer)conditionMap.get("configId");
        Integer page = (Integer)conditionMap.get("page");
        Integer limit = (Integer)conditionMap.get("limit");

        StringBuilder sb = new StringBuilder();

        if (isCount) {
            sb.append("\nSELECT mp.id ");
        } else {
            sb.append("\nSELECT mp.lastModifiedDate AS lastModifiedDate, ");
            sb.append("\n       mp.id AS meterProgramId, ");
            sb.append("\n       mp.kind AS meterProgramKind, ");
            sb.append("\n       SUM(CASE WHEN ml.result = '").append(DefaultCmdResult.SUCCESS.getCode()).append("' THEN 1 ELSE 0 END) AS successCount, ");
            sb.append("\n       SUM(CASE WHEN ml.result = '").append(DefaultCmdResult.FAILURE.getCode()).append("' THEN 1 ELSE 0 END) AS failureCount, ");
            sb.append("\n       SUM(CASE WHEN ml.id IS NOT NULL AND ml.result IS NULL THEN 1 ELSE 0 END) AS tryCount ");
        }
        sb.append("\nFROM MeterProgramLog ml ");
        sb.append("\n     RIGHT OUTER JOIN ");
        sb.append("\n     ml.meterProgram mp ");
        sb.append("\nWHERE mp.meterConfig.id = :configId ");
        sb.append("\nGROUP BY mp.lastModifiedDate, mp.id, mp.kind ");

        if (!isCount) {
            sb.append("\nORDER BY mp.lastModifiedDate DESC ");
        }

        Query query = getSession().createQuery(sb.toString());
        query.setInteger("configId", configId);

        if (isCount) {
            Map<String, Object> map = new HashMap<String, Object>();
            int count = 0;
            Iterator<?> itr = query.iterate();
            while(itr.hasNext()) {
                itr.next();
                count++;
            }
            map.put("total", count);
            result = new ArrayList<Map<String, Object>>();
            result.add(map);
        } else {
            query.setFirstResult((page - 1) * limit);
            query.setMaxResults(limit);
            result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        }

        return result;
    }
}