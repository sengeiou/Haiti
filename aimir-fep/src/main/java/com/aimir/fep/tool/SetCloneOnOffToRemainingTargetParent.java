package com.aimir.fep.tool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import com.aimir.dao.system.DeviceModelDao;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.NIAttributeId;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.Hex;
import com.aimir.model.system.DeviceModel;
import com.aimir.util.DateTimeUtil;

/*
 * SetCloneOnOffToRemainingTargetParent tool 설명
 *  : Clone OTA를 진행시 가끔 Clone전파가 되지 않고 중간에 멈추는 경우가 있다.
 *    이럴경우에는 전파가 되지 않은 모뎀부터 다시 Clone 전파를 하도록 해당 모뎀의 Parent에 Command를 내려줘야하며 이때 사용하는 Tool이다.
 *    
 * @author simhanger
 *
 */
@Service
public class SetCloneOnOffToRemainingTargetParent {
	private static Logger logger = LoggerFactory.getLogger(SetCloneOnOffToRemainingTargetParent.class);

	@Autowired
	DeviceModelDao deviceModelDao;

	@PersistenceContext
	protected EntityManager em;

	private final String deviceModelName = "NAMR-P214SR";

	private void execCommand(String cloneCode, Integer cloneCount, String fwVersion, String executeType) {
		/*
		 * 1. 대상 List 산정
		 */
		List<String> targetList = targetList(cloneCode, fwVersion, executeType);
		if (targetList == null || targetList.size() <= 0) {
			return;
		}

		/*
		 * 2. Clone On/Off Command 실행
		 */
		logger.info("### CloneOn/Off Command Execute start... Target List size = {} ###", (targetList == null ? "Null~!!" : targetList.size()));

		CommandGW gw = DataUtil.getBean(CommandGW.class);
		Map<String, ?> result = new HashMap<String, String>();
		if (executeType.equals("HES")) {
			for (String target : targetList) {
				try {
					result = gw.setCloneOnOff(target, cloneCode, cloneCount);
					for (Entry<String, ?> e : result.entrySet()) {
						logger.debug("[MODEM ID:" + target + "]  key[" + e.getKey() + "], value[" + e.getValue() + "]");
					}
				} catch (Exception e) {
					logger.error("CloneOn/Off by HES Command execute error - " + e.getMessage(), e);
				}
			}
		} else if (executeType.equals("DCU")) {
			/** Clone Configuration */
			byte[] cloneCodeData = DataUtil.readByteString(cloneCode);
			byte[] cloneCountData = new byte[] { DataUtil.getByteToInt(cloneCount) };

			for (String target : targetList) {
				try {
					logger.debug("Target MODEM_ID = {}, Param={}", target, Hex.decode(DataUtil.append(cloneCodeData, cloneCountData)));

					result = gw.cmdExecDmdNiCommand(target, "SET", Hex.decode(NIAttributeId.CloneOnOff.getCode()), Hex.decode(DataUtil.append(cloneCodeData, cloneCountData)));

					for (Entry<String, ?> e : result.entrySet()) {
						logger.info("[MODEM_ID:{}] REQUEST_TYPE:{}] KEY[{}], VALUE[{}, RESULT[{}]]", target, NIAttributeId.CloneOnOff.name(), e.getKey(), e.getValue(), (result.containsKey("cmdResult") ? result.containsKey("cmdResult") : "[Fail] communication error"));
					}
				} catch (Exception e) {
					logger.error("CloneOn/Off Command by DCU execute error - " + e.getMessage(), e);
				}
			}
		} else {
			logger.error("Unknown execute type. please check execute type.");
			return;
		}
	}

	/**
	 * 대상 List 산정
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<String> targetList(String cloneCode, String fwVersion, String executeTyp) {
		/** DeviceModel 정보 추출 */
		DeviceModel modemModel = deviceModelDao.findByCondition("name", deviceModelName);
		if (modemModel != null) {
			logger.debug("### DeviceModel info => {}", modemModel.toString());
		} else {
			logger.error("Unknown DeviceModel. please check DeviceModel information.");
			return null;
		}

		List<String> targetList = null;
		logger.debug("### Create target List start.  CloneCode={}, fwVersion={} ###", cloneCode, fwVersion);

