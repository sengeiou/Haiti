package com.aimir.dao.system;

import com.aimir.constants.CommonConstants.MeterProgramKind;
import com.aimir.dao.GenericDao;

import java.util.Map;

import com.aimir.model.system.MeterProgram;

public interface MeterProgramDao extends GenericDao<MeterProgram, Integer> {
	
	/**
     * method name : getMeterConfigId<b/>
     * method Desc : MeterConfig id로 해당미터 프로그램의 정보를 리턴한다.
     * 
	 * @param meterconfig_id MeterConfig.id
	 * @return  @see com.aimir.model.system.MeterProgram
	 */
	public MeterProgram getMeterConfigId(int meterconfig_id);
	
	/**
     * method name : getMeterConfigId<b/>
     * method Desc : MeterConfig id와 미터프로그램 종류로 해당미터 프로그램의 정보를 리턴한다.
     * 
	 * @param meterconfig_id  MeterConfig.id
	 * @param kind MeterProgramKind
	 * @return   @see com.aimir.model.system.MeterProgram
	 */
	public MeterProgram getMeterConfigId(int meterconfig_id,MeterProgramKind kind);

    /**
     * method name : getMeterProgramSettingsData<b/>
     * method Desc : Vendor Model 맥스가젯의 Meter Program 탭에서 Settings 값을 조회한다.
     *
     * @param conditionMap
	 * <ul>
	 * <li> meterProgramId : MeterProgram.id
	 * </ul>
	 * 
     * @return settings
     */
    public String getMeterProgramSettingsData(Map<String, Object> conditionMap);
}
