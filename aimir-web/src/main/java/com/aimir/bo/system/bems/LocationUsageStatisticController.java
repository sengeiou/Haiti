package com.aimir.bo.system.bems;

import java.io.InputStreamReader;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
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

import com.aimir.service.mvm.TemperatureHumidityManager;
import com.aimir.service.mvm.bean.TemperatureHumidityData;
import com.aimir.service.system.ExhibitionUsageManager;
import com.aimir.service.system.LocationUsageStatisticManager;
import com.aimir.util.BemsStatisticUtil;

@Controller
public class LocationUsageStatisticController {
	Log logger = LogFactory.getLog(LocationUsageStatisticController.class);

	@Autowired
	TemperatureHumidityManager temperatureHumidityManager;

	@Autowired
	LocationUsageStatisticManager locationUsageStatisticManager;

	@Autowired
	ExhibitionUsageManager exhibitionUsageManager;

	@RequestMapping(value = "/gadget/bems/locationUsageStatisticMax.do")
	public String locationUsageStatisticMaxView() {
		return "/gadget/bems/locationUsageStatisticMax";
	}

	@RequestMapping(value = "/gadget/bems/locationUsageStatisticMini.do")
	public String locationUsageStatisticMiniView() {
		return "/gadget/bems/locationUsageStatisticMini";
	}

	@RequestMapping(value = "/gadget/bems/searchTemperatureHumidity.do")
	public ModelAndView searchTemperatureHumidity(
			@RequestParam("searchDateType") String searchDateType,
			@RequestParam("date") String date,
			@RequestParam("locationId") int locationId) {
		ModelAndView mav = new ModelAndView("jsonView");
		TemperatureHumidityData usageData = temperatureHumidityManager
				.getUsageChartData(searchDateType, date, locationId);
		// System.out.println(usageData.toString());
		mav.addObject("usageData", usageData);

		return mav;

	}

