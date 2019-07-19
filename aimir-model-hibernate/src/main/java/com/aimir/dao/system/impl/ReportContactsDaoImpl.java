/**
 * ReportContactsDaoImpl.java Copyright NuriTelecom Limited 2011
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
import com.aimir.dao.system.ReportContactsDao;
import com.aimir.model.system.ReportContacts;
import com.aimir.util.StringUtil;

/**
 * ReportContactsDaoImpl.java Description 
 * <p>
 * <pre>
 * Date          Version     Author   Description
 * 2011. 9. 27.   v1.0       문동규   최초생성
 * </pre>
 */
@Repository(value = "reportContactsDao")
public class ReportContactsDaoImpl extends AbstractHibernateGenericDao<ReportContacts, Integer> implements ReportContactsDao {

	@Autowired
	protected ReportContactsDaoImpl(SessionFactory sessionFactory) {
		super(ReportContacts.class);
		super.setSessionFactory(sessionFactory);
	}

    /**
     * method name : getReportContactsList<b/>
     * method Desc : Report 관리에서 Email Contacts 리스트를 조회한다.
     *
     * @param conditionMap
     * @param isCount total count 여부
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getReportContactsList(Map<String, Object> conditionMap, boolean isCount) {
        List<Map<String, Object>> result;
//        Integer roleId = (Integer)conditionMap.get("roleId");
        Integer operatorId = (Integer)conditionMap.get("operatorId");
        String searchType = (String)conditionMap.get("searchType");
        String searchValue = (String)conditionMap.get("searchValue");
        Integer page = (Integer)conditionMap.get("page");
        Integer limit = (Integer)conditionMap.get("limit");

        StringBuilder sb = new StringBuilder();

        if (isCount) {
            sb.append("\nSELECT COUNT(*) AS cnt ");
        } else {
            sb.append("\nSELECT c.id AS contactsId, ");
            sb.append("\n       c.name AS name, ");
            sb.append("\n       c.email AS email, ");
            sb.append("\n       c.group.id AS groupId, ");
            sb.append("\n       c.group.name AS groupName ");
        }
        sb.append("\nFROM ReportContacts c ");
        sb.append("\nWHERE c.operator.id = :operatorId ");

        if (!StringUtil.nullToBlank(searchValue).isEmpty()) {
            if (StringUtil.nullToBlank(searchType).equals("group")) {   // 그룹으로 검색
                sb.append("\nAND   c.groupName LIKE '%'||:searchValue||'%' ");
            } else {    // 이름으로 검색
                sb.append("\nAND   c.name LIKE '%'||:searchValue||'%' ");
            }
        }

        if (!isCount) {
            sb.append("\nORDER BY c.name ");
        }

        Query query = getSession().createQuery(sb.toString());
//        query.setInteger("roleId", roleId);
        query.setInteger("operatorId", operatorId);

        if (!StringUtil.nullToBlank(searchValue).isEmpty()) {
            query.setString("searchValue", searchValue);
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
