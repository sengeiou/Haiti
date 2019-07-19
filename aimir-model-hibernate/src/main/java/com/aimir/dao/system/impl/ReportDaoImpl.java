package com.aimir.dao.system.impl;

import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.system.ReportDao;
import com.aimir.model.system.Report;

@Repository(value = "reportDao")
public class ReportDaoImpl extends AbstractHibernateGenericDao<Report, Integer> implements ReportDao {

	@Autowired
	protected ReportDaoImpl(SessionFactory sessionFactory) {
		super(Report.class);
		super.setSessionFactory(sessionFactory);
	}

    /**
     * method name : getReportResultList<b/>
     * method Desc : Report 관리 화면에서 Report Tree Root 데이터를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getReportTreeRootList(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result;
        Integer roleId = (Integer)conditionMap.get("roleId");

        StringBuilder sb = new StringBuilder();
        sb.append("\nSELECT o.report.id AS reportId, ");
        sb.append("\n       o.report.name AS reportName, ");
        sb.append("\n       o.report.description AS description, ");
        sb.append("\n       o.report.parent.id AS parentId, ");
        sb.append("\n       o.report.metaLink AS metaLink, ");
        sb.append("\n       o.report.categoryItem AS categoryItem ");
        sb.append("\nFROM ReportRole o ");
//        sb.append("\n     o.report r ");
        sb.append("\nWHERE o.role.id = :roleId ");
        sb.append("\nAND   o.report.parent.id IS NULL ");
        sb.append("\nORDER BY o.report.name ");

        Query query = getSession().createQuery(sb.toString());
        query.setInteger("roleId", roleId);

        result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        return result;
    }

    /**
     * method name : getReportTreeChildList<b/>
     * method Desc : Report 관리 화면에서 Report Tree Child 데이터를 조회한다.
     *
     * @param roleId
     * @param parentId
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getReportTreeChildList(Integer roleId, Integer parentId) {
        List<Map<String, Object>> result;

        StringBuilder sb = new StringBuilder();

        sb.append("\nSELECT o.report.id AS reportId, ");
        sb.append("\n       o.report.name AS reportName, ");
        sb.append("\n       o.report.description AS description, ");
        sb.append("\n       o.report.parent.id AS parentId, ");
        sb.append("\n       o.report.metaLink AS metaLink, ");
        sb.append("\n       o.report.categoryItem AS categoryItem ");
        sb.append("\nFROM ReportRole o ");
        sb.append("\nWHERE o.role.id = :roleId ");
        sb.append("\nAND   o.report.parent.id = :parentId ");
        sb.append("\nORDER BY o.report.name ");

        Query query = getSession().createQuery(sb.toString());
        query.setInteger("roleId", roleId);
        query.setInteger("parentId", parentId);

        result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        return result;
    }

}
