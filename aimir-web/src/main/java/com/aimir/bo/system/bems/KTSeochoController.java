package com.aimir.bo.system.bems;

import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.DateType;
import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.model.system.EnergySavingGoal2;
import com.aimir.service.system.EnergySavingGoal2Manager;
import com.aimir.service.system.KTSeochoExhibitManager;
import com.aimir.util.BemsStatisticUtil;
import com.aimir.util.CalendarUtil;
import com.aimir.util.TimeUtil;

@Controller
public class KTSeochoController {

	Log logger = LogFactory.getLog(KTSeochoController.class);

	@Autowired
	EnergySavingGoal2Manager energySavingGoal2Manager;

	@Autowired
	KTSeochoExhibitManager ktSeochoExhibitManager;

	@RequestMapping(value = "/gadget/bems/ktSeocho.ex")
	public ModelAndView getKtSeocho(
			@RequestParam("supplierId") String supplierId,
			@RequestParam("locationId") String locationId) {
		String date = CalendarUtil.getCurrentDate();
		BigDecimal emRatio = new BigDecimal(1);

		BigDecimal gmRatio = new BigDecimal(10);

		BigDecimal wmRatio = new BigDecimal(1.3);

		BigDecimal eDG = getGoalUsage(supplierId, locationId,
				CommonConstants.DateType.DAILY.getCode(), "EM", "1.3.1.1", date);
		BigDecimal eWG = getGoalUsage(supplierId, locationId,
				CommonConstants.DateType.WEEKLY.getCode(), "EM", "1.3.1.1",
				date);
		BigDecimal eMG = getGoalUsage(supplierId, locationId,
				CommonConstants.DateType.MONTHLY.getCode(), "EM", "1.3.1.1",
				date);

		BigDecimal wDG = getGoalUsage(supplierId, locationId,
				CommonConstants.DateType.DAILY.getCode(), "WM", "1.3.1.2", date);
		BigDecimal wWG = getGoalUsage(supplierId, locationId,
				CommonConstants.DateType.WEEKLY.getCode(), "WM", "1.3.1.2",
				date);
		BigDecimal wMG = getGoalUsage(supplierId, locationId,
				CommonConstants.DateType.MONTHLY.getCode(), "WM", "1.3.1.2",
				date);

		BigDecimal gDG = getGoalUsage(supplierId, locationId,
				CommonConstants.DateType.DAILY.getCode(), "GM", "1.3.1.3", date);
		BigDecimal gWG = getGoalUsage(supplierId, locationId,
				CommonConstants.DateType.WEEKLY.getCode(), "GM", "1.3.1.3",
				date);
		BigDecimal gMG = getGoalUsage(supplierId, locationId,
				CommonConstants.DateType.MONTHLY.getCode(), "GM", "1.3.1.3",
				date);

		Map<String, Object> energyData = ktSeochoExhibitManager
				.getEnergyData(locationId);

		Map<String, Object> daily = (Map<String, Object>) energyData
				.get("DAILY");
		Map<String, Object> weekly = (Map<String, Object>) energyData
				.get("WEEKLY");
		Map<String, Object> monthly = (Map<String, Object>) energyData
				.get("MONTHLY");

		Map<String, Object> preDaily = (Map<String, Object>) energyData
				.get("PREDAILY");
		Map<String, Object> preWeekly = (Map<String, Object>) energyData
				.get("PREWEEKLY");
		Map<String, Object> preMonthly = (Map<String, Object>) energyData
				.get("PREMONTHLY");

		// BigDecimal emMonthlyDiff = this.getGoalDiff(new BigDecimal(800000),
		// emMonthlyGoal);

		BigDecimal exD = getDailyExpect(preDaily, daily, "EM");

		BigDecimal exGD = getDailyExpect(preDaily, daily, "GM");

		BigDecimal exWD = getDailyExpect(preDaily, daily, "WM");

		BigDecimal exW = getWeeklyExpect(preWeekly, weekly, "EM");

		BigDecimal exGW = getWeeklyExpect(preWeekly, weekly, "GM");

		BigDecimal exWW = getWeeklyExpect(preWeekly, weekly, "WM");

		BigDecimal exM = getMonthlyExpect(preMonthly, monthly, "EM");

		BigDecimal exGM = getMonthlyExpect(preMonthly, monthly, "GM");

		BigDecimal exWM = getMonthlyExpect(preMonthly, monthly, "WM");

//		System.out.println("em daily expect:" + exD + ":goal:" + eDG
//				+ ":usage:" + getUsage(daily, "EM"));
//		System.out.println("em weekly expect:" + exW + ":goal:" + eWG
//				+ ":usage:" + getUsage(weekly, "EM"));
//		System.out.println("em monthly expect:" + exM + ":goal:" + eMG
//				+ ":usage:" + getUsage(monthly, "EM"));
//
//		System.out.println("wm daily expect:" + exWD + ":goal:" + wDG
//				+ ":usage:" + getUsage(daily, "WM"));
//		System.out.println("wm weekly expect:" + exWW + ":goal:" + wWG
//				+ ":usage:" + getUsage(weekly, "WM"));
//		System.out.println("wm monthly expect:" + exWM + ":goal:" + wMG
//				+ ":usage:" + getUsage(monthly, "WM"));
//
//		System.out.println("gm daily expect:" + exGD + ":goal:" + gDG
//				+ ":usage:" + getUsage(daily, "GM"));
//		System.out.println("gm weekly expect:" + exGW + ":goal:" + gWG
//				+ ":usage:" + getUsage(weekly, "GM"));
//		System.out.println("gm monthly expect:" + exGM + ":goal:" + gMG
//				+ ":usage:" + getUsage(monthly, "GM"));

		// ktSeochoExhibitManager.getEnergyDataByLocation("11");
		Map<String, Object> retDaily = getPeriodMap(daily, DateType.DAILY
				.getCode(), exD, exWD, exGD, eDG.multiply(emRatio), wDG
				.multiply(wmRatio), gDG.multiply(gmRatio));

		Map<String, Object> retWeekly = getPeriodMap(weekly, DateType.WEEKLY
				.getCode(), exW, exWW, exGW, eWG.multiply(emRatio), wWG
				.multiply(wmRatio), gWG.multiply(gmRatio));

		Map<String, Object> retMonthly = getPeriodMap(monthly, DateType.MONTHLY
				.getCode(), exM, exWM, exGM, eMG.multiply(emRatio), wMG
				.multiply(wmRatio), gMG.multiply(gmRatio));

		ModelAndView mav = new ModelAndView("jsonView");
		mav.addObject("weather", getWeather());
		mav.addObject("daily", retDaily);
		mav.addObject("weekly", retWeekly);
		mav.addObject("monthly", retMonthly);
		Calendar c = Calendar.getInstance();
		SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy.MM.dd");

		String currentDate = formatter1.format(c.getTime());

		final String[] week = { "일", "월", "화", "수", "목", "금", "토" };

		mav.addObject("currentDate", currentDate + " "
				+ week[c.get(Calendar.DAY_OF_WEEK) - 1] + "요일");

//		System.out.println("retDaily:" + retDaily);
//		System.out.println("weekly:" + retWeekly);
//		System.out.println("monthly:" + retMonthly);
		return mav;

	}

	@RequestMapping(value = "/gadget/bems/ktSeochoNew.ex")
	public ModelAndView getKtSeochoNew(
			@RequestParam("supplierId") String supplierId,
			@RequestParam("locationId") String locationId) {
		String date = CalendarUtil.getCurrentDate();

		BigDecimal eDG = getGoalUsage(supplierId, locationId,
				CommonConstants.DateType.DAILY.getCode(), "EM", "1.3.1.1", date);

		BigDecimal eWG = getGoalUsage(supplierId, locationId,
				CommonConstants.DateType.WEEKLY.getCode(), "EM", "1.3.1.1",
				date);
		BigDecimal eMG = getGoalUsage(supplierId, locationId,
				CommonConstants.DateType.MONTHLY.getCode(), "EM", "1.3.1.1",
				date);

		Map<String, Object> energyData = ktSeochoExhibitManager
				.getEnergyDataByLocation("10");

		List<Map<String, Object>> dailyList = (List<Map<String, Object>>) energyData
				.get("daily");
		List<Map<String, Object>> weeklyList = (List<Map<String, Object>>) energyData
				.get("weekly");
		List<Map<String, Object>> monthlyList = (List<Map<String, Object>>) energyData
				.get("monthly");

		Map<String, Object> dailyMap = getTotalNewMap(dailyList);

		Map<String, Object> weeklyMap = getTotalNewMap(weeklyList);

		Map<String, Object> monthlyMap = getTotalNewMap(monthlyList);

		BigDecimal exD = (BigDecimal) dailyMap.get("TOTALFORECAST");
		BigDecimal exW = (BigDecimal) weeklyMap.get("TOTALFORECAST");
		BigDecimal exM = (BigDecimal) monthlyMap.get("TOTALFORECAST");

		BigDecimal exLightD = (BigDecimal) dailyMap.get("LIGHTFORECAST");
		BigDecimal exHeatD = (BigDecimal) dailyMap.get("HEATFORECAST");
		BigDecimal exLightW = (BigDecimal) weeklyMap.get("LIGHTFORECAST");
		BigDecimal exHeatW = (BigDecimal) weeklyMap.get("HEATFORECAST");
		BigDecimal exLightM = (BigDecimal) monthlyMap.get("LIGHTFORECAST");
		BigDecimal exHeatM = (BigDecimal) monthlyMap.get("HEATFORECAST");

//		System.out.println("exLightD:" + exLightD);
//		System.out.println("exHeatD:" + exHeatD);
//		System.out.println("exLightW:" + exLightW);
//		System.out.println("exHeatW:" + exHeatW);
//		System.out.println("exLightM:" + exLightM);
//		System.out.println("exHeatM:" + exHeatM);
//
//		System.out.println("exD:" + exD);
//		System.out.println("exW:" + exW);
//		System.out.println("exM:" + exM);
//
//		System.out.println("eDG:" + eDG);
//		System.out.println("eWG:" + eWG);
//		System.out.println("eMG:" + eMG);

		Map<String, Object> retDaily = getPeriodNewMap(dailyMap, DateType.DAILY
				.getCode(), exD, exLightD, exD.subtract(exLightD).subtract(
				exHeatD), exHeatD, getNewGoal(eDG, "total"), getNewGoal(eDG,
				"light"), getNewGoal(eDG, "concent"), getNewGoal(eDG, "heat"));

		Map<String, Object> retWeekly = getPeriodNewMap(weeklyMap,
				DateType.WEEKLY.getCode(), exW, exLightW, exW
						.subtract(exLightW).subtract(exHeatW), exHeatW,
				getNewGoal(eWG, "total"), getNewGoal(eWG, "light"), getNewGoal(
						eWG, "concent"), getNewGoal(eWG, "heat"));

		Map<String, Object> retMonthly = getPeriodNewMap(monthlyMap,
				DateType.MONTHLY.getCode(), exM, exLightM, exM.subtract(
						exLightM).subtract(exHeatM), exHeatM, getNewGoal(eMG,
						"total"), getNewGoal(eMG, "light"), getNewGoal(eMG,
						"concent"), getNewGoal(eMG, "heat"));

		ModelAndView mav = new ModelAndView("jsonView");
		mav.addObject("weather", getWeather());
		mav.addObject("daily", retDaily);
		mav.addObject("weekly", retWeekly);
		mav.addObject("monthly", retMonthly);

		Map<String, Map<String, Object>> retDailyMap = new HashMap<String, Map<String, Object>>();
		List<Map<String, Object>> gridDailyList = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> daily : dailyList) {
			Map<String, Object> dMap = getPeriodListNewMap(daily,
					CommonConstants.DateType.DAILY.getCode(), getNewGoal(eDG,
							"total"), getNewGoal(eDG, "light"), getNewGoal(eDG,
							"concent"), getNewGoal(eDG, "heat"));

			Map<String, Object> dTotal = (Map<String, Object>) dMap
					.get("TOTAL");
			Map<String, Object> dLight = (Map<String, Object>) dMap
					.get("LIGHT");
			Map<String, Object> dHeat = (Map<String, Object>) dMap.get("HEAT");
			Map<String, Object> dConcent = (Map<String, Object>) dMap
					.get("CONCENT");
			Map<String, Object> dPutMap = new HashMap<String, Object>();
			// System.out.println("NAME:::" + (String) dTotal.get("NAME"));
			retDailyMap.put((String) dTotal.get("NAME"), dMap);
			dPutMap.put("RANK", dTotal.get("RANK"));
			dPutMap.put("NAME", dTotal.get("NAME"));
			dPutMap.put("PRERANK", dTotal.get("PRERANK"));
			dPutMap.put("LIGHT", dLight.get("USAGEBILL"));
			dPutMap.put("HEAT", dHeat.get("USAGEBILL"));
			dPutMap.put("CONCENT", dConcent.get("USAGEBILL"));
			dPutMap.put("TOTAL", dTotal.get("USAGEBILL"));
			dPutMap.put("REDUCT", dTotal.get("REDUCT"));
			int rank = (Integer) dTotal.get("RANK");
			int preRank = (Integer) dTotal.get("PRERANK");
			if (rank < preRank)
				dPutMap.put("upDown", "assets/monitor/triangle_red.png");

			if (rank > preRank)
				dPutMap.put("upDown", "assets/monitor/triangle_blue.png");
			gridDailyList.add(dPutMap);
		}

		List<Map<String, Object>> retDailyList = new ArrayList<Map<String, Object>>();
		if (!dailyList.isEmpty()) {
//			System.out.println("Map<String, Object>)retDailyMap.get:"
//					+ retDailyMap.get("지하3층"));
			Map<String, Object> b3DailyMap = (Map<String, Object>) retDailyMap
					.get("지하3층");
			setName(b3DailyMap, "B3");
			Map<String, Object> b2DailyMap = (Map<String, Object>) retDailyMap
					.get("지하2층");
			setName(b2DailyMap, "B2");
			Map<String, Object> b1DailyMap = (Map<String, Object>) retDailyMap
					.get("지하1층");
			setName(b1DailyMap, "B1");
			Map<String, Object> f1DailyMap = (Map<String, Object>) retDailyMap
					.get("1층");
			setName(f1DailyMap, "1F");
			Map<String, Object> f2DailyMap = (Map<String, Object>) retDailyMap
					.get("2층");
			setName(f2DailyMap, "2F");
			Map<String, Object> f3DailyMap = (Map<String, Object>) retDailyMap
					.get("3층");
			setName(f3DailyMap, "2F");

			Map<String, Object> f4DailyMap = (Map<String, Object>) retDailyMap
					.get("4층");
			setName(f4DailyMap, "4F");
			Map<String, Object> f5DailyMap = (Map<String, Object>) retDailyMap
					.get("5층");
			setName(f5DailyMap, "5F");
			Map<String, Object> f6DailyMap = (Map<String, Object>) retDailyMap
					.get("6층");
			setName(f6DailyMap, "6F");
			Map<String, Object> f7DailyMap = (Map<String, Object>) retDailyMap
					.get("7층");
			setName(f7DailyMap, "7F");
			Map<String, Object> f8DailyMap = (Map<String, Object>) retDailyMap
					.get("8층");
			setName(f8DailyMap, "8F");
			Map<String, Object> f9DailyMap = (Map<String, Object>) retDailyMap
					.get("9층");
			setName(f9DailyMap, "9F");
			Map<String, Object> cDailyMap = (Map<String, Object>) retDailyMap
					.get("공용");
			setName(cDailyMap, "C");
			retDailyList.add(0, b3DailyMap);
			retDailyList.add(1, b2DailyMap);
			retDailyList.add(2, b1DailyMap);
			retDailyList.add(3, f1DailyMap);
			retDailyList.add(4, f2DailyMap);
			retDailyList.add(5, f3DailyMap);
			retDailyList.add(6, f4DailyMap);
			retDailyList.add(7, f5DailyMap);
			retDailyList.add(8, f6DailyMap);
			retDailyList.add(9, f7DailyMap);
			retDailyList.add(10, f8DailyMap);
			retDailyList.add(11, f9DailyMap);
			retDailyList.add(12, cDailyMap);
		}
		
