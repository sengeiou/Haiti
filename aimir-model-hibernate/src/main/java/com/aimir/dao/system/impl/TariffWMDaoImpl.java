package com.aimir.dao.system.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.stereotype.Repository;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.DefaultChannel;
import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.device.WaterMeterDao;
import com.aimir.dao.mvm.BillingDayWMDao;
import com.aimir.dao.mvm.BillingMonthWMDao;
import com.aimir.dao.mvm.DayWMDao;
import com.aimir.dao.mvm.MonthWMDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.dao.system.TariffEMDao;
import com.aimir.dao.system.TariffWMCaliberDao;
import com.aimir.dao.system.TariffWMDao;
import com.aimir.model.mvm.DayWM;
import com.aimir.model.mvm.MonthWM;
import com.aimir.model.system.Contract;
import com.aimir.model.system.TariffWM;
import com.aimir.model.system.TariffWMCaliber;
import com.aimir.model.vo.TariffWMVO;
import com.aimir.util.CalendarUtil;

@Repository(value = "tariffwmDao")
public class TariffWMDaoImpl extends AbstractHibernateGenericDao<TariffWM, Integer> implements TariffWMDao {
			
	Log logger = LogFactory.getLog(TariffWMDaoImpl.class);
	    
	@Autowired
	DayWMDao dayWMDao;
	
	@Autowired
	MonthWMDao monthWMDao;
	
	@Autowired
	TariffWMCaliberDao tariffWMCaliberDao; 
	
	@Autowired
	WaterMeterDao waterMeterDao;
	
	@Autowired
	BillingDayWMDao billingDayWMDao;
	
	@Autowired
	BillingMonthWMDao billingMonthWMDao;
	
	@Autowired
	ContractDao contractDao;

	@Autowired
    TariffEMDao tariffEMDao;

    @Autowired
    HibernateTransactionManager transactionManager;
	
	@Autowired
	protected TariffWMDaoImpl(SessionFactory sessionFactory) {
		super(TariffWM.class);
		super.setSessionFactory(sessionFactory);
	}

	@SuppressWarnings("unchecked")
	public List<Object> getYyyymmddList(Integer supplierId){

		StringBuffer sb = new StringBuffer();
		sb.append("\n SELECT   wm.yyyymmdd as yyyymmdd ");
		sb.append("\n FROM     TariffWM wm");
		if(supplierId != null) {
			sb.append("\n where    wm.tariffType.supplierId = :supplierId");
		}
		sb.append("\n GROUP BY wm.yyyymmdd ");
		sb.append("\n ORDER BY wm.yyyymmdd ");

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
		sb.append("\n       	t.supplySizeMin as supplySizeMin, ");
		sb.append("\n       	t.supplySizeMax as supplySizeMax, ");
		sb.append("\n       	t.supplySizeUnit as supplySizeUnit, ");
		sb.append("\n       	t.condition1 as condition1, ");
		sb.append("\n       	t.condition2 as condition2, ");
		sb.append("\n       	t.usageUnitPrice as usageUnitPrice, ");
		sb.append("\n       	t.shareCost as share, ");
		sb.append("\n       	t.yyyymmdd as yyyymmdd ");
		sb.append("\n FROM      TariffWM t");
		sb.append("\n WHERE     t.tariffType.supplierId =:supplierId");
		if(yyyymmdd.length() > 0 ){
			sb.append("\n WHERE     t.yyyymmdd = :yyyymmdd ");
		}
		sb.append("\n ORDER BY t.yyyymmdd, t.tariffType.id, t.supplySizeMin ");

		Query query = getSession().createQuery(sb.toString());
		query.setInteger("supplierId", supplierId);
		if(yyyymmdd.length() > 0 ){
			query.setString("yyyymmdd", yyyymmdd);
		}
		
		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		

	}
	

