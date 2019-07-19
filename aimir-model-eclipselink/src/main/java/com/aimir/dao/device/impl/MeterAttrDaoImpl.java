package com.aimir.dao.device.impl;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.MeterAttrDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.model.device.Meter;
import com.aimir.model.device.MeterAttr;
import com.aimir.util.Condition;


/**
 * SP-898
 * @author
 *
 */
//@SuppressWarnings({ "unchecked", "rawtypes" })
@Repository(value = "meterattrDao")
public class MeterAttrDaoImpl extends AbstractJpaDao<MeterAttr, Long> implements MeterAttrDao {

	protected static Log logger = LogFactory.getLog(MeterAttrDaoImpl.class);

    MeterDao meterDao;

//	protected MeterAttrDaoImpl(SessionFactory sessionFactory) {
	protected MeterAttrDaoImpl() {
		super(MeterAttr.class);
	}

    @Override
    public Class<MeterAttr> getPersistentClass() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getSumFieldByCondition(Set<Condition> conditions,
            String field, String... groupBy) {
        // TODO Auto-generated method stub
        return null;
    }

	public MeterAttr getByMeterId(Integer meter_id)
	{
		return findByCondition("meterId", meter_id);
	}

	public MeterAttr getByMdsId(String mdsId)
	{
		MeterAttr attr = null;
		Meter meter = meterDao.get(mdsId);
		if ( meter != null ){
			attr =  getByMeterId(meter.getId());
		}
		return attr;
	}
	
	public List<Object> getMetersToClearAlarmObject(Map<String, Object> conditionMap,boolean isTotal)
	{
		return null;
	}
	public List<Map<String,Object>> getMucsToClearAlarmObject(Map<String, Object> conditionMap)
	{
		return null;
	}
}