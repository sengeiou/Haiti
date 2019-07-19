package com.aimir.model.device;

public class OperationLogChartData {

    private String rank; //jhkim 2011.05.17 String -> int
    private String operation;
    private String operationCommandId;
    private String successCnt = "0";
    private String failCnt = "0";
    private String cnt = "0";
    private String width = "0";
    
    private String date;
    private String userCnt = "0";
    private String operatorCnt = "0";
    private String systemCnt = "0";
    private String dateOperatorType;
    
    private String time;
    private String targetType;
    private String target;
    private String operatorType;
    private String operator;
    private String status;
    private String descr;
    
    public String getRank() {
        return rank;
    }
    public void setRank(String rank) {
        this.rank = rank;
    }
    public String getOperation() {
        return operation;
    }
    public void setOperation(String operation) {
        this.operation = operation;
    }
    public String getOperationCommandId() {
        return operationCommandId;
    }
    public void setOperationCommandId(String operationCommandId) {
        this.operationCommandId = operationCommandId;
    }
    public String getSuccessCnt() {
        return successCnt;
    }
    public void setSuccessCnt(String successCnt) {
        this.successCnt = successCnt;
    }
    public String getFailCnt() {
        return failCnt;
    }
    public void setFailCnt(String failCnt) {
        this.failCnt = failCnt;
    }

    public String getCnt() {
        return cnt;
    }
    public void setCnt(String cnt) {
        this.cnt = cnt;
    }
    public String getWidth() {
        return width;
    }
    public void setWidth(String width) {
        this.width = width;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getUserCnt() {
        return userCnt;
    }
    public void setUserCnt(String userCnt) {
        this.userCnt = userCnt;
    }
    public String getOperatorCnt() {
        return operatorCnt;
    }
    public void setOperatorCnt(String operatorCnt) {
        this.operatorCnt = operatorCnt;
    }
    public String getSystemCnt() {
        return systemCnt;
    }
    public void setSystemCnt(String systemCnt) {
        this.systemCnt = systemCnt;
    }
    public String getDateOperatorType() {
        return dateOperatorType;
    }
    public void setDateOperatorType(String dateOperatorType) {
        this.dateOperatorType = dateOperatorType;
    }
    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public String getTargetType() {
        return targetType;
    }
    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }
    public String getTarget() {
        return target;
    }
    public void setTarget(String target) {
        this.target = target;
    }
    public String getOperatorType() {
        return operatorType;
    }
    public void setOperatorType(String operatorType) {
        this.operatorType = operatorType;
    }
    public String getOperator() {
        return operator;
    }
    public void setOperator(String operator) {
        this.operator = operator;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getDescr() {
        return descr;
    }
    public void setDescr(String descr) {
        this.descr = descr;
    }   
}
