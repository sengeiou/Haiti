package com.aimir.service.mvm.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aimir.dao.mvm.ChannelConfigDao;
import com.aimir.dao.mvm.MeteringDayDao;
import com.aimir.dao.mvm.MeteringMonthDao;
import com.aimir.dao.mvm.SeasonDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.dao.system.CustomerDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.dao.system.TOURateDao;
import com.aimir.dao.system.TariffTypeDao;
import com.aimir.model.system.Contract;
import com.aimir.model.system.Supplier;
import com.aimir.service.mvm.ComparisonChartManager;
import com.aimir.util.Condition;
import com.aimir.util.SearchCalendarUtil;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeLocaleUtil;
import com.aimir.util.Condition.Restriction;

@Service(value = "comparisonChartManager")
public class ComparisonChartManagerImpl implements ComparisonChartManager{
	
	Log logger = LogFactory.getLog(ComparisonChartManagerImpl.class);
	
	@Autowired
	CustomerDao ctmDao;
	
	@Autowired
	ContractDao contractDao;

    @Autowired
    MeteringDayDao meteringDayDao;

    @Autowired
    MeteringMonthDao meteringMonthDao;

    @Autowired
    TariffTypeDao tariffTypeDao;
    
    @Autowired
    SeasonDao seasonDao;
    
    @Autowired
    CodeDao codeDao;
    
    @Autowired
    TOURateDao touRateDao;
    
    @Autowired
    ChannelConfigDao channelConfigDao;

    @Autowired
    SupplierDao supplierDao;

//    public HashMap<String, Object> getContractNumber(String[] values){
//    	HashMap<String, Object> result = new HashMap<String, Object>();
//    	String[] contractNumber = values[4].split(",");
//    	result.put("contractNumber", contractNumber);
//    	return result;
//    }
    
