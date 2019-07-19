package com.aimir.test.fep.pattern.metering;

import java.text.ParseException;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.protocol.fmp.datatype.UINT;
import com.aimir.fep.protocol.fmp.datatype.WORD;
import com.aimir.fep.protocol.fmp.frame.GeneralDataConstants;
import com.aimir.fep.protocol.fmp.frame.ServiceDataFrame;
import com.aimir.fep.protocol.fmp.frame.service.MDData;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FrameUtil;
import com.aimir.test.fep.util.DateTimeUtil;
import com.aimir.test.fep.util.Hex;


public class Testzip {

    private static Log log = LogFactory.getLog(Testzip.class);
    
    private String eui64 = "000B1200750C5FAA";
    private String meterId = "3230303730313239313130303238320000000000";
    
    public static void main(String[] args)
    {
    	
		int dcuCount = 1000;
		int nodeCount = 300;
    	
        for (int i=0; i < args.length; i+=2) {

            String nextArg = args[i];
            if (nextArg.startsWith("-dcuCount")) {
            	dcuCount = Integer.parseInt(args[i+1]);
            }
            else if (nextArg.startsWith("-nodeCount")) {
            	nodeCount = Integer.parseInt(args[i+1]);
            }
        }
        

        Testzip test = new Testzip();
    	try {
			MDData md = test.makeFrame();
			
			ServiceDataFrame frame = new ServiceDataFrame();
			frame.setAttrByte(GeneralDataConstants.ATTR_ACK);
			frame.setAttrByte(GeneralDataConstants.ATTR_START);
			frame.setAttrByte(GeneralDataConstants.ATTR_END);
			long mcuId = 11010;
			frame.setMcuId(new UINT(mcuId));
			frame.setSvcBody(md.encode());
			frame.setAttrByte(GeneralDataConstants.ATTR_COMPRESS);
			frame.setSvc(GeneralDataConstants.SVC_M);
			byte[] encodeFrame = frame.encodeWithCompress();
			log.debug("ZIP DATA=["+Hex.decode(encodeFrame)+"]");
			log.debug("ZIP DATA LEN=["+encodeFrame.length+"]");
			
			//5E 00 17 04 01 00 00 4D
			
			byte[] unzip = FrameUtil.unzlib(DataUtil.select(encodeFrame, 8, encodeFrame.length-8));
			//byte[] unzip = FrameUtil.ungzip(DataUtil.select(encodeFrame, 8, encodeFrame.length-8));
			log.debug("UNZIP DATA=["+Hex.decode(unzip)+"]");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }
    
    public MDData makeFrame() throws ParseException{
    	
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
        
        String sensorType = "01";
        String serviceType = "01";
        String vendor = "02";
        String dataCount = "0100";
        String length = "AF01";
        
        for(int i = 0; i < 1; i++){

            mdBuf.append(eui64);
            mdBuf.append(meterId);
            

            //mdBuf.append("000D6F00001E162B");
            //mdBuf.append("3733353030323836373438343331323000000000");
            mdBuf.append(sensorType);
            mdBuf.append(serviceType);
            mdBuf.append(vendor);
            mdBuf.append(dataCount);
            mdBuf.append(length);
            mdBuf.append(hYear2 + hMonth + hDay + hHour + hMin + hSec); // md data timestamp
            mdBuf.append("1C020000");
            mdBuf.append(hYear2 + hMonth + hDay + hHour + hMin + hSec);
            mdBuf.append("DD020000010200");
            // 하루전
            String preDay = DateTimeUtil.getPreDay(timestamp.substring(0,8));
            byte[] year3 = DataUtil.get2ByteToInt(Integer.parseInt(preDay.substring(0,4)));
            // DataUtil.convertEndian(year3);
            String hYear3 = Hex.decode(year3);
            String hMonth3 = Hex.decode(new byte[]{DataUtil.getByteToInt(Integer.parseInt(preDay.substring(4,6)))});
            String hDay3 = Hex.decode(new byte[]{DataUtil.getByteToInt(Integer.parseInt(preDay.substring(6,8)))});
            //log.info("LPDate[" + hYear3+hMonth3+hDay3);
            mdBuf.append(hYear3+hMonth3+hDay3);
            mdBuf.append("000002DD000000000000001A001C0009002C003D002E006000AA00E100D100000000000000000000000000000000000000000000");
            mdBuf.append(hYear3+hMonth+hDay);
            mdBuf.append("0000040A0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000002F41444E353533302D56312E312D3035303332320D0A02302E302E30283030373531303431290D0A302E302E31283734383433313230290D0A312E382E30283030303030372E33332A6B5768290D0A322E382E30283030303030302E30302A6B5768290D0A332E382E30283030303030302E32332A6B76617268290D0A342E382E30283030303030362E37312A6B76617268290D0A39362E382E30283030393132372A686F7572290D0A39372E39372E3128333130303030290D0A33322E372E30283231322E322A56290D0A35322E372E30283030302E302A56290D0A37322E372E30283030302E302A56290D0A33312E372E30283030302E302A41290D0A35312E372E30283030302E302A41290D0A37312E372E30283030302E302A41290D0A210D0A032B");           
        }
        
        //log.info("mdBuf.length()="+mdBuf.length());
        byte[] md = Hex.encode(mdBuf.toString());
        //log.info("mdBuf="+Hex.decode(md));
        mdData.setMdData(md);
        mdData.setMcuId("11010");
        
        return mdData;
    }
}
