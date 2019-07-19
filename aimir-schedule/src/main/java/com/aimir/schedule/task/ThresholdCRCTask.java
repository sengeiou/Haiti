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

import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.constants.CommonConstants.ThresholdName;
import com.aimir.dao.device.ThresholdWarningDao;
import com.aimir.fep.util.DataUtil;
import com.aimir.dao.device.ThresholdDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.device.MCUDao;
import com.aimir.model.device.Threshold;
import com.aimir.model.device.ThresholdWarning;
import com.aimir.schedule.command.CmdOperationUtil;

@Service
public class ThresholdCRCTask extends ScheduleTask 
{
    private static Log log = LogFactory.getLog(ThresholdCRCTask.class);
    
    @Autowired
    ThresholdDao thresholdDao;
    
    @Autowired
    ThresholdWarningDao thresholdWarningDao;    

    @Autowired
    ModemDao modemDao;

    @Autowired
    MCUDao mcuDao;
 
    @Autowired
    CmdOperationUtil cmdOperationUtil;
    
    @Resource(name="transactionManager")
    HibernateTransactionManager txmanager;

    public static void main(String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{"spring-forthreshold.xml"}); 
        DataUtil.setApplicationContext(ctx);
        
        ThresholdCRCTask task = ctx.getBean(ThresholdCRCTask.class);
        task.execute(null);
        System.exit(0);
    }
    
    private List<ThresholdWarning> getOverList() {
        Threshold threshold = thresholdDao.getThresholdByname(ThresholdName.CRC.name());
        if (threshold==null) {
        	return null;
        }
        return thresholdWarningDao.getOverThresholdDevices(threshold.getId(), threshold.getLimit());
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
        Threshold threshold = thresholdDao.getThresholdByname(ThresholdName.CRC.name());
        
        try {
        	for (ThresholdWarning data : list) {
        	    List<Object[]> modem = modemDao.getModemByIp(data.getIpAddr());
                List<String> mcu = mcuDao.getMcuByIp(data.getIpAddr());
                
                TargetClass target = null;
                String      id = "";
                
                if (modem != null && modem.size() == 1) {
                    Object[] _modem = modem.get(0);
                    target = TargetClass.valueOf(_modem[0].toString());
                    id = _modem[1].toString();
                }
                else if ( mcu != null && mcu.size() == 1) {
                    target = TargetClass.DCU;
                    id = mcu.get(0);
                }
        		
                // INSERT START SP-285
        		if (id==null ||"".equals(id)) {
        			target = TargetClass.Unknown;
        			id = data.getIpAddr() == null ? "" : data.getIpAddr();
        		}
                // INSERT END SP-285
        		
        		log.debug("ThresholdWarningID["+ data.getId() + "] target[" + target.name() + 
        				"] activatorId[" + id + "] observedValue[" + data.getValue().toString() +
        				"] thresholdValue[" + threshold.getLimit().toString() + "]");        		

        		if (id==null ||"".equals(id)) {
        			log.debug("Not send event.");
        		} else {       
	        		// send event
	    			String result = cmdOperationUtil.cmdSendEvent("Threshold Warning", 
	    					target.name(),
	    					id,
	    					new String[][] {{"kind", ThresholdName.CRC.getThresholdNameValue()},
	    									{"observedValue", data.getValue().toString()}, 
	    									{"thresholdValue", threshold.getLimit().toString()}}
	    					);
	
	                // clear count
	    			if (result.equals("SUCCESS")) {
		            	data.setValue(0);
		            	thresholdWarningDao.saveOrUpdate(data);
	    			}
	    			else {
	    				log.debug("send event failed.");
	    			}
        		}
        		
        		modem = null;
        		mcu = null;
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