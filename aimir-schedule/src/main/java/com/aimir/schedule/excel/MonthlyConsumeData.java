package com.aimir.schedule.excel;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aimir.dao.system.PrepaymentLogDao;
import com.aimir.model.system.PrepaymentLog;
import com.aimir.util.StringUtil;
import com.aimir.schedule.excel.ExcelUtil.PropertyMap;

@Component
public class MonthlyConsumeData {
	private static Log logger = LogFactory.getLog(MonthlyConsumeData.class);
	
	@Autowired
	PrepaymentLogDao prepaymentLogDao;
	
	public enum ConsumeLevel {
		Zero("zero unit", 0d,0d), 
		Lv1("1-50", 1d, 50d),
		Lv2("51-150",51d, 150d),
		Lv3("151-200", 151d, 200d),
		Lv4("201-300", 201d, 300d),
		Lv5("301-400", 301d, 400d),
		Lv6("401-500", 401d, 500d),
		Lv7("501-600",501d, 600d),
		Lv8("601-700", 601d, 700d),
		Lv9("701-800", 701d, 800d),
		Lv10("801-900", 801d, 900d),
		Lv11("901-1000", 901d, 1000d),
		Lv12("1001-1100", 1001d, 1100d),
		Lv13("1101-1200", 1101d, 1200d),
		Lv14("1201-1300", 1201d, 1300d),
		Lv15("1301-1400", 1301d, 1400d),
		Lv16("1401-1500", 1401d, 1500d),
		Lv17("1501-2000", 1501d, 2000d),
		Lv18("2001-3000", 2001d, 3000d),
		Lv19("3001-4000", 3001d, 4000d),
		Lv20("4001-5000", 4001d, 5000d),
		Lv21("5001-10000",5001d, 10000d),
		Lv22("abv-10000", 10000d, null);
		
		public String getName() {
			return name;
		}
		
		public Double getMin() {
			return min;
		}

		public Double getMax() {
			return max;
		}
		private String name;
		private Double min;
		private Double max;
		
		ConsumeLevel (String name, Double min, Double max) {
			this.name = name;
			this.min = min;
			this.max = max;
		}
		
