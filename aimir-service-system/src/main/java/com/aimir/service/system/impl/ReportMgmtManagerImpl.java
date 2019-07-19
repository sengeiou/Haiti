/**
 * ReportMgmtManagerImpl.java Copyright NuriTelecom Limited 2011
 */
package com.aimir.service.system.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants.ReportParameterType;
import com.aimir.constants.CommonConstants.ResultStatus;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.OperatorDao;
import com.aimir.dao.system.ReportContactsDao;
import com.aimir.dao.system.ReportContactsGroupDao;
import com.aimir.dao.system.ReportDao;
import com.aimir.dao.system.ReportParameterDao;
import com.aimir.dao.system.ReportParameterDataDao;
import com.aimir.dao.system.ReportResultDao;
import com.aimir.dao.system.ReportScheduleDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.model.system.Code;
import com.aimir.model.system.Location;
import com.aimir.model.system.Operator;
import com.aimir.model.system.ReportContacts;
import com.aimir.model.system.ReportContactsGroup;
import com.aimir.model.system.ReportParameter;
import com.aimir.model.system.ReportParameterData;
import com.aimir.model.system.ReportResult;
import com.aimir.model.system.ReportSchedule;
import com.aimir.model.system.Supplier;
import com.aimir.service.system.ReportMgmtManager;
import com.aimir.util.Condition;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeLocaleUtil;
import com.aimir.util.Condition.Restriction;

/**
 * ReportMgmtManagerImpl.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 9. 16.  v1.0        문동규   리포트관리 Service Impl
 *
 */
@WebService(endpointInterface = "com.aimir.service.system.ReportMgmtManager")
@Service(value = "reportMgmtManager")
public class ReportMgmtManagerImpl implements ReportMgmtManager {

    private static Log log = LogFactory.getLog(ReportMgmtManagerImpl.class);

    @Autowired
    ReportResultDao reportResultDao; 

    @Autowired
    ReportDao reportDao; 

    @Autowired
    ReportContactsDao reportContactsDao; 

    @Autowired
    ReportContactsGroupDao reportContactsGroupDao; 

    @Autowired
    ReportScheduleDao reportScheduleDao; 

    @Autowired
    ReportParameterDao reportParameterDao; 

    @Autowired
    ReportParameterDataDao reportParameterDataDao; 

    @Autowired
    SupplierDao supplierDao; 

    @Autowired
    LocationDao locationDao; 

    @Autowired
    OperatorDao operatorDao; 

    @Autowired
    CodeDao codeDao; 

    /**
     * method name : getReportResultListTotalCount
     * method Desc : Report 관리 미니가젯에서 스케줄 실행 결과 리스트의 total count를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public Integer getReportResultListTotalCount(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result = reportResultDao.getReportResultList(conditionMap, true);
        
        return ((Long)result.get(0).get("total")).intValue();
    }

    /**
     * method name : getReportResultList
     * method Desc : Report 관리 미니가젯에서 스케줄 실행 결과 리스트를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getReportResultList(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result = reportResultDao.getReportResultList(conditionMap, false);
        
        Supplier supplier = supplierDao.get((Integer)conditionMap.get("supplierId"));
        String lang = supplier.getLang().getCode_2letter();
        String country = supplier.getCountry().getCode_2letter();
        
        for (Map<String, Object> map : result) {
            map.put("writeTime", TimeLocaleUtil.getLocaleDate((String)map.get("writeTime"), lang, country));
        }
        return result;
    }

    /**
     * method name : deleteReportResult
     * method Desc : Report 관리 미니가젯에서 스케줄 실행 결과를 삭제한다.
     *
     * @param conditionMap
     */
    @Transactional
    @SuppressWarnings("unchecked")
    public void deleteReportResult(Map<String, Object> conditionMap) {
        List<Integer> resultIds = (List<Integer>)conditionMap.get("resultIds");
        String dataFileDir = (String)conditionMap.get("dataFileDir");
        String exportFileDir = (String)conditionMap.get("exportFileDir");
        
        List<String> dataFileNames = new ArrayList<String>();
        List<String> exportFileNames = new ArrayList<String>();
        ReportResult reportResult = new ReportResult();

        for (Integer resultId : resultIds) {
            reportResult = new ReportResult();
            reportResult = reportResultDao.get(resultId);
            reportResultDao.delete(reportResult);

            if (!StringUtil.nullToBlank(reportResult.getResultLink()).isEmpty()) {
                dataFileNames.add(reportResult.getResultLink());
            }

            if (!StringUtil.nullToBlank(reportResult.getResultFileLink()).isEmpty()) {
                exportFileNames.add(reportResult.getResultFileLink());
            }
        }

        // Report Data File 삭제
        if (dataFileNames.size() > 0) {
            deleteReportFile (dataFileDir, dataFileNames);
        }

        // Report Export File 삭제
        if (exportFileNames.size() > 0) {
            deleteReportFile (exportFileDir, exportFileNames);
        }
    }

