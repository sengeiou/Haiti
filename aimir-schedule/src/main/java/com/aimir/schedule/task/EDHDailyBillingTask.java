package com.aimir.schedule.task;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.mvm.BillingBlockTariffDao;
import com.aimir.dao.mvm.BillingBlockTariffWrongDao;
import com.aimir.dao.mvm.DayEMDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.dao.system.TariffEMDao;
import com.aimir.fep.logger.snowflake.SnowflakeGeneration;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.model.device.Meter;
import com.aimir.model.mvm.BillingBlockTariff;
import com.aimir.model.mvm.BillingBlockTariffWrong;
import com.aimir.model.mvm.DayEM;
import com.aimir.model.system.Contract;
import com.aimir.model.system.TariffEM;
import com.aimir.schedule.exception.DailyBillingException;
import com.aimir.schedule.task.EDHDailyBillingTask.BIGDECIMAL_CALC;
import com.aimir.util.DateTimeUtil;

import antlr.ParseTreeToken;

@Service
public class EDHDailyBillingTask extends ScheduleTask {
	private static Log log = LogFactory.getLog(EDHDailyBillingTask.class);
	
	@Resource(name = "transactionManager")
	private HibernateTransactionManager txmanager;
	
	@Autowired
	private ContractDao contractDao;
	
	public static final int BILLING_STANDARDS_DATE = 10;
    public static final int METERING_MULTIPLE_NUMBER = 3;
    public static final int MINIMUM_BILLING_USAGE = 100;
    
    public static final int METERING_DECIMAL = 4;
    public static final int CREDIT_DECIMAL = 2;
    
    public static int totalExecuteCount = 0;
    public static int currentCount = 0;
    
    public enum DAILY_BILLING_ERROR_CODE {
    	AEU("Active Energy가 이전의 Active Energy의 3배보다 많은 경우"),
    	AED("Active Energy가 이전의 Active Energy보다 작은 경우"),
    	UM("연결된 meter 또는 모뎀이 없는 경우"),
    	UT("연결된 meter의 Tariff 값이 없거나 이상한 경우"),
    	UCM("계약과 미터의 관계가 1:1 아닌 경우"),
    	MB("빌이 마이너스 경우"),
    	UNKNOW("");
    	
    	public String desc;
    	DAILY_BILLING_ERROR_CODE(String desc) {
    		this.desc = desc;
    	}
    	
    	public String getDesc() {
			return this.desc;
		}
    }
    
    public enum BIGDECIMAL_CALC {
    	ADD,
    	SUBTRACT,
    	MULTIPLY,
    	DIVIDE
    }
    
	public static void main(String[] args) {
		ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{"spring-public.xml"}); 
        DataUtil.setApplicationContext(ctx);
        
        String mdevId = null;
        String billingDay = null;
        if(args.length >= 2 ) {
	        for(int i=0; i < args.length; i +=2 ) {
	        	String nextArg = args[i];
	        	
	        	if (nextArg.startsWith("-mdevId")) {
	        		mdevId = new String(args[i+1]);
	            } else if (nextArg.startsWith("-billingDay")) {
	            	billingDay = new String(args[i+1]);
	            }
	        }
	        
	        log.info("mdevId : " + mdevId+", billingDay : " +billingDay);
        }
        
