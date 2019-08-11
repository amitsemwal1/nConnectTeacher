package com.nconnect.teacher.model;

public class Users {

    private String userName;
    private Boolean isOnboard;
    private Boolean isOnboardSetPassword;
    private Boolean isForgetSetPassword;

    public Users(String userName, Boolean isOnboard, Boolean isOnboardSetPassword) {
        this.userName = userName;
        this.isOnboard = isOnboard;
        this.isOnboardSetPassword = isOnboardSetPassword;
    }

    public Users(String userName, Boolean isOnboard, Boolean isOnboardSetPassword, Boolean isForgetSetPassword) {
        this.userName = userName;
        this.isOnboard = isOnboard;
        this.isOnboardSetPassword = isOnboardSetPassword;
        this.isForgetSetPassword = isForgetSetPassword;
    }

    public Boolean getForgetSetPassword() {
        return isForgetSetPassword;
    }

    public void setForgetSetPassword(Boolean forgetSetPassword) {
        isForgetSetPassword = forgetSetPassword;
    }

    public Boolean getOnboard() {
        return isOnboard;
    }

    public Boolean getPassword() {
        return isOnboardSetPassword;
    }

    public void setPassword(Boolean password) {
        isOnboardSetPassword = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Boolean isOnboard() {
        return isOnboard;
    }

    public void setOnboard(Boolean onboard) {
        isOnboard = onboard;
    }

    public Boolean getOnboardSetPassword() {
        return isOnboardSetPassword;
    }

    public void setOnboardSetPassword(Boolean onboardSetPassword) {
        isOnboardSetPassword = onboardSetPassword;
    }

    public Boolean getIsForgetSetPassword() {
        return isForgetSetPassword;
    }

    public void setIsForgetSetPassword(Boolean isForgetSetPassword) {
        this.isForgetSetPassword = isForgetSetPassword;
    }
}

