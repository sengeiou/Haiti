package com.aimir.dao.system.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.stereotype.Repository;
import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.DefaultChannel;
import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.mvm.BillingDayGMDao;
import com.aimir.dao.mvm.BillingMonthGMDao;
import com.aimir.dao.mvm.DayGMDao;
import com.aimir.dao.mvm.MonthGMDao;
import com.aimir.dao.mvm.SeasonDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.dao.system.TariffGMDao;
import com.aimir.model.mvm.DayGM;
import com.aimir.model.mvm.MonthGM;
import com.aimir.model.system.Contract;
import com.aimir.model.system.TariffGM;
import com.aimir.model.vo.TariffGMVO;
import com.aimir.util.CalendarUtil;
import com.aimir.util.StringUtil;

@Repository(value = "tariffgmDao")
public class TariffGMDaoImpl extends AbstractHibernateGenericDao<TariffGM, Integer> implements TariffGMDao {
			
	Log logger = LogFactory.getLog(TariffGMDaoImpl.class);
	
	@Autowired
	SeasonDao seasonDao;
	
	@Autowired
	DayGMDao dayGMDao;
	
	@Autowired
	MonthGMDao monthGMDao;
	
	@Autowired
	BillingDayGMDao billingDayGMDao;
	
	@Autowired
	BillingMonthGMDao billingMonthGMDao;
	
	@Autowired
	ContractDao contractDao;
	
    @Autowired
    HibernateTransactionManager transactionManager;

	@Autowired
	protected TariffGMDaoImpl(SessionFactory sessionFactory) {
		super(TariffGM.class);
		super.setSessionFactory(sessionFactory);
	}

