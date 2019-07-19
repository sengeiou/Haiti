package com.aimir.dao.system.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.system.PrepaymentLogDao;
import com.aimir.model.system.Contract;
import com.aimir.model.system.PrepaymentLog;
import com.aimir.util.CalendarUtil;
import com.aimir.util.Condition;
import com.aimir.util.SQLWrapper;
import com.aimir.util.StringUtil;

@Repository(value = "prepaymentlogDao")
public class PrepaymentLogDaoImpl  extends AbstractHibernateGenericDao< PrepaymentLog, Long> implements PrepaymentLogDao {
	private static Log logger = LogFactory.getLog(PrepaymentLogDaoImpl.class);
	
	@Autowired
	protected PrepaymentLogDaoImpl(SessionFactory sessionFactory) {
		super(PrepaymentLog.class);
		super.setSessionFactory(sessionFactory);
	}

	@Override
	public List<PrepaymentLog> getPrepaymentLogByListCondition(
			Set<Condition> set) {
		return findByConditions(set);
	}
	@Override
	public List<Object> getPrepaymentLogCountByListCondition(Set<Condition> set) {
		return findTotalCountByConditions(set);
	}

    /**
     * method name : getPrepaymentContractBalanceInfo
     * method Desc : 선불 웹서비스에서 잔액 충전 내역을 조회한다.
     *
     * @return
     */
    @Override
	@SuppressWarnings("unchecked")
    public List<Map<String, Object>> getPrepaymentChargeHistoryList(Map<String, Object> conditionMap) {
        String contractNumber = (String)conditionMap.get("contractNumber");
        StringBuilder sb = new StringBuilder();

        sb.append("\nSELECT c.supplier.name AS supplierName, ");
        sb.append("\n       c.contractNumber AS contractNumber, ");
        sb.append("\n       c.meter.mdsId AS mdsId, ");
        sb.append("\n       c.currentCredit AS currentCredit, ");
        sb.append("\n       c.emergencyCreditAvailable AS emergencyYn, ");
        sb.append("\n       c.creditStatus.code AS creditStatus, ");
        // 미터기 차단여부(0:차단, 1:개방, 2:대기)
//        // EnergyMeter
//        sb.append("\n       CASE WHEN c.meter.class = 'EnergyMeter' AND (c.meter.switchStatus = 1 OR c.meter.switchStatus IS NULL) ");
//        sb.append("\n                 THEN '1' ");
//        // GasMeter
//        sb.append("\n            WHEN c.meter.class = 'GasMeter' AND (c.meter.valveStatus = 0 OR c.meter.valveStatus IS NULL) ");
//        sb.append("\n                 THEN '1' ");
//        // WaterMeter
//        sb.append("\n            WHEN c.meter.class = 'WaterMeter' AND (c.meter.valveStatus = 0 OR c.meter.valveStatus IS NULL) ");
//        sb.append("\n                 THEN '1' ");

        // 수정 - 2011.11.25 : GasMeter/WaterMeter 차단여부 체크컬럼 변경.
        // 미터기 차단여부(0:차단, 1:개방, 2:대기)
        // EnergyMeter
        sb.append("\n       CASE WHEN c.meter.class = 'EnergyMeter' AND (c.meter.switchStatus = 1 OR c.meter.switchStatus IS NULL) ");
        sb.append("\n                 THEN 1 ");
        // GasMeter
        sb.append("\n            WHEN c.meter.class = 'GasMeter' ");
        sb.append("\n                 THEN CASE WHEN c.meter.meterStatus.code = '1.3.1.3.1.0' OR c.meter.meterStatus.code = '1.3.1.3.1.1' ");
        sb.append("\n                                THEN 1 ");
        sb.append("\n                           WHEN c.meter.meterStatus.code = '1.3.1.3.1.3' ");
        sb.append("\n                                THEN 2 ");
        sb.append("\n                      ELSE 0 END ");
        // WaterMeter
        sb.append("\n            WHEN c.meter.class = 'WaterMeter' ");
        sb.append("\n                 THEN CASE WHEN c.meter.meterStatus.code = '1.3.1.2.1.0' OR c.meter.meterStatus.code = '1.3.1.2.1.1' ");
        sb.append("\n                                THEN 1 ");
        sb.append("\n                           WHEN c.meter.meterStatus.code = '1.3.1.2.1.3' ");
        sb.append("\n                                THEN 2 ");
        sb.append("\n                      ELSE 0 END ");
        sb.append("\n       ELSE 0 END AS switchStatus, ");
        sb.append("\n       p.lastTokenDate AS lastTokenDate, ");
        sb.append("\n       p.lastTokenId AS lastTokenId, ");
        sb.append("\n       p.chargedCredit AS chargedCredit, ");
        sb.append("\n       p.powerLimit AS powerLimit, ");
        sb.append("\n       K.code AS keyType ");   // Code class
        sb.append("\nFROM PrepaymentLog p ");
        sb.append("\n     LEFT OUTER JOIN p.keyType K, ");
        sb.append("\n     Contract c ");
        sb.append("\nWHERE c.contractNumber = :contractNumber ");
        sb.append("\nORDER BY p.lastTokenDate DESC ");

        Query query = getSession().createQuery(sb.toString());

        query.setString("contractNumber", contractNumber);
        query.setFirstResult(0);
        query.setMaxResults(5);
        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

    /**
     * method name : getChargeInfo
     * method Desc : 고객 선불관리 화면의 충전 정보를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @Override
	@SuppressWarnings("unchecked")
    public List<Map<String, Object>> getChargeInfo(Map<String, Object> conditionMap) {

        String contractNumber = (String)conditionMap.get("contractNumber");
        String serviceType = (String)conditionMap.get("serviceType");

        StringBuilder sb = new StringBuilder();

        sb.append("\nSELECT p.lastTokenDate AS lastTokenDate, ");
        sb.append("\n       p.balance AS balance, ");
        sb.append("\n       p.contract.chargedCredit AS chargedCredit, ");
        sb.append("\n       p.contract.currentCredit AS currentCredit, ");
        sb.append("\n       p.contract.emergencyCreditAutoChange AS emergencyCreditAutoChange, ");
        sb.append("\n       p.contract.creditType.code AS creditType, ");
        sb.append("\n       p.contract.emergencyCreditStartTime AS emergencyCreditStartTime, ");
        sb.append("\n       p.contract.emergencyCreditMaxDuration AS emergencyCreditMaxDuration ");
        sb.append("\nFROM PrepaymentLog p ");
        sb.append("\nWHERE p.contract.contractNumber = :contractNumber ");
        sb.append("\nAND   p.contract.serviceTypeCode.code = :serviceType ");
        sb.append("\nORDER BY p.lastTokenDate DESC ");

        Query query = getSession().createQuery(sb.toString());

        query.setString("contractNumber", contractNumber);
        query.setString("serviceType", serviceType);
        query.setFirstResult(0);
        query.setMaxResults(1);

        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

    /**
     * method name : getChargeHistory
     * method Desc : 고객 선불관리 화면의 충전 이력 리스트를 조회한다.
     *
     * @param conditionMap
     * @param isTotal total count 여부
     * @return
     */
    @Override
	@SuppressWarnings("unchecked")
    public List<Map<String, Object>> getChargeHistory(Map<String, Object> conditionMap, boolean isCount) {

        List<Map<String, Object>> result;
        Map<String, Object> map;
        String contractNumber = (String)conditionMap.get("contractNumber");

        String searchStartDate = (String)conditionMap.get("searchStartMonth");
        String searchEndDate = (String)conditionMap.get("searchEndMonth");

        if(searchStartDate.length() <= 6){  //Prepayment Customer Management 에서 사용.
        	searchStartDate += "01000000";
        	searchEndDate += "31235959";
        }

        Integer page = (Integer)conditionMap.get("page");
        Integer limit = (Integer)conditionMap.get("limit");
        Boolean isExcel = (Boolean) (conditionMap.get("isExcel") == null ? false : conditionMap.get("isExcel"));

        StringBuilder sb = new StringBuilder();

        if (isCount) {
            sb.append("\nSELECT COUNT(*) AS cnt ");
        } else {
            sb.append("\nSELECT p.lastTokenDate AS lastTokenDate, ");
            sb.append("\n       p.balance AS balance, ");
            sb.append("\n       p.chargedCredit AS chargedCredit, ");
            sb.append("\n       p.usedCost AS usedCost, ");
            sb.append("\n       p.usedConsumption AS usedConsumption, ");
            sb.append("\n       c.currentCredit AS currentCredit, ");
            sb.append("\n       c.keyNum AS keyNum, ");
            sb.append("\n       d.name AS payment, ");
            sb.append("\n       p.lastTokenId AS lastTokenId, ");
            sb.append("\n       p.authCode AS authCode, ");
            sb.append("\n       e.descr AS municipalityCode, ");
            sb.append("\n       p.activeEnergyImport as activeImport, ");
            sb.append("\n       p.activeEnergyExport as activeExport ");
        }

        sb.append("\nFROM PrepaymentLog p ");
        sb.append("\n     LEFT OUTER JOIN p.municipalityCode e ");
        sb.append("\n     INNER JOIN p.contract c ");
        sb.append("\n     LEFT OUTER JOIN c.keyType d ");
        sb.append("\nWHERE c.contractNumber = :contractNumber ");
        sb.append("\nAND   p.lastTokenDate BETWEEN :searchStartDate AND :searchEndDate ");

        // Prepayment Customer Management 에서 사용.
        if(!(Boolean) conditionMap.get("allFlag")){
            sb.append("\nAND (  ");
            sb.append("\n     (p.usedCost IS NOT NULL AND 0 <> p.usedCost)  ");
            sb.append("\n  OR (p.usedConsumption IS NOT NULL AND 0 <> p.usedConsumption)    ");
            sb.append("\n  OR (p.chargedCredit IS NOT NULL AND 0 <> p.chargedCredit)    ");
            sb.append("\n)  ");
        }

        if (!isCount) {
            sb.append("\nORDER BY p.lastTokenDate DESC ");
        }

        Query query = getSession().createQuery(sb.toString());

        query.setString("contractNumber", contractNumber);
        query.setString("searchStartDate", searchStartDate);
        query.setString("searchEndDate", searchEndDate);

        if (isCount) {
            map = new HashMap<String, Object>();
            map.put("total", query.uniqueResult());
            result = new ArrayList<Map<String, Object>>();
            result.add(map);
        } else if (isExcel) {
        	result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        } else {
            query.setFirstResult((page - 1) * limit);
            query.setMaxResults(limit + 1);     // 사용량 계산위해 한 row 더 가져옴
            result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        }
        return result;
    }

    /**
     * @MethodName getAddBalanceList
     * @Date 2014. 4. 22.
     * @param page
     * @param limit
     * @param searchDate
     * @return
     * @Modified
     * @Description 특정 날짜에 대한 전체 선불 구매 내역 조회
     */
    @Override
	public Map<String, Object> getAddBalanceList(Integer page, Integer limit, String searchDate, String vendorId) {
    	Map<String, Object> result = new HashMap<String, Object>();
    	StringBuilder sb = new StringBuilder();

    	/*
    	 * select o.name, o.id , v.casherId  
			from operator o, role r , VENDOR_CASHER v
			where r.name='vendor' and o.role_id = r.id
			and v.vendor_id = o.id; 
    	 */

        sb.append("\nSELECT p.lastTokenDate AS lastTokenDate, ");
        sb.append("\n       p.balance AS balance, ");
        sb.append("\n       p.arrears AS arrears, ");
        sb.append("\n       p.chargedCredit AS chargedCredit, ");
        sb.append("\n		p.chargedArrears AS chargedArrears, ");
        sb.append("\n       p.usedCost AS usedCost, ");
        sb.append("\n       p.usedConsumption AS usedConsumption, ");
        sb.append("\n       p.contract.currentCredit AS currentCredit, ");
        sb.append("\n       p.contract.keyNum AS keyNum, ");
        sb.append("\n       d.descr AS payment, ");
        sb.append("\n       p.lastTokenId AS lastTokenId, ");
        sb.append("\n       p.authCode AS authCode, ");
        sb.append("\n       e.descr AS municipalityCode, ");
        sb.append("\n		c.contractNumber AS contractNumber, ");
        sb.append("\n		cu.name AS customerName, ");
        sb.append("\n		m.mdsId AS mdsId, ");
        sb.append("\n		p.descr AS transactionNumber, ");
        // add by eunmiae 3 Feb 2015
        sb.append("\n		v.name AS cashierName, ");
        sb.append("\n		o.name AS vendorName ");
        sb.append("\nFROM PrepaymentLog p ");
        sb.append("\n     LEFT OUTER JOIN p.contract.keyType d ");
        sb.append("\n     LEFT OUTER JOIN p.municipalityCode e ");
        sb.append("\n		LEFT OUTER JOIN p.contract c ");
        sb.append("\n		LEFT OUTER JOIN p.customer cu ");
        sb.append("\n		LEFT OUTER JOIN c.meter m ");
        // add by eunmiae 3 Feb 2015
        sb.append("\n		LEFT OUTER JOIN p.vendorCasher v ");
        sb.append("\n		LEFT OUTER JOIN p.operator o ");

        sb.append("\nWHERE   p.monthlyTotalAmount is NULL ");
        sb.append("\nAND   p.lastTokenDate like :searchDate  ");

        sb.append("\nAND  ( p.chargedCredit > 0 ");
        sb.append("\nOR   p.chargedArrears > 0 )");
        
        // vendor 조건이 있을 경우
        if(vendorId != null && !vendorId.isEmpty()){
        	sb.append("\nAND   o.loginId = '").append(vendorId).append("' ");
        }

    	Query query = getSession().createQuery(sb.toString());

    	query.setString("searchDate", searchDate+"%");
    	
    	Integer size = query.list().size();
    	result.put("size", size);

    	if ( page != null && limit != null ) {
    		query.setFirstResult((page - 1) * limit);
    		query.setMaxResults(limit );
    	}
    	result.put("data", query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list());
    	return result;
    }

    /* (non-Javadoc)
     * @see com.aimir.dao.system.PrepaymentLogDao#getChargeHistoryForCustomer(java.util.Map, boolean)
     */
    @Override
	@SuppressWarnings("unchecked")
    public List<Map<String, Object>> getChargeHistoryForCustomer(Map<String, Object> conditionMap, boolean isCount) {

        List<Map<String, Object>> result;
        Map<String, Object> map;
        String contractNumber = (String)conditionMap.get("contractNumber");
        String searchStartDate = (String)conditionMap.get("searchStartMonth") + "01000000";
        String searchEndDate = (String)conditionMap.get("searchEndMonth") + "31235959";
        Integer page = (Integer)conditionMap.get("page");
        Integer limit = (Integer)conditionMap.get("limit");

        StringBuilder sb = new StringBuilder();

        if (isCount) {
            sb.append("\nSELECT COUNT(*) AS cnt ");
        } else {
            sb.append("\nSELECT p.lastTokenDate AS lastTokenDate, ");
            sb.append("\n       p.balance AS balance, ");
            sb.append("\n       p.chargedCredit AS chargedCredit, ");
            sb.append("\n       p.contract.currentCredit AS currentCredit, ");
            sb.append("\n       p.contract.keyNum AS keyNum, ");
            sb.append("\n       d.name AS payment, ");
            sb.append("\n       p.lastTokenId AS lastTokenId ");
        }
        sb.append("\nFROM PrepaymentLog p ");
        sb.append("\n     LEFT OUTER JOIN p.contract.keyType d ");
        sb.append("\nWHERE p.contract.contractNumber = :contractNumber ");
        sb.append("\nAND   p.lastTokenDate BETWEEN :searchStartDate AND :searchEndDate ");
        if (!isCount) {
            sb.append("\nORDER BY p.lastTokenDate DESC ");
        }

        Query query = getSession().createQuery(sb.toString());

        query.setString("contractNumber", contractNumber);
        query.setString("searchStartDate", searchStartDate);
        query.setString("searchEndDate", searchEndDate);

        if (isCount) {
            map = new HashMap<String, Object>();
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

    /**
     * method name : getChargeHistoryByMaxUnderDate
     * method Desc : 고객 선불관리 화면의 충전 이력 리스트 이전 데이터를 조회한다.(계산용)
     *
     * @param conditionMap
     * @return
     */
    @Override
	@SuppressWarnings("unchecked")
    public List<Map<String, Object>> getChargeHistoryByMaxUnderDate(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result;
        String contractNumber = (String)conditionMap.get("contractNumber");
        Boolean isSubstraction = Boolean.parseBoolean(conditionMap.get("isSubstraction") == null ? "false" : conditionMap.get("isSubstraction").toString());
        String searchStartDate = null;

        if(isSubstraction) {
        	searchStartDate = (String)conditionMap.get("searchStartMonth");
        } else {
        	searchStartDate = (String)conditionMap.get("searchStartMonth") + "01000000";
        }


        StringBuilder sb = new StringBuilder();

        sb.append("\nSELECT p.lastTokenDate AS lastTokenDate, ");
        sb.append("\n       p.balance AS balance, ");
        sb.append("\n       p.chargedCredit AS chargedCredit, ");
        sb.append("\n       p.contract.currentCredit AS currentCredit, ");
        if(isSubstraction) {
        	sb.append("\n       p.contract.id AS contractId, ");
        	sb.append("\n       p.usedCost AS usedCost, ");
        	sb.append("\n       p.usedConsumption AS usedConsumption ");
        } else {
        	sb.append("\n       p.contract.id AS contractId ");
        }
        sb.append("\nFROM PrepaymentLog p ");
        sb.append("\nWHERE p.contract.contractNumber = :contractNumber ");
        sb.append("\nAND   p.lastTokenDate = (SELECT MAX(p2.lastTokenDate) ");
        sb.append("\n                         FROM PrepaymentLog p2 ");
        sb.append("\n                         WHERE p2.contract.contractNumber = :contractNumber ");
        sb.append("\n                         AND   p2.lastTokenDate < :searchStartDate ");
        if(isSubstraction) {
        	sb.append("\n                     AND   p2.usedCost is not null 		");
        	sb.append("\n                     AND   p2.usedConsumption is not null  ");
        } else {

        }
        sb.append("\n                        ) ");

        Query query = getSession().createQuery(sb.toString());
        query.setString("contractNumber", contractNumber);
        query.setString("searchStartDate", searchStartDate);

        result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        return result;
    }

    /**
     * method name : getChargeHistoryByLastTokenDate
     * method Desc : IHD Data관련, 계약번호와 마지막 충전 날짜로 선불데이터 조회
     *
     * @param conditionMap(contractNumber, lastTokenDate)
     * @return
     */
    @Override
	@SuppressWarnings("unchecked")
    public List<Map<String, Object>> getChargeHistoryByLastTokenDate(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result;
        String contractNumber = (String)conditionMap.get("contractNumber");
        String lastTokenDate = (String)conditionMap.get("lastTokenDate");

        StringBuilder sb = new StringBuilder();

        sb.append("\nSELECT p.lastTokenDate AS lastTokenDate, ");
        sb.append("\n       p.balance AS balance, ");
        sb.append("\n       p.chargedCredit AS chargedCredit, ");
        sb.append("\n       p.contract.currentCredit AS currentCredit, ");
        sb.append("\n       p.contract.id AS contractId ");
        sb.append("\nFROM PrepaymentLog p ");
        sb.append("\nWHERE p.contract.contractNumber = :contractNumber ");
        sb.append("\nAND   p.lastTokenDate =  :lastTokenDate");

        Query query = getSession().createQuery(sb.toString());
        query.setString("contractNumber", contractNumber);
        query.setString("lastTokenDate", lastTokenDate);

        result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        return result;
    }

    /**
     * method name : getChargeHistoryList
     * method Desc : Prepayment Charge 가젯에서 충전 이력 리스트를 조회한다.
     *
     * @param conditionMap
     * @param isCount total count 여부
     * @return
     */
    @Override
	@SuppressWarnings("unchecked")
    public List<Map<String, Object>> getChargeHistoryList(Map<String, Object> conditionMap, boolean isCount) {
        List<Map<String, Object>> result;
        Map<String, Object> map;
        String contractNumber = (String)conditionMap.get("contractNumber");
        String searchStartMonth = (String) conditionMap.get("searchStartMonth");
        String searchEndMonth = (String) conditionMap.get("searchEndMonth");
        String searchStartDate;
        String searchEndDate;

        if (searchStartMonth.length() == 6) {
        	searchStartDate = searchStartMonth + "01000000";
        } else if(searchStartMonth.length() == 8){
        	searchStartDate = searchStartMonth + "000000";
        } else {
        	searchStartDate = searchStartMonth;
        }
        if (searchEndMonth.length() == 6) {
        	searchEndDate = searchEndMonth + "31235959";
        } else if(searchStartMonth.length() ==8){
        	searchEndDate = searchEndMonth + "235959";
        } else {
        	searchEndDate = CalendarUtil.getCurrentDate() + "235959";
        }

        Integer page = (Integer)conditionMap.get("page");
        Integer limit = (Integer)conditionMap.get("limit");

        StringBuilder sb = new StringBuilder();

        if (isCount) {
            sb.append("\nSELECT COUNT(*) AS cnt ");
        } else {
            sb.append("\nSELECT p.lastTokenDate AS lastTokenDate, ");
            sb.append("\n       p.id AS prepaymentLogId, ");
            sb.append("\n       p.balance AS balance, ");
            sb.append("\n       p.arrears AS arrears, ");
            sb.append("\n       p.partpayInfo AS partpayInfo, ");
            sb.append("\n       p.isCanceled AS isCanceled, ");
            sb.append("\n       p.chargedCredit AS chargedCredit, ");
            sb.append("\n       p.chargedArrears AS chargedArrears, ");
            sb.append("\n       p.usedCost AS usedCost, ");
            sb.append("\n       p.usedConsumption AS usedConsumption, ");
            sb.append("\n       p.contract.currentCredit AS currentCredit, ");
            sb.append("\n       p.contract.firstArrears AS firstArrears, ");
            sb.append("\n       p.contract.arrearsContractCount AS arrearsContractCount, ");
            sb.append("\n       p.contract.arrearsPaymentCount AS arrearsPaymentCount, ");
            sb.append("\n       p.contract.keyNum AS keyNum, ");
            sb.append("\n       m.mdsId AS mdsId, ");
            sb.append("\n       m.id AS meterId, ");
//            sb.append("\n       d.code AS keyTypeCode ");
            sb.append("\n       d.descr AS payment, ");
            sb.append("\n       p.lastTokenId AS lastTokenId, ");
            sb.append("\n       p.authCode AS authCode, ");
            sb.append("\n       e.descr AS municipalityCode, ");
            sb.append("\n       p.contract.id AS contractId, ");
            sb.append("\n       p.id AS prepaymentLogId, ");
            sb.append("\n       pc.name as payType ");
        }

        sb.append("\nFROM PrepaymentLog p ");
        sb.append("\n     LEFT OUTER JOIN p.contract.keyType d ");
        sb.append("\n     LEFT OUTER JOIN p.contract.meter m ");
        sb.append("\n     LEFT OUTER JOIN p.payType pc ");
        sb.append("\n     LEFT OUTER JOIN p.municipalityCode e ");
        sb.append("\n     INNER JOIN p.operator o ");
        sb.append("\nWHERE p.contract.contractNumber = :contractNumber ");

        // 월정산 데이터는 조회 안함.
        sb.append("\nAND   p.monthlyPaidAmount IS NULL ");
        sb.append("\nAND   p.monthlyTotalAmount IS NULL ");

        if (searchStartMonth != null && searchEndMonth != null) {
        	sb.append("\nAND   p.lastTokenDate BETWEEN :searchStartDate AND :searchEndDate ");
        }

        if (!isCount) {
            sb.append("\nORDER BY p.lastTokenDate DESC ");
        }

        Query query = getSession().createQuery(sb.toString());

        query.setString("contractNumber", contractNumber);
        query.setString("searchStartDate", searchStartDate);
        query.setString("searchEndDate", searchEndDate);

        if (isCount) {
            map = new HashMap<String, Object>();
            map.put("total", ((Number)query.uniqueResult()).intValue());
            result = new ArrayList<Map<String, Object>>();
            result.add(map);
        } else {
            query.setFirstResult((page - 1) * limit);
            query.setMaxResults(limit);
            result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        }
        return result;
    }

    /**
     * @MethodName getMonthlyPaidAmount
     * @Date 2013. 10. 28.
     * @param contract
     * @param yyyymm
     * @return
     * @Modified
     * @Description 월간 사용료로 결제된 결제된 금액
     */
    @Override
	@Transactional(propagation=Propagation.REQUIRES_NEW)
    @SuppressWarnings("unchecked")
	public Double getMonthlyPaidAmount(Contract contract, String yyyymm) {
    	Double total = 0d;
    	String start = yyyymm +  "00000000";
    	String end = yyyymm + "99999999";
    	Criteria criteria = getSession().createCriteria(PrepaymentLog.class);
    	criteria.add(Restrictions.eq("contract.id", contract.getId()));
    	criteria.add(Restrictions.between("lastTokenDate", start, end));
    	criteria.add(Restrictions.isNull("monthlyPaidAmount"));
        criteria.add(Restrictions.isNull("monthlyTotalAmount"));
    	List<PrepaymentLog> list = criteria.list();

    	for ( PrepaymentLog log : list ) {
    		total += StringUtil.nullToDoubleZero(log.getUsedCost());
    	}
    	return total;
    }

    @Override
	@SuppressWarnings("unchecked")
	public List<PrepaymentLog> getMonthlyConsumptionLog(String yyyyMM, String tariffName) {
    	Criteria criteria = getSession().createCriteria(PrepaymentLog.class);

    	tariffName = StringUtil.nullToBlank(tariffName);
    	String startDate = yyyyMM + "00000000";
    	String endDate = yyyyMM + "99999999";

    	logger.debug("\n tarifName: " + tariffName);
    	logger.debug("\n startDate: " + startDate
    			+ "\n endDate: " + endDate);
    	criteria.createAlias("contract", "contract");
    	criteria.createAlias("contract.tariffIndex", "tariffType");

    	if ( !tariffName.equals("") ) {
    		criteria.add(Restrictions.eq("tariffType.name", tariffName));
    	}
    	criteria.add(Restrictions.isNotNull("monthlyPaidAmount"));
    	criteria.add(Restrictions.isNotNull("monthlyTotalAmount"));
    	criteria.add(Restrictions.between("lastTokenDate", startDate, endDate));
    	return criteria.list();
    }

    @Override
	public Double getMonthlyUsageByContract(Contract contract, String yyyymm) {
    	Double usage = null;
    	StringBuffer sb  = new StringBuffer("SELECT SUM(usedConsumption) \n");
    	sb.append(" FROM PrepaymentLog \n");
    	sb.append(" WHERE contract = :contract \n");
    	sb.append(" AND lastTokenDate like :lastTokenDate ");
    	sb.append(" AND monthlyTotalAmount is null");
    	sb.append(" AND monthlyPaidAmount is null");
    	sb.append(" AND monthlyServiceCharge is null");
    	Query query = getSession().createQuery(sb.toString());
    	query.setEntity("contract", contract);
    	query.setString("lastTokenDate", yyyymm +"%");
    	usage = (Double) query.uniqueResult();
    	return usage;
    }

    @Override
	@SuppressWarnings("unchecked")
	public List<PrepaymentLog> getMonthlyConsumptionLog(String yyyyMM, String tariffName, List<Integer> locationIds) {
    	Criteria criteria = getSession().createCriteria(PrepaymentLog.class);

    	tariffName = StringUtil.nullToBlank(tariffName);
    	String startDate = yyyyMM + "00000000";
    	String endDate = yyyyMM + "99999999";

    	logger.debug("\n tarifName: " + tariffName);
    	logger.debug("\n startDate: " + startDate
    			+ "\n endDate: " + endDate);

    	criteria.createAlias("tariffIndex", "tariffIndex");

    	if ( !tariffName.equals("") ) {
    		criteria.add(Restrictions.eq("tariffIndex.name", tariffName));
    	}
    	criteria.add(Restrictions.isNotNull("monthlyPaidAmount"));
    	criteria.add(Restrictions.isNotNull("monthlyTotalAmount"));
    	criteria.add(Restrictions.isNotNull("monthlyServiceCharge"));
    	criteria.add(Restrictions.between("lastTokenDate", startDate, endDate));
    	criteria.add(Restrictions.in("location.id", locationIds));
    	criteria.add(Restrictions.ge("usedConsumption", new Double(0)));
    	return criteria.list();
    }
    
    @Override
	@SuppressWarnings("unchecked")
	public List<PrepaymentLog> getMonthlyConsumptionLogByGeocode(String yyyyMM, String tariffName, String geocode) {
    	Criteria criteria = getSession().createCriteria(PrepaymentLog.class);

    	tariffName = StringUtil.nullToBlank(tariffName);
    	String startDate = yyyyMM + "00000000";
    	String endDate = yyyyMM + "99999999";

    	logger.debug("\n tarifName: " + tariffName);
    	logger.debug("\n startDate: " + startDate
    			+ "\n endDate: " + endDate);

    	criteria.createAlias("tariffIndex", "tariffIndex");
    	criteria.createAlias("contract", "contract");

    	if ( !tariffName.equals("") ) {
    		criteria.add(Restrictions.eq("tariffIndex.name", tariffName));
    	}
    	
    	criteria.add(Restrictions.isNotNull("monthlyPaidAmount"));
    	criteria.add(Restrictions.isNotNull("monthlyTotalAmount"));
    	criteria.add(Restrictions.isNotNull("monthlyServiceCharge"));
    	criteria.add(Restrictions.between("lastTokenDate", startDate, endDate));
    	criteria.add(Restrictions.like("contract.contractNumber", geocode+"%"));
    	criteria.add(Restrictions.ge("usedConsumption", new Double(0)));
    	return criteria.list();
    }


    @Override
	@SuppressWarnings("unchecked")
    public List<PrepaymentLog> getMonthlyReceiptLog(String yyyyMM) {
    	Criteria criteria = getSession().createCriteria(PrepaymentLog.class);
    	String startDate = yyyyMM + "00000000";
    	String endDate = yyyyMM + "99999999";
    	criteria.add(Restrictions.between("lastTokenDate", startDate, endDate));
    	criteria.add(Restrictions.isNotNull("monthlyPaidAmount"));
    	criteria.add(Restrictions.isNotNull("monthlyTotalAmount"));
    	criteria.add(Restrictions.isNotNull("monthlyServiceCharge"));
    	return criteria.list();
    }
    
    @Override
	@SuppressWarnings("unchecked")
    public List<PrepaymentLog> getMonthlyNotCalculationReceiptLog(String yyyyMM, String[] modelName) {
    	Criteria criteria = getSession().createCriteria(PrepaymentLog.class);
    	
    	String startDate = yyyyMM + "00000000";
    	String endDate = yyyyMM + "99999999";
    	
    	criteria.createAlias("contract", "contract");
    	criteria.createAlias("contract.meter", "meter");
    	criteria.createAlias("meter.model", "model");
    	
    	if (modelName != null && !modelName.equals("") && modelName.length > 0) {
    		criteria.add(Restrictions.in("model.name", modelName));
    	}
    	
    	criteria.add(Restrictions.between("lastTokenDate", startDate, endDate));
    	criteria.add(Restrictions.isNotNull("monthlyPaidAmount"));
    	criteria.add(Restrictions.isNotNull("monthlyTotalAmount"));
    	criteria.add(Restrictions.isNotNull("monthlyServiceCharge"));
    	return criteria.list();
    }
    
    @Override
	public Long getRecentPrepaymentLogId(String contractNubmer) {
    	StringBuffer sb = new StringBuffer();
    	sb.append("\n SELECT max(log.id) ");
    	sb.append("\n FROM PrepaymentLog log ");
    	sb.append("\n 		LEFT OUTER JOIN log.contract con ");
    	sb.append("\n		LEFT OUTER JOIN log.operator o ");
    	sb.append("\n WHERE ");
    	sb.append("\n 		con.contractNumber = :contractNumber ");
    	sb.append("\n 		AND o IS NOT null ");
    	sb.append("\n 		AND ( log.isCanceled is null  OR log.isCanceled = false ) "); // 취소된 결제내역 제외
    	sb.append("\n 		AND log.monthlyTotalAmount is null "); // 월간 정산 결제내역 제외
    	Query query = getSession().createQuery(sb.toString());
    	query.setString("contractNumber", contractNubmer);
    	return (Long) query.uniqueResult();
    }

    @Override
	@Transactional(propagation=Propagation.REQUIRES_NEW)
    @SuppressWarnings("unchecked")
	public List<Map<String, Object>> getPrepaymentLogList(Integer contractId, String startDate, String endDate, String vendorId) {
    	startDate = startDate + "000000";
    	endDate = endDate + "999999";

    	StringBuffer sb = new StringBuffer();
        sb.append("\nSELECT p.lastTokenDate AS lastTokenDate, ");
        sb.append("\n       p.balance AS balance, ");
        sb.append("\n       p.arrears AS arrears, ");
        sb.append("\n       p.isCanceled AS isCanceled, ");
        sb.append("\n       p.chargedCredit AS chargedCredit, ");
        sb.append("\n       p.chargedArrears AS chargedArrears, ");
        sb.append("\n       p.usedCost AS usedCost, ");
        sb.append("\n       p.usedConsumption AS usedConsumption, ");
        sb.append("\n       p.contract.currentCredit AS currentCredit, ");
        sb.append("\n       p.contract.keyNum AS keyNum, ");
        sb.append("\n       m.mdsId AS mdsId, ");
        sb.append("\n       m.id AS meterId, ");
        sb.append("\n       d.descr AS payment, ");
        sb.append("\n       p.lastTokenId AS lastTokenId, ");
        sb.append("\n       p.authCode AS authCode, ");
        sb.append("\n       e.descr AS municipalityCode, ");
        sb.append("\n       p.contract.id AS contractId, ");
        sb.append("\n       p.id AS prepaymentLogId, ");
        sb.append("\n		  cu.name AS customerName, ");
        sb.append("\n		  c.contractNumber AS contractNumber, ");
        // add by eunmiae 3 Feb 2015
        sb.append("\n		v.name AS cashierName, ");
        sb.append("\n		o.name AS vendorName ");
        sb.append("\nFROM PrepaymentLog p ");
        sb.append("\n     LEFT OUTER JOIN p.contract.keyType d ");
        sb.append("\n     LEFT OUTER JOIN p.contract.meter m ");
        sb.append("\n     LEFT OUTER JOIN p.municipalityCode e ");
        sb.append("\n		LEFT OUTER JOIN p.contract c ");
        sb.append("\n		LEFT OUTER JOIN c.customer cu ");

        // add by eunmiae 3 Feb 2015
        sb.append("\n		LEFT OUTER JOIN p.vendorCasher v ");
        sb.append("\n		LEFT OUTER JOIN p.operator o ");

        sb.append("\nWHERE p.contractId = :contractId ");
        sb.append("\nAND   p.lastTokenDate BETWEEN :startDate AND :endDate ");
        
        // vendor 조건이 있을 경우
        if(vendorId != null && !vendorId.isEmpty()){
        	sb.append("\nAND   o.loginId = ").append(vendorId).append(" ");
        }
        
        
        sb.append("\nORDER BY p.id");

        Query query = getSession().createQuery(sb.toString());
        query.setInteger("contractId",contractId );
        query.setString("startDate", startDate);
        query.setString("endDate", endDate);
        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

    @Override
	@SuppressWarnings("unchecked")
    public List<PrepaymentLog> getMonthlyCredit(Map<String, Object> condition) {
        Integer contractId = (Integer)condition.get("contractId");
        String startDate = (String)condition.get("startDate");
        String endDate = (String)condition.get("endDate");


        StringBuilder sb = new StringBuilder();

        sb.append("\nFROM PrepaymentLog p ");
        sb.append("\nWHERE p.contract.id = :contractId ");
        sb.append("\nAND   (p.lastTokenDate Between :monthlyFirstDay AND :monthlyLastDay) ");
        sb.append("\nAND   (p.usedCost is not null AND p.usedCost <> 0 )  ");

        Query query = getSession().createQuery(sb.toString());
        query.setInteger("contractId", contractId);
        query.setString("monthlyFirstDay", startDate+"000000");
        query.setString("monthlyLastDay", endDate+"595959");

        return query.list();
    }

    /**
     * method name : checkMonthlyFirstReceipt<b/>
     * method Desc : 영수증 출력 시 해당 월의 첫 번째 선불결제인지 체크.
     *
     * @param conditionMap
     * @return
     */
    @Override
	public Boolean checkMonthlyFirstReceipt(Map<String, Object> conditionMap) {
        Boolean result = null;
        Integer contractId = (Integer)conditionMap.get("contractId");
        String lastTokenDate = (String)conditionMap.get("lastTokenDate");
        String yyyymm = lastTokenDate.substring(0, 6);

        StringBuilder sb = new StringBuilder();
        sb.append("\nSELECT CASE WHEN MIN(pl.lastTokenDate) = :lastTokenDate ");
        sb.append("\n            THEN 'true' ELSE 'false' END AS isFirst ");
        sb.append("\nFROM PrepaymentLog pl ");
        sb.append("\nWHERE pl.lastTokenDate LIKE :yyyymm ");
        sb.append("\nAND   pl.contract.id = :contractId ");
        sb.append("\nAND   pl.monthlyPaidAmount IS NULL ");
        sb.append("\nAND   pl.monthlyTotalAmount IS NULL ");
        sb.append("\nAND   pl.operator IS NOT NULL ");
        sb.append("\nAND   (pl.isCanceled = false OR pl.isCanceled is null)");

        Query query = getSession().createQuery(sb.toString());
        query.setString("lastTokenDate", lastTokenDate);
        query.setString("yyyymm", yyyymm + "%");
        query.setInteger("contractId", contractId);

        String isFirst = (String)query.uniqueResult();
        result = Boolean.valueOf(isFirst);
        return result;
    }

    /**
     * method name : getMonthlyPaidData<b/>
     * method Desc : 월정산 데이터를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @Override
	@SuppressWarnings("unchecked")
    public PrepaymentLog getMonthlyPaidData(Map<String, Object> conditionMap) {
        List<PrepaymentLog> list = null;
        PrepaymentLog result = null;
        Integer contractId = (Integer)conditionMap.get("contractId");
        String lastTokenDate = (String)conditionMap.get("lastTokenDate");
        String yyyymm = lastTokenDate.substring(0, 6);

        StringBuilder sb = new StringBuilder();
        sb.append("\nFROM PrepaymentLog pl ");
        sb.append("\nWHERE pl.lastTokenDate LIKE :yyyymm ");
        sb.append("\nAND   pl.contract.id = :contractId ");
        sb.append("\nAND   pl.monthlyPaidAmount IS NOT NULL ");
        sb.append("\nAND   pl.monthlyTotalAmount IS NOT NULL ");
        sb.append("\nAND   pl.operator IS NOT NULL ");

        Query query = getSession().createQuery(sb.toString());
        query.setString("yyyymm", yyyymm + "%");
        query.setInteger("contractId", contractId);

        list = query.list();
        if (list != null && list.size() > 0) {
            result = list.get(0);
        }
        return result;
    }
    
    /**
     * method name : getMonthlyPaidDataCount<b/>
     * method Desc : 월정산 데이터를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @Override
	@SuppressWarnings("unchecked")
    public List<PrepaymentLog> getMonthlyPaidDataCount(Map<String, Object> conditionMap) {
        Integer contractId = (Integer)conditionMap.get("contractId");
        String lastTokenDate = (String)conditionMap.get("lastTokenDate");
        String yyyymm = lastTokenDate.substring(0, 6);

        StringBuilder sb = new StringBuilder();
        sb.append("\nFROM PrepaymentLog pl ");
        sb.append("\nWHERE pl.lastTokenDate LIKE :yyyymm ");
        sb.append("\nAND   pl.contract.id = :contractId ");
        sb.append("\nAND   pl.monthlyPaidAmount IS NOT NULL ");
        sb.append("\nAND   pl.monthlyTotalAmount IS NOT NULL ");
        sb.append("\nAND   pl.operator IS NOT NULL ");

        Query query = getSession().createQuery(sb.toString());
        query.setString("yyyymm", yyyymm + "%");
        query.setInteger("contractId", contractId);

        return query.list();
    }

    @Override
    public Long getNextVal() {
    	StringBuilder sb = new StringBuilder();
    	Long nextVal = 0L;
    	try{
    		sb.append("SELECT sequence_next_hi_value as trNo from KEY_GEN_PREPAYMENTLOG where sequence_name = 'PREPAYMENTLOG' ");
        	Query query = getSession().createSQLQuery(sb.toString());
        	nextVal = ((Integer) query.uniqueResult()).longValue();
    	}catch(Exception e){
    		logger.error(e, e);
    		return nextVal;
    	}
    
        return nextVal;
    }
    
    /**
     * method name : getDoubleSalesList
     * method Desc : Vendor 충전 가젯에서 고객의 잔액을 충전했는데 같은 금액으로 두번 로그가 남는 리스트를 삭제하기 위해 두번이상 충전한 고객목록 검색.
     */
    @Override
    public List<Map<String,Object>> getDoubleSalesList(String yyyymmdd) {
    	List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
    	StringBuilder sb = new StringBuilder();
    	try{
    		sb.append("\nSELECT p.ID AS PID, ");
    		sb.append("\n		c.CONTRACT_NUMBER AS CONTRACT_NUMBER, ");
    		sb.append("\n		c.SERVICE_POINT_ID AS SERVICE_POINT_ID, ");
    		sb.append("\n		p.LASTTOKENDATE AS LASTTOKENDATE, ");
    		sb.append("\n		p.CHARGEDCREDIT AS CHARGEDCREDIT, ");
    		sb.append("\n		p.BALANCE AS BALANCE,  ");
    		sb.append("\n		p.ARREARS AS ARREARS,  ");
    		sb.append("\n		p.PRE_BALANCE AS PRE_BALANCE, ");
    		sb.append("\n		p.PRE_ARREARS AS PRE_ARREARS  ");
    		sb.append("\nFROM   PREPAYMENTLOG p LEFT OUTER JOIN CONTRACT c on p.CONTRACT_ID = c.ID ");
    		sb.append("\nWHERE	p.CHARGEDCREDIT > 0 and p.LASTTOKENDATE like :yyyymmdd  ");
    		sb.append("\nAND 	p.CONTRACT_ID in (  ");
    		sb.append("\n						SELECT	 e.CONTRACT_ID FROM (  ");
    		sb.append("\n								SELECT COUNT(d.CONTRACT_ID) cnt, d.CONTRACT_ID ");
    		sb.append("\n								FROM PREPAYMENTLOG d   ");
    		sb.append("\n								WHERE d.CHARGEDCREDIT > 0 AND d.LASTTOKENDATE like :yyyymmdd GROUP BY d.CONTRACT_ID) e where e.cnt > 1)  ORDER BY p.id asc  ");
        	Query query = getSession().createSQLQuery(new SQLWrapper().getQuery(sb.toString()));
        	query.setString("yyyymmdd", yyyymmdd+"%");
        	result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    	}catch(Exception e){
    		logger.error(e, e);
    	}
        return result;
    }

}