        EDHDailyBillingTask task = ctx.getBean(EDHDailyBillingTask.class);
        task.execute(ctx, mdevId, billingDay);
        System.exit(0);
	}
	
	@Override
	public void execute(JobExecutionContext context) { }
	
	private void execute(ApplicationContext ctx, String mdevId, String billingDay) {
		log.info("########### START Daily Billing Task ###############");
		
		List<Contract> targets = getTargets(mdevId, billingDay);
		if(targets != null && targets.size() > 0) {
			int poolSize = Integer.parseInt(FMPProperty.getProperty("edh.sms.thread.pool.size", ""+6));
	        ThreadPoolExecutor executor = new ThreadPoolExecutor(poolSize, poolSize, 10, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>());
	        
	        for(Contract co : targets) {
	        	try {
	        		executor.execute(new EDHDailyBillingTaskSubClz(co.getId()));
	        	}catch(Exception e) {
	        		log.error(e,e);
	        	}
	        }
	        
		    try {
	            executor.shutdown();
	            while (!executor.isTerminated()) {
	            }
	        } catch (Exception e) {}
		}
		
		log.info("########### END Daily Billing Task ###############");
	}
	
	private List<Contract> getTargets(String mdevId, String billingDay) {
		TransactionStatus txstatus = null;
		List<Contract> queryResult = null;
		
		try {
			txstatus = txmanager.getTransaction(null);
			String yyyymmdd = null;
			if(billingDay != null && billingDay.length() > 1) {
				yyyymmdd = billingDay.substring(0 ,8); 
			}
			
			queryResult = contractDao.getDailyBillingContractList(mdevId, yyyymmdd);
			totalExecuteCount = queryResult.size();
			log.info("relay off meter cnt : " + totalExecuteCount);
			
			txmanager.commit(txstatus);
		}catch(Exception e) {
			log.error(e, e);
            if (txstatus != null) txmanager.rollback(txstatus);
		}
		
		return queryResult;
	}
	
	
}

class EDHDailyBillingTaskSubClz implements Runnable {
	private final static Log log = LogFactory.getLog(EDHDailyBillingTaskSubClz.class);
	
	private HibernateTransactionManager txmanager;
	private CodeDao codeDao;
	private ContractDao contractDao;
	private MeterDao meterDao;
	private DayEMDao dayEMDao;
	private BillingBlockTariffDao billingBlockTariffDao;
	private BillingBlockTariffWrongDao billingBlockTariffWrongDao;
	private TariffEMDao tariffEMDao;
	
	private Integer contractId;
	private Contract contract;
	private Meter meter;
	
	EDHDailyBillingTaskSubClz(Integer contractId) {
		this.contractId = contractId;
		SnowflakeGeneration.getInstance();
	}
	
	private void init() throws Exception {
		txmanager = (HibernateTransactionManager)DataUtil.getBean("transactionManager");
		codeDao = DataUtil.getBean(CodeDao.class);
		contractDao = DataUtil.getBean(ContractDao.class);
		meterDao = DataUtil.getBean(MeterDao.class);
		dayEMDao = DataUtil.getBean(DayEMDao.class);
		billingBlockTariffDao = DataUtil.getBean(BillingBlockTariffDao.class);
		billingBlockTariffWrongDao = DataUtil.getBean(BillingBlockTariffWrongDao.class);
		tariffEMDao = DataUtil.getBean(TariffEMDao.class);
	}
	
	private void setBaseData() throws Exception, DailyBillingException {
		TransactionStatus txstatus = null;
		StringBuffer errBuffer = new StringBuffer();
		
		try {
			txstatus = txmanager.getTransaction(null);
			
			contract = contractDao.get(contractId);
			if(contract != null) {
				meter = contract.getMeter();
				
				errBuffer.append("ContractId : ").append(contractId);
				if(meter != null) {
					errBuffer.append(", meter mdsId : ").append(meter.getMdsId());
				}
			}
							
			//Contract - Meter 1:1 관계가 아닐 경우에러 발생체크
			if(meter != null) {
				//해당 로직에서 1:1 관계가 아닐 경우 에러가 발생
				meter.getContract();
			}
			
			if(contract.getTariffIndexId() == null) {
				DailyBillingException ex = new DailyBillingException(EDHDailyBillingTask.DAILY_BILLING_ERROR_CODE.UT.name(), 
						EDHDailyBillingTask.DAILY_BILLING_ERROR_CODE.UT.getDesc());
				
				throw ex;
			}
			
			txmanager.commit(txstatus);
		}catch(HibernateException he) {
			//계약 - 미터가 1:1 관계가 아닐 경우
			DailyBillingException ex = new DailyBillingException(EDHDailyBillingTask.DAILY_BILLING_ERROR_CODE.UCM.name(), 
						EDHDailyBillingTask.DAILY_BILLING_ERROR_CODE.UCM.getDesc());
			
			throw ex;
		} catch (Exception e) {
			throw e;
		}
	}
	
