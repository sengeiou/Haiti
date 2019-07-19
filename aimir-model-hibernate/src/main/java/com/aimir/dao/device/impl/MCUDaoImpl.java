package com.aimir.dao.device.impl;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants.McuStatus;
import com.aimir.constants.CommonConstants.UsingMCUType;
import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.model.device.CommStateByLocationVO;
import com.aimir.model.device.LocationByCommStateVO;
import com.aimir.model.device.MCU;
import com.aimir.model.device.MCUTypeByCommStateVO;
import com.aimir.model.device.MCUTypeByLocationVO;
import com.aimir.model.system.Code;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.DecimalUtil;
import com.aimir.util.StringUtil;

import net.sf.json.JSONArray;

@Repository(value = "mcuDao")
public class MCUDaoImpl extends AbstractHibernateGenericDao<MCU, Integer> implements MCUDao {

    Log logger = LogFactory.getLog(MCUDaoImpl.class);
    
    @Autowired
    CodeDao codeDao;
    
    @Autowired
    SupplierDao supplierDao;

	@Autowired
	protected MCUDaoImpl(SessionFactory sessionFactory) {
		super(MCU.class);
		super.setSessionFactory(sessionFactory);
	}

	@Override
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	public MCU get(String sysID) {
	    return findByCondition("sysID", sysID);
	}

	@Override
	@SuppressWarnings("rawtypes")
	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<String> getHwVersions() {

		List<String> hwVersions = new ArrayList<String>();
		String hwVersion = null;

		Query query = getSession().createQuery("select hwVersion from MCU group by hwVersion");
		List results = query.list();

		for(int i = 0, j = results.size() ; i < j ; i++) {

			hwVersion = (String)results.get(i);
			hwVersions.add(hwVersion);
		}

		return hwVersions;
	}

	@Override
	@SuppressWarnings("rawtypes")
	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<String> getSwVersions() {

		List<String> swVersions = new ArrayList<String>();
		String swVersion = null;

		Query query = getSession().createQuery("select swVersion from MCU group by swVersion");
		List results = query.list();

		for(int i = 0, j = results.size() ; i < j ; i++) {

			swVersion = (String)results.get(i);
			swVersions.add(swVersion);
		}

		return swVersions;
	}

