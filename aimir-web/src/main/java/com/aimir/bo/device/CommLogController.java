package com.aimir.bo.device;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.constants.CommonConstants.DateType;
import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.model.device.CommLog;
import com.aimir.model.system.Code;
import com.aimir.service.device.CommLogManager;
import com.aimir.service.system.CodeManager;
import com.aimir.service.system.LocationManager;
import com.aimir.util.CalendarUtil;
import com.aimir.util.CommLogMakeExcel;
import com.aimir.util.TimeUtil;
import com.aimir.util.ZipUtils;

@Controller
public class CommLogController
{

    protected static Log logger = LogFactory.getLog(CommLogController.class);	

	@Autowired
	LocationManager locationManager;

	@Autowired
	CommLogManager commLogManager;

	@Autowired
	CodeManager codeManager;

	/**
	 * flex action
	 * @return
	 */
	@Deprecated
	@RequestMapping(value = "/gadget/device/commLog/commLogMaxGadget2")
	public ModelAndView getCommLogMaxGadget2()
	{
		ModelAndView mav = new ModelAndView("/gadget/device/commLogMaxGadget");
		mav.addObject("protocolCodes", codeManager.getChildCodes(Code.PROTOCOL));
		return mav;
	}
	
/*	public String addInspectionType(InspectionType inspectionType,		HttpSession session)
	{
		User user = (User) session.getAttribute("user");
		System.out.println("User: " + user.getUserDetails().getFirstName);

	}*/

