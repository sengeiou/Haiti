package com.aimir.dao.device;

import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.device.DistTrfmrSubstation;

/**
 * DistTrfmrSubstationDao.java Description 
 * <p>
 * <pre>
 * Date          Version     Author   Description
 * 2012. 3. 13.  v1.0        문동규   Distribution Transformer Substation 조회
 * </pre>
 */
public interface DistTrfmrSubstationDao extends GenericDao<DistTrfmrSubstation, Integer> {

    /**
     * method name : getEbsSuspectedDtsList<b/>
     * method Desc : Energy Balance Monitoring 미니가젯에서 Suspected Substation List 를 조회한다. 
     *
     * @param conditionMap
     * <ul>
     * <li> searchStartDate : String - start date (yyyyMMdd)
     * <li> searchEndDate : String - end date (yyyyMMdd)
     * <li> searchPreStartDate : String - before start date (yyyyMMdd)
     * <li> page : Integer - page number
     * <li> limit : Integer - row size per page
     * </ul>
     * 
     * @param isTotal total count 여부
     * @return List of Map if isTotal is true then return {total : Integer - total count}
     *                     else return {
     *                      DTS_NAME : DistTrfmrSubstation.name - dts name
     *                      DTS_ID : DistTrfmrSubstation.id - dts id
     *                      THRESHOLD : DistTrfmrSubstation.threshold - dts threshold
     *                      IMPORT_ENERGY_TOTAL : Double - dts import energy sum
     *                      CONSUME_ENERGY_TOTAL : Double - customer consumption energy sum
     *                     }
     */
    public List<Map<String, Object>> getEbsSuspectedDtsList(Map<String, Object> conditionMap, boolean isTotal);

    /**
     * method name : getEbsDtsStateChartData<b/>
     * method Desc : Energy Balance Monitoring 미니가젯에서 Normal/Suspected Substation Count Chart Data 를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getEbsDtsStateChartData(Map<String, Object> conditionMap);
    
    /**
     * method name : getEbsDtsTreeLocationNodeData<b/>
     * method Desc : Energy Balance Monitoring 맥스가젯의 DTS Tree 의 Location Node Data 를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getEbsDtsTreeLocationNodeData(Map<String, Object> conditionMap);

    /**
     * method name : getEbsDtsTreeDtsNodeData<b/>
     * method Desc : Energy Balance Monitoring 맥스가젯의 DTS Tree 의 DTS Node Data 를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getEbsDtsTreeDtsNodeData(Map<String, Object> conditionMap);
    
    /**
     * method name : getEbsDtsList<b/>
     * method Desc : Energy Balance Monitoring 맥스가젯의 DTS List 를 조회한다. 
     *
     * @param conditionMap
     * @param isTotal
     * @return
     */
    public List<Map<String, Object>> getEbsDtsList(Map<String, Object> conditionMap, boolean isTotal);

    /**
     * method name : getEbsDtsDupCount<b/>
     * method Desc : Energy Balance Monitoring 에서 DTS Name 중복개수를 조회한다. 
     *
     * @param conditionMap
     * @return
     */
    public Integer getEbsDtsDupCount(Map<String, Object> conditionMap);

    /**
     * method name : getEbsDtsChartImportData<b/>
     * method Desc : Energy Balance Monitoring 맥스가젯의 DTS Tree 의 Chart Data 를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getEbsDtsChartImportData(Map<String, Object> conditionMap);

    /**
     * method name : getEbsExportExcelData<b/>
     * method Desc : Energy Balance Monitoring 맥스가젯의 DTS Tree 의 Export Excel Data 를 조회한다.
     *
     * @param conditionMap
     * <ul>
     * <li> supplierId : Supplier.id - supplier id
     * <li> searchStartDate : String - 조회시작일자(yyyyMMdd)
     * <li> searchEndDate : String - 조회종료일자(yyyyMMdd)
     * <li> searchPreStartDate : String - 조회시작일자 - 7일(yyyyMMdd)
     * <li> dtsName : DistTrfmrSubstation.name - dts name
     * <li> threshold : DistTrfmrSubstation.threshold - threshold
     * <li> locationIdList : List<Integer> - 조회하는 location 의 모든 하위노드
     * <li> suspected : Boolean - 의심이 가는 dts 만 조회할지 여부
     * </ul>
     * 
     * @return List of Map {LOCATION_NAME : Location.name - location name
     *                      DTS_NAME : DistTrfmrSubstation.name - dts name
     *                      THRESHOLD : DistTrfmrSubstation.threshold - threshold
     *                      IMPORT_ENERGY_TOTAL : Double - Delivered Energy(kWh)
     *                      TOL_ENERGY_TOTAL : Double - Tolerance Delivered Energy(kWh)
     *                      CONSUME_ENERGY_TOTAL : Double - Consumed Energy(kWh)
     *                     }
     */
    public List<Map<String, Object>> getEbsExportExcelData(Map<String, Object> conditionMap);
}