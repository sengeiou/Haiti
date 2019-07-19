package com.aimir.schedule.task;

import java.util.ArrayList;
import java.util.List;
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
public class SetCloneOnOffTask extends ScheduleTask 
{
    private static Log log = LogFactory.getLog(SetCloneOnOffTask.class);
    
    @Autowired
    ModemDao modemDao;

    @Autowired
    MCUDao mcuDao;
 
    @Autowired
    CmdOperationUtil cmdOperationUtil;
    
    @Resource(name="transactionManager")
    HibernateTransactionManager txmanager;

    
    private static String _modemId;
    private static String _cloneCode;
    private static String _count;
    private static String _version;
    private static String _euiCount;
    private static String _euiList;
    
    
    public static void main(String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{"spring-schedule-task.xml"}); 
        DataUtil.setApplicationContext(ctx);

		log.debug("ARG_0[" + args[0] + "] ARG_1[" + args[1] + "] ARG_2[" + args[2] + "] ARG_3[" + args[3] + "] ARG_4[" + args[4] +"] ARG_5[" + args[5] +"]");		
		
		_modemId = args[0];
		_cloneCode = args[1];
		_count = args[2];
		_version = args[3];
		_euiCount = args[4];        
		_euiList = args[5];                
        
        SetCloneOnOffTask task = ctx.getBean(SetCloneOnOffTask.class);
        task.execute(null);
        System.exit(0);
    }
    
	@Override
	public void execute(JobExecutionContext context) {

        TransactionStatus txstatus = null;
        txstatus = txmanager.getTransaction(null);
        List<String> euiList = new ArrayList<String>();
        StringTokenizer tokenizer = new  StringTokenizer(_euiList, "|");
        while (tokenizer.hasMoreTokens()) {
        	euiList.add(tokenizer.nextToken());
        }
        try {
        	cmdOperationUtil.setCloneOnOffWithTarget(_modemId, 
        			_cloneCode,
        			Integer.parseInt(_count), 
        			_version, 
        			Integer.parseInt(_euiCount)
        			, euiList);
            
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
