package com.aimir.service.system.impl;

import org.apache.log4j.Logger;

import com.aimir.util.DecimalUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author 은미애
 *
 */
public class ETreeNode implements Comparable<ETreeNode> {
    private final static Logger log = Logger.getLogger(ETreeNode.class);
    protected Integer order;
    protected String text;
    protected Boolean expanded = true;
    protected List<ETreeNode> children = new ArrayList<ETreeNode>();
    protected Boolean leaf = true;
    protected String iconCls;
    protected String cls;
  //  protected Boolean checked = false;

    protected String pMid;
    protected String mid;
    protected Double total;
    protected Double expTotal;
    protected Double estiTotal;
    protected Double threshold;
    protected String ebsOrder;
    protected String typeCd;
    
    private static final String MAIN_INCOMER ="19.1.1";
    private static final String INCOMER = "19.1.2";
    private static final String FEEDER = "19.1.3";
    private static final String BULK = "19.1.4";
    private static final String MINI_SUB = "19.1.5";
    
    /**
     * @author 은미애
     *
     */
    public static class Builder {
        static public List<ETreeNode> getByTree(List<Map<String, Object>> codes) {
            return getByTree(codes, new ETreeNodeListener() {
                @Override
                public ETreeNode onConvert(ETreeNode node, Map<String, Object> code) {
                    return node;
                }

                @Override
                public ETreeNode getInstance() {
                    return new ETreeNode();
                }
            });
        }

        static public List<ETreeNode> getByTree(List<Map<String, Object>> codes, ETreeNodeListener listener) {
            List<ETreeNode> rootList = new ArrayList<ETreeNode>();
            DecimalFormat df = new DecimalFormat("0.00");
            try {
                List<Map<String, Object>> ebsDevices = codes;

                // child추가
                for (Map<String, Object> code : ebsDevices) {

                    String lCode = code.get("EBS_ORDER").toString();
                    String[] spCodeId = lCode.split("\\.");

                    List<ETreeNode> pointer = rootList;
                    ETreeNode self = null;

                    // 부모 노드를 찾거나 생성하는 과정
                    for (int i = 0; i < spCodeId.length; i++) {
                        Integer nodeIndex = Integer.parseInt(spCodeId[i]);

                        if(i==0 && nodeIndex == 0){
                            // 처음 노드 index가 0일경우 그 코드는 무시한다.
                            continue;
                        }

                        if (nodeIndex == 0) {
                            break;
                        }

                        ETreeNode node = listener.getInstance();
                        node.setOrder(nodeIndex);

                        // root list 에서 검색해서 있으면 가져온다.
                        Integer idx = pointer.indexOf(node);
                        if (idx != -1) {
                            node = pointer.get(idx);
                        } else {
                            node.setOrder(nodeIndex);
                            pointer.add(node);                            
                        }
                        pointer = node.getChildren();

                        if (self != null) {
                            self.setLeaf(false);
                        }
                        self = node;
                    }

                    Double expTotal = 0D;
 
                    int size = lCode.length();
                    int _size = spCodeId.length;
                    // 하위 노드의 사용량을 집계하여 설정함
                    for(Map<String, Object> map : codes) {
                    	String orderId = map.get("EBS_ORDER").toString();
                    	String[] orderCodeId = orderId.split("\\.");

                    	if(_size + 1 == orderCodeId.length){
                    		if(lCode.equals(orderId.substring(0, size))){
                    			expTotal = expTotal + (map.get("TOTAL") != null ? (Double)map.get("TOTAL") : 0D);
                    		}
                    	}

                    }
                    // 자신 노드를 생성하거나 tree에서 찾음

                    // code를 node 객체로 변환하는 로직
                    if(self!=null){
                        self.setText(code.get("MID").toString());
                        listener.onConvert(self, code);
                    }
                    self.setExpTotal(expTotal);
                    Double total = code.get("TOTAL") !=  null ? (Double) code.get("TOTAL") : 0D;
                    String estiValue = df.format(total - (total * (Double)code.get("THRESHOLD"))/100);
                    self.setEstiTotal(Double.valueOf(estiValue));
                    
                    if(MAIN_INCOMER.equals(code.get("TYPE_CD").toString())){
                    	self.setIconCls("main_incomer");
                    }else if (INCOMER.equals(code.get("TYPE_CD").toString())) {
                    	self.setIconCls("incomer");
                    }else if (FEEDER.equals(code.get("TYPE_CD").toString())) {
                    	self.setIconCls("feeder");
                    }else if (BULK.equals(code.get("TYPE_CD").toString())) {
                    	self.setIconCls("bulk");
                    }else {
                    	self.setIconCls("mini_sub");
                    }

                }

                Collections.sort(rootList);

                for (ETreeNode node : rootList) {
                    node.sortChilds();
                }
            } catch (Exception e) {
                log.error(e, e);
            }
            return rootList;
        }
    }
    
   

    /**
     * 자식 노드들 정렬
     */
    public void sortChilds() {
        Collections.sort(this.getChildren());
        for (ETreeNode node : this.getChildren()) {
            node.sortChilds();
        }
    }

    @Override
    public int compareTo(ETreeNode o) {
        if (this.order == null) {
            return 1;
        } else {
            return this.order.compareTo(o.getOrder());
        }
    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof ETreeNode) {
            // order 가 id역할을 하기때문에 동일 collection 상 에 존재해선 안된다.
            return ((ETreeNode) obj).getOrder().equals(this.getOrder());
        }

        return false;
    }

    public Double getEstiTotal() {
		return estiTotal;
	}

	public void setEstiTotal(Double estiTotal) {
		this.estiTotal = estiTotal;
	}

	public Double getThreshold() {
		return threshold;
	}

	public void setThreshold(Double threshold) {
		this.threshold = threshold;
	}

	public String getTypeCd() {
		return typeCd;
	}

	public void setTypeCd(String typeCd) {
		this.typeCd = typeCd;
	}

	public String getEbsOrder() {
		return ebsOrder;
	}

	public void setEbsOrder(String ebsOrder) {
		this.ebsOrder = ebsOrder;
	}

	public String getCls() {
		return cls;
	}

	public void setCls(String cls) {
		this.cls = cls;
	}

	public String getIconCls() {
		return iconCls;
	}

	public void setIconCls(String iconCls) {
		this.iconCls = iconCls;
	}

	public Double getExpTotal() {
		return expTotal;
	}

	public void setExpTotal(Double expTotal) {
		this.expTotal = expTotal;
	}

	public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Boolean getExpanded() {
        return expanded;
    }

    public void setExpanded(Boolean expanded) {
        this.expanded = expanded;
    }

    public List<ETreeNode> getChildren() {
        return children;
    }

    public void setChildren(List<ETreeNode> children) {
        this.children = children;
    }

    public Boolean getLeaf() {
        return leaf;
    }

    public void setLeaf(Boolean leaf) {
        this.leaf = leaf;
    }

	public String getpMid() {
		return pMid;
	}

	public void setpMid(String pMid) {
		this.pMid = pMid;
	}

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}

}
