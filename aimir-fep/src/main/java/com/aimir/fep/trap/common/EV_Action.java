package com.aimir.fep.trap.common;

import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.model.device.EventAlertLog;
import com.aimir.notification.FMPTrap;

/**
 * Event Process Action Interface
 *
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-12-13 15:59:15 +0900 $,
 */
public interface EV_Action {

	/**
	 * OTA Upgrade type
	 */
	public enum OTA_UPGRADE_TYPE {
		  MODEM("1", TargetClass.Modem)
		, METER("2", TargetClass.EnergyMeter)
		, DCU_FW("3", TargetClass.DCU)
		, DCU_KERNEL("4", TargetClass.DCU)
		, DCU_COORDINATE("5", TargetClass.DCU)
		, THIRD_PARTY_COORDINATE("6", TargetClass.DCU)
		, THIRD_PARTY_MODEM("7", TargetClass.DCU)
		, UNKNOWN("", TargetClass.Unknown);

		private String code;
		private TargetClass targetClass;

		OTA_UPGRADE_TYPE(String code, TargetClass targetClass) {
			this.code = code;
			this.targetClass = targetClass;
		}

		public TargetClass getTargetClass() {
			return this.targetClass;
		}

		public static OTA_UPGRADE_TYPE getItem(String code) {
			for (OTA_UPGRADE_TYPE fc : OTA_UPGRADE_TYPE.values()) {
				if (fc.code.equals(code)) {
					return fc;
				}
			}
			return UNKNOWN;
		}
	}

	/**
	 * OTA Upgrade Code
	 * 
	#define MTRERR_NOERROR          0
	#define MTRERR_BUSY             1
	#define MTRERR_CONNECT_FAIL     2       // network layer fail
	#define MTRERR_INVALID_MODEM    3
	#define MTRERR_METERING_FAIL    4       // Metering 도중 에러 발생
	#define MTRERR_LINK_FAIL        5       // link layer fail
	#define MTRERR_NEGO_FAIL        6       // app layer fail
	#define MTRERR_SECURITY_FAIL    7
	#define MTRERR_TIMEOUT          8       // metering timeout
	#define MTRERR_INVALID_PARAM    9       // invalid parameter
	#define MTRERR_UNKNOWN_PARSER   10      // Unknown meter parser
	#define MTRERR_NO_NETWORK       11      // Network에 Join 되어 있지 않음
	#define MTRERR_NOT_SUPPORT      12      // 지원되지 않는 기능
	#define MTRERR_VERIFY_FAIL      13      // Verification fail
	#define MTRERR_INSTALL_FAIL     14      // Install fail
	#define MTRERR_WRITE_FAIL       15      // Write fail
	#define MTRERR_POWER_FAIL       16      // Power fail
	#define MTRERR_INVALID_METER    17      // Invalid meter
	#define MTRERR_INVALID_TYPE     18      // OTA type이 잘못되었을 때
	#define MTRERR_INVALID_FILE     19      // OTA용 Image file access fail
	#define MTRERR_INITIATE_FAIL    20      // Initialize fail
	#define MTRERR_ONGOING          21      // 현재 진행 중
	#define MTRERR_READY            22      // 실행 대기
	#define MTRERR_NO_METERKEY  23  // [SORIA] 미터키가 존재하지 않음 (ref. MskScanner)
	이 에러코드로 올라갑니다.
	*/	 
	
	
	
	public enum OTA_UPGRADE_RESULT_CODE {
		/* DCU Event */
		  OTAERR_NOERROR(0, "Success")
		, OTAERR_BUSY(1, "Busy")
		, OTAERR_CONNECT_FAIL(2, "Network layer fail")
		, OTAERR_INVALID_MODEM(3, "Invalid mode")
		, OTAERR_METERING_FAIL(4, "Metering fail")
		, OTAERR_LINK_FAIL(5, "Link layer fail")
		, OTAERR_NEGO_FAIL(6, "Negotiation fail")
		, OTAERR_SECURITY_FAIL(7, "Security fail")
		, OTAERR_TIMEOUT(8, "Metering time out")
		, OTAERR_INVALID_PARAM(9, "Invalid parameter")
		, OTAERR_UNKNOWN_PARSER(10, "Unknown meter parser")
		, OTAERR_NO_NETWORK(11, "No network")
		, OTAERR_NOT_SUPPORT(12, "Not support")
		, OTAERR_VERIFY_FAIL(13, "Verification fail")
		, OTAERR_INSTALL_FAIL(14, "Install fail")
		, OTAERR_WRITE_FAIL(15, "Write fail")
		, OTAERR_POWER_FAIL(16, "Power fail")
		, OTAERR_INVALID_METER(17, "Invalid meter")
		, OTAERR_INVALID_TYPE(18, "Invalid type")
		, OTAERR_INVALID_FILE(19, "Invalid file")
		, OTAERR_INITIATE_FAIL(20, "Initialize fail")
		, OTAERR_ONGOING(21, "Ongoing")
		, OTAERR_READY(22, "Ready")
		, OTAERR_NO_METERKEY(23, "No Meter key")
		, OTAERR_IMAGE_TRANSFER_NOT_ENABLED(24, "Image transfer not enabled")
		, OTAERR_TR_STATUS_UNKNOWN(25, "Unknown ImageTransfer status")
		, OTAERR_SAME_VERSION(26, "Same Version")
		

		
		/* HES Event */
		, OTAERR_BYPASS_TRN_FAIL(100, "Bypass Transaction fail")
		, OTAERR_NI_TRN_FAIL(101, "ImageBlock Transfer fail")
		, OTAERR_BYPASS_EXCEPTION_FAILE(102, "Bypass Exception fail")
		, OTAERR_BYPASS_EXCUTE_FAIL(103, "Bypass excute fail")
		, OTAERR_CRC_FAIL(104, "CRC fail") 
		, OTAERR_EXECUTE_FAIL(105, "Execute fail")
		, OTAERR_RESPONSE_TIMEOUT(106, "Can't received response")
		, OTAERR_OPTIONAL_DATA_CRC_FAIL(107, "Optional Data CRC fail") 
		