		Map<String, Map<String, Object>> retWeeklyMap = new HashMap<String, Map<String, Object>>();
		List<Map<String, Object>> gridWeeklyList = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> weekly : weeklyList) {
			Map<String, Object> wMap = getPeriodListNewMapWeek(weekly,
					CommonConstants.DateType.WEEKLY.getCode(), getNewGoal(eWG,
							"total"), getNewGoal(eWG, "light"), getNewGoal(eWG,
							"concent"), getNewGoal(eWG, "heat"));

			// System.out.println("###################weekList:"+wMap);
			Map<String, Object> dTotal = (Map<String, Object>) wMap
					.get("TOTAL");
			retWeeklyMap.put((String) dTotal.get("NAME"), wMap);

			Map<String, Object> dLight = (Map<String, Object>) wMap
					.get("LIGHT");
			Map<String, Object> dHeat = (Map<String, Object>) wMap.get("HEAT");
			Map<String, Object> dConcent = (Map<String, Object>) wMap
					.get("CONCENT");
			Map<String, Object> dPutMap = new HashMap<String, Object>();
			dPutMap.put("RANK", dTotal.get("RANK"));
			dPutMap.put("NAME", dTotal.get("NAME"));
			dPutMap.put("PRERANK", dTotal.get("PRERANK"));
			dPutMap.put("LIGHT", dLight.get("USAGEBILL"));
			dPutMap.put("HEAT", dHeat.get("USAGEBILL"));
			dPutMap.put("CONCENT", dConcent.get("USAGEBILL"));
			dPutMap.put("TOTAL", dTotal.get("USAGEBILL"));
			dPutMap.put("REDUCT", dTotal.get("REDUCT"));
			int rank = (Integer) dTotal.get("RANK");
			int preRank = (Integer) dTotal.get("PRERANK");
			if (rank < preRank)
				dPutMap.put("upDown", "assets/monitor/triangle_red.png");

			if (rank > preRank)
				dPutMap.put("upDown", "assets/monitor/triangle_blue.png");
			gridWeeklyList.add(dPutMap);

		}

		List<Map<String, Object>> retWeeklyList = new ArrayList<Map<String, Object>>();
		if (!retWeeklyList.isEmpty()) {
			Map<String, Object> b3WeeklyMap = (Map<String, Object>) retWeeklyMap
					.get("지하3층");
			setName(b3WeeklyMap, "B3");
			Map<String, Object> b2WeeklyMap = (Map<String, Object>) retWeeklyMap
					.get("지하2층");
			setName(b2WeeklyMap, "B2");
			Map<String, Object> b1WeeklyMap = (Map<String, Object>) retWeeklyMap
					.get("지하1층");
			setName(b1WeeklyMap, "B1");
			Map<String, Object> f1WeeklyMap = (Map<String, Object>) retWeeklyMap
					.get("1층");
			setName(f1WeeklyMap, "1F");
			Map<String, Object> f2WeeklyMap = (Map<String, Object>) retWeeklyMap
					.get("2층");
			setName(f2WeeklyMap, "2F");
			Map<String, Object> f3WeeklyMap = (Map<String, Object>) retWeeklyMap
					.get("3층");
			setName(f3WeeklyMap, "3F");

			Map<String, Object> f4WeeklyMap = (Map<String, Object>) retWeeklyMap
					.get("4층");
			setName(f4WeeklyMap, "4F");
			Map<String, Object> f5WeeklyMap = (Map<String, Object>) retWeeklyMap
					.get("5층");
			setName(f5WeeklyMap, "5F");
			Map<String, Object> f6WeeklyMap = (Map<String, Object>) retWeeklyMap
					.get("6층");
			setName(f6WeeklyMap, "6F");
			Map<String, Object> f7WeeklyMap = (Map<String, Object>) retWeeklyMap
					.get("7층");
			setName(f7WeeklyMap, "7F");
			Map<String, Object> f8WeeklyMap = (Map<String, Object>) retWeeklyMap
					.get("8층");
			setName(f8WeeklyMap, "8F");
			Map<String, Object> f9WeeklyMap = (Map<String, Object>) retWeeklyMap
					.get("9층");
			setName(f9WeeklyMap, "9F");
			Map<String, Object> cWeeklyMap = (Map<String, Object>) retWeeklyMap
					.get("공용");
			setName(cWeeklyMap, "C");

			retWeeklyList.add(0, b3WeeklyMap);
			retWeeklyList.add(1, b2WeeklyMap);
			retWeeklyList.add(2, b1WeeklyMap);
			retWeeklyList.add(3, f1WeeklyMap);
			retWeeklyList.add(4, f2WeeklyMap);
			retWeeklyList.add(5, f3WeeklyMap);
			retWeeklyList.add(6, f4WeeklyMap);
			retWeeklyList.add(7, f5WeeklyMap);
			retWeeklyList.add(8, f6WeeklyMap);
			retWeeklyList.add(9, f7WeeklyMap);
			retWeeklyList.add(10, f8WeeklyMap);
			retWeeklyList.add(11, f9WeeklyMap);
			retWeeklyList.add(12, cWeeklyMap);
		}
		Map<String, Map<String, Object>> retMonthlyMap = new HashMap<String, Map<String, Object>>();
		List<Map<String, Object>> gridMonthlyList = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> monthly : monthlyList) {
			Map<String, Object> mMap = getPeriodListNewMap(monthly,
					CommonConstants.DateType.MONTHLY.getCode(), getNewGoal(eMG,
							"total"), getNewGoal(eMG, "light"), getNewGoal(eMG,
							"concent"), getNewGoal(eMG, "heat"));

			Map<String, Object> dTotal = (Map<String, Object>) mMap
					.get("TOTAL");
			retMonthlyMap.put((String) dTotal.get("NAME"), mMap);
			Map<String, Object> dLight = (Map<String, Object>) mMap
					.get("LIGHT");
			Map<String, Object> dHeat = (Map<String, Object>) mMap.get("HEAT");
			Map<String, Object> dConcent = (Map<String, Object>) mMap
					.get("CONCENT");
			Map<String, Object> dPutMap = new HashMap<String, Object>();
			dPutMap.put("RANK", dTotal.get("RANK"));
			dPutMap.put("NAME", dTotal.get("NAME"));
			dPutMap.put("PRERANK", dTotal.get("PRERANK"));
			dPutMap.put("LIGHT", dLight.get("USAGEBILL"));
			dPutMap.put("HEAT", dHeat.get("USAGEBILL"));
			dPutMap.put("CONCENT", dConcent.get("USAGEBILL"));
			dPutMap.put("TOTAL", dTotal.get("USAGEBILL"));
			dPutMap.put("REDUCT", dTotal.get("REDUCT"));
			int rank = (Integer) dTotal.get("RANK");
			int preRank = (Integer) dTotal.get("PRERANK");
			
			if (rank < preRank)
				dPutMap.put("upDown", "assets/monitor/triangle_red.png");

			if (rank > preRank)
				dPutMap.put("upDown", "assets/monitor/triangle_blue.png");
			gridMonthlyList.add(dPutMap);
		}

		List<Map<String, Object>> retMonthlyList = new ArrayList<Map<String, Object>>();
		Map<String, Object> b3MonthlyMap = (Map<String, Object>) retMonthlyMap
				.get("지하3층");
		setName(b3MonthlyMap, "B3");
		Map<String, Object> b2MonthlyMap = (Map<String, Object>) retMonthlyMap
				.get("지하2층");
		setName(b2MonthlyMap, "B2");
		Map<String, Object> b1MonthlyMap = (Map<String, Object>) retMonthlyMap
				.get("지하1층");
		setName(b1MonthlyMap, "B1");
		Map<String, Object> f1MonthlyMap = (Map<String, Object>) retMonthlyMap
				.get("1층");
		setName(f1MonthlyMap, "1F");
		Map<String, Object> f2MonthlyMap = (Map<String, Object>) retMonthlyMap
				.get("2층");
		setName(f2MonthlyMap, "2F");
		Map<String, Object> f3MonthlyMap = (Map<String, Object>) retMonthlyMap
				.get("3층");
		setName(f3MonthlyMap, "3F");

		Map<String, Object> f4MonthlyMap = (Map<String, Object>) retMonthlyMap
				.get("4층");
		setName(f4MonthlyMap, "4F");
		Map<String, Object> f5MonthlyMap = (Map<String, Object>) retMonthlyMap
				.get("5층");
		setName(f5MonthlyMap, "5F");
		Map<String, Object> f6MonthlyMap = (Map<String, Object>) retMonthlyMap
				.get("6층");
		setName(f6MonthlyMap, "6F");
		Map<String, Object> f7MonthlyMap = (Map<String, Object>) retMonthlyMap
				.get("7층");
		setName(f7MonthlyMap, "7F");
		Map<String, Object> f8MonthlyMap = (Map<String, Object>) retMonthlyMap
				.get("8층");
		setName(f8MonthlyMap, "8F");
		Map<String, Object> f9MonthlyMap = (Map<String, Object>) retMonthlyMap
				.get("9층");
		setName(f9MonthlyMap, "9F");
		Map<String, Object> cMonthlyMap = (Map<String, Object>) retMonthlyMap
				.get("공용");
		setName(cMonthlyMap, "C");
		retMonthlyList.add(0, b3MonthlyMap);
		retMonthlyList.add(1, b2MonthlyMap);
		retMonthlyList.add(2, b1MonthlyMap);
		retMonthlyList.add(3, f1MonthlyMap);
		retMonthlyList.add(4, f2MonthlyMap);
		retMonthlyList.add(5, f3MonthlyMap);
		retMonthlyList.add(6, f4MonthlyMap);
		retMonthlyList.add(7, f5MonthlyMap);
		retMonthlyList.add(8, f6MonthlyMap);
		retMonthlyList.add(9, f7MonthlyMap);
		retMonthlyList.add(10, f8MonthlyMap);
		retMonthlyList.add(11, f9MonthlyMap);
		retMonthlyList.add(12, cMonthlyMap);

		mav.addObject("dailyList", retDailyList);
		mav.addObject("weeklyList", retWeeklyList);
		mav.addObject("monthlyList", retMonthlyList);

		mav.addObject("gridDailyList", gridDailyList);
		mav.addObject("gridWeeklyList", gridWeeklyList);
		mav.addObject("gridMonthlyList", gridMonthlyList);
		Calendar c = Calendar.getInstance();
		SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy.MM.dd");

		String currentDate = formatter1.format(c.getTime());

		final String[] week = { "일", "월", "화", "수", "목", "금", "토" };

		mav.addObject("currentDate", currentDate + " "
				+ week[c.get(Calendar.DAY_OF_WEEK) - 1] + "요일");

