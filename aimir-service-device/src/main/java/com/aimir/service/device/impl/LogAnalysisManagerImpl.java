package com.aimir.service.device.impl;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.dao.device.LogAnalysisDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.model.system.Supplier;
import com.aimir.service.device.LogAnalysisManager;
import com.aimir.service.device.bean.OpCodeConvertMap;
import com.aimir.service.device.bean.OpCodeConvertMap.ConvOper;
import com.aimir.service.device.bean.OpCodeConvertMap.Saver;
import com.aimir.util.CommonUtils2;
import com.aimir.util.TimeLocaleUtil;

@Service(value = "logAnalysisManager")
@Transactional(readOnly = false)
public class LogAnalysisManagerImpl implements LogAnalysisManager {
    private static Log logger = LogFactory.getLog(LogAnalysisManagerImpl.class);

    @Autowired
    public SupplierDao supplierDao;

    @Autowired
    public LogAnalysisDao logAnalysisDao;

    private InputStream geFileStream(String filePath) {
        return getClass().getClassLoader().getResourceAsStream(filePath);
    }

    @SuppressWarnings("unchecked")
    public OpCodeConvertMap getOpCodeConvertMap(String filePath) {
        OpCodeConvertMap opMap = null;

        SAXReader reader = new SAXReader();
        Document doc;
        try {
            doc = reader.read(new InputStreamReader(geFileStream(filePath), "UTF-8"));
            Element xmlRoot = doc.getRootElement();
            Iterator<Element> i = xmlRoot.elementIterator("operation");
            opMap = new OpCodeConvertMap();

            while (i.hasNext()) {
                Element elOper = i.next();

                ConvOper oper = new ConvOper();
                oper.setCode(elOper.attribute("code").getValue());
                oper.setName(elOper.attributeValue("name"));
                oper.setBtext(elOper.element("btext").getText());

                Iterator<Element> sEl = elOper.element("savers").elementIterator("saver");
                List<Saver> saverList = new ArrayList<Saver>();

                while (sEl.hasNext()) {
                    Element elSaver = sEl.next();

                    Saver saver = new Saver();
                    saver.setName(elSaver.attributeValue("name"));
                    saver.setOperationMsg(elSaver.getText());
                    saverList.add(saver);
                }

                oper.setSavers(saverList);
                opMap.addOperation(oper.getCode(), oper);
            }

        } catch (UnsupportedEncodingException e) {
            logger.error(e);
        } catch (DocumentException e) {
            logger.error(e);
        }

        return opMap;
    }

    @Override
    public List<Map<String, Object>> getTotalTreeGridOper(Map<String, String> conditionMap) {
        List<Map<String, Object>> result = logAnalysisDao.getGridTreeOper(conditionMap);
        
        Supplier supplier = supplierDao.get(Integer.parseInt(conditionMap.get("supplierId")));

        for (Map<String, Object> data : result) {
            data.put("DATE_BY_VIEW", TimeLocaleUtil.getLocaleDate(String.valueOf(data.get("DATE_BY_VIEW")), supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));
        }

        return result; 
    }
    
    @Override
    public List<Map<String, Object>> getTotalTreeGridData(Map<String, String> conditionMap) {
        List<Map<String, Object>> result = logAnalysisDao.getGridTreeData(conditionMap);
        
        Supplier supplier = supplierDao.get(Integer.parseInt(conditionMap.get("supplierId")));

        for (Map<String, Object> data : result) {
            data.put("DATE_BY_VIEW", TimeLocaleUtil.getLocaleDate(String.valueOf(data.get("DATE_BY_VIEW")), supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));
        }

        return result;       
    }
}