package com.aimir.dao.mvm.impl;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.MeterCodes;
import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.mvm.MeteringDataDao;
import com.aimir.model.mvm.MeteringData;
import com.aimir.util.SQLWrapper;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeUtil;

@Repository(value = "meteringdataDao")
public class MeteringDataDaoImpl extends
		AbstractHibernateGenericDao<MeteringData, Integer> implements
		MeteringDataDao {

	@SuppressWarnings("unused")
    private static Log logger = LogFactory.getLog(MeteringDataDaoImpl.class);

	@Autowired
	protected MeteringDataDaoImpl(SessionFactory sessionFactory) {
		super(MeteringData.class);
		super.setSessionFactory(sessionFactory);
	}

	@SuppressWarnings("rawtypes")
    public Map<String, Object> getTotalCountByLocation(Map<String, Object> condition) {

        String meterType = condition.get("meterType").toString();
        String searchStartDate = condition.get("searchStartDate").toString();
        String today = condition.get("searchEndDate").toString();

        if (today == null || "".equals(today)) {
            try {
                today = TimeUtil.getCurrentDay();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        String supplierId = StringUtil.nullToBlank(condition.get("supplierId"));

        Map<String, Object> returnData = new HashMap<String, Object>();

        StringBuffer sb = new StringBuffer();
        sb.append("\nSELECT m.location.id, COUNT(m.location.id)  ");
        sb.append("\nFROM ").append(meterType).append(" m ");
        sb.append("\n     LEFT OUTER JOIN ");
        sb.append("\n     m.meterStatus c ");
        sb.append("\nWHERE m.location.id IS NOT NULL ");
        sb.append("\nAND   m.installDate <= :today ");

        if (!"".equals(supplierId)) {
            sb.append("\nAND   m.supplier.id = :supplierId ");
        }

        sb.append("\nAND   (c.id IS NULL ");
        sb.append("\n    OR c.code != :deleteCode ");
        sb.append("\n    OR (c.code = :deleteCode AND m.deleteDate > :deleteDate) ");
        sb.append("\n) ");
        sb.append("\nGROUP BY m.location.id ");

        Query query = getSession().createQuery(sb.toString());
        query.setString("today", today + "235959");

        if (!"".equals(supplierId)) {
            query.setInteger("supplierId", Integer.parseInt(supplierId));
        }

        query.setString("deleteCode", MeterCodes.DELETE_STATUS.getCode());
        query.setString("deleteDate", searchStartDate + "235959");

        List result = query.list();
        for (Object obj : result) {
            Object[] row = (Object[]) obj;
            returnData.put(row[0].toString(), row[1].toString());
        }

        return returnData;
    }

    @SuppressWarnings("unused")
    @Deprecated
    public String getSuccessCountByLocation(Map<String, Object> condition) {
        String meterType = (String) condition.get("meterType");
        String startDate = (String) condition.get("searchStartDate");
        String endDate = (String) condition.get("searchEndDate");
        String supplierId = StringUtil.nullToBlank(condition.get("supplierId"));
        String locationId = (String) condition.get("locationId");

        //String today = TimeUtil.getCurrentTimeMilli(); // yyyyMMddHHmmss
        String today = endDate+"235959";
        if(endDate == null || "".equals(endDate)) {
            today = TimeUtil.getCurrentTimeMilli();
        }
        int period = 0;
        try {
            period = TimeUtil.getDayDuration(startDate, endDate) + 1;
        } catch (Exception e) {
            period = 1;
        }

        // 미터 타입별 미터링데이터 테이블 설정
        String meteringDataTable = CommonConstants.MeterType.valueOf(meterType).getMeteringTableName();

        StringBuilder sb = new StringBuilder();

        // 조회기간 내 검침내역이 한 건이라도 있으면 성공
        sb.append("\nSELECT COUNT(*) AS CNT FROM (");
        sb.append("\n    SELECT m.id ");
        sb.append("\n    FROM meter m, ");
        sb.append("\n         ").append(meteringDataTable).append(" md ");
        sb.append("\n    WHERE m.meter = :meterType ");
        sb.append("\n    AND   m.location_id = :locationId ");
        sb.append("\n    AND   m.install_date <= :currentDate ");
        if (!supplierId.isEmpty()) {
            sb.append("\n    AND   m.supplier_id = :supplierId ");
        }
        sb.append("\n    AND   md.mdev_type = :mdevType ");
        sb.append("\n    AND   md.mdev_id = m.mds_id ");
        sb.append("\n    AND   md.location_id = m.location_id ");
        sb.append("\n    AND   md.yyyymmddhhmmss BETWEEN :searchStartDate AND :searchEndDate ");
        sb.append("\n    GROUP BY m.id ");
        sb.append("\n) x ");

        SQLQuery query = getSession().createSQLQuery(sb.toString());

        query.setString("meterType", meterType);
        query.setInteger("locationId", Integer.parseInt(locationId));
        query.setString("searchStartDate", startDate + "000000");
        query.setString("searchEndDate", endDate + "235959");
        query.setString("currentDate", today);
        query.setString("mdevType", CommonConstants.DeviceType.Meter.name());

        if (!supplierId.isEmpty()) {
            query.setInteger("supplierId", Integer.parseInt(supplierId));
        }

        String successCount = ((Number)query.uniqueResult()).toString();
        return successCount;
    }

	@SuppressWarnings("unchecked")
	public String getSuccessCountByLocationJeju(Map<String, Object> condition) {
		//logger.info("===================condition=====>\n " + condition);
		String meterType = (String) condition.get("meterType");
		String startDate = (String) condition.get("searchStartDate");
		String endDate = (String) condition.get("searchEndDate");
		String supplierId = StringUtil.nullToBlank(condition.get("supplierId"));
		String locationId = (String) condition.get("locationId");
		String locationId2 = (String) condition.get("locationId2");

		String today = TimeUtil.getCurrentTimeMilli();
		int period = 0;
		try {
			period = TimeUtil.getDayDuration(startDate, endDate) + 1;
		} catch (ParseException e) {
			e.printStackTrace();
		}

		// Map<String, Object> returnData = new HashMap<String, Object>();

		// 미터 타입별 미터링데이터 테이블 설정
		String meteringDataTable = CommonConstants.MeterType.valueOf(meterType)
				.getMeteringTableName();

		StringBuffer sb = new StringBuffer();

		sb.append("\n SELECT COUNT(m.ID) as cnt	");
		sb
				.append("\n FROM METER m LEFT OUTER JOIN (	SELECT md.METER_ID, md.YYYYMMDD                         ");
		sb.append("\n 						  		FROM ").append(meteringDataTable).append(
				" md               ");
		sb
				.append("\n 						  		WHERE md.MDEV_TYPE = :mdevType                                      ");
		sb.append("\n 						  		AND md.LOCATION_ID IN (:locationId) -- 지역id                ");
		sb.append("\n 						  		or md.LOCATION_ID IN (:locationId2) -- 지역id                ");
		sb
				.append("\n 						  		AND md.YYYYMMDDHHMMSS BETWEEN :searchStartDate AND :searchEndDate	");
		sb
				.append("\n 						  		GROUP BY md.METER_ID,md.YYYYMMDD                            ");
		sb
				.append("\n 						  	) x ON m.ID = x.METER_ID                                     	");
		sb.append("\n WHERE 1=1                                    ");
		sb.append("\n AND m.METER = :meterType                     ");
		sb.append("\n AND m.LOCATION_ID IN (:locationId) -- 지역id  ");
		sb.append("\n or m.LOCATION_ID IN (:locationId2) -- 지역id  ");
		sb.append("\n AND m.INSTALL_DATE <= :currentDate           ");
		sb.append("\n AND x.METER_ID is NOT null             	   ");

		if (!"".equals(supplierId)) {
			sb.append("\n AND m.SUPPLIER_ID = :supplierId ");
		}
		sb.append("\n GROUP BY m.ID");
		// sb.append("\n HAVING COUNT(m.ID) = :searchPeriodCount ) x ");
		// derby 버그로 인해서 오류발생하여 로직으로 체크함

		SQLQuery query = getSession().createSQLQuery(sb.toString());

		query.setString("meterType", meterType).setInteger("locationId",
				Integer.parseInt(locationId)).setInteger("locationId2",
						Integer.parseInt(locationId2)).setString("searchStartDate",
				startDate+"000000").setString("searchEndDate", endDate+"235959").setString(
				"currentDate", today)
		// .setInteger("searchPeriodCount", period)
				.setString("mdevType", CommonConstants.DeviceType.Meter.name());
		if (!"".equals(supplierId)) {
			query.setInteger("supplierId", Integer.parseInt(supplierId));
		}

		Integer successCount = 0;
		List<Object> list = query.list();
		if (list != null && list.size() > 0) {
			for (Object obj : list) {
				Integer cnt;
				if (obj instanceof Object[]) {
					Object[] objs = (Object[]) obj;
					cnt = ((Number) objs[0]).intValue();
				} else {
					cnt = ((Number) obj).intValue();
				}

				if (cnt.intValue() >= (new Integer(period / 2))) {
					successCount = successCount + 1;
				}
			}
		}

		return successCount.toString();
	}

    /**
     * method name : getFailureCountByCause<b/>
     * method Desc : MeteringFail 가젯의 Cause1/Cause2 Count 를 조회한다.<b/>
     *               1. meteringdata_em 테이블에 없는 meter 테이블 데이터의 LAST_READ_DATE 를 검색.<b/>
     *               2. LAST_READ_DATE 의 값이 현재일과 하루이상 차이가 나면 Cause1(통신장애)<b/>
     *               3. LAST_READ_DATE 의 값이 현재일과 같으면 Cause2(포멧에러)
     *
     * @param condition
     * @param cause Cause 종류 - 1 : Communication Error , 2 : MeteringFormatError
     * @return
     */
    @SuppressWarnings("unchecked")
    @Deprecated
    public String getFailureCountByCause(Map<String, Object> condition, int cause) {
        String meterType = (String) condition.get("meterType");
        String startDate = (String) condition.get("searchStartDate");
        String endDate = (String) condition.get("searchEndDate");
        String supplierId = StringUtil.nullToBlank(condition.get("supplierId"));
        String locationId = StringUtil.nullToBlank(condition.get("locationId"));

        String today = TimeUtil.getCurrentTimeMilli();
        int period = 0;

        // 미터 타입별 미터링데이터 테이블 설정
        String meteringDataTable = CommonConstants.MeterType.valueOf(meterType).getMeteringTableName();
        StringBuffer sb = new StringBuffer();

        sb.append("\nSELECT m.id AS ID, MAX(m.last_read_date) AS LAST_READ_DATE ");
        sb.append("\nFROM meter m ");
        sb.append("\nWHERE NOT EXISTS (SELECT 'X' ");
        sb.append("\n                  FROM ").append(meteringDataTable).append(" md ");
        sb.append("\n                  WHERE md.mdev_type = :mdevType ");
        sb.append("\n                  AND   md.location_id IN (:locationId) ");    // 지역id
        sb.append("\n                  AND   md.yyyymmddhhmmss BETWEEN :searchStartDate AND :searchEndDate ");
        sb.append("\n                  AND   md.meter_id = m.id ");
        sb.append("\n                 ) ");
        sb.append("\nAND   m.meter = :meterType ");
        sb.append("\nAND   m.location_id IN (:locationId) ");   // 지역id
        sb.append("\nAND   m.install_date <= :currentDate ");

        if (!"".equals(supplierId)) {
            sb.append("\nAND   m.supplier_id = :supplierId ");
        }

        sb.append("\nGROUP BY m.id ");

        SQLQuery query = getSession().createSQLQuery(sb.toString());

        query.setString("meterType", meterType);
        query.setInteger("locationId", Integer.parseInt(locationId));
        query.setString("searchStartDate", startDate+"000000");
        query.setString("searchEndDate", endDate+"235959");
        query.setString("currentDate", today);
        // .setInteger("searchPeriodCount", period)
        query.setString("mdevType", CommonConstants.DeviceType.Meter.name());

        if (!"".equals(supplierId)) {
            query.setInteger("supplierId", Integer.parseInt(supplierId));
        }

        Integer failureCauseCount = 0;
        query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
        List<Map<String, Object>> list = query.list();

        if (list != null && list.size() > 0) {
            for (Map<String, Object> map : list) {
                if (cause == 1) {
                    // 장기간 통신 장애 검사
                    if (!StringUtil.nullToBlank(map.get("LAST_READ_DATE")).isEmpty()) {
                        try {
                            period = TimeUtil.getDayDuration(map.get("LAST_READ_DATE").toString(), today);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        // 마지막 통신 시간과 현재 시간의 차이가 24시간 이상이면 장애 Count
                        if (period >= 1) {
                            failureCauseCount = failureCauseCount + 1;
                        }
                    } else {
                        // 통신 이력이 없을경우 장애 카운트
                        failureCauseCount = failureCauseCount + 1;
                    }
                } else if (cause == 2) {
                    if (!StringUtil.nullToBlank(map.get("LAST_READ_DATE")).isEmpty()) {
                        // LAST_READ_DATE 와 현재일이 같을 경우 Count
                        if (map.get("LAST_READ_DATE").toString().substring(0, 8).equals(today.substring(0, 8))) {
                            failureCauseCount = failureCauseCount + 1;
                        }
                    }
                }
            }
        }

        return failureCauseCount.toString();
    }

    /**
     * method name : getFailureCountByCauses<b/>
     * method Desc : MeteringFail 가젯의 Cause1/Cause2 Count 를 조회한다.<b/>
     *               1. meteringdata_em 테이블에 없는 meter 테이블 데이터의 LAST_READ_DATE 를 검색.<b/>
     *               2. LAST_READ_DATE 의 값이 현재일과 하루이상 차이가 나면 Cause1(통신장애)<b/>
     *               3. LAST_READ_DATE 의 값이 현재일과 같으면 Cause2(포멧에러)
     *               4. 그 외 경우 ETC
     *
     * @param condition
     * @return
     */
    @SuppressWarnings("unchecked")
    @Deprecated
    public Map<String, String> getFailureCountByCauses(Map<String, Object> condition) {
        Map<String, String> result = new HashMap<String, String>();
        String meterType = (String) condition.get("meterType");
        String startDate = (String) condition.get("searchStartDate");
        String endDate = (String) condition.get("searchEndDate");
        String supplierId = StringUtil.nullToBlank(condition.get("supplierId"));
        String locationId = StringUtil.nullToBlank(condition.get("locationId"));

        String currentDate = TimeUtil.getCurrentTimeMilli();
        String installDate = null;

        if (endDate != null && !endDate.isEmpty()) {
            installDate = endDate + "235959";
        } else {
            installDate = TimeUtil.getCurrentTimeMilli();
        }

        // 미터 타입별 미터링데이터 테이블 설정
        String meteringDataTable = CommonConstants.MeterType.valueOf(meterType).getMeteringTableName();
        StringBuilder sb = new StringBuilder();

        sb.append("\nSELECT m.id AS ID, MAX(m.last_read_date) AS LAST_READ_DATE ");
        sb.append("\nFROM meter m ");
        sb.append("\nWHERE 1=1 ");
        sb.append("\nAND   NOT EXISTS (SELECT 'X' ");
        sb.append("\n                  FROM ").append(meteringDataTable).append(" md ");
        sb.append("\n                  WHERE md.mdev_type = :mdevType ");
        sb.append("\n                  AND   md.mdev_id = m.mds_id ");
        sb.append("\n                  AND   md.yyyymmddhhmmss BETWEEN :searchStartDate AND :searchEndDate ");
        sb.append("\n                  AND   md.location_id = :locationId ");    // 지역id
        sb.append("\n                 ) ");
        sb.append("\nAND   m.meter = :meterType ");
        sb.append("\nAND   m.location_id = :locationId ");   // 지역id
        sb.append("\nAND   m.install_date <= :installDate ");

        if (!"".equals(supplierId)) {
            sb.append("\nAND   m.supplier_id = :supplierId ");
        }

        sb.append("\nGROUP BY m.id ");

        SQLQuery query = getSession().createSQLQuery(sb.toString());

        query.setString("meterType", meterType);
        query.setInteger("locationId", Integer.parseInt(locationId));
        query.setString("searchStartDate", startDate+"000000");
        query.setString("searchEndDate", endDate+"235959");
        query.setString("installDate", installDate);
        query.setString("mdevType", CommonConstants.DeviceType.Meter.name());

        if (!supplierId.isEmpty()) {
            query.setInteger("supplierId", Integer.parseInt(supplierId));
        }

        Integer failureCause1Count = 0;
        Integer failureCause2Count = 0;
        Integer failureCause3Count = 0;
        int period = 0;
        List<Map<String, Object>> list = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

        if (list != null && list.size() > 0) {
            for (Map<String, Object> map : list) {
                if (!StringUtil.nullToBlank(map.get("LAST_READ_DATE")).isEmpty()) {
                    // LAST_READ_DATE 와 현재일이 같을 경우 Count
                    if (map.get("LAST_READ_DATE").toString().substring(0, 8).equals(currentDate.substring(0, 8))) {
                        failureCause2Count++;
                    } else {
                        period = 0;
                        try {
                            period = TimeUtil.getDayDuration(map.get("LAST_READ_DATE").toString(), currentDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        // 마지막 통신 시간과 현재 시간의 차이가 24시간 이상이면 장애 Count
                        if (period >= 1) {
                            failureCause1Count++;
                        } else {
                            failureCause3Count++;
                        }
                    }
                } else {
                    // 통신 이력이 없을경우 장애 카운트
                    failureCause1Count++;
                }
            }
        }

        result.put("cause1", failureCause1Count.toString());
        result.put("cause2", failureCause2Count.toString());
        result.put("cause3", failureCause3Count.toString());

        return result;
    }

    /**
     * method name : getFailureCountByEtc<b/>
     * method Desc : MeteringFail 가젯의 기타이유 Count 를 조회한다.<b/>
     *                - 미터 교체 및 공급 중단<b/>
     *                - 미터 상태 이상<b/>
     *                - 미터 시간 이상 (meter 의 timeDiff 가 하루이상 차이남) TIME_DIFF>=24(하루)
     *
     * @param condition
     * @param cause Cause 종류 - 1 : Communication Error , 2 : MeteringFormatError
     * @return
     */
    @SuppressWarnings("unchecked")
    @Deprecated
    public String getFailureCountByEtc(Map<String, Object> condition) {

        String meterType = (String) condition.get("meterType");
        String startDate = (String) condition.get("searchStartDate");
        String endDate = (String) condition.get("searchEndDate");
        String supplierId = StringUtil.nullToBlank(condition.get("supplierId"));
        String locationId = StringUtil.nullToBlank(condition.get("locationId"));
        String currentDate = TimeUtil.getCurrentTimeMilli();
        String installDate = null;

        if (endDate != null && !endDate.isEmpty()) {
            installDate = endDate + "235959";
        } else {
            installDate = TimeUtil.getCurrentTimeMilli();
        }
        // 미터 타입별 미터링데이터 테이블 설정
        String meteringDataTable = CommonConstants.MeterType.valueOf(meterType).getMeteringTableName();

        StringBuilder sb = new StringBuilder();

        sb.append("\nSELECT MAX(m.last_read_date) as LAST_READ_DATE ");
        sb.append("\nFROM meter m ");
        sb.append("\n     LEFT OUTER JOIN code y ");
        sb.append("\n     ON m.meter_status = y.id ");
        sb.append("\nWHERE NOT EXISTS (SELECT 'X' ");
        sb.append("\n                  FROM ").append(meteringDataTable).append(" md ");
        sb.append("\n                  WHERE md.mdev_type = :mdevType ");
        sb.append("\n                  AND   md.location_id IN (:locationId) ");
        sb.append("\n                  AND   md.yyyymmddhhmmss BETWEEN :searchStartDate AND :searchEndDate ");
        sb.append("\n                  AND   md.meter_id = m.id) ");
        sb.append("\nAND   m.meter = :meterType ");
        sb.append("\nAND   m.location_id IN (:locationId) ");
        sb.append("\nAND   m.install_date <= :installDate ");
        sb.append("\nAND   (m.time_diff >= 86400 OR (y.code = '1.3.3.2' OR y.code = '1.3.3.3' OR y.code = '1.3.3.4' OR y.code = '1.3.3.5')) ");

        if (!"".equals(supplierId)) {
            sb.append("\nAND   m.supplier_id = :supplierId ");
        }
        sb.append("\nGROUP BY m.id ");

        SQLQuery query = getSession().createSQLQuery(new SQLWrapper().getQuery(sb.toString()));

        query.setString("meterType", meterType);
        query.setInteger("locationId", Integer.parseInt(locationId));
        query.setString("searchStartDate", startDate+"000000");
        query.setString("searchEndDate", endDate+"235959");
        query.setString("installDate", installDate);
        query.setString("mdevType", CommonConstants.DeviceType.Meter.name());

        if (!"".equals(supplierId)) {
            query.setInteger("supplierId", Integer.parseInt(supplierId));
        }

        Integer failureCauseEtcCount = 0;
        List<Object> list = query.list();

        if (list != null && list.size() > 0) {
            for (Object obj : list) {
                if (!StringUtil.nullToBlank(obj).isEmpty()) {
                    int period = 0;

                    try {
                        period = TimeUtil.getDayDuration(obj.toString(), currentDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    // Communication Error/Format Error 이 외일 경우 count
                    if (period < 1 && !obj.toString().substring(0, 8).equals(currentDate.substring(0, 8))) {
                        failureCauseEtcCount++;
                    }
                }
            }
        }

        return failureCauseEtcCount.toString();
    }

	@SuppressWarnings("unchecked")
	public Integer getCommPermitMeterCount(Map<String, Object> params) {

		String meteringType = StringUtil
				.nullToBlank(params.get("meteringType"));
		String supplierId = StringUtil.nullToBlank(params.get("supplierId"));

		StringBuffer sb = new StringBuffer();

		sb.append("\n select distinct(m.mds_id) from meter m ,").append(meteringType).append(" me ");
		sb.append("\n where m.MDS_ID = me.mdev_id and m.SUPPLIER_ID=:supplierId ");


		Query query = getSession().createSQLQuery(sb.toString());

		if (!"".equals(supplierId)) {
			query.setInteger("supplierId", Integer.parseInt(supplierId));
		}
		Integer meterCount = 0;
		List<Object> list = query.list();
		if (list != null && list.size() > 0) {

			meterCount = list.size();

		}

		return meterCount;
	}


	@SuppressWarnings("unchecked")
	public Integer getPermitMeterCount(Map<String, Object> params) {

		String searchStartDate0 = (String) params.get("searchStartDate0");
		String searchStartDate1 = (String) params.get("searchStartDate1");
		String searchStartDate2 = (String) params.get("searchStartDate2");
		String searchStartDate3 = (String) params.get("searchStartDate3");
		String searchStartDate4 = (String) params.get("searchStartDate4");

		String meteringType = StringUtil
				.nullToBlank(params.get("meteringType"));
		String supplierId = StringUtil.nullToBlank(params.get("supplierId"));

		StringBuffer sb = new StringBuffer();

		sb.append("\n select distinct(a0.mdev_id) from (select mdev_id from ").append(meteringType).append(" where YYYYMMDDHHMMSS between :searchStartDate0 and :searchEndDate0) a0, ");
		sb.append("\n (select distinct(mdev_id) from ").append(meteringType).append(" where YYYYMMDDHHMMSS between :searchStartDate1 and :searchEndDate1) a1, ");
		sb.append("\n (select distinct(mdev_id) from ").append(meteringType).append(" where YYYYMMDDHHMMSS between :searchStartDate2 and :searchEndDate2) a2, ");
		sb.append("\n (select distinct(mdev_id) from ").append(meteringType).append(" where YYYYMMDDHHMMSS between :searchStartDate3 and :searchEndDate3) a3, ");
		sb.append("\n (select distinct(mdev_id) from ").append(meteringType).append(" where YYYYMMDDHHMMSS between :searchStartDate4 and :searchEndDate4) a4, ");
		sb.append("\n (select distinct(mds_id) from meter where SUPPLIER_ID =:supplierId) m  ");
		sb.append("\n where a0.mdev_id=a1.mdev_id  ");
		sb.append("\n and a0.mdev_id=a2.mdev_id  ");
		sb.append("\n and a0.mdev_id=a3.mdev_id  ");
		sb.append("\n and a0.mdev_id=a4.mdev_id  ");
		sb.append("\n and a0.mdev_id= m.mds_id  ");

		Query query = getSession().createSQLQuery(sb.toString());
		query.setString("searchStartDate0", searchStartDate0+"000000").setString("searchEndDate0", searchStartDate0+"235959");
		query.setString("searchStartDate1", searchStartDate1+"000000").setString("searchEndDate1", searchStartDate1+"235959");
		query.setString("searchStartDate2", searchStartDate2+"000000").setString("searchEndDate2", searchStartDate2+"235959");
		query.setString("searchStartDate3", searchStartDate3+"000000").setString("searchEndDate3", searchStartDate3+"235959");
		query.setString("searchStartDate4", searchStartDate4+"000000").setString("searchEndDate4", searchStartDate4+"235959");

		if (!"".equals(supplierId)) {
			query.setInteger("supplierId", Integer.parseInt(supplierId));
		}
		Integer meterCount = 0;
		List<Object> list = query.list();
		if (list != null && list.size() > 0) {

			meterCount = list.size(); 

		}

		return meterCount;
	}

	@SuppressWarnings("unchecked")
	public List<Object> getLastRegisterMeteringData(Map<String, Object> params) {

		String mdsId = (String) params.get("mdsId");
		String searchStartDate = (String) params.get("yyyymmddhhmmss");
		String meteringType = StringUtil
				.nullToBlank(params.get("meteringType"));
		String dateType = StringUtil
				.nullToBlank(params.get("searchType"));
		StringBuffer sb = new StringBuffer();


		sb.append("\n select VALUE AS VALUE, YYYYMMDDHHMMSS AS YYYYMMDDHHMMSS from METERINGDATA_").append(meteringType);
		if(dateType.equals("LT")){
			sb.append("\n where yyyymmddhhmmss < :yyyymmddhhmmss");
		}else if(dateType.equals("GT")){
			sb.append("\n where yyyymmddhhmmss > :yyyymmddhhmmss");
		}else if(dateType.equals("EQ")){
			sb.append("\n where yyyymmddhhmmss = :yyyymmddhhmmss");
		}
		
		sb.append("\n and  mdev_id = :mdsId");
		if(dateType.equals("LT")){ 
			sb.append("\n order by yyyymmddhhmmss desc");
		}else if(dateType.equals("GT")){
			sb.append("\n order by yyyymmddhhmmss asc");
		}

		Query query = getSession().createSQLQuery(sb.toString());
		query.setString("yyyymmddhhmmss", searchStartDate);
		query.setString("mdsId", mdsId);

		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	}

	@SuppressWarnings("unchecked")
	public Integer getTotalGatheredMeterCount(Map<String, Object> params) {

		String searchStartDate = (String) params.get("searchStartDate");

		String meteringType = StringUtil
				.nullToBlank(params.get("meteringType"));
		String supplierId = StringUtil.nullToBlank(params.get("supplierId"));

		StringBuffer sb = new StringBuffer();


		sb.append("\n select distinct(me.mdev_id) from (select mdev_id from ").append(meteringType);
		sb.append("\n where YYYYMMDDHHMMSS between :searchStartDate and :searchEndDate ) me,  ");
		sb.append("\n (select mds_id from meter where SUPPLIER_ID =:supplierId) m  ");
		sb.append("\n where me.mdev_id= m.mds_id  ");


		Query query = getSession().createSQLQuery(sb.toString());
		query.setString("searchStartDate", searchStartDate+"000000");
		query.setString("searchEndDate", searchStartDate+"235959");

		if (!"".equals(supplierId)) {
			query.setInteger("supplierId", Integer.parseInt(supplierId));
		}
		Integer meterCount = 0;
		List<Object> list = query.list();
		if (list != null && list.size() > 0) {

			meterCount = list.size();

		}

		return meterCount;
	}
// 	SLA미터를 설치 후 5일 연속으로 검침된 미터로 사양 변경 됨
//	@SuppressWarnings("unchecked")
//	public Integer getSLAMeterCount(Map<String, Object> params) {
//
//		String searchStartDate = (String) params.get("searchStartDate");
//
//		String meteringType = StringUtil.nullToBlank(params.get("meteringType"));
////		String supplierId = StringUtil.nullToBlank(params.get("supplierId"));
//		String supplierId = StringUtil.nullToZero(params.get("supplierId"));
//
//		StringBuffer sb = new StringBuffer();
//
////		sb.append("\n select distinct(me.mdev_id) from (select mdev_id from ").append(meteringType);
////		sb.append("\n where YYYYMMDD =:searchStartDate) me,  ");
////		sb.append("\n (select m.mds_id from meter m,contract c,customer cu ");
////		sb.append("\n where m.id = c.METER_ID ");
////		sb.append("\n and c.CUSTOMER_ID =cu.id ");
////		sb.append("\n and m.SUPPLIER_ID=:supplierId) mm  ");
////		sb.append("\n where me.mdev_id= mm.mds_id  ");
//
//		sb.append("\nSELECT COUNT(DISTINCT(e.mdev_id)) AS cnt ");
//		sb.append("\nFROM ").append(meteringType).append(" e, ");
//		sb.append("\n     meter m ");
//		sb.append("\n     LEFT OUTER JOIN contract c ");
//		sb.append("\n     ON m.id = c.meter_id ");
//		sb.append("\nWHERE e.yyyymmdd = :searchStartDate ");
//		sb.append("\nAND   m.supplier_id = :supplierId ");
//		sb.append("\nAND   e.mdev_id = m.mds_id ");
//
//		Query query = getSession().createSQLQuery(sb.toString());
//		query.setString("searchStartDate", searchStartDate);
//
////		if (!"".equals(supplierId)) {
////			query.setInteger("supplierId", Integer.parseInt(supplierId));
////		}
//		query.setInteger("supplierId", Integer.parseInt(supplierId));
//
////		Integer meterCount = 0;
////		List<Object> list = query.list();
////		if (list != null && list.size() > 0) {
////			meterCount = list.size();
////		}
//		Integer meterCount = (Integer)query.uniqueResult();
//
//		return meterCount;
//	}

}