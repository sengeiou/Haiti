package com.aimir.dao.mvm.impl;

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

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.mvm.ChannelConfigDao;
import com.aimir.model.mvm.ChannelConfig;
import com.aimir.util.SQLWrapper;
import com.aimir.util.StringUtil;

@Repository(value = "channelconfigDao")
public class ChannelConfigDaoImpl extends AbstractHibernateGenericDao<ChannelConfig, Integer> implements ChannelConfigDao {

	private static Log logger = LogFactory.getLog(ChannelConfigDaoImpl.class);

	@Autowired
	protected ChannelConfigDaoImpl(SessionFactory sessionFactory) {
		super(ChannelConfig.class);
		super.setSessionFactory(sessionFactory);
	}

	@SuppressWarnings("unchecked")
    public List<Object> getByList(Map<String, Object> conditions) {
        String tlbType  = StringUtil.nullToBlank(conditions.get("tlbType"));
        Integer deviceConfigId = (Integer)conditions.get("deviceConfigId");
        Integer channelId = (Integer)conditions.get("chmethodChannelId");

        StringBuffer sb = new StringBuffer();
        sb.append("\nSELECT DISTINCT B.channelIndex, A.localName, A.unit, A.chMethod ");
        sb.append("\nFROM DisplayChannel A ,ChannelConfig B ");
        sb.append("\nWHERE A.id = B.channel.id ");
        sb.append("\nAND   A.chMethod IS NOT NULL ");

        if (channelId != null) {
            sb.append("\nAND B.channelIndex = :channelId ");
        }

        if (!tlbType.isEmpty()) {
            sb.append("\nAND B.dataType = :tlbType ");
        }

        if (deviceConfigId != null && deviceConfigId > 0) {
            sb.append("\nAND B.meterConfig = :deviceConfigId ");
        }

        sb.append("\nORDER BY B.channelIndex ");
        
        Query query = getSession().createQuery(sb.toString());

        if (channelId != null) {
            query.setInteger("channelId", channelId);
        }

        if (!tlbType.isEmpty()) {
            query.setString("tlbType", tlbType);
        }

        if (deviceConfigId != null && deviceConfigId > 0) {
            query.setInteger("deviceConfigId", deviceConfigId);
        }

        
        
        return query.list();
    }

    /**
     * method name : getChannelCalcMethodList<b/>
     * method Desc :
     *
     * @param conditionMap
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getChannelCalcMethodList(Map<String, Object> conditionMap) {
        String tlbType = StringUtil.nullToBlank(conditionMap.get("tlbType"));
        String mdsId = StringUtil.nullToBlank(conditionMap.get("mdsId"));
//        Integer deviceConfigId = (Integer)conditionMap.get("deviceConfigId");
        Integer meterId = (Integer)conditionMap.get("meterId");
//        Integer channelId = (Integer)conditionMap.get("channelId");
        List<Integer> channelIdList = (List<Integer>)conditionMap.get("channelIdList");

        StringBuilder sb = new StringBuilder();

        sb.append("\nSELECT DISTINCT cc.channel_index AS CHANNEL_INDEX, ");
        sb.append("\n       dc.ch_method AS CH_METHOD ");
        sb.append("\nFROM meter me, ");
        sb.append("\n     meterconfig mf, ");
        sb.append("\n     display_channel dc, ");
        sb.append("\n     channel_config  cc ");
        sb.append("\nWHERE mf.devicemodel_fk = me.devicemodel_id ");
        sb.append("\nAND   cc.meterconfig_id = mf.id ");

        if (!tlbType.isEmpty()) {
            sb.append("\nAND   cc.data_type = :tlbType ");
        }

        sb.append("\nAND   dc.id = cc.channel_id ");

        if (meterId != null) {
            sb.append("\nAND   me.id = :meterId ");
        }

        if (mdsId != null) {
            sb.append("\nAND   me.mds_id = :mdsId ");
        }

        if (channelIdList != null) {
            sb.append("\nAND   cc.channel_index IN (:channelIdList) ");
        }

        SQLQuery query = getSession().createSQLQuery(new SQLWrapper().getQuery(sb.toString()));
        
        if (!tlbType.isEmpty()) {
            query.setString("tlbType", tlbType);
        }

        if (meterId != null) {
            query.setInteger("meterId", meterId);
        }

        if (mdsId != null) {
            query.setString("mdsId", mdsId);
        }

        if (channelIdList != null) {
            query.setParameterList("channelIdList", channelIdList);
        }

        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }
}