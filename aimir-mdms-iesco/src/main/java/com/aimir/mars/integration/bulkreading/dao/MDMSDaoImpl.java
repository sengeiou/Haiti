package com.aimir.mars.integration.bulkreading.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.mars.integration.bulkreading.model.MDMBatchLog;
import com.aimir.util.Condition;

@Repository(value = "mdmsDao")
public class MDMSDaoImpl extends AbstractHibernateGenericDao<MDMBatchLog, Integer> implements MDMSDao {
	
	protected MDMSDaoImpl(SessionFactory sessionFactory) {
		super(MDMBatchLog.class);
		super.setSessionFactory(sessionFactory);
	}
	
	protected static Log logger = LogFactory.getLog(MDMSDaoImpl.class);
	
	@Override
	public List<Map<String, Object>> getMDMSStatistics(Map<String, Object> params) throws Exception {
		
		String yyyymmdd = params.get("yyyymmdd").toString();
		String batch_type = params.get("batch_type").toString();
		
		String from = yyyymmdd + "000000";
		String to = yyyymmdd + "235959";
		
		logger.debug("## from [" + from + "] + to [" + to + "]");
		
		StringBuffer queryBuffer = new StringBuffer();
		
		queryBuffer.append(" select \n");
		queryBuffer.append("	batch_type \n");
		queryBuffer.append("	, sum(batch_cnt) AS tot_cnt \n");
		queryBuffer.append("	, sum(wait_cnt) as wait_cnt \n");
		queryBuffer.append("	, round((sum(wait_cnt)/sum(batch_cnt)) * 100,2) as wait_rate \n");
		queryBuffer.append("	, sum(succ_cnt) as succ_cnt  \n");
		queryBuffer.append("	, round((sum(succ_cnt)/sum(batch_cnt)) * 100,2) as succ_rate  \n");
		queryBuffer.append(" from ( \n");
		queryBuffer.append("    SELECT  \n");
		queryBuffer.append("        BATCH_TYPE \n");
		queryBuffer.append("        , BATCH_STATUS \n");
		queryBuffer.append("        , BATCH_CNT \n");
		queryBuffer.append("        , decode(BATCH_STATUS, 1, BATCH_CNT, 0) as wait_cnt \n");
		queryBuffer.append("        , decode(BATCH_STATUS, 2, BATCH_CNT, 0) as succ_cnt \n");
		queryBuffer.append("    FROM MDM_BATCH_LOG \n");
		queryBuffer.append("    WHERE BATCH_DATETIME BETWEEN :from AND :to \n");
		
		if(!"".equals(batch_type) && batch_type != null) {
			queryBuffer.append("    AND batch_type = :batch_type \n");
		}
		
		queryBuffer.append(" ) a  \n");
		queryBuffer.append(" group by batch_type \n");
		
		SQLQuery query = getSession().createSQLQuery(queryBuffer.toString());
        
        query.setString("from", from);
        query.setString("to", to);
        
        if(!"".equals(batch_type) && batch_type != null) {
        	query.setString("batch_type", batch_type);
		}
               
        List<Object[]> obj = query.list();        
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();        
        
        for(int i = 0, size = obj.size() ; i < size ; i++) {
        	
        	Object[] objVal = obj.get(i);
        	HashMap<String, Object> hm = new HashMap<String, Object>();
			
			hm.put("batch_type", ((String)objVal[0]));			
			hm.put("tot_cnt", ((Number)objVal[1]).doubleValue());
			hm.put("wait_cnt", ((Number)objVal[2]).doubleValue());
			hm.put("wait_rate", ((Number)objVal[3]).doubleValue());
			hm.put("succ_cnt", ((Number)objVal[4]).doubleValue());	
			hm.put("succ_rate", ((Number)objVal[5]).doubleValue());	
			
			result.add(hm);
		}
	
        return result;
	}
	
	@Override
	public int getMDMSBatchCount(Map<String, Object> params) throws Exception {
		
		String yyyymmdd = params.get("yyyymmdd").toString();
		String batch_type = params.get("batch_type").toString();
		String batch_id = params.get("batch_id").toString();
				
		String from = yyyymmdd + "000000";
		String to = yyyymmdd + "235959";
		
		logger.debug("## from [" + from + "] + to [" + to + "]");
		
		StringBuffer queryBuffer = new StringBuffer();
		
		queryBuffer.append(" SELECT ");
		queryBuffer.append("     count(*) as cnt ");
		queryBuffer.append(" FROM MDM_BATCH_LOG ");
		queryBuffer.append(" WHERE BATCH_DATETIME BETWEEN :from AND :to ");
		
		if(!"".equals(batch_id) && batch_id != null) {
			queryBuffer.append(" AND batch_id = :batch_id ");
		}
		
		if(!"".equals(batch_type) && batch_type != null) {
			queryBuffer.append(" AND batch_type = :batch_type ");
		}
		
		queryBuffer.append(" ORDER BY BATCH_ID DESC ");				
		SQLQuery query = getSession().createSQLQuery(queryBuffer.toString());
        
        query.setString("from", from);
        query.setString("to", to);
        
        if(!"".equals(batch_type) && batch_type != null) {
        	query.setString("batch_type", batch_type);
		}
        
        if(!"".equals(batch_id) && batch_id != null) {
        	query.setString("batch_id", batch_id);
		}
        
        return ((Number) query.uniqueResult()).intValue();
	}

