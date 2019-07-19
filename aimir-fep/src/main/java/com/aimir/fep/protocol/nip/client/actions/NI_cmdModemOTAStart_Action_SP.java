/**
 * 
 */
package com.aimir.fep.protocol.nip.client.actions;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.dao.device.ModemDao;
import com.aimir.fep.bypass.BypassDevice;
import com.aimir.fep.protocol.nip.CommandNIProxy;
import com.aimir.fep.protocol.nip.client.multisession.MultiSession;
import com.aimir.fep.protocol.nip.frame.GeneralFrame;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.FrameControl_Ack;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.FrameOption_Type;
import com.aimir.fep.protocol.nip.frame.payload.Firmware;
import com.aimir.fep.protocol.nip.frame.payload.Firmware.ImageCode;
import com.aimir.fep.protocol.nip.frame.payload.Firmware.UpgradeCommand;
import com.aimir.fep.trap.actions.SP.EV_SP_200_63_0_Action;
import com.aimir.fep.trap.actions.SP.EV_SP_200_65_0_Action;
import com.aimir.fep.trap.actions.SP.EV_SP_200_66_0_Action;
import com.aimir.fep.trap.common.EV_Action.OTA_UPGRADE_RESULT_CODE;
import com.aimir.fep.util.CRCUtil;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.Hex;
import com.aimir.model.device.Modem;
import com.aimir.model.device.Device.DeviceType;
import com.aimir.util.DateTimeUtil;

/**
 * @author simhanger
 *
 */

public class NI_cmdModemOTAStart_Action_SP extends NICommandAction {
	private static Logger logger = LoggerFactory.getLogger(NI_cmdModemOTAStart_Action_SP.class);

	/*
	 * Image transfer 용
	 */
	private boolean needImangeBlockTransferRetry = false;
	private Timer blockTransferRetryTimer = new Timer();
	private NeedImangeBlockTransferRetry blockTransferRetryTask;

	private final int NEED_IMAGE_BLOCK_TRANSFER_MAX_RETRY_COUNT;
	private final int SEND_IAMGE_RETRY_TIMEOUT;
	private Map<String, Object> notifyParams;
	private String actionTitle = "NI_cmdModemOTAStart_Action_SP";

	public NI_cmdModemOTAStart_Action_SP() {
		UUID uuid = UUID.randomUUID();
		actionTitle += "_" + uuid.toString();
		logger.debug("### Action Title = {}", actionTitle);

		/** set timer information */
		NEED_IMAGE_BLOCK_TRANSFER_MAX_RETRY_COUNT = Integer.parseInt(FMPProperty.getProperty("ota.firmware.modem.datasend.retry", "5"));
		if (Boolean.parseBoolean(FMPProperty.getProperty("soria.protocol.modem.rf.dtls.use", "true"))) {
			SEND_IAMGE_RETRY_TIMEOUT = 20; // DTLS의 경우 Modem의 타임아웃이 30초임. 나머지 바이패스는 60초.
		} else {
			SEND_IAMGE_RETRY_TIMEOUT = Integer.parseInt(FMPProperty.getProperty("ota.firmware.modem.datasend.retry.timeout", "30"));
		}
	}

	@Override
	public String getActionTitle() {
		return actionTitle;
	}

