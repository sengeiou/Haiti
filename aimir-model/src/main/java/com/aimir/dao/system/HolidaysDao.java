package com.aimir.dao.system;

import com.aimir.dao.GenericDao;
import com.aimir.model.mvm.Holidays;

public interface HolidaysDao extends GenericDao<Holidays, Integer> {

	public Holidays getHoliday(int mm, int dd);
}
