/**
 * RoleDaoImpl.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.dao.system.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.system.RoleDao;
import com.aimir.model.system.Code;
import com.aimir.model.system.Gadget;
import com.aimir.model.system.Role;
import com.aimir.util.StringUtil;

import net.sf.ehcache.search.expression.Criteria;


/**
 * RoleDaoImpl.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 4. 12.   v1.0       김상연         Role Id 검색 (이름)
 *
 */
@Repository(value = "roleDao")
public class RoleDaoImpl extends AbstractHibernateGenericDao<Role, Integer> implements RoleDao {

    Log logger = LogFactory.getLog(RoleDaoImpl.class);
    
	@Autowired
	protected RoleDaoImpl(SessionFactory sessionFactory) {
		super(Role.class);
		super.setSessionFactory(sessionFactory);
	}


	@SuppressWarnings("unchecked")
	public List<Role> getRoleBySupplierId(Integer supplierId) {
		if(supplierId==null)
			return  null;
		
		org.hibernate.Criteria criteria = getSession().createCriteria(Role.class);
		criteria.add(Restrictions.eq("supplier.id", supplierId));
		criteria.addOrder(Order.asc("name").ignoreCase());
		
		return criteria.list();
		
		/*DetachedCriteria criteria = DetachedCriteria.forClass(Role.class);
		criteria.add(Restrictions.eq("supplier.id", supplierId));
		criteria.addOrder(Order.asc("name").ignoreCase());
		return (List<Role>)getHibernateTemplate().findByCriteria(criteria);*/
		
		
	}	
	
	@SuppressWarnings("unchecked")
    public List<Role> getRoleBySupplierIdForCustomer(Integer supplierId) {
		
		org.hibernate.Criteria criteria = getSession().createCriteria(Role.class);
        criteria.add(Restrictions.eq("supplier.id", supplierId));
        criteria.add(Restrictions.eq("customerRole", true));
        criteria.addOrder(Order.asc("name").ignoreCase());
        return criteria.list();
		
		
        
	    /*DetachedCriteria criteria = DetachedCriteria.forClass(Role.class);
        criteria.add(Restrictions.eq("supplier.id", supplierId));
        criteria.add(Restrictions.eq("customerRole", true));
        criteria.addOrder(Order.asc("name").ignoreCase());
        return (List<Role>)getHibernateTemplate().findByCriteria(criteria);*/
    }   
	
	/*
	//code table name 값 Commands 로 code 값 가져오기 
	@SuppressWarnings("unchecked")
	public List<Code> getByCode(String codeName) {
		return getHibernateTemplate().find("FROM Code c WHERE name = ? AND parent_id IS NULL " , codeName);		
	}
	
	//code 값으로 하위 가져오기
	@SuppressWarnings("unchecked")
	public List<Code> getByName(String code) {
		return getHibernateTemplate().find("FROM Code c where code like ?" ,  code + '%');	
	}
	*/
		
	@SuppressWarnings("unchecked")
	public List<Gadget> getGadgetList() {
		Query query = getSession().createQuery("FROM Gadget");
		return query.list();
		
		// return (List<Gadget>)getHibernateTemplate().find("FROM Gadget");
	}
	
	//허용된 가젯 저장
	public void updateGadget(Integer roleId, Integer gadgetId) {
		StringBuffer hqlBuf = new StringBuffer();
		hqlBuf.append("UPDATE Gadget ");
		hqlBuf.append("SET role_id = :roleId ");		
		hqlBuf.append("WHERE id = :gadgetId ");
		
		Query query = getSession().createQuery(hqlBuf.toString());
		query.setInteger("roleId", roleId);
		query.setInteger("gadgetId", gadgetId);
		query.executeUpdate();
		// this.getHibernateTemplate().bulkUpdate(hqlBuf.toString(), new Object[] { roleId, gadgetId } );		
	}

	//허용된 가젯 삭제
	public void delGadget(Integer gadgetId) {
		StringBuffer hqlBuf = new StringBuffer();
		hqlBuf.append("UPDATE Gadget gadget ");
		hqlBuf.append("SET role_id = null ");		
		hqlBuf.append("WHERE id = :gadgetId ");
		
		Query query = getSession().createQuery(hqlBuf.toString());
		query.setInteger("gadgetId", gadgetId);
		query.executeUpdate();
		// this.getHibernateTemplate().bulkUpdate(hqlBuf.toString(), gadgetId );		
	}
	
    // 허용된 가젯 검색 - Name
    @SuppressWarnings("unchecked")
    public List<Gadget> gadgetSearch(Integer roleId, String gadgetName) {
    	
    	Query query = getSession().createQuery("FROM Gadget WHERE role_id = :roleId AND name like :gadgetName");
    	query.setInteger("roleId", roleId);
    	query.setString("gadgetName", '%' + gadgetName + '%');
    	
		return query.list();
    	
		// return (List<Gadget>)getHibernateTemplate().find("FROM Gadget WHERE role_id = ? AND name like ? ", new Object[] { roleId, '%' + gadgetName + '%' });
    }

