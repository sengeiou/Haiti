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
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;

import com.aimir.fep.util.DataUtil;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.device.MCUDao;
import com.aimir.schedule.command.CmdOperationUtil;

@Service
public class ReqImagePropagateTask extends ScheduleTask 
{
    private static Log log = LogFactory.getLog(ReqImagePropagateTask.class);
    
    @Autowired
    ModemDao modemDao;

    @Autowired
    MCUDao mcuDao;
 
    @Autowired
    CmdOperationUtil cmdOperationUtil;
    
    @Resource(name="transactionManager")
    HibernateTransactionManager txmanager;

    
    private static String _mcuId;
    private static String _upgradeType;
    private static String _control;
    private static String _imageKey;
    private static String _imageUrl;
    private static String _upgradeCheckSum;
    private static String _imageVersion;
    private static String _targetModel;
    private static int _cloneCount;
    private static String _modemList;
    
    public static void main(String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{"spring-schedule-task.xml"}); 
        DataUtil.setApplicationContext(ctx);

		log.debug("ARG_0[" + args[0] + "] ARG_1[" + args[1] + "] ARG_2[" + args[2] + "] ARG_3[" + args[3] + 
				"] ARG_4[" + args[4] + "] ARG_5[" + args[5] + "] ARG_6[" + args[6] + 
				"] ARG_7[" + args[7] + "] ARG_8[" + args[8] + "] ARG_9[" + args[9]);		
		
		_mcuId = args[0];
		_upgradeType = args[1];
		_control = args[2];
		_imageKey = args[3];        
		_imageUrl = args[4];                
		_upgradeCheckSum = args[5];
		_imageVersion = args[6];
		_targetModel = args[7];		
		_cloneCount = Integer.parseInt(args[8]);                
		_modemList = args[9];
        
        ReqImagePropagateTask task = ctx.getBean(ReqImagePropagateTask.class);
        task.execute(null);
        System.exit(0);
    }
    
	@Override
	public void execute(JobExecutionContext context) {

        TransactionStatus txstatus = null;
        txstatus = txmanager.getTransaction(null);
        List<String> modemList = new ArrayList<String>();
        StringTokenizer tokenizer = new  StringTokenizer(_modemList, "|");
        while (tokenizer.hasMoreTokens()) {
        	modemList.add(tokenizer.nextToken());
        }
        try {
        	cmdOperationUtil.cmdReqImagePropagate(_mcuId, 
        			(int)Integer.decode(_upgradeType), 
        			(int)Integer.decode(_control), 
        			_imageKey, 
        			_imageUrl, 
        			_upgradeCheckSum, 
        			_imageVersion,
        			_targetModel,
        			_cloneCount,
        			modemList); 
            
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