    /**
     * method name : deleteReportFile<b/>
     * method Desc : ReportSchedule Result File 을 삭제한다.
     *
     * @param destDir
     * @param destFileList
     */
    private void deleteReportFile (String destDir, List<String> destFileList) {
        File file = null;

        if (destFileList.size() > 0) {
            for (String destFile : destFileList) {
                file = new File(destDir + File.separator + destFile);
                
                if (file.exists() && file.isFile()) {
                    if (!file.delete()) {
                        log.error("This file is not deleted : [" + destDir + File.separator + destFile + "]");
                    }
                }
            }
        }
        
    }

    /**
     * method name : getReportTreeData
     * method Desc : Report 관리 화면에서 Report Tree 데이터를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getReportTreeData(Map<String, Object> conditionMap) {
        
//        Supplier supplier = supplierDao.get((Integer)conditionMap.get("supplierId"));
        Integer roleId = (Integer)conditionMap.get("roleId");

        List<Map<String, Object>> result = reportDao.getReportTreeRootList(conditionMap);
        
        for (Map<String, Object> map : result) {
            if (StringUtil.nullToBlank(map.get("categoryItem")).equals("true")) {
                map.put("children", getReportTreeChildData(roleId, (Integer)map.get("reportId")));
            }
            map.put("description", StringUtil.nullToBlank(map.get("description")));
        }

        return result;
    }

    /**
     * method name : getReportTreeChildData
     * method Desc : Report 관리 화면에서 Report Tree Child 데이터를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    private List<Map<String, Object>> getReportTreeChildData(Integer roleId, Integer parentId) {

        List<Map<String, Object>> list = reportDao.getReportTreeChildList(roleId, parentId);
        
        for (Map<String, Object> map : list) {
            if (StringUtil.nullToBlank(map.get("categoryItem")).equals("true")) {
                map.put("children", getReportTreeChildData(roleId, (Integer)map.get("reportId")));
            }
            map.put("description", StringUtil.nullToBlank(map.get("description")));
        }

        return list;
    }

    /**
     * method name : getReportContactsGroupComboData
     * method Desc : Report 관리에서 Email Contacts Group Combo Data 를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getReportContactsGroupComboData(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result = reportContactsGroupDao.getReportContactsGroupComboData(conditionMap);
        
        return result;
    }

    /**
     * method name : getReportContactsListTotalCount
     * method Desc : Report 관리에서 Email Contacts 리스트의 total count를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public Integer getReportContactsListTotalCount(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result = reportContactsDao.getReportContactsList(conditionMap, true);
        
        return ((Long)result.get(0).get("total")).intValue();
    }

    /**
     * method name : getReportContactsList
     * method Desc : Report 관리에서 Email Contacts 리스트를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getReportContactsList(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result = reportContactsDao.getReportContactsList(conditionMap, false);
        
        return result;
    }

    /**
     * method name : insertReportContactsGroup
     * method Desc : Report 관리 화면의 Email Contacts Group 정보를 등록한다.
     *
     * @param conditionMap
     * @return
     */
    @Transactional
    public void insertReportContactsGroup(Map<String, Object> conditionMap) {
        String groupName = (String)conditionMap.get("groupName");
        Integer operatorId = (Integer)conditionMap.get("operatorId");

        Operator operator = operatorDao.getOperatorById(operatorId);
        
        ReportContactsGroup reportContactsGroup = new ReportContactsGroup();
        reportContactsGroup.setName(groupName);
        reportContactsGroup.setOperator(operator);
        reportContactsGroupDao.add(reportContactsGroup);
    }

