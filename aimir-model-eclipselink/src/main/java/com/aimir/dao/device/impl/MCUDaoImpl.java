package com.aimir.dao.device.impl;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants.McuStatus;
import com.aimir.constants.CommonConstants.UsingMCUType;
import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.model.device.CommStateByLocationVO;
import com.aimir.model.device.LocationByCommStateVO;
import com.aimir.model.device.MCU;
import com.aimir.model.device.MCUTypeByCommStateVO;
import com.aimir.model.device.MCUTypeByLocationVO;
import com.aimir.model.system.Code;
import com.aimir.util.Condition;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.DecimalUtil;
import com.aimir.util.StringUtil;

import net.sf.json.JSONArray;

@Repository(value = "mcuDao")
public class MCUDaoImpl extends AbstractJpaDao<MCU, Integer> implements MCUDao {

    Log logger = LogFactory.getLog(MCUDaoImpl.class);
    
    @Autowired
    SupplierDao supplierDao;
    
    public MCUDaoImpl() {
		super(MCU.class);
	}
	
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	public MCU get(String sysID) {
	    return findByCondition("sysID", sysID);
	}

	@SuppressWarnings("rawtypes")
	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<String> getHwVersions() {

		List<String> hwVersions = new ArrayList<String>();
		String hwVersion = null;
		
		Query query = em.createQuery("select hwVersion from MCU group by hwVersion", String.class);
		List results = query.getResultList();
		
		for(int i = 0, j = results.size() ; i < j ; i++) {
			
			hwVersion = (String)results.get(i);
			hwVersions.add(hwVersion);	
		}
					
		return hwVersions;
	}

