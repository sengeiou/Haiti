package com.aimir.dao.mvm.impl;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.mvm.PowerQualityDao;
import com.aimir.model.mvm.PowerQuality;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeLocaleUtil;

@Repository(value = "powerqualityDao")
@SuppressWarnings("unchecked")
public class PowerQualityDaoImpl extends AbstractHibernateGenericDao<PowerQuality, Integer> implements PowerQualityDao {

	private static Log logger = LogFactory.getLog(PowerQualityDaoImpl.class);

	// 표준편차율(%) 구하는 식. diviation = 100 * (avg - minimum value) / avg
	private static final String QUERY_WHERE_DEVIATION = 
		"CASE WHEN ((p.vol_a + p.vol_b + p.vol_c) / 3) > 0 THEN " +
		"(100 * (" +
		"((p.vol_a + p.vol_b + p.vol_c) / 3) - " +
		"CASE WHEN p.vol_a <= p.vol_b AND "+
				  "p.vol_a <= p.vol_c THEN " +
				  "p.vol_a " +
			 "WHEN p.vol_c < p.vol_b THEN "+
			 	  "p.vol_c " +
	         "ELSE p.vol_b END ) /" +
	    "((p.vol_a + p.vol_b + p.vol_c) / 3) )" +
		" ELSE 0 END";
    
	@Autowired
	protected PowerQualityDaoImpl(SessionFactory sessionFactory) {
		super(PowerQuality.class);
		super.setSessionFactory(sessionFactory);
	}

	
    public List<Object> getPowerQuality(Map<String, Object> condition){
		Integer supplierId  = (Integer)condition.get("supplierId");
		String fromDate = (String)condition.get("fromDate");  
		String toDate   = (String)condition.get("toDate"); 
		String deviceId   = (String)condition.get("deviceId"); 
		 
		int page       = -1;
		if(condition.containsKey("page")){
			Object oPage = condition.get("page");
			if(oPage instanceof Integer){
				page = (Integer)oPage;
			}else if(oPage instanceof String){
				page = Integer.parseInt((String)oPage);
			}
		}
		
	
		int rowPerPage = CommonConstants.Paging.ROWPERPAGE.getPageNum();
		int firstPage  = page * rowPerPage;
		
        logger.info("==conditions===="+condition);

		StringBuffer sb = new StringBuffer();
		sb.append("\n SELECT  p.yyyymmdd as yyyymmdd");
		sb.append("\n ,p.id.yyyymmddhhmm as yyyymmddhhmm ");
		sb.append("\n ,p.id.mdevId as deviceId");
		sb.append("\n ,p.id.mdevType as deviceType ");
		sb.append("\n ,c.name as customerName ");
		sb.append("\n ,pc.contractNumber as contractId ");
		sb.append("\n ,p.supplier.id as supplierId ");
		
		sb.append("\n ,p.vol_a as vol_a");
		sb.append("\n ,p.vol_b as vol_b");
		sb.append("\n ,p.vol_c as vol_c");
		
		sb.append("\n ,p.curr_a as curr_a");
		sb.append("\n ,p.curr_b as curr_b");
		sb.append("\n ,p.curr_c as curr_c");
		
		sb.append("\n ,p.curr_angle_a as curr_angle_a");
		sb.append("\n ,p.curr_angle_b as curr_angle_b");
		sb.append("\n ,p.curr_angle_c as curr_angle_c");
		
		sb.append("\n ,p.vol_angle_a as vol_angle_a");
		sb.append("\n ,p.vol_angle_b as vol_angle_b");
		sb.append("\n ,p.vol_angle_c as vol_angle_c");
		
		//편차 
		sb.append("\n , ");
		sb.append(QUERY_WHERE_DEVIATION);
		sb.append("\n 			AS divistion");

		
		sb.append("\n FROM    PowerQuality p LEFT OUTER JOIN p.contract pc LEFT OUTER JOIN pc.customer c ");
		sb.append("\n WHERE   p.id.yyyymmddhhmm BETWEEN :fromDate AND :toDate      ");
		sb.append("\n AND     p.supplier.id = :supplierId       ");
		
		if(StringUtil.nullToBlank(deviceId).length() > 0){
			sb.append("\n AND     p.id.mdevId = '"+deviceId+"'");
		}
		
		sb.append("\n ORDER BY p.id.yyyymmddhhmm, p.id.mdevId       ");

		Query query = getSession().createQuery(sb.toString())
								  .setString("fromDate", fromDate+"0000")
								  .setString("toDate", toDate+"2359")
								  .setInteger("supplierId", supplierId);
		if(page<0){
			return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		}else{
			return query.setFirstResult(firstPage).setMaxResults(rowPerPage).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		}
    }
    public Integer getCount(Map<String, Object> condition){
    	Integer supplierId  = (Integer)condition.get("supplierId");
		String fromDate = (String)condition.get("fromDate");  
		String toDate   = (String)condition.get("toDate"); 
		String deviceId   = (String)condition.get("deviceId"); 
		Integer deviation = null;
		if(condition.get("deviation")!=null){
			deviation = (Integer)condition.get("deviation");
		}
	
		Integer angle = null;
		if(condition.get("angle")!=null){
			angle = (Integer)condition.get("angle");
		}
	
        logger.info("==conditions===="+condition);

		StringBuffer sb = new StringBuffer();
		sb.append("\n SELECT  count(distinct p.id.mdevId) as cnt");
		sb.append("\n FROM    PowerQuality p LEFT OUTER JOIN p.contract pc LEFT OUTER JOIN pc.customer c ");
		sb.append("\n WHERE   p.id.yyyymmddhhmm BETWEEN :fromDate AND :toDate       ");
		sb.append("\n AND     p.supplier.id = :supplierId       ");
		
		if(deviation!=null){
			//편차
			sb.append("\n AND ");
			sb.append(QUERY_WHERE_DEVIATION);
			sb.append("\n 			>= :deviation");
		}
		
		if(angle!=null){
			//angle
			sb.append("\n AND (p.vol_angle_a < :angle"); 
			sb.append("\n OR");
			sb.append("\n  p.vol_angle_b < :angle"); 
			sb.append("\n OR");
			sb.append("\n  p.vol_angle_c < :angle)");
		}

		if(StringUtil.nullToBlank(deviceId).length() > 0){
			sb.append("\n AND     p.id.mdevId = '"+deviceId+"'");
		}

		//sb.append("\n ORDER BY p.id.mdevId       ");

		Query query = getSession().createQuery(sb.toString())
								  .setString("fromDate", fromDate+"0000")
								  .setString("toDate", toDate+"2359")
								  .setInteger("supplierId", supplierId);
		if(deviation!=null){
			query.setInteger("deviation", deviation);
		}
		if(angle!=null){
			query.setInteger("angle", angle);
		}
		List<Object> list = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

		if(list != null){
			Integer listCount = ((Number)((Map<String,Object>)list.get(0)).get("cnt")).intValue();
			listCount = listCount == null ? 0 : listCount;
			return listCount;
		}else{
			return 0;
		}
    }

