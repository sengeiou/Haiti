package com.aimir.schedule.task;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.mvm.BillingBlockTariffDao;
import com.aimir.dao.mvm.DayEMDao;
import com.aimir.dao.mvm.LpEMDao;
import com.aimir.dao.mvm.MeteringDataEMDao;
import com.aimir.dao.mvm.MonthEMDao;
import com.aimir.dao.mvm.impl.DayEMDaoImpl;
import com.aimir.dao.mvm.impl.MeteringDataEMDaoImpl;
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
import com.aimir.model.mvm.DayEM;
import com.aimir.model.mvm.LpEM;
import com.aimir.model.mvm.MeteringDataEM;
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
 * EDH의 Block Tariff 선불 요금을 계산하는 스케줄
 * Block이 월단위이므로 월사용량을 기준으로 요금을 계산해야 한다.
 *
 * @author elevas
 *
 */
@Service
public class EDHBlockDailyEMBillingInfoSaveV2Task extends ScheduleTask {

    protected static Log log = LogFactory.getLog(EDHBlockDailyEMBillingInfoSaveV2Task.class);
    private static final String SERVICE_TYPE_EM = "Electricity";
    private static final long BILLING_STANDARDS_DATE = 10;
    private static final String METERING_MULTIPLE_NUMBER = "3";

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
    
    @Autowired
    MeteringDataEMDao meteringDataEMDao;

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
    
    public static void main(String[] args) {
    	try {
        	ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{"spring-BlockDailyEMBillingTask.xml"}); 
            DataUtil.setApplicationContext(ctx);
            
            log.info("########### START EDHBlockDailyEMBillingInfoSaveV2Task ###############");
            
            EDHBlockDailyEMBillingInfoSaveV2Task task = ctx.getBean(EDHBlockDailyEMBillingInfoSaveV2Task.class);
            task.execute(null);
			
		} catch (Exception e) {
			log.error("EDHBlockDailyEMBillingInfoSaveV2Task excute error - " + e, e);
		} finally {
			log.info("#### EDHBlockDailyEMBillingInfoSaveV2Task finished. ####");
			System.exit(0);
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
        this.saveEMBillingDayInfo();

        log.info("########### END EDHBlockDailyEMBillingInfo ############");
        isNowRunning = false;
    }//execute end

    public void saveEMBillingDayInfo() {
    	TransactionStatus txStatus = txManager.getTransaction(null);

    	// Electricity 계약 목록 조회
    	List<Contract> EM_ContractList = contractDao.getContract(Code.PREPAYMENT, SERVICE_TYPE_EM);
    	
    	for (int i = 0; i < EM_ContractList.size(); i++) {
    		Contract contract = EM_ContractList.get(i);
    		
    		try {
                if (contract.getTariffIndexId() != null && contract.getCustomer() != null && contract.getMeter() != null) {
                    log.info("Contract[" + contract.getContractNumber() + "] Meter[" + contract.getMeter().getMdsId() + "]");
                    this.saveEmBillingDailyWithTariffEMCumulationCost(contract);
                }
                txManager.commit(txStatus);
            }
            catch (Exception e) {
                log.error("saveEmBillingDayInfo Exception ==> Contract number = " + contract.getContractNumber(), e);
                txManager.rollback(txStatus);
            }
		}
    }