    /*
     * 비교차트 getInOffTimeChartData를 추출
     * --- 기본로직 ---------------------
     * 계약자별 {
     * 		TOURate에 해당하는 계절별 {
     * 			데이터 세팅
     * 		}
     * }
     */
//    public HashMap<String, Object> getInOffTimeChartData(String[] values, String type) {
//    	HashMap<String, Object> resultHm = new HashMap<String, Object>();
//    	
//    	HashMap<String, Object> hm = setArrayToCondition(values, type);
//    	String year   = (String)hm.get("year");
//    	String stdDay = (String)hm.get("startDate");
//    	String endDay = (String)hm.get("endDate");
//    	
//		String[] contract_number = values[4].split(",");
//    	List<Contract> contract = contractNumberToContractInfo(contract_number);
//    	// type으로 해당 채널을 검색함, 최대 10개의 계약건이 존재할수 있어서 조회하는 테이블로 검색함
//    	HashMap<Integer,String> legendValue = makeChannelDataWithUnit(type);
//    	
//    	String meterType = ChangeMeterTypeName.valueOf(type).getCode();
//		int typeId = codeDao.getCodeIdByCode(MeterType.valueOf(meterType).getServiceType());
//		
//		// 계약별로 데이터를 추출
//		for(int idx=0;idx < contract.size();idx++) {
//			List<InOffTimeChartData> result = new ArrayList<InOffTimeChartData>();
//			int contractId = contract.get(idx).getId();
//			hm.put("contractId", contractId);// 계약id추가
//			
//			int supplierId = contract.get(idx).getSupplier().getId();
//			
//			int tariffTypeId = tariffTypeDao.getTariffTypeList(supplierId, typeId).get(0).getId();
//			String seasonGbn = "";
//			//seasonID추출
//			List<Object> objList = new ArrayList<Object>();
//			List<Season> searchSeasonList = seasonDao.getSeasonsBySyear(year);
//			HashMap<String, Object> seasonHm = new HashMap<String, Object>();
//			seasonHm.put("year", year);
//			seasonHm.put("stdDate", stdDay);
//			seasonHm.put("endDate", endDay);
//			seasonHm.put("tariffTypeId", tariffTypeId);
//			
//			if (searchSeasonList.size() > 0 && searchSeasonList != null) {
//				objList = touRateDao.getTOURateWithSeasonsBySyear(seasonHm);
//				seasonGbn = "notNull";
//			}
//			else {
//				objList = touRateDao.getTOURateWithSeasonsBySyearNull(seasonHm);
//				seasonGbn = "Null";
//			}
//			
//			HashMap<String, Object> colHm = getTableCols (objList, hm);
//
//				
//				// 추출된 계약자의 tourate와 계절별 시작일자 , 종료일자 별 데이터 추출
//				Iterator ents = colHm.entrySet().iterator();
//				while (ents.hasNext()) {
//					Map.Entry mEntry = (Map.Entry)ents.next();
//					String strKey = (String)mEntry.getKey();
//					List<String> ls = (List<String>)colHm.get(strKey);
//					String[] str = strKey.split("@");//[trLocalName][trStartTime][trEndTime]
//					String startDate = str[0];
//					String endDate = str[1];
//					String localName = str[2];
//					String startTime = str[3];
//					String endTime = str[4];
//					
//					// 수정된 startDate와 endDate, select절의 컬럼 세팅
//					hm.remove("startDate");
//			    	hm.remove("endDate");
//			    	hm.remove("selectQuery");
//			    	hm.put("startDate", startDate);
//			    	hm.put("endDate", endDate);
//					hm.put("selectQuery", ls);
//					List<Object> tmpData =  meteringDayDao.getInOffTimeChartData(hm);
//					//추출된 데이터를 객체에 세팅
//					Iterator<Object> it = tmpData.iterator();
//					while (it.hasNext()) {
//						InOffTimeChartData iod = new InOffTimeChartData();
//						Object[] obj = (Object[]) it.next();
//						
//						String contractNumber = (String)obj[0];
//						String yyyyMMdd = (String)obj[1];
//						int channel = (Integer)obj[2];
//						Double	useValue = (Double)obj[3];
//						
//						iod.setContractNumber(contractNumber);
//						iod.setYyyyMMdd(yyyyMMdd);
//						iod.setChannel(String.valueOf(channel));
//						iod.setValue(useValue);
//						iod.setEndTime(endTime);
//						iod.setLocalName(localName);
//						iod.setStartTime(startTime);
//						String name = localName+" hour("+startTime+"-"+endTime+")"+legendValue.get(channel);
//						iod.setName(name);
//						result.add(iod);
//					}
//					
//				}
//				resultHm.put(contract.get(idx).getContractNumber(), result);
//				hm.remove("contractId");//계약id삭제
//			}
//		return resultHm;
//	}
    
    
    /*
	 * 비교차트의 Load DurationChart Data를 생성한다.
	 */
    
//	public List<LoadDurationChartData> getLoadDurationChartData(String[] values, String type) {
//		
//		HashMap<String, Object> hm = setArrayToCondition(values, type);
//		List<LoadDurationChartData> result = new ArrayList<LoadDurationChartData>();
//		List<Object> tmpData = new ArrayList<Object>();
//		int totCountList = 0;
//		
//		String[] contract_number = values[4].split(",");
//		Integer[] contractIdList = contractNumberToContractId(contract_number);
//		
//		for(int idx=0;idx < contractIdList.length;idx++) {
//			hm.put("contractId", contractIdList[idx]);// 계약id추가
//			totCountList = meteringDayDao.getLoadDurationChartTotalCount(hm);
//			hm.put("dataTotCount",totCountList);
//			
//			tmpData = meteringDayDao.getLoadDurationChartData(hm);
//			if(tmpData != null && tmpData.size() >0) {
//				
//				Iterator<Object> itr = tmpData.iterator();
//				
//				while(itr.hasNext()){
//					LoadDurationChartData ld = new LoadDurationChartData();
//					Object[] data = (Object[]) itr.next();
//					
//					ld.setContractNumber((String)data[0]);
//					ld.setValue(getDoubleToStirng((Double)data[1]));
//					ld.setPerValue(Math.round((Integer)data[2])+"");
//					
//					result.add(ld);
//				}
//			}	
//			hm.remove("contractId");//계약id삭제
//		}
//		return result;
//	}
	
