package com.aimir.bo.report;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.constants.CommonConstants.DateType;
import com.aimir.dao.device.EndDeviceDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.model.system.Location;
import com.aimir.model.system.Supplier;
import com.aimir.service.mvm.EmsReportManager;
import com.aimir.service.system.LocationManager;
import com.aimir.service.system.SupplierManager;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.DecimalUtil;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeLocaleUtil;
import com.aimir.util.TimeUtil;

@Controller
public class ReportController {

	@Autowired
	EmsReportManager emsReportManager;
	
	@Autowired
	LocationManager locationManager;
	
	@Autowired 
	LocationDao locationDao;
	
	@Autowired
	EndDeviceDao endDeviceDao;
	
	@Autowired
	SupplierManager supplierManager;

    @SuppressWarnings("unused")
    private static Log logger = LogFactory.getLog(ReportController.class);

	@RequestMapping(value = "/gadget/report/emsReportMini")
	public ModelAndView emsReportMiniGadget() {
		return new ModelAndView("/gadget/report/emsReport");
	}

	@RequestMapping(value = "/gadget/report/emsReportMax")
	public ModelAndView emsReportMaxGadget() {
		return new ModelAndView("/gadget/report/emsReport");
	}

	/**
	 * method name : getEmsReport<b/>
	 * method Desc : 에너지별 사용량 통계 보고서 정보를 조회한다.
	 *
	 * @param response
	 * @param request
	 * @param periodType
	 * @param searchDate
	 * @param quarter
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/report/emsReport")
	public ModelAndView getEmsReport(HttpServletResponse response,
			HttpServletRequest request,
			@RequestParam("periodType") String periodType,
			@RequestParam("searchDate") String searchDate,
			@RequestParam("quarter") String quarter) {

		String xmlString = "";

		String year = "";
		String weekOfYear = "";
		String month = "";
		String day = "";

		String periodTypeName = "";

		SimpleDateFormat inFormatter = new SimpleDateFormat("yyyyMMdd");
		try {
			Date date = inFormatter.parse(searchDate);

			year = new SimpleDateFormat("yyyy").format(date);
			weekOfYear = new SimpleDateFormat("w").format(date);
			month = new SimpleDateFormat("M").format(date);
			day = new SimpleDateFormat("d").format(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		ModelAndView mav = new ModelAndView("report/reportData");

		String reportFileName = "";
		if (DateType.DAILY.getCode().equals(periodType)) {
			reportFileName = "energyReport"
					+ periodType
					+ "_"
					+ searchDate
					+ "."
					+ new SimpleDateFormat("yyyyMMdd").format(Calendar
							.getInstance().getTime());
		} else if (DateType.WEEKLY.getCode().equals(periodType)) {
			reportFileName = "energyReport"
					+ periodType
					+ "_"
					+ year
					+ weekOfYear
					+ "."
					+ new SimpleDateFormat("yyyyMMdd").format(Calendar
							.getInstance().getTime());
		} else if (DateType.MONTHLY.getCode().equals(periodType)) {
			reportFileName = "energyReport"
					+ periodType
					+ "_"
					+ year
					+ month
					+ "."
					+ new SimpleDateFormat("yyyyMMdd").format(Calendar
							.getInstance().getTime());
		} else if (DateType.YEARLY.getCode().equals(periodType)) {
			reportFileName = "energyReport"
					+ periodType
					+ "_"
					+ year
					+ "."
					+ new SimpleDateFormat("yyyyMMdd").format(Calendar
							.getInstance().getTime());
		} else if (DateType.QUARTERLY.getCode().equals(periodType)) {
			reportFileName = "energyReport"
					+ periodType
					+ "_"
					+ year
					+ quarter
					+ "."
					+ new SimpleDateFormat("yyyyMMdd").format(Calendar
							.getInstance().getTime());
		}

		File reportFile = getReportFile(request, reportFileName);
		BufferedWriter bw = null;

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("periodType", periodType);
		params.put("searchDate", searchDate);
		params.put("year", year);
		params.put("weekOfYear", weekOfYear);
		params.put("month", month);
		params.put("quarter", quarter);

		Map<String, Object> resultMap = emsReportManager
				.getEmsReportInfo(params);

		Element energyEl = genElementEnergy(
				(Map<String, Object>) resultMap.get("Energy"), "Energy");
		Element coopEl = genElementMachinery(
				(Map<String, Object>) resultMap.get("Machinery"), "Machinery");
		Element elecEl = genElementElectricity(
				(Map<String, Object>) resultMap.get("Electricity"),
				"Electricity");
		Element etcEl = genElementEtc(
				(Map<String, Object>) resultMap.get("Etc"), "Etc");
		// Element abnormalEl = genElement(genData(), "Abnormal");

		Element master = new Element("EmsReport");

		String srchDate = "";
		if (DateType.DAILY.getCode().equals(periodType)) {
			srchDate = year + ". " + month + ". " + day;
		} else if (DateType.WEEKLY.getCode().equals(periodType)) {
			srchDate = year + "-" + weekOfYear;
		} else if (DateType.MONTHLY.getCode().equals(periodType)) {
			srchDate = year + ". " + month;
		} else if (DateType.YEARLY.getCode().equals(periodType)) {
			srchDate = year;
		} else if (DateType.QUARTERLY.getCode().equals(periodType)) {
			srchDate = year + "-" + quarter;
		}
		master.setAttribute("searchDate", srchDate);
		master.setAttribute("searchDateType", periodTypeName);

		master.addContent(energyEl);
		master.addContent(coopEl);
		master.addContent(elecEl);
		master.addContent(etcEl);
		// master.addContent(abnormalEl);

		xmlString = getXmlString(master);

		try {
			bw = new BufferedWriter(new FileWriter(reportFile));
			bw.write(xmlString);
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			try {
				if (bw != null)
					bw.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		mav.addObject("data", xmlString);
		return mav;
	}
	
	/**
	 * method name : getUsageStatisticReport<b/>
	 * method Desc : 전기 사용실적 보고서 정보를 조회한다.
	 *
	 * @param year
	 * @param location
	 * @param supplier
	 * @return
	 */
	@RequestMapping("/report/usageReport")
	public ModelAndView getUsageStatisticReport(@RequestParam("year") String year, 
			String location, String supplier ) {
		
		ModelAndView mav = new ModelAndView("report/reportData");
		int yyyy = Integer.parseInt(year);
		int locationId;

		// locationId가 null인 경우
		if ("-1".equals(location)) {
			List<Location> locationRoot = locationDao.getRootLocationList();
			locationId = locationRoot.get(0).getId();

			// supplierId가 null인 경우
			if ( !"-1".equals(supplier)) {
				int supplierId = Integer.parseInt(supplier) ;
				
				for ( Location loc : locationRoot ) {
					if (loc.getSupplierId() == supplierId) {
						locationId = loc.getId();
					}
				}
			}
			
		} else {
			locationId = Integer.parseInt(location);
		}
		
		String last = "" + (yyyy - 1);
		String first = "" + (yyyy - 2);
		
		List<Map<String, Object>> currYear = emsReportManager.getYearlyUsageStatisticReport( year, locationId );
		List<Map<String, Object>> lastYear = emsReportManager.getYearlyUsageStatisticReport( last, locationId );
		List<Map<String, Object>> firstYear = emsReportManager.getYearlyUsageStatisticReport( first, locationId );
		
		String xml = getXmlString(genElementUsage(currYear, lastYear, firstYear));

		mav.addObject("data", xml);
		return mav;
	}
	
