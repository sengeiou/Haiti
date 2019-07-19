package com.aimir.dao.system;

import com.aimir.dao.GenericDao;
import com.aimir.model.system.GroupStrategy;

import java.util.List;
import java.util.Map;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 *
 * <p>GroupStrategy Table</p>
 * <p>Manages Metering Schedule(or Strategy) of Meter-Groups</p>
 * <p>Each groups are specified by DSO(location)</p>
 *
 * @author Han SeJin(sjhan@nuritelecom.com)
 *
 */

public interface GroupStrategyDao extends GenericDao<GroupStrategy, Integer> {

    /**
     * Get the Strategy List by supplier.id
     * @param supplierId
     */
    public List<Map<String, Object>> getStrategyBySupplier(Integer supplierId);

    /**
     * Get the Strategy List by aimirgroup.id
     * @param groupId
     */
    public List<Object> getStrategyByGroup(Integer groupId);

    /**
     * Get the Strategy List by config_name
     * @param configName
     */
    public List<Object> getStrategyByConfig(String configName);


}
