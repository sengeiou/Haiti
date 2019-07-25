package com.aimir.schedule.task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.mvm.BillingBlockTariffDao;
import com.aimir.dao.mvm.DayEMDao;
import com.aimir.dao.mvm.LpEMDao;
import com.aimir.dao.mvm.MonthEMDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.dao.system.PrepaymentLogDao;
import com.aimir.dao.system.SupplyTypeDao;
import com.aimir.dao.system.TariffEMDao;
import com.aimir.dao.system.TariffTypeDao;
import com.aimir.dao.view.MonthEMViewDao;
import com.aimir.fep.util.DataUtil;
import com.aimir.model.device.Meter;
import com.aimir.model.mvm.BillingBlockTariff;
import com.aimir.model.mvm.LpEM;
import com.aimir.model.mvm.MonthEM;
import com.aimir.model.system.Code;
import com.aimir.model.system.Contract;
import com.aimir.model.system.PrepaymentLog;
import com.aimir.model.system.TariffEM;
import com.aimir.model.system.TariffType;
import com.aimir.model.view.MonthEMView;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.TimeUtil;

/**
 * ECG의 Block Tariff 선불 요금을 계산하는 스케줄
 * Block이 월단위이므로 월사용량을 기준으로 요금을 계산해야 한다.
 * 
 * @author elevas
 *
 */
public class ECGBlockDailyEMBillingInfoSaveV3Task extends ScheduleTask {
    
    protected static Log log = LogFactory.getLog(ECGBlockDailyEMBillingInfoSaveV3Task.class);
    private static final String SERVICE_TYPE_EM = "Electricity";

    @Resource(name="transactionManager")
    HibernateTransactionManager txManager;
    
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
    
    @Autowired
    LpEMDao lpEMDao;
    
    @Autowired
    MonthEMViewDao monthEMViewDao;  
    
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
    
    public static void main(String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{"spring-forcrontab.xml"}); 
        DataUtil.setApplicationContext(ctx);
        
