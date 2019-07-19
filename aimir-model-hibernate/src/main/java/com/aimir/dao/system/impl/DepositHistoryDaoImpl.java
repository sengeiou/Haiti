package com.aimir.dao.system.impl;

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

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.system.DepositHistoryDao;
import com.aimir.model.prepayment.DepositHistory;
import com.aimir.util.SQLWrapper;
import com.aimir.util.StringUtil;

@Repository("DepositHitoryDao")
public class DepositHistoryDaoImpl extends AbstractHibernateGenericDao<DepositHistory, Integer> implements DepositHistoryDao {
	Log logger = LogFactory.getLog(CodeDaoImpl.class);
	@Autowired
	protected DepositHistoryDaoImpl(SessionFactory sessionFactory) {
		super(DepositHistory.class);
		super.setSessionFactory(sessionFactory);
	}

    /**
     * method name : getHistoryList<b/>
     * method Desc : Deposit History List 를 조회한다. 
     *
     * @param params
     * @return
     */
    @SuppressWarnings("unchecked")
    @Deprecated
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public Map<String, Object> getHistoryList(Map<String, Object> params) {
        Map<String, Object> result = new HashMap<String, Object>(); 
        Integer page = (Integer) params.get("page");
        Integer limit = (Integer) params.get("limit");
        String reportType = StringUtil.nullToBlank(params.get("reportType"));
        String contract = StringUtil.nullToBlank(params.get("contract"));
        String vendor = StringUtil.nullToBlank(params.get("vendor"));
        String customerNo = StringUtil.nullToBlank(params.get("customerNo"));
        String customerName = StringUtil.nullToBlank(params.get("customerName"));
        String meterId = StringUtil.nullToBlank(params.get("meterId"));
        String casherId = StringUtil.nullToBlank(params.get("casherId"));
        String startDate = StringUtil.nullToBlank(params.get("startDate"));
        String endDate = StringUtil.nullToBlank(params.get("endDate"));

        StringBuilder sb = new StringBuilder();

        sb.append("\nSELECT dh ");
        sb.append("\nFROM DepositHistory dh ");
        sb.append("\n     LEFT OUTER JOIN dh.prepaymentLog pl ");
        sb.append("\nWHERE (dh.prepaymentLog IS NULL OR pl.isCanceled IS NULL OR pl.isCanceled = 0) ");    // cancel 제외
        
        if (reportType.equals("deposit")) {
            sb.append("\nAND dh.contract IS NULL ");
        } else {
            if (!contract.equals("")) {
                sb.append("\nAND dh.contract.contractNumber LIKE :contractNumber ");
            }
            if (reportType.equals("sales")) {
                sb.append("\nAND dh.contract IS NOT NULL ");
            }
        }

        if (!vendor.equals("")) {
            sb.append("\nAND dh.operator.loginId LIKE :vendor ");
        }

        if (!customerNo.equals("") || !customerName.equals("")) {
            if (!customerNo.equals("")) {
                sb.append("\nAND dh.customer.customerNo = :customerNo ");
            }

            if (!customerName.equals("")) {
                sb.append("\nAND dh.customer.name LIKE :customerName ");
            }           
        }

        if (!meterId.equals("")) {
            sb.append("\nAND dh.meter.mdsId = :meterId ");
        }

        if (!casherId.equals("") ) {
            sb.append("\nAND dh.prepaymentLog.vendorCasher.casherId = :casherId ");
        }

        if (!startDate.equals("")) {
            startDate = startDate + "000000";
            sb.append("\nAND dh.changeDate >= :startDate ");
        }

        if (!endDate.equals("")) {
            endDate = endDate + "235959";
            sb.append("\nAND dh.changeDate <= :endDate ");
        }

        sb.append("\nORDER BY dh.id DESC ");

        Query query = getSession().createQuery(sb.toString());

        if (!reportType.equals("deposit") && !contract.equals("")) {
            query.setString("contractNumber", "%" + contract + "%");
        }

        if (!vendor.equals("")) {
            query.setString("vendor", "%" + vendor + "%");
        }

        if (!customerNo.equals("") || !customerName.equals("")) {
            if (!customerNo.equals("")) {
                query.setString("customerNo", customerNo);
            }

            if (!customerName.equals("")) {
                query.setString("customerName", "%" + customerName + "%");
            }           
        }

        if (!meterId.equals("")) {
            query.setString("meterId", meterId);
        }

        if (!casherId.equals("") ) {
            query.setString("casherId", casherId);
        }

        if (!startDate.equals("")) {
            startDate = startDate + "000000";
            query.setString("startDate", startDate);
        }

        if (!endDate.equals("")) {
            endDate = endDate + "235959";
            query.setString("endDate", endDate);
        }

        result.put("count", query.list().size());

        if (page != null && limit != null) {
            query.setFirstResult((page - 1) * limit);
            query.setMaxResults(limit);
        }
        List<DepositHistory> list = query.list();
        result.put("list", list);

        return result;
    }
    
