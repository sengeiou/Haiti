package com.aimir.schedule.task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
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
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.mvm.BillingBlockTariffDao;
import com.aimir.dao.mvm.DayEMDao;
import com.aimir.dao.mvm.LpEMDao;
import com.aimir.dao.mvm.MonthEMDao;
import com.aimir.dao.mvm.impl.LpEMDaoImpl;
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
import com.aimir.model.system.Customer;
import com.aimir.model.system.PrepaymentLog;
import com.aimir.model.system.TariffEM;
import com.aimir.model.system.TariffType;
import com.aimir.model.view.MonthEMView;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeUtil;

/**
 * ECG의 Block Tariff 선불 요금을 계산하는 스케줄
 * Block이 월단위이므로 월사용량을 기준으로 요금을 계산해야 한다.
 * 
 * @author elevas
 *
 */
public class ECGBlockDailyEMBillingInfoSaveV2Task extends ScheduleTask {
    
    protected static Log log = LogFactory.getLog(ECGBlockDailyEMBillingInfoSaveV2Task.class);
    private static final String SERVICE_TYPE_EM = "Electricity";

    @Resource(name="transactionManager")
    HibernateTransactionManager txmanager;
    
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
        
        ECGBlockDailyEMBillingInfoSaveV2Task task = ctx.getBean(ECGBlockDailyEMBillingInfoSaveV2Task.class);
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

