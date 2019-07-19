package com.aimir.cms.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.cms.model.DebtEnt;
import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.util.SQLWrapper;
import com.aimir.util.StringUtil;

@Repository(value="debtEntDao")
public class DebtEntDaoImpl extends AbstractHibernateGenericDao<DebtEnt, Integer> implements DebtEntDao{
	
	Log logger = LogFactory.getLog(DebtEntDaoImpl.class);
    
	@Autowired
	protected DebtEntDaoImpl(SessionFactory sessionFactory) {
		super(DebtEnt.class);
		super.setSessionFactory(sessionFactory);
	}
	
	public List<DebtEnt> getDebt(String customerNo, String debtType, String debtRef) {

		StringBuffer sb = new StringBuffer();
		sb.append("\nFROM DebtEnt debt ");
		sb.append("\nWHERE debtStatus <> 'CANCEL' ");
		if(customerNo != null && !customerNo.isEmpty()) {
			sb.append("\nAND  debt.id.customerId=:customerNo ");
		}
		if(debtRef != null && !debtRef.isEmpty()) {
			sb.append("\nAND  debt.id.debtRef=:debtRef ");
		}
		if(debtType != null && !debtType.isEmpty()) {
			sb.append("\nAND  debt.debtType=:debtType ");
		}
		
		Query query = getSession().createQuery(new SQLWrapper().getQuery(sb.toString()));
		if(customerNo != null && !customerNo.isEmpty()) {
			query.setString("customerNo", customerNo);
		}
		if(debtRef != null && !debtRef.isEmpty()) {
			query.setString("debtRef", debtRef);
		}
		if(debtType != null && !debtType.isEmpty()) {
			query.setString("debtType", debtType);
		}
		
		return (List<DebtEnt>) query.list();
	}
	
	public List<Map<String, Object>> getPrepaymentChargeContractList(Map<String, Object> condition, boolean isCount) {
        List<Map<String, Object>> result;
        Integer supplierId = (Integer)condition.get("supplierId");
        String barcode = StringUtil.nullToBlank(condition.get("barcode"));
        String contractNumber = StringUtil.nullToBlank(condition.get("contractNumber"));
        String customerNo = StringUtil.nullToBlank(condition.get("customerNo"));
        String customerName = StringUtil.nullToBlank(condition.get("customerName"));
        String mdsId = StringUtil.nullToBlank(condition.get("mdsId"));
        Integer page = (Integer)condition.get("page");
        Integer limit = (Integer)condition.get("limit");

        StringBuilder sb = new StringBuilder();

        if(isCount) {
            sb.append("\nSELECT count(cust.id) as total");
        } else {
            sb.append("\nSELECT cont.id AS CONTRACTID, ");
            sb.append("\n       meter.id AS METERID, ");
            sb.append("\n       cust.id AS CUSTOMERID, ");
            sb.append("\n       cont.contract_Number AS CONTRACTNUMBER, ");
            sb.append("\n       cont.currentCredit AS CURRENTCREDIT, ");
            sb.append("\n       cont.currentArrears AS CURRENTARREARS, ");
            sb.append("\n       cont.contract_Price AS CONTRACTPRICE, ");
            sb.append("\n       cust.customerNo AS CUSTOMERNO, ");
            sb.append("\n       cust.name AS CUSTOMERNAME, ");
            sb.append("\n       cont.barcode AS BARCODE, ");
    //        sb.append("\n       cust.address AS ADDRESS, ");
    //        sb.append("\n       cust.address1 AS ADDRESS1, ");
            sb.append("\n       cust.address2 AS ADDRESS, ");
    //        sb.append("\n       cust.address3 AS ADDRESS3, ");
            sb.append("\n       meter.mds_Id AS MDSID, ");
            sb.append("\n       cont.lastTokenDate AS LASTTOKENDATE, ");
            sb.append("\n       cont.lastTokenId AS LASKTOKENID, ");
            sb.append("\n       cont.contractDemand AS CONTRACTDEMAND, ");
            sb.append("\n       tariff.code AS TARIFFCODE, ");
            sb.append("\n       mcu.sys_ID AS MCUID, ");
            sb.append("\n       cont.charge_Available AS CHARGEAVAILABLE, ");
            sb.append("\n       stat.descr AS STATUSNAME, ");
            sb.append("\n       debt.amount AS DEBTAMOUNT ");
        }
        sb.append("\nFROM Customer cust ");
        sb.append("\n     LEFT OUTER JOIN ");
        sb.append("\n     (select customer_id,sum(debt_amount) as amount from WS_CMS_DEBTENT where debt_status <> 'CANCEL' group by customer_id) debt on(cust.customerNo = debt.customer_id) ");
        sb.append("\n     , Contract cont ");
        sb.append("\n     LEFT OUTER JOIN ");
        sb.append("\n     Meter meter on(cont.meter_id = meter.id) ");
        sb.append("\n     LEFT OUTER JOIN ");
        sb.append("\n     Modem modem on(meter.modem_id = modem.id) ");
        sb.append("\n     LEFT OUTER JOIN ");
        sb.append("\n     Mcu mcu on(modem.mcu_id = mcu.id) ");
        sb.append("\n     LEFT OUTER JOIN ");
        sb.append("\n     Code stat on(cont.status_id = stat.id)");
        sb.append("\n     LEFT OUTER JOIN ");
        sb.append("\n     Code credit on(cont.credittype_id = credit.id)");
        sb.append("\n     LEFT OUTER JOIN ");
        sb.append("\n     Code tariff on(cont.tariffIndex_id = tariff.id)");

        sb.append("\nWHERE cust.id = cont.customer_id ");
        sb.append("\nAND   cont.customer_id is not null ");
        sb.append("\nAND   cont.supplier_id = :supplierId ");
        sb.append("\nAND   credit.code IN ('2.2.1', '2.2.2') ");   // prepay, emergency credit

        if (!contractNumber.isEmpty()) {
            sb.append("\nAND   cont.contract_Number LIKE :contractNumber ");
        }
        if (!customerNo.isEmpty()) {
            sb.append("\nAND   cust.customerNo LIKE :customerNo ");
        }
        if (!customerName.isEmpty()) {
            sb.append("\nAND   cust.name LIKE :customerName ");
        }
        if (!mdsId.isEmpty()) {
            sb.append("\nAND   meter.mds_Id LIKE :mdsId ");
        }
        if (!barcode.isEmpty()) {
            sb.append("\nAND   cont.barcode = :barcode ");
        }
        if (!isCount) {
            sb.append("\nORDER BY cont.contract_Number ");
        } else {
            sb.append("\nAND   cont.tariffIndex_Id is not null  ");
        }
        SQLQuery query = null;
        try {
	        query = getSession().createSQLQuery(new SQLWrapper().getQuery(sb.toString()));
	        query.setInteger("supplierId", supplierId);
	
	        if (!contractNumber.isEmpty()) {
	            query.setString("contractNumber", contractNumber + "%");
	        }
	        if (!customerNo.isEmpty()) {
	            query.setString("customerNo", customerNo + "%");
	        }
	        if (!customerName.isEmpty()) {
	            query.setString("customerName", customerName + "%");
	        }
	        if (!mdsId.isEmpty()) {
	            query.setString("mdsId", mdsId + "%");
	        }
	        if (!barcode.isEmpty()) {
	            query.setString("barcode", barcode);
	        }
	        if (isCount) {
	            Map<String, Object> map = new HashMap<String, Object>();
	            Number count = null;
	            if(query.list() != null && query.list().size() > 0) {
	                count = (Number) query.list().get(0);
	            }
	            map.put("TOTAL", count.intValue());
	            result = new ArrayList<Map<String, Object>>();
	            result.add(map);
	
	        } else {
	            query.setFirstResult((page - 1) * limit);
	            query.setMaxResults(limit);
	            result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	        }
        } catch(Exception e) {
        	logger.error(e,e);
        	result = new ArrayList<Map<String, Object>>();
        }

        return result;
	}
	