	/**
	 * 
	 * for extjs
	 * 
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/gadget/device/commLog/commLogMaxGadget")
	public ModelAndView getCommLogMaxGadget() {
		ModelAndView mav = new ModelAndView("/gadget/device/commLogMaxGadget2");

		//패킷 타입 fetch
		List packetTypeList = commLogManager.getPacketType();

		mav.addObject("packetTypeList", packetTypeList);
		mav.addObject("protocolCodes", codeManager.getChildCodes(Code.PROTOCOL));
		mav.addObject("senderTypes", commLogManager.getSenderType());

//		String supplierId = (String) session.getAttribute("sesSupplierId");
//		mav.addObject("supplierId", supplierId);

        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();
        Integer supplierId = user.getRoleData().getSupplier().getId();

        mav.addObject("supplierId", supplierId);

		return mav;
	}

	@RequestMapping(value = "/gadget/device/commLog/getReceivePieChartData")
	public ModelAndView getReceivePieChartData(

	@RequestParam("protocolCode") String protocolCode,
			@RequestParam("senderId") String senderId,
			@RequestParam("receiverId") String receiverId,
			@RequestParam("period") String period,
			@RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate,
			@RequestParam("group") String group,
			@RequestParam("groupData") String groupData,
			@RequestParam("svcTypeCode") String svcTypeCode,
			@RequestParam("supplierId") String supplierId)
	{

		Map<String, String> conditionMap = new HashMap<String, String>();
		conditionMap.put("protocolCode", protocolCode);
		conditionMap.put("senderId", senderId);
		conditionMap.put("receiverId", receiverId);
		conditionMap.put("group", group);
		conditionMap.put("groupData", groupData);
		conditionMap.put("svcTypeCode", svcTypeCode);
		conditionMap.put("supplierId", supplierId);

		if (startDate.length() == 0 && endDate.length() == 0)
		{
			conditionMap.put("period", DateType.HOURLY.getCode());
			try
			{
				conditionMap.put("startDate",
						new StringBuilder().append(TimeUtil.getCurrentDay())
								.append("00").toString());
				conditionMap.put("endDate",
						new StringBuilder().append(TimeUtil.getCurrentDay())
								.append("23").toString());
			} catch (ParseException e)
			{
				e.printStackTrace();
			}
		} else
		{
			conditionMap.put("period", period);
			conditionMap.put("startDate", startDate.replace("/", ""));
			conditionMap.put("endDate", endDate.replace("/", ""));
		}

		ModelMap model = new ModelMap("chartDatas",
				commLogManager.getReceivePieChart(conditionMap));
		return new ModelAndView("jsonView", model);
	}

	@RequestMapping(value = "/gadget/device/commLog/getBarChartData")
	public ModelAndView getBarChartData(

	@RequestParam("protocolCode") String protocolCode,
			@RequestParam("senderId") String senderId,
			@RequestParam("receiverId") String receiverId,
			@RequestParam("period") String period,
			@RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate,
			@RequestParam("group") String group,
			@RequestParam("groupData") String groupData,
			@RequestParam("svcTypeCode") String svcTypeCode,
			@RequestParam("supplierId") String supplierId)
	{

		Map<String, String> conditionMap = new HashMap<String, String>();
		conditionMap.put("protocolCode", protocolCode);
		conditionMap.put("senderId", senderId);
		conditionMap.put("receiverId", receiverId);
		conditionMap.put("group", group);
		conditionMap.put("groupData", groupData);
		conditionMap.put("svcTypeCode", svcTypeCode);
		conditionMap.put("supplierId", supplierId);

		if (startDate.length() == 0 && endDate.length() == 0)
		{
			conditionMap.put("period", DateType.HOURLY.getCode());
			try
			{
				conditionMap.put("startDate",	new StringBuilder().append(TimeUtil.getCurrentDay()).append("00").toString());
				conditionMap.put("endDate",		new StringBuilder().append(TimeUtil.getCurrentDay()).append("23").toString());
			} catch (ParseException e)
			{
				e.printStackTrace();
			}
		} else
		{
			conditionMap.put("period", period);
			conditionMap.put("startDate", startDate.replace("/", ""));
			conditionMap.put("endDate", endDate.replace("/", ""));
		}
		
		List<Map<String, Object>> barchart = commLogManager.getBarChart(conditionMap);

		ModelMap model = new ModelMap();
		
		model.addAttribute("chartDatas", barchart);

		return new ModelAndView("jsonView", model);
	}

	@RequestMapping(value = "/gadget/device/commLog/getPieChartData")
	public ModelAndView getPieChartData(

	@RequestParam("protocolCode") String protocolCode,
			@RequestParam("senderId") String senderId,
			@RequestParam("receiverId") String receiverId,
			@RequestParam("period") String period,
			@RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate,
			@RequestParam("group") String group,
			@RequestParam("groupData") String groupData,
			@RequestParam("svcTypeCode") String svcTypeCode)
	{

		Map<String, String> conditionMap = new HashMap<String, String>();
		conditionMap.put("protocolCode", protocolCode);
		conditionMap.put("senderId", senderId);
		conditionMap.put("receiverId", receiverId);
		conditionMap.put("period", period);
		conditionMap.put("startDate", startDate.replace("/", ""));
		conditionMap.put("endDate", endDate.replace("/", ""));
		conditionMap.put("group", group);
		conditionMap.put("groupData", groupData);
		conditionMap.put("svcTypeCode", svcTypeCode);

		ModelMap model = new ModelMap("chartDatas",
				commLogManager.getPieChartData(conditionMap));

		return new ModelAndView("jsonView", model);
	}
	
	@Deprecated
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/gadget/device/commLog/getCommLogGridData")
	public ModelAndView getCommLogGridData(

	@RequestParam("protocolCode") String protocolCode,
			@RequestParam("senderId") String senderId,
			@RequestParam("receiverId") String receiverId,
			@RequestParam("period") String period,
			@RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate,
			@RequestParam("group") String group,
			@RequestParam("groupData") String groupData,
			@RequestParam("svcTypeCode") String svcTypeCode,
			@RequestParam("page") String page,
			@RequestParam("pageSize") String pageSize,
			@RequestParam("supplierId") String supplierId)
	{

		Map<String, String> conditionMap = new HashMap<String, String>();
		conditionMap.put("protocolCode", protocolCode);
		conditionMap.put("senderId", senderId);
		conditionMap.put("receiverId", receiverId);
		conditionMap.put("group", group);
		conditionMap.put("groupData", groupData);
		conditionMap.put("svcTypeCode", svcTypeCode);
		conditionMap.put("page", page);
		conditionMap.put("pageSize", pageSize);
		conditionMap.put("supplierId", supplierId);

		if (startDate.length() == 0 && endDate.length() == 0)
		{
			conditionMap.put("period", DateType.HOURLY.getCode());
			try
			{
				conditionMap.put("startDate",
						new StringBuilder().append(TimeUtil.getCurrentDay())
								.append("00").toString());
				conditionMap.put("endDate",
						new StringBuilder().append(TimeUtil.getCurrentDay())
								.append("23").toString());
			} catch (ParseException e)
			{
				e.printStackTrace();
			}
		} else
		{
			conditionMap.put("period", period);
			conditionMap.put("startDate", startDate.replace("/", ""));
			conditionMap.put("endDate", endDate.replace("/", ""));
		}

		ModelMap model = new ModelMap();

		@SuppressWarnings("rawtypes")
		List<CommLog> listcommlog = new ArrayList();

		listcommlog = commLogManager.getCommLogGridData(conditionMap);

		model.addAttribute("gridDatas", listcommlog);

		// stat데이타 fetch
		Map<String, String> commlogstatisticsdata = null;

		commlogstatisticsdata = commLogManager
				.getCommLogStatisticsData(conditionMap);

		model.addAttribute("statisticsData", commlogstatisticsdata);

		return new ModelAndView("jsonView", model);
	}

	/**
	 * @desc 그리드 데이타를 가지고 온다.for extJs
	 * @param protocolCode
	 * @param senderId
	 * @param receiverId
	 * @param period
	 * @param startDate
	 * @param endDate
	 * @param group
	 * @param groupData
	 * @param svcTypeCode
	 * @param page
	 * @param pageSize
	 * @param supplierId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/gadget/device/commLog/getCommLogGridData2")
	public ModelAndView getCommLogGridData2(

	@RequestParam("protocolCode") String protocolCode,
			@RequestParam("senderId") String senderId,
			@RequestParam("receiverId") String receiverId,
			@RequestParam("period") String period,
			@RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate,
			@RequestParam("group") String group,
			@RequestParam("groupData") String groupData,
			@RequestParam("svcTypeCode") String svcTypeCode,
			@RequestParam("page") String page,
			@RequestParam("pageSize") String pageSize,
			@RequestParam("supplierId") String supplierId)
	{

		Map<String, String> conditionMap = new HashMap<String, String>();

		// 검색 조건 셋팅 부분.
		conditionMap.put("protocolCode", protocolCode);
		conditionMap.put("senderId", senderId);
		conditionMap.put("receiverId", receiverId);

		conditionMap.put("period", period);
		conditionMap.put("startDate", startDate);
		conditionMap.put("endDate", endDate);

		conditionMap.put("page", page);

		conditionMap.put("group", group);
		conditionMap.put("groupData", groupData);
		
		//패킷타입
		conditionMap.put("svcTypeCode", svcTypeCode);

		conditionMap.put("pageSize", "10");
		conditionMap.put("supplierId", supplierId);

		if (startDate.length() == 0 && endDate.length() == 0)
		{
			conditionMap.put("period", DateType.HOURLY.getCode());
			try
			{
				conditionMap.put("startDate",
						new StringBuilder().append(TimeUtil.getCurrentDay())
								.append("00").toString());
				conditionMap.put("endDate",
						new StringBuilder().append(TimeUtil.getCurrentDay())
								.append("23").toString());
			} catch (ParseException e)
			{
				e.printStackTrace();
			}
		} else
		{
			conditionMap.put("period", period);
			conditionMap.put("startDate", startDate.replace("/", ""));
			conditionMap.put("endDate", endDate.replace("/", ""));
		}

//		ModelMap model = new ModelMap();
		ModelAndView mav = new ModelAndView("jsonView");

		// comm log datalist fetch from model
		List<CommLog> listcommlog = commLogManager.getCommLogGridData2(conditionMap);
		
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        Map<String, Object> resultMap = null;

        for (CommLog comm : listcommlog) {
            resultMap = new HashMap<String, Object>();

            resultMap.put("idx1", comm.getIdx1());
            resultMap.put("time", comm.getTime());
//            resultMap.put("svcTypeCode.descr", (comm.getSvcTypeCode() == null) ? "" : comm.getSvcTypeCode().getDescr());
//            resultMap.put("protocolCode.descr", (comm.getProtocolCode() == null) ? "" : comm.getProtocolCode().getDescr());
            resultMap.put("svcTypeCode", (comm.getSvcTypeCode() == null) ? "" : comm.getSvcTypeCode().getDescr());
            resultMap.put("protocolCode", (comm.getProtocolCode() == null) ? "" : comm.getProtocolCode().getDescr());
            resultMap.put("senderId", comm.getSenderId());
            resultMap.put("sender", comm.getSenderId());
            resultMap.put("receiver", ((comm.getReceiverTypeCode() == null) ? "" : comm.getReceiverTypeCode().getDescr()) + "[" + comm.getReceiverId() + "]");
            resultMap.put("result", comm.getResult());
            resultMap.put("strSendBytes", comm.getStrSendBytes());
            resultMap.put("strReceiverBytes", comm.getStrReceiverBytes());
            resultMap.put("strTotalCommTime", comm.getStrTotalCommTime());
            resultMap.put("operationCode", comm.getOperationCode());

            result.add(resultMap);
        }

		String commloggriddatacount = commLogManager.getCommLogGridDataCount(conditionMap);

//		model.addAttribute("commloggriddatacount", commloggriddatacount);
//		model.addAttribute("listcommlog", result);
		mav.addObject("commloggriddatacount", commloggriddatacount);
		mav.addObject("listcommlog", result);

//		/**
//		 * comlog stat data fetch
//		 */
//		Map<String, String> commlogstatisticsdata = null;
//		commlogstatisticsdata = commLogManager.getCommLogStatisticsData(conditionMap);
//		model.addAttribute("statisticsData", commlogstatisticsdata);
//		mav.addObject("statisticsData", commlogstatisticsdata);

//		return new ModelAndView("jsonView", model);
		return mav;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/gadget/device/commLog/getPacketType")
	public ModelAndView getPacketType() {

		ModelMap model = new ModelMap();

		@SuppressWarnings("rawtypes")
		List<Code> packetTypeList = new ArrayList();

		packetTypeList = commLogManager.getPacketType();
	
		model.addAttribute("packetTypeList", packetTypeList);

		return new ModelAndView("jsonView", model);
	}

