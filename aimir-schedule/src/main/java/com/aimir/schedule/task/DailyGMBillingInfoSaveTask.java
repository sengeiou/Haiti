package com.aimir.schedule.task;

import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.aimir.constants.CommonConstants.DefaultChannel;
import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.mvm.BillingDayGMDao;
import com.aimir.dao.mvm.DayGMDao;
import com.aimir.dao.mvm.SeasonDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.dao.system.TariffGMDao;
import com.aimir.dao.system.TariffTypeDao;
import com.aimir.model.device.Meter;
import com.aimir.model.mvm.BillingDayGM;
import com.aimir.model.mvm.DayGM;
import com.aimir.model.system.Code;
import com.aimir.model.system.Contract;
import com.aimir.model.system.TariffGM;
import com.aimir.model.system.TariffType;
import com.aimir.util.Condition;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.Condition.Restriction;
import com.aimir.util.TimeUtil;

@Transactional
public class DailyGMBillingInfoSaveTask  extends ScheduleTask{
	
	private static Log log = LogFactory.getLog(DailyGMBillingInfoSaveTask.class);
	
    private static final String SERVICE_TYPE_GM = "GM";
    private static final String SEARCH_DATE_TYPE_DAILY = "1";
	
	@Autowired
	HibernateTransactionManager transactionManager;
	
	@Autowired
	ContractDao contractDao;
	
	@Autowired
	CodeDao codeDao;
	
	@Autowired
	SeasonDao seasonDao;
	
	@Autowired
	MeterDao meterDao;
	
	@Autowired
	DayGMDao dayGMDao;
	
	@Autowired
	TariffGMDao tariffGMDao;	
	
	@Autowired
	TariffTypeDao tariffTypeDao;
	
	@Autowired
	BillingDayGMDao billingDayGMDao;

	public void execute(JobExecutionContext context) {
	    log.info("########### START DailyGMBillingInfo ###############");
	    
        // 일별 가스 요금 정보 등록
        this.saveGmBillingDayInfo();
	    
	    log.info("########### END DailyGMBillingInfo ############");
	}//execute end
    