		public static ConsumeLevel getConsumeLevelByUsed(Double value) {
			ConsumeLevel[] levels = ConsumeLevel.values();
			value = Math.ceil(value);
			for ( ConsumeLevel level : levels ) {
				Double min = level.getMin();
				Double max= level.getMax();
				
				if ( max == null && value >= min ) {
					return level;
				} else if ( value >= min && value <= max ) {
					return level;
				} 
			}
			
			return null;
		}
	}

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
	public static LinkedHashMap<String, Map<String, Object>> makeExcelData( List<PrepaymentLog> logList, String TariffName) {
		List<PrepaymentLog> monthlyLog = logList;
		LinkedHashMap<String, Map<String, Object>> table = new LinkedHashMap<String, Map<String, Object>>();
		
		Integer totalNumOfCust = 0;		
		Double totalUnitsConsumed = 0d;
		Double totalUnitsCharge = 0d;
		Double totalServiceCharge = 0d;
		Double totalChargesWithoutLevies = 0d;
		Double totalVat = 0d;
		Double totalVatOnSubsidy = 0d;
		Double totalGovLevy = 0d;
		Double totalPubLevy = 0d;
		Double totalMonthlyLevy = 0d;
		Double totalWholeCharge = 0d;
		Double totalSubsidy = 0d;
		Double totalLifeLineSubsidy = 0d;
		Double totalAdditionalSubsidy = 0d;
		try {
		logger.debug("\n logCount: " + monthlyLog.size());
		
		for ( PrepaymentLog log : monthlyLog ) {
			Double totalUsed = StringUtil.nullToDoubleZero(log.getUsedConsumption());
			ConsumeLevel level = ConsumeLevel.getConsumeLevelByUsed( totalUsed );
			
			if(level != null && level.getName() != null) {
				Map<String, Object> record = table.get(level.getName());
				
				if ( record == null ) {
					record = new HashMap<String, Object>();
					record.put(PropertyMap.cons.getProp(), level.getName());
				}
				
				Integer numOfCust = 
						record.get(PropertyMap.numOfCust.getProp()) == null ? 
								new Integer(0): (Integer) record.get(PropertyMap.numOfCust.getProp());
				numOfCust++;
				totalNumOfCust ++;
				
				Double unitsConsumed = 
						StringUtil.nullToDoubleZero( (Double) record.get(PropertyMap.unitsConsumed.getProp()) );
				unitsConsumed += StringUtil.nullToDoubleZero(log.getUsedConsumption());
				totalUnitsConsumed += StringUtil.nullToDoubleZero(log.getUsedConsumption());
			
				Double unitsCharge = 
						StringUtil.nullToDoubleZero( (Double) record.get(PropertyMap.unitsCharge.getProp()) );
				unitsCharge += log.getMonthlyTotalAmount();
				totalUnitsCharge += log.getMonthlyTotalAmount();
				
				Double serviceCharge = 
						StringUtil.nullToDoubleZero( (Double) record.get(PropertyMap.serviceCharge.getProp()) );
				serviceCharge += StringUtil.nullToDoubleZero(log.getMonthlyServiceCharge());
				totalServiceCharge += StringUtil.nullToDoubleZero(log.getMonthlyServiceCharge());
				
				Double subsidy = 
						StringUtil.nullToDoubleZero( (Double) record.get(PropertyMap.subsity.getProp()) );
				subsidy += StringUtil.nullToDoubleZero(log.getSubsidy());
				totalSubsidy += StringUtil.nullToDoubleZero(log.getSubsidy());
				
				Double lifeLineSubsidy = 
						StringUtil.nullToDoubleZero( (Double) record.get(PropertyMap.lifeLineSubsidy.getProp()) );
				lifeLineSubsidy += StringUtil.nullToDoubleZero(log.getLifeLineSubsidy());
				totalLifeLineSubsidy += StringUtil.nullToDoubleZero(log.getLifeLineSubsidy());
				
				Double additionalSubsidy = 
						StringUtil.nullToDoubleZero( (Double) record.get(PropertyMap.addtionalSubsidy.getProp()) );
				additionalSubsidy += StringUtil.nullToDoubleZero(log.getAdditionalSubsidy());
				totalAdditionalSubsidy += StringUtil.nullToDoubleZero(log.getAdditionalSubsidy());
				
				Double allSubsidy = 0d;
				if("Residential".equals(TariffName)) {
					allSubsidy = StringUtil.nullToDoubleZero(log.getSubsidy()) 
							+ StringUtil.nullToDoubleZero(log.getLifeLineSubsidy()) + StringUtil.nullToDoubleZero(log.getAdditionalSubsidy());
				} else if("Non Residential".equals(TariffName)) {
					allSubsidy = StringUtil.nullToDoubleZero(log.getAdditionalSubsidy());
				}
				
				Double chargesWithoutLevies = 
						StringUtil.nullToDoubleZero( (Double) record.get(PropertyMap.chargesWithoutLevies.getProp()) );
				Double logChargesWithoutLevies = ( log.getMonthlyTotalAmount() + StringUtil.nullToDoubleZero(log.getMonthlyServiceCharge()));
				chargesWithoutLevies += logChargesWithoutLevies;
				totalChargesWithoutLevies += logChargesWithoutLevies;
				
				Double vat = 
						StringUtil.nullToDoubleZero( (Double) record.get(PropertyMap.vat.getProp()) );
				Double logVat = StringUtil.nullToDoubleZero(log.getVat());
				vat += logVat;
				totalVat += logVat;
				
				Double vatOnSubsidy = StringUtil.nullToDoubleZero( (Double) record.get(PropertyMap.vatOnSubsidy.getProp()));
				Double logVatOnSubsidy = StringUtil.nullToDoubleZero(log.getVatOnSubsidy());
				vatOnSubsidy += logVatOnSubsidy;
				totalVatOnSubsidy += logVatOnSubsidy;
				
				Double govLevy = 
						StringUtil.nullToDoubleZero( (Double) record.get(PropertyMap.govLevy.getProp()) );
				Double logGovLevy = StringUtil.nullToDoubleZero(log.getGovLevy());
				govLevy += logGovLevy;
				totalGovLevy += logGovLevy;
				
				Double pubLevy = 
						StringUtil.nullToDoubleZero( (Double) record.get(PropertyMap.pubLevy.getProp()) );
				Double logPubLevy = StringUtil.nullToDoubleZero(log.getPublicLevy());
				pubLevy += logPubLevy;
				totalPubLevy += logPubLevy;
				
				Double totalLevies = 
						StringUtil.nullToDoubleZero( (Double) record.get(PropertyMap.totalLevies.getProp()) );
				Double logTotalLevies = logGovLevy + logPubLevy;
				totalLevies += logTotalLevies;
				totalMonthlyLevy += logTotalLevies; 
				
				Double totalCharge = StringUtil.nullToDoubleZero( (Double) record.get(PropertyMap.totalCharge.getProp()) );
				if("Residential".equals(TariffName)) {
					totalCharge += logChargesWithoutLevies + logTotalLevies;
					totalWholeCharge += logChargesWithoutLevies + logTotalLevies;
				} else if("Non Residential".equals(TariffName)) {
					totalCharge += logChargesWithoutLevies + logTotalLevies + logVat;
					totalWholeCharge += logChargesWithoutLevies + logTotalLevies + logVat;
				}
				
				record.put(PropertyMap.numOfCust.getProp(), numOfCust);
				record.put(PropertyMap.unitsConsumed.getProp(), unitsConsumed);
				record.put(PropertyMap.unitsCharge.getProp(), unitsCharge);
				record.put(PropertyMap.serviceCharge.getProp(), serviceCharge);
				record.put(PropertyMap.chargesWithoutLevies.getProp(), chargesWithoutLevies);
				record.put(PropertyMap.vat.getProp(), vat);
				record.put(PropertyMap.vatOnSubsidy.getProp(), vatOnSubsidy);
				record.put(PropertyMap.govLevy.getProp(), govLevy);
				record.put(PropertyMap.pubLevy.getProp(), pubLevy);
				record.put(PropertyMap.totalLevies.getProp(), totalLevies);
				record.put(PropertyMap.totalCharge.getProp(), totalCharge);
				record.put(PropertyMap.subsity.getProp(), subsidy);
				record.put(PropertyMap.lifeLineSubsidy.getProp(), lifeLineSubsidy);
				record.put(PropertyMap.addtionalSubsidy.getProp(), additionalSubsidy);
				table.put(level.getName(), record);
			}
		}
		
		Map<String, Object> record = new HashMap<String, Object>();
		record.put(PropertyMap.numOfCust.getProp(), totalNumOfCust);
		record.put(PropertyMap.unitsConsumed.getProp(), totalUnitsConsumed);
		record.put(PropertyMap.unitsCharge.getProp(), totalUnitsCharge);
		record.put(PropertyMap.serviceCharge.getProp(), totalServiceCharge);
		record.put(PropertyMap.chargesWithoutLevies.getProp(), totalChargesWithoutLevies);
		record.put(PropertyMap.vat.getProp(), totalVat);
		record.put(PropertyMap.vatOnSubsidy.getProp(), totalVatOnSubsidy	);
		record.put(PropertyMap.govLevy.getProp(), totalGovLevy);
		record.put(PropertyMap.pubLevy.getProp(), totalPubLevy);
		record.put(PropertyMap.totalLevies.getProp(), totalMonthlyLevy);
		record.put(PropertyMap.totalCharge.getProp(), totalWholeCharge);
		record.put(PropertyMap.subsity.getProp(), totalSubsidy);
		record.put(PropertyMap.lifeLineSubsidy.getProp(), totalLifeLineSubsidy);
		record.put(PropertyMap.addtionalSubsidy.getProp(), totalAdditionalSubsidy);
		table.put("total", record);		
		
		logger.info("table size: " + table.size());
		
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e);
		}
		return table;
	}

}