    @Override
    public List<Map<String, Object>> getCountForPQMini(Map<String, Object> condition){
    	Integer supplierId  = (Integer)condition.get("supplierId");
		String fromDate = (String)condition.get("fromDate");  
		String toDate   = (String)condition.get("toDate"); 
		String deviceId   = (String)condition.get("deviceId"); 
		Integer deviation = null;
		if(condition.get("deviation")!=null){
			deviation = (Integer)condition.get("deviation");
		}else deviation = 0; // deviation미입력시, 0값으로 설정하여 실행함.
	
		Integer angle = null;
		if(condition.get("angle")!=null){
			angle = (Integer)condition.get("angle");
		}

        logger.info("==conditions===="+condition);

        /*
         * select 
		count( distinct case when (case when (powerquali0_.vol_a+powerquali0_.vol_b+powerquali0_.vol_c)/3>0 then 100*((powerquali0_.vol_a+powerquali0_.vol_b+powerquali0_.vol_c)/3-(case when powerquali0_.vol_a<=powerquali0_.vol_b and powerquali0_.vol_a<=powerquali0_.vol_c then powerquali0_.vol_a when powerquali0_.vol_c<powerquali0_.vol_b 
		then powerquali0_.vol_c else powerquali0_.vol_b end))/((powerquali0_.vol_a+powerquali0_.vol_b+powerquali0_.vol_c)/3) else 0 end>= 5 )
		then   powerquali0_.mdev_id else null end) as mdev_id1,
		count( distinct case when (case when (powerquali0_.vol_a+powerquali0_.vol_b+powerquali0_.vol_c)/3>0 then 100*((powerquali0_.vol_a+powerquali0_.vol_b+powerquali0_.vol_c)/3-(case when powerquali0_.vol_a<=powerquali0_.vol_b and powerquali0_.vol_a<=powerquali0_.vol_c then powerquali0_.vol_a when powerquali0_.vol_c<powerquali0_.vol_b 
		then powerquali0_.vol_c else powerquali0_.vol_b end))/((powerquali0_.vol_a+powerquali0_.vol_b+powerquali0_.vol_c)/3) else 0 end>= 0 )
		then   powerquali0_.mdev_id else null end) as mdev_id2,
		count( distinct case when (case when (powerquali0_.vol_a+powerquali0_.vol_b+powerquali0_.vol_c)/3>0 then 100*((powerquali0_.vol_a+powerquali0_.vol_b+powerquali0_.vol_c)/3-(case when powerquali0_.vol_a<=powerquali0_.vol_b and powerquali0_.vol_a<=powerquali0_.vol_c then powerquali0_.vol_a when powerquali0_.vol_c<powerquali0_.vol_b then powerquali0_.vol_c else powerquali0_.vol_b end))/((powerquali0_.vol_a+powerquali0_.vol_b+powerquali0_.vol_c)/3) 
		else 0 end>= 0  and (powerquali0_.vol_angle_a< 0  or powerquali0_.vol_angle_b< 0  or powerquali0_.vol_angle_c< 0 ))
		then   powerquali0_.mdev_id else null end) as mdev_id3
		from 
		POWER_QUALITY powerquali0_ 
		where powerquali0_.yyyymmddhhmm between  '201402180000'  and  '201404202359' 
		and powerquali0_.supplier_id= 1 ;
         */
		StringBuffer sb = new StringBuffer();
		sb.append("\n SELECT  ")
          .append("\n   COUNT( distinct CASE WHEN (").append(QUERY_WHERE_DEVIATION).append(" >= :deviation ) ")
		  .append("\n                  THEN   p.mdev_id ELSE null END) as CNT1, ")		  
		  .append("\n   COUNT( distinct CASE WHEN (").append(QUERY_WHERE_DEVIATION).append(" >= 0 ) ")
		  .append("\n                  THEN   p.mdev_id ELSE null END) as CNT2, ")
		  .append("\n   COUNT( distinct CASE WHEN (").append(QUERY_WHERE_DEVIATION).append(" >= 0  AND (p.vol_angle_a < :angle OR p.vol_angle_b < :angle OR p.vol_angle_b < :angle)) ")
		  .append("\n                  THEN   p.mdev_id ELSE null END) as CNT3 ")
		  .append("\n FROM POWER_QUALITY p ")
		  .append("\n WHERE   p.yyyymmddhhmm BETWEEN :fromDate AND :toDate       ") // 이 조건은 필요 없는것 같다.(금일 데이터만 대상이므로)
		  .append("\n AND     p.supplier_id = :supplierId       ");

		
		
		//where this_.dst= @P0  and this_.yyyymmddhhmm= @P1  and this_.mdev_type= @P2  and this_.mdev_id= @P3 
				
		if(StringUtil.nullToBlank(deviceId).length() > 0){
			sb.append("\n AND     p.mdev_id = '"+deviceId+"'");
		}

		// COUNT안의 CASE WHEN은 HQL에서는 지원하지 않는다.
		Query query = getSession().createSQLQuery(sb.toString());
		query.setString("fromDate", fromDate+"0000");
		query.setString("toDate", toDate+"2359");
		query.setInteger("supplierId", supplierId);
		if(deviation!=null){
			query.setInteger("deviation", deviation);
		}
		if(angle!=null){
			query.setInteger("angle", angle);
		}
		List<Map<String, Object>> list = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		return list;
    }
    
    public Double getVoltageUnbalance(Double vol_a,Double vol_b,Double vol_c){
		Double min = Math.min(Math.min(vol_a, vol_b),vol_c);
		Double average = (vol_a + vol_b + vol_c) / 3;
		Double maximumDeviationAverage = average - min;
		Double voltageUnbalance = 100 * (maximumDeviationAverage/average);
		return voltageUnbalance;
	}
    
