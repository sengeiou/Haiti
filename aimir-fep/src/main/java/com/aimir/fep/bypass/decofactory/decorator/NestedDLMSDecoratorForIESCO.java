/**
 * (@)# NestedDLMSDecoratorForSORIA.java
 *
 * 2016. 4. 15.
 *
 * Copyright (c) 2013 NURITELECOM, Inc.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of 
 * NURITELECOM, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with NURITELECOM, Inc.
 *
 * For more information on this product, please see
 * http://www.nuritelecom.co.kr
 *
 */
package com.aimir.fep.bypass.decofactory.decorator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jsmpp.util.HexUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aimir.fep.bypass.decofactory.consts.DlmsConstantsForIESCO.AARE;
import com.aimir.fep.bypass.decofactory.consts.DlmsConstantsForIESCO.AARQ;
import com.aimir.fep.bypass.decofactory.consts.DlmsConstantsForIESCO.ASSOCIATION_LN;
import com.aimir.fep.bypass.decofactory.consts.DlmsConstantsForIESCO.ActionRequest;
import com.aimir.fep.bypass.decofactory.consts.DlmsConstantsForIESCO.ActionResponse;
import com.aimir.fep.bypass.decofactory.consts.DlmsConstantsForIESCO.ActionResult;
import com.aimir.fep.bypass.decofactory.consts.DlmsConstantsForIESCO.DLMSCommonDataType;
import com.aimir.fep.bypass.decofactory.consts.DlmsConstantsForIESCO.DataAccessResult;
import com.aimir.fep.bypass.decofactory.consts.DlmsConstantsForIESCO.DemandPeriodAttributes;
import com.aimir.fep.bypass.decofactory.consts.DlmsConstantsForIESCO.DlmsPiece;
import com.aimir.fep.bypass.decofactory.consts.DlmsConstantsForIESCO.GetDataResult;
import com.aimir.fep.bypass.decofactory.consts.DlmsConstantsForIESCO.GetRequest;
import com.aimir.fep.bypass.decofactory.consts.DlmsConstantsForIESCO.GetResponse;
import com.aimir.fep.bypass.decofactory.consts.DlmsConstantsForIESCO.ImageTransfer;
import com.aimir.fep.bypass.decofactory.consts.DlmsConstantsForIESCO.ImageTransferAttributes;
import com.aimir.fep.bypass.decofactory.consts.DlmsConstantsForIESCO.ImageTransferMethods;
import com.aimir.fep.bypass.decofactory.consts.DlmsConstantsForIESCO.KaifaCustomRequest;
import com.aimir.fep.bypass.decofactory.consts.DlmsConstantsForIESCO.MBusClientMethods;
import com.aimir.fep.bypass.decofactory.consts.DlmsConstantsForIESCO.MeterBillingCycleAttributes;
import com.aimir.fep.bypass.decofactory.consts.DlmsConstantsForIESCO.MeterFWInfoAttributes;
import com.aimir.fep.bypass.decofactory.consts.DlmsConstantsForIESCO.MeterParamSetMethods;
import com.aimir.fep.bypass.decofactory.consts.DlmsConstantsForIESCO.MeterRelayMethods;
import com.aimir.fep.bypass.decofactory.consts.DlmsConstantsForIESCO.SetRequest;
import com.aimir.fep.bypass.decofactory.consts.DlmsConstantsForIESCO.SetResponse;
import com.aimir.fep.bypass.decofactory.consts.DlmsConstantsForIESCO.TOUAttributes;
import com.aimir.fep.bypass.decofactory.consts.DlmsConstantsForIESCO.TOUInfoBlockType;
import com.aimir.fep.bypass.decofactory.consts.DlmsConstantsForIESCO.XDLMS_APDU_InitiateRequest;
import com.aimir.fep.bypass.decofactory.consts.DlmsConstants;
import com.aimir.fep.bypass.decofactory.consts.DlmsConstants.XDLMS_APDU;
import com.aimir.fep.bypass.decofactory.consts.DlmsConstantsForIESCO;
import com.aimir.fep.bypass.decofactory.consts.HLSAuthForIESCO.HLSSecurityControl;
import com.aimir.fep.bypass.decofactory.consts.HLSAuthForIESCO;
import com.aimir.fep.bypass.decofactory.consts.HdlcConstants.HdlcFrameType;
import com.aimir.fep.bypass.decofactory.decoframe.INestedFrame;
import com.aimir.fep.bypass.decofactory.protocolfactory.BypassFrameFactory.Procedure;
import com.aimir.fep.bypass.dlms.enums.DataType;
import com.aimir.fep.bypass.dlms.enums.ObjectType;
import com.aimir.fep.command.conf.DLMSMeta.CONTROL_STATE;
import com.aimir.fep.protocol.nip.client.multisession.MultiSession;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;
import com.aimir.util.DateTimeUtil;

import net.sf.json.JSONArray;

/**
 * @author simhanger
 *
 */
public class NestedDLMSDecoratorForIESCO extends NestFrameDecorator {
	private static Logger logger = LoggerFactory.getLogger(NestedDLMSDecoratorForIESCO.class);

	private List<HashMap<String, Object>> channelList = new ArrayList<HashMap<String, Object>>();
	//private List<HashMap<String, Object>> dataList = new ArrayList<HashMap<String, Object>>();
	private byte[] lpRawData; // Load Profile 정보를 수집하기위한 Procedure에서 사용.

	private byte[] gdDLMSFrame = null;
	/**
	 * ActionReq 시 필요한 정보.
	 */
	private byte[] aareAuthenticationValue = null; // AARE로 받은 S to C
	private byte[] aareRespondingAPtitle = null; // AARE로 받은 Server System Title. Action Response Validation시 필요.
	private byte[] aarqSendingCtoS = null;  // AARQ로 보낸 CtoS. Action Response Validation시 필요.
	

	/**
	 * 호출할때마다 1씩 증가시킨값
	 * 
	 * @return
	 */
//	private static int hdlcInvoCounter = 0;
//	public static byte[] getInvoCounter() {
//		return DataUtil.get4ByteToInt(++hdlcInvoCounter);
//	}
	private int hdlcInvoCounter = 0;
	public byte[] getInvoCounter() {
		return DataUtil.get4ByteToInt(++hdlcInvoCounter);
	}

	/*
	 * REQUEST_INVOKE_ID_AND_PRIORITY를 호출할때마다 1씩 증가시킨값. 
	 * 동일한 트렌젝션으로 묶일경우는 증가시키지 않는다.
	 */
	private int priorityCounter = 64;

	
	boolean isUseDedicatedKey = false;  // 추후 이 값은 properites로 뺄것.
	
	public byte[] getPriorityByteValue() {
		return getPriorityByteValue(false);
	}

	public byte[] getPriorityByteValue(boolean hasMorTransaction) {
		byte[] result = new byte[1];

		if (hasMorTransaction) {
			result[0] = DataUtil.getByteToInt(priorityCounter);
		} else {
			result[0] = DataUtil.getByteToInt(priorityCounter++);
		}

		return result;
	}

	/**
	 * @param nestedFrame
	 */
	public NestedDLMSDecoratorForIESCO(INestedFrame nestedFrame) {
		super(nestedFrame);
	}

