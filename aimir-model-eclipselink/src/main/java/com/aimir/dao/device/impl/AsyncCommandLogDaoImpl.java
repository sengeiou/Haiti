package com.aimir.dao.device.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.AsyncCommandLogDao;
import com.aimir.model.device.AsyncCommandLog;
import com.aimir.model.device.AsyncCommandLogPk;
import com.aimir.util.Condition;
import com.aimir.util.StringUtil;

@Repository(value = "asynccommandlogDao")
public class AsyncCommandLogDaoImpl extends AbstractJpaDao<AsyncCommandLog, AsyncCommandLogPk> implements AsyncCommandLogDao {

	public AsyncCommandLogDaoImpl() {
		super(AsyncCommandLog.class);
	}

	public Integer getCmdStatus(String deviceId, String cmd) {
        return null;
	}
	
	public Integer getCmdStatusByTrId(String deviceId, long trId) throws Exception {
		StringBuffer sbQuery = new StringBuffer();
		
		sbQuery.append("\nSELECT state ");
		sbQuery.append("\nFROM ASYNC_COMMAND_LOG o ");
		sbQuery.append("\nWHERE o.deviceId =  '"+ deviceId + "' ");
		sbQuery.append("\nAND o.trid = '"+ String.valueOf(trId) + "' ");		

		Query query = null;
		query = em.createNativeQuery(sbQuery.toString());

		Number result = (Number) query.getSingleResult();
		int returnData = 0;
        if (result == null) {
        	return null;
        }		
        returnData = result.intValue();	
        return returnData;
	}
	
	public Long getMaxTrId(String deviceId) {
		return getMaxTrId(deviceId, null);
	}

	public Long getMaxTrId(String deviceId, String cmd) {
		
		Query query = getEntityManager().createQuery("select o from AsyncCommandLog o " + "where o.deviceId = :deviceId and o.command = :command " + " order by createTime");
		query.setParameter("deviceId", deviceId);
		query.setParameter("command", cmd);
		
		Object result = query.getSingleResult();
		
		Long value = 0l;
		if (result == null) {
			return 0l;
		} else if (result instanceof Integer) {
			Integer val = (Integer) result;
			return new Long(val.intValue());
		} else if (result instanceof Long) {
			return (Long) result;
		} else if (result instanceof BigDecimal) {
			BigDecimal big = (BigDecimal) result;
			return big.longValue();
		} else if (result instanceof Number) {
			Number number = (Number) result;
			return number.longValue();
		}
		
		return value;
	}

	@Override
	public List<AsyncCommandLog> getLogListByCondition(Map<String, Object> condition) throws Exception {
		String meterId = StringUtil.nullToBlank(condition.get("meterId"));
		String modemId = StringUtil.nullToBlank(condition.get("modemId"));
		Integer state = condition.get("state") == null ? null : (Integer) condition.get("state");

		Query query = getEntityManager().createQuery("select o from AsyncCommandLog o " + "where (o.deviceId = :meterId or o.deviceId = :modemId) " + "and o.state = :state order by createTime");
		query.setParameter("meterId", meterId);
		query.setParameter("modemId", modemId);
		query.setParameter("state", state);

		return query.getResultList();
	}

	@Override
	public Class<AsyncCommandLog> getPersistentClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Object> getSumFieldByCondition(Set<Condition> conditions, String field, String... groupBy) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Object> getCommandLogList(Map<String, Object> condition) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Map<String, Object>> getCommandLogListTotalCount(Map<String, Object> condition) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	

}