    @SuppressWarnings("unchecked")
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public Map<String, Object> getArrearsInfo(Map<String, Object> params) {
        Map<String, Object> result = new HashMap<String, Object>(); 
        String startDate = StringUtil.nullToBlank(params.get("startDate"));
        String endDate = StringUtil.nullToBlank(params.get("endDate"));
        Integer supplierId = (Integer) params.get("supplierId");
        // Vendor Prepayment Charge 의 Charge History 탭에서 사용.

        StringBuilder sbCommon = new StringBuilder();
        StringBuilder sbList = new StringBuilder();
        
        sbList.append("\nSELECT	data.CONTRACTNUMBER as CONTRACTNUMBER, ");
        sbList.append("\n		data.CONTRACTID as CONTRACTID, ");
        sbList.append("\n		p.ARREARS as ARREARS ");
        sbList.append("\nFROM PREPAYMENTLOG p, ");
        sbList.append("\n (SELECT	pl.contract_Id as CONTRACTID, ");
        sbList.append("\n        	dc.contract_Number as CONTRACTNUMBER, ");
        sbList.append("\n        	max(pl.lastTokenDate) as LASTTOKENDATE ");
        sbList.append("\n FROM DEPOSIT_HISTORY dh ");
        sbList.append("\n     LEFT OUTER JOIN ");
        sbList.append("\n     PREPAYMENTLOG pl ON (dh.prepaymentLog_id=pl.id) ");
        sbList.append("\n     LEFT OUTER JOIN ");
        sbList.append("\n     VENDOR_CASHER vc ON (pl.vendorCasher_id=vc.id) ");
        sbList.append("\n     LEFT OUTER JOIN ");
        sbList.append("\n     Operator ho ON (dh.operator_id=ho.id)  ");
        sbList.append("\n     LEFT OUTER JOIN ");
        sbList.append("\n     CONTRACT dc ON (dh.contract_id=dc.id)");
        sbList.append("\n     LEFT OUTER JOIN ");
        sbList.append("\n     CUSTOMER du ON (dh.customer_id=du.id) ");
        sbList.append("\n     LEFT OUTER JOIN ");
        sbList.append("\n     METER dm ON (dh.meter_id=dm.id) ");
        sbList.append("\n WHERE 1=1 ");
        
        sbCommon.append("\n AND 	pl.monthlyTotalAmount is null");
        sbCommon.append("\n AND  	pl.monthlyPaidAmount is null ");
        sbCommon.append("\n AND 	pl.monthlyServiceCharge is null ");
        sbCommon.append("\n AND (pl.IS_CANCELED IS NULL OR pl.IS_CANCELED = 0) ");    // cancel 제외
        
        if(supplierId != null) {
        	sbCommon.append("\n AND 	ho.supplier_Id = :supplierId ");
        }
        
        if(!startDate.isEmpty() && !endDate.isEmpty()) {
        	sbCommon.append("\n AND (pl.lastTokenDate between :startDate AND :endDate) ");
        }

        sbList.append(sbCommon);
    	sbList.append("\n GROUP BY pl.contract_Id, dc.contract_Number) data");
    	sbList.append("\n WHERE data.CONTRACTID = p.contract_id and data.LASTTOKENDATE = p.lasttokendate ");
    	sbList.append("\n AND p.arrears > 0 ");

        SQLQuery queryList = getSession().createSQLQuery(new SQLWrapper().getQuery(sbList.toString()));

        if(!startDate.isEmpty() && !endDate.isEmpty()) {
        	queryList.setString("startDate", startDate+"000000");
        	queryList.setString("endDate", endDate+"235959");
        }
        if(supplierId != null) {
        	queryList.setInteger("supplierId", supplierId);
        }

        result.put("list", queryList.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list());
    	
    	StringBuilder sbSumArrears = new StringBuilder();
    	sbSumArrears.append("\n SELECT 		SUM(p2.chargedArrears) as CHARGEDARREARSSUM");
    	sbSumArrears.append("\n FROM ( ");
    	sbSumArrears.append(sbList);
    	sbSumArrears.append(" ) data2, PREPAYMENTLOG p2 ");
    	sbSumArrears.append("\n WHERE 	data2.CONTRACTID=p2.contract_id");
    	sbSumArrears.append("\n AND p2.chargedArrears > 0");
    	sbSumArrears.append("\n AND (p2.IS_CANCELED IS NULL OR p2.IS_CANCELED = 0)  ");
    	sbSumArrears.append("\n AND (p2.lastTokenDate between :startDate AND :endDate )  ");
        SQLQuery querySUM = getSession().createSQLQuery(new SQLWrapper().getQuery(sbSumArrears.toString()));
        
        if(supplierId != null) {
        	querySUM.setInteger("supplierId", supplierId);
        }

        if(!startDate.isEmpty() && !endDate.isEmpty()) {
        	querySUM.setString("startDate", startDate+"000000");
        	querySUM.setString("endDate", endDate+"235959");
        }

        List<Object> sumData = querySUM.list();
        if(sumData.size() > 0) {
        	result.put("sum", sumData.get(0));
        } else {
        	result.put("sum", null);
        }
        
        return result;
    }
    
