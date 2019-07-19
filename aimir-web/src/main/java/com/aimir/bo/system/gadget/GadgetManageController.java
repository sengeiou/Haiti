package com.aimir.bo.system.gadget;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class GadgetManageController {
	@SuppressWarnings("unused")
	private final Log log = LogFactory.getLog(GadgetManageController.class);

	@RequestMapping(value="/ajax/dashboarditems.*")
	public final ModelAndView getDashBoardItems() {
		// 구성할 가젯들을 준비한다.
		ArrayList<Object> result = new ArrayList<Object>();
		result.add(getTmpTabItems("탭1"));
		result.add(getTmpTabItems("탭2"));
		result.add(getTmpTabItems("탭3"));

		ModelAndView mav = new ModelAndView();
		mav.addObject("result", result);
		mav.setViewName("jsonView");
		return mav;
	}

	@RequestMapping(value="/ajax/ismaxitem.*", method=RequestMethod.GET)
	public final ModelAndView getIsMaxItem(@RequestParam(value="uid", required=true) String uid, @RequestParam(value="type", required=true) String type) {
		HashMap<String, Object> result = getTmpMaxGadgetItems(uid, type);

		ModelAndView mav = new ModelAndView();
		mav.addObject("result", result);
		mav.setViewName("jsonView");
		return mav;
	}

	public HashMap<String, Object> getTmpGadgetItems(String uid, String type) {
		HashMap<String, Object> gadget = new HashMap<String, Object>();

		if (type == "grid") {
			gadget.put("uid", uid);
			gadget.put("title", "고객별검침데이터" + uid);
			HashMap<String, Object> al = new HashMap<String, Object>();
			al.put("url","../MvmMiniGM.do");
			//al.put("url","grid_sample.jsp");
			al.put("scripts", true);
            gadget.put("xtype", "iframeportlet");
            gadget.put("height", 600);
            gadget.put("autoScroll", false);
			gadget.put("autoLoad", al);
			gadget.put("type", "grid");
		} else {
			gadget.put("uid", uid);
			gadget.put("title", "텍스트" + uid);
			int th = (int) (Math.random()*2);
			if (th==0) {
				HashMap<String, Object> al = new HashMap<String, Object>();
				al.put("url","html_sample.jsp");
				al.put("scripts", true);
	            gadget.put("xtype", "iframeportlet");
	            gadget.put("height", 600);
	            gadget.put("autoScroll", false);
				gadget.put("autoLoad", al);
				gadget.put("type", "text2");
			} else {
				gadget.put("html", "텍스트내용" + uid);
				gadget.put("type", "text1");
			}
		}

		return gadget;
	}

	public HashMap<String, Object> getTmpMaxGadgetItems(String uid, String type) {
		HashMap<String, Object> gadget = new HashMap<String, Object>();

		if (type.equals("grid")) {
			gadget.put("uid", uid);
			gadget.put("title", "고객별검침데이터" + uid);
			HashMap<String, Object> al = new HashMap<String, Object>();
			al.put("url","../MvmMaxEM.do");
			al.put("scripts", true);
            gadget.put("xtype", "iframeportlet");
            gadget.put("autoScroll", false);
			gadget.put("layout", "fit");
			gadget.put("height", 1000);
			gadget.put("autoLoad", al);
			gadget.put("collapsible", false);
		} else if (type.equals("text2")) {
			gadget.put("uid", uid);
			gadget.put("title", "최대화" + uid);
			HashMap<String, Object> al = new HashMap<String, Object>();
			al.put("url","html_sample_max.jsp");
			al.put("scripts", true);
            gadget.put("xtype", "iframeportlet");
            gadget.put("height", 1000);
            gadget.put("autoScroll", false);
			gadget.put("autoLoad", al);
		} else {
			gadget.put("uid", uid);
			gadget.put("title", "최대화" + uid);
			gadget.put("html", "최대화내용" + uid);
		}

		return gadget;
	}

	public HashMap<String, Object> getTmpTabItems(String title) {
		HashMap<String, Object> tab = new HashMap<String, Object>();
		HashMap<String, Object> gadget = new HashMap<String, Object>();

		ArrayList<Object> items = new ArrayList<Object>();
		
		for (int i=0; i<3; i++) {
			for (int j=0; j<5; j++) {
				char ch = (char) ((Math.random()*26)+65);
				Character cr = new Character(ch);
				int th = (int) (Math.random()*2);
				if (th==0) {
					gadget = getTmpGadgetItems(cr.toString(), "grid");
				} else {
					gadget = getTmpGadgetItems(cr.toString(), "");
				}
				gadget.put("columnIndex", i);
				gadget.put("position", j);
				items.add(gadget);
			}
		}
		tab.put("title", title);
		tab.put("data", items);

		return tab;
	}
}
