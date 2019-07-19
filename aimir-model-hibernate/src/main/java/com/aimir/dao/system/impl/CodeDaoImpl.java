package com.aimir.dao.system.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate4.HibernateCallback;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.model.system.Code;

@Repository(value = "codeDao")
public class CodeDaoImpl extends AbstractHibernateGenericDao<Code, Integer> implements CodeDao {
        
    Log logger = LogFactory.getLog(CodeDaoImpl.class);
    
    @Autowired
    protected CodeDaoImpl(SessionFactory sessionFactory) {
        super(Code.class);
        super.setSessionFactory(sessionFactory);
    }

    @SuppressWarnings("unchecked")
    public List<Code> getParents() {
        //from Location where parent is null
    	Query query = getSession().createQuery("FROM Code where parent is null ");
        return query.list();
    }

    @SuppressWarnings("unchecked")
    public List<Code> getParentAndChild(Integer id) {
    	Query query = getSession().createQuery("FROM Code c WHERE id = :id ");
        query.setInteger("id", id);
    	return query.list();
    }
    
    public int parentCodeCheck(String code, Integer id) throws DataAccessException {            
        
    	Query query = getSession().createQuery("SELECT COUNT(code) FROM Code " +
                "WHERE parent is null " +
                "AND code = :code " +
                "AND NOT EXISTS( select 1 FROM Code WHERE id = :id) " );
    	query.setString("code", code);
    	query.setInteger("id", id);
    	return DataAccessUtils.intResult(query.list());
    }

    public int childCodeCheck(String code, Integer id) {
    	Query query = getSession().createQuery("SELECT COUNT(c.code) FROM Code c " +
            "WHERE c.code = :code " +
            "AND NOT EXISTS (select a.code FROM Code a WHERE a.id = c.id AND a.id = :id) ");
    	query.setString("code", code);
    	query.setInteger("id", id);		
        return DataAccessUtils.intResult(query.list());       
    }
    
    public void updateCode(Code code) throws Exception {
        StringBuffer hqlBuf = new StringBuffer();
        hqlBuf.append("UPDATE Code code ");
        hqlBuf.append("SET code.name = ? , code.code = ? , code.descr = ? ");
        hqlBuf.append("WHERE code.id = ? ");
    
    //HQL문을 이용한 CUD를 할 경우에는 getHibernateTemplate().bulkUpdate() 메소드를 사용한다.
        // bulkUpdate 때문에 
        /*this.getSession().bulkUpdate(hqlBuf.toString(),
            new Object[] { code.getName() , code.getCode() , code.getDescr(), code.getId() } );*/
    }

    @SuppressWarnings("unchecked")
    public List<Code> getChildCodes(String parentCode)
    {
    	Query query = getSession().createQuery("from Code where parent.code = :parentCode order by upper(name) asc");
    	query.setString("parentCode", parentCode);
        return query.list();
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
        String[] exclude = excludeCodes.split(",");

        StringBuilder sb = new StringBuilder();
        sb.append("FROM Code ");
        sb.append("WHERE parent.code = :parentCode ");
        sb.append("AND   code NOT IN (:exclude) ");
        sb.append("ORDER BY name ASC");

        Query query = getSession().createQuery(sb.toString());
        query.setString("parentCode", parentCode);
        query.setParameterList("exclude", exclude);

        return query.list();
    }

    @SuppressWarnings("unchecked")
    public List<Code> getChildren(int parentId) 
    {	Query query = getSession().createQuery("from Code where parent.id = :parentId order by upper(name) asc");
    	query.setInteger("parentId", parentId);
        return query.list();
    }