	public List<Map<String,Object>> getDebtInfoByCustomerNo(String customerNo, String debtType, String debtRef) {
		StringBuffer sb = new StringBuffer();
		sb.append("\nSELECT debt.id.customerId as customerId, ");
		sb.append("\n		debt.id.debtRef as debtRef, ");
		sb.append("\n		debt.debtAmount as debtAmount, ");
		sb.append("\n		debt.debtStatus as debtStatus, ");
		sb.append("\n		debt.debtType as debtType, ");
		sb.append("\n		debt.firstDebt as firstDebt, ");
		sb.append("\n		debt.debtContractCount as debtContractCount, ");
		sb.append("\n		debt.debtPaymentCount as debtPaymentCount ");
		sb.append("\nFROM DebtEnt debt ");
		sb.append("\nWHERE debt.debtAmount > 0 ");
		sb.append("\nAND debt.debtStatus <> 'CANCEL' ");
		sb.append("\nAND debt.id.customerId=:customerNo ");
		if(debtType != null && !debtType.isEmpty()) {
			sb.append("\nAND debt.debtType =:debtType ");	
		}
		if(debtRef != null && !debtRef.isEmpty()) {
			sb.append("\nAND debt.id.debtRef =:debtRef ");
		}
		
		Query query = getSession().createQuery(new SQLWrapper().getQuery(sb.toString()));
		query.setString("customerNo", customerNo);
		if(debtType != null && !debtType.isEmpty()) {
			query.setString("debtType", debtType);
		}
		if(debtRef != null && !debtRef.isEmpty()) {
			query.setString("debtRef", debtRef);
		}
		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	}
	
	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public void modifyDebtInfo(Map<String, Object> tempData) {
		String customerNo = StringUtil.nullToBlank(tempData.get("customerNo"));
		String debtRef = StringUtil.nullToBlank(tempData.get("debtRef"));
		Integer debtContractCnt = Integer.parseInt(tempData.get("debtContractCnt").toString());
		Double firstDebt = firstDebt = Double.parseDouble(tempData.get("debtAmount").toString());

        StringBuffer sb = new StringBuffer();
        sb.append("\nUPDATE DebtEnt ");
        sb.append("\nset debtContractCount = :debtContractCnt , firstDebt = :firstDebt ");
        sb.append("\nWHERE id.customerId=:customerNo ");
        sb.append("\nAND id.debtRef=:debtRef");
        Query query = getSession().createQuery(sb.toString());
        query.setInteger("debtContractCnt",debtContractCnt);
        query.setDouble("firstDebt",firstDebt);
        query.setString("customerNo",customerNo);
        query.setString("debtRef", debtRef);
        
        query.executeUpdate();
	}

}