package com.aimir.dao.system.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants.DefaultChannel;
import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.mvm.BillingDayEMDao;
import com.aimir.dao.mvm.BillingMonthEMDao;
import com.aimir.dao.mvm.DayEMDao;
import com.aimir.dao.mvm.MonthEMDao;
import com.aimir.dao.mvm.SeasonDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.dao.system.PrepaymentLogDao;
import com.aimir.dao.system.SupplyTypeDao;
import com.aimir.dao.system.TOURateDao;
import com.aimir.dao.system.TariffEMDao;
import com.aimir.dao.system.TariffTypeDao;
import com.aimir.dao.view.MonthEMViewDao;
import com.aimir.model.mvm.DayEM;
import com.aimir.model.mvm.MonthEM;
import com.aimir.model.mvm.Season;
import com.aimir.model.system.Contract;
import com.aimir.model.system.TOURate;
import com.aimir.model.system.TariffEM;
import com.aimir.model.system.TariffType;
import com.aimir.model.view.MonthEMView;
import com.aimir.model.vo.TariffEMVO;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeUtil;

@Repository(value = "tariffemDao")
@Transactional
public class TariffEMDaoImpl extends AbstractHibernateGenericDao<TariffEM, Integer> implements TariffEMDao {
			
	private static Log logger = LogFactory.getLog(TariffEMDaoImpl.class);
	    
	@Autowired
	protected TariffEMDaoImpl(SessionFactory sessionFactory) {
		super(TariffEM.class);
		super.setSessionFactory(sessionFactory);
	}
	
	@Autowired
	TOURateDao touRateDao;
	
	@Autowired
	SeasonDao seasonDao;
	
	@Autowired
	DayEMDao dayEMDao;
	
	@Autowired
	MonthEMDao monthEMDao;
	
	@Autowired
	BillingDayEMDao billingDayEMDao;
	
	@Autowired
	BillingMonthEMDao billingMonthEMDao;
	
	@Autowired
	PrepaymentLogDao prepaymentLogDao;
	
	@Autowired
	ContractDao contractDao;
	
	@Autowired
    SupplyTypeDao supplyTypeDao;
	
	@Autowired
	CodeDao codeDao;
	
	@Autowired
	TariffTypeDao tariffTypeDao;
	
    @Autowired
    HibernateTransactionManager transactionManager;
    
    @Autowired
	MonthEMViewDao monthEMViewDao;

	@SuppressWarnings("unchecked")
	public List<Object> getYyyymmddList(Integer supplierId){

		StringBuffer sb = new StringBuffer();
		sb.append("\n SELECT   em.yyyymmdd as yyyymmdd ");
		sb.append("\n FROM     TariffEM em ");
		if(supplierId != null) {
			sb.append("\n where    em.tariffType.supplierId = :supplierId");
		}
		
		sb.append("\n GROUP BY em.yyyymmdd ");
		sb.append("\n ORDER BY em.yyyymmdd ");

		Query query = getSession().createQuery(sb.toString());
		if(supplierId != null) {
			query.setInteger("supplierId", supplierId);
		}
		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	}
	
	public String getAppliedTariffDate(String date, Integer supplierId) {
		StringBuffer sb = new StringBuffer();
		sb.append("\n SELECT max(em.YYYYMMDD) as YYYYMMDD FROM (");
		sb.append("\n		SELECT DISTINCT YYYYMMDD FROM TARIFF_EM e LEFT OUTER JOIN TARIFFTYPE T ON(e.TARIFFTYPE_ID=t.ID)");
		if(supplierId != null) {
			sb.append("\n		WHERE t.SUPPLIER_ID = :supplierId");
		}
		sb.append("\n )em WHERE  em.YYYYMMDD <= :currentDate ");
		SQLQuery query = getSession().createSQLQuery(sb.toString());
		if(supplierId != null) {
			query.setInteger("supplierId", supplierId);
		}
		query.setString("currentDate", date);
		return query.uniqueResult().toString();
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getChargeMgmtList(Map<String, Object> condition){
		
		String yyyymmdd = (String)condition.get("yyyymmdd");
		Integer supplierId = (Integer)condition.get("supplierId");
		
	    StringBuffer sb = new StringBuffer();
	    sb.append("\n SELECT	t.ID as ID, ");
	    sb.append("\n       	tt.NAME as TARIFFTYPE, ");
	    sb.append("\n       	tt.ID as TARIFFTYPEID, ");
	    sb.append("\n       	tt.CODE as TARIFFCODE, ");
	    sb.append("\n       	tt.SUPPLIER_ID as SUPPLIERID, ");
	    sb.append("\n       	s.NAME as SEASON, ");
	    sb.append("\n       	s.Id as SEASONID, ");
	    sb.append("\n       	t.START_HOUR as STARTHOUR, ");
	    sb.append("\n       	t.END_HOUR as ENDHOUR, ");
	    sb.append("\n       	t.SUPPLY_SIZE_MIN as SUPPLYSIZEMIN, ");
	    sb.append("\n       	t.SUPPLY_SIZE_MAX as SUPPLYSIZEMAX, ");
	    sb.append("\n       	t.SUPPLY_SIZE_UNIT as SUPPLYSIZEUNIT, ");
	    sb.append("\n       	t.CONDITION1 as CONDITION1, ");
	    sb.append("\n       	t.CONDITION2 as CONDITION2, ");
	    sb.append("\n       	t.PEAK_TYPE as PEAKTYPE, ");
	    sb.append("\n       	t.SERVICE_CHARGE as SERVICECHARGE, ");
	    sb.append("\n       	t.ADMIN_CHARGE as ADMINCHARGE, ");
	    sb.append("\n       	t.TRANSMISSION_NETWORK_CHARGE as TRANSMISSIONNETWORKCHARGE, ");
	    sb.append("\n       	t.DISTRIBUTION_NETWORK_CHARGE as DISTRIBUTIONNETWORKCHARGE, ");
	    sb.append("\n       	t.ENERGY_DEMAND_CHARGE as ENERGYDEMANDCHARGE, ");
	    sb.append("\n       	t.ACTIVE_ENERGY_CHARGE as ACTIVEENERGYCHARGE, ");
	    sb.append("\n       	t.REACTIVE_ENERGY_CHARGE as REACTIVEENERGYCHARGE, ");
	    sb.append("\n       	t.RATE_REBALANCING_LEVY as RATEREBALANCINGLEVY, ");
	    sb.append("\n       	t.MAXDEMAND as MAXDEMAND, ");
	    sb.append("\n			t.ERS as ERS, ");
	    sb.append("\n       	t.YYYYMMDD as YYYYMMDD ");
	    sb.append("\n FROM    TARIFFTYPE tt LEFT OUTER JOIN TARIFF_EM t ON tt.ID = t.TARIFFTYPE_ID");
	    sb.append("\n         LEFT OUTER JOIN AIMIRSEASON s ON t.SEASON_ID = s.ID");
	    sb.append("\n WHERE tt.SUPPLIER_ID = :supplierId");
	  if(yyyymmdd.length() > 0){
		  sb.append("\n AND     t.YYYYMMDD = :yyyymmdd ");
	  }
	  sb.append("\n ORDER BY t.YYYYMMDD, t.TARIFFTYPE_ID, s.ID, t.END_HOUR DESC");

	  SQLQuery query = getSession().createSQLQuery(sb.toString());
	  query.setInteger("supplierId", supplierId);
	  if(yyyymmdd.length() > 0){
		  query.setString("yyyymmdd", yyyymmdd);
	  }
	
	return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	}
	
	/*사용자 화면 - 자신이 계약한 종별*/
	@SuppressWarnings("unchecked")
	public List<TariffEMVO> getCustomerChargeMgmtList(Map<String, Object> condition){

		String yyyymmdd = (String)condition.get("yyyymmdd");
		String sUserId	= (String)condition.get("sUserId");
		
		StringBuffer sb = new StringBuffer();
		/*
		sb.append("\n SELECT	t.id as id, ");
		sb.append("\n       	t.tariffType.name as tariffType, ");
		sb.append("\n       	t.season.name as season, ");
		sb.append("\n       	t.supplySizeMin as supplySizeMin, ");
		sb.append("\n       	t.supplySizeMax as supplySizeMax, ");
		sb.append("\n       	t.supplySizeUnit as supplySizeUnit, ");
		sb.append("\n       	t.condition1 as condition1, ");
		sb.append("\n       	t.condition2 as condition2, ");
		sb.append("\n       	t.peakType as peakType, ");
		sb.append("\n       	t.serviceCharge as serviceCharge, ");
		sb.append("\n       	t.adminCharge as adminCharge, ");
		sb.append("\n       	t.transmissionNetworkCharge as transmissionNetworkCharge, ");
		sb.append("\n       	t.distributionNetworkCharge as distributionNetworkCharge, ");
		sb.append("\n       	t.energyDemandCharge as energyDemandCharge, ");
		sb.append("\n       	t.activeEnergyCharge as activeEnergyCharge, ");
		sb.append("\n       	t.reactiveEnergyCharge as reactiveEnergyCharge, ");
		sb.append("\n       	t.rateRebalancingLevy as rateRebalancingLevy, ");
		sb.append("\n       	t.yyyymmdd as yyyymmdd ");
		*/
		sb.append("\n SELECT	t ");
		sb.append("\n FROM      Operator o, Customer c, Contract ct, TariffType tt,  TariffEM t");
		sb.append("\n WHERE o.id = :userId	");
		sb.append("\n AND o.loginId = c.loginId	");
		sb.append("\n AND c.id = ct.customer.id	");
		sb.append("\n AND ct.tariffIndex.id = tt.id	");
		sb.append("\n AND t.tariffType.id = tt.id	");
		
		if(yyyymmdd.length() > 0){
			sb.append("\n AND     t.yyyymmdd = :yyyymmdd ");
		}
		sb.append("\n ORDER BY t.yyyymmdd, t.tariffType.id, t.season.id ");

		Query query = getSession().createQuery(sb.toString());
		
		query.setString("userId", sUserId);

		if(yyyymmdd.length() > 0){
			query.setString("yyyymmdd", yyyymmdd);
		}

		//return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

		List<TariffEM> result = query.list();
		List<TariffEMVO> tariffemVOs = new ArrayList<TariffEMVO>();
		TariffEMVO tariffemVO = null;
		
		for (int i = 0; i < result.size(); i++) {
			tariffemVO = new TariffEMVO();
			TariffEM tariff = result.get(i);
			tariffemVO.setId(tariff.getId());
			tariffemVO.setTariffType(tariff.getTariffType() == null ? "" : tariff.getTariffType().getName());
			tariffemVO.setSeason(tariff.getSeason() == null ? "" : tariff.getSeason().getName());			
			tariffemVO.setSupplySizeMin(tariff.getSupplySizeMin());			
			tariffemVO.setSupplySizeMax(tariff.getSupplySizeMax());
			tariffemVO.setSupplySizeUnit(tariff.getSupplySizeUnit());
			tariffemVO.setCondition1(tariff.getCondition1());
			tariffemVO.setCondition2(tariff.getCondition2());
			tariffemVO.setPeakType(tariff.getPeakType() == null ? "" : tariff.getPeakType().name());
			tariffemVO.setServiceCharge(tariff.getServiceCharge());
			tariffemVO.setAdminCharge(tariff.getAdminCharge());
			tariffemVO.setTransmissionNetworkCharge(tariff.getTransmissionNetworkCharge());
			tariffemVO.setDistributionNetworkCharge(tariff.getDistributionNetworkCharge());
			tariffemVO.setEnergyDemandCharge(tariff.getEnergyDemandCharge());
			tariffemVO.setActiveEnergyCharge(tariff.getActiveEnergyCharge());
			tariffemVO.setReactiveEnergyCharge(tariff.getReactiveEnergyCharge());
			tariffemVO.setRateRebalancingLevy(tariff.getRateRebalancingLevy());
			tariffemVO.setYyyymmdd(tariff.getYyyymmdd());
			
			tariffemVOs.add(tariffemVO);
		}
		return tariffemVOs;
	}
	
	public int updateData(TariffEM tariffEM) throws Exception {
		
		Query query = null;
		StringBuffer sb = new StringBuffer();
		int result = 0;
		
		Double serviceCharge = tariffEM.getServiceCharge();
		Double adminCharge = tariffEM.getAdminCharge();
		Double transmissionNetworkCharge = tariffEM.getTransmissionNetworkCharge();
		Double distributionNetworkCharge = tariffEM.getDistributionNetworkCharge();
		Double energyDemandCharge = tariffEM.getEnergyDemandCharge();
		Double activeEnergyCharge = tariffEM.getActiveEnergyCharge();
		Double reactiveEnergyCharge = tariffEM.getReactiveEnergyCharge();
		Double rateRebalancingLevy = tariffEM.getRateRebalancingLevy();
		TariffType tariffType 	= tariffEM.getTariffType();
		
		
		try {		
			sb.append("\nUPDATE TariffEM t SET ");
			
			if(serviceCharge != null && !"NaN".equals(serviceCharge.toString())) {
				sb.append("\nt.serviceCharge = :serviceCharge,");
			}
			if(adminCharge != null && !"NaN".equals(adminCharge.toString())) {
				sb.append("\nt.adminCharge = :adminCharge,");
			}
			if(transmissionNetworkCharge != null && !"NaN".equals(transmissionNetworkCharge.toString())) {
				sb.append("\nt.transmissionNetworkCharge = :transmissionNetworkCharge,");
			}
			if(distributionNetworkCharge != null && !"NaN".equals(distributionNetworkCharge.toString())) {
				sb.append("\nt.distributionNetworkCharge = :distributionNetworkCharge,");
			}
			if(energyDemandCharge != null && !"NaN".equals(energyDemandCharge.toString())) {
				sb.append("\nt.energyDemandCharge = :energyDemandCharge,");
			}
			if(activeEnergyCharge != null && !"NaN".equals(activeEnergyCharge.toString())) {
				sb.append("\nt.activeEnergyCharge = :activeEnergyCharge,");
			}
			if(reactiveEnergyCharge != null && !"NaN".equals(reactiveEnergyCharge.toString())) {
				sb.append("\nt.reactiveEnergyCharge = :reactiveEnergyCharge,");
			}
			if(rateRebalancingLevy != null && !"NaN".equals(rateRebalancingLevy.toString())) {
				sb.append("\nt.rateRebalancingLevy = :rateRebalancingLevy,");
			}
			if(tariffType != null) {
				sb.append("\nt.tariffType.id = :tariffTypeId,");
			}
			
			sb.append("\nt.id = :id");
			sb.append("\nWHERE t.id = :id ");
			
			query = getSession().createQuery(sb.toString());
			
			if(serviceCharge != null && !"NaN".equals(serviceCharge.toString())) {
				query.setDouble("serviceCharge", serviceCharge);
			}
			if(adminCharge != null && !"NaN".equals(adminCharge.toString())) {
				query.setDouble("adminCharge", adminCharge);
			}
			if(transmissionNetworkCharge != null && !"NaN".equals(transmissionNetworkCharge.toString())) {
				query.setDouble("transmissionNetworkCharge", transmissionNetworkCharge);
			}
			if(distributionNetworkCharge != null && !"NaN".equals(distributionNetworkCharge.toString())) {
				query.setDouble("distributionNetworkCharge", distributionNetworkCharge);
			}
			if(energyDemandCharge != null && !"NaN".equals(energyDemandCharge.toString())) {
				query.setDouble("energyDemandCharge", energyDemandCharge);
			}
			if(activeEnergyCharge != null && !"NaN".equals(activeEnergyCharge.toString())) {
				query.setDouble("activeEnergyCharge", activeEnergyCharge);
			}
			if(reactiveEnergyCharge != null && !"NaN".equals(reactiveEnergyCharge.toString())) {
				query.setDouble("reactiveEnergyCharge", reactiveEnergyCharge);
			}
			if(rateRebalancingLevy != null && !"NaN".equals(rateRebalancingLevy.toString())) {
				query.setDouble("rateRebalancingLevy", rateRebalancingLevy);
			}
			if(tariffType != null) {
				query.setInteger("tariffTypeId", tariffType.getId());
			}
			query.setInteger("id", tariffEM.getId());

			result = query.executeUpdate();
			
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e,e);
		}
		return result;
	}
	
