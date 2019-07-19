package com.aimir.dao.device.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.constants.CommonConstants.GroupType;
import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.device.IHDDao;
import com.aimir.model.device.IHD;
import com.aimir.util.StringUtil;

@Repository(value = "ihdDao")
public class IHDDaoImpl extends AbstractHibernateGenericDao<IHD, Integer> implements IHDDao {

    Log log = LogFactory.getLog(IHDDaoImpl.class);
    
	@Autowired
	protected IHDDaoImpl(SessionFactory sessionFactory) {
		super(IHD.class);
		super.setSessionFactory(sessionFactory);
	}

    /**
     * method name : getMemberSelectData<b/>
     * method Desc : HomeGroup Management 가젯에서 해당 집중기에 맞는 Member 로 등록 가능한 IHD 리스트를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Object> getMemberSelectData(Map<String, Object> conditionMap) {
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        Integer mcuId = (Integer)conditionMap.get("mcuId");
        String memberName = StringUtil.nullToBlank(conditionMap.get("memberName"));

        StringBuilder sb = new StringBuilder();
        sb.append("\nSELECT ihdMember.value as value, ");
        sb.append("\n		ihdMember.text as text, ");
        sb.append("\n		ihdMember.type as type ");
        sb.append("\nFROM ( ");
        sb.append("\n	SELECT mo.id AS value, ");
        sb.append("\n          mo.device_Serial AS text, ");
        sb.append("\n           'Modem' AS type ");
        sb.append("\n	FROM Modem mo ");
        sb.append("\n	WHERE mo.modem_Type=:modemType ");
        sb.append("\n	AND mo.supplier_Id = :supplierId ");

        sb.append("\n	AND mo.mcu_Id=:mcuId ");
        
        if (!memberName.isEmpty()) {
            sb.append("\n	AND   mo.device_Serial LIKE :memberName ");
        }

        sb.append("\n	AND   NOT EXISTS ( ");
        sb.append("\n    	SELECT 'X' ");
        sb.append("\n    	FROM  Group_Member gm, AimirGroup g ");
        sb.append("\n    	WHERE gm.group_Id = g.id ");
        sb.append("\n    	AND   g.groupName = :groupName ");
        sb.append("\n    	AND   gm.member = mo.device_Serial ");
        sb.append("\n) ");
        
        sb.append("\n	UNION ");
        
        sb.append("\n	SELECT me.id AS value, ");
        sb.append("\n     	  me.mds_Id AS text, ");
        sb.append("\n    	   'Meter' AS type ");
        sb.append("\n	FROM Meter me ");
        sb.append("\n	WHERE me.supplier_Id = :supplierId ");

        if (!memberName.isEmpty()) {
            sb.append("\n	AND   me.mds_Id LIKE :memberName ");
        }

    	sb.append("\nAND   NOT EXISTS ( ");
        sb.append("\n    	SELECT 'X' ");
        sb.append("\n    	FROM  Group_Member gm, AimirGroup g ");
        sb.append("\n    	WHERE gm.group_Id = g.id ");
        sb.append("\n    	AND   g.groupName = :groupName ");
        sb.append("\n   	AND   gm.member = me.mds_Id ");
        sb.append("\n	)");
        sb.append("\n)  AS ihdMember");
        sb.append("\nORDER BY ihdMember.text, ihdMember.type ");

        Query query = getSession().createSQLQuery(sb.toString());
        query.setString("modemType", ModemType.IHD.name());
        query.setInteger("mcuId", mcuId);
        query.setString("groupName", GroupType.HomeGroup.name());
        query.setInteger("supplierId", supplierId);
        if (!memberName.isEmpty()) {
            query.setString("memberName", "%" + memberName + "%");
        }

        return query.list();
    }
	
}