	private Element genElementUsage( List<Map<String, Object>> currData, 
			List<Map<String, Object>> lastData, List<Map<String, Object>> firstData) {
		Element element = new Element("UsageReport");
		
		for ( int i = 0 ; i < currData.size() ; i++ ) {
			Element rowElement = new Element("row");
			Map<String, Object> curRow = currData.get(i);
			Map<String, Object> lastRow = lastData.get(i);
			Map<String, Object> firstRow = firstData.get(i);
			
			rowElement.setAttribute("yyyymm", (String)curRow.get("YYYYMM"));
			rowElement.setAttribute("curUsage", "" + (Double)curRow.get("TOTAL"));
			rowElement.setAttribute("curCharge", "" + (Double)curRow.get("charge"));
			
			rowElement.setAttribute("lastUsage", "" + (Double)lastRow.get("TOTAL"));
			rowElement.setAttribute("lastCharge", "" + (Double)lastRow.get("charge"));
			
			rowElement.setAttribute("firstUsage", "" + (Double)firstRow.get("TOTAL"));
			rowElement.setAttribute("firstCharge", "" + (Double)firstRow.get("charge"));
			element.addContent(rowElement);
		}

		return element;
	}
	
	private String getXmlString(Element element) {

		XMLOutputter xmlOut = new XMLOutputter();
		Format format = Format.getPrettyFormat();
		format.setEncoding("UTF-8");
		xmlOut.setFormat(format);

		String xmlStr = xmlOut.outputString(element);

		return xmlStr;
	}

	@SuppressWarnings("unchecked")
	private Element genElementEnergy(Map<String, Object> data, String name) {
		List<Object> pieChartDataList = (List<Object>) data.get("pieChartData");
		List<Object> columnChartDataList = (List<Object>) data
				.get("columnChartData");
		List<Object> gridDataList = (List<Object>) data.get("gridData");

		Element el = new Element(name);
		// 파이차트데이터 Element 생성
		Element pieChartEl = new Element("PieChartData");
		Map<String, Object> pieChartData = null;
		for (Object obj : pieChartDataList) {
			pieChartData = (Map<String, Object>) obj;
			Element row = new Element("Row");
            row.setAttribute("type", StringUtil.nullToBlank(pieChartData.get("type")));
            row.setAttribute("usage", StringUtil.nullToBlank(pieChartData.get("usage")));
			pieChartEl.addContent(row);
		}

		// 컬럼차트데이터 Element 생성
		Element columnChartEl = new Element("ColumnChartData");
		Map<String, Object> columnChartData = null;
		for (Object obj : columnChartDataList) {
			columnChartData = (Map<String, Object>) obj;
			Element row = new Element("Row");
            row.setAttribute("date", StringUtil.nullToBlank(columnChartData.get("xField")));
            row.setAttribute("electricity", StringUtil.nullToZero(columnChartData.get("EmToe")));
            row.setAttribute("water", StringUtil.nullToZero(columnChartData.get("WmToe")));
            row.setAttribute("gas", StringUtil.nullToZero(columnChartData.get("GmToe")));
			// row.setAttribute("etc", (String)columnChartData.get("etc"));
			row.setAttribute("temp", "0");// (String)columnChartData.get("temp"));
			columnChartEl.addContent(row);
		}

		// 컬럼차트데이터 Element 생성
		Element gridEl = new Element("GridData");
		Map<String, Object> gridData = null;
		for (Object obj : gridDataList) {
			gridData = (Map<String, Object>) obj;
			Element row = new Element("Row");
            row.setAttribute("type", StringUtil.nullToBlank(gridData.get("type")));
            row.setAttribute("usage", StringUtil.nullToBlank(gridData.get("usage")));
            row.setAttribute("lastDayRate", StringUtil.nullToBlank(gridData.get("preRate")));
            row.setAttribute("lastYearRate", StringUtil.nullToBlank(gridData.get("lastYearRate")));
            row.setAttribute("usageRate", StringUtil.nullToBlank(gridData.get("usageRate")));
            row.setAttribute("peakUsage", StringUtil.nullToBlank(gridData.get("peakUsage")));
            row.setAttribute("peakDate", StringUtil.nullToBlank(gridData.get("peakPeriod")));
            row.setAttribute("toe", StringUtil.nullToBlank(gridData.get("toe")));
            row.setAttribute("co2", StringUtil.nullToBlank(gridData.get("co2")));
            row.setAttribute("co2rate", StringUtil.nullToBlank(gridData.get("co2rate")));

			gridEl.addContent(row);
		}

		el.addContent(pieChartEl);
		el.addContent(columnChartEl);
		el.addContent(gridEl);

		return el;
	}

	@SuppressWarnings("unchecked")
	private Element genElementMachinery(Map<String, Object> data, String name) {
		List<Object> pieChartDataList = (List<Object>) data.get("pieChartData");
		List<Object> columnChartDataList = (List<Object>) data.get("columnChartData");
		List<Object> gridDataList = (List<Object>) data.get("gridData");

		Element el = new Element(name);
		// 파이차트데이터 Element 생성
		Element pieChartEl = new Element("PieChartData");
		Map<String, Object> pieChartData = null;
		for (Object obj : pieChartDataList) {
			pieChartData = (Map<String, Object>) obj;
			Element row = new Element("Row");
            row.setAttribute("type", StringUtil.nullToBlank(pieChartData.get("type")));
            row.setAttribute("usage", StringUtil.nullToBlank(pieChartData.get("usage")));
			pieChartEl.addContent(row);
		}

		// 컬럼차트데이터 Element 생성
		Element columnChartEl = new Element("ColumnChartData");
		Map<String, Object> columnChartData = null;
		for (Object obj : columnChartDataList) {
			columnChartData = (Map<String, Object>) obj;
			Element row = new Element("Row");
            row.setAttribute("date", StringUtil.nullToBlank(columnChartData.get("xField")));
            row.setAttribute("HeatSource", StringUtil.nullToZero(columnChartData.get("1.9.1.1.1")));
            row.setAttribute("HeatReturn", StringUtil.nullToZero(columnChartData.get("1.9.1.1.2")));
            row.setAttribute("Motorized", StringUtil.nullToZero(columnChartData.get("1.9.1.1.3")));
            row.setAttribute("temp", "0");// (String)columnChartData.get("temp"));
			columnChartEl.addContent(row);
		}

		// 컬럼차트데이터 Element 생성
		Element gridEl = new Element("GridData");
		Map<String, Object> gridData = null;
		for (Object obj : gridDataList) {
			gridData = (Map<String, Object>) obj;
			Element row = new Element("Row");
            row.setAttribute("type", StringUtil.nullToBlank(gridData.get("type")));
            row.setAttribute("usage", StringUtil.nullToBlank(gridData.get("usage")));
            row.setAttribute("lastDayRate", StringUtil.nullToBlank(gridData.get("preRate")));
            row.setAttribute("lastYearRate", StringUtil.nullToBlank(gridData.get("lastYearRate")));
            row.setAttribute("usageRate", StringUtil.nullToBlank(gridData.get("usageRate")));
            row.setAttribute("peakUsage", StringUtil.nullToBlank(gridData.get("peakUsage")));
            row.setAttribute("peakDate", StringUtil.nullToBlank(gridData.get("peakPeriod")));
            row.setAttribute("co2", StringUtil.nullToBlank(gridData.get("co2")));
            row.setAttribute("co2rate", StringUtil.nullToBlank(gridData.get("co2rate")));

			gridEl.addContent(row);
		}

		el.addContent(pieChartEl);
		el.addContent(columnChartEl);
		el.addContent(gridEl);

		return el;
	}

