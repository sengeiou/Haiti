package com.aimir.model.device;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.Scope;
import com.aimir.audit.IAuditable;
import com.aimir.model.BaseObject;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * <pre>
 * 	집중기에 구조체 형태로 저장되어 있는 정보이며 
 * 	대부분의 필드들이 집중기에 설정하는 정보이다.
 * 	환경변수 
 * </pre>
 */
@Entity
public class MCUVar extends BaseObject implements IAuditable {

	private static final long serialVersionUID = -9167601768841438376L;
	
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="MCU_VAR_SEQ")
    @SequenceGenerator(name="MCU_VAR_SEQ", sequenceName="MCU_VAR_SEQ", allocationSize=1)
	private Long id;    
    
	private Integer varEnableMask;
	@ColumnInfo(view=@Scope(read=true, update=true, devicecontrol=true),descr="실패 검침 할것인지에 대한 값 enable  : true")
	private Boolean varEnableRecovery;
	private Boolean varEnableCmdHistLog;
	private Boolean varEnableCommLog;
	private Boolean varEnableAutoRegister;
	@ColumnInfo(view=@Scope(read=true, update=true, devicecontrol=true),descr="미터 시간 자동 동기화 할것인지에대한  값 enable  : true")
	private Boolean varEnableAutoTimesync;
	private Boolean varEnableAutoSinkReset;
	private Boolean varEnableMobileStaticLog;
	@ColumnInfo(view=@Scope(read=true, update=true, devicecontrol=true),descr="검침데이터 자동 업로드 할것인지에  대한 값 enable  : true")
	private Boolean varEnableAutoUpload;
	private Boolean varEnableSecurity;
	private Boolean varEnableMonitoring;
	private Boolean varEnableKeepAlive;
	private Boolean varEnableGpsTimesync;
	private Boolean varEnableTimeBroadcast;
	private Boolean varEnableMemoryCheck;
	@ColumnInfo(view=@Scope(read=true, update=true, devicecontrol=true),descr="미터 시간을 설정할 것인지 대한 값 enable  : true")
	private Boolean varEnableMeterTimesync;
	private Boolean varEnableMeterCheck;
	private Boolean varEnableHeater;
	private Boolean varEnableReadMeterEvent;
	private Boolean varEnableDebugLog;
	private Boolean varEnableMeterErroeRecovery;
	private Boolean varEnableHighRam;
	private Boolean varEnableGarbageCleaning;
	private Integer varValueMask;
	private Integer varAutoResetCheckIntegererval;
	private Integer varSysPowerOffDelay;
	private Integer varSysTempMonIntegererval;
	private Integer varAutoTimesyncIntegereval;
	private Integer varSysMeteringThreadCount;	
	private Integer varSinkPollingIntegererval;
	private Integer varSinkResetIntegererval;
	private Integer varSinkResetDelay;
	private Integer varSinkLedTurnOffIntegererval;
	private Integer varSinkAckWaitTime;
	private Integer varSensorKeepAlive;
	private Integer varSensorMeterSaveCount;
	
	@ColumnInfo(view=@Scope(read=true, update=true, devicecontrol=true),descr="Metering Schedule Day (Mask 32 bit) MSB->LSB 검침 일자의 스케줄 장비에 명령이 내려감")
	private Integer varMeterDayMask;
	@ColumnInfo(view=@Scope(read=true, update=true, devicecontrol=true),descr="Metering Schedule Hour (Mask 24 bit) MSB->LSB 검침 일자의 스케줄 장비에 명령이 내려감")
	private Integer varMeterHourMask;
	
	@ColumnInfo(view=@Scope(read=true, update=true, devicecontrol=true),descr="검침 시작 시간")
	private Integer varMeterStartMin;
	
	@ColumnInfo(view=@Scope(read=true, update=true, devicecontrol=true),descr="실패 검침 일자 스케줄 (Mask 32 bit) MSB->LSB 장비에 명령이 내려감")
	private Integer varRecoveryDayMask;
	
	@ColumnInfo(view=@Scope(read=true, update=true, devicecontrol=true),descr="실패 검침 시간 스케줄 (Mask 24 bit) MSB->LSB 장비에 명령이 내려감")
	private Integer varRecoveryHourMask;
	
	@ColumnInfo(view=@Scope(read=true, update=true, devicecontrol=true),descr="실패 검침 시작 시간")
	private Integer varRecoveryStartMin;
	private Integer varCmdHistSaveDay;
	private Integer varEventLogSaveDay;
	private Integer varCommLogSAveDay;
	
	@ColumnInfo(view=@Scope(read=true, update=true, devicecontrol=true),descr="자동 리셋 시간 cron expression")
	private String varAutoResetTime;
	
	@ColumnInfo(view=@Scope(read=true, update=true, devicecontrol=true),descr="겁침데이터 업로드 시간 cron expression")
	private String varUploadTime;	
	
	@ColumnInfo(view=@Scope(read=true, update=true, devicecontrol=true),descr="업로드 타입(0: unknown 1: 즉시, 2: Daily 3: Weekly 4: hourly ")
	private Integer varMeterUploadCycleType;
	
	@ColumnInfo(view=@Scope(read=true, update=true, devicecontrol=true),descr="업로드 주기 , 시간별 일별 타입에 따라 비트수가 달라짐")
	private Integer varMeterUploadCycle;
	
	@ColumnInfo(view=@Scope(read=true, update=true, devicecontrol=true),descr="업로드 시작 시간")
	private Integer varMeterUploadStartHour;
	
	@ColumnInfo(view=@Scope(read=true, update=true, devicecontrol=true),descr="업로드 시작 시간")
	private Integer varMeterUploadStartMin;
	
	@ColumnInfo(view=@Scope(read=true, update=true, devicecontrol=true),descr="업로드 서버 전송시간")
	private Integer varMeterUploadTryTime;
	
	
	@ColumnInfo(view=@Scope(read=true, update=true, devicecontrol=true),descr="업로드 재시도 횟수")
	private Integer varMeterUploadRetry;
	
	private Integer varMeterIssueDate;
	private Integer varMemoryCriticalRate;
	private Integer varFlashCriticalRate;
	private Integer varNapcGroupIntegererval;
	private Boolean varNapcRetry;
	private Integer varNapcRetryHour;
	private Integer varNapcRetryStartSecond;
	private Integer varNapcRetruClear;
	private Integer varMaxEventLogSize;
	private Integer varMaxAlamLogSize;
	private Integer varMaxCmdLogSize;
	private Integer varMaxCommLogSize;
	private Integer varMaxMobileLogSize;
	private Integer varKeepAlaiveIntegererval;
	private Integer varAlamLogSaveDay;
	private Integer varTimeBoardcastIntegererval;
	private Integer varStatusMonitorTime;
	private Integer varDataSaveDay;
	private Integer varMeteringPeriod;
	private Integer varRecoveryPeriod;
	@ColumnInfo(view=@Scope(read=true, update=true, devicecontrol=true),descr="검침 재시도 횟수")
	private Integer varMeteringRetry;
	@ColumnInfo(view=@Scope(read=true, update=true, devicecontrol=true),descr="실패검침 재시도 횟수")
	private Integer varRecoveryRetry;
	private Integer varCheckDayMask;
	private Integer varCheckHourMask;
	private Integer varCheckStartMin;
	private Integer varCheckPeriod;
	private Integer varCheckRetry;
	
	@ColumnInfo(view=@Scope(read=true, update=true, devicecontrol=true),descr="미터시간 동기화 스케줄 (Mask 32 bit) MSB->LSB 장비에 명령이 내려감")
	private Integer varMeterTimesyncDayMask;
	@ColumnInfo(view=@Scope(read=true, update=true, devicecontrol=true),descr="미터 시간 동기화 스케줄 (Mask 24 bit) MSB->LSB 장비에 명령이 내려감")
	private Integer varMeterTimesyncHourMask;
	
	private Integer varHeterOnTemp;
	private Integer varHeterOffTemp;
	private Integer varMobileLiveCheckIntegererval;
	
	@ColumnInfo(view=@Scope(read=true, update=true, devicecontrol=true),descr="이벤트 로그 읽기  스케줄 (Mask 32 bit) MSB->LSB 장비에 명령이 내려감")
	private Integer varEventReadDayMask;
	@ColumnInfo(view=@Scope(read=true, update=true, devicecontrol=true),descr="이벤트 로그 읽기  스케줄 (Mask 24 bit) MSB->LSB 장비에 명령이 내려감")
	private Integer varEventReadHourMask;
	private Integer varSendDelay;
	private Integer varEventAlertLevel;
	private Integer varSensorLimit;
	private Integer varMeterStrategy;
	private Integer varTimesyncThreshold;
	private Integer varMobileLiveCheckMEthod;
	private Integer varScanningStrategy;
	private Integer varMobileLogSaveDay;
	private Integer varUpgradeLogSaveDay;
	private Integer varUploadLogSaveDay;
	private Integer varTimesyncLogSaveDay;
	private Integer varMaxMeterLogSize;
	private Integer varMaxUpgradeLogSize;
	private Integer varMaxUploadLogSize;
	private Integer varTimesyncLogSize;
	private Integer varDefaultGeMeteringOPtion;
	private Integer varSensorCleaningThreshold;
	private Integer varTimeSyncStrategy;
	private Integer varTransactionSaveDay;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Integer getVarEnableMask() {
		return varEnableMask;
	}
	public void setVarEnableMask(Integer varEnableMask) {
		this.varEnableMask = varEnableMask;
	}
	public Boolean getVarEnableRecovery() {
		return varEnableRecovery;
	}
	public void setVarEnableRecovery(Boolean varEnableRecovery) {
		this.varEnableRecovery = varEnableRecovery;
	}
	public Boolean getVarEnableCmdHistLog() {
		return varEnableCmdHistLog;
	}
	public void setVarEnableCmdHistLog(Boolean varEnableCmdHistLog) {
		this.varEnableCmdHistLog = varEnableCmdHistLog;
	}
	public Boolean getVarEnableCommLog() {
		return varEnableCommLog;
	}
	public void setVarEnableCommLog(Boolean varEnableCommLog) {
		this.varEnableCommLog = varEnableCommLog;
	}
	public Boolean getVarEnableAutoRegister() {
		return varEnableAutoRegister;
	}
	public void setVarEnableAutoRegister(Boolean varEnableAutoRegister) {
		this.varEnableAutoRegister = varEnableAutoRegister;
	}
	public Boolean getVarEnableAutoTimesync() {
		return varEnableAutoTimesync;
	}
	public void setVarEnableAutoTimesync(Boolean varEnableAutoTimesync) {
		this.varEnableAutoTimesync = varEnableAutoTimesync;
	}
	public Boolean getVarEnableAutoSinkReset() {
		return varEnableAutoSinkReset;
	}
	public void setVarEnableAutoSinkReset(Boolean varEnableAutoSinkReset) {
		this.varEnableAutoSinkReset = varEnableAutoSinkReset;
	}
	public Boolean getVarEnableMobileStaticLog() {
		return varEnableMobileStaticLog;
	}
	public void setVarEnableMobileStaticLog(Boolean varEnableMobileStaticLog) {
		this.varEnableMobileStaticLog = varEnableMobileStaticLog;
	}
	public Boolean getVarEnableAutoUpload() {
		return varEnableAutoUpload;
	}
	public void setVarEnableAutoUpload(Boolean varEnableAutoUpload) {
		this.varEnableAutoUpload = varEnableAutoUpload;
	}
	public Boolean getVarEnableSecurity() {
		return varEnableSecurity;
	}
	public void setVarEnableSecurity(Boolean varEnableSecurity) {
		this.varEnableSecurity = varEnableSecurity;
	}
	public Boolean getVarEnableMonitoring() {
		return varEnableMonitoring;
	}
	public void setVarEnableMonitoring(Boolean varEnableMonitoring) {
		this.varEnableMonitoring = varEnableMonitoring;
	}
	public Boolean getVarEnableKeepAlive() {
		return varEnableKeepAlive;
	}
	public void setVarEnableKeepAlive(Boolean varEnableKeepAlive) {
		this.varEnableKeepAlive = varEnableKeepAlive;
	}
	public Boolean getVarEnableGpsTimesync() {
		return varEnableGpsTimesync;
	}
	public void setVarEnableGpsTimesync(Boolean varEnableGpsTimesync) {
		this.varEnableGpsTimesync = varEnableGpsTimesync;
	}
	public Boolean getVarEnableTimeBroadcast() {
		return varEnableTimeBroadcast;
	}
	public void setVarEnableTimeBroadcast(Boolean varEnableTimeBroadcast) {
		this.varEnableTimeBroadcast = varEnableTimeBroadcast;
	}
	public Boolean getVarEnableMemoryCheck() {
		return varEnableMemoryCheck;
	}
	public void setVarEnableMemoryCheck(Boolean varEnableMemoryCheck) {
		this.varEnableMemoryCheck = varEnableMemoryCheck;
	}
	public Boolean getVarEnableMeterTimesync() {
		return varEnableMeterTimesync;
	}
	public void setVarEnableMeterTimesync(Boolean varEnableMeterTimesync) {
		this.varEnableMeterTimesync = varEnableMeterTimesync;
	}
	public Boolean getVarEnableMeterCheck() {
		return varEnableMeterCheck;
	}
	public void setVarEnableMeterCheck(Boolean varEnableMeterCheck) {
		this.varEnableMeterCheck = varEnableMeterCheck;
	}
	public Boolean getVarEnableHeater() {
		return varEnableHeater;
	}
	public void setVarEnableHeater(Boolean varEnableHeater) {
		this.varEnableHeater = varEnableHeater;
	}
	public Boolean getVarEnableReadMeterEvent() {
		return varEnableReadMeterEvent;
	}
	public void setVarEnableReadMeterEvent(Boolean varEnableReadMeterEvent) {
		this.varEnableReadMeterEvent = varEnableReadMeterEvent;
	}
	public Boolean getVarEnableDebugLog() {
		return varEnableDebugLog;
	}
	public void setVarEnableDebugLog(Boolean varEnableDebugLog) {
		this.varEnableDebugLog = varEnableDebugLog;
	}
	public Boolean getVarEnableMeterErroeRecovery() {
		return varEnableMeterErroeRecovery;
	}
	public void setVarEnableMeterErroeRecovery(Boolean varEnableMeterErroeRecovery) {
		this.varEnableMeterErroeRecovery = varEnableMeterErroeRecovery;
	}
	public Boolean getVarEnableHighRam() {
		return varEnableHighRam;
	}
	public void setVarEnableHighRam(Boolean varEnableHighRam) {
		this.varEnableHighRam = varEnableHighRam;
	}
	public Boolean getVarEnableGarbageCleaning() {
		return varEnableGarbageCleaning;
	}
	public void setVarEnableGarbageCleaning(Boolean varEnableGarbageCleaning) {
		this.varEnableGarbageCleaning = varEnableGarbageCleaning;
	}
	public Integer getVarValueMask() {
		return varValueMask;
	}
	public void setVarValueMask(Integer varValueMask) {
		this.varValueMask = varValueMask;
	}
	public Integer getVarAutoResetCheckIntegererval() {
		return varAutoResetCheckIntegererval;
	}
	public void setVarAutoResetCheckIntegererval(Integer varAutoResetCheckIntegererval) {
		this.varAutoResetCheckIntegererval = varAutoResetCheckIntegererval;
	}
	public Integer getVarSysPowerOffDelay() {
		return varSysPowerOffDelay;
	}
	public void setVarSysPowerOffDelay(Integer varSysPowerOffDelay) {
		this.varSysPowerOffDelay = varSysPowerOffDelay;
	}
	public Integer getVarSysTempMonIntegererval() {
		return varSysTempMonIntegererval;
	}
	public void setVarSysTempMonIntegererval(Integer varSysTempMonIntegererval) {
		this.varSysTempMonIntegererval = varSysTempMonIntegererval;
	}
	public Integer getVarAutoTimesyncIntegereval() {
		return varAutoTimesyncIntegereval;
	}
	public void setVarAutoTimesyncIntegereval(Integer varAutoTimesyncIntegereval) {
		this.varAutoTimesyncIntegereval = varAutoTimesyncIntegereval;
	}
	public Integer getVarSysMeteringThreadCount() {
		return varSysMeteringThreadCount;
	}
	public void setVarSysMeteringThreadCount(Integer varSysMeteringThreadCount) {
		this.varSysMeteringThreadCount = varSysMeteringThreadCount;
	}
	public Integer getVarSinkPollingIntegererval() {
		return varSinkPollingIntegererval;
	}
	public void setVarSinkPollingIntegererval(Integer varSinkPollingIntegererval) {
		this.varSinkPollingIntegererval = varSinkPollingIntegererval;
	}
	public Integer getVarSinkResetIntegererval() {
		return varSinkResetIntegererval;
	}
	public void setVarSinkResetIntegererval(Integer varSinkResetIntegererval) {
		this.varSinkResetIntegererval = varSinkResetIntegererval;
	}
	public Integer getVarSinkResetDelay() {
		return varSinkResetDelay;
	}
	public void setVarSinkResetDelay(Integer varSinkResetDelay) {
		this.varSinkResetDelay = varSinkResetDelay;
	}
	public Integer getVarSinkLedTurnOffIntegererval() {
		return varSinkLedTurnOffIntegererval;
	}
	public void setVarSinkLedTurnOffIntegererval(Integer varSinkLedTurnOffIntegererval) {
		this.varSinkLedTurnOffIntegererval = varSinkLedTurnOffIntegererval;
	}
	public Integer getVarSinkAckWaitTime() {
		return varSinkAckWaitTime;
	}
	public void setVarSinkAckWaitTime(Integer varSinkAckWaitTime) {
		this.varSinkAckWaitTime = varSinkAckWaitTime;
	}
	public Integer getVarSensorKeepAlive() {
		return varSensorKeepAlive;
	}
	public void setVarSensorKeepAlive(Integer varSensorKeepAlive) {
		this.varSensorKeepAlive = varSensorKeepAlive;
	}
	public Integer getVarSensorMeterSaveCount() {
		return varSensorMeterSaveCount;
	}
	public void setVarSensorMeterSaveCount(Integer varSensorMeterSaveCount) {
		this.varSensorMeterSaveCount = varSensorMeterSaveCount;
	}
	public Integer getVarMeterDayMask() {
		return varMeterDayMask;
	}
	public void setVarMeterDayMask(Integer varMeterDayMask) {
		this.varMeterDayMask = varMeterDayMask;
	}
	public Integer getVarMeterHourMask() {
		return varMeterHourMask;
	}
	public void setVarMeterHourMask(Integer varMeterHourMask) {
		this.varMeterHourMask = varMeterHourMask;
	}
	public Integer getVarMeterStartMin() {
		return varMeterStartMin;
	}
	public void setVarMeterStartMin(Integer varMeterStartMin) {
		this.varMeterStartMin = varMeterStartMin;
	}
	public Integer getVarRecoveryDayMask() {
		return varRecoveryDayMask;
	}
	public void setVarRecoveryDayMask(Integer varRecoveryDayMask) {
		this.varRecoveryDayMask = varRecoveryDayMask;
	}
	public Integer getVarRecoveryHourMask() {
		return varRecoveryHourMask;
	}
	public void setVarRecoveryHourMask(Integer varRecoveryHourMask) {
		this.varRecoveryHourMask = varRecoveryHourMask;
	}
	public Integer getVarRecoveryStartMin() {
		return varRecoveryStartMin;
	}
	public void setVarRecoveryStartMin(Integer varRecoveryStartMin) {
		this.varRecoveryStartMin = varRecoveryStartMin;
	}
	public Integer getVarCmdHistSaveDay() {
		return varCmdHistSaveDay;
	}
	public void setVarCmdHistSaveDay(Integer varCmdHistSaveDay) {
		this.varCmdHistSaveDay = varCmdHistSaveDay;
	}
	public Integer getVarEventLogSaveDay() {
		return varEventLogSaveDay;
	}
	public void setVarEventLogSaveDay(Integer varEventLogSaveDay) {
		this.varEventLogSaveDay = varEventLogSaveDay;
	}
	public Integer getVarCommLogSAveDay() {
		return varCommLogSAveDay;
	}
	public void setVarCommLogSAveDay(Integer varCommLogSAveDay) {
		this.varCommLogSAveDay = varCommLogSAveDay;
	}
	public String getVarAutoResetTime() {
		return varAutoResetTime;
	}
	public void setVarAutoResetTime(String varAutoResetTime) {
		this.varAutoResetTime = varAutoResetTime;
	}
	public Integer getVarMeterUploadCycleType() {
		return varMeterUploadCycleType;
	}
	public void setVarMeterUploadCycleType(Integer varMeterUploadCycleType) {
		this.varMeterUploadCycleType = varMeterUploadCycleType;
	}
	public Integer getVarMeterUploadCycle() {
		return varMeterUploadCycle;
	}
	public void setVarMeterUploadCycle(Integer varMeterUploadCycle) {
		this.varMeterUploadCycle = varMeterUploadCycle;
	}
	public Integer getVarMeterUploadStartHour() {
		return varMeterUploadStartHour;
	}
	public void setVarMeterUploadStartHour(Integer varMeterUploadStartHour) {
		this.varMeterUploadStartHour = varMeterUploadStartHour;
	}
	public Integer getVarMeterUploadRetry() {
		return varMeterUploadRetry;
	}
	public void setVarMeterUploadRetry(Integer varMeterUploadRetry) {
		this.varMeterUploadRetry = varMeterUploadRetry;
	}
	public Integer getVarMeterIssueDate() {
		return varMeterIssueDate;
	}
	public void setVarMeterIssueDate(Integer varMeterIssueDate) {
		this.varMeterIssueDate = varMeterIssueDate;
	}
	public Integer getVarMemoryCriticalRate() {
		return varMemoryCriticalRate;
	}
	public void setVarMemoryCriticalRate(Integer varMemoryCriticalRate) {
		this.varMemoryCriticalRate = varMemoryCriticalRate;
	}
	public Integer getVarFlashCriticalRate() {
		return varFlashCriticalRate;
	}
	public void setVarFlashCriticalRate(Integer varFlashCriticalRate) {
		this.varFlashCriticalRate = varFlashCriticalRate;
	}
	public Integer getVarNapcGroupIntegererval() {
		return varNapcGroupIntegererval;
	}
	public void setVarNapcGroupIntegererval(Integer varNapcGroupIntegererval) {
		this.varNapcGroupIntegererval = varNapcGroupIntegererval;
	}
	public Boolean getVarNapcRetry() {
		return varNapcRetry;
	}
	public void setVarNapcRetry(Boolean varNapcRetry) {
		this.varNapcRetry = varNapcRetry;
	}
	public Integer getVarNapcRetryHour() {
		return varNapcRetryHour;
	}
	public void setVarNapcRetryHour(Integer varNapcRetryHour) {
		this.varNapcRetryHour = varNapcRetryHour;
	}
	public Integer getVarNapcRetryStartSecond() {
		return varNapcRetryStartSecond;
	}
	public void setVarNapcRetryStartSecond(Integer varNapcRetryStartSecond) {
		this.varNapcRetryStartSecond = varNapcRetryStartSecond;
	}
	public Integer getVarNapcRetruClear() {
		return varNapcRetruClear;
	}
	public void setVarNapcRetruClear(Integer varNapcRetruClear) {
		this.varNapcRetruClear = varNapcRetruClear;
	}
	public Integer getVarMaxEventLogSize() {
		return varMaxEventLogSize;
	}
	public void setVarMaxEventLogSize(Integer varMaxEventLogSize) {
		this.varMaxEventLogSize = varMaxEventLogSize;
	}
	public Integer getVarMaxAlamLogSize() {
		return varMaxAlamLogSize;
	}
	public void setVarMaxAlamLogSize(Integer varMaxAlamLogSize) {
		this.varMaxAlamLogSize = varMaxAlamLogSize;
	}
	public Integer getVarMaxCmdLogSize() {
		return varMaxCmdLogSize;
	}
	public void setVarMaxCmdLogSize(Integer varMaxCmdLogSize) {
		this.varMaxCmdLogSize = varMaxCmdLogSize;
	}
	public Integer getVarMaxCommLogSize() {
		return varMaxCommLogSize;
	}
	public void setVarMaxCommLogSize(Integer varMaxCommLogSize) {
		this.varMaxCommLogSize = varMaxCommLogSize;
	}
	public Integer getVarMaxMobileLogSize() {
		return varMaxMobileLogSize;
	}
	public void setVarMaxMobileLogSize(Integer varMaxMobileLogSize) {
		this.varMaxMobileLogSize = varMaxMobileLogSize;
	}
	public Integer getVarKeepAlaiveIntegererval() {
		return varKeepAlaiveIntegererval;
	}
	public void setVarKeepAlaiveIntegererval(Integer varKeepAlaiveIntegererval) {
		this.varKeepAlaiveIntegererval = varKeepAlaiveIntegererval;
	}
	public Integer getVarAlamLogSaveDay() {
		return varAlamLogSaveDay;
	}
	public void setVarAlamLogSaveDay(Integer varAlamLogSaveDay) {
		this.varAlamLogSaveDay = varAlamLogSaveDay;
	}
	public Integer getVarTimeBoardcastIntegererval() {
		return varTimeBoardcastIntegererval;
	}
	public void setVarTimeBoardcastIntegererval(Integer varTimeBoardcastIntegererval) {
		this.varTimeBoardcastIntegererval = varTimeBoardcastIntegererval;
	}
	public Integer getVarStatusMonitorTime() {
		return varStatusMonitorTime;
	}
	public void setVarStatusMonitorTime(Integer varStatusMonitorTime) {
		this.varStatusMonitorTime = varStatusMonitorTime;
	}
	public Integer getVarDataSaveDay() {
		return varDataSaveDay;
	}
	public void setVarDataSaveDay(Integer varDataSaveDay) {
		this.varDataSaveDay = varDataSaveDay;
	}
	public Integer getVarMeteringPeriod() {
		return varMeteringPeriod;
	}
	public void setVarMeteringPeriod(Integer varMeteringPeriod) {
		this.varMeteringPeriod = varMeteringPeriod;
	}
	public Integer getVarRecoveryPeriod() {
		return varRecoveryPeriod;
	}
	public void setVarRecoveryPeriod(Integer varRecoveryPeriod) {
		this.varRecoveryPeriod = varRecoveryPeriod;
	}
	public Integer getVarMeteringRetry() {
		return varMeteringRetry;
	}
	public void setVarMeteringRetry(Integer varMeteringRetry) {
		this.varMeteringRetry = varMeteringRetry;
	}
	public Integer getVarRecoveryRetry() {
		return varRecoveryRetry;
	}
	public void setVarRecoveryRetry(Integer varRecoveryRetry) {
		this.varRecoveryRetry = varRecoveryRetry;
	}
	public Integer getVarCheckDayMask() {
		return varCheckDayMask;
	}
	public void setVarCheckDayMask(Integer varCheckDayMask) {
		this.varCheckDayMask = varCheckDayMask;
	}
	public Integer getVarCheckHourMask() {
		return varCheckHourMask;
	}
	public void setVarCheckHourMask(Integer varCheckHourMask) {
		this.varCheckHourMask = varCheckHourMask;
	}
	public Integer getVarCheckStartMin() {
		return varCheckStartMin;
	}
	public void setVarCheckStartMin(Integer varCheckStartMin) {
		this.varCheckStartMin = varCheckStartMin;
	}
	public Integer getVarCheckPeriod() {
		return varCheckPeriod;
	}
	public void setVarCheckPeriod(Integer varCheckPeriod) {
		this.varCheckPeriod = varCheckPeriod;
	}
	public Integer getVarCheckRetry() {
		return varCheckRetry;
	}
	public void setVarCheckRetry(Integer varCheckRetry) {
		this.varCheckRetry = varCheckRetry;
	}
	public Integer getVarMeterTimesyncDayMask() {
		return varMeterTimesyncDayMask;
	}
	public void setVarMeterTimesyncDayMask(Integer varMeterTimesyncDayMask) {
		this.varMeterTimesyncDayMask = varMeterTimesyncDayMask;
	}
	public Integer getVarMeterTimesyncHourMask() {
		return varMeterTimesyncHourMask;
	}
	public void setVarMeterTimesyncHourMask(Integer varMeterTimesyncHourMask) {
		this.varMeterTimesyncHourMask = varMeterTimesyncHourMask;
	}
	public Integer getVarHeterOnTemp() {
		return varHeterOnTemp;
	}
	public void setVarHeterOnTemp(Integer varHeterOnTemp) {
		this.varHeterOnTemp = varHeterOnTemp;
	}
	public Integer getVarHeterOffTemp() {
		return varHeterOffTemp;
	}
	public void setVarHeterOffTemp(Integer varHeterOffTemp) {
		this.varHeterOffTemp = varHeterOffTemp;
	}
	public Integer getVarMobileLiveCheckIntegererval() {
		return varMobileLiveCheckIntegererval;
	}
	public void setVarMobileLiveCheckIntegererval(Integer varMobileLiveCheckIntegererval) {
		this.varMobileLiveCheckIntegererval = varMobileLiveCheckIntegererval;
	}
	public Integer getVarEventReadDayMask() {
		return varEventReadDayMask;
	}
	public void setVarEventReadDayMask(Integer varEventReadDayMask) {
		this.varEventReadDayMask = varEventReadDayMask;
	}
	public Integer getVarEventReadHourMask() {
		return varEventReadHourMask;
	}
	public void setVarEventReadHourMask(Integer varEventReadHourMask) {
		this.varEventReadHourMask = varEventReadHourMask;
	}
	public Integer getVarSendDelay() {
		return varSendDelay;
	}
	public void setVarSendDelay(Integer varSendDelay) {
		this.varSendDelay = varSendDelay;
	}
	public Integer getVarEventAlertLevel() {
		return varEventAlertLevel;
	}
	public void setVarEventAlertLevel(Integer varEventAlertLevel) {
		this.varEventAlertLevel = varEventAlertLevel;
	}
	public Integer getVarSensorLimit() {
		return varSensorLimit;
	}
	public void setVarSensorLimit(Integer varSensorLimit) {
		this.varSensorLimit = varSensorLimit;
	}
	public Integer getVarMeterStrategy() {
		return varMeterStrategy;
	}
	public void setVarMeterStrategy(Integer varMeterStrategy) {
		this.varMeterStrategy = varMeterStrategy;
	}
	public Integer getVarTimesyncThreshold() {
		return varTimesyncThreshold;
	}
	public void setVarTimesyncThreshold(Integer varTimesyncThreshold) {
		this.varTimesyncThreshold = varTimesyncThreshold;
	}
	public Integer getVarMobileLiveCheckMEthod() {
		return varMobileLiveCheckMEthod;
	}
	public void setVarMobileLiveCheckMEthod(Integer varMobileLiveCheckMEthod) {
		this.varMobileLiveCheckMEthod = varMobileLiveCheckMEthod;
	}
	public Integer getVarScanningStrategy() {
		return varScanningStrategy;
	}
	public void setVarScanningStrategy(Integer varScanningStrategy) {
		this.varScanningStrategy = varScanningStrategy;
	}
	public Integer getVarMobileLogSaveDay() {
		return varMobileLogSaveDay;
	}
	public void setVarMobileLogSaveDay(Integer varMobileLogSaveDay) {
		this.varMobileLogSaveDay = varMobileLogSaveDay;
	}
	public Integer getVarUpgradeLogSaveDay() {
		return varUpgradeLogSaveDay;
	}
	public void setVarUpgradeLogSaveDay(Integer varUpgradeLogSaveDay) {
		this.varUpgradeLogSaveDay = varUpgradeLogSaveDay;
	}
	public Integer getVarUploadLogSaveDay() {
		return varUploadLogSaveDay;
	}
	public void setVarUploadLogSaveDay(Integer varUploadLogSaveDay) {
		this.varUploadLogSaveDay = varUploadLogSaveDay;
	}
	public Integer getVarTimesyncLogSaveDay() {
		return varTimesyncLogSaveDay;
	}
	public void setVarTimesyncLogSaveDay(Integer varTimesyncLogSaveDay) {
		this.varTimesyncLogSaveDay = varTimesyncLogSaveDay;
	}
	public Integer getVarMaxMeterLogSize() {
		return varMaxMeterLogSize;
	}
	public void setVarMaxMeterLogSize(Integer varMaxMeterLogSize) {
		this.varMaxMeterLogSize = varMaxMeterLogSize;
	}
	public Integer getVarMaxUpgradeLogSize() {
		return varMaxUpgradeLogSize;
	}
	public void setVarMaxUpgradeLogSize(Integer varMaxUpgradeLogSize) {
		this.varMaxUpgradeLogSize = varMaxUpgradeLogSize;
	}
	public Integer getVarMaxUploadLogSize() {
		return varMaxUploadLogSize;
	}
	public void setVarMaxUploadLogSize(Integer varMaxUploadLogSize) {
		this.varMaxUploadLogSize = varMaxUploadLogSize;
	}
	public Integer getVarTimesyncLogSize() {
		return varTimesyncLogSize;
	}
	public void setVarTimesyncLogSize(Integer varTimesyncLogSize) {
		this.varTimesyncLogSize = varTimesyncLogSize;
	}
	public Integer getVarDefaultGeMeteringOPtion() {
		return varDefaultGeMeteringOPtion;
	}
	public void setVarDefaultGeMeteringOPtion(Integer varDefaultGeMeteringOPtion) {
		this.varDefaultGeMeteringOPtion = varDefaultGeMeteringOPtion;
	}
	public Integer getVarSensorCleaningThreshold() {
		return varSensorCleaningThreshold;
	}
	public void setVarSensorCleaningThreshold(Integer varSensorCleaningThreshold) {
		this.varSensorCleaningThreshold = varSensorCleaningThreshold;
	}
	public Integer getVarTimeSyncStrategy() {
		return varTimeSyncStrategy;
	}
	public void setVarTimeSyncStrategy(Integer varTimeSyncStrategy) {
		this.varTimeSyncStrategy = varTimeSyncStrategy;
	}
	public Integer getVarTransactionSaveDay() {
		return varTransactionSaveDay;
	}
	public void setVarTransactionSaveDay(Integer varTransactionSaveDay) {
		this.varTransactionSaveDay = varTransactionSaveDay;
	}	
    public Integer getVarMeterUploadStartMin() {
		return varMeterUploadStartMin;
	}
	public void setVarMeterUploadStartMin(Integer varMeterUploadStartMin) {
		this.varMeterUploadStartMin = varMeterUploadStartMin;
	}
	public Integer getVarMeterUploadTryTime() {
		return varMeterUploadTryTime;
	}
	public void setVarMeterUploadTryTime(Integer varMeterUploadTryTime) {
		this.varMeterUploadTryTime = varMeterUploadTryTime;
	}
	
	public String getVarUploadTime() {
		return varUploadTime;
	}
	public void setVarUploadTime(String varUploadTime) {
		this.varUploadTime = varUploadTime;
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
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public String getInstanceName() {
        return ""+this.getId();
    }
}
