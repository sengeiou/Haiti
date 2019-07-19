package com.aimir.bo.mvm;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.model.mvm.Season;
import com.aimir.model.system.Code;
import com.aimir.service.mvm.ConsumptionRankingManager;
import com.aimir.service.mvm.SeasonManager;
import com.aimir.service.system.CodeManager;
import com.aimir.service.system.SupplierManager;
import com.aimir.service.system.TariffTypeManager;
import com.aimir.util.CalendarUtil;
import com.aimir.util.ConsumptionRankingMakeExcel;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeUtil;
import com.aimir.util.ZipUtils;

@Controller
public class ConsumptionRankingController {

	@Autowired
	ConsumptionRankingManager consumptionRankingManager;
	
	@Autowired
	SupplierManager supplierManager;
	
	@Autowired
	TariffTypeManager tariffTypeManager;
	
	@Autowired
	CodeManager codeManager;

    @Autowired
    SeasonManager seasonManager;

	@RequestMapping(value="/gadget/mvm/consumptionRankingEmMaxGadget")
    public ModelAndView consumptionRankingEmMaxGadget() {
        ModelAndView mav = new ModelAndView("/gadget/mvm/consumptionRankingEmMaxGadget");

        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();
        Integer supplierId = user.getRoleData().getSupplier().getId();

        mav.addObject("supplierId", supplierId);
        return mav;
    }

	@RequestMapping(value="/gadget/mvm/consumptionRankingEmMiniGadget")
    public ModelAndView consumptionRankingEmMiniGadget() {
        ModelAndView mav = new ModelAndView("/gadget/mvm/consumptionRankingEmMiniGadget");

        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();
        Integer supplierId = user.getRoleData().getSupplier().getId();

        mav.addObject("supplierId", supplierId);
        return mav;
    }
	
	@RequestMapping(value="/gadget/mvm/consumptionRankingGmMaxGadget")
    public ModelAndView consumptionRankingGmMaxGadget() {
        ModelAndView mav = new ModelAndView("/gadget/mvm/consumptionRankingGmMaxGadget");

        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();
        Integer supplierId = user.getRoleData().getSupplier().getId();

        mav.addObject("supplierId", supplierId);
        return mav;
    }

	@RequestMapping(value="/gadget/mvm/consumptionRankingGmMiniGadget")
    public ModelAndView consumptionRankingGmMiniGadget() {
        ModelAndView mav = new ModelAndView("/gadget/mvm/consumptionRankingGmMiniGadget");

        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();
        Integer supplierId = user.getRoleData().getSupplier().getId();

        mav.addObject("supplierId", supplierId);
        return mav;
    }
	
	@RequestMapping(value="/gadget/mvm/consumptionRankingWmMaxGadget")
    public ModelAndView consumptionRankingWmMaxGadget() {
        ModelAndView mav = new ModelAndView("/gadget/mvm/consumptionRankingWmMaxGadget");

        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();
        Integer supplierId = user.getRoleData().getSupplier().getId();

        mav.addObject("supplierId", supplierId);
        return mav;
    }

	@RequestMapping(value="/gadget/mvm/consumptionRankingWmMiniGadget")
    public ModelAndView consumptionRankingWmMiniGadget() {
        ModelAndView mav = new ModelAndView("/gadget/mvm/consumptionRankingWmMiniGadget");

        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();
        Integer supplierId = user.getRoleData().getSupplier().getId();

        mav.addObject("supplierId", supplierId);
        return mav;
    }
	
	@RequestMapping(value="/gadget/mvm/consumptionRankingHmMaxGadget")
    public ModelAndView consumptionRankingHmMaxGadget() {
        ModelAndView mav = new ModelAndView("/gadget/mvm/consumptionRankingHmMaxGadget");

        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();
        Integer supplierId = user.getRoleData().getSupplier().getId();

        mav.addObject("supplierId", supplierId);
        return mav;
    }

