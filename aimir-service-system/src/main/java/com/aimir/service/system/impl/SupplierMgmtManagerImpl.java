package com.aimir.service.system.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.extractor.ExcelExtractor;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants;
import com.aimir.dao.mvm.SeasonDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.dao.system.TOURateDao;
import com.aimir.dao.system.TariffEMDao;
import com.aimir.dao.system.TariffGMDao;
import com.aimir.dao.system.TariffTypeDao;
import com.aimir.dao.system.TariffWMCaliberDao;
import com.aimir.dao.system.TariffWMDao;
import com.aimir.model.mvm.Season;
import com.aimir.model.system.Code;
import com.aimir.model.system.Supplier;
import com.aimir.model.system.TariffEM;
import com.aimir.model.system.TariffGM;
import com.aimir.model.system.TariffType;
import com.aimir.model.system.TariffWM;
import com.aimir.model.system.TariffWMCaliber;
import com.aimir.service.system.SupplierMgmtManager;
import com.aimir.service.system.TariffTypeManager;
import com.aimir.util.CalendarUtil;
import com.aimir.util.DecimalUtil;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeUtil;

@WebService(endpointInterface = "com.aimir.service.system.SupplierMgmtManager")
@Service(value = "supplierMgmtManager")
public class SupplierMgmtManagerImpl implements SupplierMgmtManager {

	Log logger = LogFactory.getLog(SupplierMgmtManagerImpl.class);

	@Autowired
    TariffTypeDao tariffTypeDao;
    @Autowired
    TariffTypeManager tariffTypeManager;
    @Autowired
    HibernateTransactionManager transactionManager;
    @Autowired
    TariffEMDao tariffEMDao;
    @Autowired
    TariffGMDao tariffGMDao;
    @Autowired
    TariffWMDao tariffWMDao;
    @Autowired
    TOURateDao touRateDao;
    @Autowired
    TariffWMCaliberDao tariffWMCaliberDao;
    @Autowired
    SupplierDao supplierDao;
    @Autowired
    CodeDao codeDao;
    @Autowired
    SeasonDao seasonDao;

    public List<Object> getYyyymmddList(String supplierType, Integer supplierId) {

        List<Object> result = null;

        if (supplierType
                .equals(CommonConstants.SupplierType.Electricity.name())) {
            result = tariffEMDao.getYyyymmddList(supplierId);
        } else if (supplierType.equals(CommonConstants.SupplierType.Gas.name())) {
            result = tariffGMDao.getYyyymmddList(supplierId);
        } else if (supplierType.equals(CommonConstants.SupplierType.Water
                .name())) {
            result = tariffWMDao.getYyyymmddList(supplierId);
        }
        return result;
    }
    
    public String getAppliedTariffDate(String supplierType, String yyyymmdd, Integer supplierId) {
		String date = StringUtil.nullToBlank(yyyymmdd);
		// Integer emCount = tariffEMDao.getRowCount();
		
		if (date.equals("")) {
			date = CalendarUtil.getCurrentDate();
		}
		
		// if (supplierType.equals(CommonConstants.SupplierType.Electricity.name()) && emCount > 0) {
		if (supplierType.equals(CommonConstants.SupplierType.Electricity.name())) {
			return tariffEMDao.getAppliedTariffDate(date, supplierId);
		} else {
			return null;
		}
    }

