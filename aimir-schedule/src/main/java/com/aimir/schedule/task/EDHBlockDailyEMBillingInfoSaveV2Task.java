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
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants.DeviceType;
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
import com.aimir.model.device.Meter;
import com.aimir.model.mvm.BillingBlockTariff;
import com.aimir.model.mvm.LpEM;
import com.aimir.model.mvm.MonthEM;
import com.aimir.model.system.Code;
import com.aimir.model.system.Contract;
import com.aimir.model.system.PrepaymentLog;
import com.aimir.model.system.TariffEM;
import com.aimir.model.system.TariffType;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.TimeUtil;

/**
 * EDH의 Block Tariff 선불 요금을 계산하는 스케줄
 * Block이 월단위이므로 월사용량을 기준으로 요금을 계산해야 한다.
 *
 * @author elevas
 *
 */
public class EDHBlockDailyEMBillingInfoSaveV2Task extends ScheduleTask {

    protected static Log log = LogFactory.getLog(EDHBlockDailyEMBillingInfoSaveV2Task.class);
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

    private boolean isNowRunning = false;
    
    /**
     *
     * @author jiae
     * @desc   EDH 에서 사용하는 채널
     *
     */
    enum I210Channel {
        ActiveEnergyImp(1), ActiveEnergyExp(2);

        private Integer channel;

        I210Channel(Integer channel) {
            this.channel = channel;
        }

        public Integer getChannel() {
            return this.channel;
        }
    }

    @Override
	public void execute(JobExecutionContext context) {
		if(isNowRunning){
			log.info("########### EDHBlockDailyEMBillingInfoSaveV2Task is already running...");
			return;
		}
		isNowRunning = true;
		
        log.info("########### START EDHBlockDailyEMBillingInfo ###############");

        // 일별 전기 요금 정보 등록
        this.saveEmBillingDayInfo();

        log.info("########### END EDHBlockDailyEMBillingInfo ############");
        isNowRunning = false;
    }//execute end

    public void saveEmBillingDayInfo() {

        // 전기 계약 정보 취득
        List<Integer> em_contractIds = this.getContractInfos(SERVICE_TYPE_EM);

        TransactionStatus txStatus = null;

        for(Integer contract_id : em_contractIds) {
            // log.info("ContractId[" + contract_id + "]");

            txStatus = txManager.getTransaction(null);
            Contract contract = null;
            try {
                contract = contractDao.get(contract_id);
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
                log.error("saveEmBillingDayInfo Exception ==> Contract number = " + contract.getContractNumber(), e);
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
        log.info("1. Contract[" + contract.getContractNumber() + "] "
        		+ " lastAccumulateBill: " + lastAccumulateBill 
        		+ " lastAccumulateUsage: " + lastAccumulateUsage 
        		+ " lastAccumulateDate: " + lastAccumulateDate);

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

                // 바뀐타리프적용전 데이터가 올라온경우 바뀌기전 타리프로 계산해야하기때문에 추가
                int a = Integer.parseInt(yyyymm);                
                boolean isOldTariff = a < 201503 ? true : false; 
                
                savePrebill(contract.getTariffIndex().getName(), contract.getMeter(), yyyymm, lastAccumulateBill, lastAccumulateUsage, tariffEMList, isOldTariff);
                cal.add(Calendar.MONTH, 1);
                lastAccumulateBill = 0;
                lastAccumulateUsage = 0;
            }
        }
    }

