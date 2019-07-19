package com.aimir.dao.system;

import com.aimir.dao.GenericDao;
import com.aimir.model.system.SupplyTypeLocation;

public interface SupplyTypeLocationDao extends GenericDao<SupplyTypeLocation, Integer>{
	
	/**
     * method name : checkSupplyType
     * method Desc : 공급타입의 중복을 체크한다
     * 
	 * @param typeId SupplyType.typeCode.id
	 * @return 중복되는 카운트가 1이상이면  false를 리턴하고  중복되는 것이 없으면 true를 리턴한다.
	 */
	public boolean checkSupplyType(Integer typeId);
}