	@Override
	public List<Map<String, Object>> getMDMSBatchList(Map<String, Object> params) throws Exception {
		
		String yyyymmdd = params.get("yyyymmdd").toString();
		String batch_type = params.get("batch_type").toString();
		String batch_id = params.get("batch_id").toString();
		
		Integer page = (Integer) params.get("page");
		Integer limit = (Integer) params.get("limit");
		
		String from = yyyymmdd + "000000";
		String to = yyyymmdd + "235959";
		
		logger.debug("## from [" + from + "] + to [" + to + "]");
		
		StringBuffer queryBuffer = new StringBuffer();
		
		queryBuffer.append(" SELECT ");
		queryBuffer.append("     BATCH_ID, BATCH_TYPE, BATCH_STATUS, BATCH_CNT, BATCH_DATETIME ");
		queryBuffer.append(" FROM MDM_BATCH_LOG ");
		queryBuffer.append(" WHERE BATCH_DATETIME BETWEEN :from AND :to ");
		
		if(!"".equals(batch_id) && batch_id != null) {
			queryBuffer.append(" AND batch_id = :batch_id ");
		}
		
		if(!"".equals(batch_type) && batch_type != null) {
			queryBuffer.append(" AND batch_type = :batch_type ");
		}
		
		queryBuffer.append(" ORDER BY BATCH_ID DESC ");				
		SQLQuery query = getSession().createSQLQuery(queryBuffer.toString());
        
        query.setString("from", from);
        query.setString("to", to);
        
        if(!"".equals(batch_type) && batch_type != null) {
        	query.setString("batch_type", batch_type);
		}
        
        if(!"".equals(batch_id) && batch_id != null) {
        	query.setString("batch_id", batch_id);
		}
        
        if (page != null && limit != null) {
			query.setFirstResult((page - 1) * limit);
			query.setMaxResults(limit);
		}
        
        List<Object[]> obj = query.list();        
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();        
        
        for(int i = 0, size = obj.size() ; i < size ; i++) {
        	
        	Object[] objVal = obj.get(i);
        	HashMap<String, Object> hm = new HashMap<String, Object>();
			
			hm.put("BATCH_ID", ((Number)objVal[0]).doubleValue());			
			hm.put("BATCH_TYPE", ((String)objVal[1]));
			hm.put("BATCH_STATUS", String.valueOf(objVal[2]));
			hm.put("BATCH_CNT", ((Number)objVal[3]).doubleValue());
			hm.put("BATCH_DATETIME", ((String)objVal[4]));			
			
			result.add(hm);
		}
	
        return result;
	}
	
