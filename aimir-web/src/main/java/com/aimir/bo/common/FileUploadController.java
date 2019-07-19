package com.aimir.bo.common;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.support.AimirFilePath;
import com.aimir.support.FileUploadHelper;

@Controller
public class FileUploadController {
	Log logger = LogFactory.getLog(FileUploadController.class);
	
	@Autowired
	AimirFilePath aimirFilePath;
	
	@RequestMapping(value="/common/deviceImgUpload.do", method = RequestMethod.POST)
	public ModelAndView upload(HttpServletRequest request, HttpServletResponse response) throws ServletRequestBindingException, IOException{
		String contextRoot = new HttpServletRequestWrapper(request).getRealPath("/");
	
		MultipartHttpServletRequest multiReq = (MultipartHttpServletRequest)request;
		MultipartFile multipartFile = multiReq.getFile("userfile");
		
		String filename = multipartFile.getOriginalFilename();
		if (filename == null || "".equals(filename))
			return null;
		
				
		String extension = filename.substring(filename.indexOf("."));
		
		String saveBasePathname = contextRoot+aimirFilePath.getPhotoBasePath();
		String saveTemppathname = contextRoot+aimirFilePath.getPhotoTempPath();
		String savepathname     = contextRoot+aimirFilePath.getDevicePath();
		
		if (!FileUploadHelper.exists(saveBasePathname)) {
			File savedir = new File(saveBasePathname);
			savedir.mkdir();
		}
		
		if (!FileUploadHelper.exists(saveTemppathname)) {
			File savedir = new File(saveTemppathname);
			savedir.mkdir();
		}
		
		if (!FileUploadHelper.exists(savepathname)) {
			File savedir = new File(savepathname);
			savedir.mkdir();
		}
		
		
		File uFile =new File(FileUploadHelper.makePath(saveTemppathname, filename));
		
		multipartFile.transferTo(uFile);
		
		Date date = new Date();
		String savefilename = date.getTime()+extension;
		
//		File Copy & Temp File Delete
		FileUploadHelper.copy(FileUploadHelper.makePath(saveTemppathname, filename), FileUploadHelper.makePath(savepathname, savefilename));	
		FileUploadHelper.removeExistingFile(FileUploadHelper.makePath(saveTemppathname, filename));	
		
		String viewfilename = aimirFilePath.getDevicePath()+"/"+savefilename;
		ModelAndView mav = new ModelAndView("gadget/system/devicemodelResult");
		mav.addObject("savefilename", viewfilename);		
		
		return mav;
//		return FileUtils.makePath(aimirFilePath.getUiPath(), savefilename);
//		return "jsonView";
	}
}