	/*
	 * 비교차트의OverlayChar Data를 생성한다.
	 */
//	public HashMap<String, Object> getOverlayChartData(String[] values, String type) {
//		HashMap<String, Object> resultHm = new HashMap<String, Object>();
//
//		HashMap<String, Object> hm = setArrayToCondition(values, type);
//		List<Object> tmpData = new ArrayList<Object>();
//		HashMap<Integer,String> legendValue = makeChannelData(type);
//		String[] contract_number = values[4].split(",");
//		Integer[] contractIdList = contractNumberToContractId(contract_number);
//
//		for(int idx=0;idx < contractIdList.length;idx++) {
//			List<OverlayChartData> result = new ArrayList<OverlayChartData>();
//			hm.put("contractId", contractIdList[idx]);// 계약id추가
//			tmpData = meteringDayDao.getOverlayChartData(hm);
//			hm.remove("contractId");
//
//			if(tmpData != null && tmpData.size() >0) {
//				
//				String contractNumber = "";
//				String yyyyMmdd = "";
//				int channel = 0;
//				String name ="";
//				
//				Iterator<Object> itr = tmpData.iterator();
//				while(itr.hasNext()){
//					OverlayChartData ocd =  new OverlayChartData();
//					Class cls =  ocd.getClass();
//					
//					Object[] obj = (Object[]) itr.next();
//					
//					contractNumber = (String)obj[0];
//					yyyyMmdd = (String)obj[1];
//					channel = (Integer)obj[2];
//					name = legendValue.get(channel)+"."+yyyyMmdd;
//					
//					try {
//						int num =0;
//						for(int objIdx =3; objIdx < obj.length; objIdx++) {
//							cls.getField("value"+num).set(ocd, String.valueOf((Double)obj[objIdx]));
//							num++;
//						}
//						
//					}
//					catch (IllegalAccessException ie) {
//						System.out.println("ie:"+ie.getMessage());
//					}
//					catch (NoSuchFieldException ne) {
//						System.out.println("ne:"+ne.getMessage());
//					}
//					catch (Exception e) {
//						System.out.println("e:"+e.getMessage());
//					}
//					ocd.setContractNumber(contractNumber);
//					ocd.setChannel(String.valueOf(channel));
//					ocd.setName(name);
//					ocd.setYyyymmdd(yyyyMmdd);
//					result.add(ocd);
//				}
//				resultHm.put(contractNumber, result);
//			}	
//			
//		}
//		
//		return resultHm;
//	}
	
	
	/*
	 * peak별로 추출해야하는 컬럼명을 초기화시킴
	 */
//	public HashMap<String, String> initTableCols () {
//		HashMap<String, String> hm = new HashMap<String, String>();
//		for(int colIdx =0; colIdx < 24; colIdx ++) {
//			String val = String.valueOf(colIdx);
//			if(colIdx <10) {
//				hm.put(val, "A.value_0"+val);
//			}
//			else {
//				hm.put(val, "A.value_"+val);
//			}
//			
//		}
//		return hm;
//	}
	
