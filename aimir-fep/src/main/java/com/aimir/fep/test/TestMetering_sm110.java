package com.aimir.fep.test;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.aimir.fep.protocol.fmp.client.Client;
import com.aimir.fep.protocol.fmp.client.ClientFactory;

import com.aimir.fep.protocol.fmp.common.LANTarget;
import com.aimir.fep.protocol.fmp.datatype.WORD;
import com.aimir.fep.protocol.fmp.frame.service.MDData;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;
import com.aimir.util.DateTimeUtil;

/**
 * Send Event Test Class
 * 
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
public class TestMetering_sm110
{
    @SuppressWarnings("unused")
	private static Log _log = LogFactory.getLog(TestMetering_sm110.class);
    
    static {
        DataUtil.setApplicationContext(new ClassPathXmlApplicationContext(new String[]{"/config/spring.xml"}));
    }

    public enum GETable{

    	A055("41303535"),
    	B055("42303535"),
        S001("53303031"),
        S003("53303033"),
        S005("53303035"),
        S011("53303131"),
        S012("53303132"),
        S015("53303135"),
        S016("53303136"),
        S021("53303231"),
        S022("53303232"),
        S023("53303233"),
        S025("53303235"),
        S026("53303236"),
        S053("53303533"),
        S055("53303535"),
        S061("53303631"),
        S062("53303632"),
        S063("53303633"),
        S064("53303634"),
        S071("53303731"),
        S072("53303732"),
        S076("53303736"),
        S130("53313330"),
        S132("53313332"),
        M000("4D303030"),
        M013("4D303133"),
        M067("4D303637"),
        M070("4D303730"),
        M072("4D303732"),
        M075("4D303735"),
        M078("4D303738"),
        M113("4D313133"),
        N023("4E303233"),
        N025("4E303235"),
        N026("4E303236"),
        N067("4E303637"),
        N078("4E303738"),
        T001("54303031");

        private String tableName;
        
        GETable(String tableName) {
            this.tableName = tableName;
        }
        
        public String getTableName() {
            return this.tableName;
        }
    }
    
    /**
     * constructor
     */
    public TestMetering_sm110()
    {
    }
    /**
     * start to send event 
     *
     * @throws Exception
     */
    public void sendEvent() throws Exception
    {
    	LANTarget target = new LANTarget("127.0.0.1",8001);
        target.setTargetId("2");
        Client client = ClientFactory.getClient(target);
        
        MDData mdData = new MDData();
        mdData.setCnt(new WORD(1));
        String timestamp = DateTimeUtil.getDateString(new Date());
        byte[] year2 = DataUtil.get2ByteToInt(Integer.parseInt(timestamp.substring(0, 4)));
        DataUtil.convertEndian(year2);
        String hYear2 = Hex.decode(year2);
        String hYear1 = Hex.decode(new byte[]{DataUtil.getByteToInt(Integer.parseInt(timestamp.substring(2, 4)))});
        String hMonth = Hex.decode(new byte[]{DataUtil.getByteToInt(Integer.parseInt(timestamp.substring(4, 6)))});
        String hDay = Hex.decode(new byte[]{DataUtil.getByteToInt(Integer.parseInt(timestamp.substring(6, 8)))});
        String hHour = Hex.decode(new byte[]{DataUtil.getByteToInt(Integer.parseInt(timestamp.substring(8, 10)))});
        String hMin = Hex.decode(new byte[]{DataUtil.getByteToInt(Integer.parseInt(timestamp.substring(10, 12)))});
        String hSec = Hex.decode(new byte[]{DataUtil.getByteToInt(Integer.parseInt(timestamp.substring(12, 14)))});
        
        StringBuffer mdBuf = new StringBuffer();
        
        
        String eui64 = "000B1200750C5FAA";
        String meterId = "3230303730313239313130303238320000000000";
        String sensorType = "01";
        String serviceType = "01";
        String vendor = "02";
        String dataCount = "0100";
        //String length = "A900";
        //String length = "E50A";
        String length = "D50A";
        mdBuf.append(eui64);
        mdBuf.append(meterId);
        mdBuf.append(sensorType);
        mdBuf.append(serviceType);
        mdBuf.append(vendor);
        mdBuf.append(dataCount);
        mdBuf.append(length);
        mdBuf.append(hYear2 + hMonth + hDay + hHour + hMin + hSec); // md data timestamp
        
        //metering data table
        
        mdBuf.append(GETable.S001.tableName);
        mdBuf.append("0020");//table length //미터 기본정보//불변
        mdBuf.append("47452020534D3131302020200106010032303037303132393131303032383220");
        
        mdBuf.append(GETable.S003.tableName);
        mdBuf.append("0005");//table length //미터 상태
        mdBuf.append("0100000010");
        
        mdBuf.append(GETable.S005.tableName);
        mdBuf.append("0014");//table length//미터 시리얼번호//불변
        mdBuf.append("3230303730313239313130303238322020202020");
        
        mdBuf.append(GETable.S021.tableName);
        mdBuf.append("000A");//table length//불변
        mdBuf.append("5F100205050A01041900");
        
        mdBuf.append(GETable.S022.tableName);
        mdBuf.append("001F");//table length//불변
        mdBuf.append("00FFFFFFFF28FFFFFFFF1FFFFFFFFFFFFFFFFFFFFF00000101020203030404");

        mdBuf.append(GETable.S023.tableName);
        mdBuf.append("02D6");//table length
        mdBuf.append("00E44D1D0800000000000000000000000000000000000000000000000000000708190E00000000000000DF1A0C0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000E44D1D0800000000000000000000000000000000000000000000000000000708190E00000000000000DF1A0C0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000");
                  
        mdBuf.append(GETable.S025.tableName);//SELF READ TABLE
        mdBuf.append("02DC");//table length
        mdBuf.append(hYear1 + hMonth + hDay+"00000002368e360a00000000000000000000000000000000000000000000000000000807020d1ea891190000001ef3170000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000368e360a00000000000000000000000000000000000000000000000000000807020d1ea891190000001ef3170000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000");
        
        
        mdBuf.append(GETable.S053.tableName);
        mdBuf.append("0006");//table length//불변
        mdBuf.append("FF0000FFFFFF");
        
        mdBuf.append(GETable.S055.tableName); //current meter time
        mdBuf.append("0009");//table length
        mdBuf.append(hYear1 + hMonth + hDay + hHour + hMin + hSec+ "45010F"); // md data timestamp       
        
        /**
         * ex) 
         * LP_MEMORY_LEN      : 4c fe 00 00 
         * LP_FLAGS           : 50 04 
         * LP_FORMATS         : 10 
         * LEN_NBR_BLKS_SET1  : 3c 00 
         * NBR_BLKS_INTS_SET1 : 60 00 
         * NBR_CHNS_SET1      : 04 
         * INT_TIME_SET1      : 0f
         */
        mdBuf.append(GETable.S061.tableName); 
        mdBuf.append("000d");//table length//LP Table
        mdBuf.append("b229000050041012006000021e");
        

        /*
         * 	LEN_LP_SEL_SET1        = 3;
            LEN_INT_FMT_CDE1       = 1;
            LEN_SCALARS_SET1       = 2;
            LEN_DIVISOR_SET1       = 2;
         */
        mdBuf.append(GETable.S062.tableName);
        mdBuf.append("000f");//table length//LP Table
        mdBuf.append("0114000115011001000100c800c800");
        
        
        /*
         * LP_SET_STATUS_FLAG = 1
			NBR_VALID_BLOCKS   = 2
			LAST_BLOCK_ELEMENT = 2
			LAST_BLOCK_SEQ_NUM = 4
			NBR_UNREAD_BLOCKS  = 2
			NBR_VALID_INT      = 2
         */
        mdBuf.append(GETable.S063.tableName); 
        mdBuf.append("000d");
        mdBuf.append("2212001100c701000012005d00");

        mdBuf.append(GETable.S064.tableName);
        mdBuf.append("0251");//table length//LP Table

        mdBuf.append(hYear1 + hMonth + hDay + hHour + "00" + "78a3a697000000000000000000006c0000000000a700000000007300000000009000000000008200000000007d00000000009500000000006e0000000000a600000000006d00000000009d0000000000760000000000fe03000000008e0000000000070100000000fa0000000000d90000000000190100000000da00000000001c01000000007e0100000000ec0000000000fb0000000000d800000000001801000000007d0100000000e900000000001b01000000005001000000000b0100000000e40000000000f60000000000eb0000000000ae00000000003f0100000000fd00000000000d0100000000c101000000008c03000000001605000000008a01000000008d0100000000ef0000000000060100000000e20000004000f30000000000f90000000000e20000000000a100000000006a0000000000a800000000006b00000000009800000000007f00000000007e00000000009400000000006a0000000000a800000000006d00000000009d0000000000640100000000cd0100000000740100000000f40000000000da0000000000330100000000fb0000000000af0000000000ba0000000000500100000000a90000000000af00000000008b0000000000be00000000001c0100000000c000000000009f0000000000d40000000000bc0000000000ac0000000000eb0000000000830000000000b50000000000570100000000720100000000d907000000004d0300000000560200000000bf0100000000140500000000ba0100000000640100000000f0000000ffffffffffffffffffffffffffffffffffff");
        
        mdBuf.append(GETable.S130.tableName);
        mdBuf.append("0002");//table length//Relay switch Table
        mdBuf.append("0100");
        
        mdBuf.append(GETable.M067.tableName);
        mdBuf.append("00BA");//table length//TRANS_RATIO  //불변
        mdBuf.append("00A0DB215D0000A0DB215D0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000A0DB215D000000000000000000000000000000000000000000000000000A000A0001003030303030303030303030300000000000003C001E000000000000202020");
        
        mdBuf.append(GETable.M070.tableName);
        mdBuf.append("002F");//table length//DISP_SCALAR//불변
        mdBuf.append("0200000000010000003333600000000000000000000000000000000000000000000000000000000020202020202020");
        
        mdBuf.append(GETable.M075.tableName);
        mdBuf.append("000C");//table length//순시값
        mdBuf.append("3617B030DD7001C40900C409");
        
        mdBuf.append(GETable.M078.tableName);
        mdBuf.append("0074");//table length//CUM_POWER_OUTAGE_SECS
        mdBuf.append("07061404354855414C4F4E472020200706140F0D4855414C4F4E4720202033000000000000000414");
        mdBuf.append("070A13140E000000000000000000573809000400");
        mdBuf.append("070A01090CA70000000000000000000000000000000000000000000000000000000000000000070702161D57270200020000000000000000");
                    
        mdBuf.append(GETable.M113.tableName);
        mdBuf.append("0005");//table length//RMS_VOLTAGE_PHA
        mdBuf.append("0609000062");
       
        mdBuf.append(GETable.B055.tableName);
        mdBuf.append("0009");           
        mdBuf.append("0a070d00080942010f");
        
        mdBuf.append(GETable.A055.tableName);
        mdBuf.append("0009");   
        mdBuf.append("0a070d00080a42010f");
        
        mdBuf.append(GETable.T001.tableName);
        mdBuf.append("002C");//table length////불변
        mdBuf.append("3100000031000000000000000000000000000000C8080000F79D00000F0400001EB1000009000000E90B0000");
        
        
        System.out.println("mdBuf.length()="+mdBuf.length());
        byte[] md = Hex.encode(mdBuf.toString());
        mdData.setMdData(md);
        mdData.setMcuId("2");
        client.sendMD(mdData);
        client.close();
       
    }

    public static void main(String[] args)
    {
        TestMetering_sm110 mc = new TestMetering_sm110();
        try {
            mc.sendEvent();
        }catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
