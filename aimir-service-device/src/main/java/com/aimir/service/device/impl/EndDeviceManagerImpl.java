package com.aimir.service.device.impl;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.DefaultChannel;
import com.aimir.dao.device.EndDeviceDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.mvm.DayEMDao;
import com.aimir.dao.mvm.DayGMDao;
import com.aimir.dao.mvm.DayHMDao;
import com.aimir.dao.mvm.DayWMDao;
import com.aimir.dao.mvm.MeteringDayDao;
import com.aimir.dao.mvm.MonthEMDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.dao.system.ZoneDao;
import com.aimir.model.device.EndDevice;
import com.aimir.model.device.EndDeviceVO;
import com.aimir.model.device.Meter;
import com.aimir.model.mvm.DayEM;
import com.aimir.model.mvm.DayGM;
import com.aimir.model.mvm.DayHM;
import com.aimir.model.mvm.DayWM;
import com.aimir.model.system.Code;
import com.aimir.model.system.DecimalPattern;
import com.aimir.model.system.Location;
import com.aimir.model.system.Supplier;
import com.aimir.model.system.Zone;
import com.aimir.service.device.EndDeviceManager;
import com.aimir.util.CalendarUtil;
import com.aimir.util.Condition;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.DecimalUtil;
import com.aimir.util.StringUtil;
import com.aimir.util.Condition.Restriction;

/**
 * EndDeviceManagerImpl.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 6. 30.   v1.0       김상연         
 * 2011. 6. 30    v1.2       김상연        EndDeviceList 조회 (조건 : EndDevice)
 *
 */
@Service(value = "endDeviceManager")
@Transactional(readOnly = false)
public class EndDeviceManagerImpl implements EndDeviceManager {

	@SuppressWarnings("unused")
	private static Log logger = LogFactory.getLog(EndDeviceManagerImpl.class);
	
	@Autowired
	EndDeviceDao dao;

	@Autowired
	LocationDao locationDao;

	@Autowired
	DayEMDao dayEMDao;

	@Autowired
	DayWMDao dayWMDao;

	@Autowired
	DayHMDao dayHMDao;

	@Autowired
	DayGMDao dayGMDao;

	@Autowired
	MonthEMDao monthEMDao;

	@Autowired
	CodeDao codeDao;

	@Autowired
	ZoneDao zoneDao;

	@Autowired
	MeteringDayDao meteringDayDao;

	@Autowired
	MeterDao meterDao;
	
	@Autowired
	SupplierDao supplierDao;

	public List<EndDeviceVO> getEndDevices() {

		List<EndDeviceVO> result = new ArrayList<EndDeviceVO>();
		List<EndDevice> endDeviceList = dao.getAll();
		for (EndDevice endDevice : endDeviceList) {
			EndDeviceVO endDeviceVO = new EndDeviceVO(endDevice);
			result.add(endDeviceVO);
		}

		return result;
	}

	public Map<String, Object> getColumnData(Map<String, Object> params) {
		Map<String, Object> retMap = new HashMap<String, Object>();
		Integer supplierId = (Integer) params.get("supplierId");
		Integer endDeviceId = (Integer) params.get("endDeviceId");

		List<Location> root = locationDao.getParentsBySupplierId(supplierId);

		List<EndDeviceVO> endDeviceList = getEndDevicesVOByLocationId(root.get(
				0).getId(), endDeviceId, 1, 10, true);

		String code = (String) params.get("code");
		List<Code> codeList = codeDao.getChildCodes(code);
		List<Object> codeArray = new ArrayList<Object>();
		for (Code cd : codeList) {
			Map<String, Object> cdMap = new HashMap<String, Object>();
			cdMap.put("name", cd.getName());
			cdMap.put("id", cd.getId());
			codeArray.add(cdMap);
		}
		List<Code> children = codeDao.getChildCodes("1.9.1");
		List<Object> retCodeList = new ArrayList<Object>();
		for (Code cd : children) {
			Set<Code> child = cd.getChildren();
			for (Code childCode : child) {
				Map<String, Object> cdMap = new HashMap<String, Object>();
				cdMap.put("name", childCode.getName());
				cdMap.put("id", childCode.getId());
				retCodeList.add(cdMap);
			}
		}

		List<Object> locationArray = new ArrayList<Object>();

		if (root.size() > 0) {
			Map<String, Object> locMap = new HashMap<String, Object>();
			locMap.put("name", root.get(0).getName());
			locMap.put("id", root.get(0).getId());
			locationArray.add(locMap);
			Set<Location> child = root.get(0).getChildren();
			Iterator<Location> childIterator = child.iterator();

			while (childIterator.hasNext()) {
				Location loc = childIterator.next();
				Map<String, Object> locMap1 = new HashMap<String, Object>();
				locMap1.put("name", loc.getName());
				locMap1.put("id", loc.getId());
				locationArray.add(locMap1);
			}

		}

		retMap.put("locationList", locationArray);
		retMap.put("endDeviceList", endDeviceList);
		retMap.put("codeList", codeArray);
		retMap.put("kindList", retCodeList);

		return retMap;

	}