	/**
	 * @desc comm log stat 데이타 가져오는 메소드
	 * @param protocolCode
	 * @param senderId
	 * @param receiverId
	 * @param period
	 * @param startDate
	 * @param endDate
	 * @param group
	 * @param groupData
	 * @param svcTypeCode
	 * @param page
	 * @param pageSize
	 * @param supplierId
	 * @return
	 */
	@RequestMapping(value = "/gadget/device/commLog/getCommLogStatData")
	public ModelAndView getCommLogStatData(

	@RequestParam("protocolCode") String protocolCode,
			@RequestParam("senderId") String senderId,
			@RequestParam("receiverId") String receiverId,
			@RequestParam("period") String period,
			@RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate,
			@RequestParam("group") String group,
			@RequestParam("groupData") String groupData,
			@RequestParam("svcTypeCode") String svcTypeCode,
			// @RequestParam("page") String page,
			// @RequestParam("pageSize") String pageSize,
			@RequestParam("supplierId") String supplierId)
	{

		Map<String, String> conditionMap = new HashMap<String, String>();

		// 검색 조건 셋팅 부분.
		conditionMap.put("protocolCode", protocolCode);
		conditionMap.put("senderId", senderId);
		conditionMap.put("receiverId", receiverId);

		conditionMap.put("period", period);
		conditionMap.put("startDate", startDate);
		conditionMap.put("endDate", endDate);

		// conditionMap.put("page", page);
		conditionMap.put("group", group);
		conditionMap.put("groupData", groupData);
		conditionMap.put("svcTypeCode", svcTypeCode);

		// conditionMap.put("pageSize", "10");
		conditionMap.put("supplierId", supplierId);

		if (startDate.length() == 0 && endDate.length() == 0)
		{
			conditionMap.put("period", DateType.HOURLY.getCode());
			try
			{
				conditionMap.put("startDate",
						new StringBuilder().append(TimeUtil.getCurrentDay())
								.append("00").toString());
				conditionMap.put("endDate",
						new StringBuilder().append(TimeUtil.getCurrentDay())
								.append("23").toString());
			} catch (ParseException e)
			{
				e.printStackTrace();
			}
		} else
		{
			conditionMap.put("period", period);
			conditionMap.put("startDate", startDate.replace("/", ""));
			conditionMap.put("endDate", endDate.replace("/", ""));
		}

		ModelMap model = new ModelMap();

		/**
		 * stat데이타 가져오는 부분.
		 */
		Map<String, String> commlogstatisticsdata = null;
		commlogstatisticsdata = commLogManager.getCommLogStatisticsData(conditionMap);

		model.addAttribute("statisticsData", commlogstatisticsdata);

		return new ModelAndView("jsonView", model);
	}
	
