package com.aimir.dao.device.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.FirmwareBoardDao;
import com.aimir.model.device.FirmwareBoard;
import com.aimir.util.Condition;

@Repository(value = "firmwareboardDao")
public class FirmwareBoardDaoImpl extends AbstractJpaDao<FirmwareBoard, Integer>  implements FirmwareBoardDao {
	private static Log logger = LogFactory.getLog(FirmwareBoardDaoImpl.class);

	public FirmwareBoardDaoImpl() {
		super(FirmwareBoard.class);
	}

    @Override
    public List<FirmwareBoard> getFirmwareBoardList(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<FirmwareBoard> getPersistentClass() {
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
