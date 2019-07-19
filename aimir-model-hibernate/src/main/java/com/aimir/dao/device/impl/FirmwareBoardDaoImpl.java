package com.aimir.dao.device.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.device.FirmwareBoardDao;
import com.aimir.model.device.FirmwareBoard;

@Repository(value = "firmwareboardDao")
public class FirmwareBoardDaoImpl extends AbstractHibernateGenericDao<FirmwareBoard, Integer>  implements FirmwareBoardDao {
	private static Log logger = LogFactory.getLog(FirmwareBoardDaoImpl.class);

	@Autowired
	protected FirmwareBoardDaoImpl(SessionFactory sessionFactory) {
		super(FirmwareBoard.class);
		super.setSessionFactory(sessionFactory);
	}

	public Serializable setFirmwareBoard(FirmwareBoard firmwareBoard) {
		return getSession().save(firmwareBoard);
	}

	public List<FirmwareBoard> getFirmwareBoardList(
			Map<String, Object> condition) {
		Criteria criteria = getSession().createCriteria(FirmwareBoard.class);

		if(condition != null) {
	        Set<String> set = condition.keySet();
	        Object []hmKeys = set.toArray();
	        for (int i=0; i<hmKeys.length; i++) {
	            String key = (String)hmKeys[i];
	            criteria.add(Restrictions.eq(key, condition.get(key)));
	        }
		}

        List<FirmwareBoard> firmwareBoards = (List<FirmwareBoard>) criteria.list();

        return firmwareBoards;
	}
}
