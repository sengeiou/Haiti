package com.aimir.util;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.aimir.constants.CommonConstants.DateType;
import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.model.mvm.MeteringDay;
import com.aimir.model.mvm.MeteringDayTHU;
import com.aimir.util.Condition.Restriction;

public class BemsStatisticUtil {
	/*
	 * 지정일의 한글 요일 return, 챠트 라벨에 사용
	 */
	public static String weekDay(String lang, String yyyymmdd) {
		int y = Integer.parseInt(yyyymmdd.substring(0, 4));
		int m = Integer.parseInt(yyyymmdd.substring(4, 6));
		int d = Integer.parseInt(yyyymmdd.substring(6, 8));
		return CalendarUtil.getWeekDay(lang, y, m, d);
	}

	/**
	 * 날짜로 요일가져오기
	 *
	 * @param y
	 * @param m
	 * @param d
	 * @return
	 */
	public static int getWeekDay(int y, int m, int d) {

		Calendar cal = new GregorianCalendar();
		cal.set(Calendar.YEAR, y);
		cal.set(Calendar.MONTH, m - 1);
		cal.set(Calendar.DAY_OF_MONTH, d);

		return cal.get(Calendar.DAY_OF_WEEK);
	}

	public static String[] compareCalendarTerm(String searchDateType,
			String date, boolean compare) {
		String[] term = null;
		int y = Integer.parseInt(date.substring(0, 4));
		int m = Integer.parseInt(date.substring(4, 6));
		if ("1".equals(searchDateType)) {

			term = new String[2];
			String startDay = "";
			if (compare) {
				startDay = CalendarUtil.getDateWithoutFormat(date,
						Calendar.YEAR, -1);
			} else {
				startDay = CalendarUtil.getDateWithoutFormat(date,
						Calendar.DATE, -1);
			}
			term[0] = startDay;
			term[1] = date;
		} else if ("2".equals(searchDateType)) {

			term = new String[2];
			String startDay = "";
			String endDay = "";
			if (compare) {

				int week = CalendarUtil.getWeekOfMonth(date);
				int d = Integer.parseInt(date.substring(6, 8));

				endDay = CalendarUtil.getDateWeekDayOfWeek(y - 1 + "", date
						.substring(4, 6), week + "", getWeekDay(y, m, d) + "");
				startDay = CalendarUtil.getDateWithoutFormat(endDay,
						Calendar.DATE, -7);

				term[0] = startDay;
				term[1] = endDay;
			} else {
				startDay = CalendarUtil.getDateWithoutFormat(date,
						Calendar.DATE, -14);
				endDay = CalendarUtil.getDateWithoutFormat(date, Calendar.DATE,
						-7);

				term[0] = startDay;
				term[1] = endDay;
			}

		} else if ("3".equals(searchDateType)) {

			String startMonth = CalendarUtil.getDateWithoutFormat(date + "01",
					Calendar.MONTH, -24);
			String endMonth = CalendarUtil.getDateWithoutFormat(date + "01",
					Calendar.MONTH, -12);

			term = new String[2];
			term[0] = startMonth.substring(0, 6);
			term[1] = endMonth.substring(0, 6);

		} else if ("4".equals(searchDateType)) {
			term = new String[8];
			if (m >= 1 && m <= 3) {
				term[0] = (y - 2) + "01";
				term[1] = (y - 2) + "03";
				term[2] = (y - 2) + "04";
				term[3] = (y - 2) + "06";
				term[4] = (y - 2) + "07";
				term[5] = (y - 2) + "09";
				term[6] = (y - 2) + "10";
				term[7] = (y - 2) + "12";

			} else if (m >= 4 && m <= 6) {
				term[0] = (y - 2) + "04";
				term[1] = (y - 2) + "06";
				term[2] = (y - 2) + "07";
				term[3] = (y - 2) + "09";
				term[4] = (y - 2) + "10";
				term[5] = (y - 2) + "12";
				term[6] = (y - 1) + "01";
				term[7] = (y - 1) + "03";

			} else if (m >= 7 && m <= 9) {
				term[0] = (y - 2) + "07";
				term[1] = (y - 2) + "09";
				term[2] = (y - 2) + "10";
				term[3] = (y - 2) + "12";
				term[4] = (y - 1) + "01";
				term[5] = (y - 1) + "03";
				term[6] = (y - 1) + "04";
				term[7] = (y - 1) + "06";

			} else if (m >= 10 && m <= 12) {
				term[0] = (y - 2) + "10";
				term[1] = (y - 2) + "12";
				term[2] = (y - 1) + "01";
				term[3] = (y - 1) + "03";
				term[4] = (y - 1) + "04";
				term[5] = (y - 1) + "06";
				term[6] = (y - 1) + "07";
				term[7] = (y - 1) + "09";

			}

		}
		return term;
	}

