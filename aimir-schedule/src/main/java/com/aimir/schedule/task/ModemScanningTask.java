package com.aimir.schedule.task;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.aimir.dao.device.MCUDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.fep.command.ws.client.CommandWS;
import com.aimir.fep.protocol.fmp.datatype.BYTE;
import com.aimir.fep.protocol.fmp.datatype.OCTET;
import com.aimir.fep.protocol.fmp.datatype.TIMESTAMP;
import com.aimir.fep.protocol.fmp.datatype.WORD;
import com.aimir.fep.protocol.fmp.frame.service.entry.sensorInfoNewEntry;
import com.aimir.fep.util.DataUtil;
import com.aimir.model.device.MCU;
import com.aimir.model.device.Modem;
import com.aimir.schedule.command.CmdManager;
import com.aimir.schedule.command.CmdOperationUtil;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;

/**
 * 모뎀 정보를 가져온다. 나중에 버전 정보가 없는 모뎀만 조회하여 스캐닝하는 기능 추가해야 함.
 * @author elevas
 *
 */
@Service
@Transactional
public class ModemScanningTask 
{
    private static Log log = LogFactory.getLog(ModemScanningTask.class);
    
    @Autowired
    MCUDao mcuDao;
    
    @Autowired
    ModemDao modemDao;
    
    @Autowired
    CmdOperationUtil cmdOperationUtil;
    
    public static void main(String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{"spring-modemscanning.xml"}); 
        DataUtil.setApplicationContext(ctx);
        ModemScanningTask task = ctx.getBean(ModemScanningTask.class);
        log.info("args.len[" + args.length + "] val[" + args[0] + "]");
        if (args[0] != null && !args[0].contains("mcuId")) {
            task.execute(args);
        }
        else {
            task.execute();
        }
        System.exit(0);
    }
    
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    private List<MCU> getMCU() {
        List<MCU> mculist = new ArrayList<MCU>();
        mculist = mcuDao.getAll();
        return mculist;
    }
    
    public void execute(String[] mculist) {
        log.info("Start MCU Scanning size[" + mculist.length + "]");
        ThreadPoolExecutor executor = new ThreadPoolExecutor(3, 3, 5, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>());
        for (String mcuId : mculist) {
            try {
                executor.execute(new ModemScanningThread(mcuId));
            }
            catch (Exception e) {
                log.error(e, e);
            }
        }
        
        try {
            executor.shutdown();
            while (!executor.isTerminated()) {
            }
        }
        catch (Exception e) {}
        log.info("End MCU Scanning size[" + mculist.length + "]");
    }
    
    public void execute() {
        List<MCU> mculist = getMCU();
        
        log.info("Start MCU Scanning size[" + mculist.size() + "]");
        if(mculist != null && mculist.size() > 0) {
            ThreadPoolExecutor executor = new ThreadPoolExecutor(3, 3, 5, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>());
            for (MCU mcu : mculist) {
                try {
                    executor.execute(new ModemScanningThread(mcu));
                }
                catch (Exception e) {
                    log.error(e, e);
                }
            }
            
            try {
                executor.shutdown();
                while (!executor.isTerminated()) {
                }
            }
            catch (Exception e) {}
        }
        log.info("End MCU Scanning size[" + mculist.size() + "]");
    }
}

class ModemScanningThread implements Runnable {
    private static Log log = LogFactory.getLog(ModemScanningThread.class);

    private MCU mcu;
    
    HibernateTransactionManager txmanager = (HibernateTransactionManager)DataUtil.getBean("transactionManager");
    
    ModemScanningThread(MCU mcu) {
        this.mcu = mcu;
    }
    
    ModemScanningThread(String mcuId) {
        MCUDao mcuDao = DataUtil.getBean(MCUDao.class);
        this.mcu = mcuDao.get(mcuId);
    }
    
    @Override
    public void run() {
        log.info("Try to scan Modem of MCU[" + mcu.getSysID() + "] ipaddr[" + mcu.getIpAddr() + "]");
        TransactionStatus txstatus = null;
        CmdOperationUtil cmdOperationUtil = DataUtil.getBean(CmdOperationUtil.class);
        try {
            txstatus = txmanager.getTransaction(null);
            List<Map<String, Object>> modemlist = cmdOperationUtil.findSensorInfo(mcu);
            log.info("MCU[" + mcu.getSysID() + " has "+ modemlist.size() + " modems");
            for (Map<String, Object> m : modemlist) {
                updateModem(m);
            }
            log.info("updated " + modemlist.size() + " modems of MCU[" + mcu.getSysID() + "]");
        }
        catch (Exception e) {
            log.error(e, e);
            if (e.getMessage().contains("Could not send Message") || e.getMessage().contains("EMPTY")) {
                List<Modem> modemlist = getModem(mcu.getSysID());
                for (Modem m : modemlist) {
                    scanModem(mcu.getSysID(), m.getId(), m.getModemType().name());
                }
            }
        }
        finally {
            if (txstatus != null)
                txmanager.commit(txstatus);
        }
    }
    
