/**
 * (@)# HLSAuthForSORIA.java
 *
 * 2016. 4. 13.
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
package com.aimir.fep.bypass.decofactory.consts;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aimir.fep.bypass.decofactory.consts.DlmsConstants.XDLMS_APDU;
import com.aimir.fep.bypass.decofactory.consts.DlmsConstantsForIESCO.DlmsPiece;
import com.aimir.fep.protocol.security.HESPkiAPI;
import com.aimir.fep.protocol.security.OacServerApi;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;

/**
 * @author simhanger
 *
 */
public class HLSAuthForIESCO {
	private static Logger logger = LoggerFactory.getLogger(HLSAuthForIESCO.class);

	private HLSSecurityControl SC;
	private final int tLen = 12 * Byte.SIZE; // 96 bit

	/*
	 * OAC에서 미터키를 받기위한 디바이스용 인증서를 받기위한 디바이스번호(고정값)
	 */
	private String HES_DEVICE_SERIAL;

	// Iraq MOE
	//	private final byte[] EK = { (byte) 0x00, (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05, (byte) 0x06, (byte) 0x07, (byte) 0x08, (byte) 0x09, (byte) 0x0A, (byte) 0x0B, (byte) 0x0C, (byte) 0x0D, (byte) 0x0E, (byte) 0x0F };
	//	private final byte[] AK = { (byte) 0xD0, (byte) 0xD1, (byte) 0xD2, (byte) 0xD3, (byte) 0xD4, (byte) 0xD5, (byte) 0xD6, (byte) 0xD7, (byte) 0xD8, (byte) 0xD9, (byte) 0xDA, (byte) 0xDB, (byte) 0xDC, (byte) 0xDD, (byte) 0xDE, (byte) 0xDF };

	// SORIA 미터키 인증 적용하지 않을때 사용	
	//	private final byte[] EK = { (byte) 0xFE, (byte) 0xE0, (byte) 0xFE, (byte) 0xE0, (byte) 0xFE, (byte) 0xE0, (byte) 0xFE, (byte) 0xE0, (byte) 0xFE, (byte) 0xE0, (byte) 0xFE, (byte) 0xE0, (byte) 0xFE, (byte) 0xE0, (byte) 0xFE, (byte) 0xE0 };
	//	private final byte[] AK = { (byte) 0xFE, (byte) 0xA0, (byte) 0xFE, (byte) 0xA0, (byte) 0xFE, (byte) 0xA0, (byte) 0xFE, (byte) 0xA0, (byte) 0xFE, (byte) 0xA0, (byte) 0xFE, (byte) 0xA0, (byte) 0xFE, (byte) 0xA0, (byte) 0xFE, (byte) 0xA0 };

	// SORIA 미터키 인증 적용시 사용
	private byte[] EK;
	private byte[] AK;
	
	// Pakistan - IESCO
	private final String ekString = "30303030303030303030303030303030";
	private final String akString = "30303030303030303030303030303030";

	public static enum HLSSecurityControl {
		NONE(0x00), COMPRESSION(0x80), AUTHENTICATION_ONLY(0x10), ENCRYPTION_ONLY(0x20), AUTHENTICATION_ENCRYPTION(0x30);

		private int value;

		private HLSSecurityControl(int value) {
			this.value = value;
		}

		public byte[] getValue() {
			return new byte[] { (byte) value };
		}

		public static HLSSecurityControl getItem(byte value) {
			for (HLSSecurityControl fc : HLSSecurityControl.values()) {
				if (fc.value == value) {
					return fc;
				}
			}
			return null;
		}
	}


	public HLSAuthForIESCO() {
		// TODO Auto-generated constructor stub
	}
	
