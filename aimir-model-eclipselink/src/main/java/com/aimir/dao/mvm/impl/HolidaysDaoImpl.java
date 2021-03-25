package com.aimir.dao.mvm.impl;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.system.HolidaysDao;
import com.aimir.model.mvm.Holidays;
import com.aimir.util.Condition;

@Repository(value = "holidaysDao")
public class HolidaysDaoImpl extends AbstractJpaDao<Holidays, Integer> implements HolidaysDao {

	public HolidaysDaoImpl() {
		super(Holidays.class);
	}

	@Override
	public Class<Holidays> getPersistentClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Object> getSumFieldByCondition(Set<Condition> conditions, String field, String... groupBy) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Holidays getHoliday(int mm, int dd) {
		// TODO Auto-generated method stub
		return null;
	}

}
