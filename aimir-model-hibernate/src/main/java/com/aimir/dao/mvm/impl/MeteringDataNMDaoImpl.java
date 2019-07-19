package com.aimir.dao.mvm.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.DefaultDate;
import com.aimir.constants.CommonConstants.MeterCodes;
import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.mvm.MeteringDataNMDao;
import com.aimir.model.mvm.MeteringDataNM;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.SQLWrapper;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeUtil;

/**
 * SP-929
 * for Net-station Monitoring
 */
@Repository(value = "meteringdatanmDao")
public class MeteringDataNMDaoImpl extends AbstractHibernateGenericDao<MeteringDataNM, Integer> implements MeteringDataNMDao {

    private static Log logger = LogFactory.getLog(MeteringDataNMDaoImpl.class);

    @Autowired
    protected MeteringDataNMDaoImpl(SessionFactory sessionFactory) {
        super(MeteringDataNM.class);
        super.setSessionFactory(sessionFactory);
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getMeteringData(Map<String, Object> conditionMap, boolean isTotal) {
        List<Map<String, Object>> result;

        Integer supplierId = (Integer)conditionMap.get("supplierId");
        //        Integer tariffType = (Integer)conditionMap.get("tariffType");

        Integer sicId = (Integer)conditionMap.get("sicId");
        Integer page = (Integer)conditionMap.get("page");
        Integer limit = (Integer)conditionMap.get("limit");
        Integer dst = (Integer)conditionMap.get("dst");
        if ( dst == null)
            dst = 0;

        String startDate = null;
        String endDate = null;
        //        String mdsId = StringUtil.nullToBlank(conditionMap.get("friendlyName"));
        String meteringSF = StringUtil.nullToBlank(conditionMap.get("meteringSF"));
        String mcuId = StringUtil.nullToBlank(conditionMap.get("mcuId"));
        String mdevId = StringUtil.nullToBlank(conditionMap.get("mdevId"));

        List<Integer> locationIdList = (List<Integer>)conditionMap.get("locationIdList");

        startDate = StringUtil.nullToBlank(conditionMap.get("startDate"));
        endDate = StringUtil.nullToBlank(conditionMap.get("endDate"));

        StringBuilder sb = new StringBuilder();


        if (isTotal) {
            sb.append("\nSELECT COUNT(*) ");
        } else {
            sb.append("\nSELECT yyyymmddhhmmss AS YYYYMMDDHHMMSS, ");
            sb.append("\n       dst AS DST, ");
            sb.append("\n       mds_id AS METER_NO, ");
            sb.append("\n       value AS VALUE, ");
            sb.append("\n       CH1 AS CHANNEL_1, ");
            sb.append("\n       CH2 AS CHANNEL_2, ");
            sb.append("\n       CH3 AS CHANNEL_3, ");
            sb.append("\n       CH4 AS CHANNEL_4, ");
            sb.append("\n       CH5 AS CHANNEL_5, ");
            sb.append("\n       CH6 AS CHANNEL_6, ");
            sb.append("\n       CH7 AS CHANNEL_7, ");
            sb.append("\n       device_serial AS MODEM_ID ");
            sb.append("\nFROM ( ");
            sb.append("\n    SELECT nm.yyyymmddhhmmss, ");
            sb.append("\n           nm.dst, ");
            sb.append("\n           mt.mds_id, ");

            if (!mcuId.isEmpty()) {
                sb.append("\n           mo.device_serial, ");
            } else {
                sb.append("\n           (SELECT md.device_serial FROM modem md WHERE md.id = mt.modem_id ) AS device_serial, ");
            }

            for (int i = 1; i < 8; i++) {
                sb.append("\n           CH").append(String.valueOf(i)).append(", ");
            }
            sb.append("VALUE ");
        }

        if (meteringSF.equals("s")) { // status == success
            sb.append("\n FROM    METERINGDATA_NM nm ,");
            sb.append("\n       meter mt ");
            if (!mcuId.isEmpty()) {
                sb.append("\n         ,modem mo ");
                sb.append("\n         ,mcu mc ");
            }

            sb.append("\n    WHERE nm.yyyymmddhhmmss BETWEEN :startDate AND :endDate ");
            sb.append("\n    AND   nm.mdev_type = :mdevType ");
            sb.append("\n    AND   nm.dst = :dst ");
            sb.append("\n    AND   mt.id = nm.meter_id ");
            sb.append("\n    AND   mt.supplier_id = :supplierId ");
            sb.append("\n    AND   nm.ch1 IS NOT NULL ");

            if (!mcuId.isEmpty()) {
                sb.append("\n    AND   mo.id = mt.modem_id ");
                sb.append("\n    AND   mc.id = mo.mcu_id ");
                sb.append("\n    AND   mc.sys_id = :mcuId ");
            }
        }
        else { // status==fail
            sb.append("\n FROM  MBUS_SLAVE_IO_MODULE mbs LEFT OUTER JOIN  METERINGDATA_NM nm ");
            sb.append("\n     ON mbs.mds_id = nm.mdev_Id  ");
            sb.append("\n         AND  nm.yyyymmddhhmmss BETWEEN :startDate AND :endDate "); 
            sb.append("\n         AND  nm.dst =:dst ");
            sb.append("\n         AND  nm.mdev_type = :mdevType ");
            sb.append("\n         INNER JOIN meter mt ON mbs.meter_id = mt.id ");

            if (!mcuId.isEmpty()) {
                sb.append("\n         INNER JOIN modem mo ON  mo.id = mt.modem_id ");
                sb.append("\n        INNER JOIN mcu mc ON mc.id = mo.mcu_id ");
            }

            sb.append("\n WHERE  mt.supplier_id = :supplierId ");
            sb.append("\n    AND   nm.ch1 IS NULL ");
            if (!mcuId.isEmpty()) {
                sb.append("\n    AND   mc.sys_id = :mcuId ");
            }
        }

        if (!mdevId.isEmpty()) {
            sb.append("\n    AND   nm.mdev_id LIKE :mdevId ");
        }
        if (locationIdList != null) {
            sb.append("\n    AND   mt.location_id IN (:locationIdList) ");
        }
        if (!isTotal) {
            sb.append("\n    ORDER BY nm.yyyymmddhhmmss, nm.mdev_id, nm.dst ");
            sb.append("\n) x ");
        }
        SQLQuery query = getSession().createSQLQuery(new SQLWrapper().getQuery(sb.toString()));

        query.setString("startDate", startDate);
        query.setString("endDate", endDate);
        query.setInteger("supplierId", supplierId);
        query.setInteger("dst", dst);
        query.setString("mdevType", CommonConstants.DeviceType.Meter.name());
        if (!mdevId.isEmpty()) {
            query.setString("mdevId", "%" + mdevId + "%");
        }

        if (sicId != null) {
            query.setInteger("sicId", sicId);
        }

        if (locationIdList != null) {
            query.setParameterList("locationIdList", locationIdList);
        }

        if (!mcuId.isEmpty()) {
            query.setString("mcuId", mcuId);
        }

        if (isTotal) {
            Map<String, Object> map = new HashMap<String, Object>();
            int count = 0;
            count = ((Number)query.uniqueResult()).intValue();
            map.put("total", count);
            result = new ArrayList<Map<String, Object>>();
            result.add(map);
        } else {
            if ( page != null && limit != null) {
                query.setFirstResult((page - 1) * limit);
                query.setMaxResults(limit);
            }
            result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getMeteringDataDetail(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result;

        Integer supplierId = (Integer)conditionMap.get("supplierId");
        String searchStartDate = StringUtil.nullToBlank(conditionMap.get("searchStartDate"));
        String searchEndDate = StringUtil.nullToBlank(conditionMap.get("searchEndDate"));
        String searchStartHour = StringUtil.nullToBlank(conditionMap.get("searchStartHour"));
        String searchEndHour = StringUtil.nullToBlank(conditionMap.get("searchEndHour"));
        String meterNo = StringUtil.nullToBlank(conditionMap.get("meterNo"));

        StringBuilder sb = new StringBuilder();

        sb.append("\nSELECT nm.yyyymmddhhmmss AS YYYYMMDDHHMMSS, ");
        sb.append("\n       nm.dst AS DST, ");
        sb.append("\n       nm.value AS VALUE, ");
        sb.append("\n       nm.CH1 AS CHANNEL_1, ");
        sb.append("\n       nm.CH2 AS CHANNEL_2, ");
        sb.append("\n       nm.CH3 AS CHANNEL_3, ");
        sb.append("\n       nm.CH4 AS CHANNEL_4, ");
        sb.append("\n       nm.CH5 AS CHANNEL_5, ");
        sb.append("\n       nm.CH6 AS CHANNEL_6, ");
        sb.append("\n       nm.CH7 AS CHANNEL_7 ");
        sb.append("\nFROM METERINGDATA_NM nm ");
        sb.append("\nWHERE nm.mdev_type = :mdevType ");
        sb.append("\nAND   nm.mdev_id = :meterNo ");
        sb.append("\nAND   nm.yyyymmddhhmmss BETWEEN :startDate AND :endDate ");
        sb.append("\nAND   nm.supplier_id = :supplierId ");
        sb.append("\nORDER BY nm.yyyymmddhhmmss, nm.dst ");

        SQLQuery query = getSession().createSQLQuery(new SQLWrapper().getQuery(sb.toString()));

        query.setString("startDate", (searchStartHour.isEmpty()) ? searchStartDate + "000000" : searchStartDate + searchStartHour +"0000");
        query.setString("endDate", (searchEndHour.isEmpty()) ? searchEndDate + "235959" : searchEndDate + searchEndHour + "5959");
        query.setString("meterNo", meterNo);
        query.setString("mdevType", CommonConstants.DeviceType.Meter.name());
        query.setInteger("supplierId", supplierId);

        result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        return result;
    }
    /**
     * method name : getMeteringSuccessCountListPerLocation<b/>
     * method Desc : 
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getMeteringSuccessCountListPerLocation(Map<String, Object> conditionMap) {
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        String meterType = StringUtil.nullToBlank(conditionMap.get("meterType"));
        String searchStartDate = StringUtil.nullToBlank(conditionMap.get("searchStartDate"));
        String searchEndDate = StringUtil.nullToBlank(conditionMap.get("searchEndDate"));

        int interval_min = 15;
        int delay_msec = 30 * 60 * 1000; // 30 min
        int cnt = 1;


        if (searchStartDate.length() == 8) 
            searchStartDate = searchStartDate + "000000";
        if (searchEndDate.length() == 8 ) 
            searchEndDate = searchEndDate + DefaultDate.LAST_HHMMSS;

        try {
            Date startDate =DateTimeUtil.getDateFromYYYYMMDDHHMMSS(searchStartDate);
            Date endDate = DateTimeUtil.getDateFromYYYYMMDDHHMMSS(searchEndDate);
            Date now =    DateTimeUtil.getDateFromYYYYMMDDHHMMSS(TimeUtil.getCurrentTimeMilli());
            long startmsec = startDate.getTime();
            long endmsec = endDate.getTime();
            if ( now.getTime() < endmsec + delay_msec  ){
                cnt = (int)( (now.getTime() - startmsec  - delay_msec)/(1000 * 60 * interval_min )) ;
            }
            else {
                cnt = (int)( (endmsec - startmsec )/(1000 * 60 * interval_min )) ;
            }
        }
        catch (Exception e){
            e.printStackTrace();
            logger.error(e.getMessage());
        }

        StringBuilder sb = new StringBuilder();
        sb.append("\nSELECT LOC_ID, COUNT(METER) AS SUCCESS_CNT ");
        sb.append("\nfrom (");
        sb.append("\n    SELECT m.location_id AS LOC_ID, ");
        sb.append("\n         d.mdev_id AS METER, ");
        sb.append("\n         COUNT(d.mdev_id) AS CNT ");
        sb.append("\n    FROM METERINGDATA_NM d, ");
        sb.append("\n         meter m ");
        sb.append("\n         LEFT OUTER JOIN ");
        sb.append("\n         code c ");
        sb.append("\n         ON c.id = m.meter_status, ");
        sb.append("\n         location p ");
        sb.append("\n    WHERE m.meter = :meterType ");
        sb.append("\n    AND   m.install_date <= :installDate ");
        sb.append("\n    AND   m.supplier_id = :supplierId ");
        sb.append("\n    AND   m.location_id = p.id ");
        sb.append("\n    AND   (c.id IS NULL ");
        sb.append("\n        OR c.code != :deleteCode ");
        sb.append("\n        OR (c.code = :deleteCode AND m.delete_date > :deleteDate) ");
        sb.append("\n    ) ");
        sb.append("\n    AND   d.mdev_type = :mdevType ");
        sb.append("\n    AND   d.mdev_id = m.mds_id ");
        sb.append("\n    AND   d.yyyymmddhhmmss BETWEEN :startDate AND :endDate ");
        sb.append("\n    GROUP BY m.location_id ,d.mdev_id ) x ");
        sb.append("\nWHERE x.CNT >= :cnt ");
        sb.append("\n     GROUP BY LOC_ID ");

        SQLQuery query = getSession().createSQLQuery(new SQLWrapper().getQuery(sb.toString()));
        query.setString("startDate", searchStartDate);
        query.setString("endDate", searchEndDate);
        query.setString("installDate", searchEndDate);
        query.setString("meterType", meterType);
        query.setString("mdevType", CommonConstants.DeviceType.Meter.name());

        query.setInteger("supplierId", supplierId);
        query.setString("deleteCode", MeterCodes.DELETE_STATUS.getCode());
        query.setString("deleteDate", searchEndDate);
        query.setInteger("cnt", cnt);

        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }
}