    private void updateModem(Map<String, Object> m) {
        log.debug(m);
        TransactionStatus txstatus = null;
        try {
            txstatus = txmanager.getTransaction(
                new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRES_NEW));
        
            String modemId = (String)m.get("deviceSerial");
            ModemDao modemDao = DataUtil.getBean(ModemDao.class);
            Modem modem = modemDao.get(modemId);
            
            String fwVer = (String)m.get("fwVer");
            String fwRevision = (String)m.get("fwRevision");
            String hwVer = (String)m.get("hwVer");
            String nodeKind = (String)m.get("nodeKind");
            if (modem != null) {
                log.info("before modem[" + modem.getDeviceSerial() + 
                        "] fwVer[" + modem.getFwVer() + 
                        "] fwRevision[" + modem.getFwRevision() + 
                        "] hwVer[" + modem.getHwVer() + 
                        "] nodeKind[" + modem.getNodeKind() + "]");
                modem.setFwVer(fwVer);
                modem.setFwRevision(fwRevision);
                modem.setHwVer(hwVer);
                modem.setNodeKind(nodeKind);
                log.info("after modem[" + modem.getDeviceSerial() + 
                        "] fwVer[" + modem.getFwVer() + 
                        "] fwRevision[" + modem.getFwRevision() + 
                        "] hwVer[" + modem.getHwVer() + 
                        "] nodeKind[" + modem.getNodeKind() + "]");
            }
        }
        finally {
            if (txstatus != null)
                txmanager.commit(txstatus);
        }
    }
    
    private List<Modem> getModem(String mcuId) {
        TransactionStatus txstatus = null;
        try {
            txstatus = txmanager.getTransaction(
                new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRES_NEW));
            ModemDao modemDao = DataUtil.getBean(ModemDao.class);
            Set<Condition> condition = new HashSet<Condition>();
            condition.add(new Condition("mcu", new Object[]{"m"}, null, Restriction.ALIAS));
            condition.add(new Condition("m.sysID", new Object[]{mcuId}, null, Restriction.EQ));
            return modemDao.findByConditions(condition);
        }
        finally {
            if (txstatus != null)
                txmanager.commit(txstatus);
        }
    }
    
    private void scanModem(String mcuId, Integer modemId, String modemType) {
        TransactionStatus txstatus = null;
        try {
            txstatus = txmanager.getTransaction(
                new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRES_NEW));
            MCUDao mcuDao = DataUtil.getBean(MCUDao.class);
            ModemDao modemDao = DataUtil.getBean(ModemDao.class);
            MCU mcu = mcuDao.get(mcuId);
            CommandWS gw = CmdManager.getCommandWS(mcu.getProtocolType().getName());
            Modem modem = modemDao.get(modemId);
            if(modem != null && modem.getAmiNetworkAddress() != null && !"".equals(modem.getAmiNetworkAddress())){
                
                log.info("doGetModemCluster[" + modemId + "]");
                gw.doGetModemCluster(mcuId, modemId+"", modemType, 1, "");
            }else{
                log.info("cmdGetModemInfoNew[" + modemId + "]");
                sensorInfoNewEntry sensorEntries = gw.cmdGetModemInfoNew(modem.getDeviceSerial());

                log.info("before modem[" + modem.getDeviceSerial() + 
                        "] fwVer[" + modem.getFwVer() + 
                        "] fwRevision[" + modem.getFwRevision() + 
                        "] hwVer[" + modem.getHwVer() + 
                        "] nodeKind[" + modem.getNodeKind() + "]");
                modem.setCommState(new BYTE(sensorEntries.getSensorState().getValue()).getValue());
                modem.setFwRevision(new WORD(sensorEntries.getSensorFwBuild().getValue()).getValue() + "");
                modem.setFwVer(new WORD(sensorEntries.getSensorFwVersion().getValue()).decodeVersion());
                modem.setHwVer(new WORD(sensorEntries.getSensorHwVersion().getValue()).decodeVersion());
                modem.setLastLinkTime(new TIMESTAMP(sensorEntries.getSensorLastConnect().getValue()).getValue());
                modem.setNodeKind(new OCTET(sensorEntries.getSensorModel().getValue()).toString());
                log.info("after modem[" + modem.getDeviceSerial() + 
                        "] fwVer[" + modem.getFwVer() + 
                        "] fwRevision[" + modem.getFwRevision() + 
                        "] hwVer[" + modem.getHwVer() + 
                        "] nodeKind[" + modem.getNodeKind() + "]");
            }
        }
        catch (Exception ne) {
            log.warn(ne);
        }
        finally {
            if (txstatus != null)
                txmanager.commit(txstatus);
        }
    }
}