	/**
	 * method name : tariffDeleteByCondition
	 * method Desc : 조건을 받아서 tariffEM 테이블의 정보를 삭제한다.
	 * @return
	 */
	public int tariffDeleteByCondition(Map<String, Object> condition) {	
		Query query = null;
		StringBuffer sb = new StringBuffer();
		int result = 0;
		logger.info("seasonId: " + condition.get("seasonId"));
		logger.info("tariffEMId: " + condition.get("tariffEMId"));
		logger.info("tariffTypeId: " + condition.get("tariffTypeId"));
		try {
			Integer seasonId = 
					condition.get("seasonId") == null ? null :  (Integer) condition.get("seasonId");
			Integer tariffEMId =
					condition.get("tariffEMId") == null ? null : Integer.parseInt(condition.get("tariffEMId").toString());
			Integer tariffTypeId = 
					condition.get("tariffTypeId") == null ? null : Integer.parseInt(condition.get("tariffTypeId").toString());
			
			sb.append("\nDELETE FROM TariffEM em WHERE 1=1 ");
			if(tariffEMId != null && tariffEMId != -1) {
				sb.append("\nAND em.id = :tariffEMId");
			}
			if(seasonId != null && seasonId != -1) {
				sb.append("\nAND em.seasonId = :seasonId");
			}
			if(tariffTypeId != null) {
				sb.append("\nAND em.tariffTypeId = :tariffTypeId");
			}
			
			query = getSession().createQuery(sb.toString());
			if(tariffEMId != null && tariffEMId != -1) {
				query.setInteger("tariffEMId", tariffEMId);
			}
			if(seasonId != null && seasonId != -1) {
				query.setInteger("seasonId", seasonId);
			}
			if(tariffTypeId != null) {
				query.setInteger("tariffTypeId", tariffTypeId);
			}
			
			result = query.executeUpdate();
			
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e,e);
		}
		return result;
	}
	
	/**
	 * 계약애 해당하는 사용량에 따른 사용요금을 계산한다.
	 * @param condition
	 * <ul>
	 * <li> contractId : 계약ID
	 * <li> dateType : 조회기간구분(일/월) ,CommonConstants.DateType
	 * <li> startDate : yyyyMMdd or yyyyMM
	 * <li> endDate : yyyyMMdd or yyyyMM
	 * </ul>
	 * @return
	 */
	@SuppressWarnings("unused")
    public Double getUsageChargeByContract(Map<String, Object> condition){
		
		Contract contract = (Contract)condition.get("contract");
		String dateType = (String)condition.get("dateType");
		String startDate = ((String)condition.get("startDate")).substring(0,6)+"01";
		String endDate = (String)condition.get("endDate");
		int supplierId = contract.getSupplierId();
		
		Integer tariffTypeCode = contract.getTariffIndex().getCode();
		Map<String, Object> tariffParam = new HashMap<String, Object>();
//		Set<Condition> param = new HashSet<Condition>();
		Map<String, Object> param = new HashMap<String, Object>();
		tariffParam.put("tariffTypeCode", tariffTypeCode);
		tariffParam.put("tariffIndex", contract.getTariffIndex());
		tariffParam.put("searchDate", startDate);
		tariffParam.put("startDate", startDate);//매월 첫달부터 구해야
		tariffParam.put("endDate", endDate);
		tariffParam.put("contractNo", contract.getContractNumber());

//        param.add(new Condition("id.mdevType", new Object[]{DeviceType.Meter}, null, Restriction.EQ));

        String mdsId = (contract.getMeter() == null) ? null : contract.getMeter().getMdsId();
//        param.add(new Condition("id.mdevId", new Object[]{mdsId}, null, Restriction.EQ));
//        param.add(new Condition("id.channel", new Object[]{DefaultChannel.Usage.getCode()}, null, Restriction.EQ));
//        param.add(new Condition("id.yyyymm", new Object[]{startDate.substring(0,6)}, null, Restriction.EQ));
        param.put("mdevId", mdsId);
        param.put("mdevType", DeviceType.Meter);
        param.put("channel", DefaultChannel.Usage.getCode());
        param.put("yyyymm", startDate.substring(0,6));
        param.put("supplierId", supplierId);

		List<TariffEM> tariffEMList = getApplyedTariff(tariffParam);

		if(tariffEMList == null || tariffEMList.size() == 0){
			return 0d;
		}
		//MonthEM monthEM = null;
		MonthEMView monthEMView = null;
		
		DayEM dayEM = null;
		Double chargeSum = 10000d;
		int duration;
		double monthTotal = 0.0;
		
		if(startDate.length() < 8){
			startDate = startDate + "01";
		}
		if(endDate.length() < 8){
			endDate = endDate + "31";
		}
		try {
			duration = TimeUtil.getDayDuration(startDate, endDate); //요금계산하는 실제 사용기간			
			
//			 List<MonthEM> tempList = monthEMDao.getMonthEMsByListCondition(param);
			/*
			 * OPF-610 정규화 관련 처리로 인한 주석
			List<MonthEM> tempList = monthEMDao.getMonthEMbySupplierId(param);
			*/
			List<MonthEMView> tempList = monthEMViewDao.getMonthEMbySupplierId(param);
			
			
			 if( tempList != null && tempList.size() > 0) {
				 /* 
				  * OPF-610 정규화 관련 처리로 인한 주석
				 monthEM = (MonthEM)tempList.get(0);
				 monthTotal = Double.parseDouble(StringUtil.nullToZero(monthEM.getTotal())); // 그 달의 총 사용량을 취득한다.
				 */
				 
				 monthEMView = (MonthEMView)tempList.get(0);
				 monthTotal = Double.parseDouble(StringUtil.nullToZero(monthEMView.getTotal())); // 그 달의 총 사용량을 취득한다.
			 }
			
			//logger.debug("monthTotal=" +monthTotal);
			 
			if(monthEMView != null) {
				for(TariffEM tariffEM : tariffEMList){			
					
						if(tariffEM.getSupplySizeMin() != null || tariffEM.getSupplySizeMax() != null){
							if(tariffEM.getSupplySizeMin() != null && tariffEM.getSupplySizeMin() >= monthTotal) {
								chargeSum = tariffEM.getEnergyDemandCharge() == null ? 0d : tariffEM.getEnergyDemandCharge();
								if(tariffEM.getActiveEnergyCharge()!= null){
									chargeSum += monthTotal*tariffEM.getActiveEnergyCharge();
								}
							}
							if(tariffEM.getSupplySizeMin() != null && tariffEM.getSupplySizeMin() >= monthTotal && tariffEM.getSupplySizeMin() < monthTotal) {
								chargeSum = tariffEM.getEnergyDemandCharge() == null ? 0d : tariffEM.getEnergyDemandCharge();
								if(tariffEM.getActiveEnergyCharge()!= null){
									chargeSum += monthTotal*tariffEM.getActiveEnergyCharge();
								}
							}
							if(tariffEM.getSupplySizeMax() != null && tariffEM.getSupplySizeMin() != null && tariffEM.getSupplySizeMin() < monthTotal) {
								chargeSum = tariffEM.getEnergyDemandCharge() == null ? 0d : tariffEM.getEnergyDemandCharge()*duration/30;
								if(tariffEM.getActiveEnergyCharge()!= null){
									chargeSum += monthTotal*tariffEM.getActiveEnergyCharge();
								}
							}				
						}else{
							if(tariffEM.getSeason() != null){
								
								String seasonStart = startDate.substring(0,4)+tariffEM.getSeason().getSmonth() + tariffEM.getSeason().getSday();
								String seasonEnd = endDate.substring(0,4)+tariffEM.getSeason().getEmonth() + tariffEM.getSeason().getEday();
								
								if(tariffEM.getPeakType() != null){
									
									TOURate touRate = touRateDao.getTOURate(contract.getTariffIndex().getId(), 
																			tariffEM.getSeason().getId(), 
																			tariffEM.getPeakType());
									double peakTimeUsage = 0.0d;
									/*
									if(touRate != null && touRate.getStartTime()){
										//peak 시간대별 사용량 구해서 계산해야 함
										chargeSum += peakTimeUsage*tariffEM.getActiveEnergyCharge();
										dayEMDao.
									}
									*/
								}else{
									
									if(startDate.compareTo(seasonStart) >= 0 && endDate.compareTo(seasonEnd) <= 0){
										chargeSum = monthTotal*(tariffEM.getActiveEnergyCharge()==null?1:tariffEM.getActiveEnergyCharge());
									}
								}
							}else{
								
								if(tariffEM.getActiveEnergyCharge() != null){
									chargeSum = monthTotal*tariffEM.getActiveEnergyCharge();
								}
							}
							chargeSum += tariffEM.getEnergyDemandCharge()*duration/30;
						}
					}
			 } else {
				 chargeSum=0d;
			 }
		} catch (ParseException e) {
			logger.warn("getUsageChargeByContract parse Error!"+e);
		}
		//logger.debug("UsageChargeByContract="+chargeSum);
		return chargeSum;
	}
	
	@SuppressWarnings("unchecked")
	public List<TariffEM> getApplyedTariff(Map<String, Object> condition){

	    //Integer tariffTypeCode = (Integer)condition.get("tariffTypeCode");
		String searchDate = StringUtil.nullToBlank(condition.get("searchDate"));
		TariffType tariffIndex = (TariffType)condition.get("tariffIndex");
		
		//if(tariffIndex != null){
		//	logger.debug("tariffTypeCode"+tariffIndex.getName());
		//}

		//logger.debug("searchDate"+ searchDate);
		String qstr = "select max(yyyymmdd) FROM TariffEM " +
                "WHERE tariffType.id = :tariffId";
		if (!searchDate.isEmpty()) {
		    qstr += "\n AND yyyymmdd <= :searchDate ";
		}

		Query query = getSession().createQuery(qstr);
		query.setInteger("tariffId", tariffIndex.getId());
		if (!searchDate.isEmpty()) {
		    query.setString("searchDate", searchDate);
		}
		// query.setParameterList("CHANNELS", sysChannels);

		searchDate = (String)query.uniqueResult();
		
		StringBuffer sb = new StringBuffer();
		sb.append("\n FROM  TariffEM ");
		sb.append("\n WHERE tariffType.id = :tariffId");
		if ( !searchDate.isEmpty() ) {
			sb.append("\n AND yyyymmdd = :searchDate  ");
		}

		query = getSession().createQuery(sb.toString());
		query.setInteger("tariffId", tariffIndex.getId());
		if ( !searchDate.isEmpty() ) {
			query.setString("searchDate", searchDate);
		}
		List<TariffEM> tariffEMList = query.list();

		return tariffEMList;
	}

    /**
     * method name : getPrepaymentTariff
     * method Desc : 고객 선불관리 화면에서 요금단가를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getPrepaymentTariff(Map<String, Object> conditionMap){
//        Integer supplierId = (Integer)conditionMap.get("supplierId");

        StringBuilder sb = new StringBuilder();
        sb.append("\nSELECT e.supplySizeMin AS supplySizeMin, ");
        sb.append("\n       e.supplySizeMax AS supplySizeMax, ");
        sb.append("\n       e.condition1 AS condition1, ");
        sb.append("\n       e.condition2 AS condition2, ");
        sb.append("\n       e.serviceCharge AS serviceCharge, ");
        sb.append("\n       e.transmissionNetworkCharge AS transmissionNetworkCharge, ");
        sb.append("\n       e.energyDemandCharge AS energyDemandCharge, ");
        sb.append("\n       e.rateRebalancingLevy AS rateRebalancingLevy ");
        sb.append("\nFROM TariffEM e ");
        sb.append("\nWHERE e.tariffType.code = 9100 "); // 선불
//        sb.append("\nAND   e.tariffType.id = t.id ");
        sb.append("\nORDER BY e.id ");

        Query query = getSession().createQuery(sb.toString());

        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

    /* (non-Javadoc)
     * @see com.aimir.dao.system.TariffEMDao#saveEmBillingDailyWithTariffEM(com.aimir.model.system.Contract)
     */
    /*
     * 선불계산로직은 스케줄러로 분리
    public Double saveEmBillingDailyWithTariffEM(Contract contract) {
        // TODO - 프로젝트별 요금제 분기. 추후 개발
        // SPASA 요금제(TOU)
        // SPASA 에서는 선불요금만 계산하므로 분기없이 바로 호출.
        return this.saveEmBillingDailyWithTariffEMSpasa(contract);
    }
*/
    /**
     * 선후불 체크. SPASA는 선불요금만 계산한다.
     * 
     * @param contract
     * @return
     */
    /*
     * 
     *선불계산로직은 스케줄러로 분리
    public Double saveEmBillingDailyWithTariffEMSpasa(Contract contract) {
        int rateType = 0;  // 0: none, 1: 단가계산, 2: TOU
        Double rtnVal = 0d;
        Code code = contract.getCreditType();
        
        if(Code.PREPAYMENT.equals(code.getCode()) || Code.EMERGENCY_CREDIT.equals(code.getCode())) { // 선불 요금일 경우
            rateType = 3;//남아공 1, 가나 3
        }
//        else {    // 후불 요금일 경우
//            rateType = 1;
//        }
        //System.out.println(" % rate Type : " + rateType);
        switch(rateType) {
            case 1:
                // 단가방식 계산
                rtnVal = this.saveEmBillingDailyWithTariffEMUnitCost(contract);
                break;
                
//            case 2:
//                // TOU방식 계산. EM 호출
//                rtnVal = tariffEMDao.saveEmBillingDailyWithTariffEM(contract);
//                break;
            case 3:
            	rtnVal = this.saveEmBillingDailyWithTariffEMCumulationCost(contract);
            	break;

        }

        return rtnVal;
    }
     */
    /**
     * Ghana 선불요금에 적용되는 요금계산
     * 단가 방식으로 사용량의 요금을 계산한다.
     * 
     * BillDate이 1일이라는 가정하에 성립한다.
     * 
     * @param contract
     * @return
     */
    /*
     * 선불계산로직은 스케줄러로 분리
    private Double saveEmBillingDailyWithTariffEMCumulationCost(Contract contract) {
    	SimpleDateFormat sd = new SimpleDateFormat("yyyyMMddHHmmss");
    	
    	String mdsId = contract.getMeter().getMdsId();
    	String currentDate = sd.format(new Date()).substring(0,10);
    	
    	//선불 로그 테이블에 저장할 금액
    	BigDecimal prepayLogCredit = new BigDecimal("0");
    	//선불 로그 테이블에 저장할 사용량
    	Double prepayLogUsage = 0d;
    	//차감스케줄을 저장할 변수
    	PrepaymentLog prepaymentLog = new PrepaymentLog();
    	
    	// Billing_Day_Em에 정보 등록
    	List<BillingDayEM> billingDayEMList = null;
    	BillingDayEM _billingDayEM = null;
    	// 검침값이 2시간이 밀려서 들어오므로 누적액이 저장된 마지막 billing정보의 갱신도 필요하다.
    	List<BillingDayEM> lastBillingDayEMList = null;
    	BillingDayEM _lastBillingDayEM = null;
    	
    	 TransactionStatus txStatus = null;
         DefaultTransactionDefinition txDefine = new DefaultTransactionDefinition();
         txDefine.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
    	try {
    		txStatus = transactionManager.getTransaction(txDefine);
	    	//해당 Tariff 정보를 가져온다.
	    	List<TariffEM> tariffEMList = this.getTariffEMInfo(contract); 
	    	
	    	//과금일을 가져온다.
	    	SupplyType supplyType = supplyTypeDao.getSupplyTypeBySupplierIdTypeId(contract.getSupplierId(), codeDao.getCodeIdByCode("3.1")).get(0);
	    	
	    	//마지막 누적요금이 저장된 데이터를 가지고 온다.
	    	List<Map<String, Object>> billingDayEM = billingDayEMDao.getLastAccumulateBill(mdsId);
	    	//마지막 누적요금 저장
	    	BigDecimal preAccumulateBill = null;
	    	//마지막 누적사용량 저장
	    	Double preAccumulateUsage = null;
	    	//마지막 누적요금이 저장된 날짜
	    	String lastAccumulateDate = null;
	    	//마지막 누적요금이 저장된 달의 마지막 날
	    	String lastAccumulateLastDate = null;
	    	//하루전날 누적요금이 저장된 데이터를 가지고 온다.
	    	List<Map<String, Object>> preDaybillingDayEM = null;
	    	
	    	if(billingDayEM.size() <= 0) {
	    		//마지막 누적 요금이 저장된 billingDayEM이 없을 경우 한번도 선불스케줄을 돌리지 않은것으로 간주
	    		preAccumulateBill = new BigDecimal("0");
	    		preAccumulateUsage = 0.0;
	    		lastAccumulateDate = monthEMDao.getMonthByMinDate(mdsId).get(0).get("YYYYMM").toString()+"00";
	    	} else {
	    		preAccumulateBill = new BigDecimal(Double.parseDouble(billingDayEM.get(0).get("ACCUMULATEBILL").toString()));
	    		preAccumulateUsage = Double.parseDouble(billingDayEM.get(0).get("ACCUMULATEUSAGE").toString());
	    		lastAccumulateDate = billingDayEM.get(0).get("YYYYMMDD").toString();
	    	}
	    	logger.info("last save preAccumulateBill : "+ preAccumulateBill);
	    	
	    	//마지막 누적요금이 계산된 달부터 오늘까지의 MonthEM
			Set<Condition> condition = new HashSet<Condition>();
	    	condition.add(new Condition("id.mdevType", new Object[]{DeviceType.Meter}, null, Restriction.EQ));
	    	condition.add(new Condition("id.mdevId", new Object[]{mdsId}, null, Restriction.EQ));
	    	condition.add(new Condition("id.channel", new Object[]{DefaultChannel.Usage.getCode()}, null, Restriction.EQ));
	    	condition.add(new Condition("id.yyyymm", new Object[]{lastAccumulateDate.substring(0,6)}, null, Restriction.GE));
	    	List<MonthEM> monthEM = monthEMDao.getMonthEMsByListCondition(condition);
	    	
	    	//1달이상의 요금이 계산되지 않았더라도 각 달의 과금일 전날까지의 요금을 계산하기 위함
	    	//Start MonthEM for
	    	for (int i = 0; i < monthEM.size(); i++) {
		    	//각 날짜별로 사용량을 저장함
		    	Double monthEMValue[] = this.getMonthValue31(monthEM.get(i));
		    	//1일 부터 오늘까지의 사용량이 누적되어 있음
		    	BigDecimal monthTotalBig = new BigDecimal(monthEM.get(i).getTotal());
				//계산된 bill값
				BigDecimal accumulateBill = new BigDecimal(0d);
				//하루치 bill값에 해당하는 날짜
				String billDay = null;
				
				//여러 달 동안 과금되지 않았을때 마지막 누적액을 다시 리셋시킨다.
				if(i >= 1) {
					preAccumulateBill = new BigDecimal("0");
					preAccumulateUsage = 0d;
				}
				
				lastAccumulateLastDate = TimeUtil.getPreMonth(lastAccumulateDate.substring(0,6)+"01000000",-i);
				lastAccumulateLastDate = lastAccumulateLastDate.substring(0,6)+CalendarUtil.getMonthLastDate(lastAccumulateLastDate.substring(0,4), lastAccumulateLastDate.substring(4,6))+"02";
				  
				
				if(Integer.parseInt(lastAccumulateLastDate.substring(0,8)) < Integer.parseInt(currentDate.substring(0,8))) {
					billDay = lastAccumulateLastDate;
				} else {
					billDay = currentDate;
				}
				
		    	//누적액(accumulateBill) 계산 로직
				logger.info("-----accumulateBill");
				accumulateBill = blockBill(tariffEMList, monthTotalBig);
				logger.info("-----accumulateBill : " + accumulateBill);
				
				
		    	//BillingDayEM의 bill 계산 로직
		    	//전날까지의 사용량 (monthTotal - 오늘 사용량)
				logger.info("-----preDayUsage");
		    	BigDecimal preDayAccumulateUsage =  monthTotalBig.subtract(new BigDecimal(monthEMValue[Integer.parseInt(billDay.substring(6,8))-1] == null ? 0 : monthEMValue[Integer.parseInt(billDay.substring(6,8))-1]));
		    	logger.info("-----preDayUsage : " + preDayAccumulateUsage);
		    	
		    	//전날 누적액
		    	BigDecimal preDayAccumulateBill =  new BigDecimal("0");
		    	logger.info("-----preDayAccumulateBill");
		    	preDayAccumulateBill = blockBill(tariffEMList, preDayAccumulateUsage);
		    	logger.info("-----preDayAccumulateBill : " + preDayAccumulateBill);
		    	
		    	logger.info("----------------- BillingDayEM save Start ----------------------");
		    	//마지막으로 billingDayEM을 저장한 날의 정보를 갱신하기 위함
		    	if(lastAccumulateDate.substring(0,6).equals(monthEM.get(i).getYyyymm())) {
			    	Set<Condition> condition3 = new HashSet<Condition>();
			    	condition3.add(new Condition("id.mdevType", new Object[]{DeviceType.Meter}, null, Restriction.EQ));
			    	condition3.add(new Condition("id.mdevId", new Object[]{mdsId}, null, Restriction.EQ));
			    	condition3.add(new Condition("id.yyyymmdd", new Object[]{lastAccumulateDate.substring(0,8)}, null, Restriction.EQ));
			    	
			    	lastBillingDayEMList = billingDayEMDao.findByConditions(condition3);
			    	_lastBillingDayEM = lastBillingDayEMList.size() <= 0 ? null : lastBillingDayEMList.get(0);
			    	if(_lastBillingDayEM != null) {
				    	Double lastBillingDayAccumulateUsage = 0d;
				    	Double lastBillingPreDayAccumulateUsage = 0d;
				    	
				    	for(int j=0; j < Integer.parseInt(lastAccumulateDate.substring(6,8)); j++) {
				    		lastBillingDayAccumulateUsage += monthEMValue[j];
				    	}
				    	lastBillingPreDayAccumulateUsage = lastBillingPreDayAccumulateUsage - monthEMValue[Integer.parseInt(lastAccumulateDate.substring(6,8))-1];
				    	
				    	BigDecimal _lastAccumulateBill = blockBill(tariffEMList, new BigDecimal(lastBillingDayAccumulateUsage))
				    			.subtract(blockBill(tariffEMList, new BigDecimal(lastBillingPreDayAccumulateUsage)));
				    	
				    	if((Integer.parseInt(lastAccumulateDate.substring(6,8))) > 1) {
			    			_lastBillingDayEM.setBill(_lastAccumulateBill.doubleValue() - 
				    				blockBill(tariffEMList, new BigDecimal(monthEMValue[Integer.parseInt(lastAccumulateDate.substring(6,8))-2])).doubleValue());
			    		} else if((Integer.parseInt(lastAccumulateDate.substring(6,8))) == 1) {
			    			_lastBillingDayEM.setBill(
				    				blockBill(tariffEMList, new BigDecimal(monthEMValue[Integer.parseInt(lastAccumulateDate.substring(6,8))-1])).doubleValue());
			    		}
			    		_lastBillingDayEM.setAccumulateBill(_lastAccumulateBill.doubleValue());
			    		_lastBillingDayEM.setAccumulateUsage(lastBillingDayAccumulateUsage.doubleValue());
				    	
				    	billingDayEMDao.saveOrUpdate(_lastBillingDayEM);
				    	
			    	}
		    	}
		    	/////////////////////////////////////
		    	
		    	
		    	Set<Condition> condition2 = new HashSet<Condition>();
		    	condition2.add(new Condition("id.mdevType", new Object[]{DeviceType.Meter}, null, Restriction.EQ));
		    	condition2.add(new Condition("id.mdevId", new Object[]{mdsId}, null, Restriction.EQ));
		    	condition2.add(new Condition("id.yyyymmdd", new Object[]{billDay.substring(0,8)}, null, Restriction.EQ));
		    	
		    	billingDayEMList = billingDayEMDao.findByConditions(condition2);
		    	_billingDayEM = billingDayEMList.size() <= 0 ? new BillingDayEM() : billingDayEMList.get(0);
		    	//계산중인 날의 누적요금
		    	BigDecimal beforeAccumulateBill = _billingDayEM.getAccumulateBill() == null ? new BigDecimal("0") : new BigDecimal(_billingDayEM.getAccumulateBill());
		    	//과금일부터 누적금액을 reset 해준다.
	    		if (_billingDayEM.getMDevId() != null) {
	    			
	    			logger.info("-----before reset-----");
	    			logger.info("setBill : " + _billingDayEM.getBill());
	    			logger.info("setAccumulateBill : " + beforeAccumulateBill);
	    			logger.info("setAccumulateUsage : " + _billingDayEM.getAccumulateUsage());
	    			
	            	if(Integer.parseInt(billDay.substring(6,8)) == Integer.parseInt(supplyType.getBillDate())) {
	            		//과금일(1일)의 경우 
	            		_billingDayEM.setBill(accumulateBill.doubleValue());
	            		_billingDayEM.setAccumulateBill(accumulateBill.doubleValue());
	            		_billingDayEM.setAccumulateUsage(monthTotalBig.doubleValue());
	            	} else {
		            	//현재누적액 - 어제 누적액
	            		_billingDayEM.setBill(accumulateBill.doubleValue() - preDayAccumulateBill.doubleValue());
		            	_billingDayEM.setAccumulateBill(accumulateBill.doubleValue());
		            	_billingDayEM.setAccumulateUsage(monthTotalBig.doubleValue());
	            	}
	               
	            } else {
	            	_billingDayEM.setMDevId(mdsId);
	                _billingDayEM.setYyyymmdd(billDay.substring(0,8));
	                _billingDayEM.setHhmmss("000000");
	                _billingDayEM.setMDevType(DeviceType.Meter.name());
	                _billingDayEM.setContract(contract);
	                _billingDayEM.setSupplier(contract.getSupplier());
	                _billingDayEM.setLocation(contract.getLocation());
	                _billingDayEM.setMeter(contract.getMeter());
	                _billingDayEM.setModem((contract.getMeter() == null) ? null : contract.getMeter().getModem());
	                _billingDayEM.setWriteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
	                _billingDayEM.setAccumulateUsage(monthTotalBig.doubleValue());
	                
	                if(Integer.parseInt(billDay.substring(6,8)) == Integer.parseInt(supplyType.getBillDate())) {
	                	//과금일(1일)의 경우 
	                	_billingDayEM.setBill(accumulateBill.doubleValue());
	            		_billingDayEM.setAccumulateBill(accumulateBill.doubleValue());
	            	} else {
	            		//현재누적액 - 어제누적액
		                _billingDayEM.setBill(accumulateBill.doubleValue() - preDayAccumulateBill.doubleValue());
		                _billingDayEM.setAccumulateBill(accumulateBill.doubleValue());
		              
	            	}
	                
	            }
		        
		        billingDayEMDao.saveOrUpdate(_billingDayEM);
		        logger.info("-----after reset-----");
            	logger.info("setBill : " + _billingDayEM.getBill());
        		logger.info("setAccumulateBill : " + _billingDayEM.getAccumulateBill());
        		logger.info("setAccumulateUsage : " + _billingDayEM.getAccumulateUsage());
		        
		        logger.info("CurrentCredit before subtraction: " + contract.getCurrentCredit());
                logger.info("befor prepayLogCredit: " + prepayLogCredit);
		        BigDecimal bdCurCredit = (contract.getCurrentCredit() != null) ? new BigDecimal(contract.getCurrentCredit()) : new BigDecimal(0d);
		        
		        if((billDay.substring(0,8)).equals(billDay.substring(0,6)+CalendarUtil.getMonthLastDate(billDay.substring(0,4), billDay.substring(4, 6)))) {
		        	//달의 마지막날일때
		        	if((i+1 == monthEM.size())) {
		        		//오늘날짜의 금액을 차감중일때
		        		if(currentDate.substring(8,10).equals("23")) {
		        			//오늘날짜의 금액을 차감하는데 23시일때 (하루 한번만 세금을 차감하기 위함)
		        			contract.setCurrentCredit(bdCurCredit.doubleValue() - (accumulateBill.doubleValue() - preAccumulateBill.doubleValue()) - tariffEMList.get(0).getServiceCharge());
		        			prepayLogCredit = new BigDecimal(prepayLogCredit.doubleValue() + (accumulateBill.doubleValue() - preAccumulateBill.doubleValue()) + tariffEMList.get(0).getServiceCharge());
		        			
		        		} else {
		        			contract.setCurrentCredit(bdCurCredit.doubleValue() - (accumulateBill.doubleValue() - preAccumulateBill.doubleValue()));
		        			prepayLogCredit = new BigDecimal(prepayLogCredit.doubleValue() + (accumulateBill.doubleValue() - preAccumulateBill.doubleValue()));
		        		}
		        	} else {
		        		//예전에 차감되지 못한 달의 금액을 차감중 일때
		        		if(!(preAccumulateBill.doubleValue() == accumulateBill.doubleValue())) { 
		        			contract.setCurrentCredit(bdCurCredit.doubleValue() - (accumulateBill.doubleValue() - preAccumulateBill.doubleValue()) - tariffEMList.get(0).getServiceCharge());
		        			prepayLogCredit = new BigDecimal(prepayLogCredit.doubleValue() + (accumulateBill.doubleValue() - preAccumulateBill.doubleValue()) + tariffEMList.get(0).getServiceCharge());
		        		}
		        	}
		        } else {
		        	//마지막 날이 아닐경우 (세금을 빼지 않는다.)
		        	contract.setCurrentCredit(bdCurCredit.doubleValue() - (accumulateBill.doubleValue() - preAccumulateBill.doubleValue()));
        			prepayLogCredit = new BigDecimal(prepayLogCredit.doubleValue() + (accumulateBill.doubleValue() - preAccumulateBill.doubleValue()));
		        }
                logger.info("after prepayLogCredit : " + prepayLogCredit);	
		    	logger.info("CurrentCredit after subtraction : " + contract.getCurrentCredit());    
		    	
	    		if(monthEM.size() > 1) {
	    			//두달이상 차감지연의 경우
	    			if(!(preAccumulateBill.doubleValue() == accumulateBill.doubleValue())) { 
	    				prepayLogUsage = prepayLogUsage + (monthTotalBig.doubleValue() - preAccumulateUsage);
	    			}
	    		} else {

	    			prepayLogUsage = monthTotalBig.doubleValue() - preAccumulateUsage;
	    		}
		    	
		    	logger.info("["+ i +"] prepayLogCredit : " + prepayLogCredit);
		    	logger.info("["+ i +"] prepayLogCreditMAth : " + prepayLogCredit.doubleValue());
		    	logger.info("["+ i +"] prepayLogUsage : " + prepayLogUsage);
		    	
		    	

	    	} //End MonthEM For
	    	
	    	prepaymentLog.setUsedConsumption(prepayLogUsage);
	    	prepaymentLog.setBalance(contract.getCurrentCredit());
	    	prepaymentLog.setChargedCredit(Double.parseDouble("0"));
	    	prepaymentLog.setLastTokenDate(sd.format(new Date()));
	    	prepaymentLog.setContract(contract);
	    	prepaymentLog.setCustomer(contract.getCustomer());
	    	prepaymentLog.setUsedCost(Double.parseDouble(prepayLogCredit.toString()));
	    	prepaymentLog.setLocation(contract.getLocation());
        	prepaymentLog.setTariffIndex(contract.getTariffIndex());
	    	
	    	prepaymentLogDao.add(prepaymentLog);
	    	
	    	transactionManager.commit(txStatus);
    	}catch (Exception e) {
    		 logger.error(e,e);
             transactionManager.rollback(txStatus);
    	}
    	
    	return _billingDayEM == null ? 0  : _billingDayEM.getBill();
    }
    
    //선불계산로직은 스케줄러로 분리
    private BigDecimal blockBill(List<TariffEM> tariffEMList, BigDecimal usage) {
    	BigDecimal supplyMin = null;
    	BigDecimal supplyMax = null;
    	//해당구간에서 사용한 사용량
    	BigDecimal diffBig = new BigDecimal("0");
    	//남은 사용량(사용량 - 계산완료된 사용량)
    	BigDecimal resultUsageBig = new BigDecimal("0");
    	BigDecimal returnBill = new BigDecimal("0");
    	
    	for(int cnt=0 ; cnt < tariffEMList.size(); cnt++){
    		supplyMin = new BigDecimal(tariffEMList.get(cnt).getSupplySizeMin() == null ? 0d : tariffEMList.get(cnt).getSupplySizeMin());
    		supplyMax = new BigDecimal(tariffEMList.get(cnt).getSupplySizeMax() == null ? 0d : tariffEMList.get(cnt).getSupplySizeMax());
    		
    		logger.info("[" + cnt + "] supplyMin : " + supplyMin + ", supplyMax : " + supplyMax);
    		
    		//Tariff 첫 구간 
    		if(tariffEMList.get(cnt).getSupplySizeMin() == null || tariffEMList.get(cnt).getSupplySizeMin() == 0) {
				if (tariffEMList.get(cnt).getSupplySizeMin() == null) {
					tariffEMList.get(cnt).setSupplySizeMin(Double.parseDouble("0"));
					supplyMin = new BigDecimal(0);
				}
				diffBig = supplyMax.subtract(supplyMin);
			} else {
				if (tariffEMList.get(cnt).getSupplySizeMax() == null) {
					//Tariff 마지막 구간
					diffBig = supplyMin;
				} else {
					diffBig = supplyMax.subtract(supplyMin).add(new BigDecimal(1));
				}
			}
			if(usage.compareTo(supplyMin) >= 0) {
					if (cnt == 0) {
//						 처음 한번만..
						resultUsageBig = usage.subtract(diffBig);
						if(resultUsageBig.compareTo(new BigDecimal("0")) < 0) {
							diffBig = usage;
						}
					} else {
						if(resultUsageBig.compareTo(diffBig) >= 0) {
							resultUsageBig = resultUsageBig.subtract(diffBig);
						} else {
							diffBig = resultUsageBig;
						}
					}
					
					logger.info("diffBig : " + diffBig);
					
					//사용량 * 단가
					returnBill = returnBill.add(diffBig.multiply(new BigDecimal(tariffEMList.get(cnt).getActiveEnergyCharge() == null ? 0d : tariffEMList.get(cnt).getActiveEnergyCharge())));
					logger.info("ActiveEnergyCharge: " + tariffEMList.get(cnt).getActiveEnergyCharge());
			}
    	}
    	
    	
    	return returnBill;
    }
*/
    /**
     * SPASA 선불요금에 적용되는 요금계산
     * 단가 방식으로 사용량의 요금을 계산한다.
     * 
     * @param contract
     * @return
     */
    /*
     * 선불계산로직은 스케줄러로 분리
    private Double saveEmBillingDailyWithTariffEMUnitCost(Contract contract) {
        Double dataValue[] = null;
        Double bill = 0d;
        // 정확한 숫자계산을 위해 BigDecimal 사용
        BigDecimal bdBill = null;                   // 사용요금
        BigDecimal bdCurBill = null;                // 기존요금
        BigDecimal bdSumBill = new BigDecimal(0d);  // 사용요금의 합
        BigDecimal bdUsage = null;                   // 사용량
        BillingDayEM _billingDayEM = null;
        String saveReadFromDateYYYYMMDDHHMMSS = null;
        String saveReadToDateYYYYMMDDHHMMSS = null;
        String newReadFromDateYYYYMMDDHHMMSS = null;    // 마지막 일자의 새로 읽을 일자시간

        TransactionStatus txStatus = null;
        DefaultTransactionDefinition txDefine = new DefaultTransactionDefinition();
        txDefine.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        // 미터시리얼 번호를 취득한다.
        String mdsId = (contract.getMeter() == null) ? null : contract.getMeter().getMdsId();

        // 계약에 해당하는 요금 정보 취득
        List<TariffEM> tariffEMList = this.getTariffEMInfo(contract);

        // 가장 최근에 갱신한 일별 빌링 정보 취득
        Map<String, Object> map = billingDayEMDao.getLast(contract.getId());

        BillingDayEM billingDayEM = new BillingDayEM();
        billingDayEM.setContract(contract);

        // TODO - 미터기의 Element 를 조회한다. 단상/3상에 따라 요금이 달라짐. 추후 재확인
        EnergyMeter energyMeter = (EnergyMeter)contract.getMeter();
        Code element = energyMeter.getMeterElement();
        // 코드를 체크해서 단상일 경우/3상일 경우를 분기한다. 미개발

        try{
            txStatus = transactionManager.getTransaction(txDefine);
            // 사용량 읽은 마지막 날짜 취득
            String readToDate = TimeUtil.getCurrentDay() + "000000";
            newReadFromDateYYYYMMDDHHMMSS = TimeUtil.getCurrentDay() + "000000";

            if(map != null) { // 빌링에 정보가 없을때는 가장 최근의(오늘) DayEM으로 부터 빌링정보를 등록한다.
                // 사용량 읽은 마지막 날짜 취득
                if((String)map.get("usageReadToDate") != null) {
                    readToDate = (String)map.get("usageReadToDate");
                    newReadFromDateYYYYMMDDHHMMSS = TimeUtil.getPreHour(readToDate, -1);
                } else {
                    // 사용량 읽은 마지막 날짜가 null 일 경우 모두 읽은 것으로 간주함.
                    readToDate = (String)map.get("lastDay") + "230000";
                    newReadFromDateYYYYMMDDHHMMSS = TimeUtil.getPreHour(readToDate, -1);
                }
            }

            // 일별 에너지 사용량 취득(빌링정보 저장한 다음 시간부터 일별 사용량을 취득한다.)
            Set<Condition> param = new HashSet<Condition>();
            param.add(new Condition("id.mdevType", new Object[]{DeviceType.Meter}, null, Restriction.EQ));
            param.add(new Condition("id.mdevId", new Object[]{mdsId}, null, Restriction.EQ));
            param.add(new Condition("id.channel", new Object[]{DefaultChannel.Usage.getCode()}, null, Restriction.EQ));
            param.add(new Condition("id.yyyymmdd", new Object[]{readToDate.substring(0,8)}, null, Restriction.GE));
            param.add(new Condition("id.yyyymmdd", new Object[]{readToDate.substring(0,8)}, null, Restriction.ORDERBYDESC));
            List<DayEM> dayEM = dayEMDao.getDayEMsByListCondition(param);

            String readToDateYYYYMMDD = readToDate.substring(0,8);                          // 마지막 읽은 일자
            String newReadFromDateHH = newReadFromDateYYYYMMDDHHMMSS.substring(8,10);       // 마지막 읽은 날의 새로 읽을 시간
            int intNewReadFromDateHH = Integer.parseInt(newReadFromDateHH);                 // 마지막 읽은 날의 새로 읽을 시간 int
            String saveReadFromDateHH = null;   // 저장할 읽은 시작시간
            String saveReadToDateHH = "23";     // 저장할 읽은 종료시간

            boolean flg = true;

            if (dayEM.size() != 0) {
                for (int i = 0 ; i < dayEM.size() ; i++) {
                    bdUsage = new BigDecimal(0d);
                    bdBill = new BigDecimal(0d);
                    dataValue =  this.getDayValue24(dayEM.get(i));
                    billingDayEM.setYyyymmdd(dayEM.get(i).getYyyymmdd());
                    List<BillingDayEM> list_billingDayEM = billingDayEMDao.getBillingDayEMs(billingDayEM, null, null);
                    _billingDayEM = (list_billingDayEM.size() != 0) ? list_billingDayEM.get(0) : null;

                    bdCurBill = _billingDayEM != null ? new BigDecimal(_billingDayEM.getBill()) : new BigDecimal(0d);

//                    System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
//                    System.out.println("bdCurBill : " + bdCurBill);
//                    System.out.println("bdCurBill.doubleValue() : " + bdCurBill.doubleValue());
//                    System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");

                    // 마지막 읽은 날의 남은 시간에 대한 사용량을 더한다.
                    // 예) 10일 15시까지 읽었으면 이번에는 10일 16시의 사용량부터 계산하도록 하기 위해서
                    if (readToDateYYYYMMDD.equals(dayEM.get(i).getYyyymmdd())) {
                        if (intNewReadFromDateHH == 0) {
                            // 새로 읽을 시간이 0 이면 skip.(마지막 읽은 시간이 23시임)
                            continue;
                        }

                        for (int j = 0; j < dataValue.length; j++) {
                            if (intNewReadFromDateHH <= j) {
                                if (flg) {
                                    saveReadFromDateHH = newReadFromDateHH;
                                    flg = false;
                                }

                                bdUsage = bdUsage.add(new BigDecimal(dataValue[j]));
                            }
                        }
                        flg = true;

                        saveReadFromDateYYYYMMDDHHMMSS = dayEM.get(i).getYyyymmdd() + saveReadFromDateHH + "0000";
                    } else { // 마지막 읽은 날짜와 같지 않을 경우는 전체 사용량을 읽는다.
                        saveReadFromDateYYYYMMDDHHMMSS = dayEM.get(i).getYyyymmdd() + "000000";
                        bdUsage = bdUsage.add(new BigDecimal(dayEM.get(i).getTotal()));
                    }

                    for (int k = dataValue.length -1 ; k >= 0 ; k--) {
                        if (dataValue[k] != 0.0) {
                            saveReadToDateHH = (String.valueOf(k).length() == 1 ? "0"+k : String.valueOf(k)); // 마지막 시간 취득
                            break;
                        }
                    }

                    saveReadToDateYYYYMMDDHHMMSS = dayEM.get(i).getYyyymmdd() + saveReadToDateHH + "0000";

                    // 사용요금 계산
                    if (tariffEMList != null && tariffEMList.size() > 0) {
                        TariffEM tariffEM = (TariffEM)tariffEMList.get(0);
                        bdBill = bdUsage.multiply(new BigDecimal(tariffEM.getActiveEnergyCharge()));

                        // 세금 계산
                        if (tariffEM.getRateRebalancingLevy() != null) {
                            bdBill = bdBill.add(bdUsage.multiply(new BigDecimal(tariffEM.getRateRebalancingLevy())));
                        }
                    }

                    bdSumBill = bdSumBill.add(bdBill); // 계산된 요금을 더한다.

                    // Billing_Day_Em에 정보 등록
                    if (_billingDayEM != null) {
                        _billingDayEM.setBill(bdCurBill.add(bdBill).doubleValue());
                        _billingDayEM.setUsageReadFromDate(saveReadFromDateYYYYMMDDHHMMSS);
                        _billingDayEM.setUsageReadToDate(saveReadToDateYYYYMMDDHHMMSS);
                    } else {
                        _billingDayEM = new BillingDayEM();
                        _billingDayEM.setYyyymmdd(saveReadToDateYYYYMMDDHHMMSS.substring(0,8));
                        _billingDayEM.setHhmmss("000000");
                        _billingDayEM.setMDevType(DeviceType.Meter.name());
                        _billingDayEM.setMDevId(mdsId);
                        _billingDayEM.setContract(contract);
                        _billingDayEM.setSupplier(contract.getSupplier());
                        _billingDayEM.setLocation(contract.getLocation());
                        _billingDayEM.setBill(bdCurBill.add(bdBill).doubleValue());
                        _billingDayEM.setActiveEnergyRateTotal(bdUsage.doubleValue());
                        _billingDayEM.setMeter(contract.getMeter());
                        _billingDayEM.setModem((contract.getMeter() == null) ? null : contract.getMeter().getModem());
                        _billingDayEM.setWriteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
                        _billingDayEM.setUsageReadFromDate(saveReadFromDateYYYYMMDDHHMMSS);
                        _billingDayEM.setUsageReadToDate(saveReadToDateYYYYMMDDHHMMSS);
                    }

//                    System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
//                    System.out.println("bdCurBill + bdBill : " + bdCurBill.add(bdBill).doubleValue());
//                    System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");

                    list_billingDayEM.clear();
                    billingDayEMDao.saveOrUpdate(_billingDayEM);
                }

                // contract 정보 갱신
                Code code = contract.getCreditType();
                if(Code.PREPAY.equals(code.getCode()) || Code.EMERGENCY_CREDIT.equals(code.getCode())) { // 선불 요금일 경우
//                    logger.info("###################################################################################");
//                    logger.info("bill : " + bdBill.doubleValue());
//                    logger.info("sumBill : " + bdSumBill.doubleValue());
//                    logger.info("currentCredit : " + contract.getCurrentCredit());
//                    logger.info("###################################################################################");
                    BigDecimal bdCurCredit = (contract.getCurrentCredit() != null) ? new BigDecimal(contract.getCurrentCredit()) : new BigDecimal(0d);

                    // 현재 잔액에서 사용요금을 차감한다.
                    contract.setCurrentCredit(bdCurCredit.subtract(bdSumBill).doubleValue());
                    contractDao.saveOrUpdate(contract);
                }
                transactionManager.commit(txStatus);
            }
        } catch(ParseException e) {
            e.printStackTrace();
            transactionManager.rollback(txStatus);
//        } finally {

        }
        bill = bdSumBill.doubleValue();
        return bill;
    }
*/
    /**
     * 사용량의 요금계산을 TOU 방식으로 계산한다.(임시)
     * @param contract
     * @return
     */
    /*
     * 선불계산로직은 스케줄러로 분리
    private Double saveEmBillingDailyWithTOUTariffEM(Contract contract) {
        Double dataValue[] = null;
        Double bill = 0d;
        // 정확한 숫자계산을 위해 BigDecimal 사용
        BigDecimal bdBill = null;                   // 사용요금
        BigDecimal bdCurBill = null;                // 기존요금
        BigDecimal bdSumBill = new BigDecimal(0d);  // 사용요금의 합
        BigDecimal bdUsage = null;                   // 사용량
        BillingDayEM _billingDayEM = null;
        String saveReadFromDateYYYYMMDDHHMMSS = null;
        String saveReadToDateYYYYMMDDHHMMSS = null;
        String newReadFromDateYYYYMMDDHHMMSS = null;    // 마지막 일자의 새로 읽을 일자시간

        TransactionStatus txStatus = null;
        DefaultTransactionDefinition txDefine = new DefaultTransactionDefinition();
        txDefine.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        // 미터시리얼 번호를 취득한다.
        String mdsId = (contract.getMeter() == null) ? null : contract.getMeter().getMdsId();

        // 계약에 해당하는 요금 정보 취득
        List<TariffEM> tariffEMList = this.getTariffEMInfo(contract);

        // 가장 최근에 갱신한 일별 빌링 정보 취득
        Map<String, Object> map = billingDayEMDao.getLast(contract.getId());

        BillingDayEM billingDayEM = new BillingDayEM();
        billingDayEM.setContract(contract);

        try{
            txStatus = transactionManager.getTransaction(txDefine);
            // 사용량 읽은 마지막 날짜 취득
            String readToDate = TimeUtil.getCurrentDay() + "000000";
            newReadFromDateYYYYMMDDHHMMSS = TimeUtil.getCurrentDay() + "000000";

            if(map != null) { // 빌링에 정보가 없을때는 가장 최근의(오늘) DayEM으로 부터 빌링정보를 등록한다.
                // 사용량 읽은 마지막 날짜 취득
                if((String)map.get("usageReadToDate") != null) {
                    readToDate = (String)map.get("usageReadToDate");
                    newReadFromDateYYYYMMDDHHMMSS = TimeUtil.getPreHour(readToDate, -1);
                } else {
                    // 사용량 읽은 마지막 날짜가 null 일 경우 모두 읽은 것으로 간주함.
                    readToDate = (String)map.get("lastDay") + "230000";
                    newReadFromDateYYYYMMDDHHMMSS = TimeUtil.getPreHour(readToDate, -1);
                }
            }

            // 일별 에너지 사용량 취득(빌링정보저장한 다음 시간부터 일별 사용량을 취득한다.)
            Set<Condition> param = new HashSet<Condition>();
            param.add(new Condition("id.mdevType", new Object[]{DeviceType.Meter}, null, Restriction.EQ));
            param.add(new Condition("id.mdevId", new Object[]{mdsId}, null, Restriction.EQ));
            param.add(new Condition("id.channel", new Object[]{DefaultChannel.Usage.getCode()}, null, Restriction.EQ));
            param.add(new Condition("id.yyyymmdd", new Object[]{readToDate.substring(0,8)}, null, Restriction.GE));
            param.add(new Condition("id.yyyymmdd", new Object[]{readToDate.substring(0,8)}, null, Restriction.ORDERBYDESC));
            List<DayEM> dayEM = dayEMDao.getDayEMsByListCondition(param);

            String readToDateYYYYMMDD = readToDate.substring(0,8);                          // 마지막 읽은 일자
            String newReadFromDateHH = newReadFromDateYYYYMMDDHHMMSS.substring(8,10);       // 마지막 읽은 날의 새로 읽을 시간
            int intNewReadFromDateHH = Integer.parseInt(newReadFromDateHH);                 // 마지막 읽은 날의 새로 읽을 시간 int
            String saveReadFromDateHH = null;   // 저장할 읽은 시작시간
            String saveReadToDateHH = "23";     // 저장할 읽은 종료시간

//            List<Double[]> list_dataValue = new ArrayList<Double[]>();
            // TOU계산을 위해 시간별로 사용량을 저장한다.
            Double[] dayValues = new Double[24];

            boolean flg = true;

            if (dayEM.size() != 0) {
                for (int i = 0 ; i < dayEM.size() ; i++) {
//                    dayValues = new Double[24];
                    bdUsage = new BigDecimal(0d);
                    dataValue =  this.getDayValue24(dayEM.get(i));
                    billingDayEM.setYyyymmdd(dayEM.get(i).getYyyymmdd());
                    List<BillingDayEM> list_billingDayEM = billingDayEMDao.getBillingDayEMs(billingDayEM, null, null);
                    _billingDayEM = (list_billingDayEM.size() != 0) ? list_billingDayEM.get(0) : null;

//                    Double dailyBill = list_billingDayEM.size() != 0 ? list_billingDayEM.get(0).getBill() : 0d;
                    bdCurBill = _billingDayEM != null ? new BigDecimal(_billingDayEM.getBill()) : new BigDecimal(0d);

//                    System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
//                    System.out.println("bdCurBill : " + bdCurBill);
//                    System.out.println("bdCurBill.doubleValue() : " + bdCurBill.doubleValue());
//                    System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");

                    // 마지막 읽은 날의 남은 시간에 대한 사용량을 더한다.
                    // 예) 10일 15시까지 읽었으면 이번에는 10일 16시의 사용량부터 계산하도록 하기 위해서
                    if (readToDateYYYYMMDD.equals(dayEM.get(i).getYyyymmdd())) {
                        if (intNewReadFromDateHH == 0) {
                            // 새로 읽을 시간이 0 이면 skip.(마지막 읽은 시간이 23시임)
                            continue;
                        }

                        for (int j = 0; j < dataValue.length; j++) {
                            dayValues[j] = 0d;
                            if (intNewReadFromDateHH <= j) {
                                if (flg) {
                                    saveReadFromDateHH = newReadFromDateHH;
                                    flg = false;
                                }

//                                usage = usage + dataValue[j];
                                bdUsage = bdUsage.add(new BigDecimal(dataValue[j]));
                                dayValues[j] = dataValue[j];
                            }
                        }
                        flg = true;

                        saveReadFromDateYYYYMMDDHHMMSS = dayEM.get(i).getYyyymmdd() + saveReadFromDateHH + "0000";
                    } else { // 마지막 읽은 날짜와 같지 않을 경우는 전체 사용량을 읽는다.
                        saveReadFromDateYYYYMMDDHHMMSS = dayEM.get(i).getYyyymmdd() + "000000";
//                        usage = usage + dayEM.get(i).getTotal();
                        bdUsage = bdUsage.add(new BigDecimal(dayEM.get(i).getTotal()));
                        dayValues = dataValue;
                    }

                    for (int k = dataValue.length -1 ; k >= 0 ; k--) {
                        if (dataValue[k] != 0.0) {
                            saveReadToDateHH = (String.valueOf(k).length() == 1 ? "0"+k : String.valueOf(k)); // 마지막 시간 취득
                            break;
                        }
                    }

                    saveReadToDateYYYYMMDDHHMMSS = dayEM.get(i).getYyyymmdd() + saveReadToDateHH + "0000";
//                    list_dataValue.add(dayValues);
                    bdBill = this.getEMChargeUsingDayUsage(contract, tariffEMList, bdUsage.doubleValue(), dayValues);
                    bdSumBill = bdSumBill.add(bdBill); // 계산된 요금을 더한다.

                    // Billing_Day_Em에 정보 등록
                    if (_billingDayEM != null) {
                        _billingDayEM.setBill(bdCurBill.add(bdBill).doubleValue());
                        _billingDayEM.setUsageReadFromDate(saveReadFromDateYYYYMMDDHHMMSS);
                        _billingDayEM.setUsageReadToDate(saveReadToDateYYYYMMDDHHMMSS);
                    } else {
                        _billingDayEM = new BillingDayEM();
                        _billingDayEM.setYyyymmdd(saveReadToDateYYYYMMDDHHMMSS.substring(0,8));
                        _billingDayEM.setHhmmss("000000");
                        _billingDayEM.setMDevType(DeviceType.Meter.name());
                        _billingDayEM.setMDevId(mdsId);
                        _billingDayEM.setContract(contract);
                        _billingDayEM.setSupplier(contract.getSupplier());
                        _billingDayEM.setLocation(contract.getLocation());
                        _billingDayEM.setBill(bdCurBill.add(bdBill).doubleValue());
                        _billingDayEM.setActiveEnergyRateTotal(bdUsage.doubleValue());
                        _billingDayEM.setMeter(contract.getMeter());
                        _billingDayEM.setModem((contract.getMeter() == null) ? null : contract.getMeter().getModem());
                        _billingDayEM.setWriteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
                        _billingDayEM.setUsageReadFromDate(saveReadFromDateYYYYMMDDHHMMSS);
                        _billingDayEM.setUsageReadToDate(saveReadToDateYYYYMMDDHHMMSS);
                    }

//                    System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
//                    System.out.println("bdCurBill + bdBill : " + bdCurBill.add(bdBill).doubleValue());
//                    System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");

                    list_billingDayEM.clear();
                    billingDayEMDao.saveOrUpdate(_billingDayEM);
                }

                // contract 정보 갱신
                Code code = contract.getCreditType();
                if(Code.PREPAY.equals(code.getCode()) || Code.EMERGENCY_CREDIT.equals(code.getCode())) { // 선불 요금일 경우
//                    logger.info("###################################################################################");
//                    logger.info("bill : " + bdBill.doubleValue());
//                    logger.info("sumBill : " + bdSumBill.doubleValue());
//                    logger.info("currentCredit : " + contract.getCurrentCredit());
//                    logger.info("###################################################################################");
                    BigDecimal bdCurCredit = (contract.getCurrentCredit() != null) ? new BigDecimal(contract.getCurrentCredit()) : new BigDecimal(0d);

                    // 현재 잔액에서 사용요금을 차감한다.
                    contract.setCurrentCredit(bdCurCredit.subtract(bdSumBill).doubleValue());
                    contractDao.saveOrUpdate(contract);
                }
                transactionManager.commit(txStatus);
            }
        } catch(ParseException e) {
            e.printStackTrace();
            transactionManager.rollback(txStatus);
//        } finally {

        }
        bill = bdSumBill.doubleValue();
        return bill;
    }
*/
    /**
     * DayEM 별 TOU 방식으로 사용량의 요금을 계산한다.
     * 
     * @param contract
     * @param tariffEMList
     * @param usage
     * @param dayValues
     * @return
     */
    /*
     * 선불계산로직은 스케줄러로 분리
    private BigDecimal getEMChargeUsingDayUsage(Contract contract, List<TariffEM> tariffEMList, Double usage, Double[] dayValues) {

//        Double bill = 0d;
        BigDecimal bdBill = new BigDecimal(0d);
        BigDecimal bdBillSum = new BigDecimal(0d);

        Season currentSeason = getCurrentSeason();

        if (tariffEMList.size() != 0) {

            for (TariffEM tariffEM : tariffEMList) {
//                      bill = bill + (tariffEM.getActiveEnergyCharge()== null ? 0d : tariffEM.getActiveEnergyCharge()) + usage*(tariffEM.getRateRebalancingLevy() == null ? 0d : tariffEM.getRateRebalancingLevy()) ;

                if (tariffEM.getSeason() != null) {     // 계절이 적용되는 경우 - 현재는 TOU 요금제만 적용

                    if (tariffEM.getSeason().getId().equals(currentSeason.getId())) {
                        // 현재일이 시즌 내에 포함되어 있는 경우

                        if (tariffEM.getPeakType() != null) {    // peakType 이 있는 경우 - TOU 요금제

                            TOURate touRate = touRateDao.getTOURate(contract.getTariffIndex().getId(), 
                                                                    tariffEM.getSeason().getId(), 
                                                                    tariffEM.getPeakType());
                            if (touRate != null && touRate.getStartTime().length() != 0 && touRate.getEndTime().length() != 0) {

//                                for (Double[] dataValue : list_dataValues) {
                                for (int i = 0; i < dayValues.length; i++) {
                                    if (this.isWithinRange(String.valueOf(i), touRate.getStartTime(), touRate.getEndTime())) {
                                        //peak 시간대별 사용량 구해서 계산해야 함
//                                                bill += dataValue[i]*(tariffEM.getActiveEnergyCharge()== null ? 0d : tariffEM.getActiveEnergyCharge());
                                        bdBill = bdBill.add(new BigDecimal(dayValues[i]).multiply(
                                                new BigDecimal(tariffEM.getActiveEnergyCharge()== null ? 0d : tariffEM.getActiveEnergyCharge())));
//                                        // 세금 계산
//                                        if (tariffEM.getRateRebalancingLevy() != null) {
//                                            bdBill = bdBill.add(new BigDecimal(bdBill).multiply(new BigDecimal(tariffEM.getRateRebalancingLevy())));
//                                        }
                                    }
                                }
//                                }
                            }
                        }
                    // 수정 : 2012-01-11 , 문동규
                    // 계절이 적용되는 경우 계절 기간 이 외의 데이터는 계산하지 않음
//                          } else {
////                                    if(today.compareTo(seasonStart) >= 0 && today.compareTo(seasonEnd) <= 0){
//                              bill = usage*(tariffEM.getActiveEnergyCharge()== null ? 0d : tariffEM.getActiveEnergyCharge());
//                                  }
                    }
                } else {    // 계절이 적용안되는 경우
                    // TODO - 현재 보류
//                    if (tariffEM.getActiveEnergyCharge() != null) {
////                            bill += usage*tariffEM.getActiveEnergyCharge();
//                        bdBill = bdBill.add(new BigDecimal(usage).multiply(new BigDecimal(tariffEM.getActiveEnergyCharge())));
//
//                        // 세금 계산
//                        if (tariffEM.getRateRebalancingLevy() != null) {
//                            bdBill = bdBill.add(new BigDecimal(usage).multiply(new BigDecimal(tariffEM.getRateRebalancingLevy())));
//                        }
//                    }
                }

                // 세금 계산
                if (tariffEM.getRateRebalancingLevy() != null) {
                	bdBillSum = bdBillSum.add(bdBill.multiply(new BigDecimal(tariffEM.getRateRebalancingLevy())));
                }
            }
//                bdBill = bdBill.add(new BigDecimal(usage).multiply(new BigDecimal(tariffEM.getRateRebalancingLevy() == null ? 0d : tariffEM.getRateRebalancingLevy())));
        }

//        System.out.println("*******************************************************************************");
//        System.out.println("calc bdBill.doubleValue() : " + bdBill.doubleValue());
//        System.out.println("*******************************************************************************");
        return bdBillSum;
    }
    
    
//선불계산로직은 스케줄러로 분리
    public Double saveEmBillingMonthWithTariffEM(Contract contract) {
    	double usage = 0d;
    	Double bill = 0d;
        TransactionStatus txStatus = null;
        DefaultTransactionDefinition txDefine = new DefaultTransactionDefinition();
        txDefine.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        try{
	        txStatus = transactionManager.getTransaction(txDefine);
	    	// 계약에 해당하는 요금 정보 취득
	    	List<TariffEM> tariffEMList = this.getTariffEMInfo(contract);

	        // 현재 일자를 취득한다.
			String today = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMdd").substring(0,8);

			// 과금일 취득
	        String startDate = BillDateUtil.getBillDate(contract, today, -1);
	        // Month To Date취득
	        String endDate = BillDateUtil.getMonthToDate(contract, today, 1);

	    	// 월 사용량
	    	DayEM dayEM = new DayEM();
	        dayEM.setChannel(DefaultChannel.Usage.getCode());
	        dayEM.setContract(contract);
	        dayEM.setMDevType(DeviceType.Meter.name());

	        usage = dayEMDao.getDayEMsUsageMonthToDate(dayEM, startDate, endDate);

	        // 요금 정보 취득
	    	bill = this.getEMChargeUsingMonthUsage(contract, tariffEMList, usage);

	        // Billing_Month_EM 정보 등록
			String mdsId = (contract.getMeter() == null) ? null : contract.getMeter().getMdsId();

	        BillingMonthEM billingMonthEM = new BillingMonthEM();
	        billingMonthEM.setYyyymmdd(endDate);
	        billingMonthEM.setHhmmss("000000");
			billingMonthEM.setMDevType(DeviceType.Meter.name());
	        billingMonthEM.setMDevId(mdsId);
			billingMonthEM.setContract(contract);
			billingMonthEM.setSupplier(contract.getSupplier());
			billingMonthEM.setLocation(contract.getLocation());
	        billingMonthEM.setBill(bill);
	        billingMonthEM.setActiveEnergyRateTotal(usage);
	        billingMonthEM.setMeter(contract.getMeter());
	        billingMonthEM.setModem((contract.getMeter() == null) ? null : contract.getMeter().getModem());
	        billingMonthEM.setWriteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
	        billingMonthEM.setUsageReadFromDate(startDate + "000000");
	        billingMonthEM.setUsageReadToDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
	        billingMonthEMDao.saveOrUpdate(billingMonthEM);

	        transactionManager.commit(txStatus);
		}catch(Exception e) {
			e.printStackTrace();
			transactionManager.rollback(txStatus);
		} finally {

		}
        return bill;
    }
	//선불계산로직은 스케줄러로 분리
    private Double getEMChargeUsingMonthUsage(Contract contract, List<TariffEM> tariffEMList, Double usage) {
    	Double bill = 10000d;
        // 현재 일자를 취득한다.
		String today = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMdd").substring(0,8);

		// 과금일 취득
        String startDate = BillDateUtil.getBillDate(contract, today, -1);
        // Month To Date취득
        String endDate = BillDateUtil.getMonthToDate(contract, today, 1);

        try{
			Code code = codeDao.get(contract.getCreditTypeCodeId());
        	int duration = TimeUtil.getDayDuration(startDate, endDate); //요금계산하는 실제 사용기간	
			if(tariffEMList == null || tariffEMList.size() == 0){
				//return 0d;
			} else {
				for(TariffEM tariffEM : tariffEMList){
					if(Code.POSTPAY.equals(code.getCode())) { // 후불 요금일 경우

						if(tariffEM.getSupplySizeMin() != null || tariffEM.getSupplySizeMax() != null){
							if(tariffEM.getSupplySizeMin() != null && tariffEM.getSupplySizeMin() >= usage) {
								bill = tariffEM.getEnergyDemandCharge() == null ? 0d : tariffEM.getEnergyDemandCharge();
								if(tariffEM.getActiveEnergyCharge()!= null){
									bill += usage*tariffEM.getActiveEnergyCharge();
								}
							}
							if(tariffEM.getSupplySizeMin() != null && tariffEM.getSupplySizeMin() >= usage && tariffEM.getSupplySizeMin() < usage) {
								bill = tariffEM.getEnergyDemandCharge();
								if(tariffEM.getActiveEnergyCharge()!= null){
									bill += usage*tariffEM.getActiveEnergyCharge();
								}
							}
							if(tariffEM.getSupplySizeMax() != null && tariffEM.getSupplySizeMin() != null && tariffEM.getSupplySizeMin() < usage) {
								bill = (tariffEM.getEnergyDemandCharge() == null ? 0d : tariffEM.getEnergyDemandCharge())*duration/30;
								if(tariffEM.getActiveEnergyCharge()!= null){
									bill += usage*tariffEM.getActiveEnergyCharge();
								}
							}
						}else{
							if(tariffEM.getSeason() != null){
								String seasonStart = startDate.substring(0,4)+tariffEM.getSeason().getSmonth() + tariffEM.getSeason().getSday();
								String seasonEnd = endDate.substring(0,4)+tariffEM.getSeason().getEmonth() + tariffEM.getSeason().getEday();
								
								if(tariffEM.getPeakType() != null){
									// Billing_day_em의 요금정보를 합산한다.
									Map<String, Object> billingDayEM = billingDayEMDao.getTotal(contract.getId(), startDate, endDate);
									bill = Double.valueOf((String)billingDayEM.get("totalBill"));
								}else{
									
									if(startDate.compareTo(seasonStart) >= 0 && endDate.compareTo(seasonEnd) <= 0){
										bill = usage*(tariffEM.getActiveEnergyCharge() == null ? 0d : tariffEM.getActiveEnergyCharge());
									}
								}
							}else{
	
								if(tariffEM.getActiveEnergyCharge() != null){
									bill = usage*tariffEM.getActiveEnergyCharge();
								}
							}
							bill += (tariffEM.getEnergyDemandCharge() == null ? 0d : tariffEM.getEnergyDemandCharge())*duration/30;
						}
					} else { // 선불 요금일 경우
						bill = bill +  usage*(tariffEM.getActiveEnergyCharge() == null ? 0d : tariffEM.getActiveEnergyCharge())
								+ usage*(tariffEM.getRateRebalancingLevy() == null ? 0d : tariffEM.getRateRebalancingLevy());
					}
				}
			}
        }catch(ParseException e) {
        	e.printStackTrace();
        }
        return bill;
    }
    
    //선불계산로직은 스케줄러로 분리
    private List<TariffEM> getTariffEMInfo(Contract contract) {
//    	contract = contractDao.get(1691);
        // 현재 일자를 취득한다.
		String today = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMdd").substring(0,8);
		TariffType tariffType = tariffTypeDao.get(contract.getTariffIndexId()); 
    	Integer tariffTypeCode = tariffType.getCode();
		Map<String, Object> tariffParam = new HashMap<String, Object>();

		tariffParam.put("tariffTypeCode", tariffTypeCode);
		tariffParam.put("tariffIndex", tariffType);
		tariffParam.put("searchDate", today);

		return this.getApplyedTariff(tariffParam);
    }
*/
	  /**
     * method name : getDayValue24
     * method Desc :
     *
     * @param meteringMonth
     * @return
     */
    /*
     * 선불계산로직은 스케줄러로 분리
    private Double[] getDayValue24(DayEM dayEm) {

        Double[] dayValues = new Double[24];

        dayValues[0] = (dayEm.getValue_00() == null ? 0 : dayEm.getValue_00());
        dayValues[1] = (dayEm.getValue_01() == null ? 0 : dayEm.getValue_01());
        dayValues[2] = (dayEm.getValue_02() == null ? 0 : dayEm.getValue_02());
        dayValues[3] = (dayEm.getValue_03() == null ? 0 : dayEm.getValue_03());
        dayValues[4] = (dayEm.getValue_04() == null ? 0 : dayEm.getValue_04());
        dayValues[5] = (dayEm.getValue_05() == null ? 0 : dayEm.getValue_05());
        dayValues[6] = (dayEm.getValue_06() == null ? 0 : dayEm.getValue_06());
        dayValues[7] = (dayEm.getValue_07() == null ? 0 : dayEm.getValue_07());
        dayValues[8] = (dayEm.getValue_08() == null ? 0 : dayEm.getValue_08());
        dayValues[9] = (dayEm.getValue_09() == null ? 0 : dayEm.getValue_09());
        dayValues[10] = (dayEm.getValue_10() == null ? 0 : dayEm.getValue_10());
        dayValues[11] = (dayEm.getValue_11() == null ? 0 : dayEm.getValue_11());
        dayValues[12] = (dayEm.getValue_12() == null ? 0 : dayEm.getValue_12());
        dayValues[13] = (dayEm.getValue_13() == null ? 0 : dayEm.getValue_13());
        dayValues[14] = (dayEm.getValue_14() == null ? 0 : dayEm.getValue_14());
        dayValues[15] = (dayEm.getValue_15() == null ? 0 : dayEm.getValue_15());
        dayValues[16] = (dayEm.getValue_16() == null ? 0 : dayEm.getValue_16());
        dayValues[17] = (dayEm.getValue_17() == null ? 0 : dayEm.getValue_17());
        dayValues[18] = (dayEm.getValue_18() == null ? 0 : dayEm.getValue_18());
        dayValues[19] = (dayEm.getValue_19() == null ? 0 : dayEm.getValue_19());
        dayValues[20] = (dayEm.getValue_20() == null ? 0 : dayEm.getValue_20());
        dayValues[21] = (dayEm.getValue_21() == null ? 0 : dayEm.getValue_21());
        dayValues[22] = (dayEm.getValue_22() == null ? 0 : dayEm.getValue_22());
        dayValues[23] = (dayEm.getValue_23() == null ? 0 : dayEm.getValue_23());
        
        return dayValues;
    }
*/
	  /**
     * method name : getMonthValue31
     * method Desc :
     *
     * @param meteringMonth
     * @return
     */
    /*
     * 선불계산로직은 스케줄러로 분리
    private Double[] getMonthValue31(MonthEM monthEm) {

        Double[] dayValues = new Double[31];

        dayValues[0] = monthEm.getValue_01() == null ? 0 : monthEm.getValue_01();
        dayValues[1] = monthEm.getValue_02() == null ? 0 : monthEm.getValue_02();
        dayValues[2] = monthEm.getValue_03() == null ? 0 : monthEm.getValue_03();
        dayValues[3] = monthEm.getValue_04() == null ? 0 : monthEm.getValue_04();
        dayValues[4] = monthEm.getValue_05() == null ? 0 : monthEm.getValue_05();
        dayValues[5] = monthEm.getValue_06() == null ? 0 : monthEm.getValue_06();
        dayValues[6] = monthEm.getValue_07() == null ? 0 : monthEm.getValue_07();
        dayValues[7] = monthEm.getValue_08() == null ? 0 : monthEm.getValue_08();
        dayValues[8] = monthEm.getValue_09() == null ? 0 : monthEm.getValue_09();
        dayValues[9] = monthEm.getValue_10() == null ? 0 : monthEm.getValue_10();
        dayValues[10] = monthEm.getValue_11() == null ? 0 : monthEm.getValue_11();
        dayValues[11] = monthEm.getValue_12() == null ? 0 : monthEm.getValue_12();
        dayValues[12] = monthEm.getValue_13() == null ? 0 : monthEm.getValue_13();
        dayValues[13] = monthEm.getValue_14() == null ? 0 : monthEm.getValue_14();
        dayValues[14] = monthEm.getValue_15() == null ? 0 : monthEm.getValue_15();
        dayValues[15] = monthEm.getValue_16() == null ? 0 : monthEm.getValue_16();
        dayValues[16] = monthEm.getValue_17() == null ? 0 : monthEm.getValue_17();
        dayValues[17] = monthEm.getValue_18() == null ? 0 : monthEm.getValue_18();
        dayValues[18] = monthEm.getValue_19() == null ? 0 : monthEm.getValue_19();
        dayValues[19] = monthEm.getValue_20() == null ? 0 : monthEm.getValue_20();
        dayValues[20] = monthEm.getValue_21() == null ? 0 : monthEm.getValue_21();
        dayValues[21] = monthEm.getValue_22() == null ? 0 : monthEm.getValue_22();
        dayValues[22] = monthEm.getValue_23() == null ? 0 : monthEm.getValue_23();
        dayValues[23] = monthEm.getValue_24() == null ? 0 : monthEm.getValue_24();
        dayValues[24] = monthEm.getValue_25() == null ? 0 : monthEm.getValue_25();
        dayValues[25] = monthEm.getValue_26() == null ? 0 : monthEm.getValue_26();
        dayValues[26] = monthEm.getValue_27() == null ? 0 : monthEm.getValue_27();
        dayValues[27] = monthEm.getValue_28() == null ? 0 : monthEm.getValue_28();
        dayValues[28] = monthEm.getValue_29() == null ? 0 : monthEm.getValue_29();
        dayValues[29] = monthEm.getValue_30() == null ? 0 : monthEm.getValue_30();
        dayValues[30] = monthEm.getValue_31() == null ? 0 : monthEm.getValue_31();
        
        return dayValues;
    }
    */