    public Map<String, Object> getChargeMgmtList(Map<String, Object> condition) {

        String strSupplierId = (String) condition.get("supplierId");
        Integer supplierId = 0;
        if (!"".equals(StringUtil.nullToBlank(strSupplierId))) {
            supplierId = Integer.parseInt(strSupplierId);
        }
        condition.put("supplierId", supplierId);

        String supplierType = (String) condition.get("supplyTypeName");
        String yyyymmdd = (String) condition.get("yyyymmdd");
        condition.put("yyyymmdd", StringUtil.nullToBlank(yyyymmdd));

        Map<String, Object> result = new HashMap<String, Object>();
        DecimalFormat dfCd = DecimalUtil.getDecimalFormat(supplierDao.get(supplierId).getCd());
        
        if (supplierType
                .equals(CommonConstants.SupplierType.Electricity.name())) {
            List<Map<String, Object>> listEM = tariffEMDao.getChargeMgmtList(condition);
            
            //formatting
            for (int i = 0; i < listEM.size(); i++) {
                Map<String, Object> em = listEM.get(i);
                if(em.get("SERVICECHARGE") != null)
                    em.put("SERVICECHARGE", dfCd.format(Double.parseDouble(listEM.get(i).get("SERVICECHARGE").toString())));
                if(em.get("ADMINCHARGE") != null)
                    em.put("ADMINCHARGE", dfCd.format(Double.parseDouble(listEM.get(i).get("ADMINCHARGE").toString())));
                if(em.get("TRANSMISSIONNETWORKCHARGE") != null)
                    em.put("TRANSMISSIONNETWORKCHARGE", dfCd.format(Double.parseDouble(listEM.get(i).get("TRANSMISSIONNETWORKCHARGE").toString())));
                if(em.get("DISTRIBUTIONNETWORKCHARGE") != null)
                    em.put("DISTRIBUTIONNETWORKCHARGE", dfCd.format(Double.parseDouble(listEM.get(i).get("DISTRIBUTIONNETWORKCHARGE").toString())));
                if(em.get("ENERGYDEMANDCHARGE") != null)
                    em.put("ENERGYDEMANDCHARGE", dfCd.format(Double.parseDouble(listEM.get(i).get("ENERGYDEMANDCHARGE").toString())));
                if(em.get("ACTIVEENERGYCHARGE") != null)
                    em.put("ACTIVEENERGYCHARGE", dfCd.format(Double.parseDouble(listEM.get(i).get("ACTIVEENERGYCHARGE").toString())));
                if(em.get("REACTIVEENERGYCHARGE") != null)
                    em.put("REACTIVEENERGYCHARGE", dfCd.format(Double.parseDouble(listEM.get(i).get("REACTIVEENERGYCHARGE").toString())));
                if(em.get("RATEREBALANCINGLEVY") != null)
                    em.put("RATEREBALANCINGLEVY", dfCd.format(Double.parseDouble(listEM.get(i).get("RATEREBALANCINGLEVY").toString())));
                if(em.get("MAXDEMAND") != null)
                    em.put("MAXDEMAND", dfCd.format(Double.parseDouble(listEM.get(i).get("MAXDEMAND").toString())));
                if(em.get("ERS") != null) {
                    em.put("ERS", dfCd.format(Double.parseDouble(listEM.get(i).get("ERS").toString())));
                }
                if(em.get("STARTHOUR") != null && em.get("ENDHOUR") != null) {
                    String startHour = StringUtil.nullToBlank(em.get("STARTHOUR"));
                    String endHour = StringUtil.nullToBlank(em.get("ENDHOUR"));
                    em.put("HOUR", startHour + "-" + endHour);
                }
            }
            
            result.put("grid", listEM);
            
            
        } else if (supplierType.equals(CommonConstants.SupplierType.Gas.name())
                || supplierType
                        .equals(CommonConstants.SupplierType.Heat.name())) {
            List<Map<String, Object>> listGM = tariffGMDao.getChargeMgmtList(condition);
            
            //formatting
            for (int i = 0; i < listGM.size(); i++) {
                if(listGM.get(i).get("basicRate") != null)
                    listGM.get(i).put("basicRate", dfCd.format(Double.parseDouble(listGM.get(i).get("basicRate").toString())));
                if(listGM.get(i).get("usageUnitPrice") != null)
                    listGM.get(i).put("usageUnitPrice", dfCd.format(Double.parseDouble(listGM.get(i).get("usageUnitPrice").toString())));
                if(listGM.get(i).get("salePrice") != null)
                    listGM.get(i).put("salePrice", dfCd.format(Double.parseDouble(listGM.get(i).get("salePrice").toString())));
            }
            
            result.put("grid", listGM);
            
        } else if (supplierType.equals(CommonConstants.SupplierType.Water
                .name()) || supplierType.equals("WaterCaliber")) {
            List<Map<String, Object>> listWM = tariffWMDao.getChargeMgmtList(condition); 
            List<Map<String, Object>> listCaliber = tariffWMCaliberDao.getChargeMgmtList(condition);
            
            for (int i = 0; i < listWM.size(); i++) {
                if(listWM.get(i).get("share") != null && !listWM.get(i).get("share").equals(""))
                    listWM.get(i).put("share", dfCd.format(Double.parseDouble(listWM.get(i).get("share").toString())));
                if(listWM.get(i).get("usageUnitPrice") != null && !listWM.get(i).get("usageUnitPrice").equals("")){
                    //listWM.get(i).put("usageUnitPrice", dfCd.format(Double.parseDouble(listWM.get(i).get("usageUnitPrice").toString())));
                    listWM.get(i).put("usageUnitPrice", dfCd.format(Double.parseDouble(listWM.get(i).get("usageUnitPrice").toString())));
                    listWM.get(i).put("SERVICECHARGE", listWM.get(i).get("usageUnitPrice"));
                }
                
                // 160216 : 전기 Tariff화면에 적용하기 위한 Header 변경
                if(listWM.get(i).get("tariffType") != null && !listWM.get(i).get("tariffType").equals(""))
                	listWM.get(i).put("TARIFFTYPE", listWM.get(i).get("tariffType").toString());
                if(listWM.get(i).get("condition1") != null && !listWM.get(i).get("condition1").equals(""))
                	listWM.get(i).put("CONDITION1", listWM.get(i).get("condition1").toString());
                if(listWM.get(i).get("condition2") != null && !listWM.get(i).get("condition2").equals(""))
                	listWM.get(i).put("CONDITION2", listWM.get(i).get("condition2").toString());
                if(listWM.get(i).get("supplySizeMin") != null && !listWM.get(i).get("supplySizeMin").equals(""))
                	listWM.get(i).put("SUPPLYSIZEMAX", listWM.get(i).get("supplySizeMax").toString());
                if(listWM.get(i).get("supplySizeMin") != null && !listWM.get(i).get("supplySizeMin").equals(""))
                	listWM.get(i).put("SUPPLYSIZEMIN", listWM.get(i).get("supplySizeMin").toString());              
                if(listWM.get(i).get("supplySizeUnit") != null && !listWM.get(i).get("supplySizeUnit").equals(""))
                	listWM.get(i).put("SUPPLYSIZEUNIT", listWM.get(i).get("supplySizeUnit").toString());
                // DeleteRow 기능을 사용하기 위한 Header 추가
                if(listWM.get(i).get("id") != null && !listWM.get(i).get("id").equals(""))
                	listWM.get(i).put("ID", listWM.get(i).get("id"));
                if(listWM.get(i).get("WATER") != null && !listWM.get(i).get("WATER").equals(""))
                	listWM.get(i).put("SEASONID", "WATER");
            }
            
            for (int i = 0; i < listCaliber.size(); i++) {
                if(listCaliber.get(i).get("basicRate") != null && listCaliber.get(i).get("basicRate").equals(""))
                    listCaliber.get(i).put("basicRate", dfCd.format(Double.parseDouble(listCaliber.get(i).get("basicRate").toString())));
                if(listCaliber.get(i).get("basicRateHot") != null || listCaliber.get(i).get("basicRateHot").equals(""))
                    listCaliber.get(i).put("basicRateHot", dfCd.format(Double.parseDouble(listCaliber.get(i).get("basicRateHot").toString())));
            }
            
            
            // Tariff 탭
            result.put("grid", listWM);
            // ExcelExport
            result.put("grid2", listWM);            
            result.put("grid1", listCaliber);
        }
        
        

        return result;
    }