    /**
     * method name : getDcuGridData<b/>
     * method Desc :
     *
     * @param conditionMap
     * @param isCount
     * @return
     */
	@Override
	@SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRED)
    public Map<String, Object> getDcuGridData(Map<String, Object> conditionMap, boolean isCount) {
        Map<String, Object> result = new HashMap<String, Object>();
        Integer supplierId = (Integer) conditionMap.get("supplierId");
        String mcuId = StringUtil.nullToBlank(conditionMap.get("mcuId"));
        String mcuSerial = StringUtil.nullToBlank(conditionMap.get("mcuSerial")); 
        String mcuType = StringUtil.nullToBlank(conditionMap.get("mcuType"));
        String swVersion = StringUtil.nullToBlank(conditionMap.get("swVersion"));
        String swRevison = StringUtil.nullToBlank(conditionMap.get("swRevison"));
        String hwVersion = StringUtil.nullToBlank(conditionMap.get("hwVersion"));
        String installDateStart = StringUtil.nullToBlank(conditionMap.get("installDateStart"));
        String installDateEnd = StringUtil.nullToBlank(conditionMap.get("installDateEnd"));
        String lastcommStartDate = StringUtil.nullToBlank(conditionMap.get("lastcommStartDate"));
        String lastcommEndDate = StringUtil.nullToBlank(conditionMap.get("lastcommEndDate"));
        String filter = StringUtil.nullToBlank(conditionMap.get("filter"));
        String order = StringUtil.nullToBlank(conditionMap.get("order"));
        String protocol = StringUtil.nullToBlank(conditionMap.get("protocol"));
        String mcuStatus = StringUtil.nullToBlank(conditionMap.get("mcuStatus"));
        String modelId = StringUtil.nullToBlank(conditionMap.get("modelId"));
        String fwGadget = StringUtil.nullToBlank(conditionMap.get("fwGadget"));
        String purchaseOrder = StringUtil.nullToBlank(conditionMap.get("purchaseOrder"));
        Integer page = (Integer) conditionMap.get("page");
        Integer limit = (Integer) conditionMap.get("limit");
        List<Integer> locationIdList = (List<Integer>)conditionMap.get("locationIdList");
        Code deleteCode = conditionMap.get("deleteCode") == null ? null : (Code) conditionMap.get("deleteCode");
        Code normalCode = conditionMap.get("normalCodeId") == null ? null : (Code) conditionMap.get("normalCodeId");
        Code securityErrorCode = conditionMap.get("securityErrorCodeId") == null ? null : (Code) conditionMap.get("securityErrorCodeId");
        Code commErrorCode = conditionMap.get("commErrorCodeId") == null ? null : (Code) conditionMap.get("commErrorCodeId");
        Code powerDownCode = conditionMap.get("powerDownCodeId") == null ? null : (Code) conditionMap.get("powerDownCodeId");
        Criteria criteria = getSession().createCriteria(MCU.class);
        
        if (isCount) {
            criteria.setProjection(Projections.rowCount());
        }
        if (supplierId != null)
            criteria.add(Restrictions.eq("supplier.id", supplierId));
        
        if(fwGadget.equals("Y")){
        	StringTokenizer st = new StringTokenizer(mcuId, ", ");
            List<String> mcuIds = new ArrayList();
            for(int i = 0 ; st.hasMoreTokens() ; i++){
            	mcuIds.add(st.nextToken());
    		}
            if (mcuIds != null && mcuIds.size() > 0) {
                criteria.add(Restrictions.in("sysID", mcuIds));
            }
        }else{
        	if (!mcuId.isEmpty())
                criteria.add(Restrictions.ilike("sysID", mcuId, MatchMode.START));
        }
        
        if (!mcuSerial.isEmpty())
            criteria.add(Restrictions.ilike("sysSerialNumber", mcuSerial, MatchMode.START)); 
        
        if (!modelId.isEmpty())
        	criteria.add(Restrictions.eq("deviceModelId", Integer.parseInt(modelId)));
        
        if (!mcuType.isEmpty())
            criteria.add(Restrictions.eq("mcuType.id", Integer.parseInt(mcuType)));

        if (locationIdList != null && locationIdList.size() > 0) {
            criteria.add(Restrictions.in("location.id", locationIdList));
        }

        if (!swVersion.isEmpty())
            criteria.add(Restrictions.eq("sysSwVersion", swVersion));
        
        if (!swRevison.isEmpty())
            criteria.add(Restrictions.eq("sysSwRevision", swRevison));

        if (!hwVersion.isEmpty())
            criteria.add(Restrictions.eq("sysHwVersion", hwVersion));
        
        if (!purchaseOrder.isEmpty())
            criteria.add(Restrictions.eq("po", purchaseOrder));
        
        if (!installDateStart.isEmpty())
            criteria.add(Restrictions.ge("installDate", installDateStart + "000000"));

        if (!installDateEnd.isEmpty())
            criteria.add(Restrictions.le("installDate", installDateEnd + "235959"));
        
        if (!lastcommStartDate.isEmpty())
            criteria.add(Restrictions.ge("lastCommDate", lastcommStartDate + "000000"));

        if (!lastcommEndDate.isEmpty())
            criteria.add(Restrictions.le("lastCommDate", lastcommEndDate + "235959"));

        if (!protocol.isEmpty())
            criteria.add(Restrictions.eq("protocolType.id", Integer.parseInt(protocol)));
        
		if(deleteCode != null && deleteCode.getId() != null) {
			if (!mcuStatus.isEmpty() && (Integer.parseInt(mcuStatus) == deleteCode.getId())) { // 상태가'delete'일 경우
	        	criteria.add(Restrictions.eq("mcuStatusCodeId", Integer.parseInt(mcuStatus)));
			} else if (!mcuStatus.isEmpty() && (Integer.parseInt(mcuStatus) == normalCode.getId())) {
				criteria.add(Restrictions.eq("mcuStatusCodeId", Integer.parseInt(mcuStatus)));
			} else if (!mcuStatus.isEmpty() && (Integer.parseInt(mcuStatus) == securityErrorCode.getId())) {
				criteria.add(Restrictions.eq("mcuStatusCodeId", Integer.parseInt(mcuStatus)));
			} else if (!mcuStatus.isEmpty() && (Integer.parseInt(mcuStatus) == commErrorCode.getId())) {
				criteria.add(Restrictions.eq("mcuStatusCodeId", Integer.parseInt(mcuStatus)));
			} else if (!mcuStatus.isEmpty() && (Integer.parseInt(mcuStatus) == powerDownCode.getId())) {
				criteria.add(Restrictions.eq("mcuStatusCodeId", Integer.parseInt(mcuStatus)));
	        } else {
	        	Criterion deleteMcu = Restrictions.ne("mcuStatus", deleteCode);
	        	Criterion nullMcu = Restrictions.isNull(("mcuStatus"));
	        	
	        	LogicalExpression expression = Restrictions.or(deleteMcu, nullMcu);
	        	criteria.add(expression);
	        }
		} else {
			logger.info("deleteCodeId is not exist");
		}


        if (!filter.isEmpty()) {

            Map<String, String> TFBeforeHour = DateTimeUtil.calcDate(Calendar.HOUR, -24);
            Map<String, String> FEBeforeHour = DateTimeUtil.calcDate(Calendar.HOUR, -48);

            String TFDate = TFBeforeHour.get("date").replace("-", "") + TFBeforeHour.get("time").replace(".", "");
            String FEDate = FEBeforeHour.get("date").replace("-", "") + FEBeforeHour.get("time").replace(".", "");

            if ("normal".equals(filter)) {
                criteria.add(Restrictions.ge("lastCommDate", TFDate));
            } else if ("commStateYellow".equals(filter)) {
                criteria.add(Restrictions.le("lastCommDate", TFDate));
                criteria.add(Restrictions.ge("lastCommDate", FEDate));
            } else if ("commStateRed".equals(filter)) {
                criteria.add(Restrictions.le("lastCommDate", FEDate));
            }
        }

        if (!isCount && !order.isEmpty()) {
            if ("lastCommDesc".equals(order)) {
                criteria.addOrder(Order.desc("lastCommDate"));
            } else if ("lastCommAsc".equals(order)) {
                criteria.addOrder(Order.asc("lastCommDate"));
            } else if ("installDateDesc".equals(order)) {
                criteria.addOrder(Order.desc("installDate"));
            } else if ("installDateAsc".equals(order)) {
                criteria.addOrder(Order.asc("installDate"));
            }
            criteria.addOrder(Order.asc("sysID"));
        }

        if (!isCount && page != null && limit != null && (page != 0 || limit != 0)) {
            criteria.setFirstResult((page - 1) * limit);
            // 페이지별로 데이터를 내보내게 하는 부분
            criteria.setMaxResults(limit);
        }

        if (isCount) {
            result.put("count", ((Number) criteria.uniqueResult()).intValue());
        } else {
            result.put("list", criteria.list());
        }
        
        return result;
    }

	@Override
	@SuppressWarnings("unchecked")
	@Deprecated
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	public List<MCU> getGridData(Map<String, String> conditionMap) {

    	String supplierId = conditionMap.get("supplierId");
		String mcuId = conditionMap.get("mcuId");
		String mcuType = conditionMap.get("mcuType");
		String swVersion = conditionMap.get("swVersion");
 		String hwVersion = conditionMap.get("hwVersion");
		String installDateStart = conditionMap.get("installDateStart");
		String installDateEnd = conditionMap.get("installDateEnd");
		String filter = conditionMap.get("filter");
		String order = conditionMap.get("order");
		String protocol = conditionMap.get("protocol");
		int page = Integer.parseInt(conditionMap.get("page"));
		//엑셀 출력의 경우는 pageSize가 0으로 들어옴
		int rowPerPage = Integer.parseInt(conditionMap.get("pageSize"));

		Criteria criteria = getSession().createCriteria(MCU.class);
		if(supplierId != null && !"".equals(supplierId))
			criteria.add(Restrictions.eq("supplier.id", Integer.parseInt(supplierId)));
		if(mcuId != null && !"".equals(mcuId))
			criteria.add(Restrictions.eq("sysID", mcuId));

		if(mcuType != null && !"".equals(mcuType))
			criteria.add(Restrictions.eq("mcuType.id", Integer.parseInt(mcuType)));

		if (StringUtil.nullToBlank(conditionMap.get("locationId")).length() > 0) {
			String[] locationIdStr = conditionMap.get("locationId").split(",");
			Integer[] locationId = new Integer[locationIdStr.length];

			for (int i = 0; i < locationIdStr.length; i++) {
				locationId[i] = Integer.parseInt(locationIdStr[i]);
			}

			criteria.add(Restrictions.in("location.id", locationId));
		}

		if(swVersion != null && !"".equals(swVersion))
			criteria.add(Restrictions.eq("sysSwVersion", swVersion));

		if(hwVersion != null && !"".equals(hwVersion))
			criteria.add(Restrictions.eq("sysHwVersion", hwVersion));

		if(installDateStart != null && !"".equals(installDateStart))
			criteria.add(Restrictions.ge("installDate", installDateStart+ "000000"));

		if(installDateEnd != null && !"".equals(installDateEnd))
			criteria.add(Restrictions.le("installDate", installDateEnd + "235959"));

		if(protocol != null && !"".equals(protocol))
			criteria.add(Restrictions.eq("protocolType.id", Integer.parseInt(protocol)));

		if(filter != null && !"".equals(filter)) {

			Map<String, String> TFBeforeHour = DateTimeUtil.calcDate(Calendar.HOUR, -24);
			Map<String, String> FEBeforeHour = DateTimeUtil.calcDate(Calendar.HOUR, -48);

			String TFDate = TFBeforeHour.get("date").replace("-", "") + TFBeforeHour.get("time").replace(".", "");
			String FEDate = FEBeforeHour.get("date").replace("-", "") + FEBeforeHour.get("time").replace(".", "");

			if("normal".equals(filter)) {
				criteria.add(Restrictions.ge("lastCommDate", TFDate));
			} else if("commStateYellow".equals(filter)) {
				criteria.add(Restrictions.le("lastCommDate", TFDate));
				criteria.add(Restrictions.ge("lastCommDate", FEDate));
			} else if("commStateRed".equals(filter)) {
				criteria.add(Restrictions.le("lastCommDate", FEDate));
			}
		}

		if(order != null && !"".equals(order)) {
			if("lastCommDesc".equals(order)) {
				criteria.addOrder(Order.desc("lastCommDate"));
			} else if("lastCommAsc".equals(order)) {
				criteria.addOrder(Order.asc("lastCommDate"));
			} else if("installDateDesc".equals(order)) {
				criteria.addOrder(Order.desc("installDate"));
			} else if("installDateAsc".equals(order)) {
				criteria.addOrder(Order.asc("installDate"));
			}
		}


		int firstResult = page * rowPerPage;

		criteria.setFirstResult(firstResult);

		//엑셀 출력시 모든 데이터를 출력하기 위한 조건문
		if(rowPerPage != 0) {
			//페이지별로 데이터를 내보내게 하는 부분
			criteria.setMaxResults(rowPerPage);
		}

		List<MCU> mcus = criteria.list();

		return mcus;
	}

	@Override
	@Deprecated
	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public Integer getMcuGridDataTotalCount(String[] conditionArray) {

		String mcuId = conditionArray[0];
		String mcuType = conditionArray[1];
		String swVersion = conditionArray[3];
		String hwVersion = conditionArray[4];
		String installDateStart = conditionArray[5];
		String installDateEnd = conditionArray[6];
//		String page = conditionArray[7];
		String filter = conditionArray[8];
//		String order = conditionArray[9];
		String protocol = conditionArray[10];
		String supplierId = conditionArray[12];

		Criteria criteria = getSession().createCriteria(MCU.class);
		criteria.setProjection(Projections.rowCount());
		if(supplierId != null && !"".equals(supplierId))
			criteria.add(Restrictions.eq("supplier.id", Integer.parseInt(supplierId)));
		if(mcuId != null && !"".equals(mcuId))
			criteria.add(Restrictions.eq("sysID", mcuId));

		if(mcuType != null && !"".equals(mcuType))
			criteria.add(Restrictions.eq("mcuType.id", Integer.parseInt(mcuType)));

		if (StringUtil.nullToBlank(conditionArray[2]).length() > 0) {
			String[] locationIdStr = conditionArray[2].split(",");
			Integer[] locationId = new Integer[locationIdStr.length];

			for (int i = 0; i < locationIdStr.length; i++) {
				locationId[i] = Integer.parseInt(locationIdStr[i]);
			}

			criteria.add(Restrictions.in("location.id", locationId));
		}

		if(swVersion != null && !"".equals(swVersion))
			criteria.add(Restrictions.eq("sysSwVersion", swVersion));

		if(hwVersion != null && !"".equals(hwVersion))
			criteria.add(Restrictions.eq("sysHwVersion", hwVersion));

		if(installDateStart != null && !"".equals(installDateStart))
			criteria.add(Restrictions.ge("installDate", installDateStart));

		if(installDateEnd != null && !"".equals(installDateEnd))
			criteria.add(Restrictions.le("installDate", installDateEnd));

		if(protocol != null && !"".equals(protocol))
			criteria.add(Restrictions.eq("protocolType.id", Integer.parseInt(protocol)));

		if(filter != null && !"".equals(filter)) {

			Map<String, String> TFBeforeHour = DateTimeUtil.calcDate(Calendar.HOUR, -24);
			Map<String, String> FEBeforeHour = DateTimeUtil.calcDate(Calendar.HOUR, -48);

			String TFDate = TFBeforeHour.get("date").replace("-", "") + TFBeforeHour.get("time").replace(".", "");
			String FEDate = FEBeforeHour.get("date").replace("-", "") + FEBeforeHour.get("time").replace(".", "");

			if("normal".equals(filter)) {
				criteria.add(Restrictions.ge("lastCommDate", TFDate));
			} else if("commStateYellow".equals(filter)) {
				criteria.add(Restrictions.le("lastCommDate", TFDate));
				criteria.add(Restrictions.ge("lastCommDate", FEDate));
			} else if("commStateRed".equals(filter)) {
				criteria.add(Restrictions.le("lastCommDate", FEDate));
			}
		}
		Long count = ((Number) criteria.uniqueResult()).longValue();
		return count.intValue();
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	public List<MCU> getMcusByCondition(String[] array, int rowPerPage) {

		//집중기번호[0], 집중기유형[1], 지역명[2], sw version[3], hw version[4], 설치일[5], 설치일[6]
		//page[7]
		String mcuId = array[0];
		String mcuType = array[1];
		String locId = array[2];
		String swVersion = array[3];
 		String hwVersion = array[4];
		String installDateStart = array[5].replace("/", "") + "000000";
		String installDateEnd = array[6].replace("/", "") + "235959";
		String filter = array[8];
		String order = array[9];

		Criteria criteria = getSession().createCriteria(MCU.class);

		if(mcuId != null && !"".equals(mcuId))
			criteria.add(Restrictions.eq("sysID", mcuId));

		if(mcuType != null && !"".equals(mcuType))
			criteria.add(Restrictions.eq("mcuType.id", Integer.parseInt(mcuType)));

		if(locId != null && !"".equals(locId))
			criteria.add(Restrictions.eq("location.id", Integer.parseInt(locId)));

		if(swVersion != null && !"".equals(swVersion))
			criteria.add(Restrictions.eq("sysSwVersion", swVersion));

		if(hwVersion != null && !"".equals(hwVersion))
			criteria.add(Restrictions.eq("sysHwVersion", hwVersion));

		if(installDateStart != null && !"".equals(installDateStart))
			criteria.add(Restrictions.ge("installDate", installDateStart));

		if(installDateEnd != null && !"".equals(installDateEnd))
			criteria.add(Restrictions.le("installDate", installDateEnd));

		if(filter != null && !"".equals(filter)) {

			Map<String, String> TFBeforeHour = DateTimeUtil.calcDate(Calendar.HOUR, -24);
			Map<String, String> FEBeforeHour = DateTimeUtil.calcDate(Calendar.HOUR, -48);

			String TFDate = TFBeforeHour.get("date").replace("-", "") + TFBeforeHour.get("time").replace(".", "");
			String FEDate = FEBeforeHour.get("date").replace("-", "") + FEBeforeHour.get("time").replace(".", "");

			if("normal".equals(filter)) {
//				sbQuery.append(" and mcu.lastCommDate >= :TFDate1 ");
				criteria.add(Restrictions.ge("lastCommDate", TFDate));
			} else if("commStateYellow".equals(filter)) {
//				sbQuery.append(" and mcu.lastCommDate < :TFDate2 AND mcu.lastCommDate >= :FEDate2 ");
				criteria.add(Restrictions.le("lastCommDate", TFDate));
				criteria.add(Restrictions.ge("lastCommDate", FEDate));
			} else if("commStateRed".equals(filter)) {
//				sbQuery.append(" and mcu.lastCommDate < :FEDate3 ");
				criteria.add(Restrictions.le("lastCommDate", FEDate));
			}
		}

		if(order != null && !"".equals(order)) {
			if("lastCommDesc".equals(order)) {
				criteria.addOrder(Order.desc("lastCommDate"));
			} else if("lastCommAsc".equals(order)) {
				criteria.addOrder(Order.asc("lastCommDate"));
			} else if("installDateDesc".equals(order)) {
				criteria.addOrder(Order.desc("installDate"));
			} else if("installDateAsc".equals(order)) {
				criteria.addOrder(Order.asc("installDate"));
//			} else if("csqDesc".equals(order)) {
//			} else if("csqAsc".equals(order)) {
			}
		}

		int page = Integer.parseInt(array[7]);
		int firstResult = (page - 1) * rowPerPage;

		criteria.setFirstResult(firstResult);
		criteria.setMaxResults(rowPerPage);

		List<MCU> mcus = criteria.list();

		return mcus;
	}

	@Override
	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public Integer getMCUCountByCondition(Map<String, String> conditionMap) {

		String mcuId = conditionMap.get("mcuId");
		String mcuType = conditionMap.get("mcuType");
		String locId = conditionMap.get("locId");
		String swVersion = conditionMap.get("swVersion");
		String hwVersion = conditionMap.get("hwVersion");
		String installDateStart = conditionMap.get("installDateStart");
		String installDateEnd = conditionMap.get("installDateEnd");
		String filter = conditionMap.get("filter");
		String protocol = conditionMap.get("protocol");
		String supplierId = conditionMap.get("supplierId");

		Criteria criteria = getSession().createCriteria(MCU.class);
		criteria.setProjection(Projections.rowCount());

		if(supplierId != null && !"".equals(supplierId))
			criteria.add(Restrictions.eq("supplier.id", Integer.parseInt(supplierId)));

		if(mcuId != null && !"".equals(mcuId))
			criteria.add(Restrictions.eq("sysID", mcuId));

		if(mcuType != null && !"".equals(mcuType))
			criteria.add(Restrictions.eq("mcuType.id", Integer.parseInt(mcuType)));

		if(locId != null && !"".equals(locId))
			criteria.add(Restrictions.eq("location.id", Integer.parseInt(locId)));

		if(swVersion != null && !"".equals(swVersion))
			criteria.add(Restrictions.eq("sysSwVersion", swVersion));

		if(hwVersion != null && !"".equals(hwVersion))
			criteria.add(Restrictions.eq("sysHwVersion", hwVersion));

		if(installDateStart != null && !"".equals(installDateStart))
			criteria.add(Restrictions.ge("installDate", installDateStart));

		if(installDateEnd != null && !"".equals(installDateEnd))
			criteria.add(Restrictions.le("installDate", installDateEnd));

		if(protocol != null && !"".equals(protocol))
			criteria.add(Restrictions.eq("protocolType.id", Integer.parseInt(protocol)));

		if(filter != null && !"".equals(filter)) {

			Map<String, String> TFBeforeHour = DateTimeUtil.calcDate(Calendar.HOUR, -24);
			Map<String, String> FEBeforeHour = DateTimeUtil.calcDate(Calendar.HOUR, -48);

			String TFDate = TFBeforeHour.get("date").replace("-", "") + TFBeforeHour.get("time").replace(".", "");
			String FEDate = FEBeforeHour.get("date").replace("-", "") + FEBeforeHour.get("time").replace(".", "");

			if("normal".equals(filter)) {
				criteria.add(Restrictions.ge("lastCommDate", TFDate));
			} else if("commStateYellow".equals(filter)) {
				criteria.add(Restrictions.le("lastCommDate", TFDate));
				criteria.add(Restrictions.ge("lastCommDate", FEDate));
			} else if("commStateRed".equals(filter)) {
				criteria.add(Restrictions.le("lastCommDate", FEDate));
			}
		}

		return ((Number)criteria.uniqueResult()).intValue();
	}

//	@SuppressWarnings("unchecked")
//	public List<MCU> getMcusByCondition(Map<String, String> conditionMap) {
//
//		String mcuType = conditionMap.get("MCUType");
//		String locId = conditionMap.get("locationId");
//		String commState = conditionMap.get("commState");
//
//		Criteria criteria = getSession().createCriteria(MCU.class);
//
//		if(mcuType != null && !"".equals(mcuType))
//			criteria.add(Restrictions.eq("mcuType.code", mcuType));
//
//		if(locId != null && !"".equals(locId))
//			criteria.add(Restrictions.eq("location.id", Integer.parseInt(locId)));
//
//		if(commState != null && !"".equals(commState)) {
//
//			Map<String, String> TFBeforeHour = DateTimeUtil.calcDate(Calendar.HOUR, -24);
//			Map<String, String> FEBeforeHour = DateTimeUtil.calcDate(Calendar.HOUR, -48);
//
//			String TFDate = TFBeforeHour.get("date").replace("-", "") + TFBeforeHour.get("time").replace(".", "");
//			String FEDate = FEBeforeHour.get("date").replace("-", "") + FEBeforeHour.get("time").replace(".", "");
//
//			if("0".equals(commState)) {
//				criteria.add(Restrictions.ge("lastCommDate", TFDate));
//			} else if("24".equals(commState)) {
//				criteria.add(Restrictions.between("lastCommDate", FEDate, TFDate));
//			} else if("48".equals(commState)) {
//				criteria.add(Restrictions.le("lastCommDate", FEDate));
//			}
//		}
//
//		return criteria.list();
//	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	public List<MCU> getMcusByCondition(Map<String, String> conditionMap) {

		String mcuType = conditionMap.get("MCUType");
		String locationId = conditionMap.get("locationId");
		String commState = conditionMap.get("commState");
		String supplierId = conditionMap.get("supplierId");

		StringBuffer sbQuery = new StringBuffer("  from MCU mcu Where mcu.id is not null ");


		if(supplierId != null && !"".equals(supplierId))
			sbQuery.append("and mcu.supplier.id = :supplierId ");

		if(mcuType != null && !"".equals(mcuType))
			sbQuery.append("and mcu.mcuType.code = :mcuType ");

		if(locationId != null && !"".equals(locationId))
			sbQuery.append("and mcu.location.id = :locationId ");

		if(commState != null && !"".equals(commState)) {

			if("0".equals(commState)) {
				sbQuery.append(" and mcu.lastCommDate >= :TFDate1 ");
			} else if("24".equals(commState)) {
				sbQuery.append(" and mcu.lastCommDate < :TFDate2 AND mcu.lastCommDate >= :FEDate2 ");
			} else if("48".equals(commState)) {
				sbQuery.append(" and mcu.lastCommDate < :FEDate3 ");
			}
		}

		Query query = getSession().createQuery(sbQuery.toString());

		if(supplierId != null && !"".equals(supplierId))
			query.setInteger("supplierId", Integer.parseInt(supplierId));

		if(mcuType != null && !"".equals(mcuType))
			query.setString("mcuType", mcuType);

		if(locationId != null && !"".equals(locationId))
			query.setString("locationId", locationId);

		if(commState != null && !"".equals(commState)) {

			Map<String, String> TFBeforeHour = DateTimeUtil.calcDate(Calendar.HOUR, -24);
			Map<String, String> FEBeforeHour = DateTimeUtil.calcDate(Calendar.HOUR, -48);

			String TFDate = TFBeforeHour.get("date").replace("-", "") + TFBeforeHour.get("time").replace(".", "");
			String FEDate = FEBeforeHour.get("date").replace("-", "") + FEBeforeHour.get("time").replace(".", "");

			if("0".equals(commState)) {
				query.setString("TFDate1", TFDate);
			} else if("24".equals(commState)) {
				query.setString("TFDate2", TFDate);
				query.setString("FEDate2", FEDate);
			} else if("48".equals(commState)) {
				query.setString("FEDate3", FEDate);
			}
		}

		return query.list();
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	public List<MCU> getMcusByTargetList(int searchType, List<String> targetList) {
		logger.debug("################### searchType = " + searchType);
		logger.info("################### targetList = " + ((targetList == null) ? "null" : targetList.toString()));

		StringBuilder sbQuery = new StringBuilder(" from MCU mcu Where mcu.id is not null ");
		if(0 < searchType){
			if(searchType == 1) {  // SID로 검색
				sbQuery.append("and mcu.sysID in(");
			}else if(searchType == 2){   // ip로 검색
				sbQuery.append("and mcu.ipAddr in(");
			}else {
				return null;
			}

			for(String sid : targetList){
				sbQuery.append(sid + ",");
			}
			sbQuery.deleteCharAt(sbQuery.lastIndexOf(","));
			sbQuery.append(")");
			sbQuery.trimToSize();
		}

		logger.debug("################### sbQuery = " + sbQuery.toString());

		Query query = getSessionFactory().getCurrentSession().createQuery(sbQuery.toString());
		return query.list();
	}


	@Override
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<MCUTypeByLocationVO> getMCUTypeByLocationDataBack()  {

		StringBuffer sbQuery = new StringBuffer()
		.append(" SELECT t4.MCU_TYPE, t5.name, t4.CNT, t5.id                                              \n")
		.append("   FROM (SELECT t1.MCU_TYPE, t2.LOCATION_ID, t2.CNT                                      \n")
		.append("           FROM (SELECT mcuType.MCU_TYPE, loc.LOCATION_ID                                \n")
		.append("                   FROM (SELECT LOCATION_ID FROM MCU GROUP BY LOCATION_ID) loc,          \n")
		.append("                        (SELECT MCU_TYPE FROM MCU GROUP BY MCU_TYPE) mcuType             \n")
		.append("                  GROUP BY mcuType.MCU_TYPE, loc.LOCATION_ID                             \n")
		.append("                 HAVING mcuType.MCU_TYPE is not null AND loc.LOCATION_ID is not null) t1 \n")
		.append("           LEFT OUTER JOIN                                                               \n")
		.append("                (SELECT mcu.MCU_TYPE, mcu.LOCATION_ID, count(*) CNT                      \n")
		.append("                   FROM MCU mcu                                                          \n")
		.append("                  GROUP BY mcu.MCU_TYPE, mcu.LOCATION_ID) t2                             \n")
		.append("             ON t1.MCU_TYPE = t2.MCU_TYPE                                                \n")
		.append("            AND t1.LOCATION_ID = t2.LOCATION_ID) t4,                                     \n")
		.append("        location t5                                                                      \n")
		.append("       WHERE t4.location_id = t5.id                                                      \n");

		List<Object[]> result = null;

		SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
		result = query.list();

		Object[] resultData = null;
		int maxDisplaySize = 4;              // 화면에 보여줄 로케이션 수
		int currentDisplaySize = 0;          // 현재 로케이션 수
		String currentMcuTypeName = null;    // 현재 mcuType
		String pastMcuTypeName = "";         // 과거 mcuType

		List<MCUTypeByLocationVO> rtnValues = new ArrayList<MCUTypeByLocationVO>();
		MCUTypeByLocationVO rtnValue = null;

		for(int i = 0, size = result.size() ; i < size ; i++) {

			resultData = result.get(i);
			currentMcuTypeName = resultData[0].toString();

			// MCUTYPE 이름이 바뀌면..
			if(!pastMcuTypeName.equals(currentMcuTypeName)) {

				if(!"".equals(pastMcuTypeName)) {
					rtnValues.add(rtnValue);
				}

				pastMcuTypeName = currentMcuTypeName;
				currentDisplaySize = 0;
				rtnValue = new MCUTypeByLocationVO();
				rtnValue.setMcuType(currentMcuTypeName);
			}

			if(currentDisplaySize > maxDisplaySize)
				continue;

			if(currentDisplaySize == 0) {
				rtnValue.setLocationName1(resultData[1].toString());
				rtnValue.setLocationCnt1(resultData[2].toString());
				rtnValue.setLocationId1(resultData[3].toString());
			} else if(currentDisplaySize == 1) {
				rtnValue.setLocationName2(resultData[1].toString());
				rtnValue.setLocationCnt2(resultData[2].toString());
				rtnValue.setLocationId2(resultData[3].toString());
			} else if(currentDisplaySize == 2) {
				rtnValue.setLocationName3(resultData[1].toString());
				rtnValue.setLocationCnt3(resultData[2].toString());
				rtnValue.setLocationId3(resultData[3].toString());
			} else if(currentDisplaySize == 3) {
				rtnValue.setLocationName4(resultData[1].toString());
				rtnValue.setLocationCnt4(resultData[2].toString());
				rtnValue.setLocationId4(resultData[3].toString());
			}

			// 마지막 MCUTYPE 정보들 목록에 포함시킴
			if(size - 1 == i) {
				rtnValues.add(rtnValue);
			}

			currentDisplaySize++;
		}

		return rtnValues;
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public String getMCUTypeByLocationData(String supplierId)  {

		StringBuffer sbQuery = new StringBuffer()
		 .append(" SELECT t6.code, t6.name locationName, t5.name codeName, t4.CNT, t5.id                    \n")
		 .append("   FROM (SELECT t1.MCU_TYPE, t2.LOCATION_ID, t2.CNT                                       \n")
		 .append("           FROM (SELECT mcuType.MCU_TYPE, loc.LOCATION_ID                                 \n")
		 .append("                   FROM (SELECT LOCATION_ID FROM MCU WHERE SUPPLIER_ID= :supplierId GROUP BY LOCATION_ID ) loc,           \n")
		 .append("                        (SELECT MCU_TYPE FROM MCU GROUP BY MCU_TYPE) mcuType              \n")
		 .append("                  GROUP BY mcuType.MCU_TYPE, loc.LOCATION_ID                              \n")
		 .append("                 HAVING mcuType.MCU_TYPE is not null AND loc.LOCATION_ID is not null) t1  \n")
		 .append("           LEFT OUTER JOIN                                                                \n")
		 .append("                (SELECT mcu.MCU_TYPE, mcu.LOCATION_ID, count(*) CNT                       \n")
		 .append("                   FROM MCU mcu, CODE code                                                \n")
		 .append("                  WHERE mcu.MCU_TYPE = code.id                                            \n")
		 .append("                    AND mcu.SUPPLIER_ID = :supplierId                                     \n")
		 .append("                  GROUP BY mcu.MCU_TYPE, mcu.LOCATION_ID) t2                              \n")
		 .append("             ON t1.MCU_TYPE = t2.MCU_TYPE                                                 \n")
		 .append("            AND t1.LOCATION_ID = t2.LOCATION_ID) t4,                                      \n")
		 .append("        location t5, CODE t6                                                              \n")
		 .append("       WHERE t4.location_id = t5.id                                                       \n")
		 .append("         AND t4.MCU_TYPE = t6.id                                                          \n");

		List<Object[]> result = null;

		SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
		query.setInteger("supplierId", Integer.parseInt(supplierId));
		result = query.list();

		Object[] resultData = null;
		int maxDisplaySize = 4;              // 화면에 보여줄 로케이션 수
		int currentDisplaySize = 1;          // 현재 로케이션 수
		String currentMcuTypeName = null;    // 현재 mcuType
		String pastMcuTypeName = "";         // 과거 mcuType

		List<Map<String, String>> rtnValues = new ArrayList<Map<String, String>>();
		Map<String, String> map = null;

		for(int i = 0, size = result.size() ; i < size ; i++) {

			resultData = result.get(i);
			currentMcuTypeName = resultData[1].toString();

			// MCUTYPE 이름이 바뀌면..
			if(!pastMcuTypeName.equals(currentMcuTypeName)) {

				if(!"".equals(pastMcuTypeName)) {
					rtnValues.add(map);
				}

				pastMcuTypeName = currentMcuTypeName;
				currentDisplaySize = 1;
				map = new HashMap<String, String>();
				map.put("mcuType", currentMcuTypeName);
			}

			if(currentDisplaySize >= maxDisplaySize)
				continue;

			map.put("locationName" + currentDisplaySize, resultData[2].toString());
			map.put("locationCnt" + currentDisplaySize, resultData[3].toString());
			map.put("locationId" + currentDisplaySize, resultData[4].toString());
			map.put("mcuType" + currentDisplaySize, resultData[0].toString());

			// 마지막 MCUTYPE 정보들 목록에 포함시킴
			if(size - 1 == i) {
				map.put("locationSize", String.valueOf(currentDisplaySize));
				rtnValues.add(map);
			}

			currentDisplaySize++;
		}

		return JSONArray.fromObject(rtnValues).toString();
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public List<Object> getMCUTypeListByLocationData(String supplierId) {
		StringBuffer sbQuery = new StringBuffer()
		 .append(" SELECT t6.code, t6.name locationName, t5.name codeName, t4.CNT, t5.id                    \n")
		 .append("   FROM (SELECT t1.MCU_TYPE, t2.LOCATION_ID, t2.CNT                                       \n")
		 .append("           FROM (SELECT mcuType.MCU_TYPE, loc.LOCATION_ID                                 \n")
		 .append("                   FROM (SELECT LOCATION_ID FROM MCU WHERE SUPPLIER_ID = :supplierId GROUP BY LOCATION_ID) loc,           \n")
		 .append("                        (SELECT MCU_TYPE FROM MCU GROUP BY MCU_TYPE) mcuType              \n")
		 .append("                  GROUP BY mcuType.MCU_TYPE, loc.LOCATION_ID                              \n")
		 .append("                 HAVING mcuType.MCU_TYPE is not null AND loc.LOCATION_ID is not null) t1  \n")
		 .append("           LEFT OUTER JOIN                                                                \n")
		 .append("                (SELECT mcu.MCU_TYPE, mcu.LOCATION_ID, count(*) CNT                       \n")
		 .append("                   FROM MCU mcu, CODE code                                                \n")
		 .append("                  WHERE mcu.MCU_TYPE = code.id                                            \n")
		 .append("                  GROUP BY mcu.MCU_TYPE, mcu.LOCATION_ID) t2                              \n")
		 .append("             ON t1.MCU_TYPE = t2.MCU_TYPE                                                 \n")
		 .append("            AND t1.LOCATION_ID = t2.LOCATION_ID) t4,                                      \n")
		 .append("        location t5, CODE t6                                                              \n")
		 .append("       WHERE t4.location_id = t5.id                                                       \n")
		 .append("         AND t4.MCU_TYPE = t6.id                                                          \n");

		SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
		query.setInteger("supplierId", Integer.parseInt(supplierId));
		return query.list();
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<MCUTypeByCommStateVO> getMCUTypeByCommStateData(String supplierId) {

		// 네이티브 쿼리임
		StringBuffer sbQuery = new StringBuffer()
		.append("   SELECT CODE,NAME,                                                                               ")
		.append("        MAX(CASE WHEN T1.COMMSTATE = 0 THEN T1.CNT ELSE 0 END) NORMAL,                             ")
		.append("        MAX(CASE WHEN T1.COMMSTATE = 1 THEN T1.CNT ELSE 0 END) TFTIME,                             ")
		.append("        MAX(CASE WHEN T1.COMMSTATE = 2 THEN T1.CNT ELSE 0 END) FETIME                              ")
		.append("   FROM (                                                                                          ")
		.append("		SELECT CODE, NAME, COMMSTATE, COUNT(*) CNT                                                  ")
		.append(" 	        FROM (SELECT code.CODE, code.NAME,                                                      ")
		.append(" 	                     CASE WHEN (LAST_COMM_DATE  >= :TFDate) THEN 0                              ")
		.append(" 		                      WHEN (LAST_COMM_DATE < :TFDate AND LAST_COMM_DATE >= :FEDate) THEN 1  ")
		.append(" 		                      WHEN (LAST_COMM_DATE < :FEDate) THEN 2                                ")
		.append(" 		                 END COMMSTATE                                                              ")
		.append(" 		            FROM MCU mcu, Code code                                                         ")
		.append(" 		           WHERE mcu.MCU_TYPE = code.id                                                     ")
		.append(" 		           AND mcu.SUPPLIER_ID = :supplierId                                                ")
		.append(" 		            ) a                                                                             ")
		.append(" 		    GROUP BY a.CODE, a.NAME, a.COMMSTATE) T1                                                ")
		.append("  GROUP BY CODE, NAME                                                                                      ");

		Map<String, String> TFBeforeHour = DateTimeUtil.calcDate(Calendar.HOUR, -24);
		Map<String, String> FEBeforeHour = DateTimeUtil.calcDate(Calendar.HOUR, -48);

		String TFDate = TFBeforeHour.get("date").replace("-", "") + TFBeforeHour.get("time").replace(".", "");
		String FEDate = FEBeforeHour.get("date").replace("-", "") + FEBeforeHour.get("time").replace(".", "");

		SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
		query.setString("TFDate", TFDate);
		query.setString("FEDate", FEDate);
		query.setInteger("supplierId", Integer.parseInt(supplierId));

		List<Object[]> result = query.list();

		List<MCUTypeByCommStateVO> rtnValues = new ArrayList<MCUTypeByCommStateVO>();
		MCUTypeByCommStateVO rtnValue = null;

		for(int i = 0, size = result.size() ; i < size ; i++) {

			Object[] resultData = result.get(i);

			if(resultData[0] != null) {

				rtnValue = new MCUTypeByCommStateVO();
				rtnValue.setMcuTypeCode(resultData[0].toString());
				rtnValue.setMcuType(resultData[1].toString());
				rtnValue.setNormalCnt(resultData[2].toString());
				rtnValue.setTwentyfourCnt(resultData[3].toString());
				rtnValue.setFortyeightCnt(resultData[4].toString());

				rtnValues.add(rtnValue);
			}
		}

		return rtnValues;
	}

	@Override
	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public String getLocationByMCUTypeData(String supplierId) {

		StringBuffer sbQuery = new StringBuffer()
		.append("   SELECT t1.name as name, t1.location_id as id,                                                      \n");
		int j = 0;
		UsingMCUType[] usingMCUTypes = UsingMCUType.values();
		for(UsingMCUType mcuType : usingMCUTypes) {
			sbQuery.append(" MAX(CASE WHEN t1.CODE = '" + mcuType.getCode() +"' THEN t1.cnt ELSE 0 END) as " + mcuType.name() + " ");
			if(j != usingMCUTypes.length - 1) sbQuery.append(", ");	j++;
		}
		sbQuery.append("     FROM (SELECT loc.name, code.code, mcu.location_id, count(*) cnt                        \n")
		.append("             FROM MCU mcu, LOCATION loc, Code code                                          \n")
		.append("            WHERE mcu.location_id = loc.id                                                  \n")
		.append("              AND mcu.mcu_type = code.id                                                    \n")
		.append("              AND mcu.supplier_id = :supplierId                                             \n")
		.append("            GROUP BY loc.name, code.code, mcu.location_id) t1                               \n")
		.append("    GROUP BY t1.name, t1.location_id                                                        \n");

		int displaySize = 6; // 화면에 표시해줄 지역 수

		SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
		query.setInteger("supplierId", Integer.parseInt(supplierId));
		query.setMaxResults(displaySize);

		return JSONArray.fromObject(query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list()).toString();
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public List<Map<String, Object>> getLocationListByMCUTypeData(String supplierId) {

		StringBuffer sbQuery = new StringBuffer()
		.append("   SELECT t1.name as name, t1.location_id as id,                                                      \n");
		int j = 0;
		UsingMCUType[] usingMCUTypes = UsingMCUType.values();
		for(UsingMCUType mcuType : usingMCUTypes) {
			sbQuery.append(" MAX(CASE WHEN t1.CODE = '" + mcuType.getCode() +"' THEN t1.cnt ELSE 0 END) as " + mcuType.name() + " ");
			if(j != usingMCUTypes.length - 1) sbQuery.append(", ");	j++;
		}
		sbQuery.append("     FROM (SELECT loc.name, code.code, mcu.location_id, count(*) cnt                        \n")
		.append("             FROM MCU mcu, LOCATION loc, Code code                                          \n")
		.append("            WHERE mcu.location_id = loc.id                                                  \n")
		.append("              AND mcu.mcu_type = code.id                                                    \n")
		.append("              AND mcu.supplier_id = :supplierId                                             \n")
		.append("            GROUP BY loc.name, code.code, mcu.location_id) t1                               \n")
		.append("    GROUP BY t1.name, t1.location_id                                                        \n");

		int displaySize = 6; // 화면에 표시해줄 지역 수

		SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
		query.setInteger("supplierId", Integer.parseInt(supplierId));
		query.setMaxResults(displaySize);

		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<LocationByCommStateVO> getLocationByCommStateData(String supplierId) {

		StringBuffer sbQuery = new StringBuffer()
		.append(" SELECT t3.NAME, t3.id,                                                                           \n")
		.append("        MAX(CASE WHEN t2.COMMSTATE = 0 THEN t2.CNT ELSE 0 END) NORMAL,                            \n")
		.append("        MAX(CASE WHEN t2.COMMSTATE = 1 THEN t2.CNT ELSE 0 END) TFTIME,                            \n")
		.append("        MAX(CASE WHEN t2.COMMSTATE = 2 THEN t2.CNT ELSE 0 END) FETIME                             \n")
		.append("   FROM (SELECT t1.LOCATION_ID, t1.commstate, count(*) cnt                                        \n")
		.append("	        FROM (SELECT LOCATION_ID,                                                              \n")
		.append(" 	                   CASE WHEN (LAST_COMM_DATE  >= :TFDate) THEN 0                               \n")
		.append(" 		                   WHEN (LAST_COMM_DATE < :TFDate AND LAST_COMM_DATE >= :FEDate) THEN 1    \n")
		.append(" 		                   WHEN (LAST_COMM_DATE < :FEDate) THEN 2                                  \n")
		.append(" 		               END COMMSTATE   FROM MCU                                                    \n")
		.append(" 		               WHERE SUPPLIER_ID = :supplierId                                             \n")
		.append(" 		              ) t1                                                                         \n")
		.append(" 		   GROUP BY t1.LOCATION_ID, t1.commstate) t2,                                              \n")
		.append(" 		  LOCATION t3                                                                              \n")
		.append("   WHERE t2.location_id = t3.id                                                                   \n")
		.append("   GROUP BY t3.NAME, t3.id                                                                        \n");

		int displaySize = 6; // 화면에 표시해줄 지역 수

		Map<String, String> TFBeforeHour = DateTimeUtil.calcDate(Calendar.HOUR, -24);
		Map<String, String> FEBeforeHour = DateTimeUtil.calcDate(Calendar.HOUR, -48);

		String TFDate = TFBeforeHour.get("date").replace("-", "") + TFBeforeHour.get("time").replace(".", "");
		String FEDate = FEBeforeHour.get("date").replace("-", "") + FEBeforeHour.get("time").replace(".", "");

		SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
		query.setString("TFDate", TFDate);
		query.setString("FEDate", FEDate);
		query.setInteger("supplierId", Integer.parseInt(supplierId));
		query.setMaxResults(displaySize);

		List<Object[]> result = query.list();

		List<LocationByCommStateVO> rtnValues = new ArrayList<LocationByCommStateVO>();
		LocationByCommStateVO rtnValue = null;
		Object[] resultData = null;

		for(int i = 0, size = result.size() ; i < size ; i++) {

			resultData = result.get(i);

			rtnValue = new LocationByCommStateVO();
			rtnValue.setName(resultData[0].toString());
			rtnValue.setId(resultData[1].toString());
			rtnValue.setNormalCnt(resultData[2].toString());
            rtnValue.setTwentyfourCnt(resultData[3].toString());
     		rtnValue.setFortyeightCnt(resultData[4].toString());

     		rtnValues.add(rtnValue);
		}

		return rtnValues;
	}

//	public List<CommStateByMCUTypeVO> getCommStateByMCUTypeData() {
	@Override
	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public String getCommStateByMCUTypeData(String supplierId) {

		StringBuffer sbQuery = new StringBuffer()
		.append(" SELECT t2.commstate as commstate,                                                                  \n");
//		.append("        MAX(CASE WHEN t2.CODE = '1.1.1.3' THEN t2.cnt ELSE 0 END) DCU,                        	 \n")
//		.append("        MAX(CASE WHEN t2.CODE = '1.1.1.4' THEN t2.cnt ELSE 0 END) Indoor,                           \n")
//		.append("        MAX(CASE WHEN t2.CODE = '1.1.1.5' THEN t2.cnt ELSE 0 END) Outdoor                           \n")
		int j = 0;
		UsingMCUType[] usingMCUTypes = UsingMCUType.values();
		for(UsingMCUType mcuType : usingMCUTypes) {
			sbQuery.append(" MAX(CASE WHEN t2.CODE = '" + mcuType.getCode() +"' THEN t2.cnt ELSE 0 END) as " + mcuType.name() + " ");
			if(j != usingMCUTypes.length - 1) sbQuery.append(", ");	j++;
		}
		sbQuery.append("   FROM (SELECT t1.commstate, t1.CODE, count(*) cnt                                         \n")
		.append("           FROM (SELECT CODE,                                                               	     \n")
		.append(" 	                      CASE WHEN (LAST_COMM_DATE >= :TFDate) THEN 0                               \n")
		.append(" 		                       WHEN (LAST_COMM_DATE < :TFDate AND LAST_COMM_DATE >= :FEDate) THEN 1  \n")
		.append(" 		                       WHEN (LAST_COMM_DATE < :FEDate) THEN 2                                \n")
		.append(" 	                       END commstate                                                             \n")
		.append(" 	                 FROM MCU, CODE                                                                  \n")
		.append("				 WHERE MCU.MCU_TYPE = CODE.ID AND MCU.SUPPLIER_ID=:supplierId) t1                    \n")
		.append("           GROUP BY t1.commstate, t1.CODE) t2                                                       \n")
		.append("  GROUP BY t2.commstate                                                                             \n")
		.append(" HAVING t2.commstate is not null                                                                    \n");

		Map<String, String> TFBeforeHour = DateTimeUtil.calcDate(Calendar.HOUR, -24);
		Map<String, String> FEBeforeHour = DateTimeUtil.calcDate(Calendar.HOUR, -48);

		String TFDate = TFBeforeHour.get("date").replace("-", "") + TFBeforeHour.get("time").replace(".", "");
		String FEDate = FEBeforeHour.get("date").replace("-", "") + FEBeforeHour.get("time").replace(".", "");

		SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
		query.setString("TFDate", TFDate);
		query.setString("FEDate", FEDate);
		query.setInteger("supplierId", Integer.parseInt(supplierId));
//		List result = query.list();
//
//		List<CommStateByMCUTypeVO> rtnValues = new ArrayList<CommStateByMCUTypeVO>();
//		CommStateByMCUTypeVO rtnValue = null;
//		Object[] resultData = null;
//
//		for(int i = 0, size = result.size() ; i < size ; i++) {
//
//			resultData = (Object[])result.get(i);
//
//			// indoor, outdoor, dcu
//			rtnValue = new CommStateByMCUTypeVO();
//			rtnValue.setCommState(pastCommStateToName(resultData[0].toString()));
//			rtnValue.setIndoorCnt(resultData[4].toString());
//			rtnValue.setOutdoorCnt(resultData[5].toString());
////			rtnValue.setDCUCnt(resultData[8].toString());
//
//			rtnValues.add(rtnValue);
//		}

		return JSONArray.fromObject(query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list()).toString();
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public List<Map<String, Object>> getCommStateListByMCUTypeData(String supplierId) {

		StringBuffer sbQuery = new StringBuffer()
		.append(" SELECT t2.commstate as commstate,                                                                  \n");
//		.append("        MAX(CASE WHEN t2.CODE = '1.1.1.3' THEN t2.cnt ELSE 0 END) DCU,                        	 \n")
//		.append("        MAX(CASE WHEN t2.CODE = '1.1.1.4' THEN t2.cnt ELSE 0 END) Indoor,                           \n")
//		.append("        MAX(CASE WHEN t2.CODE = '1.1.1.5' THEN t2.cnt ELSE 0 END) Outdoor                           \n")
		int j = 0;
		UsingMCUType[] usingMCUTypes = UsingMCUType.values();
		for(UsingMCUType mcuType : usingMCUTypes) {
			sbQuery.append(" MAX(CASE WHEN t2.CODE = '" + mcuType.getCode() +"' THEN t2.cnt ELSE 0 END) as " + mcuType.name() + " ");
			if(j != usingMCUTypes.length - 1) sbQuery.append(", ");	j++;
		}
		sbQuery.append("   FROM (SELECT t1.commstate, t1.CODE, count(*) cnt                                         \n")
		.append("           FROM (SELECT CODE,                                                               	     \n")
		.append(" 	                      CASE WHEN (LAST_COMM_DATE >= :TFDate) THEN 0                               \n")
		.append(" 		                       WHEN (LAST_COMM_DATE < :TFDate AND LAST_COMM_DATE >= :FEDate) THEN 1  \n")
		.append(" 		                       WHEN (LAST_COMM_DATE < :FEDate) THEN 2                                \n")
		.append(" 	                       END commstate                                                             \n")
		.append(" 	                 FROM MCU, CODE                                                                  \n")
		.append("				 WHERE MCU.MCU_TYPE = CODE.ID AND MCU.SUPPLIER_ID=:supplierId) t1                    \n")
		.append("           GROUP BY t1.commstate, t1.CODE) t2                                                       \n")
		.append("  GROUP BY t2.commstate                                                                             \n")
		.append(" HAVING t2.commstate is not null                                                                    \n");

		Map<String, String> TFBeforeHour = DateTimeUtil.calcDate(Calendar.HOUR, -24);
		Map<String, String> FEBeforeHour = DateTimeUtil.calcDate(Calendar.HOUR, -48);

		String TFDate = TFBeforeHour.get("date").replace("-", "") + TFBeforeHour.get("time").replace(".", "");
		String FEDate = FEBeforeHour.get("date").replace("-", "") + FEBeforeHour.get("time").replace(".", "");

		SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
		query.setString("TFDate", TFDate);
		query.setString("FEDate", FEDate);
		query.setInteger("supplierId", Integer.parseInt(supplierId));
		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<CommStateByLocationVO> getCommStateByLocationDataBack() {

		Map<String, String> TFBeforeHour = DateTimeUtil.calcDate(Calendar.HOUR, -24);
		Map<String, String> FEBeforeHour = DateTimeUtil.calcDate(Calendar.HOUR, -48);

		String TFDate = TFBeforeHour.get("date").replace("-", "") + TFBeforeHour.get("time").replace(".", "");
		String FEDate = FEBeforeHour.get("date").replace("-", "") + FEBeforeHour.get("time").replace(".", "");

		StringBuffer sbQuery = new StringBuffer()
		.append(" SELECT t3.commstate, t4.name, t4.id, t3.cnt                                                                                     \n")
		.append("   FROM (SELECT t1.commstate, t1.LOCATION_ID, t2.cnt                                                                             \n")
		.append("           FROM (SELECT cs.commstate, li.location_id                                                                             \n")
		.append("	              FROM (SELECT DISTINCT(CASE WHEN (LAST_COMM_DATE  >= '" + TFDate + "') THEN 0                                    \n")
		.append("		  	                               WHEN (LAST_COMM_DATE < '" + TFDate + "' AND LAST_COMM_DATE >= '" + FEDate + "') THEN 1 \n")
		.append("			                               WHEN (LAST_COMM_DATE < '" + FEDate + "') THEN 2                                        \n")
		.append("			                           END) commstate                                                                             \n")
		.append("	 	                 FROM MCU) cs,                                                                                            \n")
		.append("	 	               (SELECT LOCATION_ID FROM MCU GROUP BY LOCATION_ID HAVING LOCATION_ID is not null) li) t1                   \n")
		.append("            LEFT OUTER JOIN                                                                                                      \n")
		.append("                 (SELECT LOCATION_ID,                                                                                            \n")
		.append("                         CASE WHEN (LAST_COMM_DATE  >= '" + TFDate + "') THEN 0                                                  \n")
		.append("		  	               WHEN (LAST_COMM_DATE < '" + TFDate + "' AND LAST_COMM_DATE >= '" + FEDate + "') THEN 1                 \n")
		.append("			               WHEN (LAST_COMM_DATE < '" + FEDate + "') THEN 2 			                                              \n")
		.append("			           END commstate,                                                                                             \n")
		.append("			          COUNT(*) CNT                                                                                                \n")
		.append("	               FROM MCU                                                                                                       \n")
		.append("	              GROUP BY LOCATION_ID,                                                                                           \n")
		.append("	                       (CASE WHEN (LAST_COMM_DATE  >= '" + TFDate + "') THEN 0                                                \n")
		.append("		  	                   WHEN (LAST_COMM_DATE < '" + TFDate + "' AND LAST_COMM_DATE >= '" + FEDate + "') THEN 1             \n")
		.append("			                   WHEN (LAST_COMM_DATE < '" + FEDate + "') THEN 2 			                                          \n")
		.append("			               END)) t2                                                                                               \n")
		.append("              ON t1.LOCATION_ID = t2.LOCATION_ID                                                                                 \n")
		.append("             AND t1.COMMSTATE = t2.COMMSTATE	) t3,                                                                             \n")
		.append("        LOCATION t4                                                                                                              \n")
		.append("  WHERE t3.location_id = t4.id                                                                                                   \n")
		.append("    AND t3.commstate is not null                                                                                                 \n");

		SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
		List<Object[]> result = query.list();

		List<CommStateByLocationVO> rtnValues = new ArrayList<CommStateByLocationVO>();
		CommStateByLocationVO rtnValue = new CommStateByLocationVO();

		Object[] resultData = null;
		int maxDisplaySize = 4;              // 화면에 보여줄 로케이션 수
		int currentDisplaySize = 0;          // 현재 로케이션 수
		String currentCommState = null;    // 현재 mcuType
		String pastCommState = "";         // 과거 mcuType
		String cnt = null;
		String locationId = null;

		for(int i = 0, size = result.size() ; i < size ; i++) {

			resultData = result.get(i);
			currentCommState = resultData[0].toString();

			if(!pastCommState.equals(currentCommState)) {

				if(!"".equals(pastCommState)) {
					rtnValue.setCommState(pastCommStateToName(pastCommState));
					rtnValues.add(rtnValue);

					rtnValue = new CommStateByLocationVO();
					currentDisplaySize = 0;
				}

				pastCommState = currentCommState;
			}

			if(currentDisplaySize > maxDisplaySize)
				continue;

			if(resultData[3] != null)
				cnt = resultData[3].toString();
			else
				cnt = "0";

			if(resultData[2] != null)
				locationId = resultData[2].toString();
			else
				locationId = "";

			if(currentDisplaySize == 0) {
				rtnValue.setLocationName1(resultData[1].toString());
				rtnValue.setLocationCnt1(cnt);
				rtnValue.setLocationId1(locationId);
			} else if(currentDisplaySize == 1) {
				rtnValue.setLocationName2(resultData[1].toString());
				rtnValue.setLocationCnt2(cnt);
				rtnValue.setLocationId2(locationId);
			} else if(currentDisplaySize == 2) {
				rtnValue.setLocationName3(resultData[1].toString());
				rtnValue.setLocationCnt3(cnt);
				rtnValue.setLocationId3(locationId);
			} else if(currentDisplaySize == 3) {
				rtnValue.setLocationName4(resultData[1].toString());
				rtnValue.setLocationCnt4(cnt);
				rtnValue.setLocationId4(locationId);
			}

			// 마지막 MCUTYPE 정보들 목록에 포함시킴
			if(size - 1 == i) {
				rtnValue.setCommState(pastCommStateToName(pastCommState));
				rtnValues.add(rtnValue);
			}

			currentDisplaySize++;
		}

		return rtnValues;
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public String getCommStateByLocationData(String supplierId) {

		Map<String, String> TFBeforeHour = DateTimeUtil.calcDate(Calendar.HOUR, -24);
		Map<String, String> FEBeforeHour = DateTimeUtil.calcDate(Calendar.HOUR, -48);

		String TFDate = TFBeforeHour.get("date").replace("-", "") + TFBeforeHour.get("time").replace(".", "");
		String FEDate = FEBeforeHour.get("date").replace("-", "") + FEBeforeHour.get("time").replace(".", "");

		StringBuffer sbQuery = new StringBuffer()
		.append(" SELECT t3.commstate, t4.name, t4.id, t3.cnt                                                                                     \n")
		.append("   FROM (SELECT t1.commstate, t1.LOCATION_ID, t2.cnt                                                                             \n")
		.append("           FROM (SELECT cs.commstate, li.location_id                                                                             \n")
		.append("	              FROM (SELECT DISTINCT(CASE WHEN (LAST_COMM_DATE  >= '" + TFDate + "') THEN 0                                    \n")
		.append("		  	                               WHEN (LAST_COMM_DATE < '" + TFDate + "' AND LAST_COMM_DATE >= '" + FEDate + "') THEN 1 \n")
		.append("			                               WHEN (LAST_COMM_DATE < '" + FEDate + "') THEN 2                                        \n")
		.append("			                           END) commstate                                                                             \n")
		.append("	 	                 FROM MCU WHERE SUPPLIER_ID= :supplierId) cs,                                                             \n")
		.append("	 	               (SELECT LOCATION_ID FROM MCU GROUP BY LOCATION_ID HAVING LOCATION_ID is not null) li) t1                   \n")
		.append("            LEFT OUTER JOIN                                                                                                      \n")
		.append("                 (SELECT LOCATION_ID,                                                                                            \n")
		.append("                         CASE WHEN (LAST_COMM_DATE  >= '" + TFDate + "') THEN 0                                                  \n")
		.append("		  	               WHEN (LAST_COMM_DATE < '" + TFDate + "' AND LAST_COMM_DATE >= '" + FEDate + "') THEN 1                 \n")
		.append("			               WHEN (LAST_COMM_DATE < '" + FEDate + "') THEN 2 			                                              \n")
		.append("			           END commstate,                                                                                             \n")
		.append("			          COUNT(*) CNT                                                                                                \n")
		.append("	               FROM MCU                                                                                                       \n")
		.append("	              GROUP BY LOCATION_ID,                                                                                           \n")
		.append("	                       (CASE WHEN (LAST_COMM_DATE  >= '" + TFDate + "') THEN 0                                                \n")
		.append("		  	                   WHEN (LAST_COMM_DATE < '" + TFDate + "' AND LAST_COMM_DATE >= '" + FEDate + "') THEN 1             \n")
		.append("			                   WHEN (LAST_COMM_DATE < '" + FEDate + "') THEN 2 			                                          \n")
		.append("			               END)) t2                                                                                               \n")
		.append("              ON t1.LOCATION_ID = t2.LOCATION_ID                                                                                 \n")
		.append("             AND t1.COMMSTATE = t2.COMMSTATE	) t3,                                                                             \n")
		.append("        LOCATION t4                                                                                                              \n")
		.append("  WHERE t3.location_id = t4.id                                                                                                   \n")
		.append("    AND t3.commstate is not null                                                                                                 \n");

		SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
		query.setInteger("supplierId", Integer.parseInt(supplierId));
		List<Object[]> result = query.list();

		List<Map<String, String>> rtnValues = new ArrayList<Map<String, String>>();
		Map<String, String> map = new HashMap<String, String>();

		Object[] resultData = null;
		int maxDisplaySize = 4;              // 화면에 보여줄 로케이션 수
		int currentDisplaySize = 1;          // 현재 로케이션 수
		String currentCommState = null;      // 현재 mcuType
		String pastCommState = "";           // 과거 mcuType
		String cnt = null;
		String locationId = null;

		for(int i = 0, size = result.size() ; i < size ; i++) {

			resultData = result.get(i);
			currentCommState = resultData[0].toString();

			if(!pastCommState.equals(currentCommState)) {

				if(!"".equals(pastCommState)) {
					map.put("commState", pastCommStateToName(pastCommState));
					rtnValues.add(map);

					map = new HashMap<String, String>();
					currentDisplaySize = 1;
				}

				pastCommState = currentCommState;
			}

			if(currentDisplaySize > maxDisplaySize)
				continue;

			if(resultData[3] != null)
				cnt = resultData[3].toString();
			else
				cnt = "0";

			if(resultData[2] != null)
				locationId = resultData[2].toString();
			else
				locationId = "";

			map.put("locationName" + currentDisplaySize, resultData[1].toString());
			map.put("locationCnt" + currentDisplaySize, cnt);
			map.put("locationId" + currentDisplaySize, locationId);

			// 마지막 MCUTYPE 정보들 목록에 포함시킴
			if(size - 1 == i) {
				map.put("locationSize", Integer.toString(currentDisplaySize));
				map.put("commState", pastCommStateToName(pastCommState));
				rtnValues.add(map);
			}

			currentDisplaySize++;
		}

		return JSONArray.fromObject(rtnValues).toString();
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public List<Object> getCommStateListByLocationData(String supplierId) {

		Map<String, String> TFBeforeHour = DateTimeUtil.calcDate(Calendar.HOUR, -24);
		Map<String, String> FEBeforeHour = DateTimeUtil.calcDate(Calendar.HOUR, -48);

		String TFDate = TFBeforeHour.get("date").replace("-", "") + TFBeforeHour.get("time").replace(".", "");
		String FEDate = FEBeforeHour.get("date").replace("-", "") + FEBeforeHour.get("time").replace(".", "");

		StringBuffer sbQuery = new StringBuffer()
		.append(" SELECT t3.commstate, t4.name, t4.id, t3.cnt                                                                                     \n")
		.append("   FROM (SELECT t1.commstate, t1.LOCATION_ID, t2.cnt                                                                             \n")
		.append("           FROM (SELECT cs.commstate, li.location_id                                                                             \n")
		.append("	              FROM (SELECT DISTINCT(CASE WHEN (LAST_COMM_DATE  >= '" + TFDate + "') THEN 0                                    \n")
		.append("		  	                               WHEN (LAST_COMM_DATE < '" + TFDate + "' AND LAST_COMM_DATE >= '" + FEDate + "') THEN 1 \n")
		.append("			                               WHEN (LAST_COMM_DATE < '" + FEDate + "') THEN 2                                        \n")
		.append("			                           END) commstate                                                                             \n")
		.append("	 	                 FROM MCU WHERE SUPPLIER_ID= :supplierId) cs,                                                             \n")
		.append("	 	               (SELECT LOCATION_ID FROM MCU GROUP BY LOCATION_ID HAVING LOCATION_ID is not null) li) t1                   \n")
		.append("            LEFT OUTER JOIN                                                                                                      \n")
		.append("                 (SELECT LOCATION_ID,                                                                                            \n")
		.append("                         CASE WHEN (LAST_COMM_DATE  >= '" + TFDate + "') THEN 0                                                  \n")
		.append("		  	               WHEN (LAST_COMM_DATE < '" + TFDate + "' AND LAST_COMM_DATE >= '" + FEDate + "') THEN 1                 \n")
		.append("			               WHEN (LAST_COMM_DATE < '" + FEDate + "') THEN 2 			                                              \n")
		.append("			           END commstate,                                                                                             \n")
		.append("			          COUNT(*) CNT                                                                                                \n")
		.append("	               FROM MCU                                                                                                       \n")
		.append("	              GROUP BY LOCATION_ID,                                                                                           \n")
		.append("	                       (CASE WHEN (LAST_COMM_DATE  >= '" + TFDate + "') THEN 0                                                \n")
		.append("		  	                   WHEN (LAST_COMM_DATE < '" + TFDate + "' AND LAST_COMM_DATE >= '" + FEDate + "') THEN 1             \n")
		.append("			                   WHEN (LAST_COMM_DATE < '" + FEDate + "') THEN 2 			                                          \n")
		.append("			               END)) t2                                                                                               \n")
		.append("              ON t1.LOCATION_ID = t2.LOCATION_ID                                                                                 \n")
		.append("             AND t1.COMMSTATE = t2.COMMSTATE	) t3,                                                                             \n")
		.append("        LOCATION t4                                                                                                              \n")
		.append("  WHERE t3.location_id = t4.id                                                                                                   \n")
		.append("    AND t3.commstate is not null                                                                                                 \n");

		SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
		query.setInteger("supplierId", Integer.parseInt(supplierId));
		return query.list();
	}

	private String pastCommStateToName(String commState) {

		String rtnValue = null;

		if("0".equals(commState)) {
			rtnValue = "normal";
		} else if("1".equals(commState)) {
			rtnValue = "24Hours";
		} else if("2".equals(commState)) {
			rtnValue = "48Hours";
		} else if("3".equals(commState)) {
			rtnValue = "others";
		}
		return rtnValue;
	}


	@Override
	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public List<Object> getMCUNameList(Map<String, Object> condition){

    	List<Object> result      = new ArrayList<Object>();

		String sysId       		 = StringUtil.nullToBlank(condition.get("sysId"));
		String supplierId        = StringUtil.nullToBlank(condition.get("supplierId"));


		StringBuffer sbQuery 	  = new StringBuffer();

    	sbQuery = new StringBuffer();
		sbQuery.append("  SELECT SYS_ID			 				\n")
			   .append("    FROM MCU 							\n")
 			   .append("   WHERE SYS_ID LIKE '%"+ sysId + "%'  	\n")
 			   .append("     AND SUPPLIER_ID = "+ supplierId      )
 			   .append("   ORDER BY 1 							\n");

		SQLQuery query = getSession().createSQLQuery(sbQuery.toString());


    	result.add(query.list());

		return result;

    }

    @Override
	@SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRED)
    public List<MCU> getMCUWithGpio(HashMap<String, Object> condition){
        Criteria criteria = getSession().createCriteria(MCU.class);

        Set<String> set = condition.keySet();
        Object []hmKeys = set.toArray();
        for (int i=0; i<hmKeys.length; i++) {
            String key = (String)hmKeys[i];
            if (key.equals("sysID")) {
                criteria.add(Restrictions.or(Restrictions.eq(key, condition.get(key)), Restrictions.like("sysID", "%"+condition.get(key)+"%")));
            } else {
                criteria.add(Restrictions.eq(key, condition.get(key)));
            }
        }

        // 좌표 정보가 없으면 값을 반환하지 않는다.
        criteria.add(Restrictions.isNotNull("gpioX"));
        criteria.add(Restrictions.isNotNull("gpioY"));
        criteria.add(Restrictions.isNotNull("gpioZ"));

        List<MCU> mcus = criteria.list();

        return mcus;
    }

    @Override
	@SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRED)
    public List<MCU> getMCUWithGpioCodi(HashMap<String, Object> condition){
        Criteria criteria = getSession().createCriteria(MCU.class);

        Set<String> set = condition.keySet();
        Object []hmKeys = set.toArray();
        for (int i=0; i<hmKeys.length; i++) {
            String key = (String)hmKeys[i];
            if (key.equals("sysID")) {
                criteria.add(Restrictions.or(Restrictions.eq(key, condition.get(key)), Restrictions.like("sysID", "%"+condition.get(key)+"%")));
            } else {
                criteria.add(Restrictions.eq(key, condition.get(key)));
            }
        }

        // 좌표 정보가 없으면 값을 반환하지 않는다.
        criteria.add(Restrictions.isNotNull("gpioX"));
        criteria.add(Restrictions.isNotNull("gpioY"));
        criteria.add(Restrictions.isNotNull("gpioZ"));
        criteria.add(Restrictions.isNotNull("mcuCodi"));

        List<MCU> mcus = criteria.list();

        return mcus;
    }

    @Override
	@SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRED)
    public List<MCU> getMCUWithoutGpio(HashMap<String, Object> condition){
        Criteria criteria = getSession().createCriteria(MCU.class);
        int deleteCodeId = condition.get("deleteCodeId") == null ? null : (int) condition.get("deleteCodeId");
        
        if(condition.containsKey("deleteCodeId")){
        	condition.remove("deleteCodeId");
        }
        
        Set<String> set = condition.keySet();
        Object []hmKeys = set.toArray();
        for (int i=0; i<hmKeys.length; i++) {
            String key = (String)hmKeys[i];
            
            if (key.equals("sysID")) {
                criteria.add(Restrictions.or(Restrictions.eq(key, condition.get(key)), Restrictions.like("sysID", "%"+condition.get(key)+"%")));
            } else {
                criteria.add(Restrictions.eq(key, condition.get(key)));
            }
        }
        
        // Google Maps(NMS) 삭제된 DCU(상태값이 Deleted) NMS 목록에 출력되지 않도록  Query 수행
        criteria.add(Restrictions.or(Restrictions.ne("mcuStatusCodeId", deleteCodeId), Restrictions.isNull("mcuStatus")));
        
        List<MCU> mcus = criteria.list();

        return mcus;
    }

    @Override
	@SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRED)
    public List<MCU> getMCUMapDataWithoutGpio(HashMap<String, Object> condition) {
        Criteria criteria = getSession().createCriteria(MCU.class);

        Set<String> set = condition.keySet();
        Object []hmKeys = set.toArray();

        for (int i = 0 ; i < hmKeys.length ; i++) {
            String key = (String)hmKeys[i];
            criteria.add(Restrictions.eq(key, condition.get(key)));
        }

        List<MCU> mcus = criteria.list();

        return mcus;
    }

	/**
	 * 그룹 관리 중 멤버 리스트 조회
	 *
	 * @param condition
	 * @return
	 */
    @Override
	@SuppressWarnings("unchecked")
    @Deprecated
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public List<Object> getGroupMember(Map<String, Object> condition){
    	String member = StringUtil.nullToBlank(condition.get("member"));

		StringBuffer sb = new StringBuffer();
		sb.append("SELECT t.id, t.sys_id ")
		  .append("FROM MCU t LEFT JOIN GROUP_MEMBER g ON t.sys_id = g.member ")
		  .append("WHERE t.supplier_id = :supplierId ");
		if(!"".equals(member)){
			sb.append("AND t.sys_id like '%").append((String)condition.get("member")).append("%'");
		}
		sb.append("AND t.sys_id NOT IN ( ");
			sb.append("SELECT t.sys_id ");
			sb.append("FROM MCU t RIGHT JOIN GROUP_MEMBER g ON t.sys_id = g.member ");
			sb.append("WHERE t.supplier_id = :supplierId ");
		sb.append(") ");
		sb.append(" ORDER BY t.sys_id ASC");

		SQLQuery query = getSession().createSQLQuery(sb.toString());
		return query.setInteger("supplierId", Integer.parseInt((String)condition.get("supplierId")))
					.list();
    }

    /**
     * method name : getMemberSelectData<b/>
     * method Desc : Group Management 가젯에서 Member 로 등록 가능한 MCU 리스트를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @Override
	@SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getMemberSelectData(Map<String, Object> conditionMap) {
    	
    	List<Object> gridData 	= new ArrayList<Object>();		
		List<Object> result		= new ArrayList<Object>();
		
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        Integer groupId = (Integer)conditionMap.get("groupId");
        String subType = StringUtil.nullToBlank(conditionMap.get("subType"));
        String memberName = StringUtil.nullToBlank(conditionMap.get("memberName"));
        //검색조건
        String mcuId = StringUtil.nullToBlank(conditionMap.get("mcuId"));
        String mcuSerial = StringUtil.nullToBlank(conditionMap.get("mcuSerial")); 
        String mcuType = StringUtil.nullToBlank(conditionMap.get("mcuType"));
        String swVersion = StringUtil.nullToBlank(conditionMap.get("swVersion"));
        String hwVersion = StringUtil.nullToBlank(conditionMap.get("hwVersion"));
        String installDateStart = StringUtil.nullToBlank(conditionMap.get("installDateStart"));
        String installDateEnd = StringUtil.nullToBlank(conditionMap.get("installDateEnd"));
        //String filter = StringUtil.nullToBlank(conditionMap.get("filter"));
        //String order = StringUtil.nullToBlank(conditionMap.get("order"));
        String protocol = StringUtil.nullToBlank(conditionMap.get("protocol"));
        String mcuStatus = StringUtil.nullToBlank(conditionMap.get("mcuStatus"));
        String locationId = StringUtil.nullToBlank(conditionMap.get("locationId"));
        //List<Integer> locationIdList = (List<Integer>)conditionMap.get("locationIdList");
        int page = (Integer) conditionMap.get("page");
        int limit = (Integer) conditionMap.get("limit");
        
        StringBuilder sbQuery = new StringBuilder();
        StringBuilder sb = new StringBuilder();
        
        sbQuery.append("\nSELECT mc.id AS value, ");
        sbQuery.append("\n       mc.sysID AS text, ");
        sbQuery.append("\n       'MCU' AS type, ");
        sbQuery.append("\n       loc.name AS name ");
       
        sb.append("\nFROM MCU mc ");
        sb.append("\nleft outer join mc.mcuStatus mcuStatus ");
        sb.append("\nleft outer join mc.mcuType mcuType ");
        sb.append("\nleft outer join mc.location loc ");
        sb.append("\nWHERE mc.supplier.id = :supplierId ");
        sb.append("\nAND (mc.mcuStatus is null OR mcuStatus.code <> :deleteCode) ");
        if (!memberName.isEmpty()) {
            sb.append("\nAND   mc.sysID LIKE :memberName ");
        }
        // 검색조건
        if(!mcuId.equals(""))
        	sb.append("	AND mc.sysID LIKE '"+ mcuId +"%' ");
        if(!mcuSerial.equals(""))
        	sb.append("	AND mc.sysSerialNumber LIKE '"+mcuSerial +"%' ");
        if(!mcuType.equals(""))
        	sb.append("	AND mc.mcuType = '"+ Integer.parseInt(mcuType) +"' ");
        /*if(!locationIdList.equals(""))
        	sb.append("	AND mc.location.id IN (:locationIdList) ");*/
        if(!locationId.equals(""))
        	sb.append("	AND mc.location.id = '"+ locationId +"' ");
        if(!swVersion.equals(""))
        	sb.append("	AND mc.sysSwVersion = '"+ swVersion +"' ");
        if(!hwVersion.equals(""))
        	sb.append("	AND mc.sysHwVersion = '"+ hwVersion +"' ");
        if(!installDateStart.equals(""))
        	sb.append("     AND mc.installDate >= '"+ installDateStart +"000000' ");
        if(!installDateEnd.equals(""))
        	sb.append("     AND mc.installDate <= '"+ installDateEnd +"235959' ");
        if(!protocol.equals(""))
        	sb.append("	AND mc.protocolType.id = '"+ Integer.parseInt(protocol) +"' ");
        if(!mcuStatus.equals(""))
        	sb.append("	AND mc.mcuStatus = '"+ mcuStatus +"' ");

        sb.append("\nAND   NOT EXISTS ( ");
        sb.append("\n    SELECT 'X' ");
        sb.append("\n    FROM GroupMember gm ");
        sb.append("\n    WHERE gm.member = mc.sysID ");
        if(subType.isEmpty()) {
        	sb.append("\n    AND   gm.aimirGroup.id = :groupId ");
        }
        sb.append("\n) ");
        sb.append("\nORDER BY mc.sysID ");
        // 전체 건수
        StringBuffer countQuery = new StringBuffer();
        countQuery.append("\n SELECT COUNT( * ) ");
        countQuery.append(sb);
        
        Query countQueryObj = getSession().createQuery(countQuery.toString());
        countQueryObj.setInteger("supplierId", supplierId);
        if(subType.isEmpty()) {
        	countQueryObj.setInteger("groupId", groupId);
        }
        countQueryObj.setString("deleteCode", McuStatus.Delete.getCode());
        if (!memberName.isEmpty()) {
        	countQueryObj.setString("memberName", "%" + memberName + "%");
        }
        
        Number totalCount = (Number)countQueryObj.uniqueResult();
        result.add(totalCount.toString());

        sbQuery.append(sb);
        
        Query query = getSession().createQuery(sbQuery.toString());
        query.setInteger("supplierId", supplierId);
        query.setString("deleteCode", McuStatus.Delete.getCode());
        
        if(subType.isEmpty()) {
        	query.setInteger("groupId", groupId);
        }
        if (!memberName.isEmpty()) {
            query.setString("memberName", "%" + memberName + "%");
        }
     		
        query.setFirstResult((page-1) * limit);      
        query.setMaxResults(limit);
        
        List dataList = null;
		dataList = query.list();
		
		// 실제 데이터
		int dataListLen = 0;
		if(dataList != null)
			dataListLen= dataList.size();
				
		for (int i = 0; i < dataListLen; i++) {
			HashMap chartDataMap = new HashMap();
			Object[] resultData = (Object[]) dataList.get(i);
			
			chartDataMap.put("value",           resultData[0] );
			chartDataMap.put("text",      resultData[1]);                 
			chartDataMap.put("type",    resultData[2]);
			chartDataMap.put("name",     resultData[3]);
			gridData.add(chartDataMap);
		}
		
		result.add(gridData);
		
		return result;
    }

	/**
	 * OTA Event 에서 넘어온 SQL 실행
	 **/
    @Override
	public void updateFWByotaEvent(String sql)throws Exception{
       	logger.debug(this.getClass().getName()+":"+"updateFWByotaEvent()");
       	logger.debug("sql="+sql);
		SQLQuery query = getSession().createSQLQuery(sql);
		query = getSession().createSQLQuery(sql);

    	query.executeUpdate();

    }

    // 미터의 검침데이터 목록을 조회한다.
    @Override
	@SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRED)
    public List<MCU> getMCUbyCodi(String codiID) {
        Query query = getSession().createQuery("FROM MCU m WHERE m.mcuCodi.codiID = ?");
        query.setString(1,  codiID);
        return query.list();
    }

    /**
     * method name : getMiniChartMCUTypeByCommStatus<b/>
     * method Desc : 집중기관리 미니가젯에서 통신상태별 집중기타입 정보를 조회한다.
     *
     * @param condition
     * @return
     */
    @Override
	@SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<List<Map<String, Object>>> getMiniChartMCUTypeByCommStatus(Map<String, Object> condition) {
        String supplierId = StringUtil.nullToBlank(condition.get("supplierId"));

        List<Map<String, Object>> chartData = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> chartSeries = new ArrayList<Map<String, Object>>();
        List<List<Map<String, Object>>> result = new ArrayList<List<Map<String, Object>>>();

        // chartSeries
        Map<String, Object> chartSerie1 = new HashMap<String, Object>();
        chartSerie1.put("xField", "xTag");
        chartSerie1.put("yField", "value0");
        chartSerie1.put("yCode", "0");
        chartSerie1.put("displayName", "fmtMessage00");
        chartSeries.add(chartSerie1);

        Map<String, Object> chartSerie2 = new HashMap<String, Object>();
        chartSerie2.put("xField", "xTag");
        chartSerie2.put("yField", "value1");
        chartSerie2.put("yCode", "1");
        chartSerie2.put("displayName", "fmtMessage24");
        chartSeries.add(chartSerie2);

        Map<String, Object> chartSerie3 = new HashMap<String, Object>();
        chartSerie3.put("xField", "xTag");
        chartSerie3.put("yField", "value2");
        chartSerie3.put("yCode", "2");
        chartSerie3.put("displayName", "fmtMessage48");
        chartSeries.add(chartSerie3);

        Map<String, Object> chartSerie4 = new HashMap<String, Object>();
        chartSerie4.put("xField", "xTag");
        chartSerie4.put("yField", "value3");
        chartSerie4.put("yCode", "3");
        chartSerie4.put("displayName", "fmtMessage99");
        chartSeries.add(chartSerie4);
        
        Map<String, Object> chartSerie5 = new HashMap<String, Object>();
        chartSerie4.put("xField", "xTag");
        chartSerie4.put("yField", "value4");
        chartSerie4.put("yCode", "4");
        chartSerie4.put("displayName", "CommError");
        chartSeries.add(chartSerie5);
        
        Map<String, Object> chartSerie6 = new HashMap<String, Object>();
        chartSerie4.put("xField", "xTag");
        chartSerie4.put("yField", "value5");
        chartSerie4.put("yCode", "5");
        chartSerie4.put("displayName", "PowerDown");
        chartSeries.add(chartSerie6);
        
        Map<String, Object> chartSerie7 = new HashMap<String, Object>();
        chartSerie4.put("xField", "xTag");
        chartSerie4.put("yField", "value6");
        chartSerie4.put("yCode", "6");
        chartSerie4.put("displayName", "SecurityError");
        chartSeries.add(chartSerie7);

        // chartData //여기
        StringBuilder sbQuery = new StringBuilder();

        sbQuery.append("\nSELECT mc.mcuType.descr AS xTag ");
        sbQuery.append("\n     , mc.mcuType.descr AS xCode ");
        sbQuery.append("\n     , SUM(CASE WHEN mc.lastCommDate >= :datePre24H ");
        sbQuery.append("      AND   (NOT mc.mcuStatus = " + codeDao.getCodeIdByCode("1.1.4.3") + "\n" );
        sbQuery.append("      AND   NOT mc.mcuStatus = " + codeDao.getCodeIdByCode("1.1.4.4") + "\n" );
        sbQuery.append("      AND   NOT mc.mcuStatus = " + codeDao.getCodeIdByCode("1.1.4.5") +"OR mc.mcuStatus IS NULL)"+" THEN 1 ELSE 0 END) AS value0 \n");
        sbQuery.append("\n     , SUM(CASE WHEN mc.lastCommDate < :datePre24H ");
        sbQuery.append("\n                 AND mc.lastCommDate >= :datePre48H ");
        sbQuery.append("      AND   (NOT mc.mcuStatus = " + codeDao.getCodeIdByCode("1.1.4.3") + "\n" );
        sbQuery.append("      AND   NOT mc.mcuStatus = " + codeDao.getCodeIdByCode("1.1.4.4") + "\n" );
        sbQuery.append("      AND   NOT mc.mcuStatus = " + codeDao.getCodeIdByCode("1.1.4.5") +"OR mc.mcuStatus IS NULL)"+" THEN 1 ELSE 0 END) AS value1 \n");
        sbQuery.append("\n     , SUM(CASE WHEN mc.lastCommDate < :datePre48H ");
        sbQuery.append("      AND   (NOT mc.mcuStatus = " + codeDao.getCodeIdByCode("1.1.4.3") + "\n" );
        sbQuery.append("      AND   NOT mc.mcuStatus = " + codeDao.getCodeIdByCode("1.1.4.4") + "\n" );
        sbQuery.append("      AND   NOT mc.mcuStatus = " + codeDao.getCodeIdByCode("1.1.4.5") +"OR mc.mcuStatus IS NULL)"+" THEN 1 ELSE 0 END) AS value2 \n");
        sbQuery.append("\n     , SUM(CASE WHEN mc.lastCommDate IS NULL ");
        sbQuery.append("      AND   (NOT mc.mcuStatus = " + codeDao.getCodeIdByCode("1.1.4.3") + "\n" );
        sbQuery.append("      AND   NOT mc.mcuStatus = " + codeDao.getCodeIdByCode("1.1.4.4") + "\n" );
        sbQuery.append("      AND   NOT mc.mcuStatus = " + codeDao.getCodeIdByCode("1.1.4.5") +"OR mc.mcuStatus IS NULL)"+" THEN 1 ELSE 0 END) AS value3 \n");
        sbQuery.append("     , SUM(CASE WHEN mc.mcuStatus = " + codeDao.getCodeIdByCode("1.1.4.5") + " THEN 1 ELSE 0 END) AS value4 \n");
        sbQuery.append("     , SUM(CASE WHEN mc.mcuStatus = " + codeDao.getCodeIdByCode("1.1.4.3") + " THEN 1 ELSE 0 END) AS value5 \n");
        sbQuery.append("     , SUM(CASE WHEN mc.mcuStatus = " + codeDao.getCodeIdByCode("1.1.4.4") + " THEN 1 ELSE 0 END) AS value6 \n");
        sbQuery.append("\nFROM MCU mc left outer join mc.mcuStatus ms");
        sbQuery.append("\nWHERE mc.supplierId = :supplierId ");
        sbQuery.append("\nAND (mc.mcuStatus is null OR ms.code <> :deleteCode)");
        sbQuery.append("\nGROUP BY mc.mcuType.descr, mc.mcuType.order ");
        sbQuery.append("\nORDER BY mc.mcuType.order ");

        List<Object[]> dataList = null;

        Query query = getSession().createQuery(sbQuery.toString());
        String datePre24H = DateTimeUtil.calcDate(Calendar.HOUR, -24, "yyyyMMddHHmmss");
        String datePre48H = DateTimeUtil.calcDate(Calendar.HOUR, -48, "yyyyMMddHHmmss");

        query.setString("datePre24H", datePre24H);
        query.setString("datePre48H", datePre48H);
        query.setInteger("supplierId", Integer.parseInt(supplierId));
        query.setString("deleteCode", McuStatus.Delete.getCode());
        dataList = query.list();

        HashMap<String, Object> chartDataMap = null;

        DecimalFormat dfMd = DecimalUtil.getMDStyle(supplierDao.get(Integer.parseInt(supplierId)).getMd());

        for (Object[] resultData : dataList) {
            chartDataMap = new HashMap<String, Object>();
            chartDataMap.put("xTag", resultData[0]);
            chartDataMap.put("xCode", resultData[1]);
            
            int resultDataLen = resultData.length;
            for (int j = 2; j < resultDataLen; j++) {
            	/* mini gadget - chart Data  fusionchart 값 오류 때문에 decimal 없앰 */
                chartDataMap.put("value".concat(Integer.toString(j - 2)), resultData[j]);
                //chartDataMap.put("value".concat(Integer.toString(j - 2)), dfMd.format(resultData[j]));
            }

            chartData.add(chartDataMap);
        }

        result.add(chartData);
        result.add(chartSeries);

        return result;
    }

    /**
     * method name : getMiniChartCommStatusByMCUType<b/>
     * method Desc : 집중기관리 미니가젯에서 집중기타입별 통신상태 정보를 조회한다.
     *
     * @param condition
     * @return
     */
    @Override
	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<List<Map<String, Object>>> getMiniChartCommStatusByMCUType(Map<String, Object> condition) {
        return getMiniChartCommStatusByMCUType(condition, null);
    }

    /**
     * method name : getMiniChartCommStatusByMCUType<b/>
     * method Desc : 집중기관리 미니가젯에서 집중기타입별 통신상태 정보를 조회한다.
     *
     * @param condition
     * @param arrMessage
     * @return
     */
    @Override
	@SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<List<Map<String, Object>>> getMiniChartCommStatusByMCUType(Map<String, Object> condition, String[] arrMessage) {
        String supplierId = StringUtil.nullToBlank(condition.get("supplierId"));
        String chartType = StringUtil.nullToBlank(condition.get("chartType"));

        List<Map<String, Object>> chartData = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> chartSeries = new ArrayList<Map<String, Object>>();
        List<List<Map<String, Object>>> result = new ArrayList<List<Map<String, Object>>>();

        StringBuilder sbQuery = new StringBuilder();
        StringBuilder sbQueryWhere = new StringBuilder();

        // chartSeries
        sbQuery.append("\nSELECT cd.descr AS yCode, ");
        sbQuery.append("\n       cd.descr AS displayName ");
        sbQuery.append("\nFROM code cd, ");
        sbQuery.append("\n     code pc ");
        sbQuery.append("\nWHERE pc.code = '1.1.1' ");
        sbQuery.append("\nAND   cd.parent_id = pc.id ");
        sbQuery.append("\nORDER BY cd.codeorder ");

        List<Object[]> yCodeList = null;

        SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
        yCodeList = query.list();

        int yCodeLen = 0;
        Map<String, Object> chartSerie = null;
        if (yCodeList != null)
            yCodeLen = yCodeList.size();

        for (int i = 0; i < yCodeLen; i++) {
            chartSerie = new HashMap<String, Object>();
            Object[] resultData = yCodeList.get(i);

            chartSerie.put("xField", "xTag");
            chartSerie.put("yField", "value".concat(Integer.toString(i)));
            chartSerie.put("yCode", resultData[0].toString());
            chartSerie.put("displayName", resultData[1].toString());

            chartSeries.add(chartSerie);

            sbQueryWhere.append("\n     , SUM(CASE WHEN mu.typeName = '");
            sbQueryWhere.append(resultData[0].toString());
            sbQueryWhere.append("' THEN 1 ELSE 0 END) AS value");
            sbQueryWhere.append(i).append(" ");
        }

        // chartData
        StringBuilder sbChartQuery = new StringBuilder();
        //여기2
        sbChartQuery.append("\nSELECT mu.commStatus AS xTag ");
        sbChartQuery.append("\n     , mu.commStatus AS xCode ");
        sbChartQuery.append(sbQueryWhere);
        sbChartQuery.append("\nFROM (SELECT cd.name AS typeName ");
        sbChartQuery.append("\n           , CASE WHEN mc.last_comm_date >= :datePre24H ");
        sbChartQuery.append("      			     AND   (NOT mc.mcu_status = " + codeDao.getCodeIdByCode("1.1.4.3") + "\n" );
        sbChartQuery.append("      			     AND   NOT mc.mcu_status = " + codeDao.getCodeIdByCode("1.1.4.4") + "\n" );
        sbChartQuery.append("      			     AND   NOT mc.mcu_status = " + codeDao.getCodeIdByCode("1.1.4.5") +"OR mc.mcu_status IS NULL)"+" THEN '0' \n");
        sbChartQuery.append("\n                  WHEN mc.last_comm_date < :datePre24H ");
        sbChartQuery.append("\n                   AND mc.last_comm_date >= :datePre48H ");
        sbChartQuery.append("      			     AND   (NOT mc.mcu_status = " + codeDao.getCodeIdByCode("1.1.4.3") + "\n" );
        sbChartQuery.append("      			     AND   NOT mc.mcu_status = " + codeDao.getCodeIdByCode("1.1.4.4") + "\n" );
        sbChartQuery.append("      			     AND   NOT mc.mcu_status = " + codeDao.getCodeIdByCode("1.1.4.5") +"OR mc.mcu_status IS NULL)"+" THEN '1' \n");
        sbChartQuery.append("\n                  WHEN mc.last_comm_date < :datePre48H ");
        sbChartQuery.append("      			     AND   (NOT mc.mcu_status = " + codeDao.getCodeIdByCode("1.1.4.3") + "\n" );
        sbChartQuery.append("      			     AND   NOT mc.mcu_status = " + codeDao.getCodeIdByCode("1.1.4.4") + "\n" );
        sbChartQuery.append("      			     AND   NOT mc.mcu_status = " + codeDao.getCodeIdByCode("1.1.4.5") +"OR mc.mcu_status IS NULL)"+" THEN '2' \n");
        sbChartQuery.append("\n                  WHEN mc.last_comm_date IS NULL ");
        sbChartQuery.append("      			     AND   (NOT mc.mcu_status = " + codeDao.getCodeIdByCode("1.1.4.3") + "\n" );
        sbChartQuery.append("      			     AND   NOT mc.mcu_status = " + codeDao.getCodeIdByCode("1.1.4.4") + "\n" );
        sbChartQuery.append("      			     AND   NOT mc.mcu_status = " + codeDao.getCodeIdByCode("1.1.4.5") +"OR mc.mcu_status IS NULL)"+" THEN '3' \n");
        sbChartQuery.append("\n                  WHEN mc.mcu_status = " + codeDao.getCodeIdByCode("1.1.4.5") + " THEN '4'\n");
        sbChartQuery.append("\n                  WHEN mc.mcu_status = " + codeDao.getCodeIdByCode("1.1.4.3") + " THEN '5' \n");
        sbChartQuery.append("\n                  WHEN mc.mcu_status = " + codeDao.getCodeIdByCode("1.1.4.4") + " THEN '6' \n");
        sbChartQuery.append("\n             END AS commStatus ");
        sbChartQuery.append("\n      FROM mcu mc, ");
        sbChartQuery.append("\n           code cd ");
        sbChartQuery.append("\n      WHERE mc.supplier_id = :supplierId ");
        sbChartQuery.append("\n      AND   (cd.id = mc.mcu_type AND cd.code <> :deleteCode) )mu ");
        sbChartQuery.append("\nGROUP BY mu.commStatus ");

        List<Object[]> dataList = null;

        query = getSession().createSQLQuery(sbChartQuery.toString());

        String datePre24H = DateTimeUtil.calcDate(Calendar.HOUR, -24, "yyyyMMddHHmmss");
        String datePre48H = DateTimeUtil.calcDate(Calendar.HOUR, -48, "yyyyMMddHHmmss");

        query.setString("datePre24H", datePre24H);
        query.setString("datePre48H", datePre48H);
        query.setInteger("supplierId", Integer.parseInt(supplierId));
        query.setString("deleteCode", McuStatus.Delete.getCode());
        
        dataList = query.list();

        DecimalFormat dfMd = DecimalUtil.getMDStyle(supplierDao.get(Integer.parseInt(supplierId)).getMd());

        int dataListLen = 0;
        HashMap<String, Object> chartDataMap = null;
        if (dataList != null)
            dataListLen = dataList.size();

        for (int i = 0; i < dataListLen; i++) {
            chartDataMap = new HashMap<String, Object>();
            Object[] resultData = dataList.get(i);

            chartDataMap.put("xCode", resultData[1].toString());

            if (chartDataMap.get("xCode").toString().equals("0")) {
                chartDataMap.put("xTag", "fmtMessage00");
            } else if (chartDataMap.get("xCode").toString().equals("1")) {
                chartDataMap.put("xTag", "fmtMessage24");
            } else if (chartDataMap.get("xCode").toString().equals("2")) {
                chartDataMap.put("xTag", "fmtMessage48");
            } else if (chartDataMap.get("xCode").toString().equals("3")) {
                chartDataMap.put("xTag", "fmtMessage99");
            } else if (chartDataMap.get("xCode").toString().equals("4")) {
                chartDataMap.put("xTag", "CommError");
            } else if (chartDataMap.get("xCode").toString().equals("5")) {
                chartDataMap.put("xTag", "PowerDown");
            } else if (chartDataMap.get("xCode").toString().equals("6")) {
                chartDataMap.put("xTag", "SecurityError");
            }

            if (arrMessage != null) {
                // 메시지 tag 처리
                if (chartDataMap.get("xTag").toString().equals("fmtMessage00")) {
                    chartDataMap.put("xTag", arrMessage[0]);
                } else if (chartDataMap.get("xTag").toString().equals("fmtMessage24")) {
                    chartDataMap.put("xTag", arrMessage[1]);
                } else if (chartDataMap.get("xTag").toString().equals("fmtMessage48")) {
                    chartDataMap.put("xTag", arrMessage[2]);
                } else if (chartDataMap.get("xTag").toString().equals("fmtMessage99")) {
                    chartDataMap.put("xTag", arrMessage[3]);
                }
            }

            int resultDataLen = resultData.length;
            
            if(chartType.equals("grid")) {
	            for (int j = 2; j < resultDataLen; j++) {
	                chartDataMap.put("value".concat(Integer.toString(j - 2)), dfMd.format(resultData[j]));
	            }
            }else {	/* mini gadget - chart Data  fusionchart 값 오류 때문에 decimal 없앰 */
            	for (int j = 2; j < resultDataLen; j++) {
	                chartDataMap.put("value".concat(Integer.toString(j - 2)), resultData[j]);
	            }
            }

            chartData.add(chartDataMap);
        }

        result.add(chartData);
        result.add(chartSeries);

        return result;
    }
    
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	public List<Map<String, Object>> getMcuSysIdList() {
		StringBuilder sbQuery = new StringBuilder();
		sbQuery.append("\n SELECT M.SYS_ID AS SYS_ID ");
		sbQuery.append("\n , M.PROTOCOL_TYPE AS PROTOCOL_TYPE ");
		sbQuery.append("\n , CO.NAME AS PROTOCOL_NAME ");
		sbQuery.append("\n FROM MCU M ");
		sbQuery.append("\n LEFT OUTER JOIN CODE CO ON M.PROTOCOL_TYPE = CO.ID");
		sbQuery.append("\n WHERE M.ID IS NOT NULL");
		
		SQLQuery query = getSession().createSQLQuery(sbQuery.toString());

		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		
	}
	
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public int updateDcuStatus(int mcuId, Code deleteCode) {
        StringBuffer sb = new StringBuffer();
        sb.append("UPDATE MCU \n");
        sb.append("set mcuStatus =:code \n");
        sb.append("WHERE id = :id");
        Query query = getSession().createQuery(sb.toString());
        query.setEntity("code", deleteCode);
        query.setInteger("id", mcuId);
        return query.executeUpdate();
	}
    
    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<String> getMcuByIp(String ip) {
        StringBuilder sbQuery = new StringBuilder();
        sbQuery.append("\n SELECT M.SYS_ID AS SYS_ID ");
        sbQuery.append("\n FROM MCU M ");
        sbQuery.append("\n WHERE M.IP_ADDR = :ipv4 OR M.IPV6_ADDR = :ipv6");
        
        SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
        query.setString("ipv4", ip);
        query.setString("ipv6", ip);
        
        return query.list();
    }
    
    @Override
    public List<String> getFirmwareVersionList(Map<String, Object> condition) {
    	String modelId = StringUtil.nullToBlank(condition.get("modelId"));
        String versions = StringUtil.nullToBlank(condition.get("versions"));
        String deviceId = StringUtil.nullToBlank(condition.get("deviceId"));
        String fwVersion = StringUtil.nullToBlank(condition.get("fwVersion"));
        String locationId = StringUtil.nullToBlank(condition.get("locationId"));
        String installStartDate = StringUtil.nullToBlank(condition.get("installStartDate"));
        String installEndtDate = StringUtil.nullToBlank(condition.get("installEndtDate"));
        String lastCommStartDate = StringUtil.nullToBlank(condition.get("lastCommStartDate"));
        String lastCommEndDate = StringUtil.nullToBlank(condition.get("lastCommEndDate"));
        String hwVer = StringUtil.nullToBlank(condition.get("hwVer"));
        
        
        StringTokenizer st = new StringTokenizer(locationId, ",");
        String location="";
        for(int i = 0 ; st.hasMoreTokens() ; i++){
        	location += ("'" + st.nextToken() +"',");
		}
        if(location.contains(",")){
        	location = location.substring(0, location.length()-1);
        }
        
        st = new StringTokenizer(fwVersion, ",");
        String fwVersions="";
        for(int i = 0 ; st.hasMoreTokens() ; i++){
        	fwVersions += ("'" + st.nextToken() +"',");
		}
        if(fwVersions.contains(",")){
        	fwVersions = fwVersions.substring(0, fwVersions.length()-1);
        }
        
        st = new StringTokenizer(deviceId, ", ");
        String deviceIds="";
        for(int i = 0 ; st.hasMoreTokens() ; i++){
        	deviceIds += ("(0,'" + st.nextToken() +"'),");
		}
        if(deviceIds.contains(",")){
        	deviceIds = deviceIds.substring(0, deviceIds.length()-1);
        }
        
    	StringBuffer sbQuery = new StringBuffer()
    			.append("  SELECT Distinct mcu.SYS_SW_VERSION                                                                                                     \n")
    			.append("  FROM MCU mcu INNER JOIN LOCATION loc                                                                                                   \n")
    			.append("  ON mcu.location_id = loc.id                                                                                                      \n")
    			.append("  LEFT OUTER JOIN CODE co                                                                                                      \n")
    			.append(" ON mcu.MCU_STATUS = co.ID                                                                                                        \n");
    			if(!modelId.equals("") && modelId!=null)
    				sbQuery.append("  where (mcu.DEVICEMODEL_ID = :modelId) \n");
    			sbQuery.append("  and mcu.SYS_SW_VERSION is not null \n");
    			sbQuery.append("  and mcu.location_id is not null \n");
    			if(!deviceId.equals("") && deviceId!=null)
    				sbQuery.append("  and (0, mcu.SYS_ID) IN ("+deviceIds+") \n");
    			if(!fwVersion.equals("") && fwVersion!=null)
    				sbQuery.append("  and (mcu.SYS_SW_VERSION IN ("+fwVersions+")) \n");
    			if(!locationId.equals("") && locationId!=null)
    				sbQuery.append("  and (loc.name IN ("+location+")) \n");
    			
    			if(!installStartDate.equals("") && installStartDate!=null)
    				sbQuery.append("  and mcu.INSTALL_DATE >= '"+installStartDate +"000000'\n");
    			if(!installEndtDate.equals("") && installEndtDate!=null)
    				sbQuery.append("  and mcu.INSTALL_DATE <= '"+installEndtDate +"125959'\n");
    			if(!lastCommStartDate.equals("") && lastCommStartDate!=null)
    				sbQuery.append("  and mcu.LAST_COMM_DATE >= '"+lastCommStartDate +"000000'\n");
    			if(!lastCommEndDate.equals("") && lastCommEndDate!=null)
    				sbQuery.append("  and mcu.LAST_COMM_DATE <= '"+lastCommEndDate +"125959'\n");
    			if(!hwVer.equals("") && hwVer!=null)
    				sbQuery.append("  and mcu.SYS_HW_VERSION = '"+hwVer+"' \n");
    			
    			sbQuery.append(" and (co.NAME <> 'Delete' or co.NAME IS NULL) \n");
    			sbQuery.append(" ORDER BY mcu.SYS_SW_VERSION asc \n"); // 추가

    			SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
    			if(!modelId.equals("") && modelId!=null)
    	        	query.setParameter("modelId", Integer.parseInt(modelId));
    			/*if(!deviceId.equals("") && deviceId!=null)
    				query.setParameter("deviceId", deviceId);*/
    			/*if(!fwVersion.equals("") && fwVersion!=null)
    				query.setParameter("fwVersion", fwVersion);*/
    			/*if(!locationId.equals("") && locationId!=null)
    				query.setParameter("location", location);*/
    			return query.list();
    }
    
    @Override
    public List<String> getDeviceList(Map<String, Object> condition) {
    	String modelId = StringUtil.nullToBlank(condition.get("modelId"));
        String versions = StringUtil.nullToBlank(condition.get("versions"));
        String deviceId = StringUtil.nullToBlank(condition.get("deviceId"));
        String fwVersion = StringUtil.nullToBlank(condition.get("fwVersion"));
        String locationId = StringUtil.nullToBlank(condition.get("locationId"));
        String installStartDate = StringUtil.nullToBlank(condition.get("installStartDate"));
        String installEndtDate = StringUtil.nullToBlank(condition.get("installEndtDate"));
        String lastCommStartDate = StringUtil.nullToBlank(condition.get("lastCommStartDate"));
        String lastCommEndDate = StringUtil.nullToBlank(condition.get("lastCommEndDate"));
        String hwVer = StringUtil.nullToBlank(condition.get("hwVer"));
        
        
        StringTokenizer st = new StringTokenizer(locationId, ",");
        String location="";
        for(int i = 0 ; st.hasMoreTokens() ; i++){
        	location += ("'" + st.nextToken() +"',");
		}
        if(location.contains(",")){
        	location = location.substring(0, location.length()-1);
        }
        
        st = new StringTokenizer(fwVersion, ",");
        String fwVersions="";
        for(int i = 0 ; st.hasMoreTokens() ; i++){
        	fwVersions += ("'" + st.nextToken() +"',");
		}
        if(fwVersions.contains(",")){
        	fwVersions = fwVersions.substring(0, fwVersions.length()-1);
        }
        
        st = new StringTokenizer(deviceId, ", ");
        String deviceIds="";
        for(int i = 0 ; st.hasMoreTokens() ; i++){
        	deviceIds += ("'" + st.nextToken() +"',");
		}
        if(deviceIds.contains(",")){
        	deviceIds = deviceIds.substring(0, deviceIds.length()-1);
        }
        
        st = new StringTokenizer(versions, ",");
        String version ="";
    	StringBuffer sbQuery = new StringBuffer()
    			.append("  SELECT loc.name AS DSO, loc.id                                                                                                      \n");
    			for(int i = 0 ; st.hasMoreTokens() ; i++){
    				version = st.nextToken();
    				sbQuery.append("  , count(case mcu.SYS_SW_VERSION when '" + version + "' then 1 end) || '/' || loc.id || '/"+version+"'  AS VERSION"+i+"   \n");
    			}
    			sbQuery.append("  FROM MCU mcu INNER JOIN LOCATION loc                                                                                                   \n")
    			.append("  ON mcu.location_id = loc.id                                                                                                      \n")
    			.append("  LEFT OUTER JOIN CODE co                                                                                                      \n")
    			.append(" ON mcu.MCU_STATUS = co.ID                                                                                                        \n");
    			if(!modelId.equals("") && modelId!=null)
    				sbQuery.append("  where (mcu.DEVICEMODEL_ID = :modelId) \n");
    			if(!deviceId.equals("") && deviceId!=null)
    				sbQuery.append("  and (mcu.SYS_ID IN ("+deviceIds+")) \n");
    			if(!fwVersion.equals("") && fwVersion!=null)
    				sbQuery.append("  and (mcu.SYS_SW_VERSION IN ("+fwVersions+")) \n");
    			sbQuery.append("  and (mcu.SYS_SW_VERSION IS NOT NULL) \n");
    			if(!locationId.equals("") && locationId!=null)
    				sbQuery.append("  and (loc.name IN ("+location+")) \n");
    			sbQuery.append("  and (loc.name IS NOT NULL) \n");
    			
    			if(!installStartDate.equals("") && installStartDate!=null)
    				sbQuery.append("  and mcu.INSTALL_DATE >= '"+installStartDate +"000000'\n");
    			if(!installEndtDate.equals("") && installEndtDate!=null)
    				sbQuery.append("  and mcu.INSTALL_DATE <= '"+installEndtDate +"125959'\n");
    			if(!lastCommStartDate.equals("") && lastCommStartDate!=null)
    				sbQuery.append("  and mcu.LAST_COMM_DATE >= '"+lastCommStartDate +"000000'\n");
    			if(!lastCommEndDate.equals("") && lastCommEndDate!=null)
    				sbQuery.append("  and mcu.LAST_COMM_DATE <= '"+lastCommEndDate +"125959'\n");
    			if(!hwVer.equals("") && hwVer!=null)
    				sbQuery.append("  and mcu.SYS_HW_VERSION = '"+hwVer+"' \n");
    			
    			sbQuery.append(" and (co.NAME <> 'Delete' or co.NAME IS NULL) \n");
    			sbQuery.append("  GROUP BY loc.name, loc.id                                                                                                    \n");

    			SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
    			if(!modelId.equals("") && modelId!=null)
    	        	query.setParameter("modelId", Integer.parseInt(modelId));
    			/*if(!deviceId.equals("") && deviceId!=null)
    				query.setParameter("deviceId", deviceId);*/
    			/*if(!fwVersion.equals("") && fwVersion!=null)
    				query.setParameter("fwVersion", fwVersion);*/
    			/*if(!locationId.equals("") && locationId!=null)
    				query.setParameter("location", location);*/
    			return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }
    
    
    @Override
    public List<String> getTargetList(Map<String, Object> condition) {
    	String modelId = StringUtil.nullToBlank(condition.get("modelId"));
        String versions = StringUtil.nullToBlank(condition.get("versions"));
        String deviceId = StringUtil.nullToBlank(condition.get("deviceId"));
        String fwVersion = StringUtil.nullToBlank(condition.get("fwVersion"));
        String locationId = StringUtil.nullToBlank(condition.get("locationId"));
        String installStartDate = StringUtil.nullToBlank(condition.get("installStartDate"));
        String installEndtDate = StringUtil.nullToBlank(condition.get("installEndtDate"));
        String lastCommStartDate = StringUtil.nullToBlank(condition.get("lastCommStartDate"));
        String lastCommEndDate = StringUtil.nullToBlank(condition.get("lastCommEndDate"));
        String hwVer = StringUtil.nullToBlank(condition.get("hwVer"));
        
        
        StringTokenizer st = new StringTokenizer(locationId, ",");
        String location="";
        for(int i = 0 ; st.hasMoreTokens() ; i++){
        	location += ("'" + st.nextToken() +"',");
		}
        if(location.contains(",")){
        	location = location.substring(0, location.length()-1);
        }
        
        st = new StringTokenizer(fwVersion, ",");
        String fwVersions="";
        for(int i = 0 ; st.hasMoreTokens() ; i++){
        	fwVersions += ("'" + st.nextToken() +"',");
		}
        if(fwVersions.contains(",")){
        	fwVersions = fwVersions.substring(0, fwVersions.length()-1);
        }
        
        st = new StringTokenizer(deviceId, ", ");
        String deviceIds="";
        for(int i = 0 ; st.hasMoreTokens() ; i++){
        	deviceIds += ("'" + st.nextToken() +"',");
		}
        if(deviceIds.contains(",")){
        	deviceIds = deviceIds.substring(0, deviceIds.length()-1);
        }
        
    	StringBuffer sbQuery = new StringBuffer()
    			.append("  SELECT mcu.SYS_ID||'/'||mcu.LOCATION_ID                                                                                                     \n")
    			.append("  FROM MCU mcu INNER JOIN LOCATION loc                                                                                                   \n")
    			.append("  ON mcu.location_id = loc.id                                                                                                      \n")
    			.append("  LEFT OUTER JOIN CODE co                                                                                                      \n")
    			.append(" ON mcu.MCU_STATUS = co.ID                                                                                                        \n");
    			if(!modelId.equals("") && modelId!=null)
    				sbQuery.append("  where (mcu.DEVICEMODEL_ID = :modelId) \n");
    			if(!deviceId.equals("") && deviceId!=null)
    				sbQuery.append("  and (mcu.SYS_ID IN ("+deviceIds+")) \n");
    			if(!fwVersion.equals("") && fwVersion!=null)
    				sbQuery.append("  and (mcu.SYS_SW_VERSION IN ("+fwVersions+")) \n");
    			sbQuery.append("  and (mcu.SYS_SW_VERSION IS NOT NULL) \n");
    			if(!locationId.equals("") && locationId!=null)
    				sbQuery.append("  and (loc.name IN ("+location+")) \n");
    			sbQuery.append("  and (loc.name IS NOT NULL) \n");
    			
    			if(!installStartDate.equals("") && installStartDate!=null)
    				sbQuery.append("  and mcu.INSTALL_DATE >= '"+installStartDate +"000000'\n");
    			if(!installEndtDate.equals("") && installEndtDate!=null)
    				sbQuery.append("  and mcu.INSTALL_DATE <= '"+installEndtDate +"125959'\n");
    			if(!lastCommStartDate.equals("") && lastCommStartDate!=null)
    				sbQuery.append("  and mcu.LAST_COMM_DATE >= '"+lastCommStartDate +"000000'\n");
    			if(!lastCommEndDate.equals("") && lastCommEndDate!=null)
    				sbQuery.append("  and mcu.LAST_COMM_DATE <= '"+lastCommEndDate +"125959'\n");
    			if(!hwVer.equals("") && hwVer!=null)
    				sbQuery.append("  and mcu.SYS_HW_VERSION = '"+hwVer+"' \n");
    			
    			sbQuery.append(" and (co.NAME <> 'Delete' or co.NAME IS NULL) \n");
    			sbQuery.append(" ORDER BY mcu.LOCATION_ID  asc \n"); // 추가
    			
    			SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
    			if(!modelId.equals("") && modelId!=null)
    	        	query.setParameter("modelId", Integer.parseInt(modelId));
    			/*if(!deviceId.equals("") && deviceId!=null)
    				query.setParameter("deviceId", deviceId);*/
    			/*if(!fwVersion.equals("") && fwVersion!=null)
    				query.setParameter("fwVersion", fwVersion);*/
    			/*if(!locationId.equals("") && locationId!=null)
    				query.setParameter("location", location);*/
    			return query.list();
    }
    
/*    .append("  SELECT loc.name AS DSO, loc.id                                                                                                      \n");
	for(int i = 0 ; st.hasMoreTokens() ; i++){
		version = st.nextToken();
		sbQuery.append("  , count(case mcu.SYS_SW_VERSION when '" + version + "' then 1 end) || '/' || loc.id || '/"+version+"'  AS VERSION"+i+"   \n");
	}
	sbQuery.append("  FROM MCU mcu LEFT OUTER JOIN LOCATION loc                                                                                                   \n")
	.append(" ON mcu.location_id = loc.id                                                                                                      \n")
	.append(" where mcu.id is not null                                                                                                      \n");
	if(!modelId.equals("") && modelId!=null)
		sbQuery.append("  and mcu.DEVICEMODEL_ID = :modelId \n");
	if(!deviceId.equals("") && deviceId!=null)
		sbQuery.append("  and mcu.SYS_ID = :deviceId \n");
	if(!fwVersion.equals("") && fwVersion!=null)
		sbQuery.append("  and mcu.SYS_SW_VERSION = :fwVersion \n");
	if(!locationId.equals("") && locationId!=null)
		sbQuery.append("  and loc.name IN ("+location+") \n");
	sbQuery.append("  GROUP BY loc.name, loc.id      
*/    
    


	@Override
	public List<String> getGroupMcuList(Integer groupId) {
		StringBuffer sbQuery = new StringBuffer()
			.append("\nSELECT m.sysID AS MCUID")
	        .append("\nFROM GroupMember g, MCU m, Location l ")
	        .append("\nWHERE g.member = m.sysID ")
	        .append("\nAND m.locationId = l.id ")
	        .append("\nAND g.groupId = :groupId ");
		        
	        Query query = getSession().createQuery(sbQuery.toString());
	        query.setParameter("groupId", groupId);
	        	        
	        List<String> result = query.list();
		        
		    return result;
	}

	//sp-1028
	@Override
	public List<Map<String, Object>> getValidMCUList(Map<String, Object> condition) {
		String modelId = StringUtil.nullToBlank(condition.get("modelId"));
        String deviceList = StringUtil.nullToBlank(condition.get("deviceList"));
        String supplierId = StringUtil.nullToBlank(condition.get("supplierId"));
        
        StringTokenizer st = new StringTokenizer(deviceList, ", ");
        String deviceIds="";
        for(int i = 0 ; st.hasMoreTokens() ; i++){
        	deviceIds += ("(0,'" + st.nextToken() +"'),");
		}
        if(deviceIds.contains(",")){
        	deviceIds = deviceIds.substring(0, deviceIds.length()-1);
        }
        
    	StringBuffer sbQuery = new StringBuffer()
    			.append("  SELECT  mcu.SYS_ID DEVICE_ID, mcu.SYS_SW_VERSION VERSION   	\n")
    			.append("  FROM MCU mcu INNER JOIN LOCATION loc       	\n")
    			.append("  ON mcu.location_id = loc.id                  \n")
    			.append("  LEFT OUTER JOIN CODE co                  	\n")
    			.append(" ON mcu.MCU_STATUS = co.ID                     \n");
    			
		sbQuery.append("  where (mcu.DEVICEMODEL_ID = :modelId) \n");
		sbQuery.append("  and mcu.SYS_SW_VERSION is not null 	\n");
		sbQuery.append("  and mcu.location_id is not null 		\n");
		sbQuery.append("  and (0, mcu.SYS_ID) IN (" + deviceIds + ") \n");
		sbQuery.append("  and mcu.supplier_id = :supplierId 	\n");
		sbQuery.append(" and (co.NAME <> 'Delete' or co.NAME IS NULL) \n");
		sbQuery.append(" ORDER BY mcu.SYS_SW_VERSION asc \n");

		SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
		query.setParameter("modelId", Integer.parseInt(modelId));
		query.setParameter("supplierId", Integer.parseInt(supplierId));

		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	}

	//sp-1066
	@Override
	public List<String> getCodiFirmwareVersionList(Map<String, Object> condition) {

    	String modelId = StringUtil.nullToBlank(condition.get("modelId"));
        String versions = StringUtil.nullToBlank(condition.get("versions"));
        String deviceId = StringUtil.nullToBlank(condition.get("deviceId"));
        String fwVersion = StringUtil.nullToBlank(condition.get("fwVersion"));
        String locationId = StringUtil.nullToBlank(condition.get("locationId"));
        String installStartDate = StringUtil.nullToBlank(condition.get("installStartDate"));
        String installEndtDate = StringUtil.nullToBlank(condition.get("installEndtDate"));
        String lastCommStartDate = StringUtil.nullToBlank(condition.get("lastCommStartDate"));
        String lastCommEndDate = StringUtil.nullToBlank(condition.get("lastCommEndDate"));
        String hwVer = StringUtil.nullToBlank(condition.get("hwVer"));
        
        StringTokenizer st = new StringTokenizer(locationId, ",");
        String location="";
        for(int i = 0 ; st.hasMoreTokens() ; i++){
        	location += ("'" + st.nextToken() +"',");
		}
        if(location.contains(",")){
        	location = location.substring(0, location.length()-1);
        }
        
        st = new StringTokenizer(fwVersion, ",");
        String fwVersions="";
        for(int i = 0 ; st.hasMoreTokens() ; i++){
        	fwVersions += ("'" + st.nextToken() +"',");
		}
        if(fwVersions.contains(",")){
        	fwVersions = fwVersions.substring(0, fwVersions.length()-1);
        }
        
        st = new StringTokenizer(deviceId, ", ");
        String deviceIds="";
        for(int i = 0 ; st.hasMoreTokens() ; i++){
        	deviceIds += ("(0,'" + st.nextToken() +"'),");
		}
        if(deviceIds.contains(",")){
        	deviceIds = deviceIds.substring(0, deviceIds.length()-1);
        }
        
    	StringBuffer sbQuery = new StringBuffer()
    			.append("  SELECT Distinct MCU_CODI.CODI_FW_VER                                                                                      \n")
    			.append("  FROM MCU mcu                                                                   			                                 \n")
    			.append("  LEFT OUTER JOIN MCU_CODI MCU_CODI                                                                                         \n")
    			.append("  ON MCU.MCU_CODI_ID = MCU_CODI.id                                                                                          \n")
    			.append("  INNER JOIN LOCATION loc                                                                                                   \n")
    			.append("  ON mcu.location_id = loc.id                                                                                               \n")
		    	.append("  LEFT OUTER JOIN CODE co                                                                                                   \n")
				.append("  ON mcu.MCU_STATUS = co.ID                                                                                                 \n");
				if(!modelId.equals("") && modelId!=null)
    				sbQuery.append("  where (mcu.DEVICEMODEL_ID = :modelId) \n");
    			sbQuery.append("  and mcu.SYS_SW_VERSION is not null \n");
    			sbQuery.append("  and mcu.location_id is not null \n");
    			if(!deviceIds.equals("") && deviceIds!=null)
    				sbQuery.append("  and (0, mcu.SYS_ID) IN ("+deviceIds+") \n");
    			if(!fwVersion.equals("") && fwVersion!=null)
    				sbQuery.append("  and (mcu_codi.CODI_FW_VER IN ("+fwVersions+")) \n");
    			if(!locationId.equals("") && locationId!=null)
    				sbQuery.append("  and (loc.name IN ("+location+")) \n");
    			
    			if(!installStartDate.equals("") && installStartDate!=null)
    				sbQuery.append("  and mcu.INSTALL_DATE >= '"+installStartDate +"000000'\n");
    			if(!installEndtDate.equals("") && installEndtDate!=null)
    				sbQuery.append("  and mcu.INSTALL_DATE <= '"+installEndtDate +"125959'\n");
    			if(!lastCommStartDate.equals("") && lastCommStartDate!=null)
    				sbQuery.append("  and mcu.LAST_COMM_DATE >= '"+lastCommStartDate +"000000'\n");
    			if(!lastCommEndDate.equals("") && lastCommEndDate!=null)
    				sbQuery.append("  and mcu.LAST_COMM_DATE <= '"+lastCommEndDate +"125959'\n");
    			if(!hwVer.equals("") && hwVer!=null)
    				sbQuery.append("  and mcu.SYS_HW_VERSION = '"+hwVer+"' \n");
    			
    			sbQuery.append(" and (co.NAME <> 'Delete' or co.NAME IS NULL) \n");
    			sbQuery.append(" ORDER BY MCU_CODI.CODI_FW_VER asc \n"); // 추가

    			SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
    			if(!modelId.equals("") && modelId!=null)
    	        	query.setParameter("modelId", Integer.parseInt(modelId));
    			return query.list();
	}

	@Override
	public List<String> getCodiDeviceList(Map<String, Object> condition) {
    	String modelId = StringUtil.nullToBlank(condition.get("modelId"));
        String versions = StringUtil.nullToBlank(condition.get("versions"));
        String deviceId = StringUtil.nullToBlank(condition.get("deviceId"));
        String fwVersion = StringUtil.nullToBlank(condition.get("fwVersion"));
        String locationId = StringUtil.nullToBlank(condition.get("locationId"));
        String installStartDate = StringUtil.nullToBlank(condition.get("installStartDate"));
        String installEndtDate = StringUtil.nullToBlank(condition.get("installEndtDate"));
        String lastCommStartDate = StringUtil.nullToBlank(condition.get("lastCommStartDate"));
        String lastCommEndDate = StringUtil.nullToBlank(condition.get("lastCommEndDate"));
        String hwVer = StringUtil.nullToBlank(condition.get("hwVer"));
        
        StringTokenizer st = new StringTokenizer(locationId, ",");
        String location="";
        for(int i = 0 ; st.hasMoreTokens() ; i++){
        	location += ("'" + st.nextToken() +"',");
		}
        if(location.contains(",")){
        	location = location.substring(0, location.length()-1);
        }
        
        st = new StringTokenizer(fwVersion, ",");
        String fwVersions="";
        for(int i = 0 ; st.hasMoreTokens() ; i++){
        	fwVersions += ("'" + st.nextToken() +"',");
		}
        if(fwVersions.contains(",")){
        	fwVersions = fwVersions.substring(0, fwVersions.length()-1);
        }
        
        st = new StringTokenizer(deviceId, ", ");
        String deviceIds="";
        for(int i = 0 ; st.hasMoreTokens() ; i++){
        	deviceIds += ("'" + st.nextToken() +"',");
		}
        if(deviceIds.contains(",")){
        	deviceIds = deviceIds.substring(0, deviceIds.length()-1);
        }
        
        st = new StringTokenizer(versions, ",");
        String version ="";
    	StringBuffer sbQuery = new StringBuffer()
    			.append("  SELECT loc.name AS DSO, loc.id                                                                                            \n");
		
    			for(int i = 0 ; st.hasMoreTokens() ; i++){
    				version = st.nextToken();
    				if(version.equals("null")){			//dcu-codi에서 dcu ver<1.2이라서 dcu에 codi와 연결되지 않은 dcu들(sp-1066)
    					sbQuery.append("  , count(case when MCU.sys_sw_version < 1.2 then 1 end) || '/' || loc.id || '/"+version+"'  AS VERSION"+i+" 		 \n");
    				}else {
	    				sbQuery.append("  , count(case  MCU_CODI.CODI_FW_VER when '" + version + "' then 1 end) || '/' || loc.id || '/"+version+"'  AS VERSION"+i+"   \n");
    				}
    			}
    			sbQuery.append("  FROM MCU mcu                  						                                                               \n")
    			.append("  LEFT OUTER JOIN MCU_CODI MCU_CODI                                                                                           \n")
    			.append("  ON MCU.MCU_CODI_ID = MCU_CODI.id                                                                                            \n")
    			.append("  INNER JOIN LOCATION loc	                                                                                                   \n")
    			.append("  ON mcu.location_id = loc.id                                                                                                 \n")
    			.append("  LEFT OUTER JOIN CODE co                                                                                                     \n")
    			.append("  ON mcu.MCU_STATUS = co.ID                                                                                                   \n");
    			if(!modelId.equals("") && modelId!=null)
    				sbQuery.append("  where (mcu.DEVICEMODEL_ID = :modelId) \n");
    			if(!deviceId.equals("") && deviceId!=null)
    				sbQuery.append("  and (mcu.SYS_ID IN ("+deviceIds+")) \n");
    			if(!fwVersion.equals("") && fwVersion!=null)
    				sbQuery.append("  and (mcu_codi.CODI_FW_VER IN ("+fwVersions+")) \n");
    			if(!locationId.equals("") && locationId!=null)
    				sbQuery.append("  and (loc.name IN ("+location+")) \n");
    			sbQuery.append("  and (loc.name IS NOT NULL) \n");
    			sbQuery.append("  and mcu.SYS_SW_VERSION is not null \n");
    			
    			if(!installStartDate.equals("") && installStartDate!=null)
    				sbQuery.append("  and mcu.INSTALL_DATE >= '"+installStartDate +"000000'\n");
    			if(!installEndtDate.equals("") && installEndtDate!=null)
    				sbQuery.append("  and mcu.INSTALL_DATE <= '"+installEndtDate +"125959'\n");
    			if(!lastCommStartDate.equals("") && lastCommStartDate!=null)
    				sbQuery.append("  and mcu.LAST_COMM_DATE >= '"+lastCommStartDate +"000000'\n");
    			if(!lastCommEndDate.equals("") && lastCommEndDate!=null)
    				sbQuery.append("  and mcu.LAST_COMM_DATE <= '"+lastCommEndDate +"125959'\n");
    			if(!hwVer.equals("") && hwVer!=null)
    				sbQuery.append("  and mcu.SYS_HW_VERSION = '"+hwVer+"' \n");
    			
    			sbQuery.append(" and (co.NAME <> 'Delete' or co.NAME IS NULL) \n");
    			sbQuery.append("  GROUP BY loc.name, loc.id                                                                                                    \n");

    			SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
    			if(!modelId.equals("") && modelId!=null)
    	        	query.setParameter("modelId", Integer.parseInt(modelId));
    			/*if(!deviceId.equals("") && deviceId!=null)
    				query.setParameter("deviceId", deviceId);*/
    			/*if(!fwVersion.equals("") && fwVersion!=null)
    				query.setParameter("fwVersion", fwVersion);*/
    			/*if(!locationId.equals("") && locationId!=null)
    				query.setParameter("location", location);*/
    			return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	}

	@Override
	public List<String> getCodiTargetList(Map<String, Object> condition) {

    	String modelId = StringUtil.nullToBlank(condition.get("modelId"));
        String versions = StringUtil.nullToBlank(condition.get("versions"));
        String deviceId = StringUtil.nullToBlank(condition.get("deviceId"));
        String fwVersion = StringUtil.nullToBlank(condition.get("fwVersion"));
        String locationId = StringUtil.nullToBlank(condition.get("locationId"));
        String installStartDate = StringUtil.nullToBlank(condition.get("installStartDate"));
        String installEndtDate = StringUtil.nullToBlank(condition.get("installEndtDate"));
        String lastCommStartDate = StringUtil.nullToBlank(condition.get("lastCommStartDate"));
        String lastCommEndDate = StringUtil.nullToBlank(condition.get("lastCommEndDate"));
        String hwVer = StringUtil.nullToBlank(condition.get("hwVer"));
        String lowVersion ="";
        
        StringTokenizer st = new StringTokenizer(locationId, ",");
        String location="";
        for(int i = 0 ; st.hasMoreTokens() ; i++){
        	location += ("'" + st.nextToken() +"',");
		}
        if(location.contains(",")){
        	location = location.substring(0, location.length()-1);
        }
        
        st = new StringTokenizer(fwVersion, ",");
        String fwVersions="";
        String temp ="";
        for(int i = 0 ; st.hasMoreTokens() ; i++){
        	//dcu-codi에서 dcu ver<1.2이라서 dcu에 codi와 연결되지 않은 dcu들(sp-1066)
        	temp= st.nextToken();
        	if(temp.equals("null")) {
        		lowVersion += " MCU.sys_sw_version < 1.2 \n";
        	}else {
        		fwVersions += ("'" + temp +"',");
        	}
		}
        if(fwVersions.contains(",")){
        	fwVersions = fwVersions.substring(0, fwVersions.length()-1);
        }
        
        st = new StringTokenizer(deviceId, ", ");
        String deviceIds="";
        for(int i = 0 ; st.hasMoreTokens() ; i++){
        	deviceIds += ("'" + st.nextToken() +"',");
		}
        if(deviceIds.contains(",")){
        	deviceIds = deviceIds.substring(0, deviceIds.length()-1);
        }
        
    	StringBuffer sbQuery = new StringBuffer()
    			.append("  SELECT mcu.SYS_ID||'/'||mcu.LOCATION_ID                                                                                      \n")
    			.append("  FROM MCU MCU          						        	                                                          		    \n")
    			.append("  LEFT OUTER JOIN MCU_CODI MCU_CODI                                                                                            \n")
    			.append("  ON MCU.MCU_CODI_ID = MCU_CODI.id  																							\n")
    			.append("  INNER JOIN LOCATION loc                                                                                                      \n")
    			.append("  ON mcu.location_id = loc.id                                                                                                  \n")
    			.append("  LEFT OUTER JOIN CODE co                                                                                                      \n")
    			.append("  ON mcu.MCU_STATUS = co.ID                                                                                                    \n");
    			if(!modelId.equals("") && modelId!=null)
    				sbQuery.append("  where mcu.DEVICEMODEL_ID = :modelId \n");
    			if(!deviceId.equals("") && deviceId!=null)
    				sbQuery.append("  and (mcu.SYS_ID IN ("+deviceIds+")) \n");
    			
    			if(!fwVersions.equals("") && fwVersions!=null) {								//codi version 선택하는경우
    				sbQuery.append(" and ( \n");
    				sbQuery.append(" MCU_CODI.CODI_FW_VER IN ("+fwVersions+") \n");
    				
    				if(!lowVersion.equals("")) 											//codi version 과 dcu ver < 1.2를 동시에 선택하는경우) 
    					sbQuery.append("  or "+lowVersion+"");
    				
    				sbQuery.append(" ) \n");
    			}else if(!lowVersion.equals("")) {											//dcu ver < 1.2 선택하는경우(=dcu codi ver =null) 
    				sbQuery.append("  and \n");
    				sbQuery.append(lowVersion);
    			}
    			
    			if(!locationId.equals("") && locationId!=null)
    				sbQuery.append("  and (loc.name IN ("+location+")) \n");
    			sbQuery.append("  and (loc.name IS NOT NULL) \n");
    			
    			if(!installStartDate.equals("") && installStartDate!=null)
    				sbQuery.append("  and mcu.INSTALL_DATE >= '"+installStartDate +"000000'\n");
    			if(!installEndtDate.equals("") && installEndtDate!=null)
    				sbQuery.append("  and mcu.INSTALL_DATE <= '"+installEndtDate +"125959'\n");
    			if(!lastCommStartDate.equals("") && lastCommStartDate!=null)
    				sbQuery.append("  and mcu.LAST_COMM_DATE >= '"+lastCommStartDate +"000000'\n");
    			if(!lastCommEndDate.equals("") && lastCommEndDate!=null)
    				sbQuery.append("  and mcu.LAST_COMM_DATE <= '"+lastCommEndDate +"125959'\n");
    			if(!hwVer.equals("") && hwVer!=null)
    				sbQuery.append("  and mcu.SYS_HW_VERSION = '"+hwVer+"' \n");
    			
    			sbQuery.append(" and (co.NAME <> 'Delete' or co.NAME IS NULL) \n");
    			sbQuery.append(" ORDER BY mcu.LOCATION_ID  asc \n"); // 추가
    			
    			SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
    			if(!modelId.equals("") && modelId!=null)
    	        	query.setParameter("modelId", Integer.parseInt(modelId));
    			/*if(!deviceId.equals("") && deviceId!=null)
    				query.setParameter("deviceId", deviceId);*/
    			/*if(!fwVersion.equals("") && fwVersion!=null)
    				query.setParameter("fwVersion", fwVersion);*/
    			/*if(!locationId.equals("") && locationId!=null)
    				query.setParameter("location", location);*/
    			return query.list();
	}

	@Override
	@SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRED)
    public Map<String, Object> getCodiGridData(Map<String, Object> conditionMap, boolean isCount) {
        Map<String, Object> result = new HashMap<String, Object>();
        Integer supplierId = (Integer) conditionMap.get("supplierId");
        String mcuId = StringUtil.nullToBlank(conditionMap.get("mcuId"));
        String mcuSerial = StringUtil.nullToBlank(conditionMap.get("mcuSerial")); 
        String mcuType = StringUtil.nullToBlank(conditionMap.get("mcuType"));
        String swVersion = StringUtil.nullToBlank(conditionMap.get("swVersion"));
        String swRevison = StringUtil.nullToBlank(conditionMap.get("swRevison"));
        String hwVersion = StringUtil.nullToBlank(conditionMap.get("hwVersion"));
        String installDateStart = StringUtil.nullToBlank(conditionMap.get("installDateStart"));
        String installDateEnd = StringUtil.nullToBlank(conditionMap.get("installDateEnd"));
        String lastcommStartDate = StringUtil.nullToBlank(conditionMap.get("lastcommStartDate"));
        String lastcommEndDate = StringUtil.nullToBlank(conditionMap.get("lastcommEndDate"));
        String filter = StringUtil.nullToBlank(conditionMap.get("filter"));
        String order = StringUtil.nullToBlank(conditionMap.get("order"));
        String protocol = StringUtil.nullToBlank(conditionMap.get("protocol"));
        String mcuStatus = StringUtil.nullToBlank(conditionMap.get("mcuStatus"));
        String modelId = StringUtil.nullToBlank(conditionMap.get("modelId"));
        String fwGadget = StringUtil.nullToBlank(conditionMap.get("fwGadget"));
        String purchaseOrder = StringUtil.nullToBlank(conditionMap.get("purchaseOrder"));
        Integer page = (Integer) conditionMap.get("page");
        Integer limit = (Integer) conditionMap.get("limit");
        List<Integer> locationIdList = (List<Integer>)conditionMap.get("locationIdList");
        Code deleteCode = conditionMap.get("deleteCode") == null ? null : (Code) conditionMap.get("deleteCode");
        Code normalCode = conditionMap.get("normalCodeId") == null ? null : (Code) conditionMap.get("normalCodeId");
        Code securityErrorCode = conditionMap.get("securityErrorCodeId") == null ? null : (Code) conditionMap.get("securityErrorCodeId");
        Code commErrorCode = conditionMap.get("commErrorCodeId") == null ? null : (Code) conditionMap.get("commErrorCodeId");
        Code powerDownCode = conditionMap.get("powerDownCodeId") == null ? null : (Code) conditionMap.get("powerDownCodeId");
        Criteria criteria = getSession().createCriteria(MCU.class);
        
        if (isCount) {
            criteria.setProjection(Projections.rowCount());
        }
        if (supplierId != null)
            criteria.add(Restrictions.eq("supplier.id", supplierId));
        
        if(fwGadget.equals("Y")){
        	StringTokenizer st = new StringTokenizer(mcuId, ", ");
            List<String> mcuIds = new ArrayList();
            for(int i = 0 ; st.hasMoreTokens() ; i++){
            	mcuIds.add(st.nextToken());
    		}
            if (mcuIds != null && mcuIds.size() > 0) {
                criteria.add(Restrictions.in("sysID", mcuIds));
            }
        }else{
        	if (!mcuId.isEmpty())
                criteria.add(Restrictions.ilike("sysID", mcuId, MatchMode.START));
        }
        
        if (!mcuSerial.isEmpty())
            criteria.add(Restrictions.ilike("sysSerialNumber", mcuSerial, MatchMode.START)); 
        
        if (!modelId.isEmpty())
        	criteria.add(Restrictions.eq("deviceModelId", Integer.parseInt(modelId)));
        
        if (!mcuType.isEmpty())
            criteria.add(Restrictions.eq("mcuType.id", Integer.parseInt(mcuType)));

        if (locationIdList != null && locationIdList.size() > 0) {
            criteria.add(Restrictions.in("location.id", locationIdList));
        }

        if (!swVersion.isEmpty()) {			//dcu-codi에서 dcu ver<1.2이라서 dcu에 codi와 연결되지 않은 dcu들(sp-1066)
        	if(swVersion.equals("null")) {
        		criteria.add(Restrictions.lt("sysSwVersion", "1.2"));
        	}else {
        		criteria.createAlias("mcuCodi", "codi").add(Restrictions.eq("codi.codiFwVer", swVersion));
        	}
        }
        
        if (!swRevison.isEmpty())
            criteria.add(Restrictions.eq("sysSwRevision", swRevison));

        if (!hwVersion.isEmpty())
            criteria.add(Restrictions.eq("sysHwVersion", hwVersion));
        
        if (!purchaseOrder.isEmpty())
            criteria.add(Restrictions.eq("po", purchaseOrder));
        
        if (!installDateStart.isEmpty())
            criteria.add(Restrictions.ge("installDate", installDateStart + "000000"));

        if (!installDateEnd.isEmpty())
            criteria.add(Restrictions.le("installDate", installDateEnd + "235959"));
        
        if (!lastcommStartDate.isEmpty())
            criteria.add(Restrictions.ge("lastCommDate", lastcommStartDate + "000000"));

        if (!lastcommEndDate.isEmpty())
            criteria.add(Restrictions.le("lastCommDate", lastcommEndDate + "235959"));

        if (!protocol.isEmpty())
            criteria.add(Restrictions.eq("protocolType.id", Integer.parseInt(protocol)));
        
		if(deleteCode != null && deleteCode.getId() != null) {
			if (!mcuStatus.isEmpty() && (Integer.parseInt(mcuStatus) == deleteCode.getId())) { // 상태가'delete'일 경우
	        	criteria.add(Restrictions.eq("mcuStatusCodeId", Integer.parseInt(mcuStatus)));
			} else if (!mcuStatus.isEmpty() && (Integer.parseInt(mcuStatus) == normalCode.getId())) {
				criteria.add(Restrictions.eq("mcuStatusCodeId", Integer.parseInt(mcuStatus)));
			} else if (!mcuStatus.isEmpty() && (Integer.parseInt(mcuStatus) == securityErrorCode.getId())) {
				criteria.add(Restrictions.eq("mcuStatusCodeId", Integer.parseInt(mcuStatus)));
			} else if (!mcuStatus.isEmpty() && (Integer.parseInt(mcuStatus) == commErrorCode.getId())) {
				criteria.add(Restrictions.eq("mcuStatusCodeId", Integer.parseInt(mcuStatus)));
			} else if (!mcuStatus.isEmpty() && (Integer.parseInt(mcuStatus) == powerDownCode.getId())) {
				criteria.add(Restrictions.eq("mcuStatusCodeId", Integer.parseInt(mcuStatus)));
	        } else {
	        	Criterion deleteMcu = Restrictions.ne("mcuStatus", deleteCode);
	        	Criterion nullMcu = Restrictions.isNull(("mcuStatus"));
	        	
	        	LogicalExpression expression = Restrictions.or(deleteMcu, nullMcu);
	        	criteria.add(expression);
	        }
		} else {
			logger.info("deleteCodeId is not exist");
		}


        if (!filter.isEmpty()) {

            Map<String, String> TFBeforeHour = DateTimeUtil.calcDate(Calendar.HOUR, -24);
            Map<String, String> FEBeforeHour = DateTimeUtil.calcDate(Calendar.HOUR, -48);

            String TFDate = TFBeforeHour.get("date").replace("-", "") + TFBeforeHour.get("time").replace(".", "");
            String FEDate = FEBeforeHour.get("date").replace("-", "") + FEBeforeHour.get("time").replace(".", "");

            if ("normal".equals(filter)) {
                criteria.add(Restrictions.ge("lastCommDate", TFDate));
            } else if ("commStateYellow".equals(filter)) {
                criteria.add(Restrictions.le("lastCommDate", TFDate));
                criteria.add(Restrictions.ge("lastCommDate", FEDate));
            } else if ("commStateRed".equals(filter)) {
                criteria.add(Restrictions.le("lastCommDate", FEDate));
            }
        }

        if (!isCount && !order.isEmpty()) {
            if ("lastCommDesc".equals(order)) {
                criteria.addOrder(Order.desc("lastCommDate"));
            } else if ("lastCommAsc".equals(order)) {
                criteria.addOrder(Order.asc("lastCommDate"));
            } else if ("installDateDesc".equals(order)) {
                criteria.addOrder(Order.desc("installDate"));
            } else if ("installDateAsc".equals(order)) {
                criteria.addOrder(Order.asc("installDate"));
            }
            criteria.addOrder(Order.asc("sysID"));
        }

        if (!isCount && page != null && limit != null && (page != 0 || limit != 0)) {
            criteria.setFirstResult((page - 1) * limit);
            // 페이지별로 데이터를 내보내게 하는 부분
            criteria.setMaxResults(limit);
        }

        if (isCount) {
            result.put("count", ((Number) criteria.uniqueResult()).intValue());
        } else {
            result.put("list", criteria.list());
        }
        
        return result;
    }
	
	//sp-1066 valid dcu-codi
	@Override
	public List<Map<String, Object>> getValidCodiList(Map<String, Object> condition) {
		String modelId = StringUtil.nullToBlank(condition.get("modelId"));
        String deviceList = StringUtil.nullToBlank(condition.get("deviceList"));
        String supplierId = StringUtil.nullToBlank(condition.get("supplierId"));
        
        StringTokenizer st = new StringTokenizer(deviceList, ", ");
        String deviceIds="";
        for(int i = 0 ; st.hasMoreTokens() ; i++){
        	deviceIds += ("(0,'" + st.nextToken() +"'),");
		}
        if(deviceIds.contains(",")){
        	deviceIds = deviceIds.substring(0, deviceIds.length()-1);
        }
        
    	StringBuffer sbQuery = new StringBuffer()
    			.append("  SELECT  mcu.SYS_ID DEVICE_ID, MCU_CODI.CODI_FW_VER VERSION   	\n")
    			.append("  FROM MCU mcu      						  	\n")
    			.append("  LEFT OUTER JOIN MCU_CODI MCU_CODI           	\n")
    			.append("  ON MCU.MCU_CODI_ID = MCU_CODI.id             \n")
    			.append("  INNER JOIN LOCATION loc               		\n")
    			.append("  ON mcu.location_id = loc.id                  \n")
    			.append("  LEFT OUTER JOIN CODE co                  	\n")
    			.append("  ON mcu.MCU_STATUS = co.ID                    \n");
    			
		sbQuery.append("  where (mcu.DEVICEMODEL_ID = :modelId) \n");
		sbQuery.append("  and mcu.SYS_SW_VERSION is not null 	\n");
		sbQuery.append("  and mcu.location_id is not null 		\n");
		sbQuery.append("  and (0, mcu.SYS_ID) IN (" + deviceIds + ") \n");
		sbQuery.append("  and mcu.supplier_id = :supplierId 	\n");
		sbQuery.append(" and (co.NAME <> 'Delete' or co.NAME IS NULL) \n");
		sbQuery.append(" ORDER BY MCU_CODI.CODI_FW_VER asc \n");

		SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
		query.setParameter("modelId", Integer.parseInt(modelId));
		query.setParameter("supplierId", Integer.parseInt(supplierId));

		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	}

}