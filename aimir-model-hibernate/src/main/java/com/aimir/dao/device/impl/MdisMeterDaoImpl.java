/**
 * MdisMeterDaoImpl.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.dao.device.impl;

import java.util.List;
import java.util.Map;

import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.device.MdisMeterDao;
import com.aimir.model.device.MdisMeter;
import com.aimir.util.SQLWrapper;

/**
 * MdisMeterDaoImpl.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 12. 14  v1.0        문동규   MDIS Meter 모델 DaoImpl
 * 2012. 05. 10  v1.1        문동규   package 위치변경(mvm -> device)
 *
 */
@Repository(value = "mdisMeterDao")
public class MdisMeterDaoImpl extends AbstractHibernateGenericDao<MdisMeter, Integer> implements MdisMeterDao {

    @Autowired
    protected MdisMeterDaoImpl(SessionFactory sessionFactory) {
        super(MdisMeter.class);
        super.setSessionFactory(sessionFactory);
    }

    /**
     * method name : getMdisMeterByMeterIdBulkCommand
     * method Desc : MDIS - Meter Management 맥스가젯에서 Bulk Meter Command 에서 선택된 Meter 의 MdisMeter 정보를 조회한다.
     *
     * @param meterIdList List of Meter.id
     * @return List of com.aimir.model.device.MdisMeter
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getMdisMeterByMeterIdBulkCommand(List<Integer> meterIdList) {
        StringBuilder sb = new StringBuilder();

        sb.append("\nSELECT mt.id AS METER_ID, ");
        sb.append("\n       mt.mds_id AS MDS_ID, ");
        sb.append("\n       mt.switch_status AS SWITCH_STATUS, ");
        sb.append("\n       mt.conditions AS CONDITIONS, ");
        sb.append("\n       md.meter_kind AS METER_KIND, ");
        sb.append("\n       md.prepaid_deposit AS PREPAID_DEPOSIT, ");
        sb.append("\n       md.lp1_timing AS LP1_TIMING, ");
        sb.append("\n       md.lp2_pattern AS LP2_PATTERN, ");
        sb.append("\n       md.lp2_timing AS LP2_TIMING, ");
        sb.append("\n       md.meter_direction AS METER_DIRECTION, ");
        sb.append("\n       md.prepaid_alert_level1 AS PREPAID_ALERT_LEVEL1, ");
        sb.append("\n       md.prepaid_alert_level2 AS PREPAID_ALERT_LEVEL2, ");
        sb.append("\n       md.prepaid_alert_level3 AS PREPAID_ALERT_LEVEL3, ");
        sb.append("\n       md.prepaid_alert_start AS PREPAID_ALERT_START, ");
        sb.append("\n       md.prepaid_alert_off AS PREPAID_ALERT_OFF, ");
        sb.append("\n       md.lcd_disp_scroll AS LCD_DISP_SCROLL, ");
        sb.append("\n       md.lcd_disp_cycle_post AS LCD_DISP_CYCLE_POST, ");
        sb.append("\n       md.lcd_disp_content_post AS LCD_DISP_CONTENT_POST, ");
        sb.append("\n       md.lcd_disp_cycle_pre AS LCD_DISP_CYCLE_PRE, ");
        sb.append("\n       md.lcd_disp_content_pre AS LCD_DISP_CONTENT_PRE, ");
        sb.append("\n       ct.prepaymentthreshold AS PREPAYMENTTHRESHOLD ");
        sb.append("\nFROM meter mt ");
        sb.append("\n     LEFT OUTER JOIN ");
        sb.append("\n     mdis_meter md ");
        sb.append("\n     ON md.meter_id = mt.id ");
        sb.append("\n     LEFT OUTER JOIN ");
        sb.append("\n     contract ct ");
        sb.append("\n     ON ct.meter_id = mt.id ");
        sb.append("\nWHERE mt.id IN (:meterIdList) ");

//        sb.append("\nFROM MdisMeter m ");
//        sb.append("\nWHERE m.id IN (:meterIdList) ");

        SQLQuery query = getSession().createSQLQuery(new SQLWrapper().getQuery(sb.toString()));
        query.setParameterList("meterIdList", meterIdList);

        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }
}