    @SuppressWarnings("unchecked")
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public Map<String, Object> getDebtInfo(Map<String, Object> params) {
        Map<String, Object> result = new HashMap<String, Object>(); 
        String startDate = StringUtil.nullToBlank(params.get("startDate"));
        String endDate = StringUtil.nullToBlank(params.get("endDate"));
        Integer supplierId = (Integer) params.get("supplierId");
        // Vendor Prepayment Charge 의 Charge History 탭에서 사용.

        StringBuilder sbCommon = new StringBuilder();
        StringBuilder sbList = new StringBuilder();
        
        sbList.append("\nSELECT info.CUSTOMERID as CUSTOMERID, info.CUSTOMERNUMBER as CUSTOMERNUMBER, SUM(info.DEBT) as DEBTSUM");
        sbList.append("\nFROM (");
        sbCommon.append("\n	SELECT	data.CUSTOMERID as CUSTOMERID, data.CUSTOMERNUMBER as CUSTOMERNUMBER, ");
        sbCommon.append("\n		d.debt as DEBT, d.chargedDebt as CHARGEDDEBT ");
        sbCommon.append("\n	FROM PREPAYMENTLOG p, DebtLog d,");
        sbCommon.append("\n 		(SELECT	pl.customer_Id as CUSTOMERID, ");
        sbCommon.append("\n 				du.CUSTOMERNO as CUSTOMERNUMBER, ");
        sbCommon.append("\n        	max(pl.lastTokenDate) as LASTTOKENDATE ");
        sbCommon.append("\n 		FROM DEPOSIT_HISTORY dh ");
        sbCommon.append("\n     	LEFT OUTER JOIN ");
        sbCommon.append("\n     	PREPAYMENTLOG pl ON (dh.prepaymentLog_id=pl.id) ");
        sbCommon.append("\n     	LEFT OUTER JOIN ");
        sbCommon.append("\n     	VENDOR_CASHER vc ON (pl.vendorCasher_id=vc.id) ");
        sbCommon.append("\n     	LEFT OUTER JOIN ");
        sbCommon.append("\n     	Operator ho ON (dh.operator_id=ho.id)  ");
        sbCommon.append("\n     	LEFT OUTER JOIN ");
        sbCommon.append("\n     	CONTRACT dc ON (dh.contract_id=dc.id)");
        sbCommon.append("\n     	LEFT OUTER JOIN ");
        sbCommon.append("\n     	CUSTOMER du ON (dh.customer_id=du.id) ");
        sbCommon.append("\n     	LEFT OUTER JOIN ");
        sbCommon.append("\n     	METER dm ON (dh.meter_id=dm.id) ");
        sbCommon.append("\n 	WHERE 1=1 ");
        
        sbCommon.append("\n AND 	pl.monthlyTotalAmount is null");
        sbCommon.append("\n AND  	pl.monthlyPaidAmount is null ");
        sbCommon.append("\n AND 	pl.monthlyServiceCharge is null ");
        sbCommon.append("\n AND (pl.IS_CANCELED IS NULL OR pl.IS_CANCELED = 0) ");    // cancel 제외
        
        if(supplierId != null) {
        	sbCommon.append("\n AND 	ho.supplier_Id = :supplierId ");
        }
        
        if(!startDate.isEmpty() && !endDate.isEmpty()) {
        	sbCommon.append("\n AND (pl.lastTokenDate between :startDate AND :endDate) ");
        }

        sbCommon.append("\n GROUP BY pl.customer_id, du.CUSTOMERNO ) data");
        sbCommon.append("\n WHERE p.id=d.prepaymentLog_id ");
        sbCommon.append("\n AND  ( data.CUSTOMERID = p.customer_id and data.LASTTOKENDATE = p.lasttokendate) ");
        sbCommon.append("\n AND d.debt > 0 ");
        sbList.append(sbCommon);
    	sbList.append("\n ) info GROUP BY CUSTOMERID, CUSTOMERNUMBER ");
    	
        SQLQuery queryList = getSession().createSQLQuery(new SQLWrapper().getQuery(sbList.toString()));

        if(!startDate.isEmpty() && !endDate.isEmpty()) {
        	queryList.setString("startDate", startDate+"000000");
        	queryList.setString("endDate", endDate+"235959");
        }
        if(supplierId != null) {
        	queryList.setInteger("supplierId", supplierId);
        }

        result.put("list", queryList.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list());
    	
    	StringBuilder sbSumDebts = new StringBuilder();
    	sbSumDebts.append("\n SELECT 		SUM(d2.chargedDebt) as CHARGEDDEBTSUM");
    	sbSumDebts.append("\n FROM ( ");
    	sbSumDebts.append(sbList);
    	sbSumDebts.append(" ) data2, PREPAYMENTLOG p2, DEBTLOG d2 ");
    	sbSumDebts.append("\n WHERE data2.CUSTOMERID=p2.customer_id ");
    	sbSumDebts.append("\n AND p2.id=d2.prepaymentlog_id ");
    	sbSumDebts.append("\n AND d2.chargedDebt > 0 ");
    	sbSumDebts.append("\n AND (p2.IS_CANCELED IS NULL OR p2.IS_CANCELED = 0)  ");
    	sbSumDebts.append("\n AND (p2.lastTokenDate between :startDate AND :endDate )  ");
        SQLQuery querySUM = getSession().createSQLQuery(new SQLWrapper().getQuery(sbSumDebts.toString()));
        
        if(supplierId != null) {
        	querySUM.setInteger("supplierId", supplierId);
        }

        if(!startDate.isEmpty() && !endDate.isEmpty()) {
        	querySUM.setString("startDate", startDate+"000000");
        	querySUM.setString("endDate", endDate+"235959");
        }

        List<Object> sumData = querySUM.list();
        if(sumData.size() > 0) {
        	result.put("sum", sumData.get(0));
        } else {
        	result.put("sum", null);
        }
        
        return result;
    }