	@Override
	public Object executeStart(MultiSession session, GeneralFrame generalFrame) throws Exception {
		/*
		 * ACK = ON
		 * Upgrad Data Command의 경우 response command가 따로 없이 ack 로 날아오기때문에 필요.
		 */
		setUseAck(true);

		/*
		 * 1. F/W Image 준비
		 */
		long startTime = System.currentTimeMillis();
		BypassDevice bd = session.getBypassDevice();
		bd.setStartOTATime(startTime);

		/*
		 * 2. Frame 구성 및 첫번째 Request  - Upgrade Start Request
		 */
		GeneralFrame newGFrame = CommandNIProxy.setGeneralFrameOption(FrameOption_Type.Firmware, null);
		newGFrame.setFrame();
		newGFrame.setNetworkType(generalFrame.getNetworkType());
		((Firmware) newGFrame.payload).setTargetType(0); // 0 : Modem F/W upgrade
		((Firmware) newGFrame.payload).setUpgradeCommand(3); // 03 : Upgrade Start Request      
		//((Firmware) newGFrame.payload).setUpgradeSequenceNumber(Integer.parseInt(bd.getFwVersion())); // Upgrade Sequence Number => F/W 버전으로 설정 2byte.
		((Firmware) newGFrame.payload).setUpgradeSequenceNumber(bd.getFwVersion()); // Upgrade Sequence Number => F/W 버전으로 설정 2byte.

		// INSERT START SP-681
		((Firmware) newGFrame.payload).setFwVersion(bd.getOptionalVersion());
		((Firmware) newGFrame.payload).setFwModel(bd.getOptionalModel());
		// INSERT END SP-681
		
		// SP-1100
		// Optional Data CRC는 Firmware.encode() 에서 처리함.
		

		/*
		 * 3. ready for noti parameters
		 */
		notifyParams = new HashMap<String, Object>();
		notifyParams.put("modemId", bd.getModemId());
		notifyParams.put("meterId", bd.getMeterId());
		notifyParams.put("fwVersion", bd.getFwVersion());
		notifyParams.put("otaTargetDeviceType", bd.getOtaTargetDeviceType());
		notifyParams.put("elapseTime", "");
		notifyParams.put("result", false);
		notifyParams.put("resultMessage", "");

		byte[] generalFrameData = newGFrame.encode(null);
		logger.info("### STEP=[{}] Session write data => {}", UpgradeCommand.UpgradeStartRequest, newGFrame.toString());

		session.write(generalFrameData);

		/** data send retry check */
		dataSendRetryCheck(UpgradeCommand.UpgradeStartRequest, session, generalFrameData);

		//		CommandActionResult actionResult = generalFrame.getCommandActionResult();
		//		actionResult.setSuccess(true);
		//		actionResult.setResultValue("Proceeding...");

		//		ModemDao modemDao = DataUtil.getBean(ModemDao.class);
		//		Modem modem = modemDao.get(bd.getModemId());
		//		tClass = TargetClass.valueOf(modem.getModemType().name());

		//		/***************************************************
		//		 * Test Code
		//		 * 
		//		 */
		//		Map<String, Object> notifyParams = new HashMap<String, Object>();
		//		notifyParams.put("modemId", bd.getModemId());
		//		notifyParams.put("meterId", bd.getMeterId());
		//		notifyParams.put("fwVersion", bd.getFwVersion());
		//		//notifyParams.put("elapseTime", elapseTime);
		//		
		//		Thread.sleep(10000);
		//		
		//		
		//		logger.debug("### [NOTI] ### Let Noti~!. notiParams={}", notifyParams.toString());
		//		super.notifyToObserver(notifyParams);
		//		logger.debug("Call executeStop Start");
		//		executeStop(session);
		//		logger.debug("Call executeStop Stop");
		//		/*************************************************/

		return null;
	}

