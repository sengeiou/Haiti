package com.aimir.service.mvm.impl;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.ChangeMeterTypeName;
import com.aimir.constants.CommonConstants.ChannelCalcMethod;
import com.aimir.constants.CommonConstants.DateType;
import com.aimir.constants.CommonConstants.DefaultChannel;
import com.aimir.constants.CommonConstants.ElectricityChannel;
import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.constants.CommonConstants.PeakType;
import com.aimir.constants.CommonConstants.UsageRateDateType;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.mvm.ChannelConfigDao;
import com.aimir.dao.mvm.DayEMDao;
import com.aimir.dao.mvm.EachMeterChannelConfigDao;
import com.aimir.dao.mvm.MeteringDayDao;
import com.aimir.dao.mvm.MeteringLpDao;
import com.aimir.dao.mvm.MeteringMonthDao;
import com.aimir.dao.mvm.MonthEMDao;
import com.aimir.dao.mvm.SeasonDao;
import com.aimir.dao.system.Co2FormulaDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.dao.system.CustomerDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.dao.system.TOURateDao;
import com.aimir.model.device.Meter;
import com.aimir.model.mvm.Season;
import com.aimir.model.system.Co2Formula;
import com.aimir.model.system.Contract;
import com.aimir.model.system.Customer;
import com.aimir.model.system.DecimalPattern;
import com.aimir.model.system.Supplier;
import com.aimir.model.system.TOURate;
import com.aimir.model.system.TariffType;
import com.aimir.service.mvm.MvmDetailViewManager;
import com.aimir.service.mvm.SeasonManager;
import com.aimir.service.mvm.bean.ChannelInfo;
import com.aimir.service.mvm.bean.CustomerInfo;
import com.aimir.service.mvm.bean.MvmDetailViewData;
import com.aimir.service.mvm.bean.SeasonData;
import com.aimir.util.CalendarUtil;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.DecimalUtil;
import com.aimir.util.SearchCalendarUtil;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeLocaleUtil;
import com.aimir.util.TimeUtil;

@WebService(endpointInterface = "com.aimir.service.mvm.MvmDetailViewManager")
@Service(value = "mvmDetailViewManager")
public class MvmDetailViewManagerImpl implements MvmDetailViewManager {

	protected static Log logger = LogFactory.getLog(MvmDetailViewManagerImpl.class);

	@Autowired
	CustomerDao ctmDao;

	@Autowired
	MeterDao mtrDao;

	@Autowired
	SeasonDao seasonDao;

	@Autowired
	DayEMDao dayEMDao;

	@Autowired
	MonthEMDao monthEMDao;

	@Autowired
	ChannelConfigDao channelConfigDao;

	@Autowired
	EachMeterChannelConfigDao eachMeterChannelConfigDao;

	@Autowired
	MeteringDayDao meteringDayDao;

	@Autowired
	MeteringLpDao meteringLpDao;

	@Autowired
	MeteringMonthDao meteringMonthDao;

	@Autowired
	ContractDao contractDao;

	@Autowired
	TOURateDao tOURateDao;

	@Autowired
	Co2FormulaDao co2FormulaDao;

	@Autowired
	SupplierDao supplierDao;

	@Autowired
	SeasonManager seasonManager;

	/**
	 * 검색 결과 데이터를 Week단위, 요일별로 접근하기 위한 객체
	 * 
	 * @author kskim
	 *
	 */
	public class WeeklyData {
		private List<Week> weeksOfDay = new ArrayList<Week>();
		String channel = "0";

		public WeeklyData() {
		}

		public String getChannel() {
			return this.channel;
		}

		public void setChannel(String channel) {
			this.channel = channel;
		}

		/**
		 * index에 해당하는 week데이터를 가져온다.
		 * 
		 * @param index
		 * @return
		 */
		public Week getWeekOfIndex(int index) {
			if (this.weeksOfDay.size() <= index)
				return null;
			else
				return this.weeksOfDay.get(index);
		}

		/**
		 * 
		 * @return week데이터의 개수
		 */
		public int getCount() {
			return this.weeksOfDay.size();
		}

		/**
		 * 검색된 데이터들을 주 단위로 그룹을 만든다.
		 * 
		 * @param detailDaySearchData
		 * @return
		 */
		public void addWeeksData(List<Object> detailDaySearchData) {

			Iterator<Object> rows = detailDaySearchData.iterator();

			while (rows.hasNext()) {
				Object[] obj = (Object[]) rows.next();
				String date = (String) obj[0];
				Integer channel = (Integer) obj[1];
				Double value = (Double) obj[2];

				int[] weekIndex = getWeekIndex(date);

				Week w = getWeekOfIndexWithInit(weekIndex[0] - 1);
				MvmDetailViewData mdvd = w.getOfIndex(weekIndex[1]);

				mdvd.setDate(date);
				mdvd.setLocaleDate(date);
				mdvd.setChannel(channel);
				mdvd.setValue(getDoubleToStirng(value));
				// mdvd.setDecimalValue(df.format(getNullToDouble(value)));
			}
		}

		/**
		 * 날짜를 입력받아 몇째주인지 몇요일인지 구한다.
		 * 
		 * @param date
		 *            날짜
		 * @return return[0]: weekOfMonth, return[1]:dayOfWeek
		 */
		private int[] getWeekIndex(String date) {
			Calendar cal = Calendar.getInstance();
			int[] rtn = new int[2];

			int yyyy = Integer.parseInt(date.substring(0, 4));
			int mm = Integer.parseInt(date.substring(4, 6));
			int dd = Integer.parseInt(date.substring(6, 8));

			cal.set(yyyy, mm - 1, dd);
			// cal.set(Calendar.YEAR, yyyy);
			// cal.set(Calendar.MONTH, mm-1);
			// cal.set(Calendar.DAY_OF_MONTH, dd);

			rtn[0] = cal.get(Calendar.WEEK_OF_MONTH);
			rtn[1] = cal.get(Calendar.DAY_OF_WEEK);

			return rtn;

		}

		/**
		 * 주별로 1개의 객체를 생성하기 위한 메소드, 해당 주(week) 객체가 없을경우 생성하여 리턴한다.
		 * 
		 * @param i
		 *            가져와야하는 주(week) index
		 * @return
		 */
		private Week getWeekOfIndexWithInit(int i) {
			if (this.weeksOfDay.size() <= i) {
				this.weeksOfDay.add(new Week());
				return getWeekOfIndexWithInit(i);
			}
			return this.weeksOfDay.get(i);
		}

	}

	/**
	 * 주 데이터
	 * 
	 * @author kskim
	 *
	 */
	public class Week {

		public MvmDetailViewData Sun = new MvmDetailViewData();
		public MvmDetailViewData Mon = new MvmDetailViewData();
		public MvmDetailViewData Tue = new MvmDetailViewData();
		public MvmDetailViewData Wed = new MvmDetailViewData();
		public MvmDetailViewData Thu = new MvmDetailViewData();
		public MvmDetailViewData Fri = new MvmDetailViewData();
		public MvmDetailViewData Sat = new MvmDetailViewData();

		public MvmDetailViewData[] mList = new MvmDetailViewData[] { this.Sun, this.Mon, this.Tue, this.Wed, this.Thu,
				this.Fri, this.Sat };

		public Week() {

		}

		public MvmDetailViewData[] getWeekList() {
			return mList;
		}

		public MvmDetailViewData getOfIndex(int index) {
			switch (index) {
			case 1:
				return getSun();

			case 2:
				return getMon();

			case 3:
				return getTue();

			case 4:
				return getWed();

			case 5:
				return getThu();

			case 6:
				return getFri();

			case 7:
				return getSat();

			}
			return null;
		}

		public void setOfIndex(int index, MvmDetailViewData e) {
			switch (index) {
			case 1:
				setSun(e);
				break;
			case 2:
				setMon(e);
				break;
			case 3:
				setTue(e);
				break;
			case 4:
				setWed(e);
				break;
			case 5:
				setThu(e);
				break;
			case 6:
				setFri(e);
				break;
			case 7:
				setSat(e);
				break;
			}
		}

		public MvmDetailViewData getSun() {
			return Sun;
		}

		public void setSun(MvmDetailViewData sun) {
			Sun = sun;
		}

		public MvmDetailViewData getMon() {
			return Mon;
		}

		public void setMon(MvmDetailViewData mon) {
			Mon = mon;
		}

		public MvmDetailViewData getTue() {
			return Tue;
		}

		public void setTue(MvmDetailViewData tue) {
			Tue = tue;
		}

		public MvmDetailViewData getWed() {
			return Wed;
		}

		public void setWed(MvmDetailViewData wed) {
			Wed = wed;
		}

		public MvmDetailViewData getThu() {
			return Thu;
		}

		public void setThu(MvmDetailViewData thu) {
			Thu = thu;
		}

		public MvmDetailViewData getFri() {
			return Fri;
		}

		public void setFri(MvmDetailViewData fri) {
			Fri = fri;
		}

		public MvmDetailViewData getSat() {
			return Sat;
		}

		public void setSat(MvmDetailViewData sat) {
			Sat = sat;
		}

	}

	// private static Log logger =
	// LogFactory.getLog(MvmDetailViewManagerImpl.class);