    public List<Object> getVoltageLevels(Map<String, Object> condition){
    	Integer supplierId     	= (Integer)condition.get("supplierId");
		Integer deviceType     	= (Integer)condition.get("deviceType");
		Integer page 			= (Integer)condition.get("page");
		Integer rowPerPage 		= (Integer)condition.get("rowPerPage");

		String fromDate    		= (String)condition.get("fromDate");  
		String toDate      		= (String)condition.get("toDate");

		String equipId      		= (String)condition.get("equipId"); 
		Integer vendorId      		= (Integer)condition.get("vendorId"); 
		Integer modelId      		= (Integer)condition.get("modelId"); 
    	
    	Integer deviation = (Integer)condition.get("deviation");

    	Boolean isExcel = condition.get("isExcel") == null || "".equals(condition.get("isExcel")) ? false : (Boolean)condition.get("isExcel");

        logger.info("==conditions===="+condition);

		StringBuffer sb = new StringBuffer();
		sb.append("\n SELECT  p.yyyymmdd as yyyymmdd ");
		sb.append("\n        ,p.id.mdevId as deviceId   ");
		sb.append("\n        ,p.id.mdevType as deviceType ");
		sb.append("\n        ,c.name as customerName ");
		sb.append("\n        ,MIN(p.vol_a) as volA_min ");
		sb.append("\n        ,MAX(p.vol_a) as volA_max ");
		sb.append("\n        ,AVG(p.vol_a) as volA_avg ");
		sb.append("\n        ,MIN(p.vol_b) as volB_min ");
		sb.append("\n        ,MAX(p.vol_b) as volB_max ");
		sb.append("\n        ,AVG(p.vol_b) as volB_avg ");
		sb.append("\n        ,MIN(p.vol_c) as volC_min ");
		sb.append("\n        ,MAX(p.vol_c) as volC_max ");
		sb.append("\n        ,AVG(p.vol_c) as volC_avg ");
		sb.append("\n        ,MIN(p.vol_angle_a) as vol_angleA_min ");
		sb.append("\n        ,MAX(p.vol_angle_a) as vol_angleA_max ");
		sb.append("\n        ,AVG(p.vol_angle_a) as vol_angleA_avg ");
		sb.append("\n        ,MIN(p.vol_angle_b) as vol_angleB_min ");
		sb.append("\n        ,MAX(p.vol_angle_b) as vol_angleB_max ");
		sb.append("\n        ,AVG(p.vol_angle_b) as vol_angleB_avg ");
		sb.append("\n        ,MIN(p.vol_angle_c) as vol_angleC_min ");
		sb.append("\n        ,MAX(p.vol_angle_c) as vol_angleC_max ");
		sb.append("\n        ,AVG(p.vol_angle_c) as vol_angleC_avg ");
		
		sb.append("\n FROM    PowerQuality p LEFT OUTER JOIN p.contract pc LEFT OUTER JOIN pc.customer c ");
    	if(deviceType == DeviceType.Modem.getCode()) {
			sb.append("\n  LEFT OUTER JOIN  p.modem mo");
		} else if(deviceType == DeviceType.Meter.getCode()) {
			sb.append("\n  LEFT OUTER JOIN  p.meter me");
		} else if(deviceType == DeviceType.EndDevice.getCode()) {
			sb.append("\n  LEFT OUTER JOIN  p.enddevice ed");
		}

        sb.append("\n WHERE   p.id.yyyymmddhhmm BETWEEN :fromDate AND :toDate ");
		sb.append("\n AND     p.supplier.id = :supplierId ");
//		sb.append("\n AND     EXISTS  ( ");
//		sb.append("\n         SELECT 'x' FROM PowerQuality inp ");
//		sb.append("\n         WHERE inp.id.mdevId = p.id.mdevId ");
//		sb.append("\n         AND   inp.id.mdevType = p.id.mdevType ");
//		sb.append("\n         and   inp.id.dst = p.id.dst ");
//		sb.append("\n         and   inp.id.yyyymmddhhmm = p.id.yyyymmddhhmm ");
//		sb.append("\n         AND   inp.supplier.id = p.supplier.id ");

        //입력된 편차보다 이상인 항목들만 불러온다.
        sb.append("\n       AND ");
        sb.append(          QUERY_WHERE_DEVIATION);
        sb.append("\n       >= :deviation");
//		sb.append("\n ) ");

		if(deviceType !=null && deviceType >= 0 ){
			sb.append("\n AND	  p.id.mdevType = :deviceType ");
		}
		
    	if(deviceType == DeviceType.MCU.getCode()) {
    		if(equipId != null && !("".equals(equipId)) ) {
    			sb.append("\n AND p.id.mdevId = :equipId");
    		}
    		
		} else if(deviceType == DeviceType.Modem.getCode()) {
    		if(vendorId != null && vendorId > 0) {
    			sb.append("\n AND mo.model.deviceVendor.id=:vendorId");
    		}
    		if(modelId != null && modelId > 0) {
    			sb.append("\n AND mo.model.id=:modelId");
    		}
    		if(equipId != null && !("".equals(equipId)) ) {
    			sb.append("\n AND mo.deviceSerial = :equipId");
    		}
		} else if(deviceType == DeviceType.Meter.getCode()) {
			
    		if(vendorId != null && vendorId > 0) {
    			sb.append("\n AND me.model.deviceVendor.id=:vendorId");
    		}
    		if(modelId != null && modelId > 0) {
    			sb.append("\n AND me.model.id=:modelId");
    		}
    		if(equipId != null && !("".equals(equipId)) ) {
    			sb.append("\n AND me.mdsId = :equipId");
    		}
		} else if(deviceType == DeviceType.EndDevice.getCode()) {
    		if(equipId != null && !("".equals(equipId)) ) {
    			sb.append("\n AND ed.serialNumber = :equipId");
    		}
		}

		sb.append("\n GROUP BY p.yyyymmdd, p.id.mdevId, p.id.mdevType,c.name");

		
		Query query = getSession().createQuery(sb.toString())
                                  .setString("fromDate", fromDate + "0000")
                                  .setString("toDate", toDate + "2359")
								  .setInteger("deviation", deviation)
								  .setInteger("supplierId", supplierId);

		if(deviceType != null && deviceType >= 0 ){
			query.setInteger("deviceType", deviceType);
		}
		if(vendorId != null && vendorId > 0) {
			query.setInteger("vendorId", vendorId);
		}
		if(modelId != null && modelId > 0) {
			query.setInteger("modelId", modelId);
		}
		if(equipId != null && !("".equals(equipId))) {
			query.setString("equipId", equipId);
		}
		if(!isExcel) {
			query.setFirstResult(page*rowPerPage);
			query.setMaxResults(rowPerPage);
		}
	
		List<Object> result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

		return result;

    }
    // INSERT START SP-204
    public List<Object> getVoltageLevelsForSoria(Map<String, Object> condition){
    	Integer supplierId     	= (Integer)condition.get("supplierId");
		Integer deviceType     	= (Integer)condition.get("deviceType");
		Integer page 			= (Integer)condition.get("page");
		Integer rowPerPage 		= (Integer)condition.get("rowPerPage");

		String fromDate    		= (String)condition.get("fromDate");  
		String toDate      		= (String)condition.get("toDate");

		String equipId      		= (String)condition.get("equipId"); 
		Integer vendorId      		= (Integer)condition.get("vendorId"); 
		Integer modelId      		= (Integer)condition.get("modelId"); 
    	
    	Integer deviation = (Integer)condition.get("deviation");

    	Boolean isExcel = condition.get("isExcel") == null || "".equals(condition.get("isExcel")) ? false : (Boolean)condition.get("isExcel");

        logger.info("==conditions===="+condition);

		StringBuffer sb = new StringBuffer();
		sb.append("\n SELECT  p.yyyymmdd as yyyymmdd ");
		sb.append("\n        ,p.id.mdevId as deviceId   ");
		sb.append("\n        ,p.id.mdevType as deviceType ");
		sb.append("\n        ,c.name as customerName ");
		sb.append("\n        ,MIN(p.vol_thd_a) as volL1_min ");
		sb.append("\n        ,MAX(p.vol_angle_a) as volL1_max ");
		sb.append("\n        ,AVG(p.vol_a) as volL1_avg ");
		sb.append("\n        ,MIN(p.vol_thd_b) as volL2_min ");
		sb.append("\n        ,MAX(p.vol_angle_b) as volL2_max ");
		sb.append("\n        ,AVG(p.vol_b) as volL2_avg ");
		sb.append("\n        ,MIN(p.vol_thd_c) as volL3_min ");
		sb.append("\n        ,MAX(p.vol_angle_c) as volL3_max ");
		sb.append("\n        ,AVG(p.vol_c) as volL3_avg ");
		
		sb.append("\n FROM    PowerQuality p LEFT OUTER JOIN p.contract pc LEFT OUTER JOIN pc.customer c ");
    	if(deviceType == DeviceType.Modem.getCode()) {
			sb.append("\n  LEFT OUTER JOIN  p.modem mo");
		} else if(deviceType == DeviceType.Meter.getCode()) {
			sb.append("\n  LEFT OUTER JOIN  p.meter me");
		} else if(deviceType == DeviceType.EndDevice.getCode()) {
			sb.append("\n  LEFT OUTER JOIN  p.enddevice ed");
		}

        sb.append("\n WHERE   p.id.yyyymmddhhmm BETWEEN :fromDate AND :toDate ");
		sb.append("\n AND     p.supplier.id = :supplierId ");

        //입력된 편차보다 이상인 항목들만 불러온다.
        sb.append("\n       AND ");
        sb.append(          QUERY_WHERE_DEVIATION);
        sb.append("\n       >= :deviation");

		if(deviceType !=null && deviceType >= 0 ){
			sb.append("\n AND	  p.id.mdevType = :deviceType ");
		}
		
    	if(deviceType == DeviceType.MCU.getCode()) {
    		if(equipId != null && !("".equals(equipId)) ) {
    			sb.append("\n AND p.id.mdevId = :equipId");
    		}
    		
		} else if(deviceType == DeviceType.Modem.getCode()) {
    		if(vendorId != null && vendorId > 0) {
    			sb.append("\n AND mo.model.deviceVendor.id=:vendorId");
    		}
    		if(modelId != null && modelId > 0) {
    			sb.append("\n AND mo.model.id=:modelId");
    		}
    		if(equipId != null && !("".equals(equipId)) ) {
    			sb.append("\n AND mo.deviceSerial = :equipId");
    		}
		} else if(deviceType == DeviceType.Meter.getCode()) {
			
    		if(vendorId != null && vendorId > 0) {
    			sb.append("\n AND me.model.deviceVendor.id=:vendorId");
    		}
    		if(modelId != null && modelId > 0) {
    			sb.append("\n AND me.model.id=:modelId");
    		}
    		if(equipId != null && !("".equals(equipId)) ) {
    			sb.append("\n AND me.mdsId = :equipId");
    		}
		} else if(deviceType == DeviceType.EndDevice.getCode()) {
    		if(equipId != null && !("".equals(equipId)) ) {
    			sb.append("\n AND ed.serialNumber = :equipId");
    		}
		}

		sb.append("\n GROUP BY p.yyyymmdd, p.id.mdevId, p.id.mdevType,c.name");

		
		Query query = getSession().createQuery(sb.toString())
                                  .setString("fromDate", fromDate + "0000")
                                  .setString("toDate", toDate + "2359")
								  .setInteger("deviation", deviation)
								  .setInteger("supplierId", supplierId);

		if(deviceType != null && deviceType >= 0 ){
			query.setInteger("deviceType", deviceType);
		}
		if(vendorId != null && vendorId > 0) {
			query.setInteger("vendorId", vendorId);
		}
		if(modelId != null && modelId > 0) {
			query.setInteger("modelId", modelId);
		}
		if(equipId != null && !("".equals(equipId))) {
			query.setString("equipId", equipId);
		}
		if(!isExcel) {
			query.setFirstResult(page*rowPerPage);
			query.setMaxResults(rowPerPage);
		}
	
		List<Object> result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

		return result;

    }    
    // INSERT END SP-204
    public Integer getVoltageLevelsCount(Map<String, Object> condition){
    	Integer supplierId     	= (Integer)condition.get("supplierId");
		Integer deviceType     	= (Integer)condition.get("deviceType");

		String fromDate    		= (String)condition.get("fromDate");  
		String toDate      		= (String)condition.get("toDate"); 
    	
    	Integer deviation = (Integer)condition.get("deviation");
    	
		String equipId      		= (String)condition.get("equipId"); 
		Integer vendorId      		= (Integer)condition.get("vendorId"); 
		Integer modelId      		= (Integer)condition.get("modelId"); 

        logger.info("==conditions===="+condition);

		StringBuffer sb = new StringBuffer();
		sb.append("\n SELECT  COUNT(*) as cnt");

		sb.append("\n FROM    PowerQuality p LEFT OUTER JOIN p.contract pc LEFT OUTER JOIN pc.customer c ");
    	if(deviceType == DeviceType.Modem.getCode()) {
			sb.append("\n  LEFT OUTER JOIN  p.modem mo");
		} else if(deviceType == DeviceType.Meter.getCode()) {
			sb.append("\n  LEFT OUTER JOIN  p.meter me");
		} else if(deviceType == DeviceType.EndDevice.getCode()) {
			sb.append("\n  LEFT OUTER JOIN  p.enddevice ed");
		}
        sb.append("\n WHERE   p.id.yyyymmddhhmm BETWEEN :fromDate AND :toDate ");
		sb.append("\n AND     p.supplier.id = :supplierId ");

        //입력된 편차보다 이상인 항목들만 불러온다.
		//sb.append("\n AND     EXISTS  ( ");
		//sb.append("\n         SELECT 'x' FROM PowerQuality inp ");
		//sb.append("\n         WHERE inp.id.mdevId = p.id.mdevId ");
		//sb.append("\n         AND   inp.id.mdevType = p.id.mdevType ");
		//sb.append("\n         AND   inp.id.dst = p.id.dst ");
		//sb.append("\n         AND   inp.id.yyyymmddhhmm = p.id.yyyymmddhhmm ");
		//sb.append("\n         AND   inp.supplier.id = p.supplier.id ");
		sb.append("\n         AND ");
		sb.append(QUERY_WHERE_DEVIATION);
		sb.append("\n       >= :deviation");
		//sb.append("\n ) ");

		if(deviceType !=null && deviceType >= 0 ){
			sb.append("\n AND	  p.id.mdevType = :deviceType ");
		}
		
    	if(deviceType == DeviceType.MCU.getCode()) {
    		if(equipId != null && !("".equals(equipId)) ) {
    			sb.append("\n AND p.id.mdevId = :equipId");
    		}
    		
		} else if(deviceType == DeviceType.Modem.getCode()) {
    		if(vendorId != null && vendorId > 0) {
    			sb.append("\n AND mo.model.deviceVendor.id=:vendorId");
    		}
    		if(modelId != null && modelId > 0) {
    			sb.append("\n AND mo.model.id=:modelId");
    		}
    		if(equipId != null && !("".equals(equipId)) ) {
    			sb.append("\n AND mo.deviceSerial = :equipId");
    		}
		} else if(deviceType == DeviceType.Meter.getCode()) {
			
    		if(vendorId != null && vendorId > 0) {
    			sb.append("\n AND me.model.deviceVendor.id=:vendorId");
    		}
    		if(modelId != null && modelId > 0) {
    			sb.append("\n AND me.model.id=:modelId");
    		}
    		if(equipId != null && !("".equals(equipId)) ) {
    			sb.append("\n AND me.mdsId = :equipId");
    		}
		} else if(deviceType == DeviceType.EndDevice.getCode()) {
    		if(equipId != null && !("".equals(equipId)) ) {
    			sb.append("\n AND ed.serialNumber = :equipId");
    		}
		}
		sb.append("\n GROUP BY p.yyyymmdd, p.id.mdevId, p.id.mdevType, c.name");

		Query query = getSession().createQuery(sb.toString())
								  .setString("fromDate", fromDate + "0000")
								  .setString("toDate", toDate + "2359")
								  .setInteger("deviation", deviation)
								  .setInteger("supplierId", supplierId);

		if( deviceType != null && deviceType >= 0 ){
			query.setInteger("deviceType", deviceType);
		}
		if(vendorId != null && vendorId > 0) {
			query.setInteger("vendorId", vendorId);
		}
		if(modelId != null && modelId > 0) {
			query.setInteger("modelId", modelId);
		}
		if(equipId != null && !("".equals(equipId))) {
			query.setString("equipId", equipId);
		}
		
		Integer groupCount = 0;
		for(Iterator it = query.iterate(); it.hasNext();){
			it.next();
			groupCount++;
		}

		return groupCount;
    }
    public List<Object> getPowerInstrumentList(Map<String, Object> condition){
    	
    	Integer supplierId  = (Integer)condition.get("supplierId");
    	Integer nSelectType = (Integer)condition.get("selectType");
    	Integer deviation = (Integer)condition.get("deviation");
    	Integer deviceType     	= (Integer)condition.get("deviceType");
		String  equipId      	= (String)condition.get("equipId"); 
    	Integer vendorId     	= (Integer)condition.get("vendorId");
    	Integer modelId     	= (Integer)condition.get("modelId");
    	
		String fromDate = (String)condition.get("fromDate");  
		String toDate   = (String)condition.get("toDate"); 
		String deviceId   = (String)condition.get("deviceId"); 
		String from3letter = (String)condition.get("from3letter");
		String to3letter = (String)condition.get("to3letter");
		
		Boolean isExcel = condition.get("isExcel") == null || "".equals(condition.get("isExcel")) ? false : (Boolean)condition.get("isExcel");
		
		int page       = -1;
		if(condition.containsKey("page")){
			Object oPage = condition.get("page");
			if(oPage instanceof Integer){
				page = (Integer)oPage;
			}else if(oPage instanceof String){
				page = Integer.parseInt((String)oPage);
			}
		}
		 
	
		int rowPerPage = (Integer)condition.get("pageSize");
		int firstPage  = page * rowPerPage;
		
        logger.info("==conditions===="+condition);

		StringBuffer sb = new StringBuffer();
		sb.append("\n SELECT  p.yyyymmdd as yyyymmdd");
		sb.append("\n ,p.id.yyyymmddhhmm as yyyymmddhhmm ");
		sb.append("\n ,p.id.mdevId as deviceId");
		sb.append("\n ,p.id.mdevType as deviceType ");
		sb.append("\n ,c.name as customerName ");
		sb.append("\n ,pc.contractNumber as contractId ");
		sb.append("\n ,p.supplier.id as supplierId ");
		
		sb.append("\n ,p.vol_a as vol_a");
		sb.append("\n ,p.vol_b as vol_b");
		sb.append("\n ,p.vol_c as vol_c");
		
		sb.append("\n ,p.line_AB as line_AB");
		sb.append("\n ,p.line_CA as line_CA");
		sb.append("\n ,p.line_BC as line_BC");
		
		sb.append("\n ,p.curr_a as curr_a");
		sb.append("\n ,p.curr_b as curr_b");
		sb.append("\n ,p.curr_c as curr_c");
		
		sb.append("\n ,p.curr_angle_a as curr_angle_a");
		sb.append("\n ,p.curr_angle_b as curr_angle_b");
		sb.append("\n ,p.curr_angle_c as curr_angle_c");
		
		sb.append("\n ,p.vol_angle_a as vol_angle_a");
		sb.append("\n ,p.vol_angle_b as vol_angle_b");
		sb.append("\n ,p.vol_angle_c as vol_angle_c");

		sb.append("\n FROM    PowerQuality p LEFT OUTER JOIN p.contract pc LEFT OUTER JOIN pc.customer c ");
    	if(deviceType == DeviceType.Modem.getCode()) {
			sb.append("\n  LEFT OUTER JOIN  p.modem mo");
		} else if(deviceType == DeviceType.Meter.getCode()) {
			sb.append("\n  LEFT OUTER JOIN  p.meter me");
		} else if(deviceType == DeviceType.EndDevice.getCode()) {
			sb.append("\n  LEFT OUTER JOIN  p.enddevice ed");
		}
		sb.append("\n WHERE   p.id.yyyymmddhhmm BETWEEN :fromDate AND :toDate       ");
		sb.append("\n AND     p.supplier.id = :supplierId       ");
		if(deviceType !=null && deviceType >= 0 ){
			sb.append("\n AND	  p.id.mdevType = :deviceType ");
		}
		
    	if(deviceType == DeviceType.MCU.getCode()) {
    		if(equipId != null && !("".equals(equipId)) ) {
    			sb.append("\n AND p.id.mdevId = :equipId");
    		}
    		
		} else if(deviceType == DeviceType.Modem.getCode()) {
    		if(vendorId != null && vendorId > 0) {
    			sb.append("\n AND mo.model.deviceVendor.id=:vendorId");
    		}
    		if(modelId != null && modelId > 0) {
    			sb.append("\n AND mo.model.id=:modelId");
    		}
    		if(equipId != null && !("".equals(equipId)) ) {
    			sb.append("\n AND mo.deviceSerial = :equipId");
    		}
		} else if(deviceType == DeviceType.Meter.getCode()) {
			
    		if(vendorId != null && vendorId > 0) {
    			sb.append("\n AND me.model.deviceVendor.id=:vendorId");
    		}
    		if(modelId != null && modelId > 0) {
    			sb.append("\n AND me.model.id=:modelId");
    		}
    		if(equipId != null && !("".equals(equipId)) ) {
    			sb.append("\n AND me.mdsId = :equipId");
    		}
		} else if(deviceType == DeviceType.EndDevice.getCode()) {
    		if(equipId != null && !("".equals(equipId)) ) {
    			sb.append("\n AND ed.serialNumber = :equipId");
    		}
		}
		
		if(nSelectType==1){
			//편차
			sb.append("\n AND ");
			sb.append(QUERY_WHERE_DEVIATION);
			sb.append("\n 			>= :deviation");
		}else {
			//angle
			sb.append("\n AND (p.vol_angle_a < 0"); 
			sb.append("\n OR");
			sb.append("\n  p.vol_angle_b < 0"); 
			sb.append("\n OR");
			sb.append("\n  p.vol_angle_c < 0)"); 
		}
		
		if(StringUtil.nullToBlank(deviceId).length() > 0){
			sb.append("\n AND     p.id.mdevId = '"+deviceId+"'");
		}
		
		sb.append("\n ORDER BY p.id.yyyymmddhhmm, p.id.mdevId       ");

		Query query = getSession().createQuery(sb.toString())
								  .setString("fromDate", fromDate+"0000")
								  .setString("toDate", toDate+"2359")
								  .setInteger("supplierId", supplierId);
		
		if(deviceType !=null && deviceType >= 0 ){
			query.setInteger("deviceType", deviceType);
		}
		if(vendorId != null && vendorId > 0) {
			query.setInteger("vendorId", vendorId);
		}
		if(modelId != null && modelId > 0) {
			query.setInteger("modelId", modelId);
		}
		if(equipId != null && !("".equals(equipId))) {
			query.setString("equipId", equipId);
		}
		
		if(nSelectType==1){
			query.setInteger("deviation",deviation);
		}
		if(!isExcel){
			query.setFirstResult(firstPage).setMaxResults(rowPerPage);
		}
		
		List<Object> result= query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

		for(Object obj:result){
			Map<String, Object> row = (Map<String, Object>)obj;
			
			//편차 및 Angle 조건에 맞는 출력 데이터 설정
			if(nSelectType==1){
				row.put("voltA", row.get("vol_a"));
				row.put("voltB", row.get("vol_b"));
				row.put("voltC", row.get("vol_c"));
				row.put("currA", row.get("curr_a"));
				row.put("currB", row.get("curr_b"));
				row.put("currC", row.get("curr_c"));
			}else if(nSelectType==2){
				row.put("voltA", row.get("vol_angle_a"));
				row.put("voltB", row.get("vol_angle_b"));
				row.put("voltC", row.get("vol_angle_c"));
				row.put("currA", row.get("curr_angle_a"));
				row.put("currB", row.get("curr_angle_b"));
				row.put("currC", row.get("curr_angle_c"));
			}

			//supplier 날짜 형식에 맞게 날짜 포멧 변경.
			row.put("yyyymmdd",(String)row.get("yyyymmdd"));
			row.put("lastReadDate", TimeLocaleUtil.getLocaleDate((String)row.get("yyyymmddhhmm") , from3letter, to3letter));

		}
		return result;
    }
    public Integer getPowerInstrumentListCount(Map<String, Object> condition){

    	Integer supplierId  = (Integer)condition.get("supplierId");
    	Integer nSelectType = (Integer)condition.get("selectType");
    	Integer deviation = (Integer)condition.get("deviation");
    	
		String fromDate = (String)condition.get("fromDate");  
		String toDate   = (String)condition.get("toDate"); 
		String deviceId   = (String)condition.get("deviceId");
		Integer deviceType     	= (Integer)condition.get("deviceType");
		
		String equipId      		= (String)condition.get("equipId"); 
		Integer vendorId      		= (Integer)condition.get("vendorId"); 
		Integer modelId      		= (Integer)condition.get("modelId"); 

        logger.info("==conditions===="+condition);

		StringBuffer sb = new StringBuffer();
		sb.append("\n SELECT  COUNT(*) as cnt");

		sb.append("\n FROM    PowerQuality p LEFT OUTER JOIN p.contract pc LEFT OUTER JOIN pc.customer c ");
		if(deviceType == DeviceType.Modem.getCode()) {
			sb.append("\n  LEFT OUTER JOIN  p.modem mo");
		} else if(deviceType == DeviceType.Meter.getCode()) {
			sb.append("\n  LEFT OUTER JOIN  p.meter me");
		} else if(deviceType == DeviceType.EndDevice.getCode()) {
			sb.append("\n  LEFT OUTER JOIN  p.enddevice ed");
		}

		sb.append("\n WHERE   p.id.yyyymmddhhmm BETWEEN :fromDate AND :toDate       ");
		sb.append("\n AND     p.supplier.id = :supplierId       ");
		
		
		if(nSelectType==1){
			//편차
			sb.append("\n AND ");
			sb.append(QUERY_WHERE_DEVIATION);
			sb.append("\n 			>= :deviation");
		}else {
			//angle
			sb.append("\n AND (p.vol_angle_a < 0"); 
			sb.append("\n OR");
			sb.append("\n  p.vol_angle_b < 0"); 
			sb.append("\n OR");
			sb.append("\n  p.vol_angle_c < 0)"); 
		}
		
		if(StringUtil.nullToBlank(deviceId).length() > 0){
			sb.append("\n AND     p.id.mdevId = '"+deviceId+"'");
		}
		
		if(deviceType == DeviceType.MCU.getCode()) {
    		if(equipId != null && !("".equals(equipId)) ) {
    			sb.append("\n AND p.id.mdevId = :equipId");
    		}
    		
		} else if(deviceType == DeviceType.Modem.getCode()) {
    		if(vendorId != null && vendorId > 0) {
    			sb.append("\n AND mo.model.deviceVendor.id=:vendorId");
    		}
    		if(modelId != null && modelId > 0) {
    			sb.append("\n AND mo.model.id=:modelId");
    		}
    		if(equipId != null && !("".equals(equipId)) ) {
    			sb.append("\n AND mo.deviceSerial = :equipId");
    		}
		} else if(deviceType == DeviceType.Meter.getCode()) {
			
    		if(vendorId != null && vendorId > 0) {
    			sb.append("\n AND me.model.deviceVendor.id=:vendorId");
    		}
    		if(modelId != null && modelId > 0) {
    			sb.append("\n AND me.model.id=:modelId");
    		}
    		if(equipId != null && !("".equals(equipId)) ) {
    			sb.append("\n AND me.mdsId = :equipId");
    		}
		} else if(deviceType == DeviceType.EndDevice.getCode()) {
    		if(equipId != null && !("".equals(equipId)) ) {
    			sb.append("\n AND ed.serialNumber = :equipId");
    		}
		}
		
		Query query = getSession().createQuery(sb.toString())
								  .setString("fromDate", fromDate+"0000")
								  .setString("toDate", toDate+"2359")
								  .setInteger("supplierId", supplierId);
		
		if(nSelectType==1){
			query.setInteger("deviation",deviation);
		}
		if(vendorId != null && vendorId > 0) {
			query.setInteger("vendorId", vendorId);
		}
		if(modelId != null && modelId > 0) {
			query.setInteger("modelId", modelId);
		}
		if(equipId != null && !("".equals(equipId))) {
			query.setString("equipId", equipId);
		}
	
		List<Object> list = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

		if(list != null){
			Integer listCount = ((Number)((Map<String,Object>)list.get(0)).get("cnt")).intValue();
			listCount = listCount == null ? 0 : listCount;
			return listCount;
		}else{
			return 0;
		}
    }
    public List<Object> getPowerDetailList(Map<String, Object> condition){

    	int deviceType  = (Integer)condition.get("deviceType");
    	int typeView    = (Integer)condition.get("typeView");
    	
    	String deviceId = (String)condition.get("deviceId");
		String fromDate = (String)condition.get("fromDate");  
		String toDate   = (String)condition.get("toDate"); 
		String from2letter = (String)condition.get("from2letter");
		String to2letter = (String)condition.get("to2letter");
		
		DecimalFormat df = (DecimalFormat)condition.get("decimalFormat");
					
		int page       = -1;
		if(condition.containsKey("page")){
			Object oPage = condition.get("page");
			if(oPage instanceof Integer){
				page = (Integer)oPage;
			}else if(oPage instanceof String){
				page = Integer.parseInt((String)oPage);
			}	
		}
		int rowPerPage = CommonConstants.Paging.ROWPERPAGE.getPageNum();
		int firstPage  = page * rowPerPage;

        logger.info("==conditions===="+condition);
        logger.info("==first===="+firstPage);
        logger.info("==rowPerPage===="+rowPerPage);

        
		StringBuffer sb1 = new StringBuffer();
		sb1.append("\n SELECT  p.id.yyyymmddhhmm as date ");

		if(typeView > CommonConstants.TypeView.values().length || typeView <= 0){
			typeView = 1;
		}
		String strTypeView = CommonConstants.TypeView.values()[typeView-1].getHead();
		
		if((typeView)==CommonConstants.TypeView.PF.getType()){
			sb1.append("\n        ,ABS(p."+strTypeView+"_a) as decimalA ");
			sb1.append("\n        ,ABS(p."+strTypeView+"_b) as decimalB ");
			sb1.append("\n        ,ABS(p."+strTypeView+"_c) as decimalC ");
		}else {
			sb1.append("\n        ,p."+strTypeView+"_a as decimalA ");
			sb1.append("\n        ,p."+strTypeView+"_b as decimalB ");
			sb1.append("\n        ,p."+strTypeView+"_c as decimalC ");
		}
		if(typeView == CommonConstants.TypeView.Voltage.getType()){
			//편차 
			sb1.append("\n , ");
			sb1.append(QUERY_WHERE_DEVIATION);
			sb1.append("\n 			 AS decimalD");
			sb1.append("\n		 ,CASE WHEN m.id is null THEN 'null' WHEN c.id is null THEN 'null' ELSE c.name END AS phase");
		}
		
		
		StringBuffer sb2 = new StringBuffer();
		sb2.append("\n FROM    PowerQuality p left outer join p.meter m");
		sb2.append("\n  	left outer join m.meterElement c");
		sb2.append("\n WHERE   1=1 ");
		if(deviceType >= 0) {
			sb2.append("\n AND   p.id.mdevType = :deviceType ");
		}
		sb2.append("\n AND     p.id.mdevId = :deviceId ");
		sb2.append("\n AND     p.id.yyyymmddhhmm BETWEEN :fromDate AND :toDate   ");
		sb2.append("\n ORDER BY p.id.yyyymmddhhmm ASC ");

		
		Query query = getSession().createQuery(sb1.append(sb2).toString());
		if(deviceType >= 0) {
			query.setInteger("deviceType", deviceType);
		}
		
		query.setString("deviceId", deviceId);
		query.setString("fromDate",fromDate + "0000");
		query.setString("toDate", toDate + "2359");
		
		if(page >= 0){
			query.setFirstResult(firstPage).setMaxResults(rowPerPage);
		}
		List<Object> result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		
		for(Object obj:result){
			Map<String,Object>row = (Map<String,Object>)obj;
			Double[] decimalArr = new Double[3];
			decimalArr[0] = nullToZero(row.get("decimalA"));
			decimalArr[1] = nullToZero(row.get("decimalB"));
			decimalArr[2] = nullToZero(row.get("decimalC"));
			//A, B, C의 값이 6553.5일 경우 잘못된 값으로 uncertain으로 표시한다.
			row.put("decimalA", decimalArr[0] == 6553.5 ? "uncertain" : df.format(decimalArr[0]));
			row.put("decimalB", decimalArr[1] == 6553.5 ? "uncertain" : df.format(decimalArr[1]));
			row.put("decimalC", decimalArr[2] == 6553.5 ? "uncertain" : df.format(decimalArr[2]));
			row.put("yyyymmdd", String.valueOf(row.get("date")).substring(0, 8));
			row.put("date", TimeLocaleUtil.getLocaleDate(String.valueOf(row.get("date")) , from2letter, to2letter));
			
			String phase = (String) row.get("phase");
			int zeroCnt = 0;
			for (int j = 0; j < decimalArr.length; j++) {
				if(decimalArr[j] == 0)
					zeroCnt++;
			}
			
			// 단상일 경우 A,B,C값이 3개 모두 0일때 unbalance를 -로 표시한다.
			// 삼상일 경우 A,B,C값이 2개 이상이 0일때 unbalance를 -로 표시한다.
			if((typeView == CommonConstants.TypeView.Voltage.getType())){
				if(decimalArr[0] == 6553.5 || decimalArr[0]  == 6553.5 ||decimalArr[0]  == 6553.5) {
					row.put("decimalD", '-');
				} else if(!("null".equals(phase))) {
					if(phase.contains("1P")) {
						if(zeroCnt == 3)
							row.put("decimalD", '-');
						else 
							row.put("decimalD", df.format(nullToZero(row.get("decimalD"))));
					} else if(phase.contains("3P")){
						if(zeroCnt == 3 || zeroCnt == 2)
							row.put("decimalD", '-');
						else 
							row.put("decimalD", df.format(nullToZero(row.get("decimalD"))));
					}
				} else {
					row.put("decimalD", df.format(nullToZero(row.get("decimalD"))));
				}

			}
		}
		
		return result;
    }
    /**
     * object의 값이 널인지 확인하고 널일경우 해당 Instance를 생성한다.
     * @param object
     * @return
     */
    private Double nullToZero(Object object){
		if (object == null)
			return 0d;

		return ((Number) object).doubleValue();
    }
    
