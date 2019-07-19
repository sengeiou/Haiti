/**
 * MdisMeterDao.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.dao.device;

import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.device.MdisMeter;

/**
 * MdisMeterDao.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 12. 14  v1.0        문동규   MDIS Meter 모델 Dao
 * 2012. 05. 10  v1.1        문동규   package 위치변경(mvm -> device)
 *
 */

public interface MdisMeterDao extends GenericDao<MdisMeter, Integer>{

    /**
     * method name : getMdisMeterByMeterIdBulkCommand
     * method Desc : MDIS - Meter Management 맥스가젯에서 Bulk Meter Command 에서 선택된 Meter 의 MdisMeter 정보를 조회한다.
     *
     * @param meterIdList List of Meter.id
     * @return List of com.aimir.model.device.MdisMeter
     */
    public List<Map<String, Object>> getMdisMeterByMeterIdBulkCommand(List<Integer> meterIdList);
}