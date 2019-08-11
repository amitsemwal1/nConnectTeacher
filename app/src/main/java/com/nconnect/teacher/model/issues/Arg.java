package com.nconnect.teacher.model.issues;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import com.nconnect.teacher.util.Constants;


public class Arg {
    @SerializedName(Constants.NAME)
    @Expose
    private String name;
    @SerializedName(Constants.STUDENT_ID)
    @Expose
    private Integer studentId;
    @SerializedName(Constants.DESCRIPTION)
    @Expose
    private String description;
    @SerializedName(Constants.POST_TO)
    @Expose
    private String postTo;
    @SerializedName(Constants.DATE_FROM)
    @Expose
    private String dateFrom;
    @SerializedName(Constants.DATE_TO)
    @Expose
    private String dateTo;
    @SerializedName(Constants.EVENTS_IMAGE)
    @Expose
    private String image;
    @SerializedName(Constants.LOCATION)
    @Expose
    private String location;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("dp")
    @Expose
    private String dp;

    public String getDp() {
        return dp;
    }

    public void setDp(String dp) {
        this.dp = dp;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(String dateFrom) {
        this.dateFrom = dateFrom;
    }

    public String getDateTo() {
        return dateTo;
    }

    public void setDateTo(String dateTo) {
        this.dateTo = dateTo;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPostTo() {
        return postTo;
    }

    public void setPostTo(String postTo) {
        this.postTo = postTo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