	private void insertBillingError(Exception e) {
		BillingBlockTariffWrong wrong = new BillingBlockTariffWrong();
		
		if(e instanceof DailyBillingException) {
			DailyBillingException ex = (DailyBillingException)e;
			
			wrong.setCode(ex.getErrCode());
			wrong.setDescr(ex.getDescr());
			wrong.setPrevyyyymmddhh(ex.getPrev_yyyymmddhh());
			wrong.setPrevActiveEnergy(ex.getPrev_activeEnergy());
			
			wrong.setYyyymmdd(ex.getBilling_yyyymmddhh().substring(0, 8));
			wrong.setYyyymmddhh(ex.getBilling_yyyymmddhh());
			wrong.setActiveEnergy(ex.getBilling_activeEnergy());
		} else {
			wrong.setCode(EDHDailyBillingTask.DAILY_BILLING_ERROR_CODE.UNKNOW.name());
			wrong.setDescr(e.getMessage());
			wrong.setYyyymmdd(DateTimeUtil.getDateString(new Date(), "yyyyMMdd"));
		}

		wrong.setMDevId("C" + contractId);
		wrong.setContract(contract);
		wrong.setWriteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
		wrong.setLastBillingDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
		
		billingBlockTariffWrongDao.add(wrong);
	}

	@Override
	public void run() {
		long start = System.currentTimeMillis();
		SnowflakeGeneration.getId();
		
		synchronized (this) {
			EDHDailyBillingTask.currentCount++;
		}
		
		TransactionStatus txstatus = null;
		try {
			txstatus = txmanager.getTransaction(null);
			
			init();	
			setBaseData();
			
			//마지막 이전 일정산 조회
			BillingBlockTariff lastBBT = getLastBBT();
			
			//마지막 DayEM 조회
			DayEM lastDayEM = getLastDayEM();
			if(lastDayEM == null) {
				txmanager.commit(txstatus);
				log.info("meterId : " + meter.getMdsId()+" |  DayEM is empty. Daily Billing SKIP! ");
				return;
			}
			
			LinkedList<BillingBlockTariff> bbtList = getDailyBilingData(lastDayEM, lastBBT);
			
			log.debug("meterId : " + meter.getMdsId() +", bbtList Cnt : " +bbtList.size());
			log.debug("############### TAGINNG START ###############");
			if(bbtList != null) {
				BillingBlockTariff pvBBT = null;
				BillingBlockTariff cvBBT = null;
				for(int i=0; i<bbtList.size(); i++) {
					if(i == 0)
						pvBBT = lastBBT;

					cvBBT = bbtList.get(i);
					//01. bill 체크
					if(cvBBT.getBill() < 0) {
						DailyBillingException ex = new DailyBillingException(EDHDailyBillingTask.DAILY_BILLING_ERROR_CODE.MB.name(), 
								EDHDailyBillingTask.DAILY_BILLING_ERROR_CODE.MB.getDesc());
						
						throw ex;
					}
					
					if(pvBBT.getActiveEnergy() >= cvBBT.getActiveEnergy()) {
						DailyBillingException ex = new DailyBillingException(EDHDailyBillingTask.DAILY_BILLING_ERROR_CODE.AED.name(), 
								EDHDailyBillingTask.DAILY_BILLING_ERROR_CODE.AED.getDesc());
						
						throw ex;
					}
					
					if(pvBBT.getActiveEnergy() > EDHDailyBillingTask.MINIMUM_BILLING_USAGE 
							&& (pvBBT.getActiveEnergy() * EDHDailyBillingTask.METERING_MULTIPLE_NUMBER) < cvBBT.getActiveEnergy()) {
						DailyBillingException ex = new DailyBillingException(EDHDailyBillingTask.DAILY_BILLING_ERROR_CODE.AEU.name(), 
								EDHDailyBillingTask.DAILY_BILLING_ERROR_CODE.AEU.getDesc());
						
						throw ex;
					}
						
					
					log.debug("meterId : " + meter.getMdsId() +", usage : " + cvBBT.getUsage() +", accUsage : "+ cvBBT.getAccumulateUsage() +", bill : " +cvBBT.getBill() +",accBill : " +cvBBT.getAccumulateBill());
				}
			}
		
			log.debug("############### TAGINNG START ###############");
			
			txmanager.commit(txstatus);
		}catch(Exception e) {
			log.error(e, e);
			
			insertBillingError(e);
    		if (txstatus != null) 
    			txmanager.commit(txstatus);
		}
		
		synchronized (this) {
    		double rate = ((double) EDHDailyBillingTask.currentCount / (double) EDHDailyBillingTask.totalExecuteCount) * 100;

			long end = System.currentTimeMillis();
			long t = (end - start) / 1000;
			
			log.info("PROCESS CURRNET ["+String.format("%.3f", rate)+"%], TOTAL COUNT ["+EDHDailyBillingTask.totalExecuteCount+"], CURRENT COUNT ["+EDHDailyBillingTask.currentCount+"] "
					+ "time ["+t+"] ThreadName ["+Thread.currentThread().getName()+"] threadId ["+Thread.currentThread().getId()+"]");
		}
		
		SnowflakeGeneration.deleteId();
	}
	
