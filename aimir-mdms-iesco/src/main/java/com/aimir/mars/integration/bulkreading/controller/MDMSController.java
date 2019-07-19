package com.aimir.mars.integration.bulkreading.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.aimir.mars.integration.bulkreading.service.MDMSService;

@Controller
public class MDMSController {
	
	private static final Logger log = LoggerFactory.getLogger(MDMSController.class);
	
	@Autowired
	MDMSService mdmsService;
	
	@RequestMapping(value = "/mdms/MDMSService", method = RequestMethod.GET)
    public String MDMSService(HttpServletRequest request, ModelMap model)
    {	
		return "mdms/MDMSService";
    }
	
	@RequestMapping(value = "/mdms/MDMSBatchList", method = RequestMethod.GET)
    public String MDMSBatchList(HttpServletRequest request, ModelMap model)
    {	
		return "mdms/MDMSBatchList";
    }
	
	@RequestMapping(value = "/mdms/MDMSLPList", method = RequestMethod.GET)
    public String MDMSLpList(HttpServletRequest request, ModelMap model)
    {	
		return "mdms/MDMSLPList";
    }
	
	@RequestMapping(value = "/mdms/MDMSDailyList", method = RequestMethod.GET)
    public String MDMSDailyList(HttpServletRequest request, ModelMap model)
    {	
		return "mdms/MDMSDailyList";
    }
	
	@RequestMapping(value = "/mdms/MDMSMonthlyList", method = RequestMethod.GET)
    public String MDMSMonthlyList(HttpServletRequest request, ModelMap model)
    {	
		return "mdms/MDMSMonthlyList";
    }
	
	@RequestMapping(value = "/mdms/MDMSEventList", method = RequestMethod.GET)
    public String MDMSEventList(HttpServletRequest request, ModelMap model)
    {	
		return "mdms/MDMSEventList";
    }
	
	@RequestMapping(value = "/mdms/MDMSAlertList", method = RequestMethod.GET)
    public String MDMSAlertList(HttpServletRequest request, ModelMap model)
    {	
		return "mdms/MDMSAlertList";
    }
	
