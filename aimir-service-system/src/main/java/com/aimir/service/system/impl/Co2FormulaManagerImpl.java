package com.aimir.service.system.impl;

import javax.jws.WebService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.dao.system.Co2FormulaDao;
import com.aimir.model.system.Co2Formula;
import com.aimir.service.system.Co2FormulaManager;

@WebService(endpointInterface = "com.aimir.service.system.Co2FormulaManager")
@Service(value="co2formulaManager")
@Transactional
public class Co2FormulaManagerImpl implements Co2FormulaManager {

	@Autowired
	Co2FormulaDao dao;
	
	public void add(Co2Formula co2Formula) {
		dao.add(co2Formula);
	}

	public void update(Co2Formula co2Formula) {
		dao.update(co2Formula);
	}

	public Co2Formula getCo2FormulaBySupplyType(Integer supplyTypeCodeId) {
		return dao.getCo2FormulaBySupplyType(supplyTypeCodeId);
	}

	// 탄소배출량 계산 : 사용량 * 발생량
	public double calculateCo2emission(Co2Formula co2Formula) {
		return co2Formula.getUnitUsage() * co2Formula.getCo2factor();
	}

}
