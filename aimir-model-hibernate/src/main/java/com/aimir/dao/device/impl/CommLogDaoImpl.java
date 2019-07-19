package com.aimir.dao.device.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.constants.CommonConstants.DateType;
import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.device.CommLogDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.model.device.CommLog;
import com.aimir.model.device.CommLogChartVO;
import com.aimir.model.system.Code;
import com.aimir.model.system.Supplier;
import com.aimir.util.CommonUtils2;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.SQLWrapper;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeLocaleUtil;

@Repository(value = "commlogDao")
public class CommLogDaoImpl extends AbstractHibernateGenericDao<CommLog, Long> implements CommLogDao {

    protected static Log logger = LogFactory.getLog(CommLogDaoImpl.class);

	@Autowired
	SupplierDao supplierDao;
	
	@Autowired
	protected CommLogDaoImpl(SessionFactory sessionFactory) {
		super(CommLog.class);
		super.setSessionFactory(sessionFactory);
	}

	@SuppressWarnings("rawtypes")
    public List<CommLogChartVO> getReceivePieChartData(Map<String, String> conditionMap) {
		
		//FIXME 추후 네이티브 쿼리 -> HQL로 수정 작업 필요
		StringBuilder sbQuery = new StringBuilder();
		sbQuery.append("SELECT comm.svcTypeCode.code, sum(comm.rcvBytes) ");
		sbQuery.append("FROM CommLog comm ");
		sbQuery.append("WHERE 1=1 ");
		sbQuery.append(makeQueryCondition(conditionMap, ""));
		sbQuery.append("GROUP BY comm.svcTypeCode.code ");
		
		Query query = getSession().createQuery(sbQuery.toString());
		setQueryCondition(query, conditionMap, "");
		
		List result = query.list();	
		
		List<CommLogChartVO> commLogChartVOs = new ArrayList<CommLogChartVO>();		
		Object[] resultData = null;		
		String svcTypeCode = null;
		int sum = 0;
		
		int ondemand = 0;
		int event = 0;
		int metering = 0;
		
//	 	<system.Code code="4.9" name="SVC Field" descr="" order="9" parent="4"/>
//		  <system.Code code="4.9.1" name="Command Service" descr="(C)" order="1" parent="4.9"/>
//		  <system.Code code="4.9.2" name="Alarm & Event Delivery Service" descr="(A,E)" order="2" parent="4.9"/>
//		  <system.Code code="4.9.3" name="Measurement Metering Data Delivery Service" descr="(M,N,S)" order="3" parent="4.9"/>
//		  <system.Code code="4.9.4" name="Partial Frame" descr="윈도우 전송시 분할 전송 프레임(P)" order="4" parent="4.9"/>
//		  <system.Code code="4.9.5" name="Data File" descr="검침 데이터 파일(D)" order="5" parent="4.9"/>
		
		for(int i = 0, size = result.size() ; i < size ; i++) {
			
			resultData = (Object[])result.get(i);
			svcTypeCode = resultData[0].toString();
			
			if(resultData[1] != null)
				sum = Integer.parseInt(resultData[1].toString());
			
			if("4.9.M".equals(svcTypeCode) || "4.9.N".equals(svcTypeCode) || "4.9.S".equals(svcTypeCode)) {
				metering += sum;
			} else if("4.9.A".equals(svcTypeCode) || "4.9.E".equals(svcTypeCode)) {
				event += sum;
			} else {
				ondemand += sum;
			}
		}
		
		CommLogChartVO meteringVO = new CommLogChartVO();
		meteringVO.setSvcTypeCode("M");
		meteringVO.setCnt(Integer.toString(metering));
		
		CommLogChartVO ondemandVO = new CommLogChartVO();
		ondemandVO.setSvcTypeCode("O");
		ondemandVO.setCnt(Integer.toString(ondemand));
		
		CommLogChartVO eventVO = new CommLogChartVO();
		eventVO.setSvcTypeCode("E");
		eventVO.setCnt(Integer.toString(event));	
		
		commLogChartVOs.add(meteringVO);
		commLogChartVOs.add(ondemandVO);
		commLogChartVOs.add(eventVO);
		
		return commLogChartVOs;
	}

	@SuppressWarnings("unchecked")
    public List<Map<String, Object>> getReceivePieChart(Map<String, String> conditionMap) {

	    StringBuilder sbQuery = new StringBuilder();
	    sbQuery.append("SELECT comm.svcTypeCode.code, SUM(comm.rcvBytes) ");
	    sbQuery.append("FROM CommLog comm ");
	    sbQuery.append("WHERE 1=1 ");
	    sbQuery.append(makeQueryCondition(conditionMap, ""));
	    sbQuery.append("GROUP BY comm.svcTypeCode.code ");

	    Query query = getSession().createQuery(sbQuery.toString());
	    setQueryCondition(query, conditionMap, "");

	    List<Object[]> result = query.list();

	    List<Map<String, Object>> commLogChartVOs = new ArrayList<Map<String, Object>>();
	    Map<String, Object> map = null;
	    Object[] resultData = null;     
	    String svcTypeCode = null;
	    int sum = 0;

	    int ondemand = 0;
	    int event = 0;
	    int metering = 0;
	    int dataCount = result.size();

	    for(int i = 0 ; i < dataCount ; i++) {
	        resultData = (Object[])result.get(i);
	        svcTypeCode = resultData[0].toString();

	        if(resultData[1] != null)
	            sum = Integer.parseInt(resultData[1].toString());

	        if("4.9.M".equals(svcTypeCode) 
	        		|| "4.9.N".equals(svcTypeCode) 
	        		|| "4.9.S".equals(svcTypeCode)
	        		|| "4.9.D".equals(svcTypeCode)
	        		|| "4.9.P".equals(svcTypeCode)) {
	            metering += sum;
	        } else if("4.9.A".equals(svcTypeCode) || "4.9.E".equals(svcTypeCode)) {
	            event += sum;
	        } else {
	            ondemand += sum;
	        }
	    }

	    if (dataCount > 0) {
	        map = new HashMap<String, Object>();
	        map.put("svcTypeCode", "M");
	        map.put("cnt", Integer.toString(metering));
	        commLogChartVOs.add(map);

	        map = new HashMap<String, Object>();
	        map.put("svcTypeCode", "O");
	        map.put("cnt", Integer.toString(ondemand));
	        commLogChartVOs.add(map);

	        map = new HashMap<String, Object>();
	        map.put("svcTypeCode", "E");
	        map.put("cnt", Integer.toString(event));
	        commLogChartVOs.add(map);
	    }

	    return commLogChartVOs;
	}

	@SuppressWarnings("rawtypes")
    public List<CommLogChartVO> getBarChartData(Map<String, String> conditionMap) {

		String groupByDate = makeGroupByCondition(conditionMap.get("period"), "comm.");
		
		StringBuffer sbQuery = new StringBuffer()
		.append(" SELECT " + groupByDate +", sum(comm.sendBytes), sum(comm.rcvBytes) \n")
		.append("   FROM CommLog comm                                               \n")
		.append("  WHERE comm.id is not null                                        \n")
		.append( makeQueryCondition(conditionMap, "")                                 )
		.append("  GROUP BY " + groupByDate +                                     "\n");		

		Query query = getSession().createQuery(sbQuery.toString());
		setQueryCondition(query, conditionMap, "");
		
		List result = query.list();	
		
		List<CommLogChartVO> commLogChartVOs = new ArrayList<CommLogChartVO>();
		CommLogChartVO commLogChartVO = new CommLogChartVO();
		
		Object[] resultData = null;		
		
		String date = null;
		String sendBytes = null;
		String rcvBytes = null;
				
		for(int i = 0, size = result.size() ; i < size ; i++) {

			resultData = (Object[])result.get(i);

			if(resultData[0] == null) continue;

			date = resultData[0].toString();
			sendBytes = resultData[1].toString();
			rcvBytes = resultData[2].toString();

			commLogChartVO = new CommLogChartVO();
            //jhkim
            Supplier supplier = null;
            if(!conditionMap.get("supplierId").equals("")) {
                supplier = supplierDao.get(Integer.parseInt(conditionMap.get("supplierId")));
                commLogChartVO.setDate(
                        TimeLocaleUtil.getLocaleDate(StringUtil.nullToBlank(date) , supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter())
                );
            }else{
                commLogChartVO.setDate(date);
            }
			commLogChartVO.setSendCnt(sendBytes);
			commLogChartVO.setRcvCnt(rcvBytes);	
			commLogChartVOs.add(commLogChartVO);
		}
		
		return commLogChartVOs;
	}