	private DayEM getLastDayEM() {
		String mdevId = meter.getMdsId();
		 
		DayEM dayEM = dayEMDao.getLastDayEM(mdevId);
		if(dayEM != null)
			log.info("Last DayEM : " + dayEM.toString());
		
		return dayEM;
	}
	
	private BillingBlockTariff getLastBBT() {
		BillingBlockTariff bbt = billingBlockTariffDao.getLastBillingBlockTariff(contractId, meter.getMdsId());
		if(bbt == null) {
			bbt.setContract(contract);
			bbt.setMeter(meter);
			bbt.setMDevId(meter.getMdsId());
			bbt.setAccumulateBill(0d);
			bbt.setAccumulateUsage(0d);
			bbt.setActiveEnergy(0d);
			bbt.setActiveEnergyImport(0d);
			bbt.setUsage(0d);
			bbt.setBill(0d);
			
			String conDate = contract.getContractDate() != null ? contract.getContractDate() : meter.getInstallDate();
			String yyyymmdd = conDate.substring(0, 8);
			String hh = conDate.substring(8, 10) ;
			
			bbt.setYyyymmdd(yyyymmdd);
			bbt.setHhmmss(hh + "0000");
		}
		
		log.info(bbt.toString());
		return bbt;
	}
	
	private LinkedList<BillingBlockTariff> getDailyBilingData(DayEM lastDayEM, BillingBlockTariff lastBBT) throws Exception {
		LinkedList<BillingBlockTariff> billingList = new LinkedList<BillingBlockTariff>();
		
		log.info("meterId : " + meter.getMdsId()+", lastDayEM | yyyymmdd " + lastDayEM.getYyyymmdd() +", meteringValue : " +lastDayEM.getValue() );
		log.info("meterId : " + meter.getMdsId()+", lastBBT | yyyymmdd " + lastBBT.getYyyymmdd() +", lastBBT : " +lastBBT.getActiveEnergy() 
					+", accumulateBill : "  + lastBBT.getAccumulateBill() +", accumulateUsage : " +lastBBT.getAccumulateUsage());
		
		BillingBlockTariff prevBBT = null;
		
		//01. 마지막 정산의 날짜와 DayEM의 날짜가 동일한지 판단
		if(isSkipDailyBilling(lastDayEM, lastBBT)) 
			return null;
		
		//02. 이전 빌링과 최근 검침날짜의 차이를 계산
		long intervalDay = TimeUnit.DAYS.convert(Math.abs(DateTimeUtil.getDateFromYYYYMMDDHHMM(lastBBT.getYyyymmdd() + lastBBT.getHhmmss().substring(0, 2)).getTime() -
					DateTimeUtil.getDateFromYYYYMMDDHHMM(lastDayEM.getYyyymmdd() + lastDayEM.getHh()).getTime()), TimeUnit.MILLISECONDS);
		log.info("meterId : " + meter.getMdsId()+", intervalDay : " + intervalDay);
		
		if(intervalDay > EDHDailyBillingTask.BILLING_STANDARDS_DATE) {
			//만약 EDHDailyBillingTask.BILLING_STANDARDS_DATE 값보다 이전 정산간격이 크다면 평균값 계산을 진행한다.
			Calendar pCalendar = DateTimeUtil.getCalendar(lastBBT.getYyyymmdd() + "000000"); //prev bbt
			Calendar nCalendar = DateTimeUtil.getCalendar(lastDayEM.getYyyymmdd() + "000000"); //last dayEM

			BigDecimal diffMertingValue = getCale(lastDayEM.getValue(), lastBBT.getActiveEnergy(), BIGDECIMAL_CALC.SUBTRACT);
			BigDecimal avgDailyUsage = getCale(diffMertingValue, intervalDay, BIGDECIMAL_CALC.DIVIDE);
			log.info("meterId : " + meter.getMdsId()+", diffMertingValue : " + diffMertingValue + ", avgMeteringValue : " + avgDailyUsage);
			
			pCalendar.add(Calendar.DAY_OF_MONTH, 10);
			while(pCalendar.before(nCalendar)) {
				if(billingList == null || billingList.size() == 0)
					prevBBT = lastBBT;
				else
					prevBBT = billingList.get(billingList.size() - 1);
				
				BillingBlockTariff prevT = checkChangeMonth(prevBBT, avgDailyUsage, pCalendar);
				if(prevT != null)
					billingList.add(prevT);
				
				BigDecimal avgUsage = setDecimal(getCale(avgDailyUsage, EDHDailyBillingTask.BILLING_STANDARDS_DATE, BIGDECIMAL_CALC.MULTIPLY), EDHDailyBillingTask.METERING_DECIMAL);
				billingList.add(createBBT(prevBBT, pCalendar, avgUsage, true));
				
				pCalendar.add(Calendar.DAY_OF_MONTH, 10);
			}
		}
		
		if(billingList == null || billingList.size() == 0)
			prevBBT = lastBBT;
		else
			prevBBT = billingList.get(billingList.size() - 1);
		
		Calendar pCalendar = DateTimeUtil.getCalendar(lastDayEM.getYyyymmdd() + lastDayEM.getHh() + "0000");
		
		if(!isEquateDate(prevBBT.getYyyymmdd(), lastBBT.getYyyymmdd(), "yyyyMM")) {
			intervalDay = TimeUnit.DAYS.convert(Math.abs(DateTimeUtil.getDateFromYYYYMMDDHHMM(prevBBT.getYyyymmdd() + prevBBT.getHhmmss().substring(0, 2)).getTime() -
					DateTimeUtil.getDateFromYYYYMMDDHHMM(lastDayEM.getYyyymmdd() + lastDayEM.getHh()).getTime()), TimeUnit.MILLISECONDS);
			
			BigDecimal diffMertingValue = getCale(lastDayEM.getValue(), prevBBT.getActiveEnergy(), BIGDECIMAL_CALC.SUBTRACT);
			BigDecimal avgDailyUsage = getCale(diffMertingValue, intervalDay, BIGDECIMAL_CALC.DIVIDE);
			log.info("meterId : " + meter.getMdsId()+", intervalDay : " + intervalDay +", diffMertingValue : " + diffMertingValue + ", avgDailyUsage : " + avgDailyUsage);
			
			BillingBlockTariff prevT = checkChangeMonth(prevBBT, avgDailyUsage, pCalendar);
			if(prevT != null)
				billingList.add(prevT);	
		}
		
		BigDecimal usage = setDecimal(getCale(lastDayEM.getValue(), prevBBT.getActiveEnergy(), BIGDECIMAL_CALC.SUBTRACT), EDHDailyBillingTask.METERING_DECIMAL);
		billingList.add(createBBT(prevBBT, pCalendar, usage, false));
		
		return billingList;
	}
	