    @SuppressWarnings("unchecked")
    public List<Code> getChildCodesOrder(String parentCode) 
    {
    	Query query = getSession().createQuery("from Code where parent.code = :parentCode order by upper(name) asc");
    	query.setString("parentCode", parentCode);
        return query.list();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<Code> getCodesByName(final String name) {
        logger.debug("name : " + name);
        Criteria criteria = getSession().createCriteria(Code.class);
        if (!isEmpty(name)){
               criteria.add(Restrictions.ilike("name", name + "%"));
        }
        
        return criteria.list();
    }
    
    private boolean isEmpty(String val) {
        return (val == null || val.trim().length() == 0);
    }

    @SuppressWarnings("unchecked")
    public List<Code> getEnergyList(int customerId) {
    	Query query = getSession().createQuery("SELECT DISTINCT service From Contract a " +
                "left outer join a.serviceTypeCode service WHERE customer_id = :customerId ");
    	query.setInteger("customerId", customerId);
        return query.list();
    }

    @SuppressWarnings("unchecked")
    public Code getCodeByName(String name) {
    	Query query = getSession().createQuery("FROM Code c where name = :name ");
    	query.setString("name", name);
        List<Code> codeList = query.list();
        Code code = new Code();
        
        if (codeList.size() > 0) {
            code = codeList.get(0);
        }
        
        return code;
    }   
    
    @SuppressWarnings("unchecked")
    public Code getCodeWithChildByName(String name) {
    	Query query = getSession().createQuery("FROM Code c where name = :name ");
    	query.setString("name", name);
        List<Code> codeList = query.list();
        Code code = new Code();
        
        if (codeList.size() > 0) {
            code = codeList.get(0);
            
            Set<Code> children = new HashSet<Code>();
            List<Code> codes = getChildren(code.getId());

            for (Code codeC : codes) {
                if (codes != null && codes.size() > 0) {
                    children.add(codeC);
                } 
            }
            code.setChildren(children);
        }
        
        return code;
    }
    
    @SuppressWarnings("unchecked")
    public int getCodeIdByCode(String codeNm) {
        int codeId = 0;
        Query query = getSession().createQuery("FROM Code c where code = :codeNm ");
        query.setString("codeNm", codeNm);
        List<Code> codeList = query.list();
        
        if (codeList.size() > 0) {
            codeId = ((Number)codeList.get(0).getId()).intValue();
            
        }
        
        
        return codeId;
    }   
    
    @SuppressWarnings("unchecked")
    public Code getCodeIdByCodeObject(String codeNm) {
        Code code = new Code();
        Query query = getSession().createQuery("FROM Code c where code = :codeNm ");
        query.setString("codeNm", codeNm);
        List<Code> codeList = query.list();
        
        if (codeList.size() > 0) {
            code = codeList.get(0);
        }
        return code;
    }   
    
    @SuppressWarnings("unchecked")
    public List<Code> getChildren(Integer parentId) {
    	Query query = getSession().createQuery("from Code where parent_id = :parentId order by codeorder ");
    	query.setInteger("parentId", parentId);
        return query.list();
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

        sb.append("\nFROM Code cd ");
        sb.append("\nWHERE cd.parent.parent.code = :sicParentCode ");
        sb.append("\nORDER BY cd.parent.order, cd.order ");

        Query query = getSession().createQuery(sb.toString());
        query.setString("sicParentCode", Code.SIC);

        return query.list();
    }

    public Code getCodeByCondition(Map<String, Object> condition) {
    	
    	Query query = null;
    	Code code = null;
    	
    	String name = (String)condition.get("name");
    	Integer parentCodeId = (Integer)condition.get("parentCodeId");
    	
    	StringBuffer sb = new StringBuffer();
    	
    	try{
    	sb.append("\nFROM Code cd ");
        sb.append("\nWHERE cd.parent.id = :parentCodeId ");
        sb.append("\nAND cd.name = :name");
        
        query = getSession().createQuery(sb.toString());
        query.setString("name", name);
        query.setInteger("parentCodeId", parentCodeId);
        
        code = query.list().size() > 0 ? (Code)query.list().get(0) : null;
    	} catch (Exception e) {
			// TODO: handle exception
    		logger.error(e,e);
		}
    	return code;
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
    	Query query = getSession().createQuery("FROM Code c WHERE c.name = :meterStatusName AND c.parent.name = :parentName ");
    	query.setString("meterStatusName", meterStatusName);
    	query.setString("parentName", parentName);
        List<Code> codeList = query.list();
        Code code = new Code();
        
        if (codeList.size() > 0) {
            code = codeList.get(0);
        }
        
        return code;
    }  
    
    @SuppressWarnings("unchecked")
    public List<Code> getCodeList() {
    	Query query = getSession().createQuery(" FROM Code ORDER BY order ");
        return query.list();
    }
    
    @SuppressWarnings("unchecked")
    public List<Code> getChildCodes(String parentCode, String orderBy)
    {
	    Criteria criteria = getSession().createCriteria(Code.class).addOrder(Order.asc(orderBy));
	    criteria.createAlias("parent", "parent");
        criteria.add(Restrictions.eq("parent.code", parentCode));
        return criteria.list();
    }
}