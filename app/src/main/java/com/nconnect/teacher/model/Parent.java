package com.nconnect.teacher.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Parent {

    @SerializedName("xl_inactive")
    @Expose
    private String xlInactive;
    @SerializedName("percentage")
    @Expose
    private String percentage;
    @SerializedName("numbers")
    @Expose
    private Integer numbers;
    @SerializedName("xl_non_install")
    @Expose
    private String xlNonInstall;

    public String getXlNonInstall() {
        return xlNonInstall;
    }

    public void setXlNonInstall(String xlNonInstall) {
        this.xlNonInstall = xlNonInstall;
    }

    public String getXlInactive() {
        return xlInactive;
    }

    public void setXlInactive(String xlInactive) {
        this.xlInactive = xlInactive;
    }

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }

    public Integer getNumbers() {
        return numbers;
    }

    public void setNumbers(Integer numbers) {
        this.numbers = numbers;
    }
}