	@RequestMapping(value = "/gadget/device/commLog/getCommLogGridDataCount")
	public ModelAndView getCommLogGridDataCount(

	@RequestParam("protocolCode") String protocolCode,
			@RequestParam("senderId") String senderId,
			@RequestParam("receiverId") String receiverId,
			@RequestParam("period") String period,
			@RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate,
			@RequestParam("group") String group,
			@RequestParam("groupData") String groupData,
			@RequestParam("svcTypeCode") String svcTypeCode,
			@RequestParam("supplierId") String supplierId)
	{

		Map<String, String> conditionMap = new HashMap<String, String>();
		conditionMap.put("protocolCode", protocolCode);
		conditionMap.put("senderId", senderId);
		conditionMap.put("receiverId", receiverId);
		conditionMap.put("group", group);
		conditionMap.put("groupData", groupData);
		conditionMap.put("svcTypeCode", svcTypeCode);
		conditionMap.put("supplierId", supplierId);

		if (startDate.length() == 0 && endDate.length() == 0)
		{
			conditionMap.put("period", DateType.HOURLY.getCode());
			try
			{
				conditionMap.put("startDate",
						new StringBuilder().append(TimeUtil.getCurrentDay())
								.append("00").toString());
				conditionMap.put("endDate",
						new StringBuilder().append(TimeUtil.getCurrentDay())
								.append("23").toString());
			} catch (ParseException e)
			{
				e.printStackTrace();
			}
		} else
		{
			conditionMap.put("period", period);
			conditionMap.put("startDate", startDate.replace("/", ""));
			conditionMap.put("endDate", endDate.replace("/", ""));
		}

		ModelMap model = new ModelMap();
		model.addAttribute("total",
				commLogManager.getCommLogGridDataCount(conditionMap));

		return new ModelAndView("jsonView", model);
	}

	@RequestMapping(value = "/gadget/device/commLog/getLocations")
	public ModelAndView getLocations()
	{

		// ESAPI.setAuthenticator(new AimirAuthenticator());
		AimirAuthenticator instance = (AimirAuthenticator) ESAPI
				.authenticator();
		AimirUser user = (AimirUser) instance.getUserFromSession();

		int supplierId = user.getRoleData().getSupplier().getId();

		ModelMap model = new ModelMap("locations",
				locationManager.getParentsBySupplierId(supplierId));

		return new ModelAndView("jsonView", model);
	}

