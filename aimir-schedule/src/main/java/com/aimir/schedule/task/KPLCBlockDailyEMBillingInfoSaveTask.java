package com.aimir.schedule.task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.mvm.BillingBlockTariffDao;
import com.aimir.dao.mvm.DayEMDao;
import com.aimir.dao.mvm.LpEMDao;
import com.aimir.dao.mvm.MonthEMDao;
import com.aimir.dao.mvm.impl.LpEMDaoImpl;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.dao.system.OperatorDao;
import com.aimir.dao.system.PrepaymentLogDao;
import com.aimir.dao.system.SupplyTypeDao;
import com.aimir.dao.system.TariffEMDao;
import com.aimir.dao.system.TariffTypeDao;
import com.aimir.fep.util.DataUtil;
import com.aimir.model.device.Meter;
import com.aimir.model.mvm.BillingBlockTariff;
import com.aimir.model.mvm.LpEM;
import com.aimir.model.system.Code;
import com.aimir.model.system.Contract;
import com.aimir.model.system.Customer;
import com.aimir.model.system.Operator;
import com.aimir.model.system.PrepaymentLog;
import com.aimir.model.system.TariffEM;
import com.aimir.model.system.TariffType;
import com.aimir.util.Condition;
import com.aimir.util.StringUtil;
import com.aimir.util.Condition.Restriction;
import com.aimir.util.DateTimeUtil;

/**
 * KPLC의 Block Tariff 선불 요금을 계산하는 스케줄
 * Block이 월단위이므로 월사용량을 기준으로 요금을 계산해야 한다.
 * 
 * 마지막 지침값과 이전 지침값을 가지고 계산한다.
 * @author elevas
 * @since 2015.12.01
 */
@Service
public class KPLCBlockDailyEMBillingInfoSaveTask extends ScheduleTask {
    
    protected static Log log = LogFactory.getLog(KPLCBlockDailyEMBillingInfoSaveTask.class);
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
    OperatorDao operatorDao;
    
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
        