	@SuppressWarnings("unchecked")
	public List<TariffWMVO> getCustomerChargeMgmtList(Map<String, Object> condition){

		String yyyymmdd = (String)condition.get("yyyymmdd");
		String sUserId	= (String)condition.get("sUserId");
		
		StringBuffer sb = new StringBuffer();
		/*
		sb.append("\n SELECT	t.id as id, ");
		sb.append("\n       	t.tariffType.name as tariffType, ");
		sb.append("\n       	t.supplySizeMin as supplySizeMin, ");
		sb.append("\n       	t.supplySizeMax as supplySizeMax, ");
		sb.append("\n       	t.supplySizeUnit as supplySizeUnit, ");
		sb.append("\n       	t.condition1 as condition1, ");
		sb.append("\n       	t.condition2 as condition2, ");
		sb.append("\n       	t.usageUnitPrice as usageUnitPrice, ");
		sb.append("\n       	t.shareCost as share, ");
		sb.append("\n       	t.yyyymmdd as yyyymmdd ");
		*/
		sb.append("\n SELECT	t ");
		sb.append("\n FROM      Operator o, Customer c, Contract ct, TariffType tt,  TariffWM t");
		sb.append("\n WHERE o.id = :userId	");
		sb.append("\n AND o.loginId = c.loginId	");
		sb.append("\n AND c.id = ct.customer.id	");
		sb.append("\n AND ct.tariffIndex.id = tt.id	");
		sb.append("\n AND t.tariffType.id = tt.id	");
		
		if(yyyymmdd.length() > 0){
			sb.append("\n AND     t.yyyymmdd = :yyyymmdd ");
		}
		sb.append("\n ORDER BY t.yyyymmdd, t.tariffType.id, t.supplySizeMin ");

		Query query = getSession().createQuery(sb.toString());
		
		query.setString("userId", sUserId);

		if(yyyymmdd.length() > 0){
			query.setString("yyyymmdd", yyyymmdd);
		}

		//return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		List<TariffWM> result = query.list();
		List<TariffWMVO> tariffWMVOs = new ArrayList<TariffWMVO>();
		TariffWMVO tariffWMVO = null;
		
		for (int i = 0; i < result.size(); i++) {
			
			tariffWMVO = new TariffWMVO();
			TariffWM tariff = result.get(i);
			tariffWMVO.setId(tariff.getId());
			tariffWMVO.setTariffType(tariff.getTariffType() == null ? "" : tariff.getTariffType().getName());	
			tariffWMVO.setSupplySizeMin(tariff.getSupplySizeMin());			
			tariffWMVO.setSupplySizeMax(tariff.getSupplySizeMax());
			tariffWMVO.setSupplySizeUnit(tariff.getSupplySizeUnit());
			tariffWMVO.setCondition1(tariff.getCondition1());
			tariffWMVO.setCondition2(tariff.getCondition2());
			tariffWMVO.setUsageUnitPrice(tariff.getUsageUnitPrice());
			tariffWMVO.setShareCost(tariff.getShareCost());
			tariffWMVO.setYyyymmdd(tariff.getYyyymmdd());
			
			tariffWMVOs.add(tariffWMVO);
		}
		
		return tariffWMVOs;
	}

	public int updateData(TariffWM tariffWM) throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append("UPDATE TariffWM t ");
		sb.append("SET t.usageUnitPrice = ? ");
		sb.append("   ,t.shareCost = ? ");
		sb.append("   ,t.supplySizeMin = ? ");
		sb.append("   ,t.supplySizeMax = ? ");
		sb.append("   ,t.condition1 = ? ");
		sb.append("   ,t.condition2 = ? ");
		sb.append("WHERE t.id = ? ");