	//월이 바뀌었을 때 마지막 일 데이터가 없다면 해당 값을 가상으로 넣어준다.
	private BillingBlockTariff checkChangeMonth(BillingBlockTariff prevBBT, BigDecimal avgDailyUsage, Calendar pCalendar) throws Exception {
		Calendar cCalendar = DateTimeUtil.getCalendar(prevBBT.getYyyymmdd() + prevBBT.getHhmmss());

		if(isEqualDate(pCalendar, cCalendar, "yyyyMM"))
			return null;
		
		String prev_yyyymmdd = prevBBT.getYyyymmdd();
		String prev_max_yyyymmdd = DateTimeUtil.getDateString(cCalendar.getTime(), "yyyyMM") + cCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		
		if(prev_max_yyyymmdd.equals(prev_yyyymmdd))
			return null;
		
		int opDay = cCalendar.getActualMaximum(Calendar.DAY_OF_MONTH) - cCalendar.get(Calendar.DAY_OF_MONTH);
		BigDecimal avgUsage = setDecimal(getCale(avgDailyUsage, opDay, BIGDECIMAL_CALC.MULTIPLY), EDHDailyBillingTask.METERING_DECIMAL);
		log.info("meterId : " + meter.getMdsId()+", opDay : " + opDay + ", avgDailyUsage : " + avgDailyUsage +", avgUsage : " +avgUsage);
		
		cCalendar = DateTimeUtil.getCalendar(prev_max_yyyymmdd + "000000");
		return createBBT(prevBBT, cCalendar, avgUsage, true);
	}
	