	@SuppressWarnings("unchecked")
	@Override
	//public byte[] encode(HdlcObjectType hdlcType, Procedure procedure, HashMap<String, Object> param, String command) {
	public byte[] encode(HdlcFrameType hdlcType, XDLMS_APDU dlmsApdu, Procedure procedure, HashMap<String, Object> param, String command) {
		logger.debug("## Excute NestedDLMSDecorator Encoding [{}][{}]", hdlcType.name(), dlmsApdu.name());
		gdDLMSFrame = new byte[] {};
		String obisCode = null;
		String[] obisCodeArr = null;
		byte[] obisCodeByte = new byte[] {};
		String classId = null;
		String attributeNo = null;
		String value = null;
		JSONArray jsonArr = null;
		Map<String, Object> map = null;
		try {
			if (hdlcType == HdlcFrameType.SNRM && (dlmsApdu == DlmsConstants.XDLMS_APDU.NULL || dlmsApdu == DlmsConstants.XDLMS_APDU.KAIFA_CUSTOM)) {
				byte[] snrmFrame = null;
				
				/*
				 * For SORIA Kaifa Meter custom
				 */
				if(command.equals("cmdSORIAGetMeterKey")){
					snrmFrame = DataUtil.readByteString("818014050207EE060207EE070400000001080400000001");
				} else if(command.equals("cmdSORIASetMeterSerial")){
						snrmFrame = DataUtil.readByteString("818014050207EE060207EE070400000001080400000001");						
				}else{
					snrmFrame = new byte[] { (byte) 0x81, // Format identifier
							(byte) 0x80, // Group identifier
							(byte) 0x14, // Length 20
							(byte) 0x05, // maximun length - transmit
							(byte) 0x02, // Length 2
							//(byte) 0x04, (byte) 0x00, // 1024 bytes
							(byte) 0x01, (byte) 0x00, // 256
							(byte) 0x06, // maximum length - receive
							(byte) 0x02, // Length 2
							//(byte) 0x04, (byte) 0x00, // 1024 bytes
							(byte) 0x01, (byte) 0x00, // 256
							(byte) 0x07, // window size - transmit
							(byte) 0x04, // Length 4
							(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, // 1
							(byte) 0x08, // window size - receive
							(byte) 0x04, // Length 4
							(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01 // 1
					};
				}
				gdDLMSFrame = DataUtil.append(super.encode(hdlcType, dlmsApdu, null, null, command), snrmFrame);
			} else if (hdlcType == HdlcFrameType.DISC && dlmsApdu == DlmsConstants.XDLMS_APDU.NULL) {
				gdDLMSFrame = super.encode(hdlcType, dlmsApdu, null, null, command);
			} else if (hdlcType == HdlcFrameType.I) {
				switch (dlmsApdu) {
//					/*
//					 *   공통 프로시져 
//					 */
//					case SNRM:
//						byte[] snrmFrame = null;
//						
//						/*
//						 * For SORIA Kaifa Meter custom
//						 */
//						if(command.equals("cmdSORIAGetMeterKey")){
//							snrmFrame = DataUtil.readByteString("818014050207EE060207EE070400000001080400000001");
//						} else if(command.equals("cmdSORIASetMeterSerial")){
//								snrmFrame = DataUtil.readByteString("818014050207EE060207EE070400000001080400000001");						
//						}else{
//							snrmFrame = new byte[] { (byte) 0x81, // Format identifier
//									(byte) 0x80, // Group identifier
//									(byte) 0x14, // Length 20
//									(byte) 0x05, // maximun length - transmit
//									(byte) 0x02, // Length 2
//									(byte) 0x04, (byte) 0x00, // 1024 bytes
//									//(byte) 0x01, (byte) 0x00, // 256
//									(byte) 0x06, // maximum length - receive
//									(byte) 0x02, // Length 2
//									(byte) 0x04, (byte) 0x00, // 1024 bytes
//									//(byte) 0x01, (byte) 0x00, // 256
//									(byte) 0x07, // window size - transmit
//									(byte) 0x04, // Length 4
//									(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, // 1
//									(byte) 0x08, // window size - receive
//									(byte) 0x04, // Length 4
//									(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01 // 1
//							};
//						}
//	
//						gdDLMSFrame = DataUtil.append(super.encode(hdlcType, null, null, command), snrmFrame);
//	
//						break;
					case AARQ:
						gdDLMSFrame = DataUtil.append(super.encode(hdlcType, dlmsApdu, null, null, command), gdDLMSFrame);
						byte[] aarqResult = null;
						
						/*
						 * For SORIA Kaifa Meter custom
						 */
						if(command.equals("cmdSORIAGetMeterKey")){
							aarqResult = DataUtil.readByteString("E6E600601DA109060760857405080101BE10040E01000000065F1F040000181CFFFF");
						}else if(command.equals("cmdSORIASetMeterSerial")){
							aarqResult = DataUtil.readByteString("E6E600601DA109060760857405080101BE10040E01000000065F1F040000181CFFFF");
						}else{
							// AARQ Info
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, AARQ.AARQ_LLC.getValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, AARQ.APPLICATION.getValue());
	
							aarqResult = new byte[] {};
							aarqResult = DataUtil.append(aarqResult, new byte[1]); // application & application length 2바이트 제외
							aarqResult = DataUtil.append(aarqResult, AARQ.APPLICATION_CONTEXT_NAME.getValue());
							aarqResult = DataUtil.append(aarqResult, AARQ.CALLING_AP_TITLE.getValue());							
							aarqResult = DataUtil.append(aarqResult, AARQ.SENDER_ACSE_REQUIREMENTS.getValue());
							aarqResult = DataUtil.append(aarqResult, AARQ.MECHANISM_NAME.getValue());
							
							aarqSendingCtoS = DlmsPiece.C_TO_S.getBytes(16);   // IESCO는 16자리사용. 
							aarqResult = DataUtil.append(aarqResult, AARQ.CALLING_AUTHENTICATION_VALUE.getValue(aarqSendingCtoS));
							
							// make user-information
							byte[] userInformation = new byte[] {};
							userInformation = DataUtil.append(userInformation, AARQ.USER_INFORMATION_TAG.getValue());
							userInformation = DataUtil.append(userInformation, new byte[1]); // encoding of the length of the tagged component’s value field
							userInformation = DataUtil.append(userInformation, new byte[] { 0x04 }); // encoding of the choice for user-information
							userInformation = DataUtil.append(userInformation, new byte[1]); // encoding of the length of the OCTET STRING’s value field
	
							byte[] initiateRequest = new byte[] {};
							initiateRequest = DataUtil.append(initiateRequest, XDLMS_APDU.GLO_INITIATEREQUEST.getValue()); // with global ciphering
							initiateRequest = DataUtil.append(initiateRequest, new byte[1]); // length
							initiateRequest = DataUtil.append(initiateRequest, HLSSecurityControl.AUTHENTICATION_ENCRYPTION.getValue()); // SC : Security Control
							byte[] aarqIC = getInvoCounter();  // IC : Invocation Count
							initiateRequest = DataUtil.append(initiateRequest, aarqIC);
							
							logger.info("[User Information] XDLMS_APDU Type    = [{}][{}]", XDLMS_APDU.GLO_INITIATEREQUEST,  XDLMS_APDU.GLO_INITIATEREQUEST.getValue());							
							logger.info("[User Information] Security Control   = [{}][{}]", HLSSecurityControl.AUTHENTICATION_ENCRYPTION, HLSSecurityControl.AUTHENTICATION_ENCRYPTION.getValue());							
							logger.info("[User Information] Invocation Counter = [{}]", Hex.decode(aarqIC));
							
							byte[] plainText = new byte[] {};
							plainText = DataUtil.append(plainText, Hex.encode("01"));  // xDLMS APDU CHOICE (InitiateRequest)
							
							logger.debug("### Is use dedicated-key ? = {}", isUseDedicatedKey);
							
							// Dedciated-key 사용여부
							if(isUseDedicatedKey) {
								byte[] dedicatedKey = XDLMS_APDU_InitiateRequest.DEDICATED_KEY.getByteValue();
								logger.debug("### DEDICATED_KEY = {}", Hex.decode(dedicatedKey));
								
								plainText = DataUtil.append(plainText, dedicatedKey);	
							}else {
								plainText = DataUtil.append(plainText, Hex.encode("00")); //dedciated-key 사용 않함.
							}
							
							plainText = DataUtil.append(plainText, XDLMS_APDU_InitiateRequest.RESPONE_ALLOWED.getByteValue());
							plainText = DataUtil.append(plainText, XDLMS_APDU_InitiateRequest.PROPOSED_QUALITY_OF_SERVICE.getByteValue());
							plainText = DataUtil.append(plainText, XDLMS_APDU_InitiateRequest.PROPOSED_DLMS_VERSION_NUMBER.getByteValue());
							plainText = DataUtil.append(plainText, XDLMS_APDU_InitiateRequest.PROPOSED_CONFORMANCE.getByteValue());
							plainText = DataUtil.append(plainText, XDLMS_APDU_InitiateRequest.CLIENT_MAX_RECEIVE_PDU_SIZE.getByteValue());
	
							HLSAuthForIESCO aarqAuth = new HLSAuthForIESCO(HLSSecurityControl.AUTHENTICATION_ENCRYPTION, getMeterId());
							byte[] cipherText = aarqAuth.doEncryption(aarqIC, DlmsPiece.CLIENT_SYSTEM_TITLE.getBytes(), plainText);
	
							initiateRequest = DataUtil.append(initiateRequest, cipherText);
							initiateRequest[1] = DataUtil.getByteToInt(initiateRequest.length - 2); // initiateRequest & initiateRequest length 2바이트제외
	
							userInformation = DataUtil.append(userInformation, initiateRequest);
							userInformation[1] = DataUtil.getByteToInt(userInformation.length - 2); // userInformation & userInformation length 2바이트제외
							userInformation[3] = DataUtil.getByteToInt(userInformation.length - 4); // userInformation & userInformation length 2바이트제외
							
							//aarqResult = DataUtil.append(aarqResult, AARQ.USER_INFORMATION.getValue());
							aarqResult = DataUtil.append(aarqResult, userInformation);
							aarqResult[0] = DataUtil.getByteToInt(aarqResult.length - 1); // application length : -1은 length
						}
						
						gdDLMSFrame = DataUtil.append(gdDLMSFrame, aarqResult);
						break;
					case GLO_ACTION_REQUEST:
						switch(procedure){
						case HDLC_ASSOCIATION_LN:
							/*
							 *  Make Plain Text for glo-action-request
							 */
							byte[] plainText = new byte[] {};
							plainText = DataUtil.append(plainText, ActionRequest.ACTION_REQUEST.getByteValue()); // Action Request = C3
							plainText = DataUtil.append(plainText, ActionRequest.NORMAL.getByteValue());
							plainText = DataUtil.append(plainText, ActionRequest.ACTION_REQUEST_INVOKE_ID_AND_PRIORITY.getByteValue()); // 이거 안되면 C1으로 고정해서 사용해볼것
							
							plainText = DataUtil.append(plainText, ASSOCIATION_LN.CLASS_ASSOCIATION_LN.getByteValue());
							plainText = DataUtil.append(plainText, ASSOCIATION_LN.CURRENT_ASSOCIATION_LN.getByteValue());
							plainText = DataUtil.append(plainText, ASSOCIATION_LN.REPLY_TO_HLS_AUTHENTICATION.getByteValue());
							plainText = DataUtil.append(plainText, new byte[] { 0x01, 0x09, 0x11 }); // param, octet-string, length 17
							plainText = DataUtil.append(plainText, HLSSecurityControl.AUTHENTICATION_ONLY.getValue());
							byte[] plainIC = getInvoCounter();
							plainText = DataUtil.append(plainText, plainIC);
	
							HLSAuthForIESCO auth = new HLSAuthForIESCO(HLSSecurityControl.AUTHENTICATION_ONLY, getMeterId());
							byte[] tagValue = auth.getTagValue(plainIC, DlmsPiece.CLIENT_SYSTEM_TITLE.getBytes(), aareAuthenticationValue);
							plainText = DataUtil.append(plainText, tagValue);
							
							logger.info("[GLO_ACTION_REQUEST][PlainText] XDLMS_APDU Type    = [{}][{}]", XDLMS_APDU.ACTION_REQUEST,  XDLMS_APDU.ACTION_REQUEST.getCode());	
							logger.info("[GLO_ACTION_REQUEST][PlainText] Security Control   = [{}][{}]", HLSSecurityControl.AUTHENTICATION_ONLY, Hex.decode(HLSSecurityControl.AUTHENTICATION_ONLY.getValue()));							
							logger.info("[GLO_ACTION_REQUEST][PlainText] Invocation Counter = [{}]", Hex.decode(plainIC));
							logger.info("[GLO_ACTION_REQUEST][PlainText] Client SystemTitle = [{}]", Hex.decode(DlmsPiece.CLIENT_SYSTEM_TITLE.getBytes()));
							logger.info("[GLO_ACTION_REQUEST][PlainText] Information(S to C)= [{}]", Hex.decode(aareAuthenticationValue));
							logger.info("[GLO_ACTION_REQUEST][PlainText] Tag                = [{}]", Hex.decode(tagValue));
							logger.info("[GLO_ACTION_REQUEST][PlainText] Frame Data         = [{}]", Hex.decode(plainText));
							/*
							 * Make glo-action-request Frame
							 */
							byte[] gloActionReqFrame = new byte[] {};
							gloActionReqFrame = DataUtil.append(gloActionReqFrame, XDLMS_APDU.GLO_ACTION_REQUEST.getValue()); // with global ciphering
							gloActionReqFrame = DataUtil.append(gloActionReqFrame, new byte[1]); // length
							gloActionReqFrame = DataUtil.append(gloActionReqFrame, HLSSecurityControl.AUTHENTICATION_ENCRYPTION.getValue()); // SC : Security Control
							byte[] gloIC = getInvoCounter();  // IC : Invocation Count
							gloActionReqFrame = DataUtil.append(gloActionReqFrame, gloIC);
							
							auth = new HLSAuthForIESCO(HLSSecurityControl.AUTHENTICATION_ENCRYPTION, getMeterId());
							byte[] cipherText = auth.doEncryption(gloIC, DlmsPiece.CLIENT_SYSTEM_TITLE.getBytes(), plainText);
							gloActionReqFrame = DataUtil.append(gloActionReqFrame, cipherText);
							
							int gloActionRequestFrameLength = gloActionReqFrame.length - 2; // -2 는 CB + Length 바이트
							gloActionReqFrame[1] = DataUtil.getByteToInt(gloActionRequestFrameLength); // length
							
							logger.info("[GLO_ACTION_REQUEST] XDLMS_APDU Type    = [{}][{}]", XDLMS_APDU.GLO_ACTION_REQUEST,  XDLMS_APDU.GLO_ACTION_REQUEST.getCode());							
							logger.info("[GLO_ACTION_REQUEST] Security Control   = [{}][{}]", HLSSecurityControl.AUTHENTICATION_ENCRYPTION, Hex.decode(HLSSecurityControl.AUTHENTICATION_ENCRYPTION.getValue()));							
							logger.info("[GLO_ACTION_REQUEST] Invocation Counter = [{}]", Hex.decode(gloIC));
							logger.info("[GLO_ACTION_REQUEST] Client SystemTitle = [{}]", Hex.decode(DlmsPiece.CLIENT_SYSTEM_TITLE.getBytes()));
							logger.info("[GLO_ACTION_REQUEST] Information(PlainText) = [{}]", Hex.decode(plainText));
							logger.info("[GLO_ACTION_REQUEST] Cipher Text        = [{}]", Hex.decode(cipherText));
							logger.info("[GLO_ACTION_REQUEST] Frame Data         = [{}]", Hex.decode(gloActionReqFrame));

							
							/*
							 * 
							 */
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ActionRequest.ACTION_REQUEST_LLC.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, gloActionReqFrame);
							break;
						default:
							break;
						}
						break;
					case ACTION_REQUEST:
						gdDLMSFrame = DataUtil.append(super.encode(hdlcType, dlmsApdu, null, null, command), gdDLMSFrame);
	
						switch (procedure) {
						/*
						 *  공통 프로시저
						 */
						case HDLC_ASSOCIATION_LN:
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ActionRequest.ACTION_REQUEST_LLC.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ActionRequest.ACTION_REQUEST.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ActionRequest.NORMAL.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ActionRequest.ACTION_REQUEST_INVOKE_ID_AND_PRIORITY.getByteValue());
	
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ASSOCIATION_LN.CLASS_ASSOCIATION_LN.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ASSOCIATION_LN.CURRENT_ASSOCIATION_LN.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ASSOCIATION_LN.REPLY_TO_HLS_AUTHENTICATION.getByteValue());
	
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x01, 0x09, 0x11 }); // param, octet-string, length 17
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, HLSSecurityControl.AUTHENTICATION_ONLY.getValue());
							byte[] aReqIC = getInvoCounter();
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, aReqIC);
	
							HLSAuthForIESCO auth = new HLSAuthForIESCO(HLSSecurityControl.AUTHENTICATION_ONLY, getMeterId());
							byte[] tagValue = auth.getTagValue(aReqIC, DlmsPiece.CLIENT_SYSTEM_TITLE.getBytes(), aareAuthenticationValue);
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, tagValue);
							break;
						/*
						 *  Image Transfer 관련 프로시져 
						 */
						case ACTION_IMAGE_TRANSFER_INIT:
							/*
							 *  data ::= structure
								{
									image_identifier: octet-string,
									image_size: double-long-unsigned
								}
							 */
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ActionRequest.ACTION_REQUEST_LLC.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ActionRequest.ACTION_REQUEST.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ActionRequest.NORMAL.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ActionRequest.ACTION_REQUEST_INVOKE_ID_AND_PRIORITY.getByteValue());
	
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ImageTransfer.CLASS_ID.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ImageTransfer.OBIS_CODE.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ImageTransferMethods.IMAGE_TRANSFER_INITIATE.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ImageTransfer.OPTION_USE.getByteValue());
	
							byte[] image_identifier = ((String) param.get("image_identifier")).getBytes(); // F/W파일명
	
							byte[] structureA = new byte[4];
							structureA[0] = 0x02;
							structureA[1] = 0x02;
							structureA[2] = 0x09;
							structureA[3] = DataUtil.getByteToInt(image_identifier.length);
	
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, structureA);
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, image_identifier);
	
							byte[] structureB = new byte[] { 0x06 };
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, structureB);
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DataUtil.get4ByteToInt(Integer.parseInt((String) param.get("image_size"))));
							break;
						case ACTION_IMAGE_BLOCK_TRANSFER:
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ActionRequest.ACTION_REQUEST_LLC.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ActionRequest.ACTION_REQUEST.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ActionRequest.NORMAL.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ActionRequest.ACTION_REQUEST_INVOKE_ID_AND_PRIORITY.getByteValue());
	
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ImageTransfer.CLASS_ID.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ImageTransfer.OBIS_CODE.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ImageTransferMethods.IMAGE_BLOCK_TRANSFER.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ImageTransfer.OPTION_USE.getByteValue());
	
							byte[] imagePartA = new byte[3];
							imagePartA[0] = 0x02;
							imagePartA[1] = 0x02;
							imagePartA[2] = 0x06;
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, imagePartA);
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DataUtil.get4ByteToInt(Integer.parseInt(String.valueOf(param.get("image_block_number")))));
	
							byte[] imageBlock = (byte[]) param.get("image_block_value");
	
							/*
							 * Block Length 구하기
							 */
							byte[] imagePartB = null;
							if (128 <= imageBlock.length) {
								imagePartB = new byte[3];
								imagePartB[0] = 0x09;
								imagePartB[1] = (byte) 0x81;
								imagePartB[2] = DataUtil.getByteToInt(imageBlock.length);
							} else {
								imagePartB = new byte[2];
								imagePartB[0] = 0x09;
								imagePartB[1] = DataUtil.getByteToInt(imageBlock.length);
							}
	
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, imagePartB);
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, imageBlock);
							break;
						case ACTION_IMAGE_VERIFY:
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ActionRequest.ACTION_REQUEST_LLC.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ActionRequest.ACTION_REQUEST.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ActionRequest.NORMAL.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ActionRequest.ACTION_REQUEST_INVOKE_ID_AND_PRIORITY.getByteValue());
	
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ImageTransfer.CLASS_ID.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ImageTransfer.OBIS_CODE.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ImageTransferMethods.IMAGE_VERIFY.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ImageTransfer.OPTION_USE.getByteValue());
	
							byte[] verifyPartA = new byte[2];
							verifyPartA[0] = 0x0F;
							verifyPartA[1] = Byte.valueOf(String.valueOf(param.get("image_verify_data")));
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, verifyPartA);
							break;
						case ACTION_IMAGE_ACTIVATE:
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ActionRequest.ACTION_REQUEST_LLC.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ActionRequest.ACTION_REQUEST.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ActionRequest.NORMAL.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ActionRequest.ACTION_REQUEST_INVOKE_ID_AND_PRIORITY.getByteValue());
	
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ImageTransfer.CLASS_ID.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ImageTransfer.OBIS_CODE.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ImageTransferMethods.IMAGE_ACTIVATE.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ImageTransfer.OPTION_USE.getByteValue());
	
							byte[] activatePartA = new byte[2];
							activatePartA[0] = 0x0F;
							activatePartA[1] = Byte.valueOf(String.valueOf(param.get("image_activate_data")));
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, activatePartA);
							break;
						/*
						 *  Meter Alarm Reset용 프로시져 
						 */
						case ACTION_METER_ALARM_RESET:
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ActionRequest.ACTION_REQUEST_LLC.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ActionRequest.ACTION_REQUEST.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ActionRequest.NORMAL.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ActionRequest.ACTION_REQUEST_INVOKE_ID_AND_PRIORITY.getByteValue());
	
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, MeterParamSetMethods.CLASS_ID.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, MeterParamSetMethods.OBIS_CODE.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, MeterParamSetMethods.METER_ALARM_RESET.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, MeterParamSetMethods.OPTION_USE.getByteValue());
	
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x12 }); // UINT16(0x12, 2), long-unsigned 2byte
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, MeterParamSetMethods.RESET_ALARM.getByteValue());
							break;
							
						case ACTION_DISCONNECT_CONTROL:
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ActionRequest.ACTION_REQUEST_LLC.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ActionRequest.ACTION_REQUEST.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ActionRequest.NORMAL.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ActionRequest.ACTION_REQUEST_INVOKE_ID_AND_PRIORITY.getByteValue());
	
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, MeterRelayMethods.CLASS_ID.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, MeterRelayMethods.OBIS_CODE.getByteValue());
							String boolValue = String.valueOf(param.get("value"));
							if ( "true".equals(boolValue)){
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, MeterRelayMethods.REMOTE_RECONNECT.getByteValue());
							}
							else if ("false".equals(boolValue)){
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, MeterRelayMethods.REMOTE_DISCONNECT.getByteValue());
							}
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, MeterRelayMethods.OPTION_USE.getByteValue());					
							byte[] disconnectCtrl = new byte[2];
							disconnectCtrl[0] = (byte)0x0F;
							disconnectCtrl[1] = (byte)0x00;
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, disconnectCtrl);
							break;
							
						case ACTION_SLAVE_INSTALL:
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ActionRequest.ACTION_REQUEST_LLC.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ActionRequest.ACTION_REQUEST.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ActionRequest.NORMAL.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ActionRequest.ACTION_REQUEST_INVOKE_ID_AND_PRIORITY.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, MBusClientMethods.CLASS_ID.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, MBusClientMethods.OBIS_CODE.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, MBusClientMethods.SLAVE_INSTALL.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, MBusClientMethods.OPTION_USE.getByteValue());
							
	//						gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x03 });
	
							value = ((String) param.get("value")).replaceAll("\\\\\"", "\"");
							logger.debug("original=" + (String) param.get("value") + "replaced=" + value);
							String dataType = (String) param.get("dataType");
							jsonArr = null;
							if (value == null || value.isEmpty()) {
								jsonArr = new JSONArray();
							} else {
								jsonArr = JSONArray.fromObject(value);
							}
	
							map = (Map<String, Object>) jsonArr.toArray()[0];					
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) DataType.UINT8.getValue() });
							byte slaveInstall[] =   new byte[] { DataUtil.getByteToInt(Integer.parseInt(String.valueOf(map.get("value"))))};
							logger.debug("unsigned:" + Hex.decode(slaveInstall));
							gdDLMSFrame = DataUtil.append(gdDLMSFrame,slaveInstall );		
							
							break;
						case ACTION_SLAVE_DEINSTALL:
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ActionRequest.ACTION_REQUEST_LLC.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ActionRequest.ACTION_REQUEST.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ActionRequest.NORMAL.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ActionRequest.ACTION_REQUEST_INVOKE_ID_AND_PRIORITY.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, MBusClientMethods.CLASS_ID.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, MBusClientMethods.OBIS_CODE.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, MBusClientMethods.SLAVE_DEINSTALL.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, MBusClientMethods.OPTION_USE.getByteValue());					
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) DataType.INT8.getValue() });
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x00 });		
							
							break;					
						case ACTION_SET_ENCRYPTION_KEY:
						case ACTION_TRANSFER_KEY:
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ActionRequest.ACTION_REQUEST_LLC.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ActionRequest.ACTION_REQUEST.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ActionRequest.NORMAL.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ActionRequest.ACTION_REQUEST_INVOKE_ID_AND_PRIORITY.getByteValue());
	
							obisCode = (String) param.get("obisCode");
							obisCodeArr = obisCode.split("[.]");
							obisCodeByte = new byte[6];
							for (int i = 0; i < obisCodeArr.length; i++) {
								obisCodeByte[i] = (byte) Integer.parseInt(obisCodeArr[i]);
							}
	
							classId = (String) param.get("classId");
							attributeNo = (String) param.get("attributeNo");
	
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DataUtil.get2ByteToInt(String.valueOf(classId)));
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, obisCodeByte);
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) Integer.parseInt(attributeNo) });
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, Hex.encode("01"));
							value = ((String) param.get("value")).replaceAll("\\\\\"", "\"");
							logger.debug("original=" + (String) param.get("value") + "replaced=" + value);
							jsonArr = null;
							if (value == null || value.isEmpty()) {
								jsonArr = new JSONArray();
							} else {
								jsonArr = JSONArray.fromObject(value);
							}
	
							map = (Map<String, Object>) jsonArr.toArray()[0];
	//						String strVal = String.valueOf(map.get("value"));
	//						byte[] val = strVal.getBytes();
	//						logger.debug("KEY_VALUE=" +strVal );
							String strHexVal = String.valueOf(map.get("value"));
							byte[] val = HexUtil.convertHexStringToBytes(strHexVal);
	
							logger.debug("KEY_HEX=" + HexUtil.conventBytesToHexString(val));
							byte[] data = new byte[2];
	
							data[0] = DLMSCommonDataType.OctetString.getValue();
							data[1] = DataUtil.getByteToInt(val.length);
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, data);
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, val);
	
							break;
							
						default:
							break;
						}
	
						break;
					case GET_REQUEST:
						gdDLMSFrame = DataUtil.append(super.encode(hdlcType, dlmsApdu, null, null, command), gdDLMSFrame);
	
						switch (procedure) {
						/*
						 *   Image Transfer 관련 프로시져 
						 */
						case GET_IMAGE_TRANSFER_ENABLE:
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST_LLC.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.NORMAL.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST_INVOKE_ID_AND_PRIORITY.getByteValue());
	
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ImageTransfer.CLASS_ID.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ImageTransfer.OBIS_CODE.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ImageTransferAttributes.IMAGE_TRANSFER_ENABLED.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ImageTransfer.OPTION_NOT_USE.getByteValue());
							break;
						case GET_IMAGE_BLOCK_SIZE:
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST_LLC.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.NORMAL.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST_INVOKE_ID_AND_PRIORITY.getByteValue());
	
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ImageTransfer.CLASS_ID.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ImageTransfer.OBIS_CODE.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ImageTransferAttributes.IMAGE_BLOCK_SIZE.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ImageTransfer.OPTION_NOT_USE.getByteValue());
							break;
						case GET_IMAGE_TRANSFER_STATUS:
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST_LLC.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.NORMAL.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST_INVOKE_ID_AND_PRIORITY.getByteValue());
	
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ImageTransfer.CLASS_ID.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ImageTransfer.OBIS_CODE.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ImageTransferAttributes.IMAGE_TRANSFER_STATUS.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ImageTransfer.OPTION_NOT_USE.getByteValue());
							break;
						case GET_IMAGE_FIRST_NOT_TRANSFERRED_BLOCK_NUMBER:
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST_LLC.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.NORMAL.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST_INVOKE_ID_AND_PRIORITY.getByteValue());
	
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ImageTransfer.CLASS_ID.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ImageTransfer.OBIS_CODE.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ImageTransferAttributes.IMAGE_FIRST_NOT_TRANSFERRED_BLOCK_NUMBER.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ImageTransfer.OPTION_NOT_USE.getByteValue());
							break;
						case GET_IMAGE_TO_ACTIVATE_INFO:
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST_LLC.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.NORMAL.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST_INVOKE_ID_AND_PRIORITY.getByteValue());
	
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ImageTransfer.CLASS_ID.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ImageTransfer.OBIS_CODE.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ImageTransferAttributes.IMAGE_TO_ACTIVATE_INFO.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ImageTransfer.OPTION_NOT_USE.getByteValue());
							break;
	
						/*
						 *   Meter F/W 버전 확인용 프로시져 
						 */
						case GET_FIRMWARE_VERSION:
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST_LLC.getByteValue());
							byte[] getFirmwareVersionPlainText = new byte[] {};
							getFirmwareVersionPlainText = DataUtil.append(getFirmwareVersionPlainText, GetRequest.GET_REQUEST.getByteValue());
							getFirmwareVersionPlainText = DataUtil.append(getFirmwareVersionPlainText, GetRequest.NORMAL.getByteValue());
							getFirmwareVersionPlainText = DataUtil.append(getFirmwareVersionPlainText, GetRequest.GET_REQUEST_INVOKE_ID_AND_PRIORITY.getByteValue());
							getFirmwareVersionPlainText = DataUtil.append(getFirmwareVersionPlainText, MeterFWInfoAttributes.CLASS_ID.getByteValue());
							getFirmwareVersionPlainText = DataUtil.append(getFirmwareVersionPlainText, MeterFWInfoAttributes.OBIS_CODE.getByteValue());
							getFirmwareVersionPlainText = DataUtil.append(getFirmwareVersionPlainText, MeterFWInfoAttributes.FW_VERSION.getByteValue());
							getFirmwareVersionPlainText = DataUtil.append(getFirmwareVersionPlainText, MeterFWInfoAttributes.OPTION_NOT_USE.getByteValue());
							
							HLSAuthForIESCO getReqAuth = new HLSAuthForIESCO(HLSSecurityControl.AUTHENTICATION_ENCRYPTION, getMeterId());
							byte[] getFirmwareVersionReqValue = getReqAuth.getReqEncriptionGlobalCiphering(getInvoCounter(), getFirmwareVersionPlainText);
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, getFirmwareVersionReqValue);
							logger.debug("[GLOBAL_GET_REQUEST][GET_FIRMWARE_VERSION] Frame = {}", Hex.decode(gdDLMSFrame));
							break;
	
						/*
						 *   Meter BillingCycle information 확인 프로시져 
						 */
						case GET_BILLING_CYCLE:
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST_LLC.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.NORMAL.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST_INVOKE_ID_AND_PRIORITY.getByteValue());
	
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, MeterBillingCycleAttributes.CLASS_ID.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, MeterBillingCycleAttributes.OBIS_CODE.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, MeterBillingCycleAttributes.BILLING_CYCLE_EXECUTION_TIME.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, MeterBillingCycleAttributes.OPTION_NOT_USE.getByteValue());
							break;
	
						case GET_METER_TIME:
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST_LLC.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.NORMAL.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST_INVOKE_ID_AND_PRIORITY.getByteValue());
	
							obisCode = (String) param.get("obisCode");
							obisCodeArr = obisCode.split("[.]");
							obisCodeByte = new byte[6];
							for (int i = 0; i < obisCodeArr.length; i++) {
								obisCodeByte[i] = (byte) Integer.parseInt(obisCodeArr[i]);
							}
	
							classId = (String) param.get("classId");
							attributeNo = (String) param.get("attributeNo");
	
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DataUtil.get2ByteToInt(String.valueOf(classId)));
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, obisCodeByte);
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) Integer.parseInt(attributeNo) });
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, Hex.encode("00"));
							break;
	
						case GET_REGISTER_VALUE:
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST_LLC.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.NORMAL.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST_INVOKE_ID_AND_PRIORITY.getByteValue());
	
							obisCode = (String) param.get("obisCode");
							obisCodeArr = obisCode.split("[.]");
							obisCodeByte = new byte[6];
							for (int i = 0; i < obisCodeArr.length; i++) {
								obisCodeByte[i] = (byte) Integer.parseInt(obisCodeArr[i]);
							}
	
							classId = (String) param.get("classId");
							attributeNo = (String) param.get("attributeNo");
	
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DataUtil.get2ByteToInt(String.valueOf(classId)));
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, obisCodeByte);
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) Integer.parseInt(attributeNo) });
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, Hex.encode("00"));
							break;
	
						case GET_REGISTER_UNIT:
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST_LLC.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.NORMAL.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST_INVOKE_ID_AND_PRIORITY.getByteValue());
	
							obisCode = (String) param.get("obisCode");
							obisCodeArr = obisCode.split("[.]");
							obisCodeByte = new byte[6];
							for (int i = 0; i < obisCodeArr.length; i++) {
								obisCodeByte[i] = (byte) Integer.parseInt(obisCodeArr[i]);
							}
	
							classId = (String) param.get("classId");
							attributeNo = (String) param.get("attributeNo");
	
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DataUtil.get2ByteToInt(String.valueOf(classId)));
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, obisCodeByte);
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) Integer.parseInt(attributeNo) });
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, Hex.encode("00"));
							break;
	
						case GET_PROFILE_OBJECT:
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST_LLC.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.NORMAL.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST_INVOKE_ID_AND_PRIORITY.getByteValue());
	
							obisCode = (String) param.get("obisCode");
							obisCodeArr = obisCode.split("[.]");
							obisCodeByte = new byte[6];
							for (int i = 0; i < obisCodeArr.length; i++) {
								obisCodeByte[i] = (byte) Integer.parseInt(obisCodeArr[i]);
							}
	
							classId = (String) param.get("classId");
							attributeNo = "3";
	
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DataUtil.get2ByteToInt(String.valueOf(classId)));
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, obisCodeByte);
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) Integer.parseInt(attributeNo) });
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, Hex.encode("00"));
							break;
	
						case GET_PROFILE_BUFFER:
							if (param.get("isBlock") == null || ((Boolean) param.get("isBlock")) == false) {
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST_LLC.getByteValue());
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST.getByteValue());
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.NORMAL.getByteValue());
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST_INVOKE_ID_AND_PRIORITY.getByteValue());
	
								obisCode = (String) param.get("obisCode");
								obisCodeArr = obisCode.split("[.]");
								obisCodeByte = new byte[6];
								for (int i = 0; i < obisCodeArr.length; i++) {
									obisCodeByte[i] = (byte) Integer.parseInt(obisCodeArr[i]);
								}
	
								classId = (String) param.get("classId");
								attributeNo = (String) param.get("attributeNo");
	
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, DataUtil.get2ByteToInt(String.valueOf(classId)));
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, obisCodeByte);
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) Integer.parseInt(attributeNo) });
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, Hex.encode("01"));
	
								//value = (String) param.get("value");
								value = ((String) param.get("value")).replaceAll("\\\\\"", "\"");
								logger.debug("original=" + (String) param.get("value") + "replaced=" + value);
								jsonArr = null;
								if (value == null || value.isEmpty()) {
									jsonArr = new JSONArray();
								} else {
									jsonArr = JSONArray.fromObject(value);
								}
	
								map = (Map<String, Object>) jsonArr.toArray()[0];
								int option = Integer.parseInt(String.valueOf(map.get("option")));
	
								String clockObis = String.valueOf(map.get("clockObis"));
								String[] clockObisArr = clockObis.split("[.]");
								byte[] clockObisByte = new byte[6];
								for (int i = 0; i < clockObisArr.length; i++) {
									clockObisByte[i] = (byte) Integer.parseInt(clockObisArr[i]);
								}
	
								//현재 01만 개발(range_descriptor)
								if (option == 1) { //range_descriptor
									gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) option }); //range_descriptor
	
									gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) DataType.STRUCTURE.getValue() });
									gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) 4 });
									gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) DataType.STRUCTURE.getValue() });
									gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) 4 });
									gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) DataType.UINT16.getValue() });
									gdDLMSFrame = DataUtil.append(gdDLMSFrame, DataUtil.get2ByteToInt(String.valueOf(ObjectType.CLOCK.getValue())));
									gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) DataType.OCTET_STRING.getValue() });
									gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) 6 });
									gdDLMSFrame = DataUtil.append(gdDLMSFrame, clockObisByte);
									gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) DataType.INT8.getValue() });
									gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) 2 });
									gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) DataType.UINT16.getValue() });
									gdDLMSFrame = DataUtil.append(gdDLMSFrame, DataUtil.get2ByteToInt(0));
	
									//from
									//노르웨이
									gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) DataType.OCTET_STRING.getValue() });
									gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) 12 });
									//이라크
									//								gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[]{(byte)DataType.DATETIME.getValue()});
									gdDLMSFrame = DataUtil.append(gdDLMSFrame, DataUtil.get2ByteToInt(Integer.parseInt(String.valueOf(map.get("fYear")))));
									gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) Integer.parseInt(String.valueOf(map.get("fMonth"))) });
									gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) Integer.parseInt(String.valueOf(map.get("fDayOfMonth"))) });
									gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) Integer.parseInt(String.valueOf(map.get("fDayOfWeek"))) });
									gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) Integer.parseInt(String.valueOf(map.get("fHh"))) });
									gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) Integer.parseInt(String.valueOf(map.get("fMm"))) });
									gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) Integer.parseInt(String.valueOf(map.get("fSs"))) });
									gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) 0xff });
									gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) 0x80 });
									gdDLMSFrame = DataUtil.append(gdDLMSFrame, DataUtil.get2ByteToInt(0));
	
									//to
									gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) DataType.OCTET_STRING.getValue() });
									gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) 12 });
									//								gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[]{(byte)DataType.DATETIME.getValue()});
									gdDLMSFrame = DataUtil.append(gdDLMSFrame, DataUtil.get2ByteToInt(Integer.parseInt(String.valueOf(map.get("tYear")))));
									gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) Integer.parseInt(String.valueOf(map.get("tMonth"))) });
									gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) Integer.parseInt(String.valueOf(map.get("tDayOfMonth"))) });
									gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) Integer.parseInt(String.valueOf(map.get("tDayOfWeek"))) });
									gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) Integer.parseInt(String.valueOf(map.get("tHh"))) });
									gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) Integer.parseInt(String.valueOf(map.get("tMm"))) });
									gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) Integer.parseInt(String.valueOf(map.get("tSs"))) });
									gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) 0xff });
									gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) 0x80 });
									gdDLMSFrame = DataUtil.append(gdDLMSFrame, DataUtil.get2ByteToInt(0));
	
									gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) DataType.ARRAY.getValue() });
									gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) 0 });
								}
							} else {
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST_LLC.getByteValue());
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST.getByteValue());
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.NEXT.getByteValue());
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST_INVOKE_ID_AND_PRIORITY.getByteValue());
	
								int blockNumber = (int) param.get("blockNumber");
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, DataUtil.get4ByteToInt(blockNumber));
							}
							break;
						case GET_PROFILE_PERIOD:
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST_LLC.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.NORMAL.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST_INVOKE_ID_AND_PRIORITY.getByteValue());
	
							obisCode = (String) param.get("obisCode");
							obisCodeArr = obisCode.split("[.]");
							obisCodeByte = new byte[6];
							for (int i = 0; i < obisCodeArr.length; i++) {
								obisCodeByte[i] = (byte) Integer.parseInt(obisCodeArr[i]);
							}
	
							classId = (String) param.get("classId");
							attributeNo = (String) param.get("attributeNo");
	
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DataUtil.get2ByteToInt(String.valueOf(classId)));
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, obisCodeByte);
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) Integer.parseInt(attributeNo) });
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, Hex.encode("00"));
							break;
						case GET_THRESHOLD_NORMAL:
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST_LLC.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.NORMAL.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST_INVOKE_ID_AND_PRIORITY.getByteValue());
	
							obisCode = (String) param.get("obisCode");
							obisCodeArr = obisCode.split("[.]");
							obisCodeByte = new byte[6];
							for (int i = 0; i < obisCodeArr.length; i++) {
								obisCodeByte[i] = (byte) Integer.parseInt(obisCodeArr[i]);
							}
	
							classId = (String) param.get("classId");
							attributeNo = (String) param.get("attributeNo");
	
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DataUtil.get2ByteToInt(String.valueOf(classId)));
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, obisCodeByte);
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) Integer.parseInt(attributeNo) });
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, Hex.encode("00"));
							break;
						case GET_MINOVER_THRESHOLD_DURATION:
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST_LLC.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.NORMAL.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST_INVOKE_ID_AND_PRIORITY.getByteValue());
	
							obisCode = (String) param.get("obisCode");
							obisCodeArr = obisCode.split("[.]");
							obisCodeByte = new byte[6];
							for (int i = 0; i < obisCodeArr.length; i++) {
								obisCodeByte[i] = (byte) Integer.parseInt(obisCodeArr[i]);
							}
	
							classId = (String) param.get("classId");
							attributeNo = (String) param.get("attributeNo");
	
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DataUtil.get2ByteToInt(String.valueOf(classId)));
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, obisCodeByte);
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) Integer.parseInt(attributeNo) });
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, Hex.encode("00"));
							break;
						case GET_DISCONNECT_CONTROL:
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST_LLC.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.NORMAL.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST_INVOKE_ID_AND_PRIORITY.getByteValue());
	
							obisCode = (String) param.get("obisCode");
							obisCodeArr = obisCode.split("[.]");
							obisCodeByte = new byte[6];
							for (int i = 0; i < obisCodeArr.length; i++) {
								obisCodeByte[i] = (byte) Integer.parseInt(obisCodeArr[i]);
							}
	
							classId = (String) param.get("classId");
							attributeNo = (String) param.get("attributeNo");
	
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DataUtil.get2ByteToInt(String.valueOf(classId)));
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, obisCodeByte);
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) Integer.parseInt(attributeNo) });
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, Hex.encode("00"));
							break;
						case GET_VALUE:
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST_LLC.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.NORMAL.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, GetRequest.GET_REQUEST_INVOKE_ID_AND_PRIORITY.getByteValue());
	
							obisCode = (String) param.get("obisCode");
							obisCodeArr = obisCode.split("[.]");
							obisCodeByte = new byte[6];
							for (int i = 0; i < obisCodeArr.length; i++) {
								obisCodeByte[i] = (byte) Integer.parseInt(obisCodeArr[i]);
							}
	
							classId = (String) param.get("classId");
							attributeNo = (String) param.get("attributeNo");
	
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DataUtil.get2ByteToInt(String.valueOf(classId)));
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, obisCodeByte);
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) Integer.parseInt(attributeNo) });
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, Hex.encode("00"));		
							break;
						default:
							break;
						}
	
						break;
					case SET_REQUEST:
						gdDLMSFrame = DataUtil.append(super.encode(hdlcType, dlmsApdu, null, null, command), gdDLMSFrame);
						switch (procedure) {
						case SET_MINOVER_THRESHOLD_DURATION:
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST_LLC.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.NORMAL.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST_INVOKE_ID_AND_PRIORITY.getByteValue());
	
							obisCode = (String) param.get("obisCode");
							obisCodeArr = obisCode.split("[.]");
							obisCodeByte = new byte[6];
							for (int i = 0; i < obisCodeArr.length; i++) {
								obisCodeByte[i] = (byte) Integer.parseInt(obisCodeArr[i]);
							}
	
							classId = (String) param.get("classId");
							attributeNo = (String) param.get("attributeNo");
	
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DataUtil.get2ByteToInt(String.valueOf(classId)));
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, obisCodeByte);
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) Integer.parseInt(attributeNo) });
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, Hex.encode("00"));
	
							//value = (String) param.get("value");
							value = ((String) param.get("value")).replaceAll("\\\\\"", "\"");
							logger.debug("original=" + (String) param.get("value") + "replaced=" + value);
							jsonArr = null;
							if (value == null || value.isEmpty()) {
								jsonArr = new JSONArray();
							} else {
								jsonArr = JSONArray.fromObject(value);
							}
	
							map = (Map<String, Object>) jsonArr.toArray()[0];
	
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) DataType.UINT32.getValue() });
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DataUtil.get4ByteToInt(Integer.parseInt(String.valueOf(map.get("value")))));
	
							break;
						case SET_THRESHOLD_NORMAL:
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST_LLC.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.NORMAL.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST_INVOKE_ID_AND_PRIORITY.getByteValue());
	
							obisCode = (String) param.get("obisCode");
							obisCodeArr = obisCode.split("[.]");
							obisCodeByte = new byte[6];
							for (int i = 0; i < obisCodeArr.length; i++) {
								obisCodeByte[i] = (byte) Integer.parseInt(obisCodeArr[i]);
							}
	
							classId = (String) param.get("classId");
							attributeNo = (String) param.get("attributeNo");
	
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DataUtil.get2ByteToInt(String.valueOf(classId)));
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, obisCodeByte);
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) Integer.parseInt(attributeNo) });
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, Hex.encode("00"));
	
							//value = (String) param.get("value");
							value = ((String) param.get("value")).replaceAll("\\\\\"", "\"");
							logger.debug("original=" + (String) param.get("value") + "replaced=" + value);
							jsonArr = null;
							if (value == null || value.isEmpty()) {
								jsonArr = new JSONArray();
							} else {
								jsonArr = JSONArray.fromObject(value);
							}
	
							map = (Map<String, Object>) jsonArr.toArray()[0];
	
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) DataType.UINT32.getValue() });
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DataUtil.get4ByteToInt(Integer.parseInt(String.valueOf(map.get("value")))));
	
							break;
						case SET_PROFILE_PERIOD:
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST_LLC.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.NORMAL.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST_INVOKE_ID_AND_PRIORITY.getByteValue());
	
							obisCode = (String) param.get("obisCode");
							obisCodeArr = obisCode.split("[.]");
							obisCodeByte = new byte[6];
							for (int i = 0; i < obisCodeArr.length; i++) {
								obisCodeByte[i] = (byte) Integer.parseInt(obisCodeArr[i]);
							}
	
							classId = (String) param.get("classId");
							attributeNo = (String) param.get("attributeNo");
	
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DataUtil.get2ByteToInt(String.valueOf(classId)));
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, obisCodeByte);
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) Integer.parseInt(attributeNo) });
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, Hex.encode("00"));
	
							//value = (String) param.get("value");
							value = ((String) param.get("value")).replaceAll("\\\\\"", "\"");
							logger.debug("original=" + (String) param.get("value") + "replaced=" + value);
							jsonArr = null;
							if (value == null || value.isEmpty()) {
								jsonArr = new JSONArray();
							} else {
								jsonArr = JSONArray.fromObject(value);
							}
	
							map = (Map<String, Object>) jsonArr.toArray()[0];
	
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) DataType.UINT32.getValue() });
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DataUtil.get4ByteToInt(Integer.parseInt(String.valueOf(map.get("value")))));
	
							break;
						case SET_REGISTER_VALUE:
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST_LLC.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.NORMAL.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST_INVOKE_ID_AND_PRIORITY.getByteValue());
	
							obisCode = (String) param.get("obisCode");
							obisCodeArr = obisCode.split("[.]");
							obisCodeByte = new byte[6];
							for (int i = 0; i < obisCodeArr.length; i++) {
								obisCodeByte[i] = (byte) Integer.parseInt(obisCodeArr[i]);
							}
	
							classId = (String) param.get("classId");
							attributeNo = (String) param.get("attributeNo");
	
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DataUtil.get2ByteToInt(String.valueOf(classId)));
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, obisCodeByte);
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) Integer.parseInt(attributeNo) });
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, Hex.encode("00"));
	
							//value = (String) param.get("value");
							value = ((String) param.get("value")).replaceAll("\\\\\"", "\"");
							logger.debug("original=" + (String) param.get("value") + "replaced=" + value);
							jsonArr = null;
							if (value == null || value.isEmpty()) {
								jsonArr = new JSONArray();
							} else {
								jsonArr = JSONArray.fromObject(value);
							}
	
							map = (Map<String, Object>) jsonArr.toArray()[0];
	
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) DataType.UINT32.getValue() });
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DataUtil.get4ByteToInt(Integer.parseInt(String.valueOf(map.get("value")))));
	
							break;
						case SET_REGISTER_UNIT:
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST_LLC.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.NORMAL.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST_INVOKE_ID_AND_PRIORITY.getByteValue());
	
							obisCode = (String) param.get("obisCode");
							obisCodeArr = obisCode.split("[.]");
							obisCodeByte = new byte[6];
							for (int i = 0; i < obisCodeArr.length; i++) {
								obisCodeByte[i] = (byte) Integer.parseInt(obisCodeArr[i]);
							}
	
							classId = (String) param.get("classId");
							attributeNo = (String) param.get("attributeNo");
	
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DataUtil.get2ByteToInt(String.valueOf(classId)));
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, obisCodeByte);
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) Integer.parseInt(attributeNo) });
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, Hex.encode("00"));
	
							//value = (String) param.get("value");
							value = ((String) param.get("value")).replaceAll("\\\\\"", "\"");
							logger.debug("original=" + (String) param.get("value") + "replaced=" + value);
							jsonArr = null;
							if (value == null || value.isEmpty()) {
								jsonArr = new JSONArray();
							} else {
								jsonArr = JSONArray.fromObject(value);
							}
	
							map = (Map<String, Object>) jsonArr.toArray()[0];
							
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) DataType.STRUCTURE.getValue() });
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) DataType.INT8.getValue() });
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) Integer.parseInt(String.valueOf(map.get("scaler"))) });
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) DataType.ENUM.getValue() });
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) Integer.parseInt(String.valueOf(map.get("unit"))) });
	
							break;
						case SET_METER_TIME:
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST_LLC.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.NORMAL.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST_INVOKE_ID_AND_PRIORITY.getByteValue());
	
							obisCode = (String) param.get("obisCode");
							obisCodeArr = obisCode.split("[.]");
							obisCodeByte = new byte[6];
							for (int i = 0; i < obisCodeArr.length; i++) {
								obisCodeByte[i] = (byte) Integer.parseInt(obisCodeArr[i]);
							}
	
							classId = (String) param.get("classId");
							attributeNo = (String) param.get("attributeNo");
	
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DataUtil.get2ByteToInt(String.valueOf(classId)));
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, obisCodeByte);
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) Integer.parseInt(attributeNo) });
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, Hex.encode("00"));
	
							//value = (String) param.get("value");
							value = ((String) param.get("value")).replaceAll("\\\\\"", "\"");
							logger.debug("original=" + (String) param.get("value") + "replaced=" + value);
							jsonArr = null;
							if (value == null || value.isEmpty()) {
								jsonArr = new JSONArray();
							} else {
								jsonArr = JSONArray.fromObject(value);
							}
	
							map = (Map<String, Object>) jsonArr.toArray()[0];
							
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) DataType.OCTET_STRING.getValue() });
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 12 });
							
							String pcTime = String.valueOf(map.get("pcTime"));
							if("true".equals(pcTime)) {
								SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
								String dateTime = sdf.format(new Date());
								
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, DataUtil.get2ByteToInt(Integer.parseInt(dateTime.substring(0, 4))));
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) Integer.parseInt(dateTime.substring(4, 6))});
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) Integer.parseInt(dateTime.substring(6, 8))});
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) 0xff });
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) Integer.parseInt(dateTime.substring(8, 10))});
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) Integer.parseInt(dateTime.substring(10, 12))});
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) Integer.parseInt(dateTime.substring(12, 14))});
							} else {
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, DataUtil.get2ByteToInt(Integer.parseInt(String.valueOf(map.get("year")))));
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) Integer.parseInt(String.valueOf(map.get("month"))) });
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) Integer.parseInt(String.valueOf(map.get("dayOfMonth"))) });
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) 0xff });
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) Integer.parseInt(String.valueOf(map.get("hh"))) });
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) Integer.parseInt(String.valueOf(map.get("mm"))) });
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) Integer.parseInt(String.valueOf(map.get("ss"))) });
							}
	
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) 0xff });
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) 0x80 });
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) 0 });
							int daylight = Integer.parseInt(String.valueOf(map.get("daylight")));
							if(daylight == 0) {
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) 0x00 });
							} else {
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) 0x80 });
							}
							break;
							
						/*
						 * Image Transfer 관련 프로시저	
						 */
						case SET_IMAGE_TRANSFER_ENABLE:
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST_LLC.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.NORMAL.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST_INVOKE_ID_AND_PRIORITY.getByteValue());
	
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ImageTransfer.CLASS_ID.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ImageTransfer.OBIS_CODE.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ImageTransferAttributes.IMAGE_TRANSFER_ENABLED.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, ImageTransfer.OPTION_NOT_USE.getByteValue());
							byte[] enableOption = new byte[2];
							enableOption[0] = (byte) 0x03;
							enableOption[1] = (byte) 0x01;    // Enable True.
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, enableOption);
							break;					
	
						/*
						 *   Billing Cycle 관련 프로시져 
						 */
						case SET_BILLING_CYCLE:
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST_LLC.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.NORMAL.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST_INVOKE_ID_AND_PRIORITY.getByteValue());
	
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, MeterBillingCycleAttributes.CLASS_ID.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, MeterBillingCycleAttributes.OBIS_CODE.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, MeterBillingCycleAttributes.BILLING_CYCLE_EXECUTION_TIME.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, MeterBillingCycleAttributes.OPTION_NOT_USE.getByteValue());
	
							byte[] structureA = new byte[4];
							structureA[0] = (byte) 0x01;
							structureA[1] = (byte) 0x01;
							structureA[2] = (byte) 0x02;
							structureA[3] = (byte) 0x02;
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, structureA);
	
							String[] times = String.valueOf(param.get("time")).split(":");
							byte[] bilingPartA = new byte[6];
							bilingPartA[0] = (byte) 0x09;
							bilingPartA[1] = (byte) 0x04;
							bilingPartA[2] = (byte) DataUtil.getByteToInt(Integer.parseInt(times[0]));
							bilingPartA[3] = (byte) DataUtil.getByteToInt(Integer.parseInt(times[1]));
							bilingPartA[4] = (byte) DataUtil.getByteToInt(Integer.parseInt(times[2]));
							bilingPartA[5] = (byte) 0x00;
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, bilingPartA);
	
							int day = Integer.parseInt(String.valueOf(param.get("day")));
							byte[] bilingPartB = new byte[7];
							bilingPartB[0] = (byte) 0x09;
							bilingPartB[1] = (byte) 0x05;
							bilingPartB[2] = (byte) 0xFF;
							bilingPartB[3] = (byte) 0xFF;
							bilingPartB[4] = (byte) 0xFF;
							bilingPartB[5] = DataUtil.getByteToInt(day);
							bilingPartB[6] = (byte) 0xFF;
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, bilingPartB);
							break;
						/*
						 * Demand Period 관련 프로시저
						 */
						case SET_DEMAND_PLUS_A_PERIOD:
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST_LLC.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.NORMAL.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x40 });
	
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.CLASS_ID.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OBIS_DEMAND_PLUS_A_PERIOD.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.PEROID.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OPTION_NOT_USE.getByteValue());
	
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x06 });
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DataUtil.get4ByteToInt(Integer.parseInt(String.valueOf(param.get("period")))));
							break;
						case SET_DEMAND_PLUS_A_NUMBER:
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST_LLC.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.NORMAL.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x41 });
	
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.CLASS_ID.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OBIS_DEMAND_PLUS_A_NUMBER.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.NUMBER_OF_PERIODS.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OPTION_NOT_USE.getByteValue());
	
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x12 });
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DataUtil.get2ByteToInt(Integer.parseInt(String.valueOf(param.get("number")))));
							break;
						case SET_DEMAND_MINUS_A_PERIOD:
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST_LLC.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.NORMAL.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x42 });
	
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.CLASS_ID.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OBIS_DEMAND_MINUS_A_PERIOD.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.PEROID.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OPTION_NOT_USE.getByteValue());
	
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x06 });
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DataUtil.get4ByteToInt(Integer.parseInt(String.valueOf(param.get("period")))));
							break;
						case SET_DEMAND_MINUS_A_NUMBER:
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST_LLC.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.NORMAL.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x43 });
	
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.CLASS_ID.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OBIS_DEMAND_MINUS_A_NUMBER.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.NUMBER_OF_PERIODS.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OPTION_NOT_USE.getByteValue());
	
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x12 });
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DataUtil.get2ByteToInt(Integer.parseInt(String.valueOf(param.get("number")))));
							break;
						case SET_DEMAND_PLUS_R_PERIOD:
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST_LLC.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.NORMAL.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x44 });
	
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.CLASS_ID.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OBIS_DEMAND_PLUS_R_PERIOD.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.PEROID.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OPTION_NOT_USE.getByteValue());
	
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x06 });
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DataUtil.get4ByteToInt(Integer.parseInt(String.valueOf(param.get("period")))));
							break;
						case SET_DEMAND_PLUS_R_NUMBER:
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST_LLC.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.NORMAL.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x45 });
	
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.CLASS_ID.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OBIS_DEMAND_PLUS_R_NUMBER.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.NUMBER_OF_PERIODS.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OPTION_NOT_USE.getByteValue());
	
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x12 });
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DataUtil.get2ByteToInt(Integer.parseInt(String.valueOf(param.get("number")))));
							break;
						case SET_DEMAND_MINUS_R_PERIOD:
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST_LLC.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.NORMAL.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x46 });
	
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.CLASS_ID.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OBIS_DEMAND_MINUS_R_PERIOD.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.PEROID.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OPTION_NOT_USE.getByteValue());
	
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x06 });
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DataUtil.get4ByteToInt(Integer.parseInt(String.valueOf(param.get("period")))));
							break;
						case SET_DEMAND_MINUS_R_NUMBER:
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST_LLC.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.NORMAL.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x47 });
	
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.CLASS_ID.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OBIS_DEMAND_MINUS_R_NUMBER.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.NUMBER_OF_PERIODS.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OPTION_NOT_USE.getByteValue());
	
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x12 });
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DataUtil.get2ByteToInt(Integer.parseInt(String.valueOf(param.get("number")))));
							break;
						case SET_DEMAND_R_QI_PERIOD:
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST_LLC.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.NORMAL.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x48 });
	
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.CLASS_ID.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OBIS_DEMAND_R_QI_PERIOD.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.PEROID.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OPTION_NOT_USE.getByteValue());
	
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x06 });
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DataUtil.get4ByteToInt(Integer.parseInt(String.valueOf(param.get("period")))));
							break;
						case SET_DEMAND_R_QI_NUMBER:
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST_LLC.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.NORMAL.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x49 });
	
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.CLASS_ID.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OBIS_DEMAND_R_QI_NUMBER.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.NUMBER_OF_PERIODS.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OPTION_NOT_USE.getByteValue());
	
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x12 });
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DataUtil.get2ByteToInt(Integer.parseInt(String.valueOf(param.get("number")))));
							break;
						case SET_DEMAND_R_QIV_PERIOD:
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST_LLC.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.NORMAL.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x4A });
	
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.CLASS_ID.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OBIS_DEMAND_R_QIV_PERIOD.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.PEROID.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OPTION_NOT_USE.getByteValue());
	
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x06 });
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DataUtil.get4ByteToInt(Integer.parseInt(String.valueOf(param.get("period")))));
							break;
						case SET_DEMAND_R_QIV_NUMBER:
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST_LLC.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.NORMAL.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x4B });
	
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.CLASS_ID.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OBIS_DEMAND_R_QIV_NUMBER.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.NUMBER_OF_PERIODS.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OPTION_NOT_USE.getByteValue());
	
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x12 });
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DataUtil.get2ByteToInt(Integer.parseInt(String.valueOf(param.get("number")))));
							break;
						case SET_DEMAND_PLUS_PERIOD:
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST_LLC.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.NORMAL.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x4C });
	
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.CLASS_ID.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OBIS_DEMAND_PLUS_PERIOD.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.PEROID.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OPTION_NOT_USE.getByteValue());
	
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x06 });
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DataUtil.get4ByteToInt(Integer.parseInt(String.valueOf(param.get("period")))));
							break;
						case SET_DEMAND_PLUS_NUMBER:
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST_LLC.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.NORMAL.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x4D });
	
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.CLASS_ID.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OBIS_DEMAND_PLUS_NUMBER.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.NUMBER_OF_PERIODS.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OPTION_NOT_USE.getByteValue());
	
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x12 });
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DataUtil.get2ByteToInt(Integer.parseInt(String.valueOf(param.get("number")))));
							break;
						case SET_DEMAND_MINUS_PERIOD:
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST_LLC.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.NORMAL.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x4E });
	
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.CLASS_ID.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OBIS_DEMAND_MINUS_PERIOD.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.PEROID.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OPTION_NOT_USE.getByteValue());
	
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x06 });
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DataUtil.get4ByteToInt(Integer.parseInt(String.valueOf(param.get("period")))));
							break;
						case SET_DEMAND_MINUS_NUMBER:
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST_LLC.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.NORMAL.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x40 });
	
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.CLASS_ID.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OBIS_DEMAND_MINUS_NUMBER.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.NUMBER_OF_PERIODS.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DemandPeriodAttributes.OPTION_NOT_USE.getByteValue());
	
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x12 });
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DataUtil.get2ByteToInt(Integer.parseInt(String.valueOf(param.get("number")))));
							break;
						/*
						 * TOU Setting 관련
						 */
						case SET_CALENDAR_NAME_PASSIVE:
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST_LLC.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.NORMAL.getByteValue());
							//gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[]{0x40});
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, getPriorityByteValue());
	
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, TOUAttributes.CLASS_ID.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, TOUAttributes.ACTIVITY_CALENDAR.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, TOUAttributes.CALENDAR_NAME_PASSIVE.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, TOUAttributes.OPTION_NOT_USE.getByteValue());
	
							byte[] calendarName = String.valueOf(param.get("calendarNamePassive")).getBytes();
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x09 });
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { DataUtil.getByteToInt(calendarName.length) });
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, calendarName);
							break;
						case SET_SEASON_PROFILE:
						case SET_WEEK_PROFILE:
						case SET_DAY_PROFILE:
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST_LLC.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST.getByteValue());
	
							if (param.get("infoBlockType") == TOUInfoBlockType.FIRST_BLOCK && !param.containsKey("blockNumber")) { // Single모드 전송인경우
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.NORMAL.getByteValue());
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, getPriorityByteValue());
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, TOUAttributes.CLASS_ID.getByteValue());
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, TOUAttributes.ACTIVITY_CALENDAR.getByteValue());
	
								if (procedure == Procedure.SET_SEASON_PROFILE) {
									gdDLMSFrame = DataUtil.append(gdDLMSFrame, TOUAttributes.SEASON_PROFILE_PASSIVE.getByteValue());
								} else if (procedure == Procedure.SET_WEEK_PROFILE) {
									gdDLMSFrame = DataUtil.append(gdDLMSFrame, TOUAttributes.WEEK_PROFILE_TABLE_PASSIVE.getByteValue());
								} else if (procedure == Procedure.SET_DAY_PROFILE) {
									gdDLMSFrame = DataUtil.append(gdDLMSFrame, TOUAttributes.DAY_PROFILE_TABLE_PASSIVE.getByteValue());
								}
	
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, TOUAttributes.OPTION_NOT_USE.getByteValue());
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, (byte[]) param.get("blockValue"));
	
							} else if (param.get("infoBlockType") == TOUInfoBlockType.FIRST_BLOCK && param.containsKey("blockNumber")) { // Multi 모드 전송인경우
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.WITH_FIRST_DATABLOCK.getByteValue());
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, getPriorityByteValue(true));
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, TOUAttributes.CLASS_ID.getByteValue());
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, TOUAttributes.ACTIVITY_CALENDAR.getByteValue());
	
								if (procedure == Procedure.SET_SEASON_PROFILE) {
									gdDLMSFrame = DataUtil.append(gdDLMSFrame, TOUAttributes.SEASON_PROFILE_PASSIVE.getByteValue());
								} else if (procedure == Procedure.SET_WEEK_PROFILE) {
									gdDLMSFrame = DataUtil.append(gdDLMSFrame, TOUAttributes.WEEK_PROFILE_TABLE_PASSIVE.getByteValue());
								} else if (procedure == Procedure.SET_DAY_PROFILE) {
									gdDLMSFrame = DataUtil.append(gdDLMSFrame, TOUAttributes.DAY_PROFILE_TABLE_PASSIVE.getByteValue());
								}
	
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, TOUAttributes.OPTION_NOT_USE.getByteValue());
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, (boolean) param.get("isLastBlock") == true ? new byte[] { (byte) 0xFF } : new byte[] { 0x00 });
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, DataUtil.get4ByteToInt(Integer.parseInt(String.valueOf(param.get("blockNumber")))));
	
								int bLength = Integer.parseInt(String.valueOf(param.get("blockLength")));
								byte[] blockLength;
								if (128 <= bLength) {
									blockLength = new byte[] { (byte) 0x81, DataUtil.getByteToInt(bLength) };
								} else {
									blockLength = new byte[] { DataUtil.getByteToInt(bLength) };
								}
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, blockLength);
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, (byte[]) param.get("blockValue"));
	
							} else if (param.get("infoBlockType") == TOUInfoBlockType.MIDDLE_BLOCK) {
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.WITH_DATABLOCK.getByteValue());
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, getPriorityByteValue(true));
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, (boolean) param.get("isLastBlock") == true ? new byte[] { (byte) 0xFF } : new byte[] { 0x00 });
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, DataUtil.get4ByteToInt(Integer.parseInt(String.valueOf(param.get("blockNumber")))));
	
								int bLength = Integer.parseInt(String.valueOf(param.get("blockLength")));
								byte[] blockLength;
								if (128 <= bLength) {
									blockLength = new byte[] { (byte) 0x81, DataUtil.getByteToInt(bLength) };
								} else {
									blockLength = new byte[] { DataUtil.getByteToInt(bLength) };
								}
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, blockLength);
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, (byte[]) param.get("blockValue"));
	
							} else if (param.get("infoBlockType") == TOUInfoBlockType.LAST_BLOCK) {
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.WITH_DATABLOCK.getByteValue());
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, getPriorityByteValue());
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, (boolean) param.get("isLastBlock") == true ? new byte[] { (byte) 0xFF } : new byte[] { 0x00 });
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, DataUtil.get4ByteToInt(Integer.parseInt(String.valueOf(param.get("blockNumber")))));
	
								int bLength = Integer.parseInt(String.valueOf(param.get("blockLength")));
								byte[] blockLength;
								if (128 <= bLength) {
									blockLength = new byte[] { (byte) 0x81, DataUtil.getByteToInt(bLength) };
								} else {
									blockLength = new byte[] { DataUtil.getByteToInt(bLength) };
								}
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, blockLength);
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, (byte[]) param.get("blockValue"));
							}
	
							break;
						case SET_STARTING_DATE:
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST_LLC.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.NORMAL.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, getPriorityByteValue());
	
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, TOUAttributes.CLASS_ID.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, TOUAttributes.ACTIVITY_CALENDAR.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, TOUAttributes.ACTIVATE_PASSIVE_CALENDAR_TIME.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, TOUAttributes.OPTION_NOT_USE.getByteValue());
	
							String startingDate = String.valueOf(param.get("startingDate"));
							Calendar cal = DateTimeUtil.getCalendar(startingDate);
							byte[] dateTime = DataUtil.getDLMS_OCTETSTRING12ByDateTime(cal);
	
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x09 });
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { DataUtil.getByteToInt(dateTime.length) });
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, dateTime);
							break;
						case SET_DISCONNECT_CONTROL:
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST_LLC.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.NORMAL.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST_INVOKE_ID_AND_PRIORITY.getByteValue());
	
							obisCode = (String) param.get("obisCode");
							obisCodeArr = obisCode.split("[.]");
							obisCodeByte = new byte[6];
							for (int i = 0; i < obisCodeArr.length; i++) {
								obisCodeByte[i] = (byte) Integer.parseInt(obisCodeArr[i]);
							}
	
							classId = (String) param.get("classId");
							attributeNo = (String) param.get("attributeNo");
	
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DataUtil.get2ByteToInt(String.valueOf(classId)));
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, obisCodeByte);
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) Integer.parseInt(attributeNo) });
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, Hex.encode("00"));
	
							//value = (String) param.get("value");
							value = ((String) param.get("value")).replaceAll("\\\\\"", "\"");
							logger.debug("original=" + (String) param.get("value") + "replaced=" + value);
							jsonArr = null;
							if (value == null || value.isEmpty()) {
								jsonArr = new JSONArray();
							} else {
								jsonArr = JSONArray.fromObject(value);
							}
	
							map = (Map<String, Object>) jsonArr.toArray()[0];
	
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) DataType.BOOLEAN.getValue() });
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DataUtil.get2ByteToInt(Integer.parseInt(String.valueOf(param.get("value")))));
	
							break;
						case SET_VALUE:
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST_LLC.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.NORMAL.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST_INVOKE_ID_AND_PRIORITY.getByteValue());
	
							obisCode = (String) param.get("obisCode");
							obisCodeArr = obisCode.split("[.]");
							obisCodeByte = new byte[6];
							for (int i = 0; i < obisCodeArr.length; i++) {
								obisCodeByte[i] = (byte) Integer.parseInt(obisCodeArr[i]);
							}
	
							classId = (String) param.get("classId");
							attributeNo = (String) param.get("attributeNo");
	
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DataUtil.get2ByteToInt(String.valueOf(classId)));
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, obisCodeByte);
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) Integer.parseInt(attributeNo) });
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, Hex.encode("00"));
					
							//value = (String) param.get("value");
							value = ((String) param.get("value")).replaceAll("\\\\\"", "\"");
							logger.debug("original=" + (String) param.get("value") + "replaced=" + value);
							String dataType = (String) param.get("dataType");
							jsonArr = null;
							if (value == null || value.isEmpty()) {
								jsonArr = new JSONArray();
							} else {
								jsonArr = JSONArray.fromObject(value);
							}
	
							map = (Map<String, Object>) jsonArr.toArray()[0];
							if ("integer".equals(dataType)){
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) DataType.INT8.getValue() });
								byte data[] = new byte[] { Byte.parseByte(String.valueOf(map.get("value")))};
								logger.debug("integer:" + Hex.decode(data));
								gdDLMSFrame = DataUtil.append(gdDLMSFrame,  data);
							}
							else if ( "unsigned".equals(dataType)){
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) DataType.UINT8.getValue() });
								byte data[] =   new byte[] { DataUtil.getByteToInt(Integer.parseInt(String.valueOf(map.get("value"))))};
								logger.debug("unsigned:" + Hex.decode(data));
								gdDLMSFrame = DataUtil.append(gdDLMSFrame,data );		
							}
							else if ( "long".equals(dataType)){
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) DataType.INT16.getValue() });
								byte data[] = DataUtil.get2ByteToInt(Short.parseShort(String.valueOf(map.get("value"))));
								logger.debug("long:" + Hex.decode(data));
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, data);
							}
							else if ( "long-unsigned".equals(dataType)){
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) DataType.UINT16.getValue() });
								byte data[] = DataUtil.get2ByteToInt(Integer.parseInt(String.valueOf(map.get("value"))));
								logger.debug("long-unsigned:" + Hex.decode(data));
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, data);
							}
							else if ( "double-long".equals(dataType)){
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) DataType.INT32.getValue() });
								byte data[] = DataUtil.get4ByteToInt(Integer.parseInt(String.valueOf(map.get("value"))));
								logger.debug("double-long:" + Hex.decode(data));
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, data );
							}
							else if ( "double-long-unsigned".equals(dataType)){
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) DataType.UINT32.getValue() });
								byte data[] = DataUtil.get4ByteToInt(Integer.parseUnsignedInt(String.valueOf(map.get("value"))));
								logger.debug("double-long-unsigned:" + Hex.decode(data));
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, data );
							}
							else if ( "long64".equals(dataType)){
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) DataType.INT64.getValue() });
								byte data[] = DataUtil.get8ByteToInt(Long.parseLong(String.valueOf(map.get("value"))));
								logger.debug("long64:" + Hex.decode(data));
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, data);
							}
							else if ( "long64unsigned".equals(dataType)){
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) DataType.UINT64.getValue() });
								byte data[] = DataUtil.get8ByteToInt(Long.parseUnsignedLong(String.valueOf(map.get("value"))));
								logger.debug("long64unsigned:" + Hex.decode(data));
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, data);			
							}
							else if ( "boolean".equals(dataType)){
								String boolValue = (String)map.get("value");
								if ( "true".equals(boolValue)){
									gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) DataType.BOOLEAN.getValue() });
									gdDLMSFrame = DataUtil.append(gdDLMSFrame,  new byte[] { (byte) 0xFF } );
								}
								else if ("false".equals(boolValue)){
									gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) DataType.BOOLEAN.getValue() });
									gdDLMSFrame = DataUtil.append(gdDLMSFrame,  new byte[] { (byte) 0x00 } );
								}
								else {
									logger.error("DLMS Encoding Error - dataType[" + dataType +"] , value["+ boolValue +"]" );
									gdDLMSFrame = null;	
								}
							}
							else if ( "octet-string".equals(dataType)){
								String strHexVal = String.valueOf(map.get("value"));
								byte[] val = HexUtil.convertHexStringToBytes(strHexVal);
	
								logger.debug("KEY_HEX=" + HexUtil.conventBytesToHexString(val));
								byte[] data = new byte[2];
								
								data[0] = DLMSCommonDataType.OctetString.getValue();
								data[1] = DataUtil.getByteToInt(val.length);
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, data);
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, val);
							}
							else if ( "enum".equals(dataType)){
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) DataType.ENUM.getValue() });
								byte data[] =   new byte[] { DataUtil.getByteToInt(Integer.parseInt(String.valueOf(map.get("value"))))};
								logger.debug("enum:" + Hex.decode(data));
								gdDLMSFrame = DataUtil.append(gdDLMSFrame,data );		
							}
							else {
								logger.error("DLMS Encoding Error - dataType[" + dataType +"] is not supported" );
								gdDLMSFrame = null;	
							}
							break;
						case SET_SINGLE_ACTION_SCHEDULE:
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST_LLC.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.NORMAL.getByteValue());
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, SetRequest.SET_REQUEST_INVOKE_ID_AND_PRIORITY.getByteValue());
	
							obisCode = (String) param.get("obisCode");
							obisCodeArr = obisCode.split("[.]");
							obisCodeByte = new byte[6];
							for (int i = 0; i < obisCodeArr.length; i++) {
								obisCodeByte[i] = (byte) Integer.parseInt(obisCodeArr[i]);
							}
	
							classId = (String) param.get("classId");
							attributeNo = (String) param.get("attributeNo");
	
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, DataUtil.get2ByteToInt(String.valueOf(classId)));
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, obisCodeByte);
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) Integer.parseInt(attributeNo) });
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, Hex.encode("00"));
					
							value = ((String) param.get("value")).replaceAll("\\\\\"", "\"");
							logger.debug("original=" + (String) param.get("value") + "replaced=" + value);
							jsonArr = null;
							if (value == null || value.isEmpty()) {
								jsonArr = new JSONArray();
							} else {
								jsonArr = JSONArray.fromObject(value);
							}
							int arraySize = jsonArr.size();
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) DataType.ARRAY.getValue() });
							gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { DataUtil.getByteToInt(arraySize) });
							for (int i = 0; i < arraySize; i++) {
	
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { (byte) DataType.STRUCTURE.getValue() });
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { 0x02 });
								
								logger.debug("array[" + i +"] value=" + String.valueOf(jsonArr.get(i)));
								// ["YYYYMMDDWWhhmmss00"]
								// hhmmss00
								String strTime = String.valueOf(jsonArr.get(i)).substring(12, 20);
								// YYYYMMDDWW
								String strDate = String.valueOf(jsonArr.get(i)).substring(2, 12);
								for (int j = 0; j < 2; j++) {
									byte[] val = null;
									if (j == 0) {
										val = new byte[] {};
										val = DataUtil.append(val, strTime.substring(0, 2).equals("FF") ? 
												new byte[] {(byte) 0xff } : new byte[] {(byte)Integer.parseInt(strTime.substring(0, 2))});
										val = DataUtil.append(val, strTime.substring(2, 4).equals("FF") ? 
												new byte[] {(byte) 0xff } : new byte[] {(byte)Integer.parseInt(strTime.substring(2, 4))});
										val = DataUtil.append(val, strTime.substring(4, 6).equals("FF") ? 
												new byte[] {(byte) 0xff } : new byte[] {(byte)Integer.parseInt(strTime.substring(4, 6))});
										val = DataUtil.append(val, new byte[] { (byte) 0x00 });
									} else {
										val = new byte[] {};
										val = DataUtil.append(val, strDate.substring(0, 4).equals("FFFF") ? 
												new byte[] {(byte)0xff , (byte)0xff} : DataUtil.get2ByteToInt(Integer.parseInt(strDate.substring(0, 4))));
										val = DataUtil.append(val, strDate.substring(4, 6).equals("FF") ? 
												new byte[] {(byte) 0xff } : new byte[] {(byte)Integer.parseInt(strDate.substring(4, 6))});
										val = DataUtil.append(val, strDate.substring(6, 8).equals("FF") ? 
												new byte[] {(byte) 0xff } : new byte[] {(byte)Integer.parseInt(strDate.substring(6, 8))});
										val = DataUtil.append(val, strDate.substring(8, 10).equals("FF") ? 
												new byte[] {(byte) 0xff } : new byte[] {(byte)Integer.parseInt(strDate.substring(8, 10))});
									}							
									
									logger.debug("KEY_HEX=" + HexUtil.conventBytesToHexString(val));
									byte[] data = new byte[2];
									
									data[0] = DLMSCommonDataType.OctetString.getValue();
									data[1] = DataUtil.getByteToInt(val.length);
									gdDLMSFrame = DataUtil.append(gdDLMSFrame, data);
									gdDLMSFrame = DataUtil.append(gdDLMSFrame, val);
								}
							}
							
							break;					
						default:
							break;
						}
						break;
					case KAIFA_CUSTOM : 
						gdDLMSFrame = DataUtil.append(super.encode(hdlcType, dlmsApdu, null, null, command), gdDLMSFrame);
	
						switch (procedure) {
							case GET_SORIA_METER_KEY_A:
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, KaifaCustomRequest.KAIFA_CUSTOM_LLC.getByteValue());
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, KaifaCustomRequest.GET_SORIA_METER_KEY_A.getByteValue());
								break;
							case GET_SORIA_METER_KEY_B:
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, KaifaCustomRequest.KAIFA_CUSTOM_LLC.getByteValue());
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, KaifaCustomRequest.GET_SORIA_METER_KEY_B.getByteValue());
								break;
							case GET_SORIA_METER_KEY_C:
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, KaifaCustomRequest.KAIFA_CUSTOM_LLC.getByteValue());
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, KaifaCustomRequest.GET_SORIA_METER_KEY_C.getByteValue());
								break;
							case SET_SORIA_METER_SERIAL_A:
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, KaifaCustomRequest.KAIFA_CUSTOM_LLC.getByteValue());
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, KaifaCustomRequest.SET_SORIA_METER_SERIAL_A.getByteValue());
								break;
							case SET_SORIA_METER_SERIAL_B:
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, KaifaCustomRequest.KAIFA_CUSTOM_LLC.getByteValue());
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, KaifaCustomRequest.SET_SORIA_METER_SERIAL_B.getByteValue());
								break;
							case SET_SORIA_METER_SERIAL_C:
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, KaifaCustomRequest.KAIFA_CUSTOM_LLC.getByteValue());
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, KaifaCustomRequest.SET_SORIA_METER_SERIAL_C.getByteValue());	
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, ((String) param.get("meterSerial")).getBytes());//append meter serial
								break;
							case SET_SORIA_METER_SERIAL_D:
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, KaifaCustomRequest.KAIFA_CUSTOM_LLC.getByteValue());
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, KaifaCustomRequest.SET_SORIA_METER_SERIAL_D.getByteValue());
								break;
							case SET_SORIA_METER_SERIAL_E:
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, KaifaCustomRequest.KAIFA_CUSTOM_LLC.getByteValue());
								gdDLMSFrame = DataUtil.append(gdDLMSFrame, KaifaCustomRequest.SET_SORIA_METER_SERIAL_E.getByteValue());
								break;
								
						default:
							break;
						}
						
						break;