		if (cloneCode.equals("0314") || cloneCode.equals("0315")) { // Modem Clone On/Off
			/** Target Device List 추출 */
			try {
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append(" select id, device_serial, fw_ver, modem_id from modem ");
				sbQuery.append(" where id in ( ");
				sbQuery.append("    select distinct(modem_id) from modem where devicemodel_id = (select id from devicemodel where name = ?)  ");
				sbQuery.append("        and fw_ver is not null  ");
				sbQuery.append("        and fw_ver != ? ");
				sbQuery.append("        and modem_id is not null ");
				sbQuery.append("        and (modem_status is null or modem_status not in (select id from code where name in ('BreakDown' , 'Delete', 'Repair'))) ");
				sbQuery.append(" ) and fw_ver is not null ");
				sbQuery.append("  and fw_ver = ?    ");

				Query query = em.createNativeQuery(sbQuery.toString());
				query.setParameter(1, deviceModelName);
				query.setParameter(2, fwVersion);
				query.setParameter(3, fwVersion);

				List<Object[]> resultList = query.getResultList();
				logger.debug("Found target List size = {}", (resultList == null ? "Null~!" : resultList.size()));
				if (resultList != null && 0 < resultList.size()) {
					targetList = new ArrayList<String>();
					for (Object[] modem : resultList) {
						targetList.add(modem[0].toString());
						logger.debug("target id={}, modem_serial={}, fw_ver={}, parent_id={}", modem[0], modem[1], modem[2], modem[3]);
					}

					/*
					 * 현재(2018-02-01) SORIA PROD - RFModem(fwVersion:1.22)의 경우 CloneOn/Off Command 수신시 응답을 하지 못하는 문제점이 있어서
					 * Command 전송후 응답을 기다리지 않고 종료처리함. (Timeout을 5초로 설정)
					 */
					Double modemFwVer = Double.parseDouble(resultList.get(0)[2].toString());
					if (executeTyp.equals("HES") && modemFwVer <= 1.22) {
						FMPProperty.setProperty("protocol.dtls.response.timeout", "5");
					}
				}
			} catch (Exception e) {
				logger.error("Get target modem error - " + e.getMessage(), e);
			} finally {
				if (em != null) {
					em.close();
				}
			}
		} else if (cloneCode.equals("8798") || cloneCode.equals("8799")) { // Meter Clone On/Off
			/*
			 * 추후 필요시 개발할것.
			 */
		} else {
			logger.error("Unknown Clone code. please check your Clone code.");
		}

		logger.debug("### Create target List stop.  ###");
		return targetList;
	}

	/*
	 * params : 자세한 내용은 NI Protocol document 참조할것.
	 * 	1. cloneCode
	 * 		- 클론 코드가 맞아야지 클론을 On/Off 에 대한 명령어를 실행한다.코드에 따라 동작이 다르다.
	 *        0314 : 클론시 자신의 이미지를 사용한다.(자동전파 X)
	 *        0315 : 클론시 자신의 이미지를 사용한다.(자동전파 O)  ==> Modem Clone On/Off
	 *        8798 : 클론시 타장비 이미지를 사용한다.(자동전파 X)
	 *        8799 : 클론시 타장비 이미지를 사용한다.(자동전파 O)  ==> Meter Clone On/Off
	 *  2. cloneCount
	 *      - 클론을 실행할 시간 값을 의미하며, 단위는 15분이다. 
	 *        해당 값은 0, 20 ~ 96 까지 값일 때 유효한 값으로 받아 들인다.(그 외에 값은 에러 처리) 
	 *           Ex) 96 값을 설정 시 24시간 동안 클론 운영. 
	 *        클론을 종료시키고자 하면 0의 값을 준다.
	 *  3. fwVersion
	 *      - Clone OTA 하고자 하는 펌웨어 버전.
	 *        Command Target 선정시 fwVersion 보다 낮은 Device만 Target으로 선정하게 된다.
	 *  4. executeType
	 *      - DCU : DCU가 Clone On/Off 실행
	 *      - HES : HES가 Clone On/Off 실행
	 * @param args
	 */
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();

		logger.info("-----");
		logger.info("-----");
		logger.info("-----");
		logger.info("#### SetCloneOnOffToRemainingTargetParent Task start. ###");
		logger.info("args[] => {}", Arrays.deepToString(args));

		try {
			if (args == null || args.length < 3 || args[0] == null || args[1] == null || args[2] == null || args[3] == null) {
				logger.error("Invalid parameters. please check parameters.");
				return;
			}

			if (Integer.parseInt(args[1]) == 0 || (20 <= Integer.parseInt(args[1]) && Integer.parseInt(args[1]) <= 96)) {
				String cloneCode = args[0];
				int cloneCount = Integer.parseInt(args[1]);
				String fwVersion = args[2];
				String executeType = args[3];
				logger.info("Received Parameters : cloneCode={}, cloneCount={}, fwVersion={}, executeType={}", cloneCode, cloneCount, fwVersion, executeType);

				ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[] { "/config/spring-SetCloneOnOffToRemainingTargetParent.xml" });
				DataUtil.setApplicationContext(ctx);

				SetCloneOnOffToRemainingTargetParent task = (SetCloneOnOffToRemainingTargetParent) ctx.getBean(SetCloneOnOffToRemainingTargetParent.class);
				task.execCommand(cloneCode, cloneCount, fwVersion, executeType);
			} else {
				logger.error("Invalid Clone count range. please check clone count range. (0 or 20 ~ 96)");
			}
		} catch (Exception e) {
			logger.error("SetCloneOnOffToRemainingTargetParent excute error - " + e, e);
		} finally {
			logger.info("#### SetCloneOnOffToRemainingTargetParent Task finished - Elapse Time : {} ###", DateTimeUtil.getElapseTimeToString(System.currentTimeMillis() - startTime));
			System.exit(0);
		}
	}
}
