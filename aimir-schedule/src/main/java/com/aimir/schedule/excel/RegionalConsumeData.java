package com.aimir.schedule.excel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aimir.model.system.PrepaymentLog;
import com.aimir.schedule.excel.ExcelUtil.PropertyMap;
import com.aimir.util.StringUtil;

public class RegionalConsumeData {

	/**
	 *  Calculation
	 * 
	 *  Residential
	 *	Charges Without Levies And Vat = Unit Charge + Service Charge + Total Subsidies(1,2,3). 
	 * 	Total Charges = Charges Without Levies and Vat + Gov't Levies +Public Lights.
	 *
	 *  Non Residential
 	 *	Charges Without Levies And Vat = Unit Charge + Service Charge + Total Subsidies(3). 	
	 *	Total Charges = Charges Without Levies and Vat + VAT(inclusive of Vat on subsidies) +Gov't Levies +Public Lights.
	 * 
	 */
	public static Map<String, Object> makeExcelData(List<PrepaymentLog> logList, String TariffName) {
		Map<String, Object> result = new HashMap<String, Object>();
		Integer numOfCust = 0;	
		Double unitsConsumed = 0d;
		Double unitsCharge = 0d;
		Double serviceCharge = 0d;
		Double chargesWithoutLevies = 0d;
		Double vatAccu = 0d;
		Double vatOnSubsidyAccu = 0d;
		Double govLevyAccu = 0d;
		Double pubLevyAccu = 0d;
		Double monthlyLevy = 0d;
		Double wholeCharge = 0d;
		Double subsidyAccu = 0d;
		Double lifeSubsidyAccu = 0d;
		Double additionalSubsidyAccu = 0d;
		
		for ( PrepaymentLog log : logList) {
			unitsConsumed += StringUtil.nullToDoubleZero( log.getUsedConsumption() );
			Double monthlyTotalAmount = StringUtil.nullToDoubleZero( log.getMonthlyTotalAmount() );
			unitsCharge += monthlyTotalAmount;
			Double monthlyServiceCharge = StringUtil.nullToDoubleZero( log.getMonthlyServiceCharge() );
			serviceCharge += monthlyServiceCharge;
			
			Double lifeSubsidy = StringUtil.nullToDoubleZero( log.getLifeLineSubsidy() );
			lifeSubsidyAccu += lifeSubsidy;
            Double additionalSubsidy = StringUtil.nullToDoubleZero( log.getAdditionalSubsidy() );
            additionalSubsidyAccu += additionalSubsidy; 
            Double subsidy = StringUtil.nullToDoubleZero( log.getSubsidy() );
            subsidyAccu += subsidy;
			
			Double allSubsidy = 0d;
			if("Residential".equals(TariffName)) {
				allSubsidy = lifeSubsidy + additionalSubsidy + subsidy;
			} else if("Non Residential".equals(TariffName)) {
				allSubsidy = additionalSubsidy;
			}
            
			chargesWithoutLevies += (monthlyTotalAmount + monthlyServiceCharge);
			Double vat = StringUtil.nullToDoubleZero( log.getVat() );
			vatAccu += vat;
			Double vatOnSubsidy = StringUtil.nullToDoubleZero( log.getVatOnSubsidy() );
			vatOnSubsidyAccu += vatOnSubsidy;
			
			Double govLevy = StringUtil.nullToDoubleZero( log.getGovLevy() );
			govLevyAccu += govLevy;
			Double pubLevy = StringUtil.nullToDoubleZero( log.getPublicLevy() );
			pubLevyAccu += pubLevy;

			monthlyLevy += (govLevy + pubLevy);
			if("Residential".equals(TariffName)) {
				wholeCharge += (monthlyTotalAmount + monthlyServiceCharge + govLevy + pubLevy);
			} else if("Non Residential".equals(TariffName)) {
				wholeCharge += (monthlyTotalAmount + monthlyServiceCharge + govLevy + pubLevy + vat);
			}

			numOfCust++;
		}
		
		result.put(PropertyMap.numOfCust.getProp(), numOfCust	);
		result.put(PropertyMap.unitsConsumed.getProp(), unitsConsumed);
		result.put(PropertyMap.unitsCharge.getProp(), unitsCharge);
		result.put(PropertyMap.serviceCharge.getProp(), serviceCharge);
		result.put(PropertyMap.chargesWithoutLevies.getProp(), chargesWithoutLevies);
		result.put(PropertyMap.vat.getProp(), vatAccu);
		result.put(PropertyMap.vatOnSubsidy.getProp(), vatOnSubsidyAccu);
		result.put(PropertyMap.govLevy.getProp(), govLevyAccu);
		result.put(PropertyMap.pubLevy.getProp(), pubLevyAccu);
		result.put(PropertyMap.totalLevies.getProp(), monthlyLevy);
		result.put(PropertyMap.totalCharge.getProp(), wholeCharge);
		result.put(PropertyMap.subsity.getProp(), subsidyAccu);
		result.put(PropertyMap.lifeLineSubsidy.getProp(), lifeSubsidyAccu);
		result.put(PropertyMap.addtionalSubsidy.getProp(), additionalSubsidyAccu);
		return result;
	}
	