	/*
	 * 지정일의 2: 일주일 전일자,3: 지정월의 1년 전월 시작 일자,4: 분기 별 시작일자 끝일자,5:10년 전의 시작월,끝월 검색
	 * 조건 및 챠트 라벨에 사용
	 */
	public static String[] calerdarTerm(String searchDateType, String yyyymm) {
		String[] term = null;
		int y = Integer.parseInt(yyyymm.substring(0, 4));
		int m = Integer.parseInt(yyyymm.substring(4, 6));
		if ("1".equals(searchDateType)) {
			// String startDay = CalendarUtil.getDateWithoutFormat(yyyymm,
			// Calendar.DATE, -1);

			term = new String[2];
			term[0] = yyyymm;
			term[1] = yyyymm;
		} else if ("2".equals(searchDateType)) {
			String startDay = CalendarUtil.getDateWithoutFormat(yyyymm,
					Calendar.DATE, -7);

			term = new String[2];
			term[0] = startDay;
			term[1] = yyyymm;
		} else if ("3".equals(searchDateType)) {
			String startMonth = CalendarUtil.getDateWithoutFormat(
					yyyymm + "01", Calendar.MONTH, -12);
			String endMonth = CalendarUtil.getDateWithoutFormat(yyyymm + "01",
					Calendar.MONTH, -1);

			term = new String[2];
			term[0] = startMonth.substring(0, 6);
			term[1] = endMonth.substring(0, 6);
		} else if ("4".equals(searchDateType)) {
			term = new String[8];
			if (m >= 1 && m <= 3) {
				term[0] = (y - 1) + "01";
				term[1] = (y - 1) + "03";
				term[2] = (y - 1) + "04";
				term[3] = (y - 1) + "06";
				term[4] = (y - 1) + "07";
				term[5] = (y - 1) + "09";
				term[6] = (y - 1) + "10";
				term[7] = (y - 1) + "12";
			} else if (m >= 4 && m <= 6) {
				term[0] = (y - 1) + "04";
				term[1] = (y - 1) + "06";
				term[2] = (y - 1) + "07";
				term[3] = (y - 1) + "09";
				term[4] = (y - 1) + "10";
				term[5] = (y - 1) + "12";
				term[6] = y + "01";
				term[7] = y + "03";
			} else if (m >= 7 && m <= 9) {
				term[0] = (y - 1) + "07";
				term[1] = (y - 1) + "09";
				term[2] = (y - 1) + "10";
				term[3] = (y - 1) + "12";
				term[4] = y + "01";
				term[5] = y + "03";
				term[6] = y + "04";
				term[7] = y + "06";
			} else if (m >= 10 && m <= 12) {
				term[0] = (y - 1) + "10";
				term[1] = (y - 1) + "12";
				term[2] = y + "01";
				term[3] = y + "03";
				term[4] = y + "04";
				term[5] = y + "06";
				term[6] = y + "07";
				term[7] = y + "09";
			}
		} else if ("5".equals(searchDateType)) {
			term = new String[20];
			term[0] = y - 10 + "01";
			term[1] = y - 10 + "12";
			term[2] = y - 9 + "01";
			term[3] = y - 9 + "12";
			term[4] = y - 8 + "01";
			term[5] = y - 8 + "12";
			term[6] = y - 7 + "01";
			term[7] = y - 7 + "12";
			term[8] = y - 6 + "01";
			term[9] = y - 6 + "12";
			term[10] = y - 5 + "01";
			term[11] = y - 5 + "12";
			term[12] = y - 4 + "01";
			term[13] = y - 4 + "12";
			term[14] = y - 3 + "01";
			term[15] = y - 3 + "12";
			term[16] = y - 2 + "01";
			term[17] = y - 2 + "12";
			term[18] = y - 1 + "01";
			term[19] = y - 1 + "12";
		}
		return term;
	}