    @Transactional
    public int updateData(List<Object> tariffs, String supplierType)
            throws Exception {
        int returnCnt = 0;
        TransactionStatus txStatus = null;
        try { 
            txStatus = transactionManager.getTransaction(null);
            for (Object obj : tariffs) {
                if (supplierType.equals(CommonConstants.SupplierType.Electricity
                        .name())) {
                    TariffEM tariffEM = (TariffEM)obj;
                    TariffType tariffType = tariffEM.getTariffType();
                    if(tariffType == null || tariffType.getId() < 1
                            || tariffEM.getId() < 1) {
                        List<TariffType> tariffTypeList = tariffTypeDao.getTariffTypeByName(tariffType.getName());
                        TariffType existTariffType = tariffTypeList.size() > 0 ? tariffTypeList.get(0) : null;
                        if(existTariffType != null) {
                            tariffType = existTariffType;
                        }
                        
                        if((tariffType == null || tariffType.getId() < 1) && !("".equals(tariffType.getName().trim()))) {
                            Code energyCode = codeDao.getCodeByName("Energy");
                            Map<String, Object> condition = new HashMap<String, Object>();
                            condition.put("name", CommonConstants.SupplierType.Electricity.name());
                            condition.put("parentCodeId", energyCode != null ? energyCode.getId() : null);
                            Code code = codeDao.getCodeByCondition(condition);
                            tariffType.setSupplier(supplierDao.get(tariffType.getSupplierId()));
                            tariffType.setServiceTypeCode(code);
                            tariffType.setCode(null);
                            
                            tariffTypeDao.add(tariffType);
                            tariffTypeDao.flush();
                        }
                        
                        if(tariffEM.getId() < 1) {
                            
                            tariffEM.setTariffType(tariffType);
                            tariffEM.setYyyymmdd(TimeUtil.getCurrentDateUsingFormat("yyyymmdd"));
                            Double serviceCharge = tariffEM.getServiceCharge();
                            Double adminCharge = tariffEM.getAdminCharge();
                            Double transmissionNetworkCharge = tariffEM.getTransmissionNetworkCharge();
                            Double distributionNetworkCharge = tariffEM.getDistributionNetworkCharge();
                            Double energyDemandCharge = tariffEM.getEnergyDemandCharge();
                            Double activeEnergyCharge = tariffEM.getActiveEnergyCharge();
                            Double reactiveEnergyCharge = tariffEM.getReactiveEnergyCharge();
                            Double rateRebalancingLevy = tariffEM.getRateRebalancingLevy();
                            
                            int count = 0;
                            
                            if("NaN".equals(serviceCharge.toString())) {
                                tariffEM.setServiceCharge(null);
                                count++;
                            }
                            if("NaN".equals(adminCharge.toString())) {
                                tariffEM.setAdminCharge(null);
                                count++;
                            }
                            if("NaN".equals(transmissionNetworkCharge.toString())) {
                                tariffEM.setTransmissionNetworkCharge(null);
                                count++;
                            }
                            if("NaN".equals(distributionNetworkCharge.toString())) {
                                tariffEM.setDistributionNetworkCharge(null);
                                count++;
                            }
                            if("NaN".equals(energyDemandCharge.toString())) {
                                tariffEM.setEnergyDemandCharge(null);
                                count++;
                            }
                            if("NaN".equals(activeEnergyCharge.toString())) {
                                tariffEM.setActiveEnergyCharge(null);
                                count++;
                            }
                            if("NaN".equals(reactiveEnergyCharge.toString())) {
                                tariffEM.setReactiveEnergyCharge(null);
                                count++;
                            }
                            if("NaN".equals(rateRebalancingLevy.toString())) {
                                tariffEM.setRateRebalancingLevy(null);
                                count++;
                            }
                            if(count < 8) {
                                tariffEMDao.add(tariffEM);
                                tariffEMDao.flush();
                            }
                        }
                        returnCnt = 1;
                    } else {
                        List<TariffType> tariffTypeList = tariffTypeDao.getTariffTypeByName(tariffEM.getTariffType().getName());
                        TariffType existTariffType = tariffTypeList.size() > 0 ? tariffTypeList.get(0) : null;
                        if(existTariffType != null) {
                            tariffEM.setTariffType(existTariffType);
                        } else {
                            returnCnt += tariffTypeDao.updateData(tariffType);
                            tariffTypeDao.flush();
                        }
                        
                        if(tariffEM.getId() > 0) {
                            tariffEMDao.updateData(tariffEM);
                            tariffEMDao.flush();
                        }
                        returnCnt = 1;
                    }
                } else if (supplierType.equals(CommonConstants.SupplierType.Gas.name())
                        || supplierType.equals(CommonConstants.SupplierType.Heat
                                .name())) {
                    TariffGM tariffGM = (TariffGM) obj;
                    tariffGM.setYyyymmdd(TimeUtil.getCurrentDateUsingFormat("yyyymmdd"));
                    returnCnt += tariffGMDao.updateData((TariffGM) obj);
                }
            }
            transactionManager.commit(txStatus);
        } catch (Exception e) {
            // TODO: handle exception
            logger.error(e,e);
            if (transactionManager != null) {
                transactionManager.rollback(txStatus);
            }
            returnCnt = 0;
        }
        return returnCnt;
    }

