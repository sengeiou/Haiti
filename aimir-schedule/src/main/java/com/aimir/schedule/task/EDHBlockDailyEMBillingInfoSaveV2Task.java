package com.aimir.schedule.task;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
import com.aimir.dao.mvm.DayEMDao;
import com.aimir.dao.mvm.LpEMDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.dao.system.PrepaymentLogDao;
import com.aimir.dao.system.TariffEMDao;
import com.aimir.dao.system.TariffTypeDao;
import com.aimir.fep.util.DataUtil;
import com.aimir.model.device.Meter;
import com.aimir.model.mvm.BillingBlockTariff;
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
    PrepaymentLogDao prepaymentLogDao;

    @Autowired
    LpEMDao lpEMDao;

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
    	
    	try {
	    	for (int i = 0; i < EM_ContractList.size(); i++) {
	    		Contract contract = EM_ContractList.get(i);
	    		
	                if (contract.getTariffIndexId() == null || contract.getCustomerId() == null || contract.getMeterId() == null)
	                	break;                	
	                log.info("Contract[" + contract.getContractNumber() + "] Meter[" + contract.getMeter().getMdsId() + "]");
	                this.saveEmBillingDailyWithTariffEMCumulationCost(contract);
	    	}
        }catch (Exception e) {
            log.error("saveEmBillingDayInfo Exception ==> " + e);
            txManager.rollback(txStatus);
        }finally {
//            	if(i < EM_ContractList.size())
//            		continue;
		}
    		
    	if (txStatus != null) {
    		txManager.commit(txStatus);
		}
    }

    private void saveEmBillingDailyWithTariffEMCumulationCost(Contract contract) throws Exception {
    	TransactionStatus txStatus = txManager.getTransaction(null);
    	try {
    		String mdsId = meterDao.get(contract.getMeterId()).getMdsId();
            DayEM lastDayEM = null;

            //마지막 누적요금이 저장된 데이터를 가지고 온다.
            BillingBlockTariff lastBilling = getLastAccumulateBill(mdsId);
            
            if(lastBilling == null) {
            	// 마지막 누적 요금이 저장된 billingDayEM이 없을 경우 한번도 선불스케줄을 돌리지 않은것으로 간주
            	lastBilling = makeNewBillingBlockTariff(contract);
            }
            log.info("1. Contract[" + contract.getContractNumber() + "] "
        			+ " lastAccumulateBill[" + convertBigDecimal(lastBilling.getAccumulateBill()) + "] "
        			+ " lastAccumulateUsage[" + convertBigDecimal(lastBilling.getAccumulateUsage()) + "] "
        			+ " lastAccumulateDate[" + lastBilling.getYyyymmdd().toString() + "] "
        			+ " mdsId[" + mdsId + "] ");
            
            lastDayEM = getDayEM(mdsId, lastBilling.getYyyymmdd());	//최근 DAY_EM 조회
            
            // Day_EM value가 BillingBlcok activeEnergy 보다 클떄 진행하고 작으면 Wrong 테이블 저장
            if(lastDayEM.getValue() < lastBilling.getActiveEnergy() /*|| lastDayEM.getValue() > lastBilling.getActiveEnergy() * METERING_MULTIPLE_NUMBER*/) {
            	//BILLING_BLOCK_TARIFF_WRONG 이력 남기고 리턴하여 for문으로 다시 돌아감
            	
//            	saveBillingBlockTariffWrong(contract.getMeter(), sequenceBillings.get(i).getUsage(), sequenceBillings.get(i).getYyyymmdd(), sequenceBillings.get(i).getBill(), sequenceBillings.get(i-1).getBill());
            	return;
            }
            	
        	// 마지막 시간이 현재 시간보다 작으면 월 기준으로 선불 계산한다.
        	// compareTo - lastAccumulateDate가 lastDayEM 날짜 보다 작으면 음수 반환, 크면 양수 반환, 같으면 0 리턴
        	if (lastBilling.getYyyymmdd().compareTo(lastDayEM.getYyyymmdd()) < 0) {
        		Meter meter = meterDao.findByCondition("id", contract.getMeterId());
        		
        		// DAY_EM 마지막 날짜와 lastAccumulateDate간의 시간차이를 비교하여 일수로 반환
        		long diffDays = Long.parseLong(calculateDiffDays(lastDayEM.getYyyymmdd(), lastBilling.getYyyymmdd()));
        		log.info("#### diffDays : "+ diffDays+ " ####");
        		//해당 TariffEM 정보를 가져온다.
        		List<TariffEM> tariffEMList = getTariffEMList(contract, lastBilling.getYyyymmdd());
        		LinkedList<BillingBlockTariff> sequenceBillings = new LinkedList<BillingBlockTariff>();
        		sequenceBillings.add(lastBilling);	// 마지막 BillingBlockTariff 추가
        		
        		// BillingBlockTariff low 생성
        		sequenceBillings = gatherBillingBlock(sequenceBillings, diffDays, tariffEMList, contract, meter, lastDayEM);
        		
                // Contract 잔액을 차감한다.
        		BigDecimal currentCredit = convertBigDecimal(contract.getCurrentCredit());
        		for (int i = 1; i < sequenceBillings.size(); i++) {
        			BigDecimal bill = convertBigDecimal(sequenceBillings.get(i).getBill());
        			saveBillingBlockTariff(contract, meter, sequenceBillings.get(i));
        			contract.setCurrentCredit(currentCredit.subtract(bill).doubleValue());
        			log.info("[Update CurrentCredit Contract:"+ contract.getContractNumber() + " ] MeterId[" + meter.getMdsId() + "] yyyymmdd[" + lastDayEM.getYyyymmdd() + "] "
        					+ "==> BlockBill[" + sequenceBillings.get(i).getBill() + "] lastAccumulateBill[" + sequenceBillings.get(i-1).getBill()+ "]");
    			}
        		contractDao.update(contract);
        	}
		} catch (Exception e) {
			log.error("saveEmBillingDaily Exception ==> Contract number = " + contract.getContractNumber(), e);
			txManager.rollback(txStatus);
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
 	    	log.info("#### 구간 정산  for loop [ i : "+ i + ", compareCnt : " + compareCnt + "] ####");
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
 					sequenceBillings.add(makeBillings(lastIndex, usage, maxDay, meter, contract.getTariffIndex().getName(), tariffEMList, true));
 				}
 				
 				lastIndex = sequenceBillings.getLast();
 				remainBillingDay = Integer.toString(BILLING_STANDARDS_DATE - Integer.parseInt(remainBillingDay));	//기준일 10일에서 말일 차이를 뺀다
 				nextBillingDay = calculateNextDays(lastIndex.getYyyymmdd(), Integer.parseInt(remainBillingDay));	//이전 BillingBlockTariff 날짜에서 다음 정산일을 구한다
 				BillingBlockTariff firstIndex = makeNewtMonthBillingBlockTariff(lastIndex, nextBillingDay);			//달이 바뀌면서 BillingBlockTariff 초기화
 				usage = avgDayUsage.multiply(convertBigDecimal(remainBillingDay));									//남은 일수만큼의 일평균	
 				sequenceBillings.add(makeBillings(firstIndex, usage, nextBillingDay, meter, contract.getTariffIndex().getName(), tariffEMList, true));
 			}
 			// 다음 정산일과 이전 BillingBlockTariff 날짜 월이 같으면
 			else {
 				lastIndex = sequenceBillings.getLast();
 				nextBillingDay = calculateNextDays(lastIndex.getYyyymmdd(), BILLING_STANDARDS_DATE);	// 다음 정산일
 				BigDecimal usage = avgDayUsage.multiply(convertBigDecimal(BILLING_STANDARDS_DATE));		// 일평균
 				sequenceBillings.add(makeBillings(lastIndex, usage, nextBillingDay, meter, contract.getTariffIndex().getName(), tariffEMList, true));
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
 				sequenceBillings.add(makeBillings(lastIndex, usage, maxDay, meter, contract.getTariffIndex().getName(), tariffEMList, true));
 			}
 			
 			lastIndex = sequenceBillings.getLast();
 			remainBillingDay = calculateDiffDays(lastDayEM.getYyyymmdd(), lastIndex.getYyyymmdd());
 			BillingBlockTariff firstIndex = makeNewtMonthBillingBlockTariff(lastIndex, remainBillingDay);					//달이 바뀌면서 BillingBlockTariff 초기화
 			usage = convertBigDecimal(lastDayEM.getValue()).subtract(convertBigDecimal(firstIndex.getActiveEnergy()));		//Day_EM의 마지막 값에서 이전 BillingBlockTariff ActiveEnergy를 뺀 값으로 저장
 			sequenceBillings.add(makeBillings(firstIndex, usage, lastDayEM.getYyyymmdd(), meter, contract.getTariffIndex().getName(), tariffEMList, false));
 		}
 		// 다음 정산일과 이전 BillingBlockTariff 날짜 월이 같으면	
 		else {
 			lastIndex = sequenceBillings.getLast();
 			BigDecimal usage = convertBigDecimal(lastDayEM.getValue()).subtract(convertBigDecimal(lastIndex.getActiveEnergy()));
 			sequenceBillings.add(makeBillings(lastIndex, usage, lastDayEM.getYyyymmdd(), meter, contract.getTariffIndex().getName(), tariffEMList, false));
 		}
    
 		return sequenceBillings;
    }

    private BillingBlockTariff makeBillings(BillingBlockTariff lastIndex, BigDecimal usage, String date, Meter meter, String tariffName, List<TariffEM> tariffEMList, Boolean isAVG) {
    	BillingBlockTariff bill = new BillingBlockTariff();
    	bill.setYyyymmdd(date);
    	bill.setHhmmss("000000");
    	bill.setUsage(usage.doubleValue());
    	bill.setActiveEnergy(convertBigDecimal(lastIndex.getActiveEnergy()).add(usage).doubleValue());			// Previous ACTIVEENERGY + USAGE
    	bill.setActiveEnergyImport(convertBigDecimal(lastIndex.getActiveEnergy()).add(usage).doubleValue());	// Previous ACTIVEENERGY + USAGE
    	bill.setAccumulateUsage(convertBigDecimal(lastIndex.getAccumulateUsage()).add(usage).doubleValue());	// Previous ACCUMULATEUSAGE + USAGE
    	// Tariff 구간 요금 계산
    	BigDecimal accumulateBill = blockBill(meter.getMdsId(), tariffName, tariffEMList, convertBigDecimal(bill.getAccumulateUsage()));	//BlockTariff
    	bill.setAccumulateBill(accumulateBill.doubleValue());
    	BigDecimal billingBill = accumulateBill.subtract(convertBigDecimal(lastIndex.getAccumulateBill()));
    	bill.setBill(accumulateBill.subtract(convertBigDecimal(lastIndex.getAccumulateBill())).doubleValue());	// monthBill - Previous ACCUMULATEBILL
    	bill.setBalance(convertBigDecimal(lastIndex.getBalance()).subtract(billingBill).doubleValue());
    	bill.setContractId(lastIndex.getContractId());
    	bill.setAvg(isAVG);

		return bill;
	}
    
    public BigDecimal blockBill(String mdsId, String tariffName, List<TariffEM> tariffEMList, BigDecimal totalUsage) {
    	BigDecimal returnBill = new BigDecimal(0);
    	// 매월 기본요금 부과
    	BigDecimal serviceCharge = convertBigDecimal(tariffEMList.get(0).getServiceCharge());
    	
    	Collections.sort(tariffEMList, new Comparator<TariffEM>() {

            @Override
            public int compare(TariffEM t1, TariffEM t2) {
                return t1.getSupplySizeMin() < t2.getSupplySizeMin()? -1:1;
            }
        });
    	
    	BigDecimal activeEnergyCharge_T0 = convertBigDecimal(tariffEMList.get(0).getActiveEnergyCharge());
    	BigDecimal activeEnergyCharge_T1 = convertBigDecimal(tariffEMList.get(1).getActiveEnergyCharge());
    	BigDecimal activeEnergyCharge_T2 = convertBigDecimal(tariffEMList.get(2).getActiveEnergyCharge());
    	BigDecimal activeEnergyCharge_T3 = convertBigDecimal(tariffEMList.get(3).getActiveEnergyCharge());
    	
    	BigDecimal supplySizeMin_T0 = convertBigDecimal(tariffEMList.get(0).getSupplySizeMin());
    	BigDecimal supplySizeMin_T1 = convertBigDecimal(tariffEMList.get(1).getSupplySizeMin());
    	BigDecimal supplySizeMin_T2 = convertBigDecimal(tariffEMList.get(2).getSupplySizeMin());
    	BigDecimal supplySizeMin_T3 = convertBigDecimal(tariffEMList.get(3).getSupplySizeMin());
    	
    	BigDecimal supplySizeMax_T0 = convertBigDecimal(tariffEMList.get(0).getSupplySizeMax());
    	BigDecimal supplySizeMax_T1 = convertBigDecimal(tariffEMList.get(1).getSupplySizeMax()).subtract(supplySizeMax_T0);
    	BigDecimal supplySizeMax_T2 = convertBigDecimal(tariffEMList.get(2).getSupplySizeMax());

        if ("Public Organization".equals(tariffName) || "Autonomous Organization".equals(tariffName)) {
//            returnBill = totalUsage.multiply(activeEnergyCharge_T0).add(serviceCharge);
            returnBill = totalUsage.multiply(activeEnergyCharge_T0);
        } else {
//            returnBill = serviceCharge;  기본요금 매일 5HTG에서 매월 1일 150HTG 차감으로 변경
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
//                	log.info("30이하 returnBill : "+returnBill + " supplySizeMax_T1 : " + supplySizeMax_T1 + " activeEnergyCharge_T1 : " + activeEnergyCharge_T1);
                    returnBill = (supplySizeMax_T2.subtract(supplySizeMin_T2)).multiply(activeEnergyCharge_T2).add(returnBill);
//                    log.info("200이하 returnBill : "+returnBill + " supplySizeMin_T2 : " + supplySizeMin_T2 + " activeEnergyCharge_T2 : " + activeEnergyCharge_T2);
                    returnBill = (totalUsage.subtract(supplySizeMin_T3)).multiply(activeEnergyCharge_T3).add(returnBill);
//                    log.info("200 이상 returnBill : "+returnBill + " supplySizeMin_T3 : " + supplySizeMin_T3 + " activeEnergyCharge_T3 : " + activeEnergyCharge_T3);
                }
            }
        }

