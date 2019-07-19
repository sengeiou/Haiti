package com.aimir.fep.protocol.fmp.client;

import java.io.File;
import java.net.URI;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpHost;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HostParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.cxf.helpers.FileUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.snmp4j.smi.OctetString;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.MeterCommand;
import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.fep.command.conf.KamstrupCIDMeta;
import com.aimir.fep.command.conf.KamstrupCIDMeta.CID;
import com.aimir.fep.meter.data.EventLogData;
import com.aimir.fep.meter.parser.MX2Table.CommonTable;
import com.aimir.fep.meter.parser.MX2Table.MX2LPData;
import com.aimir.fep.meter.parser.MX2Table.MX2LPData.LOAD_TAMPER_INDICATOR;
import com.aimir.fep.meter.parser.MX2Table.MX2LPData.LR_FLAG_STATUS;
import com.aimir.fep.protocol.fmp.datatype.SMIValue;
import com.aimir.fep.protocol.fmp.datatype.TIMESTAMP;
import com.aimir.fep.protocol.fmp.datatype.UINT;
import com.aimir.fep.util.CRCUtil;
import com.aimir.fep.util.CmdUtil;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.EventUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.Hex;
import com.aimir.model.BaseObject;
import com.aimir.model.device.EnergyMeter;
import com.aimir.model.device.Meter;
import com.aimir.model.device.OperationList;
import com.aimir.model.system.Code;
import com.aimir.model.system.DeviceModel;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.TimeLocaleUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Send Event Test Class
 * 
 * @author J.S Park (elevas@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2011-08-24 15:59:15 +0900 $,
 */
public class BCDTest
{
    private static Log log = LogFactory.getLog(BCDTest.class);
    
    @Test
    public void test_bcd()
    {
        try {
            byte[] bcd = new byte[] {0x34, 0x12, (byte)0x90, 0x78, 0x56, 0x34, 0x12};
            log.info("bcd[" + Hex.decode(bcd) + "] value[12345678901.234]");
            DataUtil.convertEndian(bcd);
            log.info("convert["+Hex.decode(bcd)+"]");
            log.info("exp 2[" + (234.0 * Math.pow(10, 0))/1000 + "]");
            
            bcd = Hex.encode("C014284E");
            log.info("Long[" + DataFormat.getLongToBytes(bcd)+"]");
            log.info("DateTime["+ DateTimeUtil.getDateString(DataFormat.getLongToBytes(bcd)*1000) + "]");
            
            DataFormat.convertEndian(bcd);
            log.info("Long[" + DataFormat.getLongToBytes(bcd)+"]");
            log.info("DateTime["+ DateTimeUtil.getDateString(DataFormat.getLongToBytes(bcd)*1000) + "]");
            
            byte[] b = new byte[] {(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xF7};
            if (b[0] == (byte)0xFF) {
                /*
                for (int i = 0; i < b.length; i++) {
                    b[i] = (byte)(0xFF & b[i]); 
                }
                */
                log.info(Hex.decode(b) + " " + (double)DataFormat.getIntTo4Byte(b));
            }
            
            String eui64 = "000D6F00003A580B";
            log.info(Hex.decode(Hex.encode(eui64)));
            
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse("{1:'개방',2:'수동차단'}");
            JsonObject obj = element.getAsJsonObject();
            log.info("1:" + obj.get("1"));
            Set<Map.Entry<String,JsonElement>> set = obj.entrySet();
            Map.Entry<String, JsonElement> entry = null;
            for (Iterator i = set.iterator(); i.hasNext(); ) {
                entry = (Map.Entry)i.next();
                log.info("key=" + entry.getKey() + ", value=" + entry.getValue().getAsString());
            }
            
            log.info("parse byte[" + Byte.parseByte("10")+"]");
            
            EnergyMeter meter = new EnergyMeter(); 
            log.info(meter instanceof BaseObject);
            
            TimeZone tz = TimeZone.getTimeZone("Asia/Seoul");
            Calendar cal = Calendar.getInstance(tz);
            cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
            log.info("day type[" + cal.get(Calendar.DAY_OF_WEEK) + "]");
            // cal.setTimeInMillis(1329609600000l);
            log.info("Time[" + cal.toString() + "] Timezone[" + cal.getTimeZone().getDisplayName() + "] DST[" + cal.getTimeZone().getDSTSavings() + "]");
            log.info("DateTime[" + DateTimeUtil.getDateString(1329609600000l)+"]");
            
            log.info("DateTime[" + DateTimeUtil.getDateString(cal.getTimeInMillis()));
            
//            Properties envs = System.getProperties();
//            Object ent = null;
//            for (Iterator i = envs.keySet().iterator(); i.hasNext();) {
//                ent = i.next();
//                log.info("entry[" + ent + "] value[" + envs.get(ent) + "]");
//            }
            
            SMIValue smiValue = DataUtil.getSMIValueByObject("mdID", "000D293EI88230EE");
            log.info(Hex.decode(smiValue.getVariable().encode()));
            smiValue = DataUtil.getSMIValueByObject("mdSerial", "0000001");
            byte[] serial = DataUtil.fillCopy(smiValue.getVariable().encode(), (byte)0x00, 20);
            log.info(Hex.decode(serial) + " LEN[" + serial.length + "]");
            smiValue = DataUtil.getSMIValueByObject("mdTime", "20120424145000");
            log.info(Hex.decode(smiValue.getVariable().encode()));
            long seed = System.currentTimeMillis() % 1000;
            Random r = new Random(seed);
            DecimalFormat df = new DecimalFormat("#");
            log.info("Seed # " + seed + " Random # " + r.nextInt() + " Math.random # " + df.format((Math.random() * 1000)));
            
            log.info(DataUtil.getIntTo2Byte(new byte[]{0x3F, 0x10}));
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            log.info(sdf.format(sdf.parse("2000/00/00 00:00:00")));
            
            log.info(String.format("value_%s%02d%02d", "2012", 9, 3));
            
            byte DF = (byte)0x8A;
            log.info(DF & 0x3F);
            
            log.info(cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, new Locale("English")));
            
            String tests = "63-93-2939";
            log.info("REPLACE[" + tests + "] TO [" + tests.replace("-", "") + "]");
            
            log.info(df.format(Double.MAX_VALUE));
            log.info(Double.POSITIVE_INFINITY);
            
            log.info(new String(Hex.encode("313430323132")));
            // 303030313034303032433134
            log.info(Hex.decode(Hex.encode(new String(Hex.encode("303030313033313733323339")))));
            byte[] version = new byte[] {0x01, 0x00};
            log.info(DataUtil.getIntTo2Byte(version));
            int _version = DataUtil.getIntTo2Byte(new byte[]{0x01, 0x00});
            log.info(_version);
            
            // sendSMS("Hello");
            log.info("5000".compareTo("2348"));
            
            log.info(new String(Hex.encode("312E302E30")));
            log.info(new String(Hex.encode("4732363630")));
            
            KamstrupCIDMeta.CID cid = KamstrupCIDMeta.CID.GetType;
            // String[] result = cid.getResponse(Hex.encode("0169001801"));
            // log.info(result[0] + " " + result[1]);
            
            log.info(String.format("%06.0f", Math.floor(0.23)));
            
            String sms = new String("32 37 38 32 38 31 35 33 31 34 37 22 2c 31 34 35 2c 22 31 32 2f 30 39 2f 31 32 2c 31 34 3a 30 31 3a 33 34 2b 30 38 22 2c 22 31 32 2f 31 30 2f 32 38 2c 31 35 3a 33 36 3a 35 32 2b 30 38 22 2c 37");
            sms = sms.replace(" ", "");
            log.info(new String(Hex.encode(sms)));
            String res = "OK";
            while (res.lastIndexOf("OK") == -1);
            log.debug(res.indexOf("OK"));
            
            UINT mcu = new UINT(11010);
            log.debug(Hex.decode(mcu.encode()));
            
            RandomDataGenerator rr = new RandomDataGenerator();
            log.debug(rr.nextInt(0, 12));
            log.debug(rr.nextInt(0, 12));
            log.debug(rr.nextInt(0, 12));
            log.debug(rr.nextInt(0, 12));
            log.debug(rr.nextInt(0, 12));
            log.debug(rr.nextInt(0, 12));
            log.debug(rr.nextInt(0, 12));
            log.debug(rr.nextInt(0, 12));
            log.debug(rr.nextInt(0, 12));
            log.debug(rr.nextInt(0, 12));
            log.debug(rr.nextInt(0, 12));
            log.debug(rr.nextInt(0, 12));
            log.debug(rr.nextInt(0, 12));
            log.debug(rr.nextInt(0, 12));
            log.debug(rr.nextInt(0, 12));
            log.debug(rr.nextInt(0, 12));
            log.debug(rr.nextInt(0, 12));
            
            TIMESTAMP te = new TIMESTAMP("20130807111700");
            log.debug(Hex.decode(te.encode()));
            log.debug(Hex.decode(DataUtil.get4ByteToInt(45897)));
            
            df = new DecimalFormat("00.000");
            log.info(df.format(316.000));
            
            log.info(Hex.decode(Hex.encode("000D6F00003140AA")));
            
            String m = new String("K3260001");
            log.info(Hex.decode(m.getBytes()));
            
            byte[] bb = Hex.encode("B4DCBFF8B1B8C3BB");
            log.info(DataUtil.getIntToBytes(bb));
            
            sdf = new SimpleDateFormat("yyyyMMdd");
            Calendar from = Calendar.getInstance();
            Calendar today = Calendar.getInstance();
            from.setTime(sdf.parse("20131031"));
            today.setTime(sdf.parse("20131031"));
            log.info(today.equals(from));
            
            DeviceModel model = new DeviceModel();
            Set<OperationList> ops = new HashSet<OperationList>();
            OperationList op = new OperationList();
            Code opCode = new Code();
            opCode.setCode(MeterCommand.ON_DEMAND_METERING.getCode());
            op.setOperationCode(opCode);
            op.setParamType(1);
            ops.add(op);
            model.setOperationList(ops);
            CmdUtil.convertOffsetCount(model, 15, null, "201310311600", "201310311700");
            
            String[] billDates = new String[] {"0", "1", "2"};
            log.info(ArrayUtils.contains(billDates, "3"));
            
            String locdetail = "140028C0FABED0C1D629C1D7B0EEB8AE5F3835322D32";
            log.info(new String(Hex.encode(locdetail), "EUC-KR").substring(2));
            
            log.info("20130101".compareTo("20131226") + " " + "20131231".compareTo("20131226"));
            log.info(Math.pow(2, 2));
            String meteringvalue = "9999999.998";
            double maxvalue = 1.0;
            for (int i = 0; i < meteringvalue.substring(0, meteringvalue.indexOf(".")).length(); i++) {
                maxvalue *= 10;
            }
            log.info(maxvalue-Double.parseDouble(meteringvalue));
            
            String url = "service:jmx:rmi:///jndi/rmi://AIMIR_FEP1:1616/jmxrmi,service:jmx:rmi:///jndi/rmi://AIMIR_FEP2:1616/jmxrmi";
            String[] activemqList = url.split(",");
            for (String s : activemqList) {
                log.info("Empty[" + s.isEmpty() + "]" + s);
            }
            
            Locale locale = new Locale("sw", "KE");
            String[] styleNames = { "FULL", "LONG", "MEDIUM", "SHORT" };
            int[] styles = { java.text.DateFormat.FULL, java.text.DateFormat.LONG, java.text.DateFormat.MEDIUM, java.text.DateFormat.SHORT };
            DateFormat fmt = null;
            log.info("\nThe Date for " + locale.getDisplayCountry() + ":");
            for (int i = 0; i < styles.length; i++) {
                fmt = DateFormat.getDateInstance(styles[i], locale);
                log.info("\tIn " + styleNames[i] + " is " + fmt.format(new Date()));
            }
            
            log.info(locale.getCountry());
            SimpleDateFormat datef14 = new SimpleDateFormat(TimeLocaleUtil.getDateFormat(14, "sw", "KE"));
            
            log.info(datef14.format(new Date()));
            
            datef14 = (SimpleDateFormat)DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);
            log.info(datef14.format(new Date()));
            
            log.info(Hex.decode(new byte[]{(byte)CommonConstants.DataSVC.Gas.getCode()}));
            log.info("[" + DataUtil.getSMIValueByObject("mdID", "01047263420   ").getVariable().encode().length + "]");
            log.info((0x40 & 0x20) >> 5);
            
            log.info(DataUtil.getIntTo2Byte(new byte[]{0xFF^0xFF, 0x9C^0xFF}) + 1);
            
            sdf = new SimpleDateFormat("yyyyMMddHHmm");
            cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            log.info(sdf.format(cal.getTime()));
            
            log.info(Pattern.matches("(18|17|14)([0-9]{6}|[0-9]{7})|(190)([0-9]{5})", "14313841"));
            
            log.info(DateTimeUtil.getDST("Asia/Seoul", DateTimeUtil.getDateString(new Date())));
            
            b = new byte[] {0x00, 0x01, 0x12, 0x33, 0x56};
            log.info("STR[" + new String(b) + "] BYTE[" + Hex.decode(new String(b).getBytes()) + "]");
            
            byte[][] req = KamstrupCIDMeta.getRequest(new String[]{"SetClock","",""});
            log.info(Hex.decode(req[0]) + ", " + Hex.decode(req[1]));
            
            log.info((int)0x6e17 >> 8);
            
            log.info(Hex.decode(CRCUtil.calculate_Xmodem_Ccitt(new byte[]{0x20, 0x5e, 0x66})));
            log.info(Hex.decode(new byte[]{~(byte)0x80}));
            
            log.info(Hex.decode(new String(Hex.encode("030000002483670051C8DF3307DE0C1D0F2A09000000FFFE000001CD01002483670151C8DF4307DE0C1D0F2825000000FFFE000001A502002483670351C8DF7007DE0C1D0F3033000000FFFE000001A6")).getBytes()));
            log.info(new String(Hex.encode("323030204B687A000000")));
            log.info(new String(Hex.encode("3031313730393036323331000000000000000000")));
            byte[] l = Hex.encode("EA0F0000");
            DataUtil.convertEndian(l);
            log.info(Hex.decode(l) + " " + DataUtil.getIntTo4Byte(l) + " " + (char)0x47);
            log.info(Math.pow(10, 0));
            log.info(new String(Hex.encode("4E414D522D5032303953520000000000000000000")).trim());
            
            byte[] barr = new OctetString("한글").getValue();
            boolean asHex = false;
            
            if (barr != null) {
                for(int i = 0; i < barr.length; i++) {
                    byte _b = barr[i];
                    if((_b < 32 && _b != 10 && _b != 13) ||  _b == 127) {
                        asHex = true;
                        break;
                    }
                }
            }
            
            log.info(asHex + " " + new OctetString("한글").toString());
            
            String s = "s";
            String encode = "utf-8";
            s.getBytes(encode);
            
            String filename = "D:\\workspace_3\\aimir-fep-exec\\uncompress\\win32\\bin\\uncompress.exe";
            File file = new File(filename);
            log.info("ab_path[" + file.getAbsolutePath() + "] path[" + file.getPath() + "]");
            
            long startTime = System.currentTimeMillis();
            b = Hex.encode("1C020000DD070B0B0B1338E20600000400640000000F403F1003E933040000E620FA391A0D3C403F10000D0204440002B0780001020442000006E20013020442000006E2001702044200000000001B02044200000000001F02044200000000C3BF0D0F403F10003A0004000000013FC56E0D36403F10041E21020000E2041F210200000004202102000000043422044300000000043522044300000000043622044300000000B4EC0D3F403F1004193508000000000000000000040916044300000000041A3508000000000000000000040C16044300000000041B35080000000000000000005E050D34403F1003EB3004000002002703EA2F04000001B53303EC2E0400000012160417350800820117130B0B0B0D04150001000040640D30403F1003F2330A0000000000000000E620FA003333040000E620FA003433040000000000003533040000000000B8990D40403FA20705000504173508008002000000010C0B0001020442000006E20013020442000006E2001702044200000000001F02044200000000000500051068920D2E403FA20703000504193508008003000F17120B0B0027160443000000DE002B160443000004D2000500051097FD0D09403FAA0F0F81566C0DF6403FA206054832041735080002010000000B0B0D0001020442000006E200020204420000000000030E04420000000300040E0442000003A448330201000F000B0B0D000006E20000000000000003000003A448340201001E000B0B0D000006E20000000000000003000003A448350201002D000B0B0D000006E20000000000000003000003A4483602010000010B0B0D000006E20000000000000003000003A448370201000F010B0B0D000006E20000000000000003000003A448380201001E010B0B0D000006E20000000000000003000003A448390201002D010B0B0D000006E20000000000000003000003A44839485F0040C20DF6403FA20605483A041735080002010000020B0B0D0001020442000006E200020204420000000000030E04420000000300040E0442000003A4483B0201000F020B0B0D000006E20000000000000003000003A4483C0201001E020B0B0D000006E20000000000000003000003A4483D0201002D020B0B0D000006E20000000000000003000003A4483E02010000030B0B0D000006E20000000000000003000003A4483F0201000F030B0B0D000006E20000000000000003000003A448400201001E030B0B0D000006E20000000000000003000003A448410201002D030B0B0D000006E20000000000000003000003A44841485F0064A10DF6403FA206054842041735080002010000040B0B0D0001020442000006E200020204420000000000030E04420000000300040E0442000003A448430201000F040B0B0D000006E20000000000000003000003A448440201001E040B0B0D000006E20000000000000003000003A448450201002D040B0B0D000006E20000000000000003000003A4484602010000050B0B0D000006E20000000000000003000003A448470201000F050B0B0D000006E20000000000000003000003A448480201001E050B0B0D000006E20000000000000003000003A448490201002D050B0B0D000006E20000000000000003000003A44849485F00D9770DF6403FA20605484A041735080002010000060B0B0D0001020442000006E200020204420000000000030E04420000000300040E0442000003A4484B0201000F060B0B0D000006E20000000000000003000003A4484C0201001E060B0B0D000006E20000000000000003000003A4484D0201002D060B0B0D000006E20000000000000003000003A4484E02010000070B0B0D000006E20000000000000003000003A4484F0201000F070B0B0D000006E20000000000000003000003A448500201001E070B0B0D000006E20000000000000003000003A448510201002D070B0B0D000006E20000000000000003000003A44851485F002A9C0DF6403FA206054852041735080002010000080B0B0D0001020442000006E200020204420000000000030E04420000000300040E0442000003A448530201000F080B0B0D000006E20000000000000003000003A448540201001E080B0B0D000006E20000000000000003000003A448550201002D080B0B0D000006E20000000000000003000003A4485602010000090B0B0D000006E20000000000000003000003A448570201000F090B0B0D000006E20000000000000003000003A448580201001E090B0B0D000006E20000000000000003000003A448590201002D090B0B0D000006E20000000000000003000003A44859485F0048240DC2403FA20605485A0417350800020100000A0B0B0D0001020442000006E200020204420000000000030E04420000000300040E0442000003A4485B0201000F0A0B0B0D000006E20000000000000003000003A4485C0201001E0A0B0B0D000006E20000000000000003000003A4485D0201002D0A0B0B0D000006E20000000000000003000003A4485E020100000B0B0B0D000006E20000000000000003000003A4485F0201000F0B0B0B0D000006E20000000000000003000003A4485F485F0089FA0D");
            long endTime = System.currentTimeMillis();
            log.info("STR to BYTE[" + (endTime - startTime) + "]");
            startTime = System.currentTimeMillis();
            s = Hex.decode(b);
            endTime = System.currentTimeMillis();
            log.info("BYTE to STR[" + (endTime - startTime) + "]");
            
            byte[][] kvh_req = KamstrupCIDMeta.getRequest(new String[]{"GetRegister","1326"});
            log.debug("REQ[" + Hex.decode(kvh_req[0]) + "] VAL[" + Hex.decode(kvh_req[1]) + "]");
            
            byte[] kvh_res = Hex.encode("403F10052E1E0443000000008EBF0D");
            if (kvh_res[0] == 0x40) {
                byte[] bx = new byte[kvh_res.length - 6];
                System.arraycopy(kvh_res, 3, bx, 0, bx.length);
                CID.GetRegister.getResponse(bx);
            }
            
            File[] files = new File("C:\\temp").listFiles();
            for (File f : files) {
                log.info(f.getAbsolutePath());
            }
            log.info(new String(Base64.encodeBase64(",211.232.103.253:8000".getBytes())));
            log.info(new String(Base64.decodeBase64("LDIxMS4yMzIuMTAzLjI1Mzo4MDAw".getBytes())));
            
            byte[] len = Hex.encode("24000000");
            DataUtil.convertEndian(len);
            log.info(DataUtil.getLongToBytes(len));
            log.info((char)0x42);
            log.info(DataUtil.getIntTo4Byte(new byte[]{0x01, 0x23, (byte)0xE3, 0x16}));
            
            String customerId = "200260599-01,200261396-01,200261399-01,200262618-01,200262627-01,200262709-01,200263288-01"
                    + ",200263453-01,200263488-01,200263542-01,200264131-01,200264303-01,200264914-01,200264830-01,200264957-01"
                    + ",200258097-01,200256513-01,200257139-01,200257144-01,200259390-01,200259429-01,200261202-01,200253647-01"
                    + ",200253648-01,200260444-01,200260516-01,200256264-01,200260990-01,200250089-01,200250114-01,200249867-01"
                    + ",200255189-01,200250256-01,200256067-01,200256130-01,200248267-01,200256152-01,200254388-01,200255939-01"
                    + ",200259101-01,200259102-01,200258953-01,200254947-01,200258620-01,200258668-01,200254094-01,200253862-01"
                    + ",200258790-01,200249400-01,200247736-01,200249290-01,200252579-01,200252640-01,200249410-01,200249426-01"
                    + ",200246824-01,200246825-01,200246836-01,200251797-01,200251325-01,200251400-01,200250931-01,200252108-01"
                    + ",200251581-01,200250503-01,200252247-01,200268286-01,200265696-01,200267187-01,200265275-01,200265557-01"
                    + ",200265595-01,200267569-01,200268160-01,200247169-01,200248063-01,200248940-01,200248967-01,200248849-01"
                    + ",200267595-01,200265187-01";
            StringTokenizer st = new StringTokenizer(customerId, ",");
            log.info(st.countTokens());
            while (st.hasMoreTokens()) {
                log.info(st.nextToken());
            }
            
            log.info("570950826161500".substring(0, 15-10) + " " + "570950826161500".substring(15-10));
            byte[] year = DataUtil.get2ByteToInt("57095");
            DataUtil.convertEndian(year);
            log.info(DataUtil.getIntTo2Byte(year));
            log.info(Math.pow(10, -3));
            
            sdf = new SimpleDateFormat("yyyyMMdd");
            Calendar sc = Calendar.getInstance();
            sc.setTime(sdf.parse("20150710"));
            Calendar ec = Calendar.getInstance();
            ec.setTime(sdf.parse("20151017"));
            log.info(ec.compareTo(sc));
            log.info(28380.203 / 120 * 30);
            log.info((char)0x4D);
            log.info((char)0x54);
            log.info("192928382".startsWith("192"));
        }
        catch (Exception e) {
            log.error(e, e);
        }
    }
    
    @Ignore
    public void sendSMS() {
        String phonelist = FMPProperty.getProperty("sms.phonelist");
        if (phonelist != null && !"".equals(phonelist)) {
            StringTokenizer st = new StringTokenizer(phonelist, ",");
            
            try {
                HttpClient client = new HttpClient();
                
                HostConfiguration hostConfiguration = new HostConfiguration();
                hostConfiguration.setHost("203.170.230.170", 4140);
                // TRANSID=BULK&CMD=SENDMSG&FROM=9009000&TO=6618881234&REPORT=N&CHARGE=Y&CODE=TEXT&CTYPE=TEXT&CONTENT=test
                
                PostMethod method = new PostMethod();
                method.setPath("getmsg.php");
                
                while (st.hasMoreTokens()) {
                    NameValuePair[] params = new NameValuePair[8];
                    // params[0] = makeQueryString("TRANSID", "BULK");
                    params[0] = makeQueryString("CMD", "SENDMSG");
                    params[1] = makeQueryString("FROM", "MEATH12345");
                    params[2] = makeQueryString("TO", st.nextToken());
                    params[3] = makeQueryString("REPORT", "Y");
                    params[4] = makeQueryString("CHARGE", "Y");
                    params[5] = makeQueryString("CODE", "Mitsubishi_BulkSMS");
                    params[6] = makeQueryString("CTYPE", "TEXT");
                    params[7] = makeQueryString("CONTENT", "Hello!");
                    method.setQueryString(params);
                    
                    int result = client.executeMethod(hostConfiguration, method);
                    log.debug("Result[" + result + "] REQ[" + method.getQueryString() + "]");
                    log.debug("Response[" + method.getResponseBodyAsString() + "]");
                }
            }
            catch (Exception e) {
                log.error(e, e);
            }
        }
    }
    
    private NameValuePair makeQueryString(String name, String value) {
        NameValuePair param = new NameValuePair();
        param.setName(name);
        param.setValue(value);
        return param;
    }
    
    @Ignore
    public void test_flag() {
        int flag = 4608;
        int indicator = 24;
        StringBuffer tempString = new StringBuffer();
        int flagStatus[] = new int[16];
        int tamper_Indicator[] = new int[16];

        // i 는 bit 의 index라고 보면 된다.
        for (int i = 0; i < 16; i++) {
            // 2의 제곱 을 구하여 해당 bit 가 1일때의 수를 구한다.
            int sqrt = (int) Math.pow(2, i);

            // flag 값과 sqrt 값을 AND 연산을 하였을때 해당 index 의 Status 가 Active 된것이다.
            if ((flag & sqrt) != 0) {
                // flagStatus의 bit 배열을 1로 설정한다.
                flagStatus[i] = 1;
                
                if (tempString.length() != 0)
                    tempString.append(" / " + LR_FLAG_STATUS.values()[i].name());
                else
                    tempString.append(LR_FLAG_STATUS.values()[i].name());
            }

            // indicator 값과 sqrt 값을 AND 연산을 하였을때 해당 index 의 indicator 가 Active
            // 된것이다.
            if ((indicator & sqrt) != 0 && i < 5) {
                tamper_Indicator[i] = 1;
                if (tempString.length() != 0)
                    tempString.append(" / " + LOAD_TAMPER_INDICATOR.values()[i].name());
                else
                    tempString.append(LOAD_TAMPER_INDICATOR.values()[i].name());
                
            }
        }

        log.info(tempString.toString());
        
        
    }
        
        
    @Ignore
    public void test_parseChannelCount() {
        int channel = 5;
        String chByte = Integer.toBinaryString(channel);
        int internalChannelCnt = 0;
        int externalChannelCnt = 0;
        
        int LEN_MARKER_CHANNEL_AS_BIT = 16;
        
        String blank = "";
        if (chByte.length() < LEN_MARKER_CHANNEL_AS_BIT) {
            for (int i = chByte.length(); i < LEN_MARKER_CHANNEL_AS_BIT; i++) {
                blank += "0";
            }
            chByte = blank + chByte;
        }

        for (int i = chByte.length() - 1; i > 0; i--) {
            if (chByte.charAt(i) == '1') {
                if (i > 10) {
                    externalChannelCnt++;
                } else {
                    internalChannelCnt++;
                }
            }
        }
        int totalChannelCnt = internalChannelCnt + externalChannelCnt;
        log.info("internal[" + internalChannelCnt+"] external["+externalChannelCnt+"] total[" + totalChannelCnt+"]");
        
        String statusByte     = Integer.toBinaryString(DataUtil.getIntToBytes(new byte[]{0x40}));
        blank = "";
        EventLogData eventLog = null;

        if (statusByte.length() < 8) {
            for (int i = statusByte.length(); i < 8; i++) {
                blank += "0";
            }
            statusByte = blank + statusByte;
        }
        log.info("LP STATUS[" + statusByte + "]");
    }
    
    @Ignore
    public void deletesvn() {
        String path = "D:\\workspace_gwang_water";
        File root = new File(path);
        deletesvn(root);
    }
    
    private void deletesvn(File dir) {
        File[] files = dir.listFiles();
        for (File f : files) {
            if (f.isDirectory() && f.getName().equals(".svn")) {
                log.info(f.getAbsolutePath());
                deletefile(f);
                FileUtils.delete(f);
            }
            else if (f.isDirectory()) {
                deletesvn(f);
            }
        }
    }
    
    private void deletefile(File svndir) {
        File[] files = svndir.listFiles();
        for (File f : files) {
            if (f.isDirectory()) {
                deletefile(f);
            }
            FileUtils.delete(f);
        }
    }
}