	@Override
	public int getMDMSLPCount(Map<String, Object> params) throws Exception {
		
		String yyyymmdd = params.get("yyyymmdd").toString();		
		String batch_id = params.get("batch_id").toString();
		String transfer_yn = params.get("transfer_yn").toString();
		String batch_yn = params.get("batch_yn").toString();
		String transfer_date = params.get("transfer_date").toString();
		
		String yyyymmddhhmmss = params.get("yyyymmddhhmmss").toString();
		String mdev_id = params.get("mdev_id").toString();
		
		String from = yyyymmdd + "000000";
		String to = yyyymmdd + "235959";
		
		logger.debug("## from [" + from + "] + to [" + to + "]");
		
		StringBuffer queryBuffer = new StringBuffer();
		
		queryBuffer.append(" SELECT count(*) as cnt \n");
        queryBuffer.append(" FROM MDM_LP_EM A \n");           
        queryBuffer.append(" WHERE A.INSERT_DATETIME BETWEEN :from AND :to \n");
        
        if(!"".equals(batch_id) && batch_id != null) {
			queryBuffer.append(" AND A.batch_id = :batch_id ");
		}
		
		if(!"".equals(yyyymmddhhmmss) && yyyymmddhhmmss != null) {
			queryBuffer.append(" AND A.yyyymmddhhmmss like :yyyymmddhhmmss ");
		}
		
		if(!"".equals(transfer_date) && transfer_date != null) {
			queryBuffer.append(" AND A.TRANSFER_DATETIME like :transfer_date ");
		}
		
		if(!"".equals(mdev_id) && mdev_id != null) {
			queryBuffer.append(" AND A.mdev_id like :mdev_id ");
		}		
		
		if("Y".equals(transfer_yn)) {
			queryBuffer.append(" AND A.TRANSFER_DATETIME is not null ");
		} else if("N".equals(transfer_yn)) {
			queryBuffer.append(" AND A.TRANSFER_DATETIME is null ");
		}
		
		if("Y".equals(batch_yn)) {
			queryBuffer.append(" AND A.BATCH_ID is not null ");
		} else if("N".equals(batch_yn)) {
			queryBuffer.append(" AND A.BATCH_ID is null ");
		}
			
		SQLQuery query = getSession().createSQLQuery(queryBuffer.toString());
        
        query.setString("from", from);
        query.setString("to", to);
        
        if(!"".equals(batch_id) && batch_id != null) {
        	query.setString("batch_id", batch_id);
		}
        
        if(!"".equals(yyyymmddhhmmss) && yyyymmddhhmmss != null) {
        	query.setString("yyyymmddhhmmss", yyyymmddhhmmss + "%");
		}
        
        if(!"".equals(transfer_date) && transfer_date != null) {
        	query.setString("transfer_date", transfer_date + "%");
		}
        
        if(!"".equals(mdev_id) && mdev_id != null) {
        	query.setString("mdev_id", mdev_id + "%");
		}
        
        return ((Number) query.uniqueResult()).intValue();
	}
	
	@Override
	public List<Map<String, Object>> getMDMSLPList(Map<String, Object> params) throws Exception {
		
		String yyyymmdd = params.get("yyyymmdd").toString();		
		String batch_id = params.get("batch_id").toString();
		String transfer_yn = params.get("transfer_yn").toString();
		String batch_yn = params.get("batch_yn").toString();
		String transfer_date = params.get("transfer_date").toString();
		
		String yyyymmddhhmmss = params.get("yyyymmddhhmmss").toString();
		String mdev_id = params.get("mdev_id").toString();
		
		Integer page = (Integer) params.get("page");
		Integer limit = (Integer) params.get("limit");
		
		String from = yyyymmdd + "000000";
		String to = yyyymmdd + "235959";
		
		logger.debug("## from [" + from + "] + to [" + to + "]");
		
		StringBuffer queryBuffer = new StringBuffer();
		
		queryBuffer.append(" SELECT A.MDEV_ID, A.YYYYMMDDHHMMSS, A.MDEV_TYPE, A.BATCH_ID, A.CH1, A.TRANSFER_DATETIME, A.INSERT_DATETIME \n");
        queryBuffer.append(" FROM MDM_LP_EM A \n");           
        queryBuffer.append(" WHERE A.INSERT_DATETIME BETWEEN :from AND :to \n");
        
        if(!"".equals(batch_id) && batch_id != null) {
			queryBuffer.append(" AND A.batch_id = :batch_id ");
		}
		
		if(!"".equals(yyyymmddhhmmss) && yyyymmddhhmmss != null) {
			queryBuffer.append(" AND A.yyyymmddhhmmss like :yyyymmddhhmmss ");
		}
		
		if(!"".equals(transfer_date) && transfer_date != null) {
			queryBuffer.append(" AND A.TRANSFER_DATETIME like :transfer_date ");
		}
		
		if(!"".equals(mdev_id) && mdev_id != null) {
			queryBuffer.append(" AND A.mdev_id like :mdev_id ");
		}
		
		if("Y".equals(transfer_yn)) {
			queryBuffer.append(" AND A.TRANSFER_DATETIME is not null ");
		} else if("N".equals(transfer_yn)) {
			queryBuffer.append(" AND A.TRANSFER_DATETIME is null ");
		}
		
		if("Y".equals(batch_yn)) {
			queryBuffer.append(" AND A.BATCH_ID is not null ");
		} else if("N".equals(batch_yn)) {
			queryBuffer.append(" AND A.BATCH_ID is null ");
		}
			
		SQLQuery query = getSession().createSQLQuery(queryBuffer.toString());
        
        query.setString("from", from);
        query.setString("to", to);
        
        if(!"".equals(batch_id) && batch_id != null) {
        	query.setString("batch_id", batch_id);
		}
        
        if(!"".equals(yyyymmddhhmmss) && yyyymmddhhmmss != null) {
        	query.setString("yyyymmddhhmmss", yyyymmddhhmmss + "%");
		}
        
        if(!"".equals(transfer_date) && transfer_date != null) {
        	query.setString("transfer_date", transfer_date + "%");
		}
        
        if(!"".equals(mdev_id) && mdev_id != null) {
        	query.setString("mdev_id", mdev_id + "%");
		}
        
        if (page != null && limit != null) {
			query.setFirstResult((page - 1) * limit);
			query.setMaxResults(limit);
		}
        
        List<Object[]> obj = query.list();        
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();        
        
        for(int i = 0, size = obj.size() ; i < size ; i++) {
        	
        	Object[] objVal = obj.get(i);
        	HashMap<String, Object> hm = new HashMap<String, Object>();
        	
			hm.put("MDEV_ID", ((String)objVal[0]));			
			hm.put("YYYYMMDDHHMMSS", ((String)objVal[1]));
			hm.put("MDEV_TYPE", String.valueOf(objVal[2]));
			
			if(objVal[3] != null) {
				hm.put("BATCH_ID", ((Number)objVal[3]).doubleValue());
			} else {
				hm.put("BATCH_ID", "");
			}
			
			hm.put("CH1", String.valueOf(objVal[4]));			
			hm.put("TRANSFER_DATETIME", ((String)objVal[5]));
			hm.put("INSERT_DATETIME", ((String)objVal[6]));
			
			result.add(hm);
		}
        
        return result;
	}