		//HQL문을 이용한 CUD를 할 경우에는 getHibernateTemplate().bulkUpdate() 메소드를 사용한다.	
		Query query = getSession().createQuery(sb.toString());
		query.setParameter(1, tariffWM.getUsageUnitPrice());
		query.setParameter(2, tariffWM.getShareCost());
		query.setParameter(3, tariffWM.getSupplySizeMin());
		query.setParameter(4, tariffWM.getSupplySizeMax());
		query.setParameter(5, tariffWM.getCondition1());
		query.setParameter(6, tariffWM.getCondition2());
		query.setParameter(7, tariffWM.getId());
		return query.executeUpdate();
        /*return this.getHibernateTemplate().bulkUpdate(sb.toString(),
                new Object[] {tariffWM.getUsageUnitPrice(),
                              tariffWM.getShareCost(),
                              tariffWM.getSupplySizeMin(),
                              tariffWM.getSupplySizeMax(),
                              tariffWM.getCondition1(),
                              tariffWM.getCondition2(),
                              tariffWM.getId()});*/
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
	public Double getUsageChargeByContract(Map<String, Object> params){
		Contract contract = (Contract)params.get("contract");
		String dateType = (String)params.get("dateType");
		String startDate = (String)params.get("startDate");
		String endDate = (String)params.get("endDate");
		
		if(contract==null || "".equals(dateType) || "".equals(startDate) || "".equals(endDate)){
			return 0.0;
		}
		
		
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
			tariffParam.put("caliber", waterMeterDao.get(contract.getMeter().getId()).getMeterSize());
			
			List<TariffWM> tariffWMList = null;
			MonthWM monthWM = null;
			DayWM dayWM = null;
			
	    	if(CommonConstants.DateType.DAILY.getCode().equals(dateType)){
	    		int period=0;
	    		for(int i=Integer.parseInt(startDate);i<=Integer.parseInt(endDate);i=Integer.parseInt(CalendarUtil.getDateWithoutFormat(Integer.toString(i),Calendar.DATE, 1))){
	    			dayWM = new DayWM();
	    			tariffParam.put("searchDate", Integer.toString(i));
	    			tariffWMList = getApplyedTariff(tariffParam);
	    			
	    			tariffParam.put("yyyymmdd", Integer.toString(i));
	    			//dayWM =dayWMDao.getDayWM(tariffParam);
	    			dayWM =dayWMDao.getDayWMbySupplierId(tariffParam);
	    			
	    			double dayTotal = dayWM==null || dayWM.getTotal() == null ?0.0 : dayWM.getTotal();
	    			
	    			//사용량구간별 요금계산
	    			for(TariffWM tariffWM:tariffWMList){
	    				if(tariffWM.getSupplySizeMax()!=null && tariffWM.getSupplySizeMax() <= dayTotal){
	    					chargeSum = chargeSum + tariffWM.getUsageUnitPrice() * (tariffWM.getSupplySizeMax()-tariffWM.getSupplySizeMin()); 
	    				}else{
	    					chargeSum = chargeSum + tariffWM.getUsageUnitPrice() * (dayTotal - tariffWM.getSupplySizeMin());
	    					break;
	    				}
	    			}
	    			period++;
	    		}
	    		
	    		// 구경별 기본요금
	    		TariffWMCaliber tariffWMCaliber = tariffWMCaliberDao.getTariffWMCaliberByCaliber(tariffParam);    			
	    		chargeSum = chargeSum + Math.round(tariffWMCaliber.getBasicRate()*period/30);;
	    		
				
	    	}else if(CommonConstants.DateType.MONTHLY.getCode().equals(dateType)){
	    		startDate = startDate.substring(0, 6)+"01";
	    		endDate = endDate.substring(0, 6)+"31";

	    		for(int i=Integer.parseInt(startDate);i<=Integer.parseInt(endDate);i=Integer.parseInt(CalendarUtil.getDateWithoutFormat(Integer.toString(i),Calendar.MONTH, 1))){
	    			monthWM = new MonthWM();
	    			tariffParam.put("searchDate", Integer.toString(i));
	    			tariffWMList = getApplyedTariff(tariffParam);
	    			
	    			tariffParam.put("yyyymm", Integer.toString(i).substring(0, 6));
//	    			monthWM =monthWMDao.getMonthWM(tariffParam);
	    			monthWM =monthWMDao.getMonthWMbySupplierId(tariffParam);
	    			
	    			double monthTotal = 0d;
	    			if(monthWM != null) {
	    				monthTotal = monthWM.getTotal();
		    			
		    			//사용량구간별 요금계산
		    			for(TariffWM tariffWM:tariffWMList){
		    				
		    				if(tariffWM.getSupplySizeMax()!=null && tariffWM.getSupplySizeMax() <= monthTotal){
		    					chargeSum = chargeSum + tariffWM.getUsageUnitPrice() * (tariffWM.getSupplySizeMax()-tariffWM.getSupplySizeMin()); 
		    				}else{
		    					chargeSum = chargeSum + tariffWM.getUsageUnitPrice() * (monthTotal - tariffWM.getSupplySizeMin());
		    					break;
		    				}
		    			}
		    			// 구경별 기본요금
		        		TariffWMCaliber tariffWMCaliber = tariffWMCaliberDao.getTariffWMCaliberByCaliber(tariffParam);    	
		        		if(tariffWMCaliber != null) {
		        			chargeSum = chargeSum + tariffWMCaliber.getBasicRate();
		        		}
	    			}
	    		}
	    	}
			
		} catch (Exception e)
		{
			// TODO: handle exception
			
			e.printStackTrace();
			chargeSum= 0.0;
			
		}

		
		return chargeSum;
		
	}
	
	@SuppressWarnings("unchecked")
	public List<TariffWM> getApplyedTariff(Map<String, Object> params){

		Integer tariffTypeCode = (Integer)params.get("tariffTypeCode");
		String searchDate = (String)params.get("searchDate");
		
		StringBuffer sb = new StringBuffer();
		sb.append("\n FROM  TariffWM ");
		sb.append("\n WHERE tariffType.code = :tariffTypeCode");
		sb.append("\n AND yyyymmdd <= :searchDate  ");
		sb.append("\n ORDER BY supplySizeMin asc");

		Query query = getSession().createQuery(sb.toString());
		query.setInteger("tariffTypeCode", tariffTypeCode);
		query.setString("searchDate", searchDate);

		List<TariffWM> tariffWmList = query.list();
		
		return tariffWmList;
	}