    private void saveEmBillingDailyWithTariffEMCumulationCost(Contract contract) throws Exception {
        SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
        String currentDate = sd.format(new Date());
        String mdsId = meterDao.get(contract.getMeterId()).getMdsId();
        DayEM lastDayEM = null;
        String MaxYyyymmdd = null;
        boolean FirstDeal = false;

        //마지막 누적요금이 저장된 데이터를 가지고 온다.
        List<Map<String, Object>> lastBilling = billingBlockTariffDao.getLastAccumulateBill(mdsId);
        //마지막 누적요금 저장
        BigDecimal lastAccumulateBill = new BigDecimal(0);
        //마지막 누적사용량 저장
        BigDecimal lastAccumulateUsage = new BigDecimal(0);
        //마지막 누적요금이 저장된 날짜
        String lastAccumulateDate = null;
        
        if(lastBilling.size() == 0) {
        	// 마지막 누적 요금이 저장된 billingDayEM이 없을 경우 한번도 선불스케줄을 돌리지 않은것으로 간주
        	// 한달 전 선불부터 시작한다.
        	lastAccumulateDate = TimeUtil.getPreMonth(currentDate, 1).substring(0 ,8);
        	FirstDeal = true;
        }else {
        	lastAccumulateBill = convertBigDecimal(lastBilling.get(0).get("ACCUMULATEBILL"));
    		lastAccumulateUsage = convertBigDecimal(lastBilling.get(0).get("ACCUMULATEUSAGE"));
//    		lastAccumulateDate = lastBilling.get(0).get("YYYYMMDD").toString() + lastBilling.get(0).get("HHMMSS").toString();
    		lastAccumulateDate = lastBilling.get(0).get("YYYYMMDD").toString();
        }
        log.info("1. Contract[" + contract.getContractNumber() + "] "
    			+ " lastAccumulateBill[" + lastAccumulateBill + "] "
    			+ " lastAccumulateUsage[" + lastAccumulateUsage + "] "
    			+ " lastAccumulateDate[" + lastAccumulateDate + "] "
    			+ " mdsId[" + mdsId + "] ");
        
        lastDayEM = getDayEM(mdsId, lastAccumulateDate);	//최근 yyyymmdd에 해당하는 DAY_EM 조회
        MaxYyyymmdd = lastDayEM.getYyyymmdd(); 				//DAY_EM 검침테이블에서 마지막 날짜를 가져온다.
        
    	// 마지막 시간이 현재 시간보다 작으면 월 기준으로 선불 계산한다.
    	// compareTo - lastAccumulateDate가 MaxYyyymmdd보다 작으면 음수 반환, 크면 양수 반환, 같으면 0 리턴
    	if (lastAccumulateDate.compareTo(MaxYyyymmdd) < 0) {
    		// date1, date2 두 날짜를 parse()를 통해 Date형으로 변환
    		Date FirstDate = sd.parse(MaxYyyymmdd);
    		Date SecondDate = sd.parse(lastAccumulateDate);
    		long calDateDays = Math.abs(FirstDate.getTime() - SecondDate.getTime());
    		// currentDate와 lastAccumulateDate간의 시간차이를 비교하여 일수로 반환
    		long diffDays = TimeUnit.DAYS.convert(calDateDays, TimeUnit.MILLISECONDS);
    		System.out.println(String.format("A %s , B %s Diff %s Days", MaxYyyymmdd, lastAccumulateDate, diffDays));

			//해당 Tariff 정보를 가져온다.
			TariffType tariffType = tariffTypeDao.get(contract.getTariffIndexId());
			Integer tariffTypeCode = tariffType.getCode();
			
			Map<String, Object> tariffParam = new HashMap<>();
			tariffParam.put("tariffTypeCode", tariffTypeCode);
			tariffParam.put("tariffIndex", tariffType);
			tariffParam.put("searchDate", lastAccumulateDate.substring(0 ,6)+"31");
			List<TariffEM> tariffEMList = tariffEMDao.getApplyedTariff(tariffParam);
			
			if(diffDays < BILLING_STANDARDS_DATE || FirstDeal) {
				savePrebill(contract, lastDayEM, lastAccumulateBill, lastAccumulateUsage, tariffEMList);					// 일반 정산
        	}else {
        		saveIntervalPrebill(contract, lastDayEM, lastAccumulateBill, lastAccumulateUsage, tariffEMList, diffDays);	// 구간 정산
        	}
				
    	}
    }

