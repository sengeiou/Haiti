package com.aimir.dao.mvm.impl;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.mvm.MeteringDataSPMDao;
import com.aimir.model.mvm.MeteringDataSPM;
import com.aimir.util.Condition;

/**
 * 태양열에너지 검침데이터 Dao
 * @author Yi Hanghee(javarouka@gmail.com, javarouka@nuritelecom.co.kr)
 *
 */
@Repository(value = "meteringDataSPMDao")
public class MeteringDataSPMDaoImpl 
	extends AbstractJpaDao<MeteringDataSPM, Integer>
	implements MeteringDataSPMDao {

    public MeteringDataSPMDaoImpl() {
		super(MeteringDataSPM.class);
	}
	
	@Override
	public long totalByConditions(Set<Condition> condition) {
		List<Object> ret = findTotalCountByConditions(condition);
		return (Long) ret.get(0);
	}

    @Override
    public Class<MeteringDataSPM> getPersistentClass() {
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
