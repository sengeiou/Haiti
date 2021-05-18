/**
 * ContractDao.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.dao.system.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants.MeterStatus;
import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.model.device.Meter;
import com.aimir.model.system.Code;
import com.aimir.model.system.Contract;
import com.aimir.util.Condition;
import com.aimir.util.SQLWrapper;
import com.aimir.util.StringUtil;

/**
 * ContractDaoImpl.java Description
 *
 *
 * Date          Version     Author   Description
 * 2011. 4. 11.   v1.0       김상연         Contract List 조회 (조건별)
 *
 */
@Repository(value="contractDao")
public class ContractDaoImpl extends AbstractHibernateGenericDao<Contract, Integer> implements ContractDao{

    Log logger = LogFactory.getLog(ContractDaoImpl.class);

    @Autowired
    protected ContractDaoImpl(SessionFactory sessionFactory) {
        super(Contract.class);
        super.setSessionFactory(sessionFactory);
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getMyEnergy(int customerId, int serviceTypeId) {
    	Query query = getSession().createQuery("SELECT contract.id, contract.contractNumber from Contract contract " +
                "left outer join contract.serviceTypeCode service WHERE customer_id = :customerId and servicetype_id = :serviceTypeId ");
    	query.setInteger("customerId", customerId);
    	query.setInteger("serviceTypeId", serviceTypeId);
        return query.list();
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getContractIdByCustomerNo(String[] customerNo) {

        StringBuffer queryBuffer = new StringBuffer();

        queryBuffer.append(" SELECT   c.id ");
        queryBuffer.append(" FROM     Contract c INNER JOIN c.customer m  ");
        queryBuffer.append(" WHERE    m.customerNo in (" + customerNo[0] + " ");
        for(int i=1; i<customerNo.length;i++){
        	queryBuffer.append(" ," + customerNo[i] + " ");
        }
        queryBuffer.append(" ) ");
        
        Query query = getSession().createQuery(queryBuffer.toString());

        return query.list();
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getContractIdByContractNo(String contractNo) {
    	
    	Query query = getSession().createQuery(" FROM Contract c " +
                " WHERE c.contractNumber = :contractNo ");
    	query.setString("contractNo", contractNo);
        return query.list();
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public List<Contract> getContractForSAWSPOS(Map<String, Object> condition) {
        String contractNo = StringUtil.nullToBlank(condition.get("contractNo"));
        String barcode = StringUtil.nullToBlank(condition.get("barcode"));
        
        Criteria criteria = getSession().createCriteria(Contract.class);
        if(!contractNo.isEmpty()) {
            criteria.add(Restrictions.eq("contractNumber", contractNo));
        }
        if(!barcode.isEmpty()) {
            criteria.add(Restrictions.eq("barcode", barcode));
        }

        //계약이 유호한 경우를 거른다.
        criteria.add(Restrictions.isNotNull("customerId"));
        List<Contract> list = criteria.list();
        
        return list;
    }
    
    public Map<String, Object> getRequestDataForUSSDPOS(Map<String, Object> conditionMap) {
        Map<String, Object> data = new HashMap<String,Object>();
        String contractNumber = StringUtil.nullToBlank(conditionMap.get("contractNumber"));

        StringBuffer sb = new StringBuffer();
        Query query = null;
        try{
            sb.append("\nSELECT co.id as contractId, ");
            sb.append("\n       co.supplierId as supplierId, ");
            sb.append("\n       m.id as meterId, ");
            sb.append("\n       co.statusCodeId as statusCodeId, ");
            sb.append("\n       m.meterStatusCodeId as meterStatusCodeId, ");
            sb.append("\n       mcu.sysID as sysId, ");
            sb.append("\n       m.model.deviceConfig.saverName as saverName, ");
            sb.append("\n       m.mdsId as mdsId");
            sb.append("\nFROM Contract co, Meter m left outer join m.modem mo left outer join mo.mcu mcu");
            sb.append("\nWHERE co.meterId=m.id and co.contractNumber = :contractNumber ");
    
            query = getSession().createQuery(sb.toString());
            query.setString("contractNumber", contractNumber);
            
            List<Map<String,Object>> list = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
            if(list.size() > 0) {
                data = list.get(0);
            }
        }catch(Exception e) {
            logger.error(e,e);
        }
        
        return data;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getContractIdByContractNoLike(String contractNo) {

//        return getHibernateTemplate().find(" FROM Contract c " +
//                                           " WHERE c.contractNumber LIKE '%'||?||'%' ", new Object[]{contractNo});
//        return getHibernateTemplate().find(" FROM Contract c " +
//                " WHERE c.contractNumber LIKE ? ", new Object[]{new StringBuilder().append('%').append(contractNo).append('%').toString()});
        Query query = getSession().createQuery(" FROM Contract c WHERE c.contractNumber LIKE :contractNo ");
        query.setString("contractNo", new StringBuilder().append(contractNo).append('%').toString());
    	return query.list();
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getContractIdByCustomerName(String name) {

        StringBuffer queryBuffer = new StringBuffer();

//        query.append(" SELECT   c.id ");
//        query.append(" FROM     Contract c INNER JOIN c.customer m  ");
//        query.append(" WHERE m.name LIKE '%'||?||'%' ");
//
//        return getHibernateTemplate().find(query.toString(), name);

        queryBuffer.append(" SELECT   c.id ");
        queryBuffer.append(" FROM     Contract c INNER JOIN c.customer m  ");
        queryBuffer.append(" WHERE UPPER(m.name) LIKE UPPER(" + new StringBuilder().append('%').append(name).append('%').toString() + ") ");
        
        Query query = getSession().createQuery(queryBuffer.toString());
        return query.list();
    }

    //계약그룹 리스트
    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getContractIdByGroup(String group) {

        StringBuffer query = new StringBuffer();

        query.append(" SELECT   c.id ");
        query.append(" FROM     Contract c LEFT OUTER JOIN ( SELECT * FROM GROUP_MEMBER gm ");
        query.append("          )contractGroup ON c.contract_Number = contractGroup.member");
        query.append(" WHERE contractGroup.group_id = :contractGroup ");

        SQLQuery cquery = getSession().createSQLQuery(new SQLWrapper().getQuery(query.toString()));
        cquery.setString("contractGroup", group);

        return cquery.list();//getHibernateTemplate().find(query.toString(), new StringBuilder().append(group).toString());
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getContractIdByTariffIndex(int tariffIndex) {

        StringBuffer queryBuffer = new StringBuffer();

        queryBuffer.append(" SELECT   id ");
        queryBuffer.append(" FROM     Contract ");
        queryBuffer.append(" WHERE    tariffIndex.id = " + tariffIndex + "  ");
        
        Query query = getSession().createQuery(queryBuffer.toString());
        return query.list();
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getContractCountByStatusCode(Map<String,Object> params) {
        int supplierId     = (Integer)params.get("supplierId");

        StringBuffer sb = new StringBuffer();

        sb.append("\n SELECT   CASE WHEN st.code IS NOT NULL THEN st.code ELSE 'unknown' END AS statusCode, ");
        sb.append("\n          CASE WHEN st.name IS NOT NULL THEN st.name ELSE 'unknown' END AS statusName, COUNT(*) AS statusCount  ");
        sb.append("\n FROM     Contract c LEFT OUTER JOIN c.status AS st ");
        sb.append("\n WHERE 1=1 ");
        if (supplierId > 0) {
            sb.append("\n AND      c.supplier.id = :supplierId ");
            sb.append("\n AND      c.supplier.id = c.customer.supplier.id ");
        }
        sb.append("\n GROUP BY st.code, st.name ");

        Query query = getSession().createQuery(sb.toString());
        if(supplierId > 0){
            query.setInteger("supplierId", supplierId);
        }
        query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);

        return query.list();
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getContractCountForToday(Map<String,Object> params) {

        String today       = (String)params.get("today");
        String serviceType = (String)params.get("serviceType");
        int supplierId     = (Integer)params.get("supplierId");

        StringBuffer sb = new StringBuffer();
        sb.append("\n SELECT   c.status.code as statusCode, COUNT(*) as statusCount ");
        sb.append("\n FROM     Contract c INNER JOIN c.tariffIndex t ");
        sb.append("\n WHERE    c.contractDate = :today ");
        sb.append("\n AND      c.serviceTypeCode.code = :serviceType ");
        sb.append("\n AND      c.serviceTypeCode.id = t.serviceTypeCode.id ");
        if (supplierId > 0) {
            sb.append("\n AND      c.supplier.id = :supplierId ");
            sb.append("\n AND      c.supplier.id = t.supplier.id ");
        }
        sb.append("\n GROUP BY c.status.code ");

        Query query = getSession().createQuery(sb.toString());
        if(supplierId > 0){
            query.setInteger("supplierId", supplierId);
        }
        query.setString("today", today);
        query.setString("serviceType", serviceType);
        query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);

        return query.list();
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getContractCountByTariffType(Map<String,Object> params) {

        String today = (String)params.get("today");
        String type = (String)params.get("type");
        int supplierId = (Integer)params.get("supplierId");

        StringBuffer sb = new StringBuffer();

        sb.append("\nSELECT co.serviceTypeCode.name AS serviceType, ");
        sb.append("\n       co.tariffIndex.name AS tariffType, ");
        sb.append("\n       COUNT(*) AS tariffCount ");
        sb.append("\nFROM Contract co ");
        sb.append("\nWHERE co.contractDate <= :today ");

        if (!"".equals(type)) {
            if (type.equals("null")) {
                sb.append("\nAND   co.status IS NULL ");
            } else {
                sb.append("\nAND   co.status.code = :type ");
            }
        }
        if (supplierId > 0) {
            sb.append("\nAND   co.supplier.id = :supplierId ");
            sb.append("\nAND   co.customer.supplier.id = :supplierId ");
        }
        sb.append("\nGROUP BY co.serviceTypeCode.name, ");
        sb.append("\n         co.tariffIndex.id, ");
        sb.append("\n         co.tariffIndex.name ");

        Query query = getSession().createQuery(sb.toString());
        if (!"".equals(type) && !type.equals("null")) {
            query.setString("type", type);
        }

        if (supplierId > 0) {
            query.setInteger("supplierId", supplierId);
        }
        query.setString("today", today);
        query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);

        return query.list();
    }
    
    @Override
    public Map<String,Object> getPartpayInfoByContractNumber(String contractNumber, Integer supplierId) {
        
        StringBuffer sb = new StringBuffer();
        Map<String, Object> map = null;
        try {
            sb.append("\nSELECT co.creditType.code as creditTypeCode, ");
            sb.append("\n       co.arrearsPaymentCount AS arrearsPaymentCount, ");
            sb.append("\n       co.arrearsContractCount AS arrearsContractCount, ");
            sb.append("\n       co.currentArrears AS currentArrears ");
            sb.append("\nFROM Contract co ");
            sb.append("\nWHERE co.contractNumber = :contractNumber ");
    
            if (supplierId != null) {
                sb.append("\nAND   co.supplier.id = :supplierId ");
            }
    
            Query query = getSession().createQuery(sb.toString());
    
            query.setString("contractNumber", contractNumber);
            if (supplierId != null) {
                query.setInteger("supplierId", supplierId);
            }
            map = (Map<String, Object>) query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).uniqueResult();
            
        } catch(Exception e) {
            logger.error(e,e);
        }
        return map;
    }

    @Override
    public int numberOverlapCheck(String contractNumber) {
    	Query query = getSession().createQuery("SELECT COUNT(c.contractNumber) FROM Contract c WHERE c.contractNumber = " + contractNumber + " ");
        return DataAccessUtils.intResult(query.list());
    }

    /**
     * method name : meterOverlapCheck<b/>
     * method Desc : 미터가 계약된 상태인지를 판별
     *
     * @param Integer
     * @return Integer
     */
    @Override
    public int meterOverlapCheck(Integer meterId) {
    	Query query = getSession().createQuery("SELECT COUNT(c.meterId) FROM Contract c WHERE c.meterId = " + meterId + " ");
        return DataAccessUtils.intResult(query.list());
    }

    @Override
    public void contractAllDelete(Integer id) {
        StringBuffer queryBuffer = new StringBuffer();
        queryBuffer.append("DELETE Contract where customer_id = :customerId ");
        
        Query query = getSession().createQuery(queryBuffer.toString());
        query.setInteger("customerId", id);
        query.executeUpdate();
         
         // bulkUpdate 때문에 주석처리
         /*this.getHibernateTemplate().bulkUpdate(query.toString(), id );*/
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getContractIdWithCustomerName(String[] contractNo) {

        StringBuffer queryBuffer = new StringBuffer();

        queryBuffer.append(" SELECT   c.contractNumber, m.name ");
        queryBuffer.append(" FROM     Contract c INNER JOIN c.customer m  ");
        queryBuffer.append(" WHERE    c.contractNumber in (" + contractNo + " ");
        for(int i=1; i<contractNo.length;i++){
        	queryBuffer.append(" ," + contractNo + " ");
        }
        queryBuffer.append(" ) ");
        queryBuffer.append(" ORDER BY c.id ");
        
        Query query = getSession().createQuery(queryBuffer.toString());
        return query.list();
    }

    @Override
    @Transactional(readOnly=true, propagation=Propagation.REQUIRED)
    public List<Contract> getContractByListCondition(Set<Condition> set) {
        return findByConditions(set);
    }

    @Override
    @SuppressWarnings({ "unchecked" })
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    @Deprecated
    public List<Object[]> getAllCustomerTabData(Map<String, Object> conditionMap) {
        int page;
        int pageSize;
        if(conditionMap.get("page") instanceof Integer){
            page = (Integer)conditionMap.get("page");
            pageSize = (Integer)conditionMap.get("pageSize");
        }else{
            page = Integer.valueOf((String)conditionMap.get("page"));
            pageSize = Integer.valueOf((String)conditionMap.get("pageSize"));
        }
        String contractNumber = StringUtil.nullToBlank(conditionMap.get("contractNumber"));
        String customerNo = (String)conditionMap.get("customerNo");
        String customerName = (String)conditionMap.get("customerName");
        String mdsId = (String)conditionMap.get("mdsId");
        String sicId = StringUtil.nullToBlank(conditionMap.get("customerType"));
        String[] sicIds = sicId.split(",");
        List<Integer> sicIdList = new ArrayList<Integer>();

        for (String obj : sicIds) {
            if (!obj.equals("") && !obj.equals("null")) {
                sicIdList.add(Integer.valueOf(obj));
            }
        }

        String address = (String)conditionMap.get("address");
        String serviceType = (String)conditionMap.get("serviceType");
        String supplierId = (String)conditionMap.get("supplierId");
        List<Integer> locationIdList = (List<Integer>)conditionMap.get("locationIdList");

        StringBuilder sb = new StringBuilder()
        .append("SELECT cust.ID, cust.NAME, cust.ADDRESS, cont.id AS CONTID, ")
        .append("       cont.contract_number AS CONTRACT_NUMBER, loc.name AS LOCNAME, cd1.descr AS SERVICETYPENAME, ")
        .append("       meter.mds_id AS MDS_ID, cd2.descr AS SICNAME, cont.SERVICETYPE_ID, cust.CUSTOMERNO ")
        .append("FROM CUSTOMER cust ")
        .append("     LEFT OUTER JOIN ")
        .append("     CONTRACT cont ")
        .append("     ON  cont.customer_id = cust.id ")
        .append("     AND cont.supplier_id = :supplierId ")
        .append("     LEFT OUTER JOIN LOCATION loc ")
        .append("     ON cont.LOCATION_ID = loc.ID ")
        .append("     LEFT OUTER JOIN CODE cd1 ")
        .append("     ON cont.SERVICETYPE_ID = cd1.ID ")
        .append("     LEFT OUTER JOIN code cd2 ")
        .append("     ON cont.sic_id = cd2.ID ")
        .append("     LEFT OUTER JOIN METER meter ")
        .append("     ON cont.METER_ID = meter.ID ")
        .append("WHERE 1 = 1 ")
        .append("AND cust.SUPPLIER_ID = :supplierId ");
        if (!contractNumber.isEmpty()) {
            sb.append("AND cont.contract_number like :contractNumber ");
        }
        if (!customerNo.isEmpty()) {
            sb.append("AND cust.customerNo like :customerNo ");
        }
        if(!"".equals(customerName)){
            sb.append("AND UPPER(cust.NAME) like UPPER(:customerName) ");
        }
        if(locationIdList != null) sb.append("AND loc.id IN (:locationIdList) ");
        if(!"".equals(address)) sb.append("AND cust.ADDRESS like :address ");
        if(!"".equals(serviceType) && !serviceType.equals("null"))  sb.append("AND cd1.id = :serviceType ");
        if(!"".equals(mdsId)) sb.append("AND meter.mds_id like :mdsId ");
        if(sicIdList.size() > 0) sb.append("AND cont.sic_id IN (:sicIdList) ");

        sb.append("ORDER BY cust.NAME, CONTRACT_NUMBER ASC ");

        int firstResult = page * pageSize;
        SQLQuery query = getSession().createSQLQuery(sb.toString());
        query.setFirstResult(firstResult);
        query.setMaxResults(pageSize);
        query.setInteger("supplierId", Integer.parseInt(supplierId));

        if(!contractNumber.isEmpty()) query.setString("contractNumber", contractNumber+ "%");
        if(!customerNo.isEmpty()) query.setString("customerNo", "%"+ customerNo+ "%");
        if(!"".equals(customerName)) query.setString("customerName", "%"+ customerName+ "%");
        if(locationIdList != null) query.setParameterList("locationIdList", locationIdList);
        if(!"".equals(address)) query.setString("address", "%" + address + "%");
        if(!"".equals(serviceType) && !serviceType.equals("null")) query.setInteger("serviceType", Integer.parseInt(serviceType));
        if(!"".equals(mdsId)) query.setString("mdsId", "%"+ mdsId+"%");
        if(sicIdList.size() > 0) query.setParameterList("sicIdList", sicIdList);
        logger.info(customerNo);
        logger.info(supplierId);
        logger.info(query);
        return query.list();
    }

    /**
     * method name : getTotalContractCount<b/>
     * method Desc : Customer Contract 맥스가젯에서 전체 계약 개수를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @Override
    @SuppressWarnings({ "unchecked" })
    public Integer getTotalContractCount(Map<String, Object> conditionMap) {
        String contractNumber = StringUtil.nullToBlank(conditionMap.get("contractNumber"));
        String customerNo = (String)conditionMap.get("customerNo");
        String customerName = (String)conditionMap.get("customerName");
        String mdsId = (String)conditionMap.get("mdsId");
        String gs1 = StringUtil.nullToBlank(conditionMap.get("gs1"));
        String sicId = StringUtil.nullToBlank(conditionMap.get("customerType"));
        String operatorId = StringUtil.nullToBlank(conditionMap.get("operatorId"));
        String startDate = StringUtil.nullToBlank(conditionMap.get("startDate"));
        String endDate = StringUtil.nullToBlank(conditionMap.get("endDate"));
        String phoneNumber = StringUtil.nullToBlank(conditionMap.get("phoneNumber"));
        String barcode = StringUtil.nullToBlank(conditionMap.get("barcode"));
        String oldMdsId = StringUtil.nullToBlank(conditionMap.get("oldMdsId"));
        String tariffType = StringUtil.nullToBlank(conditionMap.get("tariffType"));
        String[] sicIds = sicId.split(",");
        List<Integer> sicIdList = new ArrayList<Integer>();

        for (String obj : sicIds) {
            if (!obj.equals("") && !obj.equals("null")) {
                sicIdList.add(Integer.valueOf(obj));
            }
        }

        String address = (String)conditionMap.get("address");
        String serviceType = (String)conditionMap.get("serviceType");
        String supplierId = (String)conditionMap.get("supplierId");
        List<Integer> locationIdList = (List<Integer>)conditionMap.get("locationIdList");

        StringBuilder sb = new StringBuilder();

        sb.append("SELECT COUNT(DISTINCT cont.id) AS cnt ");
        sb.append("FROM Contract cont ");
        sb.append("     RIGHT OUTER JOIN ");
        sb.append("     cont.customer cust ");
        sb.append("     LEFT OUTER JOIN ");
        sb.append("     cont.meter me ");
        sb.append("WHERE cust.supplier.id = :supplierId ");

        if (!contractNumber.isEmpty()) {
            sb.append("AND   cont.contractNumber LIKE :contractNumber ");
        }

        if (!customerNo.isEmpty()) {
            sb.append("AND   cust.customerNo LIKE :customerNo ");
        }

        if (!"".equals(customerName)) {
            sb.append("AND   UPPER(cust.name) LIKE UPPER(:customerName) ");
        }

        if (locationIdList != null) {
            sb.append("AND   cont.location.id IN (:locationIdList) ");
        }

        if (!"".equals(address)) {
            sb.append("AND   (cust.address LIKE :address ");
            sb.append("    OR cust.address1 LIKE :address ");
            sb.append("    OR cust.address2 LIKE :address ");
            sb.append("    OR cust.address3 LIKE :address) ");
        }

        if (!"".equals(phoneNumber)) {
            sb.append("AND   cust.mobileNo = :phoneNumber ");
        }
        if (!"".equals(barcode)) {
            sb.append("AND   cont.barcode = :barcode ");
        }
        if (!"".equals(oldMdsId)) {
            sb.append("AND   cont.preMdsId = :oldMdsId ");
        }
        if (!"".equals(tariffType)) {
            sb.append("AND   cont.tariffIndexId = :tariffType ");
        }
        if (!"".equals(gs1)) {
            sb.append("AND   me.gs1 = :gs1 ");
        }
        
        if (!"".equals(serviceType) && !serviceType.equals("null")) {
            sb.append("AND   cont.serviceTypeCode.id = :serviceType ");
        }

        if (!"".equals(mdsId)) {
            sb.append("AND   cont.meter.mdsId LIKE :mdsId ");
        }

        if (sicIdList.size() > 0) {
            sb.append("AND   cont.sic.id IN (:sicIdList) ");
        }
        
        if (!"".equals(operatorId)) {
            sb.append("AND cont.operator.id = :operatorId");
        }
        
        if (!"".equals(startDate)) {
            sb.append("\nAND   cont.contractDate >= :startDate ");
        }
        if (!"".equals(endDate)) {
            sb.append("\nAND   cont.contractDate <= :endDate ");
        }

        Query query = getSession().createQuery(sb.toString());
        query.setInteger("supplierId", Integer.parseInt(supplierId));

        if (!contractNumber.isEmpty()) {
            query.setString("contractNumber", contractNumber+ "%");
        }

        if (!customerNo.isEmpty()) {
            query.setString("customerNo", customerNo+ "%");
        }

        if (!"".equals(customerName)) {
            query.setString("customerName", "%"+ customerName+ "%");
        }

        if (locationIdList != null) {
            query.setParameterList("locationIdList", locationIdList);
        }

        if (!"".equals(address)) {
            query.setString("address", "%" + address + "%");
        }

        if (!"".equals(serviceType) && !serviceType.equals("null")) {
            query.setInteger("serviceType", Integer.parseInt(serviceType));
        }

        if (!"".equals(mdsId)) {
            query.setString("mdsId", mdsId+"%");
        }
        if (!"".equals(gs1)) {
            query.setString("gs1", gs1);
        }
        if (!"".equals(phoneNumber)) {
            query.setString("phoneNumber", phoneNumber);
        }
        if (!"".equals(barcode)) {
            query.setString("barcode", barcode);
        }
        if (!"".equals(oldMdsId)) {
            query.setString("oldMdsId", oldMdsId);
        }
        if (!"".equals(tariffType)) {
            query.setInteger("tariffType", Integer.parseInt(tariffType));
        }

        if (sicIdList.size() > 0) {
            query.setParameterList("sicIdList", sicIdList);
        }
        
        if (!"".equals(operatorId)) {
            query.setInteger("operatorId", Integer.parseInt(operatorId));
        }
        
        if (!"".equals(startDate)) {
            query.setString("startDate",startDate+"000000");
        }
        if (!"".equals(endDate)) {
            query.setString("endDate",endDate+"235959");
        }
        return ((Number)query.uniqueResult()).intValue();
    }

    /**
     * method name : getAllCustomerTabDataTree<b/>
     * method Desc : Customer Contract 맥스가젯에서 전체 고객 Tree Grid 데이터를 조회한다.
     *
     * @param conditionMap
     * @param isCount
     * @return
     */
    @Override
    @SuppressWarnings({ "unchecked" })
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Map<String, Object>> getAllCustomerTabDataTree(Map<String, Object> conditionMap, boolean isCount) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        Integer page;
        Integer pageSize;
        if (conditionMap.get("page") instanceof Integer) {
            page = (Integer)conditionMap.get("page");
            pageSize = (Integer)conditionMap.get("pageSize");
        } else {
            page = Integer.valueOf((String)conditionMap.get("page"));
            pageSize = Integer.valueOf((String)conditionMap.get("pageSize"));
        }
        String contractNumber = StringUtil.nullToBlank(conditionMap.get("contractNumber"));
        String customerNo = (String)conditionMap.get("customerNo");
        String customerName = (String)conditionMap.get("customerName");
        String mdsId = (String)conditionMap.get("mdsId");
        String sicId = StringUtil.nullToBlank(conditionMap.get("customerType"));
        String operatorId = StringUtil.nullToBlank(conditionMap.get("operatorId"));
        String startDate = StringUtil.nullToBlank(conditionMap.get("startDate"));
        String endDate = StringUtil.nullToBlank(conditionMap.get("endDate"));
        String[] sicIds = sicId.split(",");
        List<Integer> sicIdList = new ArrayList<Integer>();

        for (String obj : sicIds) {
            if (!obj.equals("") && !obj.equals("null")) {
                sicIdList.add(Integer.valueOf(obj));
            }
        }
        String gs1 = StringUtil.nullToBlank(conditionMap.get("gs1"));
        String address = (String)conditionMap.get("address");
        String serviceType = (String)conditionMap.get("serviceType");
        String supplierId = (String)conditionMap.get("supplierId");
        String phoneNumber = StringUtil.nullToBlank(conditionMap.get("phoneNumber"));
        String barcode = StringUtil.nullToBlank(conditionMap.get("barcode"));
        String oldMdsId = StringUtil.nullToBlank(conditionMap.get("oldMdsId"));
        String tariffType = StringUtil.nullToBlank(conditionMap.get("tariffType"));
        
        List<Integer> locationIdList = (List<Integer>)conditionMap.get("locationIdList");

        StringBuilder sb = new StringBuilder();

        if (isCount) {
            sb.append("SELECT COUNT(DISTINCT cust.id) AS cnt ");
        } else {
            sb.append("SELECT DISTINCT cust.id AS CUSTOMER_ID, ");
//            sb.append("       me.gs1 AS GS1, ");
//            sb.append("       cont.contractNumber AS CONTRACT_NUMBER, ");
            sb.append("       cust.name AS CUSTOMER_NAME, ");
            sb.append("       cust.address AS CUSTOMER_ADDRESS, ");
            sb.append("       cust.address1 AS CUSTOMER_ADDRESS1, ");
            sb.append("       cust.address2 AS CUSTOMER_ADDRESS2, ");
            sb.append("       cust.address3 AS CUSTOMER_ADDRESS3, ");
            sb.append("       cust.customerNo AS CUSTOMER_NO ");
        }
        sb.append("FROM Contract cont ");
        sb.append("     RIGHT OUTER JOIN ");
        sb.append("     cont.customer cust ");
        sb.append("     LEFT OUTER JOIN ");
        sb.append("     cont.meter me ");
        sb.append("WHERE cust.supplier.id = :supplierId ");
        if (!contractNumber.isEmpty()) {
            sb.append("AND   cont.contractNumber LIKE :contractNumber ");
        }
        if (!customerNo.isEmpty()) {
            sb.append("AND   cust.customerNo LIKE :customerNo ");
        }
        if (!"".equals(customerName)) {
            sb.append("AND   UPPER(cust.name) LIKE UPPER(:customerName) ");
        }
        if (locationIdList != null) {
            sb.append("AND   cont.location.id IN (:locationIdList) ");
        }
        if (!"".equals(address)) {
            sb.append("AND   (cust.address LIKE :address ");
            sb.append("    OR cust.address1 LIKE :address ");
            sb.append("    OR cust.address2 LIKE :address ");
            sb.append("    OR cust.address3 LIKE :address) ");
        }
        if (!"".equals(phoneNumber)) {
            sb.append("AND   cust.mobileNo = :phoneNumber ");
        }
        if (!"".equals(serviceType) && !serviceType.equals("null")) {
        	sb.append("AND   cont.serviceTypeCode.id = :serviceType ");
        }
        if(!"".equals(operatorId)) {
        	sb.append("AND   cont.operator.id = :operatorId ");
        }
        if (!"".equals(startDate)) {
        	sb.append("\nAND   cont.contractDate >= :startDate ");
        }
        if (!"".equals(endDate)) {
        	sb.append("\nAND   cont.contractDate <= :endDate ");
        }
        if (!"".equals(mdsId)) {
        	sb.append("AND   cont.meter.mdsId LIKE :mdsId ");
        }
        if (!"".equals(barcode)) {
            sb.append("AND   cont.barcode = :barcode ");
        }
        if (!"".equals(oldMdsId)) {
            sb.append("AND   cont.preMdsId = :oldMdsId ");
        }
        if (!"".equals(tariffType)) {
            sb.append("AND   cont.tariffIndexId = :tariffType ");
        }
        if (!"".equals(gs1)) {
            sb.append("AND   me.gs1 = :gs1 ");
        }
        if (sicIdList.size() > 0) {
            sb.append("AND   cont.sic.id IN (:sicIdList) ");
        }

        if (!isCount) {
            sb.append("ORDER BY cust.name, cust.customerNo, cust.id ");
        }
        int firstResult = 0;
        if(pageSize != null)
            firstResult = page * pageSize;
        Query query = getSession().createQuery(sb.toString());
        query.setInteger("supplierId", Integer.parseInt(supplierId));

        if (!contractNumber.isEmpty()) {
            query.setString("contractNumber", contractNumber+ "%");
        }
        if (!customerNo.isEmpty()) {
            query.setString("customerNo", customerNo+ "%");
        }
        if (!"".equals(customerName)) {
            query.setString("customerName", "%"+ customerName+ "%");
        }
        if (locationIdList != null) {
            query.setParameterList("locationIdList", locationIdList);
        }
        if (!"".equals(address)) {
            query.setString("address", "%" + address + "%");
        }
        if (!"".equals(serviceType) && !serviceType.equals("null")) {
            query.setInteger("serviceType", Integer.parseInt(serviceType));
        }
        if(!"".equals(operatorId)) {
            query.setInteger("operatorId", Integer.parseInt(operatorId));
        }
        if (!"".equals(startDate)) {
            query.setString("startDate",startDate+"000000");
        }
        if (!"".equals(endDate)) {
            query.setString("endDate",endDate+"235959");
        }
        if (!"".equals(mdsId)) {
            query.setString("mdsId", mdsId+"%");
        }
        if (!"".equals(gs1)) {
            query.setString("gs1", gs1);
        }
        if (!"".equals(phoneNumber)) {
            query.setString("phoneNumber", phoneNumber);
        }
        if (!"".equals(barcode)) {
            query.setString("barcode", barcode);
        }
        if (!"".equals(oldMdsId)) {
            query.setString("oldMdsId", oldMdsId);
        }
        if (!"".equals(tariffType)) {
            query.setInteger("tariffType", Integer.parseInt(tariffType));
        }
        if (sicIdList.size() > 0) {
            query.setParameterList("sicIdList", sicIdList);
        }

        if (isCount) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("total", ((Number)query.uniqueResult()).intValue());
            result = new ArrayList<Map<String, Object>>();
            result.add(map);
        } else {
            if(pageSize != null) {
                query.setFirstResult(firstResult);
                query.setMaxResults(pageSize);
            }
            result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        }
        return result;
    }

    /**
     * method name : getContractListByCustomer<b/>
     * method Desc : Customer Contract 맥스가젯에서 전체 고객 Tree Grid 에서 선택한 Customer 의 Child(Contract) 데이터를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @Override
    @SuppressWarnings({ "unchecked" })
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Map<String, Object>> getContractListByCustomer(Map<String, Object> conditionMap) {
        String contractNumber = StringUtil.nullToBlank(conditionMap.get("contractNumber"));
        String mdsId = (String)conditionMap.get("mdsId");
        String sicId = StringUtil.nullToBlank(conditionMap.get("customerType"));
        String[] sicIds = sicId.split(",");
        List<Integer> sicIdList = new ArrayList<Integer>();

        for (String obj : sicIds) {
            if (!obj.equals("") && !obj.equals("null")) {
                sicIdList.add(Integer.valueOf(obj));
            }
        }

        String serviceType = (String)conditionMap.get("serviceType");
        Integer customerId = (Integer)conditionMap.get("customerId");
        String operatorId = StringUtil.nullToBlank(conditionMap.get("operatorId"));
        String startDate = StringUtil.nullToBlank(conditionMap.get("startDate"));
        String endDate = StringUtil.nullToBlank(conditionMap.get("endDate"));
        List<Integer> locationIdList = (List<Integer>)conditionMap.get("locationIdList");

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT cont.customer.id AS CUSTOMER_ID, ");
        sb.append("       cont.customer.name AS CUSTOMER_NAME, ");
        sb.append("       cont.id AS CONTRACT_ID, ");
        sb.append("       cont.contractNumber AS CONTRACT_NUMBER, ");
        sb.append("       loc.name AS LOCATION_NAME, ");
        sb.append("       cd1.descr AS SERVICETYPE_NAME, ");
        sb.append("       meter.mdsId AS MDS_ID, ");
        sb.append("       meter.gs1 AS GS1, ");
        sb.append("       cd2.descr AS SIC_NAME, ");
        sb.append("       cd1.id AS SERVICETYPE_ID, ");
        sb.append("       cont.customer.customerNo AS CUSTOMER_NO ");
        sb.append("FROM Contract cont ");
        sb.append("     LEFT OUTER JOIN ");
        sb.append("     cont.location loc ");
        sb.append("     LEFT OUTER JOIN ");
        sb.append("     cont.serviceTypeCode cd1 ");
        sb.append("     LEFT OUTER JOIN ");
        sb.append("     cont.sic cd2 ");
        sb.append("     LEFT OUTER JOIN ");
        sb.append("     cont.meter meter ");
        sb.append("WHERE cont.customer.id = :customerId ");

        if (!contractNumber.isEmpty()) {
            sb.append("AND cont.contractNumber LIKE :contractNumber ");
        }
        if (locationIdList != null) {
            sb.append("AND loc.id IN (:locationIdList) ");
        }
        if (!"".equals(serviceType) && !serviceType.equals("null")) {
            sb.append("AND cd1.id = :serviceType ");
        }
        if (!"".equals(mdsId)) {
            sb.append("AND meter.mdsId LIKE :mdsId ");
        }
        if (sicIdList.size() > 0) {
            sb.append("AND cd2.id IN (:sicIdList) ");
        }
        if (!"".equals(operatorId)) {
            sb.append("AND cont.operator.id = :operatorId ");
        }
        if (!"".equals(startDate)) {
            sb.append("\nAND   cont.contractDate >= :startDate ");
        }
        if (!"".equals(endDate)) {
            sb.append("\nAND   cont.contractDate <= :endDate ");
        }

        sb.append("ORDER BY cont.contractNumber ");

        Query query = getSession().createQuery(sb.toString());
        query.setInteger("customerId", customerId);

        if (!contractNumber.isEmpty()) {
            query.setString("contractNumber", contractNumber+ "%");
        }
        if (locationIdList != null) {
            query.setParameterList("locationIdList", locationIdList);
        }
        if (!"".equals(serviceType) && !serviceType.equals("null")) {
            query.setInteger("serviceType", Integer.parseInt(serviceType));
        }
        if (!"".equals(mdsId)) {
            query.setString("mdsId", "%"+ mdsId+"%");
        }
        if (sicIdList.size() > 0) {
            query.setParameterList("sicIdList", sicIdList);
        }
        if (!"".equals(operatorId)) {
            query.setInteger("operatorId", Integer.parseInt(operatorId));
        }
        if (!"".equals(startDate)) {
            query.setString("startDate",startDate);
        }
        if (!"".equals(endDate)) {
            query.setString("endDate",endDate);
        }

        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    @Deprecated
    public String getAllCustomerTabDataCount(Map<String, Object> conditionMap) {
        String contractNumber = StringUtil.nullToBlank(conditionMap.get("contractNumber"));
        String customerNo = (String)conditionMap.get("customerNo");
        String customerName = (String)conditionMap.get("customerName");
        String mdsId = (String)conditionMap.get("mdsId");
        String sicId = (String)conditionMap.get("customerType");
        String[] sicIds = sicId.split(",");
        List<Integer> sicIdList = new ArrayList<Integer>();

        for (String obj : sicIds) {
            if (!obj.equals("") && !obj.equals("null")) {
                sicIdList.add(Integer.valueOf(obj));
            }
        }

        String address = (String)conditionMap.get("address");
        String serviceType = (String)conditionMap.get("serviceType");
        String supplierId = (String)conditionMap.get("supplierId");
        List<Integer> locationIdList = (List<Integer>)conditionMap.get("locationIdList");

        StringBuilder sb = new StringBuilder()
        .append("SELECT COUNT(cust.ID) ")
        .append("FROM CUSTOMER cust ")
        .append("     LEFT OUTER JOIN ")
        .append("     CONTRACT cont ")
        .append("     ON  cont.customer_id = cust.id ")
        .append("     AND cont.SUPPLIER_ID = :supplierId ")
        .append("     LEFT OUTER JOIN LOCATION loc ")
        .append("     ON cont.LOCATION_ID = loc.ID ")
        .append("     LEFT OUTER JOIN CODE cd1 ")
        .append("     ON cont.SERVICETYPE_ID = cd1.ID ")
        .append("     LEFT OUTER JOIN METER meter ")
        .append("     ON cont.METER_ID = meter.ID ")
        .append("WHERE 1 = 1 ")
        .append("AND cust.SUPPLIER_ID = :supplierId ");

        if(!contractNumber.isEmpty()) sb.append("AND cont.contract_number like :contractNumber");
        if(!"".equals(customerNo)) sb.append("AND cust.customerNo like :customerNo ");
        if(!"".equals(customerName)) sb.append("AND UPPER(cust.name) LIKE UPPER(:customerName) ");
        if(locationIdList != null) sb.append("AND loc.id IN (:locationIdList) ");
        if(!"".equals(address)) sb.append("AND cust.address LIKE :address ");
        if(!"".equals(serviceType) && !serviceType.equals("null")) sb.append("AND cd1.id = :serviceType  ");
        if(!"".equals(mdsId)) sb.append("AND meter.mds_id like :mdsId                      ");
//        if(!"".equals(sicId) && !sicId.equals("null")) sb.append("AND cont.sic_id = :sicId ");
        if(sicIdList.size() > 0) sb.append("AND cont.sic_id IN (:sicIdList) ");

        SQLQuery query = getSession().createSQLQuery(sb.toString());

        query.setString("supplierId", supplierId);
        if(!contractNumber.isEmpty()) query.setString("contractNumber", contractNumber+"%");
        if(!"".equals(customerNo)) query.setString("customerNo", "%"+customerNo+"%");
        if(!"".equals(customerName)) query.setString("customerName", "%"+customerName+"%");
        if(locationIdList != null) query.setParameterList("locationIdList", locationIdList);
        if(!"".equals(address)) query.setString("address", "%" + address + "%");
        if(!"".equals(serviceType) && !serviceType.equals("null")) query.setInteger("serviceType", Integer.parseInt(serviceType));
        if(!"".equals(mdsId)) query.setString("mdsId", "%"+mdsId+"%");
//        if(!"".equals(sicId) && !sicId.equals("null")) query.setInteger("sicId", Integer.parseInt(sicId));
        if(sicIdList.size() > 0) query.setParameterList("sicIdList", sicIdList);

        return query.uniqueResult().toString();
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public Map<String, Object> getContractInfo(int contractId) {
        StringBuilder sb = new StringBuilder();

        sb.append("\nSELECT t.name AS tariffindexName, ");
        sb.append("\n       l.name AS locationName, ");
        sb.append("\n       c.contractDemand as contractDemand, ");
        sb.append("\n       s.descr AS statusName, ");
        sb.append("\n       r.descr AS creditTypeName, ");
        sb.append("\n       r.code AS creditTypeCode, ");
        sb.append("\n       t.id AS tariffIndexId, ");
        sb.append("\n       l.name AS locationName, ");
        sb.append("\n       s.id AS statusId, ");
        sb.append("\n       r.id AS creditTypeId, ");
        sb.append("\n       v.descr AS serviceTypeName, ");
        sb.append("\n       CASE WHEN m.mdsId IS NULL THEN '-' ELSE m.mdsId END AS mdsId, ");
        sb.append("\n       i.descr AS sicName ,");
        sb.append("\n       i.id AS sicId ,");
        sb.append("\n       m.id AS meterId, ");
        sb.append("\n       m.gs1 AS gs1, ");
        sb.append("\n       c.contractNumber AS contractNumber, ");
        sb.append("\n       c.barcode AS barcode, ");
        sb.append("\n       c.receiptNumber AS receiptNumber, ");
        sb.append("\n       c.amountPaid AS amountPaid, ");
        sb.append("\n       c.serviceType2 AS serviceType2, ");
        sb.append("\n       c.currentArrears2 AS currentArrears2, ");
        sb.append("\n       c.currentArrears AS currentArrears, ");
        sb.append("\n       c.firstArrears AS firstArrears, ");
        sb.append("\n       c.arrearsPaymentCount AS arrearsPaymentCount, ");
        sb.append("\n       c.arrearsContractCount AS arrearsContractCount, ");
        sb.append("\n       c.chargeAvailable AS chargeAvailable, ");
        sb.append("\n       c.threshold1 AS threshold1, ");
        sb.append("\n       c.threshold2 AS threshold2, ");
        sb.append("\n       c.threshold3 AS threshold3, ");
        sb.append("\n       c.contractDate AS contractDate, ");
        sb.append("\n       CASE WHEN c.preMdsId IS NULL THEN '-' ELSE c.preMdsId END AS preMdsId, ");
        sb.append("\n       CASE WHEN o.loginId IS NULL THEN '-' ELSE o.loginId END AS operator ");
        sb.append("\nFROM Contract c ");
        sb.append("\n     LEFT OUTER JOIN c.tariffIndex t ");
        sb.append("\n     LEFT OUTER JOIN c.location l ");
        sb.append("\n     LEFT OUTER JOIN c.status s ");
        sb.append("\n     LEFT OUTER JOIN c.creditType r ");
        sb.append("\n     LEFT OUTER JOIN c.serviceTypeCode v ");
        sb.append("\n     LEFT OUTER JOIN c.meter m ");
        sb.append("\n     LEFT OUTER JOIN c.sic i ");
        sb.append("\n     LEFT OUTER JOIN c.operator o ");
        sb.append("\nWHERE c.id = :contractId ");

        Query query = getSession().createQuery(sb.toString()).setInteger("contractId", contractId);
        Map<String, Object> map = (Map<String, Object>) query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).uniqueResult();

        return map;
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Map<String, Object>> getEMCustomerTabData(Map<String, Object> conditionMap) {
        String contractNumber = StringUtil.nullToBlank("contractNumber");
        String customerNo = (String)conditionMap.get("customerNo");
        String customerName = (String)conditionMap.get("customerName");
        List<Integer> locationIdList = (List<Integer>)conditionMap.get("locationIdList");
        String tariffIndex = (String)conditionMap.get("tariffIndex");
        String contractDemand = (String)conditionMap.get("contractDemand");
        String creditType = (String)conditionMap.get("creditType");
        String mdsId = (String)conditionMap.get("mdsId");
        String status = (String)conditionMap.get("status");
        String demandResponse = (String)conditionMap.get("dr");
        String sicId = (String)conditionMap.get("customerType");
        String gs1 = (String)conditionMap.get("gs1");
        String operatorId = StringUtil.nullToBlank(conditionMap.get("operatorId"));
        String[] sicIds = sicId.split(",");
        List<Integer> sicIdList = new ArrayList<Integer>();

        for (String obj : sicIds) {
            if (!obj.equals("") && !obj.equals("null")) {
                sicIdList.add(Integer.valueOf(obj));
            }
        }

        String startDate = (String)conditionMap.get("startDate");
        String endDate = (String)conditionMap.get("endDate");
        //String address = conditionMap.get("address");
        //String serviceType = conditionMap.get("serviceType");
        String serviceTypeTab = (String)conditionMap.get("serviceTypeTab");
        String supplierId = (String)conditionMap.get("supplierId");

//        int page = Integer.parseInt(conditionMap.get("page"));
//        int pageSize = Integer.parseInt(conditionMap.get("pageSize"));
        int page = Integer.valueOf((String)conditionMap.get("page"));
        int pageSize = Integer.valueOf((String)conditionMap.get("pageSize"));

        StringBuilder sb = new StringBuilder()
        .append(" SELECT cont.CONTRACT_NUMBER, cust.NAME as CUSTNAME, loc.NAME as LOCNAME, tariff.NAME as TARIFFNAME, ")
        .append("        cont.CONTRACTDEMAND, cd1.NAME as CREDITTYPENAME, mt.MDS_ID, cd2.NAME as STATUSNAME, mt.gs1,  ")
        .append(" case when cust.DEMANDRESPONSE = 0 then 'Y' when cust.DEMANDRESPONSE = 1 then 'N' end as DEMANDRESPONSE,   ")
//        .append("        cd4.NAME as CUSTOMTYPENAME, cust.EMAIL, cust.TELEPHONENO, cust.MOBILENO,  ")
        .append("        cd4.NAME as SICNAME, cust.EMAIL, cust.TELEPHONENO, cust.MOBILENO,  ")
        .append("        cust.ID as CUSTOMERID, cont.ID as CONTRACTID, cont.SERVICETYPE_ID as SERVICETYPE, cust.CUSTOMERNO  ")
        .append("   FROM CONTRACT cont ")
        .append("   JOIN CUSTOMER cust ON cont.CUSTOMER_ID = cust.ID ")
        .append("   LEFT OUTER JOIN LOCATION loc on cont.LOCATION_ID = loc.ID ")
        .append("   LEFT OUTER JOIN TARIFFTYPE tariff on cont.TARIFFINDEX_ID = tariff.ID ")
        .append("   LEFT OUTER JOIN CODE cd1 on cont.CREDITTYPE_ID = cd1.ID ")
        .append("   LEFT OUTER JOIN METER mt on cont.METER_ID = mt.ID ")
        .append("   LEFT OUTER JOIN CODE cd2 on cont.STATUS_ID = cd2.ID ")
        .append("   LEFT OUTER JOIN CODE cd3 on cont.SERVICETYPE_ID = cd3.ID ")
//        .append("   LEFT OUTER JOIN CODE cd4 on cust.CUSTOMTYPE_ID = cd4.ID ")
        .append("   LEFT OUTER JOIN CODE cd4 on cont.sic_id = cd4.ID ")
        .append("  WHERE cont.SUPPLIER_ID = :supplierId   ")
        .append("    AND cd3.CODE = :serviceTypeCode   ");

        if(!"".equals(contractNumber)) sb.append(" AND cont.CONTRACT_NUMBER like :contractNumber ");
        if(!"".equals(customerNo)) sb.append(" AND cust.customerno like :customerNo ");
        if(!"".equals(customerName)) sb.append(" AND UPPER(cust.NAME) like UPPER(:customerName) ");
//        if(!"".equals(locationId)) sb.append(" AND cont.LOCATION_ID = :locationId ");
        if(locationIdList != null) sb.append(" AND cont.LOCATION_ID IN (:locationIdList) ");
        if(!"".equals(tariffIndex)) sb.append(" AND cont.TARIFFINDEX_ID = :tariffIndex ");
        if(!"".equals(contractDemand)) sb.append(" AND cont.CONTRACTDEMAND = :contractDemand ");
        if(!"".equals(creditType)) sb.append(" AND cont.CREDITTYPE_ID = :creditType ");
        if(!"".equals(mdsId)) sb.append(" AND cont.METER_ID like :mdsId ");
        if(!"".equals(status)) sb.append(" AND cont.STATUS_ID = :status");
        if(!"".equals(demandResponse)) sb.append(" AND cust.DEMANDRESPONSE = :demandResponse ");
//        if(!"".equals(customType)) sb.append(" AND cust.CUSTOMTYPE_ID = :customType ");
//        if(!"".equals(sicId)) sb.append(" AND cont.sic_id = :sicId ");
        if(sicIdList.size() > 0) sb.append(" AND cont.sic_id IN (:sicIdList) ");
        if(!"".equals(startDate)) sb.append(" AND cont.CONTRACT_DATE >= :startDate ");
        if(!"".equals(endDate)) sb.append(" AND cont.CONTRACT_DATE <= :endDate ");
        if(!"".equals(operatorId)) sb.append(" AND cont.operator_id = :operatorId");
        if(!"".equals(gs1)) sb.append(" AND mt.gs1 = :gs1");
        int firstResult = page * pageSize;

        String serviceTypeCode = "";

        if("EM".equals(serviceTypeTab)) serviceTypeCode = MeterType.EnergyMeter.getServiceType();
        if("GM".equals(serviceTypeTab)) serviceTypeCode = MeterType.GasMeter.getServiceType();
        if("WM".equals(serviceTypeTab)) serviceTypeCode = MeterType.WaterMeter.getServiceType();
        if("HM".equals(serviceTypeTab)) serviceTypeCode = MeterType.HeatMeter.getServiceType();
        if("VC".equals(serviceTypeTab)) serviceTypeCode = MeterType.VolumeCorrector.getServiceType();

        SQLQuery query = getSession().createSQLQuery(sb.toString());
        query.setInteger("supplierId", Integer.parseInt(supplierId));
        query.setString("serviceTypeCode", serviceTypeCode);

        if(contractNumber.isEmpty()) query.setString("contractNumber", contractNumber +"%");
        if(!"".equals(customerNo)) query.setString("customerNo","%"+ customerNo +"%");
        if(!"".equals(customerName)) query.setString("customerName","%"+ customerName + "%");
//        if(!"".equals(locationId)) query.setInteger("locationId", Integer.parseInt(locationId));
        if(locationIdList != null) query.setParameterList("locationIdList", locationIdList);
        if(!"".equals(tariffIndex)) query.setInteger("tariffIndex", Integer.parseInt(tariffIndex));
        if(!"".equals(contractDemand)) query.setString("contractDemand", contractDemand);
        if(!"".equals(creditType)) query.setInteger("creditType", Integer.parseInt(creditType));
        if(!"".equals(mdsId)) query.setString("mdsId", "%"+mdsId+"%");
        if(!"".equals(status)) query.setInteger("status", Integer.parseInt(status));
        if(!"".equals(demandResponse)) query.setString("demandResponse", demandResponse);
//        if(!"".equals(sicId)) query.setInteger("sicId", Integer.parseInt(sicId));
        if(sicIdList.size() > 0) query.setParameterList("sicIdList", sicIdList);
        if(!"".equals(startDate)) query.setString("startDate", startDate);
        if(!"".equals(endDate)) query.setString("endDate", endDate);
        if(!"".equals(operatorId)) query.setInteger("operatorId", Integer.parseInt(operatorId));
        if(!"".equals(gs1)) query.setString("gs1", gs1);
        query.setFirstResult(firstResult);
        query.setMaxResults(pageSize);

        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

    /**
     * method name : getCustomerListByType<b/>
     * method Desc : Customer Contract Management 맥스 가젯에서 Service Type 별 Customer List 를 조회한다.
     *
     * @param conditionMap
     * @param isCount
     * @return
     */
    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Map<String, Object>> getCustomerListByType(Map<String, Object> conditionMap, boolean isCount) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        String contractNumber = StringUtil.nullToBlank(conditionMap.get("contractNumber"));
        String customerNo = StringUtil.nullToBlank(conditionMap.get("customerNo"));
        String customerName = StringUtil.nullToBlank(conditionMap.get("customerName"));
        String contractDemand = StringUtil.nullToBlank(conditionMap.get("contractDemand"));
        String mdsId = StringUtil.nullToBlank(conditionMap.get("mdsId"));
        String gs1 = StringUtil.nullToBlank(conditionMap.get("gs1"));
        String demandResponse = StringUtil.nullToBlank(conditionMap.get("dr"));
        String startDate = StringUtil.nullToBlank(conditionMap.get("startDate"));
        String endDate = StringUtil.nullToBlank(conditionMap.get("endDate"));
        String serviceTypeTab = StringUtil.nullToBlank(conditionMap.get("serviceTypeTab"));
        String sicId = StringUtil.nullToBlank(conditionMap.get("sicIds"));
        String[] sicIds = sicId.split(",");
        Integer tariffIndex = (Integer)conditionMap.get("tariffIndex");
        Integer creditType = (Integer)conditionMap.get("creditType");
        Integer status = (Integer)conditionMap.get("status");
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        Integer page = (Integer)conditionMap.get("page");
        Integer limit = (Integer)conditionMap.get("limit");
        List<Integer> locationIdList = (List<Integer>)conditionMap.get("locationIdList");
        List<Integer> sicIdList = new ArrayList<Integer>();

        String serviceTypeCode = null;

        for (String obj : sicIds) {
            if (!obj.equals("") && !obj.equals("null")) {
                sicIdList.add(Integer.valueOf(obj));
            }
        }

        StringBuilder sb = new StringBuilder();

        if (isCount) {
            sb.append("\nSELECT COUNT(*) AS cnt FROM ( ");
        }
        sb.append("\nSELECT cont.contract_number AS CONTRACT_NUMBER, ");
        sb.append("\n       cust.name AS CUSTNAME, ");
        sb.append("\n       loc.name AS LOCNAME, ");
        sb.append("\n       tariff.name AS TARIFFNAME, ");
        sb.append("\n       cont.contractdemand AS CONTRACTDEMAND, ");
        sb.append("\n       cd1.descr AS CREDITTYPENAME, ");
        sb.append("\n       mt.mds_id AS MDS_ID, ");
        sb.append("\n       mt.gs1 AS GS1, ");
        sb.append("\n       cd2.descr AS STATUSNAME, ");
        sb.append("\n       CASE WHEN cust.demandresponse = 0 THEN 'Y' ");
        sb.append("\n            WHEN cust.demandresponse = 1 THEN 'N' END AS DEMANDRESPONSE, ");
        sb.append("\n       cd4.descr AS SICNAME, ");
        sb.append("\n       cust.email AS EMAIL, ");
        sb.append("\n       cust.telephoneno AS TELEPHONENO, ");
        sb.append("\n       cust.mobileno AS MOBILENO, ");
        sb.append("\n       cust.id AS CUSTOMERID, ");
        sb.append("\n       cont.id AS CONTRACTID, ");
        sb.append("\n       cont.servicetype_id AS SERVICETYPE, ");
        sb.append("\n       cd3.descr AS SERVICETYPE_NAME, ");
        sb.append("\n       cust.customerno AS CUSTOMERNO ");
        sb.append("\nFROM contract cont ");
        sb.append("\n     JOIN customer cust ");
        sb.append("\n     ON cont.customer_id = cust.id ");
        sb.append("\n     LEFT OUTER JOIN location loc ");
        sb.append("\n     ON cont.location_id = loc.id ");
        sb.append("\n     LEFT OUTER JOIN tarifftype tariff ");
        sb.append("\n     ON cont.tariffindex_id = tariff.id ");
        sb.append("\n     LEFT OUTER JOIN code cd1 ");
        sb.append("\n     ON cont.credittype_id = cd1.id ");
        sb.append("\n     LEFT OUTER JOIN meter mt ");
        sb.append("\n     ON cont.meter_id = mt.id ");
        sb.append("\n     LEFT OUTER JOIN code cd2 ");
        sb.append("\n     ON cont.status_id = cd2.id ");
        sb.append("\n     LEFT OUTER JOIN code cd3 ");
        sb.append("\n     ON cont.servicetype_id = cd3.id ");
        sb.append("\n     LEFT OUTER JOIN code cd4 ");
        sb.append("\n     ON cont.sic_id = cd4.id ");
        sb.append("\nWHERE cont.supplier_id = :supplierId ");
        sb.append("\nAND    cust.supplier_id = :supplierId ");
        sb.append("\nAND   cd3.code = :serviceTypeCode ");
        if(!contractNumber.isEmpty()) {
            sb.append("\nAND cont.contract_number like :contractNumber ");
        }
        if (!customerNo.isEmpty()) {
            sb.append("\nAND   cust.customerno LIKE :customerNo ");
        }
        if (!customerName.isEmpty()) {
            sb.append("\nAND   UPPER(cust.name) LIKE UPPER(:customerName) ");
        }
        if (locationIdList != null && locationIdList.size() > 0) {
            sb.append("\nAND   cont.location_id IN (:locationIdList) ");
        }
        if (tariffIndex != null) {
            sb.append("\nAND   cont.tariffindex_id = :tariffIndex ");
        }
        if (!contractDemand.isEmpty()) {
            sb.append("\nAND   cont.contractdemand = :contractDemand ");
        }
        if (creditType != null) {
            sb.append("\nAND   cont.credittype_id = :creditType ");
        }
        if (!mdsId.isEmpty()) {
            sb.append("\nAND   mt.mds_Id LIKE :mdsId ");
        }
        if (!gs1.isEmpty()) {
            sb.append("\nAND   mt.gs1 LIKE :gs1 ");
        }
        if (status != null) {
            sb.append("\nAND   cont.status_id = :status");
        }
        if (!demandResponse.isEmpty()) {
            sb.append("\nAND   cust.demandresponse = :demandResponse ");
        }
        if (sicIdList.size() > 0) {
            sb.append("\nAND   cont.sic_id IN (:sicIdList) ");
        }
        if (!startDate.isEmpty()) {
            sb.append("\nAND   cont.contract_date >= :startDate ");
        }
        if (!endDate.isEmpty()) {
            sb.append("\nAND   cont.contract_date <= :endDate ");
        }

        if (isCount) {
            sb.append("\n) x ");
        } else {
            sb.append("\n ORDER BY cont.contract_number ASC");
        }



        if ("EM".equals(serviceTypeTab)) serviceTypeCode = MeterType.EnergyMeter.getServiceType();
        if ("GM".equals(serviceTypeTab)) serviceTypeCode = MeterType.GasMeter.getServiceType();
        if ("WM".equals(serviceTypeTab)) serviceTypeCode = MeterType.WaterMeter.getServiceType();
        if ("HM".equals(serviceTypeTab)) serviceTypeCode = MeterType.HeatMeter.getServiceType();
        if ("VC".equals(serviceTypeTab)) serviceTypeCode = MeterType.VolumeCorrector.getServiceType();

        SQLQuery query = getSession().createSQLQuery(sb.toString());
        query.setInteger("supplierId", supplierId);
        query.setString("serviceTypeCode", serviceTypeCode);
        if (!contractNumber.isEmpty()) {
            query.setString("contractNumber", contractNumber +"%");
        }
        if (!customerNo.isEmpty()) {
            query.setString("customerNo", customerNo +"%");
        }
        if (!customerName.isEmpty()) {
            query.setString("customerName", "%"+ customerName + "%");
        }
        if (locationIdList != null && locationIdList.size() > 0) {
            query.setParameterList("locationIdList", locationIdList);
        }
        if (tariffIndex != null) {
            query.setInteger("tariffIndex", tariffIndex);
        }
        if (!contractDemand.isEmpty()) {
            query.setString("contractDemand", contractDemand);
        }
        if (creditType != null) {
            query.setInteger("creditType", creditType);
        }
        if (!mdsId.isEmpty()) {
            query.setString("mdsId", mdsId+"%");
        }
        if (!gs1.isEmpty()) {
            query.setString("gs1", gs1);
        }
        if (status != null) {
            query.setInteger("status", status);
        }
        if (!demandResponse.isEmpty()) {
            query.setString("demandResponse", demandResponse);
        }
        if (sicIdList.size() > 0) {
            query.setParameterList("sicIdList", sicIdList);
        }
        if (!startDate.isEmpty()) {
            query.setString("startDate", startDate);
        }
        if (!endDate.isEmpty()) {
            query.setString("endDate", endDate);
        }

        if (isCount) {
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
     *
     * @desc : all meter list fetch dao impl
     */
    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Deprecated
    @Transactional(readOnly=true, propagation=Propagation.REQUIRED)
    public List<Meter> getMeterList(Map<String, String> conditionMap)
    {
        List<Meter> meterList =new ArrayList();

        StringBuilder sb = new StringBuilder().append("select * from Meter mt ");

        SQLQuery query = this.makeQueryCondForMeterList(sb, conditionMap);

        String strPage = conditionMap.get("page");
        String strPageSize = conditionMap.get("pageSize");

        int pageCommaIndex = strPage.indexOf(".");
        int pageSizeCommaIndex = strPageSize.indexOf(".");
        int page = -1;
        int pageSize = -1;

        if(pageCommaIndex > -1)
            page = Integer.parseInt(strPage.substring(0, pageCommaIndex));
        else
            page = Integer.parseInt(strPage);

        if(pageSizeCommaIndex > -1)
            pageSize = Integer.parseInt(strPageSize.substring(0, pageSizeCommaIndex));
        else
            pageSize = Integer.parseInt(strPageSize);

        int firstResult = page * pageSize;

        query.setFirstResult(firstResult);
        query.setMaxResults(pageSize);

        meterList = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

        return meterList;
    }

    /**
     * method name : getMeterGridList<b/>
     * method Desc :
     *
     * @param conditionMap
     * @return
     */
    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Map<String, Object>> getMeterGridList(Map<String, Object> conditionMap, boolean isCount) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        String mdsId = StringUtil.nullToBlank(conditionMap.get("mdsId"));
        String gs1 = StringUtil.nullToBlank(conditionMap.get("gs1"));
        Integer page = (Integer)conditionMap.get("page");
        Integer limit = (Integer)conditionMap.get("limit");

        StringBuilder sb = new StringBuilder();

        if (isCount) {
            sb.append("\nSELECT COUNT(*) AS cnt ");
        } else {
            sb.append("\nSELECT mt.id AS id, ");
            sb.append("\n       mt.gs1 AS gs1, ");
            sb.append("\n       mt.mdsId AS mdsId ");
        }
        sb.append("\nFROM Meter mt left outer join mt.meterStatus ms ");

        sb.append("\nWHERE (mt.meterStatus is null OR ms.name <> :deleteName) ");
        
        if (!gs1.isEmpty() && !"".equals(gs1)) {
        	sb.append("\nAND mt.gs1 LIKE :gs1 ");
        }else if (!mdsId.isEmpty() && !"".equals(mdsId)) {
            sb.append("\nAND mt.mdsId LIKE :mdsId ");
        }
        
        if (!isCount) {
            sb.append("\nORDER BY mt.mdsId ");
        }

        Query query = getSession().createQuery(new SQLWrapper().getQuery(sb.toString()));
        if (!gs1.isEmpty() && !"".equals(gs1)) {
        	query.setString("gs1", "%" + gs1 + "%");
        }else if (!mdsId.isEmpty() && !"".equals(mdsId)) {
            query.setString("mdsId", "%" + mdsId + "%");
        }
        query.setString("deleteName", MeterStatus.Delete.name());
        if (isCount) {
            Map<String, Object> map = new HashMap<String, Object>();
            Number count = (Number)query.uniqueResult();
            map.put("total", count.intValue());
            result.add(map);
        } else {
            query.setFirstResult((page - 1) * limit);
            query.setMaxResults(limit);
            result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        }

        return result;
    }

    /**
     * @desc : 검색조건에 맞는 쿼리를 만들어준다.
     * @param sb
     * @param conditionMap
     * @return
     */
    @Deprecated
    public SQLQuery makeQueryCondForMeterList(StringBuilder sb, Map<String, String> conditionMap)
    {
        String  mdsId = conditionMap.get("mdsId");

        if ( mdsId !=null  )
        {
            sb.append("where mt.MDS_ID like :mdsId ");
        }

        SQLQuery query = getSession().createSQLQuery(sb.toString());

        if ( mdsId !=null  )
        {
            query.setString("mdsId"  ,"%"+conditionMap.get("mdsId")+ "%");
        }

        return query;
    }

    /**
     * @desc all meter list total count fetch dao impl
     */
    @Override
    @SuppressWarnings({ "rawtypes", "unused" })
    @Deprecated
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public String getMeterListDataCount(Map<String, String> conditionMap)
    {
        List meterList =new ArrayList();

        StringBuilder sb = new StringBuilder().append(" SELECT count(*)  from Meter mt  ");

        //쿼리를 컨디션에 맞게만들어준다.
        SQLQuery query = this.makeQueryCondForMeterList(sb, conditionMap);

        Object queryResult = query.uniqueResult();

        return queryResult.toString();
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public String getEMCustomerTabDataCount(Map<String, Object> conditionMap) {
        String contractNumber = StringUtil.nullToBlank("contractNumber");
        String customerNumber = (String)conditionMap.get("customerNo");
        String customerName = (String)conditionMap.get("customerName");
//        String locationId = conditionMap.get("location");
        List<Integer> locationIdList = (List<Integer>)conditionMap.get("locationIdList");
        String tariffIndex = (String)conditionMap.get("tariffIndex");
        String contractDemand = (String)conditionMap.get("contractDemand");
        String creditType = (String)conditionMap.get("creditType");
        String mdsId = (String)conditionMap.get("mdsId");
        String status = (String)conditionMap.get("status");
        String demandResponse = (String)conditionMap.get("dr");
        String sicId = (String)conditionMap.get("customerType");
        String operatorId = StringUtil.nullToBlank(conditionMap.get("operatorId"));
        String[] sicIds = sicId.split(",");
        List<Integer> sicIdList = new ArrayList<Integer>();

        for (String obj : sicIds) {
            if (!obj.equals("") && !obj.equals("null")) {
                sicIdList.add(Integer.valueOf(obj));
            }
        }

        String startDate = (String)conditionMap.get("startDate");
        String endDate = (String)conditionMap.get("endDate");
        //String address = conditionMap.get("address");
        //String serviceType = conditionMap.get("serviceType");
        String serviceTypeTab = (String)conditionMap.get("serviceTypeTab");
        String supplierId = (String)conditionMap.get("supplierId");

        StringBuilder sb = new StringBuilder()
        .append(" SELECT count(cont.ID) ")
        .append("   FROM CONTRACT cont ")
        .append("   JOIN CUSTOMER cust ON cont.CUSTOMER_ID = cust.ID ")
        .append("   LEFT OUTER JOIN CODE cd3 on cont.SERVICETYPE_ID = cd3.ID ")
        .append("  WHERE cont.SUPPLIER_ID = :supplierId   ")
        .append("    AND cd3.CODE = :serviceTypeCode   ");

        if(!"".equals(contractNumber)) sb.append(" AND cont.CONTRACT_NUMBER like :contractNumber ");
        if(!"".equals(customerNumber)) sb.append(" AND cust.customerno like :customerNumber ");
        if(!"".equals(customerName)) sb.append(" AND UPPER(cust.NAME) like UPPER(:customerName) ");
//        if(!"".equals(locationId)) sb.append(" AND cont.LOCATION_ID = :locationId ");
        if(locationIdList != null) sb.append(" AND cont.LOCATION_ID IN (:locationIdList) ");
        if(!"".equals(tariffIndex)) sb.append(" AND cont.TARIFFINDEX_ID = :tariffIndex ");
        if(!"".equals(contractDemand)) sb.append(" AND cont.CONTRACTDEMAND = :contractDemand ");
        if(!"".equals(creditType)) sb.append(" AND cont.CREDITTYPE_ID = :creditType ");
        if(!"".equals(mdsId)) sb.append(" AND cont.METER_ID like :mdsId ");
        if(!"".equals(status)) sb.append(" AND cont.STATUS_ID = :status");
        if(!"".equals(demandResponse)) sb.append(" AND cust.DEMANDRESPONSE = :demandResponse ");
//        if(!"".equals(customType)) sb.append(" AND cust.CUSTOMTYPE_ID = :customType ");
//        if(!"".equals(sicId)) sb.append(" AND cont.sic_id = :sicId ");
        if(sicIdList.size() > 0) sb.append(" AND cont.sic_id IN (:sicIdList) ");
        if(!"".equals(startDate)) sb.append(" AND cont.CONTRACT_DATE >= :startDate ");
        if(!"".equals(endDate)) sb.append(" AND cont.CONTRACT_DATE <= :endDate ");
        if(!"".equals(operatorId)) sb.append(" AND cont.operator_id = :operatorId");

        String serviceTypeCode = "";

        if("EM".equals(serviceTypeTab)) serviceTypeCode = MeterType.EnergyMeter.getServiceType();
        if("GM".equals(serviceTypeTab)) serviceTypeCode = MeterType.GasMeter.getServiceType();
        if("WM".equals(serviceTypeTab)) serviceTypeCode = MeterType.WaterMeter.getServiceType();

        SQLQuery query = getSession().createSQLQuery(sb.toString());
        query.setInteger("supplierId", Integer.parseInt(supplierId));
        query.setString("serviceTypeCode", serviceTypeCode);

        if(!contractNumber.isEmpty()) query.setString("contractNumber", contractNumber+"%");
        if(!"".equals(customerNumber)) query.setString("customerNumber", "%"+customerNumber+"%");
        if(!"".equals(customerName)) query.setString("customerName", "%"+customerName+"%");
//        if(!"".equals(locationId)) query.setInteger("locationId", Integer.parseInt(locationId));
        if(locationIdList != null) query.setParameterList("locationIdList", locationIdList);
        if(!"".equals(tariffIndex)) query.setInteger("tariffIndex", Integer.parseInt(tariffIndex));
        if(!"".equals(contractDemand)) query.setString("contractDemand", contractDemand);
        if(!"".equals(creditType)) query.setInteger("creditType", Integer.parseInt(creditType));
        if(!"".equals(mdsId)) query.setString("mdsId", "%"+mdsId+"%");
        if(!"".equals(status)) query.setInteger("status", Integer.parseInt(status));
        if(!"".equals(demandResponse)) query.setString("demandResponse", demandResponse);
//        if(!"".equals(sicId)) query.setInteger("sicId", Integer.parseInt(sicId));
        if(sicIdList.size() > 0) query.setParameterList("sicIdList", sicIdList);
        if(!"".equals(startDate)) query.setString("startDate", startDate+"000000");
        if(!"".equals(endDate)) query.setString("endDate", endDate+"235959");
        if(!"".equals(operatorId)) query.setInteger("operatorId", Integer.parseInt(operatorId));

        return query.uniqueResult().toString();
    }


    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRED)
    public List<Contract> getContractByCustomerId(int customerId){


        Criteria criteria = getSession().createCriteria(Contract.class);
        criteria.add(Restrictions.eq("customer.id", customerId));

        List<Contract> list = criteria.list();

        return list;
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRED)
    public List<Contract> getContractByMeterId(int meterId){


        Criteria criteria = getSession().createCriteria(Contract.class);
        criteria.add(Restrictions.eq("meter.id", meterId));

        List<Contract> list = criteria.list();

        return list;
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Map<String, String>> getSupplyCapacity(Map<String, String> paramMap) {

        int supplierId = Integer.parseInt(paramMap.get("supplierId"));
        String serviceTypeCode = paramMap.get("serviceType");
        int page = Integer.parseInt(paramMap.get("page"));
        int pageSize = Integer.parseInt(paramMap.get("pageSize"));
        int firstRecord = page * pageSize;

        StringBuilder sb = new StringBuilder()
        .append(" SELECT 'false' as checked, c.customer.name as name, c.contractNumber as contractNumber, '0' as status, c.id ")
        .append("   FROM Contract c                                ")
        .append("  WHERE c.supplier.id = :supplierId               ")
        .append("    AND c.serviceTypeCode.code = :serviceTypeCode ");

        Query query = getSession().createQuery(sb.toString());
        query.setInteger("supplierId", supplierId);
        query.setString("serviceTypeCode", serviceTypeCode);

        query.setFirstResult(firstRecord);
        query.setMaxResults(pageSize);

        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

    /**
     * 그룹 관리 중 멤버 리스트 조회
     *
     * @param condition
     * @return
     */
    @Override
    @SuppressWarnings("unchecked")
    @Deprecated
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getGroupMember(Map<String, Object> condition){
        String member = StringUtil.nullToBlank(condition.get("member"));

        StringBuffer sb = new StringBuffer();
        sb.append("SELECT t.id, t.contract_number ")
          .append("FROM CONTRACT t LEFT JOIN GROUP_MEMBER g ON t.contract_number = g.member ")
          .append("WHERE t.supplier_id = :supplierId ");
        if(!"".equals(member)){
            sb.append("AND t.contract_number like '%").append((String)condition.get("member")).append("%'");
        }
        sb.append("AND t.contract_number NOT IN ( ");
            sb.append("SELECT t.contract_number ");
            sb.append("FROM CONTRACT t RIGHT JOIN GROUP_MEMBER g ON t.contract_number = g.member ");
            sb.append("WHERE t.supplier_id = :supplierId ");
        sb.append(") ");
        sb.append(" ORDER BY t.contract_number ASC");

        SQLQuery query = getSession().createSQLQuery(sb.toString());
        return query.setInteger("supplierId", Integer.parseInt((String)condition.get("supplierId")))
                    .list();
    }

    /**
     * method name : getMemberSelectData<b/>
     * method Desc : Group Management 가젯에서 Member 로 등록 가능한 Contract 리스트를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getMemberSelectData(Map<String, Object> conditionMap) {
    	List<Object> gridData 	= new ArrayList<Object>();		
    	List<Object> result		= new ArrayList<Object>();
    	
    	Integer supplierId = (Integer)conditionMap.get("supplierId");
        Integer groupId = (Integer)conditionMap.get("groupId");
        String memberName = StringUtil.nullToBlank(conditionMap.get("memberName"));
        String contractNumber = StringUtil.nullToBlank(conditionMap.get("contractNumber"));
        String customerNo = StringUtil.nullToBlank(conditionMap.get("customerNo"));
        String customerName = StringUtil.nullToBlank(conditionMap.get("customerName"));
        String contractDemand = StringUtil.nullToBlank(conditionMap.get("contractDemand"));
        String mdsId = StringUtil.nullToBlank(conditionMap.get("mdsId"));
        String demandResponse = StringUtil.nullToBlank(conditionMap.get("dr"));
        String startDate = StringUtil.nullToBlank(conditionMap.get("startDate"));
        String endDate = StringUtil.nullToBlank(conditionMap.get("endDate"));
        String serviceTypeTab = StringUtil.nullToBlank(conditionMap.get("serviceTypeTab"));
        String sicId = StringUtil.nullToBlank(conditionMap.get("sicIds"));
        String[] sicIds = sicId.split(",");
        Integer tariffIndex = (Integer)conditionMap.get("tariffIndex");
        Integer creditType = (Integer)conditionMap.get("creditType");
        Integer status = (Integer)conditionMap.get("status");
        //Integer supplierId = (Integer)conditionMap.get("supplierId");
        int page = (Integer)conditionMap.get("page");
        int limit = (Integer)conditionMap.get("limit");
        List<Integer> locationIdList = (List<Integer>)conditionMap.get("locationIdList");
        List<Integer> sicIdList = new ArrayList<Integer>();
        
        StringBuilder sb = new StringBuilder();
        StringBuilder sbQuery = new StringBuilder();
        
        sbQuery.append("\nSELECT co.id AS value, ");
        sbQuery.append("\n       co.contractNumber AS text, ");
        sbQuery.append("\n       'Contract' AS type ");
        sb.append("\nFROM Contract co ");
        sb.append("\nleft outer join co.location loc "); //LOCATION
        sb.append("\nleft outer join co.customer cust "); //CUSTOMER
        sb.append("\nleft outer join co.tariffIndex tariff "); //
        sb.append("\nleft outer join co.creditType cd1 "); //
        sb.append("\nleft outer join co.meter mt "); //
        sb.append("\nleft outer join co.status cd2 "); //
        sb.append("\nleft outer join co.serviceTypeCode cd3 "); //
        sb.append("\nleft outer join co.sic cd4 "); //
        sb.append("\nWHERE co.supplier.id = :supplierId ");

        if (!memberName.isEmpty()) {
            sb.append("\nAND   co.contractNumber LIKE :memberName ");
        }
        //검색조건
        if(!contractNumber.isEmpty()) {
            sb.append("     AND co.contractNumber LIKE '"+ contractNumber +"%' ");
        }
        if (!customerNo.isEmpty()) {
            sb.append("\nAND   cust.customerNo LIKE '"+ customerNo +"%' ");
        }
        if (!customerName.isEmpty()) {
            sb.append("\nAND   UPPER(cust.name) LIKE UPPER('"+ customerName +"%') ");
        }
        if (locationIdList != null && locationIdList.size() > 0) {
            sb.append("\nAND   co.locationId  IN (" + locationIdList + ") ");
        }
        if (tariffIndex != null) {
            sb.append("\nAND   co.tariffIndex = '"+ tariffIndex + "' ");
        }
        if (!contractDemand.isEmpty()) {
            sb.append("\nAND   co.contractDemand = '"+ contractDemand +"' ");
        }
        if (creditType != null) {
            sb.append("\nAND   co.creditType = '"+ creditType +"' ");
        }
        if (!mdsId.isEmpty()) {
            sb.append("\nAND   mt.mdsId LIKE '"+ mdsId + "%'");
        }
        if (status != null) {
            sb.append("\nAND   co.status = '"+ status+ "' ");
        }
        if (!demandResponse.isEmpty()) {
            sb.append("\nAND   cust.demandResponse = '"+ demandResponse +"' ");
        }
        if (sicIdList.size() > 0) {
            sb.append("\nAND   co.sic IN (" + sicIdList + ") ");
        }
        if (!startDate.isEmpty()) {
            sb.append("\nAND   co.contractDate >= '"+ startDate +"000000' ");
        }
        if (!endDate.isEmpty()) {
            sb.append("\nAND   co.contractDate <= '"+ endDate +"235959' ");
        }
        sb.append("\nAND   NOT EXISTS ( ");
        sb.append("\n    SELECT 'X' ");
        sb.append("\n    FROM GroupMember gm ");
        sb.append("\n    WHERE gm.member = co.contractNumber ");
        sb.append("\n    AND   gm.aimirGroup.id = :groupId ");
        sb.append("\n) ");
        sb.append("\nORDER BY co.contractNumber ");

     // 전체 건수
        StringBuffer countQuery = new StringBuffer();
        countQuery.append("\n SELECT COUNT( * ) ");
        countQuery.append(sb);
        
        Query countQueryObj = getSession().createQuery(countQuery.toString());
        countQueryObj.setInteger("supplierId", supplierId);
        countQueryObj.setInteger("groupId", groupId);
        if (!memberName.isEmpty()) {
        	countQueryObj.setString("memberName", "%" + memberName + "%");
        }
        
        Number totalCount = (Number)countQueryObj.uniqueResult();
        result.add(totalCount.toString());

        sbQuery.append(sb);
        
        Query query = getSession().createQuery(sbQuery.toString());
        query.setInteger("supplierId", supplierId);
        query.setInteger("groupId", groupId);
        if (!memberName.isEmpty()) {
            query.setString("memberName", "%" + memberName + "%");
        }

        query.setFirstResult((page-1) * limit);      
        query.setMaxResults(limit);
        
        List dataList = null;
		dataList = query.list();
		
		// 실제 데이터
		int dataListLen = 0;
		if(dataList != null)
			dataListLen= dataList.size();
				
		for (int i = 0; i < dataListLen; i++) {
			HashMap chartDataMap = new HashMap();
			Object[] resultData = (Object[]) dataList.get(i);
			
			chartDataMap.put("value",           resultData[0] );
			chartDataMap.put("text",      resultData[1]);                 
			chartDataMap.put("type",    resultData[2]);
			gridData.add(chartDataMap);
		}
		
		result.add(gridData);
		
		return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object[]> getContractListByMeter(Map<String, String> conditionMap) {

        String customerNo    = StringUtil.nullToBlank(conditionMap.get("customerNo"));
        String customerName  = StringUtil.nullToBlank(conditionMap.get("customerName"));
        String locationId    = StringUtil.nullToBlank(conditionMap.get("location"));
        String mdsId         = StringUtil.nullToBlank(conditionMap.get("mdsId"));
        String sicId    = StringUtil.nullToBlank(conditionMap.get("customerType"));
        String address       = StringUtil.nullToBlank(conditionMap.get("address"));
        String serviceType   = StringUtil.nullToBlank(conditionMap.get("serviceType"));
        String supplierId    = StringUtil.nullToBlank(conditionMap.get("supplierId"));

        StringBuilder sb = new StringBuilder()
        .append(" SELECT cust.ID, cust.NAME, cust.ADDRESS, c.CONTID, ")
//        .append("        c.CONTRACT_NUMBER, c.LOCNAME, c.SERVICETYPENAME, c.MDS_ID, cd2.NAME as CUSTOMTYPENAME, c.SERVICETYPE_ID, cust.CUSTOMERNO, c.CHECKED ")
//        .append("        c.CONTRACT_NUMBER, c.LOCNAME, c.SERVICETYPENAME, c.MDS_ID, c.CUSTOMTYPENAME, c.SERVICETYPE_ID, cust.CUSTOMERNO, c.CHECKED ")
        .append("        c.CONTRACT_NUMBER, c.LOCNAME, c.SERVICETYPENAME, c.MDS_ID, c.SICNAME, c.SERVICETYPE_ID, cust.CUSTOMERNO, c.CHECKED ")
//        .append("   FROM (SELECT cont.ID as CONTID, cont.CUSTOMER_ID, cont.CONTRACT_NUMBER, cont.sic_id, cd2.NAME as CUSTOMTYPENAME, ")
        .append("   FROM (SELECT cont.ID as CONTID, cont.CUSTOMER_ID, cont.CONTRACT_NUMBER, cont.sic_id, cd2.NAME as SICNAME, ")
        .append("               loc.NAME as LOCNAME, cd1.NAME as SERVICETYPENAME, meter.MDS_ID, cont.SERVICETYPE_ID, cont.LOCATION_ID ")
        .append("          ,CASE WHEN meter.MDS_ID = :mdsId  THEN 'Y' ELSE 'N' END  as CHECKED ")
        .append("          FROM CONTRACT cont ")
        .append("          LEFT OUTER JOIN LOCATION loc ON cont.LOCATION_ID = loc.ID ")
        .append("          LEFT OUTER JOIN CODE cd1 ON cont.SERVICETYPE_ID = cd1.ID ")
        .append("          LEFT OUTER JOIN CODE cd2 ON cont.sic_id = cd2.ID ")
        .append("          LEFT OUTER JOIN METER meter ON cont.METER_ID = meter.ID ")
        .append("         WHERE cont.SUPPLIER_ID = :supplierId ")
        .append("         AND   ( meter.MDS_ID is null OR meter.MDS_ID = :mdsId ) ")
        .append("          ) c ")
        .append("   LEFT OUTER JOIN CUSTOMER cust ")
        .append("     ON cust.ID = c.CUSTOMER_ID ")
//        .append("   LEFT OUTER JOIN CODE cd2 ON cust.CUSTOMTYPE_ID = cd2.ID ")
        .append("  WHERE 1 = 1 ");

        if(!"".equals(customerNo))   sb.append("    AND cust.customerNo = :customerNo ");
        if(!"".equals(customerName)) sb.append("    AND UPPER(cust.NAME) = UPPER(:customerName) ");
        if(!"".equals(locationId))   sb.append("    AND c.LOCATION_ID = :locationId ");
        if(!"".equals(address))      sb.append("    AND cust.ADDRESS like :address ");
        if(!"".equals(serviceType))  sb.append("    AND c.SERVICETYPE_ID = :serviceType ");
//        if(!"".equals(customType))   sb.append("    AND cust.CUSTOMTYPE_ID = :customType ");
        if(!"".equals(sicId))   sb.append("    AND c.sic_id = :sicId ");

        SQLQuery query = getSession().createSQLQuery(sb.toString());

        query.setString("supplierId", supplierId);
        if(!"".equals(customerNo))   query.setString("customerNo", customerNo);
        if(!"".equals(customerName)) query.setString("customerName", customerName);
        if(!"".equals(locationId))   query.setInteger("locationId", Integer.parseInt(locationId));
        if(!"".equals(address))      query.setString("address", "%" + address + "%");
        if(!"".equals(serviceType))  query.setInteger("serviceType", Integer.parseInt(serviceType));
        if(!"".equals(mdsId))        query.setString("mdsId", mdsId);
        if(!"".equals(sicId))   query.setInteger("sicId", Integer.parseInt(sicId));

        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

    /* (non-Javadoc)
     * @see com.aimir.dao.system.ContractDao#getContractList(com.aimir.model.system.Contract)
     */
    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly=true, propagation=Propagation.REQUIRED)
    public List<Contract> getContractList(Contract contract) {

        Criteria criteria = getSession().createCriteria(Contract.class);

        if (contract != null) {

            if (contract.getContractNumber() != null) {

                criteria.add(Restrictions.eq("contractNumber", contract.getContractNumber()));
            }

            if (contract.getSupplier() != null) {

                criteria.add(Restrictions.eq("supplier.id", contract.getSupplier().getId()));
            }

            if (contract.getServiceTypeCode() != null) {

                criteria.add(Restrictions.eq("serviceTypeCode.id", contract.getServiceTypeCode().getId()));
            }
        }

        List<Contract> contracts = criteria.list();

        return contracts;
    }


    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Map<String, Object>> getMdsIdFromContractNumber(Map<String, Object> conditionMap) {
        String startContract = StringUtil.nullToBlank(conditionMap.get("startContract"));
        String endContract = StringUtil.nullToBlank(conditionMap.get("endContract"));
        StringBuilder sb = new StringBuilder("SELECT m.mds_Id ");
        sb.append(" FROM CONTRACT c, METER m ");
        sb.append(" WHERE c.meter_Id = m.id ");

        if ( startContract != null && !startContract.equals("") ) {
            sb.append(" AND c.contract_Number >= :startContract ");
        }

        if ( endContract != null && !endContract.equals("") ) {
            sb.append(" AND c.contract_Number <= :endContract ");
        }
        SQLQuery query = getSession().createSQLQuery(sb.toString());
        query.setString("startContract", startContract);
        query.setString("endContract", endContract);
        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }
    /**
     * method name : getPrepaymentContract
     * method Desc : 선불 웹서비스에서 조회하는 계약 리스트
     *
     * @return
     */
    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRED)
    public List<Contract> getPrepaymentContract(Map<String, Object> conditionMap) {
        List<Contract> returnData = null;
        try {
            String contractNumber = (String)conditionMap.get("contractNumber");
            String supplierName = (String)conditionMap.get("supplierName");
            String mdsId = (String)conditionMap.get("mdsId");
            String keyNum = (String)conditionMap.get("keyNum");
            Boolean emergencyCreditYn = (Boolean)conditionMap.get("emergencyCreditYn");
    
            StringBuilder sb = new StringBuilder();
    
            sb.append("\nFROM Contract c ");
            sb.append("\nWHERE c.contractNumber = :contractNumber ");
            sb.append("\nAND   c.supplier.name = :supplierName ");
            if (!StringUtil.nullToBlank(mdsId).isEmpty()) {
                sb.append("\nAND   c.meter.mdsId = :mdsId ");
            }
            if (!StringUtil.nullToBlank(keyNum).isEmpty()) {
                sb.append("\nAND   c.keyNum = :keyNum ");
            }
            if (!StringUtil.nullToBlank(emergencyCreditYn).isEmpty()) {
                sb.append("\nAND   c.emergencyCreditAvailable = :emergencyCreditYn ");
            }
    
            Query query = getSession().createQuery(sb.toString());
    
            query.setString("contractNumber", contractNumber);
            query.setString("supplierName", supplierName);
    
            if (!StringUtil.nullToBlank(mdsId).isEmpty()) {
                query.setString("mdsId", mdsId);
            }
            if (!StringUtil.nullToBlank(keyNum).isEmpty()) {
                query.setString("keyNum", keyNum);
            }
            if (!StringUtil.nullToBlank(emergencyCreditYn).isEmpty()) {
                query.setBoolean("emergencyCreditYn", emergencyCreditYn);
            }
            returnData = query.list();
        } catch(Exception e) {
            logger.error(e,e);
        }
        return returnData;  
    }

    /**
     * method name : getPrepaymentContractBalanceInfo
     * method Desc : 선불 웹서비스에서 조회하는 현재 잔액정보
     *
     * @return
     */
    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Map<String, Object>> getPrepaymentContractBalanceInfo(Map<String, Object> conditionMap) {

        String contractNumber = (String)conditionMap.get("contractNumber");
        String supplierName = (String)conditionMap.get("supplierName");
        String mdsId = (String)conditionMap.get("mdsId");
        String keyNum = (String)conditionMap.get("keyNum");

        logger.info("condition : " + conditionMap);

        StringBuilder sb = new StringBuilder();
        sb.append("\nSELECT c.supplier.name AS supplierName, ");
        sb.append("\n       c.contractNumber AS contractNumber, ");
        sb.append("\n       c.meter.mdsId AS mdsId, ");
        sb.append("\n       c.creditType.code AS creditTypeCode, ");
        sb.append("\n       c.currentCredit AS currentCredit, ");
        sb.append("\n         c.currentArrears AS currentArrears, ");
        sb.append("\n       c.emergencyCreditAvailable AS emergencyYn, ");

        // 수정 - 2011.11.25 : GasMeter/WaterMeter 차단여부 체크컬럼 변경.
        // 미터기 차단여부(0:차단, 1:개방, 2:대기)
        // EnergyMeter
        sb.append("\n       CASE WHEN c.serviceTypeCode.code = '3.1' AND (c.meter.switchStatus = 1 OR c.meter.switchStatus IS NULL) ");
        sb.append("\n                 THEN 1 ");
        // GasMeter
        sb.append("\n            WHEN c.serviceTypeCode.code = '3.3' ");
        sb.append("\n                 THEN CASE WHEN c.meter.meterStatus.code = '1.3.1.3.1.0' OR c.meter.meterStatus.code = '1.3.1.3.1.1' ");
        sb.append("\n                                THEN 1 ");
        sb.append("\n                           WHEN c.meter.meterStatus.code = '1.3.1.3.1.3' ");
        sb.append("\n                                THEN 2 ");
        sb.append("\n                      ELSE 0 END ");
        // WaterMeter
        sb.append("\n            WHEN c.serviceTypeCode.code = '3.2' ");
        sb.append("\n                 THEN CASE WHEN c.meter.meterStatus.code = '1.3.1.2.1.0' OR c.meter.meterStatus.code = '1.3.1.2.1.1' ");
        sb.append("\n                                THEN 1 ");
        sb.append("\n                           WHEN c.meter.meterStatus.code = '1.3.1.2.1.3' ");
        sb.append("\n                                THEN 2 ");
        sb.append("\n                      ELSE 0 END ");
        sb.append("\n       ELSE 0 END AS switchStatus ");

        sb.append("\nFROM Contract c ");
        sb.append("\nWHERE c.contractNumber = :contractNumber ");
        sb.append("\nAND   c.supplier.name = :supplierName ");
        if (!StringUtil.nullToBlank(mdsId).isEmpty()) {
            sb.append("\nAND   c.meter.mdsId = :mdsId ");
        }
        if (!StringUtil.nullToBlank(keyNum).isEmpty()) {
            sb.append("\nAND   c.keyNum = :keyNum ");
        }

        Query query = getSession().createQuery(sb.toString());

        query.setString("contractNumber", contractNumber);
        query.setString("supplierName", supplierName);
        if (!StringUtil.nullToBlank(mdsId).isEmpty()) {
            query.setString("mdsId", mdsId);
        }
        if (!StringUtil.nullToBlank(keyNum).isEmpty()) {
            query.setString("keyNum", keyNum);
        }

        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

    /**
     * method name : getBalanceMonitorContract
     * method Desc : 잔액모니터링 스케줄러에서 조회하는 계약 리스트
     *
     * @return
     */
    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRED)
    public List<Contract> getBalanceMonitorContract() {
        StringBuilder sb = new StringBuilder();

        sb.append("\nFROM Contract c ");
        sb.append("\nWHERE 1=1 ");
//        sb.append("\nAND   ((c.meter.class = 'EnergyMeter' ");              // Discriminator value : EnergyMeter
//        sb.append("\n    AND (c.meter.switchStatus = 1 ");                  // Activation:1(차단여부)
//        sb.append("\n      OR c.meter.switchStatus IS NULL)) ");
//        sb.append("\n    OR (c.meter.class = 'GasMeter' ");                 // Discriminator value : GasMeter
//        sb.append("\n    AND (c.meter.valveStatus = 0 ");                   // on : 0 , off : 1, 대기상태 : 2
//        sb.append("\n      OR c.meter.valveStatus IS NULL))) ");

        // 수정 - 2011.11.29 : GasMeter/WaterMeter 차단여부 체크컬럼 변경.
        // 미터기 차단여부(0:차단, 1:개방, 2:대기)
        // EnergyMeter
        sb.append("\nAND   CASE WHEN c.meter.class = 'EnergyMeter' AND (c.meter.switchStatus = 1 OR c.meter.switchStatus IS NULL) ");
        sb.append("\n                THEN 1 ");
        // GasMeter
        sb.append("\n           WHEN c.meter.class = 'GasMeter' ");
        sb.append("\n                THEN CASE WHEN c.meter.meterStatus.code = '1.3.1.3.1.0' OR c.meter.meterStatus.code = '1.3.1.3.1.1' ");
        sb.append("\n                               THEN 1 ");
        sb.append("\n                          WHEN c.meter.meterStatus.code = '1.3.1.3.1.3' ");
        sb.append("\n                               THEN 2 ");
        sb.append("\n                     ELSE 0 END ");
        // WaterMeter
        sb.append("\n           WHEN c.meter.class = 'WaterMeter' ");
        sb.append("\n                THEN CASE WHEN c.meter.meterStatus.code = '1.3.1.2.1.0' OR c.meter.meterStatus.code = '1.3.1.2.1.1' ");
        sb.append("\n                               THEN 1 ");
        sb.append("\n                          WHEN c.meter.meterStatus.code = '1.3.1.2.1.3' ");
        sb.append("\n                               THEN 2 ");
        sb.append("\n                     ELSE 0 END ");
        sb.append("\n      ELSE 0 END = 1 ");   // 개방
        sb.append("\nAND   (c.creditType.code = '2.2.1' ");              // 선불
        sb.append("\nOR   c.creditType.code = '2.2.2') ");              // 긴급
        // sb.append("\nAND   c.status.code = '2.1.0' ");                  // 공급 상태 : Normal
        sb.append("\nAND   (c.meter.prepaymentMeter = false ");             // 선불미터기 제외
        sb.append("\n    OR c.meter.prepaymentMeter is NULL) ");
        sb.append("\nAND   c.serviceTypeCode.code IN ('3.1', '3.2', '3.3') ");     // Service Type 이 전기, 가스, 수도 조회
        List<Contract> result = getSession().createQuery(sb.toString()).list();

        return result;
    }

    /**
     * method name : getEmergencyCreditMonitorContract
     * method Desc : Emergency Credit 모니터링 스케줄러에서 조회하는 Emergency Credit 모드인 계약 리스트
     *
     * @return
     */
    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRED)
    public List<Contract> getEmergencyCreditMonitorContract() {
        StringBuilder sb = new StringBuilder();

        sb.append("\nFROM Contract c ");
        sb.append("\nWHERE 1=1 ");
        sb.append("\nAND   c.creditType.code = '2.2.2' ");      // Emergency Credit

        List<Contract> result = getSession().createQuery(sb.toString()).list();

        return result;
    }

    /**
     * method name : getEmergencyCreditContractList
     * method Desc : 관리자 선불관리 미니가젯의 Emergency Credit Mode 고객 리스트를 조회한다.
     *
     * @param conditionMap
     * @param isCount total count 여부
     * @return
     */
    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Map<String, Object>> getEmergencyCreditContractList(Map<String, Object> conditionMap, boolean isCount) {
        List<Map<String, Object>> result;
        Map<String, Object> map;
        Integer page = (Integer)conditionMap.get("page");
        Integer limit = (Integer)conditionMap.get("limit");
        Integer supplierId = (Integer)conditionMap.get("supplierId");

        StringBuilder sb = new StringBuilder();

        if (isCount) {
            sb.append("\nSELECT COUNT(*) AS cnt ");
        } else {
            sb.append("\nSELECT c.id AS contractId, ");
            sb.append("\n       c.contractNumber AS contractNumber, ");
            sb.append("\n       c.customer.name AS customerName, ");
            // sb.append("\n       c.customer.address2 AS address, ");
            sb.append("\n       m.mdsId AS mdsId, ");
            sb.append("\n       c.lastTokenDate AS lastTokenDate, ");
            sb.append("\n       c.emergencyCreditStartTime AS emergencyCreditStartTime, ");
            sb.append("\n       c.emergencyCreditMaxDuration AS emergencyCreditMaxDuration, ");
            sb.append("\n       c.creditType.code AS creditTypeCode, ");
            sb.append("\n       c.creditType.descr AS creditTypeName, ");
            sb.append("\n       co.code AS statusCode, ");
            sb.append("\n       co.descr AS statusName ");
        }
        sb.append("\nFROM Contract c LEFT OUTER JOIN c.meter m LEFT OUTER JOIN c.status co");
        sb.append("\nWHERE c.creditType.code = '2.2.2' ");  // Emergency Credit Mode
//        sb.append("\nWHERE c.creditType.code = '2.2.1' ");  // TEST
        sb.append("\nAND c.customer.supplier.id = :supplierId ");  // Emergency Credit Mode
        if (!isCount) {
            sb.append("\nORDER BY c.contractNumber ");
        }

        Query query = getSession().createQuery(sb.toString());
        query.setInteger("supplierId", supplierId);
        if (isCount) {
            map = new HashMap<String, Object>();
            map.put("total", query.uniqueResult());
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
     * method name : getPrepaymentContractStatusChartData
     * method Desc : 관리자 선불관리 미니가젯의 선불고객 Pie Chart Data 를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Map<String, Object>> getPrepaymentContractStatusChartData(Map<String, Object> conditionMap) {
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        StringBuilder sb = new StringBuilder();

//        sb.append("\nSELECT cd.code AS contractStatus, ");
//        sb.append("\n       COUNT(co.id) AS cnt ");
//        sb.append("\nFROM Contract co ");
//        sb.append("\n     LEFT OUTER JOIN ");
//        sb.append("\n     co.status cd ");
//        sb.append("\nWHERE co.contractDate <= :today ");
//        sb.append("\nAND   co.creditType.code IN ('2.2.1', '2.2.2') ");
//        sb.append("\nAND   co.supplier.id = :supplierId ");
//        sb.append("\nAND   co.customer.id IS NOT NULL ");
//        sb.append("\nAND   co.tariffIndex.id IS NOT NULL ");
//        sb.append("\nGROUP BY cd.code ");

        sb.append("\nSELECT cd.code AS contractStatus, ");
        sb.append("\n       COUNT(co.id) AS cnt ");
        sb.append("\nFROM Contract co ");
        sb.append("\n     LEFT OUTER JOIN ");
        sb.append("\n     co.status cd ");
        sb.append("\nWHERE co.creditType.code IN ('2.2.1', '2.2.2') ");
        sb.append("\nAND   co.supplier.id = :supplierId ");
        sb.append("\nAND   co.customer.id IS NOT NULL ");
//        sb.append("\nAND   co.tariffIndex.id IS NOT NULL ");
        sb.append("\nGROUP BY cd.code ");

        Query query = getSession().createQuery(sb.toString());
//        query.setString("today", today);
        query.setInteger("supplierId", supplierId);
        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

    /**
     * method name : getPrepaymentContractList
     * method Desc : 관리자 선불관리 맥스가젯의 선불계약 고객 리스트를 조회한다.
     *
     * @param conditionMap
     * @param isCount total count 여부
     * @return
     */
    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Map<String, Object>> getPrepaymentContractList(Map<String, Object> conditionMap, boolean isCount) {
        List<Map<String, Object>> result;
        Map<String, Object> map;

        List<Object> contractList = null;
        if(conditionMap.get("contractNumberList") != null){
            contractList = (List<Object>) conditionMap.get("contractNumberList");
        }

        String contractNumber = (String) conditionMap.get("contractNumber");
        String customerName = (String) conditionMap.get("customerName");
        String customerNumber = (String) conditionMap.get("customerNumber");
        String statusCode 	= (String) conditionMap.get("statusCode");
        String meterStatus 	= (String) conditionMap.get("meterStatus");
        String amountStatus = StringUtil.nullToBlank(conditionMap.get("amountStatus"));
        String mdsId 		= (String) conditionMap.get("mdsId");
        String gs1 			= (String) conditionMap.get("gs1");
        Integer[] locationCondition = (Integer[]) conditionMap.get("locationCondition");
        // String address = (String)conditionMap.get("address");
        String serviceTypeCode = (String) conditionMap.get("serviceTypeCode");
        String searchLastChargeDate = StringUtil.nullToBlank(conditionMap.get("searchLastChargeDate"));
        String lastChargeStartDate = (String) conditionMap.get("lastChargeStartDate");
        String lastChargeEndDate = (String) conditionMap.get("lastChargeEndDate");
        Integer page = (Integer) conditionMap.get("page");
        Integer limit = (Integer) conditionMap.get("limit");
        Integer supplierId = (Integer) conditionMap.get("supplierId");

        StringBuilder sb = new StringBuilder();

        if (isCount) {
            sb.append("\nSELECT COUNT(*) AS cnt ");
        } else {
            sb.append("\nSELECT c.contractNumber AS contractNumber, ");                        // Contract No.
            sb.append("\n       c.customer.name AS customerName, ");                           // Customer Name
            sb.append("\n       c.customer.customerNo AS customerNumber, ");                   // customerNo
            sb.append("\n       c.customer.mobileNo AS mobileNo, ");                           // mobile No.
            sb.append("\n       m.meterStatus.name AS meterStatus, ");                              // Meter Status
            sb.append("\n       m.mdsId AS mdsId, ");                                          // Meter ID
            sb.append("\n       m.id AS meterId, ");
            sb.append("\n       m.gs1 AS gs1, ");  
            sb.append("\n       model.name AS modelName, ");
            sb.append("\n       mcu.sysID AS mcuId, ");
            sb.append("\n       c.serviceTypeCode.code AS serviceTypeCode, ");
            sb.append("\n       c.serviceTypeCode.descr AS serviceTypeName, ");                 // Supply Type
            sb.append("\n       c.creditType.code AS creditTypeCode, ");                        // Emergency Credit Mode?
            sb.append("\n       c.creditType.descr AS creditTypeName, ");
            sb.append("\n       t.name AS tariffTypeName, ");                                   // Tariff Type
            sb.append("\n       c.prepaymentPowerDelay AS prepaymentPowerDelay, ");
            sb.append("\n       c.lastTokenDate AS lastTokenDate, ");                           // Last Charge Date
            sb.append("\n       c.currentCredit AS currentCredit, ");                           // Remaining Credit(RAND)
            sb.append("\n       co.descr AS statusName, ");                                     // Supply Status 
            sb.append("\n       c.emergencyCreditStartTime AS emergencyCreditStartTime, ");     // for valid Date
            sb.append("\n       c.emergencyCreditMaxDuration AS emergencyCreditMaxDuration ");  // for valid Date
        }

        sb.append("\nFROM Contract c LEFT OUTER JOIN c.meter m left outer join c.status co left outer join c.tariffIndex t left outer join m.modem mo left outer join mo.mcu mcu left outer join m.model model ");
        sb.append("\nWHERE c.creditType.code IN ('2.2.1', '2.2.2') ");
        sb.append("\nAND c.customer.supplier.id = :supplierId ");

        if(contractList != null && 0 < contractList.size()){
            sb.append("\nAND c.contractNumber IN (");
            String tempStr = "";
            for(int i=0; i<contractList.size(); i++){
                tempStr = "'" + contractList.get(i) + "',";
            }
            tempStr = tempStr.substring(0, tempStr.length() -1);
            sb.append(tempStr + ")");
        }else if (contractNumber !=null && !contractNumber.isEmpty()) {
            sb.append("\nAND   c.contractNumber LIKE :contractNumber||'%' ");
        }

        if (mdsId != null && !mdsId.isEmpty()) {
            sb.append("\nAND   c.meter.mdsId LIKE :mdsId||'%' ");
        }
        
        if (meterStatus != null && !meterStatus.isEmpty()) {
            sb.append("\nAND   c.meter.meterStatus = :meterStatus ");
        }

        if (gs1 != null && !gs1.isEmpty()) {
            sb.append("\nAND   c.meter.gs1 LIKE :gs1 ");
        }
        
        if (locationCondition != null && locationCondition.length > 0) {
            sb.append("\nAND   c.location.id IN (:locationCondition) ");
        }

        if (customerName != null && !customerName.isEmpty()) {
            sb.append("\nAND   UPPER(c.customer.name) LIKE UPPER('%'||:customerName||'%') ");
        }
        
        if (customerNumber !=null && !customerNumber.isEmpty()) {
            sb.append("\nAND   c.customer.customerNo LIKE :customerNumber||'%' ");
        }

        if (statusCode != null && !statusCode.isEmpty()) {
            sb.append("\nAND   c.status.id = :statusCode ");
        }
        // if (!address.isEmpty()) {
        // sb.append("\nAND   c.customer.address2 LIKE '%'||:address||'%' ");
        // }
        
        if(!"".equals(amountStatus) && "negative".equals(amountStatus)) {
            sb.append("\nAND (c.currentCredit is null OR c.currentCredit <= 0) ");
        } else if(!"".equals(amountStatus) && "positive".equals(amountStatus)) {
            sb.append("\nAND c.currentCredit > 0");
        }
        
        if("enable".equals(searchLastChargeDate) && !"".equals(lastChargeStartDate) && !"".equals(lastChargeEndDate)) {
            sb.append("AND (c.lastTokenDate BETWEEN :lastChargeStartDate AND :lastChargeEndDate )");
        }
        

        if (serviceTypeCode != null && !serviceTypeCode.isEmpty()) {
            sb.append("\nAND   c.serviceTypeCode.id = :serviceTypeCode ");
        }

        if (!isCount) {
            sb.append("\nORDER BY c.contractNumber ");
        }

        Query query = getSession().createQuery(sb.toString());

        query.setInteger("supplierId", supplierId);

        if (contractNumber != null && !contractNumber.isEmpty()) {
            query.setString("contractNumber", contractNumber);
        }
        if (customerNumber != null && !customerNumber.isEmpty()) {
            query.setString("customerNumber", customerNumber);
        }
        if (mdsId != null && !mdsId.isEmpty()) {
            query.setString("mdsId", mdsId);
        }
        if (meterStatus != null && !meterStatus.isEmpty()) {
            query.setInteger("meterStatus", Integer.parseInt(meterStatus));
        }
        if (gs1 != null && !gs1.isEmpty()) {
            query.setString("gs1", '%'+gs1+'%');
        }
        
        if (locationCondition != null && locationCondition.length > 0) {
            query.setParameterList("locationCondition", locationCondition);
        }

        if (customerName != null && !customerName.isEmpty()) {
            query.setString("customerName", customerName);
        }

        if (statusCode != null && !statusCode.isEmpty()) {
            query.setString("statusCode", statusCode);
        }
        // if (!address.isEmpty()) {
        // query.setString("address", address);
        // }

        if("enable".equals(searchLastChargeDate) && !"".equals(lastChargeStartDate) && !"".equals(lastChargeEndDate)) {
            query.setString("lastChargeStartDate",lastChargeStartDate+"000000");
            query.setString("lastChargeEndDate",lastChargeEndDate+"235959");
        }
        
        if (serviceTypeCode != null && !serviceTypeCode.isEmpty()) {
            query.setString("serviceTypeCode", serviceTypeCode);
        }

        if (isCount) {
            map = new HashMap<String, Object>();
            map.put("total", query.uniqueResult());
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
     * method name : getContractBySicId
     * method Desc : SIC 산업분류코드로 Contract list 를 조회한다.
     *
     * @param codeId
     * @return
     */
    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getContractBySicId(Integer codeId) {
        StringBuilder sb = new StringBuilder();

        sb.append("\nFROM Contract c ");
//        sb.append("\nWHERE c.customer.customTypeCode.id = :customType ");
        // TODO - customTypeId 변경 : Customer -> Contract 로 위치 변경, customtype_id -> sic_id 로 컬럼명 변경
        sb.append("\nWHERE c.sic.id = :sicId ");

        Query query = getSession().createQuery(sb.toString());

        query.setInteger("sicId", codeId);

        return query.list();
    }


    @Override
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public void updateSendResult(int contractId, String delayDay) {

        StringBuffer sb = new StringBuffer();
        sb.append("UPDATE Contract ");
        if(delayDay == null) {
            sb.append("SET delayDay = null ");
        } else {
            sb.append("SET delayDay = :delayDay ");
        }
        sb.append("WHERE id = :contractId ");

        //HQL문을 이용한 CUD를 할 경우에는 getHibernateTemplate().bulkUpdate() 메소드를 사용한다.
        Query query = getSession().createQuery(sb.toString());
        query.setInteger("contactId", contractId);
        if (delayDay != null) query.setString("delayDay", delayDay);
        
        query.executeUpdate();
        // bulkUpdate 때문에 주석처리
       /* if(delayDay == null) {
            this.getHibernateTemplate().bulkUpdate(sb.toString(), new Object[] {  contractId} );
        } else {
            this.getHibernateTemplate().bulkUpdate(sb.toString(), new Object[] {  delayDay, contractId} );
        }*/
    }

    @Override
    public void updateStatus(int contractId, Code code) {
        StringBuffer sb = new StringBuffer();
        sb.append("UPDATE Contract \n");
        sb.append("set status =:code \n");
        sb.append("WHERE id = :id");
        Query query = getSession().createQuery(sb.toString());
        query.setEntity("code", code);
        query.setInteger("id", contractId);
        query.executeUpdate();
    }

    @Override
    public void updateCurrentCredit(int contractId, double currentCredit) {
        StringBuffer sb = new StringBuffer();
        sb.append("UPDATE Contract \n");
        sb.append("set currentCredit =:currentCredit \n");
        sb.append("WHERE id = :id");
        Query query = getSession().createQuery(sb.toString());
        query.setDouble("currentCredit", currentCredit);
        query.setInteger("id", contractId);
        query.executeUpdate();
    }
    
    @Override
    public void updateCreditType(int contractId, Code code) {
        StringBuffer sb = new StringBuffer();
        sb.append("UPDATE Contract \n");
        sb.append("set creditType =:code \n");
        sb.append("WHERE id = :id");
        Query query = getSession().createQuery(sb.toString());
        query.setEntity("code", code);
        query.setInteger("id", contractId);
        query.executeUpdate();
    }

    @Override
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public void updateSmsNumber(int contractId, String msg) {
        StringBuffer sb = new StringBuffer();
        sb.append("UPDATE Contract \n");
        sb.append("set smsNumber =:msg \n");
        sb.append("WHERE id = :id");
        Query query = getSession().createQuery(sb.toString());
        query.setString("msg", msg);
        query.setInteger("id", contractId);
        query.executeUpdate();
    }
    
    @Override
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public void updateSMSPriod(int contractId, String msgId, String lastNotificationDate) {
        StringBuffer sb = new StringBuffer();
        sb.append("UPDATE Contract \n");
        sb.append("set smsNumber =:msg, \n");
        sb.append("    lastNotificationDate =:lastNotificationDate \n");
        sb.append("WHERE id = :id");
        Query query = getSession().createQuery(sb.toString());
        query.setString("msg", msgId);
        query.setString("lastNotificationDate", lastNotificationDate);
        query.setInteger("id", contractId);
        query.executeUpdate();
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Map<String, Object>> getDeliveryDelayContratInfo(int serviceTypeId) {

        SQLQuery query = null;

        StringBuilder sb = new StringBuilder();
        sb.append("\n SELECT " );
        sb.append("\n   c.id as ID, c.delay_day as DELAYDAY, c.bill_Date as BILLDATE, m.mds_id as meterId");
        sb.append("\n FROM contract c, meter m ");
        sb.append("\n WHERE c.delay_day is not null ");
        sb.append("\n AND c.servicetype_id = :typeId ");
        sb.append("\n AND c.meter_id = m.id ");
        sb.append("\n ORDER BY c.delay_day ");

        query = getSession().createSQLQuery(sb.toString());

        query.setInteger("typeId", serviceTypeId);

        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRED)
    public List<Contract> getContractIdByCustomerNo(String customerNo, String supplierName) {

        StringBuilder sb = new StringBuilder();

        sb.append("\nFROM Contract c ");
        sb.append("\nWHERE c.customer.customerNo = :customerNo ");
        sb.append("\nAND   c.supplier.name = :supplierName ");


        Query query = getSession().createQuery(sb.toString());

        query.setString("customerNo", customerNo);
        query.setString("supplierName", supplierName);

        return query.list();
//
//        return getHibernateTemplate().find(" FROM Contract c " +
//                                           " WHERE c.customer.customerNo = ? AND c.supplier.name = ?  ", new Object[]{customerNo, supplierName});
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Map<String, Object>> getPrepaymentContract() {

        StringBuffer sb = new StringBuffer();

        sb.append(" SELECT   c.contractNumber as CONTRACTNUMBER, c.supplier.id as SUPPLIERID ");
        sb.append(" FROM     Contract c ");
        sb.append(" WHERE    c.creditType.code = :perpay or c.creditType.code = :emergencyCredit  ");

        Query query = getSession().createQuery(sb.toString());
        query.setString("perpay", Code.PREPAYMENT);
        query.setString("emergencyCredit", Code.EMERGENCY_CREDIT);

        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();


    }

    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    @Override
    public List<Integer> getPrepaymentContract(String serviceType) {

        StringBuffer sb = new StringBuffer();

        sb.append(" SELECT   c.id as id ");
        sb.append(" FROM     Contract c ");
        sb.append(" WHERE    (c.creditType.code = :perpay or c.creditType.code = :emergencyCredit)  ");
        sb.append(" and c.meter is not null and c.serviceTypeCode.name = :serviceType ");
        sb.append(" and c.customer is not null ");

        Query query = getSession().createQuery(sb.toString());
        query.setString("perpay", Code.PREPAYMENT);
        query.setString("emergencyCredit", Code.EMERGENCY_CREDIT);
        query.setString("serviceType", serviceType);

        return query.list();
    }
    
    @Override
	public List<Contract> getContract(String payType, String serviceType) {
    	StringBuffer sb = new StringBuffer();

        sb.append(" FROM     Contract c ");
        sb.append(" WHERE    (c.creditType.code = :perpay or c.creditType.code = :emergencyCredit)");
        sb.append(" and c.status.code != :status ");
        sb.append(" and c.meter is not null and c.serviceTypeCode.name = :serviceType ");
        sb.append(" and c.customer is not null ");

        Query query = getSession().createQuery(sb.toString());
        query.setString("perpay", payType);
        query.setString("emergencyCredit", Code.EMERGENCY_CREDIT);
        query.setString("status", Code.TERMINATION);
        query.setString("serviceType", serviceType);

        return query.list();
	}

    @Override
    public int idOverlapCheck(String contractNo) {
    	Query query = getSession().createQuery("SELECT COUNT(c.contractNumber) FROM Contract c WHERE c.contractNumber = " + contractNo + " ");
        return DataAccessUtils.intResult(query.list());
    }

    /**
     * @desc 기존의 계약된 미터의 계약(Contract)을 가지고 온다.
     */
    @Transactional(readOnly=true, propagation=Propagation.REQUIRED)
    public Contract getPreviousContractMeter(String meterId) {
        StringBuilder sbQuery = new StringBuilder();
        sbQuery.append("select * from Contract con where con.meterId = :meterId");

        Query query = getSession().createQuery(sbQuery.toString());

        //preparestatement setting
        query.setString("meterId", meterId);

        //execute
        Contract contract = (Contract)query.uniqueResult();

        return contract;
    }

    /**
     * @desc: 미터의 계약 여부를 체크
     */
    @Override
    @Deprecated
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public int checkContractedMeterYn(String meterId) {

        StringBuilder sbQuery = new StringBuilder();

        /**
         *
         * --계약된 매터 아이디 인지 여부를 체크 select count(*) from Contract con, Meter mt where con.meterId = mt.id and con.meterId=3001
         */

        sbQuery.append("select count(*) from Contract con, Meter mt where con.meterId = mt.id and con.meterId = :meterId");

        Query query = getSession().createQuery(sbQuery.toString());

        // preparestatement setting
        query.setString("meterId", meterId);

        // execute
        Number cnt = (Number) query.uniqueResult();

        return cnt.intValue();
    }

    /**
     * method name : checkContractMeterYn<b/>
     * method Desc : 계약된 Meter 아이디 인지 여부를 체크한다.
     *
     * @param meterId Meter.mdsId - 미터 아이디
     * @return
     */
    @Override
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public int checkContractMeterYn(String meterId) {
        StringBuilder sbQuery = new StringBuilder();

        sbQuery.append("SELECT COUNT(*) ");
        sbQuery.append("FROM Contract con ");
        sbQuery.append("WHERE con.meter.mdsId = :meterId");

        Query query = getSession().createQuery(sbQuery.toString());
        query.setString("meterId", meterId);

        Number cnt = (Number) query.uniqueResult();
        return cnt.intValue();
    }

    @Override
    @Deprecated
    public void updateContractByMeterId(String meterId) {
        StringBuffer sb = new StringBuffer();
        sb.append("UPDATE Contract ");
    }

    /**
     * method name : getSicContractCountList<b/>
     * method Desc : SIC Load Profile 가젯에서 SIC Code 별 계약 건수를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Map<String, Object>> getSicContractCountList(Map<String, Object> conditionMap) {
        Integer supplierId = (Integer)conditionMap.get("supplierId");

        StringBuilder sb = new StringBuilder();

        sb.append("\nSELECT cd.code AS SIC_CODE, ");
        sb.append("\n       COUNT(ct.id) AS CONTRACT_CNT ");
        sb.append("\nFROM contract ct, ");
        sb.append("\n     code cd ");
        sb.append("\nWHERE 1=1 ");
        sb.append("\nAND   ct.sic_id = cd.id ");
        sb.append("\nAND   ct.supplier_id = :supplierId ");
        sb.append("\nGROUP BY cd.code, cd.id ");

        SQLQuery query = getSession().createSQLQuery(new SQLWrapper().getQuery(sb.toString()));
        query.setInteger("supplierId", supplierId);

        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

    /**
     * method name : getPrepaymentChargeContractList<b/>
     * method Desc : Prepayment Charge 화면에서 Prepayment Charge Contract List 를 조회한다.
     *
     * @param conditionMap
     * @param isCount
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Map<String, Object>> getPrepaymentChargeContractList(Map<String, Object> conditionMap, boolean isCount) {
        List<Map<String, Object>> result;
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        String barcode = StringUtil.nullToBlank(conditionMap.get("barcode"));
        String contractNumber = StringUtil.nullToBlank(conditionMap.get("contractNumber"));
        String phone = StringUtil.nullToBlank(conditionMap.get("phone"));
        String customerNo = StringUtil.nullToBlank(conditionMap.get("customerNo"));
        String customerName = StringUtil.nullToBlank(conditionMap.get("customerName"));
        String contractStatus = StringUtil.nullToBlank(conditionMap.get("contractStatus"));
        String meterStatus = StringUtil.nullToBlank(conditionMap.get("meterStatus"));
        String mdsId = StringUtil.nullToBlank(conditionMap.get("mdsId"));
        Integer page = (Integer)conditionMap.get("page");
        Integer limit = (Integer)conditionMap.get("limit");

        StringBuilder sb = new StringBuilder();

        if(isCount) {
            sb.append("\nSELECT count(cont.customer.id) as total");
        } else {
            sb.append("\nSELECT cont.id AS contractId, ");
            sb.append("\n       meter.id AS meterId, ");
            sb.append("\n       cust.id AS customerId, ");
            sb.append("\n       cont.contractNumber AS contractNumber, ");
            sb.append("\n       cont.currentCredit AS currentCredit, ");
            sb.append("\n       cont.currentArrears AS currentArrears, ");
            sb.append("\n       cont.currentArrears2 AS currentArrears2, ");
            sb.append("\n       cont.contractPrice AS contractPrice, ");
            sb.append("\n       cust.customerNo AS customerNo, ");
            sb.append("\n       cust.mobileNo AS phone, ");
            sb.append("\n       cust.name AS customerName, ");
            sb.append("\n       cont.barcode AS barcode, ");
    //        sb.append("\n       cust.address AS address, ");
    //        sb.append("\n       cust.address1 AS address1, ");
            sb.append("\n       cust.address2 AS address, ");
    //        sb.append("\n       cust.address3 AS address3, ");
            sb.append("\n       meter.mdsId AS mdsId, ");
            sb.append("\n       cont.lastTokenDate AS lastTokenDate, ");
            sb.append("\n       cont.lastTokenId AS lastTokenId, ");
            sb.append("\n       cont.contractDemand AS contractDemand, ");
            sb.append("\n       cont.tariffIndex.code AS tariffCode, ");
            sb.append("\n       mcu.sysID AS mcuId, ");
            sb.append("\n       cont.chargeAvailable AS chargeAvailable, ");
            sb.append("\n       meterStatus.descr AS meterStatus, ");
            sb.append("\n       stat.descr AS statusName ");
        }
        sb.append("\nFROM Contract cont ");
        sb.append("\n     LEFT OUTER JOIN ");
        sb.append("\n     cont.meter meter ");
        sb.append("\n     LEFT OUTER JOIN ");
        sb.append("\n     meter.meterStatus meterStatus ");
        sb.append("\n     LEFT OUTER JOIN ");
        sb.append("\n     meter.modem modem");
        sb.append("\n     LEFT OUTER JOIN ");
        sb.append("\n     modem.mcu mcu");
        sb.append("\n     LEFT OUTER JOIN ");
        sb.append("\n     cont.status stat");
        sb.append("\n     LEFT OUTER JOIN ");
        sb.append("\n     cont.customer cust");

        sb.append("\nWHERE cont.supplier.id = :supplierId ");
        sb.append("\nAND   cont.creditType.code IN ('2.2.1', '2.2.2') ");   // prepay, emergency credit

        if (!contractNumber.isEmpty()) {
            sb.append("\nAND   cont.contractNumber LIKE :contractNumber ");
        }
        if (!phone.isEmpty()) {
            sb.append("\nAND   cust.mobileNo = :phone ");
        }
        if (!customerNo.isEmpty()) {
            sb.append("\nAND   cont.customer.customerNo LIKE :customerNo ");
        }
        if (!customerName.isEmpty()) {
            sb.append("\nAND   UPPER(cont.customer.name) LIKE UPPER(:customerName) ");
        }
        if (!mdsId.isEmpty()) {
            sb.append("\nAND   meter.mdsId LIKE :mdsId ");
        }
        if (!barcode.isEmpty()) {
            sb.append("\nAND   cont.barcode = :barcode ");
        }
        if (!meterStatus.isEmpty()) {
        	sb.append("\nAND   meter.meterStatus = :meterStatus ");
        }
        if (!contractStatus.isEmpty()) {
        	sb.append("\nAND   cont.status = :contractStatus ");
        }
        
        if (!isCount) {
            sb.append("\nORDER BY cont.contractNumber ");
        } else {
            sb.append("\nAND   cont.tariffIndexId is not null  ");
        }

        Query query = getSession().createQuery(sb.toString());
        query.setInteger("supplierId", supplierId);

        if (!contractNumber.isEmpty()) {
            query.setString("contractNumber", contractNumber + "%");
        }
        if (!customerNo.isEmpty()) {
            query.setString("customerNo", customerNo + "%");
        }
        if (!customerName.isEmpty()) {
            query.setString("customerName", customerName + "%");
        }
        if (!phone.isEmpty()) {
            query.setString("phone", phone);
        }
        if (!mdsId.isEmpty()) {
            query.setString("mdsId", mdsId + "%");
        }
        if (!barcode.isEmpty()) {
            query.setString("barcode", barcode);
        }
        if (!meterStatus.isEmpty()) {
            query.setString("meterStatus", meterStatus);
        }
        if (!contractStatus.isEmpty()) {
            query.setString("contractStatus", contractStatus);
        }
        if (isCount) {
            Map<String, Object> map = new HashMap<String, Object>();
            Number count = null;
            if(query.list() != null && query.list().size() > 0) {
                count = (Number) query.list().get(0);
            }
            map.put("total", count.intValue());
            result = new ArrayList<Map<String, Object>>();
            result.add(map);

//            Map<String, Object> map = new HashMap<String, Object>();
//            int count = 0;
//            Iterator<?> itr = query.iterate();
//            while(itr.hasNext()) {
//                itr.next();
//                count++;
//            }
//            map.put("total", count);
//            result = new ArrayList<Map<String, Object>>();
//            result.add(map);
        } else {
            query.setFirstResult((page - 1) * limit);
            query.setMaxResults(limit);
            result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        }

        return result;
    }


    /**
     * method name : getContractSMSYN<b/>
     * method Desc : sms통보에 동의하고 모바일번호를 기입한 선불고객응 검색
     *
     * @param conditionMap
     * @return
     */
    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true)
    public List<Map<String, Object>> getContractSMSYN(Map<String, Object> conditionMap) {
        SQLQuery  query = null;

        Integer prepayCreditId = (Integer)conditionMap.get("prepayCreditId");
        Integer emergencyICreditId = (Integer)conditionMap.get("emergencyICreditId");
        Boolean smsYn = (Boolean)conditionMap.get("smsYn");
        Integer contractId = conditionMap.get("contractId") == null ? -1 : Integer.parseInt(conditionMap.get("contractId").toString());
        Boolean isRecovery = conditionMap.get("isRecovery") == null ? false : (Boolean)conditionMap.get("isRecovery");

        StringBuilder sb = new StringBuilder();

        try {
            sb.append("\n   SELECT cu.name as CUSTOMERNAME, ");
            sb.append("\n   cu.mobileNo as MOBILENO, ");
            sb.append("\n   c.name as SERVICETYPE, ");
            sb.append("\n   co.prepaymentThreshold as PREPAYMENTTHRESHOLD, ");
            sb.append("\n   co.currentCredit as CURRENTCREDIT, ");
            sb.append("\n   co.currentArrears as CURRENTARREARS, ");
            sb.append("\n   co.customer_Id as CUSTOMERID, ");
            sb.append("\n   co.id as CONTRACTID, ");
            sb.append("\n   co.NOTIFICATION_INTERVAL as NOTIFICATIONINTERVAL, ");
            sb.append("\n   co.NOTIFICATION_TIME as NOTIFICATIONTIME, ");
            sb.append("\n   co.NOTIFICATION_PERIOD as NOTIFICATIONPERIOD, ");
            sb.append("\n   co.LAST_NOTIFICATION_DATE as LASTNOTIFICATIONDATE, ");
            sb.append("\n   co.NOTIFICATION_WEEKLY_SUN as NOTIFICATIONWEEKLYSUN, ");
            sb.append("\n   co.NOTIFICATION_WEEKLY_MON as NOTIFICATIONWEEKLYMON, ");
            sb.append("\n   co.NOTIFICATION_WEEKLY_TUE as NOTIFICATIONWEEKLYTUE, ");
            sb.append("\n   co.NOTIFICATION_WEEKLY_WED as NOTIFICATIONWEEKLYWED, ");
            sb.append("\n   co.NOTIFICATION_WEEKLY_THU as NOTIFICATIONWEEKLYTHU, ");
            sb.append("\n   co.NOTIFICATION_WEEKLY_FRI as NOTIFICATIONWEEKLYFRI, ");
            sb.append("\n   co.NOTIFICATION_WEEKLY_SAT as NOTIFICATIONWEEKLYSAT, ");
            sb.append("\n   m.mds_Id as METERID ");
            sb.append("\n   FROM contract co left outer join Meter m on(co.meter_id=m.id), customer cu, code c");
            sb.append("\n   WHERE 1=1 ");
            sb.append("\n   AND   co.customer_id = cu.id ");
            sb.append("\n   AND   co.servicetype_id = c.id ");
            sb.append("\n   AND   cu.mobileNo IS NOT NULL ");
            sb.append("\n   AND   cu.smsYn = :smsYn ");
            sb.append("\n   AND   (co.credittype_id = :prepayCreditId ");
            sb.append("\n   OR   co.credittype_id = :emergencyICreditId )");
            
            if(contractId != -1) {
                sb.append("\n   AND   co.id = :contractId");
            }
            
            if(isRecovery) {
                sb.append("\n   AND   co.SMS_NUMBER = :failDailySMS");
            }

            query = getSession().createSQLQuery(sb.toString());
            query.setBoolean("smsYn", smsYn);
            query.setInteger("prepayCreditId", prepayCreditId);
            query.setInteger("emergencyICreditId", emergencyICreditId);

            if(contractId != -1) {
                query.setInteger("contractId", contractId);
            }

            if(isRecovery) {
                query.setString("failDailySMS", "fail:prepaySendSMS");
            }

        } catch (Exception e) {
            logger.error(e,e);
        }
        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

    }

    /**
     * method name : getContractSMSYN<b/>
     * method Desc : sms통보에 동의하고 모바일번호를 기입한 선불고객 중 그룹에 묶여있는 고객을 검색
     *
     * @param conditionMap
     * @return
     */
    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Map<String, Object>> getContractSMSYNWithGroup(Map<String, Object> conditionMap) {
        SQLQuery  query = null;

        Integer prepayCreditId = (Integer)conditionMap.get("prepayCreditId");
        Integer emergencyICreditId = (Integer)conditionMap.get("emergencyICreditId");
        Boolean smsYn = (Boolean)conditionMap.get("smsYn");

        StringBuilder sb = new StringBuilder();

        try {
            sb.append("\n   SELECT DISTINCT cu.name as CUSTOMERNAME, ");
            sb.append("\n   cu.mobileNo as MOBILENO, ");
            sb.append("\n   c.name as SERVICETYPE, ");
            sb.append("\n   g.mobileNo as GROUP_MOBILENO, ");
            sb.append("\n   co.prepaymentThreshold as PREPAYMENTTHRESHOLD, ");
            sb.append("\n   co.currentCredit as CURRENTCREDIT, ");
            sb.append("\n   co.currentArrears as CURRENTARREARS, ");
            sb.append("\n   co.customer_Id as CUSTOMERID, ");
            sb.append("\n   co.id as CONTRACTID, ");
            sb.append("\n   co.SMS_NUMBER as SMSNUMBER, ");
            sb.append("\n   co.NOTIFICATION_INTERVAL as NOTIFICATIONINTERVAL, ");
            sb.append("\n   co.NOTIFICATION_TIME as NOTIFICATIONTIME, ");
            sb.append("\n   co.NOTIFICATION_PERIOD as NOTIFICATIONPERIOD, ");
            sb.append("\n   co.LAST_NOTIFICATION_DATE as LASTNOTIFICATIONDATE, ");
            sb.append("\n   co.NOTIFICATION_WEEKLY_SUN as NOTIFICATIONWEEKLYSUN, ");
            sb.append("\n   co.NOTIFICATION_WEEKLY_MON as NOTIFICATIONWEEKLYMON, ");
            sb.append("\n   co.NOTIFICATION_WEEKLY_TUE as NOTIFICATIONWEEKLYTUE, ");
            sb.append("\n   co.NOTIFICATION_WEEKLY_WED as NOTIFICATIONWEEKLYWED, ");
            sb.append("\n   co.NOTIFICATION_WEEKLY_THU as NOTIFICATIONWEEKLYTHU, ");
            sb.append("\n   co.NOTIFICATION_WEEKLY_FRI as NOTIFICATIONWEEKLYFRI, ");
            sb.append("\n   co.NOTIFICATION_WEEKLY_SAT as NOTIFICATIONWEEKLYSAT ");
            sb.append("\n   FROM contract co left outer join GROUP_MEMBER gm on (co.CONTRACT_NUMBER=gm.member), AIMIRGROUP g");
            sb.append("\n                           , customer cu, code c");
            sb.append("\n   WHERE 1=1 ");
            sb.append("\n   AND   gm.group_id = g.id ");
            sb.append("\n   AND   g.group_type = :groupType ");
            sb.append("\n   AND   co.customer_id = cu.id ");
            sb.append("\n   AND   co.servicetype_id = c.id ");
            sb.append("\n   AND   cu.mobileNo IS NOT NULL ");
            sb.append("\n   AND   cu.smsYn = :smsYn ");
            sb.append("\n   AND   (co.credittype_id = :prepayCreditId ");
            sb.append("\n   OR   co.credittype_id = :emergencyICreditId )");
            sb.append("\n   AND  gm.member is not null");

            query = getSession().createSQLQuery(sb.toString());
            query.setBoolean("smsYn", smsYn);
            query.setInteger("prepayCreditId", prepayCreditId);
            query.setInteger("emergencyICreditId", emergencyICreditId);
            query.setString("groupType", "Contract");

        } catch (Exception e) {
            logger.error(e,e);
        }
        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

    }

    /**
     * method name : getContractSMSYN<b/>
     * method Desc : sms통보에 동의하고 모바일번호를 기입한 중 그룹에 묶여있지 않은 고객을 검색
     *
     * @param conditionMap
     * @return
     */
    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Map<String, Object>> getContractSMSYNNOTGroup(Map<String, Object> conditionMap) {
        SQLQuery  query = null;

        Integer prepayCreditId = (Integer)conditionMap.get("prepayCreditId");
        Integer emergencyICreditId = (Integer)conditionMap.get("emergencyICreditId");
        Boolean smsYn = (Boolean)conditionMap.get("smsYn");

        StringBuilder sb = new StringBuilder();

        try {
            sb.append("\n   SELECT DISTINCT cu.name as CUSTOMERNAME, ");
            sb.append("\n   cu.mobileNo as MOBILENO, ");
            sb.append("\n   c.name as SERVICETYPE, ");
            sb.append("\n   co.prepaymentThreshold as PREPAYMENTTHRESHOLD, ");
            sb.append("\n   co.currentCredit as CURRENTCREDIT, ");
            sb.append("\n   co.currentArrears as CURRENTARREARS, ");
            sb.append("\n   co.customer_Id as CUSTOMERID, ");
            sb.append("\n   co.id as CONTRACTID, ");
            sb.append("\n   co.SMS_NUMBER as SMSNUMBER, ");
            sb.append("\n   co.NOTIFICATION_INTERVAL as NOTIFICATIONINTERVAL, ");
            sb.append("\n   co.NOTIFICATION_TIME as NOTIFICATIONTIME, ");
            sb.append("\n   co.NOTIFICATION_PERIOD as NOTIFICATIONPERIOD, ");
            sb.append("\n   co.LAST_NOTIFICATION_DATE as LASTNOTIFICATIONDATE, ");
            sb.append("\n   co.NOTIFICATION_WEEKLY_SUN as NOTIFICATIONWEEKLYSUN, ");
            sb.append("\n   co.NOTIFICATION_WEEKLY_MON as NOTIFICATIONWEEKLYMON, ");
            sb.append("\n   co.NOTIFICATION_WEEKLY_TUE as NOTIFICATIONWEEKLYTUE, ");
            sb.append("\n   co.NOTIFICATION_WEEKLY_WED as NOTIFICATIONWEEKLYWED, ");
            sb.append("\n   co.NOTIFICATION_WEEKLY_THU as NOTIFICATIONWEEKLYTHU, ");
            sb.append("\n   co.NOTIFICATION_WEEKLY_FRI as NOTIFICATIONWEEKLYFRI, ");
            sb.append("\n   co.NOTIFICATION_WEEKLY_SAT as NOTIFICATIONWEEKLYSAT ");
            sb.append("\n   FROM contract co left outer join GROUP_MEMBER gm on (co.CONTRACT_NUMBER=gm.member) ");
            sb.append("\n                           , customer cu, code c");
            sb.append("\n   WHERE 1=1 ");
            sb.append("\n   AND   co.customer_id = cu.id ");
            sb.append("\n   AND   co.servicetype_id = c.id ");
            sb.append("\n   AND   cu.mobileNo IS NOT NULL ");
            sb.append("\n   AND   cu.smsYn = :smsYn ");
            sb.append("\n   AND   (co.credittype_id = :prepayCreditId ");
            sb.append("\n   OR   co.credittype_id = :emergencyICreditId )");
            sb.append("\n   AND  gm.member is null");

            query = getSession().createSQLQuery(sb.toString());
            query.setBoolean("smsYn", smsYn);
            query.setInteger("prepayCreditId", prepayCreditId);
            query.setInteger("emergencyICreditId", emergencyICreditId);

        } catch (Exception e) {
            logger.error(e,e);
        }
        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

    }
    
    /**
     * method name : getContractUsageSMSNOTGroup<b/>
     * method Desc : sms통보에 동의하고 모바일번호를 기입한 중 그룹에 묶여있지 않은 고객을 검색
     *
     * @param conditionMap
     * @return
     */
    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Map<String, Object>> getContractUsageSMSNOTGroup(Map<String, Object> conditionMap) {
        SQLQuery  query = null;

        Integer prepayCreditId = (Integer)conditionMap.get("prepayCreditId");
        Integer emergencyICreditId = (Integer)conditionMap.get("emergencyICreditId");
        Boolean smsYn = (Boolean)conditionMap.get("smsYn");
        String meterType = (String)conditionMap.get("meterType");
        String[] channelArr = (String[])conditionMap.get("channelArr");
        String[] dayArr = (String[])conditionMap.get("dayArr");
        String yyyymm = (String)conditionMap.get("yyyymm");
        
        StringBuilder sb = new StringBuilder();

        try {
            sb.append("\n   SELECT cu.name as CUSTOMERNAME, ");
            sb.append("\n   cu.mobileNo as MOBILENO, ");
            sb.append("\n   c.name as SERVICETYPE, ");
            sb.append("\n   co.prepaymentThreshold as PREPAYMENTTHRESHOLD, ");
            sb.append("\n   co.currentCredit as CURRENTCREDIT, ");
            sb.append("\n   co.currentArrears as CURRENTARREARS, ");
            sb.append("\n   co.customer_id as CUSTOMERID, ");
            sb.append("\n   co.id as CONTRACTID, ");
            sb.append("\n   co.SMS_NUMBER as SMSNUMBER, ");
            sb.append("\n   co.NOTIFICATION_INTERVAL as NOTIFICATIONINTERVAL, ");
            sb.append("\n   co.NOTIFICATION_TIME as NOTIFICATIONTIME, ");
            sb.append("\n   co.NOTIFICATION_PERIOD as NOTIFICATIONPERIOD, ");
            sb.append("\n   co.LAST_NOTIFICATION_DATE as LASTNOTIFICATIONDATE, ");
            sb.append("\n   co.NOTIFICATION_WEEKLY_SUN as NOTIFICATIONWEEKLYSUN, ");
            sb.append("\n   co.NOTIFICATION_WEEKLY_MON as NOTIFICATIONWEEKLYMON, ");
            sb.append("\n   co.NOTIFICATION_WEEKLY_TUE as NOTIFICATIONWEEKLYTUE, ");
            sb.append("\n   co.NOTIFICATION_WEEKLY_WED as NOTIFICATIONWEEKLYWED, ");
            sb.append("\n   co.NOTIFICATION_WEEKLY_THU as NOTIFICATIONWEEKLYTHU, ");
            sb.append("\n   co.NOTIFICATION_WEEKLY_FRI as NOTIFICATIONWEEKLYFRI, ");
            sb.append("\n   co.NOTIFICATION_WEEKLY_SAT as NOTIFICATIONWEEKLYSAT, ");
            sb.append("\n   m.value_").append(dayArr[0]).append(" as PREDAY, ");
            sb.append("\n   m.value_").append(dayArr[1]).append(" as TODAY ");
            sb.append("\n   FROM Contract co left outer join Group_Member gm on (co.CONTRACT_NUMBER=gm.member) ");
            sb.append("\n                           , Customer cu, Code c, ").append(meterType + " m");
            sb.append("\n   WHERE 1=1 ");
            sb.append("\n   AND   co.customer_id = cu.id ");
            sb.append("\n   AND   co.servicetype_id = c.id ");
            sb.append("\n   AND   cu.mobileNo IS NOT NULL ");
            sb.append("\n   AND   cu.smsYn = :smsYn ");
            sb.append("\n   AND   (co.credittype_id = :prepayCreditId ");
            sb.append("\n   OR   co.credittype_id = :emergencyICreditId )");
            sb.append("\n   AND  gm.member is null");
            sb.append("\n   AND  co.id=m.contract_id");
            sb.append("\n   AND  m.yyyymm = :yyyymm");
            sb.append("\n   AND  m.channel in (:channelArr)");

            query = getSession().createSQLQuery(sb.toString());
            query.setBoolean("smsYn", smsYn);
            query.setInteger("prepayCreditId", prepayCreditId);
            query.setInteger("emergencyICreditId", emergencyICreditId);
            query.setString("yyyymm", yyyymm);
            query.setParameterList("channelArr", channelArr);

        } catch (Exception e) {
            logger.error(e,e);
        }
        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

    }

    /**
     * method name : getContractUsageSMSGroup<b/>
     * method Desc : sms통보에 동의하고 모바일번호를 기입한 선불고객 중 그룹에 묶여있는 고객을 검색
     *
     * @param conditionMap
     * @return
     */
    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Map<String, Object>> getContractUsageSMSGroup(Map<String, Object> conditionMap) {
        SQLQuery  query = null;

        Integer prepayCreditId = (Integer)conditionMap.get("prepayCreditId");
        Integer emergencyICreditId = (Integer)conditionMap.get("emergencyICreditId");
        Boolean smsYn = (Boolean)conditionMap.get("smsYn");
        String meterType = (String)conditionMap.get("meterType");
        String[] channelArr = (String[])conditionMap.get("channelArr");
        String[] dayArr = (String[])conditionMap.get("dayArr");
        String yyyymm = (String)conditionMap.get("yyyymm");

        StringBuilder sb = new StringBuilder();

        try {
            sb.append("\n   SELECT cu.name as CUSTOMERNAME, ");
            sb.append("\n   cu.mobileNo as MOBILENO, ");
            sb.append("\n   c.name as SERVICETYPE, ");
            sb.append("\n   co.prepaymentThreshold as PREPAYMENTTHRESHOLD, ");
            sb.append("\n   co.currentCredit as CURRENTCREDIT, ");
            sb.append("\n   co.currentArrears as CURRENTARREARS, ");
            sb.append("\n   co.customerId as CUSTOMERID, ");
            sb.append("\n   co.id as CONTRACTID, ");
            sb.append("\n   co.smsNumber as SMSNUMBER, ");
            sb.append("\n   co.notificationInterval as NOTIFICATIONINTERVAL, ");
            sb.append("\n   co.notificationTime as NOTIFICATIONTIME, ");
            sb.append("\n   co.notificationPeriod as NOTIFICATIONPERIOD, ");
            sb.append("\n   co.lastNotificationDate as LASTNOTIFICATIONDATE, ");
            sb.append("\n   co.notificationWeeklySun as NOTIFICATIONWEEKLYSUN, ");
            sb.append("\n   co.notificationWeeklyMon as NOTIFICATIONWEEKLYMON, ");
            sb.append("\n   co.notificationWeeklyTue as NOTIFICATIONWEEKLYTUE, ");
            sb.append("\n   co.notificationWeeklyWed as NOTIFICATIONWEEKLYWED, ");
            sb.append("\n   co.notificationWeeklyThu as NOTIFICATIONWEEKLYTHU, ");
            sb.append("\n   co.notificationWeeklyFri as NOTIFICATIONWEEKLYFRI, ");
            sb.append("\n   co.notificationWeeklySat as NOTIFICATIONWEEKLYSAT, ");
            sb.append("\n   m.value_").append(dayArr[0]).append(" as PREDAY, ");
            sb.append("\n   m.value_").append(dayArr[1]).append(" as TODAY ");
            sb.append("\n   FROM Contract co left outer join MonthEM m");
            sb.append("\n                           , Customer cu, Code c, ").append(meterType + " m");
            sb.append("\n   WHERE 1=1 ");
            sb.append("\n   AND   co.customerId = cu.id ");
            sb.append("\n   AND   co.serviceTypeCodeId = c.id ");
            sb.append("\n   AND   cu.mobileNo IS NOT NULL ");
            sb.append("\n   AND   cu.smsYn = :smsYn ");
            sb.append("\n   AND   (co.creditTypeCodeId = :prepayCreditId ");
            sb.append("\n   OR   co.creditTypeCodeId = :emergencyICreditId )");
            sb.append("\n   AND  gm.member is null");
            sb.append("\n   AND  co.id=m.contract_id");
            sb.append("\n   AND  m.yyyymm = :yyyymm");
            sb.append("\n   AND  m.channel in (:channelArr)");

            query = getSession().createSQLQuery(sb.toString());
            query.setBoolean("smsYn", smsYn);
            query.setInteger("prepayCreditId", prepayCreditId);
            query.setInteger("emergencyICreditId", emergencyICreditId);
            query.setString("yyyymm", yyyymm);
            query.setParameterList("channelArr", channelArr);

        } catch (Exception e) {
            logger.error(e,e);
        }
        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

    }
    
    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRED)
    public List<Contract> getECGContract() {
        List<String> tariffList = new ArrayList<String>();
        tariffList.add("Residential");
        tariffList.add("Non Residential");
        Criteria criteria = getSession().createCriteria(Contract.class);
        criteria.createAlias("tariffIndex", "TariffType");
        criteria.add(Restrictions.in("TariffType.name", tariffList));

        return criteria.list();
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRED)
    public List<Contract> getECGContract(List<Integer> locationId) {
        String[] creditCode = {"2.2.1","2.2.2"};
        String[] tariffName = {"Residential", "Non Residential"};

        Criteria criteria = getSession().createCriteria(Contract.class);
        criteria.createAlias("tariffIndex", "TariffType");
        criteria.createAlias("location", "location");
        criteria.createAlias("creditType", "creditType");
        criteria.add(Restrictions.in("TariffType.name", tariffName));
        criteria.add(Restrictions.in("location.id", locationId));
        criteria.add(Restrictions.in("creditType.code", creditCode));
        return criteria.list();
    }

    @Override
    public Integer getContractCount(Map<String, String> condition) {
        Integer supplierId = Integer.parseInt(condition.get("supplierId"));

        Criteria criteria = getSession().createCriteria(Contract.class);

        criteria.setProjection(Projections.rowCount());

        if (supplierId != null)
            criteria.add(Restrictions.eq("supplier.id", supplierId));

        return ((Number)criteria.uniqueResult()).intValue();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getContractByloginId(String loginId, String meterType) {

        StringBuilder sb = new StringBuilder()
        .append(" SELECT co.id as CONTRACTID, co.currentCredit AS CURRENTCREDIT, co.contractNumber as CONTRACTNUMBER, co.meterId as METERID")
        .append(" FROM Customer cu, Contract co ")
        .append("     WHERE cu.id=co.customer.id ")
        .append("     AND cu.loginId = :loginId ")
        .append("     AND co.serviceTypeCode.code = :meterType ");

        Query query = getSession().createQuery(sb.toString());
        query.setString("loginId", loginId);
        query.setString("meterType", meterType);

        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

    /**
    * 모덺명을 입력받아 그 모델에 해당하는 선불 계약을 구한다.
    * ECG용으로 Residential, Non Residential Tariff의 경우만 검색한다.
    *
    */
    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRED)
    public List<Contract> getECGContractByNotCalculation(Map<String, Object> conditionMap) {
        String[] creditCode = {"2.2.1","2.2.2"};
        String[] tariffName = {"Residential", "Non Residential"};
        String[] modelName = (String[]) conditionMap.get("modelName");
        StringBuilder sb = new StringBuilder();

        sb.append("\n FROM Contract c");
        sb.append("\n WHERE   c.creditType.code in (:creditTypeCode) ");
        sb.append("\n AND   c.tariffIndex.name in (:tariffName) ");
        sb.append("\n AND   c.meter.model.name in (:modelName) ");
        
        Query query = getSession().createQuery(sb.toString());

        query.setParameterList("creditTypeCode", creditCode);
        query.setParameterList("tariffName", tariffName);
        query.setParameterList("modelName", modelName);

        return query.list();
    }
    

    /* (non-Javadoc)
     * @see com.aimir.dao.system.ContractDao#getPrepaidContractListForSMS(java.util.Map, boolean)
     * Get prepaid Contract List to send SMS
     */
    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Map<String, Object>> getPrepaidCustomerListForSMS(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

        try{
    
            // SMS 수신 여부
            Boolean smsYn = (Boolean)conditionMap.get("smsYn");
            String mdsId = (String)conditionMap.get("mdsId");
            StringBuilder sb = new StringBuilder();
    
            sb.append("\n   SELECT  ");
            sb.append("\n   cu.name as CUSTOMERNAME ");
            sb.append("\n   ,cu.mobileNo as MOBILENO ");
            sb.append("\nFROM Contract co, Customer cu, Code c ");
            sb.append("\nWHERE co.creditType.code IN ('2.2.1', '2.2.2') ");
            sb.append("\n   AND   co.customerId = cu.id ");
            sb.append("\n   AND   co.serviceTypeCodeId = c.id ");
            sb.append("\n   AND   cu.mobileNo IS NOT NULL ");
            sb.append("\n   AND   cu.smsYn = :smsYn ");
    
            if(mdsId != null && !mdsId.isEmpty()) {
                 sb.append("AND co.meter.mdsId = :mdsId ");
            }

            Query query = getSession().createQuery(sb.toString());
            
            if(mdsId != null && !mdsId.isEmpty()) {
                query.setString("mdsId", mdsId);
            }

            query.setInteger("smsYn", smsYn? 1: 0);
            result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list(); 

        } catch (Exception e) {
            logger.error(e,e);
            return result;
        }
        return result;
    }
    
	@Override
	public List<Contract> getReqSendSMSList(String mdevId) {
		StringBuffer sbQuery = new StringBuffer();
		
		sbQuery.append(" SELECT ");
		sbQuery.append("\n 	co.* ");
		sbQuery.append("\n FROM ");
		sbQuery.append("\n 	contract co, meter me, customer cu");
		sbQuery.append("\n WHERE");
		sbQuery.append("\n 	co.METER_ID = me.id");
		sbQuery.append("\n 	AND co.CUSTOMER_ID = cu.id");
		
		if(mdevId != null && !mdevId.isEmpty()) {
			sbQuery.append("\n 	AND me.MDS_ID = '").append(mdevId).append("'");
		}
		
		sbQuery.append("\n 	AND me.meter_status != (select id from code where code = '1.3.3.9')");
		sbQuery.append("\n 	AND co.status_id != (select id from code where code = '2.1.3')");
		sbQuery.append("\n 	AND me.LAST_READ_DATE >= (SELECT TO_CHAR(SYSDATE -15 ,'yyyymmddhh24miss') FROM dual)");
		
		List<Contract> result = getSession().createNativeQuery(sbQuery.toString(), Contract.class).getResultList();
		return result;
	}
    
	@Override
	public void updateExpiredEmergencyCredit() {
		StringBuffer sbQuery = new StringBuffer();
		
		sbQuery.append("MERGE INTO contract co");
		sbQuery.append("\n	USING (");
		sbQuery.append("\n		SELECT"); 
		sbQuery.append("\n			*");
		sbQuery.append("\n		FROM ");
		sbQuery.append("\n		(");
		sbQuery.append("\n			SELECT ");
		sbQuery.append("\n				co.id, me.MDS_ID, ");
		sbQuery.append("\n				(SELECT name FROM code WHERE id = co.CREDITTYPE_ID) AS creditType,");
		sbQuery.append("\n				co.EMERGENCYCREDITAUTOCHANGE,");
		sbQuery.append("\n				co.EMERGENCYCREDITMAXDURATION,");
		sbQuery.append("\n				co.EMERGENCYCREDITSTARTTIME,");
		sbQuery.append("\n				TO_date(co.EMERGENCYCREDITSTARTTIME, 'YYYYMMDDHH24MISS') + co.EMERGENCYCREDITMAXDURATION as EMERGENCYCREDITENDTIME");
		sbQuery.append("\n			FROM ");
		sbQuery.append("\n				contract co, meter me");
		sbQuery.append("\n			WHERE ");
		sbQuery.append("\n				co.METER_ID = me.ID ");
		sbQuery.append("\n				AND co.EMERGENCYCREDITSTARTTIME IS NOT NULL");
		sbQuery.append("\n				AND co.EMERGENCYCREDITMAXDURATION IS NOT NULL");
		sbQuery.append("\n				AND co.EMERGENCYCREDITMAXDURATION > 0");
		sbQuery.append("\n		)a");
		sbQuery.append("\n		WHERE ");
		sbQuery.append("\n			a.EMERGENCYCREDITENDTIME <= SYSDATE	");
		sbQuery.append("\n	)t");
		sbQuery.append("\n	ON (");
		sbQuery.append("\n		co.id = t.id");
		sbQuery.append("\n	)when matched THEN");
		sbQuery.append("\n		UPDATE SET");
		sbQuery.append("\n			co.EMERGENCYCREDITAUTOCHANGE = NULL,");
		sbQuery.append("\n			co.EMERGENCYCREDITMAXDURATION = NULL,");
		sbQuery.append("\n			co.EMERGENCYCREDITSTARTTIME = NULL,");
		sbQuery.append("\n			co.CREDITTYPE_ID = (SELECT id FROM code WHERE name = 'prepay')");
		
		int updateCnt = getSession().createNativeQuery(sbQuery.toString()).executeUpdate();
		logger.debug("query : "+sbQuery.toString() +", updateCnt : " + updateCnt);
	}

	@Override
	public List<Contract> getValidContractList(String mdevId) {
		StringBuffer sbQuery = new StringBuffer();
		
		sbQuery.append(" SELECT ");
		sbQuery.append("\n 	co.* ");
		sbQuery.append("\n FROM ");
		sbQuery.append("\n 	contract co, meter me");
		sbQuery.append("\n WHERE");
		sbQuery.append("\n 	co.METER_ID = me.id");		
		sbQuery.append("\n 	AND me.meter_status != (select id from code where code = '1.3.3.9')");
		sbQuery.append("\n 	AND co.status_id != (select id from code where code = '2.1.3')");
		
		if(mdevId != null  && !mdevId.isEmpty())
			sbQuery.append("\n 	AND me.mds_id = " + mdevId);
		
		List<Contract> result = getSession().createNativeQuery(sbQuery.toString(), Contract.class).getResultList();
		return result;
	}
	
	@Override
	public List<Contract> getDailyBillingContractList(String mdevId, String yyyymmdd) {
		StringBuffer sbQuery = new StringBuffer();
		
		sbQuery.append(" SELECT ");
		sbQuery.append("\n  	co.* ");
		sbQuery.append("\n FROM  ");
		sbQuery.append("\n ( ");
		sbQuery.append("\n 	 SELECT   ");
		sbQuery.append("\n 		de.CONTRACT_ID ");
		sbQuery.append("\n 	FROM   ");
		sbQuery.append("\n 		DAY_EM de   ");
		sbQuery.append("\n 	WHERE  ");
		sbQuery.append("\n 		1 = 1  ");

		if(yyyymmdd == null)
			sbQuery.append("\n 			AND de.YYYYMMDD BETWEEN (SELECT to_char(sysdate - 7, 'YYYYMMDD') FROM dual) AND (SELECT to_char(sysdate - 7, 'YYYYMMDD') FROM dual) ");
		else			
			sbQuery.append("\n 			AND de.YYYYMMDD <= :yyyymmdd ");			
			
		if(mdevId != null)		
			sbQuery.append("\n 			AND de.MDEV_ID = :mdevId ");
					
		sbQuery.append("\n 		AND de.CONTRACT_ID IS NOT null ");
		sbQuery.append("\n 	GROUP BY ");
		sbQuery.append("\n 		de.CONTRACT_ID ");
		sbQuery.append("\n )A, CONTRACT co ");
		sbQuery.append("\n WHERE  ");
		sbQuery.append("\n 	a.CONTRACT_ID = co.ID ");
		sbQuery.append("\n 	AND co.CREDITTYPE_ID IN (SELECT id FROM code WHERE code IN ('2.2.2', '2.2.1')) ");
		
		
		NativeQuery query = getSession().createNativeQuery(sbQuery.toString(), Contract.class);
		
		if(yyyymmdd == null)
			query.setParameter("yyyymmdd", yyyymmdd);
			
		if(mdevId != null)
			query.setParameter("yyyymmdd", yyyymmdd);
		
		List<Contract> result = query.getResultList();
		return result;
	}
	
	@Override
	public List<Contract> getMnthlyBillingContractList(String mdevId) {
		StringBuffer sbQuery = new StringBuffer();
		
		sbQuery.append(" SELECT  ");
		sbQuery.append("\n 	co.* ");
		sbQuery.append("\n FROM  ");
		sbQuery.append("\n ( ");
		sbQuery.append("\n 	SELECT me.mds_id FROM meter me, contract co WHERE me.id = co.METER_ID  ");
		sbQuery.append("\n 		AND (me.METER_STATUS IS NULL OR me.meter_status != (select id from code where code = '1.3.3.9')) ");
		sbQuery.append("\n 		AND (co.status_id IS NULL OR co.status_id != (select id from code where code = '2.1.3')) ");
		sbQuery.append("\n 		GROUP BY me.MDS_ID  ");
		sbQuery.append("\n 	MINUS  ");
		sbQuery.append("\n 	SELECT mb.MDS_ID FROM MONTHLY_BILLING_LOG mb  ");
		sbQuery.append("\n 		WHERE mb.YYYYMM = (SELECT TO_CHAR(sysdate, 'YYYYMM') FROM DUAL) ");
		sbQuery.append("\n 		GROUP BY mb.MDS_ID  ");
		sbQuery.append("\n )a, meter me, contract co ");
		sbQuery.append("\n WHERE ");
		sbQuery.append("\n 	a.mds_id = me.MDS_ID ");
		sbQuery.append("\n 	AND co.METER_ID = me.id ");
		sbQuery.append("\n 	AND co.CREDITTYPE_ID IN (SELECT id FROM code WHERE code IN ('2.2.1', '2.2.2')) ");
		
		if(mdevId != null  && !mdevId.isEmpty())
			sbQuery.append("\n 	AND me.mds_id = " + mdevId);
		
		List<Contract> result = getSession().createNativeQuery(sbQuery.toString(), Contract.class).getResultList();
		return result;
	}
}