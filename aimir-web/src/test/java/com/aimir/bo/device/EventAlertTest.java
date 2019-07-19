package com.aimir.bo.device;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.bo.device.eventAlert.EventAlertLogController;

public class EventAlertTest {
    private static Log log = LogFactory.getLog(EventAlertTest.class);
    
    @Before
    public void setUp() {
    }
    
    @Test
    public void getRealTimeAlert() {
        EventAlertLogController ctrl = new EventAlertLogController();
        ModelAndView view = ctrl.getRealTimeEventAlertLogFromActiveMq(22, 100000L, 100000L);
        log.info(view.getModelMap().toString());
    }
}
