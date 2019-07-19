package com.aimir.dao.system.impl;

/**
 * Created on 16-08-17.
 */

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.system.GroupStrategyDao;
import com.aimir.model.system.GroupStrategy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository(value="groupStrategyDao")
public class GroupStrategyDaoImpl extends AbstractHibernateGenericDao<GroupStrategy, Integer> implements GroupStrategyDao{

    private static Log log = LogFactory.getLog(GroupStrategyDaoImpl.class);

    @Autowired
    protected GroupStrategyDaoImpl(SessionFactory sessionFactory) {
        super(GroupStrategy.class);
        super.setSessionFactory(sessionFactory);
    }

    /**
     * Get Strategy List Using SupplierID
     * @param supplierId
     */
    @Override
    public List<Map<String, Object>> getStrategyBySupplier(Integer supplierId) {
        StringBuffer sb = new StringBuffer();
        sb.append("   SELECT ");
        sb.append("\n gs.groupId AS groupId");
        sb.append("\n ,gs.aimirGroup.name AS groupName");
        sb.append("\n ,gs.configName AS configName");
        sb.append("\n ,gs.configValue AS configValue");
        sb.append("\n ,gs.prevValue AS previous");
        sb.append("\n ,gs.updateDate AS updateDate");
        sb.append("\n ,gs.createDate AS createDate");
        sb.append("\n ,gs.loginId AS loginId");
        sb.append("\n FROM GroupStrategy gs ");
        sb.append("\n WHERE gs.aimirGroup.supplierId = :supplierId ");
        sb.append("\n ORDER BY gs.updateDate");

        Query query = getSession().createQuery(sb.toString());
        query.setInteger("supplierId", supplierId);

        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

    @Override
    public List<Object> getStrategyByGroup(Integer groupId) {
        StringBuffer sb = new StringBuffer();
        sb.append("   SELECT gs, gs.aimirGroup.name ");
        sb.append("\n FROM GroupStrategy gs ");
        sb.append("\n WHERE gs.aimirGroup.id = :groupId ");
        sb.append("\n ORDER BY gs.updateDate");

        Query query = getSession().createQuery(sb.toString());
        query.setInteger("groupId", groupId);

        return query.list();
    }

    @Override
    public List<Object> getStrategyByConfig(String configName) {
        StringBuffer sb = new StringBuffer();
        sb.append("   SELECT gs, gs.aimirGroup.name ");
        sb.append("\n FROM GroupStrategy gs ");
        sb.append("\n WHERE gs.configName = :configName ");
        sb.append("\n ORDER BY gs.updateDate");

        Query query = getSession().createQuery(sb.toString());
        query.setString("configName", configName);

        return query.list();
    }
}
