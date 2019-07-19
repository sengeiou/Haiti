/**
 * (@)# BypassSORIAFactory.java
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
package com.aimir.fep.bypass.decofactory.protocolfactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.dao.device.MeterDao;
import com.aimir.fep.bypass.decofactory.consts.DlmsConstants;
import com.aimir.fep.bypass.decofactory.consts.DlmsConstants.XDLMS_APDU;
import com.aimir.fep.bypass.decofactory.consts.DlmsConstantsForIESCO.ActionResult;
import com.aimir.fep.bypass.decofactory.consts.DlmsConstantsForIESCO.DataAccessResult;
import com.aimir.fep.bypass.decofactory.consts.DlmsConstantsForIESCO.ImageTransferStatus;
import com.aimir.fep.bypass.decofactory.consts.HdlcConstants.HdlcFrameType;
import com.aimir.fep.bypass.decofactory.decoframe.IESCO_DLMSFrame;
import com.aimir.fep.bypass.decofactory.decoframe.INestedFrame;
import com.aimir.fep.bypass.decofactory.decorator.NestedDLMSDecoratorForIESCO;
import com.aimir.fep.bypass.decofactory.decorator.NestedHDLCDecoratorForIESCO;
import com.aimir.fep.bypass.decofactory.decorator.NestedNIBypassDecoratorForIESCO;
import com.aimir.fep.bypass.decofactory.protocolfactory.BypassFrameFactory.Procedure;
import com.aimir.fep.command.conf.DLMSMeta.CONTROL_STATE; // INSERT 2016/09/20 SP-117
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.meter.parser.DLMSKaifaTable.DLMSVARIABLE.DLMS_CLASS;
import com.aimir.fep.meter.parser.DLMSKaifaTable.DLMSVARIABLE.DLMS_CLASS_ATTR;
import com.aimir.fep.meter.parser.DLMSKaifaTable.DLMSVARIABLE.OBIS;
import com.aimir.fep.meter.parser.DLMSKaifaTable.DLMSVARIABLE.RELAY_STATUS_KAIFA; // INSERT 2016/09/20 SP-117
import com.aimir.fep.meter.parser.DLMSKaifaTable.DLMSVARIABLE.UNIT;
import com.aimir.fep.protocol.fmp.common.Target;
import com.aimir.fep.protocol.nip.client.multisession.MultiSession;
import com.aimir.fep.protocol.nip.frame.payload.Bypass;
import com.aimir.fep.protocol.nip.frame.payload.Bypass.TID_Type;
import com.aimir.fep.trap.actions.PH.EV_PH_200_63_0_Action;
import com.aimir.fep.trap.actions.PH.EV_PH_200_66_0_Action;
import com.aimir.fep.trap.common.EV_Action.OTA_UPGRADE_RESULT_CODE;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.Hex;
import com.aimir.fep.util.Util;
import com.aimir.model.device.Device.DeviceType;
import com.aimir.model.device.Meter;
import com.aimir.util.DateTimeUtil;

import net.sf.json.JSONArray;

/**
 * @author simhanger
 *
 */
public class BypassIESCOFactory extends BypassFrameFactory {
	private static Logger logger = LoggerFactory.getLogger(BypassIESCOFactory.class);
	private INestedFrame frame;
	private String meterId; // OAC에서 인증키를 받기위해 반드시 필요.
	private String command;
	private Procedure step;
	private MultiSession session;
	private HashMap<String, Object> params;
	private BypassFrameResult bypassFrameResult = new BypassFrameResult();
	private int sendHdlcPacketMaxSize = 0;

	/*
	 * Meter F/W OTA용
	 */
	private String imageIdentifier;
	private int fwSize;
	private int packetSize;
	private int offset;
	private byte[] fwImgArray;
	private int totalImageBlockNumber;
	private int imageBlockNumber;
	private int remainPackateLength;
	private byte[] sendPacket = null;
	private String progressRate = "0%";

	private int verificationRetryCount;
	private boolean isTakeOverMode;
	private boolean isTakeOverCheckStep = false;

	private Timer blockTransferRetryTimer;
	private int timerCreateFlag;
	private NeedImangeBlockTransferRetry blockTransferRetryTask;
	
	private int NEED_IMAGE_BLOCK_TRANSFER_MAX_RETRY_COUNT;
	private int SEND_IAMGE_RETRY_TIMEOUT;
	private long procedureStartTime;

	// Get-Response-With-Datablock 으로 받은 데이터 배열. 모든 블럭을 다 받은후 처리하기 위함.
	private byte[] dataBlockArrayOfGetRes;
	private List<HashMap<String, Object>> channelData;

	private HashMap<String, Object> optionalData;

	@SuppressWarnings("unused")
	private int sendDelayTime;
	
	// INSERT START SP-722
	private long sendTime = 0;
	private TID_Type niTidType = TID_Type.Disable;
	private int	niTidLocation = 0;
	private int	niTransId = 0;
	private	int niRetry = 0;

	// INSERT START SP-737
	private ArrayList<String> ondemandValueList = null;
	private ArrayList<byte[]> rawDataList = null; 
	private boolean sendFrameRetry = false;
	private int  	sendFrameRetryCount = 0;
	private boolean	splitOnDamend = false;
	private boolean rcvHdlcRRFrame = false;
	private int recordsPerBlock = Integer.parseInt(FMPProperty.getProperty("protocol.bypass.records.per.block", "3"));
	// INSERT END   SP-737
	private boolean  decodeFailedAndResended = false; // SP-686
	private int		 sendFramRetryMax = Integer.parseInt(FMPProperty.getProperty("protocol.bypass.frame.retry", "3")); // SP-686

	enum NI_TID_STATUS {
		TYPE_DISABLE(-1),
		OK(0),
		BAD_LOCATION(1),
		PREV_TID(2),
		BAD_TID(3);
		
		private int code;
		
		NI_TID_STATUS(int code) {
            this.code = code;
        }
        
        public int getCode() {
            return this.code;
        }
	}
	// INSERT END SP-722
	public void pushOptionalData(String key, Object value) {
		if (optionalData == null) {
			optionalData = new HashMap<String, Object>();
		}
		optionalData.put(key, value);
	}

	/*
	 * ACTION_IMAGE_BLOCK_TRANSFER 상태 플래그
	 * TRUE = SEND 했음
	 * FALSE = RECEIVE 했음.
	 */
	private boolean needImangeBlockTransferRetry = false;
	//	private boolean RetrySendTaskFlag = false;

	/**
	 * SP-519
	 * 
	 * @param meterId
	 * @param command
	 * @param useNiBypass
	 */
	public BypassIESCOFactory(String meterId, String command, boolean useNiBypass) {
		this.meterId = meterId;
		this.command = command;

		logger.debug("BypassIESCOFactory init.. MeterId={}, Command={} NiBypass={}", meterId, command, useNiBypass);

		//		int niBypassSetting  = Integer.parseInt(FMPProperty.getProperty("soria.protocol.modem.nibypass.use" , "0"));
		if (useNiBypass) {
			this.frame = new NestedNIBypassDecoratorForIESCO(new NestedHDLCDecoratorForIESCO(new NestedDLMSDecoratorForIESCO(new IESCO_DLMSFrame())));
		} else {
			// For Null Bypass
			this.frame = new NestedHDLCDecoratorForIESCO(new NestedDLMSDecoratorForIESCO(new IESCO_DLMSFrame()));
		}

		this.frame.setMeterId(meterId);

		setDefalutRetryTimeOut();
		params = new LinkedHashMap<String, Object>();
	}

	public BypassIESCOFactory(String meterId, String command) {
		this.meterId = meterId;
		this.command = command;

		logger.debug("BypassIESCOFactory init.. MeterId={}, Command={}", meterId, command);

		// For Normal Bypass
		// this.frame = new NestedNIDecorator(new NestedHDLCDecoratorForSORIA(new NestedDLMSDecoratorForSORIA(new SORIA_DLMSFrame())));

		// For Null Bypass
		this.frame = new NestedHDLCDecoratorForIESCO(new NestedDLMSDecoratorForIESCO(new IESCO_DLMSFrame()));
		this.frame.setMeterId(meterId);

		setDefalutRetryTimeOut();
		params = new LinkedHashMap<String, Object>();
	}

	private void setDefalutRetryTimeOut() {
		NEED_IMAGE_BLOCK_TRANSFER_MAX_RETRY_COUNT = Integer.parseInt(FMPProperty.getProperty("ota.firmware.meter.datasend.retry", "5"));
		
		if (Boolean.parseBoolean(FMPProperty.getProperty("soria.protocol.modem.rf.dtls.use", "true"))) {
			SEND_IAMGE_RETRY_TIMEOUT = 20; // DTLS의 경우 Modem의 타임아웃이 30초임. 나머지 바이패스는 60초.
		}else{
			SEND_IAMGE_RETRY_TIMEOUT = Integer.parseInt(FMPProperty.getProperty("ota.firmware.meter.datasend.retry.timeout", "30"));
		}
	}

	@Override
	public void setParam(HashMap<String, Object> param) {
		params = param;
	}