    public List<Map<String, Object>> getBarChart(Map<String, String> conditionMap) {

        String groupByDate = makeGroupByCondition(conditionMap.get("period"), "comm.");

        StringBuilder sbQuery = new StringBuilder();
        sbQuery.append("SELECT ").append(groupByDate).append(", SUM(comm.sendBytes), SUM(comm.rcvBytes) \n");
        sbQuery.append("FROM CommLog comm \n");
        sbQuery.append("WHERE 1=1 \n");
        sbQuery.append(makeQueryCondition(conditionMap, ""));
        sbQuery.append("GROUP BY ").append(groupByDate).append(" \n"); //.append(" , comm.endTime");
        
        
        //sbQuery.append("  order by comm.endTime desc");

        Query query = getSession().createQuery(sbQuery.toString());
        setQueryCondition(query, conditionMap, "");

        @SuppressWarnings("rawtypes")
		List result = query.list();

        List<Map<String, Object>> commLogChartVOs = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = null;

        Object[] resultData = null;

        String date = null;
        String sendBytes = null;
        String rcvBytes = null;

        for (int i = 0, size = result.size(); i < size; i++) {
            resultData = (Object[]) result.get(i);

            if (resultData[0] == null)
                continue;

            date = resultData[0].toString();
            sendBytes = resultData[1].toString();
            rcvBytes = resultData[2].toString();

            map = new HashMap<String, Object>();

//            if (!conditionMap.get("supplierId").equals("")) {
//                map.put("date", TimeLocaleUtil.getLocaleDate(StringUtil.nullToBlank(date), supplier.getLang()
//                        .getCode_2letter(), supplier.getCountry().getCode_2letter()));
//            } else {
//                map.put("date", date);
//            }
            map.put("date", date);
            map.put("sendCnt", sendBytes);
            map.put("rcvCnt", rcvBytes);
            commLogChartVOs.add(map);
        }

        return commLogChartVOs;
    }

