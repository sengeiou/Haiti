package com.aimir.dao.system.impl;

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

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.system.ObisCodeDao;
import com.aimir.model.system.OBISCode;
import com.aimir.util.StringUtil;

@Repository(value="ObisCodeDao")
public class ObisCodeDaoImpl extends AbstractHibernateGenericDao<OBISCode, Long> implements ObisCodeDao {

	Log logger = LogFactory.getLog(ObisCodeDaoImpl.class);
	
	@Autowired
	protected ObisCodeDaoImpl(SessionFactory sessionFactory) {
		super(OBISCode.class);
		super.setSessionFactory(sessionFactory);
	}

	public List<Map<String,Object>> getObisCodeInfo(Map<String,Object> condition) {
		Integer modelId = condition.get("modelId") == null? null : (Integer)condition.get("modelId");
		String obisCode = StringUtil.nullToBlank(condition.get("obisCode"));
		String classId = StringUtil.nullToBlank(condition.get("classId"));
		String attributeNo = StringUtil.nullToBlank(condition.get("attributeNo"));
		
		StringBuffer sb = new StringBuffer();
		sb.append("\nSELECT o.id as ID, o.obisCode as OBISCODE, o.className as CLASSNAME, o.classId as CLASSID, o.attributeNo as ATTRIBUTENO, ");
		sb.append("\n		o.attributeName as ATTRIBUTENAME, o.dataType as DATATYPE, o.accessRight as ACCESSRIGHT ");
		sb.append("\nFROM	OBISCode o ");
		sb.append("\nWHERE 	o.modelId = :modelId ");
		
		if(obisCode != null && !obisCode.isEmpty()) {
			sb.append("\nAND o.obisCode like :obisCode ");
		}
		if(classId != null && !classId.isEmpty()) {
			sb.append("\nAND o.classId = :classId ");
		}
		if(attributeNo != null && !attributeNo.isEmpty()) {
			sb.append("\nAND o.attributeNo = :attributeNo ");
		}

		Query query = getSession().createQuery(sb.toString());
		
		query.setInteger("modelId", modelId);
		if(obisCode != null && !obisCode.isEmpty()) {
			query.setString("obisCode",obisCode+"%");
		}
		if(classId != null && !classId.isEmpty()) {
			query.setString("classId",classId);
		}
		if(attributeNo != null && !attributeNo.isEmpty()) {
			query.setString("attributeNo",attributeNo);
		}
		List<Map<String,Object>> returnList =  query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		
		return returnList;
	}

	//meter model과 service name에 해당하는 DLMS OBISCode를 조회
    public List<Map<String,Object>> getObisCodeInfoByName(Map<String,Object> condition) {
        Integer modelId = condition.get("modelId") == null? null : (Integer)condition.get("modelId");
        String className = StringUtil.nullToBlank(condition.get("className"));

        StringBuffer sb = new StringBuffer();
        sb.append("\nSELECT o.id as ID, o.obisCode as OBISCODE, o.className as CLASSNAME, o.classId as CLASSID, o.attributeNo as ATTRIBUTENO, ");
        sb.append("\n		o.attributeName as ATTRIBUTENAME, o.dataType as DATATYPE, o.accessRight as ACCESSRIGHT ");
        sb.append("\nFROM	OBISCode o ");
        sb.append("\nWHERE 	o.modelId = :modelId ");

        if(className != null && !className.isEmpty()) {
            sb.append("\nAND o.className = :className ");
        }

        Query query = getSession().createQuery(sb.toString());

        query.setInteger("modelId", modelId);
        if(className != null && !className.isEmpty()) {
            //query.setString("className",className+"%");
            query.setString("className",className);
        }

        List<Map<String,Object>> returnList =  query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

        return returnList;
    }
	
	public List<Map<String,Object>> getObisCodeGroup(Map<String,Object> condition) {
		Integer modelId = condition.get("modelId") == null? null : (Integer)condition.get("modelId");
		String obisCode = StringUtil.nullToBlank(condition.get("obisCode"));
		String classId = StringUtil.nullToBlank(condition.get("classId"));
		String attributeNo = StringUtil.nullToBlank(condition.get("attributeNo"));
		
		StringBuffer sb = new StringBuffer();
		sb.append("\nSELECT o.obisCode as OBISCODE, o.classId as CLASSID, o.className as CLASSNAME ");
		sb.append("\nFROM	OBISCode o ");
		sb.append("\nWHERE 	o.modelId = :modelId ");
		
		if(obisCode != null && !obisCode.isEmpty()) {

		}
		if(classId != null && !classId.isEmpty()) {
			sb.append("\nAND o.classId = :classId ");
		}
		if(attributeNo != null && !attributeNo.isEmpty()) {
			sb.append("\nAND o.attributeNo = :attributeNo ");
		}
		// UPDATE START SP-709
		sb.append("\nGROUP BY o.obisCode, o.classId, o.className ");
		sb.append("ORDER BY o.obisCode");
		// UPDATE END SP-709
		
		Query query = getSession().createQuery(sb.toString());
		
		query.setInteger("modelId", modelId);
		if(obisCode != null && !obisCode.isEmpty()) {
			query.setString("obisCode",obisCode+"%");
		}
		if(classId != null && !classId.isEmpty()) {
			query.setString("classId",classId);
		}
		if(attributeNo != null && !attributeNo.isEmpty()) {
			query.setString("attributeNo",attributeNo);
		}
		List<Map<String,Object>> returnList = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		
		return returnList;
	}
	