	@Override
	public int getMDMSDailyCount(Map<String, Object> params) throws Exception {
		
		String insert_datetime = params.get("insert_datetime").toString();		
		String batch_id = params.get("batch_id").toString();
		String transfer_yn = params.get("transfer_yn").toString();
		String batch_yn = params.get("batch_yn").toString();
		String transfer_date = params.get("transfer_date").toString();
		String mdev_id = params.get("mdev_id").toString();
		String yyyymmdd = params.get("yyyymmdd").toString();
		
		String from = insert_datetime + "000000";
		String to = insert_datetime + "235959";
		
		logger.debug("## from [" + from + "] + to [" + to + "]");
		
		StringBuffer queryBuffer = new StringBuffer();
		
		queryBuffer.append(" SELECT count(*) as cnt \n");
        queryBuffer.append(" FROM MDM_BILLING_DAY_EM A \n");
        queryBuffer.append(" WHERE A.INSERT_DATETIME BETWEEN :from AND :to \n");
        
        if(!"".equals(batch_id) && batch_id != null) {
			queryBuffer.append(" AND A.batch_id = :batch_id ");
		}
		
		if(!"".equals(transfer_date) && transfer_date != null) {
			queryBuffer.append(" AND A.TRANSFER_DATETIME like :transfer_date ");
		}
		
		if(!"".equals(mdev_id) && mdev_id != null) {
			queryBuffer.append(" AND A.mdev_id like :mdev_id ");
		}
		
		if("Y".equals(transfer_yn)) {
			queryBuffer.append(" AND A.TRANSFER_DATETIME is not null ");
		} else if("N".equals(transfer_yn)) {
			queryBuffer.append(" AND A.TRANSFER_DATETIME is null ");
		}
		
		if("Y".equals(batch_yn)) {
			queryBuffer.append(" AND A.BATCH_ID is not null ");
		} else if("N".equals(batch_yn)) {
			queryBuffer.append(" AND A.BATCH_ID is null ");
		}
		
		if(!"".equals(yyyymmdd) && yyyymmdd != null) {
			queryBuffer.append(" AND A.yyyymmdd = :yyyymmdd ");
		}
			
			
		SQLQuery query = getSession().createSQLQuery(queryBuffer.toString());
        
        query.setString("from", from);
        query.setString("to", to);
        
        if(!"".equals(batch_id) && batch_id != null) {
        	query.setString("batch_id", batch_id);
		}
        
        if(!"".equals(transfer_date) && transfer_date != null) {
        	query.setString("transfer_date", transfer_date + "%");
		}
        
        if(!"".equals(mdev_id) && mdev_id != null) {
        	query.setString("mdev_id", mdev_id + "%");
		}
        
        if(!"".equals(yyyymmdd) && yyyymmdd != null) {
        	query.setString("yyyymmdd", yyyymmdd);
        }
        
        return ((Number) query.uniqueResult()).intValue();
	}

