package com.aimir.schedule.task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.dao.device.MeterDao;
import com.aimir.dao.mvm.DayEMDao;
import com.aimir.dao.mvm.LpEMDao;
import com.aimir.dao.mvm.PowerQualityDao;
import com.aimir.dao.mvm.RealTimeBillingEMDao;
import com.aimir.fep.util.DataUtil;
import com.aimir.model.device.Meter;
import com.aimir.schedule.util.SAPProperty;
import com.aimir.util.DateTimeUtil;

public class MeterDeleteTask {

    private static Log log = LogFactory.getLog(MeterDeleteTask.class);
    
    @Autowired
    MeterDao meterDao;
    
    @Autowired
    LpEMDao lpEMDao;
    
    @Autowired
    PowerQualityDao pqDao;
    
    @Autowired
    RealTimeBillingEMDao realTimeBillingEMDao;
    
    @Autowired
    DayEMDao dayDao;
    
    public void execute(String datetime) {
        List<Meter> meterlist = listMeter();
        log.info("Delete LP, RealTimeBilling, PowerQuality Meter Count[" + meterlist.size() + "] Date[" + datetime + "]");
        int i = 0;
        for (Meter m : meterlist.toArray(new Meter[0])) {
            log.info("Delete LP[" + i + "] Meter[" + m.getMdsId()+"] Date[" + datetime + "]");
            deleteLp(m.getMdsId(), datetime);
            log.info("Delete RealTimeBilling[" + i + "], Meter[" + m.getMdsId()+"] Date[" + datetime + "]");
            deleteRT(m.getMdsId(), datetime);
            log.info("Delete PowerQuality[" + i + "], Meter[" + m.getMdsId()+"] Date[" + datetime + "]");
            deletePQ(m.getMdsId(), datetime);
            log.info("Delete Day[" + (i++) + "], Meter[" + m.getMdsId()+"] Date[" + datetime + "]");
            deleteDay(m.getMdsId(), datetime);
        }
        log.info("Delete End LP, RealTimeBilling, PowerQuality, Day Meter Count[" + meterlist.size() + "] Date[" + datetime + "]");
    }
    
    public void execute() {
        int lp_period = Integer.parseInt(SAPProperty.getProperty("delete.lp.period"));
        int day_period = Integer.parseInt(SAPProperty.getProperty("delete.day.period"));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Calendar cal = Calendar.getInstance();
        String today = sdf.format(cal.getTime());
        
        cal.add(Calendar.DAY_OF_YEAR, lp_period*-1);
        String lp_date = sdf.format(cal.getTime());
        
        List<Meter> meterlist = listMeter();
        log.info("Delete LP, RealTimeBilling, PowerQuality Meter Count[" + meterlist.size() + "] Date[" + lp_date + "]");
        int i = 0;
        for (Meter m : meterlist.toArray(new Meter[0])) {
            log.info("Delete LP[" + i + "] Meter[" + m.getMdsId()+"] Date[" + lp_date + "]");
            deleteLp(m.getMdsId(), lp_date);
            log.info("Delete RealTimeBilling[" + i + "], Meter[" + m.getMdsId()+"] Date[" + lp_date + "]");
            deleteRT(m.getMdsId(), lp_date);
            log.info("Delete PowerQuality[" + (i++) + "], Meter[" + m.getMdsId()+"] Date[" + lp_date + "]");
            deletePQ(m.getMdsId(), lp_date);
        }
        log.info("Delete End LP, RealTimeBilling, PowerQuality Meter Count[" + meterlist.size() + "] Date[" + lp_date + "]");
        
        try {
            cal.setTime(sdf.parse(today));
            cal.add(Calendar.DAY_OF_YEAR, day_period*-1);
            String day_date = sdf.format(cal.getTime());
            log.info("Delete Day Meter Count[" + meterlist.size() + "] Date[" + day_date + "]");
            for (Meter m : meterlist.toArray(new Meter[0])) {
                deleteDay(m.getMdsId(), day_date);
            }
            log.info("Delete End Day Meter Count[" + meterlist.size() + "] Date[" + day_date + "]");
        }
        catch (ParseException e) {
            log.error(e, e);
        }
    }
    
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    private List<Meter> listMeter() {
        return meterDao.getAll();
    }
    
    private void deleteLp(String meterId, String yyyymmdd) {
        lpEMDao.delete(meterId, yyyymmdd);
    }
    
    private void deleteRT(String meterId, String yyyymmdd) {
        realTimeBillingEMDao.delete(meterId, yyyymmdd);
    }
    
    private void deletePQ(String meterId, String yyyymmdd) {
        pqDao.delete(meterId, yyyymmdd);
    }
    
    private void deleteDay(String meterId, String yyyymmdd) {
        dayDao.delete(meterId, yyyymmdd);
    }
    
    public static void main(String[] args) {
        if (args.length < 2)
            return;
        else {
            ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{"spring-quartz-meteringdatadelete.xml"}); 
            DataUtil.setApplicationContext(ctx);
            
            String startTime = args[0];
            String endTime = args[1];
            String datetime = null;
            MeterDeleteTask task = DataUtil.getBean(MeterDeleteTask.class);
            
            Calendar start_cal = Calendar.getInstance();
            Calendar end_cal = Calendar.getInstance();
            
            try {
                start_cal.setTime(DateTimeUtil.getDateFromYYYYMMDD(startTime));
                end_cal.setTime(DateTimeUtil.getDateFromYYYYMMDD(endTime));
                
                for (;start_cal.compareTo(end_cal) <= 0 ; start_cal.add(Calendar.DAY_OF_MONTH, 1)) {
                    datetime = DateTimeUtil.getDateString(start_cal.getTime());
                    task.execute(datetime.substring(0, 8));
                }
            }
            catch (Exception e) {
                log.error(e, e);
            }
        }
    }
}
