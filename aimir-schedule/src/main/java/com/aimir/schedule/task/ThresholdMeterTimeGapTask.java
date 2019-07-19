// INSERT SP-193
package com.aimir.schedule.task;


import java.util.ArrayList;
import java.util.List;

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

import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.constants.CommonConstants.ThresholdName;
import com.aimir.dao.device.ThresholdWarningDao;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.EventUtil;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.ThresholdDao;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Threshold;
import com.aimir.model.device.ThresholdWarning;
import com.aimir.schedule.command.CmdOperationUtil;


@Service
public class ThresholdMeterTimeGapTask extends ScheduleTask 
{
    private static Log log = LogFactory.getLog(ThresholdMeterTimeGapTask.class);
    
    @Autowired
    ThresholdDao thresholdDao;
    
    @Autowired
    ThresholdWarningDao thresholdWarningDao;    

    @Autowired
    MeterDao meterDao;
    
    @Autowired
    CmdOperationUtil cmdOperationUtil;
    
    @Resource(name="transactionManager")
    HibernateTransactionManager txmanager;

    public static void main(String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{"spring-forthreshold.xml"}); 
        DataUtil.setApplicationContext(ctx);
        
        ThresholdMeterTimeGapTask task = ctx.getBean(ThresholdMeterTimeGapTask.class);
        task.execute(null);
        System.exit(0);
    }    
    
    private List<ThresholdWarning> getOverList() {
        Threshold threshold = thresholdDao.getThresholdByname(ThresholdName.METER_TIME_GAP.name());
        if (threshold==null) {
        	return null;
        }
        return thresholdWarningDao.getThresholdWarningList(threshold.getId());
    }
    
	@Override
	public void execute(JobExecutionContext context) {
        log.info("Start Check over threshold count. ");
        TransactionStatus txstatus = null;
        txstatus = txmanager.getTransaction(null);

        List<ThresholdWarning> list = new ArrayList<ThresholdWarning>();
        list = getOverList();
        if ((list == null) || (list.size() == 0)) {
        	log.info("Threshold warning is none. ");
        	log.info("End Check over threshold count. ");
        	return;
        }
        Threshold threshold = thresholdDao.getThresholdByname(ThresholdName.METER_TIME_GAP.name());
        
        try {
        	for (ThresholdWarning data : list) {
        		
        		if ((data.getDeviceType() == null) || (DeviceType.getDeviceType(data.getDeviceType()) != DeviceType.Meter)) {
        			continue;
        		}
        		
        		Meter meter = meterDao.get(data.getDeviceId());
        		
        		TargetClass target = null;
        		if (meter != null) {
        			target = TargetClass.valueOf(meter.getMeterType().getName());

            		log.debug("ThresholdWarningID["+ data.getId() + "] target[" + target.name() + 
            				"] activatorId[" + meter.getMdsId() + "] observedValue[" + data.getValue().toString() +
            				"] thresholdValue[" + threshold.getLimit().toString() + "]");        			
        			// send event
        			String result  = cmdOperationUtil.cmdSendEvent("Threshold Warning", 
        					target.name(),
        					meter.getMdsId(),
        					new String[][] {{"kind", ThresholdName.METER_TIME_GAP.getThresholdNameValue()}, 
        									{"observedValue", data.getValue().toString()}, 
        									{"thresholdValue", threshold.getLimit().toString()}}
        					);        			
        				
	                // delete record
        			if (result.equals("SUCCESS")) {
        				thresholdWarningDao.delete(data);
        			}
           			else {
        				log.debug("send event failed.");
        			}        			
        		}
        		else {
    				log.debug("meter is not found. [" + data.getDeviceId() + "]");
        		}
        		meter = null;
            }
        }
        catch (Exception e) {
            if (txstatus != null) txmanager.rollback(txstatus);
            log.error(e, e);
            return;
        }
        if (txstatus != null) txmanager.commit(txstatus);
        
        log.info("End Check over threshold count. ");
    }    
    
}