	//평균값으로 계산하는 일정산 로직
	private BillingBlockTariff createBBT(BillingBlockTariff prevBBT, Calendar pCalendar, BigDecimal usage, boolean isVirtual) throws Exception {
		BillingBlockTariff bbt = new BillingBlockTariff();
		
		bbt.setMDevId(meter.getMdsId());
		bbt.setSupplier(meter.getSupplier());
		bbt.setLocation(meter.getLocation());
		bbt.setMeter(meter);
        bbt.setModem(meter.getModem());
        bbt.setContract(contract);
        bbt.setTariffIndex(contract.getTariffIndex());
        bbt.setMDevType(DeviceType.Meter.name());
        bbt.setAvg(true);
        bbt.setValidity(true);
                
		bbt.setYyyymmdd(DateTimeUtil.getDateString(pCalendar.getTime(), "yyyyMMdd"));
		bbt.setHhmmss(DateTimeUtil.getDateString(pCalendar.getTime(), "HH") + "0000");
		
		BigDecimal bActiveEnergy = setDecimal(getCale(prevBBT.getActiveEnergy(), usage, BIGDECIMAL_CALC.ADD), EDHDailyBillingTask.METERING_DECIMAL);
		bbt.setActiveEnergy(bActiveEnergy.doubleValue());
		bbt.setActiveEnergyImport(bActiveEnergy.doubleValue());

		if(isEqualDate(prevBBT, bbt, "yyyyMM")) {
			//같은 달이면 이전 정산기록에 신규 평균 사용량을 더해서 저장한다.
			BigDecimal bAccUsage = setDecimal(getCale(prevBBT.getAccumulateUsage(), usage, BIGDECIMAL_CALC.ADD), EDHDailyBillingTask.METERING_DECIMAL);
			bbt.setAccumulateUsage(bAccUsage.doubleValue());	
		} else {
			bbt.setAccumulateUsage(usage.doubleValue());
		}
		
		bbt.setUsage(usage.doubleValue());
		
		if(isEqualDate(prevBBT, bbt, "yyyyMM")) {
			BigDecimal accBill = new BigDecimal(String.valueOf(prevBBT.getAccumulateBill()));
					
			BigDecimal bill = getBill(bbt.getYyyymmdd(), new BigDecimal(String.valueOf(prevBBT.getAccumulateUsage())), usage);
			bill = setDecimal(bill, EDHDailyBillingTask.CREDIT_DECIMAL);
			
			accBill = getCale(accBill, bill, BIGDECIMAL_CALC.ADD);
			
			bbt.setAccumulateBill(accBill.doubleValue());
			bbt.setBill(bill.doubleValue());
		} else {
			BigDecimal bill = getBill(bbt.getYyyymmdd(), new BigDecimal(String.valueOf(prevBBT.getAccumulateUsage())), usage);
			bill = setDecimal(bill, EDHDailyBillingTask.CREDIT_DECIMAL);
			
			bbt.setAccumulateBill(0d);
			bbt.setBill(bill.doubleValue());
		}
		bbt.setWriteDate(DateTimeUtil.getDateString(new Date()));

		log.info(bbt.toString());
		return bbt;
	}

