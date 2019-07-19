package com.aimir.dao.system;

import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.system.AuditLog;

public interface AuditLogDao extends GenericDao<AuditLog, Integer> {

    /**
     * method name : getAuditLogRankingList
     * method Desc : ChangeLog 미니가젯의 AuditLog Ranking 리스트를 조회한다.
     *
     * @param conditionMap
	 * <ul>
	 * <li> startDate : Calendar
	 * <li> endDate : Calendar
	 * <li> page :page number
	 * <li> limit : max limit
	 * </ul>
	 *         
     * @param isCount total count 여부
     * @return List of Map if isCount is true then  return {total, count}
     *                     else return {
     *                      entityName,
     *                      propertyName,
     *                      action, 
     *                      count
     *                     }
     */
    public List<Map<String, Object>> getAuditLogRankingList(Map<String, Object> conditionMap, boolean isCount);

    /**
     * method name : getAuditLogList
     * method Desc : ChangeLog 맥스가젯의 AuditLog 리스트를 조회한다.
     *
     * @param conditionMap
	 * <ul>
	 * <li> action : action
	 * <li> equipType : Calendar
	 * <li> equipName :page number
	 * <li> propertyName : property Name (Entity's property name)
	 * <li> startDate : yyyymmdd
	 * <li> endDate : yyyymmdd
	 * <li> page :page number
	 * <li> limit : max limit
	 * </ul>
	 * 
     * @param isCount total count 여부
     * @return List of Map  if isCount is true then return {total, count]
     *                      else return {
     *                       action,  
     *                       createdDate,  
     *                       entityName,  
     *                       equipName,  
     *                       propertyName, 
     *                       previousState,  
     *                       currentState
     *                      }
     */
    public List<Map<String, Object>> getAuditLogList(Map<String, Object> conditionMap, boolean isCount);
}
