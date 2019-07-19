package com.aimir.model.mvm;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Index;

import com.aimir.annotation.ColumnInfo;
import com.aimir.model.BaseObject;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * <p>Metering Data Manual Upload History</p>
 * <p>Cooperate with Upload History Fail List</p>
 * @author SEJIN HAN
 *
 */
@Entity
@Table(name="UPLOADHISTORY_EM")
@Index(name="UPLOADHISTORY_EM_01", columnNames={"MDS_ID", "UPLOAD_DATE", "DATA_TYPE"})
public class UploadHistoryEM extends BaseObject {

	private static final long serialVersionUID = 8917727240920498697L;
		
	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="UploadHistory_EM_SEQ")
    @SequenceGenerator(name="UploadHistory_EM_SEQ", sequenceName="UploadHistory_EM_SEQ", allocationSize=1)
	@ColumnInfo(descr="PK")
    private String id;    //  ID(PK)
	
	@Column(name="LOGIN_ID" , length=31, nullable=false)
    @ColumnInfo(name="로그인아이디", descr="업로드 세션의 접속 아이디")    
    private String loginId;
	
	@Column(name="MDS_ID" , length=31, nullable=false)
    @ColumnInfo(name="미터시리얼", descr="검침 데이터를 추출한 미터의 serial number")    
    private String mdsId;
	
	@Column(name="UPLOAD_DATE" , length=14, nullable=false)
    @ColumnInfo(name="업로드일자", descr="YYYYMMDDHHMMSS")    
    private String uploadDate;
	
	@Column(name="METER_REGISTRATION" , length=10)
    @ColumnInfo(name="미터등록여부", descr="1.있음 or 0.없음")    
    private String meterRegistration;
	
	@Column(name="DATA_TYPE" , length=10)
    @ColumnInfo(name="데이터타입", descr="1.Load 2.Daily 3.Month")    
    private Integer dataType;
	
	@Column(name="START_DATE" , length=14)
    @ColumnInfo(name="검침데이터시작일자", descr="YYYYMMDDHHMMSS")    
    private String startDate;
	
	@Column(name="END_DATE" , length=14)
    @ColumnInfo(name="검침데이터마지막일자", descr="YYYYMMDDHHMMSS")    
    private String endDate;
	
	@Column(name="TOTAL_CNT" , length=10)
    @ColumnInfo(name="파일의모든행수", descr="TOTAL")    
    private Integer totalCnt;
	
	@Column(name="SUCCESS_CNT" , length=10)
    @ColumnInfo(name="업데이트성공행수", descr="COUNT")    
    private Integer successCnt;
	
	@Column(name="FAIL_CNT" , length=10)
    @ColumnInfo(name="업데이트실패행수", descr="COUNT")    
    private Integer failCnt;
	
	@Column(name="FILE_PATH" , length=2048)
    @ColumnInfo(name="파일경로", descr="파일 저장 경로와 파일명")    
    private String filePath;
	
	@ColumnInfo(name="이력아이디", descr="각 이력아이디에 해당하는 실패Line List")
	@OneToMany(mappedBy="uploadId", cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	private List<UploadHistoryEM_FailList> failList = new ArrayList<UploadHistoryEM_FailList>(0);
	
	public String getId(){
		return id;
	}
	
	public void setId(String _id){
		this.id = _id;
	}
	
	public String getLoginId(){
		return loginId;
	}
	
	public void setLoginId(String _loginId){
		this.loginId = _loginId;
	}
	
	public String getMdsId(){
		return mdsId;
	}
	
	public void setMdsId(String _mdsId){
		this.mdsId = _mdsId;
	}
	
	public String getUploadDate(){
		return uploadDate;
	}
	
	public void setUploadDate(String _uploadDate){
		this.uploadDate = _uploadDate;
	}
	
	public String getMeterRegistration(){
		return meterRegistration;
	}
	
	public void setMeterRegistration(String _meterRegistration){
		this.meterRegistration = _meterRegistration;
	}
	
	public Integer getDataType(){
		return dataType;
	}
	
	public void setDataType(Integer _dataType){
		this.dataType = _dataType;
	}
	
	public String getStartDate(){
		return startDate;
	}
	
	public void setStartDate(String _startDate){
		this.startDate = _startDate;
	}
	
	public String getEndDate(){
		return endDate;
	}
	
	public void setEndDate(String _endDate){
		this.endDate = _endDate;
	}
	
	public Integer getTocalCnt(){
		return totalCnt;
	}
	
	public void setTotalCnt(Integer _totalCnt){
		this.totalCnt = _totalCnt;
	}
	
	public Integer getSuccessCnt(){
		return successCnt;
	}
	
	public void setSuccessCnt(Integer _successCnt){
		this.successCnt = _successCnt;
	}
	
	public Integer getFailCnt(){
		return failCnt;
	}
	
	public void setFailCnt(Integer _failCnt){
		this.failCnt = _failCnt;
	}
	
	public String getFilePath(){
		return filePath;
	}
	
	public void setFilePath(String _filePath){
		this.filePath = _filePath;
	}
	
	public List<UploadHistoryEM_FailList> getFailList(){
		return failList;
	}
	
	public void setFailList(List<UploadHistoryEM_FailList> _failList){
		this.failList = _failList;
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