	@RequestMapping(value = "/gadget/device/commLog/getLocationLineChartData")
	public ModelAndView getLocationLineChartData(

	@RequestParam("protocolCode") String protocolCode,
			@RequestParam("senderId") String senderId,
			@RequestParam("receiverId") String receiverId,
			@RequestParam("period") String period,
			@RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate,
			@RequestParam("group") String group,
			@RequestParam("groupData") String groupData,
			@RequestParam("svcTypeCode") String svcTypeCode,
			@RequestParam("supplierId") String supplierId)
	{

		Map<String, String> conditionMap = new HashMap<String, String>();
		conditionMap.put("protocolCode", protocolCode);
		conditionMap.put("senderId", senderId);
		conditionMap.put("receiverId", receiverId);
		conditionMap.put("period", period);
		conditionMap.put("startDate", startDate.replace("/", ""));
		conditionMap.put("endDate", endDate.replace("/", ""));
		conditionMap.put("group", group);
		conditionMap.put("groupData", groupData);
		conditionMap.put("svcTypeCode", svcTypeCode);
		conditionMap.put("supplierId", supplierId);

		ModelMap model = new ModelMap("chartDatas",
				commLogManager.getLocationLineChartData(conditionMap));

		return new ModelAndView("jsonView", model);
	}

	@RequestMapping(value = "/gadget/device/commLog/getMcuLineChartData")
	public ModelAndView getMcuLineChartData(

	@RequestParam("protocolCode") String protocolCode,
			@RequestParam("senderId") String senderId,
			@RequestParam("receiverId") String receiverId,
			@RequestParam("period") String period,
			@RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate,
			@RequestParam("group") String group,
			@RequestParam("groupData") String groupData,
			@RequestParam("svcTypeCode") String svcTypeCode,
			@RequestParam("supplierId") String supplierId)
	{

		Map<String, String> conditionMap = new HashMap<String, String>();
		conditionMap.put("protocolCode", protocolCode);
		conditionMap.put("senderId", senderId);
		conditionMap.put("receiverId", receiverId);
		conditionMap.put("period", period);
		conditionMap.put("startDate", startDate.replace("/", ""));
		conditionMap.put("endDate", endDate.replace("/", ""));
		conditionMap.put("group", group);
		conditionMap.put("groupData", groupData);
		conditionMap.put("svcTypeCode", svcTypeCode);
		conditionMap.put("supplierId", supplierId);

		ModelMap model = new ModelMap("chartDatas",
				commLogManager.getMcuLineChartData(conditionMap));

		return new ModelAndView("jsonView", model);
	}

	@RequestMapping(value = "/gadget/device/commLog/getLocationPieChartData")
	public ModelAndView getLocationPieChartData(

	@RequestParam("protocolCode") String protocolCode,
			@RequestParam("senderId") String senderId,
			@RequestParam("receiverId") String receiverId,
			@RequestParam("period") String period,
			@RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate,
			@RequestParam("group") String group,
			@RequestParam("groupData") String groupData,
			@RequestParam("svcTypeCode") String svcTypeCode)
	{

		Map<String, String> conditionMap = new HashMap<String, String>();
		conditionMap.put("protocolCode", protocolCode);
		conditionMap.put("senderId", senderId);
		conditionMap.put("receiverId", receiverId);
		conditionMap.put("period", period);
		conditionMap.put("startDate", startDate.replace("/", ""));
		conditionMap.put("endDate", endDate.replace("/", ""));
		conditionMap.put("group", group);
		conditionMap.put("groupData", groupData);
		conditionMap.put("svcTypeCode", svcTypeCode);

		ModelMap model = new ModelMap("chartDatas",
				commLogManager.getLocationPieChartData(conditionMap));

		return new ModelAndView("jsonView", model);
	}

	@RequestMapping(value = "/gadget/device/commLog/getMcuPieChartData")
	public ModelAndView getMcuPieChartData(

	@RequestParam("protocolCode") String protocolCode,
			@RequestParam("senderId") String senderId,
			@RequestParam("receiverId") String receiverId,
			@RequestParam("period") String period,
			@RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate,
			@RequestParam("group") String group,
			@RequestParam("groupData") String groupData,
			@RequestParam("svcTypeCode") String svcTypeCode)
	{

		Map<String, String> conditionMap = new HashMap<String, String>();
		conditionMap.put("protocolCode", protocolCode);
		conditionMap.put("senderId", senderId);
		conditionMap.put("receiverId", receiverId);
		conditionMap.put("period", period);
		conditionMap.put("startDate", startDate.replace("/", ""));
		conditionMap.put("endDate", endDate.replace("/", ""));
		conditionMap.put("group", group);
		conditionMap.put("groupData", groupData);
		conditionMap.put("svcTypeCode", svcTypeCode);

		ModelMap model = new ModelMap("chartDatas",
				commLogManager.getMcuPieChartData(conditionMap));

		return new ModelAndView("jsonView", model);
	}