	/**
	 * × @see com.aimir.service.mvm.MvmDetailViewManager#getCustomerInfo(java.lang
	 * .String)
	 * 
	 * @Method Name : getCustomerInfo
	 * @Date : 2010. 4. 8.
	 * @Method 설명 :
	 * @param mdsId
	 *            (미터아이디)
	 * @return
	 */
	public CustomerInfo getCustomerInfo(String mdsId, String supplierId) {

		CustomerInfo customerInfo = new CustomerInfo();
		Customer customerData = null;

		// 미터정보 (미터유형, 미터번호, 최종검침시각, 최종값데이터 가져오기 //
		// param 고객번호, 집중기번호 //
		Meter meter = mtrDao.findByCondition("mdsId", mdsId);

		Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));
		String lang = supplier.getLang().getCode_2letter();
		String country = supplier.getCountry().getCode_2letter();
		DecimalFormat df = DecimalUtil.getDecimalFormat(supplier.getMd());

		if (meter != null) {
			// 미터유형
			if (meter.getMeterType() != null) {
				customerInfo.setMeterType(StringUtil.nullCheck(meter.getMeterType().getName(), ""));
				// customerInfo.setMeterType(customerInfo.getMeterType().replaceAll("Meter",
				// ""));
				// customerInfo.setMeterType(customerInfo.getMeterType().replaceAll("Energy",
				// "Electric"));
			} else {
				customerInfo.setMeterType("");
			}
			// 미터번호
			customerInfo.setMeterNo(StringUtil.nullCheck(meter.getMdsId(), ""));
			// 최종검침시각
			customerInfo.setLastTime(
					TimeLocaleUtil.getLocaleDate(StringUtil.nullCheck(meter.getLastReadDate(), ""), lang, country));

			// 최종값데이터
			if (meter.getLastMeteringValue() != null) {
				customerInfo.setLastMeteringData(df.format(getNullToDouble(meter.getLastMeteringValue())));
			} else {
				customerInfo.setLastMeteringData("");
			}

			// 집중기번호
			if (meter.getMcu() != null) {
				customerInfo.setMcuNo(meter.getMcu().getSysID());
			} else {
				customerInfo.setMcuNo("");
			}
			// 집중기번호
			/*
			 * meterId.getModem().getMcu()를 사용하면 오류남 if( (meterId.getModem() == null) ||
			 * (meterId.getModem().getMcu() ==null)) { customerInfo.setMcuNo(""); } else {
			 * customerInfo.setMcuNo(meterId.getModem().getMcu().getName());
			 * 
			 * }
			 */

			// 고객정보추출(성명, 고객번호, 주소, 전화번호, 휴대폰번호)가져오기위한 단계
			if (meter.getCustomer() != null) {
				customerData = meter.getCustomer();
				// 고객성명
				customerInfo.setCustomerName(StringUtil.nullCheck(customerData.getName(), ""));
				// 고객번호
				customerInfo.setCustomerNo(StringUtil.nullCheck(customerData.getCustomerNo(), ""));
				// 고객주소
				customerInfo.setAdress("[" + StringUtil.nullCheck(customerData.getAddress(), "")
						+ StringUtil.nullCheck(customerData.getAddress1(), "") + "] "
						+ StringUtil.nullCheck(customerData.getAddress2(), ""));
				// 고객전화번호
				customerInfo.setTelephoneNo(StringUtil.nullCheck(customerData.getTelephoneNo(), ""));
				// 고객핸드폰번호
				customerInfo.setMobileNo(StringUtil.nullCheck(customerData.getMobileNo(), ""));
			} else {
				// 고객성명
				customerInfo.setCustomerName("");
				// 고객번호
				customerInfo.setCustomerNo("");
				// 고객주소
				customerInfo.setAdress("");
				// 고객전화번호
				customerInfo.setTelephoneNo("");
				// 고객핸드폰번호
				customerInfo.setMobileNo("");
			}

			if (!"MOE".equals(supplier.getDescr())) {
				if (meter.getContract() != null) {
					Contract contract = meter.getContract();
					customerInfo.setContractNo(contract.getContractNumber());

					if (contract.getLocation() != null) {
						customerInfo.setLocation(contract.getLocation().getName());
					}

					if (contract.getTariffIndex() != null) {
						customerInfo.setTariffType(contract.getTariffIndex().getName());
					}
				}
			}
		}

		return customerInfo;
	}

	/**
	 * × @see com.aimir.service.mvm.MvmDetailViewManager#getCustomerInfo(java.lang
	 * .String)
	 * 
	 * @Method Name : getChannelInfo
	 * @Date : 2010. 4. 8.
	 * @Method 설명 : 채널값을 화면에 표시한다.
	 * @param mdsId,
	 *            type (미터아이디, 미터구분)
	 * @return
	 */
	public List<ChannelInfo> getChannelInfoAll(String mdsId, String type) {
		return getChannelInfo(mdsId, type, true);
	}

	public List<ChannelInfo> getChannelInfo(String mdsId, String type) {
		return getChannelInfo(mdsId, type, false);
	}

	public List<ChannelInfo> getChannelInfo(String mdsId, String type, boolean showCO2) {

		List<ChannelInfo> result = new ArrayList<ChannelInfo>();
		String meterType = ChangeMeterTypeName.valueOf(type).getCode();
		String tlbType = MeterType.valueOf(meterType).getLpClassName();
		Map<String, Object> hm = new HashMap<String, Object>();
		hm.put("tlbType", tlbType);
		// logger.debug("tlbType: "+tlbType);
		// logger.debug("mdsId: "+mdsId);
		Meter meter = mtrDao.findByCondition("mdsId", mdsId);
		List<Object> ojbList = null;

		hm.put("mdevId", mdsId);
		ojbList = eachMeterChannelConfigDao.getByList(hm);
		if (ojbList != null && ojbList.size() > 0) {
			Iterator<Object> it = ojbList.iterator();
			int channelIndex = 1;
			while (it.hasNext()) {
				Object[] obj = (Object[]) it.next();
				int codeId = (Integer) obj[0];
				String codeNm = (String) obj[1];
				String unit = (String) obj[2];
				if (showCO2) {
					ChannelInfo channelInfo = new ChannelInfo();
					channelInfo.setCodeId(String.valueOf(channelIndex));
					// channelInfo.setCodeName(codeNm+"("+unit+")");
					channelInfo.setCodeName(codeNm + "[" + unit + "]");
					result.add(channelInfo);
					channelIndex++;
				} else {
					if (codeId != 0) {
						ChannelInfo channelInfo = new ChannelInfo();
						channelInfo.setCodeId(String.valueOf(channelIndex));
						// channelInfo.setCodeName(codeNm+"("+unit+")");
						channelInfo.setCodeName(codeNm + "[" + unit + "]");
						result.add(channelInfo);
						channelIndex++;
					}
				}

			}

			if (result != null && !result.isEmpty()) {
				return result;
			}
		}

		if (meter != null) {
			// 미터의 devicemodel_Id가 존재할때, 존재하지 않을때는 그냥 table명으로 조회
			if (meter.getModel() != null && meter.getModel().getId() > 0) {

				if (meter.getModel().getDeviceConfig() != null) {
					int deviceConfigId = meter.getModel().getDeviceConfig().getId();
					hm.put("deviceConfigId", deviceConfigId);
					ojbList = channelConfigDao.getByList(hm);
				}
			}

			if (ojbList != null && ojbList.size() > 0) {
				Iterator<Object> it = ojbList.iterator();
				while (it.hasNext()) {
					Object[] obj = (Object[]) it.next();
					int codeId = (Integer) obj[0];
					String codeNm = (String) obj[1];
					String unit = (String) obj[2];
					if (showCO2) {
						ChannelInfo channelInfo = new ChannelInfo();
						channelInfo.setCodeId(String.valueOf(codeId));
						// channelInfo.setCodeName(codeNm+"("+unit+")");
						channelInfo.setCodeName(codeNm + "[" + unit + "]");
						result.add(channelInfo);
					} else {
						if (codeId != 0) {
							ChannelInfo channelInfo = new ChannelInfo();
							channelInfo.setCodeId(String.valueOf(codeId));
							// channelInfo.setCodeName(codeNm+"("+unit+")");
							channelInfo.setCodeName(codeNm + "[" + unit + "]");
							result.add(channelInfo);
						}
					}

				}
			} else {// 해당 데이터가 없을경우 default 세팅
				if (type.equals("EM")) {// 전기 default
					for (int i = 0; i < ElectricityChannel.values().length; i++) {
						String name = ElectricityChannel.values()[i] + "";
						int codeId = ElectricityChannel.valueOf(name).getChannel();

						if (showCO2) {
							ChannelInfo channelInfo = new ChannelInfo();
							channelInfo.setCodeId(String.valueOf(codeId));
							channelInfo.setCodeName(name);
							result.add(channelInfo);
						} else {
							if (codeId != 0) {
								ChannelInfo channelInfo = new ChannelInfo();
								channelInfo.setCodeId(String.valueOf(codeId));
								channelInfo.setCodeName(name);
								result.add(channelInfo);
							}
						}
					}
				} else {// 전기 이외의 default
					for (int i = 0; i < DefaultChannel.values().length; i++) {
						String name = DefaultChannel.values()[i] + "";
						int codeId = DefaultChannel.valueOf(name).getCode();
						if (showCO2) {
							ChannelInfo channelInfo = new ChannelInfo();
							channelInfo.setCodeId(String.valueOf(codeId));
							channelInfo.setCodeName(name);
							result.add(channelInfo);
						} else {
							if (codeId != 0) {
								ChannelInfo channelInfo = new ChannelInfo();
								channelInfo.setCodeId(String.valueOf(codeId));
								channelInfo.setCodeName(name);
								result.add(channelInfo);
							}
						}

					}
				}

			}
		}
		return result;

	}

	// *******************************************************************
	// 채널값을 멀티로 선택하게 하는 로직으로 변경한 로직 start

	/**
	 * @Method Name : getDetailHourData
	 * @Date : 2010. 7. 1.
	 * @Method 설명 : 상세조회 시간별 데이터 조회, 기존의 1차 완료분에서 체널값을 멀티로 조회해야함으로 변경함
	 * @param values
	 *            : 화면에서 체크된 데이터
	 * @param type
	 *            : 검침데이터 type(EM, GM등)
	 * @return : 검침데이터 리스트
	 * @return
	 */
	@Deprecated
	public HashMap<String, Object> getDetailHourData(String[] values, String type, String supplierId) {
		HashMap<String, Object> result = new HashMap<String, Object>();
		HashMap<String, Object> searchCondition = makeSearchCondition(values, type);

		Integer meterId = (Integer) searchCondition.get("meterId");
		String meterType = ChangeMeterTypeName.valueOf(type).getCode();
		String tlbType = MeterType.valueOf(meterType).getLpClassName();
		List<Object> ojbList = null;
		List<Map<String, Object>> channelMethods = new ArrayList<Map<String, Object>>();
		Map<String, Object> chMap = new HashMap<String, Object>();
		Meter meter;

		searchCondition.put("tlbType", tlbType);

		if (meterId != null) {
			meter = mtrDao.findByCondition("id", meterId);
			searchCondition.put("interval", (meter.getLpInterval() == null) ? 60 : meter.getLpInterval());

			if (meter.getModel() != null && meter.getModel().getId() > 0) {
				if (meter.getModel().getDeviceConfig() != null) {
					searchCondition.put("deviceConfigId", meter.getModel().getDeviceConfig().getId());
					Map<String, Object> tmpMap = searchCondition;
					ojbList = channelConfigDao.getByList(tmpMap);

					for (Object obj : ojbList) {
						Object[] arr = (Object[]) obj;
						chMap = new HashMap<String, Object>();

						int codeId = (Integer) arr[0];
						if (codeId != 0) {
							chMap.put("channelId", codeId);
							chMap.put("chMethod", (String) arr[3]);
							channelMethods.add(chMap);
						}
					}

					searchCondition.put("channelMethods", channelMethods);
				}
			}
		}

		// 검침데이터 리스트 추출
		List<Object> ojbSearchData = meteringLpDao.getDetailHourSearchData(searchCondition);
		List<MvmDetailViewData> searchData = makeSearchData(ojbSearchData, supplierId, DateType.HOURLY.getCode());

		// 검침데이터 채널별 합계, 평균, 최대/최소값 추출
		List<Object> ojbSearchAddData = meteringLpDao.getDetailLpMaxMinAvgSumData(searchCondition);
		List<MvmDetailViewData> searchAddData = makeSearchAddData(ojbSearchAddData, supplierId);

		result.put("searchData", searchData);
		result.put("searchAddData", searchAddData);

		return result;
	}

	/**
	 * @Method Name : getDetailHourlyAllData
	 * @Date : 2011. 6. 8.
	 * @Method 설명 : 상세조회 시간별 Interval 데이터 조회, 기존의 1차 완료분에서 체널값을 멀티로 조회해야함으로 변경함
	 * @param values
	 *            : 화면에서 체크된 데이터
	 * @param type
	 *            : 검침데이터 type(EM, GM등)
	 * @param supplierId
	 *            : 공급사 ID
	 * @return : 검침데이터 리스트
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getDetailHourlyAllData(String[] values, String type, String supplierId) {
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> condition = makeSearchCondition(values, type);
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> childList = new ArrayList<Map<String, Object>>();
		Map<String, Object> childMap = new HashMap<String, Object>();
		Integer meterId = (Integer) condition.get("meterId");
		List<Integer> arrChannel = ((List<Integer>) condition.get("arrChannel"));
		String meterType = ChangeMeterTypeName.valueOf(type).getCode();
		String tlbType = MeterType.valueOf(meterType).getLpClassName();
		List<Object> ojbList = null;
		List<Map<String, Object>> channelMethods = new ArrayList<Map<String, Object>>();
		Map<String, Object> chMap = new HashMap<String, Object>();
		Meter meter;

		condition.put("tlbType", tlbType);

		if (meterId != null) {
			meter = mtrDao.findByCondition("id", meterId);
			condition.put("interval", (meter.getLpInterval() == null) ? 60 : meter.getLpInterval());

			condition.put("mdevId", meter.getMdsId());
			ojbList = eachMeterChannelConfigDao.getByList(condition);

			if (ojbList != null && !ojbList.isEmpty()) {
				int channelIndex = 1;
				for (Object obj : ojbList) {
					Object[] arr = (Object[]) obj;
					chMap = new HashMap<String, Object>();

					int codeId = (Integer) arr[0];
					if (codeId != 0) {
						chMap.put("channelId", channelIndex);
						chMap.put("chMethod", (ChannelCalcMethod) arr[3]);
						channelMethods.add(chMap);
						channelIndex++;
					}
				}

				condition.put("channelMethods", channelMethods);
			}

			if ((channelMethods == null || channelMethods.isEmpty()) && meter.getModel() != null
					&& meter.getModel().getId() > 0) {
				if (meter.getModel().getDeviceConfig() != null) {
					condition.put("deviceConfigId", meter.getModel().getDeviceConfig().getId());

					ojbList = channelConfigDao.getByList(condition);

					for (Object obj : ojbList) {
						Object[] arr = (Object[]) obj;
						chMap = new HashMap<String, Object>();

						int codeId = (Integer) arr[0];
						if (codeId != 0) {
							chMap.put("channelId", codeId);
							chMap.put("chMethod", (ChannelCalcMethod) arr[3]);
							channelMethods.add(chMap);
						}
					}

					condition.put("channelMethods", channelMethods);
				}
			}
		}

		// 검침데이터 리스트 추출
		List<Map<String, Object>> searchData = meteringLpDao.getDetailHourlyLPData(condition, false);

		// 검침데이터 채널별 합계, 평균, 최대/최소값 추출
		List<Map<String, Object>> searchAddData = meteringLpDao.getDetailHourlyLPData(condition, true);

		Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));
		DecimalFormat df = DecimalUtil.getDecimalFormat(supplier.getMd());

		// formatting 할 항목 key 패턴 정의
		String regexp = ".*CHANNEL_.*";
		Pattern ptrn = Pattern.compile(regexp);

		Matcher mtch = null;
		String key = null;
		Iterator<String> itr = null;
		// Double value = null;

		// 일자,숫자 formatting
		for (Map<String, Object> data : searchData) {
			map = new HashMap<String, Object>();
			map.put("date", data.get("YYYYMMDDHH"));
			map.put("formattedDate", TimeLocaleUtil.getLocaleDateHour((String) data.get("YYYYMMDDHH"),
					supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));

			itr = data.keySet().iterator();

			while (itr.hasNext()) {
				key = itr.next();
				mtch = ptrn.matcher(key);

				if (mtch.find()) {
					// value = Double.valueOf(String.format("%.2f",
					// StringUtil.nullToDoubleZero((Double)data.get(key))));
					map.put(key.toLowerCase(), df.format(getNullToDouble((Double) data.get(key))));
				}
			}

			if (((String) condition.get("isAll")).equals("yes")) {
				map.put("children",
						getDetailHourlyLPIntervalData(condition, supplierId, (String) data.get("YYYYMMDDHH")));
			} else {
				childList = new ArrayList<Map<String, Object>>();
				childMap = new HashMap<String, Object>();
				childList.add(childMap);

				map.put("children", childList);
			}

			list.add(map);
		}

		/*
		 * if (searchData.size() > 0) {
		 * 
		 * // 일자,숫자 formatting for (Map<String, Object> data: searchAddData) { itr =
		 * data.keySet().iterator();
		 * 
		 * while (itr.hasNext()) { key = itr.next(); mtch = ptrn.matcher(key);
		 * 
		 * if (mtch.find()) { // value = Double.parseDouble(String.format("%.3f",
		 * StringUtil.nullToDoubleZero((Double)data.get(key)))); // data.put(key,
		 * df.format((StringUtil.nullToDoubleZero((Double)data.get(key))).doubleValue())
		 * ); // data.put(key, df.format(value.doubleValue())); data.put(key,
		 * df.format(getNullToDouble((Double)data.get(key)))); } }
		 * 
		 * // sum data map = new HashMap<String, Object>(); map.put("date", "");
		 * map.put("formattedDate", condition.get("sum"));
		 * 
		 * if (arrChannel != null && arrChannel.size() > 0) { for (Integer channel :
		 * arrChannel) { map.put("channel_"+channel, data.get("SUM_CHANNEL_"+channel));
		 * } }
		 * 
		 * list.add(map);
		 * 
		 * // avg, max, min data map = new HashMap<String, Object>(); map.put("date",
		 * ""); map.put("formattedDate", new
		 * StringBuilder().append(condition.get("avg")).append('(').append(condition.get
		 * ("max")).append('/').append(condition.get("min")).append(')').toString());
		 * 
		 * if (arrChannel != null && arrChannel.size() > 0) { for (Integer channel :
		 * arrChannel) { map.put("channel_"+channel, new
		 * StringBuilder().append(data.get("AVG_CHANNEL_"+channel)).append('(').append(
		 * data.get("MAX_CHANNEL_"+channel)).append('/').append(data.get("MIN_CHANNEL_"+
		 * channel)).append(')').toString()); } }
		 * 
		 * list.add(map);
		 * 
		 * } }
		 */

		// result.put("searchData", searchData);
		// result.put("searchAddData", searchAddData);
		result.put("gridData", list);

		return result;
	}

	/**
	 * @Method Name : getDetailHourlyLPIntervalData
	 * @Date : 2011. 6. 8.
	 * @Method 설명 : 상세조회 시간별 Interval 데이터 조회, 기존의 1차 완료분에서 체널값을 멀티로 조회해야함으로 변경함
	 * @param condition
	 *            : 조회조건 Map
	 * @param supplierId
	 *            : 공급사 ID
	 * @param searchDate
	 *            : 조회할 일자
	 * @return : 검침데이터 리스트
	 */
	public List<Map<String, Object>> getDetailHourlyLPIntervalData(Map<String, Object> condition, String supplierId,
			String searchDate) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();

		// 검침데이터 리스트 추출
		List<Map<String, Object>> searchData = meteringLpDao.getDetailHourlyLPIntervalData(condition, searchDate);

		Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));
		DecimalFormat df = DecimalUtil.getDecimalFormat(supplier.getMd());
		String lang = supplier.getLang().getCode_2letter();
		String country = supplier.getCountry().getCode_2letter();

		// formatting 할 항목 key 패턴 정의
		String regexp = ".*CHANNEL_.*";
		Pattern ptrn = Pattern.compile(regexp);

		Matcher mtch = null;
		String key = null;
		Iterator<String> itr = null;
		// Double value = null;

		// 일자,숫자 formatting
		for (Map<String, Object> data : searchData) {
			map = new HashMap<String, Object>();
			map.put("date", data.get("YYYYMMDDHHMM"));
			map.put("formattedDate", TimeLocaleUtil.getLocaleDate((String) data.get("YYYYMMDDHHMM"), lang, country));

			itr = data.keySet().iterator();

			while (itr.hasNext()) {
				key = itr.next();
				mtch = ptrn.matcher(key);

				if (mtch.find()) {
					// value = Double.parseDouble(String.format("%.2f",
					// StringUtil.nullToDoubleZero((Double)data.get(key))));
					// map.put(key.toLowerCase(), df.format(value.doubleValue()));
					map.put(key.toLowerCase(), df.format(getNullToDouble((Double) data.get(key))));
				}
			}

			list.add(map);
		}

		return list;
	}

	/**
	 * @Method Name : getDetailHourlyAllChildrenData
	 * @Date : 2011. 6. 13.
	 * @Method 설명 : 상세조회 시간별 Interval 데이터 조회. 화면상에서 트리의 + 클릭 시 호출.
	 * @param values
	 *            : 화면에서 체크된 데이터
	 * @param type
	 *            : 검침데이터 type(EM, GM등)
	 * @param supplierId
	 *            : 공급사 ID
	 * @param searchDate
	 *            : 조회할 일자
	 * @return : 검침데이터 리스트
	 * @return
	 */
	public Map<String, Object> getDetailHourlyAllChildrenData(String[] values, String type, String supplierId,
			String searchDate) {
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> condition = makeSearchCondition(values, type);
		Integer meterId = (Integer) condition.get("meterId");
		Meter meter;

		if (meterId != null) {
			meter = mtrDao.findByCondition("id", meterId);
			condition.put("interval", (meter.getLpInterval() == null) ? 60 : meter.getLpInterval());
		}

		List<Map<String, Object>> list = getDetailHourlyLPIntervalData(condition, supplierId, searchDate);
		result.put("childrenData", list);

		return result;
	}

	//////////////////////////////////////////////////

	/*
	 * (non-Javadoc) MeteringData 맥스가젯의 Detail 에서 시간별 Chart 데이터를 조회한다.
	 * 
	 * @see
	 * com.aimir.service.mvm.MvmDetailViewManager#getDetailHourData4fc(java.lang.
	 * String[], java.lang.String, java.lang.String)
	 */
	public HashMap<String, Object> getDetailHourData4fc(String[] values, String type, String supplierId) {

		HashMap<String, Object> result = new HashMap<String, Object>();
		HashMap<String, Object> searchCondition = makeSearchCondition(values, type);

		String meterType = ChangeMeterTypeName.valueOf(type).getCode();
		String tlbType = MeterType.valueOf(meterType).getLpClassName();
		searchCondition.put("tlbType", tlbType);

		// 검침데이터 리스트 추출
		List<Map<String, Object>> ojbSearchData = meteringLpDao.getDetailHourData4fc(searchCondition, false);

		// 검침데이터 채널별 합계, 평균, 최대/최소값 추출
		List<Map<String, Object>> ojbSearchAddData = meteringLpDao.getDetailHourData4fc(searchCondition, true);

		List<Map<String, Object>> searchData = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> searchAddData = new ArrayList<Map<String, Object>>();
		Map<String, Object> tmpMap = null;

		Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));
		DecimalFormat df = DecimalUtil.getDecimalFormat(supplier.getMd());
		String lang = supplier.getLang().getCode_2letter();
		String country = supplier.getCountry().getCode_2letter();

		if (ojbSearchData != null) {
			for (Map<String, Object> obj : ojbSearchData) {
				tmpMap = new HashMap<String, Object>();

				// tmpMap.put("date",
				// TimeLocaleUtil.getLocaleDateHour((String)obj.get("YYYYMMDDHH"), lang,
				// country));
				// tmpMap.put("localeDate",
				// TimeLocaleUtil.getLocaleDateHour((String)obj.get("YYYYMMDDHH"), lang,
				// country));
				tmpMap.put("date",
						TimeLocaleUtil.getLocaleHour(((String) obj.get("YYYYMMDDHH")).substring(8, 10), lang, country));
				tmpMap.put("localeDate",
						TimeLocaleUtil.getLocaleHour(((String) obj.get("YYYYMMDDHH")).substring(8, 10), lang, country));
				tmpMap.put("channel", obj.get("CHANNEL"));
				tmpMap.put("value",
						getMDFormatDouble(DecimalUtil.ConvertNumberToDouble(obj.get("VALUE")), supplier.getMd()));
				tmpMap.put("decimalValue",
						df.format(getNullToDouble(DecimalUtil.ConvertNumberToDouble(obj.get("VALUE")))));
				tmpMap.put("reportDate", TimeLocaleUtil.getLocaleDate(((String) obj.get("YYYYMMDDHH")), lang, country));
				searchData.add(tmpMap);
			}
		}

		/*
		 * if (ojbSearchAddData != null) { for (Map<String, Object> obj :
		 * ojbSearchAddData) { tmpMap = new HashMap<String, Object>();
		 * 
		 * tmpMap.put("channel", obj.get("CHANNEL")); tmpMap.put("minValue",
		 * StringUtil.nullToDoubleZero(DecimalUtil.ConvertNumberToDouble(obj.get(
		 * "MIN_VAL")))); tmpMap.put("maxValue",
		 * StringUtil.nullToDoubleZero(DecimalUtil.ConvertNumberToDouble(obj.get(
		 * "MAX_VAL")))); tmpMap.put("avgValue",
		 * StringUtil.nullToDoubleZero(DecimalUtil.ConvertNumberToDouble(obj.get(
		 * "AVG_VAL")))); tmpMap.put("sumValue",
		 * StringUtil.nullToDoubleZero(DecimalUtil.ConvertNumberToDouble(obj.get(
		 * "SUM_VAL")))); tmpMap.put("minDecimalValue",
		 * df.format(getNullToDouble(DecimalUtil.ConvertNumberToDouble(obj.get("MIN_VAL"
		 * ))))); tmpMap.put("maxDecimalValue",
		 * df.format(getNullToDouble(DecimalUtil.ConvertNumberToDouble(obj.get("MAX_VAL"
		 * ))))); tmpMap.put("avgDecimalValue",
		 * df.format(getNullToDouble(DecimalUtil.ConvertNumberToDouble(obj.get("AVG_VAL"
		 * ))))); tmpMap.put("sumDecimalValue",
		 * df.format(getNullToDouble(DecimalUtil.ConvertNumberToDouble(obj.get("SUM_VAL"
		 * )))));
		 * 
		 * searchAddData.add(tmpMap); } }
		 */

		result.put("searchData", searchData);
		result.put("searchAddData", searchAddData);

		return result;
	}

	/*
	 * 일별데이터 조회(신)
	 */
	/**
	 * @Method Name : getDetailDayData(일별데이터 조회(신))
	 * @Date : 2010. 7. 1.
	 * @Method 설명 : 상세조회 일별 데이터 조회, 기존의 1차 완료분에서 체널값을 멀티로 조회해야함으로 변경함
	 * @param values
	 *            : 화면에서 체크된 데이터
	 * @param type
	 *            : 검침데이터 type(EM, GM등)
	 * @return : 검침데이터 리스트, 검침데이터 최대, 최소, 평균, 합계
	 */
	public HashMap<String, Object> getDetailDayData(String[] values, String type, String supplierId) {
		HashMap<String, Object> result = new HashMap<String, Object>();
		HashMap<String, Object> searchCondition = makeSearchCondition(values, type);
		String beginDate = StringUtil.nullToBlank(searchCondition.get("beginDate"));
		String endDate = StringUtil.nullToBlank(searchCondition.get("endDate"));

		// 검침데이터 리스트 추출
		// List<Object> ojbSearchData =
		// meteringDayDao.getDetailDaySearchData(searchCondition);
		// List<Object> ojbSearchData =
		// getDailySearchData(meteringDayDao.getDetailDailySearchData(searchCondition,
		// false), beginDate, endDate);
		List<Object> ojbSearchData = makeDetailDailySearchData(
				meteringMonthDao.getDetailDailySearchData(searchCondition, false), beginDate, endDate);

		List<MvmDetailViewData> searchData = makeSearchData(ojbSearchData, supplierId, DateType.DAILY.getCode());

		// 검침데이터 채널별 합계, 평균, 최대/최소값 추출
		// List<Object> ojbSearchAddData =
		// meteringDayDao.getDetailDayMaxMinAvgSumData(searchCondition);
		List<Object> ojbSearchAddData = makeDetailDailySearchDataSumAvg(
				meteringMonthDao.getDetailDailySearchData(searchCondition, true), beginDate, endDate);

		List<MvmDetailViewData> searchAddData = makeSearchAddData(ojbSearchAddData, supplierId);

		result.put("searchData", searchData);
		result.put("searchAddData", searchAddData);
		return result;
	}

	/*
	 * (non-Javadoc) MeteringData 맥스가젯의 Detail 에서 일별 Chart 데이터를 조회한다.
	 * 
	 * @see
	 * com.aimir.service.mvm.MvmDetailViewManager#getDetailDayData4fc(java.lang.
	 * String[], java.lang.String, java.lang.String)
	 */
	@SuppressWarnings("deprecation")
	public HashMap<String, Object> getDetailDayData4fc(String[] values, String type, String supplierId) {
		HashMap<String, Object> result = new HashMap<String, Object>();
		HashMap<String, Object> searchCondition = makeSearchCondition(values, type);

		// 검침데이터 리스트 추출
		List<Object> ojbSearchData = meteringDayDao.getDetailDaySearchData(searchCondition);
		List<MvmDetailViewData> searchData = makeSearchData(ojbSearchData, supplierId, DateType.DAILY.getCode());

		// Supplier supplier =
		// supplierDao.get(Integer.parseInt(String.valueOf(values[6])));
		// for (MvmDetailViewData obj : searchData) {
		// obj.setLocaleDate(TimeLocaleUtil.getLocaleDate(obj.getDate(),
		// supplier.getLang().getCode_2letter(), supplier
		// .getCountry().getCode_2letter()));
		// }

		// 검침데이터 채널별 합계, 평균, 최대/최소값 추출
		List<Object> ojbSearchAddData = meteringDayDao.getDetailDayMaxMinAvgSumData(searchCondition);
		List<MvmDetailViewData> searchAddData = makeSearchAddData(ojbSearchAddData, supplierId);

		result.put("searchData", searchData);
		result.put("searchAddData", searchAddData);
		return result;
	}

	@SuppressWarnings("unchecked")
	public HashMap<String, Object> getCalendarDetailMonthData(String[] values, String type) {
		HashMap<String, Object> result = new HashMap<String, Object>();

		HashMap<String, Object> hm = new HashMap<String, Object>();
		String beginDate = values[0];
		String endDate = values[1];
		String mdsId = values[4];
		Meter meter = mtrDao.findByCondition("mdsId", mdsId);
		int meterId = meter.getId();

		String meterType = ChangeMeterTypeName.valueOf(type).getCode();
		String[] tmpChannel = values[5].split(",");
		List<Integer> arrChannel = new ArrayList<Integer>();
		for (int idx = 0; idx < tmpChannel.length; idx++) {
			arrChannel.add(Integer.parseInt(tmpChannel[idx]));
		}
		hm.put("beginDate", beginDate);
		hm.put("endDate", endDate);
		hm.put("beginMonthDate", beginDate);
		hm.put("endMonthDate", endDate);
		hm.put("meterId", meterId);
		hm.put("arrChannel", arrChannel);
		hm.put("meterType", meterType);
		// logger.info("\n====conditions====\n"+hm);

		// 검침데이터 리스트 추출
		List<Object> ojbSearchData = meteringDayDao.getCalendarDetailDaySearchData(hm);
		List<Object> resultMap = new ArrayList<Object>();

		String contractId = values[6];
		Integer tariffTypeId = -1;
		Integer seasonId = -1;
		Integer seasonId2 = -1;

		String seasonEndDay = "";
		// String seasonStartDay = "";

		HashMap<String, String> peakTimeMap = null;
		HashMap<String, String> peakTimeMap2 = null;

		boolean twoSeason = false;

		// peak time 가져오기.
		if (contractId != null) {
			// Contract c = contractDao.get(Integer.parseInt(contractId));
			Set<Condition> set = new HashSet<Condition>();
			set.add(new Condition("contractNumber", new Object[] { contractId }, null, Restriction.EQ));
			List<Contract> clist = contractDao.findByConditions(set);

			Contract c = null;

			if (clist.size() > 0) {
				c = contractDao.findByConditions(set).get(0);
			}

			if (c != null) {
				TariffType t = c.getTariffIndex();
				tariffTypeId = t.getId();
			}
		}

		if (tariffTypeId != -1) {
			List<Season> sList = seasonDao.getSeasonIdByYMD(endDate.substring(0, 6));

			if (sList.size() > 0) {
				if (sList.size() == 1) {

					seasonId = sList.get(0).getId();
					// logger.debug("****************************************");
					// logger.debug("****************************************");
					// logger.debug("***************임시로 tariffTypeId=1 로 세팅
					// ************************");
					// logger.debug("****************************************");
					// logger.debug("****************************************");
					// tariffTypeId = 1;

					peakTimeMap = getPeakTime(tariffTypeId, seasonId);

					if ("".equals(peakTimeMap.get("cp_e")) && "".equals(peakTimeMap.get("cp_e"))
							&& "".equals(peakTimeMap.get("p_e")) && "".equals(peakTimeMap.get("p_e"))
							&& "".equals(peakTimeMap.get("op_e")) && "".equals(peakTimeMap.get("op_e"))) {
						peakTimeMap = null;
					}

				} else { // 예) 9월 15일가지는 여름, 9월 16일 부터는 가을 인 경우에 해당되는 ...

					// logger.debug("****************************************");
					// logger.debug("****************************************");
					// logger.debug("***************임시로 tariffTypeId=1 로 세팅
					// ************************");
					// logger.debug("****************************************");
					// logger.debug("****************************************");
					// tariffTypeId = 1;

					seasonId = sList.get(0).getId(); // 예) 여름
					peakTimeMap = getPeakTime(tariffTypeId, seasonId);

					// logger.debug("peakTimeMap : " + peakTimeMap);

					if ("".equals(peakTimeMap.get("cp_e")) && "".equals(peakTimeMap.get("cp_e"))
							&& "".equals(peakTimeMap.get("p_e")) && "".equals(peakTimeMap.get("p_e"))
							&& "".equals(peakTimeMap.get("op_e")) && "".equals(peakTimeMap.get("op_e"))) {
						peakTimeMap = null;
					}

					seasonId2 = sList.get(1).getId(); // 예) 가을
					peakTimeMap2 = getPeakTime(tariffTypeId, seasonId2);

					// logger.debug("peakTimeMap2 : " + peakTimeMap2);

					if ("".equals(peakTimeMap2.get("cp_e")) && "".equals(peakTimeMap2.get("cp_e"))
							&& "".equals(peakTimeMap2.get("p_e")) && "".equals(peakTimeMap2.get("p_e"))
							&& "".equals(peakTimeMap2.get("op_e")) && "".equals(peakTimeMap2.get("op_e"))) {
						peakTimeMap2 = null;
					}

					seasonEndDay = sList.get(0).getEday(); // 예) 여름의 마지막 날짜
					// seasonStartDay = sList.get(1).getSday();//예) 가을의 시작 날짜

					twoSeason = true;
				}
			}
		}

		HashMap<String, Object> calData = null;

		// logger.debug("ojbSearchData.size() : " + ojbSearchData.size());
		HashMap<String, Object> data = null;

		for (int i = 0; i < ojbSearchData.size(); i++) {
			data = (HashMap<String, Object>) ojbSearchData.get(i);
			// logger.debug(i + " : " + data.get("yyyymmdd"));

			calData = new HashMap<String, Object>();

			calData.put("yyyymmdd", data.get("yyyymmdd"));

			for (int j = 0; j < 24; j++) {

				if (twoSeason) {
					// 예) 여름에 해당하는 날짜인 경우. - 여름의 peak time 적용.
					if (peakTimeMap != null
							&& Integer.parseInt(data.get("yyyymmdd").toString().substring(6, 8)) <= Integer
									.parseInt(seasonEndDay)) {
						if (!"".equals(StringUtil.nullToBlank(peakTimeMap.get("cp_s")))
								&& !"".equals(StringUtil.nullToBlank(peakTimeMap.get("cp_e")))) {
							checkPeakTime(resultMap, peakTimeMap, j, calData, data, "cp");
						}

						if (!"".equals(StringUtil.nullToBlank(peakTimeMap.get("p_s")))
								&& !"".equals(StringUtil.nullToBlank(peakTimeMap.get("p_e")))) {
							checkPeakTime(resultMap, peakTimeMap, j, calData, data, "p");
						}

						if (!"".equals(StringUtil.nullToBlank(peakTimeMap.get("op_s")))
								&& !"".equals(StringUtil.nullToBlank(peakTimeMap.get("op_e")))) {
							checkPeakTime(resultMap, peakTimeMap, j, calData, data, "op");
						}

						if (calData.get("total_value") != null) {
							if (data.get("value_" + String.format("%02d", j)) != null) {
								calData.put("total_value", Double.parseDouble(calData.get("total_value").toString())
										+ Double.parseDouble(data.get("value_" + String.format("%02d", j)).toString()));
							} else {
								calData.put("total_value", Double.parseDouble(calData.get("total_value").toString()));
							}
						} else {
							if (data.get("value_" + String.format("%02d", j)) != null) {
								calData.put("total_value",
										Double.parseDouble(data.get("value_" + String.format("%02d", j)).toString()));
							} else {
								calData.put("total_value", "0.0");
							}
						}

						// 예) 가을에 해당하는 날짜인 경우 - 가을의 peak time 적용.
					} else if (peakTimeMap2 != null) {
						if (!"".equals(StringUtil.nullToBlank(peakTimeMap2.get("cp_s")))
								&& !"".equals(StringUtil.nullToBlank(peakTimeMap2.get("cp_e")))) {
							checkPeakTime(resultMap, peakTimeMap2, j, calData, data, "cp");
						}

						if (!"".equals(StringUtil.nullToBlank(peakTimeMap2.get("p_s")))
								&& !"".equals(StringUtil.nullToBlank(peakTimeMap2.get("p_e")))) {
							checkPeakTime(resultMap, peakTimeMap2, j, calData, data, "p");
						}

						if (!"".equals(StringUtil.nullToBlank(peakTimeMap2.get("op_s")))
								&& !"".equals(StringUtil.nullToBlank(peakTimeMap2.get("op_e")))) {
							checkPeakTime(resultMap, peakTimeMap2, j, calData, data, "op");
						}

						if (calData.get("total_value") != null) {
							if (data.get("value_" + String.format("%02d", j)) != null) {
								calData.put("total_value", Double.parseDouble(calData.get("total_value").toString())
										+ Double.parseDouble(data.get("value_" + String.format("%02d", j)).toString()));
							} else {
								calData.put("total_value", Double.parseDouble(calData.get("total_value").toString()));
							}
						} else {
							if (data.get("value_" + String.format("%02d", j)) != null) {
								calData.put("total_value",
										Double.parseDouble(data.get("value_" + String.format("%02d", j)).toString()));
							} else {
								calData.put("total_value", "0.0");
							}
						}
					} else {
						calData.put("yyyymmdd", data.get("yyyymmdd"));

						if (calData.get("all_value") != null) {
							if (data.get("value_" + String.format("%02d", j)) != null) {
								calData.put("all_value", Double.parseDouble(calData.get("all_value").toString())
										+ Double.parseDouble(data.get("value_" + String.format("%02d", j)).toString()));
							} else {
								calData.put("all_value", Double.parseDouble(calData.get("all_value").toString()));
							}
						} else {
							if (data.get("value_" + String.format("%02d", j)) != null) {
								calData.put("all_value",
										Double.parseDouble(data.get("value_" + String.format("%02d", j)).toString()));
							} else {
								calData.put("all_value", "0.0");
							}
						}

						if (calData.get("total_value") != null) {
							if (data.get("value_" + String.format("%02d", j)) != null) {
								calData.put("total_value", Double.parseDouble(calData.get("total_value").toString())
										+ Double.parseDouble(data.get("value_" + String.format("%02d", j)).toString()));
							} else {
								calData.put("total_value", Double.parseDouble(calData.get("total_value").toString()));
							}
						} else {
							if (data.get("value_" + String.format("%02d", j)) != null) {
								calData.put("total_value",
										Double.parseDouble(data.get("value_" + String.format("%02d", j)).toString()));
							} else {
								calData.put("total_value", "0.0");
							}

						}
					}
				} else if (peakTimeMap != null) {
					if (!"".equals(StringUtil.nullToBlank(peakTimeMap.get("cp_s")))
							&& !"".equals(StringUtil.nullToBlank(peakTimeMap.get("cp_e")))) {
						checkPeakTime(resultMap, peakTimeMap, j, calData, data, "cp");
					}

					if (!"".equals(StringUtil.nullToBlank(peakTimeMap.get("p_s")))
							&& !"".equals(StringUtil.nullToBlank(peakTimeMap.get("p_e")))) {
						checkPeakTime(resultMap, peakTimeMap, j, calData, data, "p");
					}

					if (!"".equals(StringUtil.nullToBlank(peakTimeMap.get("op_s")))
							&& !"".equals(StringUtil.nullToBlank(peakTimeMap.get("op_e")))) {
						checkPeakTime(resultMap, peakTimeMap, j, calData, data, "op");
					}

					if (calData.get("total_value") != null) {
						if (data.get("value_" + String.format("%02d", j)) != null) {
							calData.put("total_value", Double.parseDouble(calData.get("total_value").toString())
									+ Double.parseDouble(data.get("value_" + String.format("%02d", j)).toString()));
						} else {
							calData.put("total_value", Double.parseDouble(calData.get("total_value").toString()));
						}
					} else {
						if (data.get("value_" + String.format("%02d", j)) != null) {
							calData.put("total_value",
									Double.parseDouble(data.get("value_" + String.format("%02d", j)).toString()));
						} else {
							calData.put("total_value", "0.0");
						}
					}
					// peak time 이 존재하지 않는 경우 당일의 모든 값을 합하여 보여준다.
				} else {
					calData.put("yyyymmdd", data.get("yyyymmdd"));

					if (calData.get("all_value") != null) {
						if (data.get("value_" + String.format("%02d", j)) != null) {
							calData.put("all_value", Double.parseDouble(calData.get("all_value").toString())
									+ Double.parseDouble(data.get("value_" + String.format("%02d", j)).toString()));
						} else {
							calData.put("all_value", Double.parseDouble(calData.get("all_value").toString()));
						}
					} else {
						if (data.get("value_" + String.format("%02d", j)) != null) {
							calData.put("all_value",
									Double.parseDouble(data.get("value_" + String.format("%02d", j)).toString()));
						} else {
							calData.put("all_value", "0.0");
						}
					}

					if (calData.get("total_value") != null) {
						if (data.get("value_" + String.format("%02d", j)) != null) {
							calData.put("total_value", Double.parseDouble(calData.get("total_value").toString())
									+ Double.parseDouble(data.get("value_" + String.format("%02d", j)).toString()));
						} else {
							calData.put("total_value", Double.parseDouble(calData.get("total_value").toString()));
						}
					} else {
						if (data.get("value_" + String.format("%02d", j)) != null) {
							calData.put("total_value",
									Double.parseDouble(data.get("value_" + String.format("%02d", j)).toString()));
						} else {
							calData.put("total_value", "0.0");
						}
					}
				}
			}

			resultMap.add(calData);
		}

		// logger.debug("***********************************************************");
		// logger.debug("***********************************************************");
		// logger.debug("tariffTypeId : " + tariffTypeId);
		// logger.debug("seasonId : " + seasonId);
		// logger.debug("seasonId2 : " + seasonId2);
		// logger.debug("seasonEndDay : " + seasonEndDay);
		// logger.debug("seasonStartDay : " + seasonStartDay);
		// logger.debug("peakTimeMap : " + peakTimeMap);
		// logger.debug("peakTimeMap2 : " + peakTimeMap2);
		// logger.debug("twoSeason : " + twoSeason);
		// logger.debug("***********************************************************");
		// logger.debug("***********************************************************");

		String supplierId = values[7];
		Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));
		DecimalFormat df = DecimalUtil.getDecimalFormat(supplier.getMd());

		// 달력 html
		int year = Integer.parseInt(beginDate.substring(0, 4));
		int month = Integer.parseInt(beginDate.substring(4, 6));

		String cp = "";
		String p = "";
		String op = "";
		String all = "";
		String total = "";

		StringBuffer sbHtml = new StringBuffer();

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month - 1); // 0월 ~11월
		cal.set(Calendar.DATE, 1);

		int statOfDay = cal.get(Calendar.DAY_OF_WEEK); // 1일이 어떤 요일
		sbHtml.append("<table>");
		sbHtml.append("<tr><td class='bluebold12pt mvm-calendar-title'>" + year + ". " + month + "</td></td>");
		sbHtml.append("</table>");
		sbHtml.append("<table class='mvm-calendar bg-blue'>");
		sbHtml.append(
				"<tr class='header'><td class='orange11pt'>Sun</td><td>Mon</td><td>Tus</td><td>Wed</td><td>Thu</td><td>Fri</td><td class='blue11pt'>Sat</td></tr>");
		sbHtml.append("<tr>");
		for (int i = 1; i < statOfDay; i++) {
			sbHtml.append("<td>&nbsp;</td>");
		}

		for (int i = 1; i <= cal.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {

			for (int j = 0; j < resultMap.size(); j++) {
				cp = "";
				p = "";
				op = "";
				all = "";
				total = "";

				HashMap<String, Object> calMap = (HashMap<String, Object>) resultMap.get(j);
				// logger.debug(calMap);
				// logger.debug(Integer.parseInt(calMap.get("yyyymmdd").toString().substring(6,
				// 8)) + " ::: " + i);
				if (Integer.parseInt(calMap.get("yyyymmdd").toString().substring(6, 8)) == i) {
					cp = calMap.get("cp_value") == null ? ""
							: df.format(Double.parseDouble(calMap.get("cp_value").toString()));
					p = calMap.get("p_value") == null ? ""
							: df.format(Double.parseDouble(calMap.get("p_value").toString()));
					op = calMap.get("op_value") == null ? ""
							: df.format(Double.parseDouble(calMap.get("op_value").toString()));
					all = calMap.get("all_value") == null ? ""
							: df.format(Double.parseDouble(calMap.get("all_value").toString()));
					total = calMap.get("total_value") == null ? ""
							: df.format(Double.parseDouble(calMap.get("total_value").toString()));
					break;
				}
			}

			// logger.debug("~~~>" + ChangeMeterTypeName.valueOf(type).getCode());
			// logger.debug("~~~>" +
			// MeterType.valueOf(ChangeMeterTypeName.valueOf(type).getCode()).getServiceType());

			Co2Formula co2f = co2FormulaDao.getCo2FormulaBySupplyType(
					MeterType.valueOf(ChangeMeterTypeName.valueOf(type).getCode()).getServiceType());

			sbHtml.append("<td><table><tr><td class='day'>");
			sbHtml.append(i);
			sbHtml.append("<br><font class='red10pt'>").append(cp).append("</font>");
			sbHtml.append("<br><font class='blue10pt'>").append(p).append("</font>");
			sbHtml.append("<br><font class='green10pt'>").append(op).append("</font>");
			sbHtml.append("<br>").append(all);

			if ("".equals(total)) {
				sbHtml.append("<br><font class='orange10pt'>").append("").append("</font>");
			} else {
				// logger.debug(CommonConstants.ChangeMeterTypeName.valueOf(type).toString() + "
				// : " + total + " : " + co2);
				sbHtml.append("<br><font class='orange10pt'>")
						.append(df.format(Double.parseDouble(total) * co2f.getCo2emissions())).append("</font>");
			}

			sbHtml.append("<br>");
			sbHtml.append("</td></tr></table></td>");

			if (i == cal.getActualMaximum(Calendar.DAY_OF_MONTH)) {
				for (int j = 0; j < 7 - ((statOfDay - 1 + i) % 7); j++) {
					sbHtml.append("<td></td>");
				}
			}

			if ((statOfDay - 1 + i) % 7 == 0) {
				sbHtml.append("</tr><tr>");
			}
		}
		sbHtml.append("</tr>");
		sbHtml.append("</table>");

		result.put("searchData", resultMap);
		result.put("html", sbHtml.toString());

		return result;
	}

	@SuppressWarnings("unchecked")
	public HashMap<String, Object> getCalendarDetailMonthChart(String[] values, String type) {
		HashMap<String, Object> result = new HashMap<String, Object>();

		HashMap<String, Object> hm = new HashMap<String, Object>();
		String beginDate = values[0];
		String endDate = values[1];
		String mdsId = values[4];
		Meter meter = mtrDao.findByCondition("mdsId", mdsId);
		int meterId = meter.getId();

		String meterType = ChangeMeterTypeName.valueOf(type).getCode();
		String[] tmpChannel = values[5].split(",");
		List<Integer> arrChannel = new ArrayList<Integer>();
		for (int idx = 0; idx < tmpChannel.length; idx++) {
			arrChannel.add(Integer.parseInt(tmpChannel[idx]));
		}
		hm.put("beginDate", beginDate);
		hm.put("endDate", endDate);
		hm.put("beginMonthDate", beginDate);
		hm.put("endMonthDate", endDate);
		hm.put("meterId", meterId);
		hm.put("arrChannel", arrChannel);
		hm.put("meterType", meterType);
		// logger.info("\n====conditions====\n"+hm);

		// 검침데이터 리스트 추출
		List<Object> ojbSearchData = meteringDayDao.getCalendarDetailDaySearchData(hm);
		List<Object> resultMap = new ArrayList<Object>();
		List<Object> dayData = null;

		// HashMap<String, Object> carbonData = null;
		HashMap<String, Object> calData = null;

		// logger.debug("ojbSearchData.size() : " + ojbSearchData.size());

		HashMap<String, Object> data = null;

		Co2Formula co2f = co2FormulaDao.getCo2FormulaBySupplyType(
				MeterType.valueOf(ChangeMeterTypeName.valueOf(type).getCode()).getServiceType());

		// StringBuffer jsonSB = new StringBuffer();

		for (int i = 0; i < ojbSearchData.size(); i++) {
			data = (HashMap<String, Object>) ojbSearchData.get(i);
			dayData = new ArrayList<Object>();

			for (int j = 0; j < 24; j++) {
				calData = new HashMap<String, Object>();

				// calData.put("\"yyyymmdd\"", "\"" + data.get("yyyymmdd").toString() + "\"");
				calData.put("\"hour\"", "\"" + String.format("%02d", j) + "\"");
				if (data.get("value_" + String.format("%02d", j)) != null) {
					calData.put("\"use\"",
							"\"" + String.format("%.4f",
									Double.parseDouble(data.get("value_" + String.format("%02d", j)).toString()))
									+ "\"");
					calData.put("\"carbon\"",
							"\"" + String.format("%.4f",
									Double.parseDouble(data.get("value_" + String.format("%02d", j)).toString())
											* co2f.getCo2emissions())
									+ "\"");
				} else {
					calData.put("\"use\"", "\"0.0\"");
					calData.put("\"carbon\"", "\"0.0\"");
				}

				dayData.add(calData);
			}

			resultMap.add(dayData);
		}

		// 달력 html
		int year = Integer.parseInt(beginDate.substring(0, 4));
		int month = Integer.parseInt(beginDate.substring(4, 6));

		StringBuffer sbHtml = new StringBuffer();

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month - 1); // 0월 ~11월
		cal.set(Calendar.DATE, 1);

		int statOfDay = cal.get(Calendar.DAY_OF_WEEK); // 1일이 어떤 요일
		sbHtml.append("<table>");
		sbHtml.append("<tr><td class='bluebold12pt mvm-calendar-title'>" + year + "년 " + month + "월 </td></td>");
		sbHtml.append("</table>");
		sbHtml.append("<table class='mvm-calendar bg-blue'>");
		sbHtml.append(
				"<tr class='header'><td class='orange11pt'>Sun</td><td>Mon</td><td>Tus</td><td>Wed</td><td>Thu</td><td>Fri</td><td class='blue11pt'>Sat</td></tr>");
		sbHtml.append("<tr>");
		for (int i = 1; i < statOfDay; i++) {
			sbHtml.append("<td>&nbsp;</td>");
		}

		String strParam = "";

		for (int i = 1; i <= cal.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {

			strParam = "";

			sbHtml.append("<td class='gray10pt' align='center'><div style='height:90px'>");
			sbHtml.append(i);

			if (resultMap != null && resultMap.size() >= i && resultMap.get(i - 1) != null) {

				// logger.debug(" *********************** ");
				// logger.debug(" **********resultMap : " + resultMap.size() + "*************
				// ");
				// logger.debug(resultMap.get(i-1).toString());
				// logger.debug(" *********************** ");

				// json 데이터 예시 :
				// {"chart":[{"carbon":"10.1760","yyyymmdd":"20100701","useage":"0.2400","hour":"00"},{"carbon":"8.1760","yyyymmdd":"20100701","useage":"0.8400","hour":"01"}]}
				strParam = resultMap.get(i - 1).toString().replaceAll("=", ":");
				strParam = strParam.replaceAll(" ", "");

				strParam = "{\"chart\":" + strParam + "}";

				// logger.debug(">>" + strParam);
			}

			if (!"".equals(strParam)) {
				sbHtml.append(" <div class='gadget_body'> ");
				sbHtml.append(
						"     <object classid='clsid:D27CDB6E-AE6D-11cf-96B8-444553540000' width='95px' height='60px' id='calFlexEx'> ");
				sbHtml.append("             <param name='wmode' value='transparent' /> ");
				sbHtml.append(
						"            <param name='movie' value='/aimir-web/flexapp/swf/veeCalendarChart.swf?param="
								+ strParam + "' /> ");
				sbHtml.append("          <!--[if !IE]>--> ");
				sbHtml.append(
						"     <object type='application/x-shockwave-flash' data='/aimir-web/flexapp/swf/veeCalendarChart.swf?param="
								+ strParam + "' width='100px' height='60px' id='calFlexOt'> ");
				sbHtml.append("      <param name='wmode' value='transparent' /> ");
				sbHtml.append("   <!--<![endif]--> ");
				sbHtml.append("  <div> ");
				sbHtml.append("      <h4>Content on this page requires a newer version of Adobe Flash Player.</h4> ");
				sbHtml.append(
						"     <p><a href='http://www.adobe.com/go/getflashplayer'><img src='http://www.adobe.com/images/shared/download_buttons/get_flash_player.gif' alt='Get Adobe Flash player' width='112' height='33' /></a></p> ");
				sbHtml.append("          </div> ");
				sbHtml.append("  <!--[if !IE]>--> ");
				sbHtml.append("  </object> ");
				sbHtml.append("  <!--<![endif]--> ");
				sbHtml.append("  </object> ");
				sbHtml.append(" </div> ");
			} else {
				sbHtml.append("&nbsp;");
			}

			sbHtml.append("</div></td>");

			if (i == cal.getActualMaximum(Calendar.DAY_OF_MONTH)) {
				for (int j = 0; j < 7 - ((statOfDay - 1 + i) % 7); j++) {
					sbHtml.append("<td></td>");
				}
			}

			if ((statOfDay - 1 + i) % 7 == 0) {
				sbHtml.append("</tr><tr>");
			}
		}
		sbHtml.append("</tr>");
		sbHtml.append("</table>");

		// result.put("searchData", resultMap);
		result.put("html", sbHtml.toString());

		return result;
	}

	public void checkPeakTime(List<Object> resultMap, HashMap<String, String> peakTimeMap, int j,
			HashMap<String, Object> calData, HashMap<String, Object> data, String peak) {

		// logger.debug("~~~~~~~> j : " + j);
		// logger.debug("~~~~~~~> peak : " + peak);
		// logger.debug("~~~~~~~> peakTimeMap.get(" + peak + "_s) : " +
		// peakTimeMap.get(peak + "_s"));
		// logger.debug("~~~~~~~> peakTimeMap.get(" + peak + "_e) : " +
		// peakTimeMap.get(peak + "_e"));

		if (Integer.parseInt(peakTimeMap.get(peak + "_s")) < Integer.parseInt(peakTimeMap.get(peak + "_e"))) {
			if (j >= Integer.parseInt(peakTimeMap.get(peak + "_s"))
					&& j < Integer.parseInt(peakTimeMap.get(peak + "_e"))) {

				if (calData.get("yyyymmdd") != null && calData.get(peak + "_value") != null) {
					if (data.get("value_" + String.format("%02d", j)) != null) {
						calData.put(peak + "_value", Double.parseDouble(calData.get(peak + "_value").toString())
								+ Double.parseDouble(data.get("value_" + String.format("%02d", j)).toString()));
					} else {
						calData.put(peak + "_value", Double.parseDouble(calData.get(peak + "_value").toString()));
					}
				} else {
					if (data.get("value_" + String.format("%02d", j)) != null) {
						calData.put(peak + "_value",
								Double.parseDouble(data.get("value_" + String.format("%02d", j)).toString()));
					} else {
						calData.put(peak + "_value", "0.0");
					}
				}

				// resultMap.add(calData);
			}
		} else {
			if ((j >= Integer.parseInt(peakTimeMap.get(peak + "_s")) && j <= 24)
					|| (j >= 0 && j < Integer.parseInt(peakTimeMap.get(peak + "_e")))) {
				if (calData.get("yyyymmdd") != null && calData.get(peak + "_value") != null) {
					if (data.get("value_" + String.format("%02d", j)) != null) {
						calData.put(peak + "_value", Double.parseDouble(calData.get(peak + "_value").toString())
								+ Double.parseDouble(data.get("value_" + String.format("%02d", j)).toString()));
					} else {
						calData.put(peak + "_value", Double.parseDouble(calData.get(peak + "_value").toString()));
					}
				} else {
					if (data.get("value_" + String.format("%02d", j)) != null) {
						calData.put(peak + "_value",
								Double.parseDouble(data.get("value_" + String.format("%02d", j)).toString()));
					} else {
						calData.put(peak + "_value", "0.0");
					}
				}
				// resultMap.add(calData);
			}
		}
	}

	public HashMap<String, String> getPeakTime(Integer tariffTypeId, Integer seasonId) {
		TOURate critical_peak = null;
		TOURate peak = null;
		TOURate off_peak = null;

		String cp_s = "";
		String cp_e = "";

		String p_s = "";
		String p_e = "";

		String op_s = "";
		String op_e = "";

		HashMap<String, String> rtn = new HashMap<String, String>();

		critical_peak = tOURateDao.getTOURate(tariffTypeId, seasonId, PeakType.CRITICAL_PEAK);
		peak = tOURateDao.getTOURate(tariffTypeId, seasonId, PeakType.PEAK);
		off_peak = tOURateDao.getTOURate(tariffTypeId, seasonId, PeakType.OFF_PEAK);

		if (critical_peak != null) {
			cp_s = (critical_peak.getStartTime());
			cp_e = (critical_peak.getEndTime());
		}

		if (peak != null) {
			p_s = (peak.getStartTime());
			p_e = (peak.getEndTime());
		}

		if (off_peak != null) {
			op_s = (off_peak.getStartTime());
			op_e = (off_peak.getEndTime());
		}

		rtn.put("cp_s", cp_s);
		rtn.put("cp_e", cp_e);
		rtn.put("p_s", p_s);
		rtn.put("p_e", p_e);
		rtn.put("op_s", op_s);
		rtn.put("op_e", op_e);

		return rtn;
	}

	/**
	 * @Method Name : getDetailDayWeekData(요일별데이터 조회(신))
	 * @Date : 2010. 7. 1.
	 * @Method 설명 : 기존의 1차 완료분에서 체널값을 멀티로 조회해야함으로 변경함
	 * @param values
	 *            : 화면에서 체크된 데이터
	 * @param type
	 *            : 검침데이터 type(EM, GM등)
	 * @return
	 */
	public HashMap<String, Object> getDetailDayWeekData(String[] values, String type, String supplierId) {

		HashMap<String, Object> result = new HashMap<String, Object>();
		HashMap<String, Object> searchCondition = makeSearchCondition(values, type);
		String beginDate = StringUtil.nullToBlank(searchCondition.get("beginDate"));
		String endDate = StringUtil.nullToBlank(searchCondition.get("endDate"));

		// 검침데이터 리스트 추출
		// List<Object> ojbSearchData
		// =meteringDayDao.getDetailDaySearchData(searchCondition);

		// List<Object> ojbSearchData =
		// makeDetailDailySearchData(meteringMonthDao.getDetailDailySearchData(searchCondition,
		// false), beginDate, endDate);
		List<Map<String, Object>> list = meteringMonthDao.getDetailDailySearchData(searchCondition, false);
		List<Object> ojbSearchData = makeDetailDailySearchData(list, beginDate, endDate);

		List<MvmDetailViewData> searchData = makeSearchDataDayWeek(ojbSearchData, supplierId);

		// 검침데이터 채널별 합계, 평균, 최대/최소값 추출
		// List<Object> ojbSearchAddData =
		// meteringDayDao.getDetailDayMaxMinAvgSumData(searchCondition);
		List<Object> ojbSearchAddData = makeDetailDailySearchDataSumAvg(
				meteringMonthDao.getDetailDailySearchData(searchCondition, true), beginDate, endDate);
		List<MvmDetailViewData> searchAddData = makeSearchAddData(ojbSearchAddData, supplierId);

		result.put("searchData", searchData);
		result.put("searchAddData", searchAddData);
		return result;
	}

	/**
	 * @Method Name : getDetailMonthData
	 * @Date : 2010. 7. 1.
	 * @Method 설명 : 기존의 1차 완료분에서 체널값을 멀티로 조회해야함으로 변경함
	 * @param values
	 *            : 화면에서 체크된 데이터
	 * @param type
	 *            : 검침데이터 type(EM, GM등)
	 * @return
	 */
	public HashMap<String, Object> getDetailMonthData(String[] values, String type, String supplierId) {
		HashMap<String, Object> result = new HashMap<String, Object>();
		HashMap<String, Object> searchCondition = makeSearchCondition(values, type);
		String meterType = ChangeMeterTypeName.valueOf(type).getCode();
		String tlbType = MeterType.valueOf(meterType).getLpClassName();
		searchCondition.put("tlbType", tlbType);
		// 검침데이터 리스트 추출
		// List<Object> ojbSearchData =
		// meteringMonthDao.getDetailMonthSearchData(searchCondition);
		List<Object> ojbSearchData = meteringMonthDao.getDetailMonthlySearchData(searchCondition, false);
		List<MvmDetailViewData> searchData = makeSearchData(ojbSearchData, supplierId, DateType.MONTHLY.getCode());

		// logger.debug("~~~~~~~~> ojbSearchData : " + ojbSearchData.size() + " :: " +
		// ojbSearchData);
		// logger.debug("~~~~~~~~> searchData : " + searchData.size() + " :: " +
		// searchData);

		// 검침데이터 채널별 합계, 평균, 최대/최소값 추출
		// List<Object> ojbSearchAddData =
		// meteringMonthDao.getDetailMonthMaxMinAvgSumData(searchCondition);
		List<Object> ojbSearchAddData = meteringMonthDao.getDetailMonthlySearchData(searchCondition, true);
		List<MvmDetailViewData> searchAddData = makeSearchAddData(ojbSearchAddData, supplierId);

		// logger.debug("~~~~~~~~> ojbSearchAddData : " + ojbSearchAddData.size() + " ::
		// " + ojbSearchAddData);
		// logger.debug("~~~~~~~~> searchAddData : " + searchAddData.size() + " :: " +
		// searchAddData);

		result.put("searchData", searchData);
		result.put("searchAddData", searchAddData);
		return result;
	}

	public HashMap<String, Object> getDetailMonthData4fc(String[] values, String type, String supplierId) {
		HashMap<String, Object> result = new HashMap<String, Object>();
		HashMap<String, Object> searchCondition = makeSearchCondition(values, type);
		String meterType = ChangeMeterTypeName.valueOf(type).getCode();
		String tlbType = MeterType.valueOf(meterType).getLpClassName();
		searchCondition.put("tlbType", tlbType);

		// 검침데이터 리스트 추출
		// List<Object> ojbSearchData =
		// meteringMonthDao.getDetailMonthSearchData(searchCondition);
		List<Object> ojbSearchData = meteringMonthDao.getDetailMonthlySearchData(searchCondition, false);
		List<MvmDetailViewData> searchData = makeSearchData(ojbSearchData, supplierId, DateType.MONTHLY.getCode());

		// Supplier supplier =
		// supplierDao.get(Integer.parseInt(String.valueOf(values[6])));
		// for (MvmDetailViewData obj : searchData) {
		// obj.setLocaleDate(obj.getDate().substring(0, 4) + ". " +
		// obj.getDate().substring(4));
		// }

		// logger.debug("~~~~~~~~> ojbSearchData : " + ojbSearchData.size() + " :: " +
		// ojbSearchData);
		// logger.debug("~~~~~~~~> searchData : " + searchData.size() + " :: " +
		// searchData);

		// 검침데이터 채널별 합계, 평균, 최대/최소값 추출
		// List<Object> ojbSearchAddData =
		// meteringMonthDao.getDetailMonthMaxMinAvgSumData(searchCondition);
		List<Object> ojbSearchAddData = meteringMonthDao.getDetailMonthlySearchData(searchCondition, true);
		List<MvmDetailViewData> searchAddData = makeSearchAddData(ojbSearchAddData, supplierId);

		// logger.debug("~~~~~~~~> ojbSearchAddData : " + ojbSearchAddData.size() + " ::
		// " + ojbSearchAddData);
		// logger.debug("~~~~~~~~> searchAddData : " + searchAddData.size() + " :: " +
		// searchAddData);

		result.put("searchData", searchData);
		result.put("searchAddData", searchAddData);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.aimir.service.mvm.MvmDetailViewManager#getDetailWeeklyUnitData(java.lang.
	 * String[], java.lang.String, java.lang.String)
	 */
	@SuppressWarnings("deprecation")
	public WeeklyData[] getDetailWeeklyUnitData(String[] condition, String type, String supplierId) {

		// HashMap<String, Object> result = new HashMap<String, Object>();
		HashMap<String, Object> searchCondition = makeSearchCondition(condition, type);

		List<WeeklyData> weeklyDatas = new ArrayList<WeeklyData>();

		Map<String, List<Object>> dataferChannel = new HashMap<String, List<Object>>();

		List<Object> weekOfDay = meteringDayDao.getDetailDaySearchData(searchCondition);

		// 채널별로 데이터를 분리한다.
		Iterator<Object> rows = weekOfDay.iterator();
		while (rows.hasNext()) {
			Object[] obj = (Object[]) rows.next();
			// String date = (String) obj[0];
			Integer channel = (Integer) obj[1];
			// Double value = (Double) obj[2];

			if (dataferChannel.containsKey(channel.toString())) {
				List<Object> channeld = dataferChannel.get(channel.toString());
				channeld.add(obj);
			} else {
				List<Object> channeld = new ArrayList<Object>();
				channeld.add(obj);
				dataferChannel.put(channel.toString(), channeld);
			}
		}

		// 분리된 데이터를 다시 weeklyData로 변환한다.
		Set<String> keys = dataferChannel.keySet();
		for (String key : keys) {
			List<Object> obj = dataferChannel.get(key);
			WeeklyData weeklyData = new WeeklyData();
			weeklyData.addWeeksData(obj);
			weeklyData.setChannel(key);
			weeklyDatas.add(weeklyData);
		}

		return weeklyDatas.toArray(new WeeklyData[0]);

	}

	// /**
	// * 주별 데이터를 받아 월,화,수,목,금,토,일 로 데이터를 그룹화 한다.
	// * @param weekOfDay
	// * @return
	// */
	// private Object makeWeekUnitData(List<Object> weekOfDay) {
	// List<Object> week = new ArrayList<Object>();
	//
	// List<Object> mon = new ArrayList<Object>();
	// List<Object> tue = new ArrayList<Object>();
	// List<Object> wed = new ArrayList<Object>();
	// List<Object> thu = new ArrayList<Object>();
	// List<Object> fri = new ArrayList<Object>();
	// List<Object> sat = new ArrayList<Object>();
	// List<Object> sun = new ArrayList<Object>();
	//
	// return null;
	// }

	/**
	 * @Method Name : getDetailDayWeekData(주별데이터 조회(신))
	 * @Date : 2010. 7. 1.
	 * @Method 설명 : 기존의 1차 완료분에서 체널값을 멀티로 조회해야함으로 변경함
	 * @param values
	 *            : 화면에서 체크된 데이터
	 * @param type
	 *            : 검침데이터 type(EM, GM등)
	 * @return
	 */
	public HashMap<String, Object> getDetailWeekData(String[] values, String type, String supplierId) {

		HashMap<String, Object> result = new HashMap<String, Object>();
		SearchCalendarUtil sCaldUtil = new SearchCalendarUtil();
		Map<String, Object> searchCondition = new HashMap<String, Object>();
		HashMap<Integer, List<Object>> Data = new HashMap<Integer, List<Object>>();

		String meterType = ChangeMeterTypeName.valueOf(type).getCode();
		String tlbType = MeterType.valueOf(meterType).getLpClassName();
		List<Object> chList = null;
		List<Map<String, Object>> channelMethods = new ArrayList<Map<String, Object>>();
		Map<String, Object> chMap = new HashMap<String, Object>();
		Meter meter;

		int keyIdx = 0;

		// 주의 시작일자와 종료일자를 구하고 구한 일자를 기준으로 데이터를 추출함
		List<String> dateList = sCaldUtil.getMonthToBeginDateEndDate(values[0].substring(0, 6).trim());
		Iterator<String> it = dateList.iterator();
		while (it.hasNext()) {
			String str = it.next();
			String stdDate = str.substring(0, 8);
			String endDate = str.substring(8, 16);
			values[0] = stdDate;
			values[1] = endDate;
			searchCondition = makeSearchCondition(values, type);
			// 검침데이터 리스트 추출, 달의 주에 해당하는 검침데이터의 합계를 추출해야함으로 sum값을 사용함
			// List<Object> ojbList =
			// meteringDayDao.getDetailDayMaxMinAvgSumData(searchCondition);
			List<Object> ojbList = makeDetailDailySearchDataSumAvg(
					meteringMonthDao.getDetailDailySearchData(searchCondition, true), stdDate, endDate);

			Data.put(keyIdx, ojbList);
			keyIdx++;
		}

		// ChannelCalcMethod 조회
		Integer meterId = (Integer) searchCondition.get("meterId");
		searchCondition.put("tlbType", tlbType);

		if (meterId != null) {
			meter = mtrDao.findByCondition("id", meterId);

			searchCondition.put("mdevId", meter.getMdsId());
			chList = eachMeterChannelConfigDao.getByList(searchCondition);

			if (chList != null && !chList.isEmpty()) {
				int channelIndex = 1;
				for (Object obj : chList) {
					Object[] arr = (Object[]) obj;
					chMap = new HashMap<String, Object>();

					int codeId = (Integer) arr[0];
					if (codeId != 0) {
						chMap.put("channelId", channelIndex);
						chMap.put("chMethod", (ChannelCalcMethod) arr[3]);
						channelMethods.add(chMap);
						channelIndex++;
					}
				}

				searchCondition.put("channelMethods", channelMethods);
			}

			if ((channelMethods == null || channelMethods.isEmpty()) && meter.getModel() != null
					&& meter.getModel().getId() > 0) {
				if (meter.getModel().getDeviceConfig() != null) {
					searchCondition.put("deviceConfigId", meter.getModel().getDeviceConfig().getId());
					chList = channelConfigDao.getByList(searchCondition);

					for (Object obj : chList) {
						Object[] arr = (Object[]) obj;
						chMap = new HashMap<String, Object>();

						int codeId = (Integer) arr[0];
						if (arr[3] != null && codeId != 0) {
							chMap.put("channelId", codeId);
							chMap.put("chMethod", (ChannelCalcMethod) arr[3]);
							channelMethods.add(chMap);
						}
					}
				}
			}
		}

		result = makeWeekSearchData(Data, supplierId, channelMethods);

		return result;
	}

	/**
	 * @Method Name : makeWeekSearchData(주별데이터 를 생성함)
	 * @Date : 2010. 7. 1.
	 * @Method 설명 : 기존의 1차 완료분에서 체널값을 멀티로 조회해야함으로 변경함
	 * @param values
	 *            : 화면에서 체크된 데이터
	 * @param type
	 *            : 검침데이터 type(EM, GM등)
	 * @return
	 */
	private HashMap<String, Object> makeWeekSearchData(HashMap<Integer, List<Object>> hm, String supplierId,
			List<Map<String, Object>> channelMethods) {
		int totWeekNum = hm.size();
		List<MvmDetailViewData> searchData = new ArrayList<MvmDetailViewData>();
		List<MvmDetailViewData> searchAddData = new ArrayList<MvmDetailViewData>();
		HashMap<String, Object> result = new HashMap<String, Object>();
		HashMap<String, Object> compData = new HashMap<String, Object>();
		HashSet<Integer> channelSet = new HashSet<Integer>();

		Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));
		DecimalFormat df = DecimalUtil.getDecimalFormat(supplier.getMd());
		ChannelCalcMethod chMethod = null;
		int weekName = 1;

		if (totWeekNum > 0) {
			// searchData생성
			for (int idx = 0; idx < totWeekNum; idx++) {
				List<Object> ojbList = hm.get(idx);
				Iterator<Object> it = ojbList.iterator();
				while (it.hasNext()) {
					MvmDetailViewData mdvd = new MvmDetailViewData();
					chMethod = null;
					Object[] obj = (Object[]) it.next();
					String date = weekName + " Week";
					Integer channel = (Integer) obj[0];
					// Double value = (Double)obj[4];
					Double value = null;
					for (Map<String, Object> map : channelMethods) {
						if (channel.equals((Integer) map.get("channelId"))) {
							chMethod = (ChannelCalcMethod) map.get("chMethod");
							break;
						}
					}

					if (chMethod == null) {
						chMethod = ChannelCalcMethod.SUM;
					}

					switch (chMethod) {
					case AVG:
						value = (Double) obj[3];
						break;
					case SUM:
						value = (Double) obj[4];
						break;
					}

					mdvd.setDate(date);
					mdvd.setLocaleDate(date);
					mdvd.setChannel(channel);
					mdvd.setValue(getDoubleToStirng(value));
					mdvd.setDecimalValue(df.format(getNullToDouble(value)));
					searchData.add(mdvd);

					compData.put(date + "@" + String.valueOf(channel), value);
					channelSet.add(channel);
					// weekName++;
				}
				weekName++;
			}

			// 합계/최대/최소/평균 값 추출
			Iterator<Integer> its = channelSet.iterator();
			while (its.hasNext()) {
				Integer channel = its.next();
				MvmDetailViewData mdvd = new MvmDetailViewData();
				Double max = 0.0;
				Double sum = 0.0;
				Double avg = 0.0;
				Double min = 100000000.0;
				String keyChannel = String.valueOf(channel);
				int weekNameKey = 1;
				for (int j = 0; j < totWeekNum; j++) {
					String key = weekNameKey + " Week@" + keyChannel;
					Double value = 0.0;
					if ((Double) compData.get(key) != null) {
						value = getNullToDouble((Double) compData.get(key));
						sum = sum + value;
						if (max < value)
							max = value;
						if (min > value)
							min = value;
					}
					weekNameKey++;
				}
				avg = sum / totWeekNum;

				mdvd.setChannel(channel);
				// mdvd.setAvgValue(String.valueOf(avg));
				// mdvd.setMaxValue(String.valueOf(max));
				// mdvd.setMinValue(String.valueOf(min));
				// mdvd.setSumValue(String.valueOf(sum));

				mdvd.setAvgValue(getDoubleToStirng(avg));
				mdvd.setMaxValue(getDoubleToStirng(max));
				mdvd.setMinValue(getDoubleToStirng(min));
				mdvd.setSumValue(getDoubleToStirng(sum));

				mdvd.setAvgDecimalValue(df.format(getNullToDouble(avg)));
				mdvd.setMaxDecimalValue(df.format(getNullToDouble(max)));
				mdvd.setMinDecimalValue(df.format(getNullToDouble(min)));
				mdvd.setSumDecimalValue(df.format(getNullToDouble(sum)));

				searchAddData.add(mdvd);
			}
		}
		result.put("searchData", searchData);
		result.put("searchAddData", searchAddData);
		return result;
	}

	// public HashMap<String, Object> makeWeekSearchData4fc (HashMap<Integer,
	// List<Object>> hm, String supplierId) {
	// int totWeekNum = hm.size();
	// List<MvmDetailViewData> searchData = new ArrayList<MvmDetailViewData>();
	// List<MvmDetailViewData> searchAddData = new ArrayList<MvmDetailViewData>();
	// HashMap<String, Object> result = new HashMap<String, Object>();
	// HashMap<String, Object> compData = new HashMap<String, Object>();
	// HashSet<Integer> channelSet = new HashSet<Integer>();
	// int weekName = 1;
	//
	// if(totWeekNum > 0) {
	// // searchData생성
	// for(int idx=0;idx<totWeekNum;idx++) {
	// List<Object> ojbList = hm.get(idx);
	// Iterator<Object> it = ojbList.iterator();
	// while (it.hasNext()) {
	// MvmDetailViewData mdvd = new MvmDetailViewData();
	// Object[] obj = (Object[]) it.next();
	// String date = weekName+" Week";
	// Integer channel = (Integer)obj[0];
	// Double value = (Double)obj[4];
	// mdvd.setDate(date);
	// mdvd.setChannel(channel);
	// mdvd.setValue(getDoubleToStirng(value));
	// searchData.add(mdvd);
	//
	// compData.put(date+"@"+String.valueOf(channel), value);
	// channelSet.add(channel);
	// weekName++;
	// }
	// }
	//
	// // 합계/최대/최소/평균 값 추출
	// Iterator<Integer> its = channelSet.iterator();
	// while (its.hasNext()) {
	// Integer channel = its.next();
	// MvmDetailViewData mdvd = new MvmDetailViewData();
	// Double max =0.0;
	// Double sum =0.0;
	// Double avg =0.0;
	// Double min =100000000.0;
	// String keyChannel = String.valueOf(channel);
	// int weekNameKey = 1;
	// for(int j=0;j<totWeekNum;j++) {
	// String key = weekNameKey+"Week@"+keyChannel;
	// Double value = 0.0;
	// if((Double)compData.get(key) != null) {
	// value = getNullToDouble((Double)compData.get(key));
	// sum = sum + value;
	// if(max < value) max = value;
	// if(min > value) min = value;
	// }
	// weekNameKey++;
	// }
	// avg = sum/totWeekNum;
	// mdvd.setAvgValue(String.valueOf(avg));
	// mdvd.setMaxValue(String.valueOf(max));
	// mdvd.setMinValue(String.valueOf(min));
	// mdvd.setSumValue(String.valueOf(sum));
	// searchAddData.add(mdvd);
	// }
	// }
	// result.put("searchData", searchData);
	// result.put("searchAddData", searchAddData);
	// return result;
	//
	// }

	/**
	 * @Method Name : getDetailSeasonData(계절별검침데이터를 조회함)
	 * @Date : 2010. 7. 1.
	 * @Method 설명 : 기존의 1차 완료분에서 체널값을 멀티로 조회해야함으로 변경함
	 * @param values
	 *            : 화면에서 체크된 데이터
	 * @param type
	 *            : 검침데이터 type(EM, GM등)
	 * @return
	 */
	public HashMap<String, Object> getDetailSeasonData(String[] values, String type, String supplierId) {

		HashMap<String, Object> result = new HashMap<String, Object>();
		// HashMap<String, Object> searchCondition = new HashMap<String, Object>();
		Map<String, Object> searchCondition = new HashMap<String, Object>();
		HashMap<String, List<Object>> Data = new HashMap<String, List<Object>>();

		int keyIdx = 0;
		List<String> dateList = new ArrayList<String>();

		String meterType = ChangeMeterTypeName.valueOf(type).getCode();
		String tlbType = MeterType.valueOf(meterType).getLpClassName();
		List<Object> chList = null;
		List<Map<String, Object>> channelMethods = new ArrayList<Map<String, Object>>();
		Map<String, Object> chMap = new HashMap<String, Object>();
		Meter meter;

		// 계절의 시작일자와 종료일자를 기준으로 데이터를 추출함
		HashMap<String, Object> seasons = getSeasonDate(values[0].substring(0, 4).trim());
		int seasonSize = CommonConstants.Season.values().length;
		Object[] seasonOjb = CommonConstants.Season.values();

		for (int i = 0; i < seasonSize; i++) {
			String seasonsKey = CommonConstants.Season.valueOf(seasonOjb[i].toString()).toString();
			dateList.add((String) seasons.get(seasonsKey));
		}

		Iterator<String> it = dateList.iterator();
		while (it.hasNext()) {
			String str = it.next();
			String[] tmpArr = str.split("@");
			values[0] = tmpArr[0];// 계절의 start일자
			values[1] = tmpArr[1];// 계절의 end일자
			searchCondition = makeSearchCondition(values, type);
			// 검침데이터 리스트 추출, 달의 주에 해당하는 검침데이터의 합계를 추출해야함으로 sum값을 사용함
			// List<Object> ojbList=
			// meteringDayDao.getDetailDayMaxMinAvgSumData(searchCondition);
			List<Object> ojbList = makeDetailDailySearchDataSumAvg(
					meteringMonthDao.getDetailDailySearchData(searchCondition, true), values[0], values[1]);
			Data.put(CommonConstants.Season.valueOf(seasonOjb[keyIdx].toString()).toString(), ojbList);
			keyIdx++;
		}

		// ChannelCalcMethod 조회
		Integer meterId = (Integer) searchCondition.get("meterId");
		searchCondition.put("tlbType", tlbType);

		if (meterId != null) {
			meter = mtrDao.findByCondition("id", meterId);
			// condition.put("interval", (meter.getLpInterval() == null) ? 60 :
			// meter.getLpInterval());

			searchCondition.put("mdevId", meter.getMdsId());
			chList = eachMeterChannelConfigDao.getByList(searchCondition);

			if (chList != null && !chList.isEmpty()) {
				int channelIndex = 1;
				for (Object obj : chList) {
					Object[] arr = (Object[]) obj;
					chMap = new HashMap<String, Object>();

					int codeId = (Integer) arr[0];
					if (codeId != 0) {
						chMap.put("channelId", channelIndex);
						chMap.put("chMethod", (ChannelCalcMethod) arr[3]);
						channelMethods.add(chMap);
						channelIndex++;
					}
				}

				searchCondition.put("channelMethods", channelMethods);
			}

			if ((channelMethods == null || channelMethods.isEmpty()) && meter.getModel() != null
					&& meter.getModel().getId() > 0) {
				if (meter.getModel().getDeviceConfig() != null) {
					searchCondition.put("deviceConfigId", meter.getModel().getDeviceConfig().getId());

					chList = channelConfigDao.getByList(searchCondition);

					for (Object obj : chList) {
						Object[] arr = (Object[]) obj;
						chMap = new HashMap<String, Object>();

						int codeId = (Integer) arr[0];
						if (arr[3] != null && codeId != 0) {
							chMap.put("channelId", codeId);
							chMap.put("chMethod", (ChannelCalcMethod) arr[3]);
							channelMethods.add(chMap);
						}
					}
				}
			}
		}

		result = makeSeasonSearchData(Data, supplierId, channelMethods);

		return result;
	}

	/**
	 * @Method Name : makeSeasonSearchData(계절별데이터를 생성함)
	 * @Date : 2010. 7. 1.
	 * @Method 설명 : 기존의 1차 완료분에서 체널값을 멀티로 조회해야함으로 변경함
	 * @param values
	 *            : 화면에서 체크된 데이터
	 * @param type
	 *            : 검침데이터 type(EM, GM등)
	 * @return
	 */
	public HashMap<String, Object> makeSeasonSearchData(HashMap<String, List<Object>> hm, String supplierId,
			List<Map<String, Object>> channelMethods) {
		int totWeekNum = hm.size();
		Object[] seasonOjb = CommonConstants.Season.values();
		List<MvmDetailViewData> searchData = new ArrayList<MvmDetailViewData>();
		List<MvmDetailViewData> searchAddData = new ArrayList<MvmDetailViewData>();
		HashMap<String, Object> result = new HashMap<String, Object>();
		HashMap<String, Object> compData = new HashMap<String, Object>();
		HashSet<Integer> channelSet = new HashSet<Integer>();
		ChannelCalcMethod chMethod = null;

		Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));
		DecimalFormat df = DecimalUtil.getDecimalFormat(supplier.getMd());

		if (totWeekNum > 0) {
			// searchData생성
			for (int idx = 0; idx < totWeekNum; idx++) {
				String seasonName = CommonConstants.Season.valueOf(seasonOjb[idx].toString()).toString().trim();

				List<Object> ojbList = hm.get(seasonName);
				Iterator<Object> it = ojbList.iterator();
				while (it.hasNext()) {
					MvmDetailViewData mdvd = new MvmDetailViewData();
					chMethod = null;
					Object[] obj = (Object[]) it.next();
					String date = seasonName;
					Integer channel = (Integer) obj[0];
					Double value = null;
					for (Map<String, Object> map : channelMethods) {
						if (channel.equals((Integer) map.get("channelId"))) {
							chMethod = (ChannelCalcMethod) map.get("chMethod");
							break;
						}
					}

					if (chMethod == null) {
						chMethod = ChannelCalcMethod.SUM;
					}

					switch (chMethod) {
					case AVG:
						value = (Double) obj[3];
						break;
					case SUM:
						value = (Double) obj[4];
						break;
					}
					mdvd.setDate(date);
					mdvd.setLocaleDate(date);
					mdvd.setChannel(channel);
					mdvd.setValue(getDoubleToStirng(value));
					mdvd.setDecimalValue(df.format(getNullToDouble(value)));
					searchData.add(mdvd);
					compData.put(date + "@" + String.valueOf(channel), value);
					channelSet.add(channel);
				}
			}
			// 합계/최대/최소/평균 값 추출
			Iterator<Integer> its = channelSet.iterator();
			while (its.hasNext()) {
				Integer channel = its.next();
				MvmDetailViewData mdvd = new MvmDetailViewData();
				Double max = 0.0;
				Double sum = 0.0;
				Double avg = 0.0;
				Double min = 10000000.0;

				String keyChannel = String.valueOf(channel);
				for (int j = 0; j < totWeekNum; j++) {
					String seasonName = CommonConstants.Season.valueOf(seasonOjb[j].toString()).toString().trim();
					String key = seasonName + "@" + keyChannel;
					Double value = 0.0;
					if ((Double) compData.get(key) != null) {
						value = getNullToDouble((Double) compData.get(key));
						sum = sum + value;
						if (max < value)
							max = value;
						if (min > value)
							min = value;
					}

				}
				avg = sum / totWeekNum;
				mdvd.setAvgValue(String.valueOf(avg));
				mdvd.setMaxValue(String.valueOf(max));
				mdvd.setMinValue(String.valueOf(min));
				mdvd.setSumValue(String.valueOf(sum));

				mdvd.setMinDecimalValue(df.format(getNullToDouble(avg)));// 최소값
				mdvd.setMaxDecimalValue(df.format(getNullToDouble(max)));// 최대값
				mdvd.setAvgDecimalValue(df.format(getNullToDouble(min)));// 평균값
				mdvd.setSumDecimalValue(df.format(getNullToDouble(sum)));// 합계값

				mdvd.setChannel(channel);
				searchAddData.add(mdvd);
			}
		}
		result.put("searchData", searchData);
		result.put("searchAddData", searchAddData);
		return result;

	}

	public HashMap<String, Object> makeSeasonSearchData4fc(HashMap<String, List<Object>> hm) {
		int totWeekNum = hm.size();
		Object[] seasonOjb = CommonConstants.Season.values();
		List<MvmDetailViewData> searchData = new ArrayList<MvmDetailViewData>();
		List<MvmDetailViewData> searchAddData = new ArrayList<MvmDetailViewData>();
		HashMap<String, Object> result = new HashMap<String, Object>();
		HashMap<String, Object> compData = new HashMap<String, Object>();
		HashSet<Integer> channelSet = new HashSet<Integer>();

		if (totWeekNum > 0) {
			// searchData생성
			for (int idx = 0; idx < totWeekNum; idx++) {
				String seasonName = CommonConstants.Season.valueOf(seasonOjb[idx].toString()).toString().trim();

				List<Object> ojbList = hm.get(seasonName);
				Iterator<Object> it = ojbList.iterator();
				while (it.hasNext()) {
					MvmDetailViewData mdvd = new MvmDetailViewData();
					Object[] obj = (Object[]) it.next();
					String date = seasonName;
					Integer channel = (Integer) obj[0];
					Double value = (Double) obj[4];
					mdvd.setDate(date);
					mdvd.setChannel(channel);
					mdvd.setValue(getDoubleToStirng(value));
					searchData.add(mdvd);
					compData.put(date + "@" + String.valueOf(channel), value);
					channelSet.add(channel);
				}
			}
			// 합계/최대/최소/평균 값 추출
			Iterator<Integer> its = channelSet.iterator();
			while (its.hasNext()) {
				Integer channel = its.next();
				MvmDetailViewData mdvd = new MvmDetailViewData();
				Double max = 0.0;
				Double sum = 0.0;
				Double avg = 0.0;
				Double min = 10000000.0;

				String keyChannel = String.valueOf(channel);
				for (int j = 0; j < totWeekNum; j++) {
					String seasonName = CommonConstants.Season.valueOf(seasonOjb[j].toString()).toString().trim();
					String key = seasonName + "@" + keyChannel;
					Double value = 0.0;
					if ((Double) compData.get(key) != null) {
						value = getNullToDouble((Double) compData.get(key));
						sum = sum + value;
						if (max < value)
							max = value;
						if (min > value)
							min = value;
					}
				}
				avg = sum / totWeekNum;
				mdvd.setAvgValue(String.valueOf(avg));
				mdvd.setMaxValue(String.valueOf(max));
				mdvd.setMinValue(String.valueOf(min));
				mdvd.setSumValue(String.valueOf(sum));
				mdvd.setChannel(channel);
				searchAddData.add(mdvd);
			}
		}
		result.put("searchData", searchData);
		result.put("searchAddData", searchAddData);
		return result;
	}

	/**
	 * @Method Name : makeSearchCondition
	 * @Date : 2010. 7. 1.
	 * @Method 설명 : 화면에서 넘어온 조회값으로 쿼리에 들어갈 조건을 생성한다.
	 * @param values
	 * @param type
	 * @return
	 */
	public HashMap<String, Object> makeSearchCondition(String[] values, String type) {
		HashMap<String, Object> hm = new HashMap<String, Object>();
		String beginDate = values[0];
		String endDate = values[1];
		String beginMonthDate = beginDate.substring(0, 6);
		String endMonthDate = endDate.substring(0, 6);
		String mdsId = values[4];
		Meter meter = mtrDao.findByCondition("mdsId", mdsId);
		int meterId = meter.getId();

		String meterType = ChangeMeterTypeName.valueOf(type).getCode();
		String[] tmpChannel = values[5].split(",");
		List<Integer> arrChannel = new ArrayList<Integer>();
		for (int idx = 0; idx < tmpChannel.length; idx++) {
			arrChannel.add(Integer.parseInt(tmpChannel[idx]));
		}
		hm.put("beginDate", beginDate);
		hm.put("endDate", endDate);
		hm.put("beginMonthDate", beginMonthDate);
		hm.put("endMonthDate", endMonthDate);
		hm.put("meterId", meterId);
		hm.put("arrChannel", arrChannel);
		hm.put("meterType", meterType);

		if (values.length > 7) {
			hm.put("avg", values[6]);
			hm.put("max", values[7]);
			hm.put("min", values[8]);
			hm.put("sum", values[9]);
			hm.put("isAll", StringUtil.nullToBlank(values[10])); // detail all
		}

		return hm;
	}

	/*
	 * makeSearchAddData 생성 dao -> 화면으로 데이터 넘기기전 MvmDetailVeiwData 형태로 데이터 조합함
	 * CHANNEL, MIN(TOTAL) MIN_VALUE, MAX(TOTAL) MAX_VALUE, AVG(TOTAL) AVG_VALUE,
	 * SUM(TOTAL) SUM_VALUE
	 */
	public List<MvmDetailViewData> makeSearchAddData(List<Object> ojbSearchAddData, String supplierId) {
		List<MvmDetailViewData> addData = new ArrayList<MvmDetailViewData>();

		Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));
		DecimalFormat df = DecimalUtil.getDecimalFormat(supplier.getMd());

		if (ojbSearchAddData.size() > 0) {
			Object[] arr = null;
			MvmDetailViewData mdvd = null;

			/*
			 * for (Object obj : ojbSearchAddData) { arr = (Object[])obj; mdvd = new
			 * MvmDetailViewData();
			 * 
			 * mdvd.setChannel((Integer)arr[0]);//채널
			 * mdvd.setMinValue(getDoubleToStirng((Double)arr[1]));//최소값
			 * mdvd.setMaxValue(getDoubleToStirng((Double)arr[2]));//최대값
			 * mdvd.setAvgValue(getDoubleToStirng((Double)arr[3]));//평균값
			 * mdvd.setSumValue(getDoubleToStirng((Double)arr[4]));//합계값
			 * 
			 * mdvd.setMinDecimalValue(df.format(getNullToDouble((Double)arr[1])));//최소값
			 * mdvd.setMaxDecimalValue(df.format(getNullToDouble((Double)arr[2])));//최대값
			 * mdvd.setAvgDecimalValue(df.format(getNullToDouble((Double)arr[3])));//평균값
			 * mdvd.setSumDecimalValue(df.format(getNullToDouble((Double)arr[4])));//합계값
			 * addData.add(mdvd); }
			 */
		}

		return addData;
	}

	/*
	 * makeSearchData 생성 dao -> 화면으로 데이터 넘기기전 MvmDetailVeiwData 형태로 데이터 조합함
	 */
	private List<MvmDetailViewData> makeSearchData(List<Object> ojbSearchData, String supplierId, String dateType) {
		List<MvmDetailViewData> searchData = new ArrayList<MvmDetailViewData>();

		Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));
		DecimalFormat df = DecimalUtil.getDecimalFormat(supplier.getMd());
		String lang = supplier.getLang().getCode_2letter();
		String country = supplier.getCountry().getCode_2letter();

		String localeDate;
		String reportDate = null;
		MvmDetailViewData mdvd = null;
		Object[] arr = null;

		if (ojbSearchData.size() > 0) {

			for (Object obj : ojbSearchData) {
				arr = (Object[]) obj;
				mdvd = new MvmDetailViewData();

				if (dateType.equals(DateType.HOURLY.getCode())) {
					localeDate = TimeLocaleUtil.getLocaleDateHour((String) arr[0], lang, country);
				} else if (dateType.equals(DateType.DAILY.getCode())) {
					localeDate = TimeLocaleUtil.getLocaleDate((String) arr[0], lang, country);
					localeDate = TimeLocaleUtil.getLocaleDay(localeDate, 8, lang, country);
					reportDate = TimeLocaleUtil.getLocaleDate((String) arr[0], lang, country);

				} else if (dateType.equals(DateType.MONTHLY.getCode())) {
					localeDate = TimeLocaleUtil.getLocaleDate((String) arr[0] + "01", lang, country);
					localeDate = TimeLocaleUtil.getLocaleMonth(localeDate, 8, lang, country);
					reportDate = TimeLocaleUtil.getLocaleYearMonth((String) arr[0], lang, country);
				} else {
					localeDate = (String) arr[0];
					reportDate = (String) arr[0];
				}

				mdvd.setDate(reportDate);
				mdvd.setLocaleDate(localeDate);

				mdvd.setChannel((Integer) arr[1]);
				mdvd.setValue(getDoubleToStirng((Double) arr[2]));
				mdvd.setDecimalValue(df.format(getNullToDouble((Double) arr[2])));
				searchData.add(mdvd);
			}
		}

		return searchData;
	}

	/**
	 * makeSearchDataWeek 생성 YYYYMMDD + 요일 dao -> 화면으로 데이터 넘기기전 MvmDetailVeiwData
	 * 형태로 데이터 조합함
	 */
	public List<MvmDetailViewData> makeSearchDataDayWeek(List<Object> ojbSearchData, String supplierId) {
		List<MvmDetailViewData> searchData = new ArrayList<MvmDetailViewData>();

		Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));
		String lang = supplier.getLang().getCode_2letter();
		String country = supplier.getCountry().getCode_2letter();

		DecimalFormat df = DecimalUtil.getDecimalFormat(supplier.getMd());
		String localeDate = null;

		MvmDetailViewData mdvd = null;
		Object[] arr = null;

		if (ojbSearchData.size() > 0) {

			for (Object obj : ojbSearchData) {
				mdvd = new MvmDetailViewData();
				arr = (Object[]) obj;

				localeDate = TimeLocaleUtil.getLocaleWeekDay((String) arr[0], lang, country);
				mdvd.setDate(localeDate); // TODO LOCALE GET
				mdvd.setLocaleDate(localeDate); // TODO LOCALE GET
				mdvd.setChannel((Integer) arr[1]);
				mdvd.setValue(getDoubleToStirng((Double) arr[2]));
				mdvd.setDecimalValue(df.format(getNullToDouble((Double) arr[2])));
				searchData.add(mdvd);
			}
		}

		return searchData;
	}

	// public List<MvmDetailViewData> makeSearchDataDayWeek4fc(List<Object>
	// ojbSearchData) {
	// List<MvmDetailViewData> searchData = new ArrayList<MvmDetailViewData>();
	//
	// SearchCalendarUtil scu = new SearchCalendarUtil();
	//
	// String[] strDayWeek = {"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};
	//
	// if(ojbSearchData.size() > 0) {
	// Iterator<Object> it = ojbSearchData.iterator();
	// while (it.hasNext()) {
	// MvmDetailViewData mdvd = new MvmDetailViewData();
	// Object[] obj = (Object[]) it.next();
	// String strDate = (String)obj[0];
	// String localeDate = TimeLocaleUtil.getLocaleDate(strDate) +" " +
	// strDayWeek[(scu.getDateTodayWeekNum(strDate) - 1)].toString();
	// mdvd.setDate(localeDate); // LOCALE GET
	// mdvd.setLocaleDate(localeDate); // LOCALE GET
	//
	// //mdvd.setDate(strDate);
	// mdvd.setChannel((Integer)obj[1]);
	// //mdvd.setChannelName(channelName);
	// mdvd.setValue(getDoubleToStirng((Double)obj[2]));
	// searchData.add(mdvd);
	// }
	// }
	// else {
	//
	// }
	//
	// return searchData;
	// }

	/*
	 * Double 형 데이터를 소스 4째자리까지 Stirng으로 표시
	 */
	public String getDoubleToStirng(Double value) {
		Double result = 0D;
		if (value != null && value != 0) {
			result = value;
		}

		return String.format("%.4f", result);
	}

	/*
	 * Double 형 데이터를 소스 4째자리까지 Double 형으로 표시
	 */
	public Double getNullToDouble(Double value) {
		Double result = 0D;
		String strValue = "0";
		if (value != null) {
			strValue = String.format("%.4f", value);
			result = Double.parseDouble(strValue);
		}

		return result;
	}

	/*
	 * Season의 계절별 시작일, 종료일 가져오기
	 */
	public HashMap<String, Object> getSeasonDate(String year) {

		HashMap<String, Object> hm = new HashMap<String, Object>();
		Iterator<Season> it = null;

		List<Season> searchSeasonList = seasonDao.getSeasonsBySyear(year);
		if (searchSeasonList.size() > 0 && searchSeasonList != null) {
			it = searchSeasonList.iterator();
			while (it.hasNext()) {
				Season retSeason = (Season) it.next();
				String[] searchDate = new String[2];

				if ("Spring".equals(retSeason.getName())) {
					searchDate[0] = retSeason.getSyear() + retSeason.getSmonth() + retSeason.getSday();
					searchDate[1] = retSeason.getEyear() + retSeason.getEmonth() + retSeason.getEday();
					hm.put("SPRING", searchDate[0] + "@" + searchDate[1]);
				} else if ("Summer".equals(retSeason.getName())) {
					searchDate[0] = retSeason.getSyear() + retSeason.getSmonth() + retSeason.getSday();
					searchDate[1] = retSeason.getEyear() + retSeason.getEmonth() + retSeason.getEday();
					hm.put("SUMMER", searchDate[0] + "@" + searchDate[1]);
				} else if ("Autumn".equals(retSeason.getName())) {
					searchDate[0] = retSeason.getSyear() + retSeason.getSmonth() + retSeason.getSday();
					searchDate[1] = retSeason.getEyear() + retSeason.getEmonth() + retSeason.getEday();
					hm.put("AUTUMN", searchDate[0] + "@" + searchDate[1]);
				} else {
					searchDate[0] = retSeason.getSyear() + retSeason.getSmonth() + retSeason.getSday();
					searchDate[1] = retSeason.getEyear() + retSeason.getEmonth() + retSeason.getEday();
					hm.put("WINTER", searchDate[0] + "@" + searchDate[1]);
				}
			}
		} else {
			List<Season> seasonList = seasonDao.getSeasonsBySyearIsNull();
			it = seasonList.iterator();

			while (it.hasNext()) {
				Season retSeason = (Season) it.next();
				String[] searchDate = new String[2];
				if ("Spring".equals(retSeason.getName())) {
					searchDate[0] = year + retSeason.getSmonth() + "01";
					searchDate[1] = year + retSeason.getEmonth() + "31";
					hm.put("SPRING", searchDate[0] + "@" + searchDate[1]);
				} else if ("Summer".equals(retSeason.getName())) {
					searchDate[0] = year + retSeason.getSmonth() + "01";
					searchDate[1] = year + retSeason.getEmonth() + "31";
					hm.put("SUMMER", searchDate[0] + "@" + searchDate[1]);
				} else if ("Autumn".equals(retSeason.getName())) {
					searchDate[0] = year + retSeason.getSmonth() + "01";
					searchDate[1] = year + retSeason.getEmonth() + "31";
					hm.put("AUTUMN", searchDate[0] + "@" + searchDate[1]);
				} else {
					searchDate[0] = year + retSeason.getSmonth() + "01";
					searchDate[1] = (Integer.parseInt(year) + 1) + retSeason.getEmonth() + "31";// 겨울의 종료일은 다음해로 넘어감
					hm.put("WINTER", searchDate[0] + "@" + searchDate[1]);
				}
			}
		}

		return hm;
	}

	private List<Object> makeDetailDailySearchData(List<Map<String, Object>> list, String startDate, String endDate) {
		List<Object> result = new ArrayList<Object>();
		Object[] arr = null;

		String startMonth = startDate.substring(0, 6);
		String endMonth = endDate.substring(0, 6);

		Integer startMonthDay = Integer.valueOf(startDate.substring(6, 8));
		Integer endMonthDay = Integer.valueOf(endDate.substring(6, 8));
		int i = 1;
		int len = 32;

		for (Map<String, Object> obj : list) {
			i = 1;
			len = 32;

			if (startMonth.equals((String) obj.get("yyyymm"))) {
				i = startMonthDay;
			}

			if (endMonth.equals((String) obj.get("yyyymm"))) {
				len = endMonthDay + 1;
			}

			for (; i < len; i++) {
				if (obj.get("value_" + StringUtil.frontAppendNStr('0', i + "", 2)) != null) {
					arr = new Object[3];
					arr[0] = obj.get("yyyymm") + StringUtil.frontAppendNStr('0', i + "", 2);
					arr[1] = obj.get("channel");
					arr[2] = DecimalUtil
							.ConvertNumberToDouble(obj.get("value_" + StringUtil.frontAppendNStr('0', i + "", 2)));
					result.add(arr);
				}
			}
		}

		return result;
	}

	private List<Object> makeDetailDailySearchDataSumAvg(List<Map<String, Object>> list, String startDate,
			String endDate) {
		List<Object> result = new ArrayList<Object>();
		Object[] arr = null;

		String startMonth = startDate.substring(0, 6);
		String endMonth = endDate.substring(0, 6);

		Integer startMonthDay = Integer.valueOf(startDate.substring(6, 8));
		Integer endMonthDay = Integer.valueOf(endDate.substring(6, 8));
		int i = 0;
		int len = 0;
		int cnt = 0;
		BigDecimal bdMin = new BigDecimal(0);
		BigDecimal bdMax = new BigDecimal(0);
		BigDecimal bdAvg = new BigDecimal(0);
		BigDecimal bdSum = new BigDecimal(0);
		BigDecimal bdVal = new BigDecimal(0);
		Integer prevChannel = null;

		for (Map<String, Object> obj : list) {
			if (prevChannel != null && !prevChannel.equals(DecimalUtil.ConvertNumberToInteger(obj.get("channel")))) {
				if (cnt != 0) {
					arr = new Object[5];
					arr[0] = prevChannel;
					arr[1] = bdMin.doubleValue();
					arr[2] = bdMax.doubleValue();
					bdAvg = (bdSum.equals(new BigDecimal(0))) ? new BigDecimal(0)
							: bdSum.divide(new BigDecimal(cnt), MathContext.DECIMAL128);
					arr[3] = bdAvg.doubleValue();
					arr[4] = bdSum.doubleValue();
					result.add(arr);
				}
				prevChannel = DecimalUtil.ConvertNumberToInteger(obj.get("channel"));
				bdMin = new BigDecimal(0);
				bdMax = new BigDecimal(0);
				bdAvg = new BigDecimal(0);
				bdSum = new BigDecimal(0);
				cnt = 0;
			} else if (prevChannel == null) {
				prevChannel = DecimalUtil.ConvertNumberToInteger(obj.get("channel"));
			}
			i = 1;
			len = 32;

			if (startMonth.equals((String) obj.get("yyyymm"))) {
				i = startMonthDay;
			}

			if (endMonth.equals((String) obj.get("yyyymm"))) {
				len = endMonthDay + 1;
			}

			for (; i < len; i++) {
				if (obj.get("value_" + StringUtil.frontAppendNStr('0', i + "", 2)) != null) {
					bdVal = new BigDecimal(DecimalUtil
							.ConvertNumberToDouble(obj.get("value_" + StringUtil.frontAppendNStr('0', i + "", 2))));
					bdMin = bdMin.min(bdVal);
					bdMax = bdMax.max(bdVal);
					bdSum = bdSum.add(bdVal);
					bdAvg = new BigDecimal(0);
					cnt++;
				}
			}
		}

		if (prevChannel != null && cnt != 0) {
			arr = new Object[5];
			arr[0] = prevChannel;
			arr[1] = bdMin.doubleValue();
			arr[2] = bdMax.doubleValue();
			bdAvg = (bdSum.equals(new BigDecimal(0))) ? new BigDecimal(0)
					: bdSum.divide(new BigDecimal(cnt), MathContext.DECIMAL128);
			arr[3] = bdAvg.doubleValue();
			arr[4] = bdSum.doubleValue();

			result.add(arr);
		}
		return result;
	}

	/**
	 * method name : getMeteringDataDetailHourlyData<b/> method Desc : Metering Data
	 * 맥스가젯 상세화면에서 시간별 검침데이터를 조회한다.
	 *
	 * @param conditionMap
	 * @return
	 */
	public List<Map<String, Object>> getMeteringDataDetailHourlyData(Map<String, Object> conditionMap) {
		return getMeteringDataDetailHourlyData(conditionMap, false);
	}

	/**
	 * method name : getMeteringDataDetailHourlyData<b/> method Desc : Metering Data
	 * 맥스가젯 상세화면에서 시간별 검침데이터를 조회한다. lpInterval 기준
	 *
	 * @param conditionMap
	 * @param isLpInterval
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getMeteringDataDetailHourlyData(Map<String, Object> conditionMap,
			boolean isLpInterval) {
		logger.debug("=========== getMeteringDataDetailHourlyData Called ===========");
		// Declare result variable
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		
		// Get conditionMap data. 
		Integer supplierId = (Integer) conditionMap.get("supplierId");
		String searchStartDate = (String) conditionMap.get("searchStartDate");
		String searchEndDate = (String) conditionMap.get("searchEndDate");
//		String searchStartHour = (String) conditionMap.get("searchStartHour");
//		String searchEndHour = (String) conditionMap.get("searchEndHour");
		// "ch1,ch2,ch3,..." -> ["ch1","ch2","ch3",...]
		String[] channelArray = ((String) conditionMap.get("channel")).split(",");
//		Meter meter = mtrDao.get((String) conditionMap.get("meterNo"));


		List<Integer> channelIdList = new ArrayList<Integer>();

		// Get supplier config
		Supplier supplier = supplierDao.get(supplierId);
		String country = supplier.getCountry().getCode_2letter();
		String lang = supplier.getLang().getCode_2letter();
		DecimalFormat mdf = DecimalUtil.getDecimalFormat(supplier.getMd());

		// ["ch1","ch2","ch3",...] -> List<Integer> { ch1, ch2, ch3, ... }
		for (String obj : channelArray) {
			channelIdList.add(Integer.parseInt(obj));
		}
		
		// Put to conditionMap
		conditionMap.put("channelIdList", channelIdList);
		
		// Get data from DB
		List<Map<String, Object>> list = meteringLpDao.getMeteringDataDetailHourlyData(conditionMap, false);
		
		// Merge by hour and channel
		Map<String, Map<String, Object>> mergeHourMap = new LinkedHashMap<>();
		for(Map<String, Object> obj : list) {
			String YYYYMMDDHH = (String) obj.get("YYYYMMDDHH");
			String CHANNEL = (String) obj.get("CHANNEL");
			BigDecimal DST = (BigDecimal) obj.get("DST");
			BigDecimal VALUE = (BigDecimal) obj.get("VALUE");
			
			if(mergeHourMap.containsKey(YYYYMMDDHH)) { // Old YYYYMMDDHH -> Add Channel
				Map<String, Object> tmpMap = mergeHourMap.get(YYYYMMDDHH);
				tmpMap.put("channel_"+CHANNEL, VALUE);
			}else { // New YYYYMMDDHH -> Just put data with Time
				Map<String, Object> tmpMap = new HashMap<>();
				tmpMap.put("channel_"+CHANNEL, VALUE);
				tmpMap.put("meteringTime", TimeLocaleUtil.getLocaleDateHourMinute(YYYYMMDDHH+"00", lang, country));
				mergeHourMap.put(YYYYMMDDHH, tmpMap);
			}
		}

		// Make dateSet
		Set<String> dateSet = new LinkedHashSet<String>();
		String startYYYYMMDDHH = searchStartDate + "00" ;
		for (int k = 0; k < 100; k++) { // 무한 loop 방지
			dateSet.add(startYYYYMMDDHH);
			try {
				startYYYYMMDDHH = DateTimeUtil.getPreHour(startYYYYMMDDHH, -1).substring(0, 10); // -(-1) Hour -> +1 Hour
			} catch (ParseException e) {
				e.printStackTrace();
			}
			if (startYYYYMMDDHH.compareTo(searchEndDate + "24") >= 0) { // 종료일자이면 종료
				break;
			}
		}
		
		// Make results
		Iterator<String> itr = dateSet.iterator();
		while (itr.hasNext()) {
			String date = itr.next();
			if(mergeHourMap.containsKey(date)) {
				Map<String, Object> tmpMap = mergeHourMap.get(date);
				// Set format to value
				for(Integer chId : channelIdList) {
					tmpMap.put("channel_"+chId, mdf.format(tmpMap.get("channel_"+chId)) );
				}
				result.add(tmpMap);
			}else{
				Map<String, Object> tmpMap = new HashMap<String, Object>();
				for (String obj : channelArray) {
					tmpMap.put("channel_"+obj, "-");
				}
				tmpMap.put("meteringTime", TimeLocaleUtil.getLocaleDateHour(date+"00", lang, country));
				result.add(tmpMap);
			}
		}
		return result;
	}

	/**
	 * method name : getMeteringDataDetailHIntervalData<b/> method Desc : Metering
	 * Data 맥스가젯 상세화면에서 검침구간별 검침데이터를 조회한다.
	 *
	 * @param conditionMap
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getMeteringDataDetailIntervalData(Map<String, Object> conditionMap) {
		logger.debug("=========== getMeteringDataDetailHIntervalData Called ===========");
		// Declare result variable
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

		// Get conditionMap data. 
		Integer supplierId = (Integer) conditionMap.get("supplierId");
		String searchStartDate = (String) conditionMap.get("searchStartDate");
		String searchEndDate = (String) conditionMap.get("searchEndDate");
		String searchStartHour = (String) conditionMap.get("searchStartHour");
		String searchEndHour = (String) conditionMap.get("searchEndHour");
		// "ch1,ch2,ch3,..." -> ["ch1","ch2","ch3",...]
		String[] channelArray = ((String) conditionMap.get("channel")).split(",");
		Meter meter = mtrDao.get((String) conditionMap.get("meterNo"));
		Integer lpInterval = (meter.getLpInterval() != null) ? meter.getLpInterval() : 60;

		List<Integer> channelIdList = new ArrayList<Integer>();
		List<Map<String, Object>> lpAllList = null;

		// Get supplier config
		Supplier supplier = supplierDao.get(supplierId);
		String country = supplier.getCountry().getCode_2letter();
		String lang = supplier.getLang().getCode_2letter();
		DecimalFormat mdf = DecimalUtil.getDecimalFormat(supplier.getMd());

		// ["ch1","ch2","ch3",...] -> List<Integer> { ch1, ch2, ch3, ... }
		for (String obj : channelArray) {
			channelIdList.add(Integer.parseInt(obj));
		}

		// Put to conditionMap
		conditionMap.put("channelIdList", channelIdList);

		// Get data from DB
		lpAllList = meteringLpDao.getMeteringDataDetailLpData(conditionMap);

		// Merge by channel
		Map<String, Map<String, Object>> mergeChannelMap = new LinkedHashMap<>();
		for (Map<String, Object> obj : lpAllList) { // Old YYYYMMDDHH -> Append 
			String YYYYMMDDHHMISS = (String) obj.get("YYYYMMDDHHMISS");
			String YYYYMMDDHHMI = YYYYMMDDHHMISS.substring(0, 12);
			BigDecimal CHANNEL = (BigDecimal) obj.get("CHANNEL");
			BigDecimal DST = (BigDecimal) obj.get("DST");
			BigDecimal VALUE = (BigDecimal) obj.get("VALUE");
			if(mergeChannelMap.containsKey(YYYYMMDDHHMI)) {
				Map<String, Object> tmpMap = mergeChannelMap.get(YYYYMMDDHHMI);
				tmpMap.put("channel_"+CHANNEL, VALUE);
			}else { // New YYYYMMDDHHMI -> Just put data with Time
				Map<String, Object> tmpMap = new HashMap<>();
//				tmpMap.put("dst", DST);
				tmpMap.put("channel_"+CHANNEL, VALUE);
				tmpMap.put("meteringTime", TimeLocaleUtil.getLocaleDateHourMinute(YYYYMMDDHHMI, lang, country));
//				tmpMap.put("iconCls", "no-icon");
				mergeChannelMap.put(YYYYMMDDHHMI, tmpMap);
			}
		}

		// Make dateSet
		Set<String> dateSet = new LinkedHashSet<String>();
		String startYYYYMMDDHHMI = searchStartDate + searchStartHour + "00" ;
		for (int k = 0; k < 100; k++) { // 무한 loop 방지
			dateSet.add(startYYYYMMDDHHMI);
			try {
				startYYYYMMDDHHMI = DateTimeUtil.getPreMinute(startYYYYMMDDHHMI, -1*lpInterval).substring(0, 12); // +interval
			} catch (ParseException e) {
				e.printStackTrace();
			}
			if (startYYYYMMDDHHMI.compareTo(searchEndDate + searchEndHour + "59") >= 0) { // 종료일자이면 종료
				break;
			}
		}

		// Make results
		Iterator<String> itr = dateSet.iterator();
		while (itr.hasNext()) {
			String date = itr.next();
//			logger.debug("date : "+date);
			if(mergeChannelMap.containsKey(date)) {
				Map<String, Object> tmpMap = mergeChannelMap.get(date);
				// Set format to value
				for(Integer chId : channelIdList) {
					tmpMap.put("channel_"+chId, mdf.format(tmpMap.get("channel_"+chId)) );
				}
				result.add(mergeChannelMap.get(date));
			}else{
				Map<String, Object> tmpMap = new HashMap<String, Object>();
//				tmpMap.put("dst", null);
				for (String obj : channelArray) {
					tmpMap.put("channel_"+obj, "-");
				}
				tmpMap.put("meteringTime", TimeLocaleUtil.getLocaleDateHourMinute(date, lang, country));
//				tmpMap.put("iconCls", "no-icon");
				result.add(tmpMap);
			}
		}

		return result;
	}

	public static String[] LP_STATUS_BIT = new String[] { 
			"Power down", // bit 7
			"Not used", 
			"Clock adjusted", 
			"Not used", 
			"Daylight saving", 
			"Data not valid", 
			"Clock invalid",
			"Critical error" // bit 0
	};

	public static String getLP_STATUS(byte[] value) {
		StringBuffer str = new StringBuffer("");
		int byte0 = value[0] & 0xFF;
		for (int i = 0; i < 8; i++) {
			if ((byte0 & (1 << (7 - i))) > 0) {
				str.append(LP_STATUS_BIT[i] + ", \n");
			}
		}
		if (byte0 == 0x00) {
			str.append("Valid");
		}
		return str.toString();
	}

	/**
	 * method name : getMeteringDataDetailHourlyChartData<b/> method Desc : Metering
	 * Data 맥스가젯 상세화면에서 시간별 Chart 데이터를 조회한다.
	 *
	 * @param conditionMap
	 * @return
	 */
	public Map<String, Object> getMeteringDataDetailHourlyChartData(Map<String, Object> conditionMap) {
		// Get variables from conditionMap
		String searchStartDate = (String) conditionMap.get("searchStartDate");
		Integer supplierId = (Integer) conditionMap.get("supplierId");
		String[] channelArray = ((String) conditionMap.get("channel")).split(",");
//		Meter meter = mtrDao.get((String) conditionMap.get("meterNo"));

		// Define variables
//		Integer lpInterval = (meter.getLpInterval() != null) ? meter.getLpInterval() : 60;
		Supplier supplier = supplierDao.get(supplierId);
		DecimalFormat df = DecimalUtil.getDecimalFormat(supplier.getMd());
		String lang = supplier.getLang().getCode_2letter();
		String country = supplier.getCountry().getCode_2letter();
		List<Integer> channelIdList = new ArrayList<Integer>();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Map<String, Object>> searchData = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> searchAddData = new ArrayList<Map<String, Object>>();

		// Make channel ID list from channel array.
		for (String obj : channelArray) {
			channelIdList.add(Integer.parseInt(obj));
		}

		// Put channel id list at conditionMap
		conditionMap.put("channelIdList", channelIdList);

		// Get LP data from DB.
		List<Map<String, Object>> list = meteringLpDao.getMeteringDataDetailHourlyData(conditionMap, false);

		// Check size of list and return.
		if (list == null || list.size() <= 0) {
			resultMap.put("searchData", searchData);
			resultMap.put("searchAddData", searchAddData);
			logger.debug("list is null or empty. return result : " + resultMap);
			return resultMap;
		}
		
		// Merge by hour and channel
		List<BigDecimal> sumList = new ArrayList<>();
		List<BigDecimal> maxList = new ArrayList<>();
		List<BigDecimal> minList = new ArrayList<>(); 
		for(int i = 0; i < channelIdList.size(); i++) { // Init
			sumList.add(new BigDecimal(0));
			maxList.add(new BigDecimal(0));
			minList.add(new BigDecimal(0));
		}
		Map<String, BigDecimal> valMap = new LinkedHashMap<>();
		for(Map<String, Object> obj : list) {
			String YYYYMMDDHH = (String) obj.get("YYYYMMDDHH");
			String CHANNEL = (String) obj.get("CHANNEL");
//			BigDecimal DST = (BigDecimal) obj.get("DST");
			BigDecimal VALUE = (BigDecimal) obj.get("VALUE");
			
			//
//			int chIdx = channelIdList.indexOf(Integer.valueOf(CHANNEL));
//			if(VALUE != null) {
//				BigDecimal sumVal = sumList.get(chIdx);
//				BigDecimal maxVal = maxList.get(chIdx);
//				BigDecimal minVal = minList.get(chIdx);
//				sumVal = sumVal.add(VALUE);
//				maxVal = maxVal.max(VALUE);
//				minVal = minVal.min(VALUE);
//				sumList.add(chIdx, sumVal);
//				maxList.add(chIdx, maxVal);
//				minList.add(chIdx, minVal);
//			}

			valMap.put(YYYYMMDDHH+"_"+CHANNEL, VALUE);
		}
		for(int i = 0; i < 24; i++) { // Hourly
			for(Integer ch : channelIdList) {
				Map<String, Object> tmpMap = new HashMap<>();
				tmpMap.put("date", i);
				tmpMap.put("localeDate", i);
				tmpMap.put("channel", ch);
				BigDecimal tmpBd = (BigDecimal) valMap.get(searchStartDate + CalendarUtil.to2Digit(i) + "_" + ch.toString());
				tmpMap.put("value", (tmpBd == null) ? 0D : tmpBd.doubleValue());
				tmpMap.put("decimalValue", (tmpBd == null) ? df.format(0D) : df.format(tmpBd));
				tmpMap.put("reportDate",
						TimeLocaleUtil.getLocaleDateHour(searchStartDate + CalendarUtil.to2Digit(i), lang, country));
				searchData.add(tmpMap);
			}
		}
//		for(Integer channel : channelIdList) {
//			Map<String, Object> tmpMap = new HashMap<>();
//			int chIdx = channelIdList.indexOf(channel);
//			BigDecimal sumVal = sumList.get(chIdx);
//			BigDecimal maxVal = maxList.get(chIdx);
//			BigDecimal minVal = minList.get(chIdx);
//			tmpMap.put("channel", channel);
//			tmpMap.put("minValue", minVal);
//			tmpMap.put("maxValue", maxVal);
//			tmpMap.put("avgValue", sumVal.divide(new BigDecimal(24), BigDecimal.ROUND_CEILING));
//			tmpMap.put("sumValue", sumVal);
//			tmpMap.put("minDecimalValue", df.format(minVal));
//			tmpMap.put("maxDecimalValue", df.format(maxVal));
//			tmpMap.put("avgDecimalValue", df.format(sumVal.divide(new BigDecimal(list.size()), BigDecimal.ROUND_CEILING)));
//			tmpMap.put("sumDecimalValue", df.format(sumVal));
//			searchAddData.add(tmpMap);
//		}

		resultMap.put("searchData", searchData);
		resultMap.put("searchAddData", searchAddData);

		return resultMap;
	}

	/**
	 * method name : getMeteringDataDetailLpData<b/> method Desc : Metering Data
	 * 맥스가젯 상세화면에서 주기별 검침데이터를 조회한다.
	 *
	 * @param conditionMap
	 * @return
	 */
	public List<Map<String, Object>> getMeteringDataDetailLpData(Map<String, Object> conditionMap) {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		Integer supplierId = (Integer) conditionMap.get("supplierId");
		Integer dst = (Integer) conditionMap.get("dst");
		String searchStartDate = (String) conditionMap.get("searchStartDate");
		String searchEndDate = (String) conditionMap.get("searchEndDate");
		String searchStartHour = StringUtil.nullToBlank(conditionMap.get("searchStartHour"));
		String searchEndHour = StringUtil.nullToBlank(conditionMap.get("searchEndHour"));
		String[] channelArray = ((String) conditionMap.get("channel")).split(",");
		List<Integer> channelIdList = new ArrayList<Integer>();

		for (String obj : channelArray) {
			channelIdList.add(Integer.parseInt(obj));
		}

		conditionMap.put("channelIdList", channelIdList);

		Meter meter = mtrDao.findByCondition("mdsId", (String) conditionMap.get("meterNo"));
		Integer lpInterval = (meter.getLpInterval() == null) ? 60 : meter.getLpInterval();

		List<Map<String, Object>> list = meteringLpDao.getMeteringDataDetailLpData(conditionMap);
		Map<String, Object> map = null;
		Map<String, Double> listMap = new HashMap<String, Double>();
		Set<String> dateSet = new LinkedHashSet<String>();

		Supplier supplier = supplierDao.get(supplierId);
		String country = supplier.getCountry().getCode_2letter();
		String lang = supplier.getLang().getCode_2letter();
		DecimalFormat mdf = DecimalUtil.getDecimalFormat(supplier.getMd());
		Double tmpValue = null;
		String lpmin = null;
		String tmpLocaleDateHour = searchStartDate + searchStartHour;

		// 조회조건 내 모든 일자 가져오기
		for (int k = 0; k < 100; k++) { // 무한 loop 방지
			for (int i = 0, j = 0; j < 60; i++, j = i * lpInterval) {
				dateSet.add(tmpLocaleDateHour + CalendarUtil.to2Digit(j));
			}

			if (tmpLocaleDateHour.compareTo(searchEndDate + searchEndHour) >= 0) { // 종료일자이면 종료
				break;
			} else {
				try {
					tmpLocaleDateHour = DateTimeUtil.getPreHour(tmpLocaleDateHour + "0000", -1).substring(0, 10); // +1
																													// 시간
																													// 더함
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}

		for (Map<String, Object> obj : list) {
			for (int i = 0, j = 0; j < 60; i++, j = i * lpInterval) {
				lpmin = CalendarUtil.to2Digit(j);
				tmpValue = DecimalUtil.ConvertNumberToDouble(obj.get("value_" + lpmin));
				listMap.put((String) obj.get("yyyymmddhh") + lpmin + "_" + obj.get("channel"), tmpValue);
			}
		}

		for (String lpDate : dateSet) {
			map = new HashMap<String, Object>();
			map.put("id", (dst != null) ? lpDate + dst.toString() : lpDate);
			map.put("meteringTime", TimeLocaleUtil.getLocaleDate(lpDate + "00", lang, country));

			for (Integer ch : channelIdList) {
				tmpValue = listMap.get(lpDate + "_" + ch);
				map.put("channel_" + ch, (tmpValue == null) ? "- " : mdf.format(tmpValue));
			}
			map.put("iconCls", "task");
			map.put("iconCls", "no-icon");
			map.put("leaf", true);

			result.add(map);
		}
		return result;
	}

	/**
	 * method name : getMeteringDataDetailDailyData<b/> method Desc : Metering Data
	 * 맥스가젯 상세화면에서 일별 검침데이터를 조회한다.
	 *
	 * @param conditionMap
	 * @return
	 */
	public List<Map<String, Object>> getMeteringDataDetailDailyData(Map<String, Object> conditionMap) {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		Integer supplierId = (Integer) conditionMap.get("supplierId");
		String searchStartDate = (String) conditionMap.get("searchStartDate");
		String searchEndDate = (String) conditionMap.get("searchEndDate");
		Supplier supplier = supplierDao.get(supplierId);
		String country = supplier.getCountry().getCode_2letter();
		String lang = supplier.getLang().getCode_2letter();
		DecimalFormat mdf = DecimalUtil.getDecimalFormat(supplier.getMd());
		
		// Set channelIdList
		String[] channelArray = ((String) conditionMap.get("channel")).split(",");
		List<Integer> channelIdList = new ArrayList<Integer>();
		for (String obj : channelArray) {
			channelIdList.add(Integer.parseInt(obj));
		}
		conditionMap.put("channelIdList", channelIdList);

		// Get data from DB
		List<Map<String, Object>> list = meteringDayDao.getMeteringDataDetailDailyData(conditionMap, false);
		
		Map<String, Object> listMap = new HashMap<String, Object>();

		// Get duration for maximum loop
		int duration = 0;
		try {
			duration = TimeUtil.getDayDuration(searchStartDate, searchEndDate);
		} catch (ParseException e) {
			logger.error(e,e);
		}

		// Set dateSet
		Set<String> dateSet = new LinkedHashSet<String>();
		String tmpDate = searchStartDate;
		for (int i = 0; i <= duration; i++) {
			dateSet.add(tmpDate);
			try {
				tmpDate = TimeUtil.getPreDay(tmpDate + "000000", -1).substring(0, 8);
			} catch (ParseException e) {
				logger.error(e,e);
			}
		}

		for (Map<String, Object> obj : list) {
			listMap.put((String) obj.get("YYYYMMDD") + "_" + (String) obj.get("CHANNEL"), obj.get("VALUE"));
			// dateSet.add((String)obj.get("YYYYMMDD"));
		}

		Iterator<String> itr = dateSet.iterator();
		String date = null;

		while (itr.hasNext()) {
			date = itr.next();
			Map<String, Object> map = new HashMap<String, Object>();

			map.put("meteringTime", TimeLocaleUtil.getLocaleDate(date, lang, country));

			for (Integer obj : channelIdList) {
				Double value = DecimalUtil.ConvertNumberToDouble(listMap.get(date + "_" + obj));
				map.put("channel_" + obj, (value == null) ? "- " : mdf.format(value));
			}

			result.add(map);
		}
		return result;
	}

	/**
	 * method name : getMeteringDataDetailDailyChartData<b/> method Desc : Metering
	 * Data 맥스가젯 상세화면에서 일별 Chart 데이터를 조회한다.
	 *
	 * @param conditionMap
	 * @return
	 */
	public Map<String, Object> getMeteringDataDetailDailyChartData(Map<String, Object> conditionMap) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Map<String, Object>> searchData = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> searchAddData = new ArrayList<Map<String, Object>>();
		Integer supplierId = (Integer) conditionMap.get("supplierId");
		String searchStartDate = (String) conditionMap.get("searchStartDate");
		String searchEndDate = (String) conditionMap.get("searchEndDate");
		String[] channelArray = ((String) conditionMap.get("channel")).split(",");
		List<Integer> channelIdList = new ArrayList<Integer>();

		for (String obj : channelArray) {
			channelIdList.add(Integer.parseInt(obj));
		}

		conditionMap.put("channelIdList", channelIdList);

		List<Map<String, Object>> list = meteringDayDao.getMeteringDataDetailDailyData(conditionMap, false);

		if (list == null || list.size() <= 0) {
			resultMap.put("searchData", searchData);
			resultMap.put("searchAddData", searchAddData);
			return resultMap;
		}

		Map<String, Double> listMap = new HashMap<String, Double>();
		Set<String> dateSet = new LinkedHashSet<String>();
		Map<String, Object> map = null;
		Supplier supplier = supplierDao.get(supplierId);
		String country = supplier.getCountry().getCode_2letter();
		String lang = supplier.getLang().getCode_2letter();
		DecimalFormat mdf = DecimalUtil.getDecimalFormat(supplier.getMd());
		Double tmpValue = null;
		Double sumValue = null;
		Double avgValue = null;
		Double maxValue = null;
		Double minValue = null;

		int duration = 0;

		try {
			duration = TimeUtil.getDayDuration(searchStartDate, searchEndDate);
		} catch (ParseException e1) {
			e1.printStackTrace();
		}

		String tmpDate = searchStartDate;
		for (int i = 0; i < (duration + 1); i++) {
			dateSet.add(tmpDate);
			try {
				tmpDate = TimeUtil.getPreDay(tmpDate + "000000", -1).substring(0, 8);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		for (Map<String, Object> obj : list) {
			listMap.put((String) obj.get("YYYYMMDD") + "_" + obj.get("CHANNEL"),
					DecimalUtil.ConvertNumberToDouble(obj.get("VALUE")));
		}

		for (String date : dateSet) {
			for (Integer ch : channelIdList) {
				map = new HashMap<String, Object>();
				map.put("reportDate", TimeLocaleUtil.getLocaleDate(date, lang, country));
				// map.put("localeDate", Integer.valueOf(date.substring(6, 8)));
				map.put("localeDate", TimeLocaleUtil.getLocaleDate(date, lang, country));
				map.put("channel", ch);
				tmpValue = listMap.get(date + "_" + ch);
				map.put("value", (tmpValue == null) ? 0D : tmpValue.doubleValue());
				map.put("decimalValue", (tmpValue == null) ? mdf.format(0D) : mdf.format(tmpValue.doubleValue()));
				searchData.add(map);
			}
		}

		resultMap.put("searchData", searchData);

//		List<Map<String, Object>> sumList = meteringDayDao.getMeteringDataDetailDailyData(conditionMap, true);
		/*
		 * if (sumList != null && sumList.size() > 0) { for (Map<String, Object> obj :
		 * sumList) { if
		 * (DecimalUtil.ConvertNumberToInteger(obj.get("CHANNEL")).equals(1)) { map =
		 * new HashMap<String, Object>();
		 * 
		 * sumValue = DecimalUtil.ConvertNumberToDouble(obj.get("SUM_VAL")); avgValue =
		 * DecimalUtil.ConvertNumberToDouble(obj.get("AVG_VAL")); maxValue =
		 * DecimalUtil.ConvertNumberToDouble(obj.get("MAX_VAL")); minValue =
		 * DecimalUtil.ConvertNumberToDouble(obj.get("MIN_VAL"));
		 * 
		 * map.put("sumValue", (sumValue == null) ? 0D : sumValue); map.put("avgValue",
		 * (avgValue == null) ? 0D : avgValue); map.put("maxValue", (maxValue == null) ?
		 * 0D : maxValue); map.put("minValue", (minValue == null) ? 0D : minValue);
		 * 
		 * map.put("sumDecimalValue", (sumValue == null) ? mdf.format(0D) :
		 * mdf.format(sumValue)); map.put("avgDecimalValue", (avgValue == null) ?
		 * mdf.format(0D) : mdf.format(avgValue)); map.put("maxDecimalValue", (maxValue
		 * == null) ? mdf.format(0D) : mdf.format(maxValue)); map.put("minDecimalValue",
		 * (minValue == null) ? mdf.format(0D) : mdf.format(minValue));
		 * 
		 * searchAddData.add(map); break; } } }
		 */

		resultMap.put("searchAddData", searchAddData);

		return resultMap;
	}

	/**
	 * method name : getMeteringDataDetailWeeklyData<b/> method Desc : Metering Data
	 * 맥스가젯 상세화면에서 주별 검침데이터를 조회한다.
	 *
	 * @param conditionMap
	 * @return
	 */
	public List<Map<String, Object>> getMeteringDataDetailWeeklyData(Map<String, Object> conditionMap) {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		Integer supplierId = (Integer) conditionMap.get("supplierId");
		String[] channelArray = ((String) conditionMap.get("channel")).split(",");
		
		List<Integer> channelIdList = new ArrayList<Integer>();
		for (String obj : channelArray) {
			channelIdList.add(Integer.parseInt(obj));
		}
		conditionMap.put("channelIdList", channelIdList);

		List<Map<String, Object>> list = meteringDayDao.getMeteringDataDetailDailyData(conditionMap, false);

		if (list == null || list.size() <= 0) {
			return result;
		}

		Set<String> dateSet = new LinkedHashSet<String>();
		Map<String, Object> listMap = new HashMap<String, Object>();
		Map<String, Object> chMethodMap = new HashMap<String, Object>();
		Map<String, Object> map = null;
		Supplier supplier = supplierDao.get(supplierId);
		String country = supplier.getCountry().getCode_2letter();
		String lang = supplier.getLang().getCode_2letter();
		DecimalFormat mdf = DecimalUtil.getDecimalFormat(supplier.getMd());
		Double value = null; // 계산용 임시변수

		for (Map<String, Object> obj : list) {
			listMap.put((String) obj.get("YYYYMMDD") + "_" + (String) obj.get("CHANNEL"), obj.get("VALUE"));
			dateSet.add((String) obj.get("YYYYMMDD"));
			chMethodMap.put(obj.get("CHANNEL").toString(), obj.get("CH_METHOD"));
		}

		List<Map<String, String>> weeksList = new ArrayList<Map<String, String>>();
		String searchStartDate = (String) conditionMap.get("searchStartDate");
		String searchEndDate = (String) conditionMap.get("searchEndDate");

		String startMonth = searchStartDate.substring(0, 6);
		Integer startWeek = CalendarUtil.getWeekOfMonth(searchStartDate);
		String endMonth = searchEndDate.substring(0, 6);
		Integer endWeek = CalendarUtil.getWeekOfMonth(searchEndDate);
		String curMonth = null;
		int firstWeek = 1;
		int lastWeek = 0;

		if (startMonth.equals(endMonth)) { // 년월이 동일한 경우
			curMonth = startMonth;

			// 해당월의 각 주에 해당하는 시작일자 종료일자를 구한다.
			for (int i = startWeek; i <= endWeek; i++) {
				weeksList.add(
						CalendarUtil.getDateWeekOfMonth(curMonth.substring(0, 4), curMonth.substring(4, 6), i + ""));
			}
		} else { // 연월이 다를 경우
			for (int i = 0; i < 100; i++) { // 무한 loop 를 피하기 위해 for 문으로 loop 회수 제한
				if (i == 0) {
					curMonth = startMonth;
					firstWeek = startWeek;
				} else {
					curMonth = (CalendarUtil.getDate(curMonth + "01", Calendar.MONTH, 1)).substring(0, 6);
					firstWeek = 1;
				}

				if (curMonth.equals(endMonth)) {
					lastWeek = endWeek;
				} else {
					lastWeek = Integer.parseInt(
							CalendarUtil.getWeekCountOfMonth(curMonth.substring(0, 4), curMonth.substring(4)));
				}

				for (int j = firstWeek; j <= lastWeek; j++) {
					weeksList.add(
							CalendarUtil.getDateWeekOfMonth(curMonth.substring(0, 4), curMonth.substring(4), j + ""));
				}

				if (curMonth.equals(endMonth)) {
					break;
				}
			}
		}

		int len = weeksList.size();
		Map<String, String> weekDatesMap = new HashMap<String, String>();
		String startDate = null;
		String endDate = null;
		String curDate = null;
		List<BigDecimal> bdTotalSumList = new ArrayList<BigDecimal>();
		List<BigDecimal> bdTotalMaxList = new ArrayList<BigDecimal>();
		List<BigDecimal> bdTotalMinList = new ArrayList<BigDecimal>();
		List<Integer> intTotalCount = new ArrayList<Integer>();
		List<BigDecimal> bdWeekValueList = new ArrayList<BigDecimal>();
		int chIndex = 0;
		int cnt = 0;

		for (int k = 0; k < len; k++) {
			map = new HashMap<String, Object>();
			weekDatesMap = new HashMap<String, String>();
			startDate = null;
			endDate = null;
			curDate = null;
			weekDatesMap = weeksList.get(k);
			startDate = weekDatesMap.get("startDate");
			endDate = weekDatesMap.get("endDate");
			curDate = startDate;
			bdWeekValueList = new ArrayList<BigDecimal>();
			cnt = 0;

			for (int l = 0; l < 7; l++) { // 일주일 데이터를 sum
				chIndex = 0;

				for (Integer obj : channelIdList) {
					value = DecimalUtil.ConvertNumberToDouble(listMap.get(curDate + "_" + obj));

					if (l == 0) {
						if (value != null) {
							bdWeekValueList.add(new BigDecimal(value.toString()));
							cnt++;
						} else {
							bdWeekValueList.add(null);
						}
					} else {
						if (value != null) {
							if (bdWeekValueList.get(chIndex) == null) {
								bdWeekValueList.set(chIndex, new BigDecimal(value.toString()));
							} else {
								if (ChannelCalcMethod.MAX.name().equals((String) chMethodMap.get(obj.toString()))) { // MAX
									bdWeekValueList.set(chIndex,
											bdWeekValueList.get(chIndex).max(new BigDecimal(value.toString())));
								} else { // SUM, AVG
									bdWeekValueList.set(chIndex,
											bdWeekValueList.get(chIndex).add(new BigDecimal(value.toString())));
								}
							}
							cnt++;
						}
					}
					chIndex++;
				}

				if (curDate.equals(endDate)) {
					break;
				} else {
					curDate = CalendarUtil.getDate(curDate, Calendar.DAY_OF_MONTH, 1);
				}
			}

			chIndex = 0;

			for (Integer obj : channelIdList) {

				if (bdWeekValueList.get(chIndex) != null) {
					if (ChannelCalcMethod.AVG.name().equals((String) chMethodMap.get(obj.toString()))) { // AVG
						bdWeekValueList.set(chIndex,
								bdWeekValueList.get(chIndex).divide(new BigDecimal(cnt + ""), MathContext.DECIMAL32));
					}
				}

				if (k == 0) {
					if (bdWeekValueList.get(chIndex) != null) {
						bdTotalSumList.add(bdWeekValueList.get(chIndex));
						bdTotalMaxList.add(bdWeekValueList.get(chIndex));
						bdTotalMinList.add(bdWeekValueList.get(chIndex));
					} else {
						bdTotalSumList.add(null);
						bdTotalMaxList.add(null);
						bdTotalMinList.add(null);
					}
				} else {
					if (bdWeekValueList.get(chIndex) != null) {
						if (bdTotalSumList.get(chIndex) == null) {
							bdTotalSumList.set(chIndex, bdWeekValueList.get(chIndex));
						} else {
							bdTotalSumList.set(chIndex, bdTotalSumList.get(chIndex).add(bdWeekValueList.get(chIndex)));
						}

						if (bdTotalMaxList.get(chIndex) == null) {
							bdTotalMaxList.set(chIndex, bdWeekValueList.get(chIndex));
						} else {
							bdTotalMaxList.set(chIndex, bdTotalMaxList.get(chIndex).max(bdWeekValueList.get(chIndex)));
						}

						if (bdTotalMinList.get(chIndex) == null) {
							bdTotalMinList.set(chIndex, bdWeekValueList.get(chIndex));
						} else {
							bdTotalMinList.set(chIndex, bdTotalMinList.get(chIndex).min(bdWeekValueList.get(chIndex)));
						}
					}
				}

				if (k == 0) {
					if (bdWeekValueList.get(chIndex) != null) {
						intTotalCount.add(1);
					} else {
						intTotalCount.add(0);
					}
				} else {
					if (bdWeekValueList.get(chIndex) != null) {
						intTotalCount.set(chIndex, intTotalCount.get(chIndex) + 1);
					}
				}

				value = (bdWeekValueList.get(chIndex) == null) ? null : bdWeekValueList.get(chIndex).doubleValue();
				map.put("channel_" + obj, (value == null) ? "- " : mdf.format(value));
				chIndex++;
			}

			map.put("meteringTime", TimeLocaleUtil.getLocaleYearMonth(startDate.substring(0, 6), lang, country) + " "
					+ CalendarUtil.getWeekOfMonth(startDate) + "Week");
			result.add(map);
		}

		/*
		 * if (bdTotalSumList != null && bdTotalSumList.size() > 0) { map = new
		 * HashMap<String, Object>(); map.put("meteringTime",
		 * conditionMap.get("msgSum")); map.put("id", "sum");
		 * 
		 * chIndex = 0; for (Integer ch : channelIdList) { sumValue =
		 * (bdTotalSumList.get(chIndex) == null) ? null :
		 * bdTotalSumList.get(chIndex).doubleValue(); map.put("channel_" + ch, (sumValue
		 * == null) ? "- " : mdf.format(sumValue)); chIndex++; }
		 * 
		 * result.add(map);
		 * 
		 * map = new HashMap<String, Object>(); map.put("meteringTime",
		 * conditionMap.get("msgAvg") + "(" + conditionMap.get("msgMax") + "/" +
		 * conditionMap.get("msgMin") + ")"); map.put("id", "avg");
		 * 
		 * chIndex = 0; for (Integer ch : channelIdList) { sbAvgValue = new
		 * StringBuilder(); maxValue = (bdTotalMaxList.get(chIndex) == null) ? null :
		 * bdTotalMaxList.get(chIndex).doubleValue(); minValue =
		 * (bdTotalMinList.get(chIndex) == null) ? null :
		 * bdTotalMinList.get(chIndex).doubleValue();
		 * 
		 * if (bdTotalSumList.get(chIndex) == null || intTotalCount.get(chIndex) ==
		 * null) { avgValue = null; } else if (intTotalCount.get(chIndex) == 0) {
		 * avgValue = 0D; } else { avgValue = bdTotalSumList.get(chIndex).divide(new
		 * BigDecimal(intTotalCount.get(chIndex)), MathContext.DECIMAL32).doubleValue();
		 * }
		 * 
		 * if (maxValue != null || minValue != null || avgValue != null) {
		 * sbAvgValue.append((avgValue == null) ? "- " : mdf.format(avgValue));
		 * sbAvgValue.append("("); sbAvgValue.append((maxValue == null) ? " - " :
		 * mdf.format(maxValue)); sbAvgValue.append("/"); sbAvgValue.append((minValue ==
		 * null) ? " - " : mdf.format(minValue)); sbAvgValue.append(")"); }
		 * map.put("channel_" + ch, sbAvgValue.toString()); chIndex++; }
		 * result.add(map); }
		 */

		return result;
	}

	/**
	 * method name : getMeteringDataDetailWeeklyChartData<b/> method Desc : Metering
	 * Data 맥스가젯 상세화면에서 주별 검침 chart 데이터를 조회한다.
	 *
	 * @param conditionMap
	 * @return
	 */
	public Map<String, Object> getMeteringDataDetailWeeklyChartData(Map<String, Object> conditionMap) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Map<String, Object>> searchData = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> searchAddData = new ArrayList<Map<String, Object>>();
		Integer supplierId = (Integer) conditionMap.get("supplierId");
		String[] channelArray = ((String) conditionMap.get("channel")).split(",");
		List<Integer> channelIdList = new ArrayList<Integer>();

		for (String obj : channelArray) {
			channelIdList.add(Integer.parseInt(obj));
		}

		conditionMap.put("channelIdList", channelIdList);

		List<Map<String, Object>> list = meteringDayDao.getMeteringDataDetailDailyData(conditionMap, false);

		if (list == null || list.size() <= 0) {
			resultMap.put("searchData", searchData);
			resultMap.put("searchAddData", searchAddData);
			return resultMap;
		}

		Set<String> dateSet = new LinkedHashSet<String>();
		Map<String, Object> listMap = new HashMap<String, Object>();
		Map<String, Object> chMethodMap = new HashMap<String, Object>();
		Map<String, Object> map = null;
		Supplier supplier = supplierDao.get(supplierId);
		String country = supplier.getCountry().getCode_2letter();
		String lang = supplier.getLang().getCode_2letter();
		DecimalFormat mdf = DecimalUtil.getDecimalFormat(supplier.getMd());
		Double value = null; // 계산용 임시변수

		for (Map<String, Object> obj : list) {
			listMap.put((String) obj.get("YYYYMMDD") + "_" + (String) obj.get("CHANNEL"), obj.get("VALUE"));
			dateSet.add((String) obj.get("YYYYMMDD"));
			chMethodMap.put(obj.get("CHANNEL").toString(), obj.get("CH_METHOD"));
		}

		List<Map<String, String>> weeksList = new ArrayList<Map<String, String>>();
		String searchStartDate = (String) conditionMap.get("searchStartDate");
		String searchEndDate = (String) conditionMap.get("searchEndDate");

		String startMonth = searchStartDate.substring(0, 6);
		String endMonth = searchEndDate.substring(0, 6);
		String curMonth = null;
		Integer startWeek = CalendarUtil.getWeekOfMonth(searchStartDate);
		Integer endWeek = CalendarUtil.getWeekOfMonth(searchEndDate);
		int firstWeek = 1;
		int lastWeek = 0;

		if (startMonth.equals(endMonth)) { // 연월이 동일한 경우
			curMonth = startMonth;

			// 해당월의 각 주에 해당하는 시작일자 종료일자를 구한다.
			for (int i = startWeek; i <= endWeek; i++) {
				weeksList.add(
						CalendarUtil.getDateWeekOfMonth(curMonth.substring(0, 4), curMonth.substring(4, 6), i + ""));
			}
		} else { // 연월이 다를 경우
			for (int i = 0; i < 100; i++) { // 무한 loop 를 피하기 위해 for 문으로 loop 회수 제한
				if (i == 0) {
					curMonth = startMonth;
					firstWeek = startWeek;
				} else {
					curMonth = (CalendarUtil.getDate(curMonth + "01", Calendar.MONTH, 1)).substring(0, 6);
					firstWeek = 1;
				}

				if (curMonth.equals(endMonth)) {
					lastWeek = endWeek;
				} else {
					lastWeek = Integer.parseInt(
							CalendarUtil.getWeekCountOfMonth(curMonth.substring(0, 4), curMonth.substring(4)));
				}

				for (int j = firstWeek; j <= lastWeek; j++) {
					weeksList.add(
							CalendarUtil.getDateWeekOfMonth(curMonth.substring(0, 4), curMonth.substring(4), j + ""));
				}

				if (curMonth.equals(endMonth)) {
					break;
				}
			}
		}

		int len = weeksList.size();
		Map<String, String> weekDatesMap = new HashMap<String, String>();
		String startDate = null;
		String endDate = null;
		String curDate = null;

		BigDecimal bdTotalSum = new BigDecimal("0");
		BigDecimal bdTotalMax = new BigDecimal("0");
		BigDecimal bdTotalMin = new BigDecimal("0");
		Integer intTotalCount = new Integer("0");
		BigDecimal bdWeekValue = null;
		int cnt = 0;

		for (int k = 0; k < len; k++) {
			map = new HashMap<String, Object>();
			weekDatesMap = new HashMap<String, String>();
			startDate = null;
			endDate = null;
			curDate = null;
			weekDatesMap = weeksList.get(k);
			startDate = weekDatesMap.get("startDate");
			endDate = weekDatesMap.get("endDate");
			curDate = startDate;
			bdWeekValue = null;
			String chMethod = null;
			cnt = 0;

			for (Integer obj : channelIdList) {
				cnt = 0;
				curDate = startDate;
				chMethod = null;
				bdWeekValue = null;

				for (int l = 0; l < 7; l++) { // 일주일 데이터를 sum
					value = DecimalUtil.ConvertNumberToDouble(listMap.get(curDate + "_" + obj));

					if (l == 0) {
						if (value != null) {
							bdWeekValue = new BigDecimal(value.toString());
							cnt++;
						} else {
							bdWeekValue = null;
						}
					} else {
						if (value != null) {
							if (bdWeekValue == null) {
								bdWeekValue = new BigDecimal(value.toString());
							} else {
								bdWeekValue = bdWeekValue.add(new BigDecimal(value.toString()));
							}
							cnt++;
						}
					}

					if (curDate.equals(endDate)) {
						break;
					} else {
						curDate = CalendarUtil.getDate(curDate, Calendar.DAY_OF_MONTH, 1);
					}
				}

				if (cnt > 0) {
					if (bdWeekValue != null) {
						chMethod = (String) chMethodMap.get(obj.toString());

						if (ChannelCalcMethod.AVG.name().equals(chMethod)) {
							bdWeekValue = bdWeekValue.divide(new BigDecimal(cnt + ""), MathContext.DECIMAL32);
						}
					} else {
						bdWeekValue = new BigDecimal("0");
					}

					// 에너지사용량(channel=1)의 Sum/Avg/Max/Min 을 계산한다.
					if (obj.equals(1)) {
						bdTotalSum = bdTotalSum.add(bdWeekValue);
						bdTotalMax = bdTotalMax.max(bdWeekValue);
						bdTotalMin = bdTotalMin.min(bdWeekValue);
						intTotalCount++;
					}

					map = new HashMap<String, Object>();
					// ex) 2012.08 1 Week
					map.put("reportDate", TimeLocaleUtil.getLocaleYearMonth(startDate.substring(0, 6), lang, country)
							+ " " + CalendarUtil.getWeekOfMonth(startDate) + "Week");
					map.put("localeDate", TimeLocaleUtil.getLocaleYearMonth(startDate.substring(0, 6), lang, country)
							+ " " + CalendarUtil.getWeekOfMonth(startDate) + "Week");
					map.put("channel", obj);
					map.put("value", bdWeekValue.doubleValue());
					map.put("decimalValue", mdf.format(bdWeekValue.doubleValue()));
					searchData.add(map);
				} else {
					map = new HashMap<String, Object>();
					// ex) 2012.08 1 Week
					map.put("reportDate", TimeLocaleUtil.getLocaleYearMonth(startDate.substring(0, 6), lang, country)
							+ " " + CalendarUtil.getWeekOfMonth(startDate) + "Week");
					map.put("localeDate", TimeLocaleUtil.getLocaleYearMonth(startDate.substring(0, 6), lang, country)
							+ " " + CalendarUtil.getWeekOfMonth(startDate) + "Week");
					map.put("channel", obj);
					map.put("value", 0D);
					map.put("decimalValue", mdf.format(0D));
					searchData.add(map);
				}
			}
		}

		resultMap.put("searchData", searchData);

		/*
		 * if (intTotalCount > 0) { map = new HashMap<String, Object>();
		 * 
		 * map.put("sumValue", bdTotalSum.doubleValue()); map.put("avgValue",
		 * bdTotalSum.divide(new BigDecimal(intTotalCount+""),
		 * MathContext.DECIMAL32).doubleValue()); map.put("maxValue",
		 * bdTotalMax.doubleValue()); map.put("minValue", bdTotalMin.doubleValue());
		 * 
		 * map.put("sumDecimalValue", mdf.format(bdTotalSum.doubleValue()));
		 * map.put("avgDecimalValue", mdf.format(bdTotalSum.divide(new
		 * BigDecimal(intTotalCount+""), MathContext.DECIMAL32).doubleValue()));
		 * map.put("maxDecimalValue", mdf.format(bdTotalMax.doubleValue()));
		 * map.put("minDecimalValue", mdf.format(bdTotalMin.doubleValue()));
		 * 
		 * searchAddData.add(map); }
		 */
		resultMap.put("searchAddData", searchAddData);

		return resultMap;
	}

	/**
	 * method name : getMeteringDataDetailMonthlyData<b/> method Desc : Metering
	 * Data 맥스가젯 상세화면에서 월별 검침데이터를 조회한다.
	 *
	 * @param conditionMap
	 * @return
	 */
	public List<Map<String, Object>> getMeteringDataDetailMonthlyData(Map<String, Object> conditionMap) {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		Integer supplierId = (Integer) conditionMap.get("supplierId");
		String searchStartDate = (String) conditionMap.get("searchStartDate");
		String[] channelArray = ((String) conditionMap.get("channel")).split(",");
		List<Integer> channelIdList = new ArrayList<Integer>();

		for (String obj : channelArray) {
			channelIdList.add(Integer.parseInt(obj));
		}

		conditionMap.put("channelIdList", channelIdList);

		List<Map<String, Object>> list = meteringMonthDao.getMeteringDataDetailMonthlyData(conditionMap, false);
		Map<String, Object> listMap = new HashMap<String, Object>();
		Map<String, Object> sumListMap = new HashMap<String, Object>();
		Set<String> dateSet = new LinkedHashSet<String>();
		Map<String, Object> map = null;
		Supplier supplier = supplierDao.get(supplierId);
		String country = supplier.getCountry().getCode_2letter();
		String lang = supplier.getLang().getCode_2letter();
		DecimalFormat mdf = DecimalUtil.getDecimalFormat(supplier.getMd());
		Double value = null;
		String startYear = searchStartDate.substring(0, 4);

		for (int i = 1; i <= 12; i++) {
			dateSet.add(startYear + CalendarUtil.to2Digit(i));
		}

		for (Map<String, Object> obj : list) {
			listMap.put((String) obj.get("YYYYMM") + "_" + (String) obj.get("CHANNEL"), obj.get("VALUE"));
		}

		Iterator<String> itr = dateSet.iterator();
		String date = null;

		while (itr.hasNext()) {
			date = itr.next();
			map = new HashMap<String, Object>();
			map.put("meteringTime", TimeLocaleUtil.getLocaleYearMonth(date, lang, country));

			for (Integer obj : channelIdList) {
				value = DecimalUtil.ConvertNumberToDouble(listMap.get(date + "_" + obj));
				map.put("channel_" + obj, (value == null) ? "- " : mdf.format(value));
			}

			result.add(map);
		}
		return result;
	}

	/**
	 * method name : getMeteringDataDetailMonthlyChartData<b/> method Desc :
	 * Metering Data 맥스가젯 상세화면에서 월별 Chart 데이터를 조회한다.
	 *
	 * @param conditionMap
	 * @return
	 */
	public Map<String, Object> getMeteringDataDetailMonthlyChartData(Map<String, Object> conditionMap) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Map<String, Object>> searchData = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> searchAddData = new ArrayList<Map<String, Object>>();
		Integer supplierId = (Integer) conditionMap.get("supplierId");
		String searchStartDate = (String) conditionMap.get("searchStartDate");
		String[] channelArray = ((String) conditionMap.get("channel")).split(",");
		List<Integer> channelIdList = new ArrayList<Integer>();

		for (String obj : channelArray) {
			channelIdList.add(Integer.parseInt(obj));
		}

		conditionMap.put("channelIdList", channelIdList);
		List<Map<String, Object>> list = meteringMonthDao.getMeteringDataDetailMonthlyData(conditionMap, false);

		if (list == null || list.size() <= 0) {
			resultMap.put("searchData", searchData);
			resultMap.put("searchAddData", searchAddData);
			return resultMap;
		}

		Map<String, Double> listMap = new HashMap<String, Double>();
		Set<String> dateSet = new LinkedHashSet<String>();
		Map<String, Object> map = null;
		Supplier supplier = supplierDao.get(supplierId);
		String country = supplier.getCountry().getCode_2letter();
		String lang = supplier.getLang().getCode_2letter();
		DecimalFormat mdf = DecimalUtil.getDecimalFormat(supplier.getMd());
		Double tmpValue = null;
		Double sumValue = null;
		Double avgValue = null;
		Double maxValue = null;
		Double minValue = null;
		String startYear = searchStartDate.substring(0, 4);

		for (int i = 1; i <= 12; i++) {
			dateSet.add(startYear + CalendarUtil.to2Digit(i));
		}

		for (Map<String, Object> obj : list) {
			listMap.put((String) obj.get("YYYYMM") + "_" + obj.get("CHANNEL"),
					DecimalUtil.ConvertNumberToDouble(obj.get("VALUE")));
		}

		for (String date : dateSet) {
			for (Integer ch : channelIdList) {
				map = new HashMap<String, Object>();
				map.put("reportDate", TimeLocaleUtil.getLocaleYearMonth(date, lang, country));
				map.put("localeDate", Integer.valueOf(date.substring(4, 6)));
				map.put("channel", ch);
				tmpValue = listMap.get(date + "_" + ch);
				map.put("value", (tmpValue == null) ? 0D : tmpValue.doubleValue());
				map.put("decimalValue", (tmpValue == null) ? mdf.format(0D) : mdf.format(tmpValue.doubleValue()));
				searchData.add(map);
			}
		}

		resultMap.put("searchData", searchData);

		List<Map<String, Object>> sumList = meteringMonthDao.getMeteringDataDetailMonthlyData(conditionMap, true);

		/*
		 * if (sumList != null && sumList.size() > 0) { for (Map<String, Object> obj :
		 * sumList) { if
		 * (DecimalUtil.ConvertNumberToInteger(obj.get("CHANNEL")).equals(1)) { map =
		 * new HashMap<String, Object>();
		 * 
		 * sumValue = DecimalUtil.ConvertNumberToDouble(obj.get("SUM_VAL")); avgValue =
		 * DecimalUtil.ConvertNumberToDouble(obj.get("AVG_VAL")); maxValue =
		 * DecimalUtil.ConvertNumberToDouble(obj.get("MAX_VAL")); minValue =
		 * DecimalUtil.ConvertNumberToDouble(obj.get("MIN_VAL"));
		 * 
		 * map.put("sumValue", (sumValue == null) ? 0D : sumValue); map.put("avgValue",
		 * (avgValue == null) ? 0D : avgValue); map.put("maxValue", (maxValue == null) ?
		 * 0D : maxValue); map.put("minValue", (minValue == null) ? 0D : minValue);
		 * 
		 * map.put("sumDecimalValue", (sumValue == null) ? mdf.format(0D) :
		 * mdf.format(sumValue)); map.put("avgDecimalValue", (avgValue == null) ?
		 * mdf.format(0D) : mdf.format(avgValue)); map.put("maxDecimalValue", (maxValue
		 * == null) ? mdf.format(0D) : mdf.format(maxValue)); map.put("minDecimalValue",
		 * (minValue == null) ? mdf.format(0D) : mdf.format(minValue));
		 * 
		 * searchAddData.add(map); break; } } }
		 */

		resultMap.put("searchAddData", searchAddData);

		return resultMap;
	}

	/**
	 * method name : getMeteringDataDetailWeekDailyData<b/> method Desc : Metering
	 * Data 맥스가젯 상세화면에서 요일별 검침데이터를 조회한다.
	 *
	 * @param conditionMap
	 * @return
	 */
	public List<Map<String, Object>> getMeteringDataDetailWeekDailyData(Map<String, Object> conditionMap) {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		Integer supplierId = (Integer) conditionMap.get("supplierId");
		String searchStartDate = (String) conditionMap.get("searchStartDate");
		String[] channelArray = ((String) conditionMap.get("channel")).split(",");
		List<Integer> channelIdList = new ArrayList<Integer>();

		for (String obj : channelArray) {
			channelIdList.add(Integer.parseInt(obj));
		}

		conditionMap.put("channelIdList", channelIdList);

		List<Map<String, Object>> list = meteringDayDao.getMeteringDataDetailDailyData(conditionMap, false);
		Map<String, Object> listMap = new HashMap<String, Object>();
		Set<String> dateSet = new LinkedHashSet<String>();
		Map<String, Object> map = null;
		Supplier supplier = supplierDao.get(supplierId);
		String country = supplier.getCountry().getCode_2letter();
		String lang = supplier.getLang().getCode_2letter();
		DecimalFormat mdf = DecimalUtil.getDecimalFormat(supplier.getMd());
		Double value = null;

		int year = 0;
		int month = 0;
		int day = 0;
		Calendar cal = null;

		year = Integer.parseInt(searchStartDate.substring(0, 4));
		month = Integer.parseInt(searchStartDate.substring(4, 6)) - 1;
		day = 1;
		cal = Calendar.getInstance();
		cal.set(year, month, day);
		String startMonth = searchStartDate.substring(0, 6);

		int lastDate = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

		for (int i = 1; i <= lastDate; i++) {
			dateSet.add(startMonth + CalendarUtil.to2Digit(i));
		}

		for (Map<String, Object> obj : list) {
			listMap.put((String) obj.get("YYYYMMDD") + "_" + (String) obj.get("CHANNEL"), obj.get("VALUE"));
		}

		Iterator<String> itr = dateSet.iterator();
		String date = null;

		while (itr.hasNext()) {
			date = itr.next();
			map = new HashMap<String, Object>();

			map.put("meteringTime", TimeLocaleUtil.getLocaleWeekDay(date, lang, country));

			for (Integer obj : channelIdList) {
				value = DecimalUtil.ConvertNumberToDouble(listMap.get(date + "_" + obj));
				map.put("channel_" + obj, (value == null) ? "- " : mdf.format(value));
			}

			result.add(map);
		}

//		List<Map<String, Object>> sumList = meteringDayDao.getMeteringDataDetailDailyData(conditionMap, true);

		/*
		 * if (sumList != null && sumList.size() > 0) { for (Map<String, Object> obj :
		 * sumList) { sumListMap.put("MAX_" + (Number)obj.get("CHANNEL"),
		 * obj.get("MAX_VAL")); sumListMap.put("MIN_" + (Number)obj.get("CHANNEL"),
		 * obj.get("MIN_VAL")); sumListMap.put("AVG_" + (Number)obj.get("CHANNEL"),
		 * obj.get("AVG_VAL")); sumListMap.put("SUM_" + (Number)obj.get("CHANNEL"),
		 * obj.get("SUM_VAL")); }
		 * 
		 * map = new HashMap<String, Object>(); map.put("meteringTime",
		 * conditionMap.get("msgSum")); map.put("id", "sum");
		 * 
		 * for (Integer ch : channelIdList) { sumValue =
		 * DecimalUtil.ConvertNumberToDouble(sumListMap.get("SUM_" + ch));
		 * map.put("channel_" + ch, (sumValue == null) ? "- " : mdf.format(sumValue)); }
		 * 
		 * result.add(map);
		 * 
		 * map = new HashMap<String, Object>(); map.put("meteringTime",
		 * conditionMap.get("msgAvg") + "(" + conditionMap.get("msgMax") + "/" +
		 * conditionMap.get("msgMin") + ")"); map.put("id", "avg");
		 * 
		 * for (Integer ch : channelIdList) { sbAvgValue = new StringBuilder(); maxValue
		 * = DecimalUtil.ConvertNumberToDouble(sumListMap.get("MAX_" + ch)); minValue =
		 * DecimalUtil.ConvertNumberToDouble(sumListMap.get("MIN_" + ch)); avgValue =
		 * DecimalUtil.ConvertNumberToDouble(sumListMap.get("AVG_" + ch));
		 * 
		 * if (maxValue != null || minValue != null || avgValue != null) {
		 * sbAvgValue.append((avgValue == null) ? "- " : mdf.format(avgValue));
		 * sbAvgValue.append("("); sbAvgValue.append((maxValue == null) ? " - " :
		 * mdf.format(maxValue)); sbAvgValue.append("/"); sbAvgValue.append((minValue ==
		 * null) ? " - " : mdf.format(minValue)); sbAvgValue.append(")"); }
		 * map.put("channel_" + ch, sbAvgValue.toString()); }
		 * 
		 * result.add(map); }
		 */
		return result;
	}

	/**
	 * method name : getMeteringDataDetailWeekDailyChartData<b/> method Desc :
	 * Metering Data 맥스가젯 상세화면에서 요일별 Chart 데이터를 조회한다.
	 *
	 * @param conditionMap
	 * @return
	 */
	public Map<String, Object> getMeteringDataDetailWeekDailyChartData(Map<String, Object> conditionMap) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Map<String, Object>> searchData = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> searchAddData = new ArrayList<Map<String, Object>>();
		String searchStartDate = (String) conditionMap.get("searchStartDate");
		Integer supplierId = (Integer) conditionMap.get("supplierId");
		String[] channelArray = ((String) conditionMap.get("channel")).split(",");
		List<Integer> channelIdList = new ArrayList<Integer>();

		for (String obj : channelArray) {
			channelIdList.add(Integer.parseInt(obj));
		}

		conditionMap.put("channelIdList", channelIdList);

		List<Map<String, Object>> list = meteringDayDao.getMeteringDataDetailDailyData(conditionMap, false);

		if (list == null || list.size() <= 0) {
			resultMap.put("searchData", searchData);
			resultMap.put("searchAddData", searchAddData);
			return resultMap;
		}

		Map<String, Double> listMap = new HashMap<String, Double>();
//		Map<String, Object> sumListMap = new HashMap<String, Object>();
		Set<String> dateSet = new LinkedHashSet<String>();
		Map<String, Object> map = null;
		Supplier supplier = supplierDao.get(supplierId);
		String country = supplier.getCountry().getCode_2letter();
		String lang = supplier.getLang().getCode_2letter();
		DecimalFormat mdf = DecimalUtil.getDecimalFormat(supplier.getMd());
		Double value = null;
//		Double maxValue = null;
//		Double minValue = null;
//		Double avgValue = null;
//		Double sumValue = null;

		int year = 0;
		int month = 0;
		int day = 0;
		Calendar cal = null;

		year = Integer.parseInt(searchStartDate.substring(0, 4));
		month = Integer.parseInt(searchStartDate.substring(4, 6)) - 1;
		day = 1;
		cal = Calendar.getInstance();
		cal.set(year, month, day);
		String startMonth = searchStartDate.substring(0, 6);

		int lastDate = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

		for (int i = 1; i <= lastDate; i++) {
			dateSet.add(startMonth + CalendarUtil.to2Digit(i));
		}

		for (Map<String, Object> obj : list) {
			listMap.put((String) obj.get("YYYYMMDD") + "_" + obj.get("CHANNEL"),
					DecimalUtil.ConvertNumberToDouble(obj.get("VALUE")));
		}

		Iterator<String> itr = dateSet.iterator();
		String date = null;
		String localeDate = null;
		String localeDay = null;

		while (itr.hasNext()) {
			date = itr.next();

			for (Integer ch : channelIdList) {
				map = new HashMap<String, Object>();
				value = StringUtil.nullToDoubleZero(listMap.get(date + "_" + ch));

				localeDate = TimeLocaleUtil.getLocaleDate(date, lang, country);
				// localeDate = TimeLocaleUtil.getLocaleDay(localeDate, 8, lang, country);
				localeDay = TimeLocaleUtil.getLocaleWeekDayOnly(date, lang, country);

				map.put("reportDate", localeDate + " " + localeDay);
				map.put("localeDate", localeDate + " " + localeDay);
				map.put("channel", ch);
				map.put("value", value);
				map.put("decimalValue", mdf.format(value));
				searchData.add(map);
			}
		}
		resultMap.put("searchData", searchData);

//		List<Map<String, Object>> sumList = meteringDayDao.getMeteringDataDetailDailyData(conditionMap, true);

		/*
		 * if (sumList != null && sumList.size() > 0) { for (Map<String, Object> obj :
		 * sumList) { sumListMap.put("MAX_" + (Number)obj.get("CHANNEL"),
		 * obj.get("MAX_VAL")); sumListMap.put("MIN_" + (Number)obj.get("CHANNEL"),
		 * obj.get("MIN_VAL")); sumListMap.put("AVG_" + (Number)obj.get("CHANNEL"),
		 * obj.get("AVG_VAL")); sumListMap.put("SUM_" + (Number)obj.get("CHANNEL"),
		 * obj.get("SUM_VAL")); }
		 * 
		 * map = new HashMap<String, Object>();
		 * 
		 * // 에너지사용량(channel=1)의 Sum/Avg/Max/Min 을 가져온다. sumValue =
		 * DecimalUtil.ConvertNumberToDouble(sumListMap.get("SUM_1")); avgValue =
		 * DecimalUtil.ConvertNumberToDouble(sumListMap.get("AVG_1")); maxValue =
		 * DecimalUtil.ConvertNumberToDouble(sumListMap.get("MAX_1")); minValue =
		 * DecimalUtil.ConvertNumberToDouble(sumListMap.get("MIN_1"));
		 * 
		 * map.put("sumValue", sumValue); map.put("avgValue", avgValue);
		 * map.put("maxValue", maxValue); map.put("minValue", minValue);
		 * 
		 * map.put("sumDecimalValue", mdf.format(sumValue)); map.put("avgDecimalValue",
		 * mdf.format(avgValue)); map.put("maxDecimalValue", mdf.format(maxValue));
		 * map.put("minDecimalValue", mdf.format(minValue));
		 * 
		 * searchAddData.add(map); }
		 */

		resultMap.put("searchAddData", searchAddData);

		return resultMap;
	}

	/**
	 * method name : getMeteringDataDetailSeasonalData<b/> method Desc : Metering
	 * Data 맥스가젯 상세화면에서 계절별 검침데이터를 조회한다.
	 *
	 * @param conditionMap
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getMeteringDataDetailSeasonalData(Map<String, Object> conditionMap) {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> list = null;
		Integer supplierId = (Integer) conditionMap.get("supplierId");
		String[] channelArray = ((String) conditionMap.get("channel")).split(",");
		List<Integer> channelIdList = new ArrayList<Integer>();

		for (String obj : channelArray) {
			channelIdList.add(Integer.parseInt(obj));
		}

		conditionMap.put("channelIdList", channelIdList);

		Set<String> dateSet = new LinkedHashSet<String>();
		Map<String, Object> listMap = new HashMap<String, Object>();
		Map<String, Object> chMethodMap = new HashMap<String, Object>();
		Map<String, Object> map = null;
		Supplier supplier = supplierDao.get(supplierId);
		DecimalFormat mdf = DecimalUtil.getDecimalFormat(supplier.getMd());
		Double value = null; // 계산용 임시변수

		Map<String, Object> seasonsListMap = null;
		List<SeasonData> seasonDataList = null;
		String searchStartDate = (String) conditionMap.get("searchStartDate");
		String searchEndDate = (String) conditionMap.get("searchEndDate");
		boolean hasDay = false;
		int sumLen = 0; // 계절별 sum 할때 loop 회수 제한

		seasonsListMap = seasonManager.getSeasonDataListByDates(searchStartDate, searchEndDate);
		hasDay = (Boolean) seasonsListMap.get("hasDay");
		seasonDataList = (List<SeasonData>) seasonsListMap.get("seasonDataList");

		if (hasDay) {
			list = meteringDayDao.getMeteringDataDetailDailyData(conditionMap, false);
			sumLen = 124; // 일별 데이터로 sum 할때 loop 제한
		} else {
			list = meteringMonthDao.getMeteringDataDetailMonthlyData(conditionMap, false);
			sumLen = 4; // 월별 데이터로 sum 할때 loop 제한
		}

		if (list == null || list.size() <= 0) {
			return result;
		}

		for (Map<String, Object> obj : list) {
			if (hasDay) {
				listMap.put((String) obj.get("YYYYMMDD") + "_" + (String) obj.get("CHANNEL"), obj.get("VALUE"));
				dateSet.add((String) obj.get("YYYYMMDD"));
			} else {
				listMap.put((String) obj.get("YYYYMM") + "_" + (String) obj.get("CHANNEL"), obj.get("VALUE"));
				dateSet.add((String) obj.get("YYYYMM"));
			}
			chMethodMap.put(obj.get("CHANNEL").toString(), obj.get("CH_METHOD"));
		}

		int len = seasonDataList.size();
		String startDate = null; // season 기간 시작일자. 월별일 경우 yyyyMM, 일별일 경우 yyyyMMdd
		String endDate = null; // season 기간 종료일자. 월별일 경우 yyyyMM, 일별일 경우 yyyyMMdd
		String curDate = null;
		List<BigDecimal> bdTotalSumList = new ArrayList<BigDecimal>();
		List<BigDecimal> bdTotalMaxList = new ArrayList<BigDecimal>();
		List<BigDecimal> bdTotalMinList = new ArrayList<BigDecimal>();
		List<Integer> intTotalCount = new ArrayList<Integer>();
		List<BigDecimal> bdSeasonValueList = new ArrayList<BigDecimal>();
		int chIndex = 0;
		int cnt = 0;
		SeasonData seasonData = null;

		for (int k = 0; k < len; k++) {
			seasonData = seasonDataList.get(k);
			map = new HashMap<String, Object>();
			startDate = null;
			endDate = null;
			curDate = null;
			cnt = 0;

			if (hasDay) {
				startDate = seasonData.getStartDate();
				endDate = seasonData.getEndDate();
			} else {
				startDate = seasonData.getStartDate().substring(0, 6);
				endDate = seasonData.getEndDate().substring(0, 6);
			}
			curDate = startDate;
			bdSeasonValueList = new ArrayList<BigDecimal>();

			for (int l = 0; l < sumLen; l++) { // 계절 단위로 sum
				chIndex = 0;

				for (Integer obj : channelIdList) { // 각 채널별 sum
					value = DecimalUtil.ConvertNumberToDouble(listMap.get(curDate + "_" + obj));

					if (l == 0) {
						if (value != null) {
							bdSeasonValueList.add(new BigDecimal(value.toString()));
							cnt++;
						} else {
							bdSeasonValueList.add(null);
						}
					} else {
						if (value != null) {
							if (bdSeasonValueList.get(chIndex) == null) {
								bdSeasonValueList.set(chIndex, new BigDecimal(value.toString()));
							} else {
								if (ChannelCalcMethod.MAX.name().equals((String) chMethodMap.get(obj.toString()))) { // MAX
									bdSeasonValueList.set(chIndex,
											bdSeasonValueList.get(chIndex).max(new BigDecimal(value.toString())));
								} else { // SUM, AVG
									bdSeasonValueList.set(chIndex,
											bdSeasonValueList.get(chIndex).add(new BigDecimal(value.toString())));
								}
							}
							cnt++;
						}
					}
					chIndex++;
				}

				if (curDate.equals(endDate)) {
					break;
				} else {
					if (hasDay) {
						curDate = CalendarUtil.getDate(curDate, Calendar.DAY_OF_MONTH, 1);
					} else {
						curDate = CalendarUtil.getDate(curDate + "01", Calendar.MONTH, 1).substring(0, 6);
					}
				}
			}

			chIndex = 0;

			for (Integer obj : channelIdList) {

				if (bdSeasonValueList.get(chIndex) != null) {
					if (ChannelCalcMethod.AVG.name().equals((String) chMethodMap.get(obj.toString()))) { // AVG
						bdSeasonValueList.set(chIndex,
								bdSeasonValueList.get(chIndex).divide(new BigDecimal(cnt + ""), MathContext.DECIMAL32));
					}
				}

				if (k == 0) {
					if (bdSeasonValueList.get(chIndex) != null) {
						bdTotalSumList.add(bdSeasonValueList.get(chIndex));
						bdTotalMaxList.add(bdSeasonValueList.get(chIndex));
						bdTotalMinList.add(bdSeasonValueList.get(chIndex));
						intTotalCount.add(1);
					} else {
						bdTotalSumList.add(null);
						bdTotalMaxList.add(null);
						bdTotalMinList.add(null);
						intTotalCount.add(0);
					}
				} else {
					if (bdSeasonValueList.get(chIndex) != null) {
						if (bdTotalSumList.get(chIndex) == null) {
							bdTotalSumList.set(chIndex, bdSeasonValueList.get(chIndex));
						} else {
							bdTotalSumList.set(chIndex,
									bdTotalSumList.get(chIndex).add(bdSeasonValueList.get(chIndex)));
						}

						if (bdTotalMaxList.get(chIndex) == null) {
							bdTotalMaxList.set(chIndex, bdSeasonValueList.get(chIndex));
						} else {
							bdTotalMaxList.set(chIndex,
									bdTotalMaxList.get(chIndex).max(bdSeasonValueList.get(chIndex)));
						}

						if (bdTotalMinList.get(chIndex) == null) {
							bdTotalMinList.set(chIndex, bdSeasonValueList.get(chIndex));
						} else {
							bdTotalMinList.set(chIndex,
									bdTotalMinList.get(chIndex).min(bdSeasonValueList.get(chIndex)));
						}
						intTotalCount.set(chIndex, intTotalCount.get(chIndex) + 1);
					}
				}

				value = (bdSeasonValueList.get(chIndex) == null) ? null : bdSeasonValueList.get(chIndex).doubleValue();
				map.put("channel_" + obj, (value == null) ? "- " : mdf.format(value));
				chIndex++;
			}

			map.put("meteringTime", startDate.substring(0, 4) + " " + seasonData.getName());
			result.add(map);
		}

		return result;
	}

	/**
	 * method name : getMeteringDataDetailSeasonalChartData<b/> method Desc :
	 * Metering Data 맥스가젯 상세화면에서 계절별 chart 데이터를 조회한다.
	 *
	 * @param conditionMap
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getMeteringDataDetailSeasonalChartData(Map<String, Object> conditionMap) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Map<String, Object>> searchData = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> searchAddData = new ArrayList<Map<String, Object>>();

		List<Map<String, Object>> list = null;
		Integer supplierId = (Integer) conditionMap.get("supplierId");
		String[] channelArray = ((String) conditionMap.get("channel")).split(",");
		List<Integer> channelIdList = new ArrayList<Integer>();

		for (String obj : channelArray) {
			channelIdList.add(Integer.parseInt(obj));
		}

		conditionMap.put("channelIdList", channelIdList);

		Set<String> dateSet = new LinkedHashSet<String>();
		Map<String, Object> listMap = new HashMap<String, Object>();
		Map<String, Object> chMethodMap = new HashMap<String, Object>();
		Map<String, Object> map = null;
		Supplier supplier = supplierDao.get(supplierId);
		DecimalFormat mdf = DecimalUtil.getDecimalFormat(supplier.getMd());
		Double value = null; // 계산용 임시변수
		Map<String, Object> seasonsListMap = null;
		List<SeasonData> seasonDataList = null;
		String searchStartDate = (String) conditionMap.get("searchStartDate");
		String searchEndDate = (String) conditionMap.get("searchEndDate");
		boolean hasDay = false;
		int sumLen = 0; // 계절별 sum 할때 loop 회수 제한

		seasonsListMap = seasonManager.getSeasonDataListByDates(searchStartDate, searchEndDate);
		hasDay = (Boolean) seasonsListMap.get("hasDay");
		seasonDataList = (List<SeasonData>) seasonsListMap.get("seasonDataList");

		if (hasDay) {
			list = meteringDayDao.getMeteringDataDetailDailyData(conditionMap, false);
			sumLen = 124; // 일별 데이터로 sum 할때 loop 제한
		} else {
			list = meteringMonthDao.getMeteringDataDetailMonthlyData(conditionMap, false);
			sumLen = 4; // 월별 데이터로 sum 할때 loop 제한
		}

		if (list == null || list.size() <= 0) {
			return resultMap;
		}

		for (Map<String, Object> obj : list) {
			if (hasDay) {
				listMap.put((String) obj.get("YYYYMMDD") + "_" + (String) obj.get("CHANNEL"), obj.get("VALUE"));
				dateSet.add((String) obj.get("YYYYMMDD"));
			} else {
				listMap.put((String) obj.get("YYYYMM") + "_" + (String) obj.get("CHANNEL"), obj.get("VALUE"));
				dateSet.add((String) obj.get("YYYYMM"));
			}
			chMethodMap.put(obj.get("CHANNEL").toString(), obj.get("CH_METHOD"));
		}

		int len = seasonDataList.size();
		String startDate = null; // season 기간 시작일자. 월별일 경우 yyyyMM, 일별일 경우 yyyyMMdd
		String endDate = null; // season 기간 종료일자. 월별일 경우 yyyyMM, 일별일 경우 yyyyMMdd
		String curDate = null;
		BigDecimal bdTotalSum = new BigDecimal("0");
		BigDecimal bdTotalMax = new BigDecimal("0");
		BigDecimal bdTotalMin = new BigDecimal("0");
		Integer intTotalCount = new Integer("0");
		BigDecimal bdSeasonValue = null;
		int cnt = 0;
		SeasonData seasonData = null;
		String chMethod = null;

		for (int k = 0; k < len; k++) {
			seasonData = seasonDataList.get(k);
			map = new HashMap<String, Object>();
			startDate = null;
			endDate = null;
			curDate = null;
			cnt = 0;

			if (hasDay) {
				startDate = seasonData.getStartDate();
				endDate = seasonData.getEndDate();
			} else {
				startDate = seasonData.getStartDate().substring(0, 6);
				endDate = seasonData.getEndDate().substring(0, 6);
			}

			for (Integer obj : channelIdList) { // 각 채널별 sum
				cnt = 0;
				curDate = startDate;
				chMethod = null;
				bdSeasonValue = null;

				for (int l = 0; l < sumLen; l++) { // 계절 단위로 sum
					value = DecimalUtil.ConvertNumberToDouble(listMap.get(curDate + "_" + obj));

					if (l == 0) {
						if (value != null) {
							bdSeasonValue = new BigDecimal(value.toString());
							cnt++;
						} else {
							bdSeasonValue = null;
						}
					} else {
						if (value != null) {
							if (bdSeasonValue == null) {
								bdSeasonValue = new BigDecimal(value.toString());
							} else {
								bdSeasonValue = bdSeasonValue.add(new BigDecimal(value.toString()));
							}
							cnt++;
						}
					}

					if (curDate.equals(endDate)) {
						break;
					} else {
						if (hasDay) {
							curDate = CalendarUtil.getDate(curDate, Calendar.DAY_OF_MONTH, 1);
						} else {
							curDate = CalendarUtil.getDate(curDate + "01", Calendar.MONTH, 1).substring(0, 6);
						}
					}
				} // for (int l = 0; l < sumLen; l++)

				if (cnt > 0) {
					if (bdSeasonValue != null) {
						chMethod = (String) chMethodMap.get(obj.toString());

						if (chMethod != null && chMethod.equals(ChannelCalcMethod.AVG.name())) {
							bdSeasonValue = bdSeasonValue.divide(new BigDecimal(cnt + ""), MathContext.DECIMAL32);
						}
					} else {
						bdSeasonValue = new BigDecimal("0");
					}

					// 에너지사용량(channel=1)의 Sum/Avg/Max/Min 을 계산한다.
					if (obj.equals(1)) {
						bdTotalSum = bdTotalSum.add(bdSeasonValue);
						bdTotalMax = bdTotalMax.max(bdSeasonValue);
						bdTotalMin = bdTotalMin.min(bdSeasonValue);
						intTotalCount++;
					}

					map = new HashMap<String, Object>();
					// ex) 2012 Spring
					map.put("reportDate", startDate.substring(0, 4) + " " + seasonData.getName());
					map.put("localeDate", startDate.substring(0, 4) + " " + seasonData.getName());
					map.put("channel", obj);
					map.put("value", bdSeasonValue.doubleValue());
					map.put("decimalValue", mdf.format(bdSeasonValue.doubleValue()));
					searchData.add(map);
				} else {
					map = new HashMap<String, Object>();
					// ex) 2012 Spring
					map.put("reportDate", startDate.substring(0, 4) + " " + seasonData.getName());
					map.put("localeDate", startDate.substring(0, 4) + " " + seasonData.getName());
					map.put("channel", obj);
					map.put("value", 0D);
					map.put("decimalValue", mdf.format(0D));
					searchData.add(map);
				}
			}
		}

		resultMap.put("searchData", searchData);

		/*
		 * if (intTotalCount > 0) { map = new HashMap<String, Object>();
		 * 
		 * map.put("sumValue", bdTotalSum.doubleValue()); map.put("avgValue",
		 * bdTotalSum.divide(new BigDecimal(intTotalCount.toString()),
		 * MathContext.DECIMAL32).doubleValue()); map.put("maxValue",
		 * bdTotalMax.doubleValue()); map.put("minValue", bdTotalMin.doubleValue());
		 * 
		 * map.put("sumDecimalValue", mdf.format(bdTotalSum.doubleValue()));
		 * map.put("avgDecimalValue", mdf.format(bdTotalSum.divide(new
		 * BigDecimal(intTotalCount.toString()), MathContext.DECIMAL32).doubleValue()));
		 * map.put("maxDecimalValue", mdf.format(bdTotalMax.doubleValue()));
		 * map.put("minDecimalValue", mdf.format(bdTotalMin.doubleValue()));
		 * 
		 * searchAddData.add(map); }
		 */
		resultMap.put("searchAddData", searchAddData);

		return resultMap;
	}

	/**
	 * method name : getMeteringDataDetailRatelyData<b/> method Desc : Metering Data
	 * 맥스가젯 상세화면에서 Rate 별 검침데이터를 조회한다.
	 *
	 * # MeteringData Gadget - Detail - Rate tab (dayType,startTime,endTime) #
	 * dayType : weekday(Monday ~ Friday)/weekend(Saturday ~ Sunday) # startTime 이
	 * endTime 보다 크면 사용량 시간범위는 0 ~ endTime, startTime ~ 23 가 된다.
	 * 
	 * @param conditionMap
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getMeteringDataDetailRatelyData(Map<String, Object> conditionMap) {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		String searchStartDate = (String) conditionMap.get("searchStartDate");
		String searchEndDate = (String) conditionMap.get("searchEndDate");
		Integer supplierId = (Integer) conditionMap.get("supplierId");
		String[] rate1Array = ((String) conditionMap.get("rate1")).split(","); // rate1=weekday,9,21
		String[] rate2Array = ((String) conditionMap.get("rate2")).split(","); // rate2=weekday,22,8
		String[] rate3Array = ((String) conditionMap.get("rate3")).split(","); // rate3=weekend,0,23
		List<Map<String, Object>> list = meteringDayDao.getMeteringDataDetailRatelyData(conditionMap);

		if (list == null || list.size() <= 0) {
			return result;
		}

		Map<String, Object> map = null;
		Map<String, Object> objMap = null;
		Map<String, Object> listMap = new HashMap<String, Object>();
		Set<String> dateSet = new LinkedHashSet<String>();
		Supplier supplier = supplierDao.get(supplierId);
		String country = supplier.getCountry().getCode_2letter();
		String lang = supplier.getLang().getCode_2letter();
		DecimalFormat mdf = DecimalUtil.getDecimalFormat(supplier.getMd());
		Number nvalue = null;
		Double avgValue = null;
		StringBuilder sbAvgValue = null;

		List<BigDecimal> bdTotalSumList = new ArrayList<BigDecimal>();
		List<BigDecimal> bdTotalMaxList = new ArrayList<BigDecimal>();
		List<BigDecimal> bdTotalMinList = new ArrayList<BigDecimal>();
		List<Integer> intTotalCount = new ArrayList<Integer>();
		BigDecimal bdRateValue = null;

		List<String> rateDateTypeList = new ArrayList<String>();
		List<Integer> rateStartTimeList = new ArrayList<Integer>();
		List<Integer> rateEndTimeList = new ArrayList<Integer>();

		rateDateTypeList.add(rate1Array[0]);
		rateDateTypeList.add(rate2Array[0]);
		rateDateTypeList.add(rate3Array[0]);
		int len = rateDateTypeList.size();

		if (rateDateTypeList.get(0).equals(UsageRateDateType.WEEK_END.getCode())) {
			rateStartTimeList.add(0);
			rateEndTimeList.add(23);
		} else {
			rateStartTimeList.add(new Integer(rate1Array[1]));
			rateEndTimeList.add(new Integer(rate1Array[2]));
		}

		if (rateDateTypeList.get(1).equals(UsageRateDateType.WEEK_END.getCode())) {
			rateStartTimeList.add(0);
			rateEndTimeList.add(23);
		} else {
			rateStartTimeList.add(new Integer(rate2Array[1]));
			rateEndTimeList.add(new Integer(rate2Array[2]));
		}

		if (rateDateTypeList.get(2).equals(UsageRateDateType.WEEK_END.getCode())) {
			rateStartTimeList.add(0);
			rateEndTimeList.add(23);
		} else {
			rateStartTimeList.add(new Integer(rate3Array[1]));
			rateEndTimeList.add(new Integer(rate3Array[2]));
		}

		int cnt = 0;
		int year = 0;
		int month = 0;
		int day = 0;
		Calendar cal = null;

		int duration = 0;

		try {
			duration = TimeUtil.getDayDuration(searchStartDate, searchEndDate);
		} catch (ParseException e1) {
			e1.printStackTrace();
		}

		String tmpDate = searchStartDate;

		for (int i = 0; i <= duration; i++) {
			dateSet.add(tmpDate);
			try {
				tmpDate = TimeUtil.getPreDay(tmpDate + "000000", -1).substring(0, 8);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		for (Map<String, Object> obj : list) {
			listMap.put((String) obj.get("YYYYMMDD"), obj);
		}

		for (String date : dateSet) {
			if (listMap.get(date) != null) {
				objMap = (Map<String, Object>) listMap.get(date);
				map = new HashMap<String, Object>();
				map.put("meteringTime", TimeLocaleUtil.getLocaleDate(date, lang, country));
				map.put("meteringTimeDis", TimeLocaleUtil.getLocaleDate(date, lang, country));

				year = Integer.parseInt(date.substring(0, 4));
				month = Integer.parseInt(date.substring(4, 6)) - 1;
				day = Integer.parseInt(date.substring(6, 8));
				cal = Calendar.getInstance();
				cal.set(year, month, day);

				for (int i = 0; i < len; i++) {
					bdRateValue = new BigDecimal("0");

					if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
							|| cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) { // 토,일요일
						if (rateDateTypeList.get(i).equals(UsageRateDateType.WEEK_END.getCode())) { // weekend 적용
							if (rateStartTimeList.get(i) > rateEndTimeList.get(i)) { // 시작시간 > 종료시간일 경우 : 0 ~ 종료시간, 시작시간
																						// ~ 23
								for (int j = 0; j < (rateEndTimeList.get(i) + 1); j++) {
									nvalue = (Number) objMap.get("VALUE_" + CalendarUtil.to2Digit(j));
									bdRateValue = bdRateValue
											.add(new BigDecimal(nvalue == null ? "0" : nvalue.toString()));
								}

								for (int j = rateStartTimeList.get(i); j < 24; j++) {
									nvalue = (Number) objMap.get("VALUE_" + CalendarUtil.to2Digit(j));
									bdRateValue = bdRateValue
											.add(new BigDecimal(nvalue == null ? "0" : nvalue.toString()));
								}
							} else { // 시작시간 < 종료시간일 경우 : 시작시간 ~ 종료시간
								for (int j = rateStartTimeList.get(i); j < (rateEndTimeList.get(i) + 1); j++) {
									nvalue = (Number) objMap.get("VALUE_" + CalendarUtil.to2Digit(j));
									bdRateValue = bdRateValue
											.add(new BigDecimal(nvalue == null ? "0" : nvalue.toString()));
								}
							}
						} else {
							bdRateValue = null;
						}
					} else { // 평일일 경우
						if (rateDateTypeList.get(i).equals(UsageRateDateType.WEEK_END.getCode())) {
							bdRateValue = null;
						} else { // weekend 이외 적용
							if (rateStartTimeList.get(i) > rateEndTimeList.get(i)) { // 시작시간 > 종료시간일 경우 : 0 ~ 종료시간, 시작시간
																						// ~ 23
								for (int j = 0; j < (rateEndTimeList.get(i) + 1); j++) {
									nvalue = (Number) objMap.get("VALUE_" + CalendarUtil.to2Digit(j));
									bdRateValue = bdRateValue
											.add(new BigDecimal(nvalue == null ? "0" : nvalue.toString()));
								}

								for (int j = rateStartTimeList.get(i); j < 24; j++) {
									nvalue = (Number) objMap.get("VALUE_" + CalendarUtil.to2Digit(j));
									bdRateValue = bdRateValue
											.add(new BigDecimal(nvalue == null ? "0" : nvalue.toString()));
								}
							} else { // 시작시간 < 종료시간일 경우 : 시작시간 ~ 종료시간
								for (int j = rateStartTimeList.get(i); j < (rateEndTimeList.get(i) + 1); j++) {
									nvalue = (Number) objMap.get("VALUE_" + CalendarUtil.to2Digit(j));
									bdRateValue = bdRateValue
											.add(new BigDecimal(nvalue == null ? "0" : nvalue.toString()));
								}
							}
						}
					}

					map.put("rate_" + (i + 1), (bdRateValue == null) ? "- " : mdf.format(bdRateValue.doubleValue()));

					if (cnt == 0) {
						if (bdRateValue != null) {
							bdTotalSumList.add(bdRateValue);
							bdTotalMaxList.add(bdRateValue);
							bdTotalMinList.add(bdRateValue);
							intTotalCount.add(1);
						} else {
							bdTotalSumList.add(null);
							bdTotalMaxList.add(null);
							bdTotalMinList.add(null);
							intTotalCount.add(0);
						}
					} else {
						if (bdRateValue != null) {
							if (bdTotalSumList.get(i) != null) {
								bdTotalSumList.set(i, bdTotalSumList.get(i).add(bdRateValue));
							} else {
								bdTotalSumList.set(i, bdRateValue);
							}
							if (bdTotalMaxList.get(i) != null) {
								bdTotalMaxList.set(i, bdTotalMaxList.get(i).max(bdRateValue));
							} else {
								bdTotalMaxList.set(i, bdRateValue);
							}
							if (bdTotalMinList.get(i) != null) {
								bdTotalMinList.set(i, bdTotalMinList.get(i).min(bdRateValue));
							} else {
								bdTotalMinList.set(i, bdRateValue);
							}
							intTotalCount.set(i, intTotalCount.get(i) + 1);
						}
					}
				}
				cnt++;
				result.add(map);
			} else {
				map = new HashMap<String, Object>();
				map.put("meteringTime", TimeLocaleUtil.getLocaleDate(date, lang, country));
				map.put("meteringTimeDis", TimeLocaleUtil.getLocaleDate(date, lang, country));
				for (int i = 0; i < len; i++) {
					map.put("rate_" + (i + 1), "- ");
				}
				result.add(map);
			}
		}

		/*
		 * if (bdTotalSumList != null && bdTotalSumList.size() > 0) { map = new
		 * HashMap<String, Object>(); map.put("meteringTime",
		 * conditionMap.get("msgSum")); map.put("id", "sum");
		 * 
		 * for (int i = 0; i < len; i++) { map.put("rate_" + (i + 1),
		 * bdTotalSumList.get(i) == null ? "- " :
		 * mdf.format(bdTotalSumList.get(i).doubleValue())); }
		 * 
		 * result.add(map);
		 * 
		 * map = new HashMap<String, Object>(); map.put("meteringTime",
		 * conditionMap.get("msgAvg") + "(" + conditionMap.get("msgMax") + "/" +
		 * conditionMap.get("msgMin") + ")"); map.put("id", "avg");
		 * 
		 * for (int i = 0; i < len; i++) { if (intTotalCount.get(i) == 0 ||
		 * bdTotalSumList.get(i) == null) { avgValue = null; } else { avgValue =
		 * bdTotalSumList.get(i).divide(new BigDecimal(intTotalCount.get(i)),
		 * MathContext.DECIMAL32).doubleValue(); }
		 * 
		 * sbAvgValue = new StringBuilder(); sbAvgValue.append((avgValue == null) ? "- "
		 * : mdf.format(avgValue)); sbAvgValue.append("(");
		 * sbAvgValue.append((bdTotalMaxList.get(i) == null) ? " - " :
		 * mdf.format(bdTotalMaxList.get(i).doubleValue())); sbAvgValue.append("/");
		 * sbAvgValue.append((bdTotalMinList.get(i) == null) ? " - " :
		 * mdf.format(bdTotalMinList.get(i).doubleValue())); sbAvgValue.append(")");
		 * map.put("rate_" + (i + 1), sbAvgValue.toString()); }
		 * 
		 * result.add(map); }
		 */

		return result;
	}

	/**
	 * method name : getMeteringDataDetailRatelyChartData<b/> method Desc : Metering
	 * Data 맥스가젯 상세화면에서 Rate 별 Chart 데이터를 조회한다.
	 *
	 * @param conditionMap
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getMeteringDataDetailRatelyChartData(Map<String, Object> conditionMap) {
		List<Map<String, Object>> searchData = new ArrayList<Map<String, Object>>();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Map<String, Object> searchTotalData = new HashMap<String, Object>();
		String searchStartDate = (String) conditionMap.get("searchStartDate");
		String searchEndDate = (String) conditionMap.get("searchEndDate");
		Integer supplierId = (Integer) conditionMap.get("supplierId");
		String[] rate1Array = ((String) conditionMap.get("rate1")).split(",");
		String[] rate2Array = ((String) conditionMap.get("rate2")).split(",");
		String[] rate3Array = ((String) conditionMap.get("rate3")).split(",");
		List<Map<String, Object>> list = meteringDayDao.getMeteringDataDetailRatelyData(conditionMap);

		if (list == null || list.size() <= 0) {
			resultMap.put("searchData", searchData);
			return resultMap;
		}

		Map<String, Object> map = null;
		Map<String, Object> objMap = null;
		Map<String, Object> listMap = new HashMap<String, Object>();
		Set<String> dateSet = new LinkedHashSet<String>();
		Supplier supplier = supplierDao.get(supplierId);
		String country = supplier.getCountry().getCode_2letter();
		String lang = supplier.getLang().getCode_2letter();
		DecimalFormat mdf = DecimalUtil.getDecimalFormat(supplier.getMd());
		Number nvalue = null;
		BigDecimal bdRateValue = null;

		List<String> rateDateTypeList = new ArrayList<String>();
		List<Integer> rateStartTimeList = new ArrayList<Integer>();
		List<Integer> rateEndTimeList = new ArrayList<Integer>();
		List<BigDecimal> rateTotalList = new ArrayList<BigDecimal>(); // rate 별 합계

		rateDateTypeList.add(rate1Array[0]);
		rateDateTypeList.add(rate2Array[0]);
		rateDateTypeList.add(rate3Array[0]);
		int len = rateDateTypeList.size();

		if (rateDateTypeList.get(0).equals(UsageRateDateType.WEEK_END.getCode())) {
			rateStartTimeList.add(0);
			rateEndTimeList.add(23);
		} else {
			rateStartTimeList.add(new Integer(rate1Array[1]));
			rateEndTimeList.add(new Integer(rate1Array[2]));
		}

		if (rateDateTypeList.get(1).equals(UsageRateDateType.WEEK_END.getCode())) {
			rateStartTimeList.add(0);
			rateEndTimeList.add(23);
		} else {
			rateStartTimeList.add(new Integer(rate2Array[1]));
			rateEndTimeList.add(new Integer(rate2Array[2]));
		}

		if (rateDateTypeList.get(2).equals(UsageRateDateType.WEEK_END.getCode())) {
			rateStartTimeList.add(0);
			rateEndTimeList.add(23);
		} else {
			rateStartTimeList.add(new Integer(rate3Array[1]));
			rateEndTimeList.add(new Integer(rate3Array[2]));
		}

		int year = 0;
		int month = 0;
		int day = 0;
		Calendar cal = null;

		int duration = 0;

		try {
			duration = TimeUtil.getDayDuration(searchStartDate, searchEndDate);
		} catch (ParseException e1) {
			e1.printStackTrace();
		}

		// rate 별 합계 초기화
		for (int i = 0; i < len; i++) {
			rateTotalList.add(new BigDecimal(0));
		}

		String tmpDate = searchStartDate;

		for (int i = 0; i < (duration + 1); i++) {
			dateSet.add(tmpDate);
			try {
				tmpDate = TimeUtil.getPreDay(tmpDate + "000000", -1).substring(0, 8);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		for (Map<String, Object> obj : list) {
			listMap.put((String) obj.get("YYYYMMDD"), obj);
		}

		for (String date : dateSet) {
			if (listMap.get(date) != null) {
				objMap = (Map<String, Object>) listMap.get(date);
				year = Integer.parseInt(date.substring(0, 4));
				month = Integer.parseInt(date.substring(4, 6)) - 1;
				day = Integer.parseInt(date.substring(6, 8));
				cal = Calendar.getInstance();
				cal.set(year, month, day);

				for (int i = 0; i < len; i++) {
					map = new HashMap<String, Object>();
					bdRateValue = new BigDecimal("0");

					if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
							|| cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) { // 토/일요일
						if (rateDateTypeList.get(i).equals(UsageRateDateType.WEEK_END.getCode())) { // weekend
							if (rateStartTimeList.get(i) > rateEndTimeList.get(i)) {
								for (int j = 0; j < (rateEndTimeList.get(i) + 1); j++) {
									nvalue = (Number) objMap.get(
											"VALUE_" + StringUtil.frontAppendNStr('0', new Integer(j).toString(), 2));
									bdRateValue = bdRateValue
											.add(new BigDecimal(nvalue == null ? "0" : nvalue.toString()));
								}

								for (int j = rateStartTimeList.get(i); j < 24; j++) {
									nvalue = (Number) objMap.get(
											"VALUE_" + StringUtil.frontAppendNStr('0', new Integer(j).toString(), 2));
									bdRateValue = bdRateValue
											.add(new BigDecimal(nvalue == null ? "0" : nvalue.toString()));
								}
							} else {
								for (int j = rateStartTimeList.get(i); j < (rateEndTimeList.get(i) + 1); j++) {
									nvalue = (Number) objMap.get(
											"VALUE_" + StringUtil.frontAppendNStr('0', new Integer(j).toString(), 2));
									bdRateValue = bdRateValue
											.add(new BigDecimal(nvalue == null ? "0" : nvalue.toString()));
								}
							}
						} else {
							bdRateValue = null;
						}
					} else { // 평일
						if (rateDateTypeList.get(i).equals(UsageRateDateType.WEEK_END.getCode())) {
							bdRateValue = null;
						} else { // weekday
							if (rateStartTimeList.get(i) > rateEndTimeList.get(i)) {
								for (int j = 0; j < (rateEndTimeList.get(i) + 1); j++) {
									nvalue = (Number) objMap.get(
											"VALUE_" + StringUtil.frontAppendNStr('0', new Integer(j).toString(), 2));
									bdRateValue = bdRateValue
											.add(new BigDecimal(nvalue == null ? "0" : nvalue.toString()));
								}

								for (int j = rateStartTimeList.get(i); j < 24; j++) {
									nvalue = (Number) objMap.get(
											"VALUE_" + StringUtil.frontAppendNStr('0', new Integer(j).toString(), 2));
									bdRateValue = bdRateValue
											.add(new BigDecimal(nvalue == null ? "0" : nvalue.toString()));
								}
							} else {
								for (int j = rateStartTimeList.get(i); j < (rateEndTimeList.get(i) + 1); j++) {
									nvalue = (Number) objMap.get(
											"VALUE_" + StringUtil.frontAppendNStr('0', new Integer(j).toString(), 2));
									bdRateValue = bdRateValue
											.add(new BigDecimal(nvalue == null ? "0" : nvalue.toString()));
								}
							}
						}
					}

					map.put("localeDate", TimeLocaleUtil.getLocaleDate(date, lang, country));
					map.put("rateIndex", (i + 1));
					map.put("value", (bdRateValue == null) ? 0D : bdRateValue.doubleValue());
					map.put("decimalValue",
							(bdRateValue == null) ? mdf.format(0D) : mdf.format(bdRateValue.doubleValue()));

					if (bdRateValue != null) {
						rateTotalList.set(i, rateTotalList.get(i).add(bdRateValue));
					}
					searchData.add(map);
				}
			} else {
				for (int i = 0; i < len; i++) {
					map = new HashMap<String, Object>();
					map.put("localeDate", TimeLocaleUtil.getLocaleDate(date, lang, country));
					map.put("rateIndex", (i + 1));
					map.put("value", 0D);
					map.put("decimalValue", mdf.format(0D));
					searchData.add(map);
				}
			}
		}

		resultMap.put("searchData", searchData);

		for (int i = 0; i < rateTotalList.size(); i++) {
			searchTotalData.put("total" + i, mdf.format(rateTotalList.get(i).doubleValue()));
		}
		resultMap.put("searchTotalData", searchTotalData);
		return resultMap;
	}

	/**
	 * method name : getMeteringDataDetailIntervalChartData<b/> method Desc :
	 * Metering Data 맥스가젯 상세화면에서 Interval 별 Chart 데이터를 조회한다.
	 *
	 * @param conditionMap
	 * @return
	 */
	public Map<String, Object> getMeteringDataDetailIntervalChartData(Map<String, Object> conditionMap) {
		// Get variables from conditionMap
		Integer supplierId = (Integer) conditionMap.get("supplierId");
		String meterNo = (String) conditionMap.get("meterNo");
		String searchStartDate = (String) conditionMap.get("searchStartDate");
		String searchEndDate = (String) conditionMap.get("searchEndDate");
		String searchStartHour = (String) conditionMap.get("searchStartHour");
		String searchEndHour = (String) conditionMap.get("searchEndHour");
		String[] channelArray = ((String) conditionMap.get("channel")).split(",");

		// Define variables
		List<Integer> channelIdList = new ArrayList<Integer>();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Map<String, Object>> searchData = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> searchAddData = new ArrayList<Map<String, Object>>();
		Supplier supplier = supplierDao.get(supplierId);
		String country = supplier.getCountry().getCode_2letter();
		String lang = supplier.getLang().getCode_2letter();
		DecimalFormat mdf = DecimalUtil.getDecimalFormat(supplier.getMd());
		Meter meter = null;
		Integer lpInterval = 0;
		Set<String> dateSet = new LinkedHashSet<String>();
		String tmpLocaleDateHour = searchStartDate + searchStartHour;

		// Make channel ID list from channel array.
		for (String obj : channelArray) {
			channelIdList.add(Integer.parseInt(obj));
		}

		// Put channel id list at conditionMap
		conditionMap.put("channelIdList", channelIdList);

		// Get lp data from DB.
		List<Map<String, Object>> list = meteringLpDao.getMeteringDataDetailLpData(conditionMap);

		// Check size of list and return.
		if (list == null || list.isEmpty()) {
			resultMap.put("searchData", searchData);
			resultMap.put("searchAddData", searchAddData);
			logger.debug("list is null or empty. return resultMap : " + resultMap);
			return resultMap;
		}

		if (meterNo != null) {
			meter = mtrDao.get(meterNo);
			lpInterval = (meter.getLpInterval() == null) ? 60 : meter.getLpInterval();
		} else {
			lpInterval = 60;
		}
		logger.debug("lpInterval : " + lpInterval);

		// 조회조건 내 모든 일자 가져오기
		for (int k = 0; k < 100; k++) { // 무한 loop 방지
			for (int i = 0, j = 0; j < 60; i++, j = i * lpInterval) {
				dateSet.add(tmpLocaleDateHour + CalendarUtil.to2Digit(j)); // YYYYMMDDHHMI
			}

			if (tmpLocaleDateHour.compareTo(searchEndDate + searchEndHour) >= 0) { // 종료일자이면 종료
				break;
			} else {
				try {
					tmpLocaleDateHour = DateTimeUtil.getPreHour(tmpLocaleDateHour + "0000", -1).substring(0, 10); // +1
																													// H
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}

		//
		Map<String, Double> listMap = new HashMap<String, Double>();
		for (Map<String, Object> obj : list) {
			// Get data from Map
			String YYYYMMDDHHMISS = (String) obj.get("YYYYMMDDHHMISS");
			String CHANNEL = ((BigDecimal) obj.get("CHANNEL")).toString();
			String VALUE = ((BigDecimal) obj.get("VALUE")).toString();
			Double value = Double.valueOf(VALUE);
			// logger.debug("YYYYMMDDHHMISS:" + YYYYMMDDHHMISS + ", CHANNEL:" + CHANNEL + ",
			// VALUE:" + VALUE);

			// Null and empty check.
			if (YYYYMMDDHHMISS == null || YYYYMMDDHHMISS.isEmpty()) {
				logger.debug("YYYYMMDDHHMISS is null or empty.");
				continue;
			}
			if (CHANNEL == null || CHANNEL.isEmpty()) {
				logger.debug("CHANNEL is null or empty.");
				continue;
			}
			if (VALUE == null || VALUE.isEmpty()) {
				logger.debug("VALUE is null or empty.");
				continue;
			}

			// Check if the value exists
			if (listMap.containsKey(YYYYMMDDHHMISS.substring(0, 12) + "_" + CHANNEL)) {
				Double tmpVal = listMap.get(YYYYMMDDHHMISS.substring(0, 12) + "_" + CHANNEL);
				BigDecimal bdCurVal = new BigDecimal(VALUE.toString());
				BigDecimal bdPrevVal = new BigDecimal(tmpVal.toString());
				value = bdCurVal.add(bdPrevVal).doubleValue();
			}

			// Put value to list
			listMap.put(YYYYMMDDHHMISS.substring(0, 12) + "_" + CHANNEL, value);
		}

		for (String lpDate : dateSet) {
			for (Integer ch : channelIdList) {
				Map<String, Object> map = new HashMap<String, Object>();
				Double value = listMap.get(lpDate + "_" + ch);

				map.put("reportDate", TimeLocaleUtil.getLocaleDate(lpDate + "00", lang, country));
				map.put("localeDate", TimeLocaleUtil.getLocaleDate(lpDate + "00", lang, country));
				map.put("channel", ch);
				map.put("value", (value == null) ? 0D : value);
				map.put("decimalValue", (value == null) ? mdf.format(0D) : mdf.format(value));
				searchData.add(map);

			}
		}
		resultMap.put("searchData", searchData);
		resultMap.put("searchAddData", searchAddData);

		return resultMap;
	}

	/*
	 * Double 형 데이터를 MD Format 자리수에 맞춰 Double 로 표시
	 */
	public Double getMDFormatDouble(Double value, DecimalPattern dp) {
		Double result = null;
		if (value == null || value == 0D) {
			return value;
		}

		String pattern = dp.getPattern();
		dp.setPattern(pattern.replaceAll(",", ""));
		DecimalFormat df = DecimalUtil.getDecimalFormat(dp);

		String fmtValue = df.format(value);
		result = Double.parseDouble(fmtValue);
		return result;
	}

	/*
	 * Double 형 데이터를 소수점 이하 값오류를 정정하기 위해 소수점 10째자리에서 반올림
	 */
	public Double getDecimalCorrectDouble(Double value) {
		Double result = null;

		if (value != null) {
			result = Double.parseDouble(String.format("%.10f", value));
		}

		return result;
	}

	@Override
	public List<Map<String, Object>> getMdsIdFromContract(Map<String, Object> conditionMap) {
		return contractDao.getMdsIdFromContractNumber(conditionMap);
	}

}