	@SuppressWarnings("unchecked")
	private Element genElementElectricity(Map<String, Object> data, String name) {
		List<Object> pieChartDataList = (List<Object>) data.get("pieChartData");
		List<Object> columnChartDataList = (List<Object>) data
				.get("columnChartData");
		List<Object> gridDataList = (List<Object>) data.get("gridData");

		Element el = new Element(name);
		// 파이차트데이터 Element 생성
		Element pieChartEl = new Element("PieChartData");
		Map<String, Object> pieChartData = null;
		for (Object obj : pieChartDataList) {
			pieChartData = (Map<String, Object>) obj;
			Element row = new Element("Row");
            row.setAttribute("type", StringUtil.nullToBlank(pieChartData.get("type")));
            row.setAttribute("usage", StringUtil.nullToBlank(pieChartData.get("usage")));
			pieChartEl.addContent(row);
		}

		// 컬럼차트데이터 Element 생성
		Element columnChartEl = new Element("ColumnChartData");
		Map<String, Object> columnChartData = null;
		for (Object obj : columnChartDataList) {
			columnChartData = (Map<String, Object>) obj;
			Element row = new Element("Row");
            row.setAttribute("date", StringUtil.nullToBlank(columnChartData.get("xField")));
            row.setAttribute("Lighting", StringUtil.nullToZero(columnChartData.get("1.9.1.2.1")));
            row.setAttribute("Socket", StringUtil.nullToZero(columnChartData.get("1.9.1.2.2")));
            row.setAttribute("transport", StringUtil.nullToZero(columnChartData.get("1.9.1.2.3")));
            row.setAttribute("temp", "0");// (String)columnChartData.get("temp"));
			columnChartEl.addContent(row);
		}

		// 컬럼차트데이터 Element 생성
		Element gridEl = new Element("GridData");
		Map<String, Object> gridData = null;
		for (Object obj : gridDataList) {
			gridData = (Map<String, Object>) obj;
			Element row = new Element("Row");
            row.setAttribute("type", StringUtil.nullToBlank(gridData.get("type")));
            row.setAttribute("usage", StringUtil.nullToBlank(gridData.get("emUsage")));
            row.setAttribute("lastDayRate", StringUtil.nullToBlank(gridData.get("preRate")));
            row.setAttribute("lastYearRate", StringUtil.nullToBlank(gridData.get("lastYearRate")));
            row.setAttribute("usageRate", StringUtil.nullToBlank(gridData.get("usageRate")));
            row.setAttribute("peakUsage", StringUtil.nullToBlank(gridData.get("peakUsage")));
            row.setAttribute("peakDate", StringUtil.nullToBlank(gridData.get("peakPeriod")));
            row.setAttribute("toe", StringUtil.nullToBlank(gridData.get("usage")));
            row.setAttribute("co2", StringUtil.nullToBlank(gridData.get("co2")));
            row.setAttribute("co2rate", StringUtil.nullToBlank(gridData.get("co2rate")));

			gridEl.addContent(row);
		}

		el.addContent(pieChartEl);
		el.addContent(columnChartEl);
		el.addContent(gridEl);

		return el;
	}

	@SuppressWarnings("unchecked")
	private Element genElementEtc(Map<String, Object> data, String name) {
		List<Object> pieChartDataList = (List<Object>) data.get("pieChartData");
		List<Object> columnChartDataList = (List<Object>) data
				.get("columnChartData");
		List<Object> gridDataList = (List<Object>) data.get("gridData");

		Element el = new Element(name);
		// 파이차트데이터 Element 생성
		Element pieChartEl = new Element("PieChartData");
		Map<String, Object> pieChartData = null;
		for (Object obj : pieChartDataList) {
			pieChartData = (Map<String, Object>) obj;
			Element row = new Element("Row");
            row.setAttribute("type", StringUtil.nullToBlank(pieChartData.get("type")));
            row.setAttribute("usage", StringUtil.nullToBlank(pieChartData.get("usage")));
			pieChartEl.addContent(row);
		}

		// 컬럼차트데이터 Element 생성
		Element columnChartEl = new Element("ColumnChartData");
		Map<String, Object> columnChartData = null;
		for (Object obj : columnChartDataList) {
			columnChartData = (Map<String, Object>) obj;
			Element row = new Element("Row");
            row.setAttribute("date", StringUtil.nullToBlank(columnChartData.get("xField")));
            row.setAttribute("renewal", StringUtil.nullToZero(columnChartData.get("1.9.1.3.1")));
			row.setAttribute("temp", "0");// (String)columnChartData.get("temp"));
			columnChartEl.addContent(row);
		}

		// 컬럼차트데이터 Element 생성
		Element gridEl = new Element("GridData");
		Map<String, Object> gridData = null;
		for (Object obj : gridDataList) {
			gridData = (Map<String, Object>) obj;
			Element row = new Element("Row");
            row.setAttribute("type", StringUtil.nullToBlank(gridData.get("type")));
            row.setAttribute("usage", StringUtil.nullToBlank(gridData.get("usage")));
            row.setAttribute("lastDayRate", StringUtil.nullToBlank(gridData.get("preRate")));
            row.setAttribute("lastYearRate", StringUtil.nullToBlank(gridData.get("lastYearRate")));
            row.setAttribute("usageRate", StringUtil.nullToBlank(gridData.get("usageRate")));
            row.setAttribute("peakUsage", StringUtil.nullToBlank(gridData.get("peakUsage")));
            row.setAttribute("peakDate", StringUtil.nullToBlank(gridData.get("peakPeriod")));
            row.setAttribute("co2", StringUtil.nullToBlank(gridData.get("co2")));
            row.setAttribute("co2rate", StringUtil.nullToBlank(gridData.get("co2rate")));

			gridEl.addContent(row);
		}

		el.addContent(pieChartEl);
		el.addContent(columnChartEl);
		el.addContent(gridEl);

		return el;
	}