	/*
	 * Hash 검색 결과를 String[]으로 반환 ,검색 조건 및 챠트 라벨에 사용
	 */
	@SuppressWarnings("unchecked")
	public static String[] setYearValue(String prefix, int cnt,
			List<Object> yearList) {

		String[] yearValue = new String[cnt];

		Map<String, Object> yearly = (HashMap<String, Object>) yearList.get(0);
		for (int i = 0; i < cnt; i++) {
			yearValue[i] = StringUtils.defaultIfEmpty(yearly.get(prefix + i)
					+ "", "0.0");

		}

		return yearValue;
	}

	/*
	 * 월,일별 검색 결과를 String[]으로 반환 검색 조건 및 챠트 라벨에 사용
	 */
	@SuppressWarnings("unchecked")
	public static String[] setDayMonthValue(List meteringList, boolean max) {

		if (meteringList == null) {
			return initStringArray(12, false);
		}
		if (meteringList.size() == 0) {
			return initStringArray(12, false);
		}
		String[] param = initStringArray(meteringList.size(), false);
		for (int i = 0; i < meteringList.size(); i++) {
			Object obj = meteringList.get(i);
			if (obj instanceof MeteringDay) {
				MeteringDay metering = (MeteringDay) meteringList.get(i);
				param[i] = StringUtils.defaultIfEmpty(metering.getTotal() + "",
						"0.0");
			} else if (obj instanceof MeteringDayTHU) {
				MeteringDayTHU metering = (MeteringDayTHU) meteringList.get(i);
				if (max) {
					param[i] = StringUtils.defaultIfEmpty(metering
							.getMaximumValue()
							+ "", "0.0");
				} else {
					param[i] = StringUtils.defaultIfEmpty(metering
							.getMinimumValue()
							+ "", "0.0");
				}
			}
		}
		return param;
	}

	/*
	 * String[] 설정 및 초기화 검색 조건 및 챠트 라벨에 사용
	 */
	public static String[] initStringArray(int cnt, boolean date) {
		String[] init = new String[cnt];
		for (int i = 0; i < cnt; i++) {
			if (date) {
				init[i] = i + 1 + "";
			} else {
				init[i] = "0.0";
			}
		}
		return init;
	}