	public List<Map<String,Object>> getObisCodeWithEvent(Map<String,Object> condition) {
		Integer modelId = condition.get("modelId") == null? null : (Integer)condition.get("modelId");
		Long obisCodeId = condition.get("obisCodeId")== null ? null : (Long)condition.get("obisCodeId");
		String obisCode = StringUtil.nullToBlank(condition.get("obisCode"));
		String classId = StringUtil.nullToBlank(condition.get("classId"));
		String attributeNo = StringUtil.nullToBlank(condition.get("attributeNo"));
		Boolean isCount = condition.get("isCount") == null ? false :(Boolean)condition.get("isCount");
		Integer page = (Integer)condition.get("page");
        Integer limit = (Integer)condition.get("limit");
		
		StringBuffer sb = new StringBuffer();
		if(isCount) {
			sb.append("SELECT COUNT(o.id) as TOTALCNT");
		} else {
			sb.append("\nSELECT o.id AS ID, ");
			sb.append("\n		o.obis_code AS OBISCODE, ");
			sb.append("\n		o.class_id AS CLASSID, ");
			sb.append("\n		o.class_name AS CLASSNAME, ");
			sb.append("\n		o.attribute_Name AS ATTRIBUTENAME, ");
			sb.append("\n		o.attribute_No AS ATTRIBUTENO, ");
			sb.append("\n		o.dataType AS DATATYPE, ");
			sb.append("\n		o.access_Right AS ACCESSRIGHT, ");
			sb.append("\n		o.descr AS DESCR, ");
			sb.append("\n		o.devicemodel_id AS MODELID, ");
			sb.append("\n		case when me.name is not null then 'Y' else 'N' end as METEREVENT ");
		}
		sb.append("\nFROM	OBISCode o ");
		sb.append("\n    	left outer join ");
		sb.append("\n    	DeviceModel dm ON o.devicemodel_id = dm.id ");
		sb.append("\n		left outer join ");
		sb.append("\n		MeterEvent me ON OBIS_CODE = me.value and dm.name=me.model");
		sb.append("\nWHERE 	o.devicemodel_id = :modelId ");
		
		if(obisCodeId != null) {
			sb.append("\nAND o.id = :obisCodeId ");
		}
		
		if(obisCode != null && !obisCode.isEmpty()) {
			sb.append("\nAND o.OBIS_CODE like :obisCode ");
		}
		if(classId != null && !classId.isEmpty()) {
			sb.append("\nAND o.class_Id = :classId ");
		}
		if(attributeNo != null && !attributeNo.isEmpty()) {
			sb.append("\nAND o.attribute_No = :attributeNo ");
		}
		if(!isCount) {
			sb.append("\nORDER BY o.obis_code, class_id, attribute_No");
		}

		SQLQuery query = getSession().createSQLQuery(sb.toString());
		
		query.setInteger("modelId", modelId);
		if(obisCodeId != null) {
			query.setLong("obisCodeId",obisCodeId);
		}
		if(obisCode != null && !obisCode.isEmpty()) {
			query.setString("obisCode",obisCode+"%");
		}
		if(classId != null && !classId.isEmpty()) {
			query.setString("classId",classId);
		}
		if(attributeNo != null && !attributeNo.isEmpty()) {
			query.setString("attributeNo",attributeNo);
		}
		
		if(!isCount) {
			query.setFirstResult((page - 1) * limit);
	        query.setMaxResults(limit);
		}
		
		List<Map<String,Object>> returnList = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		
		return returnList;
	}
	
	public List<Map<String,Object>> getEventObisCode(String obisFormat) {
		
		StringBuffer sb = new StringBuffer();
		sb.append("\nSELECT o.obisCode as OBISCODE, o.classId as CLASSID, o.attributeNo as ATTRIBUTENO");
		sb.append("\nFROM OBISCode o ");
		sb.append("\nWHERE o.obisCode like :obisFormat ");
		sb.append("\nGROUP BY o.obisCode, o.classId, o.attributeNo ");
		
		Query query = getSession().createQuery(sb.toString());
		
		query.setString("obisFormat", obisFormat);
		
		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	}
	
	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public void updateDescr(Map<String,Object> condition) throws Exception{
		String obisCode = StringUtil.nullToBlank(condition.get("obisCode"));
		String descr = StringUtil.nullToBlank(condition.get("descr"));
		Integer modelId = condition.get("modelId") == null ? null : (Integer)condition.get("modelId");

        StringBuffer sb = new StringBuffer();
        sb.append("\nUPDATE OBISCode ");
        sb.append("\nset descr=:descr ");
        sb.append("\nWHERE obisCode=:obisCode ");
        if(modelId != null) {
        	sb.append("\nAND modelId=:modelId ");
        }

        Query query = getSession().createQuery(sb.toString());
        query.setString("descr",descr);
        query.setString("obisCode",obisCode);
        if(modelId != null) {
        	query.setInteger("modelId", modelId);
        }
        query.executeUpdate();
	}

}