	/*
	 * TOURate에서 추출된 각 paek별 시작 시간을 가지고 day_xx 테이블의 해당 컬럼을 가변적으로 추출하기 위한 로직
	 * ininitTableCols()에서 선언된 테이블 컬럼명을 가진 hashmap 객체에서 paek별 시작종료시간에 대한 컬럼을 추출하고
	 * 최종적으로  Off-Peak에서는 남아있는 컬럼명들을 추출한뒤 초기화 한다.
	 * 리스트에 객체가 있을경우(계절이 2개 이상일때)는 hashmap객체에 값이 남아있는지 확인하고 없으면 다시 초기화 해서 
	 * 같은 로직을 반복하여 추출함.
	 * return key(시작일자@종료일자@peak명@peak시작시간@peak종료시간), value(day_XX의 컬럼들)
	 */
//	public HashMap<String, Object> getTableCols (List<Object> lst, HashMap<String, Object> hm) {
//		HashMap<String, Object> result = new HashMap<String, Object>();
//		HashMap<String, String> tableHm = initTableCols();
//		
//		String stdDate = (String)hm.get("startDate");
//		String endDate = (String)hm.get("endDate");
//		
//		int lstSize = lst.size();
//		
//		if(lstSize > 0){
//		
//			Object[] stdObj = (Object[])lst.get(0);
//			String firstSeasonDay = (String)stdObj[0];
//			
//			Object[] endObj = (Object[])lst.get(lstSize-1);
//			String endSeasonDay = (String)endObj[1];
//			
//			
//			for(int j=0;j<lst.size(); j++) {
//				String seasonStdDate = "";//계절의 시작일자
//				String seasonEndDate = "";//계절의 종료일자
//				String tourLocalName = "";//tourate local_name 
//				String tourStartTime = "";//tourate startTime
//				String tourEndTime   = "";//tourate endTime
//				Object[] ojb = (Object[])lst.get(j);
//				String tmpStdDate = (String)ojb[0];
//				String tmpEndDate = (String)ojb[1];
//				
//				if(firstSeasonDay.equals(tmpStdDate)) {// 첫번째 시작일자는 조회 기준 첫 일자를 세팅
//					seasonStdDate =stdDate;
//				}
//				else {
//					seasonStdDate = tmpStdDate;//계절의 시작일자
//				}
//				
//				if(endSeasonDay.equals(tmpEndDate)) {
//					seasonEndDate = endDate;//계절의 종료일자
//				}
//				else {
//					seasonEndDate = tmpEndDate;//계절의 종료일자
//				}
//				tourLocalName = (String)ojb[2]; 
//				tourStartTime = (String)ojb[3];
//				tourEndTime   = (String)ojb[4];
//				String key = seasonStdDate+"@"+seasonEndDate+"@"+tourLocalName+"@"+tourStartTime+"@"+tourEndTime;
//				
//				List<String> ls = new ArrayList<String>();
//				
//				int intTrStartTime = Integer.parseInt(tourStartTime);
//				int intTrtrEndTime = Integer.parseInt(tourEndTime);
//				
//				if("Off-Peak".equals(tourLocalName)) {
//					Iterator entrys = tableHm.entrySet().iterator();
//	
//					while (entrys.hasNext()) {
//						Map.Entry mEntry = (Map.Entry)entrys.next();
//						String strKey = (String)mEntry.getKey();			
//						ls.add(tableHm.get(strKey));
//					}
//					tableHm = initTableCols();// 작성 완료후 다시 생성
//					
//				}
//				else {
//					for(int tmp = intTrStartTime;tmp <intTrtrEndTime;tmp++ ) {
//						String strKey = String.valueOf(tmp);
//						System.out.println("hm.get("+strKey+")"+tableHm.get(strKey));
//						ls.add(tableHm.get(strKey));
//						tableHm.remove(strKey);
//					}
//				}
//				result.put(key, ls);
//			}
//		}
//		return result;
//	}

//    public HashMap<String, Object> getTableCols1 (List<TOURate> lst) {
//		HashMap<String, Object> result = new HashMap<String, Object>();
//		HashMap<String, String> hm = new HashMap<String, String>();
//		for(int colIdx =0; colIdx < 24; colIdx ++) {
//			String val = String.valueOf(colIdx);
//			if(colIdx <10) {
//				hm.put(val, "A.value_0"+val);
//			}
//			else {
//				hm.put(val, "A.value_"+val);
//			}
//			
//		}
//		int comSize = lst.size() -1;
//		for(int j=0;j<lst.size(); j++) {
//			
//			String trLocalName 		= lst.get(j).getLocalName();
//			String trStartTime 		= lst.get(j).getStartTime();
//			String trEndTime 		= lst.get(j).getEndTime();
//			String key = trLocalName+"@"+trStartTime+"@"+trEndTime;
//			
//			List<String> ls = new ArrayList<String>();
//			
//			int intTrStartTime = Integer.parseInt(trStartTime);
//			int intTrtrEndTime = Integer.parseInt(trEndTime);
//			
//			if(j == comSize) {
//				
//				Iterator entrys = hm.entrySet().iterator();
//
//				while (entrys.hasNext()) {
//					Map.Entry mEntry = (Map.Entry)entrys.next();
//					String strKey = (String)mEntry.getKey();
//					System.out.println("mEntry.getKey()["+strKey+"]"+hm.get(strKey));
//					
//					ls.add(hm.get(strKey));
//				}
//				
//				
//			}
//			else {
//				for(int tmp = intTrStartTime;tmp <intTrtrEndTime;tmp++ ) {
//					String strKey = String.valueOf(tmp);
//					System.out.println("hm.get("+strKey+")"+hm.get(strKey));
//					ls.add(hm.get(strKey));
//					hm.remove(strKey);
//				}
//			}
//			result.put(key, ls);
//			
//			logger.info("\n====result====\n"+result);
//			
//		}
//		return result;
//	}
	
