package com.aimir.bo.mvm;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.MeterStatus;
import com.aimir.model.device.Meter;
import com.aimir.model.system.Code;
import com.aimir.model.system.Supplier;
import com.aimir.service.device.MeterManager;
import com.aimir.service.mvm.ManualMeteringManager;
import com.aimir.service.mvm.bean.MeteringListData;
import com.aimir.service.system.CodeManager;
import com.aimir.service.system.SupplierManager;
import com.aimir.util.ExcelUtil;
import com.aimir.util.ManualMeteringDataMakeExcel;
import com.aimir.util.ParameterConverter;
import com.aimir.util.TimeUtil;
import com.aimir.util.ZipUtils;

/**
 * 수검침 관련 컨트롤러
 * 
 * @author Yi Hanghee(javarouka@gmail.com, javarouka@nuritelecom.co.kr)
 *
 */
@Controller
public class ManualMeteringController {
	
	private Logger logger = Logger.getLogger(ManualMeteringController.class);
	
	/**
	 * 의존성 객체들
	 */
	@Autowired private ManualMeteringManager manualMeteringManager;
	@Autowired private MeterManager meterManager;
	@Autowired private CodeManager codeManager;
	@Autowired private SupplierManager supplierManager;
	
	private static final String FILE_PATH = "/tmp"; // 파일명이 전달되지 않았을 때 사용
	private static final Integer ONCE_PACKAGE_ROWS = 5000; // 한번에 쓰는 엑셀 행 수
	
	/**
	 * 수검침 관리 미니가젯 로딩
	 * @return jsp View
	 */
	@RequestMapping(value="/gadget/mvm/ManualMeteringMini.do")
	public ModelAndView loadManualMeteringMiniGadget() {
		return new ModelAndView("gadget/mvm/manualMeteringMini"); 
	}
	
	/**
	 * 수검침 관리 맥스가젯 로딩
	 * @return jsp View
	 */
	@RequestMapping(value="/gadget/mvm/ManualMeteringMax.do")
	public ModelAndView loadManualMeteringMaxGadget() {
		return new ModelAndView("gadget/mvm/manualMeteringMax"); 
	}
	
	/**
	 * 엑셀 다운로드 팝업 오픈
	 * @return jsp(javascript)
	 */
	@RequestMapping(value = "/gadget/mvm/manualDownloadPopup.do")
	public ModelAndView fmtMessage() {
		ModelAndView mav = new ModelAndView("/gadget/mvm/manualDownloadPopup");
		return mav;
	}
	
	/**
	 * 메시지 프로퍼티 로드
	 * @return jsp(javascript)
	 */
	@RequestMapping(value = "/js/framework/Config/{branch}/fmtMessage.do")
	public ModelAndView fmtMessage(@PathVariable String branch) {
		ModelAndView mav = 
			new ModelAndView("/js/framework/Config/" + branch + "/fmtMessage");
		return mav;
	}
	
	/**
	 * 미터타입을 얻는다.
	 * 미터타입의 코드는 1.3.1
	 * 
	 * @return jsonView
	 * {
	 * 		"code":[
	 * 			{
	 * 				"id":"147",
	 * 				"parent":"146",
	 * 				"code":"1.3.1.1",
	 * 				"name":"EnergyMeter",
	 * 				"descr":"전기미터",
	 * 				"order":"1"
	 * 			},
	 * 			...
	 * 		]
	 * }
	 */
	@RequestMapping(value="/gadget/mvm/getMeterTypes.do")
	public ModelAndView getMeterTypes() {
		ModelAndView mav = new ModelAndView("jsonView");
		List<Code> meterType = codeManager.getChildCodes("1.3.1");
		mav.addObject(meterType);
		return mav;
	}
	