    public Integer getPowerDetailListCount(Map<String, Object> condition){
    	int deviceType  = (Integer)condition.get("deviceType");
    	String deviceId = (String)condition.get("deviceId");
		String fromDate = (String)condition.get("fromDate");  
		String toDate   = (String)condition.get("toDate"); 

        logger.info("==conditions===="+condition);

		StringBuffer sb = new StringBuffer();
		sb.append("\n SELECT  COUNT(*) as cnt ");
		

		sb.append("\n FROM    PowerQuality p");
		sb.append("\n WHERE   1=1 ");
		if(deviceType >= 0) {
			sb.append("\n AND   p.id.mdevType = :deviceType ");
		}
		sb.append("\n AND     p.id.mdevId = :deviceId ");
		sb.append("\n AND     p.id.yyyymmddhhmm BETWEEN :fromDate AND :toDate   ");
 
		
		Query query = getSession().createQuery(sb.toString());
		
		if(deviceType >= 0) {
			query.setInteger("deviceType", deviceType);
		}
		
		query.setString("deviceId", deviceId);
		query.setString("fromDate",fromDate + "0000");
		query.setString("toDate", toDate + "2359");
		
		List<Object> list = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		
		if(list != null){
			Integer listCount = ((Number)((Map<String,Object>)list.get(0)).get("cnt")).intValue();
			listCount = listCount == null ? 0 : listCount;
			return listCount;
		}else{
			return 0;
		}
    }