	public HLSAuthForIESCO(HLSSecurityControl securityMode, String meterId) throws Exception {
		this.SC = securityMode;

		if (securityMode == null || meterId == null || meterId.equals("")) {
			throw new Exception("HLSAuth init error.");
		}

		logger.debug("HLS Security Mode={}, MeterId={}", securityMode.name(), meterId);

		Properties prop = new Properties();
		try {
			prop.load(getClass().getClassLoader().getResourceAsStream("config/fmp.properties"));
			if (prop.containsKey("protocol.security.hes.deviceSerial")) {
				this.HES_DEVICE_SERIAL = prop.getProperty("protocol.security.hes.deviceSerial");
			} else {
				this.HES_DEVICE_SERIAL = "000H000000000003";
			}
			logger.debug("### HES DEVICE SERIAL = {}", HES_DEVICE_SERIAL);
		} catch (IOException e) {
			logger.error("Properties loading error - {}", e.getMessage());
			throw new Exception("Properties loading error.");
		}

		switch (securityMode) {
		case NONE:
			break;
		case COMPRESSION:
			break;
		case AUTHENTICATION_ONLY:
			/*
			 * For Using MeterSharedKey
			 *
			logger.debug("### getMeterSharedKey ###");
			OacServerApi oacApi = new OacServerApi();
			HashMap<String, String> sharedKey = oacApi.getMeterSharedKey(HES_DEVICE_SERIAL, meterId);
			if (sharedKey != null) {
				String unicastKey = sharedKey.get("UnicastKey");
				String authKey = sharedKey.get("AuthenticationKey");
				String pinCode = sharedKey.get("pin_arg");

				logger.debug("##############  unicastKey={}", unicastKey);
				logger.debug("##############  authKey={}", authKey);
				logger.debug("##############  pinCode={}", pinCode);

				HESPkiAPI pkiApi = new HESPkiAPI();
				logger.debug("[UnicastKey Decrypting...][{}]", unicastKey);
				EK = pkiApi.doPkiDecrypt(DataUtil.readByteString(unicastKey), DataUtil.readByteString(pinCode));
				logger.debug("EK    = {}", Hex.decode(EK)); // encryption_unicast_key

				logger.debug("[AuthenticationKey Decrypting...][{}]", authKey);
				AK = pkiApi.doPkiDecrypt(DataUtil.readByteString(authKey), DataUtil.readByteString(pinCode));
				logger.debug("AK     = {}", Hex.decode(AK)); // authentication_key

			} else {
				throw new Exception("Can not find Shared Key.");
			}
			*/
			
			/*
			 * For Using Pana MeterSharedKey
			 *
			logger.debug("### getPanaMeterSharedKey ###");			
			OacServerApi oacApi = new OacServerApi();
			HashMap<String, String> sharedKey = oacApi.getPanaMeterSharedKey(HES_DEVICE_SERIAL, meterId);
			if (sharedKey != null) {
				String unicastKey = sharedKey.get("UnicastKey");
				String authKey = sharedKey.get("AuthenticationKey");
				String pinCode = sharedKey.get("pin_arg");

				logger.debug("##############  unicastKey={}", unicastKey);
				logger.debug("##############  authKey={}", authKey);
				logger.debug("##############  pinCode={}", pinCode);

				logger.debug("[UnicastKey Decrypting...][{}]", unicastKey);
				EK = DataUtil.readByteString(unicastKey);
				logger.debug("EK    = {}", Hex.decode(EK)); // encryption_unicast_key

				logger.debug("[AuthenticationKey Decrypting...][{}]", authKey);
				AK = DataUtil.readByteString(authKey);
				logger.debug("AK     = {}", Hex.decode(AK)); // authentication_key

			} else {
				throw new Exception("Can not find Shared Key.");
			}
			*/
			
			/*
			 * Pakistan - IESCO : 고정키 사용.... 
			 * 추후 OAC를 이용해야하는 상황이면 수정할것.
			 */
			EK = DataUtil.readByteString(ekString);
			logger.debug("EK    = {}", Hex.decode(EK)); // encryption_unicast_key

			AK = DataUtil.readByteString(akString);
			logger.debug("AK    = {}", Hex.decode(AK)); // authentication_key
			break;
		case ENCRYPTION_ONLY:
			break;			
		case AUTHENTICATION_ENCRYPTION:
			/*
			 * Pakistan - IESCO에서 사용. 추후 OAC를 이용해야하는 상황이면 수정할것.
			 */
			EK = DataUtil.readByteString(ekString);
			logger.debug("EK    = {}", Hex.decode(EK)); // encryption_unicast_key

			AK = DataUtil.readByteString(akString);
			logger.debug("AK    = {}", Hex.decode(AK)); // authentication_key
			break;
		default:
			break;
		}
	}

