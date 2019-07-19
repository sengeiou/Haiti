package com.aimir.dao.device.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.constants.CommonConstants.DistTrfmrSubstationMeterPhase;
import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.device.EBSDeviceDao;
import com.aimir.model.device.EBS_DEVICE;
import com.aimir.util.SQLWrapper;
import com.aimir.util.StringUtil;


/**
 * EBSDeviceDaoImpl.java Description 
 * <p>
 * <pre>
 * Date          Version     Author   Description
 * 2012. 3. 13.  v1.0        문동규   Distribution Transformer Substation 조회
 * </pre>
 */
@Repository(value = "ebsDeviceDao")
public class EBSDeviceDaoImpl extends AbstractHibernateGenericDao<EBS_DEVICE, Integer> implements EBSDeviceDao {

    Log logger = LogFactory.getLog(EBSDeviceDaoImpl.class);
    
	@Autowired
	protected EBSDeviceDaoImpl(SessionFactory sessionFactory) {
		super(EBS_DEVICE.class);
		super.setSessionFactory(sessionFactory);
	}

    /**
     * method name : getEbsMonitoringList<b/>
     * method Desc : Energy Balance Monitoring 맥스가젯의 DTS List 를 조회한다. 
     *
     * @param conditionMap
     * @param isTotal
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getEbsMonitoringList(Map<String, Object> conditionMap, boolean isTotal) {
        List<Map<String, Object>> result;
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        String type = StringUtil.nullToBlank(conditionMap.get("type"));
        String meterId = StringUtil.nullToBlank(conditionMap.get("meterId"));
        List<Integer> locationIdList = (List<Integer>)conditionMap.get("locationIdList");
        
        String yyyymmdd = StringUtil.nullToBlank(conditionMap.get("yyyymmdd"));
//        Double threshold = (Double)conditionMap.get("threshold");
        
        Integer page = (Integer)conditionMap.get("page");
        Integer limit = (Integer)conditionMap.get("limit");

        StringBuilder sb = new StringBuilder();

        sb.append("\nSELECT ED.id AS ID, ");
        sb.append("\n       ED.typeCd.name AS TYPE_NAME, ");
        sb.append("\n       ED.meterId AS MID, ");
        sb.append("\n       ED.parentMeterId AS PARENT_MID, ");
        sb.append("\n       ED.threshold AS LOSS, ");
        sb.append("\n       ED.location.id AS LOC_ID, ");
        sb.append("\n       ED.location.name AS LOC_NAME, ");
        sb.append("\n       ED.address AS ADDR, ");
        sb.append("\n       ED.descr AS DESCR ");
        sb.append("\nFROM EBS_DEVICE ED ");
        sb.append("\nWHERE ED.supplier.id = :supplierId ");

        if (!type.isEmpty()) {
            sb.append("\nAND   ED.typeCd.id  = :type ");
        }

        if (!meterId.isEmpty()) {
            sb.append("\nAND   ED.meterId LIKE = :meterId ");
        }

        if (locationIdList != null && locationIdList.size() > 0) {
            sb.append("\nAND   ED.location.id IN (:locationIdList) ");
        }

//        if (threshold != null) {
//            sb.append("\nAND   dt.threshold >= :threshold ");
//        }

        if (!isTotal) {
            sb.append("\nORDER BY ED.typeCd.id ");
        }

        Query query = getSession().createQuery(sb.toString());
        query.setInteger("supplierId", supplierId);

        if (!type.isEmpty()) {
            query.setString("type", type);
        }

        if (!meterId.isEmpty()) {
        	query.setString("meterId", "%" + meterId + "%");
        }
        
        if (locationIdList != null && locationIdList.size() > 0) {
            query.setParameterList("locationIdList", locationIdList);
        }

//        if (threshold != null) {
//            query.setDouble("threshold", threshold);
//        }

        if (isTotal) {
            Map<String, Object> map = new HashMap<String, Object>();
            int count = 0;
            Iterator<?> itr = query.iterate();
            while(itr.hasNext()) {
                itr.next();
                count++;
            }
            map.put("total", count);
            result = new ArrayList<Map<String, Object>>();
            result.add(map);
        } else {
            query.setFirstResult((page - 1) * limit);
            query.setMaxResults(limit);
            result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        }

        return result;
    }
    
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getOrder(String meterId) {
        List<Map<String, Object>> result;
       
        StringBuilder sb = new StringBuilder();

        sb.append(" SELECT   max(ebs1.orderid) AS PORDER_ID, max(ebs2.orderid) AS CORDER_ID ");
        sb.append(" FROM     EBS_DEVICE ebs1 left outer join EBS_DEVICE ebs2 on ebs1.meter_id = ebs2.parent_mid ");
        sb.append(" WHERE    ebs1.meter_id = :meter_id ");
        Query query = getSession().createSQLQuery(sb.toString());
        query.setString("meter_id", meterId);
      
        result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
       
        return result;
    }
    
 

    @Override
	@SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getParentOrder(String typeCd) {

        StringBuffer query = new StringBuffer();
        query.append(" SELECT   max(ebs1.orderId) AS ORDERID ");
        query.append(" FROM     EBS_DEVICE ebs1 ");
        query.append(" WHERE    ebs1.typeCd.code = ? ");

        return (List<Object>)getSession().get(query.toString(), new StringBuilder().append(typeCd).toString());
    }

 
    /**
     * method name : getEbsDeviceList<b/>
     * method Desc : Energy Balance Monitoring 맥스가젯의 DTS List 를 조회한다. 
     *
     * @param conditionMap
     * @param isTotal
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getEbsDeviceList(Map<String, Object> conditionMap, boolean isTotal) {
        List<Map<String, Object>> result;
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        String type = StringUtil.nullToBlank(conditionMap.get("type"));
        String meterId = StringUtil.nullToBlank(conditionMap.get("meterId"));
        List<Integer> locationIdList = (List<Integer>)conditionMap.get("locationIdList");
//        Double threshold = (Double)conditionMap.get("threshold");
        
        Integer page = (Integer)conditionMap.get("page");
        Integer limit = (Integer)conditionMap.get("limit");

        StringBuilder sb = new StringBuilder();

        sb.append("\nSELECT ED.id AS ID, ");
        sb.append("\n       ED.typeCd.name AS TYPE_NAME, ");
        sb.append("\n       ED.meterId AS MID, ");
        sb.append("\n       ED.parentMeterId AS PARENT_MID, ");
        sb.append("\n       ED.threshold AS LOSS, ");
        sb.append("\n       ED.location.id AS LOC_ID, ");
        sb.append("\n       ED.location.name AS LOC_NAME, ");
        sb.append("\n       ED.address AS ADDR, ");
        sb.append("\n       ED.descr AS DESCR ");
        sb.append("\nFROM EBS_DEVICE ED ");
        sb.append("\nWHERE ED.supplier.id = :supplierId ");

        if (!type.isEmpty()) {
            sb.append("\nAND   ED.typeCd.id  = :type ");
        }
        
        if (!meterId.isEmpty()) {
            sb.append("\nAND   ED.meterId LIKE :meterId ");
        }

        if (locationIdList != null && locationIdList.size() > 0) {
            sb.append("\nAND   ED.location.id IN (:locationIdList) ");
        }

//        if (threshold != null) {
//            sb.append("\nAND   dt.threshold >= :threshold ");
//        }

        if (!isTotal) {
            sb.append("\nORDER BY ED.typeCd.id ");
        }

        Query query = getSession().createQuery(sb.toString());
        query.setInteger("supplierId", supplierId);

        if (!type.isEmpty()) {
            query.setString("type", type);
        }

        if (!meterId.isEmpty()) {
        	query.setString("meterId", "%" + meterId + "%");
        }
        
        if (locationIdList != null && locationIdList.size() > 0) {
            query.setParameterList("locationIdList", locationIdList);
        }

//        if (threshold != null) {
//            query.setDouble("threshold", threshold);
//        }

        if (isTotal) {
            Map<String, Object> map = new HashMap<String, Object>();
            int count = 0;
            Iterator<?> itr = query.iterate();
            while(itr.hasNext()) {
                itr.next();
                count++;
            }
            map.put("total", count);
            result = new ArrayList<Map<String, Object>>();
            result.add(map);
        } else {
            query.setFirstResult((page - 1) * limit);
            query.setMaxResults(limit);
            result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        }

        return result;
    }

    /**
     * method name : getEbsDailyMonitoringList<b/>
     * method Desc : Energy Balance Monitoring 맥스가젯의 DTS List 를 조회한다. 
     *
     * @param conditionMap
     * @param isTotal
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getEbsDailyMonitoringList(Map<String, Object> conditionMap, boolean isTotal) {
        List<Map<String, Object>> result;
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        String meterId = StringUtil.nullToBlank(conditionMap.get("meterId"));
        List<Integer> locationIdList = (List<Integer>)conditionMap.get("locationIdList");
        String yyyymmdd = StringUtil.nullToBlank(conditionMap.get("yyyymmdd"));
        Integer type = (Integer)conditionMap.get("type");
//        Double threshold = (Double)conditionMap.get("threshold");
        
        Integer page = (Integer)conditionMap.get("page");
        Integer limit = (Integer)conditionMap.get("limit");

        StringBuilder sb = new StringBuilder();
        sb.append("\nSELECT ");
        sb.append("\n        ebs.id AS ID");
        sb.append("\n       ,ebs.meter_id AS MID");
        sb.append("\n       ,ebs.THRESHOLD AS THRESHOLD");
        sb.append("\n       ,em.channel AS CHANNEL");
        sb.append("\n       ,em.total AS TOTAL");
        sb.append("\n       ,lo.name AS LOC_NAME");
        sb.append("\n       ,child.TOTAL AS CTOTAL");
        sb.append("\nFROM 	ebs_device ebs, day_em em , Location lo, ");
    	sb.append("\n(SELECT ");
    	sb.append("\n	c_ebs.parent_mid AS MID ");
    	sb.append("\n	,sum(em.total) AS TOTAL");
    	sb.append("\n	,em.channel AS CHANNEL");
    		
    	sb.append("\nFROM ebs_device ebs");
    	sb.append("\nLEFT JOIN ebs_device c_ebs ON ebs.meter_id = c_ebs.parent_mid,");
    	sb.append("\nday_em em");
    	sb.append("\nWHERE ");
    	sb.append("\n	ebs.type_id = :type ");
    	sb.append("\n	AND em.yyyymmdd = :yyyymmdd ");
    	sb.append("\n	AND em.mdev_id = c_ebs.meter_id ");
    	sb.append("\nGROUP BY c_ebs.parent_mid, em.channel ");
    	sb.append("\n	  ) child ");
	    sb.append("\nWHERE   ");
	    sb.append("\n	ebs.type_id = :type ");
	    sb.append("\n	AND em.yyyymmdd = :yyyymmdd ");
	    sb.append("\n	AND em.mdev_id = ebs.meter_id ");
	    sb.append("\n	AND em.mdev_id = child.mid ");
	    sb.append("\n	AND em.channel = child.channel ");
	    sb.append("\n	AND ebs.supplier_id = :supplierId ");
	    sb.append("\n	AND ebs.location_id = lo.id ");

        if (!meterId.isEmpty()) {
            sb.append("\nAND   ebs.meter_id LIKE = :meterId ");
        }

        if (locationIdList != null && locationIdList.size() > 0) {
            sb.append("\nAND   ebs.location_id IN (:locationIdList) ");
        }

        if (!isTotal) {
            sb.append("\nORDER BY ebs.meter_id ");
        }

        Query query = getSession().createSQLQuery(new SQLWrapper().getQuery(sb.toString()));
        query.setInteger("supplierId", supplierId);
        query.setString("yyyymmdd", yyyymmdd);
        query.setInteger("type", type);

        if (!meterId.isEmpty()) {
        	query.setString("meterId", "%" + meterId + "%");
        }

        if (locationIdList != null && locationIdList.size() > 0) {
            query.setParameterList("locationIdList", locationIdList);
        }

//        if (threshold != null) {
//            query.setDouble("threshold", threshold);
//        }

        if (isTotal) {
            Map<String, Object> map = new HashMap<String, Object>();
            int count = 0;
            Iterator<?> itr = query.iterate();
            while(itr.hasNext()) {
                itr.next();
                count++;
            }
            map.put("total", count);
            result = new ArrayList<Map<String, Object>>();
            result.add(map);
        } else {
            query.setFirstResult((page - 1) * limit);
            query.setMaxResults(limit);
            result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        }

        return result;
    }

   public Map<String, Object> getTopParentMID(String meterId){
       List<Map<String, Object>> result;

       StringBuilder sb = new StringBuilder();
       sb.append("\nSELECT ");
       sb.append("\n        code.code AS TYPE_CD");
       sb.append("\n        ,ebs.meter_id AS MID");
       sb.append("\n        ,ebs.top_parent_mid AS PMID");
       sb.append("\nFROM 	EBS_DEVICE ebs, Code code ");
	    sb.append("\nWHERE   ");
	    sb.append("\n	 ebs.meter_id = :meterId ");
	    sb.append("\nAND ebs.type_id = code.id ");

       Query query = getSession().createSQLQuery(new SQLWrapper().getQuery(sb.toString()));

       query.setString("meterId", meterId);

       result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

       if(result != null){
    	   return result != null ? result.get(0) : null;
       }else{
    	   return null;
       }
   }
   
   public List<Map<String, Object>> getMonitoringTree(String meterId, String yyyymmdd, Integer channel){
	   List<Map<String, Object>> result;

       StringBuilder sb = new StringBuilder();
       sb.append("\nSELECT ");
       sb.append("\n        ebs.meter_id AS MID");
       sb.append("\n        ,ebs.parent_mid AS PMID");
       sb.append("\n        ,ebs.parent_type_id AS PTYPE");
       sb.append("\n        ,ebs.top_parent_mid AS TPMID");
       sb.append("\n        ,ebs.threshold AS THRESHOLD");
       sb.append("\n        ,ebs.type_id AS TYPE");
       sb.append("\n        ,ebs.orderId AS EBS_ORDER");
       sb.append("\n        ,em.total AS TOTAL");
       sb.append("\n        ,em.channel AS CHANNEL");
       sb.append("\n        ,code.code AS TYPE_CD");
       sb.append("\nFROM 	ebs_device ebs left outer join day_em em on em.mdev_id = ebs.meter_id  and em.yyyymmdd = :yyyymmdd and em.channel = :channel ");
       sb.append("\n                       left join code code on ebs.type_id = code.id ");
	   sb.append("\nWHERE   ");
	   sb.append("\n	ebs.top_parent_mid = :meterId ");
	   sb.append("\n	ORDER BY ebs.orderId ");
	    

       Query query = getSession().createSQLQuery(new SQLWrapper().getQuery(sb.toString()));
       query.setString("yyyymmdd", yyyymmdd);
       query.setString("meterId", meterId);
       query.setInteger("channel", channel);
       result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

       if(result != null){
    	   return result;
       }else{
    	   return null;
       }
   }
   
   public List<Map<String, Object>> getMonitoringTreeMonthly(String meterId, String yyyymmdd, Integer channel){
	   List<Map<String, Object>> result;

       StringBuilder sb = new StringBuilder();
       sb.append("\nSELECT ");
       sb.append("\n        ebs.meter_id AS MID");
       sb.append("\n        ,ebs.parent_mid AS PMID");
       sb.append("\n        ,ebs.parent_type_id AS PTYPE");
       sb.append("\n        ,ebs.top_parent_mid AS TPMID");
       sb.append("\n        ,ebs.threshold AS THRESHOLD");
       sb.append("\n        ,ebs.type_id AS TYPE");
       sb.append("\n        ,ebs.orderId AS EBS_ORDER");
       sb.append("\n        ,em.total AS TOTAL");
       sb.append("\n        ,em.channel AS CHANNEL");
       sb.append("\n        ,code.code AS TYPE_CD");
       sb.append("\nFROM 	ebs_device ebs left outer join month_em em on em.mdev_id = ebs.meter_id  and em.yyyymm = :yyyymm and em.channel = :channel ");
       sb.append("\n                       left join code code on ebs.type_id = code.id ");
	    sb.append("\nWHERE   ");
	    sb.append("\n	ebs.top_parent_mid = :meterId ");
	    sb.append("\n	ORDER BY ebs.orderId ");

       Query query = getSession().createSQLQuery(new SQLWrapper().getQuery(sb.toString()));
       query.setString("yyyymm", yyyymmdd.substring(0, 6));
       query.setString("meterId", meterId);
       query.setInteger("channel", channel);
       result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

       if(result != null){
    	   return result;
       }else{
    	   return null;
       }
   }
    /**
     * method name : getEbsMonthlyMonitoringList<b/>
     * method Desc : Energy Balance Monitoring 맥스가젯의 DTS List 를 조회한다. 
     *
     * @param conditionMap
     * @param isTotal
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getEbsMonthlyMonitoringList(Map<String, Object> conditionMap, boolean isTotal) {
        List<Map<String, Object>> result;
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        String meterId = StringUtil.nullToBlank(conditionMap.get("meterId"));
        List<Integer> locationIdList = (List<Integer>)conditionMap.get("locationIdList");
        String yyyymmdd = StringUtil.nullToBlank(conditionMap.get("yyyymmdd"));
        Integer type = (Integer)conditionMap.get("type");
//        Double threshold = (Double)conditionMap.get("threshold");
        
        Integer page = (Integer)conditionMap.get("page");
        Integer limit = (Integer)conditionMap.get("limit");

        StringBuilder sb = new StringBuilder();
        sb.append("\nSELECT ");
        sb.append("\n        ebs.id AS ID");
        sb.append("\n       ,ebs.meter_id AS MID");
        sb.append("\n       ,ebs.THRESHOLD AS THRESHOLD");
        sb.append("\n       ,em.channel AS CHANNEL");
        sb.append("\n       ,em.total AS TOTAL");
        sb.append("\n       ,lo.name AS LOC_NAME");
        sb.append("\n       ,child.TOTAL AS CTOTAL");
        sb.append("\nFROM 	ebs_device ebs, month_em em , Location lo, ");
    	sb.append("\n(SELECT ");
    	sb.append("\n	c_ebs.parent_mid AS MID ");
    	sb.append("\n	,sum(em.total) AS TOTAL");
    	sb.append("\n	,em.channel AS CHANNEL");
    		
    	sb.append("\nFROM ebs_device ebs");
    	sb.append("\nLEFT JOIN ebs_device c_ebs ON ebs.meter_id = c_ebs.parent_mid,");
    	sb.append("\nmonth_em em");
    	sb.append("\nWHERE ");
    	sb.append("\n	ebs.type_id = :type ");
    	sb.append("\n	AND em.yyyymm = :yyyymm ");
    	sb.append("\n	AND em.mdev_id = c_ebs.meter_id ");
    	sb.append("\nGROUP BY c_ebs.parent_mid, em.channel ");
    	sb.append("\n	  ) child ");
	    sb.append("\nWHERE   ");
	    sb.append("\n	ebs.type_id = :type ");
	    sb.append("\n	AND em.yyyymm = :yyyymm ");
	    sb.append("\n	AND em.mdev_id = ebs.meter_id ");
	    sb.append("\n	AND em.mdev_id = child.mid ");
	    sb.append("\n	AND em.channel = child.channel ");
	    sb.append("\n	AND ebs.supplier_id = :supplierId ");
	    sb.append("\n	AND ebs.location_id = lo.id ");

        if (!meterId.isEmpty()) {
            sb.append("\nAND   ebs.meter_id LIKE = :meterId ");
        }

        if (locationIdList != null && locationIdList.size() > 0) {
            sb.append("\nAND   ebs.location_id IN (:locationIdList) ");
        }

//        if (threshold != null) {
//            sb.append("\nAND   dt.threshold >= :threshold ");
//        }

        if (!isTotal) {
            sb.append("\nORDER BY ebs.meter_id ");
        }

     //   Query query = getSession().createQuery(sb.toString());
        Query query = getSession().createSQLQuery(new SQLWrapper().getQuery(sb.toString()));
        query.setInteger("supplierId", supplierId);
        query.setString("yyyymm", yyyymmdd.substring(0,  6));
        query.setInteger("type", type);

        if (!meterId.isEmpty()) {
        	query.setString("meterId", "%" + meterId + "%");
        }

        if (locationIdList != null && locationIdList.size() > 0) {
            query.setParameterList("locationIdList", locationIdList);
        }

//        if (threshold != null) {
//            query.setDouble("threshold", threshold);
//        }

        if (isTotal) {
            Map<String, Object> map = new HashMap<String, Object>();
            int count = 0;
            Iterator<?> itr = query.iterate();
            while(itr.hasNext()) {
                itr.next();
                count++;
            }
            map.put("total", count);
            result = new ArrayList<Map<String, Object>>();
            result.add(map);
        } else {
            query.setFirstResult((page - 1) * limit);
            query.setMaxResults(limit);
            result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        }

        return result;
    }
    
    /**
     * method name : getEbsDeviceList<b/>
     * method Desc : Energy Balance Monitoring 맥스가젯의 DTS List 를 조회한다. 
     *
     * @param conditionMap
     * @param isTotal
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getEbsMeterList(Map<String, Object> conditionMap, boolean isTotal) {
        List<Map<String, Object>> result;
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        String vendorName = conditionMap.get("vendorName") != null ? (String)conditionMap.get("vendorName") : null;
        Integer deviceModelId = conditionMap.get("deviceModelId") != null ? (Integer)conditionMap.get("deviceModelId") : null;
        String mdsId = conditionMap.get("mdsId") != null ? StringUtil.nullToBlank(conditionMap.get("mdsId")) : "";

        Integer page = (Integer)conditionMap.get("page");
        Integer limit = (Integer)conditionMap.get("limit");

        StringBuilder sb = new StringBuilder();

        sb.append("\nSELECT mt.id AS meterId, ");
        sb.append("\n       ebs.meterId AS mdsId, ");
        sb.append("\n       lo.name AS location, ");
        sb.append("\n       mt.model.name AS model, ");
        sb.append("\n       mt.installDate AS installDate, ");
        sb.append("\n       mt.meterStatus.descr AS meterStatus ");
        sb.append("\nFROM EBS_DEVICE ebs ");
        sb.append("\n     LEFT OUTER JOIN ");
        sb.append("\n     ebs.location lo, Meter mt ");
        sb.append("\nWHERE ebs.supplier.id = :supplierId ");
        // TODO DTS 에 포함 가능한 미터 조건
        sb.append("\nAND   mt.mdsId = ebs.meterId ");              // 고압미터
       // sb.append("\nAND   mt.meterStatus.code IN ('1.3.3.1', '1.3.3.8') ");      // 미터상태:Normal or New Registered
      //  sb.append("\nAND   mt.distTrfmrSubstation IS NULL ");

        if (!mdsId.isEmpty()) {
            sb.append("\nAND   ebs.meterId LIKE :mdsId ");
        }
       
        if(vendorName != null && deviceModelId == 0) {
        	sb.append("\nAND   mt.model.deviceVendor.name = :vendorName ");
        }
        	
        if (deviceModelId != null && deviceModelId > 0) {
            sb.append("\nAND   mt.model.id = :deviceModelId ");
        } 
   

        if (!isTotal) {
            sb.append("\nORDER BY ebs.meterId ");
        }

        Query query = getSession().createQuery(sb.toString());
        query.setInteger("supplierId", supplierId);

        if (!mdsId.isEmpty()) {
            query.setString("mdsId", "%" + mdsId + "%");
        }

        if(vendorName != null && deviceModelId == 0) {
        	query.setString("vendorName", vendorName);
        }
        
        if (deviceModelId != null && deviceModelId > 0) {
            query.setInteger("deviceModelId", deviceModelId);
        }
        
        if (isTotal) {
            Map<String, Object> map = new HashMap<String, Object>();
            int count = 0;

            Iterator<?> itr = query.iterate();
            while(itr.hasNext()) {
                itr.next();
                count++;
            }
            map.put("total", count);
            result = new ArrayList<Map<String, Object>>();
            result.add(map);
        } else {
            query.setFirstResult((page - 1) * limit);
            query.setMaxResults(limit);
            result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        }

        return result;
    }
    
    /**
     * method name : getEbsDeviceList<b/>
     * method Desc : Energy Balance Monitoring 맥스가젯의 DTS List 를 조회한다. 
     *
     * @param conditionMap
     * @param isTotal
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getMeterList(Map<String, Object> conditionMap, boolean isTotal) {
        List<Map<String, Object>> result;
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        String vendorName = conditionMap.get("vendorName") != null ? (String)conditionMap.get("vendorName") : null;
        Integer deviceModelId = conditionMap.get("deviceModelId") != null ? (Integer)conditionMap.get("deviceModelId") : null;
        String mdsId = conditionMap.get("mdsId") != null ? StringUtil.nullToBlank(conditionMap.get("mdsId")) : "";

        Integer page = (Integer)conditionMap.get("page");
        Integer limit = (Integer)conditionMap.get("limit");

        StringBuilder sb = new StringBuilder();

        sb.append("\nSELECT mt.id AS meterId, ");
        sb.append("\n       mt.mdsId AS mdsId, ");
        sb.append("\n       lo.name AS location, ");
        sb.append("\n       mt.model.name AS model, ");
        sb.append("\n       mt.installDate AS installDate, ");
        sb.append("\n       mt.meterStatus.descr AS meterStatus ");
        sb.append("\nFROM Meter mt ");
        sb.append("\n     LEFT OUTER JOIN ");
        sb.append("\n     mt.location lo ");
        sb.append("\nWHERE mt.supplier.id = :supplierId ");
        // TODO DTS 에 포함 가능한 미터 조건
       // sb.append("\nAND   mt.model.name = 'A1140' ");              // 고압미터
       // sb.append("\nAND   mt.meterStatus.code IN ('1.3.3.1', '1.3.3.8') ");      // 미터상태:Normal or New Registered
      //  sb.append("\nAND   mt.distTrfmrSubstation IS NULL ");

        if (!mdsId.isEmpty()) {
            sb.append("\nAND   mt.mdsId LIKE :mdsId ");
        }
        
        if(vendorName != null && deviceModelId == 0) {
        	sb.append("\nAND   mt.model.deviceVendor.name = :vendorName ");
        }
       
        if (deviceModelId != null && deviceModelId > 0) {
            sb.append("\nAND   mt.model.id = :deviceModelId ");
        } 
   

        if (!isTotal) {
            sb.append("\nORDER BY mt.mdsId ");
        }

        Query query = getSession().createQuery(sb.toString());
        query.setInteger("supplierId", supplierId);

        if (!mdsId.isEmpty()) {
            query.setString("mdsId", "%" + mdsId + "%");
        }
        
        if(vendorName != null && deviceModelId == 0) {
        	query.setString("vendorName",vendorName);
        }

        if (deviceModelId != null && deviceModelId > 0) {
            query.setInteger("deviceModelId", deviceModelId);
        }
        
        if (isTotal) {
            Map<String, Object> map = new HashMap<String, Object>();
            int count = 0;

            Iterator<?> itr = query.iterate();
            while(itr.hasNext()) {
                itr.next();
                count++;
            }
            map.put("total", count);
            result = new ArrayList<Map<String, Object>>();
            result.add(map);
        } else {
            query.setFirstResult((page - 1) * limit);
            query.setMaxResults(limit);
            result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        }

        return result;
    }
      
    @SuppressWarnings("unchecked")
    public Integer getParentId(String meterId) {
        List<Map<String, Object>> result;
       
        StringBuilder sb = new StringBuilder();

        sb.append(" SELECT   typeId AS TYPE_ID ");
        sb.append(" FROM     EBS_DEVICE ebs ");
        sb.append(" WHERE    ebs.meterId = :meterId ");
        Query query = getSession().createQuery(sb.toString());
        query.setString("meterId", meterId);
      
        result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
       
        return Integer.valueOf(result.get(0).get("TYPE_ID").toString());
    }

    
	@SuppressWarnings("unchecked")
	public int deleteById(final Integer id) {
			int affectedRecords = getSession().createQuery(
								"delete from EBS_DEVICE where id = ?")
								.setParameter(0, id)
								.executeUpdate();
			return affectedRecords;
	}
	
	@SuppressWarnings("unchecked")
	public int deleteValify(final Integer id) {
		
        // totalCount - 시작
        StringBuffer sb = new StringBuffer();
        sb.append("\n SELECT COUNT(*) ");
        sb.append("\n FROM EBS_DEVICE ebs, EBS_DEVICE ebs_child  ");
        sb.append("\n WHERE ebs.meterId=ebs_child.parentMeterId and ebs.id=:id");

        Query query = getSession().createQuery(sb.toString());
        query.setInteger("id", id);
        Number totalCount = (Number)query.uniqueResult();
        
        return totalCount.intValue();
	}
	
    /**
     * method name : getEbsSuspectedDtsList<b/>
     * method Desc : Energy Balance Monitoring 미니가젯에서 Suspected Substation List 를 조회한다. 
     *
     * @param conditionMap
     * @param isTotal
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getEbsSuspectedDtsList(Map<String, Object> conditionMap, boolean isTotal) {
        List<Map<String, Object>> result;
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        String searchStartDate = (String)conditionMap.get("searchStartDate");
        String searchEndDate = (String)conditionMap.get("searchEndDate");
        String searchPreStartDate = (String)conditionMap.get("searchPreStartDate");
        Integer page = (Integer)conditionMap.get("page");
        Integer limit = (Integer)conditionMap.get("limit");

        StringBuilder sb = new StringBuilder();

        if (isTotal) {
            sb.append("\nSELECT COUNT(*) AS cnt FROM ( ");
        }

        sb.append("\nSELECT dtsName AS DTS_NAME, ");
        sb.append("\n       dtsId AS DTS_ID, ");
        sb.append("\n       threshold AS THRESHOLD, ");
        sb.append("\n       SUM(importEnergyTotal) AS IMPORT_ENERGY_TOTAL, ");
        sb.append("\n       SUM(consumeEnergyTotal) AS CONSUME_ENERGY_TOTAL ");
        sb.append("\nFROM ( ");
        sb.append("\n        SELECT dtsName, ");
        sb.append("\n               dtsId, ");
        sb.append("\n               threshold, ");
        sb.append("\n               CASE WHEN SUM(importEnergyTotal1) = 0 THEN 0 ");
        sb.append("\n                    ELSE SUM(importEnergyTotal1) - SUM(importEnergyTotal2) END AS importEnergyTotal, ");
        sb.append("\n               0 AS consumeEnergyTotal ");
        sb.append("\n        FROM ( ");
        sb.append("\n                SELECT dt.name AS dtsName, ");
        sb.append("\n                       dt.id AS dtsId, ");
        sb.append("\n                       dt.threshold, ");
        sb.append("\n                       me.id AS meterId, ");
        sb.append("\n                       re.activeenergyimportratetotal AS importEnergyTotal1, ");
        sb.append("\n                       0 AS importEnergyTotal2 ");
        sb.append("\n                FROM disttrfmrsubstation dt, ");
        sb.append("\n                     meter me, ");
        sb.append("\n                     realtime_billing_em re ");
        sb.append("\n                WHERE dt.supplier_id = :supplierId ");
        sb.append("\n                AND   me.disttrfmrsubstation_id = dt.id ");
        sb.append("\n                AND   me.supplier_id = dt.supplier_id ");
        sb.append("\n                AND   re.meter_id = me.id ");
//        sb.append("\n                AND   re.mdev_id = me.mds_id ");
//        sb.append("\n                AND   re.mdev_type = :mdevType ");
        sb.append("\n                AND   re.yyyymmdd = (SELECT MAX(re2.yyyymmdd) ");
        sb.append("\n                                     FROM realtime_billing_em re2 ");
        sb.append("\n                                     WHERE re2.mdev_id = re.mdev_id ");
        sb.append("\n                                     AND   re2.mdev_type = re.mdev_type ");
        sb.append("\n                                     AND   re2.yyyymmdd BETWEEN :searchStartDate AND :searchEndDate) ");
        sb.append("\n                AND   re.hhmmss = (SELECT MAX(sr.hhmmss) ");
        sb.append("\n                                   FROM realtime_billing_em sr ");
        sb.append("\n                                   WHERE sr.mdev_id = re.mdev_id ");
        sb.append("\n                                   AND   sr.mdev_type = re.mdev_type ");
        sb.append("\n                                   AND   sr.yyyymmdd = re.yyyymmdd) ");
        sb.append("\n                UNION ALL ");
        sb.append("\n                SELECT dt.name AS dtsName, ");
        sb.append("\n                       dt.id AS dtsId, ");
        sb.append("\n                       dt.threshold, ");
        sb.append("\n                       me.id AS meterId, ");
        sb.append("\n                       0 AS importEnergyTotal1, ");
        sb.append("\n                       re.activeenergyimportratetotal AS importEnergyTotal2 ");
        sb.append("\n                FROM disttrfmrsubstation dt, ");
        sb.append("\n                     meter me, ");
        sb.append("\n                     realtime_billing_em re ");
        sb.append("\n                WHERE dt.supplier_id = :supplierId ");
        sb.append("\n                AND   me.disttrfmrsubstation_id = dt.id ");
        sb.append("\n                AND   me.supplier_id = dt.supplier_id ");
        sb.append("\n                AND   re.meter_id = me.id ");
//        sb.append("\n                AND   re.mdev_id = me.mds_id ");
//        sb.append("\n                AND   re.mdev_type = :mdevType ");
        sb.append("\n                AND   re.yyyymmdd = (SELECT MAX(re2.yyyymmdd) ");
        sb.append("\n                                     FROM realtime_billing_em re2 ");
        sb.append("\n                                     WHERE re2.mdev_id = re.mdev_id ");
        sb.append("\n                                     AND   re2.mdev_type = re.mdev_type ");
        sb.append("\n                                     AND   re2.yyyymmdd >= :searchPreStartDate ");
        sb.append("\n                                     AND   re2.yyyymmdd < :searchStartDate) ");
        sb.append("\n                AND   re.hhmmss = (SELECT MAX(sr.hhmmss) ");
        sb.append("\n                                   FROM realtime_billing_em sr ");
        sb.append("\n                                   WHERE sr.mdev_id = re.mdev_id ");
        sb.append("\n                                   AND   sr.mdev_type = re.mdev_type ");
        sb.append("\n                                   AND   sr.yyyymmdd = re.yyyymmdd) ");
        sb.append("\n            ) x ");
        sb.append("\n        GROUP BY dtsName, dtsId, threshold, meterId ");
        sb.append("\n        UNION ALL ");
        sb.append("\n        SELECT dtsName, ");
        sb.append("\n               dtsId, ");
        sb.append("\n               threshold, ");
        sb.append("\n               0 AS importEnergyTotal, ");
        sb.append("\n               SUM(consumeEnergyTotal) AS consumeEnergyTotal ");
        sb.append("\n        FROM ( ");
        sb.append("\n                SELECT dtsName, ");
        sb.append("\n                       dtsId, ");
        sb.append("\n                       threshold, ");
        sb.append("\n                       meterId, ");
        sb.append("\n                       contMeterId, ");
        sb.append("\n                       SUM(consumeEnergyTotal1) AS consumeEnergyTotal1, ");
        sb.append("\n                       SUM(consumeEnergyTotal2) AS consumeEnergyTotal2, ");
        sb.append("\n                       CASE WHEN SUM(consumeEnergyTotal1) = 0 THEN 0 ");
        sb.append("\n                            ELSE SUM(consumeEnergyTotal1) - SUM(consumeEnergyTotal2) END AS consumeEnergyTotal ");
        sb.append("\n                FROM ( ");
        sb.append("\n                        SELECT dt.name AS dtsName, ");
        sb.append("\n                               dt.id AS dtsId, ");
        sb.append("\n                               dt.threshold, ");
        sb.append("\n                               me.id AS meterId, ");
        sb.append("\n                               mt.id AS contMeterId, ");
        sb.append("\n                               be.activeenergyratetot AS consumeEnergyTotal1, ");
        sb.append("\n                               0 AS consumeEnergyTotal2 ");
        sb.append("\n                        FROM disttrfmrsubstation dt, ");
        sb.append("\n                             meter me, ");
        sb.append("\n                             meter mt ");
        sb.append("\n                             LEFT OUTER JOIN ");
        sb.append("\n                             contract ct ");
        sb.append("\n                             ON ct.meter_id = mt.id, ");
//        sb.append("\n                             contract ct, ");
//        sb.append("\n                             code cd, ");
        sb.append("\n                             billing_day_em be ");
        sb.append("\n                        WHERE dt.supplier_id = :supplierId ");
        sb.append("\n                        AND   me.disttrfmrsubstation_id = dt.id ");
        sb.append("\n                        AND   me.supplier_id = dt.supplier_id ");
        sb.append("\n                        AND   (mt.disttrfmrsubstationmeter_a_id = me.id ");
        sb.append("\n                            OR mt.disttrfmrsubstationmeter_b_id = me.id ");
        sb.append("\n                            OR mt.disttrfmrsubstationmeter_c_id = me.id) ");
//        sb.append("\n                        AND   ct.supplier_id = me.supplier_id ");
//        sb.append("\n                        AND   ct.servicetype_id = cd.id ");
//        sb.append("\n                        AND   cd.code = :serviceType ");
//        sb.append("\n                        AND   be.contract_id = ct.id ");
//        sb.append("\n                        AND   be.meter_id = mt.id ");
        sb.append("\n                        AND   be.mdev_id = mt.mds_id ");
        sb.append("\n                        AND   be.mdev_type = :mdevType ");
        sb.append("\n                        AND   be.yyyymmdd = (SELECT MAX(be2.yyyymmdd) ");
        sb.append("\n                                             FROM billing_day_em be2 ");
        sb.append("\n                                             WHERE be2.mdev_id = be.mdev_id ");
        sb.append("\n                                             AND   be2.mdev_type = be.mdev_type ");
        sb.append("\n                                             AND   be2.yyyymmdd BETWEEN :searchStartDate AND :searchEndDate) ");
        sb.append("\n                        UNION ALL ");
        sb.append("\n                        SELECT dt.name AS dtsName, ");
        sb.append("\n                               dt.id AS dtsId, ");
        sb.append("\n                               dt.threshold, ");
        sb.append("\n                               me.id AS meterId, ");
        sb.append("\n                               mt.id AS contMeterId, ");
        sb.append("\n                               0 AS consumeEnergyTotal1, ");
        sb.append("\n                               be.activeenergyratetot AS consumeEnergyTotal2 ");
        sb.append("\n                        FROM disttrfmrsubstation dt, ");
        sb.append("\n                             meter me, ");
        sb.append("\n                             meter mt ");
        sb.append("\n                             LEFT OUTER JOIN ");
        sb.append("\n                             contract ct ");
        sb.append("\n                             ON ct.meter_id = mt.id, ");
//        sb.append("\n                             contract ct, ");
//        sb.append("\n                             code cd, ");
        sb.append("\n                             billing_day_em be ");
        sb.append("\n                        WHERE dt.supplier_id = :supplierId ");
        sb.append("\n                        AND   me.disttrfmrsubstation_id = dt.id ");
        sb.append("\n                        AND   me.supplier_id = dt.supplier_id ");
        sb.append("\n                        AND   (mt.disttrfmrsubstationmeter_a_id = me.id ");
        sb.append("\n                            OR mt.disttrfmrsubstationmeter_b_id = me.id ");
        sb.append("\n                            OR mt.disttrfmrsubstationmeter_c_id = me.id) ");
//        sb.append("\n                        AND   ct.supplier_id = me.supplier_id ");
//        sb.append("\n                        AND   ct.servicetype_id = cd.id ");
//        sb.append("\n                        AND   cd.code = :serviceType ");
//        sb.append("\n                        AND   be.contract_id = ct.id ");
//        sb.append("\n                        AND   be.meter_id = mt.id ");
        sb.append("\n                        AND   be.mdev_id = mt.mds_id ");
        sb.append("\n                        AND   be.mdev_type = :mdevType ");
        sb.append("\n                        AND   be.yyyymmdd = (SELECT MAX(be2.yyyymmdd) ");
        sb.append("\n                                             FROM billing_day_em be2 ");
        sb.append("\n                                             WHERE be2.mdev_id = be.mdev_id ");
        sb.append("\n                                             AND   be2.mdev_type = be.mdev_type ");
        sb.append("\n                                             AND   be2.yyyymmdd >= :searchPreStartDate ");
        sb.append("\n                                             AND   be2.yyyymmdd < :searchStartDate) ");
        sb.append("\n                    ) v ");
        sb.append("\n                GROUP BY dtsName, dtsId, threshold, meterId, contMeterId ");
        sb.append("\n            ) w ");
        sb.append("\n        GROUP BY dtsName, dtsId, threshold, meterId ");
        sb.append("\n    ) x ");
        sb.append("\nGROUP BY dtsName, dtsId, threshold ");
        sb.append("\nHAVING SUM(importEnergyTotal) > 0 ");
        sb.append("\nAND    ((SUM(importEnergyTotal) - SUM(consumeEnergyTotal)) / SUM(importEnergyTotal)) > (threshold / 100) ");

        if (!isTotal) {
            sb.append("\nORDER BY dtsName ");
        } else {
            sb.append("\n) y ");
        }

        Query query = getSession().createSQLQuery(new SQLWrapper().getQuery(sb.toString()));

        query.setInteger("supplierId", supplierId);
        query.setInteger("mdevType", DeviceType.Meter.getCode());
//        query.setString("serviceType", MeterType.EnergyMeter.getServiceType());
        query.setString("searchPreStartDate", searchPreStartDate);
        query.setString("searchStartDate", searchStartDate);
        query.setString("searchEndDate", searchEndDate);

        if (isTotal) {
            Map<String, Object> map = new HashMap<String, Object>();
            Number count = (Number)query.uniqueResult();
            map.put("total", count.intValue());
            result = new ArrayList<Map<String, Object>>();
            result.add(map);
        } else {
            query.setFirstResult((page - 1) * limit);
            query.setMaxResults(limit);
            result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        }

        return result;
    }

    /**
     * method name : getEbsDtsStateChartData<b/>
     * method Desc : Energy Balance Monitoring 미니가젯에서 Normal/Suspected Substation Count Chart Data 를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getEbsDtsStateChartData(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result;
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        String searchStartDate = (String)conditionMap.get("searchStartDate");
        String searchEndDate = (String)conditionMap.get("searchEndDate");
        String searchPreStartDate = (String)conditionMap.get("searchPreStartDate");

        StringBuilder sb = new StringBuilder();

        sb.append("\nSELECT COALESCE(COUNT(dtsName), 0) AS TOTAL_COUNT, ");
        sb.append("\n       COALESCE(SUM(CASE WHEN importEnergyTotal > 0 AND ((importEnergyTotal - consumeEnergyTotal) / importEnergyTotal) > (threshold / 100) THEN 0 ");
        sb.append("\n                ELSE 1 END), 0) AS NORMAL_COUNT, ");
        sb.append("\n       COALESCE(SUM(CASE WHEN importEnergyTotal > 0 AND ((importEnergyTotal - consumeEnergyTotal) / importEnergyTotal) > (threshold / 100) THEN 1 ");
        sb.append("\n                ELSE 0 END), 0) AS SUSPECTED_COUNT ");
        sb.append("\nFROM ( ");
        sb.append("\n        SELECT dtsName, ");
        sb.append("\n               dtsId, ");
        sb.append("\n               threshold, ");
        sb.append("\n               SUM(importEnergyTotal) AS importEnergyTotal, ");
        sb.append("\n               SUM(consumeEnergyTotal) AS consumeEnergyTotal ");
        sb.append("\n        FROM ( ");
        sb.append("\n                SELECT dt.name AS dtsName, ");
        sb.append("\n                       dt.id AS dtsId, ");
        sb.append("\n                       dt.threshold, ");
        sb.append("\n                       0 AS importEnergyTotal, ");
        sb.append("\n                       0 AS consumeEnergyTotal ");
        sb.append("\n                FROM disttrfmrsubstation dt, ");
        sb.append("\n                     location lo ");
        sb.append("\n                WHERE dt.supplier_id = :supplierId ");
        sb.append("\n                AND   lo.id = dt.location_id ");
        sb.append("\n                UNION ALL ");
        sb.append("\n                SELECT dtsName, ");
        sb.append("\n                       dtsId, ");
        sb.append("\n                       threshold, ");
        sb.append("\n                       CASE WHEN SUM(importEnergyTotal1) = 0 THEN 0 ");
        sb.append("\n                            ELSE SUM(importEnergyTotal1) - SUM(importEnergyTotal2) END AS importEnergyTotal, ");
        sb.append("\n                       0 AS consumeEnergyTotal ");
        sb.append("\n                FROM ( ");
        sb.append("\n                        SELECT dt.name AS dtsName, ");
        sb.append("\n                               dt.id AS dtsId, ");
        sb.append("\n                               dt.threshold, ");
        sb.append("\n                               me.id AS meterId, ");
        sb.append("\n                               re.activeenergyimportratetotal AS importEnergyTotal1, ");
        sb.append("\n                               0 AS importEnergyTotal2 ");
        sb.append("\n                        FROM disttrfmrsubstation dt, ");
        sb.append("\n                             meter me, ");
        sb.append("\n                             realtime_billing_em re ");
        sb.append("\n                        WHERE dt.supplier_id = :supplierId ");
        sb.append("\n                        AND   me.disttrfmrsubstation_id = dt.id ");
        sb.append("\n                        AND   me.supplier_id = dt.supplier_id ");
        sb.append("\n                        AND   re.meter_id = me.id ");
//        sb.append("\n                        AND   re.mdev_id = me.mds_id ");
//        sb.append("\n                        AND   re.mdev_type = :mdevType ");
        sb.append("\n                        AND   re.yyyymmdd = (SELECT MAX(re2.yyyymmdd) ");
        sb.append("\n                                             FROM realtime_billing_em re2 ");
        sb.append("\n                                             WHERE re2.mdev_id = re.mdev_id ");
        sb.append("\n                                             AND   re2.mdev_type = re.mdev_type ");
        sb.append("\n                                             AND   re2.yyyymmdd BETWEEN :searchStartDate AND :searchEndDate) ");
        sb.append("\n                        AND   re.hhmmss = (SELECT MAX(sr.hhmmss) ");
        sb.append("\n                                           FROM realtime_billing_em sr ");
        sb.append("\n                                           WHERE sr.mdev_id = re.mdev_id ");
        sb.append("\n                                           AND   sr.mdev_type = re.mdev_type ");
        sb.append("\n                                           AND   sr.yyyymmdd = re.yyyymmdd) ");
        sb.append("\n                        UNION ALL ");
        sb.append("\n                        SELECT dt.name AS dtsName, ");
        sb.append("\n                               dt.id AS dtsId, ");
        sb.append("\n                               dt.threshold, ");
        sb.append("\n                               me.id AS meterId, ");
        sb.append("\n                               0 AS importEnergyTotal1, ");
        sb.append("\n                               re.activeenergyimportratetotal AS importEnergyTotal2 ");
        sb.append("\n                        FROM disttrfmrsubstation dt, ");
        sb.append("\n                             meter me, ");
        sb.append("\n                             realtime_billing_em re ");
        sb.append("\n                        WHERE dt.supplier_id = :supplierId ");
        sb.append("\n                        AND   me.disttrfmrsubstation_id = dt.id ");
        sb.append("\n                        AND   me.supplier_id = dt.supplier_id ");
        sb.append("\n                        AND   re.meter_id = me.id ");
//        sb.append("\n                        AND   re.mdev_id = me.mds_id ");
//        sb.append("\n                        AND   re.mdev_type = :mdevType ");
        sb.append("\n                        AND   re.yyyymmdd = (SELECT MAX(re2.yyyymmdd) ");
        sb.append("\n                                             FROM realtime_billing_em re2 ");
        sb.append("\n                                             WHERE re2.mdev_id = re.mdev_id ");
        sb.append("\n                                             AND   re2.mdev_type = re.mdev_type ");
        sb.append("\n                                             AND   re2.yyyymmdd >= :searchPreStartDate ");
        sb.append("\n                                             AND   re2.yyyymmdd < :searchStartDate) ");
        sb.append("\n                        AND   re.hhmmss = (SELECT MAX(sr.hhmmss) ");
        sb.append("\n                                           FROM realtime_billing_em sr ");
        sb.append("\n                                           WHERE sr.mdev_id = re.mdev_id ");
        sb.append("\n                                           AND   sr.mdev_type = re.mdev_type ");
        sb.append("\n                                           AND   sr.yyyymmdd = re.yyyymmdd) ");
        sb.append("\n                    ) x ");
        sb.append("\n                GROUP BY dtsName, dtsId, threshold, meterId ");
        sb.append("\n                UNION ALL ");
        sb.append("\n                SELECT dtsName, ");
        sb.append("\n                       dtsId, ");
        sb.append("\n                       threshold, ");
        sb.append("\n                       0 AS importEnergyTotal, ");
        sb.append("\n                       SUM(consumeEnergyTotal) AS consumeEnergyTotal ");
        sb.append("\n                FROM ( ");
        sb.append("\n                        SELECT dtsName, ");
        sb.append("\n                               dtsId, ");
        sb.append("\n                               threshold, ");
        sb.append("\n                               meterId, ");
        sb.append("\n                               contMeterId, ");
        sb.append("\n                               SUM(consumeEnergyTotal1) AS consumeEnergyTotal1, ");
        sb.append("\n                               SUM(consumeEnergyTotal2) AS consumeEnergyTotal2, ");
        sb.append("\n                               CASE WHEN SUM(consumeEnergyTotal1) = 0 THEN 0 ");
        sb.append("\n                                    ELSE SUM(consumeEnergyTotal1) - SUM(consumeEnergyTotal2) END AS consumeEnergyTotal ");
        sb.append("\n                        FROM ( ");
        sb.append("\n                                SELECT dt.name AS dtsName, ");
        sb.append("\n                                       dt.id AS dtsId, ");
        sb.append("\n                                       dt.threshold, ");
        sb.append("\n                                       me.id AS meterId, ");
        sb.append("\n                                       mt.id AS contMeterId, ");
        sb.append("\n                                       be.activeenergyratetot AS consumeEnergyTotal1, ");
        sb.append("\n                                       0 AS consumeEnergyTotal2 ");
        sb.append("\n                                FROM disttrfmrsubstation dt, ");
        sb.append("\n                                     meter me, ");
        sb.append("\n                                     meter mt ");
        sb.append("\n                                     LEFT OUTER JOIN ");
        sb.append("\n                                     contract ct ");
        sb.append("\n                                     ON ct.meter_id = mt.id, ");
        sb.append("\n                                     billing_day_em be ");
        sb.append("\n                                WHERE dt.supplier_id = :supplierId ");
        sb.append("\n                                AND   me.disttrfmrsubstation_id = dt.id ");
        sb.append("\n                                AND   me.supplier_id = dt.supplier_id ");
        sb.append("\n                                AND   (mt.disttrfmrsubstationmeter_a_id = me.id ");
        sb.append("\n                                    OR mt.disttrfmrsubstationmeter_b_id = me.id ");
        sb.append("\n                                    OR mt.disttrfmrsubstationmeter_c_id = me.id) ");
//        sb.append("\n                                AND   be.meter_id = mt.id ");
        sb.append("\n                                AND   be.mdev_id = mt.mds_id ");
        sb.append("\n                                AND   be.mdev_type = :mdevType ");
        sb.append("\n                                AND   be.yyyymmdd = (SELECT MAX(be2.yyyymmdd) ");
        sb.append("\n                                                     FROM billing_day_em be2 ");
        sb.append("\n                                                     WHERE be2.mdev_id = be.mdev_id ");
        sb.append("\n                                                     AND   be2.mdev_type = be.mdev_type ");
        sb.append("\n                                                     AND   be2.yyyymmdd BETWEEN :searchStartDate AND :searchEndDate) ");
        sb.append("\n                                UNION ALL ");
        sb.append("\n                                SELECT dt.name AS dtsName, ");
        sb.append("\n                                       dt.id AS dtsId, ");
        sb.append("\n                                       dt.threshold, ");
        sb.append("\n                                       me.id AS meterId, ");
        sb.append("\n                                       mt.id AS contMeterId, ");
        sb.append("\n                                       0 AS consumeEnergyTotal1, ");
        sb.append("\n                                       be.activeenergyratetot AS consumeEnergyTotal2 ");
        sb.append("\n                                FROM disttrfmrsubstation dt, ");
        sb.append("\n                                     meter me, ");
        sb.append("\n                                     meter mt ");
        sb.append("\n                                     LEFT OUTER JOIN ");
        sb.append("\n                                     contract ct ");
        sb.append("\n                                     ON ct.meter_id = mt.id, ");
        sb.append("\n                                     billing_day_em be ");
        sb.append("\n                                WHERE dt.supplier_id = :supplierId ");
        sb.append("\n                                AND   me.disttrfmrsubstation_id = dt.id ");
        sb.append("\n                                AND   me.supplier_id = dt.supplier_id ");
        sb.append("\n                                AND   (mt.disttrfmrsubstationmeter_a_id = me.id ");
        sb.append("\n                                    OR mt.disttrfmrsubstationmeter_b_id = me.id ");
        sb.append("\n                                    OR mt.disttrfmrsubstationmeter_c_id = me.id) ");
//        sb.append("\n                                AND   be.meter_id = mt.id ");
        sb.append("\n                                AND   be.mdev_id = mt.mds_id ");
        sb.append("\n                                AND   be.mdev_type = :mdevType ");
        sb.append("\n                                AND   be.yyyymmdd = (SELECT MAX(be2.yyyymmdd) ");
        sb.append("\n                                                     FROM billing_day_em be2 ");
        sb.append("\n                                                     WHERE be2.mdev_id = be.mdev_id ");
        sb.append("\n                                                     AND   be2.mdev_type = be.mdev_type ");
        sb.append("\n                                                     AND   be2.yyyymmdd >= :searchPreStartDate ");
        sb.append("\n                                                     AND   be2.yyyymmdd < :searchStartDate) ");
        sb.append("\n                            ) v ");
        sb.append("\n                        GROUP BY dtsName, dtsId, threshold, meterId, contMeterId ");
        sb.append("\n                    ) w ");
        sb.append("\n                GROUP BY dtsName, dtsId, threshold, meterId ");
        sb.append("\n            ) x ");
        sb.append("\n        GROUP BY dtsName, dtsId, threshold ");
        sb.append("\n    ) x ");

        Query query = getSession().createSQLQuery(new SQLWrapper().getQuery(sb.toString()));

        query.setInteger("supplierId", supplierId);
        query.setInteger("mdevType", DeviceType.Meter.getCode());
//        query.setString("serviceType", MeterType.EnergyMeter.getServiceType());
        query.setString("searchPreStartDate", searchPreStartDate);
        query.setString("searchStartDate", searchStartDate);
        query.setString("searchEndDate", searchEndDate);

        result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        return result;
    }

    /**
     * method name : getEbsDtsTreeLocationNodeData<b/>
     * method Desc : Energy Balance Monitoring 맥스가젯의 DTS Tree 의 Location Node Data 를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getEbsDtsTreeLocationNodeData(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result;
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        Double threshold = (Double)conditionMap.get("threshold");
        String dtsName = StringUtil.nullToBlank(conditionMap.get("dtsName"));
        String searchPreStartDate = StringUtil.nullToBlank(conditionMap.get("searchPreStartDate"));
        String searchStartDate = StringUtil.nullToBlank(conditionMap.get("searchStartDate"));
        String searchEndDate = StringUtil.nullToBlank(conditionMap.get("searchEndDate"));
        String suspected = StringUtil.nullToBlank(conditionMap.get("suspected"));
        List<Integer> locationIdList = (List<Integer>)conditionMap.get("locationIdList");
        logger.info("\n" +
        		"supplierId: " + supplierId +
        		"\nthreshold: " + threshold + 
        		"\ndtsName: " + dtsName + 
        		"\nsearchPreStartDate: " + searchPreStartDate + 
        		"\nsearchStartDate: " + searchStartDate + 
        		"\nsearchEndDate: " + searchEndDate +
        		"\nsuspected: " + suspected);
        if ( locationIdList != null && locationIdList.size() > 0) {
        	logger.info("\nlocationId_size: " + locationIdList.size());
        }        
        StringBuilder sb = new StringBuilder();

        sb.append("\nSELECT locationName AS LOCATION_NAME, ");
        sb.append("\n       locationId AS LOCATION_ID, ");
        sb.append("\n       SUM(importEnergyTotal) AS IMPORT_ENERGY_TOTAL, ");
        sb.append("\n       SUM(consumeEnergyTotal) AS CONSUME_ENERGY_TOTAL ");
        sb.append("\nFROM ( ");
        sb.append("\n        SELECT locationName, ");
        sb.append("\n               locationId, ");
        sb.append("\n               dtsName, ");
        sb.append("\n               dtsId, ");
        sb.append("\n               threshold, ");
        sb.append("\n               SUM(importEnergyTotal) AS importEnergyTotal,");
        sb.append("\n               SUM(consumeEnergyTotal) AS consumeEnergyTotal ");
        sb.append("\n        FROM ( ");
        sb.append("\n                SELECT dt.name AS dtsName, ");
        sb.append("\n                       dt.id AS dtsId, ");
        sb.append("\n                       dt.threshold, ");
        sb.append("\n                       lo.name AS locationName, ");
        sb.append("\n                       lo.id AS locationId, ");
        sb.append("\n                       0 AS importEnergyTotal, ");
        sb.append("\n                       0 AS consumeEnergyTotal ");
        sb.append("\n                FROM disttrfmrsubstation dt, ");
        sb.append("\n                     location lo ");
        sb.append("\n                WHERE dt.supplier_id = :supplierId ");

        if (locationIdList != null && locationIdList.size() > 0) {
            sb.append("\n                AND   dt.location_id IN (:locationIdList) ");
        }

        if (!dtsName.isEmpty()) {
            sb.append("\n                AND   UPPER(dt.name) LIKE :dtsName ");
        }

        if (threshold != null) {
            sb.append("\n                AND   dt.threshold >= :threshold ");
        }

        sb.append("\n                AND   lo.id = dt.location_id ");
        sb.append("\n                UNION ALL ");
        sb.append("\n                SELECT dtsName, ");
        sb.append("\n                       dtsId, ");
        sb.append("\n                       threshold, ");
        sb.append("\n                       locationName, ");
        sb.append("\n                       locationId, ");
        sb.append("\n                       CASE WHEN SUM(importEnergyTotal1) = 0 THEN 0 ");
        sb.append("\n                            ELSE SUM(importEnergyTotal1) - SUM(importEnergyTotal2) END AS importEnergyTotal, ");
        sb.append("\n                       0 AS consumeEnergyTotal ");
        sb.append("\n                FROM ( ");
        sb.append("\n                        SELECT dt.name AS dtsName, ");
        sb.append("\n                               dt.id AS dtsId, ");
        sb.append("\n                               dt.threshold, ");
        sb.append("\n                               lo.id AS locationId, ");
        sb.append("\n                               lo.name AS locationName, ");
        sb.append("\n                               me.id AS meterId, ");
        sb.append("\n                               re.activeenergyimportratetotal AS importEnergyTotal1, ");
        sb.append("\n                               0 AS importEnergyTotal2 ");
        sb.append("\n                        FROM disttrfmrsubstation dt, ");
        sb.append("\n                             location lo, ");
        sb.append("\n                             meter me, ");
        sb.append("\n                             realtime_billing_em re ");
        sb.append("\n                        WHERE dt.supplier_id = :supplierId ");

        if (locationIdList != null && locationIdList.size() > 0) {
            sb.append("\n                        AND   dt.location_id IN (:locationIdList) ");
        }

        if (!dtsName.isEmpty()) {
            sb.append("\n                        AND   UPPER(dt.name) LIKE :dtsName ");
        }

        if (threshold != null) {
            sb.append("\n                        AND   dt.threshold >= :threshold ");
        }

        sb.append("\n                        and   lo.id = dt.location_id ");
        sb.append("\n                        AND   me.disttrfmrsubstation_id = dt.id ");
        sb.append("\n                        AND   me.supplier_id = dt.supplier_id ");
        sb.append("\n                        AND   re.meter_id = me.id ");
//        sb.append("\n                        AND   re.mdev_id = me.mds_id ");
//        sb.append("\n                        AND   re.mdev_type = :mdevType ");
        sb.append("\n                        AND   re.yyyymmdd = (SELECT MAX(re2.yyyymmdd) ");
        sb.append("\n                                             FROM realtime_billing_em re2 ");
        sb.append("\n                                             WHERE re2.mdev_id = re.mdev_id ");
        sb.append("\n                                             AND   re2.mdev_type = re.mdev_type ");
        sb.append("\n                                             AND   re2.yyyymmdd BETWEEN :searchStartDate AND :searchEndDate) ");
        sb.append("\n                        AND   re.hhmmss = (SELECT MAX(sr.hhmmss) ");
        sb.append("\n                                           FROM realtime_billing_em sr ");
        sb.append("\n                                           WHERE sr.mdev_id = re.mdev_id ");
        sb.append("\n                                           AND   sr.mdev_type = re.mdev_type ");
        sb.append("\n                                           AND   sr.yyyymmdd = re.yyyymmdd) ");
        sb.append("\n                        UNION ALL ");
        sb.append("\n                        SELECT dt.name AS dtsName, ");
        sb.append("\n                               dt.id AS dtsId, ");
        sb.append("\n                               dt.threshold, ");
        sb.append("\n                               lo.id AS locationId, ");
        sb.append("\n                               lo.name AS locationName, ");
        sb.append("\n                               me.id AS meterId, ");
        sb.append("\n                               0 AS importEnergyTotal1, ");
        sb.append("\n                               re.activeenergyimportratetotal AS importEnergyTotal2 ");
        sb.append("\n                        FROM disttrfmrsubstation dt, ");
        sb.append("\n                             location lo, ");
        sb.append("\n                             meter me, ");
        sb.append("\n                             realtime_billing_em re ");
        sb.append("\n                        WHERE dt.supplier_id = :supplierId ");

        if (locationIdList != null && locationIdList.size() > 0) {
            sb.append("\n                        AND   dt.location_id IN (:locationIdList) ");
        }

        if (!dtsName.isEmpty()) {
            sb.append("\n                        AND   UPPER(dt.name) LIKE :dtsName ");
        }

        if (threshold != null) {
            sb.append("\n                        AND   dt.threshold >= :threshold ");
        }

        sb.append("\n                        and   lo.id = dt.location_id ");
        sb.append("\n                        AND   me.disttrfmrsubstation_id = dt.id ");
        sb.append("\n                        AND   me.supplier_id = dt.supplier_id ");
        sb.append("\n                        AND   re.meter_id = me.id ");
//        sb.append("\n                        AND   re.mdev_id = me.mds_id ");
//        sb.append("\n                        AND   re.mdev_type = :mdevType ");
        sb.append("\n                        AND   re.yyyymmdd = (SELECT MAX(re2.yyyymmdd) ");
        sb.append("\n                                             FROM realtime_billing_em re2 ");
        sb.append("\n                                             WHERE re2.mdev_id = re.mdev_id ");
        sb.append("\n                                             AND   re2.mdev_type = re.mdev_type ");
        sb.append("\n                                             AND   re2.yyyymmdd >= :searchPreStartDate ");
        sb.append("\n                                             AND   re2.yyyymmdd < :searchStartDate) ");
        sb.append("\n                        AND   re.hhmmss = (SELECT MAX(sr.hhmmss) ");
        sb.append("\n                                           FROM realtime_billing_em sr ");
        sb.append("\n                                           WHERE sr.mdev_id = re.mdev_id ");
        sb.append("\n                                           AND   sr.mdev_type = re.mdev_type ");
        sb.append("\n                                           AND   sr.yyyymmdd = re.yyyymmdd) ");
        sb.append("\n                    ) x ");
        sb.append("\n                GROUP BY locationName, locationId, dtsName, dtsId, threshold, meterId ");
        sb.append("\n                UNION ALL ");
        sb.append("\n                SELECT dtsName, ");
        sb.append("\n                       dtsId, ");
        sb.append("\n                       threshold, ");
        sb.append("\n                       locationName, ");
        sb.append("\n                       locationId, ");
        sb.append("\n                       0 AS importEnergyTotal, ");
        sb.append("\n                       SUM(consumeEnergyTotal) AS consumeEnergyTotal ");
        sb.append("\n                FROM ( ");
        sb.append("\n                        SELECT dtsName, ");
        sb.append("\n                               dtsId, ");
        sb.append("\n                               threshold, ");
        sb.append("\n                               locationName, ");
        sb.append("\n                               locationId, ");
        sb.append("\n                               meterId, ");
        sb.append("\n                               contractId, ");
        sb.append("\n                               SUM(consumeEnergyTotal1) AS consumeEnergyTotal1, ");
        sb.append("\n                               SUM(consumeEnergyTotal2) AS consumeEnergyTotal2, ");
        sb.append("\n                               CASE WHEN SUM(consumeEnergyTotal1) = 0 THEN 0 ");
        sb.append("\n                                    ELSE SUM(consumeEnergyTotal1) - SUM(consumeEnergyTotal2) END AS consumeEnergyTotal ");
        sb.append("\n                        FROM ( ");
        sb.append("\n                                SELECT dt.name AS dtsName, ");
        sb.append("\n                                       dt.id AS dtsId, ");
        sb.append("\n                                       dt.threshold, ");
        sb.append("\n                                       lo.id AS locationId, ");
        sb.append("\n                                       lo.name AS locationName, ");
        sb.append("\n                                       me.id AS meterId, ");
        sb.append("\n                                       ct.id AS contractId, ");
        sb.append("\n                                       be.activeenergyratetot AS consumeEnergyTotal1, ");
        sb.append("\n                                       0 AS consumeEnergyTotal2 ");
        sb.append("\n                                FROM disttrfmrsubstation dt, ");
        sb.append("\n                                     location lo, ");
        sb.append("\n                                     meter me, ");
        sb.append("\n                                     meter mt ");
        sb.append("\n                                     LEFT OUTER JOIN ");
        sb.append("\n                                     contract ct ");
        sb.append("\n                                     ON ct.meter_id = mt.id, ");
        sb.append("\n                                     code cd, ");
        sb.append("\n                                     billing_day_em be ");
        sb.append("\n                                WHERE dt.supplier_id = :supplierId ");

        if (locationIdList != null && locationIdList.size() > 0) {
            sb.append("\n                                AND   dt.location_id IN (:locationIdList) ");
        }

        if (!dtsName.isEmpty()) {
            sb.append("\n                                AND   UPPER(dt.name) LIKE :dtsName ");
        }

        if (threshold != null) {
            sb.append("\n                                AND   dt.threshold >= :threshold ");
        }

        sb.append("\n                                AND   lo.id = dt.location_id ");
        sb.append("\n                                AND   me.disttrfmrsubstation_id = dt.id ");
        sb.append("\n                                AND   me.supplier_id = dt.supplier_id ");
        sb.append("\n                                AND   (mt.disttrfmrsubstationmeter_a_id = me.id ");
        sb.append("\n                                    OR mt.disttrfmrsubstationmeter_b_id = me.id ");
        sb.append("\n                                    OR mt.disttrfmrsubstationmeter_c_id = me.id) ");
        sb.append("\n                                AND   mt.supplier_id = me.supplier_id ");
        sb.append("\n                                AND   cd.id = mt.metertype_id ");
        sb.append("\n                                AND   cd.code = '1.3.1.1' ");
//        sb.append("\n                                AND   be.meter_id = mt.id ");
        sb.append("\n                                AND   be.mdev_id = mt.mds_id ");
        sb.append("\n                                AND   be.mdev_type = :mdevType ");
        sb.append("\n                                AND   be.yyyymmdd = (SELECT MAX(be2.yyyymmdd) ");
        sb.append("\n                                                     FROM billing_day_em be2 ");
        sb.append("\n                                                     WHERE be2.mdev_id = be.mdev_id ");
        sb.append("\n                                                     AND   be2.mdev_type = be.mdev_type ");
        sb.append("\n                                                     AND   be2.yyyymmdd BETWEEN :searchStartDate AND :searchEndDate) ");
        sb.append("\n                                UNION ALL ");
        sb.append("\n                                SELECT dt.name AS dtsName, ");
        sb.append("\n                                       dt.id AS dtsId, ");
        sb.append("\n                                       dt.threshold, ");
        sb.append("\n                                       lo.id AS locationId, ");
        sb.append("\n                                       lo.name AS locationName, ");
        sb.append("\n                                       me.id AS meterId, ");
        sb.append("\n                                       ct.id AS contractId, ");
        sb.append("\n                                       0 AS consumeEnergyTotal1, ");
        sb.append("\n                                       be.activeenergyratetot AS consumeEnergyTotal2 ");
        sb.append("\n                                FROM disttrfmrsubstation dt, ");
        sb.append("\n                                     location lo, ");
        sb.append("\n                                     meter me, ");
        sb.append("\n                                     meter mt ");
        sb.append("\n                                     LEFT OUTER JOIN ");
        sb.append("\n                                     contract ct ");
        sb.append("\n                                     ON ct.meter_id = mt.id, ");
        sb.append("\n                                     code cd, ");
        sb.append("\n                                     billing_day_em be ");
        sb.append("\n                                WHERE dt.supplier_id = :supplierId ");

        if (locationIdList != null && locationIdList.size() > 0) {
            sb.append("\n                                AND   dt.location_id IN (:locationIdList) ");
        }

        if (!dtsName.isEmpty()) {
            sb.append("\n                                AND   UPPER(dt.name) LIKE :dtsName ");
        }

        if (threshold != null) {
            sb.append("\n                                AND   dt.threshold >= :threshold ");
        }

        sb.append("\n                                and   lo.id = dt.location_id ");
        sb.append("\n                                AND   me.disttrfmrsubstation_id = dt.id ");
        sb.append("\n                                AND   me.supplier_id = dt.supplier_id ");
        sb.append("\n                                AND   (mt.disttrfmrsubstationmeter_a_id = me.id ");
        sb.append("\n                                    OR mt.disttrfmrsubstationmeter_b_id = me.id ");
        sb.append("\n                                    OR mt.disttrfmrsubstationmeter_c_id = me.id) ");
        sb.append("\n                                AND   mt.supplier_id = me.supplier_id ");
        sb.append("\n                                AND   cd.id = mt.metertype_id ");
        sb.append("\n                                AND   cd.code = '1.3.1.1' ");
//        sb.append("\n                                AND   be.meter_id = mt.id ");
        sb.append("\n                                AND   be.mdev_id = mt.mds_id ");
        sb.append("\n                                AND   be.mdev_type = :mdevType ");
        sb.append("\n                                AND   be.yyyymmdd = (SELECT MAX(be2.yyyymmdd) ");
        sb.append("\n                                                     FROM billing_day_em be2 ");
        sb.append("\n                                                     WHERE be2.mdev_id = be.mdev_id ");
        sb.append("\n                                                     AND   be2.mdev_type = be.mdev_type ");
        sb.append("\n                                                     AND   be2.yyyymmdd >= :searchPreStartDate ");
        sb.append("\n                                                     AND   be2.yyyymmdd < :searchStartDate) ");
        sb.append("\n                            ) v ");
        sb.append("\n                        GROUP BY locationName, locationId, dtsName, dtsId, threshold, meterId, contractId ");
        sb.append("\n                    ) w ");
        sb.append("\n                GROUP BY locationName, locationId, dtsName, dtsId, threshold, meterId ");
        sb.append("\n            ) x ");
        sb.append("\n        GROUP BY locationName, locationId, dtsName, dtsId, threshold ");
        sb.append("\n    ) y ");

        if (!suspected.isEmpty()) {
            sb.append("\nWHERE importEnergyTotal > 0 ");
            sb.append("\nAND  case when importEnergyTotal = 0 ");
            sb.append("\nTHEN 0 "); 
            sb.append("\nELSE ((importEnergyTotal - consumeEnergyTotal) / importEnergyTotal) ");
            sb.append("\nEND > (threshold / 100) ");
        }

        sb.append("\nGROUP BY locationName, locationId ");

        Query query = getSession().createSQLQuery(new SQLWrapper().getQuery(sb.toString()));
        query.setInteger("supplierId", supplierId);
        query.setString("searchPreStartDate", searchPreStartDate);
        query.setString("searchStartDate", searchStartDate);
        query.setString("searchEndDate", searchEndDate);
        query.setInteger("mdevType", DeviceType.Meter.getCode());

        if (locationIdList != null && locationIdList.size() > 0) {
            query.setParameterList("locationIdList", locationIdList);
        }

        if (!dtsName.isEmpty()) {
            query.setString("dtsName", "%" + dtsName.toUpperCase() + "%");
        }

        if (threshold != null) {
            query.setDouble("threshold", threshold);
        }

        result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

        return result;
    }

    /**
     * method name : getEbsDtsTreeDtsNodeData<b/>
     * method Desc : Energy Balance Monitoring 맥스가젯의 DTS Tree 의 DTS Node Data 를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getEbsDtsTreeDtsNodeData(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result;
        Integer supplierId = (Integer)conditionMap.get("supplierId");
//        Integer locationId = (Integer)conditionMap.get("dtsLocationId");
        Double threshold = (Double)conditionMap.get("threshold");
        String dtsName = StringUtil.nullToBlank(conditionMap.get("dtsName"));
        String searchPreStartDate = StringUtil.nullToBlank(conditionMap.get("searchPreStartDate"));
        String searchStartDate = StringUtil.nullToBlank(conditionMap.get("searchStartDate"));
        String searchEndDate = StringUtil.nullToBlank(conditionMap.get("searchEndDate"));
        String suspected = StringUtil.nullToBlank(conditionMap.get("suspected"));
        List<Integer> locationIdList = (List<Integer>)conditionMap.get("locationIdList");
        
        logger.info("\n" +
        		"supplierId: " + supplierId +
        		"\nthreshold: " + threshold + 
        		"\ndtsName: " + dtsName + 
        		"\nsearchPreStartDate: " + searchPreStartDate + 
        		"\nsearchStartDate: " + searchStartDate + 
        		"\nsearchEndDate: " + searchEndDate +
        		"\nsuspected: " + suspected ); 
        if ( locationIdList != null && locationIdList.size() > 0) {
        	
        	logger.info("\nlocationId_size: " + locationIdList.size());
        }
        StringBuilder sb = new StringBuilder();

        sb.append("\nSELECT dtsName AS DTS_NAME, ");
        sb.append("\n       dtsId AS DTS_ID, ");
        sb.append("\n       threshold AS THRESHOLD, ");
        sb.append("\n       location_id AS LOCATION_ID, ");
        sb.append("\n       SUM(importEnergyTotal) AS IMPORT_ENERGY_TOTAL, ");
        sb.append("\n       SUM(consumeEnergyTotal) AS CONSUME_ENERGY_TOTAL ");
        sb.append("\nFROM ( ");
        sb.append("\n        SELECT dt.name AS dtsName, ");
        sb.append("\n               dt.id AS dtsId, ");
        sb.append("\n               dt.threshold, ");
        sb.append("\n               dt.location_id, ");
        sb.append("\n               0 AS importEnergyTotal, ");
        sb.append("\n               0 AS consumeEnergyTotal ");
        sb.append("\n        FROM disttrfmrsubstation dt ");
        sb.append("\n        WHERE dt.supplier_id = :supplierId ");

        if (locationIdList != null && locationIdList.size() > 0) {
            sb.append("\n        AND   dt.location_id IN (:locationIdList) ");
        }

        if (!dtsName.isEmpty()) {
            sb.append("\n        AND   UPPER(dt.name) LIKE :dtsName ");
        }

        if (threshold != null) {
            sb.append("\n        AND   dt.threshold >= :threshold ");
        }

        sb.append("\n        UNION ALL ");
        sb.append("\n        SELECT dtsName, ");
        sb.append("\n               dtsId, ");
        sb.append("\n               threshold, ");
        sb.append("\n               location_id, ");
        sb.append("\n               CASE WHEN SUM(importEnergyTotal1) = 0 THEN 0 ");
        sb.append("\n                    ELSE SUM(importEnergyTotal1) - SUM(importEnergyTotal2) END AS importEnergyTotal, ");
        sb.append("\n               0 AS consumeEnergyTotal ");
        sb.append("\n        FROM ( ");
        sb.append("\n                SELECT dt.name AS dtsName, ");
        sb.append("\n                       dt.id AS dtsId, ");
        sb.append("\n                       dt.threshold, ");
        sb.append("\n                       dt.location_id, ");
        sb.append("\n                       me.id AS meterId, ");
        sb.append("\n                       re.activeenergyimportratetotal AS importEnergyTotal1, ");
        sb.append("\n                       0 AS importEnergyTotal2 ");
        sb.append("\n                FROM disttrfmrsubstation dt, ");
        sb.append("\n                     meter me, ");
        sb.append("\n                     realtime_billing_em re ");
        sb.append("\n                WHERE dt.supplier_id = :supplierId ");

        if (locationIdList != null && locationIdList.size() > 0) {
            sb.append("\n                AND   dt.location_id IN (:locationIdList) ");
        }

        if (!dtsName.isEmpty()) {
            sb.append("\n                AND   UPPER(dt.name) LIKE :dtsName ");
        }

        if (threshold != null) {
            sb.append("\n                AND   dt.threshold >= :threshold ");
        }

        sb.append("\n                AND   me.disttrfmrsubstation_id = dt.id ");
        sb.append("\n                AND   me.supplier_id = dt.supplier_id ");
        sb.append("\n                AND   re.meter_id = me.id ");
//        sb.append("\n                AND   re.mdev_id = me.mds_id ");
//        sb.append("\n                AND   re.mdev_type = :mdevType ");
//        sb.append("\n                AND   re.yyyymmdd BETWEEN :searchStartDate AND :searchEndDate ");
        sb.append("\n                AND   re.yyyymmdd = (SELECT MAX(re2.yyyymmdd) ");
        sb.append("\n                                     FROM realtime_billing_em re2 ");
        sb.append("\n                                     WHERE re2.mdev_id = re.mdev_id ");
        sb.append("\n                                     AND   re2.mdev_type = re.mdev_type ");
        sb.append("\n                                     AND   re2.yyyymmdd BETWEEN :searchStartDate AND :searchEndDate) ");
        sb.append("\n                AND   re.hhmmss = (SELECT MAX(sr.hhmmss) ");
        sb.append("\n                                   FROM realtime_billing_em sr ");
        sb.append("\n                                   WHERE sr.mdev_id = re.mdev_id ");
        sb.append("\n                                   AND   sr.mdev_type = re.mdev_type ");
        sb.append("\n                                   AND   sr.yyyymmdd = re.yyyymmdd) ");
        sb.append("\n                UNION ALL ");
        sb.append("\n                SELECT dt.name AS dtsName, ");
        sb.append("\n                       dt.id AS dtsId, ");
        sb.append("\n                       dt.threshold, ");
        sb.append("\n                       dt.location_id, ");
        sb.append("\n                       me.id AS meterId, ");
        sb.append("\n                       0 AS importEnergyTotal1, ");
        sb.append("\n                       re.activeenergyimportratetotal AS importEnergyTotal2 ");
        sb.append("\n                FROM disttrfmrsubstation dt, ");
        sb.append("\n                     meter me, ");
        sb.append("\n                     realtime_billing_em re ");
        sb.append("\n                WHERE dt.supplier_id = :supplierId ");

        if (locationIdList != null && locationIdList.size() > 0) {
            sb.append("\n                AND   dt.location_id IN (:locationIdList) ");
        }

        if (!dtsName.isEmpty()) {
            sb.append("\n                AND   UPPER(dt.name) LIKE :dtsName ");
        }

        if (threshold != null) {
            sb.append("\n                AND   dt.threshold >= :threshold ");
        }

        sb.append("\n                AND   me.disttrfmrsubstation_id = dt.id ");
        sb.append("\n                AND   me.supplier_id = dt.supplier_id ");
        sb.append("\n                AND   re.meter_id = me.id ");
//        sb.append("\n                AND   re.mdev_id = me.mds_id ");
//        sb.append("\n                AND   re.mdev_type = :mdevType ");
//        sb.append("\n                AND   re.yyyymmdd >= :searchPreStartDate ");
//        sb.append("\n                AND   re.yyyymmdd < :searchStartDate ");
        sb.append("\n                AND   re.yyyymmdd = (SELECT MAX(re2.yyyymmdd) ");
        sb.append("\n                                     FROM realtime_billing_em re2 ");
        sb.append("\n                                     WHERE re2.mdev_id = re.mdev_id ");
        sb.append("\n                                     AND   re2.mdev_type = re.mdev_type ");
        sb.append("\n                                     AND   re2.yyyymmdd >= :searchPreStartDate ");
        sb.append("\n                                     AND   re2.yyyymmdd < :searchStartDate) ");
        sb.append("\n                AND   re.hhmmss = (SELECT MAX(sr.hhmmss) ");
        sb.append("\n                                   FROM realtime_billing_em sr ");
        sb.append("\n                                   WHERE sr.mdev_id = re.mdev_id ");
        sb.append("\n                                   AND   sr.mdev_type = re.mdev_type ");
        sb.append("\n                                   AND   sr.yyyymmdd = re.yyyymmdd) ");
        sb.append("\n            ) x ");
        sb.append("\n        GROUP BY dtsName, dtsId, threshold, location_id, meterId ");
        sb.append("\n        UNION ALL ");
        sb.append("\n        SELECT dtsName, ");
        sb.append("\n               dtsId, ");
        sb.append("\n               threshold, ");
        sb.append("\n               location_id, ");
        sb.append("\n               0 AS importEnergyTotal, ");
        sb.append("\n               SUM(consumeEnergyTotal) AS consumeEnergyTotal ");
        sb.append("\n        FROM ( ");
        sb.append("\n                SELECT dtsName, ");
        sb.append("\n                       dtsId, ");
        sb.append("\n                       threshold, ");
        sb.append("\n                       location_id, ");
        sb.append("\n                       meterId, ");
        sb.append("\n                       contractId, ");
        sb.append("\n                       SUM(consumeEnergyTotal1) AS consumeEnergyTotal1, ");
        sb.append("\n                       SUM(consumeEnergyTotal2) AS consumeEnergyTotal2, ");
        sb.append("\n                       CASE WHEN SUM(consumeEnergyTotal1) = 0 THEN 0 ");
        sb.append("\n                            ELSE SUM(consumeEnergyTotal1) - SUM(consumeEnergyTotal2) END AS consumeEnergyTotal ");
        sb.append("\n                FROM ( ");
        sb.append("\n                        SELECT dt.name AS dtsName, ");
        sb.append("\n                               dt.id AS dtsId, ");
        sb.append("\n                               dt.threshold, ");
        sb.append("\n                               dt.location_id, ");
        sb.append("\n                               me.id AS meterId, ");
        sb.append("\n                               ct.id AS contractId, ");
        sb.append("\n                               be.activeenergyratetot AS consumeEnergyTotal1, ");
        sb.append("\n                               0 AS consumeEnergyTotal2 ");
        sb.append("\n                        FROM disttrfmrsubstation dt, ");
        sb.append("\n                             meter me, ");
        sb.append("\n                             meter mt ");
        sb.append("\n                             LEFT OUTER JOIN ");
        sb.append("\n                             contract ct ");
        sb.append("\n                             ON ct.meter_id = mt.id, ");
        sb.append("\n                             code cd, ");
        sb.append("\n                             billing_day_em be ");
        sb.append("\n                        WHERE dt.supplier_id = :supplierId ");

        if (locationIdList != null && locationIdList.size() > 0) {
            sb.append("\n                        AND   dt.location_id IN (:locationIdList) ");
        }

        if (!dtsName.isEmpty()) {
            sb.append("\n                        AND   UPPER(dt.name) LIKE :dtsName ");
        }

        if (threshold != null) {
            sb.append("\n                        AND   dt.threshold >= :threshold ");
        }

        sb.append("\n                        AND   me.disttrfmrsubstation_id = dt.id ");
        sb.append("\n                        AND   me.supplier_id = dt.supplier_id ");
        sb.append("\n                        AND   (mt.disttrfmrsubstationmeter_a_id = me.id ");
        sb.append("\n                            OR mt.disttrfmrsubstationmeter_b_id = me.id ");
        sb.append("\n                            OR mt.disttrfmrsubstationmeter_c_id = me.id) ");
        sb.append("\n                        AND   mt.supplier_id = me.supplier_id ");
        sb.append("\n                        AND   cd.id = mt.metertype_id ");
        sb.append("\n                        AND   cd.code = '1.3.1.1' ");
//        sb.append("\n                        AND   be.meter_id = mt.id ");
        sb.append("\n                        AND   be.mdev_id = mt.mds_id ");
        sb.append("\n                        AND   be.mdev_type = :mdevType ");
//        sb.append("\n                        AND   be.yyyymmdd BETWEEN :searchStartDate AND :searchEndDate ");
        sb.append("\n                        AND   be.yyyymmdd = (SELECT MAX(be2.yyyymmdd) ");
        sb.append("\n                                             FROM billing_day_em be2 ");
        sb.append("\n                                             WHERE be2.mdev_id = be.mdev_id ");
        sb.append("\n                                             AND   be2.mdev_type = be.mdev_type ");
        sb.append("\n                                             AND   be2.yyyymmdd BETWEEN :searchStartDate AND :searchEndDate) ");
        sb.append("\n                        UNION ALL ");
        sb.append("\n                        SELECT dt.name AS dtsName, ");
        sb.append("\n                               dt.id AS dtsId, ");
        sb.append("\n                               dt.threshold, ");
        sb.append("\n                               dt.location_id, ");
        sb.append("\n                               me.id AS meterId, ");
        sb.append("\n                               ct.id AS contractId, ");
        sb.append("\n                               0 AS consumeEnergyTotal1, ");
        sb.append("\n                               be.activeenergyratetot AS consumeEnergyTotal2 ");
        sb.append("\n                        FROM disttrfmrsubstation dt, ");
        sb.append("\n                             meter me, ");
        sb.append("\n                             meter mt ");
        sb.append("\n                             LEFT OUTER JOIN ");
        sb.append("\n                             contract ct ");
        sb.append("\n                             ON ct.meter_id = mt.id, ");
        sb.append("\n                             code cd, ");
        sb.append("\n                             billing_day_em be ");
        sb.append("\n                        WHERE dt.supplier_id = :supplierId ");

        if (locationIdList != null && locationIdList.size() > 0) {
            sb.append("\n                        AND   dt.location_id IN (:locationIdList) ");
        }

        if (!dtsName.isEmpty()) {
            sb.append("\n                        AND   UPPER(dt.name) LIKE :dtsName ");
        }

        if (threshold != null) {
            sb.append("\n                        AND   dt.threshold >= :threshold ");
        }

        sb.append("\n                        AND   me.disttrfmrsubstation_id = dt.id ");
        sb.append("\n                        AND   me.supplier_id = dt.supplier_id ");
        sb.append("\n                        AND   (mt.disttrfmrsubstationmeter_a_id = me.id ");
        sb.append("\n                            OR mt.disttrfmrsubstationmeter_b_id = me.id ");
        sb.append("\n                            OR mt.disttrfmrsubstationmeter_c_id = me.id) ");
        sb.append("\n                        AND   mt.supplier_id = me.supplier_id ");
        sb.append("\n                        AND   cd.id = mt.metertype_id ");
        sb.append("\n                        AND   cd.code = '1.3.1.1' ");
//        sb.append("\n                        AND   be.meter_id = mt.id ");
        sb.append("\n                        AND   be.mdev_id = mt.mds_id ");
        sb.append("\n                        AND   be.mdev_type = :mdevType ");
//        sb.append("\n                        AND   be.yyyymmdd >= :searchPreStartDate ");
//        sb.append("\n                        AND   be.yyyymmdd < :searchStartDate ");
        sb.append("\n                        AND   be.yyyymmdd = (SELECT MAX(be2.yyyymmdd) ");
        sb.append("\n                                             FROM billing_day_em be2 ");
        sb.append("\n                                             WHERE be2.mdev_id = be.mdev_id ");
        sb.append("\n                                             AND   be2.mdev_type = be.mdev_type ");
        sb.append("\n                                             AND   be2.yyyymmdd >= :searchPreStartDate ");
        sb.append("\n                                             AND   be2.yyyymmdd < :searchStartDate) ");
        sb.append("\n                    ) v ");
        sb.append("\n                GROUP BY dtsName, dtsId, threshold, location_id, meterId, contractId ");
        sb.append("\n            ) w ");
        sb.append("\n        GROUP BY dtsName, dtsId, threshold, location_id, meterId ");
        sb.append("\n    ) x ");
        sb.append("\nGROUP BY location_id, dtsName, dtsId, threshold ");

        if (!suspected.isEmpty()) {
            sb.append("\nHAVING SUM(importEnergyTotal) > 0 ");
//            sb.append("\nAND    Coalesce(SUM(importEnergyTotal), 0, 0, ((SUM(importEnergyTotal) - SUM(consumeEnergyTotal)) / SUM(importEnergyTotal)))  > (threshold / 100) ");
            sb.append("\nAND    case when SUM(importEnergyTotal) = 0 then 0" );
            sb.append("\n else ((SUM(importEnergyTotal) - SUM(consumeEnergyTotal)) / SUM(importEnergyTotal)) end  > (threshold / 100) ");
        }

        SQLQuery query = getSession().createSQLQuery(new SQLWrapper().getQuery(sb.toString()));
        query.setInteger("supplierId", supplierId);
//        query.setInteger("locationId", locationId);
        query.setInteger("mdevType", DeviceType.Meter.getCode());
        query.setString("searchPreStartDate", searchPreStartDate);
        query.setString("searchStartDate", searchStartDate);
        query.setString("searchEndDate", searchEndDate);
//        query.setString("meterType", MeterType.EnergyMeter.name());

        if (locationIdList != null && locationIdList.size() > 0) {
            query.setParameterList("locationIdList", locationIdList);
        }

        if (!dtsName.isEmpty()) {
            query.setString("dtsName", "%" + dtsName.toUpperCase() + "%");
        }

        if (threshold != null) {
            query.setDouble("threshold", threshold);
        }

        result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        return result;
    }



    /**
     * method name : getEbsDtsDupCount<b/>
     * method Desc : Energy Balance Monitoring 에서 DTS Name 중복개수를 조회한다. 
     *
     * @param conditionMap
     * @return
     */
    public Integer getEbsDtsDupCount(Map<String, Object> conditionMap) {
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        String dtsName = StringUtil.nullToBlank(conditionMap.get("dtsName"));

        StringBuilder sb = new StringBuilder();

        sb.append("\n SELECT COUNT(*) AS cnt ");
        sb.append("\n FROM DistTrfmrSubstation dt ");
        sb.append("\n WHERE dt.supplier.id = :supplierId ");
        sb.append("\n AND   dt.name = :dtsName ");

        Query query = getSession().createQuery(sb.toString());
        query.setInteger("supplierId", supplierId);
        query.setString("dtsName", dtsName);

        Number count = (Number)query.uniqueResult();

        return count.intValue();
    }

