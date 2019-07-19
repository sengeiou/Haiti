package com.aimir.model.system;

import net.sf.json.JSONString;

import com.aimir.model.BaseObject;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * <pre>
 * DashboardGadget 객체의 가상 객체
 * Entity, Table 객체가 아니라 서비스에서 데이터 가공을 위해 DashboardGadget 객체를 조작하기 쉽게 가공한 객체
 * </pre>
 * @author 허윤(unimath)
 *
 */
public class DashboardGadgetPositionVO extends BaseObject implements JSONString{

    private static final long serialVersionUID = -5735955604540849151L;
    String uid;
    Integer columnIndex;
    Integer position;
    public String getUid() {
        return uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }
    public Integer getColumnIndex() {
        return columnIndex;
    }
    public void setColumnIndex(Integer columnIndex) {
        this.columnIndex = columnIndex;
    }
    public Integer getPosition() {
        return position;
    }
    public void setPosition(Integer position) {
        this.position = position;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 0;
        result = prime * result
                + ((columnIndex == null) ? 0 : columnIndex.hashCode());
        result = prime * result
                + ((position == null) ? 0 : position.hashCode());
        result = prime * result + ((uid == null) ? 0 : uid.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (getClass() != obj.getClass())
            return false;
        DashboardGadgetPositionVO other = (DashboardGadgetPositionVO) obj;
        if (columnIndex == null) {
            if (other.columnIndex != null)
                return false;
        } else if (!columnIndex.equals(other.columnIndex))
            return false;
        if (position == null) {
            if (other.position != null)
                return false;
        } else if (!position.equals(other.position))
            return false;
        if (uid == null) {
            if (other.uid != null)
                return false;
        } else if (!uid.equals(other.uid))
            return false;
        return true;
    }
    @Override
    public String toString() {
        return "GadgetVO [columnIndex=" + columnIndex + ", position="
                + position + ", uid=" + uid + "]";
    }
    public String toJSONString() {
        // TODO Auto-generated method stub
        return toString();
    }
}
