package com.aimir.schedule.task;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
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

import com.aimir.dao.device.MeterDao;
import com.aimir.fep.util.DataUtil;
import com.aimir.model.device.Meter;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;

/**
 * 삭제할 미터에 관련된 검침데이터는 사전에 삭제되었음을 전제로함.
 * @author sjhan
 */
@Service
public class MeterDeleteSingleTask extends ScheduleTask {

	private static Log log = LogFactory.getLog(MeterDeleteSingleTask.class);
	
	@Resource(name="transactionManager")
    HibernateTransactionManager txmanager;
	
	@Autowired
    MeterDao meterDao;
	
	private boolean isNowRunning = false;

	public static void main(String[] args) {
		ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{"spring-quartz-soria.xml"}); 
        DataUtil.setApplicationContext(ctx);
        
        MeterDeleteSingleTask task = ctx.getBean(MeterDeleteSingleTask.class);
        task.execute(null);
        System.exit(0);
	}

	
	public void execute(JobExecutionContext context) {
		if(isNowRunning){
			log.info("##-- MeterDeleteSingleTask is already running...");
			return;
		}
		isNowRunning = true;
        log.info("###-- MeterDeleteSingleTask start --###");
        
        this.deleteMeterSingle();
        
        log.info("###-- MeterDeleteSingleTask end --###");
        isNowRunning = false;    
	}
	
	
	public void deleteMeterSingle() {
		List<Integer> em_meterIds = this.getMeterIds(147); //Deactivation 임시로 만든거라 고정코드사용.
		
		int poolSize = 10;
		ThreadPoolExecutor executor = new ThreadPoolExecutor(poolSize, poolSize, 10, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>());
		
		for(Integer meter_id : em_meterIds) {
			try {
				log.debug("Tried to delete the meter. @id[" + meter_id + "]");
				executor.execute(new DeleteMeterThread(meter_id));
			}catch(Exception te) {
				log.error(te,te);
			}
		}
		
	}
	
	public List<Integer> getMeterIds(int code){
		TransactionStatus txstatus = null;
        List<Integer> meterIds = new ArrayList<Integer>();
        try {
            txstatus = txmanager.getTransaction(null);
            Set<Condition> condition = new HashSet<Condition>();
            condition.add(new Condition("meterStatusCodeId", new Object[]{code}, null, Restriction.EQ));
            List<Meter> meters = meterDao.findByConditions(condition);
            
            log.debug("# ListMeter Total: " + meters.size());
            txmanager.commit(txstatus);
            
            for (Meter c : meters) {
            	meterIds.add(c.getId());
            }

        }catch (Exception e) {
                log.error(e, e);
                if (txstatus != null) txmanager.rollback(txstatus);
            }
            return meterIds;
	}
	
}



class DeleteMeterThread implements Runnable {
	private static Log log = LogFactory.getLog(MeterDeleteSingleTask.class);
	
	HibernateTransactionManager txmanager = (HibernateTransactionManager)DataUtil.getBean("transactionManager");
	MeterDao meterDao = DataUtil.getBean(MeterDao.class);
	
	private int meter_id;
	
	public DeleteMeterThread(int meterId) {
		this.meter_id = meterId;
	}

	@Override
	public void run() {

		TransactionStatus txstatus = null;
		
		try {
			txstatus = txmanager.getTransaction(null);
			txmanager.setDefaultTimeout(-1);
			
			int temp = meterDao.deleteById(meter_id);
			log.debug("Meter[" + meter_id + "] deleted.");
			
			if (!txstatus.isCompleted())
                txmanager.commit(txstatus);
			
		}catch (Exception re) {
			log.error(re,re);
			if (txstatus != null && !txstatus.isCompleted()) {
				txmanager.rollback(txstatus);
			}
		}
		
	}
	
	
	
	
	
}