/*	선불계산로직은 스케줄러로 분리
 * public Double saveWMChargeUsingDailyUsage(Contract contract) {
        // TODO - 프로젝트별 요금제 분기. 미개발
        // SPASA 요금제
        return saveWMChargeUsingDailyUsageSpasa(contract);
	}*/

	/**
	 * 선후불 체크. SPASA는 선불요금만 계산한다.
	 * 
	 * @param contract
	 * @return
	 */
	/*
	 * 선불계산로직은 스케줄러로 분리
	public Double saveWMChargeUsingDailyUsageSpasa(Contract contract) {
	    int rateType = 0;  // 0: none, 1: 단가계산, 2: TOU
	    Double rtnVal = 0d;
        Code code = contract.getCreditType();

        if(Code.PREPAY.equals(code.getCode()) || Code.EMERGENCY_CREDIT.equals(code.getCode())) { // 선불 요금일 경우
            rateType = 1;
        }
//        else {    // 후불 요금일 경우
//            rateType = 1;
//        }

        switch(rateType) {
            case 1:
                // 단가방식 계산
                rtnVal = this.saveWMChargeUsingDailyUsageUnitCost(contract);
                break;
                
//            case 2:
//                // TOU방식 계산. EM 호출
//                rtnVal = tariffEMDao.saveEmBillingDailyWithTariffEM(contract);
//                break;

        }
        
        return rtnVal;
	}
*/
    /**
     * 사용량을 단가방식으로 계산한다. SPASA
     *
     * @param contract
     * @return
     */
	/*
	 * 선불계산로직은 스케줄러로 분리
    public Double saveWMChargeUsingDailyUsageUnitCost(Contract contract) {
        Double bill = 0d;
        Double[] dataValue = null;
        double usage = 0d;
        // 정확한 숫자계산을 위해 BigDecimal 사용
        BigDecimal bdBill = null;                   // 사용요금
        BigDecimal bdCurBill = null;                // 기존요금
        BigDecimal bdSumBill = new BigDecimal(0d);  // 사용요금의 합
        BigDecimal bdUsage = null;                   // 사용량
        BillingDayWM _billingDayWM = null;
        String saveReadFromDateYYYYMMDDHHMMSS = null;
        String saveReadToDateYYYYMMDDHHMMSS = null;
        String newReadFromDateYYYYMMDDHHMMSS = null;    // 마지막 일자의 새로 읽을 일자시간

        TransactionStatus txStatus = null;
        DefaultTransactionDefinition txDefine = new DefaultTransactionDefinition();
        txDefine.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);

        // 요금 정보 취득
        List<TariffWM> tariffWMList = this.getTariffWMHMInfo(contract);

        // 가장 최근에 갱신한 일별 빌링 정보 취득
        Map<String, Object> map = billingDayWMDao.getLast(contract.getId());

        BillingDayWM billingDayWM = new BillingDayWM();
        billingDayWM.setContract(contract);

        try{
            txStatus = transactionManager.getTransaction(txDefine);
            // 사용량 읽은 마지막 날짜 취득
            String readToDate = TimeUtil.getCurrentDay() + "000000";
            saveReadFromDateYYYYMMDDHHMMSS = TimeUtil.getCurrentDay() + "000000";

            if(map != null) { // 빌링에 정보가 없을때는 가장 최근의(오늘) DayEM으로 부터 빌링정보를 등록한다.
                // 사용량 읽은 마지막 날짜 취득
                if((String)map.get("usageReadToDate") != null) {
                    readToDate = (String)map.get("usageReadToDate");
//                    saveReadFromDateYYYYMMDDHHMMSS = TimeUtil.getPreHour(readToDate, -1);
                    newReadFromDateYYYYMMDDHHMMSS = TimeUtil.getPreHour(readToDate, -1);
                } else {
                    // 사용량 읽은 마지막 날짜가 null 일 경우 모두 읽은 것으로 간주함.
                    readToDate = (String)map.get("lastDay") + "230000";
                    newReadFromDateYYYYMMDDHHMMSS = TimeUtil.getPreHour(readToDate, -1);
                }
            }

            // 일별 수도 사용량 취득
            Set<Condition> param = new HashSet<Condition>();
            param.add(new Condition("id.mdevType", new Object[]{DeviceType.Meter}, null, Restriction.EQ));
            param.add(new Condition("id.mdevId", new Object[]{contract.getMeter().getMdsId()}, null, Restriction.EQ));
            param.add(new Condition("id.channel", new Object[]{DefaultChannel.Usage.getCode()}, null, Restriction.EQ));
            param.add(new Condition("id.dst", new Object[]{0}, null, Restriction.EQ));
            param.add(new Condition("id.yyyymmdd", new Object[]{readToDate.substring(0,8)}, null, Restriction.GE));
            param.add(new Condition("id.yyyymmdd", new Object[]{readToDate.substring(0,8)}, null, Restriction.ORDERBYDESC));
            List<DayWM> dayWM = dayWMDao.getDayWMsByListCondition(param);

            String readToDateYYYYMMDD = readToDate.substring(0,8);                      // 마지막 읽은 일자
//          String readFromDateHH = saveReadFromDateYYYYMMDDHHMMSS.substring(8,10);
            String newReadFromDateHH = newReadFromDateYYYYMMDDHHMMSS.substring(8,10);       // 마지막 읽은 날의 새로 읽을 시간
            int intNewReadFromDateHH = Integer.parseInt(newReadFromDateHH);                 // 마지막 읽은 날의 새로 읽을 시간 int
            String saveReadFromDateHH = null;
            String saveReadToDateHH = "23";

//          saveReadFromDateYYYYMMDDHHMMSS = TimeUtil.getPreHour(readToDate, -1);
            boolean flg = true;

            if (dayWM.size() != 0) {
                for (int i = 0 ; i < dayWM.size() ; i++) {
                    bdUsage = new BigDecimal(0d);
                    bdBill = new BigDecimal(0d);
                    dataValue =  this.getDayValue24(dayWM.get(i));
                    billingDayWM.setYyyymmdd(dayWM.get(i).getYyyymmdd());
                    List<BillingDayWM> list_billingDayWM = billingDayWMDao.getBillingDayWMs(billingDayWM, null, null);
                    _billingDayWM = list_billingDayWM.size() != 0 ? list_billingDayWM.get(0) : null;
//                    Double dailyBill = list_billingDayWM.size() != 0 ? list_billingDayWM.get(0).getBill() : 0d;
                    bdCurBill = _billingDayWM != null ? new BigDecimal(_billingDayWM.getBill()) : new BigDecimal(0d);

                    // 마지막 읽은 날의 남은 시간에 대한 사용량을 더한다.
                    // 예) 10일 22시까지 읽었으면 이번에는 10일 23시의 사용량부터 계산하도록 하기 위해서
                    if (readToDateYYYYMMDD.equals(dayWM.get(i).getYyyymmdd())) {
                        if (intNewReadFromDateHH == 0) {
                            // 새로 읽을 시간이 0 이면 skip.(마지막 읽은 시간이 23시임)
                            continue;
                        }

                        for (int j = 0; j < dataValue.length; j++) {

                            if(intNewReadFromDateHH <= j) {
                                if(flg) {
                                    saveReadFromDateHH = newReadFromDateHH;
                                    flg = false;
                                }
                                bdUsage = bdUsage.add(new BigDecimal(dataValue[j]));
                            }
                        }
                        flg = true;
                        saveReadFromDateYYYYMMDDHHMMSS = dayWM.get(i).getYyyymmdd() + saveReadFromDateHH + "0000";
                    } else { // 마지막 읽은 날짜와 같지 않을 경우는 전체 사용량을 읽는다.
                        bdUsage = bdUsage.add(new BigDecimal(dayWM.get(i).getTotal()));
                        saveReadFromDateYYYYMMDDHHMMSS = dayWM.get(i).getYyyymmdd() + "000000";
                    }

                    // 가장 최근 데이터일 경우, 언제까지 사용량을 읽었는지 계산한다.
                    for (int k = dataValue.length -1 ; k >= 0; k--) {
                        if (dataValue[k] != 0.0) {
                            saveReadToDateHH = (String.valueOf(k).length() == 1 ? "0"+k : String.valueOf(k)); // 마지막 시간 취득
                            break;
                        }
                    }

                    saveReadToDateYYYYMMDDHHMMSS = dayWM.get(i).getYyyymmdd() + saveReadToDateHH + "0000";
    //              Double usage = (dayWM==null || dayWM.getTotal() == null ?0.0 : dayWM.getTotal());
//                  Code code = contract.getCreditType();

                    //사용량구간별 요금계산
//                    for (TariffWM tariffWM : tariffWMList) {
//                        if (tariffWM.getSupplySizeMax() != null && tariffWM.getSupplySizeMax() <= usage) {
////                            bill = bill + tariffWM.getUsageUnitPrice() * (tariffWM.getSupplySizeMax()-tariffWM.getSupplySizeMin());
//                            bdBill = bdBill.add(new BigDecimal(tariffWM.getUsageUnitPrice()).multiply(new BigDecimal(tariffWM.getSupplySizeMax()).subtract(new BigDecimal(tariffWM.getSupplySizeMin()))));
//                        } else {
////                            bill = bill + tariffWM.getUsageUnitPrice() * (usage - tariffWM.getSupplySizeMin());
//                            bdBill = bdBill.add(new BigDecimal(tariffWM.getUsageUnitPrice()).multiply(new BigDecimal(usage).subtract(new BigDecimal(tariffWM.getSupplySizeMin()))));
//                            break;
//                        }
//                    }

                    // 사용량 요금 계산.단가방식
                    if (tariffWMList != null && tariffWMList.size() > 0) {
                        TariffWM tariffWM = (TariffWM)tariffWMList.get(0);
                        bdBill = bdUsage.multiply(new BigDecimal(tariffWM.getUsageUnitPrice()));

                        // 세금 계산
                        if (tariffWM.getShareCost() != null) {
                            bdBill = bdBill.add(bdUsage.multiply(new BigDecimal(tariffWM.getShareCost())));
                        }
                    }

                    bdSumBill = bdSumBill.add(bdBill); // 계산된 요금을 더한다.

                    // Billing_Day_Wm에 정보 등록
                    if (_billingDayWM != null) {
                        _billingDayWM.setBill(bdCurBill.add(bdBill).doubleValue());
                        _billingDayWM.setUsageReadFromDate(saveReadFromDateYYYYMMDDHHMMSS);
                        _billingDayWM.setUsageReadToDate(saveReadToDateYYYYMMDDHHMMSS);
                    } else {
                        _billingDayWM = new BillingDayWM();
                        String mdsId = (contract.getMeter() == null) ? null : contract.getMeter().getMdsId();
                        _billingDayWM.setYyyymmdd(saveReadToDateYYYYMMDDHHMMSS.substring(0,8));
                        _billingDayWM.setHhmmss("000000");
                        _billingDayWM.setMDevId(mdsId);
                        _billingDayWM.setMDevType(DeviceType.Meter.name());
                        _billingDayWM.setBill(bdCurBill.add(bdBill).doubleValue());
                        _billingDayWM.setUsage(bdUsage.doubleValue());
                        _billingDayWM.setContract(contract);
                        _billingDayWM.setSupplier(contract.getSupplier());
                        _billingDayWM.setLocation(contract.getLocation());
                        _billingDayWM.setMeter(contract.getMeter());
                        _billingDayWM.setModem((contract.getMeter() == null) ? null : contract.getMeter().getModem());
                        _billingDayWM.setWriteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
                        _billingDayWM.setUsageReadFromDate(saveReadFromDateYYYYMMDDHHMMSS);
                        _billingDayWM.setUsageReadToDate(saveReadToDateYYYYMMDDHHMMSS);
                    }
                    billingDayWMDao.saveOrUpdate(_billingDayWM);
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
//                    contractDao.saveOrUpdate(contract);
                    contractDao.update(contract);
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
	public Double saveWMChargeUsingMonthlyUsage(Contract contract) {
		Double bill = 10000d;
        TransactionStatus txStatus = null;
        DefaultTransactionDefinition txDefine = new DefaultTransactionDefinition();
        txDefine.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        try{
	        txStatus = transactionManager.getTransaction(txDefine);
	   	 // 현재 일자를 취득한다.
			String today = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMdd").substring(0,8);
			// 과금일 취득
	        String startDate = BillDateUtil.getBillDate(contract, today, -1);
	        // Month To Date취득
	        String endDate = BillDateUtil.getMonthToDate(contract, today, 1);

			int duration = TimeUtil.getDayDuration(startDate, endDate); //요금계산하는 실제 사용기간	
	
	    	// 요금 정보 취득
	    	List<TariffWM> tariffWMList = this.getTariffWMHMInfo(contract);
			// 월 사용량 취득
	    	DayWM dayWM = new DayWM();
	    	dayWM.setChannel(DefaultChannel.Usage.getCode());
	    	dayWM.setContract(contract);
	    	dayWM.setMDevType(DeviceType.Meter.name());
			Double usage = dayWMDao.getDayWMsUsageMonthToDate(dayWM, startDate, endDate);
	
			Code code = contract.getCreditType();

			if(Code.POSTPAY.equals(code.getCode())) { // 후불 요금일 경우
				//사용량구간별 요금계산
				for(TariffWM tariffWM:tariffWMList){
					if(tariffWM.getSupplySizeMax()!=null && tariffWM.getSupplySizeMax() <= usage){
						bill = bill +tariffWM.getUsageUnitPrice() * (tariffWM.getSupplySizeMax()-tariffWM.getSupplySizeMin()); 
					}else{
						bill = bill + tariffWM.getUsageUnitPrice() * (usage - tariffWM.getSupplySizeMin());
						break;
					}
				}

				Map<String, Object> param = new HashMap<String, Object>();
				param.put("supplierId", contract.getSupplier().getId());
				param.put("caliber", waterMeterDao.get(contract.getMeter().getId()).getMeterSize());
				
				// 구경별 기본요금  			
				bill = bill + Math.round((this.getTariffWMCaliber(contract) == null || this.getTariffWMCaliber(contract).getBasicRate() == null ? 0d : this.getTariffWMCaliber(contract).getBasicRate())*duration/30);
			} else { // 선불 요금일 경우
				for(TariffWM tariffWM:tariffWMList){
						bill = bill + usage*tariffWM.getUsageUnitPrice();
				}
			}

			// 빌링 정보 등록
			String mdsId = (contract.getMeter() == null) ? null : contract.getMeter().getMdsId();
			
			BillingMonthWM billingMonthWM = new BillingMonthWM();
			billingMonthWM.setYyyymmdd(endDate);
			billingMonthWM.setHhmmss("000000");
			billingMonthWM.setBill(bill);
			billingMonthWM.setUsage(usage);
			billingMonthWM.setContract(contract);
			billingMonthWM.setSupplier(contract.getSupplier());
			billingMonthWM.setLocation(contract.getLocation());
			billingMonthWM.setMDevId(mdsId);
			billingMonthWM.setMDevType(DeviceType.Meter.name());
			billingMonthWM.setMeter(contract.getMeter());
			billingMonthWM.setModem((contract.getMeter() == null) ? null : contract.getMeter().getModem());
			billingMonthWM.setWriteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
			billingMonthWM.setUsageReadFromDate(startDate + "000000");
			billingMonthWM.setUsageReadToDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
			billingMonthWMDao.saveOrUpdate(billingMonthWM); 
	        transactionManager.commit(txStatus);
		}catch(Exception e) {
			e.printStackTrace();
			transactionManager.rollback(txStatus);
		} finally {
		} 
		return bill;
	}
//  선불계산로직은 스케줄러로 분리
    private List<TariffWM> getTariffWMHMInfo(Contract contract) {
        // 현재 일자를 취득한다.
		String today = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMdd").substring(0,8);
		Integer tariffTypeCode = contract.getTariffIndex().getCode();

		Map<String,Object> tariffParam = new HashMap<String,Object>();
		tariffParam.put("tariffTypeCode", tariffTypeCode); //
		tariffParam.put("searchDate", today);
		return this.getApplyedTariff(tariffParam);
    }

    private TariffWMCaliber getTariffWMCaliber(Contract contract) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("supplierId", contract.getSupplier().getId());
		param.put("caliber", waterMeterDao.get(contract.getMeter().getId()).getMeterSize());
		
		// 구경별 기본요금
		return tariffWMCaliberDao.getTariffWMCaliberByCaliber(param);  
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
    private Double[] getDayValue24(DayWM dayWm) {

        Double[] dayValues = new Double[24];

        dayValues[0] = (dayWm.getValue_00() == null ? 0 : dayWm.getValue_00());
        dayValues[1] = (dayWm.getValue_01() == null ? 0 : dayWm.getValue_01());
        dayValues[2] = (dayWm.getValue_02() == null ? 0 : dayWm.getValue_02());
        dayValues[3] = (dayWm.getValue_03() == null ? 0 : dayWm.getValue_03());
        dayValues[4] = (dayWm.getValue_04() == null ? 0 : dayWm.getValue_04());
        dayValues[5] = (dayWm.getValue_05() == null ? 0 : dayWm.getValue_05());
        dayValues[6] = (dayWm.getValue_06() == null ? 0 : dayWm.getValue_06());
        dayValues[7] = (dayWm.getValue_07() == null ? 0 : dayWm.getValue_07());
        dayValues[8] = (dayWm.getValue_08() == null ? 0 : dayWm.getValue_08());
        dayValues[9] = (dayWm.getValue_09() == null ? 0 : dayWm.getValue_09());
        dayValues[10] = (dayWm.getValue_10() == null ? 0 : dayWm.getValue_10());
        dayValues[11] = (dayWm.getValue_11() == null ? 0 : dayWm.getValue_11());
        dayValues[12] = (dayWm.getValue_12() == null ? 0 : dayWm.getValue_12());
        dayValues[13] = (dayWm.getValue_13() == null ? 0 : dayWm.getValue_13());
        dayValues[14] = (dayWm.getValue_14() == null ? 0 : dayWm.getValue_14());
        dayValues[15] = (dayWm.getValue_15() == null ? 0 : dayWm.getValue_15());
        dayValues[16] = (dayWm.getValue_16() == null ? 0 : dayWm.getValue_16());
        dayValues[17] = (dayWm.getValue_17() == null ? 0 : dayWm.getValue_17());
        dayValues[18] = (dayWm.getValue_18() == null ? 0 : dayWm.getValue_18());
        dayValues[19] = (dayWm.getValue_19() == null ? 0 : dayWm.getValue_19());
        dayValues[20] = (dayWm.getValue_20() == null ? 0 : dayWm.getValue_20());
        dayValues[21] = (dayWm.getValue_21() == null ? 0 : dayWm.getValue_21());
        dayValues[22] = (dayWm.getValue_22() == null ? 0 : dayWm.getValue_22());
        dayValues[23] = (dayWm.getValue_23() == null ? 0 : dayWm.getValue_23());

        return dayValues;
    }
    */
    public List<TariffWM> getNewestTariff(Contract contract) {
       		
    	StringBuffer sb = new StringBuffer();
    	sb.append("\n SELECT MAX(INTEGER(yyyymmdd)) ");
		sb.append("\n FROM  TariffWM ");
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
		sb2.append("\n FROM  TariffWM ");
		sb2.append("\n WHERE tariffType.id = :tariffId");
		sb2.append("\n AND yyyymmdd = :yyyymmdd");
		
		Query query2 = getSession().createQuery(sb2.toString());
		query2.setInteger("tariffId", contract.getTariffIndexId());
		query2.setString("yyyymmdd", yyyymmdd);
		
		List<TariffWM> tariffWMList = query2.list();

		return tariffWMList;
    }

    @Override
    public Boolean isNewDate(String yyyymmdd) {
        Boolean result = false;
        Criteria criteria = getSession().createCriteria(TariffWM.class);
        criteria.add(Restrictions.eq("yyyymmdd", yyyymmdd));
        criteria.setProjection(Projections.distinct(Projections.property("yyyymmdd")));
        Integer count = criteria.list().size();
        if ( count < 1 ) result = true;
        return result;
    }

    @Override
    public Boolean deleteYyyymmddTariff(String yyyymmdd) {
        Boolean result = true;
        try {
            Criteria criteria = getSession().createCriteria(TariffWM.class);
            criteria.add(Restrictions.eq("yyyymmdd", yyyymmdd));
            List<TariffWM> list = criteria.list();
            
            for ( TariffWM wm : list ) {
                delete(wm);
            }
            
            logger.info(yyyymmdd+ "'s tariffWM delete complete");
        } catch (Exception e) {
            result = false;
            e.printStackTrace();
        } 
        return result;
    }

    @Override
    public int tariffDeleteByCondition(Map<String, Object> condition) {
        Query query = null;
        StringBuffer sb = new StringBuffer();
        int result = 0;
        logger.info("TYPE(seasonId): " + condition.get("seasonId"));
        logger.info("tariffWMId: " + condition.get("tariffWMId"));
        logger.info("tariffTypeId: " + condition.get("tariffTypeId"));
        
        try {
            String seasonId = condition.get("seasonId") == null ? null :  (String) condition.get("seasonId");
            if(!seasonId.equals("WATER")) return result;
            
            Integer tariffWMId =
                    condition.get("tariffWMId") == null ? null : Integer.parseInt(condition.get("tariffWMId").toString());
            Integer tariffTypeId = 
                    condition.get("tariffTypeId") == null ? null : Integer.parseInt(condition.get("tariffTypeId").toString());
            
            sb.append("\nDELETE FROM TariffWM wm WHERE 1=1 ");
            if(tariffWMId != null && tariffWMId != -1) {
                sb.append("\nAND wm.id = :tariffWMId");
            }   
            if(tariffTypeId != null) {
                sb.append("\nAND wm.tariffTypeId = :tariffTypeId");
            }
            
            query = getSession().createQuery(sb.toString());
            if(tariffWMId != null && tariffWMId != -1) {
                query.setInteger("tariffWMId", tariffWMId);
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
}
