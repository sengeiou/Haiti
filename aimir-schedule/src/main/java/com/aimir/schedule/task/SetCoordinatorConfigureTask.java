package com.aimir.schedule.task;

import java.util.ArrayList;
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

import com.aimir.fep.util.DataUtil;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.device.MCUDao;
import com.aimir.schedule.command.CmdOperationUtil;

@Service
public class SetCoordinatorConfigureTask extends ScheduleTask 
{
    private static Log log = LogFactory.getLog(SetCoordinatorConfigureTask.class);
    
    @Autowired
    CmdOperationUtil cmdOperationUtil;
    
    private static String _mcuId;
    private static String _configurations;
    private static String _modemMode;
    private static String _resetTime;
    private static String _metringInterval;
    private static String _transmitFrequency;
    private static String _cloneTerminate;
    
    public static void main(String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{"spring-schedule-task.xml"}); 
        DataUtil.setApplicationContext(ctx);

		log.debug("ARG_0[" + args[0] + "] ARG_1[" + args[1] + "] ARG_2[" + args[2] + "] ARG_3[" + args[3] + 
				"] ARG_4[" + args[4] + "] ARG_5[" + args[5] + "] ARG_6[" + args[6] + "]");		
		
		_mcuId = args[0];
		_configurations = args[1];
		_modemMode = args[2];
		_resetTime = args[3];        
		_metringInterval = args[4];                
		_transmitFrequency = args[5];
		_cloneTerminate = args[6];
        
        SetCoordinatorConfigureTask task = ctx.getBean(SetCoordinatorConfigureTask.class);
        task.execute(null);
        System.exit(0);
    }
    
	@Override
	public void execute(JobExecutionContext context) {

        try {
        	Map<String, Object> map = cmdOperationUtil.cmdSetCoordinatorConfigure(
        			_mcuId, 
        			(int)Integer.decode(_configurations), 
        			(int)Integer.decode(_modemMode), 
        			(int)Integer.decode(_resetTime), 
        			(int)Integer.decode(_metringInterval), 
        			(int)Integer.decode(_transmitFrequency), 
        			(int)Integer.decode(_cloneTerminate) 
        			); 
            for (Map.Entry<String, Object> e : map.entrySet()) {
                log.debug("key["+e.getKey()+"], value["+ e.getValue()+"]");
            }           
        }
        catch (Exception e) {
            log.error(e, e);
            return;
        }
        
        log.info("End Check over threshold count. ");
    }    
    
}
