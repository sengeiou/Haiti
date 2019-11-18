package com.aimir.fep.meter.saver;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.MeteringFlag;
import com.aimir.constants.CommonConstants.MeteringType;
import com.aimir.fep.command.conf.KamstrupCIDMeta;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.command.mbean.CommandGW.OnDemandOption;
import com.aimir.fep.meter.AbstractMDSaver;
import com.aimir.fep.meter.data.BillingData;
import com.aimir.fep.meter.data.Instrument;
import com.aimir.fep.meter.data.LPData;
import com.aimir.fep.meter.entry.IMeasurementData;
import com.aimir.fep.meter.parser.I210Plus;
import com.aimir.fep.meter.parser.Kamstrup;
import com.aimir.fep.meter.parser.Kamstrup162;
import com.aimir.fep.meter.parser.ModemLPData;
import com.aimir.fep.util.DataUtil;
import com.aimir.model.device.Meter;
import com.aimir.util.DateTimeUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@Service
public class I210PlusMDSaver extends AbstractMDSaver {

	public void savePublic(IMeasurementData md) throws Exception {
		save(md);
	}
	
	@Override
	protected boolean save(IMeasurementData md) throws Exception {
		I210Plus parser = (I210Plus) md.getMeterDataParser();
		log.info(parser.toString());
		
		saveLPUsingLpNormalization(
				CommonConstants.MeteringType.getMeteringType(parser.getMeteringType()),
				md,
				parser.getLPData(),
				parser.getMDevId(),
				parser.getDeviceId(),
				parser.getMDevType(), 
				parser.getMeteringTime());
		
//		int interval = 60 / (parser.getPeriod() != 0 ? parser.getPeriod() : 1);
//		if (parser.getMeter().getLpInterval() == null || interval != parser.getMeter().getLpInterval())
//			parser.getMeter().setLpInterval(interval);
//
//		// TODO 정기검침으로 설정했는데 후에 변경해야 함.
//		saveMeteringData(MeteringType.Manual, parser.getMeteringTime().substring(0, 8),
//				parser.getMeteringTime().substring(8, 14), parser.getMeteringValue(), parser.getMeter(),
//				parser.getDeviceType(), parser.getDeviceId(), parser.getMDevType(), parser.getMDevId(),
//				parser.getMeterTime());
//
//		int[] flaglist = null;
//		if (parser.getLpData() != null && parser.getLpData().length > 0) {
//			ModemLPData data = parser.getLpData()[parser.getLpData().length - 1];
//			if (data == null || data.getLp() == null || data.getLp()[0].length == 0) {
//				log.warn("LP size is 0 then skip");
//			} else {
//
//				flaglist = new int[data.getLp()[0].length];
//				for (int flagcnt = 0; flagcnt < flaglist.length; flagcnt++) {
//					for (int ch = 0; ch < data.getLp().length; ch++) {
//						if (data.getLp()[ch][flagcnt] == 65535) {
//							flaglist[flagcnt] = MeteringFlag.Fail.getFlag();
//							data.getLp()[ch][flagcnt] = 0;
//						} else
//							flaglist[flagcnt] = MeteringFlag.Correct.getFlag();
//					}
//				}
//
//				saveLPData(MeteringType.Manual, data.getLpDate().substring(0, 8), data.getLpDate().substring(8, 12),
//						data.getLp(), flaglist, data.getBasePulse(), parser.getMeter(), parser.getDeviceType(),
//						parser.getDeviceId(), parser.getMDevType(), parser.getMDevId(), parser.getMeteringTime());
//			}
//		}

//		Kamstrup kamstrupMeta = parser.getKamstrupMeta();
//		Instrument[] instrument = kamstrupMeta.getInstrument();

//		savePowerQuality(parser.getMeter(), parser.getMeteringTime(), instrument, parser.getDeviceType(),
//				parser.getDeviceId(), parser.getMDevType(), parser.getMDevId());

		return true;
	}

	private String[] getMeterModelProtocol(String meterId) {
		TransactionStatus txstatus = null;
		String[] meterModelProtocolType = new String[2];
		try {
			txstatus = txmanager.getTransaction(null);
			Meter meter = meterDao.get(meterId);
			meterModelProtocolType[0] = meter.getModel().getName();
			if (meter.getModem() != null && meter.getModem().getProtocolType() != null)
				meterModelProtocolType[1] = meter.getModem().getProtocolType().name();
			txmanager.commit(txstatus);
		} catch (Exception e) {
			if (txstatus != null)
				txmanager.rollback(txstatus);
		}
		return meterModelProtocolType;
	}