	@Override
	public boolean start(MultiSession session, Object type) throws Exception {
		boolean result = false;
		this.session = session;
		step = Procedure.HDLC_SNRM;
		
		if (command.equals("cmdSetDLMSMeterTime") || command.equals("cmdGetDLMSMeterTime") || command.equals("cmdSetRegisterValue") || command.equals("cmdGetRegisterValue") || command.equals("cmdSetRegisterUnit") || command.equals("cmdGetRegisterUnit") || command.equals("cmdGetProfileBuffer") || command.equals("cmdGetProfileObject") || command.equals("cmdSetProfilePeriod")
				|| command.equals("cmdGetProfilePeriod") || command.equals("cmdSetThresholdNormal") || command.equals("cmdGetThresholdNormal") || command.equals("cmdSetMinOverThresholdDuration") || command.equals("cmdGetMinOverThresholdDuration") || command.equals("cmdGetRelayState") || command.equals("cmdSetRelayState") || command.equals("cmdActRelayState")
				|| command.equals("cmdGetMeterFWVersion") || command.equals("cmdSORIASetMeterSerial")|| command.equals("cmdSORIAGetMeterKey") || command.equals("cmdGetValue") || command.equals("cmdSetValue") || command.equals("cmdGetRelayStatusAll") // INSERT 2016/08/24 SP117
				|| command.equals("cmdActSlaveInstall") || command.equals("cmdActSetEncryptionKey") || command.equals("cmdActTransferKey") || command.equals("cmdGetLoadProfileOnDemand") || command.equals("cmdGetLoadProfileOnDemandMbb")
				|| command.equals("cmdGetSingleActionSchedule") || command.equals("cmdSetSingleActionSchedule")
				) {

			int useNiTid = Integer.parseInt(FMPProperty.getProperty("protocol.bypass.tid.use", "1"));
			Target target = (Target)params.get("target");
			if(target != null && target.toString() != null) {
				logger.debug("#### Commadn Start option. Command={}, useNiTid={}, Target={}", command, useNiTid, target.toString());
			}
			
			// INSERT START SP-722
			if (frame instanceof NestedNIBypassDecoratorForIESCO && 
			        (command.equals("cmdGetLoadProfileOnDemand") 
			                || command.equals("cmdGetMeterFWVersion")
			                || command.equals("cmdGetLoadProfileOnDemandMbb"))){
				if ( useNiTid == 0 ){
					niTidType = TID_Type.Disable;
					niTidLocation = 0;
					niTransId = 0;
					niRetry = 0;
				}
				else {
					String fwVer = target.getFwVer();
					if ( fwVer.compareTo("1.2") >= 0 ){
						logger.debug("NI TID Enable");
						niTidType = TID_Type.Enable;
						niTidLocation = 0;
						niTransId = 0;
						niRetry = 0;
					}
				}
			}
			// INSERT END  SP-722
			// INSERT START SP-737
			sendFrameRetry = false;
			splitOnDamend = false;
			int sendFrameRetryUse = Integer.parseInt(FMPProperty.getProperty("protocol.bypass.frameretry.use", "1"));
			logger.debug("sendFrameRetry Use = [{}]", sendFrameRetryUse);
			if ( sendFrameRetryUse != 0 )
				sendFrameRetry = true;
			
			int splitOnDamendUse = Integer.parseInt(FMPProperty.getProperty("protocol.bypass.splitondemand.use", "1"));	 
			if ( splitOnDamendUse != 0 && niTidType == TID_Type.Disable  ){
				if ( command.equals("cmdGetLoadProfileOnDemand") || command.equals("cmdGetLoadProfileOnDemandMbb")){
					splitOnDamend = true;
					MeterDao meterDao = DataUtil.getBean(MeterDao.class);
					Meter meter = meterDao.get(meterId);
					Integer interval = meter.getLpInterval();
					ondemandValueList = makeSplitOnDemendArg(interval, (String)params.get("value"));
					String value = getNextOndemandSplitParam();
					if ( value != null ){
						logger.debug("Set First Time Parameter for OnDemand:{}", value);
						rawDataList = new ArrayList<byte[]>();
						params.put("value", value);
					}
				}
			}
			
			logger.debug("#### Commadn Start option. Command={}, TID_Type={}, niTidLocation={}, niTransId={}, niRetry={}, sendFrameRetryUse={}, splitOnDamendUse={}"
					, command, niTidType, niTidLocation, niTransId, niRetry, sendFrameRetryUse, splitOnDamendUse);
			
			// INSERT END   SP-737
			result = ( niTidType == TID_Type.Enable) ? sendBypassWidhNiTid(true) : sendBypass(); //UPDATE SP-722
		} else if (command.equals("cmdMeterOTAStart")) {
			
			Target target = (Target)session.getAttribute("target");
			params.put("target", target);  
			
			/*
			 * TID 사용여부 세팅
			 */
			int useNiTid = Integer.parseInt(FMPProperty.getProperty("protocol.bypass.tid.use", "1"));
			if ( useNiTid == 0 ){
				niTidType = TID_Type.Disable;
				niTidLocation = 0;
				niTransId = 0;
				niRetry = 0;
			}
			else {
				logger.debug("NI TID Enable");
				niTidType = TID_Type.Enable;
				niTidLocation = 0;
				niTransId = 0;
				niRetry = 0;
			}
			
			sendFrameRetry = false;
			int sendFrameRetryUse = Integer.parseInt(FMPProperty.getProperty("protocol.bypass.frameretry.use", "1"));
			logger.debug("sendFrameRetry=[{}]", sendFrameRetryUse);
			if ( sendFrameRetryUse != 0 ){
				sendFrameRetry = true;
			}
			
			if (0 < session.getBypassDevice().getSendDelayTime()) {
				sendDelayTime = session.getBypassDevice().getSendDelayTime();
			}

			procedureStartTime = System.currentTimeMillis();
			logger.debug("## [cmdMeterOTAStart] Start : {}", DateTimeUtil.getDateString(procedureStartTime));

			if (params != null && params.get("take_over") != null) {
				isTakeOverMode = Boolean.parseBoolean(String.valueOf(params.get("take_over")));
			}

			if (params != null && params.get("image") != null) {
				imageIdentifier = (String) params.get("image_identifier");

				/*
				 *  Image Key : 6자리
				 */
				// imageIdentifier = imageIdentifier.substring(0, 6);

				fwImgArray = (byte[]) params.get("image");
				fwSize = fwImgArray.length;
				remainPackateLength = fwSize;

				result = ( niTidType == TID_Type.Enable) ? sendBypassWidhNiTid(true) : sendBypass(); //UPDATE SP-722
			}
			
			
			
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public BypassFrameResult receiveBypass(MultiSession session, byte[] rawFrame) throws Exception {
		boolean result = false;
		bypassFrameResult.clean();
		
		NestedNIBypassDecoratorForIESCO nBypassFrame = null; //SP-722
		decodeFailedAndResended = false; // SP-868
		
		//if (frame.decode(rawFrame, this.step, command)) {
		if (frame.decode(session, rawFrame, this.step, command)) {
			// INSERT START SP-722
			if ( niTidType == TID_Type.Enable) {
				// check Received Ni TID
				nBypassFrame = (NestedNIBypassDecoratorForIESCO)frame;
				byte[] sendTid = nBypassFrame.getSendTid();
				byte[] recvTid = nBypassFrame.getReceiveTid();
				NI_TID_STATUS niTidStatus= checkNiTid(sendTid, recvTid);
				switch ( niTidStatus ){
					case TYPE_DISABLE:
					case OK:
						break;
					case BAD_LOCATION:
						logger.info("TID Location is not Match.");
						return null;
					case BAD_TID:
						logger.error("TID is not Match.");
						return null;
					case PREV_TID:
						logger.info("TID is previous, ignore" );
						return null;
				default:
					break;
				}
			}
			// INSERT END SP-722
			// INSERT START SP-737
			else if ( sendFrameRetry ){
				boolean rrFrame = false;
				if ( frame instanceof NestedNIBypassDecoratorForIESCO){
					rrFrame = ((NestedNIBypassDecoratorForIESCO) frame).isHdlcRRFrame();
				}
				else if ( frame instanceof NestedHDLCDecoratorForIESCO && frame.getHDLCFrameType() == HdlcFrameType.RR){
					//rrFrame = ((NestedHDLCDecoratorForIESCO)frame).isRrFrame();
					rrFrame = true;
				}
//				logger.debug("sendRrameRetry[{}] frame.getType[{}] sendRetryCount[{}] isRrFrame[{}]", 
//						sendFrameRetry,frame.getType(),sendFrameRetryCount, rrFrame );

//				if ( HdlcObjectType.getItem(DataUtil.getByteToInt(frame.getType())) == HdlcObjectType.UNKNOWN
//					 && rrFrame
//					 && ( sendFrameRetryCount <= 1 || rcvHdlcRRFrame == false ) ){
				if(frame.getHDLCFrameType() == HdlcFrameType.NULL 
						&& rrFrame
						&& ( sendFrameRetryCount <= 1 || rcvHdlcRRFrame == false ) ){
					
					
					logger.info("Receive HDLC RR Frame, ignore");
					rcvHdlcRRFrame = true;
					return null;
				}
			}
			else {
				logger.debug("sendRetry[{}] frame.getType[{}] frame.class[{}]",
						//sendFrameRetry, frame.getType(),frame.getClass().getName());
						sendFrameRetry, frame.getHDLCFrameType(),frame.getClass().getName());
			}
			params.put("hdlcResendFrame", 0);
			rcvHdlcRRFrame = false;
			sendFrameRetryCount = 0;
			// INSERT END   SP-737
			try {
			    /*
			    if (HdlcObjectType.getItem(DataUtil.getByteToInt(frame.getType())) == HdlcObjectType.UNKNOWN) {
		            logger.debug("HdlcObjectType frameType is  {}", HdlcObjectType.getItem(DataUtil.getByteToInt(frame.getType())));
		            logger.debug("Return nothing to receive again");
		            return null;
		        }
		        */
			    
				/**
				 * 공통
				 */
				if (frame.getHDLCFrameType() == HdlcFrameType.UA) {
					if (this.step == Procedure.HDLC_SNRM) {
						// 결과 확인
						sendHdlcPacketMaxSize = Integer.parseInt(String.valueOf(frame.getResultData()));
						logger.debug("### Send HDLC Packet Max Size = {}", sendHdlcPacketMaxSize);

						this.step = Procedure.HDLC_AARQ;
						result = ( niTidType == TID_Type.Enable) ? sendBypassWidhNiTid(true) : sendBypass(); //UPDATE SP-722
					} else if (this.step == Procedure.HDLC_DISC) {
						logger.info("### HDLC DISC !!");
						//session.closeNow();
						logger.debug("BypassIESCOFactory [HDLC_DISC] MultiSession destroy start");
						session.destroy();
						logger.debug("BypassIESCOFactory [HDLC_DISC] MultiSession destroy end");
						result = true;
					}
				} else if (frame.getHDLCFrameType() == HdlcFrameType.I) {
					if (frame.isHDLCSegmented()) {
						result = sendBypass(HdlcFrameType.RR);
					} else {
						if (frame.getDlmsApdu() == XDLMS_APDU.AARE) {
							if (this.step == Procedure.HDLC_AARQ) {
								// 결과 확인
								boolean param = Boolean.valueOf(String.valueOf(frame.getResultData()));
								logger.debug("## HDLC_AARQ Result => {}", param);
								
								if (param) {
									this.step = Procedure.HDLC_ASSOCIATION_LN;							
									result = ( niTidType == TID_Type.Enable) ? sendBypassWidhNiTid(true) : sendBypass(); //UPDATE SP-722
								}else{
									if(command.equals("cmdSORIAGetMeterKey")){
										logger.debug("### !!! SORIA Get Meter Key !!! ###");
										this.step = Procedure.GET_SORIA_METER_KEY_A;
										result = ( niTidType == TID_Type.Enable) ? sendBypassWidhNiTid(true) : sendBypass(); //UPDATE SP-722
									}
									if(command.equals("cmdSORIASetMeterSerial")){
										logger.debug("### !!! SORIA Set Meter Serial !!! ###");
										this.step = Procedure.SET_SORIA_METER_SERIAL_A;
										result = ( niTidType == TID_Type.Enable) ? sendBypassWidhNiTid(true) : sendBypass(); //UPDATE SP-722
									}
								}
							}
						}else if(frame.getDlmsApdu() == XDLMS_APDU.KAIFA_CUSTOM) {  // Kaifa Custom
							logger.debug("### !!! HdlcFrameType.NULL !!! ###");
//							if(command.equals("cmdSORIAGetMeterKey")){
//								logger.debug("### !!! SORIA Get Meter Key !!! ###");
//								this.step = Procedure.GET_SORIA_METER_KEY_A;
//								result = sendBypass();
//							}
						}
						
						
						// Command별 처리
						if (command.equals("cmdSetDLMSMeterTime")) {
							//switch (HdlcObjectType.getItem(DataUtil.getByteToInt(frame.getType()))) {
							switch (frame.getDlmsApdu()) {
							case ACTION_RESPONSE:
								if (this.step == Procedure.HDLC_ASSOCIATION_LN) {
									// 결과 확인
									ActionResult param = (ActionResult) frame.getResultData();
									logger.debug("## HDLC_ASSOCIATION_LN Result => {}", param.name());
									if (param == ActionResult.SUCCESS) {
										this.step = Procedure.SET_METER_TIME;
										result = sendBypass();
									}
								}
								break;
							case SET_RESPONSE:
								// 결과 확인
								DataAccessResult param = (DataAccessResult) frame.getResultData();
								logger.debug("## SET_METER_TIME => {}", param.name());

								if (param == DataAccessResult.SUCCESS) {
									bypassFrameResult.setLastProcedure(Procedure.SET_METER_TIME);
									bypassFrameResult.setResultValue("Success");
									result = true;

									this.step = Procedure.HDLC_DISC;
									result = sendBypass();
								}
								break;
							default:
								break;
							}
						} else if (command.equals("cmdGetDLMSMeterTime")) {
							//switch (HdlcObjectType.getItem(DataUtil.getByteToInt(frame.getType()))) {
							switch (frame.getDlmsApdu()) {
							case ACTION_RESPONSE:
								if (this.step == Procedure.HDLC_ASSOCIATION_LN) {
									// 결과 확인
									ActionResult param = (ActionResult) frame.getResultData();
									logger.debug("## HDLC_ASSOCIATION_LN Result => {}", param.name());
									if (param == ActionResult.SUCCESS) {
										this.step = Procedure.GET_METER_TIME;
										result = sendBypass();
									}
								}
								break;
							case GET_RESPONSE:
								// 결과 확인
								Object param = frame.getResultData();
								if (param instanceof DataAccessResult) {
									result = false;
									logger.debug("## [{}]GET_RES_DataAccessResult => {}", step.name(), ((DataAccessResult) param).name());
								} else {
									if (this.step == Procedure.GET_METER_TIME) {
										// 결과 확인
										HashMap<String, String> resultData = (HashMap<String, String>) param;
										if (!resultData.equals("")) {
											logger.debug("## GET_METER_TIME => {}", resultData);

											bypassFrameResult.setLastProcedure(Procedure.GET_METER_TIME);
											bypassFrameResult.setResultValue(resultData);

											result = true;
										}

										this.step = Procedure.HDLC_DISC;
										result = sendBypass();
									}
								}
								break;
							default:
								break;
							}
						} else if (command.equals("cmdSetRegisterValue")) {
							//switch (HdlcObjectType.getItem(DataUtil.getByteToInt(frame.getType()))) {
							switch (frame.getDlmsApdu()) {
							case ACTION_RESPONSE:
								if (this.step == Procedure.HDLC_ASSOCIATION_LN) {
									// 결과 확인
									ActionResult param = (ActionResult) frame.getResultData();
									logger.debug("## HDLC_ASSOCIATION_LN Result => {}", param.name());
									if (param == ActionResult.SUCCESS) {
										this.step = Procedure.SET_REGISTER_VALUE;
										result = sendBypass();
									}
								}
								break;
							case SET_RESPONSE:
								// 결과 확인
								DataAccessResult param = (DataAccessResult) frame.getResultData();
								logger.debug("## SET_RESIGETER_VALUE => {}", param.name());

								if (param == DataAccessResult.SUCCESS) {
									bypassFrameResult.setLastProcedure(Procedure.SET_REGISTER_VALUE);
									bypassFrameResult.setResultValue("Success");
									result = true;

									this.step = Procedure.HDLC_DISC;
									result = sendBypass();
								}
								break;
							default:
								break;
							}
						} else if (command.equals("cmdGetRegisterValue")) {
							//switch (HdlcObjectType.getItem(DataUtil.getByteToInt(frame.getType()))) {
							switch (frame.getDlmsApdu()) {
							case ACTION_RESPONSE:
								if (this.step == Procedure.HDLC_ASSOCIATION_LN) {
									// 결과 확인
									ActionResult param = (ActionResult) frame.getResultData();
									logger.debug("## HDLC_ASSOCIATION_LN Result => {}", param.name());
									if (param == ActionResult.SUCCESS) {
										this.step = Procedure.GET_REGISTER_VALUE;
										result = sendBypass();
									}
								}
								break;
							case GET_RESPONSE:
								// 결과 확인
								Object param = frame.getResultData();
								if (param instanceof DataAccessResult) {
									result = false;
									logger.debug("## [{}]GET_RES_DataAccessResult => {}", step.name(), ((DataAccessResult) param).name());
								} else {
									if (this.step == Procedure.GET_REGISTER_VALUE) {
										// 결과 확인
										long resultData = (Long) param;
										logger.debug("## GET_RESIGETER_VALUE => {}", resultData);

										bypassFrameResult.setLastProcedure(Procedure.GET_REGISTER_VALUE);
										bypassFrameResult.setResultValue(resultData);

										result = true;

										this.step = Procedure.HDLC_DISC;
										result = sendBypass();
									}
								}
								break;
							default:
								break;
							}
						} else if (command.equals("cmdGetRegisterUnit")) {
							//switch (HdlcObjectType.getItem(DataUtil.getByteToInt(frame.getType()))) {
							switch (frame.getDlmsApdu()) {
							case ACTION_RESPONSE:
								if (this.step == Procedure.HDLC_ASSOCIATION_LN) {
									// 결과 확인
									ActionResult param = (ActionResult) frame.getResultData();
									logger.debug("## HDLC_ASSOCIATION_LN Result => {}", param.name());
									if (param == ActionResult.SUCCESS) {
										this.step = Procedure.GET_REGISTER_UNIT;
										result = sendBypass();
									}
								}
								break;
							case GET_RESPONSE:
								// 결과 확인
								Object param = frame.getResultData();
								if (param instanceof DataAccessResult) {
									result = false;
									logger.debug("## [{}]GET_RES_DataAccessResult => {}", step.name(), ((DataAccessResult) param).name());
								} else {
									if (this.step == Procedure.GET_REGISTER_UNIT) {
										// 결과 확인
										HashMap<String, Object> resultData = (HashMap<String, Object>) param;

										logger.debug("## GET_REGISTER_UNIT => {}", resultData);
										bypassFrameResult.setLastProcedure(Procedure.GET_REGISTER_UNIT);
										if (resultData.size() > 0) {
											bypassFrameResult.setResultValue(resultData);
										}
										result = true;

										this.step = Procedure.HDLC_DISC;
										result = sendBypass();
									}
								}
								break;
							default:
								break;
							}
						} else if (command.equals("cmdSetRegisterUnit")) {
							//switch (HdlcObjectType.getItem(DataUtil.getByteToInt(frame.getType()))) {
							switch (frame.getDlmsApdu()) {
							case ACTION_RESPONSE:
								if (this.step == Procedure.HDLC_ASSOCIATION_LN) {
									// 결과 확인
									ActionResult param = (ActionResult) frame.getResultData();
									logger.debug("## HDLC_ASSOCIATION_LN Result => {}", param.name());
									if (param == ActionResult.SUCCESS) {
										this.step = Procedure.SET_REGISTER_UNIT;
										result = sendBypass();
									}
								}
								break;
							case SET_RESPONSE:
								// 결과 확인
								DataAccessResult param = (DataAccessResult) frame.getResultData();
								logger.debug("## SET_REGISTER_UNIT => {}", param.name());

								if (param == DataAccessResult.SUCCESS) {
									bypassFrameResult.setLastProcedure(Procedure.SET_REGISTER_UNIT);
									bypassFrameResult.setResultValue("Success");
									result = true;

									this.step = Procedure.HDLC_DISC;
									result = sendBypass();
								}
								break;
							default:
								break;
							}
						} else if (command.equals("cmdGetProfileBuffer")) {
							//switch (HdlcObjectType.getItem(DataUtil.getByteToInt(frame.getType()))) {
							switch (frame.getDlmsApdu()) {
							case ACTION_RESPONSE:
								if (this.step == Procedure.HDLC_ASSOCIATION_LN) {
									// 결과 확인
									ActionResult param = (ActionResult) frame.getResultData();
									logger.debug("## HDLC_ASSOCIATION_LN Result => {}", param.name());
									if (param == ActionResult.SUCCESS) {
										if (params != null && params.get("obisCode") != null) {
											logger.debug("## obiscode => {}" + params.get("obisCode").toString());
											if (params.get("obisCode").toString().equals(DataUtil.convertObis(OBIS.MBUSMASTER_LOAD_PROFILE.getCode()))) {
												logger.debug("## next step = GET_PROFILE_BUFFER");
												this.step = Procedure.GET_PROFILE_BUFFER;
											} else {
												logger.debug("## next step = GET_PROFILE_OBJECT");
												this.step = Procedure.GET_PROFILE_OBJECT;
											}
										} else {
											this.step = Procedure.GET_PROFILE_OBJECT;
										}
										result = sendBypass();
									}
								}
								break;
							case GET_RESPONSE:
								// 결과 확인
								Object param = frame.getResultData();
								if (param instanceof DataAccessResult) {
									result = false;
									logger.debug("## [{}]GET_RES_DataAccessResult => {}", step.name(), ((DataAccessResult) param).name());
								} else {
									if (this.step == Procedure.GET_PROFILE_OBJECT) {
										// 결과 확인
										List<HashMap<String, Object>> resultData = (ArrayList<HashMap<String, Object>>) param;
										channelData = resultData;

										logger.debug("## GET_PROFILE_OBJECT => {}", resultData);

										bypassFrameResult.setLastProcedure(Procedure.GET_PROFILE_OBJECT);
										result = true;

										this.step = Procedure.GET_PROFILE_BUFFER;

										result = sendBypass();
									} else if (this.step == Procedure.GET_PROFILE_BUFFER) {
										// 결과 확인
										Map<String, Object> map = (Map<String, Object>) param;
										Boolean isBlock = map.get("isBlock") == null ? false : (Boolean) map.get("isBlock");
										Boolean isLast = map.get("isLast") == null ? true : (Boolean) map.get("isLast");
										Integer blockNumber = map.containsKey("blockNumber") == false ? 0 : (Integer) map.get("blockNumber");
										logger.debug("## GET_PROFILE_BUFFER => {}", map);

										if (dataBlockArrayOfGetRes == null) {
											dataBlockArrayOfGetRes = new byte[] {};
										}

										dataBlockArrayOfGetRes = DataUtil.append(dataBlockArrayOfGetRes, (byte[]) map.get("rawData")); // 누적. 여러차례에 걸쳐 넘어오는 raw data를 하나로 모은다.
										logger.debug("dataBlockArrayOfGetRes=" + Hex.decode(dataBlockArrayOfGetRes));
										if (isLast) { // 마지막 블럭 처리
											params.clear();

											// 합산데이터 파싱처리.
											Object resultObj = frame.customDecode(Procedure.GET_PROFILE_BUFFER, dataBlockArrayOfGetRes);
											List<Object> obj = (List<Object>) resultObj;

											bypassFrameResult.setLastProcedure(this.step);
											bypassFrameResult.addResultValue("channelData", channelData);
											bypassFrameResult.addResultValue("rawData", dataBlockArrayOfGetRes);
											bypassFrameResult.addResultValue("listData", obj);

											this.step = Procedure.HDLC_DISC;

											result = true;
											result = sendBypass();
										} else {
											params.put("isBlock", isBlock);
											params.put("blockNumber", blockNumber);

											result = true;
											result = sendBypass();
										}

									}
								}
								break;
							default:
								break;
							}
						} else if (command.equals("cmdGetProfileObject")) {
							//switch (HdlcObjectType.getItem(DataUtil.getByteToInt(frame.getType()))) {
							switch (frame.getDlmsApdu()) {
							case ACTION_RESPONSE:
								if (this.step == Procedure.HDLC_ASSOCIATION_LN) {
									// 결과 확인
									ActionResult param = (ActionResult) frame.getResultData();
									logger.debug("## HDLC_ASSOCIATION_LN Result => {}", param.name());
									if (param == ActionResult.SUCCESS) {
										this.step = Procedure.GET_PROFILE_OBJECT;
										result = sendBypass();
									}
								}
								break;
							case GET_RESPONSE:
								// 결과 확인
								List<Map<String, Object>> resultList = (List<Map<String, Object>>) frame.getResultData();
								logger.debug("## GET_PROFILE_OBJECT => {}", resultList);

								bypassFrameResult.setLastProcedure(Procedure.GET_PROFILE_OBJECT);
								if (resultList.size() > 0) {
									bypassFrameResult.setResultValue(resultList);
								}
								result = true;

								this.step = Procedure.HDLC_DISC;
								result = sendBypass();
								break;
							default:
								break;
							}
						} else if (command.equals("cmdSetProfilePeriod")) {
							//switch (HdlcObjectType.getItem(DataUtil.getByteToInt(frame.getType()))) {
							switch (frame.getDlmsApdu()) {
							case ACTION_RESPONSE:
								if (this.step == Procedure.HDLC_ASSOCIATION_LN) {
									// 결과 확인
									ActionResult param = (ActionResult) frame.getResultData();
									logger.debug("## HDLC_ASSOCIATION_LN Result => {}", param.name());
									if (param == ActionResult.SUCCESS) {
										this.step = Procedure.SET_PROFILE_PERIOD;
										result = sendBypass();
									}
								}
								break;
							case SET_RESPONSE:
								// 결과 확인
								DataAccessResult param = (DataAccessResult) frame.getResultData();
								logger.debug("## SET_PROFILE_PERIOD => {}", param.name());

								if (param == DataAccessResult.SUCCESS) {
									bypassFrameResult.setLastProcedure(Procedure.SET_PROFILE_PERIOD);
									bypassFrameResult.setResultValue("Success");
									result = true;

									this.step = Procedure.HDLC_DISC;
									result = sendBypass();
								}
								break;
							default:
								break;
							}
						} else if (command.equals("cmdGetProfilePeriod")) {
							//switch (HdlcObjectType.getItem(DataUtil.getByteToInt(frame.getType()))) {
							switch (frame.getDlmsApdu()) {
							case ACTION_RESPONSE:
								if (this.step == Procedure.HDLC_ASSOCIATION_LN) {
									// 결과 확인
									ActionResult param = (ActionResult) frame.getResultData();
									logger.debug("## HDLC_ASSOCIATION_LN Result => {}", param.name());
									if (param == ActionResult.SUCCESS) {
										this.step = Procedure.GET_PROFILE_PERIOD;
										result = sendBypass();
									}
								}
								break;
							case GET_RESPONSE:
								// 결과 확인
								Object param = frame.getResultData();
								if (param instanceof DataAccessResult) {
									result = false;
									logger.debug("## [{}]GET_RES_DataAccessResult => {}", step.name(), ((DataAccessResult) param).name());
								} else {
									if (this.step == Procedure.GET_PROFILE_PERIOD) {
										// 결과 확인
										long resultData = (Long) param;
										logger.debug("## GET_PROFILE_PERIOD => {}", resultData);

										bypassFrameResult.setLastProcedure(Procedure.GET_PROFILE_PERIOD);
										bypassFrameResult.setResultValue(resultData);

										result = true;

										this.step = Procedure.HDLC_DISC;
										result = sendBypass();
									}
								}
								break;
							default:
								break;
							}
						} else if (command.equals("cmdSetThresholdNormal")) {
							//switch (HdlcObjectType.getItem(DataUtil.getByteToInt(frame.getType()))) {
							switch (frame.getDlmsApdu()) {
							case ACTION_RESPONSE:
								if (this.step == Procedure.HDLC_ASSOCIATION_LN) {
									// 결과 확인
									ActionResult param = (ActionResult) frame.getResultData();
									logger.debug("## HDLC_ASSOCIATION_LN Result => {}", param.name());
									if (param == ActionResult.SUCCESS) {
										this.step = Procedure.SET_THRESHOLD_NORMAL;
										result = sendBypass();
									}
								}
								break;
							case SET_RESPONSE:
								// 결과 확인
								DataAccessResult param = (DataAccessResult) frame.getResultData();
								logger.debug("## SET_THRESHOLD_NORMAL => {}", param.name());

								if (param == DataAccessResult.SUCCESS) {
									bypassFrameResult.setLastProcedure(Procedure.SET_THRESHOLD_NORMAL);
									bypassFrameResult.setResultValue("Success");
									result = true;

									this.step = Procedure.HDLC_DISC;
									result = sendBypass();
								}
								break;
							default:
								break;
							}
						} else if (command.equals("cmdGetThresholdNormal")) {
							//switch (HdlcObjectType.getItem(DataUtil.getByteToInt(frame.getType()))) {
							switch (frame.getDlmsApdu()) {
							case ACTION_RESPONSE:
								if (this.step == Procedure.HDLC_ASSOCIATION_LN) {
									// 결과 확인
									ActionResult param = (ActionResult) frame.getResultData();
									logger.debug("## HDLC_ASSOCIATION_LN Result => {}", param.name());
									if (param == ActionResult.SUCCESS) {
										this.step = Procedure.GET_THRESHOLD_NORMAL;
										result = sendBypass();
									}
								}
								break;
							case GET_RESPONSE:
								// 결과 확인
								Object param = frame.getResultData();
								if (param instanceof DataAccessResult) {
									result = false;
									logger.debug("## [{}]GET_RES_DataAccessResult => {}", step.name(), ((DataAccessResult) param).name());
								} else {
									if (this.step == Procedure.GET_THRESHOLD_NORMAL) {
										// 결과 확인
										long resultData = (Long) param;
										logger.debug("## GET_THRESHOLD_NORMAL => {}", resultData);

										bypassFrameResult.setLastProcedure(Procedure.GET_THRESHOLD_NORMAL);
										bypassFrameResult.setResultValue(resultData);

										result = true;

										this.step = Procedure.HDLC_DISC;
										result = sendBypass();
									}
								}
								break;
							default:
								break;
							}
						} else if (command.equals("cmdSetMinOverThresholdDuration")) {
							//switch (HdlcObjectType.getItem(DataUtil.getByteToInt(frame.getType()))) {
							switch (frame.getDlmsApdu()) {
							case ACTION_RESPONSE:
								if (this.step == Procedure.HDLC_ASSOCIATION_LN) {
									// 결과 확인
									ActionResult param = (ActionResult) frame.getResultData();
									logger.debug("## HDLC_ASSOCIATION_LN Result => {}", param.name());
									if (param == ActionResult.SUCCESS) {
										this.step = Procedure.SET_MINOVER_THRESHOLD_DURATION;
										result = sendBypass();
									}
								}
								break;
							case SET_RESPONSE:
								// 결과 확인
								DataAccessResult param = (DataAccessResult) frame.getResultData();
								logger.debug("## SET_MINOVER_THRESHOLD_DURATION => {}", param.name());

								if (param == DataAccessResult.SUCCESS) {
									bypassFrameResult.setLastProcedure(Procedure.SET_MINOVER_THRESHOLD_DURATION);
									bypassFrameResult.setResultValue("Success");
									result = true;

									this.step = Procedure.HDLC_DISC;
									result = sendBypass();
								}

								break;
							default:
								break;
							}
						} else if (command.equals("cmdGetMinOverThresholdDuration")) {
							//switch (HdlcObjectType.getItem(DataUtil.getByteToInt(frame.getType()))) {
							switch (frame.getDlmsApdu()) {
							case ACTION_RESPONSE:
								if (this.step == Procedure.HDLC_ASSOCIATION_LN) {
									// 결과 확인
									ActionResult param = (ActionResult) frame.getResultData();
									logger.debug("## HDLC_ASSOCIATION_LN Result => {}", param.name());
									if (param == ActionResult.SUCCESS) {
										this.step = Procedure.GET_MINOVER_THRESHOLD_DURATION;
										result = sendBypass();
									}
								}
								break;
							case GET_RESPONSE:
								// 결과 확인
								Object param = frame.getResultData();
								if (param instanceof DataAccessResult) {
									result = false;
									bypassFrameResult.setResultValue(((DataAccessResult) param).name());
									logger.debug("## [{}]GET_RES_DataAccessResult => {}", step.name(), ((DataAccessResult) param).name());
								} else {
									if (this.step == Procedure.GET_MINOVER_THRESHOLD_DURATION) {
										// 결과 확인
										long resultData = (Long) param;
										logger.debug("## GET_MINOVER_THRESHOLD_DURATION => {}", resultData);

										bypassFrameResult.setLastProcedure(Procedure.GET_MINOVER_THRESHOLD_DURATION);
										bypassFrameResult.setResultValue(resultData);

										result = true;

										this.step = Procedure.HDLC_DISC;
										result = sendBypass();
									}
								}
								break;
							default:
								break;
							}
						} else if (command.equals("cmdSetRelayState")) {
							//switch (HdlcObjectType.getItem(DataUtil.getByteToInt(frame.getType()))) {
							switch (frame.getDlmsApdu()) {
							case ACTION_RESPONSE:
								if (this.step == Procedure.HDLC_ASSOCIATION_LN) {
									// 결과 확인
									ActionResult param = (ActionResult) frame.getResultData();
									logger.debug("## HDLC_ASSOCIATION_LN Result => {}", param.name());
									if (param == ActionResult.SUCCESS) {
										this.step = Procedure.SET_DISCONNECT_CONTROL;
										result = sendBypass();
									}
								}
								break;
							case SET_RESPONSE:
								// 결과 확인
								DataAccessResult param = (DataAccessResult) frame.getResultData();
								logger.debug("## SET_RESIGETER_VALUE => {}", param.name());

								if (param == DataAccessResult.SUCCESS) {
									bypassFrameResult.setLastProcedure(Procedure.SET_DISCONNECT_CONTROL);
									bypassFrameResult.setResultValue("Success");
									result = true;

									this.step = Procedure.HDLC_DISC;
									result = sendBypass();
								}
								break;
							default:
								break;
							}
						} else if (command.equals("cmdGetRelayState")) {
							//switch (HdlcObjectType.getItem(DataUtil.getByteToInt(frame.getType()))) {
							switch (frame.getDlmsApdu()) {
							case ACTION_RESPONSE:
								if (this.step == Procedure.HDLC_ASSOCIATION_LN) {
									// 결과 확인
									ActionResult param = (ActionResult) frame.getResultData();
									logger.debug("## HDLC_ASSOCIATION_LN Result => {}", param.name());
									if (param == ActionResult.SUCCESS) {
										this.step = Procedure.GET_DISCONNECT_CONTROL;
										result = sendBypass();
									}
								}
								break;
							case GET_RESPONSE:
								// 결과 확인
								Object param = frame.getResultData();
								if (param instanceof DataAccessResult) {
									result = false;
									logger.debug("## [{}]GET_RES_DataAccessResult => {}", step.name(), ((DataAccessResult) param).name());
								} else {
									if (this.step == Procedure.GET_DISCONNECT_CONTROL) {
										// 결과 확인
										//=> UPDATE START 2017.02.20 SP-530
										//long resultData = (Long) param;
										Boolean resultData = (Boolean) param;
										//=> UPDATE END   2017.02.20 SP-530
										logger.debug("## GET_RESIGETER_VALUE => {}", resultData);

										bypassFrameResult.setLastProcedure(Procedure.GET_DISCONNECT_CONTROL);
										//=> UPDATE START 2017.02.20 SP-530
										//bypassFrameResult.setResultValue(resultData);
										if ( resultData ){
											bypassFrameResult.setResultValue("Connected (true)");
										}else{
											bypassFrameResult.setResultValue("Disonnected (false)");
										}
										//=> UPDATE END   2017.02.20 SP-530

										result = true;

										this.step = Procedure.HDLC_DISC;
										result = sendBypass();
									}
								}
								break;
							default:
								break;
							}
						// -> UPDATE START 2016/09/14 SP-117
						/*
						} else if (command.equals("cmdActRelayState")) {
							switch (HdlcObjectType.getItem(DataUtil.getByteToInt(frame.getType()))) {
							case ACTION_RES:
								if (this.step == Procedure.HDLC_ASSOCIATION_LN) {
									// 결과 확인
									ActionResult param = (ActionResult) frame.getResultData();
									logger.debug("## HDLC_ASSOCIATION_LN Result => {}", param.name());
									if (param == ActionResult.SUCCESS) {
										this.step = Procedure.ACTION_DISCONNECT_CONTROL;
										result = sendBypass();
									}
								}
								else if ( this.step  == Procedure.ACTION_DISCONNECT_CONTROL){
									logger.debug("## ACTION_DISCONNECT_CONTROL Result => {}", frame.getResultData());
							
									if ( frame.getResultData() instanceof ActionResult){// != ActionResult.SUCCESS
										bypassFrameResult.setResultValue(frame.getResultData());
										bypassFrameResult.addResultValue("status",frame.getResultData() );
									}
									else if (frame.getResultData() instanceof HashMap ){ // ActionResult.SUCCESS
										HashMap<String,Object> param = (HashMap<String,Object>) frame.getResultData();
										bypassFrameResult.setResultValue(param.get("status"));
										bypassFrameResult.addResultValue("status", param.get("status"));
										if (  param.get("value") != null ){
											bypassFrameResult.addResultValue("value",  param.get("value"));
										}
									}
			
									bypassFrameResult.setLastProcedure(Procedure.ACTION_DISCONNECT_CONTROL);
									result = true;
									this.step = Procedure.HDLC_DISC;
									result = sendBypass();
								}
								break;
							case GET_RES:
								// 결과 확인
								Object param = frame.getResultData();
								if (param instanceof DataAccessResult) {
									result = false;
									logger.debug("## [{}]GET_RES_DataAccessResult => {}", step.name(), ((DataAccessResult) param).name());
								} else {
									if (this.step == Procedure.ACTION_DISCONNECT_CONTROL) {
										// 결과 확인
										long resultData = (Long) param;
										logger.debug("## GET_RESIGETER_VALUE => {}", resultData);
							
										bypassFrameResult.setLastProcedure(Procedure.ACTION_DISCONNECT_CONTROL);
										bypassFrameResult.setResultValue(resultData);
							
										result = true;
							
										this.step = Procedure.HDLC_DISC;
										result = sendBypass();
									}
								}
								break;	
							default:
								break;
							}
						*/
						/* */ // >>>>>>>>>>
						} else if (command.equals("cmdActRelayState")) {
							//switch (HdlcObjectType.getItem(DataUtil.getByteToInt(frame.getType()))) {
							switch (frame.getDlmsApdu()) {
							case ACTION_RESPONSE:
								if (this.step == Procedure.HDLC_ASSOCIATION_LN) {
									// 결과 확인
									ActionResult param = (ActionResult) frame.getResultData();
									logger.debug("## HDLC_ASSOCIATION_LN Result => {}", param.name());
									if (param == ActionResult.SUCCESS) {
										this.step = Procedure.ACTION_DISCONNECT_CONTROL;
										result = sendBypass();
									}
								} else if (this.step == Procedure.ACTION_DISCONNECT_CONTROL) {
									logger.debug("## ACTION_DISCONNECT_CONTROL Result => {}", frame.getResultData());

									if (frame.getResultData() instanceof ActionResult) {// != ActionResult.SUCCESS
										pushOptionalData("ActRelayStatus", frame.getResultData());

										bypassFrameResult.setLastProcedure(Procedure.ACTION_DISCONNECT_CONTROL);
										result    = true;
										this.step = Procedure.HDLC_DISC;
									} else if (frame.getResultData() instanceof HashMap) { // ActionResult.SUCCESS
										HashMap<String, Object> param = (HashMap<String, Object>) frame.getResultData();
										pushOptionalData("ActRelayStatus", param.get("status"));

										// check RelayOn or RelayOff
										String vl = (String) params.get("value");
										if (vl.equals("true")) {
											logger.debug("## ActRelay [ON]");
											pushOptionalData("ActRelayOnOff", true);
										} else {
											logger.debug("## ActRelay [OFF]");
											pushOptionalData("ActRelayOnOff", false);
										}

										// Set Next(RelayStatus)
										params.put("attributeNo", String.valueOf(DLMS_CLASS_ATTR.REGISTER_ATTR02.getAttr()));
										params.put("dataType", null);
										params.put("value", null);

										result    = true;
										this.step = Procedure.GET_REGISTER_VALUE;
									} else {
										bypassFrameResult.setLastProcedure(Procedure.ACTION_DISCONNECT_CONTROL);
										result    = true;
										this.step = Procedure.HDLC_DISC;
									}
									result = sendBypass();
								}
								break;

							case GET_RESPONSE:
								// 결과 확인
								Object param = frame.getResultData();
								if (param instanceof DataAccessResult) {
									result = false;
									logger.debug("## [{}]GET_RES_DataAccessResult => {}", step.name(), ((DataAccessResult) param).name());
								} else {
									if (this.step == Procedure.GET_REGISTER_VALUE) {
										// Relay Status
										Boolean resultData = (Boolean) param;
										logger.debug("## GET_RESIGETER_VALUE => {}", resultData);

										bypassFrameResult.setResultValue(optionalData.get("ActRelayStatus"));
										bypassFrameResult.addResultValue("status", optionalData.get("ActRelayStatus"));
										// -> UPDATE START 2016/09/20 SP-117
										// bypassFrameResult.addResultValue( "value" , resultData );
										if (resultData == true) {
											bypassFrameResult.addResultValue("Relay Status", RELAY_STATUS_KAIFA.Connected);
										} else {
											bypassFrameResult.addResultValue("Relay Status", RELAY_STATUS_KAIFA.Disconnected);
										}
										bypassFrameResult.addResultValue("ActRelayOnOff", optionalData.get("ActRelayOnOff"));
										// <- UPDATE END   2016/09/20 SP-117
										bypassFrameResult.setLastProcedure(Procedure.GET_REGISTER_VALUE);
										result    = true;
										this.step = Procedure.HDLC_DISC;
										result    = sendBypass();
									}
								}
								break;
							default:
								break;
							}
						/* */ // <<<<<<<<<<
						// <- UPDATE END   2016/09/14 SP-117
							
						} else if (command.equals("cmdActSlaveInstall")) {
							//switch (HdlcObjectType.getItem(DataUtil.getByteToInt(frame.getType()))) {
							switch (frame.getDlmsApdu()) {
							case ACTION_RESPONSE:
								if (this.step == Procedure.HDLC_ASSOCIATION_LN) {
									// 결과 확인
									ActionResult param = (ActionResult) frame.getResultData();
									logger.debug("## HDLC_ASSOCIATION_LN Result => {}", param.name());
									if (param == ActionResult.SUCCESS) {
										this.step = Procedure.ACTION_SLAVE_INSTALL;
										result = sendBypass();
									}
								} else if (this.step == Procedure.ACTION_SLAVE_INSTALL) {
									logger.debug("## ACTION_SLAVE_INSTALL Result => {}", frame.getResultData());

									if (frame.getResultData() instanceof ActionResult) {// != ActionResult.SUCCESS
										bypassFrameResult.setResultValue(frame.getResultData());
									} else if (frame.getResultData() instanceof HashMap) { // ActionResult.SUCCESS
										HashMap<String, Object> param = (HashMap<String, Object>) frame.getResultData();
										bypassFrameResult.setResultValue(param.get("status"));
										bypassFrameResult.addResultValue("status", param.get("status"));
									}

									bypassFrameResult.setLastProcedure(Procedure.ACTION_SLAVE_INSTALL);
									result = true;
									this.step = Procedure.HDLC_DISC;
									result = sendBypass();
								}
								break;

							default:
								break;
							}					
						} else if (command.equals("cmdActSlaveDeInstall")) {
							//switch (HdlcObjectType.getItem(DataUtil.getByteToInt(frame.getType()))) {
							switch (frame.getDlmsApdu()) {
							case ACTION_RESPONSE:
								if (this.step == Procedure.HDLC_ASSOCIATION_LN) {
									// 결과 확인
									ActionResult param = (ActionResult) frame.getResultData();
									logger.debug("## HDLC_ASSOCIATION_LN Result => {}", param.name());
									if (param == ActionResult.SUCCESS) {
										this.step = Procedure.ACTION_SLAVE_DEINSTALL;
										result = sendBypass();
									}
								} else if (this.step == Procedure.ACTION_SLAVE_DEINSTALL) {
									logger.debug("## ACTION_SLAVE_INSTALL Result => {}", frame.getResultData());

									if (frame.getResultData() instanceof ActionResult) {// != ActionResult.SUCCESS
										bypassFrameResult.setResultValue(frame.getResultData());
									} else if (frame.getResultData() instanceof HashMap) { // ActionResult.SUCCESS
										HashMap<String, Object> param = (HashMap<String, Object>) frame.getResultData();
										bypassFrameResult.setResultValue(param.get("status"));
										bypassFrameResult.addResultValue("status", param.get("status"));
									}

									bypassFrameResult.setLastProcedure(Procedure.ACTION_SLAVE_DEINSTALL);
									result = true;
									this.step = Procedure.HDLC_DISC;
									result = sendBypass();
								}
								break;

							default:
								break;
							}					
						} else if (command.equals("cmdActSetEncryptionKey")) {
							//switch (HdlcObjectType.getItem(DataUtil.getByteToInt(frame.getType()))) {
							switch (frame.getDlmsApdu()) {
							case ACTION_RESPONSE:
								if (this.step == Procedure.HDLC_ASSOCIATION_LN) {
									// 결과 확인
									ActionResult param = (ActionResult) frame.getResultData();
									logger.debug("## HDLC_ASSOCIATION_LN Result => {}", param.name());
									if (param == ActionResult.SUCCESS) {
										this.step = Procedure.ACTION_SET_ENCRYPTION_KEY;
										result = sendBypass();
									}
								} else if (this.step == Procedure.ACTION_SET_ENCRYPTION_KEY) {
									logger.debug("## ACTION_DISCONNECT_CONTROL Result => {}", frame.getResultData());

									if (frame.getResultData() instanceof ActionResult) {// != ActionResult.SUCCESS
										bypassFrameResult.setResultValue(frame.getResultData());
									} else if (frame.getResultData() instanceof HashMap) { // ActionResult.SUCCESS
										HashMap<String, Object> param = (HashMap<String, Object>) frame.getResultData();
										bypassFrameResult.setResultValue(param.get("status"));
										bypassFrameResult.addResultValue("status", param.get("status"));
									}

									bypassFrameResult.setLastProcedure(Procedure.ACTION_SET_ENCRYPTION_KEY);
									result = true;
									this.step = Procedure.HDLC_DISC;
									result = sendBypass();
								}
								break;

							default:
								break;
							}
						} else if (command.equals("cmdActTransferKey")) {
							//switch (HdlcObjectType.getItem(DataUtil.getByteToInt(frame.getType()))) {
							switch (frame.getDlmsApdu()) {
							case ACTION_RESPONSE:
								if (this.step == Procedure.HDLC_ASSOCIATION_LN) {
									// 결과 확인
									ActionResult param = (ActionResult) frame.getResultData();
									logger.debug("## HDLC_ASSOCIATION_LN Result => {}", param.name());
									if (param == ActionResult.SUCCESS) {
										this.step = Procedure.ACTION_TRANSFER_KEY;
										result = sendBypass();
									}
								} else if (this.step == Procedure.ACTION_TRANSFER_KEY) {
									logger.debug("## ACTION_DISCONNECT_CONTROL Result => {}", frame.getResultData());

									if (frame.getResultData() instanceof ActionResult) {// != ActionResult.SUCCESS
										bypassFrameResult.setResultValue(frame.getResultData());
									} else if (frame.getResultData() instanceof HashMap) { // ActionResult.SUCCESS
										HashMap<String, Object> param = (HashMap<String, Object>) frame.getResultData();
										bypassFrameResult.setResultValue(param.get("status"));
										bypassFrameResult.addResultValue("status", param.get("status"));
									}

									bypassFrameResult.setLastProcedure(Procedure.ACTION_TRANSFER_KEY);
									result = true;
									this.step = Procedure.HDLC_DISC;
									result = sendBypass();
								}
								break;

							default:
								break;
							}
						}
						/*************************
						 * SORIA MBB, Ethernet, RF Modem / Meter FW OTA
						 */
						else if (command.equals("cmdMeterOTAStart")) {
								//logger.debug("cmdMeterOTAStart Frame Type ==> {}", HdlcObjectType.getItem(DataUtil.getByteToInt(frame.getType())));
								logger.debug("cmdMeterOTAStart Frame Type ==> {}", frame.getDlmsApdu());
								
								//switch (HdlcObjectType.getItem(DataUtil.getByteToInt(frame.getType()))) {
								switch (frame.getDlmsApdu()) {
								case ACTION_RESPONSE:
									if (this.step == Procedure.HDLC_ASSOCIATION_LN) {
										// 결과 확인
										ActionResult param = (ActionResult) frame.getResultData();
										logger.debug("## HDLC_ASSOCIATION_LN Result => {}", param.name());
										if (param == ActionResult.SUCCESS) {
											this.step = Procedure.GET_IMAGE_TRANSFER_ENABLE;
											//result = sendBypass();
											result = ( niTidType == TID_Type.Enable) ? sendBypassWidhNiTid(true) : sendBypass(); //UPDATE SP-722
										}
									} else if (this.step == Procedure.ACTION_IMAGE_TRANSFER_INIT) {
										// 결과 확인
										ActionResult param = (ActionResult) frame.getResultData();
										logger.debug("## ACTION_IMAGE_TRANSFER_INIT => {}", param.name());

										if (param == ActionResult.SUCCESS) {
											this.step = Procedure.GET_IMAGE_TRANSFER_STATUS;
											//result = sendBypass();
											result = ( niTidType == TID_Type.Enable) ? sendBypassWidhNiTid(true) : sendBypass(); //UPDATE SP-722
										}
									} else if (this.step == Procedure.ACTION_IMAGE_BLOCK_TRANSFER) {
										// 결과 확인
										ActionResult param = (ActionResult) frame.getResultData();
										logger.debug("## ACTION_IMAGE_BLOCK_TRANSFER => {}", param.name());

										int count = purgeTransferImageTimer();
										logger.debug("## Block Transfer RetryTime purge. puged task count = {}", count);
										
										logger.debug("## Block RemainPacketLength.  = {}", remainPackateLength);

										if (param == ActionResult.SUCCESS) {
											/*
											 * 성공응답을 받은뒤 전송할 다음블럭을 세팅한다
											 */
											setNextBlockTrigger();

											/*
											 * 더 보낼 Block이 없을때 처리
											 */
											if (remainPackateLength <= 0) {
												int tempPacketLength = remainPackateLength - packetSize;
												imageBlockNumber--;  // 이미지블럭을 다 보냈기때문에 이미 증가한 이미지 블럭넘버를 보정해준다. Logging처리를 위함.
												logger.info("[ACTION_IMAGE_BLOCK_TRANSFER] Finished !! Image Block Count={}/{}, RemainPacket Size={}", imageBlockNumber, totalImageBlockNumber, (tempPacketLength <= 0 ? 0 : tempPacketLength));
												this.step = Procedure.GET_IMAGE_FIRST_NOT_TRANSFERRED_BLOCK_NUMBER;

												// 다 보내면 타이머 해지
												stopTransferImageTimer();
												logger.debug("## Imange transfer Finished and Stop Timer ==> needImangeBlockTransferRetry={}", needImangeBlockTransferRetry);
											}

											//result = sendBypass();
											result = ( niTidType == TID_Type.Enable) ? sendBypassWidhNiTid(true) : sendBypass(); //UPDATE SP-722
										} else { // 실패시 타이머 해지
											stopTransferImageTimer();
											
											bypassFrameResult.addResultValue("Meter Message", param);
											bypassFrameResult.addResultValue("Progress Rate", progressRate);
											
											logger.debug("## Fail Result 수신시 Timer 해지~! ==> needImangeBlockTransferRetry={}", needImangeBlockTransferRetry);
										}
									} else if (this.step == Procedure.ACTION_IMAGE_VERIFY) {
										// 결과 확인
										ActionResult param = (ActionResult) frame.getResultData();
										logger.debug("## ACTION_IMAGE_VERIFY => {}", param.name());

										if (param == ActionResult.SUCCESS || param == ActionResult.OTHER_REASON) { //SP-985
										
											/*
											 * SORIA KAIFA Meter는 GET_IMAGE_TO_ACTIVATE_INFO 단계를 건너뛴다.
											 * 아래주석 삭제 금지!!!									 * 
											 */
											//this.step = Procedure.GET_IMAGE_TO_ACTIVATE_INFO;
											this.step = Procedure.ACTION_IMAGE_ACTIVATE;  

											//result = sendBypass();
											result = ( niTidType == TID_Type.Enable) ? sendBypassWidhNiTid(true) : sendBypass(); //UPDATE SP-722
										} else if (param == ActionResult.TEMPORARY_FAIL) {
											/*
											 * Image transfer status 체크
											 * Procedure.ACTION_IMAGE_VERIFY를 유지한체 IMAGE_TRANSFER_STATUS 검증 => GET으로 받기때문에 밑에서 처리
											 */
											//byte[] req = frame.encode(HdlcObjectType.GET_REQ, Procedure.GET_IMAGE_TRANSFER_STATUS, null, command);
											
											if (niTidType == TID_Type.Enable){
												setNiTid(true);
											}
											
											//byte[] req = frame.encode(HdlcObjectType.GET_REQ, Procedure.GET_IMAGE_TRANSFER_STATUS, params, command);
											byte[] req = frame.encode(frame.getHDLCFrameType(), XDLMS_APDU.GET_REQUEST, Procedure.GET_IMAGE_TRANSFER_STATUS, params, command);
											
											if (req != null) {
												logger.debug("### [ACTION_IMAGE_VERIFY][GET_IMAGE_TRANSFER_STATUS] HDLC_REQUEST => {}", Hex.decode(req));
												
												this.session.write(req);
												result = true;
											}
											break;
										} else {
											// 나머지는 다 에러.
										}
									} else if (this.step == Procedure.ACTION_IMAGE_ACTIVATE) {
										// 결과 확인
										ActionResult param = (ActionResult) frame.getResultData();
										logger.debug("## ACTION_IMAGE_ACTIVATE => {}", param.name());

										if (param == ActionResult.SUCCESS) {
											logger.debug("### Meter F/W OTA Successful. ###");
											logger.debug("### Meter F/W OTA Successful. ###");
											logger.debug("### Meter F/W OTA Successful. ###");

											bypassFrameResult.setLastProcedure(Procedure.ACTION_IMAGE_ACTIVATE);
											bypassFrameResult.setResultValue("success");

											/*
											 * 종료처리
											 */
											//this.step = Procedure.HDLC_DISC;

											/*
											 * 미터 F/W 버전 갱신
											 */
											Thread.sleep(40000);
											this.step = Procedure.GET_FIRMWARE_VERSION;

											//result = sendBypass();
											result = ( niTidType == TID_Type.Enable) ? sendBypassWidhNiTid(true) : sendBypass(); //UPDATE SP-722
										} else if (param == ActionResult.TEMPORARY_FAIL) {
											/*
											 * 2016-08-06
											 * 현재 SORIA용 KAIFA Meter의 경우 ACTION_IMAGE_ACTIVATE 요청시 TEMPORARY_FAIL로 응답을보내주고
											 * 바로 연결을 끊어버리기 때문에 SUCCESS를 받을수가 없다.
											 * 하지만 테스트해본결과 F/W는 정상적으로 업데이트가 되기 때문에 이 문제가
											 * 해결되기 전까지는 성공한것으로 처리하도록한다.
											 */
											///////////////////////////////////////////////
											bypassFrameResult.setLastProcedure(Procedure.ACTION_IMAGE_ACTIVATE);
											bypassFrameResult.setResultValue("success");
											result = true;
											bypassFrameResult.setFinished(true);
											////////////////////////////////////////////////

											/*
											 * Image transfer status 체크
											 * Procedure.ACTION_IMAGE_ACTIVATE를 유지한체 IMAGE_TRANSFER_STATUS 검증 => GET으로 받기때문에 밑에서 처리
											 */
											//byte[] req = frame.encode(HdlcObjectType.GET_REQ, Procedure.GET_IMAGE_TRANSFER_STATUS, null, command);
											
											if (niTidType == TID_Type.Enable){
												setNiTid(true);
											}
											//byte[] req = frame.encode(HdlcObjectType.GET_REQ, Procedure.GET_IMAGE_TRANSFER_STATUS, params, command);
											byte[] req = frame.encode(frame.getHDLCFrameType(), XDLMS_APDU.GET_REQUEST, Procedure.GET_IMAGE_TRANSFER_STATUS, params, command);
											if (req != null) {
												logger.debug("### [ACTION_IMAGE_ACTIVATE][GET_IMAGE_TRANSFER_STATUS] HDLC_REQUEST => {}", Hex.decode(req));

												this.session.write(req);
												result = true;
											}
											break;
										} else {
											// 나머지는 다 에러.
										}
									}else if (this.step == Procedure.GET_IMAGE_FIRST_NOT_TRANSFERRED_BLOCK_NUMBER) {
										/*
										 * UDP의 경우 패킷이 잘못 전달될수 있다.
										 * 잘못 전달된 패킷을 수신시 블럭 넘버 체크하도록 함.
										 * 마지막 블럭인지 확인
										 */
										logger.warn("### HES received Inadequate DLMS Packet. Try request image first not transferred block number.");
										logger.warn("### HES received Inadequate DLMS Packet. Try request image first not transferred block number.");
										logger.warn("### HES received Inadequate DLMS Packet. Try request image first not transferred block number.");
										
										this.step = Procedure.GET_IMAGE_FIRST_NOT_TRANSFERRED_BLOCK_NUMBER;
										stopTransferImageTimer();
										//result = sendBypass();
										result = ( niTidType == TID_Type.Enable) ? sendBypassWidhNiTid(true) : sendBypass(); //UPDATE SP-722
									}
									break;
								case GET_RESPONSE:
									// 결과 확인
									Object param = frame.getResultData();

									if (param instanceof DataAccessResult) {
										result = false;
										logger.debug("## [{}]GET_RES_DataAccessResult => {}", step.name(), ((DataAccessResult) param).name());
									} else {
										if (this.step == Procedure.GET_IMAGE_TRANSFER_ENABLE) {
											// 결과 확인
											boolean resultData = Boolean.parseBoolean(String.valueOf(param));
											logger.debug("## GET_IMAGE_TRANSFER_ENABLE => {}", resultData);

											if (resultData) {
												this.step = Procedure.GET_IMAGE_BLOCK_SIZE;
											} else {
												this.step = Procedure.SET_IMAGE_TRANSFER_ENABLE;
											}
											//result = sendBypass();
											result = ( niTidType == TID_Type.Enable) ? sendBypassWidhNiTid(true) : sendBypass(); //UPDATE SP-722
										} else if (this.step == Procedure.GET_IMAGE_BLOCK_SIZE) {
											// 결과 확인
											long IMAGE_TRANSFER_BLOCK_SIZE = (Long) param;
											packetSize = (int) IMAGE_TRANSFER_BLOCK_SIZE;
											if ((long) packetSize != IMAGE_TRANSFER_BLOCK_SIZE) {
												logger.error("IMAGE_TRANSFER_BLOCK_SIZE Casting Error to Integer. => {}", IMAGE_TRANSFER_BLOCK_SIZE);
												result = false;
											} else {
												totalImageBlockNumber = fwSize / packetSize;
												if (0 < (fwSize % packetSize)) {
													totalImageBlockNumber++;
												}

												totalImageBlockNumber--; // Loging 처리시 보기 편하도록 블럭번호를 수정해줌.
												
												if (isTakeOverMode) {
													this.step = Procedure.GET_IMAGE_TRANSFER_STATUS;
													isTakeOverCheckStep = true;
												} else {
													this.step = Procedure.ACTION_IMAGE_TRANSFER_INIT;
												}

												logger.debug("## TAKEOVER_MODE => {}, IS_TAKEOVER_MODE_CHECK_STEP =? {}, IMAGE_TRANSFER_BLOCK_SIZE => {}", isTakeOverMode, isTakeOverCheckStep, IMAGE_TRANSFER_BLOCK_SIZE);
												//result = sendBypass();
												result = ( niTidType == TID_Type.Enable) ? sendBypassWidhNiTid(true) : sendBypass(); //UPDATE SP-722
											}
										} else if (this.step == Procedure.GET_IMAGE_TRANSFER_STATUS) {
											// 결과 확인
											Integer resultData = (Integer) param;
											logger.debug("## GET_IMAGE_TRANSFER_STATUS ## IS_TAKEOVER_MODE CHECK_STEP? = {}, StatusCheckValue={}, StatusCheckResult={}", isTakeOverCheckStep, resultData, ImageTransferStatus.getItem(resultData).name());
											
											// 비정상 상태이면 종료
											if(ImageTransferStatus.getItem(resultData) == ImageTransferStatus.IMAGE_UNKNOWN_STATUS){ 
												logger.warn("Unknown status received. send disc.");
												isTakeOverCheckStep = false;
												//this.step = Procedure.HDLC_DISC;										
												this.step = Procedure.ACTION_IMAGE_TRANSFER_INIT;
												
												//bypassFrameResult.setLastProcedure(Procedure.GET_IMAGE_TRANSFER_STATUS);
												//bypassFrameResult.setResultValue(ImageTransferStatus.getItem(resultData).name());
												//bypassFrameResult.setResultState(false);

												result = ( niTidType == TID_Type.Enable) ? sendBypassWidhNiTid(true) : sendBypass(); //UPDATE SP-722
												result = false;
											}
											// 이어받기모드체크 스탭이고 초기화가 되어있지 않으면 초기화 진행
											else if(isTakeOverCheckStep == true && ImageTransferStatus.getItem(resultData) != ImageTransferStatus.IMAGE_TRANSFER_INITIATED){
												isTakeOverCheckStep = false;
												this.step = Procedure.ACTION_IMAGE_TRANSFER_INIT;
												result = ( niTidType == TID_Type.Enable) ? sendBypassWidhNiTid(true) : sendBypass(); //UPDATE SP-722
											}else{
												if (ImageTransferStatus.getItem(resultData) == ImageTransferStatus.IMAGE_TRANSFER_INITIATED) {   // 정상
													this.step = Procedure.GET_IMAGE_FIRST_NOT_TRANSFERRED_BLOCK_NUMBER;
													result = ( niTidType == TID_Type.Enable) ? sendBypassWidhNiTid(true) : sendBypass(); //UPDATE SP-722
												} else if (ImageTransferStatus.getItem(resultData) == ImageTransferStatus.IMAGE_TRANSFER_NOT_INITIATED) {
													this.step = Procedure.ACTION_IMAGE_TRANSFER_INIT;
													result = ( niTidType == TID_Type.Enable) ? sendBypassWidhNiTid(true) : sendBypass(); //UPDATE SP-722
												} else if (ImageTransferStatus.getItem(resultData) == ImageTransferStatus.IMAGE_VERIFICATION_INITIATED) {
													this.step = Procedure.ACTION_IMAGE_VERIFY;
													result = verificationCheckRetry();
												} else if (ImageTransferStatus.getItem(resultData) == ImageTransferStatus.IMAGE_VERIFICATION_SUCCESSFUL) {
												// this.step = Procedure.GET_IMAGE_TO_ACTIVATE_INFO;
													this.step = Procedure.ACTION_IMAGE_ACTIVATE;  // SORIA KAIFA Meter는 GET_IMAGE_TO_ACTIVATE_INFO 단계를 건너뛴다.
													result = ( niTidType == TID_Type.Enable) ? sendBypassWidhNiTid(true) : sendBypass(); //UPDATE SP-722
												} else if (ImageTransferStatus.getItem(resultData) == ImageTransferStatus.IMAGE_ACTIVATION_INITIATED) {
													this.step = Procedure.ACTION_IMAGE_ACTIVATE;
													result = activationCheckRetry();
												} else if (ImageTransferStatus.getItem(resultData) == ImageTransferStatus.IMAGE_ACTIVATION_SUCCESSFUL) {
													this.step = Procedure.HDLC_DISC;
													result = ( niTidType == TID_Type.Enable) ? sendBypassWidhNiTid(true) : sendBypass(); //UPDATE SP-722
												}
											}

										} else if (this.step == Procedure.GET_IMAGE_FIRST_NOT_TRANSFERRED_BLOCK_NUMBER) {
											/*
											 * 마지막 블럭인지 확인
											 */
											// 결과 확인
											long resultData = (Long) param;
											int firstNotTransferredBlockNumber = (int) resultData;
											if ((long) firstNotTransferredBlockNumber != resultData) {
												logger.error("IMAGE_FIRST_NOT_TRANSFERRED_BLOCK_NUMBER Casting Error to Integer. => {}", resultData);
												result = false;
											} else {
												//if (totalImageBlockNumber <= firstNotTransferredBlockNumber) {
												if (totalImageBlockNumber < firstNotTransferredBlockNumber) {
													this.step = Procedure.ACTION_IMAGE_VERIFY;
													logger.debug("## GET_IMAGE_FIRST_NOT_TRANSFERRED_BLOCK_NUMBER : Last block = {}, Not transferred block = {}", imageBlockNumber, firstNotTransferredBlockNumber);

													
													String meterId = frame.getMeterId();
													MeterDao meterDao = DataUtil.getBean(MeterDao.class);
													Meter meter = meterDao.get(meterId);
													
													
													/*
													 * OTA Download Event save.
													 */
													String openTime = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss");
													EV_PH_200_63_0_Action action2 = new EV_PH_200_63_0_Action();
													action2.makeEvent(TargetClass.EnergyMeter, meterId, TargetClass.EnergyMeter, openTime, "HES", meter.getLocation());
													action2.updateOTAHistory(meterId, DeviceType.Meter, openTime);
													
													//result = sendBypass();
													result = ( niTidType == TID_Type.Enable) ? sendBypassWidhNiTid(true) : sendBypass(); //UPDATE SP-722
												}
												/**
												 * firstNotTransferredBlockNumber 확인시 더보내야할 블럭이 있을경우
												 * 미전송 블럭부터 전송.
												 */
												else {
													/** NullBypass 방식인경우만 사용 */
													if(niTidType == TID_Type.Disable){
														blockTransferRetryTimer = new Timer(true);  	// Timer 생성
														logger.debug("Block Transfer Timer Create...." + timerCreateFlag++);												
													}
													
													this.step = Procedure.ACTION_IMAGE_BLOCK_TRANSFER;

													imageBlockNumber = firstNotTransferredBlockNumber;
													offset = firstNotTransferredBlockNumber * packetSize;
													remainPackateLength = fwSize - offset;

													logger.warn("###### Image not transferred block is exist  ==> totalImageBlockNumber={}, firstNotTransferredBlockNumber={}, packetSize={}, offset={}" + ",  remainPackateLength={}", totalImageBlockNumber, firstNotTransferredBlockNumber, packetSize, offset, remainPackateLength);
													//result = sendBypass();
													result = ( niTidType == TID_Type.Enable) ? sendBypassWidhNiTid(true) : sendBypass(); //UPDATE SP-722
												}
											}
										} else if (this.step == Procedure.ACTION_IMAGE_VERIFY) {
											// 결과 확인
											Integer resultData = (Integer) param;
											ImageTransferStatus status = ImageTransferStatus.getItem(resultData);
											logger.debug("## GET_IMAGE_TRANSFER_STATUS => {}", status);

											if (status == ImageTransferStatus.IMAGE_VERIFICATION_SUCCESSFUL) {
											//										this.step = Procedure.GET_IMAGE_TO_ACTIVATE_INFO;

												this.step = Procedure.ACTION_IMAGE_ACTIVATE;  // SORIA KAIFA Meter는 GET_IMAGE_TO_ACTIVATE_INFO 단계를 건너뛴다.
												//result = sendBypass();
												result = ( niTidType == TID_Type.Enable) ? sendBypassWidhNiTid(true) : sendBypass(); //UPDATE SP-722
											}
											/*
											 * 초기화중... 30초간 대기후 재시도. 3회 실시.
											 */
											else if (status == ImageTransferStatus.IMAGE_VERIFICATION_INITIATED && verificationRetryCount < NEED_IMAGE_BLOCK_TRANSFER_MAX_RETRY_COUNT) {
												result = verificationCheckRetry();
											}
											/*
											 * status == ImageTransferStatus.IMAGE_VERIFICATION_FAILED)
											 */
											else {
												// Image Verify 실패
												logger.debug("ACTION_IMAGE_VERIFY 검증 실패");
												logger.debug("ACTION_IMAGE_VERIFY 검증 실패");
												logger.debug("ACTION_IMAGE_VERIFY 검증 실패");
											}
										} else if (this.step == Procedure.ACTION_IMAGE_ACTIVATE) {
											// 결과 확인
											Integer resultData = (Integer) param;
											ImageTransferStatus status = ImageTransferStatus.getItem(resultData);
											logger.debug("## GET_IMAGE_TRANSFER_STATUS => {}", status);

											if (status == ImageTransferStatus.IMAGE_ACTIVATION_SUCCESSFUL) {
												logger.debug("### Meter F/W OTA Successful. ###");
												logger.debug("### Meter F/W OTA Successful. ###");
												logger.debug("### Meter F/W OTA Successful. ###");

												bypassFrameResult.setLastProcedure(Procedure.ACTION_IMAGE_ACTIVATE);
												bypassFrameResult.setResultValue("success");

												/*
												 * 종료처리
												 */
												//this.step = Procedure.HDLC_DISC;

												/*
												 * 미터 F/W 버전 갱신
												 */
												Thread.sleep(50000);
												this.step = Procedure.GET_FIRMWARE_VERSION;

												//result = sendBypass();
												result = ( niTidType == TID_Type.Enable) ? sendBypassWidhNiTid(true) : sendBypass(); //UPDATE SP-722
											}
											/*
											 * 초기화중... 30초간 대기후 재시도. 3회 실시.
											 */
											else if (status == ImageTransferStatus.IMAGE_ACTIVATION_INITIATED && verificationRetryCount < NEED_IMAGE_BLOCK_TRANSFER_MAX_RETRY_COUNT) {
												result = activationCheckRetry();
											}
											/*
											 * status == ImageTransferStatus.IMAGE_ACTIVATION_FAILED)
											 */
											else {
												// Image Activation 실패
												logger.debug("ACTION_IMAGE_ACTIVATE 검증 실패");
											}
										} 
										/* SORIA KAIFA Meter는 GET_IMAGE_TO_ACTIVATE_INFO 단계를 건너뛴다.
										 * 주석 삭제 하지 말것 !!!
										 * 주석 삭제 하지 말것 !!!
										else if (this.step == Procedure.GET_IMAGE_TO_ACTIVATE_INFO) {
											// 결과 확인
											HashMap<String, Object> resultData = (HashMap<String, Object>) param;

											if (resultData != null && 0 < resultData.size()) {
												long image_to_activate_size = Long.parseLong(String.valueOf(resultData.get("image_to_activate_size")));
												byte[] image_to_activate_identification = (byte[]) resultData.get("image_to_activate_identification");

												logger.debug("## GET_IMAGE_TO_ACTIVATE_INFO => image_to_activate_size - Send = {}, Receive = {}", fwSize, image_to_activate_size);
												logger.debug("## GET_IMAGE_TO_ACTIVATE_INFO => image_to_activate_identification - Send = {}, Receive = {}", imageIdentifier, DataUtil.getString(image_to_activate_identification));

												if (resultData.containsKey("image_to_activate_signature")) {
													byte[] image_to_activate_signature = (byte[]) resultData.get("image_to_activate_signature");
													logger.debug("## GET_IMAGE_TO_ACTIVATE_INFO => image_to_activate_signature - {}", Hex.decode(image_to_activate_signature));
												}

												// 검증
												if (image_to_activate_size == fwSize && imageIdentifier.equals(DataUtil.getString(image_to_activate_identification))) {
													this.step = Procedure.ACTION_IMAGE_ACTIVATE;

													result = sendBypass();
												} else {
													logger.debug("## IMAGE_TO_ACTIVATE _INFO - Validation Fail.");
												}
											}
										} 
										*/	
										else if (this.step == Procedure.GET_FIRMWARE_VERSION) {
											// 결과 확인
											String resultData = (String) param;
											if (!resultData.equals("")) {
												logger.debug("## GET_FIRMWARE_VERSION => {}", resultData);

												bypassFrameResult.setLastProcedure(Procedure.GET_FIRMWARE_VERSION);
												bypassFrameResult.setResultValue(resultData);

												result = true;
											}

											this.step = Procedure.HDLC_DISC;
											//result = sendBypass();
											result = ( niTidType == TID_Type.Enable) ? sendBypassWidhNiTid(true) : sendBypass(); //UPDATE SP-722
										}

									}

									break;
								case SET_RESPONSE:

									if (this.step == Procedure.SET_IMAGE_TRANSFER_ENABLE) {
										// 결과 확인
										DataAccessResult daResult = (DataAccessResult) frame.getResultData();
										logger.debug("## SET_IMAGE_TRANSFER_ENABLE => {}", daResult.name());

										if (daResult == DataAccessResult.SUCCESS) {
											this.step = Procedure.GET_IMAGE_BLOCK_SIZE;
											//result = sendBypass();
											result = ( niTidType == TID_Type.Enable) ? sendBypassWidhNiTid(true) : sendBypass(); //UPDATE SP-722
										}
									}
									
								default:
									 //HdlcObjectType frameType = HdlcObjectType.getItem(DataUtil.getByteToInt(frame.getType()));
									 
									// 아래 코드 UDP환경에서 에러나서 주석처리함.
//								    logger.debug("HdlcObjectType frameType is  {}", HdlcObjectType.getItem(DataUtil.getByteToInt(frame.getType())));
//								    logger.debug("Try to request empty packet to receive again");
//								    // return null;
//								    this.session.write(new byte[]{});
//								    result = true;
									
									/*
									 * 미터에서 보낸 응답 메시지를 수신하지 못해서 동일한 블럭을 재전송한경우
									 * 보내지 못한 블럭 넘버 체크후 재전송하도록 한다.
									 */
									//if(this.step == Procedure.ACTION_IMAGE_BLOCK_TRANSFER && frameType == HdlcObjectType.UNKNOWN){
									 if(this.step == Procedure.ACTION_IMAGE_BLOCK_TRANSFER && frame.getDlmsApdu() == XDLMS_APDU.NULL){
										logger.debug("### Unknown frame received. Check Image first not transferred block number ###");
										logger.debug("### Unknown frame received. Check Image first not transferred block number ###");
										logger.debug("### Unknown frame received. Check Image first not transferred block number ###");
										
										this.step = Procedure.GET_IMAGE_FIRST_NOT_TRANSFERRED_BLOCK_NUMBER;
										stopTransferImageTimer();
										//result = sendBypass();
										result = ( niTidType == TID_Type.Enable) ? sendBypassWidhNiTid(true) : sendBypass(); //UPDATE SP-722
									//}else if(this.step == Procedure.GET_IMAGE_FIRST_NOT_TRANSFERRED_BLOCK_NUMBER && frameType == HdlcObjectType.UNKNOWN){
									 }else if(this.step == Procedure.GET_IMAGE_FIRST_NOT_TRANSFERRED_BLOCK_NUMBER && frame.getDlmsApdu() == XDLMS_APDU.NULL){
										logger.debug("### SEND NEXT BLOCK ~~!! ###");
										logger.debug("### SEND NEXT BLOCK ~~!! ###");
										logger.debug("### SEND NEXT BLOCK ~~!! ###");
										
										if( niTidType == TID_Type.Disable){
											int count = purgeTransferImageTimer();
											logger.debug("## Block Transfer RetryTime purge. puged task count = {}", count);
											
										}

										setNextBlockTrigger(true);

										this.step = Procedure.ACTION_IMAGE_BLOCK_TRANSFER;
										//result = sendBypass();
										result = ( niTidType == TID_Type.Enable) ? sendBypassWidhNiTid(true) : sendBypass(); //UPDATE SP-722
									}

									/*
									 * 미터에서 보낸 응답 메시지를 수신하지 못해서 동일한 블럭을 재전송한경우
									 * 다음 블럭을 전송하도록 한다.
									 */
//									if(this.step == Procedure.ACTION_IMAGE_BLOCK_TRANSFER && frameType == HdlcObjectType.UNKNOWN){
//										logger.debug("### SEND NEXT BLOCK ~~!! ###");
//										logger.debug("### SEND NEXT BLOCK ~~!! ###");
//										logger.debug("### SEND NEXT BLOCK ~~!! ###");
//										
//										needImangeBlockTransferRetry = false;
//										blockTransferRetryTask.cancel();
//										int temp = blockTransferRetryTimer.purge();
//										logger.debug("##퍼지 됬음.  ==>> {}", temp);
//										
//										setNextBlockTrigger();
//										
//										/*
//										 * 더 보낼 Block이 없을때 처리
//										 */
//										if (remainPackateLength <= 0) {
//											logger.info("[ACTION_IMAGE_BLOCK_TRANSFER] Finished !! Image Block Count={}/{}, RemainPacket Size={}", imageBlockNumber, totalImageBlockNumber, (remainPackateLength - packetSize));
//											this.step = Procedure.GET_IMAGE_FIRST_NOT_TRANSFERRED_BLOCK_NUMBER;
		//
//											// 다 보내면 타이머 해지
//											stopTransferImageTimer();
//											logger.debug("## Timer 다보낸뒤 해지~! ==> needImangeBlockTransferRetry={}", needImangeBlockTransferRetry);
//										}
		//
//										result = sendBypass();								
//									}
								}
						} else if (command.equals("cmdGetMeterFWVersion")) {  
							logger.debug("### 여기 호출 1 ###");
								//switch (HdlcObjectType.getItem(DataUtil.getByteToInt(frame.getType()))) {
							switch (frame.getDlmsApdu()) {
								case GLO_ACTION_RESPONSE:
								case ACTION_RESPONSE:
									if (this.step == Procedure.HDLC_ASSOCIATION_LN) {
										logger.debug("### 여기 호출 2 ###");
										// 결과 확인
										ActionResult param = (ActionResult) frame.getResultData();
										logger.debug("## HDLC_ASSOCIATION_LN Result => {}", param.name());
										if (param == ActionResult.SUCCESS) {
											this.step = Procedure.GET_FIRMWARE_VERSION;
											result = ( niTidType == TID_Type.Enable) ? sendBypassWidhNiTid(true) : sendBypass(); //UPDATE SP-722
										}
									}
									break;
								case GLO_GET_RESPONSE:
								case GET_RESPONSE:
									// 결과 확인
									Object param = frame.getResultData();
									logger.debug("### 여기 호출 3 => {} ###", param.toString());
									
									if (param instanceof DataAccessResult) {
										logger.debug("### 여기 호출 4 ###");
										result = false;
										logger.debug("## [{}]GET_RES_DataAccessResult => {}", step.name(), ((DataAccessResult) param).name());
									} else {
										logger.debug("### 여기 호출 5 ###");
										if (this.step == Procedure.GET_FIRMWARE_VERSION) {
											logger.debug("### 여기 호출 6 ###");
											// 결과 확인
											String resultData = (String) param;
											if (!resultData.equals("")) {
												logger.debug("## GET_FIRMWARE_VERSION => {}", resultData);

												bypassFrameResult.setLastProcedure(Procedure.GET_FIRMWARE_VERSION);
												bypassFrameResult.setResultValue(resultData);

												result = true;
											}

											this.step = Procedure.HDLC_DISC;
											result = ( niTidType == TID_Type.Enable) ? sendBypassWidhNiTid(true) : sendBypass(); //UPDATE SP-722
											
											logger.debug("### 여기 호출 7 ###");
										}
									}
									break;
								default:
									break;
								}
						}else if (command.equals("cmdSORIAGetMeterKey")) {
							//switch (HdlcObjectType.getItem(DataUtil.getByteToInt(frame.getType()))) {
							switch (frame.getDlmsApdu()) {
							case KAIFA_CUSTOM:
								if (this.step == Procedure.GET_SORIA_METER_KEY_A) {
									// 결과 확인
									Object param = frame.getResultData();
									logger.debug("## GET_SORIA_METER_KEY_A => {}", param);

									if (param != null) {
										bypassFrameResult.setLastProcedure(Procedure.GET_SORIA_METER_KEY_A);
										bypassFrameResult.addResultValue(Procedure.GET_SORIA_METER_KEY_A.name(), String.valueOf(param));

										result = true;
									}
									this.step = Procedure.GET_SORIA_METER_KEY_B;
									result = ( niTidType == TID_Type.Enable) ? sendBypassWidhNiTid(true) : sendBypass(); //UPDATE SP-722
								}else if(this.step == Procedure.GET_SORIA_METER_KEY_B) {
									// 결과 확인
									Object param = frame.getResultData();
									logger.debug("## GET_SORIA_METER_KEY_B => {}", param);

									if (param != null) {
										bypassFrameResult.setLastProcedure(Procedure.GET_SORIA_METER_KEY_B);
										bypassFrameResult.addResultValue(Procedure.GET_SORIA_METER_KEY_B.name(), String.valueOf(param));

										result = true;
									}
									this.step = Procedure.GET_SORIA_METER_KEY_C;
									result = ( niTidType == TID_Type.Enable) ? sendBypassWidhNiTid(true) : sendBypass(); //UPDATE SP-722
								}else if(this.step == Procedure.GET_SORIA_METER_KEY_C) {
									// 결과 확인
									Object param = frame.getResultData();
									logger.debug("## GET_SORIA_METER_KEY_C => {}", param);

									if (param != null) {
										bypassFrameResult.setLastProcedure(Procedure.GET_SORIA_METER_KEY_C);
										bypassFrameResult.addResultValue(Procedure.GET_SORIA_METER_KEY_C.name(), String.valueOf(param));

										result = true;
									}
									
									this.step = Procedure.HDLC_DISC;
									result = ( niTidType == TID_Type.Enable) ? sendBypassWidhNiTid(true) : sendBypass(); //UPDATE SP-722
								}
								break;
							default:
								break;
							}
						}else if (command.equals("cmdSORIASetMeterSerial")) {
							//switch (HdlcObjectType.getItem(DataUtil.getByteToInt(frame.getType()))) {
							switch (frame.getDlmsApdu()) {
							case KAIFA_CUSTOM:
								if (this.step == Procedure.SET_SORIA_METER_SERIAL_A) {
									// 결과 확인
									Object param = frame.getResultData();
									logger.debug("## SET_SORIA_METER_SERIAL_A => {}", param);

									if (param != null) {
										bypassFrameResult.setLastProcedure(Procedure.SET_SORIA_METER_SERIAL_A);
										bypassFrameResult.addResultValue(Procedure.SET_SORIA_METER_SERIAL_A.name(), String.valueOf(param));

										result = true;
									}
									this.step = Procedure.SET_SORIA_METER_SERIAL_B;
									result = ( niTidType == TID_Type.Enable) ? sendBypassWidhNiTid(true) : sendBypass(); //UPDATE SP-722
								}else if(this.step == Procedure.SET_SORIA_METER_SERIAL_B) {
									// 결과 확인
									Object param = frame.getResultData();
									logger.debug("## SET_SORIA_METER_SERIAL_B => {}", param);

									if (param != null) {
										bypassFrameResult.setLastProcedure(Procedure.SET_SORIA_METER_SERIAL_B);
										bypassFrameResult.addResultValue(Procedure.SET_SORIA_METER_SERIAL_B.name(), String.valueOf(param));
										
										String val = String.valueOf(param);
										byte[] byteVal = Hex.encode(val);
										
										if(val.indexOf("020B")>=0 && byteVal.length == 20) {
											logger.info("MeterSerialA="+new String(DataUtil.select(byteVal, 4, 16)));
											params.put("meterSerial", new String(DataUtil.select(byteVal, 4, 16)));
											result = true;
										}else {
											logger.error("Invalid Serial="+val);
											result = false;
										}
									}
									this.step = Procedure.SET_SORIA_METER_SERIAL_C;							
									result = ( niTidType == TID_Type.Enable) ? sendBypassWidhNiTid(true) : sendBypass(); //UPDATE SP-722v
								}else if(this.step == Procedure.SET_SORIA_METER_SERIAL_C) {
									// 결과 확인
									Object param = frame.getResultData();
									logger.debug("## SET_SORIA_METER_SERIAL_C => {}", param);

									if (param != null) {
										bypassFrameResult.setLastProcedure(Procedure.SET_SORIA_METER_SERIAL_C);
										bypassFrameResult.addResultValue(Procedure.SET_SORIA_METER_SERIAL_C.name(), String.valueOf(param));

										result = true;
									}
									this.step = Procedure.SET_SORIA_METER_SERIAL_D;
									result = ( niTidType == TID_Type.Enable) ? sendBypassWidhNiTid(true) : sendBypass(); //UPDATE SP-722
								}else if(this.step == Procedure.SET_SORIA_METER_SERIAL_D) {
									// 결과 확인
									Object param = frame.getResultData();
									logger.debug("## SET_SORIA_METER_SERIAL_D => {}", param);

									if (param != null) {
										bypassFrameResult.setLastProcedure(Procedure.SET_SORIA_METER_SERIAL_D);
										bypassFrameResult.addResultValue(Procedure.SET_SORIA_METER_SERIAL_D.name(), String.valueOf(param));

										String val = String.valueOf(param);
										byte[] byteVal = Hex.encode(val);
										
										if(val.indexOf("020C")>=0 && byteVal.length == 20) {
											result = true;
										}else {
											logger.error("Invalid SerialB="+val);
											result = false;
										}
									}
									this.step = Procedure.SET_SORIA_METER_SERIAL_E;
									result = ( niTidType == TID_Type.Enable) ? sendBypassWidhNiTid(true) : sendBypass(); //UPDATE SP-722
								}else if(this.step == Procedure.SET_SORIA_METER_SERIAL_E) {
									// 결과 확인
									Object param = frame.getResultData();
									logger.debug("## SET_SORIA_METER_SERIAL_E => {}", param);

									if (param != null) {
										bypassFrameResult.setLastProcedure(Procedure.SET_SORIA_METER_SERIAL_E);
										bypassFrameResult.addResultValue(Procedure.SET_SORIA_METER_SERIAL_E.name(), String.valueOf(param));

										result = true;
									}
									
									this.step = Procedure.HDLC_DISC;
									result = ( niTidType == TID_Type.Enable) ? sendBypassWidhNiTid(true) : sendBypass(); //UPDATE SP-722
								}
								break;
							default:
								break;
							}			
						} else if (command.equals("cmdSetValue")) {
								//switch (HdlcObjectType.getItem(DataUtil.getByteToInt(frame.getType()))) {
							switch (frame.getDlmsApdu()) {
								case ACTION_RESPONSE:
									if (this.step == Procedure.HDLC_ASSOCIATION_LN) {
										// 결과 확인
										ActionResult param = (ActionResult) frame.getResultData();
										logger.debug("## HDLC_ASSOCIATION_LN Result => {}", param.name());
										if (param == ActionResult.SUCCESS) {
											this.step = Procedure.SET_VALUE;
											result = sendBypass();
										}
									}
									break;
								case SET_RESPONSE:
									// 결과 확인
									DataAccessResult param = (DataAccessResult) frame.getResultData();
									logger.debug("## SET_RESIGETER_VALUE => {}", param.name());

									if (param == DataAccessResult.SUCCESS) {
										bypassFrameResult.setLastProcedure(Procedure.SET_VALUE);
										bypassFrameResult.setResultValue("Success");
										result = true;

										this.step = Procedure.HDLC_DISC;
										result = sendBypass();
									}
									break;
								default:
									break;
								}
							} else if (command.equals("cmdGetValue")) {
								//switch (HdlcObjectType.getItem(DataUtil.getByteToInt(frame.getType()))) {
								switch (frame.getDlmsApdu()) {
								case ACTION_RESPONSE:
									if (this.step == Procedure.HDLC_ASSOCIATION_LN) {
										// 결과 확인
										ActionResult param = (ActionResult) frame.getResultData();
										logger.debug("## HDLC_ASSOCIATION_LN Result => {}", param.name());
										if (param == ActionResult.SUCCESS) {
											this.step = Procedure.GET_VALUE;
											result = sendBypass();
										}
									}
									break;
								case GET_RESPONSE:
									// 결과 확인
									Object param = frame.getResultData();
									if (param instanceof DataAccessResult) {
										result = false;
										logger.debug("## [{}]GET_RES_DataAccessResult => {}", step.name(), ((DataAccessResult) param).name());
									} else {
										if (this.step == Procedure.GET_VALUE) {
											logger.debug("## GET_RESIGETER_VALUE => {}", param.toString());

											bypassFrameResult.setLastProcedure(Procedure.GET_VALUE);
											bypassFrameResult.setResultValue(param.toString());

											result = true;

											this.step = Procedure.HDLC_DISC;
											result = sendBypass();
										}
									}
									break;
								default:
									break;
								}
							// -> INSERT START 2016/08/24 SP117
							} else if (command.equals("cmdGetRelayStatusAll")) {
								//switch (HdlcObjectType.getItem(DataUtil.getByteToInt(frame.getType()))) {
								switch (frame.getDlmsApdu()) {
								case ACTION_RESPONSE:
									if (this.step == Procedure.HDLC_ASSOCIATION_LN) {
										ActionResult param = (ActionResult) frame.getResultData();
										logger.debug("## HDLC_ASSOCIATION_LN Result => {}", param.name());
										if (param == ActionResult.SUCCESS) {
											// this.step = Procedure.GET_DISCONNECT_CONTROL;
											this.step = Procedure.GET_REGISTER_VALUE;
											result = sendBypass();
										}
									}
									break;
								case GET_RESPONSE:
									Object param = frame.getResultData();
									if (param instanceof DataAccessResult) {
										result = false;
										logger.debug("## [{}]GET_RES_DataAccessResult => {}", step.name(), ((DataAccessResult) param).name());
									} else {
										// if (this.step == Procedure.GET_DISCONNECT_CONTROL) {
										if (this.step == Procedure.GET_REGISTER_VALUE) {
											// long resultData = (Long) param;
											// logger.debug("## GET_REGISTER_VALUE => {}", resultData);
											int attributeno = 0;

										attributeno = Integer.parseInt(params.get("attributeNo").toString());
										if (attributeno == DLMS_CLASS_ATTR.REGISTER_ATTR02.getAttr()) {
												// Relay Status
											Boolean resultData = (Boolean) param;
												logger.debug("## GET_REGISTER_VALUE => {}", resultData);

											logger.debug("## next step = GET_REGISTER_VALUE(RELAY LOAD CONTROL STATE)");
												pushOptionalData("RelayStatus", resultData);
											params.put("attributeNo", String.valueOf(DLMS_CLASS_ATTR.REGISTER_ATTR03.getAttr()));
												result = true;
												this.step = Procedure.GET_REGISTER_VALUE;

										} else if (attributeno == DLMS_CLASS_ATTR.REGISTER_ATTR03.getAttr()) {
												// Relay Load Control State
											Integer resultData = (Integer) param;
												logger.debug("## GET_REGISTER_VALUE => {}", resultData);

												pushOptionalData("LoadControlStatus", resultData);
											logger.debug("## next step = GET_REGISTER_VALUE(RELAY LOAD CONTROL MODE)");
											params.put("attributeNo", String.valueOf(DLMS_CLASS_ATTR.REGISTER_ATTR04.getAttr()));
												result = true;
												this.step = Procedure.GET_REGISTER_VALUE;

										} else if (attributeno == DLMS_CLASS_ATTR.REGISTER_ATTR04.getAttr()) {
												// Relay Load Control Mode
											Integer resultData = (Integer) param;
												logger.debug("## GET_REGISTER_VALUE => {}", resultData);

												pushOptionalData("LoadControlMode", resultData);
											logger.debug("## next step = HDLC_DISC");
												result = true;
												this.step = Procedure.HDLC_DISC;

											} else {
											logger.debug("## ERROR:next step = HDLC_DISC");
												result = false;
												this.step = Procedure.HDLC_DISC;
												break;
											}

										if (this.step == Procedure.HDLC_DISC) {
												// -> UPDATE START 2016/09/20 SP-117
												// bypassFrameResult.setLastProcedure( Procedure.GET_DISCONNECT_CONTROL );
												// bypassFrameResult.addResultValue( "RelayStatus"      , optionalData.get("RelayStatus"));
												// bypassFrameResult.addResultValue( "LoadControlStatus", optionalData.get("LoadControlStatus"));
												// bypassFrameResult.addResultValue( "LoadControlMode"  , optionalData.get("LoadControlMode"));
											Boolean relaystatus = (Boolean) optionalData.get("RelayStatus");
											Integer loadcontrolstatus = (Integer) optionalData.get("LoadControlStatus");

											bypassFrameResult.setLastProcedure(Procedure.GET_DISCONNECT_CONTROL);
												// Relay Status
											if (relaystatus == true) {
												bypassFrameResult.addResultValue("Relay Status", RELAY_STATUS_KAIFA.Connected);
											} else {
												bypassFrameResult.addResultValue("Relay Status", RELAY_STATUS_KAIFA.Disconnected);
												}
												// Load Control Status
											if (loadcontrolstatus == CONTROL_STATE.Connected.ordinal()) {
												bypassFrameResult.addResultValue("LoadControlStatus", CONTROL_STATE.Connected);
											} else if (loadcontrolstatus == CONTROL_STATE.Disconnected.ordinal()) {
												bypassFrameResult.addResultValue("LoadControlStatus", CONTROL_STATE.Disconnected);
											} else if (loadcontrolstatus == CONTROL_STATE.ReadyForReconnection.ordinal()) {
												bypassFrameResult.addResultValue("LoadControlStatus", CONTROL_STATE.ReadyForReconnection);
												}
												// Load Control Mode
											bypassFrameResult.addResultValue("LoadControlMode", optionalData.get("LoadControlMode"));
												// <- UPDATE START 2016/09/20 SP-117
											}
											result = sendBypass();
										}
									}
									break;
								default:
									break;
								}
							// <- INSERT END   2016/08/24 SP117
								
							} else if (command.equals("cmdSetSingleActionSchedule")) {
								//switch (HdlcObjectType.getItem(DataUtil.getByteToInt(frame.getType()))) {
								switch (frame.getDlmsApdu()) {
								case ACTION_RESPONSE:
									if (this.step == Procedure.HDLC_ASSOCIATION_LN) {
										// 결과 확인
										ActionResult param = (ActionResult) frame.getResultData();
										logger.debug("## HDLC_ASSOCIATION_LN Result => {}", param.name());
										if (param == ActionResult.SUCCESS) {
											this.step = Procedure.SET_SINGLE_ACTION_SCHEDULE;
											result = sendBypass();
										}
									}
									break;
								case SET_RESPONSE:
									// 결과 확인
									DataAccessResult param = (DataAccessResult) frame.getResultData();
									logger.debug("## SET_SINGLE_ACTION_SCHEDULE => {}", param.name());

									if (param == DataAccessResult.SUCCESS) {
										bypassFrameResult.setLastProcedure(Procedure.SET_SINGLE_ACTION_SCHEDULE);
										bypassFrameResult.setResultValue("Success");
										result = true;

										this.step = Procedure.HDLC_DISC;
										result = sendBypass();
									}
									break;
								default:
									break;
								}
							} else if (command.equals("cmdGetSingleActionSchedule")) {
								//switch (HdlcObjectType.getItem(DataUtil.getByteToInt(frame.getType()))) {
								switch (frame.getDlmsApdu()) {
								case ACTION_RESPONSE:
									if (this.step == Procedure.HDLC_ASSOCIATION_LN) {
										ActionResult param = (ActionResult) frame.getResultData();
										logger.debug("## HDLC_ASSOCIATION_LN Result => {}", param.name());
										if (param == ActionResult.SUCCESS) {
											this.step = Procedure.GET_VALUE;
											result = sendBypass();
										}
									}
									break;
								case GET_RESPONSE:
									Object param = frame.getResultData();
									if (param instanceof DataAccessResult) {
										result = false;
										logger.debug("## [{}]GET_RES_DataAccessResult => {}", step.name(), ((DataAccessResult) param).name());
									} else {
										if (this.step == Procedure.GET_VALUE) {
											logger.debug("## GET_VALUE => {}", param.toString());

											bypassFrameResult.setLastProcedure(Procedure.GET_VALUE);

											List<HashMap<String, Object>> list = (List<HashMap<String, Object>>) param;
											HashMap<String, String> map = new HashMap<String, String>();
											
											map.put("DATA_SIZE", String.valueOf(list.size()));
											int i = 0;
											
											for(HashMap<String, Object> value : list) {
												// {0=hhmmss00, 1=YYYYMMDDWW}
												// YYYY-MM-DD hh:mm:ss
												String time = (String)value.get(String.valueOf(0));
												String date = (String)value.get(String.valueOf(1));
												String executionTime = (date.substring(0, 4).equals("FFFF") ? "FFFF" : String.format("%04d", Integer.parseInt(date.substring(0, 4), 16))) + "-" +
																	   (date.substring(4, 6).equals("FF") ? "FF" : String.format("%02d", Integer.parseInt(date.substring(4, 6), 16))) + "-" +
																	   (date.substring(6, 8).equals("FF") ? "FF" : String.format("%02d", Integer.parseInt(date.substring(6, 8), 16))) + " " +
																	   (time.substring(0, 2).equals("FF") ? "FF" : String.format("%02d", Integer.parseInt(time.substring(0, 2), 16))) + ":" +
																	   (time.substring(2, 4).equals("FF") ? "FF" : String.format("%02d", Integer.parseInt(time.substring(2, 4), 16))) + ":" +
																	   (time.substring(4, 6).equals("FF") ? "FF" : String.format("%02d", Integer.parseInt(time.substring(4, 6), 16)));
										
												
												map.put("["+i+"] Execution Time", executionTime);
												i++;
											}
											
											bypassFrameResult.setResultValue(map);

											result = true;

											this.step = Procedure.HDLC_DISC;
											result = sendBypass();
										}
									}
									break;
								default:
									break;
								}
								
						} else if ((command.equals("cmdGetLoadProfileOnDemand")) || (command.equals("cmdGetLoadProfileOnDemandMbb"))) {
								//switch (HdlcObjectType.getItem(DataUtil.getByteToInt(frame.getType()))) {
							switch (frame.getDlmsApdu()) {
								case ACTION_RESPONSE:
									if (this.step == Procedure.HDLC_ASSOCIATION_LN) {
										// 결과 확인
										ActionResult param = (ActionResult) frame.getResultData();
										logger.debug("## HDLC_ASSOCIATION_LN Result => {}", param.name());
										if (param == ActionResult.SUCCESS) {
											if (params != null && params.get("obisCode") != null) {
											logger.debug("## obiscode => {}" + params.get("obisCode").toString());
											if (params.get("obisCode").toString().equals(DataUtil.convertObis(OBIS.MBUSMASTER_LOAD_PROFILE.getCode()))) {
												logger.debug("## next step = GET_PROFILE_BUFFER");
													this.step = Procedure.GET_PROFILE_BUFFER;
											} else {
												logger.debug("## next step = GET_PROFILE_OBJECT");
													this.step = Procedure.GET_PROFILE_OBJECT;
												}
											} else {
												this.step = Procedure.GET_PROFILE_OBJECT;
											}
											result = ( niTidType == TID_Type.Enable) ? sendBypassWidhNiTid(true) : sendBypass(); //UPDATE SP-722
										}
									}
									break;
								case GET_RESPONSE:
									// 결과 확인
									Object param = frame.getResultData();
									if (param instanceof DataAccessResult) {
										result = false;
										logger.debug("## [{}]GET_RES_DataAccessResult => {}", step.name(), ((DataAccessResult) param).name());
									} else {
										if (this.step == Procedure.GET_PROFILE_OBJECT) {
											// 결과 확인
											List<HashMap<String, Object>> resultData = (ArrayList<HashMap<String, Object>>) param;
											channelData = resultData;

											logger.debug("## GET_PROFILE_OBJECT => {}", resultData);

											bypassFrameResult.setLastProcedure(Procedure.GET_PROFILE_OBJECT);
											result = true;

											this.step = Procedure.GET_PROFILE_BUFFER;
											result = ( niTidType == TID_Type.Enable) ? sendBypassWidhNiTid(true) : sendBypass();//UPDATE SP-722
										} else if (this.step == Procedure.GET_PROFILE_BUFFER) {
											// 결과 확인
											Map<String, Object> map = (Map<String, Object>) param;
											Boolean isBlock = map.get("isBlock") == null ? false : (Boolean) map.get("isBlock");
											Boolean isLast = map.get("isLast") == null ? true : (Boolean) map.get("isLast");
											Integer blockNumber = map.containsKey("blockNumber") == false ? 0 : (Integer) map.get("blockNumber");
											logger.debug("## GET_PROFILE_BUFFER => {}", map);
			
											if (dataBlockArrayOfGetRes == null) {
												dataBlockArrayOfGetRes = new byte[] {};
											}
			
											dataBlockArrayOfGetRes = DataUtil.append(dataBlockArrayOfGetRes, (byte[]) map.get("rawData")); // 누적. 여러차례에 걸쳐 넘어오는 raw data를 하나로 모은다.
											logger.debug("dataBlockArrayOfGetRes=" + Hex.decode(dataBlockArrayOfGetRes));
											if (isLast) { // 마지막 블럭 처리
												// 합산데이터 파싱처리.
												// UPDATE START SP-737
												//Object resultObj = frame.customDecode(Procedure.GET_PROFILE_BUFFER, dataBlockArrayOfGetRes);
												//List<Object> obj = (List<Object>) resultObj;
												List<Object> obj = null;
												boolean last = false;
												if (   splitOnDamend == false ) {
													Object resultObj = frame.customDecode(Procedure.GET_PROFILE_BUFFER, dataBlockArrayOfGetRes);
													obj = (List<Object>) resultObj;
													last = true;
												}
												else { //  splitOnDamend == true 
													String value = getNextOndemandSplitParam(); // get
													if ( value != null){
														logger.debug("Set Next Time Parameter for OnDemand:{}", value);
														params.put("isBlock", false);
														params.put("value", value);
														rawDataList.add(dataBlockArrayOfGetRes);
														dataBlockArrayOfGetRes = null;
													}
													else { 
														logger.debug("All Time Split Parameter is finishd");
														rawDataList.add(dataBlockArrayOfGetRes);
														dataBlockArrayOfGetRes = null;
														obj = new ArrayList<Object>();
														if ( rawDataList != null && rawDataList.size() > 0 ){
															logger.debug("rawDataList size = {}",rawDataList.size() );
															dataBlockArrayOfGetRes = new byte[] {};
															for ( byte[] rawdata : rawDataList ){
																dataBlockArrayOfGetRes = DataUtil.append(dataBlockArrayOfGetRes,rawdata );
																List<Object> resultObj = (List<Object>) frame.customDecode(Procedure.GET_PROFILE_BUFFER, rawdata);
																obj.addAll(resultObj);
															}
														}
														last = true;
														logger.debug("Before add Result dataBlockArrayOfGetRes=" + Hex.decode(dataBlockArrayOfGetRes));
													}
												}
												if ( last ){
													bypassFrameResult.setLastProcedure(this.step);
													bypassFrameResult.addResultValue("channelData", channelData);
													bypassFrameResult.addResultValue("rawData", dataBlockArrayOfGetRes);
													bypassFrameResult.addResultValue("listData", obj);
				
													if (params.get("obisCode").toString().equals(DataUtil.convertObis(OBIS.ENERGY_LOAD_PROFILE.getCode()))) {
														String obisCode = DataUtil.convertObis(OBIS.CUMULATIVE_ACTIVEENERGY_IMPORT.getCode());
														int classId = DLMS_CLASS.REGISTER.getClazz();
														int attrId = DLMS_CLASS_ATTR.REGISTER_ATTR02.getAttr();
														params.put("obisCode", obisCode);
														params.put("classId", String.valueOf(classId));
														params.put("attributeNo", String.valueOf(attrId));
														this.step = Procedure.GET_REGISTER_VALUE;
													} else { // MBUSMASTER_LOAD_PROFILE
														params.clear();
														this.step = Procedure.HDLC_DISC;
													}
												}
												result = ( niTidType == TID_Type.Enable) ? sendBypassWidhNiTid(true) : sendBypass(); //UPDATE SP-722
											} else {
													params.put("isBlock", isBlock);
													params.put("blockNumber", blockNumber);

													result = ( niTidType == TID_Type.Enable) ? sendBypassWidhNiTid(true) : sendBypass(); //UPDATE SP-722
											}
									} else if (this.step == Procedure.GET_REGISTER_VALUE) {
											long resultData = (Long) param;
											logger.debug("## GET_RESIGETER_VALUE => {}", resultData);

										logger.debug("## obiscode => {}" + params.get("obisCode").toString());
											bypassFrameResult.setLastProcedure(this.step);
										if (params.get("obisCode").toString().equals(DataUtil.convertObis(OBIS.CUMULATIVE_ACTIVEENERGY_IMPORT.getCode()))) {
											logger.debug("## next step = GET_REGISTER_VALUE(CUMULATIVE_ACTIVEENERGY_EXPORT)");
												pushOptionalData(OBIS.CUMULATIVE_ACTIVEENERGY_IMPORT.name(), resultData);
												String obisCode = DataUtil.convertObis(OBIS.CUMULATIVE_ACTIVEENERGY_EXPORT.getCode());
											params.put("obisCode", obisCode);
												this.step = Procedure.GET_REGISTER_VALUE;
										} else if (params.get("obisCode").toString().equals(DataUtil.convertObis(OBIS.CUMULATIVE_ACTIVEENERGY_EXPORT.getCode()))) {
											logger.debug("## next step = GET_REGISTER_VALUE(CUMULATIVE_REACTIVEENERGY_IMPORT)");
												pushOptionalData(OBIS.CUMULATIVE_ACTIVEENERGY_EXPORT.name(), resultData);
												String obisCode = DataUtil.convertObis(OBIS.CUMULATIVE_REACTIVEENERGY_IMPORT.getCode());
											params.put("obisCode", obisCode);
												this.step = Procedure.GET_REGISTER_VALUE;
										} else if (params.get("obisCode").toString().equals(DataUtil.convertObis(OBIS.CUMULATIVE_REACTIVEENERGY_IMPORT.getCode()))) {
											logger.debug("## next step = GET_REGISTER_VALUE(CUMULATIVE_REACTIVEENERGY_EXPORT)");
												pushOptionalData(OBIS.CUMULATIVE_REACTIVEENERGY_IMPORT.name(), resultData);
												String obisCode = DataUtil.convertObis(OBIS.CUMULATIVE_REACTIVEENERGY_EXPORT.getCode());
											params.put("obisCode", obisCode);
												this.step = Procedure.GET_REGISTER_VALUE;
										} else if (params.get("obisCode").toString().equals(DataUtil.convertObis(OBIS.CUMULATIVE_REACTIVEENERGY_EXPORT.getCode()))) {
											//										logger.debug("## next step = HDLC_DISC" );
											//										params.clear();
												pushOptionalData(OBIS.CUMULATIVE_REACTIVEENERGY_EXPORT.name(), resultData);
											//										
											//										this.step = Procedure.HDLC_DISC;
											logger.debug("## next step = GET_REGISTER_UNIT(CUMULATIVE_ACTIVEENERGY_IMPORT)");
												 String obisCode = DataUtil.convertObis(OBIS.CUMULATIVE_ACTIVEENERGY_IMPORT.getCode());
												 int classId = DLMS_CLASS.REGISTER.getClazz();
												 int attrId = DLMS_CLASS_ATTR.REGISTER_ATTR03.getAttr(); // scalar unit
												 params.put("obisCode", obisCode);
											params.put("classId", String.valueOf(classId));
												 params.put("attributeNo", String.valueOf(attrId));
												 this.step = Procedure.GET_REGISTER_UNIT;
										} else {
											logger.debug("## next step = HDLC_DISC");
												params.clear();
												this.step = Procedure.HDLC_DISC;
											}
										//									bypassFrameResult.setLastProcedure(this.step);
										//									bypassFrameResult.addResultValue("channelData", channelData);
										//									bypassFrameResult.addResultValue("rawData", dataBlockArrayOfGetRes);
										//									bypassFrameResult.addResultValue("ActiveEnergyExport", optionalData.get("ActiveEnergyExport"));
										//									bypassFrameResult.addResultValue("ActiveEnergyImport", optionalData.get("ActiveEnergyImport"));
										//									bypassFrameResult.addResultValue("ReactiveEnergyExport",optionalData.get("ReactiveEnergyExport"));
										//									bypassFrameResult.addResultValue("ReactiveEnergyImport",optionalData.get("ReactiveEnergyImport"));
										//									bypassFrameResult.setLastProcedure(Procedure.GET_REGISTER_VALUE);
										//result = sendBypass();
										result = ( niTidType == TID_Type.Enable) ? sendBypassWidhNiTid(true) : sendBypass(); //UPDATE SP-722

									} else if (this.step == Procedure.GET_REGISTER_UNIT) {
											// map  "scaler", int; "unit" int
										HashMap<String, Object> resultData = (HashMap<String, Object>) param;
											logger.debug("## GET_REGISTER_UNIT => {}", resultData);
										if (resultData != null && resultData.get("unit") != null) {
												String unitString = "";
												UNIT unit = UNIT.getItem((int) resultData.get("unit"));
											if (unit != null)
													unitString = unit.getName();
											logger.debug("## GET_REGISTER_UNIT => scaler = {}, unit =  {}", unitString, (int) resultData.get("unit"));
										} else {
												logger.debug("## GET_REGISTER_UNIT => {}", resultData);
											}
										logger.debug("## obiscode => {}" + params.get("obisCode").toString());
											bypassFrameResult.setLastProcedure(Procedure.GET_REGISTER_UNIT);
										if (params.get("obisCode").toString().equals(DataUtil.convertObis(OBIS.CUMULATIVE_ACTIVEENERGY_IMPORT.getCode()))) {
											logger.debug("## next step = GET_REGISTER_UNIT(CUMULATIVE_ACTIVEENERGY_EXPORT)");
												pushOptionalData(OBIS.CUMULATIVE_ACTIVEENERGY_IMPORT.name() + "_UNIT", resultData);
												String obisCode = DataUtil.convertObis(OBIS.CUMULATIVE_ACTIVEENERGY_EXPORT.getCode());
											params.put("obisCode", obisCode);
												this.step = Procedure.GET_REGISTER_UNIT;
										} else if (params.get("obisCode").toString().equals(DataUtil.convertObis(OBIS.CUMULATIVE_ACTIVEENERGY_EXPORT.getCode()))) {
											logger.debug("## next step = GET_REGISTER_UNIT(CUMULATIVE_REACTIVEENERGY_IMPORT)");
												pushOptionalData(OBIS.CUMULATIVE_ACTIVEENERGY_EXPORT.name() + "_UNIT", resultData);
												String obisCode = DataUtil.convertObis(OBIS.CUMULATIVE_REACTIVEENERGY_IMPORT.getCode());
											params.put("obisCode", obisCode);
												this.step = Procedure.GET_REGISTER_UNIT;
										} else if (params.get("obisCode").toString().equals(DataUtil.convertObis(OBIS.CUMULATIVE_REACTIVEENERGY_IMPORT.getCode()))) {
											logger.debug("## next step = GET_REGISTER_UNIT(CUMULATIVE_REACTIVEENERGY_EXPORT)");
												pushOptionalData(OBIS.CUMULATIVE_REACTIVEENERGY_IMPORT.name() + "_UNIT", resultData);
												String obisCode = DataUtil.convertObis(OBIS.CUMULATIVE_REACTIVEENERGY_EXPORT.getCode());
											params.put("obisCode", obisCode);
												this.step = Procedure.GET_REGISTER_UNIT;
										} else if (params.get("obisCode").toString().equals(DataUtil.convertObis(OBIS.CUMULATIVE_REACTIVEENERGY_EXPORT.getCode()))) {
											logger.debug("## next step = HDLC_DISC");
												pushOptionalData(OBIS.CUMULATIVE_REACTIVEENERGY_EXPORT.name() + "_UNIT", resultData);
											params.clear();
												this.step = Procedure.HDLC_DISC;

											OBIS cumulatives[] = new OBIS[4];
												cumulatives[0] = OBIS.CUMULATIVE_ACTIVEENERGY_IMPORT;
												cumulatives[1] = OBIS.CUMULATIVE_ACTIVEENERGY_EXPORT;
												cumulatives[2] = OBIS.CUMULATIVE_REACTIVEENERGY_IMPORT;
												cumulatives[3] = OBIS.CUMULATIVE_REACTIVEENERGY_EXPORT;
												bypassFrameResult.addResultValue("channelData", channelData);
												bypassFrameResult.addResultValue("rawData", dataBlockArrayOfGetRes);
											for (int i = 0; i < cumulatives.length; i++) {
												bypassFrameResult.addResultValue(cumulatives[i].name(), optionalData.get(cumulatives[i].name()));
												bypassFrameResult.addResultValue(cumulatives[i].name() + "_UNIT", optionalData.get(cumulatives[i].name() + "_UNIT"));

												}
										} else {
											logger.debug("## next step = HDLC_DISC");
												params.clear();
												this.step = Procedure.HDLC_DISC;
										}
											//result = sendBypass();
											result = ( niTidType == TID_Type.Enable) ? sendBypassWidhNiTid(true) : sendBypass();//UPDATE SP-722
									}

									}
									break;
								default:
									break;
								}
							}
						
					// Command 별 처리 끝
					}  // isHDLCSegmented 가 아닌경우 끝
				}   // HdlcFrameType.I 인경우 처리 끝
				


			} catch (Exception e) {
				logger.error("BYPASS_IESCO RECEIVE ERROR - {}", e);
				result = false;
			}
		}
		else { // frame.decode() is false, INSERT SP-868 
			if ((command.equals("cmdGetLoadProfileOnDemand"))|| (command.equals("cmdGetLoadProfileOnDemandMbb"))) {
				if ( sendFrameRetry && splitOnDamend ){
					if ( sendFrameRetryCount >= sendFramRetryMax) {
						logger.error("Decode fail and sendFrameRetryCount >= {}, not retry. RETRY[{}] ", sendFramRetryMax, sendFrameRetryCount );
					}
					else {
						decodeFailedAndResended = true;
						result = sendBypass();
						result = false;
						rcvHdlcRRFrame = true; // for don't ignore next receive RR frame 
						sendFrameRetryCount++;
						logger.info("Decode fail and Retry sendBypass. STEP [{}] Retry[{}] ", this.step, this.sendFrameRetryCount);
					}
				}
			}
		}
		bypassFrameResult.setStep(this.step);
		bypassFrameResult.setResultState(result);
		return bypassFrameResult;
	}

	/**
	 * SP-722
	 * @param tidUp
	 * @return
	 */
	private boolean sendBypassWidhNiTid(boolean tidUp){
		return sendBypassWidhNiTid(null, tidUp);
	}
	private boolean sendBypassWidhNiTid(HdlcFrameType hdlcFrameType, boolean tidUp){
		if ( niTidType == TID_Type.Enable){
			setNiTid(tidUp);
		}
		return sendBypass(hdlcFrameType);
	}
	private boolean sendBypass() {
		return sendBypass(null);
	}
	private boolean sendBypass(HdlcFrameType hdlcFrameType) {
		boolean result = false;
		byte[] req = null;
		@SuppressWarnings("unused")
		HashMap<String, Object> initParam = null;

		try {
			// DelayTime설정시 전송전에 딜레이시간을 준다.
			/*logger.debug("Set delay time [{}] before send DLMS packet." , sendDelayTime);
			if(0 < sendDelayTime){			
				Thread.sleep(sendDelayTime);  
			}			
			*/
			logger.debug("STEP [{}] before send DLMS packet.", this.step);
			sendTime = System.currentTimeMillis();// INSERT SP-722
			
			
			if (hdlcFrameType != null) {
				if (hdlcFrameType == HdlcFrameType.RR) {
					req = frame.encode(hdlcFrameType, DlmsConstants.XDLMS_APDU.NULL, null, null, null);
					if (req != null) {
						logger.debug("### [{}] HDLC_REQUEST_RR => {}", this.step.name(), Hex.decode(req));
						this.session.write(req);
						result = true;
					}
				} else {

				}
			}else {
				switch (this.step) {
				case HDLC_SNRM:
					//req = frame.encode(HdlcObjectType.SNRM, null, null);
					//req = frame.encode(HdlcObjectType.SNRM, null, params); // SP-519
					req = frame.encode(HdlcFrameType.SNRM, XDLMS_APDU.NULL, null, params, command); // SP-519
					if (req != null) {
						logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));

						this.session.write(req);
						result = true;
					}
					break;
				case HDLC_AARQ:
					//req = frame.encode(HdlcObjectType.AARQ, null, null, command);
					//req = frame.encode(HdlcObjectType.AARQ, null, params, command); // //UPDATE SP-722
					req = frame.encode(HdlcFrameType.I, XDLMS_APDU.AARQ, null, params, command);
					if (req != null) {
						logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));

						this.session.write(req);
						result = true;
					}
					break;
				case HDLC_ASSOCIATION_LN:
					//req = frame.encode(HdlcObjectType.ACTION_REQ, Procedure.HDLC_ASSOCIATION_LN, null, command);
					//req = frame.encode(HdlcObjectType.ACTION_REQ, Procedure.HDLC_ASSOCIATION_LN, params, command);// //UPDATE SP-722
					req = frame.encode(HdlcFrameType.I, XDLMS_APDU.GLO_ACTION_REQUEST, Procedure.HDLC_ASSOCIATION_LN, params, command);  // Pakistan - IESCO
					if (req != null) {
						logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));

						this.session.write(req);
						result = true;
					}
					break;
				case HDLC_DISC:
					//req = frame.encode(HdlcObjectType.DISC, null, null, command);
					//req = frame.encode(HdlcObjectType.DISC, null, params, command);// //UPDATE SP-722
					req = frame.encode(hdlcFrameType.DISC, XDLMS_APDU.NULL, null, params, command);
					bypassFrameResult.setFinished(true);