    /**
     * method name : getMeterDetailInfoPqData<b/>
     * method Desc : MDIS - Meter Management 맥스가젯의 Detail Information 탭에서 PowerQuality 데이터를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getMeterDetailInfoPqData(Map<String, Object> conditionMap) {

        String mdsId = StringUtil.nullToBlank(conditionMap.get("mdsId"));
        Integer dst = (Integer)conditionMap.get("dst");

        StringBuilder sb = new StringBuilder();

        sb.append("\nSELECT p.vol_a AS voltage, ");
        sb.append("\n       p.curr_a AS currnt, ");
        sb.append("\n       p.kva_a AS apPower, ");
        sb.append("\n       p.pf_a AS powerFactor ");
        sb.append("\nFROM PowerQuality p ");
        sb.append("\nWHERE p.id.mdevType = 2 ");
        sb.append("\nAND   p.id.mdevId = :mdsId ");
        sb.append("\nAND   p.id.dst = :dst ");
        sb.append("\nAND   p.id.yyyymmddhhmm = (SELECT MAX(p2.id.yyyymmddhhmm) ");
        sb.append("\n                           FROM PowerQuality p2 ");
        sb.append("\n                           WHERE p2.id.mdevType = 2 ");
        sb.append("\n                           AND   p2.id.mdevId = :mdsId ");
        sb.append("\n                           AND   p2.id.dst = :dst ");
        sb.append("\n                          ) ");

        Query query = getSession().createQuery(sb.toString());
        query.setString("mdsId", mdsId);
        query.setInteger("dst", dst);

        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }


    /* (non-Javadoc)
     * @see com.aimir.dao.mvm.PowerQualityDao#getPowerQualityData(java.util.Map)
     */
    public Map<String, Object> getPowerQualityData(Map<String, Object> condition) {
    	Map<String, Object> result = new HashMap<String, Object>();
    	String date = StringUtil.nullToBlank(condition.get("date"));
    	Double	sag =StringUtil.nullToDoubleZero((Double) condition.get("sag"));
    	Double swell = StringUtil.nullToDoubleZero((Double) condition.get("swell"));
    	Double sagVoltage = null;
    	Double swellVoltage = null;
    	Double vol = StringUtil.nullToDoubleZero((Double) condition.get("vol"));
    	Integer page = (Integer) ObjectUtils.defaultIfNull(condition.get("page"), new Integer(0));
    	Integer limit = (Integer) ObjectUtils.defaultIfNull(condition.get("limit"), new Integer(0));
    	
    	if ( sag != 0d) {    		
    		sagVoltage = vol * (sag * (0.01) );
    		result.put("sag", sagVoltage);
    	}
    	if ( swell !=0d) {
    		swellVoltage = vol * (swell * (0.01) );
    		result.put("swell", swellVoltage);
    	}
    	
    	StringBuffer sb = new StringBuffer();
    	sb.append("\n SELECT p.hhmm as hhmm, p.id.mdevId as mdsId, contract.contractNumber as contractNumber, ");
    	sb.append("\n p.line_AB as line_ab, p.line_CA as line_ac, ");
    	sb.append("\n p.vol_a as vol_a, p.vol_b as vol_b, p.vol_c as vol_c, ");
    	sb.append("\n p.curr_a as curr_a, p.curr_b as curr_b, p.curr_c as curr_c, ");
    	sb.append("\n p.vol_thd_a as vol_thd_a, p.vol_thd_b as vol_thd_b, p.vol_thd_c as vol_thd_c, ");
    	sb.append("\n p.pf_a as pf_a, p.pf_b as pf_b, p.pf_c as pf_c, " );
    	sb.append("\n p.vol_angle_a as vol_angle_a, p.vol_angle_b as vol_angle_b, p.vol_angle_c as vol_angle_c ");
    	sb.append("\n FROM PowerQuality p left outer join p.contract contract ");
    	sb.append("\n WHERE 1=1  ");
    	
    	sb.append("\n and p.id.yyyymmddhhmm between :startdate and :enddate ");
    	sb.append("\n and p.id.dst = 0");
    	sb.append("\n and p.id.mdevType = 2");
    	
    	if ( sagVoltage != null || swellVoltage != null ) {
	    	sb.append("\n and ( ");
	    	if (sagVoltage != null) {
		    	sb.append("\n (p.vol_a <= :sag and p.vol_a != 0) or");
		    	sb.append("\n (p.vol_b <= :sag and p.vol_b != 0) or");
		    	sb.append("\n (p.vol_c <= :sag and p.vol_c != 0) ");
	    	}
	    	if ( sagVoltage != null && swellVoltage != null ) {
	    		sb.append("\n or ");
	    	}
	    	if ( swellVoltage != null) {
		    	sb.append("\n p.vol_a >= :swell or");
		    	sb.append("\n p.vol_b >= :swell or");
		    	sb.append("\n p.vol_c >= :swell  ");
	    	}
	    	sb.append("\n ) ");
    	}
    	sb.append("\n ORDER BY p.id.yyyymmddhhmm desc");
    	
    	Query query = getSession().createQuery(sb.toString());
    	if ( sagVoltage != null ) {
    		query.setDouble("sag", sagVoltage);
    	}
    	if ( swellVoltage != null ) {
    		query.setDouble("swell", swellVoltage);
    	}
    	query.setString("startdate", date+"0000");
    	query.setString("enddate", date+"2359");
    	result.put("size", query.list().size());
    	
    	query.setFirstResult((page-1)*limit);
    	query.setMaxResults(limit);
    	
    	result.put("data", query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list());
    	return result;
    }
    