	public List<EndDeviceVO> getEndDevicesMetering() {

		List<EndDeviceVO> result = new ArrayList<EndDeviceVO>();
		List<EndDevice> endDeviceList = dao.getAll();

		for (EndDevice endDevice : endDeviceList) {
			EndDeviceVO endDeviceVO = new EndDeviceVO(endDevice);
			Set<Condition> conditionList = new HashSet<Condition>();
			conditionList.add(new Condition("id.yyyymmdd",
					new Object[] { DateTimeUtil
							.getCurrentDateTimeByFormat("yyyyMMdd") }, null,
					Restriction.EQ));
			conditionList.add(new Condition("enddevice.id",
					new Object[] { endDevice.getId() }, null, Restriction.EQ));
			List<DayEM> dayEMList = dayEMDao
					.getDayEMsByListCondition(conditionList);
			List<DayGM> dayGMList = dayGMDao
					.getDayGMsByListCondition(conditionList);
			List<DayWM> dayWMList = dayWMDao
					.getDayWMsByListCondition(conditionList);
			List<DayHM> dayHMList = dayHMDao
					.getDayHMsByListCondition(conditionList);

			if (dayEMList.size() > 0)
				endDeviceVO.setDayEM(dayEMList.get(0).getTotal() + "");
			if (dayGMList.size() > 0)
				endDeviceVO.setDayGM(dayGMList.get(0).getTotal() + "");
			if (dayWMList.size() > 0)
				endDeviceVO.setDayWM(dayWMList.get(0).getTotal() + "");
			if (dayHMList.size() > 0)
				endDeviceVO.setDayHM(dayHMList.get(0).getTotal() + "");
			result.add(endDeviceVO);
		}

		return result;
	}

	public List<EndDevice> getEndDevicesList() {
		return dao.getAll();
	}

	public EndDevice addEndDevice(EndDevice endDevice) {

		return dao.add(endDevice);
	}

	/**
	 * 먼저 meter의 endDeviceId 를 null로 업데이트하고,
	 * endDevice를 삭제한다.
	 * 
	 * @modify Yi Hanghee(javarouka@gmail.com, javarouka@nuritelecom.co.kr)
	 */
	@Transactional
	public void delete(Integer endDeviceId) {		
		EndDevice endDevice = dao.get(endDeviceId);
		Set<Meter> meters = endDevice.getMeters();
		if(meters != null && !meters.isEmpty()) {
			for (Meter meter : meters) {
				if(meter == null || meter.getId() == null) {
					continue;
				}
				logger.info(
					"[" + meter.getId() + "] enddevice data update null value"
				);
				meter.setEndDevice(null);
				meter.setEndDeviceId(endDevice.getId());
				meterDao.update(meter);
			}
		}
		dao.deleteById(endDeviceId);
	}

	public EndDevice getEndDevice(Integer endDeviceId) {
		return dao.get(endDeviceId);
	}

	public EndDevice getEndDevice(String serialNumber) {
	    return dao.findByCondition("serialNumber", serialNumber);
	}

	public void updateEndDevice(EndDevice endDevice) {
		dao.update(endDevice);

	}

	public List<EndDevice> getEndDevicesByLocationId(int locationId, int page,
			int count) {

		return dao.getEndDevicesByLocationId(locationId, page, count);
	}

