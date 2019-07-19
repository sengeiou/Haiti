package com.aimir.bo.system.memo;

import java.io.UnsupportedEncodingException;

import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.service.system.MemoManager;


@Controller
public class MemoController {

	@Autowired
    MemoManager memoManager;
	
	@RequestMapping(value="/gadget/system/memoMax.*")
    public String getMemoMax() {
        return "/gadget/system/memoMax";
    }
	
	@RequestMapping(value="/gadget/system/memoMini.*")
    public String getMemoMini() {
        return "/gadget/system/memoMini";
    }
	
	//로그인한 아이디
	@RequestMapping(value = "/gadget/system/memo/getUser.do")
	public ModelAndView getUser() {
		ModelAndView mav = new ModelAndView("jsonView");
		// ESAPI.setAuthenticator((Authenticator) new AimirAuthenticator());
		AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
		
		AimirUser user = (AimirUser) instance.getUserFromSession();

		mav.addObject("user", user.getAccountId());
		return mav;
	}
	
	//메모 리스트
	@RequestMapping(value="/gadget/system/memo/getMemo.do")
    public ModelAndView getMemo(@RequestParam("userId") long userId) {
        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("memo", memoManager.getMemos(userId));
        return mav;
	}

	//페이징이 적용된 메모 리스트
	@RequestMapping(value="/gadget/system/memo/ListMemos.do")
    public ModelAndView ListMemos(long userId, Integer startIndex, Integer maxIndex) {

        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("memo", memoManager.getMemos(userId, startIndex, maxIndex));
        mav.addObject("memocount", memoManager.getCount(userId)); //전체 메모의 갯수
        return mav;
	}
	
	//메모개별삭제
	@RequestMapping("/gadget/system/memo/deleteMemo.do")
    public ModelAndView deleteMemo(@RequestParam("Id") int Id) {
		ModelAndView mav = new ModelAndView("jsonView");
		memoManager.delete(Id);
		
		mav.addObject("result", "success");
		return mav;
	}
	
	//메모전체삭제
	@RequestMapping("/gadget/system/memo/deleteAll.do")
	public ModelAndView deleteAll(@RequestParam("userId") long userId) {
		ModelAndView mav = new ModelAndView("jsonView");
		memoManager.deleteAll(userId);
		
		mav.addObject("result", "success");
		return mav;
	}
	
	//메모검색
	@RequestMapping(value="/gadget/system/memo/searchMemo.do")
    public ModelAndView getMemo(@RequestParam("word") String word) throws UnsupportedEncodingException {
        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("memo", memoManager.searchMemos(word));
        
        return mav;
	}
}