	@RequestMapping(value = "/gadget/bems/weather.ex")
	public ModelAndView getWeather() {
		URL url;
		SAXBuilder builder = new SAXBuilder();
		Document doc = null;
		try {
			url = new URL("http://www.google.co.kr/ig/api?weather=seoul");
			doc = builder.build(new InputStreamReader(url.openConnection()
					.getInputStream(), "EUC-KR"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		Element root = doc.getRootElement();
		Element weather = root.getChild("weather");
		Element current_conditions = weather.getChild("current_conditions");
		List list = current_conditions.getChildren();
		HashMap<String, String> result = new HashMap<String, String>();
		for (Object obj : list) {
			Element ele = (Element) obj;
			result.put(ele.getName(), ele.getAttributeValue("data"));
		}
		Map<String, Object> usage = locationUsageStatisticManager
				.getExbitionUsage(1);

		ModelAndView mav = new ModelAndView("jsonView");
		mav.addObject("weather", result);
		mav.addObject("daily", usage.get("daily"));
		mav.addObject("weekly", usage.get("weekly"));
		mav.addObject("monthly", usage.get("monthly"));
		return mav;

	}
	@RequestMapping(value = "/gadget/bems/seochoExhibition.ex")
	public ModelAndView getSeochoExhibition(int locationId) {
		URL url;
		SAXBuilder builder = new SAXBuilder();
		Document doc = null;
		try {
			url = new URL("http://www.google.co.kr/ig/api?weather=seoul");
			doc = builder.build(new InputStreamReader(url.openConnection()
					.getInputStream(), "EUC-KR"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Element root = doc.getRootElement();
		Element weather = root.getChild("weather");
		Element current_conditions = weather.getChild("current_conditions");
		List list = current_conditions.getChildren();
		HashMap<String, String> result = new HashMap<String, String>();
		for (Object obj : list) {
			Element ele = (Element) obj;
			result.put(ele.getName(), ele.getAttributeValue("data"));
		}
		
		
		Map<String, Object> usage = locationUsageStatisticManager
				.getExbitionUsage(locationId);
        System.out.println("###########usage:"+usage);
		//result.put("icon", imageConvert((String) result.get("icon")));
		
		ModelAndView mav = new ModelAndView("jsonView");

		//mav.addObject("weather", result);
		// mav.addObject("total", usage.get("total"));

//		Map<String, Object> totalUsage = setLocale("total",
//				(Map<String, Object>) usage.get("total"));
		mav.addObject("weather", result);
		//mav.addObject("total", usage.get("total"));
		mav.addObject("daily", usage.get("daily"));
		mav.addObject("weekly", usage.get("weekly"));
		mav.addObject("monthly", usage.get("monthly"));
		Map<String,Object> day = (Map<String,Object>)usage.get("daily");
		Map<String,Object> week = (Map<String,Object>)usage.get("weekly");
		Map<String,Object> month = (Map<String,Object>)usage.get("monthly");
		
		
//		mav.addObject("total", usage.get("total"));
//		mav.addObject("position0", totalUsage);
//		mav.addObject("position1", setLocale("position0",
//				(Map<String, Object>) usage.get("position0")));
//		mav.addObject("position2", setLocale("position1",
//				(Map<String, Object>) usage.get("position1")));
//		mav.addObject("position3", setLocale("position2",
//				(Map<String, Object>) usage.get("position2")));
		// System.out.println("usage.get(position1):"+usage.get("position1"));
		// System.out.println("usage.get(position2):"+usage.get("position2"));
		// System.out.println("usage.get(position3):"+usage.get("position3"));
		System.out.println("mav:" + mav);
		return mav;

	}

	@RequestMapping(value = "/gadget/bems/locationExhibition.ex")
	public ModelAndView getLocationExhibition() {
		URL url;
		SAXBuilder builder = new SAXBuilder();
		Document doc = null;
		try {
			url = new URL("http://www.google.co.kr/ig/api?weather=seoul");
			doc = builder.build(new InputStreamReader(url.openConnection()
					.getInputStream(), "EUC-KR"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Element root = doc.getRootElement();
		Element weather = root.getChild("weather");
		Element current_conditions = weather.getChild("current_conditions");
		List list = current_conditions.getChildren();
		HashMap<String, String> result = new HashMap<String, String>();
		for (Object obj : list) {
			Element ele = (Element) obj;
			result.put(ele.getName(), ele.getAttributeValue("data"));
		}
		
		Map<String, Object> usage = locationUsageStatisticManager
				.getLocationExbitionUsage();

		result.put("icon", imageConvert((String) result.get("icon")));
		
		ModelAndView mav = new ModelAndView("jsonView");

		mav.addObject("weather", result);
		// mav.addObject("total", usage.get("total"));

		Map<String, Object> totalUsage = setLocale("total",
				(Map<String, Object>) usage.get("total"));
		mav.addObject("total", totalUsage);
		mav.addObject("position0", totalUsage);
		mav.addObject("position1", setLocale("position0",
				(Map<String, Object>) usage.get("position0")));
		mav.addObject("position2", setLocale("position1",
				(Map<String, Object>) usage.get("position1")));
		mav.addObject("position3", setLocale("position2",
				(Map<String, Object>) usage.get("position2")));
		// System.out.println("usage.get(position1):"+usage.get("position1"));
		// System.out.println("usage.get(position2):"+usage.get("position2"));
		// System.out.println("usage.get(position3):"+usage.get("position3"));
		System.out.println("mav:" + mav);
		return mav;

	}

	private String formatting(Double value) {

		NumberFormat formatter = new DecimalFormat("###,###,###,###");

		return formatter.format(value.intValue());
	}

	private String formatting(int value) {

		NumberFormat formatter = new DecimalFormat("###,###,###,###");

		return formatter.format(value);
	}

	private Map<String, Object> setLocale(String pos, Map<String, Object> usage) {
		double scale = 1;
		if ("position0".equals(pos)) {
			scale = 0.44;
		} else if ("position1".equals(pos)) {
			scale = 0.04;
		} else if ("position2".equals(pos)) {
			scale = 0.54;
		}

		NumberFormat formatter = new DecimalFormat("###,###,###,###");
		Map<String, Object> daily = (Map<String, Object>) usage.get("daily");
		Integer dGoalBill = (Integer) daily.get("EmGoalBillUsage");
		Integer dGoal = (Integer) daily.get("EmGoal");
		Integer dBill = (Integer) daily.get("EmBillUsage");

		Double dg=dGoal * scale;
		
		daily.put("EmGoalBillUsage", formatting(dGoalBill * scale));
		daily.put("EmGoal",dg.intValue());
		daily.put("EmBillUsage", formatting(dBill));

		Map<String, Object> weekly = (Map<String, Object>) usage.get("weekly");

		Integer wGoalBill = (Integer) weekly.get("EmGoalBillUsage");
		Integer wGoal = (Integer) weekly.get("EmGoal");
		Integer wBill = (Integer) weekly.get("EmBillUsage");
		Double wg=wGoal * scale;
		weekly.put("EmGoalBillUsage", formatting(wGoalBill * scale));
		weekly.put("EmGoal",wg.intValue() );
		weekly.put("EmBillUsage", formatting(wBill));

		Map<String, Object> monthly = (Map<String, Object>) usage
				.get("monthly");
		
		Integer mGoalBill = (Integer) monthly.get("EmGoalBillUsage");
		Integer mGoal = (Integer) monthly.get("EmGoal");
		Integer mBill = (Integer) monthly.get("EmBillUsage");

		Double mg=mGoal * scale;
		monthly.put("EmGoalBillUsage", formatting(mGoalBill * scale));
		monthly.put("EmGoal",mg.intValue() );
		monthly.put("EmBillUsage", formatting(mBill));
		
		return usage;
	}

	private Map<String, String> getGoogleWeather() {
		URL url;
		SAXBuilder builder = new SAXBuilder();
		Document doc = null;
		try {
			url = new URL("http://www.google.co.kr/ig/api?weather=jeju");
			doc = builder.build(new InputStreamReader(url.openConnection()
					.getInputStream(), "EUC-KR"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		Element root = doc.getRootElement();
		Element weather = root.getChild("weather");
		Element current_conditions = weather.getChild("current_conditions");
		List list = null;
		Map<String, String> result = new HashMap<String, String>();
		if (current_conditions != null) {
			list = current_conditions.getChildren();

			for (Object obj : list) {
				Element ele = (Element) obj;
				result.put(ele.getName(), ele.getAttributeValue("data"));
			}
		} else {
			result.put("icon", "/ig/images/weather/sunny.gif");
			result.put("condition", "맑음");
			result.put("temp_c", "0");
			result.put("humidity", "0");
			result.put("wind_condition", "0");

		}

		return result;
	}

	private Map<String, String> getExhibitParam() {

		SAXBuilder builder = new SAXBuilder();
		Document doc = null;
		try {
			BemsStatisticUtil bemsUtil = new BemsStatisticUtil();
			doc = builder.build(new InputStreamReader(bemsUtil
					.getExhibitionParamStream(), "UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		Element root = doc.getRootElement();
		Element position0 = root.getChild("position0");
		Element position1 = root.getChild("position1");
		Element position2 = root.getChild("position2");
		Element goal = root.getChild("goal");
		Map<String, String> result = new HashMap<String, String>();
		result.put("name0", position0.getAttributeValue("data"));
		result.put("name1", position1.getAttributeValue("data"));
		result.put("name2", position2.getAttributeValue("data"));
		result.put("channel0", position0.getAttributeValue("channel"));
		result.put("channel1", position1.getAttributeValue("channel"));
		result.put("channel2", position2.getAttributeValue("channel"));
		result.put("goal", goal.getAttributeValue("data"));

		return result;
	}

	@RequestMapping(value = "/gadget/bems/totalMeteringData.ex")
	public ModelAndView getTotalMeteringData() {
		Map<String, String> result = new HashMap<String, String>();				
		
		Map<String, String> params = getExhibitParam();

		int goal = Integer.parseInt(params.get("goal"));
		int channel0 = Integer.parseInt(params.get("channel0"));
		int channel1 = Integer.parseInt(params.get("channel1"));
		int channel2 = Integer.parseInt(params.get("channel2"));

		Map<String, Object> usage = exhibitionUsageManager
				.getLocationExbitionUsage(goal, channel0, channel1, channel2);		

		ModelAndView mav = new ModelAndView("jsonView");

		
		// mav.addObject("total", usage.get("total"));
		mav.addObject("daily", usage.get("daily"));
		mav.addObject("weekly", usage.get("weekly"));
		mav.addObject("monthly", usage.get("monthly"));

		mav.addObject("params", params);

		System.out.println("mav:" + mav);
		return mav;

	}
	
	@RequestMapping(value = "/gadget/bems/totalMeteringDataJeju.ex")
	public ModelAndView getTotalMeteringDataJeju() {
		Map<String, String> result = new HashMap<String, String>();				
		
		Map<String, String> params = getExhibitParam();

		int forwardGoal = Integer.parseInt(params.get("forwardGoal"));
		int reverseGoal = Integer.parseInt(params.get("reverseGoal"));
		int wmGoal = Integer.parseInt(params.get("wmGoal"));
		int co2Goal = Integer.parseInt(params.get("co2Goal"));
		int meteringGoal = Integer.parseInt(params.get("meteringGoal"));
		

		Map<String, Object> usage = exhibitionUsageManager
				.getLocationExbitionUsageJeju(forwardGoal,reverseGoal, wmGoal,co2Goal,meteringGoal);		

		ModelAndView mav = new ModelAndView("jsonView");

		
		// mav.addObject("total", usage.get("total"));
		mav.addObject("daily", usage.get("daily"));
		mav.addObject("weekly", usage.get("weekly"));
		mav.addObject("monthly", usage.get("monthly"));

		mav.addObject("params", params);

		System.out.println("mav:" + mav);
		return mav;

	}

	@RequestMapping(value = "/gadget/bems/locationExhibition_en.ex")
	public ModelAndView getLocationExhibition_en() {
		URL url;
		SAXBuilder builder = new SAXBuilder();
		Document doc = null;
		try {
			url = new URL(
					"http://www.google.com/ig/api?weather=seoul&;ie=utf-8&oe=utf-8&hl=en");
			doc = builder.build(new InputStreamReader(url.openConnection()
					.getInputStream(), "UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		Element root = doc.getRootElement();
		Element weather = root.getChild("weather");
		Element current_conditions = weather.getChild("current_conditions");
		List list = current_conditions.getChildren();
		HashMap<String, String> result = new HashMap<String, String>();
		for (Object obj : list) {
			Element ele = (Element) obj;
			result.put(ele.getName(), ele.getAttributeValue("data"));
		}
		Map<String, Object> usage = locationUsageStatisticManager
				.getLocationExbitionUsage();

		result.put("icon", imageConvert((String) result.get("icon")));

		ModelAndView mav = new ModelAndView("jsonView");

		mav.addObject("weather", result);
		mav.addObject("total", usage.get("total"));
		mav.addObject("position0", usage.get("position0"));
		mav.addObject("position1", usage.get("position1"));
		mav.addObject("position2", usage.get("position2"));
		return mav;

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
	
	@RequestMapping(value = "/gadget/bems/getSearchChartData.do")
	public ModelAndView getSearchChartData(@RequestParam("periodType") String periodType 
			,@RequestParam("date") String date 
			,@RequestParam("supplierId") Integer supplierId 
			,@RequestParam("locationId") Integer locationId) {
	
		Map<String, Object> params = new HashMap<String, Object>();			

		params.put("periodType", periodType);
		params.put("date", date);
		params.put("supplierId", supplierId);
		params.put("locationId", locationId);

		Map<String, Object> usage = locationUsageStatisticManager.getSearchChartData(params);

		ModelAndView mav = new ModelAndView("jsonView");

		
		mav.addObject("minVal", usage.get("minVal"));
		mav.addObject("maxVal", usage.get("maxVal"));
		mav.addObject("billMinVal", usage.get("billMinVal"));
		mav.addObject("billMaxVal", usage.get("billMaxVal"));
		mav.addObject("co2MinVal", usage.get("co2MinVal"));
		mav.addObject("co2MaxVal", usage.get("co2MaxVal"));
		
		mav.addObject("tmMinVal", usage.get("tmMinVal"));
		mav.addObject("tmMaxVal", usage.get("tmMaxVal"));
		
		mav.addObject("humMinVal", usage.get("humMinVal"));		
		mav.addObject("humMaxVal", usage.get("humMaxVal"));
		mav.addObject("currUsageList", usage.get("currUsageList"));
		mav.addObject("totalUsageMap", usage.get("totalUsageMap"));
		
		return mav;

	}
	
	@RequestMapping(value = "/gadget/bems/getSearchCompareChartData.do")
	public ModelAndView getSearchCompareChartData(@RequestParam("periodType") String periodType 
			,@RequestParam("date") String date 
			,@RequestParam("supplierId") Integer supplierId 
			,@RequestParam("locationId") Integer locationId
			,@RequestParam("compare") Boolean compare
			,@RequestParam("root") Boolean root) {
	
		Map<String, Object> params = new HashMap<String, Object>();			

		params.put("periodType", periodType);
		params.put("date", date);
		params.put("supplierId", supplierId);
		params.put("locationId", locationId);
		params.put("compare", compare);

		Map<String, Object> usage = locationUsageStatisticManager.getSearchCompareChartData(params);

		ModelAndView mav = new ModelAndView("jsonView");

				
		mav.addObject("minVal", usage.get("minVal"));
		mav.addObject("maxVal", usage.get("maxVal"));
		mav.addObject("billMinVal", usage.get("billMinVal"));
		mav.addObject("billMaxVal", usage.get("billMaxVal"));
		mav.addObject("co2MinVal", usage.get("co2MinVal"));
		mav.addObject("co2MaxVal", usage.get("co2MaxVal"));
		
		mav.addObject("tmMinVal", usage.get("tmMinVal"));
		mav.addObject("tmMaxVal", usage.get("tmMaxVal"));
		
		mav.addObject("humMinVal", usage.get("humMinVal"));		
		mav.addObject("humMaxVal", usage.get("humMaxVal"));
		mav.addObject("currUsageList", usage.get("currUsageList"));
		mav.addObject("preUsageList", usage.get("preUsageList"));
		mav.addObject("totalUsageMap", usage.get("totalUsageMap"));
		
		return mav;

	}


	
	
}