    /**
     * method name : updateReportContactsGroup
     * method Desc : Report 관리 미니가젯의 Email Contacts Group 정보를 수정한다.
     *
     * @param conditionMap
     * @return
     */
    @Transactional
    public void updateReportContactsGroup(Map<String, Object> conditionMap) {
        Integer groupId = (Integer)conditionMap.get("groupId");
        String groupName = (String)conditionMap.get("groupName");

//        ReportContactsGroup group = reportContactsGroupDao.get(groupId);
        ReportContactsGroup reportContactsGroup = reportContactsGroupDao.get(groupId);
        reportContactsGroup.setName(groupName);
//        reportContacts.setEmail(email);
//        reportContacts.setGroupName(groupName);
//        reportContacts.setGroup(group);
        reportContactsGroupDao.update(reportContactsGroup);
    }

    /**
     * method name : deleteReportContactsGroup
     * method Desc : Report 관리 화면의 Email Contacts Group 정보를 삭제한다.
     *
     * @param conditionMap
     */
    @Transactional
    public void deleteReportContactsGroup(Map<String, Object> conditionMap) {
        Integer groupId = (Integer)conditionMap.get("groupId");

        // ReportContacts 조회
        Set<Condition> set = new HashSet<Condition>();
        Condition condition = new Condition();

        condition.setField("group.id");
        condition.setValue(new Object[] {groupId});
        condition.setRestrict(Restriction.EQ);
        set.add(condition);

        List<ReportContacts> contactsList = reportContactsDao.findByConditions(set);

        // Group 에 포함되어있는 ReportContacts 삭제
        for (ReportContacts contacts : contactsList) {
            reportContactsDao.delete(contacts);
        }

        ReportContactsGroup group = reportContactsGroupDao.get(groupId);
        reportContactsGroupDao.delete(group);
    }

    /**
     * method name : insertReportContactsData
     * method Desc : Report 관리 미니가젯의 Email Contacts 정보를 등록한다.
     *
     * @param conditionMap
     * @return
     */
    @Transactional
    public void insertReportContactsData(Map<String, Object> conditionMap) {
        String name = (String)conditionMap.get("name");
        String email = (String)conditionMap.get("email");
        Integer groupId = (Integer)conditionMap.get("groupId");
        Integer operatorId = (Integer)conditionMap.get("operatorId");

        ReportContacts reportContacts = null;

        Operator operator = operatorDao.getOperatorById(operatorId);
        ReportContactsGroup group = reportContactsGroupDao.get(groupId);
        
        reportContacts = new ReportContacts();
        reportContacts.setName(name);
        reportContacts.setEmail(email);
        reportContacts.setGroup(group);
        reportContacts.setOperator(operator);
        reportContactsDao.add(reportContacts);
    }

    /**
     * method name : updateReportContactsData
     * method Desc : Report 관리 미니가젯의 Email Contacts 정보를 수정한다.
     *
     * @param conditionMap
     * @return
     */
    @Transactional
    public void updateReportContactsData(Map<String, Object> conditionMap) {
        Integer contactsId = (Integer)conditionMap.get("contactsId");
        String name = (String)conditionMap.get("name");
        String email = (String)conditionMap.get("email");
        Integer groupId = (Integer)conditionMap.get("groupId");
//        Integer operatorId = (Integer)conditionMap.get("operatorId");

        ReportContactsGroup group = reportContactsGroupDao.get(groupId);
        ReportContacts reportContacts = reportContactsDao.get(contactsId);
        reportContacts.setName(name);
        reportContacts.setEmail(email);
        reportContacts.setGroup(group);
        reportContactsDao.update(reportContacts);
    }