    public Map<String, Object> updateWMData(
            List<TariffWMCaliber> tariffCalibers, List<TariffWM> tariffs)
            throws Exception {

        Map<String, Object> result = new HashMap<String, Object>();

        int returnCnt = 0;
        for (TariffWMCaliber tariff : tariffCalibers) {
            returnCnt += tariffWMCaliberDao.updateData(tariff);
        }
        result.put("grid1", returnCnt);

        returnCnt = 0;
        for (TariffWM tariff : tariffs) {
            returnCnt += tariffWMDao.updateData(tariff);
        }
        result.put("grid2", returnCnt);

        return result;
    }

    @Transactional(readOnly = false)
    public int insertEMData(Map<String, Object> conditionMap) {
        String[] serviceCharge = (String[]) conditionMap.get("serviceCharge");
        String[] adminCharge = (String[]) conditionMap.get("adminCharge");
        String[] transmissionNetworkCharge = (String[]) conditionMap
                .get("transmissionNetworkCharge");
        String[] distributionNetworkCharge = (String[]) conditionMap
                .get("distributionNetworkCharge");
        String[] energyDemandCharge = (String[]) conditionMap
                .get("energyDemandCharge");
        String[] activeEnergyCharge = (String[]) conditionMap
                .get("activeEnergyCharge");
        String[] reactiveEnergyCharge = (String[]) conditionMap
                .get("reactiveEnergyCharge");
        String[] rateRebalancingLevy = (String[]) conditionMap
                .get("rateRebalancingLevy");
        String[] maxDemand = (String[]) conditionMap
                .get("maxDemand");
        String[] tariffType = (String[]) conditionMap.get("tariffType");
        String[] season = (String[]) conditionMap.get("season");
        String[] yyyymmdd = (String[]) conditionMap.get("yyyymmdd");
        String supplyTypeName = (String) conditionMap.get("supplyTypeName");

        int returnCnt = 0;

        if (supplyTypeName.equals(CommonConstants.SupplierType.Electricity
                .name())) {
            TariffEM tariffEM = new TariffEM();
            int len = tariffType.length;

            for (int i = 0; i < len; i++) {
                tariffEM = new TariffEM();
                tariffEM.setServiceCharge(Double.valueOf(StringUtil.nullCheck(
                        serviceCharge[i], "0")));
                tariffEM.setAdminCharge(Double.valueOf(StringUtil.nullCheck(
                        adminCharge[i], "0")));
                tariffEM.setTransmissionNetworkCharge(Double.valueOf(StringUtil
                        .nullCheck(transmissionNetworkCharge[i], "0")));
                tariffEM.setDistributionNetworkCharge(Double.valueOf(StringUtil
                        .nullCheck(distributionNetworkCharge[i], "0")));
                tariffEM.setEnergyDemandCharge(Double.valueOf(StringUtil
                        .nullCheck(energyDemandCharge[i], "0")));
                tariffEM.setActiveEnergyCharge(Double.valueOf(StringUtil
                        .nullCheck(activeEnergyCharge[i], "0")));
                tariffEM.setReactiveEnergyCharge(Double.valueOf(StringUtil
                        .nullCheck(reactiveEnergyCharge[i], "0")));
                tariffEM.setRateRebalancingLevy(Double.valueOf(StringUtil
                        .nullCheck(rateRebalancingLevy[i], "0")));
                tariffEM.setMaxDemand(Double.valueOf(StringUtil.nullCheck(maxDemand[i], "0")));
                tariffEM.setTariffType(tariffTypeDao.getTariffTypeByName(
                        tariffType[i]).get(0));
                tariffEM.setSeason(seasonDao.getSeasonByName(season[i]).get(0));
                tariffEM.setYyyymmdd(yyyymmdd[i]);

                tariffEMDao.add(tariffEM);
                tariffEMDao.flushAndClear();
                returnCnt++;
            }

        }

        return returnCnt;
    }

