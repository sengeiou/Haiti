package com.aimir.service.mvm.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aimir.constants.CommonConstants.WeekDay;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.mvm.DayHUMDao;
import com.aimir.dao.mvm.DayTMDao;
import com.aimir.dao.mvm.MonthHUMDao;
import com.aimir.dao.mvm.MonthTMDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.DeviceModelDao;
import com.aimir.dao.system.DeviceVendorDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.model.device.Meter;
import com.aimir.model.mvm.DayHUM;
import com.aimir.model.mvm.DayTM;
import com.aimir.model.mvm.MonthHUM;
import com.aimir.model.mvm.MonthTM;
import com.aimir.model.system.Code;
import com.aimir.model.system.DeviceModel;
import com.aimir.model.system.DeviceVendor;
import com.aimir.model.system.Language;
import com.aimir.model.system.Location;
import com.aimir.service.mvm.TemperatureHumidityManager;
import com.aimir.service.mvm.bean.TemperatureHumidityData;
import com.aimir.util.BemsStatisticUtil;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;

@Service(value = "temperatureHumidityManager")
public class TemperatureHumidityManagerImpl implements
		TemperatureHumidityManager {
    private static Log log = LogFactory.getLog(TemperatureHumidityManagerImpl.class);
    
	@Autowired
	DayTMDao tmdao;

	@Autowired
	DayHUMDao hudao;

	@Autowired
	MonthTMDao monthtmDao;

	@Autowired
	MonthHUMDao monthhumDao;

	@Autowired
	MeterDao meterDao;

	@Autowired
	LocationDao locationDao;

	@Autowired
	DeviceModelDao deviceModelDao;

	@Autowired
	DeviceVendorDao deviceVendorDao;

	@Autowired
	CodeDao codeDao;

	private List<Meter> getMeterLocation(int locationId, int inner) {
		int modelId = 0;
		List<DeviceModel> deviceModel = null;
		if (inner == TemperatureHumidityData.inner) {
			deviceModel = getDeviceModel("HeatMeter", "NURITelecom");
		} else {
			deviceModel = getDeviceModel("WaterMeter", "NURITelecom");
		}

		if (deviceModel.size() > 0) {
			modelId = deviceModel.get(0).getId();
		} else {
			return new ArrayList<Meter>();
		}
		Set<Condition> endDeviceCondition = new HashSet<Condition>(0);
		endDeviceCondition.add(new Condition("location.id",
				new Object[] { locationId }, null, Restriction.EQ));
		endDeviceCondition.add(new Condition("model.id",
				new Object[] { modelId }, null, Restriction.EQ));

		return meterDao.findByConditions(endDeviceCondition);
	}

	private List<DeviceModel> getDeviceModel(String codeName, String vendorName) {
		Code code = getCode(codeName);
		DeviceVendor deviceVendor = getDeviceVendor(vendorName);
		return deviceModelDao.getDeviceModels(deviceVendor.getId(), code
				.getId());

	}

	private DeviceVendor getDeviceVendor(String name) {
		Set<Condition> condition = new HashSet<Condition>(0);
		condition.add(new Condition("name", new Object[] { name }, null,
				Restriction.EQ));
		return deviceVendorDao.findByCondition("name", "NURITelecom");
	}

	private Code getCode(String name) {
		return codeDao.getCodeByName(name);
	}

	private int[] locationIdWithEndDevice(int locationId, int inner,
			boolean searchParent) {
		int[] idList = new int[3];

		Location location = locationDao.get(locationId);

		List<Meter> innerMeter = null;
		try {
			innerMeter = getMeterLocation(locationId, inner);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (innerMeter.size() > 0) {

			idList[0] = locationId;
			idList[1] = innerMeter.get(0).getId();

			return idList;
		} else {
			if (!searchParent) {
				return null;
			}
			if (location != null) {
				if (location.getParent() != null) {
					locationIdWithEndDevice(location.getParent().getId(),
							inner, searchParent);
				} else {
					return null;
				}
			} else {
				return null;
			}
		}
		return null;
	}

	public TemperatureHumidityData getUsageChartData(String searchDateType,
			String date, int location) {

		try {
			int[] innerIdList = locationIdWithEndDevice(location,
					TemperatureHumidityData.inner, true);
			int[] outerIdList = null;
			if (innerIdList != null) {
				outerIdList = locationIdWithEndDevice(innerIdList[0],
						TemperatureHumidityData.outer, false);
				if (outerIdList != null) {

					return seachChartData(searchDateType, date, innerIdList[0],
							innerIdList[1], outerIdList[1]);
				} else {
					return seachChartData(searchDateType, date, innerIdList[0],
							innerIdList[1], -1);

				}
			} else {
				outerIdList = locationIdWithEndDevice(location,
						TemperatureHumidityData.outer, true);
				if (outerIdList != null) {

					return seachChartData(searchDateType, date, outerIdList[0],
							-1, outerIdList[1]);
				} else {
					return seachChartData(searchDateType, date, -1, -1, -1);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private TemperatureHumidityData seachChartData(String searchDateType,
			String date, int location, int mdsId, int outerMdsId) {

		List<DayTM> dayTMList = null;
		List<DayHUM> dayHUMList = null;
		List<DayTM> outerDayTMList = null;
		List<DayHUM> outerDayHUMList = null;
		List<MonthTM> monthTMList = null;
		List<MonthHUM> monthHUMList = null;
		List<MonthTM> outerMonthTMList = null;
		List<MonthHUM> outerMonthHUMList = null;
		List<Object> yearTMList = null;
		List<Object> yearHUMList = null;
		List<Object> outerYearTMList = null;
		List<Object> outerYearHUMList = null;

		log.info("searchDateType:" + searchDateType);
		log.info("date:" + date);
		log.info("location:" + location);
		log.info("mdsId:" + mdsId);
		log.info("outerMdsId:" + outerMdsId);

		Set<Condition> conditions = BemsStatisticUtil.getConditions(
				searchDateType, date, location, mdsId, 0, false, 0);
		Set<Condition> outerConditions = BemsStatisticUtil.getConditions(
				searchDateType, date, location, outerMdsId, 0, false, 0);
		if ("1".equals(searchDateType) || "2".equals(searchDateType)) {

			try {

				dayTMList = tmdao.getDayTMsByListCondition(conditions);
				dayHUMList = hudao.getDayHUMsByListCondition(conditions);

			} catch (Exception e) {
				e.printStackTrace();
			}
			if (outerMdsId != -1) {
				outerDayTMList = tmdao
						.getDayTMsByListCondition(outerConditions);
				outerDayHUMList = hudao
						.getDayHUMsByListCondition(outerConditions);
			}

			Language lang = locationDao.get(location).getSupplier().getLang();
			
			return makeTemperatureHumidityDayData(searchDateType, dayTMList,
					outerDayTMList, dayHUMList, outerDayHUMList, lang.getCode_2letter());

		} else if ("3".equals(searchDateType)) {

			try {

				monthTMList = monthtmDao.getMonthTMsByListCondition(conditions);
				monthHUMList = monthhumDao
						.getMonthHUMsByListCondition(conditions);
				outerMonthTMList = monthtmDao
						.getMonthTMsByListCondition(outerConditions);
				outerMonthHUMList = monthhumDao
						.getMonthHUMsByListCondition(outerConditions);

				return makeTemperatureHumidityMonthData(searchDateType,
						monthTMList, monthHUMList, outerMonthTMList,
						outerMonthHUMList);

			} catch (Exception e) {
				e.printStackTrace();
			}

		} else if ("4".equals(searchDateType) || "5".equals(searchDateType)) {
			int cnt = 4;
			if ("5".equals(searchDateType)) {
				cnt = 10;

			}

			try {
				yearTMList = monthtmDao.getUsageChartData(conditions);
				yearHUMList = monthhumDao.getUsageChartData(conditions);
				outerYearTMList = monthtmDao.getUsageChartData(outerConditions);
				outerYearHUMList = monthhumDao
						.getUsageChartData(outerConditions);
			} catch (Exception e) {
				e.printStackTrace();
			}

			return mmakeTemperatureHumidityYearData(searchDateType, yearTMList,
					yearHUMList, outerYearTMList, outerYearHUMList, date, cnt);

		}
		return null;
	}

	private TemperatureHumidityData mmakeTemperatureHumidityYearData(
			String searchDateType, List<Object> yearTMList,
			List<Object> yearHUMList, List<Object> outerYearTMList,
			List<Object> outerYearHUMList, String date, int cnt) {
		String[] term = BemsStatisticUtil.calerdarTerm(searchDateType, date);
		String[] labeldate = BemsStatisticUtil.setYearQuaterLable(
				searchDateType, term);

		String[] tmMaxMap = BemsStatisticUtil.setYearValue("MAX_", cnt,
				yearTMList);
		String[] tmMinMap = BemsStatisticUtil.setYearValue("MAX_", cnt,
				yearTMList);
		String[] huMaxMap = BemsStatisticUtil.setYearValue("MAX_", cnt,
				yearTMList);
		String[] huMinMap = BemsStatisticUtil.setYearValue("MAX_", cnt,
				yearTMList);
		String[] outerTmMaxMap = BemsStatisticUtil.setYearValue("MAX_", cnt,
				yearTMList);
		String[] outerTmMinMap = BemsStatisticUtil.setYearValue("MAX_", cnt,
				yearTMList);
		String[] outerHmMaxMap = BemsStatisticUtil.setYearValue("MAX_", cnt,
				yearTMList);
		String[] outerHmMinMap = BemsStatisticUtil.setYearValue("MAX_", cnt,
				yearTMList);
		return setChartData(searchDateType, labeldate, tmMaxMap, tmMinMap,
				huMaxMap, huMinMap, outerTmMaxMap, outerTmMinMap,
				outerHmMaxMap, outerHmMinMap);

	}

	private TemperatureHumidityData makeTemperatureHumidityMonthData(
			String searchDateType, List<MonthTM> monthTMList,
			List<MonthHUM> monthHUMList, List<MonthTM> outerMonthTMList,
			List<MonthHUM> outerMonthHUMList) {
		int arrCnt = 12;
		String[] date = BemsStatisticUtil.initStringArray(arrCnt, true);
		if (monthTMList.size() > 0) {
			date = new String[monthTMList.size()];
			for (int i = 0; i < monthTMList.size(); i++) {
				date[i] = monthTMList.get(i).getYyyymm().substring(4, 6);
			}
		}
		return setChartData(searchDateType, BemsStatisticUtil.initStringArray(
				24, true), BemsStatisticUtil
				.setDayMonthValue(monthTMList, true), BemsStatisticUtil
				.setDayMonthValue(monthTMList, false), BemsStatisticUtil
				.setDayMonthValue(monthHUMList, true), BemsStatisticUtil
				.setDayMonthValue(monthHUMList, false), BemsStatisticUtil
				.setDayMonthValue(outerMonthTMList, true), BemsStatisticUtil
				.setDayMonthValue(outerMonthTMList, false), BemsStatisticUtil
				.setDayMonthValue(outerMonthHUMList, true), BemsStatisticUtil
				.setDayMonthValue(outerMonthHUMList, false));

	}

	private TemperatureHumidityData makeTemperatureHumidityDayData(
			String searchDateType, List<DayTM> dayTMList,
			List<DayTM> outerDayTMList, List<DayHUM> dayHUMList,
			List<DayHUM> outerDayHUMList, String lang) {

		if ("1".equals(searchDateType)) {

			return setChartData(searchDateType, BemsStatisticUtil
					.initStringArray(24, true), BemsStatisticUtil
					.setHourValue(dayTMList), BemsStatisticUtil
					.initStringArray(24, false), BemsStatisticUtil
					.setHourValue(dayHUMList), BemsStatisticUtil
					.initStringArray(24, false), BemsStatisticUtil
					.setHourValue(outerDayTMList), BemsStatisticUtil
					.initStringArray(24, false), BemsStatisticUtil
					.setHourValue(outerDayHUMList), BemsStatisticUtil
					.initStringArray(24, true));

		} else if ("2".equals(searchDateType)) {
		    String[] dayOfWeek = null;
            if (lang.equals("ko"))
                dayOfWeek = new String[]{
                    WeekDay.Monday.getKorName(),
                    WeekDay.Tuesday.getKorName(),
                    WeekDay.Wednesday.getKorName(),
                    WeekDay.Thursday.getKorName(),
                    WeekDay.Friday.getKorName(),
                    WeekDay.Saturday.getKorName(),
                    WeekDay.Sunday.getKorName()
                    };
            else
                dayOfWeek = new String[]{
                    WeekDay.Monday.getEngName(),
                    WeekDay.Tuesday.getEngName(),
                    WeekDay.Wednesday.getEngName(),
                    WeekDay.Thursday.getEngName(),
                    WeekDay.Friday.getEngName(),
                    WeekDay.Saturday.getEngName(),
                    WeekDay.Sunday.getEngName()
            };
			if (dayTMList.size() > 0) {
			    dayOfWeek = new String[dayTMList.size()];
				for (int i = 0; i < dayTMList.size(); i++) {
				    dayOfWeek[i] = BemsStatisticUtil.weekDay(lang, dayTMList.get(i)
							.getYyyymmdd());
				}
			}
			return setChartData(searchDateType, dayOfWeek, BemsStatisticUtil
					.setDayMonthValue(dayTMList, true), BemsStatisticUtil
					.setDayMonthValue(dayTMList, false), BemsStatisticUtil
					.setDayMonthValue(dayHUMList, true), BemsStatisticUtil
					.setDayMonthValue(dayHUMList, false), BemsStatisticUtil
					.setDayMonthValue(outerDayTMList, true), BemsStatisticUtil
					.setDayMonthValue(outerDayTMList, false), BemsStatisticUtil
					.setDayMonthValue(outerDayHUMList, true), BemsStatisticUtil
					.setDayMonthValue(outerDayHUMList, false));

		}

		return null;
	}

	private TemperatureHumidityData setChartData(String searchDateType,
			String[] label, String[] tempMaxValue, String[] tempMinValue,
			String[] huMaxValue, String[] huMinValue,
			String[] outerTempMaxValue, String[] outerTempMinValue,
			String[] outerHuMaxValue, String[] outerHuMinValue) {
		TemperatureHumidityData retData = new TemperatureHumidityData(
				searchDateType);
		retData.setLabel(label);
		retData.setTempMaxvalue(tempMaxValue);
		retData.setTempMinvalue(tempMinValue);

		retData.setHuMaxvalue(huMaxValue);
		retData.setHuMinvalue(huMinValue);

		retData.setOuterTempMaxvalue(outerTempMaxValue);
		retData.setOuterTempMinvalue(outerTempMinValue);

		retData.setOuterHuMaxvalue(outerHuMaxValue);
		retData.setOuterHuMinvalue(outerHuMinValue);
		return retData;
	}

}