    /**
     * method name : deleteReportContactsData
     * method Desc : Report 관리 화면의 Email Contacts 정보를 삭제한다.
     *
     * @param conditionMap
     * @return
     */
    @Transactional
    public Integer deleteReportContactsData(Map<String, Object> conditionMap) {
        Integer contactsId = (Integer)conditionMap.get("contactsId");

        int delCnt = reportContactsDao.deleteById(contactsId);
        return delCnt;
    }

    /**
     * method name : insertReportSchedule
     * method Desc : Report 관리 미니가젯의 Report Schedule 정보를 등록한다.
     *
     * @param conditionMap
     * @return
     */
    @Transactional
    public void insertReportSchedule(Map<String, Object> conditionMap) {

        Integer reportId = (Integer)conditionMap.get("reportId");
        String sheduleName = (String)conditionMap.get("scheduleName");
        String cronFormat = (String)conditionMap.get("cronFormat");
        String exportFormat = (String)conditionMap.get("exportFormat");
        Boolean useEmailYn = (Boolean)conditionMap.get("useEmailYn");
        String email = (String)conditionMap.get("email");
        String startDate = (String)conditionMap.get("startDate");
        String endDate = (String)conditionMap.get("endDate");
        Integer locationId = (Integer)conditionMap.get("locationId");
        String meterType = (String)conditionMap.get("meterType");
        Integer operatorId = (Integer)conditionMap.get("operatorId");

        ReportSchedule reportSchedule = new ReportSchedule();
        ReportParameterData reportParameterData = new ReportParameterData();
//        List<ReportParameterData> paramDataList = new ArrayList<ReportParameterData>();
        

        Operator operator = operatorDao.getOperatorById(operatorId);

        // setting ReportSchedule
        reportSchedule.setName(sheduleName);
        reportSchedule.setCronFormat(cronFormat);
        reportSchedule.setExportFormat(exportFormat);
        reportSchedule.setUsed(useEmailYn);
        reportSchedule.setEmail(email);
        reportSchedule.setOperator(operator);
        reportSchedule.setWriteTime(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
//        reportSchedule.setParameterData(paramDataList);

        // insert ReportSchedule
        ReportSchedule rtnSchedule = reportScheduleDao.add(reportSchedule);

        // ReportParameter 조회
        Set<Condition> set = new HashSet<Condition>();
        Condition condition = new Condition();

        condition.setField("report.id");
        condition.setValue(new Object[] { reportId });
        condition.setRestrict(Restriction.EQ);
        set.add(condition);

        List<ReportParameter> paramList = reportParameterDao.findByConditions(set);

        // insert ReportParameterData List
        for (ReportParameter parameter : paramList) {
            reportParameterData = new ReportParameterData();
            reportParameterData.setReportParameter(parameter);

            if (parameter.getParameterType() != null) {
                if (parameter.getParameterType().equals(ReportParameterType.Period)) {
                    reportParameterData.setValue(startDate+","+endDate);
                } else if (parameter.getParameterType().equals(ReportParameterType.Location)) {
                    reportParameterData.setValue(locationId.toString());
                } else if (parameter.getParameterType().equals(ReportParameterType.MeterType)) {
                    reportParameterData.setValue(meterType);
                }
            }
            reportParameterData.setReportSchedule(rtnSchedule);
            reportParameterDataDao.add(reportParameterData);
        }

    }

    /**
     * method name : updateReportSchedule
     * method Desc : Report 관리 맥스가젯에서 Report Schedule 정보를 수정한다.
     *
     * @param conditionMap
     * @return
     */
    @Transactional
    public void updateReportSchedule(Map<String, Object> conditionMap) {
        Integer scheduleId = (Integer)conditionMap.get("scheduleId");
        String sheduleName = (String)conditionMap.get("scheduleName");
        String cronFormat = (String)conditionMap.get("cronFormat");
        String exportFormat = (String)conditionMap.get("exportFormat");
        Boolean useEmailYn = (Boolean)conditionMap.get("useEmailYn");
        String email = (String)conditionMap.get("email");
        String startDate = (String)conditionMap.get("startDate");
        String endDate = (String)conditionMap.get("endDate");
        Integer locationId = (Integer)conditionMap.get("locationId");
        String meterType = (String)conditionMap.get("meterType");

        ReportSchedule reportSchedule = new ReportSchedule();
//        ReportParameterData reportParameterData = new ReportParameterData();

        // setting ReportSchedule
        reportSchedule = reportScheduleDao.get(scheduleId);
        reportSchedule.setName(sheduleName);
        reportSchedule.setCronFormat(cronFormat);
        reportSchedule.setExportFormat(exportFormat);
        reportSchedule.setUsed(useEmailYn);
        reportSchedule.setEmail(email);

        // update ReportSchedule
        reportScheduleDao.update(reportSchedule);

        Set<Condition> set = new HashSet<Condition>();
        Condition condition = new Condition();

        condition.setField("reportSchedule.id");
        condition.setValue(new Object[] { scheduleId });
        condition.setRestrict(Restriction.EQ);
        set.add(condition);

        List<ReportParameterData> paramDataList = reportParameterDataDao.findByConditions(set);
        
        // update ReportParameterData List
        for (ReportParameterData paramData : paramDataList) {

            if (paramData.getReportParameter().getParameterType() != null) {
                if (paramData.getReportParameter().getParameterType().equals(ReportParameterType.Period)) {
                    paramData.setValue(startDate+","+endDate);
                } else if (paramData.getReportParameter().getParameterType().equals(ReportParameterType.Location)) {
                    paramData.setValue(locationId.toString());
                } else if (paramData.getReportParameter().getParameterType().equals(ReportParameterType.MeterType)) {
                    paramData.setValue(meterType);
                }
            }
            reportParameterDataDao.update(paramData);
        }
    }

    /**
     * method name : deleteReportSchedule
     * method Desc : Report 관리 화면에서 Report Schedule 정보를 삭제한다.
     *
     * @param conditionMap
     */
    @Transactional(readOnly = false)
    public void deleteReportSchedule(Map<String, Object> conditionMap) {
        Integer scheduleId = (Integer)conditionMap.get("scheduleId");
        String dataFileDir = (String)conditionMap.get("dataFileDir");
        String exportFileDir = (String)conditionMap.get("exportFileDir");

        List<String> dataFileNames = new ArrayList<String>();
        List<String> exportFileNames = new ArrayList<String>();

        Set<Condition> set = new HashSet<Condition>();
        Condition condition = new Condition();

        condition.setField("reportSchedule.id");
        condition.setValue(new Object[] { scheduleId });
        condition.setRestrict(Restriction.EQ);
        set.add(condition);

        List<ReportParameterData> paramDataList = reportParameterDataDao.findByConditions(set);
        
        // delete ReportParameterData List
        for (ReportParameterData paramData : paramDataList) {
            reportParameterDataDao.delete(paramData);
        }

        set = new HashSet<Condition>();
        condition = new Condition();

        condition.setField("reportSchedule.id");
        condition.setValue(new Object[] { scheduleId });
        condition.setRestrict(Restriction.EQ);
        set.add(condition);

        List<ReportResult> resultList = reportResultDao.findByConditions(set);
        
        // delete ReportResult List
        for (ReportResult result : resultList) {
            reportResultDao.delete(result);
            
            if (!StringUtil.nullToBlank(result.getResultLink()).isEmpty()) {
                dataFileNames.add(result.getResultLink());
            }

            if (!StringUtil.nullToBlank(result.getResultFileLink()).isEmpty()) {
                exportFileNames.add(result.getResultFileLink());
            }
        }

        ReportSchedule reportSchedule = reportScheduleDao.get(scheduleId);
        reportScheduleDao.delete(reportSchedule);

        // Report Data File 삭제
        if (dataFileNames.size() > 0) {
            deleteReportFile (dataFileDir, dataFileNames);
        }

        // Report Export File 삭제
        if (exportFileNames.size() > 0) {
            deleteReportFile (exportFileDir, exportFileNames);
        }
    }

    /**
     * method name : getReportScheduleListTotalCount
     * method Desc : Report 관리 맥스가젯에서 스케줄 리스트의 total count를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public Integer getReportScheduleListTotalCount(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result = reportScheduleDao.getReportScheduleList(conditionMap, true);
        
        return ((Long)result.get(0).get("total")).intValue();
    }

    /**
     * method name : getReportScheduleList
     * method Desc : Report 관리 맥스가젯에서 스케줄 리스트를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getReportScheduleList(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result = reportScheduleDao.getReportScheduleList(conditionMap, false);
        List<Map<String, Object>> paramList = new ArrayList<Map<String, Object>>();

        Location loc = null;
        Code code = null;

        StringBuilder sbParam = new StringBuilder();
        Supplier supplier = supplierDao.get((Integer)conditionMap.get("supplierId"));
        String lang = supplier.getLang().getCode_2letter();
        String country = supplier.getCountry().getCode_2letter();

        Integer page = (Integer)conditionMap.get("page");
        Integer limit = (Integer)conditionMap.get("limit");
        int num = ((page - 1) * limit) + 1;

        for (Map<String, Object> map : result) {
            paramList = new ArrayList<Map<String, Object>>();
            paramList = reportParameterDataDao.getReportParameterDataBySchedule((Integer)map.get("scheduleId"));
            sbParam = new StringBuilder();

            for (int i = 0 ; i < paramList.size() ; i++) {
                Map<String, Object> param = (Map<String, Object>)paramList.get(i);

                if (i != 0) {
                    sbParam.append(", ");
                }
                sbParam.append(((ReportParameterType)param.get("parameterType")).toString());
                sbParam.append(":");

                if (((ReportParameterType)param.get("parameterType")).equals(ReportParameterType.Location)) {
                    loc = locationDao.get(new Integer((String)param.get("parameterData")));
                    map.put("paramLocation", (String)param.get("parameterData"));
                    map.put("paramLocationName", loc.getName());
                    sbParam.append(loc.getName());
                } else if (((ReportParameterType)param.get("parameterType")).equals(ReportParameterType.Period)) {
                    map.put("paramStartDate", ((String)param.get("parameterData")).substring(0, ((String)param.get("parameterData")).indexOf(",")));
                    map.put("paramEndDate", ((String)param.get("parameterData")).substring(((String)param.get("parameterData")).indexOf(",") + 1));
                    sbParam.append(TimeLocaleUtil.getLocaleDate(((String)param.get("parameterData")).substring(0, ((String)param.get("parameterData")).indexOf(",")), lang, country));
                    sbParam.append("~");
                    sbParam.append(TimeLocaleUtil.getLocaleDate(((String)param.get("parameterData")).substring(((String)param.get("parameterData")).indexOf(",") + 1), lang, country));
                } else if (((ReportParameterType)param.get("parameterType")).equals(ReportParameterType.MeterType)) {
                    map.put("paramMeterType", (String)param.get("parameterData"));
                    code = codeDao.get(new Integer((String)param.get("parameterData")));
                    sbParam.append(code.getName());
                }
            }

            map.put("num", num++);
            map.put("parameter", sbParam.toString());
            map.put("isUsed", ((Boolean)map.get("used")) ? "Yes" : "No");

            if (!StringUtil.nullToBlank(map.get("writeTime")).isEmpty()) {
                map.put("writeTime", TimeLocaleUtil.getLocaleDate((String)map.get("writeTime"), lang, country));
            }
        }
        return result;
    }

    /**
     * method name : getReportScheduleResultListTotalCount
     * method Desc : Report 관리 미니가젯에서 스케줄 실행 결과 리스트의 total count를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public Integer getReportScheduleResultListTotalCount(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result = reportResultDao.getReportScheduleResultList(conditionMap, true);
        
        return ((Long)result.get(0).get("total")).intValue();
    }

    /**
     * method name : getReportScheduleResultList
     * method Desc : Report 관리 미니가젯에서 스케줄 실행 결과 리스트를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getReportScheduleResultList(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result = reportResultDao.getReportScheduleResultList(conditionMap, false);
        
        Supplier supplier = supplierDao.get((Integer)conditionMap.get("supplierId"));
        String lang = supplier.getLang().getCode_2letter();
        String country = supplier.getCountry().getCode_2letter();
        Integer page = (Integer)conditionMap.get("page");
        Integer limit = (Integer)conditionMap.get("limit");
        int num = ((page - 1) * limit) + 1;
        
        for (Map<String, Object> map : result) {
            map.put("num", num++);
            map.put("writeTime", TimeLocaleUtil.getLocaleDate((String)map.get("writeTime"), lang, country));
            map.put("status", ((ResultStatus)map.get("result")).toString());
        }
        return result;
    }

    /***********************************************************************************************************************************************
     **** TEST REPORT_RESULT INSERT START **********************************************************************************************************
     ***********************************************************************************************************************************************
    @Transactional
    public void testInsertReportScheduleResult(Map<String, Object> conditionMap) {
        Integer scheduleId = (Integer)conditionMap.get("scheduleId");
        Integer operatorId = (Integer)conditionMap.get("operatorId");
        String dataFileDir = (String)conditionMap.get("dataFileDir");
        String exportFileDir = (String)conditionMap.get("exportFileDir");
        
        String writeTime = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss");
        String sourceDataFileName = "energyReport2011_11.20111012_test.rpt";
        String sourceExportFileName = "energySaving_test.pdf";
        String targetDataFileName = "energyReport_" + writeTime + ".rpt";
        String targetExportFileName = "energySaving_" + writeTime + ".pdf";

        
        Operator operator = operatorDao.getOperatorById(operatorId);
        
        ReportResult reportResult = new ReportResult();
        ReportSchedule reportSchedule = reportScheduleDao.get(scheduleId);
        reportResult.setReportSchedule(reportSchedule);
        reportResult.setOperator(operator);
        reportResult.setResult(ResultStatus.SUCCESS.getCode());
        reportResult.setWriteTime(writeTime);
        reportResult.setResultLink(targetDataFileName);
        reportResult.setResultFileLink(targetExportFileName);
        reportResultDao.add(reportResult);
        
        testCopyFile(dataFileDir + File.separator + sourceDataFileName, dataFileDir + File.separator + targetDataFileName);
        testCopyFile(exportFileDir + File.separator + sourceExportFileName, exportFileDir + File.separator + targetExportFileName);
        
    }
    
    private void testCopyFile (String sourceFileName, String targetFileName) {
        FileInputStream inputStream = null;
        FileOutputStream outputStream = null;
        FileChannel fcin = null;
        FileChannel fcout = null;

        try {
            File sourceFile = new File(sourceFileName);
            
            if (!sourceFile.exists() || !sourceFile.isFile()) {
                return;
            }

            //스트림 생성
            inputStream = new FileInputStream(sourceFile);
            outputStream = new FileOutputStream(targetFileName);
            //채널 생성
            fcin = inputStream.getChannel();
            fcout = outputStream.getChannel();
            
            //채널을 통한 스트림 전송
            long size = fcin.size();
            fcin.transferTo(0, size, fcout);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //자원 해제
            try{
                fcout.close();
            }catch(IOException ioe){}
            try{
                fcin.close();
            }catch(IOException ioe){}
            try{
                outputStream.close();
            }catch(IOException ioe){}
            try{
                inputStream.close();
            }catch(IOException ioe){}
        }

    }
    ***********************************************************************************************************************************************
     ***** TEST REPORT_RESULT INSERT END ***********************************************************************************************************
     ***********************************************************************************************************************************************/

}