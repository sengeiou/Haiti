package com.aimir.mars.bulkreading.test;

import org.junit.Before;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.aimir.mars.integration.bulkreading.service.MDMDataEventService;
import com.aimir.mars.integration.bulkreading.service.MDMDataLPService;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:META-INF/webapp/WEB-INF/config/context-*.xml" })
public class MDMEventTest {
	
	@Autowired
	MDMDataEventService service;
	 
    @Before
    public void setUp() throws Exception {
      
    }
 
    @Test
	public void test() {		
    	service.getBatchDataTest();
	}
}