package com.aimir.schedule.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.dao.system.DepositHistoryDao;
import com.aimir.dao.system.PrepaymentLogDao;
import com.aimir.fep.util.DataUtil;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeUtil;

@Service
public class DuplicateChargeDeleteTask extends ScheduleTask {
	
	private static Log log = LogFactory.getLog(DuplicateChargeDeleteTask.class);
	
    @Resource(name="transactionManager")
    HibernateTransactionManager txManager;

    @Autowired
    DepositHistoryDao depositHistoryDao;
    
    @Autowired
    PrepaymentLogDao prepaymentLogDao;
	
	private boolean isNowRunning = false;
	
    public static void main(String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{"spring-forcrontab.xml"}); 
        DataUtil.setApplicationContext(ctx);
        
        DuplicateChargeDeleteTask task = ctx.getBean(DuplicateChargeDeleteTask.class);
        try {
        	String searchDate = null;
        	log.info(args.length);
        	if(args.length > 0) {
        		searchDate = args[0];
        	}
            task.execute(searchDate);
        }
        catch (Exception e) {
            log.error(e, e);
        }
        System.exit(0);
    }

    public void execute(String yyyymmdd) throws Exception {
        //default : 어제 날짜.
        if(yyyymmdd == null || yyyymmdd.isEmpty()) {
        	yyyymmdd = TimeUtil.getPreDay(TimeUtil.getCurrentTime()).substring(0,8);
        }
        log.info("###### Start DuplicateChargeDeleteTask [" + yyyymmdd + "] ######");        
        //두번 로그가 남은 충전 리스트 검색
        List<Map<String, Object>> doubleSalesList = getDoubleSalesList(yyyymmdd);

        if(doubleSalesList == null) {
        	doubleSalesList = new ArrayList<Map<String,Object>>();
        }
        TransactionStatus txStatus = null;
        try {
        	txStatus = txManager.getTransaction(null);
	        for (int i = 0; i < doubleSalesList.size(); i++) {
	        	log.debug("i["+i+"], size["+doubleSalesList.size()+"] \n next j data : [" + doubleSalesList.get(i) + "]");
	        	Map<String, Object> resourceData = doubleSalesList.get(i);
	        	for (int j = 1; j < doubleSalesList.size(); j++) {
	        		log.debug("j["+j+"], size["+doubleSalesList.size()+"] \n next j data : [" + doubleSalesList.get(j) + "]");
		        	Map<String, Object> compareData = doubleSalesList.get(j);
		        	 
		        	Map<String, Object> deleteData = compareData;
		        	long liveDataId = Long.parseLong(StringUtil.nullToBlank(resourceData.get("PID")));
		        	
		        	//덜 검증
		        	boolean isSameData = checkSameHistory(resourceData, compareData);
		        	
		        	if(Long.parseLong(StringUtil.nullToBlank(resourceData.get("LASTTOKENDATE"))) > Long.parseLong(StringUtil.nullToBlank(compareData.get("LASTTOKENDATE")))) {
		        		deleteData = resourceData;
		        		liveDataId = Long.parseLong(StringUtil.nullToBlank(compareData.get("PID")));;
		        	}
		        	
		        	if(isSameData) {
		        		long pid = Long.parseLong(StringUtil.nullToZero(deleteData.get("PID")));
		        		log.info("DELETE START == "+deleteData);
		        		boolean endDelete = deleteHistory(pid);
		        		if(endDelete) {
		        			log.info("DELETE SUCCESS ["+pid+"] / LIVE Pid [" + liveDataId + "]");
		        		} else {
		        			log.info("DELETE FAIL ["+pid+"] / LIVE Pid [" + liveDataId + "]");
		        		}
		        		log.debug("will next i data : "+doubleSalesList.get(i+1));
		        		log.debug("will next j data : "+doubleSalesList.get(j+1));
		        		doubleSalesList.remove(j);
		        		//데이터를 지워줬으므로 초기화
		        		break;
		        	}
	        	}
	        	doubleSalesList.remove(i);
	        	i=i-1;
			}
	        txManager.commit(txStatus);
        } catch(Exception e) {
        	log.error(e,e);
            if (txStatus != null) {
                try {
                    txManager.rollback(txStatus);
                }
                catch (Exception ee) {}
            }
            
        }
        log.info("###### End DuplicateChargeDeleteTask [" + yyyymmdd + "] ######");
    }
    
	@Override
	public void execute(JobExecutionContext context) {
		if(isNowRunning){
			log.info("########### DuplicateChargeDeleteTask is already running...");
			return;
		}
		isNowRunning = true;
		
		String yyyymmdd = null;
		try {
			//default : 어제 날짜.
	        if(yyyymmdd == null || yyyymmdd.isEmpty()) {
	        	yyyymmdd = TimeUtil.getPreDay(TimeUtil.getCurrentTime()).substring(0,8);
	        }
			execute(yyyymmdd);
		} catch (Exception e) {
			log.error(e, e);
		}
		
        isNowRunning = false;
	}
	
	/**
	 * Double Sales History가 맞는지 검증
	 */
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	private Boolean checkSameHistory(Map<String, Object> resourceData, Map<String, Object> compareData) {
		boolean isSameHistory = false;
		if(StringUtil.nullToBlank(resourceData.get("CONTRACT_NUMBER")).equals(StringUtil.nullToBlank(compareData.get("CONTRACT_NUMBER")))) {
			isSameHistory = true;
		}
		
		isSameHistory = isSameHistory && StringUtil.nullToBlank(resourceData.get("SERVICE_POINT_ID")).equals(StringUtil.nullToBlank(compareData.get("SERVICE_POINT_ID")));
		isSameHistory = isSameHistory && StringUtil.nullToBlank(resourceData.get("CHARGEDCREDIT")).equals(StringUtil.nullToBlank(compareData.get("CHARGEDCREDIT")));
		isSameHistory = isSameHistory && StringUtil.nullToBlank(resourceData.get("BALANCE")).equals(StringUtil.nullToBlank(compareData.get("BALANCE")));
		isSameHistory = isSameHistory && StringUtil.nullToBlank(resourceData.get("ARREARS")).equals(StringUtil.nullToBlank(compareData.get("ARREARS")));
		isSameHistory = isSameHistory && StringUtil.nullToBlank(resourceData.get("PRE_BALANCE")).equals(StringUtil.nullToBlank(compareData.get("PRE_BALANCE")));
		isSameHistory = isSameHistory && StringUtil.nullToBlank(resourceData.get("PRE_ARREARS")).equals(StringUtil.nullToBlank(compareData.get("PRE_ARREARS")));
		
		return isSameHistory;
	}
    
    /**
     * @MethodName getDoubleSalesList
     * @param yyyymmdd
     * @Modified
     * @Description Vendor 충전 가젯에서 고객의 잔액을 충전했는데 같은 금액으로 두번 로그가 남는 리스트를 삭제하기 위해 두번이상 충전한 고객목록 검색. 
     */
    @Transactional(propagation=Propagation.REQUIRED)
    private List<Map<String,Object>> getDoubleSalesList(String yyyymmdd) {
    	List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
    	TransactionStatus txStatus = null;
    	try {
    		txStatus = txManager.getTransaction(null);
            list = prepaymentLogDao.getDoubleSalesList(yyyymmdd);
            txManager.commit(txStatus);
        } catch (Exception e) {
        	txManager.commit(txStatus);
            log.error(e, e);
        }
    	return list;
    }
    
    /**
     * @MethodName getDoubleSalesList
     * @param yyyymmdd
     * @Modified
     * @Description Vendor 충전 가젯에서 고객의 잔액을 충전했는데 같은 금액으로 두번 로그가 남는 리스트를 삭제하기 위해 두번이상 충전한 고객목록 검색. 
     */
    @Transactional(propagation=Propagation.REQUIRED)
    private boolean deleteHistory(Long pid) {
    	boolean endDelete = false;
    	try {
    		log.info("delete start depositHistory");
    		depositHistoryDao.deleteByPrepaymentLogId(pid);
    		log.info("delete start prepaymentLog");
    		prepaymentLogDao.deleteById(pid);
    		endDelete = true;
        } catch (Exception e) {
            log.error(e, e);
        }
    	return endDelete;
    }
}