        ECGBlockDailyEMBillingInfoSaveV3Task task = ctx.getBean(ECGBlockDailyEMBillingInfoSaveV3Task.class);
        task.execute(null);
        System.exit(0);
    }

    
    public void execute(JobExecutionContext context) {
		if(isNowRunning){
			log.info("########### ECGBlockDailyEMBillingInfoSaveV2Task is already running...");
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

        TransactionStatus txStatus = null;
        
        for(Integer contract_id : em_contractIds) {
            // log.info("ContractId[" + contract_id + "]");
            
            txStatus = txManager.getTransaction(null);
            try {
                Contract contract = contractDao.get(contract_id);
                Code code = contract.getCreditType();
                if (contract.getTariffIndexId() != null && contract.getCustomer() != null && contract.getMeter() != null) {
                    if(Code.PREPAYMENT.equals(code.getCode()) || Code.EMERGENCY_CREDIT.equals(code.getCode())) { // 선불 요금일 경우
                        log.info("Contract[" + contract.getContractNumber() + "] Meter[" + contract.getMeter().getMdsId() + "]");
                        this.saveEmBillingDailyWithTariffEMCumulationCost(contract);
                    }
                }
                txManager.commit(txStatus);
            }
            catch (Exception e) {
                log.error(e, e);
                txManager.rollback(txStatus);
            }
        }
        // setSuccessResult();
    }

    private List<Integer> getContractInfos(String serviceType){
        return contractDao.getPrepaymentContract(serviceType);
    }
    
    private void saveEmBillingDailyWithTariffEMCumulationCost(Contract contract)
    throws Exception
    {
        SimpleDateFormat sd = new SimpleDateFormat("yyyyMMddHHmmss");
        
        String mdsId = meterDao.get(contract.getMeterId()).getMdsId();
        String currentDate = DateTimeUtil.getDateString(new Date());
        
        //마지막 누적요금이 저장된 데이터를 가지고 온다.
        List<Map<String, Object>> lastBilling = billingBlockTariffDao.getLastAccumulateBill(mdsId);
        //마지막 누적요금 저장
        double lastAccumulateBill = 0.0;
        //마지막 누적사용량 저장
        double lastAccumulateUsage = 0.0;
        //마지막 누적요금이 저장된 날짜
        String lastAccumulateDate = null;
        //마지막 지침값
        double lastMeteringValue = 0.0;
        
        if(lastBilling.size() <= 0) {
            //마지막 누적 요금이 저장된 billingDayEM이 없을 경우 한번도 선불스케줄을 돌리지 않은것으로 간주
            lastAccumulateBill = 0.0;
            lastAccumulateUsage = 0.0;
            lastMeteringValue = 0.0;
            // 두달 전 선불부터 시작한다.
            lastAccumulateDate = TimeUtil.getPreMonth(currentDate, 2);
        } else {
            lastAccumulateBill = Double.parseDouble(lastBilling.get(0).get("ACCUMULATEBILL").toString());
            lastAccumulateUsage = Double.parseDouble(lastBilling.get(0).get("ACCUMULATEUSAGE").toString());
            lastMeteringValue = Double.parseDouble(lastBilling.get(0).get("LASTMETERINGVALUE").toString());
            lastAccumulateDate = lastBilling.get(0).get("YYYYMMDD").toString() + lastBilling.get(0).get("HHMMSS").toString();
        }
        log.info("\n lastAccumulateBill: " + lastAccumulateBill);
        log.info("\n lastAccumulateUsage: " + lastAccumulateUsage);
        log.info("\n lastAccumulateDate: " + lastAccumulateDate);
        
        // 마지막 시간이 현재 시간보다 작으면 월 기준으로 선불 계산한다.
        if (lastAccumulateDate.compareTo(currentDate) < 0) {
            String yyyymm = lastAccumulateDate.substring(0 ,6);
            String endYyyymm = currentDate.substring(0, 6);
            Calendar cal = Calendar.getInstance();
            sd = new SimpleDateFormat("yyyyMM");
            cal.setTime(sd.parse(yyyymm));
            
            //해당 Tariff 정보를 가져온다.
            TariffType tariffType = tariffTypeDao.get(contract.getTariffIndexId()); 
            Integer tariffTypeCode = tariffType.getCode();
            Map<String, Object> tariffParam = new HashMap<String, Object>();

            tariffParam.put("tariffTypeCode", tariffTypeCode);
            tariffParam.put("tariffIndex", tariffType);
            List<TariffEM> tariffEMList = null; 
            
            while ((yyyymm=sd.format(cal.getTime()).substring(0, 6)).compareTo(endYyyymm) <= 0) {
                tariffParam.put("searchDate", yyyymm+"31");
                tariffEMList = tariffEMDao.getApplyedTariff(tariffParam); 
                
                savePrebill(contract.getTariffIndex().getName(), contract.getMeter(), yyyymm, lastAccumulateBill,
                        lastAccumulateUsage, lastMeteringValue, tariffEMList);
                cal.add(Calendar.MONTH, 1);
                lastAccumulateBill = 0;
                lastAccumulateUsage = 0;
            }
        }
    }
    
    // yyyymm의 월 사용량을 가져와 선불을 계산하고 마지막 선불요금을 뺀다.
    private void savePrebill(String tariffName, Meter meter, String yyyymm, double lastAccumulateBill,
            double lastAccumulateUsage, double lastMeteringValue, List<TariffEM> tariffEMList)
    throws Exception
    {
    	log.info("####savePrebill####");
    	log.info("MeterId[" + meter.getMdsId() + "] YYYYMM[" + yyyymm + "]");
    	
        //마지막 누적요금이 계산된 달부터 오늘까지의 MonthEM
        //ECG에 4개의 채널이 적용되어 있음.
        List<Integer> channelList = new ArrayList<Integer>();
        channelList.add(KamstrupChannel.ActiveEnergyImp.getChannel());
        channelList.add(KamstrupChannel.ActiveEnergyExp.getChannel());
        // channelList.add(KamstrupChannel.ReactiveEnergyImp.getChannel());
        // channelList.add(KamstrupChannel.ReactiveEnergyExp.getChannel());
        
        // 모뎀이 ZRU 유형은 기존 방식대로 월 사용량을 기준으로 선불 계산한다.
        if (meter.getModem() != null && meter.getModem().getModemType() == ModemType.ZRU) {
            Map<String, Object> condition = new HashMap<String, Object>();
            condition.put("mdevType", DeviceType.Meter);
            condition.put("mdevId", meter.getMdsId());
            condition.put("channelList", channelList);
            condition.put("yyyymm", yyyymm);
            condition.put("dst", 0);
            
            /*
             * OPF-610 정규화 관련 처리로 인한 주석
            List<MonthEM> monthEMs = monthEMDao.getMonthEMsByCondition(condition);
            */
            List<MonthEMView> monthEMs = monthEMViewDao.getMonthEMsByCondition(condition);
            
            log.info("monthEM size: " + monthEMs.size());
            if (monthEMs.size() > 0) {
				/*
				 * OPF-610 정규화 관련 처리로 인한 주석
				 * MonthEM monthEM = monthEMs.get(0);
				 */
            	MonthEMView monthEM = monthEMs.get(0);
                monthEM.setMDevType(DeviceType.Meter.name());
                monthEM.setMDevId(meter.getMdsId());
                
                // 월 선불금액 계산
                double monthBill = blockBill(tariffName, tariffEMList, monthEM.getTotal());
                
                // 월의 마지막 lp 데이타의 시간을 가져온다.
                String lastLpTime = getLastLpTime(monthEM);
                // LP 날짜로 Billing Day를 생성하거나 업데이트한다.
                saveBillingBlockTariff(meter, monthEM.getTotal(), lastLpTime, monthBill, lastAccumulateBill);
                
                // 잔액을 차감한다.
                Contract contract = meter.getContract();
                log.info("contract_number[" + contract.getContractNumber() + 
                        "] MonthBill[" + monthBill + "] lastAccumulateBill[" + lastAccumulateBill+ "]");
                contract.setCurrentCredit(contract.getCurrentCredit() - (monthBill - lastAccumulateBill));
                contractDao.updateCurrentCredit(contract.getId(), contract.getCurrentCredit());
                
                // 선불로그 기록
                savePrepyamentLog(contract, monthEM.getTotal()-lastAccumulateUsage,
                        monthBill-lastAccumulateBill, lastLpTime);
            }
        }
        else {
            // 월 선불금액 계산
            double usage = meter.getLastMeteringValue() - lastMeteringValue + lastAccumulateUsage;
            double monthBill = blockBill(tariffName, tariffEMList, usage);
            
            // 월의 마지막 lp 데이타의 시간을 가져온다.
            String lastLpTime = meter.getLastReadDate();
            // LP 날짜로 Billing Day를 생성하거나 업데이트한다.
            saveBillingBlockTariff(meter, usage, lastLpTime, monthBill, lastAccumulateBill);
            
            // 잔액을 차감한다.
            Contract contract = meter.getContract();
            log.info("contract_number[" + contract.getContractNumber() + 
                    "] MonthBill[" + monthBill + "] lastAccumulateBill[" + lastAccumulateBill+ "]");
            contract.setCurrentCredit(contract.getCurrentCredit() - (monthBill - lastAccumulateBill));
            contractDao.updateCurrentCredit(contract.getId(), contract.getCurrentCredit());
            
            // 선불로그 기록
            savePrepyamentLog(contract, usage-lastAccumulateUsage,
                    monthBill-lastAccumulateBill, lastLpTime);
        }
    }
    
    private void saveBillingBlockTariff(Meter meter, double usage, String lastLpTime, double newbill, double oldbill) {
        BillingBlockTariff bill = new BillingBlockTariff();
        
        bill.setMDevId(meter.getMdsId());
        bill.setYyyymmdd(lastLpTime.substring(0, 8));
        bill.setHhmmss(lastLpTime.substring(8, 14));
        bill.setMDevType(DeviceType.Meter.name());
        bill.setSupplier(meter.getSupplier());
        bill.setLocation(meter.getLocation());
        bill.setMeter(meter);
        bill.setModem((meter == null) ? null : meter.getModem());
        bill.setWriteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
        bill.setAccumulateUsage(usage);
        if (meter.getContract() != null)
            bill.setContract(meter.getContract());
        
        //현재누적액 - 어제누적액
        bill.setBill(newbill - oldbill);
        bill.setAccumulateBill(newbill);
    
        log.info("MeterId[" + bill.getMDevId() + "] BillDay[" + bill.getYyyymmdd() + 
                "] BillTime[" + bill.getHhmmss() + "] AccumulateUsage[" + bill.getAccumulateUsage() +
                "] AccumulateBill[" + bill.getAccumulateBill() + "] CurrentBill[" + bill.getBill() + "]");
        
        billingBlockTariffDao.saveOrUpdate(bill);
    }
    
    private void savePrepyamentLog(Contract contract, double usage, double bill, String lastLpTime) {
        PrepaymentLog prepaymentLog = new PrepaymentLog();
        prepaymentLog.setUsedConsumption(usage);
        prepaymentLog.setBalance(contract.getCurrentCredit());
        prepaymentLog.setChargedCredit(Double.parseDouble("0"));
        prepaymentLog.setLastTokenDate(DateTimeUtil.getDateString(new Date()));
        prepaymentLog.setContract(contract);
        prepaymentLog.setCustomer(contract.getCustomer());
        prepaymentLog.setUsedCost(bill);
        prepaymentLog.setLastLpTime(lastLpTime.substring(0, 6));
        prepaymentLog.setLocation(contract.getLocation());
        prepaymentLog.setTariffIndex(contract.getTariffIndex());
        
        log.info(prepaymentLog.toString());
        
        prepaymentLogDao.saveOrUpdate(prepaymentLog);
    }
    
    /*
     * BillingDayEM의 시간에 넣기 위한 LP 마지막 시간을 가져온다.
     */
    private String getLastLpTime(MonthEM monthEM) throws Exception {
        String yyyymmdd = null;
        String lpValue = null;
        for (int i = 31; i > 0; i--) {
            lpValue = BeanUtils.getProperty(monthEM, String.format("value_%02d", i));
            if (lpValue != null && !lpValue.equals("")) {
                yyyymmdd = String.format("%s%02d", monthEM.getYyyymm(), i);
                log.info("getLastLpTime: " + yyyymmdd);
                break;
            }
        }
        if (yyyymmdd != null) {
            Set<Condition> condition = new HashSet<Condition>();
            log.info("MDevType[" + monthEM.getMDevType() + "] MDevId[" + monthEM.getMDevId() + "] YYYYMMDD[" + yyyymmdd + "]");
            condition.add(new Condition("id.mdevType", new Object[]{monthEM.getMDevType()}, null, Restriction.EQ));
            condition.add(new Condition("id.mdevId", new Object[]{monthEM.getMDevId()}, null, Restriction.EQ));
            condition.add(new Condition("id.channel", new Object[]{1}, null, Restriction.EQ));
            condition.add(new Condition("id.yyyymmddhhmiss", new Object[]{yyyymmdd+"000000", yyyymmdd+"235959"}, null, Restriction.BETWEEN));
            condition.add(new Condition("id.dst", new Object[]{0}, null, Restriction.EQ));
            
            List<LpEM> lpEMs = lpEMDao.findByConditions(condition);
            LpEM lastLp = null;
            if (lpEMs.size() > 0) {
                lastLp = lpEMs.get(0);
                for (int i = 0; i < lpEMs.size(); i++) {
                    if (lastLp.getYyyymmddhh().compareTo(lpEMs.get(i).getYyyymmddhh()) < 0) {
                        lastLp = lpEMs.get(i);
                    }
                }
            }
            /*
             * OPF-610 정규화 관련 처리로 인한 주석
            if (lastLp.getValue_30() != null)
                return lastLp.getYyyymmddhh() + "3000";
            else if (lastLp.getValue_00() != null)
                return lastLp.getYyyymmddhh() + "0000";
            */
        }
        
        return null;
    }
    
    private String getLastLpTime(MonthEMView monthEM) throws Exception {
        String yyyymmdd = null;
        String lpValue = null;
        for (int i = 31; i > 0; i--) {
            lpValue = BeanUtils.getProperty(monthEM, String.format("value_%02d", i));
            if (lpValue != null && !lpValue.equals("")) {
                yyyymmdd = String.format("%s%02d", monthEM.id.getYyyymm(), i);
                log.info("getLastLpTime: " + yyyymmdd);
                break;
            }
        }
        if (yyyymmdd != null) {
            Set<Condition> condition = new HashSet<Condition>();
            log.info("MDevType[" + monthEM.getMDevType() + "] MDevId[" + monthEM.getMDevId() + "] YYYYMMDD[" + yyyymmdd + "]");
            condition.add(new Condition("id.mdevType", new Object[]{monthEM.getMDevType()}, null, Restriction.EQ));
            condition.add(new Condition("id.mdevId", new Object[]{monthEM.getMDevId()}, null, Restriction.EQ));
            condition.add(new Condition("id.channel", new Object[]{1}, null, Restriction.EQ));
            condition.add(new Condition("id.yyyymmddhhmiss", new Object[]{yyyymmdd+"000000", yyyymmdd+"235959"}, null, Restriction.BETWEEN));
            condition.add(new Condition("id.dst", new Object[]{0}, null, Restriction.EQ));
            
            List<LpEM> lpEMs = lpEMDao.findByConditions(condition);
            LpEM lastLp = null;
            if (lpEMs.size() > 0) {
                lastLp = lpEMs.get(0);
                for (int i = 0; i < lpEMs.size(); i++) {
                    if (lastLp.getYyyymmddhh().compareTo(lpEMs.get(i).getYyyymmddhh()) < 0) {
                        lastLp = lpEMs.get(i);
                    }
                }
            }
            /*
             * OPF-610 정규화 관련 처리로 인한 주석
            if (lastLp.getValue_30() != null)
                return lastLp.getYyyymmddhh() + "3000";
            else if (lastLp.getValue_00() != null)
                return lastLp.getYyyymmddhh() + "0000";
            */
            
            if(lastLp.getValue() != null) {
            	if("30".equals(lastLp.getMinute()))
            		return lastLp.getYyyymmddhh() + "3000";
            	else if("00".equals(lastLp.getMinute()))
            		return lastLp.getYyyymmddhh() + "0000";
            }
        }
        
        return null;
    }
    
    public double blockBill(String tariffName, List<TariffEM> tariffEMList, double usage) {
        ECGBlockDailyEMBillingInfoSaveV2Task task = new ECGBlockDailyEMBillingInfoSaveV2Task();
        return task.blockBill(tariffName, tariffEMList, usage);
    }
}