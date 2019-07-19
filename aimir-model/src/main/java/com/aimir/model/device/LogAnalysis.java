/**
 * (@)# LogAnalysis.java
 *
 * 2014. 7. 14.
 *
 * Copyright (c) 2013 NuriTelecom, Inc.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of 
 * ITCOMM, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with NuriTelecom, Inc.
 *
 * For more information on this product, please see
 * www.nuritelecom.co.kr
 *
 */
package com.aimir.model.device;

import com.aimir.model.BaseObject;

/**
 * @author nuri
 * 
 */
public class LogAnalysis extends BaseObject {

    private static final long serialVersionUID = -8576813510994652326L;
    private String sort1;
    private String sort2;
    private String dateByOrder;
    private String dateByGrouping;
    private String dateByView;
    private String logType;
    private String senderId;
    private String device;
    private String userId;
    private String operationCode;
    private String result;
    private String message;

    public String getSort1() {
        return sort1;
    }

    public void setSort1(String sort1) {
        this.sort1 = sort1;
    }

    public String getSort2() {
        return sort2;
    }

    public void setSort2(String sort2) {
        this.sort2 = sort2;
    }

    public String getDateByOrder() {
        return dateByOrder;
    }

    public void setDateByOrder(String dateByOrder) {
        this.dateByOrder = dateByOrder;
    }

    public String getDateByGrouping() {
        return dateByGrouping;
    }

    public void setDateByGrouping(String dateByGrouping) {
        this.dateByGrouping = dateByGrouping;
    }

    public String getDateByView() {
        return dateByView;
    }

    public void setDateByView(String dateByView) {
        this.dateByView = dateByView;
    }

    public String getLogType() {
        return logType;
    }

    public void setLogType(String logType) {
        this.logType = logType;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOperationCode() {
        return operationCode;
    }

    public void setOperationCode(String operationCode) {
        this.operationCode = operationCode;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aimir.model.BaseObject#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aimir.model.BaseObject#hashCode()
     */
    @Override
    public int hashCode() {
        // TODO Auto-generated method stub
        return 0;
    }

}
