package com.aimir.dao.device.impl;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.GroupType;
import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.model.device.Modem;
import com.aimir.model.system.Code;
import com.aimir.model.system.DecimalPattern;
import com.aimir.util.Condition;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.DecimalUtil;
import com.aimir.util.StringUtil;

@SuppressWarnings("unused")
@Repository(value = "modemDao")
public class ModemDaoImpl extends AbstractJpaDao<Modem, Integer> implements ModemDao {

    Log log = LogFactory.getLog(ModemDaoImpl.class);
    
    @Autowired
    CodeDao codeDao;
    
    @Autowired
    SupplierDao supplierDao;
    
	public ModemDaoImpl() {
		super(Modem.class);
	}

	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	public Modem get(String deviceSerial) {
	    return findByCondition("deviceSerial", deviceSerial);
	}
	
	
	// Modem 정보 저장
	public Serializable setModem(Modem modem) {
	    return add(modem);
	}

    @Override
    public Class<Modem> getPersistentClass() {
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
    public List<Object> getMiniChartModemTypeByLocation(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMiniChartModemTypeByCommStatus(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMiniChartLocationByModemType(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMiniChartLocationByCommStatus(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMiniChartCommStatusByModemType(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int deleteModemStatus(int modemId, Code code) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public List<Object> getMiniChartCommStatusByModemType(
            Map<String, Object> condition, String[] arrFmtmessagecommalert) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMiniChartCommStatusByModemType2(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMiniChartCommStatusByLocation(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMiniChartCommStatusByLocation(
            Map<String, Object> condition, String[] arrFmtmessagecommalert) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getModemSearchChart(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getModemSearchGrid(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getModemSearchGrid2(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getModemLogChart(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getModemCommLog(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getModemOperationLog(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, Object> getModemSearchCondition() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getModemSerialList(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Modem> getModemWithGpio(HashMap<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getBatteryLog(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getBatteryLogByLocation(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getBatteryLogList(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getBatteryLogList(Map<String, Object> condition,
            boolean isCount) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getBatteryLogDetailList(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getBatteryLogDetailList(Map<String, Object> condition,
            boolean isCount) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Modem> getModemWithoutGpio(HashMap<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Modem> getModemMapDataWithoutGpio(
            HashMap<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Modem> getModemHavingMCU(Integer id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getGroupMember(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMemberSelectData(Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getHomeGroupMemberSelectData(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getModemListByMCUsysID(String sys_id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getModemIdListByDevice_serial(String device_serial) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getGroupMember(String name, int supplierId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getMcuConnectedDeviceList(
            Map<String, Object> conditionMap, boolean isCount) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer getModemCount(Map<String, String> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getDeviceSerialByMcu(String sys_id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void updateModemColumn(String modemTypeName, String DeviceSerial) {
        // TODO Auto-generated method stub
        
    }    
    
	// INSERT START SP-193
    @Override
    public List<Object[]> getModemByIp(String ip) {
        StringBuilder sbQuery = new StringBuilder();
        sbQuery.append("\n SELECT m.MODEM_TYPE, m.DEVICE_SERIAL ");
        sbQuery.append("\n FROM Modem m");
        sbQuery.append("\n WHERE m.IP_ADDR = ? or m.IPV6_ADDRESS = ?");
        
        Query query = getEntityManager().createNativeQuery(sbQuery.toString());
        query.setParameter(1, ip);
        query.setParameter(2, ip);
        
        return query.getResultList();
    }
    
    @Override
    public String getModemIpv6ByDeviceSerial(String serial) {
        // TODO Auto-generated method stub
    	return null;
    }
	// INSERT END SP-193   
    
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>>  getModemWithMCU(Map<String, Object> condition) {
    	return null;
    
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

    public List<String> getTargetList2(Map<String, Object> condition){
    	return null;
    }
    
	@Override
	public List<Object> getModemList(Map<String, Object> condition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getTargetListModem(Map<String, Object> condition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getDeviceListModem(Map<String, Object> condition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Map<String, Object>> getValidModemList(Map<String, Object> condition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Map<String, Object>> getParentDevice(Map<String, Object> condition) {
		// TODO Auto-generated method stub
		return null;
	}
}