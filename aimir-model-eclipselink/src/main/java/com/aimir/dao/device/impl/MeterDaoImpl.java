package com.aimir.dao.device.impl;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.MeterCodes;
import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.mvm.MeteringLpDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.model.device.Meter;
import com.aimir.util.CalendarUtil;
import com.aimir.util.Condition;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeUtil;

@Repository(value = "meterDao")
public class MeterDaoImpl extends AbstractJpaDao<Meter, Integer> implements MeterDao {

    @Autowired
    MeteringLpDao meteringlpDao;

    @Autowired
    CodeDao codedao;

    @Autowired
    SupplierDao supplierDao;

    protected static Log logger = LogFactory.getLog(MeterDaoImpl.class);

    public MeterDaoImpl() {
        super(Meter.class);
    }

    @Transactional(readOnly=true, propagation=Propagation.REQUIRED)
    public Meter get(String mdsId) {
        return findByCondition("mdsId", mdsId);
    }

    /**
     * 제주 실증단지에서 사용하는 11자리 미터키
     * @param installProperty
     * @return
     */
    @Transactional(readOnly=true, propagation=Propagation.REQUIRED)
    public Meter getInstallProperty(String installProperty) {
        return findByCondition("installProperty", installProperty);
    }

    // Meter 정보 저장
    public Serializable setMeter(Meter meter) {
        return add(meter);
    }

    @Transactional(readOnly=true, propagation=Propagation.REQUIRED)
    public Meter getMeterByModemDeviceSerial(String deviceSerial, int modemPort) {
        String sql = "select m from Meter m where m.modemPort = :modemPort and m.modem.deviceSerial = :deviceSerial";
        Query query = em.createQuery(sql, Meter.class);
        query.setParameter("modemPort", modemPort);
        query.setParameter("deviceSerial", deviceSerial);
        return (Meter)query.getSingleResult();
    }

    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getMetersByMcuName(String name) {
        String sql = "select id from Meter where m.modem.mcu.sysID = :mcuId";
        Query query = em.createQuery(sql, Integer.class);
        query.setParameter("mcuId", name);
        return query.getResultList();

    }