	@SuppressWarnings("unchecked")
	public List<Object> getYyyymmddList(Integer supplierId){

		StringBuffer sb = new StringBuffer();
		sb.append("\n SELECT   gm.yyyymmdd as yyyymmdd ");
		sb.append("\n FROM     TariffGM gm ");
		if(supplierId != null) {
			sb.append("\n where    gm.tariffType.supplierId = :supplierId");
		}
		sb.append("\n GROUP BY gm.yyyymmdd ");
		sb.append("\n ORDER BY gm.yyyymmdd ");

		Query query = getSession().createQuery(sb.toString());
		if(supplierId != null) {
			query.setInteger("supplierId", supplierId);
		}
		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getChargeMgmtList(Map<String, Object> condition){
		
		String yyyymmdd = (String)condition.get("yyyymmdd");
		Integer supplierId = (Integer)condition.get("supplierId");
		
		StringBuffer sb = new StringBuffer();
		sb.append("\n SELECT	t.id as id, ");
		sb.append("\n       	t.tariffType.name as tariffType, ");
		sb.append("\n       	s.name as season, ");
		sb.append("\n       	t.basicRate as basicRate, ");
		sb.append("\n       	t.usageUnitPrice as usageUnitPrice, ");
		sb.append("\n       	t.adjustmentFactor as adjustmentFactor, ");
		sb.append("\n       	t.salePrice as salePrice, ");
		sb.append("\n       	t.yyyymmdd as yyyymmdd ");
		sb.append("\n FROM      TariffGM t" +
				"				LEFT OUTER JOIN t.season s ");
		sb.append("\n WHERE t.tariffType.supplierId = :supplierId");
		if(yyyymmdd.length() > 0 ){
			sb.append("\n AND     t.yyyymmdd = :yyyymmdd ");
		}
		sb.append("\n ORDER BY t.yyyymmdd, t.tariffType.id, s.id ");

		Query query = getSession().createQuery(sb.toString());
		query.setInteger("supplierId", supplierId);
		if(yyyymmdd.length() > 0){
			query.setString("yyyymmdd", yyyymmdd);
		}

		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	}
	
	/*사용자 화면 - 자신이 계약한 종별*/
	@SuppressWarnings("unchecked")
	public List<TariffGMVO> getCustomerChargeMgmtList(Map<String, Object> condition){

		String yyyymmdd = (String)condition.get("yyyymmdd");
		String sUserId	= (String)condition.get("sUserId");
		
		StringBuffer sb = new StringBuffer();
		/*
		sb.append("\n SELECT	t.id as id, ");
		sb.append("\n       	t.tariffType.name as tariffType, ");
		sb.append("\n       	t.season.name as season, ");
		sb.append("\n       	t.basicRate as basicRate, ");
		sb.append("\n       	t.usageUnitPrice as usageUnitPrice, ");
		sb.append("\n       	t.adjustmentFactor as adjustmentFactor, ");
		sb.append("\n       	t.salePrice as salePrice, ");
		sb.append("\n       	t.yyyymmdd as yyyymmdd ");
		*/
		sb.append("\n SELECT	t ");
		sb.append("\n FROM      Operator o, Customer c, Contract ct, TariffType tt,  TariffGM t");
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
		List<TariffGM> result = query.list();
		List<TariffGMVO> tariffgmVOs = new ArrayList<TariffGMVO>();
		TariffGMVO tariffgmVO = null;
		
		for (int i = 0; i < result.size(); i++) {
			tariffgmVO = new TariffGMVO();
			TariffGM tariff = result.get(i);
			tariffgmVO.setId(tariff.getId());
			tariffgmVO.setTariffType(tariff.getTariffType() == null ? "" : tariff.getTariffType().getName());
			tariffgmVO.setSeason(tariff.getSeason() == null ? "" : tariff.getSeason().getName());			
			tariffgmVO.setAdjustmentFactor(tariff.getAdjustmentFactor());
			tariffgmVO.setBasicRate(tariff.getBasicRate());
			tariffgmVO.setSalePrice(tariff.getSalePrice());
			tariffgmVO.setUsageUnitPrice(tariff.getUsageUnitPrice());
			tariffgmVO.setYyyymmdd(tariff.getYyyymmdd());
			
			tariffgmVOs.add(tariffgmVO);
		}
		return tariffgmVOs;
	}
	
	public int updateData(TariffGM tariffGM) throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append("UPDATE TariffGM t ");
		sb.append("SET t.basicRate = ?, ");
		sb.append("    t.usageUnitPrice = ?, ");
		sb.append("    t.adjustmentFactor = ?, ");
		sb.append("    t.salePrice = ? ");		
		sb.append("WHERE t.id = ? ");					
	
		//HQL문을 이용한 CUD를 할 경우에는 getHibernateTemplate().bulkUpdate() 메소드를 사용한다.		
		Query query = getSession().createQuery(sb.toString());
		query.setParameter(1, tariffGM.getBasicRate());
		query.setParameter(2, tariffGM.getUsageUnitPrice());
		query.setParameter(3, tariffGM.getAdjustmentFactor());
		query.setParameter(4, tariffGM.getSalePrice());
		query.setParameter(5, tariffGM.getId());
		
		return query.executeUpdate();
		/*return this.getHibernateTemplate().bulkUpdate(sb.toString(), new Object[] {  tariffGM.getBasicRate(), 
					 																 tariffGM.getUsageUnitPrice(), 
																					 tariffGM.getAdjustmentFactor(), 
																					 tariffGM.getSalePrice(), 
																					 tariffGM.getId() } );*/
	}

