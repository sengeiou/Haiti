package com.aimir.model.device;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.xml.bind.annotation.XmlTransient;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.ReferencedBy;
import com.aimir.annotation.Scope;
import com.aimir.constants.CommonConstants.FW_STATE;
import com.aimir.constants.CommonConstants.GroupType;
import com.aimir.constants.CommonConstants.LimitType;
import com.aimir.constants.CommonConstants.PeakType;
import com.aimir.constants.CommonConstants.ScheduleType;
import com.aimir.constants.CommonConstants.WeekDay;
import com.aimir.model.BaseObject;
import com.aimir.model.system.Supplier;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * <p>Load Control Schedule</p>
 * ACD에 연결된 장치의 부하 공급의 Limit을 설정<br>
 * 
 * @author YeonKyoung Park(goodjob)
 */
@Entity
public class LoadLimitSchedule extends BaseObject {

	private static final long serialVersionUID = -3726084692444191291L;

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="LOADLIMITSCHEDULE_SEQ")
	@SequenceGenerator(name="LOADLIMITSCHEDULE_SEQ", sequenceName="LOADLIMITSCHEDULE_SEQ", allocationSize=1) 
    @ColumnInfo(name="PK", descr="PK")
    private Integer id;

	@Column(name="target_type", nullable=false)
    @ColumnInfo(name="장비유형 Location,Operator,Contract,MCU,Modem,Meter,EndDevice")
    @Enumerated(EnumType.STRING)
    private GroupType targetType;
	
    @Column(name="target_id", nullable=false)
    @ColumnInfo(name="타겟 아이디(미터,그룹,지역 등이 됨), 비지니스 키가됨")
    private String target;
    
    @Column(name="SCHEDULE_TYPE", nullable=false)
    @ColumnInfo(descr="0:즉시, 1: 일자,  2:요일별")
    private ScheduleType scheduleType;
    
	@Column(name="limit_type")
	@ColumnInfo(descr="0:전력 1:전류")
    private LimitType limitType;
    
	@Column(name="power_limit")
	@ColumnInfo(descr="전력 또는 전류 임계치")
    private Double limit;
	
	@Column(name="interval_type")
	@ColumnInfo(descr="전력 또는 전류 평균을 구하기 위한 구간 스케줄 시작시간, 종료시간을 별도 지정하지 않고 피크타입으로 대체할 수 있다.")
    private PeakType peakType;
	
	@Column(name="create_time", length=14)
	@ColumnInfo(name="스케줄 생성 시간", descr="YYYYMMDDHHMMSS")
	private String createTime;
	
	@Column(name="start_time", length=14)
	@ColumnInfo(name="스케줄 시작 시간", descr="YYYYMMDDHHMMSS")
	private String startTime;	
	
	@Column(name="end_time", length=14)
	@ColumnInfo(name="스케줄 종료 시간", descr="YYYYMMDDHHMMSS")
	private String endTime;
	
	@Column(name="open_period")
	@ColumnInfo(descr="1~24hh restorationTime ")
	private Integer openPeriod;

	@Column(name="week_day")
    @ColumnInfo(name="0:일~6:토")
	private WeekDay weekDay;	
	
	@Column(name="status")
	@ColumnInfo(name="0:set fail, 1:set success")
	private FW_STATE status;
	
    @ColumnInfo(name="공급사아이디", view=@Scope(create=true, read=true, update=true), descr="공급사 테이블의 ID 혹은  NULL")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="SUPPLIER_ID")
    @ReferencedBy(name="name" )
    private Supplier supplier;
     
    @Column(name="SUPPLIER_ID", nullable=true, updatable=false, insertable=false)
    private Integer supplierId;
    
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public GroupType getTargetType() {
		return targetType;
	}

	public void setTargetType(String targetType) {
		this.targetType = GroupType.valueOf(targetType);
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public ScheduleType getScheduleType() {
		return scheduleType;
	}

	public void setScheduleType(String scheduleType) {
		this.scheduleType = ScheduleType.valueOf(scheduleType);
	}
	
	public LimitType getLimitType() {
		return limitType;
	}

	public void setLimitType(String limitType) {
		this.limitType = LimitType.valueOf(limitType);
	}

	public Double getLimit() {
		return limit;
	}

	public void setLimit(Double limit) {
		this.limit = limit;
	}

	public PeakType getPeakType() {
		return peakType;
	}

	public void setPeakType(String peakType) {
		this.peakType = PeakType.valueOf(peakType);
	}

	public Integer getOpenPeriod() {
		return openPeriod;
	}

	public void setOpenPeriod(Integer openPeriod) {
		this.openPeriod = openPeriod;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public WeekDay getWeekDay() {
		return weekDay;
	}

	public void setWeekDay(String weekDay) {
		this.weekDay = WeekDay.valueOf(weekDay);
	}

	public FW_STATE getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = FW_STATE.valueOf(status);
	}	

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	@XmlTransient
	public Supplier getSupplier() {
		return supplier;
	}

	public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

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
