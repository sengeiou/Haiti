package com.aimir.dao.system.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.system.ContractChangeLogDao;
import com.aimir.model.system.ContractChangeLog;
import com.aimir.util.Condition;
import com.aimir.util.StringUtil;

@Repository(value = "contractchangelogDao")
public class ContractChangeLogDaoImpl  extends AbstractHibernateGenericDao<ContractChangeLog, Long> implements ContractChangeLogDao {

    @Autowired
    protected ContractChangeLogDaoImpl(SessionFactory sessionFactory) {
        super(ContractChangeLog.class);
        super.setSessionFactory(sessionFactory);
    }

    public List<ContractChangeLog> getContractChangeLogByListCondition(
            Set<Condition> set) {
        return findByConditions(set);
    }

    public List<Object> getContractChangeLogCountByListCondition(
            Set<Condition> set) {
        return findTotalCountByConditions(set);
    }

    public void contractLogDelete(int contractId) {
        StringBuffer query = new StringBuffer();
        query.append("DELETE ContractChangeLog WHERE contract_id = ? ");
        // bulkUpdate 때문에 주석처리
        /*this.getHibernateTemplate().bulkUpdate(query.toString(), contractId );*/
    }

    public void contractLogAllDelete(int customerId) {
        StringBuffer query = new StringBuffer();
        query.append("DELETE ContractChangeLog WHERE customer_id = ? ");
        // bulkUpdate 때문에 주석처리
        /*this.getHibernateTemplate().bulkUpdate(query.toString(), customerId );*/

    }

    /**
     * method name : getContractChangeLogList<b/>
     * method Desc : Customer Contract Management 맥스 가젯에서 Contract ChangeLog 를 조회한다.
     *
     * @param conditionMap
     * @param isCount
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<Map<String, Object>> getContractChangeLogList(Map<String, Object> conditionMap, boolean isCount) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        String startDate = StringUtil.nullToBlank(conditionMap.get("startDate"));
        String endDate = StringUtil.nullToBlank(conditionMap.get("endDate"));
        Integer contractId = (Integer)conditionMap.get("contractId");
        Integer page = (Integer)conditionMap.get("page");
        Integer limit = (Integer)conditionMap.get("limit");

        StringBuilder sb = new StringBuilder();

        if (isCount) {
            sb.append("\nSELECT COUNT(*) AS cnt ");
        } else {
            sb.append("\nSELECT log.startDatetime AS startDatetime, ");
            sb.append("\n       log.writeDatetime AS writeDatetime, ");
            sb.append("\n       log.changeField AS changeField, ");
            sb.append("\n       log.beforeValue AS beforeValue, ");
            sb.append("\n       log.afterValue AS afterValue ");
        }
        sb.append("\nFROM ContractChangeLog log ");
        sb.append("\nWHERE 1=1 ");

        if (contractId != null) {
            sb.append("\nAND   log.contract.id = :contractId ");
        }
        if (!startDate.isEmpty() && !endDate.isEmpty()) {
            sb.append("\nAND   log.writeDatetime BETWEEN :startDate AND :endDate ");
        } else {
            if (!startDate.isEmpty()) {
                sb.append("\nAND   log.writeDatetime >= :startDate ");
            }
            if (!endDate.isEmpty()) {
                sb.append("\nAND   log.writeDatetime <= :endDate ");
            }
        }

        Query query = getSession().createQuery(sb.toString());

        if (contractId != null) {
            query.setInteger("contractId", contractId);
        }
        if (!startDate.isEmpty() && !endDate.isEmpty()) {
            query.setString("startDate", startDate);
            query.setString("endDate", endDate);
        } else {
            if (!startDate.isEmpty()) {
                query.setString("startDate", startDate);
            }
            if (!endDate.isEmpty()) {
                query.setString("endDate", endDate);
            }
        }

        if (isCount) {
            Map<String, Object> map = new HashMap<String, Object>();
            Number count = (Number)query.uniqueResult();
            map.put("total", count.intValue());
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