	@Override
	public List<Map<String, Object>> getMDMSDailyList(Map<String, Object> params) throws Exception {
		
		String insert_datetime = params.get("insert_datetime").toString();		
		String batch_id = params.get("batch_id").toString();
		String transfer_yn = params.get("transfer_yn").toString();
		String batch_yn = params.get("batch_yn").toString();
		String transfer_date = params.get("transfer_date").toString();
		String yyyymmdd = params.get("yyyymmdd").toString();
		
		String mdev_id = params.get("mdev_id").toString();
		
		Integer page = (Integer) params.get("page");
		Integer limit = (Integer) params.get("limit");
		
		String from = insert_datetime + "000000";
		String to = insert_datetime + "235959";
		
		logger.debug("## from [" + from + "] + to [" + to + "]");
		
		StringBuffer queryBuffer = new StringBuffer();
		
		queryBuffer.append(" SELECT A.MDEV_ID, A.YYYYMMDD || A.HHMMSS AS YYYYMMDDHHMMSS, A.CUMULACTIVEENGYIMPORT, A.CUMULREACTIVEENGYIMPORT, A.BATCH_ID, A.TRANSFER_DATETIME, A.INSERT_DATETIME \n");
        queryBuffer.append(" FROM MDM_BILLING_DAY_EM A \n");           
        queryBuffer.append(" WHERE A.INSERT_DATETIME BETWEEN :from AND :to \n");
        
        if(!"".equals(batch_id) && batch_id != null) {
			queryBuffer.append(" AND A.batch_id = :batch_id ");
		}
		
		if(!"".equals(transfer_date) && transfer_date != null) {
			queryBuffer.append(" AND A.TRANSFER_DATETIME like :transfer_date ");
		}
		
		if(!"".equals(mdev_id) && mdev_id != null) {
			queryBuffer.append(" AND A.mdev_id like :mdev_id ");
		}
		
		if("Y".equals(transfer_yn)) {
			queryBuffer.append(" AND A.TRANSFER_DATETIME is not null ");
		} else if("N".equals(transfer_yn)) {
			queryBuffer.append(" AND A.TRANSFER_DATETIME is null ");
		}
		
		if("Y".equals(batch_yn)) {
			queryBuffer.append(" AND A.BATCH_ID is not null ");
		} else if("N".equals(batch_yn)) {
			queryBuffer.append(" AND A.BATCH_ID is null ");
		}
		
		if(!"".equals(yyyymmdd) && yyyymmdd != null) {
			queryBuffer.append(" AND A.yyyymmdd = :yyyymmdd ");
		}
			
		SQLQuery query = getSession().createSQLQuery(queryBuffer.toString());
        
        query.setString("from", from);
        query.setString("to", to);
        
        if(!"".equals(batch_id) && batch_id != null) {
        	query.setString("batch_id", batch_id);
		}
        
        if(!"".equals(transfer_date) && transfer_date != null) {
        	query.setString("transfer_date", transfer_date + "%");
		}
        
        if(!"".equals(mdev_id) && mdev_id != null) {
        	query.setString("mdev_id", mdev_id + "%");
		}
        
        if(!"".equals(yyyymmdd) && yyyymmdd != null) {
        	query.setString("yyyymmdd", yyyymmdd);
		}
        
        if (page != null && limit != null) {
			query.setFirstResult((page - 1) * limit);
			query.setMaxResults(limit);
		}
        
        List<Object[]> obj = query.list();        
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();        
        
        for(int i = 0, size = obj.size() ; i < size ; i++) {
        	
        	Object[] objVal = obj.get(i);
        	HashMap<String, Object> hm = new HashMap<String, Object>();
        	
			hm.put("MDEV_ID", ((String)objVal[0]));			
			hm.put("YYYYMMDDHHMMSS", ((String)objVal[1]));
			hm.put("CUMULACTIVEENGYIMPORT", ((Number)objVal[2]).doubleValue());
			hm.put("CUMULREACTIVEENGYIMPORT", ((Number)objVal[3]).doubleValue());
			
			if(objVal[4] != null) {
				hm.put("BATCH_ID", ((Number)objVal[4]).doubleValue());
			} else {
				hm.put("BATCH_ID", "");
			}
			
			hm.put("TRANSFER_DATETIME", ((String)objVal[5]));
			hm.put("INSERT_DATETIME", ((String)objVal[6]));
			
			result.add(hm);
		}
        
        return result;
	}