    // yyyymm의 월 사용량을 가져와 선불을 계산하고 마지막 선불요금을 뺀다.
    private void savePrebill(String tariffName, Meter meter, String yyyymm, double lastAccumulateBill, double lastAccumulateUsage, List<TariffEM> tariffEMList, boolean isOldTariff) throws Exception
    {
    	log.info("2. ####savePrebill####  MeterId[" + meter.getMdsId() + "] YYYYMM[" + yyyymm + "]");

        //마지막 누적요금이 계산된 달부터 오늘까지의 MonthEM
        //EDH에 4개의 채널이 적용되어 있음.
        List<Integer> channelList = new ArrayList<Integer>();
        channelList.add(I210Channel.ActiveEnergyImp.getChannel());
        // channelList.add(I210Channel.ActiveEnergyExp.getChannel());
        // channelList.add(KamstrupChannel.ReactiveEnergyImp.getChannel());
        // channelList.add(KamstrupChannel.ReactiveEnergyExp.getChannel());

        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("mdevType", DeviceType.Meter);
        condition.put("mdevId", meter.getMdsId());
        condition.put("channelList", channelList);
        condition.put("yyyymm", yyyymm);
        condition.put("dst", 0);

        List<MonthEM> monthEMs = monthEMDao.getMonthEMsByCondition(condition);

        log.info("3. MeterId[" + meter.getMdsId() + "] YYYYMM[" + yyyymm + "] ==> monthEM size: " + monthEMs.size());
        if (monthEMs.size() > 0) {
            MonthEM monthEM = monthEMs.get(0);
            monthEM.setMDevType(DeviceType.Meter.name());
            monthEM.setMDevId(meter.getMdsId());

            double monthBill = 0.0;
            
            // 월 선불금액 계산
            if(isOldTariff){  // 이전 타리프로 계산해야 하는지 체크
                monthBill = blockBill1(meter.getMdsId(), tariffName, tariffEMList, monthEM.getTotal());
            }else {
                monthBill = blockBill2(meter.getMdsId(), tariffName, tariffEMList, monthEM.getTotal());            	
            }


            // 월의 마지막 lp 데이타의 시간을 가져온다.
            String lastLpTime = getLastLpTime(meter.getMdsId(), monthEM);
            // LP 날짜로 Billing Day를 생성하거나 업데이트한다.
            saveBillingBlockTariff(meter, monthEM.getTotal(), lastLpTime, monthBill, lastAccumulateBill);

            // 잔액을 차감한다.
            Contract contract = meter.getContract();
            contract.setCurrentCredit((contract.getCurrentCredit()==null? 0:contract.getCurrentCredit()) - (monthBill - lastAccumulateBill));
            contractDao.update(contract);
            log.info("7. [Update CurrentCredit Contract:"+ contract.getContractNumber() + " ] MeterId[" + meter.getMdsId() + "] YYYYMM[" + yyyymm + "] "
            		+ "==> MonthBill[" + monthBill + "] lastAccumulateBill[" + lastAccumulateBill+ "]");

            // 선불로그 기록
            savePrepyamentLog(contract, monthEM.getTotal()-lastAccumulateUsage, monthBill-lastAccumulateBill, lastLpTime);
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

        billingBlockTariffDao.saveOrUpdate(bill);
        
        log.info("6. [SaveBillingBlockTariff] MeterId[" + bill.getMDevId() + "] BillDay[" + bill.getYyyymmdd() +
                "] BillTime[" + bill.getHhmmss() + "] AccumulateUsage[" + bill.getAccumulateUsage() +
                "] AccumulateBill[" + bill.getAccumulateBill() + "] CurrentBill[" + bill.getBill() + "]");

        
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

        log.info("8(END). [Save PrepaymentLog Contract:" + contract.getContractNumber() + "] ==> " + prepaymentLog.toString());

        prepaymentLogDao.saveOrUpdate(prepaymentLog);
    }

    /*
     * BillingDayEM의 시간에 넣기 위한 LP 마지막 시간을 가져온다.
     */
    private String getLastLpTime(String meterId, MonthEM monthEM) throws Exception {
        String yyyymmdd = null;
        String lpValue = null;
        for (int i = 31; i > 0; i--) {
            lpValue = BeanUtils.getProperty(monthEM, String.format("value_%02d", i));
            if (lpValue != null && !lpValue.equals("")) {
                yyyymmdd = String.format("%s%02d", monthEM.getYyyymm(), i);
                log.info("5-1. MeterId [" + meterId + "] getLastLpTime: " + yyyymmdd);
                break;
            }
        }
        if (yyyymmdd != null) {
            Set<Condition> condition = new HashSet<Condition>();
            log.info("5-2. MeterId[" + meterId + "] MDevType[" + monthEM.getMDevType() + "] MDevId[" + monthEM.getMDevId() + "] YYYYMMDD[" + yyyymmdd + "]");
            condition.add(new Condition("id.mdevType", new Object[]{monthEM.getMDevType()}, null, Restriction.EQ));
            condition.add(new Condition("id.mdevId", new Object[]{monthEM.getMDevId()}, null, Restriction.EQ));
            condition.add(new Condition("id.channel", new Object[]{1}, null, Restriction.EQ));
            condition.add(new Condition("id.yyyymmddhh", new Object[]{yyyymmdd+"00", yyyymmdd+"23"}, null, Restriction.BETWEEN));
            // condition.add(new Condition("id.dst", new Object[]{0}, null, Restriction.EQ));

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

            if (lastLp.getValue_30() != null)
                return lastLp.getYyyymmddhh() + "3000";
            else if (lastLp.getValue_00() != null)
                return lastLp.getYyyymmddhh() + "0000";
        }

        return null;
    }

    public double blockBill1(String mdsId, String tariffName, List<TariffEM> tariffEMList, double usage) {
        double returnBill = 0.0;

        Collections.sort(tariffEMList, new Comparator<TariffEM>() {

            @Override
            public int compare(TariffEM t1, TariffEM t2) {
                return t1.getSupplySizeMin() < t2.getSupplySizeMin()? -1:1;
            }
        });

        if (tariffName.equals("Industrie BT") || tariffName.equalsIgnoreCase("Commerce")) {
            returnBill = usage * tariffEMList.get(0).getActiveEnergyCharge() + tariffEMList.get(0).getServiceCharge();
        }
        else if (tariffName.equals("Industrie MT")) {
           
        }
        else {
            returnBill = tariffEMList.get(0).getServiceCharge();
            // 0 ~ 1
            if (usage <= tariffEMList.get(1).getSupplySizeMin()) {
                
            }
            // 1 ~
            else {
                // ~ 30
                if (usage <= tariffEMList.get(1).getSupplySizeMax()) {
                    returnBill += usage * tariffEMList.get(1).getActiveEnergyCharge();
                }
                // ~ 200
                else if (usage <= tariffEMList.get(2).getSupplySizeMax()) {
                    returnBill += tariffEMList.get(1).getSupplySizeMax() * tariffEMList.get(1).getActiveEnergyCharge();
                    returnBill += ((usage - tariffEMList.get(2).getSupplySizeMin()) * tariffEMList.get(2).getActiveEnergyCharge());
                }
                // 200 ~
                else {
                    returnBill += tariffEMList.get(1).getSupplySizeMax() * tariffEMList.get(1).getActiveEnergyCharge();
                    returnBill += (tariffEMList.get(2).getSupplySizeMax()-tariffEMList.get(2).getSupplySizeMin()) * tariffEMList.get(2).getActiveEnergyCharge();
                    returnBill += ((usage - tariffEMList.get(3).getSupplySizeMin()) * tariffEMList.get(3).getActiveEnergyCharge());
                }
            }
        }

        double tca = returnBill * 0.1;
        double frais = tca * 0.1;
        
        log.debug("4. BlockBill1 Meter[" + mdsId + "] Bill[" + returnBill + "] TCA[" + tca + "] Frais[" + frais + "] Total[" + returnBill + tca + frais + "]");
        
        return returnBill + tca + frais;
    }    
    
    public double blockBill2(String mdsId, String tariffName, List<TariffEM> tariffEMList, double usage) {
        double returnBill = 0.0;

        Collections.sort(tariffEMList, new Comparator<TariffEM>() {

            @Override
            public int compare(TariffEM t1, TariffEM t2) {
                return t1.getSupplySizeMin() < t2.getSupplySizeMin()? -1:1;
            }
        });

        if (tariffName.equals("Industrie BT")) {
            returnBill = usage * tariffEMList.get(0).getActiveEnergyCharge() + tariffEMList.get(0).getServiceCharge();
        }
        else if (tariffName.equals("Industrie MT")) {
            // Heures creuses와 Heures pleines 두 종류가 있음.
        }
        else {
        //tariffName : Residence or Commerce
            returnBill = tariffEMList.get(0).getServiceCharge();
//            returnBill += returnBill * 0.05; 
            // 0 ~ 1
            if (usage <= tariffEMList.get(1).getSupplySizeMin()) {
                
            }
            // 1 ~
            else {
                // ~ 30
                if (usage <= tariffEMList.get(1).getSupplySizeMax()) {
                    returnBill += usage * tariffEMList.get(1).getActiveEnergyCharge();
                    
        //            returnBill += returnBill * 0.05;
                }
                // ~ 200
                else if (usage <= tariffEMList.get(2).getSupplySizeMax()) {
                    returnBill += tariffEMList.get(1).getSupplySizeMax() * tariffEMList.get(1).getActiveEnergyCharge();
                    returnBill += ((usage - tariffEMList.get(2).getSupplySizeMin()) * tariffEMList.get(2).getActiveEnergyCharge());
                    
          //          returnBill += returnBill * 0.05;
                }
                // 200 ~
                else {
                    returnBill += tariffEMList.get(1).getSupplySizeMax() * tariffEMList.get(1).getActiveEnergyCharge();
                    returnBill += (tariffEMList.get(2).getSupplySizeMax()-tariffEMList.get(2).getSupplySizeMin()) *
                            tariffEMList.get(2).getActiveEnergyCharge();
                    returnBill += ((usage - tariffEMList.get(3).getSupplySizeMin()) * tariffEMList.get(3).getActiveEnergyCharge());
                    
        //            returnBill += returnBill * 0.05;
                }
            }
        }

        // TCA = returnBill * 0.1
        double tca = returnBill * 0.1;

        // Frais special = tca * 0.1;
        double frais = tca * 0.1;
        
        // 충전금 Service Charge 5%
//        double serviceCharge = frais * 0.05;
//        log.debug("New Service Charge[" + serviceCharge + "]");

//        return returnBill + tca + frais + serviceCharge;
        
        log.debug("4. BlockBill2 Meter[" + mdsId + "] Bill[" + returnBill + "] TCA[" + tca + "] Frais[" + frais + "] Total[" + returnBill + tca + frais + "]");
        
        return returnBill + tca + frais;
    }
}