	@RequestMapping(value = "/mdms/getMDMSStatistics")
    public ResponseEntity<Object> MDMSStatistics(HttpServletRequest request)
    {	
		JSONObject json = new JSONObject();
		Map<String, Object> params = new HashMap<String, Object>();
		
		String yyyymmdd = (request.getParameter("yyyymmdd") == null) ? "" : (String)request.getParameter("yyyymmdd");
		String batch_type = (request.getParameter("batch_type") == null) ? "" : (String)request.getParameter("batch_type");
		
		params.put("yyyymmdd", yyyymmdd);
		params.put("batch_type", batch_type);
		
		try {
			
			List<Map<String, Object>> result = mdmsService.getMDMSStatistics(params);
			json.put("resultGrid", result);	
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/html; charset=UTF-8");
		return new ResponseEntity<Object>(json, responseHeaders, HttpStatus.CREATED);
    }
	
	@RequestMapping(value = "/mdms/getMDMSBatchList")
    public ResponseEntity<Object> MDMSBatchList(HttpServletRequest request)
    {	
		JSONObject json = new JSONObject();
		Map<String, Object> params = new HashMap<String, Object>();
		
		String yyyymmdd = (request.getParameter("yyyymmdd") == null) ? "" : (String)request.getParameter("yyyymmdd");
		String batch_type = (request.getParameter("batch_type") == null) ? "" : (String)request.getParameter("batch_type");
		String batch_id = (request.getParameter("batch_id") == null) ? "" : (String)request.getParameter("batch_id");
		
		int page = Integer.parseInt(request.getParameter("page"));
		int limit = Integer.parseInt(request.getParameter("limit"));
		
		page = (page == 0) ? 1 : page;
		limit = (limit == 0) ? 20 : limit;
		
		params.put("yyyymmdd", yyyymmdd);
		params.put("batch_type", batch_type);
		params.put("batch_id", batch_id);		
		params.put("page", page);
		params.put("limit", limit);
			
		try {
			
			Map<String, Object> result = mdmsService.getMDMSBatchList(params);
			
			json.put("totalCount", (Integer)result.get("totalCount"));
			json.put("resultGrid", (List)result.get("resultGrid"));			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/html; charset=UTF-8");
		return new ResponseEntity<Object>(json, responseHeaders, HttpStatus.CREATED);
    }
	
	@RequestMapping(value = "/mdms/getMDMSLPList")
    public ResponseEntity<Object> getMDMSLPList(HttpServletRequest request)
    {	
		JSONObject json = new JSONObject();
		Map<String, Object> params = new HashMap<String, Object>();
		
		String yyyymmdd = (request.getParameter("yyyymmdd") == null) ? "" : (String)request.getParameter("yyyymmdd");		
		String batch_id = (request.getParameter("batch_id") == null) ? "" : (String)request.getParameter("batch_id");
		String transfer_yn = (request.getParameter("transfer_yn") == null) ? "" : (String)request.getParameter("transfer_yn");
		String batch_yn = (request.getParameter("batch_yn") == null) ? "" : (String)request.getParameter("batch_yn");		
		String transfer_date = (request.getParameter("transfer_date") == null) ? "" : (String)request.getParameter("transfer_date");		
		String yyyymmddhhmmss = (request.getParameter("yyyymmddhhmmss") == null) ? "" : (String)request.getParameter("yyyymmddhhmmss");
		String mdev_id = (request.getParameter("mdev_id") == null) ? "" : (String)request.getParameter("mdev_id");
		
		
		int page = Integer.parseInt(request.getParameter("page"));
		int limit = Integer.parseInt(request.getParameter("limit"));
		
		page = (page == 0) ? 1 : page;
		limit = (limit == 0) ? 20 : limit;
		
		params.put("yyyymmdd", yyyymmdd);		
		params.put("batch_id", batch_id);
		params.put("transfer_yn", transfer_yn);
		params.put("yyyymmddhhmmss", yyyymmddhhmmss);
		
		params.put("batch_yn", batch_yn);
		params.put("transfer_date", transfer_date);
		params.put("mdev_id", mdev_id);
		params.put("page", page);
		params.put("limit", limit);
		
		try {
			
			Map<String, Object> result = mdmsService.getMDMSLPList(params);
			
			json.put("totalCount", (Integer)result.get("totalCount"));
			json.put("resultGrid", (List)result.get("resultGrid"));			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/html; charset=UTF-8");
		return new ResponseEntity<Object>(json, responseHeaders, HttpStatus.CREATED);
    }
	
	@RequestMapping(value = "/mdms/getMDMSDailyList")
    public ResponseEntity<Object> getMDMSDailyList(HttpServletRequest request)
    {	
		JSONObject json = new JSONObject();
		Map<String, Object> params = new HashMap<String, Object>();
		
		String insert_datetime = (request.getParameter("insert_datetime") == null) ? "" : (String)request.getParameter("insert_datetime");		
		String batch_id = (request.getParameter("batch_id") == null) ? "" : (String)request.getParameter("batch_id");
		String transfer_yn = (request.getParameter("transfer_yn") == null) ? "" : (String)request.getParameter("transfer_yn");
		String batch_yn = (request.getParameter("batch_yn") == null) ? "" : (String)request.getParameter("batch_yn");		
		String transfer_date = (request.getParameter("transfer_date") == null) ? "" : (String)request.getParameter("transfer_date");		
		String yyyymmdd = (request.getParameter("yyyymmdd") == null) ? "" : (String)request.getParameter("yyyymmdd");
		String mdev_id = (request.getParameter("mdev_id") == null) ? "" : (String)request.getParameter("mdev_id");
		
		int page = Integer.parseInt(request.getParameter("page"));
		int limit = Integer.parseInt(request.getParameter("limit"));
		
		page = (page == 0) ? 1 : page;
		limit = (limit == 0) ? 20 : limit;
		
		params.put("insert_datetime", insert_datetime);
		params.put("yyyymmdd", yyyymmdd);		
		params.put("batch_id", batch_id);
		params.put("transfer_yn", transfer_yn);
		
		params.put("batch_yn", batch_yn);
		params.put("transfer_date", transfer_date);
		params.put("mdev_id", mdev_id);
		params.put("page", page);
		params.put("limit", limit);
		
		try {
			
			Map<String, Object> result = mdmsService.getMDMSDailyList(params);
			
			json.put("totalCount", (Integer)result.get("totalCount"));
			json.put("resultGrid", (List)result.get("resultGrid"));			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/html; charset=UTF-8");
		return new ResponseEntity<Object>(json, responseHeaders, HttpStatus.CREATED);
    }
	
	@RequestMapping(value = "/mdms/getMDMSMonthlyList")
    public ResponseEntity<Object> getMDMSMonthlyList(HttpServletRequest request)
    {	
		JSONObject json = new JSONObject();
		Map<String, Object> params = new HashMap<String, Object>();
		
		String insert_datetime = (request.getParameter("insert_datetime") == null) ? "" : (String)request.getParameter("insert_datetime");		
		String batch_id = (request.getParameter("batch_id") == null) ? "" : (String)request.getParameter("batch_id");
		String transfer_yn = (request.getParameter("transfer_yn") == null) ? "" : (String)request.getParameter("transfer_yn");
		String batch_yn = (request.getParameter("batch_yn") == null) ? "" : (String)request.getParameter("batch_yn");		
		String transfer_date = (request.getParameter("transfer_date") == null) ? "" : (String)request.getParameter("transfer_date");		
		String yyyymmdd = (request.getParameter("yyyymmdd") == null) ? "" : (String)request.getParameter("yyyymmdd");
		String mdev_id = (request.getParameter("mdev_id") == null) ? "" : (String)request.getParameter("mdev_id");
		
		int page = Integer.parseInt(request.getParameter("page"));
		int limit = Integer.parseInt(request.getParameter("limit"));
		
		page = (page == 0) ? 1 : page;
		limit = (limit == 0) ? 20 : limit;
		
		params.put("insert_datetime", insert_datetime);
		params.put("yyyymmdd", yyyymmdd);		
		params.put("batch_id", batch_id);
		params.put("transfer_yn", transfer_yn);
		
		params.put("batch_yn", batch_yn);
		params.put("transfer_date", transfer_date);
		params.put("mdev_id", mdev_id);
		params.put("page", page);
		params.put("limit", limit);
		
		try {
			
			Map<String, Object> result = mdmsService.getMDMSMonthlyList(params);
			
			json.put("totalCount", (Integer)result.get("totalCount"));
			json.put("resultGrid", (List)result.get("resultGrid"));			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/html; charset=UTF-8");
		return new ResponseEntity<Object>(json, responseHeaders, HttpStatus.CREATED);
    }
	
	@RequestMapping(value = "/mdms/getMDMSEventList")
    public ResponseEntity<Object> getMDMSEventList(HttpServletRequest request)
    {	
		JSONObject json = new JSONObject();
		Map<String, Object> params = new HashMap<String, Object>();
		
		String insert_datetime = (request.getParameter("insert_datetime") == null) ? "" : (String)request.getParameter("insert_datetime");		
		String batch_id = (request.getParameter("batch_id") == null) ? "" : (String)request.getParameter("batch_id");
		String transfer_yn = (request.getParameter("transfer_yn") == null) ? "" : (String)request.getParameter("transfer_yn");
		String batch_yn = (request.getParameter("batch_yn") == null) ? "" : (String)request.getParameter("batch_yn");		
		String transfer_date = (request.getParameter("transfer_date") == null) ? "" : (String)request.getParameter("transfer_date");		
		String yyyymmdd = (request.getParameter("yyyymmdd") == null) ? "" : (String)request.getParameter("yyyymmdd");
		String mdev_id = (request.getParameter("mdev_id") == null) ? "" : (String)request.getParameter("mdev_id");
		
		int page = Integer.parseInt(request.getParameter("page"));
		int limit = Integer.parseInt(request.getParameter("limit"));
		
		page = (page == 0) ? 1 : page;
		limit = (limit == 0) ? 20 : limit;
		
		params.put("insert_datetime", insert_datetime);
		params.put("yyyymmdd", yyyymmdd);		
		params.put("batch_id", batch_id);
		params.put("transfer_yn", transfer_yn);
		
		params.put("batch_yn", batch_yn);
		params.put("transfer_date", transfer_date);
		params.put("mdev_id", mdev_id);
		params.put("page", page);
		params.put("limit", limit);
		
		try {
			
			Map<String, Object> result = mdmsService.getMDMSEventList(params);
			
			json.put("totalCount", (Integer)result.get("totalCount"));
			json.put("resultGrid", (List)result.get("resultGrid"));			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/html; charset=UTF-8");
		return new ResponseEntity<Object>(json, responseHeaders, HttpStatus.CREATED);
    }
}