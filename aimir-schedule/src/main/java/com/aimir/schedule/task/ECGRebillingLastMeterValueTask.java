package com.aimir.schedule.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
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
import com.aimir.model.system.PrepaymentLog;
import com.aimir.model.system.TariffEM;
import com.aimir.model.system.TariffType;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;
import com.aimir.util.DateTimeUtil;

/**
 * ECG의 지침값을 가지고 선불요금을 계산하기 이전에
 * 모든 고객에 대해서 정산을 한번 실행해야 한다.
 * 
 * 현재 고객 중 미터 교체나 고객이 변경된 경우를 제외하고
 * 모든 고객에 대해서 마지막 지침값과 선불 요금에서 사용한 총 사용량을
 * 사용하여 차이를 구하고 차이만큼 정산하도록 한다.
 * 
 * 이때, 차이가 월평균과 비교하여 넘어가면 월정산하고 나머지는 선불 요금 방식으로 계산한다.
 * 
 * @author elevas
 * @since 2015.10.17
 */
@Service
public class ECGRebillingLastMeterValueTask {
    
    protected static Log log = LogFactory.getLog(ECGRebillingLastMeterValueTask.class);
    private static final String SERVICE_TYPE_EM = "Electricity";

    @Resource(name="transactionManager")
    HibernateTransactionManager txmanager;
    
    @Resource(name="dataSource")
    DataSource ds;
    
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
        