//		System.out.println("retDaily:" + retDaily);
//		System.out.println("weekly:" + retWeekly);
//		System.out.println("monthly:" + retMonthly);
//
//		System.out.println("retDailyList:" + retDailyList);
//		System.out.println("retWeeklyList:" + retWeeklyList);
//		System.out.println("retMonthlyList:" + retMonthlyList);
		return mav;
	}

	private void setName(Map<String, Object> map, String name) {
		((Map<String, Object>) map.get("TOTAL")).put("NAME", name);
		((Map<String, Object>) map.get("LIGHT")).put("NAME", name);
		((Map<String, Object>) map.get("HEAT")).put("NAME", name);
		((Map<String, Object>) map.get("CONCENT")).put("NAME", name);
	}

	private BigDecimal getNewGoal(BigDecimal goal, String energy) {
		BigDecimal newRatio = new BigDecimal(0.15);

		if (energy.equals("light"))
			// return goal.multiply(newRatio).multiply(new BigDecimal(0.38));
			return goal.multiply(newRatio).multiply(new BigDecimal(0.50));
		else if (energy.equals("concent"))
			return goal.multiply(newRatio).multiply(new BigDecimal(0.45));
		else if (energy.equals("heat"))
			return goal.multiply(newRatio).multiply(new BigDecimal(0.05));

		return goal.multiply(newRatio);
	}

	private Map<String, Object> getPeriodListNewMap(
			Map<String, Object> usageMap, String dateType,
			BigDecimal totalGoal, BigDecimal lightGoal, BigDecimal concentGoal,
			BigDecimal heatGoal) {

		// System.out.println("getPeriodListNewMap usageMap:"+usageMap);
		Map<String, Object> retMap = new HashMap<String, Object>();

		Integer tReduct = getCharge(MeterType.EnergyMeter.getServiceType(),
				dateType, getUsage("REDUCT", usageMap));
		Integer tfcast = getCharge(MeterType.EnergyMeter.getServiceType(),
				dateType, getUsage("EXP", usageMap));

		Integer tGoal = getCharge(MeterType.EnergyMeter.getServiceType(),
				dateType, totalGoal);

		Integer tUsage = getCharge(MeterType.EnergyMeter.getServiceType(),
				dateType, getUsage("CURTOTAL", usageMap));

		BigDecimal tCo2 = getCo2(usageMap, "EM");
		BigDecimal usage = getUsage("CURTOTAL", usageMap);

		Map<String, Object> totalMap = getPanelListMap(tfcast, tGoal / 13,
				tUsage, tCo2, usage, (String) usageMap.get("NAME"),
				(Integer) usageMap.get("RANK"), (Integer) usageMap
						.get("PRERANK"), tReduct);

		Integer lfcast = getCharge(MeterType.EnergyMeter.getServiceType(),
				dateType, getUsage("EXPLIGHT", usageMap));
		Integer lGoal = getCharge(MeterType.EnergyMeter.getServiceType(),
				dateType, lightGoal);

		Map<String, Object> light = (Map<String, Object>) usageMap.get("LIGHT");
		Integer lUsage = getCharge(MeterType.EnergyMeter.getServiceType(),
				dateType, getUsage("EMUSAGE", light));

		Map<String, Object> lMap = getPanelListMap(lfcast, lGoal / 13, lUsage,
				getCo2(usageMap, "EM"), getUsage("EMUSAGE", light),
				(String) usageMap.get("NAME"), (Integer) usageMap.get("RANK"),
				(Integer) usageMap.get("PRERANK"), tReduct);

		Integer hfcast = getCharge(MeterType.EnergyMeter.getServiceType(),
				dateType, getUsage("EXPHEAT", usageMap));
		Integer hGoal = getCharge(MeterType.EnergyMeter.getServiceType(),
				dateType, heatGoal);

		Map<String, Object> heat = (Map<String, Object>) usageMap.get("HEAT");
		Integer hUsage = getCharge(MeterType.EnergyMeter.getServiceType(),
				dateType, getUsage("EMUSAGE", heat));

		Map<String, Object> hMap = getPanelListMap(hfcast, hGoal / 13, hUsage,
				getCo2(usageMap, "EM"), getUsage("EMUSAGE", heat),
				(String) usageMap.get("NAME"), (Integer) usageMap.get("RANK"),
				(Integer) usageMap.get("PRERANK"), tReduct);

		Integer cfcast = getCharge(MeterType.EnergyMeter.getServiceType(),
				dateType, getUsage("EXP", usageMap).subtract(
						getUsage("EXPLIGHT", usageMap).add(
								getUsage("EXPHEAT", usageMap))));
		Integer cGoal = getCharge(MeterType.EnergyMeter.getServiceType(),
				dateType, concentGoal);

		Integer cUsage = getCharge(MeterType.EnergyMeter.getServiceType(),
				dateType, getUsage("CURTOTAL", usageMap).subtract(
						getUsage("EMUSAGE", light)).subtract(
						getUsage("EMUSAGE", heat)));

		Map<String, Object> cMap = getPanelListMap(cfcast, cGoal / 13, cUsage,
				getCo2(usageMap, "EM"), getUsage("CURTOTAL", usageMap)
						.subtract(getUsage("EMUSAGE", light)).subtract(
								getUsage("EMUSAGE", heat)), (String) usageMap
						.get("NAME"), (Integer) usageMap.get("RANK"),
				(Integer) usageMap.get("PRERANK"), tReduct);

		// retMap.put("NAME", usageMap.get("NAME"));
		retMap.put("TOTAL", totalMap);
		retMap.put("LIGHT", lMap);
		retMap.put("CONCENT", cMap);
		retMap.put("HEAT", hMap);
		return retMap;
	}

	private Map<String, Object> getPeriodListNewMapWeek(
			Map<String, Object> usageMap, String dateType,
			BigDecimal totalGoal, BigDecimal lightGoal, BigDecimal concentGoal,
			BigDecimal heatGoal) {

		// System.out.println("getPeriodListNewMapWeek usageMap:"+usageMap);
		Map<String, Object> retMap = new HashMap<String, Object>();

		Integer tReduct = getCharge(MeterType.EnergyMeter.getServiceType(),
				dateType, getUsage("REDUCT", usageMap));
		Integer tfcast = getCharge(MeterType.EnergyMeter.getServiceType(),
				dateType, getUsage("EXP", usageMap));

		Integer tGoal = getCharge(MeterType.EnergyMeter.getServiceType(),
				dateType, totalGoal);

		Integer tUsage = getCharge(MeterType.EnergyMeter.getServiceType(),
				dateType, getUsage("CURTOTAL", usageMap));

		BigDecimal tCo2 = getCo2(usageMap, "EM");
		BigDecimal usage = getUsage("CURTOTAL", usageMap);

		Map<String, Object> totalMap = getPanelListMap(tfcast, tGoal / 13,
				tUsage, tCo2, usage, (String) usageMap.get("NAME"),
				(Integer) usageMap.get("RANK"), (Integer) usageMap
						.get("PRERANK"), tReduct);

		Integer lfcast = getCharge(MeterType.EnergyMeter.getServiceType(),
				dateType, getUsage("EXPLIGHT", usageMap));
		Integer lGoal = getCharge(MeterType.EnergyMeter.getServiceType(),
				dateType, lightGoal);

		Map<String, Object> light = (Map<String, Object>) usageMap.get("LIGHT");
		Integer lUsage = getCharge(MeterType.EnergyMeter.getServiceType(),
				dateType, getUsage("EMUSAGE", light));

		Map<String, Object> lMap = getPanelListMap(lfcast, lGoal / 39, lUsage,
				getCo2(usageMap, "EM"), getUsage("EMUSAGE", light),
				(String) usageMap.get("NAME"), (Integer) usageMap.get("RANK"),
				(Integer) usageMap.get("PRERANK"), tReduct);

		Integer hfcast = getCharge(MeterType.EnergyMeter.getServiceType(),
				dateType, getUsage("EXPHEAT", usageMap));
		Integer hGoal = getCharge(MeterType.EnergyMeter.getServiceType(),
				dateType, heatGoal);

		Map<String, Object> heat = (Map<String, Object>) usageMap.get("HEAT");
		Integer hUsage = getCharge(MeterType.EnergyMeter.getServiceType(),
				dateType, getUsage("EMUSAGE", heat));

		Map<String, Object> hMap = getPanelListMap(hfcast, hGoal / 39, hUsage,
				getCo2(usageMap, "EM"), getUsage("EMUSAGE", heat),
				(String) usageMap.get("NAME"), (Integer) usageMap.get("RANK"),
				(Integer) usageMap.get("PRERANK"), tReduct);

		Integer cfcast = getCharge(MeterType.EnergyMeter.getServiceType(),
				dateType, getUsage("EXP", usageMap).subtract(
						getUsage("EXPLIGHT", usageMap).add(
								getUsage("EXPHEAT", usageMap))));
		Integer cGoal = getCharge(MeterType.EnergyMeter.getServiceType(),
				dateType, concentGoal);

		Integer cUsage = getCharge(MeterType.EnergyMeter.getServiceType(),
				dateType, getUsage("CURTOTAL", usageMap).subtract(
						getUsage("EMUSAGE", light)).subtract(
						getUsage("EMUSAGE", heat)));

		Map<String, Object> cMap = getPanelListMap(cfcast, cGoal / 39, cUsage,
				getCo2(usageMap, "EM"), getUsage("CURTOTAL", usageMap)
						.subtract(getUsage("EMUSAGE", light)).subtract(
								getUsage("EMUSAGE", heat)), (String) usageMap
						.get("NAME"), (Integer) usageMap.get("RANK"),
				(Integer) usageMap.get("PRERANK"), tReduct);

		// System.out.println("LIGHT lMap:"+lMap);
		// System.out.println("LIGHT hMap:"+hMap);
		retMap.put("TOTAL", totalMap);
		retMap.put("LIGHT", lMap);
		retMap.put("CONCENT", cMap);
		retMap.put("HEAT", hMap);
		return retMap;
	}

	private Map<String, Object> getPeriodNewMap(Map<String, Object> usageMap,
			String dateType, BigDecimal totalExpect, BigDecimal lightExpect,
			BigDecimal concentExpect, BigDecimal heatExpect,
			BigDecimal totalGoal, BigDecimal lightGoal, BigDecimal concentGoal,
			BigDecimal heatGoal) {
		Map<String, Object> retMap = new HashMap<String, Object>();

		Integer tfcast = getCharge(MeterType.EnergyMeter.getServiceType(),
				dateType, totalExpect);
		Integer tGoal = getCharge(MeterType.EnergyMeter.getServiceType(),
				dateType, totalGoal);

		Integer tUsage = getCharge(MeterType.EnergyMeter.getServiceType(),
				dateType, getUsage("TOTAL", usageMap));

		BigDecimal tCo2 = getCo2(usageMap, "EM");
		BigDecimal usage = getUsage("TOTAL", usageMap);

		Map<String, Object> totalMap = getPanelMap(tfcast, tGoal, tUsage, tCo2,
				usage);

		Integer lfcast = getCharge(MeterType.EnergyMeter.getServiceType(),
				dateType, lightExpect);
		Integer lGoal = getCharge(MeterType.EnergyMeter.getServiceType(),
				dateType, lightGoal);

		Integer lUsage = getCharge(MeterType.EnergyMeter.getServiceType(),
				dateType, getUsage("LIGHT", usageMap));

		Map<String, Object> lMap = getPanelMap(lfcast, lGoal, lUsage, getCo2(
				usageMap, "EM"), getUsage("LIGHT", usageMap));

		Integer cfcast = getCharge(MeterType.EnergyMeter.getServiceType(),
				dateType, concentExpect);
		Integer cGoal = getCharge(MeterType.EnergyMeter.getServiceType(),
				dateType, concentGoal);

		Integer cUsage = getCharge(MeterType.EnergyMeter.getServiceType(),
				dateType, getUsage("TOTAL", usageMap).subtract(
						getUsage("LIGHT", usageMap)).subtract(
						getUsage("HEAT", usageMap)));

		Map<String, Object> cMap = getPanelMap(cfcast, cGoal, cUsage, getCo2(
				usageMap, "EM"), getUsage("TOTAL", usageMap).subtract(
				getUsage("LIGHT", usageMap)).subtract(
				getUsage("HEAT", usageMap)));

		Integer hfcast = getCharge(MeterType.EnergyMeter.getServiceType(),
				dateType, heatExpect);
		Integer hGoal = getCharge(MeterType.EnergyMeter.getServiceType(),
				dateType, heatGoal);

		Integer hUsage = getCharge(MeterType.EnergyMeter.getServiceType(),
				dateType, getUsage("HEAT", usageMap));

		Map<String, Object> hMap = getPanelMap(hfcast, hGoal, hUsage, getCo2(
				usageMap, "EM"), getUsage("HEAT", usageMap));

		retMap.put("TOTAL", totalMap);
		retMap.put("LIGHT", lMap);
		retMap.put("CONCENT", cMap);
		retMap.put("HEAT", hMap);
		return retMap;
	}

	private Map<String, Object> getTotalNewMap(List<Map<String, Object>> list) {

		Map<String, Object> retMap = new HashMap<String, Object>();

		Double total = 0d;
		BigDecimal forecast = new BigDecimal(0);
		BigDecimal light = new BigDecimal(0);
		BigDecimal lightForecast = new BigDecimal(0);
		BigDecimal heat = new BigDecimal(0);
		BigDecimal heatForecast = new BigDecimal(0);

		for (Map<String, Object> map : list) {
			total = total + getDoubleValue(map.get("CURTOTAL"));
			heat = heat
					.add((BigDecimal) ((Map<String, Object>) map.get("HEAT"))
							.get("EMUSAGE"));
			light = light.add((BigDecimal) ((Map<String, Object>) map
					.get("LIGHT")).get("EMUSAGE"));

			forecast = forecast.add((BigDecimal) map.get("EXP"));
			heatForecast = heatForecast.add((BigDecimal) map.get("EXPHEAT"));
			lightForecast = lightForecast.add((BigDecimal) map.get("EXPLIGHT"));
		}

		retMap.put("TOTAL", total);
		retMap.put("HEAT", heat);
		retMap.put("LIGHT", light);
		retMap.put("TOTALFORECAST", forecast);
		retMap.put("HEATFORECAST", heatForecast);
		retMap.put("LIGHTFORECAST", lightForecast);

		return retMap;

	}

	private Map<String, Object> getTotalDailyNewMap(
			List<Map<String, Object>> list) {
		Map<String, Object> retPreMap = new HashMap<String, Object>();
		Map<String, Object> retDailyMap = new HashMap<String, Object>();

		Map<String, Object> retPreHeatMap = new HashMap<String, Object>();
		Map<String, Object> retDailyHeatMap = new HashMap<String, Object>();

		Map<String, Object> retPreLightMap = new HashMap<String, Object>();
		Map<String, Object> retDailyLightMap = new HashMap<String, Object>();

		Map<String, Object> retMap = new HashMap<String, Object>();
		Double pre00 = 0d;
		Double pre01 = 0d;
		Double pre02 = 0d;
		Double pre03 = 0d;
		Double pre04 = 0d;
		Double pre05 = 0d;
		Double pre06 = 0d;
		Double pre07 = 0d;
		Double pre08 = 0d;
		Double pre09 = 0d;
		Double pre10 = 0d;
		Double pre11 = 0d;
		Double pre12 = 0d;
		Double pre13 = 0d;
		Double pre14 = 0d;
		Double pre15 = 0d;
		Double pre16 = 0d;
		Double pre17 = 0d;
		Double pre18 = 0d;
		Double pre19 = 0d;
		Double pre20 = 0d;
		Double pre21 = 0d;
		Double pre22 = 0d;
		Double pre23 = 0d;
		Double preTotal = 0d;
		BigDecimal preLight = new BigDecimal(0);
		BigDecimal preLight00 = new BigDecimal(0);
		BigDecimal preLight01 = new BigDecimal(0);
		BigDecimal preLight02 = new BigDecimal(0);
		BigDecimal preLight03 = new BigDecimal(0);
		BigDecimal preLight04 = new BigDecimal(0);
		BigDecimal preLight05 = new BigDecimal(0);
		BigDecimal preLight06 = new BigDecimal(0);
		BigDecimal preLight07 = new BigDecimal(0);
		BigDecimal preLight08 = new BigDecimal(0);
		BigDecimal preLight09 = new BigDecimal(0);
		BigDecimal preLight10 = new BigDecimal(0);
		BigDecimal preLight11 = new BigDecimal(0);
		BigDecimal preLight12 = new BigDecimal(0);
		BigDecimal preLight13 = new BigDecimal(0);
		BigDecimal preLight14 = new BigDecimal(0);
		BigDecimal preLight15 = new BigDecimal(0);
		BigDecimal preLight16 = new BigDecimal(0);
		BigDecimal preLight17 = new BigDecimal(0);
		BigDecimal preLight18 = new BigDecimal(0);
		BigDecimal preLight19 = new BigDecimal(0);
		BigDecimal preLight20 = new BigDecimal(0);
		BigDecimal preLight21 = new BigDecimal(0);
		BigDecimal preLight22 = new BigDecimal(0);
		BigDecimal preLight23 = new BigDecimal(0);
		BigDecimal preHeat = new BigDecimal(0);
		BigDecimal preHeat00 = new BigDecimal(0);
		BigDecimal preHeat01 = new BigDecimal(0);
		BigDecimal preHeat02 = new BigDecimal(0);
		BigDecimal preHeat03 = new BigDecimal(0);
		BigDecimal preHeat04 = new BigDecimal(0);
		BigDecimal preHeat05 = new BigDecimal(0);
		BigDecimal preHeat06 = new BigDecimal(0);
		BigDecimal preHeat07 = new BigDecimal(0);
		BigDecimal preHeat08 = new BigDecimal(0);
		BigDecimal preHeat09 = new BigDecimal(0);
		BigDecimal preHeat10 = new BigDecimal(0);
		BigDecimal preHeat11 = new BigDecimal(0);
		BigDecimal preHeat12 = new BigDecimal(0);
		BigDecimal preHeat13 = new BigDecimal(0);
		BigDecimal preHeat14 = new BigDecimal(0);
		BigDecimal preHeat15 = new BigDecimal(0);
		BigDecimal preHeat16 = new BigDecimal(0);
		BigDecimal preHeat17 = new BigDecimal(0);
		BigDecimal preHeat18 = new BigDecimal(0);
		BigDecimal preHeat19 = new BigDecimal(0);
		BigDecimal preHeat20 = new BigDecimal(0);
		BigDecimal preHeat21 = new BigDecimal(0);
		BigDecimal preHeat22 = new BigDecimal(0);
		BigDecimal preHeat23 = new BigDecimal(0);

		Double cur00 = 0d;
		Double cur01 = 0d;
		Double cur02 = 0d;
		Double cur03 = 0d;
		Double cur04 = 0d;
		Double cur05 = 0d;
		Double cur06 = 0d;
		Double cur07 = 0d;
		Double cur08 = 0d;
		Double cur09 = 0d;
		Double cur10 = 0d;
		Double cur11 = 0d;
		Double cur12 = 0d;
		Double cur13 = 0d;
		Double cur14 = 0d;
		Double cur15 = 0d;
		Double cur16 = 0d;
		Double cur17 = 0d;
		Double cur18 = 0d;
		Double cur19 = 0d;
		Double cur20 = 0d;
		Double cur21 = 0d;
		Double cur22 = 0d;
		Double cur23 = 0d;

		Double curTotal = 0d;
		BigDecimal curLight = new BigDecimal(0);
		BigDecimal curLight00 = new BigDecimal(0);
		BigDecimal curLight01 = new BigDecimal(0);
		BigDecimal curLight02 = new BigDecimal(0);
		BigDecimal curLight03 = new BigDecimal(0);
		BigDecimal curLight04 = new BigDecimal(0);
		BigDecimal curLight05 = new BigDecimal(0);
		BigDecimal curLight06 = new BigDecimal(0);
		BigDecimal curLight07 = new BigDecimal(0);
		BigDecimal curLight08 = new BigDecimal(0);
		BigDecimal curLight09 = new BigDecimal(0);
		BigDecimal curLight10 = new BigDecimal(0);
		BigDecimal curLight11 = new BigDecimal(0);
		BigDecimal curLight12 = new BigDecimal(0);
		BigDecimal curLight13 = new BigDecimal(0);
		BigDecimal curLight14 = new BigDecimal(0);
		BigDecimal curLight15 = new BigDecimal(0);
		BigDecimal curLight16 = new BigDecimal(0);
		BigDecimal curLight17 = new BigDecimal(0);
		BigDecimal curLight18 = new BigDecimal(0);
		BigDecimal curLight19 = new BigDecimal(0);
		BigDecimal curLight20 = new BigDecimal(0);
		BigDecimal curLight21 = new BigDecimal(0);
		BigDecimal curLight22 = new BigDecimal(0);
		BigDecimal curLight23 = new BigDecimal(0);
		BigDecimal curHeat = new BigDecimal(0);
		BigDecimal curHeat00 = new BigDecimal(0);
		BigDecimal curHeat01 = new BigDecimal(0);
		BigDecimal curHeat02 = new BigDecimal(0);
		BigDecimal curHeat03 = new BigDecimal(0);
		BigDecimal curHeat04 = new BigDecimal(0);
		BigDecimal curHeat05 = new BigDecimal(0);
		BigDecimal curHeat06 = new BigDecimal(0);
		BigDecimal curHeat07 = new BigDecimal(0);
		BigDecimal curHeat08 = new BigDecimal(0);
		BigDecimal curHeat09 = new BigDecimal(0);
		BigDecimal curHeat10 = new BigDecimal(0);
		BigDecimal curHeat11 = new BigDecimal(0);
		BigDecimal curHeat12 = new BigDecimal(0);
		BigDecimal curHeat13 = new BigDecimal(0);
		BigDecimal curHeat14 = new BigDecimal(0);
		BigDecimal curHeat15 = new BigDecimal(0);
		BigDecimal curHeat16 = new BigDecimal(0);
		BigDecimal curHeat17 = new BigDecimal(0);
		BigDecimal curHeat18 = new BigDecimal(0);
		BigDecimal curHeat19 = new BigDecimal(0);
		BigDecimal curHeat20 = new BigDecimal(0);
		BigDecimal curHeat21 = new BigDecimal(0);
		BigDecimal curHeat22 = new BigDecimal(0);
		BigDecimal curHeat23 = new BigDecimal(0);

		for (Map<String, Object> map : list) {

			pre00 = pre00 + getDoubleValue(map.get("PREVALUE00"));
			pre01 = pre01 + getDoubleValue(map.get("PREVALUE01"));
			pre02 = pre02 + getDoubleValue(map.get("PREVALUE02"));
			pre03 = pre03 + getDoubleValue(map.get("PREVALUE03"));
			pre04 = pre04 + getDoubleValue(map.get("PREVALUE04"));
			pre05 = pre05 + getDoubleValue(map.get("PREVALUE05"));
			pre06 = pre06 + getDoubleValue(map.get("PREVALUE06"));
			pre07 = pre07 + getDoubleValue(map.get("PREVALUE07"));
			pre08 = pre08 + getDoubleValue(map.get("PREVALUE08"));
			pre09 = pre09 + getDoubleValue(map.get("PREVALUE09"));
			pre10 = pre10 + getDoubleValue(map.get("PREVALUE10"));
			pre11 = pre11 + getDoubleValue(map.get("PREVALUE11"));
			pre12 = pre12 + getDoubleValue(map.get("PREVALUE12"));
			pre13 = pre13 + getDoubleValue(map.get("PREVALUE13"));
			pre14 = pre14 + getDoubleValue(map.get("PREVALUE14"));
			pre15 = pre15 + getDoubleValue(map.get("PREVALUE15"));
			pre16 = pre16 + getDoubleValue(map.get("PREVALUE16"));
			pre17 = pre17 + getDoubleValue(map.get("PREVALUE17"));
			pre18 = pre18 + getDoubleValue(map.get("PREVALUE18"));
			pre19 = pre19 + getDoubleValue(map.get("PREVALUE19"));
			pre20 = pre20 + getDoubleValue(map.get("PREVALUE20"));
			pre21 = pre21 + getDoubleValue(map.get("PREVALUE21"));
			pre22 = pre22 + getDoubleValue(map.get("PREVALUE22"));
			pre23 = pre23 + getDoubleValue(map.get("PREVALUE23"));
			preTotal = preTotal + getDoubleValue(map.get("PRETOTAL"));
			preHeat = preHeat.add((BigDecimal) ((Map<String, Object>) map
					.get("PREHEAT")).get("EMUSAGE"));
			preHeat00 = preHeat00.add((BigDecimal) ((Map<String, Object>) map
					.get("PREHEAT")).get("EMUSAGE_00"));
			preHeat01 = preHeat01.add((BigDecimal) ((Map<String, Object>) map
					.get("PREHEAT")).get("EMUSAGE_01"));
			preHeat02 = preHeat02.add((BigDecimal) ((Map<String, Object>) map
					.get("PREHEAT")).get("EMUSAGE_02"));
			preHeat03 = preHeat03.add((BigDecimal) ((Map<String, Object>) map
					.get("PREHEAT")).get("EMUSAGE_03"));
			preHeat04 = preHeat04.add((BigDecimal) ((Map<String, Object>) map
					.get("PREHEAT")).get("EMUSAGE_04"));
			preHeat05 = preHeat05.add((BigDecimal) ((Map<String, Object>) map
					.get("PREHEAT")).get("EMUSAGE_05"));
			preHeat06 = preHeat06.add((BigDecimal) ((Map<String, Object>) map
					.get("PREHEAT")).get("EMUSAGE_06"));
			preHeat07 = preHeat07.add((BigDecimal) ((Map<String, Object>) map
					.get("PREHEAT")).get("EMUSAGE_07"));
			preHeat08 = preHeat08.add((BigDecimal) ((Map<String, Object>) map
					.get("PREHEAT")).get("EMUSAGE_08"));
			preHeat09 = preHeat09.add((BigDecimal) ((Map<String, Object>) map
					.get("PREHEAT")).get("EMUSAGE_09"));
			preHeat10 = preHeat10.add((BigDecimal) ((Map<String, Object>) map
					.get("PREHEAT")).get("EMUSAGE_10"));
			preHeat11 = preHeat11.add((BigDecimal) ((Map<String, Object>) map
					.get("PREHEAT")).get("EMUSAGE_11"));
			preHeat12 = preHeat12.add((BigDecimal) ((Map<String, Object>) map
					.get("PREHEAT")).get("EMUSAGE_12"));
			preHeat13 = preHeat13.add((BigDecimal) ((Map<String, Object>) map
					.get("PREHEAT")).get("EMUSAGE_13"));
			preHeat14 = preHeat14.add((BigDecimal) ((Map<String, Object>) map
					.get("PREHEAT")).get("EMUSAGE_14"));
			preHeat15 = preHeat15.add((BigDecimal) ((Map<String, Object>) map
					.get("PREHEAT")).get("EMUSAGE_15"));
			preHeat16 = preHeat16.add((BigDecimal) ((Map<String, Object>) map
					.get("PREHEAT")).get("EMUSAGE_16"));
			preHeat17 = preHeat17.add((BigDecimal) ((Map<String, Object>) map
					.get("PREHEAT")).get("EMUSAGE_17"));
			preHeat18 = preHeat18.add((BigDecimal) ((Map<String, Object>) map
					.get("PREHEAT")).get("EMUSAGE_18"));
			preHeat19 = preHeat19.add((BigDecimal) ((Map<String, Object>) map
					.get("PREHEAT")).get("EMUSAGE_19"));
			preHeat20 = preHeat20.add((BigDecimal) ((Map<String, Object>) map
					.get("PREHEAT")).get("EMUSAGE_20"));
			preHeat21 = preHeat21.add((BigDecimal) ((Map<String, Object>) map
					.get("PREHEAT")).get("EMUSAGE_21"));
			preHeat22 = preHeat22.add((BigDecimal) ((Map<String, Object>) map
					.get("PREHEAT")).get("EMUSAGE_22"));
			preHeat23 = preHeat23.add((BigDecimal) ((Map<String, Object>) map
					.get("PREHEAT")).get("EMUSAGE_23"));

			preLight = preLight.add((BigDecimal) ((Map<String, Object>) map
					.get("PRELIGHT")).get("EMUSAGE"));
			preLight00 = preLight00.add((BigDecimal) ((Map<String, Object>) map
					.get("PRELIGHT")).get("EMUSAGE_00"));
			preLight01 = preLight01.add((BigDecimal) ((Map<String, Object>) map
					.get("PRELIGHT")).get("EMUSAGE_01"));
			preLight02 = preLight02.add((BigDecimal) ((Map<String, Object>) map
					.get("PRELIGHT")).get("EMUSAGE_02"));
			preLight03 = preLight03.add((BigDecimal) ((Map<String, Object>) map
					.get("PRELIGHT")).get("EMUSAGE_03"));
			preLight04 = preLight04.add((BigDecimal) ((Map<String, Object>) map
					.get("PRELIGHT")).get("EMUSAGE_04"));
			preLight05 = preLight05.add((BigDecimal) ((Map<String, Object>) map
					.get("PRELIGHT")).get("EMUSAGE_05"));
			preLight06 = preLight06.add((BigDecimal) ((Map<String, Object>) map
					.get("PRELIGHT")).get("EMUSAGE_06"));
			preLight07 = preLight07.add((BigDecimal) ((Map<String, Object>) map
					.get("PRELIGHT")).get("EMUSAGE_07"));
			preLight08 = preLight08.add((BigDecimal) ((Map<String, Object>) map
					.get("PRELIGHT")).get("EMUSAGE_08"));
			preLight09 = preLight09.add((BigDecimal) ((Map<String, Object>) map
					.get("PRELIGHT")).get("EMUSAGE_09"));
			preLight10 = preLight10.add((BigDecimal) ((Map<String, Object>) map
					.get("PRELIGHT")).get("EMUSAGE_10"));
			preLight11 = preLight11.add((BigDecimal) ((Map<String, Object>) map
					.get("PRELIGHT")).get("EMUSAGE_11"));
			preLight12 = preLight12.add((BigDecimal) ((Map<String, Object>) map
					.get("PRELIGHT")).get("EMUSAGE_12"));
			preLight13 = preLight13.add((BigDecimal) ((Map<String, Object>) map
					.get("PRELIGHT")).get("EMUSAGE_13"));
			preLight14 = preLight14.add((BigDecimal) ((Map<String, Object>) map
					.get("PRELIGHT")).get("EMUSAGE_14"));
			preLight15 = preLight15.add((BigDecimal) ((Map<String, Object>) map
					.get("PRELIGHT")).get("EMUSAGE_15"));
			preLight16 = preLight16.add((BigDecimal) ((Map<String, Object>) map
					.get("PRELIGHT")).get("EMUSAGE_16"));
			preLight17 = preLight17.add((BigDecimal) ((Map<String, Object>) map
					.get("PRELIGHT")).get("EMUSAGE_17"));
			preLight18 = preLight18.add((BigDecimal) ((Map<String, Object>) map
					.get("PRELIGHT")).get("EMUSAGE_18"));
			preLight19 = preLight19.add((BigDecimal) ((Map<String, Object>) map
					.get("PRELIGHT")).get("EMUSAGE_19"));
			preLight20 = preLight20.add((BigDecimal) ((Map<String, Object>) map
					.get("PRELIGHT")).get("EMUSAGE_20"));
			preLight21 = preLight21.add((BigDecimal) ((Map<String, Object>) map
					.get("PRELIGHT")).get("EMUSAGE_21"));
			preLight22 = preLight22.add((BigDecimal) ((Map<String, Object>) map
					.get("PRELIGHT")).get("EMUSAGE_22"));
			preLight23 = preLight23.add((BigDecimal) ((Map<String, Object>) map
					.get("PRELIGHT")).get("EMUSAGE_23"));

			cur00 = cur00 + getDoubleValue(map.get("CURVALUE00"));
			cur01 = cur01 + getDoubleValue(map.get("CURVALUE01"));
			cur02 = cur02 + getDoubleValue(map.get("CURVALUE02"));
			cur03 = cur03 + getDoubleValue(map.get("CURVALUE03"));
			cur04 = cur04 + getDoubleValue(map.get("CURVALUE04"));
			cur05 = cur05 + getDoubleValue(map.get("CURVALUE05"));
			cur06 = cur06 + getDoubleValue(map.get("CURVALUE06"));
			cur07 = cur07 + getDoubleValue(map.get("CURVALUE07"));
			cur08 = cur08 + getDoubleValue(map.get("CURVALUE08"));
			cur09 = cur09 + getDoubleValue(map.get("CURVALUE09"));
			cur10 = cur10 + getDoubleValue(map.get("CURVALUE10"));
			cur11 = cur11 + getDoubleValue(map.get("CURVALUE11"));
			cur12 = cur12 + getDoubleValue(map.get("CURVALUE12"));
			cur13 = cur13 + getDoubleValue(map.get("CURVALUE13"));
			cur14 = cur14 + getDoubleValue(map.get("CURVALUE14"));
			cur15 = cur15 + getDoubleValue(map.get("CURVALUE15"));
			cur16 = cur16 + getDoubleValue(map.get("CURVALUE16"));
			cur17 = cur17 + getDoubleValue(map.get("CURVALUE17"));
			cur18 = cur18 + getDoubleValue(map.get("CURVALUE18"));
			cur19 = cur19 + getDoubleValue(map.get("CURVALUE19"));
			cur20 = cur20 + getDoubleValue(map.get("CURVALUE20"));
			cur21 = cur21 + getDoubleValue(map.get("CURVALUE21"));
			cur22 = cur22 + getDoubleValue(map.get("CURVALUE22"));
			cur23 = cur23 + getDoubleValue(map.get("CURVALUE23"));
			curTotal = curTotal + getDoubleValue(map.get("CURTOTAL"));
			curHeat = curHeat.add((BigDecimal) ((Map<String, Object>) map
					.get("HEAT")).get("EMUSAGE"));
			curHeat00 = curHeat00.add((BigDecimal) ((Map<String, Object>) map
					.get("HEAT")).get("EMUSAGE_00"));
			curHeat01 = curHeat01.add((BigDecimal) ((Map<String, Object>) map
					.get("HEAT")).get("EMUSAGE_01"));
			curHeat02 = curHeat02.add((BigDecimal) ((Map<String, Object>) map
					.get("HEAT")).get("EMUSAGE_02"));
			curHeat03 = curHeat03.add((BigDecimal) ((Map<String, Object>) map
					.get("HEAT")).get("EMUSAGE_03"));
			curHeat04 = curHeat04.add((BigDecimal) ((Map<String, Object>) map
					.get("HEAT")).get("EMUSAGE_04"));
			curHeat05 = curHeat05.add((BigDecimal) ((Map<String, Object>) map
					.get("HEAT")).get("EMUSAGE_05"));
			curHeat06 = curHeat06.add((BigDecimal) ((Map<String, Object>) map
					.get("HEAT")).get("EMUSAGE_06"));
			curHeat07 = curHeat07.add((BigDecimal) ((Map<String, Object>) map
					.get("HEAT")).get("EMUSAGE_07"));
			curHeat08 = curHeat08.add((BigDecimal) ((Map<String, Object>) map
					.get("HEAT")).get("EMUSAGE_08"));
			curHeat09 = curHeat09.add((BigDecimal) ((Map<String, Object>) map
					.get("HEAT")).get("EMUSAGE_09"));
			curHeat10 = curHeat10.add((BigDecimal) ((Map<String, Object>) map
					.get("HEAT")).get("EMUSAGE_10"));
			curHeat11 = curHeat11.add((BigDecimal) ((Map<String, Object>) map
					.get("HEAT")).get("EMUSAGE_11"));
			curHeat12 = curHeat12.add((BigDecimal) ((Map<String, Object>) map
					.get("HEAT")).get("EMUSAGE_12"));
			curHeat13 = curHeat13.add((BigDecimal) ((Map<String, Object>) map
					.get("HEAT")).get("EMUSAGE_13"));
			curHeat14 = curHeat14.add((BigDecimal) ((Map<String, Object>) map
					.get("HEAT")).get("EMUSAGE_14"));
			curHeat15 = curHeat15.add((BigDecimal) ((Map<String, Object>) map
					.get("HEAT")).get("EMUSAGE_15"));
			curHeat16 = curHeat16.add((BigDecimal) ((Map<String, Object>) map
					.get("HEAT")).get("EMUSAGE_16"));
			curHeat17 = curHeat17.add((BigDecimal) ((Map<String, Object>) map
					.get("HEAT")).get("EMUSAGE_17"));
			curHeat18 = curHeat18.add((BigDecimal) ((Map<String, Object>) map
					.get("HEAT")).get("EMUSAGE_18"));
			curHeat19 = curHeat19.add((BigDecimal) ((Map<String, Object>) map
					.get("HEAT")).get("EMUSAGE_19"));
			curHeat20 = curHeat20.add((BigDecimal) ((Map<String, Object>) map
					.get("HEAT")).get("EMUSAGE_20"));
			curHeat21 = curHeat21.add((BigDecimal) ((Map<String, Object>) map
					.get("HEAT")).get("EMUSAGE_21"));
			curHeat22 = curHeat22.add((BigDecimal) ((Map<String, Object>) map
					.get("HEAT")).get("EMUSAGE_22"));
			curHeat23 = curHeat23.add((BigDecimal) ((Map<String, Object>) map
					.get("HEAT")).get("EMUSAGE_23"));
			curLight = curLight.add((BigDecimal) ((Map<String, Object>) map
					.get("LIGHT")).get("EMUSAGE"));

			curLight00 = curLight00.add((BigDecimal) ((Map<String, Object>) map
					.get("LIGHT")).get("EMUSAGE_00"));

			curLight01 = curLight01.add((BigDecimal) ((Map<String, Object>) map
					.get("LIGHT")).get("EMUSAGE_01"));
			curLight02 = curLight02.add((BigDecimal) ((Map<String, Object>) map
					.get("LIGHT")).get("EMUSAGE_02"));
			curLight03 = curLight03.add((BigDecimal) ((Map<String, Object>) map
					.get("LIGHT")).get("EMUSAGE_03"));
			curLight04 = curLight04.add((BigDecimal) ((Map<String, Object>) map
					.get("LIGHT")).get("EMUSAGE_04"));
			curLight05 = curLight05.add((BigDecimal) ((Map<String, Object>) map
					.get("LIGHT")).get("EMUSAGE_05"));
			curLight06 = curLight06.add((BigDecimal) ((Map<String, Object>) map
					.get("LIGHT")).get("EMUSAGE_06"));
			curLight07 = curLight07.add((BigDecimal) ((Map<String, Object>) map
					.get("LIGHT")).get("EMUSAGE_07"));
			curLight08 = curLight08.add((BigDecimal) ((Map<String, Object>) map
					.get("LIGHT")).get("EMUSAGE_08"));
			curLight09 = curLight09.add((BigDecimal) ((Map<String, Object>) map
					.get("LIGHT")).get("EMUSAGE_09"));
			curLight10 = curLight10.add((BigDecimal) ((Map<String, Object>) map
					.get("LIGHT")).get("EMUSAGE_10"));
			curLight11 = curLight11.add((BigDecimal) ((Map<String, Object>) map
					.get("LIGHT")).get("EMUSAGE_11"));
			curLight12 = curLight12.add((BigDecimal) ((Map<String, Object>) map
					.get("LIGHT")).get("EMUSAGE_12"));
			curLight13 = curLight13.add((BigDecimal) ((Map<String, Object>) map
					.get("LIGHT")).get("EMUSAGE_13"));
			curLight14 = curLight14.add((BigDecimal) ((Map<String, Object>) map
					.get("LIGHT")).get("EMUSAGE_14"));
			curLight15 = curLight15.add((BigDecimal) ((Map<String, Object>) map
					.get("LIGHT")).get("EMUSAGE_15"));
			curLight16 = curLight16.add((BigDecimal) ((Map<String, Object>) map
					.get("LIGHT")).get("EMUSAGE_16"));
			curLight17 = curLight17.add((BigDecimal) ((Map<String, Object>) map
					.get("LIGHT")).get("EMUSAGE_17"));
			curLight18 = curLight18.add((BigDecimal) ((Map<String, Object>) map
					.get("LIGHT")).get("EMUSAGE_18"));
			curLight19 = curLight19.add((BigDecimal) ((Map<String, Object>) map
					.get("LIGHT")).get("EMUSAGE_19"));
			curLight20 = curLight20.add((BigDecimal) ((Map<String, Object>) map
					.get("LIGHT")).get("EMUSAGE_20"));
			curLight21 = curLight21.add((BigDecimal) ((Map<String, Object>) map
					.get("LIGHT")).get("EMUSAGE_21"));
			curLight22 = curLight22.add((BigDecimal) ((Map<String, Object>) map
					.get("LIGHT")).get("EMUSAGE_22"));
			curLight23 = curLight23.add((BigDecimal) ((Map<String, Object>) map
					.get("LIGHT")).get("EMUSAGE_23"));

		}
		retPreMap.put("EMUSAGE_00", pre00);
		retPreMap.put("EMUSAGE_01", pre01);
		retPreMap.put("EMUSAGE_02", pre02);
		retPreMap.put("EMUSAGE_03", pre03);
		retPreMap.put("EMUSAGE_04", pre04);
		retPreMap.put("EMUSAGE_05", pre05);
		retPreMap.put("EMUSAGE_06", pre06);
		retPreMap.put("EMUSAGE_07", pre07);
		retPreMap.put("EMUSAGE_08", pre08);
		retPreMap.put("EMUSAGE_09", pre09);
		retPreMap.put("EMUSAGE_10", pre10);
		retPreMap.put("EMUSAGE_11", pre11);
		retPreMap.put("EMUSAGE_12", pre12);
		retPreMap.put("EMUSAGE_13", pre13);
		retPreMap.put("EMUSAGE_14", pre14);
		retPreMap.put("EMUSAGE_15", pre15);
		retPreMap.put("EMUSAGE_16", pre16);
		retPreMap.put("EMUSAGE_17", pre17);
		retPreMap.put("EMUSAGE_18", pre18);
		retPreMap.put("EMUSAGE_19", pre19);
		retPreMap.put("EMUSAGE_20", pre20);
		retPreMap.put("EMUSAGE_21", pre21);
		retPreMap.put("EMUSAGE_22", pre22);
		retPreMap.put("EMUSAGE_23", pre23);
		retPreMap.put("TOTAL", preTotal);

		retPreMap.put("HEAT", preHeat);
		retPreHeatMap.put("EMUSAGE_00", preHeat00);
		retPreHeatMap.put("EMUSAGE_01", preHeat01);
		retPreHeatMap.put("EMUSAGE_02", preHeat02);
		retPreHeatMap.put("EMUSAGE_03", preHeat03);
		retPreHeatMap.put("EMUSAGE_04", preHeat04);
		retPreHeatMap.put("EMUSAGE_05", preHeat05);
		retPreHeatMap.put("EMUSAGE_06", preHeat06);
		retPreHeatMap.put("EMUSAGE_07", preHeat07);
		retPreHeatMap.put("EMUSAGE_08", preHeat08);
		retPreHeatMap.put("EMUSAGE_09", preHeat09);
		retPreHeatMap.put("EMUSAGE_10", preHeat10);
		retPreHeatMap.put("EMUSAGE_11", preHeat11);
		retPreHeatMap.put("EMUSAGE_12", preHeat12);
		retPreHeatMap.put("EMUSAGE_13", preHeat13);
		retPreHeatMap.put("EMUSAGE_14", preHeat14);
		retPreHeatMap.put("EMUSAGE_15", preHeat15);
		retPreHeatMap.put("EMUSAGE_16", preHeat16);
		retPreHeatMap.put("EMUSAGE_17", preHeat17);
		retPreHeatMap.put("EMUSAGE_18", preHeat18);
		retPreHeatMap.put("EMUSAGE_19", preHeat19);
		retPreHeatMap.put("EMUSAGE_20", preHeat20);
		retPreHeatMap.put("EMUSAGE_21", preHeat21);
		retPreHeatMap.put("EMUSAGE_22", preHeat22);
		retPreHeatMap.put("EMUSAGE_23", preHeat23);
		retPreHeatMap.put("LIGHT", preLight);
		retPreLightMap.put("EMUSAGE_00", preLight00);
		retPreLightMap.put("EMUSAGE_01", preLight01);
		retPreLightMap.put("EMUSAGE_02", preLight02);
		retPreLightMap.put("EMUSAGE_03", preLight03);
		retPreLightMap.put("EMUSAGE_04", preLight04);
		retPreLightMap.put("EMUSAGE_05", preLight05);
		retPreLightMap.put("EMUSAGE_06", preLight06);
		retPreLightMap.put("EMUSAGE_07", preLight07);
		retPreLightMap.put("EMUSAGE_08", preLight08);
		retPreLightMap.put("EMUSAGE_09", preLight09);
		retPreLightMap.put("EMUSAGE_10", preLight10);
		retPreLightMap.put("EMUSAGE_11", preLight11);
		retPreLightMap.put("EMUSAGE_12", preLight12);
		retPreLightMap.put("EMUSAGE_13", preLight13);
		retPreLightMap.put("EMUSAGE_14", preLight14);
		retPreLightMap.put("EMUSAGE_15", preLight15);
		retPreLightMap.put("EMUSAGE_16", preLight16);
		retPreLightMap.put("EMUSAGE_17", preLight17);
		retPreLightMap.put("EMUSAGE_18", preLight18);
		retPreLightMap.put("EMUSAGE_19", preLight19);
		retPreLightMap.put("EMUSAGE_20", preLight20);
		retPreLightMap.put("EMUSAGE_21", preLight21);
		retPreLightMap.put("EMUSAGE_22", preLight22);
		retPreLightMap.put("EMUSAGE_23", preLight23);

		retDailyMap.put("EMUSAGE_00", cur00);
		retDailyMap.put("EMUSAGE_01", cur01);
		retDailyMap.put("EMUSAGE_02", cur02);
		retDailyMap.put("EMUSAGE_03", cur03);
		retDailyMap.put("EMUSAGE_04", cur04);
		retDailyMap.put("EMUSAGE_05", cur05);
		retDailyMap.put("EMUSAGE_06", cur06);
		retDailyMap.put("EMUSAGE_07", cur07);
		retDailyMap.put("EMUSAGE_08", cur08);
		retDailyMap.put("EMUSAGE_09", cur09);
		retDailyMap.put("EMUSAGE_10", cur10);
		retDailyMap.put("EMUSAGE_11", cur11);
		retDailyMap.put("EMUSAGE_12", cur12);
		retDailyMap.put("EMUSAGE_13", cur13);
		retDailyMap.put("EMUSAGE_14", cur14);
		retDailyMap.put("EMUSAGE_15", cur15);
		retDailyMap.put("EMUSAGE_16", cur16);
		retDailyMap.put("EMUSAGE_17", cur17);
		retDailyMap.put("EMUSAGE_18", cur18);
		retDailyMap.put("EMUSAGE_19", cur19);
		retDailyMap.put("EMUSAGE_20", cur20);
		retDailyMap.put("EMUSAGE_21", cur21);
		retDailyMap.put("EMUSAGE_22", cur22);
		retDailyMap.put("EMUSAGE_23", cur23);
		retDailyMap.put("TOTAL", curTotal);
		retDailyMap.put("HEAT", curHeat);
		retDailyHeatMap.put("EMUSAGE_00", curHeat00);
		retDailyHeatMap.put("EMUSAGE_01", curHeat01);
		retDailyHeatMap.put("EMUSAGE_02", curHeat02);
		retDailyHeatMap.put("EMUSAGE_03", curHeat03);
		retDailyHeatMap.put("EMUSAGE_04", curHeat04);
		retDailyHeatMap.put("EMUSAGE_05", curHeat05);
		retDailyHeatMap.put("EMUSAGE_06", curHeat06);
		retDailyHeatMap.put("EMUSAGE_07", curHeat07);
		retDailyHeatMap.put("EMUSAGE_08", curHeat08);
		retDailyHeatMap.put("EMUSAGE_09", curHeat09);
		retDailyHeatMap.put("EMUSAGE_10", curHeat10);
		retDailyHeatMap.put("EMUSAGE_11", curHeat11);
		retDailyHeatMap.put("EMUSAGE_12", curHeat12);
		retDailyHeatMap.put("EMUSAGE_13", curHeat13);
		retDailyHeatMap.put("EMUSAGE_14", curHeat14);
		retDailyHeatMap.put("EMUSAGE_15", curHeat15);
		retDailyHeatMap.put("EMUSAGE_16", curHeat16);
		retDailyHeatMap.put("EMUSAGE_17", curHeat17);
		retDailyHeatMap.put("EMUSAGE_18", curHeat18);
		retDailyHeatMap.put("EMUSAGE_19", curHeat19);
		retDailyHeatMap.put("EMUSAGE_20", curHeat20);
		retDailyHeatMap.put("EMUSAGE_21", curHeat21);
		retDailyHeatMap.put("EMUSAGE_22", curHeat22);
		retDailyHeatMap.put("EMUSAGE_23", curHeat23);

		retDailyMap.put("LIGHT", curLight);
		// System.out.println("curLight:"+curLight);
		retDailyLightMap.put("EMUSAGE_00", curLight00);
		retDailyLightMap.put("EMUSAGE_01", curLight01);
		retDailyLightMap.put("EMUSAGE_02", curLight02);
		retDailyLightMap.put("EMUSAGE_03", curLight03);
		retDailyLightMap.put("EMUSAGE_04", curLight04);
		retDailyLightMap.put("EMUSAGE_05", curLight05);
		retDailyLightMap.put("EMUSAGE_06", curLight06);
		retDailyLightMap.put("EMUSAGE_07", curLight07);
		retDailyLightMap.put("EMUSAGE_08", curLight08);
		retDailyLightMap.put("EMUSAGE_09", curLight09);
		retDailyLightMap.put("EMUSAGE_10", curLight10);
		retDailyLightMap.put("EMUSAGE_11", curLight11);
		retDailyLightMap.put("EMUSAGE_12", curLight12);
		retDailyLightMap.put("EMUSAGE_13", curLight13);
		retDailyLightMap.put("EMUSAGE_14", curLight14);
		retDailyLightMap.put("EMUSAGE_15", curLight15);
		retDailyLightMap.put("EMUSAGE_16", curLight16);
		retDailyLightMap.put("EMUSAGE_17", curLight17);
		retDailyLightMap.put("EMUSAGE_18", curLight18);
		retDailyLightMap.put("EMUSAGE_19", curLight19);
		retDailyLightMap.put("EMUSAGE_20", curLight20);
		retDailyLightMap.put("EMUSAGE_21", curLight21);
		retDailyLightMap.put("EMUSAGE_22", curLight22);
		retDailyLightMap.put("EMUSAGE_23", curLight23);
		retMap.put("PRE", retPreMap);
		retMap.put("PREHEAT", retPreHeatMap);
		retMap.put("PRELIGHT", retPreLightMap);
		retMap.put("CUR", retDailyMap);
		retMap.put("CURHEAT", retDailyHeatMap);
		retMap.put("CURLIGHT", retDailyLightMap);

		return retMap;

	}

	private Double getDoubleValue(Object value) {
		return value == null ? 0d : (Double) value;
	}

	private Map<String, Object> getTotalWeeklyNewMap(
			List<Map<String, Object>> list) {
		Map<String, Object> retPreMap = new HashMap<String, Object>();
		Map<String, Object> retDailyMap = new HashMap<String, Object>();
		Map<String, Object> retMap = new HashMap<String, Object>();

		Map<String, Object> retPreHeatMap = new HashMap<String, Object>();
		Map<String, Object> retDailyHeatMap = new HashMap<String, Object>();

		Map<String, Object> retPreLightMap = new HashMap<String, Object>();
		Map<String, Object> retDailyLightMap = new HashMap<String, Object>();

		Double pre00 = 0d;
		Double pre01 = 0d;
		Double pre02 = 0d;
		Double pre03 = 0d;
		Double pre04 = 0d;
		Double pre05 = 0d;
		Double pre06 = 0d;

		Double preTotal = 0d;
		BigDecimal preLight = new BigDecimal(0);
		BigDecimal preLight00 = new BigDecimal(0);
		BigDecimal preLight01 = new BigDecimal(0);
		BigDecimal preLight02 = new BigDecimal(0);
		BigDecimal preLight03 = new BigDecimal(0);
		BigDecimal preLight04 = new BigDecimal(0);
		BigDecimal preLight05 = new BigDecimal(0);
		BigDecimal preLight06 = new BigDecimal(0);

		BigDecimal preHeat = new BigDecimal(0);
		BigDecimal preHeat00 = new BigDecimal(0);
		BigDecimal preHeat01 = new BigDecimal(0);
		BigDecimal preHeat02 = new BigDecimal(0);
		BigDecimal preHeat03 = new BigDecimal(0);
		BigDecimal preHeat04 = new BigDecimal(0);
		BigDecimal preHeat05 = new BigDecimal(0);
		BigDecimal preHeat06 = new BigDecimal(0);

		Double cur00 = 0d;
		Double cur01 = 0d;
		Double cur02 = 0d;
		Double cur03 = 0d;
		Double cur04 = 0d;
		Double cur05 = 0d;
		Double cur06 = 0d;

		Double curTotal = 0d;
		BigDecimal curLight = new BigDecimal(0);
		BigDecimal curLight00 = new BigDecimal(0);
		BigDecimal curLight01 = new BigDecimal(0);
		BigDecimal curLight02 = new BigDecimal(0);
		BigDecimal curLight03 = new BigDecimal(0);
		BigDecimal curLight04 = new BigDecimal(0);
		BigDecimal curLight05 = new BigDecimal(0);
		BigDecimal curLight06 = new BigDecimal(0);

		BigDecimal curHeat = new BigDecimal(0);
		BigDecimal curHeat00 = new BigDecimal(0);
		BigDecimal curHeat01 = new BigDecimal(0);
		BigDecimal curHeat02 = new BigDecimal(0);
		BigDecimal curHeat03 = new BigDecimal(0);
		BigDecimal curHeat04 = new BigDecimal(0);
		BigDecimal curHeat05 = new BigDecimal(0);
		BigDecimal curHeat06 = new BigDecimal(0);

		for (Map<String, Object> map : list) {

			pre00 = pre00 + getDoubleValue(map.get("PREVALUE01"));
			pre01 = pre01 + getDoubleValue(map.get("PREVALUE02"));
			pre02 = pre02 + getDoubleValue(map.get("PREVALUE03"));
			pre03 = pre03 + getDoubleValue(map.get("PREVALUE04"));
			pre04 = pre04 + getDoubleValue(map.get("PREVALUE05"));
			pre05 = pre05 + getDoubleValue(map.get("PREVALUE06"));
			pre06 = pre06 + getDoubleValue(map.get("PREVALUE07"));

			preTotal = preTotal + getDoubleValue(map.get("PRETOTAL"));
			preHeat = preHeat.add((BigDecimal) ((Map<String, Object>) map
					.get("PREHEAT")).get("EMUSAGE"));

			preHeat00 = preHeat00.add((BigDecimal) ((Map<String, Object>) map
					.get("PREHEAT")).get("EMUSAGE_01"));
			preHeat01 = preHeat01.add((BigDecimal) ((Map<String, Object>) map
					.get("PREHEAT")).get("EMUSAGE_02"));
			preHeat02 = preHeat02.add((BigDecimal) ((Map<String, Object>) map
					.get("PREHEAT")).get("EMUSAGE_03"));
			preHeat03 = preHeat03.add((BigDecimal) ((Map<String, Object>) map
					.get("PREHEAT")).get("EMUSAGE_04"));
			preHeat04 = preHeat04.add((BigDecimal) ((Map<String, Object>) map
					.get("PREHEAT")).get("EMUSAGE_05"));
			preHeat05 = preHeat05.add((BigDecimal) ((Map<String, Object>) map
					.get("PREHEAT")).get("EMUSAGE_06"));
			preHeat06 = preHeat06.add((BigDecimal) ((Map<String, Object>) map
					.get("PREHEAT")).get("EMUSAGE_07"));

			preLight = preLight.add((BigDecimal) ((Map<String, Object>) map
					.get("PRELIGHT")).get("EMUSAGE"));
			preLight00 = preLight00.add((BigDecimal) ((Map<String, Object>) map
					.get("PRELIGHT")).get("EMUSAGE_01"));
			preLight01 = preLight01.add((BigDecimal) ((Map<String, Object>) map
					.get("PRELIGHT")).get("EMUSAGE_02"));
			preLight02 = preLight02.add((BigDecimal) ((Map<String, Object>) map
					.get("PRELIGHT")).get("EMUSAGE_03"));
			preLight03 = preLight03.add((BigDecimal) ((Map<String, Object>) map
					.get("PRELIGHT")).get("EMUSAGE_04"));
			preLight04 = preLight04.add((BigDecimal) ((Map<String, Object>) map
					.get("PRELIGHT")).get("EMUSAGE_05"));
			preLight05 = preLight05.add((BigDecimal) ((Map<String, Object>) map
					.get("PRELIGHT")).get("EMUSAGE_06"));
			preLight06 = preLight06.add((BigDecimal) ((Map<String, Object>) map
					.get("PRELIGHT")).get("EMUSAGE_07"));

			cur00 = cur00 + getDoubleValue(map.get("CURVALUE01"));
			cur01 = cur01 + getDoubleValue(map.get("CURVALUE02"));
			cur02 = cur02 + getDoubleValue(map.get("CURVALUE03"));
			cur03 = cur03 + getDoubleValue(map.get("CURVALUE04"));
			cur04 = cur04 + getDoubleValue(map.get("CURVALUE05"));
			cur05 = cur05 + getDoubleValue(map.get("CURVALUE06"));
			cur06 = cur06 + getDoubleValue(map.get("CURVALUE07"));

			curTotal = curTotal + getDoubleValue(map.get("CURTOTAL"));
			curHeat = curHeat.add((BigDecimal) ((Map<String, Object>) map
					.get("HEAT")).get("EMUSAGE"));
			curHeat00 = curHeat00.add((BigDecimal) ((Map<String, Object>) map
					.get("HEAT")).get("EMUSAGE_01"));
			curHeat01 = curHeat01.add((BigDecimal) ((Map<String, Object>) map
					.get("HEAT")).get("EMUSAGE_02"));
			curHeat02 = curHeat02.add((BigDecimal) ((Map<String, Object>) map
					.get("HEAT")).get("EMUSAGE_03"));
			curHeat03 = curHeat03.add((BigDecimal) ((Map<String, Object>) map
					.get("HEAT")).get("EMUSAGE_04"));
			curHeat04 = curHeat04.add((BigDecimal) ((Map<String, Object>) map
					.get("HEAT")).get("EMUSAGE_05"));
			curHeat05 = curHeat05.add((BigDecimal) ((Map<String, Object>) map
					.get("HEAT")).get("EMUSAGE_06"));
			curHeat06 = curHeat06.add((BigDecimal) ((Map<String, Object>) map
					.get("HEAT")).get("EMUSAGE_07"));

			curLight = curLight.add((BigDecimal) ((Map<String, Object>) map
					.get("LIGHT")).get("EMUSAGE"));

			curLight00 = curLight00.add((BigDecimal) ((Map<String, Object>) map
					.get("LIGHT")).get("EMUSAGE_01"));
			curLight01 = curLight01.add((BigDecimal) ((Map<String, Object>) map
					.get("LIGHT")).get("EMUSAGE_02"));
			curLight02 = curLight02.add((BigDecimal) ((Map<String, Object>) map
					.get("LIGHT")).get("EMUSAGE_03"));
			curLight03 = curLight03.add((BigDecimal) ((Map<String, Object>) map
					.get("LIGHT")).get("EMUSAGE_04"));
			curLight04 = curLight04.add((BigDecimal) ((Map<String, Object>) map
					.get("LIGHT")).get("EMUSAGE_05"));
			curLight05 = curLight05.add((BigDecimal) ((Map<String, Object>) map
					.get("LIGHT")).get("EMUSAGE_06"));
			curLight06 = curLight06.add((BigDecimal) ((Map<String, Object>) map
					.get("LIGHT")).get("EMUSAGE_07"));

		}
		retPreMap.put("EMUSAGE_01", pre00);
		retPreMap.put("EMUSAGE_02", pre01);
		retPreMap.put("EMUSAGE_03", pre02);
		retPreMap.put("EMUSAGE_04", pre03);
		retPreMap.put("EMUSAGE_05", pre04);
		retPreMap.put("EMUSAGE_06", pre05);
		retPreMap.put("EMUSAGE_07", pre06);

		retPreMap.put("TOTAL", preTotal);
		retPreMap.put("HEAT", preHeat);
		retPreHeatMap.put("EMUSAGE_01", preHeat00);
		retPreHeatMap.put("EMUSAGE_02", preHeat01);
		retPreHeatMap.put("EMUSAGE_03", preHeat02);
		retPreHeatMap.put("EMUSAGE_04", preHeat03);
		retPreHeatMap.put("EMUSAGE_05", preHeat04);
		retPreHeatMap.put("EMUSAGE_06", preHeat05);
		retPreHeatMap.put("EMUSAGE_07", preHeat06);

		retPreHeatMap.put("LIGHT", preLight);
		retPreLightMap.put("EMUSAGE_01", preLight00);
		retPreLightMap.put("EMUSAGE_02", preLight01);
		retPreLightMap.put("EMUSAGE_03", preLight02);
		retPreLightMap.put("EMUSAGE_04", preLight03);
		retPreLightMap.put("EMUSAGE_05", preLight04);
		retPreLightMap.put("EMUSAGE_06", preLight05);
		retPreLightMap.put("EMUSAGE_07", preLight06);

		retDailyMap.put("EMUSAGE_01", cur00);
		retDailyMap.put("EMUSAGE_02", cur01);
		retDailyMap.put("EMUSAGE_03", cur02);
		retDailyMap.put("EMUSAGE_04", cur03);
		retDailyMap.put("EMUSAGE_05", cur04);
		retDailyMap.put("EMUSAGE_06", cur05);
		retDailyMap.put("EMUSAGE_07", cur06);

		retDailyMap.put("TOTAL", curTotal);
		retDailyMap.put("HEAT", curHeat);
		retDailyHeatMap.put("EMUSAGE_01", curHeat00);
		retDailyHeatMap.put("EMUSAGE_02", curHeat01);
		retDailyHeatMap.put("EMUSAGE_03", curHeat02);
		retDailyHeatMap.put("EMUSAGE_04", curHeat03);
		retDailyHeatMap.put("EMUSAGE_05", curHeat04);
		retDailyHeatMap.put("EMUSAGE_06", curHeat05);
		retDailyHeatMap.put("EMUSAGE_07", curHeat06);

		retDailyMap.put("LIGHT", curLight);
		retDailyLightMap.put("EMUSAGE_01", curLight00);
		retDailyLightMap.put("EMUSAGE_02", curLight01);
		retDailyLightMap.put("EMUSAGE_03", curLight02);
		retDailyLightMap.put("EMUSAGE_04", curLight03);
		retDailyLightMap.put("EMUSAGE_05", curLight04);
		retDailyLightMap.put("EMUSAGE_06", curLight05);
		retDailyLightMap.put("EMUSAGE_07", curLight06);

		retMap.put("PRE", retPreMap);
		retMap.put("PREHEAT", retPreHeatMap);
		retMap.put("PRELIGHT", retPreLightMap);
		retMap.put("CUR", retDailyMap);
		retMap.put("CURHEAT", retDailyHeatMap);
		retMap.put("CURLIGHT", retDailyLightMap);
		System.out.println("weekly retMap:" + retMap);
		return retMap;

	}

	private Map<String, Object> getTotalMonthlyNewMap(
			List<Map<String, Object>> list) {
		Map<String, Object> retPreMap = new HashMap<String, Object>();
		Map<String, Object> retDailyMap = new HashMap<String, Object>();
		Map<String, Object> retMap = new HashMap<String, Object>();

		Map<String, Object> retPreHeatMap = new HashMap<String, Object>();
		Map<String, Object> retDailyHeatMap = new HashMap<String, Object>();

		Map<String, Object> retPreLightMap = new HashMap<String, Object>();
		Map<String, Object> retDailyLightMap = new HashMap<String, Object>();

		Double pre01 = 0d;
		Double pre02 = 0d;
		Double pre03 = 0d;
		Double pre04 = 0d;
		Double pre05 = 0d;
		Double pre06 = 0d;
		Double pre07 = 0d;
		Double pre08 = 0d;
		Double pre09 = 0d;
		Double pre10 = 0d;
		Double pre11 = 0d;
		Double pre12 = 0d;
		Double pre13 = 0d;
		Double pre14 = 0d;
		Double pre15 = 0d;
		Double pre16 = 0d;
		Double pre17 = 0d;
		Double pre18 = 0d;
		Double pre19 = 0d;
		Double pre20 = 0d;
		Double pre21 = 0d;
		Double pre22 = 0d;
		Double pre23 = 0d;
		Double pre24 = 0d;
		Double pre25 = 0d;
		Double pre26 = 0d;
		Double pre27 = 0d;
		Double pre28 = 0d;
		Double pre29 = 0d;
		Double pre30 = 0d;
		Double pre31 = 0d;
		Double preTotal = 0d;

		BigDecimal preLight = new BigDecimal(0);
		BigDecimal preLight01 = new BigDecimal(0);
		BigDecimal preLight02 = new BigDecimal(0);
		BigDecimal preLight03 = new BigDecimal(0);
		BigDecimal preLight04 = new BigDecimal(0);
		BigDecimal preLight05 = new BigDecimal(0);
		BigDecimal preLight06 = new BigDecimal(0);
		BigDecimal preLight07 = new BigDecimal(0);
		BigDecimal preLight08 = new BigDecimal(0);
		BigDecimal preLight09 = new BigDecimal(0);
		BigDecimal preLight10 = new BigDecimal(0);
		BigDecimal preLight11 = new BigDecimal(0);
		BigDecimal preLight12 = new BigDecimal(0);
		BigDecimal preLight13 = new BigDecimal(0);
		BigDecimal preLight14 = new BigDecimal(0);
		BigDecimal preLight15 = new BigDecimal(0);
		BigDecimal preLight16 = new BigDecimal(0);
		BigDecimal preLight17 = new BigDecimal(0);
		BigDecimal preLight18 = new BigDecimal(0);
		BigDecimal preLight19 = new BigDecimal(0);
		BigDecimal preLight20 = new BigDecimal(0);
		BigDecimal preLight21 = new BigDecimal(0);
		BigDecimal preLight22 = new BigDecimal(0);
		BigDecimal preLight23 = new BigDecimal(0);
		BigDecimal preLight24 = new BigDecimal(0);
		BigDecimal preLight25 = new BigDecimal(0);
		BigDecimal preLight26 = new BigDecimal(0);
		BigDecimal preLight27 = new BigDecimal(0);
		BigDecimal preLight28 = new BigDecimal(0);
		BigDecimal preLight29 = new BigDecimal(0);
		BigDecimal preLight30 = new BigDecimal(0);
		BigDecimal preLight31 = new BigDecimal(0);

		BigDecimal preHeat = new BigDecimal(0);
		BigDecimal preHeat01 = new BigDecimal(0);
		BigDecimal preHeat02 = new BigDecimal(0);
		BigDecimal preHeat03 = new BigDecimal(0);
		BigDecimal preHeat04 = new BigDecimal(0);
		BigDecimal preHeat05 = new BigDecimal(0);
		BigDecimal preHeat06 = new BigDecimal(0);
		BigDecimal preHeat07 = new BigDecimal(0);
		BigDecimal preHeat08 = new BigDecimal(0);
		BigDecimal preHeat09 = new BigDecimal(0);
		BigDecimal preHeat10 = new BigDecimal(0);
		BigDecimal preHeat11 = new BigDecimal(0);
		BigDecimal preHeat12 = new BigDecimal(0);
		BigDecimal preHeat13 = new BigDecimal(0);
		BigDecimal preHeat14 = new BigDecimal(0);
		BigDecimal preHeat15 = new BigDecimal(0);
		BigDecimal preHeat16 = new BigDecimal(0);
		BigDecimal preHeat17 = new BigDecimal(0);
		BigDecimal preHeat18 = new BigDecimal(0);
		BigDecimal preHeat19 = new BigDecimal(0);
		BigDecimal preHeat20 = new BigDecimal(0);
		BigDecimal preHeat21 = new BigDecimal(0);
		BigDecimal preHeat22 = new BigDecimal(0);
		BigDecimal preHeat23 = new BigDecimal(0);
		BigDecimal preHeat24 = new BigDecimal(0);
		BigDecimal preHeat25 = new BigDecimal(0);
		BigDecimal preHeat26 = new BigDecimal(0);
		BigDecimal preHeat27 = new BigDecimal(0);
		BigDecimal preHeat28 = new BigDecimal(0);
		BigDecimal preHeat29 = new BigDecimal(0);
		BigDecimal preHeat30 = new BigDecimal(0);
		BigDecimal preHeat31 = new BigDecimal(0);

		Double cur00 = 0d;
		Double cur01 = 0d;
		Double cur02 = 0d;
		Double cur03 = 0d;
		Double cur04 = 0d;
		Double cur05 = 0d;
		Double cur06 = 0d;
		Double cur07 = 0d;
		Double cur08 = 0d;
		Double cur09 = 0d;
		Double cur10 = 0d;
		Double cur11 = 0d;
		Double cur12 = 0d;
		Double cur13 = 0d;
		Double cur14 = 0d;
		Double cur15 = 0d;
		Double cur16 = 0d;
		Double cur17 = 0d;
		Double cur18 = 0d;
		Double cur19 = 0d;
		Double cur20 = 0d;
		Double cur21 = 0d;
		Double cur22 = 0d;
		Double cur23 = 0d;
		Double cur24 = 0d;
		Double cur25 = 0d;
		Double cur26 = 0d;
		Double cur27 = 0d;
		Double cur28 = 0d;
		Double cur29 = 0d;
		Double cur30 = 0d;
		Double cur31 = 0d;

		Double curTotal = 0d;
		BigDecimal curLight = new BigDecimal(0);
		BigDecimal curLight01 = new BigDecimal(0);
		BigDecimal curLight02 = new BigDecimal(0);
		BigDecimal curLight03 = new BigDecimal(0);
		BigDecimal curLight04 = new BigDecimal(0);
		BigDecimal curLight05 = new BigDecimal(0);
		BigDecimal curLight06 = new BigDecimal(0);
		BigDecimal curLight07 = new BigDecimal(0);
		BigDecimal curLight08 = new BigDecimal(0);
		BigDecimal curLight09 = new BigDecimal(0);
		BigDecimal curLight10 = new BigDecimal(0);
		BigDecimal curLight11 = new BigDecimal(0);
		BigDecimal curLight12 = new BigDecimal(0);
		BigDecimal curLight13 = new BigDecimal(0);
		BigDecimal curLight14 = new BigDecimal(0);
		BigDecimal curLight15 = new BigDecimal(0);
		BigDecimal curLight16 = new BigDecimal(0);
		BigDecimal curLight17 = new BigDecimal(0);
		BigDecimal curLight18 = new BigDecimal(0);
		BigDecimal curLight19 = new BigDecimal(0);
		BigDecimal curLight20 = new BigDecimal(0);
		BigDecimal curLight21 = new BigDecimal(0);
		BigDecimal curLight22 = new BigDecimal(0);
		BigDecimal curLight23 = new BigDecimal(0);
		BigDecimal curLight24 = new BigDecimal(0);
		BigDecimal curLight25 = new BigDecimal(0);
		BigDecimal curLight26 = new BigDecimal(0);
		BigDecimal curLight27 = new BigDecimal(0);
		BigDecimal curLight28 = new BigDecimal(0);
		BigDecimal curLight29 = new BigDecimal(0);
		BigDecimal curLight30 = new BigDecimal(0);
		BigDecimal curLight31 = new BigDecimal(0);

		BigDecimal curHeat = new BigDecimal(0);
		BigDecimal curHeat01 = new BigDecimal(0);
		BigDecimal curHeat02 = new BigDecimal(0);
		BigDecimal curHeat03 = new BigDecimal(0);
		BigDecimal curHeat04 = new BigDecimal(0);
		BigDecimal curHeat05 = new BigDecimal(0);
		BigDecimal curHeat06 = new BigDecimal(0);
		BigDecimal curHeat07 = new BigDecimal(0);
		BigDecimal curHeat08 = new BigDecimal(0);
		BigDecimal curHeat09 = new BigDecimal(0);
		BigDecimal curHeat10 = new BigDecimal(0);
		BigDecimal curHeat11 = new BigDecimal(0);
		BigDecimal curHeat12 = new BigDecimal(0);
		BigDecimal curHeat13 = new BigDecimal(0);
		BigDecimal curHeat14 = new BigDecimal(0);
		BigDecimal curHeat15 = new BigDecimal(0);
		BigDecimal curHeat16 = new BigDecimal(0);
		BigDecimal curHeat17 = new BigDecimal(0);
		BigDecimal curHeat18 = new BigDecimal(0);
		BigDecimal curHeat19 = new BigDecimal(0);
		BigDecimal curHeat20 = new BigDecimal(0);
		BigDecimal curHeat21 = new BigDecimal(0);
		BigDecimal curHeat22 = new BigDecimal(0);
		BigDecimal curHeat23 = new BigDecimal(0);
		BigDecimal curHeat24 = new BigDecimal(0);
		BigDecimal curHeat25 = new BigDecimal(0);
		BigDecimal curHeat26 = new BigDecimal(0);
		BigDecimal curHeat27 = new BigDecimal(0);
		BigDecimal curHeat28 = new BigDecimal(0);
		BigDecimal curHeat29 = new BigDecimal(0);
		BigDecimal curHeat30 = new BigDecimal(0);
		BigDecimal curHeat31 = new BigDecimal(0);

		for (Map<String, Object> map : list) {

			pre01 = pre01 + getDoubleValue(map.get("PREVALUE01"));
			pre02 = pre02 + getDoubleValue(map.get("PREVALUE02"));
			pre03 = pre03 + getDoubleValue(map.get("PREVALUE03"));
			pre04 = pre04 + getDoubleValue(map.get("PREVALUE04"));
			pre05 = pre05 + getDoubleValue(map.get("PREVALUE05"));
			pre06 = pre06 + getDoubleValue(map.get("PREVALUE06"));
			pre07 = pre07 + getDoubleValue(map.get("PREVALUE07"));
			pre08 = pre08 + getDoubleValue(map.get("PREVALUE08"));
			pre09 = pre09 + getDoubleValue(map.get("PREVALUE09"));
			pre10 = pre10 + getDoubleValue(map.get("PREVALUE10"));
			pre11 = pre11 + getDoubleValue(map.get("PREVALUE11"));
			pre12 = pre12 + getDoubleValue(map.get("PREVALUE12"));
			pre13 = pre13 + getDoubleValue(map.get("PREVALUE13"));
			pre14 = pre14 + getDoubleValue(map.get("PREVALUE14"));
			pre15 = pre15 + getDoubleValue(map.get("PREVALUE15"));
			pre16 = pre16 + getDoubleValue(map.get("PREVALUE16"));
			pre17 = pre17 + getDoubleValue(map.get("PREVALUE17"));
			pre18 = pre18 + getDoubleValue(map.get("PREVALUE18"));
			pre19 = pre19 + getDoubleValue(map.get("PREVALUE19"));
			pre20 = pre20 + getDoubleValue(map.get("PREVALUE20"));
			pre21 = pre21 + getDoubleValue(map.get("PREVALUE21"));
			pre22 = pre22 + getDoubleValue(map.get("PREVALUE22"));
			pre23 = pre23 + getDoubleValue(map.get("PREUSAGE23"));
			pre23 = pre24 + getDoubleValue(map.get("PREVALUE24"));
			pre23 = pre25 + getDoubleValue(map.get("PREVALUE25"));
			pre23 = pre26 + getDoubleValue(map.get("PREVALUE26"));
			pre23 = pre27 + getDoubleValue(map.get("PREVALUE27"));
			pre23 = pre28 + getDoubleValue(map.get("PREVALUE28"));
			pre23 = pre29 + getDoubleValue(map.get("PREVALUE29"));
			pre23 = pre30 + getDoubleValue(map.get("PREVALUE30"));
			pre23 = pre31 + getDoubleValue(map.get("PREVALUE31"));
			preTotal = preTotal + getDoubleValue(map.get("PRETOTAL"));
			preHeat = preHeat.add((BigDecimal) ((Map<String, Object>) map
					.get("HEAT")).get("EMUSAGE"));
			preHeat01 = preHeat01.add((BigDecimal) ((Map<String, Object>) map
					.get("PREHEAT")).get("EMUSAGE_01"));
			preHeat02 = preHeat02.add((BigDecimal) ((Map<String, Object>) map
					.get("PREHEAT")).get("EMUSAGE_02"));
			preHeat03 = preHeat03.add((BigDecimal) ((Map<String, Object>) map
					.get("PREHEAT")).get("EMUSAGE_03"));
			preHeat04 = preHeat04.add((BigDecimal) ((Map<String, Object>) map
					.get("PREHEAT")).get("EMUSAGE_04"));
			preHeat05 = preHeat05.add((BigDecimal) ((Map<String, Object>) map
					.get("PREHEAT")).get("EMUSAGE_05"));
			preHeat06 = preHeat06.add((BigDecimal) ((Map<String, Object>) map
					.get("PREHEAT")).get("EMUSAGE_06"));
			preHeat07 = preHeat07.add((BigDecimal) ((Map<String, Object>) map
					.get("PREHEAT")).get("EMUSAGE_07"));
			preHeat08 = preHeat08.add((BigDecimal) ((Map<String, Object>) map
					.get("PREHEAT")).get("EMUSAGE_08"));
			preHeat09 = preHeat09.add((BigDecimal) ((Map<String, Object>) map
					.get("PREHEAT")).get("EMUSAGE_09"));
			preHeat10 = preHeat10.add((BigDecimal) ((Map<String, Object>) map
					.get("PREHEAT")).get("EMUSAGE_10"));
			preHeat11 = preHeat11.add((BigDecimal) ((Map<String, Object>) map
					.get("PREHEAT")).get("EMUSAGE_11"));
			preHeat12 = preHeat12.add((BigDecimal) ((Map<String, Object>) map
					.get("PREHEAT")).get("EMUSAGE_12"));
			preHeat13 = preHeat13.add((BigDecimal) ((Map<String, Object>) map
					.get("PREHEAT")).get("EMUSAGE_13"));
			preHeat14 = preHeat14.add((BigDecimal) ((Map<String, Object>) map
					.get("PREHEAT")).get("EMUSAGE_14"));
			preHeat15 = preHeat15.add((BigDecimal) ((Map<String, Object>) map
					.get("PREHEAT")).get("EMUSAGE_15"));
			preHeat16 = preHeat16.add((BigDecimal) ((Map<String, Object>) map
					.get("PREHEAT")).get("EMUSAGE_16"));
			preHeat17 = preHeat17.add((BigDecimal) ((Map<String, Object>) map
					.get("PREHEAT")).get("EMUSAGE_17"));
			preHeat18 = preHeat18.add((BigDecimal) ((Map<String, Object>) map
					.get("PREHEAT")).get("EMUSAGE_18"));
			preHeat19 = preHeat19.add((BigDecimal) ((Map<String, Object>) map
					.get("PREHEAT")).get("EMUSAGE_19"));
			preHeat20 = preHeat20.add((BigDecimal) ((Map<String, Object>) map
					.get("PREHEAT")).get("EMUSAGE_20"));
			preHeat21 = preHeat21.add((BigDecimal) ((Map<String, Object>) map
					.get("PREHEAT")).get("EMUSAGE_21"));
			preHeat22 = preHeat22.add((BigDecimal) ((Map<String, Object>) map
					.get("PREHEAT")).get("EMUSAGE_22"));
			preHeat23 = preHeat23.add((BigDecimal) ((Map<String, Object>) map
					.get("PREHEAT")).get("EMUSAGE_23"));
			preHeat24 = preHeat24.add((BigDecimal) ((Map<String, Object>) map
					.get("PREHEAT")).get("EMUSAGE_24"));
			preHeat25 = preHeat25.add((BigDecimal) ((Map<String, Object>) map
					.get("PREHEAT")).get("EMUSAGE_25"));
			preHeat26 = preHeat26.add((BigDecimal) ((Map<String, Object>) map
					.get("PREHEAT")).get("EMUSAGE_26"));
			preHeat27 = preHeat27.add((BigDecimal) ((Map<String, Object>) map
					.get("PREHEAT")).get("EMUSAGE_27"));
			preHeat28 = preHeat28.add((BigDecimal) ((Map<String, Object>) map
					.get("PREHEAT")).get("EMUSAGE_28"));
			preHeat29 = preHeat29.add((BigDecimal) ((Map<String, Object>) map
					.get("PREHEAT")).get("EMUSAGE_29"));
			preHeat30 = preHeat30.add((BigDecimal) ((Map<String, Object>) map
					.get("PREHEAT")).get("EMUSAGE_30"));
			preHeat31 = preHeat31.add((BigDecimal) ((Map<String, Object>) map
					.get("PREHEAT")).get("EMUSAGE_31"));

			preLight = preLight.add((BigDecimal) ((Map<String, Object>) map
					.get("PRELIGHT")).get("EMUSAGE"));
			preLight01 = preLight01.add((BigDecimal) ((Map<String, Object>) map
					.get("PRELIGHT")).get("EMUSAGE_01"));
			preLight02 = preLight02.add((BigDecimal) ((Map<String, Object>) map
					.get("PRELIGHT")).get("EMUSAGE_02"));
			preLight03 = preLight03.add((BigDecimal) ((Map<String, Object>) map
					.get("PRELIGHT")).get("EMUSAGE_03"));
			preLight04 = preLight04.add((BigDecimal) ((Map<String, Object>) map
					.get("PRELIGHT")).get("EMUSAGE_04"));
			preLight05 = preLight05.add((BigDecimal) ((Map<String, Object>) map
					.get("PRELIGHT")).get("EMUSAGE_05"));
			preLight06 = preLight06.add((BigDecimal) ((Map<String, Object>) map
					.get("PRELIGHT")).get("EMUSAGE_06"));
			preLight07 = preLight07.add((BigDecimal) ((Map<String, Object>) map
					.get("PRELIGHT")).get("EMUSAGE_07"));
			preLight08 = preLight08.add((BigDecimal) ((Map<String, Object>) map
					.get("PRELIGHT")).get("EMUSAGE_08"));
			preLight09 = preLight09.add((BigDecimal) ((Map<String, Object>) map
					.get("PRELIGHT")).get("EMUSAGE_09"));
			preLight10 = preLight10.add((BigDecimal) ((Map<String, Object>) map
					.get("PRELIGHT")).get("EMUSAGE_10"));
			preLight11 = preLight11.add((BigDecimal) ((Map<String, Object>) map
					.get("PRELIGHT")).get("EMUSAGE_11"));
			preLight12 = preLight12.add((BigDecimal) ((Map<String, Object>) map
					.get("PRELIGHT")).get("EMUSAGE_12"));
			preLight13 = preLight13.add((BigDecimal) ((Map<String, Object>) map
					.get("PRELIGHT")).get("EMUSAGE_13"));
			preLight14 = preLight14.add((BigDecimal) ((Map<String, Object>) map
					.get("PRELIGHT")).get("EMUSAGE_14"));
			preLight15 = preLight15.add((BigDecimal) ((Map<String, Object>) map
					.get("PRELIGHT")).get("EMUSAGE_15"));
			preLight16 = preLight16.add((BigDecimal) ((Map<String, Object>) map
					.get("PRELIGHT")).get("EMUSAGE_16"));
			preLight17 = preLight17.add((BigDecimal) ((Map<String, Object>) map
					.get("PRELIGHT")).get("EMUSAGE_17"));
			preLight18 = preLight18.add((BigDecimal) ((Map<String, Object>) map
					.get("PRELIGHT")).get("EMUSAGE_18"));
			preLight19 = preLight19.add((BigDecimal) ((Map<String, Object>) map
					.get("PRELIGHT")).get("EMUSAGE_19"));
			preLight20 = preLight20.add((BigDecimal) ((Map<String, Object>) map
					.get("PRELIGHT")).get("EMUSAGE_20"));
			preLight21 = preLight21.add((BigDecimal) ((Map<String, Object>) map
					.get("PRELIGHT")).get("EMUSAGE_21"));
			preLight22 = preLight22.add((BigDecimal) ((Map<String, Object>) map
					.get("PRELIGHT")).get("EMUSAGE_22"));
			preLight23 = preLight23.add((BigDecimal) ((Map<String, Object>) map
					.get("PRELIGHT")).get("EMUSAGE_23"));
			preLight24 = preLight24.add((BigDecimal) ((Map<String, Object>) map
					.get("PRELIGHT")).get("EMUSAGE_24"));
			preLight25 = preLight25.add((BigDecimal) ((Map<String, Object>) map
					.get("PRELIGHT")).get("EMUSAGE_25"));
			preLight26 = preLight26.add((BigDecimal) ((Map<String, Object>) map
					.get("PRELIGHT")).get("EMUSAGE_26"));
			preLight27 = preLight27.add((BigDecimal) ((Map<String, Object>) map
					.get("PRELIGHT")).get("EMUSAGE_27"));
			preLight28 = preLight28.add((BigDecimal) ((Map<String, Object>) map
					.get("PRELIGHT")).get("EMUSAGE_28"));
			preLight29 = preLight29.add((BigDecimal) ((Map<String, Object>) map
					.get("PRELIGHT")).get("EMUSAGE_29"));
			preLight30 = preLight30.add((BigDecimal) ((Map<String, Object>) map
					.get("PRELIGHT")).get("EMUSAGE_30"));
			preLight31 = preLight31.add((BigDecimal) ((Map<String, Object>) map
					.get("PRELIGHT")).get("EMUSAGE_31"));

			cur00 = cur00 + getDoubleValue(map.get("CURVALUE00"));
			cur01 = cur01 + getDoubleValue(map.get("CURVALUE01"));
			cur02 = cur02 + getDoubleValue(map.get("CURVALUE02"));
			cur03 = cur03 + getDoubleValue(map.get("CURVALUE03"));
			cur04 = cur04 + getDoubleValue(map.get("CURVALUE04"));
			cur05 = cur05 + getDoubleValue(map.get("CURVALUE05"));
			cur06 = cur06 + getDoubleValue(map.get("CURVALUE06"));
			cur07 = cur07 + getDoubleValue(map.get("CURVALUE07"));
			cur08 = cur08 + getDoubleValue(map.get("CURVALUE08"));
			cur09 = cur09 + getDoubleValue(map.get("CURVALUE09"));
			cur10 = cur10 + getDoubleValue(map.get("CURVALUE10"));
			cur11 = cur11 + getDoubleValue(map.get("CURVALUE11"));
			cur12 = cur12 + getDoubleValue(map.get("CURVALUE12"));
			cur13 = cur13 + getDoubleValue(map.get("CURVALUE13"));
			cur14 = cur14 + getDoubleValue(map.get("CURVALUE14"));
			cur15 = cur15 + getDoubleValue(map.get("CURVALUE15"));
			cur16 = cur16 + getDoubleValue(map.get("CURVALUE16"));
			cur17 = cur17 + getDoubleValue(map.get("CURVALUE17"));
			cur18 = cur18 + getDoubleValue(map.get("CURVALUE18"));
			cur19 = cur19 + getDoubleValue(map.get("CURVALUE19"));
			cur20 = cur20 + getDoubleValue(map.get("CURVALUE20"));
			cur21 = cur21 + getDoubleValue(map.get("CURVALUE21"));
			cur22 = cur22 + getDoubleValue(map.get("CURVALUE22"));
			cur23 = cur23 + getDoubleValue(map.get("CURVALUE23"));
			cur23 = cur24 + getDoubleValue(map.get("CURVALUE24"));
			cur23 = cur25 + getDoubleValue(map.get("CURVALUE25"));
			cur23 = cur26 + getDoubleValue(map.get("CURVALUE26"));
			cur23 = cur27 + getDoubleValue(map.get("CURVALUE27"));
			cur23 = cur28 + getDoubleValue(map.get("CURVALUE28"));
			cur23 = cur29 + getDoubleValue(map.get("CURVALUE29"));
			cur23 = cur30 + getDoubleValue(map.get("CURVALUE30"));
			cur23 = cur31 + getDoubleValue(map.get("CURVALUE31"));
			curTotal = curTotal + getDoubleValue(map.get("CURTOTAL"));
			curHeat = curHeat.add((BigDecimal) ((Map<String, Object>) map
					.get("HEAT")).get("EMUSAGE"));

			curHeat01 = curHeat01.add((BigDecimal) ((Map<String, Object>) map
					.get("HEAT")).get("EMUSAGE_01"));
			curHeat02 = curHeat02.add((BigDecimal) ((Map<String, Object>) map
					.get("HEAT")).get("EMUSAGE_02"));
			curHeat03 = curHeat03.add((BigDecimal) ((Map<String, Object>) map
					.get("HEAT")).get("EMUSAGE_03"));
			curHeat04 = curHeat04.add((BigDecimal) ((Map<String, Object>) map
					.get("HEAT")).get("EMUSAGE_04"));
			curHeat05 = curHeat05.add((BigDecimal) ((Map<String, Object>) map
					.get("HEAT")).get("EMUSAGE_05"));
			curHeat06 = curHeat06.add((BigDecimal) ((Map<String, Object>) map
					.get("HEAT")).get("EMUSAGE_06"));
			curHeat07 = curHeat07.add((BigDecimal) ((Map<String, Object>) map
					.get("HEAT")).get("EMUSAGE_07"));
			curHeat08 = curHeat08.add((BigDecimal) ((Map<String, Object>) map
					.get("HEAT")).get("EMUSAGE_08"));
			curHeat09 = curHeat09.add((BigDecimal) ((Map<String, Object>) map
					.get("HEAT")).get("EMUSAGE_09"));
			curHeat10 = curHeat10.add((BigDecimal) ((Map<String, Object>) map
					.get("HEAT")).get("EMUSAGE_10"));
			curHeat11 = curHeat11.add((BigDecimal) ((Map<String, Object>) map
					.get("HEAT")).get("EMUSAGE_11"));
			curHeat12 = curHeat12.add((BigDecimal) ((Map<String, Object>) map
					.get("HEAT")).get("EMUSAGE_12"));
			curHeat13 = curHeat13.add((BigDecimal) ((Map<String, Object>) map
					.get("HEAT")).get("EMUSAGE_13"));
			curHeat14 = curHeat14.add((BigDecimal) ((Map<String, Object>) map
					.get("HEAT")).get("EMUSAGE_14"));
			curHeat15 = curHeat15.add((BigDecimal) ((Map<String, Object>) map
					.get("HEAT")).get("EMUSAGE_15"));
			curHeat16 = curHeat16.add((BigDecimal) ((Map<String, Object>) map
					.get("HEAT")).get("EMUSAGE_16"));
			curHeat17 = curHeat17.add((BigDecimal) ((Map<String, Object>) map
					.get("HEAT")).get("EMUSAGE_17"));
			curHeat18 = curHeat18.add((BigDecimal) ((Map<String, Object>) map
					.get("HEAT")).get("EMUSAGE_18"));
			curHeat19 = curHeat19.add((BigDecimal) ((Map<String, Object>) map
					.get("HEAT")).get("EMUSAGE_19"));
			curHeat20 = curHeat20.add((BigDecimal) ((Map<String, Object>) map
					.get("HEAT")).get("EMUSAGE_20"));
			curHeat21 = curHeat21.add((BigDecimal) ((Map<String, Object>) map
					.get("HEAT")).get("EMUSAGE_21"));
			curHeat22 = curHeat22.add((BigDecimal) ((Map<String, Object>) map
					.get("HEAT")).get("EMUSAGE_22"));
			curHeat23 = curHeat23.add((BigDecimal) ((Map<String, Object>) map
					.get("HEAT")).get("EMUSAGE_23"));
			curHeat24 = curHeat24.add((BigDecimal) ((Map<String, Object>) map
					.get("HEAT")).get("EMUSAGE_24"));
			curHeat25 = curHeat25.add((BigDecimal) ((Map<String, Object>) map
					.get("HEAT")).get("EMUSAGE_25"));
			curHeat26 = curHeat26.add((BigDecimal) ((Map<String, Object>) map
					.get("HEAT")).get("EMUSAGE_26"));
			curHeat27 = curHeat27.add((BigDecimal) ((Map<String, Object>) map
					.get("HEAT")).get("EMUSAGE_27"));
			curHeat28 = curHeat28.add((BigDecimal) ((Map<String, Object>) map
					.get("HEAT")).get("EMUSAGE_28"));
			curHeat29 = curHeat29.add((BigDecimal) ((Map<String, Object>) map
					.get("HEAT")).get("EMUSAGE_29"));
			curHeat30 = curHeat30.add((BigDecimal) ((Map<String, Object>) map
					.get("HEAT")).get("EMUSAGE_30"));
			curHeat31 = curHeat31.add((BigDecimal) ((Map<String, Object>) map
					.get("HEAT")).get("EMUSAGE_31"));

			curLight = curLight.add((BigDecimal) ((Map<String, Object>) map
					.get("LIGHT")).get("EMUSAGE"));

			curLight01 = curLight01.add((BigDecimal) ((Map<String, Object>) map
					.get("LIGHT")).get("EMUSAGE_01"));
			curLight02 = curLight02.add((BigDecimal) ((Map<String, Object>) map
					.get("LIGHT")).get("EMUSAGE_02"));
			curLight03 = curLight03.add((BigDecimal) ((Map<String, Object>) map
					.get("LIGHT")).get("EMUSAGE_03"));
			curLight04 = curLight04.add((BigDecimal) ((Map<String, Object>) map
					.get("LIGHT")).get("EMUSAGE_04"));
			curLight05 = curLight05.add((BigDecimal) ((Map<String, Object>) map
					.get("LIGHT")).get("EMUSAGE_05"));
			curLight06 = curLight06.add((BigDecimal) ((Map<String, Object>) map
					.get("LIGHT")).get("EMUSAGE_06"));
			curLight07 = curLight07.add((BigDecimal) ((Map<String, Object>) map
					.get("LIGHT")).get("EMUSAGE_07"));
			curLight08 = curLight08.add((BigDecimal) ((Map<String, Object>) map
					.get("LIGHT")).get("EMUSAGE_08"));
			curLight09 = curLight09.add((BigDecimal) ((Map<String, Object>) map
					.get("LIGHT")).get("EMUSAGE_09"));
			curLight10 = curLight10.add((BigDecimal) ((Map<String, Object>) map
					.get("LIGHT")).get("EMUSAGE_10"));
			curLight11 = curLight11.add((BigDecimal) ((Map<String, Object>) map
					.get("LIGHT")).get("EMUSAGE_11"));
			curLight12 = curLight12.add((BigDecimal) ((Map<String, Object>) map
					.get("LIGHT")).get("EMUSAGE_12"));
			curLight13 = curLight13.add((BigDecimal) ((Map<String, Object>) map
					.get("LIGHT")).get("EMUSAGE_13"));
			curLight14 = curLight14.add((BigDecimal) ((Map<String, Object>) map
					.get("LIGHT")).get("EMUSAGE_14"));
			curLight15 = curLight15.add((BigDecimal) ((Map<String, Object>) map
					.get("LIGHT")).get("EMUSAGE_15"));
			curLight16 = curLight16.add((BigDecimal) ((Map<String, Object>) map
					.get("LIGHT")).get("EMUSAGE_16"));
			curLight17 = curLight17.add((BigDecimal) ((Map<String, Object>) map
					.get("LIGHT")).get("EMUSAGE_17"));
			curLight18 = curLight18.add((BigDecimal) ((Map<String, Object>) map
					.get("LIGHT")).get("EMUSAGE_18"));
			curLight19 = curLight19.add((BigDecimal) ((Map<String, Object>) map
					.get("LIGHT")).get("EMUSAGE_19"));
			curLight20 = curLight20.add((BigDecimal) ((Map<String, Object>) map
					.get("LIGHT")).get("EMUSAGE_20"));
			curLight21 = curLight21.add((BigDecimal) ((Map<String, Object>) map
					.get("LIGHT")).get("EMUSAGE_21"));
			curLight22 = curLight22.add((BigDecimal) ((Map<String, Object>) map
					.get("LIGHT")).get("EMUSAGE_22"));
			curLight23 = curLight23.add((BigDecimal) ((Map<String, Object>) map
					.get("LIGHT")).get("EMUSAGE_23"));
			curLight24 = curLight24.add((BigDecimal) ((Map<String, Object>) map
					.get("LIGHT")).get("EMUSAGE_24"));
			curLight25 = curLight25.add((BigDecimal) ((Map<String, Object>) map
					.get("LIGHT")).get("EMUSAGE_25"));
			curLight26 = curLight26.add((BigDecimal) ((Map<String, Object>) map
					.get("LIGHT")).get("EMUSAGE_26"));
			curLight27 = curLight27.add((BigDecimal) ((Map<String, Object>) map
					.get("LIGHT")).get("EMUSAGE_27"));
			curLight28 = curLight28.add((BigDecimal) ((Map<String, Object>) map
					.get("LIGHT")).get("EMUSAGE_28"));
			curLight29 = curLight29.add((BigDecimal) ((Map<String, Object>) map
					.get("LIGHT")).get("EMUSAGE_29"));
			curLight30 = curLight30.add((BigDecimal) ((Map<String, Object>) map
					.get("LIGHT")).get("EMUSAGE_30"));
			curLight31 = curLight31.add((BigDecimal) ((Map<String, Object>) map
					.get("LIGHT")).get("EMUSAGE_31"));

		}

		retPreMap.put("EMUSAGE_01", pre01);
		retPreMap.put("EMUSAGE_02", pre02);
		retPreMap.put("EMUSAGE_03", pre03);
		retPreMap.put("EMUSAGE_04", pre04);
		retPreMap.put("EMUSAGE_05", pre05);
		retPreMap.put("EMUSAGE_06", pre06);
		retPreMap.put("EMUSAGE_07", pre07);
		retPreMap.put("EMUSAGE_08", pre08);
		retPreMap.put("EMUSAGE_09", pre09);
		retPreMap.put("EMUSAGE_10", pre10);
		retPreMap.put("EMUSAGE_11", pre11);
		retPreMap.put("EMUSAGE_12", pre12);
		retPreMap.put("EMUSAGE_13", pre13);
		retPreMap.put("EMUSAGE_14", pre14);
		retPreMap.put("EMUSAGE_15", pre15);
		retPreMap.put("EMUSAGE_16", pre16);
		retPreMap.put("EMUSAGE_17", pre17);
		retPreMap.put("EMUSAGE_18", pre18);
		retPreMap.put("EMUSAGE_19", pre19);
		retPreMap.put("EMUSAGE_20", pre20);
		retPreMap.put("EMUSAGE_21", pre21);
		retPreMap.put("EMUSAGE_22", pre22);
		retPreMap.put("EMUSAGE_23", pre23);
		retPreMap.put("EMUSAGE_24", pre24);
		retPreMap.put("EMUSAGE_25", pre25);
		retPreMap.put("EMUSAGE_26", pre26);
		retPreMap.put("EMUSAGE_27", pre27);
		retPreMap.put("EMUSAGE_28", pre28);
		retPreMap.put("EMUSAGE_29", pre29);
		retPreMap.put("EMUSAGE_30", pre30);
		retPreMap.put("EMUSAGE_31", pre31);
		retPreMap.put("TOTAL", preTotal);
		retPreMap.put("HEAT", preHeat);
		retPreMap.put("LIGHT", preLight);

		retDailyMap.put("EMUSAGE_00", cur00);
		retDailyMap.put("EMUSAGE_01", cur01);
		retDailyMap.put("EMUSAGE_02", cur02);
		retDailyMap.put("EMUSAGE_03", cur03);
		retDailyMap.put("EMUSAGE_04", cur04);
		retDailyMap.put("EMUSAGE_05", cur05);
		retDailyMap.put("EMUSAGE_06", cur06);
		retDailyMap.put("EMUSAGE_07", cur07);
		retDailyMap.put("EMUSAGE_08", cur08);
		retDailyMap.put("EMUSAGE_09", cur09);
		retDailyMap.put("EMUSAGE_10", cur10);
		retDailyMap.put("EMUSAGE_11", cur11);
		retDailyMap.put("EMUSAGE_12", cur12);
		retDailyMap.put("EMUSAGE_13", cur13);
		retDailyMap.put("EMUSAGE_14", cur14);
		retDailyMap.put("EMUSAGE_15", cur15);
		retDailyMap.put("EMUSAGE_16", cur16);
		retDailyMap.put("EMUSAGE_17", cur17);
		retDailyMap.put("EMUSAGE_18", cur18);
		retDailyMap.put("EMUSAGE_19", cur19);
		retDailyMap.put("EMUSAGE_20", cur20);
		retDailyMap.put("EMUSAGE_21", cur21);
		retDailyMap.put("EMUSAGE_22", cur22);
		retDailyMap.put("EMUSAGE_23", cur23);
		retDailyMap.put("EMUSAGE_24", cur24);
		retDailyMap.put("EMUSAGE_25", cur25);
		retDailyMap.put("EMUSAGE_26", cur26);
		retDailyMap.put("EMUSAGE_27", cur27);
		retDailyMap.put("EMUSAGE_28", cur28);
		retDailyMap.put("EMUSAGE_29", cur29);
		retDailyMap.put("EMUSAGE_30", cur30);
		retDailyMap.put("EMUSAGE_31", cur31);
		retDailyMap.put("TOTAL", curTotal);
		retDailyMap.put("HEAT", curHeat);

		retDailyHeatMap.put("EMUSAGE_01", curHeat01);
		retDailyHeatMap.put("EMUSAGE_02", curHeat02);
		retDailyHeatMap.put("EMUSAGE_03", curHeat03);
		retDailyHeatMap.put("EMUSAGE_04", curHeat04);
		retDailyHeatMap.put("EMUSAGE_05", curHeat05);
		retDailyHeatMap.put("EMUSAGE_06", curHeat06);
		retDailyHeatMap.put("EMUSAGE_07", curHeat07);
		retDailyHeatMap.put("EMUSAGE_08", curHeat08);
		retDailyHeatMap.put("EMUSAGE_09", curHeat09);
		retDailyHeatMap.put("EMUSAGE_10", curHeat10);
		retDailyHeatMap.put("EMUSAGE_11", curHeat11);
		retDailyHeatMap.put("EMUSAGE_12", curHeat12);
		retDailyHeatMap.put("EMUSAGE_13", curHeat13);
		retDailyHeatMap.put("EMUSAGE_14", curHeat14);
		retDailyHeatMap.put("EMUSAGE_15", curHeat15);
		retDailyHeatMap.put("EMUSAGE_16", curHeat16);
		retDailyHeatMap.put("EMUSAGE_17", curHeat17);
		retDailyHeatMap.put("EMUSAGE_18", curHeat18);
		retDailyHeatMap.put("EMUSAGE_19", curHeat19);
		retDailyHeatMap.put("EMUSAGE_20", curHeat20);
		retDailyHeatMap.put("EMUSAGE_21", curHeat21);
		retDailyHeatMap.put("EMUSAGE_22", curHeat22);
		retDailyHeatMap.put("EMUSAGE_23", curHeat23);
		retDailyHeatMap.put("EMUSAGE_24", curHeat24);
		retDailyHeatMap.put("EMUSAGE_25", curHeat25);
		retDailyHeatMap.put("EMUSAGE_26", curHeat26);
		retDailyHeatMap.put("EMUSAGE_27", curHeat27);
		retDailyHeatMap.put("EMUSAGE_28", curHeat28);
		retDailyHeatMap.put("EMUSAGE_29", curHeat29);
		retDailyHeatMap.put("EMUSAGE_30", curHeat30);
		retDailyHeatMap.put("EMUSAGE_31", curHeat31);

		retDailyMap.put("LIGHT", curLight);

		retDailyLightMap.put("EMUSAGE_01", curLight01);
		retDailyLightMap.put("EMUSAGE_02", curLight02);
		retDailyLightMap.put("EMUSAGE_03", curLight03);
		retDailyLightMap.put("EMUSAGE_04", curLight04);
		retDailyLightMap.put("EMUSAGE_05", curLight05);
		retDailyLightMap.put("EMUSAGE_06", curLight06);
		retDailyLightMap.put("EMUSAGE_07", curLight07);
		retDailyLightMap.put("EMUSAGE_08", curLight08);
		retDailyLightMap.put("EMUSAGE_09", curLight09);
		retDailyLightMap.put("EMUSAGE_10", curLight10);
		retDailyLightMap.put("EMUSAGE_11", curLight11);
		retDailyLightMap.put("EMUSAGE_12", curLight12);
		retDailyLightMap.put("EMUSAGE_13", curLight13);
		retDailyLightMap.put("EMUSAGE_14", curLight14);
		retDailyLightMap.put("EMUSAGE_15", curLight15);
		retDailyLightMap.put("EMUSAGE_16", curLight16);
		retDailyLightMap.put("EMUSAGE_17", curLight17);
		retDailyLightMap.put("EMUSAGE_18", curLight18);
		retDailyLightMap.put("EMUSAGE_19", curLight19);
		retDailyLightMap.put("EMUSAGE_20", curLight20);
		retDailyLightMap.put("EMUSAGE_21", curLight21);
		retDailyLightMap.put("EMUSAGE_22", curLight22);
		retDailyLightMap.put("EMUSAGE_23", curLight23);
		retDailyLightMap.put("EMUSAGE_24", curLight24);
		retDailyLightMap.put("EMUSAGE_25", curLight25);
		retDailyLightMap.put("EMUSAGE_26", curLight26);
		retDailyLightMap.put("EMUSAGE_27", curLight27);
		retDailyLightMap.put("EMUSAGE_28", curLight28);
		retDailyLightMap.put("EMUSAGE_29", curLight29);
		retDailyLightMap.put("EMUSAGE_30", curLight30);
		retDailyLightMap.put("EMUSAGE_31", curLight31);
		retMap.put("PRE", retPreMap);
		retMap.put("PREHEAT", retPreHeatMap);
		retMap.put("PRELIGHT", retPreLightMap);
		retMap.put("CUR", retDailyMap);
		retMap.put("CURHEAT", retDailyHeatMap);
		retMap.put("CURLIGHT", retDailyLightMap);

		return retMap;

	}

	private Map<String, Object> getPeriodMap(Map<String, Object> usageMap,
			String dateType, BigDecimal emExpect, BigDecimal wmExpect,
			BigDecimal gmExpect, BigDecimal emGoal, BigDecimal wmGoal,
			BigDecimal gmGoal) {
		Map<String, Object> retMap = new HashMap<String, Object>();

		Integer tfcast = getCharge(MeterType.EnergyMeter.getServiceType(),
				dateType, emExpect)
				+ getWMCharge(MeterType.WaterMeter.getServiceType(), dateType,
						wmExpect)
				+ getCharge(MeterType.GasMeter.getServiceType(), dateType,
						gmExpect);
		Integer tGoal = getCharge(MeterType.EnergyMeter.getServiceType(),
				dateType, emGoal)
				+ getCharge(MeterType.WaterMeter.getServiceType(), dateType,
						wmGoal)
				+ getCharge(MeterType.GasMeter.getServiceType(), dateType,
						gmGoal);

		Integer tUsage = getCharge(MeterType.EnergyMeter.getServiceType(),
				dateType, getUsage(usageMap, "EM"))
				+ getWMCharge(MeterType.WaterMeter.getServiceType(), dateType,
						getUsage(usageMap, "WM"))
				+ getCharge(MeterType.GasMeter.getServiceType(), dateType,
						getUsage(usageMap, "GM"));
		BigDecimal tCo2 = getCo2(usageMap, "EM").add(
				getCo2(usageMap, "WM").add(getCo2(usageMap, "GM")));
		BigDecimal usage = getUsage(usageMap, "EM").add(
				getUsage(usageMap, "WM")).add(getUsage(usageMap, "GM"));

		Map<String, Object> totalMap = getPanelMap(tfcast, tGoal, tUsage, tCo2,
				usage);

		Integer efcast = getCharge(MeterType.EnergyMeter.getServiceType(),
				dateType, emExpect);
		Integer eGoal = getCharge(MeterType.EnergyMeter.getServiceType(),
				dateType, emGoal);

		Integer eUsage = getCharge(MeterType.EnergyMeter.getServiceType(),
				dateType, getUsage(usageMap, "EM"));

		Map<String, Object> emMap = getPanelMap(efcast, eGoal, eUsage, getCo2(
				usageMap, "EM"), getUsage(usageMap, "EM"));

		Integer wfcast = getWMCharge(MeterType.WaterMeter.getServiceType(),
				dateType, wmExpect);
		Integer wGoal = getCharge(MeterType.WaterMeter.getServiceType(),
				dateType, wmGoal);

		Integer wUsage = getWMCharge(MeterType.WaterMeter.getServiceType(),
				dateType, getUsage(usageMap, "WM"));

		Map<String, Object> wmMap = getPanelMap(wfcast, wGoal, wUsage, getCo2(
				usageMap, "WM"), getUsage(usageMap, "WM"));

		Integer gfcast = getCharge(MeterType.GasMeter.getServiceType(),
				dateType, gmExpect);
		Integer gGoal = getCharge(MeterType.GasMeter.getServiceType(),
				dateType, gmGoal);

		Integer gUsage = getCharge(MeterType.GasMeter.getServiceType(),
				dateType, getUsage(usageMap, "GM"));

		Map<String, Object> gmMap = getPanelMap(gfcast, gGoal, gUsage, getCo2(
				usageMap, "GM"), getUsage(usageMap, "GM"));

		retMap.put("TOTAL", totalMap);
		retMap.put("EM", emMap);
		retMap.put("WM", wmMap);
		retMap.put("GM", gmMap);
		return retMap;
	}

	private Map<String, Object> getPanelMap(Integer forecast, Integer goal,
			Integer usageBill, BigDecimal co2, BigDecimal usage) {
		Map<String, Object> totalMap = new HashMap<String, Object>();

		// forecast:7355:goal:1386usageBill:2552:co2:13.6:usage:23.2
		// totalMap:{TOTALGOAL=1386, FORECAST=1386, USAGEBILL=1386,
		// FORECASTGRAPH=5969, USAGEGRAPH=1166, CO2=13.6, GOAL=1386, USAGE=23.2}

		Integer tfcast = forecast;
		Integer tGoal = goal;

		Integer tUsage = usageBill;
		// Integer tfGraph = getGraphValue(tGoal, tfcast);
		//
		// Integer tuGraph = getGraphValue(tGoal, tUsage);

		Integer tfGraphValue = 0;
		Integer tuGraphValue = 0;

		// if (tfGraph > 0) {
		// tfcast = tGoal;
		// tfGraphValue = tfGraph;
		// }
		//
		// if (tuGraph > 0) {
		// tUsage = tGoal;
		// tuGraphValue = tuGraph;
		// }

		if (usageBill >= forecast) {
			tfcast = 0;
			if (usageBill >= goal) {
				tGoal = usageBill - goal;
				tUsage = goal;
			} else {
				tGoal = 0;
			}

		} else {
			if (forecast >= goal) {
				if (usageBill >= goal) {
					tfcast = 0;
					tGoal = forecast - goal;
					tUsage = goal;
				} else {
					tfcast = goal - usageBill;
					tGoal = forecast - goal;
				}

			} else {
				tGoal = 0;
				tfcast = tfcast - tUsage;
			}

		}

		if (usageBill == 0 && tfcast == 0) {
			tfcast = (new BigDecimal(goal * 0.75)).intValue();
			forecast = (new BigDecimal(goal * 0.75)).intValue();
		}

		totalMap.put("FORECAST", tfcast);
		totalMap.put("FORECASTGRAPH", tfGraphValue);
		totalMap.put("GOAL", tGoal);
		totalMap.put("TOTALGOAL", goal);
		totalMap.put("USAGEBILL", tUsage);
		totalMap.put("ORIUSAGEBILL", usageBill);
		totalMap.put("ORIFORECAST", forecast);
		totalMap.put("USAGEGRAPH", tuGraphValue);
		totalMap.put("YMAX", goal * 1.3);

		totalMap.put("CO2", co2);
		totalMap.put("USAGE", usage);

		// System.out.println("forecast:"+ forecast+":goal:"+ goal
		// +"usageBill:"+usageBill+":co2:"+co2+":usage:"+ usage);
		// System.out.println("totalMap:"+totalMap);
		return totalMap;
	}

	private Map<String, Object> getPanelListMap(Integer forecast, Integer goal,
			Integer usageBill, BigDecimal co2, BigDecimal usage, String name,
			Integer rank, Integer preRank, Integer reduct) {
		Map<String, Object> totalMap = new HashMap<String, Object>();

		Integer tfcast = forecast;
		Integer tGoal = goal;
		Integer tUsage = usageBill;

		Integer tfGraphValue = 0;
		Integer tuGraphValue = 0;
		// System.out.println("###########3tfcast:"+tfcast+":tGoal:"+tGoal+":tUsage:"+tUsage);
		Integer addUsage = 0;

		if (usageBill >= forecast) {
			tfcast = 0;
			if (usageBill >= goal) {
				tGoal = usageBill - goal;
				tUsage = goal;
			} else {
				tGoal = 0;
			}

		} else {
			if (forecast >= goal) {
				if (usageBill >= goal) {
					tfcast = 0;
					tGoal = forecast - goal;
					tUsage = goal;
				} else {
					tfcast = goal - usageBill;
					tGoal = forecast - goal;
				}

			} else {
				tGoal = 0;
				tfcast = tfcast - tUsage;
			}

		}

		// System.out.println("@@@@@@@@@@@@@@@tfcast:"+tfcast+":tGoal:"+tGoal+":tUsage:"+tUsage);

		totalMap.put("FORECAST", tfcast);
		totalMap.put("GOAL", tGoal);
		totalMap.put("USAGEBILL", tUsage);
		totalMap.put("YMAX", goal * 1.3);
		totalMap.put("TREND", goal);

		totalMap.put("NAME", name.substring(2, name.length()));
		totalMap.put("RANK", rank);
		totalMap.put("PRERANK", preRank);
		totalMap.put("upDown", "");
		if (rank < preRank) {
			totalMap.put("upDown",
					"@Embed(source='assets/monitor/triangle_red.png')");
		}

		if (rank > preRank) {
			totalMap.put("upDown",
					"@Embed(source='assets/monitor/triangle_blue.png')");
		}
		totalMap.put("upDown", preRank);

		totalMap.put("REDUCT", goal - forecast);
		// System.out.println("totalMap:"+totalMap);
		return totalMap;
	}

	private Integer getGraphValue(Integer goal, Integer value) {
		if (goal >= value) {
			return 0;
		} else {
			return value - goal;
		}

	}

	private Integer getWMCharge(String meterType, String dateType, Object usage) {

		BemsStatisticUtil bemsUtil = new BemsStatisticUtil();
		Map<String, Object> chargeMap = new HashMap<String, Object>();
		chargeMap.put("serviceType", meterType);
		chargeMap.put("dateType", DateType.DAILY.getCode());
		if (usage instanceof BigDecimal) {
			chargeMap.put("usage", ((BigDecimal) usage).doubleValue());
		} else {
			chargeMap.put("usage", usage);
		}
		chargeMap.put("period", 1);
		if (dateType.equals(DateType.WEEKLY.getCode())) {
			chargeMap.put("period", 7);

		}
		return new BigDecimal((Double) bemsUtil.getUsageCharge(chargeMap))
				.intValue() * 2;

	}

	private Integer getCharge(String meterType, String dateType, Object usage) {

		BemsStatisticUtil bemsUtil = new BemsStatisticUtil();
		Map<String, Object> chargeMap = new HashMap<String, Object>();
		chargeMap.put("serviceType", meterType);
		chargeMap.put("dateType", DateType.DAILY.getCode());
		if (usage instanceof BigDecimal) {
			chargeMap.put("usage", ((BigDecimal) usage).doubleValue());
		} else {
			chargeMap.put("usage", usage);
		}
		chargeMap.put("period", 1);
		if (dateType.equals(DateType.WEEKLY.getCode())) {
			chargeMap.put("period", 7);

		}
		return new BigDecimal((Double) bemsUtil.getUsageCharge(chargeMap))
				.intValue();

	}

	private BigDecimal getUsage(Map<String, Object> usage, String energyType) {

		if (usage.get(energyType + "USAGE") != null) {
			return (BigDecimal) usage.get(energyType + "USAGE");
		}

		return new BigDecimal(0);

	}

	private BigDecimal getUsage(String param, Map<String, Object> usage) {

		// System.out.println("getUsage TOTAL:"+usage.get("TOTAL"));
		if (usage.get(param) != null) {
			if (usage.get(param) instanceof BigDecimal)
				return (BigDecimal) usage.get(param);
			else
				return new BigDecimal((Double) usage.get(param)).setScale(3,
						BigDecimal.ROUND_DOWN);
		}

		return new BigDecimal(0);

	}

	private BigDecimal getCo2(Map<String, Object> usage, String energyType) {

		if (usage.get(energyType + "CO2USAGE") != null) {
			return (BigDecimal) usage.get(energyType + "CO2USAGE");
		}

		return new BigDecimal(0);

	}

	private BigDecimal getDailyExpect(Map<String, Object> preDaily,
			Map<String, Object> daily, String energyType) {

		Double[] expectDaily = new Double[24];
		Double[] preExpectDaily = new Double[24];

		for (int i = 23; i > -1; i--) {
			String hh = TimeUtil.to2Digit(i);

			preExpectDaily[i] = preDaily.get(energyType + "USAGE_" + hh) == null ? 0d
					: (Double) getConvertDoubleValue(preDaily.get(energyType
							+ "USAGE_" + hh));
			expectDaily[i] = (Double) getConvertDoubleValue(daily
					.get(energyType + "USAGE_" + hh));

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

	private Double getConvertDoubleValue(Object value) {

		if (value instanceof BigDecimal) {
			BigDecimal bValue = (BigDecimal) value;
			return bValue.doubleValue();
		}

		return (Double) value;
	}

	private BigDecimal getWeeklyExpect(Map<String, Object> preWeekly,
			Map<String, Object> weekly, String energyType) {

		// System.out.println("getWeeklyExpect preWeekly:" + preWeekly);
		// System.out.println("getWeeklyExpect weekly:" + weekly);
		Double[] expectWeekly = new Double[7];
		Double[] preExpectWeekly = new Double[7];
		for (int i = 7; i > 0; i--) {
			String hh = TimeUtil.to2Digit(i);
			preExpectWeekly[i - 1] = preWeekly.get(energyType + "USAGE_" + hh) == null ? 0d
					: (Double) getConvertDoubleValue(preWeekly.get(energyType
							+ "USAGE_" + hh));

			// System.out.println("weekly.get(energyType + USAGE_" + hh + "):"
			// + weekly.get(energyType + "USAGE_" + hh));
			expectWeekly[i - 1] = (Double) getConvertDoubleValue(weekly
					.get(energyType + "USAGE_" + hh));

			// System.out.println("expectWeekly[" + (i - 1) + "]:"
			// + expectWeekly[i - 1]);
		}

		// for (int i = 0; i < 7; i++) {
		// System.out.println("PRE[" + i + "]:" + preExpectWeekly[i]
		// + ":CURR:" + expectWeekly[i]);
		//
		// }

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

	private BigDecimal getMonthlyExpect(Map<String, Object> preMonthly,
			Map<String, Object> monthly, String energyType) {

		Double[] expectMonthly = new Double[31];
		Double[] preExpectMonthly = new Double[31];
		for (int i = 31; i > 0; i--) {
			String hh = TimeUtil.to2Digit(i);
			preExpectMonthly[i - 1] = preMonthly
					.get(energyType + "USAGE_" + hh) == null ? 0d
					: (Double) getConvertDoubleValue(preMonthly.get(energyType
							+ "USAGE_" + hh));
			expectMonthly[i - 1] = (Double) getConvertDoubleValue(monthly
					.get(energyType + "USAGE_" + hh));

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

	private BigDecimal getGoal(Double avg, Double savingGoal) {

		BigDecimal bAvg = new BigDecimal(avg);
		BigDecimal divide = new BigDecimal(100 - savingGoal);
		BigDecimal divideParam = new BigDecimal(100);
		return bAvg.multiply(divide.divide(divideParam)).setScale(3,
				BigDecimal.ROUND_DOWN);

	}

	private BigDecimal getGoalUsage(String supplierId, String locationId,
			String searchDateType, String energyType, String meterTypeCode,
			String date) {
		Map<String, String> condition = new HashMap<String, String>();
		condition.put("supplierId", supplierId);
		condition.put("locationId", locationId);
		condition.put("searchDateType", searchDateType);
		condition.put("energyType", energyType);
		condition.put("meterTypeCode", meterTypeCode);

		Map<String, Object> avg = energySavingGoal2Manager
				.getDayMonthRangeAvgByUsed(condition);

		// System.out.println("getGoalUsage avg:"+avg);
		List<EnergySavingGoal2> goalList = ktSeochoExhibitManager
				.getSavingGoal(searchDateType, energyType, date, Integer
						.parseInt(supplierId));

		// System.out.println("getGoalUsage goalList:"+goalList);
		BigDecimal goalUsage = new BigDecimal(0);
		if (!goalList.isEmpty()) {
			goalUsage = this.getGoal((Double) avg.get("averageUsage"), goalList
					.get(0).getSavingGoal());
		} else {
			goalUsage = this.getGoal((Double) avg.get("averageUsage"), 0d);
		}
		return goalUsage;
	}

	private Map<String, String> getWeather() {
		URL url;
		SAXBuilder builder = new SAXBuilder();
		Document doc = null;
		try {
			url = new URL("http://www.google.co.kr/ig/api?weather=seoul");
			doc = builder.build(new InputStreamReader(url.openConnection()
					.getInputStream(), "EUC-KR"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Element root = doc.getRootElement();
		Element weather = root.getChild("weather");
		Element current_conditions = weather.getChild("current_conditions");
		List list = current_conditions.getChildren();
		Map<String, String> result = new HashMap<String, String>();
		for (Object obj : list) {
			Element ele = (Element) obj;
			result.put(ele.getName(), ele.getAttributeValue("data"));
		}
		result.put("icon", imageConvert((String) result.get("icon")));
		return result;
	}

	private String imageConvert(String src) {

		if (src.endsWith("sunny.gif")) {
			return "sunny";
		} else if (src.endsWith("mostly_sunny.gif")) {

			return "mostly_sunny";
		} else if (src.endsWith("haze.gif")) {

			return "haze";
		} else if (src.endsWith("cloudy.gif")) {

			return "cloudy";
		} else if (src.endsWith("mostly_cloudy.gif")) {

			return "mostly_cloudy";
		} else if (src.endsWith("rain.gif")) {

			return "rain";
		} else if (src.endsWith("fog.gif")) {

			return "fog";
		} else if (src.endsWith("chance_of_rain.gif")) {

			return "chance_of_rain";
		} else if (src.endsWith("thunderstorm.gif")) {

			return "thunderstorm";
		} else if (src.endsWith("storm.gif")) {

			return "storm";
		} else if (src.endsWith("chance_of_storm.gif")) {

			return "chance_of_storm";
		} else if (src.endsWith("snow.gif")) {

			return "snow";
		} else if (src.endsWith("chance_of_snow.gif")) {

			return "chance_of_snow";
		} else if (src.endsWith("mist.gif")) {
			return "mist";
		} else {
			return "mostly_sunny";
		}
	}
}
