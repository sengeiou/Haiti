package com.aimir.fep.test;

import java.util.ArrayList;

import org.apache.mina.core.session.IoSession;

import com.aimir.fep.protocol.fmp.datatype.BYTE;
import com.aimir.fep.protocol.fmp.datatype.OID;
import com.aimir.fep.protocol.fmp.datatype.UINT;
import com.aimir.fep.protocol.fmp.datatype.WORD;
import com.aimir.fep.protocol.fmp.exception.FMPEncodeException;
import com.aimir.fep.protocol.fmp.frame.GeneralDataConstants;
import com.aimir.fep.protocol.fmp.frame.ServiceDataConstants;
import com.aimir.fep.protocol.fmp.frame.ServiceDataFrame;
import com.aimir.fep.protocol.fmp.frame.service.CommandData;
import com.aimir.fep.util.FrameUtil;
import com.aimir.fep.util.Hex;

public class FrameUtilTest {

	public static void main(String[] args) { 
		
        ServiceDataFrame frame = new ServiceDataFrame();
        CommandData command = null;

        command = new CommandData();
        command.setAttr(ServiceDataConstants.C_ATTR_RESPONSE);
        command.setTid(FrameUtil.getCommandTid());           
        command.setCnt(new WORD(0));
        command.setErrCode(new BYTE(0));
        command.setCmd(new OID("111.1.0"));
        long mcuId = 0L;
        frame.setMcuId(new UINT(mcuId));
        frame.setSvc(GeneralDataConstants.SVC_C);
        frame.setAttr((byte)(GeneralDataConstants.ATTR_START | GeneralDataConstants.ATTR_END));

        
        String dataStr = "6F01040300000500010B00100035313030303030303030303030303131010C0040005AD765768AFEBAA81C76663CCCBBFCE987C01EB216861B334E99742C7B068910CCEC4015874513DA0141BD121B1ED4DE7226B9984B20360E79F9A445278B8359010C0040009C2CA7992D7ED4237230516489238C43663E5E4EA6E824C980C8E79416C043415FC77753D552723F82005EEB1D640A6C56C441CE52A3C873DA1DD8E461EDA897010C004000AEEEA8D55559FCAA4164D4B9473BA9AA596FDCD899D699058F5F41FC574EA75F2B3B353F9787EBFA98D726CED71F3322CBB0D01BE4A1A78EAE32E2A883574794010C004000C4558E6CD5E3FA934C0AD8163AC012EA16CC810CE514012CA1A86ADD625B898A11202CD4EC51E8C34643CB4543806AFC6E71E5B6816F0876B264C06F8ADE9502";
        frame.setLength(dataStr.length()/2);
        frame.setSvcBody(Hex.encode(dataStr));
        try {
			frame.setSvcBody(command.encode());

		} catch (FMPEncodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
                
		try {
			ArrayList<byte[]> lists = FrameUtil.makeMultiEncodedFrame(frame.encode(), null);
			
			for(byte[] b: lists){
				System.out.println(Hex.decode(b));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}


