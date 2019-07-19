package com.aimir.service.system.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service(value = "ebsStore")
public class EBSStoreImpl implements EBSStore{

	final String ACTIVE_KWH = "1";
	final String Q1_REAC_KVARH = "2";
	final String Q2_REAC_KVARH = "3";
	final String KVH ="4";
	/**
	 * summary 타입이 평균/합계 인지 설정한다.
	 */
	boolean isAvgSummary = false;

	public List<ETreeNode> getTreeList(List<Map<String, Object>> result) {

		List<ETreeNode> treeNode = ETreeNode.Builder.getByTree(result, new ETreeNodeListener() {

			@Override
			public ETreeNode onConvert(ETreeNode node, Map<String, Object> code) {

				node.setText(code.get("MID").toString());
				node.setMid(code.get("MID").toString());
				node.setTotal((Double)code.get("TOTAL"));
				node.setpMid(code.get("PMID") != null ? code.get("PMID").toString() : null);
				node.setEbsOrder(code.get("EBS_ORDER").toString());
				node.setThreshold((Double)code.get("THRESHOLD"));
                node.setExpanded(true);
				return node;
			}

			@Override
			public ETreeNode getInstance() {
				return new ETreeNode();
			}
		});

		return treeNode;
	}
	
	public List refind(List<Map<String, Object>> result) {
		//데이터 저장소
		List<Map<String, Object>> data = new ArrayList();

		Map<String, Integer> dataIndex = new HashMap();
		
		Map<String, Object> thisMap = null;
		//EBSDataMapper thisEBSMap = null;

    	for(Map<String, Object> map : result) {
    		String meterId = map.get("MID") != null ? map.get("MID").toString() : "";
    	
		// 인덱스에 데이터가 있는지 확인한다.
			if (dataIndex.containsKey(meterId)) {
				Integer index = dataIndex.get(meterId);
				thisMap = data.get(index);
				//thisEBSMap = (EBSDataMapper) thisMap.get("ebsDataMap");
				//Map<String, Object> oldMap = thisMap.get(meterId);
				String channel = map.get("CHANNEL").toString();
				
				if(ACTIVE_KWH.equals(channel)){
					thisMap.put("IMP_ACTIVE_KWH", (Double)map.get("TOTAL"));
					thisMap.put("EXP_ACTIVE_KWH", (Double)map.get("CTOTAL"));
	
				}
				
				if(Q1_REAC_KVARH.equals(channel)){
					thisMap.put("IMP_Q1_REAC_KVARH", (Double)map.get("TOTAL"));
					thisMap.put("EXP_Q1_REAC_KVARH", (Double)map.get("CTOTAL"));
	
				}
				
				if(Q2_REAC_KVARH.equals(channel)){
					thisMap.put("IMP_Q2_REAC_KVARH", (Double)map.get("TOTAL"));
					thisMap.put("EXP_Q2_REAC_KVARH", (Double)map.get("CTOTAL"));
		
				}
				
				if(KVH.equals(channel)){
					thisMap.put("IMP_KVH", (Double)map.get("TOTAL"));
					thisMap.put("EXP_KVH", (Double)map.get("CTOTAL"));
				}
			} else {
				// 인덱스가 없을경우 빈 row 를 생성한다.
				
				thisMap = new HashMap<String, Object>();
				thisMap.put("MID", meterId);
				thisMap.put("THRESHOLD",  (Double)map.get("THRESHOLD"));
				thisMap.put("LOC_NAME", map.get("LOC_NAME").toString());
				String channel = map.get("CHANNEL").toString();
	
				if(ACTIVE_KWH.equals(channel)){
					thisMap.put("IMP_ACTIVE_KWH", (Double)map.get("TOTAL"));
					thisMap.put("EXP_ACTIVE_KWH", (Double)map.get("CTOTAL"));
				}
	
				if(Q1_REAC_KVARH.equals(channel)){
					thisMap.put("IMP_Q1_REAC_KVARH", (Double)map.get("TOTAL"));
					thisMap.put("EXP_Q1_REAC_KVARH", (Double)map.get("CTOTAL"));
				}
	
				if(Q2_REAC_KVARH.equals(channel)){
					thisMap.put("IMP_Q2_REAC_KVARH", (Double)map.get("TOTAL"));
					thisMap.put("EXP_Q2_REAC_KVARH", (Double)map.get("CTOTAL"));	
				}
	
				if(KVH.equals(channel)){
					thisMap.put("IMP_KVH", (Double)map.get("TOTAL"));
					thisMap.put("EXP_KVH", (Double)map.get("CTOTAL"));
				}
	
				//thisMap.put(meterId, newStatsDataMapper);
				data.add(thisMap);
				dataIndex.put(meterId, data.size() - 1);
			}
    	}
    	return data;
	}
}