					if (req != null) {
						logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));

						this.session.write(req);
						result = true;
					}
					break;
				case SET_METER_TIME:
					//req = frame.encode(HdlcObjectType.SET_REQ, Procedure.SET_METER_TIME, params, command);
					req = frame.encode(HdlcFrameType.I, XDLMS_APDU.SET_REQUEST, Procedure.SET_METER_TIME, params, command);

					if (req != null) {
						logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));

						this.session.write(req);
						result = true;
					}
					break;
				case GET_METER_TIME:
					//req = frame.encode(HdlcObjectType.GET_REQ, Procedure.GET_METER_TIME, params, command);
					req = frame.encode(HdlcFrameType.I, XDLMS_APDU.GET_REQUEST, Procedure.GET_METER_TIME, params, command);

					if (req != null) {
						logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));

						this.session.write(req);
						result = true;
					}
					break;
				case SET_REGISTER_VALUE:
					//req = frame.encode(HdlcObjectType.SET_REQ, Procedure.SET_REGISTER_VALUE, params, command);
					req = frame.encode(HdlcFrameType.I, XDLMS_APDU.SET_REQUEST, Procedure.SET_REGISTER_VALUE, params, command);

					if (req != null) {
						logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));

						this.session.write(req);
						result = true;
					}
					break;
				case GET_REGISTER_VALUE:
					//req = frame.encode(HdlcObjectType.GET_REQ, Procedure.GET_REGISTER_VALUE, params, command);
					req = frame.encode(HdlcFrameType.I, XDLMS_APDU.GET_REQUEST, Procedure.GET_REGISTER_VALUE,  params, command);

					if (req != null) {
						logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));

						this.session.write(req);
						result = true;
					}
					break;
				case SET_REGISTER_UNIT:
					//req = frame.encode(HdlcObjectType.SET_REQ, Procedure.SET_REGISTER_UNIT, params, command);
					req = frame.encode(HdlcFrameType.I, XDLMS_APDU.SET_REQUEST, Procedure.SET_REGISTER_UNIT, params, command);

					if (req != null) {
						logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));

						this.session.write(req);
						result = true;
					}
					break;
				case GET_REGISTER_UNIT:
					//req = frame.encode(HdlcObjectType.GET_REQ, Procedure.GET_REGISTER_UNIT, params, command);
					req = frame.encode(HdlcFrameType.I, XDLMS_APDU.GET_REQUEST, Procedure.GET_REGISTER_UNIT, params, command);

					if (req != null) {
						logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));

						this.session.write(req);
						result = true;
					}
					break;
				case SET_PROFILE_PERIOD:
					//req = frame.encode(HdlcObjectType.SET_REQ, Procedure.SET_PROFILE_PERIOD, params, command);
					req = frame.encode(HdlcFrameType.I, XDLMS_APDU.SET_REQUEST, Procedure.SET_PROFILE_PERIOD, params, command);

					if (req != null) {
						logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));

						this.session.write(req);
						result = true;
					}
					break;
				case GET_PROFILE_PERIOD:
					//req = frame.encode(HdlcObjectType.GET_REQ, Procedure.GET_PROFILE_PERIOD, params, command);
					req = frame.encode(HdlcFrameType.I, XDLMS_APDU.GET_REQUEST, Procedure.GET_PROFILE_PERIOD, params, command);

					if (req != null) {
						logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));

						this.session.write(req);
						result = true;
					}
					break;
				case GET_PROFILE_OBJECT:
					//req = frame.encode(HdlcObjectType.GET_REQ, Procedure.GET_PROFILE_OBJECT, params, command);
					req = frame.encode(HdlcFrameType.I, XDLMS_APDU.GET_REQUEST, Procedure.GET_PROFILE_OBJECT, params, command);

					if (req != null) {
						logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));

						this.session.write(req);
						result = true;
					}
					break;
				case GET_PROFILE_BUFFER:
					//req = frame.encode(HdlcObjectType.GET_REQ, Procedure.GET_PROFILE_BUFFER, params, command);
					req = frame.encode(HdlcFrameType.I, XDLMS_APDU.GET_REQUEST, Procedure.GET_PROFILE_BUFFER, params, command);

					if (req != null) {
						logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));
						this.session.write(req);
						result = true;
					}
					break;
				case SET_THRESHOLD_NORMAL:
					//req = frame.encode(HdlcObjectType.SET_REQ, Procedure.SET_THRESHOLD_NORMAL, params, command);
					req = frame.encode(HdlcFrameType.I, XDLMS_APDU.SET_REQUEST, Procedure.SET_THRESHOLD_NORMAL, params, command);

					if (req != null) {
						logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));

						this.session.write(req);
						result = true;
					}
					break;
				case GET_THRESHOLD_NORMAL:
					//req = frame.encode(HdlcObjectType.GET_REQ, Procedure.GET_THRESHOLD_NORMAL, params, command);
					req = frame.encode(HdlcFrameType.I, XDLMS_APDU.GET_REQUEST, Procedure.GET_THRESHOLD_NORMAL, params, command);

					if (req != null) {
						logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));

						this.session.write(req);
						result = true;
					}
					break;
				case SET_MINOVER_THRESHOLD_DURATION:
					//req = frame.encode(HdlcObjectType.SET_REQ, Procedure.SET_MINOVER_THRESHOLD_DURATION, params, command);
					req = frame.encode(HdlcFrameType.I, XDLMS_APDU.SET_REQUEST, Procedure.SET_MINOVER_THRESHOLD_DURATION, params, command);

					if (req != null) {
						logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));

						this.session.write(req);
						result = true;
					}
					break;
				case GET_MINOVER_THRESHOLD_DURATION:
					//req = frame.encode(HdlcObjectType.GET_REQ, Procedure.GET_MINOVER_THRESHOLD_DURATION, params, command);
					req = frame.encode(HdlcFrameType.I, XDLMS_APDU.GET_REQUEST, Procedure.GET_MINOVER_THRESHOLD_DURATION, params, command);

					if (req != null) {
						logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));

						this.session.write(req);
						result = true;
					}
					break;
				case SET_DISCONNECT_CONTROL:
					//req = frame.encode(HdlcObjectType.SET_REQ, Procedure.SET_DISCONNECT_CONTROL, params, command);
					req = frame.encode(HdlcFrameType.I, XDLMS_APDU.SET_REQUEST, Procedure.SET_DISCONNECT_CONTROL, params, command);

					if (req != null) {
						logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));

						this.session.write(req);
						result = true;
					}
					break;
				case GET_DISCONNECT_CONTROL:
					//req = frame.encode(HdlcObjectType.GET_REQ, Procedure.GET_DISCONNECT_CONTROL, params, command);
					req = frame.encode(HdlcFrameType.I, XDLMS_APDU.GET_REQUEST, Procedure.GET_DISCONNECT_CONTROL, params, command);

					if (req != null) {
						logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));

						this.session.write(req);
						result = true;
					}
					break;
				case ACTION_DISCONNECT_CONTROL:
					//req = frame.encode(HdlcObjectType.ACTION_REQ, Procedure.ACTION_DISCONNECT_CONTROL, params, command);
					req = frame.encode(HdlcFrameType.I, XDLMS_APDU.ACTION_REQUEST, Procedure.ACTION_DISCONNECT_CONTROL, params, command);

					if (req != null) {
						logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));

						this.session.write(req);
						result = true;
					}
					break;
					/*******************************************************
					 * Meter F/W 용
					 */
				case GET_IMAGE_TRANSFER_ENABLE:
					//req = frame.encode(HdlcObjectType.GET_REQ, Procedure.GET_IMAGE_TRANSFER_ENABLE, null, command);
					//req = frame.encode(HdlcObjectType.GET_REQ, Procedure.GET_IMAGE_TRANSFER_ENABLE, params, command);
					req = frame.encode(HdlcFrameType.I, XDLMS_APDU.GET_REQUEST, Procedure.GET_IMAGE_TRANSFER_ENABLE, params, command);

					if (req != null) {
						logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));

						this.session.write(req);
						result = true;
					}
					break;
				case SET_IMAGE_TRANSFER_ENABLE:
					//req = frame.encode(HdlcObjectType.SET_REQ, Procedure.SET_IMAGE_TRANSFER_ENABLE, null, command);
					//req = frame.encode(HdlcObjectType.SET_REQ, Procedure.SET_IMAGE_TRANSFER_ENABLE, params, command);
					req = frame.encode(HdlcFrameType.I, XDLMS_APDU.SET_REQUEST, Procedure.SET_IMAGE_TRANSFER_ENABLE, params, command);

					if (req != null) {
						logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));

						this.session.write(req);
						result = true;
					}
					break;
				case GET_IMAGE_BLOCK_SIZE:
					//req = frame.encode(HdlcObjectType.GET_REQ, Procedure.GET_IMAGE_BLOCK_SIZE, null, command);
					//req = frame.encode(HdlcObjectType.GET_REQ, Procedure.GET_IMAGE_BLOCK_SIZE, params, command);
					req = frame.encode(HdlcFrameType.I, XDLMS_APDU.GET_REQUEST, Procedure.GET_IMAGE_BLOCK_SIZE, params, command);

					if (req != null) {
						logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));

						this.session.write(req);
						result = true;
					}
					break;
				case ACTION_IMAGE_TRANSFER_INIT:
//					initParam = new HashMap<String, Object>();
//					initParam.put("image_identifier", imageIdentifier);
//					initParam.put("image_size", String.valueOf(fwSize));
					
					params.put("image_identifier", imageIdentifier);
					params.put("image_size", String.valueOf(fwSize));

//					req = frame.encode(HdlcObjectType.ACTION_REQ, Procedure.ACTION_IMAGE_TRANSFER_INIT, initParam, command);
					//req = frame.encode(HdlcObjectType.ACTION_REQ, Procedure.ACTION_IMAGE_TRANSFER_INIT, params, command);
					req = frame.encode(HdlcFrameType.I, XDLMS_APDU.ACTION_REQUEST, Procedure.ACTION_IMAGE_TRANSFER_INIT,  params, command);

					if (req != null) {
						//logger.debug("### [{}] HDLC_REQUEST => Image={}, size={} / {}", this.step.name(), initParam.get("image_identifier"), initParam.get("image_size"), Hex.decode(req));
						logger.debug("### [{}] HDLC_REQUEST => Image={}, size={} / {}", this.step.name(), params.get("image_identifier"), params.get("image_size"), Hex.decode(req));

						this.session.write(req);
						result = true;
					}
					break;
				case GET_IMAGE_TRANSFER_STATUS:
//					req = frame.encode(HdlcObjectType.GET_REQ, Procedure.GET_IMAGE_TRANSFER_STATUS, null, command);
					//req = frame.encode(HdlcObjectType.GET_REQ, Procedure.GET_IMAGE_TRANSFER_STATUS, params, command);
					req = frame.encode(HdlcFrameType.I, XDLMS_APDU.GET_REQUEST, Procedure.GET_IMAGE_TRANSFER_STATUS, params, command);
					
					if (req != null) {
						logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));

						this.session.write(req);
						result = true;
					}
					break;
				case ACTION_IMAGE_BLOCK_TRANSFER:
					if (0 < remainPackateLength) {
						// 전송실패시 어디까지 보냈는지 저장하기위함.
						bypassFrameResult.setResultValue(imageBlockNumber + "/" + totalImageBlockNumber);

						if (packetSize < remainPackateLength) {
							sendPacket = new byte[packetSize];
						} else {
							sendPacket = new byte[remainPackateLength];
						}
						System.arraycopy(fwImgArray, offset, sendPacket, 0, sendPacket.length);

//						initParam = new HashMap<String, Object>();
//						initParam.put("image_block_number", imageBlockNumber);
//						initParam.put("image_block_value", sendPacket);
//	                  req = frame.encode(HdlcObjectType.ACTION_REQ, Procedure.ACTION_IMAGE_BLOCK_TRANSFER, initParam, command);
						
						params.put("image_block_number", imageBlockNumber);
						params.put("image_block_value", sendPacket);
						//req = frame.encode(HdlcObjectType.ACTION_REQ, Procedure.ACTION_IMAGE_BLOCK_TRANSFER, params, command);
						req = frame.encode(HdlcFrameType.I, XDLMS_APDU.ACTION_REQUEST, Procedure.ACTION_IMAGE_BLOCK_TRANSFER, params, command);

						if (req != null) {
							logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));

							this.session.write(req);
							result = true;
						} else {
							stopTransferImageTimer();
							throw new Exception("ACTION_IMAGE_BLOCK_TRANSFER Encoding Error");
						}

						double tempa = fwImgArray.length;
						double tempb = offset + sendPacket.length;
						progressRate = String.format("%.2f", tempb / tempa * 100) + "%";
						int tempPacketLength = remainPackateLength - packetSize;
						logger.info("[ACTION_IMAGE_BLOCK_TRANSFER][{}] #### PROGRESS_RATE={} #####, Sended Image Block Number={}/{}, Packet Size={}, RemainPacket Size={}, ", frame.getMeterId(), progressRate, imageBlockNumber, totalImageBlockNumber, sendPacket.length, (tempPacketLength <= 0 ? 0 : tempPacketLength));

						//					remainPackateLength -= packetSize;
						//					offset += sendPacket.length;
						//					imageBlockNumber++;222
						/*
						 *  재전송해야할 필요가 있는지 체크하는 타이머
						 *  retryTime초뒤에 실행, retryTime초 간격으로 NEED_IMAGE_BLOCK_TRANSFER_MAX_RETRY_COUNT 만큼 재실행
						 */

						/**
						 *  Nullbypass 방식인 경우에만 사용. 
						 */
						if (niTidType == TID_Type.Disable){
							// TaskName = MeterId + ImageBlockNumber + NowDateTime
							String taskName = frame.getMeterId() + "_" + imageBlockNumber + "_" + DateTimeUtil.getCurrentDateTimeByFormat(null);
							logger.debug("ImangeBlockTransferRetryTask Create. TaskName=[{}], retyrCount={}, retryTime={}", taskName, NEED_IMAGE_BLOCK_TRANSFER_MAX_RETRY_COUNT, SEND_IAMGE_RETRY_TIMEOUT);
			
							needImangeBlockTransferRetry = true;
							blockTransferRetryTask = new NeedImangeBlockTransferRetry(taskName, this.session, req, NEED_IMAGE_BLOCK_TRANSFER_MAX_RETRY_COUNT);
							blockTransferRetryTimer.scheduleAtFixedRate(blockTransferRetryTask, SEND_IAMGE_RETRY_TIMEOUT * 1000, SEND_IAMGE_RETRY_TIMEOUT * 1000);						
						}
					} else {
						stopTransferImageTimer();
						//logger.debug("## Timer 중지!! ==> needImangeBlockTransferRetry ={}", needImangeBlockTransferRetry);
					}

					break;
				case GET_IMAGE_FIRST_NOT_TRANSFERRED_BLOCK_NUMBER:
//					req = frame.encode(HdlcObjectType.GET_REQ, Procedure.GET_IMAGE_FIRST_NOT_TRANSFERRED_BLOCK_NUMBER, null, command);
					//req = frame.encode(HdlcObjectType.GET_REQ, Procedure.GET_IMAGE_FIRST_NOT_TRANSFERRED_BLOCK_NUMBER, params, command);
					req = frame.encode(HdlcFrameType.I, XDLMS_APDU.GET_REQUEST, Procedure.GET_IMAGE_FIRST_NOT_TRANSFERRED_BLOCK_NUMBER, params, command);

					if (req != null) {
						logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));

						this.session.write(req);
						result = true;
					}
					break;
				case ACTION_IMAGE_VERIFY:
//					initParam = new HashMap<String, Object>();
//					initParam.put("image_verify_data", (byte) 0x00);
//					req = frame.encode(HdlcObjectType.ACTION_REQ, Procedure.ACTION_IMAGE_VERIFY, initParam, command);
					
					params.put("image_verify_data", (byte) 0x00);
					//req = frame.encode(HdlcObjectType.ACTION_REQ, Procedure.ACTION_IMAGE_VERIFY, params, command);
					req = frame.encode(HdlcFrameType.I, XDLMS_APDU.ACTION_REQUEST, Procedure.ACTION_IMAGE_VERIFY, params, command);

					if (req != null) {
						logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));

						this.session.write(req);
						result = true;
					}
					break;
				/* SORIA KAIFA Meter는 GET_IMAGE_TO_ACTIVATE_INFO 단계를 건너뛴다.
				 * 주석 삭제하지 말것~!!
				 * 주석 삭제하지 말것~!!
				 * 주석 삭제하지 말것~!!
				case GET_IMAGE_TO_ACTIVATE_INFO:
					req = frame.encode(HdlcObjectType.GET_REQ, Procedure.GET_IMAGE_TO_ACTIVATE_INFO, null);
				
					if (req != null) {
						logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));
				
						this.session.write(req);
						result = true;
					}
					break;
				*/
				case ACTION_IMAGE_ACTIVATE:
//					initParam = new HashMap<String, Object>();
//					initParam.put("image_activate_data", (byte) 0x00);
//					req = frame.encode(HdlcObjectType.ACTION_REQ, Procedure.ACTION_IMAGE_ACTIVATE, initParam, command);
					
					params.put("image_activate_data", (byte) 0x00);
					//req = frame.encode(HdlcObjectType.ACTION_REQ, Procedure.ACTION_IMAGE_ACTIVATE, params, command);
					req = frame.encode(HdlcFrameType.I, XDLMS_APDU.ACTION_REQUEST, Procedure.ACTION_IMAGE_ACTIVATE, params, command);

					if (req != null) {
						logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));

						this.session.write(req);
						result = true;
					}
					break;
				case GET_FIRMWARE_VERSION:
					//req = frame.encode(HdlcObjectType.GET_REQ, Procedure.GET_FIRMWARE_VERSION, null, command);
					//req = frame.encode(HdlcObjectType.GET_REQ, Procedure.GET_FIRMWARE_VERSION, params, command);
					req = frame.encode(HdlcFrameType.I, XDLMS_APDU.GET_REQUEST, Procedure.GET_FIRMWARE_VERSION, params, command);
					
					if (req != null) {
						logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));

						this.session.write(req);
						result = true;
					}
					break;
				case GET_SORIA_METER_KEY_A:
					//req = frame.encode(HdlcObjectType.KAIFA_CUSTOM, Procedure.GET_SORIA_METER_KEY_A, null, command);
					req = frame.encode(HdlcFrameType.I, XDLMS_APDU.KAIFA_CUSTOM, Procedure.GET_SORIA_METER_KEY_A, null, command);

					if (req != null) {
						logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));

						this.session.write(req);
						result = true;
					}
					break;
				case GET_SORIA_METER_KEY_B:
					//req = frame.encode(HdlcObjectType.KAIFA_CUSTOM, Procedure.GET_SORIA_METER_KEY_B, null, command);
					req = frame.encode(HdlcFrameType.I, XDLMS_APDU.KAIFA_CUSTOM, Procedure.GET_SORIA_METER_KEY_B, null, command);

					if (req != null) {
						logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));

						this.session.write(req);
						result = true;
					}
					break;
				case GET_SORIA_METER_KEY_C:
					//req = frame.encode(HdlcObjectType.KAIFA_CUSTOM, Procedure.GET_SORIA_METER_KEY_C, null, command);
					req = frame.encode(HdlcFrameType.I, XDLMS_APDU.KAIFA_CUSTOM, Procedure.GET_SORIA_METER_KEY_C, null, command);

					if (req != null) {
						logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));

						this.session.write(req);
						result = true;
					}
					break;
				case SET_SORIA_METER_SERIAL_A:
					//req = frame.encode(HdlcObjectType.KAIFA_CUSTOM, Procedure.SET_SORIA_METER_SERIAL_A, null, command);
					req = frame.encode(HdlcFrameType.I, XDLMS_APDU.KAIFA_CUSTOM, Procedure.SET_SORIA_METER_SERIAL_A, null, command);

					if (req != null) {
						logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));

						this.session.write(req);
						result = true;
					}
					break;
				case SET_SORIA_METER_SERIAL_B:
					//req = frame.encode(HdlcObjectType.KAIFA_CUSTOM, Procedure.SET_SORIA_METER_SERIAL_B, null, command);
					req = frame.encode(HdlcFrameType.I, XDLMS_APDU.KAIFA_CUSTOM, Procedure.SET_SORIA_METER_SERIAL_B, null, command);

					if (req != null) {
						logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));

						this.session.write(req);
						result = true;
					}
					break;
				case SET_SORIA_METER_SERIAL_C:
					//req = frame.encode(HdlcObjectType.KAIFA_CUSTOM, Procedure.SET_SORIA_METER_SERIAL_C, params, command);
					req = frame.encode(HdlcFrameType.I, XDLMS_APDU.KAIFA_CUSTOM, Procedure.SET_SORIA_METER_SERIAL_C, params, command);

					if (req != null) {
						logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));

						this.session.write(req);
						result = true;
					}
					break;
				case SET_SORIA_METER_SERIAL_D:
					//req = frame.encode(HdlcObjectType.KAIFA_CUSTOM, Procedure.SET_SORIA_METER_SERIAL_D, null, command);
					req = frame.encode(HdlcFrameType.I, XDLMS_APDU.KAIFA_CUSTOM, Procedure.SET_SORIA_METER_SERIAL_D, null, command);

					if (req != null) {
						logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));

						this.session.write(req);
						result = true;
					}
					break;
				case SET_SORIA_METER_SERIAL_E:
					//req = frame.encode(HdlcObjectType.KAIFA_CUSTOM, Procedure.SET_SORIA_METER_SERIAL_E, null, command);
					req = frame.encode(HdlcFrameType.I, XDLMS_APDU.KAIFA_CUSTOM, Procedure.SET_SORIA_METER_SERIAL_E, null, command);

					if (req != null) {
						logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));

						this.session.write(req);
						result = true;
					}
					break;
				case ACTION_SLAVE_INSTALL:
					//req = frame.encode(HdlcObjectType.ACTION_REQ, Procedure.ACTION_SLAVE_INSTALL, params, command);
					req = frame.encode(HdlcFrameType.I, XDLMS_APDU.ACTION_REQUEST, Procedure.ACTION_SLAVE_INSTALL, params, command);

					if (req != null) {
						logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));

						this.session.write(req);
						result = true;
					}				
					
					break;
				case ACTION_SLAVE_DEINSTALL:
					//req = frame.encode(HdlcObjectType.ACTION_REQ, Procedure.ACTION_SLAVE_DEINSTALL, params, command);
					req = frame.encode(HdlcFrameType.I, XDLMS_APDU.ACTION_REQUEST, Procedure.ACTION_SLAVE_DEINSTALL, params, command);

					if (req != null) {
						logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));

						this.session.write(req);
						result = true;
					}				
					
					break;
				case ACTION_SET_ENCRYPTION_KEY:
					//req = frame.encode(HdlcObjectType.ACTION_REQ, Procedure.ACTION_SET_ENCRYPTION_KEY, params, command);
					req = frame.encode(HdlcFrameType.I, XDLMS_APDU.ACTION_REQUEST, Procedure.ACTION_SET_ENCRYPTION_KEY, params, command);

					if (req != null) {
						logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));

						this.session.write(req);
						result = true;
					}
					break;
				case ACTION_TRANSFER_KEY:
					//req = frame.encode(HdlcObjectType.ACTION_REQ, Procedure.ACTION_SET_ENCRYPTION_KEY, params, command);
					req = frame.encode(HdlcFrameType.I, XDLMS_APDU.ACTION_REQUEST, Procedure.ACTION_SET_ENCRYPTION_KEY, params, command);

					if (req != null) {
						logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));

						this.session.write(req);
						result = true;
					}
					break;
				case GET_VALUE:
					//req = frame.encode(HdlcObjectType.GET_REQ, Procedure.GET_VALUE, params, command);
					req = frame.encode(HdlcFrameType.I, XDLMS_APDU.GET_REQUEST, Procedure.GET_VALUE, params, command);

					if (req != null) {
						logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));

						this.session.write(req);
						result = true;
					}
					break;
				case SET_VALUE:
					//req = frame.encode(HdlcObjectType.SET_REQ, Procedure.SET_VALUE, params, command);
					req = frame.encode(HdlcFrameType.I, XDLMS_APDU.SET_REQUEST, Procedure.SET_VALUE, params, command);

					if (req != null) {
						logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));

						this.session.write(req);
						result = true;
					}
					break;
				case SET_SINGLE_ACTION_SCHEDULE:
					//req = frame.encode(HdlcObjectType.SET_REQ, Procedure.SET_SINGLE_ACTION_SCHEDULE, params, command);
					req = frame.encode(HdlcFrameType.I, XDLMS_APDU.SET_REQUEST, Procedure.SET_SINGLE_ACTION_SCHEDULE, params, command);

					if (req != null) {
						logger.debug("### [{}] HDLC_REQUEST => {}", this.step.name(), Hex.decode(req));

						this.session.write(req);
						result = true;
					}
					break;
				default:
					break;
				}
			}
		} catch (Exception e) {
			logger.error("BYPASS_SP SEND ERROR - {}", e);
			result = false;
		}

		return result;
	}

	/**
	 * Image Transfer next block setting.
	 */
	private void setNextBlockTrigger() {
		setNextBlockTrigger(false);
	}
	private void setNextBlockTrigger(boolean force) {
		/*
		 * Meter RS count를 확인해서 R이 증가됬을경우에만 다음블럭을 전송, 동일한 count일 경우에는 동일 블럭 재전송.
		 */
		int[] meterRScount = frame.getMeterRSCount();
		logger.debug("Meter RS Count : before[{}] -> current[{}]"
				, meterRScount[0] + ", " + meterRScount[1]
				, (2 < meterRScount.length ? meterRScount[2] : "-") + ", " + (3 < meterRScount.length ? meterRScount[3] : "-"));
		
		// for RS rotation.
		if(meterRScount[0] == 7){
			meterRScount[0] = -1;
		}
		
		if (meterRScount.length == 2 || meterRScount[0] < meterRScount[2] || force) {
			remainPackateLength -= packetSize;
			offset += sendPacket.length;
			imageBlockNumber++;
		}else{
			logger.warn("### The same RS number is received and the same block is sending...");
			logger.warn("### The same RS number is received and the same block is sending...");
			logger.warn("### The same RS number is received and the same block is sending...");
		}
	}

	private boolean verificationCheckRetry() {
		boolean result = false;
		/*
		 * Image transfer status 체크
		 */
		try {
			Thread.sleep(30000);

			//byte[] req = frame.encode(HdlcObjectType.GET_REQ, Procedure.GET_IMAGE_TRANSFER_STATUS, null, command);
			
			if(niTidType == TID_Type.Enable){
				setNiTid(true);
			}
			
			//byte[] req = frame.encode(HdlcObjectType.GET_REQ, Procedure.GET_IMAGE_TRANSFER_STATUS, params, command);
			byte[] req = frame.encode(frame.getHDLCFrameType(), XDLMS_APDU.GET_REQUEST, Procedure.GET_IMAGE_TRANSFER_STATUS, params, command);
			if (req != null) {
				verificationRetryCount++; // 재시도 횟수 카운팅
				logger.debug("### [ACTION_IMAGE_VERIFY][GET_IMAGE_TRANSFER_STATUS][{}] Retry Count={} HDLC_REQUEST => {}", frame.getMeterId(), verificationRetryCount, Hex.decode(req));
				session.write(req);
				result = true;
			}
		} catch (Exception e) {
			logger.error("verificationCheckRetry Error - " + e);
		}
		return result;
	}

	private boolean activationCheckRetry() {
		boolean result = false;

		/*
		 * Image transfer status 체크
		 */
		try {
			Thread.sleep(30000);
			//byte[] req = frame.encode(HdlcObjectType.GET_REQ, Procedure.GET_IMAGE_TRANSFER_STATUS, null, command);
			
			if(niTidType == TID_Type.Enable){
				setNiTid(true);
			}
			
			//byte[] req = frame.encode(HdlcObjectType.GET_REQ, Procedure.GET_IMAGE_TRANSFER_STATUS, params, command);
			byte[] req = frame.encode(frame.getHDLCFrameType(), XDLMS_APDU.GET_REQUEST, Procedure.GET_IMAGE_TRANSFER_STATUS, params, command);
			if (req != null) {
				verificationRetryCount++; // 재시도 횟수 카운팅
				logger.debug("### [ACTION_IMAGE_ACTIVATE][GET_IMAGE_TRANSFER_STATUS][{}] Retry Count={} HDLC_REQUEST => {}", frame.getMeterId(), verificationRetryCount, Hex.decode(req));
				session.write(req);
				result = true;
			}
		} catch (Exception e) {
			logger.error("activationCheckRetry Error - " + e);
		}

		return result;
	}

	/**
	 * Timer, Timer Task cancel
	 */
	private void stopTransferImageTimer() {
		/** NullBypass 인경우만 사용 */
		if( niTidType == TID_Type.Disable){
			needImangeBlockTransferRetry = false;
			if(blockTransferRetryTask != null){
				blockTransferRetryTask.cancel();
				logger.debug("## Execute Task timer stop. taskName={}", blockTransferRetryTask.getTaskName());
			}
			
			if(blockTransferRetryTimer != null){
				blockTransferRetryTimer.cancel();			
			}
			
			blockTransferRetryTimer = null;
			logger.debug("## Timer Task Stop.");
		}
	}
	
	/**
	 * Timer purge.
	 * @return tasks removed from the queue.
	 */
	private int purgeTransferImageTimer() {
		int purgeCount = 0;

		/** NullBypass 인경우만 사용 */
		if( niTidType == TID_Type.Disable){
			needImangeBlockTransferRetry = false;
			
			if(blockTransferRetryTask != null){
				blockTransferRetryTask.cancel();			
			}
			
			if(blockTransferRetryTimer != null){
				purgeCount = blockTransferRetryTimer.purge();
				logger.debug("## Execute Task timer purge. taskName={}, {} tasks removed from the queue.", (blockTransferRetryTask == null ? "Null~!!" : blockTransferRetryTask.getTaskName()), purgeCount);
			}			
		}
		return purgeCount;
	}

	/**
	 * Image Block Transfer를 반복 실행하는 TimerTask
	 * 
	 * @author simhanger
	 *
	 */
	protected class NeedImangeBlockTransferRetry extends TimerTask {
		private String taskName;
		private MultiSession session;
		private byte[] req;
		private int maxRetryCount;
		private int retryCount;

		public NeedImangeBlockTransferRetry(String taskName, MultiSession session, byte[] req, int maxRetryCount) {
			this.taskName = taskName;
			this.session = session;
			this.req = req;
			this.maxRetryCount = maxRetryCount;
			
			logger.debug("Task Created. TaskName=[{}]", taskName);
		}

		public String getTaskName(){
			return this.taskName;
		}
		
		@Override
		public void run() {
			if (needImangeBlockTransferRetry == true && this.retryCount < this.maxRetryCount) {
				logger.info("[IMAGE_BLOCK_TRANSFER_RETRY][EXECUTE] TaskName=[{}], MeterId={}, Retry={}/{}, Sended Image Block Number={}/{}, ProgressRate={}"
						, taskName, frame.getMeterId(), retryCount + 1, maxRetryCount, imageBlockNumber, totalImageBlockNumber, progressRate);
				this.session.write(this.req);
				this.retryCount++;
			} else {
				this.cancel();
				logger.warn("[IMAGE_BLOCK_TRANSFER_RETRY][CANCEL] TaskName=[{}], MeterId={}, Retry={}/{}, Sended Image Block Number={}/{}, ProgressRate={}"
						, taskName, frame.getMeterId(), retryCount + 1, maxRetryCount, imageBlockNumber, totalImageBlockNumber, progressRate);
				
				/*
				 * OTA 종료후 Event 저장
				 */
				logger.debug("OTA Result Event Saving start in TimerTask.");
				String issueDate = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss");
				
				String meterId = session.getBypassDevice().getMeterId();
				MeterDao meterDao = DataUtil.getBean(MeterDao.class);
				Meter meter = meterDao.get(meterId);
				
				EV_PH_200_66_0_Action action2 = new EV_PH_200_66_0_Action();
				action2.makeEvent(TargetClass.EnergyMeter, meterId, TargetClass.EnergyMeter, issueDate, OTA_UPGRADE_RESULT_CODE.OTAERR_BYPASS_TRN_FAIL, null, "HES", meter.getLocation());
				action2.updateOTAHistory(meterId, DeviceType.Meter, issueDate, OTA_UPGRADE_RESULT_CODE.OTAERR_BYPASS_TRN_FAIL, "[ACTION_IMAGE_BLOCK_TRANSFER] Progress Rate=" + progressRate + ", Retry count=" + retryCount);
				logger.debug("OTA Result Event Saving finished in TimerTask.");
				
				stop(session);
			}
		}
	}

	@Override
	//public void stop(IoSession session) {
	public void stop(MultiSession session) {
		// Timer Stop.
		stopTransferImageTimer();
		
		//byte[] frameArray = frame.encode(HdlcObjectType.DISC, null, null, command);
		//byte[] frameArray = frame.encode(HdlcObjectType.DISC, null, params, command);//UPDATE SP-722
		byte[] frameArray = frame.encode(HdlcFrameType.DISC, XDLMS_APDU.NULL, null, params, command);
		
		if (frameArray != null) {
			this.step = Procedure.HDLC_DISC;

			logger.debug("### Stop Bypass ~ !! => {}", Hex.decode(frameArray));
			logger.debug("### Stop Bypass ~ !! => {}", Hex.decode(frameArray));
			logger.debug("### Stop Bypass ~ !! => {}", Hex.decode(frameArray));

			if(niTidType == TID_Type.Enable){
				setNiTid(true);
			}
			this.session.write(frameArray);
		}
	}
	
	// INSERT START SP-628
	public String getStepName() {
		return this.step.name();
	}
	
	public boolean isHandshakeFinish() throws Exception {
		boolean result = false;

		if ((this.step == Procedure.HDLC_SNRM) ||
			(this.step == Procedure.HDLC_AARQ) ||
			(this.step == Procedure.HDLC_ASSOCIATION_LN)
		){
			result = false;
		}
		else {
			result = true;
		}
		
		return result;
	}
	public void retryHandshake() throws Exception {
		@SuppressWarnings("unused")
		boolean result = false;
		
		try {
			logger.debug("Retry DLMS handshake. STEP [{}] ", this.step);
			
			if ((this.step == Procedure.HDLC_SNRM) ||
					(this.step == Procedure.HDLC_AARQ) ||
					(this.step == Procedure.HDLC_ASSOCIATION_LN)
				){

				result = sendBypass();
				
			}
		}
		catch (Exception e) {
			logger.error("BYPASS_SORIA RECEIVE ERROR - {}", e);
			result = false;
		}
	}
	// INSERT END SP-628

	/**
	 * SP-722
	 * @param maxRetry
	 * @return
	 * @throws Exception
	 */
	public int retrySendNiBypass(int maxRetry ) throws Exception {
		int result = 0;
		
		if ( this.niTidType == TID_Type.Disable){
			logger.error("TID_Type is Disable, not retry. TID [{}] RETRY[{}/{}] ", this.niTransId, this.niRetry, maxRetry);
		}else{
			try {
				if ( this.niRetry < maxRetry ){
					if ( sendBypassWidhNiTid(false)){
						logger.info("Retry sendBypass finished. TID [{}] RETRY[{}/{}] ", this.niTransId, this.niRetry, maxRetry);
						result = this.niRetry;
					}
					else {
						logger.error("sendBypass failed. TID [{}] RETRY[{}/{}] ", this.niTransId, this.niRetry, maxRetry);
						result = -1;
					}
				}
			}
			catch (Exception e) {
				logger.error("BYPASS_SORIA RECEIVE ERROR - {}", e);
			}			
		}
		
		logger.debug("################################################################");
		logger.debug("");
		
		return result;
	}
	
	/**
	 * SP-722
	 * @param send
	 * @param recv
	 * @return
	 */
	private NI_TID_STATUS checkNiTid(byte[] send, byte[] recv)
	{
		if ( send == null || recv == null ){
			logger.debug("TID information is null, return TYPE_DISABLE");
			return NI_TID_STATUS.TYPE_DISABLE;
		}
		// check type
		TID_Type sType = Bypass.getTidType(send);
		TID_Type rType = Bypass.getTidType(recv);
		int sLoc = Bypass.getTidLocation(send);
		int rLoc = Bypass.getTidLocation(recv);
		int sTid = Bypass.getTid(send);
		int rTid = Bypass.getTid(recv);
		logger.debug("NI TID Send:{},{},{}, RECV:{},{},{}",
				sType.name(),String.valueOf(sLoc), String.valueOf(sTid),
				rType.name(),String.valueOf(rLoc), String.valueOf(rTid));
		
		if (sType == TID_Type.Disable ){
			return NI_TID_STATUS.TYPE_DISABLE;
		}
		if ( sTid != rTid ){
			if ( Bypass.getPrevTid(sTid) == rTid ){
				return NI_TID_STATUS.PREV_TID;
			}
			else {
				return NI_TID_STATUS.BAD_TID;
			}
		}
		if ( sLoc != rLoc ){
			return NI_TID_STATUS.BAD_LOCATION;
		}
		else {
			return NI_TID_STATUS.OK;
		}
		
	}
	
	/**
	 * @return the niTidtype
	 */
	public TID_Type getNiTidType() {
		return niTidType;
	}

	/**
	 * @param niTidtype the niTidtype to set
	 */
	public void setNiTidType(TID_Type niTidtype) {
		this.niTidType = niTidtype;
	}
	/**
	 * @return the niRetry
	 */
	public int getNiRetry() {
		return niRetry;
	}

	/**
	 * @param niRetry the niRetry to set
	 */
	public void setNiRetry(int niRetry) {
		this.niRetry = niRetry;
	}
	/**
	 * @return the niTransId
	 */
	public int getNiTransId() {
		return niTransId;
	}

	/**
	 * @param niTransId the niTransId to set
	 */
	public void setNiTransId(int niTransId) {
		this.niTransId = niTransId;
	}
	/**
	 * @return the niTidLocation
	 */
	public int getNiTidLocation() {
		return niTidLocation;
	}

	/**
	 * @param niTidLocation the niTidLocation to set
	 */
	public void setNiTidLocation(int niTidLocation) {
		this.niTidLocation = niTidLocation;
	}
	/**
	 * SP-722
	 * @param tidUp
	 */
	synchronized public void setNiTid(boolean tidUp)
	{
		if ( niTidType == TID_Type.Disable){
			// nothing to do.
			return;
		}
		
		if ( tidUp ){// Count Up TID
	        if ( niTransId >= 0x3F ) {
	        	niTransId = 0x00;
	        	if ( niTidLocation == 0x00 )
	        		niTidLocation = 0x40;
	        	else 
	        		niTidLocation = 0x00;
	        }
	        else {
	        	niTransId++;
	        }
	        niRetry = 0;
		}
		else { // increase the number of retry without changing Tid
			niRetry++;
		}
		// set TID in params
        params.put("niTidType", niTidType);
        params.put("niTransId", niTransId);
        params.put("niTidLocation", niTidLocation);
        params.put("niRetry", niRetry);
        
        logger.debug("NI TID TransId[{}] TidLocation[{}]", niTransId, niTidLocation);
	}
	
	public long getSendTime() {
		return this.sendTime;
	}

	/**
	 * SP-737
	 * @return
	 */
	private String getNextOndemandSplitParam()
	{
		String ret = null;
		if ( ondemandValueList != null && ondemandValueList.size()> 0 ){
			ret = ondemandValueList.get(0);
			ondemandValueList.remove(0);
		}
		return ret;
	}
	
	/**
	 * SP-737
	 * retry send frame 
	 * @param maxRetry
	 * @return
	 * @throws Exception
	 */
	public boolean  retrySendBypass( int maxRetry ) throws Exception {
		boolean result = false;	
		try {
			if ( sendFrameRetry == false ){
				logger.error("sendFrameRetry is false, sendBypass()");
				return sendBypass();
			}
			// sendFrameRetry = true
			if ( this.sendFrameRetryCount >= maxRetry ){
				logger.error("sendFrameRetryCount >= {}, not retry. RETRY[{}] ", maxRetry,  this.sendFrameRetryCount );
				return result;
			}
			if ( this.sendFrameRetryCount == 0  ){
				logger.debug("Retry First Time, Send Same Frame. STEP [{}] ",this.step);
				params.put("hdlcResendFrame", 1);
			}
			else {
				params.put("hdlcResendFrame", 0);
			}
			result = sendBypass();
			sendFrameRetryCount++;
			logger.info("Retry sendBypass. STEP [{}] Retry[{}] ", this.step, this.sendFrameRetryCount);
		}
		catch (Exception e) {
			logger.error("BYPASS_SORIA RECEIVE ERROR - {}", e);
			result = false;
		}
		return result;
	}
	
	/**
	 * @return the sendFrameRetry
	 */
	public boolean isSendFrameRetry() {
		return sendFrameRetry;
	}

	/**
	 * @param sendFrameRetry the sendFrameRetry to set
	 */
	public void setSendFrameRetry(boolean sendFrameRetry) {
		this.sendFrameRetry = sendFrameRetry;
	}

	/**
	 * @return the sendFrameRetryCount
	 */
	public int getSendFrameRetryCount() {
		return sendFrameRetryCount;
	}

	/**
	 * @param sendFrameRetryCount the sendFrameRetryCount to set
	 */
	public void setSendFrameRetryCount(int sendFrameRetryCount) {
		this.sendFrameRetryCount = sendFrameRetryCount;
	}
	
	/**
	 * SP-737
	 * @param interval
	 * @param orgvalue
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private ArrayList<String> makeSplitOnDemendArg(Integer interval, String orgvalue) throws Exception
	{
		ArrayList<String> ret = new ArrayList<String>();
		String value = orgvalue.replaceAll("\\\\\"", "\"");
		//logger.debug("original=" + orgvalue + ",replaced=" + value);
		
		Map<String, Object> map = null;
		JSONArray jsonArr = null;
		if (value == null || value.isEmpty()) {
			jsonArr = new JSONArray();
		} else {
			jsonArr = JSONArray.fromObject(value);
		}
		
		map = (Map<String, Object>)jsonArr.toArray()[0];
		//from String
		String from = String.valueOf(map.get("fYear")) + String.valueOf(map.get("fMonth"))+ String.valueOf(map.get("fDayOfMonth")) 
			+String.valueOf(map.get("fHh")) + String.valueOf(map.get("fMm"))+String.valueOf(map.get("fSs"));
		String to = String.valueOf(map.get("tYear"))  +  String.valueOf(map.get("tMonth")) + String.valueOf(map.get("tDayOfMonth")) 
			+  String.valueOf(map.get("tHh")) + String.valueOf(map.get("tMm")) + String.valueOf(map.get("tSs"));

		Date current = new Date();
		Date fromDate = DateTimeUtil.getDateFromYYYYMMDDHHMMSS(from);
		Date toDate   = DateTimeUtil.getDateFromYYYYMMDDHHMMSS(to);
		String orgToDate = to;
		if ( current.compareTo(toDate) < 0){
			toDate = current;
			to = DateTimeUtil.getDateString(toDate);
		}
		long minDiff = (toDate.getTime() - fromDate.getTime()) / (1000 * 60);
		
		int  onceMin = interval * recordsPerBlock;
		int  splitNum = 1;
		if ( (minDiff % onceMin) > 0 ){
			splitNum = (int) (minDiff / onceMin + 1);
		}
		else {
			splitNum = (int) (minDiff / onceMin);
		}
		logger.debug("Split OnDemand change from to param: from[{}], to[{}], split num[{}], original_to[{}]", from, to, splitNum, orgToDate);
		for ( int i = 0; i < splitNum ; i++){
			String sfrom = DateTimeUtil.getDateString(fromDate.getTime() + (onceMin* 1000 * 60 * i ));
			String sto = null;
			if ( i == splitNum - 1){
				sto = to;
			}
			else {
				sto   = DateTimeUtil.getDateString(fromDate.getTime() + (onceMin * 1000 * 60 * (i+1))- 1);
			}
			Map<String,String> valueMap = Util.getParamValueByRange(sfrom, sto);
			String sValue = CommandGW.meterParamMapToJSON(valueMap);
//			logger.debug("OnDemand Time Split Parameter[{}] : {}", i, sValue);
			ret.add(sValue);
		}
		return ret;
	}
	
	/**
	 * @return the decodeFail
	 */
	public boolean isDecodeFailedAndResended() {
		return 	 decodeFailedAndResended;
	}
	
	/**
	 * @param sendFramRetryMax the sendFramRetryMax to set
	 */
	public void setSendFramRetryMax(int sendFramRetryMax) {
		this.sendFramRetryMax = sendFramRetryMax;
	}
}