    @Transactional(readOnly = false)
    public int insertGMData(Map<String, Object> conditionMap) {
        String[] tariffType = (String[]) conditionMap.get("tariffType");
        String[] usageUnitPrice = (String[]) conditionMap.get("usageUnitPrice");
        String[] salePrice = (String[]) conditionMap.get("salePrice");
        String[] adjustmentFactor = (String[]) conditionMap
                .get("adjustmentFactor");
        String[] season = (String[]) conditionMap.get("season");
        String[] yyyymmdd = (String[]) conditionMap.get("yyyymmdd");
        String supplyTypeName = (String) conditionMap.get("supplyTypeName");

        int returnCnt = 0;

        if (supplyTypeName.equals(CommonConstants.SupplierType.Gas.name())) {
            TariffGM tariffGM = new TariffGM();
            int len = tariffType.length;

            for (int i = 0; i < len; i++) {
                tariffGM = new TariffGM();

                tariffGM.setUsageUnitPrice(Double.valueOf(StringUtil.nullCheck(
                        usageUnitPrice[i], "0")));
                tariffGM.setSalePrice(Double.valueOf(StringUtil.nullCheck(
                        salePrice[i], "0")));
                tariffGM.setAdjustmentFactor(Double.valueOf(StringUtil
                        .nullCheck(adjustmentFactor[i], "0")));
                tariffGM.setTariffType(tariffTypeDao.getTariffTypeByName(
                        tariffType[i]).get(0));
                List<Season> seasonList = seasonDao.getSeasonByName(season[i]);
                if(seasonList.size() > 0 ) {
                    tariffGM.setSeason(seasonList.get(0));
                }
                tariffGM.setYyyymmdd(yyyymmdd[i]);

                tariffGMDao.add(tariffGM);
                tariffGMDao.flushAndClear();
                returnCnt++;
            }
        }

        return returnCnt;
    }

    @Transactional(readOnly = false)
    public int insertWMData(Map<String, Object> conditionMap) {
        String[] caliber = (String[]) conditionMap.get("caliber");
        String[] basicRate = (String[]) conditionMap.get("basicRate");
        String[] basicRateHot = (String[]) conditionMap.get("basicRateHot");
        String[] supplierName = (String[]) conditionMap.get("supplierName");
        String[] writeTime = (String[]) conditionMap.get("writeTime");

        String[] usageUnitPrice = (String[]) conditionMap.get("usageUnitPrice");
        String[] share = (String[]) conditionMap.get("share");
        String[] tariffType = (String[]) conditionMap.get("tariffType");
        String[] yyyymmdd = (String[]) conditionMap.get("yyyymmdd");

        String supplyTypeName = (String) conditionMap.get("supplyTypeName");

        int returnCnt = 0;

        if (supplyTypeName.equals(CommonConstants.SupplierType.Water.name())) {
            TariffWM tariffWM = new TariffWM();
            TariffWMCaliber tariffCaliber = new TariffWMCaliber();
            int lenWM;
            int lenCaliber;
            if("".equals(caliber[0]) || caliber == null) {
                lenCaliber = 0;
            } else {
                lenCaliber = caliber.length;
            }
            
            if("".equals(tariffType[0]) || tariffType == null) {
                lenWM = 0;
            } else {
                lenWM = tariffType.length;
            }
            try {
            for (int i = 0; i < lenWM; i++) {
                tariffWM = new TariffWM();

                tariffWM.setUsageUnitPrice(Double.valueOf(StringUtil.nullCheck(usageUnitPrice[i], "0")));
                tariffWM.setShareCost((Double.valueOf(StringUtil.nullCheck(share[i], "0"))));
                tariffWM.setTariffType((tariffTypeDao.getTariffTypeByName(tariffType[i]).get(0)));
                tariffWM.setYyyymmdd((yyyymmdd[i]));

                tariffWMDao.add(tariffWM);
                tariffWMDao.flushAndClear();
                returnCnt++;
            }
            
            for (int i = 0; i < lenCaliber; i++) {
                tariffCaliber = new TariffWMCaliber();

                tariffCaliber.setCaliber((Double.valueOf(StringUtil.nullCheck(caliber[i], "0"))));
                tariffCaliber.setBasicRate((Double.valueOf(StringUtil.nullCheck(basicRate[i], "0"))));
                tariffCaliber.setBasicRateHot((Double.valueOf(StringUtil.nullCheck(basicRateHot[i], "0"))));
                tariffCaliber.setWriteTime((writeTime[i]));
                tariffCaliber.setSupplier(supplierDao.getSupplierByName(supplierName[i]));

                tariffWMCaliberDao.add(tariffCaliber);
                tariffWMCaliberDao.flushAndClear();
                returnCnt++;
            }
            }catch (Exception e) {
                e.printStackTrace();
            }
                
        }

        return returnCnt;
    }
    
