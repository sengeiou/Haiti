package com.aimir.model.device;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

import org.eclipse.persistence.annotations.Index;

import com.aimir.annotation.ColumnInfo;
import com.aimir.model.BaseObject;
/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * 비동기 명령어 처리 로그
 * 
 * @author 박종성(elevas)
 */
@Entity
@Table(name = "ASYNC_COMMAND_LOG")
@Index(name="AYNC_COMMAND_LOG_01", columnNames={"DEVICEID", "STATE"})
public class AsyncCommandLog extends BaseObject {

	private static final long serialVersionUID = -913243250705177861L;

	@EmbeddedId public AsyncCommandLogPk id;
    
    @Column(length=20)
    @ColumnInfo(name="장비유형")
    private String deviceType;
    
    @Column(length=20)
    @ColumnInfo(name="장비아이디")
    private String deviceId;
    
    @Column(length=50)
    @ColumnInfo(name="명령어")
    private String command;
    
    @Column(length=2)
    @ColumnInfo(name="트랜잭션 옵션")
    private Integer trOption;
    
    @Column(length=2)
    @ColumnInfo(name="보관일수", descr="Keep Option이 Enable 되었을 때 보관 일 수 0 이면 집중기 설정값 사용")
    private Integer day;
    
    @Column(length=2)
    @ColumnInfo(name="초기 우선 순위", descr="Request 우선 순위 (-2 ~ 3 : Default 0). 값이 작을수록 우선순위가 높다")
    private Integer initNice;
    
    @Column(length=2)
    @ColumnInfo(name="현재 우선 순위", descr="")
    private Integer curNice;
    
    @Column(length=2)
    @ColumnInfo(name="초기 시도 회수", descr="시도 횟수 (기본 1 : 한번만 시도함. 최대 3). 재시도를 할 때 마다 Nice 값이 증가한다.")
    private Integer initTry;
    
    @Column(length=2)
    @ColumnInfo(name="현재 시도 회수", descr="")
    private Integer curTry;
    
    @Column(length=2)
    @ColumnInfo(name="큐")
    private Integer queue;
    
    @Column(length=2)
    @ColumnInfo(name="상태")
    private Integer state;
    
    @Column(length=2)
    @ColumnInfo(name="에러코드")
    private Integer errorCode;
    
    @Column(length=2)
    @ColumnInfo(name="이벤트유형")
    private Integer eventType;
    
    @Column(length=2)
    @ColumnInfo(name="결과수")
    private Integer resultCnt;
    
    @Column(length=20)
    @ColumnInfo(name="실행자")
    private String operator;
    
    @Column(length=14)
    @ColumnInfo(name="생성일")
    private String createTime;
    
    @Column(length=14)
    @ColumnInfo(name="종료일")
    private String lastTime;
    
    @Column(length=14)
    @ColumnInfo(name="요청일")
    private String requestTime;
    
    @OneToMany(fetch=FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name="trId", referencedColumnName="trId"),
        @JoinColumn(name="mcuId", referencedColumnName="mcuId")
        })
    private List<AsyncCommandParam> params;
    
    @OneToMany(fetch=FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name="trId", referencedColumnName="trId"),
        @JoinColumn(name="mcuId", referencedColumnName="mcuId")
        })
    private List<AsyncCommandResult> results;
    
    public AsyncCommandLog() {
        id = new AsyncCommandLogPk();
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
    
    public AsyncCommandLogPk getId() {
        return id;
    }

    public void setId(AsyncCommandLogPk id) {
        this.id = id;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public Integer getTrOption() {
        return trOption;
    }

    public void setTrOption(Integer trOption) {
        this.trOption = trOption;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public Integer getInitNice() {
        return initNice;
    }

    public void setInitNice(Integer initNice) {
        this.initNice = initNice;
    }

    public Integer getCurNice() {
        return curNice;
    }

    public void setCurNice(Integer curNice) {
        this.curNice = curNice;
    }

    public Integer getInitTry() {
        return initTry;
    }

    public void setInitTry(Integer initTry) {
        this.initTry = initTry;
    }

    public Integer getCurTry() {
        return curTry;
    }

    public void setCurTry(Integer curTry) {
        this.curTry = curTry;
    }

    public Integer getQueue() {
        return queue;
    }

    public void setQueue(Integer queue) {
        this.queue = queue;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public Integer getEventType() {
        return eventType;
    }

    public void setEventType(Integer eventType) {
        this.eventType = eventType;
    }

    public Integer getResultCnt() {
        return resultCnt;
    }

    public void setResultCnt(Integer resultCnt) {
        this.resultCnt = resultCnt;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getLastTime() {
        return lastTime;
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }

    public String getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(String requestTime) {
        this.requestTime = requestTime;
    }

    @XmlTransient
    public List<AsyncCommandParam> getParams() {
        return params;
    }

    public void setParams(List<AsyncCommandParam> params) {
        this.params = params;
    }

    @XmlTransient
    public List<AsyncCommandResult> getResults() {
        return results;
    }

    public void setResults(List<AsyncCommandResult> results) {
        this.results = results;
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
        return "AsyncCommandLog " + toJSONString();
    }

    public String toJSONString() {

        StringBuffer str = new StringBuffer();
        
        str.append("{"
            + "trid:'" + this.id.getTrId()
            + "', mcuId:'" + this.id.getMcuId()
            + "', deviceType:'" + this.deviceType
            + "', deviceId:'" + this.deviceId
            + "', command:'" + this.command
            + "', trOption:'" + this.trOption
            + "', day:'" + this.day
            + "', initNice:'" + this.initNice
            + "', curNice:'" + this.curNice
            + "', initTry:'" + this.initTry
            + "', curTry:'" + this.curTry
            + "', queue:'" + this.queue
            + "', state:'" + this.state
            + "', errorCode:'" + this.errorCode
            + "', eventType:'" + this.eventType
            + "', resultCnt:'" + this.resultCnt
            + "', operator:'" + this.operator
            + "', createTime:'" + this.createTime
            + "', lastTime:'" + this.lastTime
            + "', requestTime:'" + this.requestTime
            + "'}");
        
        return str.toString();
    }
}
