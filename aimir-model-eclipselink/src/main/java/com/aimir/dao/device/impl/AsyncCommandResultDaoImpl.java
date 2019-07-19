package com.aimir.dao.device.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.AsyncCommandResultDao;
import com.aimir.model.device.AsyncCommandResult;
import com.aimir.model.device.AsyncCommandResultPk;
import com.aimir.util.Condition;

@Repository(value = "asynccommandresultDao")
public class AsyncCommandResultDaoImpl extends AbstractJpaDao<AsyncCommandResult, AsyncCommandResultPk> implements AsyncCommandResultDao {

	public AsyncCommandResultDaoImpl() {
		super(AsyncCommandResult.class);
	}

	@Override
	public Class<AsyncCommandResult> getPersistentClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Object> getSumFieldByCondition(Set<Condition> conditions, String field, String... groupBy) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getMaxNum(String mcuId, Long trId) {
		StringBuffer sb = new StringBuffer();

		sb.append("\n SELECT max(a.id.num)");
		sb.append("\n FROM AsyncCommandResult a");
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
	public List<AsyncCommandResult> getCmdResults(String deviceSerial, long trId, String paramName) {
		StringBuffer sbQuery = new StringBuffer();
		
		sbQuery.append("\nSELECT a.DATA, a.LENGTH, a.OID, a.RESULTTYPE, a.RESULTVALUE, a.MCUID, a.NUM, a.TRID, a.TR_TYPE ");
		sbQuery.append("\nFROM ASYNC_COMMAND_RESULT a ");
		sbQuery.append("\nWHERE a.mcuId =  '"+ deviceSerial + "' ");
		sbQuery.append("\nAND a.trId = '"+ String.valueOf(trId) + "' ");
        if (!"".equals(paramName)) {
        	sbQuery.append("\nAND a.resultType = '" + paramName + "' ");
        }
		
		Query query = null;
		query = em.createNativeQuery(sbQuery.toString());
		List<Object> result = query.getResultList();
        if (result == null) {
        	return null;
        }		
		
        List<AsyncCommandResult> cmdResultList= new ArrayList<AsyncCommandResult>();
        for (Object obj : result) {
        	Object[] objs = (Object[]) obj;
        	AsyncCommandResult cmdResult = new AsyncCommandResult();
        	
        	if (objs[0] != null)cmdResult.setData((byte[])objs[0]);
        	if (objs[1] != null)cmdResult.setLength(Long.valueOf(objs[1].toString()));
        	if (objs[2] != null)cmdResult.setOid(objs[2].toString());
        	if (objs[3] != null)cmdResult.setResultType(objs[3].toString());
        	if (objs[4] != null)cmdResult.setResultValue(objs[4].toString());
        	if (objs[5] != null)cmdResult.setMcuId(objs[5].toString());
        	if (objs[6] != null)cmdResult.setNum(Integer.valueOf(objs[6].toString()));
        	if (objs[7] != null)cmdResult.setTrId(Long.valueOf(objs[7].toString()));
        	if (objs[8] != null)cmdResult.setTrType(objs[8].toString());
        	
        	
        	cmdResultList.add(cmdResult);
        }
        
        
        return cmdResultList;		
				
	}
	
	@Override
	public List<AsyncCommandResult> getCmdResults(String deviceSerial, long trId, String tr_type, String paramName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCmdResults(String deviceSerial, long trId) {
		// TODO Auto-generated method stub
		return null;
	}
}
