package com.aimir.bo.system.notice;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.model.device.Converter;
import com.aimir.model.system.Notice;
import com.aimir.schedule.command.CmdOperationUtil;
import com.aimir.service.device.ModemManager;
import com.aimir.service.system.NoticeManager;

@Controller
public class NoticeController {
	@Autowired
	NoticeManager noticeManager;
	
	@Autowired
	CmdOperationUtil cmdOperationUtil;
	
	@Autowired
	ModemManager modemManager;
	
	SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
	
    @RequestMapping(value="/gadget/system/noticeMax")
    public String getNoticeMax() {
        return "/gadget/system/noticeMax";
    }
    
    @RequestMapping(value="/gadget/system/noticeMini")
    public String getNoticeMini() {
        return "/gadget/system/noticeMini";
    }

	@RequestMapping(value = "/gadget/system/notice/addNotice")
	public String addNotice(@ModelAttribute Notice notice,
			BindingResult result, SessionStatus status,
			HttpServletRequest request) {
		notice.setWriteDate(formatter.format(Calendar.getInstance().getTime()));
		notice.setHits("0");
		// ESAPI.setAuthenticator((Authenticator) new AimirAuthenticator());
		AimirAuthenticator instance = (AimirAuthenticator) ESAPI
				.authenticator();
		AimirUser user = (AimirUser) instance.getUserFromSession();
		notice.setWriter(user.getLoginId());
		noticeManager.add(notice);
		try{
			List<Converter> modems = modemManager.getConverterModem();
			for(int i = 0; modems != null && modems.size() > i;i++){
				Converter modem = modems.get(i);
				cmdOperationUtil.cmdBroadcast("", modem.getDeviceSerial(), notice.getSubject());
			}

		}catch(Exception e){
			e.printStackTrace();
		}
		return "/gadget/system/noticeMax";
	}

	@RequestMapping(value = "/gadget/system/notice/deleteNotice")
	public String deleteNotice(@RequestParam("noticeId") int noticeId) {
		noticeManager.delete(noticeId);
		
		return "/gadget/system/noticeMax";
	}

	@RequestMapping(value = "/gadget/system/notice/getNotice")
	public ModelAndView getNotice(@RequestParam("noticeId") int noticeId) {
		Notice notice = noticeManager.getNotice(noticeId);
		ModelAndView mav = new ModelAndView("jsonView");
		mav.addObject("notice", notice);
		return mav;
	}

	@RequestMapping(value = "/gadget/system/notice/getNotices")
	public ModelAndView getNotices(@RequestParam("page") int page,
			@RequestParam("count") int count) {
		ModelAndView mav = new ModelAndView("jsonView");
		return mav.addObject("notices", noticeManager.getNotices(page, count));
	}
	
	@RequestMapping(value = "/gadget/system/notice/sortList")
	public ModelAndView sortList(@RequestParam("page") int page,@RequestParam("count") int count, 
			@RequestParam("name") String name, @RequestParam("sortCheck") int sortCheck) {
		ModelAndView mav = new ModelAndView("jsonView");
		return mav.addObject("notices", noticeManager.sortList(name, page, count, sortCheck));
	}

	@RequestMapping(value = "/gadget/system/notice/getCount")
	public ModelAndView getCount() {
		ModelAndView mav = new ModelAndView("jsonView");
		mav.addObject("count", noticeManager.getCount().toString());
		return mav;
	}
	
	@RequestMapping(value = "/gadget/system/notice/getUser")
	public ModelAndView getUser() {
		ModelAndView mav = new ModelAndView("jsonView");
		// ESAPI.setAuthenticator((Authenticator) new AimirAuthenticator());
		AimirAuthenticator instance = (AimirAuthenticator) ESAPI
				.authenticator();
		AimirUser user = (AimirUser) instance.getUserFromSession();
		mav.addObject("user", user.getLoginId());
		return mav;
	}

/*	@RequestMapping(value = "/gadget/system/notice/searchNotice.do")
	public ModelAndView searchNotice(
			@RequestParam("searchWord") String searchWord,
			@RequestParam("searchDetail") String searchDetail,
			@RequestParam("searchCategory") String searchCategory,
			@RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate) throws UnsupportedEncodingException {
		ModelAndView mav = new ModelAndView("jsonView");
		
		searchWord = URLDecoder.decode(searchWord, "UTF-8");
		searchDetail = URLDecoder.decode(searchDetail, "UTF-8");
		searchCategory = URLDecoder.decode(searchCategory, "UTF-8");
		startDate = URLDecoder.decode(startDate, "UTF-8");
		endDate = URLDecoder.decode(endDate, "UTF-8");
		
		return mav.addObject("searchList", noticeManager.searchNotice(
				searchWord, searchDetail, searchCategory, startDate, endDate));
	}
*/
	@RequestMapping(value = "/gadget/system/notice/hitsPlus")
	public void hitsPlus(@RequestParam("noticeId") int noticeId) {
		noticeManager.hitsPlus(noticeId);
	}

	@RequestMapping(value = "/gadget/system/notice/editNotice")
	public String editNotice(@ModelAttribute Notice notice,
		BindingResult result, SessionStatus status,
		HttpServletRequest request) {
		Notice notice1 = noticeManager.getNotice(notice.getId());
		notice1.setCategory(notice.getCategory());
		notice1.setContent(notice.getContent());
		notice1.setSubject(notice.getSubject());
		noticeManager.update(notice1);
		try{
			List<Converter> modems = modemManager.getConverterModem();
			for(int i = 0; modems != null && modems.size() > i;i++){
				Converter modem = modems.get(i);
				cmdOperationUtil.cmdBroadcast("", modem.getDeviceSerial(), notice.getSubject());
			}

		}catch(Exception e){
			e.printStackTrace();
		}
		return "/gadget/system/noticeMax";
	}
}