	@Override
	public Object executeTransaction(MultiSession session, GeneralFrame gFrame) throws Exception {
		/** Timer Purge */
		purgeTransferImageTimer();

		Firmware firmwareFrame = (Firmware) gFrame.getPayload();
		logger.debug("Firmware Frame = {}", firmwareFrame.toString());
		BypassDevice bd = session.getBypassDevice();

		long endTime = System.currentTimeMillis();
		String elapseTime = DateTimeUtil.getElapseTimeToString((endTime - bd.getStartOTATime()));
		String openTime = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss");

		try {
			GeneralFrame newGFrame = CommandNIProxy.setGeneralFrameOption(FrameOption_Type.Firmware, null);
			newGFrame.setFrame();
			newGFrame.setNetworkType(gFrame.getNetworkType());

			switch (firmwareFrame.get_upgradeCommand()) {
			case UpgradeStartResponse:
				int address = firmwareFrame.getAddress();
				
				// SP-1100
				if(address < 0 ) {
					/*
					 * Optional Data CRC를 사용하는 경우에 받은 CRC와 계산한 CRC가 다른 경우.
					 * OTA 종료후 Event 저장
					 */
					OTA_UPGRADE_RESULT_CODE resultCode = OTA_UPGRADE_RESULT_CODE.OTAERR_OPTIONAL_DATA_CRC_FAIL;
					
					String modemId = bd.getModemId();
					ModemDao modemDao = DataUtil.getBean(ModemDao.class);
					Modem modem = modemDao.get(modemId);
					
					EV_SP_200_65_0_Action action1 = new EV_SP_200_65_0_Action();
					action1.makeEvent(TargetClass.valueOf(bd.getModemType().name()), modemId, bd.getTargetClass(), openTime, elapseTime, resultCode, resultCode.getDesc(), "HES", modem.getLocation());
					action1.updateOTAHistory((bd.getOtaTargetDeviceType() == DeviceType.Modem ? modemId : bd.getMeterId()), bd.getOtaTargetDeviceType(), openTime, resultCode, "");

					logger.error("### STEP=[{}], ModemId={}, MeterId={}, Modem Optional Data CRC Fail~~~!! ResponseCode={}", firmwareFrame.get_upgradeCommand(), bd.getModemId(), bd.getMeterId(), DataUtil.get4ByteToInt(address));
					
					return new Exception("Upgrade Start Response Optional Data CRC Fail");
				}
				
				if (bd.isTakeOver() && address < bd.getFw_bin().length) {
					bd.setOffset(address);
				} else {
					bd.setOffset(0);
				}

				bd.setRemainPackateLength(bd.getFw_bin().length - bd.getOffset());
				logger.info("### STEP=[{}], ModemId={}, MeterId={}, GeneralFrame = {}, TakeOver={},  Address={}", firmwareFrame.get_upgradeCommand(), bd.getModemId(), bd.getMeterId(), newGFrame.toString(), bd.isTakeOver(), address);

				/** Image Transfer */
				sendImage(session, bd, newGFrame);

				break;
			case UpgradeEndResponse:
				logger.info("### STEP=[{}], ModemId={}, MeterId={}, ResultCode={}", firmwareFrame.get_upgradeCommand(), bd.getModemId(), bd.getMeterId(), firmwareFrame.get_imageCode().name());

				/*
				 * OTA 종료후 Event 저장
				 */
				OTA_UPGRADE_RESULT_CODE resultCode = OTA_UPGRADE_RESULT_CODE.UNKNOWN;

				if (firmwareFrame.get_imageCode() == ImageCode.NoError) {
					resultCode = OTA_UPGRADE_RESULT_CODE.OTAERR_NOERROR;
				} else if (firmwareFrame.get_imageCode() == ImageCode.CRCFail) {
					resultCode = OTA_UPGRADE_RESULT_CODE.OTAERR_CRC_FAIL;
				}

				String modemId = bd.getModemId();
				ModemDao modemDao = DataUtil.getBean(ModemDao.class);
				Modem modem = modemDao.get(modemId);
				
				EV_SP_200_65_0_Action action1 = new EV_SP_200_65_0_Action();
				action1.makeEvent(TargetClass.valueOf(bd.getModemType().name()), modemId, bd.getTargetClass(), openTime, elapseTime, resultCode, resultCode.getDesc(), "HES", modem.getLocation());

				action1.updateOTAHistory((bd.getOtaTargetDeviceType() == DeviceType.Modem ? modemId : bd.getMeterId()), bd.getOtaTargetDeviceType(), openTime, resultCode, "");

				switch (firmwareFrame.get_imageCode()) {
				case NoError:
					/*
					 * Upgrade Image Install Request
					 */
					((Firmware) newGFrame.payload).setTargetType(0); // 0 : Modem F/W upgrade
					((Firmware) newGFrame.payload).setUpgradeCommand(8); // 08 : Image Install Request  
					((Firmware) newGFrame.payload).setImageLength(bd.getFw_bin().length); // Image length
					((Firmware) newGFrame.payload).setCrc(bd.getFwCRC()); // Image CRC

					// INSERT START SP-681  -> delete at NI Protocol 5.70
					//((Firmware) newGFrame.payload).setInstallTime(bd.getOptionalInstallTime());
					// INSERT END SP-681    -> delete at NI Protocol 5.70

					byte[] generalFrameData = newGFrame.encode(null);

					session.write(generalFrameData);
					logger.debug("### STEP=[{}] Session write => [{}][{}]", UpgradeCommand.UpgradeImageInstallRequest, newGFrame.toString(), Hex.decode(generalFrameData));

					/** data send retry check */
					dataSendRetryCheck(UpgradeCommand.UpgradeImageInstallRequest, session, generalFrameData);
					break;
				case CRCFail:
					logger.error("### STEP=[{}], ModemId={}, MeterId={}, Modem OTA Fail~~~!!", firmwareFrame.get_upgradeCommand(), bd.getModemId(), bd.getMeterId());

					return new Exception("Upgrade End Response CRC Fail");
				case UnknownError:
					logger.error("### STEP=[{}], ModemId={}, MeterId={}, Modem OTA Fail~~~!!", firmwareFrame.get_upgradeCommand(), bd.getModemId(), bd.getMeterId());

					return new Exception("Upgrade End Response Unknown Error");
				default:
					break;
				}
				break;
			case UpgradeImageInstallResponse:
				logger.info("### STEP=[{}], ModemId={}, MeterId={}, ResultCode={}", firmwareFrame.get_upgradeCommand(), bd.getModemId(), bd.getMeterId(), firmwareFrame.get_imageCode().name());

				boolean result = false;
				switch (firmwareFrame.get_imageCode()) {
				case NoError:
					result = true;
					break;
				case CRCFail:
					logger.error("### STEP=[{}], ModemId={}, MeterId={}, Modem OTA Fail~~~!!", firmwareFrame.get_upgradeCommand(), bd.getModemId(), bd.getMeterId());

					return new Exception("Upgrade Image Install Response CRC Fail");
				case UnknownError:
					logger.error("### STEP=[{}], ModemId={}, MeterId={}, Modem OTA Fail~~~!!", firmwareFrame.get_upgradeCommand(), bd.getModemId(), bd.getMeterId());

					return new Exception("Upgrade Image Install Response Unknown Error");
				default:
					break;
				}

				
				modemId = bd.getModemId();
				modemDao = DataUtil.getBean(ModemDao.class);
				modem = modemDao.get(modemId);
				
				/*
				 * OTA 종료후 Event 저장. fw version 정보는 event 240.2.0 에서 처리 
				 */
				EV_SP_200_66_0_Action action2 = new EV_SP_200_66_0_Action();
				//String msg = "F/W Version=[" + bd.getFwVersion() + "] ElapseTime = [" + elapseTime + "]";
				String msg = "ElapseTime = [" + elapseTime + "]";
				action2.makeEvent(TargetClass.valueOf(bd.getModemType().name()), modemId, bd.getTargetClass(), openTime, OTA_UPGRADE_RESULT_CODE.OTAERR_NOERROR, msg, "HES", modem.getLocation());
				action2.updateOTAHistory(bd.getOtaTargetDeviceType() == DeviceType.Modem ? modemId : bd.getMeterId(), bd.getOtaTargetDeviceType(), openTime, OTA_UPGRADE_RESULT_CODE.OTAERR_NOERROR, msg);

				logger.info("#### [Upgrade Fininsed] OTATargetType={}, Meter={}, Modem={}, FWVersion={}, ElapseTime={} F/W Upgrade finished. result = {}!!! ", bd.getOtaTargetDeviceType(), bd.getMeterId(), modemId, bd.getFwVersion(), elapseTime, result);

				/** Notify to Observers */
				notifyParams.put("elapseTime", elapseTime);
				notifyParams.put("result", true);
				notifyParams.put("resultMessage", "OTA Success - ElapseTime = " + elapseTime);
				notiFire(notifyParams);

				logger.debug("Call executeStop Start");
				executeStop(session);
				logger.debug("Call executeStop Stop");
			default:
				break;
			}
		} catch (Exception e) {
			logger.error("executeTransaction error - " + e.getMessage(), e);

			/** Notify to Observers */
			notifyParams.put("elapseTime", elapseTime);
			notifyParams.put("result", false);
			notifyParams.put("resultMessage", "[" + firmwareFrame.get_upgradeCommand() + "] - " + e.getMessage());
			notiFire(notifyParams);

			throw e;
		}

		return null;
	}