	public Double getUsageCharge(Map<String, Object> condition)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Object> getUsageCharges(Map<String, Object> condition)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * 계약에 해당하는 사용량에 따른 사용요금을 계산한다.
	 * @param condition
	 * <ul>
	 * <li> contractId : 계약ID
	 * <li> dateType : 조회기간구분(일/월) ,CommonConstants.DateType
	 * <li> startDate : yyyyMMdd or yyyyMM
	 * <li> endDate : yyyyMMdd or yyyyMM
	 * </ul>
	 * @return
	 */
	public Double getUsageChargeByContract(Map<String, Object> params){
		Contract contract = (Contract)params.get("contract");
		String dateType = StringUtil.nullToBlank(params.get("dateType"));
		String startDate = StringUtil.nullToBlank(params.get("startDate"));
		String endDate = StringUtil.nullToBlank(params.get("endDate"));
		
		if(contract==null || "".equals(dateType) || "".equals(startDate) || "".equals(endDate)){
			return 0.0;
		}
		
		//Integer tariffTypeCode=0;
		Double chargeSum = 0.0;
		
		try
		{
			Integer tariffTypeCode = contract.getTariffIndex().getCode();
			
			
			Map<String,Object> tariffParam = new HashMap<String,Object>();
			tariffParam.put("tariffTypeCode", tariffTypeCode);
			tariffParam.put("channel", DefaultChannel.Usage.getCode());
			tariffParam.put("dst", 0);
			tariffParam.put("mdevType", DeviceType.Meter);
			tariffParam.put("mdevId", contract.getMeter().getMdsId());
			tariffParam.put("supplierId", contract.getSupplier().getId());
			
			TariffGM tariffGM = null;
			@SuppressWarnings("unused")
	        MonthGM monthGM = null;
			DayGM dayGM = null;
			
			Double usageUnitPrice = 0.0;
			Double basicRate = 0.0;
			Double usage = 0.0;
			
	    	if(CommonConstants.DateType.DAILY.getCode().equals(dateType))
	    	{
	    		int period = 0;
	    		for(int i=Integer.parseInt(startDate);i<=Integer.parseInt(endDate);i=Integer.parseInt(CalendarUtil.getDateWithoutFormat(Integer.toString(i),Calendar.DATE, 1)))
	    		{
	    			tariffGM = new TariffGM();
	    			dayGM = new DayGM();
	    			
	    			tariffParam.put("searchDate", Integer.toString(i));
	    			tariffParam.put("seasonId", seasonDao.getSeasonByMonth(startDate.substring(4, 6)).getId());
	    			tariffGM = getApplyedTariff(tariffParam);
	    			usageUnitPrice = tariffGM==null||tariffGM.getUsageUnitPrice()==null?0.0:tariffGM.getUsageUnitPrice();
	    			basicRate = tariffGM==null||tariffGM.getBasicRate()==null?0.0:tariffGM.getBasicRate();
	    			
	    			tariffParam.put("yyyymmdd", Integer.toString(i));
	    			
//	    			dayGM =dayGMDao.getDayGM(tariffParam);
	    			dayGM =dayGMDao.getDayGMbySupplierId(tariffParam);
	    			usage = dayGM==null||dayGM.getTotal()==null?0.0:dayGM.getTotal();
	    			
	    			chargeSum = chargeSum + usage * usageUnitPrice;
	    			period++;
	    		}
	    		chargeSum = chargeSum + Math.round(basicRate*period/30);
	    		
	    	}
	    	else if(CommonConstants.DateType.MONTHLY.getCode().equals(dateType))
	    	{
	    		startDate = startDate.substring(0, 6)+"01";
	    		endDate = endDate.substring(0, 6)+"31";

	    		for(int i=Integer.parseInt(startDate);i<=Integer.parseInt(endDate);i=Integer.parseInt(CalendarUtil.getDateWithoutFormat(Integer.toString(i),Calendar.MONTH, 1))){
	    			tariffGM = new TariffGM();
	    			monthGM = new MonthGM();
	    			tariffParam.put("searchDate", Integer.toString(i));
	    			tariffParam.put("seasonId", seasonDao.getSeasonByMonth(startDate.substring(4, 6)).getId());
	    			tariffGM = getApplyedTariff(tariffParam);
	    			usageUnitPrice = tariffGM==null||tariffGM.getUsageUnitPrice()==null?0.0:tariffGM.getUsageUnitPrice();
	    			basicRate = tariffGM==null||tariffGM.getBasicRate()==null?0.0:tariffGM.getBasicRate();

	    			tariffParam.put("yyyymm", Integer.toString(i).substring(0, 6));
//	    			monthGM =monthGMDao.getMonthGM(tariffParam);
	    			monthGM =monthGMDao.getMonthGMbySupplierId(tariffParam);
	    			if(monthGM != null) {
	    			usage = dayGM==null||dayGM.getTotal()==null?0.0:dayGM.getTotal();
	    			
	    			chargeSum = chargeSum + usage * usageUnitPrice + basicRate;
	    			}
	    		}
	    	}
			
			
		} catch (Exception e)
		{
			// TODO: handle exception
			e.printStackTrace();
			chargeSum=0.0;
		}
		 
		 
		return chargeSum;
  		
	}