    /**
     * method name : getDepositHistoryList<b/>
     * method Desc : Deposit History List 를 조회한다.
     *
     * @param params
     * @return
     */
    @SuppressWarnings("unchecked")
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public Map<String, Object> getDepositHistoryList(Map<String, Object> params) {
        Map<String, Object> result = new HashMap<String, Object>(); 
        Integer page = (Integer) params.get("page");
        Integer limit = (Integer) params.get("limit");
        String reportType = StringUtil.nullToBlank(params.get("reportType"));
        String subType = StringUtil.nullToBlank(params.get("subType"));
        String contract = StringUtil.nullToBlank(params.get("contract"));
        String vendor = StringUtil.nullToBlank(params.get("vendor"));
        String customerNo = StringUtil.nullToBlank(params.get("customerNo"));
        String customerName = StringUtil.nullToBlank(params.get("customerName"));
        String meterId = StringUtil.nullToBlank(params.get("meterId"));
        String casherId = StringUtil.nullToBlank(params.get("casherId"));
        String startDate = StringUtil.nullToBlank(params.get("startDate"));
        String endDate = StringUtil.nullToBlank(params.get("endDate"));
        Integer loginIntId = (Integer) params.get("loginIntId");
        Boolean onlyLoginData = (Boolean) params.get("onlyLoginData");
        Integer supplierId = (Integer) params.get("supplierId");
        // Vendor Prepayment Charge 의 Charge History 탭에서 사용.
        List<Integer> locationIdList = (List<Integer>)params.get("locationIdList");

        StringBuilder sb = new StringBuilder();
        StringBuilder sbList = new StringBuilder();
        StringBuilder sbCount = new StringBuilder();

        sbCount.append("\nSELECT  COUNT(*) AS cnt ");

        sbList.append("\nSELECT  dh.id AS depositHistoryId, ");
        sbList.append("\n        dh.changeDate AS changeDate, ");
        sbList.append("\n        dh.chargeCredit AS chargeCredit, ");
        sbList.append("\n        dh.chargeDeposit AS chargeDeposit, ");
        sbList.append("\n        dh.commission AS commission, ");
        sbList.append("\n        dh.tax AS tax, ");
        sbList.append("\n        dh.netValue AS netValue, ");
        sbList.append("\n        dh.deposit AS deposit, ");
        sbList.append("\n        dh.isCanceled AS isCanceledByDeposit, ");
        sbList.append("\n        ho.id AS historyOpId, ");
        sbList.append("\n        ho.name AS historyOpName, ");
        sbList.append("\n        ho.loginId AS historyOpLoginId, ");
        sbList.append("\n        dc.id AS historyContractId, ");
        sbList.append("\n        dc.contractNumber AS historyContractNumber, ");
        sbList.append("\n        du.id AS historyCustomerId, ");
        sbList.append("\n        du.name AS historyCustomerName, ");
        sbList.append("\n        du.customerNo AS historyCustomerNo, ");
        sbList.append("\n        du.address AS historyCustomerAddress, ");
        sbList.append("\n        du.address2 AS historyCustomerAddress2, ");
        sbList.append("\n        dm.id AS historyMeterId, ");
        sbList.append("\n        dm.mdsId AS historyMeterMdsId, ");
        sbList.append("\n        pl.id AS prepaymentLogId, ");
        sbList.append("\n        pl.chargedCredit AS chargedCredit, ");
        sbList.append("\n        plpt.name as payType, ");
        sbList.append("\n        pl.chargedArrears AS chargedArrears, ");
        sbList.append("\n        pl.isCanceled AS isCanceled, ");
        sbList.append("\n        pl.cancelDate AS cancelDate, ");
        sbList.append("\n        pl.cancelReason AS cancelReason, ");
        sbList.append("\n        pl.lastTokenId AS lastTokenId, ");
        sbList.append("\n        vc.id AS vendorCasherId, ");
        sbList.append("\n        vc.name AS vendorCasherName, ");
        sbList.append("\n        vc.casherId AS vcCasherId, ");
        sbList.append("\n        op.id AS vendingStationId, ");
        sbList.append("\n        op.name AS vendingStationName, ");
        sbList.append("\n        co.id AS contractId, ");
        sbList.append("\n        co.contractNumber AS geoCode, ");
        sbList.append("\n        me.id AS meterId, ");
        sbList.append("\n        me.mdsId AS mdsId, ");
        sbList.append("\n        ti.id AS tariffId, ");
        sbList.append("\n        ti.name AS tariffName, ");
        sbList.append("\n        cu.id AS customerId, ");
        sbList.append("\n        cu.name AS customerName, ");
        sbList.append("\n        cu.customerNo AS customerNo, ");
        sbList.append("\n        cu.address AS address, ");
        sbList.append("\n        cu.address1 AS address1, ");
        sbList.append("\n        cu.address2 AS address2 ");

        sb.append("\nFROM DepositHistory dh ");
        sb.append("\n     LEFT OUTER JOIN ");
        sb.append("\n     dh.prepaymentLog pl ");
        sb.append("\n     LEFT OUTER JOIN ");
        sb.append("\n     dh.operator ho ");
        sb.append("\n     LEFT OUTER JOIN ");
        sb.append("\n     dh.contract dc ");
        sb.append("\n     LEFT OUTER JOIN ");
        sb.append("\n     dh.customer du ");
        sb.append("\n     LEFT OUTER JOIN ");
        sb.append("\n     dh.meter dm ");
        sb.append("\n     LEFT OUTER JOIN ");
        sb.append("\n     pl.vendorCasher vc ");
        sb.append("\n     LEFT OUTER JOIN ");
        sb.append("\n     pl.operator op ");
        sb.append("\n     LEFT OUTER JOIN ");
        sb.append("\n     pl.contract co ");
        sb.append("\n     LEFT OUTER JOIN ");
        sb.append("\n     pl.payType plpt ");
        sb.append("\n     LEFT OUTER JOIN ");
        sb.append("\n     co.meter me ");
        sb.append("\n     LEFT OUTER JOIN ");
        sb.append("\n     co.tariffIndex ti ");
        sb.append("\n     LEFT OUTER JOIN ");
        sb.append("\n     co.customer cu ");
        sb.append("\nWHERE 1=1 ");
        if(supplierId != null) {
        	sb.append("\nAND ho.supplierId = :supplierId");
        }
        try{
        //Sales Report
        if(reportType.equals("sales")) {
        	//canceled OR nonCanceled
        	if("cancelled".equals(subType)) {
        		sb.append("\nAND (pl.isCanceled = 1) ");    // cancel 검색
        	} else if("unCancelled".equals(subType)) {
        		sb.append("\nAND (pl.isCanceled IS NULL OR pl.isCanceled = 0) ");    // cancel 제외
        	}

        	//sales common
            if (!"".equals(contract)) {
                sb.append("\nAND dh.contract.contractNumber LIKE :contractNumber ");
            }
            
        	sb.append("\nAND dh.contract IS NOT NULL ");
            if (onlyLoginData && loginIntId != null) {
            	sb.append("\nAND pl.operator.id = :loginIntId ");
            }

            if (locationIdList != null && locationIdList.size() > 0) {
                sb.append("\nAND dh.contract.location.id IN (:locationIdList) ");
            }
          //Deposit Report
        } else if("deposit".equals(reportType)) {
        	//deposit common
        	sb.append("\nAND dh.prepaymentLog IS NULL ");    //deposit 판별
        	sb.append("\nAND dh.contract IS NULL ");
        	
        	//canceled OR nonCanceled
        	if("cancelled".equals(subType)) {
        		sb.append("\nAND dh.isCanceled = 1 ");
        	} else if("unCancelled".equals(subType)) {
        		sb.append("\nAND (dh.isCanceled IS NULL OR dh.isCanceled = 0) ");
        	}

        	//deposit common        	
        	if (onlyLoginData && loginIntId != null) {
            	sb.append("\nAND dh.loginUser.id = :loginIntId ");
            }
        //All(Sales + Deposit)
        } else {
            if("cancelled".equals(subType)) {
                sb.append("\nAND (pl.isCanceled = 1 OR dh.isCanceled = 1) ");    // cancel 검색
            } else if("unCancelled".equals(subType)) {
                sb.append("\nAND ((pl.isCanceled IS NULL OR pl.isCanceled = 0) ");    // cancel 제외    
                sb.append("\nAND (dh.isCanceled IS NULL OR dh.isCanceled = 0)) ");
            }
        	
            if (!"".equals(contract)) {
                sb.append("\nAND dh.contract.contractNumber LIKE :contractNumber ");
            }
        	if (onlyLoginData && loginIntId != null) {
            	sb.append("\nAND (dh.loginUser.id = :loginIntId OR pl.operator.id = :loginIntId) ");
            }
            if (locationIdList != null && locationIdList.size() > 0) {
                sb.append("\nAND dh.contract.location.id IN (:locationIdList) ");
            }
        }

        if (!"".equals(vendor)) {
            sb.append("\nAND dh.operator.loginId = :vendor ");
        }

        if (!"".equals(customerNo)) {
            sb.append("\nAND du.customerNo LIKE :customerNo ");
        }

        if (!"".equals(customerName)) {
            sb.append("\nAND du.name LIKE :customerName ");
        }

        if (!"".equals(meterId)) {
            sb.append("\nAND dm.mdsId LIKE :meterId ");
        }

        if (!"".equals(casherId) ) {
            sb.append("\nAND vc.casherId LIKE :casherId ");
        }

        if (!"".equals(startDate)) {
            startDate = startDate + "000000";
            sb.append("\nAND dh.changeDate >= :startDate ");
        }

        if (!"".equals(endDate)) {
            endDate = endDate + "235959";
            sb.append("\nAND dh.changeDate <= :endDate ");
        }

        sbCount.append(sb);
        sbList.append(sb);

        sbList.append("\nORDER BY dh.id DESC ");

        Query queryCount = getSession().createQuery(sbCount.toString());
        Query queryList = getSession().createQuery(sbList.toString());
        if(supplierId != null) {
        	queryCount.setInteger("supplierId", supplierId);
        	queryList.setInteger("supplierId", supplierId);
        }
        
        if (!reportType.equals("deposit")) {
            if (!contract.equals("")) {
                queryCount.setString("contractNumber", contract + "%");
                queryList.setString("contractNumber", contract + "%");
            }

            if (locationIdList != null && locationIdList.size() > 0) {
                queryCount.setParameterList("locationIdList", locationIdList);
                queryList.setParameterList("locationIdList", locationIdList);
            }
        }

        if (onlyLoginData && loginIntId != null) {
        	queryCount.setInteger("loginIntId",loginIntId);
        	queryList.setInteger("loginIntId", loginIntId);
        }

        if (!"".equals(vendor)) {
            queryCount.setString("vendor", vendor);
            queryList.setString("vendor", vendor);
        }

        if (!"".equals(customerNo)) {
            queryCount.setString("customerNo", customerNo + "%");
            queryList.setString("customerNo", customerNo + "%");
        }

        if (!"".equals(customerName)) {
            queryCount.setString("customerName", "%" + customerName + "%");
            queryList.setString("customerName", "%" + customerName + "%");
        }

        if (!"".equals(meterId)) {
            queryCount.setString("meterId", meterId + "%");
            queryList.setString("meterId", meterId + "%");
        }

        if (!"".equals(casherId)) {
            queryCount.setString("casherId", casherId + "%");
            queryList.setString("casherId", casherId + "%");
        }

        if (!"".equals(startDate)) {
            startDate = startDate + "000000";
            queryCount.setString("startDate", startDate);
            queryList.setString("startDate", startDate);
        }

        if (!"".equals(endDate)) {
            endDate = endDate + "235959";
            queryCount.setString("endDate", endDate);
            queryList.setString("endDate", endDate);
        }

        result.put("count", ((Number)queryCount.uniqueResult()).intValue());

        if (page != null && limit != null) {
            queryList.setFirstResult((page - 1) * limit);
            queryList.setMaxResults(limit);
        }

        List<Map<String, Object>> list = queryList.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        result.put("list", list);

        }catch (Exception e) {
			logger.error(e,e);
		}
        return result;
    }
    