//					case DISC:
//						gdDLMSFrame = super.encode(hdlcType, null, null, command);
//						break;
					default:
						break;
					
				}
			}

		} catch (Exception e) {
			logger.error("DLMS Encoding Error - {}", e);
			gdDLMSFrame = null;
		}

		return gdDLMSFrame;
	}

	@Override
	public boolean decode(MultiSession session, byte[] frame, Procedure procedure, String command) {
		logger.info("## Excute NestedDLMSDecorator Decoding...");
		boolean result = true;
		int pos = 0;
		int infoPos = 0;

		byte[] llc = null;
		byte[] information = null;

		try {
			//818014050200AF060200AF070400000001080400000001
			/*
			 * 81
			 * 80
			 * 14
			 * 	05
			 * 	02
			 * 	00AF
			 * 	06
			 * 	02
			 * 	00AF
			 * 	07
			 * 	04
			 * 	00000001
			 * 	08
			 * 	04
			 * 	00000001
			 */
//			if (HdlcObjectType.getItem(getType()) == HdlcObjectType.UA) {
			if (getHDLCFrameType() == HdlcFrameType.UA) {
				byte[] formatIdentifier = new byte[1];
				System.arraycopy(frame, infoPos, formatIdentifier, 0, formatIdentifier.length);
				infoPos += formatIdentifier.length;
				logger.debug("[DLMS] FORMAT_IDENTIFIER = [{}]", Hex.decode(formatIdentifier));

				byte[] groupIdentifier = new byte[1];
				System.arraycopy(frame, infoPos, groupIdentifier, 0, groupIdentifier.length);
				infoPos += groupIdentifier.length;
				logger.debug("[DLMS] GROUP_IDENTIFIER = [{}]", Hex.decode(groupIdentifier));

				byte[] groupLength = new byte[1];
				System.arraycopy(frame, infoPos, groupLength, 0, groupLength.length);
				infoPos += groupLength.length;
				logger.debug("[DLMS] GROUP_LENGTH = [{}]", Hex.decode(groupLength));

				byte[] sendIdentifier = new byte[1];
				System.arraycopy(frame, infoPos, sendIdentifier, 0, sendIdentifier.length);
				infoPos += sendIdentifier.length;
				logger.debug("[DLMS] PARAM_SEND_IDENTIFIER = [{}]", Hex.decode(sendIdentifier));

				byte[] sendLength = new byte[1];
				System.arraycopy(frame, infoPos, sendLength, 0, sendLength.length);
				infoPos += sendLength.length;
				logger.debug("[DLMS] PARAM_SEND_LENGTH = [{}]", Hex.decode(sendLength));

//				byte[] sendValue = new byte[2];
//				System.arraycopy(frame, infoPos, sendValue, 0, sendValue.length);
//				infoPos += sendValue.length;
//				logger.debug("[DLMS] PARAM_SEND_VALUE = [{}]", DataUtil.getIntTo2Byte(sendValue));
				
				int maximumTransmitLength = 0;
				if (31 < getHDLCFrameLength()) {
					byte[] sendValue = new byte[2];
					System.arraycopy(frame, infoPos, sendValue, 0, sendValue.length);
					infoPos += sendValue.length;

					maximumTransmitLength = DataUtil.getIntTo2Byte(sendValue);
				} else {
					byte[] sendValue = new byte[1];
					System.arraycopy(frame, infoPos, sendValue, 0, sendValue.length);
					infoPos += sendValue.length;

					maximumTransmitLength = DataUtil.getIntToByte(sendValue[0]);
				}
				logger.debug("[DLMS] MAXIMUM_TRANSMIT_LENGTH = [{}]", maximumTransmitLength);
				

				byte[] receiveIdentifier = new byte[1];
				System.arraycopy(frame, infoPos, receiveIdentifier, 0, receiveIdentifier.length);
				infoPos += receiveIdentifier.length;
				logger.debug("[DLMS] PARAM_RECEIVE_IDENTIFIER = [{}]", Hex.decode(receiveIdentifier));

				byte[] receiveLength = new byte[1];
				System.arraycopy(frame, infoPos, receiveLength, 0, receiveLength.length);
				infoPos += receiveLength.length;
				logger.debug("[DLMS] PARAM_RECEIVE_LENGTH = [{}]", Hex.decode(receiveLength));

//				byte[] receiveValue = new byte[2];
//				System.arraycopy(frame, infoPos, receiveValue, 0, receiveValue.length);
//				infoPos += receiveValue.length;
//				int sendHdlcPacketMaxSize = DataUtil.getIntTo2Byte(receiveValue);
//				logger.debug("[DLMS] PARAM_RECEIVE_VALUE = [{}]", sendHdlcPacketMaxSize);
//				setResultData(sendHdlcPacketMaxSize);
//				
				int sendHdlcPacketMaxSize = 0;
				if (31 < getHDLCFrameLength()) {
					byte[] receiveValue = new byte[2];
					System.arraycopy(frame, infoPos, receiveValue, 0, receiveValue.length);
					infoPos += receiveValue.length;
					sendHdlcPacketMaxSize = DataUtil.getIntTo2Byte(receiveValue);
				} else {
					byte[] receiveValue = new byte[1];
					System.arraycopy(frame, infoPos, receiveValue, 0, receiveValue.length);
					infoPos += receiveValue.length;
					sendHdlcPacketMaxSize = DataUtil.getIntToByte(receiveValue[0]);
				}
				logger.debug("[DLMS] MAXIMUM_RECEIVE_LENGTH = [{}]", sendHdlcPacketMaxSize);
				setResultData(sendHdlcPacketMaxSize);
				
				byte[] sendWindowIdentifier = new byte[1];
				System.arraycopy(frame, infoPos, sendWindowIdentifier, 0, sendWindowIdentifier.length);
				infoPos += sendWindowIdentifier.length;
				logger.debug("[DLMS] PARAM_WINDOW_SEND_IDENTIFIER = [{}]", Hex.decode(sendWindowIdentifier));

				byte[] sendWindowLength = new byte[1];
				System.arraycopy(frame, infoPos, sendWindowLength, 0, sendWindowLength.length);
				infoPos += sendWindowLength.length;
				logger.debug("[DLMS] PARAM_WINDOW_SEND_LENGTH = [{}]", Hex.decode(sendWindowLength));

				byte[] sendWindowValue = new byte[4];
				System.arraycopy(frame, infoPos, sendWindowValue, 0, sendWindowValue.length);
				infoPos += sendWindowValue.length;
				logger.debug("[DLMS] PARAM_WINDOW_SEND_VALUE = [{}]", DataUtil.getIntTo4Byte(sendWindowValue));

				byte[] receiveWindowIdentifier = new byte[1];
				System.arraycopy(frame, infoPos, receiveWindowIdentifier, 0, receiveWindowIdentifier.length);
				infoPos += receiveWindowIdentifier.length;
				logger.debug("[DLMS] PARAM_WINDOW_RECEIVE_IDENTIFIER = [{}]", Hex.decode(receiveWindowIdentifier));

				byte[] receiveWindowLength = new byte[1];
				System.arraycopy(frame, infoPos, receiveWindowLength, 0, receiveWindowLength.length);
				infoPos += receiveWindowLength.length;
				logger.debug("[DLMS] PARAM_WINDOW_RECEIVE_LENGTH = [{}]", Hex.decode(receiveWindowLength));

				byte[] receiveWindowValue = new byte[4];
				System.arraycopy(frame, infoPos, receiveWindowValue, 0, receiveWindowValue.length);
				infoPos += receiveWindowValue.length;
				logger.debug("[DLMS] PARAM_WINDOW_RECEIVE_LENGTH = [{}]", DataUtil.getIntTo4Byte(receiveWindowValue));

				logger.debug("[DLMS] UA = [{}]", Hex.decode(frame));
			} else if (getHDLCFrameType() == HdlcFrameType.I) {

				llc = new byte[3];
				pos = 0;

				System.arraycopy(frame, pos, llc, 0, llc.length);
				pos += llc.length;
				logger.debug("[DLMS] LLC = [{}]", Hex.decode(llc));

				information = new byte[frame.length - 3]; // 3 : llc
				System.arraycopy(frame, pos, information, 0, information.length);

				/**
				 * IFrame Type 파싱
				 */
				List<HashMap<AARE, byte[]>> aareList = new ArrayList<HashMap<AARE, byte[]>>();

				infoPos = 0;
				byte[] commandType = new byte[1];
				System.arraycopy(information, infoPos, commandType, 0, commandType.length);
				infoPos += commandType.length;

				// Command 타입설정
//				setType(DataUtil.getIntToByte(commandType[0]));
//				logger.debug("[DLMS] COMMAND_TYPE = [{}]", HdlcObjectType.getItem(commandType[0]).name());

				setDlmsApdu(XDLMS_APDU.getItem(commandType[0]));
				logger.debug("[DLMS] COMMAND_TYPE = [{}]", getDlmsApdu().name());				
				//if (HdlcObjectType.getItem(commandType[0]) == HdlcObjectType.AARE) {
				if (getDlmsApdu() == XDLMS_APDU.AARE) {
					/**
					 * AARE Parsing
					 */
					byte[] infoLength = new byte[1];
					System.arraycopy(information, infoPos, infoLength, 0, infoLength.length);
					infoPos += infoLength.length;

					for (int i = infoPos; i < DataUtil.getIntToByte(infoLength[0]); i = infoPos) {
						byte[] tagLength = new byte[2];
						System.arraycopy(information, infoPos, tagLength, 0, tagLength.length);
						infoPos += tagLength.length;

						AARE tag = AARE.getItem(tagLength[0]);

						// Server System Title or StoC 일 경우 한단계 더 들어감.
						if (AARE.getItem(tagLength[0]) == AARE.RESPONDING_AP_TITLE || AARE.getItem(tagLength[0]) == AARE.RESPONDING_AUTHENTICATION_VALUE) {
							tagLength = new byte[2];
							System.arraycopy(information, infoPos, tagLength, 0, tagLength.length);
							infoPos += tagLength.length;
						}

						int valueLength = DataUtil.getIntToByte(tagLength[1]);
						byte[] value = new byte[valueLength];
						System.arraycopy(information, infoPos, value, 0, value.length);
						infoPos += value.length;

						HashMap<AARE, byte[]> item = new HashMap<AARE, byte[]>();
						item.put(tag, value);
						aareList.add(item);
						logger.debug("[DLMS] AARE tag={}, value={}", tag, Hex.decode(value));
					}

					Iterator<HashMap<AARE, byte[]>> aareIeter = aareList.iterator();
					while (aareIeter.hasNext()) {
						HashMap<AARE, byte[]> map = aareIeter.next();
						if (map.containsKey(AARE.RESPONDING_AP_TITLE)) { // responding-AP-title
							aareRespondingAPtitle = map.get(AARE.RESPONDING_AP_TITLE);
						} else if (map.containsKey(AARE.RESPONDING_AUTHENTICATION_VALUE)) { // Authentication value
							aareAuthenticationValue = map.get(AARE.RESPONDING_AUTHENTICATION_VALUE);
						}else if (map.containsKey(AARE.USER_INFORMATION)) { // User-information
							byte[] aareUInformation = map.get(AARE.USER_INFORMATION);  // 0421281F30000000012E1EF9DF04BF01432560CADA2AA0F996406E6BE853CA5AA6FA2F
							//byte[] initateResponse = map.get(AARE.USER_INFORMATION);
							
							byte[] initateResponse = new byte[DataUtil.getIntToByte(aareUInformation[1])];  // 281F30000000012E1EF9DF04BF01432560CADA2AA0F996406E6BE853CA5AA6FA2F
							System.arraycopy(aareUInformation, 2, initateResponse, 0, initateResponse.length);
							
							if (XDLMS_APDU.getItem(DataUtil.getIntToByte(initateResponse[0])) == XDLMS_APDU.GLO_INITIATERESPONSE) {
								int valueLength = DataUtil.getIntToByte(initateResponse[1]);
								byte SC = initateResponse[2];
								logger.debug("[DLMS] SC={}", Hex.decode(new byte[]{SC}));								
								
								byte[] IC = new byte[4];
								System.arraycopy(initateResponse, 3, IC, 0, IC.length);
								logger.debug("[DLMS] IC={}", Hex.decode(IC));

								byte[] cyperTextValue = new byte[valueLength - 5]; // 5 = SC + IC 
								System.arraycopy(initateResponse, 7, cyperTextValue, 0, cyperTextValue.length);

								logger.debug("### AARE Cyper TextValue = " + Hex.decode(cyperTextValue));

								HLSAuthForIESCO aareAuth = new HLSAuthForIESCO(HLSSecurityControl.getItem(SC), getMeterId());
								byte[] plainText = aareAuth.doDecryption(IC, aareRespondingAPtitle, cyperTextValue);
								logger.debug("### AARE Plain TextValue = " + Hex.decode(plainText));
							} else {
								logger.warn("[AARE UserInformation Validation Fail~!! - Unknown GLO_INITIATERESPONSE packet.[" + Hex.getHexDump(initateResponse) + "]");
							}
						}
					}

					logger.debug("[DLMS] ## SERVER_SYSTEM_TITLE = [{}]", Hex.decode(aareRespondingAPtitle));
					logger.debug("[DLMS] ## S_TO_C = [{}]", Hex.decode(aareAuthenticationValue));

					if (aareRespondingAPtitle == null || aareRespondingAPtitle.equals("") || aareAuthenticationValue == null || aareAuthenticationValue.equals("")) {
						setResultData(false);
					} else {
						setResultData(true);
					}
				//} else if (HdlcObjectType.getItem(commandType[0]) == HdlcObjectType.ACTION_RES) {
				} else if (getDlmsApdu() == XDLMS_APDU.ACTION_RESPONSE || getDlmsApdu() == XDLMS_APDU.DED_ACTION_RESPONSE || getDlmsApdu() == XDLMS_APDU.GLO_ACTION_RESPONSE) {
					if (getDlmsApdu() == XDLMS_APDU.DED_ACTION_RESPONSE || getDlmsApdu() == XDLMS_APDU.GLO_ACTION_RESPONSE) {
						byte[] dedLength = new byte[1];
						System.arraycopy(information, infoPos, dedLength, 0, dedLength.length);
						infoPos += dedLength.length;

						byte[] SC = new byte[1];
						System.arraycopy(information, infoPos, SC, 0, SC.length);
						infoPos += SC.length;

						byte[] IC = new byte[4];
						System.arraycopy(information, infoPos, IC, 0, IC.length);
						infoPos += IC.length;

						byte[] cipherText = new byte[DataUtil.getIntToByte(dedLength[0]) - 5]; // 6?
						System.arraycopy(information, infoPos, cipherText, 0, cipherText.length);
						infoPos += cipherText.length;

						HLSAuthForIESCO dedGetReqAuth = new HLSAuthForIESCO(HLSSecurityControl.getItem(SC[0]), getMeterId());
						byte[] plainText = dedGetReqAuth.doDecryption(IC, aareRespondingAPtitle, cipherText);

						information = plainText;
						infoPos = 0;
						
						byte[] plainTextCommandType = new byte[1];
						System.arraycopy(information, infoPos, plainTextCommandType, 0, plainTextCommandType.length);
						infoPos += plainTextCommandType.length;

						// Command 타입설정
						logger.debug("[DLMS] PLAIN_TEXT_COMMAND_TYPE = [{}]", XDLMS_APDU.getItem(plainTextCommandType[0]));					
					}
					
					/**
					 * Action response Parsing
					 */
					byte[] resonseType = new byte[1];
					System.arraycopy(information, infoPos, resonseType, 0, resonseType.length);
					infoPos += resonseType.length;
					logger.debug("[DLMS] ACTION-Response = [{}]", ActionResponse.getItem(resonseType[0]).name());

					switch (ActionResponse.getItem(resonseType[0])) {
					case NORMAL:
						byte[] idProperty = new byte[1];
						System.arraycopy(information, infoPos, idProperty, 0, idProperty.length);
						infoPos += idProperty.length;
						logger.debug("[DLMS] Invoke-Id-And-Priority = [{}]", Hex.decode(idProperty));

						byte[] actionResult = new byte[1];
						System.arraycopy(information, infoPos, actionResult, 0, actionResult.length);
						infoPos += actionResult.length;
						ActionResult aResult = ActionResult.getItem(actionResult[0]);
						logger.debug("[DLMS] Action-Result = [{}]", aResult.name());

						// 결과 저장
						setResultData(aResult);

						if (aResult == ActionResult.SUCCESS) { // 성공
							if (procedure == Procedure.HDLC_ASSOCIATION_LN) {
								byte[] getDataResult = new byte[1];
								System.arraycopy(information, infoPos, getDataResult, 0, getDataResult.length);
								infoPos += getDataResult.length;
								logger.debug("[DLMS] Get-Data-Result = [{}]", Hex.decode(getDataResult));

								byte[] data = new byte[1];
								System.arraycopy(information, infoPos, data, 0, data.length);
								infoPos += data.length;
								logger.debug("[DLMS] Data = [{}]", Hex.decode(data));

								byte[] octetString = new byte[1];
								System.arraycopy(information, infoPos, octetString, 0, octetString.length);
								infoPos += octetString.length;
								logger.debug("[DLMS] OCTET_STRING = [{}]", Hex.decode(octetString));

								byte[] length = new byte[1];
								System.arraycopy(information, infoPos, length, 0, length.length);
								infoPos += length.length;
								logger.debug("[DLMS] LENGTH = [{}]", Hex.decode(length));

								byte[] securityControlByte = new byte[1];
								System.arraycopy(information, infoPos, securityControlByte, 0, securityControlByte.length);
								infoPos += securityControlByte.length;
								logger.debug("[DLMS] SECURITY_CONTROL = [{}]", Hex.decode(securityControlByte));

								byte[] invocationCounter = new byte[4];
								System.arraycopy(information, infoPos, invocationCounter, 0, invocationCounter.length);
								infoPos += invocationCounter.length;
								logger.debug("[DLMS] INVOCATION_COUNTER = [{}]", Hex.decode(invocationCounter));

								byte[] tagValue = new byte[12];
								System.arraycopy(information, infoPos, tagValue, 0, tagValue.length);
								infoPos += tagValue.length;
								logger.debug("[DLMS] TAG_VALUE = [{}]", Hex.decode(tagValue));

								/**
								 * Validation
								 */
								HLSAuthForIESCO auth = new HLSAuthForIESCO(HLSSecurityControl.getItem(securityControlByte[0]), getMeterId());
								result = auth.doValidation(aareRespondingAPtitle, invocationCounter, aarqSendingCtoS, tagValue);
								if (!result) {
//									logger.debug("[ActionResponse Validation Fail~!! but Skip.");
//									logger.debug("[ActionResponse Validation Fail~!! but Skip.");
//									logger.debug("[ActionResponse Validation Fail~!! but Skip.");
//									logger.debug("[ActionResponse Validation Fail~!! but Skip.");

									logger.debug("[ActionResponse Validation Fail~!!");
									logger.debug("[ActionResponse Validation Fail~!!");
									logger.debug("[ActionResponse Validation Fail~!!");
									logger.debug("[ActionResponse Validation Fail~!!");
									result = false;
								} else {
									logger.debug("[ActionResponse Validation Success~!!");
									result = true;
								}
							} else if (procedure == Procedure.ACTION_IMAGE_TRANSFER_INIT) {
								byte[] getDataResult = new byte[1];
								System.arraycopy(information, infoPos, getDataResult, 0, getDataResult.length);
								infoPos += getDataResult.length;
								logger.debug("[DLMS] Get-Data-Result = [{}]", Hex.decode(getDataResult));
							} else if (procedure == Procedure.ACTION_IMAGE_BLOCK_TRANSFER) {
								byte[] getDataResult = new byte[1];
								System.arraycopy(information, infoPos, getDataResult, 0, getDataResult.length);
								infoPos += getDataResult.length;
								logger.debug("[DLMS] Get-Data-Result = [{}]", Hex.decode(getDataResult));
							} else if (procedure == Procedure.ACTION_IMAGE_VERIFY) {
								byte[] getDataResult = new byte[1];
								System.arraycopy(information, infoPos, getDataResult, 0, getDataResult.length);
								infoPos += getDataResult.length;
								logger.debug("[DLMS] Get-Data-Result = [{}]", Hex.decode(getDataResult));
							} else if (procedure == Procedure.ACTION_IMAGE_ACTIVATE) {
								byte[] getDataResult = new byte[1];
								System.arraycopy(information, infoPos, getDataResult, 0, getDataResult.length);
								infoPos += getDataResult.length;
								logger.debug("[DLMS] Get-Data-Result = [{}]", Hex.decode(getDataResult));
							} else if (procedure == Procedure.ACTION_METER_ALARM_RESET) {
								byte[] getDataResult = new byte[1];
								System.arraycopy(information, infoPos, getDataResult, 0, getDataResult.length);
								infoPos += getDataResult.length;
								logger.debug("[DLMS] Get-Data-Result = [{}]", Hex.decode(getDataResult));
							} else if ( procedure == Procedure.ACTION_DISCONNECT_CONTROL ){
								byte[] getDataResult = new byte[1];
								System.arraycopy(information, infoPos, getDataResult, 0, getDataResult.length);
								infoPos += getDataResult.length;
								HashMap<String, Object> r = new HashMap<String, Object>();
								r.put("status", aResult);
								r.put("value", CONTROL_STATE.getValue(DataUtil.getIntToByte(getDataResult[0])));
								setResultData(r);
								logger.debug("[DLMS] Get-Data-Result = [{}]", Hex.decode(getDataResult));
							} else if (procedure == Procedure.ACTION_SLAVE_INSTALL ) {
								byte[] getDataResult = new byte[1];
								System.arraycopy(information, infoPos, getDataResult, 0, getDataResult.length);
								infoPos += getDataResult.length;
								logger.debug("[DLMS] Get-Data-Result = [{}]", Hex.decode(getDataResult));
							} else if (procedure == Procedure.ACTION_SLAVE_DEINSTALL ) {
								byte[] getDataResult = new byte[1];
								System.arraycopy(information, infoPos, getDataResult, 0, getDataResult.length);
								infoPos += getDataResult.length;
								logger.debug("[DLMS] Get-Data-Result = [{}]", Hex.decode(getDataResult));
							} else if (procedure == Procedure.ACTION_SET_ENCRYPTION_KEY ) {
								byte[] getDataResult = new byte[1];
								System.arraycopy(information, infoPos, getDataResult, 0, getDataResult.length);
								infoPos += getDataResult.length;
								logger.debug("[DLMS] Get-Data-Result = [{}]", Hex.decode(getDataResult));
							} else if (procedure == Procedure.ACTION_TRANSFER_KEY ) {
								byte[] getDataResult = new byte[1];
								System.arraycopy(information, infoPos, getDataResult, 0, getDataResult.length);
								infoPos += getDataResult.length;
								logger.debug("[DLMS] Get-Data-Result = [{}]", Hex.decode(getDataResult));
							}else if (procedure == Procedure.GET_IMAGE_FIRST_NOT_TRANSFERRED_BLOCK_NUMBER ) {
								logger.warn("### HES received Inadequate DLMS Packet. This Procedure is [GET_IMAGE_FIRST_NOT_TRANSFERRED_BLOCK_NUMBER]");
								logger.warn("### HES received Inadequate DLMS Packet. This Procedure is [GET_IMAGE_FIRST_NOT_TRANSFERRED_BLOCK_NUMBER]");
								logger.warn("### HES received Inadequate DLMS Packet. This Procedure is [GET_IMAGE_FIRST_NOT_TRANSFERRED_BLOCK_NUMBER]");
							}
							else {
								logger.error("### [{}] Inadequate DLMS Packet.", procedure.name());
								logger.error("### [{}] Inadequate DLMS Packet.", procedure.name());
								logger.error("### [{}] Inadequate DLMS Packet.", procedure.name());
								result = false;
							}

						} else {
							logger.debug("[DLMS] Action-Result Fail = [{}]", aResult.name());
							//throw new Exception("[ACTION_RES] Action Result = " + Hex.decode(actionResult) + " - Fail");
						}
						break;
					case WITH_PBLOCK:
						/*
						 * 추후 필요시 구현
						 */
						break;
					case WITH_LIST:
						/*
						 * 추후 필요시 구현
						 */
						break;
					case NEXT_PBLOCK:
						/*
						 * 추후 필요시 구현
						 */
						break;
					default:
						break;
					}

					//					result = true;
				//} else if (HdlcObjectType.getItem(commandType[0]) == HdlcObjectType.GET_RES) {
				} else if (getDlmsApdu() == XDLMS_APDU.GET_RESPONSE || getDlmsApdu() == XDLMS_APDU.DED_GET_RESPONSE || getDlmsApdu() == XDLMS_APDU.GLO_GET_RESPONSE) {
					if (getDlmsApdu() == XDLMS_APDU.DED_GET_RESPONSE || getDlmsApdu() == XDLMS_APDU.GLO_GET_RESPONSE) {
						byte[] dedLength = new byte[1];
						System.arraycopy(information, infoPos, dedLength, 0, dedLength.length);
						infoPos += dedLength.length;

						byte[] SC = new byte[1];
						System.arraycopy(information, infoPos, SC, 0, SC.length);
						infoPos += SC.length;

						byte[] IC = new byte[4];
						System.arraycopy(information, infoPos, IC, 0, IC.length);
						infoPos += IC.length;

						byte[] cipherText = new byte[DataUtil.getIntToByte(dedLength[0]) - 5]; // 6?
						System.arraycopy(information, infoPos, cipherText, 0, cipherText.length);
						infoPos += cipherText.length;

						HLSAuthForIESCO dedGetReqAuth = new HLSAuthForIESCO(HLSSecurityControl.getItem(SC[0]), getMeterId());
						byte[] plainText = dedGetReqAuth.doDecryption(IC, aareRespondingAPtitle, cipherText);

						information = plainText;
						infoPos = 0;
						
						byte[] plainTextCommandType = new byte[1];
						System.arraycopy(information, infoPos, plainTextCommandType, 0, plainTextCommandType.length);
						infoPos += plainTextCommandType.length;

						// Command 타입설정
						logger.debug("[DLMS] PLAIN_TEXT_COMMAND_TYPE = [{}]", XDLMS_APDU.getItem(plainTextCommandType[0]));			
					}
					
					/**
					 * Action response Parsing
					 */
					byte[] getResponseType = new byte[1];
					System.arraycopy(information, infoPos, getResponseType, 0, getResponseType.length);
					infoPos += getResponseType.length;
					logger.debug("[DLMS] GET-Response = [{}]", GetResponse.getItem(getResponseType[0]).name());

					switch (GetResponse.getItem(getResponseType[0])) {
					case NORMAL:
						byte[] idProperty = new byte[1];
						System.arraycopy(information, infoPos, idProperty, 0, idProperty.length);
						infoPos += idProperty.length;
						logger.debug("[DLMS] invoke-id-and-priority = [{}]", Hex.decode(idProperty));

						byte[] getDataResult = new byte[1];
						System.arraycopy(information, infoPos, getDataResult, 0, getDataResult.length);
						infoPos += getDataResult.length;
						GetDataResult aResult = GetDataResult.getItem(getDataResult[0]);
						logger.debug("[DLMS] Get-Data-Result = [{}]", aResult.name());

						if (aResult == GetDataResult.DATA) {
							lpRawData = new byte[information.length - infoPos];
							System.arraycopy(information, infoPos, lpRawData, 0, lpRawData.length);
							
							byte[] getDataResultType = new byte[1];
							System.arraycopy(information, infoPos, getDataResultType, 0, getDataResultType.length);
							infoPos += getDataResultType.length;

							logger.debug("[DLMS] Get-Data-Result-type = [{}]", DLMSCommonDataType.getItem(getDataResultType[0]).name());
							byte[] data = null;
							switch (DLMSCommonDataType.getItem(getDataResultType[0])) {
							case Boolean:
								data = new byte[DLMSCommonDataType.Boolean.getLenth()];
								System.arraycopy(information, infoPos, data, 0, data.length);
								infoPos += data.length;
								boolean getResult = Boolean.valueOf(String.valueOf(DlmsConstantsForIESCO.getValueByDLMSCommonDataType(DLMSCommonDataType.Boolean, data)));
								setResultData(getResult);

								logger.debug("[DLMS] Get-Data-Result-type-data = [{}]", getResult);
								break;
							case FLOAT32:
								data = new byte[DLMSCommonDataType.FLOAT32.getLenth()];
								System.arraycopy(information, infoPos, data, 0, data.length);
								infoPos += data.length;
								float floatResult = Float.parseFloat(String.valueOf(DlmsConstantsForIESCO.getValueByDLMSCommonDataType(DLMSCommonDataType.FLOAT32, data)));
								setResultData(floatResult);

								logger.debug("[DLMS] Get-Data-Result-type-data = [{}]", floatResult);

								break;	
							case UINT8:
								data = new byte[DLMSCommonDataType.UINT8.getLenth()];
								System.arraycopy(information, infoPos, data, 0, data.length);
								infoPos += data.length;
								long uint8Result = Long.parseLong(String.valueOf(DlmsConstantsForIESCO.getValueByDLMSCommonDataType(DLMSCommonDataType.UINT8, data)));
								setResultData(uint8Result);

								logger.debug("[DLMS] Get-Data-Result-type-data = [{}]", uint8Result);

								break;
							case INT8:
								data = new byte[DLMSCommonDataType.INT8.getLenth()];
								System.arraycopy(information, infoPos, data, 0, data.length);
								infoPos += data.length;
								long int8Result = Long.parseLong(String.valueOf(DlmsConstantsForIESCO.getValueByDLMSCommonDataType(DLMSCommonDataType.INT8, data)));
								setResultData(int8Result);

								logger.debug("[DLMS] Get-Data-Result-type-data = [{}]", int8Result);

								break;	
							case UINT16:
								data = new byte[DLMSCommonDataType.UINT16.getLenth()];
								System.arraycopy(information, infoPos, data, 0, data.length);
								infoPos += data.length;
								long uint16Result = Long.parseLong(String.valueOf(DlmsConstantsForIESCO.getValueByDLMSCommonDataType(DLMSCommonDataType.UINT16, data)));
								setResultData(uint16Result);

								logger.debug("[DLMS] Get-Data-Result-type-data = [{}]", uint16Result);
								break;
							case INT16:
								data = new byte[DLMSCommonDataType.INT16.getLenth()];
								System.arraycopy(information, infoPos, data, 0, data.length);
								infoPos += data.length;
								long int16Result = Long.parseLong(String.valueOf(DlmsConstantsForIESCO.getValueByDLMSCommonDataType(DLMSCommonDataType.INT16, data)));
								setResultData(int16Result);

								logger.debug("[DLMS] Get-Data-Result-type-data = [{}]", int16Result);
								break;								
							case UINT32:
								data = new byte[DLMSCommonDataType.UINT32.getLenth()];
								System.arraycopy(information, infoPos, data, 0, data.length);
								infoPos += data.length;
								long longResult = Long.parseLong(String.valueOf(DlmsConstantsForIESCO.getValueByDLMSCommonDataType(DLMSCommonDataType.UINT32, data)));
								setResultData(longResult);

								logger.debug("[DLMS] Get-Data-Result-type-data = [{}]", longResult);
								break;
							case INT32:
								data = new byte[DLMSCommonDataType.INT32.getLenth()];
								System.arraycopy(information, infoPos, data, 0, data.length);
								infoPos += data.length;
								long int32Result = Long.parseLong(String.valueOf(DlmsConstantsForIESCO.getValueByDLMSCommonDataType(DLMSCommonDataType.INT32, data)));
								setResultData(int32Result);

								logger.debug("[DLMS] Get-Data-Result-type-data = [{}]", int32Result);
								break;								
							case Enum:
								data = new byte[DLMSCommonDataType.Enum.getLenth()];
								System.arraycopy(information, infoPos, data, 0, data.length);
								infoPos += data.length;

								setResultData(DataUtil.getIntToBytes(data));

								logger.debug("[DLMS] Get-Data-Result-type-data = [{}]", DataUtil.getIntToBytes(data));
								break;
							case OctetString:
								if (procedure == Procedure.GET_METER_TIME) {
									byte[] octetLength = new byte[1];
									System.arraycopy(information, infoPos, octetLength, 0, octetLength.length);
									infoPos += octetLength.length;

									byte[] year = new byte[2];
									System.arraycopy(information, infoPos, year, 0, year.length);
									infoPos += year.length;

									byte[] month = new byte[1];
									System.arraycopy(information, infoPos, month, 0, month.length);
									infoPos += month.length;

									byte[] dayOfMonth = new byte[1];
									System.arraycopy(information, infoPos, dayOfMonth, 0, dayOfMonth.length);
									infoPos += dayOfMonth.length;

									byte[] dayOfWeek = new byte[1];
									System.arraycopy(information, infoPos, dayOfWeek, 0, dayOfWeek.length);
									infoPos += dayOfWeek.length;

									byte[] hour = new byte[1];
									System.arraycopy(information, infoPos, hour, 0, hour.length);
									infoPos += hour.length;

									byte[] minute = new byte[1];
									System.arraycopy(information, infoPos, minute, 0, minute.length);
									infoPos += minute.length;

									byte[] second = new byte[1];
									System.arraycopy(information, infoPos, second, 0, second.length);
									infoPos += second.length;

									byte[] hundredthsOfSecond = new byte[1];
									System.arraycopy(information, infoPos, hundredthsOfSecond, 0, hundredthsOfSecond.length);
									infoPos += hundredthsOfSecond.length;

									byte[] deviation = new byte[2];
									System.arraycopy(information, infoPos, deviation, 0, deviation.length);
									infoPos += deviation.length;

									byte[] clockStatus = new byte[1];
									System.arraycopy(information, infoPos, clockStatus, 0, clockStatus.length);
									infoPos += clockStatus.length;

									String dayOfWeekStr = "";
									switch (dayOfWeek[0]) {
									case 1:
										dayOfWeekStr = "Mon";
										break;
									case 2:
										dayOfWeekStr = "The";
										break;
									case 3:
										dayOfWeekStr = "Wed";
										break;
									case 4:
										dayOfWeekStr = "Thu";
										break;
									case 5:
										dayOfWeekStr = "Fri";
										break;
									case 6:
										dayOfWeekStr = "Sat";
										break;
									case 7:
										dayOfWeekStr = "Sun";
										break;
									default:
										break;
									}
									
									String daylight=null;
									if((clockStatus[0] & 0x80) == 0x80) {
										daylight = "true";
									} else if((clockStatus[0] & 0x80) == 0x00) {
										daylight = "false";
									}

									String date = String.format("%04d", DataUtil.getIntTo2Byte(year)) + "/" + String.format("%02d", DataUtil.getIntToByte(month[0])) + "/" + String.format("%02d", DataUtil.getIntToByte(dayOfMonth[0])) + "(" + dayOfWeekStr + ")";
									String time = String.format("%02d", DataUtil.getIntToByte(hour[0])) + ":" + String.format("%02d", DataUtil.getIntToByte(minute[0])) + ":" + String.format("%02d", DataUtil.getIntToByte(second[0]));
									HashMap<String, String> r = new HashMap<String, String>();
									r.put("date", date);
									r.put("time", time);
									r.put("daylight", daylight);

									setResultData(r);

									logger.debug("[DLMS] Get-Data-Result-type-data = [{}]", r.toString());
								} else {
									byte[] octetLength = new byte[1];
									System.arraycopy(information, infoPos, octetLength, 0, octetLength.length);
									infoPos += octetLength.length;

									data = new byte[DataUtil.getIntToByte(octetLength[0])];
									System.arraycopy(information, infoPos, data, 0, data.length);
									infoPos += data.length;
									//setResultData(DataUtil.getString(data));
									//logger.debug("[DLMS] Get-Data-Result-type-data = [{}]", DataUtil.getString(data));
									String dataStr = DataUtil.getString(data);
									if ( dataStr.matches("\\p{Print}*") ){
										setResultData(dataStr);
										logger.debug("[DLMS] Get-Data-Result-type-data = [{}]", dataStr);
									}
									else {
										setResultData(Hex.decode(data));
										logger.debug("[DLMS] Get-Data-Result-type-data = [{}]", Hex.decode(data));
									}
								}
								break;
							case Array:
								/*
								 *  array image_to_activate_info_element
									image_to_activate_info_element ::= structure
									{
										image_to_activate_size: double-long-unsigned,
										image_to_activate_identification: octet-string,
										image_to_activate_signature: octet-string
									} 
								 */
								if (procedure == Procedure.GET_IMAGE_TO_ACTIVATE_INFO) {
									byte[] arrayLength = new byte[1];
									System.arraycopy(information, infoPos, arrayLength, 0, arrayLength.length);
									infoPos += arrayLength.length;

									byte[] structure = new byte[1];
									System.arraycopy(information, infoPos, structure, 0, structure.length);
									infoPos += structure.length;

									byte[] structureLength = new byte[1];
									System.arraycopy(information, infoPos, structureLength, 0, structureLength.length);
									infoPos += structureLength.length;

									byte[] eltype = new byte[1];
									System.arraycopy(information, infoPos, eltype, 0, eltype.length);
									infoPos += eltype.length;

									byte[] image_to_activate_sizeData = new byte[DLMSCommonDataType.UINT32.getLenth()];
									System.arraycopy(information, infoPos, image_to_activate_sizeData, 0, image_to_activate_sizeData.length);
									infoPos += image_to_activate_sizeData.length;
									long image_to_activate_size = Long.parseLong(String.valueOf(DlmsConstantsForIESCO.getValueByDLMSCommonDataType(DLMSCommonDataType.UINT32, image_to_activate_sizeData)));
									logger.debug("[DLMS] Get-Data-Result-type-data = [{}]", image_to_activate_size);

									byte[] eltype2 = new byte[1];
									System.arraycopy(information, infoPos, eltype2, 0, eltype2.length);
									infoPos += eltype2.length;

									byte[] eltype2Length = new byte[1];
									System.arraycopy(information, infoPos, eltype2Length, 0, eltype2Length.length);
									infoPos += eltype2Length.length;

									byte[] image_to_activate_identificationData = new byte[DataUtil.getIntToByte(eltype2Length[0])];
									System.arraycopy(information, infoPos, image_to_activate_identificationData, 0, image_to_activate_identificationData.length);
									infoPos += image_to_activate_identificationData.length;
									logger.debug("[DLMS] Get-Data-Result-type-data = [{}]", Hex.decode(image_to_activate_identificationData));

									byte[] eltype3 = new byte[1];
									System.arraycopy(information, infoPos, eltype3, 0, eltype3.length);
									infoPos += eltype3.length;

									byte[] eltype3Length = new byte[1];
									System.arraycopy(information, infoPos, eltype3Length, 0, eltype3Length.length);
									infoPos += eltype3Length.length;

									HashMap<String, Object> resultDataMap = new HashMap<String, Object>();
									resultDataMap.put("image_to_activate_size", image_to_activate_size);
									resultDataMap.put("image_to_activate_identification", image_to_activate_identificationData);

									// image_to_activate_signature 가 없는경우도 있는것 같음.
									if (0 < DataUtil.getIntToByte(eltype3Length[0])) {
										byte[] image_to_activate_signatureData = new byte[DataUtil.getIntToByte(eltype3Length[0])];
										System.arraycopy(information, infoPos, image_to_activate_signatureData, 0, image_to_activate_signatureData.length);
										infoPos += image_to_activate_signatureData.length;
										logger.debug("[DLMS] Get-Data-Result-type-data = [{}]", Hex.decode(image_to_activate_signatureData));

										resultDataMap.put("image_to_activate_signature", image_to_activate_signatureData);
									}

									setResultData(resultDataMap);
								} else if (procedure == Procedure.GET_BILLING_CYCLE) {
									byte[] arrayLength = new byte[1];
									System.arraycopy(information, infoPos, arrayLength, 0, arrayLength.length);
									infoPos += arrayLength.length;

									byte[] structure = new byte[1];
									System.arraycopy(information, infoPos, structure, 0, structure.length);
									infoPos += structure.length;

									byte[] structureLength = new byte[1];
									System.arraycopy(information, infoPos, structureLength, 0, structureLength.length);
									infoPos += structureLength.length;

									byte[] dataResultType1 = new byte[1];
									System.arraycopy(information, infoPos, dataResultType1, 0, dataResultType1.length);
									infoPos += dataResultType1.length;

									byte[] oLength1 = new byte[1];
									System.arraycopy(information, infoPos, oLength1, 0, oLength1.length);
									infoPos += oLength1.length;

									byte[] time = new byte[DataUtil.getIntToByte(oLength1[0])];
									System.arraycopy(information, infoPos, time, 0, time.length);
									infoPos += time.length;

									byte[] dataResultType2 = new byte[1];
									System.arraycopy(information, infoPos, dataResultType2, 0, dataResultType2.length);
									infoPos += dataResultType2.length;

									byte[] oLength2 = new byte[1];
									System.arraycopy(information, infoPos, oLength2, 0, oLength2.length);
									infoPos += oLength2.length;

									byte[] day = new byte[DataUtil.getIntToByte(oLength2[0])];
									System.arraycopy(information, infoPos, day, 0, day.length);
									infoPos += day.length;

									String times = String.format("%02d", DataUtil.getIntToByte(time[0])) + ":" + String.format("%02d", DataUtil.getIntToByte(time[1])) + ":" + String.format("%02d", DataUtil.getIntToByte(time[2]));
									int days = DataUtil.getIntToByte(day[3]);

									HashMap<String, String> r = new HashMap<String, String>();
									r.put("time", times);
									r.put("day", String.format("%02d", days));

									setResultData(r);

									logger.debug("[DLMS] Get-Data-Result-type-data = [{}]", r.toString());
								} else if (procedure == Procedure.GET_PROFILE_OBJECT) {
									List<HashMap<String, Object>> returnList = new ArrayList<HashMap<String, Object>>();
									channelList = new ArrayList<HashMap<String, Object>>();

									byte[] arraySize = new byte[1];
									System.arraycopy(information, infoPos, arraySize, 0, arraySize.length);
									infoPos += arraySize.length;

									logger.debug("[DLMS] Get-Data-Result-type-size = [{}]", DataUtil.getIntToBytes(arraySize));

									for (int i = 0; i < DataUtil.getIntToBytes(arraySize); i++) {
										byte[] structure = new byte[1];
										System.arraycopy(information, infoPos, structure, 0, structure.length);
										infoPos += structure.length;

										byte[] structureSize = new byte[1];
										System.arraycopy(information, infoPos, structureSize, 0, structureSize.length);
										infoPos += structureSize.length;

										byte[] classSize = new byte[1];
										System.arraycopy(information, infoPos, classSize, 0, classSize.length);
										infoPos += classSize.length;

										byte[] classId = new byte[2];
										System.arraycopy(information, infoPos, classId, 0, classId.length);
										infoPos += classId.length;

										byte[] obisType = new byte[1];
										System.arraycopy(information, infoPos, obisType, 0, obisType.length);
										infoPos += obisType.length;

										byte[] obisSize = new byte[1];
										System.arraycopy(information, infoPos, obisSize, 0, obisSize.length);
										infoPos += obisSize.length;

										byte[] obisCode = new byte[6];
										System.arraycopy(information, infoPos, obisCode, 0, obisCode.length);
										infoPos += obisCode.length;

										byte[] attributeType = new byte[1];
										System.arraycopy(information, infoPos, attributeType, 0, attributeType.length);
										infoPos += attributeType.length;

										byte[] attribute = new byte[1];
										System.arraycopy(information, infoPos, attribute, 0, attribute.length);
										infoPos += attribute.length;

										byte[] longType = new byte[1];
										System.arraycopy(information, infoPos, longType, 0, longType.length);
										infoPos += longType.length;

										byte[] longData = new byte[2];
										System.arraycopy(information, infoPos, longData, 0, longData.length);
										infoPos += longData.length;

										HashMap<String, Object> map = new HashMap<String, Object>();
										map.put("classId", DataUtil.getIntTo2Byte(classId));
										map.put("obisCode", Hex.decode(obisCode));
										map.put("attribute", DataUtil.getIntToByte(attribute[0]));
										map.put("longData", longData);
										returnList.add(map);
									}

									channelList = returnList;
									setResultData(returnList);

									logger.debug("[DLMS] Get-Data-Result-type-data = [{}]", returnList.toString());
								} else if (procedure == Procedure.GET_PROFILE_BUFFER) {
									
									logger.debug("###################  GET_PROFILE_BUFFER - NORMAL ###############");


									HashMap<String, Object> tempParamMap = new HashMap<String, Object>();
									tempParamMap.put("isBlock", false);
									tempParamMap.put("isLast", true);
									tempParamMap.put("rawData", lpRawData);
									
									setResultData(tempParamMap);
								} else if (procedure == Procedure.GET_VALUE) {
									List<HashMap<String, Object>> returnList = new ArrayList<HashMap<String, Object>>();
								
									logger.debug("###################  GET_VALUE - NORMAL ###############");

									byte[] arraySize = new byte[1];
									System.arraycopy(information, infoPos, arraySize, 0, arraySize.length);
									infoPos += arraySize.length;

									logger.debug("[DLMS] Get-Data-Result-type-size = [{}]", DataUtil.getIntToBytes(arraySize));

									for (int i = 0; i < DataUtil.getIntToBytes(arraySize); i++) {
										byte[] structure = new byte[1];
										System.arraycopy(information, infoPos, structure, 0, structure.length);
										infoPos += structure.length;

										byte[] structureSize = new byte[1];
										System.arraycopy(information, infoPos, structureSize, 0, structureSize.length);
										infoPos += structureSize.length;

										logger.debug("[DLMS] Get-Data-Result-type-size = [{}]", DataUtil.getIntToBytes(structureSize));
										HashMap<String, Object> map = new HashMap<String, Object>();
										for (int j = 0; j < DataUtil.getIntToBytes(structureSize); j++) {
											byte[] valType = new byte[1];
											System.arraycopy(information, infoPos, valType, 0, valType.length);
											infoPos += valType.length;
	
											byte[] valSize = new byte[1];
											System.arraycopy(information, infoPos, valSize, 0, valSize.length);
											infoPos += valSize.length;
	
											byte[] val = new byte[DataUtil.getIntToBytes(valSize)];
											System.arraycopy(information, infoPos, val, 0, val.length);
											infoPos += val.length;	
											map.put(String.valueOf(j), Hex.decode(val));
										}
										returnList.add(map);
									}								
									setResultData(returnList);

									logger.debug("[DLMS] Get-Data-Result-type-data = [{}]", returnList.toString());
								}
								break;
							case Structure:
								if (procedure == Procedure.GET_REGISTER_UNIT) {
									byte[] arrayLength = new byte[1];
									System.arraycopy(information, infoPos, arrayLength, 0, arrayLength.length);
									infoPos += arrayLength.length;
									
									byte[] integerLen = new byte[1];
									System.arraycopy(information, infoPos, integerLen, 0, integerLen.length);
									infoPos += integerLen.length;

									byte[] scaler = new byte[DLMSCommonDataType.INT8.getLenth()];
									System.arraycopy(information, infoPos, scaler, 0, scaler.length);
									infoPos += scaler.length;

									byte[] enumLen = new byte[1];
									System.arraycopy(information, infoPos, enumLen, 0, enumLen.length);
									infoPos += enumLen.length;

									byte[] unit = new byte[DLMSCommonDataType.Enum.getLenth()];
									System.arraycopy(information, infoPos, unit, 0, unit.length);
									infoPos += unit.length;

									HashMap<String, Object> r = new HashMap<String, Object>();
									r.put("scaler", DataUtil.getIntToByte(scaler[0]));
									r.put("unit", DataUtil.getIntToByte(unit[0]));

									setResultData(r);

									logger.debug("[DLMS] Get-Data-Result-type-data = [{}]", r.toString());

								}
								break;
							default:
								break;
							}

						} else if (aResult == GetDataResult.DATA_ACCESS_RESULT) {
							byte[] getDataAccessResult = new byte[1];
							System.arraycopy(information, infoPos, getDataAccessResult, 0, getDataAccessResult.length);
							infoPos += getDataAccessResult.length;
							setResultData(DataAccessResult.getItem(getDataAccessResult[0]));
							logger.debug("[DLMS] Get-Data-Access-Result = [{}]", DataAccessResult.getItem(getDataAccessResult[0]).name());
						}

						break;
					case WITH_DATABLOCK:
						/*
						 * 추후 필요시 구현
						 */
						byte[] idPropertyB = new byte[1];
						System.arraycopy(information, infoPos, idPropertyB, 0, idPropertyB.length);
						infoPos += idPropertyB.length;
						logger.debug("[DLMS] invoke-id-and-priority = [{}]", Hex.decode(idPropertyB));

						byte[] lastBlock = new byte[1];
						System.arraycopy(information, infoPos, lastBlock, 0, lastBlock.length);
						infoPos += lastBlock.length;
						logger.debug("[DLMS] last-block = [{}]", lastBlock[0] == 0 ? "FALSE" : "TRUE");

						byte[] blockNumber = new byte[4];
						System.arraycopy(information, infoPos, blockNumber, 0, blockNumber.length);
						infoPos += blockNumber.length;
						logger.debug("[DLMS] block-number = [{}]", DataUtil.getIntTo4Byte(blockNumber));

						byte[] getResultChoice = new byte[1];
						System.arraycopy(information, infoPos, getResultChoice, 0, getResultChoice.length);
						infoPos += getResultChoice.length;
						aResult = GetDataResult.getItem(getResultChoice[0]);
						logger.debug("[DLMS] Get-Data-Result = [{}]", aResult.name());

						if (aResult == GetDataResult.DATA) {
							/*
							 * Block Length 구하기 : 사실 이 로직에서 길이는 별의미가 없음 단지 infoPos를 늘리기위함.
							 */
							byte[] length = new byte[1];
							System.arraycopy(information, infoPos, length, 0, length.length);
							infoPos += length.length;

							byte[] byteLength = null;
							if ((length[0] & 0x80) == 0x80) {
								byteLength = new byte[(length[0] & 0x7F)];
								System.arraycopy(information, infoPos, byteLength, 0, byteLength.length);
								infoPos += byteLength.length;
								logger.debug("[DLMS] this block byte Length= [{}]", Hex.decode(byteLength));

								length = byteLength;
							}

							// 
							lpRawData = new byte[information.length - infoPos];
							System.arraycopy(information, infoPos, lpRawData, 0, lpRawData.length);

							//List<HashMap<String, Object>> returnList = new ArrayList<HashMap<String, Object>>();
							Map<String, Object> tempParamMap = new HashMap<String, Object>();
							tempParamMap.put("isBlock", true);
							tempParamMap.put("blockNumber", DataUtil.getIntTo4Byte(blockNumber));
							tempParamMap.put("isLast", lastBlock[0] == 0 ? false : true);
							//tempParamMap.put("dataSize", dataList == null ? 0 : dataList.size());							
							tempParamMap.put("rawData", lpRawData);

							setResultData(tempParamMap);
							logger.debug("[DLMS] Get-Data-Result-data-info = [{}]", tempParamMap.toString());

							
							byte[] getDataResultType = new byte[1];
							System.arraycopy(information, infoPos, getDataResultType, 0, getDataResultType.length);
							infoPos += getDataResultType.length;

							logger.debug("### GET-RESPONSE-WITH-DATABLOCK - RAW Data 저장 ###");
							
						} else if (aResult == GetDataResult.DATA_ACCESS_RESULT) {
							byte[] getDataAccessResult = new byte[1];
							System.arraycopy(information, infoPos, getDataAccessResult, 0, getDataAccessResult.length);
							infoPos += getDataAccessResult.length;
							setResultData(DataAccessResult.getItem(getDataAccessResult[0]));
							logger.debug("[DLMS] Get-Data-Access-Result = [{}]", DataAccessResult.getItem(getDataAccessResult[0]).name());
						}

						break;
					case WITH_LIST:
						/*
						 * 추후 필요시 구현
						 */
						break;
					default:
						break;
					}

					//					result = true;
				//} else if (HdlcObjectType.getItem(commandType[0]) == HdlcObjectType.SET_RES) {
				} else if (getDlmsApdu() == XDLMS_APDU.SET_RESPONSE || getDlmsApdu() == XDLMS_APDU.DED_SET_RESPONSE) {
					if (XDLMS_APDU.getItem(commandType[0]) == XDLMS_APDU.DED_SET_RESPONSE) {
						byte[] dedLength = new byte[1];
						System.arraycopy(information, infoPos, dedLength, 0, dedLength.length);
						infoPos += dedLength.length;

						byte[] SC = new byte[1];
						System.arraycopy(information, infoPos, SC, 0, SC.length);
						infoPos += SC.length;

						byte[] IC = new byte[4];
						System.arraycopy(information, infoPos, IC, 0, IC.length);
						infoPos += IC.length;

						byte[] cipherText = new byte[DataUtil.getIntToByte(dedLength[0]) - 5]; // 6?
						System.arraycopy(information, infoPos, cipherText, 0, cipherText.length);
						infoPos += cipherText.length;

						HLSAuthForIESCO dedGetReqAuth = new HLSAuthForIESCO(HLSSecurityControl.getItem(SC[0]), getMeterId());
						byte[] plainText = dedGetReqAuth.doDecryption(IC, aareRespondingAPtitle, cipherText);

						information = plainText;
						infoPos = 0;
						
						byte[] plainTextCommandType = new byte[1];
						System.arraycopy(information, infoPos, plainTextCommandType, 0, plainTextCommandType.length);
						infoPos += plainTextCommandType.length;

						// Command 타입설정
						logger.debug("[DLMS] PLAIN_TEXT_COMMAND_TYPE = [{}]", XDLMS_APDU.getItem(plainTextCommandType[0]));			
					}
					
					/**
					 * Set response Parsing
					 */
					byte[] resonseType = new byte[1];
					System.arraycopy(information, infoPos, resonseType, 0, resonseType.length);
					infoPos += resonseType.length;
					logger.debug("[DLMS] Set-Response = [{}]", SetResponse.getItem(resonseType[0]).name());

					byte[] idProperty;
					byte[] dataAccessResult;
					DataAccessResult aResult;
					byte[] blockNumber;
					switch (SetResponse.getItem(resonseType[0])) {
					case NORMAL:
						idProperty = new byte[1];
						System.arraycopy(information, infoPos, idProperty, 0, idProperty.length);
						infoPos += idProperty.length;
						logger.debug("[DLMS] Invoke-Id-And-Priority = [{}]", Hex.decode(idProperty));

						dataAccessResult = new byte[1];
						System.arraycopy(information, infoPos, dataAccessResult, 0, dataAccessResult.length);
						infoPos += dataAccessResult.length;
						aResult = DataAccessResult.getItem(dataAccessResult[0]);

						// 결과 저장
						setResultData(aResult);
						logger.debug("[DLMS] Get-Data-Access-Result = [{}]", aResult.name());

						if (aResult != DataAccessResult.SUCCESS) { // 성공이 아닌것.
							result = false;
						}
						break;
					case DATABLOCK:
						idProperty = new byte[1];
						System.arraycopy(information, infoPos, idProperty, 0, idProperty.length);
						infoPos += idProperty.length;
						logger.debug("[DLMS] Invoke-Id-And-Priority = [{}]", Hex.decode(idProperty));

						blockNumber = new byte[4];
						System.arraycopy(information, infoPos, blockNumber, 0, blockNumber.length);
						infoPos += blockNumber.length;
						logger.debug("[DLMS] Block Number = [{}]", DataUtil.getIntTo4Byte(blockNumber));

						// 결과 저장
						setResultData(DataUtil.getIntTo4Byte(blockNumber));
						break;
					case LAST_DATABLOCK:
						idProperty = new byte[1];
						System.arraycopy(information, infoPos, idProperty, 0, idProperty.length);
						infoPos += idProperty.length;
						logger.debug("[DLMS] Invoke-Id-And-Priority = [{}]", Hex.decode(idProperty));

						dataAccessResult = new byte[1];
						System.arraycopy(information, infoPos, dataAccessResult, 0, dataAccessResult.length);
						infoPos += dataAccessResult.length;
						aResult = DataAccessResult.getItem(dataAccessResult[0]);

						// 결과 저장
						setResultData(aResult);
						logger.debug("[DLMS] Get-Data-Access-Result = [{}]", aResult.name());

						if (aResult != DataAccessResult.SUCCESS) { // 성공이 아닌것.
							result = false;
						}

						blockNumber = new byte[4];
						System.arraycopy(information, infoPos, blockNumber, 0, blockNumber.length);
						infoPos += blockNumber.length;
						logger.debug("[DLMS] Block Number = [{}]", DataUtil.getIntTo4Byte(blockNumber));
						break;
					case LAST_DATABLOCK_WITH_LIST:
						/*
						 * 추후 필요시 구현
						 */
						break;
					case WITH_LIST:
						/*
						 * 추후 필요시 구현
						 */
						break;
					default:
						break;
					}
				}else if(getDlmsApdu() == XDLMS_APDU.EXCEPTION_RESPONSE) {
					logger.debug("[DLMS] GET Exception Response DATA = [{}]", Hex.decode(information).trim());
					setResultData(Hex.decode(information).trim());
					logger.debug("[DLMS] COMMAND_TYPE = [{}]", getDlmsApdu().name());
				} else {
					// Kaifa Custom response Parsing
					logger.debug("[DLMS] GET Kaifa Custom DATA = [{}]", Hex.decode(information).trim());
					setResultData(Hex.decode(information).trim());
					
					// Command 타입설정
					//setType(DataUtil.getIntToByte(HdlcObjectType.KAIFA_CUSTOM.getBytes()));
					//logger.debug("[DLMS] COMMAND_TYPE = [{}]", HdlcObjectType.getItem(commandType[0]).name());
					setDlmsApdu(XDLMS_APDU.KAIFA_CUSTOM);
					logger.debug("[DLMS] COMMAND_TYPE = [{}]", getDlmsApdu().name());
				}

						
			} // I Frame parsing close
			else {
				logger.debug("!!!!!!!!!!!!!!!! 여그확인해봐~~");
			} 

		} catch (Exception e) {
			logger.error("DLMS Decoding Error - {}", e);
			result = false;
		}

		return result;
	}

	@Override
	public String toByteString() {
		return Hex.decode(gdDLMSFrame);
	}

	/**
	 * 기존 Decode 방식이 아닌 다른 방식으로 처리해야할경우 사용
	 */
	@Override
	public Object customDecode(Procedure procedure, byte[] data) {
		logger.info("## Excute NestedDLMSDecorator - Custom Decoding...");

		int infoPos = 0;
		byte[] information = data;
		List<HashMap<String, Object>> dataList = new ArrayList<HashMap<String, Object>>();

		byte[] getDataResultType = new byte[1];
		System.arraycopy(information, infoPos, getDataResultType, 0, getDataResultType.length);
		infoPos += getDataResultType.length;

		logger.debug("[DLMS] Get-Data-Result-type = [{}]", DLMSCommonDataType.getItem(getDataResultType[0]).name());
		switch (DLMSCommonDataType.getItem(getDataResultType[0])) {
		case Array:
			if (procedure == Procedure.GET_PROFILE_BUFFER) {
				byte[] arrayLength = new byte[1];
				System.arraycopy(information, infoPos, arrayLength, 0, arrayLength.length);
				infoPos += arrayLength.length;

				int arraySize = 0;
				if ((arrayLength[0] & 0x80) == 0x80) {
					int aSize = arrayLength[0] & 0x7F;
					
					arrayLength = new byte[aSize];
					System.arraycopy(information, infoPos, arrayLength, 0, arrayLength.length);
					infoPos += arrayLength.length;
					
					arraySize = DataUtil.getIntToBytes(arrayLength); 
				}else{
					arraySize = DataUtil.getIntToBytes(arrayLength);
				}
				logger.debug("[DLMS] Get-Data-Result-type-size = " + arraySize);
				
				HashMap<String, Object> dataMap = new HashMap<String, Object>();

				while (information.length != infoPos) {
					byte[] structureType = new byte[1];
					System.arraycopy(information, infoPos, structureType, 0, structureType.length);
					infoPos += structureType.length;

					byte[] structureLen = new byte[1];
					System.arraycopy(information, infoPos, structureLen, 0, structureLen.length);
					infoPos += structureLen.length;

					dataMap = new HashMap<String, Object>();
					for (int i = 0; i < channelList.size(); i++) {
						HashMap<String, Object> tempMap = channelList.get(i);

						if (ObjectType.CLOCK.getValue() == Integer.parseInt(String.valueOf(tempMap.get("classId"))) && "2".equals(String.valueOf(tempMap.get("attribute")))) {
							byte[] octetType = new byte[1];
							System.arraycopy(information, infoPos, octetType, 0, octetType.length);
							infoPos += octetType.length;

							byte[] octetSize = new byte[1];
							System.arraycopy(information, infoPos, octetSize, 0, octetSize.length);
							infoPos += octetSize.length;

							byte[] year = new byte[2];
							System.arraycopy(information, infoPos, year, 0, year.length);
							infoPos += year.length;

							byte[] month = new byte[1];
							System.arraycopy(information, infoPos, month, 0, month.length);
							infoPos += month.length;

							byte[] dayOfMonth = new byte[1];
							System.arraycopy(information, infoPos, dayOfMonth, 0, dayOfMonth.length);
							infoPos += dayOfMonth.length;

							byte[] dayOfWeek = new byte[1];
							System.arraycopy(information, infoPos, dayOfWeek, 0, dayOfWeek.length);
							infoPos += dayOfWeek.length;

							byte[] hour = new byte[1];
							System.arraycopy(information, infoPos, hour, 0, hour.length);
							infoPos += hour.length;

							byte[] minute = new byte[1];
							System.arraycopy(information, infoPos, minute, 0, minute.length);
							infoPos += minute.length;

							byte[] second = new byte[1];
							System.arraycopy(information, infoPos, second, 0, second.length);
							infoPos += second.length;

							byte[] hundredthsOfSecond = new byte[1];
							System.arraycopy(information, infoPos, hundredthsOfSecond, 0, hundredthsOfSecond.length);
							infoPos += hundredthsOfSecond.length;

							byte[] deviation = new byte[2];
							System.arraycopy(information, infoPos, deviation, 0, deviation.length);
							infoPos += deviation.length;

							byte[] clockStatus = new byte[1];
							System.arraycopy(information, infoPos, clockStatus, 0, clockStatus.length);
							infoPos += clockStatus.length;

							String yyyymmdd = String.format("%04d", DataUtil.getIntTo2Byte(year)) + String.format("%02d", DataUtil.getIntToByte(month[0])) + String.format("%02d", DataUtil.getIntToByte(dayOfMonth[0]));
							String yyyymmddhhmmss = yyyymmdd + String.format("%02d", DataUtil.getIntToByte(hour[0])) + String.format("%02d", DataUtil.getIntToByte(minute[0])) + String.format("%02d", DataUtil.getIntToByte(second[0]));

							dataMap.put("yyyymmddhhmmss", yyyymmddhhmmss);
							dataMap.put("yyyymmdd", yyyymmdd);
						} else if ((ObjectType.DATA.getValue() == Integer.parseInt(String.valueOf(tempMap.get("classId"))) 
								|| ObjectType.REGISTER.getValue() == Integer.parseInt(String.valueOf(tempMap.get("classId")))) 
								&& "2".equals(String.valueOf(tempMap.get("attribute")))) {
							byte[] dataType = new byte[1];
							System.arraycopy(information, infoPos, dataType, 0, dataType.length);
							infoPos += dataType.length;

							byte[] dataValue = null;
							switch (DLMSCommonDataType.getItem(dataType[0])) {
							case Null:
								dataValue = null;
								break;
							case UINT8:
								dataValue = new byte[DLMSCommonDataType.UINT8.getLenth()];
								System.arraycopy(information, infoPos, dataValue, 0, dataValue.length);
								infoPos += dataValue.length;
								break;
							case UINT16:
								dataValue = new byte[DLMSCommonDataType.UINT16.getLenth()];
								System.arraycopy(information, infoPos, dataValue, 0, dataValue.length);
								infoPos += dataValue.length;
								break;
							case UINT32:
								dataValue = new byte[DLMSCommonDataType.UINT32.getLenth()];
								System.arraycopy(information, infoPos, dataValue, 0, dataValue.length);
								infoPos += dataValue.length;
								break;
							default:
								break;
							}

							if (String.valueOf(tempMap.get("obisCode")).startsWith("0000600B") && String.valueOf(tempMap.get("obisCode")).endsWith("FF")) {
								dataMap.put("eventCode", dataValue == null ? null : DataUtil.getIntToBytes(dataValue));
							} else {
								dataMap.put("value"+i, dataValue == null ? null : DataUtil.getIntToBytes(dataValue));
							}

						} else if (ObjectType.LIMITER.getValue() == Integer.parseInt(String.valueOf(tempMap.get("classId"))) && ("3".equals(String.valueOf(tempMap.get("attribute"))))) {
							byte[] dataType = new byte[1];
							System.arraycopy(information, infoPos, dataType, 0, dataType.length);
							infoPos += dataType.length;

							byte[] dataValue = new byte[DLMSCommonDataType.UINT32.getLenth()];
							System.arraycopy(information, infoPos, dataValue, 0, dataValue.length);
							infoPos += dataValue.length;

							dataMap.put("thresholdActive", DataUtil.getIntToBytes(dataValue));
						}
					}

					dataList.add(dataMap);
				}

				logger.debug("#### Custom Decoding Data List [Array] = {}", dataList.toString());
				return dataList;
			}
			break;

		case Structure:
			if (procedure == Procedure.GET_PROFILE_BUFFER) {
				infoPos -= getDataResultType.length;

				while (information.length != infoPos) {
					byte[] structureType = new byte[1];
					System.arraycopy(information, infoPos, structureType, 0, structureType.length);
					infoPos += structureType.length;

					byte[] structureLen = new byte[1];
					System.arraycopy(information, infoPos, structureLen, 0, structureLen.length);
					infoPos += structureLen.length;

					HashMap<String, Object> dataMap = new HashMap<String, Object>();
					for (int i = 0; i < channelList.size(); i++) {
						HashMap<String, Object> tempMap = channelList.get(i);

						if (ObjectType.CLOCK.getValue() == Integer.parseInt(String.valueOf(tempMap.get("classId"))) && "2".equals(String.valueOf(tempMap.get("attribute")))) {
							byte[] octetType = new byte[1];
							System.arraycopy(information, infoPos, octetType, 0, octetType.length);
							infoPos += octetType.length;

							byte[] octetSize = new byte[1];
							System.arraycopy(information, infoPos, octetSize, 0, octetSize.length);
							infoPos += octetSize.length;

							byte[] year = new byte[2];
							System.arraycopy(information, infoPos, year, 0, year.length);
							infoPos += year.length;

							byte[] month = new byte[1];
							System.arraycopy(information, infoPos, month, 0, month.length);
							infoPos += month.length;

							byte[] dayOfMonth = new byte[1];
							System.arraycopy(information, infoPos, dayOfMonth, 0, dayOfMonth.length);
							infoPos += dayOfMonth.length;

							byte[] dayOfWeek = new byte[1];
							System.arraycopy(information, infoPos, dayOfWeek, 0, dayOfWeek.length);
							infoPos += dayOfWeek.length;

							byte[] hour = new byte[1];
							System.arraycopy(information, infoPos, hour, 0, hour.length);
							infoPos += hour.length;

							byte[] minute = new byte[1];
							System.arraycopy(information, infoPos, minute, 0, minute.length);
							infoPos += minute.length;

							byte[] second = new byte[1];
							System.arraycopy(information, infoPos, second, 0, second.length);
							infoPos += second.length;

							byte[] hundredthsOfSecond = new byte[1];
							System.arraycopy(information, infoPos, hundredthsOfSecond, 0, hundredthsOfSecond.length);
							infoPos += hundredthsOfSecond.length;

							byte[] deviation = new byte[2];
							System.arraycopy(information, infoPos, deviation, 0, deviation.length);
							infoPos += deviation.length;

							byte[] clockStatus = new byte[1];
							System.arraycopy(information, infoPos, clockStatus, 0, clockStatus.length);
							infoPos += clockStatus.length;

							String yyyymmdd = String.format("%04d", DataUtil.getIntTo2Byte(year)) + String.format("%02d", DataUtil.getIntToByte(month[0])) + String.format("%02d", DataUtil.getIntToByte(dayOfMonth[0]));
							String yyyymmddhhmmss = yyyymmdd + String.format("%02d", DataUtil.getIntToByte(hour[0])) + String.format("%02d", DataUtil.getIntToByte(minute[0])) + String.format("%02d", DataUtil.getIntToByte(second[0]));

							dataMap.put("yyyymmddhhmmss", yyyymmddhhmmss);
							dataMap.put("yyyymmdd", yyyymmdd);
						} else if ((ObjectType.DATA.getValue() == Integer.parseInt(String.valueOf(tempMap.get("classId"))) 
								|| ObjectType.REGISTER.getValue() == Integer.parseInt(String.valueOf(tempMap.get("classId")))) 
									&& "2".equals(String.valueOf(tempMap.get("attribute")))) {
							byte[] dataType = new byte[1];
							System.arraycopy(information, infoPos, dataType, 0, dataType.length);
							infoPos += dataType.length;

							byte[] dataValue = null;
							switch (DLMSCommonDataType.getItem(dataType[0])) {
							case UINT8:
								dataValue = new byte[DLMSCommonDataType.UINT8.getLenth()];
								System.arraycopy(information, infoPos, dataValue, 0, dataValue.length);
								infoPos += dataValue.length;
								break;
							case UINT16:
								dataValue = new byte[DLMSCommonDataType.UINT16.getLenth()];
								System.arraycopy(information, infoPos, dataValue, 0, dataValue.length);
								infoPos += dataValue.length;
								break;
							case UINT32:
								dataValue = new byte[DLMSCommonDataType.UINT32.getLenth()];
								System.arraycopy(information, infoPos, dataValue, 0, dataValue.length);
								infoPos += dataValue.length;
								break;
							default:
								break;
							}

							if (String.valueOf(tempMap.get("obisCode")).startsWith("0000600B") && String.valueOf(tempMap.get("obisCode")).endsWith("FF")) {
								dataMap.put("eventCode", dataValue == null ? null : DataUtil.getIntToBytes(dataValue));
							} else {
								dataMap.put("value"+i, dataValue == null ? null : DataUtil.getIntToBytes(dataValue));
							}

						} else if (ObjectType.LIMITER.getValue() == Integer.parseInt(String.valueOf(tempMap.get("classId"))) && ("3".equals(tempMap.get("attribute")))) {
							byte[] dataType = new byte[1];
							System.arraycopy(information, infoPos, dataType, 0, dataType.length);
							infoPos += dataType.length;

							byte[] dataValue = new byte[DLMSCommonDataType.UINT32.getLenth()];
							System.arraycopy(information, infoPos, dataValue, 0, dataValue.length);
							infoPos += dataValue.length;

							dataMap.put("thresholdActive", DataUtil.getIntToBytes(dataValue));
						}
					}

					dataList.add(dataMap);
				}

				logger.debug("#### Custom Decoding Data List [Structure] = ", dataList.toString());
				
				return dataList;
			}
			break;

		default:
			break;
		}
		return null;
	}

}
