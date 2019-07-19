package com.aimir.schedule.task;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import com.aimir.dao.mvm.BillingDayEMDao;
import com.aimir.dao.mvm.DayEMDao;
import com.aimir.dao.mvm.SeasonDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.dao.system.TOURateDao;
import com.aimir.dao.system.TariffEMDao;
import com.aimir.dao.system.TariffTypeDao;
import com.aimir.model.device.Meter;
import com.aimir.model.mvm.BillingDayEM;
import com.aimir.model.mvm.DayEM;
import com.aimir.model.mvm.Season;
import com.aimir.model.system.Code;
import com.aimir.model.system.Contract;
import com.aimir.model.system.TOURate;
import com.aimir.model.system.TariffEM;
import com.aimir.model.system.TariffType;
import com.aimir.util.CalendarUtil;
import com.aimir.util.Condition;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.StringUtil;
import com.aimir.util.Condition.Restriction;
import com.aimir.util.TimeUtil;

@Transactional
public class SPASATariffDailyEMBillingInfoSaveTask extends ScheduleTask {
	
	private static Log log = LogFactory.getLog(SPASATariffDailyEMBillingInfoSaveTask.class);
	
    private static final String SERVICE_TYPE_EM = "EM";
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
	TOURateDao touRateDao;
	
	@Autowired
	DayEMDao dayEMDao;
	
	@Autowired
	TariffEMDao tariffEMDao;	
	
	@Autowired
	TariffTypeDao tariffTypeDao;
	
	@Autowired
	BillingDayEMDao billingDayEMDao;

	private boolean isNowRunning = false;
	
	@Override
	public void execute(JobExecutionContext context) {
		if(isNowRunning){
			log.info("########### SPASATariffDailyEMBillingInfoSaveTask is already running...");
			return;
		}
		isNowRunning = true;
	    log.info("########### START SPASATariffDailyEMBillingInfo ###############");
	    
        // 일별 전기 요금 정보 등록
        this.saveEmBillingDayInfo();

	    log.info("########### END SPASATariffDailyEMBillingInfo ############");
	    isNowRunning = false;
	}//execute end
	 
    public void saveEmBillingDayInfo() {
    	 
    	// 전기 계약 정보 취득
        List<Integer> em_contractIds = this.getContractInfos(SERVICE_TYPE_EM); 

        for(Integer contract_id : em_contractIds) {
            log.info("ContractId[" + contract_id + "]");
        	Contract contract = contractDao.get(contract_id);
        	Code code = codeDao.get(contract.getCreditTypeCodeId());
        	if(Code.PREPAYMENT.equals(code.getCode()) || Code.EMERGENCY_CREDIT.equals(code.getCode())) { // 선불 요금일 경우
        		this.saveEmBillingDailyWithTOUTariffEM(contract);
        	}
        }
    }

    private List<Integer> getContractInfos(String serviceType){
    	List<Integer> contractIds = null;

        // 현재 일자를 취득한다.
		String today = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMdd").substring(0,8);

		// 일별 사용량 취득 조건을 설정한다.
    	Map<String, String> conditionMap = new HashMap<String, String>();
//
		conditionMap.put("startDate", today);
		conditionMap.put("endDate", today);
		conditionMap.put("locationCondition", "");
		conditionMap.put("searchDateType", SEARCH_DATE_TYPE_DAILY); // 일별
		conditionMap.put("serviceType", serviceType);

		// 전기 
		contractIds = dayEMDao.getContractIds(conditionMap);
		
		return contractIds;
    }
	
    /**
     * 사용량의 요금계산을 TOU 방식으로 계산한다.(임시)
     * @param contract
     * @return
     */
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
        Meter meter = meterDao.get(contract.getMeterId());
        String mdsId = (meter == null) ? null : meter.getMdsId();

        // 계약에 해당하는 요금 정보 취득
        String today = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMdd").substring(0,8);
        TariffType tariffType = tariffTypeDao.get(contract.getTariffIndexId()); 
    	Integer tariffTypeCode = tariffType.getCode();
        Map<String, Object> tariffParam = new HashMap<String, Object>();

		tariffParam.put("tariffTypeCode", tariffTypeCode);
		tariffParam.put("tariffIndex", tariffType);
		tariffParam.put("searchDate", today);
        List<TariffEM> tariffEMList = tariffEMDao.getApplyedTariff(tariffParam);

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
                        _billingDayEM.setMeter(meter);
                        _billingDayEM.setModem((meter == null) ? null : meter.getModem());
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
                Code code = codeDao.get(contract.getCreditTypeCodeId());
                if(Code.PREPAYMENT.equals(code.getCode()) || Code.EMERGENCY_CREDIT.equals(code.getCode())) { // 선불 요금일 경우
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
    
    /**
     * DayEM 별 TOU 방식으로 사용량의 요금을 계산한다.
     * 
     * @param contract
     * @param tariffEMList
     * @param usage
     * @param dayValues
     * @return
     */
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
    
    /**
     * 현재일자의 Season 모델객체를 가져온다.
     * 
     * @return Season 모델객체
     */
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
    
    /**
     * 해당 시간이 조건범위에 포함되는지 체크한다.
     * 
     * @param targetHour 해당 시간
     * @param startHour 시작 시간
     * @param endHour 종료 시간
     * @return
     */
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

		  /**
	     * method name : getDayValue24
	     * method Desc : EM
	     *
	     * @param meteringMonth
	     * @return
	     */
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

}