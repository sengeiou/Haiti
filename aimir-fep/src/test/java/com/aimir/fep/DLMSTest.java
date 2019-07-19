package com.aimir.fep;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aimir.fep.bypass.decofactory.consts.HLSAuthForIESCO;
import com.aimir.fep.bypass.decofactory.consts.DlmsConstants;
import com.aimir.fep.bypass.decofactory.consts.DlmsConstantsForIESCO.AARE;
import com.aimir.fep.bypass.decofactory.consts.DlmsConstantsForIESCO.AARQ;
import com.aimir.fep.bypass.decofactory.consts.DlmsConstantsForIESCO.DlmsPiece;
import com.aimir.fep.bypass.decofactory.consts.HLSAuthForIESCO.HLSSecurityControl;
import com.aimir.fep.bypass.decofactory.decoframe.INestedFrame;
import com.aimir.fep.bypass.decofactory.decoframe.SORIA_DLMSFrame;
import com.aimir.fep.bypass.decofactory.decorator.NestedDLMSDecoratorForSORIA;
import com.aimir.fep.bypass.decofactory.decorator.NestedHDLCDecoratorForSORIA;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;
import com.aimir.util.DateTimeUtil;

public class DLMSTest {
	private static Logger logger = LoggerFactory.getLogger(DLMSTest.class);

	
	@org.junit.Test
	public void getReqGlobalCipheringTest() {
		try {
			String cyperTextValue = "040600C99AB743CE3872FD108DDE1CEE3580F698F6A27E972D";
			
			String callingApTitle = "0000000000000000";                            // Clinet에서 Server로 AARQ 보낼때 사용한 DlmsPiece.CLIENT_SYSTEM_TITLE
			HLSSecurityControl SC = HLSSecurityControl.AUTHENTICATION_ENCRYPTION;  // Ciper text 앞부분에서 확인가능함
			byte[] IC = DataUtil.readByteString("00000004");                       // 4byte, Ciper text 앞부분에서 확인가능함
			
			HLSAuthForIESCO aareAuth = new HLSAuthForIESCO(SC, "simhanger");
			byte[] plainText = aareAuth.doDecryption(IC, DataUtil.readByteString(callingApTitle), DataUtil.readByteString(cyperTextValue));
			logger.debug("### AssociationLN Plain TextValue = " + Hex.decode(plainText));
			
			
			// Encoding
			HLSAuthForIESCO auth = new HLSAuthForIESCO(SC, "simhanger");
			byte[] tagValue = auth.getTagValue(IC, DataUtil.readByteString(callingApTitle), plainText);
			logger.debug("### AssociationLN Cipher TextValue = " + Hex.decode(tagValue));
			
		} catch (Exception e) {
			logger.error("Error - " + e.getMessage(), e);
		}
	}
	
	
	public void decodeTagTest() {
		try {
			/*			7EA0380303520238
						E6E700
						CF 2A  -- glo-action-response [207] IMPLICIT OCTET STRING + Length(42byte)
						30       -- Security Control : AUTHENTICATION_ENCRYPTION(0x30)
						00000003   -- Invocation Counter
						EE1F62D858FA0D724FEFDC050BE0E830C8CBBD7C3D5BA308B2B556C616721BCD16FE5AAAA1   -- Cipher Text
					8AE1
					7E*/
		
			String cyperTextValue = "EE1F62D858FA0D724FEFDC050B37FC5B138428E9F72AD32CFB119F6B287635A1A9EEE8CE7B";
			
			String sToCCallingApTitle = "434C450C9E8322D0";
			HLSSecurityControl SC = HLSSecurityControl.AUTHENTICATION_ENCRYPTION;  // Ciper text 앞부분에서 확인가능함
			byte[] IC = DataUtil.readByteString("00000003");                       // 4byte, Ciper text 앞부분에서 확인가능함
			
			HLSAuthForIESCO aareAuth = new HLSAuthForIESCO(SC, "simhanger");
			byte[] plainText = aareAuth.doDecryption(IC, DataUtil.readByteString(sToCCallingApTitle), DataUtil.readByteString(cyperTextValue));
			logger.debug("### AssociationLN Plain TextValue = " + Hex.decode(plainText));
			logger.debug("");
			logger.debug("");
			logger.debug("");
			
			
			/*			C7 01  
						C1   
							00 
							01 
								00 
								09 11 
								10  
								00000002 
								5303946868346E19BF932963 
						
			*/			
			String responseTagValue = "8417FFB327A1FBD3CEE30D2A";
			String aareRespondingAPtitle = "434C450C9E8322D0";    // AARE로 받은 Server System Title
			String cToS = "58EB99BB64C78C8042744A5256654D8B";
			SC = HLSSecurityControl.AUTHENTICATION_ONLY;  
			IC = DataUtil.readByteString("00000002");     
			
			aareAuth = new HLSAuthForIESCO(SC, "simhanger");
			boolean result = aareAuth.doValidation(DataUtil.readByteString(aareRespondingAPtitle), IC, DataUtil.readByteString(cToS), DataUtil.readByteString(responseTagValue));
			
			logger.debug("### AssociationLN Result validation = " +result);
			
			
			
			
		} catch (Exception e) {
			logger.error("Error - " + e.getMessage(), e);
		}
	}
	