    @Override
    public Map<String, Object> getMeteringFailureMeter(Map<String, Object> param) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer getMeterCount(Map<String, Object> params) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer getAllMissingMeterCount(Map<String, Object> params) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer getPatialMissingMeterCount(Map<String, Object> params) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMissingMetersByHour(Map<String, Object> params) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMissingMetersByDay(Map<String, Object> params) {
        // TODO Auto-generated method stub
        return null;
    }

    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getMissingMeters(Map<String, Object> params) {
        String searchStartDate = (String) params.get("searchStartDate");
        String searchEndDate = (String) params.get("searchEndDate");
        String meterType = StringUtil.nullToBlank(params.get("meterType"));
        String supplierId = StringUtil.nullToBlank(params.get("supplierId"));
        Integer channel = (Integer) params.get("channel");
        String mdsId = StringUtil.nullToBlank(params.get("mdsId"));
        String deviceType = StringUtil.nullToBlank((String) params.get("deviceType"));
        String deviceId = StringUtil.nullToBlank((String) params.get("deviceId"));

        String today = TimeUtil.getCurrentTimeMilli(); // yyyyMMddHHmmss
        String currDate = today.substring(0, 8);
        String currHour = today.substring(8, 10);
        Integer currMinute = Integer.parseInt(today.substring(10, 12));

        String lpTable = CommonConstants.MeterType.valueOf(meterType).getLpTableName();

        int period = 0;
        try {
            period = TimeUtil.getDayDuration(searchStartDate, searchEndDate) + 1;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        StringBuffer sbQueryAllPeriod = new StringBuffer();
        sbQueryAllPeriod.append("\nSELECT customer.name ");
        sbQueryAllPeriod.append("\n       ,modem.device_serial ");
        sbQueryAllPeriod.append("\n       ,mcu.sys_name AS mcuName ");
        sbQueryAllPeriod.append("\n       ,m1.mds_id ");
        sbQueryAllPeriod.append("\n       ,m1.last_read_date ");
        sbQueryAllPeriod.append("\n       ,m1.id ");
        sbQueryAllPeriod.append("\n       ,m1.lp_interval ");
        sbQueryAllPeriod.append("\nFROM meter m1 ");
        sbQueryAllPeriod.append("\n     LEFT OUTER JOIN contract contract ON m1.id = contract.meter_id ");
        sbQueryAllPeriod.append("\n     LEFT OUTER JOIN customer customer ON contract.customer_id = customer.id ");
        sbQueryAllPeriod.append("\n     LEFT OUTER JOIN modem modem ON m1.modem_id = modem.id ");
        sbQueryAllPeriod.append("\n     LEFT OUTER JOIN mcu mcu ON modem.mcu_id = mcu.id ");
        sbQueryAllPeriod.append("\n     LEFT OUTER JOIN code cd ON cd.id = m1.meter_status ");
        sbQueryAllPeriod.append("\nWHERE m1.install_date <= '"+searchStartDate + CommonConstants.DefaultDate.LAST_HHMMSS.getValue()+"'");//:installDate ");
        sbQueryAllPeriod.append("\nAND m1.meter = '"+meterType+ "' ");
        if (!"".equals(supplierId)) {
            sbQueryAllPeriod.append("\nAND m1.supplier_id = " + String.valueOf(supplierId) + " ");
        }
        if (!"".equals(mdsId)) {
            sbQueryAllPeriod.append("\nAND m1.mds_id LIKE '" + "%" + mdsId + "%' ");
        }
        // deviceId , deviceType 의 null 값 체크
        if (!"".equals(deviceId) && !"".equals(deviceType)) {
            if (CommonConstants.DeviceType.MCU.getCode().equals(Integer.parseInt(deviceType))) {
                sbQueryAllPeriod.append("\nAND mcu.sys_name LIKE '%" + deviceId + "%' ");
            } else {
                sbQueryAllPeriod.append("\nAND modem.device_serial LIKE '%" + deviceId + "%' ");
            }
        }
        sbQueryAllPeriod.append("\nAND   (cd.id IS NULL ");
        sbQueryAllPeriod.append("\n    OR cd.code != '" +MeterCodes.DELETE_STATUS.getCode()+"' ");
        sbQueryAllPeriod.append("\n    OR (cd.code = '" +MeterCodes.DELETE_STATUS.getCode()+"' AND m1.delete_date > '"+searchStartDate + "235959"+"' )) ");
        sbQueryAllPeriod.append("\nAND m1.id NOT IN ( ");
        sbQueryAllPeriod.append("\n    SELECT x.id ");
        sbQueryAllPeriod.append("\n    FROM ( ");
        sbQueryAllPeriod.append("\n        SELECT m.id,lp.yyyymmdd ");
        sbQueryAllPeriod.append("\n        FROM meter m ");
        sbQueryAllPeriod.append("\n             LEFT OUTER JOIN ");
        sbQueryAllPeriod.append("\n             code c ");
        sbQueryAllPeriod.append("\n             ON c.id = m.meter_status ");
        sbQueryAllPeriod.append("\n             LEFT OUTER JOIN ");
        sbQueryAllPeriod.append("\n             ").append(lpTable).append(" lp ");
        sbQueryAllPeriod.append("\n             ON  lp.mdev_id = m.mds_id ");
        sbQueryAllPeriod.append("\n             AND lp.yyyymmddhh BETWEEN '"+searchStartDate + "00"+"' AND '"+searchEndDate + "23"+"' ");
        sbQueryAllPeriod.append("\n             AND lp.channel = "+channel.toString()+" ");
        sbQueryAllPeriod.append("\n        WHERE m.install_date <= '"+searchStartDate + CommonConstants.DefaultDate.LAST_HHMMSS.getValue()+"' ");
//        sbQueryAllPeriod.append("\n        AND 1 = CASE WHEN m.lp_interval = 1 THEN CASE WHEN lp.value_cnt < 60   THEN 0 ELSE 1 END ");
//        sbQueryAllPeriod.append("\n                     WHEN m.lp_interval = 5 THEN CASE WHEN lp.value_cnt < 12   THEN 0 ELSE 1 END ");
//        sbQueryAllPeriod.append("\n                     WHEN m.lp_interval = 10 THEN CASE WHEN lp.value_cnt < 6   THEN 0 ELSE 1 END ");
//        sbQueryAllPeriod.append("\n                     WHEN m.lp_interval = 15 THEN CASE WHEN lp.value_cnt < 4   THEN 0 ELSE 1 END ");
//        sbQueryAllPeriod.append("\n                     WHEN m.lp_interval = 30 THEN CASE WHEN lp.value_cnt < 2   THEN 0 ELSE 1 END ");
//        sbQueryAllPeriod.append("\n                     WHEN m.lp_interval = 60 THEN CASE WHEN lp.value_cnt < 1   THEN 0 ELSE 1 END ");
//        sbQueryAllPeriod.append("\n                ELSE 0 end ");
        sbQueryAllPeriod.append("\n        AND   (m.lp_interval IN (1, 5, 10, 15, 30, 60) ");
        sbQueryAllPeriod.append("\n           AND lp.value_cnt >= (60/m.lp_interval)) ");
        sbQueryAllPeriod.append("\n        AND   (c.id IS NULL ");
        sbQueryAllPeriod.append("\n            OR c.code != '"+MeterCodes.DELETE_STATUS.getCode()+"' ");
        sbQueryAllPeriod.append("\n            OR (c.code = '"+MeterCodes.DELETE_STATUS.getCode()+"' AND m.delete_date > '"+searchStartDate + "235959"+"')) ");
        sbQueryAllPeriod.append("\n        GROUP BY m.id, lp.yyyymmdd ");
        sbQueryAllPeriod.append("\n        HAVING COUNT(m.id) = 24 ");
        sbQueryAllPeriod.append("\n    ) x ");
        sbQueryAllPeriod.append("\n    WHERE x.yyyymmdd IS NOT NULL ");
        sbQueryAllPeriod.append("\n    GROUP BY x.id ");
        sbQueryAllPeriod.append("\n    HAVING COUNT(x.id) = "+String.valueOf(period)+" ");
        sbQueryAllPeriod.append("\n) ");

        StringBuffer sbQueryAllPeriodWithToday = new StringBuffer();
        sbQueryAllPeriodWithToday.append("\nSELECT customer.name ");
        sbQueryAllPeriodWithToday.append("\n       ,modem.device_serial ");
        sbQueryAllPeriodWithToday.append("\n       ,mcu.sys_name AS mcuname ");
        sbQueryAllPeriodWithToday.append("\n       ,m1.mds_id ");
        sbQueryAllPeriodWithToday.append("\n       ,m1.last_read_date ");
        sbQueryAllPeriodWithToday.append("\n       ,m1.id ");
        sbQueryAllPeriodWithToday.append("\n       ,m1.lp_interval ");
        sbQueryAllPeriodWithToday.append("\nFROM meter m1 ");
        sbQueryAllPeriodWithToday.append("\n     LEFT OUTER JOIN contract contract ON m1.id = contract.meter_id ");
        sbQueryAllPeriodWithToday.append("\n     LEFT OUTER JOIN customer customer ON contract.customer_id = customer.id ");
        sbQueryAllPeriodWithToday.append("\n     LEFT OUTER JOIN modem modem ON m1.modem_id = modem.id ");
        sbQueryAllPeriodWithToday.append("\n     LEFT OUTER JOIN mcu mcu ON modem.mcu_id = mcu.id ");
        sbQueryAllPeriodWithToday.append("\n     LEFT OUTER JOIN code cd ON cd.id = m1.meter_status ");
//        sbQueryAllPeriodWithToday.append("\nWHERE m1.install_date <= :installDate ");
//        sbQueryAllPeriodWithToday.append("\nAND   m1.meter = :meterType ");
        sbQueryAllPeriodWithToday.append("\nWHERE m1.install_date <= '"+searchStartDate + CommonConstants.DefaultDate.LAST_HHMMSS.getValue()+"' ");//:installDate ");
        sbQueryAllPeriodWithToday.append("\nAND m1.meter = '"+meterType+ "' ");
        if (!"".equals(supplierId)) {
            sbQueryAllPeriodWithToday.append("\nAND   m1.supplier_id = " + String.valueOf(supplierId) + " ");
        }
        if (!"".equals(mdsId)) {
            sbQueryAllPeriodWithToday.append("\nAND   m1.mds_id LIKE '" + "%" + mdsId + "%' ");
        }
        // deviceId , deviceType 의 null 값 체크
        if (!"".equals(deviceId) && !"".equals(deviceType)) {
            if (CommonConstants.DeviceType.MCU.getCode().equals(Integer.parseInt(deviceType))) {
                sbQueryAllPeriodWithToday.append("\nAND   mcu.sys_name LIKE '%" + deviceId + "%' ");
            } else {
                sbQueryAllPeriodWithToday.append("\nAND   modem.device_serial LIKE '%" + deviceId + "%' ");
            }
        }
        sbQueryAllPeriodWithToday.append("\nAND   (cd.id IS NULL ");
        sbQueryAllPeriodWithToday.append("\n    OR cd.code != '" +MeterCodes.DELETE_STATUS.getCode()+"' ");
        sbQueryAllPeriodWithToday.append("\n    OR (cd.code = '" +MeterCodes.DELETE_STATUS.getCode()+"' AND m1.delete_date > '"+searchStartDate + "235959"+"' )) ");
        sbQueryAllPeriodWithToday.append("\nAND   m1.id NOT IN ( ");
        sbQueryAllPeriodWithToday.append("\n    SELECT i3.id");
        sbQueryAllPeriodWithToday.append("\n    FROM ( ");
        sbQueryAllPeriodWithToday.append("\n        SELECT i1.id FROM ( ");
        sbQueryAllPeriodWithToday.append("\n            SELECT x.id ");
        sbQueryAllPeriodWithToday.append("\n            FROM ( ");
        sbQueryAllPeriodWithToday.append("\n                SELECT m.id,lp.yyyymmdd ");
        sbQueryAllPeriodWithToday.append("\n                FROM meter m ");
        sbQueryAllPeriodWithToday.append("\n                     LEFT OUTER JOIN ");
        sbQueryAllPeriodWithToday.append("\n                     code c ");
        sbQueryAllPeriodWithToday.append("\n                     ON c.id = m.meter_status ");
        sbQueryAllPeriodWithToday.append("\n                     LEFT OUTER JOIN ").append(lpTable).append(" lp ");
        sbQueryAllPeriodWithToday.append("\n                     ON lp.mdev_id = m.mds_id ");
        sbQueryAllPeriodWithToday.append("\n                     AND lp.yyyymmddhh BETWEEN '"+searchStartDate + "00"+"' AND '"+CalendarUtil.getDateWithoutFormat(searchEndDate, Calendar.DATE, -1) + "23"+"' ");
        sbQueryAllPeriodWithToday.append("\n                     AND lp.channel = "+channel.toString()+" ");
        sbQueryAllPeriodWithToday.append("\n                WHERE m.install_date <= '"+searchStartDate + CommonConstants.DefaultDate.LAST_HHMMSS.getValue()+"' ");

//        sbQueryAllPeriodWithToday.append("\n        AND 1 = CASE WHEN m.lp_interval = 1 THEN CASE WHEN lp.value_cnt < 60 THEN 0 ELSE 1 END ");
//        sbQueryAllPeriodWithToday.append("\n                     WHEN m.lp_interval = 5 THEN CASE WHEN lp.value_cnt < 12 THEN 0 ELSE 1 END ");
//        sbQueryAllPeriodWithToday.append("\n                     WHEN m.lp_interval = 10 THEN CASE WHEN lp.value_cnt < 6 THEN 0 ELSE 1 END ");
//        sbQueryAllPeriodWithToday.append("\n                     WHEN m.lp_interval = 15 THEN CASE WHEN lp.value_cnt < 4 THEN 0 ELSE 1 END ");
//        sbQueryAllPeriodWithToday.append("\n                     WHEN m.lp_interval = 30 THEN CASE WHEN lp.value_cnt < 2 THEN 0 ELSE 1 END ");
//        sbQueryAllPeriodWithToday.append("\n                     WHEN m.lp_interval = 60 THEN CASE WHEN lp.value_cnt < 1 THEN 0 ELSE 1 END ");
//        sbQueryAllPeriodWithToday.append("\n                ELSE 0 end ");
        sbQueryAllPeriodWithToday.append("\n                AND   (m.lp_interval IN (1, 5, 10, 15, 30, 60) ");
        sbQueryAllPeriodWithToday.append("\n                   AND lp.value_cnt >= (60/m.lp_interval)) ");
        sbQueryAllPeriodWithToday.append("\n                AND   (c.id IS NULL ");
        sbQueryAllPeriodWithToday.append("\n                    OR c.code != '"+MeterCodes.DELETE_STATUS.getCode()+"' ");
        sbQueryAllPeriodWithToday.append("\n                    OR (c.code = '"+MeterCodes.DELETE_STATUS.getCode()+"' AND m.delete_date > '"+searchStartDate + "235959"+"')) ");
        sbQueryAllPeriodWithToday.append("\n                GROUP BY m.id,lp.yyyymmdd ");
        sbQueryAllPeriodWithToday.append("\n                HAVING COUNT(m.id) = 24 ");
        sbQueryAllPeriodWithToday.append("\n            )x ");
        sbQueryAllPeriodWithToday.append("\n            WHERE x.yyyymmdd IS NOT NULL ");
        sbQueryAllPeriodWithToday.append("\n            GROUP BY x.id ");
        sbQueryAllPeriodWithToday.append("\n            HAVING COUNT(x.id) = "+String.valueOf(period)+" ");
        sbQueryAllPeriodWithToday.append("\n        ) i1 ");

        sbQueryAllPeriodWithToday.append("\n        INNER JOIN ");

        sbQueryAllPeriodWithToday.append("\n        ( ");
        sbQueryAllPeriodWithToday.append("\n            SELECT m.id ");
        sbQueryAllPeriodWithToday.append("\n            FROM meter m ");
        sbQueryAllPeriodWithToday.append("\n                 LEFT OUTER JOIN ");
        sbQueryAllPeriodWithToday.append("\n                 code c ");
        sbQueryAllPeriodWithToday.append("\n                 ON c.id = m.meter_status ");
        sbQueryAllPeriodWithToday.append("\n                 LEFT OUTER JOIN ").append(lpTable).append(" lp ");
        sbQueryAllPeriodWithToday.append("\n                 ON lp.mdev_id = m.mds_id ");
        sbQueryAllPeriodWithToday.append("\n                 AND lp.yyyymmddhh BETWEEN '"+currDate + "00"+"' AND '"+currDate + "23"+"' ");
        sbQueryAllPeriodWithToday.append("\n                 AND lp.hh < '"+currHour+"'  ");
        sbQueryAllPeriodWithToday.append("\n                 AND lp.channel = " + channel.toString()+ " ");
        sbQueryAllPeriodWithToday.append("\n            WHERE m.install_date <= '"+searchStartDate + CommonConstants.DefaultDate.LAST_HHMMSS.getValue()+"'");
//        sbQueryAllPeriodWithToday.append("\n    AND 1 = CASE WHEN m.lp_interval = 1 THEN CASE WHEN lp.value_cnt < 60   THEN 0 ELSE 1 END ");
//        sbQueryAllPeriodWithToday.append("\n                 WHEN m.lp_interval = 5 THEN CASE WHEN lp.value_cnt < 12   THEN 0 ELSE 1 END ");
//        sbQueryAllPeriodWithToday.append("\n                 WHEN m.lp_interval = 10 THEN CASE WHEN lp.value_cnt < 6   THEN 0 ELSE 1 END ");
//        sbQueryAllPeriodWithToday.append("\n                 WHEN m.lp_interval = 15 THEN CASE WHEN lp.value_cnt < 4   THEN 0 ELSE 1 END ");
//        sbQueryAllPeriodWithToday.append("\n                 WHEN m.lp_interval = 30 THEN CASE WHEN lp.value_cnt < 2   THEN 0 ELSE 1 END ");
//        sbQueryAllPeriodWithToday.append("\n                 WHEN m.lp_interval = 60 THEN CASE WHEN lp.value_cnt < 1   THEN 0 ELSE 1 END ");
//        sbQueryAllPeriodWithToday.append("\n            ELSE 0 end ");
        sbQueryAllPeriodWithToday.append("\n            AND   (m.lp_interval IN (1, 5, 10, 15, 30, 60) ");
        sbQueryAllPeriodWithToday.append("\n               AND lp.value_cnt >= (60/m.lp_interval)) ");
        sbQueryAllPeriodWithToday.append("\n            AND   (c.id IS NULL ");
        sbQueryAllPeriodWithToday.append("\n                OR c.code != '" +MeterCodes.DELETE_STATUS.getCode()+"' ");
        sbQueryAllPeriodWithToday.append("\n                OR (c.code = '" +MeterCodes.DELETE_STATUS.getCode()+"' AND m.delete_date > '"+searchStartDate + "235959"+"' )) ");
        sbQueryAllPeriodWithToday.append("\n            GROUP BY m.id, lp.yyyymmdd ");
//        sbQueryAllPeriodWithToday.append("\n            HAVING COUNT(m.id) = :currHour) i2 ON i1.id=i2.id ) i3");
        sbQueryAllPeriodWithToday.append("\n            HAVING COUNT(m.id) = " +currHour+" ");
        sbQueryAllPeriodWithToday.append("\n        ) i2 ON i1.id = i2.id ");
        sbQueryAllPeriodWithToday.append("\n    ) i3");

        sbQueryAllPeriodWithToday.append("\n    INNER JOIN ");

        sbQueryAllPeriodWithToday.append("\n    ( ");
        sbQueryAllPeriodWithToday.append("\n        SELECT m.id ");
        sbQueryAllPeriodWithToday.append("\n        FROM meter m ");
        sbQueryAllPeriodWithToday.append("\n             LEFT OUTER JOIN ");
        sbQueryAllPeriodWithToday.append("\n             code c ");
        sbQueryAllPeriodWithToday.append("\n             ON c.id = m.meter_status ");
        sbQueryAllPeriodWithToday.append("\n             LEFT OUTER JOIN ").append(lpTable).append(" lp ");
        sbQueryAllPeriodWithToday.append("\n             ON lp.mdev_id = m.mds_id ");
        sbQueryAllPeriodWithToday.append("\n             AND lp.yyyymmddhh BETWEEN '"+currDate + "00"+"' AND '"+currDate + "23"+"' ");
        sbQueryAllPeriodWithToday.append("\n             AND lp.hh = '" +currHour+"' ");
        sbQueryAllPeriodWithToday.append("\n             AND lp.channel = "+channel.toString()+" ");
        sbQueryAllPeriodWithToday.append("\n        WHERE m.install_date <= '"+searchStartDate + CommonConstants.DefaultDate.LAST_HHMMSS.getValue()+"' ");

        if (currMinute < 1) {
//            sbQueryAllPeriodWithToday.append("\n        AND 1 = CASE WHEN m.lp_interval = 1  THEN CASE WHEN 1 <= lp.value_cnt THEN 1 ELSE 0 END ");
//            sbQueryAllPeriodWithToday.append("\n                     WHEN m.lp_interval = 5  THEN CASE WHEN 1 <= lp.value_cnt THEN 1 ELSE 0 END ");
//            sbQueryAllPeriodWithToday.append("\n                     WHEN m.lp_interval = 10 THEN CASE WHEN 1 <= lp.value_cnt THEN 1 ELSE 0 END ");
//            sbQueryAllPeriodWithToday.append("\n                     WHEN m.lp_interval = 15 THEN CASE WHEN 1 <= lp.value_cnt THEN 1 ELSE 0 END ");
//            sbQueryAllPeriodWithToday.append("\n                     WHEN m.lp_interval = 30 THEN CASE WHEN 1 <= lp.value_cnt THEN 1 ELSE 0 END ");
//            sbQueryAllPeriodWithToday.append("\n                     WHEN m.lp_interval = 60 THEN CASE WHEN 1 <= lp.value_cnt THEN 1 ELSE 0 END ");
//            sbQueryAllPeriodWithToday.append("\n                ELSE 0 END) i4 ON i3.id=i4.id ");
            sbQueryAllPeriodWithToday.append("\n        AND   (m.lp_interval IN (1, 5, 10, 15, 30, 60) ");
            sbQueryAllPeriodWithToday.append("\n           AND lp.value_cnt >= 1) ");
        } else {
//            sbQueryAllPeriodWithToday.append("\n        AND 1 = CASE WHEN m.lp_interval = 1  THEN CASE WHEN (:currMinute/1) < lp.value_cnt THEN 1 ELSE 0 END ");
//            sbQueryAllPeriodWithToday.append("\n                     WHEN m.lp_interval = 5  THEN CASE WHEN (:currMinute/5) < lp.value_cnt THEN 1 ELSE 0 END ");
//            sbQueryAllPeriodWithToday.append("\n                     WHEN m.lp_interval = 10 THEN CASE WHEN (:currMinute/10) < lp.value_cnt THEN 1 ELSE 0 END ");
//            sbQueryAllPeriodWithToday.append("\n                     WHEN m.lp_interval = 15 THEN CASE WHEN (:currMinute/15) < lp.value_cnt THEN 1 ELSE 0 END ");
//            sbQueryAllPeriodWithToday.append("\n                     WHEN m.lp_interval = 30 THEN CASE WHEN (:currMinute/30) < lp.value_cnt THEN 1 ELSE 0 END ");
//            sbQueryAllPeriodWithToday.append("\n                     WHEN m.lp_interval = 60 THEN CASE WHEN (:currMinute/60) < lp.value_cnt THEN 1 ELSE 0 END ");
//            sbQueryAllPeriodWithToday.append("\n                ELSE 0 END) i4 ON i3.id=i4.id ");
            sbQueryAllPeriodWithToday.append("\n        AND   (m.lp_interval IN (1, 5, 10, 15, 30, 60) ");
            sbQueryAllPeriodWithToday.append("\n           AND lp.value_cnt > ("+currMinute.toString()+"/m.lp_interval)) ");
        }
        sbQueryAllPeriodWithToday.append("\n        AND   (c.id IS NULL ");
        sbQueryAllPeriodWithToday.append("\n            OR c.code != '" +MeterCodes.DELETE_STATUS.getCode()+"' ");
        sbQueryAllPeriodWithToday.append("\n            OR (c.code = '" +MeterCodes.DELETE_STATUS.getCode()+"' AND m1.delete_date > '"+searchStartDate + "235959"+"' )) ");
        sbQueryAllPeriodWithToday.append("\n    ) i4 ON i3.id = i4.id ");
        sbQueryAllPeriodWithToday.append("\n) ");

        StringBuffer sbQueryToday = new StringBuffer();
        sbQueryToday.append("\nSELECT customer.name ");
        sbQueryToday.append("\n       ,modem.device_serial ");
        sbQueryToday.append("\n       ,mcu.sys_name as mcuName ");
        sbQueryToday.append("\n       ,m1.mds_id ");
        sbQueryToday.append("\n       ,m1.last_read_date ");
        sbQueryToday.append("\n       ,m1.id ");
        sbQueryToday.append("\n       ,m1.lp_interval ");
        sbQueryToday.append("\nFROM meter m1 ");
        sbQueryToday.append("\n     LEFT OUTER JOIN contract contract ON m1.id = contract.meter_id ");
        sbQueryToday.append("\n     LEFT OUTER JOIN customer customer ON contract.customer_id = customer.id ");
        sbQueryToday.append("\n     LEFT OUTER JOIN modem modem ON m1.modem_id = modem.id ");
        sbQueryToday.append("\n     LEFT OUTER JOIN mcu mcu ON modem.mcu_id = mcu.id ");
        sbQueryToday.append("\n     LEFT OUTER JOIN code cd ON cd.id = m1.meter_status ");
        sbQueryToday.append("\nWHERE m1.install_date <= '"+searchStartDate + CommonConstants.DefaultDate.LAST_HHMMSS.getValue()+"'");
        sbQueryToday.append("\nAND   m1.meter = '"+meterType+ "' ");
        if (!"".equals(supplierId)) {
            sbQueryToday.append("\nAND   m1.supplier_id = " + String.valueOf(supplierId) + " ");
        }
        if (!"".equals(mdsId)) {
            sbQueryToday.append("\nAND   m1.mds_id LIKE '" + "%" + mdsId + "%' ");
        }
        // deviceId , deviceType 의 null 값 체크
        if (!"".equals(deviceId) && !"".equals(deviceType)) {
            if (CommonConstants.DeviceType.MCU.getCode().equals(Integer.parseInt(deviceType))) {
                sbQueryToday.append("\nAND   mcu.sys_name LIKE '%" + deviceId + "%' ");
            } else {
                sbQueryToday.append("\nAND   modem.device_serial LIKE '%" + deviceId + "%' ");
            }
        }
        sbQueryToday.append("\nAND   (cd.id IS NULL ");
        sbQueryToday.append("\n    OR cd.code != '" +MeterCodes.DELETE_STATUS.getCode()+"' ");
        sbQueryToday.append("\n    OR (cd.code = '" +MeterCodes.DELETE_STATUS.getCode()+"' AND m1.delete_date > '"+searchStartDate + "235959"+"' )) ");
        sbQueryToday.append("\nAND   m1.id NOT IN ( ");
        sbQueryToday.append("\n    SELECT i1.id ");
        sbQueryToday.append("\n    FROM ( ");
        sbQueryToday.append("\n        SELECT m.id ");
        sbQueryToday.append("\n        FROM meter m ");
        sbQueryToday.append("\n             LEFT OUTER JOIN ");
        sbQueryToday.append("\n             code c ");
        sbQueryToday.append("\n             ON c.id = m.meter_status ");
        sbQueryToday.append("\n             LEFT OUTER JOIN ").append(lpTable).append(" lp ");
        sbQueryToday.append("\n             ON lp.mdev_id = m.mds_id ");
        sbQueryToday.append("\n        AND lp.yyyymmddhh BETWEEN '"+currDate + "00"+"' AND '"+currDate + "23"+"' ");
        sbQueryToday.append("\n        AND lp.hh < '" +currHour+"' ");
        sbQueryToday.append("\n        AND lp.channel = "+channel.toString()+" ");
        sbQueryToday.append("\n        WHERE m.install_date <= '"+searchStartDate + CommonConstants.DefaultDate.LAST_HHMMSS.getValue()+"' ");
//        sbQueryToday.append("\n        AND 1 = CASE WHEN m.lp_interval = 1 THEN CASE WHEN lp.value_cnt < 60   THEN 0 ELSE 1 END ");
//        sbQueryToday.append("\n                     WHEN m.lp_interval = 5 THEN CASE WHEN lp.value_cnt < 12   THEN 0 ELSE 1 END ");
//        sbQueryToday.append("\n                     WHEN m.lp_interval = 10 THEN CASE WHEN lp.value_cnt < 6   THEN 0 ELSE 1 END ");
//        sbQueryToday.append("\n                     WHEN m.lp_interval = 15 THEN CASE WHEN lp.value_cnt < 4   THEN 0 ELSE 1 END ");
//        sbQueryToday.append("\n                     WHEN m.lp_interval = 30 THEN CASE WHEN lp.value_cnt < 2   THEN 0 ELSE 1 END ");
//        sbQueryToday.append("\n                     WHEN m.lp_interval = 60 THEN CASE WHEN lp.value_cnt < 1   THEN 0 ELSE 1 END ");
//        sbQueryToday.append("\n                ELSE 0 end ");
        sbQueryToday.append("\n        AND   (m.lp_interval IN (1, 5, 10, 15, 30, 60) ");
        sbQueryToday.append("\n           AND lp.value_cnt >= (60/m.lp_interval)) ");
        sbQueryToday.append("\n        AND   (c.id IS NULL ");
        sbQueryToday.append("\n            OR c.code != '" +MeterCodes.DELETE_STATUS.getCode()+"' ");
        sbQueryToday.append("\n            OR (c.code = '" +MeterCodes.DELETE_STATUS.getCode()+"' AND m1.delete_date > '"+searchStartDate + "235959"+"' )) ");
        sbQueryToday.append("\n        GROUP BY m.id,lp.yyyymmdd ");
        sbQueryToday.append("\n        HAVING COUNT(m.id) = "+currHour+" ");
        sbQueryToday.append("\n    ) i1 ");

        sbQueryToday.append("\n    INNER JOIN ");

        sbQueryToday.append("\n    ( ");
        sbQueryToday.append("\n        SELECT m.id ");
        sbQueryToday.append("\n        FROM meter m ");
        sbQueryToday.append("\n             LEFT OUTER JOIN ");
        sbQueryToday.append("\n             code c ");
        sbQueryToday.append("\n             ON c.id = m.meter_status ");
        sbQueryToday.append("\n             LEFT OUTER JOIN ").append(lpTable).append(" lp ");
        sbQueryToday.append("\n             ON lp.mdev_id = m.mds_id ");
        sbQueryToday.append("\n             AND lp.yyyymmddhh BETWEEN '"+currDate + "00"+"' AND '"+currDate + "23"+"' ");
        sbQueryToday.append("\n             AND lp.hh = '" +currHour+"' ");
        sbQueryToday.append("\n             AND lp.channel = "+channel.toString()+" ");
        sbQueryToday.append("\n        WHERE m.install_date <= '"+searchStartDate + CommonConstants.DefaultDate.LAST_HHMMSS.getValue()+"' ");
        if (currMinute < 1) {
//            sbQueryToday.append("\n        AND 1 = CASE WHEN m.lp_interval = 1  THEN CASE WHEN 1 <= lp.value_cnt THEN 1 ELSE 0 END ");
//            sbQueryToday.append("\n                     WHEN m.lp_interval = 5  THEN CASE WHEN 1 <= lp.value_cnt THEN 1 ELSE 0 END ");
//            sbQueryToday.append("\n                     WHEN m.lp_interval = 10 THEN CASE WHEN 1 <= lp.value_cnt THEN 1 ELSE 0 END ");
//            sbQueryToday.append("\n                     WHEN m.lp_interval = 15 THEN CASE WHEN 1 <= lp.value_cnt THEN 1 ELSE 0 END ");
//            sbQueryToday.append("\n                     WHEN m.lp_interval = 30 THEN CASE WHEN 1 <= lp.value_cnt THEN 1 ELSE 0 END ");
//            sbQueryToday.append("\n                     WHEN m.lp_interval = 60 THEN CASE WHEN 1 <= lp.value_cnt THEN 1 ELSE 0 END ");
//            sbQueryToday.append("\n                ELSE 0 END) i2 ON i1.id=i2.id  ");
            sbQueryToday.append("\n        AND   (m.lp_interval IN (1, 5, 10, 15, 30, 60) ");
            sbQueryToday.append("\n           AND lp.value_cnt >= 1) ");
        } else {
//            sbQueryToday.append("\n        AND 1 = CASE WHEN m.lp_interval = 1  THEN CASE WHEN (:currMinute/1) < lp.value_cnt THEN 1 ELSE 0 END ");
//            sbQueryToday.append("\n                     WHEN m.lp_interval = 5  THEN CASE WHEN (:currMinute/5) < lp.value_cnt THEN 1 ELSE 0 END ");
//            sbQueryToday.append("\n                     WHEN m.lp_interval = 10 THEN CASE WHEN (:currMinute/10) < lp.value_cnt THEN 1 ELSE 0 END ");
//            sbQueryToday.append("\n                     WHEN m.lp_interval = 15 THEN CASE WHEN (:currMinute/15) < lp.value_cnt THEN 1 ELSE 0 END ");
//            sbQueryToday.append("\n                     WHEN m.lp_interval = 30 THEN CASE WHEN (:currMinute/30) < lp.value_cnt THEN 1 ELSE 0 END ");
//            sbQueryToday.append("\n                     WHEN m.lp_interval = 60 THEN CASE WHEN (:currMinute/60) < lp.value_cnt THEN 1 ELSE 0 END ");
//            sbQueryToday.append("\n                ELSE 0 END) i2 ON i1.id=i2.id ");
            sbQueryToday.append("\n        AND   (m.lp_interval IN (1, 5, 10, 15, 30, 60) ");
            sbQueryToday.append("\n           AND lp.value_cnt > ("+currMinute.toString()+"/m.lp_interval)) ");
        }
        sbQueryToday.append("\n        AND   (c.id IS NULL ");
        sbQueryToday.append("\n            OR c.code != '" +MeterCodes.DELETE_STATUS.getCode()+"' ");
        sbQueryToday.append("\n            OR (c.code = '" +MeterCodes.DELETE_STATUS.getCode()+"' AND m1.delete_date > '"+searchStartDate + "235959"+"' )) ");
        sbQueryToday.append("\n    ) i2 ON i1.id = i2.id ");
        sbQueryToday.append("\n) ");

        Query query = null;
        if (Integer.parseInt(searchEndDate) < Integer.parseInt(currDate)) {
            // 조회종료일이 현재일자보다 이전일 경우 전체일자 조회
            //query = getSession().createSQLQuery(sbQueryAllPeriod.toString());
            query = em.createNativeQuery(sbQueryAllPeriod.toString());
//            query.setParameter("startDate", searchStartDate + "00");
//            query.setParameter("endDate", searchEndDate + "23");
//            query.setParameter("installDate", searchStartDate + CommonConstants.DefaultDate.LAST_HHMMSS.getValue());
//            query.setParameter("meterType", meterType);
//            query.setParameter("channel", channel);
//            query.setParameter("period", period);
//            if (!"".equals(supplierId)) {
//                query.setParameter("supplierId", Integer.parseInt(supplierId));
//            }
//            if (!"".equals(mdsId)) {
//                query.setParameter("mdsId", "%" + mdsId + "%");
//            }
//            if (!"".equals(deviceId)) {
//                query.setParameter("deviceId", "%" + deviceId + "%");
//            }

        } else {
            if (Integer.parseInt(searchStartDate) < Integer.parseInt(currDate)) {
                // 조회종료일이 현재일자이고 조회시작일이 현재일자 이전일경우 시작일~종료일전일,현재일자,현재시간 별로 조회
                //query = getSession().createSQLQuery(sbQueryAllPeriodWithToday.toString());
                query = em.createNativeQuery(sbQueryAllPeriodWithToday.toString());
//                query.setParameter("startDate", searchStartDate + "00");
//                query.setParameter("endDate", CalendarUtil.getDateWithoutFormat(searchEndDate, Calendar.DATE, -1) + "23");
//                query.setParameter("installDate", searchStartDate + CommonConstants.DefaultDate.LAST_HHMMSS.getValue());
//                query.setParameter("meterType", meterType);
//                query.setParameter("currStartDate", currDate + "00");
//                query.setParameter("currEndDate", currDate + "23");
//                query.setParameter("currHour", currHour);
//                if (currMinute >= 1) {
//                    query.setParameter("currMinute", currMinute);
//                }
//                query.setParameter("channel", channel);
//                query.setParameter("period", period);
//                if (!"".equals(supplierId)) {
//                    query.setParameter("supplierId", Integer.parseInt(supplierId));
//                }
//                if (!"".equals(mdsId)) {
//                    query.setParameter("mdsId", "%" + mdsId + "%");
//                }
//                if (!"".equals(deviceId)) {
//                    query.setParameter("deviceId", "%" + deviceId + "%");
//                }
            } else {
                // 조회일자가 현재일자 하루일경우 현재날짜,현재시간만 조회
                //query = getSession().createSQLQuery(sbQueryToday.toString());
                query = em.createNativeQuery(sbQueryToday.toString());
//                query.setParameter("installDate", searchStartDate + CommonConstants.DefaultDate.LAST_HHMMSS.getValue());
//                query.setParameter("meterType", meterType);
//                query.setParameter("currStartDate", currDate + "00");
//                query.setParameter("currEndDate", currDate + "23");
//                query.setParameter("currHour", currHour);
//                if (currMinute >= 1) {
//                    query.setParameter("currMinute", currMinute);
//                }
//                query.setParameter("channel", channel);
//                if (!"".equals(supplierId)) {
//                    query.setParameter("supplierId", Integer.parseInt(supplierId));
//                }
//                if (!"".equals(mdsId)) {
//                    query.setParameter("mdsId", "%" + mdsId + "%");
//                }
//                if (!"".equals(deviceId)) {
//                    query.setParameter("deviceId", "%" + deviceId + "%");
//                }
            }
        }
//        query.setParameter("deleteCode", MeterCodes.DELETE_STATUS.getCode());
//        query.setParameter("deleteDate", searchStartDate + "235959");

        // query 결과목록
        //List<Object> result = query.list();
        List<Object> result = query.getResultList();
        if (result == null) {
        	return null;
        }

        List<Object> resultList = new ArrayList<Object>();

        int i = 1;
        for (Object obj : result) {
            HashMap<String, Object> resultMap = new HashMap<String, Object>();
            Object[] objs = (Object[]) obj;

            params.put("meterId", ((Number) objs[5]).intValue());
            params.put("mdsId", (String) objs[3]);

            if (objs[6] == null) {
                params.put("lpInterval", 60); // lpInterval 값이 null일 경우 기본값(60) 입력
            } else {
                params.put("lpInterval", ((Number) objs[6]).intValue());
            }

            //Map<String, Object> countMap = meteringlpDao.getMissingCountByDay(params);

            resultMap.put("no", Integer.toString(i++));
            resultMap.put("customerName", (String) objs[0]);

            if (!"".equals(deviceType)) {// deviceType의 값이 null일 경우 체크
                if (CommonConstants.DeviceType.Modem.getCode().equals(Integer.parseInt(deviceType))) {
                    resultMap.put("deviceNo", (String) objs[1]);
                } else {
                    resultMap.put("deviceNo", (String) objs[2]);
                }
            }
            resultMap.put("mdsId", (String) objs[3]);
            //resultMap.put("missingCnt", countMap.get("totalCount"));
            resultMap.put("lastReadDate", (String) objs[4]);
            resultMap.put("meterId", params.get("meterId"));
            resultMap.put("lpInterval", params.get("lpInterval"));
            resultList.add(resultMap);
        }

        return resultList;
    }

    @Override
    public List<Object> getMissingMeters2(Map<String, Object> params) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getMissingMetersTotalCnt(Map<String, Object> params) {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
	public List<Object> getMissingMetersForRecollectByHour(Map<String,Object> params){
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public List<Object> getMiniChartMeterTypeByLocation(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMiniChartMeterTypeByCommStatus(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMiniChartLocationByMeterType(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMiniChartLocationByCommStatus(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMiniChartCommStatusByMeterType(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMiniChartCommStatusByMeterType(
            Map<String, Object> condition, String[] arrFmtmessagecommalert) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMiniChartCommStatusByLocation(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMiniChartCommStatusByLocation(
            Map<String, Object> condition, String[] arrFmtmessagecommalert) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMeterSearchChart(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMeterSearchGrid(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getSimpleMeterSearchGrid(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMeterLogChart(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMeterLogGrid(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMeterCommLog(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMeterOperationLog(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, Object> getMeterSearchCondition() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Meter> getMeterWithGpio(HashMap<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Meter> getMeterWithoutGpio(HashMap<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Meter> getMeterMapDataWithoutGpio(
            HashMap<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Meter> getMeterHavingModem(Integer id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMeteringDataByMeterChart(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMeteringDataByMeterGrid(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMeterListByModem(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMeterListByNotModem(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer getMeterVEEParamsCount(HashMap<String, Object> hm) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMeterListForContract(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMeterListForContractExtJs(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    @Deprecated
    public List<Object> getGroupMember(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMemberSelectData(Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMeterSupplierList() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer getSLAMeterCount(String today, int supplierId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getEbsDtsTreeDtsMeterNodeData(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getEbsDtsTreeContractMeterNodeData(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getEbsMeterList(
            Map<String, Object> conditionMap, boolean isTotal) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getEbsContractMeterList(
            Map<String, Object> conditionMap, boolean isTotal) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Meter> getDeleteEbsContractMeterNodeListByMeter(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getEbsDtsChartConsumeData(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getEbsDtsChartContractMeterIds(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Meter> getMeters(String mcuId, Integer shortId) {
        String sql = "select m from Meter m where m.modem.mcu.sysID = :mcuId and m.shortId = :shortId";
        Query query = em.createQuery(sql, Meter.class);
        query.setParameter("mcuId", mcuId);
        query.setParameter("shortId", shortId);
        
        return query.getResultList();
    }

    @Override
    public List<Meter> getMeterList(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getMeterCountListPerLocation(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getMcuIdFromMdsId(String mdsId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer getActiveMeterCount(Map<String, String> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<Meter> getPersistentClass() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getSumFieldByCondition(Set<Condition> conditions,
            String field, String... groupBy) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Meter> getMeterByModemId(String modemId) {
        // TODO Auto-generated method stub
        return null;
    }

	@Override
	public Integer getTotalMeterCount() {

		StringBuffer sb = new StringBuffer();
		sb.append("SELECT COUNT(*) FROM METER me									\n");
		sb.append("LEFT OUTER JOIN CODE co ON me.METER_STATUS = co.ID				\n");
		sb.append("WHERE co.name <> 'Delete'										\n");

		Query query = em.createNativeQuery(sb.toString());
		Number totalCount = (Number) query.getResultList().get(0);
		return totalCount.intValue();
	}
	
	@Override
	public List<Map<String, Object>> getMeteringRate(String searchTime, String tableName) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<Map<String, Object>> getMeteringRate_detail(String searchTime, String tableName, String tempTableName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Map<String, Object>> getPilot2MeteringRate(String searchTime, String tableName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Map<String, Object>> getPilot2MeteringRate_detail(String searchTime, String tableName,
			String tempTableName) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<Map<String, Object>> getSmallScaleMeteringRate(String searchTime) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Map<String, Object>> getSmallScaleMeteringRate_detail(String searchTime) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<Map<String, Object>> getSmallScaleSLAMeteringRate(String searchTime) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Map<String, Object>> getRollOutMeteringRate(String searchTime) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<Map<String, Object>> getSmallScaleSLAMeteringRate_detail(String searchTime) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
    public List<Map<String, Object>> getMeterWithMCU(Map<String, String> params)
    {
    	return null;
    }
	
	@Override
	public List<Map<String, Object>> getMeterMMIU(Map<String, String>condition)
	{
		return null;
	}
	
	@Override
    public List<Object> getMissingMetersForRecollect(Map<String, Object> params) // SP-784
	{
		return null;
	}
	
	// INSERT START SP-818
	@Override
    public List<Object> getProblematicMeters(Map<String, Object> params)
    {
		return null;
    }
    // INSERT END SP-818

	@Override
	public List<Map<String, Object>> get48HourNoMeteringRate(String searchTime) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Map<String, Object>> getHLSKeyErrorMeteringRate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Map<String, Object>> getMeterNoResponseMeteringRate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Map<String, Object>> getNoValueMeteringRate() {
		// TODO Auto-generated method stub
		return null;
	}
	
	 @Override
	 public List<String> getFirmwareVersionList(Map<String, Object> condition) {
	       StringBuilder sbQuery = new StringBuilder();
	        Query query = getEntityManager().createQuery(sbQuery.toString());
	        return query.getResultList();
	    }
	    
	    @Override
	    public List<String> getDeviceList(Map<String, Object> condition) {
	        StringBuilder sbQuery = new StringBuilder();
	        Query query = getEntityManager().createQuery(sbQuery.toString());
	        return query.getResultList();
	    }
	    
	    @Override
	    public List<String> getTargetList(Map<String, Object> condition) {
	        StringBuilder sbQuery = new StringBuilder();
	        Query query = getEntityManager().createQuery(sbQuery.toString());
	        return query.getResultList();
	    }

    @Override
    public void updateModemIdNull(int id) {
        String sql = "UPDATE METER SET MODEM_ID = NULL WHERE ID =" + String.valueOf(id);
        Query query = em.createNativeQuery(sql);
        query.executeUpdate();
    }

	@Override
	public List<String> getDeviceListMeter(Map<String, Object> condition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Object> getMeterListCloneonoff(Map<String, Object> condition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public  List<Object>  getMsaListByLocationName(String locationName){
		return null;
	}
	
	@Override
	public List<String> getTargetListMeter(Map<String, Object> condition) {
		// TODO Auto-generated method stub
		return null;
	}    
    
	@Override
	public List<Map<String, Object>> getValidMeterList(Map<String, Object> condition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Map<String, Object>> getParentDevice(Map<String, Object> condition) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
    public List<Object> getMissingMetersForRecollectSLA(Map<String, Object> params) // SP-1075
	{
        String searchStartDate = (String) params.get("searchStartDate"); //yyyymmdd
        String lastLinkTime = StringUtil.nullToBlank((String) params.get("lastLinkTime"));
        String locationName = StringUtil.nullToBlank(params.get("locationName"));
        Integer targetSLA30 = (Integer) params.get("targetSLA30");
        String lpcntTableName = "sla_rawdata";
        if ( params.get("lpcntTableName") != null ) {
            lpcntTableName = (String)params.get("lpcntTableName");
        }
        StringBuffer sbQueryAllPeriod = new StringBuffer();        
        
        sbQueryAllPeriod.append("\nSELECT * ");
        sbQueryAllPeriod.append("\nFROM ( ");
        sbQueryAllPeriod.append("\n       SELECT mcu.sys_id ");
        sbQueryAllPeriod.append("\n       ,mcu.id AS MCU_ID");
        sbQueryAllPeriod.append("\n       ,modem.device_serial ");
        sbQueryAllPeriod.append("\n       ,meter.mds_id ");
        sbQueryAllPeriod.append("\n       ,meter.last_read_date ");
        sbQueryAllPeriod.append("\n       ,meter.id  AS METER_ID");
        sbQueryAllPeriod.append("\n       ,modem.modem_type AS MODEM_TYPE, modem.protocol_type AS PROTOCOL_TYPE ");
        sbQueryAllPeriod.append("\n       ,meter.lp_interval, a.targetsla30, b.yyyymmdd ");
        sbQueryAllPeriod.append("\n       , 24*60/meter.LP_INTERVAL as expect_mv_count ");
        sbQueryAllPeriod.append("\n       , current_mv_count, last_update_date ");
        sbQueryAllPeriod.append("\n       from sla_target a ");
        sbQueryAllPeriod.append("\n       join meter meter on a.meter_id=meter.mds_id  ");
        sbQueryAllPeriod.append("\n            and meter.meter_status =(select id from code where code='1.3.3.1') ");
        
        sbQueryAllPeriod.append("\n       left join " + lpcntTableName + " b on a.meter_id=b.meter_id and b.yyyymmdd= '" + searchStartDate +"' ");
        
        sbQueryAllPeriod.append("\n       LEFT OUTER JOIN modem modem ON meter.modem_id = modem.id ");
        sbQueryAllPeriod.append("\n       LEFT OUTER JOIN mcu mcu ON modem.mcu_id = mcu.id ");
        sbQueryAllPeriod.append("\n       LEFT OUTER JOIN location lo ON meter.location_id = lo.id "); 

        if (!"".equals(locationName)) {
        	sbQueryAllPeriod.append("\nWHERE lo.name IN ("); 
        	sbQueryAllPeriod.append(locationName);
        	sbQueryAllPeriod.append(") ");        	
        }        
	    if ( !"".equals(lastLinkTime) ) {
	        if (!"".equals(locationName)) {
	        	sbQueryAllPeriod.append("\nAND modem.last_link_time >= '" + lastLinkTime + "' ");
	        } else {
	        	sbQueryAllPeriod.append("\nWHERE modem.last_link_time >= '" + lastLinkTime + "' ");
	        }
	    }
	    
        sbQueryAllPeriod.append("\n ) ");
        

        if (targetSLA30==0) {
            sbQueryAllPeriod.append("\nWHERE ( targetsla30=" + targetSLA30.toString() +"  OR targetsla30 IS NULL ) ");
        } else {
            sbQueryAllPeriod.append("\nWHERE ( targetsla30= " + targetSLA30.toString() + " ) ");	
        }
        sbQueryAllPeriod.append("\nAND (current_mv_count is null or expect_mv_count!=current_mv_count) ");
		
        
 /////


        Query query = null;
        
        query = em.createNativeQuery(sbQueryAllPeriod.toString());
        List<Object> result = query.getResultList();
        if (result == null) {
        	return null;
        }        
        
        List<Object> resultList = new ArrayList<Object>();
        for (Object obj : result) {
            HashMap<String, Object> resultMap = new HashMap<String, Object>();
            Object[] objs = (Object[]) obj;
            resultMap.put("sysId" , (String)objs[0]);

            resultMap.put("mcuId", objs[1] == null ?  null : ((Number) objs[1]).intValue());
            resultMap.put("deviceSerial", (String) objs[2]);
            resultMap.put("mdsId", (String) objs[3]);
            resultMap.put("lastReadDate", (String) objs[4]);
            resultMap.put("meterId", ((Number) objs[5]).intValue());
            resultMap.put("modemType", (String)objs[6]);
            resultMap.put("protocolType", (String)objs[7]);
            resultList.add(resultMap);
        }
        return resultList;
	}

	@Override
	public List<Map<String, Object>> getMissLpMeter(Map<String, Object> condition) {
		// TODO Auto-generated method stub
		return null;
	}
}	