    // yyyymm의 월 사용량을 가져와 선불을 계산하고 마지막 선불요금을 뺀다.
    private void savePrebill(Contract contract, DayEM lastDayEM, BigDecimal lastAccumulateBill, BigDecimal lastAccumulateUsage, List<TariffEM> tariffEMList) throws Exception
    {
    	log.info("2. ####savePrebill####  MeterId[" + contract.getMeter().getMdsId() + "] yyyymmdd[" + lastDayEM.getYyyymmdd() + "]");

        //마지막 누적요금이 계산된 달부터 오늘까지의 MonthEM
        List<Integer> channelList = new ArrayList<Integer>();
        channelList.add(I210Channel.ActiveEnergyImp.getChannel());
        // channelList.add(I210Channel.ActiveEnergyExp.getChannel());
        Meter meter = meterDao.findByCondition("id", contract.getMeterId());
        
/*        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("mdevType", DeviceType.Meter);
        condition.put("mdevId", meter.getMdsId());
        condition.put("channelList", channelList);
        condition.put("yyyymm", yyyymmdd.substring(0 ,6));
        condition.put("dst", 0);

        List<MonthEMView> monthEMs = monthEMViewDao.getMonthEMsByCondition(condition); // 수정 필요 DAY_EM으로
*/        
        log.info("3. MeterId[" + meter.getMdsId() + "] yyyymmdd[" + lastDayEM.getYyyymmdd() + "]");
        if (lastDayEM != null) {
        	
//        	MonthEMView monthEM = monthEMs.get(0);

        	BigDecimal monthBill = new BigDecimal(0);
        	BigDecimal totalUsage = convertBigDecimal(lastDayEM.getValue());
        	
            // 월 선불금액 계산
            // 지침사용량이 누적사용량의 기준 배수(3배)이상이 아니고 누적사용량보다 클때
            if(lastAccumulateUsage.multiply(new BigDecimal(METERING_MULTIPLE_NUMBER)).compareTo(totalUsage) >= 0 || lastAccumulateUsage.compareTo(totalUsage) < 0) {
            	monthBill = blockBill(meter.getMdsId(), contract.getTariffIndex().getName(), tariffEMList, totalUsage);
            }else {
            	//BILLING_BLOCK_TARIFF_WRONG 이력 남기고 리턴하여 for문으로 다시 돌아감
            	
            }

            // 월의 마지막 lp 데이타의 시간을 가져온다.
//            String lastLpTime = getLastLpTime(meter.getMdsId(), lastDayEM);
            String lastLpTime = lastDayEM.getYyyymmdd()+"000000";
            // LP 날짜로 Billing Day를 생성하거나 업데이트한다.
            saveBillingBlockTariff(meter, totalUsage, lastLpTime, monthBill, lastAccumulateBill);

            // Contract 잔액을 차감한다.
            contract.setCurrentCredit((contract.getCurrentCredit()==null? 0:contract.getCurrentCredit()) - (monthBill.subtract(lastAccumulateBill).doubleValue()));
            contractDao.update(contract);
            log.info("7. [Update CurrentCredit Contract:"+ contract.getContractNumber() + " ] MeterId[" + meter.getMdsId() + "] yyyymmdd[" + lastDayEM.getYyyymmdd() + "] "
            		+ "==> MonthBill[" + monthBill + "] lastAccumulateBill[" + lastAccumulateBill+ "]");
        }
    }
    
