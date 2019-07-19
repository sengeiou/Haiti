package com.aimir.model.device;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.aimir.annotation.ColumnInfo;
import com.aimir.model.BaseObject;
import com.aimir.util.StringUtil;
/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * 비동기 명령어 결과
 * 
 * @author 박종성(elevas)
 */
@Entity
@Table(name = "ASYNC_COMMAND_RESULT")
public class AsyncCommandResult extends BaseObject {

	private static final long serialVersionUID = -8700026411751401051L;

	@EmbeddedId public AsyncCommandResultPk id;
    
    @Column(length=20)
    @ColumnInfo(name="OID")
    private String oid;
    
    @Column(length=16)
    @ColumnInfo(name="길이")
    private Long length;
    
    @Column(length=2000)
    @ColumnInfo(name="원데이터")
    private byte[] data;
    
    @Column(length=30)
    @ColumnInfo(name="인자타입")
    private String resultType;
    
    @Column(length=500)
    @ColumnInfo(name="인자값")
    private String resultValue;
    
    @Column(name="TR_TYPE", length=10)
    @ColumnInfo(name="트렌젝션타입", descr="트렌젝션타입")
    private String trType;
    
    public AsyncCommandResult() {
        id = new AsyncCommandResultPk();
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
    
    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public Long getLength() {
        return length;
    }

    public void setLength(Long length) {
        this.length = length;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public String getResultValue() {
        return resultValue;
    }

    public void setResultValue(String resultValue) {
        this.resultValue = resultValue;
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
        return "AsyncCommandResult : " + toJSONString();
    }

    public String toJSONString() {

        StringBuffer str = new StringBuffer();
        
        str.append("{"
            + "trid:'" + this.id.getTrId()
            + "', mcuId:'" + this.id.getMcuId()
            + "', num:'" + this.id.getNum()
            + "', oid:'" + this.oid
            + "', length:'" + this.length
            + "', data:'" + StringUtil.getHexDump(this.data)
            + "', resultType:'" + this.resultType
            + "', resultValue:'" + this.resultValue
            + "', trType:'" + this.trType
            + "'}");
        
        return str.toString();
    }
}
