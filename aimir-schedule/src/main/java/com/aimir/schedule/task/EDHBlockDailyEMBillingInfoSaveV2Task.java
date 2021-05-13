package com.aimir.schedule.task;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import com.aimir.dao.mvm.LpEMDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.dao.system.MonthlyBillingLogDao;
import com.aimir.dao.system.PrepaymentLogDao;
import com.aimir.dao.system.TariffEMDao;
import com.aimir.dao.system.TariffTypeDao;
import com.aimir.fep.logger.snowflake.SnowflakeGeneration;
import com.aimir.fep.util.DataUtil;
import com.aimir.model.device.Meter;
import com.aimir.model.mvm.BillingBlockTariff;
import com.aimir.model.mvm.BillingBlockTariffWrong;
import com.aimir.model.mvm.DayEM;
import com.aimir.model.system.Code;
import com.aimir.model.system.Contract;
import com.aimir.model.system.TariffEM;
import com.aimir.model.system.TariffType;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;
import com.aimir.util.DateTimeUtil;

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
    private static final int BILLING_STANDARDS_DATE = 10;
    private static final int METERING_MULTIPLE_NUMBER = 3;
    private static final int MINIMUM_BILLING_USAGE = 100;
    private static final int MAX_THREAD_WORKER = 10;

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
    TariffEMDao tariffEMDao;

    @Autowired
    TariffTypeDao tariffTypeDao;

    @Autowired
    BillingBlockTariffDao billingBlockTariffDao;
    
    @Autowired
    BillingBlockTariffWrongDao billingBlockTariffWrongDao ;

    @Autowired
    PrepaymentLogDao prepaymentLogDao;

    @Autowired
    LpEMDao lpEMDao;
    
    @Autowired
    MonthlyBillingLogDao monthlyBillingLogDao;

    private boolean isNowRunning = false;
    
    /**
     *
     * @author jhdang
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
    
    public enum BILLING_BLOCK_ERROR_CODE {
    	AEAE("Active Energy가 이전의 Active Energy의 3배보다 많은 경우"),
    	AEPS("Active Energy가 이전의 Active Energy보다 작은 경우"),
    	INVD("연결된 meter 또는 모뎀이 없는 경우"),
    	INVT("연결된 meter의 Tariff 값이 없거나 이상한 경우"),
    	ETC("기타");
    	
    	public String desc;
    	BILLING_BLOCK_ERROR_CODE(String desc) {
    		this.desc = desc;
    	}
    	
    	public String getDesc() {
			return this.desc;
		}
    }
    
    public static void main(String[] args) {
    	try {
        	ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{"spring-BlockDailyEMBillingTask.xml"}); 
            DataUtil.setApplicationContext(ctx);
            
            log.info("########### START EDHBlockDailyEMBillingInfoSaveV2Task ###############");
            SnowflakeGeneration.getInstance();
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
    	// Electricity 계약 목록 조회
    	List<Contract> emContractList = getEMContractList(); 
    	
    	int threadPoolSize = MAX_THREAD_WORKER < emContractList.size() ? MAX_THREAD_WORKER : emContractList.size();
    	ExecutorService executorService = Executors.newFixedThreadPool(threadPoolSize);
    	
    	try {
	    	for (int i = 0; i < emContractList.size(); i++) {
	    		Contract contract = emContractList.get(i);
				log.info("contractNumber[" + contract.getContractNumber() + "] meterId[" + contract.getMeterId() + "]");
                if (contract.getCustomerId() == null || contract.getMeterId() == null) continue;
                
	    		Runnable runnable = new Runnable() {
	    			@Override
	                public void run() {
	    				SnowflakeGeneration.getId();
		                try {
							saveEmBillingDailyWithTariffEM(contract.getId());
						} catch (Exception e) {
							log.info("Thread runnable Exception : contractNumber[" + contract.getContractNumber() + "] meterId[" + contract.getMeterId() + "]");
						}
		                SnowflakeGeneration.deleteId();
	    			}
	    		};
	    		
	    		//스레드풀에게 작업 처리 요청
	            executorService.execute(runnable);
                }
	    	
    		//스레드풀 종료
    		try {
    			executorService.shutdown();
                while (!executorService.isTerminated()) {
                }
            }
            catch (Exception e) {}
    		
        }catch (Exception e) {
            log.error("saveEmBillingDayInfo Exception ==> " + e);
        }
    }

	private void saveEmBillingDailyWithTariffEM(Integer contractId) throws Exception {
    	TransactionStatus txStatus = null;
    	try {
    		txStatus = txManager.getTransaction(null);
    		
    		Contract contract =  contractDao.findByCondition("id", contractId);
    		Meter meter = meterDao.findByCondition("id", contract.getMeterId());
            DayEM lastDayEM = null;

            //마지막 누적요금이 저장된 데이터를 가지고 온다.
            BillingBlockTariff lastBilling = getLastAccumulateBill(meter.getMdsId());
            
            if(lastBilling == null) {
            	// 마지막 누적 요금이 저장된 billingDayEM이 없을 경우 한번도 선불스케줄을 돌리지 않은것으로 간주
            	lastBilling = makeNewBillingBlockTariff(contract);
            }
            log.info("ContractNumber [" + contract.getContractNumber() + "] lastBilling balance [" + convertBigDecimal(lastBilling.getBalance()) + "] lastAccumulateBill [" + convertBigDecimal(lastBilling.getAccumulateBill()) + "] lastAccumulateUsage [" + convertBigDecimal(lastBilling.getAccumulateUsage()) + "] lastAccumulateDate [" + lastBilling.getYyyymmdd().toString() + "] mdsId [" + meter.getMdsId() + "] ");
            
            lastDayEM = getDayEM(meter.getMdsId(), lastBilling.getYyyymmdd());	//최근 DAY_EM 조회
            
            //데이터 검증 하여 BILLING_BLOCK_TARIFF_WRONG 이력 남기고 리턴하여 for문으로 다시 돌아감
            if(validateBillingValues(contract, lastDayEM, lastBilling)) {
            	txManager.commit(txStatus);
            	return;
            }
            	
        	// 마지막 시간이 현재 시간보다 작으면 월 기준으로 선불 계산한다.
        	// compareTo - lastAccumulateDate가 lastDayEM 날짜 보다 작으면 음수 반환, 크면 양수 반환, 같으면 0 리턴
        	if (lastBilling.getYyyymmdd().compareTo(lastDayEM.getYyyymmdd()) <= 0 && lastBilling.getActiveEnergy() < lastDayEM.getValue()) {
        		log.info("#### dailyBilling start  lastBilling yyyymmdd["+ lastBilling.getYyyymmdd()+ "] ActiveEnergy[" + lastBilling.getActiveEnergy() + "] lastDayEM yyyymmdd["+ lastDayEM.getYyyymmdd() +"] ActiveEnergy["+ lastDayEM.getValue() +"] ####");
        		// DAY_EM 마지막 날짜와 lastAccumulateDate간의 시간차이를 비교하여 일수로 반환
        		long diffDays = Long.parseLong(calculateDiffDays(lastDayEM.getYyyymmdd(), lastBilling.getYyyymmdd()));
        		log.info("#### diffDays : "+ diffDays+ " ####");
        		
        		//해당 TariffEM 정보를 가져온다.
        		List<TariffEM> tariffEMList = getTariffEMList(contract, lastBilling.getYyyymmdd());
        		
        		// 마지막 BillingBlockTariff 추가
        		LinkedList<BillingBlockTariff> sequenceBillings = new LinkedList<BillingBlockTariff>();
        		sequenceBillings.add(lastBilling);
        		
        		// BillingBlockTariff low 생성
        		sequenceBillings = gatherBillingBlock(sequenceBillings, diffDays, tariffEMList, contract, meter, lastDayEM);
        		
                // Contract 잔액을 차감한다.
        		for (int i = 1; i < sequenceBillings.size(); i++) {
        			saveBillingBlockTariff(contract, meter, sequenceBillings.get(i));		// BillingBlockTariff 저장
        			contract.setCurrentCredit(sequenceBillings.get(i).getBalance());
        			log.info("Update contract is ContractNumber["+ contract.getContractNumber() + "] MdsId[" + meter.getMdsId() + "] yyyymmdd[" + lastDayEM.getYyyymmdd() + "] "
        					+ "==> Balance[" + sequenceBillings.get(i).getBalance() + "] BlockBill[" + sequenceBillings.get(i).getBill() + "] lastAccumulateBill[" + sequenceBillings.get(i-1).getBill()+ "]");
    			}
        		contractDao.merge(contract);
        	}
        	txManager.commit(txStatus);
		} catch (Exception e) {
			log.error("saveEmBillingDaily Exception ==> Contract number = " + contractId, e);
			if(txStatus != null) txManager.rollback(txStatus);
		}    	
    }
    
	private LinkedList<BillingBlockTariff> gatherBillingBlock(LinkedList<BillingBlockTariff> sequenceBillings, long diffDays, List<TariffEM> tariffEMList, Contract contract, Meter meter, DayEM lastDayEM) throws ParseException{
 	   long compareCnt = diffDays % BILLING_STANDARDS_DATE == 0 ? (diffDays / BILLING_STANDARDS_DATE) -1 : diffDays / BILLING_STANDARDS_DATE ;
 		log.info("####  compareCnt : "+ compareCnt+ " ####");
 		BigDecimal avgDayUsage = new BigDecimal(0);
 		if(compareCnt > 0) {
 			avgDayUsage = convertBigDecimal(lastDayEM.getValue()).subtract(convertBigDecimal(sequenceBillings.get(0).getActiveEnergy())).divide(convertBigDecimal(diffDays), 4, BigDecimal.ROUND_HALF_UP) ; // 소수점 4자리 까지
 			log.info("####  avgDayUsage : "+ avgDayUsage+ " ####");
 		}
 		// 구간 정산
 	    for (int i = 0; i < compareCnt; i++) {
 			BillingBlockTariff lastIndex = sequenceBillings.getLast();
 			String nextBillingDay = calculateNextDays(lastIndex.getYyyymmdd(), BILLING_STANDARDS_DATE);
 			String remainBillingDay = null;
 			
 			// 다음 정산일과 이전 BillingBlockTariff 날짜 월이 다르면
 			if(!lastIndex.getYyyymmdd().subSequence(0, 6).equals(nextBillingDay.substring(0, 6))) {
 				BigDecimal usage = new BigDecimal(0);
 				String maxDay = maxDayOfMonth(lastIndex.getYyyymmdd());						//해당 월의 말일
 				remainBillingDay = calculateDiffDays(lastIndex.getYyyymmdd(), maxDay);		//이전 BillingBlockTariff 날짜와 해당 월의 말일 차이를 계산
 				if(!remainBillingDay.equals("0")) {											//이전 BillingBlockTariff 날짜와 해당 월의 말일 차이가 있다면
 					usage = avgDayUsage.multiply(convertBigDecimal(remainBillingDay));		//일평균
 					sequenceBillings.add(makeBillings(lastIndex, usage, maxDay, meter, contract, tariffEMList, true));
 				}
 				
 				lastIndex = sequenceBillings.getLast();
 				remainBillingDay = Integer.toString(BILLING_STANDARDS_DATE - Integer.parseInt(remainBillingDay));	//기준일 10일에서 말일 차이를 뺀다
 				nextBillingDay = calculateNextDays(lastIndex.getYyyymmdd(), Integer.parseInt(remainBillingDay));	//이전 BillingBlockTariff 날짜에서 다음 정산일을 구한다
 				BillingBlockTariff firstIndex = makeNewMonthBillingBlockTariff(lastIndex, nextBillingDay);			//달이 바뀌면서 BillingBlockTariff 초기화
 				usage = avgDayUsage.multiply(convertBigDecimal(remainBillingDay));									//남은 일수만큼의 일평균	
 				sequenceBillings.add(makeBillings(firstIndex, usage, nextBillingDay, meter, contract, tariffEMList, true));
 			}
 			// 다음 정산일과 이전 BillingBlockTariff 날짜 월이 같으면
 			else {
 				lastIndex = sequenceBillings.getLast();
 				nextBillingDay = calculateNextDays(lastIndex.getYyyymmdd(), BILLING_STANDARDS_DATE);	// 다음 정산일
 				BigDecimal usage = avgDayUsage.multiply(convertBigDecimal(BILLING_STANDARDS_DATE));		// 일평균
 				sequenceBillings.add(makeBillings(lastIndex, usage, nextBillingDay, meter, contract, tariffEMList, true));
 			}
 		}
 	    // 일반 정산
 		BillingBlockTariff lastIndex = sequenceBillings.getLast();
 		String remainBillingDay = null;
 		log.info("#### Last BillingBlockTariff conclusion begins  ####");
 		// 다음 정산일과 이전 BillingBlockTariff 날짜 월이 다르면
 		if(!lastIndex.getYyyymmdd().subSequence(0, 6).equals(lastDayEM.getYyyymmdd().substring(0, 6))) {
 			BigDecimal usage = new BigDecimal(0);
 			String maxDay = maxDayOfMonth(lastIndex.getYyyymmdd());						//해당 월의 말일
 			remainBillingDay = calculateDiffDays(lastIndex.getYyyymmdd(), maxDay);		//이전 BillingBlockTariff 날짜와 해당 월의 말일 차이를 계산
 			if(!remainBillingDay.equals("0")) {											//이전 BillingBlockTariff 날짜와 해당 월의 말일 차이가 있다면
 				usage = avgDayUsage.multiply(convertBigDecimal(remainBillingDay));		//일평균
 				sequenceBillings.add(makeBillings(lastIndex, usage, maxDay, meter, contract, tariffEMList, true));
 			}
 			
 			lastIndex = sequenceBillings.getLast();
 			remainBillingDay = calculateDiffDays(lastDayEM.getYyyymmdd(), lastIndex.getYyyymmdd());
 			BillingBlockTariff firstIndex = makeNewMonthBillingBlockTariff(lastIndex, remainBillingDay);					//달이 바뀌면서 BillingBlockTariff 초기화
 			usage = convertBigDecimal(lastDayEM.getValue()).subtract(convertBigDecimal(firstIndex.getActiveEnergy()));		//Day_EM의 마지막 값에서 이전 BillingBlockTariff ActiveEnergy를 뺀 값으로 저장
 			sequenceBillings.add(makeBillings(firstIndex, usage, lastDayEM.getYyyymmdd()+lastDayEM.getId().getHh(), meter, contract, tariffEMList, false));
 		}
 		// 다음 정산일과 이전 BillingBlockTariff 날짜 월이 같으면	
 		else {
 			lastIndex = sequenceBillings.getLast();
 			BigDecimal usage = convertBigDecimal(lastDayEM.getValue()).subtract(convertBigDecimal(lastIndex.getActiveEnergy()));
 			sequenceBillings.add(makeBillings(lastIndex, usage, lastDayEM.getYyyymmdd()+lastDayEM.getId().getHh(), meter, contract, tariffEMList, false));
 		}
    
 		return sequenceBillings;
    }

    private BillingBlockTariff makeBillings(BillingBlockTariff lastIndex, BigDecimal usage, String date, Meter meter, Contract contract, List<TariffEM> tariffEMList, Boolean isAVG) {
    	BillingBlockTariff bill = new BillingBlockTariff();
    	if(isAVG) {
    		bill.setYyyymmdd(date);
    		bill.setHhmmss("000000");
    	}else {
    		bill.setYyyymmdd(date.substring(0, 8));
    		bill.setHhmmss(date.substring(8)+"0000");
    	}
    	bill.setUsage(usage.doubleValue());																		// ACTIVEENERGY - Previous ACTIVEENERGY
    	bill.setActiveEnergy(convertBigDecimal(lastIndex.getActiveEnergy()).add(usage).doubleValue());			// Previous ACTIVEENERGY + USAGE
    	bill.setActiveEnergyImport(convertBigDecimal(lastIndex.getActiveEnergy()).add(usage).doubleValue());	// Previous ACTIVEENERGY + USAGE
    	bill.setAccumulateUsage(convertBigDecimal(lastIndex.getAccumulateUsage()).add(usage).doubleValue());	// Previous ACCUMULATEUSAGE + USAGE
    	// Tariff 구간 요금 계산
    	BigDecimal accumulateBill = blockBill(meter.getMdsId(), contract.getTariffIndex().getName(), tariffEMList, convertBigDecimal(bill.getAccumulateUsage()));	//BlockTariff
    	bill.setAccumulateBill(accumulateBill.doubleValue());
    	BigDecimal billingBill = accumulateBill.subtract(convertBigDecimal(lastIndex.getAccumulateBill()));
    	bill.setBill(accumulateBill.subtract(convertBigDecimal(lastIndex.getAccumulateBill()).setScale(2, BigDecimal.ROUND_HALF_UP)).doubleValue());	// ACCUMULATEBILL - Previous ACCUMULATEBILL = Bill
    	bill.setBalance(convertBigDecimal(contract.getCurrentCredit() != null ? contract.getCurrentCredit() : 0.0).subtract(billingBill).doubleValue());// Balance - Bill
    	bill.setContractId(lastIndex.getContractId());
    	bill.setAvg(isAVG);
    	
    	contract.setCurrentCredit(bill.getBalance());   	

		return bill;
	}
    
    public BigDecimal blockBill(String mdsId, String tariffName, List<TariffEM> tariffEMList, BigDecimal totalUsage) {
    	BigDecimal returnBill = new BigDecimal(0);
    	// 매월 기본요금 부과 - 월정산으로 대체
    	// TCA, Fraise Special은 충전할 때 충전 요금에서 차감
    	//BigDecimal serviceCharge = convertBigDecimal(tariffEMList.get(0).getServiceCharge());
    	
    	Collections.sort(tariffEMList, new Comparator<TariffEM>() {

            @Override
            public int compare(TariffEM t1, TariffEM t2) {
                return t1.getSupplySizeMin() < t2.getSupplySizeMin()? -1:1;
            }
        });
    	
		for (TariffEM tariffEM : tariffEMList) {
    		if ("Eclairage Public".equals(tariffName) || "Organisme Public".equals(tariffName) || "Organisme Autonome".equals(tariffName)) {
    			returnBill = totalUsage.multiply(convertBigDecimal(tariffEM.getActiveEnergyCharge()));
	        }else {
	        	// 구간 SupplySizeMax 값보다 크면 (Max - Min) * ActiveEnergyCharge
	        	if(tariffEM.getSupplySizeMax() != null && totalUsage.compareTo(convertBigDecimal(tariffEM.getSupplySizeMax())) >= 0) {
            		returnBill = convertBigDecimal(tariffEM.getSupplySizeMax()).subtract(convertBigDecimal(tariffEM.getSupplySizeMin())).multiply(convertBigDecimal(tariffEM.getActiveEnergyCharge())).add(returnBill);
            		log.info("Interval Bill : "+returnBill + " supplySize_Min : " + tariffEM.getSupplySizeMin() + " supplySize_Max : " + tariffEM.getSupplySizeMax() + " ActiveEnergyCharge : " + tariffEM.getActiveEnergyCharge());
	        	}
	        	// 구간 SupplySizeMax 값보다 작으면 (Usage - Min) * ActiveEnergyCharge
	        	else {
            		returnBill = totalUsage.subtract(convertBigDecimal(tariffEM.getSupplySizeMin())).multiply(convertBigDecimal(tariffEM.getActiveEnergyCharge())).add(returnBill);
            		log.info("Interval Bill : "+returnBill + " supplySize_Min : " + tariffEM.getSupplySizeMin() + " supplySize_Max : " + tariffEM.getSupplySizeMax() + " ActiveEnergyCharge : " + tariffEM.getActiveEnergyCharge());
            		break;	// 사용량이 현재 구간 SupplySizeMax보다 작으면 다음 구간 계산 불필요
            	}
	        }
		}

    	log.info("Total BlockBill is Meter[" + mdsId + "] ACCUMULATEUSAGE[" + totalUsage + "] Bill[" + returnBill + "]");
        return returnBill.setScale(2, BigDecimal.ROUND_HALF_UP);	//소수점 2자리 까지
    }
    
    private List<Contract> getEMContractList() {
    	TransactionStatus txStatus = null;
    	List<Contract> emContractList = new ArrayList<Contract>();
    	try {
    		txStatus = txManager.getTransaction(null);
    		emContractList = contractDao.getContract(Code.PREPAYMENT, SERVICE_TYPE_EM);
    		txManager.commit(txStatus);
		} catch (Exception e) {
			log.error("getEMContractList Exception ==> ", e);
			if(txStatus != null) txManager.rollback(txStatus);
		}
		return emContractList;
	}
    
    private void saveBillingBlockTariff(Contract contract, Meter meter, BillingBlockTariff billingBlockTariff) {
        BillingBlockTariff bill = new BillingBlockTariff();

        bill.setMDevId(meter.getMdsId());
        bill.setYyyymmdd(billingBlockTariff.getYyyymmdd());
        bill.setHhmmss(billingBlockTariff.getHhmmss());
        bill.setMDevType(DeviceType.Meter.name());
        bill.setTariffIndex(contract.getTariffIndex());
        bill.setSupplier(meter.getSupplier());
        bill.setLocation(meter.getLocation());
        bill.setContract(contract);
        bill.setMeter(meter);
        bill.setModem((meter == null) ? null : meter.getModem());
        bill.setAvg(billingBlockTariff.isAvg());
        bill.setValidity(true);
        bill.setWriteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
        bill.setActiveEnergy(billingBlockTariff.getActiveEnergy());
        bill.setActiveEnergyImport(billingBlockTariff.getActiveEnergyImport());
        bill.setUsage(billingBlockTariff.getUsage());
        bill.setAccumulateUsage(billingBlockTariff.getAccumulateUsage());
        bill.setBalance(billingBlockTariff.getBalance());
        bill.setBill(billingBlockTariff.getBill());
        bill.setAccumulateBill(billingBlockTariff.getAccumulateBill());

        monthlyBillingLogDao.updateMonthlyUsageInfo(contract.getId(), bill.getMDevId(), bill.getYyyymmdd(), bill.getAccumulateUsage(), bill.getAccumulateBill(), bill.getActiveEnergyImport(), bill.getActiveEnergyExport());
        log.info("SaveMonthlyBillingLog ==> MeterId[" + bill.getMDevId() + "] BillDay[" + bill.getYyyymmdd() +
                "] AccumulateUsage[" + bill.getAccumulateUsage() + "] AccumulateBill[" + bill.getAccumulateBill() +
                "] ActiveEnergyImport[" + bill.getActiveEnergyImport() + "]" + "] ActiveEnergyExport[" + bill.getActiveEnergyExport() + "]");
        
        billingBlockTariffDao.add(bill);
        log.info("SaveBillingBlockTariff ==> MeterId[" + bill.getMDevId() + "] BillDay[" + bill.getYyyymmdd() +
            "] BillTime[" + bill.getHhmmss() + "] AccumulateUsage[" + bill.getAccumulateUsage() +
            "] AccumulateBill[" + bill.getAccumulateBill() + "] CurrentBill[" + bill.getBill() + "] Balance[" + bill.getBalance() + "]");
    }
    
    /**
     * DAY 검침테이블에서 마지막 데이터를 가져온다.
     * @param meterId
     * @return
     */
    public DayEM getDayEM(String meterId, String yyyymmdd) {
        try {
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
                    if (lastDayEM.getYyyymmdd().compareTo(ret.get(i).getYyyymmdd()) <= 0) {
                    	lastDayEM = ret.get(i);
                    }
                }
             }
            
            return lastDayEM;
        }
        catch (Exception e) {
            log.error(e, e);
        }
        return null;
    }
    
    public BillingBlockTariff getLastAccumulateBill(String meterId) {
        try {
            LinkedHashSet<Condition> condition = new LinkedHashSet<Condition>();
            condition.add(new Condition("id.mdevId", new Object[]{meterId}, null, Restriction.EQ));
            condition.add(new Condition("id.mdevType", new Object[]{DeviceType.Meter}, null, Restriction.EQ));
            condition.add(new Condition("validity", new Object[]{true}, null, Restriction.EQ));
            
            List<BillingBlockTariff> ret = billingBlockTariffDao.findByConditions(condition);
            BillingBlockTariff lastBillingBlockTariff = null;
            if(ret.size() != 0) {
            	lastBillingBlockTariff = ret.get(0);
                for (int i = 0; i < ret.size(); i++) {
                    if (lastBillingBlockTariff.getYyyymmdd().compareTo(ret.get(i).getYyyymmdd()) <= 0) {
                    	lastBillingBlockTariff = ret.get(i);
                    }
                }
             }
            
            return lastBillingBlockTariff;
        }catch (ArrayIndexOutOfBoundsException e) {
            log.info("BillingBlockTariff does not exist : "+ e);
        }
        return null;
    }
    
    private boolean validateBillingValues(Contract contract, DayEM lastDayEM, BillingBlockTariff lastBilling) {
    	boolean result = false;
    	
    	if(lastDayEM == null) {
        	log.info("### validateBillingValues DayEM is null ContractNumber[" +contract.getContractNumber() + "] Meter[" + contract.getMeter().getMdsId() + "] ###");
        	return true;
        }else {
        	log.info("#### validateBillingValues start ==> lastBilling yyyymmdd["+ lastBilling.getYyyymmdd()+ "] lastBilling ActiveEnergy[" + lastBilling.getActiveEnergy() + "] lastDayEM yyyymmdd["+ lastDayEM.getYyyymmdd() +"] lastDayEM ActiveEnergy["+ lastDayEM.getValue() +"] ####");
        }
    	
    	log.info("### validateBillingValues   TariffIndexId["+contract.getTariffIndexId()+"]  lastDayEM["+lastDayEM.getValue()+"]  ActiveEnergy["+lastBilling.getActiveEnergy() + "] ###");
    	// Day_EM value가 이전 activeEnergy 보다 클떄 진행하고 작으면 Wrong 테이블 저장
    	if(lastDayEM.getValue() < lastBilling.getActiveEnergy()) {
    		addWrongBillingBlockTariff(contract, contract.getMeter(), lastDayEM, lastBilling, BILLING_BLOCK_ERROR_CODE.AEPS);
    		result = true;
   		// activeEnergy 사용량 최소 100이상은 되고 Day_EM value가 activeEnergy 사용량 3배 보다 크면 Wrong 테이블 저장	 
    	}else if(lastDayEM.getValue() > lastBilling.getActiveEnergy() * METERING_MULTIPLE_NUMBER && lastBilling.getActiveEnergy() > MINIMUM_BILLING_USAGE) {
    		addWrongBillingBlockTariff(contract, contract.getMeter(), lastDayEM, lastBilling, BILLING_BLOCK_ERROR_CODE.AEAE);
    		result = true;
    	// tariffIndexId가 null이면 Wrong 테이블 저장	
    	}else if(contract.getTariffIndexId() == null) {
			addWrongBillingBlockTariff(contract, contract.getMeter(), lastDayEM, lastBilling, BILLING_BLOCK_ERROR_CODE.INVT);
			result = true;
		}
       	return result;
	}
    
    /*
	 * 잘못된 일일정산 데이터를 저장
	 */
	private void addWrongBillingBlockTariff(Contract contract, Meter meter, DayEM lastDayEM, BillingBlockTariff lastBilling, BILLING_BLOCK_ERROR_CODE code) {
    	try {
	        BillingBlockTariffWrong billWrong = new BillingBlockTariffWrong();
	        billWrong.setCode(code.name());
	        billWrong.setMDevId(meter.getMdsId());
	        billWrong.setYyyymmdd(lastDayEM.getYyyymmdd());
	        billWrong.setYyyymmddhh(lastDayEM.getYyyymmdd()+lastDayEM.getId().getHh());
	        billWrong.setLocation(meter.getLocation());
	        billWrong.setContract(contract);
	        billWrong.setMeter(meter);
	        billWrong.setModem((meter == null) ? null : meter.getModem());
	        billWrong.setWriteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
	        billWrong.setActiveEnergy(lastDayEM.getValue());
	        billWrong.setActiveEnergyImport(lastDayEM.getValue());
	        billWrong.setPrevActiveEnergy(lastBilling.getActiveEnergy());
	        billWrong.setPrevActiveEnergyImport(lastBilling.getActiveEnergyImport());
	        billWrong.setPrevyyyymmddhh(lastBilling.getYyyymmdd()+lastBilling.getId().getHhmmss().substring(0, 2));
	        billWrong.setLastBillingDate(DateTimeUtil.getDateString(new Date()));
	        billWrong.setIntervalDay(Integer.parseInt(calculateDiffDays(lastDayEM.getYyyymmdd(), lastBilling.getYyyymmdd())));
	        billWrong.setDescr(code.getDesc());
	
	        billingBlockTariffWrongDao.saveOrUpdate(billWrong);
        
	        log.info(" [SaveBillingBlockTariffWrong] + Code[" + billWrong.getCode() + "] MeterId[" + billWrong.getMDevId() + "] BillDay[" + billWrong.getYyyymmdd());
    	}catch (Exception e) {
    		log.error(e, e);
		}
	}
    
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
    
    private String calculateDiffDays(String FirstDiff, String SecondDiff) throws ParseException {
    	SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
    	
    	// date1, date2 두 날짜를 parse()를 통해 Date형으로 변환
		Date FirstDate = sd.parse(FirstDiff);
		Date SecondDate = sd.parse(SecondDiff);
		long calDateDays = 0;
		calDateDays = Math.abs(FirstDate.getTime() - SecondDate.getTime());
		// FirstDiff와 SecondDiff간의 시간차이를 비교하여 일수로 반환
		long diffDays = TimeUnit.DAYS.convert(calDateDays, TimeUnit.MILLISECONDS);
    	
		return Long.toString(diffDays);
    }
    
    private String calculateNextDays(String yyyymmdd, int add) {
		LocalDate localDate = LocalDate.of(Integer.parseInt(yyyymmdd.substring(0,4)), Integer.parseInt(yyyymmdd.substring(4,6)), Integer.parseInt(yyyymmdd.substring(6,8)));
        LocalDate newDate = localDate.plusDays(add);
		return newDate.format(DateTimeFormatter.BASIC_ISO_DATE);
    }
    
   private String maxDayOfMonth(String yyyymmdd) {
	   LocalDate localDate = LocalDate.of(Integer.parseInt(yyyymmdd.substring(0,4)), Integer.parseInt(yyyymmdd.substring(4,6)), Integer.parseInt(yyyymmdd.substring(6,8)));
       Calendar cal = Calendar.getInstance();
       cal.set(localDate.getYear(), localDate.getMonthValue()-1, localDate.getDayOfMonth()); //월은 -1해줘야 해당월로 인식
       String maxDate = Integer.toString(localDate.getYear()) + String.format("%02d", localDate.getMonthValue()) + String.format("%02d", cal.getActualMaximum(Calendar.DAY_OF_MONTH));
       return maxDate;
   }
   
   private List<TariffEM> getTariffEMList(Contract contract, String YYYYMMDD){
	   //해당 Tariff 정보를 가져온다.
	   TariffType tariffType = tariffTypeDao.get(contract.getTariffIndexId());
	   Integer tariffTypeCode = tariffType.getCode();
	
	   Map<String, Object> tariffParam = new HashMap<>();
	   tariffParam.put("tariffTypeCode", tariffTypeCode);
	   tariffParam.put("tariffIndex", tariffType);
	   List<TariffEM> tariffEMList = tariffEMDao.getApplyedTariff(tariffParam);
	   
	   return tariffEMList;
   }
   
   private BillingBlockTariff makeNewBillingBlockTariff(Contract contract) {
	   BillingBlockTariff billingBlockTariff = new BillingBlockTariff();
	   billingBlockTariff.setMDevId(contract.getPreMdsId());
	   billingBlockTariff.setMDevType(DeviceType.Meter.name());
	   billingBlockTariff.setContract(contract);
	   billingBlockTariff.setAccumulateBill(0.0);
	   billingBlockTariff.setAccumulateUsage(0.0);
	   billingBlockTariff.setActiveEnergy(0.0);
	   billingBlockTariff.setActiveEnergyImport(0.0);
	   billingBlockTariff.setActiveEnergyExport(0.0);
	   billingBlockTariff.setUsage(0.0);
	   billingBlockTariff.setBill(0.0);
	   billingBlockTariff.setBalance(contract.getCurrentCredit() != null ? contract.getCurrentCredit() : 0.0);
	   billingBlockTariff.setYyyymmdd(contract.getContractDate() != null ? contract.getContractDate().substring(0, 8) : contract.getMeter().getInstallDate().substring(0, 8));
	   billingBlockTariff.setHhmmss(contract.getContractDate() != null ? contract.getContractDate().substring(8, 14) : contract.getMeter().getInstallDate().substring(8, 14));
	   return billingBlockTariff;
   }
   
   private BillingBlockTariff makeNewMonthBillingBlockTariff(BillingBlockTariff lastIndex, String yyyymmdd) {
	   BillingBlockTariff billingBlockTariff = new BillingBlockTariff();
	   billingBlockTariff.setAccumulateBill(0.0);
	   billingBlockTariff.setAccumulateUsage(0.0);
	   billingBlockTariff.setActiveEnergy(lastIndex.getActiveEnergy());
	   billingBlockTariff.setActiveEnergyImport(lastIndex.getActiveEnergyImport());
	   billingBlockTariff.setUsage(0.0);
	   billingBlockTariff.setBill(0.0);
	   billingBlockTariff.setBalance(lastIndex.getBalance());
	   billingBlockTariff.setYyyymmdd(yyyymmdd);
	   return billingBlockTariff;
	}
}