	/*
	 * 조회 조건을 생성함
	 */
//	private HashMap<String, Object> setArrayToCondition (String[] values, String type) {
//		HashMap<String, Object> resultHm = new HashMap<String, Object>();
//		
//		Integer[] arrChannel = {1,2};
//		int channel = 1;
//		
//		String startDate = values[0].substring(0, 8);
//		String endDate = values[1].substring(0, 8);
//		String year = startDate.substring(0, 4);
//		
//		resultHm.put("year", year);
//		
//		
//		
//		
//		resultHm.put("meterType", type);
//		resultHm.put("startDate", startDate);
//		resultHm.put("endDate", endDate);
//		resultHm.put("arrChannel", arrChannel);
//		resultHm.put("channel", channel);
//		return resultHm;
//	}
	
	
	/*
	 * contractNumber로 contract객체를 추출한다.
	 */
//	private List<Contract> contractNumberToContractInfo(String [] contractNumber) {
//		List<Contract> contract = new ArrayList<Contract>();
//		
//		if( contractNumber.length > 0) {
//			Set<Condition> set = new HashSet<Condition>();
//			Condition cdt1 = new Condition("contractNumber", contractNumber, null, Restriction.IN);
//			set.add(cdt1);	
//			Condition cdt2 = new Condition("id", null, null, Restriction.ORDERBY);
//			set.add(cdt2);
//			contract = contractDao.getContractByListCondition(set);
//			
//			
//		}
//		return contract;
//	}
	
	/*
	 * Double 형 데이터를 소스 4째자리까지 Stirng으로 표시
	 */
//	public String getDoubleToStirng(Double value) {
//		Double result = 0D;
//		if (value != null && value != 0) {
//			result = value;
//		}
//		else {
//			result=0D;
//		}
//
//		return String.format("%.4f", result);
//	}
	
	/*
     * Season의 계절별 시작일, 종료일 가져오기
     */
//	public String[]  getSeasonDate (Integer id, String year, String gbn) {
//		String[] searchDate = new String[2];
//		HashMap<String, Object> seasonHm = new HashMap<String, Object>();
//		seasonHm.put("year", year);
//		seasonHm.put("seasonId", id);
//		List<Object> dataList = new ArrayList<Object>();
//		String stdDate = "";
//		String endDate = "";
//		if(gbn.equals("notNull")) {
//			dataList = seasonDao.getSeasonsDateBySyearId(seasonHm);
//			Object[] objVal = (Object[])dataList.get(0);
//			stdDate = (String)objVal[0];
//			endDate = (String)objVal[1];
//			
//		}
//		else {
//			dataList = seasonDao.getSeasonsDateBySyearNullId(seasonHm);
//			Object[] objVal = (Object[])dataList.get(0);
//			int nextYear = Integer.parseInt(year)+1;
//			String stdMonth = (String)objVal[0];
//			String endMonth = (String)objVal[1];
//			String seasonNm = (String)objVal[3];
//			if("Winter".equals(seasonNm)) {
//				stdDate = year+stdMonth+"01";
//				endDate = String.valueOf(nextYear)+endMonth+"31";
//			}
//			else {
//				stdDate = year+stdMonth+"01";
//				endDate = year+endMonth+"31";
//			}
//			
//		}
//		searchDate[0] = stdDate;
//		searchDate[1] = endDate;
//		return searchDate;
//		
//	}

//    public HashMap<Integer, String> makeChannelDataWithUnit (String type) {
//		HashMap<Integer, String> resultHm = new HashMap<Integer, String>();
//		
//		HashMap<String, Object> hm = new HashMap<String, Object>();
//		//채널값 추출 로직
//		String meterType = ChangeMeterTypeName.valueOf(type).getCode();
//		String tlbType = MeterType.valueOf(meterType).getLpClassName();
//		hm.put("tlbType", tlbType);
//		List<Object> ojbLs = channelConfigDao.getByList(hm);
//		Iterator<Object> it = ojbLs.iterator();
//		while (it.hasNext()) {
//			Object[] obj = (Object[]) it.next();
//			Integer key = (Integer)obj[0];
//			String value = (String)obj[1]+"("+(String)obj[2]+")";
//			resultHm.put(key, value);
//		}
//		return resultHm;
//	}
	
//	public HashMap<Integer, String> makeChannelData (String type) {
//		HashMap<Integer, String> resultHm = new HashMap<Integer, String>();
//		
//		HashMap<String, Object> hm = new HashMap<String, Object>();
//		//채널값 추출 로직
//		String meterType = ChangeMeterTypeName.valueOf(type).getCode();
//		String tlbType = MeterType.valueOf(meterType).getLpClassName();
//		hm.put("tlbType", tlbType);
//		List<Object> ojbLs = channelConfigDao.getByList(hm);
//		Iterator<Object> it = ojbLs.iterator();
//		while (it.hasNext()) {
//			Object[] obj = (Object[]) it.next();
//			Integer key = (Integer)obj[0];
//			String value = (String)obj[1];
//			resultHm.put(key, value);
//		}
//		return resultHm;
//	}