    public int tariffDelete(List<Map<String,Object>> deleteDatas) {
        int result = 0;
        TransactionStatus txStatus = null;
        
        try {
             txStatus = transactionManager.getTransaction(null);
             
             for (int i = 0; i < deleteDatas.size(); i++) {
                 Map<String, Object> data = new HashMap<String, Object>();
                 data = deleteDatas.get(i);
                 
                 if("WATER".equals(data.get("seasonId").toString())){
                     Map<String, Object> condition = new HashMap<String, Object>();
                     condition.put("tariffWMId", data.get("tariffEMId"));
                     condition.put("seasonId", data.get("seasonId"));
                     condition.put("tariffTypeId", data.get("tariffTypeId"));
                     result = tariffWMDao.tariffDeleteByCondition(condition);
                 }else if(data.get("tariffEMId") == null) {
                    Map<String, Object> condition = new HashMap<String, Object>();
                    condition.put("tariffTypeId", data.get("tariffTypeId"));
                    tariffEMDao.tariffDeleteByCondition(condition);
                    
                    touRateDao.touDeleteByCondition(condition);
                    
                    TariffType tariffType = tariffTypeDao.get((Integer)data.get("tariffTypeId"));
                    tariffTypeManager.delete(tariffType);
                    result = 1;
                 } else {
                    Map<String, Object> condition = new HashMap<String, Object>();
                    condition.put("tariffEMId", data.get("tariffEMId"));
                    condition.put("seasonId", data.get("seasonId"));
                    result = tariffEMDao.tariffDeleteByCondition(condition);                    
                 }
             }
            transactionManager.commit(txStatus);
        } catch (Exception e) {
            // TODO: handle exception
            if (transactionManager != null) {
                transactionManager.rollback(txStatus);
            }
            logger.error(e,e);
            result = 0;
            
        }
        return result;
    }