    private void saveIntervalPrebill(Contract contract, DayEM lastDayEM, BigDecimal lastAccumulateBill, BigDecimal lastAccumulateUsage, List<TariffEM> tariffEMList, long diffDays) throws Exception
    {
    	log.info("2. ####savePrebill####  MeterId[" + contract.getMeter().getMdsId() + "] yyyymmdd[" + lastDayEM.getYyyymmdd() + "]");

        //마지막 누적요금이 계산된 달부터 오늘까지의 MonthEM
        List<Integer> channelList = new ArrayList<Integer>();
        channelList.add(I210Channel.ActiveEnergyImp.getChannel());
        // channelList.add(I210Channel.ActiveEnergyExp.getChannel());
        Meter meter = meterDao.findByCondition("id", contract.getMeterId());
        
/*        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("mdevType", DeviceType.Meter);
        condition.put("mdevId", meter.getMdsId());
        condition.put("channelList", channelList);
        condition.put("yyyymm", yyyymmdd.substring(0 ,6));
        condition.put("dst", 0);

        List<MonthEMView> monthEMs = monthEMViewDao.getMonthEMsByCondition(condition);*/
        
        log.info("3. MeterId[" + meter.getMdsId() + "] yyyymmdd[" + lastDayEM.getYyyymmdd() + "]");
        if (lastDayEM != null) {
        	
//        	MonthEMView monthEM = monthEMs.get(0);

        	BigDecimal blockBill = new BigDecimal(0);
        	BigDecimal lastUsage = convertBigDecimal(lastDayEM.getValue());        	
    		int intervalDays = (int) Math.ceil(diffDays / BILLING_STANDARDS_DATE);
    		
    		BigDecimal avgDayUsage = new BigDecimal(0); 
    		BigDecimal intervalUsage = new BigDecimal(0);
    		BigDecimal totalUsage = new BigDecimal(0);
    		
    		if(lastAccumulateUsage.compareTo(BigDecimal.ZERO) == 1) {	// lastAccumulateUsage 값이 0보다 클 경우
    			avgDayUsage = lastUsage.subtract(lastAccumulateUsage).divide(new BigDecimal(String.valueOf(diffDays)));		// 구간 일평균 사용량
    		}
    		
    		for(int j=0; j < intervalDays; j++) {
    			//마지막 누적요금, 누적 사용량이 저장된 데이터를 가지고 온다.
    	        List<Map<String, Object>> lastBilling = billingBlockTariffDao.getLastAccumulateBill(meter.getMdsId());
    	        lastAccumulateBill = (BigDecimal) lastBilling.get(0).get("ACCUMULATEBILL");
        		lastAccumulateUsage = (BigDecimal) lastBilling.get(0).get("ACCUMULATEUSAGE");
    	        
        		if(diffDays % BILLING_STANDARDS_DATE == 0) {
       			 	intervalUsage = avgDayUsage.multiply(new BigDecimal(Long.toString(BILLING_STANDARDS_DATE)));					// 구간 사용량
       			 	totalUsage = intervalUsage.add(lastAccumulateUsage);
	       		}else {
	       			
	       			if(j == intervalDays-1) {
	       				totalUsage = lastUsage;
	       			}else {
	       				intervalUsage = avgDayUsage.multiply(new BigDecimal(Long.toString(BILLING_STANDARDS_DATE)));					// 구간 사용량
	       				totalUsage = intervalUsage.add(lastAccumulateUsage);
	       			}
	       		}
        		
                // 지침사용량이 누적사용량의 기준 배수(3배)이상이 아니고 누적사용량보다 클때
        		if(lastAccumulateUsage.multiply(new BigDecimal(METERING_MULTIPLE_NUMBER)).compareTo(convertBigDecimal(lastDayEM.getValue())) >= 0 || lastAccumulateUsage.compareTo(convertBigDecimal(lastDayEM.getValue())) < 0) {
        			blockBill = blockBill(meter.getMdsId(), contract.getTariffIndex().getName(), tariffEMList, totalUsage);
                	
                	// 월의 마지막 lp 데이타의 시간을 가져온다.
//                    String lastLpTime = getLastLpTime(meter.getMdsId(), lastDayEM);
                    String lastLpTime = lastDayEM.getYyyymmdd()+"000000";
                    // LP 날짜로 Billing Day를 생성하거나 업데이트한다.
                    saveBillingBlockTariff(meter, totalUsage, lastLpTime, blockBill, lastAccumulateBill);
                }else {
                	//BILLING_BLOCK_TARIFF_WRONG 이력 남기고 리턴하여 for문으로 다시 돌아감
                	
                }
    		}	//기준 일수 구간만큼 정산

            // Contract 잔액을 차감한다.
            contract.setCurrentCredit((contract.getCurrentCredit()==null? 0:contract.getCurrentCredit()) - (blockBill.subtract(lastAccumulateBill).doubleValue()));
            contractDao.update(contract);
            log.info("7. [Update CurrentCredit Contract:"+ contract.getContractNumber() + " ] MeterId[" + meter.getMdsId() + "] yyyymmdd[" + lastDayEM.getYyyymmdd() + "] "
            		+ "==> BlockBill[" + blockBill + "] lastAccumulateBill[" + lastAccumulateBill+ "]");
        }
    }
    