	public void aaa() {
		byte[] aareUInformation = DataUtil.readByteString("0421281F30000000012E1EF9DF04BF01432560CADA2AA0F996406E6BE853CA5AA6FA2F");
		
		byte[] initateResponse = new byte[DataUtil.getIntToByte(aareUInformation[1])];
		System.arraycopy(aareUInformation, 2, initateResponse, 0, initateResponse.length);
		
		logger.debug("initateResponse = {}", Hex.decode(initateResponse));
		
	}
	
	/*
	 *  인코딩 한 plain text 확인
	 */
	public void decodeTest() {
		try {
			String cyperTextValue = "91F973AD8F27F716BE73C526055BB95543EC7331DCD939DC20474865A7BE3348DB52DCA69DCC022D4539FC7F";
			
			String callingApTitle = "0000000000000000";                            // Clinet에서 Server로 AARQ 보낼때 사용한 DlmsPiece.CLIENT_SYSTEM_TITLE
			HLSSecurityControl SC = HLSSecurityControl.AUTHENTICATION_ENCRYPTION;  // Ciper text 앞부분에서 확인가능함
			byte[] IC = DataUtil.readByteString("00000003");                       // 4byte, Ciper text 앞부분에서 확인가능함
			
			HLSAuthForIESCO aareAuth = new HLSAuthForIESCO(SC, "simhanger");
			byte[] plainText = aareAuth.doDecryption(IC, DataUtil.readByteString(callingApTitle), DataUtil.readByteString(cyperTextValue));
			logger.debug("### AssociationLN Plain TextValue = " + Hex.decode(plainText));
			
			
			// Encoding
			HLSAuthForIESCO auth = new HLSAuthForIESCO(SC, "simhanger");
			byte[] tagValue = auth.getTagValue(IC, DataUtil.readByteString(callingApTitle), plainText);
			logger.debug("### AssociationLN Cipher TextValue = " + Hex.decode(tagValue));
			
		} catch (Exception e) {
			logger.error("Error - " + e.getMessage(), e);
		}
	}
	
	
	public void ttt() {
		int a = 3;
		System.out.println("a = " + a + " to hex => " + DataUtil.getByteToInt(a));
		
		a = 14;
		System.out.println("a = " + a + " to hex => " + Hex.decode(new byte[] {DataUtil.getByteToInt(a)}));
	}
	
	
	public void dlmspeace() {
//		String str = DlmsPiece.C_TO_S.getValue(16);
//		System.out.println("str16 => " + str + ", length = " + str.length()/2);
//		
//		String str8 = DlmsPiece.C_TO_S.getValue();
//		System.out.println("str => " + str8 + ", length = " + str8.length()/2);
//		
//		String cst = DlmsPiece.CLIENT_SYSTEM_TITLE.getValue();
//		System.out.println("cst => " + cst + ", length = " + cst.length()/2);
//		
//		String cst2 = DlmsPiece.CLIENT_SYSTEM_TITLE.getValue(8);
//		System.out.println("cst2 => " + cst2 + ", length = " + cst2.length()/2);
		
		byte[] aarq = AARQ.CALLING_AUTHENTICATION_VALUE.getValue();
		System.out.println("cab => " + Hex.decode(aarq) + ", length = " + Hex.decode(aarq).length()/2);
		
		byte[] aarq2 = AARQ.CALLING_AUTHENTICATION_VALUE.getValue(DlmsPiece.C_TO_S.getBytes(16));
		System.out.println("cab2 => " + Hex.decode(aarq2) + ", length = " + Hex.decode(aarq2).length()/2);
		
		byte[] sar = AARQ.SENDER_ACSE_REQUIREMENTS.getValue();
		System.out.println("sar => " + Hex.decode(sar) + ", length = " + Hex.decode(sar).length()/2);
		
		byte[] sar2 = AARQ.SENDER_ACSE_REQUIREMENTS.getValue(DlmsPiece.C_TO_S.getBytes(16));
		System.out.println("sar2 => " + Hex.decode(sar2) + ", length = " + Hex.decode(sar2).length()/2);
	}
	
	public void dateTest() {
		logger.debug("=> " + DateTimeUtil.getDateString(1556590355685L));
		System.out.println(DateTimeUtil.getDateString(1556590355685L));
	}

//	public void HDLCDecodeTest() {
//		String str = "7EA87A0302FFF642F2E6E700C401400001040204090C07E2020E03140000008000000600000019060000000011000204090C07E2020E03150000008000000600";
//
//		INestedFrame frame = new NestedHDLCDecoratorForSORIA(new NestedDLMSDecoratorForSORIA(new SORIA_DLMSFrame()));
//		//frame.decode(null, DataUtil.readByteString(str), null);
//		frame.decode(DataUtil.readByteString(str), null, null);
//
//	}

}