	@RequestMapping(value="/gadget/mvm/consumptionRankingHmMiniGadget")
    public ModelAndView consumptionRankingHmMiniGadget() {
        ModelAndView mav = new ModelAndView("/gadget/mvm/consumptionRankingHmMiniGadget");

        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();
        Integer supplierId = user.getRoleData().getSupplier().getId();

        mav.addObject("supplierId", supplierId);
        return mav;
    }

	/*
	@RequestMapping(value="/gadget/mvm/getSuppliers")
	public ModelAndView getSuppliers() {
		ModelAndView mav = new ModelAndView("jsonView");
    	mav.addObject("supplierList", supplierManager.getSuppliers());
    	return mav;
	}
	
	@RequestMapping(value="/gadget/mvm/getTariffTypes")
	public ModelAndView getTariffTypes(@RequestParam("serviceType") String serviceType
									  ,@RequestParam("supplierId") int supplierId ) {
		ModelAndView mav = new ModelAndView("jsonView");
		mav.addObject("tariffTypes", tariffTypeManager.getTariffTypeBySupplier(serviceType, supplierId));
		return mav;
	}
	
	@RequestMapping(value="/gadget/mvm/getLocations")
	public ModelAndView getLocations(@RequestParam("supplierId") int supplierId) {
		ModelAndView mav = new ModelAndView("jsonView");
		Supplier supplier = supplierManager.getSupplier(supplierId);
		mav.addObject("locations", supplier.getLocations());
		return mav;
	}
	*/
	
	@RequestMapping(value="/gadget/mvm/getUsageList")
	public ModelAndView getUsageList() {
		ModelAndView mav = new ModelAndView("jsonView");
		mav.addObject("usageList", codeManager.getChildCodes("000"));
		return mav;
	}
	
	@RequestMapping(value="/gadget/mvm/getRankingList")
	public ModelAndView getRankingList() {
		ModelAndView mav = new ModelAndView("jsonView");
		mav.addObject("rankingList", codeManager.getChildCodes(Code.RANKINGRANGE));
		return mav;
	}
	
	@RequestMapping(value="/gadget/mvm/consumptionRankingExcelDownloadPopup")
    public ModelAndView consumptionRankingExcelDownloadPopup() {      
    	
        ModelAndView mav = new ModelAndView("/gadget/mvm/consumptionRankingExcelDownloadPopup");
        return mav;
    }
	