        ECGRebillingLastMeterValueTask task = ctx.getBean(ECGRebillingLastMeterValueTask.class);
        task.execute();
        System.exit(0);
    }

    
    public void execute() {
        log.info("########### START ECGRebillingLastMeterValue ###############");
        List<Integer> contracts = getContractWithOneMeteringData();
        LpEM[] lpems = null;
        for (int contractId : contracts) {
            // contractId가 특정 지역에 속하는지 검사한다.
            // 특정 지역만 적용
            
            lpems = getLastLp(contractId);
            
            if (lpems != null) {
                try {
                    double avg = avgMonth(contractId, lpems);
                    double usedConsumption = getUsedConsumption(contractId);
                    
                    double diffUsage = lpems[0].getValue() + lpems[1].getValue() - usedConsumption;
                    
                    while (true) {
                        diffUsage -= avg;
                        
                        if (diffUsage - avg >= 0) {
                            // avg로 월정산
                            diffUsage -= avg;
                        }
                        else {
                            // diffUsage로 선불요금계산 
                            break;
                        }
                    }
                }
                catch (ParseException e) {
                    log.error(e, e);
                }
            }
        }
        log.info("########### END ECGRebillingLastMeterValue ############");
    }//execute end
    
    
    
    /*
     * 마지막 지침값과 미터 설치일자를 가지고 월평균값을 구한다.
     */
    public double avgMonth(int contractId, LpEM[] lpems)
            throws ParseException 
    {
        TransactionStatus txstatus = null;
        try {
            txstatus = txmanager.getTransaction(null);
            Contract contract = contractDao.get(contractId);
            Meter meter = contract.getMeter();
            txmanager.commit(txstatus);
            
            if (meter != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                Calendar cal = Calendar.getInstance();
                cal.setTime(sdf.parse(meter.getInstallDate().substring(0, 8)));
                String lastLpDate = lpems[0].getYyyymmdd();
                int days = 1;
                while (true) {
                    if (lastLpDate.equals(sdf.format(cal.getTime()))) break;
                    days++;
                    cal.add(Calendar.DAY_OF_MONTH, 1);
                }
                
                return (lpems[0].getValue() + lpems[1].getValue()) / (double)days * 30; 
            }
        }
        catch (Exception e) {
            if (txstatus != null && !txstatus.isCompleted()) txmanager.rollback(txstatus);
        }
        
        return lpems[0].getValue() + lpems[1].getValue();
    }
    /*
     * 선불요금에서 사용한 총 사용량을 구한다.
     */
    public double getUsedConsumption(int contractId) {
        String query = "select sum(used_consumption) from prepaymentlog where contract_id=? and monthlyTotalAmount is null";
        
        TransactionStatus txstatus = null;
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            txstatus = txmanager.getTransaction(null);
            con = ds.getConnection();
            stmt = con.prepareStatement(query);
            stmt.setInt(1, contractId);
            rs = stmt.executeQuery(query);
            
            if (rs.next()) {
                return rs.getDouble(1);
            }
            
            txmanager.commit(txstatus);
            return 0.0;
        }
        catch (Exception e) {
            log.error(e, e);
            if (txstatus != null) txmanager.rollback(txstatus);
        }
        finally {
            try {
                if (rs != null) rs.close();
            }
            catch (Exception e) {}
            try {
                if (stmt != null) stmt.close();
            }
            catch (Exception e) {}
            try {
                if (con != null) con.close();
            }
            catch (Exception e) {}
        }
        return 0.0;
    }
    
    /*
     * 마지막 지침값을 가져온다.
     */
    public LpEM[] getLastLp(int contractId) {
        TransactionStatus txstatus = null;
        LpEM[] lpems = null;
        try {
            txstatus = txmanager.getTransaction(null);
            Contract contract = contractDao.get(contractId);
            Meter meter = contract.getMeter();
            
            if (meter != null) {
                ECGBlockDailyEMBillingInfoSaveV4Task task = new ECGBlockDailyEMBillingInfoSaveV4Task();
                lpems = task.getLastLp(meter.getMdsId(), null, meter.getInstallDate());
            }
            txmanager.commit(txstatus);
        }
        catch (Exception e) {
            if (txstatus != null) txmanager.rollback(txstatus);
        }
        
        return lpems;
    }
    
    /*
     * 미터 교체나 고객 교체가 된 적이 없는 계약 정보를 가져온다.
     */
    public List<Integer> getContractWithOneMeteringData() {
        String query = "select contract_id from (select contract_id, count(mdev_id) cnt from "
                       + "(select contract_id, mdev_id "
                       + "from day_em where mdev_type = 'Meter' and dst=0 and channel = 1 "
                       + "group by contract_id, mdev_id "
                       + ") group by contract_id ) where cnt = 1";
        
        TransactionStatus txstatus = null;
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            txstatus = txmanager.getTransaction(null);
            con = ds.getConnection();
            stmt = con.createStatement();
            rs = stmt.executeQuery(query);
            
            List<Integer> list = new ArrayList<Integer>();
            if (rs.next()) {
                list.add(rs.getInt("contract_id"));
            }
            
            txmanager.commit(txstatus);
            return list;
        }
        catch (Exception e) {
            log.error(e, e);
            if (txstatus != null) txmanager.rollback(txstatus);
        }
        finally {
            try {
                if (rs != null) rs.close();
            }
            catch (Exception e) {}
            try {
                if (stmt != null) stmt.close();
            }
            catch (Exception e) {}
            try {
                if (con != null) con.close();
            }
            catch (Exception e) {}
        }
        return null;
    }
    
    public void saveEmBillingDayInfo() {
         
        // 전기 계약 정보 취득
        List<Integer> em_contractIds = this.getContractInfos(SERVICE_TYPE_EM); 

        TransactionStatus txStatus = null;
        
        for(Integer contract_id : em_contractIds) {
            // log.info("ContractId[" + contract_id + "]");
            
            txStatus = txmanager.getTransaction(null);
            try {
                Contract contract = contractDao.get(contract_id);
                Code code = contract.getCreditType();
                if (contract.getTariffIndexId() != null && contract.getCustomer() != null && contract.getMeter() != null) {
                    if(Code.PREPAYMENT.equals(code.getCode()) || Code.EMERGENCY_CREDIT.equals(code.getCode())) { // 선불 요금일 경우
                        log.info("Contract[" + contract.getContractNumber() + "] Meter[" + contract.getMeter().getMdsId() + "]");
                        this.saveEmBillingDailyWithTariffEMCumulationCost(contract);
                    }
                }
                // txmanager.commit(txStatus);
                txmanager.rollback(txStatus);
            }
            catch (Exception e) {
                log.error(e, e);
                txmanager.rollback(txStatus);
            }
        }
        // setSuccessResult();
    }

    private List<Integer> getContractInfos(String serviceType){
        return contractDao.getPrepaymentContract(serviceType);
    }
    
    public void saveEmBillingDailyWithTariffEMCumulationCost(Contract contract)
            throws Exception
    {
        if (contract.getMeter() != null) {
            // 마지막 activenergy 값을 가져온다.
            // 검침된 적이 없으면 널값이 온다.
            LpEM[] lastLp = getLastLp(contract.getMeter().getMdsId(), null);
            if (lastLp != null && lastLp.length == 2) {
                BillingBlockTariff prevBill = getLastBillingBlockTariff(contract.getMeter().getMdsId(), lastLp);
                saveBill(contract, lastLp, prevBill);
            }
        }
    }
    
    /**
     * 계약일이 널이 아니면 이전 LP 중 제일 마지막 LP를 가져온다.
     * 계약일이 널이면 제일 큰 값으로 가져온다.
     * @param meterId
     * @return
     */
    public LpEM[] getLastLp(String meterId, String yyyymmddhh) {
        TransactionStatus txstatus = txmanager.getTransaction(null);
        try {
            Set<Condition> conditions = new HashSet<Condition>();
            conditions.add(new Condition("id.mdevType", new Object[]{DeviceType.Meter}, null, Restriction.EQ));
            conditions.add(new Condition("id.mdevId", new Object[]{meterId}, null, Restriction.EQ));
            conditions.add(new Condition("id.channel", new Object[]{KamstrupChannel.ActiveEnergyImp.getChannel()},
                    null, Restriction.EQ));
            conditions.add(new Condition("id.dst", new Object[]{0}, null, Restriction.EQ));
            
            if (yyyymmddhh != null && !"".equals(yyyymmddhh))
                conditions.add(new Condition("id.yyyymmddhh", new Object[]{yyyymmddhh}, null, Restriction.LE));
            
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
        }
        catch (Exception e) {
            if (txstatus != null) txmanager.rollback(txstatus);
        }
        finally {
            if (txstatus != null) txmanager.commit(txstatus);
        }
        return null;
    }
    
    /**
     * 고객의 계약일 기준으로 날짜가 크거나 또는 계약 관계가 널인 것인 최초의 LP를 가져온다.
     * @param meterId
     * @return
     */
    public LpEM[] getFirstLp(String meterId, String contractNumber, String contractDate) {
        Set<Condition> conditions = new HashSet<Condition>();
        
        // 계약정보가 널인 것으로 먼저 찾고
        conditions.add(new Condition("id.mdevType", new Object[]{DeviceType.Meter}, null, Restriction.EQ));
        conditions.add(new Condition("id.mdevId", new Object[]{meterId}, null, Restriction.EQ));
        conditions.add(new Condition("id.channel", new Object[]{KamstrupChannel.ActiveEnergyImp.getChannel()},
                null, Restriction.EQ));
        conditions.add(new Condition("id.dst", new Object[]{0}, null, Restriction.EQ));
        conditions.add(new Condition("id.yyyymmddhh", new Object[]{contractDate.substring(0, 10)}, null, Restriction.GE));
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
        conditions.add(new Condition("id.yyyymmddhh", new Object[]{contractDate.substring(0, 10)}, null, Restriction.GE));
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
            
            String contractDate = contract.getContractDate();
            
            BillingBlockTariff bbt = new BillingBlockTariff();
            bbt.setMDevType("Meter");
            bbt.setMDevId(meterId);
            bbt.setYyyymmdd(contractDate.substring(0, 8));
            bbt.setHhmmss(contractDate.substring(8, 10) + "0000");
            bbt.setAccumulateBill(0.0);
            bbt.setAccumulateUsage(0.0);
            
            if (contractDate != null && !"".equals(contractDate)) {
                // 계약일보다 작은 LP time 중 마지막 LP를 가져온다.
                LpEM[] lps = getLastLp(meterId, contractDate.substring(0, 10));
                
                if (lps != null && lps.length == 2) {
                    double activeEnergy = lps[0].getValue() + lps[1].getValue();
                    // lp 날짜와 계약일자의 차이가 발생하면 일평균을 구하여 차이만큼을 누적치에서 더한다.
                    // contractDate가 무조건 더 크다.
                    if (contractDate.substring(0, 8).compareTo(lps[0].getYyyymmdd()) == 0) {
                        bbt.setYyyymmdd(lps[0].getYyyymmdd());
                        bbt.setHhmmss(lps[0].getYyyymmddhh().substring(8, 10) + "0000");
                        bbt.setActiveEnergy(activeEnergy);
                    }
                    // contractDate와 lp의 yyyymmdd의 차이 일수만큼 activeEnergy 값을 만든다. 
                    else {
                        double lastActiveEnergy = lastLp[0].getValue() + lastLp[1].getValue() -
                                lps[0].getValue() - lps[1].getValue();
                        double usageFromLpDateContractDate = calUsageFromLpDateContractDate(lastActiveEnergy, 
                                lps[0].getYyyymmddhh(), lastLp[0].getYyyymmddhh(), contractDate);
                        activeEnergy += usageFromLpDateContractDate;
                        bbt.setActiveEnergy(activeEnergy);
                    }
                }
                // 고객의 계약일보다 작은 마지막 lp가 없으면 계약일보다 큰 최초값을 가져온다.
                else {
                    // LP는 있는데 고객이 다르면 미터와 고객이 매핑된 첫번재 값을 가져온다.
                    LpEM[] firstLps = getFirstLp(meterId, contract.getContractNumber(), contract.getContractDate());
                 
                    // 평균값을 빼서 계약일 기준값으로 만든다.
                    double lastActiveEnergy = lastLp[0].getValue() + lastLp[1].getValueCnt() -
                            firstLps[0].getValue() - firstLps[1].getValue();
                    double usageContractFromLpDate = 
                            calContractDateFromLpDate(lastActiveEnergy, firstLps[0].getYyyymmddhh(),
                                    lastLp[0].getYyyymmddhh(), contractDate);
                    bbt.setActiveEnergy(firstLps[0].getValue() + firstLps[1].getValue() - usageContractFromLpDate);
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
    
    public void saveBill(Contract contract, LpEM[] lp, BillingBlockTariff prevBill) 
    throws Exception
    {
        // 날짜 비교
        String lptime = lp[0].getYyyymmddhh();
        String prevBillTime = prevBill.getYyyymmdd() + prevBill.getHhmmss().substring(0, 2);
        
        // 마지막 LP시간이 이미 계산한 것이라면 종료한다.
        if (lptime.equals(prevBillTime))
            return;
        else {
            String lp_yyyymm = lptime.substring(0, 6);
            String prevBillTime_yyyymm = prevBillTime.substring(0, 6);
            
            // 두 개의 yyyymm을 비교허여 같으면 lp의 누적치에서 prevBill의 누적치의 차를 이용하여 계산한다.
            if (lp_yyyymm.equals(prevBillTime_yyyymm)) {
                savePrebill(contract.getTariffIndex().getName(),
                        contract.getMeter(), lptime, prevBill.getAccumulateBill(),
                        prevBill.getAccumulateUsage(), prevBill.getActiveEnergy(),
                        lp[0].getValue()+lp[1].getValue(), getTariff(contract.getTariffIndexId(),
                                lptime.substring(0, 8)));
            }
            else {
                // 지침값이 여러달인 경우 총 사용량을 이용하여 월 사용량을 구한다.
                // 월 사용량은 월정산으로 계산하고 마지막 말은 선불요금으로 계산한다.
                double totalUsage = lp[0].getValue()+lp[1].getValue() - prevBill.getActiveEnergy();
                double usageFromLpDateContractDate = 
                        calUsageFromLpDateContractDate(totalUsage, prevBillTime, lptime, contract.getContractDate()); 
                
                // 이전에 정산한 누적치가 한번도 없으면 0이다.
                double lastActiveEnergy = prevBill.getActiveEnergy();
                
                lastActiveEnergy += usageFromLpDateContractDate;
                // 마지막 달에 대해서만 선불요금을 실행하고 나머지는 월정산 실행
                // 마지막 사용량은 선불요금계산
                // 마지막 달에 대해서는 요금계산이 실행된 적이 없기 때문에 lastAccumulateUsage와 lastAccumulateBill이 0이다.
                savePrebill(contract.getTariffIndex().getName(),
                        contract.getMeter(), lptime, 0, 0, lastActiveEnergy,
                        lp[0].getValue()+lp[1].getValue(), getTariff(contract.getTariffIndexId(),
                                lptime.substring(0, 8)));
            }
        }
    }
    
    /*
     * 시작 년월부터 마지막 LP의 년월까지의 일수를 계산하여 LP 전월까지의 월 평균 사용량을 계산하고
     * LP 시작부터 계약일까지의 사용량을 구한다.
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

            switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                days = 31;
                break;
            case 2:
                if (year % 4 == 0) days = 29;
                else days = 28;
                break;
            default : days = 30;
            }
            
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
    private double calContractDateFromLpDate(double usage, String fromLpDate, String toLpDate, String contractDate)
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
        cal.setTime(sdf.parse(contractDate.substring(0, 6)));
        while (true) {
            year = cal.get(Calendar.YEAR);
            month = cal.get(Calendar.MONTH) + 1;
            yearmonth = String.format("%04d%02d", year, month);
            
            switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                days = 31;
                break;
            case 2:
                if (year % 4 == 0) days = 29;
                else days = 28;
                break;
            default : days = 30;
            }
            
            // 계약일과 lp 시작일이 같은 년월이면 기간만큼의 사용량을 구한다.
            if (yearmonth.equals(contractDate.substring(0, 6)) 
                    && yearmonth.equals(fromLpDate.substring(0, 6))) {
                usageContractDateFromLpDate += ((days - Integer.parseInt(contractDate.substring(6,8))) * avg);
                usageContractDateFromLpDate -= ((days - Integer.parseInt(fromLpDate.substring(6,8))) * avg);
                break;
            }
            // 계약일과만 같으면 말일에서 계약일자만큼의 일수에 대한 사용량을 더한다.
            else if (yearmonth.equals(contractDate.substring(0, 6))) {
                usageContractDateFromLpDate += ((days - Integer.parseInt(contractDate.substring(6,8))) * avg);
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

            switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                days = 31;
                break;
            case 2:
                if (year % 4 == 0) days = 29;
                else days = 28;
                break;
            default : days = 30;
            }
            
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
    private void savePrebill(String tariffName, Meter meter, String lastLpTime, double lastAccumulateBill,
            double lastAccumulateUsage, double lastActiveEnergy, double lpActiveEnergy, List<TariffEM> tariffEMList)
    throws Exception
    {
    	log.info("####savePrebill####");
    	log.info("MeterId[" + meter.getMdsId() + "] LAST_LPTIME[" + lastLpTime + "]");
    	
        // 모뎀이 ZRU 유형은 기존 방식대로 월 사용량을 기준으로 선불 계산한다.
        if (meter.getModem() != null && meter.getModem().getModemType() == ModemType.ZRU) {
            // 월 사용량
            double usage = lpActiveEnergy - lastActiveEnergy + lastAccumulateUsage;
            
            // 월 선불금액 계산
            double monthBill = blockBill(tariffName, tariffEMList, usage);
            
            // LP 날짜로 Billing Day를 생성하거나 업데이트한다.
            saveBillingBlockTariff(meter, usage, lpActiveEnergy, lastLpTime, monthBill, lastAccumulateBill);
            
            // 잔액을 차감한다.
            Contract contract = meter.getContract();
            log.info("contract_number[" + contract.getContractNumber() + 
                    "] MonthBill[" + monthBill + "] lastAccumulateBill[" + lastAccumulateBill+ "]");
            contract.setCurrentCredit(contract.getCurrentCredit() - (monthBill - lastAccumulateBill));
            contractDao.updateCurrentCredit(contract.getId(), contract.getCurrentCredit());
            
            // 선불로그 기록
            savePrepyamentLog(contract, lpActiveEnergy - lastActiveEnergy,
                    monthBill-lastAccumulateBill, lastLpTime);
        }
        else {
            // 월 선불금액 계산
            double usage = meter.getLastMeteringValue() - lastActiveEnergy + lastAccumulateUsage;
            double monthBill = blockBill(tariffName, tariffEMList, usage);
            
            // 월의 마지막 lp 데이타의 시간을 가져온다.
            lastLpTime = meter.getLastReadDate();
            // LP 날짜로 Billing Day를 생성하거나 업데이트한다.
            saveBillingBlockTariff(meter, usage, meter.getLastMeteringValue(), lastLpTime, monthBill, lastAccumulateBill);
            
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
    
    private void saveBillingBlockTariff(Meter meter, double lastAccumulateUsage, double activeEnergy, 
            String lastLpTime, double newbill, double oldbill) {
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
        ECGBlockDailyEMBillingInfoSaveV2Task task = new ECGBlockDailyEMBillingInfoSaveV2Task();
        return task.blockBill(tariffName, tariffEMList, usage);
    }
}