	@SuppressWarnings("rawtypes")
	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<String> getSwVersions() {
		
		List<String> swVersions = new ArrayList<String>();
		String swVersion = null;
		
		Query query = em.createQuery("select swVersion from MCU group by swVersion", String.class);
		List results = query.getResultList();
		
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
        String mcuType = StringUtil.nullToBlank(conditionMap.get("mcuType"));
        String swVersion = StringUtil.nullToBlank(conditionMap.get("swVersion"));
        String hwVersion = StringUtil.nullToBlank(conditionMap.get("hwVersion"));
        String installDateStart = StringUtil.nullToBlank(conditionMap.get("installDateStart"));
        String installDateEnd = StringUtil.nullToBlank(conditionMap.get("installDateEnd"));
        String filter = StringUtil.nullToBlank(conditionMap.get("filter"));
        String order = StringUtil.nullToBlank(conditionMap.get("order"));
        String protocol = StringUtil.nullToBlank(conditionMap.get("protocol"));
        String mcuStatus = StringUtil.nullToBlank(conditionMap.get("mcuStatus"));
        Integer page = (Integer) conditionMap.get("page");
        Integer limit = (Integer) conditionMap.get("limit");
        List<Integer> locationIdList = (List<Integer>)conditionMap.get("locationIdList");
        Code deleteCode = conditionMap.get("deleteCode") == null ? null : (Code) conditionMap.get("deleteCode");

        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> count = cb.createQuery(Long.class);
        CriteriaQuery<MCU> criteria = cb.createQuery(MCU.class);
        Root<MCU> from = criteria.from(MCU.class);

        if (supplierId != null) {
            count.where(cb.equal(from.get("supplier.id"), supplierId));
            criteria.where(cb.equal(from.get("supplier.id"), supplierId));
        }
        if (!mcuId.isEmpty()) {
            count.where(cb.equal(from.get("sysID"), mcuId));
            criteria.where(cb.equal(from.get("sysID"), mcuId));
        }

        if (!mcuType.isEmpty()) {
            count.where(cb.equal(from.get("mcuType.id"), Integer.parseInt(mcuType)));
            criteria.where(cb.equal(from.get("mcuType.id"), Integer.parseInt(mcuType)));
        }

        if (locationIdList != null && locationIdList.size() > 0) {
            count.where(from.get("location.id").in(locationIdList));
            criteria.where(from.get("location.id").in(locationIdList));
        }

        if (!swVersion.isEmpty()) {
            count.where(cb.equal(from.get("sysSwVersion"), swVersion));
            criteria.where(cb.equal(from.get("sysSwVersion"), swVersion));
        }
        
        if (!hwVersion.isEmpty()) {
            count.where(cb.equal(from.get("sysHwVersion"), hwVersion));
            criteria.where(cb.equal(from.get("sysHwVersion"), hwVersion));
        }

        if (!installDateStart.isEmpty()) {
            count.where(cb.greaterThanOrEqualTo(from.<String>get("installDate"), installDateStart + "000000"));
            criteria.where(cb.greaterThanOrEqualTo(from.<String>get("installDate"), installDateStart + "000000"));
        }

        if (!installDateEnd.isEmpty()) {
            count.where(cb.lessThanOrEqualTo(from.<String>get("installDate"), installDateEnd + "235959"));
            criteria.where(cb.lessThanOrEqualTo(from.<String>get("installDate"), installDateEnd + "235959"));
        }

        if (!protocol.isEmpty()) {
            count.where(cb.equal(from.get("protocolType.id"), Integer.parseInt(protocol)));
            criteria.where(cb.equal(from.get("protocolType.id"), Integer.parseInt(protocol)));
        }
        
        if(deleteCode != null && deleteCode.getId() != null) {
            if (!mcuStatus.isEmpty() && Integer.parseInt(mcuStatus) == deleteCode.getId()) {
                count.where(cb.equal(from.get("mcuStatusCodeId"), Integer.parseInt(mcuStatus)));
                criteria.where(cb.equal(from.get("mcuStatusCodeId"), Integer.parseInt(mcuStatus)));
            } else {
                Expression deleteMcu = cb.notEqual(from.get("mcuStatus"), deleteCode);
                Expression nullMcu = cb.isNull(from.get("mcuStatus"));
                
                Predicate expression = cb.or(deleteMcu, nullMcu);
                count.where(expression);
                criteria.where(expression);
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
                count.where(cb.greaterThanOrEqualTo(from.<String>get("lastCommDate"), TFDate));
                criteria.where(cb.greaterThanOrEqualTo(from.<String>get("lastCommDate"), TFDate));
            } else if ("commStateYellow".equals(filter)) {
                count.where(cb.lessThanOrEqualTo(from.<String>get("lastCommDate"), TFDate));
                criteria.where(cb.lessThanOrEqualTo(from.<String>get("lastCommDate"), TFDate));
                count.where(cb.greaterThanOrEqualTo(from.<String>get("lastCommDate"), FEDate));
                criteria.where(cb.greaterThanOrEqualTo(from.<String>get("lastCommDate"), FEDate));
            } else if ("commStateRed".equals(filter)) {
                criteria.where(cb.lessThanOrEqualTo(from.<String>get("lastCommDate"), FEDate));
                criteria.where(cb.lessThanOrEqualTo(from.<String>get("lastCommDate"), FEDate));
            }
        }

        if (!isCount && !order.isEmpty()) {
            if ("lastCommDesc".equals(order)) {
                criteria.orderBy(cb.desc(from.get("lastCommDate")));
            } else if ("lastCommAsc".equals(order)) {
                criteria.orderBy(cb.asc(from.get("lastCommDate")));
            } else if ("installDateDesc".equals(order)) {
                criteria.orderBy(cb.desc(from.get("installDate")));
            } else if ("installDateAsc".equals(order)) {
                criteria.orderBy(cb.asc(from.get("installDate")));
            }
        }

        if (isCount) {
            count.multiselect(cb.count(from.get("id")));
            TypedQuery<Long> typedQuery = getEntityManager().createQuery(count);
            result.put("count", typedQuery.getSingleResult());
        } else {
            TypedQuery<MCU> typedQuery = getEntityManager().createQuery(criteria);
            if (page != null && limit != null && (page != 0 || limit != 0)) {
            	typedQuery.setFirstResult((page - 1) * limit);
            	typedQuery.setMaxResults(limit);
            }
            result.put("list", typedQuery.getResultList());
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

        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<MCU> criteria = cb.createQuery(MCU.class);
        Root<MCU> from = criteria.from(MCU.class);
        
        if(supplierId != null && !"".equals(supplierId))
            criteria.where(cb.equal(from.get("supplier.id"), Integer.parseInt(supplierId)));
        if(mcuId != null && !"".equals(mcuId))
            criteria.where(cb.equal(from.get("sysID"), mcuId));

        if(mcuType != null && !"".equals(mcuType))
            criteria.where(cb.equal(from.get("mcuType.id"), Integer.parseInt(mcuType)));

        if (StringUtil.nullToBlank(conditionMap.get("locationId")).length() > 0) {
            String[] locationIdStr = conditionMap.get("locationId").split(",");
            Integer[] locationId = new Integer[locationIdStr.length];

            for (int i = 0; i < locationIdStr.length; i++) {
                locationId[i] = Integer.parseInt(locationIdStr[i]);
            }

            criteria.where(from.get("location.id").in(locationId));
        }

        if(swVersion != null && !"".equals(swVersion))
            criteria.where(cb.equal(from.get("sysSwVersion"), swVersion));

        if(hwVersion != null && !"".equals(hwVersion))
            criteria.where(cb.equal(from.get("sysHwVersion"), hwVersion));

        if(installDateStart != null && !"".equals(installDateStart))
            criteria.where(cb.greaterThanOrEqualTo(from.<String>get("installDate"), installDateStart+ "000000"));

        if(installDateEnd != null && !"".equals(installDateEnd))
            criteria.where(cb.lessThanOrEqualTo(from.<String>get("installDate"), installDateEnd + "235959"));

        if(protocol != null && !"".equals(protocol))
            criteria.where(cb.equal(from.get("protocolType.id"), Integer.parseInt(protocol)));

        if(filter != null && !"".equals(filter)) {

            Map<String, String> TFBeforeHour = DateTimeUtil.calcDate(Calendar.HOUR, -24);
            Map<String, String> FEBeforeHour = DateTimeUtil.calcDate(Calendar.HOUR, -48);

            String TFDate = TFBeforeHour.get("date").replace("-", "") + TFBeforeHour.get("time").replace(".", "");
            String FEDate = FEBeforeHour.get("date").replace("-", "") + FEBeforeHour.get("time").replace(".", "");

            if("normal".equals(filter)) {
                criteria.where(cb.greaterThanOrEqualTo(from.<String>get("lastCommDate"), TFDate));
            } else if("commStateYellow".equals(filter)) {
                criteria.where(cb.lessThanOrEqualTo(from.<String>get("lastCommDate"), TFDate));
                criteria.where(cb.greaterThanOrEqualTo(from.<String>get("lastCommDate"), FEDate));
            } else if("commStateRed".equals(filter)) {
                criteria.where(cb.lessThanOrEqualTo(from.<String>get("lastCommDate"), FEDate));
            }
        }

        if(order != null && !"".equals(order)) {
            if("lastCommDesc".equals(order)) {
                criteria.orderBy(cb.desc(from.get("lastCommDate")));
            } else if("lastCommAsc".equals(order)) {
                criteria.orderBy(cb.asc(from.get("lastCommDate")));
            } else if("installDateDesc".equals(order)) {
                criteria.orderBy(cb.desc(from.get("installDate")));
            } else if("installDateAsc".equals(order)) {
                criteria.orderBy(cb.asc(from.get("installDate")));
            }
        }


        TypedQuery typedQuery = getEntityManager().createQuery(criteria);
        
        int firstResult = page * rowPerPage;
        typedQuery.setFirstResult(firstResult);

        //엑셀 출력시 모든 데이터를 출력하기 위한 조건문
        if(rowPerPage != 0) {
            //페이지별로 데이터를 내보내게 하는 부분
            typedQuery.setMaxResults(rowPerPage);
        }

        List<MCU> mcus = typedQuery.getResultList();

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
//      String page = conditionArray[7];
        String filter = conditionArray[8];
//      String order = conditionArray[9];
        String protocol = conditionArray[10];
        String supplierId = conditionArray[12];

        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> criteria = cb.createQuery(Long.class);
        Root<MCU> from = criteria.from(MCU.class);
        
        if(supplierId != null && !"".equals(supplierId)) {
            criteria.where(cb.equal(from.get("supplier.id"), Integer.parseInt(supplierId)));
        }
        if(mcuId != null && !"".equals(mcuId)) {
            criteria.where(cb.equal(from.get("sysID"), mcuId));
        }

        if(mcuType != null && !"".equals(mcuType)) {
            criteria.where(cb.equal(from.get("mcuType.id"), Integer.parseInt(mcuType)));
        }

        if (StringUtil.nullToBlank(conditionArray[2]).length() > 0) {
            String[] locationIdStr = conditionArray[2].split(",");
            Integer[] locationId = new Integer[locationIdStr.length];

            for (int i = 0; i < locationIdStr.length; i++) {
                locationId[i] = Integer.parseInt(locationIdStr[i]);
            }

            criteria.where(from.get("location.id").in(locationId));
        }

        if(swVersion != null && !"".equals(swVersion)) {
            criteria.where(cb.equal(from.get("sysSwVersion"), swVersion));
        }
        
        if(hwVersion != null && !"".equals(hwVersion)) {
            criteria.where(cb.equal(from.get("sysHwVersion"), hwVersion));
        }

        if(installDateStart != null && !"".equals(installDateStart)) {
            criteria.where(cb.greaterThanOrEqualTo(from.<String>get("installDate"), installDateStart));
        }

        if(installDateEnd != null && !"".equals(installDateEnd)) {
            criteria.where(cb.lessThanOrEqualTo(from.<String>get("installDate"), installDateEnd));
        }

        if(protocol != null && !"".equals(protocol)) {
            criteria.where(cb.equal(from.get("protocolType.id"), Integer.parseInt(protocol)));
        }

        if(filter != null && !"".equals(filter)) {

            Map<String, String> TFBeforeHour = DateTimeUtil.calcDate(Calendar.HOUR, -24);
            Map<String, String> FEBeforeHour = DateTimeUtil.calcDate(Calendar.HOUR, -48);

            String TFDate = TFBeforeHour.get("date").replace("-", "") + TFBeforeHour.get("time").replace(".", "");
            String FEDate = FEBeforeHour.get("date").replace("-", "") + FEBeforeHour.get("time").replace(".", "");

            if("normal".equals(filter)) {
                criteria.where(cb.greaterThanOrEqualTo(from.<String>get("lastCommDate"), TFDate));
            } else if("commStateYellow".equals(filter)) {
                criteria.where(cb.lessThanOrEqualTo(from.<String>get("lastCommDate"), TFDate));
                criteria.where(cb.greaterThanOrEqualTo(from.<String>get("lastCommDate"), FEDate));
            } else if("commStateRed".equals(filter)) {
                criteria.where(cb.lessThanOrEqualTo(from.<String>get("lastCommDate"), FEDate));
            }
        }
        
        criteria.multiselect(cb.count(from.get("id")));
        TypedQuery<Long> countQuery = getEntityManager().createQuery(criteria);
        
        Long count = countQuery.getSingleResult();
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

        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<MCU> criteria = cb.createQuery(MCU.class);
        Root<MCU> from = criteria.from(MCU.class);

        if(mcuId != null && !"".equals(mcuId))
            criteria.where(cb.equal(from.get("sysID"), mcuId));

        if(mcuType != null && !"".equals(mcuType))
            criteria.where(cb.equal(from.get("mcuType.id"), Integer.parseInt(mcuType)));

        if(locId != null && !"".equals(locId))
            criteria.where(cb.equal(from.get("location.id"), Integer.parseInt(locId)));

        if(swVersion != null && !"".equals(swVersion))
            criteria.where(cb.equal(from.get("sysSwVersion"), swVersion));

        if(hwVersion != null && !"".equals(hwVersion))
            criteria.where(cb.equal(from.get("sysHwVersion"), hwVersion));

        if(installDateStart != null && !"".equals(installDateStart))
            criteria.where(cb.greaterThanOrEqualTo(from.<String>get("installDate"), installDateStart));

        if(installDateEnd != null && !"".equals(installDateEnd))
            criteria.where(cb.lessThanOrEqualTo(from.<String>get("installDate"), installDateEnd));

        if(filter != null && !"".equals(filter)) {

            Map<String, String> TFBeforeHour = DateTimeUtil.calcDate(Calendar.HOUR, -24);
            Map<String, String> FEBeforeHour = DateTimeUtil.calcDate(Calendar.HOUR, -48);

            String TFDate = TFBeforeHour.get("date").replace("-", "") + TFBeforeHour.get("time").replace(".", "");
            String FEDate = FEBeforeHour.get("date").replace("-", "") + FEBeforeHour.get("time").replace(".", "");

            if("normal".equals(filter)) {
//              sbQuery.append(" and mcu.lastCommDate >= :TFDate1 ");
                criteria.where(cb.greaterThanOrEqualTo(from.<String>get("lastCommDate"), TFDate));
            } else if("commStateYellow".equals(filter)) {
//              sbQuery.append(" and mcu.lastCommDate < :TFDate2 AND mcu.lastCommDate >= :FEDate2 ");
                criteria.where(cb.lessThanOrEqualTo(from.<String>get("lastCommDate"), TFDate));
                criteria.where(cb.greaterThanOrEqualTo(from.<String>get("lastCommDate"), FEDate));
            } else if("commStateRed".equals(filter)) {
//              sbQuery.append(" and mcu.lastCommDate < :FEDate3 ");
                criteria.where(cb.lessThanOrEqualTo(from.<String>get("lastCommDate"), FEDate));
            }
        }

        if(order != null && !"".equals(order)) {
            if("lastCommDesc".equals(order)) {
                criteria.orderBy(cb.desc(from.get("lastCommDate")));
            } else if("lastCommAsc".equals(order)) {
                criteria.orderBy(cb.asc(from.get("lastCommDate")));
            } else if("installDateDesc".equals(order)) {
                criteria.orderBy(cb.desc(from.get("installDate")));
            } else if("installDateAsc".equals(order)) {
                criteria.orderBy(cb.asc(from.get("installDate")));
//          } else if("csqDesc".equals(order)) {
//          } else if("csqAsc".equals(order)) {
            }
        }

        int page = Integer.parseInt(array[7]);
        int firstResult = (page - 1) * rowPerPage;

        TypedQuery<MCU> typedQuery = getEntityManager().createQuery(criteria);
        typedQuery.setFirstResult(firstResult);
        typedQuery.setMaxResults(rowPerPage);

        List<MCU> mcus = typedQuery.getResultList();

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

        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> criteria = cb.createQuery(Long.class);
        Root<MCU> from = criteria.from(MCU.class);
        
        if(supplierId != null && !"".equals(supplierId))
            criteria.where(cb.equal(from.get("supplier.id"), Integer.parseInt(supplierId)));

        if(mcuId != null && !"".equals(mcuId))
            criteria.where(cb.equal(from.get("sysID"), mcuId));

        if(mcuType != null && !"".equals(mcuType))
            criteria.where(cb.equal(from.get("mcuType.id"), Integer.parseInt(mcuType)));

        if(locId != null && !"".equals(locId))
            criteria.where(cb.equal(from.get("location.id"), Integer.parseInt(locId)));

        if(swVersion != null && !"".equals(swVersion))
            criteria.where(cb.equal(from.get("sysSwVersion"), swVersion));

        if(hwVersion != null && !"".equals(hwVersion))
            criteria.where(cb.equal(from.get("sysHwVersion"), hwVersion));

        if(installDateStart != null && !"".equals(installDateStart))
            criteria.where(cb.greaterThanOrEqualTo(from.<String>get("installDate"), installDateStart));

        if(installDateEnd != null && !"".equals(installDateEnd))
            criteria.where(cb.lessThanOrEqualTo(from.<String>get("installDate"), installDateEnd));

        if(protocol != null && !"".equals(protocol))
            criteria.where(cb.equal(from.get("protocolType.id"), Integer.parseInt(protocol)));

        if(filter != null && !"".equals(filter)) {

            Map<String, String> TFBeforeHour = DateTimeUtil.calcDate(Calendar.HOUR, -24);
            Map<String, String> FEBeforeHour = DateTimeUtil.calcDate(Calendar.HOUR, -48);

            String TFDate = TFBeforeHour.get("date").replace("-", "") + TFBeforeHour.get("time").replace(".", "");
            String FEDate = FEBeforeHour.get("date").replace("-", "") + FEBeforeHour.get("time").replace(".", "");

            if("normal".equals(filter)) {
                criteria.where(cb.greaterThanOrEqualTo(from.<String>get("lastCommDate"), TFDate));
            } else if("commStateYellow".equals(filter)) {
                criteria.where(cb.lessThanOrEqualTo(from.<String>get("lastCommDate"), TFDate));
                criteria.where(cb.greaterThanOrEqualTo(from.<String>get("lastCommDate"), FEDate));
            } else if("commStateRed".equals(filter)) {
                criteria.where(cb.lessThanOrEqualTo(from.<String>get("lastCommDate"), FEDate));
            }
        }

        criteria.multiselect(cb.count(from.get("id")));
        TypedQuery<Long> typedQuery = getEntityManager().createQuery(criteria);
        return typedQuery.getSingleResult().intValue();
    }

//  @SuppressWarnings("unchecked")
//  public List<MCU> getMcusByCondition(Map<String, String> conditionMap) {
//
//      String mcuType = conditionMap.get("MCUType");
//      String locId = conditionMap.get("locationId");
//      String commState = conditionMap.get("commState");
//
//      Criteria criteria = getSession().createCriteria(MCU.class);
//
//      if(mcuType != null && !"".equals(mcuType))
//          criteria.add(Restrictions.eq("mcuType.code", mcuType));
//
//      if(locId != null && !"".equals(locId))
//          criteria.add(Restrictions.eq("location.id", Integer.parseInt(locId)));
//
//      if(commState != null && !"".equals(commState)) {
//
//          Map<String, String> TFBeforeHour = DateTimeUtil.calcDate(Calendar.HOUR, -24);
//          Map<String, String> FEBeforeHour = DateTimeUtil.calcDate(Calendar.HOUR, -48);
//
//          String TFDate = TFBeforeHour.get("date").replace("-", "") + TFBeforeHour.get("time").replace(".", "");
//          String FEDate = FEBeforeHour.get("date").replace("-", "") + FEBeforeHour.get("time").replace(".", "");
//
//          if("0".equals(commState)) {
//              criteria.add(Restrictions.ge("lastCommDate", TFDate));
//          } else if("24".equals(commState)) {
//              criteria.add(Restrictions.between("lastCommDate", FEDate, TFDate));
//          } else if("48".equals(commState)) {
//              criteria.add(Restrictions.le("lastCommDate", FEDate));
//          }
//      }
//
//      return criteria.list();
//  }

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

        Query query = getEntityManager().createQuery(sbQuery.toString());

        if(supplierId != null && !"".equals(supplierId))
            query.setParameter("supplierId", Integer.parseInt(supplierId));

        if(mcuType != null && !"".equals(mcuType))
            query.setParameter("mcuType", mcuType);

        if(locationId != null && !"".equals(locationId))
            query.setParameter("locationId", locationId);

        if(commState != null && !"".equals(commState)) {

            Map<String, String> TFBeforeHour = DateTimeUtil.calcDate(Calendar.HOUR, -24);
            Map<String, String> FEBeforeHour = DateTimeUtil.calcDate(Calendar.HOUR, -48);

            String TFDate = TFBeforeHour.get("date").replace("-", "") + TFBeforeHour.get("time").replace(".", "");
            String FEDate = FEBeforeHour.get("date").replace("-", "") + FEBeforeHour.get("time").replace(".", "");

            if("0".equals(commState)) {
                query.setParameter("TFDate1", TFDate);
            } else if("24".equals(commState)) {
                query.setParameter("TFDate2", TFDate);
                query.setParameter("FEDate2", FEDate);
            } else if("48".equals(commState)) {
                query.setParameter("FEDate3", FEDate);
            }
        }

        return query.getResultList();
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

        Query query = getEntityManager().createQuery(sbQuery.toString());
        return query.getResultList();
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

        Query query = getEntityManager().createNativeQuery(sbQuery.toString());
        result = query.getResultList();

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

        Query query = getEntityManager().createNativeQuery(sbQuery.toString());
        query.setParameter("supplierId", Integer.parseInt(supplierId));
        result = query.getResultList();

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

        Query query = getEntityManager().createNativeQuery(sbQuery.toString());
        query.setParameter("supplierId", Integer.parseInt(supplierId));
        return query.getResultList();
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
        .append("       SELECT CODE, NAME, COMMSTATE, COUNT(*) CNT                                                  ")
        .append("           FROM (SELECT code.CODE, code.NAME,                                                      ")
        .append("                        CASE WHEN (LAST_COMM_DATE  >= :TFDate) THEN 0                              ")
        .append("                             WHEN (LAST_COMM_DATE < :TFDate AND LAST_COMM_DATE >= :FEDate) THEN 1  ")
        .append("                             WHEN (LAST_COMM_DATE < :FEDate) THEN 2                                ")
        .append("                        END COMMSTATE                                                              ")
        .append("                   FROM MCU mcu, Code code                                                         ")
        .append("                  WHERE mcu.MCU_TYPE = code.id                                                     ")
        .append("                  AND mcu.SUPPLIER_ID = :supplierId                                                ")
        .append("                   ) a                                                                             ")
        .append("           GROUP BY a.CODE, a.NAME, a.COMMSTATE) T1                                                ")
        .append("  GROUP BY CODE, NAME                                                                                      ");

        Map<String, String> TFBeforeHour = DateTimeUtil.calcDate(Calendar.HOUR, -24);
        Map<String, String> FEBeforeHour = DateTimeUtil.calcDate(Calendar.HOUR, -48);

        String TFDate = TFBeforeHour.get("date").replace("-", "") + TFBeforeHour.get("time").replace(".", "");
        String FEDate = FEBeforeHour.get("date").replace("-", "") + FEBeforeHour.get("time").replace(".", "");

        Query query = getEntityManager().createNativeQuery(sbQuery.toString());
        query.setParameter("TFDate", TFDate);
        query.setParameter("FEDate", FEDate);
        query.setParameter("supplierId", Integer.parseInt(supplierId));

        List<Object[]> result = query.getResultList();

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
            if(j != usingMCUTypes.length - 1) sbQuery.append(", "); j++;
        }
        sbQuery.append("     FROM (SELECT loc.name, code.code, mcu.location_id, count(*) cnt                        \n")
        .append("             FROM MCU mcu, LOCATION loc, Code code                                          \n")
        .append("            WHERE mcu.location_id = loc.id                                                  \n")
        .append("              AND mcu.mcu_type = code.id                                                    \n")
        .append("              AND mcu.supplier_id = :supplierId                                             \n")
        .append("            GROUP BY loc.name, code.code, mcu.location_id) t1                               \n")
        .append("    GROUP BY t1.name, t1.location_id                                                        \n");

