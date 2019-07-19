package com.aimir.model.vo;


/**
 * SGDG_XAM3VO.java Description
 * <p>
 * <pre>
 * Date          Version     Author   Description
 * 2013. 11. 21. v1.0        문동규
 * </pre>
 */
public class SGDG_XAM3VO {

    // 고객번호
    private String customerId;

    // 계량기식별자
    private String meterId;

    // 발생일시
    private String eventDate;

    // 정전
    private Integer powerFailure;

    // 복전
    private Integer powerRestore;

    // 시간변경전
    private Integer timeChangeFrom;

    // 시간변경후
    private Integer timeChangeTo;

    // 수요전력복귀
    private Integer demandReset;

    // 수동검침
    private Integer manualDemandReset;

    // 자기검침
    private Integer selfRead;

    // 프로그램변경
    private Integer programChange;

    // 전류제한
    private Integer currentLimit;

    // 미터커버
    private Integer meterCover;

    // 자계감지
    private Integer magneticDetect;

    // Sag
    private Integer sag;

    // Swell
    private Integer swell;

    // 원격부하개폐
    private Integer remoteControl;

    // 전류제한해제. 전류제한이 1에서 0으로 변경되는 시점에 1로 설정되어야 함.
    private Integer currentLimitRelease;

    // 터미널커버
    private Integer terminalCover;

    // 오결선
    private Integer wrongLine;

    // 정보생성일자
    private String addDt;

    // 정보생성자
    private String addId;

    /**
     * @return the customerId
     */
    public String getCustomerId() {
        return customerId;
    }

    /**
     * @param customerId the customerId to set
     */
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    /**
     * @return the meterId
     */
    public String getMeterId() {
        return meterId;
    }

    /**
     * @param meterId the meterId to set
     */
    public void setMeterId(String meterId) {
        this.meterId = meterId;
    }

    /**
     * @return the eventDate
     */
    public String getEventDate() {
        return eventDate;
    }

    /**
     * @param eventDate the eventDate to set
     */
    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    /**
     * @return the powerFailure
     */
    public Integer getPowerFailure() {
        return powerFailure;
    }

    /**
     * @param powerFailure the powerFailure to set
     */
    public void setPowerFailure(Integer powerFailure) {
        this.powerFailure = powerFailure;
    }

    /**
     * @return the powerRestore
     */
    public Integer getPowerRestore() {
        return powerRestore;
    }

    /**
     * @param powerRestore the powerRestore to set
     */
    public void setPowerRestore(Integer powerRestore) {
        this.powerRestore = powerRestore;
    }

    /**
     * @return the timeChangeFrom
     */
    public Integer getTimeChangeFrom() {
        return timeChangeFrom;
    }

    /**
     * @param timeChangeFrom the timeChangeFrom to set
     */
    public void setTimeChangeFrom(Integer timeChangeFrom) {
        this.timeChangeFrom = timeChangeFrom;
    }

    /**
     * @return the timeChangeTo
     */
    public Integer getTimeChangeTo() {
        return timeChangeTo;
    }

    /**
     * @param timeChangeTo the timeChangeTo to set
     */
    public void setTimeChangeTo(Integer timeChangeTo) {
        this.timeChangeTo = timeChangeTo;
    }

    /**
     * @return the demandReset
     */
    public Integer getDemandReset() {
        return demandReset;
    }

    /**
     * @param demandReset the demandReset to set
     */
    public void setDemandReset(Integer demandReset) {
        this.demandReset = demandReset;
    }

    /**
     * @return the manualDemandReset
     */
    public Integer getManualDemandReset() {
        return manualDemandReset;
    }

    /**
     * @param manualDemandReset the manualDemandReset to set
     */
    public void setManualDemandReset(Integer manualDemandReset) {
        this.manualDemandReset = manualDemandReset;
    }

    /**
     * @return the self_read
     */
    public Integer getSelfRead() {
        return selfRead;
    }

    /**
     * @param self_read the self_read to set
     */
    public void setSelfRead(Integer selfRead) {
        this.selfRead = selfRead;
    }

    /**
     * @return the programChange
     */
    public Integer getProgramChange() {
        return programChange;
    }

    /**
     * @param programChange the programChange to set
     */
    public void setProgramChange(Integer programChange) {
        this.programChange = programChange;
    }

    /**
     * @return the currentLimit
     */
    public Integer getCurrentLimit() {
        return currentLimit;
    }

    /**
     * @param currentLimit the currentLimit to set
     */
    public void setCurrentLimit(Integer currentLimit) {
        this.currentLimit = currentLimit;
    }

    /**
     * @return the meterCover
     */
    public Integer getMeterCover() {
        return meterCover;
    }

    /**
     * @param meterCover the meterCover to set
     */
    public void setMeterCover(Integer meterCover) {
        this.meterCover = meterCover;
    }

    /**
     * @return the magneticDetect
     */
    public Integer getMagneticDetect() {
        return magneticDetect;
    }

    /**
     * @param magneticDetect the magneticDetect to set
     */
    public void setMagneticDetect(Integer magneticDetect) {
        this.magneticDetect = magneticDetect;
    }

    /**
     * @return the sag
     */
    public Integer getSag() {
        return sag;
    }

    /**
     * @param sag the sag to set
     */
    public void setSag(Integer sag) {
        this.sag = sag;
    }

    /**
     * @return the swell
     */
    public Integer getSwell() {
        return swell;
    }

    /**
     * @param swell the swell to set
     */
    public void setSwell(Integer swell) {
        this.swell = swell;
    }

    /**
     * @return the remoteControl
     */
    public Integer getRemoteControl() {
        return remoteControl;
    }

    /**
     * @param remoteControl the remoteControl to set
     */
    public void setRemoteControl(Integer remoteControl) {
        this.remoteControl = remoteControl;
    }

    /**
     * @return the currentLimitRelease
     */
    public Integer getCurrentLimitRelease() {
        return currentLimitRelease;
    }

    /**
     * @param currentLimitRelease the currentLimitRelease to set
     */
    public void setCurrentLimitRelease(Integer currentLimitRelease) {
        this.currentLimitRelease = currentLimitRelease;
    }

    /**
     * @return the terminalCover
     */
    public Integer getTerminalCover() {
        return terminalCover;
    }

    /**
     * @param terminalCover the terminalCover to set
     */
    public void setTerminalCover(Integer terminalCover) {
        this.terminalCover = terminalCover;
    }

    /**
     * @return the wrongLine
     */
    public Integer getWrongLine() {
        return wrongLine;
    }

    /**
     * @param wrongLine the wrongLine to set
     */
    public void setWrongLine(Integer wrongLine) {
        this.wrongLine = wrongLine;
    }

    /**
     * @return the addDt
     */
    public String getAddDt() {
        return addDt;
    }

    /**
     * @param addDt the addDt to set
     */
    public void setAddDt(String addDt) {
        this.addDt = addDt;
    }

    /**
     * @return the addId
     */
    public String getAddId() {
        return addId;
    }

    /**
     * @param addId the addId to set
     */
    public void setAddId(String addId) {
        this.addId = addId;
    }

}