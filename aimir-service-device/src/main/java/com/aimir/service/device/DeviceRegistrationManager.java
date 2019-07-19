package com.aimir.service.device;

import java.util.List;
import java.util.Map;

import com.aimir.model.system.DeviceVendor;


public interface DeviceRegistrationManager {
	
	// Mini
	public List<Object> getMiniChart(Map<String, Object> condition);
	
	public List<DeviceVendor> getVendorListBySubDeviceType(Map<String, Object> condition);
	
	public List<Object> getDeviceRegLog(Map<String, Object> condition);

	public String getTitleName(String excel, String ext);

	public Map<String,Object> readExcelXLS(String excel, String fileType, int supplierId, String detailType);
	
	public Map<String,Object> readExcelXLSX(String excel, String fileType, int supplierId, String detailType);
	
	public Map<String, Object> readShipmentExcelXLS(String excel, String fileType, int supplierId, String detailType);

	public Map<String, Object> readShipmentExcelXLSX(String excel, String fileType, int supplierId, String detailType);

	public Map<String, Object> readOnlyExcelXLS(String excel, String fileType, int supplierId, String detailType);

	public Map<String, Object> readOnlyExcelXLSX(String excel, String fileType, int supplierId, String detailType);

	public Object insertDevice(Object obj, String fileType,String detailType);
	
	// 로그 기록
	public void insertDeviceRegLog(Map<String, Object> insertData);
	
	/**
	 * 
	 * 자산관리 미니 가젯의 차트 데이터를 검색한다.
	 * 집중기, 활성화된 미터, 모뎀, 계약, 고객의 개수를 조회
	 * 
	 * @param condition
	 * @return
	 */
	public Map<String,Object> getAssetMiniChart(Map<String, String> condition);

    /**
     * method name : updateDevice<b/>
     * method Desc : Device Bulk 등록 시 기존 데이터가 있으면 Update 한다.
     *
     * @param objList
     * @param fileType
     * @param detailType
     * @return
     */
    public void updateDevice(List<Object> objList, String fileType, String detailType);

	public List<Object> getShipmentImportHistory(Map<String, Object> condition);

	public Integer getShipmentImportHistoryTotalCount(Map<String, Object> condition);
    
	public Map<String, Object> readDeviceIdExcelXLSX(String excel, String targetDeviceType, String modelId, String sType, String SupplierId);
}
