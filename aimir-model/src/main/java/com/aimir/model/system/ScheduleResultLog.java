package com.aimir.model.system;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import net.sf.json.JSONString;

import com.aimir.annotation.ColumnInfo;
import com.aimir.constants.CommonConstants.GroupType;
import com.aimir.constants.CommonConstants.OperatorType;
import com.aimir.constants.CommonConstants.ResultStatus;
import com.aimir.constants.CommonConstants.ResultType;
import com.aimir.constants.CommonConstants.TriggerType;
import com.aimir.model.BaseObject;

import org.eclipse.persistence.annotations.Index;
import org.eclipse.persistence.annotations.Indexes;

/**
 * 스케줄 결과 로그
 *  
 *  @author YeonKyoung Park(goodjob)
 */
@Entity
@Table(name="SCHEDULERESULTLOG")
@Indexes({
    @Index(name="IDX_SCHEDULERESULTLOG_01", columnNames={"JOB_NAME", "TRIGGER_NAME", "CREATE_TIME"})
})
public class ScheduleResultLog extends BaseObject implements JSONString{

	private static final long serialVersionUID = 8456161866692456444L;

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SCHEDULERESULTLOG_SEQ")
	@SequenceGenerator(name="SCHEDULERESULTLOG_SEQ", sequenceName="SCHEDULERESULTLOG_SEQ", allocationSize=1) 
	private Long id;

	@Column(name="CREATE_TIME", length=14, nullable=false)
	@ColumnInfo(descr="로그 생성 시간")
	private String createTime;
	
	@Column(name="RESPONSE_TIME", length=14)
	@ColumnInfo(descr="수행 시작 시간")
	private String responseTime;
	
	@Column(name="JOB_NAME", nullable=false)
	@ColumnInfo(descr="스케줄 잡 이름")
	private String jobName;
	
	@Column(name="TRIGGER_NAME", nullable=false)
	@ColumnInfo(descr="스케줄 트리거 이름")
	private String triggerName;
	
	@Column(name="OPERATOR_TYPE", nullable=false)
	@ColumnInfo(descr="오퍼레이터 타입       SYSTEM(0), OPERATOR(1), OFFLINE(2)")
	@Enumerated(EnumType.STRING)
	private OperatorType operatorType;
	
	@Column(name="OPERATOR", nullable=false)
	@ColumnInfo(descr="오퍼레이터 비지니스 키")
	private String operator;
	
	@Column(name="COMMAND_PARAMETER")
	@ColumnInfo(descr="스케줄  파라미터들")
	private String commandParameter;
	
	@Column(name="TARGET_TYPE")
	@ColumnInfo(descr="스케줄 대상 타입      Location,Operator,Contract,MCU,Modem,Meter,EndDevice")
	@Enumerated(EnumType.STRING)
	private GroupType targetType;
	
	@Column(name="TARGET")
	@ColumnInfo(descr="스케줄 대상 ")
	private String target;
	
	@Column(name="TRIGGER_TYPE", nullable=false)
	@ColumnInfo(descr="스케줄 트리거 타입 Cron(0), 	Simple(1), 	Unknown(99")
	@Enumerated(EnumType.STRING)
	private TriggerType triggerType;
	
	@Column(name="START_DATE", length=8)
	@ColumnInfo(descr="수행 시작날짜")
	private String startDate;
	
	@Column(name="END_DATE", length=8)
	@ColumnInfo(descr="수행 종료 날짜")
	private String endDate;	
	
	@Column(name="CRON_EXP")
	@ColumnInfo(descr="스케줄 표현 양식")
	private String cronExp;
	
	@Column(name="REPEAT_COUNT")
	@ColumnInfo(descr="재시도 횟수")
	private Integer repeatCount;
	
	@Column(name="REPEAT_INTERVAL")
	@ColumnInfo(descr="재시도 간격")
	private Long repeatInterval;
	
	@Column(name="NEXT_FIRED",length=14)
	@ColumnInfo(descr="다음 스케줄 수행예정시간")
	private String nextFired;
	