	@SuppressWarnings("rawtypes")
    public List<CommLogChartVO> getPieChartData(Map<String, String> conditionMap) {

		StringBuffer sbQuery = new StringBuffer()
		.append(" SELECT sum(comm.sendBytes), sum(comm.rcvBytes)  \n")
		.append("   FROM CommLog comm                            \n")
		.append("  WHERE comm.id is not null                     \n")
		.append(makeQueryCondition(conditionMap, "")               );            
		
		Query query = getSession().createQuery(sbQuery.toString());
		setQueryCondition(query, conditionMap, "");

		List<CommLogChartVO> commLogChartVOs = new ArrayList<CommLogChartVO>();
		List result = query.list();
		
		if(result.size() > 0) {

			Object[] resultData = (Object[])result.get(0);
			
			CommLogChartVO sendVO = new CommLogChartVO();
			sendVO.setRcvSend("send");
			
			if(resultData[0] != null)
				sendVO.setRcvSendCnt(resultData[0].toString());
			else
				sendVO.setRcvSendCnt("0");
		
			CommLogChartVO rcvVO = new CommLogChartVO();
			rcvVO.setRcvSend("receive");

			if(resultData[1] != null)
				rcvVO.setRcvSendCnt(resultData[1].toString());
			else
				rcvVO.setRcvSendCnt("0");
			
			commLogChartVOs.add(sendVO);
			commLogChartVOs.add(rcvVO);
		}
		
		return commLogChartVOs;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<CommLog> getCommLogGridData(Map<String, String> conditionMap) {

		StringBuilder sbQuery = new StringBuilder("from CommLog comm where 1=1  ");
		
		//쿼리를 검색 조건에 맞게 만들어 주는 부분.
		sbQuery.append(this.makeQueryCondition(conditionMap, ""));
		
		sbQuery.append("order by comm.endTime desc");
			
		Query query = getSession().createQuery(sbQuery.toString());
		
		
        setQueryCondition(query, conditionMap, "");
        
            
        
        //쿼리에 페이징 처리
        query= CommonUtils2.addPagingForQuery(query, conditionMap);
        
        
        List list = query.list();
		
		return list;
		
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<Code> getPacketType()
	{


		StringBuilder sbQuery = new StringBuilder("from Code where parent.code = '4.9' order by name asc ");
		

			
		Query query = getSession().createQuery(sbQuery.toString());
		
				
		List list = query.list();
		
		return list;
		
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<Code> getSenderType()
	{


		StringBuilder sbQuery = new StringBuilder("from Code where parent.code = '4.10' order by name asc ");
		

			
		Query query = getSession().createQuery(sbQuery.toString());
		
				
		List list = query.list();
		
		return list;
		
	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<CommLog> getCommLogGridDataForExcel(Map<String, String> conditionMap) {

		StringBuilder sbQuery = new StringBuilder("from CommLog comm where 1=1 ");
		
		/*:startDate
		 :endDate
		 
		 */
		
		String search_from= conditionMap.get("search_from");
		
		String[] search_froms= search_from.split("@");
		
		/*conditionMap.put("startDate", search_froms[0]);
		conditionMap.put("endDate", search_froms[1]);
		conditionMap.put("supplierId", "22");*/
		
		
		sbQuery.append(makeQueryCondition(conditionMap, ""));
		
		sbQuery.append(" order by comm.endTime desc ");
		
			
		Query query = getSession().createQuery(sbQuery.toString());
		
		
		
		setQueryCondition(query, conditionMap, "");
		String period = conditionMap.get("period");
		if(DateType.HOURLY.getCode().equals(period)) {
			search_froms[0] = search_froms[0]+"0000";
			search_froms[1] = search_froms[1]+"5959";
		}
		
		query.setString("startDate", search_froms[0]);
		query.setString("endDate", search_froms[1]);
		query.setString("supplierId", conditionMap.get("supplierId"));
		
		
		
		List list = query.list();
		
		return list;
		
	}
	
	
	/**
	 * 
	 * for extJs
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<CommLog> getCommLogGridData2(Map<String, String> conditionMap) {

		StringBuilder sbQuery = new StringBuilder("from CommLog comm where 1=1  order by comm.id desc ");
		sbQuery.append(makeQueryCondition(conditionMap, ""));
			
		Query query = getSession().createQuery(sbQuery.toString());
		setQueryCondition(query, conditionMap, "");
			
		String strPage = conditionMap.get("page");
		String strPageSize = conditionMap.get("pageSize");

		int pageCommaIndex = strPage.indexOf(".");
		int pageSizeCommaIndex = strPageSize.indexOf(".");
		int page = -1;
		int pageSize = -1;
		
		if(pageCommaIndex > -1)
			page = Integer.parseInt(strPage.substring(0, pageCommaIndex));
		else
			page = Integer.parseInt(strPage);
		
		if(pageSizeCommaIndex > -1)
			pageSize = Integer.parseInt(strPageSize.substring(0, pageSizeCommaIndex));
		else
			pageSize = Integer.parseInt(strPageSize);

		int firstResult = page * pageSize;
		
		query.setFirstResult(firstResult);		
		query.setMaxResults(pageSize);
		
		List list = query.list();
		
		return list;
		
	}

	public String getCommLogGridDataCount(Map<String, String> conditionMap) {

		StringBuilder sbQuery = new StringBuilder("select count(*) from CommLog comm where 1=1 ");
		sbQuery.append(makeQueryCondition(conditionMap, ""));
			
		Query query = getSession().createQuery(sbQuery.toString());
		setQueryCondition(query, conditionMap, "");
		
		Object object = query.uniqueResult();
		
		return object.toString(); 	
	}

    @Deprecated
	public Map<String, String> getCommLogData(Map<String, String> conditionMap) {

		StringBuffer sbQuery = new StringBuffer("select sum(sendBytes), max(comm.sendBytes), min(comm.sendBytes), sum(rcvBytes), max(comm.rcvBytes), min(comm.rcvBytes) from CommLog comm where id is not null");
		sbQuery.append(makeQueryCondition(conditionMap, ""));
			
		Query query = getSession().createQuery(sbQuery.toString());
		setQueryCondition(query, conditionMap, "");
			
		List<?> list = query.list();
		Map<String, String> map = new HashMap<String, String>();
		
		if(list != null && list.size() >= 1) {
			Object[] object = (Object[])list.get(0);
			
			if(object[0] == null) {
				map.put("sendSum", "0");
				map.put("sendMax", "0");
				map.put("sendMin", "0");
				map.put("rcvSum", "0");
				map.put("rcvdMax", "0");
				map.put("rcvMin", "0");
			} else {							
				map.put("sendSum", object[0].toString());
				map.put("sendMax", object[1].toString());
				map.put("sendMin", object[2].toString());
				map.put("rcvSum", object[3].toString());
				map.put("rcvdMax", object[4].toString());
				map.put("rcvMin", object[5].toString());
			}
		} else{
			map.put("sendSum", "0");
			map.put("sendMax", "0");
			map.put("sendMin", "0");
			map.put("rcvSum", "0");
			map.put("rcvdMax", "0");
			map.put("rcvMin", "0");
		}
		
		return map;
	}

	@SuppressWarnings("rawtypes")
    public List<Map<String, String>> getLocationLineChartData(Map<String, String> conditionMap) {
		String groupByDate = makeGroupByNativeSQLCondition(conditionMap.get("period"), "comm.");
		
		//FIXME 추후 네이티브 쿼리 -> HQL로 수정 작업 필요
		StringBuffer sbQuery = new StringBuffer()
		.append(" SELECT T1.STARTDATE, T1.NAME, T1.ID, T2.bytesSum                                 \n")
		.append("   FROM (SELECT " + groupByDate + " STARTDATE, loc.NAME, loc.ID                   \n")
		.append("           FROM COMMLOG comm, LOCATION loc                                         \n")
//		.append("          WHERE comm.id is not null                                                \n")
		// TODO 수정 : 문동규
		//      내용 : 조인조건 추가
		.append("          WHERE comm.LOCATION_ID = loc.ID \n")
		.append( makeQueryNativeSQLCondition(conditionMap, "1", false))		
		.append("          GROUP BY " + groupByDate + ", loc.NAME, loc.ID                          \n")
		.append("         )  T1                                                                    \n")
		.append("   LEFT OUTER JOIN                                                                \n")
		.append("        (SELECT " + groupByDate + " STARTDATE, loc.NAME, sum(comm.RCV_BYTES) bytesSum  \n")
		.append("           FROM COMMLOG comm, LOCATION loc, Code code                              \n")
		.append("          WHERE comm.LOCATION_ID = loc.ID                                          \n")
		.append("            AND comm.SVC_TYPE_CODE = code.ID                                       \n")
		.append( makeQueryNativeSQLCondition(conditionMap, "2", true)                                       )
		.append("          GROUP BY " + groupByDate + ", loc.NAME                                  \n")
		.append("        ) T2                                                                      \n")
		.append("    ON T1.STARTDATE = T2.STARTDATE                                                \n")
		.append("   AND T1.NAME = T2.NAME                                                          \n");
		
		SQLQuery query = getSession().createSQLQuery(new SQLWrapper().getQuery(sbQuery.toString()));
		setQueryCondition(query, conditionMap, "1");
		setQueryCondition(query, conditionMap, "2");
		List result = query.list();	
		
		List<Map<String, String>> maps = new ArrayList<Map<String, String>>();
		Map<String, String> map = null;	
		Object[] resultData = null;		
		
		String date = null;
		String pastDate = "";
		String locationName = null;
		String locationId = null;
		String cnt = null;
		int locationDisplayIndex = 0;
		
		for(int i = 0, size = result.size() ; i < size ; i++) {

			resultData = (Object[])result.get(i);
			date = resultData[0].toString();
			locationName = resultData[1].toString();
			locationId = resultData[2].toString();
			if(resultData[3] != null)
				cnt = (resultData[3].toString()).trim();
			else
				cnt = "0";

			if(!pastDate.equals(date)) {
				
				if(!"".equals(pastDate)) {					
					maps.add(map);
				}
				
				pastDate = date;
				locationDisplayIndex = 1;
				map = new HashMap<String, String>();
                //jhkim
                Supplier supplier = null;
                if(!conditionMap.get("supplierId").equals("")) {
                    supplier = supplierDao.get(Integer.parseInt(conditionMap.get("supplierId")));
                    map.put("date", 
                            TimeLocaleUtil.getLocaleDate(StringUtil.nullToBlank(date) , supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter())
                    );
                }else{  
                    map.put("date", pastDate);                  
                }               
                //map.put("date", pastDate);                                
			}

			map.put("locationName_" + locationDisplayIndex, locationName);
			map.put("locationId_" + locationDisplayIndex, locationId);
			map.put("locationCnt_" + locationDisplayIndex, cnt);			

			// 마지막 정보들 목록에 포함시킴
			if(size - 1 == i) {
				map.put("locationSize", Integer.toString(locationDisplayIndex));
				maps.add(map);			
			}		
			
			locationDisplayIndex++;
		}
		
		// 사이즈가 1 일경우 그래프가 그려지지 않을 때..
		if(maps.size() == 1) {
		
			Map<String, String> tempMap = new HashMap<String, String>();
			tempMap.put("date", "0");
			
			for(int i = 1, size = Integer.parseInt(maps.get(0).get("locationSize")) ; i <= size ; i++) {
											
				tempMap.put("locationName_" + i, maps.get(0).get("locationName_" + i));
				tempMap.put("locationId_" + i, maps.get(0).get("locationId_" + i));
				tempMap.put("locationCnt_" + i, "0");								
			}
			
			maps.add(0, tempMap);
		}
		
		// 사이즈가 0 일경우 그래프가 지워지지 않을 때
		if(maps.size() == 0) {
			
			Map<String, String> tempMap1 = new HashMap<String, String>();
			tempMap1.put("date", "0");
			tempMap1.put("locationName_1", "0");
			tempMap1.put("locationId_1", "0");
			tempMap1.put("locationCnt_1", "0");								

			Map<String, String> tempMap2 = new HashMap<String, String>();
			tempMap2.put("date", "0");
			tempMap2.put("locationName_2", "0");
			tempMap2.put("locationId_2", "0");
			tempMap2.put("locationCnt_2", "0");	
			tempMap2.put("locationSize", "2");	
			
			maps.add(0, tempMap2);
		}		
		
		return maps;
	}

	@SuppressWarnings("rawtypes")
    public List<Map<String, String>> getMcuLineChartData(Map<String, String> conditionMap) {

		String groupByDate = makeGroupByNativeSQLCondition(conditionMap.get("period"), "comm.");
		String group = conditionMap.get("group");
		String groupData = conditionMap.get("groupData");
		
		//FIXME 추후 네이티브 쿼리 -> HQL로 수정 작업 필요
		StringBuffer sbQuery = new StringBuffer();
		sbQuery.append(" SELECT T1.STARTDATE, T1.SENDER_ID, T2.sumBytes \n");
		sbQuery.append("   FROM (SELECT " + groupByDate + " STARTDATE, loc.SENDER_ID \n");
		sbQuery.append("           FROM COMMLOG comm, COMMLOG loc \n");
        // TODO 수정 : 문동규
        //      내용 : 두 테이블 조인조건 추가
		sbQuery.append("          WHERE comm.location_id = loc.id \n");
		sbQuery.append( makeQueryNativeSQLCondition(conditionMap, "1", false) );
		sbQuery.append("          GROUP BY " + groupByDate + ", loc.SENDER_ID \n");
		if("mcu".equals(group) && !"".equals(groupData)) {
			sbQuery.append(" HAVING loc.SENDER_ID in (" + addDoubleQuotationMark(groupData) + ") \n");
		}
		sbQuery.append("         )  T1 \n");
		sbQuery.append("   LEFT OUTER JOIN \n");
		sbQuery.append("        (SELECT " + groupByDate + " STARTDATE, comm.SENDER_ID, sum(comm.RCV_BYTES) sumBytes \n");
		sbQuery.append("           FROM COMMLOG comm, CODE code \n");
		sbQuery.append("          WHERE comm.SVC_TYPE_CODE = code.ID \n");
		sbQuery.append( makeQueryNativeSQLCondition(conditionMap, "2", true) );
		sbQuery.append("          GROUP BY " + groupByDate + ", comm.SENDER_ID \n");
		sbQuery.append("        ) T2 \n");
		sbQuery.append("    ON T1.STARTDATE = T2.STARTDATE \n");
		sbQuery.append("   AND T1.SENDER_ID = T2.SENDER_ID \n");

		SQLQuery query = getSession().createSQLQuery(new SQLWrapper().getQuery(sbQuery.toString()));	
		setQueryCondition(query, conditionMap, "1");
		setQueryCondition(query, conditionMap, "2");
		
		List result = query.list();	
		
		List<Map<String, String>> maps = new ArrayList<Map<String, String>>();
		Map<String, String> map = null;
		Object[] resultData = null;		
		
		String date = null;
		String pastDate = "";
		String mcuId = null;
		String cnt = null;
		int mcuDisplayIndex = 0;
		
		for(int i = 0, size = result.size() ; i < size ; i++) {

			resultData = (Object[])result.get(i);
			date = (resultData[0].toString()).trim();
			mcuId = (resultData[1].toString()).trim();
						
			if(resultData[2] != null)
				cnt = (resultData[2].toString()).trim();
			else
				cnt = "0";

			if(!pastDate.equals(date)) {
				
				if(!"".equals(pastDate)) {
					maps.add(map);
				}
				
				pastDate = date;				
				mcuDisplayIndex = 1;				
				map = new HashMap<String, String>();
                
                //jhkim
                Supplier supplier = null;
                if(!conditionMap.get("supplierId").equals("")) {
                    supplier = supplierDao.get(Integer.parseInt(conditionMap.get("supplierId")));
                    map.put("date", 
                            TimeLocaleUtil.getLocaleDate(StringUtil.nullToBlank(date) , supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter())
                    );
                }else{  
                    map.put("date", pastDate);                  
                }               
                //map.put("date",pastDate);                     
			}

			map.put("mcuId_" + mcuDisplayIndex, mcuId);
			map.put("mcuCnt_" + mcuDisplayIndex, cnt);
			

			// 마지막 정보들 목록에 포함시킴
			if(size - 1 == i) {
				map.put("mcuSize", Integer.toString(mcuDisplayIndex));
				maps.add(map);			
			}				
			
			mcuDisplayIndex++;
		}
		
		if(maps.size() == 1) {
			
			Map<String, String> tempMap = new HashMap<String, String>();
			tempMap.put("date", "0");
			
			for(int i = 1, size = Integer.parseInt(maps.get(0).get("mcuSize")) ; i <= size ; i++) {
											
				tempMap.put("mcuId_" + i, maps.get(0).get("mcuId_" + i));
				tempMap.put("mcuCnt_" + i, "0");								
			}
			
			maps.add(0, tempMap);
		} else if(maps.size() == 0) {
			
			Map<String, String> tempMap = new HashMap<String, String>();
			tempMap.put("date", "0");
			tempMap.put("mcuId_" + 1, "0");
			tempMap.put("mcuCnt_" + 1, "0");	
			
			maps.add(0, tempMap);
		}
			
		
		return maps;
	}

	@SuppressWarnings("rawtypes")
    public List<CommLogChartVO> getLocationPieChartData(Map<String, String> conditionMap) {
	
		StringBuffer sbQuery = new StringBuffer()
		.append(" SELECT comm.location.name, comm.location.id, sum(comm.rcvBytes) \n")
		.append("   FROM CommLog comm \n")
		.append("  WHERE comm.id is not null \n")
		.append( makeQueryCondition(conditionMap, "") )
		.append("  GROUP BY comm.location.name, comm.location.id  \n");
		
		Query query = getSession().createQuery(sbQuery.toString());
		setQueryCondition(query, conditionMap, "");
		
		List result = query.list();	
		
		List<CommLogChartVO> commLogChartVOs = new ArrayList<CommLogChartVO>();
		CommLogChartVO commLogChartVO = null;		
		Object[] resultData = null;		
		String locationName = null;
		String locationId = null;
		String cnt = null;
				
		for(int i = 0, size = result.size() ; i < size ; i++) {
			
			resultData = (Object[])result.get(i);
			locationName = (resultData[0].toString()).trim();
			locationId = (resultData[1].toString()).trim();
			cnt = (resultData[2].toString()).trim();
			
			commLogChartVO = new CommLogChartVO();
			commLogChartVO.setLocationName1(locationName);
			commLogChartVO.setLocationId(locationId);
			commLogChartVO.setLocationCnt1(cnt);
			
			commLogChartVOs.add(commLogChartVO);
		}
		
		return commLogChartVOs;
	}

	@SuppressWarnings("rawtypes")
    public List<CommLogChartVO> getMcuPieChartData(Map<String, String> conditionMap) {

		StringBuffer sbQuery = new StringBuffer()
		.append(" SELECT comm.senderId, sum(comm.rcvBytes) \n")
		.append("   FROM CommLog comm \n")
		.append("  WHERE comm.id is not null \n")
		.append( makeQueryCondition(conditionMap, "") )
		.append("  GROUP BY comm.senderId \n");
		
		Query query = getSession().createQuery(sbQuery.toString());	
		setQueryCondition(query, conditionMap, "");
		
		List result = query.list();	
		
		List<CommLogChartVO> commLogChartVOs = new ArrayList<CommLogChartVO>();
		CommLogChartVO commLogChartVO = null;		
		Object[] resultData = null;		
		String mcuId = null;
		String cnt = null;
				
		for(int i = 0, size = result.size() ; i < size ; i++) {
			
			resultData = (Object[])result.get(i);
			mcuId = (resultData[0].toString()).trim();
			cnt = (resultData[1].toString()).trim();
			
			commLogChartVO = new CommLogChartVO();
			commLogChartVO.setMcuId1(mcuId);
			commLogChartVO.setMcuCnt1(cnt);
			
			commLogChartVOs.add(commLogChartVO);
		}
		
		return commLogChartVOs;
	}
	
	private String makeQueryCondition(Map<String, String> conditionMap, String index) {

		StringBuilder sbCondition = new StringBuilder();

		String protocolCode = conditionMap.get("protocolCode");
		String senderId = conditionMap.get("senderId");
		String receiverId = conditionMap.get("receiverId");
		String period = conditionMap.get("period");
		String group = conditionMap.get("group");
		String groupData = conditionMap.get("groupData");
		String svcTypeCode = conditionMap.get("svcTypeCode");
		String supplierId = conditionMap.get("supplierId");
		String operationCode = "";
		if(conditionMap.containsKey("operationCode")) operationCode = conditionMap.get("operationCode");

		if(protocolCode != null && !"".equals(protocolCode)) {
			sbCondition.append(" AND comm.protocolCode = :protocolCode").append(index).append(" \n");
		}

		if(senderId != null && !"".equals(senderId)) {
            //sbCondition.append(" AND comm.senderId = :senderId").append(index).append("\n");          
            sbCondition.append(" AND comm.senderId LIKE :senderId").append(index).append("\n");
        }

        if(receiverId != null && !"".equals(receiverId)) {
            //sbCondition.append(" AND comm.receiverId = :receiverId").append(index).append("\n");
            sbCondition.append(" AND comm.receiverId LIKE :receiverId").append(index).append("\n");
        }

		if(DateType.HOURLY.getCode().equals(period)) {
            sbCondition.append(" AND comm.startDateTime >= :startDate").append(index).append(" \n");
            sbCondition.append(" AND comm.startDateTime <= :endDate").append(index).append(" \n");
            // index 를 타게 하기 위해 추가.
            sbCondition.append(" AND comm.startDate BETWEEN SUBSTRING(:startDate").append(index).append(", 1, 8) AND SUBSTRING(:endDate").append(index).append(", 1, 8) \n");
//		} else if(DateType.MONTHLY.getCode().equals(period)) {
//            sbCondition.append(" AND comm.startDate >= :startDate").append(index).append(" \n");
//            sbCondition.append(" AND comm.startDate <= :endDate").append(index).append(" \n");
		} else if(DateType.PERIOD.getCode().equals(period)){
            sbCondition.append(" AND comm.startDate >= :startDate").append(index).append(" \n");
            sbCondition.append(" AND comm.startDate <= :endDate").append(index).append(" \n");
		} else {
			sbCondition.append(" AND comm.startDate >= :startDate").append(index).append(" \n");
			sbCondition.append(" AND comm.startDate <= :endDate").append(index).append(" \n");
		}		

		if("location".equals(group) && !"".equals(groupData)) {
			sbCondition.append(" AND comm.location.id in (").append(groupData).append(") \n");
		}		

		if("mcu".equals(group) && !"".equals(groupData)) {
			sbCondition.append(" AND comm.senderId in (").append(addDoubleQuotationMark(groupData)).append(") \n");
		}		

		/*
		 * Operation code 가 동일한 조건 검색을 위해 코드 추가.
		 * 2014-07-09
		 */
		if(operationCode != null && !operationCode.equals("")){
		    sbCondition.append(" AND comm.operationCode in (" + operationCode + ") \n");
		}
		
		
		
		/**
		 * 서비스 타입(페킷타입) 조건 걸어주는 부분.
		 */
		if(svcTypeCode != null && !"".equals(svcTypeCode)) 
		{			
			//Meter
			if("M".equals(svcTypeCode)) 
			{
				sbCondition.append(" AND comm.svcTypeCode.code in ('4.9.M') \n");
			}
			else if("N".equals(svcTypeCode)) 
			{
				sbCondition.append(" AND comm.svcTypeCode.code in ('4.9.N')  \n");
			}
			else if("S".equals(svcTypeCode)) 
			{
				sbCondition.append(" AND comm.svcTypeCode.code in ('4.9.S')  \n");
			}

			//alarm
			else if("A".equals(svcTypeCode)) 
			{
				sbCondition.append(" AND comm.svcTypeCode.code in ('4.9.A')  \n");
			}
		
			else if("C".equals(svcTypeCode)) 
			{
				sbCondition.append(" AND comm.svcTypeCode.code in ('4.9.C')  \n");
			}
			//dateFile
			else if("D".equals(svcTypeCode)) 
			{
				sbCondition.append(" AND comm.svcTypeCode.code in ('4.9.D') \n");
			}
			else if("E".equals(svcTypeCode)) 
			{
				sbCondition.append(" AND comm.svcTypeCode.code in ('4.9.E') \n");
			}
			else if("F".equals(svcTypeCode)) 
			{
				sbCondition.append(" AND comm.svcTypeCode.code in ('4.9.F') \n");
			}
			else if("P".equals(svcTypeCode)) 
			{
				sbCondition.append(" AND comm.svcTypeCode.code in ('4.9.P') \n");
			}
			
			else if("R".equals(svcTypeCode)) 
			{
				sbCondition.append(" AND comm.svcTypeCode.code in ('4.9.R') \n");
			}
			
			else if("T".equals(svcTypeCode)) 
			{
				sbCondition.append(" AND comm.svcTypeCode.code in ('4.9.T') \n");
			}
			
		}
				
		if(supplierId != null && !"".equals(supplierId)) {
			sbCondition.append(" AND comm.suppliedId = :supplierId").append(index).append(" \n");
		}
		
		//아이디 순으로 정렬 추가
		//sbCondition.append("  order by comm.ID desc ");
		
		return sbCondition.toString();
	}	
	
	
	private String makeQueryCondition2(Map<String, String> conditionMap, String index) {

		StringBuilder sbCondition = new StringBuilder();

		String protocolCode = conditionMap.get("protocolCode");
		String senderId = conditionMap.get("senderId");
		String receiverId = conditionMap.get("receiverId");
		String period = conditionMap.get("period");
		String group = conditionMap.get("group");
		String groupData = conditionMap.get("groupData");
		String svcTypeCode = conditionMap.get("svcTypeCode");
		String supplierId = conditionMap.get("supplierId");

		if(protocolCode != null && !"".equals(protocolCode)) {
			sbCondition.append(" AND comm.protocolCode = :protocolCode").append(index).append(" \n");
		}

		if(senderId != null && !"".equals(senderId)) {
			sbCondition.append(" AND comm.senderId = :senderId").append(index).append("\n");
		}

		if(receiverId != null && !"".equals(receiverId)) {
			sbCondition.append(" AND comm.receiverId = :receiverId").append(index).append(" \n");
		}

		//if(DateType.HOURLY.getCode().equals(period))
		if( period.equals("HOURLY"))
		{
			//hourly 일 경우..
            sbCondition.append(" AND comm.startDateTime >= :startDate").append(index).append(" \n");
            sbCondition.append(" AND comm.startDateTime <= :endDate").append(index).append(" \n");
            // index 를 타게 하기 위해 추가.
            sbCondition.append(" AND comm.startDate BETWEEN SUBSTRING(:startDate").append(index).append(", 1, 8) AND SUBSTRING(:endDate").append(index).append(", 1, 8) \n");
//		} else if(DateType.MONTHLY.getCode().equals(period)) {
//            sbCondition.append(" AND comm.startDate >= :startDate").append(index).append(" \n");
//            sbCondition.append(" AND comm.startDate <= :endDate").append(index).append(" \n");
		} else {
			sbCondition.append(" AND comm.startDate >= :startDate").append(index).append(" \n");
			sbCondition.append(" AND comm.startDate <= :endDate").append(index).append(" \n");
		}		

		if("location".equals(group) && !"".equals(groupData)) {
			sbCondition.append(" AND comm.location.id in (").append(groupData).append(") \n");
		}		

		if("mcu".equals(group) && !"".equals(groupData)) {
			sbCondition.append(" AND comm.senderId in (").append(addDoubleQuotationMark(groupData)).append(") \n");
		}		

		if(svcTypeCode != null && !"".equals(svcTypeCode)) {			
			if("M".equals(svcTypeCode)) {
				sbCondition.append(" AND comm.svcTypeCode.code in ('4.9.M', '4.9.N', '4.9.S') \n");
			} else if("O".equals(svcTypeCode)) {
				sbCondition.append(" AND comm.svcTypeCode.code in ('4.9.C', '4.9.F', '4.9.T', '4.9.P', '4.9.D') \n");
			} else if("E".equals(svcTypeCode)) {
				sbCondition.append(" AND comm.svcTypeCode.code in ('4.9.A', '4.9.E') \n");
			}
		}
				
		if(supplierId != null && !"".equals(supplierId)) {
			sbCondition.append(" AND comm.suppliedId = :supplierId").append(index).append(" \n");
		}
		
		return sbCondition.toString();
	}	
	
	private String makeQueryNativeSQLCondition(Map<String, String> conditionMap, String index, boolean flag) {

		StringBuilder sbCondition = new StringBuilder();

		String protocolCode = conditionMap.get("protocolCode");
		String senderId = conditionMap.get("senderId");
		String receiverId = conditionMap.get("receiverId");
		String period = conditionMap.get("period");
		String group = conditionMap.get("group");
		String groupData = conditionMap.get("groupData");
		String svcTypeCode = conditionMap.get("svcTypeCode");
		String supplierId = conditionMap.get("supplierId");

		if(!"".equals(protocolCode)) {
			sbCondition.append(" AND comm.PROTOCOL_CODE = :protocolCode").append(index).append(" \n");
		}

		if(!"".equals(senderId)) {
			sbCondition.append(" AND comm.SENDER_ID = :senderId").append(index).append(" \n");
		}

		if(!"".equals(receiverId)) {
			sbCondition.append(" AND comm.RECEIVER_ID = :receiverId").append(index).append(" \n");
		}		
		if(DateType.HOURLY.getCode().equals(period)) {
			sbCondition.append(" AND comm.START_DATE_TIME >= :startDate").append(index).append(" \n");
			sbCondition.append(" AND comm.START_DATE_TIME <= :endDate").append(index).append(" \n");
//		} else if(DateType.MONTHLYPERIOD.getCode().equals(period)) {
//			sbCondition.append(" AND comm.START_DATE >= :startDate").append(index).append(" \n");
//			sbCondition.append(" AND comm.START_DATE <= :endDate").append(index).append(" \n");
		} else {
			sbCondition.append(" AND comm.START_DATE >= :startDate").append(index).append(" \n");
			sbCondition.append(" AND comm.START_DATE <= :endDate").append(index).append(" \n");
		}
		if("location".equals(group) && !"".equals(groupData)) {
			sbCondition.append(" AND comm.LOCATION_ID in (").append(groupData).append(") \n");
		}				
		if("mcu".equals(group) && !"".equals(groupData)) {
			sbCondition.append(" AND comm.SENDER_ID in (").append(addDoubleQuotationMark(groupData)).append(") \n");
		}		
		if(true == flag) {
			if(svcTypeCode != null && !"".equals(svcTypeCode)) {			
				if("M".equals(svcTypeCode)) {
					sbCondition.append(" AND code.CODE in ('4.9.M', '4.9.N', '4.9.S') \n");
				} else if("O".equals(svcTypeCode)) {
					sbCondition.append(" AND code.CODE not in ('4.9.C', '4.9.F', '4.9.T', '4.9.P', '4.9.D') \n");
				} else if("E".equals(svcTypeCode)) {
					sbCondition.append(" AND code.CODE in ('4.9.A', '4.9.E') \n");
				}
			}
		}

		if(!"".equals(supplierId)) {
			sbCondition.append(" AND comm.SUPPLIERED_ID = :supplierId").append(index).append(" \n");
		}
		return sbCondition.toString();
	}	

	private void setQueryCondition(Query query, Map<String, String> conditionMap, String index) {

		String protocolCode = conditionMap.get("protocolCode");
		String senderId = conditionMap.get("senderId");
		String receiverId = conditionMap.get("receiverId");
		String period = conditionMap.get("period");
		
        String startDate = conditionMap.get("startDate");
        String endDate = conditionMap.get("endDate");
		String supplierId = conditionMap.get("supplierId");

        if(DateType.HOURLY.getCode().equals(period)) {
            startDate = new StringBuilder().append(conditionMap.get("startDate")).append("0000").toString();
            endDate = new StringBuilder().append(conditionMap.get("endDate")).append("5959").toString();
        } else if(DateType.MONTHLY.getCode().equals(period)) {
            startDate = new StringBuilder().append(conditionMap.get("startDate")).append("01").toString();
            endDate = new StringBuilder().append(conditionMap.get("endDate")).append("31").toString();
        } else if(DateType.PERIOD.getCode().equals(period)){
            startDate = conditionMap.get("startDate");
            endDate = conditionMap.get("endDate");
        } else {
            startDate = conditionMap.get("startDate");
            endDate = conditionMap.get("endDate");
        }     

		if(protocolCode != null && !"".equals(protocolCode)) {
			query.setString("protocolCode" + index, protocolCode);
		}

		if(senderId != null && !"".equals(senderId)) {
            query.setString("senderId" + index, "%"+senderId+"%");//String.valueOf(Math.round(Double.parseDouble(senderId))));
        }

        if(receiverId != null && !"".equals(receiverId)) {
            query.setString("receiverId" + index, "%"+receiverId+"%");
        }		

		if(period !=null & !"".equals(period)) {
			query.setString("startDate" + index, startDate);
			query.setString("endDate" + index, endDate);
		}
		
		if(supplierId != null && !"".equals(supplierId)) {
			query.setString("supplierId" + index, supplierId);
		}
		//order by idx1 desc;
		
		

		
	}
	
	private String makeGroupByCondition(String period, String prefix) {
		
		String groupByCondition = null;
		
		if(DateType.HOURLY.getCode().equals(period)) {
			groupByCondition = "substring(" + prefix + "startDateTime, 1, 10) ";
		} else if(DateType.MONTHLY.getCode().equals(period)) {
			groupByCondition = "substring(" + prefix + "startDate, 1, 6) ";
		} else {
			groupByCondition = prefix + "startDate";
		}
		
		return groupByCondition;
	}
	
	private String makeGroupByNativeSQLCondition(String period, String prefix) {
		
		String groupByCondition = "START_DATE";
		
		if(DateType.HOURLY.getCode().equals(period)) {
			groupByCondition = "substr(" + prefix + "START_DATE_TIME, 1, 10) ";
		} else if(DateType.MONTHLY.getCode().equals(period)) {
			groupByCondition = "substr(" + prefix + "START_DATE, 1, 6) ";
		} else {
			groupByCondition = prefix + "START_DATE";
		}
		
		return groupByCondition;		
	}	

	private String addDoubleQuotationMark(String groupData) {

		String[] groupDataArray = groupData.split(",");
		
		String returnValue = "";
		
		for(int i = 0, size = groupDataArray.length ; i < size ; i++) {
			
			if(i != 0) returnValue += ",";
			
			returnValue += "'" + groupDataArray[i] + "'";			
		}
		
		return returnValue;
	}

	@SuppressWarnings("rawtypes")
    public List<CommLogChartVO> getSendReceiveChartData(String suppliedId) {

        String beforeWeekDate = DateTimeUtil.calcDate(Calendar.DATE, -7).get("date").replace("-", "");
		
        StringBuilder sbQuery = new StringBuilder();
        sbQuery.append("\nSELECT comm.startDate, ");
        sbQuery.append("\n       SUM(comm.sendBytes), ");
        sbQuery.append("\n       SUM(comm.rcvBytes) ");
        sbQuery.append("\nFROM CommLog comm ");
        sbQuery.append("\nWHERE comm.startDate > :beforeWeekDate ");
        sbQuery.append("\nAND   comm.suppliedId = :suppliedId ");
        sbQuery.append("\nGROUP BY comm.startDate ");
        sbQuery.append("\nORDER BY comm.startDate ");

		Query query = getSession().createQuery(sbQuery.toString());		
		query.setString("beforeWeekDate", beforeWeekDate);
		query.setString("suppliedId", suppliedId);
		
		List result = query.list();	
		
		List<CommLogChartVO> commLogChartVOs = new ArrayList<CommLogChartVO>();
		CommLogChartVO commLogChartVO = null;
		
		Object[] resultData = null;		
		
		String date = null;
		String sendBytes = null;
		String rcvBytes = null;
				
		for(int i = 0, size = result.size() ; i < size ; i++) {
			
			resultData = (Object[])result.get(i);
			
			if(resultData[0] == null)
				continue;
			
			date = resultData[0].toString();
			sendBytes = resultData[1].toString();
			rcvBytes = resultData[2].toString();

			commLogChartVO = new CommLogChartVO();
			commLogChartVO.setDate(date);			
			commLogChartVO.setSendCnt(sendBytes);
			commLogChartVO.setRcvCnt(rcvBytes);			
			commLogChartVOs.add(commLogChartVO);
		}
		
		return commLogChartVOs;
	}

	// XXX - 속도향상
	@SuppressWarnings("rawtypes")
    public List<CommLogChartVO> getSVCTypeChartData(String suppliedId) {

		// bizRule SVCTypeCode = 미터링[N, S], 온디멘드[C, M, F, T, P, D], 이벤트[A, E]
		//FIXME 추후 네이티브 쿼리 -> HQL로 수정 작업 필요
	    StringBuilder sbQuery = new StringBuilder();
	    sbQuery.append("\nSELECT t1.date1, t1.type, t1.cnt ");
	    sbQuery.append("\nFROM ( ");
	    sbQuery.append("\n    SELECT comm.start_date date1, 'M' type, SUM(comm.rcv_bytes) cnt  ");
	    sbQuery.append("\n    FROM CommLog comm, CODE code ");
	    sbQuery.append("\n    WHERE comm.svc_type_code = code.id ");
	    sbQuery.append("\n    AND   code.code IN ('4.9.M', '4.9.N', '4.9.S') ");
	    sbQuery.append("\n    AND   comm.start_date > :weekDate1 ");
	    sbQuery.append("\n    AND   comm.suppliered_id = :suppliedId ");
	    sbQuery.append("\n    GROUP BY comm.start_date ");
	    sbQuery.append("\n    UNION ALL ");
	    sbQuery.append("\n    SELECT comm.start_date date1, 'O' type, SUM(comm.rcv_bytes) cnt ");
	    sbQuery.append("\n    FROM CommLog comm, CODE code ");
	    sbQuery.append("\n    WHERE comm.svc_type_code = code.id ");
	    sbQuery.append("\n    AND   code.code NOT IN ('4.9.C', '4.9.F', '4.9.T', '4.9.P', '4.9.D') ");
	    sbQuery.append("\n    AND   comm.start_date > :weekDate2 ");
	    sbQuery.append("\n    AND   comm.suppliered_id = :suppliedId ");
	    sbQuery.append("\n    GROUP BY comm.start_date ");
	    sbQuery.append("\n    UNION ALL ");
	    sbQuery.append("\n    SELECT comm.start_date date1, 'E' type, SUM(comm.rcv_bytes) cnt ");
	    sbQuery.append("\n    FROM CommLog comm, CODE code ");
	    sbQuery.append("\n    WHERE comm.svc_type_code = code.id ");
	    sbQuery.append("\n    AND   code.code IN ('4.9.A', '4.9.E') ");
	    sbQuery.append("\n    AND   comm.start_date > :weekDate3 ");
	    sbQuery.append("\n    AND   comm.suppliered_id = :suppliedId ");
	    sbQuery.append("\n    GROUP BY comm.start_date ");
	    sbQuery.append("\n) t1 ");
	    sbQuery.append("\nWHERE date1 IS NOT NULL ");
	    sbQuery.append("\nORDER BY t1.date1, t1.type ");

        String weekDate = DateTimeUtil.calcDate(Calendar.DATE, -7).get("date").replace("-", "");

		SQLQuery query = getSession().createSQLQuery(new SQLWrapper().getQuery(sbQuery.toString()));				
		query.setString("weekDate1", weekDate);
		query.setString("weekDate2", weekDate);
		query.setString("weekDate3", weekDate);
		query.setString("suppliedId", suppliedId);

		List result = query.list();	

		List<CommLogChartVO> commLogChartVOs = new ArrayList<CommLogChartVO>();
		CommLogChartVO commLogChartVO = new CommLogChartVO();	
		Object[] resultData = null;		

		String date = null;
		String pastDate = "";
		String svcTypeCode = null;
		String cnt = null;

		for(int i = 0, size = result.size() ; i < size ; i++) {

			resultData = (Object[])result.get(i);
			date = (resultData[0].toString()).trim();
			svcTypeCode = (resultData[1].toString()).trim();
			cnt = (resultData[2].toString()).trim();

			if(!pastDate.equals(date)) {

				if(!"".equals(pastDate)) {

					commLogChartVO.setDate(pastDate);
					commLogChartVOs.add(commLogChartVO);

					commLogChartVO = new CommLogChartVO();
				}

				pastDate = date;
			}

			// svcTypeCode => M:metering, O:ondemand, E:event
			if("M".equals(svcTypeCode)) {
				commLogChartVO.setMeteringCnt(cnt);
			} else if("O".equals(svcTypeCode)) {
				commLogChartVO.setOndemandCnt(cnt);
			} else if("E".equals(svcTypeCode)) {
				commLogChartVO.setEventCnt(cnt);
			}

			// 마지막 정보들 목록에 포함시킴
			if(size - 1 == i) {
				commLogChartVO.setDate(pastDate);
				commLogChartVOs.add(commLogChartVO);				
			}
		}
		return commLogChartVOs;
	}

	@SuppressWarnings("rawtypes")
    public List<Map<String, String>> getLocationChartData() {
		// TODO - 현재 사용하지 않음. 사용할 경우 SQL 수정필요.
        StringBuilder sbQuery = new StringBuilder();
        sbQuery.append("\nSELECT t1.startdate, t1.name, t2.cnt \n");
        sbQuery.append("\nFROM (SELECT SUBSTR(comm.start_date_time, 1, 8) startdate, loc.name \n");
        sbQuery.append("\n      FROM commlog comm, location loc \n");
        sbQuery.append("\n      WHERE SUBSTR(comm.start_date_time, 1, 8) > :weekDate1 \n");
        sbQuery.append("\n      GROUP BY SUBSTR(comm.start_date_time, 1, 8), loc.name \n");
        sbQuery.append("\n     )  t1 \n");
        sbQuery.append("\n     INNER JOIN \n");
        sbQuery.append("\n     (SELECT SUBSTR(comm.start_date_time, 1, 8) startdate, loc.name, SUM(comm.rcv_bytes) cnt \n");
        sbQuery.append("\n      FROM commlog comm, location loc \n");
        sbQuery.append("\n      WHERE comm.location_id = loc.id \n");
        sbQuery.append("\n      AND SUBSTR(comm.start_date_time, 1, 8) > :weekDate2 \n");
        sbQuery.append("\n      GROUP BY SUBSTR(comm.start_date_time, 1, 8), loc.name \n");
        sbQuery.append("\n     ) t2 \n");
        sbQuery.append("\n     ON t1.startdate = t2.startdate \n");
        sbQuery.append("\n     AND t1.name = t2.name ORDER BY t1.startdate \n");

        String weekDate = DateTimeUtil.calcDate(Calendar.DATE, -7).get("date").replace("-", "");
		
		SQLQuery query = getSession().createSQLQuery(new SQLWrapper().getQuery(sbQuery.toString()));		
		query.setString("weekDate1", weekDate);
		query.setString("weekDate2", weekDate);
		
		List result = query.list();	
		
		List<Map<String, String>> maps = new ArrayList<Map<String, String>>();
		Map<String, String> map = null;

		Object[] resultData = null;		
		
		String date = null;
		String pastDate = "";
		String locationName = null;
		String cnt = null;
		int locationDisplayIndex = 0;
		
		for(int i = 0, size = result.size() ; i < size ; i++) {

			resultData = (Object[])result.get(i);
			date = resultData[0].toString();
			locationName = resultData[1].toString();
						
			if(resultData[2] != null)
				cnt = (resultData[2].toString()).trim();
			else
				cnt = "0";

			if(!pastDate.equals(date)) {
				
				if(!"".equals(pastDate)) {									
					maps.add(map);
				}
				
				pastDate = date;
				locationDisplayIndex = 1;
				map = new HashMap<String, String>();
				map.put("date", pastDate);
			}

			map.put("locationName" + locationDisplayIndex, locationName);
			map.put("locationCnt" + locationDisplayIndex, cnt);

			// 마지막 정보들 목록에 포함시킴
			if(size - 1 == i) {			
				map.put("locationSize", Integer.toString(locationDisplayIndex));
				maps.add(map);
			}		
			
			locationDisplayIndex++;
		}
		
		return maps;
	}

	public Map<String, String> getCommLogStatisticsData(Map<String, String> conditionMap) {
        StringBuilder sbQuery = new StringBuilder();
        sbQuery.append("select sum(comm.sendBytes), avg(comm.sendBytes), sum(comm.rcvBytes), avg(comm.rcvBytes) ");
        sbQuery.append("from CommLog comm where 1=1 ");
		sbQuery.append(makeQueryCondition(conditionMap, ""));
			
		Query query = getSession().createQuery(sbQuery.toString());
		setQueryCondition(query, conditionMap, "");
		
		Map<String, String> map = new HashMap<String, String>();		
		List<?> list = query.list();

		if(list != null && list.size() >= 1) {
			Object[] object = (Object[])list.get(0);
			
			if(object[0] == null) {
				map.put("totalSender", "0");
				map.put("avgSender", "0");
				map.put("totalReceiver", "0");
				map.put("avgReceiver", "0");
			} else {							
				map.put("totalSender", object[0].toString());
				map.put("avgSender", object[1].toString());
				map.put("totalReceiver", object[2].toString());
				map.put("avgReceiver", object[3].toString());
			}
		} else{
			map.put("totalSender", "0");
			map.put("avgSender", "0");
			map.put("totalReceiver", "0");
			map.put("avgReceiver", "0");
		}		

		return map;
	}	

    /**
     * method name : getMcuCommLogList<b/>
     * method Desc :
     *
     * @param conditionMap
     * @param isCount
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getMcuCommLogList(Map<String, Object> conditionMap, boolean isCount) {
        List<Map<String, Object>> result;
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        Integer page = (Integer)conditionMap.get("page");
        Integer limit = (Integer)conditionMap.get("limit");
        String senderId = StringUtil.nullToBlank(conditionMap.get("senderId"));
        String startDate = StringUtil.nullToBlank(conditionMap.get("startDate"));
        String endDate = StringUtil.nullToBlank(conditionMap.get("endDate"));
        StringBuilder sb = new StringBuilder();

        if (isCount) {
            sb.append("\nSELECT COUNT(*) AS cnt ");
        } else {
            sb.append("\nSELECT co.svcTypeCode.descr AS svcTypeCode, ");
            sb.append("\n       COALESCE(co.sendBytes, 0) AS sendBytes, ");
            sb.append("\n       COALESCE(co.rcvBytes, 0) AS rcvBytes, ");
            sb.append("\n       (COALESCE(co.sendBytes, 0) + COALESCE(co.rcvBytes, 0)) AS totalBytes, ");
            sb.append("\n       COALESCE(co.totalCommTime, 0) AS totalCommTime ");
        }
        sb.append("\nFROM CommLog co ");
        sb.append("\nWHERE 1=1 ");
        sb.append("\nAND   co.suppliedId = :supplierId ");
        sb.append("\nAND   co.senderId = :senderId ");
        sb.append("\nAND   co.startDate BETWEEN :startDate AND :endDate ");
        if (!isCount) {
            sb.append("\nORDER BY co.endTime ");
        }

        Query query = getSession().createQuery(sb.toString());
        query.setString("supplierId", supplierId.toString());
        query.setString("senderId", senderId);
        query.setString("startDate", startDate);
        query.setString("endDate", endDate);

        if (isCount) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("total", ((Number)query.uniqueResult()).intValue());
            result = new ArrayList<Map<String, Object>>();
            result.add(map);
        } else {
            if (page != null && limit != null) {
                query.setFirstResult((page - 1) * limit);
                query.setMaxResults(limit);
            }
            result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        }

        return result;
    }

    /**
     * method name : getMcuCommLogData<b/>
     * method Desc :
     *
     * @param conditionMap
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getMcuCommLogData(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result;
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        String senderId = StringUtil.nullToBlank(conditionMap.get("senderId"));
        String startDate = StringUtil.nullToBlank(conditionMap.get("startDate"));
        String endDate = StringUtil.nullToBlank(conditionMap.get("endDate"));
        StringBuilder sb = new StringBuilder();

        sb.append("\nSELECT COALESCE(SUM(co.sendBytes), 0) AS sendSum, ");
        sb.append("\n       COALESCE(MAX(co.sendBytes), 0) AS sendMax, ");
        sb.append("\n       COALESCE(MIN(co.sendBytes), 0) AS sendMin, ");
        sb.append("\n       COALESCE(SUM(co.rcvBytes), 0) AS rcvSum, ");
        sb.append("\n       COALESCE(MAX(co.rcvBytes), 0) AS rcvMax, ");
        sb.append("\n       COALESCE(MIN(co.rcvBytes), 0) AS rcvMin ");
        sb.append("\nFROM CommLog co ");
        sb.append("\nWHERE 1=1 ");
        sb.append("\nAND   co.suppliedId = :supplierId ");
        sb.append("\nAND   co.senderId = :senderId ");
        sb.append("\nAND   co.startDate BETWEEN :startDate AND :endDate ");

        Query query = getSession().createQuery(sb.toString());
        query.setString("supplierId", supplierId.toString());
        query.setString("senderId", senderId);
        query.setString("startDate", startDate);
        query.setString("endDate", endDate);

        result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        return result;
    }
}