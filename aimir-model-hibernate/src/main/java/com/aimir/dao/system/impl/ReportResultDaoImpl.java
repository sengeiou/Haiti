package com.aimir.dao.system.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.system.ReportResultDao;
import com.aimir.model.system.ReportResult;
import com.aimir.util.StringUtil;

/**
 * ReportResultDaoImpl.java Description 
 * <p>
 * <pre>
 * Date          Version     Author   Description
 * 2011. 9. 20.   v1.0       문동규   주석생성
 * </pre>
 */
@Repository(value = "reportResultDao")
public class ReportResultDaoImpl extends AbstractHibernateGenericDao<ReportResult, Integer> implements ReportResultDao {

	@Autowired
	protected ReportResultDaoImpl(SessionFactory sessionFactory) {
		super(ReportResult.class);
		super.setSessionFactory(sessionFactory);
	}

    /**
     * method name : getReportResultList<b/>
     * method Desc : Report 관리 미니가젯에서 스케줄 실행 결과를 조회한다.
     *
     * @param conditionMap
     * @param isCount total count 여부
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getReportResultList(Map<String, Object> conditionMap, boolean isCount) {
        List<Map<String, Object>> result;
        Integer roleId = (Integer)conditionMap.get("roleId");
        Integer operatorId = (Integer)conditionMap.get("operatorId");
        String reportName = (String)conditionMap.get("reportName");
        String startDate = (String)conditionMap.get("startDate");
        String endDate = (String)conditionMap.get("endDate");
        Integer page = (Integer)conditionMap.get("page");
        Integer limit = (Integer)conditionMap.get("limit");

        StringBuilder sb = new StringBuilder();

        if (isCount) {
            sb.append("\nSELECT COUNT(DISTINCT s.id) AS cnt ");
        } else {
            sb.append("\nSELECT DISTINCT s.id AS resultId, ");
            sb.append("\n       s.writeTime AS writeTime, ");
            sb.append("\n       s.resultLink AS resultLink, ");
            sb.append("\n       s.resultFileLink AS resultFileLink, ");
            sb.append("\n       r.name AS reportName, ");
            sb.append("\n       r.metaLink AS metaLink, ");
            sb.append("\n       s.operator.name AS operatorName ");
        }

        sb.append("\nFROM ");
        sb.append("\n     ReportResult as s ");
        sb.append("\n     left join s.reportSchedule as c ");
        sb.append("\n     left join c.parameterData as p ");
        sb.append("\n     left join p.reportParameter as m ");
        sb.append("\n     left join m.report as r, ");
        sb.append("\n     ReportRole as o ");

        sb.append("\nWHERE o.role.id = :roleId ");
        sb.append("\nAND   r.id = o.report.id ");
        sb.append("\nAND   s.operator.id = :operatorId ");
        sb.append("\nAND   s.writeTime BETWEEN :startDate AND :endDate ");

        if (!StringUtil.nullToBlank(reportName).isEmpty()) {
            sb.append("\nAND   r.name LIKE '%'||:reportName||'%' ");
        }

        if (!isCount) {
            sb.append("\nORDER BY s.writeTime DESC, s.id DESC ");
        }
        
        Query query = getSession().createQuery(sb.toString());
        query.setInteger("roleId", roleId);
        query.setInteger("operatorId", operatorId);
        query.setString("startDate", startDate + "000000");
        query.setString("endDate", endDate + "235959");

        if (!StringUtil.nullToBlank(reportName).isEmpty()) {
            query.setString("reportName", reportName);
        }

        if (isCount) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("total", query.uniqueResult());
            result = new ArrayList<Map<String, Object>>();
            result.add(map);
        } else {
            query.setFirstResult((page - 1) * limit);
            query.setMaxResults(limit);
            result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        }

        return result;
    }

    /**
     * method name : getReportScheduleResultList<b/>
     * method Desc : Report 관리 맥스가젯에서 스케줄 실행 결과를 조회한다.
     *
     * @param conditionMap
     * @param isCount total count 여부
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getReportScheduleResultList(Map<String, Object> conditionMap, boolean isCount) {
        List<Map<String, Object>> result;
        Integer scheduleId = (Integer)conditionMap.get("scheduleId");
        String startDate = (String)conditionMap.get("startDate");
        String endDate = (String)conditionMap.get("endDate");
        Integer page = (Integer)conditionMap.get("page");
        Integer limit = (Integer)conditionMap.get("limit");

        StringBuilder sb = new StringBuilder();

        if (isCount) {
            sb.append("\nSELECT COUNT(*) AS cnt ");
        } else {
            sb.append("\nSELECT s.id AS resultId, ");
            sb.append("\n       s.writeTime AS writeTime, ");
            sb.append("\n       s.result AS result, ");
            sb.append("\n       s.failReason AS failReason ");
        }

        sb.append("\nFROM ");
        sb.append("\n     ReportResult as s ");

        sb.append("\nWHERE s.reportSchedule.id = :scheduleId ");
        sb.append("\nAND   s.writeTime BETWEEN :startDate AND :endDate ");

        if (!isCount) {
            sb.append("\nORDER BY s.writeTime DESC, s.id DESC ");
        }

        Query query = getSession().createQuery(sb.toString());
        query.setInteger("scheduleId", scheduleId);
        query.setString("startDate", startDate + "000000");
        query.setString("endDate", endDate + "235959");

        if (isCount) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("total", query.uniqueResult());
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