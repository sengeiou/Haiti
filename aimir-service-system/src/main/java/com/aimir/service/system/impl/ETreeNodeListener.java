package com.aimir.service.system.impl;

import java.util.Map;



/**
 * @author 은미애
 *
 */
public interface ETreeNodeListener {
    /**
     * code 객체를 node 객체로 변환하는 맵핑 과정에 호출됨
     * @param node
     * @param code
     * @return
     */
    public ETreeNode onConvert(ETreeNode node, Map<String, Object> code) ;
    
    /**
     * Build 중에 노드 객체를 생성할때 호출됨
     * @return
     */
    public ETreeNode getInstance();
}