	/*
	 * RESTful Service로 Feph가 Fepa를 호출한다.
	 */
	public void getMeterKeyForHLS(String meterId) throws Exception {
		if (meterId == null || meterId.equals("")) {
			throw new Exception("HLSAuth init error - no meterId");
		}

	}

	private Cipher getCipher(int mode, byte[] iv, byte[] aad) throws Exception {
		SecretKey sKey = new SecretKeySpec(EK, "AES");
		GCMParameterSpec params = new GCMParameterSpec(tLen, iv);

		Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding", "SunJCE");
		cipher.init(mode, sKey, params);
		cipher.updateAAD(aad);

		return cipher;
	}

	public byte[] doEncryption(byte[] ic, byte[] apTitle, byte[] information) {
		byte[] result = null;

		try {
			if (ic != null && apTitle != null && information != null) {
				/*
				 * P(plainText) = information
				 * EK:16byte
				 * IV:12byte = Sys-T(apTitle:8byte) + IC:4byte
				 * AAD
				 *  - Authentication Only      : SC:1byte + AK:16byte + (C) Information
				 *  - Encryption Only          : null 
				 *  - Authenticated encryption : SC:1byte + AK:16byte
				 */
				byte[] IV = DataUtil.append(apTitle, ic);
				byte[] AAD = null;

				switch (SC) {
				case NONE:
					break;
				case COMPRESSION:
					break;
				case AUTHENTICATION_ONLY:
					AAD = DataUtil.append(DataUtil.append(SC.getValue(), AK), information);
					result = getCipher(Cipher.ENCRYPT_MODE, IV, AAD).doFinal(); // Authentication only 모드에서는 plainText를 쓰지 않는다
					break;
				case ENCRYPTION_ONLY:
					break;
				case AUTHENTICATION_ENCRYPTION:
					AAD = DataUtil.append(SC.getValue(), AK);
					result = getCipher(Cipher.ENCRYPT_MODE, IV, AAD).doFinal(information);
					break;
				default:
					break;
				}

				logger.info("[ENCRYPTION] IC    = [{}]", Hex.decode(ic));
				logger.info("[ENCRYPTION] Sys-T = [{}]", Hex.decode(apTitle));
				logger.info("[ENCRYPTION] IV    = [{}]", Hex.decode(IV));
				logger.info("[ENCRYPTION] AAD   = [{}]", Hex.decode(AAD));
				logger.info("[ENCRYPTION] Plain Text = [{}]", Hex.decode(information));
				logger.info("[ENCRYPTION] Cyper Text = [{}]", Hex.decode(result));
			}
			
		} catch (Exception e) {
			logger.error("HLSAuth Encryption Error - {}", e);
			result = null;
		}

		return result;
	}
	
	public byte[] doDecryption(byte[] ic, byte[] apTitle, byte[] cipherText) {
		byte[] result = null;

		try {
			if (ic != null && apTitle != null && cipherText != null) {
				/*
				 * P(plainText) = information
				 * EK:16byte
				 * IV:12byte = Sys-T(apTitle:8byte) + IC:4byte
				 * AAD
				 *  - Authentication Only      : SC:1byte + AK:16byte + (C) Information
				 *  - Encryption Only          : null 
				 *  - Authenticated encryption : SC:1byte + AK:16byte
				 */
				byte[] IV = DataUtil.append(apTitle, ic);
				byte[] AAD = null;

				switch (SC) {
				case NONE:
					break;
				case COMPRESSION:
					break;
				case AUTHENTICATION_ONLY:
					AAD = DataUtil.append(DataUtil.append(SC.getValue(), AK), cipherText);
					result = getCipher(Cipher.DECRYPT_MODE, IV, AAD).doFinal(); // Authentication only 모드에서는 plainText를 쓰지 않는다
					break;
				case ENCRYPTION_ONLY:
					break;
				case AUTHENTICATION_ENCRYPTION:
					AAD = DataUtil.append(SC.getValue(), AK);
					result = getCipher(Cipher.DECRYPT_MODE, IV, AAD).doFinal(cipherText);
					break;
				default:
					break;
				}

				logger.info("[DECRYPTION] IC    = [{}]", Hex.decode(ic));
				logger.info("[DECRYPTION] Sys-T = [{}]", Hex.decode(apTitle));
				logger.info("[DECRYPTION] IV    = [{}]", Hex.decode(IV));
				logger.info("[DECRYPTION] AAD   = [{}]", Hex.decode(AAD));
				logger.info("[DECRYPTION] Cyper Text = [{}]", Hex.decode(cipherText));
				logger.info("[DECRYPTION] Plain Text = [{}]", Hex.decode(result));
			}
		} catch (Exception e) {
			logger.error("HLSAuth doDecryption Error - {}", e);
			result = null;
		}

		return result;
	}