        for(Integer contract_id : em_contractIds) {
            // log.info("ContractId[" + contract_id + "]");
            try {
                    if(!exceptContract(contract_id)) {
                        this.saveEmBillingDailyWithTariffEMCumulationCost(contract_id);
                    } else {
                        log.info("Billing Except contract_id : " + contract_id);
                    }
            }
            catch (Exception e) {
                log.error(e, e);
            }
        }
        // setSuccessResult();
    }
    
    private Boolean exceptContract(Integer contract_id) {
        Boolean isExcept = false; 
        Integer[] exceptContractId = {12146,13278,13279,13280,16770,16771,16772,16773,16774,17023,17038,17046,17048,17049,17155,17225,17230,17344,17388,18004,18366,18418,18710};
        for (int i = 0; i < exceptContractId.length; i++) {
            if(exceptContractId[i].equals(contract_id)) {
                isExcept = true;
                break;
            }
        }
        return isExcept;
    }

    private List<Integer> getContractInfos(String serviceType){
        return contractDao.getPrepaymentContract(serviceType);
    }
    
    private void saveEmBillingDailyWithTariffEMCumulationCost(Integer contract_id)
    throws Exception
    {
        TransactionStatus txstatus = null;
        
        try {
            txstatus = txmanager.getTransaction(null);
        
            Contract contract = contractDao.get(contract_id);
            String locName = contract.getLocation().getName();
            if (locName.equals("UT PROPERTIES ESTATE") || locName.equals("KUMSARK ESTATE")
                    || locName.equals("OWUSU ANSAH") || locName.equals("ABELENKPE")
                    || locName.equals("CSIR") || locName.equals("OTENSHIE")
                    || locName.equals("Prampram District") || locName.equals("Dawhenya")
                    || locName.equals("Dev Court") || locName.equals("P S Global")
                    || locName.equals("Central University Hostel") || locName.equals("Alter Development")
                    || locName.equals("Magna Terris") || locName.equals("New Dawhwenya") || locName.equals("Old Ningo")
                    || contract.getId() == 33098 || contract.getId() == 31097 || contract.getId() == 24633
                    || contract.getId() == 10979 || contract.getId() == 37500 || contract.getId() == 31946
                    || contract.getId() == 41006 || contract.getId() == 44791 || contract.getId() == 13278
                    || contract.getId() == 13280 || contract.getId() == 16770 || contract.getId() == 16771
                    || contract.getId() == 16772 || contract.getId() == 16773 || contract.getId() == 16774
                    || contract.getId() == 17023 || contract.getId() == 17038 || contract.getId() == 17046
                    || contract.getId() == 17048 || contract.getId() == 17049 || contract.getId() == 17155
                    || contract.getId() == 17225 || contract.getId() == 17230 || contract.getId() == 17344
                    || contract.getId() == 17388 || contract.getId() == 18004 || contract.getId() == 18366
                    || contract.getId() == 18418 || contract.getId() == 18710)
                return;
            
            Code code = contract.getCreditType();
            if (contract.getTariffIndexId() != null && contract.getCustomer() != null && contract.getMeter() != null) {
                if(Code.PREPAYMENT.equals(code.getCode()) || Code.EMERGENCY_CREDIT.equals(code.getCode())) { // 선불 요금일 경우
                    log.info("Contract[" + contract.getContractNumber() + "] Meter[" + contract.getMeter().getMdsId() + "]");
                }
                else return;
            }
            
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
            
            if(lastBilling.size() <= 0) {
                //마지막 누적 요금이 저장된 billingDayEM이 없을 경우 한번도 선불스케줄을 돌리지 않은것으로 간주
                lastAccumulateBill = 0.0;
                lastAccumulateUsage = 0.0;
                // 두달 전 선불부터 시작한다.
                lastAccumulateDate = TimeUtil.getPreMonth(currentDate, 2);
            } else {
                lastAccumulateBill = Double.parseDouble(lastBilling.get(0).get("ACCUMULATEBILL").toString());
                lastAccumulateUsage = Double.parseDouble(lastBilling.get(0).get("ACCUMULATEUSAGE").toString());
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
                
                int lastDay = 1;
                while ((yyyymm=sd.format(cal.getTime()).substring(0, 6)).compareTo(endYyyymm) <= 0) {
                    tariffParam.put("searchDate", yyyymm+"31");
                    tariffEMList = tariffEMDao.getApplyedTariff(tariffParam); 
                    
                    if (yyyymm.equals(endYyyymm)) {
                        savePrebill(contract.getTariffIndex().getName(), contract.getMeter(), yyyymm, lastAccumulateBill,
                                lastAccumulateUsage, tariffEMList, DateTimeUtil.getDateString(new Date()));
                    }
                    else {
                        lastDay = getLastDay(yyyymm);
                        savePrebill(contract.getTariffIndex().getName(), contract.getMeter(), yyyymm, lastAccumulateBill,
                                lastAccumulateUsage, tariffEMList, yyyymm+lastDay+"235959");
                    }
                    cal.add(Calendar.MONTH, 1);
                    lastAccumulateBill = 0;
                    lastAccumulateUsage = 0;
                }
            }
        }
        finally {
            if (txstatus != null) txmanager.commit(txstatus);
        }
    }
    
    private int getLastDay(String yyyymm) {
        int yyyy = Integer.parseInt(yyyymm.substring(0, 4));
        int mm = Integer.parseInt(yyyymm.substring(4, 6));

        int day = 1;
        switch (mm) {
        case 1:
        case 3:
        case 5:
        case 7:
        case 8:
        case 10:
        case 12:
            day = 31;
            break;
        case 2:
            if (yyyy % 4 == 0) day = 29;
            else day = 28;
            break;
        default : day = 30;
        }
        
        return day;
    }
    
    // yyyymm의 월 사용량을 가져와 선불을 계산하고 마지막 선불요금을 뺀다.
    @Transactional
    private void savePrebill(String tariffName, Meter meter, String yyyymm, double lastAccumulateBill,
            double lastAccumulateUsage, List<TariffEM> tariffEMList, String lastTokenDate)
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
            LpEM[] lps = getLastLpTime(monthEM);
            double lastActiveEnergy = lps[0].getValue() + lps[1].getValue();
            String lastLpTime = lps[0].getYyyymmddhh() + "0000";
            
            // LP 날짜로 Billing Day를 생성하거나 업데이트한다.
            saveBillingBlockTariff(meter, monthEM.getTotal(), lastActiveEnergy, lastLpTime, monthBill, lastAccumulateBill);
            
            // 잔액을 차감한다.
            Contract contract = contractDao.get(meter.getContract().getId());
            log.info("contract_number[" + contract.getContractNumber() + 
                    "] MonthBill[" + monthBill + "] lastAccumulateBill[" + lastAccumulateBill+ 
                    "] LastActiveEnergyImp[" + lps[0].getValue() + " LastActiveEnergyExp[" + lps[1].getValue() + 
                    "] LastLPTime[" + lastLpTime + "]");
            contract.setCurrentCredit(contract.getCurrentCredit() - (monthBill - lastAccumulateBill));
            contractDao.updateCurrentCredit(contract.getId(), contract.getCurrentCredit());
            
            // 선불로그 기록
            savePrepyamentLog(contract, contract.getCustomer(), monthEM.getTotal()-lastAccumulateUsage,
                    monthBill-lastAccumulateBill, lastLpTime, lastTokenDate);
        }
    }
    
    @Transactional
    private void saveBillingBlockTariff(Meter meter, double usage, double baseValue, String lastLpTime, double newbill, double oldbill)
    throws Exception
    {
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
        bill.setActiveEnergy(baseValue);
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
    
    @Transactional
    private void savePrepyamentLog(Contract contract, Customer customer, 
            double usage, double bill, String lastLpTime, String lastTokenDate) {
        PrepaymentLog prepaymentLog = new PrepaymentLog();
        prepaymentLog.setUsedConsumption(usage);
        prepaymentLog.setBalance(contract.getCurrentCredit());
        prepaymentLog.setChargedCredit(Double.parseDouble("0"));
        // prepaymentLog.setLastTokenDate(DateTimeUtil.getDateString(new Date()));
        prepaymentLog.setLastTokenDate(lastTokenDate);
        prepaymentLog.setContract(contract);
        prepaymentLog.setCustomer(customer);
        prepaymentLog.setUsedCost(bill);
        prepaymentLog.setLastLpTime(lastLpTime.substring(0, 6));
        prepaymentLog.setLocation(contract.getLocation());
        prepaymentLog.setTariffIndex(contract.getTariffIndex());
        
        log.info(prepaymentLog.toString());
        
        prepaymentLogDao.add(prepaymentLog);
    }
    
    /*
     * BillingDayEM의 시간에 넣기 위한 LP 마지막 시간을 가져온다.
     */
    @Transactional
    private LpEM[] getLastLpTime(MonthEM monthEM) throws Exception {
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
            condition.add(new Condition("id.channel", new Object[]{KamstrupChannel.ActiveEnergyImp.getChannel()}, null, Restriction.EQ));
            condition.add(new Condition("id.dst", new Object[]{0}, null, Restriction.EQ));
            condition.add(new Condition("id.yyyymmddhhmiss", new Object[]{yyyymmdd + "%"}, null, Restriction.LIKE));
            
            List<Projection> projections = new ArrayList<Projection>();
            projections.add(Projections.alias(Projections.max("id.yyyymmddhhmiss"), "maxYyyymmddhhmiss"));
            
            List<Map<String, Object>> lastLpEM = ((LpEMDaoImpl)lpEMDao).findByConditionsAndProjections(condition, projections);
            
            if (lastLpEM != null && lastLpEM.size() == 1) {
                String maxYyyymmddhhmiss = (String)lastLpEM.get(0).get("maxYyyymmddhhmiss");
                
                condition = new HashSet<Condition>();
                condition.add(new Condition("id.mdevType", new Object[]{monthEM.getMDevType()}, null, Restriction.EQ));
                condition.add(new Condition("id.mdevId", new Object[]{monthEM.getMDevId()}, null, Restriction.EQ));
                condition.add(new Condition("id.channel", new Object[]{KamstrupChannel.ActiveEnergyImp.getChannel(),
                        KamstrupChannel.ActiveEnergyExp.getChannel()}, null, Restriction.IN));
                condition.add(new Condition("id.dst", new Object[]{0}, null, Restriction.EQ));
                condition.add(new Condition("id.yyyymmddhhmiss", new Object[]{maxYyyymmddhhmiss}, null, Restriction.EQ));
                
                return lpEMDao.findByConditions(condition).toArray(new LpEM[0]);
            }
        }
        
        return null;
    }
    
    @Transactional
    private LpEM[] getLastLpTime(MonthEMView monthEM) throws Exception {
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
            condition.add(new Condition("id.channel", new Object[]{KamstrupChannel.ActiveEnergyImp.getChannel()}, null, Restriction.EQ));
            condition.add(new Condition("id.dst", new Object[]{0}, null, Restriction.EQ));
            condition.add(new Condition("id.yyyymmddhhmiss", new Object[]{yyyymmdd + "%"}, null, Restriction.LIKE));
            
            List<Projection> projections = new ArrayList<Projection>();
            projections.add(Projections.alias(Projections.max("id.yyyymmddhhmiss"), "maxYyyymmddhhmiss"));
            
            List<Map<String, Object>> lastLpEM = ((LpEMDaoImpl)lpEMDao).findByConditionsAndProjections(condition, projections);
            
            if (lastLpEM != null && lastLpEM.size() == 1) {
                String maxYyyymmddhhmiss = (String)lastLpEM.get(0).get("maxYyyymmddhhmiss");
                
                condition = new HashSet<Condition>();
                condition.add(new Condition("id.mdevType", new Object[]{monthEM.getMDevType()}, null, Restriction.EQ));
                condition.add(new Condition("id.mdevId", new Object[]{monthEM.getMDevId()}, null, Restriction.EQ));
                condition.add(new Condition("id.channel", new Object[]{KamstrupChannel.ActiveEnergyImp.getChannel(),
                        KamstrupChannel.ActiveEnergyExp.getChannel()}, null, Restriction.IN));
                condition.add(new Condition("id.dst", new Object[]{0}, null, Restriction.EQ));
                condition.add(new Condition("id.yyyymmddhhmiss", new Object[]{maxYyyymmddhhmiss}, null, Restriction.EQ));
                
                return lpEMDao.findByConditions(condition).toArray(new LpEM[0]);
            }
        }
        
        return null;
    }
    
    public double blockBill(String tariffName, List<TariffEM> tariffEMList, double usage) {
        double returnBill = 0.0;
        
        Collections.sort(tariffEMList, new Comparator<TariffEM>() {

            @Override
            public int compare(TariffEM t1, TariffEM t2) {
                return t1.getSupplySizeMin() < t2.getSupplySizeMin()? -1:1;
            }
        });
        
        /*
        if (tariffName.equals("Residential")) {
            // 0 ~ 50
            if (usage <= tariffEMList.get(1).getSupplySizeMin()) {
                returnBill = usage * tariffEMList.get(0).getActiveEnergyCharge();
            }
            // 50 ~
            else {
                // ~ 300
                if (usage <= tariffEMList.get(1).getSupplySizeMax()) {
                    returnBill = usage * tariffEMList.get(1).getActiveEnergyCharge();
                }
                // ~ 600
                else if (usage <= tariffEMList.get(2).getSupplySizeMax()) {
                    returnBill = tariffEMList.get(1).getSupplySizeMax() * tariffEMList.get(1).getActiveEnergyCharge();
                    returnBill += ((usage - tariffEMList.get(2).getSupplySizeMin()) * tariffEMList.get(2).getActiveEnergyCharge());
                }
                // 600 ~
                else {
                    returnBill = tariffEMList.get(1).getSupplySizeMax() * tariffEMList.get(1).getActiveEnergyCharge();
                    returnBill += (tariffEMList.get(2).getSupplySizeMax()-tariffEMList.get(2).getSupplySizeMin()) * 
                            tariffEMList.get(2).getActiveEnergyCharge();
                    returnBill += ((usage - tariffEMList.get(3).getSupplySizeMin()) * tariffEMList.get(3).getActiveEnergyCharge());
                }
            }
        }
        else if (tariffName.equals("Non Residential")) {
            // ~ 300
            if (usage <= tariffEMList.get(0).getSupplySizeMax()) {
                returnBill = usage * tariffEMList.get(0).getActiveEnergyCharge();
            }
            // ~ 600 
            else if (usage <= tariffEMList.get(1).getSupplySizeMax()) {
                returnBill = tariffEMList.get(0).getSupplySizeMax() * tariffEMList.get(0).getActiveEnergyCharge();
                returnBill += ((usage - tariffEMList.get(1).getSupplySizeMin()) * tariffEMList.get(1).getActiveEnergyCharge());
            }
            // 600 ~
            else {
                returnBill = tariffEMList.get(0).getSupplySizeMax() * tariffEMList.get(0).getActiveEnergyCharge();
                returnBill += (tariffEMList.get(1).getSupplySizeMax()-tariffEMList.get(1).getSupplySizeMin()) * tariffEMList.get(1).getActiveEnergyCharge();
                returnBill += ((usage - tariffEMList.get(2).getSupplySizeMin()) * tariffEMList.get(2).getActiveEnergyCharge());
            }
        }
        else if (tariffName.contains("SLT")) {
            returnBill = usage * tariffEMList.get(0).getActiveEnergyCharge();
        }
        */
        double supplyMin = 0.0;
        double supplyMax = 0.0;
        double block = 0.0;
        double blockUsage = 0.0;
        Double activeEnergy = 0.0;
        Double publicLevy = 0.0;
        Double govLevy = 0.0;
        Double blockBill = 0.0;
        
        for(int cnt=0 ; cnt < tariffEMList.size(); cnt++){
            supplyMin = tariffEMList.get(cnt).getSupplySizeMin() == null ? 0.0 : tariffEMList.get(cnt).getSupplySizeMin();
            supplyMax = tariffEMList.get(cnt).getSupplySizeMax() == null ? 0.0 : tariffEMList.get(cnt).getSupplySizeMax();
            
            log.info("[" + cnt + "] supplyMin : " + supplyMin + ", supplyMax : " + supplyMax);
            
            //Tariff 첫 구간
            if (usage >= supplyMin) {
                if(supplyMax != 0) {
                    // Tariff가 Residential이면 사용량이 51 이상일때 두번째 block 요금제 적용
                    if (tariffName.equals("Residential")) {
                        if (cnt == 0 && usage >= supplyMax) continue;
                        if (cnt == 1) supplyMin = 0;
                    }
                    block = supplyMax - supplyMin;
                    
                    blockUsage = usage - supplyMax;
                    
                    if (blockUsage < 0) blockUsage = usage - supplyMin;
                    else blockUsage = block;
                } else {
                    blockUsage = usage - supplyMin;
                }
                
                activeEnergy = tariffEMList.get(cnt).getActiveEnergyCharge() == null ? 0d : tariffEMList.get(cnt).getActiveEnergyCharge();
                publicLevy = tariffEMList.get(cnt).getDistributionNetworkCharge() == null ? 0d : tariffEMList.get(cnt).getDistributionNetworkCharge();
                govLevy = tariffEMList.get(cnt).getTransmissionNetworkCharge() == null ? 0d : tariffEMList.get(cnt).getTransmissionNetworkCharge();
                
                activeEnergy = activeEnergy + activeEnergy * publicLevy + activeEnergy * govLevy;
                blockBill = blockUsage * activeEnergy;
                returnBill = returnBill + blockBill;
                log.info("Block Usage[" + blockUsage + "]");
                log.info("ActiveEnergyCharge: " + tariffEMList.get(cnt).getActiveEnergyCharge());
                log.info("block bill[" + blockBill + "]");
            }
        }
        
        
        return returnBill;
    }
    
    public double[] blockNewSubsidy(String tariffName, List<TariffEM> tariffEMList, double usage) {
        Collections.sort(tariffEMList, new Comparator<TariffEM>() {

            @Override
            public int compare(TariffEM t1, TariffEM t2) {
                return t1.getSupplySizeMin() < t2.getSupplySizeMin()? -1:1;
            }
        });
        
        Double vatOnSubsidy = 0.0;
        Double newSubsidyRate = 0.0;
        Double newSubsidy = 0.0;
        Double vatOnSubsidyRate = 0.0;
        double supplyMin = 0.0;
        double supplyMax = 0.0;
        double block = 0.0;
        double blockUsage = 0.0;
        for(int cnt=0 ; cnt < tariffEMList.size(); cnt++){
            supplyMin = tariffEMList.get(cnt).getSupplySizeMin() == null ? 0.0 : tariffEMList.get(cnt).getSupplySizeMin();
            supplyMax = tariffEMList.get(cnt).getSupplySizeMax() == null ? 0.0 : tariffEMList.get(cnt).getSupplySizeMax();
            
            log.info("[" + cnt + "] supplyMin : " + supplyMin + ", supplyMax : " + supplyMax);
            
            //Tariff 첫 구간 
            if (usage >= supplyMin) {
                if(supplyMax != 0) {
                    // Tariff가 Residential이면 사용량이 51 이상일때 두번째 block 요금제 적용
                    if (tariffName.equals("Residential")) {
                        if (cnt == 0 && usage >= supplyMax) continue;
                        if (cnt == 1) supplyMin = 0;
                    }
                    block = supplyMax - supplyMin;
                    
                    blockUsage = usage - supplyMax;
                    
                    if (blockUsage < 0) blockUsage = usage - supplyMin;
                    else blockUsage = block;
                } else {
                    blockUsage = usage - supplyMin;
                }
                
                vatOnSubsidyRate = StringUtil.nullToDoubleZero(tariffEMList.get(cnt).getEnergyDemandCharge()) 
                        * StringUtil.nullToDoubleZero(tariffEMList.get(cnt).getRateRebalancingLevy());
                
                vatOnSubsidyRate = Double.parseDouble(String.format("%.4f", vatOnSubsidyRate));
                vatOnSubsidy += (vatOnSubsidyRate * blockUsage);
                newSubsidyRate = (StringUtil.nullToDoubleZero(vatOnSubsidyRate) 
                        + StringUtil.nullToDoubleZero(tariffEMList.get(cnt).getRateRebalancingLevy()));
                newSubsidy += (newSubsidyRate * blockUsage);
                
                log.info("blockUsage: " + blockUsage);
                log.info("vatOnNewSubsidyRate: " + vatOnSubsidyRate);
                log.info("vatOnNewSubsidy: " + vatOnSubsidy);
                log.info("newSubsidyRate: " + newSubsidyRate);
                log.info("newSubsidy: " + newSubsidy);
            }
        }
        
        
        return new double[]{newSubsidy, vatOnSubsidy};
    }
}