	/**
	 * 벤더리스트를 얻는다
	 * 
	 * @return jsonView
	 * {
	 * 		"code":[
	 * 			{
	 * 				"address: "......."
	 * 				"code":"146",
	 * 				"descr":"NURI Telecom Co.,Ltd.",
	 * 				"id":"2",
	 * 				"name": "NURITelecom",
	 * 				"supplier":"1"
	 * 			},
	 * 			...
	 * 		]
	 * }
	 */
	@RequestMapping(value="/gadget/mvm/getVenders.do")
	public ModelAndView getVenders() {
		ModelAndView mav = new ModelAndView("jsonView");
		List<Code> meterType = codeManager.getChildCodes("1.3.1");
		mav.addObject(meterType);
		return mav;
	}
	
	/**
	 * 수동 미터를 전부 얻는다.
	 * 
	 * @return jsonView
		{
		"meterlist":[
			{
				"address":,
				"contract":,
				"endDevice":"1층-조명",
				"expirationDate":,
				"gpioX":,
				"gpioY":,
				"gpioZ":,
				"hwVersion":"252",
				"id":48,
				"ihdId":,
				"installDate":"20120501180755",
				"installedSiteImg":,
				"installProperty":,
				"lastMeteringValue":13113.186,
				"lastReadDate":"20120613092912",
				"location":"1층",
				"lpInterval":15,
				"mdsId":"00017617",
				"meterCaution":,
				"meterError":,
				"meterStatus":{
					"id":"219",
					"parent":"211",
					"code":"1.3.3.8",
					"name":"NewRegistered",
					"descr":"NewRegistered",
					"order":"8"
				},
				"meterType":{
					"id":"147",
					"parent":"146",
					"code":"1.3.1.1",
					"name":"EnergyMeter",
					"descr":"전기미터",
					"order":"1"
				},
				"model":"DLMS Meter",
				"modemPort":,
				"pulseConstant":5000,
				"qualifiedDate":,
				"supplier":"한전",
				"swName":,
				"swUpdateDate":,
				"swVersion":"239",
				"timeDiff":,
				"usageThreshold":,
				"writeDate":,
				"shortId":
				}
			]
		}
	 */
	@RequestMapping(value="/gadget/mvm/getManualMeters.do")
	public ModelAndView getManualMeters(
		@RequestParam("supplierId") int supplierId,
		@RequestParam(value="meterType", required=false) String meterType,
		@RequestParam(value="mdsId", required=false) String mdsId) {
		
		ModelAndView mav = new ModelAndView("jsonView");
		
		Map<String, Object> condition = new HashMap<String, Object>(); 
				
		condition.put("supplierId", supplierId);
		condition.put("meterTypeCode", meterType);
		condition.put("mdsId", mdsId);
		
		String code = CommonConstants.getMeterStatusCode(MeterStatus.Delete);
		Code meterStatus = CommonConstants.getMeterStatus(code);
		condition.put("meterStatus", meterStatus.getId());
		List<Meter> meterList = meterManager.getManualMeterList(condition);
		
		mav.addObject("meterList", meterList);
		
		return mav;		
	}
	
