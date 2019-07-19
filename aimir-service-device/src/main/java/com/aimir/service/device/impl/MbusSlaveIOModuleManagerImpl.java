package com.aimir.service.device.impl;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.dao.device.MbusSlaveIOModuleDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.model.device.MbusSlaveIOModule;
import com.aimir.model.system.Supplier;
import com.aimir.service.device.MbusSlaveIOModuleManager;
import com.aimir.util.DecimalUtil;
import com.aimir.util.TimeLocaleUtil;

@Service(value = "mbusSlaveIOModuleManager")
@Transactional(readOnly=false)
public class MbusSlaveIOModuleManagerImpl implements MbusSlaveIOModuleManager {
    private static Log log = LogFactory.getLog(MbusSlaveIOModuleManagerImpl.class);

    @Autowired
    SupplierDao supplierDao;
    
    @Autowired
    MeterDao meterDao;
    
    @Autowired
    MbusSlaveIOModuleDao mbusSlaveIOModuleDao;
    
    @Override
    public MbusSlaveIOModule getMbusSlaveIOModule(Integer meterId) {
        return  mbusSlaveIOModuleDao.get(meterId);
    }

    @Override
    public MbusSlaveIOModule getMbusSlaveIOModule(String mdsId) {
        return mbusSlaveIOModuleDao.get(mdsId);
    }
    
    @Override
    public Map<String, Object> getMbusSlaveIOModuleInfo(String mdsId, Integer supplierId) {
        MbusSlaveIOModule module =  mbusSlaveIOModuleDao.get(mdsId);
        return getMap(module,supplierId);
    }
    
    @Override
    public Map<String, Object> getMbusSlaveIOModuleInfo(Integer meterId, Integer supplierId) {
        MbusSlaveIOModule module =  mbusSlaveIOModuleDao.get(meterId);
        return getMap(module,supplierId);
    }
    
    private Map<String, Object> getMap(MbusSlaveIOModule module, Integer supplierId){
        Supplier supplier = supplierDao.get(supplierId);
        String country = supplier.getCountry().getCode_2letter();
        String lang    = supplier.getLang().getCode_2letter();
        DecimalFormat mdf = DecimalUtil.getDecimalFormat(supplier.getMd());

        HashMap<String,Object> map = new HashMap<String,Object>();
        if ( module != null){
            map.put("mdsId", module.getMdsId());
            map.put("meterId", module.getMeterId());
            String tmpTime = (String)module.getInstallDate() != null ? (String)module.getInstallDate() : "";
            if ( !tmpTime.isEmpty()){
                map.put("installDate", TimeLocaleUtil.getLocaleDate(tmpTime, lang, country));
            }
            else {
                map.put("installDate","");
            }
            tmpTime = (String)module.getLastUpdateTime() != null ? (String)module.getLastUpdateTime(): "";
            if ( !tmpTime.isEmpty()){
                map.put("lastUpdateTime", TimeLocaleUtil.getLocaleDate(tmpTime, lang, country));
            }
            else {
                map.put("lastUpdateTime","");
            }
            map.put("analogCurrent",  module.getAnalogCurrent()== null ? "" : mdf.format(DecimalUtil.ConvertNumberToDouble(module.getAnalogCurrent())));
            map.put("analogCurrentCnv",  module.getAnalogCurrentCnv()== null ? "" : mdf.format(DecimalUtil.ConvertNumberToDouble(module.getAnalogCurrentCnv())));
            map.put("analogVoltage",  module.getAnalogVoltage()== null ? "" : mdf.format(DecimalUtil.ConvertNumberToDouble(module.getAnalogVoltage())));
            map.put("analogVoltageCnv",  module.getAnalogVoltageCnv()== null ? "" : mdf.format(DecimalUtil.ConvertNumberToDouble(module.getAnalogVoltageCnv())));
            map.put("degital", module.getDegital() == null ? "" : module.getDegital().toString());
            String hex = "";
            if ( module.getDegital() != null ){ 
                hex = String.format("0x%02X", Integer.valueOf(module.getDegitalCurrent()));
            }
            map.put("degitalHex", hex.toUpperCase());
            map.put("degitalBin", module.getDegitalString());
            ArrayList<String>  degitalArray = new ArrayList<String>();
            try {
                for ( int i = 1; i <= 8; i++ ){
                    String propName = "degital" + String.valueOf(i);
                    Object val;

                    val = PropertyUtils.getProperty(module, propName);

                    if ( val == null ){
                        degitalArray.add("");
                    }
                    else {
                        if ((Boolean)val){
                            degitalArray.add("On(1)");
                        }
                        else {
                            degitalArray.add("Off(0)");
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                degitalArray.clear();
                for ( int i = 1; i <= 8; i++ ){
                    degitalArray.add("");
                }
            } 
            map.put("degitalArray", degitalArray);
        }
        else {
            map = null;
        }
        return map;
    }

}