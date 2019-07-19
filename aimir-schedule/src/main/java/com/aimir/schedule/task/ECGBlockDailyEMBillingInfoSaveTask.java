package com.aimir.schedule.task;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.util.StringUtils;

import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.mvm.BillingBlockTariffDao;
import com.aimir.dao.mvm.DayEMDao;
import com.aimir.dao.mvm.MonthEMDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.dao.system.PrepaymentLogDao;
import com.aimir.dao.system.SupplyTypeDao;
import com.aimir.dao.system.TariffEMDao;
import com.aimir.dao.system.TariffTypeDao;
import com.aimir.model.device.Meter;
import com.aimir.model.mvm.BillingBlockTariff;
import com.aimir.model.mvm.MonthEM;
import com.aimir.model.system.Code;
import com.aimir.model.system.Contract;
import com.aimir.model.system.PrepaymentLog;
import com.aimir.model.system.SupplyType;
import com.aimir.model.system.TariffEM;
import com.aimir.model.system.TariffType;
import com.aimir.util.CalendarUtil;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.TimeUtil;

@Transactional
public class ECGBlockDailyEMBillingInfoSaveTask  extends ScheduleTask{
    
    protected static Log log = LogFactory.getLog(ECGBlockDailyEMBillingInfoSaveTask.class);
    private static final String SERVICE_TYPE_EM = "EM";
    private static final String SEARCH_DATE_TYPE_DAILY = "1";

    @Autowired
    HibernateTransactionManager transactionManager;
    
    @Autowired
    ContractDao contractDao;
    
    @Autowired
    CodeDao codeDao;
    
    @Autowired
    MeterDao meterDao;
    
    @Autowired
    DayEMDao dayEMDao;

    @Autowired
    MonthEMDao monthEMDao;  
    
    @Autowired
    TariffEMDao tariffEMDao;    

    @Autowired
    TariffTypeDao tariffTypeDao;
    
    @Autowired
    SupplyTypeDao supplyTypeDao;
    
    @Autowired
    BillingBlockTariffDao billingBlockTariffDao;

    @Autowired
    PrepaymentLogDao prepaymentLogDao;
    
    private boolean isNowRunning = false;
    
    /**
     * 
     * @author jiae
     * @desc   가나 ECG 에서 사용하는 채널
     *
     */
    enum KamstrupChannel {
        ActiveEnergyImp(1), ActiveEnergyExp(2), ReactiveEnergyImp(3), ReactiveEnergyExp(4);
        
        private Integer channel;
        
        KamstrupChannel(Integer channel) {
            this.channel = channel;
        }
        
        public Integer getChannel() {
            return this.channel;
        }
    }
    