	private boolean sendImage(MultiSession session, BypassDevice bd, GeneralFrame newGFrame) throws Exception {
		boolean hasRemainData = true;

		byte[] sendPacket = null;
		int remainPackateLength = bd.getRemainPackateLength();
		int offSet = bd.getOffset();

		logger.debug("### SEND_IMAGE_START - ModemId={}, MeterId={} Offset={}, RemainPacketLength={}/total={} ###", bd.getModemId(), bd.getMeterId(), offSet, remainPackateLength, bd.getFw_bin().length);

		if (0 < remainPackateLength) {
			if (bd.getPacket_size() < remainPackateLength) {
				sendPacket = new byte[bd.getPacket_size()];
			} else {
				sendPacket = new byte[remainPackateLength];
			}
			System.arraycopy(bd.getFw_bin(), offSet, sendPacket, 0, sendPacket.length);

			// Firmware Upgrad Frame Setting            
			newGFrame.setFrameControl_Ack(FrameControl_Ack.Ack);
			newGFrame.setFrameSequence(bd.getNextFrameSequence());

			((Firmware) newGFrame.payload).setTargetType(0); // 0 : Modem F/W upgrade
			((Firmware) newGFrame.payload).setUpgradeCommand(5); // 05 : UpgradeData    
			((Firmware) newGFrame.payload).setAddress(offSet); // 전송하고자 하는 Image의 상대적인 주소
			((Firmware) newGFrame.payload).setLength(sendPacket.length); // 전송할 data의 길이
			((Firmware) newGFrame.payload).setData(sendPacket); // 전송할 data

			byte[] generalFrameData = newGFrame.encode(null);

			logger.debug("SEND_DATA_PACKET = {}", Hex.decode(sendPacket));

			if (generalFrameData != null && 0 < generalFrameData.length) {
				session.write(generalFrameData);
				logger.debug("### STEP=[{}] Image Send Session write => {}", UpgradeCommand.UpgradeData, newGFrame.toString());
			} else {
				needImangeBlockTransferRetry = false;
				throw new Exception("[Upgrade Data] Image Send Encoding Error");
			}

			remainPackateLength -= sendPacket.length;
			if (remainPackateLength <= 0) {
				remainPackateLength = 0;
			}
			bd.setRemainPackateLength(remainPackateLength);

			bd.setOffset(offSet += sendPacket.length);

			double tempa = bd.getFw_bin().length;
			double tempb = offSet;
			logger.info("STEP=[{}] ######### ProgressRate={}% ###########, [ModemId={}, MeterId={}] Sended Packet Size={}, Offset={}, RemainPacket Size={}/{}, ", UpgradeCommand.UpgradeData, String.format("%.2f", tempb / tempa * 100), bd.getModemId(), bd.getMeterId(), sendPacket.length, offSet, remainPackateLength, tempa);

			/** data send retry check */
			dataSendRetryCheck(UpgradeCommand.UpgradeData, session, generalFrameData);
		} else {
			hasRemainData = false;
			logger.debug("All data sending complete!");
		}

		logger.debug("### SEND IMAGE - ok ###");
		return hasRemainData;
	}