	@Override
	public int getMDMSMonthlyCount(Map<String, Object> params) throws Exception {

		String insert_datetime = params.get("insert_datetime").toString();		
		String batch_id = params.get("batch_id").toString();
		String transfer_yn = params.get("transfer_yn").toString();
		String batch_yn = params.get("batch_yn").toString();
		String transfer_date = params.get("transfer_date").toString();
		String mdev_id = params.get("mdev_id").toString();
		String yyyymmdd = params.get("yyyymmdd").toString();
		
		String from = insert_datetime + "000000";
		String to = insert_datetime + "235959";
		
		logger.debug("## from [" + from + "] + to [" + to + "]");
		
		StringBuffer queryBuffer = new StringBuffer();
		
		queryBuffer.append(" SELECT count(*) as cnt \n");
        queryBuffer.append(" FROM MDM_BILLING_MONTH_EM A \n");
        queryBuffer.append(" WHERE A.INSERT_DATETIME BETWEEN :from AND :to \n");
        
        if(!"".equals(batch_id) && batch_id != null) {
			queryBuffer.append(" AND A.batch_id = :batch_id ");
		}
		
		if(!"".equals(transfer_date) && transfer_date != null) {
			queryBuffer.append(" AND A.TRANSFER_DATETIME like :transfer_date ");
		}
		
		if(!"".equals(mdev_id) && mdev_id != null) {
			queryBuffer.append(" AND A.mdev_id like :mdev_id ");
		}
		
		if("Y".equals(transfer_yn)) {
			queryBuffer.append(" AND A.TRANSFER_DATETIME is not null ");
		} else if("N".equals(transfer_yn)) {
			queryBuffer.append(" AND A.TRANSFER_DATETIME is null ");
		}
		
		if("Y".equals(batch_yn)) {
			queryBuffer.append(" AND A.BATCH_ID is not null ");
		} else if("N".equals(batch_yn)) {
			queryBuffer.append(" AND A.BATCH_ID is null ");
		}
		
		if(!"".equals(yyyymmdd) && yyyymmdd != null) {
			queryBuffer.append(" AND A.yyyymmdd = :yyyymmdd ");
		}
			
			
		SQLQuery query = getSession().createSQLQuery(queryBuffer.toString());
        
        query.setString("from", from);
        query.setString("to", to);
        
        if(!"".equals(batch_id) && batch_id != null) {
        	query.setString("batch_id", batch_id);
		}
        
        if(!"".equals(transfer_date) && transfer_date != null) {
        	query.setString("transfer_date", transfer_date + "%");
		}
        
        if(!"".equals(mdev_id) && mdev_id != null) {
        	query.setString("mdev_id", mdev_id + "%");
		}
        
        if(!"".equals(yyyymmdd) && yyyymmdd != null) {
        	query.setString("yyyymmdd", yyyymmdd);
        }
        
        return ((Number) query.uniqueResult()).intValue();
	}

	@Override
	public List<Map<String, Object>> getMDMSMonthlyList(Map<String, Object> params) throws Exception {

		String insert_datetime = params.get("insert_datetime").toString();		
		String batch_id = params.get("batch_id").toString();
		String transfer_yn = params.get("transfer_yn").toString();
		String batch_yn = params.get("batch_yn").toString();
		String transfer_date = params.get("transfer_date").toString();
		String yyyymmdd = params.get("yyyymmdd").toString();
		
		String mdev_id = params.get("mdev_id").toString();
		
		Integer page = (Integer) params.get("page");
		Integer limit = (Integer) params.get("limit");
		
		String from = insert_datetime + "000000";
		String to = insert_datetime + "235959";
		
		logger.debug("## from [" + from + "] + to [" + to + "]");
		
		StringBuffer queryBuffer = new StringBuffer();
		
		queryBuffer.append(" SELECT A.MDEV_ID, A.YYYYMMDD || A.HHMMSS AS YYYYMMDDHHMMSS, A.CUMULACTIVEENGYIMPORT, A.CUMULREACTIVEENGYIMPORT, A.BATCH_ID, A.TRANSFER_DATETIME, A.INSERT_DATETIME \n");
        queryBuffer.append(" FROM MDM_BILLING_MONTH_EM A \n");           
        queryBuffer.append(" WHERE A.INSERT_DATETIME BETWEEN :from AND :to \n");
        
        if(!"".equals(batch_id) && batch_id != null) {
			queryBuffer.append(" AND A.batch_id = :batch_id ");
		}
		
		if(!"".equals(transfer_date) && transfer_date != null) {
			queryBuffer.append(" AND A.TRANSFER_DATETIME like :transfer_date ");
		}
		
		if(!"".equals(mdev_id) && mdev_id != null) {
			queryBuffer.append(" AND A.mdev_id like :mdev_id ");
		}
		
		if("Y".equals(transfer_yn)) {
			queryBuffer.append(" AND A.TRANSFER_DATETIME is not null ");
		} else if("N".equals(transfer_yn)) {
			queryBuffer.append(" AND A.TRANSFER_DATETIME is null ");
		}
		
		if("Y".equals(batch_yn)) {
			queryBuffer.append(" AND A.BATCH_ID is not null ");
		} else if("N".equals(batch_yn)) {
			queryBuffer.append(" AND A.BATCH_ID is null ");
		}
		
		if(!"".equals(yyyymmdd) && yyyymmdd != null) {
			queryBuffer.append(" AND A.yyyymmdd = :yyyymmdd ");
		}
			
		SQLQuery query = getSession().createSQLQuery(queryBuffer.toString());
        
        query.setString("from", from);
        query.setString("to", to);
        
        if(!"".equals(batch_id) && batch_id != null) {
        	query.setString("batch_id", batch_id);
		}
        
        if(!"".equals(transfer_date) && transfer_date != null) {
        	query.setString("transfer_date", transfer_date + "%");
		}
        
        if(!"".equals(mdev_id) && mdev_id != null) {
        	query.setString("mdev_id", mdev_id + "%");
		}
        
        if(!"".equals(yyyymmdd) && yyyymmdd != null) {
        	query.setString("yyyymmdd", yyyymmdd);
		}
        
        if (page != null && limit != null) {
			query.setFirstResult((page - 1) * limit);
			query.setMaxResults(limit);
		}
        
        List<Object[]> obj = query.list();        
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();        
        
        for(int i = 0, size = obj.size() ; i < size ; i++) {
        	
        	Object[] objVal = obj.get(i);
        	HashMap<String, Object> hm = new HashMap<String, Object>();
        	
        	hm.put("MDEV_ID", ((String)objVal[0]));			
			hm.put("YYYYMMDDHHMMSS", ((String)objVal[1]));
			hm.put("CUMULACTIVEENGYIMPORT", ((Number)objVal[2]).doubleValue());
			hm.put("CUMULREACTIVEENGYIMPORT", ((Number)objVal[3]).doubleValue());
			
			if(objVal[4] != null) {
				hm.put("BATCH_ID", ((Number)objVal[4]).doubleValue());
			} else {
				hm.put("BATCH_ID", "");
			}
			
			hm.put("TRANSFER_DATETIME", ((String)objVal[5]));
			hm.put("INSERT_DATETIME", ((String)objVal[6]));
			
			result.add(hm);
		}
        
        return result;
	}

