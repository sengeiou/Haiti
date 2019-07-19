package com.aimir.dao.system.impl;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.system.PointDao;
import com.aimir.model.system.Point;
import com.aimir.util.Condition;

@Repository(value = "pointDao")
public class PointDaoImpl extends AbstractJpaDao<Point, Integer> implements PointDao {

	public PointDaoImpl() {
		super(Point.class);
	}

    @Override
    public Class<Point> getPersistentClass() {
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