	public List<EndDeviceVO> getEndDevicesVOByLocationId(int locationId,
			int endDeviceId, int page, int count, boolean metering) {
		List<EndDeviceVO> result = new ArrayList<EndDeviceVO>();

		try {
			if (locationId != -1) {

				List<EndDevice> endDeviceList = 
					getEndDevicesByLocationId(locationId, page, count);

				Location location = locationDao.get(locationId);
				for (EndDevice endDevice : endDeviceList) {

					EndDeviceVO endDeviceVO = new EndDeviceVO(endDevice);

					if (!metering) {
						result.add(endDeviceVO);
						continue;
					}
					Set<Condition> conditionList = new HashSet<Condition>();
					conditionList.add(new Condition("id.yyyymmdd",
							new Object[] { DateTimeUtil
									.getCurrentDateTimeByFormat("yyyyMMdd") },
							null, Restriction.EQ));
					if (endDevice.getModem() != null) {
						Integer modemId = endDevice.getModem().getId();
						conditionList
								.add(new Condition("modem.id",
										new Object[] { modemId }, null,
										Restriction.EQ));
					} else {
						Set<Condition> endDeviceCondition = new HashSet<Condition>(
								0);
						endDeviceCondition.add(new Condition("endDevice.id",
								new Object[] { endDevice.getId() }, null,
								Restriction.EQ));

						List<Meter> meterList = meterDao
								.findByConditions(endDeviceCondition);

						if (!meterList.isEmpty()) 
						{
							conditionList
							.add(new Condition("meter.id",
								new Object[] { meterList.get(0).getId() }, null,
								Restriction.EQ));
						} 
					}

					conditionList.add(new Condition("id.channel",
							new Object[] { DefaultChannel.Usage.getCode() },
							null, Restriction.EQ));
					List<DayEM> dayEMList = dayEMDao
							.getDayEMsByListCondition(conditionList);
					List<DayGM> dayGMList = dayGMDao
							.getDayGMsByListCondition(conditionList);
					List<DayWM> dayWMList = dayWMDao
							.getDayWMsByListCondition(conditionList);
					List<DayHM> dayHMList = dayHMDao
							.getDayHMsByListCondition(conditionList);

					if (dayEMList.size() > 0) {
						endDeviceVO.setDayEM(new BigDecimal(dayEMList
								.get(0).getTotal())
								+ "");
					}
					if (dayGMList.size() > 0)
						endDeviceVO.setDayGM(dayGMList.get(0)
								.getTotal()
								+ "");
					if (dayWMList.size() > 0)
						endDeviceVO.setDayWM(dayWMList.get(0)
								.getTotal()
								+ "");
					if (dayHMList.size() > 0)
						endDeviceVO.setDayHM(dayHMList.get(0)
								.getTotal()
								+ "");
					result.add(endDeviceVO);
				}

				Set<Location> child = location.getChildren();
				Iterator<Location> iterator = child.iterator();

				while (iterator.hasNext()) {
					List<EndDevice> childEndDeviceList = getEndDevicesByLocationId(iterator
							.next().getId());
					for (EndDevice endDevice : childEndDeviceList) {
						EndDeviceVO endDeviceVO = new EndDeviceVO(endDevice);
						if (!metering) {
							result.add(endDeviceVO);
							continue;
						}
						Set<Condition> conditionList = new HashSet<Condition>();
						conditionList
								.add(new Condition(
										"id.yyyymmdd",
										new Object[] { DateTimeUtil
												.getCurrentDateTimeByFormat("yyyyMMdd") },
										null, Restriction.EQ));
						if (endDevice.getModem() != null) {
							Integer modemId = endDevice.getModem().getId();
							conditionList
									.add(new Condition("modem.id",
											new Object[] { modemId }, null,
											Restriction.EQ));
						} else {
							Set<Condition> endDeviceCondition = new HashSet<Condition>(
									0);
							endDeviceCondition.add(new Condition("endDevice.id",
									new Object[] { endDevice.getId() }, null,
									Restriction.EQ));

							List<Meter> meterList = meterDao
									.findByConditions(endDeviceCondition);

							if(!meterList.isEmpty())
							{
								conditionList
								.add(new Condition("meter.id",
									new Object[] { meterList.get(0).getId() }, null,
									Restriction.EQ));
							}
						}

						conditionList
								.add(new Condition("id.channel",
										new Object[] { DefaultChannel.Usage
												.getCode() }, null,
										Restriction.EQ));

						List<DayEM> dayEMList = dayEMDao
								.getDayEMsByListCondition(conditionList);
						List<DayGM> dayGMList = dayGMDao
								.getDayGMsByListCondition(conditionList);
						List<DayWM> dayWMList = dayWMDao
								.getDayWMsByListCondition(conditionList);
						List<DayHM> dayHMList = dayHMDao
								.getDayHMsByListCondition(conditionList);

						if (dayEMList.size() > 0) {
							endDeviceVO.setDayEM(new BigDecimal(
									dayEMList.get(0).getTotal())
									+ "");
						}
						if (dayGMList.size() > 0)
							endDeviceVO.setDayGM(dayGMList.get(0)
									.getTotal()
									+ "");
						if (dayWMList.size() > 0)
							endDeviceVO.setDayWM(dayWMList.get(0)
									.getTotal()
									+ "");
						if (dayHMList.size() > 0)
							endDeviceVO.setDayHM(dayHMList.get(0)
									.getTotal()
									+ "");
						result.add(endDeviceVO);
					}
				}
			} else {

				EndDevice ed = dao.get(endDeviceId);

				EndDeviceVO endDeviceVO = new EndDeviceVO(ed);

				if (!metering) {
					result.add(endDeviceVO);
					return result;
				}
				Set<Condition> conditionList = new HashSet<Condition>();
				conditionList.add(new Condition("id.yyyymmdd",
						new Object[] { DateTimeUtil
								.getCurrentDateTimeByFormat("yyyyMMdd") },
						null, Restriction.EQ));
				if (ed.getModem() != null) {
					Integer modemId = ed.getModem().getId();
					conditionList
							.add(new Condition("modem.id",
									new Object[] { modemId }, null,
									Restriction.EQ));
				} else {
					Set<Condition> endDeviceCondition = new HashSet<Condition>(
							0);
					endDeviceCondition.add(new Condition("endDevice.id",
							new Object[] { ed.getId() }, null,
							Restriction.EQ));

					List<Meter> meterList = meterDao
							.findByConditions(endDeviceCondition);
					conditionList
					.add(new Condition("meter.id",
							new Object[] { meterList.get(0).getId() }, null,
							Restriction.EQ));
				}
				conditionList.add(new Condition("modem.id",
						new Object[] { Integer.parseInt(endDeviceVO
								.getModemId()) }, null, Restriction.EQ));

				conditionList.add(new Condition("id.channel",
						new Object[] { DefaultChannel.Usage.getCode() }, null,
						Restriction.EQ));
				List<DayEM> dayEMList = dayEMDao
						.getDayEMsByListCondition(conditionList);
				List<DayGM> dayGMList = dayGMDao
						.getDayGMsByListCondition(conditionList);
				List<DayWM> dayWMList = dayWMDao
						.getDayWMsByListCondition(conditionList);
				List<DayHM> dayHMList = dayHMDao
						.getDayHMsByListCondition(conditionList);

				if (dayEMList.size() > 0) {
					endDeviceVO.setDayEM(new BigDecimal(dayEMList.get(
							0).getTotal())
							+ "");
				}
				if (dayGMList.size() > 0)
					endDeviceVO.setDayGM(dayGMList.get(0).getTotal()
							+ "");
				if (dayWMList.size() > 0)
					endDeviceVO.setDayWM(dayWMList.get(0).getTotal()
							+ "");
				if (dayHMList.size() > 0)
					endDeviceVO.setDayHM(dayHMList.get(0).getTotal()
							+ "");
				result.add(endDeviceVO);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public List<EndDeviceVO> getEndDevices(int page, int count) {
		List<EndDeviceVO> result = new ArrayList<EndDeviceVO>();
		List<EndDevice> endDeviceList = dao.getEndDevices(page, count);
		for (EndDevice endDevice : endDeviceList) {
			EndDeviceVO endDeviceVO = new EndDeviceVO(endDevice);
			result.add(endDeviceVO);
		}

		return result;
	}

	public List<EndDevice> getEndDevicesByLocationId(int locationId) {
		return dao.getEndDevicesByLocationId(locationId);
	}

	@SuppressWarnings("unchecked")
	public HashMap<String, Object> getCompareFacilityDayData(Map<String, Object> condition) {
		Integer modemId = (Integer) condition.get("modemId");
		Integer endDeviceId = (Integer) condition.get("endDeviceId");
		
		// XXX: 모뎀아이디와 엔드디바이스가 둘다 존재하지 않는 경우가 있을지...?
		if (/*modemId == -1 && */endDeviceId != -1) {
			Set<Condition> endDeviceCondition = new HashSet<Condition>(0);
			endDeviceCondition.add(new Condition("endDevice.id",
					new Object[] { endDeviceId }, null, Restriction.EQ));

			List<Meter> meterList = meterDao.findByConditions(endDeviceCondition);
			if(meterList.size() > 0) {
				condition.put("endDeviceId", (meterList.get(0)).getId());
				condition.put("convert", false);
			}
			else {
				condition.put("endDeviceId", modemId);
				condition.put("convert", true);
			}
		} 
		else {
			condition.put("endDeviceId", modemId);
			condition.put("convert", true);
		}
		List<Object> compareFacility = dayEMDao
				.getCompareFacilityDayData(condition);
		if (!compareFacility.isEmpty()) {
			return (HashMap<String, Object>) compareFacility.get(0);
		} else
			return new HashMap<String, Object>();
	}

	@SuppressWarnings("unchecked")
	public HashMap<String, Object> getCompareFacilityMonthData(Map<String, Object> condition) {
		Integer modemId = (Integer) condition.get("modemId");
		Integer endDeviceId = (Integer) condition.get("endDeviceId");

		// XXX: 모뎀아이디와 엔드디바이스가 둘다 존재하지 않는 경우가 있을지...?
		if (/*modemId == -1 && */endDeviceId != -1) {
			Set<Condition> endDeviceCondition = new HashSet<Condition>(0);
			endDeviceCondition.add(new Condition("endDevice.id",
					new Object[] { endDeviceId }, null, Restriction.EQ));

			List<Meter> meterList = meterDao.findByConditions(endDeviceCondition);
			if(meterList.size() > 0) {
				condition.put("endDeviceId", (meterList.get(0)).getId());
				condition.put("convert", false);
			}
			else {
				condition.put("endDeviceId", modemId);
				condition.put("convert", true);
			}
		} 
		else {
			condition.put("endDeviceId", modemId);
			condition.put("convert", true);
		}
		List<Object> compareFacility = monthEMDao
				.getCompareFacilityMonthData(condition);
		if (!compareFacility.isEmpty()) {
			return (HashMap<String, Object>) compareFacility.get(0);
		} else
			return new HashMap<String, Object>();
	}

	public List<EndDevice> getEndDevicesByCodeId(Integer codeId) {
		Set<Condition> conditionList = new HashSet<Condition>();
		conditionList.add(new Condition("categoryCode.id",
				new Object[] { codeId }, null, Restriction.EQ));
		return dao.findByConditions(conditionList);
	}

	/**
	 * 입력된 Zone 의 최하위 zone 에 할당된 EndDevice 목록을 조회한다.
	 * 
	 * @param zoneId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Object> getEndDeviceByZone(Map<String, Object> params) {

		String zoneId = StringUtil.nullToBlank(params.get("zoneId"));
		String supplierId = StringUtil.nullToBlank(params.get("supplierId"));
		
		Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));
		DecimalFormat df = DecimalUtil.getDecimalFormat(supplier.getMd());
		
		// 입력된 zone 의 최하위 zoneId 목록을 조회
		List<Integer> zoneIdList = null;
		if (!"".equals(zoneId)) {
			zoneIdList = zoneDao.getLeafZoneId(Integer.parseInt(zoneId));
		} else {
			zoneIdList = zoneDao.getLeafZoneId(null);
		}

		// 최하위 EndDevice 분류에 해당하는 EndDevice 목록조회
		List<EndDevice> endDeviceList = dao.getEndDevicesByzones(zoneIdList);

		List<Object> resultList = new ArrayList<Object>();
		Map<String, Object> resultMap = null;
		for (EndDevice endDevice : endDeviceList) {

			// 검침데이터와 조인을 하기위한 IN 조건 내용 조립
			// 1.EndDevice ID 목록
			// 2.EndDevice 의 Meter ID 목록
			// 3.EndDevice 의 Modem ID 목록,EndDevice 의 Meter 의 Modem ID 목록
			List<Integer> endDeviceId = new ArrayList<Integer>();
			List<Integer> modemId = new ArrayList<Integer>();
			List<Integer> meterId = new ArrayList<Integer>();

			// EndDevice의 ServiceType 별 현재일자 사용량조회
			endDeviceId.add(endDevice.getId());
			if (endDevice.getModem() != null) {
				modemId.add(endDevice.getModem().getId());
			}
			if (endDevice.getMeters() != null) {
				for (Meter meter : endDevice.getMeters()) {
					meterId.add(meter.getId());
					if (meter.getModem() != null) {
						modemId.add(meter.getModem().getId());
					}
				}
			}

			// IN 문이 OR 조건으로 걸리는데 내용이없을경우 쿼리 오류발생하므로
			// -1 을 설정해준다.
			if (endDeviceId.size() < 1) {
				endDeviceId.add(-1);
			}
			if (modemId.size() < 1) {
				modemId.add(-1);
			}
			if (meterId.size() < 1) {
				meterId.add(-1);
			}

			Map<String, Object> usageParam = new HashMap<String, Object>();
			usageParam.put("endDeviceId", endDeviceId);
			usageParam.put("modemId", modemId);
			usageParam.put("meterId", meterId);
			usageParam.put("today", CalendarUtil.getCurrentDate());

			Map<String, Object> emparams = new HashMap<String, Object>();
			emparams.put("dst", 0);
			emparams.put("channel", DefaultChannel.Usage.getCode());
			emparams.put("meterType", CommonConstants.MeterType.EnergyMeter
					.getDayClassName());
			List<Object> emList = meteringDayDao.getUsageForEndDevicesByDay(
					usageParam, emparams);

			Map<String, Object> gmparams = new HashMap<String, Object>();
			gmparams.put("dst", 0);
			gmparams.put("channel", DefaultChannel.Usage.getCode());
			gmparams.put("meterType", CommonConstants.MeterType.GasMeter
					.getDayClassName());
			List<Object> gmList = meteringDayDao.getUsageForEndDevicesByDay(
					usageParam, gmparams);

			Map<String, Object> wmparams = new HashMap<String, Object>();
			wmparams.put("dst", 0);
			wmparams.put("channel", DefaultChannel.Usage.getCode());
			wmparams.put("meterType", CommonConstants.MeterType.WaterMeter
					.getDayClassName());
			List<Object> wmList = meteringDayDao.getUsageForEndDevicesByDay(
					usageParam, wmparams);

			Map<String, Object> hmparams = new HashMap<String, Object>();
			hmparams.put("dst", 0);
			hmparams.put("channel", DefaultChannel.Usage.getCode());
			hmparams.put("meterType", CommonConstants.MeterType.HeatMeter
					.getDayClassName());
			List<Object> hmList = meteringDayDao.getUsageForEndDevicesByDay(
					usageParam, hmparams);
			
			Map<String, Object> emMap = new HashMap<String, Object>();
			Map<String, Object> gmMap = new HashMap<String, Object>();
			Map<String, Object> wmMap = new HashMap<String, Object>();
			Map<String, Object> hmMap = new HashMap<String, Object>();

			if (emList != null && emList.size() > 0) {
				emMap = (Map<String, Object>) emList.get(0);
			}
			if (gmList != null && gmList.size() > 0) {
				gmMap = (Map<String, Object>) gmList.get(0);
			}
			if (wmList != null && wmList.size() > 0) {
				wmMap = (Map<String, Object>) wmList.get(0);
			}
			if (hmList != null && hmList.size() > 0) {
				hmMap = (Map<String, Object>) hmList.get(0);
			}

			String emUsage = StringUtil.nullToBlank(emMap.get("total"));
			String gmUsage = StringUtil.nullToBlank(gmMap.get("total"));
			String wmUsage = StringUtil.nullToBlank(wmMap.get("total"));
			String hmUsage = StringUtil.nullToBlank(hmMap.get("total"));
			
			if (emUsage.length() > 0) {
				emUsage = df.format(Double.parseDouble(emUsage));
			}
			if (gmUsage.length() > 0) {
				gmUsage = df.format(Double.parseDouble(gmUsage));
			}
			if (wmUsage.length() > 0) {
				wmUsage = df.format(Double.parseDouble(wmUsage));
			}
			if (hmUsage.length() > 0) {
				hmUsage = df.format(Double.parseDouble(hmUsage));
			}
			
			// 결과Map 생성
			resultMap = new HashMap<String, Object>();
			resultMap.put("id", endDevice.getId());
			resultMap.put("zone", endDevice.getZone().getName());
			resultMap.put("location", endDevice.getLocation().getName());
			resultMap.put("category", endDevice.getCategoryCode().getDescr());
			resultMap.put("manufacturer", endDevice.getManufacturer());
			resultMap.put("modelName", endDevice.getModelName());
			resultMap.put("friendlyName", endDevice.getFriendlyName());
			resultMap.put("status", endDevice.getStatusCode().getDescr());
			resultMap.put("serialNumber", endDevice.getSerialNumber());
			resultMap.put("emUsage", emUsage);
			resultMap.put("gmUsage", gmUsage);
			resultMap.put("wmUsage", wmUsage);
			resultMap.put("hmUsage", hmUsage);
			
			resultList.add(resultMap);
		}

		return resultList;
	}

	/**
	 * 입력된 Location 에 할당된 EndDevice 목록을 조회한다.
	 * 
	 * @param zoneId
	 * @return
	 */
	public List<Object> getEndDeviceByLocation(Map<String, Object> params) {

		String locationId = StringUtil.nullToBlank(params.get("locationId"));

		List<Integer> locationIdList = null;
		if ("".equals(locationId)) {
			locationIdList = locationDao.getChildLocationId(null);
		} else {
			locationIdList = locationDao.getChildLocationId(Integer
					.parseInt(locationId));
			locationIdList.add(Integer.parseInt(locationId));
		}

		List<EndDevice> endDeviceList = null;
		// 최하위 EndDevice 분류에 해당하는 EndDevice 목록조회
		endDeviceList = dao.getEndDevicesByLocations(locationIdList);

		List<Object> resultList = new ArrayList<Object>();
		Map<String, Object> resultMap = null;
		try{
			for (EndDevice endDevice : endDeviceList) {
	
				// 결과Map 생성
				resultMap = new HashMap<String, Object>();
				resultMap.put("id", endDevice.getId());
				resultMap.put("zone", endDevice.getZone() == null ? "" : endDevice
						.getZone().getName());
				resultMap.put("location", endDevice.getLocation().getName());
				resultMap.put("category", endDevice.getCategoryCode().getDescr());
				resultMap.put("manufacturer", endDevice.getManufacturer());
				resultMap.put("modelName", endDevice.getModelName());
				resultMap.put("friendlyName", endDevice.getFriendlyName());
				resultMap.put("status", endDevice.getStatusCode().getDescr());
				resultMap.put("serialNumber", endDevice.getSerialNumber());
	
				resultList.add(resultMap);
			}
		}catch(java.lang.NullPointerException e){
			e.printStackTrace();
		}
		return resultList;
	}
	
	

	/**
	 * EndDevice 의 Zone 을 null 로 업데이트 한다.
	 * 
	 * @param endDeviceIdList
	 */
	public void removeEndDeviceFromZone(List<Object> endDeviceIdList) {
		for (Object obj : endDeviceIdList) {
			Integer id = (Integer) obj;
			EndDevice endDevice = dao.get(id);
			endDevice.getZone().setId(null);
			dao.updateZoneOfEndDevice(endDevice);
		}
	}

	/**
	 * 입력받은 Zone 을 EndDevice 에 업데이트 한다.
	 * 
	 * @param params
	 */
	@SuppressWarnings("unchecked")
	public void addEndDeviceToZone(Map<String, Object> params) {
		List<Object> endDeviceIdList = (List<Object>) params.get("endDeviceId");
		Integer zoneId = (Integer) params.get("zoneId");

		Zone zone = zoneDao.get(zoneId);

		for (Object obj : endDeviceIdList) {
			Integer id = (Integer) obj;
			EndDevice endDevice = dao.get(id);
			endDevice.setZone(zone);
			dao.updateZoneOfEndDevice(endDevice);
		}
	}

	/* (non-Javadoc)
	 * @see com.aimir.service.device.EndDeviceManager#getEndDeviceList(com.aimir.model.device.EndDevice)
	 */
	public List<EndDevice> getEndDeviceList(EndDevice endDevice) {

		return dao.getEndDevices(endDevice);
	}
	
	/**
	 * 엔드디바이스 관련 기본 정보를 제공
	 * @param supplierId
	 * @return Map 형식의 로케이션, 코드, 종류 데이터
	 */
	@Override
	public Map<String, Object> getMetaData(int supplierId) {
		Map<String, Object> retMap = new HashMap<String, Object>();
		List<Location> root = locationDao.getParentsBySupplierId(supplierId);
		String code = "1.9.2";
		List<Code> codeList = codeDao.getChildCodes(code);
		List<Object> codeArray = new ArrayList<Object>();
		for (Code cd : codeList) {
			Map<String, Object> cdMap = new HashMap<String, Object>();
			cdMap.put("name", cd.getDescr());
			cdMap.put("id", cd.getId());
			codeArray.add(cdMap);
		}
		
		code = "1.10";
		List<Code> mcodeList = codeDao.getChildCodes(code);
		List<Object> mcodeArray = new ArrayList<Object>();
		for (Code cd : mcodeList) {
			Map<String, Object> cdMap = new HashMap<String, Object>();
			cdMap.put("name", cd.getDescr());
			cdMap.put("id", cd.getId());
			mcodeArray.add(cdMap);
		}
		
		List<Code> children = codeDao.getChildCodes("1.9.1");
		List<Map<String,Object>> retCodeList = new ArrayList<Map<String,Object>>();
		for (Code cd : children) {
			Set<Code> child = cd.getChildren();
			for (Code childCode : child) {
				Map<String, Object> cdMap = new HashMap<String, Object>();
				cdMap.put("name", childCode.getDescr());
				cdMap.put("id", childCode.getId());
				retCodeList.add(cdMap);
			}
		}

		 Collections.sort(retCodeList, new Comparator<Map<String,Object>>(){

				@Override
				public int compare(Map<String, Object> o1, Map<String, Object> o2) {
					 String firstValue =  (String) o1.get("name");
					 String secondValue = (String) o2.get("name");
				    return firstValue.compareToIgnoreCase(secondValue);
				}
	        	
	        });
		 
		List<Object> locationArray = new ArrayList<Object>();

		if (root.size() > 0) {
			Map<String, Object> locMap = new HashMap<String, Object>();
			locMap.put("name", root.get(0).getName());
			locMap.put("id", root.get(0).getId());
			locationArray.add(locMap);
			Set<Location> child = root.get(0).getChildren();
			Iterator<Location> childIterator = child.iterator();

			while (childIterator.hasNext()) {
				Location loc = childIterator.next();
				Map<String, Object> locMap1 = new HashMap<String, Object>();
				locMap1.put("name", loc.getName());
				locMap1.put("id", loc.getId());
				locationArray.add(locMap1);
				
				/*  하위단의 location(level2에 해당하는 location) 정보 가져오기.
				 *   @modify prrain
				 * */
				List<Location> locchild = locationDao.getChildren(loc.getId(),loc.getSupplierId());
				
				if(locchild != null && !locchild.isEmpty()){
					for(int i=0;i< locchild.size();i++){
						Map<String, Object> locMap2 = new HashMap<String, Object>();
						locMap2.put("name", locchild.get(i).getName());
						locMap2.put("id", locchild.get(i).getId());
						locationArray.add(locMap2);
					}
				}				
			}
		}

		retMap.put("locationList", locationArray);
		retMap.put("codeList", codeArray);
		retMap.put("mcodeList", mcodeArray);
		retMap.put("kindList", retCodeList);

		return retMap;

	}
	
	@Override
	public List<EndDeviceVO> getEndDevicesVOByLocationIdExt(
			int locationId,
			int endDeviceId, 
			int start, 
			int limit, 
			boolean metering) {
		
		List<EndDeviceVO> result = new ArrayList<EndDeviceVO>();
		List<EndDevice> endDevices = null;
		if (locationId != -1) {
			List<Integer> list = locationDao.getChildLocationId(locationId);
			list.add(locationId);
			endDevices = dao.getEndDevicesByLocationIds(list, start, limit);
		}
		else {
			endDevices = dao.getEndDevicesByLocationIds(null, start, limit);
		}
		
		for (EndDevice endDevice : endDevices) {
			EndDeviceVO endDeviceVO = new EndDeviceVO(endDevice);
			if (!metering) {
				result.add(endDeviceVO);
				continue;
			}
			Set<Condition> conditionList = new HashSet<Condition>();
			conditionList.add(
				new Condition(
					"id.yyyymmdd", 
					new Object[] { 
						DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMdd") 
					},
					null,
					Restriction.EQ));
			
			if(endDevice.getModem() != null) {
				Integer modemId = endDevice.getModem().getId();
				conditionList.add(new Condition("modem.id",	new Object[] { modemId }, null, Restriction.EQ));
			} 
			else {
				Set<Condition> endDeviceCondition = new HashSet<Condition>(0);
				endDeviceCondition.add(
					new Condition("endDevice.id",new Object[] { endDevice.getId() }, null, Restriction.EQ)
				);

				List<Meter> meterList = meterDao.findByConditions(endDeviceCondition);

				if (!meterList.isEmpty()) {
					conditionList.add(
						new Condition("meter.id", new Object[] { meterList.get(0).getId() }, null, Restriction.EQ)
					);
				} 
			}

			conditionList.add(
				new Condition("id.channel", new Object[] { DefaultChannel.Usage.getCode() }, null, Restriction.EQ)
			);
			List<DayEM> dayEMList = dayEMDao.getDayEMsByListCondition(conditionList);
			List<DayGM> dayGMList = dayGMDao.getDayGMsByListCondition(conditionList);
			List<DayWM> dayWMList = dayWMDao.getDayWMsByListCondition(conditionList);
			List<DayHM> dayHMList = dayHMDao.getDayHMsByListCondition(conditionList);

			if (dayEMList.size() > 0) {
				endDeviceVO.setDayEM(new BigDecimal(dayEMList.get(0).getTotal()) + "");
			}
			if (dayGMList.size() > 0) {
				endDeviceVO.setDayGM(dayGMList.get(0).getTotal() + "");
			}
			if (dayWMList.size() > 0){
				endDeviceVO.setDayWM(dayWMList.get(0).getTotal() + "");
			}
			if (dayHMList.size() > 0) {
				endDeviceVO.setDayHM(dayHMList.get(0).getTotal() + "");
			}
			result.add(endDeviceVO);
		}
		return result;
	}

	@Override
	public long getTotalSize(int locationId) {
		long total = 0;
		if (locationId != -1) {
			List<Integer> list = locationDao.getChildLocationId(locationId);
			list.add(locationId);
			total = dao.getTotalSize(list);
		}
		else {
			total = dao.getTotalSize(null);
		}
		return total;
	}
}