    /**
     * Method Name : getOverlayChartDailyData
     * Date : 2011. 7. 4
     * Method 설명 : Metering Data - Chart View - Overlay Chart (Daily)
     * 
     * @param condition
     * @return
     */
    public Map<String, Object> getOverlayChartDailyData(Map<String, Object> condition) {
        Map<String, Object> result = new HashMap<String, Object>();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        String supplierId = (String)condition.get("supplierId");
        String[] contractNumbers = ((String)condition.get("contractNumbers")).split(",");
//        Integer[] contractIds = contractNumberToContractId(contractNumbers);
        List<Contract> contractList = contractNumberToContractList(contractNumbers);

        Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));
        String lang = supplier.getLang().getCode_2letter();
        String country = supplier.getCountry().getCode_2letter();
//        DecimalFormat df = DecimalUtil.getDecimalFormat(supplier.getMd());

        // formatting 할 항목 key 패턴 정의
        String regVal = ".*value_.*";

        Pattern pVal = Pattern.compile(regVal);

        Matcher mVal = null;
        String key      = null;
        Iterator<String> itr = null;

        if (contractList != null) {
            for (Contract contract : contractList) {
                list = new ArrayList<Map<String, Object>>();
                list = meteringDayDao.getOverlayChartDailyData(condition, contract.getId());
                
                if (list != null) {
                    for (Map<String, Object> obj : list) {
                        itr = obj.keySet().iterator();

                        while (itr.hasNext()) {
                            key = itr.next();
                            mVal = pVal.matcher(key);

                            if(mVal.find()) {
                                obj.put(key, getNullToDouble((Double)obj.get(key)));
                            }
                        }
                        
                        obj.put("yyyymmdd", TimeLocaleUtil.getLocaleDate((String)obj.get("yyyymmdd"), lang, country));
                    }
                }
                result.put(contract.getContractNumber(), list);
            }
        }
        return result;
    }

    /**
     * Method Name : getOverlayChartDailyWeekData
     * Date : 2011. 7. 6
     * Method 설명 : Metering Data - Chart View - Overlay Chart (DailyWeek)
     * 
     * @param condition
     * @return
     */
    public Map<String, Object> getOverlayChartDailyWeekData(Map<String, Object> condition) {
        Map<String, Object> result = new HashMap<String, Object>();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
//        List<Map<String, Object>> datalist = new ArrayList<Map<String, Object>>();
        String supplierId = (String)condition.get("supplierId");
        String[] contractNumbers = ((String)condition.get("contractNumbers")).split(",");
        List<Contract> contractList = contractNumberToContractList(contractNumbers);

        Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));
        String lang = supplier.getLang().getCode_2letter();
        String country = supplier.getCountry().getCode_2letter();
        List<String> weekDayList = TimeLocaleUtil.getLocaleWeekDayList(lang, country);
        String[] weekDays = new String[weekDayList.size()];
        weekDayList.toArray(weekDays);
        SearchCalendarUtil scu = new SearchCalendarUtil();

        // formatting 할 항목 key 패턴 정의
        String regVal = ".*value_.*";
        Pattern pVal = Pattern.compile(regVal);
        Matcher mVal = null;
        String key      = null;
        Iterator<String> itr = null;
