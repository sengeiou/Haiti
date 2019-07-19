package com.aimir.service.mvm;

import com.aimir.service.mvm.bean.TemperatureHumidityData;

public interface TemperatureHumidityManager {

	
	public TemperatureHumidityData getUsageChartData(String searchDateType,
			String date, int location);

}
