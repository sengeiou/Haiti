package com.aimir.dao.device.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.constants.CommonConstants.LineType;
import com.aimir.constants.CommonConstants.PowerEventStatus;
import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.device.PowerAlarmLogDao;
import com.aimir.model.device.PowerAlarmLog;
import com.aimir.util.SQLWrapper;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeUtil;

@Repository(value = "poweralarmlogDao")
public class PowerAlarmLogDaoImpl extends AbstractHibernateGenericDao<PowerAlarmLog, Long> implements PowerAlarmLogDao {

    @SuppressWarnings("unused")
    private static Log log = LogFactory.getLog(PowerAlarmLogDaoImpl.class);

    @Autowired
    protected PowerAlarmLogDaoImpl(SessionFactory sessionFactory) {
        super(PowerAlarmLog.class);
        super.setSessionFactory(sessionFactory);
    }

    /**
     * Flex ColumnChart 데이터 조회
     */
    @SuppressWarnings("unchecked")
    public List<Object> getPowerAlarmLogColumnChartData(Map<String, Object> condition) {
        String startDate = StringUtil.nullToBlank(condition.get("searchStartDate"));
        String endDate = StringUtil.nullToBlank(condition.get("searchEndDate"));
        String currTabId = StringUtil.nullToBlank(condition.get("currTabId"));
        
        String customerName = StringUtil.nullToBlank(condition.get("customerName"));
        String mdsId = StringUtil.nullToBlank(condition.get("meter"));
        String type = StringUtil.nullToBlank(condition.get("type"));
        String lineMissingType = StringUtil.nullToBlank(condition.get("lineMissingType"));
        Integer supplierId = (Integer)condition.get("supplierId");
        List<Integer> locations = (List<Integer>)condition.get("locations");
        String dateLength = (String)condition.get("dateLength");

        String date = "";
        Calendar cal = Calendar.getInstance();
        date += cal.get(Calendar.YEAR);
        date += (cal.get(Calendar.MONTH) + 1) > 9 ? (cal.get(Calendar.MONTH) + 1) : "0" + (cal.get(Calendar.MONTH) + 1);
        date += cal.get(Calendar.DAY_OF_MONTH) > 9 ? cal.get(Calendar.DAY_OF_MONTH) : "0" + cal.get(Calendar.DAY_OF_MONTH);

        if (startDate.isEmpty()) {
            startDate = date;
        }
        if (endDate.isEmpty()) {
            endDate = date;
        }

        StringBuilder strbf = new StringBuilder();

        strbf.append("\nSELECT SUBSTR(pl.opentime, 1, " + dateLength + ") AS OPENDATE, ");
        strbf.append("\n       pl.type_id AS TYPE, ");
        strbf.append("\n       COUNT(*) AS COUNT ");
        strbf.append("\nFROM power_alarm_log pl ");
        strbf.append("\n     LEFT OUTER JOIN contract ct ON pl.meter_id = ct.meter_id ");
        strbf.append("\n     LEFT OUTER JOIN customer cs ON ct.customer_id = cs.id ");

        if (!mdsId.trim().isEmpty()) {
            strbf.append("\n     INNER JOIN meter mt ON mt.id = pl.meter_id ");
        }
        strbf.append("\nWHERE (pl.supplier_id = :supplier OR pl.supplier_id IS NULL) ");
        strbf.append("\nAND   pl.opentime BETWEEN :startDate AND :endDate ");

        if (!customerName.trim().isEmpty()) {
            strbf.append("\nAND   cs.name LIKE :customerName ");
        }

        if (!mdsId.trim().isEmpty()) {
            strbf.append("\nAND   mt.mds_id = :mdsId ");
        }

        if (!type.isEmpty()) {
            if (!type.equals("open")) {
                strbf.append("\nAND   pl.type_id = :type ");
            } else {
                strbf.append("\nAND   pl.type_id IS NULL ");
            }
        }

        if (currTabId.equals("PC")) {
            strbf.append("\nAND   pl.linetype IS NULL ");
        } else {
            if (!lineMissingType.trim().isEmpty()) {
                strbf.append("\nAND   pl.linetype = :lineMissingType ");
            } else {
                strbf.append("\nAND   pl.linetype IS NOT NULL ");
            }
        }

        if (locations != null) {
            strbf.append("\nAND   ct.location_id IN (:locations) ");
        }

        strbf.append("GROUP BY SUBSTR(pl.opentime, 1, " + dateLength + "), pl.type_id ");
        strbf.append("ORDER BY SUBSTR(pl.opentime, 1, " + dateLength + "), pl.type_id DESC");

        SQLQuery query = getSession().createSQLQuery(new SQLWrapper().getQuery(strbf.toString()));
        query.setInteger("supplier", supplierId);
        query.setString("startDate", startDate + "000000");
        query.setString("endDate", endDate + "235959");

        if (!customerName.trim().isEmpty()) {
            query.setString("customerName", "%" + customerName + "%");
        }

        if (!mdsId.trim().isEmpty()) {
            query.setString("mdsId", mdsId);
        }

        if (!type.isEmpty() && !type.equals("open")) {
            query.setInteger("type", Integer.valueOf(type));
        }

        if (!currTabId.equals("PC") && !lineMissingType.trim().isEmpty()) {
            query.setString("lineMissingType", lineMissingType);
        }

        if (locations != null) {
            query.setParameterList("locations", locations);
        }

        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

    /**
     * Flex PieChart 데이터 조회
     */
    @SuppressWarnings("unchecked")
    public List<Object> getPowerAlarmLogPieData(Map<String, Object> condition) {
        String startDate = StringUtil.nullToBlank(condition.get("searchStartDate"));
        String endDate = StringUtil.nullToBlank(condition.get("searchEndDate"));
        String customerName = StringUtil.nullToBlank(condition.get("customerName"));
        String mdsId = StringUtil.nullToBlank(condition.get("meter"));
        String currTabId = StringUtil.nullToBlank(condition.get("currTabId"));
        String lineMissingType = StringUtil.nullToBlank(condition.get("lineMissingType"));
        String type = StringUtil.nullToBlank(condition.get("type"));
        Integer supplierId = (Integer)condition.get("supplierId");
        List<Integer> locations = (List<Integer>)condition.get("locations");

        String date = "";
        Calendar cal = Calendar.getInstance();
        date += cal.get(Calendar.YEAR);
        date += (cal.get(Calendar.MONTH) + 1) > 9 ? (cal.get(Calendar.MONTH) + 1) : "0" + (cal.get(Calendar.MONTH) + 1);
        date += cal.get(Calendar.DAY_OF_MONTH) > 9 ? cal.get(Calendar.DAY_OF_MONTH) : "0" + cal.get(Calendar.DAY_OF_MONTH);
        if (startDate.isEmpty()) {
            startDate = date;
        }
        if (endDate.isEmpty()) {
            endDate = date;
        }

        StringBuilder strbf = new StringBuilder();

        strbf.append("\nSELECT pl.status AS STATUS, ");
        strbf.append("\n       COUNT(*) AS COUNT ");
        strbf.append("\nFROM power_alarm_log pl ");
        strbf.append("\n     LEFT OUTER JOIN contract ct ON pl.meter_id = ct.meter_id ");
        strbf.append("\n     LEFT OUTER JOIN customer cs ON ct.customer_id = cs.id ");
        if (!mdsId.trim().isEmpty()) {
            strbf.append("\n     INNER JOIN meter mt ON mt.id = pl.meter_id ");
        }
        strbf.append("\nWHERE (pl.supplier_id = :supplier OR pl.supplier_id IS NULL) ");
        strbf.append("\nAND   pl.opentime BETWEEN :startDate AND :endDate ");

        if (!customerName.trim().isEmpty()) {
//            strbf.append("\nAND   cs.name LIKE '%" + customerName + "%' ");
            strbf.append("\nAND   cs.name LIKE :customerName ");
        }
        if (!mdsId.trim().isEmpty()) {
            strbf.append("\nAND   mt.mds_id = :mdsId ");
        }
        if (!type.isEmpty()) {
            if (!type.equals("open")) {
                strbf.append("\nAND   pl.type_id = :type ");
            } else {
                strbf.append("\nAND   pl.type_id IS NULL ");
            }
        }

        if (currTabId.equals("PC")) {
            strbf.append("\nAND   pl.linetype IS NULL ");
        } else {
            if (!lineMissingType.trim().isEmpty()) {
                strbf.append("\nAND   pl.linetype = :lineMissingType ");
            } else {
                strbf.append("\nAND   pl.linetype IS NOT NULL ");
            }
        }

        if (locations != null) {
            strbf.append("\nAND   ct.location_id IN (:locations) ");
        }

        strbf.append("\nGROUP BY pl.status ");
        strbf.append("\nORDER BY pl.status DESC");

        SQLQuery query = getSession().createSQLQuery(new SQLWrapper().getQuery(strbf.toString()));
        query.setInteger("supplier", supplierId);
        query.setString("startDate", startDate + "000000");
        query.setString("endDate", endDate + "235959");

        if (!customerName.trim().isEmpty()) {
            query.setString("customerName", "%" + customerName + "%");
        }

        if (!mdsId.trim().isEmpty()) {
            query.setString("mdsId", mdsId);
        }

        if (!type.isEmpty() && !type.equals("open")) {
            query.setInteger("type", Integer.valueOf(type));
        }

        if (!currTabId.equals("PC") && !lineMissingType.trim().isEmpty()) {
            query.setString("lineMissingType", lineMissingType);
        }

        if (locations != null) {
            query.setParameterList("locations", locations);
        }

        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

    /**
     * method name : getPowerAlarmLogListData<b/>
     * method Desc :
     *
     * @param condition
     * @return
     */
    public List<Object> getPowerAlarmLogListData(Map<String, Object> condition) {
        return getPowerAlarmLogListData(condition, false);
    }

    /**
     * Flex Grid 데이터 조회
     */
    @SuppressWarnings("unchecked")
    public List<Object> getPowerAlarmLogListData(Map<String, Object> condition, Boolean isCount) {
        List<Object> result = new ArrayList<Object>();
        String startDate = StringUtil.nullToBlank(condition.get("searchStartDate"));
        String endDate = StringUtil.nullToBlank(condition.get("searchEndDate"));
        String customerName = StringUtil.nullToBlank(condition.get("customerName"));
        String mdsId = StringUtil.nullToBlank(condition.get("meter"));
        String currTabId = StringUtil.nullToBlank(condition.get("currTabId"));
        String lineMissingType = StringUtil.nullToBlank(condition.get("lineMissingType"));
        String type = StringUtil.nullToBlank(condition.get("type"));
        Integer supplierId = (Integer)condition.get("supplierId");
        Integer page = (Integer)condition.get("page");
        Integer limit = (Integer)condition.get("limit");
        List<Integer> locations = (List<Integer>)condition.get("locations");

        String date = null;

        try {
            date = TimeUtil.getCurrentDay();
        } catch(ParseException e) {
            e.printStackTrace();
        }

        if (startDate.isEmpty()) {
            startDate = date;
        }
        if (endDate.isEmpty()) {
            endDate = date;
        }

        StringBuilder sb = new StringBuilder();
        
        if (isCount) {
            sb.append("\nSELECT COUNT(*) AS cnt ");
        } else {
            sb.append("\nSELECT pl.id AS ID, ");
            sb.append("\n       pl.opentime AS OPENTIME, ");
            sb.append("\n       pl.closetime AS CLOSETIME, ");
            sb.append("\n       lc.name AS LOCATIONNAME, ");
            sb.append("\n       pl.linetype AS LINETYPE, ");
            sb.append("\n       cs.name AS CUSTOMERNAME, ");
            sb.append("\n       mt.mds_id AS MDS_ID, ");
            sb.append("\n       pl.duration AS DURATION, ");
            sb.append("\n       pl.status AS STATUS, ");
            sb.append("\n       pl.message AS MESSAGE ");
        }

        sb.append("\nFROM power_alarm_log pl ");
        sb.append("\n     LEFT OUTER JOIN contract ct ");
        sb.append("\n     ON pl.meter_id = ct.meter_id ");
        sb.append("\n     LEFT OUTER JOIN customer cs ");
        sb.append("\n     ON ct.customer_id = cs.id ");
        sb.append("\n     LEFT OUTER JOIN location lc ");
        sb.append("\n     ON ct.location_id = lc.id ");
        sb.append("\n     LEFT OUTER JOIN meter mt ");
        sb.append("\n     ON pl.meter_id = mt.id ");
        sb.append("\nWHERE pl.opentime BETWEEN :startDate AND :endDate ");
        sb.append("\nAND   pl.supplier_id = :supplier ");

        if (!customerName.trim().isEmpty()) {
            sb.append("\nAND   cs.name LIKE :customerName ");
        }

        if (!mdsId.trim().isEmpty()) {
            sb.append("\nAND   mt.mds_id = :mdsId ");
        }

        if (!type.isEmpty()) {
            if (!type.equals("open")) {
                sb.append("\nAND   pl.type_id = :type ");
            } else {
                sb.append("\nAND   pl.type_id IS NULL ");
            }
        }

        if (currTabId.equals("PC")) {
            sb.append("\nAND   pl.linetype IS NULL ");
        } else {
            if (!lineMissingType.trim().isEmpty()) {
                sb.append("\nAND   pl.linetype = :lineMissingType ");
            } else {
                sb.append("\nAND   pl.linetype IS NOT NULL ");
            }
        }

        if (locations != null) {
            sb.append("\nAND   ct.location_id IN (:locations) ");
        }

        if (!isCount) {
            sb.append("\nORDER BY pl.id DESC");
        }

        SQLQuery query = getSession().createSQLQuery(new SQLWrapper().getQuery(sb.toString()));
        query.setInteger("supplier", supplierId);
        query.setString("startDate", startDate + "000000");
        query.setString("endDate", endDate + "235959");

        if (!customerName.trim().isEmpty()) {
            query.setString("customerName", "%" + customerName + "%");
        }

        if (!mdsId.trim().isEmpty()) {
            query.setString("mdsId", mdsId);
        }

        if (!type.isEmpty() && !type.equals("open")) {
            query.setInteger("type", Integer.valueOf(type));
        }

        if (!currTabId.equals("PC") && !lineMissingType.trim().isEmpty()) {
            query.setString("lineMissingType", lineMissingType);
        }

        if (locations != null) {
            query.setParameterList("locations", locations);
        }

        if (isCount) {
            Integer count = ((Number)query.uniqueResult()).intValue();
            result.add(count);
        } else {
            if (page != null && limit != null) {
                query.setFirstResult((page) * limit);
                query.setMaxResults(limit);
            }
            result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<PowerAlarmLog> getOpenPowerAlarmLog(Integer id, String closeTime, LineType lineType) {

        Criteria criteria = getSession().createCriteria(PowerAlarmLog.class);
        criteria.add(Restrictions.lt("openTime", closeTime));

        criteria.add(Restrictions.eq("meter.id", id));

        if (lineType != null) {
            criteria.add(Restrictions.eq("lineType", lineType));
        }

        criteria.add(Restrictions.eq("status", PowerEventStatus.Open));

        return criteria.list();
    }
}