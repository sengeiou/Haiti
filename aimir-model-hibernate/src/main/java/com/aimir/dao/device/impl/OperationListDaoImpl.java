package com.aimir.dao.device.impl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.device.OperationListDao;
import com.aimir.model.device.OperationList;
import com.aimir.util.SQLWrapper;

@Repository(value = "operationlistDao")
public class OperationListDaoImpl extends AbstractHibernateGenericDao<OperationList, Integer> implements OperationListDao {

	@Autowired
	protected OperationListDaoImpl(SessionFactory sessionFactory) {
		super(OperationList.class);
		super.setSessionFactory(sessionFactory);
	}

	/*
	@SuppressWarnings("unchecked")
	public List<OperationList> getOperationListByConstraintId(int constraintId) {

		String sql = " FROM OperationList operationList WHERE operationList.constraintId = :constraintId ";
		
		Query query = getSession().createQuery(sql);
		query.setInteger("constraintId", constraintId);
		
		return query.list();
	}
	*/
	
	@SuppressWarnings("unchecked")
	public List<OperationList> getOperationListByOperationCodeId(int operationCodeId) {
		Query query = getSession().createQuery(" from OperationList o where o.operationCode.id = ? ");
		query.setInteger(1,  operationCodeId);
		return query.list();
	}

    /**
     * method name : getOperationListByModelId<b/>
     * method Desc : MDIS - Meter Management 맥스가젯에서 선택한 Meter 의 Model ID 에 해당하는 OperationList 를 조회한다.
     *
     * @param modelId aimir.model.system.DeviceModel.id
     * @param operationCodeList List of aimir.model.device.OperationList.operationCode.code
     * @return List of aimir.model.device.OperationList
     */
    @SuppressWarnings("unchecked")
    public List<OperationList> getOperationListByModelId(Integer modelId, List<String> operationCodeList) {
        StringBuilder sb = new StringBuilder();

        sb.append("\nFROM OperationList op ");
        sb.append("\nWHERE op.model.id = :modelId ");
        sb.append("\nAND   op.operationCode.code IN (:operationCodeList) ");

        Query query = getSession().createQuery(sb.toString());
        query.setInteger("modelId", modelId);
        query.setParameterList("operationCodeList", operationCodeList);

        return query.list();
    }

	@Override
	public List<String> getAvailableOperationList(Integer modelId, Integer roleId) {
		StringBuilder sb = new StringBuilder();

		sb.append("\nselect cd.NAME ");
		sb.append("\nfrom role_code rc ");
		sb.append("\n    join code cd on rc.COMMANDS_ID = cd.ID ");
		sb.append("\n    join operation_list ol on cd.ID = ol.OPERATION_CODE ");
		sb.append("\nwhere rc.ROLE_ID =:roleId ");
		sb.append("\n    and ol.MODEL_ID =:modelId ");

		SQLQuery query = getSession().createSQLQuery(new SQLWrapper().getQuery(sb.toString()));
        query.setInteger("modelId", modelId);
        query.setInteger("roleId", roleId);

	    return query.list();
	}
}