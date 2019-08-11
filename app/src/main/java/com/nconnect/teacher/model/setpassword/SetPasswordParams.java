package com.nconnect.teacher.model.setpassword;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import com.nconnect.teacher.util.Constants;

public class SetPasswordParams {
    @SerializedName(Constants.USER_TOKEN)
    @Expose
    private int userToken;
    @SerializedName(Constants.PASSWORD)
    @Expose
    private String password;
    @SerializedName(Constants.LOGIN_TYPE)
    @Expose
    private String loginType;

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

    public SetPasswordParams(int userToken, String password) {
        this.userToken = userToken;
        this.password = password;
    }

    public SetPasswordParams() {

    }

    public int getUserToken() {
        return userToken;
    }

    public void setUserToken(int userToken) {
        this.userToken = userToken;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