        KPLCBlockDailyEMBillingInfoSaveTask task = ctx.getBean(KPLCBlockDailyEMBillingInfoSaveTask.class);
        task.execute(null);
        System.exit(0);
    }

    
    public void execute(JobExecutionContext context) {
		if(isNowRunning){
			log.info("########### ECGBlockDailyEMBillingInfoSaveV4Task is already running...");
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
                this.saveEmBillingDailyWithTariffEMCumulationCost(contract_id);
            }
            catch (Exception e) {
                log.error(e, e);
            }
        }
        // setSuccessResult();
    }

    public List<Integer> getContractInfos(String serviceType){
        TransactionStatus txstatus = null;
        List<Integer> contractIds = new ArrayList<Integer>();
        try {
            txstatus = txmanager.getTransaction(null);
            Set<Condition> condition = new HashSet<Condition>();
            condition.add(new Condition("location", new Object[]{"l"}, null, Restriction.ALIAS));
            condition.add(new Condition("serviceTypeCode", new Object[]{"s"}, null, Restriction.ALIAS));
            condition.add(new Condition("s.name", new Object[]{serviceType}, null, Restriction.EQ));
            condition.add(new Condition("creditType", new Object[]{"c"}, null, Restriction.ALIAS));
            condition.add(new Condition("c.code", new Object[]{Code.PREPAYMENT, Code.EMERGENCY_CREDIT}, null, Restriction.IN));
            List<Contract> contracts = contractDao.findByConditions(condition);
            txmanager.commit(txstatus);
            
            for (Contract c : contracts) {
                contractIds.add(c.getId());
            }
        }
        catch (Exception e) {
            log.error(e, e);
            if (txstatus != null) txmanager.rollback(txstatus);
        }
        return contractIds;
    }
    
    public void saveEmBillingDailyWithTariffEMCumulationCost(int contract_id)
            throws Exception
    {
        TransactionStatus txstatus = null;
        try {
            txstatus = txmanager.getTransaction(null);
            txmanager.setDefaultTimeout(-1);
            Contract contract = contractDao.get(contract_id);
            Code code = contract.getCreditType();
            if (contract.getTariffIndexId() != null && contract.getCustomer() != null && contract.getMeter() != null) {
                if(Code.PREPAYMENT.equals(code.getCode()) || Code.EMERGENCY_CREDIT.equals(code.getCode())) { // 선불 요금일 경우
                    log.info("Contract[" + contract.getContractNumber() + "] Meter[" + contract.getMeter().getMdsId() + "]");
                    // 마지막 activenergy 값을 가져온다.
                    // 검침된 적이 없으면 널값이 온다.
                    LpEM[] lastLp = getLastLp(contract.getMeter().getMdsId(), null, contract.getMeter().getInstallDate());
                    if (lastLp != null && lastLp.length == 2) {
                        BillingBlockTariff prevBill = getLastBillingBlockTariff(contract.getMeter().getMdsId(), lastLp);
                        saveBill(contract, lastLp, prevBill);
                    }
                }
            }
            if (!txstatus.isCompleted())
                txmanager.commit(txstatus);
        }
        catch (Exception e) {
            log.error(e, e);
            if (txstatus != null && !txstatus.isCompleted())
                txmanager.rollback(txstatus);
        }
    }
    
    /**
     * 선불시작일 널이 아니면 이전 LP 중 제일 마지막 LP를 가져온다.
     * 선불시작일이 널이면 제일 큰 값으로 가져온다.
     * @param meterId
     * @return
     */
    public LpEM[] getLastLp(String meterId, String prepayStartDate, String meterInstallDate) {
        TransactionStatus txstatus = null;
        try {
            txstatus = txmanager.getTransaction(null);
            Set<Condition> conditions = new HashSet<Condition>();
            conditions.add(new Condition("id.mdevType", new Object[]{DeviceType.Meter}, null, Restriction.EQ));
            conditions.add(new Condition("id.mdevId", new Object[]{meterId}, null, Restriction.EQ));
            conditions.add(new Condition("id.channel", new Object[]{KamstrupChannel.ActiveEnergyImp.getChannel()},
                    null, Restriction.EQ));
            conditions.add(new Condition("id.dst", new Object[]{0}, null, Restriction.EQ));
            conditions.add(new Condition("id.yyyymmddhh", new Object[]{meterInstallDate.substring(0, 10)}, null, Restriction.GE));
            
            if (prepayStartDate != null && !"".equals(prepayStartDate))
                conditions.add(new Condition("id.yyyymmddhh", new Object[]{prepayStartDate.substring(0, 10)}, null, Restriction.LE));
            
            List<Projection> projections = new ArrayList<Projection>();
            projections.add(Projections.alias(Projections.max("id.yyyymmddhh"), "maxYyyymmddhh"));
            
            List<Map<String, Object>> maxyyyymmddhh = ((LpEMDaoImpl)lpEMDao).findByConditionsAndProjections(conditions, projections);
            
            if (maxyyyymmddhh != null && maxyyyymmddhh.size() == 1) {
                log.info(maxyyyymmddhh.get(0));
                String _yyyymmddhh = (String)maxyyyymmddhh.get(0).get("maxYyyymmddhh");
                if (_yyyymmddhh == null)
                    return null;
                
                conditions = new HashSet<Condition>();
                conditions.add(new Condition("id.yyyymmddhh", new Object[]{_yyyymmddhh}, null, Restriction.EQ));
                conditions.add(new Condition("id.dst", new Object[]{0}, null, Restriction.EQ));
                conditions.add(new Condition("id.mdevType", new Object[]{DeviceType.Meter}, null, Restriction.EQ));
                conditions.add(new Condition("id.mdevId", new Object[]{meterId}, null, Restriction.EQ));
                conditions.add(new Condition("id.channel", new Object[]{KamstrupChannel.ActiveEnergyImp.getChannel(),
                        KamstrupChannel.ActiveEnergyExp.getChannel()}, null, Restriction.IN));
                
                return (LpEM[])lpEMDao.findByConditions(conditions).toArray(new LpEM[0]);
            }
            txmanager.commit(txstatus);
        }
        catch (Exception e) {
            log.error(e, e);
            if (txstatus != null) txmanager.rollback(txstatus);
        }
        return null;
    }
    
    /**
     * 고객의 선불시작일 기준으로 날짜가 크거나 또는 계약 관계가 널인 것인 최초의 LP를 가져온다.
     * @param meterId
     * @return
     */
    public LpEM[] getFirstLp(String meterId, String contractNumber, String prepayStartDate) {
        Set<Condition> conditions = new HashSet<Condition>();
        
        // 계약정보가 널인 것으로 먼저 찾고
        conditions.add(new Condition("id.mdevType", new Object[]{DeviceType.Meter}, null, Restriction.EQ));
        conditions.add(new Condition("id.mdevId", new Object[]{meterId}, null, Restriction.EQ));
        conditions.add(new Condition("id.channel", new Object[]{KamstrupChannel.ActiveEnergyImp.getChannel()},
                null, Restriction.EQ));
        conditions.add(new Condition("id.dst", new Object[]{0}, null, Restriction.EQ));
        conditions.add(new Condition("id.yyyymmddhh", new Object[]{prepayStartDate.substring(0, 10)}, null, Restriction.GE));
        conditions.add(new Condition("contract", null, null, Restriction.NULL));
        // conditions.add(new Condition("c.contractNumber", null, null, Restriction.NULL));
        
        List<Projection> projections = new ArrayList<Projection>();
        projections.add(Projections.alias(Projections.min("id.yyyymmddhh"), "minYyyymmddhh"));
        
        List<Map<String, Object>> minyyyymmddhh = ((LpEMDaoImpl)lpEMDao).findByConditionsAndProjections(conditions, projections);
        
        String yyyymmddhh_contractnull = (String)minyyyymmddhh.get(0).get("minYyyymmddhh");
        
        // 계약정보가 있는 것으로 찾는다.
        conditions = new HashSet<Condition>();
        conditions.add(new Condition("id.mdevType", new Object[]{DeviceType.Meter}, null, Restriction.EQ));
        conditions.add(new Condition("id.mdevId", new Object[]{meterId}, null, Restriction.EQ));
        conditions.add(new Condition("id.channel", new Object[]{KamstrupChannel.ActiveEnergyImp.getChannel()},
                null, Restriction.EQ));
        conditions.add(new Condition("id.dst", new Object[]{0}, null, Restriction.EQ));
        conditions.add(new Condition("id.yyyymmddhh", new Object[]{prepayStartDate.substring(0, 10)}, null, Restriction.GE));
        conditions.add(new Condition("contract", new Object[]{"c"}, null, Restriction.ALIAS));
        conditions.add(new Condition("c.contractNumber", new Object[]{contractNumber}, null, Restriction.EQ));
        
        minyyyymmddhh = ((LpEMDaoImpl)lpEMDao).findByConditionsAndProjections(conditions, projections);
        
        String yyyymmddhh_contract = (String)minyyyymmddhh.get(0).get("minYyyymmddhh");
        
        String _yyyymmddhh = null;
        if (yyyymmddhh_contractnull == null && yyyymmddhh_contract == null)
            return null;
        else if (yyyymmddhh_contractnull != null && yyyymmddhh_contract == null)
            _yyyymmddhh = yyyymmddhh_contractnull;
        else if (yyyymmddhh_contractnull == null && yyyymmddhh_contract != null)
            _yyyymmddhh = yyyymmddhh_contract;
        else {
            if (yyyymmddhh_contractnull.compareTo(yyyymmddhh_contract) > 0)
                _yyyymmddhh = yyyymmddhh_contract;
            else
                _yyyymmddhh = yyyymmddhh_contractnull;
        }

        log.info(_yyyymmddhh);
        conditions = new HashSet<Condition>();
        conditions.add(new Condition("id.yyyymmddhh", new Object[]{_yyyymmddhh}, null, Restriction.EQ));
        conditions.add(new Condition("id.dst", new Object[]{0}, null, Restriction.EQ));
        conditions.add(new Condition("id.mdevType", new Object[]{DeviceType.Meter}, null, Restriction.EQ));
        conditions.add(new Condition("id.mdevId", new Object[]{meterId}, null, Restriction.EQ));
        conditions.add(new Condition("id.channel", new Object[]{KamstrupChannel.ActiveEnergyImp.getChannel(),
                KamstrupChannel.ActiveEnergyExp.getChannel()}, null, Restriction.IN));
        
        return (LpEM[])lpEMDao.findByConditions(conditions).toArray(new LpEM[0]);
    }
    
    /**
     * 마지막 BillingBlockTariff를 가져온다.
     * 만약 BillingBlockTariff 정보가 없으면 고객의 계약기간에 해당하는 LP를 가져와서 누적치를 이용하는데
     * 이것도 없으면 미터 설치시간값을 넘기고 active energy값은 0이다.
     * 
     * @param meterId
     * @return
     */
    public BillingBlockTariff getLastBillingBlockTariff(String meterId, LpEM[] lastLp) throws Exception {
        // 미터 시리얼번호로 가장 최근 빌링 정보를 가져온다.
        List<Map<String, Object>> list = billingBlockTariffDao.getLastAccumulateBill(meterId);

        // 빌링정보가 있으면 미터의 고객과 상관없이 마지막 누적치를 이용하여 
        if (list != null && list.size() == 1) {
            Map<String, Object> map = list.get(0);
            
            BillingBlockTariff bbt = new BillingBlockTariff();
            bbt.setMDevType("Meter");
            bbt.setMDevId(meterId);
            bbt.setYyyymmdd((String)map.get("YYYYMMDD"));
            bbt.setHhmmss((String)map.get("HHMMSS"));
            
            // 고객 계약이 같지 않으면 요금과 사용량을 0으로 설정한다.
            String contractNumber = (String)map.get("CONTRACTNUMBER");
            if (lastLp[0] != null && lastLp[0].getContract() != null)
                log.info("BB_CONTRACT[" + contractNumber + "] LP_CONTRACT[" + lastLp[0].getContract().getContractNumber() + "]");
            else
                log.info("BB_CONTRACT[" + contractNumber + "] LP_CONTRACT[NULL]");
            
            if (contractNumber != null && lastLp[0].getContract() != null 
                    && lastLp[0].getContract().getContractNumber().equals(contractNumber)) {
                bbt.setAccumulateBill((Double)map.get("ACCUMULATEBILL"));
                bbt.setAccumulateUsage((Double)map.get("ACCUMULATEUSAGE"));
            }
            else {
                bbt.setAccumulateBill(0.0);
                bbt.setAccumulateUsage(0.0);
            }
            
            // 고객 계약이 같지 않지만 billingblocktariff의 마지막 누적값을 이용해야 한다.
            Object activeEnergy = map.get("ACTIVEENERGY");
            bbt.setActiveEnergy(activeEnergy == null? 0.0:(Double)activeEnergy);
            
            return bbt;
        }
        // 고객의 billingblocktariff가 없으면 미터에 대해서 선불계산한 적이 없는 최초 설치이기 때문에
        // 고객의 계약 기준으로 최초값을 가져온다.
        else {
            Meter meter = meterDao.get(meterId);
            Contract contract = meter.getContract();
            
            if (contract == null) return null;
            
            String prepayStartDate = contract.getPrepayStartTime();
            
            BillingBlockTariff bbt = new BillingBlockTariff();
            bbt.setMDevType("Meter");
            bbt.setMDevId(meterId);
            bbt.setAccumulateBill(0.0);
            bbt.setAccumulateUsage(0.0);
            
            if (prepayStartDate != null && !"".equals(prepayStartDate)) {
                bbt.setYyyymmdd(prepayStartDate.substring(0, 8));
                bbt.setHhmmss(prepayStartDate.substring(8, 10) + "0000");
                
                // 미터설치일 이후와 선불시작일 이전 LP중 마지막 LP를 가져온다.
                LpEM[] lps = getLastLp(meterId, prepayStartDate, meter.getInstallDate());
                
                // 고객 선불 시작일 이전 미터가 설치된 경우 또는 고객이 이사하여 미터가 변경된 경우로
                // 계약일이 반드시 변경되거나 새로 생성되어야 한다.
                if (lps != null && lps.length == 2) {
                    double activeEnergy = lps[0].getValue() + lps[1].getValue();
                    // lp 날짜와 계약일자의 차이가 발생하면 일평균을 구하여 차이만큼을 누적치에서 더한다.
                    // contractDate가 무조건 더 크다.
                    if (prepayStartDate.substring(0, 8).compareTo(lps[0].getYyyymmdd()) == 0) {
                        bbt.setYyyymmdd(lps[0].getYyyymmdd());
                        bbt.setHhmmss(lps[0].getYyyymmddhh().substring(8, 10) + "0000");
                        bbt.setActiveEnergy(activeEnergy);
                    }
                    // 선불시작일과 lp의 yyyymmdd의 차이 일수만큼 activeEnergy 값을 만든다. 
                    else {
                        double lastActiveEnergy = lastLp[0].getValue() + lastLp[1].getValue() -
                                lps[0].getValue() - lps[1].getValue();
                        double usageFromLpDateContractDate = calUsageFromLpDateContractDate(lastActiveEnergy, 
                                lps[0].getYyyymmddhh(), lastLp[0].getYyyymmddhh(), prepayStartDate);
                        activeEnergy += usageFromLpDateContractDate;
                        bbt.setActiveEnergy(activeEnergy);
                    }
                }
                // 고객의 계약일보다 작은 마지막 lp가 없으면 계약일 이후 LP 중 최초값을 가져온다.
                // 미터정보가 먼저 생성되어 계약이 먼저 생성된 경우나 미터가 교체된 경우에 해당할 것 같다.
                else {
                    // LP는 있는데 고객이 다르면 미터와 고객이 매핑된 첫번재 값을 가져온다.
                    LpEM[] firstLps = getFirstLp(meterId, contract.getContractNumber(), contract.getPrepayStartTime());
                 
                    // 평균값을 빼서 계약일 기준값으로 만든다.
                    double lastActiveEnergy = lastLp[0].getValue() + lastLp[1].getValueCnt() -
                            firstLps[0].getValue() - firstLps[1].getValue();
                    
                    // 미터 설치일이 계약일보다 늦으면 계약일에 해당하는 지침값을 계산하지 않는다.
                    if (meter.getInstallDate().substring(0, 8).compareTo(contract.getContractDate().substring(0, 8)) < 0) {
                        double usageContractFromLpDate = 
                                calContractDateFromLpDate(lastActiveEnergy, firstLps[0].getYyyymmddhh(),
                                        lastLp[0].getYyyymmddhh(), prepayStartDate);
                        bbt.setActiveEnergy(firstLps[0].getValue() + firstLps[1].getValue() - usageContractFromLpDate);
                    }
                    else {
                        bbt.setActiveEnergy(firstLps[0].getValue() + firstLps[1].getValue());
                    }
                }
                
                return bbt;
            }
            
            // 계약일이 없으면 미터 실치 기준일과 0부터 시작한다.
            if (meter.getInstallDate() == null || "".equals(meter.getInstallDate()) 
                    || meter.getInstallDate().length() != 14)
                throw new Exception("check install date of METER[" + meterId + "]");
            
            bbt.setYyyymmdd(meter.getInstallDate().substring(0, 8));
            bbt.setHhmmss(meter.getInstallDate().substring(8));
            bbt.setActiveEnergy(0.0);
            
            return bbt;
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
    
    public void saveBill(Contract contract, LpEM[] lastLp, BillingBlockTariff prevBill) 
    throws Exception
    {
        // 날짜 비교
        String lptime = lastLp[0].getYyyymmddhh();
        String prevBillTime = prevBill.getYyyymmdd() + prevBill.getHhmmss().substring(0, 2);
        
        // 마지막 LP시간이 이미 계산한 것이라면 종료한다.
        if (lptime.equals(prevBillTime))
            return;
        else {
            String lp_yyyymm = lptime.substring(0, 6);
            String prevBillTime_yyyymm = prevBillTime.substring(0, 6);
            
            // 두 개의 yyyymm을 비교하여 같으면 lp의 누적치에서 prevBill의 누적치의 차를 이용하여 계산한다.
            if (lp_yyyymm.equals(prevBillTime_yyyymm)) {
                savePrebill(contract.getTariffIndex().getName(),
                        contract.getMeter(), lptime, prevBill.getAccumulateBill(),
                        prevBill.getAccumulateUsage(), prevBill.getActiveEnergy(),
                        lastLp[0].getValue()+lastLp[1].getValue(), getTariff(contract.getTariffIndexId(),
                                lptime.substring(0, 8)));
            }
            else {
                // 지침값이 여러달인 경우 총 사용량을 이용하여 월 사용량을 구한다.
                // 월 사용량은 월정산으로 계산하고 마지막 말은 선불요금으로 계산한다.
                double billingActiveEnergy = prevBill.getActiveEnergy();
                double totalUsage = lastLp[0].getValue()+lastLp[1].getValue() - billingActiveEnergy;
                
                if (totalUsage <= 0) {
                    log.warn("TOTAL_USAGE[" + totalUsage + "] <= 0");
                    return;
                }
                
                // 마지막 달에 대해서만 선불요금을 실행하고 나머지는 월정산 실행
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
                Calendar cal = Calendar.getInstance();
                cal.setTime(sdf.parse(prevBillTime_yyyymm));
                String billYyyymm = null;
                String lastTokenDate = DateTimeUtil.getDateString(new Date());
                Operator operator = operatorDao.getOperatorByLoginId("admin");
                double avgDay = avgDay(totalUsage, prevBill.getYyyymmdd(), lptime);
                int lastDay = 1;
                // 위에서 구한 일평균을 이용하여 월사용량을 구한 후 월정산을 마지막 lp 이전 달까지 실행한다.
                while (true) {
                    billYyyymm = sdf.format(cal.getTime());
                    
                    // 정산할 년월이 마지막 lp 년월과 같으면 종료하고 나머지는 선불요금계산
                    if (lp_yyyymm.equals(billYyyymm)) {
                        // 마지막 사용량은 선불요금계산
                        // 마지막 달에 대해서는 요금계산이 실행된 적이 없기 때문에 lastAccumulateUsage와 lastAccumulateBill이 0이다.
                        savePrebill(contract.getTariffIndex().getName(),
                                contract.getMeter(), lptime, 0, 0, billingActiveEnergy,
                                lastLp[0].getValue()+lastLp[1].getValue(), getTariff(contract.getTariffIndexId(),
                                        lptime.substring(0, 8)));
                        break;
                    }
                    // 마지막 선불요금정산일과 같은 년월이면 마지막 일수에 대해서만 사용량을 구한다.
                    else if (prevBillTime_yyyymm.equals(billYyyymm)) {
                        lastDay = getLastDay(billYyyymm);
                        totalUsage = avgDay * (lastDay - Integer.parseInt(prevBill.getYyyymmdd().substring(6,8)) + 1);
                        
                        savePrebill(contract.getTariffIndex().getName(),
                                contract.getMeter(), billYyyymm+lastDay+"23", prevBill.getAccumulateBill(),
                                prevBill.getAccumulateUsage(), billingActiveEnergy,
                                billingActiveEnergy+totalUsage, getTariff(contract.getTariffIndexId(),
                                        billYyyymm+lastDay));
                    }
                    else {
                        lastDay = getLastDay(billYyyymm);
                        totalUsage = avgDay * lastDay;
                        
                        savePrebill(contract.getTariffIndex().getName(),
                                contract.getMeter(), billYyyymm+lastDay+"23", 0, 0, billingActiveEnergy,
                                billingActiveEnergy+totalUsage, getTariff(contract.getTariffIndexId(),
                                        billYyyymm+lastDay));
                    }
                    
                    // saveMonthBill(contract, totalUsage, billYyyymm, lastTokenDate, operator);
                    
                    // 정산한 마지막 ActiveEnergy
                    billingActiveEnergy += totalUsage;
                    cal.add(Calendar.MONTH, 1);
                }
            }
        }
    }
    
    /*
     * 시작 년월부터 마지막 LP의 년월까지의 일수를 계산하여 LP 전월까지의 월 평균 사용량을 계산하고
     * LP 시작부터 계약일까지의 사용량을 구한다. 이때 계약일이 LP 시작일과 마지막 일자 사이에 있어야 한다.
     */
    private double calUsageFromLpDateContractDate(double usage, String fromLpDate, String toLpDate, String contractDate)
            throws ParseException {
        // 마지막 LP의 일자를 가져온다.
        int days = 0;
        int sumDays = 0;
        double avg = 0.0;
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
        Calendar cal = Calendar.getInstance();
        cal.setTime(sdf.parse(fromLpDate.substring(0, 6)));
        Map<String, Double> yyyymmDays = new HashMap<String, Double>();
        
        int year = 0;
        int month = 1;
        String yearmonth;
        while (true) {
            year = cal.get(Calendar.YEAR);
            month = cal.get(Calendar.MONTH) + 1;
            yearmonth = String.format("%04d%02d", year, month);
            days = getLastDay(yearmonth);
            
            // 시작년월이 같으면 일자만큼 빼고 나머지 일수를 사용할 수 있도록 한다.
            if (yearmonth.equals(fromLpDate.substring(0, 6)) && 
                    yearmonth.equals(toLpDate.substring(0, 6))) {
                days = Integer.parseInt(toLpDate.substring(6,8)) - Integer.parseInt(fromLpDate.substring(6, 8));
            }
            else if (yearmonth.equals(fromLpDate.substring(0, 6))) {
                days = days - Integer.parseInt(fromLpDate.substring(6, 8));
            }
            else if (yearmonth.equals(toLpDate.substring(0, 6))) {
                days = Integer.parseInt(toLpDate.substring(6, 8));
            }
            
            sumDays += days;
            yyyymmDays.put(yearmonth, (double)days);
            log.debug("YYYYMM[" + yearmonth + "] DAYS[" + days + "] SUM_DAYS[" + sumDays + "]");
            
            // LP와 같은 년월이면 종료한다.
            if (yearmonth.equals(toLpDate.substring(0,6))) {
                break;
            }
            
            cal.add(Calendar.MONTH, 1);
        }
        
        log.info("FromLPDate[" + fromLpDate + "] ToLPDate[" + toLpDate + "] TotalLPDays[" + sumDays + "]");
        if (sumDays != 0) avg = usage / (double)sumDays;
        log.info("TotalUsage[" + usage + "] AvgUsage[" + avg + "]");
        
        String yyyymm = null;
        double usageFromLpDateContractDate = 0.0;
        for (Iterator<String> i = yyyymmDays.keySet().iterator(); i.hasNext(); ) {
            yyyymm = i.next();
            if (yyyymm.equals(contractDate.substring(0, 6))) {
                usageFromLpDateContractDate += avg * Integer.parseInt(contractDate.substring(6,8));
                break;
            }
            else 
                usageFromLpDateContractDate += avg * yyyymmDays.get(yyyymm);
        }
        
        return usageFromLpDateContractDate;
    }
    
    /*
     * 시작 년월부터 마지막 LP의 년월까지의 일수를 계산하여 LP 전월까지의 월 평균 사용량을 계산하고
     * 계약일부터 LP 시작일까지의 사용량을 계산한다.
     */
    private double calContractDateFromLpDate(double usage, String fromLpDate, String toLpDate, String prepayStartDate)
            throws ParseException {
        // 마지막 LP의 일자를 가져온다.
        int days = 0;
        int year = 0;
        int month = 0;
        String yearmonth = null;
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
        Calendar cal = Calendar.getInstance();
        
        double avg = avgDay(usage, fromLpDate, toLpDate);
        double usageContractDateFromLpDate = 0.0;
        cal = Calendar.getInstance();
        cal.setTime(sdf.parse(prepayStartDate.substring(0, 6)));
        while (true) {
            year = cal.get(Calendar.YEAR);
            month = cal.get(Calendar.MONTH) + 1;
            yearmonth = String.format("%04d%02d", year, month);
            days = getLastDay(yearmonth);
            
            // 계약일과 lp 시작일이 같은 년월이면 기간만큼의 사용량을 구한다.
            if (yearmonth.equals(prepayStartDate.substring(0, 6)) 
                    && yearmonth.equals(fromLpDate.substring(0, 6))) {
                usageContractDateFromLpDate += ((days - Integer.parseInt(prepayStartDate.substring(6,8))) * avg);
                usageContractDateFromLpDate -= ((days - Integer.parseInt(fromLpDate.substring(6,8))) * avg);
                break;
            }
            // 계약일과만 같으면 말일에서 계약일자만큼의 일수에 대한 사용량을 더한다.
            else if (yearmonth.equals(prepayStartDate.substring(0, 6))) {
                usageContractDateFromLpDate += ((days - Integer.parseInt(prepayStartDate.substring(6,8))) * avg);
            }
            // LP 시작일과 같으면 1일부터 시작일까지의 일수만큼의 사용량을 더한다.
            else if (yearmonth.equals(fromLpDate.substring(0, 6))) {
                usageContractDateFromLpDate += (Integer.parseInt(fromLpDate.substring(6,8)) * avg);
                break;
            }
            else {
                usageContractDateFromLpDate += (days * avg);
            }
            
            cal.add(Calendar.MONTH, 1);
        }
        return usageContractDateFromLpDate;
    }
    
    private double avgDay(double usage, String fromLpDate, String toLpDate) throws ParseException {
     // 마지막 LP의 일자를 가져온다.
        int days = 0;
        int sumDays = 0;
        double avg = 0.0;
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
        Calendar cal = Calendar.getInstance();
        cal.setTime(sdf.parse(fromLpDate.substring(0, 6)));
        Map<String, Double> yyyymmDays = new HashMap<String, Double>();
        
        int year = 0;
        int month = 1;
        String yearmonth;
        while (true) {
            year = cal.get(Calendar.YEAR);
            month = cal.get(Calendar.MONTH) + 1;
            yearmonth = String.format("%04d%02d", year, month);
            days = getLastDay(yearmonth);
            
            // 시작년월이 같으면 일자만큼 빼고 나머지 일수를 사용할 수 있도록 한다.
            if (yearmonth.equals(fromLpDate.substring(0, 6)) && 
                    yearmonth.equals(toLpDate.substring(0, 6))) {
                days = Integer.parseInt(toLpDate.substring(6,8)) - Integer.parseInt(fromLpDate.substring(6, 8)) + 1;
            }
            else if (yearmonth.equals(fromLpDate.substring(0, 6))) {
                days = days - Integer.parseInt(fromLpDate.substring(6, 8)) + 1;
            }
            else if (yearmonth.equals(toLpDate.substring(0, 6))) {
                days = Integer.parseInt(toLpDate.substring(6, 8));
            }
            
            sumDays += days;
            yyyymmDays.put(yearmonth, (double)days);
            log.debug("YYYYMM[" + yearmonth + "] DAYS[" + days + "] SUM_DAYS[" + sumDays + "]");
            
            // LP와 같은 년월이면 종료한다.
            if (yearmonth.equals(toLpDate.substring(0,6))) {
                break;
            }
            
            cal.add(Calendar.MONTH, 1);
        }
        
        log.info("FromLPDate[" + fromLpDate + "] ToLPDate[" + toLpDate + "] TotalLPDays[" + sumDays + "]");
        if (sumDays != 0) avg = usage / (double)sumDays;
        log.info("TotalUsage[" + usage + "] AvgUsage[" + avg + "]");
        
        return avg;
    }
    
    private List<TariffEM> getTariff(Integer tariffIndexId, String yyyymmdd) {
        TariffType tariffType = tariffTypeDao.get(tariffIndexId);
        Integer tariffTypeCode = tariffType.getCode();
        
        Map<String, Object> tariffParam = new HashMap<String, Object>();

        tariffParam.put("tariffTypeCode", tariffTypeCode);
        tariffParam.put("tariffIndex", tariffType);
        tariffParam.put("searchDate", yyyymmdd);
        
        return tariffEMDao.getApplyedTariff(tariffParam); 
    }
    
    // yyyymm의 월 사용량을 가져와 선불을 계산하고 마지막 선불요금을 뺀다.
    private void savePrebill(String tariffName, Meter meter, String lastLpTime, double blockBillAccumulateBill,
            double blockBillAccumulateUsage, double blockBillActiveEnergy, double lastLpActiveEnergy, List<TariffEM> tariffEMList)
    throws Exception
    {
    	log.info("####savePrebill####");
    	log.info("MeterId[" + meter.getMdsId() + "] LAST_LPTIME[" + lastLpTime + "]");
    	
    	// 월 사용량
        double usage = lastLpActiveEnergy - blockBillActiveEnergy + blockBillAccumulateUsage;
        
        // 사용량이 0보다 작거나 작으면 종료한다.
        if (usage < 0) {
            log.warn("MONTH_USAGE[" + usage + "] < 0");
            return;
        }
        
        // 모뎀이 ZRU 유형은 기존 방식대로 월 사용량을 기준으로 선불 계산한다.
        if (meter.getModem() != null && meter.getModem().getModemType() == ModemType.ZRU) {
            // 월 선불금액 계산
            double monthBill = blockBill(tariffName, tariffEMList, usage);
            
            // LP 날짜로 Billing Day를 생성하거나 업데이트한다.
            saveBillingBlockTariff(meter, usage, lastLpActiveEnergy, lastLpTime, monthBill, blockBillAccumulateBill);
            
            // 잔액을 차감한다.
            Contract contract = meter.getContract();
            log.info("contract_number[" + contract.getContractNumber() + 
                    "] MonthBill[" + monthBill + "] blockBillccumulateBill[" + blockBillAccumulateBill+ "]");
            contract.setCurrentCredit(contract.getCurrentCredit() - (monthBill - blockBillAccumulateBill));
            contractDao.updateCurrentCredit(contract.getId(), contract.getCurrentCredit());
            
            // 선불로그 기록
            savePrepyamentLog(contract, lastLpActiveEnergy - blockBillActiveEnergy,
                    monthBill-blockBillAccumulateBill, lastLpTime);
        }
        else {
            double monthBill = blockBill(tariffName, tariffEMList, usage);
            
            // 월의 마지막 lp 데이타의 시간을 가져온다.
            lastLpTime = meter.getLastReadDate();
            // LP 날짜로 Billing Day를 생성하거나 업데이트한다.
            saveBillingBlockTariff(meter, usage, meter.getLastMeteringValue(), lastLpTime, monthBill, blockBillAccumulateBill);
            
            // 잔액을 차감한다.
            Contract contract = meter.getContract();
            log.info("contract_number[" + contract.getContractNumber() + 
                    "] MonthBill[" + monthBill + "] lastAccumulateBill[" + blockBillAccumulateBill+ "]");
            contract.setCurrentCredit(contract.getCurrentCredit() - (monthBill - blockBillAccumulateBill));
            contractDao.updateCurrentCredit(contract.getId(), contract.getCurrentCredit());
            
            // 선불로그 기록
            savePrepyamentLog(contract, usage-blockBillAccumulateUsage,
                    monthBill-blockBillAccumulateBill, lastLpTime);
        }
    }
    
    private void saveBillingBlockTariff(Meter meter, double lastAccumulateUsage, double activeEnergy, 
            String lastLpTime, double newbill, double oldbill) {
        BillingBlockTariff bill = new BillingBlockTariff();
        
        bill.setMDevId(meter.getMdsId());
        bill.setYyyymmdd(lastLpTime.substring(0, 8));
        bill.setHhmmss(lastLpTime.substring(8, 10)+"0000");
        bill.setMDevType(DeviceType.Meter.name());
        bill.setSupplier(meter.getSupplier());
        bill.setLocation(meter.getLocation());
        bill.setMeter(meter);
        bill.setModem((meter == null) ? null : meter.getModem());
        bill.setWriteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
        bill.setAccumulateUsage(lastAccumulateUsage);
        bill.setActiveEnergy(activeEnergy);
        if (meter.getContract() != null)
            bill.setContract(meter.getContract());
        
        //현재누적액 - 어제누적액
        bill.setBill(newbill - oldbill);
        bill.setAccumulateBill(newbill);
    
        log.info("MeterId[" + bill.getMDevId() + "] BillDay[" + bill.getYyyymmdd() + 
                "] BillTime[" + bill.getHhmmss() + "] AccumulateUsage[" + bill.getAccumulateUsage() +
                "] AccumulateBill[" + bill.getAccumulateBill() + "] CurrentBill[" + bill.getBill() + 
                "] ActiveEnergy[" + activeEnergy + "]");
        
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
    
    public double blockBill(String tariffName, List<TariffEM> tariffEMList, double usage) {
        double returnBill = 0.0;
        
        Collections.sort(tariffEMList, new Comparator<TariffEM>() {

            @Override
            public int compare(TariffEM t1, TariffEM t2) {
                return t1.getSupplySizeMin() < t2.getSupplySizeMin()? -1:1;
            }
        });
        
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
                    block = supplyMax - supplyMin;
                    
                    blockUsage = usage - supplyMax;
                    
                    if (blockUsage < 0) blockUsage = usage - supplyMin;
                    else blockUsage = block;
                } else {
                    blockUsage = usage - supplyMin;
                }
                
                returnBill = returnBill + (blockUsage * (tariffEMList.get(cnt).getActiveEnergyCharge() == null ? 0d : tariffEMList.get(cnt).getActiveEnergyCharge()));
                log.info("Block Usage[" + blockUsage + "]");
                log.info("ActiveEnergyCharge: " + tariffEMList.get(cnt).getActiveEnergyCharge());
                log.info("block bill[" + blockUsage * (tariffEMList.get(cnt).getActiveEnergyCharge() == null ? 0d : tariffEMList.get(cnt).getActiveEnergyCharge()) + "]");
            }
        }
        
        
        return returnBill;
    }
    
    private TariffEM getTariffByUsage(List<TariffEM> tariffList, Double usage) {
        TariffEM result = null;
        
        for ( TariffEM tariff : tariffList ) {
            Double min = tariff.getSupplySizeMin();
            Double max = tariff.getSupplySizeMax();
            
            if ( min == null && max == null ) {
                continue;
            }
            
            if (( min == null && usage <= max )|| (min == 0 && usage <=max)) {
                result =  tariff;
            } else if ( max == null && usage > min ) {
                result =  tariff;
            } else if ( usage > min && usage <= max ) {
                result =  tariff;
            } 
        }
        return result;
    }
    
    private void saveMonthBill(Contract contract, 
            double totalUsage,
            String yyyymm,
            String lastTokenDate,
            Operator operator) {
        
        Map<String ,Object> param = new HashMap<String, Object>();
        
        Customer customer = contract.getCustomer();
        
        // 이전 월정산을 롤백한다.
        // rollbackMonthlyAdjustedLog(contract, lastTokenDate);
        
        param.put("tariffIndex", contract.getTariffIndex());
        param.put("searchDate", yyyymm+"31");
        
        TariffType tariffType = tariffTypeDao.get(contract.getTariffIndexId());
        String tariffName = tariffType.getName();
        
        List<TariffEM> tariffList = tariffEMDao.getApplyedTariff(param);
        if ( tariffList == null || tariffList.size() < 1 ) {
            return;
        }
        
        Double totalAmount = 0d;
        
        TariffEM levyTariff = getTariffByUsage(tariffList, totalUsage);
        if (levyTariff == null) {
            log.info("skip \n" +
                    "totalUsage: " + totalUsage +
                    "tariffId: " + contract.getTariffIndexId());
        }
        Double govLevy = StringUtil.nullToDoubleZero(levyTariff.getTransmissionNetworkCharge()) * totalUsage;
        Double publicLevy = StringUtil.nullToDoubleZero(levyTariff.getDistributionNetworkCharge()) * totalUsage;
        totalAmount = blockBill(tariffName, tariffList, totalUsage);
        Double serviceCharge = StringUtil.nullToDoubleZero(levyTariff.getServiceCharge());
        Double vat = StringUtil.nullToDoubleZero(levyTariff.getEnergyDemandCharge()) * ( totalAmount + serviceCharge ); 
        Double govSubsidy = StringUtil.nullToDoubleZero(levyTariff.getAdminCharge()) * totalUsage;
        Double lifeLineSubsidy = StringUtil.nullToDoubleZero(levyTariff.getReactiveEnergyCharge());
        
        //사용량이 0이면 lifeLineSubsidy는 없다.
        if("Residential".equals(contract.getTariffIndex().getName()) && totalUsage == 0) {
            lifeLineSubsidy = 0d;
        }
        
        //사용량이 150이상이면 govSubsidy는 없다.
        if("Residential".equals(contract.getTariffIndex().getName()) && totalUsage > 150) {
            govSubsidy = 0d;
        }

        Double paidAmount = prepaymentLogDao.getMonthlyPaidAmount(contract, yyyymm);
        
        ECGBlockDailyEMBillingInfoSaveV2Task task = new ECGBlockDailyEMBillingInfoSaveV2Task();
        double[] blockNewSubsidy = task.blockNewSubsidy(contract.getTariffIndex().getName(), tariffList, totalUsage);
        
        /*
        Double vatOnSubsidyRate = StringUtil.nullToDoubleZero(levyTariff.getEnergyDemandCharge()) 
                * StringUtil.nullToDoubleZero(levyTariff.getRateRebalancingLevy());
        vatOnSubsidyRate = Double.parseDouble(String.format("%.4f", vatOnSubsidyRate));
        
        Double vatOnSubsidy = vatOnSubsidyRate * totalUsage;
        Double newSubsidyRate = (StringUtil.nullToDoubleZero(vatOnSubsidyRate) 
                + StringUtil.nullToDoubleZero(levyTariff.getRateRebalancingLevy()));
        Double newSubsidy = newSubsidyRate * totalUsage;
        */
        double newSubsidy = blockNewSubsidy[0];
        double vatOnSubsidy = blockNewSubsidy[1];
        
        Double additionalAmount = totalAmount - paidAmount;
        
        // subsidy는 모든 subsidy의 합으로 계산한다.
        Double subsidy = govSubsidy + lifeLineSubsidy + newSubsidy;
        Double levy = publicLevy + govLevy;
        
        // 2015.04.08 월정산중에 충전을 시도하면 충전금액이 반영되지 않을 수 있다.
        // 최근 잔액을 가져오도록 한다.
        contract = contractDao.get(contract.getId());
        
        Double beforeCredit = StringUtil.nullToDoubleZero(contract.getCurrentCredit());
        Double currentCredit =  beforeCredit - additionalAmount - serviceCharge - levy - vat + subsidy;     
        
        log.debug("\n=== Contract Number: " + contract.getContractNumber() + " ==="
        + "\n Before Credit: " + StringUtil.nullToDoubleZero(contract.getCurrentCredit())
        + "\n After Credit: " + currentCredit
        + "\n Total Usage: " + totalUsage
        + "\n Total Amount: " + totalAmount
        + "\n Paid Amount: " + paidAmount
        + "\n Additional Amount: " + additionalAmount
        + "\n Service Charge Amount: " + serviceCharge
        + "\n Public Lavy: " + publicLevy
        + "\n Gov. Levy: " + govLevy
        + "\n VAT: " + vat
        // + "\n vatOnSubsidyRate: " + vatOnSubsidyRate
        + "\n vatOnSubsidy: " + vatOnSubsidy
        + "\n LifeLine Subsidy: " + lifeLineSubsidy
        + "\n Subsidy: " + govSubsidy
        // + "\n new Subsidy rate: " + newSubsidyRate
        + "\n new Subsidy: " + newSubsidy);
        
        contract.setCurrentCredit(currentCredit);
        contractDao.update(contract);
        
        PrepaymentLog prepaymentLog = new PrepaymentLog();
        prepaymentLog.setLastTokenDate(lastTokenDate);
        prepaymentLog.setCustomer(customer);
        prepaymentLog.setContract(contract);            
        prepaymentLog.setPreBalance(beforeCredit);
        prepaymentLog.setBalance(currentCredit);
        prepaymentLog.setMonthlyTotalAmount(totalAmount);
        prepaymentLog.setMonthlyPaidAmount(paidAmount);
        prepaymentLog.setMonthlyServiceCharge(serviceCharge);
        prepaymentLog.setUsedConsumption(totalUsage);
        prepaymentLog.setUsedCost(additionalAmount);
        prepaymentLog.setPublicLevy(publicLevy);
        prepaymentLog.setGovLevy(govLevy);
        prepaymentLog.setVat(vat);
        prepaymentLog.setVatOnSubsidy(vatOnSubsidy);
        prepaymentLog.setLifeLineSubsidy(lifeLineSubsidy);
        prepaymentLog.setSubsidy(govSubsidy);
        prepaymentLog.setAdditionalSubsidy(newSubsidy);
        prepaymentLog.setOperator(operator);
        prepaymentLog.setLocation(contract.getLocation());
        prepaymentLog.setTariffIndex(contract.getTariffIndex());
        prepaymentLogDao.add(prepaymentLog);
        log.info(prepaymentLog);
    }
}