	@Override
	public int getMDMSEventCount(Map<String, Object> params) throws Exception {
		String insert_datetime = params.get("insert_datetime").toString();		
		String batch_id = params.get("batch_id").toString();
		String transfer_yn = params.get("transfer_yn").toString();
		String batch_yn = params.get("batch_yn").toString();
		String transfer_date = params.get("transfer_date").toString();
		String mdev_id = params.get("mdev_id").toString();
		String yyyymmdd = params.get("yyyymmdd").toString();
		
		String from = insert_datetime + "000000";
		String to = insert_datetime + "235959";
		
		logger.debug("## from [" + from + "] + to [" + to + "]");
		
		StringBuffer queryBuffer = new StringBuffer();
		
		queryBuffer.append(" SELECT count(*) as cnt \n");
        queryBuffer.append(" FROM MDM_METEREVENT_LOG A \n");
        queryBuffer.append(" WHERE A.INSERT_DATETIME BETWEEN :from AND :to \n");
        
        if(!"".equals(batch_id) && batch_id != null) {
			queryBuffer.append(" AND A.batch_id = :batch_id ");
		}
		
		if(!"".equals(transfer_date) && transfer_date != null) {
			queryBuffer.append(" AND A.TRANSFER_DATETIME like :transfer_date ");
		}
		
		if(!"".equals(mdev_id) && mdev_id != null) {
			queryBuffer.append(" AND A.activator_id like :mdev_id ");
		}
		
		if("Y".equals(transfer_yn)) {
			queryBuffer.append(" AND A.TRANSFER_DATETIME is not null ");
		} else if("N".equals(transfer_yn)) {
			queryBuffer.append(" AND A.TRANSFER_DATETIME is null ");
		}
		
		if("Y".equals(batch_yn)) {
			queryBuffer.append(" AND A.BATCH_ID is not null ");
		} else if("N".equals(batch_yn)) {
			queryBuffer.append(" AND A.BATCH_ID is null ");
		}
		
		if(!"".equals(yyyymmdd) && yyyymmdd != null) {
			queryBuffer.append(" AND A.yyyymmdd = :yyyymmdd ");
		}
			
			
		SQLQuery query = getSession().createSQLQuery(queryBuffer.toString());
        
        query.setString("from", from);
        query.setString("to", to);
        
        if(!"".equals(batch_id) && batch_id != null) {
        	query.setString("batch_id", batch_id);
		}
        
        if(!"".equals(transfer_date) && transfer_date != null) {
        	query.setString("transfer_date", transfer_date + "%");
		}
        
        if(!"".equals(mdev_id) && mdev_id != null) {
        	query.setString("mdev_id", mdev_id + "%");
		}
        
        if(!"".equals(yyyymmdd) && yyyymmdd != null) {
        	query.setString("yyyymmdd", yyyymmdd);
        }
        
        return ((Number) query.uniqueResult()).intValue();
	}