		, OTAERR_CLONE_ON_FAIL(200, "Clone ON fail")  // Clone ON fail. 2017.11.10 추가됨.
		, OTAERR_CLONE_OFF_FAIL(201, "Clone OFF fail")  // Clone OFF fail. 2018.04.13 추가됨.
		
		/* Common Event */
		, UNKNOWN(-1, "Unknown Error");

		private int code;
		private String desc;

		OTA_UPGRADE_RESULT_CODE(int code, String desc) {
			this.code = code;
			this.desc = desc;
		}

		public int getCode() {
			return this.code;
		}
		
		public String getDesc(){
			return this.desc;
		}

		public static OTA_UPGRADE_RESULT_CODE getItem(int code) {
			for (OTA_UPGRADE_RESULT_CODE fc : OTA_UPGRADE_RESULT_CODE.values()) {
				if (fc.code == code) {
					return fc;
				}
			}
			return UNKNOWN;
		}
	}

	/**
	 * System status
	 */
	public enum SYSTEM_STATUS {
		  SS_LOWBATTERY(1, "Low Battery")
		, SS_POWERFAIL(2, "Power Fail")
		, SS_HWERROR(4, "HW Error")
		, SS_APPLICATIONERROR(8, "Application Error")
		, SS_NETWORKERROR(16, "Network Error")
		, SS_AUTHENTICATIONERROR(32, "Authentication Error")
		, SS_RESERVED6(64, "Reserved")
		, SS_RESERVED7(128, "Reserved")
		, SS_RESERVED8(256, "Reserved")
		, SS_RESERVED9(512, "Reserved")
		, SS_RESERVED10(1024, "Reserved")
		, SS_RESERVED11(2048, "Reserved")
		, SS_RESERVED12(4096, "Reserved")
		, SS_RESERVED13(8192, "Reserved")
		, SS_UNLICENSED(16384, "Unlicensed")
		, SS_TESTMODE(32768, "Test Mode")
		
		/* Common Event */
		, Normal(0, "Normal")
		, Unknown(-1, "");

		private int code;
		private String desc;

		SYSTEM_STATUS(int code, String desc) {
			this.code = code;
			this.desc = desc;
		}

		public int getCode() {
			return this.code;
		}
		
		public String getDesc(){
			return this.desc;
		}

		public static SYSTEM_STATUS getItem(int code) {
			for (SYSTEM_STATUS fc : SYSTEM_STATUS.values()) {
				if (fc.code == code) {
					return fc;
				}
			}
			return Unknown;
		}
	}

	/**
	 * Network status
	 */
	public enum NETWORK_STATUS {
		  SS_LINKDOWN(1, "Link down")
		, SS_RESERVED1(2, "Reserved")
		, SS_RESERVED2(4, "Reserved")
		, SS_RESERVED3(8, "Reserved")
		, SS_RESERVED4(16, "Reserved")
		, SS_RESERVED5(32, "Reserved")
		, SS_RESERVED6(64, "Reserved")
		, SS_RESERVED7(128, "Reserved")
		, SS_RESERVED8(256, "Reserved")
		, SS_RESERVED9(512, "Reserved")
		, SS_RESERVED10(1024, "Reserved")
		, SS_RESERVED11(2048, "Reserved")
		, SS_RESERVED12(4096, "Reserved")
		, SS_RESERVED13(8192, "Reserved")
		, SS_KEEPALIVEFAIL(16384, "Keepalive fail")
		, SS_PINGFAIL(32768, "Ping fail")
		
		/* Common Event */
		, Normal(0, "Normal")
		, Unknown(-1, "");

		private int code;
		private String desc;

		NETWORK_STATUS(int code, String desc) {
			this.code = code;
			this.desc = desc;
		}

		public int getCode() {
			return this.code;
		}
		
		public String getDesc(){
			return this.desc;
		}

		public static NETWORK_STATUS getItem(int code) {
			for (NETWORK_STATUS fc : NETWORK_STATUS.values()) {
				if (fc.code == code) {
					return fc;
				}
			}
			return Unknown;
		}
	}

	/**
	 * execute event action
	 *
	 * @param trap
	 *            - FMP Trap(MCU Event)
	 * @param event
	 *            - Event Alert Log Data
	 */
	public void execute(FMPTrap trap, EventAlertLog event) throws Exception;
}