	/*
	 * 시간별 검색 결과를 String[]으로 반환 ,검색 조건 및 챠트 라벨에 사용
	 */
	@SuppressWarnings("unchecked")
	public static String[] setHourValue(List meteringList) {
		String[] param = initStringArray(24, false);
		Object metering = null;

		if (meteringList != null && meteringList.size() > 0) {
			metering = meteringList.get(0);
		} else {
			return param;
		}

		if (metering instanceof MeteringDay) {
			MeteringDay meter = (MeteringDay) metering;
			param[0] = StringUtils.defaultIfEmpty(meter.getValue_00() + "",
					"0.0");
			param[1] = StringUtils.defaultIfEmpty(meter.getValue_01() + "",
					"0.0");
			param[2] = StringUtils.defaultIfEmpty(meter.getValue_02() + "",
					"0.0");
			param[3] = StringUtils.defaultIfEmpty(meter.getValue_03() + "",
					"0.0");
			param[4] = StringUtils.defaultIfEmpty(meter.getValue_04() + "",
					"0.0");
			param[5] = StringUtils.defaultIfEmpty(meter.getValue_05() + "",
					"0.0");
			param[6] = StringUtils.defaultIfEmpty(meter.getValue_06() + "",
					"0.0");
			param[7] = StringUtils.defaultIfEmpty(meter.getValue_07() + "",
					"0.0");
			param[8] = StringUtils.defaultIfEmpty(meter.getValue_08() + "",
					"0.0");
			param[9] = StringUtils.defaultIfEmpty(meter.getValue_09() + "",
					"0.0");
			param[10] = StringUtils.defaultIfEmpty(meter.getValue_10() + "",
					"0.0");
			param[11] = StringUtils.defaultIfEmpty(meter.getValue_11() + "",
					"0.0");
			param[12] = StringUtils.defaultIfEmpty(meter.getValue_12() + "",
					"0.0");
			param[13] = StringUtils.defaultIfEmpty(meter.getValue_13() + "",
					"0");
			param[14] = StringUtils.defaultIfEmpty(meter.getValue_14() + "",
					"0.0");
			param[15] = StringUtils.defaultIfEmpty(meter.getValue_15() + "",
					"0.0");
			param[16] = StringUtils.defaultIfEmpty(meter.getValue_16() + "",
					"0.0");
			param[17] = StringUtils.defaultIfEmpty(meter.getValue_17() + "",
					"0.0");
			param[18] = StringUtils.defaultIfEmpty(meter.getValue_18() + "",
					"0.0");
			param[19] = StringUtils.defaultIfEmpty(meter.getValue_19() + "",
					"0.0");
			param[20] = StringUtils.defaultIfEmpty(meter.getValue_20() + "",
					"0.0");
			param[21] = StringUtils.defaultIfEmpty(meter.getValue_21() + "",
					"0.0");
			param[22] = StringUtils.defaultIfEmpty(meter.getValue_22() + "",
					"0.0");
			param[23] = StringUtils.defaultIfEmpty(meter.getValue_23() + "",
					"0.0");
		} else if (metering instanceof MeteringDayTHU) {
			MeteringDayTHU meter = (MeteringDayTHU) metering;
			param[0] = StringUtils.defaultIfEmpty(meter.getValue_00() + "",
					"0.0");
			param[1] = StringUtils.defaultIfEmpty(meter.getValue_01() + "",
					"0.0");
			param[2] = StringUtils.defaultIfEmpty(meter.getValue_02() + "",
					"0.0");
			param[3] = StringUtils.defaultIfEmpty(meter.getValue_03() + "",
					"0.0");
			param[4] = StringUtils.defaultIfEmpty(meter.getValue_04() + "",
					"0.0");
			param[5] = StringUtils.defaultIfEmpty(meter.getValue_05() + "",
					"0.0");
			param[6] = StringUtils.defaultIfEmpty(meter.getValue_06() + "",
					"0.0");
			param[7] = StringUtils.defaultIfEmpty(meter.getValue_07() + "",
					"0.0");
			param[8] = StringUtils.defaultIfEmpty(meter.getValue_08() + "",
					"0.0");
			param[9] = StringUtils.defaultIfEmpty(meter.getValue_09() + "",
					"0.0");
			param[10] = StringUtils.defaultIfEmpty(meter.getValue_10() + "",
					"0.0");
			param[11] = StringUtils.defaultIfEmpty(meter.getValue_11() + "",
					"0.0");
			param[12] = StringUtils.defaultIfEmpty(meter.getValue_12() + "",
					"0.0");
			param[13] = StringUtils.defaultIfEmpty(meter.getValue_13() + "",
					"0.0");
			param[14] = StringUtils.defaultIfEmpty(meter.getValue_14() + "",
					"0.0");
			param[15] = StringUtils.defaultIfEmpty(meter.getValue_15() + "",
					"0.0");
			param[16] = StringUtils.defaultIfEmpty(meter.getValue_16() + "",
					"0.0");
			param[17] = StringUtils.defaultIfEmpty(meter.getValue_17() + "",
					"0.0");
			param[18] = StringUtils.defaultIfEmpty(meter.getValue_18() + "",
					"0.0");
			param[19] = StringUtils.defaultIfEmpty(meter.getValue_19() + "",
					"0.0");
			param[20] = StringUtils.defaultIfEmpty(meter.getValue_20() + "",
					"0.0");
			param[21] = StringUtils.defaultIfEmpty(meter.getValue_21() + "",
					"0.0");
			param[22] = StringUtils.defaultIfEmpty(meter.getValue_22() + "",
					"0.0");
			param[23] = StringUtils.defaultIfEmpty(meter.getValue_23() + "",
					"0.0");
		}

		return param;
	}

