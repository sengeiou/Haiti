package com.aimir.model.device;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.aimir.annotation.ColumnInfo;
import com.aimir.constants.CommonConstants.SeverityType;
import com.aimir.model.BaseObject;
/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * 비동기 명령어 인자 를 정의한 클래스<br>
 * Parameter 정보 를 정의<br>
 * 
 * @author 박종성(elevas)
 */
@Entity
@Table(name = "ASYNC_COMMAND_PARAM")
public class AsyncCommandParam extends BaseObject {

	private static final long serialVersionUID = -5054482590902198307L;

	@EmbeddedId public AsyncCommandParamPk id;
    
    @Column(length=30)
    @ColumnInfo(name="인자타입")
    private String paramType;
    
    @Column(length=500)
    @ColumnInfo(name="인자값")
    private String paramValue;
    
    @Column(name="TR_TYPE", length=30)
    @ColumnInfo(name="트렌젝션타입", descr="트렌젝션타입")
    private String trType;
    
    public AsyncCommandParam() {
        id = new AsyncCommandParamPk();
    }
    
    public void setTrId(Long trId) {
        id.setTrId(trId);
    }
    
    public Long getTrId() {
        return id.getTrId();
    }
    
    public void setMcuId(String mcuId) {
        id.setMcuId(mcuId);
    }
    
    public String getMcuId() {
        return id.getMcuId();
    }
    
    public void setNum(Integer num) {
        id.setNum(num);
    }
    
    public Integer getNum() {
        return id.getNum();
    }
    
    public String getParamType() {
        return paramType;
    }

    public void setParamType(String paramType) {
        this.paramType = paramType;
    }

    public String getParamValue() {
        return paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }

    public String getTrType() {
		return trType;
	}

	public void setTrType(String trType) {
		this.trType = trType;
	}

	@Override
    public boolean equals(Object o) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int hashCode() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String toString() {
        return "AsyncCommandParam " + toJSONString();
    }

    public String toJSONString() {

        StringBuffer str = new StringBuffer();
        
        str.append("{"
            + "trid:'" + this.id.getTrId()
            + "', mcuId:'" + this.id.getMcuId()
            + "', num:'" + this.id.getNum()
            + "', paramType:'" + this.paramType
            + "', paramValue:'" + this.paramValue
            + "', trType:'" + this.trType
            + "'}");
        
        return str.toString();
    }
}