	@SuppressWarnings("unused")
    private Map<String, Object> genData() {

		Map<String, Object> resultData = new HashMap<String, Object>();

		List<Object> pieChartDataList = new ArrayList<Object>();
		List<Object> columnChartDataList = new ArrayList<Object>();
		List<Object> gridDataList = new ArrayList<Object>();

		// 파이차트 데이터
		Map<String, Object> pieChartData = null;
		for (int i = 0; i < 4; i++) {
			pieChartData = new HashMap<String, Object>();
			String type = "";
			String usage = "0";
			if (i == 0) {
				type = "전기";
				usage = "100";
			} else if (i == 1) {
				type = "가스";
				usage = "120";
			} else if (i == 2) {
				type = "수도";
				usage = "70";
			} else if (i == 3) {
				type = "기타";
				usage = "10";
			}
			pieChartData.put("type", type);
			pieChartData.put("usage", usage);

			pieChartDataList.add(pieChartData);
		}

		// 컬럼차트 데이터
		Map<String, Object> columnChartData = null;
		for (int i = 0; i < 24; i++) {
			columnChartData = new HashMap<String, Object>();

			columnChartData.put("date", TimeUtil.to2Digit(i) + "h");
			columnChartData.put("electricity",
					Integer.toString((int) (Math.random() * 100 + 1)));
			columnChartData.put("water",
					Integer.toString((int) (Math.random() * 100 + 1)));
			columnChartData.put("gas",
					Integer.toString((int) (Math.random() * 100 + 1)));
			columnChartData.put("etc",
					Integer.toString((int) (Math.random() * 100 + 1)));
			columnChartData.put("temp",
					Integer.toString((int) (Math.random() * 40 + 1)));

			columnChartDataList.add(columnChartData);
		}

		// 그리드 데이터
		Map<String, Object> gridData = null;
		for (int i = 0; i < 4; i++) {
			gridData = new HashMap<String, Object>();
			String type = "";
			String usage = "0";
			String lastDayRate = "";
			String lastYearRate = "";
			String usageRate = "";
			String peakUsage = "";
			String peakDate = "";
			String toe = "";
			String co2 = "";
			String co2rate = "";
			String minTemp = "";
			String maxTemp = "";

			if (i == 0) {
				type = "전기";
				usage = "100KWh";
				lastDayRate = "90%";
				lastYearRate = "85%";
				usageRate = "50%";
				peakUsage = "45KWh";
				peakDate = "13h";
				toe = "13.43";
				co2 = "243Kg";
				co2rate = "40%";
				minTemp = "23";
				maxTemp = "32";

			} else if (i == 1) {
				type = "가스";
				usage = "120m3";
				lastDayRate = "70%";
				lastYearRate = "95%";
				usageRate = "30%";
				peakUsage = "54m3";
				peakDate = "12h";
				toe = "11.13";
				co2 = "343Kg";
				co2rate = "40%";
				minTemp = "23";
				maxTemp = "32";
			} else if (i == 2) {
				type = "수도";
				usage = "70";
				lastDayRate = "95%";
				lastYearRate = "90%";
				usageRate = "15%";
				peakUsage = "44m3";
				peakDate = "16h";
				toe = "9.13";
				co2 = "143Kg";
				co2rate = "15%";
				minTemp = "23";
				maxTemp = "32";
			} else if (i == 3) {
				type = "기타";
				usage = "10";
				lastDayRate = "110%";
				lastYearRate = "120%";
				usageRate = "5%";
				peakUsage = "14m3";
				peakDate = "19h";
				toe = "5.13";
				co2 = "113Kg";
				co2rate = "10%";
				minTemp = "23";
				maxTemp = "32";
			}
			gridData.put("type", type);
			gridData.put("usage", usage);
			gridData.put("lastDayRate", lastDayRate);
			gridData.put("lastYearRate", lastYearRate);
			gridData.put("usageRate", usageRate);
			gridData.put("peakUsage", peakUsage);
			gridData.put("peakDate", peakDate);
			gridData.put("toe", toe);
			gridData.put("co2", co2);
			gridData.put("co2rate", co2rate);
			gridData.put("minTemp", minTemp);
			gridData.put("maxTemp", maxTemp);

			gridDataList.add(gridData);
		}

		resultData.put("pieChartData", pieChartDataList);
		resultData.put("columnChartData", columnChartDataList);
		resultData.put("gridData", gridDataList);

		return resultData;
	}

	/**
	 * method name : getEnergySavingReport<b/>
	 * method Desc : 에너지 목표관리 보고서 정보를 조회한다.
	 *
	 * @param response
	 * @param request
	 * @param supplierId
	 * @param searchYear
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "unused" })
    @RequestMapping(value = "/report/energySaving")
	public ModelAndView getEnergySavingReport(HttpServletResponse response,
			HttpServletRequest request,
			@RequestParam("supplierId") String supplierId,
			@RequestParam("searchYear") String searchYear) {

		ModelAndView mav = new ModelAndView("report/reportData");
		String xmlString = "";

		String reportFileName = "energyReport"
				+ searchYear
				+ "_"
				+ supplierId
				+ "."
				+ new SimpleDateFormat("yyyyMMdd").format(Calendar
						.getInstance().getTime());

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("supplierId", supplierId);
		params.put("searchYear", searchYear);

		Map<String, Object> resultMap = emsReportManager
				.getEnergySavingReportInfo(params);

		Element monthlyEl = genElementMonthlyEnergySaving(
				(Map<String, Object>) resultMap.get("Monthly"), "Monthly");
		Element yearlyEl = genElementYearlyEnergySaving(
				(Map<String, Object>) resultMap.get("Yearly"), "Yearly");

		Element master = new Element("EnergySavingReport");

		master.addContent(monthlyEl);
		master.addContent(yearlyEl);

		xmlString = getXmlString(master);

		mav.addObject("data", xmlString);

		return mav;
	}

	/**
	 * method name : getEnergyUsageStatisticReport<b/>
	 * method Desc : 요구청구 금액내역 보고서 정보를 조회한다.
	 *
	 * @param energyType
	 * @param yyyymm
	 * @return
	 */
    @RequestMapping("/report/energyUsageStatistic")
	public ModelAndView getEnergyUsageStatisticReport(@RequestParam("energyType") String energyType,
			@RequestParam("yyyymm") String yyyymm ) {
		ModelAndView mav = new ModelAndView("report/reportData");
		String xml = "";
		energyType = "Month" + energyType;

		List<Map<String, Object>> data = emsReportManager.getEnergyUsageInfo(yyyymm, energyType);

		Element element = genElementEnergyUsageStatistic(data);
		xml = getXmlString(element);
		mav.addObject("data", xml);
		return mav;
	}