    public Map<String, List<Map<String, Object>>> readOnlyExcel(
            String filePath, String fileType, int supplierId, String yyyymmdd) {

        List<Map<String, Object>> readList = new ArrayList<Map<String, Object>>();
        Map<String, List<Map<String, Object>>> result = new HashMap<String, List<Map<String, Object>>>();
        try {
            File file = new File(filePath.trim()); // jhkim trim() 추가

            // Workbook
            HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(file));

            // Text Extraction
            ExcelExtractor extractor = new ExcelExtractor(wb);
            extractor.setFormulasNotResults(true);
            extractor.setIncludeSheetNames(false);

            HSSFSheet sheet = wb.getSheetAt(0);

            Row titles = null;
            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    titles = row;
                    continue;
                }
                readList.add(getFileMapRead(titles, row, yyyymmdd));
            }

            if ("Electricity".equals(fileType) || "Gas".equals(fileType) || "Heat".equals(fileType)) {
                result.put("grid", readList);
            } else if ("Water(Caliber)".equals(fileType)) { // Water
                if (readList.get(0).containsKey("caliber")) {
                    result.put("grid1", readList);
                } else {
                    result.put("grid2", readList);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public Map<String, Object> getFileMapRead(Row titles, Row row,
            String yyyymmdd) throws IOException {

        String colName = null;
        String colValue = null;

        Map<String, Object> readData = new HashMap<String, Object>();

        for (Cell cell : row) {

            colName = titles.getCell(cell.getColumnIndex())
                    .getRichStringCellValue().getString();

            switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                colValue = cell.getRichStringCellValue().getString();
                break;

            case Cell.CELL_TYPE_NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    colValue = cell.getDateCellValue().toString();
                } else {
                    Long roundVal = Math.round(cell.getNumericCellValue());
                    Double doubleVal = cell.getNumericCellValue();
                    if (doubleVal.equals(roundVal.doubleValue())) {
                        colValue = String.valueOf(roundVal);
                    } else {
                        colValue = String.valueOf(doubleVal);
                    }
                }
                break;

            case Cell.CELL_TYPE_BOOLEAN:
                colValue = String.valueOf(cell.getBooleanCellValue());
                break;

            case Cell.CELL_TYPE_FORMULA:
                colValue = cell.getCellFormula();
                break;

            default:
                colValue = "";
            }
            readData.put("yyyymmdd", yyyymmdd);
            readData.put(colName, colValue);

        }
        return readData;
    }

    @Transactional
    public String updateTariffEMTable(String date, String jsonString) {
        String result = "success";
        JSONArray dataList = null;

        try {
            dataList = new JSONArray(jsonString);
        } catch (JSONException e) {
            result = "failed";
            e.printStackTrace();
        }
        // true: 새로운 appliedDate, false: 기존의 tariff update
        Boolean isNew = tariffEMDao.isNewDate(date);
        logger.info("isNew: " + isNew);

        if ( !isNew ) {
            tariffEMDao.deleteYyyymmddTariff(date);
        }
        if ( dataList == null || dataList.length() < 1) return result;

        for ( int i = 0 ; i < dataList.length() ; i++ ) {
            JSONObject json = null;
            TariffType tariffType = null;
            Season season = null;
            try {
                json = dataList.getJSONObject(i);
                TariffEM em = new TariffEM();

                if (!json.isNull("HOUR") && !json.get("HOUR").equals("")) {
                    String hour = json.getString("HOUR").trim();
                    String[] hours = hour.split("-");
                    String startHour = hours[0];
                    String endHour = hours[1];
                    em.setStartHour(startHour);
                    em.setEndHour(endHour);
                } else {
                    em.setStartHour(json.isNull("STARTHOUR") ? null : json.getString("STARTHOUR"));
                    em.setEndHour(json.isNull("ENDHOUR") ? null : json.getString("ENDHOUR"));
                }

                em.setSupplySizeMax(json.isNull("SUPPLYSIZEMAX") || "".equals(json.get("SUPPLYSIZEMAX")) ? null : Double
                        .valueOf(json.getString("SUPPLYSIZEMAX").replaceAll(",", "")));
                em.setRateRebalancingLevy(json.isNull("RATEREBALANCINGLEVY") || "".equals(json.get("RATEREBALANCINGLEVY")) ? null
                        : Double.valueOf(json.getString("RATEREBALANCINGLEVY").replaceAll(",", "")));
                em.setEnergyDemandCharge(json.isNull("ENERGYDEMANDCHARGE") || "".equals(json.get("ENERGYDEMANDCHARGE")) ? null
                        : Double.valueOf(json.getString("ENERGYDEMANDCHARGE").replaceAll(",", "")));
                em.setServiceCharge(json.isNull("SERVICECHARGE") || "".equals(json.get("SERVICECHARGE")) ? null : Double
                        .valueOf(json.getString("SERVICECHARGE").replaceAll(",", "")));
                em.setDistributionNetworkCharge(json.isNull("DISTRIBUTIONNETWORKCHARGE")
                        || "".equals(json.get("DISTRIBUTIONNETWORKCHARGE")) ? null : Double.valueOf(json.getString(
                        "DISTRIBUTIONNETWORKCHARGE").replaceAll(",", "")));
                em.setActiveEnergyCharge(json.isNull("ACTIVEENERGYCHARGE") || "".equals(json.get("ACTIVEENERGYCHARGE")) ? null
                        : Double.valueOf(json.getString("ACTIVEENERGYCHARGE").replaceAll(",", "")));
                em.setAdminCharge(json.isNull("ADMINCHARGE") || "".equals(json.get("ADMINCHARGE")) ? null : Double.valueOf(json
                        .getString("ADMINCHARGE").replaceAll(",", "")));

                Integer tariffTypeId = json.isNull("TARIFFTYPEID") ? null : json.getInt("TARIFFTYPEID");
                if (tariffTypeId != null) {
                    tariffType = tariffTypeDao.get(tariffTypeId);
                }
                if (tariffType != null) {
                    em.setTariffType(tariffType);
                }

                Integer seasonId = json.isNull("SEASONID") ? null : json.getInt("SEASONID");
                if (seasonId != null) {
                    season = seasonDao.get(seasonId);
                }
                if (season != null) {
                    em.setSeason(season);
                }

                em.setTransmissionNetworkCharge(json.isNull("TRANSMISSIONNETWORKCHARGE")
                        || "".equals(json.get("TRANSMISSIONNETWORKCHARGE")) ? null : Double.valueOf(json.getString(
                        "TRANSMISSIONNETWORKCHARGE").replaceAll(",", "")));
                em.setSupplySizeMin(json.isNull("SUPPLYSIZEMIN") || "".equals(json.get("SUPPLYSIZEMIN")) ? null : Double
                        .valueOf(json.getString("SUPPLYSIZEMIN").replaceAll(",", "")));
                em.setSupplySizeUnit(json.isNull("SUPPLYSIZEUNIT") || "".equals(json.get("SUPPLYSIZEUNIT")) ? null : json
                        .getString("SUPPLYSIZEUNIT"));
                em.setReactiveEnergyCharge(json.isNull("REACTIVEENERGYCHARGE") || "".equals(json.get("REACTIVEENERGYCHARGE")) ? null
                        : Double.valueOf(json.getString("REACTIVEENERGYCHARGE").replaceAll(",", "")));
                em.setMaxDemand(json.isNull("MAXDEMAND") || "".equals(json.get("MAXDEMAND")) ? null : Double.valueOf(json
                        .getString("MAXDEMAND").replaceAll(",", "")));

                em.setYyyymmdd(date);
                em.setCondition1(json.isNull("CONDITION1") || "".equals(json.get("CONDITION1")) ? null : json
                        .getString("CONDITION1"));
                em.setCondition2(json.isNull("CONDITION2") || "".equals(json.get("CONDITION2")) ? null : json
                        .getString("CONDITION2"));

                tariffEMDao.add(em);
            } catch (JSONException e) {
                result = "failed";
                e.printStackTrace();
            }
        }

        return result;
    }
    
    //수도 Tariff 업데이트
    @Transactional
    public String updateTariffWMTable(String date, String jsonString) {
        String result = "success";
        JSONArray dataList = null;

        try {
            dataList = new JSONArray(jsonString);
        } catch (JSONException e) {
            result = "failed";
            e.printStackTrace();
        }
        // true: 새로운 appliedDate, false: 기존의 tariff update
        Boolean isNew = tariffWMDao.isNewDate(date);
        logger.info("isNew: " + isNew);

        if ( !isNew ) {
            // 기존 tariff_wm 지우고 새로 삽입
            tariffWMDao.deleteYyyymmddTariff(date);
        }
        if ( dataList == null || dataList.length() < 1) return result;

        for ( int i = 0 ; i < dataList.length() ; i++ ) {
            JSONObject json = null;
            TariffType tariffType = null;            
            try {
                json = dataList.getJSONObject(i);
                TariffWM wm = new TariffWM();

                wm.setSupplySizeMax(json.isNull("SUPPLYSIZEMAX") || "".equals(json.get("SUPPLYSIZEMAX")) ? null : Double
                        .valueOf(json.getString("SUPPLYSIZEMAX").replaceAll(",", "")));
                wm.setUsageUnitPrice(json.isNull("SERVICECHARGE") || "".equals(json.get("SERVICECHARGE")) ? null : Double
                        .valueOf(json.getString("SERVICECHARGE").replaceAll(",", "")));
                                
               Integer tariffTypeId = json.isNull("TARIFFTYPEID")||json.get("TARIFFTYPEID").equals("") ? null : json.getInt("TARIFFTYPEID");
                if (tariffTypeId != null) {
                    tariffType = tariffTypeDao.get(tariffTypeId);
                }else {
                    List<TariffType> tfts = tariffTypeDao.getTariffTypeByName(json.isNull("TARIFFTYPE") || "".equals(json.get("TARIFFTYPE")) ? null :
                        json.getString("TARIFFTYPE"));                  
                    if(tfts.size()<1) {
                        result = "failed";
                        break;
                    }
                    tariffType = tfts.get(0);
                }
                if (tariffType != null) {
                    wm.setTariffType(tariffType);
                }
                
                wm.setSupplySizeMin(json.isNull("SUPPLYSIZEMIN") || "".equals(json.get("SUPPLYSIZEMIN")) ? null : Double
                        .valueOf(json.getString("SUPPLYSIZEMIN").replaceAll(",", "")));
                // supplySizeUnit이 null인 경우 default값 "m3" 설정
                wm.setSupplySizeUnit(json.isNull("SUPPLYSIZEUNIT") || "".equals(json.get("SUPPLYSIZEUNIT")) ? "m3" : json
                        .getString("SUPPLYSIZEUNIT"));
                
                wm.setYyyymmdd(date);
                wm.setCondition1(json.isNull("CONDITION1") || "".equals(json.get("CONDITION1")) ? null : json
                        .getString("CONDITION1"));
                wm.setCondition2(json.isNull("CONDITION2") || "".equals(json.get("CONDITION2")) ? null : json
                        .getString("CONDITION2"));

                tariffWMDao.add(wm);
            } catch (JSONException e) {
                result = "failed";
                e.printStackTrace();
            }
        }

        return result;
    }
    
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public TariffType addTariffType(Map<String, Object> condition) {
        TariffType tariffType = null; 
        String energyType = StringUtil.nullToBlank(condition.get("energyType"));
        String tariffTypeName = StringUtil.nullToBlank(condition.get("tariffType"));
        Integer supplierId = (Integer) ObjectUtils.defaultIfNull(condition.get("supplierId"), 0 ); 
        Supplier supplier = supplierDao.get(supplierId);
        Code serviceType = codeDao.getCodeByName(energyType);
        tariffType = tariffTypeDao.addTariffType(tariffTypeName, serviceType, supplier);
        return tariffType;
    }
}