    // 허용된 가젯 검색 - Tag
    @SuppressWarnings("unchecked")
    public List<Gadget> gadgetSearchByTag(Integer roleId, String tag) {
        String isBlank = StringUtil.nullToBlank(tag).length() == 0 ? "true" : "false";
        StringBuilder sb = new StringBuilder();

        // 입력된 조건값이 없으면 Gadget 테이블 모든 데이터를 조회하기 위해 Tag 테이블과의 조건을 skip
        sb.append("FROM Gadget g ");
        sb.append("WHERE role_id = :roleId ");
        sb.append("AND   CASE WHEN 'false' = :isBlank ");
        sb.append("                THEN CASE WHEN EXISTS (SELECT 'x' ");
        sb.append("                                       FROM Tag t ");
        sb.append("                                       WHERE t.gadget.id = g.id ");
        sb.append("                                       AND   t.tag LIKE :tag) THEN 1 ");
        sb.append("                          ELSE 0 END ");
        sb.append("           ELSE 1 END = 1 ");
        sb.append("ORDER BY g.name ");
        
        Query query = getSession().createQuery(sb.toString());
        query.setInteger("roleId", roleId);
        query.setString("isBlank", isBlank);
        query.setString("tag", new StringBuilder().append('%').append(tag).append('%').toString());
        return query.list();
        
        // return (List<Gadget>) getHibernateTemplate().find(sb.toString(),new Object[] { roleId, isBlank, new StringBuilder().append('%').append(tag).append('%').toString() });
    }

	//전체 가젯 검색 - Name
	@SuppressWarnings("unchecked")
	public List<Gadget> gadgetAllSearch(Integer roleId, String name) {
		
		Query query = getSession().createQuery("from Gadget where (role_id is null or role_id != :roleId) and name like :name ");
		query.setInteger("roleId", roleId);
		query.setString("name", '%' + name+ '%');
		return query.list();
		
		/*return (List<Gadget>)getHibernateTemplate().find("from Gadget where (role_id is null or role_id != ? ) and name like ? ", 
				new Object[] { roleId, '%' + name+ '%'} );*/
	}

    // 전체 가젯 검색 - Tag
    @SuppressWarnings("unchecked")
    public List<Gadget> gadgetAllSearchByTag(Integer roleId, String tag) {
        String isBlank = StringUtil.nullToBlank(tag).length() == 0 ? "true" : "false";
        StringBuilder sb = new StringBuilder();

        // 입력된 조건값이 없으면 Gadget 테이블 모든 데이터를 조회하기 위해 Tag 테이블과의 조건을 skip 
        sb.append("FROM Gadget g ");
        sb.append("WHERE (role_id IS NULL OR role_id != :roleId) ");
        sb.append("AND   CASE WHEN 'false' = :isBlank ");
        sb.append("                THEN CASE WHEN EXISTS (SELECT 'x' ");
        sb.append("                                       FROM Tag t ");
        sb.append("                                       WHERE t.gadget.id = g.id ");
        sb.append("                                       AND   t.tag LIKE :tag) THEN 1 ");
        sb.append("                          ELSE 0 END ");
        sb.append("           ELSE 1 END = 1 ");
        sb.append("ORDER BY g.name ");
        
        Query query = getSession().createQuery(sb.toString());
        query.setInteger("roleId", roleId);
        query.setString("isBlank", isBlank);
        query.setString("tag", new StringBuilder().append('%').append(tag).append('%').toString());
        
        return query.list();
        
        // return (List<Gadget>)getHibernateTemplate().find(sb.toString(), new Object[] { roleId, isBlank, new StringBuilder().append('%').append(tag).append('%').toString() });
    }

	@SuppressWarnings("unchecked")
	public List<Gadget> search(String name) {
		String val_name = "";
		Query query = getSession().createQuery("FROM Gadget WHERE name like :val_name");
		query.setString("val_name", '%' + name+ '%');
		
		return query.list();
		
		// return (List<Gadget>)getHibernateTemplate().find("FROM Gadget WHERE name like ? " , '%' + name+ '%' );
	}

	@SuppressWarnings("unchecked")
	public List<Gadget> getPermitedGadgets(Integer roleId) {
		Query query = getSession().createQuery("FROM Gadget WHERE role_id =:roleId");
		query.setInteger("roleId", roleId);
		return query.list();
		
		// return (List<Gadget>)getHibernateTemplate().find("FROM Gadget WHERE role_id = ? " , roleId );
	}

	//그룹명 중복체크
	public int nameOverlapCheck(String name) {
		Query query = getSession().createQuery("SELECT COUNT(r.name) FROM Role r WHERE r.name = :name");
		query.setString("name", name);
		
		return DataAccessUtils.intResult(query.list());
		
		// return DataAccessUtils.intResult(getHibernateTemplate().find("SELECT COUNT(r.name) FROM Role r WHERE r.name = ? " , name ));			
	}
	
	/* (non-Javadoc)
	 * @see com.aimir.dao.system.RoleDao#getRoleByName(java.lang.String)
	 */
	public Role getRoleByName(String name) {
		return findByCondition("name", name);
	}
	
	public List<Code> getModemCommands(Integer roleId){
		String modem_command = "";
		Query query = getSession().createQuery("SELECT rc FROM Role r join r.commands rc WHERE r.id = :roleId rc.parent.code = :modem_command");
		query.setInteger("roleId", roleId);
		query.setString("modem_command", Code.MODEM_COMMAND);
		return query.list();
		
		
		// return (List<Code>)getHibernateTemplate().find("SELECT rc FROM Role r join r.commands rc WHERE r.id=? rc.parent.code=?",roleId,Code.MODEM_COMMAND);
	}
}