	/*
	 * 분기,10년 검색조건을 챠트 라벨로 변환 ,검색 조건 및 챠트 라벨에 사용
	 */
	public static String[] setYearQuaterLable(String searchDateType,
			String[] label) {
		String[] date = new String[label.length / 2];
		if ("4".equals(searchDateType)) {
			int j = 0;
			for (int i = 0; i < label.length; i = i + 2) {
				String year = label[i].substring(0, 4);
				String month = label[i].substring(4, 6);
				String setMonth = "";
				if ("01".equals(month)) {
					setMonth = "/1";
				} else if ("04".equals(month)) {
					setMonth = "/2";
				} else if ("07".equals(month)) {
					setMonth = "/3";
				} else if ("10".equals(month)) {
					setMonth = "/4";
				}
				date[j] = year + setMonth;
				j++;
			}

		} else if ("5".equals(searchDateType)) {
			int j = 0;
			for (int i = 0; i < label.length; i = i + 2) {
				date[j] = label[i].substring(0, 4);
				j++;
			}
		}
		return date;
	}

	/*
	 * 검색 조건 가져 오기 ,검색 조건 및 챠트 라벨에 사용
	 */
	public static Set<Condition> getConditions(String searchDateType,
			String date, int location, int meterId, int includeMeterId,
			boolean compare, int channel) {
		Set<Condition> conditions = new HashSet<Condition>(0);
		String[] term = null;
		conditions.add(new Condition("location.id", new Object[] { location },
				null, Restriction.EQ));

		if (includeMeterId == 0) {
			conditions.add(new Condition("meter.id", new Object[] { meterId },
					null, Restriction.EQ));
			term = calerdarTerm(searchDateType, date);
		} else if (includeMeterId == 1) {
			if (meterId != -1) {
				conditions.add(new Condition("enddevice.id",
						new Object[] { meterId }, null, Restriction.EQ));
			}
			conditions.add(new Condition("id.channel",
					new Object[] { channel }, null, Restriction.EQ));
			term = calerdarTerm(searchDateType, date);
		} else if (includeMeterId == 2) {

			if (meterId != -1) {
				conditions.add(new Condition("enddevice.id",
						new Object[] { meterId }, null, Restriction.EQ));
			}
			conditions.add(new Condition("id.channel",
					new Object[] { channel }, null, Restriction.EQ));
			term = compareCalendarTerm(searchDateType, date, compare);
		}

		if ("1".equals(searchDateType)) {
			conditions.add(new Condition("id.yyyymmdd",
					new Object[] { term[0] }, null, Restriction.EQ));
			conditions.add(new Condition("id.yyyymmdd", new Object[] {}, null,
					Restriction.ORDERBY));

		} else if ("2".equals(searchDateType)) {
			conditions.add(new Condition("id.yyyymmdd", new Object[] { term[0],
					term[1] }, null, Restriction.BETWEEN));
			conditions.add(new Condition("id.yyyymmdd", new Object[] {}, null,
					Restriction.ORDERBY));

		} else if ("3".equals(searchDateType)) {
			conditions.add(new Condition("id.yyyymm", new Object[] { term[0],
					term[1] }, null, Restriction.BETWEEN));
			conditions.add(new Condition("id.yyyymm", new Object[] {}, null,
					Restriction.ORDERBY));

		} else if ("4".equals(searchDateType)) {
			conditions.add(new Condition("date", new Object[] { term[0],
					term[1] }, "0", Restriction.EQ));
			conditions.add(new Condition("date", new Object[] { term[2],
					term[3] }, "1", Restriction.EQ));
			conditions.add(new Condition("date", new Object[] { term[4],
					term[5] }, "2", Restriction.EQ));
			conditions.add(new Condition("date", new Object[] { term[6],
					term[7] }, "3", Restriction.EQ));
			conditions.add(new Condition("startEndDate", new Object[] {
					term[0], term[7] }, "4", Restriction.EQ));
		} else if ("5".equals(searchDateType)) {
			conditions.add(new Condition("date", new Object[] { term[0],
					term[1] }, "0", Restriction.EQ));
			conditions.add(new Condition("date", new Object[] { term[2],
					term[3] }, "1", Restriction.EQ));
			conditions.add(new Condition("date", new Object[] { term[4],
					term[5] }, "2", Restriction.EQ));
			conditions.add(new Condition("date", new Object[] { term[6],
					term[7] }, "3", Restriction.EQ));
			conditions.add(new Condition("date", new Object[] { term[8],
					term[9] }, "4", Restriction.EQ));
			conditions.add(new Condition("date", new Object[] { term[10],
					term[11] }, "5", Restriction.EQ));
			conditions.add(new Condition("date", new Object[] { term[12],
					term[13] }, "6", Restriction.EQ));
			conditions.add(new Condition("date", new Object[] { term[14],
					term[15] }, "7", Restriction.EQ));
			conditions.add(new Condition("date", new Object[] { term[16],
					term[17] }, "8", Restriction.EQ));
			conditions.add(new Condition("date", new Object[] { term[18],
					term[19] }, "9", Restriction.EQ));
			conditions.add(new Condition("startEndDate", new Object[] {
					term[0], term[19] }, "9", Restriction.EQ));
		}
		return conditions;
	}

