package com.aimir.dao.device.impl;

import java.util.List;
import java.util.Set;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.AsyncCommandParamDao;
import com.aimir.model.device.AsyncCommandParam;
import com.aimir.model.device.AsyncCommandParamPk;
import com.aimir.util.Condition;

@Repository(value = "asynccommandparamDao")
public class AsyncCommandParamDaoImpl extends AbstractJpaDao<AsyncCommandParam, AsyncCommandParamPk> implements AsyncCommandParamDao {

	public AsyncCommandParamDaoImpl() {
		super(AsyncCommandParam.class);
	}

	public Integer getMaxNum(String mcuId, Long trId) {

		StringBuffer sb = new StringBuffer();

		sb.append("\n SELECT max(a.id.num)");
		sb.append("\n FROM AsyncCommandParam a");
		sb.append("\n WHERE a.id.mcuId=:mcuId");
		sb.append("\n AND a.id.trId=:trId");

		Query query = getEntityManager().createQuery(sb.toString());
		query.setParameter("mcuId", mcuId);
		query.setParameter("trId", trId);

		Number totalCount = (Number) query.getSingleResult();
		int returnData = 0;

		if (totalCount == null)
			returnData = 0;
		else {
			returnData = totalCount.intValue();
		}
		return returnData;

	}

	@Override
	public List<AsyncCommandParam> getCmdParams(String deviceSerial, long trId, String paramName) {
		String sql = "select a from AynchCommandParam a " + "where a.id.mcuId = :mcuId and a.id.trId = :trId";
		if (paramName != null)
			sql += " and paramType = :paramType";

		Query query = getEntityManager().createQuery(sql);
		query.setParameter("mcuId", deviceSerial);
		query.setParameter("trId", trId);

		if (paramName != null) {
			query.setParameter("paramType", paramName);
		}

		return query.getResultList();
	}

	@Override
	public Class<AsyncCommandParam> getPersistentClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Object> getSumFieldByCondition(Set<Condition> conditions, String field, String... groupBy) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AsyncCommandParam> getCmdParamsByTrnxId(String deviceSerial, String paramName) {
		// TODO Auto-generated method stub
		return null;
	}
}