	@RequestMapping(value = "/gadget/device/commLog/commLogMiniGadget")
	public ModelAndView getCommLogMiniGadget()
	{

		return new ModelAndView("/gadget/device/commLogMiniGadget");
	}

    /**
     * method name : commLogSendRevceive<b/>
     * method Desc : method 단어 오타 수정. 호환성을 위해 기존 method 명 남겨둠.
     *
     * @param supplierId
     * @return
     */
    @RequestMapping(value = "/gadget/device/commLog/commLogSendRevceiveChartData")
    @Deprecated
    public ModelAndView commLogSendRevceive(@RequestParam("supplierId") String supplierId) {

        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("chartMapDatas", commLogManager.getSendRevceiveChartData(supplierId));

        return mav;
    }

    @RequestMapping(value = "/gadget/device/commLog/commLogSendReceiveChartData")
    public ModelAndView commLogSendReceive(@RequestParam("supplierId") String supplierId) {

        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("chartMapDatas", commLogManager.getSendReceiveChartData(supplierId));

        return mav;
    }

	@RequestMapping(value = "/gadget/device/commLog/commLogSVCTypeChartData")
	public ModelAndView commLogSVCType(
			@RequestParam("supplierId") String supplierId)
	{

		ModelAndView mav = new ModelAndView("jsonView");
		mav.addObject("chartMapDatas",
				commLogManager.getSVCTypeChartData(supplierId));

		return mav;
	}