	public Double getUsageCharge(Map<String, Object> params) {

		String serviceType = (String) params.get("serviceType");
		String dateType = (String) params.get("dateType");
		Double usage = (Double) params.get("usage");
		Integer period = (Integer) params.get("period");

		Properties prop = new Properties();
		try {
			prop.load(getClass().getClassLoader().getResourceAsStream(
					"bems_charge.properties"));
		} catch (IOException e) {
			return 0.0;
		}

		Double basicPrice = 0.0;
		Double usageUnitPrice = 0.0;
		if (MeterType.EnergyMeter.getServiceType().equals(serviceType)) {
			basicPrice = Double.parseDouble(prop
					.getProperty("energy.basicPrice"));
			usageUnitPrice = Double.parseDouble(prop
					.getProperty("energy.usageUnitPrice"));
		} else if (MeterType.GasMeter.getServiceType().equals(serviceType)) {
			basicPrice = Double.parseDouble(prop.getProperty("gas.basicPrice"));
			usageUnitPrice = Double.parseDouble(prop
					.getProperty("gas.usageUnitPrice"));
		} else if (MeterType.WaterMeter.getServiceType().equals(serviceType)) {
			basicPrice = Double.parseDouble(prop
					.getProperty("water.basicPrice"));
			usageUnitPrice = Double.parseDouble(prop
					.getProperty("water.usageUnitPrice"));
		} else if (MeterType.HeatMeter.getServiceType().equals(serviceType)) {
			basicPrice = Double.parseDouble(prop
					.getProperty("heat.basicPrice"));
			usageUnitPrice = Double.parseDouble(prop
					.getProperty("heat.usageUnitPrice"));
		}

		Double charge = 0.0;

		if (DateType.DAILY.getCode().equals(dateType)) {
			charge = charge + Math.round(basicPrice * period / 30);// 기본요금
			charge = charge + usage * usageUnitPrice;// 사용량별요금
		} else if (DateType.MONTHLY.getCode().equals(dateType)) {
			charge = charge + period * basicPrice;// 기본요금
			charge = charge + (usage * usageUnitPrice);// 사용량별요금
		}

		return charge;
	}

