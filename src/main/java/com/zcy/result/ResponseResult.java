package com.zcy.result;

/**
 * @author zhouchunyang
 * @Date: Created in 14:14 2021/8/31
 * @Description:
 */
public class ResponseResult {
    private String status;
    private String code;
    private String msg;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
