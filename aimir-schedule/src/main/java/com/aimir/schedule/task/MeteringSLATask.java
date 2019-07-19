package com.aimir.schedule.task;

import java.math.BigDecimal;
import java.math.MathContext;
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

import com.aimir.constants.CommonConstants;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.mvm.MeteringDataDao;
import com.aimir.dao.mvm.MeteringSLADao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.model.mvm.MeteringSLA;
import com.aimir.model.system.Code;
import com.aimir.util.TimeUtil;

@Transactional
public class MeteringSLATask {
    private static Log log = LogFactory.getLog(MeteringSLATask.class);

    @Autowired
    MeterDao meterDao;

    @Autowired
    CodeDao codeDao;

    @Autowired
    MeteringDataDao meteringDataDao;

    @Autowired
    SupplierDao supplierDao;

    @Autowired
    MeteringSLADao meteringSLADao;

    @SuppressWarnings("unchecked")
    public void excute() {
        SimpleDateFormat curSdf = new SimpleDateFormat("yyyyMMdd");
        Calendar cal = Calendar.getInstance();

        try {
            //String d ="20110308";
            String date = TimeUtil.getCurrentDay();
            log.debug(">>>>>>>>>>>>>>>>>>>> current date : " + date);
//      SimpleDateFormat curSdf = new SimpleDateFormat("yyyyMMdd");
//      Calendar cal = Calendar.getInstance();
        //cal.setTime(new Date());
            cal.setTime(curSdf.parse(date));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
//        cal.add(Calendar.DATE, -1); 오늘을 기준으로 5일전으로 변경함 - eunmiae
        String today = curSdf.format(cal.getTime());
        String searchStartDate0 = curSdf.format(cal.getTime());
        cal.add(Calendar.DATE, -1);
        String searchStartDate1 = curSdf.format(cal.getTime());
        cal.add(Calendar.DATE, -1);
        String searchStartDate2 = curSdf.format(cal.getTime());
        cal.add(Calendar.DATE, -1);
        String searchStartDate3 = curSdf.format(cal.getTime());
        cal.add(Calendar.DATE, -1);
        String searchStartDate4 = curSdf.format(cal.getTime());
        List<Code> codeList = codeDao.getChildCodes("1.3.1");

        List<Object> supplierList = meterDao.getMeterSupplierList();

        List<Integer> suppliers = new ArrayList<Integer>();

        for (Object s : supplierList) {
            Map<String, Object> supplierMap = (Map<String, Object>) s;
            suppliers.add((Integer) supplierMap.get("SUPPLIER"));
        }

        for (Integer supplier : suppliers) {
            // AllMeterCount : 등록된 모든 미터 수
            Integer allMeterCount = getMeterCount(today, codeList, supplier);
            System.out.println("##################allMeterCount:"+allMeterCount);

            // commPermitMeterCount : 설치 후 한번이라도 통신에 성공한 미터
            Integer commPermitMeterCount = getCommPermitMeterCount(supplier);
            System.out.println("##################commPermitMeterCount:"+commPermitMeterCount);
            
            // permitMeterCount : 5일전부터 현재까지 연속해서 검침에 성공한 미터 수
            Integer permitMeterCount = getPermitMeterCount(searchStartDate0,
                    searchStartDate1, searchStartDate2, searchStartDate3,
                    searchStartDate4, supplier);           
            System.out.println("##################permitMeterCount:"+permitMeterCount);
            
            // totalGatheredMeterCount : 오늘 검침에 성공한 미터 수
            Integer totalGatheredMeterCount = getTotalGatheredMeterCount(today,supplier);
            System.out.println("##################totalGatheredMeterCount:"+totalGatheredMeterCount);

            // slaMeterCount : contract의 apply_date이 오늘보다 과거인 미터 수
//            Integer slaMeterCount = getSLAMeterCount(today,supplier);
            Integer slaMeterCount = meterDao.getSLAMeterCount(today,supplier);

            System.out.println("##################slaMeterCount:"+slaMeterCount);

            MeteringSLA sla = new MeteringSLA();
            sla.setTotalInstalledMeters(Long.parseLong(allMeterCount.toString()));
            sla.setCommPermittedMeters(Long.parseLong(commPermitMeterCount.toString()));
            sla.setPermittedMeters(Long.parseLong(permitMeterCount.toString()));
            sla.setTotalGatheredMeters(Long.parseLong(totalGatheredMeterCount.toString()));
            sla.setSlaMeters(Long.parseLong(slaMeterCount.toString()));
            sla.setSuccessRate(getSuccessRate(totalGatheredMeterCount,slaMeterCount));
            sla.setDeliveredMeters(0l);
            sla.setSupplier(supplierDao.getSupplierById(supplier));
            sla.id.setYyyymmdd(today);
            System.out.println("##################SuccessRate:"+getSuccessRate(totalGatheredMeterCount,slaMeterCount));
            meteringSLADao.saveOrUpdate(sla);
        }
    }

    private Double getSuccessRate(Integer gatheredMeter,Integer slaMeter){
        BigDecimal zero = new BigDecimal(0);
        BigDecimal maxPercent = new BigDecimal(100);
        BigDecimal g = new BigDecimal(gatheredMeter);
        BigDecimal s = new BigDecimal(slaMeter);

        if (zero.compareTo(g) >= 0) {
            return zero.doubleValue();
        }

        BigDecimal percent = s.divide(g, MathContext.DECIMAL32).multiply(maxPercent).setScale(2, BigDecimal.ROUND_DOWN);
        return percent.doubleValue();
    }

    private int getMeterCount(String today, List<Code> meterTypeList,
            Integer supplier) {

        Integer meterCount = 0;
        for (Code code : meterTypeList) {
            Map<String, Object> condition = new HashMap<String, Object>();
            condition.put("searchStartDate", today);
            condition.put("meterType", code.getName());
            condition.put("supplierId", String.valueOf(supplier));
            meterCount = meterCount + meterDao.getMeterCount(condition);
        }
        return meterCount;
    }

    private int getCommPermitMeterCount(Integer supplier) {

        Integer meterCount = 0;
        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("meteringType", CommonConstants.MeterType.EnergyMeter
                .getMeteringTableName());
        condition.put("supplierId", String.valueOf(supplier));
        meterCount = meterCount
                + meteringDataDao.getCommPermitMeterCount(condition);
        condition.put("meteringType", CommonConstants.MeterType.GasMeter
                .getMeteringTableName());
        meterCount = meterCount
                + meteringDataDao.getCommPermitMeterCount(condition);
        condition.put("meteringType", CommonConstants.MeterType.WaterMeter
                .getMeteringTableName());
        meterCount = meterCount
                + meteringDataDao.getCommPermitMeterCount(condition);

        return meterCount;
    }

    private int getPermitMeterCount(String searchStartDate0,
            String searchStartDate1, String searchStartDate2,
            String searchStartDate3, String searchStartDate4, Integer supplier) {
 
        Integer meterCount = 0;
        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("searchStartDate0", searchStartDate0);
        condition.put("searchStartDate1", searchStartDate1);
        condition.put("searchStartDate2", searchStartDate2);
        condition.put("searchStartDate3", searchStartDate3);
        condition.put("searchStartDate4", searchStartDate4);
        condition.put("meteringType", CommonConstants.MeterType.EnergyMeter
                .getMeteringTableName());

        condition.put("supplierId", String.valueOf(supplier));
        meterCount = meterCount
                + meteringDataDao.getPermitMeterCount(condition);

        condition.put("meteringType", CommonConstants.MeterType.GasMeter
                .getMeteringTableName());
        meterCount = meterCount
                + meteringDataDao.getPermitMeterCount(condition);
        condition.put("meteringType", CommonConstants.MeterType.WaterMeter
                .getMeteringTableName());
        meterCount = meterCount
                + meteringDataDao.getPermitMeterCount(condition);
        return meterCount;
    }

    private int getTotalGatheredMeterCount(String searchStartDate,
            Integer supplier) {
        Integer meterCount = 0;
        Map<String, Object> condition = new HashMap<String, Object>();

        condition.put("searchStartDate", searchStartDate);
        condition.put("meteringType", CommonConstants.MeterType.EnergyMeter
                .getMeteringTableName());
        condition.put("supplierId", String.valueOf(supplier));
        meterCount = meterCount
                + meteringDataDao.getTotalGatheredMeterCount(condition);
        
        condition.put("meteringType", CommonConstants.MeterType.GasMeter
                .getMeteringTableName());
        meterCount = meterCount
                + meteringDataDao.getTotalGatheredMeterCount(condition);
        condition.put("meteringType", CommonConstants.MeterType.WaterMeter
                .getMeteringTableName());
        meterCount = meterCount
                + meteringDataDao.getTotalGatheredMeterCount(condition);

        return meterCount;
    }

//    private int getSLAMeterCount(String searchStartDate, Integer supplierId) {
//
//        Integer meterCount = 0;
//        Map<String, Object> condition = new HashMap<String, Object>();
//
//        condition.put("searchStartDate", searchStartDate);
//        condition.put("meteringType", CommonConstants.MeterType.EnergyMeter
//                .getMeteringTableName());
//        condition.put("supplierId", String.valueOf(supplierId));
//        
//       
//        meterCount = meterCount + meteringDataDao.getSLAMeterCount(condition);
//
//        condition.put("meteringType", CommonConstants.MeterType.GasMeter
//                .getMeteringTableName());
//        meterCount = meterCount + meteringDataDao.getSLAMeterCount(condition);
//
//        condition.put("meteringType", CommonConstants.MeterType.WaterMeter
//                .getMeteringTableName());
//        meterCount = meterCount + meteringDataDao.getSLAMeterCount(condition);
//
//        return meterCount;
//    }

}