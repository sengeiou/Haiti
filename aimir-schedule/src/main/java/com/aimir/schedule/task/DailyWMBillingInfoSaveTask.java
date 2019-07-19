package com.aimir.schedule.task;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.aimir.constants.CommonConstants.DefaultChannel;
import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.mvm.BillingDayWMDao;
import com.aimir.dao.mvm.DayWMDao;
import com.aimir.dao.mvm.SeasonDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.dao.system.TariffTypeDao;
import com.aimir.dao.system.TariffWMDao;
import com.aimir.model.device.Meter;
import com.aimir.model.mvm.BillingDayWM;
import com.aimir.model.mvm.DayWM;
import com.aimir.model.system.Code;
import com.aimir.model.system.Contract;
import com.aimir.model.system.TariffType;
import com.aimir.model.system.TariffWM;
import com.aimir.util.Condition;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.Condition.Restriction;
import com.aimir.util.TimeUtil;

@Transactional
public class DailyWMBillingInfoSaveTask  extends ScheduleTask{
	
	private static Log log = LogFactory.getLog(DailyWMBillingInfoSaveTask.class);
	
    private static final String SERVICE_TYPE_WM = "WM";
    private static final String SEARCH_DATE_TYPE_DAILY = "1";
	
	@Autowired
	HibernateTransactionManager transactionManager;
	
	@Autowired
	ContractDao contractDao;
	
	@Autowired
	CodeDao codeDao;
	
	@Autowired
	SeasonDao seasonDao;
	
	@Autowired
	MeterDao meterDao;
	
	@Autowired
	DayWMDao dayWMDao;
	
	@Autowired
	TariffWMDao tariffWMDao;
	
	@Autowired
	TariffTypeDao tariffTypeDao;
	
	@Autowired
	BillingDayWMDao billingDayWMDao;
	
	public void execute(JobExecutionContext context) {
	    log.info("########### START DailyWMBillingInfo ###############");

        // 일별 수도 요금 정보 등록
        this.saveWmBillingDayInfo();
	    
	    log.info("########### END DailyWMBillingInfo ############");
	}//execute end
	
    private void saveWmBillingDayInfo() {

		// 수도 계약 정보 취득
		List<Integer> wm_contractIds = this.getContractInfos(SERVICE_TYPE_WM);
        for(Integer contract_id : wm_contractIds) {
        	// 계약 정보 취득
        	Contract contract = contractDao.get(contract_id);
        	Code code = codeDao.get(contract.getCreditTypeCodeId());
        	if(Code.PREPAYMENT.equals(code.getCode()) || Code.EMERGENCY_CREDIT.equals(code.getCode())) { // 선불 요금일 경우
        		this.saveWMChargeUsingDailyUsageUnitCost(contract);
        	}
        }
    }
 
    private List<Integer> getContractInfos(String serviceType){
    	List<Integer> contractIds = null;

        // 현재 일자를 취득한다.
		String today = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMdd").substring(0,8);

		// 일별 사용량 취득 조건을 설정한다.
    	Map<String, String> conditionMap = new HashMap<String, String>();

		conditionMap.put("startDate", today);
		conditionMap.put("endDate", today);
		conditionMap.put("locationCondition", "");
		conditionMap.put("searchDateType", SEARCH_DATE_TYPE_DAILY); // 일별
		conditionMap.put("serviceType", serviceType);
		
		// 수도
		contractIds = dayWMDao.getContractIds(conditionMap);

		return contractIds;
    }
	
	    /**
	     * 사용량을 단가방식으로 계산한다.
	     *
	     * @param contract
	     * @return
	     */
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
	        String today = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMdd").substring(0,8);
	        TariffType tariffType = tariffTypeDao.get(contract.getTariffIndexId());
			Integer tariffTypeCode = tariffType.getCode();

			Map<String,Object> tariffParam = new HashMap<String,Object>();
			tariffParam.put("tariffTypeCode", tariffTypeCode); //
			tariffParam.put("searchDate", today);
	        List<TariffWM> tariffWMList = tariffWMDao.getApplyedTariff(tariffParam);

	        // 가장 최근에 갱신한 일별 빌링 정보 취득
	        Map<String, Object> map = billingDayWMDao.getLast(contract.getId());

	        BillingDayWM billingDayWM = new BillingDayWM();
	        billingDayWM.setContract(contract);
	        
	        Meter meter = meterDao.get(contract.getMeterId());

