/**
 * ReportContactsGroupDaoImpl.java Copyright NuriTelecom Limited 2011
 */
package com.aimir.dao.system.impl;

import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.system.ReportContactsGroupDao;
import com.aimir.model.system.ReportContactsGroup;

/**
 * ReportContactsGroupDaoImpl.java Description 
 * <p>
 * <pre>
 * Date          Version     Author   Description
 * 2011. 10. 13.   v1.0       문동규   최초생성
 * </pre>
 */
@Repository(value = "reportContactsGroupDao")
public class ReportContactsGroupDaoImpl extends AbstractHibernateGenericDao<ReportContactsGroup, Integer> implements ReportContactsGroupDao {

	@Autowired
	protected ReportContactsGroupDaoImpl(SessionFactory sessionFactory) {
		super(ReportContactsGroup.class);
		super.setSessionFactory(sessionFactory);
	}

    /**
     * method name : getReportContactsGroupComboData<b/>
     * method Desc : Report 관리에서 Email Contacts Group Combo Data 를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getReportContactsGroupComboData(Map<String, Object> conditionMap) {
        Integer operatorId = (Integer)conditionMap.get("operatorId");

        StringBuilder sb = new StringBuilder();

        sb.append("\nSELECT g.id AS id, ");
        sb.append("\n       g.name AS name ");
        sb.append("\nFROM ReportContactsGroup g ");
        sb.append("\nWHERE g.operator.id = :operatorId ");
        sb.append("\nORDER BY g.name ");

        Query query = getSession().createQuery(sb.toString());
        query.setInteger("operatorId", operatorId);

        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

}