    public void saveGmBillingDayInfo() {

		// 가스 계약 정보 취득
		List<Integer> gm_contractIds = this.getContractInfos(SERVICE_TYPE_GM);
        for(Integer contract_id : gm_contractIds) {
        	// 계약 정보 취득
        	Contract contract = contractDao.get(contract_id);
        	Code code = codeDao.get(contract.getCreditTypeCodeId());
        	if(Code.PREPAYMENT.equals(code.getCode()) || Code.EMERGENCY_CREDIT.equals(code.getCode())) { // 선불 요금일 경우
        		this.saveGmBillingDayWithTariffGM(contract);
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

		// 가스
		contractIds = dayGMDao.getContractIds(conditionMap);
		
		return contractIds;
    }
	
	 public Double saveGmBillingDayWithTariffGM(Contract contract) {
			double usage = 0d;
			Double bill = 0d;
			Double usageUnitPrice = 0d;
			Double[] dataValue = null;
			BillingDayGM _billingDayGM = null;
	        String saveReadFromDateYYYYMMDDHHMMSS = null;
	        String saveReadToDateYYYYMMDDHHMMSS = null;

	        TransactionStatus txStatus = null;
	        DefaultTransactionDefinition txDefine = new DefaultTransactionDefinition();
	        txDefine.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);

	    	// 요금 정보 취득
	        String today = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMdd").substring(0,8);
	        TariffType tariffType = tariffTypeDao.get(contract.getTariffIndexId());
	    	Integer tariffTypeCode = tariffType.getCode();

			Map<String,Object> tariffParam = new HashMap<String,Object>();

			tariffParam.put("tariffTypeCode", tariffTypeCode);
			tariffParam.put("searchDate", today);
			tariffParam.put("seasonId", seasonDao.getSeasonByMonth(today.substring(4, 6)).getId());
	    	TariffGM tariffGM = tariffGMDao.getApplyedTariff(tariffParam);
	    	
	        // 가장 최근에 갱신한 일별 빌링 정보 취득
	        Map<String, Object> map = billingDayGMDao.getLast(contract.getId());

	        BillingDayGM billingDayGM = new BillingDayGM();
	        billingDayGM.setContract(contract);
	        
	        Meter meter = meterDao.get(contract.getMeterId());
	        
	        try{

		        txStatus = transactionManager.getTransaction(txDefine);
		        // 사용량 읽은 마지막 날짜 취득
		        String readToDate = TimeUtil.getCurrentDay() + "000000";
		        saveReadFromDateYYYYMMDDHHMMSS = TimeUtil.getCurrentDay() + "000000";
		        if(map != null) { // 빌링에 정보가 없을때는 가장 최근의(오늘) DayEM으로 부터 빌링정보를 등록한다.
			        // 사용량 읽은 마지막 날짜 취득
		        	if((String)map.get("usageReadToDate") != null) {
		        		readToDate = (String)map.get("usageReadToDate");
		        		saveReadFromDateYYYYMMDDHHMMSS = TimeUtil.getPreHour(readToDate, -1);
		        	}
		        }

				// 일별 가스 사용량 취득
				Set<Condition> param = new HashSet<Condition>();
		        param.add(new Condition("id.mdevType", new Object[]{DeviceType.Meter}, null, Restriction.EQ));
		        param.add(new Condition("id.mdevId", new Object[]{meter.getMdsId()}, null, Restriction.EQ));
		        param.add(new Condition("id.channel", new Object[]{DefaultChannel.Usage.getCode()}, null, Restriction.EQ));
		        param.add(new Condition("id.dst", new Object[]{0}, null, Restriction.EQ));
		        param.add(new Condition("id.yyyymmdd", new Object[]{readToDate.substring(0,8)}, null, Restriction.GE));
		        param.add(new Condition("id.yyyymmdd", new Object[]{readToDate.substring(0,8)}, null, Restriction.ORDERBYDESC));
				List<DayGM> dayGM = dayGMDao.getDayGMsByListCondition(param);
		
		        String readToDateYYYYMMDD = readToDate.substring(0,8);
		        String readFromDateHH = saveReadFromDateYYYYMMDDHHMMSS.substring(8,10);
		        String saveReadFromDateHH = null;
		    	String saveReadToDateHH = "23";

//	        	saveReadFromDateYYYYMMDDHHMMSS = TimeUtil.getPreHour(readToDate, -1);
		        boolean flg = true;
		        
		    	if(dayGM.size() != 0) {
		    		for(int i=0; i<dayGM.size(); i++) {
		    			dataValue =  this.getDayValue24(dayGM.get(i));
		    			billingDayGM.setYyyymmdd(dayGM.get(i).getYyyymmdd());
		    			List<BillingDayGM> list_billingDayGM = billingDayGMDao.getBillingDayGMs(billingDayGM, null, null);
		    			Double dailyBill = list_billingDayGM.size() != 0 ? list_billingDayGM.get(0).getBill() : 0d;

		    			// 마지막 읽은 날의 남은 시간에 대한 사용량을 더한다.
		    			// 예) 10일 22시까지 읽었으면 이번에는 10일 23시의 사용량부터 계산하도록 하기 위해서
		    			if(readToDateYYYYMMDD.equals(dayGM.get(i).getYyyymmdd())) { 
		    				for (int j = 0; j < dataValue.length; j++) {

		    					if(Integer.parseInt(readFromDateHH) <= j) {
		    						usage = usage + dataValue[j];
		    						if(flg) {
			    						saveReadFromDateHH = readFromDateHH;
			    						flg = false;
		    						}
		    					}
							}
		    				flg = true;
		    				saveReadFromDateYYYYMMDDHHMMSS = dayGM.get(i).getYyyymmdd() + saveReadFromDateHH + "0000";
		    			} else { // 마지막 읽은 날짜와 같지 않을 경우는 전체 사용량을 읽는다.
		    				usage = usage + dayGM.get(i).getTotal();
		    				saveReadFromDateYYYYMMDDHHMMSS = dayGM.get(i).getYyyymmdd() + "000000";
		    			}
		    			// 가장 최근 데이터일 경우, 언제까지 사용량을 읽었는지 계산한다.
						for(int k=dataValue.length -1; k>=0; k--) {
							if(dataValue[k] != 0.0) {
								saveReadToDateHH = (String.valueOf(k).length() == 1 ? "0"+k : String.valueOf(k)); // 마지막 시간 취득
								break;
							}
						}
						saveReadToDateYYYYMMDDHHMMSS = dayGM.get(i).getYyyymmdd() + saveReadToDateHH + "0000";

		    			Code code = codeDao.get(contract.getCreditTypeCodeId());

		                // 선후불 요금제가 동일 할 경우는 if문에 의한 분기가 필요없다. 현재는 선불요금제가 명확하지 않아서 if문에 의해 분기를 해 놓은 상태임
		            	if(Code.POSTPAY.equals(code.getCode())) { // 후불 요금일 경우
		            		usageUnitPrice = (tariffGM==null||tariffGM.getUsageUnitPrice()==null?0.0:tariffGM.getUsageUnitPrice());
		            		bill = usage * usageUnitPrice;
		            	} else { // 선불 요금일 경우
		            		usageUnitPrice = (tariffGM==null||tariffGM.getUsageUnitPrice()==null?0.0:tariffGM.getUsageUnitPrice());
		            		bill = usage * usageUnitPrice;
		            	}
		
		    			// Billing_Day_Gm에 정보 등록
		    	    	if(list_billingDayGM.size() != 0) {
		    	    		_billingDayGM = list_billingDayGM.get(0);   	    		
		    	    	} else {
		    	    		_billingDayGM = new BillingDayGM();
		    	    	}
		    	    	
		        		String mdsId = (meter == null) ? null : meter.getMdsId();

		        		_billingDayGM.setYyyymmdd(saveReadToDateYYYYMMDDHHMMSS.substring(0,8));
		        		_billingDayGM.setHhmmss(list_billingDayGM.size() != 0 ? list_billingDayGM.get(0).getHhmmss() : "000000");
		        		_billingDayGM.setMDevId(mdsId);
		        		_billingDayGM.setMDevType(DeviceType.Meter.name());
		        		_billingDayGM.setBill(dailyBill + bill);
		        		_billingDayGM.setUsage(usage);
		        		_billingDayGM.setContract(contract);
		        		_billingDayGM.setSupplier(contract.getSupplier());
		        		_billingDayGM.setLocation(contract.getLocation());
		        		_billingDayGM.setMeter(meter);
		        		_billingDayGM.setModem((meter == null) ? null : meter.getModem());
		        		_billingDayGM.setWriteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
		        		_billingDayGM.setUsageReadFromDate(saveReadFromDateYYYYMMDDHHMMSS);
		        		_billingDayGM.setUsageReadToDate(saveReadToDateYYYYMMDDHHMMSS);
		        		billingDayGMDao.saveOrUpdate(_billingDayGM);
		    		}
		    		// contract 정보 갱신
		    		Code code = codeDao.get(contract.getCreditTypeCodeId());
		    		if(Code.PREPAYMENT.equals(code.getCode())) { // 선불 요금일 경우
		    		    contract.setCurrentCredit(contract.getCurrentCredit() - bill); // 현재 잔액에서 사용요금 뺀다
		    			contractDao.saveOrUpdate(contract);
		    		}
		    		transactionManager.commit(txStatus);
		    	}
	        }catch(ParseException e) {
	        	e.printStackTrace();
	        	transactionManager.rollback(txStatus);
	        }
			return bill;
		}

		  /**
	     * method name : getDayValue24
	     * method Desc : GM
	     *
	     * @param meteringMonth
	     * @return
	     */
	    private Double[] getDayValue24(DayGM dayGm) {

	        Double[] dayValues = new Double[24];

	        dayValues[0] = (dayGm.getValue_00() == null ? 0 : dayGm.getValue_00());
	        dayValues[1] = (dayGm.getValue_01() == null ? 0 : dayGm.getValue_01());
	        dayValues[2] = (dayGm.getValue_02() == null ? 0 : dayGm.getValue_02());
	        dayValues[3] = (dayGm.getValue_03() == null ? 0 : dayGm.getValue_03());
	        dayValues[4] = (dayGm.getValue_04() == null ? 0 : dayGm.getValue_04());
	        dayValues[5] = (dayGm.getValue_05() == null ? 0 : dayGm.getValue_05());
	        dayValues[6] = (dayGm.getValue_06() == null ? 0 : dayGm.getValue_06());
	        dayValues[7] = (dayGm.getValue_07() == null ? 0 : dayGm.getValue_07());
	        dayValues[8] = (dayGm.getValue_08() == null ? 0 : dayGm.getValue_08());
	        dayValues[9] = (dayGm.getValue_09() == null ? 0 : dayGm.getValue_09());
	        dayValues[10] = (dayGm.getValue_10() == null ? 0 : dayGm.getValue_10());
	        dayValues[11] = (dayGm.getValue_11() == null ? 0 : dayGm.getValue_11());
	        dayValues[12] = (dayGm.getValue_12() == null ? 0 : dayGm.getValue_12());
	        dayValues[13] = (dayGm.getValue_13() == null ? 0 : dayGm.getValue_13());
	        dayValues[14] = (dayGm.getValue_14() == null ? 0 : dayGm.getValue_14());
	        dayValues[15] = (dayGm.getValue_15() == null ? 0 : dayGm.getValue_15());
	        dayValues[16] = (dayGm.getValue_16() == null ? 0 : dayGm.getValue_16());
	        dayValues[17] = (dayGm.getValue_17() == null ? 0 : dayGm.getValue_17());
	        dayValues[18] = (dayGm.getValue_18() == null ? 0 : dayGm.getValue_18());
	        dayValues[19] = (dayGm.getValue_19() == null ? 0 : dayGm.getValue_19());
	        dayValues[20] = (dayGm.getValue_20() == null ? 0 : dayGm.getValue_20());
	        dayValues[21] = (dayGm.getValue_21() == null ? 0 : dayGm.getValue_21());
	        dayValues[22] = (dayGm.getValue_22() == null ? 0 : dayGm.getValue_22());
	        dayValues[23] = (dayGm.getValue_23() == null ? 0 : dayGm.getValue_23());

	        return dayValues;
	    }

}