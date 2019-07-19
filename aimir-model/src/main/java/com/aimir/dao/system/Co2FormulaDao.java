package com.aimir.dao.system;

import com.aimir.dao.GenericDao;
import com.aimir.model.system.Co2Formula;

public interface Co2FormulaDao extends GenericDao<Co2Formula, Integer> {

    /**
     * method name : getCo2FormulaBySupplyType
     * method Desc : 서비스타입(공급유형, 에너지타입) 코드 아이디를 이용하여 CO2 계산식을 가져온다.
     *
     * @param supplyTypeCodeId 서비스타입(공급유형, 에너지타입) 코드 아이디
     * @return @see com.aimir.model.system.Co2Formula
     */
	public Co2Formula getCo2FormulaBySupplyType(Integer supplyTypeCodeId);
	
	/**
     * method name : getCo2FormulaBySupplyType
     * method Desc : 서비스타입(공급유형, 에너지타입) 코드를 이용하여 CO2 계산식을 가져온다.
	 * 
	 * @param supplyTypeCode 서비스타입(공급유형, 에너지타입) 코드
	 * @return @see com.aimir.model.system.Co2Formula
	 */
	public Co2Formula getCo2FormulaBySupplyType(String supplyTypeCode);
}