	public byte[] getTagValue(byte[] ic, byte[] apTitle, byte[] information) {
		byte[] tagValue = null;

		if (ic != null && apTitle != null && information != null) {
			byte[] cipherText = doEncryption(ic, apTitle, information);
			tagValue = Arrays.copyOfRange(cipherText, cipherText.length - (tLen / Byte.SIZE), cipherText.length);

			logger.info("[ENCRYPTION] TAG_VALUE = [{}]", Hex.decode(tagValue));
		}

		return tagValue;
	}

	public boolean doValidation(byte[] apTitle, byte[] ic, byte[] information, byte[] responseTagValue) {
		boolean result = false;

		if (ic != null && apTitle != null && information != null && responseTagValue != null) {
			byte[] myTagValue = getTagValue(ic, apTitle, information);
			result = Arrays.equals(responseTagValue, myTagValue);

			if (!result) {
				logger.debug("[Action Response Validation] Org TagValue = [{}], Response TagValue = [{}]", Hex.decode(myTagValue), Hex.decode(responseTagValue));
			}
		}

		return result;
	}
	
	public byte[] getReqEncriptionGlobalCiphering(byte[] ic, byte[] information) {
		byte[] reqValue = new byte[] {};

		try {
			byte[] cipherText = new byte[] {};

			if (ic != null && information != null) {
				cipherText = doEncryption(ic, DlmsPiece.CLIENT_SYSTEM_TITLE.getBytes(), information);

				reqValue = DataUtil.append(reqValue, XDLMS_APDU.GLO_GET_REQUEST.getValue()); 
				reqValue = DataUtil.append(reqValue, new byte[1]); // length
				reqValue = DataUtil.append(reqValue, SC.getValue()); // SC
				reqValue = DataUtil.append(reqValue, ic); // IC
				reqValue = DataUtil.append(reqValue, cipherText); // Cipher Text
				reqValue[1] = DataUtil.getByteToInt(reqValue.length - 2); // request & request length 2바이트제외

				logger.info("[Global-get-request] XDLMS-APDU Type = {} [{}]", XDLMS_APDU.GLO_GET_REQUEST, Hex.decode(XDLMS_APDU.GLO_GET_REQUEST.getValue()));
				logger.info("[Global-get-request] REQ_VALUE[APDU + LENGTH + SC + IC + CIPHER_TEXT(+TAG)] = [{}]", Hex.decode(reqValue));
			}
		} catch (Exception e) {
			logger.error("HLSAuth getReqEncriptionGlobalCiphering Error - {}", e);
			reqValue = null;
		}

		return reqValue;
	}
	
