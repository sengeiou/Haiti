package com.aimir.dao.system.impl;

import java.util.List;
import java.util.Map;

import javax.jms.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.system.GadgetRoleDao;
import com.aimir.model.system.GadgetRole;
import com.aimir.util.StringUtil;



@Repository(value = "gadgetroleDao")
public class GadgetRoleDaoImpl extends AbstractHibernateGenericDao<GadgetRole, Integer> implements GadgetRoleDao {

    Log logger = LogFactory.getLog(GadgetRoleDaoImpl.class);
    
	@Autowired
	protected GadgetRoleDaoImpl(SessionFactory sessionFactory) {
		super(GadgetRole.class);
		super.setSessionFactory(sessionFactory);
	}

	@SuppressWarnings("unchecked")
    public List<Map<String, Object>> getGadgetRolesList(Map<String, Object> params){
		
		String roleId = StringUtil.nullToBlank(params.get("roleId"));
		String gadgetId = StringUtil.nullToBlank(params.get("gadgetId"));
		String supplierId = StringUtil.nullToBlank(params.get("supplierId"));
		
		StringBuffer sql = new StringBuffer();
		
		sql.append(" SELECT id as id, gadget_id as gadget_id, role_id as role_id, supplier_id as supplier_id ");
		sql.append("FROM Gadget_Role WHERE 1=1 ");
		
		if(!"".equals(roleId)){
			sql.append(" AND role_id = :roleId ");
		}
		
		if(!"".equals(gadgetId)){
			sql.append(" AND gadget_id = :gadgetId ");
		}
		
		if(!"".equals(supplierId)){
			sql.append(" AND supplier_id = :supplierId ");
		}
		
		Query query = getSession().createQuery(sql.toString());

		if(!"".equals(roleId)){
			query.setParameter("roleId", roleId);
		}
		
		if(!"".equals(gadgetId)){
			query.setParameter("gadgetId", gadgetId);
		}
		
		if(!"".equals(supplierId)){
			query.setParameter("supplierId", supplierId);
		}
		
		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	}

	 /**
     * method name : getGadgetListByRole<b/>
     * method Desc : UserManagement 맥스가젯에서 허용된 가젯 리스트를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<Map<String, Object>> getGadgetListByRole(Map<String, Object> conditionMap) {

        Integer roleId = (Integer)conditionMap.get("roleId");
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        String gadgetName = StringUtil.nullToBlank(conditionMap.get("gadgetName"));
        String tagName = StringUtil.nullToBlank(conditionMap.get("tagName"));

   /*     Criteria crit = getSession().createCriteria(GadgetRole.class);
        crit.add(Restrictions.eq("role.id", roleId));
        crit.add(Restrictions.eq("supplier.id", supplierId));

        return crit.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        */
        
        
        //return crit.list();
        
        
        StringBuilder sb = new StringBuilder();
		sb.append("\nSELECT r.gadget AS gadget ");
		sb.append("\nFROM GadgetRole r ");
		
		
		if (!tagName.isEmpty())
		{
			sb.append("\n     , Tag t ");
		}
		
		sb.append("\nWHERE r.role.id = :roleId ");
		sb.append("\nAND   r.supplier.id = :supplierId ");

		if (!gadgetName.isEmpty())
		{
			sb.append("\nAND   r.gadget.name LIKE :gadgetName ");
		}

		if (!tagName.isEmpty())
		{
			sb.append("\nAND   t.gadget.id = r.gadget.id ");
			sb.append("\nAND   t.tag LIKE :tagName ");
		}
		
		

		// name 순 정렬
		sb.append("  order by r.gadget.name asc   ");
        

        Query query = getSession().createQuery(sb.toString());
        
        
        query.setInteger("roleId", roleId);
        query.setInteger("supplierId", supplierId);
        
        

        if (!gadgetName.isEmpty()) {
            query.setString("gadgetName", "%" + gadgetName + "%");
        }

        if (!tagName.isEmpty()) {
            query.setString("tagName", "%" + tagName + "%");
        }
        
        
        


        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

    /**
     * method name : getDelGadgetRoleList<b/>
     * method Desc : UserManagement 맥스가젯에서 삭제할 가젯 리스트를 조회한다. Role 삭제 시에는 gadgetList 가 null 로 넘어온다.
     *
     * @param conditionMap
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<GadgetRole> getDelGadgetRoleList(Map<String, Object> conditionMap) {

        Integer roleId = (Integer)conditionMap.get("roleId");
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        List<Integer> gadgetList = (List<Integer>)conditionMap.get("gadgetList");

        StringBuilder sb = new StringBuilder();

        sb.append("\nFROM GadgetRole r ");
        sb.append("\nWHERE r.role.id = :roleId ");
        sb.append("\nAND   r.supplier.id = :supplierId ");
        
        if (gadgetList != null) {
            sb.append("\nAND   r.gadget.id IN (:gadgetList) ");
        }

        Query query = getSession().createQuery(sb.toString());
        query.setInteger("roleId", roleId);
        query.setInteger("supplierId", supplierId);

        if (gadgetList != null) {
            query.setParameterList("gadgetList", gadgetList);
        }

        return query.list();
    }
}