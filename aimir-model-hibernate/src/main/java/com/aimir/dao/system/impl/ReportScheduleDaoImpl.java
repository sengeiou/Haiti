/**
 * ReportScheduleDaoImpl.java Copyright NuriTelecom Limited 2011
 */
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
import com.aimir.dao.system.ReportScheduleDao;
import com.aimir.model.system.ReportSchedule;
import com.aimir.util.StringUtil;

/**
 * ReportScheduleDaoImpl.java Description 
 * <p>
 * <pre>
 * Date          Version     Author   Description
 * 2011. 10. 6.   v1.0       문동규   최초생성
 * </pre>
 */
@Repository(value = "reportScheduleDao")
public class ReportScheduleDaoImpl extends AbstractHibernateGenericDao<ReportSchedule, Integer> implements ReportScheduleDao {

	@Autowired
	protected ReportScheduleDaoImpl(SessionFactory sessionFactory) {
		super(ReportSchedule.class);
		super.setSessionFactory(sessionFactory);
	}

    /**
     * method name : getReportScheduleList<b/>
     * method Desc : Report 관리 맥스가젯에서 스케줄 리스트를 조회한다.
     *
     * @param conditionMap
     * @param isCount total count 여부
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getReportScheduleList(Map<String, Object> conditionMap, boolean isCount) {
        List<Map<String, Object>> result;
//        Integer roleId = (Integer)conditionMap.get("roleId");
        Integer operatorId = (Integer)conditionMap.get("operatorId");
        Integer reportId = (Integer)conditionMap.get("reportId");
        Integer page = (Integer)conditionMap.get("page");
        Integer limit = (Integer)conditionMap.get("limit");

        StringBuilder sb = new StringBuilder();


        if (isCount) {
            sb.append("\nSELECT COUNT(DISTINCT c.id) AS cnt ");
        } else {
            sb.append("\nSELECT c.id AS scheduleId, ");
            sb.append("\n       c.name AS scheduleName, ");
            sb.append("\n       c.used AS used, ");
            sb.append("\n       c.cronFormat AS cronFormat, ");
            sb.append("\n       c.exportFormat AS exportFormat, ");
            sb.append("\n       MAX(s.writeTime) AS writeTime, ");
            sb.append("\n       c.email AS email ");
        }

        sb.append("\nFROM ");
        sb.append("\n     ReportResult s ");
        sb.append("\n     right outer join s.reportSchedule c ");
        sb.append("\n     left join c.parameterData p ");
        sb.append("\n     left join p.reportParameter m ");
        sb.append("\n     left join m.report r ");
//        sb.append("\n     ReportRole as o ");

        sb.append("\nWHERE 1=1 ");
        sb.append("\nAND   c.operator.id = :operatorId ");
        
        if (!StringUtil.nullToBlank(reportId).isEmpty()) {
            sb.append("\nAND   r.id = :reportId ");
        }

        if (!isCount) {
            sb.append("\nGROUP BY c.id, c.name, c.used, c.cronFormat, c.exportFormat, c.email ");
            sb.append("\nORDER BY c.name, c.id ");
        }

        Query query = getSession().createQuery(sb.toString());
//        query.setInteger("roleId", roleId);
        query.setInteger("operatorId", operatorId);
//        query.setString("startDate", startDate + "000000");
//        query.setString("endDate", endDate + "235959");

        if (!StringUtil.nullToBlank(reportId).isEmpty()) {
            query.setInteger("reportId", reportId);
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

}