    @Override
	public Integer getRecentDepositId(String vendor) {
    	StringBuffer sb = new StringBuffer();
    	sb.append("\n SELECT max(dh.id) ");
    	sb.append("\n FROM DepositHistory dh ");
    	sb.append("\n		LEFT OUTER JOIN dh.operator o ");
    	sb.append("\n WHERE ");
    	sb.append("\n 		o.loginId = :vendorId");
    	sb.append("\n 		AND ( dh.isCanceled is null  OR dh.isCanceled = false ) "); // 취소된 결제내역 제외
    	sb.append("\n 		AND ( dh.prepaymentLog.id is null ) "); // sales 내역제외.
    	Query query = getSession().createQuery(sb.toString());
    	query.setString("vendorId", vendor);
    	return (Integer) query.uniqueResult();
    }
    
    /**
     * method name : deleteByPrepaymentLogId<b/>
     * method Desc : prepaymentLogId를 가지로 삭제한다.
     * 
     * @param pId
     * @return
     */
    @Override
    public void deleteByPrepaymentLogId(long pId) {
        StringBuffer queryBuffer = new StringBuffer();
        queryBuffer.append("DELETE DepositHistory where prepaymentLogId = ? ");
        
        Query query = getSession().createQuery(queryBuffer.toString());
        query.setParameter(1, pId);
        query.executeUpdate();
        // bulkUpdate 때문에 주석처리
        /*this.getHibernateTemplate().bulkUpdate(query.toString(), pId );*/
    }
    
}