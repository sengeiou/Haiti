package com.aimir.service.system.impl;

import java.util.List;
import java.util.Map;

public interface EBSStore {
	public List<ETreeNode> getTreeList(List<Map<String, Object>> result) ;
	public List refind(List<Map<String, Object>> result);
}
