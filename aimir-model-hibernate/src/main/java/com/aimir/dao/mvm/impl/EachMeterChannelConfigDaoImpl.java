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
import com.aimir.dao.mvm.EachMeterChannelConfigDao;
import com.aimir.model.mvm.EachMeterChannelConfig;
import com.aimir.model.mvm.EachMeterChannelConfigPk;
import com.aimir.util.SQLWrapper;
import com.aimir.util.StringUtil;

@Repository(value = "eachmeterchannelconfigDao")
public class EachMeterChannelConfigDaoImpl extends AbstractHibernateGenericDao<EachMeterChannelConfig, EachMeterChannelConfigPk> implements EachMeterChannelConfigDao {

	private static Log logger = LogFactory.getLog(EachMeterChannelConfigDaoImpl.class);

	@Autowired
	protected EachMeterChannelConfigDaoImpl(SessionFactory sessionFactory) {
		super(EachMeterChannelConfig.class);
		super.setSessionFactory(sessionFactory);
	}

	@SuppressWarnings("unchecked")
	public List<Object> getByList(Map<String, Object> conditions) {
		String tlbType  = StringUtil.nullToBlank(conditions.get("tlbType"));
		String mdevId = StringUtil.nullToBlank(conditions.get("mdevId"));
		Integer channelId = (Integer)conditions.get("chmethodChannelId");

		StringBuffer sb = new StringBuffer();
		sb.append("\nSELECT DISTINCT B.id.channelIndex, A.localName, A.unit, A.chMethod ");
		sb.append("\nFROM DisplayChannel A ,EachMeterChannelConfig B ");
		sb.append("\nWHERE A.id = B.channel.id ");
		sb.append("\n AND   A.chMethod IS NOT NULL ");
		sb.append("\n AND   A.localName NOT LIKE 'Unknown%' ");

		if (channelId != null) {
		    sb.append("\nAND B.id.channelIndex = :channelId ");
		}
		
		if (!tlbType.isEmpty()) {
			sb.append("\nAND B.dataType = :tlbType ");
		}
		
		if (!mdevId.isEmpty()) {
			sb.append("\nAND B.id.mdevId = :mdevId ");
		}
		sb.append("\n ORDER BY B.id.channelIndex ");

		Query query = getSession().createQuery(sb.toString());


		if (channelId != null) {
		    query.setInteger("channelId", channelId);
		}
		
		if (!tlbType.isEmpty()) {
			query.setString("tlbType", tlbType);
		}

		if (!mdevId.isEmpty()) {
			query.setString("mdevId", mdevId);
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
        List<Integer> channelIdList = (List<Integer>)conditionMap.get("channelIdList");

        StringBuilder sb = new StringBuilder();

        sb.append("\nSELECT DISTINCT cc.channel_index AS CHANNEL_INDEX, ");
        sb.append("\n       dc.ch_method AS CH_METHOD ");
        sb.append("\nFROM display_channel dc, ");
        sb.append("\n     each_meter_channel_config  cc ");
        sb.append("\nWHERE dc.id = cc.channel_id ");

        if (!tlbType.isEmpty()) {
            sb.append("\nAND   cc.data_type = :tlbType ");
        }

        if (mdsId != null) {
            sb.append("\nAND   cc.mdev_id = :mdsId ");
        }

        if (channelIdList != null) {
            sb.append("\nAND   cc.channel_index IN (:channelIdList) ");
        }

        SQLQuery query = getSession().createSQLQuery(new SQLWrapper().getQuery(sb.toString()));
        
        if (!tlbType.isEmpty()) {
            query.setString("tlbType", tlbType);
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