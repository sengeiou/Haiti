package com.aimir.dao.device.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.device.CircuitBreakerLogDao;
import com.aimir.dao.device.CircuitBreakerSettingDao;
import com.aimir.dao.device.InverterDao;
import com.aimir.dao.mvm.SeasonDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.TOURateDao;
import com.aimir.model.device.Inverter;

@Repository(value = "inverterDao")
public class InverterDaoImpl extends AbstractHibernateGenericDao<Inverter, Integer> implements InverterDao {

    Log logger = LogFactory.getLog(InverterDaoImpl.class);
    
    @Autowired CodeDao codeDao;
    @Autowired CircuitBreakerSettingDao circuitBreakerSettingDao;
    @Autowired CircuitBreakerLogDao circuitBreakerLogDao;
    @Autowired TOURateDao tOURateDao;
    @Autowired SeasonDao seasonDao;
    
	@Autowired
	protected InverterDaoImpl(SessionFactory sessionFactory) {
		super(Inverter.class);
		super.setSessionFactory(sessionFactory);
	}

/*
 	

	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public List<Map<String, String>> getElecSupplyCapacityGridData(	Map<String, String> paramMap) {

		String switchStatus = StringUtil.nullToBlank(paramMap.get("switchStatus"));
		String groupType = StringUtil.nullCheck(paramMap.get("groupType"), "Meter");
		String target = StringUtil.nullToBlank(paramMap.get("target"));
		String condition = StringUtil.nullToBlank(paramMap.get("condition"));
		String meterStatus = StringUtil.nullToBlank(paramMap.get("meterStatus"));
		String dateFlag = StringUtil.nullToBlank(paramMap.get("dateFlag"));
		
//		int page = Integer.parseInt(paramMap.get("page"));
//		int pageSize = Integer.parseInt(paramMap.get("pageSize"));
//		int firstIndex = page * pageSize;

		StringBuilder sb = new StringBuilder()
		.append(" SELECT 'false' as checked, m.location.name as locationName, m.mdsId as meterId, mcu.sysID as mcuId, m.switchStatus as switchStatus, ")
		.append("        cont.contractDemand as contractDemand, m.meterStatus.descr as meterStatus, m.id as id, cont.id as contractId, cont.customer.name as customerName, ")
		.append("        ed.serialNumber as endDeviceId, '" + groupType + "' as groupType, '' as target, cont.contractNumber as contractNumber, ")
		.append("        modem.deviceSerial as modemId , cont.keyType.code as creditTypeCode, cont.creditStatus.code as creditStatusCode, cont.chargedCredit as chargedCredit, ")
		.append("        cont.prepaymentThreshold as prepaymentThreshold, cont.currentCredit as currentCredit, cont.tariffIndex.id as tariffTypeId ")
		.append("   FROM EnergyMeter m, DayEM d, Contract cont ")
		.append("   LEFT OUTER JOIN m.modem as modem ")
		.append("   LEFT OUTER JOIN modem.mcu as mcu ")
		.append("   LEFT OUTER JOIN m.endDevice as ed ")		
		.append("  WHERE m.contract.id = cont.id ")
		.append("    AND d.meter.id = m.id       ")
		.append("    AND d.contract.id = cont.id ")
		.append("    AND d.id.channel = 1 ");
		
		if(!"false".equals(dateFlag)) {
			sb.append("    AND d.id.yyyymmdd = :yyyymmdd  ");
		}
						
		if(!"".equals(switchStatus)) sb.append(" AND m.switchStatus = :switchStatus  ");
		if(!"".equals(meterStatus)) sb.append(" AND m.meterStatus = :meterStatus  ");
		
		if(!"".equals(target)) {
			if("Location".equals(groupType))
				sb.append(" AND cont.location.name like :target ");
			else if("Contract".equals(groupType))
				sb.append(" AND cont.contractNumber like :target ");
			else if("MCU".equals(groupType)) 
				sb.append(" AND mcu.sysID like :target ");
			else if("Modem".equals(groupType)) 
				sb.append(" AND modem.deviceSerial like :target ");
			else if("Meter".equals(groupType)) 
				sb.append(" AND m.mdsId like :target ");
			else if("EndDevice".equals(groupType)) 
				sb.append(" AND ed.serialNumber like :target ");			
		}

		Query query = getSession().createQuery(sb.toString());
		if(!"false".equals(dateFlag)) {
			query.setString("yyyymmdd", DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMdd"));
		}
//		query.setFirstResult(firstIndex);		
//		query.setMaxResults(pageSize);
		
		if(!"".equals(switchStatus)) query.setInteger("switchStatus", CircuitBreakerStatus.valueOf(switchStatus).ordinal());
		if(!"".equals(meterStatus)) query.setInteger("meterStatus", CommonConstants.getMeterStatusByName(meterStatus).getId());
		
		if(!"".equals(target)) {
			query.setString("target", target + "%");
		}
		
		List<Map<String, String>> result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		
		return makeGridData(result, condition, dateFlag);
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public List<Map<String, String>> getEmergencyElecSupplyCapacityGridData(	Map<String, String> paramMap) {

		String switchStatus = StringUtil.nullToBlank(paramMap.get("switchStatus"));
		String groupType = StringUtil.nullCheck(paramMap.get("groupType"), "Meter");
		String target = StringUtil.nullToBlank(paramMap.get("target"));
		String condition = StringUtil.nullToBlank(paramMap.get("condition"));
		String meterStatus = StringUtil.nullToBlank(paramMap.get("meterStatus"));
		String dateFlag = StringUtil.nullToBlank(paramMap.get("dateFlag"));
		
		if(paramMap.get("groupType") == null || "".equals(paramMap.get("groupType"))){
			return new ArrayList<Map<String, String>>(0);
		}

		StringBuilder sb = new StringBuilder()
		.append(" SELECT 'false' as checked, m.location.name as locationName, m.mdsId as meterId, mcu.sysID as mcuId, m.switchStatus as switchStatus, ")
		.append("        cont.contractDemand as contractDemand, m.meterStatus.descr as meterStatus, m.id as id, cont.id as contractId, cont.customer.name as customerName, ")
		.append("        ed.serialNumber as endDeviceId, '" + groupType + "' as groupType, '' as target, cont.contractNumber as contractNumber, ")
		.append("        modem.deviceSerial as modemId , cont.keyType.code as creditTypeCode, cont.creditStatus.code as creditStatusCode, cont.chargedCredit as chargedCredit, ")
		.append("        cont.prepaymentThreshold as prepaymentThreshold, cont.currentCredit as currentCredit, cont.tariffIndex.id as tariffTypeId ")
		.append("   FROM EnergyMeter m, Contract cont ")
		.append("   LEFT OUTER JOIN m.modem as modem ")
		.append("   LEFT OUTER JOIN modem.mcu as mcu ")
		.append("   LEFT OUTER JOIN m.endDevice as ed ")		
		.append("  WHERE m.contract.id = cont.id ");

						
		if(!"".equals(switchStatus)) sb.append(" AND m.switchStatus = :switchStatus  ");
		if(!"".equals(meterStatus)) sb.append(" AND m.meterStatus = :meterStatus  ");
		
		if(!"".equals(target)) {
			if("Location".equals(groupType))
				sb.append(" AND cont.location.name like :target ");
			else if("Contract".equals(groupType))
				sb.append(" AND cont.contractNumber like :target ");
			else if("MCU".equals(groupType)) 
				sb.append(" AND mcu.sysID like :target ");
			else if("Modem".equals(groupType)) 
				sb.append(" AND modem.deviceSerial like :target ");
			else if("Meter".equals(groupType)) 
				sb.append(" AND m.mdsId like :target ");
			else if("EndDevice".equals(groupType)) 
				sb.append(" AND ed.serialNumber like :target ");			
		}

		logger.debug("Query="+sb.toString());
		Query query = getSession().createQuery(sb.toString());

		
		if(!"".equals(switchStatus)) query.setInteger("switchStatus", CircuitBreakerStatus.valueOf(switchStatus).ordinal());
		if(!"".equals(meterStatus)) query.setInteger("meterStatus", CommonConstants.getMeterStatusByName(meterStatus).getId());
		
		if(!"".equals(target)) {
			query.setString("target", target + "%");
		}
		
		List<Map<String, String>> result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		
		return makeGridData(result, condition, dateFlag);
	}
	
	private List<Map<String, String>> makeGridData(List<Map<String, String>> result, String condition, String dateFlag) {
		
		List<Map<String, String>> gridData = new ArrayList<Map<String, String>>();
		
		Object oSwitchStatus = null;
		Object oCurrentCredit = null;
		Object oPrepaymentThreshold = null;
		Object oContractDemand = null;
		Object oChargedCredit = null;
		
		String switchStatus = null;
		String creditTypeCode = null;
		String creditStatusCode = null;
		
		int compareValue1 = -1;
		int compareValue2 = -1;
		int currentCredit = -1;
		int prepaymentThreshold = -1;
		int chargedCredit = -1;
		Double peakDemand = null;
		Double contractDemand = null;
		Double commonPrepaymentThreshold = null;
		boolean prepaymentFlag = true;
		boolean thresholdFlag = true;
		
		if(condition.equals("Prepayment")) 
			thresholdFlag = false;
		if(condition.equals("ExceedsThreshold"))
			prepaymentFlag = false;
		
		for(Map<String, String> map : result) {
			
			oSwitchStatus = map.get("switchStatus");
			oCurrentCredit = map.get("currentCredit");
			oPrepaymentThreshold = map.get("prepaymentThreshold");
			oContractDemand = map.get("contractDemand");
			oChargedCredit = map.get("chargedCredit");	
			if(oSwitchStatus == null) continue;
			switchStatus = oSwitchStatus.toString();
			creditTypeCode = map.get("creditTypeCode");
			creditStatusCode = map.get("creditStatusCode");
 			if(oCurrentCredit == null) oCurrentCredit = "0";
			if(oPrepaymentThreshold == null) oPrepaymentThreshold = "0";
 			if(oChargedCredit == null) oChargedCredit = "0";
 			if(oContractDemand == null) oContractDemand = "0";
			currentCredit = (int) Double.parseDouble(oCurrentCredit.toString());
			prepaymentThreshold = Integer.parseInt(oPrepaymentThreshold.toString());
			chargedCredit = (int) Double.parseDouble(oChargedCredit.toString());
			contractDemand = Double.parseDouble(oContractDemand.toString());			
			peakDemand = getPeakDemand(map, dateFlag);
			commonPrepaymentThreshold = getCommonPrepaymentThreshold(map);
			compareValue1 = (int)((double)currentCredit / (double)chargedCredit * 100);
			compareValue2 = (int)(peakDemand / contractDemand * 100);
			map.put("peakDemand", peakDemand + "");
			
			// 차단대상
			if(switchStatus.equals(CircuitBreakerStatus.Activation.name())) {
				// 선불차단
				if(creditTypeCode.equals(PaymentType.Debit.getCode()) && creditStatusCode.equals(PaymentStatus.Unpaid.getCode()) && prepaymentFlag) {										
					map.put("block", CircuitBreakerStatus.Deactivation.name());
					map.put("condition", CircuitBreakerCondition.Prepayment.name());
					gridData.add(map);
					continue;
				} else if(creditTypeCode.equals(PaymentType.Debit.getCode()) && currentCredit == 0 && prepaymentFlag) {
					map.put("block", CircuitBreakerStatus.Deactivation.name());
					map.put("condition", CircuitBreakerCondition.Prepayment.name());
					gridData.add(map);
					continue;					
				} else if(creditTypeCode.equals(PaymentType.Debit.getCode()) && compareValue1 < prepaymentThreshold && prepaymentFlag) {
					map.put("block", CircuitBreakerStatus.Deactivation.name());
					map.put("condition", CircuitBreakerCondition.Prepayment.name());
					gridData.add(map);
					continue;					
				} else if(creditTypeCode.equals(PaymentType.Debit.getCode()) && compareValue1 < commonPrepaymentThreshold && prepaymentFlag) {
					map.put("block", CircuitBreakerStatus.Deactivation.name());
					map.put("condition", CircuitBreakerCondition.Prepayment.name());
					gridData.add(map);
					continue;	
					
				// 공급임계치 초과
				} else if(creditTypeCode.equals(PaymentType.Credit.getCode()) && contractDemand >= peakDemand && thresholdFlag) {
					map.put("block", CircuitBreakerStatus.Deactivation.name());
					map.put("condition", CircuitBreakerCondition.ExceedsThreshold.name());
					gridData.add(map);
					continue;					
				} else if(creditTypeCode.equals(PaymentType.Credit.getCode()) && compareValue2 >= prepaymentThreshold && thresholdFlag) {
					map.put("block", CircuitBreakerStatus.Deactivation.name());
					map.put("condition", CircuitBreakerCondition.ExceedsThreshold.name());
					gridData.add(map);
					continue;					
				}
				
			// 해제 대상
			} else if(switchStatus.equals(CircuitBreakerStatus.Deactivation.name())) {
												
				//선불 재개
				if(creditTypeCode.equals(PaymentType.Debit.getCode()) && creditStatusCode.equals(PaymentStatus.Paid.getCode()) && prepaymentFlag) {
					map.put("block", CircuitBreakerStatus.Activation.name());
					map.put("condition", CircuitBreakerCondition.Prepayment.name());
					gridData.add(map);
					continue;					
				} else if(creditTypeCode.equals(PaymentType.Debit.getCode()) && currentCredit > 0 && prepaymentFlag) {
					map.put("block", CircuitBreakerStatus.Activation.name());
					map.put("condition", CircuitBreakerCondition.Prepayment.name());
					gridData.add(map);
					continue;					
				} else if(creditTypeCode.equals(PaymentType.Debit.getCode()) && compareValue1 > prepaymentThreshold && prepaymentFlag) {
					map.put("block", CircuitBreakerStatus.Activation.name());
					map.put("condition", CircuitBreakerCondition.Prepayment.name());
					gridData.add(map);
					continue;					
				} else if(creditTypeCode.equals(PaymentType.Debit.getCode()) && compareValue1 > commonPrepaymentThreshold && prepaymentFlag) {
					map.put("block", CircuitBreakerStatus.Activation.name());
					map.put("condition", CircuitBreakerCondition.Prepayment.name());
					gridData.add(map);
					continue;					
				
				// 공급임계치 초과
				} else if(creditTypeCode.equals(PaymentType.Credit.getCode()) && getLogRecord(map) && thresholdFlag) {

					map.put("block", CircuitBreakerStatus.Activation.name());
					map.put("condition", CircuitBreakerCondition.ExceedsThreshold.name());
					gridData.add(map);			
				}				
			}					
		}
				
		return gridData;
	}

	
	private boolean getLogRecord(Map<String, String> map) {

		String locationName = StringUtil.nullCheck(map.get("locationName"), "");
		String contractNumber = StringUtil.nullCheck(map.get("contractNumber"), "");
		String mcuId = StringUtil.nullCheck(map.get("mcuId"), "");
		String modemId = StringUtil.nullCheck(map.get("modemId"), "");
		String meterId = StringUtil.nullCheck(map.get("meterId"), "");
		String endDeviceId = StringUtil.nullCheck(map.get("endDeviceId"), "");
		
		Criteria criteria = getSession().createCriteria(CircuitBreakerLog.class);
		criteria.setProjection(Projections.rowCount());
		criteria.add(Restrictions.eq("status", CircuitBreakerStatus.Deactivation));
		
		Disjunction or = Restrictions.disjunction();
		or.add(Restrictions.eq("target", locationName))
		.add(Restrictions.eq("target", contractNumber))
		.add(Restrictions.eq("target", mcuId))
		.add(Restrictions.eq("target", modemId))
		.add(Restrictions.eq("target", meterId))
		.add(Restrictions.eq("target", endDeviceId));	
		
		criteria.add(or);
		
		if(((Number)criteria.uniqueResult()).intValue() > 0) 
			return true;
		else 
			return false;
	}

	// 공통임계치 
	private Double getCommonPrepaymentThreshold(Map<String, String> map) {
	
		Set<Condition> conditionSet = new HashSet<Condition>();
		conditionSet.add(new Condition("condition", new Object[] { CircuitBreakerCondition.Prepayment }, null, Restriction.EQ));		
		List<CircuitBreakerSetting> settings = circuitBreakerSettingDao.findByConditions(conditionSet);

		if(settings == null || settings.size() == 0 || settings.get(0).getBlockingThreshold() == null) return 0d;
		
		return settings.get(0).getBlockingThreshold();
	}
	
	// peak demand
	@SuppressWarnings("unchecked")
	private Double getPeakDemand(Map<String, String> map, String dateFlag)  {
		
		Object oTariffTypeId = map.get("tariffTypeId");
		String tariffTypeId = oTariffTypeId.toString();
		String startTime = null;
		String endTime = null;
		Season season = seasonDao.getSeasonByMonth(DateTimeUtil.getCurrentDateTimeByFormat("MM"));
		
		Set<Condition> conditionSet = new HashSet<Condition>();
		conditionSet.add(new Condition("tariffType.id", new Object[] { Integer.parseInt(tariffTypeId) }, null, Restriction.EQ));
		conditionSet.add(new Condition("peakType", new Object[] { PeakType.CRITICAL_PEAK }, null, Restriction.EQ));
		conditionSet.add(new Condition("season.id", new Object[] { season.getId() }, null, Restriction.EQ));
		List<TOURate> TOURates =  tOURateDao.getTOURateByListCondition(conditionSet);
		
		if(TOURates == null || TOURates.size() == 0) {
			startTime = "00";
			endTime = "23";
		} else {
			startTime = TOURates.get(0).getStartTime();
			startTime = TOURates.get(0).getEndTime();
		}
		
		StringBuilder sb = new StringBuilder()
		.append(" SELECT value_00, value_01, value_02, value_03, value_04, value_05, value_06, value_07, ") 
		.append("        value_08, value_09, value_10, value_11, value_12, value_13, value_14, value_15, ")
		.append("        value_16, value_17, value_18, value_19, value_20, value_21, value_22, value_23  ")
		.append("   FROM DAY_EM d                                                                        ")
		.append("  WHERE d.contract_id = :contractId                                                     ")
		.append("    AND d.meter_id =    :meterId                                                        ");
		
		if(!"false".equals(dateFlag))
			sb.append("    AND d.yyyymmdd = :yyyymmdd                                                        ");
		
		 
		Object oContractId = map.get("contractId");
		Object oMeterId = map.get("id");

		SQLQuery query = getSession().createSQLQuery(sb.toString());		
		query.setInteger("contractId", Integer.parseInt(oContractId.toString()));
		query.setInteger("meterId", Integer.parseInt(oMeterId.toString()));
		if(!"false".equals(dateFlag))
			query.setString("yyyymmdd", DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMdd"));
		
		List<Object[]> result = query.list();

		if(result == null || result.size() < 1){
			return 0d;
		}
		Object[] obj = result.get(0);
		List<Double> values = new ArrayList<Double>();
		Double value = null;
		
//		for(Object value : obj) {
//			values.add(Double.parseDouble(value.toString()));
//		}
		for(int i = Integer.parseInt(startTime), endIndex = Integer.parseInt(endTime); i < endIndex; i ++) {
			value = Double.parseDouble(obj[i] ==null ? "0" : obj[i].toString());
			values.add(value);			
		}

		return Collections.max(values);
	}
	
	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public String getElecSupplyCapacityGridDataCount(Map<String, String> paramMap) {

		String switchStatus = StringUtil.nullToBlank(paramMap.get("switchStatus"));
		String groupType = StringUtil.nullCheck(paramMap.get("groupType"), "Meter");
		String target = StringUtil.nullToBlank(paramMap.get("target"));
//		String reason = StringUtil.nullToBlank(paramMap.get("reason"));
		String meterStatus = StringUtil.nullToBlank(paramMap.get("meterStatus"));

		StringBuilder sb = new StringBuilder()
		.append(" SELECT count(m)      ")
		.append("   FROM EnergyMeter m ")
		.append("   LEFT OUTER JOIN m.location as loc ")
		.append("   LEFT OUTER JOIN m.modem as modem ")
		.append("   LEFT OUTER JOIN modem.mcu as mcu ")
		.append("   LEFT OUTER JOIN m.contract as cont ")
		.append("   LEFT OUTER JOIN cont.customer as cust ")
		.append("   LEFT OUTER JOIN m.endDevice as ed ")
		.append("   LEFT OUTER JOIN m.meterStatus as ms ")
		.append("  WHERE m.id is not null ");
		
		if(!"".equals(switchStatus)) sb.append(" AND m.switchStatus = :switchStatus  ");
		if(!"".equals(meterStatus)) sb.append(" AND m.meterStatus = :meterStatus  ");
		
		if(!"".equals(target)) {
			if("Location".equals(groupType))
				sb.append(" AND loc.name like :target ");
			else if("Contract".equals(groupType))
				sb.append(" AND cont.contractNumber like :target ");
			else if("MCU".equals(groupType)) 
				sb.append(" AND mcu.sysID like :target ");
			else if("Modem".equals(groupType)) 
				sb.append(" AND modem.deviceSerial like :target ");
			else if("Meter".equals(groupType)) 
				sb.append(" AND m.mdsId like :target ");
			else if("EndDevice".equals(groupType)) 
				sb.append(" AND ed.serialNumber like :target ");			
		}

		Query query = getSession().createQuery(sb.toString());		

		if(!"".equals(switchStatus)) query.setInteger("switchStatus", CircuitBreakerStatus.valueOf(switchStatus).ordinal());
		if(!"".equals(meterStatus)) query.setInteger("meterStatus", CommonConstants.getMeterStatusByName(meterStatus).getId());
		
		if(!"".equals(target)) {
			query.setString("target", target + "%");
		}
		
		return query.uniqueResult().toString();
	}	
	
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public List<Map<String, String>> getElecSupplyCapacityMiniGridData(	Map<String, String> paramMap) {

		String target = StringUtil.nullToBlank(paramMap.get("target"));

		String today;
		String yesterday;

		try {
			today = TimeUtil.getCurrentDay();
			yesterday = TimeUtil.getPreDay(today).substring(0, 8);
		} catch (Exception e) {
			today = null;
			yesterday = null;
		} 
		
		StringBuilder sb = new StringBuilder()
        .append("SELECT  'false'              as  checked ")
        .append("      , cont.customer.name   as  customerName ")
        .append("      , cont.contractNumber  as  contractNumber ")
        .append("      , m.mdsId              as  meterId ")
        .append("      , ''                   as  condition ")
        .append("      , m.switchStatus       as  switchStatus ")
        .append("      , mcu.sysID            as  mcuId ")
        .append("  FROM  EnergyMeter m ")
        .append("      , Contract    cont ")
        .append("      , DayEM       d ")
        .append("  LEFT OUTER JOIN m.modem as modem ")
        .append("  LEFT OUTER JOIN modem.mcu  as mcu ")
        .append(" WHERE  m.contract.id = cont.id ")
        .append("   AND  m.id          = d.meter.id ")
		.append("   AND  cont.id       = d.contract.id ")
		.append("   AND  d.id.channel  = 1 ");
		
		if (today != null && yesterday != null) {
			sb.append("   AND  d.id.yyyymmdd between :yesterday AND :today ");
		}
		
		if (target.equals("0")) {
			sb.append("   AND m.switchStatus = :target ")
			.append("   AND ( ( cont.keyType.code = :debitCode ")
			.append("         AND ( cont.creditStatus.code = :unpaidCode ")
			.append("             OR cont.currentCredit = 0 ")
			.append("             OR cont.currentCredit / cont.chargedCredit * 100 < cont.prepaymentThreshold ")
			.append("             OR cont.currentCredit / cont.chargedCredit * 100 <  ")
			.append("             OR cont.prepaymentThreshold ")
			.append("             OR cont.prepaymentThreshold ")
			.append("             OR cont.prepaymentThreshold ")
			.append("             OR cont.prepaymentThreshold ")
			.append("             OR cont.prepaymentThreshold ")
			.append("             OR cont.prepaymentThreshold ")
			.append("      OR ( cont.keyType.code = :debitCode AND cont.creditStatus.code = :unpaidCode ) ")
			.append("      OR ( cont.keyType.code = :debitCode AND cont.creditStatus.code = :unpaidCode ) ")
			.append("      OR ( cont.keyType.code = :debitCode AND cont.creditStatus.code = :unpaidCode ) ")
			;
		} else if (target.equals("1")) {
			sb.append("   AND m.switchStatus = :target ");
		}
		
		Query query = getSession().createQuery(sb.toString());

		if (today != null && yesterday != null) {
			query.setString("today", today);
			query.setString("yesterday", yesterday);
		}

		if (target.equals("0")) {
			query.setString("target", target);
			query.setString("debitCode", PaymentType.Debit.getCode());
			query.setString("unpaidCode", PaymentStatus.Unpaid.getCode());
			query.setString("debitCode", PaymentType.Debit.getCode());
			query.setString("debitCode", PaymentType.Debit.getCode());
			query.setString("debitCode", PaymentType.Debit.getCode());
			query.setString("debitCode", PaymentType.Debit.getCode());
			
		} else if (target.equals("1")) {
			query.setString("target", target);
		}
		
		List<Map<String, String>> result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		
		return result;
	}
*/
}