    public BigDecimal blockBill(String mdsId, String tariffName, List<TariffEM> tariffEMList, BigDecimal totalUsage) {
    	BigDecimal returnBill = new BigDecimal(0);
    	BigDecimal serviceCharge = convertBigDecimal(tariffEMList.get(0).getServiceCharge());
    	
    	BigDecimal activeEnergyCharge_T0 = convertBigDecimal(tariffEMList.get(0).getActiveEnergyCharge());
    	BigDecimal activeEnergyCharge_T1 = convertBigDecimal(tariffEMList.get(1).getActiveEnergyCharge());
    	BigDecimal activeEnergyCharge_T2 = convertBigDecimal(tariffEMList.get(2).getActiveEnergyCharge());
    	BigDecimal activeEnergyCharge_T3 = convertBigDecimal(tariffEMList.get(3).getActiveEnergyCharge());
    	
    	BigDecimal supplySizeMin_T0 = convertBigDecimal(tariffEMList.get(0).getSupplySizeMin());
    	BigDecimal supplySizeMin_T1 = convertBigDecimal(tariffEMList.get(1).getSupplySizeMin());
    	BigDecimal supplySizeMin_T2 = convertBigDecimal(tariffEMList.get(2).getSupplySizeMin());
    	BigDecimal supplySizeMin_T3 = convertBigDecimal(tariffEMList.get(3).getSupplySizeMin());
    	
    	BigDecimal supplySizeMax_T0 = convertBigDecimal(tariffEMList.get(0).getSupplySizeMax());
    	BigDecimal supplySizeMax_T1 = convertBigDecimal(tariffEMList.get(1).getSupplySizeMax());
    	BigDecimal supplySizeMax_T2 = convertBigDecimal(tariffEMList.get(2).getSupplySizeMax());
    	
        Collections.sort(tariffEMList, new Comparator<TariffEM>() {

            @Override
            public int compare(TariffEM t1, TariffEM t2) {
                return t1.getSupplySizeMin() < t2.getSupplySizeMin()? -1:1;
            }
        });

        if ("Public Organization".equals(tariffName) || "Autonomous Organization".equals(tariffName)) {
            returnBill = totalUsage.multiply(activeEnergyCharge_T0).add(serviceCharge);
        } else {
            returnBill = serviceCharge;
            // 0 ~ 1
            if (totalUsage.compareTo(supplySizeMin_T1) <= 0) {
                
            }
            // 1 ~
            else {
                // 1 ~ 30
                if (totalUsage.compareTo(supplySizeMax_T1) <= 0) {
                    returnBill = totalUsage.multiply(activeEnergyCharge_T1).add(returnBill);
                }
                // 31 ~ 200
                else if (totalUsage.compareTo(supplySizeMax_T2) <= 0) {
                    returnBill = supplySizeMax_T1.multiply(activeEnergyCharge_T1).add(returnBill);
                    returnBill = (totalUsage.subtract(supplySizeMin_T2)).multiply(activeEnergyCharge_T2).add(returnBill);
                }
                // 201 ~
                else {
                	returnBill = supplySizeMax_T1.multiply(activeEnergyCharge_T1).add(returnBill);
                    returnBill = (totalUsage.subtract(supplySizeMin_T2)).multiply(activeEnergyCharge_T2).add(returnBill);
                    returnBill = (totalUsage.subtract(supplySizeMin_T3)).multiply(activeEnergyCharge_T3).add(returnBill);
                }
            }
        }

        BigDecimal tca = returnBill.multiply(new BigDecimal("0.1"));
        BigDecimal frais = tca.multiply(new BigDecimal("0.1"));
        
        log.debug("4. BlockBill1 Meter[" + mdsId + "] Bill[" + returnBill + "] TCA[" + tca + "] Frais[" + frais + "] Total[" + returnBill.add(tca).add(frais) + "]");
        
        return returnBill.add(tca).add(frais);
    }
    
    private void saveBillingBlockTariff(Meter meter, BigDecimal usage, String lastLpTime, BigDecimal newbill, BigDecimal oldbill) {
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
        bill.setAccumulateUsage(usage.doubleValue());
        if (meter.getContract() != null)
            bill.setContract(meter.getContract());

        //현재누적액 - 어제누적액
        bill.setBill(newbill.subtract(oldbill).doubleValue());
        bill.setAccumulateBill(newbill.doubleValue());

        billingBlockTariffDao.saveOrUpdate(bill);
        
        log.info("6. [SaveBillingBlockTariff] MeterId[" + bill.getMDevId() + "] BillDay[" + bill.getYyyymmdd() +
                "] BillTime[" + bill.getHhmmss() + "] AccumulateUsage[" + bill.getAccumulateUsage() +
                "] AccumulateBill[" + bill.getAccumulateBill() + "] CurrentBill[" + bill.getBill() + "]");

        
    }
    
