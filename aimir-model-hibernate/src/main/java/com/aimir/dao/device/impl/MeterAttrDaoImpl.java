package com.aimir.dao.device.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.device.MeterAttrDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.model.device.Meter;
import com.aimir.model.device.MeterAttr;
import com.aimir.util.StringUtil;


/**
 * SP-898
 * @author
 *
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
@Repository(value = "meterAttrDao")
public class MeterAttrDaoImpl extends AbstractHibernateGenericDao<MeterAttr, Long> implements MeterAttrDao {

	protected static Log logger = LogFactory.getLog(MeterAttrDaoImpl.class);

	@Autowired
	MeterDao meterDao;

	@Autowired
	protected MeterAttrDaoImpl(SessionFactory sessionFactory) {
		super(MeterAttr.class);
		super.setSessionFactory(sessionFactory);
	}

	public MeterAttr getByMeterId(Integer meter_id)
	{
		return findByCondition("meterId", meter_id);
	}

	public MeterAttr getByMdsId(String mdsId)
	{
		MeterAttr attr = null;
		Meter meter = meterDao.get(mdsId);
		if ( meter != null ){
			attr =  getByMeterId(meter.getId());
		}
		return attr;
	}
	
	/* (non-Javadoc)
	 * @see com.aimir.dao.device.MeterAttrDao#getMucsToClearAlarmObject(java.util.Map)
	 */
	public List<Map<String,Object>> getMucsToClearAlarmObject(Map<String, Object> conditionMap)
	{
		String arvalues[] = null;
		List<Map<String,Object>> result      = new ArrayList<Map<String,Object>>();

		String lastLinkTime =  StringUtil.nullToBlank(conditionMap.get("lastLinkTime"));
		Integer page = conditionMap.get("page") != null ? (Integer)conditionMap.get("page") : null;
		Integer limit = conditionMap.get("limit") != null ? (Integer)conditionMap.get("limit"): null;

		HashMap<String, String> values = new HashMap<String, String>();
		String alarmValue = StringUtil.nullToBlank(conditionMap.get("alarmValue"));
		String modemType = StringUtil.nullToBlank(conditionMap.get("modemType"));
		String alarmDateBefore = StringUtil.nullToBlank(conditionMap.get("alarmDateBefore"));
		
		if ( !"".equals(alarmValue) ) {
			arvalues = alarmValue.split(",");
			for ( int i = 0; i < arvalues.length ; i++) {
				values.put("val" + String.valueOf(i), arvalues[i]);
			}
		}


		StringBuilder sb = new StringBuilder();

		sb.append("\nSELECT mcu.id ,mcu.sys_id, count(*) as cnt");
		sb.append("\nFROM METER_ATTR ma ");
		sb.append(" join METER me on ma.METER_ID = me.ID ");
		sb.append(" join MODEM mo on me.MODEM_ID = mo.ID ");
		sb.append(" join MCU mcu on mo.MCU_ID = mcu.ID ");
		sb.append("\nWHERE ma.ALARM_VALUE is not NULL ");
		for( String key : values.keySet()) {
			sb.append(" and ma.ALARM_VALUE != :" + key + " ");
		}
		if (! "".equals(alarmDateBefore)) {
			sb.append(" and ma.ALARM_DATE >= :alarmDateBefore ");
		}
		if (! "".equals(lastLinkTime)) {
			sb.append(" and mo.LAST_LINK_TIME >= :lastLinkTime ");
		}
		if (! "".equals(modemType
				)) {
			sb.append(" and mo.MODEM_TYPE = :modemType  ");
		}
		sb.append(" group by mcu.id , mcu.sys_id ");
		sb.append(" order by cnt desc ");
		Query query = getSession().createSQLQuery(sb.toString());

		for( String key : values.keySet()) {
			query.setString(key, values.get(key));
		}
		if (! "".equals(lastLinkTime)) {
			query.setString("lastLinkTime", lastLinkTime);
		}
		if (! "".equals(modemType)) {
			query.setString("modemType", modemType);
		}
		if (! "".equals(alarmDateBefore)) {
			query.setString("alarmDateBefore", alarmDateBefore);
		}
		List dateList = query.list();
		for (int i = 0; i < dateList.size(); i++) {

			HashMap map = new HashMap();
			Object[] resultData = (Object[]) dateList.get(i);
			map.put("mcuId", ((BigDecimal)resultData[0]).intValue());
			map.put("sysId",  resultData[1]);
			map.put("cnt", ((BigDecimal)resultData[2]).intValue());
			result.add(map);
		}

		return result;
	}
	
	/* (non-Javadoc)
	 * @see com.aimir.dao.device.MeterAttrDao#getMetersToClearAlarmObject(java.util.Map, boolean)
	 */
	public List<Object> getMetersToClearAlarmObject(Map<String, Object> conditionMap,boolean isTotal)
	{
		String attrs[] = null;
		List<Object> result      = new ArrayList<Object>();

		String lastLinkTime =  StringUtil.nullToBlank(conditionMap.get("lastLinkTime"));
		Integer page = conditionMap.get("page") != null ? (Integer)conditionMap.get("page") : null;
		Integer limit = conditionMap.get("limit") != null ? (Integer)conditionMap.get("limit"): null;
		Integer mcuId = conditionMap.get("mcuId") != null ? (Integer)conditionMap.get("mcuId"): null;
		String modemType = StringUtil.nullToBlank(conditionMap.get("modemType"));

		HashMap<String, String> values = new HashMap<String, String>();
		String alarmValue = StringUtil.nullToBlank(conditionMap.get("alarmValue"));
		if ( !"".equals(alarmValue) ) {
			attrs = alarmValue.split(",");
			for ( int i = 0; i < attrs.length ; i++) {
				values.put("val" + String.valueOf(i), attrs[i]);
			}
		}

		String alarmDate1 = StringUtil.nullToBlank(conditionMap.get("alarmDate1"));
		String alarmDate2  = StringUtil.nullToBlank(conditionMap.get("alarmDate2"));

		StringBuilder sb = new StringBuilder();

		StringBuilder sbTarget = new StringBuilder();
		sbTarget.append(" ma.ALARM_VALUE is not NULL ");
		for( String key : values.keySet()) {
			sbTarget.append(" and ma.ALARM_VALUE != :" + key + " ");
		}
		if ( ! "".equals(alarmDate1) ) {
			sbTarget.append(" and ma.ALARM_DATE >= :alarmDate1 ");
		}
		
		StringBuffer sbSub = new StringBuffer();
		if ( ! "".equals(alarmDate2) ) {
			sbSub.append(" ( ( ");
			sbSub.append(sbTarget.toString());
			sbSub.append(" ) or ( ");
			sbSub.append(" ma.ALARM_VALUE = '00000000' and ma.ALARM_DATE >= :alarmDate2 ");
			sbSub.append(" ) ) ");
		}
		else {
			sbSub.append(" ( ");
			sbSub.append(sbTarget.toString());
			sbSub.append(" ) ");
		}
		
		if ( isTotal ) {
			sb.append("SELECT count(*)  from ( ");
		}
		sb.append("\nSELECT ma.METER_ID , me.MDS_ID, mo.DEVICE_SERIAL, mo.MODEM_TYPE, mo.PROTOCOL_TYPE, mo.PHONE_NUMBER,  mo.id  ");
		sb.append("\nFROM METER_ATTR ma ");
		sb.append(" join METER me on ma.METER_ID = me.ID ");
		sb.append(" join MODEM mo on me.MODEM_ID = mo.ID ");
		if ( mcuId != null ) {
			sb.append(" join MCU mcu on mo.mcu_id = mcu.id ");
		}
		sb.append("\nWHERE 1 = 1 ");
		sb.append(" and ");
		sb.append(sbSub.toString());
		if (! "".equals(lastLinkTime)) {
			sb.append(" and mo.LAST_LINK_TIME >= :lastLinkTime ");
		}
		if (! "".equals(modemType)) {
			sb.append(" and mo.MODEM_TYPE = :modemType  ");
		}
		if ( mcuId != null ) {
			sb.append(" and mcu.id = :mcuId" );
		}

		if ( isTotal ) {
			sb.append("\n) ");
		}
		else {
			sb.append(" order by ma.METER_ID ");
		}
		Query query = getSession().createSQLQuery(sb.toString());

		for( String key : values.keySet()) {
			query.setString(key, values.get(key));
		}
		if (! "".equals(lastLinkTime)) {
			query.setString("lastLinkTime", lastLinkTime);
		}
		if (! "".equals(modemType)) {
			query.setString("modemType", modemType);
		}
		if ( mcuId != null ) {
			query.setInteger("mcuId", mcuId);
		}
		if ( ! "".equals(alarmDate1) ) {
			query.setString("alarmDate1", alarmDate1);
		}
		if ( ! "".equals(alarmDate2) ) {
			query.setString("alarmDate2", alarmDate2);
		}
		if ( !isTotal && page != null && limit != null ) {
			query.setFirstResult(page * limit);
			query.setMaxResults(limit);
		}
		if (isTotal) {
			result = query.list();
		}
		else {
			List dateList = query.list();
			for (int i = 0; i < dateList.size(); i++) {

				HashMap map = new HashMap();
				Object[] resultData = (Object[]) dateList.get(i);
				map.put("meterId" 	, ((BigDecimal)resultData[0]).intValue());
				map.put("mdsId", resultData[1]);
				map.put("deviceSerial", resultData[2]);
				map.put("modemType", resultData[3]);
				map.put("protocolType", resultData[4]);
				map.put("phoneNumber", resultData[5]);
				map.put("modemId",  ((BigDecimal)resultData[6]).intValue());
				result.add(map);
			}
		}
		return result;
	}
}
