package com.aimir.cms.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.model.device.Meter;
import com.aimir.model.system.Contract;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;

public class MeterSync {
    private static Log log = LogFactory.getLog(MeterSync.class);
    
    private static ApplicationContext ctx;
    
    public static void main(String[] args) {
        ctx = new ClassPathXmlApplicationContext(new String[]{"/spring.xml"}); 
        MeterSync sync = new MeterSync();
        sync.run();
    }
    
    private void run() {
        MeterDao meterDao = ctx.getBean(MeterDao.class);
        ContractDao contractDao = ctx.getBean(ContractDao.class);
        HibernateTransactionManager txmanager = ctx.getBean(HibernateTransactionManager.class);
        
        TransactionStatus txstatus = null;
        try {
            txstatus = txmanager.getTransaction(null);
            Set<Condition> condition = new HashSet<Condition>();
            condition.add(new Condition("mdsId", new Object[]{"P"}, null, Restriction.LIKE));
            
            List<Meter> meters = meterDao.findByConditions(condition);
            
            Contract c = null;
            for (Meter m : meters) {
                Meter rm = meterDao.get(m.getMdsId().substring(1));
                
                if (rm != null) {
                    c = m.getContract();
                    
                    if (c != null) {
                        c.setMeter(rm);
                        rm.setContract(c);
                        m.setContract(null);
                        m.setMeterStatus(CommonConstants.getMeterStatusByName("Deleted"));
                        
                        contractDao.update(c);
                        meterDao.update(rm);
                        meterDao.update(m);
                    }
                }
                else {
                    m.setMdsId(m.getMdsId().substring(1));
                    meterDao.update(m);
                }
            }
            txmanager.commit(txstatus);
        }
        catch (Exception e) {
            log.error(e, e);
            if (txstatus != null) txmanager.rollback(txstatus);
        }
    }
}
