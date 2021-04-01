package com.aimir.bo.system.sla;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.util.TimeUtil;

/**
 * firmware-file upload/download를 위한 Controller
 * @author jhdang
 */
@Controller
public class firmwareController {
    protected static Log log = LogFactory.getLog(firmwareController.class);
    public static Properties prop = null;
    
    @RequestMapping(value = "/list/firmwareListView")
    public ModelAndView SLAReportList() {
    	ModelAndView mav = new ModelAndView("/firmware/list/firmwareListView");
    	String path = getSLAExcelSavePath();
    	
    	List<Map<String, String>> fileDatas = getFileNameList(path);
    	
    	
    	mav.addObject("fileDatas", fileDatas);
        return mav;
    }
    
    @RequestMapping(value = "/firmware/list/fileDownload")
    public ModelAndView fileDownload(
    		@RequestParam("fileName") String fileName) {
    	//
    	ModelAndView mav = new ModelAndView("download");
    	String path = getSLAExcelSavePath();
    	
    	mav.addObject("filePath", path); // 뷰 클래스로 전송된다.
        mav.addObject("fileName", fileName); // 뷰 클래스로 전송된다.
        mav.addObject("realFileName", fileName); // 뷰 클래스로 전송된다.
    	
        return mav;
    }
    
    private String getSLAExcelSavePath() {
    	try {
			prop.load(getClass().getClassLoader().getResourceAsStream("jdbc.properties"));
		} catch (IOException e) {
			log.error("jdbc.properties path reading failed");
		}
    	String path = prop.getProperty("firmware.path", "/home/aimir/aimiramm/firmware-file/fw/dcu/NURITelecom");
    	return path;
    }
    
    // 디렉토리 하위의 모든 파일 이름을 가져옴
    private List<Map<String, String>> getFileNameList(String filePath) {
    	List<Map<String, String>> list = null;
    	
    	File f = new File(filePath);
    	File[] fileList = f.listFiles();
    	if(fileList.length > 0) {
    		list = new ArrayList<>();
    		File[] fileSortList = sortFileList(fileList, COMPARETYPE_NAME);
        	for(File fL : fileSortList) {
        		Map<String, String> map = new HashMap<>();
        		map.put("fileName", fL.getName());
        		map.put("absolPath", fL.getAbsolutePath());
        		list.add(map);
        	}
    	} 
    	else {
    		return null;
    	}
    	return list;
	}
    
    
    
	private static int COMPARETYPE_NAME = 0;
	private static int COMPARETYPE_DATE = 1;

	/**
	 * @param files
	 * @param compareType
	 * @return 파일을 이름 또는 생성일자별로 정렬한다.
	 */
	private File[] sortFileList(File[] files, final int compareType) {

		Arrays.sort(files, new Comparator<Object>() {
			@Override
			public int compare(Object object1, Object object2) {

				String s1 = "";
				String s2 = "";

				if (compareType == COMPARETYPE_NAME) {
					s1 = ((File) object1).getName();
					s2 = ((File) object2).getName();
				} else if (compareType == COMPARETYPE_DATE) {
					s1 = ((File) object1).lastModified() + "";
					s2 = ((File) object2).lastModified() + "";
				}

				return s1.compareTo(s2);

			}
		});

		return files;
	}
	
	 @RequestMapping(value = "/requestupload")
	    public String requestupload2(MultipartHttpServletRequest mtfRequest) {
		 	String referer = mtfRequest.getHeader("Referer");
		 	
		 	try {
				prop.load(getClass().getClassLoader().getResourceAsStream("jdbc.properties"));
			} catch (IOException e) {
				log.error("jdbc.properties path reading failed");
			}
		 	
	        List<MultipartFile> fileList = mtfRequest.getFiles("file");
	        String src = mtfRequest.getParameter("src");
	        System.out.println("src value : " + src);

	        String path = prop.getProperty("firmware.path", "/home/aimir/aimiramm/firmware-file/fw/dcu/NURITelecom");

	        for (MultipartFile mf : fileList) {
	            String originFileName = mf.getOriginalFilename(); // 원본 파일 명
	            long fileSize = mf.getSize(); // 파일 사이즈
	            
	            System.out.println("filePath : "+path);
	            System.out.println("originFileName : " + originFileName);
	            System.out.println("fileSize : " + fileSize);

	            String safeFile = path+"/"+originFileName;
	            try {
	                mf.transferTo(new File(safeFile));
	            } catch (IllegalStateException e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            } catch (IOException e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            }
	        }

	        return "redirect:/list/firmwareListView.do";
//	        return "redirect:"+ referer;

	    }

    
}