	@Column(name="RESULT_TYPE")
	@ColumnInfo(descr="결과 타입 Text,File,Email,FTP")
	@Enumerated(EnumType.STRING)
	private ResultType resultType;
	
	@Column(name="RESULT_TARGET")
	@ColumnInfo(descr="결과 경로")
	private String resultTarget;
	
	@Column(name="RESULT")
	@ColumnInfo(descr="결과")
	@Enumerated(EnumType.STRING)
	private ResultStatus result;	//
	
	@Column(name="ERROR_MESSAGE")
	@ColumnInfo(descr="에러 일때 메시지")
	private String errorMessage;		//비고
    
    
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getResponseTime() {
		return responseTime;
	}
	public void setResponseTime(String responseTime) {
		this.responseTime = responseTime;
	}
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	public String getTriggerName() {
		return triggerName;
	}
	public void setTriggerName(String triggerName) {
		this.triggerName = triggerName;
	}
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	public String getCommandParameter() {
		return commandParameter;
	}
	public void setCommandParameter(String commandParameter) {
		this.commandParameter = commandParameter;
	}
	public GroupType getTargetType() {
		return targetType;
	}
	public void setTargetType(String targetType) {
	    if (targetType != null) {
	        this.targetType = GroupType.valueOf(targetType);
	    } else {
	        this.targetType = null;
	    }
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
	public TriggerType getTriggerType() {
		return triggerType;
	}
	public void setTriggerType(String triggerType) {
		this.triggerType = TriggerType.valueOf(triggerType);
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getCronExp() {
		return cronExp;
	}
	public void setCronExp(String cronExp) {
		this.cronExp = cronExp;
	}
	public Integer getRepeatCount() {
		return repeatCount;
	}
	public void setRepeatCount(Integer repeatCount) {
		this.repeatCount = repeatCount;
	}
	public Long getRepeatInterval() {
		return repeatInterval;
	}
	public void setRepeatInterval(Long repeatInterval) {
		this.repeatInterval = repeatInterval;
	}
	public String getNextFired() {
		return nextFired;
	}
	public void setNextFired(String nextFired) {
		this.nextFired = nextFired;
	}
	public ResultType getResultType() {
		return resultType;
	}
	public void setResultType(String resultType) {
		this.resultType = ResultType.valueOf(resultType);
	}
	public String getResultTarget() {
		return resultTarget;
	}
	public void setResultTarget(String resultTarget) {
		this.resultTarget = resultTarget;
	}
	public ResultStatus getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = ResultStatus.valueOf(result);
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public void setOperatorType(String operatorType) {
		this.operatorType = OperatorType.valueOf(operatorType);
	}
	public OperatorType getOperatorType() {
		return operatorType;
	}
	
	public String toString()
	{
	    return "ScheduleResultLog "+toJSONString();
	}
	
	public String toJSONString() {
		
		String retValue = "";
		
	    retValue = "{"
	        + "id:'" + this.id 
	        + "',createTime:'" + this.createTime 
	        + "',responseTime:'" + this.responseTime 
	        + "',jobName:'" + this.jobName 
	        + "',triggerName:'" + this.triggerName
	        + "',operatorType:'" + ((this.operatorType == null)? "null" : this.operatorType.name()) 
	        + "',operator:'" + this.operator
	        + "',commandParameter:'" + this.commandParameter  
	        + "',targetType:'" + ((this.targetType == null)? "null" : this.targetType.name()) 
		    + "',target:'" + this.target
		    + "',keyType:'" + ((this.triggerType == null)? "null" : this.triggerType.name())
		    + "',startDate:'" + this.startDate
		    + "',endDate:'" + this.endDate
		    + "',cronExp:'" + this.cronExp
		    + "',repeatCount:'" + this.repeatCount
		    + "',repeatInterval:'" + this.repeatInterval
		    + "',nextFired:'" + this.nextFired
		    + "',resultType:'" + ((this.resultType == null)? "null" : this.resultType.name())
		    + "',resultTarget:'" + this.resultTarget
		    + "',result:'" + this.result
		    + "',errorMessage:'" + ((this.errorMessage == null) ? "" : this.errorMessage)
	        + "'}";
	    
	    return retValue;
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