	@Override
	public void executeAck(MultiSession session, GeneralFrame generalFrame) throws Exception {
		BypassDevice bd = session.getBypassDevice();
		logger.debug("### [cmdModemOTA Received ACK] ModemId={}, MeterId={}, GeneralFrame = {}", bd.getModemId(), bd.getMeterId(), generalFrame.toString());

		/*
		 * 보냈던 sequence number가 아니면 동일한 블럭을 재전송한다.
		 */
		int receivedSeq = generalFrame.getFrameSequence();
		int sendedSeq = bd.getFrameSequence();

		/*
		 * receivedSeq == 0 
		 *  : frameSequence를 사용하지 않는 버전일경우 처리
		 */
		if (receivedSeq == 0 || receivedSeq == sendedSeq) {
			purgeTransferImageTimer(); // Timer puge

			GeneralFrame newGFrame = CommandNIProxy.setGeneralFrameOption(FrameOption_Type.Firmware, null);
			newGFrame.setFrame();
			newGFrame.setNetworkType(generalFrame.getNetworkType());

			try {
				if (!sendImage(session, bd, newGFrame)) {

					/*
					 * Upgrade End Request
					 */
					newGFrame.setFrameControl_Ack(FrameControl_Ack.None);
					//generalFrame.setFrame();  필요시 주석풀어서 사용
					((Firmware) newGFrame.payload).setTargetType(0); // 0 : Modem F/W upgrade
					((Firmware) newGFrame.payload).setUpgradeCommand(6); // 06 : Upgrade End Request    
					((Firmware) newGFrame.payload).setImageLength(bd.getFw_bin().length); // Image length
					((Firmware) newGFrame.payload).setCrc(bd.getFwCRC()); // Image CRC
					byte[] generalFrameData = newGFrame.encode(null);

					session.write(generalFrameData);
					logger.debug("### STEP=[{}] Session write => [{}][{}]", UpgradeCommand.UpgradeEndRequest, newGFrame.toString(), Hex.decode(generalFrameData));

					
					String modemId = bd.getModemId();
					ModemDao modemDao = DataUtil.getBean(ModemDao.class);
					Modem modem = modemDao.get(modemId);
					
					/*
					 * OTA Download Event save.
					 */
					String openTime = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss");
					EV_SP_200_63_0_Action action2 = new EV_SP_200_63_0_Action();
					action2.makeEvent(TargetClass.valueOf(bd.getModemType().name()), modemId, bd.getTargetClass(), openTime, "HES", modem.getLocation());
					action2.updateOTAHistory((bd.getOtaTargetDeviceType() == DeviceType.Modem ? modemId : bd.getMeterId()), bd.getOtaTargetDeviceType(), openTime);

					/** data send retry check */
					dataSendRetryCheck(UpgradeCommand.UpgradeEndRequest, session, generalFrameData);
				}
			} catch (Exception e) {
				logger.error("Send Image Error - " + e, e);

				stopTransferImageTimer();
				logger.debug("## Timer 실패시 해지~! ==> needImangeBlockTransferRetry={}", needImangeBlockTransferRetry);

				/** Notify to Observers */
				notifyParams.put("result", false);
				notifyParams.put("resultMessage", e);
				notiFire(notifyParams);

				throw new Exception("Send Image Error - ", e);
			}
		} else {
			logger.warn("Invalid frame sequence received. SendedFrameSeq=[{}], ReceivedFrameSeq=[{}]", sendedSeq, receivedSeq);
		}
	}

