package com.aimir.service.system.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.dao.system.TariffEMDao;
import com.aimir.dao.system.TariffTypeDao;
import com.aimir.model.system.TariffType;
import com.aimir.service.system.TariffTypeManager;
import com.aimir.util.StringUtil;

@WebService(endpointInterface = "com.aimir.service.system.TariffTypeManager")
@Service(value = "tariffTypeManager")
public class TariffTypeManagerImpl implements TariffTypeManager{

	@Autowired
	TariffTypeDao dao;

    @Autowired
    TariffEMDao tariffEMDao;

	public List<TariffType> getAll() {
		return dao.getAll();
	}

	public List<TariffType> getTariffTypeBySupplier(String serviceType, Integer supplierId) {
		return dao.getTariffTypeBySupplier(serviceType, supplierId);
	}

	public List<TariffType> getTariffTypeList(Integer supplier , Integer serviceType) {
		return dao.getTariffTypeList(supplier, serviceType);
	}
	
	public TariffType getTariffType(Integer id) {
		return dao.get(id);
	}
	
	@Transactional(readOnly=false)
	public void delete(TariffType tariffType) {
		dao.delete(tariffType);
	}

	/**
	 * method name : getTariffSupplySizeComboData<b/>
	 * method Desc : TariffEM 의 SupplySize ComboData 를 조회한다.
	 *
	 * @param conditionMap
	 * @return
	 */
	public Map<String, Object> getTariffSupplySizeComboData(Map<String, Object> conditionMap) {
	    Map<String, Object> result = new HashMap<String, Object>();
	    List<Map<String, Object>> list = tariffEMDao.getTariffSupplySizeComboData(conditionMap);
	    List<Map<String, Object>> combo = new ArrayList<Map<String, Object>>();
	    Map<String, Object> data = null;
	    StringBuilder sbText = new StringBuilder();
	    StringBuilder sbCode = new StringBuilder();
	    boolean isEmpty = false;
	    
	    if (list != null) {
	        for (Map<String, Object> map : list) {
                if ((map.get("supplySizeMin") != null && !StringUtil.nullToBlank(map.get("condition1")).isEmpty())
                        || (map.get("supplySizeMax") != null && !StringUtil.nullToBlank(map.get("condition2")).isEmpty())) {
                    sbCode.delete(0, sbCode.length());
                    sbText.delete(0, sbText.length());

                    if (map.get("supplySizeMin") != null && !StringUtil.nullToBlank(map.get("condition1")).isEmpty()) {
                        sbText.append(map.get("supplySizeMin")).append(" ");
                        if (map.get("condition1").toString().equals(">")) {
                            sbText.append("＜");
                        } else if (map.get("condition1").toString().equals(">=")) {
                            sbText.append("≤");
                        }
                        sbText.append(" ");
                    }
                    sbText.append("x ");

                    if (map.get("supplySizeMax") != null && !StringUtil.nullToBlank(map.get("condition2")).isEmpty()) {

                        if (map.get("condition2").toString().equals("<")) {
                            sbText.append("＜");
                        } else if (map.get("condition2").toString().equals("<=")) {
                            sbText.append("≤");
                        }
                        sbText.append(" ").append(map.get("supplySizeMax"));
                    }
                    sbCode.append(StringUtil.nullToBlank(map.get("supplySizeMin"))).append(",").append(StringUtil.nullToBlank(map.get("condition1"))).append(",");
                    sbCode.append(StringUtil.nullToBlank(map.get("supplySizeMax"))).append(",").append(StringUtil.nullToBlank(map.get("condition2")));
                    data = new HashMap<String, Object>();
                    data.put("id", sbCode.toString());
                    data.put("name", sbText.toString());

                    combo.add(data);
                }
	        }
	    }

	    if (list == null || list.size() <= 0) {
	        isEmpty = true;
	    }

	    result.put("empty", isEmpty);
	    result.put("result", combo);
	    return result;
	}
}