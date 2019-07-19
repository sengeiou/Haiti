package com.aimir.schedule.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

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

import com.aimir.fep.command.ws.client.ResponseMap;
import com.aimir.fep.util.DataUtil;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.device.MCUDao;
import com.aimir.schedule.command.CmdOperationUtil;

@Service
public class GetCoordinatorConfigureTask extends ScheduleTask 
{
    private static Log log = LogFactory.getLog(GetCoordinatorConfigureTask.class);
    
    @Autowired
    CmdOperationUtil cmdOperationUtil;

    @Resource(name="transactionManager")
    HibernateTransactionManager txmanager;

   
    private static String _mcuId;

    
    public static void main(String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{"spring-schedule-task.xml"}); 
        DataUtil.setApplicationContext(ctx);

		log.debug("ARG_0[" + args[0] + "]");		
		
		_mcuId = args[0];
        
        GetCoordinatorConfigureTask task = ctx.getBean(GetCoordinatorConfigureTask.class);
        task.execute(null);
        System.exit(0);
    }
    
	@Override
	public void execute(JobExecutionContext context) {
        TransactionStatus txstatus = null;
        txstatus = txmanager.getTransaction(null);

        try {
        	Map<String, Object> map = cmdOperationUtil.cmdGetCoordinatorConfigure(_mcuId);             
            for (Map.Entry<String, Object> e : map.entrySet()) {
                log.debug("key["+e.getKey()+"], value["+ e.getValue()+"]");
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