	public byte[] getReqEncryptionDedicateCiphering(byte[] ic, byte[] information) {
		byte[] reqValue = new byte[] {};

		try {
			byte[] cipherText = new byte[] {};

			if (ic != null && information != null) {
				cipherText = doEncryption(ic, DlmsPiece.CLIENT_SYSTEM_TITLE.getBytes(), information);

				XDLMS_APDU cyperType = XDLMS_APDU.DED_GET_REQUEST;
				reqValue = DataUtil.append(reqValue, cyperType.getValue()); // ded-get-request
				reqValue = DataUtil.append(reqValue, new byte[1]); // length
				reqValue = DataUtil.append(reqValue, SC.getValue()); // SC
				reqValue = DataUtil.append(reqValue, ic); // IC
				reqValue = DataUtil.append(reqValue, cipherText); // Cipher Text
				reqValue[1] = DataUtil.getByteToInt(reqValue.length - 2); // request & request length 2바이트제외

				logger.info("[GET-REQ:DEDICATED_CIPHERING] XDLMS-APDU Type = {} [{}]", cyperType.name(), Hex.decode(cyperType.getValue()));
				logger.info("[GET-REQ:DEDICATED_CIPHERING] REQ_VALUE[APDU + LENGTH + SC + IC + CIPHER_TEXT(+TAG)] = [{}]", Hex.decode(reqValue));
			}
		} catch (Exception e) {
			logger.error("HLSAuth getReqEncryptionDedicateCiphering Error - {}", e);
			reqValue = null;
		}

		return reqValue;
	}
	
	
	public byte[] setReqEncryptionDedicateCiphering(byte[] ic, byte[] information) {
		byte[] reqValue = new byte[] {};

		try {
			byte[] cipherText = new byte[] {};

			if (ic != null && information != null) {
				cipherText = doEncryption(ic, DlmsPiece.CLIENT_SYSTEM_TITLE.getBytes(), information);

				XDLMS_APDU cyperType = XDLMS_APDU.DED_SET_REQUEST;
				reqValue = DataUtil.append(reqValue, cyperType.getValue()); // ded-get-request
				reqValue = DataUtil.append(reqValue, new byte[1]); // length
				reqValue = DataUtil.append(reqValue, SC.getValue()); // SC
				reqValue = DataUtil.append(reqValue, ic); // IC
				reqValue = DataUtil.append(reqValue, cipherText); // Cipher Text
				reqValue[1] = DataUtil.getByteToInt(reqValue.length - 2); // request & request length 2바이트제외

				logger.info("[SET-REQ:DEDICATED_CIPHERING] XDLMS-APDU Type = {} [{}]", cyperType.name(), Hex.decode(cyperType.getValue()));
				logger.info("[SET-REQ:DEDICATED_CIPHERING] REQ_VALUE[APDU + LENGTH + SC + IC + CIPHER_TEXT(+TAG)] = [{}]", Hex.decode(reqValue));
			}
		} catch (Exception e) {
			logger.error("HLSAuth setReqEncryptionDedicateCiphering Error - {}", e);
			reqValue = null;
		}

		return reqValue;
	}

	public byte[] actionReqEncryptionDedicateCiphering(byte[] ic, byte[] information) {
		byte[] reqValue = new byte[] {};

		try {
			byte[] cipherText = new byte[] {};

			if (ic != null && information != null) {
				cipherText = doEncryption(ic, DlmsPiece.CLIENT_SYSTEM_TITLE.getBytes(), information);

				XDLMS_APDU cyperType = XDLMS_APDU.DED_ACTIONREQUEST;

				reqValue = DataUtil.append(reqValue, cyperType.getValue()); // ded-actionRequest
				reqValue = DataUtil.append(reqValue, new byte[1]); // length
				reqValue = DataUtil.append(reqValue, SC.getValue()); // SC
				reqValue = DataUtil.append(reqValue, ic); // IC
				reqValue = DataUtil.append(reqValue, cipherText); // Cipher Text
				reqValue[1] = DataUtil.getByteToInt(reqValue.length - 2); // request & request length 2바이트제외

				logger.info("[ACTION-REQ:DEDICATED_CIPHERING] XDLMS-APDU Type = {} [{}]", cyperType.name(), Hex.decode(cyperType.getValue()));
				logger.info("[ACTION-REQ:DEDICATED_CIPHERING] REQ_VALUE[APDU + LENGTH + SC + IC + CIPHER_TEXT(+TAG)] = [{}]", Hex.decode(reqValue));
			}
		} catch (Exception e) {
			logger.error("HLSAuth actionReqEncryptionDedicateCiphering Error - {}", e);
			reqValue = null;
		}

		return reqValue;
	}

}
