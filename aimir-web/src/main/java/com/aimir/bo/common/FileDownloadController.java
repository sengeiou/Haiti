package com.aimir.bo.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class FileDownloadController {
	Log logger = LogFactory.getLog(FileDownloadController.class);

    @RequestMapping(value="/common/fileDownload")
    public ModelAndView fileDownload(
            @RequestParam("filePath") String filePath,
            @RequestParam("fileName") String fileName,
            @RequestParam(value="realFileName", required=false) String realFileName) {
        ModelAndView mav = new ModelAndView("download");

        mav.addObject("filePath", filePath); // 뷰 클래스로 전송된다.
        mav.addObject("fileName", fileName); // 뷰 클래스로 전송된다.
        mav.addObject("realFileName", realFileName); // 뷰 클래스로 전송된다.

        return mav;
    }
    
    // 엑셀로 저장하는 페이지
	@RequestMapping(value = "/gadget/ExcelDownloadPopup")
	public ModelAndView powerQualityexcelDownloadPopup() {
		ModelAndView mav = new ModelAndView(
				"/gadget/ExcelDownloadPopup");
		return mav;
	}

}