	@Override
	public void executeStop(MultiSession session) throws Exception {
		logger.debug("call executeStop1 - Modem={}, Meter={}", session.getBypassDevice().getModemId(), session.getBypassDevice().getMeterId());
		deleteMultiSession(session);
		logger.debug("call executeStop2 - Modem={}, Meter={}", session.getBypassDevice().getModemId(), session.getBypassDevice().getMeterId());
	}

	/**
	 * Timer purge.
	 * 
	 * @return tasks removed from the queue.
	 */
	private void purgeTransferImageTimer() {
		int purgeCount = 0;
		needImangeBlockTransferRetry = false;

		if (blockTransferRetryTask != null) {
			blockTransferRetryTask.cancel();
		}

		if (blockTransferRetryTimer != null) {
			purgeCount = blockTransferRetryTimer.purge();
			logger.debug("## [RETRY_TASK][PURGE] taskName={}, {} tasks removed from the queue.", (blockTransferRetryTask == null ? "Null~!!" : blockTransferRetryTask.getTaskName()), purgeCount);
		}
	}

	/**
	 * Timer, Timer Task cancel
	 */
	private void stopTransferImageTimer() {
		needImangeBlockTransferRetry = false;
		if (blockTransferRetryTask != null) {
			blockTransferRetryTask.cancel();
			logger.debug("## [RETRY_TASK][STOP] taskName={}", blockTransferRetryTask.getTaskName());
		}

		if (blockTransferRetryTimer != null) {
			blockTransferRetryTimer.purge();
			blockTransferRetryTimer.cancel();
		}

		blockTransferRetryTimer = null;
		logger.debug("## Timer Task Stop.");
	}

	/**
	 * Notification to Observers
	 * 
	 * @param notifyParams
	 */
	private void notiFire(Map<String, Object> notifyParams) {
		logger.debug("Noti Fire~~!! ==> {}", notifyParams.toString());
		super.notifyToObserver(notifyParams);
	}