//        int cnt = 1;
//        int weekDay = 1;

        if (contractList != null) {
            for (Contract contract : contractList) {
                list = new ArrayList<Map<String, Object>>();
                list = meteringDayDao.getOverlayChartDailyData(condition, contract.getId());
//                cnt = 1;
//                weekDay = 1;
                if (list != null) {
                    
                    for (Map<String, Object> obj : list) {
//                        weekDay = scu.getDateTodayWeekNum((String)obj.get("yyyymmdd"));
                        
//                        if (cnt != weekDay) {
//                            while(cnt != weekDay || cnt > 7) {
//                                
//                            }
//                        }
                        obj.put("yyyymmdd", weekDays[scu.getDateTodayWeekNum((String)obj.get("yyyymmdd")) - 1]);

                        itr = obj.keySet().iterator();

                        while (itr.hasNext()) {
                            key = itr.next();
                            mVal = pVal.matcher(key);

                            if(mVal.find()) {
                                obj.put(key, getNullToDouble((Double)obj.get(key)));
                            }
                        }
                        
//                        datalist.add(obj);
//                        cnt++;
                    }
                }
                result.put(contract.getContractNumber(), list);
            }
        }
        return result;
    }

    /**
     * Method Name : getOverlayChartWeeklyData
     * Date : 2011. 7. 7
     * Method 설명 : Metering Data - Chart View - Overlay Chart (Weekly)
     * 
     * @param condition
     * @return
     */
    public Map<String, Object> getOverlayChartWeeklyData(Map<String, Object> condition) {
        Map<String, Object> result = new HashMap<String, Object>();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> rtnList = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();
        String[] contractNumbers = ((String)condition.get("contractNumbers")).split(",");
        List<Contract> contractList = contractNumberToContractList(contractNumbers);

        String beginDate = (String) condition.get("beginDate");
        String beginMonth = beginDate.substring(0, 6);
        SearchCalendarUtil sCaldUtil = new SearchCalendarUtil();
        List<String> weekList = sCaldUtil.getMonthToBeginDateEndDate(beginMonth);
        String weekDates = null;
        String weekBeginDate = null;
        String weekEndDate = null;
        String date = null;

        int weekListCnt = weekList.size();
        int weekBeginDay = 0;
        int weekEndDay = 0;

        if (contractList != null) {
            for (Contract contract : contractList) {
                rtnList = new ArrayList<Map<String, Object>>();
                list = new ArrayList<Map<String, Object>>();
                list = meteringMonthDao.getOverlayChartMonthlyData(condition, contract.getId());
                
                if (list != null) {
                    for (Map<String, Object> obj : list) {
                        
                        for (int i = 0 ; i < weekListCnt ; i++) {
                            map = new HashMap<String, Object>();
                        
                            weekDates = weekList.get(i);
                            weekBeginDate = weekDates.substring(0, 8);
                            weekEndDate = weekDates.substring(8, 16);
                            weekBeginDay = sCaldUtil.getDateTodayWeekNum(weekBeginDate);
                            weekEndDay = sCaldUtil.getDateTodayWeekNum(weekEndDate);
                        
                            map.put("yyyymmdd", (i+1)+"Week");
                            map.put("contractNumber", obj.get("contractNumber"));
                        
                            for (int j = 1 ; j < 8 ; j++) {
                                date = StringUtil.frontAppendNStr('0', Integer.toString(j), 2);
                                if ((i == 0 && weekBeginDay != 1 && weekBeginDay > j) || (i == (weekListCnt-1) && weekEndDay != 7 && weekEndDay < j)) {
                                    map.put("value_" + date, getNullToDouble(0.0d));
                                } else {
                                    map.put("value_" + date, getNullToDouble((Double)obj.get("value_" + date)));
                                }
                            }
                            rtnList.add(map);
                        }
                    }
                }
                result.put(contract.getContractNumber(), rtnList);
            }
        }
        
        return result;
    }

    /**
     * Method Name : getOverlayChartMonthlyData
     * Date : 2011. 7. 7
     * Method 설명 : Metering Data - Chart View - Overlay Chart (Monthly)
     * 
     * @param condition
     * @return
     */
    public Map<String, Object> getOverlayChartMonthlyData(Map<String, Object> condition) {
        Map<String, Object> result = new HashMap<String, Object>();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        String supplierId = (String)condition.get("supplierId");
        String[] contractNumbers = ((String)condition.get("contractNumbers")).split(",");
        List<Contract> contractList = contractNumberToContractList(contractNumbers);

        Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));
        String lang = supplier.getLang().getCode_2letter();
        String country = supplier.getCountry().getCode_2letter();

        // formatting 할 항목 key 패턴 정의
        String regVal = ".*value_.*";

        Pattern pVal = Pattern.compile(regVal);

        Matcher mVal = null;
        String key      = null;
        Iterator<String> itr = null;

        if (contractList != null) {
            for (Contract contract : contractList) {
                list = new ArrayList<Map<String, Object>>();
                list = meteringMonthDao.getOverlayChartMonthlyData(condition, contract.getId());
                
                if (list != null) {
                    for (Map<String, Object> obj : list) {
                        itr = obj.keySet().iterator();

                        while (itr.hasNext()) {
                            key = itr.next();
                            mVal = pVal.matcher(key);

                            if(mVal.find()) {
                                obj.put(key, getNullToDouble((Double)obj.get(key)));
                            }
                        }
                        
                        obj.put("yyyymmdd", TimeLocaleUtil.getLocaleYearMonth((String)obj.get("yyyymm"), lang, country));
                    }
                }
                result.put(contract.getContractNumber(), list);
            }
        }
        return result;
    }
    
    /*
     * 조회 조건을 생성함
     */
