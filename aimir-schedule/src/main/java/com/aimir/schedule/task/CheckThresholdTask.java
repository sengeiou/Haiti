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
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants.MeterStatus;
import com.aimir.constants.CommonConstants.ThresholdName;
import com.aimir.dao.device.ThresholdWarningDao;
import com.aimir.dao.device.ThresholdDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.fep.util.DataUtil;
import com.aimir.model.device.MCU;
import com.aimir.model.device.Threshold;
import com.aimir.model.device.ThresholdWarning;
import com.aimir.model.system.Code;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;


@Service
public class CheckThresholdTask extends ScheduleTask 
{
    private static Log log = LogFactory.getLog(CheckThresholdTask.class);
    
    @Autowired
    ThresholdDao thresholdDao;
    
    @Autowired
    ThresholdWarningDao ThresholdWarningDao;    

    @Resource(name="transactionManager")
    HibernateTransactionManager txmanager;
    
//    public static void main(String[] args) {
//        ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{"spring-checkthreshold.xml"}); 
//        DataUtil.setApplicationContext(ctx);
//        CheckThresholdTask task = ctx.getBean(CheckThresholdTask.class);
//        log.info("args.len[" + args.length + "] val[" + args[0] + "]");
//        if (args[0] != null) {
//            //task.execute(args);
//        }
//        else {
//            log.error("Threshold type is not setting to args.");
//        }
//        System.exit(0);
//    }

    private List<ThresholdWarning> getOverList() {
        TransactionStatus txstatus = null;
        
        try {
            txstatus = txmanager.getTransaction(null);
            
            Threshold threshold = thresholdDao.getThresholdByname(ThresholdName.CRC.name());
            return ThresholdWarningDao.getOverThresholdDevices(threshold.getId(), threshold.getLimit());
        }
        finally {
            if (txstatus != null) txmanager.commit(txstatus);
        }
    }
    
	@Override
	public void execute(JobExecutionContext context) {
        log.info("Start Check over threshold count. ");

        List<ThresholdWarning> list = new ArrayList<ThresholdWarning>();
        list = getOverList();
        if (list.size() == 0) return;
        
        for (ThresholdWarning data : list) {
            try {
                
                // send event
                
                // clear count
            }
            catch (Exception e) {
                log.error(e, e);
            }
        }
        
        log.info("End Check over threshold count. ");
    }    
    
}