	/**
	 * 매뉴얼 미터의 아이디를 모두 얻는다.
	 * 
	 * @param supplierId 공급자 아이디
	 * @return
	 	{
	 		meterMdsIds: [
	 			{mdsId} : {entityId},
	 			...
	 		]
	 	}
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/gadget/mvm/getManualMeterMdsId.do")
	public ModelAndView getManualMeterMdsId(@RequestParam("supplierId") int supplierId) {
		
		ModelAndView ret = getManualMeters(supplierId, null, null);
		ModelAndView mav = new ModelAndView("jsonView");
		
		ModelMap model = ret.getModelMap();
		if(!model.containsKey("meterList")) {
			List<Meter> meterlist = (List<Meter>) model.get("meterList");
			List<Map<String, String>> meterMdsIds = new ArrayList<Map<String,String>>();
			Map<String, String> mdsId = null;
			for (Meter meter : meterlist) {
				mdsId = new HashMap<String, String>();
				mdsId.put(meter.getMdsId(), meter.getId().toString());
				meterMdsIds.add(mdsId);
			}
			mav.addObject("meterMdsIds", meterMdsIds);
		}
		
		return mav;		
	}
	
	/**
	 * 수동 검침 리스트를 얻는다.
	 * Raw SQL 방식이며 속도가 listManualMetering 보다 빠르다.
	 * 
	 * @param start 시작 인덱스
	 * @param limit 가져올 양
	 * @param supplierId 공급자 아이디
	 * @param dayType 검색 날자조건
	 * @param meterType 미터타입
	 * @param id 아이디
	 * @param mdsId mds 아이디
	 * @param meterName 미터이름
	 * @param sdate 시작일
	 * @param edate 종료일
	 * 
	 * @return json
	 */
	@RequestMapping(value="/gadget/mvm/listManualMeteringRaw.do")
	public ModelAndView listManualMeteringRaw(
		@RequestParam int start,
		@RequestParam int limit,
		@RequestParam int supplierId,
		@RequestParam String dayType,
		@RequestParam String meterType,
		@RequestParam(required=false) Integer id,		
		@RequestParam(required=false) String mdsId,
		@RequestParam(required=false) String meterName,
		@RequestParam(required=false) String sdate,
		@RequestParam(required=false) String edate) {
		
		ModelAndView mav = new ModelAndView("jsonView");
		int page = 1;
		try { 
			page = (start > 0) ? ((int)(start/limit)) + 1 : 1;
		}
		catch (Exception e) { 
			page = 1; 
		}
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("page", page);
		condition.put("limit", limit);
		condition.put("supplierId", supplierId);
		condition.put("mdsId", mdsId);
		condition.put("dayType", dayType);
		condition.put("meterType", meterType);
		condition.put("friendlyName", meterName);		
		condition.put("isManualMeter", 1);
		condition.put("sdate", sdate);
		condition.put("edate", edate);
		
		List<Map<String, Object>> meterList =
			manualMeteringManager.getManualMeteringData(condition);
		Integer totalCount = manualMeteringManager.getManualMeteringDataTotal(condition);
		
		return mav
			.addObject("meterList", meterList)
			.addObject("totalCount", totalCount);
	}
	
	/**
	 * 수동 검침 리스트를 얻는다.
	 * 
	 * @param start 시작 인덱스
	 * @param limit 가져올 양
	 * @param supplierId 공급자 아이디
	 * @param dayType 검색 날자조건
	 * @param meterType 미터타입
	 * @param id 아이디
	 * @param mdsId mds 아이디
	 * @param isManualMeter 수동미터 여부 {null|1}
	 * @param meterName 미터이름
	 * @param sdate 시작일
	 * @param edate 종료일
	 * 
	 * @return json
	 */
	@RequestMapping(value="/gadget/mvm/listManualMetering.do")
	public ModelAndView listManualMetering(
		@RequestParam int start,
		@RequestParam int limit,
		@RequestParam Integer supplierId,
		@RequestParam String dayType,
		@RequestParam String meterType,
		@RequestParam(required=false) String mdsId,
		@RequestParam(required=false) String meterName,
		@RequestParam(required=false) String sdate,
		@RequestParam(required=false) String edate ) {
		
		ModelAndView mav = new ModelAndView("jsonView");
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("first", start);
		parameters.put("max", limit);
		
		// 기본값
		// 시작일이 없다면 일주일 전으로 계산
		if(sdate == null) { 
			if(sdate == null || sdate.isEmpty()) {
				Calendar now = Calendar.getInstance();
				now.set(Calendar.DATE, now.get(Calendar.DATE) - 7);
				try { sdate = TimeUtil.getFormatTime(now); }
				catch (ParseException e) {}
			}
			if(edate == null || edate.isEmpty()) {
				try { edate = TimeUtil.getCurrentTime(); }
				catch (ParseException e) {}
			}
			
			int ln = (dayType.equals("DAY")) ? 10 : 12;
			sdate = sdate.substring(0, ln);
			edate = edate.substring(0, ln);
		}
		else {
			Supplier supplier = supplierManager.getSupplier(supplierId);
			sdate = ParameterConverter.convertDBDate(supplier, sdate);
			edate = ParameterConverter.convertDBDate(supplier, edate);
		}		
		parameters.put("search_from", sdate + "@" + edate);
		
		if(mdsId != null) parameters.put("meter_id", mdsId);
		if(meterName != null) parameters.put("friendly_name", meterName);
		if(dayType != null) parameters.put("dayType", dayType);
		if(meterType != null) parameters.put("meterType", meterType);
		if(supplierId != null) parameters.put("supplierId", supplierId);
		
		Long total = manualMeteringManager.getManualMeteringTotal(parameters);
		mav.addObject("meterList", manualMeteringManager.getManualMetering(parameters));
		mav.addObject("totalCount", total);
		
		mav.addObject("sdate", sdate);
		mav.addObject("edate", edate);
		mav.addObject("start", start);
		mav.addObject("limit", limit);
		
		return mav;
	}
	