	@SuppressWarnings("unchecked")
    public Element genElementMonthlyEnergySaving(Map<String, Object> data,
			String name) {
        List<Object> emMonthlyReportList = (List<Object>) data.get("emMonthlyReport");
        List<Object> wmMonthlyReportList = (List<Object>) data.get("wmMonthlyReport");
        List<Object> gmMonthlyReportList = (List<Object>) data.get("gmMonthlyReport");

		Element el = new Element(name);

		// 전기
		Element emMonthlyReportEl = new Element("emMonthlyReport");
		Map<String, Object> emMonthlyReportData = null;
		for (Object obj : emMonthlyReportList) {
			emMonthlyReportData = (Map<String, Object>) obj;
			Element row = new Element("Row");
            row.setAttribute("month", StringUtil.nullToBlank(emMonthlyReportData.get("month")));
            row.setAttribute("usage", StringUtil.nullToBlank(emMonthlyReportData.get("usage")));
            row.setAttribute("goalUsage", StringUtil.nullToBlank(emMonthlyReportData.get("goalUsage")));
            row.setAttribute("toe", StringUtil.nullToBlank(emMonthlyReportData.get("toe")));
            row.setAttribute("goalToe", StringUtil.nullToBlank(emMonthlyReportData.get("goalToe")));
            row.setAttribute("reduceUsageRate", StringUtil.nullToBlank(emMonthlyReportData.get("reduceUsageRate")));
            row.setAttribute("reduceCo2", StringUtil.nullToBlank(emMonthlyReportData.get("reduceCo2")));
            row.setAttribute("effectedUsage", StringUtil.nullToBlank(emMonthlyReportData.get("effectedUsage")));
            row.setAttribute("effectedToe", StringUtil.nullToBlank(emMonthlyReportData.get("effectedToe")));
            row.setAttribute("goal", StringUtil.nullToBlank(emMonthlyReportData.get("goal")));
			emMonthlyReportEl.addContent(row);
		}

		// 수도
		Element wmMonthlyReportEl = new Element("wmMonthlyReport");
		Map<String, Object> wmMonthlyReportData = null;
		for (Object obj : wmMonthlyReportList) {
			wmMonthlyReportData = (Map<String, Object>) obj;
			Element row = new Element("Row");
            row.setAttribute("month", StringUtil.nullToBlank(wmMonthlyReportData.get("month")));
            row.setAttribute("usage", StringUtil.nullToBlank(wmMonthlyReportData.get("usage")));
            row.setAttribute("goalUsage", StringUtil.nullToBlank(wmMonthlyReportData.get("goalUsage")));
            row.setAttribute("toe", StringUtil.nullToBlank(wmMonthlyReportData.get("toe")));
            row.setAttribute("goalToe", StringUtil.nullToBlank(wmMonthlyReportData.get("goalToe")));
            row.setAttribute("reduceUsageRate", StringUtil.nullToBlank(wmMonthlyReportData.get("reduceUsageRate")));
            row.setAttribute("reduceCo2", StringUtil.nullToBlank(wmMonthlyReportData.get("reduceCo2")));
            row.setAttribute("effectedUsage", StringUtil.nullToBlank(wmMonthlyReportData.get("effectedUsage")));
            row.setAttribute("effectedToe", StringUtil.nullToBlank(wmMonthlyReportData.get("effectedToe")));
            row.setAttribute("goal", StringUtil.nullToBlank(wmMonthlyReportData.get("goal")));
			wmMonthlyReportEl.addContent(row);
		}

		// 가스
		Element gmMonthlyReportEl = new Element("gmMonthlyReport");
		Map<String, Object> gmMonthlyReportData = null;
		for (Object obj : gmMonthlyReportList) {
			gmMonthlyReportData = (Map<String, Object>) obj;
			Element row = new Element("Row");
            row.setAttribute("month", StringUtil.nullToBlank(gmMonthlyReportData.get("month")));
            row.setAttribute("usage", StringUtil.nullToBlank(gmMonthlyReportData.get("usage")));
            row.setAttribute("goalUsage", StringUtil.nullToBlank(gmMonthlyReportData.get("goalUsage")));
            row.setAttribute("toe", StringUtil.nullToBlank(gmMonthlyReportData.get("toe")));
            row.setAttribute("goalToe", StringUtil.nullToBlank(gmMonthlyReportData.get("goalToe")));
            row.setAttribute("reduceUsageRate", StringUtil.nullToBlank(gmMonthlyReportData.get("reduceUsageRate")));
            row.setAttribute("reduceCo2", StringUtil.nullToBlank(gmMonthlyReportData.get("reduceCo2")));
            row.setAttribute("effectedUsage", StringUtil.nullToBlank(gmMonthlyReportData.get("effectedUsage")));
            row.setAttribute("effectedToe", StringUtil.nullToBlank(gmMonthlyReportData.get("effectedToe")));
            row.setAttribute("goal", StringUtil.nullToBlank(gmMonthlyReportData.get("goal")));
			gmMonthlyReportEl.addContent(row);
		}

		el.addContent(emMonthlyReportEl);
		el.addContent(wmMonthlyReportEl);
		el.addContent(gmMonthlyReportEl);

		return el;
	}