	public String getNumberFormat() {

		Properties prop = new Properties();
		try {
			prop.load(getClass().getClassLoader().getResourceAsStream(
					"bems_charge.properties"));
		} catch (IOException e) {
			return "###,###,###,###.###";
		}

		String numberFormat = prop.getProperty("pattern.format.usageNumber");
		return numberFormat;
	}

	// public Map<Integer,String> getLocationConverting() {
	// Map<Integer,String> retMap= new HashMap<Integer,String>();
	// Properties prop = new Properties();
	// try {
	// prop.load(getClass().getClassLoader().getResourceAsStream(
	// "bems_charge.properties"));
	// } catch (IOException e) {
	// return retMap;
	// }
	// String convert = prop.getProperty("location.converting");
	// String[] convertList=convert.split(",");
	//
	// for(String loc:convertList){
	// if(loc != null && loc.length()>0)
	// retMap.put(Integer.parseInt(loc), loc);
	// }
	// return retMap;
	// }

	public InputStream getExhibitionParamStream() {

		return getClass().getClassLoader()
				.getResourceAsStream("exhibition.xml");
	}

	public InputStream getExhibitionParamStreamJeju() {

		return getClass().getClassLoader()
				.getResourceAsStream("exhibitionJeju.xml");
	}

	public static BigDecimal[] getExpectData(Double[] post, Double[] current) {

		BigDecimal[] expect = new BigDecimal[post.length];

		for (int i = 0; i < expect.length; i++) {
			expect[i] = new BigDecimal(0);
		}

		BigDecimal currentRatio = new BigDecimal(1d);
		BigDecimal postRatio = new BigDecimal(1d);
		BigDecimal ratio = new BigDecimal(1d);

		BigDecimal postSum = new BigDecimal(0d);
		BigDecimal currentSum = new BigDecimal(0d);

		BigDecimal zero = new BigDecimal(0);
		BigDecimal one = new BigDecimal(1);

		for (int i = 0; i < post.length; i++) {
			postSum = postSum.add(new BigDecimal(post[i]).setScale(3,
					BigDecimal.ROUND_HALF_UP));
			currentSum = currentSum.add(new BigDecimal(current[i]).setScale(3,
					BigDecimal.ROUND_HALF_UP));


			//System.out.println("post["+i+"]:"+post[i]);
			//System.out.println("current["+i+"]:"+current[i]);
			if (current[i] > 0d) {
				expect[i] = new BigDecimal(current[i]).setScale(3,
						BigDecimal.ROUND_HALF_UP);
				currentRatio = currentSum.divide(new BigDecimal(i + 1), 1,
						BigDecimal.ROUND_HALF_UP);
				postRatio = postSum.divide(new BigDecimal(i + 1), 1,
						BigDecimal.ROUND_HALF_UP);

				if (postRatio.compareTo(zero) == 0) {
					postRatio = one;
				}

				ratio = currentRatio.divide(postRatio, 1,
						BigDecimal.ROUND_HALF_UP);
			} else {

				expect[i] = new BigDecimal(post[i]).multiply(ratio).setScale(3,
						BigDecimal.ROUND_HALF_UP);
			}
			//System.out.println("expect["+i+"]:"+expect[i]);
		}

		return expect;
	}

