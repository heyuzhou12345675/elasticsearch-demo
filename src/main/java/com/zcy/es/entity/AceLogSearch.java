package com.zcy.es.entity;

import java.util.Date;

public class AceLogSearch {

    private String sourceSysID;
    private String targetSysID;
    private String targetService;
    private String requestID;
    private String appName;
    private String messageContent;
    private String level;
    private Date start;
    private Date end;

    /**
     * 错误代码*/
    private String errorCode;


    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getSourceSysID() {
        return sourceSysID;
    }

    public void setSourceSysID(String sourceSysID) {
        this.sourceSysID = sourceSysID;
    }

    public String getTargetSysID() {
        return targetSysID;
    }

    public void setTargetSysID(String targetSysID) {
        this.targetSysID = targetSysID;
    }

    public String getTargetService() {
        return targetService;
    }

    public void setTargetService(String targetService) {
        this.targetService = targetService;
    }

    public String getRequestID() {
        return requestID;
    }

    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
