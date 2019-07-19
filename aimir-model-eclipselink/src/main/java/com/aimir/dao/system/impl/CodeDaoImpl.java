package com.aimir.dao.system.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.model.system.Code;
import com.aimir.util.Condition;

@Repository(value = "codeDao")
public class CodeDaoImpl extends AbstractJpaDao<Code, Integer> implements CodeDao {
        
    Log logger = LogFactory.getLog(CodeDaoImpl.class);
    
    public CodeDaoImpl() {
        super(Code.class);
    }

    @SuppressWarnings("unchecked")
    public List<Code> getParents() {
        //from Location where parent is null
        return em.createQuery( "select c FROM Code c where c.parent is null ", Code.class).getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Code> getParentAndChild(Integer id) {
        String sql = "select c from Code c where c.id = :id";
        Query query = em.createQuery(sql, Code.class);
        query.setParameter("id",  id);
        return query.getResultList();
    }
    
    public int parentCodeCheck(String code, Integer id) throws DataAccessException {      
        String sql = "SELECT COUNT(code) FROM Code " +
                    "WHERE parent is null " +
                    "AND code = :code " +
                    "AND NOT EXISTS( select 1 FROM Code WHERE id = :id) ";
        Query query = em.createQuery(sql, Integer.class);
        query.setParameter("code", code);
        query.setParameter("id", id);
        return (Integer)query.getSingleResult();
    }

    public int childCodeCheck(String code, Integer id) {
        String sql = "SELECT COUNT(c.code) FROM Code c " +
                    "WHERE c.code = :code " +
                    "AND NOT EXISTS (select a.code FROM Code a WHERE a.id = c.id AND a.id = :id) ";
        Query query = em.createQuery(sql, Integer.class);
        query.setParameter("code", code);
        query.setParameter("id", id);
        return (Integer)query.getSingleResult();
    }
    
    public void updateCode(Code code) throws Exception {
        StringBuffer hqlBuf = new StringBuffer();
        hqlBuf.append("UPDATE Code code ");
        hqlBuf.append("SET code.name = :name , code.code = :code , code.descr = :descr ");
        hqlBuf.append("WHERE code.id = :id ");
    
        Query query = em.createQuery(hqlBuf.toString());
        query.setParameter("name", code.getName());
        query.setParameter("code", code.getCode());
        query.setParameter("descr", code.getDescr());
        query.setParameter("id", code.getId());
        query.executeUpdate();
    }

    @SuppressWarnings("unchecked")
    public List<Code> getChildCodes(String parentCode)
    {
        String sql = "select c from Code c where c.parent.code = :code order by upper(c.name) asc";
        Query query = em.createQuery(sql, Code.class);
        query.setParameter("code", parentCode);
        return query.getResultList();
    }

    /**
     * method name : getChildCodesSelective
     * method Desc : parent 의 code 값으로 Code List 를 리턴. parameter 로 넘어온 코드(들)를 제외하고 조회함.
     * 
     * @param parentCode Code.parent.code
     * @param excludeCodes String ','값으로 구분된 조회시 제외할 code 들
     * @return List of Code  @see com.aimir.model.system.Code
     */
    @SuppressWarnings("unchecked")
    public List<Code> getChildCodesSelective(String parentCode, String excludeCodes) {
        StringBuilder sb = new StringBuilder();
        sb.append("select c ");
        sb.append("FROM Code c ");
        sb.append("WHERE c.parent.code = :parentCode ");
        sb.append("AND   c.code NOT IN (:exclude) ");
        sb.append("ORDER BY c.name ASC");

        Query query = em.createQuery(sb.toString(), Code.class);
        query.setParameter("parentCode", parentCode);
        query.setParameter("exclude", excludeCodes);

        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Code> getChildren(int parentId) 
    {
        String sql = "select c from Code c where c.parent.id = :parentId order by upper(c.name) asc";
        Query query = em.createQuery(sql, Code.class);
        query.setParameter("parentId", parentId);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Code> getChildCodesOrder(String parentCode) 
    {
        String sql = "select c from Code c where c.parent.code = :parentCode order by upper(c.name) asc";
        Query query = em.createQuery(sql, Code.class);
        query.setParameter("parentCode", parentCode);
        return query.getResultList();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<Code> getCodesByName(final String name) {
        logger.debug("name : " + name);
        String sql = "select c from Code c where c.name like :name";
        Query query = em.createQuery(sql, Code.class);
        query.setParameter("name", name+"%");
        return query.getResultList();
    }
    
    private boolean isEmpty(String val) {
        return (val == null || val.trim().length() == 0);
    }

    @SuppressWarnings("unchecked")
    public List<Code> getEnergyList(int customerId) {
        String sql = "SELECT DISTINCT service From Contract a " +
                "left outer join a.serviceTypeCode service WHERE a.customer.id = :customerId ";
        Query query = em.createQuery(sql, Code.class);
        query.setParameter("customerId", customerId);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public Code getCodeByName(String name) {
        String sql = "select c from Code c where c.name = :name";
        Query query = em.createQuery(sql, Code.class);
        query.setParameter("name", name);
        return (Code)query.getSingleResult();
    }   
    
    @SuppressWarnings("unchecked")
    public Code getCodeWithChildByName(String name) {
        Code code = getCodeByName(name);
        code.getChildren();
        return code;
    }
    
    @SuppressWarnings("unchecked")
    public int getCodeIdByCode(String codeNm) {
        String sql = "select id from Code c where c.code = :code";
        Query query = em.createQuery(sql,  Integer.class);
        query.setParameter("code", codeNm);
        return (Integer)query.getSingleResult();
    }   
    
    @SuppressWarnings("unchecked")
    public Code getCodeIdByCodeObject(String codeNm) {

        //String sql = "select c from Code c where c.code = :code";
        //Query query = em.createQuery(sql, Code.class);
        //query.setParameter("code", codeNm);
        //return (Code)query.getSingleResult();
        
        
        return findByCondition("code",codeNm);
    }   
    
    @SuppressWarnings("unchecked")
    public List<Code> getChildren(Integer parentId) {
        String sql = "select c from Code c where c.parent.id = :parentId order by c.order";
        Query query = em.createQuery(sql, Code.class);
        query.setParameter("parentId",  parentId);
        return query.getResultList();
    }
    
    public List<Integer> getLeafCode(Integer codeId) {

        List<Integer> codeIdList = new ArrayList<Integer>();
        try {

            List<Code> codes = getChildren(codeId);

            for (Code code : codes) {
                if (code.getChildren() != null && code.getChildren().size() > 0) {
                    codeIdList.addAll(getLeafCode(code.getChildren()));
                } else {
                    codeIdList.add(code.getId());
                }
            }

            if (codeIdList == null || codeIdList.size() < 1) {
                codeIdList.add(codeId);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return codeIdList;
    }

    /**
     * 최하위 Code 를 반환하는 재귀함수
     * 
     * @param locations
     * @return
     */
    private List<Integer> getLeafCode(Set<Code> codes) {
        List<Integer> codeIdList = new ArrayList<Integer>();
        for (Code code : codes) {
            if (code.getChildren() != null && code.getChildren().size() > 0) {
                codeIdList.addAll(getLeafCode(code.getChildren()));
            } else {
                codeIdList.add(code.getId());
            }
        }
        return codeIdList;
    }

    /**
     * method name : getSicChildrenCodeList<b/>
     * method Desc : SIC Load Profile 가젯에서 2 level SIC Code list 를 조회한다.
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Code> getSicChildrenCodeList() {
        StringBuilder sb = new StringBuilder();

        sb.append("select cd ");
        sb.append("\nFROM Code cd ");
        sb.append("\nWHERE cd.parent.parent.code = :sicParentCode ");
        sb.append("\nORDER BY cd.parent.order, cd.order ");

        Query query =em.createQuery(sb.toString(), Code.class);
        query.setParameter("sicParentCode", Code.SIC);

        return query.getResultList();
    }

    public Code getCodeByCondition(Map<String, Object> condition) {
    	
    	Query query = null;
    	
    	String name = (String)condition.get("name");
    	Integer parentCodeId = (Integer)condition.get("parentCodeId");
    	
    	StringBuffer sb = new StringBuffer();
    	
    	sb.append("select cd ");
    	sb.append("\nFROM Code cd ");
        sb.append("\nWHERE cd.parent.id = :parentCodeId ");
        sb.append("\nAND cd.name = :name");
        
        query = em.createQuery(sb.toString(), Code.class);
        query.setParameter("name", name);
        query.setParameter("parentCodeId", parentCodeId);
        
        return (Code)query.getSingleResult();
    }

    /**
     * method name : getMeterStatusCodeByName<b/>
     * method Desc : MeterStatus Name 으로 Code 를 조회한다. Parent Code Name 은 default 로 "MeterStatus"
     *
     * @param meterStatusName
     * @return
     */
    public Code getMeterStatusCodeByName(String meterStatusName) {
        return getMeterStatusCodeByName(meterStatusName, "MeterStatus");
    }

    /**
     * method name : getMeterStatusCodeByName<b/>
     * method Desc : MeterStatus Name 과 Parent Code Name 으로 Code 를 조회한다.
     *
     * @param meterStatusName
     * @param parentName
     * @return
     */
    @SuppressWarnings("unchecked")
    public Code getMeterStatusCodeByName(String meterStatusName, String parentName) {
        String sql = "select c from Code c where c.name = :meterStatusName and c.parent.name = :parentName";
        Query query = em.createQuery(sql, Code.class);
        query.setParameter("meterStatusName", meterStatusName);
        query.setParameter("parentName", parentName);
        return (Code)query.getSingleResult();
    }  
    
    @SuppressWarnings("unchecked")
    public List<Code> getCodeList() {
        String sql = "select c from Code c order by c.order";
        Query query = em.createQuery(sql, Code.class);
        return query.getResultList();
    }

    @Override
    public Class<Code> getPersistentClass() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getSumFieldByCondition(Set<Condition> conditions,
            String field, String... groupBy) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Code> getChildCodes(String parentCode, String orderBy) {
        // TODO Auto-generated method stub
        return null;
    }
}