    /*private String getLastLpTime(String meterId, DayEM lastDayEM) throws Exception {
        String yyyymmdd = null;
        String lpValue = null;
        if (lastDayEM != null) {
            yyyymmdd = lastDayEM.getYyyymmdd();
            log.info("5-1. MeterId [" + meterId + "] getLastLpTime: " + yyyymmdd);
            break;
        }
        
        if (yyyymmdd != null) {
            Set<Condition> condition = new HashSet<Condition>();
            log.info("5-2. MeterId[" + meterId + "] MdevType[" + monthEM.getMdevType() + "] MdevId[" + monthEM.getMdevId() + "] YYYYMMDD[" + yyyymmdd + "]");
            condition.add(new Condition("id.mdevType", new Object[]{monthEM.getMdevType()}, null, Restriction.EQ));
            condition.add(new Condition("id.mdevId", new Object[]{monthEM.getMdevId()}, null, Restriction.EQ));
            condition.add(new Condition("id.channel", new Object[]{1}, null, Restriction.EQ));
            condition.add(new Condition("id.yyyymmddhhmiss", new Object[]{yyyymmdd+"000000", yyyymmdd+"235959"}, null, Restriction.BETWEEN));
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
            
            if(lastLp != null) {
        		return lastLp.getYyyymmddhhmiss();
            }else {
            	return yyyymmdd + "000000";
            }
            
        }

        return null;
    }*/
    
    /**
     * DAY 검침테이블에서 마지막 데이터를 가져온다.
     * @param meterId
     * @return
     */
    public DayEM getDayEM(String meterId, String yyyymmdd) {
    	TransactionStatus txstatus = null;
        try {
            txstatus = txManager.getTransaction(null);
            LinkedHashSet<Condition> condition = new LinkedHashSet<Condition>();
            condition.add(new Condition("id.mdevId", new Object[]{meterId}, null, Restriction.EQ));
            condition.add(new Condition("id.mdevType", new Object[]{DeviceType.Meter}, null, Restriction.EQ));
            condition.add(new Condition("id.dst", new Object[]{0}, null, Restriction.EQ));
            condition.add(new Condition("id.channel", new Object[]{I210Channel.ActiveEnergyImp.getChannel()}, null, Restriction.EQ));
            condition.add(new Condition("id.yyyymmdd", new Object[]{yyyymmdd}, null, Restriction.GE));
            
            List<DayEM> ret = dayEMDao.findByConditions(condition);
            DayEM lastDayEM = null;
            if(ret.size() != 0) {
                lastDayEM = ret.get(0);
                for (int i = 0; i < ret.size(); i++) {
                    if (lastDayEM.getYyyymmdd().compareTo(ret.get(i).getYyyymmdd()) < 0) {
                    	lastDayEM = ret.get(i);
                    }
                }
             }
            
            txManager.commit(txstatus);
            return lastDayEM;
        }
        catch (Exception e) {
            log.error(e, e);
            if (txstatus != null) txManager.rollback(txstatus);
        }
        return null;
    }
    
