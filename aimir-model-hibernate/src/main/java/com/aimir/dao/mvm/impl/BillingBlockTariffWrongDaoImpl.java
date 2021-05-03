package com.aimir.dao.mvm.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.mvm.BillingBlockTariffWrongDao;
import com.aimir.model.mvm.BillingBlockTariffWrong;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.StringUtil;

@Repository(value = "billingBlockTariffWrongDao")
public class BillingBlockTariffWrongDaoImpl  extends AbstractHibernateGenericDao<BillingBlockTariffWrong, Integer> implements BillingBlockTariffWrongDao {

    @SuppressWarnings("unused")
	private static Log logger = LogFactory.getLog(BillingBlockTariffWrongDaoImpl.class);
    
	@Autowired
	protected BillingBlockTariffWrongDaoImpl(SessionFactory sessionFactory) {
		super(BillingBlockTariffWrong.class);
		super.setSessionFactory(sessionFactory);
	}

	@Override
	public BillingBlockTariffWrong getBillingBlockTariffWrong(Map<String, Object> condition) {
		String code = StringUtil.nullToBlank(condition.get("code"));
		String mdevId = StringUtil.nullToBlank(condition.get("mdevId"));
		String prevYyyymmddhh = StringUtil.nullToBlank(condition.get("prevYyyymmddhh"));
		
		logger.debug("code: "+code+", mdevId:"+mdevId+", yyyymmddhh:"+prevYyyymmddhh);
		
		StringBuffer sbQuery = new StringBuffer();
		sbQuery.append("select ");		
		sbQuery.append("\n * ");
		sbQuery.append("\n from BILLING_BLOCK_TARIFF_WRONG bw ");		
		sbQuery.append("\n where 1=1 and bw.CODE = :code ");
		sbQuery.append("\n and bw.MDEV_ID = :mdevId ");
		sbQuery.append("\n and bw.prev_YYYYMMDDHH = :prevYyyymmddhh ");
		sbQuery.append("\n and bw.complateDate is null");
		
		logger.debug("sbQuery: "+sbQuery.toString());
		
		SQLQuery query = getSession().createSQLQuery(sbQuery.toString()).addEntity(BillingBlockTariffWrong.class);
		
		query.setString("code", code);
		query.setString("mdevId", mdevId);
		query.setString("prevYyyymmddhh", prevYyyymmddhh);
		
		List<BillingBlockTariffWrong> list = query.list();
		if(list == null || list.size() == 0)
			return null;
		else
			return list.get(0);
			
	}

	@Override
	public int updateComplateBillingBlockWrong(String mdevId) {
		String complateDate = DateTimeUtil.getDateString(new Date());
		logger.debug("mdevId: "+mdevId+", complateDate:"+complateDate);
		
		StringBuffer sbQuery = new StringBuffer();
		sbQuery.append(" update ");
		sbQuery.append("\n     BILLING_BLOCK_TARIFF_WRONG bw ");
		sbQuery.append("\n set ");
		sbQuery.append("\n     bw.COMPLATEDATE = :complateDate ");
		sbQuery.append("\n where ");
		sbQuery.append("\n     bw.COMPLATEDATE is null ");
		sbQuery.append("\n     and bw.MDEV_ID = :mdevId ");
		
		logger.debug("sbQuery: "+sbQuery.toString());
		SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
		
		query.setString("mdevId", mdevId);
		query.setString("complateDate", complateDate);
		
		return query.executeUpdate();		
	}

	@Override
	public Integer udpateBillingFail() {
		StringBuffer sbQuery = new StringBuffer();
		sbQuery.append("\n update ");
		sbQuery.append("\n     billing_block_tariff_wrong wr ");
		sbQuery.append("\n set ");
		sbQuery.append("\n     wr.COMPLATEDATE = sysdate, ");
		sbQuery.append("\n     wr.DESCR = 'contract or modem status is delete' ");
		sbQuery.append("\n where ");
		sbQuery.append("\n     wr.mdev_id in  ");
		sbQuery.append("\n ( ");
		sbQuery.append("\n     select  ");
		sbQuery.append("\n         ta.MDEV_ID ");
		sbQuery.append("\n     from     ");
		sbQuery.append("\n         billing_block_tariff_wrong ta, contract co, meter me, modem mo ");
		sbQuery.append("\n     where ");
		sbQuery.append("\n         ta.CONTRACT_ID = co.id ");
		sbQuery.append("\n         and ta.MDEV_ID = me.MDS_ID ");
		sbQuery.append("\n         and mo.id = me.MODEM_ID(+) ");
		sbQuery.append("\n         and ta.COMPLATEDATE is null ");
		sbQuery.append("\n         and (co.STATUS_ID = (select id from code where code = '2.1.3') ");
		sbQuery.append("\n         or me.meter_status = (select id from code where code = '1.3.3.9') ");
		sbQuery.append("\n         or me.modem_id is null ");
		sbQuery.append("\n         ) ");
		sbQuery.append("\n ) ");
		
		logger.debug("sbQuery: "+sbQuery.toString());
		SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
		
		return query.executeUpdate();
	}
	
	
	
	
}
