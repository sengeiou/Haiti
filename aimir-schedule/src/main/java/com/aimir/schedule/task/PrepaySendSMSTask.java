package com.aimir.schedule.task;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants.WeekDay;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.dao.system.CustomerDao;
import com.aimir.dao.system.LanguageDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.sms.SendSMS;
import com.aimir.model.device.Meter;
import com.aimir.model.system.Contract;
import com.aimir.model.system.Language;
import com.aimir.model.system.Supplier;
import com.aimir.util.CalendarUtil;
import com.aimir.util.DecimalUtil;
import com.aimir.util.TimeUtil;

/**
 * 
 * @author jiae
 * Prepayment Customer Management 가젯에서 [Your balance Alert]에 설정된 정보대로 고객에게 SMS를 전송하는 Task
 * 
 * ECG 
 * 메세질를 보내는 URL - 성공 시 message-id 가 return 됨 (OK: message-id) 
 * sendURL : http://api.smsgh.com/v2/message/send?username=xxx&password=xxx&from=smsgh&to=0101111111&text=hello
 * 
 * 전송성공 여부를 확인하는 URL - (OK: Delivered)
 * queryURL : http://api.smsgh.com/v2/messages/query?username=xxx&password=xxx&message-id=3361287
 * 
 * delivery-Reports URL : http://api.smsgh.com/v2/account/delivery-reports?username=xxx&password=xxx
 *
 */
@Transactional
public class PrepaySendSMSTask extends ScheduleTask {
    
    private static Log log = LogFactory.getLog(PrepaySendSMSTask.class);
    
    @Resource(name="transactionManager")
    HibernateTransactionManager txManager;
    
    @Autowired
    SupplierDao supplierDao;
    
    @Autowired
    CodeDao codeDao;
    
    @Autowired
    ContractDao contractDao;
    
    @Autowired
    CustomerDao customerDao;
    
    @Autowired
    MeterDao meterDao;
    
	@Autowired
	LanguageDao languageDao;
    
    URL url = null;
    String baseURL= null;
    String SMSGHId = null;
    String SMSGHPass = null;
    String msgOriginator = null;
    String mobileNo = null;
    String text = null;
    String clientId = null;
    String time = null;
	Properties messageProp = null;
	 private boolean isNowRunning = false;
	 
    public static void main(String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{"spring-forcrontab.xml"}); 
        DataUtil.setApplicationContext(ctx);
        