    /*
	 * 잘못된 일일정산 데이터를 저장
	 */
	/*private boolean addWrongBillingBlockTariff(AbstractDailyBillingException billingException) {
		try {
			if(billingException.getCode() == null 
					|| billingException.getMdevId() == null
					|| billingException.getYyyymmddhh() == null) {
				log.info("[" + uuid + "] || code : " + billingException.getCode() +", mdevId : " + billingException.getMdevId() + ", yyyymmddhh : " + billingException.getYyyymmddhh());
				return false;
			}

			Map<String, Object> condition = new HashMap<String, Object>();
			condition.put("code", billingException.getCode());
			condition.put("mdevId", billingException.getMdevId());
			condition.put("prevYyyymmddhh", billingException.getPrevyyyymmddhh());

			BillingBlockTariffWrong existQuery = billingBlockTariffWrongDao.getBillingBlockTariffWrong(condition);
			if(existQuery == null) {
				BillingBlockTariffWrong wrong = new BillingBlockTariffWrong();

				wrong.setCode(billingException.getCode());
				wrong.setMDevId(billingException.getMdevId());
				wrong.setYyyymmdd(billingException.getYyyymmdd());			
				wrong.setYyyymmddhh(billingException.getYyyymmddhh());
				
				wrong.setContract(contract);
				wrong.setMeter(meter);
				wrong.setModem(modem);
				
				wrong.setActiveEnergy(setDecimalValue(billingException.getAcitveEnergy(), ECGBillingBlockTariffTask.METERING_DECIMAL_PLACE));
				wrong.setActiveEnergyImport(setDecimalValue(billingException.getActiveEnergyImport(), ECGBillingBlockTariffTask.METERING_DECIMAL_PLACE));
				wrong.setActiveEnergyExport(setDecimalValue(billingException.getActiveEnergyExport(), ECGBillingBlockTariffTask.METERING_DECIMAL_PLACE));
				
				wrong.setPrevyyyymmddhh(billingException.getPrevyyyymmddhh());
				wrong.setPrevActiveEnergy(setDecimalValue(billingException.getPrevAcitveEnergy(), ECGBillingBlockTariffTask.METERING_DECIMAL_PLACE));
				wrong.setPrevActiveEnergyImport(setDecimalValue(billingException.getPrevAcitveEnergyImport(), ECGBillingBlockTariffTask.METERING_DECIMAL_PLACE));
				wrong.setPrevActiveEnergyExport(setDecimalValue(billingException.getPrevAcitveEnergyExport(), ECGBillingBlockTariffTask.METERING_DECIMAL_PLACE));
				
				wrong.setDescr(billingException.getDescr());
				wrong.setWriteDate(DateTimeUtil.getDateString(new Date()));
				wrong.setLastBillingDate(DateTimeUtil.getDateString(new Date()));
				
				int intervalDay = getIntervalDay(wrong.getPrevyyyymmddhh(), billingException.getYyyymmddhh());				
				wrong.setIntervalDay(intervalDay);				
				
				billingBlockTariffWrongDao.add(wrong);
				log.info("[" + uuid + "] || meter : "+meter.getMdsId()+" is wrong table add");
			} else {
				existQuery.setCode(billingException.getCode());
							
				existQuery.setYyyymmdd(billingException.getYyyymmdd());			
				existQuery.setYyyymmddhh(billingException.getYyyymmddhh());
				existQuery.setActiveEnergy(billingException.getAcitveEnergy());
				existQuery.setActiveEnergyImport(billingException.getActiveEnergyImport());
				existQuery.setActiveEnergyExport(billingException.getActiveEnergyExport());
				
				existQuery.setPrevActiveEnergy(billingException.getPrevAcitveEnergy());
				existQuery.setPrevActiveEnergyImport(billingException.getPrevAcitveEnergyImport());
				existQuery.setPrevActiveEnergyExport(billingException.getPrevAcitveEnergyExport());
				
				existQuery.setDescr(billingException.getDescr());
				
				existQuery.setLastBillingDate(DateTimeUtil.getDateString(new Date()));				
				
				int intervalDay = getIntervalDay(existQuery.getPrevyyyymmddhh(), billingException.getYyyymmddhh());				
				existQuery.setIntervalDay(intervalDay);
				
				billingBlockTariffWrongDao.update(existQuery);
				log.info("[" + uuid + "] || meter : "+meter.getMdsId()+" is wrong table update");
			}
						
			return true;
		}catch(Exception e) {
			log.error("[" + uuid + "] || "+e.getMessage());
			log.error(e,e);
			
			return false;
		}
	}*/
    
    private BigDecimal convertBigDecimal(Object val) {
    	BigDecimal bigDecimal = new BigDecimal(0);
    	
    	if(val instanceof String) {
    		bigDecimal = new BigDecimal(String.valueOf(val));
    	}else if(val instanceof Integer) {
    		bigDecimal = new BigDecimal(Integer.toString((int) val));
    	}else if(val instanceof Float) {
    		bigDecimal = new BigDecimal(Float.toString((float) val));
    	}else if(val instanceof Double) {
    		bigDecimal = new BigDecimal(Double.toString((double) val));
    	}else if(val instanceof Object) {
    		bigDecimal = new BigDecimal(String.valueOf(val));
    	}else {
    		bigDecimal = new BigDecimal(String.valueOf(val));
    	}
    	
    	return bigDecimal;
    }
}