        int displaySize = 6; // 화면에 표시해줄 지역 수

        Query query = getEntityManager().createNativeQuery(sbQuery.toString());
        query.setParameter("supplierId", Integer.parseInt(supplierId));
        query.setMaxResults(displaySize);

        // TODO
        // return JSONArray.fromObject(query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list()).toString();
        return null;
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
            if(j != usingMCUTypes.length - 1) sbQuery.append(", "); j++;
        }
        sbQuery.append("     FROM (SELECT loc.name, code.code, mcu.location_id, count(*) cnt                        \n")
        .append("             FROM MCU mcu, LOCATION loc, Code code                                          \n")
        .append("            WHERE mcu.location_id = loc.id                                                  \n")
        .append("              AND mcu.mcu_type = code.id                                                    \n")
        .append("              AND mcu.supplier_id = :supplierId                                             \n")
        .append("            GROUP BY loc.name, code.code, mcu.location_id) t1                               \n")
        .append("    GROUP BY t1.name, t1.location_id                                                        \n");

        int displaySize = 6; // 화면에 표시해줄 지역 수

        Query query = getEntityManager().createNativeQuery(sbQuery.toString());
        query.setParameter("supplierId", Integer.parseInt(supplierId));
        query.setMaxResults(displaySize);

        // TODO
        // return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        return null;
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
        .append("           FROM (SELECT LOCATION_ID,                                                              \n")
        .append("                      CASE WHEN (LAST_COMM_DATE  >= :TFDate) THEN 0                               \n")
        .append("                          WHEN (LAST_COMM_DATE < :TFDate AND LAST_COMM_DATE >= :FEDate) THEN 1    \n")
        .append("                          WHEN (LAST_COMM_DATE < :FEDate) THEN 2                                  \n")
        .append("                      END COMMSTATE   FROM MCU                                                    \n")
        .append("                      WHERE SUPPLIER_ID = :supplierId                                             \n")
        .append("                     ) t1                                                                         \n")
        .append("          GROUP BY t1.LOCATION_ID, t1.commstate) t2,                                              \n")
        .append("         LOCATION t3                                                                              \n")
        .append("   WHERE t2.location_id = t3.id                                                                   \n")
        .append("   GROUP BY t3.NAME, t3.id                                                                        \n");

        int displaySize = 6; // 화면에 표시해줄 지역 수

        Map<String, String> TFBeforeHour = DateTimeUtil.calcDate(Calendar.HOUR, -24);
        Map<String, String> FEBeforeHour = DateTimeUtil.calcDate(Calendar.HOUR, -48);

        String TFDate = TFBeforeHour.get("date").replace("-", "") + TFBeforeHour.get("time").replace(".", "");
        String FEDate = FEBeforeHour.get("date").replace("-", "") + FEBeforeHour.get("time").replace(".", "");

        Query query = getEntityManager().createNativeQuery(sbQuery.toString());
        query.setParameter("TFDate", TFDate);
        query.setParameter("FEDate", FEDate);
        query.setParameter("supplierId", Integer.parseInt(supplierId));
        query.setMaxResults(displaySize);

        List<Object[]> result = query.getResultList();

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