	@SuppressWarnings("unchecked")
    public Element genElementYearlyEnergySaving(Map<String, Object> data, String name) {
        List<Object> emYearlyReportList = (List<Object>) data.get("emYearlyReport");
        List<Object> wmYearlyReportList = (List<Object>) data.get("wmYearlyReport");
        List<Object> gmYearlyReportList = (List<Object>) data.get("gmYearlyReport");

		Element el = new Element(name);

		// 전기
		Element emYearlyReportEl = new Element("emYearlyReport");
		Map<String, Object> emYearlyReportData = null;
		for (Object obj : emYearlyReportList) {
			emYearlyReportData = (Map<String, Object>) obj;
			Element row = new Element("Row");
            row.setAttribute("year", StringUtil.nullToBlank(emYearlyReportData.get("year")));
            row.setAttribute("usage", StringUtil.nullToBlank(emYearlyReportData.get("usage")));
            row.setAttribute("goalUsage", StringUtil.nullToBlank(emYearlyReportData.get("goalUsage")));
            row.setAttribute("toe", StringUtil.nullToBlank(emYearlyReportData.get("toe")));
            row.setAttribute("goalToe", StringUtil.nullToBlank(emYearlyReportData.get("goalToe")));
            row.setAttribute("reduceUsageRate", StringUtil.nullToBlank(emYearlyReportData.get("reduceUsageRate")));
            row.setAttribute("reduceCo2", StringUtil.nullToBlank(emYearlyReportData.get("reduceCo2")));
            row.setAttribute("effectedUsage", StringUtil.nullToBlank(emYearlyReportData.get("effectedUsage")));
            row.setAttribute("effectedToe", StringUtil.nullToBlank(emYearlyReportData.get("effectedToe")));
            row.setAttribute("period", StringUtil.nullToBlank(emYearlyReportData.get("period")));
            row.setAttribute("avgUsage", StringUtil.nullToBlank(emYearlyReportData.get("avgUsage")));
            row.setAttribute("goal", StringUtil.nullToBlank(emYearlyReportData.get("goal")));
			emYearlyReportEl.addContent(row);
		}

		Element emYearlyChartEl = new Element("emYearlyChart");
		if (emYearlyReportList.size() > 0) {
            Map<String, Object> emYearlyChartData = (Map<String, Object>) emYearlyReportList.get(0);
			Element row = new Element("Row");
            row.setAttribute("xField", StringUtil.nullToBlank(emYearlyChartData.get("period")) + "년 평균");
            row.setAttribute("value", StringUtil.nullToBlank(emYearlyChartData.get("avgUsage")));
            row.setAttribute("order", "1");
			emYearlyChartEl.addContent(row);

			row = new Element("Row");
            row.setAttribute("xField", "목표");
            row.setAttribute("value", StringUtil.nullToBlank(emYearlyChartData.get("goalUsage")));
            row.setAttribute("order", "2");
			emYearlyChartEl.addContent(row);

			row = new Element("Row");
			row.setAttribute("xField", "예상");
			row.setAttribute("value", "0");
			row.setAttribute("order", "3");
			emYearlyChartEl.addContent(row);

			row = new Element("Row");
            row.setAttribute("xField", StringUtil.nullToBlank(emYearlyChartData.get("year")));
            row.setAttribute("value", StringUtil.nullToBlank(emYearlyChartData.get("usage")));
			row.setAttribute("order", "4");
			emYearlyChartEl.addContent(row);
		}

		// 수도
		Element wmYearlyReportEl = new Element("wmYearlyReport");
		Map<String, Object> wmYearlyReportData = null;
		for (Object obj : wmYearlyReportList) {
			wmYearlyReportData = (Map<String, Object>) obj;
			Element row = new Element("Row");
            row.setAttribute("year", StringUtil.nullToBlank(wmYearlyReportData.get("year")));
            row.setAttribute("usage", StringUtil.nullToBlank(wmYearlyReportData.get("usage")));
            row.setAttribute("goalUsage", StringUtil.nullToBlank(wmYearlyReportData.get("goalUsage")));
            row.setAttribute("toe", StringUtil.nullToBlank(wmYearlyReportData.get("toe")));
            row.setAttribute("goalToe", StringUtil.nullToBlank(wmYearlyReportData.get("goalToe")));
            row.setAttribute("reduceUsageRate", StringUtil.nullToBlank(wmYearlyReportData.get("reduceUsageRate")));
            row.setAttribute("reduceCo2", StringUtil.nullToBlank(wmYearlyReportData.get("reduceCo2")));
            row.setAttribute("effectedUsage", StringUtil.nullToBlank(wmYearlyReportData.get("effectedUsage")));
            row.setAttribute("effectedToe", StringUtil.nullToBlank(wmYearlyReportData.get("effectedToe")));
            row.setAttribute("period", StringUtil.nullToBlank(wmYearlyReportData.get("period")));
            row.setAttribute("avgUsage", StringUtil.nullToBlank(wmYearlyReportData.get("avgUsage")));
            row.setAttribute("goal", StringUtil.nullToBlank(wmYearlyReportData.get("goal")));
			wmYearlyReportEl.addContent(row);
		}

		Element wmYearlyChartEl = new Element("wmYearlyChart");
		if (wmYearlyReportList.size() > 0) {
            Map<String, Object> wmYearlyChartData = (Map<String, Object>) wmYearlyReportList.get(0);
            Element row = new Element("Row");
            row.setAttribute("xField", StringUtil.nullToBlank(wmYearlyChartData.get("period")) + "년 평균");
            row.setAttribute("value", StringUtil.nullToBlank(wmYearlyChartData.get("avgUsage")));
            row.setAttribute("order", "1");
            wmYearlyChartEl.addContent(row);

            row = new Element("Row");
            row.setAttribute("xField", "목표");
            row.setAttribute("value", StringUtil.nullToBlank(wmYearlyChartData.get("goalUsage")));
            row.setAttribute("order", "2");
            wmYearlyChartEl.addContent(row);

            row = new Element("Row");
            row.setAttribute("xField", "예상");
            row.setAttribute("value", "0");
            row.setAttribute("order", "3");
            wmYearlyChartEl.addContent(row);

            row = new Element("Row");
            row.setAttribute("xField", StringUtil.nullToBlank(wmYearlyChartData.get("year")));
            row.setAttribute("value", StringUtil.nullToBlank(wmYearlyChartData.get("usage")));
            row.setAttribute("order", "4");
            wmYearlyChartEl.addContent(row);
		}

		// 가스
		Element gmYearlyReportEl = new Element("gmYearlyReport");
		Map<String, Object> gmYearlyReportData = null;
		for (Object obj : gmYearlyReportList) {
			gmYearlyReportData = (Map<String, Object>) obj;
			Element row = new Element("Row");
            row.setAttribute("year", StringUtil.nullToBlank(gmYearlyReportData.get("year")));
            row.setAttribute("usage", StringUtil.nullToBlank(gmYearlyReportData.get("usage")));
            row.setAttribute("goalUsage", StringUtil.nullToBlank(gmYearlyReportData.get("goalUsage")));
            row.setAttribute("toe", StringUtil.nullToBlank(gmYearlyReportData.get("toe")));
            row.setAttribute("goalToe", StringUtil.nullToBlank(gmYearlyReportData.get("goalToe")));
            row.setAttribute("reduceUsageRate", StringUtil.nullToBlank(gmYearlyReportData.get("reduceUsageRate")));
            row.setAttribute("reduceCo2", StringUtil.nullToBlank(gmYearlyReportData.get("reduceCo2")));
            row.setAttribute("effectedUsage", StringUtil.nullToBlank(gmYearlyReportData.get("effectedUsage")));
            row.setAttribute("effectedToe", StringUtil.nullToBlank(gmYearlyReportData.get("effectedToe")));
            row.setAttribute("period", StringUtil.nullToBlank(gmYearlyReportData.get("period")));
            row.setAttribute("avgUsage", StringUtil.nullToBlank(gmYearlyReportData.get("avgUsage")));
            row.setAttribute("goal", StringUtil.nullToBlank(gmYearlyReportData.get("goal")));
			gmYearlyReportEl.addContent(row);
		}

		Element gmYearlyChartEl = new Element("gmYearlyChart");
		if (gmYearlyReportList.size() > 0) {
            Map<String, Object> gmYearlyChartData = (Map<String, Object>) gmYearlyReportList.get(0);
            Element row = new Element("Row");
            row.setAttribute("xField", StringUtil.nullToBlank(gmYearlyChartData.get("period")) + "년 평균");
            row.setAttribute("value", StringUtil.nullToBlank(gmYearlyChartData.get("avgUsage")));
            row.setAttribute("order", "1");
            gmYearlyChartEl.addContent(row);

            row = new Element("Row");
            row.setAttribute("xField", "목표");
            row.setAttribute("value", StringUtil.nullToBlank(gmYearlyChartData.get("goalUsage")));
            row.setAttribute("order", "2");
            gmYearlyChartEl.addContent(row);

            row = new Element("Row");
            row.setAttribute("xField", "예상");
            row.setAttribute("value", "0");
            row.setAttribute("order", "3");
            gmYearlyChartEl.addContent(row);

            row = new Element("Row");
            row.setAttribute("xField", StringUtil.nullToBlank(gmYearlyChartData.get("year")));
            row.setAttribute("value", StringUtil.nullToBlank(gmYearlyChartData.get("usage")));
            row.setAttribute("order", "4");
            gmYearlyChartEl.addContent(row);
		}

		el.addContent(emYearlyReportEl);
		el.addContent(emYearlyChartEl);
		el.addContent(wmYearlyReportEl);
		el.addContent(wmYearlyChartEl);
		el.addContent(gmYearlyReportEl);
		el.addContent(gmYearlyChartEl);

		return el;
	}
	
