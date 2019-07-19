package com.aimir.schedule.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.aimir.dao.device.MeterDao;
import com.aimir.dao.mvm.BillingMonthEMDao;
import com.aimir.dao.mvm.RealTimeBillingEMDao;
import com.aimir.dao.mvm.SAPDao;
import com.aimir.fep.meter.data.BillingData;
import com.aimir.fep.meter.parser.MX2Table.SAPWrap;
import com.aimir.model.mvm.BillingEM;
import com.aimir.model.mvm.BillingMonthEM;
import com.aimir.model.mvm.SAP;
import com.aimir.util.Condition;

/**
 * mx2 미터 스팩에 정의된 SAP 파일을 출력하는 기능.
 * @author kskim
 *
 */
@Component
@Qualifier("MX2")
@Scope("prototype")
public class SAPFileMX2 extends SAPFileOutputUtil {
	private static Log log = LogFactory.getLog(SAPFileMX2.class);
	
	@Autowired
	MeterDao meterDao;
	
	@Autowired
	RealTimeBillingEMDao realBillDao;
	
	@Autowired
	BillingMonthEMDao billingMonthEMDao;
	
	@Autowired
	SAPDao sapDAO;
	
	@Autowired
	SessionFactory sessionFactory;

	
	private StringBuffer errorMsg = new StringBuffer();
	
	@Override
	public boolean isError() {
		if("".equals(errorMsg.toString()))
			return false;
		else
			return true;
	}

	@Override
	public String getErrorMsg() {
		return errorMsg.toString();
	}
	
	@Override
	public byte[] build(SAPFileOutputCondition condition) {
		if(condition==null)
			return null;
		
		boolean validate = validationConditions(condition);
		
		if(validate){
			//sap 파일을 만들기 위한 데이터를 조회해서 가져와 미터별로 목록 화한다.
			String[] meterList = condition.getMeterSerials();
			Map<String,BillingData> currentData = getBillingData(condition,"RealTimeBillingEM");
			Map<String,BillingData> billingData = getBillingData(condition,"BillingMonthEM");
			Map<String,SAP> sapInfos = getSAPInfo(condition); 
			
			
			String[] ableMeterList = validationData(meterList,currentData,billingData,sapInfos,condition);
			
			
			ByteArrayOutputStream bis = new ByteArrayOutputStream();
			byte[] resultData = null;
			byte[] rtnStr = "\r\n".getBytes();
			
			
			for (String meter : ableMeterList) {
				
				//sap 포멧을 변환
				SAPWrap sapw = new SAPWrap();
				sapw.setSap(sapInfos.get(meter));
				sapw.setCurrBilling(currentData.get(meter));
				sapw.setBilling(billingData.get(meter));
				
				//바이트 배열에 나열.
				try {
					bis.write(sapw.toSAPFormat().getBytes());
					bis.write(rtnStr);
				} catch (Exception e) {
					log.error(e,e);
					errorMsg.append(e.getMessage());
					return null;
				}
			}
			try {
				bis.flush();
				resultData = bis.toByteArray();
			} catch (IOException e) {
				log.error(e,e);
				errorMsg.append(e.getMessage());
				return null;
			}finally{
				try {
					bis.close();
				} catch (IOException e) {
					log.error(e,e);
				}
			}
			return resultData;
		}
		return null;
	}
	
	/**
	 * 미터당 3개의 데이터가 올바르게 존제하는 확인하고, 오류 로깅 및 이상없는 목록을 리턴한다.
	 * @param meterList
	 * @param currentData
	 * @param billingData
	 * @param sapInfos
	 * @return 3개의 데이터가 이상없이 모두 있는 미터 시리얼 목록
	 */
	private String[] validationData(String[] meterList,
			Map<String, BillingData> currentData,
			Map<String, BillingData> billingData, Map<String, SAP> sapInfos,
			SAPFileOutputCondition condition) {
		
		List<String> ableList = new ArrayList<String>();
		
		for (String meter : meterList) {

			boolean error = false;

			if (!sapInfos.containsKey(meter)) {
				errorMsg.append(String.format(
						"Meter[%s] is not found SAP Infomation\n", meter));
				error = true;
			}else {
				
				//필요한 데이터가 있는지 확인
				SAP sap = sapInfos.get(meter);
				if(!(sap.getMeaNumber()!=null &&
						sap.getSaveTime()!=null &&
						sap.getMultiplier()!=null &&
						sap.getErrorCode()!=null)){
					errorMsg.append(String.format(
							"Meter[%s] is not found SAP Infomation\n", meter));
					error = true;
				}
			}

			if (!currentData.containsKey(meter)) {
				errorMsg.append(String.format(
						"Meter[%s] is not found Current Billing Data\n", meter));
				error = true;
			}

			if (!billingData.containsKey(meter)) {
				errorMsg.append(String.format(
						"Meter[%s] is not found Billing Data in [%s ~ %s]\n",
						meter, condition.getDateRange()[0],
						condition.getDateRange()[1]));
				error = true;
			}

			if (error) {
				// 실패 목록에 추가.
				this.addFailList(meter);
				// 데이터 없을시 skip
				continue;
			}else{
				ableList.add(meter);
			}
		}
		return ableList.toArray(new String[0]);
	}