    public void execute(JobExecutionContext context) {
		if(isNowRunning){
			log.info("########### ECGBlockDailyEMBillingInfoSaveTask is already running...");
			return;
		}
		isNowRunning = true;
		
        log.info("########### START ECGBlockDailyEMBillingInfo ###############");
        
        // 일별 전기 요금 정보 등록
        this.saveEmBillingDayInfo();
        
        log.info("########### END ECGBlockDailyEMBillingInfo ############");
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
                this.saveEmBillingDailyWithTariffEMCumulationCost(contract);
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
    
    @Transactional
     private Double saveEmBillingDailyWithTariffEMCumulationCost(Contract contract) {
            SimpleDateFormat sd = new SimpleDateFormat("yyyyMMddHHmmss");
            
            String mdsId = meterDao.get(contract.getMeterId()).getMdsId();
            String currentDate = sd.format(new Date()).substring(0,10);
            
            //선불 로그 테이블에 저장할 금액
            BigDecimal prepayLogCredit = new BigDecimal("0");
            //선불 로그 테이블에 저장할 사용량
            Double prepayLogUsage = 0d;
            //차감스케줄을 저장할 변수
            PrepaymentLog prepaymentLog = new PrepaymentLog();
            
            // Billing_Day_Em에 정보 등록
            List<BillingBlockTariff> billingBlockTariffList = null;
            BillingBlockTariff _billingBlockTariff = null;
            // 검침값이 2시간이 밀려서 들어오므로 누적액이 저장된 마지막 billing정보의 갱신도 필요하다.
            List<BillingBlockTariff> lastBillingBlockTariff = null;
            BillingBlockTariff _lastBillingBlockTariff = null;
            
             TransactionStatus txStatus = null;
             DefaultTransactionDefinition txDefine = new DefaultTransactionDefinition();
             txDefine.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
            try {
                txStatus = transactionManager.getTransaction(txDefine);
                //해당 Tariff 정보를 가져온다.
                String today = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMdd").substring(0,8);
                TariffType tariffType = tariffTypeDao.get(contract.getTariffIndexId()); 
                Integer tariffTypeCode = tariffType.getCode();
                Map<String, Object> tariffParam = new HashMap<String, Object>();

                tariffParam.put("tariffTypeCode", tariffTypeCode);
                tariffParam.put("tariffIndex", tariffType);
                tariffParam.put("searchDate", today);
                List<TariffEM> tariffEMList = tariffEMDao.getApplyedTariff(tariffParam); 
                
                //과금일을 가져온다.
                SupplyType supplyType = supplyTypeDao.getSupplyTypeBySupplierIdTypeId(contract.getSupplierId(), codeDao.getCodeIdByCode("3.1")).get(0);
                
                //마지막 누적요금이 저장된 데이터를 가지고 온다.
                List<Map<String, Object>> billingBlockTariff = billingBlockTariffDao.getLastAccumulateBill(mdsId);
                //마지막 누적요금 저장
                BigDecimal preAccumulateBill = null;
                //마지막 누적사용량 저장
                Double preAccumulateUsage = null;
                //마지막 누적요금이 저장된 날짜
                String lastAccumulateDate = null;
                //마지막 누적요금이 저장된 달의 마지막 날
                String lastAccumulateLastDate = null;
                //하루전날 누적요금이 저장된 데이터를 가지고 온다.
                List<Map<String, Object>> preDaybillingBlockTariff = null;
                
                if(billingBlockTariff.size() <= 0) {
                    //마지막 누적 요금이 저장된 billingBlockTariff이 없을 경우 한번도 선불스케줄을 돌리지 않은것으로 간주
                    preAccumulateBill = new BigDecimal("0");
                    preAccumulateUsage = 0.0;
                    lastAccumulateDate = monthEMDao.getMonthByMinDate(mdsId).get(0).get("YYYYMM").toString()+"00";
                } else {
                    preAccumulateBill = new BigDecimal(Double.parseDouble(billingBlockTariff.get(0).get("ACCUMULATEBILL").toString()));
                    preAccumulateUsage = Double.parseDouble(billingBlockTariff.get(0).get("ACCUMULATEUSAGE").toString());
                    lastAccumulateDate = billingBlockTariff.get(0).get("YYYYMMDD").toString();
                }
                log.info("last save preAccumulateBill : "+ preAccumulateBill);
                
                //마지막 누적요금이 계산된 달부터 오늘까지의 MonthEM
                //ECG에 4개의 채널이 적용되어 있음.
                List<Integer> channelList = new ArrayList<Integer>();
                channelList.add(KamstrupChannel.ActiveEnergyImp.getChannel());
                channelList.add(KamstrupChannel.ActiveEnergyExp.getChannel());
                channelList.add(KamstrupChannel.ReactiveEnergyImp.getChannel());
                channelList.add(KamstrupChannel.ReactiveEnergyExp.getChannel());
                
                Map<String, Object> condition = new HashMap<String, Object>();
                condition.put("mdevType", DeviceType.Meter);
                condition.put("mdevId", mdsId);
                condition.put("channelList", channelList);
                condition.put("yyyymm", lastAccumulateDate.substring(0,6));
                condition.put("supplierId", contract.getSupplierId());
                List<MonthEM> monthEM = monthEMDao.getMonthEMsByCondition(condition);

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
                    log.info("-----accumulateBill");
                    accumulateBill = blockBill(tariffEMList, monthTotalBig);
                    log.info("-----accumulateBill : " + accumulateBill);
                    
                    //BillingBlockTariff의 bill 계산 로직
                    //전날까지의 누적 사용량 (monthTotal - 오늘 사용량)
                    log.info("-----preDayAccumulateUsage");
                    BigDecimal preDayAccumulateUsage =  monthTotalBig.subtract(new BigDecimal(monthEMValue[Integer.parseInt(billDay.substring(6,8))-1] == null ? 0 : monthEMValue[Integer.parseInt(billDay.substring(6,8))-1]));
                    log.info("-----preDayAccumulateUsage : " + preDayAccumulateUsage);
                    
                    //전날 누적액
                    BigDecimal preDayAccumulateBill =  new BigDecimal("0");
                    log.info("-----preDayAccumulateBill");
                    preDayAccumulateBill = blockBill(tariffEMList, preDayAccumulateUsage);
                    log.info("-----preDayAccumulateBill : " + preDayAccumulateBill); 
                    
                    
                    log.info("----------------- BillingBlockTariff save Start ----------------------");

                    //마지막으로 billingBlockTariff을 저장한 날의 정보를 갱신하기 위함
                    if(lastAccumulateDate.substring(0,6).equals(monthEM.get(i).getYyyymm())) {
                        Set<Condition> condition3 = new HashSet<Condition>();
                        condition3.add(new Condition("id.mdevType", new Object[]{DeviceType.Meter}, null, Restriction.EQ));
                        condition3.add(new Condition("id.mdevId", new Object[]{mdsId}, null, Restriction.EQ));
                        condition3.add(new Condition("id.yyyymmdd", new Object[]{lastAccumulateDate.substring(0,8)}, null, Restriction.EQ));
                        
                        lastBillingBlockTariff = billingBlockTariffDao.findByConditions(condition3);
                        _lastBillingBlockTariff = lastBillingBlockTariff.size() <= 0 ? null : lastBillingBlockTariff.get(0);
                        if(_lastBillingBlockTariff != null) {
                            //마지막으로 billingDayEm이 저장된 날의 누적사용량
                            Double lastBillingDayAccumulateUsage = 0d;
                            //마지막으로 billingDayEm이 저장된 날 전날의 누적사용량
                            Double lastBillingPreDayAccumulateUsage = 0d;
                            
                            for(int j=0; j < Integer.parseInt(lastAccumulateDate.substring(6,8)); j++) {
                                lastBillingDayAccumulateUsage += monthEMValue[j];
                            }
                            lastBillingPreDayAccumulateUsage = lastBillingDayAccumulateUsage - monthEMValue[Integer.parseInt(lastAccumulateDate.substring(6,8))-1];
                            
                            log.debug("lastBillingDayAccumulateUsage : " + lastBillingDayAccumulateUsage);
                            log.debug("lastBillingPreDayAccumulateUsage : " + lastBillingPreDayAccumulateUsage);
                            
                            //마지막으로 저장된 날의 bill
                            BigDecimal _lastAccumulateBill = blockBill(tariffEMList, new BigDecimal(lastBillingDayAccumulateUsage));
                            BigDecimal _lastPreAccumulatebill = blockBill(tariffEMList, new BigDecimal(lastBillingPreDayAccumulateUsage));
                            log.debug("_lastAccumulateBill : " + _lastAccumulateBill);
                            log.debug("_lastPreAccumulatebill : " + _lastPreAccumulatebill);
                            
                            log.debug("before _lastBillingBlockTariff.getBill : " + _lastBillingBlockTariff.getBill());
                            //새로 계산된  bill이 기존의 bill보다 값이 크거나 같을 때만 새로 갱신시킨다.
                            if(_lastBillingBlockTariff.getBill() <= _lastAccumulateBill.subtract(_lastPreAccumulatebill).doubleValue()) {
                                _lastBillingBlockTariff.setBill((_lastAccumulateBill.subtract(_lastPreAccumulatebill)).doubleValue());
                            } 
                            _lastBillingBlockTariff.setAccumulateBill(_lastAccumulateBill.doubleValue());
                            _lastBillingBlockTariff.setAccumulateUsage(lastBillingDayAccumulateUsage.doubleValue());
                            
                            log.debug("after Update_lastBillingBlockTariff.getBill : " + _lastBillingBlockTariff.getBill());
                            
                            billingBlockTariffDao.saveOrUpdate(_lastBillingBlockTariff);
                            
                        }
                    }
                    /////////////////////////////////////
                    
                    Set<Condition> condition2 = new HashSet<Condition>();
                    condition2.add(new Condition("id.mdevType", new Object[]{DeviceType.Meter}, null, Restriction.EQ));
                    condition2.add(new Condition("id.mdevId", new Object[]{mdsId}, null, Restriction.EQ));
                    condition2.add(new Condition("id.yyyymmdd", new Object[]{billDay.substring(0,8)}, null, Restriction.EQ));
                    
                    billingBlockTariffList = billingBlockTariffDao.findByConditions(condition2);
                    _billingBlockTariff = billingBlockTariffList.size() <= 0 ? new BillingBlockTariff() : billingBlockTariffList.get(0);
                    
                    //계산중인 날의 누적요금
                    BigDecimal beforeAccumulateBill = _billingBlockTariff.getAccumulateBill() == null ? new BigDecimal("0") : new BigDecimal(_billingBlockTariff.getAccumulateBill());
                    //과금일부터 누적금액을 reset 해준다.
                    if (_billingBlockTariff.getMDevId() != null) {
                        
                        log.info("-----before reset-----");
                        log.info("setBill : " + _billingBlockTariff.getBill());
                        log.info("setAccumulateBill : " + beforeAccumulateBill);
                        log.info("setAccumulateUsage : " + _billingBlockTariff.getAccumulateUsage());
                        
                        String billDate = supplyType.getBillDate();
                        String[] billDates = StringUtils.delimitedListToStringArray(billDate, ",");
                        
                        if(ArrayUtils.contains(billDates, billDay.substring(6,8))) {
                            //과금일(1일)의 경우 
                            _billingBlockTariff.setBill(accumulateBill.doubleValue());
                            _billingBlockTariff.setAccumulateBill(accumulateBill.doubleValue());
                            _billingBlockTariff.setAccumulateUsage(monthTotalBig.doubleValue());
                        } else {
                            //현재누적액 - 어제 누적액
                            _billingBlockTariff.setBill(accumulateBill.doubleValue() - preDayAccumulateBill.doubleValue());
                            _billingBlockTariff.setAccumulateBill(accumulateBill.doubleValue());
                            _billingBlockTariff.setAccumulateUsage(monthTotalBig.doubleValue());
                        }
                       
                    } else {
                        Meter meter = meterDao.get(contract.getMeterId());
                        
                        _billingBlockTariff.setMDevId(mdsId);
                        _billingBlockTariff.setYyyymmdd(billDay.substring(0,8));
                        _billingBlockTariff.setHhmmss("000000");
                        _billingBlockTariff.setMDevType(DeviceType.Meter.name());
                        _billingBlockTariff.setContract(contract);
                        _billingBlockTariff.setSupplier(contract.getSupplier());
                        _billingBlockTariff.setLocation(contract.getLocation());
                        _billingBlockTariff.setMeter(meter);
                        _billingBlockTariff.setModem((meter == null) ? null : meter.getModem());
                        _billingBlockTariff.setWriteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
                        _billingBlockTariff.setAccumulateUsage(monthTotalBig.doubleValue());
                        
                        String billDate = supplyType.getBillDate();
                        String[] billDates = StringUtils.delimitedListToStringArray(billDate, ",");
                        
                        if(ArrayUtils.contains(billDates, billDay.substring(6,8))) {
                            //과금일(1일)의 경우 
                            _billingBlockTariff.setBill(accumulateBill.doubleValue());
                            _billingBlockTariff.setAccumulateBill(accumulateBill.doubleValue());
                        } else {
                            //현재누적액 - 어제누적액
                            _billingBlockTariff.setBill(accumulateBill.doubleValue() - preDayAccumulateBill.doubleValue());
                            _billingBlockTariff.setAccumulateBill(accumulateBill.doubleValue());
                          
                        }
                        
                    }
                    
                    billingBlockTariffDao.saveOrUpdate(_billingBlockTariff);
                    log.info("-----after reset-----");
                    log.info("setBill : " + _billingBlockTariff.getBill());
                    log.info("setAccumulateBill : " + _billingBlockTariff.getAccumulateBill());
                    log.info("setAccumulateUsage : " + _billingBlockTariff.getAccumulateUsage());
                    
                    log.info("CurrentCredit before subtraction: " + contract.getCurrentCredit());
                    log.info("befor prepayLogCredit: " + prepayLogCredit);
                    BigDecimal bdCurCredit = (contract.getCurrentCredit() != null) ? new BigDecimal(contract.getCurrentCredit()) : new BigDecimal(0d);
                    
                    //if((billDay.substring(0,8)).equals(billDay.substring(0,6)+CalendarUtil.getMonthLastDate(billDay.substring(0,4), billDay.substring(4, 6)))) {
                        //달의 마지막날일때
/*                      if((i+1 == monthEM.size())) {
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
                        }*/
                    //} else {
                        //마지막 날이 아닐경우 (세금을 빼지 않는다.)
                        contract.setCurrentCredit(bdCurCredit.doubleValue() - (accumulateBill.doubleValue() - preAccumulateBill.doubleValue()));
                        prepayLogCredit = new BigDecimal(prepayLogCredit.doubleValue() + (accumulateBill.doubleValue() - preAccumulateBill.doubleValue()));
                    //}
                    log.info("after prepayLogCredit : " + prepayLogCredit); 
                    log.info("CurrentCredit after subtraction : " + contract.getCurrentCredit());    
                    
                    if(monthEM.size() > 1) {
                        //두달이상 차감지연의 경우
                        if(!(preAccumulateBill.doubleValue() == accumulateBill.doubleValue())) { 
                            prepayLogUsage = prepayLogUsage + (monthTotalBig.doubleValue() - preAccumulateUsage);
                        }
                    } else {

                        prepayLogUsage = monthTotalBig.doubleValue() - preAccumulateUsage;
                    }
                    
                    log.info("["+ i +"] prepayLogCredit : " + prepayLogCredit);
                    log.info("["+ i +"] prepayLogCreditMAth : " + prepayLogCredit.doubleValue());
                    log.info("["+ i +"] prepayLogUsage : " + prepayLogUsage);
                    
                    

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
                 log.error(e,e);
                 transactionManager.rollback(txStatus);
            }
            
            return _billingBlockTariff == null ? 0  : _billingBlockTariff.getBill();
            
        }
    
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
            
            log.info("[" + cnt + "] supplyMin : " + supplyMin + ", supplyMax : " + supplyMax);
            
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
//                       처음 한번만..
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
                    
                    log.info("diffBig : " + diffBig);
                    
                    //사용량 * 단가
                    
                    // 첫번째 구간 계산시 누적사용량이 51 이상이 되면 첫번째 구간 사용량도 두번째 구간 단가를 적용한다. Residential에만 해당한다.
                    if (cnt == 0 && usage.compareTo(supplyMax) > 0 && tariffEMList.get(cnt).getTariffType().getName().equals("Residential"))
                        returnBill = returnBill.add(diffBig.multiply(new BigDecimal(tariffEMList.get(cnt+1).getActiveEnergyCharge() == null ? 0d : tariffEMList.get(cnt+1).getActiveEnergyCharge())));
                    else
                        returnBill = returnBill.add(diffBig.multiply(new BigDecimal(tariffEMList.get(cnt).getActiveEnergyCharge() == null ? 0d : tariffEMList.get(cnt).getActiveEnergyCharge())));
                    
                    log.info("ActiveEnergyCharge: " + tariffEMList.get(cnt).getActiveEnergyCharge());
            }
        }
        
        
        return returnBill;
    }
    

      /**
         * method name : getMonthValue31
         * method Desc : EM
         *
         * @param meteringMonth
         * @return
         */
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
}