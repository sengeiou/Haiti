package com.aimir.fep.meter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.fep.BaseTestCase;
import com.aimir.fep.meter.saver.OmniMDSaver;

public class OmniTest extends BaseTestCase {
    private static Log log = LogFactory.getLog(OmniTest.class);
    
    @Test
    @Transactional
    @Rollback(false)
    public void test() {
        try {
            OmniMDSaver saver = this.applicationContext.getBean(OmniMDSaver.class);
            saver.save("0630835", "1002", "000D6F0000D69F09", "201212240002", 2268.0, 1,
                    new double[]{0,0,0,0,0,0});
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