	private BigDecimal getBill(String yyyymmdd, BigDecimal prevAccUsage, BigDecimal usage) throws Exception {
		log.info("meterId : " + meter.getMdsId()+", yyyymmdd : " + yyyymmdd + ", prevAccUsage : " +prevAccUsage.doubleValue() + ", usage : " + usage.doubleValue());
		List<TariffEM> list = getTariffList(yyyymmdd);
		
		BigDecimal bill = null;
		
		if(list != null) {
			BigDecimal mod = usage;
			for(TariffEM em : list) {
				//해당 구간의 사용요금
				BigDecimal tariffAmount = new BigDecimal(String.valueOf(em.getActiveEnergyCharge()));
				
				if(em.getSupplySizeMax() == null) {
					if(bill == null) {
						bill = setDecimal(getCale(mod, tariffAmount, BIGDECIMAL_CALC.MULTIPLY), EDHDailyBillingTask.CREDIT_DECIMAL);
					} else {
						BigDecimal tBill = setDecimal(getCale(mod, tariffAmount, BIGDECIMAL_CALC.MULTIPLY), EDHDailyBillingTask.CREDIT_DECIMAL);
						bill = getCale(bill, tBill, BIGDECIMAL_CALC.ADD);
					}
					
					log.info("meterId : " + meter.getMdsId()+", bill : " + bill);
					return bill;
				}
				
				BigDecimal supplyMax = new BigDecimal(String.valueOf(em.getSupplySizeMax()));
				if(prevAccUsage.compareTo(supplyMax) >= 0 )
					continue;
				
				//블럭타리프에서 해당 구간에서 남은 사용량
				BigDecimal remainUsage = getCale(supplyMax, prevAccUsage, BIGDECIMAL_CALC.SUBTRACT);
				if(remainUsage.compareTo(mod) >= 0) {
					if(bill == null) {
						bill = setDecimal(getCale(mod, tariffAmount, BIGDECIMAL_CALC.MULTIPLY), EDHDailyBillingTask.CREDIT_DECIMAL);
					} else {
						BigDecimal tBill = setDecimal(getCale(mod, tariffAmount, BIGDECIMAL_CALC.MULTIPLY), EDHDailyBillingTask.CREDIT_DECIMAL);
						bill = getCale(bill, tBill, BIGDECIMAL_CALC.ADD);
					}
					
					log.info("meterId : " + meter.getMdsId()+", bill : " + bill);
					return bill;
				} else {
					if(bill == null) {
						bill = setDecimal(getCale(remainUsage, tariffAmount, BIGDECIMAL_CALC.MULTIPLY), EDHDailyBillingTask.CREDIT_DECIMAL);
					} else {
						BigDecimal tBill = setDecimal(getCale(remainUsage, tariffAmount, BIGDECIMAL_CALC.MULTIPLY), EDHDailyBillingTask.CREDIT_DECIMAL);
						bill = getCale(bill, tBill, BIGDECIMAL_CALC.ADD);
						mod = getCale(mod, remainUsage, BIGDECIMAL_CALC.SUBTRACT);
					}
				}
			}
		}
		
		return null;
	}
	
	
	