	public static BigDecimal getDailyExpect(Map<String, Object> preDaily,
			Map<String, Object> daily, String energyType) {

		Double[] expectDaily = new Double[24];
		Double[] preExpectDaily = new Double[24];

		for (int i = 23; i > -1; i--) {
			String hh = TimeUtil.to2Digit(i);

			preExpectDaily[i] = preDaily.get(energyType + "USAGE_" + hh) == null ? 0d
					: ((Number) preDaily.get(energyType
							+ "USAGE_" + hh)).doubleValue();

			if (daily.get(energyType + "USAGE_" + hh) != null) {
				expectDaily[i] = ((Number)daily
						.get(energyType + "USAGE_" + hh)).doubleValue();
			} else {
				expectDaily[i] = 0d;

			}

		}

		BigDecimal[] expectD = BemsStatisticUtil.getExpectData(preExpectDaily,
				expectDaily);
		BigDecimal exD = new BigDecimal(0);
		for (int i = 0; i < 24; i++) {
			// System.out.println("PRE[" + i + "]:" + preExpectDaily[i]
			// + ":EXPECT[" + i + "]:" + expectD[i] + ":CURR:"
			// + expectDaily[i]);
			exD = exD.add(expectD[i]);
		}
		return exD;
	}

	public static BigDecimal getWeeklyExpect(Map<String, Object> preWeekly,
			Map<String, Object> weekly, String energyType) {

		Double[] expectWeekly = new Double[7];
		Double[] preExpectWeekly = new Double[7];
		for (int i = 7; i > 0; i--) {
			String hh = TimeUtil.to2Digit(i);

			preExpectWeekly[i - 1] = preWeekly.get(energyType + "USAGE_" + hh) == null ? 0d
					: ((Number)preWeekly.get(energyType
							+ "USAGE_" + hh)).doubleValue();
			if (weekly.get(energyType + "USAGE_" + hh) != null) {
				expectWeekly[i - 1] = ((Number)weekly
						.get(energyType + "USAGE_" + hh)).doubleValue();
			} else {
				expectWeekly[i - 1] = 0d;
			}
		}

		BigDecimal[] expectW = BemsStatisticUtil.getExpectData(preExpectWeekly,
				expectWeekly);
		BigDecimal exW = new BigDecimal(0);
		for (int i = 0; i < 7; i++) {
			// System.out.println("PRE[" + i + "]:" + preExpectWeekly[i]
			// + ":EXPECT[" + i + "]:" + expectW[i] + ":CURR:"
			// + expectWeekly[i]);
			exW = exW.add(expectW[i]);
		}
		return exW;
	}

	public static BigDecimal getMonthlyExpect(Map<String, Object> preMonthly,
			Map<String, Object> monthly, String energyType) {

		Double[] expectMonthly = new Double[31];
		Double[] preExpectMonthly = new Double[31];
		for (int i = 31; i > 0; i--) {
			String hh = TimeUtil.to2Digit(i);
			preExpectMonthly[i - 1] = preMonthly
					.get(energyType + "USAGE_" + hh) == null ? 0d
					: ((Number)preMonthly.get(energyType
							+ "USAGE_" + hh)).doubleValue();
			// System.out.println("preExpectMonthly[" + (i - 1) + "]:"
			// + preExpectMonthly[i - 1]);
			if (monthly.get(energyType + "USAGE_" + hh) != null) {
				expectMonthly[i - 1] = ((Number)monthly
						.get(energyType + "USAGE_" + hh)).doubleValue();
			} else {
				expectMonthly[i - 1] = 0d;

			}

		}

		BigDecimal[] expectM = BemsStatisticUtil.getExpectData(
				preExpectMonthly, expectMonthly);
		BigDecimal exM = new BigDecimal(0);
		Calendar c = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("dd");

		c.set(Calendar.DATE, c.getActualMaximum(Calendar.DAY_OF_MONTH));

		int dayOfMonth = Integer.parseInt(formatter.format(c.getTime()));
		BigDecimal lastValue = new BigDecimal(0);
		for (int i = 0; i < dayOfMonth; i++) {
			// System.out.println("PRE[" + i + "]:" + preExpectMonthly[i]
			// + ":EXPECT[" + i + "]:" + expectM[i] + ":CURR:"
			// + expectMonthly[i]);
			if (dayOfMonth >= i && expectM[i].intValue() == 0) {
				exM = exM.add(lastValue);
			} else {
				exM = exM.add(expectM[i]);
				lastValue = expectM[i];
			}
		}
		return exM;

	}
}