	/**
	 * 사용량 통계치 데이터를 jsonView 형식으로 반환한다.
	 * 
	 * @param supplierId 공급자 아이디
	 * @param mdsId mds 아이디
	 * @param energyType 검침 종류
	 * @return
	 */
	@RequestMapping(value="/gadget/mvm/getManualUsageMeteringData.do")
	public ModelAndView getManualUsageMeteringData(
		@RequestParam("supplierId") String supplierId,
		@RequestParam("mdsId") String mdsId,
		@RequestParam("energyType") String energyType) {
		
		ModelAndView mav = new ModelAndView("jsonView");
		Map<String,Object> condition = new HashMap<String,Object>();
		condition.put("supplierId", supplierId);
		condition.put("mdsId", mdsId);
		
		Map<String, List<Map<String, String>>> result = null;
		result = manualMeteringManager.getManualMeteringStatisticsByMdsId(mdsId, energyType);
		
		return mav.addObject("result", result);
	}
	
	/**
	 * 수동 검침 엑셀 파일을 다운로드한다.
	 * 
	 * @see	com.aimir.bo.mvm.MvmMaxController ## mvmMaxGadgetExcelMake 를 거의 그대로 사용하였다.
	 * 
	 * @param supplierId 공급자 아이디
	 * @param dayType 검색 타입. 기본은 DAY
	 * @param meterType 미터타입
	 * @param number 번호 의미의 Locale 문자열 (엑셀 컬럼명)
	 * @param contractNumber 번호 의미의 Locale 문자열 (엑셀 컬럼명)
	 * @param customername 번호 의미의 Locale 문자열 (엑셀 컬럼명)
	 * @param meteringtime 번호 의미의 Locale 문자열 (엑셀 컬럼명)
	 * @param usage 번호 의미의 Locale 문자열 (엑셀 컬럼명)
	 * @param previous 번호 의미의 Locale 문자열 (엑셀 컬럼명)
	 * @param co2formula 번호 의미의 Locale 문자열 (엑셀 컬럼명)
	 * @param mcuid2 번호 의미의 Locale 문자열 (엑셀 컬럼명)
	 * @param meterid2 번호 의미의 Locale 문자열 (엑셀 컬럼명)
	 * @param strLocation 번호 의미의 Locale 문자열 (엑셀 컬럼명)
	 * @param onceCount 한번에 엑셀에 쓸 양
	 * @param id 아이디
	 * @param filePath 엑셀이 저장될 디렉토리 파일 경로
	 * @param mdsId 미터 번호
	 * @param meterName 미터이름
	 * @param isManualMeter 수검침 미터 여부
	 * @param sdate 시작일
	 * @param edate 종료일
	 * 
	 * @return JSP View
	 */
	@RequestMapping(value="/gadget/mvm/excelManualMetering.do", method={ RequestMethod.POST })
	public ModelAndView excelManualMetering(
		@RequestParam Integer supplierId,
		@RequestParam String dayType,
		@RequestParam String meterType,
		@RequestParam String number,
		@RequestParam String contractNumber,
		@RequestParam String customername, 
		@RequestParam String meteringtime,
		@RequestParam String usage,
		@RequestParam String previous,
		@RequestParam String co2formula,
		@RequestParam String mcuid2,
		@RequestParam String thisDayData,
		@RequestParam String meterid2,
		@RequestParam String strLocation,
		@RequestParam(required=false) Integer onceCount,		
		@RequestParam(required=false) Integer id,		
		@RequestParam(required=false) String filePath,
		@RequestParam(required=false) String mdsId,
		@RequestParam(required=false) String meterName,
		@RequestParam(required=false) String isManualMeter,
		@RequestParam(required=false) String sdate,
		@RequestParam(required=false) String edate ) {
		
		ModelAndView mav = new ModelAndView("/gadget/mvm/manualDownloadPopup");
		Map<String, String> msgMap = new HashMap<String, String>();
		List<String> fileNameList = new ArrayList<String>();

		msgMap.put("number", number);
        msgMap.put("contractNumber", contractNumber);
        msgMap.put("customername", customername);
        msgMap.put("meteringtime", meteringtime);
        msgMap.put("usage", usage);
        msgMap.put("previous", previous);
        msgMap.put("co2formula", co2formula);
        msgMap.put("mcuid2", mcuid2);
        msgMap.put("meterid2", meterid2);
        msgMap.put("location", strLocation);
        msgMap.put("thisDayData", thisDayData);
		
		int totalCount = 0;
		int once = (onceCount == null) ? ONCE_PACKAGE_ROWS : onceCount;
		
		Map<String, String> parameters = new HashMap<String, String>();
				
		parameters.put("first", "0");
		
		String searchDate = "";
		
		// 기본값
		// 시작일이 없다면 일주일 전으로 계산
		if(sdate == null) { 
			if(sdate == null || sdate.isEmpty()) {
				Calendar now = Calendar.getInstance();
				now.set(Calendar.DATE, now.get(Calendar.DATE) - 7);
				try { sdate = TimeUtil.getFormatTime(now); }
				catch (ParseException e) {}
			}
			if(edate == null || edate.isEmpty()) {
				try { edate = TimeUtil.getCurrentTime(); }
				catch (ParseException e) {}
			}
			
			int ln = (dayType.equals("DAY")) ? 10 : 12;
			sdate = sdate.substring(0, ln);
			edate = edate.substring(0, ln);
			searchDate = sdate + "@" + edate;
		}
		else {
			Supplier supplier = supplierManager.getSupplier(supplierId);
			searchDate = 
				ParameterConverter.convertDBDate(supplier, sdate) + "@" + 
				ParameterConverter.convertDBDate(supplier, edate);
		}		
		parameters.put("search_from", searchDate);
		
		if(mdsId != null) parameters.put("meter_id", mdsId);
		if(meterName != null) parameters.put("friendly_name", meterName);
		if(isManualMeter != null) parameters.put("is_manual_metering", "1");
		
		List<MeteringListData> meterList = 
			manualMeteringManager.getManualMeteringData(
				parameters, dayType, meterType, String.valueOf(supplierId)
			);
		
		totalCount = meterList.size();
		
		StringBuilder sbFileName = new StringBuilder();
		StringBuilder sbSplFileName = new StringBuilder();
		if(filePath == null) filePath = FILE_PATH;
		
		sbFileName.append(TimeUtil.getCurrentTimeMilli());
		
		File file = ExcelUtil.initDirectory(filePath);
		if(file == null || !file.exists() || !file.isDirectory()) {
			mav.addObject("result", "fail");
			mav.addObject("msg", "file io error [" + file.getName() + "]");
			return mav;
		}
		
		List<MeteringListData> list = null;
		ManualMeteringDataMakeExcel wExcel = new ManualMeteringDataMakeExcel();
		
		int cnt = 1;
		int idx = 0;
		int fnum = 0;
		int splCnt = 0;
		
		if (totalCount <= once) {
			sbSplFileName = new StringBuilder();
			sbSplFileName.append(sbFileName);
			sbSplFileName.append(".xls");
			wExcel.writeReportExcel(meterList, msgMap, false, filePath, sbSplFileName.toString());
			fileNameList.add(sbSplFileName.toString());
		} 
		else {
			for (int i = 0; i < totalCount; i++) {
				if ((splCnt * fnum + cnt) == totalCount || cnt == once) {
					sbSplFileName = new StringBuilder();
					sbSplFileName.append(sbFileName);
					sbSplFileName.append('(').append(++fnum).append(").xls");

					list = meterList.subList(idx, (i + 1));

					wExcel.writeReportExcel(list, msgMap, false, filePath, sbSplFileName.toString());
					fileNameList.add(sbSplFileName.toString());
					list = null;
					splCnt = cnt;
					cnt = 0;
					idx = (i + 1);
				}
				cnt++;
			}
		}

		StringBuilder sbZipFile = new StringBuilder();
		sbZipFile.append(sbFileName).append(".zip");

		ZipUtils zutils = new ZipUtils();
		try {
			zutils.zipEntry(fileNameList, sbZipFile.toString(), filePath);
		}
		catch (Exception e) {
			mav.addObject("result", "fail");
			mav.addObject("msg", "file to zip proccess failed. " + e.getMessage());
			return mav;
		}
		
		mav.addObject("result", "success");
		mav.addObject("filePath", filePath);
		mav.addObject("fileName", fileNameList.get(0));
		mav.addObject("zipFileName", sbZipFile.toString());
		mav.addObject("fileNames", fileNameList);
		
		return mav;
	}
	