//  public List<CommStateByMCUTypeVO> getCommStateByMCUTypeData() {
    @Override
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public String getCommStateByMCUTypeData(String supplierId) {

        StringBuffer sbQuery = new StringBuffer()
        .append(" SELECT t2.commstate as commstate,                                                                  \n");
//      .append("        MAX(CASE WHEN t2.CODE = '1.1.1.3' THEN t2.cnt ELSE 0 END) DCU,                          \n")
//      .append("        MAX(CASE WHEN t2.CODE = '1.1.1.4' THEN t2.cnt ELSE 0 END) Indoor,                           \n")
//      .append("        MAX(CASE WHEN t2.CODE = '1.1.1.5' THEN t2.cnt ELSE 0 END) Outdoor                           \n")
        int j = 0;
        UsingMCUType[] usingMCUTypes = UsingMCUType.values();
        for(UsingMCUType mcuType : usingMCUTypes) {
            sbQuery.append(" MAX(CASE WHEN t2.CODE = '" + mcuType.getCode() +"' THEN t2.cnt ELSE 0 END) as " + mcuType.name() + " ");
            if(j != usingMCUTypes.length - 1) sbQuery.append(", "); j++;
        }
        sbQuery.append("   FROM (SELECT t1.commstate, t1.CODE, count(*) cnt                                         \n")
        .append("           FROM (SELECT CODE,                                                                       \n")
        .append("                         CASE WHEN (LAST_COMM_DATE >= :TFDate) THEN 0                               \n")
        .append("                              WHEN (LAST_COMM_DATE < :TFDate AND LAST_COMM_DATE >= :FEDate) THEN 1  \n")
        .append("                              WHEN (LAST_COMM_DATE < :FEDate) THEN 2                                \n")
        .append("                          END commstate                                                             \n")
        .append("                    FROM MCU, CODE                                                                  \n")
        .append("                WHERE MCU.MCU_TYPE = CODE.ID AND MCU.SUPPLIER_ID=:supplierId) t1                    \n")
        .append("           GROUP BY t1.commstate, t1.CODE) t2                                                       \n")
        .append("  GROUP BY t2.commstate                                                                             \n")
        .append(" HAVING t2.commstate is not null                                                                    \n");

        Map<String, String> TFBeforeHour = DateTimeUtil.calcDate(Calendar.HOUR, -24);
        Map<String, String> FEBeforeHour = DateTimeUtil.calcDate(Calendar.HOUR, -48);

        String TFDate = TFBeforeHour.get("date").replace("-", "") + TFBeforeHour.get("time").replace(".", "");
        String FEDate = FEBeforeHour.get("date").replace("-", "") + FEBeforeHour.get("time").replace(".", "");

        Query query = getEntityManager().createNativeQuery(sbQuery.toString());
        query.setParameter("TFDate", TFDate);
        query.setParameter("FEDate", FEDate);
        query.setParameter("supplierId", Integer.parseInt(supplierId));
//      List result = query.list();
//
//      List<CommStateByMCUTypeVO> rtnValues = new ArrayList<CommStateByMCUTypeVO>();
//      CommStateByMCUTypeVO rtnValue = null;
//      Object[] resultData = null;
//
//      for(int i = 0, size = result.size() ; i < size ; i++) {
//
//          resultData = (Object[])result.get(i);
//
//          // indoor, outdoor, dcu
//          rtnValue = new CommStateByMCUTypeVO();
//          rtnValue.setCommState(pastCommStateToName(resultData[0].toString()));
//          rtnValue.setIndoorCnt(resultData[4].toString());
//          rtnValue.setOutdoorCnt(resultData[5].toString());
////            rtnValue.setDCUCnt(resultData[8].toString());
//
//          rtnValues.add(rtnValue);
//      }

        // TODO
        // return JSONArray.fromObject(query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list()).toString();
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Map<String, Object>> getCommStateListByMCUTypeData(String supplierId) {

        StringBuffer sbQuery = new StringBuffer()
        .append(" SELECT t2.commstate as commstate,                                                                  \n");
//      .append("        MAX(CASE WHEN t2.CODE = '1.1.1.3' THEN t2.cnt ELSE 0 END) DCU,                          \n")
//      .append("        MAX(CASE WHEN t2.CODE = '1.1.1.4' THEN t2.cnt ELSE 0 END) Indoor,                           \n")
//      .append("        MAX(CASE WHEN t2.CODE = '1.1.1.5' THEN t2.cnt ELSE 0 END) Outdoor                           \n")
        int j = 0;
        UsingMCUType[] usingMCUTypes = UsingMCUType.values();
        for(UsingMCUType mcuType : usingMCUTypes) {
            sbQuery.append(" MAX(CASE WHEN t2.CODE = '" + mcuType.getCode() +"' THEN t2.cnt ELSE 0 END) as " + mcuType.name() + " ");
            if(j != usingMCUTypes.length - 1) sbQuery.append(", "); j++;
        }
        sbQuery.append("   FROM (SELECT t1.commstate, t1.CODE, count(*) cnt                                         \n")
        .append("           FROM (SELECT CODE,                                                                       \n")
        .append("                         CASE WHEN (LAST_COMM_DATE >= :TFDate) THEN 0                               \n")
        .append("                              WHEN (LAST_COMM_DATE < :TFDate AND LAST_COMM_DATE >= :FEDate) THEN 1  \n")
        .append("                              WHEN (LAST_COMM_DATE < :FEDate) THEN 2                                \n")
        .append("                          END commstate                                                             \n")
        .append("                    FROM MCU, CODE                                                                  \n")
        .append("                WHERE MCU.MCU_TYPE = CODE.ID AND MCU.SUPPLIER_ID=:supplierId) t1                    \n")
        .append("           GROUP BY t1.commstate, t1.CODE) t2                                                       \n")
        .append("  GROUP BY t2.commstate                                                                             \n")
        .append(" HAVING t2.commstate is not null                                                                    \n");

        Map<String, String> TFBeforeHour = DateTimeUtil.calcDate(Calendar.HOUR, -24);
        Map<String, String> FEBeforeHour = DateTimeUtil.calcDate(Calendar.HOUR, -48);

        String TFDate = TFBeforeHour.get("date").replace("-", "") + TFBeforeHour.get("time").replace(".", "");
        String FEDate = FEBeforeHour.get("date").replace("-", "") + FEBeforeHour.get("time").replace(".", "");

        Query query = getEntityManager().createNativeQuery(sbQuery.toString());
        query.setParameter("TFDate", TFDate);
        query.setParameter("FEDate", FEDate);
        query.setParameter("supplierId", Integer.parseInt(supplierId));
        
        // TODO
        // return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        return null;
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
        .append("                 FROM (SELECT DISTINCT(CASE WHEN (LAST_COMM_DATE  >= '" + TFDate + "') THEN 0                                    \n")
        .append("                                          WHEN (LAST_COMM_DATE < '" + TFDate + "' AND LAST_COMM_DATE >= '" + FEDate + "') THEN 1 \n")
        .append("                                          WHEN (LAST_COMM_DATE < '" + FEDate + "') THEN 2                                        \n")
        .append("                                      END) commstate                                                                             \n")
        .append("                        FROM MCU) cs,                                                                                            \n")
        .append("                      (SELECT LOCATION_ID FROM MCU GROUP BY LOCATION_ID HAVING LOCATION_ID is not null) li) t1                   \n")
        .append("            LEFT OUTER JOIN                                                                                                      \n")
        .append("                 (SELECT LOCATION_ID,                                                                                            \n")
        .append("                         CASE WHEN (LAST_COMM_DATE  >= '" + TFDate + "') THEN 0                                                  \n")
        .append("                          WHEN (LAST_COMM_DATE < '" + TFDate + "' AND LAST_COMM_DATE >= '" + FEDate + "') THEN 1                 \n")
        .append("                          WHEN (LAST_COMM_DATE < '" + FEDate + "') THEN 2                                                        \n")
        .append("                      END commstate,                                                                                             \n")
        .append("                     COUNT(*) CNT                                                                                                \n")
        .append("                  FROM MCU                                                                                                       \n")
        .append("                 GROUP BY LOCATION_ID,                                                                                           \n")
        .append("                          (CASE WHEN (LAST_COMM_DATE  >= '" + TFDate + "') THEN 0                                                \n")
        .append("                              WHEN (LAST_COMM_DATE < '" + TFDate + "' AND LAST_COMM_DATE >= '" + FEDate + "') THEN 1             \n")
        .append("                              WHEN (LAST_COMM_DATE < '" + FEDate + "') THEN 2                                                    \n")
        .append("                          END)) t2                                                                                               \n")
        .append("              ON t1.LOCATION_ID = t2.LOCATION_ID                                                                                 \n")
        .append("             AND t1.COMMSTATE = t2.COMMSTATE   ) t3,                                                                             \n")
        .append("        LOCATION t4                                                                                                              \n")
        .append("  WHERE t3.location_id = t4.id                                                                                                   \n")
        .append("    AND t3.commstate is not null                                                                                                 \n");

        Query query = getEntityManager().createNativeQuery(sbQuery.toString());
        List<Object[]> result = query.getResultList();

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
        .append("                 FROM (SELECT DISTINCT(CASE WHEN (LAST_COMM_DATE  >= '" + TFDate + "') THEN 0                                    \n")
        .append("                                          WHEN (LAST_COMM_DATE < '" + TFDate + "' AND LAST_COMM_DATE >= '" + FEDate + "') THEN 1 \n")
        .append("                                          WHEN (LAST_COMM_DATE < '" + FEDate + "') THEN 2                                        \n")
        .append("                                      END) commstate                                                                             \n")
        .append("                        FROM MCU WHERE SUPPLIER_ID= :supplierId) cs,                                                             \n")
        .append("                      (SELECT LOCATION_ID FROM MCU GROUP BY LOCATION_ID HAVING LOCATION_ID is not null) li) t1                   \n")
        .append("            LEFT OUTER JOIN                                                                                                      \n")
        .append("                 (SELECT LOCATION_ID,                                                                                            \n")
        .append("                         CASE WHEN (LAST_COMM_DATE  >= '" + TFDate + "') THEN 0                                                  \n")
        .append("                          WHEN (LAST_COMM_DATE < '" + TFDate + "' AND LAST_COMM_DATE >= '" + FEDate + "') THEN 1                 \n")
        .append("                          WHEN (LAST_COMM_DATE < '" + FEDate + "') THEN 2                                                        \n")
        .append("                      END commstate,                                                                                             \n")
        .append("                     COUNT(*) CNT                                                                                                \n")
        .append("                  FROM MCU                                                                                                       \n")
        .append("                 GROUP BY LOCATION_ID,                                                                                           \n")
        .append("                          (CASE WHEN (LAST_COMM_DATE  >= '" + TFDate + "') THEN 0                                                \n")
        .append("                              WHEN (LAST_COMM_DATE < '" + TFDate + "' AND LAST_COMM_DATE >= '" + FEDate + "') THEN 1             \n")
        .append("                              WHEN (LAST_COMM_DATE < '" + FEDate + "') THEN 2                                                    \n")
        .append("                          END)) t2                                                                                               \n")
        .append("              ON t1.LOCATION_ID = t2.LOCATION_ID                                                                                 \n")
        .append("             AND t1.COMMSTATE = t2.COMMSTATE   ) t3,                                                                             \n")
        .append("        LOCATION t4                                                                                                              \n")
        .append("  WHERE t3.location_id = t4.id                                                                                                   \n")
        .append("    AND t3.commstate is not null                                                                                                 \n");

        Query query = getEntityManager().createNativeQuery(sbQuery.toString());
        query.setParameter("supplierId", Integer.parseInt(supplierId));
        List<Object[]> result = query.getResultList();

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
        .append("                 FROM (SELECT DISTINCT(CASE WHEN (LAST_COMM_DATE  >= '" + TFDate + "') THEN 0                                    \n")
        .append("                                          WHEN (LAST_COMM_DATE < '" + TFDate + "' AND LAST_COMM_DATE >= '" + FEDate + "') THEN 1 \n")
        .append("                                          WHEN (LAST_COMM_DATE < '" + FEDate + "') THEN 2                                        \n")
        .append("                                      END) commstate                                                                             \n")
        .append("                        FROM MCU WHERE SUPPLIER_ID= :supplierId) cs,                                                             \n")
        .append("                      (SELECT LOCATION_ID FROM MCU GROUP BY LOCATION_ID HAVING LOCATION_ID is not null) li) t1                   \n")
        .append("            LEFT OUTER JOIN                                                                                                      \n")
        .append("                 (SELECT LOCATION_ID,                                                                                            \n")
        .append("                         CASE WHEN (LAST_COMM_DATE  >= '" + TFDate + "') THEN 0                                                  \n")
        .append("                          WHEN (LAST_COMM_DATE < '" + TFDate + "' AND LAST_COMM_DATE >= '" + FEDate + "') THEN 1                 \n")
        .append("                          WHEN (LAST_COMM_DATE < '" + FEDate + "') THEN 2                                                        \n")
        .append("                      END commstate,                                                                                             \n")
        .append("                     COUNT(*) CNT                                                                                                \n")
        .append("                  FROM MCU                                                                                                       \n")
        .append("                 GROUP BY LOCATION_ID,                                                                                           \n")
        .append("                          (CASE WHEN (LAST_COMM_DATE  >= '" + TFDate + "') THEN 0                                                \n")
        .append("                              WHEN (LAST_COMM_DATE < '" + TFDate + "' AND LAST_COMM_DATE >= '" + FEDate + "') THEN 1             \n")
        .append("                              WHEN (LAST_COMM_DATE < '" + FEDate + "') THEN 2                                                    \n")
        .append("                          END)) t2                                                                                               \n")
        .append("              ON t1.LOCATION_ID = t2.LOCATION_ID                                                                                 \n")
        .append("             AND t1.COMMSTATE = t2.COMMSTATE   ) t3,                                                                             \n")
        .append("        LOCATION t4                                                                                                              \n")
        .append("  WHERE t3.location_id = t4.id                                                                                                   \n")
        .append("    AND t3.commstate is not null                                                                                                 \n");

        Query query = getEntityManager().createNativeQuery(sbQuery.toString());
        query.setParameter("supplierId", Integer.parseInt(supplierId));
        return query.getResultList();
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

        String sysId             = StringUtil.nullToBlank(condition.get("sysId"));
        String supplierId        = StringUtil.nullToBlank(condition.get("supplierId"));


        StringBuffer sbQuery      = new StringBuffer();

        sbQuery = new StringBuffer();
        sbQuery.append("  SELECT SYS_ID                         \n")
               .append("    FROM MCU                            \n")
               .append("   WHERE SYS_ID LIKE '%"+ sysId + "%'   \n")
               .append("     AND SUPPLIER_ID = "+ supplierId      )
               .append("   ORDER BY 1                           \n");

        Query query = getEntityManager().createNativeQuery(sbQuery.toString());


        result.add(query.getResultList());

        return result;

    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRED)
    public List<MCU> getMCUWithGpio(HashMap<String, Object> condition){
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<MCU> criteria = cb.createQuery(MCU.class);
        Root<MCU> from = criteria.from(MCU.class);

        Set<String> set = condition.keySet();
        Object []hmKeys = set.toArray();
        for (int i=0; i<hmKeys.length; i++) {
            String key = (String)hmKeys[i];
            if (key.equals("sysID")) {
                criteria.where(cb.like(from.<String>get("sysID"), cb.literal("%"+condition.get(key)+"%")));
            } else {
                criteria.where(cb.equal(from.get(key), condition.get(key)));
            }
        }

        // 좌표 정보가 없으면 값을 반환하지 않는다.
        criteria.where(cb.isNotNull(from.get("gpioX")));
        criteria.where(cb.isNotNull(from.get("gpioY")));
        criteria.where(cb.isNotNull(from.get("gpioZ")));

        TypedQuery typedQuery = getEntityManager().createQuery(criteria);
        List<MCU> mcus = typedQuery.getResultList();

        return mcus;
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRED)
    public List<MCU> getMCUWithGpioCodi(HashMap<String, Object> condition){
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<MCU> criteria = cb.createQuery(MCU.class);
        Root<MCU> from = criteria.from(MCU.class);

        Set<String> set = condition.keySet();
        Object []hmKeys = set.toArray();
        for (int i=0; i<hmKeys.length; i++) {
            String key = (String)hmKeys[i];
            if (key.equals("sysID")) {
                criteria.where(cb.like(from.<String>get("sysID"), cb.literal("%"+condition.get(key)+"%")));
            } else {
                criteria.where(cb.equal(from.get(key), condition.get(key)));
            }
        }

        // 좌표 정보가 없으면 값을 반환하지 않는다.
        criteria.where(cb.isNotNull(from.get("gpioX")));
        criteria.where(cb.isNotNull(from.get("gpioY")));
        criteria.where(cb.isNotNull(from.get("gpioZ")));
        criteria.where(cb.isNotNull(from.get("mcuCodi")));

        TypedQuery typedQuery = getEntityManager().createQuery(criteria);
        List<MCU> mcus = typedQuery.getResultList();

        return mcus;
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRED)
    public List<MCU> getMCUWithoutGpio(HashMap<String, Object> condition){
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<MCU> criteria = cb.createQuery(MCU.class);
        Root<MCU> from = criteria.from(MCU.class);
        
        Set<String> set = condition.keySet();
        Object []hmKeys = set.toArray();
        for (int i=0; i<hmKeys.length; i++) {
            String key = (String)hmKeys[i];
            if (key.equals("sysID")) {
                criteria.where(cb.like(from.<String>get("sysID"), cb.literal("%"+condition.get(key)+"%")));
            } else {
                criteria.where(cb.equal(from.get(key), condition.get(key)));
            }
        }

        TypedQuery<MCU> typedQuery = getEntityManager().createQuery(criteria);
        List<MCU> mcus = typedQuery.getResultList();

        return mcus;
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRED)
    public List<MCU> getMCUMapDataWithoutGpio(HashMap<String, Object> condition) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery criteria = cb.createQuery(MCU.class);
        Root<MCU> from = criteria.from(MCU.class);

        Set<String> set = condition.keySet();
        Object []hmKeys = set.toArray();

        for (int i = 0 ; i < hmKeys.length ; i++) {
            String key = (String)hmKeys[i];
            criteria.where(cb.equal(from.get(key), condition.get(key)));
        }

        TypedQuery typedQuery = getEntityManager().createQuery(criteria);
        List<MCU> mcus = typedQuery.getResultList();

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

        Query query = getEntityManager().createNativeQuery(sb.toString());
        query.setParameter("supplierId", Integer.parseInt((String)condition.get("supplierId")));
        return query.getResultList();
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
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        Integer groupId = (Integer)conditionMap.get("groupId");
        String subType = StringUtil.nullToBlank(conditionMap.get("subType"));
        String memberName = StringUtil.nullToBlank(conditionMap.get("memberName"));

        StringBuilder sb = new StringBuilder();

        sb.append("\nSELECT mc.id AS value, ");
        sb.append("\n       mc.sysID AS text, ");
        sb.append("\n       'MCU' AS type ");
        sb.append("\nFROM MCU mc left outer join mc.mcuStatus mcuStatus");
        sb.append("\nWHERE mc.supplier.id = :supplierId ");
        sb.append("\nAND (mc.mcuStatus is null OR mcuStatus.code <> :deleteCode) ");
        if (!memberName.isEmpty()) {
            sb.append("\nAND   mc.sysID LIKE :memberName ");
        }

        sb.append("\nAND   NOT EXISTS ( ");
        sb.append("\n    SELECT 'X' ");
        sb.append("\n    FROM GroupMember gm ");
        sb.append("\n    WHERE gm.member = mc.sysID ");
        if(subType.isEmpty()) {
            sb.append("\n    AND   gm.aimirGroup.id = :groupId ");
        }
        sb.append("\n) ");
        sb.append("\nORDER BY mc.sysID ");

        Query query = getEntityManager().createQuery(sb.toString());
        query.setParameter("supplierId", supplierId);
        query.setParameter("deleteCode", McuStatus.Delete.getCode());
        if(subType.isEmpty()) {
            query.setParameter("groupId", groupId);
        }
        if (!memberName.isEmpty()) {
            query.setParameter("memberName", "%" + memberName + "%");
        }

        return query.getResultList();
    }

    /**
     * OTA Event 에서 넘어온 SQL 실행
     **/
    @Override
    public void updateFWByotaEvent(String sql)throws Exception{
        logger.debug(this.getClass().getName()+":"+"updateFWByotaEvent()");
        logger.debug("sql="+sql);
        Query query = getEntityManager().createNativeQuery(sql);

        query.executeUpdate();
    }

    // 미터의 검침데이터 목록을 조회한다.
    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRED)
    public List<MCU> getMCUbyCodi(String codiID) {
        Query query = getEntityManager().createQuery("\n FROM MCU m WHERE m.mcuCodi.codiID = :codiID ");
        query.setParameter("codiID", codiID);
        return query.getResultList();
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

        // chartData
        StringBuilder sbQuery = new StringBuilder();

        sbQuery.append("\nSELECT mc.mcuType.descr AS xTag ");
        sbQuery.append("\n     , mc.mcuType.descr AS xCode ");
        sbQuery.append("\n     , SUM(CASE WHEN mc.lastCommDate >= :datePre24H THEN 1 ELSE 0 END) AS value0 ");
        sbQuery.append("\n     , SUM(CASE WHEN mc.lastCommDate < :datePre24H ");
        sbQuery.append("\n                 AND mc.lastCommDate >= :datePre48H THEN 1 ELSE 0 END) AS value1 ");
        sbQuery.append("\n     , SUM(CASE WHEN mc.lastCommDate < :datePre48H THEN 1 ELSE 0 END) AS value2 ");
        sbQuery.append("\n     , SUM(CASE WHEN mc.lastCommDate IS NULL THEN 1 ELSE 0 END) AS value3 ");
        sbQuery.append("\nFROM MCU mc left outer join mc.mcuStatus ms");
        sbQuery.append("\nWHERE mc.supplierId = :supplierId ");
        sbQuery.append("\nAND (mc.mcuStatus is null OR ms.code <> :deleteCode)");
        sbQuery.append("\nGROUP BY mc.mcuType.descr, mc.mcuType.order ");
        sbQuery.append("\nORDER BY mc.mcuType.order ");

        List<Object[]> dataList = null;

        Query query = getEntityManager().createQuery(sbQuery.toString());
        String datePre24H = DateTimeUtil.calcDate(Calendar.HOUR, -24, "yyyyMMddhhmmss");
        String datePre48H = DateTimeUtil.calcDate(Calendar.HOUR, -48, "yyyyMMddhhmmss");

        query.setParameter("datePre24H", datePre24H);
        query.setParameter("datePre48H", datePre48H);
        query.setParameter("supplierId", Integer.parseInt(supplierId));
        query.setParameter("deleteCode", McuStatus.Delete.getCode());
        dataList = query.getResultList();

        HashMap<String, Object> chartDataMap = null;

        DecimalFormat dfMd = DecimalUtil.getMDStyle(supplierDao.get(Integer.parseInt(supplierId)).getMd());

        for (Object[] resultData : dataList) {
            chartDataMap = new HashMap<String, Object>();
            chartDataMap.put("xTag", resultData[0]);
            chartDataMap.put("xCode", resultData[1]);

            int resultDataLen = resultData.length;
            for (int j = 2; j < resultDataLen; j++) {
                chartDataMap.put("value".concat(Integer.toString(j - 2)), dfMd.format(resultData[j]));
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

        Query query = getEntityManager().createQuery(sbQuery.toString());
        yCodeList = query.getResultList();

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

        sbChartQuery.append("\nSELECT mu.commStatus AS xTag ");
        sbChartQuery.append("\n     , mu.commStatus AS xCode ");
        sbChartQuery.append(sbQueryWhere);
        sbChartQuery.append("\nFROM (SELECT cd.name AS typeName ");
        sbChartQuery.append("\n           , CASE WHEN mc.last_comm_date >= :datePre24H THEN '0' ");
        sbChartQuery.append("\n                  WHEN mc.last_comm_date < :datePre24H ");
        sbChartQuery.append("\n                   AND mc.last_comm_date >= :datePre48H THEN '1' ");
        sbChartQuery.append("\n                  WHEN mc.last_comm_date < :datePre48H THEN '2' ");
        sbChartQuery.append("\n                  ELSE '3' ");
        sbChartQuery.append("\n             END AS commStatus ");
        sbChartQuery.append("\n      FROM mcu mc, ");
        sbChartQuery.append("\n           code cd ");
        sbChartQuery.append("\n      WHERE mc.supplier_id = :supplierId ");
        sbChartQuery.append("\n      AND   cd.id = mc.mcu_type) mu ");
        sbChartQuery.append("\nGROUP BY mu.commStatus ");

        List<Object[]> dataList = null;

        query = getEntityManager().createQuery(sbChartQuery.toString());

        String datePre24H = DateTimeUtil.calcDate(Calendar.HOUR, -24, "yyyyMMddhhmmss");
        String datePre48H = DateTimeUtil.calcDate(Calendar.HOUR, -48, "yyyyMMddhhmmss");

        query.setParameter("datePre24H", datePre24H);
        query.setParameter("datePre48H", datePre48H);
        query.setParameter("supplierId", Integer.parseInt(supplierId));

        dataList = query.getResultList();

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
            } else {
                chartDataMap.put("xTag", "fmtMessage99");
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
            for (int j = 2; j < resultDataLen; j++) {
                chartDataMap.put("value".concat(Integer.toString(j - 2)), dfMd.format(resultData[j]));
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
        
        Query query = getEntityManager().createQuery(sbQuery.toString());

        return null; // query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        
    }
    
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public int updateDcuStatus(int mcuId, Code deleteCode) {
        StringBuffer sb = new StringBuffer();
        sb.append("UPDATE MCU \n");
        sb.append("set mcuStatus =:code \n");
        sb.append("WHERE id = :id");
        Query query = getEntityManager().createQuery(sb.toString());
        query.setParameter("code", deleteCode);
        query.setParameter("id", mcuId);
        return query.executeUpdate();
    }

    @Override
    public Class<MCU> getPersistentClass() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getSumFieldByCondition(Set<Condition> conditions,
            String field, String... groupBy) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<String> getMcuByIp(String ip) {
        StringBuilder sbQuery = new StringBuilder();
        sbQuery.append("\n SELECT m.sysID ");
        sbQuery.append("\n FROM MCU m");
        sbQuery.append("\n WHERE m.ipAddr = :ipv4 or m.ipv6Addr = :ipv6");
        
        Query query = getEntityManager().createQuery(sbQuery.toString());
        query.setParameter("ipv4", ip);
        query.setParameter("ipv6", ip);
        
        return query.getResultList();
    }
     
    @Override
    public List<String> getFirmwareVersionList(Map<String, Object> condition) {
        StringBuilder sbQuery = new StringBuilder();
        Query query = getEntityManager().createQuery(sbQuery.toString());
        return query.getResultList();
    }
    
    @Override
    public List<String> getDeviceList(Map<String, Object> condition) {
        StringBuilder sbQuery = new StringBuilder();
        Query query = getEntityManager().createQuery(sbQuery.toString());
        return query.getResultList();
    }
    
    @Override
    public List<String> getTargetList(Map<String, Object> condition) {
        StringBuilder sbQuery = new StringBuilder();
        Query query = getEntityManager().createQuery(sbQuery.toString());
        return query.getResultList();
    }
    
    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<String> getGroupMcuList(Integer groupId) {
        StringBuffer sbQuery = new StringBuffer()
        .append("\nSELECT m.sysID AS MCUID")
        .append("\nFROM GroupMember g, MCU m, Location l ")
        .append("\nWHERE g.member = m.sysID ")
        .append("\nAND m.locationId = l.id ")
        .append("\nAND g.groupId = :groupId ");
        
        Query query = getEntityManager().createQuery(sbQuery.toString());
        query.setParameter("groupId", groupId);
        	        
        List<String> result = query.getResultList();
        
	return result;
	
    }

	@Override
	public List<Map<String, Object>> getValidMCUList(Map<String, Object> condition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getCodiFirmwareVersionList(Map<String, Object> condition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getCodiDeviceList(Map<String, Object> condition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getCodiTargetList(Map<String, Object> condition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> getCodiGridData(Map<String, Object> conditionMap, boolean isCount) {
		// TODO Auto-generated method stub
		return null;
	}		

	@Override
	public List<Map<String, Object>> getValidCodiList(Map<String, Object> condition) {
		// TODO Auto-generated method stub
		return null;
	}
    
    
}