package com.aimir.mars.bulkreading.test;

import org.junit.Before;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.aimir.mars.integration.bulkreading.service.MDMDataLPService;
import com.aimir.mars.util.FTPClient;
import com.aimir.mars.util.FileUtil;
import com.aimir.mars.util.SFTPClient;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:META-INF/webapp/WEB-INF/config/context-*.xml" })
public class MDMFtpTest {
	
	@Autowired
	SFTPClient sftpClient;
	 
    @Before
    public void setUp() throws Exception {
      
    }
 
    @Test
	public void test() {		
    	boolean result = false;
		
    	String SOURCE_DIR = "E:\\test";
    	String FTP_REMOTE_DIR = "/home/aimir/tmp";
    	
    	result = sftpClient.folderUpload(SOURCE_DIR, FTP_REMOTE_DIR);
		
	}
}