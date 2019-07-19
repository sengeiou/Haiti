package com.aimir.fep.tool;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.aimir.fep.protocol.fmp.processor.MDProcessor;
import com.aimir.fep.util.DataUtil;

public class MDRestore {
    private static Log log = LogFactory.getLog(MDRestore.class);

    public static void main(String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{"/config/spring-restore.xml"}); 
        DataUtil.setApplicationContext(ctx);
        
        try {
            MDProcessor mdp = DataUtil.getBean(MDProcessor.class);
            mdp.restore();
        }
        catch (Exception e) {
            log.error(e, e);
        }
        
        System.exit(0);
    }
}