//    private boolean isWithinRange(String targetHour, String startHour, String endHour) {
//        SimpleDateFormat sdf6 = new SimpleDateFormat("H");
//        Date targetTime = null;
//        Date startTime = null;
//        Date endTime = null;
//
//        try{
//            targetTime = sdf6.parse(targetHour);
//            startTime = sdf6.parse(startHour);
//            endTime = sdf6.parse(endHour);
//        }catch(Exception e) {
//            e.printStackTrace();
//        }
//
//        return !(targetTime.before(startTime) || targetTime.after(endTime));
//    }

    /**
     * 해당 시간이 조건범위에 포함되는지 체크한다.
     * 
     * @param targetHour 해당 시간
     * @param startHour 시작 시간
     * @param endHour 종료 시간
     * @return
     */
    /*
     * 선불계산로직은 스케줄러로 분리
    private boolean isWithinRange(String targetHour, String startHour, String endHour) {
        boolean isWithin = false;
        SimpleDateFormat sdf6 = new SimpleDateFormat("H");
        Date targetTime = null;
        Date startTime = null;
        Date endTime = null;
        Date startTime2 = null;
        Date endTime2 = null;
        boolean hasNextDay = ((new Integer(startHour)).compareTo(new Integer(endHour)) > 0);

        try{
            targetTime = sdf6.parse(targetHour);
            
            if (!hasNextDay) {
                startTime = sdf6.parse(startHour);
                endTime = sdf6.parse(endHour);
            } else {
                // 종료 시간이 다음일 일 경우
                startTime = sdf6.parse("00");
                endTime = sdf6.parse(endHour);

                startTime2 = sdf6.parse(startHour);
                endTime2 = sdf6.parse("23");
            }
        }catch(ParseException e) {
            e.printStackTrace();
        }

        isWithin = ((targetTime.compareTo(startTime) >= 0 && targetTime.compareTo(endTime) <= 0) || (hasNextDay && (targetTime
                .compareTo(startTime2) >= 0 && targetTime.compareTo(endTime2) <= 0)));
        return isWithin;
    }
*/
    /**
     * 현재일자의 Season 모델객체를 가져온다.
     * 
     * @return Season 모델객체
     */
    /*
     * 선불계산로직은 스케줄러로 분리
    private Season getCurrentSeason() {
        List<Season> slist = seasonDao.getAll();
        Season rtnSeason = null;

        String today = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMdd").substring(0,8);
        String seasonStart = null;
        String seasonEnd = null;
        String seasonStart2 = null;
        String seasonEnd2 = null;
        String tyear = today.substring(0, 4);

        try {
            String smonth = null;
            String emonth = null;
            String sday = null;
            String eday = null;

            Date todayDate = DateTimeUtil.getDateFromYYYYMMDD(today);
            Date seasonStartDate = null;
            Date seasonEndDate = null;
            Date seasonStartDate2 = null;
            Date seasonEndDate2 = null;
            boolean isNull = false;

            if (slist != null && slist.size() > 0) {
                
                for (Season season : slist) {
                    smonth = season.getSmonth();
                    emonth = season.getEmonth();
                    sday = season.getSday();
                    eday = null;
                    isNull = StringUtil.nullToBlank(season.getEday()).isEmpty();

                    if (isNull) { // 종료일이 없으면 종료월 마지막일자 적용
                        eday = CalendarUtil.getMonthLastDate(tyear, emonth);
                    } else {
                        eday = season.getEday();
                    }

                    if ((new Integer(smonth)).compareTo(new Integer(emonth)) <= 0) { // 계절 시작월이 종료월보다 작은 경우
                        seasonStart = tyear + smonth + sday;
                        seasonEnd = tyear + emonth + eday;
                        seasonStartDate = DateTimeUtil.getDateFromYYYYMMDD(seasonStart);
                        seasonEndDate = DateTimeUtil.getDateFromYYYYMMDD(seasonEnd);
                    } else {
                        // 계절 시작월이 종료월 보다 큰 경우. 연도에 걸쳐있는 경우
                        // ex. Winter 시작일자 12-01, 종료일자 02-28 : 01-01 ~ 02-28 과 12-01 ~ 12-31 두 개의 기간으로 검색
                        seasonStart = tyear + "0101";
                        seasonEnd = tyear + emonth + eday;

                        seasonStart2 = tyear + smonth + sday;
                        seasonEnd2 = tyear + "1231";

                        seasonStartDate = DateTimeUtil.getDateFromYYYYMMDD(seasonStart);
                        seasonEndDate = DateTimeUtil.getDateFromYYYYMMDD(seasonEnd);
                        seasonStartDate2 = DateTimeUtil.getDateFromYYYYMMDD(seasonStart2);
                        seasonEndDate2 = DateTimeUtil.getDateFromYYYYMMDD(seasonEnd2);
                    }

                    if ((todayDate.compareTo(seasonStartDate) >= 0 && todayDate.compareTo(seasonEndDate) <= 0)
                            || (((new Integer(smonth)).compareTo(new Integer(emonth)) > 0) && (todayDate
                                    .compareTo(seasonStartDate2) >= 0 && todayDate.compareTo(seasonEndDate2) <= 0))) {
                        rtnSeason = season;
                        break;
                    }

                }
            }
        } catch(ParseException pe) {
            pe.printStackTrace();
        }

        return rtnSeason;
    }
    */
    /*
     * 
     * 최신에 해당되는 Tariff 정보를 추출함
     * 
     */
    @SuppressWarnings("unchecked")
    public List<TariffEM> getNewestTariff(Contract contract) {
		
    	StringBuffer sb = new StringBuffer();
    	sb.append("\n SELECT  MAX(INTEGER(yyyymmdd)) ");
		sb.append("\n FROM  TariffEM ");
		sb.append("\n WHERE tariffType.id = :tariffId");
    	
		Query query = getSession().createQuery(sb.toString());
		query.setInteger("tariffId", contract.getTariffIndexId());
		
		String yyyymmdd = null;
		
		if(query.list().get(0) == null || query.list().size() < 0) {
			yyyymmdd = null;
		} else {
			yyyymmdd = query.list().get(0).toString();
		}
		
		StringBuffer sb2 = new StringBuffer();
		sb2.append("\n FROM  TariffEM ");
		sb2.append("\n WHERE tariffType.id = :tariffId");
		sb2.append("\n 	 AND yyyymmdd = :yyyymmdd");

		Query query2 = getSession().createQuery(sb2.toString());
		query2.setInteger("tariffId", contract.getTariffIndexId());
		query2.setString("yyyymmdd", yyyymmdd);
		
		List<TariffEM> tariffEMList = query2.list();

		return tariffEMList;
		
    }
    
    public Boolean isNewDate(String yyyymmdd) {
    	Boolean result = false;
    	Criteria criteria = getSession().createCriteria(TariffEM.class);
    	criteria.add(Restrictions.eq("yyyymmdd", yyyymmdd));
    	criteria.setProjection(Projections.distinct(Projections.property("yyyymmdd")));
    	Integer count = criteria.list().size();
    	if ( count < 1 ) result = true;
    	return result;
    }
    
    /*
     * 
     * 최신에 해당되는 Tariff 정보를 추출함
     * 
     */
    @SuppressWarnings("unchecked")
    public List<TariffEM> getNewestTariff(Contract contract, String yyyymmdd) {
        
        StringBuffer sb = new StringBuffer();
        sb.append("\n SELECT  MAX(yyyymmdd) ");
        sb.append("\n FROM  TariffEM ");
        sb.append("\n WHERE tariffType.id = :tariffId");
        sb.append("\n and yyyymmdd <= :yyyymmdd");
        
        Query query = getSession().createQuery(sb.toString());
        query.setInteger("tariffId", contract.getTariffIndexId());
        query.setString("yyyymmdd", yyyymmdd);
        
        String appliedDate = null;
        
        if(query.list().get(0) == null || query.list().size() < 0) {
            appliedDate = null;
        } else {
            appliedDate = query.list().get(0).toString();
        }
        
        StringBuffer sb2 = new StringBuffer();
        sb2.append("\n FROM  TariffEM ");
        sb2.append("\n WHERE tariffType.id = :tariffId");
        sb2.append("\n   AND yyyymmdd = :yyyymmdd");

        Query query2 = getSession().createQuery(sb2.toString());
        query2.setInteger("tariffId", contract.getTariffIndexId());
        query2.setString("yyyymmdd", appliedDate);
        
        List<TariffEM> tariffEMList = query2.list();

        return tariffEMList;
        
    }
    
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    @SuppressWarnings("unchecked")
	public Boolean deleteYyyymmddTariff(String yyyymmdd) {
    	Boolean result = true;
    	try {
			Criteria criteria = getSession().createCriteria(TariffEM.class);
			criteria.add(Restrictions.eq("yyyymmdd", yyyymmdd));
			List<TariffEM> list = criteria.list();
			
			for ( TariffEM em : list ) {
				delete(em);
			}
			
			logger.info(yyyymmdd+ "'s tariffEM delete complete");
		} catch (Exception e) {
			result = false;
			e.printStackTrace();
		} 
    	return result;
    }

    /**
     * method name : getTariffSupplySizeComboData<b/>
     * method Desc : Consumption Ranking 가젯에서 선택한 TariffType 에 해당하는 SupplySize ComboData 리스트를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getTariffSupplySizeComboData(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result = null;
        Season season = (Season)conditionMap.get("season");
        Integer tariffTypeId = (Integer)conditionMap.get("tariffTypeId");

        StringBuilder sb = new StringBuilder();

        sb.append("\nSELECT te.supplySizeMin AS supplySizeMin, ");
        sb.append("\n       te.supplySizeMax AS supplySizeMax, ");
        sb.append("\n       te.condition1 AS condition1, ");
        sb.append("\n       te.condition2 AS condition2 ");
        sb.append("\nFROM TariffEM te ");
        sb.append("\nWHERE te.tariffType.id = :tariffTypeId ");
        sb.append("\nAND   te.yyyymmdd = (SELECT MAX(ts.yyyymmdd) ");
        sb.append("\n                     FROM TariffEM ts ");
        sb.append("\n                     WHERE ts.tariffType.id = :tariffTypeId ");
        sb.append("\n                    ) ");

        if (season != null) {
            sb.append("\nAND   (te.season = null OR te.season.id = :seasonId) ");    
        }
        sb.append("\nORDER BY te.supplySizeMin ");

        Query query = getSession().createQuery(sb.toString());
        query.setInteger("tariffTypeId", tariffTypeId);
        if (season != null) {
            query.setInteger("seasonId", season.getId());
        }
        result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        return result;
    }
}