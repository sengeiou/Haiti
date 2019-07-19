package com.aimir.service.system.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.dao.system.ContractCapacityDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.model.system.ContractCapacity;
import com.aimir.service.system.ContractEnergyPeakDemandManager;
import com.aimir.util.DecimalUtil;
import com.aimir.util.TimeUtil;

@WebService(endpointInterface = "com.aimir.service.system.ContractEnergyPeakDemandManager")
@Service(value="contractEnergyPeakDemandManager")
@Transactional(readOnly=false)
public class ContractEnergyPeakDemandManagerImpl implements ContractEnergyPeakDemandManager{

    Log logger = LogFactory.getLog(ContractEnergyPeakDemandManagerImpl.class);

	@Autowired
	LocationDao locationDao;
	
	@Autowired
	ContractCapacityDao contractCapacityDao;
	
	@SuppressWarnings("unchecked")
	public List getEnergyPeakDemandCombo() {
			
		Map<String,Object> resultMap = new HashMap();
			// Gauge Chart contractCapacity 에서 조회해야함
			List<Object> capaticyList = contractCapacityDao.contractEnergyCombo();
			
			List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
			Integer prevId = -1;
			
			/**********/
			int a =0;
			/**********/
			
			for (Object result:capaticyList) {
				Map<String,Object> tmp = (Map<String,Object>)result;
				resultMap = new HashMap();
				Integer id =  ((Number)tmp.get("ID")).intValue();
				String name =  (String)tmp.get("NAME");
				if (id.equals(prevId)) {
					resultList.get(resultList.size() - 1).put("name", resultList.get(resultList.size() - 1).get("name") + "," + name);
				} else {
					resultMap.put("id", id);
					resultMap.put("name", name);
					resultList.add(resultMap);
				}
				prevId = id;
				
				/**********/
				a++;
				if( a > 2 ){
					break;
				}
				/**********/
				
			}
			return resultList;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> getEnergyPeakDemand(Map<String, Object> condition) {
		
		Properties prop = new Properties();
		try {
			prop.load(getClass().getClassLoader().getResourceAsStream("bems_charge.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Integer lpInterval = Integer.parseInt(prop.getProperty("peak.demand.interval").trim());
		Integer durationHour = Integer.parseInt(prop.getProperty("peak.demand.duration.hour").trim()) - 1;
		String patternNum = prop.getProperty("pattern.format.usage");
		
		DecimalFormat df = new DecimalFormat("00");
		DecimalFormat df2 = new DecimalFormat(patternNum);
		
		String today = TimeUtil.getCurrentTimeMilli(); //yyyymmddhhmmss
		Integer currMinute = Integer.parseInt(today.substring(10, 12));
		String contractCapacityId = (String)condition.get("contractCapacityId");
		
		List<Map> resultList = new ArrayList<Map>();
		Map<String, Object> energyPeakDemandMap = new HashMap<String, Object>();
		List<Object> multipleChartData = new ArrayList<Object>();
		Map<String, Object> gaugeChartData = new HashMap<String, Object>();
		
		try {
			// Gauge Chart contractCapacity 에서 조회해야함
			ContractCapacity daoResultGauge = contractCapacityDao.get(Integer.parseInt(contractCapacityId));
			
			// Gauge Chart contractCapacity dao Hybernate 호출 구현
			Double capacity = daoResultGauge.getCapacity();
			
			// Multiple Chart datatype setting & drawing
			Map<String, Object> result;
			
			condition.put("durationHour", durationHour);
			condition.put("locType", "parent");			
			List<Object> daoResultList = contractCapacityDao.contractEnergyPeakDemand(condition);
			
			boolean isNull = true;
			if(daoResultList != null && daoResultList.size() != 0) {
				for (Object obj : daoResultList) {
					result = new HashMap<String, Object>();
					Object[] objs = (Object[])obj;					
					int len = objs.length;
					for (int k = 0; k < len; k++) {
						result.put(df.format(k), objs[k]);
						if(isNull) {
							if(objs[k] != null && !String.valueOf(objs[k]).equals("null") ) {
								isNull = false;
							}
						}
					}
					resultList.add(result);
				}
			}
			
			if(isNull) {
				condition.put("locType", "child");
				daoResultList = contractCapacityDao.contractEnergyPeakDemand(condition);
				
				resultList.clear();				
				for (Object obj : daoResultList) {
					result = new HashMap<String, Object>();
					Object[] objs = (Object[])obj;
					int len = objs.length;
					for (int k = 0; k < len; k++) {
						result.put(df.format(k), objs[k]);
					}
					resultList.add(result);
				}
			}
			
			boolean flag = true;
			Map<String, Object> lastHh = new HashMap<String, Object>();
			lastHh.put("amount", "0.0");
			lastHh.put("percent", "0");
			
			Map<String, Object> resultMap = null;
			for (int k = 0; k < resultList.size() ; k++) {
				
				result = resultList.get(k);
			
				for (int j = 0; j < 60; j = j + lpInterval) {
					
					resultMap = new HashMap<String, Object>();
					
					String hh = df.format(j);
					Double amount = 0.0;
					if (k == resultList.size() - 1 && j > currMinute) {
						amount = 0.0;
					} else {
						// 15분 데이타에 *4를 한다. 추후에는 15분 Max 데이타에 * 4를 해야한다.
						amount = result.get(hh) == null ? 0.0 : DecimalUtil.ConvertNumberToDouble(result.get(hh)) * 4;
						amount = Double.parseDouble(df2.format(amount));
						//amount = result.get(hh) == null ? 0.0 : Double.valueOf(String.valueOf( result.get(hh) ) );
					}
					
					BigDecimal percent = null;
					if (amount == 0.0) {
						percent = new BigDecimal(0);
					} else {
						//계약전력량 단위(kWh)기준으로 했을때 percent calculate : past=100, as-is=0.01, to-be=?
						percent = new BigDecimal(amount).divide(new BigDecimal(capacity), MathContext.DECIMAL128).multiply(new BigDecimal(100));
					}
	
					percent = percent.setScale(2, BigDecimal.ROUND_DOWN);//소수점 2자리
					
					if(j == 0)
						resultMap.put("hh", String.valueOf(result.get("61")).substring(8) + ":"  +  df.format(j));
					else
						resultMap.put("hh", "");
					resultMap.put("amount", amount.toString());
					resultMap.put("percent", percent.toString());
					multipleChartData.add(resultMap);
									
					if(k == 0 && j == 0){
						lastHh = (Map<String,Object>)multipleChartData.get(multipleChartData.size() - 1);
					} else if(amount > 0) {
						lastHh = new HashMap<String, Object>();
						lastHh.put("amount", amount.toString());
						lastHh.put("percent", percent.toString());
					}
				}
			
			}
			
			if(multipleChartData.isEmpty()) {
				for(int i = durationHour ; i >= 0 ; i-- ) {
					SimpleDateFormat dateFormat = new SimpleDateFormat("HH");
					Calendar c = Calendar.getInstance();
					c.set(Calendar.HOUR_OF_DAY, c.get(Calendar.HOUR_OF_DAY) - i);
					
					for (int j = 0; j < 60; j = j + lpInterval) {
						resultMap = new HashMap<String, Object>();
						if(j == 0)
							resultMap.put("hh", dateFormat.format(c.getTime()) + ":"  +  df.format(j));
						else
							resultMap.put("hh", "");
						resultMap.put("amount", "0.0");
						resultMap.put("percent", "0");
						multipleChartData.add(resultMap);
					}
				}
				
			}
			
			// 게이지 값 설정
			gaugeChartData.put("threshold1", daoResultGauge.getThreshold1());
			gaugeChartData.put("threshold2", daoResultGauge.getThreshold2());	
			gaugeChartData.put("threshold3", daoResultGauge.getThreshold3());	
			gaugeChartData.put("lastAmount", lastHh.get("amount"));
			gaugeChartData.put("lastPercent", lastHh.get("percent"));
			gaugeChartData.put("useAmount", lastHh.get("amount"));
			gaugeChartData.put("capacity", Double.toString(capacity));
			
			gaugeChartData.put("maxPercent", capacity);		
			
			energyPeakDemandMap.put("gauge", gaugeChartData);
			energyPeakDemandMap.put("column", multipleChartData);
			
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
			String currDateTime = formatter.format(new Date());
			energyPeakDemandMap.put("currentDateTime", currDateTime);
			
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return energyPeakDemandMap;
	}
	
	public void updateThreshold(Map<String, Object> condition) {

		String contractCapacityId = (String)condition.get("contractCapacityId");  
		String threshold1	= (String)condition.get("threshold1");
		String threshold2	= (String)condition.get("threshold2");
		String threshold3	= (String)condition.get("threshold3");
		
		ContractCapacity entity = contractCapacityDao.get(Integer.parseInt(contractCapacityId));
		entity.setThreshold1(Double.parseDouble("0"));
		entity.setThreshold2(Double.parseDouble(threshold2));
		entity.setThreshold3(Double.parseDouble(threshold3));
		
		contractCapacityDao.update(entity);
		
	}


	@Override
	public Map<String, Object> getEnergyPeakDemandByParam(
			String contractCapacityId, String startDate, String endDate) {
		Map<String, Object> condition = new HashMap<String, Object>();
	    condition .put("contractCapacityId", contractCapacityId);
	    condition .put("startDate", startDate);
	    condition .put("endDate", endDate);
	    return getEnergyPeakDemand(condition);
	}

	@Override
	public void updateThresholdByParam(String contractCapacityId,
			String threshold1, String threshold2, String threshold3) {
		Map<String, Object> condition = new HashMap<String, Object>();
	    condition .put("contractCapacityId", contractCapacityId);
	    condition .put("threshold1", threshold1);
	    condition .put("threshold2", threshold2);
	    condition .put("threshold3", threshold3);
	    updateThreshold(condition);		
	}	
}
