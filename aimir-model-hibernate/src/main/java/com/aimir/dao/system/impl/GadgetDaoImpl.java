package com.aimir.dao.system.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.system.GadgetDao;
import com.aimir.model.system.Gadget;
import com.aimir.util.StringUtil;

@Repository(value = "gadgetDao")
public class GadgetDaoImpl extends AbstractHibernateGenericDao<Gadget, Integer> implements GadgetDao {

    private static Log logger = LogFactory.getLog(GadgetDaoImpl.class);
    
	@Autowired
	protected GadgetDaoImpl(SessionFactory sessionFactory) {
		super(Gadget.class);
		super.setSessionFactory(sessionFactory);
	}

	@SuppressWarnings("unchecked")
	public List<Gadget> searchGadgetList(String gadgetName, Integer roleId) {
//		DetachedCriteria subCriteria1 = DetachedCriteria.forClass(Operator.class, "operator")
//			.add(Restrictions.eq("operator.id", operatorId))
//			.add(Restrictions.eqProperty("operator.role", "gadgetRole.role"));
//		DetachedCriteria subCriteria2 = DetachedCriteria.forClass(GadgetRole.class, "gadgetRole")
//			.add(Property.forName("role").gt(subCriteria1))
//			.add(Restrictions.eqProperty("gadgetRole.gadget", "gadget.id"));
//		
//		Criteria criteria = getSession().createCriteria(Gadget.class, "gadget")
//			.add(Property.forName("id").gt(subCriteria2))
//			.add(Restrictions.like("name", gadgetName));
//		
//		return criteria.list();
		StringBuffer hqlBuf = new StringBuffer();
		hqlBuf.append(" SELECT g")
		      .append(" FROM GadgetRole gr join gr.role r")
		      .append("                    join gr.gadget g")
		      .append(" WHERE 1 = 1");
		      
		      if(roleId != null && roleId > 0){
		    	  hqlBuf.append("   AND r.id = :roleId");
		      }
		      if(gadgetName != null || !"".equals(gadgetName)){
		    	  hqlBuf.append("   AND g.name LIKE :gadgetName");
		      }
		      
		
		Query query = getSession().createQuery(hqlBuf.toString());
	      if(roleId != null && roleId > 0){
	  			query.setParameter("roleId", roleId);
	      }
	      if(gadgetName != null || !"".equals(gadgetName)){
	  			query.setParameter("gadgetName", gadgetName);
	      }

		return query.list();
	}

	/* (non-Javadoc)
	 * @see com.aimir.dao.system.GadgetDao#getGadgetByGadgetCode(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public List<Gadget> getGadgetByGadgetCode(String gadgetCode){
		Criteria criteria = getSession().createCriteria(Gadget.class);
		criteria.add(Restrictions.eq("gadgetCode", gadgetCode));
		//criteria.addOrder(Order.asc("orderNo"));
		return criteria.list();
	}

    /**
     * method name : getRemainGadgetList<b/>
     * method Desc : UserManagement 맥스가젯에서 전체가젯 리스트를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Gadget> getRemainGadgetList(Map<String, Object> conditionMap) {

        Integer roleId = (Integer)conditionMap.get("roleId");
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        String gadgetName = StringUtil.nullToBlank(conditionMap.get("gadgetName"));
        String tagName = StringUtil.nullToBlank(conditionMap.get("tagName"));

        StringBuilder sb = new StringBuilder();

        if (!tagName.isEmpty()) {
            sb.append("\nSELECT t.gadget ");
        }

        sb.append("\nFROM Gadget g ");
        if (!tagName.isEmpty()) {
            sb.append("\n     , Tag t ");
        }
        sb.append("\nWHERE 1=1 ");
        
        if (roleId != -1) {
            sb.append("\nAND   NOT EXISTS (SELECT 'x' ");
            sb.append("\n                  FROM GadgetRole r ");
            sb.append("\n                  WHERE r.role.id = :roleId ");
            sb.append("\n                  AND   r.supplier.id = :supplierId ");
            sb.append("\n                  AND   r.gadget.id = g.id) ");
        }

        if (!gadgetName.isEmpty()) {
            sb.append("\nAND   g.name LIKE :gadgetName ");
        }

        if (!tagName.isEmpty()) {
            sb.append("\nAND   t.gadget.id = g.id ");
            sb.append("\nAND   t.tag LIKE :tagName ");
        }
        
        
        

        Query query = getSession().createQuery(sb.toString());
        if (roleId != -1) {
            query.setInteger("roleId", roleId);
            query.setInteger("supplierId", supplierId);
        }

        if (!gadgetName.isEmpty()) {
            query.setString("gadgetName", "%" + gadgetName + "%");
        }

        if (!tagName.isEmpty()) {
            query.setString("tagName", "%" + tagName + "%");
        }

        return query.list();
    }
    
    
    /*
     * @desc
     * (non-Javadoc)
     * @see com.aimir.dao.system.GadgetDao#getAllGadgetList(java.util.Map)
     */
    @SuppressWarnings("unchecked")
	public List<Gadget> getAllGadgetList(Map<String, Object> conditionMap) 
    {

        
        StringBuilder sb = new StringBuilder();
		sb.append("\nSELECT r.gadget AS gadget ");
		sb.append("\nFROM GadgetRole r ");
		
		
		sb.append("\nWHERE r.role.id = :roleId ");
		sb.append("\nAND   r.supplier.id = :supplierId ");
		
		sb.append("\n order by r.gadget.name asc ");
		

        Query query = getSession().createQuery(sb.toString());
        
        int roleId = Integer.parseInt( conditionMap.get("roleId").toString());
        int supplierId = Integer.parseInt( conditionMap.get("supplierId").toString());
        
        query.setInteger("roleId",roleId );
        query.setInteger("supplierId", supplierId);
        
        

        return query.list();

        //return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }
}