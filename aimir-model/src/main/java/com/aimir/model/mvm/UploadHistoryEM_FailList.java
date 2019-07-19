package com.aimir.model.mvm;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Index;

import com.aimir.annotation.ColumnInfo;
import com.aimir.model.BaseObject;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * <p>Metering Data Manual Upload History Fail List </p>
 * <p>Cooperate with Upload History</p>
 * @author SEJIN HAN
 *
 */
@Entity
@Table(name="UPLOADHISTORY_FAILLIST_EM")
@Index(name="UPLOADHISTORY_FAILLIST_EM_01", columnNames={"UPLOAD_ID"})
public class UploadHistoryEM_FailList extends BaseObject {
	
	private static final long serialVersionUID = -2702645582567160766L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="UploadHistory_FailList_SEQ")
	@SequenceGenerator(name="UploadHistory_FailList_SEQ", sequenceName="UploadHistory_FailList_SEQ", allocationSize=1)
	@ColumnInfo(name="PK", descr="PK")
	private Integer id;
	
	@JoinColumn(name="UPLOAD_ID")
	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	private UploadHistoryEM uploadId;
		
	@Column(name="ROW_LINE" , length=10, nullable=false)
    @ColumnInfo(name="행번호", descr="파일 내부의 INDEX 열값")    
    private Integer rowLine;
	
	@Column(name="FAIL_REASON" , length=255)
    @ColumnInfo(name="업데이트실패사유", descr="중복,포맷에러 등")    
    private String failReason;
	
	@Column(name="DATA_TYPE" , length=10)
	@ColumnInfo(name="데이터타입", descr="1.Load 2.Daily 3.Month")    
    private Integer dataType;
	
	@Column(name="METERING_TIME" , length=14)
    @ColumnInfo(name="검침시간", descr="YYYYMMDDHHMMSS")    
    private String meteringTime;
	
	@Column(name="MD_VALUE" , length=2048)
    @ColumnInfo(name="검침값", descr="기호로 구분되는 묶음값")    
    private String mdValue;
	
	@Column(name="EXTRA_VALUE_1" , length=2048)
    @ColumnInfo(name="예비1", descr="예비공간1")    
    private String extraValue1;
	
	@Column(name="EXTRA_VALUE_2" , length=2048)
    @ColumnInfo(name="예비2", descr="예비공간2")    
    private String extraValue2;
	
	public Integer getId(){
		return id;
	}
	
	public void setId(Integer _id){
		this.id = _id;
	}
	
	public UploadHistoryEM getUploadId(){
		return uploadId;
	}
	
	public void setUploadId(UploadHistoryEM _uploadId){
		this.uploadId = _uploadId;
	}
	
	public Integer getRowLine(){
		return rowLine;
	}
	
	public void setRowLine(Integer _rowLine){
		this.rowLine = _rowLine;
	}
	
	public String getFailReason(){
		return failReason;
	}
	
	public void setFailReason(String _failReason){
		this.failReason = _failReason;
	}
	
	public Integer getDataType(){
		return dataType;
	}
	
	public void setDataType(Integer _dataType){
		this.dataType = _dataType;
	}
	
	public String getMeteringTime(){
		return meteringTime;
	}
	
	public void setMeteringTime(String _meteringTime){
		this.meteringTime = _meteringTime;
	}
	
	public String getMdValue(){
		return mdValue;
	}
	
	public void setMdValue(String _mdValue){
		this.mdValue = _mdValue;
	}
	
	public String getExtraValue1(){
		return extraValue1;
	}
	
	public void setExtraValue1(String _extraValue1){
		this.extraValue1 = _extraValue1;
	}
	
	public String getExtraValue2(){
		return extraValue2;
	}
	
	public void setExtraValue2(String _extraValue2){
		this.extraValue2 = _extraValue2;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
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
	
	
}