	@RequestMapping(value = "/gadget/device/commLog/commLogLocationChartData")
	public ModelAndView commLogLocation()
	{
		return new ModelAndView("jsonView", new ModelMap("chartDatas",
				commLogManager.getLocationChartData()));
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/gadget/device/commLog/commLogExcelDownloadPopup")
	public ModelAndView commLogExcelDownloadPopup()
	{
		// ESAPI.setAuthenticator(new AimirAuthenticator());
		AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
		AimirUser user = (AimirUser) instance.getUserFromSession();
		Integer supplierId = user.getRoleData().getSupplier().getId();

		//ModelAndView mav = new ModelAndView("/gadget/device/commLogMaxGadget");

		ModelAndView mav = new ModelAndView("/gadget/device/commLogExcelDownloadPopup");

		mav.addObject("supplierId", supplierId);

		return mav;
	}

	/*
	 * private String toChartXML(List<CommLogChartVO> chartData) 
	 {
	 * 
	 * StringBuffer xml = new StringBuffer("<items>");
	 * 
	 * for (CommLogChartVO vo : chartData) { xml.append("<item date=\"" +
	 * vo.getDate() + "\" rcvCnt=\"" + vo.getRcvCnt() + "\" sendCnt=\"" +
	 * vo.getSendCnt() + "\" />"); }
	 * 
	 * xml.append("</items>");
	 * 
	 * return xml.toString(); }
	 * 
	 * */

    @RequestMapping(value="gadget/device/commLog/commLogExcelMake")
    public ModelAndView commLogExcelMake (
    		
    		@RequestParam("supplierId") String supplierId,
    	//	@RequestParam("tabName") String tabName,
    		@RequestParam("tabType") String tabType,
    		@RequestParam("search_from") String search_from,
    		@RequestParam("svcTypeCode") String svcTypeCode,
    		@RequestParam("protocolCode") String protocolCode,
    		@RequestParam("senderId") String senderId,
    		@RequestParam("receiverId") String receiverId,
    		@RequestParam("hourlyStartDate") String hourlyStartDate,
    		@RequestParam("hourlyEndDate") String hourlyEndDate,
    		@RequestParam("hourlyStartHourCombo_input") String hourlyStartHourCombo_input,
    		@RequestParam("hourlyEndHourCombo_input") String hourlyEndHourCombo_input,
    		@RequestParam("periodType_input") String periodType_input,
    		@RequestParam("periodStartDate") String periodStartDate,
    		@RequestParam("periodEndDate") String periodEndDate,
    		@RequestParam("weeklyYearCombo_input") String weeklyYearCombo_input,
    		@RequestParam("weeklyMonthCombo_input") String weeklyMonthCombo_input,
    		@RequestParam("weeklyWeekCombo_input") String weeklyWeekCombo_input,
    		@RequestParam("monthlyYearCombo_input") String monthlyYearCombo_input,
    		@RequestParam("monthlyMonthCombo_input") String monthlyMonthCombo_input,
    		
    		@RequestParam("msg_time") String msg_time,
    		@RequestParam("msg_datatype") String msg_datatype,
    		@RequestParam("msg_protocol") String msg_protocol,
    		@RequestParam("msg_sender") String msg_sender,
    		@RequestParam("msg_receiver") String msg_receiver,
    		@RequestParam("msg_sendbytes") String msg_sendbytes,
    		@RequestParam("msg_receivebytes") String msg_receivebytes,
    		@RequestParam("msg_result") String msg_result,
    		@RequestParam("msg_totalcommtime") String msg_totalcommtime,
    		//msg_operationcode
    		@RequestParam("msg_operationcode") String msg_operationcode,
    		@RequestParam("filePath") String filePath
    		
    		
    		) 
	{
		
        ModelAndView mav = new ModelAndView("jsonView");        
        List<CommLog> result = null;
        Map<String, String> msgMap = new HashMap<String, String>();
        List<String> fileNameList = new ArrayList<String>();
//        List<MeteringListData> list = new ArrayList<MeteringListData>();
        List<CommLog> list = new ArrayList<CommLog> ();

        StringBuilder sbFileName = new StringBuilder();
        StringBuilder sbSplFileName = new StringBuilder();

        boolean isLast = false;
        Long total = 0L;        // 데이터 조회건수
        Long maxRows = 5000L;   // excel sheet 하나에 보여줄 수 있는 최대 데이터 row 수

        final String dayWeekPrefix = "CommDataDayWeek";    //19글자
        final String seasonPrefix  = "CommDataSeason";     //18글자
        final String yearPrefix    = "CommDataYear";       //16글자
        final String hourPrefix    = "commLogDataHourly";       //16글자
        final String dayPrefix     = "commLogDataDay";        //15글자
        final String weekPrefix    = "commLogDataWeek";       //16글자
        final String monthPrefix   = "commLogDataMonth";      //17글자

        Map<String, String> conditionMap = new HashMap<String, String>();

        conditionMap.put("tabType",tabType );
        conditionMap.put("search_from",search_from );
        //
        conditionMap.put("supplierId",supplierId );
       // conditionMap.put("tabName",tabName );
    	conditionMap.put("svcTypeCode", svcTypeCode);
    	conditionMap.put("protocolCode", protocolCode);
    	conditionMap.put("senderId", senderId);
    	conditionMap.put("hourlyStartDate", hourlyStartDate);
    	conditionMap.put("hourlyEndDate", hourlyEndDate);
    	conditionMap.put("hourlyStartHourCombo_input", hourlyStartHourCombo_input);
    	conditionMap.put("hourlyEndHourCombo_input", hourlyEndHourCombo_input);
    	conditionMap.put("periodType_input", periodType_input);
    	conditionMap.put("periodStartDate", periodStartDate);
    	conditionMap.put("periodEndDate", periodEndDate);
    	conditionMap.put("weeklyYearCombo_input", weeklyYearCombo_input);
    	conditionMap.put("weeklyMonthCombo_input", weeklyMonthCombo_input);
    	conditionMap.put("weeklyWeekCombo_input", weeklyWeekCombo_input);
    	conditionMap.put("monthlyYearCombo_input", monthlyYearCombo_input);
    	conditionMap.put("monthlyMonthCombo_input", monthlyMonthCombo_input);
    	
       // String meterType = ChangeMeterTypeName.valueOf(mvmMiniType).getCode();
        //conditionMap.put("meterType", meterType);
        //String tlbType = MeterType.valueOf(meterType).getLpClassName();
        //conditionMap.put("tlbType", tlbType);

        DateType dateType = null;
        
       // String tabName2 = tabName;
        
        String searchDateType= tabType; 
        
        /*HOURLY("0"),           *//** 시간별 *//*
        DAILY("1"),            *//** 일별 *//*
        PERIOD("2"),           *//** 기간별 *//*
        WEEKLY("3"),           *//** 주별 */
        
        if ( tabType.equals("hour"))
        	searchDateType="0";
        if ( tabType.equals("period"))
        	searchDateType="2";
        if ( tabType.equals("week"))
        	searchDateType="3";
        if ( tabType.equals("month"))
        	searchDateType="4";
        
        for (DateType obj : DateType.values())
        {
            if (obj.getCode().equals(searchDateType)) {
                dateType = obj;
                break;
            }
        }

        conditionMap.put("period", searchDateType.toString());
        
        switch(dateType) 
        {
            case HOURLY:

                result = commLogManager.getCommLogGridDataForExcel(conditionMap);
                
                sbFileName.append(hourPrefix);
                break;
            case PERIOD:

                result = commLogManager.getCommLogGridDataForExcel(conditionMap);
                sbFileName.append(dayPrefix);
                break;
            case WEEKLY:

                result = commLogManager.getCommLogGridDataForExcel(conditionMap);
                sbFileName.append(weekPrefix);
                break;
            case MONTHLY:

                result = commLogManager.getCommLogGridDataForExcel(conditionMap);
                sbFileName.append(monthPrefix);
                break;

        }

        //가져온 데이터의 총갯수를 계산.
        total = new Integer(result.size()).longValue();
        
        mav.addObject("total", total);
        
        if (total <= 0) 
        {
            return mav;
        }

        sbFileName.append(TimeUtil.getCurrentTimeMilli());
        
        // message 생성(파라매터에서 날라온 값으로 설정해준다)
        msgMap.put("msg_time", msg_time);
    	msgMap.put("msg_datatype", msg_datatype);
    	msgMap.put("msg_protocol", msg_protocol);
    	msgMap.put("msg_sender", msg_sender);
    	msgMap.put("msg_receiver", msg_receiver);
    	msgMap.put("msg_sendbytes", msg_sendbytes);
    	msgMap.put("msg_receivebytes", msg_receivebytes);
    	msgMap.put("msg_result", msg_result);
    	msgMap.put("msg_totalcommtime", msg_totalcommtime);
    	msgMap.put("msg_operationcode", msg_operationcode);
    	msgMap.put("filePath", filePath);
    	
        // check download dir
        // check download dir
        // check download dir
        // check download dir
    	
    	
        //filePath  = "d://temp";
        
    	File downDir = new File(filePath);

        if (downDir.exists()) {
            File[] files = downDir.listFiles();

            if (files != null) {
                String filename = null;
                String deleteDate = null;

                try {
                    deleteDate = CalendarUtil.getDate(TimeUtil.getCurrentDay(), Calendar.DAY_OF_MONTH, -10);    // 10일 이전 일자
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                boolean isDel = false;

                for (File file : files) {
                    filename = file.getName();
                    isDel = false;

                    // 파일길이 : 22이상, 확장자 : xls|zip
                    if (filename.length() > 22 && (filename.endsWith("xls") || filename.endsWith("zip"))) {
                        // 10일 지난 파일들 삭제
                        if (filename.startsWith(hourPrefix) && filename.substring(16, 24).compareTo(deleteDate) < 0) {
                            isDel = true;
                        } else if (filename.startsWith(dayPrefix) && filename.substring(15, 23).compareTo(deleteDate) < 0) {
                            isDel = true;
                        } else if (filename.startsWith(dayWeekPrefix) && filename.substring(19, 27).compareTo(deleteDate) < 0) {
                            isDel = true;
                        }else if (filename.startsWith(weekPrefix) && filename.substring(16, 24).compareTo(deleteDate) < 0) {
                            isDel = true;
                        } else if (filename.startsWith(monthPrefix) && filename.substring(17, 25).compareTo(deleteDate) < 0) {
                            isDel = true;
                        }else if (filename.startsWith(seasonPrefix) && filename.substring(18, 26).compareTo(deleteDate) < 0) {
                            isDel = true;
                        } else if (filename.startsWith(yearPrefix) && filename.substring(16, 24).compareTo(deleteDate) < 0) {
                            isDel = true;
                        }

                        if (isDel) {
                            file.delete();
                        }
                    }
                    filename = null;
                }
            }
        } else {
            // directory 가 없으면 생성
           // downDir.mkdir();
        }  
        
        //////////// 수정해야되는 부분
        // create excel file
        
        // HSSFWorkbook lib을 사용해서 excel 파일로 만들어 주는 부분.
        CommLogMakeExcel wExcel = new CommLogMakeExcel();
        
        int cnt = 1;
        int idx = 0;
        int fnum = 0;
        int splCnt = 0;

        if (total <= maxRows) 
        {
            sbSplFileName = new StringBuilder();
            sbSplFileName.append(sbFileName);
            sbSplFileName.append(".xls");
            
            //엑셀 형테로 만들어준다..excel export 형태로 시트를 만들어준다.
            wExcel.writeReportExcel(result, msgMap, isLast, filePath, sbSplFileName.toString());
            
            fileNameList.add(sbSplFileName.toString());
        } 
        else 
        {
            for (int i = 0; i < total; i++) 
            {
                if ((splCnt * fnum + cnt) == total || cnt == maxRows)
                {
                    sbSplFileName = new StringBuilder();
                    sbSplFileName.append(sbFileName);
                    sbSplFileName.append('(').append(++fnum).append(").xls");

                    list = result.subList(idx, (i + 1));

                    wExcel.writeReportExcel(list, msgMap, isLast, filePath, sbSplFileName.toString());
                    fileNameList.add(sbSplFileName.toString());
                    list = null;
                    splCnt = cnt;
                    cnt = 0;
                    idx = (i + 1);
                }
                cnt++;
            }
        }

        // create zip file
        StringBuilder sbZipFile = new StringBuilder();
        sbZipFile.append(sbFileName).append(".zip");

        ZipUtils zutils = new ZipUtils();
        try
        {
            zutils.zipEntry(fileNameList, sbZipFile.toString(), filePath);
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }

        // return object
        mav.addObject("filePath", filePath);
        mav.addObject("fileName", fileNameList.get(0));
        mav.addObject("zipFileName", sbZipFile.toString());
        mav.addObject("fileNames", fileNameList);
        
        return mav;
    }
}