    /* (non-Javadoc)
     * @see com.aimir.dao.mvm.PowerQualityDao#getPowerQualityChartData(java.util.Map)
     */
    public List<Map<String, Object>> getPowerQualityChartData(Map<String, Object> condition) {
    	List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
    	String date = StringUtil.nullToBlank(condition.get("date"));
    	Double sag = (Double) ObjectUtils.defaultIfNull(condition.get("sag"), null);
    	Double swell = (Double) ObjectUtils.defaultIfNull(condition.get("swell"), null);
    	Integer sagCount = 0;
    	Integer swellCount = 0;
    	Integer normalCount = 0;
    	StringBuffer sb = new StringBuffer();
    	sb.append("\n SELECT vol_a, vol_b, vol_c");
    	sb.append("\n FROM PowerQuality ");
    	sb.append("\n WHERE id.yyyymmddhhmm between :startdate and :enddate");
    	sb.append("\n and id.mdevType=2");
    	sb.append("\n and id.dst=0");
    	
    	Query query = getSession().createQuery(sb.toString());
    	query.setString("startdate", date+"0000");
    	query.setString("enddate", date+"2359");
    	List<Map<String, Object>> list = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    	
    	for ( Map<String, Object> row : list ) {
    		Collection<Object> values = row.values();
    		for ( Object value : values ) {
    			Double val = (Double) ObjectUtils.defaultIfNull(value, 0d);
    			if ( val >= swell ) {
    				swellCount++;
    			} else if ( val <= sag && val != 0d) {
    				sagCount++;
    			} else {
    				normalCount++;
    			}
    		}
    	}
    	Map<String, Object> sagMap = new HashMap<String, Object>();
    	Map<String, Object> swellMap = new HashMap<String, Object>();
    	Map<String, Object> normalMap = new HashMap<String, Object>();
    	sagMap.put("label", "sag");
    	sagMap.put("value", sagCount);
    	sagMap.put("color", "FFFF00");
    	ret.add(sagMap);
    	
    	swellMap.put("label", "swell");
    	swellMap.put("value", swellCount);
    	swellMap.put("color", "FF0000");
    	ret.add(swellMap);
    	
    	normalMap.put("label", "normal");
    	normalMap.put("value", normalCount);
    	ret.add(normalMap);
    	return ret;
    }
    
    @Override
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public void delete(String meterId, String yyyymmdd) {
        String qstr = "DELETE from PowerQuality WHERE id.mdevId = :meterId AND id.yyyymmddhhmm BETWEEN :startDate AND :endDate";
        Query query = getSession().createQuery(qstr);
        query.setString("meterId", meterId);
        query.setString("startDate", yyyymmdd+"0000");
        query.setString("endDate", yyyymmdd+"2359");
        query.executeUpdate();
    }
}