	@SuppressWarnings("unchecked")
	public TariffGM getApplyedTariff(Map<String, Object> params){

		Integer tariffTypeCode = (Integer)params.get("tariffTypeCode");
		Integer seasonId = (Integer)params.get("seasonId");
		String searchDate = (String)params.get("searchDate");

		StringBuffer sb = new StringBuffer();
		sb.append("\n FROM  TariffGM ");
		sb.append("\n WHERE tariffType.code = :tariffTypeCode");
		sb.append("\n AND yyyymmdd <= :searchDate  ");
		sb.append("\n AND season.id = :seasonId");
		sb.append("\n ORDER BY yyyymmdd desc");

		Query query = getSession().createQuery(sb.toString());
		query.setInteger("tariffTypeCode", tariffTypeCode);
		query.setString("searchDate", searchDate);
		query.setInteger("seasonId", seasonId);

		List<TariffGM> tariffGmList = query.list();

		return tariffGmList!=null&&tariffGmList.size()>0?tariffGmList.get(0):null;
	}
/*
 * 선불계산로직은 스케줄러로 분리
 * 
	public Double saveGmBillingDayWithTariffGM(Contract contract) {
		double usage = 0d;
		Double bill = 0d;
		Double usageUnitPrice = 0d;
		Double[] dataValue = null;
		BillingDayGM _billingDayGM = null;
        String saveReadFromDateYYYYMMDDHHMMSS = null;
        String saveReadToDateYYYYMMDDHHMMSS = null;

        TransactionStatus txStatus = null;
        DefaultTransactionDefinition txDefine = new DefaultTransactionDefinition();
        txDefine.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);

    	// 요금 정보 취득
    	TariffGM tariffGM = this.getTariffGMInfo(contract);
    	
        // 가장 최근에 갱신한 일별 빌링 정보 취득
        Map<String, Object> map = billingDayGMDao.getLast(contract.getId());

        BillingDayGM billingDayGM = new BillingDayGM();
        billingDayGM.setContract(contract);
        try{

	        txStatus = transactionManager.getTransaction(txDefine);
	        // 사용량 읽은 마지막 날짜 취득
	        String readToDate = TimeUtil.getCurrentDay() + "000000";
	        saveReadFromDateYYYYMMDDHHMMSS = TimeUtil.getCurrentDay() + "000000";
	        if(map != null) { // 빌링에 정보가 없을때는 가장 최근의(오늘) DayEM으로 부터 빌링정보를 등록한다.
		        // 사용량 읽은 마지막 날짜 취득
	        	if((String)map.get("usageReadToDate") != null) {
	        		readToDate = (String)map.get("usageReadToDate");
	        		saveReadFromDateYYYYMMDDHHMMSS = TimeUtil.getPreHour(readToDate, -1);
	        	}
	        }

			// 일별 가스 사용량 취득
			Set<Condition> param = new HashSet<Condition>();
	        param.add(new Condition("id.mdevType", new Object[]{DeviceType.Meter}, null, Restriction.EQ));
	        param.add(new Condition("id.mdevId", new Object[]{contract.getMeter().getMdsId()}, null, Restriction.EQ));
	        param.add(new Condition("id.channel", new Object[]{DefaultChannel.Usage.getCode()}, null, Restriction.EQ));
	        param.add(new Condition("id.dst", new Object[]{0}, null, Restriction.EQ));
	        param.add(new Condition("id.yyyymmdd", new Object[]{readToDate.substring(0,8)}, null, Restriction.GE));
	        param.add(new Condition("id.yyyymmdd", new Object[]{readToDate.substring(0,8)}, null, Restriction.ORDERBYDESC));
			List<DayGM> dayGM = dayGMDao.getDayGMsByListCondition(param);
	
	        String readToDateYYYYMMDD = readToDate.substring(0,8);
	        String readFromDateHH = saveReadFromDateYYYYMMDDHHMMSS.substring(8,10);
	        String saveReadFromDateHH = null;
	    	String saveReadToDateHH = "23";

//        	saveReadFromDateYYYYMMDDHHMMSS = TimeUtil.getPreHour(readToDate, -1);
	        boolean flg = true;
	        
	    	if(dayGM.size() != 0) {
	    		for(int i=0; i<dayGM.size(); i++) {
	    			dataValue =  this.getDayValue24(dayGM.get(i));
	    			billingDayGM.setYyyymmdd(dayGM.get(i).getYyyymmdd());
	    			List<BillingDayGM> list_billingDayGM = billingDayGMDao.getBillingDayGMs(billingDayGM, null, null);
	    			Double dailyBill = list_billingDayGM.size() != 0 ? list_billingDayGM.get(0).getBill() : 0d;

	    			// 마지막 읽은 날의 남은 시간에 대한 사용량을 더한다.
	    			// 예) 10일 22시까지 읽었으면 이번에는 10일 23시의 사용량부터 계산하도록 하기 위해서
	    			if(readToDateYYYYMMDD.equals(dayGM.get(i).getYyyymmdd())) { 
	    				for (int j = 0; j < dataValue.length; j++) {

	    					if(Integer.parseInt(readFromDateHH) <= j) {
	    						usage = usage + dataValue[j];
	    						if(flg) {
		    						saveReadFromDateHH = readFromDateHH;
		    						flg = false;
	    						}
	    					}
						}
	    				flg = true;
	    				saveReadFromDateYYYYMMDDHHMMSS = dayGM.get(i).getYyyymmdd() + saveReadFromDateHH + "0000";
	    			} else { // 마지막 읽은 날짜와 같지 않을 경우는 전체 사용량을 읽는다.
	    				usage = usage + dayGM.get(i).getTotal();
	    				saveReadFromDateYYYYMMDDHHMMSS = dayGM.get(i).getYyyymmdd() + "000000";
	    			}
	    			// 가장 최근 데이터일 경우, 언제까지 사용량을 읽었는지 계산한다.
					for(int k=dataValue.length -1; k>=0; k--) {
						if(dataValue[k] != 0.0) {
							saveReadToDateHH = (String.valueOf(k).length() == 1 ? "0"+k : String.valueOf(k)); // 마지막 시간 취득
							break;
						}
					}
					saveReadToDateYYYYMMDDHHMMSS = dayGM.get(i).getYyyymmdd() + saveReadToDateHH + "0000";

	    			Code code = contract.getCreditType();

	                // 선후불 요금제가 동일 할 경우는 if문에 의한 분기가 필요없다. 현재는 선불요금제가 명확하지 않아서 if문에 의해 분기를 해 놓은 상태임
	            	if(Code.POSTPAY.equals(code.getCode())) { // 후불 요금일 경우
	            		usageUnitPrice = (tariffGM==null||tariffGM.getUsageUnitPrice()==null?0.0:tariffGM.getUsageUnitPrice());
	            		bill = usage * usageUnitPrice;
	            	} else { // 선불 요금일 경우
	            		usageUnitPrice = (tariffGM==null||tariffGM.getUsageUnitPrice()==null?0.0:tariffGM.getUsageUnitPrice());
	            		bill = usage * usageUnitPrice;
	            	}
	
	    			// Billing_Day_Gm에 정보 등록
	    	    	if(list_billingDayGM.size() != 0) {
	    	    		_billingDayGM = list_billingDayGM.get(0);   	    		
	    	    	} else {
	    	    		_billingDayGM = new BillingDayGM();
	    	    	}
	    	    	
	        		String mdsId = (contract.getMeter() == null) ? null : contract.getMeter().getMdsId();

	        		_billingDayGM.setYyyymmdd(saveReadToDateYYYYMMDDHHMMSS.substring(0,8));
	        		_billingDayGM.setHhmmss(list_billingDayGM.size() != 0 ? list_billingDayGM.get(0).getHhmmss() : "000000");
	        		_billingDayGM.setMDevId(mdsId);
	        		_billingDayGM.setMDevType(DeviceType.Meter.name());
	        		_billingDayGM.setBill(dailyBill + bill);
	        		_billingDayGM.setUsage(usage);
	        		_billingDayGM.setContract(contract);
	        		_billingDayGM.setSupplier(contract.getSupplier());
	        		_billingDayGM.setLocation(contract.getLocation());
	        		_billingDayGM.setMeter(contract.getMeter());
	        		_billingDayGM.setModem((contract.getMeter() == null) ? null : contract.getMeter().getModem());
	        		_billingDayGM.setWriteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
	        		_billingDayGM.setUsageReadFromDate(saveReadFromDateYYYYMMDDHHMMSS);
	        		_billingDayGM.setUsageReadToDate(saveReadToDateYYYYMMDDHHMMSS);
	        		billingDayGMDao.saveOrUpdate(_billingDayGM);
	    		}
	    		// contract 정보 갱신
	    		Code code = contract.getCreditType();
	    		if(Code.PREPAY.equals(code.getCode())) { // 선불 요금일 경우
	    		    contract.setCurrentCredit(contract.getCurrentCredit() - bill); // 현재 잔액에서 사용요금 뺀다
	    			contractDao.saveOrUpdate(contract);
	    		}
	    		transactionManager.commit(txStatus);
	    	}
        }catch(ParseException e) {
        	e.printStackTrace();
        	transactionManager.rollback(txStatus);
        }
		return bill;
	}

//  선불계산로직은 스케줄러로 분리
	public Double saveGmBillingMonthWithTariffGM(Contract contract) {
		Double bill = 10000d;
        TransactionStatus txStatus = null;
        DefaultTransactionDefinition txDefine = new DefaultTransactionDefinition();
        txDefine.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        try{
		    txStatus = transactionManager.getTransaction(txDefine);
	    	// 요금 정보 취득
	    	TariffGM tariffGM = this.getTariffGMInfo(contract);
			Double usageUnitPrice = 0d;
			Double basicRate = tariffGM==null||tariffGM.getBasicRate()==null?0.0:tariffGM.getBasicRate();
			
	        // 현재 일자를 취득한다.
			String today = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMdd").substring(0,8);
			// 과금일 취득
	        String startDate = BillDateUtil.getBillDate(contract, today, -1);
	        // Month To Date취득
	        String endDate = BillDateUtil.getMonthToDate(contract, today, 1);
	
	    	DayGM dayGM = new DayGM();
	    	dayGM.setChannel(DefaultChannel.Usage.getCode());
	    	dayGM.setContract(contract);
	    	dayGM.setMDevType(DeviceType.Meter.name());
	
	    	Double usage = dayGMDao.getDayGMsUsageMonthToDate(dayGM, startDate, endDate);
	    	Code code = contract.getCreditType();
	
	        // 선후불 요금제가 동일 할 경우는 if문에 의한 분기가 필요없다. 현재는 선불요금제가 명확하지 않아서 if문에 의해 분기를 해 놓은 상태임
	    	if(Code.POSTPAY.equals(code.getCode())) { // 후불 요금일 경우
	    		usageUnitPrice = tariffGM==null||tariffGM.getUsageUnitPrice()==null?0.0:tariffGM.getUsageUnitPrice();
	    		bill = usage * usageUnitPrice  + basicRate;
	    	} else { // 선불 요금일 경우
	    		usageUnitPrice = tariffGM==null||tariffGM.getUsageUnitPrice()==null?0.0:tariffGM.getUsageUnitPrice();
	    		bill = usage * usageUnitPrice;
	    	}
	
			// 빌링 정보 등록
			String mdsId = (contract.getMeter() == null) ? null : contract.getMeter().getMdsId();
			
			BillingMonthGM billingMonthGM = new BillingMonthGM();
			billingMonthGM.setYyyymmdd(endDate);
			billingMonthGM.setHhmmss("000000");
			billingMonthGM.setBill(bill);
			billingMonthGM.setUsage(usage);
			billingMonthGM.setContract(contract);
			billingMonthGM.setSupplier(contract.getSupplier());
			billingMonthGM.setLocation(contract.getLocation());
			billingMonthGM.setMDevId(mdsId);
			billingMonthGM.setMDevType(DeviceType.Meter.name());
			billingMonthGM.setMeter(contract.getMeter());
			billingMonthGM.setModem((contract.getMeter() == null) ? null : contract.getMeter().getModem());
			billingMonthGM.setWriteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
			billingMonthGM.setUsageReadFromDate(startDate + "000000");
			billingMonthGM.setUsageReadToDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
			billingMonthGMDao.saveOrUpdate(billingMonthGM);
	        transactionManager.commit(txStatus);
		}catch(Exception e) {
			e.printStackTrace();
			transactionManager.rollback(txStatus);
		} finally {

		} 
		return bill;
	}

//  선불계산로직은 스케줄러로 분리
    private TariffGM getTariffGMInfo(Contract contract) {
        // 현재 일자를 취득한다.
		String today = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMdd").substring(0,8);
    	Integer tariffTypeCode = contract.getTariffIndex().getCode();

		Map<String,Object> tariffParam = new HashMap<String,Object>();

		tariffParam.put("tariffTypeCode", tariffTypeCode);
		tariffParam.put("searchDate", today);
		tariffParam.put("seasonId", seasonDao.getSeasonByMonth(today.substring(4, 6)).getId());
		
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
    private Double[] getDayValue24(DayGM dayGm) {

        Double[] dayValues = new Double[24];

        dayValues[0] = (dayGm.getValue_00() == null ? 0 : dayGm.getValue_00());
        dayValues[1] = (dayGm.getValue_01() == null ? 0 : dayGm.getValue_01());
        dayValues[2] = (dayGm.getValue_02() == null ? 0 : dayGm.getValue_02());
        dayValues[3] = (dayGm.getValue_03() == null ? 0 : dayGm.getValue_03());
        dayValues[4] = (dayGm.getValue_04() == null ? 0 : dayGm.getValue_04());
        dayValues[5] = (dayGm.getValue_05() == null ? 0 : dayGm.getValue_05());
        dayValues[6] = (dayGm.getValue_06() == null ? 0 : dayGm.getValue_06());
        dayValues[7] = (dayGm.getValue_07() == null ? 0 : dayGm.getValue_07());
        dayValues[8] = (dayGm.getValue_08() == null ? 0 : dayGm.getValue_08());
        dayValues[9] = (dayGm.getValue_09() == null ? 0 : dayGm.getValue_09());
        dayValues[10] = (dayGm.getValue_10() == null ? 0 : dayGm.getValue_10());
        dayValues[11] = (dayGm.getValue_11() == null ? 0 : dayGm.getValue_11());
        dayValues[12] = (dayGm.getValue_12() == null ? 0 : dayGm.getValue_12());
        dayValues[13] = (dayGm.getValue_13() == null ? 0 : dayGm.getValue_13());
        dayValues[14] = (dayGm.getValue_14() == null ? 0 : dayGm.getValue_14());
        dayValues[15] = (dayGm.getValue_15() == null ? 0 : dayGm.getValue_15());
        dayValues[16] = (dayGm.getValue_16() == null ? 0 : dayGm.getValue_16());
        dayValues[17] = (dayGm.getValue_17() == null ? 0 : dayGm.getValue_17());
        dayValues[18] = (dayGm.getValue_18() == null ? 0 : dayGm.getValue_18());
        dayValues[19] = (dayGm.getValue_19() == null ? 0 : dayGm.getValue_19());
        dayValues[20] = (dayGm.getValue_20() == null ? 0 : dayGm.getValue_20());
        dayValues[21] = (dayGm.getValue_21() == null ? 0 : dayGm.getValue_21());
        dayValues[22] = (dayGm.getValue_22() == null ? 0 : dayGm.getValue_22());
        dayValues[23] = (dayGm.getValue_23() == null ? 0 : dayGm.getValue_23());

        return dayValues;
    }
*/
}