	/**
	 * sap 정보를 조회한다.
	 * @param condition
	 * @return key값에 mdevId(meter serial) 값이 들어간다
	 */
	private Map<String, SAP> getSAPInfo(SAPFileOutputCondition condition) {
		Map<String,SAP> resultData = new HashMap<String,SAP>();
		
		//mdevid 조건으로 검색.
		Set<Condition> conditions = new HashSet<Condition>();
		conditions.add(new Condition("meter",new String[]{"m"},null,Condition.Restriction.ALIAS));
		conditions.add(new Condition("m.mdsId",condition.getMeterSerials(),null,Condition.Restriction.IN));
		
		//조회 결과.
		List<SAP> saps = sapDAO.findByConditions(conditions);
		
		// 조회 결과를 map 데이터로 변환
		for (SAP sap : saps) {
			resultData.put(sap.getMeter().getMdsId(), sap);
		}

		return resultData;
	}

	/**
	 * billing data 를 조회한다.
	 * @param condition
	 * @return key값에 mdevId(meter serial) 값이 들어간다
	 */
	private Map<String, BillingData> getBillingData(
			SAPFileOutputCondition condition,String schemaName) {
		Map<String,BillingData> resultData = new HashMap<String,BillingData>();
		
		//SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
		
		final String mthHql = "FROM "+schemaName +" WHERE id.mdevId=:MDEVID AND writeDate IS NOT NULL ORDER BY writeDate DESC";
		//최근 데이터 불러오기
		
		Session session = null;
		try{
			session = sessionFactory.openSession();
			
			for (String mdsId : condition.getMeterSerials()) {

				Query query = session.createQuery(mthHql);
				query.setMaxResults(1);
				query.setString("MDEVID", mdsId);
				List<BillingEM> biem = query.list();

				if (biem != null && biem.size() > 0) {
					BillingEM em = biem.get(0);
					BillingData billingData = new BillingData();
					try {
						BeanUtils.copyProperties(billingData, em);
						billingData.setBillingTimestamp(em.getYyyymmdd()
								+ em.getHhmmss());
						billingData.setBillingTimestamp(em.getYyyymmdd()+em.getHhmmss());
						resultData.put(em.getId().getMDevId(), billingData);
					} catch (Exception e) {
						log.error(e, e);
						return null;
					}
				}
				
				try{
					session.clear();
				}catch(Exception e){
					
					try{
						session.close();
					}catch(Exception _e){
					}finally{
						session = sessionFactory.openSession();
					}
				}
				
			}
			
		}catch(Exception e){
			log.error(e,e);
			return null;
		}finally{
			if(session!=null){
				try{
					session.clear();
					session.close();
				}catch(Exception e){
					log.error(e,e);
				}
			}
		}
		
		return resultData;
	}

	/**
	 * current billing data 를 조회한다.
	 * @param condition
	 * @return key값에 mdevId(meter serial) 값이 들어간다
	 */
//	@Transactional
//	private Map<String,BillingData> getCurrentData(SAPFileOutputCondition condition) {
//		Map<String,BillingData> resultData = new HashMap<String,BillingData>();
//		
//		//단일 미터별로 조회한다. 가장 최근 데이터만 읽어와야 하기때문에.
//		for (String strMeter : condition.getMeterSerials()) {
//			Set<Condition> conditions = new HashSet<Condition>();
//			conditions.add(new Condition("id.mdevId",new String[]{strMeter},null,Condition.Restriction.EQ));//mdevid 조건으로 검색.
//			conditions.add(new Condition("id.yyyymmdd",null,null,Condition.Restriction.ORDERBYDESC));//yyyymmdd로 정력하면 가장 최근 데이터가 먼저 검색된다.
//			conditions.add(new Condition(null,new Integer[]{1},null,Condition.Restriction.MAX)); // 조회 데이터 limit 설정
//			
//			List<RealTimeBillingEM> resultBilling = realBillDao.findByConditions(conditions);
//			
//			//조회 결과를 map 데이터로 변환
//			if(resultBilling!=null && resultBilling.size()>0){
//				RealTimeBillingEM bill = resultBilling.get(0);
//				BillingData billingData = new BillingData();
//				try {
//					BeanUtils.copyProperties(billingData, bill);
//					billingData.setBillingTimestamp(bill.getYyyymmdd()+bill.getHhmmss());
//					resultData.put(bill.getId().getMDevId(), billingData);
//				} catch (Exception e) {
//					log.error(e,e);
//					return null;
//				}
//			}
//		}
//		
//		return resultData;
//	}
	
	/**
	 * 필요한 조회 조건이 모두 있는지 확인.
	 * @param condition
	 * @return
	 */
	private boolean validationConditions(SAPFileOutputCondition condition) {
		if(condition.getMeterSerials() != null &&
				condition.getDateRange() !=null &&
				condition.getDateRange().length >= 2){
			return true;
		}
		return false;
	}

}