	@Override
	public List<Map<String, Object>> getMDMSEventList(Map<String, Object> params) throws Exception {
		String insert_datetime = params.get("insert_datetime").toString();		
		String batch_id = params.get("batch_id").toString();
		String transfer_yn = params.get("transfer_yn").toString();
		String batch_yn = params.get("batch_yn").toString();
		String transfer_date = params.get("transfer_date").toString();
		String yyyymmdd = params.get("yyyymmdd").toString();
		
		String mdev_id = params.get("mdev_id").toString();
		
		Integer page = (Integer) params.get("page");
		Integer limit = (Integer) params.get("limit");
		
		String from = insert_datetime + "000000";
		String to = insert_datetime + "235959";
		
		logger.debug("## from [" + from + "] + to [" + to + "]");
		
		StringBuffer queryBuffer = new StringBuffer();
		
		queryBuffer.append(" SELECT A.ACTIVATOR_ID, A.METEREVENT_ID, A.OPEN_TIME, A.MESSAGE, A.TRANSFER_DATETIME, A.INSERT_DATETIME, A.BATCH_ID, B.VALUE \n");
        queryBuffer.append(" FROM MDM_METEREVENT_LOG A \n");    
        queryBuffer.append(" INNER JOIN METEREVENT B ON (A.METEREVENT_ID = B.ID) \n"); 
        queryBuffer.append(" WHERE A.INSERT_DATETIME BETWEEN :from AND :to \n");
        
        if(!"".equals(batch_id) && batch_id != null) {
			queryBuffer.append(" AND A.batch_id = :batch_id ");
		}
		
		if(!"".equals(transfer_date) && transfer_date != null) {
			queryBuffer.append(" AND A.TRANSFER_DATETIME like :transfer_date ");
		}
		
		if(!"".equals(mdev_id) && mdev_id != null) {
			queryBuffer.append(" AND A.activator_id like :mdev_id ");
		}
		
		if("Y".equals(transfer_yn)) {
			queryBuffer.append(" AND A.TRANSFER_DATETIME is not null ");
		} else if("N".equals(transfer_yn)) {
			queryBuffer.append(" AND A.TRANSFER_DATETIME is null ");
		}
		
		if("Y".equals(batch_yn)) {
			queryBuffer.append(" AND A.BATCH_ID is not null ");
		} else if("N".equals(batch_yn)) {
			queryBuffer.append(" AND A.BATCH_ID is null ");
		}
		
		if(!"".equals(yyyymmdd) && yyyymmdd != null) {
			queryBuffer.append(" AND A.yyyymmdd = :yyyymmdd ");
		}
			
		SQLQuery query = getSession().createSQLQuery(queryBuffer.toString());
        
        query.setString("from", from);
        query.setString("to", to);
        
        if(!"".equals(batch_id) && batch_id != null) {
        	query.setString("batch_id", batch_id);
		}
        
        if(!"".equals(transfer_date) && transfer_date != null) {
        	query.setString("transfer_date", transfer_date + "%");
		}
        
        if(!"".equals(mdev_id) && mdev_id != null) {
        	query.setString("mdev_id", mdev_id + "%");
		}
        
        if(!"".equals(yyyymmdd) && yyyymmdd != null) {
        	query.setString("yyyymmdd", yyyymmdd);
		}
        
        if (page != null && limit != null) {
			query.setFirstResult((page - 1) * limit);
			query.setMaxResults(limit);
		}
        
        List<Object[]> obj = query.list();        
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();        
        
        for(int i = 0, size = obj.size() ; i < size ; i++) {
        	
        	Object[] objVal = obj.get(i);
        	HashMap<String, Object> hm = new HashMap<String, Object>();
        	    	
			hm.put("MDEV_ID", ((String)objVal[0]));			
			hm.put("METEREVENT_ID", ((String)objVal[1]));
			hm.put("OPEN_TIME", ((String)objVal[2]));
			
			String message = (String)objVal[3];
			
			hm.put("MESSAGE", message);
			hm.put("TRANSFER_DATETIME", ((String)objVal[4]));
			hm.put("INSERT_DATETIME", ((String)objVal[5]));
			
			if(objVal[6] != null) {
				hm.put("BATCH_ID", ((Number)objVal[6]).doubleValue());
			} else {
				hm.put("BATCH_ID", "");
			}
			hm.put("VALUE", (String.valueOf(objVal[7])));
			
			result.add(hm);
		}
        
        return result;
	}
}