//        BigDecimal tca = returnBill.multiply(new BigDecimal("0.1"));		//TCA는 billing의 10%
//        BigDecimal frais = returnBill.multiply(new BigDecimal("0.01"));		//Fraise Special은 billing의 1%
        
//        log.debug("4. BlockBill1 Meter[" + mdsId + "] Usage[" + totalUsage + "] Bill[" + returnBill + "] TCA[" + tca + "] Frais[" + frais + "] Total Bill[" + returnBill.add(tca).add(frais) + "]");
        log.debug("4. BlockBill Meter[" + mdsId + "] ACCUMULATEUSAGE[" + totalUsage + "] Bill[" + returnBill + "] Total Bill[" + returnBill + "]");
        
//        return returnBill.add(tca).add(frais);
        return returnBill.setScale(2, BigDecimal.ROUND_HALF_UP);	//소수점 2자리 까지
    }
    
    private void saveBillingBlockTariff(Contract contract, Meter meter, BillingBlockTariff billingBlockTariff) {
    	TransactionStatus txstatus = null;
    	try {
            txstatus = txManager.getTransaction(null);
	        BillingBlockTariff bill = new BillingBlockTariff();
	
	        bill.setMDevId(meter.getMdsId());
	        bill.setYyyymmdd(billingBlockTariff.getYyyymmdd());
	        bill.setHhmmss("000000");
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
	
	//        billingBlockTariffDao.saveOrUpdate(bill);
	        billingBlockTariffDao.add(bill);
        
	        log.info("6. [SaveBillingBlockTariff] MeterId[" + bill.getMDevId() + "] BillDay[" + bill.getYyyymmdd() +
                "] BillTime[" + bill.getHhmmss() + "] AccumulateUsage[" + bill.getAccumulateUsage() +
                "] AccumulateBill[" + bill.getAccumulateBill() + "] CurrentBill[" + bill.getBill() + "]");
    	}catch (Exception e) {
    		log.error(e, e);
            if (txstatus != null) txManager.rollback(txstatus);
		}
    }
    
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
    
    public BillingBlockTariff getLastAccumulateBill(String meterId) {
    	TransactionStatus txstatus = null;
        try {
            txstatus = txManager.getTransaction(null);
            LinkedHashSet<Condition> condition = new LinkedHashSet<Condition>();
            condition.add(new Condition("id.mdevId", new Object[]{meterId}, null, Restriction.EQ));
            condition.add(new Condition("id.mdevType", new Object[]{DeviceType.Meter}, null, Restriction.EQ));
//            condition.add(new Condition("id.yyyymmdd", new Object[]{}, null, Restriction.MAX));
            
            List<BillingBlockTariff> ret = billingBlockTariffDao.findByConditions(condition);
            BillingBlockTariff lastBillingBlockTariff = null;
            if(ret.size() != 0) {
            	lastBillingBlockTariff = ret.get(0);
                for (int i = 0; i < ret.size(); i++) {
                    if (lastBillingBlockTariff.getYyyymmdd().compareTo(ret.get(i).getYyyymmdd()) < 0) {
                    	lastBillingBlockTariff = ret.get(i);
                    }
                }
             }
            
            txManager.commit(txstatus);
            return lastBillingBlockTariff;
        }catch (ArrayIndexOutOfBoundsException e) {
            log.info("BillingBlockTariff does not exist : "+ e);
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
//	   tariffParam.put("searchDate", YYYYMMDD.substring(0 ,6)+"31");
	   List<TariffEM> tariffEMList = tariffEMDao.getApplyedTariff(tariffParam);
	   
	   return tariffEMList;
   }
   
   private BillingBlockTariff makeNewBillingBlockTariff(Contract contract) {
	   BillingBlockTariff billingBlockTariff = new BillingBlockTariff();
	   billingBlockTariff.setMDevId(contract.getPreMdsId());
	   billingBlockTariff.setMDevType(DeviceType.Meter.name());
	   billingBlockTariff.setContract(contract);
	   billingBlockTariff.setAccumulateBill(0.0);;
	   billingBlockTariff.setAccumulateUsage(0.0);;
	   billingBlockTariff.setActiveEnergy(0.0);;
	   billingBlockTariff.setActiveEnergyImport(0.0);;
	   billingBlockTariff.setActiveEnergyExport(0.0);;
	   billingBlockTariff.setUsage(0.0);;
	   billingBlockTariff.setBill(0.0);;
	   billingBlockTariff.setBalance(contract.getCurrentCredit());
	   billingBlockTariff.setYyyymmdd(contract.getContractDate() != null ? contract.getContractDate().substring(0, 8) : contract.getMeter().getInstallDate().substring(0, 8));
	   billingBlockTariff.setHhmmss(contract.getContractDate() != null ? contract.getContractDate().substring(8, 14) : contract.getMeter().getInstallDate().substring(8, 14));
	   return billingBlockTariff;
   }
   
   private BillingBlockTariff makeNewtMonthBillingBlockTariff(BillingBlockTariff lastIndex, String yyyymmdd) {
	   BillingBlockTariff billingBlockTariff = new BillingBlockTariff();
	   billingBlockTariff.setAccumulateBill(0.0);;
	   billingBlockTariff.setAccumulateUsage(0.0);;
	   billingBlockTariff.setActiveEnergy(lastIndex.getActiveEnergy());;
	   billingBlockTariff.setActiveEnergyImport(lastIndex.getActiveEnergyImport());;
	   billingBlockTariff.setUsage(0.0);;
	   billingBlockTariff.setBill(0.0);;
	   billingBlockTariff.setBalance(lastIndex.getBalance());;;
	   billingBlockTariff.setYyyymmdd(yyyymmdd);
	   return billingBlockTariff;
	}
}