	public static void makeRegionalTotal( Map<String, Map<String, Object>> regionalData, Map<String, Map<String, Object>> data, Boolean requestTotal) {
		Integer numOfCust = 0;	
		Double unitsConsumed = 0d;
		Double unitsCharge = 0d;
		Double serviceCharge = 0d;
		Double chargesWithoutLevies = 0d;
		Double vat = 0d;
		Double vatOnSubsidy = 0d;
		Double govLevy = 0d;
		Double pubLevy = 0d;
		Double monthlyLevy = 0d;
		Double wholeCharge = 0d;
		Double subsidy = 0d;
		Double lifeSubsidy = 0d;
		Double additionalSubsidy = 0d;
		
		Set<String> regions = regionalData.keySet();
		for ( String region : regions ) {
			Map<String, Object> row = regionalData.get( region );
			numOfCust += row.get(PropertyMap.numOfCust.getProp()) == null? 
					new Integer(0) : (Integer) row.get(PropertyMap.numOfCust.getProp()) ;
			unitsConsumed += StringUtil.nullToDoubleZero( (Double) row.get(PropertyMap.unitsConsumed.getProp()) );
			unitsCharge += StringUtil.nullToDoubleZero((Double) row.get(PropertyMap.unitsCharge.getProp()) );
			serviceCharge += StringUtil.nullToDoubleZero( (Double) row.get(PropertyMap.serviceCharge.getProp()) );
			chargesWithoutLevies += StringUtil.nullToDoubleZero( (Double) row.get(PropertyMap.chargesWithoutLevies.getProp()) );
			vat += StringUtil.nullToDoubleZero( (Double) row.get(PropertyMap.vat.getProp()) );
			vatOnSubsidy += StringUtil.nullToDoubleZero( (Double) row.get(PropertyMap.vatOnSubsidy.getProp()) );
			govLevy += StringUtil.nullToDoubleZero( (Double) row.get(PropertyMap.govLevy.getProp()) );
			pubLevy += StringUtil.nullToDoubleZero( (Double) row.get(PropertyMap.pubLevy.getProp()) );
			monthlyLevy += StringUtil.nullToDoubleZero( (Double) row.get(PropertyMap.totalLevies.getProp()) );
			wholeCharge += StringUtil.nullToDoubleZero( (Double) row.get(PropertyMap.totalCharge.getProp()) );
			subsidy += StringUtil.nullToDoubleZero( (Double) row.get(PropertyMap.subsity.getProp()) );
			lifeSubsidy += StringUtil.nullToDoubleZero( (Double) row.get(PropertyMap.lifeLineSubsidy.getProp()) );
			additionalSubsidy += StringUtil.nullToDoubleZero( (Double) row.get(PropertyMap.addtionalSubsidy.getProp()) );
		}
		
		if(requestTotal) {
    		Map<String, Object> totalMap = new HashMap<String, Object>();
    		totalMap.put(PropertyMap.numOfCust.getProp(), numOfCust	);
    		totalMap.put(PropertyMap.unitsConsumed.getProp(), unitsConsumed);
    		totalMap.put(PropertyMap.unitsCharge.getProp(), unitsCharge);
    		totalMap.put(PropertyMap.serviceCharge.getProp(), serviceCharge);
    		totalMap.put(PropertyMap.chargesWithoutLevies.getProp(), chargesWithoutLevies);
    		totalMap.put(PropertyMap.vat.getProp(), vat);
    		totalMap.put(PropertyMap.vatOnSubsidy.getProp(), vatOnSubsidy);
    		totalMap.put(PropertyMap.govLevy.getProp(), govLevy);
    		totalMap.put(PropertyMap.pubLevy.getProp(), pubLevy);
    		totalMap.put(PropertyMap.totalLevies.getProp(), monthlyLevy);
    		totalMap.put(PropertyMap.totalCharge.getProp(), wholeCharge);
    		totalMap.put(PropertyMap.subsity.getProp(), subsidy);
    		totalMap.put(PropertyMap.lifeLineSubsidy.getProp(), lifeSubsidy);
    		totalMap.put(PropertyMap.addtionalSubsidy.getProp(), additionalSubsidy);
    		data.put("total", totalMap);
		}
	}
}
