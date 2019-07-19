package com.aimir.bo.system.memo;




import java.text.SimpleDateFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.model.system.Memo;
import com.aimir.service.system.MemoManager;

@Controller
@RequestMapping("/gadget/system/memo/updateMemo.do")
public class UpdateMemoForm {
	
	@SuppressWarnings("unused")
    private final Log log = LogFactory.getLog(UpdateMemoForm.class);

    @Autowired
    MemoManager memoManager;
	
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    
	@RequestMapping(method = RequestMethod.POST)
    public ModelAndView updateSupplier(@ModelAttribute Memo memo) 
	{
		ModelAndView mav = new ModelAndView("jsonView");
		
		memo.setCoord("white");
//		memo.setIn_date(Calendar.getInstance().getTime());
		
		memo.setCont(memo.getCont().trim()); //공백제거
		memo.setCont(memo.getCont().replace("\r\n","<br/>"));
//		memo.setIn_date(formatter.format(Calendar.getInstance().getTime()));	//update 날짜로 변경 시
		memo.setIn_date(memo.getIn_date());	//최초 작성 날짜 유지시 
		memoManager.update(memo);
		
		mav.addObject("result", "success");
		return mav;
	}
}
