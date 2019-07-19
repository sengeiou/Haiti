package com.aimir.fep.meter;

import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Ignore;
import org.junit.Test;

import com.aimir.fep.meter.parser.UMC2000G4R;
import com.aimir.fep.meter.parser.UMC2000W4R;
import com.aimir.fep.util.Hex;
import com.aimir.model.device.GasMeter;
import com.aimir.model.device.MCU;
import com.aimir.model.device.Modem;
import com.aimir.model.device.WaterMeter;
import com.aimir.model.system.Code;

public class ZEUPLS2Test {
    private static Log log = LogFactory.getLog(ZEUPLS2Test.class);

    @Ignore
    public void test4663() throws Exception {
        byte[] b = Hex.encode("003C000007DD091A0C2C26011900D491160123F80000033E010107DD091A0000033E24131524000000000007DD091A0000033EFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF000000000000000000000000000000000000000000000000000000000000033E303034373438350000000000000131313033313141482F57322E3230532F57322E3230");
        
        UMC2000G4R z = new UMC2000G4R();
        z.setMcuRevision("4668");
        GasMeter meter = new GasMeter();
        Code code = new Code();
        code.setName("GasMeter");
        meter.setMeterType(code);
        meter.setPulseConstant(100.0);
        MCU mcu = new MCU();
        mcu.setSysSwRevision("4668");
        Modem modem = new Modem();
        modem.setMcu(mcu);
        meter.setModem(modem);
        z.setMeter(meter);
        z.parse(b);
        Map map = z.getData();
        Object key = null;
        for (Iterator i = map.keySet().iterator(); i.hasNext(); ) {
            key = i.next();
            log.info(key + "=" + map.get(key));
        }
    }
    
    @Test
    public void testUMC2000W4R() throws Exception {
        byte[] b = Hex.encode("021C000007DE0218173100018E03BC8FFF03E03B00000385010107DE02170000038524131524FFE40600000007DE021700000385000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000");
        
        UMC2000W4R z = new UMC2000W4R();
        z.setMcuRevision("5348");
        WaterMeter meter = new WaterMeter();
        Code code = new Code();
        code.setName("WaterMeter");
        meter.setMeterType(code);
        meter.setPulseConstant(100.0);
        MCU mcu = new MCU();
        mcu.setSysSwRevision("5348");
        Modem modem = new Modem();
        modem.setMcu(mcu);
        meter.setModem(modem);
        z.setMeter(meter);
        z.parse(b);
        Map map = z.getData();
        Object key = null;
        for (Iterator i = map.keySet().iterator(); i.hasNext(); ) {
            key = i.next();
            log.info(key + "=" + map.get(key));
        }
    }
}