	@SuppressWarnings({ "unused", "unchecked" })
    @RequestMapping(value = "/gadget/mvm/consumptionRankingExcelMake")
    public ModelAndView consumptionRankingExcelMake(@RequestParam("condition[]") String[] condition,
            @RequestParam("fmtMessage[]") String[] fmtMessage,
            @RequestParam("filePath") String filePath) {

		Map<String, String> msgMap = new HashMap<String, String>();
		List<String> fileNameList = new ArrayList<String>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		StringBuilder sbFileName = new StringBuilder();
		StringBuilder sbSplFileName = new StringBuilder();

		boolean isLast = false;
        Integer total = 0; // 데이터 조회건수
        Integer maxRows = 5000; // excel sheet 하나에 보여줄 수 있는 최대 데이터 row 수

		final String logPrefix = "consumptionRanking("+fmtMessage[11]+")";

		ModelAndView mav = new ModelAndView("jsonView");

		List<Map<String, Object>> result = null;
		try {
			Map<String, Object> conditionMap = new HashMap<String, Object>();
            conditionMap.put("meterType", condition[0]);
            conditionMap.put("rankingType", condition[1]);
            conditionMap.put("supplierId", (StringUtil.nullToBlank(condition[2]).isEmpty()) ? null : Integer.valueOf(condition[2]));
            conditionMap.put("locationId", (StringUtil.nullToBlank(condition[3]).isEmpty()) ? null : Integer.valueOf(condition[3]));
            conditionMap.put("sysId", condition[4]);
            conditionMap.put("tariffType", (StringUtil.nullToBlank(condition[5]).isEmpty()) ? null : Integer.valueOf(condition[5]));
            conditionMap.put("contractNo", condition[6]);
            conditionMap.put("totalUsage", (StringUtil.nullToBlank(condition[7]).isEmpty()) ? null : Double.valueOf(condition[7]));
            conditionMap.put("rankingCount", (StringUtil.nullToBlank(condition[8]).isEmpty()) ? null : Integer.valueOf(condition[8]));
            conditionMap.put("dateType", condition[9]);
            conditionMap.put("startDate", condition[10]);
            conditionMap.put("endDate", condition[11]);
            
            if (condition.length == 12) {
                conditionMap.put("usageRange", condition[11]);
            }

			Map<String, Object> resultMap = consumptionRankingManager.getConsumptionRankingDataList(conditionMap, true);
			result = (List<Map<String, Object>>)resultMap.get("result");
			total = (Integer)resultMap.get("totalCount");

			mav.addObject("total", total);
			if (total <= 0) {
				return mav;
			}

			sbFileName.append(logPrefix);

			sbFileName.append(TimeUtil.getCurrentTimeMilli());
			
			// message 생성
            msgMap.put("ranking", fmtMessage[0]);
            msgMap.put("date", fmtMessage[1]);
            msgMap.put("totalUsage", fmtMessage[2]);
            msgMap.put("contractNo", fmtMessage[3]);
            msgMap.put("customerName", fmtMessage[4]);
            msgMap.put("tariffType", fmtMessage[5]);
            msgMap.put("location", fmtMessage[6]);
            msgMap.put("title", fmtMessage[13]);
            msgMap.put("mdsId", fmtMessage[16]);

			// check download dir
			File downDir = new File(filePath);

			if (downDir.exists()) {
				File[] files = downDir.listFiles();

				if (files != null) {
					String filename = null;
					String deleteDate;

					deleteDate = CalendarUtil.getDate(TimeUtil.getCurrentDay(),
							Calendar.DAY_OF_MONTH, -10); // 10일 이전 일자
					boolean isDel = false;

					for (File file : files) {

						filename = file.getName();
						isDel = false;

						// 파일길이 : 30이상, 확장자 : xls|zip
						if (filename.length() > 30
								&& (filename.endsWith("xls") || filename
										.endsWith("zip"))) {
							// 10일 지난 파일들 삭제
							if (filename.startsWith(logPrefix)
									&& filename.substring(17, 25).compareTo(
											deleteDate) < 0) {
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
				downDir.mkdir();
			}

			// create excel file
			ConsumptionRankingMakeExcel wExcel = new ConsumptionRankingMakeExcel();
			int cnt = 1;
			int idx = 0;
			int fnum = 0;
			int splCnt = 0;

			if (total <= maxRows) {
				sbSplFileName = new StringBuilder();
				sbSplFileName.append(sbFileName);
				sbSplFileName.append(".xls");
				wExcel.writeReportExcel(result, msgMap, isLast, filePath, sbSplFileName.toString());
				fileNameList.add(sbSplFileName.toString());
			} else {
				for (int i = 0, len = ((total%maxRows != 0) ? (total/maxRows)+1 : total/maxRows); i < len; i++) {
				    sbSplFileName.delete(0, sbSplFileName.length());
                    sbSplFileName.append(sbFileName);
                    sbSplFileName.append('(').append(++fnum).append(").xls");

                    if (i == (len-1)) {
                        cnt = total;
                    } else {
                        cnt = (i + 1) * maxRows;
                    }

                    list = result.subList(idx, cnt);
                    wExcel.writeReportExcel(list, msgMap, isLast, filePath, sbSplFileName.toString());
                    fileNameList.add(sbSplFileName.toString());
                    list = null;
                    idx = cnt;
				}
			}

			// create zip file
			StringBuilder sbZipFile = new StringBuilder();
			sbZipFile.append(sbFileName).append(".zip");

			ZipUtils zutils = new ZipUtils();
			zutils.zipEntry(fileNameList, sbZipFile.toString(), filePath);

			// return object
			mav.addObject("filePath", filePath);
			mav.addObject("fileName", fileNameList.get(0));
			mav.addObject("zipFileName", sbZipFile.toString());
			mav.addObject("fileNames", fileNameList);

		} catch (ParseException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mav;
	}
	
	/*
    @RequestMapping(value="/gadget/mvm/getConsumptionRanking")
    public ModelAndView getConsumptionRanking( @RequestParam("meterType") String meterType
			  								  ,@RequestParam("rankingType") String rankingType
											  ,@RequestParam("supplierId") String strSupplierId
											  ,@RequestParam("locationId") int locationId
											  ,@RequestParam("tariffType") int tariffType
											  ,@RequestParam("usage") long usage
											  ,@RequestParam("dateType") String dateType
											  ,@RequestParam("startDate") String startDate
											  ,@RequestParam("endDate") String endDate ) {
        ModelAndView mav = new ModelAndView("jsonView");

		int supplierId = 0;
		if(!"".equals(StringUtil.nullToBlank(strSupplierId))){
			supplierId = Integer.parseInt(strSupplierId);
		}
        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("meterType", meterType);
        condition.put("rankingType", rankingType);
        condition.put("supplierId", supplierId);
        condition.put("locationId", locationId);
        condition.put("tariffType", tariffType);
        condition.put("usage", usage);
        condition.put("dateType", dateType);
        condition.put("startDate", startDate);
        condition.put("endDate", endDate);

        Map<String, Object> result = consumptionRankingManager.getConsumptionRanking(condition);
        mav.addObject("grid", result.get("grid"));

        return mav;
    }
	
    @RequestMapping(value="/gadget/mvm/getConsumptionRankingList")
    public ModelAndView getConsumptionRankingList( @RequestParam("meterType") String meterType
			  									  ,@RequestParam("rankingType") String rankingType
    		                                      ,@RequestParam("supplierId") String strSupplierId
    		                                      ,@RequestParam("locationId") int locationId
    		                                      ,@RequestParam("tariffType") int tariffType
    		                                      ,@RequestParam("usage") long usage
    		                                      ,@RequestParam("rankingCount") int rankingCount
    											  ,@RequestParam("dateType") String dateType
    											  ,@RequestParam("startDate") String startDate
    											  ,@RequestParam("endDate") String endDate ) {
        ModelAndView mav = new ModelAndView("jsonView");

		int supplierId = 0;
		if(!"".equals(StringUtil.nullToBlank(strSupplierId))){
			supplierId = Integer.parseInt(strSupplierId);
		}
        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("meterType", meterType);
        condition.put("rankingType", rankingType);
        condition.put("supplierId", supplierId);
        condition.put("locationId", locationId);
        condition.put("tariffType", tariffType);
        condition.put("usage", usage);
        condition.put("rankingCount", rankingCount);
        condition.put("dateType", dateType);
        condition.put("startDate", startDate);
        condition.put("endDate", endDate);
        
        Map<String, Object> result = consumptionRankingManager.getConsumptionRankingList(condition);
        
        mav.addObject("grid", result.get("grid"));

        return mav;
    }
	*/

    /**
     * method name : getConsumptionRanking<b/>
     * method Desc : Consumption Ranking 미니가젯에서 소비랭킹 리스트를 조회한다.
     *
     * @param meterType
     * @param rankingType
     * @param supplierId
     * @param locationId
     * @param tariffType
     * @param totalUsage
     * @param dateType
     * @param startDate
     * @param endDate
     * @return
     */
    @RequestMapping(value="/gadget/mvm/getConsumptionRanking")
    public ModelAndView getConsumptionRanking(@RequestParam("meterType") String meterType,
            @RequestParam("rankingType") String rankingType,
            @RequestParam("supplierId") Integer supplierId,
            @RequestParam("locationId") Integer locationId,
            @RequestParam("tariffType") Integer tariffType,
            @RequestParam("totalUsage") Double totalUsage,
            @RequestParam("dateType") String dateType,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate,
            @RequestParam(value="usageRange", required=false) String usageRange) {
        ModelAndView mav = new ModelAndView("jsonView");

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("meterType", meterType);
        conditionMap.put("rankingType", rankingType);
        conditionMap.put("supplierId", supplierId);
        conditionMap.put("locationId", locationId);
        conditionMap.put("tariffType", tariffType);
        conditionMap.put("totalUsage", totalUsage);
        conditionMap.put("dateType", dateType);
        conditionMap.put("startDate", startDate);
        conditionMap.put("endDate", endDate);
        conditionMap.put("usageRange", usageRange);
        conditionMap.put("page", 1);
        conditionMap.put("limit", 10);  // Top 10 조회

        List<Map<String, Object>> result = consumptionRankingManager.getConsumptionRankingData(conditionMap);
        mav.addObject("result", result);

        return mav;
    }

    /**
     * method name : getConsumptionRankingList<b/>
     * method Desc : Consumption Ranking 가젯에서 소비랭킹 리스트를 조회한다.
     *
     * @param meterType
     * @param rankingType
     * @param supplierId
     * @param locationId
     * @param tariffType
     * @param contractNo
     * @param totalUsage
     * @param rankingCount
     * @param dateType
     * @param startDate
     * @param endDate
     * @param page
     * @param limit
     * @return
     */
    @RequestMapping(value="/gadget/mvm/getConsumptionRankingList")
    public ModelAndView getConsumptionRankingList(@RequestParam("meterType") String meterType,
            @RequestParam("rankingType") String rankingType,
            @RequestParam("supplierId") Integer supplierId,
            @RequestParam("locationId") Integer locationId,
            @RequestParam("sysId") String sysId,
            @RequestParam("tariffType") Integer tariffType,
            @RequestParam("contractNo") String contractNo,
            @RequestParam("totalUsage") Double totalUsage,
            @RequestParam("rankingCount") Integer rankingCount,
            @RequestParam("dateType") String dateType,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate,
            @RequestParam(value="usageRange", required=false) String usageRange,
            @RequestParam("page") Integer page,
            @RequestParam("limit") Integer limit) {
        ModelAndView mav = new ModelAndView("jsonView");

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("meterType", meterType);
        conditionMap.put("rankingType", rankingType);
        conditionMap.put("supplierId", supplierId);
        conditionMap.put("locationId", locationId);
        conditionMap.put("sysId", sysId);
        conditionMap.put("tariffType", tariffType);
        conditionMap.put("contractNo", contractNo);
        conditionMap.put("totalUsage", totalUsage);
        conditionMap.put("rankingCount", rankingCount);
        conditionMap.put("dateType", dateType);
        conditionMap.put("startDate", startDate);
        conditionMap.put("endDate", endDate);
        conditionMap.put("usageRange", usageRange);
        conditionMap.put("page", page);
        conditionMap.put("limit", limit);

        Map<String, Object> result = consumptionRankingManager.getConsumptionRankingDataList(conditionMap);
        mav.addAllObjects(result);
        return mav;
    }

    /**
     * method name : getTariffSupplySizeComboData<b/>
     * method Desc : Consumption Ranking 가젯에서 해당 Tariff 의 SupplySize ComboData 를 조회한다.
     *
     * @param searchEndDate
     * @param supplierId
     * @param tariffTypeId
     * @return
     */
    @RequestMapping(value="/gadget/mvm/getTariffSupplySizeComboData")
    public ModelAndView getTariffSupplySizeComboData(@RequestParam("searchEndDate") String searchEndDate,
            @RequestParam("supplierId") Integer supplierId,
            @RequestParam("tariffTypeId") Integer tariffTypeId) {
        ModelAndView mav = new ModelAndView("jsonView");
        Season season = seasonManager.getSeasonByDate(searchEndDate);
        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("searchEndDate", searchEndDate);
        conditionMap.put("season", season);
        conditionMap.put("supplierId", supplierId);
        conditionMap.put("tariffTypeId", tariffTypeId);

        Map<String, Object> result = tariffTypeManager.getTariffSupplySizeComboData(conditionMap);
        mav.addAllObjects(result);
        return mav;
    }
}