//    private HashMap<String, Object> setArrayToCondition (String[] values, String type) {
//        HashMap<String, Object> resultHm = new HashMap<String, Object>();
//
//        String beginDate = values[0];
//        String endDate = values[1];
////        String year = startDate.substring(0, 4);
//        int channel = Integer.parseInt(values[5]);
//        String supplierId = values[6];
//
////        resultHm.put("year", year);
//        resultHm.put("beginDate", beginDate);
//        resultHm.put("endDate", endDate);
//        resultHm.put("channel", channel);
//        resultHm.put("meterType", type);
//        resultHm.put("supplierId", supplierId);
//        return resultHm;
//    }
    
    /*
     * contractNumber로 contractId를 추출한다.
     */
//    private Integer[] contractNumberToContractId(String [] contractNumber) {
//        List<Contract> contract = new ArrayList<Contract>();
//        Integer[] result = new Integer[contractNumber.length];
//
//        if( contractNumber.length > 0) {
//            Set<Condition> set = new HashSet<Condition>();
//            Condition cdt1 = new Condition("contractNumber", contractNumber, null, Restriction.IN);
//            set.add(cdt1);  
//            Condition cdt2 = new Condition("id", null, null, Restriction.ORDERBY);
//            set.add(cdt2);
//            contract = contractDao.getContractByListCondition(set);
//
//            int i=0;
//            Iterator<Contract> it = contract.iterator();
//            while (it.hasNext()) {
//                Contract ctrt = it.next();
//                result[i] = ctrt.getId();
//                i++;
//            }
//        }
//        return result;
//    }

    /*
     * contractNumber 로 Contract List 를 추출한다.
     */
    private List<Contract> contractNumberToContractList(String [] contractNumber) {
        List<Contract> contract = new ArrayList<Contract>();

        if( contractNumber.length > 0) {
            Set<Condition> set = new HashSet<Condition>();
            Condition cdt1 = new Condition("contractNumber", contractNumber, null, Restriction.IN);
            set.add(cdt1);  
            Condition cdt2 = new Condition("id", null, null, Restriction.ORDERBY);
            set.add(cdt2);
            contract = contractDao.getContractByListCondition(set);
//            int i=0;
//            Iterator<Contract> it = contract.iterator();
//            while (it.hasNext()) {
//                Contract ctrt = it.next();
//                result[i] = ctrt.getId();
//                i++;
//            }
        }
        return contract;
    }

    /*
     * Double 형 데이터를 소스 4째자리까지 Double 형으로 표시
     */
    private Double getNullToDouble(Double value) {
        Double result = 0D;
        String strValue = "0";
        if (value != null) {
            strValue = String.format("%.4f", value);
            result = Double.parseDouble(strValue);
        }

        return result;
    }

    /**
     * Locale formatting 된 00시 ~ 23시 시간형식 리스트를 조회한다.
     * 
     * @param supplierId
     * @return
     */
    public List<String> getLocaleAllHours(String supplierId) {
        List<String> allHours = new ArrayList<String>();
        Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));
        String lang = supplier.getLang().getCode_2letter();
        String country = supplier.getCountry().getCode_2letter();

        for (int i = 0 ; i < 24 ; i++) {
            allHours.add(TimeLocaleUtil.getLocaleHour(StringUtil.frontAppendNStr('0', Integer.toString(i), 2), lang, country));
        }
        
        return allHours;
    }

    /**
     * Locale formatting 된 요일 전체 리스트를 조회한다.
     * 
     * @param supplierId
     * @return
     */
    public List<String> getLocaleAllWeekDays(String supplierId) {
        List<String> allWeekDays = new ArrayList<String>();
        Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));
        String lang = supplier.getLang().getCode_2letter();
        String country = supplier.getCountry().getCode_2letter();

        allWeekDays = TimeLocaleUtil.getLocaleWeekDayList(lang, country);
        return allWeekDays;
    }

}