	/**
	 * 재전송해야할 필요가 있는지 체크하는 타이머 SEND_IAMGE_RETRY_TIMEOUT 초뒤에 실행,
	 * SEND_IAMGE_RETRY_TIMEOUT 초 간격으로 NEED_IMAGE_BLOCK_TRANSFER_MAX_RETRY_COUNT
	 * 만큼 재실행
	 * 
	 * @param session
	 * @param sendData
	 */
	private void dataSendRetryCheck(UpgradeCommand otaStep, MultiSession session, byte[] sendData) {
		String taskName = otaStep + "_DEFAULT_TASK";
		BypassDevice bd = session.getBypassDevice();
		if (bd != null && bd.getModemId() != null) {
			taskName = otaStep + "_" + bd.getModemId();
		}
		logger.debug("[RETRY_TASK][CREATE] TaskName=[{}], OTA_STEP=[{}], retyrCount={}, retryTime={}", taskName, otaStep, NEED_IMAGE_BLOCK_TRANSFER_MAX_RETRY_COUNT, SEND_IAMGE_RETRY_TIMEOUT);

		needImangeBlockTransferRetry = true;
		blockTransferRetryTask = new NeedImangeBlockTransferRetry(taskName, otaStep, this, session, sendData, NEED_IMAGE_BLOCK_TRANSFER_MAX_RETRY_COUNT);
		blockTransferRetryTimer.scheduleAtFixedRate(blockTransferRetryTask, SEND_IAMGE_RETRY_TIMEOUT * 1000, SEND_IAMGE_RETRY_TIMEOUT * 1000);
	}

	/**
	 * 이미지전송을 반복 실행하는 TimerTask
	 * 
	 * @author simhanger
	 *
	 */
	protected class NeedImangeBlockTransferRetry extends TimerTask {
		private String taskName;
		private UpgradeCommand otaStep;
		private NI_cmdModemOTAStart_Action_SP action;
		private MultiSession session;
		private byte[] req;
		private int maxRetryCount;
		private int retryCount;

		public NeedImangeBlockTransferRetry(String taskName, UpgradeCommand otaStep, NI_cmdModemOTAStart_Action_SP action, MultiSession session, byte[] req, int maxRetryCount) {
			this.taskName = taskName;
			this.otaStep = otaStep;
			this.action = action;
			this.session = session;
			this.req = req;
			this.maxRetryCount = maxRetryCount;
		}

		public String getTaskName() {
			return this.taskName;
		}

		@Override
		public void run() {
			if (needImangeBlockTransferRetry == true && this.retryCount < this.maxRetryCount) {
				this.session.write(req);
				logger.info("### [RETRY_TASK][RETRY] !!!! TaskName=[{}], STEP=[{}], [Meter={}, Modem={}] Data Send Retry={}/{} Session write => {}", taskName, otaStep, session.getBypassDevice().getMeterId(), session.getBypassDevice().getModemId(), retryCount + 1, maxRetryCount, Hex.decode(req));
				this.retryCount++;
			} else {
				this.cancel();

				long endTime = System.currentTimeMillis();
				String elapseTime = DateTimeUtil.getElapseTimeToString((endTime - session.getBypassDevice().getStartOTATime()));
				String msg = "";

				/** Notify to Observers */
				Map<String, Object> notifyParams = new HashMap<String, Object>();
				notifyParams.put("modemId", session.getBypassDevice().getModemId());
				notifyParams.put("meterId", session.getBypassDevice().getMeterId());
				notifyParams.put("fwVersion", session.getBypassDevice().getFwVersion());
				notifyParams.put("otaTargetDeviceType", session.getBypassDevice().getOtaTargetDeviceType());
				notifyParams.put("elapseTime", elapseTime);
				notifyParams.put("result", false);

				if (otaStep == UpgradeCommand.UpgradeData) {
					/* OTA END&RESULT Event */
					double tempa = session.getBypassDevice().getFw_bin().length;
					double tempb = session.getBypassDevice().getOffset();
					String progressRate = String.format("%.2f", tempb / tempa * 100);

					msg = OTA_UPGRADE_RESULT_CODE.OTAERR_NI_TRN_FAIL.getDesc() + "Progress Rate: " + progressRate + "%, Retry count=" + retryCount + "/" + maxRetryCount;
				} else {
					msg = OTA_UPGRADE_RESULT_CODE.OTAERR_RESPONSE_TIMEOUT.getDesc() + ", Retry count=" + retryCount + "/" + maxRetryCount;
				}

				notifyParams.put("resultMessage", msg);

				logger.warn("### [RETRY_TASK][CANCEL] !!!! TaskName=[{}], STEP=[{}], Info=", taskName, otaStep, notifyParams.toString());

				action.notiFire(notifyParams);
			}
		}
	}

	@Override
	public void executeResponse(MultiSession session, GeneralFrame generalFrame) throws Exception {

	}

	@Override
	public String getNotiGeneratorName() {
		return getActionTitle();
	}

}
