package com.aimir.dao.device.impl;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.MCUCodiBindingDao;
import com.aimir.model.device.MCUCodiBinding;
import com.aimir.util.Condition;

@Repository(value = "mcucodibindingDao")
public class MCUCodiBindingDaoImpl extends AbstractJpaDao<MCUCodiBinding, Long> implements MCUCodiBindingDao {

	public MCUCodiBindingDaoImpl() {
		super(MCUCodiBinding.class);
	}

    @Override
    public Class<MCUCodiBinding> getPersistentClass() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getSumFieldByCondition(Set<Condition> conditions,
            String field, String... groupBy) {
        // TODO Auto-generated method stub
        return null;
    }
}