	private List<TariffEM> getTariffList(String yyymmdd) throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("tariffIndex", contract.getTariffIndex());
		param.put("searchDate", yyymmdd);
		
		List<TariffEM> list = tariffEMDao.getApplyedTariff(param);
		
		Collections.sort(list, new Comparator<TariffEM>() {
            @Override
            public int compare(TariffEM o1, TariffEM o2) {
                return o1.getSupplySizeMin().compareTo(o2.getSupplySizeMin());
            }
            
        });
		
		return list;
	}
	
	private BigDecimal getCale(Object left, Object right, EDHDailyBillingTask.BIGDECIMAL_CALC calcOP) {
		BigDecimal bLeft = null;
		BigDecimal bRigth = null;
		
		if(left instanceof Number)
			bLeft = new BigDecimal(String.valueOf((Double)left));
		else if(left instanceof BigDecimal)
			bLeft = (BigDecimal)left;
		
		if(right instanceof Number)
			bRigth = new BigDecimal(String.valueOf((Double)right));
		else if(right instanceof BigDecimal)
			bRigth = (BigDecimal)right;
		
		if(bLeft != null && bRigth != null)
			return getCale(bLeft, bRigth, calcOP);
		else
			return null;
	}
	
	private BigDecimal getCale(BigDecimal left, BigDecimal right, EDHDailyBillingTask.BIGDECIMAL_CALC calcOP) {
		switch(calcOP) {
		case ADD:
			return left.add(right);
		case DIVIDE:
			return left.divide(right);					
		case MULTIPLY:
			return left.multiply(right);
		case SUBTRACT:
			return left.subtract(right);
		default:
			return null;
		}
	}
	
	private BigDecimal setDecimal(BigDecimal val, int digit) {
		return val.setScale(digit, RoundingMode.DOWN);
	}
	
	private boolean isSkipDailyBilling(DayEM lastDayEM, BillingBlockTariff lastBBT) {
		String leftYYYYMMDDHH = lastDayEM.getYyyymmdd() + lastDayEM.getHh();
		String rigthYYYYMMDDHH = lastBBT.getYyyymmdd() + lastBBT.getHhmmss().substring(0, 2);
		
		if(leftYYYYMMDDHH.equals(rigthYYYYMMDDHH))
			return true;
		else 
			return false;
	}
	
	private boolean isEqualDate(BillingBlockTariff prev, BillingBlockTariff next, String pattern) {
		String prev_date = prev.getYyyymmdd().substring(0, pattern.trim().length());
		String next_date = next.getYyyymmdd().substring(0, pattern.trim().length());
		
		if(prev_date.equals(next_date))
			return true;
		else
			return false;
	}
	
	private boolean isEqualDate(Calendar prev, Calendar next, String pattern) {
		String prev_date = DateTimeUtil.getDateString(prev.getTime()).substring(0, pattern.trim().length());
		String next_date = DateTimeUtil.getDateString(next.getTime()).substring(0, pattern.trim().length());
		
		if(prev_date.equals(next_date))
			return true;
		else
			return false;
	}
	
	private boolean isEquateDate(String prev, String next, String pattern) {
		String prev_date = prev.substring(0, pattern.trim().length());
		String next_date = next.substring(0, pattern.trim().length());
		
		if(prev_date.equals(next_date))
			return true;
		else
			return false;
	}
}
