package com.aimir.dao.device.impl;

import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants.MeterCodes;
import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.device.MbusSlaveIOModuleDao;
import com.aimir.model.device.MbusSlaveIOModule;
import com.aimir.util.SQLWrapper;

@Repository(value = "mbusSlaveIOModuleDao")
public class MbusSlaveIOModuleDaoImpl extends AbstractHibernateGenericDao<MbusSlaveIOModule, Long> implements MbusSlaveIOModuleDao {

    @Autowired
    protected MbusSlaveIOModuleDaoImpl(SessionFactory sessionFactory) {
        super(MbusSlaveIOModule.class);
        super.setSessionFactory(sessionFactory);
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public MbusSlaveIOModule get(String mdsId) {
        MbusSlaveIOModule m = findByCondition("mdsId", mdsId);
        return m;
    }
    
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public MbusSlaveIOModule get(Integer meterId) {
        MbusSlaveIOModule m = findByCondition("meterId", meterId);
        return m;
    }
    
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>>  getMbusSlaveIOModuleInfo(String mdsId) {

        StringBuffer hqlBuf = new StringBuffer();
        
        hqlBuf.append("SELECT ");
        hqlBuf.append("\n  m.mdsId, ");
        hqlBuf.append("\n  m.degital1, m.degital2, m.degital3,m.degital4, ");
        hqlBuf.append("\n  m.degital5, m.degital6, m.degital7,m.degital8, ");
        hqlBuf.append("\n  m.analogCurrent, m.analogCurrentCnv, m.analogVoltage,m.analogVoltageCnv ");
        hqlBuf.append("FROM MbusSlaveIOModule m ");
        hqlBuf.append("WHERE m.mdsId = :mdsId ");
        
        Query query = getSession().createQuery(hqlBuf.toString());
        query.setParameter("mdsId", mdsId);
        Object o = query.list();
        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

   @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
   public List<Map<String, Object>> getMbusSlaveIOModuleCountListPerLocation(Map<String, Object> conditionMap) {
       List<Map<String, Object>> result;

       Integer supplierId = (Integer)conditionMap.get("supplierId");
       String meterType = (String)conditionMap.get("meterType");
       String searchEndDate = (String)conditionMap.get("searchEndDate");
       String searchStartDate = (String)conditionMap.get("searchStartDate");

       StringBuilder sb = new StringBuilder();

       sb.append("\nSELECT p.id AS LOC_ID, ");
       sb.append("\n       COUNT(m.id) AS METER_CNT ");
       sb.append("\nFROM location p, ");
       sb.append("\n     meter m ");
       sb.append("\n     LEFT OUTER JOIN ");
       sb.append("\n     code c ");
       sb.append("\n     ON c.id = m.meter_status ");
       sb.append("\n     INNER JOIN mbus_slave_io_module module  ON  m.id = module.meter_id ");
       sb.append("\nWHERE m.location_id = p.id ");
       sb.append("\nAND   m.meter = :meterType ");
       sb.append("\nAND   m.install_date <= :searchEndDate ");
       sb.append("\nAND   m.supplier_id = :supplierId ");
       sb.append("\nAND   (c.id IS NULL ");
       sb.append("\n    OR c.code != :deleteCode ");
       sb.append("\n    OR (c.code = :deleteCode AND m.delete_date > :deleteDate) ");
       sb.append("\n) ");
       sb.append("\nGROUP BY p.id ");


       SQLQuery query = getSession().createSQLQuery(new SQLWrapper().getQuery(sb.toString()));
       query.setString("meterType", meterType);
       query.setString("searchEndDate", searchEndDate + "235959");
       query.setInteger("supplierId", supplierId);
       query.setString("deleteCode", MeterCodes.DELETE_STATUS.getCode());
       query.setString("deleteDate", searchStartDate + "235959");

       result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

       return result;
   }
   
}