    /**
     * method name : getEbsDtsChartImportData<b/>
     * method Desc : Energy Balance Monitoring 맥스가젯의 DTS Tree Chart 의 Import Energy Data 를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getEbsDtsChartImportData(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result;
        Integer dtsId = (Integer)conditionMap.get("dtsId");
        Integer meterId = (Integer)conditionMap.get("meterId");
        Integer phaseId = (Integer)conditionMap.get("phaseId");
        Integer depth = (Integer)conditionMap.get("depth");
        String searchPreStartDate = StringUtil.nullToBlank(conditionMap.get("searchPreStartDate"));
        String searchEndDate = StringUtil.nullToBlank(conditionMap.get("searchEndDate"));

        StringBuilder sb = new StringBuilder();

        sb.append("\nSELECT re.yyyymmdd AS YYYYMMDD, ");
        sb.append("\n       dt.id AS DTS_ID, ");
        sb.append("\n       dt.name AS DTS_NAME, ");
        sb.append("\n       me.id AS METER_ID, ");
        sb.append("\n       me.mds_id AS MDS_ID, ");

        switch(depth) {
            case 2: // DTS
                
            case 3: // Meter
                sb.append("\n       COALESCE(re.activeenergyimportratetotal, 0) AS ENERGY_SUM ");
                break;

            case 4: // phase
                for (DistTrfmrSubstationMeterPhase obj : DistTrfmrSubstationMeterPhase.values()) {
                    if (obj.getCode().equals(phaseId)) {
                        switch(obj) {
                            case LINE_A:
                                sb.append("\n       COALESCE(re.importkwhphasea, 0) AS ENERGY_SUM ");
                                break;
                                
                            case LINE_B:
                                sb.append("\n       COALESCE(re.importkwhphaseb, 0) AS ENERGY_SUM ");
                                break;

                            case LINE_C:
                                sb.append("\n       COALESCE(re.importkwhphasec, 0) AS ENERGY_SUM ");
                                break;
                        }
                    }
                }
                break;
        }

        sb.append("\nFROM disttrfmrsubstation dt, ");
        sb.append("\n     meter me, ");
        sb.append("\n     realtime_billing_em re ");
        sb.append("\nWHERE 1=1 ");

        switch(depth) {
            case 2: // DTS
                sb.append("\nAND   dt.id = :dtsId ");
                sb.append("\nAND   me.disttrfmrsubstation_id = dt.id ");
                sb.append("\nAND   me.supplier_id = dt.supplier_id ");
                break;

            case 3: // Meter

            case 4: // phase
                sb.append("\nAND   me.id = :meterId ");
                sb.append("\nAND   dt.id = me.disttrfmrsubstation_id ");
                break;
        }

        sb.append("\nAND   re.meter_id = me.id ");
//        sb.append("\nAND   re.mdev_id = me.mds_id ");
//        sb.append("\nAND   re.mdev_type = :mdevType ");
        sb.append("\nAND   re.yyyymmdd BETWEEN :searchPreStartDate AND :searchEndDate ");
        sb.append("\nAND   re.hhmmss = (SELECT MAX(sr.hhmmss) ");
        sb.append("\n                   FROM realtime_billing_em sr ");
        sb.append("\n                   WHERE sr.mdev_id = re.mdev_id ");
        sb.append("\n                   AND   sr.mdev_type = re.mdev_type ");
        sb.append("\n                   AND   sr.yyyymmdd = re.yyyymmdd) ");
        sb.append("\nORDER BY re.yyyymmdd ");

        Query query = getSession().createSQLQuery(new SQLWrapper().getQuery(sb.toString()));

        query.setString("searchPreStartDate", searchPreStartDate);
        query.setString("searchEndDate", searchEndDate);
//        query.setInteger("mdevType", DeviceType.Meter.getCode());

        switch(depth) {
            case 2: // DTS
                query.setInteger("dtsId", dtsId);
                break;

            case 3: // Meter

            case 4: // phase
                query.setInteger("meterId", meterId);
                break;
        }
        
        result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

        return result;
    }

    /**
     * method name : getEbsExportExcelData<b/>
     * method Desc : Energy Balance Monitoring 맥스가젯의 DTS Tree 의 Export Excel Data 를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getEbsExportExcelData(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result;
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        Double threshold = (Double)conditionMap.get("threshold");
        String dtsName = StringUtil.nullToBlank(conditionMap.get("dtsName"));
        String searchPreStartDate = StringUtil.nullToBlank(conditionMap.get("searchPreStartDate"));
        String searchStartDate = StringUtil.nullToBlank(conditionMap.get("searchStartDate"));
        String searchEndDate = StringUtil.nullToBlank(conditionMap.get("searchEndDate"));
        Boolean suspected = (Boolean)conditionMap.get("suspected");
        List<Integer> locationIdList = (List<Integer>)conditionMap.get("locationIdList");

        StringBuilder sb = new StringBuilder();

        sb.append("\nSELECT lo.name AS LOCATION_NAME, ");
        sb.append("\n       dt.dtsName AS DTS_NAME, ");
        sb.append("\n       dt.threshold AS THRESHOLD, ");
        sb.append("\n       dt.importEnergyTotal AS IMPORT_ENERGY_TOTAL, ");
//        sb.append("\n       CASE WHEN threshold IS NULL THEN importEnergyTotal ");
//        sb.append("\n            ELSE (importEnergyTotal * ((100 - threshold)/100)) END AS TOL_ENERGY_TOTAL, ");
        sb.append("\n       dt.consumeEnergyTotal AS CONSUME_ENERGY_TOTAL ");
        sb.append("\nFROM ( ");
        sb.append("\n        SELECT dtsName, ");
        sb.append("\n               dtsId, ");
        sb.append("\n               threshold, ");
        sb.append("\n               location_id, ");
        sb.append("\n               SUM(importEnergyTotal) AS importEnergyTotal, ");
        sb.append("\n               SUM(consumeEnergyTotal) AS consumeEnergyTotal ");
        sb.append("\n        FROM ( ");
        sb.append("\n                SELECT dt.name AS dtsName, ");
        sb.append("\n                       dt.id AS dtsId, ");
        sb.append("\n                       dt.threshold, ");
        sb.append("\n                       dt.location_id, ");
        sb.append("\n                       0 AS importEnergyTotal, ");
        sb.append("\n                       0 AS consumeEnergyTotal ");
        sb.append("\n                FROM disttrfmrsubstation dt ");
        sb.append("\n                WHERE dt.supplier_id = :supplierId ");

        if (locationIdList != null && locationIdList.size() > 0) {
            sb.append("\n                AND   dt.location_id IN (:locationIdList) ");
        }

        if (!dtsName.isEmpty()) {
            sb.append("\n                AND   UPPER(dt.name) LIKE :dtsName ");
        }

        if (threshold != null) {
            sb.append("\n                AND   dt.threshold >= :threshold ");
        }

        sb.append("\n                UNION ALL ");
        sb.append("\n                SELECT dtsName, ");
        sb.append("\n                       dtsId, ");
        sb.append("\n                       threshold, ");
        sb.append("\n                       location_id, ");
        sb.append("\n                       CASE WHEN SUM(importEnergyTotal1) = 0 THEN 0 ");
        sb.append("\n                            ELSE SUM(importEnergyTotal1) - SUM(importEnergyTotal2) END AS importEnergyTotal, ");
        sb.append("\n                       0 AS consumeEnergyTotal ");
        sb.append("\n                FROM ( ");
        sb.append("\n                        SELECT dt.name AS dtsName, ");
        sb.append("\n                               dt.id AS dtsId, ");
        sb.append("\n                               dt.threshold, ");
        sb.append("\n                               dt.location_id, ");
        sb.append("\n                               me.id AS meterId, ");
        sb.append("\n                               re.activeenergyimportratetotal AS importEnergyTotal1, ");
        sb.append("\n                               0 AS importEnergyTotal2 ");
        sb.append("\n                        FROM disttrfmrsubstation dt, ");
        sb.append("\n                             meter me, ");
        sb.append("\n                             realtime_billing_em re ");
        sb.append("\n                        WHERE dt.supplier_id = :supplierId ");

        if (locationIdList != null && locationIdList.size() > 0) {
            sb.append("\n                        AND   dt.location_id IN (:locationIdList) ");
        }

        if (!dtsName.isEmpty()) {
            sb.append("\n                        AND   UPPER(dt.name) LIKE :dtsName ");
        }

        if (threshold != null) {
            sb.append("\n                        AND   dt.threshold >= :threshold ");
        }

        sb.append("\n                        AND   me.disttrfmrsubstation_id = dt.id ");
        sb.append("\n                        AND   me.supplier_id = dt.supplier_id ");
        sb.append("\n                        AND   re.meter_id = me.id ");
        sb.append("\n                        AND   re.yyyymmdd = (SELECT MAX(re2.yyyymmdd) ");
        sb.append("\n                                             FROM realtime_billing_em re2 ");
        sb.append("\n                                             WHERE re2.mdev_id = re.mdev_id ");
        sb.append("\n                                             AND   re2.mdev_type = re.mdev_type ");
        sb.append("\n                                             AND   re2.yyyymmdd BETWEEN :searchStartDate AND :searchEndDate) ");
        sb.append("\n                        AND   re.hhmmss = (SELECT MAX(sr.hhmmss) ");
        sb.append("\n                                           FROM realtime_billing_em sr ");
        sb.append("\n                                           WHERE sr.mdev_id = re.mdev_id ");
        sb.append("\n                                           AND   sr.mdev_type = re.mdev_type ");
        sb.append("\n                                           AND   sr.yyyymmdd = re.yyyymmdd) ");
        sb.append("\n                        UNION ALL ");
        sb.append("\n                        SELECT dt.name AS dtsName, ");
        sb.append("\n                               dt.id AS dtsId, ");
        sb.append("\n                               dt.threshold, ");
        sb.append("\n                               dt.location_id, ");
        sb.append("\n                               me.id AS meterId, ");
        sb.append("\n                               0 AS importEnergyTotal1, ");
        sb.append("\n                               re.activeenergyimportratetotal AS importEnergyTotal2 ");
        sb.append("\n                        FROM disttrfmrsubstation dt, ");
        sb.append("\n                             meter me, ");
        sb.append("\n                             realtime_billing_em re ");
        sb.append("\n                        WHERE dt.supplier_id = :supplierId ");

        if (locationIdList != null && locationIdList.size() > 0) {
            sb.append("\n                        AND   dt.location_id IN (:locationIdList) ");
        }

        if (!dtsName.isEmpty()) {
            sb.append("\n                        AND   UPPER(dt.name) LIKE :dtsName ");
        }

        if (threshold != null) {
            sb.append("\n                        AND   dt.threshold >= :threshold ");
        }

        sb.append("\n                        AND   me.disttrfmrsubstation_id = dt.id ");
        sb.append("\n                        AND   me.supplier_id = dt.supplier_id ");
        sb.append("\n                        AND   re.meter_id = me.id ");
        sb.append("\n                        AND   re.yyyymmdd = (SELECT MAX(re2.yyyymmdd) ");
        sb.append("\n                                             FROM realtime_billing_em re2 ");
        sb.append("\n                                             WHERE re2.mdev_id = re.mdev_id ");
        sb.append("\n                                             AND   re2.mdev_type = re.mdev_type ");
        sb.append("\n                                             AND   re2.yyyymmdd >= :searchPreStartDate ");
        sb.append("\n                                             AND   re2.yyyymmdd < :searchStartDate) ");
        sb.append("\n                        AND   re.hhmmss = (SELECT MAX(sr.hhmmss) ");
        sb.append("\n                                           FROM realtime_billing_em sr ");
        sb.append("\n                                           WHERE sr.mdev_id = re.mdev_id ");
        sb.append("\n                                           AND   sr.mdev_type = re.mdev_type ");
        sb.append("\n                                           AND   sr.yyyymmdd = re.yyyymmdd) ");
        sb.append("\n                    ) x ");
        sb.append("\n                GROUP BY dtsName, dtsId, threshold, location_id, meterId ");
        sb.append("\n                UNION ALL ");
        sb.append("\n                SELECT dtsName, ");
        sb.append("\n                       dtsId, ");
        sb.append("\n                       threshold, ");
        sb.append("\n                       location_id, ");
        sb.append("\n                       0 AS importEnergyTotal, ");
        sb.append("\n                       SUM(consumeEnergyTotal) AS consumeEnergyTotal ");
        sb.append("\n                FROM ( ");
        sb.append("\n                        SELECT dtsName, ");
        sb.append("\n                               dtsId, ");
        sb.append("\n                               threshold, ");
        sb.append("\n                               location_id, ");
        sb.append("\n                               meterId, ");
        sb.append("\n                               contractId, ");
        sb.append("\n                               SUM(consumeEnergyTotal1) AS consumeEnergyTotal1, ");
        sb.append("\n                               SUM(consumeEnergyTotal2) AS consumeEnergyTotal2, ");
        sb.append("\n                               CASE WHEN SUM(consumeEnergyTotal1) = 0 THEN 0 ");
        sb.append("\n                                    ELSE SUM(consumeEnergyTotal1) - SUM(consumeEnergyTotal2) END AS consumeEnergyTotal ");
        sb.append("\n                        FROM ( ");
        sb.append("\n                                SELECT dt.name AS dtsName, ");
        sb.append("\n                                       dt.id AS dtsId, ");
        sb.append("\n                                       dt.threshold, ");
        sb.append("\n                                       dt.location_id, ");
        sb.append("\n                                       me.id AS meterId, ");
        sb.append("\n                                       ct.id AS contractId, ");
        sb.append("\n                                       be.activeenergyratetot AS consumeEnergyTotal1, ");
        sb.append("\n                                       0 AS consumeEnergyTotal2 ");
        sb.append("\n                                FROM disttrfmrsubstation dt, ");
        sb.append("\n                                     meter me, ");
        sb.append("\n                                     meter mt ");
        sb.append("\n                                     LEFT OUTER JOIN ");
        sb.append("\n                                     contract ct ");
        sb.append("\n                                     ON ct.meter_id = mt.id, ");
        sb.append("\n                                     code cd, ");
        sb.append("\n                                     billing_day_em be ");
        sb.append("\n                                WHERE dt.supplier_id = :supplierId ");

        if (locationIdList != null && locationIdList.size() > 0) {
            sb.append("\n                                AND   dt.location_id IN (:locationIdList) ");
        }

        if (!dtsName.isEmpty()) {
            sb.append("\n                                AND   UPPER(dt.name) LIKE :dtsName ");
        }

        if (threshold != null) {
            sb.append("\n                                AND   dt.threshold >= :threshold ");
        }

        sb.append("\n                                AND   me.disttrfmrsubstation_id = dt.id ");
        sb.append("\n                                AND   me.supplier_id = dt.supplier_id ");
        sb.append("\n                                AND   (mt.disttrfmrsubstationmeter_a_id = me.id ");
        sb.append("\n                                    OR mt.disttrfmrsubstationmeter_b_id = me.id ");
        sb.append("\n                                    OR mt.disttrfmrsubstationmeter_c_id = me.id) ");
        sb.append("\n                                AND   mt.supplier_id = me.supplier_id ");
        sb.append("\n                                AND   cd.id = mt.metertype_id ");
        sb.append("\n                                AND   cd.code = '1.3.1.1' ");
        sb.append("\n                                AND   be.mdev_id = mt.mds_id ");
        sb.append("\n                                AND   be.mdev_type = :mdevType ");
        sb.append("\n                                AND   be.yyyymmdd = (SELECT MAX(be2.yyyymmdd) ");
        sb.append("\n                                                     FROM billing_day_em be2 ");
        sb.append("\n                                                     WHERE be2.mdev_id = be.mdev_id ");
        sb.append("\n                                                     AND   be2.mdev_type = be.mdev_type ");
        sb.append("\n                                                     AND   be2.yyyymmdd BETWEEN :searchStartDate AND :searchEndDate) ");
        sb.append("\n                                UNION ALL ");
        sb.append("\n                                SELECT dt.name AS dtsName, ");
        sb.append("\n                                       dt.id AS dtsId, ");
        sb.append("\n                                       dt.threshold, ");
        sb.append("\n                                       dt.location_id, ");
        sb.append("\n                                       me.id AS meterId, ");
        sb.append("\n                                       ct.id AS contractId, ");
        sb.append("\n                                       0 AS consumeEnergyTotal1, ");
        sb.append("\n                                       be.activeenergyratetot AS consumeEnergyTotal2 ");
        sb.append("\n                                FROM disttrfmrsubstation dt, ");
        sb.append("\n                                     meter me, ");
        sb.append("\n                                     meter mt ");
        sb.append("\n                                     LEFT OUTER JOIN ");
        sb.append("\n                                     contract ct ");
        sb.append("\n                                     ON ct.meter_id = mt.id, ");
        sb.append("\n                                     code cd, ");
        sb.append("\n                                     billing_day_em be ");
        sb.append("\n                                WHERE dt.supplier_id = :supplierId ");

        if (locationIdList != null && locationIdList.size() > 0) {
            sb.append("\n                                AND   dt.location_id IN (:locationIdList) ");
        }

        if (!dtsName.isEmpty()) {
            sb.append("\n                                AND   UPPER(dt.name) LIKE :dtsName ");
        }

        if (threshold != null) {
            sb.append("\n                                AND   dt.threshold >= :threshold ");
        }

        sb.append("\n                                AND   me.disttrfmrsubstation_id = dt.id ");
        sb.append("\n                                AND   me.supplier_id = dt.supplier_id ");
        sb.append("\n                                AND   (mt.disttrfmrsubstationmeter_a_id = me.id ");
        sb.append("\n                                    OR mt.disttrfmrsubstationmeter_b_id = me.id ");
        sb.append("\n                                    OR mt.disttrfmrsubstationmeter_c_id = me.id) ");
        sb.append("\n                                AND   mt.supplier_id = me.supplier_id ");
        sb.append("\n                                AND   cd.id = mt.metertype_id ");
        sb.append("\n                                AND   cd.code = '1.3.1.1' ");
        sb.append("\n                                AND   be.mdev_id = mt.mds_id ");
        sb.append("\n                                AND   be.mdev_type = :mdevType ");
        sb.append("\n                                AND   be.yyyymmdd = (SELECT MAX(be2.yyyymmdd) ");
        sb.append("\n                                                     FROM billing_day_em be2 ");
        sb.append("\n                                                     WHERE be2.mdev_id = be.mdev_id ");
        sb.append("\n                                                     AND   be2.mdev_type = be.mdev_type ");
        sb.append("\n                                                     AND   be2.yyyymmdd >= :searchPreStartDate ");
        sb.append("\n                                                     AND   be2.yyyymmdd < :searchStartDate) ");
        sb.append("\n                            ) v ");
        sb.append("\n                        GROUP BY dtsName, dtsId, threshold, location_id, meterId, contractId ");
        sb.append("\n                    ) w ");
        sb.append("\n                GROUP BY dtsName, dtsId, threshold, location_id, meterId ");
        sb.append("\n            ) x ");
        sb.append("\n        GROUP BY location_id, dtsName, dtsId, threshold ");

        if (suspected) {
            sb.append("\n        HAVING SUM(importEnergyTotal) > 0 ");
            sb.append("\n        AND    case when SUM(importEnergyTotal) = 0 ");
    		sb.append("\n THEN 0 ");
    		sb.append("\n ELSE (SUM(importEnergyTotal) - SUM(consumeEnergyTotal)) / SUM(importEnergyTotal) ");
    		sb.append("\n END > (threshold / 100) ");
        }

        sb.append("\n    ) dt, ");
        sb.append("\n    location lo ");
        sb.append("\nWHERE lo.id = dt.location_id ");
        sb.append("\nORDER BY lo.name, dt.dtsName ");

        SQLQuery query = getSession().createSQLQuery(new SQLWrapper().getQuery(sb.toString()));
        query.setInteger("supplierId", supplierId);
        query.setInteger("mdevType", DeviceType.Meter.getCode());
        query.setString("searchPreStartDate", searchPreStartDate);
        query.setString("searchStartDate", searchStartDate);
        query.setString("searchEndDate", searchEndDate);

        if (locationIdList != null && locationIdList.size() > 0) {
            query.setParameterList("locationIdList", locationIdList);
        }

        if (!dtsName.isEmpty()) {
            query.setString("dtsName", "%" + dtsName.toUpperCase() + "%");
        }

        if (threshold != null) {
            query.setDouble("threshold", threshold);
        }

        result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        return result;
    }
    
 
}