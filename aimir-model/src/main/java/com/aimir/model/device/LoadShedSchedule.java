package com.aimir.model.device;

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
import javax.xml.bind.annotation.XmlTransient;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.ReferencedBy;
import com.aimir.annotation.Scope;
import com.aimir.constants.CommonConstants.FW_STATE;
import com.aimir.constants.CommonConstants.OnOffType;
import com.aimir.constants.CommonConstants.ScheduleType;
import com.aimir.constants.CommonConstants.WeekDay;
import com.aimir.model.BaseObject;
import com.aimir.model.system.Supplier;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * Load Shed Schedule<br>
 * Demand Response 허용된 고객 그룹 대상으로 Load Shed을 수행하는 스케줄<br>
 * 
 * @author YeonKyoung Park(goodjob)
 */
@Entity
@Table(name="LOAD_SHED_SCHEDULE")
public class LoadShedSchedule extends BaseObject {

	private static final long serialVersionUID = 5064442695778529580L;

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="LOADSHEDSCHEDULE_SEQ")
	@SequenceGenerator(name="LOADSHEDSCHEDULE_SEQ", sequenceName="LOADSHEDSCHEDULE_SEQ", allocationSize=1) 
    @ColumnInfo(name="PK", descr="PK")
    private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="loadshedgroup_id")
    @ColumnInfo(name="그룹")
    @ReferencedBy(name="name")
    private LoadShedGroup target;
	
	@Column(name="loadshedgroup_id", nullable=true, updatable=false, insertable=false)
	private Integer loadShedGroupId;
    
    @Column(name="SCHEDULE_TYPE", nullable=false)
    @ColumnInfo(descr="0:즉시, 1: 일자,  2:요일별")
    private ScheduleType scheduleType;
    
    @Column(name="ON_OFF", nullable=false)
    @ColumnInfo(descr="On/Off Type")
    private OnOffType onOff;
	
	@Column(name="create_time", length=14, nullable=false)
	@ColumnInfo(name="스케줄 생성 시간", descr="YYYYMMDDHHMMSS")
	private String createTime;
	
	@Column(name="start_time", length=14, nullable=false)
	@ColumnInfo(name="스케줄 시작 시간", descr="YYYYMMDDHHMMSS")
	private String startTime;	
	
	@Column(name="end_time", length=14, nullable=false)
	@ColumnInfo(name="스케줄 종료 시간", descr="YYYYMMDDHHMMSS")
	private String endTime;

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

	@XmlTransient
	public LoadShedGroup getTarget() {
		return target;
	}

	public void setTarget(LoadShedGroup target) {
		this.target = target;
	}

	public ScheduleType getScheduleType() {
		return scheduleType;
	}

	public void setScheduleType(String scheduleType) {
		this.scheduleType = ScheduleType.valueOf(scheduleType);
	}

	public OnOffType getOnOff() {
		return onOff;
	}

	public void setOnOff(String onOff) {
		this.onOff = OnOffType.valueOf(onOff);
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

	@XmlTransient
	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public Integer getLoadShedGroupId() {
        return loadShedGroupId;
    }

    public void setLoadShedGroupId(Integer loadShedGroupId) {
        this.loadShedGroupId = loadShedGroupId;
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
