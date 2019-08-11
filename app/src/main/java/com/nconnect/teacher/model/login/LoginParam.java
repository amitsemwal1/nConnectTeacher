package com.nconnect.teacher.model.login;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import com.nconnect.teacher.util.Constants;

public class LoginParam {
    @SerializedName(Constants.LOGIN)
    @Expose
    private String login;
    @SerializedName(Constants.PASSWORD)
    @Expose
    private String password;
    @SerializedName(Constants.LOGIN_TYPE)
    @Expose
    private String loginType;
    @SerializedName("device_token")
    @Expose
    private String token;

    public String getLoginType() {
        return loginType;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

    public LoginParam(String login, String password, String token, String loginType) {
        this.login = login;
        this.password = password;
        this.token = token;
        this.loginType = loginType;
    }

    public LoginParam() {

    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @SerializedName("teacher_id")
    @Expose
    private String employeeId;
    @SerializedName(Constants.STATUS_CODE)
    @Expose
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }
}