	@Override
	public String relayValveOn(String mcuId, String meterId) {
		Object[] result = null;
		try {
			String[] meterModelProtocol = getMeterModelProtocol(meterId);

			CommandGW commandGw = DataUtil.getBean(CommandGW.class);

			// relay released
			result = KamstrupCIDMeta
					.getResult(
							commandGw.cmdKamstrupCID(mcuId, meterId,
									new String[] { KamstrupCIDMeta.CID.SetCutOffState.getCommand(),
											KamstrupCIDMeta.CID.SetCutOffState.getArgs()[1][0] }),
							meterModelProtocol[0]);

			if (result[0] != null && result[0].equals("Relays released for reconnection")) {
				result = KamstrupCIDMeta
						.getResult(
								commandGw.cmdKamstrupCID(mcuId, meterId,
										new String[] { KamstrupCIDMeta.CID.SetCutOffState.getCommand(),
												KamstrupCIDMeta.CID.SetCutOffState.getArgs()[2][0] }),
								meterModelProtocol[0]);
			}
		} catch (Exception e) {
			result = new String[] { e.getMessage() };
		}

		Meter meter = new Meter();
		meter.setMdsId(meterId);
		String str = MapToJSON((String[]) result);
		JsonArray ja = StringToJsonArray(str).getAsJsonArray();
		JsonObject jo = null;
		for (int i = 0; i < ja.size(); i++) {
			jo = ja.get(i).getAsJsonObject();
			if (jo.get("value").getAsString().equals("Relays connected")) {
				updateMeterStatusNormal(meter);
				ja.add(StringToJsonArray("{\"name\":\"Result\", \"value\":\"Success\"}"));
			}
		}

		return ja.toString();
	}

	@Override
	public String relayValveOff(String mcuId, String meterId) {
		Object[] result = null;
		try {
			String[] meterModelProtocol = getMeterModelProtocol(meterId);
			CommandGW commandGw = DataUtil.getBean(CommandGW.class);

			result = KamstrupCIDMeta
					.getResult(
							commandGw.cmdKamstrupCID(mcuId, meterId,
									new String[] { KamstrupCIDMeta.CID.SetCutOffState.getCommand(),
											KamstrupCIDMeta.CID.SetCutOffState.getArgs()[0][0] }),
							meterModelProtocol[0]);
		} catch (Exception e) {
			result = new String[] { e.getMessage() };
		}

		Meter meter = new Meter();
		meter.setMdsId(meterId);
		String str = MapToJSON((String[]) result);
		JsonArray ja = StringToJsonArray(str).getAsJsonArray();
		JsonObject jo = null;
		for (int i = 0; i < ja.size(); i++) {
			jo = ja.get(i).getAsJsonObject();
			if (jo.get("value").getAsString().equals("Relays disconnected by command")) {
				updateMeterStatusCutOff(meter);
				ja.add(StringToJsonArray("{\"name\":\"Result\", \"value\":\"Success\"}"));
			}
		}

		return ja.toString();
	}

	@Override
	public String relayValveStatus(String mcuId, String meterId) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        
        try {
            CommandGW commandGw = DataUtil.getBean(CommandGW.class);
            commandGw.cmdOnDemandMeter( mcuId, meterId, OnDemandOption.WRITE_OPTION_RELAYOFF.getCode());

            String str = relayValveStatus(mcuId, meterId);
            JsonArray ja = StringToJsonArray(str).getAsJsonArray();
            
            JsonObject jo = null;
            for (int i = 0; i < ja.size(); i++) {
                jo = ja.get(i).getAsJsonObject();
                if (jo.get("name").getAsString().equals("switchStatus") && jo.get("value").getAsString().equals("Off")) {
                    ja.add(StringToJsonArray("{\"name\":\"Result\", \"value\":\"Success\"}"));
                }
            }
            return ja.toString();
        }
        catch (Exception e) {
            resultMap.put("failReason", e.getMessage());
        }
        
        return MapToJSON(resultMap);
    }

	@Override
	public String syncTime(String mcuId, String meterId) {
		Object[] result = null;

		try {
			String[] meterModelProtocol = getMeterModelProtocol(meterId);
			CommandGW commandGw = DataUtil.getBean(CommandGW.class);
			result = KamstrupCIDMeta
					.getResult(
							commandGw
									.cmdKamstrupCID(mcuId, meterId,
											new String[] { KamstrupCIDMeta.CID.SetClock.getCommand(),
													KamstrupCIDMeta.CID.SetClock.getArgs()[0][0] }),
							meterModelProtocol[0]);

			// saveMeterTimeSyncLog(meter, (String)result[0], (String)result[1], 0);
		} catch (Exception e) {
			result = new String[] { e.getMessage() };
		}
		return MapToJSON((String[]) result);
	}

	@Override
	public String relayValveActivate(String mcuId, String meterId) {
		Object[] result = null;
		try {
			String[] meterModelProtocol = getMeterModelProtocol(meterId);
			CommandGW commandGw = DataUtil.getBean(CommandGW.class);

			// relay released
			result = KamstrupCIDMeta
					.getResult(
							commandGw.cmdKamstrupCID(mcuId, meterId,
									new String[] { KamstrupCIDMeta.CID.SetCutOffState.getCommand(),
											KamstrupCIDMeta.CID.SetCutOffState.getArgs()[1][0] }),
							meterModelProtocol[0]);
		} catch (Exception e) {
			result = new String[] { e.getMessage() };
		}
		return MapToJSON((String[]) result);
	}
}