    private Element genElementEnergyUsageStatistic(List<Map<String, Object>> data) {
		Element element = new Element("EnergyCharge");

		Properties prop = new Properties();

		try {
			prop.load(getClass().getClassLoader().getResourceAsStream("bems_charge.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		Element row1 = new Element("Row1");
		
		Double currUsageSum =0.0;
		Double lastUsageSum =0.0;
		Double currBillSum =0.0;
		Double lastBillSum =0.0;

		int index =1;

		for (Map<String, Object> tmp : data) {
			Element row = new Element("row");

			Double currUsage = (Double) tmp.get("currUsage");
			Double lastUsage = (Double) tmp.get("lastUsage");

            row.setAttribute("endDeviceName", StringUtil.nullToBlank(tmp.get("endDeviceName")));
			row.setAttribute("currUsage", "" + currUsage.intValue());
			row.setAttribute("lastUsage", "" + lastUsage.intValue());

			if (tmp.get("energyType").equals("MonthEM")) {
				int currBill = Integer.parseInt(prop.getProperty("energy.usageUnitPrice")) * currUsage.intValue();
				int lastBill = Integer.parseInt(prop.getProperty("energy.usageUnitPrice")) * lastUsage.intValue();
				row.setAttribute("currBill", String.valueOf(currBill));	
				row.setAttribute("lastBill", String.valueOf(lastBill));
				row.setAttribute("unit", "kWh");
				
				currUsageSum  = currUsageSum + currUsage;
				lastUsageSum  = lastUsageSum + lastUsage;
				currBillSum = currBillSum + currBill;
				lastBillSum = lastBillSum + lastBill;

				if (data.size() == index) {
					row1.setAttribute("currBillSum", ""+currBillSum.intValue());
					row1.setAttribute("lastBillSum", ""+lastBillSum.intValue());
					row1.setAttribute("currUsageSum", ""+currUsageSum.intValue());
					row1.setAttribute("lastUsageSum", ""+lastUsageSum.intValue());
					row1.setAttribute("unit", "kWh");
					
					element.addContent(row1);
				}
			} else if (tmp.get("energyType").equals("MonthGM")) {
				int currBill = Integer.parseInt(prop.getProperty("gas.usageUnitPrice")) * currUsage.intValue();
				int lastBill = Integer.parseInt(prop.getProperty("gas.usageUnitPrice")) * lastUsage.intValue();
				row.setAttribute("currBill", String.valueOf(currBill));	
				row.setAttribute("lastBill", String.valueOf(lastBill));
				row.setAttribute("unit", "㎥");
				
				currUsageSum  = currUsageSum + currUsage;
				lastUsageSum  = lastUsageSum + lastUsage;
				currBillSum = currBillSum + currBill;
				lastBillSum = lastBillSum + lastBill;

				if (data.size() == index) {
					row1.setAttribute("currBillSum", ""+currBillSum.intValue());
					row1.setAttribute("lastBillSum", ""+lastBillSum.intValue());
					row1.setAttribute("currUsageSum", ""+currUsageSum.intValue());
					row1.setAttribute("lastUsageSum", ""+lastUsageSum.intValue());
					row1.setAttribute("unit", "㎥");

					element.addContent(row1);
				}
			} else if (tmp.get("energyType").equals("MonthHM")) {
				int currBill = Integer.parseInt(prop.getProperty("heat.usageUnitPrice")) * currUsage.intValue();
				int lastBill = Integer.parseInt(prop.getProperty("heat.usageUnitPrice")) * lastUsage.intValue();
				row.setAttribute("currBill", String.valueOf(currBill));	
				row.setAttribute("lastBill", String.valueOf(lastBill));
				row.setAttribute("unit", "Gcal");

				currUsageSum  = currUsageSum + currUsage;
				lastUsageSum  = lastUsageSum + lastUsage;
				currBillSum = currBillSum + currBill;
				lastBillSum = lastBillSum + lastBill;

				if (data.size() == index) {
					row1.setAttribute("currBillSum", ""+currBillSum.intValue());
					row1.setAttribute("lastBillSum", ""+lastBillSum.intValue());
					row1.setAttribute("currUsageSum", ""+currUsageSum.intValue());
					row1.setAttribute("lastUsageSum", ""+lastUsageSum.intValue());
					row1.setAttribute("unit", "Gcal");

					element.addContent(row1);
				}
			} else {
				int currBill = Integer.parseInt(prop.getProperty("water.usageUnitPrice")) * currUsage.intValue();
				int lastBill = Integer.parseInt(prop.getProperty("water.usageUnitPrice")) * lastUsage.intValue();
				row.setAttribute("currBill", String.valueOf(currBill));	
				row.setAttribute("lastBill", String.valueOf(lastBill));
				row.setAttribute("unit", "㎥");

				currUsageSum  = currUsageSum + currUsage;
				lastUsageSum  = lastUsageSum + lastUsage;
				currBillSum = currBillSum + currBill;
				lastBillSum = lastBillSum + lastBill;

				if (data.size() == index) {
					row1.setAttribute("currBillSum", ""+currBillSum.intValue());
					row1.setAttribute("lastBillSum", ""+lastBillSum.intValue());
					row1.setAttribute("currUsageSum", ""+currUsageSum.intValue());
					row1.setAttribute("lastUsageSum", ""+lastUsageSum.intValue());
					row1.setAttribute("unit", "㎥");

					element.addContent(row1);
				}
			}
			element.addContent(row);
			index++;
		}

		return element;
	}
	
	/**
	 * method name : getZoneUsageReport<b/>
	 * method Desc : Zone 별 사용량 보고서 정보를 조회한다.
	 *
	 * @param response
	 * @param request
	 * @param searchStartDate
	 * @param searchEndDate
	 * @param supplierId
	 * @return
	 * @throws ParseException
	 */
	@SuppressWarnings({ "unused", "unchecked" })
    @RequestMapping(value = "/report/zoneUsageReport")
	public ModelAndView getZoneUsageReport(HttpServletResponse response,
			HttpServletRequest request,
			@RequestParam("searchStartDate") String searchStartDate,
			@RequestParam("searchEndDate") String searchEndDate,
			@RequestParam("supplierId") String supplierId) throws ParseException {

		ModelAndView mav = new ModelAndView("report/reportData");
		String xmlString = "";

		String reportFileName = "zoneUsageReport"
				+ searchStartDate
				+ "_"
				+ searchStartDate
				+"_"
				+ searchEndDate
				+ "."
				+ new SimpleDateFormat("yyyyMMdd").format(Calendar
						.getInstance().getTime());

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("supplierId", supplierId);
		params.put("startDate", searchStartDate);
		params.put("endDate", searchEndDate);
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap = emsReportManager.getZoneUsageInfo(params);

		List<Map<String,Object>> zoneTotal = new ArrayList<Map<String,Object>>();
		List<Map<String,Object>> total = new ArrayList<Map<String,Object>>();

		zoneTotal = (List<Map<String, Object>>) resultMap.get("zoneTotal");
		total = (List<Map<String, Object>>) resultMap.get("total");

		Properties prop = new Properties();

		try {
			prop.load(getClass().getClassLoader().getResourceAsStream("bems_charge.properties"));

		} catch (IOException e) {
			e.printStackTrace();
		}
		Element master = new Element("ZoneUsageReport");
        DecimalFormat df = new DecimalFormat("###.###");
        DecimalFormat df1 = new DecimalFormat("###,###,###");
        String unitPrice = prop.getProperty("energy.usageUnitPrice");
        BigDecimal tmpBdUnitPrice = new BigDecimal(unitPrice);
        BigDecimal tmpBdFee = null;

		for (Map<String, Object> map : zoneTotal) {
	        tmpBdFee = null;
			tmpBdFee = tmpBdUnitPrice.multiply(new BigDecimal(DecimalUtil.ConvertNumberToDouble(map.get("TOTAL"))));

            Element row = new Element("Row");
			row.setAttribute("TOTAL", df.format(DecimalUtil.ConvertNumberToDouble(map.get("TOTAL"))));
			row.setAttribute("NAME", String.valueOf(map.get("NAME")));
			row.setAttribute("charge", unitPrice);
			row.setAttribute("FEE", df1.format(tmpBdFee.doubleValue()));
			master.addContent(row);
		}

		Element row1 = new Element("Row1");

		DateTimeUtil.getCurrentDateTimeByFormat("yyyymmdd");

		Supplier supplier = supplierManager.getSupplier(Integer.parseInt(supplierId));
		String lang = supplier.getLang().getCode_2letter();
		String country = supplier.getCountry().getCode_2letter();
		String current = TimeLocaleUtil.getLocaleDate(TimeUtil.getCurrentDay(),lang,country);

		String startDate = TimeLocaleUtil.getLocaleDate(searchStartDate);
		String endDate   = TimeLocaleUtil.getLocaleDate(searchEndDate);

		for (Map<String, Object> map : total) {
	        tmpBdFee = null;
	        if(map.get("TOTAL") == null) {
	        	continue;
	        }
            tmpBdFee = tmpBdUnitPrice.multiply(new BigDecimal(DecimalUtil.ConvertNumberToDouble(map.get("TOTAL"))));

			row1.setAttribute("currentDate", current);
            row1.setAttribute("zoneTotal", df.format(DecimalUtil.ConvertNumberToDouble(map.get("TOTAL"))));
            row1.setAttribute("charge", unitPrice);
			row1.setAttribute("startDate", startDate);
			row1.setAttribute("endDate", endDate);
            row1.setAttribute("FEE", df1.format(tmpBdFee.doubleValue()));
		}
		master.addContent(row1);
		xmlString = getXmlString(master);

		mav.addObject("data", xmlString);

		return mav;
	}

	/**
	 * method name : getLocationUsageReport<b/>
	 * method Desc : 위치별 사용량 보고서 정보를 조회한다.
	 *
	 * @param response
	 * @param request
	 * @param searchStartDate
	 * @param searchEndDate
	 * @param supplierId
	 * @return
	 * @throws ParseException
	 */
	@SuppressWarnings({ "unused", "unchecked" })
    @RequestMapping(value = "/report/locationUsageReport")
	public ModelAndView getLocationUsageReport(HttpServletResponse response,
			HttpServletRequest request,
			@RequestParam("searchStartDate") String searchStartDate,
			@RequestParam("searchEndDate") String searchEndDate,
			@RequestParam("supplierId") String supplierId) throws ParseException {

		ModelAndView mav = new ModelAndView("report/reportData");
		String xmlString = "";

		String reportFileName = "locationUsageReport"
				+ searchStartDate
				+ "_"
				+ searchStartDate
				+"_"
				+ searchEndDate
				+ "."
				+ new SimpleDateFormat("yyyyMMdd").format(Calendar
						.getInstance().getTime());

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("supplierId", supplierId);
		params.put("startDate", searchStartDate);
		params.put("endDate", searchEndDate);
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap = emsReportManager.getLocationUsageInfo(params);

		List<Map<String,Object>> locationTotal = new ArrayList<Map<String,Object>>();
		List<Map<String,Object>> total = new ArrayList<Map<String,Object>>();

		locationTotal = (List<Map<String, Object>>) resultMap.get("locationTotal");
		total = (List<Map<String, Object>>) resultMap.get("total");

		Properties prop = new Properties();

		try {
			prop.load(getClass().getClassLoader().getResourceAsStream("bems_charge.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		Element master = new Element("LocationUsageReport");
        DecimalFormat df = new DecimalFormat("###.###");
        DecimalFormat df1 = new DecimalFormat("###,###,###");
        String unitPrice = prop.getProperty("energy.usageUnitPrice");
        BigDecimal tmpBdUnitPrice = new BigDecimal(unitPrice);
        BigDecimal tmpBdFee = null;

		for (Map<String, Object> map : locationTotal) {
			tmpBdFee = null;
			tmpBdFee = tmpBdUnitPrice.multiply(new BigDecimal(DecimalUtil.ConvertNumberToDouble(map.get("TOTAL"))));

            Element row = new Element("Row");
			row.setAttribute("TOTAL", df.format(DecimalUtil.ConvertNumberToDouble(map.get("TOTAL"))));
			row.setAttribute("NAME", String.valueOf(map.get("NAME")));
            row.setAttribute("charge", unitPrice);
            row.setAttribute("FEE", df1.format(tmpBdFee.doubleValue()));
			master.addContent(row);
		}

		Element row1 = new Element("Row1");

		DateTimeUtil.getCurrentDateTimeByFormat("yyyymmdd");

		Supplier supplier = supplierManager.getSupplier(Integer.parseInt(supplierId));
		String lang = supplier.getLang().getCode_2letter();
		String country = supplier.getCountry().getCode_2letter();
		String current = TimeLocaleUtil.getLocaleDate(TimeUtil.getCurrentDay(),lang,country);

		String startDate = TimeLocaleUtil.getLocaleDate(searchStartDate);
		String endDate   = TimeLocaleUtil.getLocaleDate(searchEndDate);

		for (Map<String, Object> map : total) {
		    tmpBdFee = null;
		    tmpBdFee = tmpBdUnitPrice.multiply(new BigDecimal(DecimalUtil.ConvertNumberToDouble(map.get("TOTAL"))));

			row1.setAttribute("currentDate", current);
            row1.setAttribute("zoneTotal", df.format(DecimalUtil.ConvertNumberToDouble(map.get("TOTAL"))));
            row1.setAttribute("charge", unitPrice);
			row1.setAttribute("startDate", startDate);
			row1.setAttribute("endDate", endDate);
            row1.setAttribute("FEE", df1.format(tmpBdFee.doubleValue()));
		}
		master.addContent(row1);
		xmlString = getXmlString(master);

		mav.addObject("data", xmlString);

		return mav;
	}

	@Deprecated
	public boolean isExistFile(HttpServletRequest request, String filename) {
		String reportFilePath = "/EmsReport/";
		String destDir = request.getSession().getServletContext()
				.getRealPath(reportFilePath);
		try {
			File dirPath = new File(destDir);
			if (!dirPath.exists()) {
				boolean created = dirPath.mkdirs();
				if (!created) {
					throw new Exception(
							"Fail to create a directory for product image. ["
									+ destDir + "]");
				}
			}
			File file = new File(destDir, filename + ".rpt");
			if (!file.exists()) {
				try {
					file.createNewFile();
					return false;
				} catch (IOException io) {
					io.printStackTrace();
				}
			} else {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

    public File getReportFile(HttpServletRequest request, String filename) {
        String reportFilePath = "/EmsReport/";
        String destDir = request.getSession().getServletContext().getRealPath(reportFilePath);

        File fileDir = new File(destDir);
        File file = null;

        try {
            if (!fileDir.exists()) {
                fileDir.mkdir();
            }
            file = new File(destDir, filename + ".rpt");
        } catch (Exception e) {
            e.printStackTrace();
            
        }

        return file;
    }
}
