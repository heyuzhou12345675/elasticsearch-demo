package com.zcy.es.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;


@Document(indexName = "acelog")
public class AceLog {


    @Id
    private String id;

    /**
     * 记录日志时间
     */
    @Field(type = FieldType.Date, format = DateFormat.custom, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private String logTime;


    /**
     * 域节点同步
     */
    @Field(type = FieldType.Keyword)
    private String node;

    /**
     * 消息流时间戳记录
     */
    @Field(type = FieldType.Date, format = DateFormat.custom, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private String firstTimestamp;

    @Field(type = FieldType.Date, format = DateFormat.custom, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private String secondTimestamp;

    @Field(type = FieldType.Date, format = DateFormat.custom, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private String thirdTimestamp;

    @Field(type = FieldType.Date, format = DateFormat.custom, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private String fourthTimestamp;

    /**
     * 消息流时间计算
     */
    @Field(type = FieldType.Integer)
    private String firstLogInterval;

    @Field(type = FieldType.Integer)
    private String secondLogInterval;

    @Field(type = FieldType.Integer)
    private String thirdLogInterval;

    /**
     * 日志级别 默认使用“info”
     */
    @Field(type = FieldType.Keyword)
    private String level;

    /**
     * 消息ID
     */
    @Field(type = FieldType.Keyword)
    private String messageUUID;


    @Field(type = FieldType.Keyword)
    private String messageID;


    @Field(type = FieldType.Keyword)
    private String infoLog;


    /**
     * 消息头信息
     */
    @Field(type = FieldType.Keyword)
    private String sourceSysID;

    @Field(type = FieldType.Keyword)
    private String targetSysID;

    @Field(type = FieldType.Keyword)
    private String targetService;

    @Field(type = FieldType.Keyword)
    private String requestID;

    @Field(type = FieldType.Keyword)
    private String correlationID;

    /**
     * 异常信息
     */
    @Field(type = FieldType.Keyword)
    private String errorCode;

    @Field(type = FieldType.Keyword)
    private String errorCause;

    @Field(type = FieldType.Text)
    private String desc;

    /**
     * 消息内容与消息大小
     */
    @Field(type = FieldType.Text)
    private String firstMsgContent;

    @Field(type = FieldType.Integer)
    private String firstMsgSize;

    @Field(type = FieldType.Text)
    private String secondMsgContent;

    @Field(type = FieldType.Integer)
    private String secondMsgSize;

    @Field(type = FieldType.Text)
    private String thirdMsgContent;
    @Field(type = FieldType.Integer)
    private String thirdMsgSize;

    @Field(type = FieldType.Text)
    private String fourthMsgContent;
    @Field(type = FieldType.Integer)
    private String fourthMsgSize;

    /**
     * 请求响应计数
     */
    @Field(type = FieldType.Integer)
    private String reqAttachmentCount;
    @Field(type = FieldType.Integer)
    private String resAttachmentCount;

    /**
     * 组件名称 记录最后一个环节
     */
    @Field(type = FieldType.Keyword)
    private String mediationName;
    /**
     * 应用名称即消息流名称
     */
    @Field(type = FieldType.Keyword)
    private String appName;
    /**
     * 消息流版本
     */
    @Field(type = FieldType.Keyword)
    private String version;


    @Field(type = FieldType.Keyword)
    private String msgRecord;

    public String getFirstLogInterval() {
        return firstLogInterval;
    }

    public void setFirstLogInterval(String firstLogInterval) {
        this.firstLogInterval = firstLogInterval;
    }

    public String getSecondLogInterval() {
        return secondLogInterval;
    }

    public void setSecondLogInterval(String secondLogInterval) {
        this.secondLogInterval = secondLogInterval;
    }

    public String getThirdLogInterval() {
        return thirdLogInterval;
    }

    public void setThirdLogInterval(String thirdLogInterval) {
        this.thirdLogInterval = thirdLogInterval;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getMessageUUID() {
        return messageUUID;
    }

    public void setMessageUUID(String messageUUID) {
        this.messageUUID = messageUUID;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
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

    public String getCorrelationID() {
        return correlationID;
    }

    public void setCorrelationID(String correlationID) {
        this.correlationID = correlationID;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorCause() {
        return errorCause;
    }

    public void setErrorCause(String errorCause) {
        this.errorCause = errorCause;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getFirstMsgContent() {
        return firstMsgContent;
    }

    public void setFirstMsgContent(String firstMsgContent) {
        this.firstMsgContent = firstMsgContent;
    }

    public String getFirstMsgSize() {
        return firstMsgSize;
    }

    public void setFirstMsgSize(String firstMsgSize) {
        this.firstMsgSize = firstMsgSize;
    }

    public String getSecondMsgContent() {
        return secondMsgContent;
    }

    public void setSecondMsgContent(String secondMsgContent) {
        this.secondMsgContent = secondMsgContent;
    }

    public String getSecondMsgSize() {
        return secondMsgSize;
    }

    public void setSecondMsgSize(String secondMsgSize) {
        this.secondMsgSize = secondMsgSize;
    }

    public String getThirdMsgContent() {
        return thirdMsgContent;
    }

    public void setThirdMsgContent(String thirdMsgContent) {
        this.thirdMsgContent = thirdMsgContent;
    }

    public String getThirdMsgSize() {
        return thirdMsgSize;
    }

    public void setThirdMsgSize(String thirdMsgSize) {
        this.thirdMsgSize = thirdMsgSize;
    }

    public String getFourthMsgContent() {
        return fourthMsgContent;
    }

    public void setFourthMsgContent(String fourthMsgContent) {
        this.fourthMsgContent = fourthMsgContent;
    }

    public String getFourthMsgSize() {
        return fourthMsgSize;
    }

    public void setFourthMsgSize(String fourthMsgSize) {
        this.fourthMsgSize = fourthMsgSize;
    }

    public String getReqAttachmentCount() {
        return reqAttachmentCount;
    }

    public void setReqAttachmentCount(String reqAttachmentCount) {
        this.reqAttachmentCount = reqAttachmentCount;
    }

    public String getResAttachmentCount() {
        return resAttachmentCount;
    }

    public void setResAttachmentCount(String resAttachmentCount) {
        this.resAttachmentCount = resAttachmentCount;
    }

    public String getMediationName() {
        return mediationName;
    }

    public void setMediationName(String mediationName) {
        this.mediationName = mediationName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }


    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getLogTime() {
        return logTime;
    }

    public void setLogTime(String logTime) {
        this.logTime = logTime;
    }

    public String getFirstTimestamp() {
        return firstTimestamp;
    }

    public void setFirstTimestamp(String firstTimestamp) {
        this.firstTimestamp = firstTimestamp;
    }

    public String getSecondTimestamp() {
        return secondTimestamp;
    }

    public void setSecondTimestamp(String secondTimestamp) {
        this.secondTimestamp = secondTimestamp;
    }

    public String getThirdTimestamp() {
        return thirdTimestamp;
    }

    public void setThirdTimestamp(String thirdTimestamp) {
        this.thirdTimestamp = thirdTimestamp;
    }

    public String getFourthTimestamp() {
        return fourthTimestamp;
    }

    public void setFourthTimestamp(String fourthTimestamp) {
        this.fourthTimestamp = fourthTimestamp;
    }

    public String getInfoLog() {
        return infoLog;
    }

    public void setInfoLog(String infoLog) {
        this.infoLog = infoLog;
    }

    public String getMsgRecord() {
        return msgRecord;
    }

    public void setMsgRecord(String msgRecord) {
        this.msgRecord = msgRecord;
    }

}
