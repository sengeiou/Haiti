package com.aimir.schedule.task;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.DefaultChannel;
import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.dao.mvm.DayEMDao;
import com.aimir.dao.mvm.DayGMDao;
import com.aimir.dao.mvm.DayWMDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.fep.meter.saver.OmniMDSaver;
import com.aimir.model.system.Location;
import com.aimir.schedule.util.SAPProperty;

@Transactional
@Deprecated
public class AnsansiSumTask {

    private static Log log = LogFactory.getLog(AnsansiSumTask.class);

    @Autowired
    DayEMDao dayEmDao;

    @Autowired
    DayWMDao dayWmDao;

    @Autowired
    DayGMDao dayGmDao;

    @Autowired
    LocationDao locationDao;

    @Autowired
    OmniMDSaver sumMDSaver;
    
    @Autowired
    HibernateTransactionManager transactionManager;

    public void execute() {
    	log.debug("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<AnsansiSumTask Start<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
    	int sumTime = Integer.parseInt(SAPProperty.getProperty("bems.sum.time"));
    	log.debug("running period : " + sumTime);

        List<Integer> rootIdArr = locationDao.getRoot();
        SimpleDateFormat curSdf = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat curSdf1 = new SimpleDateFormat("HH");
        SimpleDateFormat curSdf2 = new SimpleDateFormat("mm");
 
        TransactionStatus txStatus = null;
        DefaultTransactionDefinition txDefine = new DefaultTransactionDefinition();
        txDefine.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        
        
        
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, -sumTime);  
        int count=0;
        try {
            txStatus = transactionManager.getTransaction(txDefine);
            for (int i = 0; i < (sumTime+1); i++) { 
		log.debug("i : " + i);
                cal.add(Calendar.HOUR, count);
                String searchTime = curSdf.format(cal.getTime());
                String hh0 = curSdf1.format(cal.getTime());
                String mm0 = curSdf2.format(cal.getTime());

                Integer rootId = rootIdArr.get(0);
                List<Location> loc = locationDao.getChildren(rootId);
                
                log.debug("searchDate["+searchTime+"] time["+hh0+"]");	 
                   
                List<CommonConstants.MeterType> meterTypeList = new ArrayList<CommonConstants.MeterType>();
                String mcuName = "1022";  //가상 DCU Name
                
                for (int j = 0; j < loc.size(); j++) {
                	
                	if("안산시청".equals(loc.get(j).getName())) {
                		meterTypeList.add(MeterType.EnergyMeter);
                		meterTypeList.add(MeterType.GasMeter);
                		meterTypeList.add(MeterType.WaterMeter);
                	} else if ("구청".equals(loc.get(j).getName())) {
                		meterTypeList.add(MeterType.EnergyMeter);
                		meterTypeList.add(MeterType.WaterMeter);
                	} else if ("정수장".equals(loc.get(j).getName())) {
                		meterTypeList.add(MeterType.EnergyMeter);
                	} else if ("주민센터".equals(loc.get(j).getName())) {
                		meterTypeList.add(MeterType.EnergyMeter);
                		meterTypeList.add(MeterType.WaterMeter);
                	} else if ("주거단지".equals(loc.get(j).getName())) {
                	    List<Location> subloc = locationDao.getChildren(loc.get(j).getId());
                	    for (int k = 0; k < subloc.size(); k++) {
                	        if ("공동주택".equals(subloc.get(k).getName())) {
                	            meterTypeList.add(MeterType.EnergyMeter);
                	            emGmWmSave(subloc.get(k).getName(), meterTypeList, subloc.get(k).getId(), mcuName, searchTime, hh0, mm0);
                                meterTypeList.clear();
                	        }
                	    }
                        meterTypeList.add(MeterType.EnergyMeter);
                    }
                	emGmWmSave(loc.get(j).getName(), meterTypeList, loc.get(j).getId(), mcuName, searchTime, hh0, mm0);
                	meterTypeList.clear();
                	
				}
                
                meterTypeList.add(MeterType.EnergyMeter);
        		meterTypeList.add(MeterType.GasMeter);
        		meterTypeList.add(MeterType.WaterMeter);
                emGmWmSave("안산시", meterTypeList, rootId, mcuName, searchTime, hh0, mm0);
       
                count=1;
            }
            transactionManager.commit(txStatus);
        }
        catch (Exception e) {
            transactionManager.rollback(txStatus);
            log.error(e);
        }
        log.debug("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<AnsansiSumTask End<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        
    }

    void emGmWmSave(String meterId, List<CommonConstants.MeterType> meterTypeList, Integer newLoc, String mcuName, String searchDate, String hh, String mm) 
	{
        Map<String, Object> condition0 = new HashMap<String, Object>();

        condition0.put("locationId", newLoc);
        condition0.put("channel", DefaultChannel.Usage.getCode());
        condition0.put("startDate", searchDate);
        condition0.put("hh0", hh);

        List<Object> dayList0 = null;
        String meterIdByType = null;
        for (int i = 0; i < meterTypeList.size(); i++) {
        	 if (meterTypeList.get(i) == MeterType.EnergyMeter) {
        		 meterIdByType = "EM_" + meterId;
                 dayList0 = dayEmDao.getConsumptionEmCo2DayValuesParentId(condition0);
        	 } else if (meterTypeList.get(i) == MeterType.GasMeter) {
            	 meterIdByType = "GM_" + meterId;
                 dayList0 = dayGmDao.getConsumptionGmCo2DayValuesParentId(condition0);
        	 } else if (meterTypeList.get(i) == MeterType.WaterMeter) {
        		 meterIdByType = "WM_" + meterId;
                 dayList0 = dayWmDao.getConsumptionWmCo2DayValuesParentId(condition0);
        	 }
        	 Map<String, Object> dayMap0 = (Map<String, Object>)dayList0.get(0);
        	 BigDecimal emSum0 = new BigDecimal(
                     dayMap0.get("VALUE_" + hh) == null ? 0 : Double.parseDouble(dayMap0
                             .get("VALUE_" + hh).toString()));
             
             log.info("SUM VALUE[" + emSum0 +"] hh["+hh+"]");
	     	 log.info("mm["+mm+"]");
	
	     	double[] lplist0 = null;
	     	int lp_interval = 60;
     		lplist0 = new double[1];
	     	lplist0[0] = emSum0.doubleValue();
	     		
             try {
            	 //각 이름 별(안산시청, 구청, 정수장, 주민센터, 안산시, 주거단지) 미터, 모뎀을 가상으로 미리 등록해놓는다
                 sumMDSaver.save(meterIdByType, mcuName, meterIdByType, searchDate + hh
                         + "00", emSum0.doubleValue(), lp_interval, lplist0);
             } catch (Exception e) {
                 // TODO Auto-generated catch block
                 e.printStackTrace();
             }
        	 
		}
       
    }
}