	        try{
	            txStatus = transactionManager.getTransaction(txDefine);
	            // 사용량 읽은 마지막 날짜 취득
	            String readToDate = TimeUtil.getCurrentDay() + "000000";
	            saveReadFromDateYYYYMMDDHHMMSS = TimeUtil.getCurrentDay() + "000000";

	            if(map != null) { // 빌링에 정보가 없을때는 가장 최근의(오늘) DayEM으로 부터 빌링정보를 등록한다.
	                // 사용량 읽은 마지막 날짜 취득
	                if((String)map.get("usageReadToDate") != null) {
	                    readToDate = (String)map.get("usageReadToDate");
//	                    saveReadFromDateYYYYMMDDHHMMSS = TimeUtil.getPreHour(readToDate, -1);
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
	            param.add(new Condition("id.mdevId", new Object[]{meter.getMdsId()}, null, Restriction.EQ));
	            param.add(new Condition("id.channel", new Object[]{DefaultChannel.Usage.getCode()}, null, Restriction.EQ));
	            param.add(new Condition("id.dst", new Object[]{0}, null, Restriction.EQ));
	            param.add(new Condition("id.yyyymmdd", new Object[]{readToDate.substring(0,8)}, null, Restriction.GE));
	            param.add(new Condition("id.yyyymmdd", new Object[]{readToDate.substring(0,8)}, null, Restriction.ORDERBYDESC));
	            List<DayWM> dayWM = dayWMDao.getDayWMsByListCondition(param);

	            String readToDateYYYYMMDD = readToDate.substring(0,8);                      // 마지막 읽은 일자
//	          String readFromDateHH = saveReadFromDateYYYYMMDDHHMMSS.substring(8,10);
	            String newReadFromDateHH = newReadFromDateYYYYMMDDHHMMSS.substring(8,10);       // 마지막 읽은 날의 새로 읽을 시간
	            int intNewReadFromDateHH = Integer.parseInt(newReadFromDateHH);                 // 마지막 읽은 날의 새로 읽을 시간 int
	            String saveReadFromDateHH = null;
	            String saveReadToDateHH = "23";

//	          saveReadFromDateYYYYMMDDHHMMSS = TimeUtil.getPreHour(readToDate, -1);
	            boolean flg = true;

	            if (dayWM.size() != 0) {
	                for (int i = 0 ; i < dayWM.size() ; i++) {
	                    bdUsage = new BigDecimal(0d);
	                    bdBill = new BigDecimal(0d);
	                    dataValue =  this.getDayValue24(dayWM.get(i));
	                    billingDayWM.setYyyymmdd(dayWM.get(i).getYyyymmdd());
	                    List<BillingDayWM> list_billingDayWM = billingDayWMDao.getBillingDayWMs(billingDayWM, null, null);
	                    _billingDayWM = list_billingDayWM.size() != 0 ? list_billingDayWM.get(0) : null;
//	                    Double dailyBill = list_billingDayWM.size() != 0 ? list_billingDayWM.get(0).getBill() : 0d;
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
//	                  Code code = contract.getCreditType();

	                    //사용량구간별 요금계산
//	                    for (TariffWM tariffWM : tariffWMList) {
//	                        if (tariffWM.getSupplySizeMax() != null && tariffWM.getSupplySizeMax() <= usage) {
////	                            bill = bill + tariffWM.getUsageUnitPrice() * (tariffWM.getSupplySizeMax()-tariffWM.getSupplySizeMin());
//	                            bdBill = bdBill.add(new BigDecimal(tariffWM.getUsageUnitPrice()).multiply(new BigDecimal(tariffWM.getSupplySizeMax()).subtract(new BigDecimal(tariffWM.getSupplySizeMin()))));
//	                        } else {
////	                            bill = bill + tariffWM.getUsageUnitPrice() * (usage - tariffWM.getSupplySizeMin());
//	                            bdBill = bdBill.add(new BigDecimal(tariffWM.getUsageUnitPrice()).multiply(new BigDecimal(usage).subtract(new BigDecimal(tariffWM.getSupplySizeMin()))));
//	                            break;
//	                        }
//	                    }

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
	                        String mdsId = (meter == null) ? null : meter.getMdsId();
	                        _billingDayWM.setYyyymmdd(saveReadToDateYYYYMMDDHHMMSS.substring(0,8));
	                        _billingDayWM.setHhmmss("000000");
	                        _billingDayWM.setMDevId(mdsId);
	                        _billingDayWM.setMDevType(DeviceType.Meter.name());
	                        _billingDayWM.setBill(bdCurBill.add(bdBill).doubleValue());
	                        _billingDayWM.setUsage(bdUsage.doubleValue());
	                        _billingDayWM.setContract(contract);
	                        _billingDayWM.setSupplier(contract.getSupplier());
	                        _billingDayWM.setLocation(contract.getLocation());
	                        _billingDayWM.setMeter(meter);
	                        _billingDayWM.setModem((meter == null) ? null : meter.getModem());
	                        _billingDayWM.setWriteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
	                        _billingDayWM.setUsageReadFromDate(saveReadFromDateYYYYMMDDHHMMSS);
	                        _billingDayWM.setUsageReadToDate(saveReadToDateYYYYMMDDHHMMSS);
	                    }
	                    billingDayWMDao.saveOrUpdate(_billingDayWM);
	                }
	                // contract 정보 갱신
	                Code code = codeDao.get(contract.getCreditTypeCodeId());
	                if(Code.PREPAYMENT.equals(code.getCode()) || Code.EMERGENCY_CREDIT.equals(code.getCode())) { // 선불 요금일 경우
//	                    logger.info("###################################################################################");
//	                    logger.info("bill : " + bdBill.doubleValue());
//	                    logger.info("sumBill : " + bdSumBill.doubleValue());
//	                    logger.info("currentCredit : " + contract.getCurrentCredit());
//	                    logger.info("###################################################################################");
	                    BigDecimal bdCurCredit = (contract.getCurrentCredit() != null) ? new BigDecimal(contract.getCurrentCredit()) : new BigDecimal(0d);

	                    // 현재 잔액에서 사용요금을 차감한다.
	                    contract.setCurrentCredit(bdCurCredit.subtract(bdSumBill).doubleValue());
//	                    contractDao.saveOrUpdate(contract);
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
	    
	    /**
	     * method name : getDayValue24
	     * method Desc : WM
	     *
	     * @param meteringMonth
	     * @return
	     */
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

}