	/**
	 * 
	 * 수동 검침데이터를 입력한다.
	 * 
	 * @param meterType 미터타입
	 * @param meterNumber 미터번호
	 * @param meteringDate 검침일
	 * @param meteringValue 검침값
	 * 
	 * @return json 형식의 결과 및 입력된 검침 아이디
	 * {
	 * 		"result": "success" || "fail"
	 * }
	 * 
	 * @author Yi Hanghee(javarouka@gmail.com, javarouka@nuritelecom.co.kr)
	 */
	@RequestMapping(value="/gadget/mvm/writeManualMetering.do", method={ RequestMethod.POST })
	public ModelAndView writeManualMetering(
		@RequestParam String dayType,
		@RequestParam String mdsId,
		@RequestParam int supplierId,
		@RequestParam String meteringDate,
		@RequestParam double meteringValue	) {
		
		ModelAndView mav = new ModelAndView("jsonView");
		Set<String> errSet = new HashSet<String>();
		boolean isSuccess = manualMeteringManager.updateManualMeteringData(
			mdsId, dayType, supplierId, meteringDate, meteringValue, errSet);
		
		if(isSuccess) {
			mav.addObject("result", "success");
		}
		else {
			logger.error(errSet);
			mav.addObject("result", "fail");
			if(errSet.isEmpty()) {
				errSet.add("internal server error");			
			}
			mav.addObject("msg", errSet);
		}
		return mav;
	}
	
	/**
	 * 
	 * 수동으로 입력한 검침 결과를 수정한다
	 * 
	 * @param meterType 미터타입
	 * @param dayType 검침 날자타입
	 * @param mdsId 미터번호
	 * @param supplierId 공급자 아이디
	 * @param meteringDate 검침일
	 * @param meterValueUnit 검침 단위
	 * @param meteringValue 검침값
	 * 
	 * @return json 형식의 결과
	 * {
	 * 		"result": "success" || "fail"
	 * }
	 * 
	 * @author Yi Hanghee(javarouka@gmail.com, javarouka@nuritelecom.co.kr)
	 */
	@RequestMapping(value="/gadget/mvm/modifyManualMetering.do", method={ RequestMethod.POST })
	public ModelAndView modifyManualMetering(
		@RequestParam String dayType,
		@RequestParam String mdsId,
		@RequestParam int supplierId,
		@RequestParam String meteringDate,
		@RequestParam double meteringValue	) {
		
		return writeManualMetering(dayType, mdsId, supplierId, meteringDate, meteringValue);
	}	
}