package com.aimir.schedule.task;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants.MeterStatus;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.fep.util.DataUtil;
import com.aimir.model.device.MCU;
import com.aimir.model.device.Meter;
import com.aimir.model.system.Code;
import com.aimir.schedule.command.CmdOperationUtil;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;

/**
 * 모뎀 정보를 가져온다. 나중에 버전 정보가 없는 모뎀만 조회하여 스캐닝하는 기능 추가해야 함.
 * @author elevas
 *
 */
@Service
public class MeterStatusScanningTask 
{
    private static Log log = LogFactory.getLog(MeterStatusScanningTask.class);
    
    @Autowired
    MCUDao mcuDao;
    
    @Autowired
    CmdOperationUtil cmdOperationUtil;
    
    @Resource(name="transactionManager")
    HibernateTransactionManager txmanager;
    
    public static void main(String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{"spring-meterstatusscanning.xml"}); 
        DataUtil.setApplicationContext(ctx);
        MeterStatusScanningTask task = ctx.getBean(MeterStatusScanningTask.class);
        log.info("args.len[" + args.length + "] val[" + args[0] + "]");
        if (args[0] != null && !args[0].contains("mcuId")) {
            task.execute(args);
        }
        else {
            task.execute();
        }
        System.exit(0);
    }

    private List<MCU> getMCU() {
        TransactionStatus txstatus = null;
        
        try {
            txstatus = txmanager.getTransaction(null);
            
            List<MCU> mculist = new ArrayList<MCU>();
            mculist = mcuDao.getAll();
            return mculist;
        }
        finally {
            if (txstatus != null) txmanager.commit(txstatus);
        }
    }

    private Code getDeleteCode() {
        TransactionStatus txstatus = null;
    	Code deleteCode = null;
    	try{
    		txstatus = txmanager.getTransaction(null);
	    	Set<Condition> codeCondition = new HashSet<Condition>();
	        codeCondition.add(new Condition("parent", new Object[]{"p"}, null, Restriction.ALIAS));
	        codeCondition.add(new Condition("p.code", new Object[]{Code.METER_STATUS}, null, Restriction.EQ));
	        codeCondition.add(new Condition("name", new Object[]{MeterStatus.Delete.name()}, null, Restriction.EQ));
	        CodeDao codeDao = DataUtil.getBean(CodeDao.class);
	        List<Code> deleteCodeList = codeDao.findByConditions(codeCondition);
	        if(deleteCodeList.size() > 0) {
	        	deleteCode = deleteCodeList.get(0);
	        }
    	} catch (Exception e) {
    		log.warn(e,e);
    	} finally {
            if (txstatus != null) txmanager.commit(txstatus);
        }
        return deleteCode;
    }
    
    public void execute(String[] mculist) {
        log.info("Start MCU Scanning size[" + mculist.length + "]");
        ThreadPoolExecutor executor = new ThreadPoolExecutor(3, 3, 5, TimeUnit.HOURS, new LinkedBlockingQueue<Runnable>());
        for (String mcuId : mculist) {
            try {
                executor.execute(new MeterStatusScanningThread(mcuId));
            }
            catch (Exception e) {
                log.error(e, e);
            }
        }
        
        try {
            executor.shutdown();
            while (!executor.isTerminated()) {
                Thread.sleep(100);
            }
        }
        catch (Exception e) {}
        log.info("End MCU Scanning size[" + mculist.length + "]");
    }
    
    public void execute() {
        List<MCU> mculist = getMCU();
        Code deleteCode = getDeleteCode();
        log.info("Start MCU Scanning size[" + mculist.size() + "]");
        if(mculist != null && mculist.size() > 0) {
            ThreadPoolExecutor executor = new ThreadPoolExecutor(3, 3, 30, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>());
            for (MCU mcu : mculist) {
                try {
                    executor.execute(new MeterStatusScanningThread(mcu, deleteCode));
                }
                catch (Exception e) {
                    log.error(e, e);
                }
            }
            
            try {
                executor.shutdown();
                while (!executor.isTerminated()) {
                    Thread.sleep(100);
                }
            }
            catch (Exception e) {}
        }
        log.info("End MCU Scanning size[" + mculist.size() + "]");
    }
}

class MeterStatusScanningThread implements Runnable {
    private static Log log = LogFactory.getLog(MeterStatusScanningThread.class);

    private MCU mcu;
    private Code deleteCode;
    
    HibernateTransactionManager txmanager = (HibernateTransactionManager)DataUtil.getBean("transactionManager");
    
    MeterStatusScanningThread(MCU mcu, Code deleteCode) {
        this.mcu = mcu;
        this.deleteCode = deleteCode;
    }
    
    MeterStatusScanningThread(String mcuId) {
        MCUDao mcuDao = DataUtil.getBean(MCUDao.class);
        this.mcu = mcuDao.get(mcuId);
    }
    
    private List<Meter> listMeter(int mcuId) {
        TransactionStatus txstatus = null;

        try {
            txstatus = txmanager.getTransaction(null);
            Set<Condition> condition = new HashSet<Condition>();
            condition.add(new Condition("modem", new Object[]{"m"}, null, Restriction.ALIAS));
            condition.add(new Condition("m.mcu", new Object[]{"mcu"}, null, Restriction.ALIAS));
            condition.add(new Condition("mcu.id", new Object[]{mcuId}, null, Restriction.EQ));
            if(deleteCode != null) {
            	condition.add(new Condition("meterStatus", new Object[]{deleteCode}, null, Restriction.NE));
            }
            MeterDao meterDao = DataUtil.getBean(MeterDao.class);
            List<Meter> meterlist = meterDao.findByConditions(condition);
            
            return meterlist;
        }
        finally {
            if (txstatus != null) txmanager.commit(txstatus);
        }
    }
    
    @Override
    public void run() {
        log.info("Try to scan meter status of MCU[" + mcu.getSysID() + "] ipaddr[" + mcu.getIpAddr() + "]");
        TransactionStatus txstatus = null;
        CmdOperationUtil cmdOperationUtil = DataUtil.getBean(CmdOperationUtil.class);
        try {
            Map<String, Object> result = null;
            String response = null;
            List<Meter> meterlist = listMeter(mcu.getId());

            for (Meter m : meterlist) {
                result = cmdOperationUtil.relayValveStatus(mcu.getSysID(), m.getMdsId());
                log.debug(result);
                
                response = (String)result.get("Response");
                if (response != null && response.contains("DCU")) {
                    log.warn("DCU is not connected. skip...");
                    return;
                }
            }
            log.info("updated " + meterlist.size() + " meters of MCU[" + mcu.getSysID() + "]");
        }
        catch (Exception e) {
            log.error(e, e);
        }
        finally {
            if (txstatus != null)
                txmanager.commit(txstatus);
        }
    }
}
