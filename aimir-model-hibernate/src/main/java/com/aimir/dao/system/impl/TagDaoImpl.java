package com.aimir.dao.system.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.system.TagDao;
import com.aimir.model.system.Gadget;
import com.aimir.model.system.Tag;

	@Repository(value = "tagDao")
	public class TagDaoImpl extends AbstractHibernateGenericDao<Tag, Integer> implements TagDao {
			
	    Log logger = LogFactory.getLog(TagDaoImpl.class);
	    
	    @Autowired
	    protected TagDaoImpl(SessionFactory sessionFactory) {
	        super(Tag.class);
	        super.setSessionFactory(sessionFactory);
	    }

		@SuppressWarnings("unchecked")
		public List<Gadget> searchGadgetByTag(String tag, int roleId) {
			StringBuffer hqlBuf = new StringBuffer();
			hqlBuf.append(" SELECT g ");
			hqlBuf.append(" FROM Tag t JOIN t.gadget g, ");
			hqlBuf.append("      GadgetRole r");
			hqlBuf.append(" WHERE t.tag = :tag");
			hqlBuf.append("   AND r.gadget = t.gadget");
			hqlBuf.append("   AND r.role.id = :roleId");
			
			Query query = getSession().createQuery(hqlBuf.toString());

			query.setParameter("tag", tag);
			query.setParameter("roleId", roleId);
			return query.list();
		}

		@SuppressWarnings("unchecked")
		public List<Tag> getTags(int roleId) {
			StringBuffer hqlBuf = new StringBuffer();
			//hqlBuf.append(" SELECT DISTINCT tag ");
			//hqlBuf.append(" FROM Tag");
			
			hqlBuf.append(" SELECT DISTINCT t.tag ");
			hqlBuf.append(" FROM Tag t JOIN t.gadget g, ");
			hqlBuf.append("      GadgetRole r");
			hqlBuf.append(" WHERE r.gadget = t.gadget ");
			hqlBuf.append("   AND r.role.id = :roleId ");
			
			Query query = getSession().createQuery(hqlBuf.toString());
			query.setParameter("roleId", roleId);
			return query.list();
		}
 
		
}