        PrepaySendSMSTask task = ctx.getBean(PrepaySendSMSTask.class);
        task.execute(null);
        System.exit(0);
    }

    @Override
    public void execute(JobExecutionContext context) {
		if(isNowRunning){
			log.info("########### PrepaySendSMSTask is already running...");
			return;
		}
		isNowRunning = true;
		
        log.info("########### 1. START prepaySMSSend ###############");
        
        this.ContractInfo();

        log.info("########### 1. END prepaySMSSend ############");
        isNowRunning = false;
    }

	@SuppressWarnings("unused")
	private Properties getMessageProp(Supplier supplier){
		try {
			if(messageProp == null){
				messageProp = new Properties();
				Language la = languageDao.get(supplier.getLangId());
				String lang = (la.getCode_2letter() == null) ? "en" : la.getCode_2letter();
		        InputStream ips = getClass().getClassLoader().getResourceAsStream("lang/message_"+ lang +".properties");
		        if(ips == null){
		        	ips = getClass().getClassLoader().getResourceAsStream("message_en.properties");	        	
		        }
		        messageProp.load(ips);			
			}			
		} catch (Exception e) {
			log.debug(e);
		}
        
        return this.messageProp;
	}
    
    public void ContractInfo() {
        try {
            Map<String, Object> condition = new HashMap<String, Object>();
            condition.put("prepayCreditId", codeDao.getCodeIdByCode("2.2.1"));
            condition.put("emergencyICreditId", codeDao.getCodeIdByCode("2.2.2"));
            condition.put("smsYn", true);
            //sms를 받기로 설정하고 모바일번호를 가지고 있는 선불고객을 검색 _ 계약이 그룹지어 있는지 아닌지 여부를 판단후 각각 조건에 맞게 검색해온다.
            List<Map<String, Object>> contractInfoByNotGroup = contractDao.getContractSMSYNNOTGroup(condition);
            List<Map<String, Object>> contractInfoByGroup = contractDao.getContractSMSYNWithGroup(condition);
            
            log.debug("contractInfoByNotGroup size : " + contractInfoByNotGroup.size());
            log.debug("contractInfoByGroup size : " + contractInfoByGroup.size());
            
            if(contractInfoByNotGroup.size() > 0) {
                this.sendSMSTargetVal(contractInfoByNotGroup); 
                this.sendSMSPeriod(contractInfoByNotGroup);
            }
                
            if(contractInfoByGroup.size() > 0) {
                this.sendSMSTargetVal(contractInfoByGroup); 
            }
            // setSuccessResult();
        } catch (Exception e) {
            // setFailResult();
            log.error(e,e);
        }
    }
    
    /**
     * 
     * @param contractInfo
     * 설정한 Target보다 현재금액이 작을 경우 SMS를 보낸다.
     */
    public void sendSMSTargetVal(List<Map<String, Object>> contractInfo) {
         log.info("########### 1.1 START prepaySMSSend_sendSMSTartgetVal ###############");
        Supplier supplier = null;
        Double currentCredit = 0d;
        TransactionStatus txStatus = null;
        Contract contract = null;
        Double targetVal = null;
        Integer contractSize = contractInfo.size();
        for (int i = 0; i < contractSize; i++) {
            
            try {
            	txStatus = txManager.getTransaction(null);
                contract = contractDao.get(Integer.parseInt(contractInfo.get(i).get("CONTRACTID").toString()));
                if (contract.getMeter() == null) {
                	txManager.commit(txStatus);
                	continue;
                }
                
                Meter meter = meterDao.get(contract.getMeterId());
                String mdsId =meter.getMdsId();
                supplier = supplierDao.get(contract.getSupplierId());
                DecimalFormat cdf = DecimalUtil.getDecimalFormat(supplier.getCd());
                
                targetVal = Double.parseDouble(
                        contractInfo.get(i).get("PREPAYMENTTHRESHOLD") == null ? "0" : contractInfo.get(i).get("PREPAYMENTTHRESHOLD").toString());
                currentCredit = contractInfo.get(i).get("CURRENTCREDIT") == null ? 0d : Double.parseDouble(contractInfo.get(i).get("CURRENTCREDIT").toString());
                /*
                 * You have reached your target credit value. Kindly top up to avoid inconvenience.
                 * Customer Name :
                 * METER ID :
                 * Current Credit :
                 */
//                text = "You have reached your target credit value. Kindly top up to avoid inconvenience."
//                        + "\n Customer Name: " + contractInfo.get(i).get("CUSTOMERNAME")
//                        + "\n METER ID : " + mdsId
//                        // spasa 전용 문자 항목 ecg에서는 contract nr를 사용하지 않음
//                        //+ "\n Contract nr : " + contract.getContractNumber()
//                        + "\n Current Credit : " +  cdf.format(currentCredit);
                
				text = getMessageProp(supplier).getProperty("aimir.sms.balance.threshold.msg")  
						+ "\n " + getMessageProp(supplier).getProperty("aimir.sms.customer.name") + " : " + contractInfo.get(i).get("CUSTOMERNAME")
						+ "\n " + getMessageProp(supplier).getProperty("aimir.meterid") + " : " + mdsId
						+ "\n " + getMessageProp(supplier).getProperty("aimir.sms.credit.current") + " : " +  cdf.format(currentCredit);
                
                if(contractInfo.get(i).get("GROUP_MOBILENO") == null) {
                    mobileNo = contractInfo.get(i).get("MOBILENO").toString().replace("-", "");
                } else {
                    //그룹의 경우 그룹의 대표번호로 SMS를 보낸다.
                    mobileNo = contractInfo.get(i).get("GROUP_MOBILENO").toString().replace("-", "");
                }
                
                txManager.commit(txStatus);
            } catch (Exception e) {
            	txManager.commit(txStatus);
                log.error(e,e);
                continue;
            }
            
            try {
            	txStatus = txManager.getTransaction(null);
                if(targetVal >= currentCredit ) {
                    log.debug("Target Value contractNumber[" + contract.getContractNumber() + "]");
                    log.debug("text messae : " + text);

                    String messageId = notificationSMS(contract);

                    if(!"".equals(messageId)) {
                        contractDao.updateSmsNumber(contract.getId(), messageId);
                    }
                }
                txManager.commit(txStatus);
            } catch (Exception e) {
                if (txStatus != null) {
                    try {
                        txManager.rollback(txStatus);
                    }
                    catch (Exception ee) {}
                }
                log.error(e,e);
            }
        }
        log.info("########### 1.1 END prepaySMSSend_sendSMSTartgetVal ###############");
    }
    
    /**
     * 
     * @param contractInfo
     * 주기에 따라 SMS를 보낸다.
     */
    public void sendSMSPeriod(List<Map<String, Object>> contractInfo) {
        log.info("########### 1.2 START prepaySMSSend_sendSMSPeriod ###############");
        SimpleDateFormat sd = new SimpleDateFormat("yyyyMMddHHmmss");
        String today = sd.format(new Date());
        int yyyy = Integer.parseInt(today.substring(0,4));
        int mm = Integer.parseInt(today.substring(4,6));
        int dd = Integer.parseInt(today.substring(6,8));
        String lang = null;
        Supplier supplier = null;
        Double currentCredit;
        Contract contract = null;
        TransactionStatus txStatus = null;
        Integer notificationInterval = null;
        Integer notificationPeriod = null;
        String lastNotificationDate = null;
        Integer contractSize = contractInfo.size();
        for (int i = 0; i < contractSize; i++) {
            try {
            	txStatus = txManager.getTransaction(null);
                contract = contractDao.get(Integer.parseInt(contractInfo.get(i).get("CONTRACTID").toString()));
                
                if (contract.getMeter() == null) {
                	txManager.commit(txStatus);
                	continue;
                }
                
                Meter meter = meterDao.get(contract.getMeterId());
                String mdsId =meter.getMdsId();
                
                // Meter meter = meterDao.get(contract.getMeter().getId());
                supplier = supplierDao.get(contract.getSupplierId());
                DecimalFormat cdf = DecimalUtil.getDecimalFormat(supplier.getCd());
                
                //notification~ 관련 값들이 null일 경우 default 값으로 실행
                notificationInterval = Integer.parseInt(contractInfo.get(i).get("NOTIFICATIONINTERVAL") == null ? "1" : contractInfo.get(i).get("NOTIFICATIONINTERVAL").toString());
                notificationPeriod = Integer.parseInt(contractInfo.get(i).get("NOTIFICATIONPERIOD") == null ? "1" : contractInfo.get(i).get("NOTIFICATIONPERIOD").toString());
                //Integer notificationTime = Integer.parseInt(contractInfo.get(i).get("NOTIFICATIONTIME") == null ? "13" : contractInfo.get(i).get("NOTIFICATIONTIME").toString());
                
                //마지막 잔액 통보 일자 (yyyyMMddHHmmss)
                lastNotificationDate = contractInfo.get(i).get("LASTNOTIFICATIONDATE") == null ? null : contractInfo.get(i).get("LASTNOTIFICATIONDATE").toString();
                currentCredit = contractInfo.get(i).get("CURRENTCREDIT") == null ? 0d : Double.parseDouble(contractInfo.get(i).get("CURRENTCREDIT").toString());

                /*
                 * Customer Name :
                 * METER ID:
                 * Supply Type :
                 * Current Credit
                 */
//                text = "Customer Name: " + contractInfo.get(i).get("CUSTOMERNAME")
//                        + "\n METER ID : " + mdsId
//                        + "\n Supply Type: " + contractInfo.get(i).get("SERVICETYPE")
//                        + "\n Current Credit : " +  cdf.format(currentCredit);
                
				text = getMessageProp(supplier).getProperty("aimir.sms.customer.name") + " : " + contractInfo.get(i).get("CUSTOMERNAME")
						+ "\n " + getMessageProp(supplier).getProperty("aimir.meterid") + " : " + mdsId
						+ "\n " + getMessageProp(supplier).getProperty("aimir.supply.type") + " : " + contractInfo.get(i).get("SERVICETYPE")
                        + "\n " + getMessageProp(supplier).getProperty("aimir.prepayment.currentcredit") + " : " +  cdf.format(currentCredit);						                
                
                mobileNo = contractInfo.get(i).get("MOBILENO").toString().replace("-", "");
                txManager.commit(txStatus);
            } catch (Exception e) {
            	txManager.commit(txStatus);
                log.error(e,e);
            }
            
            try {
            	txStatus = txManager.getTransaction(null);
            	
                //Period가 Daily일 때 : 1, Weely일때 : 2
                if(notificationPeriod == 1) {
                    //마지막 저장날짜에서 interval만큼 지난 날짜와 오늘 날짜가 일치하는지 체크
                    Integer todayYyyymmdd = Integer.parseInt(today.substring(0,8)); 
                    Integer intervalYyyymmdd = null;
                    if(lastNotificationDate != null) {
                        intervalYyyymmdd = Integer.parseInt(TimeUtil.getPreDay(lastNotificationDate, -notificationInterval).subSequence(0, 8).toString());
                    }
                    
                    if(lastNotificationDate == null 
                            || todayYyyymmdd >= intervalYyyymmdd) {
                        //시간체크
                        // if(Integer.parseInt(today.substring(8,10)) == notificationTime) {
                            log.debug("Period contractNumber[" + contract.getContractNumber() + "]");
                            log.debug("text messae : " + text);
                            
                            String messageId = notificationSMS(contract);
                            contractDao.updateSMSPriod(contract.getId(), messageId, today);
                        // }
                    }
                } else if(notificationPeriod == 2) {
                    Boolean weekly[] = new Boolean[7];
                    weekly[0] = contractInfo.get(i).get("NOTIFICATIONWEEKLYSUN") == null ? false : (Integer.parseInt(contractInfo.get(i).get("NOTIFICATIONWEEKLYSUN").toString()) != 0);
                    weekly[1] = contractInfo.get(i).get("NOTIFICATIONWEEKLYMON") == null ? false : (Integer.parseInt(contractInfo.get(i).get("NOTIFICATIONWEEKLYMON").toString()) != 0);
                    weekly[2] = contractInfo.get(i).get("NOTIFICATIONWEEKLYTUE") == null ? false : (Integer.parseInt(contractInfo.get(i).get("NOTIFICATIONWEEKLYTUE").toString()) != 0);
                    weekly[3] = contractInfo.get(i).get("NOTIFICATIONWEEKLYWED") == null ? false : (Integer.parseInt(contractInfo.get(i).get("NOTIFICATIONWEEKLYWED").toString()) != 0);
                    weekly[4] = contractInfo.get(i).get("NOTIFICATIONWEEKLYTHU") == null ? false : (Integer.parseInt(contractInfo.get(i).get("NOTIFICATIONWEEKLYTHU").toString()) != 0);
                    weekly[5] = contractInfo.get(i).get("NOTIFICATIONWEEKLYFRI") == null ? false : (Integer.parseInt(contractInfo.get(i).get("NOTIFICATIONWEEKLYFRI").toString()) != 0);
                    weekly[6] = contractInfo.get(i).get("NOTIFICATIONWEEKLYSAT") == null ? false : (Integer.parseInt(contractInfo.get(i).get("NOTIFICATIONWEEKLYSAT").toString()) != 0);
                    
                    lang = codeDao.get(supplier.getLangId()).getCode();
                    String todayWeek = CalendarUtil.getWeekDay(lang, yyyy, mm, dd);
                    WeekDay week = null;
                    if(todayWeek.equals("Sun")) {
                        week = WeekDay.Sunday;
                    } else if (todayWeek.equals("Mon")) {
                        week = WeekDay.Monday;
                    } else if (todayWeek.equals("Tue")) {
                        week = WeekDay.Tuesday;
                    } else if (todayWeek.equals("Wed")) {
                        week = WeekDay.Wednesday;
                    } else if (todayWeek.equals("Thu")) {
                        week = WeekDay.Thursday;
                    } else if (todayWeek.equals("Fri")) {
                        week = WeekDay.Friday;
                    } else if (todayWeek.equals("Sat")) {
                        week = WeekDay.Saturday;
                    }
                    
                    //Weekly
                    //다음 잔액통보 주
                    Integer lastNotificatonNextWeek = null;
                    Integer lastNotificatonLastWeek = null;
                    Integer lastNotificatonNextWeekDate = null;
                    
                    Boolean thisWeekleftover = false;
                    
                    //마지막으로 SMS를 보낸 주의 시작날짜, 마지막 날짜
                    Map<String,String> startEndDateByLastWeek = null;
                    //지정된 interval 뒤의 SMS 보낼  주의 시작날짜, 마지막 날짜
                    Map<String,String> startEndDateByNextWeek = null;
                    
                    if(!("".equals(lastNotificationDate) || lastNotificationDate == null)) {
                        lastNotificatonNextWeek = CalendarUtil.getWeekOfMonth(TimeUtil.getPreDay(lastNotificationDate,-(7*notificationInterval)));
                        lastNotificatonLastWeek = CalendarUtil.getWeekOfMonth(lastNotificationDate);
                        
                        //주의 시작날짜와 마지막 날짜를 구함
                        startEndDateByLastWeek = searchStartEndDate(lastNotificationDate.toString(), lastNotificatonLastWeek);

                        lastNotificatonNextWeekDate = Integer.parseInt(TimeUtil.getPreDay(lastNotificationDate,-(7*notificationInterval)).substring(0,8));
                        
                        startEndDateByNextWeek = searchStartEndDate(lastNotificatonNextWeekDate.toString(), lastNotificatonNextWeek);
                        
                        //오늘 날짜가 마지막으로 SMS를 보낸 날짜와 같은 주에 있을 때 _ 한 주동안 하나이상의 요일에 SMS 를 보내는 경우를 판단하기 위함
                        thisWeekleftover = Integer.parseInt(CalendarUtil.getCurrentDate().substring(0,8)) >= Integer.parseInt(startEndDateByLastWeek.get("startDate")) 
                                && Integer.parseInt(CalendarUtil.getCurrentDate().substring(0,8)) <= Integer.parseInt(startEndDateByLastWeek.get("endDate"));
                    }
                    
                    //한번도 SMS를 보낸적이 없을 경우, 다음 SMS를 보내는 요일일 경우, 다음 SMS를 보내는 주기일 경우 SMS를 보낸다.
                    if(lastNotificationDate == null || thisWeekleftover
                            || Integer.parseInt(startEndDateByNextWeek.get("startDate")) <= Integer.parseInt(CalendarUtil.getCurrentDate().substring(0,8))) {
                        //시간체크
                        //if(Integer.parseInt(today.substring(8,10)) == notificationTime) {
                            //요일체크
                            if(weekly[Integer.parseInt(week.getCode())]) {
                                String messageId = notificationSMS(contract);
                                contractDao.updateSMSPriod(contract.getId(), messageId, lastNotificationDate);
                            }
                        //}
                    }
                }
                txManager.commit(txStatus);
            } catch (Exception e) {
                if (txStatus != null) {
                    try {
                        txManager.rollback(txStatus);
                    }
                    catch (Exception ee) {}
                }
                log.error(e,e);
            }
            
        }
        log.info("########### 1.2 END prepaySMSSend_sendSMSPeriod ###############");
    }
    
    /**
     * 일력한 날짜 주의 시작날짜와 마지막날짜를 구한다.
     * 
     * @param searchDate
     * @param weekDate
     * @return
     * @throws Exception
     */
    private Map<String,String> searchStartEndDate(String searchDate, Integer weekDate)  throws Exception {
        Map<String,String> startEndDate = null;
        
        startEndDate = CalendarUtil.getDateWeekOfMonth(searchDate.substring(0,4),searchDate.substring(4,6), weekDate.toString());
        Integer gap = 6 - (Integer.parseInt(startEndDate.get("endDate")) - Integer.parseInt(startEndDate.get("startDate")));
        
        //달의 마지막주가 2개의 달이 걸쳐있는 경우도 포함해서 주의 시작날짜와 마지막 날짜를 가져오도록 한다. ex) 3월 30일 ~ 4월 5일
        if(gap != 0) {
            if(Integer.parseInt(startEndDate.get("startDate").substring(6,8)) == 1)
                startEndDate.put("startDate", TimeUtil.getPreDay(startEndDate.get("startDate"), gap).substring(0,8));
            else 
                startEndDate.put("endDate", TimeUtil.getPreDay(startEndDate.get("endDate"), -gap).substring(0,8));
        }
        
        return startEndDate;
    }
    
    /**
     * SMS 보내는 부분
     * 
     * @param contract
     * @param today
     * @return
     * @throws Exception
     */
    private String notificationSMS(Contract contract) {
        Properties prop = new Properties();
        String messageId = "fail:prepaySendSMS";
        try {
	        prop.load(getClass().getClassLoader().getResourceAsStream("config/schedule.properties"));
	        String smsClassPath = prop.getProperty("smsClassPath");
	        SendSMS obj = (SendSMS) Class.forName(smsClassPath).newInstance();
	        
	        Method m = obj.getClass().getDeclaredMethod("send", String.class, String.class, Properties.class);
	        messageId = (String) m.invoke(obj, mobileNo, text, prop);
	
	        if(!"".equals(messageId)) {
	        	messageId = messageId + ":prepaySendSMS";
	        }
        } catch(Exception e) {
        	log.error(e,e);
        }
        return messageId;
    }
}
